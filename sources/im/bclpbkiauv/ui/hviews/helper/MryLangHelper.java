package im.bclpbkiauv.ui.hviews.helper;

import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;
import org.slf4j.Marker;

public class MryLangHelper {
    public static int getNumberDigits(int number) {
        if (number <= 0) {
            return 0;
        }
        return (int) (Math.log10((double) number) + 1.0d);
    }

    public static int getNumberDigits(long number) {
        if (number <= 0) {
            return 0;
        }
        return (int) (Math.log10((double) number) + 1.0d);
    }

    public static String formatNumberToLimitedDigits(int number, int maxDigits) {
        if (getNumberDigits(number) <= maxDigits) {
            return String.valueOf(number);
        }
        StringBuilder result = new StringBuilder();
        for (int digit = 1; digit <= maxDigits; digit++) {
            result.append("9");
        }
        result.append(Marker.ANY_NON_NULL_MARKER);
        return result.toString();
    }

    public static String regularizePrice(float price) {
        return String.format(Locale.CHINESE, "%.2f", new Object[]{Float.valueOf(price)});
    }

    public static String regularizePrice(double price) {
        return String.format(Locale.CHINESE, "%.2f", new Object[]{Double.valueOf(price)});
    }

    public static boolean isNullOrEmpty(CharSequence string) {
        return string == null || string.length() == 0;
    }

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean objectEquals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int constrain(int amount, int low, int high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }

    public static float constrain(float amount, float low, float high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }
}
