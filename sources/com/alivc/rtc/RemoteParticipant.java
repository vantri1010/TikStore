package com.alivc.rtc;

import android.text.TextUtils;
import com.alivc.rtc.AliRtcEngine;
import java.util.Arrays;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;

public class RemoteParticipant {
    public static final String FAKE_SUBED = "FAKE_SUBED";
    private String audioSubscribed;
    private String audioTrackLabel = "";
    private String callID;
    private AliRtcEngine.AliVideoCanvas cameraCanvas = null;
    private String displayName;
    private boolean firstSubscribe = true;
    private boolean muteAudioPlaying = false;
    private AliRtcEngine.AliVideoCanvas screenCanvas = null;
    private String sessionID;
    private String streamLabel = "";
    private boolean subscribeing = false;
    public boolean ucAudioSubed;
    public boolean ucScreenSubed;
    public boolean ucVideoSubed;
    public boolean ucVideoSubedMaster;
    private String userID;
    private String[] videoSubscribed = new String[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_MAX.getValue()];
    private String[] videoSubscribedCached = new String[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_MAX.getValue()];
    private String[] videoTrackLabels = new String[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_MAX.getValue()];

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID2) {
        this.userID = userID2;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(String sessionID2) {
        this.sessionID = sessionID2;
    }

    public String getCallID() {
        return this.callID;
    }

    public void setCallID(String callID2) {
        this.callID = callID2;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName2) {
        this.displayName = displayName2;
    }

    public String getStreamLabel() {
        return this.streamLabel;
    }

    public void setStreamLabel(String streamLabel2) {
        this.streamLabel = streamLabel2;
    }

    public String getAudioTrackLabel() {
        return this.audioTrackLabel;
    }

    public void setAudioTrackLabel(String audioTrackLabel2) {
        this.audioTrackLabel = audioTrackLabel2;
    }

    public void setVideoTrackLabels(String[] videoTrackLabels2) {
        this.videoTrackLabels = videoTrackLabels2;
    }

    public String[] getVideoTrackLabels() {
        return this.videoTrackLabels;
    }

    public boolean isHasAudioStream() {
        return !TextUtils.isEmpty(this.audioTrackLabel);
    }

    public boolean isHasVideoLargeStream() {
        return !TextUtils.isEmpty(this.videoTrackLabels[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge.ordinal()]);
    }

    public boolean isHasVideoSmallStream() {
        return !TextUtils.isEmpty(this.videoTrackLabels[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSmall.ordinal()]);
    }

    public boolean isHasScreenShareStream() {
        return !TextUtils.isEmpty(this.videoTrackLabels[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare.ordinal()]);
    }

    public boolean isHasVideoSuperStream() {
        return !TextUtils.isEmpty(this.videoTrackLabels[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSuper.ordinal()]);
    }

    public boolean isSubVideoLargeStream() {
        return !TextUtils.isEmpty(this.videoSubscribed[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge.ordinal()]);
    }

    public boolean isSubVideoSmallStream() {
        return !TextUtils.isEmpty(this.videoSubscribed[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSmall.ordinal()]);
    }

    public boolean isSubVideoSuperStream() {
        return !TextUtils.isEmpty(this.videoSubscribed[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSuper.ordinal()]);
    }

    public boolean isSubShcreenShareStream() {
        return !TextUtils.isEmpty(this.videoSubscribed[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare.ordinal()]);
    }

    public String[] getVideoSubscribed() {
        return this.videoSubscribed;
    }

    public void setVideoSubscribed(String[] videoSubscribed2) {
        this.videoSubscribed = videoSubscribed2;
    }

    public void setVideoSubscribedCached(String[] videoSubscribedCached2) {
        int count = videoSubscribedCached2.length;
        for (int i = 0; i < count; i++) {
            this.videoSubscribedCached[i] = videoSubscribedCached2[i];
        }
    }

    public String[] getVideoSubscribedCached() {
        return this.videoSubscribedCached;
    }

    public boolean isAudioSubscribed() {
        return !TextUtils.isEmpty(this.audioSubscribed);
    }

    public boolean isVideoSubscribed() {
        return isSubVideoLargeStream() || isSubVideoSmallStream() || isSubVideoSuperStream();
    }

    public void setAudioSubscribed(String audioSubscribed2) {
        this.audioSubscribed = audioSubscribed2;
    }

    public String getAudioSubscribed() {
        return this.audioSubscribed;
    }

    public boolean isFirstSubscribe() {
        return this.firstSubscribe;
    }

    public void setFirstSubscribe(boolean firstSubscribe2) {
        this.firstSubscribe = firstSubscribe2;
    }

    public boolean isSubscribeing() {
        return this.subscribeing;
    }

    public void setSubscribeing(boolean subscribeing2) {
        this.subscribeing = subscribeing2;
    }

    public AliRtcEngine.AliVideoCanvas getCameraCanvas() {
        return this.cameraCanvas;
    }

    public void setCameraCanvas(AliRtcEngine.AliVideoCanvas cameraCanvas2) {
        this.cameraCanvas = cameraCanvas2;
    }

    public AliRtcEngine.AliVideoCanvas getScreenCanvas() {
        return this.screenCanvas;
    }

    public void setScreenCanvas(AliRtcEngine.AliVideoCanvas screenCanvas2) {
        this.screenCanvas = screenCanvas2;
    }

    public boolean isMuteAudioPlaying() {
        return this.muteAudioPlaying;
    }

    public void setMuteAudioPlaying(boolean muteAudioPlaying2) {
        this.muteAudioPlaying = muteAudioPlaying2;
    }

    public boolean isOnline() {
        return !TextUtils.isEmpty(this.sessionID);
    }

    public static AliRtcEngine.AliRtcAudioTrack getAudioTrack(String audioTrackLabel2) {
        return TextUtils.isEmpty(audioTrackLabel2) ? AliRtcEngine.AliRtcAudioTrack.AliRtcAudioTrackNo : AliRtcEngine.AliRtcAudioTrack.AliRtcAudioTrackMic;
    }

    public static AliRtcEngine.AliRtcVideoTrack getVideoTrack(String[] videoTrackLabels2) {
        AliRtcEngine.AliRtcVideoTrack ret = AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo;
        int bigIndex = ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge.ordinal();
        int smallIndex = ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSmall.ordinal();
        int screenIndex = ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare.ordinal();
        int superIndex = ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSuper.ordinal();
        boolean hasCamera = false;
        boolean hasScreen = false;
        if (!TextUtils.isEmpty(videoTrackLabels2[bigIndex]) || !TextUtils.isEmpty(videoTrackLabels2[smallIndex]) || !TextUtils.isEmpty(videoTrackLabels2[superIndex])) {
            hasCamera = true;
        }
        if (!TextUtils.isEmpty(videoTrackLabels2[screenIndex])) {
            hasScreen = true;
        }
        if (hasCamera && hasScreen) {
            return AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth;
        }
        if (hasCamera) {
            return AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera;
        }
        if (hasScreen) {
            return AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen;
        }
        return ret;
    }

    public AliRtcEngine.AliVideoCanvas getVideoCanvas(int videoTrackIndex) {
        if (videoTrackIndex == ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge.ordinal() || videoTrackIndex == ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSmall.ordinal() || videoTrackIndex == ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSuper.ordinal()) {
            return this.cameraCanvas;
        }
        if (videoTrackIndex == ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare.ordinal()) {
            return this.screenCanvas;
        }
        return null;
    }

    /* renamed from: com.alivc.rtc.RemoteParticipant$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack;

        static {
            int[] iArr = new int[AliRtcEngine.AliRtcVideoTrack.values().length];
            $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack = iArr;
            try {
                iArr[AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack[AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public void setVideoCanvas(AliRtcEngine.AliRtcVideoTrack track, AliRtcEngine.AliVideoCanvas canvas) {
        int i = AnonymousClass1.$SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack[track.ordinal()];
        if (i == 1) {
            this.cameraCanvas = canvas;
        } else if (i == 2) {
            this.screenCanvas = canvas;
        }
    }

    public int getSubVideoSurceIndex(AliRtcEngine.AliRtcVideoTrack track) {
        int i = AnonymousClass1.$SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack[track.ordinal()];
        if (i == 1) {
            int count = this.videoSubscribed.length;
            for (int i2 = 0; i2 < count; i2++) {
                if (i2 != ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare.ordinal() && !TextUtils.isEmpty(this.videoSubscribed[i2])) {
                    return i2;
                }
            }
            return -1;
        } else if (i != 2) {
            return -1;
        } else {
            return ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare.ordinal();
        }
    }

    public String[] getAvailableVideoSubed() {
        int count = this.videoSubscribed.length;
        for (int i = 0; i < count; i++) {
            if (!TextUtils.isEmpty(this.videoSubscribed[i]) && TextUtils.isEmpty(this.videoTrackLabels[i])) {
                this.videoSubscribed[i] = "";
            }
        }
        return this.videoSubscribed;
    }

    public String getAvailableAudioSubed() {
        if (!this.ucAudioSubed || TextUtils.isEmpty(this.audioTrackLabel)) {
            this.audioSubscribed = "";
        } else {
            this.audioSubscribed = this.audioTrackLabel;
        }
        return this.audioSubscribed;
    }

    public void clearAll() {
        clearStreams();
        this.ucVideoSubed = false;
        this.ucVideoSubedMaster = false;
        this.ucAudioSubed = false;
        this.ucScreenSubed = false;
        clearSubedStatus();
    }

    public void clearStreams() {
        Arrays.fill(this.videoTrackLabels, "");
        this.audioTrackLabel = "";
    }

    public void clearSubedStatus() {
        Arrays.fill(this.videoSubscribed, "");
        Arrays.fill(this.videoSubscribedCached, "");
        this.audioSubscribed = "";
        this.firstSubscribe = true;
    }

    public boolean isUcVideoSubed() {
        return this.ucVideoSubed;
    }

    public void setUcVideoSubed(boolean sub) {
        this.ucVideoSubed = sub;
    }

    public boolean isUcVideoSubedMaster() {
        return this.ucVideoSubedMaster;
    }

    public void setUcVideoSubedMaster(boolean master) {
        this.ucVideoSubedMaster = master;
    }

    public boolean isUcAudeoSubed() {
        return this.ucAudioSubed;
    }

    public void setUcAudeoSubed(boolean sub) {
        this.ucAudioSubed = sub;
    }

    public boolean isUcScreenSubed() {
        return this.ucScreenSubed;
    }

    public void setUcScreenSubed(boolean sub) {
        this.ucScreenSubed = sub;
    }
}
