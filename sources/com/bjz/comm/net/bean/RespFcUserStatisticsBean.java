package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class RespFcUserStatisticsBean implements Parcelable {
    public static final Parcelable.Creator<RespFcUserStatisticsBean> CREATOR = new Parcelable.Creator<RespFcUserStatisticsBean>() {
        public RespFcUserStatisticsBean createFromParcel(Parcel in) {
            return new RespFcUserStatisticsBean(in);
        }

        public RespFcUserStatisticsBean[] newArray(int size) {
            return new RespFcUserStatisticsBean[size];
        }
    };
    private int FansCount;
    private int FollowCount;
    private int ForumCount;
    private String HomeBackground;
    private int ThumbCount;
    private FcUserInfoBean User;

    protected RespFcUserStatisticsBean(Parcel in) {
        this.ForumCount = in.readInt();
        this.ThumbCount = in.readInt();
        this.FansCount = in.readInt();
        this.FollowCount = in.readInt();
        this.HomeBackground = in.readString();
        this.User = (FcUserInfoBean) in.readParcelable(FcUserInfoBean.class.getClassLoader());
    }

    public int getForumCount() {
        return Math.max(this.ForumCount, 0);
    }

    public void setForumCount(int forumCount) {
        this.ForumCount = forumCount;
    }

    public int getThumbCount() {
        return Math.max(this.ThumbCount, 0);
    }

    public void setThumbCount(int thumbCount) {
        this.ThumbCount = thumbCount;
    }

    public int getFansCount() {
        return Math.max(this.FansCount, 0);
    }

    public void setFansCount(int fansCount) {
        this.FansCount = fansCount;
    }

    public int getFollowCount() {
        return Math.max(this.FollowCount, 0);
    }

    public void setFollowCount(int followCount) {
        this.FollowCount = followCount;
    }

    public String getHomeBackground() {
        return this.HomeBackground;
    }

    public void setHomeBackground(String homeBackground) {
        this.HomeBackground = homeBackground;
    }

    public FcUserInfoBean getUser() {
        return this.User;
    }

    public void setUser(FcUserInfoBean user) {
        this.User = user;
    }

    public String toString() {
        return "RespFcUserStatisticsBean{ForumCount=" + this.ForumCount + ", ThumbCount=" + this.ThumbCount + ", FansCount=" + this.FansCount + ", FollowCount=" + this.FollowCount + ", HomeBackground='" + this.HomeBackground + '\'' + ", User=" + this.User + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ForumCount);
        dest.writeInt(this.ThumbCount);
        dest.writeInt(this.FansCount);
        dest.writeInt(this.FollowCount);
        dest.writeString(this.HomeBackground);
        dest.writeParcelable(this.User, flags);
    }
}
