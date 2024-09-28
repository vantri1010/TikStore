package com.baidu.mapapi.model;

import android.os.Parcel;
import android.os.Parcelable;

final class c implements Parcelable.Creator<ParcelItem> {
    c() {
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
