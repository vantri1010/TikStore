package com.king.zxing.util;

import android.util.Log;

public class LogUtils {
    public static final int ASSERT = 7;
    public static final String COLON = ":";
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static final int PRINTLN = 1;
    public static final String TAG = "ZXingLite";
    public static final String TAG_FORMAT = "%s.%s(L:%d)";
    public static final int VERBOSE = 2;
    public static final String VERTICAL = "|";
    public static final int WARN = 5;
    private static boolean isShowLog = true;
    private static int priority = 1;

    private LogUtils() {
        throw new AssertionError();
    }

    public static void setShowLog(boolean isShowLog2) {
        isShowLog = isShowLog2;
    }

    public static boolean isShowLog() {
        return isShowLog;
    }

    public static int getPriority() {
        return priority;
    }

    public static void setPriority(int priority2) {
        priority = priority2;
    }

    private static String generateTag(StackTraceElement caller) {
        String callerClazzName = caller.getClassName();
        String tag = String.format(TAG_FORMAT, new Object[]{callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1), caller.getMethodName(), Integer.valueOf(caller.getLineNumber())});
        return TAG + VERTICAL + tag;
    }

    public static StackTraceElement getStackTraceElement(int n) {
        return Thread.currentThread().getStackTrace()[n];
    }

    private static String getCallerStackLogTag() {
        return generateTag(getStackTraceElement(5));
    }

    private static String getStackTraceString(Throwable t) {
        return Log.getStackTraceString(t);
    }

    public static void v(String msg) {
        if (isShowLog && priority <= 2) {
            Log.v(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void v(Throwable t) {
        if (isShowLog && priority <= 2) {
            Log.v(getCallerStackLogTag(), getStackTraceString(t));
        }
    }

    public static void v(String msg, Throwable t) {
        if (isShowLog && priority <= 2) {
            Log.v(getCallerStackLogTag(), String.valueOf(msg), t);
        }
    }

    public static void d(String msg) {
        if (isShowLog && priority <= 3) {
            Log.d(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void d(Throwable t) {
        if (isShowLog && priority <= 3) {
            Log.d(getCallerStackLogTag(), getStackTraceString(t));
        }
    }

    public static void d(String msg, Throwable t) {
        if (isShowLog && priority <= 3) {
            Log.d(getCallerStackLogTag(), String.valueOf(msg), t);
        }
    }

    public static void i(String msg) {
        if (isShowLog && priority <= 4) {
            Log.i(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void i(Throwable t) {
        if (isShowLog && priority <= 4) {
            Log.i(getCallerStackLogTag(), getStackTraceString(t));
        }
    }

    public static void i(String msg, Throwable t) {
        if (isShowLog && priority <= 4) {
            Log.i(getCallerStackLogTag(), String.valueOf(msg), t);
        }
    }

    public static void w(String msg) {
        if (isShowLog && priority <= 5) {
            Log.w(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void w(Throwable t) {
        if (isShowLog && priority <= 5) {
            Log.w(getCallerStackLogTag(), getStackTraceString(t));
        }
    }

    public static void w(String msg, Throwable t) {
        if (isShowLog && priority <= 5) {
            Log.w(getCallerStackLogTag(), String.valueOf(msg), t);
        }
    }

    public static void e(String msg) {
        if (isShowLog && priority <= 6) {
            Log.e(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void e(Throwable t) {
        if (isShowLog && priority <= 6) {
            Log.e(getCallerStackLogTag(), getStackTraceString(t));
        }
    }

    public static void e(String msg, Throwable t) {
        if (isShowLog && priority <= 6) {
            Log.e(getCallerStackLogTag(), String.valueOf(msg), t);
        }
    }

    public static void wtf(String msg) {
        if (isShowLog && priority <= 7) {
            Log.wtf(getCallerStackLogTag(), String.valueOf(msg));
        }
    }

    public static void wtf(Throwable t) {
        if (isShowLog && priority <= 7) {
            Log.wtf(getCallerStackLogTag(), getStackTraceString(t));
        }
    }

    public static void wtf(String msg, Throwable t) {
        if (isShowLog && priority <= 7) {
            Log.wtf(getCallerStackLogTag(), String.valueOf(msg), t);
        }
    }

    public static void print(String msg) {
        if (isShowLog && priority <= 1) {
            System.out.print(msg);
        }
    }

    public static void print(Object obj) {
        if (isShowLog && priority <= 1) {
            System.out.print(obj);
        }
    }

    public static void printf(String msg) {
        if (isShowLog && priority <= 1) {
            System.out.printf(msg, new Object[0]);
        }
    }

    public static void println(String msg) {
        if (isShowLog && priority <= 1) {
            System.out.println(msg);
        }
    }

    public static void println(Object obj) {
        if (isShowLog && priority <= 1) {
            System.out.println(obj);
        }
    }
}
