package im.bclpbkiauv.ui.utils.number;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.messenger.FileLog;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MoneyUtil extends NumberUtil {
    public static final String TAG = MoneyUtil.class.getName();

    public static double formatToDouble(double money, int countAfterPoint) {
        return formatToDouble(String.valueOf(money), countAfterPoint);
    }

    public static double formatToDouble(String money, int countAfterPoint) {
        return formatToDoubleDown(money, countAfterPoint);
    }

    public static double formatToDoubleHalfUp(double money, int countAfterPoint) {
        return formatToDoubleHalfUp(String.valueOf(money), countAfterPoint);
    }

    public static double formatToDoubleHalfUp(String money, int countAfterPoint) {
        return formatToDouble(money, countAfterPoint, RoundingMode.HALF_UP);
    }

    public static double formatToDoubleHalfDown(double money, int countAfterPoint) {
        return formatToDoubleHalfDown(String.valueOf(money), countAfterPoint);
    }

    public static double formatToDoubleHalfDown(String money, int countAfterPoint) {
        return formatToDouble(money, countAfterPoint, RoundingMode.HALF_DOWN);
    }

    public static double formatToDoubleUp(double money, int countAfterPoint) {
        return formatToDoubleUp(String.valueOf(money), countAfterPoint);
    }

    public static double formatToDoubleUp(String money, int countAfterPoint) {
        return formatToDouble(money, countAfterPoint, RoundingMode.UP);
    }

    public static double formatToDoubleDown(double money, int countAfterPoint) {
        return formatToDoubleDown(String.valueOf(money), countAfterPoint);
    }

    public static double formatToDoubleDown(String money, int countAfterPoint) {
        return formatToDouble(money, countAfterPoint, RoundingMode.DOWN);
    }

    public static double formatToDouble(double money, int countAfterPoint, RoundingMode mode) {
        return formatToDouble(String.valueOf(money), countAfterPoint, mode);
    }

    public static String formatToString(double money, int countAfterPoint) {
        return formatToString(String.valueOf(money), countAfterPoint, true);
    }

    public static String formatToString(double money, int countAfterPoint, boolean needComm) {
        return formatToString(String.valueOf(money), countAfterPoint, needComm);
    }

    public static String formatToString(String money, int countAfterPoint) {
        return formatToStringHalfDown(money, countAfterPoint);
    }

    public static String formatToString(String money, int countAfterPoint, boolean needComm) {
        return formatToStringHalfDown(money, countAfterPoint, needComm);
    }

    public static String formatToStringHalfUp(double money, int countAfterPoint) {
        return formatToStringHalfUp(String.valueOf(money), countAfterPoint, true);
    }

    public static String formatToStringHalfUp(double money, int countAfterPoint, boolean needComm) {
        return formatToStringHalfUp(String.valueOf(money), countAfterPoint, needComm);
    }

    public static String formatToStringHalfUp(String money, int countAfterPoint) {
        return formatToString(money, countAfterPoint, RoundingMode.HALF_UP, true);
    }

    public static String formatToStringHalfUp(String money, int countAfterPoint, boolean needComm) {
        return formatToString(money, countAfterPoint, RoundingMode.HALF_UP, needComm);
    }

    public static String formatToStringHalfDown(double money, int countAfterPoint) {
        return formatToStringHalfDown(String.valueOf(money), countAfterPoint, true);
    }

    public static String formatToStringHalfDown(double money, int countAfterPoint, boolean needComm) {
        return formatToStringHalfDown(String.valueOf(money), countAfterPoint, needComm);
    }

    public static String formatToStringHalfDown(String money, int countAfterPoint) {
        return formatToString(money, countAfterPoint, RoundingMode.HALF_DOWN, true);
    }

    public static String formatToStringHalfDown(String money, int countAfterPoint, boolean needComm) {
        return formatToString(money, countAfterPoint, RoundingMode.HALF_DOWN, needComm);
    }

    public static String formatToStringUp(double money, int countAfterPoint) {
        return formatToStringUp(String.valueOf(money), countAfterPoint, true);
    }

    public static String formatToStringUp(double money, int countAfterPoint, boolean needComm) {
        return formatToStringUp(String.valueOf(money), countAfterPoint, needComm);
    }

    public static String formatToStringUp(String money, int countAfterPoint) {
        return formatToString(money, countAfterPoint, RoundingMode.UP, true);
    }

    public static String formatToStringUp(String money, int countAfterPoint, boolean needComm) {
        return formatToString(money, countAfterPoint, RoundingMode.UP, needComm);
    }

    public static String formatToStringDown(double money, int countAfterPoint) {
        return formatToStringDown(String.valueOf(money), countAfterPoint, true);
    }

    public static String formatToStringDown(double money, int countAfterPoint, boolean needComm) {
        return formatToStringDown(String.valueOf(money), countAfterPoint, needComm);
    }

    public static String formatToStringDown(String money, int countAfterPoint) {
        return formatToString(money, countAfterPoint, RoundingMode.DOWN, true);
    }

    public static String formatToStringDown(String money, int countAfterPoint, boolean needComm) {
        return formatToString(money, countAfterPoint, RoundingMode.DOWN, needComm);
    }

    public static String formatToString(double money, int countAfterPoint, RoundingMode mode, boolean needComm) {
        return formatToString(String.valueOf(money), countAfterPoint, mode, needComm);
    }

    public static double formatToDouble(String money, int countAfterPoint, RoundingMode mode) {
        try {
            String r = formatToString(money, countAfterPoint, mode, false);
            if (isNumber(r)) {
                return Double.parseDouble(r);
            }
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        } catch (Exception e) {
            FileLog.e(TAG + " =====> " + e.getMessage());
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
    }

    public static String formatToString(String money, int countAfterPoint, RoundingMode mode, boolean needComm) {
        if (!isNumber(money)) {
            return "0.00";
        }
        String regex = needComm ? "#,##0." : "###0.";
        if (countAfterPoint <= 0) {
            regex = needComm ? "#,###" : "####";
        }
        double m = Double.parseDouble(new BigDecimal(money).setScale(countAfterPoint, mode).toPlainString());
        if (countAfterPoint < 0) {
            countAfterPoint = 2;
        }
        StringBuilder sb = new StringBuilder(regex);
        for (int i = 0; i < countAfterPoint; i++) {
            sb.append("0");
        }
        return new DecimalFormat(sb.toString()).format(m);
    }

    public static String fenToYuan(double amount, int scale) {
        return new BigDecimal(amount).divide(new BigDecimal(100)).setScale(scale).toString();
    }
}
