package com.bigkoo.pickerview.utils;

import android.util.Log;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.GregorianCalendar;

public class LunarCalendar {
    private static final int[] DAYS_BEFORE_MONTH = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
    private static final int[] LUNAR_INFO;
    public static final int MAX_YEAR = 2099;
    public static final int MIN_YEAR = 1900;
    private static int[] lunar_month_days = {1887, 5780, 5802, 19157, 2742, 50359, 1198, 2646, 46378, 7466, 3412, 30122, 5482, 67949, 2396, 5294, 43597, 6732, 6954, 36181, 2772, 4954, 18781, 2396, 54427, 5274, 6730, 47781, 5800, 6868, 21210, 4790, 59703, 2350, 5270, 46667, 3402, 3496, 38325, 1388, 4782, 18735, 2350, 52374, 6804, 7498, 44457, 2906, 1388, 29294, 4700, 63789, 6442, 6804, 56138, 5802, 2772, 38235, 1210, 4698, 22827, 5418, 63125, 3476, 5802, 43701, 2484, 5302, 27223, 2646, 70954, 7466, 3412, 54698, 5482, 2412, 38062, 5294, 2636, 32038, 6954, 60245, 2772, 4826, 43357, 2394, 5274, 39501, 6730, 72357, 5800, 5844, 53978, 4790, 2358, 38039, 5270, 87627, 3402, 3496, 54708, 5484, 4782, 43311, 2350, 3222, 27978, 7498, 68965, 2904, 5484, 45677, 4700, 6444, 39573, 6804, 6986, 19285, 2772, 62811, 1210, 4698, 47403, 5418, 5780, 38570, 5546, 76469, 2420, 5302, 51799, 2646, 5414, 36501, 3412, 5546, 18869, 2412, 54446, 5276, 6732, 48422, 6822, 2900, 28010, 4826, 92509, 2394, 5274, 55883, 6730, 6820, 47956, 5812, 2778, 18779, 2358, 62615, 5270, 5450, 46757, 3492, 5556, 27318, 4718, 67887, 2350, 3222, 52554, 7498, 3428, 38252, 5468, 4700, 31022, 6444, 64149, 6804, 6986, 43861, 2772, 5338, 35421, 2650, 70955, 5418, 5780, 54954, 5546, 2740, 38074, 5302, 2646, 29991, 3366, 61011, 3412, 5546, 43445, 2412, 5294, 35406, 6732, 72998, 6820, 6996, 52586, 2778, 2396, 38045, 5274, 6698, 23333, 6820, 64338, 5812, 2746, 43355, 2358, 5270, 39499, 5450, 79525, 3492, 5548};
    private static int[] solar_1_1 = {1887, 966732, 967231, 967733, 968265, 968766, 969297, 969798, 970298, 970829, 971330, 971830, 972362, 972863, 973395, 973896, 974397, 974928, 975428, 975929, 976461, 976962, 977462, 977994, 978494, 979026, 979526, 980026, 980558, 981059, 981559, 982091, 982593, 983124, 983624, 984124, 984656, 985157, 985656, 986189, 986690, 987191, 987722, 988222, 988753, 989254, 989754, 990286, 990788, 991288, 991819, 992319, 992851, 993352, 993851, 994383, 994885, 995385, 995917, 996418, 996918, 997450, 997949, 998481, 998982, 999483, 1000014, 1000515, 1001016, 1001548, 1002047, 1002578, 1003080, 1003580, 1004111, 1004613, 1005113, 1005645, 1006146, 1006645, 1007177, 1007678, 1008209, 1008710, 1009211, 1009743, 1010243, 1010743, 1011275, 1011775, 1012306, 1012807, 1013308, 1013840, 1014341, 1014841, 1015373, 1015874, 1016404, 1016905, 1017405, 1017937, 1018438, 1018939, 1019471, 1019972, 1020471, 1021002, 1021503, 1022035, 1022535, 1023036, 1023568, 1024069, 1024568, 1025100, 1025601, 1026102, 1026633, 1027133, 1027666, 1028167, 1028666, 1029198, 1029699, 1030199, 1030730, 1031231, 1031763, 1032264, 1032764, 1033296, 1033797, 1034297, 1034828, 1035329, 1035830, 1036362, 1036861, 1037393, 1037894, 1038394, 1038925, 1039427, 1039927, 1040459, 1040959, 1041491, 1041992, 1042492, 1043023, 1043524, 1044024, 1044556, 1045057, 1045558, 1046090, 1046590, 1047121, 1047622, 1048122, 1048654, 1049154, 1049655, 1050187, 1050689, 1051219, 1051720, 1052220, 1052751, 1053252, 1053752, 1054284, 1054786, 1055285, 1055817, 1056317, 1056849, 1057349, 1057850, 1058382, 1058883, 1059383, 1059915, 1060415, 1060947, 1061447, 1061947, 1062479, 1062981, 1063480, 1064012, 1064514, 1065014, 1065545, 1066045, 1066577, 1067078, 1067578, 1068110, 1068611, 1069112, 1069642, 1070142, 1070674, 1071175, 1071675, 1072207, 1072709, 1073209, 1073740, 1074241, 1074741, 1075273, 1075773, 1076305, 1076807, 1077308, 1077839, 1078340, 1078840, 1079372, 1079871, 1080403, 1080904};

