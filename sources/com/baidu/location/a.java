package com.baidu.location;

import android.os.Parcel;
import android.os.Parcelable;

final class a implements Parcelable.Creator<BDLocation> {
    a() {
    }

    public BDLocation createFromParcel(Parcel parcel) {
        return new BDLocation(parcel, (a) null);
    }

    public BDLocation[] newArray(int i) {
        return new BDLocation[i];
    }
}
