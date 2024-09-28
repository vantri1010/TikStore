package com.bjz.comm.net.bean;

import java.io.Serializable;

public class ResponseFcAlbumlistBean extends RecycleGridBean implements Serializable {
    private long CreateTime;
    private String FileName;
    private int FileSize;
    private int ForumID;
    private int ID;
    private int Index;
    private int PicHeight;
    private int PicWidth;
    private String Region;
    private String Thum;
    private int ThumSize;
    private String ThumbKeyHash;
    private String URL;
    private String URLKeyHash;
    private int URLType;
    private int VideoDuration;
    private int VideoHeight;
    private int VideoWidth;

    public int getID() {
        return this.ID;
    }

    public void setID(int ID2) {
        this.ID = ID2;
    }

    public int getForumID() {
        return this.ForumID;
    }

    public void setForumID(int ForumID2) {
        this.ForumID = ForumID2;
    }

    public int getIndex() {
        return this.Index;
    }

    public void setIndex(int Index2) {
        this.Index = Index2;
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

    public void setThum(String Thum2) {
        this.Thum = Thum2;
    }

    public String getRegion() {
        return this.Region;
    }

    public void setRegion(String Region2) {
        this.Region = Region2;
    }

    public int getFileSize() {
        return this.FileSize;
    }

    public void setFileSize(int FileSize2) {
        this.FileSize = FileSize2;
    }

    public String getFileName() {
        return this.FileName;
    }

    public void setFileName(String FileName2) {
        this.FileName = FileName2;
    }

    public int getThumSize() {
        return this.ThumSize;
    }

    public void setThumSize(int ThumSize2) {
        this.ThumSize = ThumSize2;
    }

    public long getCreateTime() {
        return this.CreateTime;
    }

    public void setCreateTime(long CreateTime2) {
        this.CreateTime = CreateTime2;
    }

    public String getURLKeyHash() {
        return this.URLKeyHash;
    }

    public void setURLKeyHash(String URLKeyHash2) {
        this.URLKeyHash = URLKeyHash2;
    }

    public String getThumbKeyHash() {
        return this.ThumbKeyHash;
    }

    public void setThumbKeyHash(String ThumbKeyHash2) {
        this.ThumbKeyHash = ThumbKeyHash2;
    }

    public int getPicHeight() {
        return this.PicHeight;
    }

    public void setPicHeight(int PicHeight2) {
        this.PicHeight = PicHeight2;
    }

    public int getPicWidth() {
        return this.PicWidth;
    }

    public void setPicWidth(int PicWidth2) {
        this.PicWidth = PicWidth2;
    }

    public int getVideoDuration() {
        return this.VideoDuration;
    }

    public void setVideoDuration(int VideoDuration2) {
        this.VideoDuration = VideoDuration2;
    }

    public int getVideoHeight() {
        return this.VideoHeight;
    }

    public void setVideoHeight(int VideoHeight2) {
        this.VideoHeight = VideoHeight2;
    }

    public int getVideoWidth() {
        return this.VideoWidth;
    }

    public void setVideoWidth(int VideoWidth2) {
        this.VideoWidth = VideoWidth2;
    }

    public String toString() {
        return "ResponseFcAlbumlistBean{ID=" + this.ID + ", ForumID=" + this.ForumID + ", Index=" + this.Index + ", URL='" + this.URL + '\'' + ", URLType=" + this.URLType + ", Thum='" + this.Thum + '\'' + ", Region='" + this.Region + '\'' + ", FileSize=" + this.FileSize + ", FileName='" + this.FileName + '\'' + ", ThumSize=" + this.ThumSize + ", CreateTime=" + this.CreateTime + ", URLKeyHash='" + this.URLKeyHash + '\'' + ", ThumbKeyHash='" + this.ThumbKeyHash + '\'' + ", PicHeight=" + this.PicHeight + ", PicWidth=" + this.PicWidth + ", VideoDuration=" + this.VideoDuration + ", VideoHeight=" + this.VideoHeight + ", VideoWidth=" + this.VideoWidth + '}';
    }
}
