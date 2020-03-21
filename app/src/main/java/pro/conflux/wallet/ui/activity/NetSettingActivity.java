package pro.conflux.wallet.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pro.conflux.wallet.ChainWalletApp;
import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseActivity;
import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.repository.CfxNetworkRepository;


import butterknife.BindView;
import butterknife.OnClick;

import static pro.conflux.wallet.C.CONFLUX_MAIN_NETWORK_NAME;
import static pro.conflux.wallet.C.LOCAL_DEV_NETWORK_NAME;


public class NetSettingActivity extends BaseActivity {


    CfxNetworkRepository cfxNetworkRepository;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    TextView tvBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;

    @BindView(R.id.iv_mainnet)
    ImageView ivMainnet;

    @BindView(R.id.iv_local_dev)
    ImageView ivLocalDev;

    private String networkName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_net_setting;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_net);
        rlBtn.setVisibility(View.VISIBLE);
        tvBtn.setText(R.string.language_setting_save);
    }

    @Override
    public void initDatas() {
        cfxNetworkRepository = ChainWalletApp.repositoryFactory().cfxNetworkRepository;

        networkName = cfxNetworkRepository.getDefaultNetwork().name;

        if (CONFLUX_MAIN_NETWORK_NAME.equals(networkName)) {
            ivMainnet.setVisibility(View.VISIBLE);
            ivLocalDev.setVisibility(View.GONE);
        } else if (LOCAL_DEV_NETWORK_NAME.equals(networkName)) {
            ivMainnet.setVisibility(View.GONE);
            ivLocalDev.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_mainnet, R.id.rl_local_dev, R.id.rl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_mainnet:
                networkName = CONFLUX_MAIN_NETWORK_NAME;
                ivMainnet.setVisibility(View.VISIBLE);
                ivLocalDev.setVisibility(View.GONE);
                break;
            case R.id.rl_local_dev:
                 networkName = LOCAL_DEV_NETWORK_NAME;
                ivMainnet.setVisibility(View.GONE);

                ivLocalDev.setVisibility(View.VISIBLE);

                 break;
            case R.id.rl_btn:// 设置语言并保存

                NetworkInfo[] networks = cfxNetworkRepository.getAvailableNetworkList();
                for (NetworkInfo networkInfo : networks) {
                    if (networkInfo.name.equals(networkName)) {
                        cfxNetworkRepository.setDefaultNetworkInfo(networkInfo);
                    }
                }

                finish();
                break;
        }
    }
}
