package org.webrtc.ali;

import java.nio.ByteBuffer;
import org.webrtc.ali.VideoFrame;

class I420BufferImpl implements VideoFrame.I420Buffer {
    private final int height;
    private final int strideUV;
    private final ByteBuffer u;
    private final ByteBuffer v;
    private final int width;
    private final ByteBuffer y;

    I420BufferImpl(int width2, int height2) {
        this.width = width2;
        this.height = height2;
        this.strideUV = (width2 + 1) / 2;
        int halfHeight = (height2 + 1) / 2;
        this.y = ByteBuffer.allocateDirect(width2 * height2);
        this.u = ByteBuffer.allocateDirect(this.strideUV * halfHeight);
        this.v = ByteBuffer.allocateDirect(this.strideUV * halfHeight);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ByteBuffer getDataY() {
        return this.y;
    }

    public ByteBuffer getDataU() {
        return this.u;
    }

    public ByteBuffer getDataV() {
        return this.v;
    }

    public int getStrideY() {
        return this.width;
    }

    public int getStrideU() {
        return this.strideUV;
    }

    public int getStrideV() {
        return this.strideUV;
    }

    public VideoFrame.I420Buffer toI420() {
        return this;
    }

    public void retain() {
    }

    public void release() {
    }
}