    static {
        int[] iArr = new int[ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION];
        // fill-array-data instruction
        iArr[0] = 8697535;
        iArr[1] = 306771;
        iArr[2] = 677704;
        iArr[3] = 5580477;
        iArr[4] = 861776;
        iArr[5] = 890180;
        iArr[6] = 4631225;
        iArr[7] = 354893;
        iArr[8] = 634178;
        iArr[9] = 2404022;
        iArr[10] = 306762;
        iArr[11] = 6966718;
        iArr[12] = 675154;
        iArr[13] = 861510;
        iArr[14] = 6116026;
        iArr[15] = 742478;
        iArr[16] = 879171;
        iArr[17] = 2714935;
        iArr[18] = 613195;
        iArr[19] = 7642049;
        iArr[20] = 300884;
        iArr[21] = 674632;
        iArr[22] = 5973436;
        iArr[23] = 435536;
        iArr[24] = 447557;
        iArr[25] = 4905656;
        iArr[26] = 177741;
        iArr[27] = 612162;
        iArr[28] = 2398135;
        iArr[29] = 300874;
        iArr[30] = 6703934;
        iArr[31] = 870993;
        iArr[32] = 959814;
        iArr[33] = 5690554;
        iArr[34] = 372046;
        iArr[35] = 177732;
        iArr[36] = 3749688;
        iArr[37] = 601675;
        iArr[38] = 8165055;
        iArr[39] = 824659;
        iArr[40] = 870984;
        iArr[41] = 7185723;
        iArr[42] = 742735;
        iArr[43] = 354885;
        iArr[44] = 4894137;
        iArr[45] = 154957;
        iArr[46] = 601410;
        iArr[47] = 2921910;
        iArr[48] = 693578;
        iArr[49] = 8080061;
        iArr[50] = 445009;
        iArr[51] = 742726;
        iArr[52] = 5593787;
        iArr[53] = 318030;
        iArr[54] = 678723;
        iArr[55] = 3484600;
        iArr[56] = 338764;
        iArr[57] = 9082175;
        iArr[58] = 955730;
        iArr[59] = 436808;
        iArr[60] = 7001404;
        iArr[61] = 701775;
        iArr[62] = 308805;
        iArr[63] = 4871993;
        iArr[64] = 677709;
        iArr[65] = 337474;
        iArr[66] = 4100917;
        iArr[67] = 890185;
        iArr[68] = 7711422;
        iArr[69] = 354897;
        iArr[70] = 617798;
        iArr[71] = 5549755;
        iArr[72] = 306511;
        iArr[73] = 675139;
        iArr[74] = 5056183;
        iArr[75] = 861515;
        iArr[76] = 9261759;
        iArr[77] = 742482;
        iArr[78] = 748103;
        iArr[79] = 6909244;
        iArr[80] = 613200;
        iArr[81] = 301893;
        iArr[82] = 4869049;
        iArr[83] = 674637;
        iArr[84] = 11216322;
        iArr[85] = 435540;
        iArr[86] = 447561;
        iArr[87] = 7002685;
        iArr[88] = 702033;
        iArr[89] = 612166;
        iArr[90] = 5543867;
        iArr[91] = 300879;
        iArr[92] = 412484;
        iArr[93] = 3581239;
        iArr[94] = 959818;
        iArr[95] = 8827583;
        iArr[96] = 371795;
        iArr[97] = 702023;
        iArr[98] = 5846716;
        iArr[99] = 601680;
        iArr[100] = 824901;
        iArr[101] = 5065400;
        iArr[102] = 870988;
        iArr[103] = 894273;
        iArr[104] = 2468534;
        iArr[105] = 354889;
        iArr[106] = 8039869;
        iArr[107] = 154962;
        iArr[108] = 601415;
        iArr[109] = 6067642;
        iArr[110] = 693582;
        iArr[111] = 739907;
        iArr[112] = 4937015;
        iArr[113] = 709962;
        iArr[114] = 9788095;
        iArr[115] = 309843;
        iArr[116] = 678728;
        iArr[117] = 6630332;
        iArr[118] = 338768;
        iArr[119] = 693061;
        iArr[120] = 4672185;
        iArr[121] = 436812;
        iArr[122] = 709953;
        iArr[123] = 2415286;
        iArr[124] = 308810;
        iArr[125] = 6969149;
        iArr[126] = 675409;
        iArr[127] = 861766;
        iArr[128] = 6198074;
        iArr[129] = 873293;
        iArr[130] = 371267;
        iArr[131] = 3585335;
        iArr[132] = 617803;
        iArr[133] = 11841215;
        iArr[134] = 306515;
        iArr[135] = 675144;
        iArr[136] = 7153084;
        iArr[137] = 861519;
        iArr[138] = 873028;
        iArr[139] = 6138424;
        iArr[140] = 744012;
        iArr[141] = 355649;
        iArr[142] = 2403766;
        iArr[143] = 301898;
        iArr[144] = 8014782;
        iArr[145] = 674641;
        iArr[146] = 697670;
        iArr[147] = 5984954;
        iArr[148] = 447054;
        iArr[149] = 711234;
        iArr[150] = 3496759;
        iArr[151] = 603979;
        iArr[152] = 8689601;
        iArr[153] = 300883;
        iArr[154] = 412488;
        iArr[155] = 6726972;
        iArr[156] = 959823;
        iArr[157] = 436804;
        iArr[158] = 4896312;
        iArr[159] = 699980;
        iArr[160] = 601666;
        iArr[161] = 3970869;
        iArr[162] = 824905;
        iArr[163] = 8211133;
        iArr[164] = 870993;
        iArr[165] = 894277;
        iArr[166] = 5614266;
        iArr[167] = 354894;
        iArr[168] = 683331;
        iArr[169] = 4533943;
        iArr[170] = 339275;
        iArr[171] = 9082303;
        iArr[172] = 693587;
        iArr[173] = 739911;
        iArr[174] = 7034171;
        iArr[175] = 709967;
        iArr[176] = 350789;
        iArr[177] = 4873528;
        iArr[178] = 678732;
        iArr[179] = 338754;
        iArr[180] = 3838902;
        iArr[181] = 430921;
        iArr[182] = 7809469;
        iArr[183] = 436817;
        iArr[184] = 709958;
        iArr[185] = 5561018;
        iArr[186] = 308814;
        iArr[187] = 677699;
        iArr[188] = 4532024;
        iArr[189] = 861770;
        iArr[190] = 9343806;
        iArr[191] = 873042;
        iArr[192] = 895559;
        iArr[193] = 6731067;
        iArr[194] = 355663;
        iArr[195] = 306757;
        iArr[196] = 4869817;
        iArr[197] = 675148;
        iArr[198] = 857409;
        iArr[199] = 2986677;
        LUNAR_INFO = iArr;
    }

