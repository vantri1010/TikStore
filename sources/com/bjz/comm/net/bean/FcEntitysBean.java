package com.bjz.comm.net.bean;

import java.io.Serializable;

public class FcEntitysBean implements Serializable {
    private long accessHash;
    private String nickName;
    private int offsetEnd;
    private int offsetStart;
    private String showName;
    private int userID;
    private String userName;

    public FcEntitysBean(int userID2, String nickName2, String userName2, String showName2, long accessHash2, int offsetStart2, int offsetEnd2) {
        this.userID = userID2;
        this.nickName = nickName2;
        this.userName = userName2;
        this.showName = showName2;
        this.accessHash = accessHash2;
        this.offsetStart = offsetStart2;
        this.offsetEnd = offsetEnd2;
    }

    public int getUserID() {
        return this.userID;
    }

    public void setUserID(int userID2) {
        this.userID = userID2;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName2) {
        this.nickName = nickName2;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName2) {
        this.userName = userName2;
    }

    public String getShowName() {
        return this.showName;
    }

    public void setShowName(String showName2) {
        this.showName = showName2;
    }

    public long getAccessHash() {
        return this.accessHash;
    }

    public void setAccessHash(long accessHash2) {
        this.accessHash = accessHash2;
    }

    public int getOffsetStart() {
        return this.offsetStart;
    }

    public void setOffsetStart(int offsetStart2) {
        this.offsetStart = offsetStart2;
    }

    public int getOffsetEnd() {
        return this.offsetEnd;
    }

    public void setOffsetEnd(int offsetEnd2) {
        this.offsetEnd = offsetEnd2;
    }
}
