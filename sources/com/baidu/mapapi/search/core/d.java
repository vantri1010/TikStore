package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class d implements Parcelable.Creator<PlaneInfo> {
    d() {
    }

    /* renamed from: a */
    public PlaneInfo createFromParcel(Parcel parcel) {
        return new PlaneInfo(parcel);
    }

    /* renamed from: a */
    public PlaneInfo[] newArray(int i) {
        return new PlaneInfo[i];
    }
}
