package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class k implements Parcelable.Creator<RouteStep> {
    k() {
    }

    /* renamed from: a */
    public RouteStep createFromParcel(Parcel parcel) {
        return new RouteStep(parcel);
    }

    /* renamed from: a */
    public RouteStep[] newArray(int i) {
        return new RouteStep[i];
    }
}
