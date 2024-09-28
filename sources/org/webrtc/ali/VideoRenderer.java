package org.webrtc.ali;

import com.king.zxing.util.LogUtils;
import java.nio.ByteBuffer;
import org.webrtc.ali.VideoFrame;

public class VideoRenderer {
    long nativeVideoRenderer;

    public interface Callbacks {
        void renderFrame(I420Frame i420Frame);
    }

    private static native void freeWrappedVideoRenderer(long j);

    public static native void nativeCopyPlane(ByteBuffer byteBuffer, int i, int i2, int i3, ByteBuffer byteBuffer2, int i4);

    private static native long nativeWrapVideoRenderer(Callbacks callbacks);

    private static native void releaseNativeFrame(long j);

    public static class I420Frame {
        public final int height;
        /* access modifiers changed from: private */
        public long nativeFramePointer;
        public int rotationDegree;
        public final float[] samplingMatrix;
        public int textureId;
        public final int width;
        public final boolean yuvFrame;
        public ByteBuffer[] yuvPlanes;
        public final int[] yuvStrides;

        public I420Frame(int width2, int height2, int rotationDegree2, int[] yuvStrides2, ByteBuffer[] yuvPlanes2, long nativeFramePointer2) {
            this.width = width2;
            this.height = height2;
            this.yuvStrides = yuvStrides2;
            this.yuvPlanes = yuvPlanes2;
            this.yuvFrame = true;
            this.rotationDegree = rotationDegree2;
            this.nativeFramePointer = nativeFramePointer2;
            if (rotationDegree2 % 90 == 0) {
                this.samplingMatrix = RendererCommon.verticalFlipMatrix();
                return;
            }
            throw new IllegalArgumentException("Rotation degree not multiple of 90: " + rotationDegree2);
        }

        public I420Frame(int width2, int height2, int rotationDegree2, int textureId2, float[] samplingMatrix2, long nativeFramePointer2) {
            this.width = width2;
            this.height = height2;
            this.yuvStrides = null;
            this.yuvPlanes = null;
            this.samplingMatrix = samplingMatrix2;
            this.textureId = textureId2;
            this.yuvFrame = false;
            this.rotationDegree = rotationDegree2;
            this.nativeFramePointer = nativeFramePointer2;
            if (rotationDegree2 % 90 != 0) {
                throw new IllegalArgumentException("Rotation degree not multiple of 90: " + rotationDegree2);
            }
        }

        public I420Frame(int width2, int height2, int rotationDegree2, float[] samplingMatrix2, VideoFrame.Buffer buffer, long nativeFramePointer2) {
            this.width = width2;
            this.height = height2;
            this.rotationDegree = rotationDegree2;
            if (rotationDegree2 % 90 == 0) {
                if (buffer instanceof VideoFrame.TextureBuffer) {
                    this.yuvFrame = false;
                    this.textureId = ((VideoFrame.TextureBuffer) buffer).getTextureId();
                    this.samplingMatrix = samplingMatrix2;
                    this.yuvStrides = null;
                    this.yuvPlanes = null;
                } else {
                    VideoFrame.I420Buffer i420Buffer = buffer.toI420();
                    this.yuvFrame = true;
                    this.yuvStrides = new int[]{i420Buffer.getStrideY(), i420Buffer.getStrideU(), i420Buffer.getStrideV()};
                    this.yuvPlanes = new ByteBuffer[]{i420Buffer.getDataY(), i420Buffer.getDataU(), i420Buffer.getDataV()};
                    this.samplingMatrix = RendererCommon.multiplyMatrices(samplingMatrix2, RendererCommon.verticalFlipMatrix());
                    this.textureId = 0;
                }
                this.nativeFramePointer = nativeFramePointer2;
                return;
            }
            throw new IllegalArgumentException("Rotation degree not multiple of 90: " + rotationDegree2);
        }

        public int rotatedWidth() {
            return this.rotationDegree % 180 == 0 ? this.width : this.height;
        }

        public int rotatedHeight() {
            return this.rotationDegree % 180 == 0 ? this.height : this.width;
        }

        public String toString() {
            return this.width + "x" + this.height + LogUtils.COLON + this.yuvStrides[0] + LogUtils.COLON + this.yuvStrides[1] + LogUtils.COLON + this.yuvStrides[2];
        }
    }

    public static void renderFrameDone(I420Frame frame) {
        frame.yuvPlanes = null;
        frame.textureId = 0;
        if (frame.nativeFramePointer != 0) {
            releaseNativeFrame(frame.nativeFramePointer);
            long unused = frame.nativeFramePointer = 0;
        }
    }

    public VideoRenderer(Callbacks callbacks) {
        this.nativeVideoRenderer = nativeWrapVideoRenderer(callbacks);
    }

    public void dispose() {
        long j = this.nativeVideoRenderer;
        if (j != 0) {
            freeWrappedVideoRenderer(j);
            this.nativeVideoRenderer = 0;
        }
    }
}
