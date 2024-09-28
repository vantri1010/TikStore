package com.bjz.comm.net.bean;

import java.io.Serializable;

public class FcRedPointBean implements Serializable {
    private long ForumID;
    private int Sender;

    public int getSender() {
        return this.Sender;
    }

    public void setSender(int sender) {
        this.Sender = sender;
    }

    public long getForumID() {
        return this.ForumID;
    }

    public void setForumID(long forumID) {
        this.ForumID = forumID;
    }

    public String toString() {
        return "FcRedPointBean{Sender=" + this.Sender + ", ForumID=" + this.ForumID + '}';
    }
}
