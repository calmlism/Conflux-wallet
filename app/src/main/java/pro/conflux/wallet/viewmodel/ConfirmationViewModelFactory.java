package pro.conflux.wallet.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import pro.conflux.wallet.ChainWalletApp;
import pro.conflux.wallet.interact.CreateTransactionInteract;
import pro.conflux.wallet.interact.FetchGasSettingsInteract;
import pro.conflux.wallet.interact.FetchWalletInteract;
import pro.conflux.wallet.repository.CfxNetworkRepository;
import pro.conflux.wallet.repository.RepositoryFactory;


public class ConfirmationViewModelFactory implements ViewModelProvider.Factory {

    private final CfxNetworkRepository cfxNetworkRepository;
    private FetchWalletInteract findDefaultWalletInteract;
    private FetchGasSettingsInteract fetchGasSettingsInteract;
    private CreateTransactionInteract createTransactionInteract;

    public ConfirmationViewModelFactory() {
        RepositoryFactory rf = ChainWalletApp.repositoryFactory();

        this.cfxNetworkRepository = rf.cfxNetworkRepository;
        this.findDefaultWalletInteract = new FetchWalletInteract();
        this.fetchGasSettingsInteract = new FetchGasSettingsInteract(ChainWalletApp.sp, cfxNetworkRepository);
        this.createTransactionInteract = new CreateTransactionInteract(cfxNetworkRepository);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ConfirmationViewModel(cfxNetworkRepository, findDefaultWalletInteract, fetchGasSettingsInteract , createTransactionInteract);
    }
}
