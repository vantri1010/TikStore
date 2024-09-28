package im.bclpbkiauv.javaBean.fc;

import com.bjz.comm.net.bean.FcMediaBean;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import java.io.Serializable;
import java.util.ArrayList;

@Table("fclist_content_home")
public class FcContentBean implements Serializable {
    @Mapping(Relation.OneToMany)
    private ArrayList<FcMediaBean> AttachList = new ArrayList<>();
    private String Content;
    private int ContentType;
    @PrimaryKey(AssignType.BY_MYSELF)
    private long ForumID;
    private int LikeCount;
    private long OwenerID;
    private long PostTime;
    private int ReplyCount;
    private String ReplyFromID;
    private String ReplyToID;
    private int Right;
    private long SourceForumID;
    private ArrayList<String> ThumList = new ArrayList<>();
    private ArrayList<String> URLList = new ArrayList<>();
    private ArrayList<String> URLTypeList = new ArrayList<>();
    private int VideoHeight;
    private int VideoWidth;
    private String ViewCount;

    public int getRight() {
        return this.Right;
    }

    public void setRight(int right) {
        this.Right = right;
    }

    public int getVideoWidth() {
        return this.VideoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.VideoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return this.VideoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.VideoHeight = videoHeight;
    }

    public long getForumID() {
        return this.ForumID;
    }

    public void setForumID(long forumID) {
        this.ForumID = forumID;
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public long getPostTime() {
        return this.PostTime;
    }

    public void setPostTime(long postTime) {
        this.PostTime = postTime;
    }

    public int getContentType() {
        return this.ContentType;
    }

    public void setContentType(int contentType) {
        this.ContentType = contentType;
    }

    public long getSourceForumID() {
        return this.SourceForumID;
    }

    public void setSourceForumID(long sourceForumID) {
        this.SourceForumID = sourceForumID;
    }

    public long getOwenerID() {
        return this.OwenerID;
    }

    public void setOwenerID(long owenerID) {
        this.OwenerID = owenerID;
    }

    public String getReplyFromID() {
        return this.ReplyFromID;
    }

    public void setReplyFromID(String replyFromID) {
        this.ReplyFromID = replyFromID;
    }

    public String getReplyToID() {
        return this.ReplyToID;
    }

    public void setReplyToID(String replyToID) {
        this.ReplyToID = replyToID;
    }

    public int getLikeCount() {
        return this.LikeCount;
    }

    public void setLikeCount(int likeCount) {
        this.LikeCount = likeCount;
    }

    public String getViewCount() {
        return this.ViewCount;
    }

    public void setViewCount(String viewCount) {
        this.ViewCount = viewCount;
    }

    public int getReplyCount() {
        return this.ReplyCount;
    }

    public void setReplyCount(int replyCount) {
        this.ReplyCount = replyCount;
    }

    public ArrayList<FcMediaBean> getAttachList() {
        return this.AttachList;
    }

    public void setAttachList(ArrayList<FcMediaBean> attachList) {
        this.AttachList = attachList;
    }

    public ArrayList<String> getURLList() {
        return this.URLList;
    }

    public void setURLList(ArrayList<String> URLList2) {
        this.URLList = URLList2;
    }

    public ArrayList<String> getURLTypeList() {
        return this.URLTypeList;
    }

    public void setURLTypeList(ArrayList<String> URLTypeList2) {
        this.URLTypeList = URLTypeList2;
    }

    public ArrayList<String> getThumList() {
        return this.ThumList;
    }

    public void setThumList(ArrayList<String> thumList) {
        this.ThumList = thumList;
    }

    public String toString() {
        return "FcContentBean{ForumID=" + this.ForumID + ", Content='" + this.Content + '\'' + ", PostTime='" + this.PostTime + '\'' + ", ContentType=" + this.ContentType + ", SourceForumID=" + this.SourceForumID + ", OwenerID=" + this.OwenerID + ", ReplyFromID='" + this.ReplyFromID + '\'' + ", ReplyToID='" + this.ReplyToID + '\'' + ", LikeCount=" + this.LikeCount + ", ViewCount='" + this.ViewCount + '\'' + ", ReplyCount=" + this.ReplyCount + ", VideoWidth=" + this.VideoWidth + ", VideoHeight=" + this.VideoHeight + ", Right=" + this.Right + ", AttachList=" + this.AttachList + ", URLList=" + this.URLList + ", URLTypeList=" + this.URLTypeList + ", ThumList=" + this.ThumList + '}';
    }
}
