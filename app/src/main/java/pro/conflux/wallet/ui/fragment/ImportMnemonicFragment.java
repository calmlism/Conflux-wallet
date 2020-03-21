package pro.conflux.wallet.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseFragment;
import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.interact.CreateWalletInteract;
import pro.conflux.wallet.utils.CFXWalletUtils;
import pro.conflux.wallet.utils.LogUtils;
import pro.conflux.wallet.utils.ToastUtils;
import pro.conflux.wallet.utils.UUi;
import pro.conflux.wallet.utils.WalletDaoUtils;
import pro.conflux.wallet.view.LoadWalletSelectStandardPopupWindow;

import butterknife.BindView;
import butterknife.OnClick;


public class ImportMnemonicFragment extends BaseFragment {

    @BindView(R.id.et_mnemonic)
    EditText etMnemonic;
    @BindView(R.id.et_standard)
    EditText etStandard;
    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.et_wallet_pwd_again)
    EditText etWalletPwdAgain;
    @BindView(R.id.et_wallet_pwd_reminder_info)
    EditText etWalletPwdReminderInfo;
    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.lly_wallet_agreement)
    LinearLayout llyWalletAgreement;
    @BindView(R.id.btn_load_wallet)
    TextView btnLoadWallet;

    CreateWalletInteract createWalletInteract;

    private LoadWalletSelectStandardPopupWindow popupWindow;

    private String cfxType = CFXWalletUtils.CFX_JAXX_TYPE;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_load_wallet_by_mnemonic;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {

        createWalletInteract = new CreateWalletInteract();
    }

    @Override
    public void configViews() {

        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    String mnemonic = etMnemonic.getText().toString().trim();
                    String walletPwd = etWalletPwd.getText().toString().trim();
                    String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                    String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                    boolean verifyWalletInfo = verifyInfo(mnemonic, walletPwd, confirmPwd, pwdReminder);

                    if (verifyWalletInfo) {
                        btnLoadWallet.setEnabled(true);
                    }
                } else {
                    btnLoadWallet.setEnabled(false);
                }
            }
        });


        popupWindow = new LoadWalletSelectStandardPopupWindow(getContext());
        popupWindow.setOnPopupItemSelectedListener(new LoadWalletSelectStandardPopupWindow.OnPopupItemSelectedListener() {
            @Override
            public void onSelected(int selection) {
                switch (selection) {
                    case 0:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard);
                        cfxType = CFXWalletUtils.CFX_JAXX_TYPE;
                        etStandard.setEnabled(false);
                        break;
                    case 1:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard_ledger);
                        cfxType = CFXWalletUtils.CFX_LEDGER_TYPE;
                        etStandard.setEnabled(false);
                        break;
                    case 2:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard_custom);
                        cfxType = CFXWalletUtils.CFX_CUSTOM_TYPE;
                        etStandard.setEnabled(true);
                        etStandard.setFocusable(true);
                        etStandard.setFocusableInTouchMode(true);
                        etStandard.requestFocus();
                        break;

                }
            }
        });
    }

    public void loadSuccess(CfxWallet wallet) {

        dismissDialog();
        ToastUtils.showToast("导入钱包成功");

        getActivity().finish();

    }

    public void onError(Throwable e) {
        ToastUtils.showToast("导入钱包失败");
        dismissDialog();
    }


    @OnClick({R.id.btn_load_wallet, R.id.lly_standard_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load_wallet:
                String mnemonic = etMnemonic.getText().toString().trim();
                String walletPwd = etWalletPwd.getText().toString().trim();
                String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(mnemonic, walletPwd, confirmPwd, pwdReminder);
                if (verifyWalletInfo) {
                    showDialog(getString(R.string.loading_wallet_tip));
                    createWalletInteract.loadWalletByMnemonic(cfxType, mnemonic, walletPwd).subscribe(this::loadSuccess, this::onError);
                }
                break;
            case R.id.lly_standard_menu:
                LogUtils.i("LoadWallet", "lly_standard_menu");
                popupWindow.showAsDropDown(etStandard, 0, UUi.dip2px(10));
                break;
        }
    }

    private boolean verifyInfo(String mnemonic, String walletPwd, String confirmPwd, String pwdReminder) {
        if (TextUtils.isEmpty(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_by_mnemonic_input_tip);
            return false;
        } else if (!WalletDaoUtils.isValid(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_by_mnemonic_input_tip);
            return false;
        } else if (WalletDaoUtils.checkRepeatByMenmonic(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_already_exist);
            return false;
        }
//        else if (TextUtils.isEmpty(walletPwd)) {
//            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
//            // 同时判断强弱
//            return false;
//        }
        else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        }
        return true;
    }

}
