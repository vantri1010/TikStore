package com.baidu.location;

import android.os.Parcel;
import android.os.Parcelable;

final class d implements Parcelable.Creator<Poi> {
    d() {
    }

    public Poi createFromParcel(Parcel parcel) {
        return new Poi(parcel.readString(), parcel.readString(), parcel.readDouble(), parcel.readString(), parcel.readString());
    }

    public Poi[] newArray(int i) {
        return new Poi[i];
    }
}
