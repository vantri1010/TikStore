package com.baidu.location;

import android.os.Parcel;
import android.os.Parcelable;

final class e implements Parcelable.Creator<PoiRegion> {
    e() {
    }

    public PoiRegion createFromParcel(Parcel parcel) {
        return new PoiRegion(parcel.readString(), parcel.readString(), parcel.readString());
    }

    public PoiRegion[] newArray(int i) {
        return new PoiRegion[i];
    }
}
