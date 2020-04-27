package pro.conflux.wallet.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import pro.conflux.wallet.R;

public class LoadTokenTypeSelectStandardPopupWindow extends PopupWindow implements View.OnClickListener {

    private View contentView;
    private final TextView tv20;
    private final TextView tv721;
    private final ImageView iv20;
    private final ImageView iv721;
    private int selection = 0;

    public void setOnPopupItemSelectedListener(OnPopupItemSelectedListener onPopupItemSelectedListener) {
        this.onPopupItemSelectedListener = onPopupItemSelectedListener;
    }

    private OnPopupItemSelectedListener onPopupItemSelectedListener;


    //token类型
    public LoadTokenTypeSelectStandardPopupWindow(Context context){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popup_load_tokentype_select_standard, null);
        this.setContentView(contentView);
        contentView.findViewById(R.id.rl_20).setOnClickListener(this);
        contentView.findViewById(R.id.rl_721).setOnClickListener(this);
        tv20 = (TextView)contentView.findViewById(R.id.tv_standard_20);
        tv20.setSelected(true);
        tv721 = (TextView)contentView.findViewById(R.id.tv_standard_721);
        iv20 = (ImageView)contentView.findViewById(R.id.iv_standard_20);
        iv721 = (ImageView)contentView.findViewById(R.id.iv_standard_721);
        setProperty();

    }

    private void initSelection() {
        tv20.setSelected(false);
        iv20.setVisibility(View.GONE);
        tv721.setSelected(false);
        iv721.setVisibility(View.GONE);
    }

    private void setProperty() {
        setAnimationStyle(R.style.AnimBottom);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0xff000000);
        setBackgroundDrawable(dw);
    }


    @Override
    public void onClick(View v) {
        initSelection();

        switch (v.getId()) {
            case R.id.rl_20:
                selection = 0;
                tv20.setSelected(true);
                iv20.setVisibility(View.VISIBLE);
                break;

            case R.id.rl_721:
                selection = 1;
                tv721.setSelected(true);
                iv721.setVisibility(View.VISIBLE);
                break;
        }
        dismiss();
        if (onPopupItemSelectedListener != null)
            onPopupItemSelectedListener.onSelected(selection);
    }


    public interface OnPopupItemSelectedListener {
        void onSelected(int selection);
    }
}
