package pro.conflux.wallet.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;

import pro.conflux.wallet.domain.CfxWallet;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.wallet.DeterministicSeed;
import org.cfx.crypto.CipherException;
import org.cfx.crypto.Credentials;
import org.cfx.crypto.ECKeyPair;
import org.cfx.crypto.Keys;
import org.cfx.crypto.Wallet;
import org.cfx.crypto.WalletFile;
import org.cfx.crypto.WalletUtils;
import org.cfx.protocol.ObjectMapperFactory;
import org.cfx.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

/**
 * 钱包创建工具类
 */

public class CFXWalletUtils {

    private static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    /**
     * 随机
     */
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();
    private Credentials credentials;

    /**
     * 通用基于bip44协议的助记词路径 （imtoken jaxx Metamask mycfxwallet）
     */
    public static String CFX_JAXX_TYPE = "m/44'/60'/0'/0/0";
    public static String CFX_LEDGER_TYPE = "m/44'/60'/0'/0";
    public static String CFX_CUSTOM_TYPE = "m/44'/60'/1'/0/0";

    /**
     * 创建助记词，并通过助记词创建钱包
     *
     * @param walletName
     * @param pwd
     * @return
     */
    public static CfxWallet generateMnemonic(String walletName, String pwd) {
        String[] pathArray = CFX_JAXX_TYPE.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;

        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }

    /**
     * 通过导入助记词，导入钱包
     *
     * @param path 路径
     * @param list 助记词
     * @param pwd  密码
     * @return
     */
    public static CfxWallet importMnemonic(String path, List<String> list, String pwd) {
        if (!path.startsWith("m") && !path.startsWith("M")) {
            //参数非法
            return null;
        }
        String[] pathArray = path.split("/");
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(generateNewWalletName(), ds, pathArray, pwd);
    }

    @NonNull
    private static String generateNewWalletName() {
        char letter1 = (char) (int) (Math.random() * 26 + 97);
        char letter2 = (char) (int) (Math.random() * 26 + 97);

        String walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-wallet";
        while (WalletDaoUtils.walletNameChecking(walletName)) {
            letter1 = (char) (int) (Math.random() * 26 + 97);
            letter2 = (char) (int) (Math.random() * 26 + 97);
            walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-wallet";
        }
        return walletName;
    }

    /**
     * @param walletName 钱包名称
     * @param ds         助记词加密种子
     * @param pathArray  助记词标准
     * @param pwd        密码
     * @return
     */
    @Nullable
    public static CfxWallet generateWalletByMnemonic(String walletName, DeterministicSeed ds,
                                                     String[] pathArray, String pwd) {
        //种子
        byte[] seedBytes = ds.getSeedBytes();
//        System.out.println(Arrays.toString(seedBytes));
        //助记词
        List<String> mnemonic = ds.getMnemonicCode();
        LogUtils.d("mnemonic: " + mnemonic);
        if (seedBytes == null)
            return null;

        DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);

        for (int i = 1; i < pathArray.length; i++) {
            ChildNumber childNumber;
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                childNumber = new ChildNumber(number, true);
            } else {
                int number = Integer.parseInt(pathArray[i]);
                childNumber = new ChildNumber(number, false);
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
        }

