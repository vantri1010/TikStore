package com.baidu.mapapi.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public final class LatLng implements Parcelable {
    public static final Parcelable.Creator<LatLng> CREATOR = new a();
    private static final String a = LatLng.class.getSimpleName();
    public final double latitude;
    public final double latitudeE6;
    public final double longitude;
    public final double longitudeE6;

    public LatLng(double d, double d2) {
        if (Double.isNaN(d) || Double.isNaN(d2) || Double.isInfinite(d) || Double.isInfinite(d2)) {
            this.latitudeE6 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            this.longitudeE6 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            this.latitude = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            this.longitude = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            return;
        }
        double d3 = d * 1000000.0d;
        double d4 = d2 * 1000000.0d;
        this.latitudeE6 = d3;
        this.longitudeE6 = d4;
        this.latitude = d3 / 1000000.0d;
        this.longitude = d4 / 1000000.0d;
    }

    protected LatLng(Parcel parcel) {
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
        this.latitudeE6 = parcel.readDouble();
        this.longitudeE6 = parcel.readDouble();
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return ((new String("latitude: ") + this.latitude) + ", longitude: ") + this.longitude;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
        parcel.writeDouble(this.latitudeE6);
        parcel.writeDouble(this.longitudeE6);
    }
}
