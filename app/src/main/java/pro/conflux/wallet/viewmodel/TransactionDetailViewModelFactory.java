package pro.conflux.wallet.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import pro.conflux.wallet.ChainWalletApp;
import pro.conflux.wallet.interact.FetchWalletInteract;
import pro.conflux.wallet.repository.CfxNetworkRepository;
import pro.conflux.wallet.repository.RepositoryFactory;


public class TransactionDetailViewModelFactory implements ViewModelProvider.Factory {

    private final CfxNetworkRepository CfxNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;

    public TransactionDetailViewModelFactory() {
        RepositoryFactory rf = ChainWalletApp.repositoryFactory();

        this.CfxNetworkRepository = rf.cfxNetworkRepository;
        this.findDefaultWalletInteract = new FetchWalletInteract();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TransactionDetailViewModel(
                CfxNetworkRepository,
                findDefaultWalletInteract
                );
    }
}
