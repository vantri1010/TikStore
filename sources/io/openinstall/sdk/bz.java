package io.openinstall.sdk;

public class bz {
    public static String a(String str) {
        try {
            return new String(ab.c().a(str), ac.c);
        } catch (Exception e) {
            return str;
        }
    }

    public static String b(String str) {
        try {
            return new String(ab.d().a(str), ac.c);
        } catch (Exception e) {
            return str;
        }
    }

    public static String c(String str) {
        try {
            return new String(ab.a().a(str.getBytes(ac.c)), ac.c);
        } catch (Exception e) {
            return str;
        }
    }
}
