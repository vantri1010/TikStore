package org.webrtc.ali;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Build;
import android.os.SystemClock;
import android.view.Surface;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.webrtc.ali.SurfaceTextureHelper;

public class MediaCodecVideoDecoder {
    private static final int COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m = 2141391876;
    private static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar16m4ka = 2141391874;
    private static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar32m4ka = 2141391873;
    private static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar64x32Tile2m8ka = 2141391875;
    private static final int DEQUEUE_INPUT_TIMEOUT = 500000;
    private static final String FORMAT_KEY_CROP_BOTTOM = "crop-bottom";
    private static final String FORMAT_KEY_CROP_LEFT = "crop-left";
    private static final String FORMAT_KEY_CROP_RIGHT = "crop-right";
    private static final String FORMAT_KEY_CROP_TOP = "crop-top";
    private static final String FORMAT_KEY_SLICE_HEIGHT = "slice-height";
    private static final String FORMAT_KEY_STRIDE = "stride";
    private static final String H264_MIME_TYPE = "video/avc";
    private static final long MAX_DECODE_TIME_MS = 200;
    private static final int MAX_QUEUED_OUTPUTBUFFERS = 3;
    private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
    private static final String TAG = "MediaCodecVideoDecoder";
    private static final String VP8_MIME_TYPE = "video/x-vnd.on2.vp8";
    private static final String VP9_MIME_TYPE = "video/x-vnd.on2.vp9";
    private static int codecErrors = 0;
    private static MediaCodecVideoDecoderErrorCallback errorCallback = null;
    private static Set<String> hwDecoderDisabledTypes = new HashSet();
    private static MediaCodecVideoDecoder runningInstance = null;
    private static final List<Integer> supportedColorList = Arrays.asList(new Integer[]{19, 21, 2141391872, Integer.valueOf(COLOR_QCOM_FORMATYVU420PackedSemiPlanar32m4ka), Integer.valueOf(COLOR_QCOM_FORMATYVU420PackedSemiPlanar16m4ka), Integer.valueOf(COLOR_QCOM_FORMATYVU420PackedSemiPlanar64x32Tile2m8ka), Integer.valueOf(COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m)});
    private static final String supportedExynosH264HighProfileHwCodecPrefix = "OMX.Exynos.";
    private static final String[] supportedH264HwCodecPrefixes = {supportedQcomH264HighProfileHwCodecPrefix, "OMX.Intel.", supportedExynosH264HighProfileHwCodecPrefix};
    private static final String supportedQcomH264HighProfileHwCodecPrefix = "OMX.qcom.";
    private static final String[] supportedVp8HwCodecPrefixes = {supportedQcomH264HighProfileHwCodecPrefix, "OMX.Nvidia.", supportedExynosH264HighProfileHwCodecPrefix, "OMX.Intel."};
    private static final String[] supportedVp9HwCodecPrefixes = {supportedQcomH264HighProfileHwCodecPrefix, supportedExynosH264HighProfileHwCodecPrefix};
    private int colorFormat;
    private final Queue<TimeStamps> decodeStartTimeMs = new LinkedList();
    private final Queue<DecodedOutputBuffer> dequeuedSurfaceOutputBuffers = new LinkedList();
    private int droppedFrames;
    private boolean hasDecodedFirstFrame;
    private int height;
    private ByteBuffer[] inputBuffers;
    /* access modifiers changed from: private */
    public MediaCodec mediaCodec;
    private Thread mediaCodecThread;
    private ByteBuffer[] outputBuffers;
    private int sliceHeight;
    private int stride;
    private Surface surface = null;
    private TextureListener textureListener;
    private boolean useSurface;
    private int width;

    public interface MediaCodecVideoDecoderErrorCallback {
        void onMediaCodecVideoDecoderCriticalError(int i);
    }

    public enum VideoCodecType {
        VIDEO_CODEC_VP8,
        VIDEO_CODEC_VP9,
        VIDEO_CODEC_H264
    }

