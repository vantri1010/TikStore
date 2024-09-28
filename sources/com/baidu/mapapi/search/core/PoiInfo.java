package com.baidu.mapapi.search.core;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.model.LatLng;

public class PoiInfo implements Parcelable {
    public static final Parcelable.Creator<PoiInfo> CREATOR = new g();
    public String address;
    public String area;
    public String city;
    public int detail;
    public String direction;
    public int distance;
    public boolean hasCaterDetails;
    public boolean isPano;
    public LatLng location;
    public String name;
    public ParentPoiInfo parentPoiInfo;
    public String phoneNum;
    public PoiDetailInfo poiDetailInfo;
    public String postCode;
    public String province;
    public String street_id;
    public String tag;
    public POITYPE type;
    public String uid;

    public enum POITYPE {
        POINT(0),
        BUS_STATION(1),
        BUS_LINE(2),
        SUBWAY_STATION(3),
        SUBWAY_LINE(4);
        
        private int a;

        private POITYPE(int i) {
            this.a = i;
        }

        public static POITYPE fromInt(int i) {
            if (i == 0) {
                return POINT;
            }
            if (i == 1) {
                return BUS_STATION;
            }
            if (i == 2) {
                return BUS_LINE;
            }
            if (i == 3) {
                return SUBWAY_STATION;
            }
            if (i != 4) {
                return null;
            }
            return SUBWAY_LINE;
        }

        public int getInt() {
            return this.a;
        }
    }

    public static class ParentPoiInfo implements Parcelable {
        public static final Parcelable.Creator<ParentPoiInfo> CREATOR = new h();
        public String parentPoiAddress;
        public String parentPoiDirection;
        public int parentPoiDistance;
        public LatLng parentPoiLocation;
        public String parentPoiName;
        public String parentPoiTag;
        public String parentPoiUid;

        public ParentPoiInfo() {
        }

        protected ParentPoiInfo(Parcel parcel) {
            this.parentPoiName = parcel.readString();
            this.parentPoiTag = parcel.readString();
            this.parentPoiAddress = parcel.readString();
            this.parentPoiLocation = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
            this.parentPoiDirection = parcel.readString();
            this.parentPoiDistance = parcel.readInt();
            this.parentPoiUid = parcel.readString();
        }

        public int describeContents() {
            return 0;
        }

        public String getParentPoiAddress() {
            return this.parentPoiAddress;
        }

        public String getParentPoiDirection() {
            return this.parentPoiDirection;
        }

        public int getParentPoiDistance() {
            return this.parentPoiDistance;
        }

        public LatLng getParentPoiLocation() {
            return this.parentPoiLocation;
        }

        public String getParentPoiName() {
            return this.parentPoiName;
        }

        public String getParentPoiTag() {
            return this.parentPoiTag;
        }

        public String getParentPoiUid() {
            return this.parentPoiUid;
        }

        public void setParentPoiAddress(String str) {
            this.parentPoiAddress = str;
        }

        public void setParentPoiDirection(String str) {
            this.parentPoiDirection = str;
        }

        public void setParentPoiDistance(int i) {
            this.parentPoiDistance = i;
        }

        public void setParentPoiLocation(LatLng latLng) {
            this.parentPoiLocation = latLng;
        }

        public void setParentPoiName(String str) {
            this.parentPoiName = str;
        }

        public void setParentPoiTag(String str) {
            this.parentPoiTag = str;
        }

