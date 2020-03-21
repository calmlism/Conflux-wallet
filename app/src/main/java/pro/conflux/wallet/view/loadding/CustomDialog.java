
package pro.conflux.wallet.view.loadding;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pro.conflux.wallet.R;


public class CustomDialog extends Dialog {

    private TextView tvProgress;
    private LoadingView loadingView;

    public CustomDialog(Context context) {
        this(context, 0);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private View inflateView(Context ct) {
        View v = View.inflate(ct, R.layout.common_progress_view, null);
        loadingView = (LoadingView)v.findViewById(R.id.loadingView);
        tvProgress = (TextView)v.findViewById(R.id.tv_progress);
        return v;
    }

    public static CustomDialog instance(Context context) {
        CustomDialog dialog = new CustomDialog(context, R.style.loading_dialog);
        View v = dialog.inflateView(context);
        dialog.setContentView(v,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        return dialog;

    }

    public void setTvProgress(String progressTip) {
        tvProgress.setText(progressTip);
    }
}

