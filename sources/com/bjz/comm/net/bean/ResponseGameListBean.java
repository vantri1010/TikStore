package com.bjz.comm.net.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseGameListBean implements Serializable {
    private int code;
    private ArrayList<MiniGameBean> data;
    private String msg;
    private String requestId;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code2) {
        this.code = code2;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg2) {
        this.msg = msg2;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId2) {
        this.requestId = requestId2;
    }

    public ArrayList<MiniGameBean> getData() {
        return this.data;
    }

    public void setData(ArrayList<MiniGameBean> data2) {
        this.data = data2;
    }
}
