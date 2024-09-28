package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.route.MassTransitRouteLine;

final class j implements Parcelable.Creator<MassTransitRouteLine.TransitStep> {
    j() {
    }

    /* renamed from: a */
    public MassTransitRouteLine.TransitStep createFromParcel(Parcel parcel) {
        return new MassTransitRouteLine.TransitStep(parcel);
    }

    /* renamed from: a */
    public MassTransitRouteLine.TransitStep[] newArray(int i) {
        return new MassTransitRouteLine.TransitStep[i];
    }
}
