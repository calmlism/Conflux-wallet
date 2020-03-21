package pro.conflux.wallet.repository;


import pro.conflux.wallet.entity.Transaction;

import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface TransactionRepositoryType {
	Observable<Transaction[]> fetchTransaction(String walletAddr, String token);
	Maybe<Transaction> findTransaction(String walletAddr, String transactionHash);
}
