package com.bjz.comm.net.bean;

import java.io.Serializable;

public class RespFcIgnoreBean implements Serializable {
    private boolean LookMe;
    private boolean LookOther;

    public boolean isLookMe() {
        return this.LookMe;
    }

    public void setLookMe(boolean lookMe) {
        this.LookMe = lookMe;
    }

    public boolean isLookOther() {
        return this.LookOther;
    }

    public void setLookOther(boolean lookOther) {
        this.LookOther = lookOther;
    }
}
