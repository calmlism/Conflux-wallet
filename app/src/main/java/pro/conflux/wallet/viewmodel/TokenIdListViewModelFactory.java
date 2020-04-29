package pro.conflux.wallet.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import pro.conflux.wallet.ChainWalletApp;
import pro.conflux.wallet.interact.FetchTokensInteract;
import pro.conflux.wallet.interact.FetchWalletInteract;
import pro.conflux.wallet.repository.CfxNetworkRepository;
import pro.conflux.wallet.repository.RepositoryFactory;

public class TokenIdListViewModelFactory implements ViewModelProvider.Factory {

    private final FetchTokensInteract fetchTokensInteract;
    private final CfxNetworkRepository cfxNetworkRepository;

    public TokenIdListViewModelFactory() {
        RepositoryFactory rf = ChainWalletApp.repositoryFactory();
        fetchTokensInteract = new FetchTokensInteract(rf.tokenRepository);
        cfxNetworkRepository = rf.cfxNetworkRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TokensViewModel(
                cfxNetworkRepository,
                new FetchWalletInteract(),
                fetchTokensInteract
        );
    }
}
