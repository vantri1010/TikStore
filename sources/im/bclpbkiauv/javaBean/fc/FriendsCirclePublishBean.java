package im.bclpbkiauv.javaBean.fc;

import android.os.Parcel;
import android.os.Parcelable;
import com.bjz.comm.net.bean.FCEntitysRequest;
import com.bjz.comm.net.bean.FcMediaBean;
import com.bjz.comm.net.bean.TopicBean;
import java.io.Serializable;
import java.util.ArrayList;

public class FriendsCirclePublishBean implements Serializable, Parcelable {
    public static final Parcelable.Creator<FriendsCirclePublishBean> CREATOR = new Parcelable.Creator<FriendsCirclePublishBean>() {
        public FriendsCirclePublishBean createFromParcel(Parcel in) {
            return new FriendsCirclePublishBean(in);
        }

        public FriendsCirclePublishBean[] newArray(int size) {
            return new FriendsCirclePublishBean[size];
        }
    };
    private static final long serialVersionUID = 3097934312329920502L;
    private String Content;
    private int ContentType;
    private ArrayList<FCEntitysRequest> Entitys = new ArrayList<>();
    private double Latitude;
    private String LocationAddress;
    private String LocationCity;
    private String LocationName;
    private double Longitude;
    private ArrayList<FcMediaBean> Medias = new ArrayList<>();
    private int Permission;
    private String Tips;
    private ArrayList<TopicBean> Topic;
    private long UserID;

    public FriendsCirclePublishBean() {
    }

    protected FriendsCirclePublishBean(Parcel in) {
        this.ContentType = in.readInt();
        this.UserID = in.readLong();
        this.Content = in.readString();
        this.Permission = in.readInt();
        this.Longitude = in.readDouble();
        this.Latitude = in.readDouble();
        this.LocationName = in.readString();
        this.LocationAddress = in.readString();
        this.LocationCity = in.readString();
        this.Tips = in.readString();
        this.Topic = in.createTypedArrayList(TopicBean.CREATOR);
        this.Medias = in.createTypedArrayList(FcMediaBean.CREATOR);
    }

    public int getContentType() {
        return this.ContentType;
    }

    public void setContentType(int ContentType2) {
        this.ContentType = ContentType2;
    }

    public long getUserID() {
        return this.UserID;
    }

    public void setUserID(long UserID2) {
        this.UserID = UserID2;
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String Content2) {
        this.Content = Content2;
    }

    public int getPermission() {
        return this.Permission;
    }

    public void setPermission(int Right) {
        this.Permission = Right;
    }

    public double getLongitude() {
        return this.Longitude;
    }

    public void setLongitude(double Longitude2) {
        this.Longitude = Longitude2;
    }

    public double getLatitude() {
        return this.Latitude;
    }

    public void setLatitude(double Latitude2) {
        this.Latitude = Latitude2;
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

    public String getTips() {
        return this.Tips;
    }

    public void setTips(String Tips2) {
        this.Tips = Tips2;
    }

    public ArrayList<FcMediaBean> getMedias() {
        return this.Medias;
    }

    public void setMedias(ArrayList<FcMediaBean> medias) {
        this.Medias = medias;
    }

    public ArrayList<FCEntitysRequest> getEntitys() {
        return this.Entitys;
    }

    public void setEntitys(ArrayList<FCEntitysRequest> entitys) {
        this.Entitys = entitys;
    }

    public ArrayList<TopicBean> getTopics() {
        return this.Topic;
    }

    public void setTopics(ArrayList<TopicBean> topics) {
        this.Topic = topics;
    }

    public String toString() {
        return "FriendsCirclePublishBean{ContentType=" + this.ContentType + ", UserID=" + this.UserID + ", Content='" + this.Content + '\'' + ", Permission=" + this.Permission + ", Longitude=" + this.Longitude + ", Latitude=" + this.Latitude + ", LocationName='" + this.LocationName + '\'' + ", LocationAddress='" + this.LocationAddress + '\'' + ", LocationCity='" + this.LocationCity + '\'' + ", Tips='" + this.Tips + '\'' + ", Medias=" + this.Medias + ", Entitys=" + this.Entitys + ", Topics=" + this.Topic + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ContentType);
        dest.writeLong(this.UserID);
        dest.writeString(this.Content);
        dest.writeInt(this.Permission);
        dest.writeDouble(this.Longitude);
        dest.writeDouble(this.Latitude);
        dest.writeString(this.LocationName);
        dest.writeString(this.LocationAddress);
        dest.writeString(this.LocationCity);
        dest.writeString(this.Tips);
        dest.writeTypedList(this.Topic);
        dest.writeTypedList(this.Medias);
    }
}
