package com.baidu.mapapi.search.sug;

import android.os.Parcel;
import android.os.Parcelable;

final class a implements Parcelable.Creator<SuggestionResult> {
    a() {
    }

    /* renamed from: a */
    public SuggestionResult createFromParcel(Parcel parcel) {
        return new SuggestionResult(parcel);
    }

    /* renamed from: a */
    public SuggestionResult[] newArray(int i) {
        return new SuggestionResult[i];
    }
}
