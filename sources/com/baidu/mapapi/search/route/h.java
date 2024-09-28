package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;

final class h implements Parcelable.Creator<IndoorRouteResult> {
    h() {
    }

    /* renamed from: a */
    public IndoorRouteResult createFromParcel(Parcel parcel) {
        return new IndoorRouteResult(parcel);
    }

    /* renamed from: a */
    public IndoorRouteResult[] newArray(int i) {
        return new IndoorRouteResult[i];
    }
}
