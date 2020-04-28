package pro.conflux.wallet.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import pro.conflux.wallet.R;
import pro.conflux.wallet.base.ViewHolder;
import pro.conflux.wallet.ui.activity.TokenidListActivity;

public class TokenidListAdapter extends BaseAdapter {

    private TokenidListActivity mActivity;
    private int layoutId;
    private List<TokenidListActivity.TokenIDItem> items;

    public TokenidListAdapter(TokenidListActivity context, List<TokenidListActivity.TokenIDItem> tokenidItems , int layoutId) {

        this.mActivity = context;
        this.layoutId = layoutId;
        this.items = tokenidItems;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        pro.conflux.wallet.base.ViewHolder holder = ViewHolder.get(mActivity, convertView, parent,
                layoutId, position);
        if (position == 0) {
            holder.getView(R.id.lly_item).setBackgroundColor(mActivity.getResources().getColor(R.color.white));
        } else {
            holder.getView(R.id.lly_item).setBackgroundColor(mActivity.getResources().getColor(R.color.add_property_gray_bg_color));

        }

        holder.setText(R.id.tv_ico_name, items.get(position).tokenInfo.symbol);
        holder.setText(R.id.type,items.get(position).tokenInfo.type);



        return holder.getConvertView();
    }
}
