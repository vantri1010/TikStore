package com.baidu.mapapi.search.poi;

import android.os.Parcel;
import android.os.Parcelable;

final class c implements Parcelable.Creator<PoiFilter> {
    c() {
    }

    /* renamed from: a */
    public PoiFilter createFromParcel(Parcel parcel) {
        return new PoiFilter(parcel);
    }

    /* renamed from: a */
    public PoiFilter[] newArray(int i) {
        return new PoiFilter[i];
    }
}
