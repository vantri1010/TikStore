package com.baidu.mapapi.search.poi;

import android.os.Parcel;
import android.os.Parcelable;

final class b implements Parcelable.Creator<PoiDetailSearchResult> {
    b() {
    }

    /* renamed from: a */
    public PoiDetailSearchResult createFromParcel(Parcel parcel) {
        return new PoiDetailSearchResult(parcel);
    }

    /* renamed from: a */
    public PoiDetailSearchResult[] newArray(int i) {
        return new PoiDetailSearchResult[i];
    }
}
