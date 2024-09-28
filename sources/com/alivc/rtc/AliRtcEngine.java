package com.alivc.rtc;

import android.content.Context;
import com.serenegiant.usb.UVCCamera;
import im.bclpbkiauv.ui.utils.translate.common.AudioEditConstant;
import java.lang.ref.WeakReference;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;
import org.webrtc.alirtcInterface.CollectStatusListener;
import org.webrtc.alirtcInterface.SophonEngine;
import org.webrtc.sdk.SophonSurfaceView;
import org.webrtc.utils.AlivcLog;

public abstract class AliRtcEngine {
    protected static int EnableH5Compatible = 0;
    private static final int MAX_REMOTE_USER_NUMBER = 96;
    private static final String TAG = "AliRtcEngine";
    private static WeakReference<Context> mContext;
    private static AliRtcEngineImpl mInstance;

    public enum AliRtcOrientationMode {
        AliRtcOrientationModePortrait,
        AliRtcOrientationModeLandscapeLeft,
        AliRtcOrientationModeLandscapeRight,
        AliRtcOrientationModeAuto
    }

    public static class AliRtcRemoteTextureInfo {
        public AliVideoCanvas aliVideoCanvas;
        public String userId;
        public int videoTrack;
    }

    public enum AliRtcRenderMode {
        AliRtcRenderModeAuto,
        AliRtcRenderModeStretch,
        AliRtcRenderModeFill,
        AliRtcRenderModeClip
    }

    public static class AliRtcTextureInfo {
        public AliRtcRenderMirrorMode mirrorMode = AliRtcRenderMirrorMode.AliRtcRenderMirrorModeOnlyFront;
        public int textureId;
    }

    public abstract void RegisterAudioObserver(ALI_RTC_INTERFACE.AliAudioType aliAudioType, ALI_RTC_INTERFACE.AliAudioObserver aliAudioObserver);

    public abstract void RegisterPreprocessVideoObserver(ALI_RTC_INTERFACE.AliDetectObserver aliDetectObserver);

    public abstract void RegisterRGBAObserver(String str, ALI_RTC_INTERFACE.AliRenderDataObserver aliRenderDataObserver);

    public abstract void RegisterTexturePostObserver(String str, ALI_RTC_INTERFACE.AliTextureObserver aliTextureObserver);

    public abstract void RegisterTexturePreObserver(String str, ALI_RTC_INTERFACE.AliTextureObserver aliTextureObserver);

    public abstract void RegisterVideoSampleObserver(ALI_RTC_INTERFACE.AliVideoObserver aliVideoObserver);

    public abstract void UnRegisterAudioObserver(ALI_RTC_INTERFACE.AliAudioType aliAudioType);

    public abstract void UnRegisterPreprocessVideoObserver();

    public abstract void UnRegisterRGBAObserver(String str);

    public abstract void UnRegisterTexturePostObserver(String str);

    public abstract void UnRegisterTexturePreObserver(String str);

    public abstract void UnRegisterVideoSampleObserver();

    public abstract void configLocalAudioPublish(boolean z);

    public abstract void configLocalCameraPublish(boolean z);

    public abstract void configLocalScreenPublish(boolean z);

    public abstract int configLocalSimulcast(boolean z, AliRtcVideoTrack aliRtcVideoTrack);

    public abstract void configRemoteAudio(String str, boolean z);

    public abstract void configRemoteCameraTrack(String str, boolean z, boolean z2);

    public abstract void configRemoteScreenTrack(String str, boolean z);

    public abstract void destroy();

    public abstract void enableBackgroundAudioRecording(boolean z);

    public abstract int enableEarBack(boolean z);

    public abstract int enableHighDefinitionPreview(boolean z);

    public abstract int enableSpeakerphone(boolean z);

    public abstract int generateTexture();

    public abstract int getAudioAccompanyPlayoutVolume();

    public abstract int getAudioAccompanyPublishVolume();

    public abstract int getAudioEffectPlayoutVolume(int i);

    public abstract int getAudioEffectPublishVolume(int i);

    public abstract float getCameraZoom();

    public abstract AliRTCCameraType getCurrentCameraType();

    public abstract String getMediaInfoWithUserId(String str, AliRtcVideoTrack aliRtcVideoTrack, String[] strArr);

    public abstract String[] getOnlineRemoteUsers();

    public abstract int getPreCameraType();

    public abstract String getSdkVersion();

    public abstract AliRtcUsbDeviceEvent getUsbDeviceEvent();

    public abstract AliRtcRemoteUserInfo getUserInfo(String str);

