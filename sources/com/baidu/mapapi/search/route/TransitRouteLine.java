package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.TaxiInfo;
import com.baidu.mapapi.search.core.VehicleInfo;
import java.util.List;

public final class TransitRouteLine extends RouteLine<TransitStep> implements Parcelable {
    public static final Parcelable.Creator<TransitRouteLine> CREATOR = new o();
    private TaxiInfo b;

    public static class TransitStep extends RouteStep implements Parcelable {
        public static final Parcelable.Creator<TransitStep> CREATOR = new p();
        private VehicleInfo d;
        private RouteNode e;
        private RouteNode f;
        private TransitRouteStepType g;
        private String h;
        private String i;

        public enum TransitRouteStepType {
            BUSLINE,
            SUBWAY,
            WAKLING
        }

        public TransitStep() {
        }

        protected TransitStep(Parcel parcel) {
            super(parcel);
            this.d = (VehicleInfo) parcel.readParcelable(VehicleInfo.class.getClassLoader());
            this.e = (RouteNode) parcel.readParcelable(RouteNode.class.getClassLoader());
            this.f = (RouteNode) parcel.readParcelable(RouteNode.class.getClassLoader());
            int readInt = parcel.readInt();
            this.g = readInt == -1 ? null : TransitRouteStepType.values()[readInt];
            this.h = parcel.readString();
            this.i = parcel.readString();
        }

        public int describeContents() {
            return 0;
        }

        public RouteNode getEntrance() {
            return this.e;
        }

        public RouteNode getExit() {
            return this.f;
        }

        public String getInstructions() {
            return this.h;
        }

        public TransitRouteStepType getStepType() {
            return this.g;
        }

        public VehicleInfo getVehicleInfo() {
            return this.d;
        }

        public List<LatLng> getWayPoints() {
            if (this.mWayPoints == null) {
                this.mWayPoints = CoordUtil.decodeLocationList(this.i);
            }
            return this.mWayPoints;
        }

        public void setEntrace(RouteNode routeNode) {
            this.e = routeNode;
        }

        public void setExit(RouteNode routeNode) {
            this.f = routeNode;
        }

        public void setInstructions(String str) {
            this.h = str;
        }

        public void setPathString(String str) {
            this.i = str;
        }

        public void setStepType(TransitRouteStepType transitRouteStepType) {
            this.g = transitRouteStepType;
        }

        public void setVehicleInfo(VehicleInfo vehicleInfo) {
            this.d = vehicleInfo;
        }

        public void writeToParcel(Parcel parcel, int i2) {
            super.writeToParcel(parcel, i2);
            parcel.writeParcelable(this.d, 1);
            parcel.writeParcelable(this.e, 1);
            parcel.writeParcelable(this.f, 1);
            TransitRouteStepType transitRouteStepType = this.g;
            parcel.writeInt(transitRouteStepType == null ? -1 : transitRouteStepType.ordinal());
            parcel.writeString(this.h);
            parcel.writeString(this.i);
        }
    }

    public TransitRouteLine() {
    }

    protected TransitRouteLine(Parcel parcel) {
        super(parcel);
        this.b = (TaxiInfo) parcel.readParcelable(TaxiInfo.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    @Deprecated
    public TaxiInfo getTaxitInfo() {
        return this.b;
    }

    public void setTaxitInfo(TaxiInfo taxiInfo) {
        this.b = taxiInfo;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.setType(RouteLine.TYPE.TRANSITSTEP);
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(this.b, 1);
    }
}
