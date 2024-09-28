package com.baidu.mapsdkplatform.comjni.tools;

import android.os.Parcel;
import android.os.Parcelable;

final class b implements Parcelable.Creator<ParcelItem> {
    b() {
    }

    /* renamed from: a */
    public ParcelItem createFromParcel(Parcel parcel) {
        ParcelItem parcelItem = new ParcelItem();
        parcelItem.setBundle(parcel.readBundle());
        return parcelItem;
    }

    /* renamed from: a */
    public ParcelItem[] newArray(int i) {
        return new ParcelItem[i];
    }
}
