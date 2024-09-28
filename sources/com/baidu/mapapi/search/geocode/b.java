package com.baidu.mapapi.search.geocode;

import android.os.Parcel;
import android.os.Parcelable;

final class b implements Parcelable.Creator<ReverseGeoCodeResult> {
    b() {
    }

    /* renamed from: a */
    public ReverseGeoCodeResult createFromParcel(Parcel parcel) {
        return new ReverseGeoCodeResult(parcel);
    }

    /* renamed from: a */
    public ReverseGeoCodeResult[] newArray(int i) {
        return new ReverseGeoCodeResult[i];
    }
}
