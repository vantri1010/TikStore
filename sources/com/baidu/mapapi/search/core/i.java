package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class i implements Parcelable.Creator<PriceInfo> {
    i() {
    }

    /* renamed from: a */
    public PriceInfo createFromParcel(Parcel parcel) {
        return new PriceInfo(parcel);
    }

    /* renamed from: a */
    public PriceInfo[] newArray(int i) {
        return new PriceInfo[i];
    }
}
