package org.webrtc.alirtcInterface;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.view.PointerIconCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;

public class ALI_RTC_INTERFACE_IMPL extends ALI_RTC_INTERFACE {
    final int Rtc_Event_OnAddStream_Type = 9;
    final int Rtc_Event_OnAudioTrackAdded = 11;
    final int Rtc_Event_OnAudioTrackRemoved = 12;
    final int Rtc_Event_OnAuthResult_Type = 1;
    final int Rtc_Event_OnCollectStatus_Type = PointerIconCompat.TYPE_CROSSHAIR;
    final int Rtc_Event_OnCreateRoomResult_Type = 2;
    final int Rtc_Event_OnGslb_Type = 15;
    final int Rtc_Event_OnJoinRoomResult_Type = 3;
    final int Rtc_Event_OnLeaveRoomResult_Type = 4;
    final int Rtc_Event_OnParticipantJoin_Type = 1003;
    final int Rtc_Event_OnParticipantLeave_Type = PointerIconCompat.TYPE_WAIT;
    final int Rtc_Event_OnParticipantPublish_Type = 1001;
    final int Rtc_Event_OnParticipantSubscribe_Type = 1005;
    final int Rtc_Event_OnParticipantUnpublish_Type = 1002;
    final int Rtc_Event_OnParticipantUnsubscribe_Type = PointerIconCompat.TYPE_CELL;
    final int Rtc_Event_OnPublishResult_Type = 5;
    final int Rtc_Event_OnRePublishResult_Type = 16;
    final int Rtc_Event_OnRemoveStream_Type = 10;
    final int Rtc_Event_OnSubscribeResult_Type = 7;
    final int Rtc_Event_OnUnpublishResult_Type = 6;
    final int Rtc_Event_OnUnsubscribeResult_Type = 8;
    final int Rtc_Event_OnVideoTrackAdded = 13;
    final int Rtc_Event_OnVideoTrackRemoved = 14;
    final int Rtc_Event_onReSubscribeResult_Tpye = 17;
    final int Rtc_event_OnRecvStats_Report = PointerIconCompat.TYPE_TEXT;
    private String SDK_VERSION = "";
    private String TAG = "ALI_RTC_INTERFACE_IMPL";
    private CollectStatusListener collectStatusListener = null;
    private ALI_RTC_INTERFACE.AliAudioObserver mExternAudioObserver = null;
    private Map<String, ALI_RTC_INTERFACE.AliRenderDataObserver> mExternRenderDataObserverMap = new HashMap();
    private Map<String, ALI_RTC_INTERFACE.AliTextureObserver> mExternTexturePostObserverMap = new HashMap();
    private Map<String, ALI_RTC_INTERFACE.AliTextureObserver> mExternTexturePreObserverMap = new HashMap();
    private ALI_RTC_INTERFACE.AliDetectObserver mExternVideoDetectObserver = null;
    private Map<String, ALI_RTC_INTERFACE.AliVideoObserver> mExternVideoObserver = new HashMap();
    ALI_RTC_INTERFACE.AliVideoObserver mExternVideoSampleObserver = null;
    private Map<ALI_RTC_INTERFACE.AliRawDataStreamType, ALI_RTC_INTERFACE.VideoRawDataInterface> mVideoRawDataInterfaces = new HashMap();
    /* access modifiers changed from: private */
    public long m_nRtcInterface = 0;
    private AliSophonEngine m_nRtcInterfaceListener = null;

    public static native int nativeGetH5CompatibleMode();

    public static native int nativeSetH5CompatibleMode(int i);

    public native void nativeAddLocalDisplayWindow(long j, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, ALI_RTC_INTERFACE.AliRendererConfig aliRendererConfig);

    public native void nativeAddRemoteDisplayWindow(long j, String str, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, ALI_RTC_INTERFACE.AliRendererConfig aliRendererConfig);

    public native void nativeApplicationMicInterrupt(long j);

    public native void nativeApplicationMicInterruptResume(long j);

    public native void nativeApplicationWillBecomeActive(long j);

    public native void nativeApplicationWillResignActive(long j);

    public native void nativeChangeLogLevel(long j, ALI_RTC_INTERFACE.AliRTCSDKLogLevel aliRTCSDKLogLevel);

    public native int nativeCloseCamera(long j);

    public native long nativeCreate(String str, AliSophonEngine aliSophonEngine);

    public native int nativeDeliverVideoRawDataFrame(long j, long j2, int i, ALI_RTC_INTERFACE.AliRawDataFrame aliRawDataFrame, long j3);

    public native void nativeDestroy(long j);

    public native void nativeEnableBackgroundAudioRecording(long j, boolean z);

    public native int nativeEnableBackgroundPlayout(long j, boolean z);

    public native int nativeEnableBackgroundRecording(long j, boolean z);

    public native int nativeEnableEarBack(long j, boolean z);

    public native int nativeEnableHighDefinitionPreview(long j, boolean z);

    public native void nativeEnableLocalAudio(long j, boolean z);

    public native void nativeEnableLocalVideo(long j, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, boolean z);

    public native void nativeEnableRemoteAudio(long j, String str, boolean z);

    public native void nativeEnableRemoteVideo(long j, String str, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type, boolean z);

    public native void nativeEnableUpload(long j, boolean z);

    public native String[] nativeEnumerateAllCaptureDevices(long j);

    public native int nativeGenerateTexture(long j);

    public native int nativeGetAudioAccompanyPlayoutVolume(long j);

    public native int nativeGetAudioAccompanyPublishVolume(long j);

    public native int nativeGetAudioAccompanyVolume(long j);

    public native int nativeGetAudioEffectPlayoutVolume(long j, int i);

    public native int nativeGetAudioEffectPublishVolume(long j, int i);

    public native int nativeGetCaptureType(long j);

    public native int nativeGetLogLevel(long j);

    public native String nativeGetMediaInfo(long j, String str, String str2, String[] strArr);

    public native String nativeGetSDKVersion(long j);

    public native int nativeGetTransportStatus(long j, String str, ALI_RTC_INTERFACE.TransportType transportType);

    public native int nativeGslb(long j, ALI_RTC_INTERFACE.AuthInfo authInfo);

    public native boolean nativeIsBackgroundAudioRecording(long j);

    public native boolean nativeIsCameraExposurePointSupported(long j);

    public native boolean nativeIsCameraFocusPointSupported(long j);

    public native int nativeJoinChannel(long j, ALI_RTC_INTERFACE.AuthInfo authInfo, String str);

    public native int nativeJoinRoom(long j, String str);

    public native int nativeLeaveChannel(long j, long j2);

    public native int nativeLeaveRoom(long j);

    public native void nativeLog(long j, String str, int i, int i2, String str2, String str3);

    public native void nativeLogDestroy(long j);

