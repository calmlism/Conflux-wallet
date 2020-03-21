package pro.conflux.wallet.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseActivity;
import pro.conflux.wallet.utils.GlideImageLoader;
import pro.conflux.wallet.utils.ToastUtils;


import org.cfx.utils.Convert;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Single;

import static pro.conflux.wallet.C.EXTRA_ADDRESS;
import static pro.conflux.wallet.C.EXTRA_CONTRACT_ADDRESS;
import static pro.conflux.wallet.C.EXTRA_DECIMALS;

public class GatheringQRCodeActivity extends BaseActivity {

    @BindView(R.id.iv_gathering_qrcode)
    ImageView ivGatheringQrcode;

    @BindView(R.id.btn_copy_address)
    Button btnCopyAddress;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;

    @BindView(R.id.et_gathering_money)
    EditText etGatheringMoney;

    private String walletAddress;
    private String contractAddress;
    private int decimals;
    private String qRStr;

    @Override
    public int getLayoutId() {
        return R.layout.activity_gathering_qrcode;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        walletAddress = intent.getStringExtra(EXTRA_ADDRESS);
        contractAddress = intent.getStringExtra(EXTRA_CONTRACT_ADDRESS);
        decimals = intent.getIntExtra(EXTRA_DECIMALS, 18);

        tvWalletAddress.setText(walletAddress);
        initAddressQRCode();
    }

    // 参考
    private void initAddressQRCode() {

        qRStr = "conflux:" + walletAddress + "?decimal=" + decimals;
        if (!TextUtils.isEmpty(contractAddress)) {
            qRStr += "&contractAddress=" + contractAddress;
        }

        Single.fromCallable(
                () -> {
                    return QRCodeEncoder.syncEncodeQRCode(qRStr, BGAQRCodeUtil.dp2px(GatheringQRCodeActivity.this, 270), Color.parseColor("#000000"));
                }
        ).subscribe( bitmap ->  GlideImageLoader.loadBmpImage(ivGatheringQrcode, bitmap, -1) );

    }

    @Override
    public void configViews() {
        etGatheringMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Single.fromCallable(
                        () -> {
                            String value = etGatheringMoney.getText().toString().trim();

                            if (TextUtils.isEmpty(value)) {
                                return QRCodeEncoder.syncEncodeQRCode(qRStr, BGAQRCodeUtil.dp2px(GatheringQRCodeActivity.this, 270), Color.parseColor("#000000"));
                            } else {
                                String weiValue = Convert.toWei(value, Convert.Unit.ETHER).toString();
                                return QRCodeEncoder.syncEncodeQRCode(qRStr + "&value=" + weiValue, BGAQRCodeUtil.dp2px(GatheringQRCodeActivity.this, 270), Color.parseColor("#000000"));
                            }
                        }
                ).subscribe( bitmap ->  GlideImageLoader.loadBmpImage(ivGatheringQrcode, bitmap, -1) );

            }
        });
    }

    @OnClick({R.id.lly_back, R.id.btn_copy_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_back:
                finish();
                break;
            case R.id.btn_copy_address:
                copyWalletAddress();
                break;
        }
    }

    private void copyWalletAddress() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        if (cm != null) {
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", walletAddress);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
        }
        ToastUtils.showToast(R.string.gathering_qrcode_copy_success);
        btnCopyAddress.setText(R.string.gathering_qrcode_copy_success);
    }
}
