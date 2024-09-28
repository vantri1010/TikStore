package com.baidu.mapsdkplatform.comapi.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.baidu.android.bbalbs.common.util.CommonParam;
import com.baidu.mapapi.VersionInfo;
import com.baidu.mapsdkplatform.comjni.util.AppMD5;
import com.baidu.mapsdkplatform.comjni.util.a;
import im.bclpbkiauv.messenger.BuildConfig;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.webrtc.utils.RecvStatsLogKey;
import org.webrtc.utils.RecvStatsReportCommon;

public class h {
    private static String A = "";
    private static Map<String, String> B = new HashMap();
    public static Context a;
    public static final int b = Integer.parseInt(Build.VERSION.SDK);
    public static float c = 1.0f;
    public static String d;
    private static final String e = h.class.getSimpleName();
    private static a f = new a();
    private static String g = "02";
    private static String h;
    private static String i;
    private static String j;
    private static String k;
    private static int l;
    private static int m;
    private static int n;
    private static int o;
    private static int p;
    private static int q;
    private static String r;
    private static String s = "baidu";
    private static String t = "";
    private static String u = "";
    private static String v = "";
    private static String w;
    private static String x;
    private static String y = "-1";
    private static String z = "-1";

    public static void a() {
        d();
    }

    public static void a(String str) {
        r = str;
        f();
    }

    public static void a(String str, String str2) {
        y = str2;
        z = str;
        f();
    }

    public static byte[] a(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures[0].toByteArray();
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static Bundle b() {
        Bundle bundle = new Bundle();
        bundle.putString("cpu", t);
        bundle.putString("resid", g);
        bundle.putString("channel", s);
        bundle.putString("glr", u);
        bundle.putString("glv", v);
        bundle.putString("mb", g());
        bundle.putString("sv", i());
        bundle.putString(RecvStatsLogKey.KEY_OS, k());
        bundle.putInt("dpi_x", l());
        bundle.putInt("dpi_y", l());
        bundle.putString("net", r);
        bundle.putString("cuid", A);
        bundle.putByteArray("signature", a(a));
        bundle.putString("pcn", a.getPackageName());
        bundle.putInt("screen_x", h());
        bundle.putInt("screen_y", j());
        a aVar = f;
        if (aVar != null) {
            aVar.a(bundle);
        }
        return bundle;
    }

    public static void b(Context context) {
        a = context;
        if (context.getFilesDir() != null) {
            w = context.getFilesDir().getAbsolutePath();
        }
        if (context.getCacheDir() != null) {
            x = context.getCacheDir().getAbsolutePath();
        }
        i = Build.MODEL;
        j = RecvStatsReportCommon.sdk_platform + Build.VERSION.SDK;
        h = context.getPackageName();
        c(context);
        d(context);
        q();
        A = p();
        B.put("resid", AppMD5.encodeUrlParamsValue(g));
        B.put("channel", AppMD5.encodeUrlParamsValue(m()));
        B.put("mb", AppMD5.encodeUrlParamsValue(g()));
        B.put("sv", AppMD5.encodeUrlParamsValue(i()));
        B.put(RecvStatsLogKey.KEY_OS, AppMD5.encodeUrlParamsValue(k()));
        B.put("dpi", AppMD5.encodeUrlParamsValue(String.format("%d,%d", new Object[]{Integer.valueOf(l()), Integer.valueOf(l())})));
        B.put("cuid", AppMD5.encodeUrlParamsValue(A));
        B.put("pcn", AppMD5.encodeUrlParamsValue(a.getPackageName()));
        B.put("screen", AppMD5.encodeUrlParamsValue(String.format("%d,%d", new Object[]{Integer.valueOf(h()), Integer.valueOf(j())})));
        a aVar = f;
        if (aVar != null) {
            aVar.a();
        }
    }

    public static String c() {
        if (B == null) {
            return null;
        }
        Date date = new Date();
        long time = date.getTime() + ((long) (date.getSeconds() * 1000));
        B.put("ctm", AppMD5.encodeUrlParamsValue(String.format("%f", new Object[]{Double.valueOf(((double) (time / 1000)) + (((double) (time % 1000)) / 1000.0d))})));
        StringBuilder sb = new StringBuilder();
        for (Map.Entry next : B.entrySet()) {
            sb.append("&");
            sb.append((String) next.getKey());
            sb.append("=");
            sb.append((String) next.getValue());
        }
        return sb.toString();
    }

    private static void c(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String apiVersion = VersionInfo.getApiVersion();
            k = apiVersion;
            if (apiVersion != null && !apiVersion.equals("")) {
                k = k.replace('_', '.');
            }
            l = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e2) {
            k = BuildConfig.VERSION_NAME;
            l = 1;
        }
    }

    public static void d() {
        a aVar = f;
        if (aVar != null) {
            aVar.b();
        }
    }

    private static void d(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display defaultDisplay = windowManager != null ? windowManager.getDefaultDisplay() : null;
        if (defaultDisplay != null) {
            m = defaultDisplay.getWidth();
            n = defaultDisplay.getHeight();
            defaultDisplay.getMetrics(displayMetrics);
        }
        c = displayMetrics.density;
        o = (int) displayMetrics.xdpi;
        p = (int) displayMetrics.ydpi;
        if (b > 3) {
            q = displayMetrics.densityDpi;
        } else {
            q = 160;
        }
        if (q == 0) {
            q = 160;
        }
    }

    public static String e() {
        return r;
    }

    public static void f() {
        B.put("net", AppMD5.encodeUrlParamsValue(e()));
        B.put("appid", AppMD5.encodeUrlParamsValue(y));
        B.put("bduid", "");
        if (f != null) {
            Bundle bundle = new Bundle();
            bundle.putString("cpu", t);
            bundle.putString("resid", g);
            bundle.putString("channel", s);
            bundle.putString("glr", u);
            bundle.putString("glv", v);
            bundle.putString("mb", g());
            bundle.putString("sv", i());
            bundle.putString(RecvStatsLogKey.KEY_OS, k());
            bundle.putInt("dpi_x", l());
            bundle.putInt("dpi_y", l());
            bundle.putString("net", r);
            bundle.putString("cuid", A);
            bundle.putString("pcn", a.getPackageName());
            bundle.putInt("screen_x", h());
            bundle.putInt("screen_y", j());
            bundle.putString("appid", y);
            bundle.putString("duid", z);
            if (!TextUtils.isEmpty(d)) {
                bundle.putString("token", d);
            }
            f.a(bundle);
            SysUpdateObservable.getInstance().updatePhoneInfo();
        }
    }

    public static String g() {
        return i;
    }

    public static int h() {
        return m;
    }

    public static String i() {
        return k;
    }

    public static int j() {
        return n;
    }

    public static String k() {
        return j;
    }

    public static int l() {
        return q;
    }

    public static String m() {
        return s;
    }

    public static String n() {
        return h;
    }

    public static String o() {
        return w;
    }

    public static String p() {
        String str;
        try {
            str = CommonParam.getCUID(a);
        } catch (Exception e2) {
            str = "";
        }
        return str == null ? "" : str;
    }

    private static void q() {
        r = "0";
    }
}
