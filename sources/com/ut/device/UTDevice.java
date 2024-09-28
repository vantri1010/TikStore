package com.ut.device;

import android.content.Context;

public class UTDevice {
    public static String getUtdid(Context context) {
        return com.ta.utdid2.device.UTDevice.getUtdid(context);
    }

    @Deprecated
    public static String getUtdidForUpdate(Context context) {
        return com.ta.utdid2.device.UTDevice.getUtdidForUpdate(context);
    }

    @Deprecated
    public static String getAid(String appName, String token, Context context) {
        return "";
    }

    @Deprecated
    public static void getAidAsync(String appName, String token, Context context, AidCallback callback) {
    }
}
