package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class b implements Parcelable.Creator<CityInfo> {
    b() {
    }

    /* renamed from: a */
    public CityInfo createFromParcel(Parcel parcel) {
        return new CityInfo(parcel);
    }

    /* renamed from: a */
    public CityInfo[] newArray(int i) {
        return new CityInfo[i];
    }
}
