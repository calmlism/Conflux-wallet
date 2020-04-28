package pro.conflux.wallet.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseActivity;
import pro.conflux.wallet.entity.Token;
import pro.conflux.wallet.entity.TokenInfo;
import pro.conflux.wallet.ui.adapter.AddTokenListAdapter;
import pro.conflux.wallet.utils.LogUtils;
import pro.conflux.wallet.viewmodel.AddTokenViewModel;
import pro.conflux.wallet.viewmodel.AddTokenViewModelFactory;
import pro.conflux.wallet.viewmodel.TokensViewModel;
import pro.conflux.wallet.viewmodel.TokensViewModelFactory;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.OnClick;


public class AddTokenActivity extends BaseActivity {

    TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;

    protected AddTokenViewModelFactory addTokenViewModelFactory;
    private AddTokenViewModel addTokenViewModel;

    private static final int SEARCH_ICO_TOKEN_REQUEST = 1000;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv_ico)
    ListView tokenList;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;


    List<TokenItem> mItems = new ArrayList<TokenItem>();

    private AddTokenListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

        @Override
    public int getLayoutId() {
        return R.layout.activity_add_new_property;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.add_new_property_title);
        rlBtn.setVisibility(View.VISIBLE);
    }

    public static class TokenItem {
        public final TokenInfo tokenInfo;
        public boolean added;
        public int iconId;


        public TokenItem(TokenInfo tokenInfo, boolean added, int id) {
            this.tokenInfo = tokenInfo;
            this.added = added;
            this.iconId = id;
        }
    }

    @Override
    public void initDatas() {

        // TODO
        mItems.add(new TokenItem(new TokenInfo("", "CFX", "CFX", 18,"BASE"), true, R.mipmap.wallet_logo_demo));
        mItems.add(new TokenItem(new TokenInfo("0x88a8f9b1835ae66b6f1da3c930b7d11220bebf78", "Fans Coin", "FC", 18,"CRC20"), false, R.mipmap.fanscoin));

        tokensViewModelFactory = new TokensViewModelFactory();
        tokensViewModel = ViewModelProviders.of(this, tokensViewModelFactory)
                .get(TokensViewModel.class);
        tokensViewModel.tokens().observe(this, this::onTokens);

        tokensViewModel.prepare();

        addTokenViewModelFactory = new AddTokenViewModelFactory();
        addTokenViewModel = ViewModelProviders.of(this, addTokenViewModelFactory)
                .get(AddTokenViewModel.class);


    }

    private void onTokens(Token[] tokens) {

        for (TokenItem item : mItems) {
            for (Token token: tokens) {
                if (item.tokenInfo.address.equals(token.tokenInfo.address)) {
                    item.added = true;
                }
            }
        }


        mAdapter = new AddTokenListAdapter(this, mItems, R.layout.list_item_add_ico_property);
        tokenList.setAdapter(mAdapter);
    }

    public void onCheckedChanged(CompoundButton btn, boolean checked){
        TokenItem info = (TokenItem) btn.getTag();
        info.added = checked;
//        LogUtils.d(info.toString() + ", checked:" + checked);

        if (checked) {
            addTokenViewModel.save(info.tokenInfo.address, info.tokenInfo.symbol, info.tokenInfo.decimals,info.tokenInfo.type);
        }


    };

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_btn})
    public void onClick(View view) {
        if (view.getId() == R.id.rl_btn) {
            Intent intent = new Intent(this, AddCustomTokenActivity.class);
            startActivityForResult(intent, SEARCH_ICO_TOKEN_REQUEST);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
