package com.bjz.comm.net.bean;

import java.util.ArrayList;

public class RespFcReplyBean {
    private ArrayList<FcReplyBean> Comments;

    public ArrayList<FcReplyBean> getComments() {
        return this.Comments;
    }

    public void setComments(ArrayList<FcReplyBean> comments) {
        this.Comments = comments;
    }

    public String toString() {
        return "RespFcReplyBean{Comments=" + this.Comments + '}';
    }
}
