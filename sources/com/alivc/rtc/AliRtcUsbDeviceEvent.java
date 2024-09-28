package com.alivc.rtc;

public interface AliRtcUsbDeviceEvent {
    void onUSBDeviceCancel();

    void onUSBDeviceConnect(int i);

    void onUSBDeviceDisconnect();
}
