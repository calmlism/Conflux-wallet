package pro.conflux.wallet.interact;

import org.cfx.protocol.core.DefaultBlockParameterName;
import org.cfx.protocol.core.methods.request.Transaction;
import org.cfx.protocol.core.methods.response.CfxGetTransactionCount;
import org.cfx.protocol.core.methods.response.UsedGasAndCollateral;
import org.cfx.protocol.core.methods.response.UsedGasAndCollateralResponse;
import org.cfx.rlp.RlpEncoder;
import org.cfx.rlp.RlpList;
import org.cfx.rlp.RlpString;
import org.cfx.rlp.RlpType;
import org.cfx.utils.Bytes;

import org.cfx.crypto.Credentials;
import org.cfx.crypto.RawTransaction;
import org.cfx.crypto.Sign;
import org.cfx.crypto.TransactionEncoder;
import org.cfx.crypto.WalletUtils;

import org.cfx.protocol.Cfx;

import org.cfx.protocol.core.methods.response.CfxSendTransaction;

import org.cfx.protocol.http.HttpService;

import org.cfx.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.repository.CfxNetworkRepository;
import pro.conflux.wallet.repository.TokenRepository;
import pro.conflux.wallet.utils.LogUtils;

public class CreateTransactionInteract {


    private final CfxNetworkRepository networkRepository;


    public CreateTransactionInteract(
            CfxNetworkRepository networkRepository) {
        this.networkRepository = networkRepository;

    }

    public Single<byte[]> sign(CfxWallet wallet, byte[] message, String pwd) {
        return getSignature(wallet, message, pwd);
    }

