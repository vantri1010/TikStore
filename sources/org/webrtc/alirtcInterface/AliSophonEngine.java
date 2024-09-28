package org.webrtc.alirtcInterface;

import java.util.ArrayList;
import java.util.HashMap;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;

public interface AliSophonEngine {
    void onAliRtcStats(ALI_RTC_INTERFACE.AliRtcStats aliRtcStats);

    void onBye(int i);

    String onCollectPlatformProfile();

    void onCollectStatus(String str, HashMap hashMap);

    void onConnectionChange(int i);

    void onError(int i, String str);

    String onFetchAudioDeviceInfo();

    boolean onFetchAudioPermissionInfo();

    int onFetchDeviceOrientation();

    String onFetchPerformanceInfo();

    void onFirstFrameReceived(String str, String str2, String str3, int i);

    void onFirstPacketReceived(String str, String str2, String str3, int i);

    void onFirstPacketSent(String str, String str2, String str3, int i);

    void onGslbResult(int i);

    void onJoinChannelResult(int i);

    void onLeaveChannelResult(int i);

    void onLogMessage(String str);

    void onMessage(String str, String str2, String str3);

    void onNetworkQualityChange(ArrayList<ALI_RTC_INTERFACE.AliTransportInfo> arrayList);

    void onParticipantJoinNotify(AliParticipantInfo[] aliParticipantInfoArr, int i);

    void onParticipantLeaveNotify(AliParticipantInfo[] aliParticipantInfoArr, int i);

    void onParticipantPublishNotify(PublisherInfo[] publisherInfoArr, int i);

    void onParticipantStatusNotify(AliStatusInfo[] aliStatusInfoArr, int i);

    void onParticipantSubscribeNotify(AliSubscriberInfo[] aliSubscriberInfoArr, int i);

    void onParticipantUnpublishNotify(AliUnPublisherInfo[] aliUnPublisherInfoArr, int i);

    void onParticipantUnsubscribeNotify(AliParticipantInfo[] aliParticipantInfoArr, int i);

    void onPublishResult(int i, String str);

    void onRecvStatsReport(HashMap hashMap);

    void onRepublishResult(int i, String str);

    void onResubscribeResult(int i, String str);

    void onResubscribeResult2(int i, String str, ALI_RTC_INTERFACE.AliSubscribeConfig aliSubscribeConfig, ALI_RTC_INTERFACE.AliSubscribeConfig aliSubscribeConfig2);

    void onSubscribeResult(int i, String str);

    void onSubscribeResult2(int i, String str, ALI_RTC_INTERFACE.AliSubscribeConfig aliSubscribeConfig, ALI_RTC_INTERFACE.AliSubscribeConfig aliSubscribeConfig2);

    void onTransportStatusChange(String str, ALI_RTC_INTERFACE.TransportType transportType, ALI_RTC_INTERFACE.TransportStatus transportStatus);

    void onUnpublishResult(int i, String str);

    void onUnsubscribeResult(int i, String str);

    void onUpdateRoleNotify(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_Client_Role, ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_Client_Role2);

    void onUplinkChannelMessage(int i, String str, String str2);

    void onWarning(int i, String str);

    void onWindowRenderReady(String str, int i);

    void release();
}
