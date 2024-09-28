package com.baidu.mapapi.search.poi;

import android.os.Parcel;
import android.os.Parcelable;

final class e implements Parcelable.Creator<PoiIndoorResult> {
    e() {
    }

    /* renamed from: a */
    public PoiIndoorResult createFromParcel(Parcel parcel) {
        return new PoiIndoorResult(parcel);
    }

    /* renamed from: a */
    public PoiIndoorResult[] newArray(int i) {
        return new PoiIndoorResult[i];
    }
}
