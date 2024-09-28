package com.fm.openinstall;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import io.openinstall.sdk.af;
import io.openinstall.sdk.ah;
import io.openinstall.sdk.al;
import io.openinstall.sdk.au;
import io.openinstall.sdk.ca;
import io.openinstall.sdk.cb;
import io.openinstall.sdk.k;

public final class OpenInstallHelper {
    private OpenInstallHelper() {
    }

    public static String checkGaid(Context context) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (cb.a) {
                cb.b("不能在主线程调用", new Object[0]);
            }
            return null;
        }
        af.a a = af.a(context.getApplicationContext());
        if (a != null) {
            return a.a();
        }
        return null;
    }

    public static String checkOaid(Context context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            ah ahVar = new ah();
            ahVar.a(context.getApplicationContext());
            return ahVar.a();
        } else if (!cb.a) {
            return null;
        } else {
            cb.b("不能在主线程调用", new Object[0]);
            return null;
        }
    }

    public static boolean checkSimulator(Context context) {
        return k.a().a(context);
    }

    public static boolean isLauncherFromYYB(Activity activity, Intent intent) {
        Uri referrer;
        boolean z = false;
        if (activity == null || intent == null || TextUtils.isEmpty(intent.getAction()) || intent.getCategories() == null || !intent.getAction().equals("android.intent.action.MAIN") || !intent.getCategories().contains("android.intent.category.LAUNCHER") || Build.VERSION.SDK_INT < 22 || (referrer = activity.getReferrer()) == null) {
            return false;
        }
        String authority = referrer.getAuthority();
        if (TextUtils.isEmpty(authority)) {
            return false;
        }
        if (authority.equalsIgnoreCase(ca.o) || authority.equalsIgnoreCase(ca.p) || authority.equalsIgnoreCase(ca.n)) {
            z = true;
        }
        if (authority.equalsIgnoreCase(ca.q) || authority.equalsIgnoreCase(ca.r) || authority.equalsIgnoreCase(ca.s)) {
            return true;
        }
        return z;
    }

    public static boolean isSchemeWakeup(Intent intent) {
        if (intent == null || intent.getData() == null || intent.getAction() == null) {
            return false;
        }
        String action = intent.getAction();
        String host = intent.getData().getHost();
        if (TextUtils.isEmpty(host) || !action.equals("android.intent.action.VIEW")) {
            return false;
        }
        return au.a(host);
    }

    public static boolean isTrackData(ClipData clipData) {
        al a = al.a(clipData);
        if (a == null) {
            return false;
        }
        return a.c(1) || a.c(2);
    }
}
