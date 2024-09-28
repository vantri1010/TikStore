package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

public class TaxiInfo implements Parcelable {
    public static final Parcelable.Creator<TaxiInfo> CREATOR = new m();
    private float a;
    private String b;
    private int c;
    private int d;
    private float e;
    private float f;

    public TaxiInfo() {
    }

    TaxiInfo(Parcel parcel) {
        this.a = parcel.readFloat();
        this.b = parcel.readString();
        this.c = parcel.readInt();
        this.d = parcel.readInt();
        this.e = parcel.readFloat();
        this.f = parcel.readFloat();
    }

    public int describeContents() {
        return 0;
    }

    public String getDesc() {
        return this.b;
    }

    public int getDistance() {
        return this.c;
    }

    public int getDuration() {
        return this.d;
    }

    public float getPerKMPrice() {
        return this.e;
    }

    public float getStartPrice() {
        return this.f;
    }

    public float getTotalPrice() {
        return this.a;
    }

    public void setDesc(String str) {
        this.b = str;
    }

    public void setDistance(int i) {
        this.c = i;
    }

    public void setDuration(int i) {
        this.d = i;
    }

    public void setPerKMPrice(float f2) {
        this.e = f2;
    }

    public void setStartPrice(float f2) {
        this.f = f2;
    }

    public void setTotalPrice(float f2) {
        this.a = f2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.a);
        parcel.writeString(this.b);
        parcel.writeInt(this.c);
        parcel.writeInt(this.d);
        parcel.writeFloat(this.e);
        parcel.writeFloat(this.f);
    }
}
