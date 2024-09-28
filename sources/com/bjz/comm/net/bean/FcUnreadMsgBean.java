package com.bjz.comm.net.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class FcUnreadMsgBean implements Serializable {
    private int Count;
    private ArrayList<Integer> Sender;

    public ArrayList<Integer> getSender() {
        return this.Sender;
    }

    public void setSender(ArrayList<Integer> sender) {
        this.Sender = sender;
    }

    public int getCount() {
        return this.Count;
    }

    public void setCount(int count) {
        this.Count = count;
    }
}
