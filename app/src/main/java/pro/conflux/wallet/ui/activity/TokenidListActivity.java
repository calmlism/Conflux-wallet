package pro.conflux.wallet.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseActivity;
import pro.conflux.wallet.entity.TokenInfo;
import pro.conflux.wallet.ui.adapter.AddTokenListAdapter;
import pro.conflux.wallet.ui.adapter.TokenidListAdapter;

/**
 * 地址下TokenID列表
 */
public class TokenidListActivity extends BaseActivity {

    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;

    List<TokenIDItem> mItems = new ArrayList<TokenIDItem>();

    private TokenidListAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_list_tokenid_property;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }

    public static class TokenIDItem {
        public final TokenInfo tokenInfo;
        public String tokenid;


        public TokenIDItem(TokenInfo tokenInfo,  String tokenid) {
            this.tokenInfo = tokenInfo;
            this.tokenid = tokenid;
        }
    }
}
