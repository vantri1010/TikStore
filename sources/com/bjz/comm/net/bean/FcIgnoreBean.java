package com.bjz.comm.net.bean;

import java.io.Serializable;

public class FcIgnoreBean implements Serializable {
    private long CreateAt;
    private long FriendID;
    private long ID;
    private int LookFriend;
    private int LookMe;
    private int SwitchType;
    private long UserID;

    public long getID() {
        return this.ID;
    }

    public void setID(long ID2) {
        this.ID = ID2;
    }

    public long getUserID() {
        return this.UserID;
    }

    public void setUserID(long userID) {
        this.UserID = userID;
    }

    public long getFriendID() {
        return this.FriendID;
    }

    public void setFriendID(long friendID) {
        this.FriendID = friendID;
    }

    public int getLookMe() {
        return this.LookMe;
    }

    public void setLookMe(int lookMe) {
        this.LookMe = lookMe;
    }

    public int getLookFriend() {
        return this.LookFriend;
    }

    public void setLookFriend(int lookFriend) {
        this.LookFriend = lookFriend;
    }

    public long getCreateAt() {
        return this.CreateAt;
    }

    public void setCreateAt(long createAt) {
        this.CreateAt = createAt;
    }

    public int getSwitchType() {
        return this.SwitchType;
    }

    public void setSwitchType(int switchType) {
        this.SwitchType = switchType;
    }
}
