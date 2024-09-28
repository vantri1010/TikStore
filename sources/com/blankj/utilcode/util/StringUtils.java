package com.blankj.utilcode.util;

import android.content.res.Resources;

public final class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean isTrimEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(CharSequence s1, CharSequence s2) {
        if (s1 == s2) {
            return true;
        }
        if (!(s1 == null || s2 == null)) {
            int length = s1.length();
            int length2 = length;
            if (length == s2.length()) {
                if ((s1 instanceof String) && (s2 instanceof String)) {
                    return s1.equals(s2);
                }
                for (int i = 0; i < length2; i++) {
                    if (s1.charAt(i) != s2.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean equalsIgnoreCase(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        }
        return s1.equalsIgnoreCase(s2);
    }

    public static String null2Length0(String s) {
        return s == null ? "" : s;
    }

    public static int length(CharSequence s) {
        if (s == null) {
            return 0;
        }
        return s.length();
    }

    public static String upperFirstLetter(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - ' ')) + s.substring(1);
    }

    public static String lowerFirstLetter(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + ' ')) + s.substring(1);
    }

    public static String reverse(String s) {
        if (s == null) {
            return "";
        }
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
        if (s == null || s.length() == 0) {
            return "";
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
        if (s == null || s.length() == 0) {
            return "";
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

    public static String getString(int id) {
        try {
            return Utils.getApp().getResources().getString(id);
        } catch (Resources.NotFoundException e) {
            return "";
        }
    }

    public static String getString(int id, Object... formatArgs) {
        try {
            return Utils.getApp().getString(id, formatArgs);
        } catch (Resources.NotFoundException e) {
            return "";
        }
    }

    public static String[] getStringArray(int id) {
        try {
            return Utils.getApp().getResources().getStringArray(id);
        } catch (Resources.NotFoundException e) {
            return new String[0];
        }
    }
}
