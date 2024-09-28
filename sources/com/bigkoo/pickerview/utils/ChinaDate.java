package com.bigkoo.pickerview.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ChinaDate {
    private static final String[] Animals = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
    private static final String[] Gan = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] Zhi = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static final long[] lunarInfo = {19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422, 42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216, 27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 28309, 23248, 11104, 100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938, 43344, 46423, 27808, 46416, 86869, 19872, 42416, 83315, 21168, 43432, 59728, 27296, 44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925, 19152, 42192, 54484, 53840, 54616, 46400, 46752, 103846, 38320, 18864, 43380, 42160, 45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480, 21952, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680, 37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312, 31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371, 38608, 19195, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864, 42359, 42160, 43600, 111189, 27936, 44448, 84835, 37744, 18936, 18800, 25776, 92326, 59984, 27424, 108228, 43744, 41696, 53987, 51552, 54615, 54432, 55888, 23893, 22176, 42704, 21972, 21200, 43448, 43344, 46240, 46758, 44368, 21920, 43940, 42416, 21168, 45683, 26928, 29495, 27296, 44368, 84821, 19296, 42352, 21732, 53600, 59752, 54560, 55968, 92838, 22224, 19168, 43476, 41680, 53584, 62034, 54560};
    private static final String[] nStr1 = {"", "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊"};
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 EEEEE");

    private static final int lYearDays(int y) {
        int sum = 348;
        for (int i = 32768; i > 8; i >>= 1) {
            if ((lunarInfo[y - 1900] & ((long) i)) != 0) {
                sum++;
            }
        }
        return leapDays(y) + sum;
    }

    public static final int leapDays(int y) {
        if (leapMonth(y) == 0) {
            return 0;
        }
        if ((lunarInfo[y - 1900] & 65536) != 0) {
            return 30;
        }
        return 29;
    }

    public static final int leapMonth(int y) {
        return (int) (lunarInfo[y - 1900] & 15);
    }

    public static final int monthDays(int y, int m) {
        if ((lunarInfo[y - 1900] & ((long) (65536 >> m))) == 0) {
            return 29;
        }
        return 30;
    }

    public static final String AnimalsYear(int y) {
        return Animals[(y - 4) % 12];
    }

    private static final String cyclicalm(int num) {
        return Gan[num % 10] + Zhi[num % 12];
    }

    public static final String cyclical(int y) {
        return cyclicalm((y - 1900) + 36);
    }

    public static final long[] calElement(int y, int m, int d) {
        int temp;
        long[] nongDate = new long[7];
        int temp2 = 0;
        Date baseDate = new GregorianCalendar(LunarCalendar.MIN_YEAR, 0, 31).getTime();
        Date objDate = new GregorianCalendar(y, m - 1, d).getTime();
        long offset = (objDate.getTime() - baseDate.getTime()) / 86400000;
        nongDate[5] = 40 + offset;
        nongDate[4] = 14;
        int i = LunarCalendar.MIN_YEAR;
        while (i < 2100 && offset > 0) {
            temp2 = lYearDays(i);
            offset -= (long) temp2;
            nongDate[4] = nongDate[4] + 12;
            i++;
            objDate = objDate;
        }
        if (offset < 0) {
            offset += (long) temp;
            i--;
            nongDate[4] = nongDate[4] - 12;
        }
        nongDate[0] = (long) i;
        nongDate[3] = (long) (i - 1864);
        int leap = leapMonth(i);
        nongDate[6] = 0;
        int i2 = 1;
        while (i2 < 13 && offset > 0) {
            if (leap > 0 && i2 == leap + 1 && nongDate[6] == 0) {
                i2--;
                nongDate[6] = 1;
                temp = leapDays((int) nongDate[0]);
            } else {
                temp = monthDays((int) nongDate[0], i2);
            }
            if (nongDate[6] == 1 && i2 == leap + 1) {
                nongDate[6] = 0;
            }
            offset -= (long) temp;
            if (nongDate[6] == 0) {
                nongDate[4] = nongDate[4] + 1;
            }
            i2++;
            int i3 = y;
        }
        if (offset == 0 && leap > 0 && i2 == leap + 1) {
            if (nongDate[6] == 1) {
                nongDate[6] = 0;
            } else {
                nongDate[6] = 1;
                i2--;
                nongDate[4] = nongDate[4] - 1;
            }
        }
        if (offset < 0) {
            offset += (long) temp;
            i2--;
            nongDate[4] = nongDate[4] - 1;
        }
        nongDate[1] = (long) i2;
        nongDate[2] = 1 + offset;
        return nongDate;
    }

    public static final String getChinaDate(int day) {
        String a = "";
        if (day == 10) {
            return "初十";
        }
        if (day == 20) {
            return "二十";
        }
        if (day == 30) {
            return "三十";
        }
        int two = day / 10;
        if (two == 0) {
            a = "初";
        }
        if (two == 1) {
            a = "十";
        }
        if (two == 2) {
            a = "廿";
        }
        if (two == 3) {
            a = "三";
        }
        switch (day % 10) {
            case 1:
                return a + "一";
            case 2:
                return a + "二";
            case 3:
                return a + "三";
            case 4:
                return a + "四";
            case 5:
                return a + "五";
            case 6:
                return a + "六";
            case 7:
                return a + "七";
            case 8:
                return a + "八";
            case 9:
                return a + "九";
            default:
                return a;
        }
    }

    /* JADX INFO: finally extract failed */
    public static String getCurrentLunarDate() {
        Calendar today = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        int year = today.get(1);
        long[] l = calElement(year, today.get(2) + 1, today.get(5));
        StringBuffer sToday = new StringBuffer();
        try {
            sToday.append(sdf.format(today.getTime()));
            sToday.append(" 农历");
            sToday.append(cyclical(year));
            sToday.append('(');
            sToday.append(AnimalsYear(year));
            sToday.append(")年");
            sToday.append(nStr1[(int) l[1]]);
            sToday.append("月");
            sToday.append(getChinaDate((int) l[2]));
            return sToday.toString();
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public static String oneDay(int year, int month, int day) {
        long[] l = calElement(year, month, day);
        StringBuffer sToday = new StringBuffer();
        try {
            sToday.append(" 农历");
            sToday.append(cyclical(year));
            sToday.append('(');
            sToday.append(AnimalsYear(year));
            sToday.append(")年");
            sToday.append(nStr1[(int) l[1]]);
            sToday.append("月");
            sToday.append(getChinaDate((int) l[2]));
            return sToday.toString();
        } catch (Throwable th) {
            throw th;
        }
    }

    public static String getLunarYearText(int lunarYear) {
        return Gan[(lunarYear - 4) % 10] + Zhi[(lunarYear - 4) % 12] + "年";
    }

    public static ArrayList<String> getYears(int startYear, int endYear) {
        ArrayList<String> years = new ArrayList<>();
        for (int i = startYear; i < endYear; i++) {
            years.add(String.format("%s(%d)", new Object[]{getLunarYearText(i), Integer.valueOf(i)}));
        }
        return years;
    }

    public static ArrayList<String> getMonths(int year) {
        ArrayList<String> baseMonths = new ArrayList<>();
        for (int i = 1; i < nStr1.length; i++) {
            baseMonths.add(nStr1[i] + "月");
        }
        if (leapMonth(year) != 0) {
            int leapMonth = leapMonth(year);
            baseMonths.add(leapMonth, "闰" + nStr1[leapMonth(year)] + "月");
        }
        return baseMonths;
    }

    public static ArrayList<String> getLunarDays(int maxDay) {
        ArrayList<String> days = new ArrayList<>();
        for (int i = 1; i <= maxDay; i++) {
            days.add(getChinaDate(i));
        }
        return days;
    }
}
