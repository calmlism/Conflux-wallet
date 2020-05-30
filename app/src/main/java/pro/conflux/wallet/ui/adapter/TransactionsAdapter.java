package pro.conflux.wallet.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import pro.conflux.wallet.R;
import pro.conflux.wallet.entity.Logs;
import pro.conflux.wallet.entity.Transaction;
import pro.conflux.wallet.entity.TransactionOperation;
import pro.conflux.wallet.utils.LogUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static pro.conflux.wallet.C.CFX_DECIMALS;

public class TransactionsAdapter  extends BaseQuickAdapter<Transaction, BaseViewHolder> {

    private final List<Transaction> items = new ArrayList<>();

    private static final int SIGNIFICANT_FIGURES = 3;

    private String symbol;
    private String defaultAddress;


    public TransactionsAdapter(int layoutResId, @Nullable List<Transaction> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, Transaction transaction) {
//        LogUtils.d(TAG, "convert: helper:" + helper + ", transaction:" + transaction);

        boolean isSent = transaction.from.toLowerCase().equals(defaultAddress.toLowerCase());
        boolean isCreateContract = TextUtils.isEmpty(transaction.to);

        helper.setTextColor(R.id.type, ContextCompat.getColor(mContext, transaction.status != 0 ? R.color.red : R.color.black));

        if (isSent) {
            if (isCreateContract) {

                helper.setText(R.id.type, R.string.create);
            } else {
                helper.setText(R.id.type, R.string.sent);
            }
        }
        else {
            helper.setText(R.id.type, R.string.received);
        }


        if (isSent) {
            helper.setImageResource(R.id.type_icon, R.drawable.ic_arrow_upward_black_24dp);
        } else {
            helper.setImageResource(R.id.type_icon, R.drawable.ic_arrow_downward_black_24dp);
        }

        helper.setTextColor(R.id.address, ContextCompat.getColor(mContext, transaction.status != 0 ? R.color.red : R.color.grey));

        if (isCreateContract) {
            //这里有问题
            helper.setText(R.id.address, transaction.contractCreated);
        } else {
            helper.setText(R.id.address, isSent ? transaction.to : transaction.from);
        }


        helper.setTextColor(R.id.value, ContextCompat.getColor(mContext, isSent ? R.color.red : R.color.green));


        String valueStr = "";


        // If operations include token transfer, display token transfer instead
//        Logs operation = transaction.logs == null
//                || transaction.logs.length == 0 ? null : transaction.logs[0];

        if (transaction.contractCreated == null || transaction.contractCreated == "null") {  // default to cfx transaction
            valueStr = transaction.value;

            if (valueStr.equals("0")) {
                valueStr = "0 " + symbol;
            } else {
                valueStr = (isSent ? "-" : "+") +  getScaledValue(valueStr, CFX_DECIMALS) + " " + symbol;
            }

        } else {
//            valueStr = operation.value;//这里有问题

            if (valueStr.equals("0")) {
                valueStr = "0 " + symbol;
            } else {
//                valueStr = (isSent ? "-" : "+") +  getScaledValue(valueStr, operation.contract.decimals) + " " + symbol;//这里有问题
            }
        }


        helper.setText(R.id.value, valueStr);


    }


    private String getScaledValue(String valueStr, long decimals) {

        //十六进制转十进制
        String s = new BigInteger(valueStr.substring(2), 16).toString();
        // Perform decimal conversion
        BigDecimal value = new BigDecimal(s);
        value = value.divide(new BigDecimal(Math.pow(10, decimals)));
        int scale = SIGNIFICANT_FIGURES - value.precision() + value.scale();
        return value.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }


    public void addTransactions(List<Transaction> transactions, String walletAddress, String symbol) {
        setNewData(transactions);
        this.defaultAddress = walletAddress;
        this.symbol = symbol;
    }


}
