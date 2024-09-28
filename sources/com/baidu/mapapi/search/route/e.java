package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.route.DrivingRouteLine;

final class e implements Parcelable.Creator<DrivingRouteLine.DrivingStep> {
    e() {
    }

    /* renamed from: a */
    public DrivingRouteLine.DrivingStep createFromParcel(Parcel parcel) {
        return new DrivingRouteLine.DrivingStep(parcel);
    }

    /* renamed from: a */
    public DrivingRouteLine.DrivingStep[] newArray(int i) {
        return new DrivingRouteLine.DrivingStep[i];
    }
}
