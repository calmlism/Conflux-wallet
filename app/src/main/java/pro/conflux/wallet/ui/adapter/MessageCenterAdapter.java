package pro.conflux.wallet.ui.adapter;

import android.content.Context;

import pro.conflux.wallet.base.CommonAdapter;
import pro.conflux.wallet.base.ViewHolder;

import java.util.List;


public class MessageCenterAdapter extends CommonAdapter<String> {
    public MessageCenterAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, String s) {

    }
}
