package im.bclpbkiauv.ui.utils.number;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0 || "null".equals(str.toString());
    }

    public static boolean isNumber(CharSequence str) {
        if (isEmpty(str)) {
            return false;
        }
        return Pattern.compile("([0-9]\\d*\\.?\\d*)|(0\\.\\d*[0-9])").matcher(str).matches();
    }

    public static boolean isNumericalValue(CharSequence str) {
        if (isEmpty(str)) {
            return false;
        }
        return Pattern.compile("^[+-]?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d)+)?$").matcher(str).matches();
    }

    public static boolean isNumericInt(CharSequence str) {
        if (isEmpty(str)) {
            return false;
        }
        return Pattern.compile("(^[+-]?([0-9]|([1-9][0-9]*))$)").matcher(str).matches();
    }

    public static boolean isNumericDouble(CharSequence str) {
        if (isEmpty(str)) {
            return false;
        }
        return Pattern.compile("^[+-]?(([1-9]\\d*\\.?\\d+)|(0{1}\\.\\d+)|0{1})").matcher(str).matches();
    }

    public static boolean isNumericPositiveInt(CharSequence str) {
        if (isEmpty(str)) {
            return false;
        }
        return Pattern.compile("^[+-]?(([1-9]{1}\\d*)|([0]{1}))$").matcher(str).matches();
    }

    public static boolean isOneDouble(CharSequence str) {
        if (isEmpty(str)) {
            return false;
        }
        return Pattern.compile("^(\\d+\\.\\d{1,1}|\\d+)$").matcher(str).matches();
    }

    public static boolean isTwoDouble(CharSequence str) {
        if (isEmpty(str)) {
            return false;
        }
        return Pattern.compile("^(\\d+\\.\\d{1,2}|\\d+)$").matcher(str).matches();
    }

    public static boolean isNumLess(String str, float min) {
        if (isNumber(str) && Float.parseFloat(str) < min) {
            return true;
        }
        return false;
    }

    public static int compareTo(String str1, String str2) {
        if (!isNumber(str1) || !isNumber(str2)) {
            return str1.compareTo(str2);
        }
        return Double.compare(Double.parseDouble(str1), Double.parseDouble(str2));
    }

    public static boolean isNumMore(String str, float max) {
        if (isNumber(str) && Float.parseFloat(str) > max) {
            return true;
        }
        return false;
    }

    public static String getFirstNumbers(CharSequence str) {
        Matcher matcher = Pattern.compile("\\d+").matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static String[] getNumbersFromStr(String str) {
        return getNumbersFromStr(str, -1);
    }

    public static String[] getNumbersFromStr(String str, int length) {
        List<String> result = new LinkedList<>();
        if (str != null && str.length() > 0) {
            Matcher matcher = Pattern.compile("\\d+").matcher(str);
            int index = 0;
            while (matcher.find() && (length <= 0 || index < length)) {
                result.add(matcher.group(0));
                index++;
            }
        }
        return (String[]) result.toArray(new String[0]);
    }

    public static double roundUp(double originData, int scale) {
        return roundUp(originData + "", scale);
    }

    public static double roundUp(String originData, int scale) {
        return roundByRoundingMode(originData, scale, RoundingMode.UP);
    }

    public static double roundHalfUp(double originData, int scale) {
        return roundHalfUp(originData + "", scale);
    }

    public static double roundHalfUp(String originData, int scale) {
        return roundByRoundingMode(originData, scale, RoundingMode.HALF_UP);
    }

    public static double roundByRoundingMode(double originData, int scale, RoundingMode mode) {
        return roundByRoundingMode(originData + "", scale, mode);
    }

    public static double roundByRoundingMode(String originData, int scale, RoundingMode mode) {
        if (!isNumber(originData)) {
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
        if (scale <= 0) {
            scale = 2;
        }
        return new BigDecimal(originData).setScale(scale, mode).doubleValue();
    }

    public static String replacesSientificE(double originData, int scale) {
        StringBuilder regex = new StringBuilder("#0.");
        if (scale <= 0) {
            scale = 2;
        }
        for (int i = 0; i < scale; i++) {
            regex.append("0");
        }
        return replacesSientificE(originData, regex.toString());
    }

    public static String replacesSientificE(double originData, String regex) {
        if (isEmpty(regex)) {
            regex = "#0.00";
        }
        return new DecimalFormat(regex).format(originData);
    }

    public static String toPercentHalf(double originData, int digitsCount) {
        return toPercentHalf(originData + "", digitsCount);
    }

    public static String toPercentHalf(String originData, int digitsCount) {
        return toPercent(originData, digitsCount, RoundingMode.HALF_UP);
    }

    public static String toPercent(double originData, int digitsCount, RoundingMode mode) {
        return toPercent(originData + "", digitsCount, mode);
    }

    public static String toPercent(String originData, int digitsCount, RoundingMode mode) {
        if (!isNumber(originData)) {
            return originData;
        }
        double newData = Double.parseDouble(originData);
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMinimumFractionDigits(digitsCount);
        percent.setRoundingMode(mode);
        return percent.format(newData);
    }
}
