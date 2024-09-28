package org.webrtc.alirtcInterface;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceView;
import org.webrtc.ali.USBMediaDevice;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;
import org.webrtc.audio.AppRTCAudioManager;
import org.webrtc.utils.AlivcLog;

public abstract class SophonEngine {
    private static SophonEngineImpl mInstance = null;
    private static USBMediaDevice mUSBMediaDevice = null;
    private static Handler mhandler;

    public abstract int EnableEarBack(boolean z);

    public abstract int GetAudioAccompanyPlayoutVolume();

    public abstract int GetAudioAccompanyPublishVolume();

    public abstract int GetAudioEffectPlayoutVolume(int i);

    public abstract int GetAudioEffectPublishVolume(int i);

    public abstract int PauseAudioEffect(int i);

    public abstract int PauseAudioMixing();

    public abstract int PlayAudioEffect(int i, String str, int i2, boolean z);

    public abstract int PreloadAudioEffect(int i, String str);

    public abstract void RegisterAudioObserver(ALI_RTC_INTERFACE.AliAudioType aliAudioType, ALI_RTC_INTERFACE.AliAudioObserver aliAudioObserver);

    public abstract void RegisterPreprocessVideoObserver(ALI_RTC_INTERFACE.AliDetectObserver aliDetectObserver);

    public abstract void RegisterRGBAObserver(String str, ALI_RTC_INTERFACE.AliRenderDataObserver aliRenderDataObserver);

    public abstract void RegisterTexturePostObserver(String str, ALI_RTC_INTERFACE.AliTextureObserver aliTextureObserver);

    public abstract void RegisterTexturePreObserver(String str, ALI_RTC_INTERFACE.AliTextureObserver aliTextureObserver);

    public abstract void RegisterVideoObserver(ALI_RTC_INTERFACE.AliVideoObserver aliVideoObserver);

    public abstract void RegisterYUVObserver(String str, ALI_RTC_INTERFACE.AliVideoObserver aliVideoObserver);

    public abstract int ResumeAudioEffect(int i);

    public abstract int ResumeAudioMixing();

    public abstract int SetAudioAccompanyPlayoutVolume(int i);

    public abstract int SetAudioAccompanyPublishVolume(int i);

    public abstract int SetAudioEffectPlayoutVolume(int i, int i2);

    public abstract int SetAudioEffectPublishVolume(int i, int i2);

    public abstract int SetChannelProfile(ALI_RTC_INTERFACE.AliRTCSDK_Channel_Profile aliRTCSDK_Channel_Profile);

    public abstract int SetClientRole(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_Client_Role);

    public abstract int SetEarBackVolume(int i);

    public abstract int StartAudioFileRecording(String str, int i, int i2);

    public abstract int StopAudioEffect(int i);

    public abstract int StopAudioFileRecording();

    public abstract void UnRegisterAudioObserver(ALI_RTC_INTERFACE.AliAudioType aliAudioType);

    public abstract void UnRegisterPreprocessVideoObserver();

    public abstract void UnRegisterRGBAObserver(String str);

    public abstract void UnRegisterTexturePostObserver(String str);

    public abstract void UnRegisterTexturePreObserver(String str);

    public abstract void UnRegisterVideoObserver();

    public abstract void UnRegisterYUVObserver(String str);

    public abstract int UnloadAudioEffect(int i);

    public abstract void addLocalDisplayWindow(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, AliRendererConfig aliRendererConfig);

    public abstract void addRemoteDisplayWindow(String str, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, AliRendererConfig aliRendererConfig);

    public abstract void applicationMicInterrupt();

    public abstract void applicationMicInterruptResume();

    public abstract void applicationWillBecomeActive();

    public abstract void applicationWillResignActive();

    public abstract void changeLogLevel(ALI_RTC_INTERFACE.AliRTCSDKLogLevel aliRTCSDKLogLevel);

    public abstract void closeCamera();

    public abstract void destory();

    public abstract void enableBackgroundAudioRecording(boolean z);

    public abstract int enableHighDefinitionPreview(boolean z);

    public abstract void enableLocalAudio(boolean z);

    public abstract void enableLocalVideo(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, boolean z);

    public abstract void enableRemoteAudio(String str, boolean z);

    public abstract void enableRemoteVideo(String str, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, boolean z);

    public abstract String[] enumerateAllCaptureDevices();

    public abstract int generateTexture();

    public abstract int getAudioAccompanyVolume();

    public abstract ALI_RTC_INTERFACE.AliCaptureType getCaptureType();

    public abstract String getMediaInfo(String str, String str2, String[] strArr);

    public abstract String getSDKVersion();

    public abstract AppRTCAudioManager.AudioDevice getSelectAudioDevice();

    public abstract ALI_RTC_INTERFACE.TransportStatus getTransportStatus(String str, ALI_RTC_INTERFACE.TransportType transportType);

    public abstract int gslb(ALI_RTC_INTERFACE.AuthInfo authInfo);

    public abstract boolean isCameraSupportExposurePoint();

    public abstract boolean isCameraSupportFocusPoint();

    public abstract boolean isEnableBackgroundAudioRecording();

    public abstract int joinChannel(String str);

    public abstract int joinChannel(ALI_RTC_INTERFACE.AuthInfo authInfo, String str);

    public abstract int leaveChannel();

    public abstract int leaveChannel(long j);

    public abstract void openCamera(ALI_RTC_INTERFACE.AliCameraConfig aliCameraConfig);

    public abstract void pauseRender();

    public abstract void publish(ALI_RTC_INTERFACE.AliPublishConfig aliPublishConfig);

