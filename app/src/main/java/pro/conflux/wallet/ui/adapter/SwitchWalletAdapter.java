package pro.conflux.wallet.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import pro.conflux.wallet.R;
import pro.conflux.wallet.base.CommonAdapter;
import pro.conflux.wallet.base.ViewHolder;

import java.util.List;


public class SwitchWalletAdapter extends CommonAdapter<String> {


    private int selection = 0;

    public SwitchWalletAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, String s) {
        ImageView ivCheck = holder.getView(R.id.iv_check);
        if (holder.getPosition() == selection) {
            ivCheck.setVisibility(View.VISIBLE);
        } else {
            ivCheck.setVisibility(View.GONE);
        }
    }

    public void setSelection(int selection) {
        this.selection = selection;
        notifyDataSetChanged();
    }
}
