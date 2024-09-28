package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.route.MassTransitRouteLine;

final class k implements Parcelable.Creator<MassTransitRouteLine.TransitStep.TrafficCondition> {
    k() {
    }

    /* renamed from: a */
    public MassTransitRouteLine.TransitStep.TrafficCondition createFromParcel(Parcel parcel) {
        return new MassTransitRouteLine.TransitStep.TrafficCondition(parcel);
    }

    /* renamed from: a */
    public MassTransitRouteLine.TransitStep.TrafficCondition[] newArray(int i) {
        return new MassTransitRouteLine.TransitStep.TrafficCondition[i];
    }
}
