package pro.conflux.wallet.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import pro.conflux.wallet.R;
import pro.conflux.wallet.base.BaseActivity;
import pro.conflux.wallet.domain.CfxWallet;
import pro.conflux.wallet.entity.NetworkInfo;
import pro.conflux.wallet.entity.Transaction;
import pro.conflux.wallet.utils.BalanceUtils;
import pro.conflux.wallet.viewmodel.TransactionDetailViewModel;
import pro.conflux.wallet.viewmodel.TransactionDetailViewModelFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;

import static pro.conflux.wallet.C.Key.TRANSACTION;


public class TransactionDetailActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.amount)
    TextView amount;

    @BindView(R.id.from)
    TextView tvFrom;

    @BindView(R.id.to)
    TextView tvTo;

    @BindView(R.id.gas_fee)
    TextView tvGasFee;

    @BindView(R.id.txn_hash)
    TextView tvTxHash;

    @BindView(R.id.txn_time)
    TextView tvTxTime;
    @BindView(R.id.block_number)
    TextView tvBlockNumber;

    private Transaction transaction;

    TransactionDetailViewModelFactory transactionDetailViewModelFactory;
    private TransactionDetailViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.subject_transaction_detail);
    }

    @Override
    public void initDatas() {

        transaction = (Transaction)getIntent().getParcelableExtra(TRANSACTION);

        if (transaction == null) {
            finish();
            return;
        }

//        BigInteger gasFee = new BigInteger(transaction.gasUsed).multiply(new BigInteger(transaction.gasPrice));
        BigInteger gasFee = new BigInteger(transaction.gas).multiply(new BigInteger(transaction.gasPrice));//这里有问题

        tvFrom.setText(transaction.from);
        tvTo.setText(transaction.to);
        tvGasFee.setText(BalanceUtils.weiToCfx(gasFee).toPlainString());
        tvTxHash.setText(transaction.hash);
        tvTxTime.setText(getDate(transaction.timeStamp));
//        tvBlockNumber.setText(transaction.blockNumber);//缺少数据

        transactionDetailViewModelFactory  = new TransactionDetailViewModelFactory();

        viewModel = ViewModelProviders.of(this, transactionDetailViewModelFactory)
                .get(TransactionDetailViewModel.class);
        viewModel.defaultNetwork().observe(this, this::onDefaultNetwork);
        viewModel.defaultWallet().observe(this, this::onDefaultWallet);

    }


    private void onDefaultNetwork(NetworkInfo networkInfo) {
        findViewById(R.id.more_detail).setVisibility(
                TextUtils.isEmpty(networkInfo.cfxscanUrl) ? View.GONE : View.VISIBLE);
    }

    private void onDefaultWallet(CfxWallet wallet) {
        boolean isSent = transaction.from.toLowerCase().equals(wallet.getAddress().toLowerCase());
        String rawValue;
        String symbol;
        long decimals = 18;
        NetworkInfo networkInfo = viewModel.defaultNetwork().getValue();
        if (transaction.contractCreated == null || transaction.contractCreated == "null") {
            rawValue = transaction.value;
            symbol = networkInfo == null ? "" : networkInfo.symbol;
        } else {
//            rawValue = transaction.operations[0].value;
//            decimals = transaction.operations[0].contract.decimals;
//            symbol = transaction.operations[0].contract.symbol;
            //这里有问题
            rawValue = "";
            decimals = 18;
            symbol = "";

        }

        amount.setTextColor(ContextCompat.getColor(this, isSent ? R.color.red : R.color.green));
        if (rawValue.equals("0")) {
            rawValue = "0 " + symbol;
        } else {
            rawValue = (isSent ? "-" : "+") + getScaledValue(rawValue, decimals) + " " + symbol;
        }
        amount.setText(rawValue);
    }


    private String getDate(long timeStampInSec) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStampInSec * 1000);
        return DateFormat.getLongDateFormat(this).format(cal.getTime());
    }


    private String getScaledValue(String valueStr, long decimals) {
        // Perform decimal conversion
        BigDecimal value = new BigDecimal(valueStr);
        value = value.divide(new BigDecimal(Math.pow(10, decimals)));
        int scale = 3 - value.precision() + value.scale();
        return value.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    @Override
    public void configViews() {
        findViewById(R.id.more_detail).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        viewModel.showMoreDetails(v.getContext(), transaction);
    }

}
