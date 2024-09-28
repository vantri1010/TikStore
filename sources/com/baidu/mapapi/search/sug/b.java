package com.baidu.mapapi.search.sug;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.sug.SuggestionResult;

final class b implements Parcelable.Creator<SuggestionResult.SuggestionInfo> {
    b() {
    }

    /* renamed from: a */
    public SuggestionResult.SuggestionInfo createFromParcel(Parcel parcel) {
        return new SuggestionResult.SuggestionInfo(parcel);
    }

    /* renamed from: a */
    public SuggestionResult.SuggestionInfo[] newArray(int i) {
        return new SuggestionResult.SuggestionInfo[i];
    }
}
