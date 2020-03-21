package pro.conflux.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;


import pro.conflux.wallet.R;
import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.entity.Transaction;
import pro.conflux.wallet.interact.FetchWalletInteract;
import pro.conflux.wallet.repository.CfxNetworkRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class TransactionDetailViewModel extends BaseViewModel {

    private final CfxNetworkRepository cfxNetworkRepository;

    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();
    private final MutableLiveData<CfxWallet> defaultWallet = new MutableLiveData<>();

    TransactionDetailViewModel(
            CfxNetworkRepository cfxNetworkRepository,
            FetchWalletInteract findDefaultWalletInteract) {
        this.cfxNetworkRepository = cfxNetworkRepository;

        cfxNetworkRepository
                .find()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(defaultNetwork::postValue);

        disposable = findDefaultWalletInteract
                .findDefault()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(defaultWallet::postValue);
    }


    public LiveData<NetworkInfo> defaultNetwork() {
        return defaultNetwork;
    }

    public void showMoreDetails(Context context, Transaction transaction) {
        NetworkInfo networkInfo = defaultNetwork.getValue();
        if (networkInfo != null && !TextUtils.isEmpty(networkInfo.cfxscanUrl)) {
            Uri uri = Uri.parse(networkInfo.cfxscanUrl)
                    .buildUpon()
                    .appendEncodedPath("tx")
                    .appendEncodedPath(transaction.hash)
                    .build();


            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(launchBrowser);
        }
    }

    public void shareTransactionDetail(Context context, Transaction transaction) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.subject_transaction_detail));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, buildEtherscanUri(transaction).toString());
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private Uri buildEtherscanUri(Transaction transaction) {
        NetworkInfo networkInfo = defaultNetwork.getValue();
        if (networkInfo != null && !TextUtils.isEmpty(networkInfo.cfxscanUrl)) {
            return Uri.parse(networkInfo.cfxscanUrl)
                    .buildUpon()
                    .appendEncodedPath("tx")
                    .appendEncodedPath(transaction.hash)
                    .build();
        }
        return null;
    }

    public LiveData<CfxWallet> defaultWallet() {
        return defaultWallet;
    }
}
