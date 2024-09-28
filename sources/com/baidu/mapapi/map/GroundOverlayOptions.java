package com.baidu.mapapi.map;

import android.os.Bundle;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

public final class GroundOverlayOptions extends OverlayOptions {
    int a;
    boolean b = true;
    Bundle c;
    private BitmapDescriptor d;
    private LatLng e;
    private int f;
    private int g;
    private float h = 0.5f;
    private float i = 0.5f;
    private LatLngBounds j;
    private float k = 1.0f;

    /* access modifiers changed from: package-private */
    public Overlay a() {
        int i2;
        LatLngBounds latLngBounds;
        LatLng latLng;
        GroundOverlay groundOverlay = new GroundOverlay();
        groundOverlay.B = this.b;
        groundOverlay.A = this.a;
        groundOverlay.C = this.c;
        BitmapDescriptor bitmapDescriptor = this.d;
        if (bitmapDescriptor != null) {
            groundOverlay.b = bitmapDescriptor;
            if (this.j != null || (latLng = this.e) == null) {
                if (this.e != null || (latLngBounds = this.j) == null) {
                    throw new IllegalStateException("BDMapSDKException: when you add ground overlay, you must set one of position or bounds");
                }
                groundOverlay.h = latLngBounds;
                i2 = 1;
            } else if (this.f <= 0 || this.g <= 0) {
                throw new IllegalArgumentException("BDMapSDKException: when you add ground overlay, the width and height must greater than 0");
            } else {
                groundOverlay.c = latLng;
                groundOverlay.f = this.h;
                groundOverlay.g = this.i;
                groundOverlay.d = (double) this.f;
                groundOverlay.e = (double) this.g;
                i2 = 2;
            }
            groundOverlay.a = i2;
            groundOverlay.i = this.k;
            return groundOverlay;
        }
        throw new IllegalStateException("BDMapSDKException: when you add ground overlay, you must set the image");
    }

    public GroundOverlayOptions anchor(float f2, float f3) {
        if (f2 >= 0.0f && f2 <= 1.0f && f3 >= 0.0f && f3 <= 1.0f) {
            this.h = f2;
            this.i = f3;
        }
        return this;
    }

    public GroundOverlayOptions dimensions(int i2) {
        this.f = i2;
        this.g = Integer.MAX_VALUE;
        return this;
    }

    public GroundOverlayOptions dimensions(int i2, int i3) {
        this.f = i2;
        this.g = i3;
        return this;
    }

    public GroundOverlayOptions extraInfo(Bundle bundle) {
        this.c = bundle;
        return this;
    }

    public float getAnchorX() {
        return this.h;
    }

    public float getAnchorY() {
        return this.i;
    }

    public LatLngBounds getBounds() {
        return this.j;
    }

    public Bundle getExtraInfo() {
        return this.c;
    }

    public int getHeight() {
        int i2 = this.g;
        return i2 == Integer.MAX_VALUE ? (int) (((float) (this.f * this.d.a.getHeight())) / ((float) this.d.a.getWidth())) : i2;
    }

    public BitmapDescriptor getImage() {
        return this.d;
    }

    public LatLng getPosition() {
        return this.e;
    }

    public float getTransparency() {
        return this.k;
    }

    public int getWidth() {
        return this.f;
    }

    public int getZIndex() {
        return this.a;
    }

    public GroundOverlayOptions image(BitmapDescriptor bitmapDescriptor) {
        if (bitmapDescriptor != null) {
            this.d = bitmapDescriptor;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: image can not be null");
    }

    public boolean isVisible() {
        return this.b;
    }

    public GroundOverlayOptions position(LatLng latLng) {
        if (latLng != null) {
            this.e = latLng;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: position can not be null");
    }

    public GroundOverlayOptions positionFromBounds(LatLngBounds latLngBounds) {
        if (latLngBounds != null) {
            this.j = latLngBounds;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: bounds can not be null");
    }

    public GroundOverlayOptions transparency(float f2) {
        if (f2 <= 1.0f && f2 >= 0.0f) {
            this.k = f2;
        }
        return this;
    }

    public GroundOverlayOptions visible(boolean z) {
        this.b = z;
        return this;
    }

    public GroundOverlayOptions zIndex(int i2) {
        this.a = i2;
        return this;
    }
}
