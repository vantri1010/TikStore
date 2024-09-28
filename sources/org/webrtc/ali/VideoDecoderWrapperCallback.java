package org.webrtc.ali;

import org.webrtc.ali.VideoDecoder;

class VideoDecoderWrapperCallback implements VideoDecoder.Callback {
    private final long nativeDecoder;

    private static native void nativeOnDecodedFrame(long j, VideoFrame videoFrame, Integer num, Integer num2);

    public VideoDecoderWrapperCallback(long nativeDecoder2) {
        this.nativeDecoder = nativeDecoder2;
    }

    public void onDecodedFrame(VideoFrame frame, Integer decodeTimeMs, Integer qp) {
        nativeOnDecodedFrame(this.nativeDecoder, frame, decodeTimeMs, qp);
    }
}
