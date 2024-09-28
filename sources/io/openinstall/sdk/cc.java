package io.openinstall.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class cc {
    private static Object a(Context context, String str) {
        try {
            Bundle bundle = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData;
            if (bundle != null) {
                return bundle.get(str);
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String a(Context context) {
        Object a = a(context, "com.openinstall.APP_KEY");
        if (a == null) {
            return "";
        }
        try {
            return String.valueOf(a);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean b(Context context) {
        Object a = a(context, "com.openinstall.PB_ENABLED");
        if (a == null) {
            return true;
        }
        try {
            return Boolean.parseBoolean(String.valueOf(a));
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean c(Context context) {
        Object a = a(context, "com.openinstall.PB_SIGNAL");
        if (a == null) {
            return true;
        }
        try {
            return Boolean.parseBoolean(String.valueOf(a));
        } catch (Exception e) {
            return true;
        }
    }
}
