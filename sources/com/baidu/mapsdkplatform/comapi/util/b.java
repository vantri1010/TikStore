package com.baidu.mapsdkplatform.comapi.util;

import android.os.Bundle;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.model.inner.Point;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.baidu.mapsdkplatform.comjni.tools.JNITools;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class b {
    static double[] a = {1.289059486E7d, 8362377.87d, 5591021.0d, 3481989.83d, 1678043.12d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE};
    static double[] b = {7.5E7d, 6.0E7d, 4.5E7d, 3.0E7d, 1.5E7d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE};
    static double[][] c = {new double[]{1.410526172116255E-8d, 8.98305509648872E-6d, -1.9939833816331d, 200.9824383106796d, -187.2403703815547d, 91.6087516669843d, -23.38765649603339d, 2.57121317296198d, -0.03801003308653d, 1.73379812E7d}, new double[]{-7.435856389565537E-9d, 8.983055097726239E-6d, -0.78625201886289d, 96.32687599759846d, -1.85204757529826d, -59.36935905485877d, 47.40033549296737d, -16.50741931063887d, 2.28786674699375d, 1.026014486E7d}, new double[]{-3.030883460898826E-8d, 8.98305509983578E-6d, 0.30071316287616d, 59.74293618442277d, 7.357984074871d, -25.38371002664745d, 13.45380521110908d, -3.29883767235584d, 0.32710905363475d, 6856817.37d}, new double[]{-1.981981304930552E-8d, 8.983055099779535E-6d, 0.03278182852591d, 40.31678527705744d, 0.65659298677277d, -4.44255534477492d, 0.85341911805263d, 0.12923347998204d, -0.04625736007561d, 4482777.06d}, new double[]{3.09191371068437E-9d, 8.983055096812155E-6d, 6.995724062E-5d, 23.10934304144901d, -2.3663490511E-4d, -0.6321817810242d, -0.00663494467273d, 0.03430082397953d, -0.00466043876332d, 2555164.4d}, new double[]{2.890871144776878E-9d, 8.983055095805407E-6d, -3.068298E-8d, 7.47137025468032d, -3.53937994E-6d, -0.02145144861037d, -1.234426596E-5d, 1.0322952773E-4d, -3.23890364E-6d, 826088.5d}};
    static double[][] d = {new double[]{-0.0015702102444d, 111320.7020616939d, 1.704480524535203E15d, -1.033898737604234E16d, 2.611266785660388E16d, -3.51496691766537E16d, 2.659570071840392E16d, -1.072501245418824E16d, 1.800819912950474E15d, 82.5d}, new double[]{8.277824516172526E-4d, 111320.7020463578d, 6.477955746671607E8d, -4.082003173641316E9d, 1.077490566351142E10d, -1.517187553151559E10d, 1.205306533862167E10d, -5.124939663577472E9d, 9.133119359512032E8d, 67.5d}, new double[]{0.00337398766765d, 111320.7020202162d, 4481351.045890365d, -2.339375119931662E7d, 7.968221547186455E7d, -1.159649932797253E8d, 9.723671115602145E7d, -4.366194633752821E7d, 8477230.501135234d, 52.5d}, new double[]{0.00220636496208d, 111320.7020209128d, 51751.86112841131d, 3796837.749470245d, 992013.7397791013d, -1221952.21711287d, 1340652.697009075d, -620943.6990984312d, 144416.9293806241d, 37.5d}, new double[]{-3.441963504368392E-4d, 111320.7020576856d, 278.2353980772752d, 2485758.690035394d, 6070.750963243378d, 54821.18345352118d, 9540.606633304236d, -2710.55326746645d, 1405.483844121726d, 22.5d}, new double[]{-3.218135878613132E-4d, 111320.7020701615d, 0.00369383431289d, 823725.6402795718d, 0.46104986909093d, 2351.343141331292d, 1.58060784298199d, 8.77738589078284d, 0.37238884252424d, 7.45d}};

    static class a {
        double a;
        double b;

        a() {
        }
    }

    public static int a(LatLng latLng, int i) {
        LatLng latLng2 = new LatLng(latLng.latitude + (((double) i) / 111000.0d), latLng.longitude);
        GeoPoint a2 = a(latLng);
        GeoPoint a3 = a(latLng2);
        return (int) Math.sqrt(Math.pow(a2.getLatitudeE6() - a3.getLatitudeE6(), 2.0d) + Math.pow(a2.getLongitudeE6() - a3.getLongitudeE6(), 2.0d));
    }

    public static LatLng a(float f, float f2, String str) {
        if (str == null) {
            return null;
        }
        if (str.equals("") || str.equals("bd09ll")) {
            return new LatLng((double) f, (double) f2);
        }
        if (!str.equals("bd09ll") && !str.equals(CoordinateType.BD09MC) && !str.equals(CoordinateType.GCJ02) && !str.equals(CoordinateType.WGS84)) {
            return null;
        }
        Bundle bundle = new Bundle();
        JNITools.CoordinateEncryptEx(f, f2, str, bundle);
        if (!bundle.isEmpty()) {
            return new LatLng(bundle.getDouble("y"), bundle.getDouble("x"));
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0099 A[EDGE_INSN: B:32:0x0099->B:30:0x0099 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.baidu.mapapi.model.LatLng a(com.baidu.mapapi.model.inner.GeoPoint r11) {
        /*
            com.baidu.mapsdkplatform.comapi.util.b$a r0 = new com.baidu.mapsdkplatform.comapi.util.b$a
            r0.<init>()
            double r1 = r11.getLongitudeE6()
            r0.a = r1
            double r1 = r11.getLatitudeE6()
            r0.b = r1
            com.baidu.mapsdkplatform.comapi.util.b$a r11 = new com.baidu.mapsdkplatform.comapi.util.b$a
            r11.<init>()
            double r1 = r0.a
            r11.a = r1
            double r1 = r11.a
            r3 = -4507228048936348418(0xc1731bf84578d4fe, double:-2.0037508342E7)
            r5 = 4716143987918427390(0x41731bf84578d4fe, double:2.0037508342E7)
            int r7 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            double r1 = r11.a
            if (r7 <= 0) goto L_0x0031
            double r1 = r1 - r5
            double r1 = r1 + r3
        L_0x002e:
            r11.a = r1
            goto L_0x003c
        L_0x0031:
            int r7 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r7 >= 0) goto L_0x003c
            double r1 = r11.a
            double r1 = r3 - r1
            double r1 = r5 - r1
            goto L_0x002e
        L_0x003c:
            double r0 = r0.b
            r11.b = r0
            double r0 = r11.b
            r7 = 0
            r9 = 4517329193108106637(0x3eb0c6f7a0b5ed8d, double:1.0E-6)
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 >= 0) goto L_0x0056
            double r0 = r11.b
            int r2 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r2 < 0) goto L_0x0056
            r11.b = r9
            goto L_0x007b
        L_0x0056:
            double r0 = r11.b
            int r2 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r2 >= 0) goto L_0x006a
            double r0 = r11.b
            r7 = -4706042843746669171(0xbeb0c6f7a0b5ed8d, double:-1.0E-6)
            int r2 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r2 <= 0) goto L_0x006a
            r11.b = r7
            goto L_0x007b
        L_0x006a:
            double r0 = r11.b
            int r2 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x0073
            r11.b = r5
            goto L_0x007b
        L_0x0073:
            double r0 = r11.b
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x007b
            r11.b = r3
        L_0x007b:
            r0 = 10
            double[] r0 = new double[r0]
            r1 = 0
        L_0x0080:
            r2 = 6
            if (r1 >= r2) goto L_0x0099
            double r2 = r11.b
            double r2 = java.lang.Math.abs(r2)
            double[] r4 = a
            r5 = r4[r1]
            int r4 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x0096
            double[][] r0 = c
            r0 = r0[r1]
            goto L_0x0099
        L_0x0096:
            int r1 = r1 + 1
            goto L_0x0080
        L_0x0099:
            com.baidu.mapsdkplatform.comapi.util.b$a r11 = a((com.baidu.mapsdkplatform.comapi.util.b.a) r11, (double[]) r0)
            com.baidu.mapapi.model.LatLng r0 = new com.baidu.mapapi.model.LatLng
            double r1 = r11.b
            double r3 = r11.a
            r0.<init>(r1, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.util.b.a(com.baidu.mapapi.model.inner.GeoPoint):com.baidu.mapapi.model.LatLng");
    }

    public static LatLng a(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putString("strkey", str);
        JNITools.TransGeoStr2Pt(bundle);
        GeoPoint geoPoint = new GeoPoint(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
        geoPoint.setLongitudeE6((double) bundle.getInt("ptx"));
        geoPoint.setLatitudeE6((double) bundle.getInt("pty"));
        return a(geoPoint);
    }

    public static GeoPoint a(LatLng latLng) {
        a aVar = new a();
        double[] dArr = new double[10];
        aVar.b = Math.abs(latLng.latitude * 1000000.0d);
        if (aVar.b < 0.1d) {
            aVar.b = 0.1d;
        }
        int i = 0;
        while (true) {
            if (i >= b.length) {
                break;
            } else if (aVar.b > b[i]) {
                dArr = d[i];
                break;
            } else {
                i++;
            }
        }
        aVar.a = latLng.longitude;
        aVar.b = latLng.latitude;
        a a2 = a(aVar, dArr);
        return new GeoPoint(a2.b, a2.a);
    }

    static a a(a aVar, double[] dArr) {
        a aVar2 = new a();
        int i = 1;
        aVar2.a = dArr[0] + (dArr[1] * Math.abs(aVar.a));
        double abs = Math.abs(aVar.b) / dArr[9];
        aVar2.b = dArr[2] + (dArr[3] * abs) + (dArr[4] * abs * abs) + (dArr[5] * abs * abs * abs) + (dArr[6] * abs * abs * abs * abs) + (dArr[7] * abs * abs * abs * abs * abs) + (dArr[8] * abs * abs * abs * abs * abs * abs);
        aVar2.a *= (double) (aVar.a < FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? -1 : 1);
        double d2 = aVar2.b;
        if (aVar.b < FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            i = -1;
        }
        aVar2.b = d2 * ((double) i);
        return aVar2;
    }

    public static LatLng b(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putString("strkey", str);
        JNITools.TransNodeStr2Pt(bundle);
        return a(new GeoPoint(bundle.getDouble("pty"), bundle.getDouble("ptx")));
    }

    public static Point b(LatLng latLng) {
        if (latLng == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        JNITools.CoordinateEncryptMc((float) latLng.longitude, (float) latLng.latitude, bundle);
        Point point = new Point(0, 0);
        point.setmPtx((int) bundle.getDouble("x"));
        point.setmPty((int) bundle.getDouble("y"));
        return point;
    }

    public static List<LatLng> c(String str) {
        com.baidu.mapapi.model.inner.a a2 = com.baidu.mapsdkplatform.comjni.tools.a.a(str);
        ArrayList arrayList = new ArrayList();
        if (a2 == null || a2.d == null) {
            return null;
        }
        ArrayList<ArrayList<Point>> arrayList2 = a2.d;
        if (arrayList2.size() > 0) {
            ArrayList arrayList3 = arrayList2.get(0);
            for (int i = 0; i < arrayList3.size(); i++) {
                Point point = (Point) arrayList3.get(i);
                arrayList.add(SDKInitializer.getCoordType() == CoordType.GCJ02 ? CoordTrans.baiduToGcj(a(new GeoPoint((double) (point.y / 100), (double) (point.x / 100)))) : a(new GeoPoint((double) (point.y / 100), (double) (point.x / 100))));
            }
        }
        return arrayList;
    }

    public static List<List<LatLng>> d(String str) {
        com.baidu.mapapi.model.inner.a a2 = com.baidu.mapsdkplatform.comjni.tools.a.a(str);
        if (a2 == null || a2.d == null) {
            return null;
        }
        ArrayList<ArrayList<Point>> arrayList = a2.d;
        ArrayList arrayList2 = new ArrayList();
        Iterator<ArrayList<Point>> it = arrayList.iterator();
        while (it.hasNext()) {
            ArrayList arrayList3 = new ArrayList();
            Iterator it2 = it.next().iterator();
            while (it2.hasNext()) {
                Point point = (Point) it2.next();
                arrayList3.add(SDKInitializer.getCoordType() == CoordType.GCJ02 ? CoordTrans.baiduToGcj(a(new GeoPoint((double) (point.y / 100), (double) (point.x / 100)))) : a(new GeoPoint((double) (point.y / 100), (double) (point.x / 100))));
            }
            arrayList2.add(arrayList3);
        }
        return arrayList2;
    }
}
