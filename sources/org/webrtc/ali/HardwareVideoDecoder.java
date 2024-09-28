package org.webrtc.ali;

import android.graphics.Matrix;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.SystemClock;
import android.view.Surface;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import org.webrtc.ali.EncodedImage;
import org.webrtc.ali.ThreadUtils;
import org.webrtc.ali.VideoDecoder;
import org.webrtc.ali.VideoFrame;

class HardwareVideoDecoder implements VideoDecoder {
    private static final int DEQUEUE_INPUT_TIMEOUT_US = 500000;
    private static final int DEQUEUE_OUTPUT_BUFFER_TIMEOUT_US = 100000;
    private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
    private static final String MEDIA_FORMAT_KEY_CROP_BOTTOM = "crop-bottom";
    private static final String MEDIA_FORMAT_KEY_CROP_LEFT = "crop-left";
    private static final String MEDIA_FORMAT_KEY_CROP_RIGHT = "crop-right";
    private static final String MEDIA_FORMAT_KEY_CROP_TOP = "crop-top";
    private static final String MEDIA_FORMAT_KEY_SLICE_HEIGHT = "slice-height";
    private static final String MEDIA_FORMAT_KEY_STRIDE = "stride";
    private static final String TAG = "HardwareVideoDecoder";
    private int activeOutputBuffers = 0;
    /* access modifiers changed from: private */
    public final Object activeOutputBuffersLock = new Object();
    private VideoDecoder.Callback callback;
    /* access modifiers changed from: private */
    public MediaCodec codec = null;
    private final String codecName;
    private final VideoCodecType codecType;
    private int colorFormat;
    private ThreadUtils.ThreadChecker decoderThreadChecker;
    private final Object dimensionLock = new Object();
    private final Deque<FrameInfo> frameInfos;
    private boolean hasDecodedFirstFrame;
    private int height;
    private boolean keyFrameRequired;
    private Thread outputThread;
    /* access modifiers changed from: private */
    public ThreadUtils.ThreadChecker outputThreadChecker;
    /* access modifiers changed from: private */
    public volatile boolean running = false;
    private volatile Exception shutdownException = null;
    private int sliceHeight;
    private int stride;
    private int width;

    static /* synthetic */ int access$610(HardwareVideoDecoder x0) {
        int i = x0.activeOutputBuffers;
        x0.activeOutputBuffers = i - 1;
        return i;
    }

    private static class FrameInfo {
        final long decodeStartTimeMs;
        final int rotation;

        FrameInfo(long decodeStartTimeMs2, int rotation2) {
            this.decodeStartTimeMs = decodeStartTimeMs2;
            this.rotation = rotation2;
        }
    }

    HardwareVideoDecoder(String codecName2, VideoCodecType codecType2, int colorFormat2) {
        if (isSupportedColorFormat(colorFormat2)) {
            this.codecName = codecName2;
            this.codecType = codecType2;
            this.colorFormat = colorFormat2;
            this.frameInfos = new LinkedBlockingDeque();
            return;
        }
        throw new IllegalArgumentException("Unsupported color format: " + colorFormat2);
    }

    public VideoCodecStatus initDecode(VideoDecoder.Settings settings, VideoDecoder.Callback callback2) {
        this.decoderThreadChecker = new ThreadUtils.ThreadChecker();
        return initDecodeInternal(settings.width, settings.height, callback2);
    }

    private VideoCodecStatus initDecodeInternal(int width2, int height2, VideoDecoder.Callback callback2) {
        this.decoderThreadChecker.checkIsOnValidThread();
        if (this.outputThread != null) {
            Logging.e(TAG, "initDecodeInternal called while the codec is already running");
            return VideoCodecStatus.ERROR;
        }
        this.callback = callback2;
        this.width = width2;
        this.height = height2;
        this.stride = width2;
        this.sliceHeight = height2;
        this.hasDecodedFirstFrame = false;
        this.keyFrameRequired = true;
        try {
            this.codec = MediaCodec.createByCodecName(this.codecName);
            try {
                MediaFormat format = MediaFormat.createVideoFormat(this.codecType.mimeType(), width2, height2);
                format.setInteger("color-format", this.colorFormat);
                this.codec.configure(format, (Surface) null, (MediaCrypto) null, 0);
                this.codec.start();
                this.running = true;
                Thread createOutputThread = createOutputThread();
                this.outputThread = createOutputThread;
                createOutputThread.start();
                return VideoCodecStatus.OK;
            } catch (IllegalStateException e) {
                Logging.e(TAG, "initDecode failed", e);
                release();
                return VideoCodecStatus.ERROR;
            }
        } catch (IOException | IllegalArgumentException e2) {
            Logging.e(TAG, "Cannot create media decoder " + this.codecName);
            return VideoCodecStatus.ERROR;
        }
    }

