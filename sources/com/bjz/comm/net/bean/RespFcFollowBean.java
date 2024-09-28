package com.bjz.comm.net.bean;

import java.io.Serializable;

public class RespFcFollowBean implements Serializable {
    private boolean HasFollow;

    public boolean isHasFollow() {
        return this.HasFollow;
    }

    public void setHasFollow(boolean hasFollow) {
        this.HasFollow = hasFollow;
    }
}
