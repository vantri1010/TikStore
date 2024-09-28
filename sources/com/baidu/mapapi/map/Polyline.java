package com.baidu.mapapi.map;

import android.os.Bundle;
import android.util.Log;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.j;
import im.bclpbkiauv.keepalive.DaemonService;
import io.reactivex.annotations.SchedulerSupport;
import java.util.List;

public final class Polyline extends Overlay {
    int a;
    List<LatLng> b;
    int[] c;
    int[] d;
    int e = 5;
    boolean f = false;
    boolean g = false;
    boolean h = true;
    boolean i = true;
    BitmapDescriptor j;
    List<BitmapDescriptor> k;
    int l = 0;
    boolean m = true;

    Polyline() {
        this.type = j.polyline;
    }

    private Bundle a(boolean z) {
        if (z) {
            BitmapDescriptor fromAsset = BitmapDescriptorFactory.fromAsset(this.l == 1 ? "CircleDashTexture.png" : "lineDashTexture.png");
            if (fromAsset != null) {
                return fromAsset.b();
            }
        }
        return this.j.b();
    }

    private static void a(int[] iArr, Bundle bundle) {
        if (iArr != null && iArr.length > 0) {
            bundle.putIntArray("traffic_array", iArr);
        }
    }

    private Bundle b(boolean z) {
        if (z) {
            Bundle bundle = new Bundle();
            bundle.putInt("total", 1);
            BitmapDescriptor fromAsset = BitmapDescriptorFactory.fromAsset("lineDashTexture.png");
            if (fromAsset != null) {
                bundle.putBundle("texture_0", fromAsset.b());
            }
            return bundle;
        }
        Bundle bundle2 = new Bundle();
        int i2 = 0;
        for (int i3 = 0; i3 < this.k.size(); i3++) {
            if (this.k.get(i3) != null) {
                bundle2.putBundle("texture_" + String.valueOf(i2), this.k.get(i3).b());
                i2++;
            }
        }
        bundle2.putInt("total", i2);
        return bundle2;
    }

    private static void b(int[] iArr, Bundle bundle) {
        if (iArr != null && iArr.length > 0) {
            bundle.putIntArray("color_array", iArr);
            bundle.putInt("total", 1);
        }
    }

    /* access modifiers changed from: package-private */
    public Bundle a(Bundle bundle) {
        super.a(bundle);
        GeoPoint ll2mc = CoordUtil.ll2mc(this.b.get(0));
        bundle.putDouble("location_x", ll2mc.getLongitudeE6());
        bundle.putDouble("location_y", ll2mc.getLatitudeE6());
        bundle.putInt("width", this.e);
        Overlay.a(this.b, bundle);
        Overlay.a(this.a, bundle);
        a(this.c, bundle);
        b(this.d, bundle);
        int[] iArr = this.c;
        int i2 = 1;
        if (iArr != null && iArr.length > 0 && iArr.length > this.b.size() - 1) {
            Log.e("baidumapsdk", "the size of textureIndexs is larger than the size of points");
        }
        bundle.putInt("dotline", this.f ? 1 : 0);
        bundle.putInt("focus", this.g ? 1 : 0);
        bundle.putInt("isClickable", this.i ? 1 : 0);
        bundle.putInt("isThined", this.m ? 1 : 0);
        try {
            if (this.j != null) {
                bundle.putInt(SchedulerSupport.CUSTOM, 1);
                bundle.putBundle("image_info", a(false));
            } else {
                if (this.f) {
                    bundle.putBundle("image_info", a(true));
                    bundle.putInt("dotted_line_type", this.l);
                }
                bundle.putInt(SchedulerSupport.CUSTOM, 0);
            }
            if (this.k != null) {
                bundle.putInt("customlist", 1);
                bundle.putBundle("image_info_list", b(false));
            } else {
                if (this.f && this.d != null && this.d.length > 0) {
                    bundle.putBundle("image_info_list", b(true));
                }
                bundle.putInt("customlist", 0);
            }
            if (!this.h) {
                i2 = 0;
            }
            bundle.putInt(DaemonService.KEEP_CHANNEL_NAME, i2);
        } catch (Exception e2) {
            Log.e("baidumapsdk", "load texture resource failed!");
            bundle.putInt("dotline", 0);
        }
        return bundle;
    }

    public int getColor() {
        return this.a;
    }

    public int[] getColorList() {
        return this.d;
    }

    public int getDottedLineType() {
        return this.l;
    }

    public List<LatLng> getPoints() {
        return this.b;
    }

    public BitmapDescriptor getTexture() {
        return this.j;
    }

    public int getWidth() {
        return this.e;
    }

    public boolean isClickable() {
        return this.i;
    }

    public boolean isDottedLine() {
        return this.f;
    }

    public boolean isFocus() {
        return this.g;
    }

    public boolean isIsKeepScale() {
        return this.h;
    }

    public boolean isThined() {
        return this.m;
    }

    public void setClickable(boolean z) {
        this.i = z;
        this.listener.b(this);
    }

    public void setColor(int i2) {
        this.a = i2;
        this.listener.b(this);
    }

    public void setColorList(int[] iArr) {
        if (iArr == null || iArr.length == 0) {
            throw new IllegalArgumentException("BDMapSDKException: colorList can not empty");
        }
        this.d = iArr;
    }

    public void setDottedLine(boolean z) {
        this.f = z;
        this.listener.b(this);
    }

    public void setDottedLineType(PolylineDottedLineType polylineDottedLineType) {
        this.l = polylineDottedLineType.ordinal();
        this.listener.b(this);
    }

    public void setFocus(boolean z) {
        this.g = z;
        this.listener.b(this);
    }

    public void setIndexs(int[] iArr) {
        if (iArr == null || iArr.length == 0) {
            throw new IllegalArgumentException("BDMapSDKException: indexList can not empty");
        }
        this.c = iArr;
    }

    public void setIsKeepScale(boolean z) {
        this.h = z;
    }

    public void setPoints(List<LatLng> list) {
        if (list == null) {
            throw new IllegalArgumentException("BDMapSDKException: points list can not be null");
        } else if (list.size() < 2) {
            throw new IllegalArgumentException("BDMapSDKException: points count can not less than 2 or more than 10000");
        } else if (!list.contains((Object) null)) {
            this.b = list;
            this.listener.b(this);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: points list can not contains null");
        }
    }

    public void setTexture(BitmapDescriptor bitmapDescriptor) {
        this.j = bitmapDescriptor;
        this.listener.b(this);
    }

    public void setTextureList(List<BitmapDescriptor> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("BDMapSDKException: textureList can not empty");
        }
        this.k = list;
    }

    public void setThined(boolean z) {
        this.m = z;
        this.listener.b(this);
    }

    public void setWidth(int i2) {
        if (i2 > 0) {
            this.e = i2;
            this.listener.b(this);
        }
    }
}
