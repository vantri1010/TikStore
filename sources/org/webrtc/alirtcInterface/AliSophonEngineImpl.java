package org.webrtc.alirtcInterface;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;
import org.webrtc.utils.CpuMonitor;
import org.webrtc.utils.MemoryMonitor;
import org.webrtc.utils.NetworkMonitor;
import org.webrtc.utils.RecvStatsReportParam;

public class AliSophonEngineImpl implements AliSophonEngine {
    private static final long NETWORK_OBSERVER = 10001;
    private ALI_RTC_INTERFACE aliRtc;
    private CpuMonitor cpuMonitor;
    private MemoryMonitor memoryMonitor;
    private SophonEventListener sophonEventListener;

    AliSophonEngineImpl(Context context, ALI_RTC_INTERFACE aliRtcInterface, SophonEventListener listener) {
        this.sophonEventListener = listener;
        this.aliRtc = aliRtcInterface;
        this.cpuMonitor = new CpuMonitor(context);
        this.memoryMonitor = new MemoryMonitor(context);
        startMonitoring();
        this.cpuMonitor.resume();
        this.memoryMonitor.resume();
    }

    public void onGslbResult(int result) {
        this.sophonEventListener.onGslbResult(result);
    }

    public void onJoinChannelResult(int result) {
        this.sophonEventListener.onJoinChannelResult(result);
    }

    public void onLeaveChannelResult(int result) {
        this.sophonEventListener.onLeaveChannelResult(result);
    }

    public void onPublishResult(int result, String callId) {
        this.sophonEventListener.onPublishResult(result, callId);
    }

    public void onRepublishResult(int result, String callId) {
        this.sophonEventListener.onRepublishResult(result, callId);
    }

    public void onUnpublishResult(int result, String callId) {
        this.sophonEventListener.onUnpublishResult(result, callId);
    }

    public void onSubscribeResult(int result, String callId) {
        this.sophonEventListener.onSubscribeResult(result, callId);
    }

    public void onResubscribeResult(int result, String callId) {
        this.sophonEventListener.onResubscribeResult(result, callId);
    }

    public void onUnsubscribeResult(int result, String callId) {
        this.sophonEventListener.onUnsubscribeResult(result, callId);
    }

    public void onCollectStatus(String callId, HashMap collectStatus) {
        this.sophonEventListener.onCollectStats(callId, collectStatus);
    }

    public void onConnectionChange(int mediaConState) {
        this.sophonEventListener.onConnectionChange(mediaConState);
    }

    public void onWarning(int warningEvent, String params) {
        this.sophonEventListener.onWarning(warningEvent, params);
    }

    public void onError(int event, String params) {
        this.sophonEventListener.onError(event, params);
    }

    public void onLogMessage(String message) {
        this.sophonEventListener.onLogMessage(message);
    }

    public void onParticipantPublishNotify(PublisherInfo[] publisherList, int publisherCount) {
        this.sophonEventListener.onParticipantPublishNotify(publisherList, publisherCount);
    }

