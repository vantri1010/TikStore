package com.baidu.mapapi.search.poi;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.SearchResult;
import java.util.List;

public class PoiDetailSearchResult extends SearchResult implements Parcelable {
    public static final Parcelable.Creator<PoiDetailSearchResult> CREATOR = new b();
    private List<PoiDetailInfo> a;

    public PoiDetailSearchResult() {
    }

    protected PoiDetailSearchResult(Parcel parcel) {
        super(parcel);
        this.a = parcel.createTypedArrayList(PoiDetailInfo.CREATOR);
    }

    public PoiDetailSearchResult(SearchResult.ERRORNO errorno) {
        super(errorno);
    }

    public int describeContents() {
        return 0;
    }

    public List<PoiDetailInfo> getPoiDetailInfoList() {
        return this.a;
    }

    public void setPoiDetailInfoList(List<PoiDetailInfo> list) {
        this.a = list;
    }

    public String toString() {
        List<PoiDetailInfo> list = this.a;
        if (list == null || list.isEmpty()) {
            return "PoiDetailSearchResult is null";
        }
        StringBuffer stringBuffer = new StringBuffer("PoiDetailSearchResult:");
        for (int i = 0; i < this.a.size(); i++) {
            stringBuffer.append(" ");
            stringBuffer.append(i);
            stringBuffer.append(" ");
            PoiDetailInfo poiDetailInfo = this.a.get(i);
            stringBuffer.append(poiDetailInfo != null ? poiDetailInfo.toString() : "null");
        }
        return stringBuffer.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeTypedList(this.a);
    }
}
