package org.webrtc.ali;

import java.nio.ByteBuffer;

public class EncodedImage {
    public final ByteBuffer buffer;
    public final long captureTimeMs;
    public final boolean completeFrame;
    public final int encodedHeight;
    public final int encodedWidth;
    public final FrameType frameType;
    public final Integer qp;
    public final int rotation;

    public enum FrameType {
        EmptyFrame,
        VideoFrameKey,
        VideoFrameDelta
    }

    private EncodedImage(ByteBuffer buffer2, int encodedWidth2, int encodedHeight2, long captureTimeMs2, FrameType frameType2, int rotation2, boolean completeFrame2, Integer qp2) {
        this.buffer = buffer2;
        this.encodedWidth = encodedWidth2;
        this.encodedHeight = encodedHeight2;
        this.captureTimeMs = captureTimeMs2;
        this.frameType = frameType2;
        this.rotation = rotation2;
        this.completeFrame = completeFrame2;
        this.qp = qp2;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ByteBuffer buffer;
        private long captureTimeMs;
        private boolean completeFrame;
        private int encodedHeight;
        private int encodedWidth;
        private FrameType frameType;
        private Integer qp;
        private int rotation;

        private Builder() {
        }

        public Builder setBuffer(ByteBuffer buffer2) {
            this.buffer = buffer2;
            return this;
        }

        public Builder setEncodedWidth(int encodedWidth2) {
            this.encodedWidth = encodedWidth2;
            return this;
        }

        public Builder setEncodedHeight(int encodedHeight2) {
            this.encodedHeight = encodedHeight2;
            return this;
        }

        public Builder setCaptureTimeMs(long captureTimeMs2) {
            this.captureTimeMs = captureTimeMs2;
            return this;
        }

        public Builder setFrameType(FrameType frameType2) {
            this.frameType = frameType2;
            return this;
        }

        public Builder setRotation(int rotation2) {
            this.rotation = rotation2;
            return this;
        }

        public Builder setCompleteFrame(boolean completeFrame2) {
            this.completeFrame = completeFrame2;
            return this;
        }

        public Builder setQp(Integer qp2) {
            this.qp = qp2;
            return this;
        }

        public EncodedImage createEncodedImage() {
            return new EncodedImage(this.buffer, this.encodedWidth, this.encodedHeight, this.captureTimeMs, this.frameType, this.rotation, this.completeFrame, this.qp);
        }
    }
}
