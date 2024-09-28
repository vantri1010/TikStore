package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

public class CoachInfo extends TransitBaseInfo {
    public static final Parcelable.Creator<CoachInfo> CREATOR = new c();
    private double a;
    private String b;
    private String c;
    private String d;

    public CoachInfo() {
    }

    protected CoachInfo(Parcel parcel) {
        super(parcel);
        this.a = parcel.readDouble();
        this.b = parcel.readString();
        this.c = parcel.readString();
        this.d = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public String getBooking() {
        return this.b;
    }

    public double getPrice() {
        return this.a;
    }

    public String getProviderName() {
        return this.c;
    }

    public String getProviderUrl() {
        return this.d;
    }

    public void setBooking(String str) {
        this.b = str;
    }

    public void setPrice(double d2) {
        this.a = d2;
    }

    public void setProviderName(String str) {
        this.c = str;
    }

    public void setProviderUrl(String str) {
        this.d = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeDouble(this.a);
        parcel.writeString(this.b);
        parcel.writeString(this.c);
        parcel.writeString(this.d);
    }
}
