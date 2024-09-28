package com.alivc.rtc;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AppFrontBackHelper;
import com.king.zxing.util.LogUtils;
import com.ut.device.UTDevice;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;
import org.webrtc.ali.ContextUtils;
import org.webrtc.ali.USBMediaDevice;
import org.webrtc.ali.voiceengine.WebRtcAudioUtils;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;
import org.webrtc.alirtcInterface.AliParticipantInfo;
import org.webrtc.alirtcInterface.AliStatusInfo;
import org.webrtc.alirtcInterface.AliSubscriberInfo;
import org.webrtc.alirtcInterface.AliUnPublisherInfo;
import org.webrtc.alirtcInterface.CollectStatusListener;
import org.webrtc.alirtcInterface.ErrorCodes;
import org.webrtc.alirtcInterface.PublisherInfo;
import org.webrtc.alirtcInterface.SophonEngine;
import org.webrtc.alirtcInterface.SophonEventListener;
import org.webrtc.audio.AppRTCAudioManager;
import org.webrtc.sdk.SophonSurfaceView;
import org.webrtc.utils.AlivcLog;

public class AliRtcEngineImpl extends AliRtcEngine implements USBMediaDevice.USBMediaDeviceEvent {
    private static final String FAKE_SESSION = "123456ALIBABAFAKESESSIONID";
    private static final int SDK_RESULT_PUBLISH_ALREADY_EXIST = 16974594;
    private static final String TAG = "AliRTCEngine";
    private static String VERSION = "";
    private static ArrayList<AliRtcEngine.AliRtcRemoteTextureInfo> aliRtcRemoteTextureInfos = new ArrayList<>();
    /* access modifiers changed from: private */
    public AlbumOrientationEventListener mAlbumOrientationEventListener;
    /* access modifiers changed from: private */
    public AliRtcConfig mAliRtcConfig = new AliRtcConfig();
    private volatile boolean mApiPass;
    private AppFrontBackHelper mAppFrontBackHelper;
    /* access modifiers changed from: private */
    public Context mContext;
    private boolean mDetectedUsbDevice;
    /* access modifiers changed from: private */
    public AliRtcEngineEventListener mEventListener;
    private String mExtras = "";
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public AliRtcEngineNotify mNotifyListener;
    /* access modifiers changed from: private */
    public ProcessCpuTracker mProcessCpuTracker;
    /* access modifiers changed from: private */
    public SophonEngine mSophonEngine;
    private SophonEventListener mSophonEventListener;
    /* access modifiers changed from: private */
    public boolean mUsbCameraStatus;
    private AliRtcUsbDeviceEvent mUsbDeviceEvent;
    /* access modifiers changed from: private */
    public Handler mainHandler = new Handler(Looper.getMainLooper());

    static {
        System.loadLibrary("wukong_ua");
    }