    public static final int[] lunarToSolar(int year, int month, int monthDay, boolean isLeapMonth) {
        int dayOffset;
        if (year < 1900 || year > 2099 || month < 1 || month > 12 || monthDay < 1 || monthDay > 30) {
            throw new IllegalArgumentException("Illegal lunar date, must be like that:\n\tyear : 1900~2099\n\tmonth : 1~12\n\tday : 1~30");
        }
        int[] iArr = LUNAR_INFO;
        int dayOffset2 = (iArr[year - 1900] & 31) - 1;
        if (((iArr[year - 1900] & 96) >> 5) == 2) {
            dayOffset2 += 31;
        }
        for (int i = 1; i < month; i++) {
            if (((524288 >> (i - 1)) & LUNAR_INFO[year - 1900]) == 0) {
                dayOffset += 29;
            } else {
                dayOffset += 30;
            }
        }
        int dayOffset3 = dayOffset + monthDay;
        int leapMonth = (LUNAR_INFO[year - 1900] & 15728640) >> 20;
        if (leapMonth != 0 && (month > leapMonth || (month == leapMonth && isLeapMonth))) {
            dayOffset3 = ((524288 >> (month + -1)) & LUNAR_INFO[year + -1900]) == 0 ? dayOffset3 + 29 : dayOffset3 + 30;
        }
        if (dayOffset3 > 366 || (year % 4 != 0 && dayOffset3 > 365)) {
            year++;
            if (year % 4 == 1) {
                dayOffset3 -= 366;
            } else {
                dayOffset3 -= 365;
            }
        }
        int[] solarInfo = new int[3];
        int i2 = 1;
        while (true) {
            if (i2 >= 13) {
                break;
            }
            int iPos = DAYS_BEFORE_MONTH[i2];
            if (year % 4 == 0 && i2 > 2) {
                iPos++;
            }
            if (year % 4 == 0 && i2 == 2 && iPos + 1 == dayOffset3) {
                solarInfo[1] = i2;
                solarInfo[2] = dayOffset3 - 31;
                break;
            } else if (iPos >= dayOffset3) {
                solarInfo[1] = i2;
                int iPos2 = DAYS_BEFORE_MONTH[i2 - 1];
                if (year % 4 == 0 && i2 > 2) {
                    iPos2++;
                }
                if (dayOffset3 > iPos2) {
                    solarInfo[2] = dayOffset3 - iPos2;
                } else if (dayOffset3 != iPos2) {
                    solarInfo[2] = dayOffset3;
                } else if (year % 4 == 0 && i2 == 2) {
                    int[] iArr2 = DAYS_BEFORE_MONTH;
                    solarInfo[2] = (iArr2[i2] - iArr2[i2 - 1]) + 1;
                } else {
                    int[] iArr3 = DAYS_BEFORE_MONTH;
                    solarInfo[2] = iArr3[i2] - iArr3[i2 - 1];
                }
            } else {
                i2++;
            }
        }
        solarInfo[0] = year;
        return solarInfo;
    }