    public VideoCodecStatus decode(EncodedImage frame, VideoDecoder.DecodeInfo info) {
        int width2;
        int height2;
        VideoCodecStatus status;
        this.decoderThreadChecker.checkIsOnValidThread();
        if (this.codec == null || this.callback == null) {
            return VideoCodecStatus.UNINITIALIZED;
        }
        if (frame.buffer == null) {
            Logging.e(TAG, "decode() - no input data");
            return VideoCodecStatus.ERR_PARAMETER;
        }
        int size = frame.buffer.remaining();
        if (size == 0) {
            Logging.e(TAG, "decode() - input buffer empty");
            return VideoCodecStatus.ERR_PARAMETER;
        }
        synchronized (this.dimensionLock) {
            width2 = this.width;
            height2 = this.height;
        }
        if (frame.encodedWidth * frame.encodedHeight > 0 && ((frame.encodedWidth != width2 || frame.encodedHeight != height2) && (status = reinitDecode(frame.encodedWidth, frame.encodedHeight)) != VideoCodecStatus.OK)) {
            return status;
        }
        if (this.keyFrameRequired) {
            if (frame.frameType != EncodedImage.FrameType.VideoFrameKey) {
                Logging.e(TAG, "decode() - key frame required first");
                return VideoCodecStatus.ERROR;
            } else if (!frame.completeFrame) {
                Logging.e(TAG, "decode() - complete frame required first");
                return VideoCodecStatus.ERROR;
            }
        }
        try {
            int index = this.codec.dequeueInputBuffer(500000);
            if (index < 0) {
                Logging.e(TAG, "decode() - no HW buffers available; decoder falling behind");
                return VideoCodecStatus.ERROR;
            }
            try {
                ByteBuffer buffer = this.codec.getInputBuffers()[index];
                if (buffer.capacity() < size) {
                    Logging.e(TAG, "decode() - HW buffer too small");
                    return VideoCodecStatus.ERROR;
                }
                buffer.put(frame.buffer);
                this.frameInfos.offer(new FrameInfo(SystemClock.elapsedRealtime(), frame.rotation));
                try {
                    this.codec.queueInputBuffer(index, 0, size, frame.captureTimeMs * 1000, 0);
                    if (this.keyFrameRequired) {
                        this.keyFrameRequired = false;
                    }
                    return VideoCodecStatus.OK;
                } catch (IllegalStateException e) {
                    Logging.e(TAG, "queueInputBuffer failed", e);
                    this.frameInfos.pollLast();
                    return VideoCodecStatus.ERROR;
                }
            } catch (IllegalStateException e2) {
                Logging.e(TAG, "getInputBuffers failed", e2);
                return VideoCodecStatus.ERROR;
            }
        } catch (IllegalStateException e3) {
            Logging.e(TAG, "dequeueInputBuffer failed", e3);
            return VideoCodecStatus.ERROR;
        }
    }

    public boolean getPrefersLateDecoding() {
        return true;
    }

    public String getImplementationName() {
        return "HardwareVideoDecoder: " + this.codecName;
    }

