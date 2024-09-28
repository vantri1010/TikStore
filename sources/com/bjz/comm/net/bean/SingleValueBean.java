package com.bjz.comm.net.bean;

import java.io.Serializable;

public class SingleValueBean implements Serializable {
    private Object Value;

    public Object getValue() {
        return this.Value;
    }

    public void setValue(String value) {
        this.Value = value;
    }
}
