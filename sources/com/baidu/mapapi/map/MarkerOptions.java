package com.baidu.mapapi.map;

import android.graphics.Point;
import android.os.Bundle;
import com.baidu.mapapi.model.LatLng;
import java.util.ArrayList;

public final class MarkerOptions extends OverlayOptions {
    int a;
    boolean b = true;
    Bundle c;
    private LatLng d;
    private BitmapDescriptor e;
    private float f = 0.5f;
    private float g = 1.0f;
    private boolean h = true;
    private boolean i = false;
    private float j;
    private String k;
    private int l;
    private boolean m = false;
    private ArrayList<BitmapDescriptor> n;
    private int o = 20;
    private float p = 1.0f;
    private float q = 1.0f;
    private float r = 1.0f;
    private int s = MarkerAnimateType.none.ordinal();
    private boolean t = false;
    private Point u;
    private boolean v = true;
    private InfoWindow w;

    public enum MarkerAnimateType {
        none,
        drop,
        grow,
        jump
    }

    /* access modifiers changed from: package-private */
    public Overlay a() {
        Marker marker = new Marker();
        marker.B = this.b;
        marker.A = this.a;
        marker.C = this.c;
        LatLng latLng = this.d;
        if (latLng != null) {
            marker.a = latLng;
            if (this.e == null && this.n == null) {
                throw new IllegalStateException("BDMapSDKException: when you add marker, you must set the icon or icons");
            }
            marker.b = this.e;
            marker.c = this.f;
            marker.d = this.g;
            marker.e = this.h;
            marker.f = this.i;
            marker.g = this.j;
            marker.h = this.k;
            marker.i = this.l;
            marker.j = this.m;
            marker.p = this.n;
            marker.q = this.o;
            marker.l = this.r;
            marker.s = this.p;
            marker.t = this.q;
            marker.m = this.s;
            marker.n = this.t;
            marker.w = this.w;
            marker.o = this.v;
            Point point = this.u;
            if (point != null) {
                marker.v = point;
            }
            return marker;
        }
        throw new IllegalStateException("BDMapSDKException: when you add marker, you must set the position");
    }

    public MarkerOptions alpha(float f2) {
        if (f2 < 0.0f || f2 > 1.0f) {
            this.r = 1.0f;
            return this;
        }
        this.r = f2;
        return this;
    }

    public MarkerOptions anchor(float f2, float f3) {
        if (f2 >= 0.0f && f2 <= 1.0f && f3 >= 0.0f && f3 <= 1.0f) {
            this.f = f2;
            this.g = f3;
        }
        return this;
    }

    public MarkerOptions animateType(MarkerAnimateType markerAnimateType) {
        if (markerAnimateType == null) {
            markerAnimateType = MarkerAnimateType.none;
        }
        this.s = markerAnimateType.ordinal();
        return this;
    }

    public MarkerOptions clickable(boolean z) {
        this.v = z;
        return this;
    }

    public MarkerOptions draggable(boolean z) {
        this.i = z;
        return this;
    }

    public MarkerOptions extraInfo(Bundle bundle) {
        this.c = bundle;
        return this;
    }

    public MarkerOptions fixedScreenPosition(Point point) {
        this.u = point;
        this.t = true;
        return this;
    }

    public MarkerOptions flat(boolean z) {
        this.m = z;
        return this;
    }

    public float getAlpha() {
        return this.r;
    }

    public float getAnchorX() {
        return this.f;
    }

    public float getAnchorY() {
        return this.g;
    }

    public MarkerAnimateType getAnimateType() {
        int i2 = this.s;
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? MarkerAnimateType.none : MarkerAnimateType.jump : MarkerAnimateType.grow : MarkerAnimateType.drop;
    }

    public Bundle getExtraInfo() {
        return this.c;
    }

    public BitmapDescriptor getIcon() {
        return this.e;
    }

    public ArrayList<BitmapDescriptor> getIcons() {
        return this.n;
    }

    public int getPeriod() {
        return this.o;
    }

    public LatLng getPosition() {
        return this.d;
    }

    public float getRotate() {
        return this.j;
    }

    @Deprecated
    public String getTitle() {
        return this.k;
    }

    public int getZIndex() {
        return this.a;
    }

    public MarkerOptions icon(BitmapDescriptor bitmapDescriptor) {
        if (bitmapDescriptor != null) {
            this.e = bitmapDescriptor;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: marker's icon can not be null");
    }

    public MarkerOptions icons(ArrayList<BitmapDescriptor> arrayList) {
        if (arrayList == null) {
            throw new IllegalArgumentException("BDMapSDKException: marker's icons can not be null");
        } else if (arrayList.size() == 0) {
            return this;
        } else {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (arrayList.get(i2) == null || arrayList.get(i2).a == null) {
                    return this;
                }
            }
            this.n = arrayList;
            return this;
        }
    }

    public MarkerOptions infoWindow(InfoWindow infoWindow) {
        this.w = infoWindow;
        return this;
    }

    public boolean isDraggable() {
        return this.i;
    }

    public boolean isFlat() {
        return this.m;
    }

    public boolean isPerspective() {
        return this.h;
    }

    public boolean isVisible() {
        return this.b;
    }

    public MarkerOptions period(int i2) {
        if (i2 > 0) {
            this.o = i2;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: marker's period must be greater than zero ");
    }

    public MarkerOptions perspective(boolean z) {
        this.h = z;
        return this;
    }

    public MarkerOptions position(LatLng latLng) {
        if (latLng != null) {
            this.d = latLng;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: marker's position can not be null");
    }

    public MarkerOptions rotate(float f2) {
        while (f2 < 0.0f) {
            f2 += 360.0f;
        }
        this.j = f2 % 360.0f;
        return this;
    }

    public MarkerOptions scaleX(float f2) {
        if (f2 < 0.0f) {
            return this;
        }
        this.p = f2;
        return this;
    }

    public MarkerOptions scaleY(float f2) {
        if (f2 < 0.0f) {
            return this;
        }
        this.q = f2;
        return this;
    }

    @Deprecated
    public MarkerOptions title(String str) {
        this.k = str;
        return this;
    }

    public MarkerOptions visible(boolean z) {
        this.b = z;
        return this;
    }

    public MarkerOptions yOffset(int i2) {
        this.l = i2;
        return this;
    }

    public MarkerOptions zIndex(int i2) {
        this.a = i2;
        return this;
    }
}
