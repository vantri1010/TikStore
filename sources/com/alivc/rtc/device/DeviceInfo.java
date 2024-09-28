package com.alivc.rtc.device;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.alivc.rtc.device.utils.PhoneInfoUtils;
import java.util.zip.Adler32;

public class DeviceInfo {
    static final Object CREATE_DEVICE_METADATA_LOCK = new Object();
    private static Device mDevice = null;

    static long getMetadataCheckSum(Device device) {
        if (device == null) {
            return 0;
        }
        String checkSumContent = String.format("%s%s%s%s%s", new Object[]{device.getUtdid(), device.getDeviceId(), Long.valueOf(device.getCreateTimestamp()), device.getImsi(), device.getImei()});
        if (TextUtils.isEmpty(checkSumContent)) {
            return 0;
        }
        Adler32 adler32 = new Adler32();
        adler32.reset();
        adler32.update(checkSumContent.getBytes());
        return adler32.getValue();
    }

    private static Device initDeviceMetadata(Context aContext) {
        if (aContext == null) {
            return null;
        }
        synchronized (CREATE_DEVICE_METADATA_LOCK) {
            Log.e("ydsyds", "getDevice，mDevice == null,initDeviceMetadata");
            String utdid = UTUtdid.instance(aContext).getValue();
            if (TextUtils.isEmpty(utdid)) {
                return null;
            }
            if (utdid.endsWith("\n")) {
                utdid = utdid.substring(0, utdid.length() - 1);
            }
            Device device = new Device();
            long timestamp = System.currentTimeMillis();
            String imei = PhoneInfoUtils.getImei(aContext);
            String imsi = PhoneInfoUtils.getImsi(aContext);
            device.setDeviceId(imei);
            device.setImei(imei);
            device.setCreateTimestamp(timestamp);
            device.setImsi(imsi);
            device.setUtdid(utdid);
            device.setCheckSum(getMetadataCheckSum(device));
            return device;
        }
    }

    public static synchronized Device getDevice(Context context) {
        synchronized (DeviceInfo.class) {
            if (mDevice != null) {
                Log.e("ydsyds", "getDevice，mDevice= null");
                Device device = mDevice;
                return device;
            } else if (context == null) {
                return null;
            } else {
                Log.e("ydsyds", "getDevice，mDevice == null,initDeviceMetadata");
                Device device2 = initDeviceMetadata(context);
                mDevice = device2;
                return device2;
            }
        }
    }
}
