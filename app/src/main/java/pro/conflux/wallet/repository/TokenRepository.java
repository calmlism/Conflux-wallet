package pro.conflux.wallet.repository;

import android.util.Log;

import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.entity.Token;
import pro.conflux.wallet.entity.TokenInfo;
import pro.conflux.wallet.service.TokenExplorerClientType;
import pro.conflux.wallet.utils.LogUtils;

import org.cfx.abi.FunctionEncoder;
import org.cfx.abi.FunctionReturnDecoder;
import org.cfx.abi.TypeReference;
import org.cfx.abi.datatypes.Address;
import org.cfx.abi.datatypes.Bool;
import org.cfx.abi.datatypes.Function;
import org.cfx.abi.datatypes.Type;
import org.cfx.abi.datatypes.generated.Uint256;

import org.cfx.protocol.Cfx;
import org.cfx.protocol.core.DefaultBlockParameterName;
import org.cfx.protocol.core.methods.request.Transaction;
import org.cfx.protocol.core.methods.response.CfxGetTransactionCount;
import org.cfx.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;

public class TokenRepository implements TokenRepositoryType {

    private final TokenExplorerClientType tokenNetworkService;
    private final TokenLocalSource tokenLocalSource;
    private final OkHttpClient httpClient;
    private final CfxNetworkRepository cfxNetworkRepository;
    private Cfx web3j;

    public TokenRepository(
            OkHttpClient okHttpClient,
            CfxNetworkRepository cfxNetworkRepository,
            TokenExplorerClientType tokenNetworkService,
            TokenLocalSource tokenLocalSource) {

        this.httpClient = okHttpClient;
        this.cfxNetworkRepository = cfxNetworkRepository;
        this.tokenNetworkService = tokenNetworkService;
        this.tokenLocalSource = tokenLocalSource;
        this.cfxNetworkRepository.addOnChangeDefaultNetwork(this::buildWeb3jClient);

        buildWeb3jClient(cfxNetworkRepository.getDefaultNetwork());
    }

    private void buildWeb3jClient(NetworkInfo networkInfo) {
        LogUtils.d(networkInfo.rpcServerUrl + " " + networkInfo.rpcServerUrl);
        web3j = Cfx.build(new HttpService(networkInfo.rpcServerUrl, httpClient, false));
    }

    @Override
    public Observable<Token[]> fetch(String walletAddress) {
        return Observable.create(e -> {
            NetworkInfo defaultNetwork = cfxNetworkRepository.getDefaultNetwork();

//            Token[] tokens = tokenLocalSource.fetch(defaultNetwork, walletAddress)
//                    .map(items -> {
//                        int len = items.length;
//                        Token[] result = new Token[len];
//                        for (int i = 0; i < len; i++) {
//                            result[i] = new Token(items[i], null);
//                        }
//                        return result;
//                    })
//                    .blockingGet();
//            e.onNext(tokens);

//            updateTokenInfoCache(defaultNetwork, walletAddress);//更新接口下的钱包关联绑定的token数据，这里不用，所以屏蔽
            Token[]  tokens = tokenLocalSource.fetch(defaultNetwork,  walletAddress)
                        .map(items -> {
                            int len = items.length;
                            Token[] result = new Token[len];
                            for (int i = 0; i < len; i++) {
                                BigDecimal balance = null;
                                try {
                                    if (items[i].address.isEmpty()) {
                                        balance = getCfxBalance(walletAddress);
                                    } else {
                                        balance = getBalance(walletAddress, items[i]);
                                    }


                                } catch (Exception e1) {
                                    Log.d("TOKEN", "Err" +  e1.getMessage());
                                    /* Quietly */
                                }

                                if (balance == null || balance.compareTo(BigDecimal.ZERO) == 0) {
                                    result[i] = new Token(items[i], "0");
                                } else {
                                    BigDecimal decimalDivisor = new BigDecimal(Math.pow(10, items[i].decimals));
                                    BigDecimal cfxBalance = balance.divide(decimalDivisor);
                                    if (items[i].decimals > 4) {

                                        result[i] = new Token(items[i], cfxBalance.setScale(4, RoundingMode.CEILING).toPlainString());
                                    } else {
                                        result[i] = new Token(items[i], cfxBalance.setScale(items[i].decimals, RoundingMode.CEILING).toPlainString());
                                    }

                                }


                            }
                            return result;
                        }).blockingGet();

            e.onNext(tokens);

        });
    }

