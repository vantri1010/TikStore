package im.bclpbkiauv.messenger.utils;

import android.os.Build;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.zxing.common.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import kotlin.UByte;

public class DataTools {
    private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("###,###,###,##0.00");
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static String[] pyStr = {"a", "ai", "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian", "biao", "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai", "chan", "chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu", "cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de", "deng", "di", "dian", "diao", "die", "ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo", "e", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo", "fou", "fu", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo", "ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun", "huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan", "kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia", "lian", "liang", "liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", "lv", "luan", "lue", "lun", "luo", "ma", "mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai", "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang", "niao", "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan", "nue", "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pu", "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", "qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao", "re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan", "shang", "shao", "she", "shen", "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang", "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui", "sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya", "yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang", "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo"};
    private static int[] pyValue = {-20319, -20317, -20304, -20295, -20292, -20283, -20265, -20257, -20242, -20230, -20051, -20036, -20032, -20026, -20002, -19990, -19986, -19982, -19976, -19805, -19784, -19775, -19774, -19763, -19756, -19751, -19746, -19741, -19739, -19728, -19725, -19715, -19540, -19531, -19525, -19515, -19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275, -19270, -19263, -19261, -19249, -19243, -19242, -19238, -19235, -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006, -19003, -18996, -18977, -18961, -18952, -18783, -18774, -18773, -18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697, -18696, -18526, -18518, -18501, -18490, -18478, -18463, -18448, -18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201, -18184, -18183, -18181, -18012, -17997, -17988, -17970, -17964, -17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752, -17733, -17730, -17721, -17703, -17701, -17697, -17692, -17683, -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427, -17417, -17202, -17185, -16983, -16970, -16942, -16915, -16733, -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470, -16465, -16459, -16452, -16448, -16433, -16429, -16427, -16423, -16419, -16412, -16407, -16403, -16401, -16393, -16220, -16216, -16212, -16205, -16202, -16187, -16180, -16171, -16169, -16158, -16155, -15959, -15958, -15944, -15933, -15920, -15915, -15903, -15889, -15878, -15707, -15701, -15681, -15667, -15661, -15659, -15652, -15640, -15631, -15625, -15454, -15448, -15436, -15435, -15419, -15416, -15408, -15394, -15385, -15377, -15375, -15369, -15363, -15362, -15183, -15180, -15165, -15158, -15153, -15150, -15149, -15144, -15143, -15141, -15140, -15139, -15128, -15121, -15119, -15117, -15110, -15109, -14941, -14937, -14933, -14930, -14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902, -14894, -14889, -14882, -14873, -14871, -14857, -14678, -14674, -14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429, -14407, -14399, -14384, -14379, -14368, -14355, -14353, -14345, -14170, -14159, -14151, -14149, -14145, -14140, -14137, -14135, -14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094, -14092, -14090, -14087, -14083, -13917, -13914, -13910, -13907, -13906, -13905, -13896, -13894, -13878, -13870, -13859, -13847, -13831, -13658, -13611, -13601, -13406, -13404, -13400, -13398, -13395, -13391, -13387, -13383, -13367, -13359, -13356, -13343, -13340, -13329, -13326, -13318, -13147, -13138, -13120, -13107, -13096, -13095, -13091, -13076, -13068, -13063, -13060, -12888, -12875, -12871, -12860, -12858, -12852, -12849, -12838, -12831, -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556, -12359, -12346, -12320, -12300, -12120, -12099, -12089, -12074, -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798, -11781, -11604, -11589, -11536, -11358, -11340, -11339, -11324, -11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041, -11038, -11024, -11020, -11019, -11018, -11014, -10838, -10832, -10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533, -10519, -10331, -10329, -10328, -10322, -10315, -10309, -10307, -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254};