    public native int nativeOpenCamera(long j, ALI_RTC_INTERFACE.AliCameraConfig aliCameraConfig);

    public native int nativePauseAudioEffect(long j, int i);

    public native int nativePauseAudioMixing(long j);

    public native int nativePauseRender(long j);

    public native int nativePlayAudioEffect(long j, int i, String str, int i2, boolean z);

    public native int nativePreloadAudioEffect(long j, int i, String str);

    public native void nativePublish(long j, ALI_RTC_INTERFACE.AliPublishConfig aliPublishConfig);

    public native void nativeRegisterAudioCaptureCallback(long j);

    public native void nativeRegisterAudioRenderCallback(long j);

    public native void nativeRegisterAudioVolumeCaptureCallback(long j);

    public native void nativeRegisterRGBACallback(long j, String str);

    public native void nativeRegisterRawAudioCaptureCallback(long j);

    public native void nativeRegisterTexturePostCallback(long j, String str);

    public native void nativeRegisterTexturePreCallback(long j, String str);

    public native void nativeRegisterVideoCallback(long j);

    public native long nativeRegisterVideoRawDataInterface(long j, int i);

    public native void nativeRegisterYUVCallback(long j, String str);

    public native void nativeRegisterYUVDetectCallback(long j);

    public native void nativeRemoveLocalDisplayWindow(long j, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type);

    public native void nativeRemoveRemoteDisplayWindow(long j, String str, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_VideSource_Type);

    public native void nativeRepublish(long j, ALI_RTC_INTERFACE.AliPublishConfig aliPublishConfig);

    public native int nativeRespondMessageNotification(long j, String str, String str2, String str3);

    public native void nativeResubscribe(long j, String str, ALI_RTC_INTERFACE.AliSubscribeConfig aliSubscribeConfig);

    public native int nativeResumeAudioEffect(long j, int i);

    public native int nativeResumeAudioMixing(long j);

    public native int nativeResumeRender(long j);

    public native int nativeSetAudioAccompanyPlayoutVolume(long j, int i);

    public native int nativeSetAudioAccompanyPublishVolume(long j, int i);

    public native int nativeSetAudioAccompanyVolume(long j, int i);

    public native int nativeSetAudioEffectPlayoutVolume(long j, int i, int i2);

    public native int nativeSetAudioEffectPublishVolume(long j, int i, int i2);

    public native int nativeSetCameraExposurePoint(long j, float f, float f2);

    public native int nativeSetCameraFocusPoint(long j, float f, float f2);

    public native int nativeSetCameraZoom(long j, float f);

    public native int nativeSetCaptureDeviceByName(long j, String str);

    public native int nativeSetChannelProfile(long j, ALI_RTC_INTERFACE.AliRTCSDK_Channel_Profile aliRTCSDK_Channel_Profile);

    public native int nativeSetClientRole(long j, ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_Client_Role);

    public native void nativeSetContext(long j, Context context);

    public native void nativeSetDeviceOrientationMode(long j, ALI_RTC_INTERFACE.Ali_RTC_Device_Orientation_Mode ali_RTC_Device_Orientation_Mode);

    public native int nativeSetEarBackVolume(long j, int i);

    public native int nativeSetFlash(long j, boolean z);

    public native int nativeSetPlayoutVolume(long j, int i);

    public native int nativeSetRecordingVolume(long j, int i);

    public native void nativeSetSpeakerStatus(long j, boolean z);

    public native void nativeSetTraceId(long j, String str);

    public native void nativeSetUploadAppID(long j, String str);

    public native void nativeSetUploadSessionID(long j, String str);

    public native int nativeStartAudioAccompany(long j, String str, boolean z, boolean z2, int i);

    public native int nativeStartAudioFileRecording(long j, String str, int i, int i2);

    public native int nativeStopAudioAccompany(long j);

    public native int nativeStopAudioEffect(long j, int i);

    public native int nativeStopAudioFileRecording(long j);

    public native void nativeSubscribe(long j, String str, ALI_RTC_INTERFACE.AliSubscribeConfig aliSubscribeConfig);

    public native int nativeSwitchCamera(long j);

    public native void nativeUnRegisterAudioCaptureCallback(long j);

    public native void nativeUnRegisterAudioRenderCallback(long j);

    public native void nativeUnRegisterAudioVolumeCaptureCallback(long j);

    public native void nativeUnRegisterRGBACallback(long j, String str);

    public native void nativeUnRegisterRawAudioCaptureCallback(long j);

    public native void nativeUnRegisterTexturePostCallback(long j, String str);

    public native void nativeUnRegisterTexturePreCallback(long j, String str);

    public native void nativeUnRegisterVideoCallback(long j);

    public native void nativeUnRegisterVideoRawDataInterface(long j, long j2, int i);

    public native void nativeUnRegisterYUVCallback(long j, String str);

    public native void nativeUnRegisterYUVDetectCallback(long j);

    public native int nativeUnloadAudioEffect(long j, int i);

    public native void nativeUnpublish(long j);

    public native void nativeUnsubscribe(long j, String str);

    public native void nativeUpdateDisplayWindow(long j, ALI_RTC_INTERFACE.AliRendererConfig aliRendererConfig);

    public native int nativeUplinkChannelMessage(long j, String str, String str2);

    public native void nativeUploadChannelLog(long j);

    public native void nativeUploadLog(long j);

    public static int SetH5CompatibleMode(int enable) {
        Log.i("ALI_RTC_INTERFACE_IMPL", " SetH5CompatibleMode---enable=" + enable);
        return nativeSetH5CompatibleMode(enable);
    }

    public static int GetH5CompatibleMode() {
        Log.i("ALI_RTC_INTERFACE_IMPL", " GetH5CompatibleMode---");
        return nativeGetH5CompatibleMode();
    }

    public void setCollectStatusListener(CollectStatusListener collectStatusListener2) {
        this.collectStatusListener = collectStatusListener2;
    }

    public void OnEventNotifyJNI(int event, int result, String callID, String param) {
        String str = this.TAG;
        Log.i(str, " OnEventNotify:event=" + event + " result=" + result);
        switch (event) {
            case 3:
                this.m_nRtcInterfaceListener.onJoinChannelResult(result);
                return;
            case 4:
                this.m_nRtcInterfaceListener.onLeaveChannelResult(result);
                return;
            case 5:
                this.m_nRtcInterfaceListener.onPublishResult(result, callID);
                return;
            case 6:
                this.m_nRtcInterfaceListener.onUnpublishResult(result, callID);
                return;
            case 7:
                this.m_nRtcInterfaceListener.onSubscribeResult(result, callID);
                return;
            case 8:
                this.m_nRtcInterfaceListener.onUnsubscribeResult(result, callID);
                return;
            default:
                switch (event) {
                    case 15:
                        this.m_nRtcInterfaceListener.onGslbResult(result);
                        return;
                    case 16:
                        this.m_nRtcInterfaceListener.onRepublishResult(result, callID);
                        return;
                    case 17:
                        this.m_nRtcInterfaceListener.onResubscribeResult(result, callID);
                        return;
                    default:
                        String str2 = this.TAG;
                        Log.w(str2, "wrong event id::event=" + event);
                        return;
                }
        }
    }

