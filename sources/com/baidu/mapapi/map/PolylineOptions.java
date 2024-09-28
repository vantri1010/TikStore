package com.baidu.mapapi.map;

import android.os.Bundle;
import android.util.Log;
import com.baidu.mapapi.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public final class PolylineOptions extends OverlayOptions {
    int a;
    boolean b = true;
    Bundle c;
    private int d = -16777216;
    private List<LatLng> e;
    private List<Integer> f;
    private List<Integer> g;
    private int h = 5;
    private BitmapDescriptor i;
    private List<BitmapDescriptor> j;
    private boolean k = true;
    private boolean l = false;
    private boolean m = false;
    private boolean n = true;
    private int o = 0;
    private boolean p = true;

    /* access modifiers changed from: package-private */
    public Overlay a() {
        Polyline polyline = new Polyline();
        polyline.B = this.b;
        polyline.f = this.m;
        polyline.A = this.a;
        polyline.C = this.c;
        List<LatLng> list = this.e;
        if (list == null || list.size() < 2) {
            throw new IllegalStateException("BDMapSDKException: when you add polyline, you must at least supply 2 points");
        }
        polyline.b = this.e;
        polyline.a = this.d;
        polyline.e = this.h;
        polyline.j = this.i;
        polyline.k = this.j;
        polyline.g = this.k;
        polyline.h = this.l;
        polyline.i = this.n;
        polyline.l = this.o;
        polyline.m = this.p;
        List<Integer> list2 = this.f;
        if (list2 != null && list2.size() < this.e.size() - 1) {
            ArrayList arrayList = new ArrayList((this.e.size() - 1) - this.f.size());
            List<Integer> list3 = this.f;
            list3.addAll(list3.size(), arrayList);
        }
        List<Integer> list4 = this.f;
        int i2 = 0;
        if (list4 != null && list4.size() > 0) {
            int[] iArr = new int[this.f.size()];
            int i3 = 0;
            for (Integer intValue : this.f) {
                iArr[i3] = intValue.intValue();
                i3++;
            }
            polyline.c = iArr;
        }
        List<Integer> list5 = this.g;
        if (list5 != null && list5.size() < this.e.size() - 1) {
            ArrayList arrayList2 = new ArrayList((this.e.size() - 1) - this.g.size());
            List<Integer> list6 = this.g;
            list6.addAll(list6.size(), arrayList2);
        }
        List<Integer> list7 = this.g;
        if (list7 != null && list7.size() > 0) {
            int[] iArr2 = new int[this.g.size()];
            for (Integer intValue2 : this.g) {
                iArr2[i2] = intValue2.intValue();
                i2++;
            }
            polyline.d = iArr2;
        }
        return polyline;
    }

    public PolylineOptions clickable(boolean z) {
        this.n = z;
        return this;
    }

    public PolylineOptions color(int i2) {
        this.d = i2;
        return this;
    }

    public PolylineOptions colorsValues(List<Integer> list) {
        if (list == null) {
            throw new IllegalArgumentException("BDMapSDKException: colors list can not be null");
        } else if (!list.contains((Object) null)) {
            this.g = list;
            return this;
        } else {
            throw new IllegalArgumentException("BDMapSDKException: colors list can not contains null");
        }
    }

    public PolylineOptions customTexture(BitmapDescriptor bitmapDescriptor) {
        this.i = bitmapDescriptor;
        return this;
    }

    public PolylineOptions customTextureList(List<BitmapDescriptor> list) {
        if (list != null) {
            if (list.size() == 0) {
                Log.e("baidumapsdk", "custom texture list is empty,the texture will not work");
            }
            for (BitmapDescriptor bitmapDescriptor : list) {
                if (bitmapDescriptor == null) {
                    Log.e("baidumapsdk", "the custom texture item is null,it will be discard");
                }
            }
            this.j = list;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: customTexture list can not be null");
    }

    public PolylineOptions dottedLine(boolean z) {
        this.m = z;
        return this;
    }

    public PolylineOptions dottedLineType(PolylineDottedLineType polylineDottedLineType) {
        this.o = polylineDottedLineType.ordinal();
        return this;
    }

    public PolylineOptions extraInfo(Bundle bundle) {
        this.c = bundle;
        return this;
    }

    public PolylineOptions focus(boolean z) {
        this.k = z;
        return this;
    }

    public int getColor() {
        return this.d;
    }

    public BitmapDescriptor getCustomTexture() {
        return this.i;
    }

    public List<BitmapDescriptor> getCustomTextureList() {
        return this.j;
    }

    public Bundle getExtraInfo() {
        return this.c;
    }

    public List<LatLng> getPoints() {
        return this.e;
    }

    public List<Integer> getTextureIndexs() {
        return this.f;
    }

    public int getWidth() {
        return this.h;
    }

    public int getZIndex() {
        return this.a;
    }

    public boolean isDottedLine() {
        return this.m;
    }

    public boolean isFocus() {
        return this.k;
    }

    public PolylineOptions isThined(boolean z) {
        this.p = z;
        return this;
    }

    public boolean isVisible() {
        return this.b;
    }

    public PolylineOptions keepScale(boolean z) {
        this.l = z;
        return this;
    }

    public PolylineOptions points(List<LatLng> list) {
        if (list == null) {
            throw new IllegalArgumentException("BDMapSDKException: points list can not be null");
        } else if (list.size() < 2) {
            throw new IllegalArgumentException("BDMapSDKException: points count can not less than 2");
        } else if (!list.contains((Object) null)) {
            this.e = list;
            return this;
        } else {
            throw new IllegalArgumentException("BDMapSDKException: points list can not contains null");
        }
    }

    public PolylineOptions textureIndex(List<Integer> list) {
        if (list == null) {
            throw new IllegalArgumentException("BDMapSDKException: indexs list can not be null");
        } else if (!list.contains((Object) null)) {
            this.f = list;
            return this;
        } else {
            throw new IllegalArgumentException("BDMapSDKException: index list can not contains null");
        }
    }

    public PolylineOptions visible(boolean z) {
        this.b = z;
        return this;
    }

    public PolylineOptions width(int i2) {
        if (i2 > 0) {
            this.h = i2;
        }
        return this;
    }

    public PolylineOptions zIndex(int i2) {
        this.a = i2;
        return this;
    }
}
