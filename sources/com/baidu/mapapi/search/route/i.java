package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;

final class i implements Parcelable.Creator<MassTransitRouteLine> {
    i() {
    }

    /* renamed from: a */
    public MassTransitRouteLine createFromParcel(Parcel parcel) {
        return new MassTransitRouteLine(parcel);
    }

    /* renamed from: a */
    public MassTransitRouteLine[] newArray(int i) {
        return new MassTransitRouteLine[i];
    }
}
