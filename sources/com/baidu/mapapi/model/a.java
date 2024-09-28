package com.baidu.mapapi.model;

import android.os.Parcel;
import android.os.Parcelable;

final class a implements Parcelable.Creator<LatLng> {
    a() {
    }

    /* renamed from: a */
    public LatLng createFromParcel(Parcel parcel) {
        return new LatLng(parcel);
    }

    /* renamed from: a */
    public LatLng[] newArray(int i) {
        return new LatLng[i];
    }
}
