package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;

final class n implements Parcelable.Creator<TrainInfo> {
    n() {
    }

    /* renamed from: a */
    public TrainInfo createFromParcel(Parcel parcel) {
        return new TrainInfo(parcel);
    }

    /* renamed from: a */
    public TrainInfo[] newArray(int i) {
        return new TrainInfo[i];
    }
}
