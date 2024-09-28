package com.alivc.rtc.device;

import android.content.Context;
import android.text.TextUtils;

public class UTDevice {
    public static String getUtdid(Context context) {
        Device device = DeviceInfo.getDevice(context);
        return (device == null || TextUtils.isEmpty(device.getUtdid())) ? "ffffffffffffffffffffffff" : device.getUtdid();
    }
}
