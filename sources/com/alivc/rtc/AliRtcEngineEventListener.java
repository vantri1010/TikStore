package com.alivc.rtc;

import com.alivc.rtc.AliRtcEngine;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;

public interface AliRtcEngineEventListener {
    void onConnectionLost();

    void onConnectionRecovery();

    void onJoinChannelResult(int i);

    void onLeaveChannelResult(int i);

    void onNetworkQualityChanged(String str, AliRtcEngine.AliRtcNetworkQuality aliRtcNetworkQuality, AliRtcEngine.AliRtcNetworkQuality aliRtcNetworkQuality2);

    void onOccurError(int i);

    void onOccurWarning(int i);

    void onPerformanceLow();

    void onPermormanceRecovery();

    void onPublishResult(int i, String str);

    void onSubscribeResult(String str, int i, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack);

    void onTryToReconnect();

    void onUnpublishResult(int i);

    void onUnsubscribeResult(int i, String str);

    void onUpdateRoleNotify(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_Client_Role, ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_Client_Role2);
}
