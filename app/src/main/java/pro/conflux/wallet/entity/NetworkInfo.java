package pro.conflux.wallet.entity;

public class NetworkInfo {
    public final String name;
    public final String symbol;
    public final String rpcServerUrl;
    public final String backendUrl;
    public final String cfxscanUrl;
    public final int chainId;
    public final boolean isMainNetwork;

    public NetworkInfo(
            String name,
            String symbol,
            String rpcServerUrl,
            String backendUrl,
            String cfxscanUrl,
            int chainId,
            boolean isMainNetwork) {
        this.name = name;
        this.symbol = symbol;
        this.rpcServerUrl = rpcServerUrl;
        this.backendUrl = backendUrl;
        this.cfxscanUrl = cfxscanUrl;
        this.chainId = chainId;
        this.isMainNetwork = isMainNetwork;
    }
}
