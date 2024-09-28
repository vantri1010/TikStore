package com.bjz.comm.net.bean;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import java.io.Serializable;

@Table("fc_myfocus_user")
public class ResponseFcAttentionUsertBean implements Serializable {
    private int Enable;
    private int FocusDatatime;
    private int FocusID;
    @PrimaryKey(AssignType.BY_MYSELF)
    private int ID;
    private int UserID;

    public int getID() {
        return this.ID;
    }

    public void setID(int ID2) {
        this.ID = ID2;
    }

    public int getUserID() {
        return this.UserID;
    }

    public void setUserID(int userID) {
        this.UserID = userID;
    }

    public int getFocusId() {
        return this.FocusID;
    }

    public void setFocusId(int focusId) {
        this.FocusID = focusId;
    }

    public int getFocusDatatime() {
        return this.FocusDatatime;
    }

    public void setFocusDatatime(int focusDatatime) {
        this.FocusDatatime = focusDatatime;
    }

    public int getEnable() {
        return this.Enable;
    }

    public void setEnable(int enable) {
        this.Enable = enable;
    }
}
