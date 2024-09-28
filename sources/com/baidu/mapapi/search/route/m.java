package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;

final class m implements Parcelable.Creator<PlanNode> {
    m() {
    }

    /* renamed from: a */
    public PlanNode createFromParcel(Parcel parcel) {
        return new PlanNode(parcel);
    }

    /* renamed from: a */
    public PlanNode[] newArray(int i) {
        return new PlanNode[i];
    }
}
