package pro.conflux.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.entity.Ticker;
import pro.conflux.wallet.entity.Token;
import pro.conflux.wallet.entity.TokenId;
import pro.conflux.wallet.entity.TokenInfo;
import pro.conflux.wallet.interact.FetchTokensInteract;
import pro.conflux.wallet.interact.FetchWalletInteract;
import pro.conflux.wallet.repository.CfxNetworkRepository;

public class TokenIdListViewModel  extends BaseViewModel  {

    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();
    private final MutableLiveData<CfxWallet> defaultWallet = new MutableLiveData<>();
    private final CfxNetworkRepository cfxNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;
    private final FetchTokensInteract fetchTokensInteract;

    private final MutableLiveData<Ticker> prices = new MutableLiveData<>();
    private final MutableLiveData<TokenId[]> tokenids = new MutableLiveData<>();

    private TokenInfo defaultTokenInfo ;

    TokenIdListViewModel(
            CfxNetworkRepository cfxNetworkRepository,
            FetchWalletInteract findDefaultWalletInteract,
            FetchTokensInteract fetchTokensInteract) {
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.cfxNetworkRepository = cfxNetworkRepository;
        this.fetchTokensInteract = fetchTokensInteract;

    }

    public void prepare(TokenInfo tokenInfo) {
        progress.postValue(true);
        defaultTokenInfo = tokenInfo;
        defaultNetwork.postValue(cfxNetworkRepository.getDefaultNetwork());
        disposable = findDefaultWalletInteract.findDefault()
                .subscribe(this::onDefaultWallet, this::onError);

    }

    private void onDefaultWallet(CfxWallet wallet) {
        defaultWallet.setValue(wallet);
        fetchTokenIDs();
    }

    public MutableLiveData<Ticker> prices() {
        return prices;
    }

    public void fetchTokenIDs() {
        progress.postValue(true);

        disposable = fetchTokensInteract
                .fetchTokenIds(defaultWallet.getValue().address,defaultTokenInfo)
                .subscribe(this::onTokenIds, this::onError);
    }


    public LiveData<TokenId[]> tokenids() {
        return tokenids;
    }

    private void onTokenIds(TokenId[] tokenids) {
        progress.postValue(false);
        this.tokenids.postValue(tokenids);


    }


}
