package io.openinstall.sdk;

public class au {
    private static final String[] a = {"openinstall.io", "deepinstall.com"};
    private static final String[] b = {"openinstall.io", "openlink.cc"};
    private static int c = 0;

    public static void a() {
        c = (c + 1) % a.length;
    }

    public static boolean a(String str) {
        for (String endsWith : b) {
            if (str.endsWith(endsWith)) {
                return true;
            }
        }
        return false;
    }

    public static String b() {
        return "api2." + a[c];
    }

    public static String c() {
        return "stat2." + a[c];
    }
}
