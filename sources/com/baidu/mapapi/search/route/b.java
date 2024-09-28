package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.route.BikingRouteLine;

final class b implements Parcelable.Creator<BikingRouteLine.BikingStep> {
    b() {
    }

    /* renamed from: a */
    public BikingRouteLine.BikingStep createFromParcel(Parcel parcel) {
        return new BikingRouteLine.BikingStep(parcel);
    }

    /* renamed from: a */
    public BikingRouteLine.BikingStep[] newArray(int i) {
        return new BikingRouteLine.BikingStep[i];
    }
}
