package com.blankj.utilcode.util;

import androidx.exifinterface.media.ExifInterface;
import com.blankj.utilcode.constant.TimeConstants;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {
    private static final String[] CHINESE_ZODIAC = {"猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊"};
    private static final ThreadLocal<SimpleDateFormat> SDF_THREAD_LOCAL = new ThreadLocal<>();
    private static final String[] ZODIAC = {"水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"};
    private static final int[] ZODIAC_FLAGS = {20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22};

    private static SimpleDateFormat getDefaultFormat() {
        return getDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private static SimpleDateFormat getDateFormat(String pattern) {
        SimpleDateFormat simpleDateFormat = SDF_THREAD_LOCAL.get();
        if (simpleDateFormat == null) {
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern, Locale.getDefault());
            SDF_THREAD_LOCAL.set(simpleDateFormat2);
            return simpleDateFormat2;
        }
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat;
    }

    private TimeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String millis2String(long millis) {
        return millis2String(millis, (DateFormat) getDefaultFormat());
    }

    public static String millis2String(long millis, String pattern) {
        if (pattern != null) {
            return millis2String(millis, (DateFormat) getDateFormat(pattern));
        }
        throw new NullPointerException("Argument 'pattern' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String millis2String(long millis, DateFormat format) {
        if (format != null) {
            return format.format(new Date(millis));
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long string2Millis(String time) {
        return string2Millis(time, (DateFormat) getDefaultFormat());
    }

    public static long string2Millis(String time, String pattern) {
        if (pattern != null) {
            return string2Millis(time, (DateFormat) getDateFormat(pattern));
        }
        throw new NullPointerException("Argument 'pattern' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long string2Millis(String time, DateFormat format) {
        if (format != null) {
            try {
                return format.parse(time).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Date string2Date(String time) {
        return string2Date(time, (DateFormat) getDefaultFormat());
    }

    public static Date string2Date(String time, String pattern) {
        if (pattern != null) {
            return string2Date(time, (DateFormat) getDateFormat(pattern));
        }
        throw new NullPointerException("Argument 'pattern' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Date string2Date(String time, DateFormat format) {
        if (format != null) {
            try {
                return format.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String date2String(Date date) {
        return date2String(date, (DateFormat) getDefaultFormat());
    }

    public static String date2String(Date date, String pattern) {
        if (pattern != null) {
            return getDateFormat(pattern).format(date);
        }
        throw new NullPointerException("Argument 'pattern' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String date2String(Date date, DateFormat format) {
        if (format != null) {
            return format.format(date);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long date2Millis(Date date) {
        return date.getTime();
    }

    public static Date millis2Date(long millis) {
        return new Date(millis);
    }

    public static long getTimeSpan(String time1, String time2, int unit) {
        return getTimeSpan(time1, time2, getDefaultFormat(), unit);
    }

    public static long getTimeSpan(String time1, String time2, DateFormat format, int unit) {
        if (format != null) {
            return millis2TimeSpan(string2Millis(time1, format) - string2Millis(time2, format), unit);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long getTimeSpan(Date date1, Date date2, int unit) {
        return millis2TimeSpan(date2Millis(date1) - date2Millis(date2), unit);
    }

    public static long getTimeSpan(long millis1, long millis2, int unit) {
        return millis2TimeSpan(millis1 - millis2, unit);
    }

    public static String getFitTimeSpan(String time1, String time2, int precision) {
        return millis2FitTimeSpan(string2Millis(time1, (DateFormat) getDefaultFormat()) - string2Millis(time2, (DateFormat) getDefaultFormat()), precision);
    }

    public static String getFitTimeSpan(String time1, String time2, DateFormat format, int precision) {
        if (format != null) {
            return millis2FitTimeSpan(string2Millis(time1, format) - string2Millis(time2, format), precision);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getFitTimeSpan(Date date1, Date date2, int precision) {
        return millis2FitTimeSpan(date2Millis(date1) - date2Millis(date2), precision);
    }

    public static String getFitTimeSpan(long millis1, long millis2, int precision) {
        return millis2FitTimeSpan(millis1 - millis2, precision);
    }

    public static long getNowMills() {
        return System.currentTimeMillis();
    }

    public static String getNowString() {
        return millis2String(System.currentTimeMillis(), (DateFormat) getDefaultFormat());
    }

    public static String getNowString(DateFormat format) {
        if (format != null) {
            return millis2String(System.currentTimeMillis(), format);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Date getNowDate() {
        return new Date();
    }

    public static long getTimeSpanByNow(String time, int unit) {
        return getTimeSpan(time, getNowString(), getDefaultFormat(), unit);
    }

    public static long getTimeSpanByNow(String time, DateFormat format, int unit) {
        if (format != null) {
            return getTimeSpan(time, getNowString(format), format, unit);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long getTimeSpanByNow(Date date, int unit) {
        return getTimeSpan(date, new Date(), unit);
    }

    public static long getTimeSpanByNow(long millis, int unit) {
        return getTimeSpan(millis, System.currentTimeMillis(), unit);
    }

    public static String getFitTimeSpanByNow(String time, int precision) {
        return getFitTimeSpan(time, getNowString(), getDefaultFormat(), precision);
    }

    public static String getFitTimeSpanByNow(String time, DateFormat format, int precision) {
        if (format != null) {
            return getFitTimeSpan(time, getNowString(format), format, precision);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getFitTimeSpanByNow(Date date, int precision) {
        return getFitTimeSpan(date, getNowDate(), precision);
    }

    public static String getFitTimeSpanByNow(long millis, int precision) {
        return getFitTimeSpan(millis, System.currentTimeMillis(), precision);
    }

    public static String getFriendlyTimeSpanByNow(String time) {
        return getFriendlyTimeSpanByNow(time, getDefaultFormat());
    }

    public static String getFriendlyTimeSpanByNow(String time, DateFormat format) {
        if (format != null) {
            return getFriendlyTimeSpanByNow(string2Millis(time, format));
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getFriendlyTimeSpanByNow(Date date) {
        return getFriendlyTimeSpanByNow(date.getTime());
    }

    public static String getFriendlyTimeSpanByNow(long millis) {
        long span = System.currentTimeMillis() - millis;
        if (span < 0) {
            return String.format("%tc", new Object[]{Long.valueOf(millis)});
        } else if (span < 1000) {
            return "刚刚";
        } else {
            if (span < DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS) {
                return String.format(Locale.getDefault(), "%d秒前", new Object[]{Long.valueOf(span / 1000)});
            } else if (span < 3600000) {
                return String.format(Locale.getDefault(), "%d分钟前", new Object[]{Long.valueOf(span / DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS)});
            } else {
                long wee = getWeeOfToday();
                if (millis >= wee) {
                    return String.format("今天%tR", new Object[]{Long.valueOf(millis)});
                } else if (millis >= wee - 86400000) {
                    return String.format("昨天%tR", new Object[]{Long.valueOf(millis)});
                } else {
                    return String.format("%tF", new Object[]{Long.valueOf(millis)});
                }
            }
        }
    }

    private static long getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(13, 0);
        cal.set(12, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public static long getMillis(long millis, long timeSpan, int unit) {
        return timeSpan2Millis(timeSpan, unit) + millis;
    }

    public static long getMillis(String time, long timeSpan, int unit) {
        return getMillis(time, getDefaultFormat(), timeSpan, unit);
    }

    public static long getMillis(String time, DateFormat format, long timeSpan, int unit) {
        if (format != null) {
            return string2Millis(time, format) + timeSpan2Millis(timeSpan, unit);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long getMillis(Date date, long timeSpan, int unit) {
        return date2Millis(date) + timeSpan2Millis(timeSpan, unit);
    }

    public static String getString(long millis, long timeSpan, int unit) {
        return getString(millis, (DateFormat) getDefaultFormat(), timeSpan, unit);
    }

    public static String getString(long millis, DateFormat format, long timeSpan, int unit) {
        if (format != null) {
            return millis2String(timeSpan2Millis(timeSpan, unit) + millis, format);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getString(String time, long timeSpan, int unit) {
        return getString(time, (DateFormat) getDefaultFormat(), timeSpan, unit);
    }

    public static String getString(String time, DateFormat format, long timeSpan, int unit) {
        if (format != null) {
            return millis2String(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit), format);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getString(Date date, long timeSpan, int unit) {
        return getString(date, (DateFormat) getDefaultFormat(), timeSpan, unit);
    }

    public static String getString(Date date, DateFormat format, long timeSpan, int unit) {
        if (format != null) {
            return millis2String(date2Millis(date) + timeSpan2Millis(timeSpan, unit), format);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Date getDate(long millis, long timeSpan, int unit) {
        return millis2Date(timeSpan2Millis(timeSpan, unit) + millis);
    }

    public static Date getDate(String time, long timeSpan, int unit) {
        return getDate(time, getDefaultFormat(), timeSpan, unit);
    }

    public static Date getDate(String time, DateFormat format, long timeSpan, int unit) {
        if (format != null) {
            return millis2Date(string2Millis(time, format) + timeSpan2Millis(timeSpan, unit));
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Date getDate(Date date, long timeSpan, int unit) {
        return millis2Date(date2Millis(date) + timeSpan2Millis(timeSpan, unit));
    }

    public static long getMillisByNow(long timeSpan, int unit) {
        return getMillis(getNowMills(), timeSpan, unit);
    }

    public static String getStringByNow(long timeSpan, int unit) {
        return getStringByNow(timeSpan, getDefaultFormat(), unit);
    }

    public static String getStringByNow(long timeSpan, DateFormat format, int unit) {
        if (format != null) {
            return getString(getNowMills(), format, timeSpan, unit);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Date getDateByNow(long timeSpan, int unit) {
        return getDate(getNowMills(), timeSpan, unit);
    }

    public static boolean isToday(String time) {
        return isToday(string2Millis(time, (DateFormat) getDefaultFormat()));
    }

    public static boolean isToday(String time, DateFormat format) {
        if (format != null) {
            return isToday(string2Millis(time, format));
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isToday(Date date) {
        return isToday(date.getTime());
    }

    public static boolean isToday(long millis) {
        long wee = getWeeOfToday();
        return millis >= wee && millis < 86400000 + wee;
    }

    public static boolean isLeapYear(String time) {
        return isLeapYear(string2Date(time, (DateFormat) getDefaultFormat()));
    }

    public static boolean isLeapYear(String time, DateFormat format) {
        if (format != null) {
            return isLeapYear(string2Date(time, format));
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isLeapYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return isLeapYear(cal.get(1));
    }

    public static boolean isLeapYear(long millis) {
        return isLeapYear(millis2Date(millis));
    }

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    public static String getChineseWeek(String time) {
        return getChineseWeek(string2Date(time, (DateFormat) getDefaultFormat()));
    }

    public static String getChineseWeek(String time, DateFormat format) {
        if (format != null) {
            return getChineseWeek(string2Date(time, format));
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getChineseWeek(Date date) {
        return new SimpleDateFormat(ExifInterface.LONGITUDE_EAST, Locale.CHINA).format(date);
    }

    public static String getChineseWeek(long millis) {
        return getChineseWeek(new Date(millis));
    }

    public static String getUSWeek(String time) {
        return getUSWeek(string2Date(time, (DateFormat) getDefaultFormat()));
    }

    public static String getUSWeek(String time, DateFormat format) {
        if (format != null) {
            return getUSWeek(string2Date(time, format));
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getUSWeek(Date date) {
        return new SimpleDateFormat("EEEE", Locale.US).format(date);
    }

    public static String getUSWeek(long millis) {
        return getUSWeek(new Date(millis));
    }

    public static boolean isAm() {
        return Calendar.getInstance().get(9) == 0;
    }

    public static boolean isAm(String time) {
        return getValueByCalendarField(time, getDefaultFormat(), 9) == 0;
    }

    public static boolean isAm(String time, DateFormat format) {
        if (format != null) {
            return getValueByCalendarField(time, format, 9) == 0;
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isAm(Date date) {
        return getValueByCalendarField(date, 9) == 0;
    }

    public static boolean isAm(long millis) {
        return getValueByCalendarField(millis, 9) == 0;
    }

    public static boolean isPm() {
        return !isAm();
    }

    public static boolean isPm(String time) {
        return !isAm(time);
    }

    public static boolean isPm(String time, DateFormat format) {
        if (format != null) {
            return !isAm(time, format);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isPm(Date date) {
        return !isAm(date);
    }

    public static boolean isPm(long millis) {
        return !isAm(millis);
    }

    public static int getValueByCalendarField(int field) {
        return Calendar.getInstance().get(field);
    }

    public static int getValueByCalendarField(String time, int field) {
        return getValueByCalendarField(string2Date(time, (DateFormat) getDefaultFormat()), field);
    }

    public static int getValueByCalendarField(String time, DateFormat format, int field) {
        if (format != null) {
            return getValueByCalendarField(string2Date(time, format), field);
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getValueByCalendarField(Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(field);
    }

    public static int getValueByCalendarField(long millis, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal.get(field);
    }

    public static String getChineseZodiac(String time) {
        return getChineseZodiac(string2Date(time, (DateFormat) getDefaultFormat()));
    }

    public static String getChineseZodiac(String time, DateFormat format) {
        if (format != null) {
            return getChineseZodiac(string2Date(time, format));
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getChineseZodiac(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return CHINESE_ZODIAC[cal.get(1) % 12];
    }

    public static String getChineseZodiac(long millis) {
        return getChineseZodiac(millis2Date(millis));
    }

    public static String getChineseZodiac(int year) {
        return CHINESE_ZODIAC[year % 12];
    }

    public static String getZodiac(String time) {
        return getZodiac(string2Date(time, (DateFormat) getDefaultFormat()));
    }

    public static String getZodiac(String time, DateFormat format) {
        if (format != null) {
            return getZodiac(string2Date(time, format));
        }
        throw new NullPointerException("Argument 'format' of type DateFormat (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getZodiac(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getZodiac(cal.get(2) + 1, cal.get(5));
    }

    public static String getZodiac(long millis) {
        return getZodiac(millis2Date(millis));
    }

    public static String getZodiac(int month, int day) {
        return ZODIAC[day >= ZODIAC_FLAGS[month + -1] ? month - 1 : (month + 10) % 12];
    }

    private static long timeSpan2Millis(long timeSpan, int unit) {
        return ((long) unit) * timeSpan;
    }

    private static long millis2TimeSpan(long millis, int unit) {
        return millis / ((long) unit);
    }

    private static String millis2FitTimeSpan(long millis, int precision) {
        if (precision <= 0) {
            return null;
        }
        int precision2 = Math.min(precision, 5);
        String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
        if (millis == 0) {
            return 0 + units[precision2 - 1];
        }
        StringBuilder sb = new StringBuilder();
        if (millis < 0) {
            sb.append("-");
            millis = -millis;
        }
        int[] unitLen = {TimeConstants.DAY, TimeConstants.HOUR, TimeConstants.MIN, 1000, 1};
        for (int i = 0; i < precision2; i++) {
            if (millis >= ((long) unitLen[i])) {
                long mode = millis / ((long) unitLen[i]);
                millis -= ((long) unitLen[i]) * mode;
                sb.append(mode);
                sb.append(units[i]);
            }
        }
        return sb.toString();
    }
}
