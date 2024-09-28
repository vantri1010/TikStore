package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.route.WalkingRouteLine;

final class s implements Parcelable.Creator<WalkingRouteLine.WalkingStep> {
    s() {
    }

    /* renamed from: a */
    public WalkingRouteLine.WalkingStep createFromParcel(Parcel parcel) {
        return new WalkingRouteLine.WalkingStep(parcel);
    }

    /* renamed from: a */
    public WalkingRouteLine.WalkingStep[] newArray(int i) {
        return new WalkingRouteLine.WalkingStep[i];
    }
}
