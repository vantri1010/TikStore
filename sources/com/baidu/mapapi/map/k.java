package com.baidu.mapapi.map;

import android.os.Parcel;
import android.os.Parcelable;

final class k implements Parcelable.Creator<MapStatus> {
    k() {
    }

    /* renamed from: a */
    public MapStatus createFromParcel(Parcel parcel) {
        return new MapStatus(parcel);
    }

    /* renamed from: a */
    public MapStatus[] newArray(int i) {
        return new MapStatus[i];
    }
}