    public static final int[] solarToLunar(int year, int month, int monthDay) {
        int[] lunarDate = new int[4];
        int[] iArr = solar_1_1;
        int index = year - iArr[0];
        if (iArr[index] > ((year << 9) | (month << 5) | monthDay)) {
            index--;
        }
        int solar11 = solar_1_1[index];
        int y = getBitInt(solar11, 12, 9);
        long offset = solarToInt(year, month, monthDay) - solarToInt(y, getBitInt(solar11, 4, 5), getBitInt(solar11, 5, 0));
        int days = lunar_month_days[index];
        int i = 13;
        int leap = getBitInt(days, 4, 13);
        int lunarY = solar_1_1[0] + index;
        int lunarM = 1;
        long offset2 = offset + 1;
        int i2 = 0;
        while (true) {
            if (i2 >= i) {
                break;
            }
            int dm = getBitInt(days, 1, 12 - i2) == 1 ? 30 : 29;
            int y2 = y;
            if (offset2 <= ((long) dm)) {
                break;
            }
            lunarM++;
            offset2 -= (long) dm;
            i2++;
            y = y2;
            i = 13;
        }
        int lunarD = (int) offset2;
        lunarDate[0] = lunarY;
        lunarDate[1] = lunarM;
        int i3 = 0;
        if (leap != 0 && lunarM > leap) {
            lunarDate[1] = lunarM - 1;
            if (lunarM == leap + 1) {
                i3 = 1;
            }
        }
        lunarDate[2] = lunarD;
        lunarDate[3] = i3;
        return lunarDate;
    }

