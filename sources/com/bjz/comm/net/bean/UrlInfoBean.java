package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.bjz.comm.net.utils.HttpUtils;

public class UrlInfoBean implements Parcelable {
    public static final Parcelable.Creator<UrlInfoBean> CREATOR = new Parcelable.Creator<UrlInfoBean>() {
        public UrlInfoBean createFromParcel(Parcel in) {
            return new UrlInfoBean(in);
        }

        public UrlInfoBean[] newArray(int size) {
            return new UrlInfoBean[size];
        }
    };
    private long CreateTime;
    private long ForumID;
    private String Thum;
    private String URL;
    private int URLType;
    private float VideoHeight;
    private float VideoWidth;

    protected UrlInfoBean(Parcel in) {
        this.ForumID = in.readLong();
        this.URL = in.readString();
        this.URLType = in.readInt();
        this.Thum = in.readString();
        this.CreateTime = in.readLong();
        this.VideoHeight = in.readFloat();
        this.VideoWidth = in.readFloat();
    }

    public UrlInfoBean(RespFcAlbumListBean bean) {
        this.ForumID = bean.getMainID();
        this.URL = HttpUtils.getInstance().getDownloadFileUrl() + bean.getName();
        this.URLType = bean.getExt();
        this.Thum = bean.getThum();
        this.CreateTime = bean.getCreateAt();
        this.VideoWidth = (float) bean.getWidth();
        this.VideoHeight = (float) bean.getHeight();
    }

    public UrlInfoBean(long forumID, String URL2, int URLType2, String thum, long createTime, int videoHeight, int videoWidth) {
        this.ForumID = forumID;
        this.URL = URL2;
        this.URLType = URLType2;
        this.Thum = thum;
        this.CreateTime = createTime;
        this.VideoHeight = (float) videoHeight;
        this.VideoWidth = (float) videoWidth;
    }

    public long getForumID() {
        return this.ForumID;
    }

    public void setForumID(long forumID) {
        this.ForumID = forumID;
    }

    public String getURL() {
        return this.URL;
    }

    public void setURL(String URL2) {
        this.URL = URL2;
    }

    public int getURLType() {
        return this.URLType;
    }

    public void setURLType(int URLType2) {
        this.URLType = URLType2;
    }

    public String getThum() {
        return this.Thum;
    }

    public void setThum(String thum) {
        this.Thum = thum;
    }

    public long getCreateTime() {
        return this.CreateTime;
    }

    public void setCreateTime(long createTime) {
        this.CreateTime = createTime;
    }

    public float getVideoHeight() {
        return this.VideoHeight;
    }

    public void setVideoHeight(float videoHeight) {
        this.VideoHeight = videoHeight;
    }

    public float getVideoWidth() {
        return this.VideoWidth;
    }

    public void setVideoWidth(float videoWidth) {
        this.VideoWidth = videoWidth;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ForumID);
        dest.writeString(this.URL);
        dest.writeInt(this.URLType);
        dest.writeString(this.Thum);
        dest.writeLong(this.CreateTime);
        dest.writeFloat(this.VideoHeight);
        dest.writeFloat(this.VideoWidth);
    }

    public String toString() {
        return "UrlInfoBean{ForumID=" + this.ForumID + ", URL='" + this.URL + '\'' + ", URLType=" + this.URLType + ", Thum='" + this.Thum + '\'' + ", CreateTime=" + this.CreateTime + ", VideoHeight=" + this.VideoHeight + ", VideoWidth=" + this.VideoWidth + '}';
    }
}
