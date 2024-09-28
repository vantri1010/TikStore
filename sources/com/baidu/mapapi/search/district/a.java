package com.baidu.mapapi.search.district;

import android.os.Parcel;
import android.os.Parcelable;

final class a implements Parcelable.Creator<DistrictResult> {
    a() {
    }

    /* renamed from: a */
    public DistrictResult createFromParcel(Parcel parcel) {
        return new DistrictResult(parcel);
    }

    /* renamed from: a */
    public DistrictResult[] newArray(int i) {
        return new DistrictResult[i];
    }
}
