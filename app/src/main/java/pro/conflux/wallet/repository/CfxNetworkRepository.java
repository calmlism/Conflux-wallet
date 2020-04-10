package pro.conflux.wallet.repository;

import android.text.TextUtils;


import org.cfx.protocol.Cfx;
import org.cfx.protocol.core.DefaultBlockParameterName;
import org.cfx.protocol.core.methods.response.CfxGetTransactionCount;

import pro.conflux.wallet.entity.NetworkInfo;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static pro.conflux.wallet.C.CONFLUX_MAIN_NETWORK_NAME;
import static pro.conflux.wallet.C.CFX_SYMBOL;

import static pro.conflux.wallet.C.LOCAL_DEV_NETWORK_NAME;


public class CfxNetworkRepository {

    public static CfxNetworkRepository sSelf;

private final NetworkInfo[] NETWORKS = new NetworkInfo[] {
        new NetworkInfo(CONFLUX_MAIN_NETWORK_NAME, CFX_SYMBOL,
                "http://testnet-jsonrpc.conflux-chain.org:12537",
                "http://testnet-jsonrpc.conflux-chain.org:18084/",
                "https://www.confluxscan.io/",1, true),
        new NetworkInfo(LOCAL_DEV_NETWORK_NAME, CFX_SYMBOL,
                "http://testnet-jsonrpc.conflux-chain.org:12537",
                "http://testnet-jsonrpc.conflux-chain.org:18084/",
                "https://www.confluxscan.io/",1, false),
};
    private final SharedPreferenceRepository preferences;
    private NetworkInfo defaultNetwork;
    private final Set<OnNetworkChangeListener> onNetworkChangedListeners = new HashSet<>();


    public static CfxNetworkRepository init(SharedPreferenceRepository sp) {
        if (sSelf == null) {
            sSelf = new CfxNetworkRepository(sp);
        }
        return sSelf;
    }

    private CfxNetworkRepository(SharedPreferenceRepository preferenceRepository) {
        this.preferences = preferenceRepository;
        defaultNetwork = getByName(preferences.getDefaultNetwork());
        if (defaultNetwork == null) {
            defaultNetwork = NETWORKS[0];
        }
    }

    private NetworkInfo getByName(String name) {
        if (!TextUtils.isEmpty(name)) {
            for (NetworkInfo NETWORK : NETWORKS) {
                if (name.equals(NETWORK.name)) {
                    return NETWORK;
                }
            }
        }
        return null;
    }

    public String getCurrency() {
        int currencyUnit =  preferences.getCurrencyUnit();
        if (currencyUnit ==0 ) {
            return "CNY";
        } else {
            return "USD";
        }
    }

    public NetworkInfo getDefaultNetwork() {
        return defaultNetwork;
    }

    public void setDefaultNetworkInfo(NetworkInfo networkInfo) {
        defaultNetwork = networkInfo;
        preferences.setDefaultNetwork(defaultNetwork.name);

        for (OnNetworkChangeListener listener : onNetworkChangedListeners) {
            listener.onNetworkChanged(networkInfo);
        }
    }

    public NetworkInfo[] getAvailableNetworkList() {
        return NETWORKS;
    }

    public void addOnChangeDefaultNetwork(OnNetworkChangeListener onNetworkChanged) {
        onNetworkChangedListeners.add(onNetworkChanged);
    }

    public Single<NetworkInfo> find() {
        return Single.just(getDefaultNetwork())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<BigInteger> getLastTransactionNonce(Cfx web3j, String walletAddress)
    {
        return Single.fromCallable(() -> {
            CfxGetTransactionCount cfxGetTransactionCount = web3j
                    .cfxGetNonce(walletAddress, DefaultBlockParameterName.LATEST)   //
                    .send();
            return cfxGetTransactionCount.getTransactionCount();
        });
    }

}
