package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.bjz.comm.net.expandViewModel.ExpandableStatusFix;
import com.bjz.comm.net.expandViewModel.StatusType;
import com.google.gson.annotations.SerializedName;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import java.io.Serializable;
import java.util.ArrayList;

public class RespFcListBean implements Parcelable, Serializable, ExpandableStatusFix {
    public static final Parcelable.Creator<RespFcListBean> CREATOR = new Parcelable.Creator<RespFcListBean>() {
        public RespFcListBean createFromParcel(Parcel in) {
            return new RespFcListBean(in);
        }

        public RespFcListBean[] newArray(int size) {
            return new RespFcListBean[size];
        }
    };
    private static final long serialVersionUID = -8486142001897485591L;
    private int CommentCount;
    @Mapping(Relation.OneToMany)
    private ArrayList<FcReplyBean> Comments = new ArrayList<>();
    private String Content;
    private int ContentType;
    private long CreateAt;
    private long CreateBy;
    @SerializedName("Creator")
    private FcUserInfoBean CreatorUser;
    private ArrayList<FCEntitysResponse> Entitys = new ArrayList<>();
    @PrimaryKey(AssignType.BY_MYSELF)
    private long ForumID;
    private boolean HasFollow;
    private boolean HasThumb;
    private boolean IsRecommend;
    private double Latitude;
    @Ignore
    private ArrayList<FcLikeBean> Likeds = new ArrayList<>();
    private int LimitVIP;
    private String LocationAddress;
    private String LocationCity;
    private String LocationName;
    private double Longitude;
    @Mapping(Relation.OneToMany)
    private ArrayList<FcMediaBean> Medias = new ArrayList<>();
    private int Permission;
    private ArrayList<String> PermissionUser = new ArrayList<>();
    private int ThumbUp;
    @Ignore
    private ArrayList<FcUserInfoBean> ThumbUserInfos = new ArrayList<>();
    private ArrayList<TopicBean> Topic;
    private StatusType statusType;
    private long thumbId;

    public RespFcListBean() {
    }

    public RespFcListBean(RespFcListBean response) {
        this.ForumID = response.getForumID();
        this.ContentType = response.getContentType();
        this.CreateBy = response.getCreateBy();
        this.CreateAt = response.getCreateAt();
        this.CommentCount = response.getCommentCount();
        this.Content = response.getContent();
        this.Permission = response.getPermission();
        this.Longitude = response.getLongitude();
        this.Latitude = response.getLatitude();
        this.LocationName = response.getLocationName();
        this.LocationAddress = response.getLocationAddress();
        this.LocationCity = response.getLocationCity();
        this.ThumbUp = response.getThumbUp();
        this.HasThumb = response.isHasThumb();
        this.thumbId = response.getThumbId();
        this.HasFollow = response.isHasFollow();
        this.IsRecommend = response.isRecommend();
        this.LimitVIP = response.getRequiredVipLevel();
        this.CreatorUser = response.getCreatorUser();
        this.Comments = response.getComments();
        this.Likeds = response.getLikeds();
        this.Medias = response.getMedias();
        this.Entitys = response.getEntitys();
        this.Topic = response.getTopic();
    }

    protected RespFcListBean(Parcel in) {
        this.ForumID = in.readLong();
        this.ContentType = in.readInt();
        this.CreateBy = in.readLong();
        this.CreateAt = in.readLong();
        this.CommentCount = in.readInt();
        this.Content = in.readString();
        this.Permission = in.readInt();
        this.PermissionUser = in.createStringArrayList();
        this.Longitude = in.readDouble();
        this.Latitude = in.readDouble();
        this.LocationName = in.readString();
        this.LocationAddress = in.readString();
        this.LocationCity = in.readString();
        this.ThumbUp = in.readInt();
        boolean z = true;
        this.HasThumb = in.readByte() != 0;
        this.thumbId = in.readLong();
        this.HasFollow = in.readByte() != 0;
        this.IsRecommend = in.readByte() == 0 ? false : z;
        this.LimitVIP = in.readInt();
        this.CreatorUser = (FcUserInfoBean) in.readParcelable(FcUserInfoBean.class.getClassLoader());
        this.Comments = in.createTypedArrayList(FcReplyBean.CREATOR);
        this.Likeds = in.createTypedArrayList(FcLikeBean.CREATOR);
        this.Medias = in.createTypedArrayList(FcMediaBean.CREATOR);
        this.Entitys = in.createTypedArrayList(FCEntitysResponse.CREATOR);
        this.Topic = in.createTypedArrayList(TopicBean.CREATOR);
        this.ThumbUserInfos = in.createTypedArrayList(FcUserInfoBean.CREATOR);
    }

    public long getForumID() {
        return this.ForumID;
    }

    public void setForumID(long forumID) {
        this.ForumID = forumID;
    }

    public int getContentType() {
        return this.ContentType;
    }

    public void setContentType(int contentType) {
        this.ContentType = contentType;
    }

    public long getCreateBy() {
        return this.CreateBy;
    }

