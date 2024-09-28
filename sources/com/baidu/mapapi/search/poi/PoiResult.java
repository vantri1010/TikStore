package com.baidu.mapapi.search.poi;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import java.util.List;

public class PoiResult extends SearchResult implements Parcelable {
    public static final Parcelable.Creator<PoiResult> CREATOR = new f();
    private int a = 0;
    private int b = 0;
    private int c = 0;
    private int d = 0;
    private List<PoiInfo> e;
    private boolean f = false;
    private List<PoiAddrInfo> g;
    private List<CityInfo> h;

    public PoiResult() {
    }

    protected PoiResult(Parcel parcel) {
        super(parcel);
        boolean z = false;
        this.a = parcel.readInt();
        this.b = parcel.readInt();
        this.c = parcel.readInt();
        this.d = parcel.readInt();
        this.e = parcel.createTypedArrayList(PoiInfo.CREATOR);
        this.f = parcel.readByte() != 0 ? true : z;
        this.h = parcel.createTypedArrayList(CityInfo.CREATOR);
    }

    public PoiResult(SearchResult.ERRORNO errorno) {
        super(errorno);
    }

    public int describeContents() {
        return 0;
    }

    public List<PoiAddrInfo> getAllAddr() {
        return this.g;
    }

    public List<PoiInfo> getAllPoi() {
        return this.e;
    }

    public int getCurrentPageCapacity() {
        return this.c;
    }

    public int getCurrentPageNum() {
        return this.a;
    }

    public List<CityInfo> getSuggestCityList() {
        return this.h;
    }

    public int getTotalPageNum() {
        return this.b;
    }

    public int getTotalPoiNum() {
        return this.d;
    }

    public boolean isHasAddrInfo() {
        return this.f;
    }

    public void setAddrInfo(List<PoiAddrInfo> list) {
        this.g = list;
    }

    public void setCurrentPageCapacity(int i) {
        this.c = i;
    }

    public void setCurrentPageNum(int i) {
        this.a = i;
    }

    public void setHasAddrInfo(boolean z) {
        this.f = z;
    }

    public void setPoiInfo(List<PoiInfo> list) {
        this.e = list;
    }

    public void setSuggestCityList(List<CityInfo> list) {
        this.h = list;
    }

    public void setTotalPageNum(int i) {
        this.b = i;
    }

    public void setTotalPoiNum(int i) {
        this.d = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.a);
        parcel.writeInt(this.b);
        parcel.writeInt(this.c);
        parcel.writeInt(this.d);
        parcel.writeTypedList(this.e);
        parcel.writeByte(this.f ? (byte) 1 : 0);
        parcel.writeTypedList(this.h);
    }
}
