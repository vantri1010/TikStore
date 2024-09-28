package com.alivc.rtc;

import java.util.Arrays;

public class AliRtcAuthInfo {
    public String[] mAgent;
    public String mAppid;
    public String mConferenceId;
    public String[] mGslb;
    public String mNonce;
    public long mTimestamp;
    public String mToken;
    public String mUserId;

    public String getConferenceId() {
        return this.mConferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.mConferenceId = conferenceId;
    }

    public String getUserId() {
        return this.mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getAppid() {
        return this.mAppid;
    }

    public void setAppid(String appid) {
        this.mAppid = appid;
    }

    public String getNonce() {
        return this.mNonce;
    }

    public void setNonce(String nonce) {
        this.mNonce = nonce;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        this.mTimestamp = timestamp;
    }

    public String getToken() {
        return this.mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String[] getGslb() {
        return this.mGslb;
    }

    public void setGslb(String[] gslb) {
        this.mGslb = gslb;
    }

    public String[] getAgent() {
        return this.mAgent;
    }

    public void setAgent(String[] agent) {
        this.mAgent = agent;
    }

    public String toString() {
        return "AliRtcAuthInfo{mConferenceId='" + this.mConferenceId + '\'' + ", mUserId='" + this.mUserId + '\'' + ", mAppid='" + this.mAppid + '\'' + ", mNonce='" + this.mNonce + '\'' + ", mTimestamp=" + this.mTimestamp + ", mToken='" + this.mToken + '\'' + ", mGslb=" + Arrays.toString(this.mGslb) + ", mAgent=" + Arrays.toString(this.mAgent) + '}';
    }
}