    @Deprecated
    public static final int[] solarToLunarDeprecated(int year, int month, int monthDay) {
        int[] lunarDate = new int[4];
        int offset = (int) ((new GregorianCalendar(year, month - 1, monthDay).getTime().getTime() - new GregorianCalendar(MIN_YEAR, 0, 31).getTime().getTime()) / 86400000);
        int daysOfYear = 0;
        int iYear = MIN_YEAR;
        while (iYear <= 2099 && offset > 0) {
            daysOfYear = daysInLunarYear(iYear);
            offset -= daysOfYear;
            iYear++;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
        }
        lunarDate[0] = iYear;
        int leapMonth = leapMonth(iYear);
        int i = 0;
        int daysOfMonth = 0;
        int iMonth = 1;
        while (iMonth <= 13 && offset > 0) {
            daysOfMonth = daysInLunarMonth(iYear, iMonth);
            offset -= daysOfMonth;
            iMonth++;
        }
        if (leapMonth != 0 && iMonth > leapMonth) {
            iMonth--;
            Log.i("----------->", year + "-" + month + "-" + monthDay + "====>" + iMonth + "-" + leapMonth);
            if (iMonth == leapMonth) {
                i = 1;
            }
        }
        if (offset < 0) {
            offset += daysOfMonth;
            iMonth--;
        }
        lunarDate[1] = iMonth;
        lunarDate[2] = offset + 1;
        lunarDate[3] = i;
        return lunarDate;
    }

    public static final int daysInMonth(int year, int month) {
        return daysInMonth(year, month, false);
    }

    public static final int daysInMonth(int year, int month, boolean leap) {
        int leapMonth = leapMonth(year);
        int offset = 0;
        if (leapMonth != 0 && month > leapMonth) {
            offset = 1;
        }
        if (!leap) {
            return daysInLunarMonth(year, month + offset);
        }
        if (leapMonth == 0 || leapMonth != month) {
            return 0;
        }
        return daysInLunarMonth(year, month + 1);
    }

    private static int daysInLunarYear(int year) {
        int sum = 348;
        if (leapMonth(year) != 0) {
            sum = 377;
        }
        int monthInfo = LUNAR_INFO[year - 1900] & 1048448;
        for (int i = 524288; i > 7; i >>= 1) {
            if ((monthInfo & i) != 0) {
                sum++;
            }
        }
        return sum;
    }

    private static int daysInLunarMonth(int year, int month) {
        if ((LUNAR_INFO[year - 1900] & (1048576 >> month)) == 0) {
            return 29;
        }
        return 30;
    }

    public static int leapMonth(int year) {
        return (LUNAR_INFO[year - 1900] & 15728640) >> 20;
    }

    private static int getBitInt(int data, int length, int shift) {
        return ((((1 << length) - 1) << shift) & data) >> shift;
    }

    private static long solarToInt(int y, int m, int d) {
        int m2 = (m + 9) % 12;
        int y2 = y - (m2 / 10);
        return (long) ((((y2 * 365) + (y2 / 4)) - (y2 / 100)) + (y2 / 400) + (((m2 * 306) + 5) / 10) + (d - 1));
    }
}
