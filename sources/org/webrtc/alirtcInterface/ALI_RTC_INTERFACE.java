package org.webrtc.alirtcInterface;

import android.content.Context;
import android.os.Build;
import android.view.Surface;

public abstract class ALI_RTC_INTERFACE {
    public static final String CAMERA_STRING = "sophon_video_camera_large";
    public static final String ENGINE_BASIC_QUALITY_MODE = "ENGINE_BASIC_QUALITY_MODE";
    public static final String ENGINE_HIGH_QUALITY_MODE = "ENGINE_HIGH_QUALITY_MODE";
    public static final String ENGINE_LOW_QUALITY_MODE = "ENGINE_LOW_QUALITY_MODE";
    public static final String ENGINE_STANDARD_QUALITY_MODE = "ENGINE_STANDARD_QUALITY_MODE";
    public static final String SCENE_DEFAULT_MODE = "SCENE_DEFAULT_MODE";
    public static final String SCENE_EDUCATION_MODE = "SCENE_EDUCATION_MODE";
    public static final String SCENE_MEDIA_MODE = "SCENE_MEDIA_MODE";
    public static final String SCENE_MUSIC_MODE = "SCENE_MUSIC_MODE";
    public static final String SCREEN_STRING = "sophon_video_screen_share";
    public static final String SMALL_STRING = "sophon_video_camera_small";
    public static final String SUPER_STRING = "sophon_video_camera_super";
    public static final int Video_Profile_160_120 = 0;
    public static final int Video_Profile_240_180 = 1;
    public static final int Video_Profile_320_180 = 2;
    public static final int Video_Profile_640_360 = 3;
    public static final int ideo_Profile_1280_7200 = 4;

    enum ALI_RTC_ERROR_CODE {
        RTC_ERROR_NONE,
        RTC_ERROR_NORMAL
    }

    public interface AliAudioObserver {
        void onCaptureData(long j, int i, int i2, int i3, int i4, int i5);

        void onCaptureRawData(long j, int i, int i2, int i3, int i4, int i5);

        void onCaptureVolumeData(String str, int i);

        void onRenderData(long j, int i, int i2, int i3, int i4, int i5);
    }

    public enum AliAudioType {
        PUB_OBSERVER,
        SUB_OBSERVER,
        RAW_DATA_OBSERVER,
        VOLUME_DATA_OBSERVER
    }

    public interface AliDetectObserver {
        long onData(long j, long j2, long j3, AliRTCImageFormat aliRTCImageFormat, int i, int i2, int i3, int i4, int i5, int i6, long j4);
    }

    public enum AliDisplayMode {
        AliRTCSdk_Auto_Mode,
        AliRTCSdk_FullOf_Mode,
        AliRTCSdk_FillBlcak_Mode,
        AliRTCSdk_Scale_Mode
    }

    public static class AliPublishConfig {
        public boolean audio_track = true;
        public int[] video_track_profile = new int[AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_MAX.getValue()];
        public boolean[] video_tracks = new boolean[AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_MAX.getValue()];
    }

    public static class AliRawDataConfig {
        public MediaStatesVideoFormat format;
    }

    public static class AliRawDataFrame {
        public MediaStatesVideoFormat format = MediaStatesVideoFormat.UnKnown;
        public byte[] frame;
        public int height;
        public int[] lineSize = new int[4];
        public int rotation;
        public int video_frame_length;
        public int width;
    }

    public enum AliRawDataStreamType {
        AliRTCSdk_Streame_Type_Capture,
        AliRTCSdk_Streame_Type_Screen
    }

    public interface AliRenderDataObserver {
        void onRenderData(String str, long j, int i, int i2, int i3, int i4, int i5, long j2);
    }

    public static class AliRendererConfig {
        public int api_level = Build.VERSION.SDK_INT;
        public int display_mode = AliDisplayMode.AliRTCSdk_Auto_Mode.ordinal();
        public Surface display_view = null;
        public boolean enableBeauty = false;
        public boolean flip = false;
        public int height;
        public int render_id;
        public long sharedContext = 0;
        public int textureHeight = 0;
        public int textureId = 0;
        public int textureWidth = 0;
        public int width;
    }

    public static class AliSubscribeConfig {
        public String audio_track_label;
        public String stream_label;
        public String[] video_track_labels = new String[AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_MAX.getValue()];
    }

    public interface AliTextureObserver {
        int onTexture(String str, int i, int i2, int i3, int i4, int i5, long j);

        void onTextureCreate(String str, long j);

        void onTextureDestroy(String str);
    }