    public abstract AliRtcVideoProfile getVideoProfile(AliRtcVideoTrack aliRtcVideoTrack);

    public abstract boolean isAudioOnly();

    public abstract boolean isAutoPublish();

    public abstract boolean isAutoSubscribe();

    public abstract boolean isCameraAutoFocus();

    public abstract boolean isCameraFlash();

    public abstract boolean isCameraOn();

    public abstract boolean isCameraSupportExposurePoint();

    public abstract boolean isCameraSupportFocusPoint();

    public abstract boolean isEnableBackgroundAudioRecording();

    public abstract boolean isInCall();

    public abstract boolean isLocalAudioPublishEnabled();

    public abstract boolean isLocalCameraPublishEnabled();

    public abstract boolean isLocalScreenPublishEnabled();

    public abstract boolean isLocalSimulcastEnabled();

    public abstract boolean isSpeakerOn();

    public abstract boolean isUsbDeviceDetected();

    public abstract boolean isUserOnline(String str);

    public abstract void joinChannel(AliRtcAuthInfo aliRtcAuthInfo, String str);

    public abstract void leaveChannel();

    public abstract int muteLocalCamera(boolean z, AliRtcVideoTrack aliRtcVideoTrack);

    public abstract int muteLocalMic(boolean z);

    public abstract int muteRemoteAudioPlaying(String str, boolean z);

    public abstract int pauseAudioAccompany();

    public abstract int pauseAudioEffect(int i);

    public abstract int playAudioEffect(int i, String str, int i2, boolean z);

    public abstract int preloadAudioEffect(int i, String str);

    public abstract void publish();

    public abstract ALI_RTC_INTERFACE.VideoRawDataInterface registerVideoRawDataInterface(ALI_RTC_INTERFACE.AliRawDataStreamType aliRawDataStreamType);

    public abstract void removeTexture(int i);

    public abstract int resumeAudioAccompany();

    public abstract int resumeAudioEffect(int i);

    public abstract int setAudioAccompanyPlayoutVolume(int i);

    public abstract int setAudioAccompanyPublishVolume(int i);

    public abstract int setAudioAccompanyVolume(int i);

    public abstract int setAudioEffectPlayoutVolume(int i, int i2);

    public abstract int setAudioEffectPublishVolume(int i, int i2);

    public abstract int setAudioOnlyMode(boolean z);

    public abstract int setAutoPublish(boolean z, boolean z2);

    public abstract int setCameraExposurePoint(float f, float f2);

    public abstract int setCameraFocusPoint(float f, float f2);

    public abstract int setCameraZoom(float f, boolean z, boolean z2);

    public abstract int setChannelProfile(ALI_RTC_INTERFACE.AliRTCSDK_Channel_Profile aliRTCSDK_Channel_Profile);

    public abstract int setClientRole(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_Client_Role);

    public abstract void setCollectStatusListener(CollectStatusListener collectStatusListener);

    public abstract void setDeviceOrientationMode(AliRtcOrientationMode aliRtcOrientationMode);

    public abstract int setEarBackVolume(int i);

    public abstract int setLocalViewConfig(AliVideoCanvas aliVideoCanvas, AliRtcVideoTrack aliRtcVideoTrack);

    public abstract void setLogLevel(AliRtcLogLevel aliRtcLogLevel);

    public abstract int setPlayoutVolume(int i);

    public abstract void setPreCameraType(int i);

    public abstract int setRecordingVolume(int i);

    public abstract int setRemoteViewConfig(AliVideoCanvas aliVideoCanvas, String str, AliRtcVideoTrack aliRtcVideoTrack);

    public abstract void setRenderSharedContext(long j);

    public abstract void setRtcEngineEventListener(AliRtcEngineEventListener aliRtcEngineEventListener);

    public abstract void setRtcEngineNotify(AliRtcEngineNotify aliRtcEngineNotify);

    public abstract int setTexture(AliRtcTextureInfo aliRtcTextureInfo, int i, String str);

    public abstract void setTraceId(String str);

    public abstract void setUsbDeviceEvent(AliRtcUsbDeviceEvent aliRtcUsbDeviceEvent);

    public abstract void setVideoProfile(AliRtcVideoProfile aliRtcVideoProfile, AliRtcVideoTrack aliRtcVideoTrack);

    public abstract int startAudioAccompany(String str, boolean z, boolean z2, int i);

    public abstract int startAudioCapture();

    public abstract int startAudioFileRecording(String str, AliRtcAudioSampleRate aliRtcAudioSampleRate, AliRtcAudioCodecQualityType aliRtcAudioCodecQualityType);

