package pro.conflux.wallet.service;


import pro.conflux.wallet.entity.Transaction;

import io.reactivex.Observable;

public interface BlockExplorerClientType {
    Observable<Transaction[]> fetchTransactions(String forAddress, String forToken);
}
