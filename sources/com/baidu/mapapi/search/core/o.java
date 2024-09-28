package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class o implements Parcelable.Creator<TransitBaseInfo> {
    o() {
    }

    /* renamed from: a */
    public TransitBaseInfo createFromParcel(Parcel parcel) {
        return new TransitBaseInfo(parcel);
    }

    /* renamed from: a */
    public TransitBaseInfo[] newArray(int i) {
        return new TransitBaseInfo[i];
    }
}
