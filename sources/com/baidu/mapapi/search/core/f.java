package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class f implements Parcelable.Creator<PoiDetailInfo> {
    f() {
    }

    /* renamed from: a */
    public PoiDetailInfo createFromParcel(Parcel parcel) {
        return new PoiDetailInfo(parcel);
    }

    /* renamed from: a */
    public PoiDetailInfo[] newArray(int i) {
        return new PoiDetailInfo[i];
    }
}
