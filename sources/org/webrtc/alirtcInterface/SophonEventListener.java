package org.webrtc.alirtcInterface;

import java.util.ArrayList;
import java.util.HashMap;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;

public abstract class SophonEventListener {
    public void onGslbResult(int result) {
    }

    public void onJoinChannelResult(int result) {
    }

    public void onLeaveChannelResult(int result) {
    }

    public void onPublishResult(int result, String callId) {
    }

    public void onRepublishResult(int result, String callId) {
    }

    public void onUnpublishResult(int result, String callId) {
    }

    public void onSubscribeResult(int result, String callId) {
    }

    public void onResubscribeResult(int result, String callId) {
    }

    public void onUnsubscribeResult(int result, String callId) {
    }

    public void onParticipantJoinNotify(AliParticipantInfo[] participantList, int feedCount) {
    }

    public void onParticipantLeaveNotify(AliParticipantInfo[] participantList, int feedCount) {
    }

    public void onParticipantPublishNotify(PublisherInfo[] publisherList, int publisherCount) {
    }

    public void onParticipantUnpublishNotify(AliUnPublisherInfo[] unpublisherList, int feedCount) {
    }

    public void onParticipantSubscribeNotify(AliSubscriberInfo[] subcribeinfoList, int feedCount) {
    }

    public void onParticipantUnsubscribeNotify(AliParticipantInfo[] participantList, int feedCount) {
    }

    public void onParticipantStatusNotify(AliStatusInfo[] status_info_list, int count) {
    }

    public void onAliRtcStats(ALI_RTC_INTERFACE.AliRtcStats aliRtcStats) {
    }

    public void onSubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
    }

    public void onResubscribeResult2(int result, String callID, ALI_RTC_INTERFACE.AliSubscribeConfig reqConfig, ALI_RTC_INTERFACE.AliSubscribeConfig curConfig) {
    }

    public void onChannelReleaseNotify() {
    }

    public void onConnectionChange(int mediaConState) {
    }

    public void onCollectStats(String callId, HashMap collectStatus) {
    }

    public void onWarning(int warningEvent, String params) {
    }

    public void onError(int event, String params) {
    }

    public void onTransportStatusChange(String callId, ALI_RTC_INTERFACE.TransportType event, ALI_RTC_INTERFACE.TransportStatus status) {
    }

    public void onNetworkQualityChange(ArrayList<ALI_RTC_INTERFACE.AliTransportInfo> arrayList) {
    }

    public void onLogMessage(String message) {
    }

    public void onMessage(String tid, String contentType, String content) {
    }

    public void onBye(int code) {
    }

    public void onUplinkChannelMessage(int result, String contentType, String content) {
    }

    public String onCollectPlatformProfile() {
        return null;
    }

    public String onFetchPerformanceInfo() {
        return null;
    }

    public boolean onFetchAudioPermissionInfo() {
        return true;
    }

    public String onFetchAudioDeviceInfo() {
        return null;
    }

    public void onWindowRenderReady(String callId, int videoType) {
    }

    public void onUpdateRoleNotify(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role old_role, ALI_RTC_INTERFACE.AliRTCSDK_Client_Role new_role) {
    }

    public void onFirstFramereceived(String callId, String stream_label, String track_label, int time_cost_ms) {
    }

    public void onFirstPacketSent(String callId, String stream_label, String track_label, int time_cost_ms) {
    }

    public void onFirstPacketReceived(String callId, String stream_label, String track_label, int time_cost_ms) {
    }

    public int onFetchDeviceOrientation() {
        return 0;
    }
}
