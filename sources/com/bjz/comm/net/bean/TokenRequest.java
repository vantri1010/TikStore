package com.bjz.comm.net.bean;

public class TokenRequest {
    private String Token;
    private int flag;

    public String getToken() {
        return this.Token;
    }

    public void setToken(String token) {
        this.Token = token;
    }

    public void setFlag(int flag2) {
        this.flag = flag2;
    }

    public int getFlag() {
        return this.flag;
    }
}