    public abstract int startAudioPlayer();

    public abstract int startPreview();

    public abstract int stopAudioAccompany();

    public abstract int stopAudioCapture();

    public abstract int stopAudioEffect(int i);

    public abstract int stopAudioFileRecording();

    public abstract int stopAudioPlayer();

    public abstract int stopPreview();

    public abstract int subscribe(String str);

    public abstract int switchCamera();

    public abstract void unRegisterVideoRawDataInterface(ALI_RTC_INTERFACE.AliRawDataStreamType aliRawDataStreamType);

    public abstract int unloadAudioEffect(int i);

    public abstract void uploadLog();

    public static AliRtcEngineImpl getInstance(Context context, String extras) {
        if (mInstance == null) {
            AlivcLog.i(TAG, "[API]getInstance:context:" + context.hashCode() + "&&extras" + extras);
            synchronized (AliRtcEngineImpl.class) {
                mContext = new WeakReference<>(context);
                if (mInstance == null) {
                    mInstance = new AliRtcEngineImpl((Context) mContext.get(), extras);
                }
            }
        }
        return mInstance;
    }

    public static AliRtcEngineImpl getInstance(Context context) {
        if (mInstance == null) {
            AlivcLog.i(TAG, "[API]getInstance:context:" + context.hashCode());
            synchronized (AliRtcEngineImpl.class) {
                mContext = new WeakReference<>(context);
                if (mInstance == null) {
                    mInstance = new AliRtcEngineImpl((Context) mContext.get());
                }
            }
        }
        return mInstance;
    }

    public static int setH5CompatibleMode(int enable) {
        StringBuilder sb = new StringBuilder();
        sb.append("[API][Result]setH5CompatibleMode:enable:");
        sb.append(enable);
        EnableH5Compatible = enable;
        sb.append(enable);
        AlivcLog.i(TAG, sb.toString());
        EnableH5Compatible = enable;
        return enable;
    }

    public static int getH5CompatibleMode() {
        int ret = SophonEngine.getH5CompatibleMode();
        AlivcLog.i(TAG, "[API] getH5CompatibleMode:" + ret);
        return ret;
    }

    public enum AliRtcVideoTrack {
        AliRtcVideoTrackNo(0),
        AliRtcVideoTrackCamera(1),
        AliRtcVideoTrackScreen(2),
        AliRtcVideoTrackBoth(3);
        
        private int videoTrack;

        private AliRtcVideoTrack(int videoTrack2) {
            this.videoTrack = videoTrack2;
        }

        public int getValue() {
            return this.videoTrack;
        }
    }

    public enum AliRtcAudioTrack {
        AliRtcAudioTrackNo(0),
        AliRtcAudioTrackMic(1);
        
        private int audioTrack;

        private AliRtcAudioTrack(int audioTrack2) {
            this.audioTrack = audioTrack2;
        }

        public int getValue() {
            return this.audioTrack;
        }
    }

    public enum AliRTCCameraType {
        AliRTCCameraBack(0),
        AliRTCCameraFront(1),
        AliRTCCameraUsb(2),
        AliRTCCameraInvalid(-1);
        
        private int cameraType;

        private AliRTCCameraType(int cameraType2) {
            this.cameraType = cameraType2;
        }

        public int getCameraType() {
            return this.cameraType;
        }
    }

    public enum AliRtcNetworkQuality {
        Network_Excellent(0),
        Network_Good(1),
        Network_Poor(2),
        Network_Bad(3),
        Network_VeryBad(4),
        Network_Disconnected(5),
        Network_Unknow(6);
        
        private int transport;

        private AliRtcNetworkQuality(int transport2) {
            this.transport = transport2;
        }

        public int getValue() {
            return this.transport;
        }
    }

    public enum AliRtcRenderMirrorMode {
        AliRtcRenderMirrorModeOnlyFront(0),
        AliRtcRenderMirrorModeAllEnabled(1),
        AliRtcRenderMirrorModeAllDisable(2);
        
        private int value;

