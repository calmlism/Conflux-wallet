package pro.conflux.wallet.interact;


import android.arch.lifecycle.MutableLiveData;

import org.cfx.protocol.Cfx;
import org.cfx.protocol.core.methods.request.Transaction;
import org.cfx.protocol.core.methods.response.CfxEstimateGas;
import org.cfx.protocol.core.methods.response.CfxGasPrice;
import org.cfx.protocol.core.methods.response.UsedGasAndCollateralResponse;
import org.cfx.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import pro.conflux.wallet.C;
import pro.conflux.wallet.entity.ConfirmationType;
import pro.conflux.wallet.entity.GasSettings;
import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.repository.CfxNetworkRepository;
import pro.conflux.wallet.repository.SharedPreferenceRepository;
import pro.conflux.wallet.utils.BalanceUtils;
import pro.conflux.wallet.utils.LogUtils;

import static pro.conflux.wallet.C.GAS_LIMIT_MIN;
import static pro.conflux.wallet.C.GAS_PER_BYTE;

public class FetchGasSettingsInteract {


    private final CfxNetworkRepository networkRepository;

    private BigInteger cachedGasPrice;

    private final MutableLiveData<BigInteger> gasPrice = new MutableLiveData<>();

    private int currentChainId;

    private Disposable gasSettingsDisposable;

    private final static long FETCH_GAS_PRICE_INTERVAL = 60;

    public FetchGasSettingsInteract(SharedPreferenceRepository repository, CfxNetworkRepository networkRepository) {
        this.networkRepository = networkRepository;

        this.currentChainId = networkRepository.getDefaultNetwork().chainId;

        cachedGasPrice = new BigInteger(C.DEFAULT_GAS_PRICE);

        gasSettingsDisposable = Observable.interval(0, FETCH_GAS_PRICE_INTERVAL, TimeUnit.SECONDS)
                .doOnNext(l ->
                        fetchGasPriceByWeb3()
                ).subscribe();
    }

    public void clean() {
        gasSettingsDisposable.dispose();
    }


    public MutableLiveData<BigInteger> gasPriceUpdate()
    {
        return gasPrice;
    }

    public Single<GasSettings> fetch(ConfirmationType type) {

        return Single.fromCallable( () -> {
            BigInteger gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT);
            if (type == ConfirmationType.CFX) {
                gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_CFX);
            } else if (type == ConfirmationType.ERC20) {
                gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_TOKENS);
            }
            return new GasSettings(cachedGasPrice, gasLimit);
        });

//        return repository.getGasSettings(forTokenTransfer);
    }

    public Single<GasSettings> fetch(byte[] transactionBytes, boolean isNonFungible) {
        return getGasSettings(transactionBytes, isNonFungible);
    }

    //调取接口获取当前gas，这里先修改为默认100gas
    private void fetchGasPriceByWeb3() {
        LogUtils.d("fetchGasPriceByWeb3 start");
        final Cfx web3j = Cfx.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        try {
            CfxGasPrice price = web3j
                    .cfxGasPrice()
                    .send();

            //这里有网络不稳定，先默认为100gas
            if (price.getGasPrice().compareTo(BalanceUtils.gweiToWei(BigDecimal.ONE)) >= 0)
            {
//                cachedGasPrice = price.getGasPrice();//这里网络不稳定，先使用默认100
                cachedGasPrice=new BigInteger(C.DEFAULT_GAS_PRICE);
                LogUtils.d("FetchGasSettingsInteract", "web3 price:" +  price.getGasPrice());
                gasPrice.postValue(cachedGasPrice);
            }
            else if (networkRepository.getDefaultNetwork().chainId != currentChainId)
            {
                //didn't update the current price correctly, switch to default:
                cachedGasPrice = new BigInteger(C.DEFAULT_GAS_PRICE);
                this.currentChainId = networkRepository.getDefaultNetwork().chainId;
            }
        } catch (Exception ex) {
            // silently
        }
    }

    public Single<GasSettings> getGasSettings(byte[] transactionBytes, boolean isNonFungible) {
        return Single.fromCallable( () -> {
            BigInteger gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT);
            if (transactionBytes != null) {
                if (isNonFungible)
                {
                    gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_NONFUNGIBLE_TOKENS);
                }
                else
                {
                    gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_TOKENS);
                }
                BigInteger estimate = estimateGasLimit(transactionBytes);
                if (estimate.compareTo(gasLimit) > 0) gasLimit = estimate;
            }
            return new GasSettings(cachedGasPrice, gasLimit);
        });
    }

    public Single<GasSettings> fetchDefault(boolean tokenTransfer, NetworkInfo networkInfo) {
        return Single.fromCallable(() -> {
            BigInteger gasPrice = new BigInteger(C.DEFAULT_GAS_PRICE);
            BigInteger gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT);
            if (tokenTransfer) {
                gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_TOKENS);
            }
            return new GasSettings(gasPrice, gasLimit);
        });
    }

    private BigInteger estimateGasLimit(byte[] data)
    {
        BigInteger roundingFactor = BigInteger.valueOf(10000);
        BigInteger txMin = BigInteger.valueOf(GAS_LIMIT_MIN);
        BigInteger bytePrice = BigInteger.valueOf(GAS_PER_BYTE);
        BigInteger dataLength = BigInteger.valueOf(data.length);
        BigInteger estimate = bytePrice.multiply(dataLength).add(txMin);
        estimate = estimate.divide(roundingFactor).add(BigInteger.ONE).multiply(roundingFactor);
        return estimate;
    }

    public BigInteger getTransactionGasLimit(Transaction transaction) {

        final Cfx web3j = Cfx.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));
        try {
            UsedGasAndCollateralResponse cfxEstimateGas = web3j.cfxEstimateGasAndCollateral(transaction).send();
            if (cfxEstimateGas.hasError()){
                throw new RuntimeException(cfxEstimateGas.getError().getMessage());
            }
            return cfxEstimateGas.getValue().getStorageCollateralized();
        } catch (IOException e) {
            throw new RuntimeException("net error");
        }

    }

}