    public interface AliVideoObserver {
        void onLocalVideoSample(AliVideoSourceType aliVideoSourceType, AliVideoSample aliVideoSample);

        void onRemoteVideoSample(String str, AliVideoSourceType aliVideoSourceType, AliVideoSample aliVideoSample);
    }

    public static class AliVideoSample {
        public long dataFrameU = 0;
        public long dataFrameV = 0;
        public long dataFrameY = 0;
        public long extraData = 0;
        public AliRTCImageFormat format = AliRTCImageFormat.GetAliRTCImageFormat(-1);
        public int height = 0;
        public int rotate = 0;
        public int strideU = 0;
        public int strideV = 0;
        public int strideY = 0;
        public int width = 0;
    }

    public enum AliVideoSourceType {
        AliRTCSdk_Videosource_Camera_Large_Type,
        AliRTCSdk_Videosource_Camera_Small_Type,
        AliRTCSdk_Videosource_ScreenShare_Type
    }

    public static class AuthInfo {
        public String[] agent;
        public String appid;
        public String channel;
        public String[] gslb;
        public String nonce;
        public String session;
        public long timestamp;
        public String token;
        public String user_id;
    }

    public enum VideoProfile {
        Video_Profile_160_120,
        Video_Profile_240_180,
        Video_Profile_320_180,
        Video_Profile_640_360,
        ideo_Profile_1280_720
    }

    public interface VideoRawDataConsumer {
        int consume(AliRawDataFrame aliRawDataFrame, long j);
    }

    public abstract void AddLocalDisplayWindow(AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, AliRendererConfig aliRendererConfig);

    public abstract void AddRemoteDisplayWindow(String str, AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, AliRendererConfig aliRendererConfig);

    public abstract void ChangeLogLevel(AliRTCSDKLogLevel aliRTCSDKLogLevel);

    public abstract void CloseCamera();

    public abstract long Create(String str, AliSophonEngine aliSophonEngine);

    public abstract void Destory();

    public abstract int EnableEarBack(boolean z);

    public abstract void EnableLocalAudio(boolean z);

    public abstract void EnableLocalVideo(AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, boolean z);

    public abstract void EnableRemoteAudio(String str, boolean z);

    public abstract void EnableRemoteVideo(String str, AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, boolean z);

    public abstract void EnableUpload(boolean z);

    public abstract String[] EnumerateAllCaptureDevices();

    public abstract int GetAudioAccompanyPlayoutVolume();

    public abstract int GetAudioAccompanyPublishVolume();

    public abstract int GetAudioAccompanyVolume();

    public abstract int GetAudioEffectPlayoutVolume(int i);

    public abstract int GetAudioEffectPublishVolume(int i);

    public abstract AliCaptureType GetCaptureType();

    public abstract int GetLogLevel();

    public abstract String GetSDKVersion();

    public abstract TransportStatus GetTransportStatus(String str, TransportType transportType);

    public abstract int Gslb(AuthInfo authInfo);

    public abstract int JoinChannel(String str);

    public abstract int JoinChannel(AuthInfo authInfo, String str);

    public abstract int LeaveChannel();

    public abstract int LeaveChannel(long j);

    public abstract void Log(String str, int i, AliRTCSDKLogLevel aliRTCSDKLogLevel, String str2, String str3);

    public abstract void LogDestroy();

    public abstract void OpenCamera(AliCameraConfig aliCameraConfig);

    public abstract int PauseAudioEffect(int i);

    public abstract int PauseAudioMixing();

    public abstract void PauseRender();

    public abstract int PlayAudioEffect(int i, String str, int i2, boolean z);

    public abstract int PreloadAudioEffect(int i, String str);

    public abstract void Publish(AliPublishConfig aliPublishConfig);

    public abstract void RegisterAudioObserver(AliAudioType aliAudioType, AliAudioObserver aliAudioObserver);

    public abstract void RegisterPreprocessVideoObserver(AliDetectObserver aliDetectObserver);

    public abstract void RegisterRGBAObserver(String str, AliRenderDataObserver aliRenderDataObserver);

    public abstract void RegisterTexturePostObserver(String str, AliTextureObserver aliTextureObserver);

    public abstract void RegisterTexturePreObserver(String str, AliTextureObserver aliTextureObserver);

    public abstract void RegisterVideoObserver(AliVideoObserver aliVideoObserver);

    public abstract void RegisterYUVObserver(String str, AliVideoObserver aliVideoObserver);

    public abstract void RemoveLocalDisplayWindow(AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type);

    public abstract void RemoveRemoteDisplayWindow(String str, AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type);

