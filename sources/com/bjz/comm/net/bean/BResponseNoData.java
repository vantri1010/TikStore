package com.bjz.comm.net.bean;

public class BResponseNoData {
    public String Code;
    public String Message;
    private int State;

    public boolean isState() {
        return this.State == 200;
    }

    public int getState() {
        return this.State;
    }

    public String getCode() {
        return this.Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    public String getMessage() {
        return this.Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public void setState(int state) {
        this.State = state;
    }
}
