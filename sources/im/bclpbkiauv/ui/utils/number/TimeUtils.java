package im.bclpbkiauv.ui.utils.number;

import androidx.exifinterface.media.ExifInterface;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static long getTimeLong() {
        return System.currentTimeMillis();
    }

    public static int getTimeInt(String filter) {
        return Integer.parseInt(new SimpleDateFormat(filter).format(new Date()));
    }

    public static String getTimeStringE() {
        return new SimpleDateFormat(ExifInterface.LONGITUDE_EAST).format(new Date());
    }

    public static int getTimeInt(String StringTime, String filter) {
        return Integer.parseInt(new SimpleDateFormat(filter).format(new Date(getTimeLong("yyyy-MM-dd HH:mm:ss", StringTime).longValue())));
    }

    public static String getTimeStringE(String stringTime) {
        return new SimpleDateFormat(ExifInterface.LONGITUDE_EAST).format(new Date(getTimeLong("yyyy-MM-dd HH:mm:ss", stringTime).longValue()));
    }

    public static final String getTimeString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getTimeLong()));
    }

    public static final String getTimeString(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    public static final String getTimeString(long time, String filter) {
        return new SimpleDateFormat(filter).format(new Date(time));
    }

    public static final String getTimeString(String filter) {
        return new SimpleDateFormat(filter).format(new Date(getTimeLong()));
    }

    public static Long getTimeLong(String filter, String date) {
        try {
            return Long.valueOf(new SimpleDateFormat(filter).parse(date).getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String getTimeLocalString(String filter, String data, String filterInside) {
        return getTimeString(getTimeLong(filter, data).longValue(), filterInside);
    }
}
