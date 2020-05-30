package pro.conflux.wallet;

public abstract class C {

    // 获取实时价格（行情 Ticker ） URL
    public static final String TICKER_API_URL = "http://127.0.0.1:8000";

    public static final String CONFLUX_MAIN_NETWORK_NAME = "Mainnet";

    public static final String LOCAL_DEV_NETWORK_NAME = "local_dev";

    public static final String CFX_SYMBOL = "CFX";

    public static final String GWEI_UNIT = "Gdrip";

    public static final String EXTRA_ADDRESS = "ADDRESS";
    public static final String EXTRA_CONTRACT_ADDRESS = "CONTRACT_ADDRESS";
    public static final String EXTRA_CONTRACT_NAME = "CONTRACT_NAME";
    public static final String EXTRA_CONTRACT_TYPE = "CONTRACT_TYPE";
    public static final String EXTRA_DECIMALS = "DECIMALS";
    public static final String EXTRA_SYMBOL = "SYMBOL";
    public static final String EXTRA_BALANCE = "balance";

//    public static final String DEFAULT_GAS_PRICE = "100";
    public static final String DEFAULT_GAS_PRICE = "1000000000";

    public static final String DEFAULT_GAS_LIMIT_FOR_CFX = "100000";

    public static final String DEFAULT_GAS_LIMIT = "100000";


    public static final String DEFAULT_GAS_LIMIT_FOR_TOKENS = "100000";

    public static final String DEFAULT_GAS_LIMIT_FOR_NONFUNGIBLE_TOKENS = "100000";

    public static final long GAS_LIMIT_MIN = 1000000L;
    public static final long GAS_PER_BYTE = 300;
    public static final int CFX_DECIMALS = 18;

    public interface ErrorCode {

        int UNKNOWN = 1;
        int CANT_GET_STORE_PASSWORD = 2;
    }

    public interface Key {
        String WALLET = "wallet";
        String TRANSACTION = "transaction";
        String SHOULD_SHOW_SECURITY_WARNING = "should_show_security_warning";
    }
}
