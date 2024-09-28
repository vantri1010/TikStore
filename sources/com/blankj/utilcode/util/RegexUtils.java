package com.blankj.utilcode.util;

import androidx.collection.SimpleArrayMap;
import com.blankj.utilcode.constant.RegexConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtils {
    private static final SimpleArrayMap<String, String> CITY_MAP = new SimpleArrayMap<>();

    private RegexUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isMobileSimple(CharSequence input) {
        return isMatch(RegexConstants.REGEX_MOBILE_SIMPLE, input);
    }

    public static boolean isMobileExact(CharSequence input) {
        return isMatch(RegexConstants.REGEX_MOBILE_EXACT, input);
    }

    public static boolean isTel(CharSequence input) {
        return isMatch(RegexConstants.REGEX_TEL, input);
    }

    public static boolean isIDCard15(CharSequence input) {
        return isMatch(RegexConstants.REGEX_ID_CARD15, input);
    }

    public static boolean isIDCard18(CharSequence input) {
        return isMatch(RegexConstants.REGEX_ID_CARD18, input);
    }

    public static boolean isIDCard18Exact(CharSequence input) {
        if (isIDCard18(input)) {
            int[] factor = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
            char[] suffix = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
            if (CITY_MAP.isEmpty()) {
                CITY_MAP.put("11", "北京");
                CITY_MAP.put("12", "天津");
                CITY_MAP.put("13", "河北");
                CITY_MAP.put("14", "山西");
                CITY_MAP.put("15", "内蒙古");
                CITY_MAP.put("21", "辽宁");
                CITY_MAP.put("22", "吉林");
                CITY_MAP.put("23", "黑龙江");
                CITY_MAP.put("31", "上海");
                CITY_MAP.put("32", "江苏");
                CITY_MAP.put("33", "浙江");
                CITY_MAP.put("34", "安徽");
                CITY_MAP.put("35", "福建");
                CITY_MAP.put("36", "江西");
                CITY_MAP.put("37", "山东");
                CITY_MAP.put("41", "河南");
                CITY_MAP.put("42", "湖北");
                CITY_MAP.put("43", "湖南");
                CITY_MAP.put("44", "广东");
                CITY_MAP.put("45", "广西");
                CITY_MAP.put("46", "海南");
                CITY_MAP.put("50", "重庆");
                CITY_MAP.put("51", "四川");
                CITY_MAP.put("52", "贵州");
                CITY_MAP.put("53", "云南");
                CITY_MAP.put("54", "西藏");
                CITY_MAP.put("61", "陕西");
                CITY_MAP.put("62", "甘肃");
                CITY_MAP.put("63", "青海");
                CITY_MAP.put("64", "宁夏");
                CITY_MAP.put("65", "新疆");
                CITY_MAP.put("71", "台湾");
                CITY_MAP.put("81", "香港");
                CITY_MAP.put("82", "澳门");
                CITY_MAP.put("91", "国外");
            }
            if (CITY_MAP.get(input.subSequence(0, 2).toString()) != null) {
                int weightSum = 0;
                for (int i = 0; i < 17; i++) {
                    weightSum += (input.charAt(i) - '0') * factor[i];
                }
                if (input.charAt(17) == suffix[weightSum % 11]) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public static boolean isEmail(CharSequence input) {
        return isMatch(RegexConstants.REGEX_EMAIL, input);
    }

    public static boolean isURL(CharSequence input) {
        return isMatch(RegexConstants.REGEX_URL, input);
    }

    public static boolean isZh(CharSequence input) {
        return isMatch(RegexConstants.REGEX_ZH, input);
    }

    public static boolean isUsername(CharSequence input) {
        return isMatch(RegexConstants.REGEX_USERNAME, input);
    }

    public static boolean isDate(CharSequence input) {
        return isMatch(RegexConstants.REGEX_DATE, input);
    }

    public static boolean isIP(CharSequence input) {
        return isMatch(RegexConstants.REGEX_IP, input);
    }

    public static boolean isMatch(String regex, CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    public static List<String> getMatches(String regex, CharSequence input) {
        if (input == null) {
            return Collections.emptyList();
        }
        List<String> matches = new ArrayList<>();
        Matcher matcher = Pattern.compile(regex).matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    public static String[] getSplits(String input, String regex) {
        if (input == null) {
            return new String[0];
        }
        return input.split(regex);
    }

    public static String getReplaceFirst(String input, String regex, String replacement) {
        if (input == null) {
            return "";
        }
        return Pattern.compile(regex).matcher(input).replaceFirst(replacement);
    }

    public static String getReplaceAll(String input, String regex, String replacement) {
        if (input == null) {
            return "";
        }
        return Pattern.compile(regex).matcher(input).replaceAll(replacement);
    }
}
