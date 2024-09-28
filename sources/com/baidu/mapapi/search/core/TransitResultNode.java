package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.model.LatLng;

public class TransitResultNode implements Parcelable {
    public static final Parcelable.Creator<TransitResultNode> CREATOR = new p();
    private int a;
    private String b = null;
    private LatLng c = null;
    private String d = null;

    public TransitResultNode(int i, String str, LatLng latLng, String str2) {
        this.a = i;
        this.b = str;
        this.c = latLng;
        this.d = str2;
    }

    protected TransitResultNode(Parcel parcel) {
        this.a = parcel.readInt();
        this.b = parcel.readString();
        this.c = (LatLng) parcel.readValue(LatLng.class.getClassLoader());
        this.d = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public int getCityId() {
        return this.a;
    }

    public String getCityName() {
        return this.b;
    }

    public LatLng getLocation() {
        return this.c;
    }

    public String getSearchWord() {
        return this.d;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.a);
        parcel.writeString(this.b);
        parcel.writeValue(this.c);
        parcel.writeString(this.d);
    }
}
