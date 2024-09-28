package com.bjz.comm.net.bean;

import java.io.Serializable;

public class FcVersionBean implements Serializable {
    private boolean FollowState;
    private boolean FriendState;
    private int FriendUID;
    private boolean RecommendState;

    public boolean isRecommendState() {
        return this.RecommendState;
    }

    public void setRecommendState(boolean recommendState) {
        this.RecommendState = recommendState;
    }

    public boolean isFriendState() {
        return this.FriendState;
    }

    public void setFriendState(boolean friendState) {
        this.FriendState = friendState;
    }

    public boolean isFollowState() {
        return this.FollowState;
    }

    public void setFollowState(boolean followState) {
        this.FollowState = followState;
    }

    public int getFriendUID() {
        return this.FriendUID;
    }

    public void setFriendUID(int friendUID) {
        this.FriendUID = friendUID;
    }

    public String toString() {
        return "FcVersionBean{, RecommendState=" + this.RecommendState + ", FriendState=" + this.FriendState + ", FriendUID=" + this.FriendUID + ", FollowState=" + this.FollowState + '}';
    }
}
