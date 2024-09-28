package com.bjz.comm.net.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestReplyFcBean implements Serializable {
    private long CommentID;
    private String Content;
    private ArrayList<FCEntitysRequest> Entitys;
    private long ForumID;
    private long ForumUser;
    private int LimitVIP;
    private long ReplayID;
    private long ReplayUID;
    private long SupID;
    private long SupUser;

    public RequestReplyFcBean() {
    }

    public RequestReplyFcBean(long forumID, long forumUser, long replayID, long replayUID, long supID, long supUser, String content, int limitVIP) {
        this.ForumID = forumID;
        this.ForumUser = forumUser;
        this.SupID = supID;
        this.SupUser = supUser;
        this.ReplayID = replayID;
        this.ReplayUID = replayUID;
        this.Content = content;
        this.LimitVIP = limitVIP;
    }

    public long getForumID() {
        return this.ForumID;
    }

    public void setForumID(long forumID) {
        this.ForumID = forumID;
    }

    public long getForumUser() {
        return this.ForumUser;
    }

    public void setForumUser(long forumUser) {
        this.ForumUser = forumUser;
    }

    public long getSupID() {
        return this.SupID;
    }

    public void setSupID(long supID) {
        this.SupID = supID;
    }

    public long getSupUser() {
        return this.SupUser;
    }

    public void setSupUser(long supUser) {
        this.SupUser = supUser;
    }

    public long getReplayID() {
        return this.ReplayID;
    }

    public void setReplayID(long replayID) {
        this.ReplayID = replayID;
    }

    public long getReplayUID() {
        return this.ReplayUID;
    }

    public void setReplayUID(long replayUID) {
        this.ReplayUID = replayUID;
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public long getCommentID() {
        return this.CommentID;
    }

    public void setCommentID(long commentID) {
        this.CommentID = commentID;
    }

    public int getLimitVIP() {
        return this.LimitVIP;
    }

    public void setLimitVIP(int limitVIP) {
        this.LimitVIP = limitVIP;
    }

    public ArrayList<FCEntitysRequest> getEntitys() {
        return this.Entitys;
    }

    public void setEntitys(ArrayList<FCEntitysRequest> entitys) {
        this.Entitys = entitys;
    }

    public String toString() {
        return "RequestReplyFcBean{ForumID=" + this.ForumID + ", ForumUser=" + this.ForumUser + ", SupID=" + this.SupID + ", SupUser=" + this.SupUser + ", ReplayID=" + this.ReplayID + ", ReplayUID=" + this.ReplayUID + ", CommentID=" + this.CommentID + ", Content='" + this.Content + '\'' + ", LimitVIP=" + this.LimitVIP + ", Entitys=" + this.Entitys + '}';
    }
}
