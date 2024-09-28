package com.bjz.comm.net.bean;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import java.io.Serializable;

@Table("game_list_all")
public class MiniGameBean implements Serializable {
    private String CoverUrl;
    private int Direction;
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long ID;
    private String Key;
    private String MD5;
    private String Name;
    private String Remark;
    private String UUID;
    private String Url;
    private String Version;
    private String filePath;

    public long getID() {
        return this.ID;
    }

    public void setID(long ID2) {
        this.ID = ID2;
    }

    public String getUUID() {
        return this.UUID;
    }

    public void setUUID(String UUID2) {
        this.UUID = UUID2;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name2) {
        this.Name = Name2;
    }

    public String getVersion() {
        return this.Version;
    }

    public void setVersion(String Version2) {
        this.Version = Version2;
    }

    public String getKey() {
        return this.Key;
    }

    public void setKey(String Key2) {
        this.Key = Key2;
    }

    public String getUrl() {
        return this.Url;
    }

    public void setUrl(String Url2) {
        this.Url = Url2;
    }

    public String getRemark() {
        return this.Remark;
    }

    public void setRemark(String Remark2) {
        this.Remark = Remark2;
    }

    public String getMD5() {
        return this.MD5;
    }

    public void setMD5(String MD52) {
        this.MD5 = MD52;
    }

    public String getCoverUrl() {
        return this.CoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.CoverUrl = coverUrl;
    }

    public int getDirection() {
        return this.Direction;
    }

    public void setDirection(int direction) {
        this.Direction = direction;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath2) {
        this.filePath = filePath2;
    }

    public String toString() {
        return "MiniGameBean{ID=" + this.ID + ", UUID='" + this.UUID + '\'' + ", Name='" + this.Name + '\'' + ", Version='" + this.Version + '\'' + ", Key='" + this.Key + '\'' + ", Url='" + this.Url + '\'' + ", Remark='" + this.Remark + '\'' + ", MD5='" + this.MD5 + '\'' + ", CoverUrl='" + this.CoverUrl + '\'' + ", Direction='" + this.Direction + '\'' + ", filePath='" + this.filePath + '\'' + '}';
    }
}
