package com.bjz.comm.net.bean;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import java.io.Serializable;

@Table("mp_list_all")
public class MiniProgramBean implements Serializable {
    private String AppId;
    private String AppRouterPath;
    private int Classtify;
    private String CreateAt;
    private String CreateBy;
    private int DownloadCount;
    private String DownloadURL;
    private String FileName;
    private String FilePath;
    @PrimaryKey(AssignType.BY_MYSELF)
    private int ID;
    private String Introduction;
    private boolean IsNeedRqToken;
    private String Language;
    private String Logo;
    private String Name;
    private int Score;
    private int Size;
    private String Slogan;
    private int State;
    private int Version;
    private String VersionCreateAt;
    private DigitalTokenBean digitalTokenBean;

    public MiniProgramBean(int ID2) {
        this.ID = ID2;
    }

    public MiniProgramBean(int ID2, boolean isNeedToken, String appRouterPath) {
        this.ID = ID2;
        this.IsNeedRqToken = isNeedToken;
        this.AppRouterPath = appRouterPath;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID2) {
        this.ID = ID2;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name2) {
        this.Name = Name2;
    }

    public int getClasstify() {
        return this.Classtify;
    }

    public void setClasstify(int Classtify2) {
        this.Classtify = Classtify2;
    }

    public String getCreateAt() {
        return this.CreateAt;
    }

    public void setCreateAt(String CreateAt2) {
        this.CreateAt = CreateAt2;
    }

    public String getCreateBy() {
        return this.CreateBy;
    }

    public void setCreateBy(String CreateBy2) {
        this.CreateBy = CreateBy2;
    }

    public String getLanguage() {
        return this.Language;
    }

    public void setLanguage(String Language2) {
        this.Language = Language2;
    }

    public int getScore() {
        return this.Score;
    }

    public void setScore(int Score2) {
        this.Score = Score2;
    }

    public int getState() {
        return this.State;
    }

    public void setState(int State2) {
        this.State = State2;
    }

    public int getVersion() {
        return this.Version;
    }

    public void setVersion(int Version2) {
        this.Version = Version2;
    }

    public int getSize() {
        return this.Size;
    }

    public void setSize(int Size2) {
        this.Size = Size2;
    }

    public String getDownloadURL() {
        return this.DownloadURL;
    }

    public void setDownloadURL(String DownloadURL2) {
        this.DownloadURL = DownloadURL2;
    }

    public int getDownloadCount() {
        return this.DownloadCount;
    }

    public void setDownloadCount(int DownloadCount2) {
        this.DownloadCount = DownloadCount2;
    }

    public String getIntroduction() {
        return this.Introduction;
    }

    public void setIntroduction(String Introduction2) {
        this.Introduction = Introduction2;
    }

    public String getSlogan() {
        return this.Slogan;
    }

    public void setSlogan(String Slogan2) {
        this.Slogan = Slogan2;
    }

    public String getLogo() {
        return this.Logo;
    }

    public void setLogo(String Logo2) {
        this.Logo = Logo2;
    }

    public String getVersionCreateAt() {
        return this.VersionCreateAt;
    }

    public void setVersionCreateAt(String VersionCreateAt2) {
        this.VersionCreateAt = VersionCreateAt2;
    }

    public String getFileName() {
        return this.FileName;
    }

    public void setFileName(String fileName) {
        this.FileName = fileName;
    }

    public String getAppId() {
        return this.AppId;
    }

    public void setAppId(String appId) {
        this.AppId = appId;
    }

    public String getFilePath() {
        return this.FilePath;
    }

    public void setFilePath(String filePath) {
        this.FilePath = filePath;
    }

    public boolean isNeedRqToken() {
        return this.IsNeedRqToken;
    }

    public void setNeedRqToken(boolean needRqToken) {
        this.IsNeedRqToken = needRqToken;
    }

    public String getAppRouterPath() {
        return this.AppRouterPath;
    }

    public void setAppRouterPath(String appRouterPath) {
        this.AppRouterPath = appRouterPath;
    }

    public DigitalTokenBean getDigitalTokenBean() {
        return this.digitalTokenBean;
    }

    public void setDigitalTokenBean(DigitalTokenBean digitalTokenBean2) {
        this.digitalTokenBean = digitalTokenBean2;
    }

    public String toString() {
        return "MiniProgramBean{ID=" + this.ID + ", Name='" + this.Name + '\'' + ", Classtify=" + this.Classtify + ", CreateAt='" + this.CreateAt + '\'' + ", CreateBy='" + this.CreateBy + '\'' + ", Language='" + this.Language + '\'' + ", Score=" + this.Score + ", State=" + this.State + ", Version=" + this.Version + ", Size=" + this.Size + ", DownloadURL='" + this.DownloadURL + '\'' + ", DownloadCount=" + this.DownloadCount + ", Introduction='" + this.Introduction + '\'' + ", Slogan='" + this.Slogan + '\'' + ", Logo='" + this.Logo + '\'' + ", VersionCreateAt='" + this.VersionCreateAt + '\'' + ", FileName='" + this.FileName + '\'' + ", AppId='" + this.AppId + '\'' + ", FilePath='" + this.FilePath + '\'' + ", IsNeedRqToken='" + this.IsNeedRqToken + '\'' + ", AppRouterPath='" + this.AppRouterPath + '\'' + '}';
    }
}
