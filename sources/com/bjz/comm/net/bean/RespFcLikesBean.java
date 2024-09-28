package com.bjz.comm.net.bean;

import java.util.ArrayList;

public class RespFcLikesBean {
    private ArrayList<FcLikeBean> Thumbs;
    private ArrayList<FcUserInfoBean> UserInfo;

    public ArrayList<FcLikeBean> getThumbs() {
        return this.Thumbs;
    }

    public void setThumbs(ArrayList<FcLikeBean> thumbs) {
        this.Thumbs = thumbs;
    }

    public ArrayList<FcUserInfoBean> getUserInfo() {
        return this.UserInfo;
    }

    public void setUserInfo(ArrayList<FcUserInfoBean> userInfo) {
        this.UserInfo = userInfo;
    }
}
