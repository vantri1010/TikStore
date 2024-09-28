package org.webrtc.ali;

import com.google.android.exoplayer2.util.MimeTypes;

enum VideoCodecType {
    VP8(MimeTypes.VIDEO_VP8),
    VP9(MimeTypes.VIDEO_VP9),
    H264("video/avc");
    
    private final String mimeType;

    private VideoCodecType(String mimeType2) {
        this.mimeType = mimeType2;
    }

    /* access modifiers changed from: package-private */
    public String mimeType() {
        return this.mimeType;
    }
}
