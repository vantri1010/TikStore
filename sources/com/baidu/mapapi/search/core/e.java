package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class e implements Parcelable.Creator<PoiChildrenInfo> {
    e() {
    }

    /* renamed from: a */
    public PoiChildrenInfo createFromParcel(Parcel parcel) {
        return new PoiChildrenInfo(parcel);
    }

    /* renamed from: a */
    public PoiChildrenInfo[] newArray(int i) {
        return new PoiChildrenInfo[i];
    }
}