    public void OnError(int error, String param) {
        String str = this.TAG;
        Log.i(str, "error code is " + error);
        this.m_nRtcInterfaceListener.onError(error, param);
    }

    public void OnWarning(int warning, String param) {
        String str = this.TAG;
        Log.i(str, "warning code is " + warning);
        this.m_nRtcInterfaceListener.onWarning(warning, param);
    }

    public void OnConnectionChange(int mediaConState) {
        String str = this.TAG;
        Log.i(str, "mediaConState is " + mediaConState);
        this.m_nRtcInterfaceListener.onConnectionChange(mediaConState);
    }

    public void OnParticipantJoinNotifyJNI(AliParticipantInfo[] participant_list, int join_count) {
        String str = this.TAG;
        Log.i(str, " OnParticipantJoinNotifyJNI " + join_count);
        this.m_nRtcInterfaceListener.onParticipantJoinNotify(participant_list, join_count);
    }

    public void OnParticipantLeaveNotifyJNI(AliParticipantInfo[] participant_list, int leave_count) {
        String str = this.TAG;
        Log.i(str, " OnParticipantLeaveNotifyJNI " + leave_count);
        this.m_nRtcInterfaceListener.onParticipantLeaveNotify(participant_list, leave_count);
    }

    public void OnPublishInfoNotifyJNI(PublisherInfo[] publisher_list, int publisher_count) {
        String str = this.TAG;
        Log.i(str, " OnPublishInfoNotifyJNI " + publisher_list.length);
        this.m_nRtcInterfaceListener.onParticipantPublishNotify(publisher_list, publisher_count);
    }

