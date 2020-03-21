package pro.conflux.wallet.interact;

import io.reactivex.Single;
import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.utils.CFXWalletUtils;
import pro.conflux.wallet.utils.WalletDaoUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ModifyWalletInteract {


    public ModifyWalletInteract() {
    }


    public Single<Boolean> modifyWalletName(long walletId, String name) {
        return Single.fromCallable(() -> {
            WalletDaoUtils.updateWalletName(walletId, name);
            return true;
        });

    }


    public Single<CfxWallet> modifyWalletPwd(final long walletId, final String walletName, final String oldPassword, final String newPassword) {

        return Single.fromCallable(() -> {
            return CFXWalletUtils.modifyPassword(walletId, walletName, oldPassword, newPassword);
                }

        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String>  deriveWalletPrivateKey(final long walletId, final String password) {

        return Single.fromCallable(() -> {
                return CFXWalletUtils.derivePrivateKey(walletId, password);
        } ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public  Single<String>  deriveWalletKeystore(final long walletId, final String password) {

        return Single.fromCallable(() -> {
                return CFXWalletUtils.deriveKeystore(walletId, password);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        // mView.showDeriveKeystore(keystore);

    }


    public Single<Boolean> deleteWallet(final long walletId) {
        return Single.fromCallable(() -> {
               return CFXWalletUtils.deleteWallet(walletId);
            }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
