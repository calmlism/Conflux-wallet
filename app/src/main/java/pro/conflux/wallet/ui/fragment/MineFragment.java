package pro.conflux.wallet.ui.fragment;

import android.content.Intent;
import android.view.View;
import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseFragment;
import pro.conflux.wallet.ui.activity.ContactsActivity;
import pro.conflux.wallet.ui.activity.HelpActivity;
import pro.conflux.wallet.ui.activity.MessageCenterActivity;
import pro.conflux.wallet.ui.activity.SystemSettingActivity;
import pro.conflux.wallet.ui.activity.TransactionsActivity;
import pro.conflux.wallet.ui.activity.WalletMangerActivity;

import butterknife.OnClick;


public class MineFragment extends BaseFragment {



    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
    }

    @OnClick({R.id.lly_wallet_manage, R.id.lly_contacts, R.id.lly_msg_center, R.id.ask_help
            , R.id.lly_system_setting, R.id.lly_trade_recode})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_contacts:
                intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_wallet_manage:
                intent = new Intent(getActivity(), WalletMangerActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_msg_center:
                intent = new Intent(getActivity(), MessageCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_system_setting:
                intent = new Intent(getActivity(), SystemSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_trade_recode:
                intent = new Intent(getActivity(), TransactionsActivity.class);
                startActivity(intent);
                break;
            case R.id.ask_help:
                intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);

                break;
        }
    }
}

