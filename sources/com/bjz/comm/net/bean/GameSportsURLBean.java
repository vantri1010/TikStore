package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class GameSportsURLBean implements Parcelable {
    public static final Parcelable.Creator<GameSportsURLBean> CREATOR = new Parcelable.Creator<GameSportsURLBean>() {
        public GameSportsURLBean createFromParcel(Parcel in) {
            return new GameSportsURLBean(in);
        }

        public GameSportsURLBean[] newArray(int size) {
            return new GameSportsURLBean[size];
        }
    };
    private GameSportsLoginUrlBean data;
    private String msg;
    private int status;

    public GameSportsURLBean() {
    }

    protected GameSportsURLBean(Parcel in) {
        this.status = in.readInt();
        this.msg = in.readString();
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status2) {
        this.status = status2;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg2) {
        this.msg = msg2;
    }

    public GameSportsLoginUrlBean getData() {
        return this.data;
    }

    public void setData(GameSportsLoginUrlBean data2) {
        this.data = data2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.msg);
    }
}
