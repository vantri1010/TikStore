package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class j implements Parcelable.Creator<RouteNode> {
    j() {
    }

    /* renamed from: a */
    public RouteNode createFromParcel(Parcel parcel) {
        return new RouteNode(parcel);
    }

    /* renamed from: a */
    public RouteNode[] newArray(int i) {
        return new RouteNode[i];
    }
}
