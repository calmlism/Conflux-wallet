package pro.conflux.wallet.interact;

import pro.conflux.wallet.domain.CfxWallet;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.conflux.wallet.utils.WalletDaoUtils;

public class FetchWalletInteract {


    public FetchWalletInteract() {
    }

    public Single<List<CfxWallet>> fetch() {


        return Single.fromCallable(() -> {
            return WalletDaoUtils.loadAll();
        }).observeOn(AndroidSchedulers.mainThread());

    }

    public Single<CfxWallet> findDefault() {

        return Single.fromCallable(() -> {
            return WalletDaoUtils.getCurrent();
        });

    }
}