    // transfer ether
    public Single<String>  createCfxTransaction(CfxWallet from, String to, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit, String password) {

        final Cfx cfx = Cfx.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        return networkRepository.getLastTransactionNonce(cfx, from.address)
                .flatMap(nonce -> Single.fromCallable( () -> {

            Credentials credentials = WalletUtils.loadCredentials(password,  from.getKeystorePath());

            BigInteger storageLimit = BigInteger.valueOf(0) ;

            //判断是否为合约
            if(to.indexOf("0x8") > -1){

                UsedGasAndCollateral usedGasAndCollateral = cfx.cfxEstimateGasAndCollateral(new Transaction(from.address,nonce,gasPrice,gasLimit,to,amount,null)).send().getValue();
                storageLimit = usedGasAndCollateral.getGasUsed();
                storageLimit = BigInteger.valueOf(512);//暂时先固定设置为512
            }

            BigInteger epochHeight = cfx.cfxBlockNumber().send().getBlockNumber();
            BigInteger chainId = BigInteger.valueOf(0);
            RawTransaction rawTransaction = RawTransaction.createCfxTransaction(nonce, gasPrice, gasLimit, to, amount,storageLimit,epochHeight,chainId);

            byte[] signedMessage =TransactionEncoder.signMessage(rawTransaction,credentials);

            String hexValue = Numeric.toHexString(signedMessage);

            CfxSendTransaction cfxSendTransaction = cfx.cfxSendRawTransaction(hexValue).sendAsync().get();

            String hash = cfxSendTransaction.getTransactionHash();
            return hash;

        } ).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    // transfer ERC20
    public Single<String>  createERC20Transfer(CfxWallet from,  String to, String contractAddress, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit, String password) {
        final Cfx cfx = Cfx.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        String callFuncData = TokenRepository.createTokenTransferData(to, amount);


        return networkRepository.getLastTransactionNonce(cfx, from.address)
                .flatMap(nonce -> Single.fromCallable( () -> {

            Credentials credentials = WalletUtils.loadCredentials(password,  from.getKeystorePath());
//            BigInteger storageLimit = BigInteger.valueOf(100000) ;
            UsedGasAndCollateral usedGasAndCollateral = cfx.cfxEstimateGasAndCollateral(new Transaction(from.address,nonce,gasPrice,gasLimit,to,amount,null)).send().getValue();
            BigInteger storageLimit =usedGasAndCollateral.getGasUsed();

            BigInteger epochHeight = cfx.cfxBlockNumber().send().getBlockNumber();
            BigInteger chainId = BigInteger.valueOf(0);
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce, gasPrice, gasLimit, contractAddress, callFuncData,storageLimit,epochHeight,chainId);

            LogUtils.d("rawTransaction:" + rawTransaction);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

            String hexValue = Numeric.toHexString(signedMessage);

            CfxSendTransaction cfxSendTransaction = cfx.cfxSendRawTransaction(hexValue).sendAsync().get();

            return cfxSendTransaction.getTransactionHash();

        } ).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    public Single<String> create(CfxWallet from, String to, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit,  String data, String pwd) throws IOException {
        return createTransaction(from, to, subunitAmount, gasPrice, gasLimit, data, pwd)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<String> createContract(CfxWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String pwd) throws IOException {
        return createTransaction(from, gasPrice, gasLimit, data, pwd)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread());
    }


    // https://github.com/web3j/web3j/issues/208
    // https://ethereum.stackexchange.com/questions/17708/solidity-ecrecover-and-web3j-sign-signmessage-are-not-compatible-is-it

    // message : TransactionEncoder.encode(rtx)   // may wrong

    public Single<byte[]> getSignature(CfxWallet wallet, byte[] message, String password) {
        return  Single.fromCallable(() -> {
            Credentials credentials = WalletUtils.loadCredentials(password, wallet.getKeystorePath());
            Sign.SignatureData signatureData = Sign.signMessage(
                    message, credentials.getEcKeyPair());

            List<RlpType> result = new ArrayList<>();
            result.add(RlpString.create(message));

            if (signatureData != null) {
                result.add(RlpString.create(signatureData.getV()));
                result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
                result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
            }

            RlpList rlpList = new RlpList(result);
            return RlpEncoder.encode(rlpList);
        });
    }

    public Single<String> createTransaction(CfxWallet from, String toAddress, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, String data, String password) throws IOException {
        final Cfx web3j = Cfx.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));
        BigInteger storageLimit = BigInteger.valueOf(100000) ;
        BigInteger epochHeight = web3j.cfxBlockNumber().send().getBlockNumber();
        BigInteger chainId = BigInteger.valueOf(0);

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit,toAddress, subunitAmount,  data,storageLimit,epochHeight,chainId))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    CfxSendTransaction raw = web3j
                            .cfxSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    return raw.getTransactionHash();
                })).subscribeOn(Schedulers.io());
    }


    // for DApp create contract transaction
    public Single<String> createTransaction(CfxWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String password) throws IOException {

        final Cfx web3j = Cfx.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));
        BigInteger storageLimit = BigInteger.valueOf(100000) ;
        BigInteger epochHeight = web3j.cfxBlockNumber().send().getBlockNumber();
        BigInteger chainId = BigInteger.valueOf(0);

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit, BigInteger.ZERO, data,storageLimit,epochHeight,chainId))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    CfxSendTransaction raw = web3j
                            .cfxSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    return raw.getTransactionHash();
                })).subscribeOn(Schedulers.io());

    };


    // for DApp  create contract  transaction
    private Single<RawTransaction> getRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, BigInteger value, String data,BigInteger storageLimit,
                                                     BigInteger epochHeight,
                                                     BigInteger chainId)
    {
        return Single.fromCallable(() ->
                RawTransaction.createContractTransaction(
                        nonce,
                        gasPrice,
                        gasLimit,
                        value,
                        data,storageLimit,epochHeight,chainId));
    }

    private Single<RawTransaction> getRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to , BigInteger value, String data,BigInteger storageLimit,
                                                     BigInteger epochHeight,
                                                     BigInteger chainId)
    {
        return Single.fromCallable(() ->
                RawTransaction.createTransaction(
                        nonce,
                        gasPrice,
                        gasLimit,
                        to,
                        value,
                        data,storageLimit,epochHeight,chainId));
    }

    private  Single<byte[]> signEncodeRawTransaction(RawTransaction rtx, String password, CfxWallet wallet, int chainId) {

        return Single.fromCallable(() -> {
            Credentials credentials = WalletUtils.loadCredentials(password, wallet.getKeystorePath());
            byte[] signedMessage = TransactionEncoder.signMessage(rtx, credentials);
            return signedMessage;
        });
    }



}
