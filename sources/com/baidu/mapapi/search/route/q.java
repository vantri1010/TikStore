package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;

final class q implements Parcelable.Creator<TransitRouteResult> {
    q() {
    }

    /* renamed from: a */
    public TransitRouteResult createFromParcel(Parcel parcel) {
        return new TransitRouteResult(parcel);
    }

    /* renamed from: a */
    public TransitRouteResult[] newArray(int i) {
        return new TransitRouteResult[i];
    }
}
