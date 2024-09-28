package com.bjz.comm.net.bean;

import java.io.Serializable;

public class ResponseFcAttentionFriendsBean implements Serializable {
    private long FocusDatetime;
    private long FocusID;
    private long UserID;

    public long getUserID() {
        return this.UserID;
    }

    public void setUserID(long userID) {
        this.UserID = userID;
    }

    public long getFocusID() {
        return this.FocusID;
    }

    public void setFocusID(long focusID) {
        this.FocusID = focusID;
    }

    public long getFocusDatetime() {
        return this.FocusDatetime;
    }

    public void setFocusDatetime(long focusDatetime) {
        this.FocusDatetime = focusDatetime;
    }
}