    @Override
    public Completable addToken(String walletAddress, String address, String symbol, int decimals,String tokenType) {

        return tokenLocalSource.put(
                cfxNetworkRepository.getDefaultNetwork(),
                walletAddress,
                new TokenInfo(address, "", symbol, decimals,tokenType));
    }

    private void updateTokenInfoCache(NetworkInfo defaultNetwork, String walletAddress) {
        if (!defaultNetwork.isMainNetwork) {
            return;
        }
        tokenNetworkService
                .fetch(walletAddress)
                .flatMapCompletable(items -> Completable.fromAction(() -> {
                    for (TokenInfo tokenInfo : items) {
                        try {
                            tokenLocalSource.put(
                                    cfxNetworkRepository.getDefaultNetwork(), walletAddress, tokenInfo)
                                    .blockingAwait();
                        } catch (Throwable t) {
                            Log.d("TOKEN_REM", "Err", t);
                        }
                    }
                }))
                .blockingAwait();
    }

    private BigDecimal getCfxBalance(String walletAddress) throws Exception {


        return new BigDecimal(web3j
                .cfxGetBalance(walletAddress, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance());
    }


    /**
     * 获取token余额
     * @param walletAddress
     * @param tokenInfo
     * @return
     * @throws Exception
     */
    private BigDecimal getBalance(String walletAddress, TokenInfo tokenInfo) throws Exception {

        org.cfx.abi.datatypes.Function function = balanceOf(walletAddress);

        String responseValue = callSmartContractFunction(function, tokenInfo.address, walletAddress);

        List<Type> response = FunctionReturnDecoder.decode(
                responseValue, function.getOutputParameters());
        if (response.size() == 1) {
            return new BigDecimal(((Uint256) response.get(0)).getValue());
        } else {
            return null;
        }
    }

    /**
     * 获取指定地址指定索引的tokenid信息
     * @param walletAddress
     * @param index
     * @param tokenInfo
     * @return
     * @throws Exception
     */
    private String getTokenOfOwnerByIndex(String walletAddress,int index, TokenInfo tokenInfo) throws Exception {

        org.cfx.abi.datatypes.Function function = tokenOfOwnerByIndex(walletAddress,index);

        String responseValue = callSmartContractFunction(function, tokenInfo.address, walletAddress);

        List<Type> response = FunctionReturnDecoder.decode(
                responseValue, function.getOutputParameters());
        if (response.size() == 1) {
            return ((Address) response.get(0)).getValue();
        } else {
            return null;
        }
    }

    private static org.cfx.abi.datatypes.Function balanceOf(String owner) {
        return new org.cfx.abi.datatypes.Function(
                "balanceOf",
                Collections.singletonList(new Address(owner)),
                Collections.singletonList(new TypeReference<Uint256>() {}));
    }


    /**
     * 获取指定地址下的索引的tokenid
     * @param owner
     * @param index
     * @return
     */
    private static org.cfx.abi.datatypes.Function tokenOfOwnerByIndex(String owner, int index){
        List<Type> params = Arrays.<Type>asList(new Address(owner), new Uint256(index));
        List<TypeReference<?>> returnTypes = Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
        });
        return new org.cfx.abi.datatypes.Function(
                "tokenOfOwnerByIndex",
                params,
                returnTypes);
    }



    private String callSmartContractFunction(
            org.cfx.abi.datatypes.Function function, String contractAddress, String walletAddress) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);
        CfxGetTransactionCount cfxGetTransactionCount = web3j
                .cfxGetNonce(walletAddress, DefaultBlockParameterName.LATEST)   //
                .send();
        BigInteger nonceValue= cfxGetTransactionCount.getTransactionCount();

        Transaction tokenTr =new Transaction(walletAddress,nonceValue, (BigInteger)null, (BigInteger)null, contractAddress, (BigInteger)null, encodedFunction);
        org.cfx.protocol.core.methods.response.CfxCall response = web3j.cfxCall(
                tokenTr,
                DefaultBlockParameterName.LATEST)
                .sendAsync().get();

        return  response.getValue();


    }

    public static String createTokenTransferData(String to, BigInteger tokenAmount) {
        List<Type> params = Arrays.<Type>asList(new Address(to), new Uint256(tokenAmount));

        List<TypeReference<?>> returnTypes = Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
        });

        Function function = new Function("transfer", params, returnTypes);

//        Function function = new Function(
//                "transfer",
//                Arrays.asList(new Address(to), new Uint256(tokenAmount)),
//                Arrays.asList(new TypeReference<Type>() {
//                }));
        return FunctionEncoder.encode(function);
//        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction));
    }
}
