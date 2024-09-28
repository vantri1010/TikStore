package com.alivc.rtc;

import com.alivc.rtc.AliRtcEngine;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;
import org.webrtc.alirtcInterface.AliParticipantInfo;
import org.webrtc.alirtcInterface.AliStatusInfo;
import org.webrtc.alirtcInterface.AliSubscriberInfo;

public interface AliRtcEngineNotify {
    void onAliRtcStats(ALI_RTC_INTERFACE.AliRtcStats aliRtcStats);

    void onBye(int i);

    void onFirstFramereceived(String str, String str2, String str3, int i);

    void onFirstPacketReceived(String str, String str2, String str3, int i);

    void onFirstPacketSent(String str, String str2, String str3, int i);

    void onParticipantStatusNotify(AliStatusInfo[] aliStatusInfoArr, int i);

    void onParticipantSubscribeNotify(AliSubscriberInfo[] aliSubscriberInfoArr, int i);

    void onParticipantUnsubscribeNotify(AliParticipantInfo[] aliParticipantInfoArr, int i);

    void onRemoteTrackAvailableNotify(String str, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack);

    void onRemoteUserOffLineNotify(String str);

    void onRemoteUserOnLineNotify(String str);

    void onRemoteUserUnPublish(AliRtcEngine aliRtcEngine, String str);

    void onSubscribeChangedNotify(String str, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack);
}
