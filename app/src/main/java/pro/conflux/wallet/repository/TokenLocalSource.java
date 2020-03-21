package pro.conflux.wallet.repository;


import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.entity.TokenInfo;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface TokenLocalSource {
    Completable put(NetworkInfo networkInfo, String walletAddress, TokenInfo tokenInfo);
    Single<TokenInfo[]> fetch(NetworkInfo networkInfo, String walletAddress);
}