    public void onSubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
        this.sophonEventListener.onSubscribeResult2(result, callID, reqConfig, curConfig);
    }

    public void onResubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
        this.sophonEventListener.onResubscribeResult2(result, callID, reqConfig, curConfig);
    }

    public void onParticipantJoinNotify(AliParticipantInfo[] participantList, int feedCount) {
        this.sophonEventListener.onParticipantJoinNotify(participantList, feedCount);
    }

    public void onParticipantLeaveNotify(AliParticipantInfo[] participantList, int feedCount) {
        this.sophonEventListener.onParticipantLeaveNotify(participantList, feedCount);
    }

    public void onParticipantSubscribeNotify(AliSubscriberInfo[] subcribeinfoList, int feedCount) {
        this.sophonEventListener.onParticipantSubscribeNotify(subcribeinfoList, feedCount);
    }

    public void onParticipantStatusNotify(AliStatusInfo[] status_info_list, int count) {
        this.sophonEventListener.onParticipantStatusNotify(status_info_list, count);
    }

    public void onParticipantUnpublishNotify(AliUnPublisherInfo[] unpublisherList, int feedCount) {
        this.sophonEventListener.onParticipantUnpublishNotify(unpublisherList, feedCount);
    }

    public void onParticipantUnsubscribeNotify(AliParticipantInfo[] participantList, int feedCount) {
        this.sophonEventListener.onParticipantUnsubscribeNotify(participantList, feedCount);
    }

    public void onTransportStatusChange(String callId, ALI_RTC_INTERFACE.TransportType event, ALI_RTC_INTERFACE.TransportStatus status) {
        this.sophonEventListener.onTransportStatusChange(callId, event, status);
    }

    public void onNetworkQualityChange(ArrayList<ALI_RTC_INTERFACE.AliTransportInfo> network_quality) {
        this.sophonEventListener.onNetworkQualityChange(network_quality);
    }

    public void onRecvStatsReport(HashMap map) {
        Map<String, String> generatePublicParamters = RecvStatsReportParam.generatePublicParamters(map, getCurrentConnectionType(), String.valueOf(((double) this.cpuMonitor.getCpuUsageCurrent()) / 100.0d), String.valueOf(this.memoryMonitor.getMemoryUsageCurrentByPid()), this.aliRtc.GetSDKVersion());
    }

    private void startMonitoring() {
        NetworkMonitor.getInstance().startMonitoring(NETWORK_OBSERVER);
    }

    private void stopMonitoring() {
        NetworkMonitor.getInstance().stopMonitoring(NETWORK_OBSERVER);
    }

    private String getCurrentConnectionType() {
        return NetworkMonitor.getInstance().getCurrentConnectionType().toString();
    }

    public void release() {
        stopMonitoring();
        this.cpuMonitor.pause();
        this.memoryMonitor.pause();
    }

    public void onUplinkChannelMessage(int result, String contentType, String content) {
        this.sophonEventListener.onUplinkChannelMessage(result, contentType, content);
    }

    public String onCollectPlatformProfile() {
        return this.sophonEventListener.onCollectPlatformProfile();
    }

    public String onFetchPerformanceInfo() {
        return this.sophonEventListener.onFetchPerformanceInfo();
    }

    public boolean onFetchAudioPermissionInfo() {
        return this.sophonEventListener.onFetchAudioPermissionInfo();
    }

    public String onFetchAudioDeviceInfo() {
        return this.sophonEventListener.onFetchAudioDeviceInfo();
    }

    public void onWindowRenderReady(String callId, int videoType) {
        this.sophonEventListener.onWindowRenderReady(callId, videoType);
    }

    public void onUpdateRoleNotify(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role old_role, ALI_RTC_INTERFACE.AliRTCSDK_Client_Role new_role) {
        this.sophonEventListener.onUpdateRoleNotify(old_role, new_role);
    }

    public void onFirstFrameReceived(String callId, String stream_label, String track_label, int time_cost_ms) {
        this.sophonEventListener.onFirstFramereceived(callId, stream_label, track_label, time_cost_ms);
    }

    public void onFirstPacketSent(String callId, String stream_label, String track_label, int time_cost_ms) {
        this.sophonEventListener.onFirstPacketSent(callId, stream_label, track_label, time_cost_ms);
    }

    public void onFirstPacketReceived(String callId, String stream_label, String track_label, int time_cost_ms) {
        this.sophonEventListener.onFirstPacketReceived(callId, stream_label, track_label, time_cost_ms);
    }

    public void onBye(int code) {
        this.sophonEventListener.onBye(code);
    }

    public void onMessage(String tid, String contentType, String content) {
        this.sophonEventListener.onMessage(tid, contentType, content);
    }

    public int onFetchDeviceOrientation() {
        return this.sophonEventListener.onFetchDeviceOrientation();
    }

    public void onAliRtcStats(ALI_RTC_INTERFACE.AliRtcStats aliRtcStats) {
        this.sophonEventListener.onAliRtcStats(aliRtcStats);
    }
}
