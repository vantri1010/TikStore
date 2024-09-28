package com.baidu.mapapi.search.poi;

import android.os.Parcel;
import android.os.Parcelable;

final class f implements Parcelable.Creator<PoiResult> {
    f() {
    }

    /* renamed from: a */
    public PoiResult createFromParcel(Parcel parcel) {
        return new PoiResult(parcel);
    }

    /* renamed from: a */
    public PoiResult[] newArray(int i) {
        return new PoiResult[i];
    }
}
