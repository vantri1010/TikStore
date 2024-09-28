package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.model.LatLng;

public class PoiChildrenInfo implements Parcelable {
    public static final Parcelable.Creator<PoiChildrenInfo> CREATOR = new e();
    private String a;
    private String b;
    private String c;
    private String d;
    private LatLng e;
    private String f;

    public PoiChildrenInfo() {
    }

    protected PoiChildrenInfo(Parcel parcel) {
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.c = parcel.readString();
        this.d = parcel.readString();
        this.e = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
        this.f = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public String getAddress() {
        return this.f;
    }

    public LatLng getLocation() {
        return this.e;
    }

    public String getName() {
        return this.b;
    }

    public String getShowName() {
        return this.c;
    }

    public String getTag() {
        return this.d;
    }

    public String getUid() {
        return this.a;
    }

    public void setAddress(String str) {
        this.f = str;
    }

    public void setLocation(LatLng latLng) {
        this.e = latLng;
    }

    public void setName(String str) {
        this.b = str;
    }

    public void setShowName(String str) {
        this.c = str;
    }

    public void setTag(String str) {
        this.d = str;
    }

    public void setUid(String str) {
        this.a = str;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("PoiChildrenInfo: ");
        stringBuffer.append("uid = ");
        stringBuffer.append(this.a);
        stringBuffer.append("; name = ");
        stringBuffer.append(this.b);
        stringBuffer.append("; showName = ");
        stringBuffer.append(this.c);
        stringBuffer.append("; tag = ");
        stringBuffer.append(this.d);
        stringBuffer.append("; location = ");
        LatLng latLng = this.e;
        stringBuffer.append(latLng != null ? latLng.toString() : "null");
        stringBuffer.append("; address = ");
        stringBuffer.append(this.f);
        return stringBuffer.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeString(this.c);
        parcel.writeString(this.d);
        parcel.writeParcelable(this.e, i);
        parcel.writeString(this.f);
    }
}