    public VideoCodecStatus release() {
        try {
            this.running = false;
            if (!ThreadUtils.joinUninterruptibly(this.outputThread, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS)) {
                Logging.e(TAG, "Media encoder release timeout", new RuntimeException());
                return VideoCodecStatus.TIMEOUT;
            } else if (this.shutdownException != null) {
                Logging.e(TAG, "Media encoder release error", new RuntimeException(this.shutdownException));
                this.shutdownException = null;
                VideoCodecStatus videoCodecStatus = VideoCodecStatus.ERROR;
                this.codec = null;
                this.callback = null;
                this.outputThread = null;
                this.frameInfos.clear();
                return videoCodecStatus;
            } else {
                this.codec = null;
                this.callback = null;
                this.outputThread = null;
                this.frameInfos.clear();
                return VideoCodecStatus.OK;
            }
        } finally {
            this.codec = null;
            this.callback = null;
            this.outputThread = null;
            this.frameInfos.clear();
        }
    }

    private VideoCodecStatus reinitDecode(int newWidth, int newHeight) {
        this.decoderThreadChecker.checkIsOnValidThread();
        VideoCodecStatus status = release();
        if (status != VideoCodecStatus.OK) {
            return status;
        }
        return initDecodeInternal(newWidth, newHeight, this.callback);
    }

    private Thread createOutputThread() {
        return new Thread("HardwareVideoDecoder.outputThread") {
            public void run() {
                ThreadUtils.ThreadChecker unused = HardwareVideoDecoder.this.outputThreadChecker = new ThreadUtils.ThreadChecker();
                while (HardwareVideoDecoder.this.running) {
                    HardwareVideoDecoder.this.deliverDecodedFrame();
                }
                HardwareVideoDecoder.this.releaseCodecOnOutputThread();
            }
        };
    }

