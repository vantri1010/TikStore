package com.baidu.mapapi.utils.route;

import android.content.Context;
import android.util.Log;
import com.baidu.mapapi.navi.IllegalNaviArgumentException;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.b;
import com.baidu.mapapi.utils.poi.IllegalPoiSearchArgumentException;
import com.baidu.mapapi.utils.route.RouteParaOption;

public class BaiduMapRoutePlan {
    private static boolean a = true;

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00f8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void a(com.baidu.mapapi.utils.route.RouteParaOption r10, android.content.Context r11, int r12) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "http://api.map.baidu.com/direction?"
            r0.append(r1)
            java.lang.String r1 = "origin="
            r0.append(r1)
            com.baidu.mapapi.model.LatLng r1 = r10.a
            com.baidu.mapapi.CoordType r2 = com.baidu.mapapi.SDKInitializer.getCoordType()
            com.baidu.mapapi.CoordType r3 = com.baidu.mapapi.CoordType.GCJ02
            if (r2 != r3) goto L_0x001f
            if (r1 == 0) goto L_0x001f
            com.baidu.mapapi.model.LatLng r1 = com.baidu.mapsdkplatform.comapi.util.CoordTrans.gcjToBaidu(r1)
        L_0x001f:
            com.baidu.mapapi.model.LatLng r2 = r10.a
            java.lang.String r3 = "name:"
            java.lang.String r4 = "|"
            java.lang.String r5 = "latlng:"
            java.lang.String r6 = ","
            java.lang.String r7 = ""
            if (r2 == 0) goto L_0x0053
            java.lang.String r2 = r10.c
            if (r2 == 0) goto L_0x0053
            java.lang.String r2 = r10.c
            boolean r2 = r2.equals(r7)
            if (r2 != 0) goto L_0x0053
            if (r1 == 0) goto L_0x0053
            r0.append(r5)
            double r8 = r1.latitude
            r0.append(r8)
            r0.append(r6)
            double r1 = r1.longitude
            r0.append(r1)
            r0.append(r4)
            r0.append(r3)
            goto L_0x0067
        L_0x0053:
            com.baidu.mapapi.model.LatLng r2 = r10.a
            if (r2 == 0) goto L_0x0067
            if (r1 == 0) goto L_0x0067
            double r8 = r1.latitude
            r0.append(r8)
            r0.append(r6)
            double r1 = r1.longitude
            r0.append(r1)
            goto L_0x006c
        L_0x0067:
            java.lang.String r1 = r10.c
            r0.append(r1)
        L_0x006c:
            com.baidu.mapapi.model.LatLng r1 = r10.b
            com.baidu.mapapi.CoordType r2 = com.baidu.mapapi.SDKInitializer.getCoordType()
            com.baidu.mapapi.CoordType r8 = com.baidu.mapapi.CoordType.GCJ02
            if (r2 != r8) goto L_0x007c
            if (r1 == 0) goto L_0x007c
            com.baidu.mapapi.model.LatLng r1 = com.baidu.mapsdkplatform.comapi.util.CoordTrans.gcjToBaidu(r1)
        L_0x007c:
            java.lang.String r2 = "&destination="
            r0.append(r2)
            com.baidu.mapapi.model.LatLng r2 = r10.b
            if (r2 == 0) goto L_0x00aa
            java.lang.String r2 = r10.d
            if (r2 == 0) goto L_0x00aa
            java.lang.String r2 = r10.d
            boolean r2 = r2.equals(r7)
            if (r2 != 0) goto L_0x00aa
            if (r1 == 0) goto L_0x00aa
            r0.append(r5)
            double r8 = r1.latitude
            r0.append(r8)
            r0.append(r6)
            double r1 = r1.longitude
            r0.append(r1)
            r0.append(r4)
            r0.append(r3)
            goto L_0x00be
        L_0x00aa:
            com.baidu.mapapi.model.LatLng r2 = r10.b
            if (r2 == 0) goto L_0x00be
            if (r1 == 0) goto L_0x00be
            double r2 = r1.latitude
            r0.append(r2)
            r0.append(r6)
            double r1 = r1.longitude
            r0.append(r1)
            goto L_0x00c3
        L_0x00be:
            java.lang.String r1 = r10.d
            r0.append(r1)
        L_0x00c3:
            if (r12 == 0) goto L_0x00d3
            r1 = 1
            if (r12 == r1) goto L_0x00d0
            r1 = 2
            if (r12 == r1) goto L_0x00cd
            r12 = r7
            goto L_0x00d5
        L_0x00cd:
            java.lang.String r12 = "walking"
            goto L_0x00d5
        L_0x00d0:
            java.lang.String r12 = "transit"
            goto L_0x00d5
        L_0x00d3:
            java.lang.String r12 = "driving"
        L_0x00d5:
            java.lang.String r1 = "&mode="
            r0.append(r1)
            r0.append(r12)
            java.lang.String r12 = "&region="
            r0.append(r12)
            java.lang.String r12 = r10.getCityName()
            if (r12 == 0) goto L_0x00f8
            java.lang.String r12 = r10.getCityName()
            boolean r12 = r12.equals(r7)
            if (r12 == 0) goto L_0x00f3
            goto L_0x00f8
        L_0x00f3:
            java.lang.String r10 = r10.getCityName()
            goto L_0x00fb
        L_0x00f8:
            java.lang.String r10 = "全国"
        L_0x00fb:
            r0.append(r10)
            java.lang.String r10 = "&output=html"
            r0.append(r10)
            java.lang.String r10 = "&src="
            r0.append(r10)
            java.lang.String r10 = r11.getPackageName()
            r0.append(r10)
            java.lang.String r10 = r0.toString()
            android.net.Uri r10 = android.net.Uri.parse(r10)
            android.content.Intent r12 = new android.content.Intent
            r12.<init>()
            java.lang.String r0 = "android.intent.action.VIEW"
            r12.setAction(r0)
            r0 = 268435456(0x10000000, float:2.5243549E-29)
            r12.setFlags(r0)
            r12.setData(r10)
            r11.startActivity(r12)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.utils.route.BaiduMapRoutePlan.a(com.baidu.mapapi.utils.route.RouteParaOption, android.content.Context, int):void");
    }

    public static void finish(Context context) {
        if (context != null) {
            b.a(context);
        }
    }

    public static boolean openBaiduMapDrivingRoute(RouteParaOption routeParaOption, Context context) {
        if (routeParaOption == null || context == null) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: para or context can not be null.");
        } else if (routeParaOption.b == null && routeParaOption.a == null && routeParaOption.d == null && routeParaOption.c == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: startPoint and endPoint and endName and startName not all null.");
        } else if (routeParaOption.c == null && routeParaOption.a == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: startPoint and startName not all null.");
        } else if (routeParaOption.d == null && routeParaOption.b == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: endPoint and endName not all null.");
        } else if (((routeParaOption.c == null || routeParaOption.c.equals("")) && routeParaOption.a == null) || ((routeParaOption.d == null || routeParaOption.d.equals("")) && routeParaOption.b == null)) {
            Log.e(BaiduMapRoutePlan.class.getName(), "poi startName or endName can not be empty string while pt is null");
            return false;
        } else {
            if (routeParaOption.f == null) {
                routeParaOption.f = RouteParaOption.EBusStrategyType.bus_recommend_way;
            }
            int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
            if (baiduMapVersion == 0) {
                Log.e("baidumapsdk", "BaiduMap app is not installed.");
                if (a) {
                    a(routeParaOption, context, 0);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: BaiduMap app is not installed.");
            } else if (baiduMapVersion >= 810) {
                return b.a(routeParaOption, context, 0);
            } else {
                Log.e("baidumapsdk", "Baidumap app version is too lowl.Version is greater than 8.1");
                if (a) {
                    a(routeParaOption, context, 0);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: Baidumap app version is too lowl.Version is greater than 8.1");
            }
        }
    }

    public static boolean openBaiduMapTransitRoute(RouteParaOption routeParaOption, Context context) {
        if (routeParaOption == null || context == null) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: para or context can not be null.");
        } else if (routeParaOption.b == null && routeParaOption.a == null && routeParaOption.d == null && routeParaOption.c == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: startPoint and endPoint and endName and startName not all null.");
        } else if (routeParaOption.c == null && routeParaOption.a == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: startPoint and startName not all null.");
        } else if (routeParaOption.d == null && routeParaOption.b == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: endPoint and endName not all null.");
        } else if (((routeParaOption.c == null || routeParaOption.c.equals("")) && routeParaOption.a == null) || ((routeParaOption.d == null || routeParaOption.d.equals("")) && routeParaOption.b == null)) {
            Log.e(BaiduMapRoutePlan.class.getName(), "poi startName or endName can not be empty string while pt is null");
            return false;
        } else {
            if (routeParaOption.f == null) {
                routeParaOption.f = RouteParaOption.EBusStrategyType.bus_recommend_way;
            }
            int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
            if (baiduMapVersion == 0) {
                Log.e("baidumapsdk", "BaiduMap app is not installed.");
                if (a) {
                    a(routeParaOption, context, 1);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: BaiduMap app is not installed.");
            } else if (baiduMapVersion >= 810) {
                return b.a(routeParaOption, context, 1);
            } else {
                Log.e("baidumapsdk", "Baidumap app version is too lowl.Version is greater than 8.1");
                if (a) {
                    a(routeParaOption, context, 1);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: Baidumap app version is too lowl.Version is greater than 8.1");
            }
        }
    }

    public static boolean openBaiduMapWalkingRoute(RouteParaOption routeParaOption, Context context) {
        if (routeParaOption == null || context == null) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: para or context can not be null.");
        } else if (routeParaOption.b == null && routeParaOption.a == null && routeParaOption.d == null && routeParaOption.c == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: startPoint and endPoint and endName and startName not all null.");
        } else if (routeParaOption.c == null && routeParaOption.a == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: startPoint and startName not all null.");
        } else if (routeParaOption.d == null && routeParaOption.b == null) {
            throw new IllegalNaviArgumentException("BDMapSDKException: endPoint and endName not all null.");
        } else if (((routeParaOption.c == null || routeParaOption.c.equals("")) && routeParaOption.a == null) || ((routeParaOption.d == null || routeParaOption.d.equals("")) && routeParaOption.b == null)) {
            Log.e(BaiduMapRoutePlan.class.getName(), "poi startName or endName can not be empty string while pt is null");
            return false;
        } else {
            if (routeParaOption.f == null) {
                routeParaOption.f = RouteParaOption.EBusStrategyType.bus_recommend_way;
            }
            int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
            if (baiduMapVersion == 0) {
                Log.e("baidumapsdk", "BaiduMap app is not installed.");
                if (a) {
                    a(routeParaOption, context, 2);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: BaiduMap app is not installed.");
            } else if (baiduMapVersion >= 810) {
                return b.a(routeParaOption, context, 2);
            } else {
                Log.e("baidumapsdk", "Baidumap app version is too lowl.Version is greater than 8.1");
                if (a) {
                    a(routeParaOption, context, 2);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: Baidumap app version is too lowl.Version is greater than 8.1");
            }
        }
    }

    public static void setSupportWebRoute(boolean z) {
        a = z;
    }
}
