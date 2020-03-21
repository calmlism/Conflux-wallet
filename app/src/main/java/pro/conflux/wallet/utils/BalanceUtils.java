package pro.conflux.wallet.utils;

import org.cfx.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class BalanceUtils {
    private static String weiInCFX  = "1000000000000000000";

    public static BigDecimal weiToCfx(BigInteger wei) {
        return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
    }

    public static String weiToCfx(BigInteger wei, int sigFig) throws Exception {
        BigDecimal cfx = weiToCfx(wei);
        int scale = sigFig - cfx.precision() + cfx.scale();
        BigDecimal cfx_scaled = cfx.setScale(scale, RoundingMode.HALF_UP);
        return cfx_scaled.toString();
    }

    public static String cfxToUsd(String priceUsd, String cfxBalance) {
        BigDecimal usd = new BigDecimal(cfxBalance).multiply(new BigDecimal(priceUsd));
        usd = usd.setScale(2, RoundingMode.CEILING);
        return usd.toString();
    }

    public static String CfxToWei(String cfx) throws Exception {
        BigDecimal wei = new BigDecimal(cfx).multiply(new BigDecimal(weiInCFX));
        return wei.toBigInteger().toString();
    }

    public static BigDecimal weiToGweiBI(BigInteger wei) {
        return Convert.fromWei(new BigDecimal(wei), Convert.Unit.GWEI);
    }

    public static String weiToGwei(BigInteger wei) {
        return Convert.fromWei(new BigDecimal(wei), Convert.Unit.GWEI).toPlainString();
    }

    public static BigInteger gweiToWei(BigDecimal gwei) {
        return Convert.toWei(gwei, Convert.Unit.GWEI).toBigInteger();
    }

    public static BigDecimal tokenToWei(BigDecimal number, int decimals) {
        BigDecimal weiFactor = BigDecimal.TEN.pow(decimals);
        return number.multiply(weiFactor);
    }

    /**
     * Base - taken to mean default unit for a currency e.g. cfx, DOLLARS
     * Subunit - taken to mean subdivision of base e.g. WEI, CENTS
     *
     * @param baseAmountStr - decimal amonut in base unit of a given currency
     * @param decimals - decimal places used to convert to subunits
     * @return amount in subunits
     */
    public static BigInteger baseToSubunit(String baseAmountStr, int decimals) {
        assert(decimals >= 0);
        BigDecimal baseAmount = new BigDecimal(baseAmountStr);
        BigDecimal subunitAmount = baseAmount.multiply(BigDecimal.valueOf(10).pow(decimals));
        try {
            return subunitAmount.toBigIntegerExact();
        } catch (ArithmeticException ex) {
            assert(false);
            return subunitAmount.toBigInteger();
        }
    }

    /**
     * @param subunitAmount - amouunt in subunits
     * @param decimals - decimal places used to convert subunits to base
     * @return amount in base units
     */
    public static BigDecimal subunitToBase(BigInteger subunitAmount, int decimals) {
        assert(decimals >= 0);
        return new BigDecimal(subunitAmount).divide(BigDecimal.valueOf(10).pow(decimals));
    }
}
