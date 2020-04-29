package pro.conflux.wallet.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pro.conflux.wallet.C;
import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseActivity;

import pro.conflux.wallet.entity.TokenId;
import pro.conflux.wallet.entity.TokenInfo;

import pro.conflux.wallet.ui.adapter.TokenidListAdapter;
import pro.conflux.wallet.viewmodel.TokenIdListViewModel;
import pro.conflux.wallet.viewmodel.TokenIdListViewModelFactory;


/**
 * 地址下TokenID列表
 */
public class TokenidListActivity extends BaseActivity {

    TokenIdListViewModelFactory tokenIdListViewModelFactory;
    private TokenIdListViewModel tokenidListViewModel;

    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;

    @BindView(R.id.lv_tokenid)
    ListView tokenidList;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    List<TokenIDItem> mItems = new ArrayList<TokenIDItem>();

    private TokenidListAdapter mAdapter;

    private String currWallet;
    private String contractAddress;
    private int decimals;
    private String balance;
    private String symbol;
    private String tokenName;
    private String tokenType;

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
        Intent intent = getIntent();
        currWallet = intent.getStringExtra(C.EXTRA_ADDRESS);
        balance = intent.getStringExtra(C.EXTRA_BALANCE);
        contractAddress = intent.getStringExtra(C.EXTRA_CONTRACT_ADDRESS);
        decimals = intent.getIntExtra(C.EXTRA_DECIMALS, C.CFX_DECIMALS);
        tokenName = intent.getStringExtra(C.EXTRA_CONTRACT_NAME);
        tokenType = intent.getStringExtra(C.EXTRA_CONTRACT_TYPE);
        symbol = intent.getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.CFX_SYMBOL : symbol;

        tvTitle.setText(symbol);

        tokenIdListViewModelFactory = new TokenIdListViewModelFactory();
        tokenidListViewModel = ViewModelProviders.of(this, tokenIdListViewModelFactory)
                .get(TokenIdListViewModel.class);
        tokenidListViewModel.tokenids().observe(this, this::onTokenIDs);

        tokenidListViewModel.prepare(new TokenInfo(contractAddress,tokenName,symbol,decimals,tokenType));
    }


    private void onTokenIDs(TokenId[] tokenids) {

        mAdapter = new TokenidListAdapter(this, mItems, R.layout.list_item_tokenid_property);
        tokenidList.setAdapter(mAdapter);

    }

    @Override
    public void configViews() {

    }

    public static class TokenIDItem {
        public final TokenInfo tokenInfo;
        public String tokenid;
        public int index;


        public TokenIDItem(TokenInfo tokenInfo,  String tokenid , int index) {
            this.tokenInfo = tokenInfo;
            this.tokenid = tokenid;
            this.index = index;
        }
    }
}
