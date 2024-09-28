package com.baidu.mapapi.map;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapsdkplatform.comapi.map.ab;

public final class BaiduMapOptions implements Parcelable {
    public static final Parcelable.Creator<BaiduMapOptions> CREATOR = new g();
    MapStatus a;
    boolean b;
    int c;
    boolean d;
    boolean e;
    boolean f;
    boolean g;
    boolean h;
    boolean i;
    LogoPosition j;
    Point k;
    Point l;

    public BaiduMapOptions() {
        this.a = new MapStatus(0.0f, new LatLng(39.914935d, 116.403119d), 0.0f, 12.0f, (Point) null, (LatLngBounds) null);
        this.b = true;
        this.c = 1;
        this.d = true;
        this.e = true;
        this.f = true;
        this.g = true;
        this.h = true;
        this.i = true;
    }

    protected BaiduMapOptions(Parcel parcel) {
        this.a = new MapStatus(0.0f, new LatLng(39.914935d, 116.403119d), 0.0f, 12.0f, (Point) null, (LatLngBounds) null);
        boolean z = true;
        this.b = true;
        this.c = 1;
        this.d = true;
        this.e = true;
        this.f = true;
        this.g = true;
        this.h = true;
        this.i = true;
        this.a = (MapStatus) parcel.readParcelable(MapStatus.class.getClassLoader());
        this.b = parcel.readByte() != 0;
        this.c = parcel.readInt();
        this.d = parcel.readByte() != 0;
        this.e = parcel.readByte() != 0;
        this.f = parcel.readByte() != 0;
        this.g = parcel.readByte() != 0;
        this.h = parcel.readByte() != 0;
        this.i = parcel.readByte() == 0 ? false : z;
        this.k = (Point) parcel.readParcelable(Point.class.getClassLoader());
        this.l = (Point) parcel.readParcelable(Point.class.getClassLoader());
    }

    /* access modifiers changed from: package-private */
    public ab a() {
        return new ab().a(this.a.c()).a(this.b).a(this.c).b(this.d).c(this.e).d(this.f).e(this.g);
    }

    public BaiduMapOptions compassEnabled(boolean z) {
        this.b = z;
        return this;
    }

    public int describeContents() {
        return 0;
    }

    public BaiduMapOptions logoPosition(LogoPosition logoPosition) {
        this.j = logoPosition;
        return this;
    }

    public BaiduMapOptions mapStatus(MapStatus mapStatus) {
        if (mapStatus != null) {
            this.a = mapStatus;
        }
        return this;
    }

    public BaiduMapOptions mapType(int i2) {
        this.c = i2;
        return this;
    }

    public BaiduMapOptions overlookingGesturesEnabled(boolean z) {
        this.f = z;
        return this;
    }

    public BaiduMapOptions rotateGesturesEnabled(boolean z) {
        this.d = z;
        return this;
    }

    public BaiduMapOptions scaleControlEnabled(boolean z) {
        this.i = z;
        return this;
    }

    public BaiduMapOptions scaleControlPosition(Point point) {
        this.k = point;
        return this;
    }

    public BaiduMapOptions scrollGesturesEnabled(boolean z) {
        this.e = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i2) {
        parcel.writeParcelable(this.a, i2);
        parcel.writeByte(this.b ? (byte) 1 : 0);
        parcel.writeInt(this.c);
        parcel.writeByte(this.d ? (byte) 1 : 0);
        parcel.writeByte(this.e ? (byte) 1 : 0);
        parcel.writeByte(this.f ? (byte) 1 : 0);
        parcel.writeByte(this.g ? (byte) 1 : 0);
        parcel.writeByte(this.h ? (byte) 1 : 0);
        parcel.writeByte(this.i ? (byte) 1 : 0);
        parcel.writeParcelable(this.k, i2);
        parcel.writeParcelable(this.l, i2);
    }

    public BaiduMapOptions zoomControlsEnabled(boolean z) {
        this.h = z;
        return this;
    }

    public BaiduMapOptions zoomControlsPosition(Point point) {
        this.l = point;
        return this;
    }

    public BaiduMapOptions zoomGesturesEnabled(boolean z) {
        this.g = z;
        return this;
    }
}
