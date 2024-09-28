package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.route.TransitRouteLine;

final class p implements Parcelable.Creator<TransitRouteLine.TransitStep> {
    p() {
    }

    /* renamed from: a */
    public TransitRouteLine.TransitStep createFromParcel(Parcel parcel) {
        return new TransitRouteLine.TransitStep(parcel);
    }

    /* renamed from: a */
    public TransitRouteLine.TransitStep[] newArray(int i) {
        return new TransitRouteLine.TransitStep[i];
    }
}