    public abstract void Republish(AliPublishConfig aliPublishConfig);

    public abstract int RespondMessageNotification(String str, String str2, String str3);

    public abstract void Resubscribe(String str, AliSubscribeConfig aliSubscribeConfig);

    public abstract int ResumeAudioEffect(int i);

    public abstract int ResumeAudioMixing();

    public abstract void ResumeRender();

    public abstract int SetAudioAccompanyPlayoutVolume(int i);

    public abstract int SetAudioAccompanyPublishVolume(int i);

    public abstract int SetAudioAccompanyVolume(int i);

    public abstract int SetAudioEffectPlayoutVolume(int i, int i2);

    public abstract int SetAudioEffectPublishVolume(int i, int i2);

    public abstract int SetCameraZoom(float f);

    public abstract int SetCaptureDeviceByName(String str);

    public abstract int SetChannelProfile(AliRTCSDK_Channel_Profile aliRTCSDK_Channel_Profile);

    public abstract int SetClientRole(AliRTCSDK_Client_Role aliRTCSDK_Client_Role);

    public abstract void SetContext(Context context);

    public abstract int SetEarBackVolume(int i);

    public abstract int SetFlash(boolean z);

    public abstract void SetSpeakerStatus(boolean z);

    public abstract void SetUploadAppID(String str);

    public abstract void SetUploadSessionID(String str);

    public abstract int StartAudioAccompany(String str, boolean z, boolean z2, int i);

    public abstract int StartAudioFileRecording(String str, int i, int i2);

    public abstract int StopAudioAccompany();

    public abstract int StopAudioEffect(int i);

    public abstract int StopAudioFileRecording();

    public abstract void Subscribe(String str, AliSubscribeConfig aliSubscribeConfig);

    public abstract int SwitchCramer();

    public abstract void UnRegisterAudioObserver(AliAudioType aliAudioType);

    public abstract void UnRegisterPreprocessVideoObserver();

    public abstract void UnRegisterRGBAObserver(String str);

    public abstract void UnRegisterTexturePostObserver(String str);

    public abstract void UnRegisterTexturePreObserver(String str);

    public abstract void UnRegisterVideoObserver();

    public abstract void UnRegisterYUVObserver(String str);

    public abstract int UnloadAudioEffect(int i);

    public abstract void Unpublish();

    public abstract void Unsubscribe(String str);

    public abstract void UpdateDisplayWindow(AliRendererConfig aliRendererConfig);

    public abstract int UplinkChannelMessage(String str, String str2);

    public abstract void UploadChannelLog();

    public abstract void UploadLog();

    public abstract void applicationMicInterrupt();

    public abstract void applicationMicInterruptResume();

    public abstract void applicationWillBecomeActive();

    public abstract void applicationWillResignActive();

    public abstract void enableBackgroundAudioRecording(boolean z);

    public abstract int enableHighDefinitionPreview(boolean z);

    public abstract int generateTexture();

    public abstract String getMediaInfo(String str, String str2, String[] strArr);

    public abstract boolean isCameraSupportExposurePoint();

    public abstract boolean isCameraSupportFocusPoint();

    public abstract boolean isEnableBackgroundAudioRecording();

    public abstract VideoRawDataInterface registerVideoRawDataInterface(AliRawDataStreamType aliRawDataStreamType);

    public abstract int setCameraExposurePoint(float f, float f2);

    public abstract int setCameraFocusPoint(float f, float f2);

    public abstract void setCollectStatusListener(CollectStatusListener collectStatusListener);

    public abstract void setDeviceOrientationMode(Ali_RTC_Device_Orientation_Mode ali_RTC_Device_Orientation_Mode);

    public abstract int setPlayoutVolume(int i);

    public abstract int setRecordingVolume(int i);

    public abstract void setTraceId(String str);

    public abstract int startAudioCapture();

    public abstract int startAudioPlayer();

    public abstract int stopAudioCapture();

    public abstract int stopAudioPlayer();

    public abstract void unRegisterVideoRawDataInterface(AliRawDataStreamType aliRawDataStreamType);

    public enum AliRTCSdk_VideSource_Type {
        AliRTCSDK_VideoSource_Type_CameraLarge(0),
        AliRTCSDK_VideoSource_Type_CameraSmall(1),
        AliRTCSDK_VideoSource_Type_ScreenShare(2),
        AliRTCSDK_VideoSource_Type_CameraSuper(3),
        AliRTCSDK_VideoSource_Type_MAX(4);
        
        private int videoSourceType;

        private AliRTCSdk_VideSource_Type(int videoSourceType2) {
            this.videoSourceType = videoSourceType2;
        }

