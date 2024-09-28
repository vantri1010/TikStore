package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import java.io.Serializable;

@Table("fc_list_media")
public class FcMediaBean implements Parcelable, Serializable {
    public static final Parcelable.Creator<FcMediaBean> CREATOR = new Parcelable.Creator<FcMediaBean>() {
        public FcMediaBean createFromParcel(Parcel in) {
            return new FcMediaBean(in);
        }

        public FcMediaBean[] newArray(int size) {
            return new FcMediaBean[size];
        }
    };
    private static final long serialVersionUID = 7425577064178720227L;
    private long CreateAt;
    private int Ext;
    private int Height;
    private String Name;
    private int Seq;
    private String Thum;
    private int Width;
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    public FcMediaBean() {
    }

    public FcMediaBean(int seq, String name, int ext, String thum) {
        this.Seq = seq;
        this.Name = name;
        this.Ext = ext;
        this.Thum = thum;
    }

    protected FcMediaBean(Parcel in) {
        this.id = in.readInt();
        this.Name = in.readString();
        this.Ext = in.readInt();
        this.Thum = in.readString();
        this.Height = in.readInt();
        this.Width = in.readInt();
        this.Seq = in.readInt();
        this.CreateAt = in.readLong();
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String url) {
        this.Name = url;
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

    public int getId() {
        return this.id;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public double getHeight() {
        return (double) this.Height;
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

    public int getSeq() {
        return this.Seq;
    }

    public void setSeq(int seq) {
        this.Seq = seq;
    }

    public String toString() {
        return "FcMediaBean{id=" + this.id + ", Seq='" + this.Seq + '\'' + ", Name='" + this.Name + '\'' + ", Ext='" + this.Ext + '\'' + ", Thum='" + this.Thum + '\'' + ", Height=" + this.Height + ", Width=" + this.Width + ", CreateAt=" + this.CreateAt + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.Name);
        dest.writeInt(this.Ext);
        dest.writeString(this.Thum);
        dest.writeInt(this.Height);
        dest.writeInt(this.Width);
        dest.writeInt(this.Seq);
        dest.writeLong(this.CreateAt);
    }
}
