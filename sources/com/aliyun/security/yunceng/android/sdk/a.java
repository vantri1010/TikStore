package com.aliyun.security.yunceng.android.sdk;

import android.content.Context;

class a {
    private static Context a = null;

    a() {
    }

    public static Context a() {
        if (a == null) {
            try {
                Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
                Object currentActivityThread = ActivityThread.getMethod("currentActivityThread", new Class[0]).invoke(ActivityThread, new Object[0]);
                a = (Context) currentActivityThread.getClass().getMethod("getApplication", new Class[0]).invoke(currentActivityThread, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return a;
    }
}
