package com.baidu.mapapi.search.route;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.BusInfo;
import com.baidu.mapapi.search.core.CoachInfo;
import com.baidu.mapapi.search.core.PlaneInfo;
import com.baidu.mapapi.search.core.PriceInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.TrainInfo;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import java.util.ArrayList;
import java.util.List;

public final class MassTransitRouteLine extends RouteLine<TransitStep> implements Parcelable {
    public static final Parcelable.Creator<MassTransitRouteLine> CREATOR = new i();
    private String b;
    private double c;
    private List<PriceInfo> d;
    private List<List<TransitStep>> e = null;

    public static class TransitStep extends RouteStep implements Parcelable {
        public static final Parcelable.Creator<TransitStep> CREATOR = new j();
        private List<TrafficCondition> d;
        private LatLng e;
        private LatLng f;
        private TrainInfo g;
        private PlaneInfo h;
        private CoachInfo i;
        private BusInfo j;
        private StepVehicleInfoType k;
        private String l;
        private String m;

        public enum StepVehicleInfoType {
            ESTEP_TRAIN(1),
            ESTEP_PLANE(2),
            ESTEP_BUS(3),
            ESTEP_DRIVING(4),
            ESTEP_WALK(5),
            ESTEP_COACH(6);
            
            private int a;

            private StepVehicleInfoType(int i) {
                this.a = 0;
                this.a = i;
            }

            public int getInt() {
                return this.a;
            }
        }

        public static class TrafficCondition implements Parcelable {
            public static final Parcelable.Creator<TrafficCondition> CREATOR = new k();
            private int a;
            private int b;

            public TrafficCondition() {
            }

            protected TrafficCondition(Parcel parcel) {
                this.a = parcel.readInt();
                this.b = parcel.readInt();
            }

            public int describeContents() {
                return 0;
            }

            public int getTrafficGeoCnt() {
                return this.b;
            }

            public int getTrafficStatus() {
                return this.a;
            }

            public void setTrafficGeoCnt(int i) {
                this.b = i;
            }

            public void setTrafficStatus(int i) {
                this.a = i;
            }

            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(this.a);
                parcel.writeInt(this.b);
            }
        }

        public TransitStep() {
        }

        protected TransitStep(Parcel parcel) {
            super(parcel);
            StepVehicleInfoType stepVehicleInfoType;
            this.d = parcel.createTypedArrayList(TrafficCondition.CREATOR);
            this.e = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
            this.f = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
            this.g = (TrainInfo) parcel.readParcelable(TrainInfo.class.getClassLoader());
            this.h = (PlaneInfo) parcel.readParcelable(PlaneInfo.class.getClassLoader());
            this.i = (CoachInfo) parcel.readParcelable(CoachInfo.class.getClassLoader());
            this.j = (BusInfo) parcel.readParcelable(BusInfo.class.getClassLoader());
            switch (parcel.readInt()) {
                case 1:
                    stepVehicleInfoType = StepVehicleInfoType.ESTEP_TRAIN;
                    break;
                case 2:
                    stepVehicleInfoType = StepVehicleInfoType.ESTEP_PLANE;
                    break;
                case 3:
                    stepVehicleInfoType = StepVehicleInfoType.ESTEP_BUS;
                    break;
                case 4:
                    stepVehicleInfoType = StepVehicleInfoType.ESTEP_DRIVING;
                    break;
                case 5:
                    stepVehicleInfoType = StepVehicleInfoType.ESTEP_WALK;
                    break;
                case 6:
                    stepVehicleInfoType = StepVehicleInfoType.ESTEP_COACH;
                    break;
                default:
                    this.l = parcel.readString();
                    this.m = parcel.readString();
            }
            this.k = stepVehicleInfoType;
            this.l = parcel.readString();
            this.m = parcel.readString();
        }

