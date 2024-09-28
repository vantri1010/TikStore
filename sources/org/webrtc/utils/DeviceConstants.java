package org.webrtc.utils;

import android.os.Build;

public class DeviceConstants {
    private static final String BRAND_HUAWEI = "HUAWEI";
    private static final String BRAND_LE = "LeEco";
    private static final String BRAND_LENOVO = "lenovo";
    private static final String BRAND_MEIZU = "Meizu";
    private static final String BRAND_OPPO = "OPPO";
    private static final String BRAND_VIVO = "vivo";
    private static final String BRAND_XIAOMI = "xiaomi";
    private static final String BRAND_ZUK = "zuk";
    private static final String MODEL_LE = "Le";
    private static final String MODEL_OPPO_R17 = "PBDM00";
    private static final String MODEL_VIVO_NEX = "V1821A";
    private static final String MODEL_VIVO_X23 = "V1809A";
    private static final String MODEL_VIVO_Z3 = "V1813BA";
    private static final String MODEL_Z1 = "V1730EA";

    public static boolean shouldSetMode() {
        if (!BRAND_VIVO.equals(Build.BRAND) || !MODEL_VIVO_NEX.equals(Build.MODEL)) {
            return false;
        }
        return true;
    }

    public static boolean shouldListenerPhoneState() {
        if (BRAND_HUAWEI.equals(Build.BRAND) || "OPPO".equals(Build.BRAND)) {
            return true;
        }
        return false;
    }
}
