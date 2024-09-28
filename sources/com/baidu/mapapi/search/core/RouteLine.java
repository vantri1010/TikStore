package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import java.util.List;

public class RouteLine<T extends RouteStep> implements Parcelable {
    TYPE a;
    private RouteNode b;
    private RouteNode c;
    private String d;
    private List<T> e;
    private int f;
    private int g;

    protected enum TYPE {
        DRIVESTEP(0),
        TRANSITSTEP(1),
        WALKSTEP(2),
        BIKINGSTEP(3);
        
        private int a;

        private TYPE(int i) {
            this.a = i;
        }

        /* access modifiers changed from: private */
        public int a() {
            return this.a;
        }
    }

    protected RouteLine() {
    }

    protected RouteLine(Parcel parcel) {
        Parcelable.Creator creator;
        int readInt = parcel.readInt();
        this.b = (RouteNode) parcel.readValue(RouteNode.class.getClassLoader());
        this.c = (RouteNode) parcel.readValue(RouteNode.class.getClassLoader());
        this.d = parcel.readString();
        if (readInt == 0) {
            creator = DrivingRouteLine.DrivingStep.CREATOR;
        } else if (readInt == 1) {
            creator = TransitRouteLine.TransitStep.CREATOR;
        } else if (readInt != 2) {
            if (readInt == 3) {
                creator = BikingRouteLine.BikingStep.CREATOR;
            }
            this.f = parcel.readInt();
            this.g = parcel.readInt();
        } else {
            creator = WalkingRouteLine.WalkingStep.CREATOR;
        }
        this.e = parcel.createTypedArrayList(creator);
        this.f = parcel.readInt();
        this.g = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public List<T> getAllStep() {
        return this.e;
    }

    public int getDistance() {
        return this.f;
    }

    public int getDuration() {
        return this.g;
    }

    public RouteNode getStarting() {
        return this.b;
    }

    public RouteNode getTerminal() {
        return this.c;
    }

    public String getTitle() {
        return this.d;
    }

    /* access modifiers changed from: protected */
    public TYPE getType() {
        return this.a;
    }

    public void setDistance(int i) {
        this.f = i;
    }

    public void setDuration(int i) {
        this.g = i;
    }

    public void setStarting(RouteNode routeNode) {
        this.b = routeNode;
    }

    public void setSteps(List<T> list) {
        this.e = list;
    }

    public void setTerminal(RouteNode routeNode) {
        this.c = routeNode;
    }

    public void setTitle(String str) {
        this.d = str;
    }

    /* access modifiers changed from: protected */
    public void setType(TYPE type) {
        this.a = type;
    }

    public void writeToParcel(Parcel parcel, int i) {
        TYPE type = this.a;
        parcel.writeInt(type != null ? type.a() : 10);
        parcel.writeValue(this.b);
        parcel.writeValue(this.c);
        parcel.writeString(this.d);
        if (this.a != null) {
            parcel.writeTypedList(this.e);
        }
        parcel.writeInt(this.f);
        parcel.writeInt(this.g);
    }
}
