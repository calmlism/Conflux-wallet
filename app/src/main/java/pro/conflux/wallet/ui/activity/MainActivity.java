package pro.conflux.wallet.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pro.conflux.wallet.ChainWalletApp;
import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseActivity;
import pro.conflux.wallet.ui.adapter.HomePagerAdapter;
import pro.conflux.wallet.ui.fragment.DappFragment;
import pro.conflux.wallet.ui.fragment.MineFragment;
import pro.conflux.wallet.ui.fragment.PropertyFragment;
import pro.conflux.wallet.utils.ToastUtils;
import pro.conflux.wallet.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import butterknife.BindView;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.vp_home)
    NoScrollViewPager vpHome;
    @BindView(R.id.iv_mall)
    ImageView ivMall;
    @BindView(R.id.tv_mall)
    TextView tvMall;
    @BindView(R.id.lly_mall)
    LinearLayout llyMall;

    @BindView(R.id.iv_mine)
    ImageView ivMine;
    @BindView(R.id.tv_mine)
    TextView tvMine;
    @BindView(R.id.lly_mine)
    LinearLayout llyMine;

    @BindView(R.id.iv_dapp)
    ImageView ivDapp;
    @BindView(R.id.tv_dapp)
    TextView tvDapp;
    @BindView(R.id.lly_dapp)
    LinearLayout llyDapp;


    private HomePagerAdapter homePagerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        int language = ChainWalletApp.sp.getCurrencyUnit();
        Resources resources = getResources();

        Configuration config = resources.getConfiguration();

        if (language == 1){
            config.locale = Locale.ENGLISH;
        }else{
            config.locale = Locale.CHINA;
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    public void configViews() {
        ivMall.setSelected(true);
        tvMall.setSelected(true);


        llyMall.setOnClickListener(this);
        llyMine.setOnClickListener(this);
        llyDapp.setOnClickListener(this);

        vpHome.setOffscreenPageLimit(3);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new PropertyFragment());
        fragmentList.add(new DappFragment());//dapp
        fragmentList.add(new MineFragment());
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), fragmentList);
        vpHome.setAdapter(homePagerAdapter);
        vpHome.setCurrentItem(0, false);
    }

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showToast(getString(R.string.exit_tips));
                return true;
            } else {
                finish(); // 退出
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View v) {
        setAllUnselected();
        switch (v.getId()) {
            case R.id.lly_mall:
                ivMall.setSelected(true);
                tvMall.setSelected(true);
                vpHome.setCurrentItem(0, false);
                break;
            case R.id.lly_dapp:// DAPP
                ivDapp.setSelected(true);
                tvDapp.setSelected(true);
                vpHome.setCurrentItem(1, false);
                break;
            case R.id.lly_mine:// 我的
                ivMine.setSelected(true);
                tvMine.setSelected(true);
                vpHome.setCurrentItem(2, false);
                break;
        }
    }

    private void setAllUnselected() {
        ivMall.setSelected(false);
        tvMall.setSelected(false);
        ivMine.setSelected(false);
        tvMine.setSelected(false);
        ivDapp.setSelected(false);
        tvDapp.setSelected(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
