package com.baidu.mapapi.search.geocode;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

final class c implements Parcelable.Creator<ReverseGeoCodeResult.AddressComponent> {
    c() {
    }

    /* renamed from: a */
    public ReverseGeoCodeResult.AddressComponent createFromParcel(Parcel parcel) {
        return new ReverseGeoCodeResult.AddressComponent(parcel);
    }

    /* renamed from: a */
    public ReverseGeoCodeResult.AddressComponent[] newArray(int i) {
        return new ReverseGeoCodeResult.AddressComponent[i];
    }
}
