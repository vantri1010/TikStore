package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;

final class t implements Parcelable.Creator<WalkingRouteResult> {
    t() {
    }

    /* renamed from: a */
    public WalkingRouteResult createFromParcel(Parcel parcel) {
        return new WalkingRouteResult(parcel);
    }

    /* renamed from: a */
    public WalkingRouteResult[] newArray(int i) {
        return new WalkingRouteResult[i];
    }
}
