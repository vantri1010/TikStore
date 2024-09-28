package org.webrtc.ali;

import android.graphics.Matrix;
import java.nio.ByteBuffer;

public class VideoFrame {
    private final Buffer buffer;
    private final int rotation;
    private final long timestampNs;
    private final Matrix transformMatrix;

    public interface Buffer {
        int getHeight();

        int getWidth();

        void release();

        void retain();

        I420Buffer toI420();
    }

    public interface I420Buffer extends Buffer {
        ByteBuffer getDataU();

        ByteBuffer getDataV();

        ByteBuffer getDataY();

        int getStrideU();

        int getStrideV();

        int getStrideY();
    }

    public interface TextureBuffer extends Buffer {

        public enum Type {
            OES,
            RGB
        }

        int getTextureId();

        Type getType();
    }

    public VideoFrame(Buffer buffer2, int rotation2, long timestampNs2, Matrix transformMatrix2) {
        if (buffer2 == null) {
            throw new IllegalArgumentException("buffer not allowed to be null");
        } else if (transformMatrix2 != null) {
            this.buffer = buffer2;
            this.rotation = rotation2;
            this.timestampNs = timestampNs2;
            this.transformMatrix = transformMatrix2;
        } else {
            throw new IllegalArgumentException("transformMatrix not allowed to be null");
        }
    }

    public Buffer getBuffer() {
        return this.buffer;
    }

    public int getRotation() {
        return this.rotation;
    }

    public long getTimestampNs() {
        return this.timestampNs;
    }

    public Matrix getTransformMatrix() {
        return this.transformMatrix;
    }

    public int getWidth() {
        return this.buffer.getWidth();
    }

    public int getHeight() {
        return this.buffer.getHeight();
    }

    public void retain() {
        this.buffer.retain();
    }

    public void release() {
        this.buffer.release();
    }
}
