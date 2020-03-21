package pro.conflux.wallet.service;

import pro.conflux.wallet.entity.ApiErrorException;
import pro.conflux.wallet.entity.ErrorEnvelope;
import pro.conflux.wallet.entity.TokenInfo;
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
import retrofit2.http.Path;

import static pro.conflux.wallet.C.ErrorCode.UNKNOWN;

public class CfxplorerTokenService implements TokenExplorerClientType {
    private static final String CFXPLORER_API_URL = "https://api.ethplorer.io";

    private CfxplorerApiClient cfxplorerApiClient;

    public CfxplorerTokenService(
            OkHttpClient httpClient,
            Gson gson) {
        cfxplorerApiClient = new Retrofit.Builder()
                .baseUrl(CFXPLORER_API_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(CfxplorerApiClient.class);
    }

    @Override
    public Observable<TokenInfo[]> fetch(String walletAddress) {
        return cfxplorerApiClient.fetchTokens(walletAddress)
                .lift(apiError())
                .map(r -> {
                    if (r.tokens == null) {
                        return new TokenInfo[0];
                    } else {
                        int len = r.tokens.length;
                        TokenInfo[] result = new TokenInfo[len];
                        for (int i = 0; i < len; i++) {
                            result[i] = r.tokens[i].tokenInfo;
                        }
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private static @NonNull
    ApiErrorOperator apiError() {
        return new ApiErrorOperator();
    }

    public interface CfxplorerApiClient {
        @GET("/getAddressInfo/{address}?apiKey=freekey")
        Observable<Response<CfxplorerResponse>> fetchTokens(@Path("address") String address);
    }

    private static class Token {
        TokenInfo tokenInfo;
    }

    private static class CfxplorerResponse {
        Token[] tokens;
        CfxplorerError error;
    }

    private static class CfxplorerError {
        int code;
        String message;
    }

    private final static class ApiErrorOperator implements ObservableOperator<CfxplorerResponse, Response<CfxplorerResponse>> {

        @Override
        public Observer<? super Response<CfxplorerResponse>> apply(Observer<? super CfxplorerResponse> observer) throws Exception {
            return new DisposableObserver<Response<CfxplorerResponse>>() {
                @Override
                public void onNext(Response<CfxplorerResponse> response) {
                    CfxplorerResponse body = response.body();
                    if (body != null && body.error == null) {
                        observer.onNext(body);
                        observer.onComplete();
                    } else {
                        if (body != null) {
                            observer.onError(new ApiErrorException(new ErrorEnvelope(body.error.code, body.error.message)));
                        } else {
                            observer.onError(new ApiErrorException(new ErrorEnvelope(UNKNOWN, "Service not available")));
                        }
                    }
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
