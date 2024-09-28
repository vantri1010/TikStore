package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class c implements Parcelable.Creator<CoachInfo> {
    c() {
    }

    /* renamed from: a */
    public CoachInfo createFromParcel(Parcel parcel) {
        return new CoachInfo(parcel);
    }

    /* renamed from: a */
    public CoachInfo[] newArray(int i) {
        return new CoachInfo[i];
    }
}
