package com.baidu.mapapi.search.poi;

import android.os.Parcel;
import android.os.Parcelable;

final class a implements Parcelable.Creator<PoiDetailResult> {
    a() {
    }

    /* renamed from: a */
    public PoiDetailResult createFromParcel(Parcel parcel) {
        return new PoiDetailResult(parcel);
    }

    /* renamed from: a */
    public PoiDetailResult[] newArray(int i) {
        return new PoiDetailResult[i];
    }
}
