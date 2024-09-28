package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

public class CityInfo implements Parcelable {
    public static final Parcelable.Creator<CityInfo> CREATOR = new b();
    public String city;
    public int num;

    public CityInfo() {
    }

    protected CityInfo(Parcel parcel) {
        this.city = parcel.readString();
        this.num = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.city);
        parcel.writeInt(this.num);
    }
}
