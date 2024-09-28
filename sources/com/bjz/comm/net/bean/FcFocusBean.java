package com.bjz.comm.net.bean;

import java.io.Serializable;

public class FcFocusBean implements Serializable {
    private int Enable;
    private String FocusDatetime;
    private String FocusID;
    private String ID;
    private String UserID;

    public String getID() {
        return this.ID;
    }

    public void setID(String ID2) {
        this.ID = ID2;
    }

    public String getUserID() {
        return this.UserID;
    }

    public void setUserID(String userID) {
        this.UserID = userID;
    }

    public String getFocusID() {
        return this.FocusID;
    }

    public void setFocusID(String focusID) {
        this.FocusID = focusID;
    }

    public String getFocusDatetime() {
        return this.FocusDatetime;
    }

    public void setFocusDatetime(String focusDatetime) {
        this.FocusDatetime = focusDatetime;
    }

    public int getEnable() {
        return this.Enable;
    }

    public void setEnable(int enable) {
        this.Enable = enable;
    }
}
