package com.bjz.comm.net.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class UnReadMsgBean implements Serializable {
    private String Content;
    private int ContentType;
    private ArrayList<FCEntitysResponse> Entitys = new ArrayList<>();
    private long Owner;
    private long PostTime;
    private long Receiver;
    private long Sender;
    private String SourceContent;
    private long SourceForumID;
    private String Thum;

    public long getPostTime() {
        return this.PostTime;
    }

    public void setPostTime(long postTime) {
        this.PostTime = postTime;
    }

    public long getOwner() {
        return this.Owner;
    }

    public void setOwner(long owner) {
        this.Owner = owner;
    }

    public long getReceiver() {
        return this.Receiver;
    }

    public void setReceiver(long receiver) {
        this.Receiver = receiver;
    }

    public long getSender() {
        return this.Sender;
    }

    public void setSender(long sender) {
        this.Sender = sender;
    }

    public int getContentType() {
        return this.ContentType;
    }

    public void setContentType(int contentType) {
        this.ContentType = contentType;
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public String getSourceContent() {
        return this.SourceContent;
    }

    public void setSourceContent(String sourceContent) {
        this.SourceContent = sourceContent;
    }

    public String getThum() {
        return this.Thum;
    }

    public void setThum(String thum) {
        this.Thum = thum;
    }

    public long getSourceForumID() {
        return this.SourceForumID;
    }

    public void setSourceForumID(long sourceForumID) {
        this.SourceForumID = sourceForumID;
    }

    public ArrayList<FCEntitysResponse> getEntitys() {
        return this.Entitys;
    }

    public void setEntitys(ArrayList<FCEntitysResponse> entitys) {
        this.Entitys = entitys;
    }

    public String toString() {
        return "UnReadMsgBean{Sender=" + this.Sender + ", Owner=" + this.Owner + ", Receiver=" + this.Receiver + ", ContentType=" + this.ContentType + ", PostTime=" + this.PostTime + ", Content='" + this.Content + '\'' + ", SourceContent='" + this.SourceContent + '\'' + ", Thum='" + this.Thum + '\'' + ", SourceForumID=" + this.SourceForumID + ", Entitys=" + this.Entitys + '}';
    }
}
