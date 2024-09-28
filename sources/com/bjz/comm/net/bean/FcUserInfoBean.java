package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class FcUserInfoBean implements Parcelable, Serializable {
    public static final Parcelable.Creator<FcUserInfoBean> CREATOR = new Parcelable.Creator<FcUserInfoBean>() {
        public FcUserInfoBean createFromParcel(Parcel in) {
            return new FcUserInfoBean(in);
        }

        public FcUserInfoBean[] newArray(int size) {
            return new FcUserInfoBean[size];
        }
    };
    private static final long serialVersionUID = -5042866721105262380L;
    @SerializedName("access_hash")
    private long accessHash;
    private int birthday;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private AvatarPhotoBean photo;
    private int sex;
    @SerializedName("user_id")
    private int userId;
    private String username;

    public FcUserInfoBean() {
    }

    protected FcUserInfoBean(Parcel in) {
        this.userId = in.readInt();
        this.accessHash = in.readLong();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.sex = in.readInt();
        this.username = in.readString();
        this.birthday = in.readInt();
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId2) {
        this.userId = userId2;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    public void setAccessHash(long accessHash2) {
        this.accessHash = accessHash2;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName2) {
        this.firstName = firstName2;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName2) {
        this.lastName = lastName2;
    }

    public int getSex() {
        return this.sex;
    }

    public void setSex(int sex2) {
        this.sex = sex2;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username2) {
        this.username = username2;
    }

    public int getBirthday() {
        return this.birthday;
    }

    public void setBirthday(int birthday2) {
        this.birthday = birthday2;
    }

    public AvatarPhotoBean getPhoto() {
        return this.photo;
    }

    public void setPhoto(AvatarPhotoBean photo2) {
        this.photo = photo2;
    }

    public String toString() {
        return "FcUserInfoBean{userId=" + this.userId + ", accessHash=" + this.accessHash + ", firstName='" + this.firstName + '\'' + ", lastName='" + this.lastName + '\'' + ", sex=" + this.sex + ", username='" + this.username + '\'' + ", birthday=" + this.birthday + ", photo=" + this.photo + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeLong(this.accessHash);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeInt(this.sex);
        dest.writeString(this.username);
        dest.writeInt(this.birthday);
    }
}
