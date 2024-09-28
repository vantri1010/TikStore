package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;

final class a implements Parcelable.Creator<BikingRouteLine> {
    a() {
    }

    /* renamed from: a */
    public BikingRouteLine createFromParcel(Parcel parcel) {
        return new BikingRouteLine(parcel);
    }

    /* renamed from: a */
    public BikingRouteLine[] newArray(int i) {
        return new BikingRouteLine[i];
    }
}