    /* access modifiers changed from: private */
    public void deliverDecodedFrame() {
        int rotation;
        Integer decodeTimeMs;
        int width2;
        int height2;
        int stride2;
        int sliceHeight2;
        int stride3;
        VideoFrame.I420Buffer frameBuffer;
        this.outputThreadChecker.checkIsOnValidThread();
        try {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int result = this.codec.dequeueOutputBuffer(info, 100000);
            if (result == -2) {
                reformat(this.codec.getOutputFormat());
            } else if (result < 0) {
                Logging.v(TAG, "dequeueOutputBuffer returned " + result);
            } else {
                FrameInfo frameInfo = this.frameInfos.poll();
                if (frameInfo != null) {
                    decodeTimeMs = Integer.valueOf((int) (SystemClock.elapsedRealtime() - frameInfo.decodeStartTimeMs));
                    rotation = frameInfo.rotation;
                } else {
                    decodeTimeMs = null;
                    rotation = 0;
                }
                this.hasDecodedFirstFrame = true;
                synchronized (this.dimensionLock) {
                    width2 = this.width;
                    height2 = this.height;
                    stride2 = this.stride;
                    sliceHeight2 = this.sliceHeight;
                }
                if (info.size < ((width2 * height2) * 3) / 2) {
                    Logging.e(TAG, "Insufficient output buffer size: " + info.size);
                    return;
                }
                if (info.size >= ((stride2 * height2) * 3) / 2 || sliceHeight2 != height2 || stride2 <= width2) {
                    stride3 = stride2;
                } else {
                    stride3 = (info.size * 2) / (height2 * 3);
                }
                ByteBuffer buffer = this.codec.getOutputBuffers()[result];
                buffer.position(info.offset);
                buffer.limit(info.size);
                if (this.colorFormat != 19) {
                    VideoFrame.I420Buffer frameBuffer2 = new I420BufferImpl(width2, height2);
                    nv12ToI420(buffer, info.offset, frameBuffer2, stride3, sliceHeight2, width2, height2);
                    this.codec.releaseOutputBuffer(result, false);
                    frameBuffer = frameBuffer2;
                } else if (sliceHeight2 % 2 == 0) {
                    ByteBuffer byteBuffer = buffer;
                    int i = sliceHeight2;
                    frameBuffer = createBufferFromI420(buffer, result, info.offset, stride3, sliceHeight2, width2, height2);
                } else {
                    VideoFrame.I420Buffer frameBuffer3 = new I420BufferImpl(width2, height2);
                    copyI420(buffer, info.offset, frameBuffer3, stride3, sliceHeight2, width2, height2);
                    this.codec.releaseOutputBuffer(result, false);
                    frameBuffer = frameBuffer3;
                }
                int i2 = height2;
                VideoFrame frame = new VideoFrame(frameBuffer, rotation, info.presentationTimeUs * 1000, new Matrix());
                this.callback.onDecodedFrame(frame, decodeTimeMs, (Integer) null);
                frame.release();
            }
        } catch (IllegalStateException e) {
            Logging.e(TAG, "deliverDecodedFrame failed", e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00bb, code lost:
        if (r7.containsKey("color-format") == false) goto L_0x0105;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00bd, code lost:
        r6.colorFormat = r7.getInteger("color-format");
        org.webrtc.ali.Logging.d(TAG, "Color: 0x" + java.lang.Integer.toHexString(r6.colorFormat));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00e7, code lost:
        if (isSupportedColorFormat(r6.colorFormat) != false) goto L_0x0105;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00e9, code lost:
        stopOnOutputThread(new java.lang.IllegalStateException("Unsupported color format: " + r6.colorFormat));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0104, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0105, code lost:
        r3 = r6.dimensionLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0107, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x010e, code lost:
        if (r7.containsKey(MEDIA_FORMAT_KEY_STRIDE) == false) goto L_0x0118;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0110, code lost:
        r6.stride = r7.getInteger(MEDIA_FORMAT_KEY_STRIDE);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x011e, code lost:
        if (r7.containsKey(MEDIA_FORMAT_KEY_SLICE_HEIGHT) == false) goto L_0x0128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0120, code lost:
        r6.sliceHeight = r7.getInteger(MEDIA_FORMAT_KEY_SLICE_HEIGHT);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0128, code lost:
        org.webrtc.ali.Logging.d(TAG, "Frame stride and slice height: " + r6.stride + " x " + r6.sliceHeight);
        r6.stride = java.lang.Math.max(r6.width, r6.stride);
        r6.sliceHeight = java.lang.Math.max(r6.height, r6.sliceHeight);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x015e, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x015f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void reformat(android.media.MediaFormat r7) {
        /*
            r6 = this;
            org.webrtc.ali.ThreadUtils$ThreadChecker r0 = r6.outputThreadChecker
            r0.checkIsOnValidThread()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Decoder format changed: "
            r0.append(r1)
            java.lang.String r1 = r7.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "HardwareVideoDecoder"
            org.webrtc.ali.Logging.d(r1, r0)
            java.lang.String r0 = "crop-left"
            boolean r0 = r7.containsKey(r0)
            if (r0 == 0) goto L_0x005e
            java.lang.String r0 = "crop-right"
            boolean r0 = r7.containsKey(r0)
            if (r0 == 0) goto L_0x005e
            java.lang.String r0 = "crop-bottom"
            boolean r0 = r7.containsKey(r0)
            if (r0 == 0) goto L_0x005e
            java.lang.String r0 = "crop-top"
            boolean r0 = r7.containsKey(r0)
            if (r0 == 0) goto L_0x005e
            java.lang.String r0 = "crop-right"
            int r0 = r7.getInteger(r0)
            int r0 = r0 + 1
            java.lang.String r1 = "crop-left"
            int r1 = r7.getInteger(r1)
            int r0 = r0 - r1
            java.lang.String r1 = "crop-bottom"
            int r1 = r7.getInteger(r1)
            int r1 = r1 + 1
            java.lang.String r2 = "crop-top"
            int r2 = r7.getInteger(r2)
            int r1 = r1 - r2
            goto L_0x006a
        L_0x005e:
            java.lang.String r0 = "width"
            int r0 = r7.getInteger(r0)
            java.lang.String r1 = "height"
            int r1 = r7.getInteger(r1)
        L_0x006a:
            java.lang.Object r2 = r6.dimensionLock
            monitor-enter(r2)
            boolean r3 = r6.hasDecodedFirstFrame     // Catch:{ all -> 0x0163 }
            if (r3 == 0) goto L_0x00b0
            int r3 = r6.width     // Catch:{ all -> 0x0163 }
            if (r3 != r0) goto L_0x0079
            int r3 = r6.height     // Catch:{ all -> 0x0163 }
            if (r3 == r1) goto L_0x00b0
        L_0x0079:
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ all -> 0x0163 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0163 }
            r4.<init>()     // Catch:{ all -> 0x0163 }
            java.lang.String r5 = "Unexpected size change. Configured "
            r4.append(r5)     // Catch:{ all -> 0x0163 }
            int r5 = r6.width     // Catch:{ all -> 0x0163 }
            r4.append(r5)     // Catch:{ all -> 0x0163 }
            java.lang.String r5 = "*"
            r4.append(r5)     // Catch:{ all -> 0x0163 }
            int r5 = r6.height     // Catch:{ all -> 0x0163 }
            r4.append(r5)     // Catch:{ all -> 0x0163 }
            java.lang.String r5 = ". New "
            r4.append(r5)     // Catch:{ all -> 0x0163 }
            r4.append(r0)     // Catch:{ all -> 0x0163 }
            java.lang.String r5 = "*"
            r4.append(r5)     // Catch:{ all -> 0x0163 }
            r4.append(r1)     // Catch:{ all -> 0x0163 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0163 }
            r3.<init>(r4)     // Catch:{ all -> 0x0163 }
            r6.stopOnOutputThread(r3)     // Catch:{ all -> 0x0163 }
            monitor-exit(r2)     // Catch:{ all -> 0x0163 }
            return
        L_0x00b0:
            r6.width = r0     // Catch:{ all -> 0x0163 }
            r6.height = r1     // Catch:{ all -> 0x0163 }
            monitor-exit(r2)     // Catch:{ all -> 0x0163 }
            java.lang.String r2 = "color-format"
            boolean r2 = r7.containsKey(r2)
            if (r2 == 0) goto L_0x0105
            java.lang.String r2 = "color-format"
            int r2 = r7.getInteger(r2)
            r6.colorFormat = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Color: 0x"
            r2.append(r3)
            int r3 = r6.colorFormat
            java.lang.String r3 = java.lang.Integer.toHexString(r3)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "HardwareVideoDecoder"
            org.webrtc.ali.Logging.d(r3, r2)
            int r2 = r6.colorFormat
            boolean r2 = r6.isSupportedColorFormat(r2)
            if (r2 != 0) goto L_0x0105
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unsupported color format: "
            r3.append(r4)
            int r4 = r6.colorFormat
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            r6.stopOnOutputThread(r2)
            return
        L_0x0105:
            java.lang.Object r3 = r6.dimensionLock
            monitor-enter(r3)
            java.lang.String r2 = "stride"
            boolean r2 = r7.containsKey(r2)     // Catch:{ all -> 0x0160 }
            if (r2 == 0) goto L_0x0118
            java.lang.String r2 = "stride"
            int r2 = r7.getInteger(r2)     // Catch:{ all -> 0x0160 }
            r6.stride = r2     // Catch:{ all -> 0x0160 }
        L_0x0118:
            java.lang.String r2 = "slice-height"
            boolean r2 = r7.containsKey(r2)     // Catch:{ all -> 0x0160 }
            if (r2 == 0) goto L_0x0128
            java.lang.String r2 = "slice-height"
            int r2 = r7.getInteger(r2)     // Catch:{ all -> 0x0160 }
            r6.sliceHeight = r2     // Catch:{ all -> 0x0160 }
        L_0x0128:
            java.lang.String r2 = "HardwareVideoDecoder"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0160 }
            r4.<init>()     // Catch:{ all -> 0x0160 }
            java.lang.String r5 = "Frame stride and slice height: "
            r4.append(r5)     // Catch:{ all -> 0x0160 }
            int r5 = r6.stride     // Catch:{ all -> 0x0160 }
            r4.append(r5)     // Catch:{ all -> 0x0160 }
            java.lang.String r5 = " x "
            r4.append(r5)     // Catch:{ all -> 0x0160 }
            int r5 = r6.sliceHeight     // Catch:{ all -> 0x0160 }
            r4.append(r5)     // Catch:{ all -> 0x0160 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0160 }
            org.webrtc.ali.Logging.d(r2, r4)     // Catch:{ all -> 0x0160 }
            int r2 = r6.width     // Catch:{ all -> 0x0160 }
            int r4 = r6.stride     // Catch:{ all -> 0x0160 }
            int r2 = java.lang.Math.max(r2, r4)     // Catch:{ all -> 0x0160 }
            r6.stride = r2     // Catch:{ all -> 0x0160 }
            int r2 = r6.height     // Catch:{ all -> 0x0160 }
            int r4 = r6.sliceHeight     // Catch:{ all -> 0x0160 }
            int r2 = java.lang.Math.max(r2, r4)     // Catch:{ all -> 0x0160 }
            r6.sliceHeight = r2     // Catch:{ all -> 0x0160 }
            monitor-exit(r3)     // Catch:{ all -> 0x0160 }
            return
        L_0x0160:
            r2 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0160 }
            throw r2
        L_0x0163:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0163 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.ali.HardwareVideoDecoder.reformat(android.media.MediaFormat):void");
    }

    /* access modifiers changed from: private */
    public void releaseCodecOnOutputThread() {
        this.outputThreadChecker.checkIsOnValidThread();
        Logging.d(TAG, "Releasing MediaCodec on output thread");
        waitOutputBuffersReleasedOnOutputThread();
        try {
            this.codec.stop();
        } catch (Exception e) {
            Logging.e(TAG, "Media decoder stop failed", e);
        }
        try {
            this.codec.release();
        } catch (Exception e2) {
            Logging.e(TAG, "Media decoder release failed", e2);
            this.shutdownException = e2;
        }
        this.codec = null;
        this.callback = null;
        this.outputThread = null;
        this.frameInfos.clear();
        Logging.d(TAG, "Release on output thread done");
    }

    private void waitOutputBuffersReleasedOnOutputThread() {
        this.outputThreadChecker.checkIsOnValidThread();
        synchronized (this.activeOutputBuffersLock) {
            while (this.activeOutputBuffers > 0) {
                Logging.d(TAG, "Waiting for all frames to be released.");
                try {
                    this.activeOutputBuffersLock.wait();
                } catch (InterruptedException e) {
                    Logging.e(TAG, "Interrupted while waiting for output buffers to be released.", e);
                    return;
                }
            }
        }
    }

    private void stopOnOutputThread(Exception e) {
        this.outputThreadChecker.checkIsOnValidThread();
        this.running = false;
        this.shutdownException = e;
    }

    private boolean isSupportedColorFormat(int colorFormat2) {
        for (int supported : MediaCodecUtils.DECODER_COLOR_FORMATS) {
            if (supported == colorFormat2) {
                return true;
            }
        }
        return false;
    }

    private VideoFrame.I420Buffer createBufferFromI420(ByteBuffer buffer, int outputBufferIndex, int offset, int stride2, int sliceHeight2, int width2, int height2) {
        int uvStride = stride2 / 2;
        int i = (width2 + 1) / 2;
        int chromaHeight = (height2 + 1) / 2;
        int yPos = offset;
        int uPos = yPos + (stride2 * sliceHeight2);
        int vPos = uPos + ((uvStride * sliceHeight2) / 2);
        synchronized (this.activeOutputBuffersLock) {
            this.activeOutputBuffers++;
        }
        final ByteBuffer byteBuffer = buffer;
        final int i2 = yPos;
        final int i3 = height2;
        final int i4 = uPos;
        final int i5 = chromaHeight;
        final int i6 = vPos;
        final int i7 = stride2;
        final int i8 = uvStride;
        final int i9 = width2;
        final int i10 = outputBufferIndex;
        return new VideoFrame.I420Buffer() {
            private int refCount = 1;

            public ByteBuffer getDataY() {
                ByteBuffer data = byteBuffer.slice();
                data.position(i2);
                data.limit(i2 + (getStrideY() * i3));
                return data;
            }

            public ByteBuffer getDataU() {
                ByteBuffer data = byteBuffer.slice();
                data.position(i4);
                data.limit(i4 + (getStrideU() * i5));
                return data;
            }

            public ByteBuffer getDataV() {
                ByteBuffer data = byteBuffer.slice();
                data.position(i6);
                data.limit(i6 + (getStrideV() * i5));
                return data;
            }

            public int getStrideY() {
                return i7;
            }

            public int getStrideU() {
                return i8;
            }

            public int getStrideV() {
                return i8;
            }

            public int getWidth() {
                return i9;
            }

            public int getHeight() {
                return i3;
            }

            public VideoFrame.I420Buffer toI420() {
                return this;
            }

            public void retain() {
                this.refCount++;
            }

            public void release() {
                int i = this.refCount - 1;
                this.refCount = i;
                if (i == 0) {
                    HardwareVideoDecoder.this.codec.releaseOutputBuffer(i10, false);
                    synchronized (HardwareVideoDecoder.this.activeOutputBuffersLock) {
                        HardwareVideoDecoder.access$610(HardwareVideoDecoder.this);
                        HardwareVideoDecoder.this.activeOutputBuffersLock.notifyAll();
                    }
                }
            }
        };
    }

    private static void copyI420(ByteBuffer src, int offset, VideoFrame.I420Buffer frameBuffer, int stride2, int sliceHeight2, int width2, int height2) {
        int uvStride = stride2 / 2;
        int chromaWidth = (width2 + 1) / 2;
        int chromaHeight = sliceHeight2 % 2 == 0 ? (height2 + 1) / 2 : height2 / 2;
        int yPos = offset;
        int uPos = yPos + (stride2 * sliceHeight2);
        ByteBuffer byteBuffer = src;
        copyPlane(byteBuffer, yPos, stride2, frameBuffer.getDataY(), 0, frameBuffer.getStrideY(), width2, height2);
        int i = uvStride;
        int i2 = chromaWidth;
        int i3 = chromaHeight;
        copyPlane(byteBuffer, uPos, i, frameBuffer.getDataU(), 0, frameBuffer.getStrideU(), i2, i3);
        copyPlane(byteBuffer, uPos + ((uvStride * sliceHeight2) / 2), i, frameBuffer.getDataV(), 0, frameBuffer.getStrideV(), i2, i3);
        if (sliceHeight2 % 2 != 0) {
            int strideU = frameBuffer.getStrideU();
            int endU = chromaHeight * strideU;
            copyRow(frameBuffer.getDataU(), endU - strideU, frameBuffer.getDataU(), endU, chromaWidth);
            int strideV = frameBuffer.getStrideV();
            int endV = chromaHeight * strideV;
            copyRow(frameBuffer.getDataV(), endV - strideV, frameBuffer.getDataV(), endV, chromaWidth);
        }
    }

    private static void nv12ToI420(ByteBuffer src, int offset, VideoFrame.I420Buffer frameBuffer, int stride2, int sliceHeight2, int width2, int height2) {
        ByteBuffer byteBuffer = src;
        int yPos = offset;
        int uvPos = yPos + (stride2 * sliceHeight2);
        int chromaWidth = (width2 + 1) / 2;
        int chromaHeight = (height2 + 1) / 2;
        copyPlane(src, yPos, stride2, frameBuffer.getDataY(), 0, frameBuffer.getStrideY(), width2, height2);
        int dstUPos = 0;
        int dstVPos = 0;
        for (int i = 0; i < chromaHeight; i++) {
            for (int j = 0; j < chromaWidth; j++) {
                frameBuffer.getDataU().put(dstUPos + j, src.get((j * 2) + uvPos));
                frameBuffer.getDataV().put(dstVPos + j, src.get((j * 2) + uvPos + 1));
            }
            dstUPos += frameBuffer.getStrideU();
            dstVPos += frameBuffer.getStrideV();
            uvPos += stride2;
        }
    }

    private static void copyPlane(ByteBuffer src, int srcPos, int srcStride, ByteBuffer dst, int dstPos, int dstStride, int width2, int height2) {
        for (int i = 0; i < height2; i++) {
            copyRow(src, srcPos, dst, dstPos, width2);
            srcPos += srcStride;
            dstPos += dstStride;
        }
    }

    private static void copyRow(ByteBuffer src, int srcPos, ByteBuffer dst, int dstPos, int width2) {
        for (int i = 0; i < width2; i++) {
            dst.put(dstPos + i, src.get(srcPos + i));
        }
    }
}
