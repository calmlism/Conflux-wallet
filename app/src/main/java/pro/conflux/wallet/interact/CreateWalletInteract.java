package pro.conflux.wallet.interact;

import java.util.Arrays;

import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.utils.CFXWalletUtils;
import pro.conflux.wallet.utils.WalletDaoUtils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateWalletInteract {


    public CreateWalletInteract() {
    }

    public Single<CfxWallet> create(final String name, final String pwd, String confirmPwd, String pwdReminder) {
        return Single.fromCallable(() -> {
            CfxWallet cfxWallet = CFXWalletUtils.generateMnemonic(name, pwd);
            WalletDaoUtils.insertNewWallet(cfxWallet);
            return cfxWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<CfxWallet> loadWalletByKeystore(final String keystore, final String pwd) {
        return Single.fromCallable(() -> {
            CfxWallet cfxWallet = CFXWalletUtils.loadWalletByKeystore(keystore, pwd);
            if (cfxWallet != null) {
                WalletDaoUtils.insertNewWallet(cfxWallet);
            }

            return cfxWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<CfxWallet> loadWalletByPrivateKey(final String privateKey, final String pwd) {
        return Single.fromCallable(() -> {

            CfxWallet cfxWallet = CFXWalletUtils.loadWalletByPrivateKey(privateKey, pwd);
                    if (cfxWallet != null) {
                        WalletDaoUtils.insertNewWallet(cfxWallet);
                    }
                    return cfxWallet;
                }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<CfxWallet> loadWalletByMnemonic(final String bipPath, final String mnemonic, final String pwd) {
        return Single.fromCallable(() -> {
            CfxWallet cfxWallet = CFXWalletUtils.importMnemonic(bipPath
                    , Arrays.asList(mnemonic.split(" ")), pwd);
            if (cfxWallet != null) {
                WalletDaoUtils.insertNewWallet(cfxWallet);
            }
            return cfxWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


    }

}
