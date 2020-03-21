package pro.conflux.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pro.conflux.wallet.domain.CfxWallet;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.conflux.wallet.interact.FetchWalletInteract;

public class SplashActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FetchWalletInteract().fetch().observeOn(AndroidSchedulers.mainThread()).delay(2, TimeUnit.SECONDS).subscribe(
                this::onWalltes, this::onError
        );

    }

    public void onWalltes(List<CfxWallet> cfxWallets) {

        if (cfxWallets.size() == 0) {
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SplashActivity.this.startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SplashActivity.this.startActivity(intent);
        }


    }


    public void onError(Throwable throwable) {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SplashActivity.this.startActivity(intent);
    }

}