        private List<LatLng> a(String str) {
            String[] split;
            ArrayList arrayList = new ArrayList();
            String[] split2 = str.split(";");
            if (split2 != null) {
                for (int i2 = 0; i2 < split2.length; i2++) {
                    if (!(split2[i2] == null || split2[i2] == "" || (split = split2[i2].split(",")) == null || split[1] == "" || split[0] == "")) {
                        LatLng latLng = new LatLng(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
                        if (SDKInitializer.getCoordType() == CoordType.GCJ02) {
                            latLng = CoordTrans.baiduToGcj(latLng);
                        }
                        arrayList.add(latLng);
                    }
                }
            }
            return arrayList;
        }

        public int describeContents() {
            return 0;
        }

        public BusInfo getBusInfo() {
            return this.j;
        }

        public CoachInfo getCoachInfo() {
            return this.i;
        }

        public LatLng getEndLocation() {
            return this.f;
        }

        public String getInstructions() {
            return this.l;
        }

        public PlaneInfo getPlaneInfo() {
            return this.h;
        }

        public LatLng getStartLocation() {
            return this.e;
        }

        public List<TrafficCondition> getTrafficConditions() {
            return this.d;
        }

        public TrainInfo getTrainInfo() {
            return this.g;
        }

        public StepVehicleInfoType getVehileType() {
            return this.k;
        }

        public List<LatLng> getWayPoints() {
            if (this.mWayPoints == null) {
                this.mWayPoints = a(this.m);
            }
            return this.mWayPoints;
        }

        public void setBusInfo(BusInfo busInfo) {
            this.j = busInfo;
        }

        public void setCoachInfo(CoachInfo coachInfo) {
            this.i = coachInfo;
        }

        public void setEndLocation(LatLng latLng) {
            this.f = latLng;
        }

        public void setInstructions(String str) {
            this.l = str;
        }

        public void setPathString(String str) {
            this.m = str;
        }

        public void setPlaneInfo(PlaneInfo planeInfo) {
            this.h = planeInfo;
        }

        public void setStartLocation(LatLng latLng) {
            this.e = latLng;
        }

        public void setTrafficConditions(List<TrafficCondition> list) {
            this.d = list;
        }

        public void setTrainInfo(TrainInfo trainInfo) {
            this.g = trainInfo;
        }

        public void setVehileType(StepVehicleInfoType stepVehicleInfoType) {
            this.k = stepVehicleInfoType;
        }

        public void writeToParcel(Parcel parcel, int i2) {
            super.writeToParcel(parcel, i2);
            parcel.writeTypedList(this.d);
            parcel.writeParcelable(this.e, i2);
            parcel.writeParcelable(this.f, i2);
            parcel.writeParcelable(this.g, i2);
            parcel.writeParcelable(this.h, i2);
            parcel.writeParcelable(this.i, i2);
            parcel.writeParcelable(this.j, i2);
            parcel.writeInt(this.k.getInt());
            parcel.writeString(this.l);
            parcel.writeString(this.m);
        }
    }

    public MassTransitRouteLine() {
    }

    protected MassTransitRouteLine(Parcel parcel) {
        super(parcel);
        int readInt = parcel.readInt();
        this.b = parcel.readString();
        this.c = parcel.readDouble();
        this.d = parcel.createTypedArrayList(PriceInfo.CREATOR);
        if (readInt > 0) {
            this.e = new ArrayList();
            for (int i = 0; i < readInt; i++) {
                this.e.add(parcel.createTypedArrayList(TransitStep.CREATOR));
            }
        }
    }

    public int describeContents() {
        return 0;
    }

    public String getArriveTime() {
        return this.b;
    }

    public List<List<TransitStep>> getNewSteps() {
        return this.e;
    }

    public double getPrice() {
        return this.c;
    }

    public List<PriceInfo> getPriceInfo() {
        return this.d;
    }

    public void setArriveTime(String str) {
        this.b = str;
    }

    public void setNewSteps(List<List<TransitStep>> list) {
        this.e = list;
    }

    public void setPrice(double d2) {
        this.c = d2;
    }

    public void setPriceInfo(List<PriceInfo> list) {
        this.d = list;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        List<List<TransitStep>> list = this.e;
        parcel.writeInt(list == null ? 0 : list.size());
        parcel.writeString(this.b);
        parcel.writeDouble(this.c);
        parcel.writeTypedList(this.d);
        for (List<TransitStep> writeTypedList : this.e) {
            parcel.writeTypedList(writeTypedList);
        }
    }
}