    public static boolean isNullString(String str) {
        return str == null || str.length() == 0 || "null".equals(str);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if ((obj instanceof String) && obj.toString().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if ((obj instanceof Collection) && ((Collection) obj).isEmpty()) {
            return true;
        }
        if ((obj instanceof Map) && ((Map) obj).isEmpty()) {
            return true;
        }
        if ((obj instanceof SparseArray) && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if ((obj instanceof SparseBooleanArray) && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if ((obj instanceof SparseIntArray) && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT < 18 || !(obj instanceof SparseLongArray) || ((SparseLongArray) obj).size() != 0) {
            return false;
        }
        return true;
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return value.contains(".");
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }

    public static String getAstro(int month, int day) {
        String[] starArr = {"魔羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座"};
        int[] DayArr = {22, 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22};
        if (month <= 0 || day <= 0 || month > 12 || day > 31) {
            return "猴年马月座";
        }
        int index = month;
        if (day < DayArr[month - 1]) {
            index--;
        }
        return starArr[index % 12];
    }

    public static String getAnimalYearName(int year) {
        return new String[]{"猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊"}[year % 12];
    }

    public static String hideMobilePhone4(String mobile_phone) {
        if (mobile_phone.length() != 11) {
            return "手机号码不正确";
        }
        return mobile_phone.substring(0, 3) + "****" + mobile_phone.substring(7, 11);
    }

    public static String formatCard(String cardNo) {
        if (cardNo.length() < 8) {
            return "银行卡号有误";
        }
        return (cardNo.substring(0, 4) + " **** **** ") + cardNo.substring(cardNo.length() - 4);
    }

    public static String formatCardEnd4(String cardNo) {
        if (cardNo.length() < 8) {
            return "银行卡号有误";
        }
        return "" + cardNo.substring(cardNo.length() - 4);
    }

    public static int stringToInt(String str) {
        if (isNullString(str)) {
            return 0;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int intsGetSum(int[] ints) {
        int sum = 0;
        for (int i : ints) {
            sum += i;
        }
        return sum;
    }

    public static long stringToLong(String str) {
        if (isNullString(str)) {
            return 0;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double stringToDouble(String str) {
        if (isNullString(str)) {
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
    }

    public static float stringToFloat(String str) {
        if (isNullString(str)) {
            return 0.0f;
        }
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    public static String format2Decimals(String str) {
        return String.valueOf(new BigDecimal(str).setScale(2, 4));
    }

    public static InputStream StringToInputStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    public static String upperFirstLetter(String s) {
        if (isNullString(s) || !Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - ' ')) + s.substring(1);
    }

    public static String lowerFirstLetter(String s) {
        if (isNullString(s) || !Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + ' ')) + s.substring(1);
    }

    public static String reverse(String s) {
        int len = s.length();
        if (len <= 1) {
            return s;
        }
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        for (int i = 0; i < mid; i++) {
            char c = chars[i];
            chars[i] = chars[(len - i) - 1];
            chars[(len - i) - 1] = c;
        }
        return new String(chars);
    }

    public static String toDBC(String s) {
        if (isNullString(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 > chars[i] || chars[i] > 65374) {
                chars[i] = chars[i];
            } else {
                chars[i] = (char) (chars[i] - 65248);
            }
        }
        return new String(chars);
    }

    public static String toSBC(String s) {
        if (isNullString(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = 12288;
            } else if ('!' > chars[i] || chars[i] > '~') {
                chars[i] = chars[i];
            } else {
                chars[i] = (char) (chars[i] + 65248);
            }
        }
        return new String(chars);
    }

    public static int oneCn2ASCII(String s) {
        if (s.length() != 1) {
            return -1;
        }
        int ascii = 0;
        try {
            byte[] bytes = s.getBytes(StringUtils.GB2312);
            if (bytes.length == 1) {
                ascii = bytes[0];
            } else if (bytes.length == 2) {
                ascii = (((bytes[0] + UByte.MIN_VALUE) * 256) + (bytes[1] + UByte.MIN_VALUE)) - 65536;
            } else {
                throw new IllegalArgumentException("Illegal resource string");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ascii;
    }

    public static String oneCn2PY(String s) {
        int ascii = oneCn2ASCII(s);
        if (ascii == -1) {
            return null;
        }
        if (ascii >= 0 && ascii <= 127) {
            return String.valueOf((char) ascii);
        }
        for (int i = pyValue.length - 1; i >= 0; i--) {
            if (pyValue[i] <= ascii) {
                return pyStr[i];
            }
        }
        return null;
    }

    public static String getPYFirstLetter(String s) {
        if (isNullString(s)) {
            return "";
        }
        String py = oneCn2PY(s.substring(0, 1));
        if (py == null) {
            return null;
        }
        return py.substring(0, 1);
    }

    public static String getPYAllFirstLetter(String s) {
        if (isNullString(s)) {
            return "";
        }
        String py = "";
        for (int i = 0; i < s.length(); i++) {
            String py1 = oneCn2PY(s.substring(i, i + 1));
            if (py1 != null) {
                py = py + py1.substring(0, 1);
            }
        }
        if (py == "") {
            return null;
        }
        return py;
    }

    public static String cn2PY(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            String py = oneCn2PY(s.substring(i, i + 1));
            if (py == null) {
                py = "?";
            }
            sb.append(py);
        }
        return sb.toString();
    }

    public static String bytes2HexString(byte[] bytes) {
        char[] ret = new char[(bytes.length << 1)];
        int j = 0;
        for (int i = 0; i < bytes.length; i++) {
            int j2 = j + 1;
            char[] cArr = HEX_DIGITS;
            ret[j] = cArr[(bytes[i] >>> 4) & 15];
            j = j2 + 1;
            ret[j2] = cArr[bytes[i] & 15];
        }
        return new String(ret);
    }

    public static byte[] hexString2Bytes(String hexString) {
        int len = hexString.length();
        if (len % 2 == 0) {
            char[] hexBytes = hexString.toUpperCase().toCharArray();
            byte[] ret = new byte[(len >>> 1)];
            for (int i = 0; i < len; i += 2) {
                ret[i >> 1] = (byte) ((hex2Dec(hexBytes[i]) << 4) | hex2Dec(hexBytes[i + 1]));
            }
            return ret;
        }
        throw new IllegalArgumentException("长度不是偶数");
    }

    private static int hex2Dec(char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        }
        if (hexChar >= 'A' && hexChar <= 'F') {
            return (hexChar - 'A') + 10;
        }
        throw new IllegalArgumentException();
    }

    public static byte[] chars2Bytes(char[] chars) {
        int len = chars.length;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    public static char[] bytes2Chars(byte[] bytes) {
        int len = bytes.length;
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = (char) (bytes[i] & UByte.MAX_VALUE);
        }
        return chars;
    }

    public static InputStream bytes2InputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    public static byte[] outputStream2Bytes(OutputStream out) {
        if (out == null) {
            return null;
        }
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    public static InputStream string2InputStream(String string, String charsetName) {
        if (string == null || isNullString(charsetName)) {
            return null;
        }
        try {
            return new ByteArrayInputStream(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String outputStream2String(OutputStream out, String charsetName) {
        if (out == null) {
            return null;
        }
        try {
            return new String(outputStream2Bytes(out), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAmountValue(double value) {
        return AMOUNT_FORMAT.format(value);
    }

    public static String getAmountValue(String value) {
        if (isNullString(value)) {
            return "0";
        }
        return AMOUNT_FORMAT.format(Double.parseDouble(value));
    }

    public static String getRoundUp(BigDecimal value, int digit) {
        return value.setScale(digit, 4).toString();
    }

    public static String getRoundUp(double value, int digit) {
        return new BigDecimal(value).setScale(digit, 4).toString();
    }

    public static String getRoundUp(String value, int digit) {
        if (isNullString(value)) {
            return "0";
        }
        return new BigDecimal(Double.parseDouble(value)).setScale(digit, 4).toString();
    }

    public static String getPercentValue(BigDecimal value, int digit) {
        return getRoundUp(value.multiply(new BigDecimal(100)), digit);
    }

    public static String getPercentValue(double value, int digit) {
        return getPercentValue(new BigDecimal(value), digit);
    }

    public static String getPercentValue(double value) {
        return getPercentValue(new BigDecimal(value), 2);
    }

    public ByteArrayInputStream output2InputStream(OutputStream out) {
        if (out == null) {
            return null;
        }
        return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
    }
}
