package pro.conflux.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import pro.conflux.wallet.base.BaseActivity;
import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.entity.ConfirmationType;
import pro.conflux.wallet.entity.GasSettings;
import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.interact.CreateTransactionInteract;
import pro.conflux.wallet.interact.FetchGasSettingsInteract;

import pro.conflux.wallet.interact.FetchWalletInteract;
import pro.conflux.wallet.repository.CfxNetworkRepository;

import java.math.BigInteger;

import io.reactivex.schedulers.Schedulers;

public class ConfirmationViewModel extends BaseViewModel {
    private final MutableLiveData<String> newTransaction = new MutableLiveData<>();
    private final MutableLiveData<CfxWallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<GasSettings> gasSettings = new MutableLiveData<>();

    private GasSettings gasSettingsOverride = null;   // use setting

    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();

    private final CfxNetworkRepository cfxNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;
    private final FetchGasSettingsInteract fetchGasSettingsInteract;
    private final CreateTransactionInteract createTransactionInteract;

    ConfirmationType confirmationType;

    public ConfirmationViewModel(
            CfxNetworkRepository cfxNetworkRepository,
            FetchWalletInteract findDefaultWalletInteract,
            FetchGasSettingsInteract fetchGasSettingsInteract,
            CreateTransactionInteract createTransactionInteract) {
        this.cfxNetworkRepository = cfxNetworkRepository;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.fetchGasSettingsInteract = fetchGasSettingsInteract;
        this.createTransactionInteract = createTransactionInteract;
    }

    public LiveData<NetworkInfo> defaultNetwork() {
        return defaultNetwork;
    }

    public LiveData<CfxWallet> defaultWallet() {
        return defaultWallet;
    }

    public MutableLiveData<GasSettings> gasSettings() {
        return gasSettings;
    }

    public LiveData<String> sendTransaction() { return newTransaction; }

    public void overrideGasSettings(GasSettings settings)
    {
        gasSettingsOverride = settings;
        gasSettings.postValue(settings);
    }

    public void prepare(BaseActivity ctx,  ConfirmationType type) {
        this.confirmationType = type;
        disposable = cfxNetworkRepository
                .find()
                .subscribe(this::onDefaultNetwork, this::onError);

        fetchGasSettingsInteract.gasPriceUpdate().observe(ctx, this::onGasPrice);  // listen price
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        fetchGasSettingsInteract.clean();
    }



    private void onDefaultNetwork(NetworkInfo networkInfo) {
        defaultNetwork.postValue(networkInfo);
        disposable = findDefaultWalletInteract
                .findDefault()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    private void onCreateTransaction(String transaction) {
        progress.postValue(false);
        newTransaction.postValue(transaction);
    }

    public void createTransaction(String password, String to, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit) {
        progress.postValue(true);

        createTransactionInteract.createCfxTransaction(defaultWallet.getValue(), to, amount, gasPrice, gasLimit, password)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onCreateTransaction, this::onError);

    }

    public void createTokenTransfer(String password, String to, String contractAddress,
                                    BigInteger amount, BigInteger gasPrice, BigInteger gasLimit) {
        progress.postValue(true);
        createTransactionInteract.createERC20Transfer(defaultWallet.getValue(), to, contractAddress, amount, gasPrice, gasLimit, password)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onCreateTransaction, this::onError);
    }

    private void onDefaultWallet(CfxWallet wallet) {
        defaultWallet.setValue(wallet);
        if (gasSettings.getValue() == null) {
            fetchGasSettingsInteract.fetch(confirmationType).subscribe(this::onGasSettings, this::onError);
        }
    }

    public void calculateGasSettings(byte[] transaction, boolean isNonFungible)
    {
        if (gasSettings.getValue() == null)
        {
            disposable = fetchGasSettingsInteract
                    .fetch(transaction, isNonFungible)
                    .subscribe(this::onGasSettings, this::onError);
        }
    }


    private void onGasSettings(GasSettings gasSettings) {
        this.gasSettings.setValue(gasSettings);
    }

    private void onGasPrice(BigInteger currentGasPrice)
    {
        if (this.gasSettings.getValue() != null //protect against race condition
                && this.gasSettingsOverride == null //only update if user hasn't overriden
                )
        {
            GasSettings updateSettings = new GasSettings(currentGasPrice, gasSettings.getValue().gasLimit);
            this.gasSettings.postValue(updateSettings);
        }
    }

}