        public void setParentPoiUid(String str) {
            this.parentPoiUid = str;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.parentPoiName);
            parcel.writeString(this.parentPoiTag);
            parcel.writeString(this.parentPoiAddress);
            parcel.writeParcelable(this.parentPoiLocation, i);
            parcel.writeString(this.parentPoiDirection);
            parcel.writeInt(this.parentPoiDistance);
            parcel.writeString(this.parentPoiUid);
        }
    }

    public PoiInfo() {
    }

    protected PoiInfo(Parcel parcel) {
        this.name = parcel.readString();
        this.uid = parcel.readString();
        this.tag = parcel.readString();
        this.address = parcel.readString();
        this.province = parcel.readString();
        this.city = parcel.readString();
        this.area = parcel.readString();
        this.street_id = parcel.readString();
        this.phoneNum = parcel.readString();
        this.postCode = parcel.readString();
        this.detail = ((Integer) parcel.readValue(Integer.class.getClassLoader())).intValue();
        this.type = (POITYPE) parcel.readValue(POITYPE.class.getClassLoader());
        this.location = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
        this.hasCaterDetails = ((Boolean) parcel.readValue(Boolean.class.getClassLoader())).booleanValue();
        this.isPano = ((Boolean) parcel.readValue(Boolean.class.getClassLoader())).booleanValue();
        this.poiDetailInfo = (PoiDetailInfo) parcel.readParcelable(PoiDetailInfo.class.getClassLoader());
        this.direction = parcel.readString();
        this.distance = parcel.readInt();
        this.parentPoiInfo = (ParentPoiInfo) parcel.readParcelable(ParentPoiInfo.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public String getAddress() {
        return this.address;
    }

    public String getArea() {
        return this.area;
    }

    public String getCity() {
        return this.city;
    }

    public int getDetail() {
        return this.detail;
    }

    public String getDirection() {
        return this.direction;
    }

    public int getDistance() {
        return this.distance;
    }

    public LatLng getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }

    public ParentPoiInfo getParentPoi() {
        return this.parentPoiInfo;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }

    public PoiDetailInfo getPoiDetailInfo() {
        return this.poiDetailInfo;
    }

    public String getPostCode() {
        return this.postCode;
    }

    public String getProvince() {
        return this.province;
    }

    public String getStreetId() {
        return this.street_id;
    }

    public String getTag() {
        return this.tag;
    }

    public POITYPE getType() {
        return this.type;
    }

    public String getUid() {
        return this.uid;
    }

    public boolean isHasCaterDetails() {
        return this.hasCaterDetails;
    }

    public boolean isPano() {
        return this.isPano;
    }

    public void setAddress(String str) {
        this.address = str;
    }

    public void setArea(String str) {
        this.area = str;
    }

    public void setCity(String str) {
        this.city = str;
    }

    public void setDetail(int i) {
        this.detail = i;
    }

    public void setDirection(String str) {
        this.direction = str;
    }

    public void setDistance(int i) {
        this.distance = i;
    }

    public void setHasCaterDetails(boolean z) {
        this.hasCaterDetails = z;
    }

    public void setLocation(LatLng latLng) {
        this.location = latLng;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setPano(boolean z) {
        this.isPano = z;
    }

    public void setParentPoi(ParentPoiInfo parentPoiInfo2) {
        this.parentPoiInfo = parentPoiInfo2;
    }

    public void setPhoneNum(String str) {
        this.phoneNum = str;
    }

    public void setPoiDetailInfo(PoiDetailInfo poiDetailInfo2) {
        this.poiDetailInfo = poiDetailInfo2;
    }

    public void setPostCode(String str) {
        this.postCode = str;
    }

    public void setProvince(String str) {
        this.province = str;
    }

    public void setStreetId(String str) {
        this.street_id = str;
    }

    public void setTag(String str) {
        this.tag = str;
    }

    public void setType(POITYPE poitype) {
        this.type = poitype;
    }

    public void setUid(String str) {
        this.uid = str;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("PoiInfo: ");
        stringBuffer.append("name = ");
        stringBuffer.append(this.name);
        stringBuffer.append("; uid = ");
        stringBuffer.append(this.uid);
        stringBuffer.append("; address = ");
        stringBuffer.append(this.address);
        stringBuffer.append("; province = ");
        stringBuffer.append(this.province);
        stringBuffer.append("; city = ");
        stringBuffer.append(this.city);
        stringBuffer.append("; area = ");
        stringBuffer.append(this.area);
        stringBuffer.append("; street_id = ");
        stringBuffer.append(this.street_id);
        stringBuffer.append("; phoneNum = ");
        stringBuffer.append(this.phoneNum);
        stringBuffer.append("; postCode = ");
        stringBuffer.append(this.postCode);
        stringBuffer.append("; detail = ");
        stringBuffer.append(this.detail);
        stringBuffer.append("; location = ");
        LatLng latLng = this.location;
        if (latLng != null) {
            stringBuffer.append(latLng.toString());
        } else {
            stringBuffer.append("null");
        }
        stringBuffer.append("; hasCaterDetails = ");
        stringBuffer.append(this.hasCaterDetails);
        stringBuffer.append("; isPano = ");
        stringBuffer.append(this.isPano);
        stringBuffer.append("; tag = ");
        stringBuffer.append(this.tag);
        stringBuffer.append("; poiDetailInfo = ");
        PoiDetailInfo poiDetailInfo2 = this.poiDetailInfo;
        if (poiDetailInfo2 != null) {
            stringBuffer.append(poiDetailInfo2.toString());
        } else {
            stringBuffer.append("null");
        }
        stringBuffer.append("; direction = ");
        stringBuffer.append(this.direction);
        stringBuffer.append("; distance = ");
        stringBuffer.append(this.distance);
        if (this.parentPoiInfo != null) {
            stringBuffer.append("; parentPoiAddress = ");
            stringBuffer.append(this.parentPoiInfo.getParentPoiAddress());
            stringBuffer.append("; parentPoiDirection = ");
            stringBuffer.append(this.parentPoiInfo.getParentPoiDirection());
            stringBuffer.append("; parentPoiDistance = ");
            stringBuffer.append(this.parentPoiInfo.getParentPoiDistance());
            stringBuffer.append("; parentPoiName = ");
            stringBuffer.append(this.parentPoiInfo.getParentPoiName());
            stringBuffer.append("; parentPoiTag = ");
            stringBuffer.append(this.parentPoiInfo.getParentPoiTag());
            stringBuffer.append("; parentPoiUid = ");
            stringBuffer.append(this.parentPoiInfo.getParentPoiUid());
            stringBuffer.append("; parentPoiLocation = ");
            stringBuffer.append(this.parentPoiInfo.getParentPoiLocation());
        }
        return stringBuffer.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.uid);
        parcel.writeString(this.tag);
        parcel.writeString(this.address);
        parcel.writeString(this.province);
        parcel.writeString(this.city);
        parcel.writeString(this.area);
        parcel.writeString(this.street_id);
        parcel.writeString(this.phoneNum);
        parcel.writeString(this.postCode);
        parcel.writeValue(Integer.valueOf(this.detail));
        parcel.writeValue(this.type);
        parcel.writeParcelable(this.location, 1);
        parcel.writeValue(Boolean.valueOf(this.hasCaterDetails));
        parcel.writeValue(Boolean.valueOf(this.isPano));
        parcel.writeParcelable(this.poiDetailInfo, 1);
        parcel.writeString(this.direction);
        parcel.writeInt(this.distance);
        parcel.writeParcelable(this.parentPoiInfo, 1);
    }
}
