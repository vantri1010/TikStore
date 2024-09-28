package org.webrtc.ali;

import org.webrtc.ali.EncodedImage;

public interface VideoEncoder {

    public interface Callback {
        void onEncodedFrame(EncodedImage encodedImage, CodecSpecificInfo codecSpecificInfo);
    }

    public static class CodecSpecificInfo {
    }

    public static class CodecSpecificInfoH264 extends CodecSpecificInfo {
    }

    public static class CodecSpecificInfoVP8 extends CodecSpecificInfo {
    }

    public static class CodecSpecificInfoVP9 extends CodecSpecificInfo {
    }

    VideoCodecStatus encode(VideoFrame videoFrame, EncodeInfo encodeInfo);

    String getImplementationName();

    ScalingSettings getScalingSettings();

    VideoCodecStatus initEncode(Settings settings, Callback callback);

    VideoCodecStatus release();

    VideoCodecStatus setChannelParameters(short s, long j);

    VideoCodecStatus setRateAllocation(BitrateAllocation bitrateAllocation, int i);

    public static class Settings {
        public final int height;
        public final int maxFramerate;
        public final int numberOfCores;
        public final int startBitrate;
        public final int width;

        public Settings(int numberOfCores2, int width2, int height2, int startBitrate2, int maxFramerate2) {
            this.numberOfCores = numberOfCores2;
            this.width = width2;
            this.height = height2;
            this.startBitrate = startBitrate2;
            this.maxFramerate = maxFramerate2;
        }
    }

    public static class EncodeInfo {
        public final EncodedImage.FrameType[] frameTypes;

        public EncodeInfo(EncodedImage.FrameType[] frameTypes2) {
            this.frameTypes = frameTypes2;
        }
    }

    public static class BitrateAllocation {
        public final int[][] bitratesBbs;

        public BitrateAllocation(int[][] bitratesBbs2) {
            this.bitratesBbs = bitratesBbs2;
        }

        public int getSum() {
            int sum = 0;
            for (int[] spatialLayer : this.bitratesBbs) {
                for (int bitrate : r1[r4]) {
                    sum += bitrate;
                }
            }
            return sum;
        }
    }

    public static class ScalingSettings {
        public final int high;
        public final int low;
        public final boolean on;

        public ScalingSettings(boolean on2, int low2, int high2) {
            this.on = on2;
            this.low = low2;
            this.high = high2;
        }
    }
}
