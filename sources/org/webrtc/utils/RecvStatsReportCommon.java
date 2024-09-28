package org.webrtc.utils;

import android.os.Build;
import java.util.UUID;

public class RecvStatsReportCommon {
    public static final String OS_VERSION = Build.VERSION.RELEASE;
    public static final String cpu_band = Build.HARDWARE;
    public static final String model = Build.MODEL;
    public static final String model_phone_brand = Build.BRAND;
    public static final String sdk_platform = "Android";
    public static final String uuid = UUID.randomUUID().toString();
}