    public void setCreateBy(long createBy) {
        this.CreateBy = createBy;
    }

    public long getCreateAt() {
        return this.CreateAt;
    }

    public void setCreateAt(long createAt) {
        this.CreateAt = createAt;
    }

    public int getCommentCount() {
        return this.CommentCount;
    }

    public void setCommentCount(int commentCount) {
        this.CommentCount = commentCount;
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public int getPermission() {
        return this.Permission;
    }

    public void setPermission(int permission) {
        this.Permission = permission;
    }

    public ArrayList<String> getPermissionUser() {
        return this.PermissionUser;
    }

    public void setPermissionUser(ArrayList<String> permissionUser) {
        this.PermissionUser = permissionUser;
    }

    public double getLongitude() {
        return this.Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    public double getLatitude() {
        return this.Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public String getLocationName() {
        return this.LocationName;
    }

    public void setLocationName(String locationName) {
        this.LocationName = locationName;
    }

    public String getLocationAddress() {
        return this.LocationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.LocationAddress = locationAddress;
    }

    public String getLocationCity() {
        return this.LocationCity;
    }

    public void setLocationCity(String locationCity) {
        this.LocationCity = locationCity;
    }

    public int getThumbUp() {
        return this.ThumbUp;
    }

    public void setThumbUp(int thumbUp) {
        this.ThumbUp = thumbUp;
    }

    public boolean isHasThumb() {
        return this.HasThumb;
    }

    public void setHasThumb(boolean hasThumb) {
        this.HasThumb = hasThumb;
    }

    public long getThumbId() {
        return this.thumbId;
    }

    public void setThumbId(long thumbId2) {
        this.thumbId = thumbId2;
    }

    public boolean isHasFollow() {
        return this.HasFollow;
    }

    public void setHasFollow(boolean hasFollow) {
        this.HasFollow = hasFollow;
    }

    public boolean isRecommend() {
        return this.IsRecommend;
    }

    public void setRecommend(boolean recommend) {
        this.IsRecommend = recommend;
    }

    public int getRequiredVipLevel() {
        return this.LimitVIP;
    }

    public void setRequiredVipLevel(int level) {
        this.LimitVIP = level;
    }

    public FcUserInfoBean getCreatorUser() {
        return this.CreatorUser;
    }

    public void setCreatorUser(FcUserInfoBean creatorUser) {
        this.CreatorUser = creatorUser;
    }

    public ArrayList<FcReplyBean> getComments() {
        return this.Comments;
    }

    public void setComments(ArrayList<FcReplyBean> comments) {
        this.Comments = comments;
    }

    public ArrayList<FcLikeBean> getLikeds() {
        return this.Likeds;
    }

    public void setLikeds(ArrayList<FcLikeBean> likeds) {
        this.Likeds = likeds;
    }

    public ArrayList<FcMediaBean> getMedias() {
        return this.Medias;
    }

    public void setMedias(ArrayList<FcMediaBean> medias) {
        this.Medias = medias;
    }

    public ArrayList<FCEntitysResponse> getEntitys() {
        return this.Entitys;
    }

    public void setEntitys(ArrayList<FCEntitysResponse> entitys) {
        this.Entitys = entitys;
    }

    public ArrayList<TopicBean> getTopic() {
        return this.Topic;
    }

    public void setTopic(ArrayList<TopicBean> topic) {
        this.Topic = topic;
    }

    public ArrayList<FcUserInfoBean> getThumbUserInfos() {
        return this.ThumbUserInfos;
    }

    public void setThumbUserInfos(ArrayList<FcUserInfoBean> thumbUserInfos) {
        this.ThumbUserInfos = thumbUserInfos;
    }

    public StatusType getStatusType() {
        return this.statusType;
    }

    public void setStatusType(StatusType statusType2) {
        this.statusType = statusType2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ForumID);
        dest.writeInt(this.ContentType);
        dest.writeLong(this.CreateBy);
        dest.writeLong(this.CreateAt);
        dest.writeInt(this.CommentCount);
        dest.writeString(this.Content);
        dest.writeInt(this.Permission);
        dest.writeStringList(this.PermissionUser);
        dest.writeDouble(this.Longitude);
        dest.writeDouble(this.Latitude);
        dest.writeString(this.LocationName);
        dest.writeString(this.LocationAddress);
        dest.writeString(this.LocationCity);
        dest.writeInt(this.ThumbUp);
        dest.writeByte(this.HasThumb ? (byte) 1 : 0);
        dest.writeLong(this.thumbId);
        dest.writeByte(this.HasFollow ? (byte) 1 : 0);
        dest.writeByte(this.IsRecommend ? (byte) 1 : 0);
        dest.writeInt(this.LimitVIP);
        dest.writeParcelable(this.CreatorUser, flags);
        dest.writeTypedList(this.Comments);
        dest.writeTypedList(this.Likeds);
        dest.writeTypedList(this.Medias);
        dest.writeTypedList(this.Entitys);
        dest.writeTypedList(this.Topic);
        dest.writeTypedList(this.ThumbUserInfos);
    }
}
