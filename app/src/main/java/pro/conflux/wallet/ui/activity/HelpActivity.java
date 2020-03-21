package pro.conflux.wallet.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseActivity;


public class HelpActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_help;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mine_help_center);
    }

    @Override
    public void initDatas() {
    }

    @Override
    public void configViews() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}
