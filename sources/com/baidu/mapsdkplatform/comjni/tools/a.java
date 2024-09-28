package com.baidu.mapsdkplatform.comjni.tools;

import android.os.Bundle;
import com.baidu.mapapi.model.inner.Point;
import java.util.ArrayList;

public class a {
    public static double a(Point point, Point point2) {
        Bundle bundle = new Bundle();
        bundle.putDouble("x1", (double) point.x);
        bundle.putDouble("y1", (double) point.y);
        bundle.putDouble("x2", (double) point2.x);
        bundle.putDouble("y2", (double) point2.y);
        JNITools.GetDistanceByMC(bundle);
        return bundle.getDouble("distance");
    }

    public static com.baidu.mapapi.model.inner.a a(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putString("strkey", str);
        JNITools.TransGeoStr2ComplexPt(bundle);
        com.baidu.mapapi.model.inner.a aVar = new com.baidu.mapapi.model.inner.a();
        Bundle bundle2 = bundle.getBundle("map_bound");
        if (bundle2 != null) {
            Bundle bundle3 = bundle2.getBundle("ll");
            if (bundle3 != null) {
                aVar.b = new Point((int) bundle3.getDouble("ptx"), (int) bundle3.getDouble("pty"));
            }
            Bundle bundle4 = bundle2.getBundle("ru");
            if (bundle4 != null) {
                aVar.c = new Point((int) bundle4.getDouble("ptx"), (int) bundle4.getDouble("pty"));
            }
        }
        ParcelItem[] parcelItemArr = (ParcelItem[]) bundle.getParcelableArray("poly_line");
        for (ParcelItem bundle5 : parcelItemArr) {
            if (aVar.d == null) {
                aVar.d = new ArrayList<>();
            }
            Bundle bundle6 = bundle5.getBundle();
            if (bundle6 != null) {
                ParcelItem[] parcelItemArr2 = (ParcelItem[]) bundle6.getParcelableArray("point_array");
                ArrayList arrayList = new ArrayList();
                for (ParcelItem bundle7 : parcelItemArr2) {
                    Bundle bundle8 = bundle7.getBundle();
                    if (bundle8 != null) {
                        arrayList.add(new Point((int) bundle8.getDouble("ptx"), (int) bundle8.getDouble("pty")));
                    }
                }
                arrayList.trimToSize();
                aVar.d.add(arrayList);
            }
        }
        aVar.d.trimToSize();
        aVar.a = (int) bundle.getDouble("type");
        return aVar;
    }

    public static String a() {
        return JNITools.GetToken();
    }

    public static void a(boolean z, int i) {
        JNITools.openLogEnable(z, i);
    }

    public static void b() {
        JNITools.initClass(new Bundle(), 0);
    }
}
