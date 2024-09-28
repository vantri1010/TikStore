package com.baidu.mapapi.map;

import android.view.View;
import com.baidu.mapapi.common.SysOSUtil;
import com.baidu.mapapi.model.LatLng;

public class InfoWindow {
    BitmapDescriptor a;
    View b;
    LatLng c;
    OnInfoWindowClickListener d;
    a e;
    int f;
    boolean g = false;
    int h = SysOSUtil.getDensityDpi();
    boolean i = false;
    boolean j = false;
    boolean k = false;
    private String l = "";

    public interface OnInfoWindowClickListener {
        void onInfoWindowClick();
    }

    interface a {
        void a(InfoWindow infoWindow);

        void b(InfoWindow infoWindow);
    }

    public InfoWindow(View view, LatLng latLng, int i2) {
        if (view == null || latLng == null) {
            throw new IllegalArgumentException("BDMapSDKException: view and position can not be null");
        }
        this.b = view;
        this.c = latLng;
        this.f = i2;
        this.j = true;
    }

    public InfoWindow(View view, LatLng latLng, int i2, boolean z, int i3) {
        if (view == null || latLng == null) {
            throw new IllegalArgumentException("BDMapSDKException: view and position can not be null");
        }
        this.b = view;
        this.c = latLng;
        this.f = i2;
        this.g = z;
        this.h = i3;
        this.j = true;
    }

    public InfoWindow(BitmapDescriptor bitmapDescriptor, LatLng latLng, int i2, OnInfoWindowClickListener onInfoWindowClickListener) {
        if (bitmapDescriptor == null || latLng == null) {
            throw new IllegalArgumentException("BDMapSDKException: bitmapDescriptor and position can not be null");
        }
        this.a = bitmapDescriptor;
        this.c = latLng;
        this.d = onInfoWindowClickListener;
        this.f = i2;
        this.k = true;
    }

    public BitmapDescriptor getBitmapDescriptor() {
        return this.a;
    }

    public LatLng getPosition() {
        return this.c;
    }

    public String getTag() {
        return this.l;
    }

    public View getView() {
        return this.b;
    }

    public int getYOffset() {
        return this.f;
    }

    public void setBitmapDescriptor(BitmapDescriptor bitmapDescriptor) {
        if (bitmapDescriptor != null) {
            this.a = bitmapDescriptor;
            this.e.b(this);
        }
    }

    public void setPosition(LatLng latLng) {
        if (latLng != null) {
            this.c = latLng;
            this.e.b(this);
        }
    }

    public void setTag(String str) {
        this.l = str;
    }

    public void setView(View view) {
        if (view != null) {
            this.b = view;
            this.e.b(this);
        }
    }

    public void setYOffset(int i2) {
        this.f = i2;
        this.e.b(this);
    }
}
