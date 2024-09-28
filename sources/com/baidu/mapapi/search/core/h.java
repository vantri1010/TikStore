package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.core.PoiInfo;

final class h implements Parcelable.Creator<PoiInfo.ParentPoiInfo> {
    h() {
    }

    /* renamed from: a */
    public PoiInfo.ParentPoiInfo createFromParcel(Parcel parcel) {
        return new PoiInfo.ParentPoiInfo(parcel);
    }

    /* renamed from: a */
    public PoiInfo.ParentPoiInfo[] newArray(int i) {
        return new PoiInfo.ParentPoiInfo[i];
    }
}
