package com.bjz.comm.net.bean;

import java.io.Serializable;

public class FcBgBean implements Serializable {
    private String HomeBackground;
    private int UserID;

    public int getUserID() {
        return this.UserID;
    }

    public void setUserID(int userID) {
        this.UserID = userID;
    }

    public String getHomeBackground() {
        return this.HomeBackground;
    }

    public void setHomeBackground(String homeBackground) {
        this.HomeBackground = homeBackground;
    }
}
