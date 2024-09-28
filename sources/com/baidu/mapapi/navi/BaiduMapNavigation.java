package com.baidu.mapapi.navi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.b;
import com.google.android.exoplayer2.C;

public class BaiduMapNavigation {
    private static boolean a = true;

    private static String a(Context context) {
        PackageManager packageManager;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            try {
                applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
            }
        } catch (PackageManager.NameNotFoundException e2) {
            packageManager = null;
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x00b8 A[Catch:{ JSONException -> 0x00ea }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00be A[Catch:{ JSONException -> 0x00ea }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00f4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void a(com.baidu.mapapi.navi.NaviParaOption r17, android.content.Context r18) throws com.baidu.mapapi.navi.IllegalNaviArgumentException {
        /*
            r0 = r17
            r1 = r18
            java.lang.String r2 = ","
            java.lang.String r3 = "xy"
            java.lang.String r4 = "1"
            java.lang.String r5 = "type"
            if (r0 == 0) goto L_0x0135
            if (r1 == 0) goto L_0x0135
            com.baidu.mapapi.model.LatLng r6 = r0.a
            if (r6 == 0) goto L_0x012d
            com.baidu.mapapi.model.LatLng r6 = r0.c
            if (r6 == 0) goto L_0x012d
            com.baidu.mapapi.model.LatLng r6 = r0.a
            com.baidu.mapapi.model.inner.GeoPoint r6 = com.baidu.mapapi.model.CoordUtil.ll2mc(r6)
            com.baidu.mapapi.model.LatLng r7 = r0.c
            com.baidu.mapapi.model.inner.GeoPoint r7 = com.baidu.mapapi.model.CoordUtil.ll2mc(r7)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "http://app.navi.baidu.com/mobile/#navi/naving/"
            r8.append(r9)
            java.lang.String r9 = "&sy=0"
            r8.append(r9)
            java.lang.String r9 = "&endp="
            r8.append(r9)
            java.lang.String r9 = "&start="
            r8.append(r9)
            java.lang.String r9 = "&startwd="
            r8.append(r9)
            java.lang.String r9 = "&endwd="
            r8.append(r9)
            java.lang.String r9 = "&fromprod=map_sdk"
            r8.append(r9)
            java.lang.String r9 = "&app_version="
            r8.append(r9)
            java.lang.String r9 = "6_2_0"
            r8.append(r9)
            org.json.JSONArray r9 = new org.json.JSONArray
            r9.<init>()
            org.json.JSONObject r10 = new org.json.JSONObject
            r10.<init>()
            org.json.JSONObject r11 = new org.json.JSONObject
            r11.<init>()
            r10.put(r5, r4)     // Catch:{ JSONException -> 0x00ea }
            java.lang.String r12 = r0.b     // Catch:{ JSONException -> 0x00ea }
            java.lang.String r13 = ""
            java.lang.String r14 = "keyword"
            if (r12 == 0) goto L_0x007e
            java.lang.String r12 = r0.b     // Catch:{ JSONException -> 0x00ea }
            boolean r12 = r12.equals(r13)     // Catch:{ JSONException -> 0x00ea }
            if (r12 != 0) goto L_0x007e
            java.lang.String r12 = r0.b     // Catch:{ JSONException -> 0x00ea }
            r10.put(r14, r12)     // Catch:{ JSONException -> 0x00ea }
            goto L_0x0081
        L_0x007e:
            r10.put(r14, r13)     // Catch:{ JSONException -> 0x00ea }
        L_0x0081:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x00ea }
            r12.<init>()     // Catch:{ JSONException -> 0x00ea }
            double r15 = r6.getLongitudeE6()     // Catch:{ JSONException -> 0x00ea }
            java.lang.String r15 = java.lang.String.valueOf(r15)     // Catch:{ JSONException -> 0x00ea }
            r12.append(r15)     // Catch:{ JSONException -> 0x00ea }
            r12.append(r2)     // Catch:{ JSONException -> 0x00ea }
            double r15 = r6.getLatitudeE6()     // Catch:{ JSONException -> 0x00ea }
            java.lang.String r6 = java.lang.String.valueOf(r15)     // Catch:{ JSONException -> 0x00ea }
            r12.append(r6)     // Catch:{ JSONException -> 0x00ea }
            java.lang.String r6 = r12.toString()     // Catch:{ JSONException -> 0x00ea }
            r10.put(r3, r6)     // Catch:{ JSONException -> 0x00ea }
            r9.put(r10)     // Catch:{ JSONException -> 0x00ea }
            r11.put(r5, r4)     // Catch:{ JSONException -> 0x00ea }
            java.lang.String r4 = r0.d     // Catch:{ JSONException -> 0x00ea }
            if (r4 == 0) goto L_0x00be
            java.lang.String r4 = r0.d     // Catch:{ JSONException -> 0x00ea }
            boolean r4 = r4.equals(r13)     // Catch:{ JSONException -> 0x00ea }
            if (r4 != 0) goto L_0x00be
            java.lang.String r0 = r0.d     // Catch:{ JSONException -> 0x00ea }
            r10.put(r14, r0)     // Catch:{ JSONException -> 0x00ea }
            goto L_0x00c1
        L_0x00be:
            r10.put(r14, r13)     // Catch:{ JSONException -> 0x00ea }
        L_0x00c1:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x00ea }
            r0.<init>()     // Catch:{ JSONException -> 0x00ea }
            double r4 = r7.getLongitudeE6()     // Catch:{ JSONException -> 0x00ea }
            java.lang.String r4 = java.lang.String.valueOf(r4)     // Catch:{ JSONException -> 0x00ea }
            r0.append(r4)     // Catch:{ JSONException -> 0x00ea }
            r0.append(r2)     // Catch:{ JSONException -> 0x00ea }
            double r4 = r7.getLatitudeE6()     // Catch:{ JSONException -> 0x00ea }
            java.lang.String r2 = java.lang.String.valueOf(r4)     // Catch:{ JSONException -> 0x00ea }
            r0.append(r2)     // Catch:{ JSONException -> 0x00ea }
            java.lang.String r0 = r0.toString()     // Catch:{ JSONException -> 0x00ea }
            r11.put(r3, r0)     // Catch:{ JSONException -> 0x00ea }
            r9.put(r11)     // Catch:{ JSONException -> 0x00ea }
            goto L_0x00ee
        L_0x00ea:
            r0 = move-exception
            r0.printStackTrace()
        L_0x00ee:
            int r0 = r9.length()
            if (r0 <= 0) goto L_0x0100
            java.lang.String r0 = "&positions="
            r8.append(r0)
            java.lang.String r0 = r9.toString()
            r8.append(r0)
        L_0x0100:
            java.lang.String r0 = "&ctrl_type="
            r8.append(r0)
            java.lang.String r0 = "&mrsl="
            r8.append(r0)
            java.lang.String r0 = "/vt=map&state=entry"
            r8.append(r0)
            java.lang.String r0 = r8.toString()
            android.net.Uri r0 = android.net.Uri.parse(r0)
            android.content.Intent r2 = new android.content.Intent
            r2.<init>()
            java.lang.String r3 = "android.intent.action.VIEW"
            r2.setAction(r3)
            r3 = 268435456(0x10000000, float:2.5243549E-29)
            r2.setFlags(r3)
            r2.setData(r0)
            r1.startActivity(r2)
            return
        L_0x012d:
            com.baidu.mapapi.navi.IllegalNaviArgumentException r0 = new com.baidu.mapapi.navi.IllegalNaviArgumentException
            java.lang.String r1 = "BDMapSDKException: you must set start and end point."
            r0.<init>(r1)
            throw r0
        L_0x0135:
            com.baidu.mapapi.navi.IllegalNaviArgumentException r0 = new com.baidu.mapapi.navi.IllegalNaviArgumentException
            java.lang.String r1 = "BDMapSDKException: para or context can not be null."
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.navi.BaiduMapNavigation.a(com.baidu.mapapi.navi.NaviParaOption, android.content.Context):void");
    }

    public static void finish(Context context) {
        if (context != null) {
            b.a(context);
        }
    }

    public static boolean openBaiduMapBikeNavi(NaviParaOption naviParaOption, Context context) {
        String str;
        if (naviParaOption == null || context == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: para or context can not be null.");
        } else if (naviParaOption.c == null || naviParaOption.a == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: start point or end point can not be null.");
        } else {
            int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
            if (baiduMapVersion == 0) {
                str = "BaiduMap app is not installed.";
            } else if (baiduMapVersion >= 869) {
                return b.a(naviParaOption, context, 8);
            } else {
                str = "Baidumap app version is too lowl.Version is greater than 8.6.6";
            }
            Log.e("baidumapsdk", str);
            return false;
        }
    }

    public static boolean openBaiduMapNavi(NaviParaOption naviParaOption, Context context) {
        if (naviParaOption == null || context == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: para or context can not be null.");
        } else if (naviParaOption.c == null || naviParaOption.a == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: start point or end point can not be null.");
        } else {
            int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
            if (baiduMapVersion == 0) {
                Log.e("baidumapsdk", "BaiduMap app is not installed.");
                if (a) {
                    a(naviParaOption, context);
                    return true;
                }
                throw new BaiduMapAppNotSupportNaviException("BDMapSDKException: BaiduMap app is not installed.");
            } else if (baiduMapVersion >= 830) {
                return b.a(naviParaOption, context, 5);
            } else {
                Log.e("baidumapsdk", "Baidumap app version is too lowl.Version is greater than 8.2");
                if (a) {
                    a(naviParaOption, context);
                    return true;
                }
                throw new BaiduMapAppNotSupportNaviException("BDMapSDKException: Baidumap app version is too lowl.Version is greater than 8.2");
            }
        }
    }

    public static boolean openBaiduMapWalkNavi(NaviParaOption naviParaOption, Context context) {
        String str;
        if (naviParaOption == null || context == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: para or context can not be null.");
        } else if (naviParaOption.c == null || naviParaOption.a == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: start point or end point can not be null.");
        } else {
            int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
            if (baiduMapVersion == 0) {
                str = "BaiduMap app is not installed.";
            } else if (baiduMapVersion >= 869) {
                return b.a(naviParaOption, context, 7);
            } else {
                str = "Baidumap app version is too lowl.Version is greater than 8.6.6";
            }
            Log.e("baidumapsdk", str);
            return false;
        }
    }

    public static boolean openBaiduMapWalkNaviAR(NaviParaOption naviParaOption, Context context) {
        String str;
        if (naviParaOption == null || context == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: para or context can not be null.");
        } else if (naviParaOption.c == null || naviParaOption.a == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: start point or end point can not be null.");
        } else {
            int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
            if (baiduMapVersion == 0) {
                str = "BaiduMap app is not installed.";
            } else if (baiduMapVersion >= 869) {
                return b.a(naviParaOption, context, 9);
            } else {
                str = "Baidumap app version is too lowl.Version is greater than 8.6.6";
            }
            Log.e("baidumapsdk", str);
            return false;
        }
    }

    @Deprecated
    public static void openWebBaiduMapNavi(NaviParaOption naviParaOption, Context context) throws IllegalNaviArgumentException {
        Uri parse;
        Intent intent;
        if (naviParaOption == null || context == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: para or context can not be null.");
        }
        if (naviParaOption.a != null && naviParaOption.c != null) {
            GeoPoint ll2mc = CoordUtil.ll2mc(naviParaOption.a);
            GeoPoint ll2mc2 = CoordUtil.ll2mc(naviParaOption.c);
            parse = Uri.parse("http://daohang.map.baidu.com/mobile/#navi/naving/start=" + ll2mc.getLongitudeE6() + "," + ll2mc.getLatitudeE6() + "&endp=" + ll2mc2.getLongitudeE6() + "," + ll2mc2.getLatitudeE6() + "&fromprod=" + a(context) + "/vt=map&state=entry");
            intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setFlags(C.ENCODING_PCM_MU_LAW);
        } else if (naviParaOption.b == null || naviParaOption.b.equals("") || naviParaOption.d == null || naviParaOption.d.equals("")) {
            throw new IllegalNaviArgumentException("BDMapSDKException: you must set start and end point or set the start and end name.");
        } else {
            parse = Uri.parse("http://daohang.map.baidu.com/mobile/#search/search/qt=nav&sn=2$$$$$$" + naviParaOption.b + "$$$$$$&en=2$$$$$$" + naviParaOption.d + "$$$$$$&fromprod=" + a(context));
            intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
        }
        intent.setData(parse);
        context.startActivity(intent);
    }

    public static void setSupportWebNavi(boolean z) {
        a = z;
    }
}
