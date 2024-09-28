package com.litesuits.orm.log;

import android.util.Log;

public final class OrmLog {
    private static String defaultTag = "OrmLog";
    public static boolean isPrint = false;

    private OrmLog() {
    }

    public static void setTag(String tag) {
        defaultTag = tag;
    }

    public static int i(Object o) {
        if (!isPrint || o == null) {
            return -1;
        }
        return Log.i(defaultTag, o.toString());
    }

    public static int i(String m) {
        if (!isPrint || m == null) {
            return -1;
        }
        return Log.i(defaultTag, m);
    }

    public static int v(String tag, String msg) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.v(tag, msg);
    }

    public static int d(String tag, String msg) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.d(tag, msg);
    }

    public static int i(String tag, String msg) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.i(tag, msg);
    }

    public static int w(String tag, String msg) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.w(tag, msg);
    }

    public static int e(String tag, String msg) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.e(tag, msg);
    }

    public static int v(String tag, Object... msg) {
        if (isPrint) {
            return Log.v(tag, getLogMessage(msg));
        }
        return -1;
    }

    public static int d(String tag, Object... msg) {
        if (isPrint) {
            return Log.d(tag, getLogMessage(msg));
        }
        return -1;
    }

    public static int i(String tag, Object... msg) {
        if (isPrint) {
            return Log.i(tag, getLogMessage(msg));
        }
        return -1;
    }

    public static int w(String tag, Object... msg) {
        if (isPrint) {
            return Log.w(tag, getLogMessage(msg));
        }
        return -1;
    }

    public static int e(String tag, Object... msg) {
        if (isPrint) {
            return Log.e(tag, getLogMessage(msg));
        }
        return -1;
    }

    private static String getLogMessage(Object... msg) {
        if (msg == null || msg.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object s : msg) {
            if (s != null) {
                sb.append(s.toString());
            }
        }
        return sb.toString();
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.v(tag, msg, tr);
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.d(tag, msg, tr);
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.i(tag, msg, tr);
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.w(tag, msg, tr);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (!isPrint || msg == null) {
            return -1;
        }
        return Log.e(tag, msg, tr);
    }

    public static int v(Object tag, String msg) {
        if (isPrint) {
            return Log.v(tag.getClass().getSimpleName(), msg);
        }
        return -1;
    }

    public static int d(Object tag, String msg) {
        if (isPrint) {
            return Log.d(tag.getClass().getSimpleName(), msg);
        }
        return -1;
    }

    public static int i(Object tag, String msg) {
        if (isPrint) {
            return Log.i(tag.getClass().getSimpleName(), msg);
        }
        return -1;
    }

    public static int w(Object tag, String msg) {
        if (isPrint) {
            return Log.w(tag.getClass().getSimpleName(), msg);
        }
        return -1;
    }

    public static int e(Object tag, String msg) {
        if (isPrint) {
            return Log.e(tag.getClass().getSimpleName(), msg);
        }
        return -1;
    }
}
