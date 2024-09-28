package com.bjz.comm.net.bean;

import java.io.Serializable;

public class RespFcAlbumListBean extends RecycleGridBean implements Serializable {
    private long CreateAt;
    private int Ext;
    private int Height;
    private long ID;
    private long MainID;
    private String Name;
    private String Thum;
    private int Width;

    public long getID() {
        return this.ID;
    }

    public void setID(long ID2) {
        this.ID = ID2;
    }

    public long getMainID() {
        return this.MainID;
    }

    public void setMainID(long mainID) {
        this.MainID = mainID;
    }

    public int getExt() {
        return this.Ext;
    }

    public void setExt(int ext) {
        this.Ext = ext;
    }

    public String getThum() {
        return this.Thum;
    }

    public void setThum(String thum) {
        this.Thum = thum;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public int getHeight() {
        return this.Height;
    }

    public void setHeight(int height) {
        this.Height = height;
    }

    public int getWidth() {
        return this.Width;
    }

    public void setWidth(int width) {
        this.Width = width;
    }

    public long getCreateAt() {
        return this.CreateAt;
    }

    public void setCreateAt(long createAt) {
        this.CreateAt = createAt;
    }

    public String toString() {
        return "RespFcAlbumListBean{ID=" + this.ID + ", MainID=" + this.MainID + ", Ext=" + this.Ext + ", Thum='" + this.Thum + '\'' + ", Name='" + this.Name + '\'' + ", Height=" + this.Height + ", Width=" + this.Width + ", CreateAt=" + this.CreateAt + '}';
    }
}
