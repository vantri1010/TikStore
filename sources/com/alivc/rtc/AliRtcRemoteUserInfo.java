package com.alivc.rtc;

import com.alivc.rtc.AliRtcEngine;

public class AliRtcRemoteUserInfo {
    private String callID;
    private AliRtcEngine.AliVideoCanvas cameraCanvas;
    private String displayName;
    private boolean firstSubscribe = true;
    private boolean hasAudio;
    private boolean hasCameraMaster;
    private boolean hasCameraSlave;
    private boolean hasScreenSharing;
    private boolean isOnline;
    private boolean muteAudioPlaying;
    private boolean requestAudio;
    private boolean requestCamera;
    private boolean requestCameraMaster;
    private boolean requestScreenSharing;
    private AliRtcEngine.AliVideoCanvas screenCanvas;
    private String sessionID;
    private String streamLabel;
    private boolean subAudio;
    private boolean subCamera;
    private boolean subCameraMaster;
    private boolean subScreenSharing;
    private String userID;

    public String getUserID() {
        return this.userID;
    }

    /* access modifiers changed from: package-private */
    public void setUserID(String userID2) {
        this.userID = userID2;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    /* access modifiers changed from: package-private */
    public void setSessionID(String sessionID2) {
        this.sessionID = sessionID2;
    }

    public String getCallID() {
        return this.callID;
    }

    /* access modifiers changed from: package-private */
    public void setCallID(String callID2) {
        this.callID = callID2;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    /* access modifiers changed from: package-private */
    public void setDisplayName(String displayName2) {
        this.displayName = displayName2;
    }

    public String getStreamLabel() {
        return this.streamLabel;
    }

    /* access modifiers changed from: package-private */
    public void setStreamLabel(String streamLabel2) {
        this.streamLabel = streamLabel2;
    }

    public boolean isFirstSubscribe() {
        return this.firstSubscribe;
    }

    /* access modifiers changed from: package-private */
    public void setFirstSubscribe(boolean firstSubscribe2) {
        this.firstSubscribe = firstSubscribe2;
    }

    public AliRtcEngine.AliVideoCanvas getCameraCanvas() {
        return this.cameraCanvas;
    }

    /* access modifiers changed from: package-private */
    public void setCameraCanvas(AliRtcEngine.AliVideoCanvas cameraCanvas2) {
        this.cameraCanvas = cameraCanvas2;
    }

    public AliRtcEngine.AliVideoCanvas getScreenCanvas() {
        return this.screenCanvas;
    }

    /* access modifiers changed from: package-private */
    public void setScreenCanvas(AliRtcEngine.AliVideoCanvas screenCanvas2) {
        this.screenCanvas = screenCanvas2;
    }

    public boolean isMuteAudioPlaying() {
        return this.muteAudioPlaying;
    }

    /* access modifiers changed from: package-private */
    public void setMuteAudioPlaying(boolean muteAudioPlaying2) {
        this.muteAudioPlaying = muteAudioPlaying2;
    }

    public boolean isOnline() {
        return this.isOnline;
    }

    /* access modifiers changed from: package-private */
    public void setOnline(boolean online) {
        this.isOnline = online;
    }

    public boolean isHasAudio() {
        return this.hasAudio;
    }

    /* access modifiers changed from: package-private */
    public void setHasAudio(boolean has) {
        this.hasAudio = has;
    }

    public boolean isHasCameraMaster() {
        return this.hasCameraMaster;
    }

    /* access modifiers changed from: package-private */
    public void setHasCameraMaster(boolean has) {
        this.hasCameraMaster = has;
    }

    public boolean isHasCameraSlave() {
        return this.hasCameraSlave;
    }

    /* access modifiers changed from: package-private */
    public void setHasCameraSlave(boolean has) {
        this.hasCameraSlave = has;
    }

    public boolean isHasScreenSharing() {
        return this.hasScreenSharing;
    }

    /* access modifiers changed from: package-private */
    public void setHasScreenSharing(boolean has) {
        this.hasScreenSharing = has;
    }

    /* access modifiers changed from: package-private */
    public void setSubAudio(boolean sub) {
        this.subAudio = sub;
    }

    public boolean isSubAudio() {
        return this.subAudio;
    }

    /* access modifiers changed from: package-private */
    public void setSubCameraMaster(boolean sub) {
        this.subCameraMaster = sub;
    }

    public boolean isSubCameraMaster() {
        return this.subCameraMaster;
    }

    /* access modifiers changed from: package-private */
    public void setSubCamera(boolean sub) {
        this.subCamera = sub;
    }

    public boolean isSubCamera() {
        return this.subCamera;
    }

    /* access modifiers changed from: package-private */
    public void setSubScreenSharing(boolean sub) {
        this.subScreenSharing = sub;
    }

    public boolean isSubScreenSharing() {
        return this.subScreenSharing;
    }

    /* access modifiers changed from: package-private */
    public void setRequestSubAudio(boolean request) {
        this.requestAudio = request;
    }

    public boolean isRequestSubAudio() {
        return this.requestAudio;
    }

    /* access modifiers changed from: package-private */
    public void setRequestCameraMaster(boolean request) {
        this.requestCameraMaster = request;
    }

    public boolean isRequestCameraMaster() {
        return this.requestCameraMaster;
    }

    /* access modifiers changed from: package-private */
    public void setRequestCamera(boolean request) {
        this.requestCamera = request;
    }

    public boolean isRequestCamera() {
        return this.requestCamera;
    }

    /* access modifiers changed from: package-private */
    public void setRequestScreenSharing(boolean request) {
        this.requestScreenSharing = request;
    }

    public boolean isRequestScreenSharing() {
        return this.requestScreenSharing;
    }

    public String toString() {
        return "AliRtcRemoteUserInfo{userID='" + this.userID + '\'' + ", sessionID='" + this.sessionID + '\'' + ", callID='" + this.callID + '\'' + ", displayName='" + this.displayName + '\'' + ", streamLabel='" + this.streamLabel + '\'' + ", firstSubscribe=" + this.firstSubscribe + ", muteAudioPlaying=" + this.muteAudioPlaying + ", isOnline=" + this.isOnline + ", cameraCanvas=" + this.cameraCanvas + ", screenCanvas=" + this.screenCanvas + ", hasAudio=" + this.hasAudio + ", hasCameraMaster=" + this.hasCameraMaster + ", hasCameraSlave=" + this.hasCameraSlave + ", hasScreenSharing=" + this.hasScreenSharing + ", subAudio=" + this.subAudio + ", subCamera=" + this.subCamera + ", subCameraMaster=" + this.subCameraMaster + ", subScreenSharing=" + this.subScreenSharing + ", requestAudio=" + this.requestAudio + ", requestCamera=" + this.requestCamera + ", requestCameraMaster=" + this.requestCameraMaster + ", requestScreenSharing=" + this.requestScreenSharing + '}';
    }
}
