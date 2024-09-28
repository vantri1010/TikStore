package com.bjz.comm.net.bean;

public class HuanHuiUploadFileResponseBean {
    public String Access_Hash;
    public int code;
    public String desc;
    public String furl;

    public boolean isSuccess() {
        return this.code == 0;
    }
}