    public void OnSubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
        String str = this.TAG;
        Log.i(str, " onSubscribeResult2 result is " + result);
        this.m_nRtcInterfaceListener.onSubscribeResult2(result, callID, reqConfig, curConfig);
    }

    public void OnResubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
        String str = this.TAG;
        Log.i(str, " onResubscribeResult2 result is" + result);
        this.m_nRtcInterfaceListener.onResubscribeResult2(result, callID, reqConfig, curConfig);
    }

    public void OnUnpublishInfoNotifyJNI(AliUnPublisherInfo[] unpublisher_list, int publisher_count) {
        String str = this.TAG;
        Log.i(str, " OnUnpublishInfoNotifyJNI " + unpublisher_list.length);
        this.m_nRtcInterfaceListener.onParticipantUnpublishNotify(unpublisher_list, publisher_count);
    }

    public void OnParticipantSubscribeNotifyJNI(AliSubscriberInfo[] subcribeinfo_list, int publisher_count) {
        String str = this.TAG;
        Log.i(str, " OnParticipantSubscribeNotifyJNI " + subcribeinfo_list.length);
        this.m_nRtcInterfaceListener.onParticipantSubscribeNotify(subcribeinfo_list, publisher_count);
    }

    public void OnParticipantUnsubscribeNotifyJNI(AliParticipantInfo[] participant_list, int unsub_count) {
        this.m_nRtcInterfaceListener.onParticipantUnsubscribeNotify(participant_list, unsub_count);
    }

    public void OnParticipantStatusNotifyJNI(AliStatusInfo[] status_info_list, int count) {
        this.m_nRtcInterfaceListener.onParticipantStatusNotify(status_info_list, count);
    }

    public void OnAliRtcStatsJNI(ALI_RTC_INTERFACE.AliRtcStats aliRtcStats) {
        Log.d(this.TAG, "OnAliRtcStatsJNI");
        this.m_nRtcInterfaceListener.onAliRtcStats(aliRtcStats);
    }

    public void OnLogMessageJNI(String message) {
        this.m_nRtcInterfaceListener.onLogMessage(message);
    }

    public void OnCollectStatusJNI(int event, String call_id, ArrayList<HashMap<String, String>> list) {
        CollectStatusListener collectStatusListener2;
        if (event == 1007 && (collectStatusListener2 = this.collectStatusListener) != null) {
            collectStatusListener2.onCollectStatusInfo(call_id, list);
        }
    }

    public void OnTransportStatusChangeJNI(String callId, int event, int status) {
        this.m_nRtcInterfaceListener.onTransportStatusChange(callId, ALI_RTC_INTERFACE.TransportType.fromNativeIndex(event), ALI_RTC_INTERFACE.TransportStatus.fromNativeIndex(status));
    }

    public void OnNetworkQualityChangedJNI(ArrayList<ALI_RTC_INTERFACE.AliTransportInfo> network_quality) {
        this.m_nRtcInterfaceListener.onNetworkQualityChange(network_quality);
    }

    public void OnMessage(String tid, String content_type, String content) {
        this.m_nRtcInterfaceListener.onMessage(tid, content_type, content);
    }

    public void OnBye(int code) {
        this.m_nRtcInterfaceListener.onBye(code);
    }

    public void OnUplinkChannelMessage(int result, String content_type, String content) {
        this.m_nRtcInterfaceListener.onUplinkChannelMessage(result, content_type, content);
    }

    public String OnCollectPlatformProfile() {
        return this.m_nRtcInterfaceListener.onCollectPlatformProfile();
    }

    public String OnFetchPerformanceInfo() {
        return this.m_nRtcInterfaceListener.onFetchPerformanceInfo();
    }

    public boolean OnFetchAudioPermissionInfo() {
        return this.m_nRtcInterfaceListener.onFetchAudioPermissionInfo();
    }

    public String OnFetchAudioDeviceInfo() {
        return this.m_nRtcInterfaceListener.onFetchAudioDeviceInfo();
    }

    public int OnFetchDeviceOrientation() {
        return this.m_nRtcInterfaceListener.onFetchDeviceOrientation();
    }

    public int GetApiLevel() {
        return Build.VERSION.SDK_INT;
    }

    public void OnWindowRenderReady(String callId, int video_type) {
        this.m_nRtcInterfaceListener.onWindowRenderReady(callId, video_type);
    }

    public void OnUpdateRoleNotify(int old_role, int new_role) {
        this.m_nRtcInterfaceListener.onUpdateRoleNotify(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role.fromNativeIndex(old_role), ALI_RTC_INTERFACE.AliRTCSDK_Client_Role.fromNativeIndex(new_role));
    }

    public void OnFirstFrameReceived(String callId, String stream_label, String track_label, int time_cost_ms) {
        this.m_nRtcInterfaceListener.onFirstFrameReceived(callId, stream_label, track_label, time_cost_ms);
    }

    public void OnFirstPacketSent(String callId, String stream_label, String track_label, int time_cost_ms) {
        this.m_nRtcInterfaceListener.onFirstPacketSent(callId, stream_label, track_label, time_cost_ms);
    }

    public void OnFirstPacketReceived(String callId, String stream_label, String track_label, int time_cost_ms) {
        this.m_nRtcInterfaceListener.onFirstPacketReceived(callId, stream_label, track_label, time_cost_ms);
    }

    public void OnAudioCaptureVolumeData(String callid, int volume) {
        ALI_RTC_INTERFACE.AliAudioObserver aliAudioObserver = this.mExternAudioObserver;
        if (aliAudioObserver != null) {
            aliAudioObserver.onCaptureVolumeData(callid, volume);
        }
    }

    public void OnAudioCaptureRawData(long dataPtr, int numSamples, int bytesPerSample, int numChannels, int sampleRate, int samplesPerSec) {
        ALI_RTC_INTERFACE.AliAudioObserver aliAudioObserver = this.mExternAudioObserver;
        if (aliAudioObserver != null) {
            aliAudioObserver.onCaptureRawData(dataPtr, numSamples, bytesPerSample, numChannels, sampleRate, samplesPerSec);
        }
    }

    public void OnAudioCaptureData(long dataPtr, int numSamples, int bytesPerSample, int numChannels, int sampleRate, int samplesPerSec) {
        ALI_RTC_INTERFACE.AliAudioObserver aliAudioObserver = this.mExternAudioObserver;
        if (aliAudioObserver != null) {
            aliAudioObserver.onCaptureData(dataPtr, numSamples, bytesPerSample, numChannels, sampleRate, samplesPerSec);
        }
    }

    public void OnAudioRenderData(long dataPtr, int numSamples, int bytesPerSample, int numChannels, int sampleRate, int samplesPerSec) {
        ALI_RTC_INTERFACE.AliAudioObserver aliAudioObserver = this.mExternAudioObserver;
        if (aliAudioObserver != null) {
            aliAudioObserver.onRenderData(dataPtr, numSamples, bytesPerSample, numChannels, sampleRate, samplesPerSec);
        }
    }

    public void OnVideoRenderCallbackData(String callId, int sourceType, long yPtr, long uPtr, long vPtr, int format, int width, int height, int ystride, int ustride, int vstride, int rotate, long extraData) {
        if (this.mExternVideoSampleObserver != null) {
            ALI_RTC_INTERFACE.AliVideoSample videoSample = new ALI_RTC_INTERFACE.AliVideoSample();
            videoSample.dataFrameY = yPtr;
            videoSample.dataFrameU = uPtr;
            videoSample.dataFrameV = vPtr;
            videoSample.format = ALI_RTC_INTERFACE.AliRTCImageFormat.GetAliRTCImageFormat(format);
            videoSample.width = width;
            videoSample.height = height;
            videoSample.strideY = ystride;
            videoSample.strideU = ustride;
            videoSample.strideV = vstride;
            videoSample.rotate = rotate;
            videoSample.extraData = extraData;
            this.mExternVideoSampleObserver.onRemoteVideoSample(callId, ALI_RTC_INTERFACE.AliVideoSourceType.values()[sourceType], videoSample);
            return;
        }
        String str = callId;
        long j = uPtr;
        long j2 = vPtr;
        int i = width;
        int i2 = height;
        int i3 = ystride;
        int i4 = ustride;
        int i5 = vstride;
        int i6 = rotate;
        long j3 = extraData;
    }

    public long OnVideoDetectData(long yPtr, long uPtr, long vPtr, int format, int width, int height, int ystride, int ustride, int vstride, int rotate, long extraData) {
        ALI_RTC_INTERFACE.AliDetectObserver aliDetectObserver = this.mExternVideoDetectObserver;
        if (aliDetectObserver == null) {
            return 0;
        }
        return aliDetectObserver.onData(yPtr, uPtr, vPtr, ALI_RTC_INTERFACE.AliRTCImageFormat.GetAliRTCImageFormat(format), width, height, ystride, ustride, vstride, rotate, extraData);
    }

    public void OnVideoCaptureData(int sourceType, long yPtr, long uPtr, long vPtr, int format, int width, int height, int ystride, int ustride, int vstride, int rotate, long extraData) {
        ALI_RTC_INTERFACE.AliVideoSample videoSample = new ALI_RTC_INTERFACE.AliVideoSample();
        videoSample.dataFrameY = yPtr;
        videoSample.dataFrameU = uPtr;
        videoSample.dataFrameV = vPtr;
        videoSample.format = ALI_RTC_INTERFACE.AliRTCImageFormat.GetAliRTCImageFormat(format);
        videoSample.width = width;
        videoSample.height = height;
        videoSample.strideY = ystride;
        videoSample.strideU = ustride;
        videoSample.strideV = vstride;
        videoSample.rotate = rotate;
        videoSample.extraData = extraData;
        this.mExternVideoSampleObserver.onLocalVideoSample(ALI_RTC_INTERFACE.AliVideoSourceType.values()[sourceType], videoSample);
    }

    public void OnTexturePreCreate(String callId, long context) {
        if (this.mExternTexturePreObserverMap.get(callId) != null) {
            this.mExternTexturePreObserverMap.get(callId).onTextureCreate(callId, context);
        }
    }

    public void OnTexturePostCreate(String callId, long context) {
        if (this.mExternTexturePostObserverMap.get(callId) != null) {
            this.mExternTexturePostObserverMap.get(callId).onTextureCreate(callId, context);
        }
    }

    public void OnTexturePreDestroy(String callId) {
        if (this.mExternTexturePreObserverMap.get(callId) != null) {
            this.mExternTexturePreObserverMap.get(callId).onTextureDestroy(callId);
        }
    }

    public void OnTexturePostDestroy(String callId) {
        if (this.mExternTexturePostObserverMap.get(callId) != null) {
            this.mExternTexturePostObserverMap.get(callId).onTextureDestroy(callId);
        }
    }

    public int OnTexturePreData(String callId, int textureId, int width, int height, int stride, int rotate, long extraData) {
        String str = callId;
        if (this.mExternTexturePreObserverMap.get(callId) != null) {
            return this.mExternTexturePreObserverMap.get(callId).onTexture(callId, textureId, width, height, stride, rotate, extraData);
        }
        return textureId;
    }

    public int OnTexturePostData(String callId, int textureId, int width, int height, int stride, int rotate, long extraData) {
        String str = callId;
        if (this.mExternTexturePostObserverMap.get(callId) != null) {
            return this.mExternTexturePostObserverMap.get(callId).onTexture(callId, textureId, width, height, stride, rotate, extraData);
        }
        return textureId;
    }

    public void OnVideoRenderData(String callId, long dataPtr, int format, int width, int height, int stride, int rotation, long extraData) {
        String str = callId;
        if (this.mExternRenderDataObserverMap.get(callId) != null) {
            this.mExternRenderDataObserverMap.get(callId).onRenderData(callId, dataPtr, format, width, height, stride, rotation, extraData);
        } else if (this.mExternRenderDataObserverMap.size() > 0) {
            this.mExternRenderDataObserverMap.values().iterator().next().onRenderData(callId, dataPtr, format, width, height, stride, rotation, extraData);
        }
    }

    public long Create(String extras, AliSophonEngine listener) {
        this.m_nRtcInterfaceListener = listener;
        long nativeCreate = nativeCreate(extras, listener);
        this.m_nRtcInterface = nativeCreate;
        return nativeCreate;
    }

    public void Destory() {
        nativeDestroy(this.m_nRtcInterface);
        this.m_nRtcInterfaceListener.release();
    }

    public void SetContext(Context context) {
        nativeSetContext(this.m_nRtcInterface, context);
    }

    public String GetSDKVersion() {
        if (TextUtils.isEmpty(this.SDK_VERSION)) {
            this.SDK_VERSION = nativeGetSDKVersion(this.m_nRtcInterface);
        }
        return this.SDK_VERSION;
    }

    public int Gslb(ALI_RTC_INTERFACE.AuthInfo auth_info) {
        return nativeGslb(this.m_nRtcInterface, auth_info);
    }

    public int JoinChannel(String display_name) {
        return nativeJoinRoom(this.m_nRtcInterface, display_name);
    }

    public int JoinChannel(ALI_RTC_INTERFACE.AuthInfo auth_info, String display_name) {
        return nativeJoinChannel(this.m_nRtcInterface, auth_info, display_name);
    }

    public int LeaveChannel() {
        return nativeLeaveRoom(this.m_nRtcInterface);
    }

    public int LeaveChannel(long timeout) {
        return nativeLeaveChannel(this.m_nRtcInterface, timeout);
    }

    public void Publish(ALI_RTC_INTERFACE.AliPublishConfig publish_config) {
        nativePublish(this.m_nRtcInterface, publish_config);
    }

    public void Republish(ALI_RTC_INTERFACE.AliPublishConfig publish_config) {
        nativeRepublish(this.m_nRtcInterface, publish_config);
    }

    public void Unpublish() {
        nativeUnpublish(this.m_nRtcInterface);
    }

    public void Subscribe(String call_id, ALI_RTC_INTERFACE.AliSubscribeConfig subscribe_config) {
        nativeSubscribe(this.m_nRtcInterface, call_id, subscribe_config);
    }

    public void Resubscribe(String call_id, ALI_RTC_INTERFACE.AliSubscribeConfig subscribe_config) {
        nativeResubscribe(this.m_nRtcInterface, call_id, subscribe_config);
    }

    public void Unsubscribe(String call_id) {
        nativeUnsubscribe(this.m_nRtcInterface, call_id);
    }

    public void AddRemoteDisplayWindow(String call_id, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type type, ALI_RTC_INTERFACE.AliRendererConfig config) {
        String str = this.TAG;
        Log.e(str, "addRemoteDisplayWindow:callId" + call_id + "\nSurface" + config.display_view + "width:" + config.width + "height:" + config.height);
        nativeAddRemoteDisplayWindow(this.m_nRtcInterface, call_id, type, config);
        for (Map.Entry<String, ALI_RTC_INTERFACE.AliRenderDataObserver> entry : this.mExternRenderDataObserverMap.entrySet()) {
            nativeRegisterRGBACallback(this.m_nRtcInterface, entry.getKey());
        }
        for (Map.Entry<String, ALI_RTC_INTERFACE.AliTextureObserver> entry2 : this.mExternTexturePreObserverMap.entrySet()) {
            if (entry2.getKey() != null && !entry2.getKey().equals("")) {
                nativeRegisterTexturePreCallback(this.m_nRtcInterface, entry2.getKey());
            }
        }
        for (Map.Entry<String, ALI_RTC_INTERFACE.AliTextureObserver> entry3 : this.mExternTexturePostObserverMap.entrySet()) {
            if (entry3.getKey() != null && !entry3.getKey().equals("")) {
                nativeRegisterTexturePostCallback(this.m_nRtcInterface, entry3.getKey());
            }
        }
    }

    public void RemoveRemoteDisplayWindow(String call_id, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type type) {
        String str = this.TAG;
        Log.e(str, "removeRemoteDisplayWindow:callId" + call_id);
        nativeRemoveRemoteDisplayWindow(this.m_nRtcInterface, call_id, type);
    }

    public void AddLocalDisplayWindow(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_videSource_type, ALI_RTC_INTERFACE.AliRendererConfig config) {
        String str = this.TAG;
        Log.e(str, "addLocalDisplayWindow\nSurface" + config.display_view + "width:" + config.width + "height:" + config.height);
        nativeAddLocalDisplayWindow(this.m_nRtcInterface, aliRTCSdk_videSource_type, config);
        for (Map.Entry<String, ALI_RTC_INTERFACE.AliTextureObserver> entry : this.mExternTexturePreObserverMap.entrySet()) {
            nativeRegisterTexturePreCallback(this.m_nRtcInterface, entry.getKey());
        }
        for (Map.Entry<String, ALI_RTC_INTERFACE.AliTextureObserver> entry2 : this.mExternTexturePostObserverMap.entrySet()) {
            nativeRegisterTexturePostCallback(this.m_nRtcInterface, entry2.getKey());
        }
        for (Map.Entry<String, ALI_RTC_INTERFACE.AliRenderDataObserver> entry3 : this.mExternRenderDataObserverMap.entrySet()) {
            nativeRegisterRGBACallback(this.m_nRtcInterface, entry3.getKey());
        }
    }

    public void RemoveLocalDisplayWindow(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_videSource_type) {
        Log.e(this.TAG, "removeLocalDisplayWindow");
        nativeRemoveLocalDisplayWindow(this.m_nRtcInterface, aliRTCSdk_videSource_type);
    }

    public void UpdateDisplayWindow(ALI_RTC_INTERFACE.AliRendererConfig config) {
        String str = this.TAG;
        Log.e(str, "updateDisplayWindow\nSurface" + config.display_view + "width:" + config.width + "height:" + config.height + " ,textureId" + config.textureId + "textureWidth:" + config.textureWidth + " ,textureHeight:" + config.textureHeight);
        nativeUpdateDisplayWindow(this.m_nRtcInterface, config);
    }

    public ALI_RTC_INTERFACE.AliCaptureType GetCaptureType() {
        ALI_RTC_INTERFACE.AliCaptureType type = ALI_RTC_INTERFACE.AliCaptureType.SDK_Capture_Typ_Front;
        int tmp = nativeGetCaptureType(this.m_nRtcInterface);
        if (ALI_RTC_INTERFACE.AliCaptureType.SDK_Capture_Typ_Invalid.getCaptureType() == tmp) {
            return ALI_RTC_INTERFACE.AliCaptureType.SDK_Capture_Typ_Invalid;
        }
        if (ALI_RTC_INTERFACE.AliCaptureType.SDK_Capture_Typ_Front.getCaptureType() == tmp) {
            return ALI_RTC_INTERFACE.AliCaptureType.SDK_Capture_Typ_Back;
        }
        if (ALI_RTC_INTERFACE.AliCaptureType.SDK_Capture_Typ_Back.getCaptureType() == tmp) {
            return ALI_RTC_INTERFACE.AliCaptureType.SDK_Capture_Typ_Front;
        }
        return type;
    }

    public int enableHighDefinitionPreview(boolean enable) {
        return nativeEnableHighDefinitionPreview(this.m_nRtcInterface, enable);
    }

    public void OpenCamera(ALI_RTC_INTERFACE.AliCameraConfig aliCameraConfig) {
        nativeOpenCamera(this.m_nRtcInterface, aliCameraConfig);
    }

    public void CloseCamera() {
        nativeCloseCamera(this.m_nRtcInterface);
    }

    public int SwitchCramer() {
        return nativeSwitchCamera(this.m_nRtcInterface);
    }

    public int SetFlash(boolean flash) {
        return nativeSetFlash(this.m_nRtcInterface, flash);
    }

    public int SetCameraZoom(float zoom) {
        return nativeSetCameraZoom(this.m_nRtcInterface, zoom);
    }

    public boolean isCameraSupportFocusPoint() {
        return nativeIsCameraFocusPointSupported(this.m_nRtcInterface);
    }

    public boolean isCameraSupportExposurePoint() {
        return nativeIsCameraExposurePointSupported(this.m_nRtcInterface);
    }

    public int setCameraFocusPoint(float x, float y) {
        return nativeSetCameraFocusPoint(this.m_nRtcInterface, x, y);
    }

    public int setCameraExposurePoint(float x, float y) {
        return nativeSetCameraExposurePoint(this.m_nRtcInterface, x, y);
    }

    public int setRecordingVolume(int volume) {
        return nativeSetRecordingVolume(this.m_nRtcInterface, volume);
    }

    public int setPlayoutVolume(int volume) {
        return nativeSetPlayoutVolume(this.m_nRtcInterface, volume);
    }

    public void EnableLocalAudio(boolean enable) {
        nativeEnableLocalAudio(this.m_nRtcInterface, enable);
    }

    public void SetSpeakerStatus(boolean enable) {
        nativeSetSpeakerStatus(this.m_nRtcInterface, enable);
    }

    public void EnableLocalVideo(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_videSource_type, boolean enable) {
        nativeEnableLocalVideo(this.m_nRtcInterface, aliRTCSdk_videSource_type, enable);
    }

    public void EnableRemoteAudio(String call_id, boolean mute) {
        nativeEnableRemoteAudio(this.m_nRtcInterface, call_id, mute);
    }

    public void EnableRemoteVideo(String call_id, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type aliRTCSdk_videSource_type, boolean mute) {
        nativeEnableRemoteVideo(this.m_nRtcInterface, call_id, aliRTCSdk_videSource_type, mute);
    }

    public void ChangeLogLevel(ALI_RTC_INTERFACE.AliRTCSDKLogLevel level) {
        nativeChangeLogLevel(this.m_nRtcInterface, level);
    }

    public void Log(String file, int line, ALI_RTC_INTERFACE.AliRTCSDKLogLevel sev, String tag, String log) {
        nativeLog(this.m_nRtcInterface, file, line, sev.getValue(), tag, log);
    }

    public int GetLogLevel() {
        return nativeGetLogLevel(this.m_nRtcInterface);
    }

    public void EnableUpload(boolean enable) {
        nativeEnableUpload(this.m_nRtcInterface, enable);
    }

    public void SetUploadAppID(String appID) {
        nativeSetUploadAppID(this.m_nRtcInterface, appID);
    }

    public void SetUploadSessionID(String sessionID) {
        nativeSetUploadSessionID(this.m_nRtcInterface, sessionID);
    }

    public void UploadLog() {
        nativeUploadLog(this.m_nRtcInterface);
    }

    public void UploadChannelLog() {
        nativeUploadChannelLog(this.m_nRtcInterface);
    }

    public void LogDestroy() {
        nativeLogDestroy(this.m_nRtcInterface);
    }

    public void PauseRender() {
        nativePauseRender(this.m_nRtcInterface);
    }

    public void ResumeRender() {
        nativeResumeRender(this.m_nRtcInterface);
    }

    public ALI_RTC_INTERFACE.TransportStatus GetTransportStatus(String call_id, ALI_RTC_INTERFACE.TransportType type) {
        return ALI_RTC_INTERFACE.TransportStatus.fromNativeIndex(nativeGetTransportStatus(this.m_nRtcInterface, call_id, type));
    }

    public String[] EnumerateAllCaptureDevices() {
        return nativeEnumerateAllCaptureDevices(this.m_nRtcInterface);
    }

    public int SetCaptureDeviceByName(String deviceName) {
        return nativeSetCaptureDeviceByName(this.m_nRtcInterface, deviceName);
    }

    public int RespondMessageNotification(String tid, String content_type, String content) {
        return nativeRespondMessageNotification(this.m_nRtcInterface, tid, content_type, content);
    }

    public int UplinkChannelMessage(String content_type, String content) {
        return nativeUplinkChannelMessage(this.m_nRtcInterface, content_type, content);
    }

    public void RegisterAudioObserver(ALI_RTC_INTERFACE.AliAudioType type, ALI_RTC_INTERFACE.AliAudioObserver observer) {
        if (observer == null && type.equals(ALI_RTC_INTERFACE.AliAudioType.PUB_OBSERVER)) {
            nativeUnRegisterAudioCaptureCallback(this.m_nRtcInterface);
        } else if (type.equals(ALI_RTC_INTERFACE.AliAudioType.PUB_OBSERVER)) {
            nativeRegisterAudioCaptureCallback(this.m_nRtcInterface);
        } else if (observer == null && type.equals(ALI_RTC_INTERFACE.AliAudioType.SUB_OBSERVER)) {
            nativeUnRegisterAudioRenderCallback(this.m_nRtcInterface);
        } else if (type.equals(ALI_RTC_INTERFACE.AliAudioType.SUB_OBSERVER)) {
            nativeRegisterAudioRenderCallback(this.m_nRtcInterface);
        } else if (observer == null && type.equals(ALI_RTC_INTERFACE.AliAudioType.RAW_DATA_OBSERVER)) {
            nativeUnRegisterRawAudioCaptureCallback(this.m_nRtcInterface);
        } else if (type.equals(ALI_RTC_INTERFACE.AliAudioType.RAW_DATA_OBSERVER)) {
            nativeRegisterRawAudioCaptureCallback(this.m_nRtcInterface);
        } else if (observer == null && type.equals(ALI_RTC_INTERFACE.AliAudioType.VOLUME_DATA_OBSERVER)) {
            nativeUnRegisterAudioVolumeCaptureCallback(this.m_nRtcInterface);
        } else if (type.equals(ALI_RTC_INTERFACE.AliAudioType.VOLUME_DATA_OBSERVER)) {
            nativeRegisterAudioVolumeCaptureCallback(this.m_nRtcInterface);
        }
        this.mExternAudioObserver = observer;
    }

    public void UnRegisterAudioObserver(ALI_RTC_INTERFACE.AliAudioType type) {
        if (type.equals(ALI_RTC_INTERFACE.AliAudioType.PUB_OBSERVER)) {
            nativeUnRegisterAudioCaptureCallback(this.m_nRtcInterface);
        } else if (type.equals(ALI_RTC_INTERFACE.AliAudioType.SUB_OBSERVER)) {
            nativeUnRegisterAudioRenderCallback(this.m_nRtcInterface);
        } else if (type.equals(ALI_RTC_INTERFACE.AliAudioType.RAW_DATA_OBSERVER)) {
            nativeUnRegisterRawAudioCaptureCallback(this.m_nRtcInterface);
        } else if (type.equals(ALI_RTC_INTERFACE.AliAudioType.VOLUME_DATA_OBSERVER)) {
            nativeUnRegisterAudioVolumeCaptureCallback(this.m_nRtcInterface);
        }
        this.mExternAudioObserver = null;
    }

    public void RegisterYUVObserver(String callId, ALI_RTC_INTERFACE.AliVideoObserver observer) {
        if (observer == null) {
            this.mExternVideoObserver.remove(callId);
            nativeUnRegisterYUVCallback(this.m_nRtcInterface, callId);
            return;
        }
        this.mExternVideoObserver.put(callId, observer);
        nativeRegisterYUVCallback(this.m_nRtcInterface, callId);
    }

    public void UnRegisterYUVObserver(String callId) {
        this.mExternVideoObserver.remove(callId);
        nativeUnRegisterYUVCallback(this.m_nRtcInterface, callId);
    }

    public void RegisterVideoObserver(ALI_RTC_INTERFACE.AliVideoObserver observer) {
        if (observer == null) {
            this.mExternVideoSampleObserver = null;
            nativeUnRegisterVideoCallback(this.m_nRtcInterface);
            return;
        }
        this.mExternVideoSampleObserver = observer;
        nativeRegisterVideoCallback(this.m_nRtcInterface);
    }

    public void UnRegisterVideoObserver() {
        this.mExternVideoSampleObserver = null;
        nativeUnRegisterVideoCallback(this.m_nRtcInterface);
    }

    public void RegisterPreprocessVideoObserver(ALI_RTC_INTERFACE.AliDetectObserver observer) {
        this.mExternVideoDetectObserver = observer;
        nativeRegisterYUVDetectCallback(this.m_nRtcInterface);
    }

    public void UnRegisterPreprocessVideoObserver() {
        this.mExternVideoDetectObserver = null;
        nativeUnRegisterYUVDetectCallback(this.m_nRtcInterface);
    }

    public void RegisterTexturePreObserver(String callId, ALI_RTC_INTERFACE.AliTextureObserver observer) {
        if (observer == null) {
            this.mExternTexturePreObserverMap.remove(callId);
            nativeUnRegisterTexturePreCallback(this.m_nRtcInterface, callId);
            return;
        }
        this.mExternTexturePreObserverMap.put(callId, observer);
        nativeRegisterTexturePreCallback(this.m_nRtcInterface, callId);
    }

    public void UnRegisterTexturePreObserver(String callId) {
        this.mExternTexturePreObserverMap.remove(callId);
        nativeUnRegisterTexturePreCallback(this.m_nRtcInterface, callId);
    }

    public void RegisterTexturePostObserver(String callId, ALI_RTC_INTERFACE.AliTextureObserver observer) {
        if (observer == null) {
            this.mExternTexturePostObserverMap.remove(callId);
            nativeUnRegisterTexturePostCallback(this.m_nRtcInterface, callId);
            return;
        }
        this.mExternTexturePostObserverMap.put(callId, observer);
        nativeRegisterTexturePostCallback(this.m_nRtcInterface, callId);
    }

    public void UnRegisterTexturePostObserver(String callId) {
        this.mExternTexturePostObserverMap.remove(callId);
        nativeUnRegisterTexturePostCallback(this.m_nRtcInterface, callId);
    }

    public void RegisterRGBAObserver(String callId, ALI_RTC_INTERFACE.AliRenderDataObserver observer) {
        if (observer == null) {
            this.mExternRenderDataObserverMap.remove(callId);
            nativeUnRegisterRGBACallback(this.m_nRtcInterface, callId);
            return;
        }
        this.mExternRenderDataObserverMap.put(callId, observer);
        nativeRegisterRGBACallback(this.m_nRtcInterface, callId);
    }

    public void UnRegisterRGBAObserver(String callId) {
        this.mExternRenderDataObserverMap.remove(callId);
        nativeUnRegisterRGBACallback(this.m_nRtcInterface, callId);
    }

    public void enableBackgroundAudioRecording(boolean enable) {
        nativeEnableBackgroundAudioRecording(this.m_nRtcInterface, enable);
    }

    public boolean isEnableBackgroundAudioRecording() {
        return nativeIsBackgroundAudioRecording(this.m_nRtcInterface);
    }

    public ALI_RTC_INTERFACE.VideoRawDataInterface registerVideoRawDataInterface(final ALI_RTC_INTERFACE.AliRawDataStreamType streamType) {
        final long nativePtr = nativeRegisterVideoRawDataInterface(this.m_nRtcInterface, streamType.ordinal());
        ALI_RTC_INTERFACE.VideoRawDataInterface dataInterface = new ALI_RTC_INTERFACE.VideoRawDataInterface() {
            public int deliverFrame(ALI_RTC_INTERFACE.AliRawDataFrame frame, long timeStam) {
                ALI_RTC_INTERFACE_IMPL ali_rtc_interface_impl = ALI_RTC_INTERFACE_IMPL.this;
                return ali_rtc_interface_impl.nativeDeliverVideoRawDataFrame(ali_rtc_interface_impl.m_nRtcInterface, nativePtr, streamType.ordinal(), frame, timeStam);
            }
        };
        dataInterface.setNativePtr(nativePtr);
        this.mVideoRawDataInterfaces.put(streamType, dataInterface);
        return dataInterface;
    }

    public void unRegisterVideoRawDataInterface(ALI_RTC_INTERFACE.AliRawDataStreamType streamType) {
        ALI_RTC_INTERFACE.VideoRawDataInterface dataInterface = this.mVideoRawDataInterfaces.get(streamType);
        if (dataInterface != null) {
            this.mVideoRawDataInterfaces.remove(streamType);
            nativeUnRegisterVideoRawDataInterface(this.m_nRtcInterface, dataInterface.getNativePtr(), streamType.ordinal());
        }
    }

    public void setTraceId(String traceId) {
        nativeSetTraceId(this.m_nRtcInterface, traceId);
    }

    public String getMediaInfo(String callId, String trackId, String[] keys) {
        return nativeGetMediaInfo(this.m_nRtcInterface, callId, trackId, keys);
    }

    public int startAudioCapture() {
        return nativeEnableBackgroundRecording(this.m_nRtcInterface, true);
    }

    public int stopAudioCapture() {
        return nativeEnableBackgroundRecording(this.m_nRtcInterface, false);
    }

    public int startAudioPlayer() {
        return nativeEnableBackgroundPlayout(this.m_nRtcInterface, true);
    }

    public int stopAudioPlayer() {
        return nativeEnableBackgroundPlayout(this.m_nRtcInterface, false);
    }

    public void setDeviceOrientationMode(ALI_RTC_INTERFACE.Ali_RTC_Device_Orientation_Mode mode) {
        nativeSetDeviceOrientationMode(this.m_nRtcInterface, mode);
    }

    public int StartAudioAccompany(String fileName, boolean localPlay, boolean replaceMic, int loopCycles) {
        return nativeStartAudioAccompany(this.m_nRtcInterface, fileName, localPlay, replaceMic, loopCycles);
    }

    public int StopAudioAccompany() {
        return nativeStopAudioAccompany(this.m_nRtcInterface);
    }

    public int SetAudioAccompanyVolume(int volume) {
        return nativeSetAudioAccompanyVolume(this.m_nRtcInterface, volume);
    }

    public int GetAudioAccompanyVolume() {
        return nativeGetAudioAccompanyVolume(this.m_nRtcInterface);
    }

    public int SetAudioAccompanyPublishVolume(int volume) {
        return nativeSetAudioAccompanyPublishVolume(this.m_nRtcInterface, volume);
    }

    public int GetAudioAccompanyPublishVolume() {
        return nativeGetAudioAccompanyPublishVolume(this.m_nRtcInterface);
    }

    public int SetAudioAccompanyPlayoutVolume(int volume) {
        return nativeSetAudioAccompanyPlayoutVolume(this.m_nRtcInterface, volume);
    }

    public int GetAudioAccompanyPlayoutVolume() {
        return nativeGetAudioAccompanyPlayoutVolume(this.m_nRtcInterface);
    }

    public int PauseAudioMixing() {
        return nativePauseAudioMixing(this.m_nRtcInterface);
    }

    public int ResumeAudioMixing() {
        return nativeResumeAudioMixing(this.m_nRtcInterface);
    }

    public int PreloadAudioEffect(int sound_id, String file_path) {
        return nativePreloadAudioEffect(this.m_nRtcInterface, sound_id, file_path);
    }

    public int UnloadAudioEffect(int sound_id) {
        return nativeUnloadAudioEffect(this.m_nRtcInterface, sound_id);
    }

    public int PlayAudioEffect(int sound_id, String file_path, int cycles, boolean publish) {
        return nativePlayAudioEffect(this.m_nRtcInterface, sound_id, file_path, cycles, publish);
    }

    public int StopAudioEffect(int sound_id) {
        return nativeStopAudioEffect(this.m_nRtcInterface, sound_id);
    }

    public int SetAudioEffectPublishVolume(int sound_id, int volume) {
        return nativeSetAudioEffectPublishVolume(this.m_nRtcInterface, sound_id, volume);
    }

    public int GetAudioEffectPublishVolume(int sound_id) {
        return nativeGetAudioEffectPublishVolume(this.m_nRtcInterface, sound_id);
    }

    public int SetAudioEffectPlayoutVolume(int sound_id, int volume) {
        return nativeSetAudioEffectPlayoutVolume(this.m_nRtcInterface, sound_id, volume);
    }

    public int GetAudioEffectPlayoutVolume(int sound_id) {
        return nativeGetAudioEffectPlayoutVolume(this.m_nRtcInterface, sound_id);
    }

    public int PauseAudioEffect(int sound_id) {
        return nativePauseAudioEffect(this.m_nRtcInterface, sound_id);
    }

    public int ResumeAudioEffect(int sound_id) {
        return nativeResumeAudioEffect(this.m_nRtcInterface, sound_id);
    }

    public int EnableEarBack(boolean enable) {
        return nativeEnableEarBack(this.m_nRtcInterface, enable);
    }

    public int SetEarBackVolume(int volume) {
        return nativeSetEarBackVolume(this.m_nRtcInterface, volume);
    }

    public int SetChannelProfile(ALI_RTC_INTERFACE.AliRTCSDK_Channel_Profile profile) {
        return nativeSetChannelProfile(this.m_nRtcInterface, profile);
    }

    public int SetClientRole(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role role) {
        return nativeSetClientRole(this.m_nRtcInterface, role);
    }

    public int StartAudioFileRecording(String file_Name, int sample_Rate, int quality) {
        return nativeStartAudioFileRecording(this.m_nRtcInterface, file_Name, sample_Rate, quality);
    }

    public int StopAudioFileRecording() {
        return nativeStopAudioFileRecording(this.m_nRtcInterface);
    }

    public int generateTexture() {
        int ret = nativeGenerateTexture(this.m_nRtcInterface);
        String str = this.TAG;
        Log.e(str, "m_nAliRTCInterface--------generateTexture = " + ret);
        return ret;
    }

    public void applicationWillBecomeActive() {
        nativeApplicationWillBecomeActive(this.m_nRtcInterface);
    }

    public void applicationWillResignActive() {
        nativeApplicationWillResignActive(this.m_nRtcInterface);
    }

    public void applicationMicInterrupt() {
        nativeApplicationMicInterrupt(this.m_nRtcInterface);
    }

    public void applicationMicInterruptResume() {
        nativeApplicationMicInterruptResume(this.m_nRtcInterface);
    }
}
