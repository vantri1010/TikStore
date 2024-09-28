package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

public class BusInfo extends TransitBaseInfo {
    public static final Parcelable.Creator<BusInfo> CREATOR = new a();
    private int a;
    private int b;

    public BusInfo() {
    }

    protected BusInfo(Parcel parcel) {
        super(parcel);
        this.a = parcel.readInt();
        this.b = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public int getStopNum() {
        return this.b;
    }

    public int getType() {
        return this.a;
    }

    public void setStopNum(int i) {
        this.b = i;
    }

    public void setType(int i) {
        this.a = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.a);
        parcel.writeInt(this.b);
    }
}
