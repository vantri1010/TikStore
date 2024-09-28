package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.TaxiInfo;
import java.util.ArrayList;
import java.util.List;

public class WalkingRouteResult extends SearchResult implements Parcelable {
    public static final Parcelable.Creator<WalkingRouteResult> CREATOR = new t();
    private List<WalkingRouteLine> a;
    private TaxiInfo b;
    private SuggestAddrInfo c;

    public WalkingRouteResult() {
    }

    protected WalkingRouteResult(Parcel parcel) {
        ArrayList arrayList = new ArrayList();
        this.a = arrayList;
        parcel.readList(arrayList, WalkingRouteLine.class.getClassLoader());
        this.b = (TaxiInfo) parcel.readParcelable(TaxiInfo.class.getClassLoader());
        this.c = (SuggestAddrInfo) parcel.readParcelable(SuggestAddrInfo.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public List<WalkingRouteLine> getRouteLines() {
        return this.a;
    }

    public SuggestAddrInfo getSuggestAddrInfo() {
        return this.c;
    }

    public TaxiInfo getTaxiInfo() {
        return this.b;
    }

    public void setRouteLines(List<WalkingRouteLine> list) {
        this.a = list;
    }

    public void setSuggestAddrInfo(SuggestAddrInfo suggestAddrInfo) {
        this.c = suggestAddrInfo;
    }

    public void setTaxiInfo(TaxiInfo taxiInfo) {
        this.b = taxiInfo;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(this.a);
        parcel.writeParcelable(this.b, 1);
        parcel.writeParcelable(this.c, 1);
    }
}