        ECKeyPair keyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());
        CfxWallet cfxWallet = generateWallet(walletName, pwd, keyPair);
        if (cfxWallet != null) {
            cfxWallet.setMnemonic(convertMnemonicList(mnemonic));
        }
        return cfxWallet;
    }

    private static String convertMnemonicList(List<String> mnemonics) {
        StringBuilder sb = new StringBuilder();
        int size =  mnemonics.size();

        for (int i = 0; i < size; i++) {
            sb.append(mnemonics.get(i));
            if (i != size - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    @Nullable
    private static CfxWallet generateWallet(String walletName, String pwd, ECKeyPair ecKeyPair) {
        WalletFile keyStoreFile;
        try {
            keyStoreFile = Wallet.create(pwd, ecKeyPair, 1024, 1); // WalletUtils. .generateNewWalletFile();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger publicKey = ecKeyPair.getPublicKey();
        String s = publicKey.toString();
        LogUtils.i("CFXWalletUtils", "publicKey = " + s);

        String wallet_dir = AppFilePath.Wallet_DIR;
        LogUtils.i("CFXWalletUtils", "wallet_dir = " + wallet_dir);

        String keystorePath = "keystore_" + walletName + ".json";
        File destination = new File(wallet_dir, "keystore_" + walletName + ".json");

        //目录不存在则创建目录，创建不了则报错
        if (!createParentDir(destination)) {
            return null;
        }
        try {
            objectMapper.writeValue(destination, keyStoreFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        CfxWallet cfxWallet = new CfxWallet();
        cfxWallet.setName(walletName);
        //cfxWallet.setAddress(Keys.toChecksumAddress(keyStoreFile.getAddress()));//钱包地址特殊处理
        cfxWallet.setAddress("0x1"+keyStoreFile.getAddress().substring(1));//这里去掉上面的特殊处理，解决大写钱包地址问题
        cfxWallet.setKeystorePath(destination.getAbsolutePath());
        cfxWallet.setPassword(Md5Utils.md5(pwd));
        return cfxWallet;
    }

    /**
     * 通过keystore.json文件导入钱包
     *
     * @param keystore 原json文件
     * @param pwd      json文件密码
     * @return
     */
    public static CfxWallet loadWalletByKeystore(String keystore, String pwd) {
        Credentials credentials = null;
        try {
            WalletFile walletFile = null;
            walletFile = objectMapper.readValue(keystore, WalletFile.class);

//            WalletFile walletFile = new Gson().fromJson(keystore, WalletFile.class);
            credentials = Credentials.create(Wallet.decrypt(pwd, walletFile));
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("CFXWalletUtils", e.toString());
        } catch (CipherException e) {
            LogUtils.e("CFXWalletUtils", e.toString());
//            ToastUtils.showToast(R.string.load_wallet_by_official_wallet_keystore_input_tip);
            e.printStackTrace();
        }
        if (credentials != null) {
            return generateWallet(generateNewWalletName(), pwd, credentials.getEcKeyPair());
        }
        return null;
    }

    /**
     * 通过明文私钥导入钱包
     *
     * @param privateKey
     * @param pwd
     * @return
     */
    public static CfxWallet loadWalletByPrivateKey(String privateKey, String pwd) {
        Credentials credentials = null;
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        return generateWallet(generateNewWalletName(), pwd, ecKeyPair);
    }


    public static boolean isTooSimplePrivateKey(String privateKey) {


        if (Numeric.toBigInt(privateKey).intValue() < 100000000) {
            return true;
        } else {
            return true;
        }

    }

    private static boolean createParentDir(File file) {
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建");
            if (!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        return true;
    }

    /**
     * 修改钱包密码
     *
     * @param walletId
     * @param walletName
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public static CfxWallet modifyPassword(long walletId, String walletName, String oldPassword, String newPassword) {
        CfxWallet cfxWallet = WalletDaoUtils.cfxWalletDao.load(walletId);
        Credentials credentials = null;
        ECKeyPair keypair = null;
        try {
            credentials = WalletUtils.loadCredentials(oldPassword, cfxWallet.getKeystorePath());
            keypair = credentials.getEcKeyPair();
            File destinationDirectory = new File(AppFilePath.Wallet_DIR, "keystore_" + walletName + ".json");
            WalletUtils.generateWalletFile(newPassword, keypair, destinationDirectory, true);
            cfxWallet.setPassword(newPassword);
            WalletDaoUtils.cfxWalletDao.insert(cfxWallet);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfxWallet;
    }

    /**
     * 导出明文私钥
     *
     * @param walletId 钱包Id
     * @param pwd      钱包密码
     * @return
     */
    public static String derivePrivateKey(long walletId, String pwd) {
        CfxWallet cfxWallet = WalletDaoUtils.cfxWalletDao.load(walletId);
        Credentials credentials;
        ECKeyPair keypair;
        String privateKey = null;
        try {
            credentials = WalletUtils.loadCredentials(pwd, cfxWallet.getKeystorePath());
            keypair = credentials.getEcKeyPair();
            privateKey = Numeric.toHexStringNoPrefixZeroPadded(keypair.getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * 导出keystore文件
     *
     * @param walletId
     * @param pwd
     * @return
     */
    public static String deriveKeystore(long walletId, String pwd) {
        CfxWallet cfxWallet = WalletDaoUtils.cfxWalletDao.load(walletId);
        String keystore = null;
        WalletFile walletFile;
        try {
            walletFile = objectMapper.readValue(new File(cfxWallet.getKeystorePath()), WalletFile.class);
            keystore = objectMapper.writeValueAsString(walletFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keystore;
    }

    /**
     * 删除钱包
     *
     * @param walletId
     * @return
     */
    public static boolean deleteWallet(long walletId) {
        CfxWallet cfxWallet = WalletDaoUtils.cfxWalletDao.load(walletId);
        if (deleteFile(cfxWallet.getKeystorePath())) {
            WalletDaoUtils.cfxWalletDao.deleteByKey(walletId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
//                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
//                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
//            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
