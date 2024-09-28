package com.bjz.comm.net.bean;

import java.io.Serializable;

public class FCEntitysRequest implements Serializable {
    private long AccessHash;
    private int UserID;
    private String UserName;
    private String realName;

    public FCEntitysRequest(String userName, int userID, long accessHash) {
        this.UserName = userName;
        this.UserID = userID;
        this.AccessHash = accessHash;
    }

    public FCEntitysRequest(String userName, String realName2, int userID, long accessHash) {
        this.UserName = userName;
        this.realName = realName2;
        this.UserID = userID;
        this.AccessHash = accessHash;
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

    public String toString() {
        return "FCEntitysResponse{, UserID=" + this.UserID + ", UserName='" + this.UserName + '\'' + ", AccessHash=" + this.AccessHash + '}';
    }
}
