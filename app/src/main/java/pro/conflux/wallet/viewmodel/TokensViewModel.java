package pro.conflux.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import pro.conflux.wallet.ChainWalletApp;
import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.entity.Ticker;
import pro.conflux.wallet.entity.Token;
import pro.conflux.wallet.interact.FetchTokensInteract;
import pro.conflux.wallet.interact.FetchWalletInteract;
import pro.conflux.wallet.repository.CfxNetworkRepository;
import pro.conflux.wallet.service.TickerService;
import pro.conflux.wallet.service.WalletTickerService;
import pro.conflux.wallet.utils.LogUtils;
import com.google.gson.Gson;

import io.reactivex.Single;
import pro.conflux.wallet.utils.WalletDaoUtils;


public class TokensViewModel extends BaseViewModel {
    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();

    private final MutableLiveData<CfxWallet> defaultWallet = new MutableLiveData<>();


    private final MutableLiveData<Ticker> prices = new MutableLiveData<>();
    private final MutableLiveData<Token[]> tokens = new MutableLiveData<>();

    private final CfxNetworkRepository cfxNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;

    private final FetchTokensInteract fetchTokensInteract;
    private final TickerService tickerService;

    TokensViewModel(
            CfxNetworkRepository cfxNetworkRepository,
            FetchWalletInteract findDefaultWalletInteract,
            FetchTokensInteract fetchTokensInteract) {
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.cfxNetworkRepository = cfxNetworkRepository;
        this.fetchTokensInteract = fetchTokensInteract;


        tickerService = new WalletTickerService(ChainWalletApp.okHttpClient(), new Gson());
    }

    public void prepare() {
        progress.postValue(true);

        defaultNetwork.postValue(cfxNetworkRepository.getDefaultNetwork());
        disposable = findDefaultWalletInteract.findDefault()
                .subscribe(this::onDefaultWallet, this::onError);

    }

    public void updateDefaultWallet(final long walletId) {

        Single.fromCallable(() -> {
            return WalletDaoUtils.updateCurrent(walletId);
        }).subscribe(this::onDefaultWallet);

    }

    private void onDefaultWallet(CfxWallet wallet) {
        defaultWallet.setValue(wallet);
        fetchTokens();
    }

    public LiveData<NetworkInfo> defaultNetwork() {
        return defaultNetwork;
    }

    public LiveData<CfxWallet> defaultWallet() {
        return defaultWallet;
    }

    public LiveData<Token[]> tokens() {
        return tokens;
    }

    public MutableLiveData<Ticker> prices() {
        return prices;
    }

    public void fetchTokens() {
        progress.postValue(true);

        disposable = fetchTokensInteract
                .fetch(defaultWallet.getValue().address)
                .subscribe(this::onTokens, this::onError);
    }

    private void onTokens(Token[] tokens) {
        LogUtils.d("Tokens", "onTokens");
        progress.postValue(false);
        this.tokens.postValue(tokens);

        //  TODO： 是否出现重复调用
        for (Token token : tokens ) {
            if (token.balance!=null && !token.balance.equals("0")) {   // > 0
                getTicker(token.tokenInfo.symbol).subscribe(this::onPrice, this::onError);
            }
        }

    }

    public Single<Ticker> getTicker(String symbol) {
        return Single.fromObservable(tickerService
                .fetchTickerPrice(symbol, getCurrency()));   // getDefaultNetwork().symbol
    }

    public  String getCurrency() {
        return cfxNetworkRepository.getCurrency();
    }

    private  void onPrice(Ticker ticker) {
//        LogUtils.d("Tokens", "price: " + ticker.symbol + "  " + ticker.price);
        this.prices.postValue(ticker);
    }

}