    public abstract ALI_RTC_INTERFACE.VideoRawDataInterface registerVideoRawDataInterface(ALI_RTC_INTERFACE.AliRawDataStreamType aliRawDataStreamType);

    public abstract void removeLocalDisplayWindow(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type);

    public abstract void removeRemoteDisplayWindow(String str, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type);

    public abstract void republish(ALI_RTC_INTERFACE.AliPublishConfig aliPublishConfig);

    public abstract int respondMessageNotification(String str, String str2, String str3);

    public abstract void resubscribe(String str, ALI_RTC_INTERFACE.AliSubscribeConfig aliSubscribeConfig);

    public abstract void resumeRender();

    public abstract void selectSpeakePhone(boolean z);

    public abstract int setAudioAccompanyVolume(int i);

    public abstract int setCameraExposurePoint(float f, float f2);

    public abstract int setCameraFocusPoint(float f, float f2);

    public abstract int setCameraZoom(float f);

    public abstract int setCaptureDeviceByName(String str);

    public abstract void setCollectStatusListener(CollectStatusListener collectStatusListener);

    public abstract void setDeviceOrientationMode(ALI_RTC_INTERFACE.Ali_RTC_Device_Orientation_Mode ali_RTC_Device_Orientation_Mode);

    public abstract int setFlash(boolean z);

    public abstract int setPlayoutVolume(int i);

    public abstract int setRecordingVolume(int i);

    public abstract void setSpeakerStatus(boolean z);

    public abstract void setTraceId(String str);

    public abstract int startAudioAccompany(String str, boolean z, boolean z2, int i);

    public abstract int startAudioCapture();

    public abstract int startAudioPlayer();

    public abstract int stopAudioAccompany();

    public abstract int stopAudioCapture();

    public abstract int stopAudioPlayer();

    public abstract void subscribe(String str, ALI_RTC_INTERFACE.AliSubscribeConfig aliSubscribeConfig);

    public abstract int switchCramer();

    public abstract void unRegisterVideoRawDataInterface(ALI_RTC_INTERFACE.AliRawDataStreamType aliRawDataStreamType);

    public abstract void unpublish();

    public abstract void unsubscribe(String str);

    public abstract void updateDisplayWindow(AliRendererConfig aliRendererConfig);

    public abstract int uplinkChannelMessage(String str, String str2);

    public abstract void uploadLop();

    public class AliRTCStreamConfig {
        public String stream_id;
        public String track_id;

        public AliRTCStreamConfig() {
        }
    }

    public static class AliRendererConfig {
        public int apiLevel = Build.VERSION.SDK_INT;
        public int displayMode = ALI_RTC_INTERFACE.AliDisplayMode.AliRTCSdk_Auto_Mode.ordinal();
        public SurfaceView displayView;
        public boolean enableBeauty = false;
        public boolean flip = false;
        public int height;
        public long sharedContext = 0;
        public int textureHeight = 0;
        public int textureId = 0;
        public int textureWidth = 0;
        public int width;

        public String toString() {
            return "AliRendererConfig{displayView=" + this.displayView + ", width=" + this.width + ", height=" + this.height + ", apiLevel=" + this.apiLevel + ", displayMode=" + this.displayMode + ", flip=" + this.flip + ", sharedContext=" + this.sharedContext + ", enableBeauty=" + this.enableBeauty + '}';
        }
    }

    public static int setupUSBDevice(Context context, USBMediaDevice.USBMediaDeviceEvent devEvent) {
        if (mUSBMediaDevice != null) {
            return 0;
        }
        synchronized (SophonEngine.class) {
            if (mUSBMediaDevice != null) {
                return 0;
            }
            USBMediaDevice uSBMediaDevice = new USBMediaDevice(context, devEvent);
            mUSBMediaDevice = uSBMediaDevice;
            int i = uSBMediaDevice.setupDevice();
            return i;
        }
    }

    public static void closeUSBDevice() {
        synchronized (SophonEngine.class) {
            if (mUSBMediaDevice != null) {
                mUSBMediaDevice.release();
                mUSBMediaDevice = null;
            }
        }
    }

    public static int setH5CompatibleMode(int enable) {
        AlivcLog.i("SophonEngine", "setH5CompatibleMode" + enable);
        return ALI_RTC_INTERFACE_IMPL.SetH5CompatibleMode(enable);
    }

    public static int getH5CompatibleMode() {
        return ALI_RTC_INTERFACE_IMPL.GetH5CompatibleMode();
    }

    public static synchronized SophonEngineImpl create(Context context, String extras, SophonEventListener listener) throws Exception {
        synchronized (SophonEngine.class) {
            if (context != null) {
                if (mInstance == null) {
                    SophonEngineImpl create = new SophonEngineImpl(context, extras, listener).create();
                    mInstance = create;
                    return create;
                }
            }
            SophonEngineImpl sophonEngineImpl = mInstance;
            return sophonEngineImpl;
        }
    }

    public static synchronized SophonEngineImpl create(Context context, SophonEventListener listener) throws Exception {
        synchronized (SophonEngine.class) {
            if (context != null) {
                if (mInstance == null) {
                    SophonEngineImpl create = new SophonEngineImpl(context, listener).create();
                    mInstance = create;
                    return create;
                }
            }
            SophonEngineImpl sophonEngineImpl = mInstance;
            return sophonEngineImpl;
        }
    }

    public static synchronized void destroy() {
        synchronized (SophonEngine.class) {
            if (mInstance != null) {
                mInstance = null;
                System.gc();
            }
        }
    }
}
