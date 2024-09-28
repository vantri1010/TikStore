package com.alivc.rtc;

import com.alivc.rtc.AliRtcEngine;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AliRtcConfig {
    private boolean audioOnly = false;
    private boolean autoPublish = true;
    private boolean autoSubscribe = true;
    private AliRtcAuthInfo cachedAuthorInfo = null;
    private String cachedUserName = "";
    private boolean camAutoFocus = true;
    private boolean camFlash = false;
    private AliRtcEngine.AliRtcVideoProfile camVideoProfile = AliRtcEngine.AliRtcVideoProfile.AliRTCSDK_Video_Profile_Default;
    private float camZoom = 1.0f;
    private int cameraType = 1;
    private boolean dualStream = true;
    private boolean hasLeftChannel = true;
    private boolean isCameraOn = false;
    private boolean isInCall = false;
    private boolean isSpeakerOn = false;
    private boolean keepInChannel = false;
    private String localCallID = "";
    private String localCallID_tmp = "";
    private AliRtcEngine.AliVideoCanvas localVideoCanvas = null;
    private boolean muteLocalCameraVideo = false;
    private boolean muteLocalMic = false;
    private boolean muteLocalScreenVideo = false;
    private boolean publishAudio = true;
    private boolean publishCameraTrack = true;
    private boolean publishIsGoing = false;
    private boolean publishScreenTrack = false;
    private Map<String, RemoteParticipant> remoteParticipants = new ConcurrentHashMap();
    private Map<String, RemoteParticipant> remotePublishParticipants = new ConcurrentHashMap();
    private AliRtcEngine.AliRtcVideoProfile screenVideoProfile = AliRtcEngine.AliRtcVideoProfile.AliRTCSDK_Video_Profile_Default;
    private long sharedContext = 0;

    public boolean isAutoPublish() {
        return this.autoPublish;
    }

    public void setAutoPublish(boolean autoPublish2) {
        this.autoPublish = autoPublish2;
    }

    public boolean isAutoSubscribe() {
        return this.autoSubscribe;
    }

    public void setAutoSubscribe(boolean autoSubscribe2) {
        this.autoSubscribe = autoSubscribe2;
    }

    public boolean isAudioOnly() {
        return this.audioOnly;
    }

    public void setAudioOnly(boolean audioOnly2) {
        this.audioOnly = audioOnly2;
    }

    public boolean isInCall() {
        return this.isInCall;
    }

    public void setInCall(boolean inCall) {
        this.isInCall = inCall;
    }

    public boolean isHasLeftChannel() {
        return this.hasLeftChannel;
    }

    public void setHasLeftChannel(boolean hasLeftChannel2) {
        this.hasLeftChannel = hasLeftChannel2;
    }

    public boolean isCamFlash() {
        return this.camFlash;
    }

    public void setCamFlash(boolean camFlash2) {
        this.camFlash = camFlash2;
    }

    public float getCamZoom() {
        return this.camZoom;
    }

    public void setCamZoom(float camZoom2) {
        this.camZoom = camZoom2;
    }

    public boolean isCamAutoFocus() {
        return this.camAutoFocus;
    }

    public void setCamAutoFocus(boolean camAutoFocus2) {
        this.camAutoFocus = camAutoFocus2;
    }

    public boolean isCameraOn() {
        return this.isCameraOn;
    }

    public void setCameraOn(boolean cameraOn) {
        this.isCameraOn = cameraOn;
    }

    public int getCameraType() {
        return this.cameraType;
    }

    public void setCameraType(int cameraType2) {
        this.cameraType = cameraType2;
    }

    public boolean isSpeakerOn() {
        return this.isSpeakerOn;
    }

    public void setSpeakerOn(boolean speakerOn) {
        this.isSpeakerOn = speakerOn;
    }

    public String getLocalCallID() {
        return this.localCallID;
    }

    public void setLocalCallID(String localCallID2) {
        this.localCallID = localCallID2;
    }

    public void setLocalCallID(String localCallID2, boolean tmp) {
        setLocalCallID(localCallID2);
        if (tmp) {
            setTmpLocalCallID(localCallID2);
        }
    }

    public String getTmpLocalCallID() {
        return this.localCallID_tmp;
    }

    public void setTmpLocalCallID(String tmpLocalCallID) {
        this.localCallID_tmp = tmpLocalCallID;
    }

    public boolean isPublishCameraTrack() {
        return this.publishCameraTrack;
    }

    public void setPublishCameraTrack(boolean publishCameraTrack2) {
        this.publishCameraTrack = publishCameraTrack2;
    }

    public boolean isPublishScreenTrack() {
        return this.publishScreenTrack;
    }

    public void setPublishScreenTrack(boolean publishScreenTrack2) {
        this.publishScreenTrack = publishScreenTrack2;
    }

    public boolean isPublishAudio() {
        return this.publishAudio;
    }

    public void setPublishAudio(boolean publishAudio2) {
        this.publishAudio = publishAudio2;
    }

    public boolean isDualStream() {
        return this.dualStream;
    }

    public void setDualStream(boolean dualStream2) {
        this.dualStream = dualStream2;
    }

    public AliRtcEngine.AliRtcVideoProfile getCamVideoProfile() {
        return this.camVideoProfile;
    }

    public void setCamVideoProfile(AliRtcEngine.AliRtcVideoProfile camVideoProfile2) {
        this.camVideoProfile = camVideoProfile2;
    }

    public AliRtcEngine.AliRtcVideoProfile getScreenVideoProfile() {
        return this.screenVideoProfile;
    }

    public void setScreenVideoProfile(AliRtcEngine.AliRtcVideoProfile screenVideoProfile2) {
        this.screenVideoProfile = screenVideoProfile2;
    }

    public AliRtcEngine.AliVideoCanvas getLocalVideoCanvas() {
        return this.localVideoCanvas;
    }

    public void setLocalVideoCanvas(AliRtcEngine.AliVideoCanvas localVideoCanvas2) {
        this.localVideoCanvas = localVideoCanvas2;
    }

    public boolean isMuteLocalCameraVideo() {
        return this.muteLocalCameraVideo;
    }

    public void setMuteLocalCameraVideo(boolean muteLocalCameraVideo2) {
        this.muteLocalCameraVideo = muteLocalCameraVideo2;
    }

    public boolean isMuteLocalScreenVideo() {
        return this.muteLocalScreenVideo;
    }

    public void setMuteLocalScreenVideo(boolean muteLocalScreenVideo2) {
        this.muteLocalScreenVideo = muteLocalScreenVideo2;
    }

    public boolean isMuteLocalMic() {
        return this.muteLocalMic;
    }

    public void setMuteLocalMic(boolean muteLocalMic2) {
        this.muteLocalMic = muteLocalMic2;
    }

    public boolean isPublishIsGoing() {
        return this.publishIsGoing;
    }

    public void setPublishIsGoing(boolean publishIsGoing2) {
        this.publishIsGoing = publishIsGoing2;
    }

    public Map<String, RemoteParticipant> getRemoteParticipants() {
        return this.remoteParticipants;
    }

    public void setRemoteParticipants(Map<String, RemoteParticipant> remoteParticipants2) {
        this.remoteParticipants = remoteParticipants2;
    }

    public Map<String, RemoteParticipant> getRemotePublishParticipants() {
        return this.remotePublishParticipants;
    }

    public void setRemotePublishParticipants(Map<String, RemoteParticipant> remotePublishParticipants2) {
        this.remotePublishParticipants = remotePublishParticipants2;
    }

    public AliRtcAuthInfo getCachedAuthorInfo() {
        return this.cachedAuthorInfo;
    }

    public void setCachedAuthorInfo(AliRtcAuthInfo cachedAuthorInfo2) {
        this.cachedAuthorInfo = cachedAuthorInfo2;
    }

    public String getCachedUserName() {
        return this.cachedUserName;
    }

    public void setCachedUserName(String cachedUserName2) {
        this.cachedUserName = cachedUserName2;
    }

    public boolean isKeepInChannel() {
        return this.keepInChannel;
    }

    public void setKeepInChannel(boolean keepInChannel2) {
        this.keepInChannel = keepInChannel2;
    }

    public boolean hasPublished() {
        String str = this.localCallID;
        return str != null && !"".equals(str);
    }

    public long getSharedContext() {
        return this.sharedContext;
    }

    public void setSharedContext(long sharedContext2) {
        this.sharedContext = sharedContext2;
    }
}