        public int getValue() {
            return this.videoSourceType;
        }
    }

    public enum AliRTCImageFormat {
        ALIRTC_IMAGE_FORMAT_UNKNOWN(0),
        ALIRTC_IMAGE_FORMAT_I420(1),
        ALIRTC_IMAGE_FORMAT_IYUV(2),
        ALIRTC_IMAGE_FORMAT_RGB24(3),
        ALIRTC_IMAGE_FORMAT_ABGR(4),
        ALIRTC_IMAGE_FORMAT_ARGB(5),
        ALIRTC_IMAGE_FORMAT_ARGB4444(6),
        ALIRTC_IMAGE_FORMAT_RGB565(7),
        ALIRTC_IMAGE_FORMAT_ARGB1555(8),
        ALIRTC_IMAGE_FORMAT_YUY2(9),
        ALIRTC_IMAGE_FORMAT_YV12(10),
        ALIRTC_IMAGE_FORMAT_UYVY(11),
        ALIRTC_IMAGE_FORMAT_MJPEG(12),
        ALIRTC_IMAGE_FORMAT_NV21(13),
        ALIRTC_IMAGE_FORMAT_NV12(14),
        ALIRTC_IMAGE_FORMAT_BGRA(15);
        
        private int imageFormat;

        private AliRTCImageFormat(int imageFormat2) {
            this.imageFormat = imageFormat2;
        }

        public int getValue() {
            return this.imageFormat;
        }

        public static AliRTCImageFormat GetAliRTCImageFormat(int format) {
            for (AliRTCImageFormat imageFormat2 : values()) {
                if (imageFormat2.getValue() == format) {
                    return imageFormat2;
                }
            }
            return ALIRTC_IMAGE_FORMAT_UNKNOWN;
        }
    }

    public enum AliRTCSDKLogLevel {
        AliRTCSDK_LOG_DUMP(0),
        AliRTCSDK_LOG_DEBUG(1),
        AliRTCSDK_LOG_VERBOSE(2),
        AliRTCSDK_LOG_INFO(3),
        AliRTCSDK_LOG_WARNING(4),
        AliRTCSDK_LOG_ERROR(5),
        AliRTCSDK_LOG_FATAL(6),
        AliRTCSDK_LOG_NONE(7);
        
        private int logLevel;

        private AliRTCSDKLogLevel(int logLevel2) {
            this.logLevel = logLevel2;
        }

        public int getValue() {
            return this.logLevel;
        }
    }

    public enum TransportStatus {
        Network_Excellent(0),
        Network_Good(1),
        Network_Poor(2),
        Network_Bad(3),
        Network_VeryBad(4),
        Network_Disconnected(5),
        Network_Unknow(6);
        
        private int transport;

        private TransportStatus(int transport2) {
            this.transport = transport2;
        }

        public int getValue() {
            return this.transport;
        }

        public static TransportStatus fromNativeIndex(int i) {
            try {
                return values()[i];
            } catch (Exception e) {
                return Network_Unknow;
            }
        }
    }

    public enum Ali_RTC_Device_Orientation_Mode {
        Ali_RTC_Device_Orientation_0(0),
        Ali_RTC_Device_Orientation_90(90),
        Ali_RTC_Device_Orientation_270(270),
        Ali_RTC_Device_Orientation_Adaptive(1000);
        
        private int mode;

        private Ali_RTC_Device_Orientation_Mode(int mode2) {
            this.mode = mode2;
        }

        public int getValue() {
            return this.mode;
        }
    }

    public enum TransportType {
        Transport_Video_TYPE(0),
        Transport_Audio_TYPE(1),
        Transport_Mix_TYPE(2);
        
        private int transportTyoe;

        private TransportType(int transportTyoe2) {
            this.transportTyoe = transportTyoe2;
        }

        public int getValue() {
            return this.transportTyoe;
        }

        public static TransportType fromNativeIndex(int i) {
            if (i == 0) {
                return Transport_Video_TYPE;
            }
            if (i == 1) {
                return Transport_Audio_TYPE;
            }
            if (i != 2) {
                return null;
            }
            return Transport_Mix_TYPE;
        }
    }

    public static class AliTransportInfo {
        public TransportStatus downQuality;
        int downQuality_idx;
        public TransportStatus upQuality;
        int upQuality_idx;
        public String user_id;

        /* access modifiers changed from: package-private */
        public void convertIntToEnum() {
            this.upQuality = TransportStatus.fromNativeIndex(this.upQuality_idx);
            this.downQuality = TransportStatus.fromNativeIndex(this.downQuality_idx);
        }
    }

