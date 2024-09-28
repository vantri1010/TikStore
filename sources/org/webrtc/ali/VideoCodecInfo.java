package org.webrtc.ali;

import java.util.Map;

public class VideoCodecInfo {
    public final String name;
    public final Map<String, String> params;
    public final int payload;

    public VideoCodecInfo(int payload2, String name2, Map<String, String> params2) {
        this.payload = payload2;
        this.name = name2;
        this.params = params2;
    }
}
