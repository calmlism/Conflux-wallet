package pro.conflux.wallet.repository;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.entity.Transaction;
import pro.conflux.wallet.service.BlockExplorerClientType;

import io.reactivex.Maybe;
import io.reactivex.Observable;

public class TransactionRepository implements TransactionRepositoryType {

    private final CfxNetworkRepository networkRepository;
    private final TransactionLocalSource transactionLocalSource;
    private final BlockExplorerClientType blockExplorerClient;

    public TransactionRepository(
            CfxNetworkRepository networkRepository,
            TransactionLocalSource inMemoryCache,
            TransactionLocalSource inDiskCache,
            BlockExplorerClientType blockExplorerClient) {
        this.networkRepository = networkRepository;
        this.blockExplorerClient = blockExplorerClient;
        this.transactionLocalSource = inMemoryCache;

        this.networkRepository.addOnChangeDefaultNetwork(this::onNetworkChanged);
    }

    @Override
    public Observable<Transaction[]> fetchTransaction(String walletAddr, String tokenAddr) {
        return Observable.create(e -> {
            Transaction[] transactions;
            if (TextUtils.isEmpty(tokenAddr)) {
                transactions = transactionLocalSource.fetchTransaction(walletAddr).blockingGet();
            } else {
                transactions = transactionLocalSource.fetchTransaction(walletAddr, tokenAddr).blockingGet();
            }

            if (transactions != null && transactions.length > 0) {
                e.onNext(transactions);
            }
            transactions = blockExplorerClient.fetchTransactions(walletAddr, tokenAddr).blockingFirst();

            //添加过滤无效交易数据
            ArrayList<Transaction> transacList = new ArrayList<Transaction>();


            transactionLocalSource.clear();
            if (TextUtils.isEmpty(tokenAddr)) {
                transactionLocalSource.putTransactions(walletAddr, transactions);
            } else {
                transactionLocalSource.putTransactions(walletAddr, tokenAddr, transactions);
            }
            e.onNext(transactions);
            e.onComplete();
        });
    }

    @Override
    public Maybe<Transaction> findTransaction(String walletAddr, String transactionHash) {
        return fetchTransaction(walletAddr, null)
                .firstElement()
                .flatMap(transactions -> {
                    for (Transaction transaction : transactions) {
                        if (transaction.hash.equals(transactionHash)) {
                            return Maybe.just(transaction);
                        }
                    }
                    return null;
                });
    }

    private void onNetworkChanged(NetworkInfo networkInfo) {
        transactionLocalSource.clear();
    }
}
