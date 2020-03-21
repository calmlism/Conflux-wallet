package pro.conflux.wallet.ui.adapter;

import android.content.Context;

import pro.conflux.wallet.R;
import pro.conflux.wallet.base.CommonAdapter;
import pro.conflux.wallet.base.ViewHolder;
import pro.conflux.wallet.domain.CfxWallet;

import java.util.List;


public class WalletManagerAdapter extends CommonAdapter<CfxWallet> {
    public WalletManagerAdapter(Context context, List<CfxWallet> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, CfxWallet wallet) {
        holder.setText(R.id.tv_wallet_name,wallet.getName());
        holder.setText(R.id.tv_wallet_address,wallet.getAddress());
    }
}
