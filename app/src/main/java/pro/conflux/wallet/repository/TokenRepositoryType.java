package pro.conflux.wallet.repository;


import pro.conflux.wallet.entity.Token;

import io.reactivex.Completable;
import io.reactivex.Observable;
import pro.conflux.wallet.entity.TokenId;
import pro.conflux.wallet.entity.TokenInfo;

public interface TokenRepositoryType {

    Observable<Token[]> fetch(String walletAddress);

    Observable<TokenId[]> fetchTokenIds(String walletAddress, TokenInfo tokenInfo);

    Completable addToken(String walletAddress, String address, String symbol, int decimals,String tokenType);
}
