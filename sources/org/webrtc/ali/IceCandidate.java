package org.webrtc.ali;

import com.king.zxing.util.LogUtils;

public class IceCandidate {
    public final String sdp;
    public final int sdpMLineIndex;
    public final String sdpMid;
    public final String serverUrl;

    public IceCandidate(String sdpMid2, int sdpMLineIndex2, String sdp2) {
        this.sdpMid = sdpMid2;
        this.sdpMLineIndex = sdpMLineIndex2;
        this.sdp = sdp2;
        this.serverUrl = "";
    }

    private IceCandidate(String sdpMid2, int sdpMLineIndex2, String sdp2, String serverUrl2) {
        this.sdpMid = sdpMid2;
        this.sdpMLineIndex = sdpMLineIndex2;
        this.sdp = sdp2;
        this.serverUrl = serverUrl2;
    }

    public String toString() {
        return this.sdpMid + LogUtils.COLON + this.sdpMLineIndex + LogUtils.COLON + this.sdp + LogUtils.COLON + this.serverUrl;
    }
}
