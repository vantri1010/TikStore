package com.bjz.comm.net.bean;

public class BResponseBool<T> {
    public String Code;
    public T Data;
    public String Message;
    private boolean State;

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

    public void setState(boolean state) {
        this.State = state;
    }

    public boolean isState() {
        return this.State;
    }
}
