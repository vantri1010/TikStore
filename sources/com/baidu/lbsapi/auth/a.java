package com.baidu.lbsapi.auth;

import android.util.Log;

class a {
    public static boolean a = false;
    private static String b = "BaiduApiAuth";

    public static String a() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        return stackTraceElement.getFileName() + "[" + stackTraceElement.getLineNumber() + "]";
    }

    public static void a(String str) {
        if (a && Thread.currentThread().getStackTrace().length != 0) {
            String str2 = b;
            Log.d(str2, a() + ";" + str);
        }
    }

    public static void b(String str) {
        if (Thread.currentThread().getStackTrace().length != 0) {
            Log.i(b, str);
        }
    }

    public static void c(String str) {
        if (a && Thread.currentThread().getStackTrace().length != 0) {
            String str2 = b;
            Log.e(str2, a() + ";" + str);
        }
    }
}
