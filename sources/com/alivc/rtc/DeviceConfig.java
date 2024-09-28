package com.alivc.rtc;

import android.os.Build;

public class DeviceConfig {
    private static final String DINGTALK_BOX_BRAND = "Android";
    private static final String DINGTALK_BOX_MODEL = "SDM710 for arm64";
    private static boolean isSupportSuperStream = false;

    public static void setSupportSuperStream(boolean support) {
        isSupportSuperStream = support;
    }

    public static boolean isSupportSuperStream() {
        return isSupportSuperStream;
    }

    public static boolean isDingTalkBox() {
        if (!"Android".equalsIgnoreCase(getPhoneBrand()) || !DINGTALK_BOX_MODEL.equalsIgnoreCase(getPhoneModel())) {
            return false;
        }
        return true;
    }

    public static void initConfig() {
        setSupportSuperStream(isDingTalkBox());
    }

    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    public static String getPhoneModel() {
        return Build.MODEL;
    }
}
