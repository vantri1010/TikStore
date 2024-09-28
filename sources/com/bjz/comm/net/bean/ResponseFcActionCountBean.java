package com.bjz.comm.net.bean;

import java.io.Serializable;

public class ResponseFcActionCountBean implements Serializable {
    private int FirendLikeCount;
    private int UserFans;
    private int UserFocus;
    private int UserPostforumCount;

    public int getUserPostforumCount() {
        return this.UserPostforumCount;
    }

    public void setUserPostforumCount(int userPostforumCount) {
        this.UserPostforumCount = userPostforumCount;
    }

    public int getFirendLikeCount() {
        return this.FirendLikeCount;
    }

    public void setFirendLikeCount(int firendLikeCount) {
        this.FirendLikeCount = firendLikeCount;
    }

    public int getUserFans() {
        return this.UserFans;
    }

    public void setUserFans(int userFans) {
        this.UserFans = userFans;
    }

    public int getUserFocus() {
        return this.UserFocus;
    }

    public void setUserFocus(int userFocus) {
        this.UserFocus = userFocus;
    }
}
