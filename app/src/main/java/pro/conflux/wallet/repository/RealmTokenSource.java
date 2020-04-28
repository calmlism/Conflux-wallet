package pro.conflux.wallet.repository;


import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.entity.TokenInfo;
import pro.conflux.wallet.repository.entity.RealmTokenInfo;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmTokenSource implements TokenLocalSource {

    @Override
    public Completable put(NetworkInfo networkInfo, String walletAddress, TokenInfo tokenInfo) {
        return Completable.fromAction(() -> putInNeed(networkInfo, walletAddress, tokenInfo));
    }

    @Override
    public Single<TokenInfo[]> fetch(NetworkInfo networkInfo, String walletAddress) {
        return Single.fromCallable(() -> {
            Realm realm = null;
            try {
                realm = getRealmInstance(networkInfo, walletAddress);
                RealmResults<RealmTokenInfo> realmItems = realm.where(RealmTokenInfo.class)
                        .sort("addedTime", Sort.ASCENDING)
                        .findAll();
                int len = realmItems.size();

                TokenInfo[] result = new TokenInfo[len + 1];

                result[0] = new TokenInfo("", "CFX", "CFX", 18,"BASE");//默认第一个显示CFX

                for (int i = 0; i < len; i++) {
                    RealmTokenInfo realmItem = realmItems.get(i);
                    if (realmItem != null) {
                        result[i + 1] = new TokenInfo(
                                realmItem.getAddress(),
                                realmItem.getName(),
                                realmItem.getSymbol(),
                                realmItem.getDecimals(),
                                realmItem.getType());
                    }
                }

                return result;
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    private Realm getRealmInstance(NetworkInfo networkInfo, String walletAddress) {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(walletAddress + "-" + networkInfo.name + ".realm")
                .schemaVersion(1)
                .build();
        return Realm.getInstance(config);
    }

    private void putInNeed(NetworkInfo networkInfo, String walletAddress, TokenInfo tokenInfo) {
        Realm realm = null;
        try {
            realm = getRealmInstance(networkInfo, walletAddress);
            RealmTokenInfo realmTokenInfo = realm.where(RealmTokenInfo.class)
                    .equalTo("address", tokenInfo.address)
                    .findFirst();
            if (realmTokenInfo == null) {
                realm.executeTransaction(r -> {
                    RealmTokenInfo obj = r.createObject(RealmTokenInfo.class, tokenInfo.address);
                    obj.setName(tokenInfo.name);
                    obj.setSymbol(tokenInfo.symbol);
                    obj.setDecimals(tokenInfo.decimals);
                    obj.setType(tokenInfo.type);
                    obj.setAddedTime(System.currentTimeMillis());
                });
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

}
