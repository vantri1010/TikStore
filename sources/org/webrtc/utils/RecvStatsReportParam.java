package org.webrtc.utils;

import java.util.HashMap;
import java.util.Map;

public class RecvStatsReportParam {
    public static Map<String, String> generatePublicParamters(HashMap<String, String> map, String access, String cpuUsage, String memory, String sdkVerison) {
        map.put(RecvStatsLogKey.KEY_SDK_PLATFORM, RecvStatsReportCommon.sdk_platform);
        map.put(RecvStatsLogKey.KEY_SDK_VERSION, sdkVerison);
        map.put(RecvStatsLogKey.KEY_ACCESS, access);
        map.put(RecvStatsLogKey.KEY_OS, RecvStatsReportCommon.OS_VERSION);
        map.put("uuid", RecvStatsReportCommon.uuid);
        map.put(RecvStatsLogKey.KEY_CPU_BAND, RecvStatsReportCommon.cpu_band);
        map.put(RecvStatsLogKey.KEY_CPU_USAGE, cpuUsage);
        map.put(RecvStatsLogKey.KEY_MOBILE_PHONE_BAND, RecvStatsReportCommon.model_phone_brand);
        map.put(RecvStatsLogKey.KEY_MOBILE_PHONE_TYPE, RecvStatsReportCommon.model);
        map.put(RecvStatsLogKey.KEY_MEMORY, memory);
        return map;
    }
}
