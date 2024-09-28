package com.baidu.mapapi.search.geocode;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

final class d implements Parcelable.Creator<ReverseGeoCodeResult.PoiRegionsInfo> {
    d() {
    }

    /* renamed from: a */
    public ReverseGeoCodeResult.PoiRegionsInfo createFromParcel(Parcel parcel) {
        return new ReverseGeoCodeResult.PoiRegionsInfo(parcel);
    }

    /* renamed from: a */
    public ReverseGeoCodeResult.PoiRegionsInfo[] newArray(int i) {
        return new ReverseGeoCodeResult.PoiRegionsInfo[i];
    }
}
