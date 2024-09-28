package im.bclpbkiauv.ui.hui.friendscircle_v1.utils;

import com.blankj.utilcode.constant.TimeConstants;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static String getCurrentTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String fcFormat2Date1(long timeStamp) {
        long timeStamp2;
        String strTime;
        if (String.valueOf(timeStamp).length() == 10) {
            timeStamp2 = 1000 * timeStamp;
        } else {
            timeStamp2 = timeStamp;
        }
        long curTimeMillis = System.currentTimeMillis();
        Date curDate = new Date(curTimeMillis);
        long todayStartMillis = curTimeMillis - ((long) (((((curDate.getHours() * 60) * 60) + (curDate.getMinutes() * 60)) + curDate.getSeconds()) * 1000));
        if (timeStamp2 >= todayStartMillis) {
            Date date = new Date(timeStamp2);
            Object obj = "";
            if (date.getMinutes() < 10) {
                strTime = date.getHours() + ":0" + date.getMinutes();
            } else {
                strTime = date.getHours() + LogUtils.COLON + date.getMinutes();
            }
            return LocaleController.getString("today", R.string.today) + strTime;
        }
        Object obj2 = "";
        long timeStamp3 = timeStamp2;
        long yesterdayStartMillis = todayStartMillis - ((long) TimeConstants.DAY);
        if (timeStamp3 < yesterdayStartMillis) {
            return timeFormat(timeStamp3, "yyyy-MM-dd HH:mm");
        }
        long j = yesterdayStartMillis;
        Date date2 = new Date(timeStamp3);
        long j2 = curTimeMillis;
        if (date2.getMinutes() < 10) {
            return LocaleController.getString("Yesterday", R.string.Yesterday) + date2.getHours() + ":0" + date2.getMinutes();
        }
        return LocaleController.getString("Yesterday", R.string.Yesterday) + date2.getHours() + LogUtils.COLON + date2.getMinutes();
    }

    public static String fcFormat2Date(long timeStamp) {
        long timeStamp2;
        if (String.valueOf(timeStamp).length() == 10) {
            timeStamp2 = timeStamp * 1000;
        } else {
            timeStamp2 = timeStamp;
        }
        Date date = new Date(timeStamp2);
        Calendar calendar = Calendar.getInstance();
        calendar.get(5);
        long now = calendar.getTimeInMillis();
        calendar.setTime(date);
        long time = (now - calendar.getTimeInMillis()) / 1000;
        if (time <= 60) {
            return LocaleController.getString("fc_time_recently", R.string.fc_time_recently);
        }
        if (time <= 3600) {
            return String.format(LocaleController.getString(R.string.fc_time_format_minute), new Object[]{Long.valueOf(time / 60)});
        } else if (time <= 86400) {
            return String.format(LocaleController.getString(R.string.fc_time_format_hour), new Object[]{Long.valueOf(time / 3600)});
        } else if (time <= 172800) {
            return String.format(LocaleController.getString(R.string.fc_time_format_yesterday), new Object[]{timeFormat(timeStamp2, "HH:mm")});
        } else {
            calendar.clear();
            calendar.setTime(new Date(now));
            int nowYear = calendar.get(1);
            calendar.setTime(date);
            if (calendar.get(1) == nowYear) {
                return timeFormat(timeStamp2, "MM-dd HH:mm");
            }
            return timeFormat(timeStamp2, "yyyy-MM-dd HH:mm");
        }
    }

    public static Date getDate(String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String setDate(String timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (timeStamp == null || timeStamp.isEmpty() || timeStamp.equals("null")) {
            return "";
        }
        return sdf.format(new Date(Long.valueOf(timeStamp).longValue()));
    }

    public static String fcFormat(Date timeStamp) {
        String strTime;
        if (timeStamp == null) {
            return "";
        }
        long curTimeMillis = System.currentTimeMillis();
        Date curDate = new Date(curTimeMillis);
        long todayStartMillis = curTimeMillis - ((long) (((((curDate.getHours() * 60) * 60) + (curDate.getMinutes() * 60)) + curDate.getSeconds()) * 1000));
        if (timeStamp.getTime() >= todayStartMillis) {
            if (timeStamp.getMinutes() < 10) {
                strTime = timeStamp.getHours() + ":0" + timeStamp.getMinutes();
            } else {
                strTime = timeStamp.getHours() + LogUtils.COLON + timeStamp.getMinutes();
            }
            return LocaleController.getString("MessageScheduleToday", R.string.MessageScheduleToday) + strTime;
        }
        long yesterdayStartMillis = todayStartMillis - ((long) TimeConstants.DAY);
        if (timeStamp.getTime() >= yesterdayStartMillis) {
            Object obj = "";
            long j = curTimeMillis;
            if (timeStamp.getMinutes() < 10) {
                return LocaleController.getString("Yesterday", R.string.Yesterday) + timeStamp.getHours() + ":0" + timeStamp.getMinutes();
            }
            return LocaleController.getString("Yesterday", R.string.Yesterday) + timeStamp.getHours() + LogUtils.COLON + timeStamp.getMinutes();
        }
        String strTime2 = "";
        long j2 = curTimeMillis;
        long yesterdayBeforeStartMillis = yesterdayStartMillis - ((long) TimeConstants.DAY);
        if (timeStamp.getTime() >= yesterdayBeforeStartMillis) {
            long j3 = yesterdayBeforeStartMillis;
            if (timeStamp.getMinutes() < 10) {
                return LocaleController.getString("date_before_yesterday", R.string.date_before_yesterday) + timeStamp.getHours() + ":0" + timeStamp.getMinutes();
            }
            return LocaleController.getString("date_before_yesterday", R.string.date_before_yesterday) + timeStamp.getHours() + LogUtils.COLON + timeStamp.getMinutes();
        }
        return timeFormat(timeStamp.getTime(), "yyyy-MM-dd ");
    }

    public static String timeFormat(long timeStamp, String format) {
        return new SimpleDateFormat(format).format(Long.valueOf(timeStamp));
    }

    public static String YearMon(long timeStamp) {
        return new SimpleDateFormat("yyyy年MM月").format(new Date(1000 * timeStamp));
    }

    public static int getAgeByBirthday(Date birthday) {
        Calendar cal = Calendar.getInstance();
        if (!cal.before(birthday)) {
            int yearNow = cal.get(1);
            int monthNow = cal.get(2) + 1;
            int dayOfMonthNow = cal.get(5);
            cal.setTime(birthday);
            int yearBirth = cal.get(1);
            int monthBirth = cal.get(2) + 1;
            int dayOfMonthBirth = cal.get(5);
            int age = yearNow - yearBirth;
            if (monthNow > monthBirth) {
                return age;
            }
            if (monthNow != monthBirth) {
                return age - 1;
            }
            if (dayOfMonthNow < dayOfMonthBirth) {
                return age - 1;
            }
            return age;
        }
        throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
    }
}