        private AliRtcRenderMirrorMode(int value2) {
            this.value = value2;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum AliRtcLogLevel {
        AliRtcLogLevelDump(0),
        AliRtcLogLevelDebug(1),
        AliRtcLogLevelVerbose(2),
        AliRtcLogLevelInfo(3),
        AliRtcLogLevelWarn(4),
        AliRtcLogLevelError(5),
        AliRtcLogLevelFatal(6),
        AliRtcLogLevelNone(7);
        
        private int value;

        private AliRtcLogLevel(int value2) {
            this.value = value2;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum AliRtcAudioCodecQualityType {
        AliRTCSDK_Audio_Codec_Quality_low(0),
        AliRTCSDK_Audio_Codec_Quality_midium(1),
        AliRTCSDK_Audio_Codec_Quality_high(2);
        
        private int id_;

        private AliRtcAudioCodecQualityType(int id) {
            this.id_ = id;
        }

        public int getId() {
            return this.id_;
        }

        public String toString() {
            return "AliRtcAudioCodecQualityType{id=" + this.id_ + '}';
        }
    }

    public enum AliRtcAudioSampleRate {
        AliRtcAudioSampleRate_8000(8000),
        AliRtcAudioSampleRate_16000(AudioEditConstant.ExportSampleRate),
        AliRtcAudioSampleRate_32000(32000),
        AliRtcAudioSampleRate_44100(44100),
        AliRtcAudioSampleRate_48000(48000),
        AliRtcAudioSampleRate_Max(96000);
        
        private int id;

        private AliRtcAudioSampleRate(int id2) {
            this.id = id2;
        }

        /* access modifiers changed from: package-private */
        public int getId() {
            return this.id;
        }
    }

    public enum AliRtcVideoProfile {
        AliRTCSDK_Video_Profile_Default(0, UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, 15),
        AliRTCSDK_Video_Profile_180_320P_15(1, 320, 180, 15),
        AliRTCSDK_Video_Profile_180_320P_30(2, 320, 180, 30),
        AliRTCSDK_Video_Profile_360_640P_15(3, UVCCamera.DEFAULT_PREVIEW_WIDTH, 360, 15),
        AliRTCSDK_Video_Profile_360_640P_30(4, UVCCamera.DEFAULT_PREVIEW_WIDTH, 360, 30),
        AliRTCSDK_Video_Profile_720_1280P_15(5, 1280, 720, 15),
        AliRTCSDK_Video_Profile_720_1280P_30(6, 1280, 720, 30),
        AliRTCSDK_Video_Profile_480_640P_15(100, UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, 15),
        AliRTCSDK_Video_Profile_480_640P_30(101, UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, 30),
        AliRTCSDK_Video_Profile_Max(9);
        
        private int fps;
        private int height;
        private int id;
        private int width;

        private AliRtcVideoProfile(int id2) {
            this(r8, r9, id2, 0, 0, 0);
        }

        private AliRtcVideoProfile(int id2, int width2, int height2, int fps2) {
            this.id = id2;
            this.width = width2;
            this.height = height2;
            this.fps = fps2;
        }

        public int getId() {
            return this.id;
        }

        public int getFPS() {
            return this.fps;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public String toString() {
            return "AliRtcVideoProfile{id=" + this.id + ", fps=" + this.fps + ", width=" + this.width + ", height=" + this.height + '}';
        }
    }

    public enum AliRtcOnByeType {
        AliRtcOnByeChannelTerminated(2);
        
        private int mOnByeType;

        private AliRtcOnByeType(int type) {
            this.mOnByeType = type;
        }
    }

    public static class AliVideoCanvas {
        public boolean enableBeauty = false;
        boolean flip = false;
        public AliRtcRenderMirrorMode mirrorMode = AliRtcRenderMirrorMode.AliRtcRenderMirrorModeOnlyFront;
        public AliRtcRenderMode renderMode = AliRtcRenderMode.AliRtcRenderModeAuto;
        public long sharedContext = 0;
        public int textureHeight = 0;
        public int textureId = 0;
        public int textureWidth = 0;
        public SophonSurfaceView view;

        public String toString() {
            return "AliVideoCanvas{sharedContext=" + this.sharedContext + ", enableBeauty=" + this.enableBeauty + ", view=" + this.view + ", renderMode=" + this.renderMode + ", mirrorMode=" + this.mirrorMode + ", flip=" + this.flip + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof AliVideoCanvas)) {
                return false;
            }
            AliVideoCanvas canvas = (AliVideoCanvas) o;
            if (this.textureId == canvas.textureId && this.textureWidth == canvas.textureWidth && this.textureHeight == canvas.textureHeight && this.sharedContext == canvas.sharedContext && this.enableBeauty == canvas.enableBeauty && this.flip == canvas.flip && this.view.hashCode() == canvas.view.hashCode() && this.renderMode == canvas.renderMode && this.mirrorMode == canvas.mirrorMode) {
                return true;
            }
            return false;
        }
    }

    public static synchronized void release() {
        synchronized (AliRtcEngine.class) {
            if (mInstance != null) {
                mInstance = null;
                System.gc();
            }
        }
    }
}
