package pro.conflux.wallet.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseActivity;
import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.interact.FetchWalletInteract;
import pro.conflux.wallet.ui.adapter.WalletManagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class WalletMangerActivity extends BaseActivity {
    private static final int CREATE_WALLET_REQUEST = 1101;
    private static final int WALLET_DETAIL_REQUEST = 1102;
    @BindView(R.id.lv_wallet)
    ListView lvWallet;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private List<CfxWallet> walletList;
    private WalletManagerAdapter walletManagerAdapter;
    private FetchWalletInteract fetchWalletInteract;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_manager;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mine_wallet_manage);
    }

    @Override
    public void initDatas() {
        walletList = new ArrayList<>();
        walletManagerAdapter = new WalletManagerAdapter(this, walletList, R.layout.list_item_wallet_manager);
        lvWallet.setAdapter(walletManagerAdapter);


        fetchWalletInteract = new FetchWalletInteract();

        fetchWalletInteract.fetch().subscribe(
                this::showWalletList
        );

    }

    @Override
    public void configViews() {
        lvWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, WalletDetailActivity.class);

                CfxWallet wallet = walletList.get(position);
                intent.putExtra("walletId", wallet.getId());
                intent.putExtra("walletPwd", wallet.getPassword());
                intent.putExtra("walletAddress", wallet.getAddress());
                intent.putExtra("walletName", wallet.getName());
                intent.putExtra("walletMnemonic", wallet.getMnemonic());
                intent.putExtra("walletIsBackup", wallet.getIsBackup());
                intent.putExtra("fromList", true);

                startActivityForResult(intent, WALLET_DETAIL_REQUEST);
            }
        });
    }

    @OnClick({R.id.lly_create_wallet, R.id.lly_load_wallet})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_create_wallet:
                intent = new Intent(this, CreateWalletActivity.class);
                startActivityForResult(intent, CREATE_WALLET_REQUEST);
                break;
            case R.id.lly_load_wallet:
                intent = new Intent(this, ImportWalletActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_WALLET_REQUEST) {
            if (data != null) {
                finish();

            }
        } else if (requestCode == WALLET_DETAIL_REQUEST) {
            if (data != null) {
                fetchWalletInteract.fetch().subscribe(
                        this::showWalletList
                );
            }
        }
    }

    public void showWalletList(List<CfxWallet> cfxWallets) {
        walletList.clear();
        walletList.addAll(cfxWallets);
        walletManagerAdapter.notifyDataSetChanged();
    }


    public void onError(Throwable throwable) {

    }
}
