package com.bjz.comm.net.bean;

import java.io.Serializable;
import java.util.List;

public class FriendsCircleHomeBean implements Serializable {
    int avatarId;
    int imageId;
    String name;
    String nickname;
    List<String> pics;

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname2) {
        this.nickname = nickname2;
    }

    public int getImageId() {
        return this.imageId;
    }

    public void setImageId(int imageId2) {
        this.imageId = imageId2;
    }

    public int getAvatarId() {
        return this.avatarId;
    }

    public void setAvatarId(int avatarId2) {
        this.avatarId = avatarId2;
    }

    public List<String> getPics() {
        return this.pics;
    }

    public void setPics(List<String> pics2) {
        this.pics = pics2;
    }
}
