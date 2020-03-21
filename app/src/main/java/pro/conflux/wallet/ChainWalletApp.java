package pro.conflux.wallet;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDexApplication;

import pro.conflux.wallet.domain.DaoMaster;
import pro.conflux.wallet.domain.DaoSession;
import pro.conflux.wallet.repository.RepositoryFactory;
import pro.conflux.wallet.repository.SharedPreferenceRepository;
import pro.conflux.wallet.utils.AppFilePath;
import pro.conflux.wallet.utils.LogInterceptor;
import com.google.gson.Gson;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.realm.Realm;
import okhttp3.OkHttpClient;

public class ChainWalletApp extends MultiDexApplication {

    private static ChainWalletApp sInstance;

    private RefWatcher refWatcher;

    private DaoSession daoSession;
    private static OkHttpClient httpClient;
    public static RepositoryFactory repositoryFactory;
    public static SharedPreferenceRepository sp;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static RefWatcher getRefWatcher(Context context) {
        ChainWalletApp application = (ChainWalletApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        init();

        Realm.init(this);

        refWatcher = LeakCanary.install(this);

        AppFilePath.init(this);

    }


    public static ChainWalletApp getsInstance() {
        return sInstance;
    }

    protected void init() {

        sp = SharedPreferenceRepository.init(getApplicationContext());

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .build();

        Gson gson = new Gson();

        repositoryFactory = RepositoryFactory.init(sp, httpClient, gson);


        //创建数据库表
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "wallet", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();


    }


    public static OkHttpClient okHttpClient() {
        return httpClient;
    }

    public static RepositoryFactory repositoryFactory() {
        return  repositoryFactory;
    }


}