    public enum AliRTCMediaConnectionReConnectState {
        AliRTC_MeidaConnection_ReConnect_Init(0),
        AliRTC_MeidaConnection_ReConnect_Connecting(1),
        AliRTC_MeidaConnection_ReConnect_Connected(2),
        AliRTC_MeidaConnection_ReConnect_Failed(3);
        
        private int state;

        private AliRTCMediaConnectionReConnectState(int state2) {
            this.state = state2;
        }

        public int getValue() {
            return this.state;
        }

        public static AliRTCMediaConnectionReConnectState fromNativeIndex(int i) {
            try {
                return values()[i];
            } catch (Exception e) {
                return null;
            }
        }
    }

    public enum AliCaptureType {
        SDK_Capture_Typ_Back(0),
        SDK_Capture_Typ_Front(1),
        SDK_Capture_Typ_Usb(2),
        SDK_Capture_Typ_Invalid(-1);
        
        private int captureType;

        private AliCaptureType(int captureType2) {
            this.captureType = captureType2;
        }

        public int getCaptureType() {
            return this.captureType;
        }
    }

    public static class AliCameraConfig {
        public boolean autoFocus = true;
        public Context context = null;
        public boolean flash = false;
        boolean highDefPreview = true;
        public int preferFps;
        public int preferHeight;
        public int preferWidth;
        public boolean restart = false;
        public long sharedContext = 0;
        public int video_source = 1;

        public String toString() {
            return "AliCameraConfig{video_source=" + this.video_source + ", autoFocus=" + this.autoFocus + ", flash=" + this.flash + ", restart=" + this.restart + ", sharedContext=" + this.sharedContext + ", context=" + this.context + ", preferWidth=" + this.preferWidth + ", preferHeight=" + this.preferHeight + ", preferFps=" + this.preferFps + '}';
        }
    }

    public static class AliRtcStats {
        float cpu_usage;
        long rcvd_bytes;
        long rcvd_kbitrate;
        long sent_bytes;
        long sent_kbitrate;
        float system_cpu_usage;

        public String toString() {
            return "AliRtcStats{sent_kbitrate=" + this.sent_kbitrate + ", rcvd_kbitrate=" + this.rcvd_kbitrate + ", sent_bytes=" + this.sent_bytes + ", rcvd_bytes=" + this.rcvd_bytes + ", cpu_usage=" + this.cpu_usage + '}';
        }

        public void setCpu_usage(float cpu_usage2) {
            this.cpu_usage = cpu_usage2;
        }

        public void setSystem_cpu_usage(float system_cpu_usage2) {
            this.system_cpu_usage = system_cpu_usage2;
        }
    }

    class AliRTCStreamConfig {
        public String stream_id;
        public String track_id;

        AliRTCStreamConfig() {
        }
    }

    public enum MediaStatesVideoFormat {
        UnKnown,
        I420,
        IYUV,
        RGB24,
        ABGR,
        ARGB,
        ARGB4444,
        RGB565,
        ARGB1555,
        YUY2,
        YV12,
        UYVY,
        NV21,
        NV12,
        BGRA;

        public static MediaStatesVideoFormat fromNativeIndex(int index) {
            if (index < 0 || index >= values().length) {
                return null;
            }
            return values()[index];
        }

        public int value() {
            return ordinal();
        }
    }

    public static abstract class VideoRawDataInterface {
        private long mNativePtr;

        public abstract int deliverFrame(AliRawDataFrame aliRawDataFrame, long j);

        /* access modifiers changed from: package-private */
        public void setNativePtr(long nativePtr) {
            this.mNativePtr = nativePtr;
        }

        /* access modifiers changed from: package-private */
        public long getNativePtr() {
            return this.mNativePtr;
        }
    }

    public enum AliRTCSDK_Channel_Profile {
        AliRTCSDK_Communication(0),
        AliRTCSDK_Interactive_live(1);
        
        int val;

        private AliRTCSDK_Channel_Profile(int val2) {
            this.val = val2;
        }

        public int getValue() {
            return this.val;
        }

        public static AliRTCSDK_Channel_Profile fromNativeIndex(int index) {
            try {
                return values()[index];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public enum AliRTCSDK_Client_Role {
        AliRTCSDK_Interactive(0),
        AliRTCSDK_live(1);
        
        int val;

        private AliRTCSDK_Client_Role(int val2) {
            this.val = val2;
        }

        public int getValue() {
            return this.val;
        }

        public static AliRTCSDK_Client_Role fromNativeIndex(int index) {
            try {
                return values()[index];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