    public int onUSBDeviceConnect() {
        try {
            this.mUsbCameraStatus = true;
            if (this.mSophonEngine == null) {
                this.mSophonEngine = SophonEngine.create(this.mContext.getApplicationContext(), this.mExtras, this.mSophonEventListener);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (this.mUsbDeviceEvent != null) {
            AlivcLog.i(TAG, "[API][Callback]onUSBDeviceConnect:ret:" + 0);
            this.mUsbDeviceEvent.onUSBDeviceConnect(0);
            AlivcLog.i(TAG, "[API][End][Callback]onUSBDeviceConnect");
        }
        return 0;
    }

    public void onUSBDeviceDisconnect() {
        if (this.mUsbDeviceEvent != null) {
            AlivcLog.i(TAG, "[API][Callback]onUSBDeviceDisconnect");
            this.mUsbDeviceEvent.onUSBDeviceDisconnect();
            AlivcLog.i(TAG, "[API][End][Callback]onUSBDeviceDisconnect");
        }
    }

    public void onUSBDeviceCancel() {
        try {
            if (this.mSophonEngine == null) {
                this.mSophonEngine = SophonEngine.create(this.mContext.getApplicationContext(), this.mExtras, this.mSophonEventListener);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (this.mUsbDeviceEvent != null) {
            AlivcLog.i(TAG, "[API][Callback]onUSBDeviceCancel");
            this.mUsbDeviceEvent.onUSBDeviceCancel();
            AlivcLog.i(TAG, "[API][End][Callback]onUSBDeviceCancel");
        }
    }

    public void setUsbDeviceEvent(AliRtcUsbDeviceEvent usbDeviceEvent) {
        if (usbDeviceEvent != null) {
            AlivcLog.i(TAG, "[API]setUsbDeviceEvent:" + usbDeviceEvent);
            this.mUsbDeviceEvent = usbDeviceEvent;
            AlivcLog.i(TAG, "[API][End]setUsbDeviceEvent");
        }
    }

    public AliRtcUsbDeviceEvent getUsbDeviceEvent() {
        AlivcLog.i(TAG, "[API]getUsbDeviceEvent");
        return this.mUsbDeviceEvent;
    }

    public boolean isUsbDeviceDetected() {
        AlivcLog.i(TAG, "[API]isUsbDeviceDetected");
        return this.mDetectedUsbDevice;
    }

    public AliRtcEngineImpl(Context context, String extras) {
        boolean z = false;
        this.mUsbCameraStatus = false;
        this.mDetectedUsbDevice = false;
        this.mApiPass = true;
        this.mSophonEventListener = new SophonEventListener() {
            public void onGslbResult(int result) {
                super.onGslbResult(result);
            }

            public void onJoinChannelResult(int result) {
                super.onJoinChannelResult(result);
                if (result == 0) {
                    AliRtcEngineImpl.this.mAliRtcConfig.setInCall(true);
                    if (AliRtcEngineImpl.this.mAliRtcConfig.isAutoPublish()) {
                        AliRtcEngineImpl.this.publish();
                    }
                } else {
                    AliRtcEngineImpl.this.mAliRtcConfig.setInCall(false);
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onJoinChannelResult:result:" + result);
                    AliRtcEngineImpl.this.mEventListener.onJoinChannelResult(result);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onJoinChannelResult");
                }
            }

            public void onLeaveChannelResult(int result) {
                super.onLeaveChannelResult(result);
                synchronized (AliRtcEngineImpl.this.mLock) {
                    AliRtcEngineImpl.this.mLock.notify();
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onLeaveChannelResult:result:" + result);
                    AliRtcEngineImpl.this.mAliRtcConfig.setPublishIsGoing(false);
                    AliRtcEngineImpl.this.mEventListener.onLeaveChannelResult(result);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onLeaveChannelResult");
                }
                if (result != ErrorCodes.SDK_RESULT_WRONG_STATE_ERROR) {
                    AlivcLog.uploadChannelLog();
                }
            }

            public void onPublishResult(int result, String callId) {
                super.onPublishResult(result, callId);
                if (result == 0) {
                    AliRtcEngineImpl.this.mAliRtcConfig.setLocalCallID(callId, true);
                    AliRtcEngineImpl.this.mAliRtcConfig.setPublishIsGoing(false);
                    if (AliRtcEngineImpl.this.mAliRtcConfig.isMuteLocalMic()) {
                        AliRtcEngineImpl.this.muteLocalMic(true);
                    }
                    if (!AliRtcEngineImpl.this.mAliRtcConfig.isAudioOnly() && AliRtcEngineImpl.this.mAliRtcConfig.isMuteLocalCameraVideo()) {
                        AliRtcEngineImpl.this.muteLocalCamera(true, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
                    }
                    AliRtcEngineImpl.this.mainHandler.post(new Runnable() {
                        public void run() {
                            AliRtcEngineImpl.this.enableSpeakerphone(AliRtcEngineImpl.this.mAliRtcConfig.isSpeakerOn());
                        }
                    });
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onPublishResult:result:" + result + "&&callId" + callId);
                    AliRtcEngineImpl.this.mEventListener.onPublishResult(result, callId);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onPublishResult");
                }
            }

            public void onRepublishResult(int result, String callId) {
                super.onRepublishResult(result, callId);
                if (result == 0) {
                    AliRtcEngineImpl.this.mAliRtcConfig.setLocalCallID(callId, true);
                    if (AliRtcEngineImpl.this.mAliRtcConfig.isMuteLocalMic()) {
                        AliRtcEngineImpl.this.muteLocalMic(true);
                    }
                    if (!AliRtcEngineImpl.this.mAliRtcConfig.isAudioOnly() && AliRtcEngineImpl.this.mAliRtcConfig.isMuteLocalCameraVideo()) {
                        AliRtcEngineImpl.this.muteLocalCamera(true, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
                    }
                    AliRtcEngineImpl.this.mainHandler.post(new Runnable() {
                        public void run() {
                            AliRtcEngineImpl.this.enableSpeakerphone(AliRtcEngineImpl.this.mAliRtcConfig.isSpeakerOn());
                        }
                    });
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onPublishResult:result:" + result + "&&callId" + callId);
                    AliRtcEngineImpl.this.mEventListener.onPublishResult(result, callId);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onPublishResult");
                }
            }

            public void onUnpublishResult(int result, String callId) {
                super.onUnpublishResult(result, callId);
                if (result == 0) {
                    AliRtcEngineImpl.this.mAliRtcConfig.setLocalCallID((String) null, true);
                } else {
                    AliRtcEngineImpl.this.mAliRtcConfig.setLocalCallID(AliRtcEngineImpl.this.mAliRtcConfig.getTmpLocalCallID());
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onUnpublishResult:result:" + result + "&&callId" + callId);
                    AliRtcEngineImpl.this.mEventListener.onUnpublishResult(result);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onUnpublishResult");
                }
            }

            public void onSubscribeResult(int result, String callId) {
                super.onSubscribeResult(result, callId);
            }

            public void onResubscribeResult(int result, String callId) {
                super.onResubscribeResult(result, callId);
            }

            public void onUnsubscribeResult(int result, String callId) {
                super.onUnsubscribeResult(result, callId);
                RemoteParticipant remoteParticipant = AliRtcEngineImpl.this.findParticipantByCallID(callId);
                if (remoteParticipant != null) {
                    int count = remoteParticipant.getVideoSubscribed().length;
                    for (int i = 0; i < count; i++) {
                        AliRtcEngineImpl.this.removeRemoteDisplayWindow(callId, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[i]);
                    }
                    remoteParticipant.clearSubedStatus();
                }
                if (AliRtcEngineImpl.this.mEventListener != null && remoteParticipant != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onUnsubscribeResult:result:" + result + "&&userId" + remoteParticipant.getUserID());
                    AliRtcEngineImpl.this.mEventListener.onUnsubscribeResult(result, remoteParticipant.getUserID());
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onUnsubscribeResult");
                }
            }

            public void onParticipantJoinNotify(AliParticipantInfo[] participantList, int feedCount) {
                RemoteParticipant remoteParticipant;
                super.onParticipantJoinNotify(participantList, feedCount);
                for (AliParticipantInfo info : participantList) {
                    if (AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().containsKey(info.getUser_id())) {
                        remoteParticipant = AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().get(info.getUser_id());
                    } else {
                        remoteParticipant = new RemoteParticipant();
                        remoteParticipant.clearAll();
                        if (AliRtcEngineImpl.this.mAliRtcConfig.isAutoSubscribe()) {
                            AliRtcEngineImpl.this.configRemoteParticipantDefaultValues(remoteParticipant);
                        }
                        AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().put(info.getUser_id(), remoteParticipant);
                    }
                    remoteParticipant.setUserID(info.getUser_id());
                    remoteParticipant.setSessionID(info.getSession());
                    remoteParticipant.setDisplayName(info.getUser_name());
                    if (AliRtcEngineImpl.this.mNotifyListener != null) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onRemoteUserOnLineNotify:userId:" + info.getUser_id());
                        AliRtcEngineImpl.this.mNotifyListener.onRemoteUserOnLineNotify(info.getUser_id());
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onRemoteUserOnLineNotify");
                    }
                }
            }

            public void onParticipantLeaveNotify(AliParticipantInfo[] participantList, int feedCount) {
                super.onParticipantLeaveNotify(participantList, feedCount);
                for (int i = 0; i < feedCount; i++) {
                    AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().remove(participantList[i].getUser_id());
                    if (AliRtcEngineImpl.this.mNotifyListener != null) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onRemoteUserOffLineNotify:uid:" + participantList[i].getUser_id());
                        AliRtcEngineImpl.this.mNotifyListener.onRemoteUserOffLineNotify(participantList[i].getUser_id());
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onRemoteUserOffLineNotify");
                    }
                }
            }

            public void onParticipantPublishNotify(PublisherInfo[] publisherList, int publisherCount) {
                RemoteParticipant remoteParticipant;
                String str;
                super.onParticipantPublishNotify(publisherList, publisherCount);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onPublishNotify count: " + publisherCount);
                for (PublisherInfo info : publisherList) {
                    if (AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().containsKey(info.getUser_id())) {
                        remoteParticipant = AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().get(info.getUser_id());
                    } else {
                        remoteParticipant = new RemoteParticipant();
                        remoteParticipant.clearAll();
                        if (AliRtcEngineImpl.this.mAliRtcConfig.isAutoSubscribe()) {
                            AliRtcEngineImpl.this.configRemoteParticipantDefaultValues(remoteParticipant);
                        }
                        AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().put(info.getUser_id(), remoteParticipant);
                    }
                    remoteParticipant.setCallID(info.getCall_id());
                    remoteParticipant.setUserID(info.getUser_id());
                    if (TextUtils.isEmpty(info.getSession())) {
                        str = AliRtcEngineImpl.FAKE_SESSION;
                    } else {
                        str = info.getSession();
                    }
                    remoteParticipant.setSessionID(str);
                    remoteParticipant.setDisplayName(info.getDisplay());
                    remoteParticipant.setStreamLabel(info.getStream_label());
                    remoteParticipant.setAudioTrackLabel(info.getAudio_track_label());
                    remoteParticipant.setVideoTrackLabels(info.getVideo_track_labels());
                    if (TextUtils.isEmpty(info.getCall_id())) {
                        AlivcLog.e(AliRtcEngineImpl.TAG, "onParticipantPublishNotify callid is null, uid = " + info.getUser_id());
                    } else {
                        AliRtcEngineImpl.this.mAliRtcConfig.getRemotePublishParticipants().put(info.getCall_id(), remoteParticipant);
                    }
                    AliRtcEngineImpl.this.removeRemoteNullTracksDisplayWindow(info.getCall_id(), info.video_track_labels);
                    if (AliRtcEngineImpl.this.mNotifyListener != null) {
                        AliRtcEngine.AliRtcAudioTrack at = RemoteParticipant.getAudioTrack(info.getAudio_track_label());
                        AliRtcEngine.AliRtcVideoTrack vt = RemoteParticipant.getVideoTrack(info.getVideo_track_labels());
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onRemoteTrackAvailableNotify:userid: " + remoteParticipant.getUserID() + "&&audioTrack: " + at + "&&videoTrack: " + vt);
                        AliRtcEngineImpl.this.mNotifyListener.onRemoteTrackAvailableNotify(remoteParticipant.getUserID(), at, vt);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onRemoteTrackAvailableNotify");
                    }
                    if (AliRtcEngineImpl.this.mAliRtcConfig.isAutoSubscribe()) {
                        AliRtcEngineImpl.this.configRemoteAudio(remoteParticipant.getUserID(), remoteParticipant.isUcAudeoSubed());
                        AliRtcEngineImpl.this.configRemoteCameraTrack(remoteParticipant.getUserID(), remoteParticipant.isUcVideoSubedMaster(), remoteParticipant.isUcVideoSubed());
                        AliRtcEngineImpl.this.configRemoteScreenTrack(remoteParticipant.getUserID(), remoteParticipant.isUcScreenSubed());
                        AliRtcEngineImpl.this.subscribe(remoteParticipant.getUserID());
                    }
                }
            }

            public void onParticipantUnpublishNotify(AliUnPublisherInfo[] unpublisherList, int feedCount) {
                super.onParticipantUnpublishNotify(unpublisherList, feedCount);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onParticipantUnpublishNotify count: " + feedCount);
                if (unpublisherList == null) {
                    AlivcLog.e(AliRtcEngineImpl.TAG, "onParticipantUnpublishNotify unpublisherList is null ");
                    return;
                }
                for (int i = 0; i < feedCount; i++) {
                    RemoteParticipant remoteParticipant = null;
                    if (i < unpublisherList.length && unpublisherList[i] != null && AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().containsKey(unpublisherList[i].getUser_id())) {
                        remoteParticipant = AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().get(unpublisherList[i].getUser_id());
                    }
                    if (remoteParticipant != null) {
                        int count = remoteParticipant.getVideoTrackLabels().length;
                        for (int j = 0; j < count; j++) {
                            AliRtcEngineImpl.this.removeRemoteDisplayWindow(remoteParticipant.getCallID(), ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[j]);
                        }
                        remoteParticipant.clearStreams();
                        remoteParticipant.setFirstSubscribe(true);
                        if (unpublisherList[i] == null || TextUtils.isEmpty(unpublisherList[i].getCall_id())) {
                            AlivcLog.i(AliRtcEngineImpl.TAG, "onParticipantUnpublishNotify callid is null");
                        } else {
                            AliRtcEngineImpl.this.mAliRtcConfig.getRemotePublishParticipants().remove(unpublisherList[i].getCall_id());
                        }
                    }
                    if (!(AliRtcEngineImpl.this.mNotifyListener == null || remoteParticipant == null)) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onRemoteUserUnPublish:rtcEngine" + AliRtcEngineImpl.this + "userid: " + remoteParticipant.getUserID());
                        AliRtcEngineImpl.this.mNotifyListener.onRemoteUserUnPublish(AliRtcEngineImpl.this, remoteParticipant.getUserID());
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onRemoteUserUnPublish");
                    }
                }
            }

            public void onParticipantSubscribeNotify(AliSubscriberInfo[] subcribeinfoList, int feedCount) {
                super.onParticipantSubscribeNotify(subcribeinfoList, feedCount);
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onParticipantSubscribeNotify:AliSubscriberInfo" + subcribeinfoList[0].user_id + "feedCount: " + feedCount);
                    AliRtcEngineImpl.this.mNotifyListener.onParticipantSubscribeNotify(subcribeinfoList, feedCount);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onParticipantSubscribeNotify");
                }
            }

            public void onParticipantUnsubscribeNotify(AliParticipantInfo[] participantList, int feedCount) {
                super.onParticipantUnsubscribeNotify(participantList, feedCount);
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onParticipantUnsubscribeNotify:AliParticipantInfo" + participantList[0].user_id + "feedCount: " + feedCount);
                    AliRtcEngineImpl.this.mNotifyListener.onParticipantUnsubscribeNotify(participantList, feedCount);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onParticipantUnsubscribeNotify");
                }
            }

            public void onParticipantStatusNotify(AliStatusInfo[] status_info_list, int count) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onParticipantUnsubscribeNotify:AliStatusInfo" + status_info_list[0].user_id + "&&count: " + count);
                    AliRtcEngineImpl.this.mNotifyListener.onParticipantStatusNotify(status_info_list, count);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onParticipantUnsubscribeNotify");
                }
            }

            public void onAliRtcStats(ALI_RTC_INTERFACE.AliRtcStats aliRtcStats) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onAliRtcStats:AliRtcStats:" + aliRtcStats.toString());
                    if (AliRtcEngineImpl.this.mProcessCpuTracker != null) {
                        AliRtcEngineImpl.this.mProcessCpuTracker.updateCpuUsages(AliRtcEngineImpl.this.mContext.getApplicationContext());
                        aliRtcStats.setCpu_usage((float) AliRtcEngineImpl.this.mProcessCpuTracker.getMyPicCpuPercent());
                        aliRtcStats.setSystem_cpu_usage((float) AliRtcEngineImpl.this.mProcessCpuTracker.getTotalSysCpuPercent());
                    }
                    AliRtcEngineImpl.this.mNotifyListener.onAliRtcStats(aliRtcStats);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onAliRtcStats:");
                }
            }

            public void onSubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
                super.onSubscribeResult2(result, callID, reqConfig, curConfig);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onSubscribeResult2 result: " + result + " callID: " + callID + "reqConfig: " + AliRtcEngineImpl.this.getSubscribeConfigString(reqConfig) + "curConfig: " + AliRtcEngineImpl.this.getSubscribeConfigString(curConfig));
                RemoteParticipant remoteParticipant = AliRtcEngineImpl.this.findParticipantByCallID(callID);
                if (remoteParticipant != null) {
                    if (result == 0) {
                        int count = curConfig.video_track_labels.length;
                        for (int i = 0; i < count; i++) {
                            if (!TextUtils.isEmpty(curConfig.video_track_labels[i])) {
                                AliRtcEngineImpl.this.addRemoteDisplayWindow(callID, remoteParticipant.getVideoCanvas(i), ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[i]);
                            }
                        }
                        remoteParticipant.setAudioSubscribed(curConfig.audio_track_label);
                        remoteParticipant.setVideoSubscribed(curConfig.video_track_labels);
                        remoteParticipant.setVideoSubscribedCached(curConfig.video_track_labels);
                        remoteParticipant.setFirstSubscribe(false);
                        AliRtcEngineImpl.this.mSophonEngine.enableRemoteAudio(callID, !remoteParticipant.isMuteAudioPlaying());
                    }
                    if (AliRtcEngineImpl.this.mEventListener != null) {
                        AliRtcEngine.AliRtcAudioTrack at = RemoteParticipant.getAudioTrack(curConfig.audio_track_label);
                        AliRtcEngine.AliRtcVideoTrack vt = RemoteParticipant.getVideoTrack(curConfig.video_track_labels);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onSubscribeResult:userID:" + remoteParticipant.getUserID() + "&&result: " + result + "&&VideoTrack: " + vt + "&&AudioTrack: " + at);
                        AliRtcEngineImpl.this.mEventListener.onSubscribeResult(remoteParticipant.getUserID(), result, vt, at);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onSubscribeResult");
                    }
                }
            }

            public void onResubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
                super.onResubscribeResult2(result, callID, reqConfig, curConfig);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onReSubscribeResult2:result:" + result + "&&callID: " + callID + "&&reqConfig: " + AliRtcEngineImpl.this.getSubscribeConfigString(reqConfig) + "&&curConfig: " + AliRtcEngineImpl.this.getSubscribeConfigString(curConfig));
                RemoteParticipant remoteParticipant = AliRtcEngineImpl.this.findParticipantByCallID(callID);
                if (remoteParticipant != null) {
                    if (result == 0) {
                        int count = curConfig.video_track_labels.length;
                        for (int i = 0; i < count; i++) {
                            if (TextUtils.isEmpty(curConfig.video_track_labels[i]) && !TextUtils.isEmpty(remoteParticipant.getVideoSubscribedCached()[i])) {
                                AliRtcEngineImpl.this.removeRemoteDisplayWindow(callID, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[i]);
                            }
                        }
                        int count2 = curConfig.video_track_labels.length;
                        for (int i2 = 0; i2 < count2; i2++) {
                            if (!TextUtils.isEmpty(curConfig.video_track_labels[i2]) && TextUtils.isEmpty(remoteParticipant.getVideoSubscribedCached()[i2])) {
                                AliRtcEngineImpl.this.addRemoteDisplayWindow(callID, remoteParticipant.getVideoCanvas(i2), ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[i2]);
                            }
                        }
                        remoteParticipant.setAudioSubscribed(curConfig.audio_track_label);
                        remoteParticipant.setVideoSubscribed(curConfig.video_track_labels);
                        remoteParticipant.setVideoSubscribedCached(curConfig.video_track_labels);
                        AliRtcEngineImpl.this.mSophonEngine.enableRemoteAudio(callID, !remoteParticipant.isMuteAudioPlaying());
                    }
                    if (AliRtcEngineImpl.this.mEventListener != null) {
                        AliRtcEngine.AliRtcAudioTrack at = RemoteParticipant.getAudioTrack(curConfig.audio_track_label);
                        AliRtcEngine.AliRtcVideoTrack vt = RemoteParticipant.getVideoTrack(curConfig.video_track_labels);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onSubscribeResult:userID:" + remoteParticipant.getUserID() + "&&result: " + result + "&&VideoTrack: " + vt + "&&AudioTrack: " + at);
                        AliRtcEngineImpl.this.mEventListener.onSubscribeResult(remoteParticipant.getUserID(), result, vt, at);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onSubscribeResult");
                    }
                }
            }

            public void onChannelReleaseNotify() {
                super.onChannelReleaseNotify();
                AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onChannelReleaseNotify");
            }

            public void onConnectionChange(int mediaConState) {
                super.onConnectionChange(mediaConState);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onConnectionChange:mediaConState: " + mediaConState);
                ALI_RTC_INTERFACE.AliRTCMediaConnectionReConnectState state = ALI_RTC_INTERFACE.AliRTCMediaConnectionReConnectState.fromNativeIndex(mediaConState);
                if (state != null && AliRtcEngineImpl.this.mEventListener != null) {
                    int i = AnonymousClass7.$SwitchMap$org$webrtc$alirtcInterface$ALI_RTC_INTERFACE$AliRTCMediaConnectionReConnectState[state.ordinal()];
                    if (i == 1) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onConnectionLost");
                        AliRtcEngineImpl.this.mEventListener.onConnectionLost();
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onConnectionLost");
                    } else if (i == 2) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onConnectionRecovery");
                        AliRtcEngineImpl.this.mEventListener.onConnectionRecovery();
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onConnectionRecovery");
                    } else if (i == 3) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onTryToReconnect");
                        AliRtcEngineImpl.this.mEventListener.onTryToReconnect();
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onTryToReconnect");
                    }
                }
            }

            public void onWarning(int warningEvent, String params) {
                super.onWarning(warningEvent, params);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onWarning event: " + warningEvent);
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onOccurWarning: warningEvent:" + warningEvent + "&&params" + params);
                    AliRtcEngineImpl.this.mEventListener.onOccurWarning(warningEvent);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onOccurWarning");
                }
            }

            public void onError(int event, String params) {
                super.onError(event, params);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onError event: " + event);
                if (AliRtcEngineImpl.this.mEventListener == null) {
                    return;
                }
                if (event == 17170689) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onConnectionLost");
                    AliRtcEngineImpl.this.mEventListener.onConnectionLost();
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onConnectionLost");
                } else if (event == 17170690) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onConnectionRecovery");
                    AliRtcEngineImpl.this.mEventListener.onConnectionRecovery();
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onConnectionRecovery");
                } else {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onOccurError");
                    AliRtcEngineImpl.this.mEventListener.onOccurError(event);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onOccurError");
                }
            }

            public void onTransportStatusChange(String callId, ALI_RTC_INTERFACE.TransportType event, ALI_RTC_INTERFACE.TransportStatus status) {
                super.onTransportStatusChange(callId, event, status);
            }

            public void onNetworkQualityChange(ArrayList<ALI_RTC_INTERFACE.AliTransportInfo> network_quality) {
                AliRtcEngine.AliRtcNetworkQuality upQuality;
                AliRtcEngine.AliRtcNetworkQuality downQuality;
                super.onNetworkQualityChange(network_quality);
                if (AliRtcEngineImpl.this.mEventListener != null && network_quality != null && !network_quality.isEmpty()) {
                    int count = network_quality.size();
                    for (int i = 0; i < count; i++) {
                        ALI_RTC_INTERFACE.AliTransportInfo info = network_quality.get(i);
                        if (info != null) {
                            try {
                                upQuality = AliRtcEngine.AliRtcNetworkQuality.values()[info.upQuality.getValue()];
                            } catch (Exception e) {
                                upQuality = AliRtcEngine.AliRtcNetworkQuality.Network_Unknow;
                            }
                            try {
                                downQuality = AliRtcEngine.AliRtcNetworkQuality.values()[info.downQuality.getValue()];
                            } catch (Exception e2) {
                                downQuality = AliRtcEngine.AliRtcNetworkQuality.Network_Unknow;
                            }
                            AliRtcEngineImpl.this.mEventListener.onNetworkQualityChanged(info.user_id, upQuality, downQuality);
                        }
                    }
                }
            }

            public void onLogMessage(String message) {
                super.onLogMessage(message);
            }

            public void onMessage(String tid, String contentType, String content) {
                super.onMessage(tid, contentType, content);
            }

            public void onBye(int code) {
                super.onBye(code);
                AliRtcConfig unused = AliRtcEngineImpl.this.mAliRtcConfig = new AliRtcConfig();
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onBye: code:" + code);
                    AliRtcEngineImpl.this.mNotifyListener.onBye(code);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onBye");
                }
            }

            public void onUplinkChannelMessage(int result, String contentType, String content) {
                super.onUplinkChannelMessage(result, contentType, content);
            }

            public String onCollectPlatformProfile() {
                return AliRtcEngineImpl.this.getOsInfo();
            }

            public String onFetchPerformanceInfo() {
                JSONObject jsonObject = new JSONObject();
                int cpuPercent = 0;
                try {
                    if (AliRtcEngineImpl.this.mProcessCpuTracker != null) {
                        AliRtcEngineImpl.this.mProcessCpuTracker.updateCpuUsages(AliRtcEngineImpl.this.mContext.getApplicationContext());
                        cpuPercent = AliRtcEngineImpl.this.mProcessCpuTracker.getMyPicCpuPercent();
                    }
                    jsonObject.putOpt("cpu_usage", String.valueOf(cpuPercent));
                    jsonObject.putOpt("mem_usage", String.valueOf(AliRtcEngineUtil.getRunningAppProcessInfo(AliRtcEngineImpl.this.mContext.getApplicationContext())));
                } catch (Exception e) {
                }
                return jsonObject.toString();
            }

            public boolean onFetchAudioPermissionInfo() {
                if (AliRtcEngineImpl.this.mContext == null) {
                    return false;
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    return WebRtcAudioUtils.hasPermission(AliRtcEngineImpl.this.mContext.getApplicationContext(), "android.permission.RECORD_AUDIO");
                }
                return true;
            }

            public String onFetchAudioDeviceInfo() {
                JSONObject jsonObject = new JSONObject();
                int portType = 0;
                try {
                    if (AliRtcEngineImpl.this.mUsbCameraStatus) {
                        portType = 2;
                    } else if (AliRtcEngineImpl.this.mSophonEngine != null) {
                        portType = AliRtcEngineImpl.this.mSophonEngine.getSelectAudioDevice() == AppRTCAudioManager.AudioDevice.BLUETOOTH ? 1 : 0;
                    }
                    jsonObject.putOpt("AudioPortType", Integer.valueOf(portType));
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onFetchAudioDeviceInfo: CurrentPort type: " + portType);
                } catch (Exception e) {
                    AlivcLog.e(AliRtcEngineImpl.TAG, "[API] [Callback]onFetchAudioDeviceInfo Error");
                }
                return jsonObject.toString();
            }

            public void onWindowRenderReady(String callId, int videoType) {
                super.onWindowRenderReady(callId, videoType);
            }

            public void onUpdateRoleNotify(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role old_role, ALI_RTC_INTERFACE.AliRTCSDK_Client_Role new_role) {
                super.onUpdateRoleNotify(old_role, new_role);
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onUpdateRoleNotify: old_role:" + old_role + "&&new_role:" + new_role);
                    AliRtcEngineImpl.this.mEventListener.onUpdateRoleNotify(old_role, new_role);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onUpdateRoleNotify");
                }
            }

            public void onFirstFramereceived(String callId, String stream_label, String track_label, int time_cost_ms) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onFirstFramereceived: callId:" + callId + "&&stream_label:" + stream_label + "&&time_cost_ms:" + time_cost_ms);
                    AliRtcEngineImpl.this.mNotifyListener.onFirstFramereceived(callId, stream_label, track_label, time_cost_ms);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onFirstFramereceived");
                }
            }

            public void onFirstPacketSent(String callId, String stream_label, String track_label, int time_cost_ms) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onFirstPacketSent: callId:" + callId + "&&stream_label:" + stream_label + "&&track_label:" + track_label + "&&time_cost_ms:" + time_cost_ms);
                    AliRtcEngineImpl.this.mNotifyListener.onFirstPacketSent(callId, stream_label, track_label, time_cost_ms);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onFirstPacketSent");
                }
            }

            public void onFirstPacketReceived(String callId, String stream_label, String track_label, int time_cost_ms) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onFirstPacketReceived: callId:" + callId + "&&stream_label:" + stream_label + "&&track_label:" + track_label + "&&time_cost_ms:" + time_cost_ms);
                    AliRtcEngineImpl.this.mNotifyListener.onFirstPacketReceived(callId, stream_label, track_label, time_cost_ms);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onFirstPacketReceived");
                }
            }

            public int onFetchDeviceOrientation() {
                if (AliRtcEngineImpl.this.mAlbumOrientationEventListener == null) {
                    return super.onFetchDeviceOrientation();
                }
                AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]getOrientation");
                int retOrientation = AliRtcEngineImpl.this.mAlbumOrientationEventListener.getOrientation();
                AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]getOrientation");
                return retOrientation;
            }
        };
        try {
            if (!TextUtils.isEmpty(extras)) {
                this.mExtras = extras;
            }
            this.mContext = context;
            ContextUtils.initialize(context.getApplicationContext());
            initProcessCpuTracker();
            LogWhenGoBackOrFront((Application) ContextUtils.getApplicationContext(), true);
            enableOrientation(ContextUtils.getApplicationContext(), true);
            DeviceConfig.initConfig();
            SophonEngine.setH5CompatibleMode(EnableH5Compatible);
            this.mDetectedUsbDevice = SophonEngine.setupUSBDevice(context, this) != 0 ? true : z;
            AlivcLog.i(TAG, "AliRtcEngine init mDetectedUsbDevice = " + this.mDetectedUsbDevice);
            if (!this.mDetectedUsbDevice) {
                SophonEngine.closeUSBDevice();
                if (this.mSophonEngine == null) {
                    this.mSophonEngine = SophonEngine.create(context.getApplicationContext(), this.mExtras, this.mSophonEventListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AliRtcEngineImpl(Context context) {
        boolean z = false;
        this.mUsbCameraStatus = false;
        this.mDetectedUsbDevice = false;
        this.mApiPass = true;
        this.mSophonEventListener = new SophonEventListener() {
            public void onGslbResult(int result) {
                super.onGslbResult(result);
            }

            public void onJoinChannelResult(int result) {
                super.onJoinChannelResult(result);
                if (result == 0) {
                    AliRtcEngineImpl.this.mAliRtcConfig.setInCall(true);
                    if (AliRtcEngineImpl.this.mAliRtcConfig.isAutoPublish()) {
                        AliRtcEngineImpl.this.publish();
                    }
                } else {
                    AliRtcEngineImpl.this.mAliRtcConfig.setInCall(false);
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onJoinChannelResult:result:" + result);
                    AliRtcEngineImpl.this.mEventListener.onJoinChannelResult(result);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onJoinChannelResult");
                }
            }

            public void onLeaveChannelResult(int result) {
                super.onLeaveChannelResult(result);
                synchronized (AliRtcEngineImpl.this.mLock) {
                    AliRtcEngineImpl.this.mLock.notify();
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onLeaveChannelResult:result:" + result);
                    AliRtcEngineImpl.this.mAliRtcConfig.setPublishIsGoing(false);
                    AliRtcEngineImpl.this.mEventListener.onLeaveChannelResult(result);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onLeaveChannelResult");
                }
                if (result != ErrorCodes.SDK_RESULT_WRONG_STATE_ERROR) {
                    AlivcLog.uploadChannelLog();
                }
            }

            public void onPublishResult(int result, String callId) {
                super.onPublishResult(result, callId);
                if (result == 0) {
                    AliRtcEngineImpl.this.mAliRtcConfig.setLocalCallID(callId, true);
                    AliRtcEngineImpl.this.mAliRtcConfig.setPublishIsGoing(false);
                    if (AliRtcEngineImpl.this.mAliRtcConfig.isMuteLocalMic()) {
                        AliRtcEngineImpl.this.muteLocalMic(true);
                    }
                    if (!AliRtcEngineImpl.this.mAliRtcConfig.isAudioOnly() && AliRtcEngineImpl.this.mAliRtcConfig.isMuteLocalCameraVideo()) {
                        AliRtcEngineImpl.this.muteLocalCamera(true, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
                    }
                    AliRtcEngineImpl.this.mainHandler.post(new Runnable() {
                        public void run() {
                            AliRtcEngineImpl.this.enableSpeakerphone(AliRtcEngineImpl.this.mAliRtcConfig.isSpeakerOn());
                        }
                    });
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onPublishResult:result:" + result + "&&callId" + callId);
                    AliRtcEngineImpl.this.mEventListener.onPublishResult(result, callId);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onPublishResult");
                }
            }

            public void onRepublishResult(int result, String callId) {
                super.onRepublishResult(result, callId);
                if (result == 0) {
                    AliRtcEngineImpl.this.mAliRtcConfig.setLocalCallID(callId, true);
                    if (AliRtcEngineImpl.this.mAliRtcConfig.isMuteLocalMic()) {
                        AliRtcEngineImpl.this.muteLocalMic(true);
                    }
                    if (!AliRtcEngineImpl.this.mAliRtcConfig.isAudioOnly() && AliRtcEngineImpl.this.mAliRtcConfig.isMuteLocalCameraVideo()) {
                        AliRtcEngineImpl.this.muteLocalCamera(true, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
                    }
                    AliRtcEngineImpl.this.mainHandler.post(new Runnable() {
                        public void run() {
                            AliRtcEngineImpl.this.enableSpeakerphone(AliRtcEngineImpl.this.mAliRtcConfig.isSpeakerOn());
                        }
                    });
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onPublishResult:result:" + result + "&&callId" + callId);
                    AliRtcEngineImpl.this.mEventListener.onPublishResult(result, callId);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onPublishResult");
                }
            }

            public void onUnpublishResult(int result, String callId) {
                super.onUnpublishResult(result, callId);
                if (result == 0) {
                    AliRtcEngineImpl.this.mAliRtcConfig.setLocalCallID((String) null, true);
                } else {
                    AliRtcEngineImpl.this.mAliRtcConfig.setLocalCallID(AliRtcEngineImpl.this.mAliRtcConfig.getTmpLocalCallID());
                }
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onUnpublishResult:result:" + result + "&&callId" + callId);
                    AliRtcEngineImpl.this.mEventListener.onUnpublishResult(result);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onUnpublishResult");
                }
            }

            public void onSubscribeResult(int result, String callId) {
                super.onSubscribeResult(result, callId);
            }

            public void onResubscribeResult(int result, String callId) {
                super.onResubscribeResult(result, callId);
            }

            public void onUnsubscribeResult(int result, String callId) {
                super.onUnsubscribeResult(result, callId);
                RemoteParticipant remoteParticipant = AliRtcEngineImpl.this.findParticipantByCallID(callId);
                if (remoteParticipant != null) {
                    int count = remoteParticipant.getVideoSubscribed().length;
                    for (int i = 0; i < count; i++) {
                        AliRtcEngineImpl.this.removeRemoteDisplayWindow(callId, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[i]);
                    }
                    remoteParticipant.clearSubedStatus();
                }
                if (AliRtcEngineImpl.this.mEventListener != null && remoteParticipant != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onUnsubscribeResult:result:" + result + "&&userId" + remoteParticipant.getUserID());
                    AliRtcEngineImpl.this.mEventListener.onUnsubscribeResult(result, remoteParticipant.getUserID());
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onUnsubscribeResult");
                }
            }

            public void onParticipantJoinNotify(AliParticipantInfo[] participantList, int feedCount) {
                RemoteParticipant remoteParticipant;
                super.onParticipantJoinNotify(participantList, feedCount);
                for (AliParticipantInfo info : participantList) {
                    if (AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().containsKey(info.getUser_id())) {
                        remoteParticipant = AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().get(info.getUser_id());
                    } else {
                        remoteParticipant = new RemoteParticipant();
                        remoteParticipant.clearAll();
                        if (AliRtcEngineImpl.this.mAliRtcConfig.isAutoSubscribe()) {
                            AliRtcEngineImpl.this.configRemoteParticipantDefaultValues(remoteParticipant);
                        }
                        AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().put(info.getUser_id(), remoteParticipant);
                    }
                    remoteParticipant.setUserID(info.getUser_id());
                    remoteParticipant.setSessionID(info.getSession());
                    remoteParticipant.setDisplayName(info.getUser_name());
                    if (AliRtcEngineImpl.this.mNotifyListener != null) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onRemoteUserOnLineNotify:userId:" + info.getUser_id());
                        AliRtcEngineImpl.this.mNotifyListener.onRemoteUserOnLineNotify(info.getUser_id());
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onRemoteUserOnLineNotify");
                    }
                }
            }

            public void onParticipantLeaveNotify(AliParticipantInfo[] participantList, int feedCount) {
                super.onParticipantLeaveNotify(participantList, feedCount);
                for (int i = 0; i < feedCount; i++) {
                    AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().remove(participantList[i].getUser_id());
                    if (AliRtcEngineImpl.this.mNotifyListener != null) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onRemoteUserOffLineNotify:uid:" + participantList[i].getUser_id());
                        AliRtcEngineImpl.this.mNotifyListener.onRemoteUserOffLineNotify(participantList[i].getUser_id());
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onRemoteUserOffLineNotify");
                    }
                }
            }

            public void onParticipantPublishNotify(PublisherInfo[] publisherList, int publisherCount) {
                RemoteParticipant remoteParticipant;
                String str;
                super.onParticipantPublishNotify(publisherList, publisherCount);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onPublishNotify count: " + publisherCount);
                for (PublisherInfo info : publisherList) {
                    if (AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().containsKey(info.getUser_id())) {
                        remoteParticipant = AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().get(info.getUser_id());
                    } else {
                        remoteParticipant = new RemoteParticipant();
                        remoteParticipant.clearAll();
                        if (AliRtcEngineImpl.this.mAliRtcConfig.isAutoSubscribe()) {
                            AliRtcEngineImpl.this.configRemoteParticipantDefaultValues(remoteParticipant);
                        }
                        AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().put(info.getUser_id(), remoteParticipant);
                    }
                    remoteParticipant.setCallID(info.getCall_id());
                    remoteParticipant.setUserID(info.getUser_id());
                    if (TextUtils.isEmpty(info.getSession())) {
                        str = AliRtcEngineImpl.FAKE_SESSION;
                    } else {
                        str = info.getSession();
                    }
                    remoteParticipant.setSessionID(str);
                    remoteParticipant.setDisplayName(info.getDisplay());
                    remoteParticipant.setStreamLabel(info.getStream_label());
                    remoteParticipant.setAudioTrackLabel(info.getAudio_track_label());
                    remoteParticipant.setVideoTrackLabels(info.getVideo_track_labels());
                    if (TextUtils.isEmpty(info.getCall_id())) {
                        AlivcLog.e(AliRtcEngineImpl.TAG, "onParticipantPublishNotify callid is null, uid = " + info.getUser_id());
                    } else {
                        AliRtcEngineImpl.this.mAliRtcConfig.getRemotePublishParticipants().put(info.getCall_id(), remoteParticipant);
                    }
                    AliRtcEngineImpl.this.removeRemoteNullTracksDisplayWindow(info.getCall_id(), info.video_track_labels);
                    if (AliRtcEngineImpl.this.mNotifyListener != null) {
                        AliRtcEngine.AliRtcAudioTrack at = RemoteParticipant.getAudioTrack(info.getAudio_track_label());
                        AliRtcEngine.AliRtcVideoTrack vt = RemoteParticipant.getVideoTrack(info.getVideo_track_labels());
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onRemoteTrackAvailableNotify:userid: " + remoteParticipant.getUserID() + "&&audioTrack: " + at + "&&videoTrack: " + vt);
                        AliRtcEngineImpl.this.mNotifyListener.onRemoteTrackAvailableNotify(remoteParticipant.getUserID(), at, vt);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onRemoteTrackAvailableNotify");
                    }
                    if (AliRtcEngineImpl.this.mAliRtcConfig.isAutoSubscribe()) {
                        AliRtcEngineImpl.this.configRemoteAudio(remoteParticipant.getUserID(), remoteParticipant.isUcAudeoSubed());
                        AliRtcEngineImpl.this.configRemoteCameraTrack(remoteParticipant.getUserID(), remoteParticipant.isUcVideoSubedMaster(), remoteParticipant.isUcVideoSubed());
                        AliRtcEngineImpl.this.configRemoteScreenTrack(remoteParticipant.getUserID(), remoteParticipant.isUcScreenSubed());
                        AliRtcEngineImpl.this.subscribe(remoteParticipant.getUserID());
                    }
                }
            }

            public void onParticipantUnpublishNotify(AliUnPublisherInfo[] unpublisherList, int feedCount) {
                super.onParticipantUnpublishNotify(unpublisherList, feedCount);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onParticipantUnpublishNotify count: " + feedCount);
                if (unpublisherList == null) {
                    AlivcLog.e(AliRtcEngineImpl.TAG, "onParticipantUnpublishNotify unpublisherList is null ");
                    return;
                }
                for (int i = 0; i < feedCount; i++) {
                    RemoteParticipant remoteParticipant = null;
                    if (i < unpublisherList.length && unpublisherList[i] != null && AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().containsKey(unpublisherList[i].getUser_id())) {
                        remoteParticipant = AliRtcEngineImpl.this.mAliRtcConfig.getRemoteParticipants().get(unpublisherList[i].getUser_id());
                    }
                    if (remoteParticipant != null) {
                        int count = remoteParticipant.getVideoTrackLabels().length;
                        for (int j = 0; j < count; j++) {
                            AliRtcEngineImpl.this.removeRemoteDisplayWindow(remoteParticipant.getCallID(), ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[j]);
                        }
                        remoteParticipant.clearStreams();
                        remoteParticipant.setFirstSubscribe(true);
                        if (unpublisherList[i] == null || TextUtils.isEmpty(unpublisherList[i].getCall_id())) {
                            AlivcLog.i(AliRtcEngineImpl.TAG, "onParticipantUnpublishNotify callid is null");
                        } else {
                            AliRtcEngineImpl.this.mAliRtcConfig.getRemotePublishParticipants().remove(unpublisherList[i].getCall_id());
                        }
                    }
                    if (!(AliRtcEngineImpl.this.mNotifyListener == null || remoteParticipant == null)) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onRemoteUserUnPublish:rtcEngine" + AliRtcEngineImpl.this + "userid: " + remoteParticipant.getUserID());
                        AliRtcEngineImpl.this.mNotifyListener.onRemoteUserUnPublish(AliRtcEngineImpl.this, remoteParticipant.getUserID());
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onRemoteUserUnPublish");
                    }
                }
            }

            public void onParticipantSubscribeNotify(AliSubscriberInfo[] subcribeinfoList, int feedCount) {
                super.onParticipantSubscribeNotify(subcribeinfoList, feedCount);
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onParticipantSubscribeNotify:AliSubscriberInfo" + subcribeinfoList[0].user_id + "feedCount: " + feedCount);
                    AliRtcEngineImpl.this.mNotifyListener.onParticipantSubscribeNotify(subcribeinfoList, feedCount);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onParticipantSubscribeNotify");
                }
            }

            public void onParticipantUnsubscribeNotify(AliParticipantInfo[] participantList, int feedCount) {
                super.onParticipantUnsubscribeNotify(participantList, feedCount);
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onParticipantUnsubscribeNotify:AliParticipantInfo" + participantList[0].user_id + "feedCount: " + feedCount);
                    AliRtcEngineImpl.this.mNotifyListener.onParticipantUnsubscribeNotify(participantList, feedCount);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onParticipantUnsubscribeNotify");
                }
            }

            public void onParticipantStatusNotify(AliStatusInfo[] status_info_list, int count) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onParticipantUnsubscribeNotify:AliStatusInfo" + status_info_list[0].user_id + "&&count: " + count);
                    AliRtcEngineImpl.this.mNotifyListener.onParticipantStatusNotify(status_info_list, count);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onParticipantUnsubscribeNotify");
                }
            }

            public void onAliRtcStats(ALI_RTC_INTERFACE.AliRtcStats aliRtcStats) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onAliRtcStats:AliRtcStats:" + aliRtcStats.toString());
                    if (AliRtcEngineImpl.this.mProcessCpuTracker != null) {
                        AliRtcEngineImpl.this.mProcessCpuTracker.updateCpuUsages(AliRtcEngineImpl.this.mContext.getApplicationContext());
                        aliRtcStats.setCpu_usage((float) AliRtcEngineImpl.this.mProcessCpuTracker.getMyPicCpuPercent());
                        aliRtcStats.setSystem_cpu_usage((float) AliRtcEngineImpl.this.mProcessCpuTracker.getTotalSysCpuPercent());
                    }
                    AliRtcEngineImpl.this.mNotifyListener.onAliRtcStats(aliRtcStats);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onAliRtcStats:");
                }
            }

            public void onSubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
                super.onSubscribeResult2(result, callID, reqConfig, curConfig);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onSubscribeResult2 result: " + result + " callID: " + callID + "reqConfig: " + AliRtcEngineImpl.this.getSubscribeConfigString(reqConfig) + "curConfig: " + AliRtcEngineImpl.this.getSubscribeConfigString(curConfig));
                RemoteParticipant remoteParticipant = AliRtcEngineImpl.this.findParticipantByCallID(callID);
                if (remoteParticipant != null) {
                    if (result == 0) {
                        int count = curConfig.video_track_labels.length;
                        for (int i = 0; i < count; i++) {
                            if (!TextUtils.isEmpty(curConfig.video_track_labels[i])) {
                                AliRtcEngineImpl.this.addRemoteDisplayWindow(callID, remoteParticipant.getVideoCanvas(i), ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[i]);
                            }
                        }
                        remoteParticipant.setAudioSubscribed(curConfig.audio_track_label);
                        remoteParticipant.setVideoSubscribed(curConfig.video_track_labels);
                        remoteParticipant.setVideoSubscribedCached(curConfig.video_track_labels);
                        remoteParticipant.setFirstSubscribe(false);
                        AliRtcEngineImpl.this.mSophonEngine.enableRemoteAudio(callID, !remoteParticipant.isMuteAudioPlaying());
                    }
                    if (AliRtcEngineImpl.this.mEventListener != null) {
                        AliRtcEngine.AliRtcAudioTrack at = RemoteParticipant.getAudioTrack(curConfig.audio_track_label);
                        AliRtcEngine.AliRtcVideoTrack vt = RemoteParticipant.getVideoTrack(curConfig.video_track_labels);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onSubscribeResult:userID:" + remoteParticipant.getUserID() + "&&result: " + result + "&&VideoTrack: " + vt + "&&AudioTrack: " + at);
                        AliRtcEngineImpl.this.mEventListener.onSubscribeResult(remoteParticipant.getUserID(), result, vt, at);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onSubscribeResult");
                    }
                }
            }

            public void onResubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
                super.onResubscribeResult2(result, callID, reqConfig, curConfig);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onReSubscribeResult2:result:" + result + "&&callID: " + callID + "&&reqConfig: " + AliRtcEngineImpl.this.getSubscribeConfigString(reqConfig) + "&&curConfig: " + AliRtcEngineImpl.this.getSubscribeConfigString(curConfig));
                RemoteParticipant remoteParticipant = AliRtcEngineImpl.this.findParticipantByCallID(callID);
                if (remoteParticipant != null) {
                    if (result == 0) {
                        int count = curConfig.video_track_labels.length;
                        for (int i = 0; i < count; i++) {
                            if (TextUtils.isEmpty(curConfig.video_track_labels[i]) && !TextUtils.isEmpty(remoteParticipant.getVideoSubscribedCached()[i])) {
                                AliRtcEngineImpl.this.removeRemoteDisplayWindow(callID, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[i]);
                            }
                        }
                        int count2 = curConfig.video_track_labels.length;
                        for (int i2 = 0; i2 < count2; i2++) {
                            if (!TextUtils.isEmpty(curConfig.video_track_labels[i2]) && TextUtils.isEmpty(remoteParticipant.getVideoSubscribedCached()[i2])) {
                                AliRtcEngineImpl.this.addRemoteDisplayWindow(callID, remoteParticipant.getVideoCanvas(i2), ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[i2]);
                            }
                        }
                        remoteParticipant.setAudioSubscribed(curConfig.audio_track_label);
                        remoteParticipant.setVideoSubscribed(curConfig.video_track_labels);
                        remoteParticipant.setVideoSubscribedCached(curConfig.video_track_labels);
                        AliRtcEngineImpl.this.mSophonEngine.enableRemoteAudio(callID, !remoteParticipant.isMuteAudioPlaying());
                    }
                    if (AliRtcEngineImpl.this.mEventListener != null) {
                        AliRtcEngine.AliRtcAudioTrack at = RemoteParticipant.getAudioTrack(curConfig.audio_track_label);
                        AliRtcEngine.AliRtcVideoTrack vt = RemoteParticipant.getVideoTrack(curConfig.video_track_labels);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onSubscribeResult:userID:" + remoteParticipant.getUserID() + "&&result: " + result + "&&VideoTrack: " + vt + "&&AudioTrack: " + at);
                        AliRtcEngineImpl.this.mEventListener.onSubscribeResult(remoteParticipant.getUserID(), result, vt, at);
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onSubscribeResult");
                    }
                }
            }

            public void onChannelReleaseNotify() {
                super.onChannelReleaseNotify();
                AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onChannelReleaseNotify");
            }

            public void onConnectionChange(int mediaConState) {
                super.onConnectionChange(mediaConState);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onConnectionChange:mediaConState: " + mediaConState);
                ALI_RTC_INTERFACE.AliRTCMediaConnectionReConnectState state = ALI_RTC_INTERFACE.AliRTCMediaConnectionReConnectState.fromNativeIndex(mediaConState);
                if (state != null && AliRtcEngineImpl.this.mEventListener != null) {
                    int i = AnonymousClass7.$SwitchMap$org$webrtc$alirtcInterface$ALI_RTC_INTERFACE$AliRTCMediaConnectionReConnectState[state.ordinal()];
                    if (i == 1) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onConnectionLost");
                        AliRtcEngineImpl.this.mEventListener.onConnectionLost();
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onConnectionLost");
                    } else if (i == 2) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onConnectionRecovery");
                        AliRtcEngineImpl.this.mEventListener.onConnectionRecovery();
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onConnectionRecovery");
                    } else if (i == 3) {
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onTryToReconnect");
                        AliRtcEngineImpl.this.mEventListener.onTryToReconnect();
                        AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onTryToReconnect");
                    }
                }
            }

            public void onWarning(int warningEvent, String params) {
                super.onWarning(warningEvent, params);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onWarning event: " + warningEvent);
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onOccurWarning: warningEvent:" + warningEvent + "&&params" + params);
                    AliRtcEngineImpl.this.mEventListener.onOccurWarning(warningEvent);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onOccurWarning");
                }
            }

            public void onError(int event, String params) {
                super.onError(event, params);
                AlivcLog.i(AliRtcEngineImpl.TAG, "onError event: " + event);
                if (AliRtcEngineImpl.this.mEventListener == null) {
                    return;
                }
                if (event == 17170689) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onConnectionLost");
                    AliRtcEngineImpl.this.mEventListener.onConnectionLost();
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onConnectionLost");
                } else if (event == 17170690) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onConnectionRecovery");
                    AliRtcEngineImpl.this.mEventListener.onConnectionRecovery();
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onConnectionRecovery");
                } else {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onOccurError");
                    AliRtcEngineImpl.this.mEventListener.onOccurError(event);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onOccurError");
                }
            }

            public void onTransportStatusChange(String callId, ALI_RTC_INTERFACE.TransportType event, ALI_RTC_INTERFACE.TransportStatus status) {
                super.onTransportStatusChange(callId, event, status);
            }

            public void onNetworkQualityChange(ArrayList<ALI_RTC_INTERFACE.AliTransportInfo> network_quality) {
                AliRtcEngine.AliRtcNetworkQuality upQuality;
                AliRtcEngine.AliRtcNetworkQuality downQuality;
                super.onNetworkQualityChange(network_quality);
                if (AliRtcEngineImpl.this.mEventListener != null && network_quality != null && !network_quality.isEmpty()) {
                    int count = network_quality.size();
                    for (int i = 0; i < count; i++) {
                        ALI_RTC_INTERFACE.AliTransportInfo info = network_quality.get(i);
                        if (info != null) {
                            try {
                                upQuality = AliRtcEngine.AliRtcNetworkQuality.values()[info.upQuality.getValue()];
                            } catch (Exception e) {
                                upQuality = AliRtcEngine.AliRtcNetworkQuality.Network_Unknow;
                            }
                            try {
                                downQuality = AliRtcEngine.AliRtcNetworkQuality.values()[info.downQuality.getValue()];
                            } catch (Exception e2) {
                                downQuality = AliRtcEngine.AliRtcNetworkQuality.Network_Unknow;
                            }
                            AliRtcEngineImpl.this.mEventListener.onNetworkQualityChanged(info.user_id, upQuality, downQuality);
                        }
                    }
                }
            }

            public void onLogMessage(String message) {
                super.onLogMessage(message);
            }

            public void onMessage(String tid, String contentType, String content) {
                super.onMessage(tid, contentType, content);
            }

            public void onBye(int code) {
                super.onBye(code);
                AliRtcConfig unused = AliRtcEngineImpl.this.mAliRtcConfig = new AliRtcConfig();
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onBye: code:" + code);
                    AliRtcEngineImpl.this.mNotifyListener.onBye(code);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onBye");
                }
            }

            public void onUplinkChannelMessage(int result, String contentType, String content) {
                super.onUplinkChannelMessage(result, contentType, content);
            }

            public String onCollectPlatformProfile() {
                return AliRtcEngineImpl.this.getOsInfo();
            }

            public String onFetchPerformanceInfo() {
                JSONObject jsonObject = new JSONObject();
                int cpuPercent = 0;
                try {
                    if (AliRtcEngineImpl.this.mProcessCpuTracker != null) {
                        AliRtcEngineImpl.this.mProcessCpuTracker.updateCpuUsages(AliRtcEngineImpl.this.mContext.getApplicationContext());
                        cpuPercent = AliRtcEngineImpl.this.mProcessCpuTracker.getMyPicCpuPercent();
                    }
                    jsonObject.putOpt("cpu_usage", String.valueOf(cpuPercent));
                    jsonObject.putOpt("mem_usage", String.valueOf(AliRtcEngineUtil.getRunningAppProcessInfo(AliRtcEngineImpl.this.mContext.getApplicationContext())));
                } catch (Exception e) {
                }
                return jsonObject.toString();
            }

            public boolean onFetchAudioPermissionInfo() {
                if (AliRtcEngineImpl.this.mContext == null) {
                    return false;
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    return WebRtcAudioUtils.hasPermission(AliRtcEngineImpl.this.mContext.getApplicationContext(), "android.permission.RECORD_AUDIO");
                }
                return true;
            }

            public String onFetchAudioDeviceInfo() {
                JSONObject jsonObject = new JSONObject();
                int portType = 0;
                try {
                    if (AliRtcEngineImpl.this.mUsbCameraStatus) {
                        portType = 2;
                    } else if (AliRtcEngineImpl.this.mSophonEngine != null) {
                        portType = AliRtcEngineImpl.this.mSophonEngine.getSelectAudioDevice() == AppRTCAudioManager.AudioDevice.BLUETOOTH ? 1 : 0;
                    }
                    jsonObject.putOpt("AudioPortType", Integer.valueOf(portType));
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onFetchAudioDeviceInfo: CurrentPort type: " + portType);
                } catch (Exception e) {
                    AlivcLog.e(AliRtcEngineImpl.TAG, "[API] [Callback]onFetchAudioDeviceInfo Error");
                }
                return jsonObject.toString();
            }

            public void onWindowRenderReady(String callId, int videoType) {
                super.onWindowRenderReady(callId, videoType);
            }

            public void onUpdateRoleNotify(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role old_role, ALI_RTC_INTERFACE.AliRTCSDK_Client_Role new_role) {
                super.onUpdateRoleNotify(old_role, new_role);
                if (AliRtcEngineImpl.this.mEventListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onUpdateRoleNotify: old_role:" + old_role + "&&new_role:" + new_role);
                    AliRtcEngineImpl.this.mEventListener.onUpdateRoleNotify(old_role, new_role);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onUpdateRoleNotify");
                }
            }

            public void onFirstFramereceived(String callId, String stream_label, String track_label, int time_cost_ms) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onFirstFramereceived: callId:" + callId + "&&stream_label:" + stream_label + "&&time_cost_ms:" + time_cost_ms);
                    AliRtcEngineImpl.this.mNotifyListener.onFirstFramereceived(callId, stream_label, track_label, time_cost_ms);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onFirstFramereceived");
                }
            }

            public void onFirstPacketSent(String callId, String stream_label, String track_label, int time_cost_ms) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onFirstPacketSent: callId:" + callId + "&&stream_label:" + stream_label + "&&track_label:" + track_label + "&&time_cost_ms:" + time_cost_ms);
                    AliRtcEngineImpl.this.mNotifyListener.onFirstPacketSent(callId, stream_label, track_label, time_cost_ms);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onFirstPacketSent");
                }
            }

            public void onFirstPacketReceived(String callId, String stream_label, String track_label, int time_cost_ms) {
                if (AliRtcEngineImpl.this.mNotifyListener != null) {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]onFirstPacketReceived: callId:" + callId + "&&stream_label:" + stream_label + "&&track_label:" + track_label + "&&time_cost_ms:" + time_cost_ms);
                    AliRtcEngineImpl.this.mNotifyListener.onFirstPacketReceived(callId, stream_label, track_label, time_cost_ms);
                    AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]onFirstPacketReceived");
                }
            }

            public int onFetchDeviceOrientation() {
                if (AliRtcEngineImpl.this.mAlbumOrientationEventListener == null) {
                    return super.onFetchDeviceOrientation();
                }
                AlivcLog.i(AliRtcEngineImpl.TAG, "[API][Callback]getOrientation");
                int retOrientation = AliRtcEngineImpl.this.mAlbumOrientationEventListener.getOrientation();
                AlivcLog.i(AliRtcEngineImpl.TAG, "[API][End][Callback]getOrientation");
                return retOrientation;
            }
        };
        try {
            this.mContext = context;
            ContextUtils.initialize(context.getApplicationContext());
            initProcessCpuTracker();
            LogWhenGoBackOrFront((Application) ContextUtils.getApplicationContext(), true);
            enableOrientation(ContextUtils.getApplicationContext(), true);
            DeviceConfig.initConfig();
            SophonEngine.setH5CompatibleMode(EnableH5Compatible);
            z = SophonEngine.setupUSBDevice(context, this) != 0 ? true : z;
            this.mDetectedUsbDevice = z;
            if (!z) {
                SophonEngine.closeUSBDevice();
                if (this.mSophonEngine == null) {
                    this.mSophonEngine = SophonEngine.create(context.getApplicationContext(), this.mExtras, this.mSophonEventListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initProcessCpuTracker() {
        if (this.mProcessCpuTracker == null) {
            this.mProcessCpuTracker = new ProcessCpuTracker();
        }
        this.mProcessCpuTracker.updateCpuUsages(this.mContext.getApplicationContext());
    }

    public void destroy() {
        AlivcLog.i(TAG, "[API]destroy");
        destroyEngine();
        AlivcLog.i(TAG, "[API][End]destroy");
    }

    private void destroyEngine() {
        this.mContext = null;
        this.mDetectedUsbDevice = false;
        LogWhenGoBackOrFront((Application) ContextUtils.getApplicationContext(), false);
        enableOrientation(ContextUtils.getApplicationContext(), false);
        if (this.mSophonEngine != null) {
            if (this.mUsbCameraStatus) {
                SophonEngine.closeUSBDevice();
                this.mUsbCameraStatus = false;
            }
            this.mSophonEngine.destory();
            this.mSophonEngine = null;
        }
        release();
    }

    private void LogWhenGoBackOrFront(Application app, boolean bind) {
        if (bind) {
            AppFrontBackHelper appFrontBackHelper = new AppFrontBackHelper();
            this.mAppFrontBackHelper = appFrontBackHelper;
            appFrontBackHelper.bindApplication(app, new AppFrontBackHelper.OnAppStatusListener() {
                public void onFront() {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "applicationWillBecomeActive ==");
                    if (AliRtcEngineImpl.this.mSophonEngine != null) {
                        AliRtcEngineImpl.this.mSophonEngine.applicationWillBecomeActive();
                    }
                }

                public void onBack() {
                    AlivcLog.i(AliRtcEngineImpl.TAG, "applicationWillResignActive ==");
                    if (AliRtcEngineImpl.this.mSophonEngine != null) {
                        AliRtcEngineImpl.this.mSophonEngine.applicationWillResignActive();
                    }
                }
            });
            return;
        }
        AppFrontBackHelper appFrontBackHelper2 = this.mAppFrontBackHelper;
        if (appFrontBackHelper2 != null) {
            appFrontBackHelper2.unBindApplication(app);
            this.mAppFrontBackHelper = null;
        }
    }

    private void enableOrientation(Context context, boolean enable) {
        if (enable) {
            if (this.mAlbumOrientationEventListener == null) {
                this.mAlbumOrientationEventListener = new AlbumOrientationEventListener(context, 3);
            }
            if (this.mAlbumOrientationEventListener.canDetectOrientation()) {
                this.mAlbumOrientationEventListener.enable();
            } else {
                AlivcLog.e(TAG, "Can't Detect Orientation");
            }
        } else {
            AlbumOrientationEventListener albumOrientationEventListener = this.mAlbumOrientationEventListener;
            if (albumOrientationEventListener != null) {
                albumOrientationEventListener.disable();
                this.mAlbumOrientationEventListener = null;
            }
        }
    }

    public int getDeviceOrientation() {
        AlbumOrientationEventListener albumOrientationEventListener = this.mAlbumOrientationEventListener;
        if (albumOrientationEventListener != null) {
            return albumOrientationEventListener.getOrientation();
        }
        AlivcLog.e(TAG, "mAlbumOrientationEventListener is null");
        return 0;
    }

    public String getSdkVersion() {
        SophonEngine sophonEngine;
        AlivcLog.i(TAG, "[API]getSdkVersion");
        if (TextUtils.isEmpty(VERSION) && (sophonEngine = this.mSophonEngine) != null) {
            VERSION = sophonEngine.getSDKVersion();
        }
        AlivcLog.i(TAG, "[API][End][Result]getSdkVersion");
        return VERSION;
    }

    public int setAutoPublish(boolean autoPub, boolean autoSub) {
        AlivcLog.i(TAG, "[API]setAutoPublish:autoPub: " + autoPub + "&&autoSub: " + autoSub);
        if (this.mAliRtcConfig.isInCall()) {
            AlivcLog.e(TAG, "setAutoPublish:should set before join channel.:-1");
            AlivcLog.i(TAG, "[API][End][Result]setAutoPublish:autoPub: " + autoPub + "&&autoSub: " + autoSub + ":-1");
            return -1;
        }
        this.mAliRtcConfig.setAutoPublish(autoPub);
        this.mAliRtcConfig.setAutoSubscribe(autoSub);
        AlivcLog.i(TAG, "[API][End][Result]setAutoPublish:autoPub:0");
        return 0;
    }

    public int setAudioOnlyMode(boolean audioOnly) {
        AlivcLog.i(TAG, "[API]setAudioOnlyMode:audioOnly:" + audioOnly);
        if (this.mAliRtcConfig.isInCall()) {
            AlivcLog.e(TAG, "[API][Result]setAudioOnlyMode: should set before join channel.&&audioOnly: " + audioOnly + ":-1");
            return -1;
        }
        this.mAliRtcConfig.setAudioOnly(audioOnly);
        AlivcLog.i(TAG, "[API][End][Result]setAudioOnlyMode:audioOnly:0");
        return 0;
    }

    public void setRenderSharedContext(long sharedContext) {
        AlivcLog.i(TAG, "[API]setAudioOnlyMode:setSharedContext:" + sharedContext);
        this.mAliRtcConfig.setSharedContext(sharedContext);
        AlivcLog.i(TAG, "[API][End]setSharedContext");
    }

    public boolean isAutoPublish() {
        boolean bool = this.mAliRtcConfig.isAutoPublish();
        AlivcLog.i(TAG, "[API][Result]isAutoPublish:" + bool);
        return bool;
    }

    public boolean isAutoSubscribe() {
        boolean bool = this.mAliRtcConfig.isAutoSubscribe();
        AlivcLog.i(TAG, "[API][Result]isAutoSubscribe:" + bool);
        return bool;
    }

    public boolean isAudioOnly() {
        boolean bool = this.mAliRtcConfig.isAudioOnly();
        AlivcLog.i(TAG, "[API][Result]isAudioOnly:" + bool);
        return bool;
    }

    public void joinChannel(AliRtcAuthInfo authInfo, String userName) {
        AlivcLog.i(TAG, "[API]joinChannel:authInfo" + authInfo.toString() + "&userName:" + userName);
        if (this.mSophonEngine == null) {
            AlivcLog.e(TAG, "[API]joinChannel:mSophonEngine is null");
            return;
        }
        this.mAliRtcConfig.setCachedAuthorInfo(authInfo);
        ALI_RTC_INTERFACE.AuthInfo info = new ALI_RTC_INTERFACE.AuthInfo();
        info.channel = authInfo.getConferenceId();
        info.user_id = authInfo.getUserId();
        info.appid = authInfo.getAppid();
        info.nonce = authInfo.getNonce();
        info.timestamp = authInfo.getTimestamp();
        info.token = authInfo.getToken();
        info.gslb = authInfo.getGslb();
        info.agent = authInfo.getAgent();
        AlivcLog.enableUpload(true);
        AlivcLog.setUploadAppID(authInfo.getAppid());
        this.mSophonEngine.joinChannel(info, userName);
        AlivcLog.i(TAG, "[API][End]joinChannel");
    }

    public void leaveChannel() {
        AlivcLog.i(TAG, "[API]leaveChannel");
        leaveChannel(1000);
        AlivcLog.i(TAG, "[API][End]leaveChannel");
    }

    private void leaveChannel(long timeout) {
        AliRtcEngine.AliVideoCanvas canvas;
        AliRtcConfig aliRtcConfig;
        synchronized (this.mLock) {
            try {
                if (this.mSophonEngine != null) {
                    if (!this.mApiPass) {
                        AlivcLog.e(TAG, "leaveChannel double call, return it !");
                        this.mApiPass = true;
                        int cameraType = getPreCameraType();
                        AliRtcEngine.AliVideoCanvas canvas2 = this.mAliRtcConfig.getLocalVideoCanvas();
                        AliRtcConfig aliRtcConfig2 = new AliRtcConfig();
                        this.mAliRtcConfig = aliRtcConfig2;
                        aliRtcConfig2.setCameraType(cameraType);
                        this.mAliRtcConfig.setLocalVideoCanvas(canvas2);
                        return;
                    }
                    this.mApiPass = false;
                    int ret = this.mSophonEngine.leaveChannel(timeout);
                    this.mLock.wait(timeout);
                }
                this.mApiPass = true;
                int cameraType2 = getPreCameraType();
                canvas = this.mAliRtcConfig.getLocalVideoCanvas();
                AliRtcConfig aliRtcConfig3 = new AliRtcConfig();
                this.mAliRtcConfig = aliRtcConfig3;
                aliRtcConfig3.setCameraType(cameraType2);
                aliRtcConfig = this.mAliRtcConfig;
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                    this.mApiPass = true;
                    int cameraType3 = getPreCameraType();
                    canvas = this.mAliRtcConfig.getLocalVideoCanvas();
                    AliRtcConfig aliRtcConfig4 = new AliRtcConfig();
                    this.mAliRtcConfig = aliRtcConfig4;
                    aliRtcConfig4.setCameraType(cameraType3);
                    aliRtcConfig = this.mAliRtcConfig;
                } catch (Throwable th) {
                    this.mApiPass = true;
                    int cameraType4 = getPreCameraType();
                    AliRtcEngine.AliVideoCanvas canvas3 = this.mAliRtcConfig.getLocalVideoCanvas();
                    AliRtcConfig aliRtcConfig5 = new AliRtcConfig();
                    this.mAliRtcConfig = aliRtcConfig5;
                    aliRtcConfig5.setCameraType(cameraType4);
                    this.mAliRtcConfig.setLocalVideoCanvas(canvas3);
                    throw th;
                }
            }
            aliRtcConfig.setLocalVideoCanvas(canvas);
        }
    }

    public boolean isInCall() {
        AliRtcConfig aliRtcConfig = this.mAliRtcConfig;
        if (aliRtcConfig == null) {
            return false;
        }
        boolean isIncall = aliRtcConfig.isInCall();
        AlivcLog.i(TAG, "[API]isInCall:" + isIncall);
        return isIncall;
    }

    public int enableHighDefinitionPreview(boolean enable) {
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine == null) {
            return -1;
        }
        int ret = sophonEngine.enableHighDefinitionPreview(enable);
        Log.e(TAG, "enableHighDefinitionPreview:" + ret);
        AlivcLog.i(TAG, "[API]enableHighDefinitionPreview:enable" + enable);
        return ret;
    }

    public int setLocalViewConfig(AliRtcEngine.AliVideoCanvas viewConfig, AliRtcEngine.AliRtcVideoTrack track) {
        AlivcLog.e(TAG, "[API]setLocalViewConfig:viewConfig:" + viewConfig.toString() + "&&videoTrack:" + track);
        if (track != AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera) {
            AlivcLog.e(TAG, "[API][Result]setLocalViewConfig:viewConfig:" + viewConfig.toString() + "&&videoTrack:" + track + ":-1");
            return -1;
        } else if (viewConfig == null || (viewConfig.textureId == 0 && viewConfig.view == null)) {
            AlivcLog.e(TAG, "[API][Result]setLocalViewConfig:setLocalViewConfig:view is null:-1");
            return -1;
        } else {
            viewConfig.flip = getIsFlip(viewConfig.mirrorMode, getPreCameraType(), false);
            if (this.mAliRtcConfig.getLocalVideoCanvas() != null) {
                AlivcLog.d(TAG, "mAliRtcConfig getLocalVideoCanvas not null");
                if (viewConfig.textureId == this.mAliRtcConfig.getLocalVideoCanvas().textureId && viewConfig.view == this.mAliRtcConfig.getLocalVideoCanvas().view) {
                    updateDisplayWindow(viewConfig);
                } else {
                    AlivcLog.e(TAG, "mAliRtcConfig setLocalViewConfig addLocalDisplayWindow");
                    removeLocalDisplayWindow();
                    addLocalDisplayWindow(viewConfig);
                }
            }
            this.mAliRtcConfig.setLocalVideoCanvas(viewConfig);
            AlivcLog.i(TAG, "[API][End][Result]setLocalViewConfig:0");
            return 0;
        }
    }

    public int startPreview() {
        AlivcLog.i(TAG, "[API]startPreview");
        if (this.mAliRtcConfig.getLocalVideoCanvas() == null) {
            AlivcLog.e(TAG, "[API][Result]startPreview: view is null error:-1");
            return -1;
        } else if (this.mAliRtcConfig.getLocalVideoCanvas().textureId == 0 && this.mAliRtcConfig.getLocalVideoCanvas().view == null) {
            AlivcLog.e(TAG, "[API][Result]startPreview: view is null error:-1");
            return -1;
        } else if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.e(TAG, "[API][End][Result]startPreview: audio only mode error:-1");
            return -1;
        } else if (startCapture() != 0) {
            AlivcLog.e(TAG, "[API][End][Result]startPreview:startCapture error:-1");
            return -1;
        } else {
            addLocalDisplayWindow(this.mAliRtcConfig.getLocalVideoCanvas());
            AlivcLog.i(TAG, "[API][End][Result]startPreview:0");
            return 0;
        }
    }

    public int stopPreview() {
        AlivcLog.i(TAG, "[API]stopPreview:");
        removeLocalDisplayWindow();
        stopCapture();
        AlivcLog.i(TAG, "[API][End][Result]stopPreview:0");
        return 0;
    }

    private int startCapture() {
        if (this.mSophonEngine == null) {
            AlivcLog.e(TAG, "startCapture: SDK is null");
            return -1;
        } else if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.e(TAG, "startCapture: audio only mode");
            return -1;
        } else {
            ALI_RTC_INTERFACE.AliCameraConfig config = new ALI_RTC_INTERFACE.AliCameraConfig();
            config.video_source = this.mAliRtcConfig.getCameraType();
            config.autoFocus = this.mAliRtcConfig.isCamAutoFocus();
            config.flash = this.mAliRtcConfig.isCamFlash();
            config.sharedContext = this.mAliRtcConfig.getSharedContext();
            config.context = this.mContext;
            config.preferWidth = this.mAliRtcConfig.getCamVideoProfile().getWidth();
            config.preferHeight = this.mAliRtcConfig.getCamVideoProfile().getHeight();
            config.preferFps = this.mAliRtcConfig.getCamVideoProfile().getFPS();
            this.mAliRtcConfig.setCameraOn(true);
            AlivcLog.i(TAG, "openCamera,config.video_source:" + config.video_source + "config.autoFocus:" + config.autoFocus + "config.flash:" + config.flash + "config.sharedContext:" + config.sharedContext + "config.context:" + config.context + "config.preferWidth:" + config.preferWidth + "config.preferHeight:" + config.preferHeight + "config.preferFps:" + config.preferFps);
            this.mSophonEngine.openCamera(config);
            return 0;
        }
    }

    private int stopCapture() {
        AlivcLog.d(TAG, "stopCapture");
        if (this.mSophonEngine == null) {
            AlivcLog.e(TAG, "stopCapture: SDK is null");
            return -1;
        } else if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.e(TAG, "stopCapture: audio only mode");
            return -1;
        } else {
            this.mAliRtcConfig.setCameraOn(false);
            this.mSophonEngine.closeCamera();
            return 0;
        }
    }

    public int muteLocalCamera(boolean mute, AliRtcEngine.AliRtcVideoTrack track) {
        AlivcLog.i(TAG, "[API]muteLocalCamera:mute:" + mute + "&&VideoTrack:" + track);
        if (this.mSophonEngine == null) {
            AlivcLog.e(TAG, "[API][End][Result]muteLocalCamera: SDK is null:-1");
            return -1;
        } else if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.e(TAG, "[API][End][Result]muteLocalCamera: audio only mode:-1");
            return -1;
        } else {
            boolean z = false;
            if (track.getValue() == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera.getValue() || track.getValue() == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth.getValue()) {
                this.mAliRtcConfig.setMuteLocalCameraVideo(mute);
                if (this.mAliRtcConfig.hasPublished()) {
                    this.mSophonEngine.enableLocalVideo(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge, !mute);
                    if (this.mAliRtcConfig.isDualStream()) {
                        this.mSophonEngine.enableLocalVideo(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSmall, !mute);
                    }
                }
                AlivcLog.i(TAG, "[API][End][Result]muteLocalCamera:mute:0");
                return 0;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("muteLocalCamera: error video track");
            sb.append(track == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
            if (track == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth) {
                z = true;
            }
            sb.append(z);
            AlivcLog.e(TAG, sb.toString());
            return -1;
        }
    }

    public boolean isCameraOn() {
        boolean bool = this.mAliRtcConfig.isCameraOn();
        AlivcLog.i(TAG, "[API][Result]isCameraOn:" + bool);
        return bool;
    }

    public int muteLocalMic(boolean mute) {
        AlivcLog.i(TAG, "[API]muteLocalMic:mute:" + mute);
        this.mAliRtcConfig.setMuteLocalMic(mute);
        if (this.mSophonEngine == null) {
            AlivcLog.e(TAG, "[API][Result]muteLocalMic: SDK is null&&mute:" + mute + "-1");
            return -1;
        }
        if (this.mAliRtcConfig.hasPublished()) {
            this.mSophonEngine.enableLocalAudio(!mute);
        }
        AlivcLog.i(TAG, "[API][End][Result]muteLocalMic:0");
        return 0;
    }

    public void publish() {
        AlivcLog.i(TAG, "[API]publish");
        if (!this.mAliRtcConfig.isInCall()) {
            AlivcLog.e(TAG, "[API]publish:not in call");
        } else if (this.mSophonEngine == null) {
            AlivcLog.e(TAG, "[API]publish:SDK is null");
        } else {
            ALI_RTC_INTERFACE.AliPublishConfig publishConfig = getConfig();
            if (this.mAliRtcConfig.isPublishAudio() || ((this.mAliRtcConfig.isPublishCameraTrack() || this.mAliRtcConfig.isPublishScreenTrack()) && !this.mAliRtcConfig.isAudioOnly())) {
                if (!TextUtils.isEmpty(this.mAliRtcConfig.getLocalCallID()) || this.mAliRtcConfig.isPublishIsGoing()) {
                    AlivcLog.i(TAG, "[API]republish");
                    this.mSophonEngine.republish(publishConfig);
                } else {
                    AlivcLog.i(TAG, "[API]publish");
                    this.mSophonEngine.publish(publishConfig);
                    this.mAliRtcConfig.setPublishIsGoing(true);
                }
                AliRtcConfig aliRtcConfig = this.mAliRtcConfig;
                aliRtcConfig.setCameraOn(aliRtcConfig.isPublishCameraTrack());
                AlivcLog.i(TAG, "[API][End]publish");
                return;
            }
            AlivcLog.i(TAG, "[API]unpublish");
            this.mAliRtcConfig.setCameraOn(false);
            this.mSophonEngine.unpublish();
            this.mAliRtcConfig.setLocalCallID((String) null, false);
        }
    }

    public void setVideoProfile(AliRtcEngine.AliRtcVideoProfile profile, AliRtcEngine.AliRtcVideoTrack track) {
        AlivcLog.i(TAG, "[API]setVideoProfile:VideoProfile:" + profile.toString() + "&&VideoTrack:" + track);
        if (!this.mAliRtcConfig.isAudioOnly()) {
            if (track == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth) {
                this.mAliRtcConfig.setCamVideoProfile(profile);
                this.mAliRtcConfig.setScreenVideoProfile(profile);
            } else if (track.getValue() == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera.getValue()) {
                AlivcLog.i(TAG, "setVideoProfileprofile:" + profile.getId() + "track:" + track.getValue());
                this.mAliRtcConfig.setCamVideoProfile(profile);
            } else if (track == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen) {
                this.mAliRtcConfig.setScreenVideoProfile(profile);
            }
            AlivcLog.i(TAG, "[API][End]setVideoProfile");
        }
    }

    public AliRtcEngine.AliRtcVideoProfile getVideoProfile(AliRtcEngine.AliRtcVideoTrack track) {
        AlivcLog.i(TAG, "[API]getVideoProfile:VideoTrack:" + track);
        AliRtcEngine.AliRtcVideoProfile ret = this.mAliRtcConfig.getCamVideoProfile();
        if (track != AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth) {
            if (track == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera) {
                ret = this.mAliRtcConfig.getCamVideoProfile();
            } else if (track == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen) {
                ret = this.mAliRtcConfig.getScreenVideoProfile();
            }
        }
        AlivcLog.i(TAG, "[API][End][Result]getVideoProfile:VideoTrack:" + track + LogUtils.COLON + ret.toString());
        return ret;
    }

    public int setRemoteViewConfig(AliRtcEngine.AliVideoCanvas canvas, String uid, AliRtcEngine.AliRtcVideoTrack track) {
        AlivcLog.i(TAG, "[API]setRemoteViewConfig:canvas:" + canvas.toString() + "&&uid:" + uid + "&&VideoTrack" + track);
        if (track == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo || track == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth) {
            AlivcLog.e(TAG, "[API][Result]setRemoteViewConfig: error video track:-1");
            return -1;
        } else if (canvas == null || (canvas.textureId == 0 && canvas.view == null)) {
            AlivcLog.e(TAG, "[API][Result]setRemoteViewConfig: canvas is null:-1");
            return -1;
        } else if (this.mSophonEngine == null) {
            AlivcLog.e(TAG, "[API][Result]setRemoteViewConfig: SDK is null:-1");
            return -1;
        } else {
            RemoteParticipant remoteParticipant = this.mAliRtcConfig.getRemoteParticipants().get(uid);
            if (remoteParticipant == null) {
                AlivcLog.e(TAG, "[API][Result]setRemoteViewConfig: remote user is null:-1");
                return -1;
            }
            int sourceIndex = remoteParticipant.getSubVideoSurceIndex(track);
            if (sourceIndex == -1) {
                return 0;
            }
            if (canvas.textureId > 0 && remoteParticipant.getVideoCanvas(sourceIndex) != null && remoteParticipant.getVideoCanvas(sourceIndex).textureId == canvas.textureId) {
                if (track == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera) {
                    removeRemoteDisplayWindow(remoteParticipant.getCallID(), ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge);
                } else {
                    removeRemoteDisplayWindow(remoteParticipant.getCallID(), ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare);
                }
                remoteParticipant.getVideoCanvas(sourceIndex).textureId = 0;
            }
            if (!this.mAliRtcConfig.isInCall()) {
                remoteParticipant.setVideoCanvas(track, canvas);
                return 0;
            }
            ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type vst = ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.values()[sourceIndex];
            if (!TextUtils.isEmpty(remoteParticipant.getVideoSubscribed()[sourceIndex])) {
                canvas.flip = getIsFlip(canvas.mirrorMode, getPreCameraType(), true);
                if (remoteParticipant.getVideoCanvas(sourceIndex) == null) {
                    addRemoteDisplayWindow(remoteParticipant.getCallID(), canvas, vst);
                } else if (canvas.textureId == remoteParticipant.getVideoCanvas(sourceIndex).textureId && remoteParticipant.getVideoCanvas(sourceIndex).view == canvas.view) {
                    updateDisplayWindow(canvas);
                } else {
                    AlivcLog.e(TAG, "setRemoteViewConfig: remove and add ");
                    removeRemoteDisplayWindow(remoteParticipant.getCallID(), vst);
                    addRemoteDisplayWindow(remoteParticipant.getCallID(), canvas, vst);
                }
                remoteParticipant.setVideoCanvas(track, canvas);
            }
            AlivcLog.i(TAG, "[API][End][Result]setRemoteViewConfig:0");
            return 0;
        }
    }

    public int muteRemoteAudioPlaying(String uid, boolean mute) {
        AlivcLog.i(TAG, "[API]muteRemoteAudioPlaying:uid:" + uid + "&&mute:" + mute);
        if (this.mSophonEngine == null) {
            AlivcLog.e(TAG, "[API][End][Result]muteRemoteAudioPlaying: SDK is null-1");
            return -1;
        }
        RemoteParticipant remoteParticipant = this.mAliRtcConfig.getRemoteParticipants().get(uid);
        if (remoteParticipant == null) {
            if (this.mAliRtcConfig.getRemoteParticipants() != null && this.mAliRtcConfig.getRemoteParticipants().size() > 0) {
                for (Map.Entry<String, RemoteParticipant> remoteParticipantEntry : this.mAliRtcConfig.getRemoteParticipants().entrySet()) {
                    AlivcLog.e(TAG, "[API]muteRemoteAudioPlaying: mAliRtcConfig.getRemoteParticipants() uid is:" + remoteParticipantEntry.getKey() + ",value:" + remoteParticipantEntry.getValue());
                }
            }
            AlivcLog.e(TAG, "[API][End][Result]muteRemoteAudioPlaying: remote user can not found user " + uid + ":-1");
            return -1;
        } else if (TextUtils.isEmpty(remoteParticipant.getCallID())) {
            AlivcLog.e(TAG, "[API][End][Result]muteRemoteAudioPlaying: callid is null:-1");
            return -1;
        } else {
            if (remoteParticipant.isMuteAudioPlaying() != mute) {
                remoteParticipant.setMuteAudioPlaying(mute);
                this.mSophonEngine.enableRemoteAudio(remoteParticipant.getCallID(), !mute);
            }
            AlivcLog.i(TAG, "[API][End][Result]muteRemoteAudioPlaying:0");
            return 0;
        }
    }

    public int subscribe(String uid) {
        AlivcLog.i(TAG, "[API]subscribe: uid:" + uid);
        if (this.mSophonEngine == null) {
            AlivcLog.e(TAG, "[API][End][Result]subscribe: SDK is null:-1");
            return -1;
        } else if (TextUtils.isEmpty(uid)) {
            AlivcLog.e(TAG, "[API][End][Result]subscribe: uid is null:-1");
            return -1;
        } else {
            RemoteParticipant remoteParticipant = this.mAliRtcConfig.getRemoteParticipants().get(uid);
            if (remoteParticipant == null) {
                AlivcLog.e(TAG, "[API][End][Result]subscribe: remote user is null:-1");
                return -1;
            } else if (TextUtils.isEmpty(remoteParticipant.getCallID())) {
                AlivcLog.e(TAG, "[API][End][Result]subscribe: remote callId is null:-1");
                return -1;
            } else {
                ALI_RTC_INTERFACE.AliSubscribeConfig subscribeConfig = new ALI_RTC_INTERFACE.AliSubscribeConfig();
                subscribeConfig.stream_label = remoteParticipant.getStreamLabel();
                if (!this.mAliRtcConfig.isAudioOnly()) {
                    subscribeConfig.video_track_labels = remoteParticipant.getAvailableVideoSubed();
                    subscribeConfig.audio_track_label = remoteParticipant.getAvailableAudioSubed();
                } else {
                    subscribeConfig.audio_track_label = remoteParticipant.getAvailableAudioSubed();
                    Arrays.fill(subscribeConfig.video_track_labels, "");
                }
                if (RemoteParticipant.getAudioTrack(subscribeConfig.audio_track_label) == AliRtcEngine.AliRtcAudioTrack.AliRtcAudioTrackNo && RemoteParticipant.getVideoTrack(subscribeConfig.video_track_labels) == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo) {
                    AlivcLog.i(TAG, "unsubscribe callId: " + remoteParticipant.getCallID());
                    this.mSophonEngine.unsubscribe(remoteParticipant.getCallID());
                    return 0;
                } else if (!remoteParticipant.isFirstSubscribe()) {
                    AlivcLog.i(TAG, "subscribe callId: " + remoteParticipant.getCallID() + " reSubscribeConfig: " + getSubscribeConfigString(subscribeConfig));
                    this.mSophonEngine.resubscribe(remoteParticipant.getCallID(), subscribeConfig);
                    return 0;
                } else {
                    AlivcLog.i(TAG, "subscribe callId: " + remoteParticipant.getCallID() + " subscribeConfig: " + getSubscribeConfigString(subscribeConfig));
                    this.mSophonEngine.subscribe(remoteParticipant.getCallID(), subscribeConfig);
                    remoteParticipant.setFirstSubscribe(false);
                    AlivcLog.e(TAG, "[API][End][Result]subscribe: :0");
                    return 0;
                }
            }
        }
    }

    public void configLocalCameraPublish(boolean enable) {
        AlivcLog.e(TAG, "[API]configLocalCameraPublish:" + enable);
        this.mAliRtcConfig.setPublishCameraTrack(enable);
        AlivcLog.e(TAG, "[API][End]configLocalCameraPublish");
    }

    public boolean isLocalCameraPublishEnabled() {
        boolean bool = this.mAliRtcConfig.isPublishCameraTrack();
        AlivcLog.e(TAG, "[API][Result]isLocalCameraPublishEnabled:" + bool);
        return bool;
    }

    public void configLocalScreenPublish(boolean enable) {
        AlivcLog.e(TAG, "[API]configLocalScreenPublish:enable:" + enable);
        this.mAliRtcConfig.setPublishScreenTrack(enable);
        AlivcLog.e(TAG, "[API][End]configLocalScreenPublish");
    }

    public boolean isLocalScreenPublishEnabled() {
        AlivcLog.e(TAG, "[API]isLocalScreenPublishEnabled");
        boolean bool = this.mAliRtcConfig.isPublishScreenTrack();
        AlivcLog.e(TAG, "[API][End][Result]isLocalScreenPublishEnabled:" + bool);
        return bool;
    }

    public void configLocalAudioPublish(boolean enable) {
        AlivcLog.e(TAG, "[API]configLocalAudioPublish:enable:" + enable);
        this.mAliRtcConfig.setPublishAudio(enable);
        AlivcLog.e(TAG, "[API][End]configLocalAudioPublish");
    }

    public boolean isLocalAudioPublishEnabled() {
        boolean bool = this.mAliRtcConfig.isPublishAudio();
        AlivcLog.e(TAG, "[API][Result]isLocalAudioPublishEnabled:" + bool);
        return bool;
    }

    public int configLocalSimulcast(boolean enable, AliRtcEngine.AliRtcVideoTrack track) {
        AlivcLog.i(TAG, "[API]configLocalSimulcast: enable:" + enable + "&&videoTrack: " + track.toString());
        if (track != AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera) {
            AlivcLog.e(TAG, "configLocalSimulcast: error videoTrack");
            return -1;
        }
        this.mAliRtcConfig.setDualStream(enable);
        AlivcLog.i(TAG, "[API][End][Result]configLocalSimulcast: enable:" + enable + "&&videoTrack: " + track.toString() + ":0");
        return 0;
    }

    public boolean isLocalSimulcastEnabled() {
        boolean bool = this.mAliRtcConfig.isDualStream();
        AlivcLog.e(TAG, "[API][Result]isLocalSimulcastEnabled:" + bool);
        return bool;
    }

    public void configRemoteAudio(String uid, boolean enable) {
        AlivcLog.i(TAG, "[API]configRemoteAudio:uid: " + uid + "&&enable: " + enable);
        if (uid == null && !isInCall()) {
            DefaultValueConstant.SUB_AUDIO = enable;
        } else if (TextUtils.isEmpty(uid)) {
            AlivcLog.e(TAG, "configRemoteAudio: uid is null");
        } else {
            RemoteParticipant remoteParticipant = this.mAliRtcConfig.getRemoteParticipants().get(uid);
            if (remoteParticipant != null) {
                remoteParticipant.setAudioSubscribed(enable ? remoteParticipant.getAudioTrackLabel() : "");
                remoteParticipant.setUcAudeoSubed(enable);
            }
            AlivcLog.i(TAG, "[API][End]configRemoteAudio");
        }
    }

    public void configRemoteCameraTrack(String uid, boolean master, boolean enable) {
        AlivcLog.d(TAG, "[API]configRemoteCameraTrack uid: " + uid + "&&master: " + master + "&&enable: " + enable);
        if (uid == null && !isInCall()) {
            DefaultValueConstant.SUB_CAMERA_MASTER = master;
            DefaultValueConstant.SUB_CAMERA = enable;
        } else if (TextUtils.isEmpty(uid)) {
            AlivcLog.e(TAG, "configRemoteCameraTrack: uid is null");
        } else {
            RemoteParticipant remoteParticipant = this.mAliRtcConfig.getRemoteParticipants().get(uid);
            if (remoteParticipant != null) {
                String[] videoTrackLabels = remoteParticipant.getVideoTrackLabels();
                String[] videoSubscribed = remoteParticipant.getVideoSubscribed();
                boolean supportSuper = DeviceConfig.isSupportSuperStream();
                int bigIndex = ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge.ordinal();
                int smallIndex = ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSmall.ordinal();
                int superIndex = ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSuper.ordinal();
                videoSubscribed[bigIndex] = "";
                videoSubscribed[smallIndex] = "";
                videoSubscribed[superIndex] = "";
                if (enable) {
                    if (!master) {
                        videoSubscribed[smallIndex] = videoTrackLabels[smallIndex];
                    } else if (!supportSuper) {
                        videoSubscribed[bigIndex] = videoTrackLabels[bigIndex];
                    } else if (!TextUtils.isEmpty(videoTrackLabels[superIndex])) {
                        videoSubscribed[superIndex] = videoTrackLabels[superIndex];
                    } else {
                        videoSubscribed[bigIndex] = videoTrackLabels[bigIndex];
                    }
                }
                remoteParticipant.setUcVideoSubed(enable);
                remoteParticipant.setUcVideoSubedMaster(master);
                AlivcLog.d(TAG, "[API][End]configRemoteCameraTrack");
            }
        }
    }

    public void configRemoteScreenTrack(String uid, boolean enable) {
        AlivcLog.i(TAG, "[API]configRemoteScreenTrack uid: " + uid + "&&enable: " + enable);
        if (uid == null && !isInCall()) {
            DefaultValueConstant.SUB_SCREEN = enable;
        } else if (TextUtils.isEmpty(uid)) {
            AlivcLog.e(TAG, "configRemoteScreenTrack: uid is null");
        } else {
            RemoteParticipant remoteParticipant = this.mAliRtcConfig.getRemoteParticipants().get(uid);
            if (remoteParticipant != null) {
                String[] videoTrackLabels = remoteParticipant.getVideoTrackLabels();
                String[] videoSubscribed = remoteParticipant.getVideoSubscribed();
                int screenIndex = ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare.ordinal();
                videoSubscribed[screenIndex] = enable ? videoTrackLabels[screenIndex] : "";
                remoteParticipant.setUcScreenSubed(enable);
            }
            AlivcLog.i(TAG, "[API][End]configRemoteScreenTrack");
        }
    }

    public String[] getOnlineRemoteUsers() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Map.Entry<String, RemoteParticipant> entry : this.mAliRtcConfig.getRemoteParticipants().entrySet()) {
            if (entry.getValue().isOnline()) {
                arrayList.add(entry.getKey());
            }
        }
        String[] remoteUsers = new String[arrayList.size()];
        AlivcLog.i(TAG, "[API][End]getOnlineRemoteUsers: " + arrayList.toArray(remoteUsers));
        return (String[]) arrayList.toArray(remoteUsers);
    }

    public AliRtcRemoteUserInfo getUserInfo(String uid) {
        AlivcLog.i(TAG, "[API]getUserInfo:uid:" + uid);
        RemoteParticipant remoteParticipant = this.mAliRtcConfig.getRemoteParticipants().get(uid);
        AliRtcRemoteUserInfo userInfo = null;
        if (remoteParticipant != null) {
            userInfo = new AliRtcRemoteUserInfo();
            userInfo.setUserID(remoteParticipant.getUserID());
            userInfo.setCallID(remoteParticipant.getCallID());
            userInfo.setSessionID(remoteParticipant.getSessionID());
            userInfo.setDisplayName(remoteParticipant.getDisplayName());
            userInfo.setStreamLabel(remoteParticipant.getStreamLabel());
            userInfo.setFirstSubscribe(remoteParticipant.isFirstSubscribe());
            userInfo.setOnline(remoteParticipant.isOnline());
            userInfo.setMuteAudioPlaying(remoteParticipant.isMuteAudioPlaying());
            userInfo.setCameraCanvas(remoteParticipant.getCameraCanvas());
            userInfo.setScreenCanvas(remoteParticipant.getScreenCanvas());
            userInfo.setHasAudio(remoteParticipant.isHasAudioStream());
            boolean z = false;
            userInfo.setHasCameraMaster(remoteParticipant.isHasVideoSuperStream() || remoteParticipant.isHasVideoLargeStream());
            userInfo.setHasCameraSlave(remoteParticipant.isHasVideoSmallStream());
            userInfo.setHasScreenSharing(remoteParticipant.isHasScreenShareStream());
            userInfo.setSubAudio(remoteParticipant.isAudioSubscribed());
            if (remoteParticipant.isSubVideoSuperStream() || remoteParticipant.isSubVideoLargeStream()) {
                z = true;
            }
            userInfo.setSubCameraMaster(z);
            userInfo.setSubCamera(remoteParticipant.isVideoSubscribed());
            userInfo.setSubScreenSharing(remoteParticipant.isSubShcreenShareStream());
            userInfo.setRequestSubAudio(remoteParticipant.isUcAudeoSubed());
            userInfo.setRequestCameraMaster(remoteParticipant.isUcVideoSubedMaster());
            userInfo.setRequestCamera(remoteParticipant.isUcVideoSubed());
            userInfo.setRequestScreenSharing(remoteParticipant.isUcScreenSubed());
            AlivcLog.i(TAG, "[API][End][Result]getUserInfo:" + userInfo.toString());
        }
        return userInfo;
    }

    public boolean isUserOnline(String uid) {
        RemoteParticipant remoteParticipant = this.mAliRtcConfig.getRemoteParticipants().get(uid);
        if (remoteParticipant == null) {
            return false;
        }
        boolean bool = remoteParticipant.isOnline();
        AlivcLog.i(TAG, "[API][End][Result]isUserOnline:uid: " + uid + LogUtils.COLON + bool);
        return bool;
    }

    public int enableSpeakerphone(boolean enable) {
        AlivcLog.i(TAG, "[API]enableSpeakerphone:enable: " + enable);
        if (this.mSophonEngine == null) {
            AlivcLog.i(TAG, "[API][Result]enableSpeakerphone: SDK is null: -1");
            return -1;
        }
        this.mAliRtcConfig.setSpeakerOn(enable);
        this.mSophonEngine.selectSpeakePhone(enable);
        this.mSophonEngine.setSpeakerStatus(enable);
        AlivcLog.i(TAG, "[API][End][Result]enableSpeakerphone:enable: 0");
        return 0;
    }

    public boolean isSpeakerOn() {
        AlivcLog.i(TAG, "[API]isSpeakerOn");
        boolean bool = this.mAliRtcConfig.isSpeakerOn();
        AlivcLog.i(TAG, "[API][End][Result]isSpeakerOn:" + bool);
        return bool;
    }

    public int switchCamera() {
        SophonEngine sophonEngine;
        AlivcLog.i(TAG, "[API]switchCamera");
        if (this.mAliRtcConfig.isAudioOnly() || (sophonEngine = this.mSophonEngine) == null) {
            return -1;
        }
        int ret = sophonEngine.switchCramer();
        if (ret == 0) {
            if (this.mAliRtcConfig.getCameraType() == AliRtcEngine.AliRTCCameraType.AliRTCCameraBack.getCameraType()) {
                this.mAliRtcConfig.setCameraType(AliRtcEngine.AliRTCCameraType.AliRTCCameraFront.getCameraType());
            } else {
                this.mAliRtcConfig.setCameraType(AliRtcEngine.AliRTCCameraType.AliRTCCameraBack.getCameraType());
            }
            setLocalViewConfig(this.mAliRtcConfig.getLocalVideoCanvas(), AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
        }
        AlivcLog.i(TAG, "[API][End][Result]switchCamera:" + ret);
        return ret;
    }

    public AliRtcEngine.AliRTCCameraType getCurrentCameraType() {
        if (this.mSophonEngine == null) {
            return AliRtcEngine.AliRTCCameraType.AliRTCCameraInvalid;
        }
        AlivcLog.i(TAG, "[API]getCurrentCameraType");
        AliRtcEngine.AliRTCCameraType aliRTCCameraType = AliRtcEngine.AliRTCCameraType.values()[this.mSophonEngine.getCaptureType().ordinal()];
        AlivcLog.i(TAG, "[API][End][Result]getCurrentCameraType:" + aliRTCCameraType);
        return aliRTCCameraType;
    }

    public void setPreCameraType(int faceTo) {
        AlivcLog.i(TAG, "[API]setPreCameraType:faceTo:" + faceTo);
        this.mAliRtcConfig.setCameraType(faceTo);
        AlivcLog.i(TAG, "[API][End]setPreCameraType");
    }

    public int getPreCameraType() {
        AlivcLog.i(TAG, "[API]getPreCameraType");
        int camereType = this.mAliRtcConfig.getCameraType();
        AlivcLog.i(TAG, "[API][End][Result]getPreCameraType:" + camereType);
        return camereType;
    }

    public int setCameraZoom(float zoom, boolean flash, boolean autoFocus) {
        AlivcLog.i(TAG, "[API]setCameraZoom:zoom: " + zoom + "&&flash: " + flash + "&&autoFocus: " + autoFocus);
        if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.e(TAG, "setCameraZoom: audio only mode");
            return -1;
        }
        this.mAliRtcConfig.setCamZoom(zoom);
        this.mAliRtcConfig.setCamFlash(flash);
        this.mAliRtcConfig.setCamAutoFocus(autoFocus);
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine != null) {
            sophonEngine.setFlash(this.mAliRtcConfig.isCamFlash());
            this.mSophonEngine.setCameraZoom(this.mAliRtcConfig.getCamZoom());
        }
        AlivcLog.i(TAG, "[API][End][Result]setCameraZoom:0");
        return 0;
    }

    public float getCameraZoom() {
        AlivcLog.i(TAG, "[API]getCameraZoom");
        float camZoom = this.mAliRtcConfig.getCamZoom();
        AlivcLog.d(TAG, "[API][End][Result]getCameraZoom:" + camZoom);
        return camZoom;
    }

    public boolean isCameraFlash() {
        AlivcLog.i(TAG, "[API]isCameraFlash");
        boolean isCamFlash = this.mAliRtcConfig.isCamFlash();
        AlivcLog.i(TAG, "[API][End][Result]isCameraFlash:" + isCamFlash);
        return isCamFlash;
    }

    public boolean isCameraAutoFocus() {
        AlivcLog.i(TAG, "[API]isCameraAutoFocus");
        boolean isCamAutoFocus = this.mAliRtcConfig.isCamAutoFocus();
        AlivcLog.i(TAG, "[API][End][Result]isCameraAutoFocus:" + isCamAutoFocus);
        return isCamAutoFocus;
    }

    public boolean isCameraSupportFocusPoint() {
        if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.e(TAG, "isCameraSupportFocusPoint: audio only mode");
            return false;
        } else if (this.mSophonEngine == null) {
            return false;
        } else {
            AlivcLog.i(TAG, "[API]isCameraSupportFocusPoint");
            boolean isSupportFocusPoint = this.mSophonEngine.isCameraSupportFocusPoint();
            AlivcLog.i(TAG, "[API][End][Result]isCameraSupportFocusPoint:" + isSupportFocusPoint);
            return isSupportFocusPoint;
        }
    }

    public boolean isCameraSupportExposurePoint() {
        if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.e(TAG, "isCameraSupportExposurePoint: audio only mode");
            return false;
        } else if (this.mSophonEngine == null) {
            return false;
        } else {
            AlivcLog.i(TAG, "[API]isCameraSupportExposurePoint");
            boolean isSupportExposurePoint = this.mSophonEngine.isCameraSupportExposurePoint();
            AlivcLog.i(TAG, "[API][End][Result]isCameraSupportExposurePoint:" + isSupportExposurePoint);
            return isSupportExposurePoint;
        }
    }

    public int setCameraFocusPoint(float x, float y) {
        if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.e(TAG, "setCameraFocusPoint: audio only mode");
            return -1;
        } else if (this.mSophonEngine == null) {
            return 0;
        } else {
            AlivcLog.i(TAG, "[API]setCameraFocusPoint:x:" + x + "&&y:" + y);
            this.mSophonEngine.setCameraFocusPoint(x, y);
            AlivcLog.i(TAG, "[API][End][Result]setCameraFocusPoint:0");
            return 0;
        }
    }

    public int setCameraExposurePoint(float x, float y) {
        if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.e(TAG, "setCameraExposurePoint: audio only mode");
            return -1;
        } else if (this.mSophonEngine == null) {
            return -1;
        } else {
            AlivcLog.i(TAG, "[API]setCameraExposurePoint:x:" + x + "&&y: " + y);
            this.mSophonEngine.setCameraExposurePoint(x, y);
            StringBuilder sb = new StringBuilder();
            sb.append("[API][End][Result]setCameraExposurePoint:");
            sb.append(-1);
            AlivcLog.i(TAG, sb.toString());
            return -1;
        }
    }

    public int setRecordingVolume(int volume) {
        if (this.mSophonEngine == null) {
            return 0;
        }
        AlivcLog.i(TAG, "[API]setRecordingVolume: volume:" + volume);
        return this.mSophonEngine.setRecordingVolume(volume);
    }

    public int setPlayoutVolume(int volume) {
        if (this.mSophonEngine == null) {
            return 0;
        }
        AlivcLog.i(TAG, "[API]setPlayoutVolume: volume:" + volume);
        return this.mSophonEngine.setPlayoutVolume(volume);
    }

    public void setLogLevel(AliRtcEngine.AliRtcLogLevel logLevel) {
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]setLogLevel:logLevel:" + logLevel);
            this.mSophonEngine.changeLogLevel(ALI_RTC_INTERFACE.AliRTCSDKLogLevel.values()[logLevel.ordinal()]);
            AlivcLog.i(TAG, "[API][End][Result]setLogLevel");
        }
    }

    public void uploadLog() {
        AlivcLog.i(TAG, "[API]uploadLog");
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine != null) {
            sophonEngine.uploadLop();
            AlivcLog.i(TAG, "[API][End]uploadLog");
        }
    }

    public void setRtcEngineEventListener(AliRtcEngineEventListener listener) {
        if (listener != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[API]setRtcEngineEventListener:listener:");
            sb.append(listener != null ? listener.hashCode() : 0);
            AlivcLog.i(TAG, sb.toString());
            this.mEventListener = listener;
            AlivcLog.i(TAG, "[API][End]setRtcEngineEventListener");
        }
    }

    public void setRtcEngineNotify(AliRtcEngineNotify engineNotify) {
        if (engineNotify != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[API]setRtcEngineNotify:listener:");
            sb.append(engineNotify != null ? engineNotify.hashCode() : 0);
            AlivcLog.i(TAG, sb.toString());
            this.mNotifyListener = engineNotify;
            AlivcLog.i(TAG, "[API][End]setRtcEngineNotify");
        }
    }

    public void RegisterAudioObserver(ALI_RTC_INTERFACE.AliAudioType type, ALI_RTC_INTERFACE.AliAudioObserver observer) {
        if (this.mSophonEngine != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[API]RegisterAudioObserver:AliAudioType:");
            sb.append(type);
            sb.append("&&observer:");
            sb.append(observer != null ? observer.hashCode() : 0);
            AlivcLog.i(TAG, sb.toString());
            this.mSophonEngine.RegisterAudioObserver(type, observer);
        }
    }

    public void UnRegisterAudioObserver(ALI_RTC_INTERFACE.AliAudioType type) {
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]UnRegisterAudioObserver:AliAudioType:" + type);
            this.mSophonEngine.UnRegisterAudioObserver(type);
        }
    }

    public void RegisterVideoSampleObserver(final ALI_RTC_INTERFACE.AliVideoObserver observer) {
        if (this.mSophonEngine != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[API]RegisterVideoSampleObserver:&&observer");
            sb.append(observer != null ? observer.hashCode() : 0);
            AlivcLog.i(TAG, sb.toString());
            this.mSophonEngine.RegisterVideoObserver(new ALI_RTC_INTERFACE.AliVideoObserver() {
                public void onLocalVideoSample(ALI_RTC_INTERFACE.AliVideoSourceType sourceType, ALI_RTC_INTERFACE.AliVideoSample videoSample) {
                    observer.onLocalVideoSample(sourceType, videoSample);
                }

                public void onRemoteVideoSample(String callId, ALI_RTC_INTERFACE.AliVideoSourceType sourceType, ALI_RTC_INTERFACE.AliVideoSample videoSample) {
                    observer.onRemoteVideoSample(callId, sourceType, videoSample);
                }
            });
        }
    }

    public void UnRegisterVideoSampleObserver() {
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]UnRegisterVideoSampleObserver");
            this.mSophonEngine.UnRegisterVideoObserver();
        }
    }

    public void RegisterPreprocessVideoObserver(ALI_RTC_INTERFACE.AliDetectObserver observer) {
        StringBuilder sb = new StringBuilder();
        sb.append("[API]RegisterPreprocessVideoObserver:observer: ");
        sb.append(observer != null ? observer.hashCode() : 0);
        AlivcLog.i(TAG, sb.toString());
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine != null) {
            sophonEngine.RegisterPreprocessVideoObserver(observer);
        }
        AlivcLog.i(TAG, "[API][End]RegisterPreprocessVideoObserver");
    }

    public void UnRegisterPreprocessVideoObserver() {
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]UnRegisterPreprocessVideoObserver");
            this.mSophonEngine.UnRegisterPreprocessVideoObserver();
            AlivcLog.i(TAG, "[API][End]UnRegisterPreprocessVideoObserver");
        }
    }

    public void RegisterTexturePreObserver(String userId, final ALI_RTC_INTERFACE.AliTextureObserver observer) {
        if (this.mSophonEngine != null) {
            String callId = userId;
            if (!(userId == null || userId.equals("") || this.mAliRtcConfig.getRemoteParticipants().get(userId) == null)) {
                callId = this.mAliRtcConfig.getRemoteParticipants().get(userId).getCallID();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[API]RegisterTexturePreObserver:userId:");
            sb.append(userId);
            sb.append("&&observer: ");
            sb.append(observer != null ? observer.hashCode() : 0);
            AlivcLog.i(TAG, sb.toString());
            this.mSophonEngine.RegisterTexturePreObserver(callId, new ALI_RTC_INTERFACE.AliTextureObserver() {
                public void onTextureCreate(String callId, long context) {
                    String userId = callId;
                    if (!(callId == null || callId.equals("") || AliRtcEngineImpl.this.findParticipantByCallID(callId) == null)) {
                        userId = AliRtcEngineImpl.this.findParticipantByCallID(callId).getUserID();
                    }
                    observer.onTextureCreate(userId, context);
                }

                public int onTexture(String callId, int textureId, int width, int height, int stride, int rotate, long extraData) {
                    String userId = callId;
                    if (!(callId == null || callId.equals("") || AliRtcEngineImpl.this.findParticipantByCallID(callId) == null)) {
                        userId = AliRtcEngineImpl.this.findParticipantByCallID(callId).getUserID();
                    }
                    return observer.onTexture(userId, textureId, width, height, stride, rotate, extraData);
                }

                public void onTextureDestroy(String callId) {
                    String userId = callId;
                    if (!(callId == null || callId.equals("") || AliRtcEngineImpl.this.findParticipantByCallID(callId) == null)) {
                        userId = AliRtcEngineImpl.this.findParticipantByCallID(callId).getUserID();
                    }
                    observer.onTextureDestroy(userId);
                }
            });
        }
    }

    public void UnRegisterTexturePreObserver(String callId) {
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "UnRegisterTexturePreObserver: callID:" + callId);
            this.mSophonEngine.UnRegisterTexturePreObserver(callId);
        }
    }

    public void RegisterTexturePostObserver(String userId, final ALI_RTC_INTERFACE.AliTextureObserver observer) {
        if (this.mSophonEngine != null) {
            String callId = userId;
            if (!(userId == null || userId.equals("") || this.mAliRtcConfig.getRemoteParticipants().get(userId) == null)) {
                callId = this.mAliRtcConfig.getRemoteParticipants().get(userId).getCallID();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[API]RegisterTexturePostObserver: userId:");
            sb.append(userId);
            sb.append("&&observer: ");
            sb.append(observer != null ? observer.hashCode() : 0);
            AlivcLog.i(TAG, sb.toString());
            this.mSophonEngine.RegisterTexturePostObserver(callId, new ALI_RTC_INTERFACE.AliTextureObserver() {
                public void onTextureCreate(String callId, long context) {
                    String userId = callId;
                    if (!(callId == null || callId.equals("") || AliRtcEngineImpl.this.findParticipantByCallID(callId) == null)) {
                        userId = AliRtcEngineImpl.this.findParticipantByCallID(callId).getUserID();
                    }
                    observer.onTextureCreate(userId, context);
                }

                public int onTexture(String callId, int textureId, int width, int height, int stride, int rotate, long extraData) {
                    String userId = callId;
                    if (!(callId == null || callId.equals("") || AliRtcEngineImpl.this.findParticipantByCallID(callId) == null)) {
                        userId = AliRtcEngineImpl.this.findParticipantByCallID(callId).getUserID();
                    }
                    return observer.onTexture(userId, textureId, width, height, stride, rotate, extraData);
                }

                public void onTextureDestroy(String callId) {
                    String userId = callId;
                    if (!(callId == null || callId.equals("") || AliRtcEngineImpl.this.findParticipantByCallID(callId) == null)) {
                        userId = AliRtcEngineImpl.this.findParticipantByCallID(callId).getUserID();
                    }
                    observer.onTextureDestroy(userId);
                }
            });
        }
    }

    public void UnRegisterTexturePostObserver(String callId) {
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]UnRegisterTexturePostObserver: callId:" + callId);
            this.mSophonEngine.UnRegisterTexturePostObserver(callId);
        }
    }

    public void RegisterRGBAObserver(String userId, final ALI_RTC_INTERFACE.AliRenderDataObserver observer) {
        if (this.mSophonEngine != null) {
            String callId = userId;
            if (!(userId == null || userId.equals("") || this.mAliRtcConfig.getRemoteParticipants().get(userId) == null)) {
                callId = this.mAliRtcConfig.getRemoteParticipants().get(userId).getCallID();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[API]RegisterRGBAObserver: userId:");
            sb.append(userId);
            sb.append("&&observer: ");
            sb.append(observer != null ? observer.hashCode() : 0);
            AlivcLog.i(TAG, sb.toString());
            this.mSophonEngine.RegisterRGBAObserver(callId, new ALI_RTC_INTERFACE.AliRenderDataObserver() {
                public void onRenderData(String callId, long dataPtr, int format, int width, int height, int stride, int rotation, long extraData) {
                    String userId = callId;
                    if (!(callId == null || callId.equals("") || AliRtcEngineImpl.this.findParticipantByCallID(callId) == null)) {
                        userId = AliRtcEngineImpl.this.findParticipantByCallID(callId).getUserID();
                    }
                    observer.onRenderData(userId, dataPtr, format, width, height, stride, rotation, extraData);
                }
            });
        }
    }

    public void UnRegisterRGBAObserver(String callId) {
        AlivcLog.i(TAG, "UnRegisterRGBAObserver observer: ");
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]UnRegisterRGBAObserver: callId:" + callId);
            this.mSophonEngine.UnRegisterRGBAObserver(callId);
        }
    }

    public void enableBackgroundAudioRecording(boolean enable) {
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]enableBackgroundAudioRecording: enable:" + enable);
            this.mSophonEngine.enableBackgroundAudioRecording(enable);
            AlivcLog.i(TAG, "[API][End]enableBackgroundAudioRecording");
        }
    }

    public boolean isEnableBackgroundAudioRecording() {
        if (this.mSophonEngine == null) {
            return false;
        }
        AlivcLog.i(TAG, "[API]isEnableBackgroundAudioRecording:");
        boolean isEnable = this.mSophonEngine.isEnableBackgroundAudioRecording();
        AlivcLog.i(TAG, "[API][End][Result]isEnableBackgroundAudioRecording:" + isEnable);
        return isEnable;
    }

    public void setCollectStatusListener(CollectStatusListener collectStatusListener) {
        if (this.mSophonEngine != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[API]setCollectStatusListener:collectStatusListener:");
            sb.append(collectStatusListener != null ? collectStatusListener.hashCode() : 0);
            AlivcLog.i(TAG, sb.toString());
            this.mSophonEngine.setCollectStatusListener(collectStatusListener);
        }
    }

    public ALI_RTC_INTERFACE.VideoRawDataInterface registerVideoRawDataInterface(ALI_RTC_INTERFACE.AliRawDataStreamType streamType) {
        if (this.mSophonEngine == null) {
            return null;
        }
        AlivcLog.i(TAG, "[API]registerVideoRawDataInterface:streamType:" + streamType);
        return this.mSophonEngine.registerVideoRawDataInterface(streamType);
    }

    public void unRegisterVideoRawDataInterface(ALI_RTC_INTERFACE.AliRawDataStreamType streamType) {
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]unRegisterVideoRawDataInterface:streamType:" + streamType);
            this.mSophonEngine.unRegisterVideoRawDataInterface(streamType);
        }
    }

    /* access modifiers changed from: private */
    public void configRemoteParticipantDefaultValues(RemoteParticipant info) {
        info.setUcAudeoSubed(DefaultValueConstant.SUB_AUDIO);
        info.setUcVideoSubed(DefaultValueConstant.SUB_CAMERA);
        info.setUcVideoSubedMaster(DefaultValueConstant.SUB_CAMERA_MASTER);
        info.setUcScreenSubed(DefaultValueConstant.SUB_SCREEN);
    }

    private ALI_RTC_INTERFACE.AliPublishConfig getConfig() {
        boolean camBig = false;
        boolean camSmall = false;
        boolean screen = false;
        boolean supportSuper = false;
        boolean audio = this.mAliRtcConfig.isPublishAudio();
        boolean isDual = this.mAliRtcConfig.isDualStream();
        if (!this.mAliRtcConfig.isAudioOnly()) {
            if (this.mAliRtcConfig.isPublishCameraTrack()) {
                if (DeviceConfig.isSupportSuperStream()) {
                    supportSuper = true;
                    camBig = true;
                } else {
                    camBig = true;
                }
                if (isDual) {
                    camSmall = true;
                }
            }
            if (this.mAliRtcConfig.isPublishScreenTrack()) {
                screen = true;
            }
        }
        ALI_RTC_INTERFACE.AliPublishConfig config = new ALI_RTC_INTERFACE.AliPublishConfig();
        boolean[] videoTracks = new boolean[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_MAX.getValue()];
        videoTracks[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge.ordinal()] = camBig;
        videoTracks[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSmall.ordinal()] = camSmall;
        videoTracks[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare.ordinal()] = screen;
        videoTracks[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraSuper.ordinal()] = supportSuper;
        config.audio_track = audio;
        config.video_tracks = videoTracks;
        int[] videoTrackProfile = new int[ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_MAX.getValue()];
        int count = videoTracks.length;
        for (int i = 0; i < count; i++) {
            if (videoTracks[i]) {
                videoTrackProfile[i] = this.mAliRtcConfig.getCamVideoProfile().getId();
                AlivcLog.i(TAG, "videotrack index : " + i + ", videoprofile : " + this.mAliRtcConfig.getCamVideoProfile().toString());
            }
        }
        config.video_track_profile = videoTrackProfile;
        return config;
    }

    /* access modifiers changed from: private */
    public RemoteParticipant findParticipantByCallID(String callID) {
        return this.mAliRtcConfig.getRemotePublishParticipants().get(callID);
    }

    /* access modifiers changed from: private */
    public void addRemoteDisplayWindow(String callID, AliRtcEngine.AliVideoCanvas aliVideoCanvas, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type videoSourceType) {
        if (aliVideoCanvas == null || (aliVideoCanvas.textureId == 0 && aliVideoCanvas.view == null)) {
            AlivcLog.e(TAG, "addRemoteDisplayWindow: canvas is null");
            return;
        }
        SophonEngine.AliRendererConfig aliRendererConfig = new SophonEngine.AliRendererConfig();
        if (aliVideoCanvas.textureId > 0) {
            AlivcLog.e(TAG, "addRemoteDisplayWindow: videoCanvas.textureId != 0 videoCanvas.textureId is = " + aliVideoCanvas.textureId + "aliRendererConfig.sharedContext" + aliVideoCanvas.sharedContext);
            aliRendererConfig.textureId = aliVideoCanvas.textureId;
            aliRendererConfig.textureWidth = aliVideoCanvas.textureWidth;
            aliRendererConfig.textureHeight = aliVideoCanvas.textureHeight;
            aliRendererConfig.sharedContext = aliVideoCanvas.sharedContext;
        } else {
            SophonSurfaceView surfaceView = aliVideoCanvas.view;
            if (surfaceView != null) {
                aliRendererConfig.displayView = surfaceView;
                aliRendererConfig.width = surfaceView.getWidth();
                aliRendererConfig.height = surfaceView.getHeight();
            }
        }
        aliRendererConfig.displayMode = aliVideoCanvas.renderMode.ordinal();
        aliVideoCanvas.flip = getIsFlip(aliVideoCanvas.mirrorMode, getPreCameraType(), true);
        aliRendererConfig.flip = aliVideoCanvas.flip;
        aliRendererConfig.sharedContext = this.mAliRtcConfig.getSharedContext();
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine != null) {
            sophonEngine.addRemoteDisplayWindow(callID, videoSourceType, aliRendererConfig);
        }
    }

    /* access modifiers changed from: private */
    public void removeRemoteNullTracksDisplayWindow(String callId, String[] videoTracks) {
        if (!TextUtils.isEmpty(callId) && videoTracks != null) {
            String allTracksStr = Arrays.toString(videoTracks);
            if (!allTracksStr.contains(ALI_RTC_INTERFACE.CAMERA_STRING) && !allTracksStr.contains(ALI_RTC_INTERFACE.SMALL_STRING) && !allTracksStr.contains(ALI_RTC_INTERFACE.SUPER_STRING)) {
                removeRemoteDisplayWindow(callId, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge);
            }
            if (!allTracksStr.contains(ALI_RTC_INTERFACE.SCREEN_STRING)) {
                removeRemoteDisplayWindow(callId, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_ScreenShare);
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeRemoteDisplayWindow(String callId, ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type videoSourceType) {
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine != null) {
            sophonEngine.removeRemoteDisplayWindow(callId, videoSourceType);
        }
    }

    public void addLocalDisplayWindow(AliRtcEngine.AliVideoCanvas videoCanvas) {
        if (videoCanvas.textureId > 0 || videoCanvas.view != null) {
            SophonEngine.AliRendererConfig config = new SophonEngine.AliRendererConfig();
            if (videoCanvas.textureId > 0) {
                AlivcLog.e(TAG, "addLocalDisplayWindow: videoCanvas.textureId != 0 videoCanvas.textureId is = " + videoCanvas.textureId + "videoCanvas.textureWidth:" + videoCanvas.textureWidth + "videoCanvas.textureHeight" + videoCanvas.textureHeight);
                config.textureId = videoCanvas.textureId;
                StringBuilder sb = new StringBuilder();
                sb.append("addLocalDisplayWindow1: config.textureId is = ");
                sb.append(config.textureId);
                AlivcLog.e(TAG, sb.toString());
                config.textureWidth = videoCanvas.textureWidth;
                config.textureHeight = videoCanvas.textureHeight;
            } else if (this.mAliRtcConfig.getLocalVideoCanvas().view != null) {
                config.displayView = this.mAliRtcConfig.getLocalVideoCanvas().view;
                config.width = config.displayView.getWidth();
                config.height = config.displayView.getHeight();
                AlivcLog.e(TAG, "addLocalDisplayWindow: textureId is = " + videoCanvas.textureId + " ,w:" + config.width + " ,h:" + config.height);
            }
            config.displayMode = videoCanvas.renderMode.ordinal();
            videoCanvas.flip = getIsFlip(videoCanvas.mirrorMode, getPreCameraType(), false);
            config.flip = videoCanvas.flip;
            config.sharedContext = videoCanvas.sharedContext;
            config.enableBeauty = videoCanvas.enableBeauty;
            if (this.mSophonEngine != null) {
                AlivcLog.i(TAG, "[API]addLocalDisplayWindow: VideSource_Type:" + ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge + "&&config:" + config.toString());
                this.mSophonEngine.addLocalDisplayWindow(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge, config);
                return;
            }
            return;
        }
        AlivcLog.e(TAG, "addLocalDisplayWindow: canvas is null");
    }

    private void removeLocalDisplayWindow() {
        if (this.mAliRtcConfig.getLocalVideoCanvas() == null || (this.mAliRtcConfig.getLocalVideoCanvas().view == null && this.mAliRtcConfig.getLocalVideoCanvas().textureId <= 0)) {
            AlivcLog.e(TAG, "removeLocalDisplayWindow: canvas is null");
        } else if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]addLocalDisplayWindow: VideSource_Type:" + ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge);
            this.mSophonEngine.removeLocalDisplayWindow(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type.AliRTCSDK_VideoSource_Type_CameraLarge);
        }
    }

    private void updateDisplayWindow(AliRtcEngine.AliVideoCanvas videoCanvas) {
        if (this.mSophonEngine != null) {
            SophonEngine.AliRendererConfig aliRendererConfig = new SophonEngine.AliRendererConfig();
            aliRendererConfig.displayMode = videoCanvas.renderMode.ordinal();
            if (videoCanvas.textureId > 0) {
                aliRendererConfig.textureId = videoCanvas.textureId;
                aliRendererConfig.textureWidth = videoCanvas.textureWidth;
                aliRendererConfig.textureHeight = videoCanvas.textureHeight;
                aliRendererConfig.width = videoCanvas.textureWidth;
                aliRendererConfig.height = videoCanvas.textureHeight;
                AlivcLog.e(TAG, "updateDisplayWindow videoCanvas.textureId" + videoCanvas.textureId);
            }
            if (videoCanvas.view != null) {
                aliRendererConfig.displayView = videoCanvas.view;
                aliRendererConfig.width = videoCanvas.view.getWidth();
                aliRendererConfig.height = videoCanvas.view.getHeight();
                AlivcLog.e(TAG, "updateDisplayWindow view != null");
            } else {
                AlivcLog.e(TAG, "updateDisplayWindow view == null");
            }
            aliRendererConfig.flip = videoCanvas.flip;
            AlivcLog.i(TAG, "[API][Callback]addLocalDisplayWindow: config:" + aliRendererConfig.toString());
            this.mSophonEngine.updateDisplayWindow(aliRendererConfig);
        }
    }

    private boolean getIsFlip(AliRtcEngine.AliRtcRenderMirrorMode mirrorMode, int cameraType, boolean isRemote) {
        if (mirrorMode == null) {
            return true;
        }
        int i = AnonymousClass7.$SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcRenderMirrorMode[mirrorMode.ordinal()];
        if (i != 1) {
            if (i != 2) {
                return false;
            }
            return true;
        } else if (cameraType != AliRtcEngine.AliRTCCameraType.AliRTCCameraFront.getCameraType() || isRemote) {
            return false;
        } else {
            return true;
        }
    }

    /* access modifiers changed from: private */
    public String getSubscribeConfigString(ALI_RTC_INTERFACE.AliSubscribeConfig aliSubscribeConfig) {
        StringBuffer stringBuffer = new StringBuffer();
        if (aliSubscribeConfig != null) {
            stringBuffer.append(aliSubscribeConfig.stream_label);
            stringBuffer.append(",");
            if (aliSubscribeConfig.video_track_labels != null) {
                for (String append : aliSubscribeConfig.video_track_labels) {
                    stringBuffer.append(append);
                    stringBuffer.append(",");
                }
            }
            stringBuffer.append(aliSubscribeConfig.audio_track_label);
        }
        return stringBuffer.toString();
    }

    /* access modifiers changed from: private */
    public String getOsInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt(AliRtcConstants.OS_NAME, AliRtcConstants.ANDROID);
            jsonObject.putOpt(AliRtcConstants.OS_SDK, Integer.valueOf(Build.VERSION.SDK_INT));
            jsonObject.putOpt(AliRtcConstants.OS_VERSION, Build.VERSION.RELEASE);
            jsonObject.putOpt(AliRtcConstants.OS_CPUABI, Build.CPU_ABI);
            jsonObject.putOpt(AliRtcConstants.DEVICENAME, Build.MODEL);
            jsonObject.putOpt(AliRtcConstants.BRAND, Build.BRAND);
            jsonObject.putOpt(AliRtcConstants.PLATFORM, Build.HARDWARE);
            jsonObject.putOpt(AliRtcConstants.ACCESS, AliRtcEngineUtil.getNetWorkStatus(this.mContext));
            jsonObject.putOpt(AliRtcConstants.CARRIER, AliRtcEngineUtil.getOperators(this.mContext));
            jsonObject.putOpt(AliRtcConstants.CPU_TYPE, "");
            jsonObject.putOpt(AliRtcConstants.UDID, UTDevice.getUtdid(this.mContext));
            String str = AliRtcConstants.SCREEN_RESOLUTION;
            jsonObject.putOpt(str, AliRtcEngineUtil.getWindowHeight(this.mContext) + "x" + AliRtcEngineUtil.getWindowWidth(this.mContext));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void setTraceId(String traceId) {
        if (this.mSophonEngine != null) {
            AlivcLog.i(TAG, "[API]setTraceId: traceId:" + traceId);
            this.mSophonEngine.setTraceId(traceId);
            AlivcLog.i(TAG, "[API][End]setTraceId");
        }
    }

    public String getMediaInfoWithUserId(String userId, AliRtcEngine.AliRtcVideoTrack track, String[] keys) {
        AlivcLog.i(TAG, "[API]getMediaInfoWithUserId: &&userCallId" + userId + "&&traceId:" + track.toString() + "&&keys:" + Arrays.toString(keys));
        String ret = null;
        if (this.mSophonEngine != null) {
            RemoteParticipant user = null;
            if (userId != null) {
                user = this.mAliRtcConfig.getRemoteParticipants().get(userId);
            }
            String userCallId = user != null ? user.getCallID() : "";
            if (TextUtils.isEmpty(userCallId)) {
                track = null;
            }
            String trackId = "";
            if (track != null) {
                int i = AnonymousClass7.$SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack[track.ordinal()];
                if (i == 1) {
                    trackId = DeviceConfig.isSupportSuperStream() ? ALI_RTC_INTERFACE.SUPER_STRING : ALI_RTC_INTERFACE.CAMERA_STRING;
                } else if (i == 2 || i == 3) {
                    trackId = ALI_RTC_INTERFACE.SCREEN_STRING;
                }
            }
            ret = this.mSophonEngine.getMediaInfo(userCallId, trackId, keys);
        }
        AlivcLog.i(TAG, "[API][End][Result]getMediaInfoWithUserId:" + ret);
        return ret;
    }

    public int startAudioCapture() {
        AlivcLog.i(TAG, "[API]startAudioCapture");
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine == null) {
            return -1;
        }
        int ret = sophonEngine.startAudioCapture();
        AlivcLog.i(TAG, "[API][End][Result]startAudioCapture:" + ret);
        return ret;
    }

    public int stopAudioCapture() {
        AlivcLog.i(TAG, "[API]stopAudioCapture");
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine == null) {
            return -1;
        }
        int ret = sophonEngine.startAudioCapture();
        AlivcLog.i(TAG, "[API][End][Result]stopAudioCapture:" + ret);
        return ret;
    }

    public int startAudioPlayer() {
        AlivcLog.i(TAG, "[API]startAudioPlayer");
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine == null) {
            return -1;
        }
        int ret = sophonEngine.startAudioPlayer();
        AlivcLog.i(TAG, "[API][End][Result]startAudioPlayer:" + ret);
        return ret;
    }

    public int stopAudioPlayer() {
        AlivcLog.i(TAG, "[API]stopAudioPlayer");
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine == null) {
            return -1;
        }
        int ret = sophonEngine.stopAudioPlayer();
        AlivcLog.i(TAG, "[API][End][Result]stopAudioPlayer:" + ret);
        return ret;
    }

    public void setDeviceOrientationMode(AliRtcEngine.AliRtcOrientationMode mode) {
        if (this.mSophonEngine != null) {
            AlivcLog.e(TAG, "[API]setDeviceOrientationMode: " + mode.ordinal());
            ALI_RTC_INTERFACE.Ali_RTC_Device_Orientation_Mode ret = ALI_RTC_INTERFACE.Ali_RTC_Device_Orientation_Mode.Ali_RTC_Device_Orientation_0;
            int i = AnonymousClass7.$SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcOrientationMode[mode.ordinal()];
            if (i == 1) {
                ret = ALI_RTC_INTERFACE.Ali_RTC_Device_Orientation_Mode.Ali_RTC_Device_Orientation_0;
            } else if (i == 2) {
                ret = ALI_RTC_INTERFACE.Ali_RTC_Device_Orientation_Mode.Ali_RTC_Device_Orientation_90;
            } else if (i == 3) {
                ret = ALI_RTC_INTERFACE.Ali_RTC_Device_Orientation_Mode.Ali_RTC_Device_Orientation_270;
            } else if (i == 4) {
                ret = ALI_RTC_INTERFACE.Ali_RTC_Device_Orientation_Mode.Ali_RTC_Device_Orientation_Adaptive;
            }
            this.mSophonEngine.setDeviceOrientationMode(ret);
            AlivcLog.e(TAG, "[API][End]setDeviceOrientationMode");
        }
    }

    /* renamed from: com.alivc.rtc.AliRtcEngineImpl$7  reason: invalid class name */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcOrientationMode;
        static final /* synthetic */ int[] $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcRenderMirrorMode;
        static final /* synthetic */ int[] $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack;
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$alirtcInterface$ALI_RTC_INTERFACE$AliRTCMediaConnectionReConnectState;

        static {
            int[] iArr = new int[AliRtcEngine.AliRtcOrientationMode.values().length];
            $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcOrientationMode = iArr;
            try {
                iArr[AliRtcEngine.AliRtcOrientationMode.AliRtcOrientationModePortrait.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcOrientationMode[AliRtcEngine.AliRtcOrientationMode.AliRtcOrientationModeLandscapeLeft.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcOrientationMode[AliRtcEngine.AliRtcOrientationMode.AliRtcOrientationModeLandscapeRight.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcOrientationMode[AliRtcEngine.AliRtcOrientationMode.AliRtcOrientationModeAuto.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            int[] iArr2 = new int[AliRtcEngine.AliRtcVideoTrack.values().length];
            $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack = iArr2;
            try {
                iArr2[AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack[AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcVideoTrack[AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            int[] iArr3 = new int[AliRtcEngine.AliRtcRenderMirrorMode.values().length];
            $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcRenderMirrorMode = iArr3;
            try {
                iArr3[AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeOnlyFront.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcRenderMirrorMode[AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllEnabled.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$alivc$rtc$AliRtcEngine$AliRtcRenderMirrorMode[AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllDisable.ordinal()] = 3;
            } catch (NoSuchFieldError e10) {
            }
            int[] iArr4 = new int[ALI_RTC_INTERFACE.AliRTCMediaConnectionReConnectState.values().length];
            $SwitchMap$org$webrtc$alirtcInterface$ALI_RTC_INTERFACE$AliRTCMediaConnectionReConnectState = iArr4;
            try {
                iArr4[ALI_RTC_INTERFACE.AliRTCMediaConnectionReConnectState.AliRTC_MeidaConnection_ReConnect_Failed.ordinal()] = 1;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$webrtc$alirtcInterface$ALI_RTC_INTERFACE$AliRTCMediaConnectionReConnectState[ALI_RTC_INTERFACE.AliRTCMediaConnectionReConnectState.AliRTC_MeidaConnection_ReConnect_Connected.ordinal()] = 2;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$org$webrtc$alirtcInterface$ALI_RTC_INTERFACE$AliRTCMediaConnectionReConnectState[ALI_RTC_INTERFACE.AliRTCMediaConnectionReConnectState.AliRTC_MeidaConnection_ReConnect_Connecting.ordinal()] = 3;
            } catch (NoSuchFieldError e13) {
            }
        }
    }

    public int startAudioAccompany(String fileName, boolean onlyLocalPlay, boolean replaceMic, int loopCycles) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.e(TAG, "[API]startAudioAccompany:fileName:" + fileName + "&&onlyLocalPlay:" + onlyLocalPlay + "&&replaceMic" + replaceMic + "&&loopCycles" + loopCycles);
        int ret = this.mSophonEngine.startAudioAccompany(fileName, onlyLocalPlay, replaceMic, loopCycles);
        StringBuilder sb = new StringBuilder();
        sb.append("[API][End][Result]startAudioAccompany:");
        sb.append(ret);
        AlivcLog.i(TAG, sb.toString());
        return ret;
    }

    public int stopAudioAccompany() {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]stopAudioAccompany");
        int ret = this.mSophonEngine.stopAudioAccompany();
        AlivcLog.i(TAG, "[API][End]stopAudioAccompany:" + ret);
        return ret;
    }

    public int setAudioAccompanyVolume(int volume) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]setAudioAccompanyVolume:volume:" + volume);
        int ret = this.mSophonEngine.setAudioAccompanyVolume(volume);
        AlivcLog.i(TAG, "[API][End][Result]setAudioAccompanyVolume:" + ret);
        return ret;
    }

    public int setAudioAccompanyPublishVolume(int volume) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]setAudioAccompanyPublishVolume:volume:" + volume);
        int ret = this.mSophonEngine.SetAudioAccompanyPublishVolume(volume);
        AlivcLog.i(TAG, "[API][End][Result]setAudioAccompanyPublishVolume:" + ret);
        return ret;
    }

    public int getAudioAccompanyPublishVolume() {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]GetAudioAccompanyPublishVolume");
        int ret = this.mSophonEngine.GetAudioAccompanyPublishVolume();
        AlivcLog.i(TAG, "[API][End][Result]GetAudioAccompanyPublishVolume:" + ret);
        return ret;
    }

    public int setAudioAccompanyPlayoutVolume(int volume) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]setAudioAccompanyPlayoutVolume:volume:" + volume);
        int ret = this.mSophonEngine.SetAudioAccompanyPlayoutVolume(volume);
        AlivcLog.i(TAG, "[API][End][Result]setAudioAccompanyPlayoutVolume:" + ret);
        return ret;
    }

    public int getAudioAccompanyPlayoutVolume() {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]GetAudioAccompanyPlayoutVolume");
        int ret = this.mSophonEngine.GetAudioAccompanyPlayoutVolume();
        AlivcLog.i(TAG, "[API][End][Result]GetAudioAccompanyPlayoutVolume:" + ret);
        return ret;
    }

    public int pauseAudioAccompany() {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]pauseAudioAccompany");
        int ret = this.mSophonEngine.PauseAudioMixing();
        AlivcLog.i(TAG, "[API][End][Result]pauseAudioAccompany:" + ret);
        return ret;
    }

    public int resumeAudioAccompany() {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]resumeAudioAccompany");
        int ret = this.mSophonEngine.ResumeAudioMixing();
        AlivcLog.i(TAG, "[API][End][Result]resumeAudioAccompany:" + ret);
        return ret;
    }

    public int preloadAudioEffect(int soundId, String filePath) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]preloadAudioEffect:soundId:" + soundId + "&&filePath:" + filePath);
        int ret = this.mSophonEngine.PreloadAudioEffect(soundId, filePath);
        StringBuilder sb = new StringBuilder();
        sb.append("[API][End][Result]preloadAudioEffect:");
        sb.append(ret);
        AlivcLog.i(TAG, sb.toString());
        return ret;
    }

    public int unloadAudioEffect(int soundId) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]UnloadAudioEffect:soundId:" + soundId);
        int ret = this.mSophonEngine.UnloadAudioEffect(soundId);
        AlivcLog.i(TAG, "[API][End][Result]UnloadAudioEffect:" + ret);
        return ret;
    }

    public int playAudioEffect(int soundId, String filePath, int cycles, boolean publish) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]playAudioEffect:soundId:" + soundId + "&&filePath:" + filePath + "&&cycles:" + cycles + "&&publish:" + publish);
        int ret = this.mSophonEngine.PlayAudioEffect(soundId, filePath, cycles, publish);
        StringBuilder sb = new StringBuilder();
        sb.append("[API][End][Result]playAudioEffect:");
        sb.append(ret);
        AlivcLog.i(TAG, sb.toString());
        return ret;
    }

    public int stopAudioEffect(int soundId) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]StopAudioEffect:soundId:" + soundId);
        int ret = this.mSophonEngine.StopAudioEffect(soundId);
        AlivcLog.i(TAG, "[API][Result]StopAudioEffect:" + ret);
        return ret;
    }

    public int setAudioEffectPublishVolume(int soundId, int volume) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]SetAudioEffectPublishVolume:soundId:" + soundId + "&&volume" + volume);
        int ret = this.mSophonEngine.SetAudioEffectPublishVolume(soundId, volume);
        StringBuilder sb = new StringBuilder();
        sb.append("[API][End][Result]SetAudioEffectPublishVolume:");
        sb.append(ret);
        AlivcLog.i(TAG, sb.toString());
        return ret;
    }

    public int getAudioEffectPublishVolume(int soundId) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]GetAudioEffectPublishVolume:soundId:" + soundId);
        int ret = this.mSophonEngine.GetAudioEffectPublishVolume(soundId);
        AlivcLog.i(TAG, "[API][End][Result]GetAudioEffectPublishVolume:" + ret);
        return ret;
    }

    public int setAudioEffectPlayoutVolume(int soundId, int volume) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]SetAudioEffectPlayoutVolume:soundId:" + soundId + "&&volume" + volume);
        int ret = this.mSophonEngine.SetAudioEffectPlayoutVolume(soundId, volume);
        StringBuilder sb = new StringBuilder();
        sb.append("[API][End][Result]SetAudioEffectPlayoutVolume:");
        sb.append(ret);
        AlivcLog.i(TAG, sb.toString());
        return ret;
    }

    public int getAudioEffectPlayoutVolume(int soundId) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]GetAudioEffectPlayoutVolume:soundId:" + soundId);
        int ret = this.mSophonEngine.GetAudioEffectPlayoutVolume(soundId);
        AlivcLog.i(TAG, "[API][End][Result]GetAudioEffectPlayoutVolume:" + ret);
        return ret;
    }

    public int pauseAudioEffect(int soundId) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]PauseAudioEffect:soundId:" + soundId);
        int ret = this.mSophonEngine.PauseAudioEffect(soundId);
        AlivcLog.i(TAG, "[API][End][Result]PauseAudioEffect:" + ret);
        return ret;
    }

    public int resumeAudioEffect(int soundId) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]ResumeAudioEffect:soundId:" + soundId);
        int ret = this.mSophonEngine.ResumeAudioEffect(soundId);
        AlivcLog.i(TAG, "[API][End][Result]ResumeAudioEffect:" + ret);
        return ret;
    }

    public int enableEarBack(boolean enable) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]EnableEarBack:enable:" + enable);
        int ret = this.mSophonEngine.EnableEarBack(enable);
        AlivcLog.i(TAG, "[API][End][Result]EnableEarBack:" + ret);
        return ret;
    }

    public int setEarBackVolume(int volume) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]SetEarBackVolume:volume:" + volume);
        int ret = this.mSophonEngine.SetEarBackVolume(volume);
        AlivcLog.i(TAG, "[API][End][Result]SetEarBackVolume:" + ret);
        return ret;
    }

    public int setChannelProfile(ALI_RTC_INTERFACE.AliRTCSDK_Channel_Profile channel_profile) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]SetChannelProfile:channel_profile:" + channel_profile.getValue());
        int ret = this.mSophonEngine.SetChannelProfile(channel_profile);
        AlivcLog.i(TAG, "[API][End][Result]SetChannelProfile:" + ret);
        return ret;
    }

    public int setClientRole(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role client_role) {
        SophonEngine sophonEngine = this.mSophonEngine;
        if (sophonEngine == null) {
            return -1;
        }
        int ret = sophonEngine.SetClientRole(client_role);
        AlivcLog.i(TAG, "[API][Result]SetClientRole:client_role:" + client_role + LogUtils.COLON + ret);
        return ret;
    }

    public int generateTexture() {
        if (this.mSophonEngine == null) {
            return -1;
        }
        if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.i(TAG, "[API][Result]generateTexture: audio only mode error:-1");
            return -1;
        }
        int ret = this.mSophonEngine.generateTexture();
        AlivcLog.i(TAG, "generateTexture = " + ret);
        return ret;
    }

    public int setTexture(AliRtcEngine.AliRtcTextureInfo textureInfo, int track, String userId) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        if (textureInfo.textureId <= 0) {
            AlivcLog.i(TAG, "[API] setTexture invalid textureInfo id" + textureInfo.textureId);
            return -1;
        } else if (this.mAliRtcConfig.isAudioOnly()) {
            AlivcLog.i(TAG, "[API][Result]setTexture: audio only mode error:-1");
            return -1;
        } else {
            AliRtcEngine.AliVideoCanvas aliVideoCanvas = new AliRtcEngine.AliVideoCanvas();
            aliVideoCanvas.textureId = textureInfo.textureId;
            aliVideoCanvas.textureWidth = 720;
            aliVideoCanvas.textureHeight = 1280;
            aliVideoCanvas.renderMode = AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
            aliVideoCanvas.mirrorMode = textureInfo.mirrorMode;
            aliVideoCanvas.view = null;
            AliRtcEngine.AliRtcRemoteTextureInfo aliRtcRemoteTextureInfo = new AliRtcEngine.AliRtcRemoteTextureInfo();
            aliRtcRemoteTextureInfo.aliVideoCanvas = aliVideoCanvas;
            aliRtcRemoteTextureInfo.userId = userId;
            aliRtcRemoteTextureInfo.videoTrack = track;
            aliRtcRemoteTextureInfos.add(aliRtcRemoteTextureInfo);
            AlivcLog.i(TAG, "[API] setTexture uid = " + userId + " videoTrack" + track + "textureId" + textureInfo.textureId + "mirrorMode" + textureInfo.mirrorMode);
            StringBuilder sb = new StringBuilder();
            sb.append("userId isEmpty = ");
            sb.append(TextUtils.isEmpty(userId));
            Log.d(TAG, sb.toString());
            if (TextUtils.isEmpty(userId)) {
                return setLocalViewConfig(aliVideoCanvas, swapTrack(track));
            }
            return setRemoteViewConfig(aliVideoCanvas, userId, swapTrack(track));
        }
    }

    private static AliRtcEngine.AliRtcVideoTrack swapTrack(int track) {
        if (track == 0) {
            return AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo;
        }
        if (track == 1) {
            return AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera;
        }
        if (track == 2) {
            return AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen;
        }
        if (track == 4) {
            return AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth;
        }
        throw new IllegalStateException("Unexpected value: " + track);
    }

    public void removeTexture(int textureId) {
        if (this.mSophonEngine != null && !aliRtcRemoteTextureInfos.isEmpty()) {
            AliRtcEngine.AliRtcRemoteTextureInfo needRemoveTextureInfo = null;
            Iterator<AliRtcEngine.AliRtcRemoteTextureInfo> it = aliRtcRemoteTextureInfos.iterator();
            while (it.hasNext()) {
                AliRtcEngine.AliRtcRemoteTextureInfo aliRtcRemoteTextureInfo = it.next();
                if (aliRtcRemoteTextureInfo.aliVideoCanvas.textureId == textureId) {
                    needRemoveTextureInfo = aliRtcRemoteTextureInfo;
                    if (TextUtils.isEmpty(aliRtcRemoteTextureInfo.userId)) {
                        setLocalViewConfig(aliRtcRemoteTextureInfo.aliVideoCanvas, swapTrack(aliRtcRemoteTextureInfo.videoTrack));
                    } else {
                        setRemoteViewConfig(aliRtcRemoteTextureInfo.aliVideoCanvas, aliRtcRemoteTextureInfo.userId, swapTrack(aliRtcRemoteTextureInfo.videoTrack));
                    }
                }
            }
            aliRtcRemoteTextureInfos.remove(needRemoveTextureInfo);
        }
    }

    public int startAudioFileRecording(String file_Name, AliRtcEngine.AliRtcAudioSampleRate sample_Rate, AliRtcEngine.AliRtcAudioCodecQualityType quality) {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]StartAudioFileRecording: filename:" + file_Name + " sample_Rate:" + sample_Rate + " quality:" + quality.getId());
        int ret = this.mSophonEngine.StartAudioFileRecording(file_Name, sample_Rate.getId(), quality.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("[API][End][Result]StartAudioFileRecording:");
        sb.append(ret);
        AlivcLog.i(TAG, sb.toString());
        return ret;
    }

    public int stopAudioFileRecording() {
        if (this.mSophonEngine == null) {
            return -1;
        }
        AlivcLog.i(TAG, "[API]StopAudioFileRecording");
        int ret = this.mSophonEngine.StopAudioFileRecording();
        AlivcLog.i(TAG, "[API][End][Result]StopAudioFileRecording ret:" + ret);
        return ret;
    }
}
