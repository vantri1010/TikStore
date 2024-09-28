package com.bjz.comm.net.bean;

public class RespTopicTypeBean {
    public int ID;
    public int Status;
    public long TopicTypeID;
    public String TopicTypeName;

    public int getID() {
        return this.ID;
    }

    public void setID(int ID2) {
        this.ID = ID2;
    }

    public long getTopicTypeID() {
        return this.TopicTypeID;
    }

    public void setTopicTypeID(long topicTypeID) {
        this.TopicTypeID = topicTypeID;
    }

    public String getTopicTypeName() {
        return this.TopicTypeName;
    }

    public void setTopicTypeName(String topicTypeName) {
        this.TopicTypeName = topicTypeName;
    }

    public int getStatus() {
        return this.Status;
    }

    public void setStatus(int status) {
        this.Status = status;
    }

    public String toString() {
        return "RespTopicTypeBean{ID=" + this.ID + ", TopicTypeID=" + this.TopicTypeID + ", TopicTypeName='" + this.TopicTypeName + '\'' + ", Status=" + this.Status + '}';
    }
}
