package pro.conflux.wallet.service;

import android.text.TextUtils;
import android.util.Log;

import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.entity.Transaction;
import pro.conflux.wallet.repository.CfxNetworkRepository;
import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class BlockExplorerClient implements BlockExplorerClientType {

	private final OkHttpClient httpClient;
	private final Gson gson;
	private final CfxNetworkRepository networkRepository;

	private TransactionsApiClient transactionsApiClient;

	public BlockExplorerClient(
			OkHttpClient httpClient,
			Gson gson,
			CfxNetworkRepository networkRepository) {
		this.httpClient = httpClient;
		this.gson = gson;
		this.networkRepository = networkRepository;
		this.networkRepository.addOnChangeDefaultNetwork(this::onNetworkChanged);
		NetworkInfo networkInfo = networkRepository.getDefaultNetwork();
		onNetworkChanged(networkInfo);
	}

	private void buildApiClient(String baseUrl) {
		transactionsApiClient = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(httpClient)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
				.create(TransactionsApiClient.class);
	}

	@Override
	public Observable<Transaction[]> fetchTransactions(String address, String tokenAddr) {
		//先根据地址从新替换transactionsApiClient
		String backendUrl = networkRepository.getDefaultNetwork().backendUrl + "api/";
		buildApiClient(backendUrl);


	    if (TextUtils.isEmpty(tokenAddr)) {//判断是否为token
            return transactionsApiClient
                    .fetchTransactions(address, true)
                    .lift(apiError(gson))
                    .map(r -> r.result.list)

                    .subscribeOn(Schedulers.io());
        } else {
	    	//当是token时的查询
//            return transactionsApiClient
//                    .fetchTransactions(address, tokenAddr)
//                    .lift(apiError(gson))
//                    .map(r -> r.result.data)
//                    .subscribeOn(Schedulers.io());
			return null;
        }

	}

	private void onNetworkChanged(NetworkInfo networkInfo) {
		buildApiClient(networkInfo.backendUrl);
	}

	private static @NonNull
    <T> ApiErrorOperator<T> apiError(Gson gson) {
		return new ApiErrorOperator<>(gson);
	}


//	http://47.102.164.229:8885/api/account/0x1fc46011b87442d6a2f25e2e48101cdc9019839b/transactionList?pageNum=1&pageSize=50
	private interface TransactionsApiClient {
		//获取cfx历史记录
		@GET("transaction/list?page=1&pageSize=100&txType=all")
		Observable<Response<CfxScanResponse>> fetchTransactions(
			@Query("accountAddress") String address,
			@Query("filterContractInteraction") boolean filter
		);

	@GET("transactionList?pageNum=1&pageSize=50")
	Observable<Response<CfxScanResponse>> fetchTransactions(
			@Query("address") String address, @Query("contract") String contract);
	}


	private  final static  class CfxScanResponse{
		String code;
		String message;
		Result result;
	}

	private  final  static  class Result{

		String total;
		Transaction[] list;
	}

	private final static class ApiErrorOperator <T> implements ObservableOperator<T, Response<T>> {

		private final Gson gson;

		public ApiErrorOperator(Gson gson) {
			this.gson = gson;
		}

		@Override
		public Observer<? super Response<T>> apply(Observer<? super T> observer) throws Exception {
            return new DisposableObserver<Response<T>>() {
                @Override
                public void onNext(Response<T> response) {
                    observer.onNext(response.body());
                    observer.onComplete();
                }

                @Override
                public void onError(Throwable e) {
                    observer.onError(e);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            };
		}
	}
}