    public static void setErrorCallback(MediaCodecVideoDecoderErrorCallback errorCallback2) {
        Logging.d(TAG, "Set error callback");
        errorCallback = errorCallback2;
    }

    public static void disableVp8HwCodec() {
        Logging.w(TAG, "VP8 decoding is disabled by application.");
        hwDecoderDisabledTypes.add("video/x-vnd.on2.vp8");
    }

    public static void disableVp9HwCodec() {
        Logging.w(TAG, "VP9 decoding is disabled by application.");
        hwDecoderDisabledTypes.add("video/x-vnd.on2.vp9");
    }

    public static void disableH264HwCodec() {
        Logging.w(TAG, "H.264 decoding is disabled by application.");
        hwDecoderDisabledTypes.add("video/avc");
    }

    public static boolean isVp8HwSupported() {
        return !hwDecoderDisabledTypes.contains("video/x-vnd.on2.vp8") && findDecoder("video/x-vnd.on2.vp8", supportedVp8HwCodecPrefixes) != null;
    }

    public static boolean isVp9HwSupported() {
        return !hwDecoderDisabledTypes.contains("video/x-vnd.on2.vp9") && findDecoder("video/x-vnd.on2.vp9", supportedVp9HwCodecPrefixes) != null;
    }

    public static boolean isH264HwSupported() {
        return !hwDecoderDisabledTypes.contains("video/avc") && findDecoder("video/avc", supportedH264HwCodecPrefixes) != null;
    }

