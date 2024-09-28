package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class p implements Parcelable.Creator<TransitResultNode> {
    p() {
    }

    /* renamed from: a */
    public TransitResultNode createFromParcel(Parcel parcel) {
        return new TransitResultNode(parcel);
    }

    /* renamed from: a */
    public TransitResultNode[] newArray(int i) {
        return new TransitResultNode[i];
    }
}
