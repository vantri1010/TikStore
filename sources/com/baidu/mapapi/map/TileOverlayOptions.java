package com.baidu.mapapi.map;

import android.os.Bundle;
import android.util.Log;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.google.android.gms.common.internal.ImagesContract;

public final class TileOverlayOptions {
    private static Bundle c;
    private static final String j = TileOverlayOptions.class.getSimpleName();
    private int a = 209715200;
    private TileProvider b;
    private int d = 20;
    public int datasource;
    private int e = 3;
    private int f = 15786414;
    private int g = -20037726;
    private int h = -15786414;
    private int i = 20037726;
    public String urlString;

    public TileOverlayOptions() {
        Bundle bundle = new Bundle();
        c = bundle;
        bundle.putInt("rectr", this.f);
        c.putInt("rectb", this.g);
        c.putInt("rectl", this.h);
        c.putInt("rectt", this.i);
    }

    private TileOverlayOptions a(int i2, int i3) {
        this.d = i2;
        this.e = i3;
        return this;
    }

    /* access modifiers changed from: package-private */
    public Bundle a() {
        c.putString(ImagesContract.URL, this.urlString);
        c.putInt("datasource", this.datasource);
        c.putInt("maxDisplay", this.d);
        c.putInt("minDisplay", this.e);
        c.putInt("sdktiletmpmax", this.a);
        return c;
    }

    /* access modifiers changed from: package-private */
    public TileOverlay a(BaiduMap baiduMap) {
        return new TileOverlay(baiduMap, this.b);
    }

    public TileOverlayOptions setMaxTileTmp(int i2) {
        this.a = i2;
        return this;
    }

    public TileOverlayOptions setPositionFromBounds(LatLngBounds latLngBounds) {
        if (latLngBounds != null) {
            GeoPoint ll2mc = CoordUtil.ll2mc(latLngBounds.northeast);
            GeoPoint ll2mc2 = CoordUtil.ll2mc(latLngBounds.southwest);
            double latitudeE6 = ll2mc.getLatitudeE6();
            double longitudeE6 = ll2mc2.getLongitudeE6();
            double latitudeE62 = ll2mc2.getLatitudeE6();
            double longitudeE62 = ll2mc.getLongitudeE6();
            if (latitudeE6 <= latitudeE62 || longitudeE62 <= longitudeE6) {
                Log.e(j, "BDMapSDKException: bounds is illegal, use default bounds");
            } else {
                c.putInt("rectr", (int) longitudeE62);
                c.putInt("rectb", (int) latitudeE62);
                c.putInt("rectl", (int) longitudeE6);
                c.putInt("rectt", (int) latitudeE6);
            }
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: bound can not be null");
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x005f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.baidu.mapapi.map.TileOverlayOptions tileProvider(com.baidu.mapapi.map.TileProvider r4) {
        /*
            r3 = this;
            r0 = 0
            if (r4 != 0) goto L_0x0004
            return r0
        L_0x0004:
            boolean r1 = r4 instanceof com.baidu.mapapi.map.UrlTileProvider
            if (r1 == 0) goto L_0x0042
            r1 = 1
            r3.datasource = r1
            r1 = r4
            com.baidu.mapapi.map.UrlTileProvider r1 = (com.baidu.mapapi.map.UrlTileProvider) r1
            java.lang.String r1 = r1.getTileUrl()
            if (r1 == 0) goto L_0x003a
            java.lang.String r2 = ""
            boolean r2 = r2.equals(r1)
            if (r2 != 0) goto L_0x003a
            java.lang.String r2 = "{x}"
            boolean r2 = r1.contains(r2)
            if (r2 == 0) goto L_0x003a
            java.lang.String r2 = "{y}"
            boolean r2 = r1.contains(r2)
            if (r2 == 0) goto L_0x003a
            java.lang.String r2 = "{z}"
            boolean r2 = r1.contains(r2)
            if (r2 == 0) goto L_0x003a
            r3.urlString = r1
            goto L_0x0049
        L_0x003a:
            java.lang.String r4 = j
            java.lang.String r1 = "tile url template is illegal, must contains {x}、{y}、{z}"
        L_0x003e:
            android.util.Log.e(r4, r1)
            return r0
        L_0x0042:
            boolean r1 = r4 instanceof com.baidu.mapapi.map.FileTileProvider
            if (r1 == 0) goto L_0x0067
            r0 = 0
            r3.datasource = r0
        L_0x0049:
            r3.b = r4
            int r0 = r4.getMaxDisLevel()
            int r4 = r4.getMinDisLevel()
            r1 = 21
            if (r0 > r1) goto L_0x005f
            r1 = 3
            if (r4 >= r1) goto L_0x005b
            goto L_0x005f
        L_0x005b:
            r3.a(r0, r4)
            goto L_0x0066
        L_0x005f:
            java.lang.String r4 = j
            java.lang.String r0 = "display level is illegal"
            android.util.Log.e(r4, r0)
        L_0x0066:
            return r3
        L_0x0067:
            java.lang.String r4 = j
            java.lang.String r1 = "tileProvider must be UrlTileProvider or FileTileProvider"
            goto L_0x003e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.map.TileOverlayOptions.tileProvider(com.baidu.mapapi.map.TileProvider):com.baidu.mapapi.map.TileOverlayOptions");
    }
}