    public static boolean isH264HighProfileHwSupported() {
        if (hwDecoderDisabledTypes.contains("video/avc")) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 21 && findDecoder("video/avc", new String[]{supportedQcomH264HighProfileHwCodecPrefix}) != null) {
            return true;
        }
        if (Build.VERSION.SDK_INT < 23 || findDecoder("video/avc", new String[]{supportedExynosH264HighProfileHwCodecPrefix}) == null) {
            return false;
        }
        return true;
    }

    public static void printStackTrace() {
        Thread thread;
        MediaCodecVideoDecoder mediaCodecVideoDecoder = runningInstance;
        if (mediaCodecVideoDecoder != null && (thread = mediaCodecVideoDecoder.mediaCodecThread) != null) {
            StackTraceElement[] mediaCodecStackTraces = thread.getStackTrace();
            if (mediaCodecStackTraces.length > 0) {
                Logging.d(TAG, "MediaCodecVideoDecoder stacks trace:");
                for (StackTraceElement stackTrace : mediaCodecStackTraces) {
                    Logging.d(TAG, stackTrace.toString());
                }
            }
        }
    }

    private static class DecoderProperties {
        public final String codecName;
        public final int colorFormat;

        public DecoderProperties(String codecName2, int colorFormat2) {
            this.codecName = codecName2;
            this.colorFormat = colorFormat2;
        }
    }

    private static DecoderProperties findDecoder(String mime, String[] supportedCodecPrefixes) {
        if (Build.VERSION.SDK_INT < 19) {
            return null;
        }
        Logging.d(TAG, "Trying to find HW decoder for mime " + mime);
        for (int i = 0; i < MediaCodecList.getCodecCount(); i++) {
            MediaCodecInfo info = null;
            try {
                info = MediaCodecList.getCodecInfoAt(i);
            } catch (IllegalArgumentException e) {
                Logging.e(TAG, "Cannot retrieve decoder codec info", e);
            }
            if (info != null && !info.isEncoder()) {
                String name = null;
                String[] supportedTypes = info.getSupportedTypes();
                int length = supportedTypes.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    } else if (supportedTypes[i2].equals(mime)) {
                        name = info.getName();
                        break;
                    } else {
                        i2++;
                    }
                }
                if (name == null) {
                    continue;
                } else {
                    Logging.d(TAG, "Found candidate decoder " + name);
                    boolean supportedCodec = false;
                    int length2 = supportedCodecPrefixes.length;
                    int i3 = 0;
                    while (true) {
                        if (i3 >= length2) {
                            break;
                        } else if (name.startsWith(supportedCodecPrefixes[i3])) {
                            supportedCodec = true;
                            break;
                        } else {
                            i3++;
                        }
                    }
                    if (!supportedCodec) {
                        continue;
                    } else {
                        try {
                            MediaCodecInfo.CodecCapabilities capabilities = info.getCapabilitiesForType(mime);
                            for (int colorFormat2 : capabilities.colorFormats) {
                                Logging.v(TAG, "   Color: 0x" + Integer.toHexString(colorFormat2));
                            }
                            for (Integer intValue : supportedColorList) {
                                int supportedColorFormat = intValue.intValue();
                                int[] iArr = capabilities.colorFormats;
                                int length3 = iArr.length;
                                int i4 = 0;
                                while (true) {
                                    if (i4 < length3) {
                                        int codecColorFormat = iArr[i4];
                                        if (codecColorFormat == supportedColorFormat) {
                                            Logging.d(TAG, "Found target decoder " + name + ". Color: 0x" + Integer.toHexString(codecColorFormat));
                                            return new DecoderProperties(name, codecColorFormat);
                                        }
                                        i4++;
                                    }
                                }
                            }
                            continue;
                        } catch (IllegalArgumentException e2) {
                            Logging.e(TAG, "Cannot retrieve decoder capabilities", e2);
                        }
                    }
                }
            }
        }
        Logging.d(TAG, "No HW decoder found for mime " + mime);
        return null;
    }

    private void checkOnMediaCodecThread() throws IllegalStateException {
        if (this.mediaCodecThread.getId() != Thread.currentThread().getId()) {
            throw new IllegalStateException("MediaCodecVideoDecoder previously operated on " + this.mediaCodecThread + " but is now called on " + Thread.currentThread());
        }
    }

    private boolean initDecode(VideoCodecType type, int width2, int height2, SurfaceTextureHelper surfaceTextureHelper) {
        String[] supportedCodecPrefixes;
        String mime;
        if (this.mediaCodecThread == null) {
            this.useSurface = surfaceTextureHelper != null;
            if (type == VideoCodecType.VIDEO_CODEC_VP8) {
                mime = "video/x-vnd.on2.vp8";
                supportedCodecPrefixes = supportedVp8HwCodecPrefixes;
            } else if (type == VideoCodecType.VIDEO_CODEC_VP9) {
                mime = "video/x-vnd.on2.vp9";
                supportedCodecPrefixes = supportedVp9HwCodecPrefixes;
            } else if (type == VideoCodecType.VIDEO_CODEC_H264) {
                mime = "video/avc";
                supportedCodecPrefixes = supportedH264HwCodecPrefixes;
            } else {
                throw new RuntimeException("initDecode: Non-supported codec " + type);
            }
            DecoderProperties properties = findDecoder(mime, supportedCodecPrefixes);
            if (properties != null) {
                Logging.d(TAG, "Java initDecode: " + type + " : " + width2 + " x " + height2 + ". Color: 0x" + Integer.toHexString(properties.colorFormat) + ". Use Surface: " + this.useSurface);
                runningInstance = this;
                this.mediaCodecThread = Thread.currentThread();
                try {
                    this.width = width2;
                    this.height = height2;
                    this.stride = width2;
                    this.sliceHeight = height2;
                    if (this.useSurface) {
                        this.textureListener = new TextureListener(surfaceTextureHelper);
                        this.surface = new Surface(surfaceTextureHelper.getSurfaceTexture());
                    }
                    MediaFormat format = MediaFormat.createVideoFormat(mime, width2, height2);
                    if (!this.useSurface) {
                        format.setInteger("color-format", properties.colorFormat);
                    }
                    Logging.d(TAG, "  Format: " + format);
                    MediaCodec createByCodecName = MediaCodecVideoEncoder.createByCodecName(properties.codecName);
                    this.mediaCodec = createByCodecName;
                    if (createByCodecName == null) {
                        Logging.e(TAG, "Can not create media decoder");
                        return false;
                    }
                    createByCodecName.configure(format, this.surface, (MediaCrypto) null, 0);
                    this.mediaCodec.start();
                    this.colorFormat = properties.colorFormat;
                    this.outputBuffers = this.mediaCodec.getOutputBuffers();
                    this.inputBuffers = this.mediaCodec.getInputBuffers();
                    this.decodeStartTimeMs.clear();
                    this.hasDecodedFirstFrame = false;
                    this.dequeuedSurfaceOutputBuffers.clear();
                    this.droppedFrames = 0;
                    Logging.d(TAG, "Input buffers: " + this.inputBuffers.length + ". Output buffers: " + this.outputBuffers.length);
                    return true;
                } catch (IllegalStateException e) {
                    Logging.e(TAG, "initDecode failed", e);
                    return false;
                }
            } else {
                throw new RuntimeException("Cannot find HW decoder for " + type);
            }
        } else {
            throw new RuntimeException("initDecode: Forgot to release()?");
        }
    }

    private void reset(int width2, int height2) {
        if (this.mediaCodecThread == null || this.mediaCodec == null) {
            throw new RuntimeException("Incorrect reset call for non-initialized decoder.");
        }
        Logging.d(TAG, "Java reset: " + width2 + " x " + height2);
        this.mediaCodec.flush();
        this.width = width2;
        this.height = height2;
        this.decodeStartTimeMs.clear();
        this.dequeuedSurfaceOutputBuffers.clear();
        this.hasDecodedFirstFrame = false;
        this.droppedFrames = 0;
    }

    private void release() {
        Logging.d(TAG, "Java releaseDecoder. Total number of dropped frames: " + this.droppedFrames);
        checkOnMediaCodecThread();
        final CountDownLatch releaseDone = new CountDownLatch(1);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Logging.d(MediaCodecVideoDecoder.TAG, "Java releaseDecoder on release thread");
                    MediaCodecVideoDecoder.this.mediaCodec.stop();
                    MediaCodecVideoDecoder.this.mediaCodec.release();
                    Logging.d(MediaCodecVideoDecoder.TAG, "Java releaseDecoder on release thread done");
                } catch (Exception e) {
                    Logging.e(MediaCodecVideoDecoder.TAG, "Media decoder release failed", e);
                }
                releaseDone.countDown();
            }
        }).start();
        if (!ThreadUtils.awaitUninterruptibly(releaseDone, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS)) {
            Logging.e(TAG, "Media decoder release timeout");
            codecErrors++;
            if (errorCallback != null) {
                Logging.e(TAG, "Invoke codec error callback. Errors: " + codecErrors);
                errorCallback.onMediaCodecVideoDecoderCriticalError(codecErrors);
            }
        }
        this.mediaCodec = null;
        this.mediaCodecThread = null;
        runningInstance = null;
        if (this.useSurface) {
            this.surface.release();
            this.surface = null;
            this.textureListener.release();
        }
        Logging.d(TAG, "Java releaseDecoder done");
    }

    private int dequeueInputBuffer() {
        checkOnMediaCodecThread();
        try {
            return this.mediaCodec.dequeueInputBuffer(500000);
        } catch (IllegalStateException e) {
            Logging.e(TAG, "dequeueIntputBuffer failed", e);
            return -2;
        }
    }

    private boolean queueInputBuffer(int inputBufferIndex, int size, long presentationTimeStamUs, long timeStampMs, long ntpTimeStamp) {
        checkOnMediaCodecThread();
        try {
            this.inputBuffers[inputBufferIndex].position(0);
            int i = size;
            try {
                this.inputBuffers[inputBufferIndex].limit(size);
                this.decodeStartTimeMs.add(new TimeStamps(SystemClock.elapsedRealtime(), timeStampMs, ntpTimeStamp));
                this.mediaCodec.queueInputBuffer(inputBufferIndex, 0, size, presentationTimeStamUs, 0);
                return true;
            } catch (IllegalStateException e) {
                e = e;
                Logging.e(TAG, "decode failed", e);
                return false;
            }
        } catch (IllegalStateException e2) {
            e = e2;
            int i2 = size;
            Logging.e(TAG, "decode failed", e);
            return false;
        }
    }

    private static class TimeStamps {
        /* access modifiers changed from: private */
        public final long decodeStartTimeMs;
        /* access modifiers changed from: private */
        public final long ntpTimeStampMs;
        /* access modifiers changed from: private */
        public final long timeStampMs;

        public TimeStamps(long decodeStartTimeMs2, long timeStampMs2, long ntpTimeStampMs2) {
            this.decodeStartTimeMs = decodeStartTimeMs2;
            this.timeStampMs = timeStampMs2;
            this.ntpTimeStampMs = ntpTimeStampMs2;
        }
    }

    private static class DecodedOutputBuffer {
        /* access modifiers changed from: private */
        public final long decodeTimeMs;
        /* access modifiers changed from: private */
        public final long endDecodeTimeMs;
        /* access modifiers changed from: private */
        public final int index;
        /* access modifiers changed from: private */
        public final long ntpTimeStampMs;
        private final int offset;
        /* access modifiers changed from: private */
        public final long presentationTimeStampMs;
        private final int size;
        /* access modifiers changed from: private */
        public final long timeStampMs;

        public DecodedOutputBuffer(int index2, int offset2, int size2, long presentationTimeStampMs2, long timeStampMs2, long ntpTimeStampMs2, long decodeTime, long endDecodeTime) {
            this.index = index2;
            this.offset = offset2;
            this.size = size2;
            this.presentationTimeStampMs = presentationTimeStampMs2;
            this.timeStampMs = timeStampMs2;
            this.ntpTimeStampMs = ntpTimeStampMs2;
            this.decodeTimeMs = decodeTime;
            this.endDecodeTimeMs = endDecodeTime;
        }
    }

    private static class DecodedTextureBuffer {
        private final long decodeTimeMs;
        private final long frameDelayMs;
        private final long ntpTimeStampMs;
        private final long presentationTimeStampMs;
        private final int textureID;
        private final long timeStampMs;
        private final float[] transformMatrix;

        public DecodedTextureBuffer(int textureID2, float[] transformMatrix2, long presentationTimeStampMs2, long timeStampMs2, long ntpTimeStampMs2, long decodeTimeMs2, long frameDelay) {
            this.textureID = textureID2;
            this.transformMatrix = transformMatrix2;
            this.presentationTimeStampMs = presentationTimeStampMs2;
            this.timeStampMs = timeStampMs2;
            this.ntpTimeStampMs = ntpTimeStampMs2;
            this.decodeTimeMs = decodeTimeMs2;
            this.frameDelayMs = frameDelay;
        }
    }

    private static class TextureListener implements SurfaceTextureHelper.OnTextureFrameAvailableListener {
        private DecodedOutputBuffer bufferToRender;
        private final Object newFrameLock = new Object();
        private DecodedTextureBuffer renderedBuffer;
        private final SurfaceTextureHelper surfaceTextureHelper;

        public TextureListener(SurfaceTextureHelper surfaceTextureHelper2) {
            this.surfaceTextureHelper = surfaceTextureHelper2;
            surfaceTextureHelper2.startListening(this);
        }

        public void addBufferToRender(DecodedOutputBuffer buffer) {
            if (this.bufferToRender == null) {
                this.bufferToRender = buffer;
            } else {
                Logging.e(MediaCodecVideoDecoder.TAG, "Unexpected addBufferToRender() called while waiting for a texture.");
                throw new IllegalStateException("Waiting for a texture.");
            }
        }

        public boolean isWaitingForTexture() {
            boolean z;
            synchronized (this.newFrameLock) {
                z = this.bufferToRender != null;
            }
            return z;
        }

        public void onTextureFrameAvailable(int oesTextureId, float[] transformMatrix, long timestampNs) {
            synchronized (this.newFrameLock) {
                if (this.renderedBuffer == null) {
                    this.renderedBuffer = new DecodedTextureBuffer(oesTextureId, transformMatrix, this.bufferToRender.presentationTimeStampMs, this.bufferToRender.timeStampMs, this.bufferToRender.ntpTimeStampMs, this.bufferToRender.decodeTimeMs, SystemClock.elapsedRealtime() - this.bufferToRender.endDecodeTimeMs);
                    this.bufferToRender = null;
                    this.newFrameLock.notifyAll();
                } else {
                    Logging.e(MediaCodecVideoDecoder.TAG, "Unexpected onTextureFrameAvailable() called while already holding a texture.");
                    throw new IllegalStateException("Already holding a texture.");
                }
            }
        }

        public DecodedTextureBuffer dequeueTextureBuffer(int timeoutMs) {
            DecodedTextureBuffer returnedBuffer;
            synchronized (this.newFrameLock) {
                if (this.renderedBuffer == null && timeoutMs > 0 && isWaitingForTexture()) {
                    try {
                        this.newFrameLock.wait((long) timeoutMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                returnedBuffer = this.renderedBuffer;
                this.renderedBuffer = null;
            }
            return returnedBuffer;
        }

        public void release() {
            this.surfaceTextureHelper.stopListening();
            synchronized (this.newFrameLock) {
                if (this.renderedBuffer != null) {
                    this.surfaceTextureHelper.returnTextureFrame();
                    this.renderedBuffer = null;
                }
            }
        }
    }

    private DecodedOutputBuffer dequeueOutputBuffer(int dequeueTimeoutMs) {
        long decodeTimeMs;
        int newWidth;
        int newHeight;
        checkOnMediaCodecThread();
        if (this.decodeStartTimeMs.isEmpty()) {
            return null;
        }
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        while (true) {
            int result = this.mediaCodec.dequeueOutputBuffer(info, TimeUnit.MILLISECONDS.toMicros((long) dequeueTimeoutMs));
            if (result == -3) {
                this.outputBuffers = this.mediaCodec.getOutputBuffers();
                Logging.d(TAG, "Decoder output buffers changed: " + this.outputBuffers.length);
                if (this.hasDecodedFirstFrame) {
                    throw new RuntimeException("Unexpected output buffer change event.");
                }
            } else if (result == -2) {
                MediaFormat format = this.mediaCodec.getOutputFormat();
                Logging.d(TAG, "Decoder format changed: " + format.toString());
                if (!format.containsKey(FORMAT_KEY_CROP_LEFT) || !format.containsKey(FORMAT_KEY_CROP_RIGHT) || !format.containsKey(FORMAT_KEY_CROP_BOTTOM) || !format.containsKey(FORMAT_KEY_CROP_TOP)) {
                    newWidth = format.getInteger("width");
                    newHeight = format.getInteger("height");
                } else {
                    newWidth = (format.getInteger(FORMAT_KEY_CROP_RIGHT) + 1) - format.getInteger(FORMAT_KEY_CROP_LEFT);
                    newHeight = (format.getInteger(FORMAT_KEY_CROP_BOTTOM) + 1) - format.getInteger(FORMAT_KEY_CROP_TOP);
                }
                if (!this.hasDecodedFirstFrame || (newWidth == this.width && newHeight == this.height)) {
                    this.width = newWidth;
                    this.height = newHeight;
                    if (!this.useSurface && format.containsKey("color-format")) {
                        this.colorFormat = format.getInteger("color-format");
                        Logging.d(TAG, "Color: 0x" + Integer.toHexString(this.colorFormat));
                        if (!supportedColorList.contains(Integer.valueOf(this.colorFormat))) {
                            throw new IllegalStateException("Non supported color format: " + this.colorFormat);
                        }
                    }
                    if (format.containsKey(FORMAT_KEY_STRIDE)) {
                        this.stride = format.getInteger(FORMAT_KEY_STRIDE);
                    }
                    if (format.containsKey(FORMAT_KEY_SLICE_HEIGHT)) {
                        this.sliceHeight = format.getInteger(FORMAT_KEY_SLICE_HEIGHT);
                    }
                    Logging.d(TAG, "Frame stride and slice height: " + this.stride + " x " + this.sliceHeight);
                    this.stride = Math.max(this.width, this.stride);
                    this.sliceHeight = Math.max(this.height, this.sliceHeight);
                }
            } else if (result == -1) {
                return null;
            } else {
                this.hasDecodedFirstFrame = true;
                TimeStamps timeStamps = this.decodeStartTimeMs.remove();
                long decodeTimeMs2 = SystemClock.elapsedRealtime() - timeStamps.decodeStartTimeMs;
                if (decodeTimeMs2 > MAX_DECODE_TIME_MS) {
                    Logging.e(TAG, "Very high decode time: " + decodeTimeMs2 + "ms. Q size: " + this.decodeStartTimeMs.size() + ". Might be caused by resuming H264 decoding after a pause.");
                    decodeTimeMs = 200;
                } else {
                    decodeTimeMs = decodeTimeMs2;
                }
                return new DecodedOutputBuffer(result, info.offset, info.size, TimeUnit.MICROSECONDS.toMillis(info.presentationTimeUs), timeStamps.timeStampMs, timeStamps.ntpTimeStampMs, decodeTimeMs, SystemClock.elapsedRealtime());
            }
        }
        throw new RuntimeException("Unexpected size change. Configured " + this.width + "*" + this.height + ". New " + newWidth + "*" + newHeight);
    }

    private DecodedTextureBuffer dequeueTextureBuffer(int dequeueTimeoutMs) {
        int i = dequeueTimeoutMs;
        checkOnMediaCodecThread();
        if (this.useSurface) {
            DecodedOutputBuffer outputBuffer = dequeueOutputBuffer(dequeueTimeoutMs);
            if (outputBuffer != null) {
                this.dequeuedSurfaceOutputBuffers.add(outputBuffer);
            }
            MaybeRenderDecodedTextureBuffer();
            DecodedTextureBuffer renderedBuffer = this.textureListener.dequeueTextureBuffer(i);
            if (renderedBuffer != null) {
                MaybeRenderDecodedTextureBuffer();
                return renderedBuffer;
            } else if (this.dequeuedSurfaceOutputBuffers.size() < Math.min(3, this.outputBuffers.length) && (i <= 0 || this.dequeuedSurfaceOutputBuffers.isEmpty())) {
                return null;
            } else {
                this.droppedFrames++;
                DecodedOutputBuffer droppedFrame = this.dequeuedSurfaceOutputBuffers.remove();
                if (i > 0) {
                    Logging.w(TAG, "Draining decoder. Dropping frame with TS: " + droppedFrame.presentationTimeStampMs + ". Total number of dropped frames: " + this.droppedFrames);
                } else {
                    Logging.w(TAG, "Too many output buffers " + this.dequeuedSurfaceOutputBuffers.size() + ". Dropping frame with TS: " + droppedFrame.presentationTimeStampMs + ". Total number of dropped frames: " + this.droppedFrames);
                }
                this.mediaCodec.releaseOutputBuffer(droppedFrame.index, false);
                return new DecodedTextureBuffer(0, (float[]) null, droppedFrame.presentationTimeStampMs, droppedFrame.timeStampMs, droppedFrame.ntpTimeStampMs, droppedFrame.decodeTimeMs, SystemClock.elapsedRealtime() - droppedFrame.endDecodeTimeMs);
            }
        } else {
            throw new IllegalStateException("dequeueTexture() called for byte buffer decoding.");
        }
    }

    private void MaybeRenderDecodedTextureBuffer() {
        if (!this.dequeuedSurfaceOutputBuffers.isEmpty() && !this.textureListener.isWaitingForTexture()) {
            DecodedOutputBuffer buffer = this.dequeuedSurfaceOutputBuffers.remove();
            this.textureListener.addBufferToRender(buffer);
            this.mediaCodec.releaseOutputBuffer(buffer.index, true);
        }
    }

    private void returnDecodedOutputBuffer(int index) throws IllegalStateException, MediaCodec.CodecException {
        checkOnMediaCodecThread();
        if (!this.useSurface) {
            this.mediaCodec.releaseOutputBuffer(index, false);
            return;
        }
        throw new IllegalStateException("returnDecodedOutputBuffer() called for surface decoding.");
    }
}
