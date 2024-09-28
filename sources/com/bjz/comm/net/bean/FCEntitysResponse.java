package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class FCEntitysResponse implements Parcelable, Serializable {
    public static final Parcelable.Creator<FCEntitysResponse> CREATOR = new Parcelable.Creator<FCEntitysResponse>() {
        public FCEntitysResponse createFromParcel(Parcel in) {
            return new FCEntitysResponse(in);
        }

        public FCEntitysResponse[] newArray(int size) {
            return new FCEntitysResponse[size];
        }
    };
    private static final long serialVersionUID = -1514867824432041313L;
    private long AccessHash;
    private int Limit;
    private int Offset;
    private int Type;
    private int ULimit;
    private int UOffset;
    private int UserID;
    private String UserName;
    private int id;

    public FCEntitysResponse(String userName, int userID, long accessHash) {
        this.UserName = userName;
        this.UserID = userID;
        this.AccessHash = accessHash;
    }

    protected FCEntitysResponse(Parcel in) {
        this.id = in.readInt();
        this.UserID = in.readInt();
        this.UserName = in.readString();
        this.AccessHash = in.readLong();
        this.Type = in.readInt();
        this.Offset = in.readInt();
        this.Limit = in.readInt();
        this.UOffset = in.readInt();
        this.ULimit = in.readInt();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public int getUserID() {
        return this.UserID;
    }

    public void setUserID(int userID) {
        this.UserID = userID;
    }

    public String getUserName() {
        return this.UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public long getAccessHash() {
        return this.AccessHash;
    }

    public void setAccessHash(long accessHash) {
        this.AccessHash = accessHash;
    }

    public int getType() {
        return this.Type;
    }

    public void setType(int type) {
        this.Type = type;
    }

    public int getOffset() {
        return this.Offset;
    }

    public void setOffset(int offset) {
        this.Offset = offset;
    }

    public int getLimit() {
        return this.Limit;
    }

    public void setLimit(int limit) {
        this.Limit = limit;
    }

    public int getUOffset() {
        return this.UOffset;
    }

    public void setUOffset(int UOffset2) {
        this.UOffset = UOffset2;
    }

    public int getULimit() {
        return this.ULimit;
    }

    public void setULimit(int ULimit2) {
        this.ULimit = ULimit2;
    }

    public String toString() {
        return "FCEntitysResponse{id=" + this.id + ", UserID=" + this.UserID + ", UserName='" + this.UserName + '\'' + ", AccessHash=" + this.AccessHash + ", Type=" + this.Type + ", Offset=" + this.Offset + ", Limit=" + this.Limit + ", UOffset=" + this.UOffset + ", ULimit=" + this.ULimit + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.UserID);
        dest.writeString(this.UserName);
        dest.writeLong(this.AccessHash);
        dest.writeInt(this.Type);
        dest.writeInt(this.Offset);
        dest.writeInt(this.Limit);
        dest.writeInt(this.UOffset);
        dest.writeInt(this.ULimit);
    }
}
