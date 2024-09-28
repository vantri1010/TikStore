package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class GameSportsLoginUrlBean implements Parcelable {
    public static final Parcelable.Creator<GameSportsLoginUrlBean> CREATOR = new Parcelable.Creator<GameSportsLoginUrlBean>() {
        public GameSportsLoginUrlBean createFromParcel(Parcel in) {
            return new GameSportsLoginUrlBean(in);
        }

        public GameSportsLoginUrlBean[] newArray(int size) {
            return new GameSportsLoginUrlBean[size];
        }
    };
    private String loginurl;

    public GameSportsLoginUrlBean() {
    }

    protected GameSportsLoginUrlBean(Parcel in) {
        this.loginurl = in.readString();
    }

    public String getLoginurl() {
        return this.loginurl;
    }

    public void setLoginurl(String loginurl2) {
        this.loginurl = loginurl2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.loginurl);
    }
}
