package pro.conflux.wallet.repository;

import pro.conflux.wallet.service.BlockExplorerClient;
import pro.conflux.wallet.service.CfxplorerTokenService;
import pro.conflux.wallet.service.TokenExplorerClientType;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

public class RepositoryFactory {

    public TokenRepository tokenRepository;
    public TransactionRepository transactionRepository;
    public CfxNetworkRepository cfxNetworkRepository;

    public static RepositoryFactory sSelf;

    private RepositoryFactory(SharedPreferenceRepository sp, OkHttpClient httpClient, Gson gson) {
        cfxNetworkRepository = CfxNetworkRepository.init(sp);

        TokenLocalSource tokenLocalSource = new RealmTokenSource();

        TokenExplorerClientType tokenExplorerClientType =  new CfxplorerTokenService(httpClient, gson);
        BlockExplorerClient blockExplorerClient = new BlockExplorerClient(httpClient, gson, cfxNetworkRepository);

        tokenRepository = new TokenRepository(httpClient, cfxNetworkRepository, tokenExplorerClientType, tokenLocalSource);

        TransactionLocalSource inMemoryCache = new TransactionInMemorySource();

        transactionRepository = new TransactionRepository(cfxNetworkRepository, inMemoryCache, null, blockExplorerClient);
    }

    public static RepositoryFactory init (SharedPreferenceRepository sp, OkHttpClient httpClient, Gson gson) {
        if (sSelf == null) {
            sSelf = new RepositoryFactory(sp, httpClient, gson);
        }
        return sSelf;
    }

}
