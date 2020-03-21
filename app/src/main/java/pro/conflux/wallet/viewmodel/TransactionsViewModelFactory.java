package pro.conflux.wallet.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import pro.conflux.wallet.ChainWalletApp;
import pro.conflux.wallet.interact.FetchTransactionsInteract;
import pro.conflux.wallet.interact.FetchWalletInteract;
import pro.conflux.wallet.repository.CfxNetworkRepository;
import pro.conflux.wallet.repository.RepositoryFactory;


public class TransactionsViewModelFactory implements ViewModelProvider.Factory {

    private final CfxNetworkRepository cfxNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;
    private final FetchTransactionsInteract fetchTransactionsInteract;


    public TransactionsViewModelFactory() {

        RepositoryFactory rf = ChainWalletApp.repositoryFactory();
        this.cfxNetworkRepository = rf.cfxNetworkRepository;
        this.findDefaultWalletInteract = new FetchWalletInteract();
        this.fetchTransactionsInteract = new FetchTransactionsInteract(rf.transactionRepository);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TransactionsViewModel(
                cfxNetworkRepository,
                findDefaultWalletInteract,
                fetchTransactionsInteract);
    }
}
