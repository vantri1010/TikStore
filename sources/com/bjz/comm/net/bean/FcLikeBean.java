package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class FcLikeBean implements Parcelable, Serializable {
    public static final Parcelable.Creator<FcLikeBean> CREATOR = new Parcelable.Creator<FcLikeBean>() {
        public FcLikeBean createFromParcel(Parcel in) {
            return new FcLikeBean(in);
        }

        public FcLikeBean[] newArray(int size) {
            return new FcLikeBean[size];
        }
    };
    private static final long serialVersionUID = -5288893082273507011L;
    private long CommentID;
    private long CommentUID;
    private long CreateAt;
    private long CreateBy;
    private FcUserInfoBean Creator;
    private long ForumID;
    private long ForumUID;
    private long ThumbID;
    private int UpDown;

    public FcLikeBean() {
    }

    protected FcLikeBean(Parcel in) {
        this.ThumbID = in.readLong();
        this.ForumID = in.readLong();
        this.ForumUID = in.readLong();
        this.CommentID = in.readLong();
        this.CommentUID = in.readLong();
        this.CreateAt = in.readLong();
        this.CreateBy = in.readLong();
        this.UpDown = in.readInt();
        this.Creator = (FcUserInfoBean) in.readParcelable(FcUserInfoBean.class.getClassLoader());
    }

    public long getThumbID() {
        return this.ThumbID;
    }

    public void setThumbID(long thumbID) {
        this.ThumbID = thumbID;
    }

    public long getForumID() {
        return this.ForumID;
    }

    public void setForumID(long forumID) {
        this.ForumID = forumID;
    }

    public long getForumUID() {
        return this.ForumUID;
    }

    public void setForumUID(long forumUID) {
        this.ForumUID = forumUID;
    }

    public long getCommentID() {
        return this.CommentID;
    }

    public void setCommentID(long commentID) {
        this.CommentID = commentID;
    }

    public long getCommentUID() {
        return this.CommentUID;
    }

    public void setCommentUID(long commentUID) {
        this.CommentUID = commentUID;
    }

    public long getCreateAt() {
        return this.CreateAt;
    }

    public void setCreateAt(long createAt) {
        this.CreateAt = createAt;
    }

    public long getCreateBy() {
        return this.CreateBy;
    }

    public void setCreateBy(long createBy) {
        this.CreateBy = createBy;
    }

    public int getUpDown() {
        return this.UpDown;
    }

    public void setUpDown(int upDown) {
        this.UpDown = upDown;
    }

    public FcUserInfoBean getCreator() {
        return this.Creator;
    }

    public void setCreator(FcUserInfoBean creator) {
        this.Creator = creator;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ThumbID);
        dest.writeLong(this.ForumID);
        dest.writeLong(this.ForumUID);
        dest.writeLong(this.CommentID);
        dest.writeLong(this.CommentUID);
        dest.writeLong(this.CreateAt);
        dest.writeLong(this.CreateBy);
        dest.writeInt(this.UpDown);
        dest.writeParcelable(this.Creator, flags);
    }
}
