package im.bclpbkiauv.javaBean.fc;

import java.io.Serializable;

public class FcLocationInfoBean implements Serializable {
    private double Latitude;
    private String LocationAddress;
    private String LocationCity;
    private String LocationName;
    private double Longitude;

    public FcLocationInfoBean(double longitude, double latitude, String locationName, String locationAddress, String locationCity) {
        this.Longitude = longitude;
        this.Latitude = latitude;
        this.LocationName = locationName;
        this.LocationAddress = locationAddress;
        this.LocationCity = locationCity;
    }

    public double getLongitude() {
        return this.Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    public double getLatitude() {
        return this.Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public String getLocationName() {
        return this.LocationName;
    }

    public void setLocationName(String locationName) {
        this.LocationName = locationName;
    }

    public String getLocationAddress() {
        return this.LocationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.LocationAddress = locationAddress;
    }

    public String getLocationCity() {
        return this.LocationCity;
    }

    public void setLocationCity(String locationCity) {
        this.LocationCity = locationCity;
    }

    public String toString() {
        return "FcLocationInfoBean{Longitude=" + this.Longitude + ", Latitude=" + this.Latitude + ", LocationName='" + this.LocationName + '\'' + ", LocationAddress='" + this.LocationAddress + '\'' + ", LocationCity='" + this.LocationCity + '\'' + '}';
    }
}
