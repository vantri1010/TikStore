package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class TopicBean implements Parcelable, Serializable {
    public static final Parcelable.Creator<TopicBean> CREATOR = new Parcelable.Creator<TopicBean>() {
        public TopicBean createFromParcel(Parcel in) {
            return new TopicBean(in);
        }

        public TopicBean[] newArray(int size) {
            return new TopicBean[size];
        }
    };
    private static final long serialVersionUID = -5341889615601952280L;
    private int TopicID;
    private String TopicName;

    public TopicBean(String topicName, int topicId) {
        this.TopicName = topicName;
        this.TopicID = topicId;
    }

    protected TopicBean(Parcel in) {
        this.TopicName = in.readString();
        this.TopicID = in.readInt();
    }

    public String getTopicName() {
        return this.TopicName;
    }

    public void setTopicName(String topicName) {
        this.TopicName = topicName;
    }

    public int getTopicId() {
        return this.TopicID;
    }

    public void setTopicId(int topicId) {
        this.TopicID = topicId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.TopicName);
        dest.writeInt(this.TopicID);
    }
}
