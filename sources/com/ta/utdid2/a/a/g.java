package com.ta.utdid2.a.a;

import java.util.regex.Pattern;

public class g {
    private static final Pattern a = Pattern.compile("([\t\r\n])+");

    /* renamed from: a  reason: collision with other method in class */
    public static boolean m1a(String str) {
        if (str == null || str.length() <= 0) {
            return true;
        }
        return false;
    }

    public static int a(String str) {
        if (str.length() <= 0) {
            return 0;
        }
        int i = 0;
        for (char c : str.toCharArray()) {
            i = (i * 31) + c;
        }
        return i;
    }
}
