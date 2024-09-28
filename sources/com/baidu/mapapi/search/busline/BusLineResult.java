package com.baidu.mapapi.search.busline;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.SearchResult;
import java.util.Date;
import java.util.List;

public class BusLineResult extends SearchResult implements Parcelable {
    public static final Parcelable.Creator<BusLineResult> CREATOR = new a();
    private String a = null;
    private String b = null;
    private boolean c;
    private Date d;
    private Date e;
    private String f;
    private List<BusStation> g = null;
    private List<BusStep> h = null;
    private float i;
    private float j;
    private String k = null;

    public static class BusStation extends RouteNode {
    }

    public static class BusStep extends RouteStep {
    }

    public BusLineResult() {
    }

    BusLineResult(Parcel parcel) {
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.c = ((Boolean) parcel.readValue(Boolean.class.getClassLoader())).booleanValue();
        this.d = (Date) parcel.readValue(Date.class.getClassLoader());
        this.e = (Date) parcel.readValue(Date.class.getClassLoader());
        this.f = parcel.readString();
        this.g = parcel.readArrayList(BusStation.class.getClassLoader());
        this.h = parcel.readArrayList(RouteStep.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public float getBasePrice() {
        return this.i;
    }

    public String getBusCompany() {
        return this.a;
    }

    public String getBusLineName() {
        return this.b;
    }

    public Date getEndTime() {
        return this.e;
    }

    public String getLineDirection() {
        return this.k;
    }

    public float getMaxPrice() {
        return this.j;
    }

    public Date getStartTime() {
        return this.d;
    }

    public List<BusStation> getStations() {
        return this.g;
    }

    public List<BusStep> getSteps() {
        return this.h;
    }

    public String getUid() {
        return this.f;
    }

    public boolean isMonthTicket() {
        return this.c;
    }

    public void setBasePrice(float f2) {
        this.i = f2;
    }

    public void setBusLineName(String str) {
        this.b = str;
    }

    public void setEndTime(Date date) {
        this.e = date;
    }

    public void setLineDirection(String str) {
        this.k = str;
    }

    public void setMaxPrice(float f2) {
        this.j = f2;
    }

    public void setMonthTicket(boolean z) {
        this.c = z;
    }

    public void setStartTime(Date date) {
        this.d = date;
    }

    public void setStations(List<BusStation> list) {
        this.g = list;
    }

    public void setSteps(List<BusStep> list) {
        this.h = list;
    }

    public void setUid(String str) {
        this.f = str;
    }

    public void writeToParcel(Parcel parcel, int i2) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeValue(Boolean.valueOf(this.c));
        parcel.writeValue(this.d);
        parcel.writeValue(this.e);
        parcel.writeString(this.f);
        parcel.writeList(this.g);
        parcel.writeList(this.h);
    }
}
