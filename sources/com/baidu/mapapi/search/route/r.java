package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;

final class r implements Parcelable.Creator<WalkingRouteLine> {
    r() {
    }

    /* renamed from: a */
    public WalkingRouteLine createFromParcel(Parcel parcel) {
        return new WalkingRouteLine(parcel);
    }

    /* renamed from: a */
    public WalkingRouteLine[] newArray(int i) {
        return new WalkingRouteLine[i];
    }
}
