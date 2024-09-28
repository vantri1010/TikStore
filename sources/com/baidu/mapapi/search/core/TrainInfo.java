package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

public class TrainInfo extends TransitBaseInfo {
    public static final Parcelable.Creator<TrainInfo> CREATOR = new n();
    private double a;
    private String b;

    public TrainInfo() {
    }

    protected TrainInfo(Parcel parcel) {
        super(parcel);
        this.a = parcel.readDouble();
        this.b = parcel.readString();
    }

    public void a(double d) {
        this.a = d;
    }

    public void a(String str) {
        this.b = str;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeDouble(this.a);
        parcel.writeString(this.b);
    }
}
