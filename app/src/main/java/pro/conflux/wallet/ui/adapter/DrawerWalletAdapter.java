package pro.conflux.wallet.ui.adapter;

import android.content.Context;
import android.graphics.Color;

import pro.conflux.wallet.R;
import pro.conflux.wallet.base.CommonAdapter;
import pro.conflux.wallet.base.ViewHolder;
import pro.conflux.wallet.domain.CfxWallet;

import java.util.List;


public class DrawerWalletAdapter extends CommonAdapter<CfxWallet> {

    private int currentWalletPosition = 0;

    public DrawerWalletAdapter(Context context, List<CfxWallet> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setCurrentWalletPosition(int currentWalletPosition) {
        this.currentWalletPosition = currentWalletPosition;
        notifyDataSetChanged();
    }

    @Override
    public void convert(ViewHolder holder, CfxWallet wallet) {
        boolean isCurrent = wallet.getIsCurrent();
        int position = holder.getPosition();
        if (isCurrent) {
            currentWalletPosition = position;
            holder.getView(R.id.lly_wallet).setBackgroundColor(mContext.getResources().getColor(R.color.item_divider_bg_color));
        } else {
            holder.getView(R.id.lly_wallet).setBackgroundColor(Color.WHITE);
        }
        holder.setText(R.id.tv_wallet_name, wallet.getName());
    }
}
