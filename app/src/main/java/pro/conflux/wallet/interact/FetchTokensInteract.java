package pro.conflux.wallet.interact;


import pro.conflux.wallet.entity.Token;
import pro.conflux.wallet.entity.TokenId;
import pro.conflux.wallet.entity.TokenInfo;
import pro.conflux.wallet.repository.TokenRepositoryType;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FetchTokensInteract {

    private final TokenRepositoryType tokenRepository;

    public FetchTokensInteract(TokenRepositoryType tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Observable<Token[]> fetch(String walletAddress) {
        return tokenRepository.fetch(walletAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TokenId[]> fetchTokenIds(String walletAddress, TokenInfo tokenInfo){
        return tokenRepository.fetchTokenIds(walletAddress,tokenInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
