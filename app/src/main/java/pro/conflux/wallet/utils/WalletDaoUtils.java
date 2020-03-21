package pro.conflux.wallet.utils;

import android.text.TextUtils;

import pro.conflux.wallet.ChainWalletApp;
import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.domain.CfxWalletDao;

import java.util.List;

public class WalletDaoUtils {
    public static CfxWalletDao cfxWalletDao = ChainWalletApp.getsInstance().getDaoSession().getCfxWalletDao();

    /**
     * 插入新创建钱包
     *
     * @param cfxWallet 新创建钱包
     */
    public static void insertNewWallet(CfxWallet cfxWallet) {
        updateCurrent(-1);
        cfxWallet.setCurrent(true);
        cfxWalletDao.insert(cfxWallet);
    }

    /**
     * 更新选中钱包
     *
     * @param id 钱包ID
     */
    public static CfxWallet updateCurrent(long id) {
        List<CfxWallet> cfxWallets = cfxWalletDao.loadAll();
        CfxWallet currentWallet = null;
        for (CfxWallet cfxwallet : cfxWallets) {
            if (id != -1 && cfxwallet.getId() == id) {
                cfxwallet.setCurrent(true);
                currentWallet = cfxwallet;
            } else {
                cfxwallet.setCurrent(false);
            }
            cfxWalletDao.update(cfxwallet);
        }
        return currentWallet;
    }

    /**
     * 获取当前钱包
     *
     * @return 钱包对象
     */
    public static CfxWallet getCurrent() {
        List<CfxWallet> cfxWallets = cfxWalletDao.loadAll();
        for (CfxWallet cfxwallet : cfxWallets) {
            if (cfxwallet.isCurrent()) {
                cfxwallet.setCurrent(true);
                return cfxwallet;
            }
        }
        return null;
    }

    /**
     * 查询所有钱包
     */
    public static List<CfxWallet> loadAll() {
        return cfxWalletDao.loadAll();
    }

    /**
     * 检查钱包名称是否存在
     *
     * @param name
     * @return
     */
    public static boolean walletNameChecking(String name) {
        List<CfxWallet> cfxWallets = loadAll();
        for (CfxWallet cfxwallet : cfxWallets
                ) {
            if (TextUtils.equals(cfxwallet.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    public static CfxWallet getWalletById(long walletId) {
        return cfxWalletDao.load(walletId);

    }

    /**
     * 设置isBackup为已备份
     *
     * @param walletId 钱包Id
     */
    public static void setIsBackup(long walletId) {
        CfxWallet cfxwallet = cfxWalletDao.load(walletId);
        cfxwallet.setIsBackup(true);
        cfxWalletDao.update(cfxwallet);
    }

    /**
     * 以助记词检查钱包是否存在
     *
     * @param mnemonic
     * @return true if repeat
     */
    public static boolean checkRepeatByMenmonic(String mnemonic) {
        List<CfxWallet> cfxWallets = loadAll();
        for (CfxWallet cfxwallet : cfxWallets
                ) {
            if (TextUtils.isEmpty(cfxwallet.getMnemonic())) {
                LogUtils.d("wallet mnemonic empty");
                continue;
            }
            if (TextUtils.equals(cfxwallet.getMnemonic().trim(), mnemonic.trim())) {
                LogUtils.d("aleady");
                return true;
            }
        }
        return false;
    }


    public static boolean isValid(String mnemonic) {
        return mnemonic.split(" ").length >= 12;
    }

    public static boolean checkRepeatByKeystore(String keystore) {
        return false;
    }

    /**
     * 修改钱包名称
     *
     * @param walletId
     * @param name
     */
    public static void updateWalletName(long walletId, String name) {
        CfxWallet wallet = cfxWalletDao.load(walletId);
        wallet.setName(name);
        cfxWalletDao.update(wallet);
    }

    public static void setCurrentAfterDelete() {
        List<CfxWallet> cfxWallets = cfxWalletDao.loadAll();
        if (cfxWallets != null && cfxWallets.size() > 0) {
            CfxWallet cfxWallet = cfxWallets.get(0);
            cfxWallet.setCurrent(true);
            cfxWalletDao.update(cfxWallet);
        }
    }
}
