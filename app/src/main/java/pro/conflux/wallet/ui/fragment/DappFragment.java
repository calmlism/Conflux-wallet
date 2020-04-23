package pro.conflux.wallet.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import butterknife.BindView;
import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseFragment;
import pro.conflux.wallet.ui.activity.BountyViewActivity;
import pro.conflux.wallet.ui.activity.ForumViewActivity;
import pro.conflux.wallet.ui.activity.ScanViewActivity;
import pro.conflux.wallet.ui.adapter.PictureAdapter;

/**
 * Created by Conflux on
 */
public class DappFragment extends BaseFragment {

    @BindView(R.id.dapp_gridview)
    GridView gridView;

    //图片的文字标题
    private String[] titles = new String[]
            { "Bounty", "Forum", "Scan"};

    //图片ID数组
    private int[] images = new int[]{
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher
    };

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_dapp;
    }

    @Override
    public void attachView() {
        PictureAdapter adapter = new PictureAdapter(titles, images, this.getContext());
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {

                if(position == 0){
                    Intent intent = new Intent(getActivity(), BountyViewActivity.class);
                    startActivity(intent);
                }
                if(position == 1){
                    Intent intent = new Intent(getActivity(), ForumViewActivity.class);
                    startActivity(intent);
                }
                if(position == 2){
                    Intent intent = new Intent(getActivity(), ScanViewActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public void initDatas() {


    }

    @Override
    public void configViews() {

    }
}
