package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;

final class l implements Parcelable.Creator<MassTransitRouteResult> {
    l() {
    }

    /* renamed from: a */
    public MassTransitRouteResult createFromParcel(Parcel parcel) {
        return new MassTransitRouteResult(parcel);
    }

    /* renamed from: a */
    public MassTransitRouteResult[] newArray(int i) {
        return new MassTransitRouteResult[i];
    }
}
