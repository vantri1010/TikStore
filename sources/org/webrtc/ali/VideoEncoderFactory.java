package org.webrtc.ali;

public interface VideoEncoderFactory {
    VideoEncoder createEncoder(VideoCodecInfo videoCodecInfo);

    VideoCodecInfo[] getSupportedCodecs();
}
