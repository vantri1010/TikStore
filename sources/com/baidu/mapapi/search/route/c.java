package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;

final class c implements Parcelable.Creator<BikingRouteResult> {
    c() {
    }

    /* renamed from: a */
    public BikingRouteResult createFromParcel(Parcel parcel) {
        return new BikingRouteResult(parcel);
    }

    /* renamed from: a */
    public BikingRouteResult[] newArray(int i) {
        return new BikingRouteResult[i];
    }
}
