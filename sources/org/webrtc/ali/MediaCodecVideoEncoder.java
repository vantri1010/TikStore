package org.webrtc.ali;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.webrtc.ali.EglBase14;

public class MediaCodecVideoEncoder {
    private static final int BITRATE_ADJUSTMENT_FPS = 30;
    private static final double BITRATE_CORRECTION_MAX_SCALE = 4.0d;
    private static final double BITRATE_CORRECTION_SEC = 3.0d;
    private static final int BITRATE_CORRECTION_STEPS = 20;
    private static final int COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m = 2141391876;
    private static final int DEQUEUE_TIMEOUT = 0;
    private static final String[] H264_HW_EXCEPTION_MODELS = {"SAMSUNG-SGH-I337", "Nexus 7", "Nexus 4"};
    private static final String H264_MIME_TYPE = "video/avc";
    private static final int MAXIMUM_INITIAL_FPS = 30;
    private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
    private static final long QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_L_MS = 15000;
    private static final long QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS = 20000;
    private static final long QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_N_MS = 15000;
    private static final String TAG = "MediaCodecVideoEncoder";
    private static final int VIDEO_AVCLevel3 = 256;
    private static final int VIDEO_AVCProfileHigh = 8;
    private static final int VIDEO_ControlRateConstant = 2;
    private static final String VP8_MIME_TYPE = "video/x-vnd.on2.vp8";
    private static final String VP9_MIME_TYPE = "video/x-vnd.on2.vp9";
    private static int codecErrors = 0;
    private static MediaCodecVideoEncoderErrorCallback errorCallback = null;
    private static final MediaCodecProperties exynosH264HighProfileHwProperties;
    private static final MediaCodecProperties exynosH264HwProperties;
    private static final MediaCodecProperties exynosVp8HwProperties = new MediaCodecProperties("OMX.Exynos.", 23, BitrateAdjustmentType.DYNAMIC_ADJUSTMENT);
    private static final MediaCodecProperties exynosVp9HwProperties;
    private static final MediaCodecProperties[] h264HighProfileHwList;
    private static final MediaCodecProperties[] h264HwList;
    private static Set<String> hwEncoderDisabledTypes = new HashSet();
    private static final MediaCodecProperties intelVp8HwProperties = new MediaCodecProperties("OMX.Intel.", 21, BitrateAdjustmentType.NO_ADJUSTMENT);
    private static final MediaCodecProperties qcomH264HwProperties = new MediaCodecProperties("OMX.qcom.", 19, BitrateAdjustmentType.NO_ADJUSTMENT);
    private static final MediaCodecProperties qcomVp8HwProperties = new MediaCodecProperties("OMX.qcom.", 19, BitrateAdjustmentType.NO_ADJUSTMENT);
    private static final MediaCodecProperties qcomVp9HwProperties = new MediaCodecProperties("OMX.qcom.", 24, BitrateAdjustmentType.NO_ADJUSTMENT);
    private static MediaCodecVideoEncoder runningInstance = null;
    private static final int[] supportedColorList = {19, 21, 2141391872, COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m};
    private static final int[] supportedSurfaceColorList = {2130708361};
    private static final MediaCodecProperties[] vp9HwList;
    private double bitrateAccumulator;
    private double bitrateAccumulatorMax;
    private int bitrateAdjustmentScaleExp;
    private BitrateAdjustmentType bitrateAdjustmentType = BitrateAdjustmentType.NO_ADJUSTMENT;
    private double bitrateObservationTimeMs;
    private int colorFormat;
    private ByteBuffer configData = null;
    private GlRectDrawer drawer;
    private EglBase14 eglBase;
    private long forcedKeyFrameMs;
    private int height;
    private Surface inputSurface;
    private long lastKeyFrameMs;
    /* access modifiers changed from: private */
    public MediaCodec mediaCodec;
    private Thread mediaCodecThread;
    private ByteBuffer[] outputBuffers;
    private int profile;
    private int targetBitrateBps;
    private int targetFps;
    private VideoCodecType type;
    private int width;

    public enum BitrateAdjustmentType {
        NO_ADJUSTMENT,
        FRAMERATE_ADJUSTMENT,
        DYNAMIC_ADJUSTMENT
    }

    public interface MediaCodecVideoEncoderErrorCallback {
        void onMediaCodecVideoEncoderCriticalError(int i);
    }

    public enum VideoCodecType {
        VIDEO_CODEC_VP8,
        VIDEO_CODEC_VP9,
        VIDEO_CODEC_H264
    }

    static {
        MediaCodecProperties mediaCodecProperties = new MediaCodecProperties("OMX.Exynos.", 24, BitrateAdjustmentType.FRAMERATE_ADJUSTMENT);
        exynosVp9HwProperties = mediaCodecProperties;
        vp9HwList = new MediaCodecProperties[]{qcomVp9HwProperties, mediaCodecProperties};
        MediaCodecProperties mediaCodecProperties2 = new MediaCodecProperties("OMX.Exynos.", 21, BitrateAdjustmentType.FRAMERATE_ADJUSTMENT);
        exynosH264HwProperties = mediaCodecProperties2;
        h264HwList = new MediaCodecProperties[]{qcomH264HwProperties, mediaCodecProperties2};
        MediaCodecProperties mediaCodecProperties3 = new MediaCodecProperties("OMX.Exynos.", 23, BitrateAdjustmentType.FRAMERATE_ADJUSTMENT);
        exynosH264HighProfileHwProperties = mediaCodecProperties3;
        h264HighProfileHwList = new MediaCodecProperties[]{mediaCodecProperties3};
    }

    public enum H264Profile {
        CONSTRAINED_BASELINE(0),
        BASELINE(1),
        MAIN(2),
        CONSTRAINED_HIGH(3),
        HIGH(4);
        
        private final int value;

        private H264Profile(int value2) {
            this.value = value2;
        }

        public int getValue() {
            return this.value;
        }
    }

    private static class MediaCodecProperties {
        public final BitrateAdjustmentType bitrateAdjustmentType;
        public final String codecPrefix;
        public final int minSdk;

        MediaCodecProperties(String codecPrefix2, int minSdk2, BitrateAdjustmentType bitrateAdjustmentType2) {
            this.codecPrefix = codecPrefix2;
            this.minSdk = minSdk2;
            this.bitrateAdjustmentType = bitrateAdjustmentType2;
        }
    }

    private static MediaCodecProperties[] vp8HwList() {
        ArrayList<MediaCodecProperties> supported_codecs = new ArrayList<>();
        supported_codecs.add(qcomVp8HwProperties);
        supported_codecs.add(exynosVp8HwProperties);
        if (PeerConnectionFactory.fieldTrialsFindFullName("WebRTC-IntelVP8").equals("Enabled")) {
            supported_codecs.add(intelVp8HwProperties);
        }
        return (MediaCodecProperties[]) supported_codecs.toArray(new MediaCodecProperties[supported_codecs.size()]);
    }

    public static void setErrorCallback(MediaCodecVideoEncoderErrorCallback errorCallback2) {
        Logging.d(TAG, "Set error callback");
        errorCallback = errorCallback2;
    }

    public static void disableVp8HwCodec() {
        Logging.w(TAG, "VP8 encoding is disabled by application.");
        hwEncoderDisabledTypes.add("video/x-vnd.on2.vp8");
    }

    public static void disableVp9HwCodec() {
        Logging.w(TAG, "VP9 encoding is disabled by application.");
        hwEncoderDisabledTypes.add("video/x-vnd.on2.vp9");
    }

    public static void disableH264HwCodec() {
        Logging.w(TAG, "H.264 encoding is disabled by application.");
        hwEncoderDisabledTypes.add("video/avc");
    }

    public static boolean isVp8HwSupported() {
        return !hwEncoderDisabledTypes.contains("video/x-vnd.on2.vp8") && findHwEncoder("video/x-vnd.on2.vp8", vp8HwList(), supportedColorList) != null;
    }

    public static EncoderProperties vp8HwEncoderProperties() {
        if (hwEncoderDisabledTypes.contains("video/x-vnd.on2.vp8")) {
            return null;
        }
        return findHwEncoder("video/x-vnd.on2.vp8", vp8HwList(), supportedColorList);
    }

    public static boolean isVp9HwSupported() {
        return !hwEncoderDisabledTypes.contains("video/x-vnd.on2.vp9") && findHwEncoder("video/x-vnd.on2.vp9", vp9HwList, supportedColorList) != null;
    }

    public static boolean isH264HwSupported() {
        return !hwEncoderDisabledTypes.contains("video/avc") && findHwEncoder("video/avc", h264HwList, supportedColorList) != null;
    }

    public static boolean isH264HighProfileHwSupported() {
        return !hwEncoderDisabledTypes.contains("video/avc") && findHwEncoder("video/avc", h264HighProfileHwList, supportedColorList) != null;
    }

    public static boolean isVp8HwSupportedUsingTextures() {
        return !hwEncoderDisabledTypes.contains("video/x-vnd.on2.vp8") && findHwEncoder("video/x-vnd.on2.vp8", vp8HwList(), supportedSurfaceColorList) != null;
    }

    public static boolean isVp9HwSupportedUsingTextures() {
        return !hwEncoderDisabledTypes.contains("video/x-vnd.on2.vp9") && findHwEncoder("video/x-vnd.on2.vp9", vp9HwList, supportedSurfaceColorList) != null;
    }

    public static boolean isH264HwSupportedUsingTextures() {
        return !hwEncoderDisabledTypes.contains("video/avc") && findHwEncoder("video/avc", h264HwList, supportedSurfaceColorList) != null;
    }

    public static class EncoderProperties {
        public final BitrateAdjustmentType bitrateAdjustmentType;
        public final String codecName;
        public final int colorFormat;

        public EncoderProperties(String codecName2, int colorFormat2, BitrateAdjustmentType bitrateAdjustmentType2) {
            this.codecName = codecName2;
            this.colorFormat = colorFormat2;
            this.bitrateAdjustmentType = bitrateAdjustmentType2;
        }
    }

    private static EncoderProperties findHwEncoder(String mime, MediaCodecProperties[] supportedHwCodecProperties, int[] colorList) {
        String name;
        BitrateAdjustmentType bitrateAdjustmentType2;
        BitrateAdjustmentType bitrateAdjustmentType3;
        String str = mime;
        MediaCodecProperties[] mediaCodecPropertiesArr = supportedHwCodecProperties;
        int[] iArr = colorList;
        if (Build.VERSION.SDK_INT < 19) {
            return null;
        }
        if (!str.equals("video/avc") || !Arrays.asList(H264_HW_EXCEPTION_MODELS).contains(Build.MODEL)) {
            for (int i = 0; i < MediaCodecList.getCodecCount(); i++) {
                MediaCodecInfo info = null;
                try {
                    info = MediaCodecList.getCodecInfoAt(i);
                } catch (IllegalArgumentException e) {
                    Logging.e(TAG, "Cannot retrieve encoder codec info", e);
                }
                if (info != null && info.isEncoder()) {
                    String[] supportedTypes = info.getSupportedTypes();
                    int length = supportedTypes.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            name = null;
                            break;
                        } else if (supportedTypes[i2].equals(str)) {
                            name = info.getName();
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (name == null) {
                        continue;
                    } else {
                        Logging.v(TAG, "Found candidate encoder " + name);
                        BitrateAdjustmentType bitrateAdjustmentType4 = BitrateAdjustmentType.NO_ADJUSTMENT;
                        int length2 = mediaCodecPropertiesArr.length;
                        int i3 = 0;
                        while (true) {
                            if (i3 >= length2) {
                                bitrateAdjustmentType2 = bitrateAdjustmentType4;
                                bitrateAdjustmentType3 = null;
                                break;
                            }
                            MediaCodecProperties codecProperties = mediaCodecPropertiesArr[i3];
                            if (name.startsWith(codecProperties.codecPrefix)) {
                                if (Build.VERSION.SDK_INT < codecProperties.minSdk) {
                                    Logging.w(TAG, "Codec " + name + " is disabled due to SDK version " + Build.VERSION.SDK_INT);
                                } else {
                                    if (codecProperties.bitrateAdjustmentType != BitrateAdjustmentType.NO_ADJUSTMENT) {
                                        bitrateAdjustmentType4 = codecProperties.bitrateAdjustmentType;
                                        Logging.w(TAG, "Codec " + name + " requires bitrate adjustment: " + bitrateAdjustmentType4);
                                    }
                                    bitrateAdjustmentType2 = bitrateAdjustmentType4;
                                    bitrateAdjustmentType3 = 1;
                                }
                            }
                            i3++;
                        }
                        if (bitrateAdjustmentType3 == null) {
                            continue;
                        } else {
                            try {
                                MediaCodecInfo.CodecCapabilities capabilities = info.getCapabilitiesForType(str);
                                for (int colorFormat2 : capabilities.colorFormats) {
                                    Logging.v(TAG, "   Color: 0x" + Integer.toHexString(colorFormat2));
                                }
                                for (int supportedColorFormat : iArr) {
                                    int[] iArr2 = capabilities.colorFormats;
                                    int length3 = iArr2.length;
                                    int i4 = 0;
                                    while (i4 < length3) {
                                        MediaCodecInfo.CodecCapabilities capabilities2 = capabilities;
                                        int codecColorFormat = iArr2[i4];
                                        if (codecColorFormat == supportedColorFormat) {
                                            Logging.d(TAG, "Found target encoder for mime " + str + " : " + name + ". Color: 0x" + Integer.toHexString(codecColorFormat) + ". Bitrate adjustment: " + bitrateAdjustmentType2);
                                            return new EncoderProperties(name, codecColorFormat, bitrateAdjustmentType2);
                                        }
                                        i4++;
                                        capabilities = capabilities2;
                                    }
                                }
                            } catch (IllegalArgumentException e2) {
                                Logging.e(TAG, "Cannot retrieve encoder capabilities", e2);
                            }
                        }
                    }
                }
            }
            return null;
        }
        Logging.w(TAG, "Model: " + Build.MODEL + " has black listed H.264 encoder.");
        return null;
    }

    private void checkOnMediaCodecThread() {
        if (this.mediaCodecThread.getId() != Thread.currentThread().getId()) {
            throw new RuntimeException("MediaCodecVideoEncoder previously operated on " + this.mediaCodecThread + " but is now called on " + Thread.currentThread());
        }
    }

    public static void printStackTrace() {
        Thread thread;
        MediaCodecVideoEncoder mediaCodecVideoEncoder = runningInstance;
        if (mediaCodecVideoEncoder != null && (thread = mediaCodecVideoEncoder.mediaCodecThread) != null) {
            StackTraceElement[] mediaCodecStackTraces = thread.getStackTrace();
            if (mediaCodecStackTraces.length > 0) {
                Logging.d(TAG, "MediaCodecVideoEncoder stacks trace:");
                for (StackTraceElement stackTrace : mediaCodecStackTraces) {
                    Logging.d(TAG, stackTrace.toString());
                }
            }
        }
    }

    static MediaCodec createByCodecName(String codecName) {
        try {
            return MediaCodec.createByCodecName(codecName);
        } catch (Exception e) {
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean initEncode(VideoCodecType type2, int profile2, int width2, int height2, int kbps, int fps, EglBase14.Context sharedContext) {
        EncoderProperties properties;
        int fps2;
        boolean useSurface;
        VideoCodecType videoCodecType = type2;
        int i = profile2;
        int i2 = width2;
        int i3 = height2;
        int i4 = kbps;
        int i5 = fps;
        EglBase14.Context context = sharedContext;
        boolean useSurface2 = context != null;
        Logging.d(TAG, "Java initEncode: " + videoCodecType + ". Profile: " + i + " : " + i2 + " x " + i3 + ". @ " + i4 + " kbps. Fps: " + i5 + ". Encode from texture : " + useSurface2);
        this.profile = i;
        this.width = i2;
        this.height = i3;
        if (this.mediaCodecThread == null) {
            String mime = null;
            int keyFrameIntervalSec = 0;
            if (videoCodecType == VideoCodecType.VIDEO_CODEC_VP8) {
                mime = "video/x-vnd.on2.vp8";
                properties = findHwEncoder("video/x-vnd.on2.vp8", vp8HwList(), useSurface2 ? supportedSurfaceColorList : supportedColorList);
                keyFrameIntervalSec = 100;
            } else if (videoCodecType == VideoCodecType.VIDEO_CODEC_VP9) {
                mime = "video/x-vnd.on2.vp9";
                properties = findHwEncoder("video/x-vnd.on2.vp9", vp9HwList, useSurface2 ? supportedSurfaceColorList : supportedColorList);
                keyFrameIntervalSec = 100;
            } else if (videoCodecType == VideoCodecType.VIDEO_CODEC_H264) {
                mime = "video/avc";
                EncoderProperties properties2 = findHwEncoder("video/avc", h264HwList, useSurface2 ? supportedSurfaceColorList : supportedColorList);
                if (i == H264Profile.CONSTRAINED_HIGH.getValue()) {
                    if (findHwEncoder("video/avc", h264HighProfileHwList, useSurface2 ? supportedSurfaceColorList : supportedColorList) != null) {
                        Logging.d(TAG, "High profile H.264 encoder supported.");
                    } else {
                        Logging.d(TAG, "High profile H.264 encoder requested, but not supported. Use baseline.");
                    }
                }
                keyFrameIntervalSec = 20;
                properties = properties2;
            } else {
                properties = null;
            }
            if (properties != null) {
                runningInstance = this;
                this.colorFormat = properties.colorFormat;
                BitrateAdjustmentType bitrateAdjustmentType2 = properties.bitrateAdjustmentType;
                this.bitrateAdjustmentType = bitrateAdjustmentType2;
                if (bitrateAdjustmentType2 == BitrateAdjustmentType.FRAMERATE_ADJUSTMENT) {
                    fps2 = 30;
                } else {
                    fps2 = Math.min(i5, 30);
                }
                this.forcedKeyFrameMs = 0;
                this.lastKeyFrameMs = -1;
                if (videoCodecType != VideoCodecType.VIDEO_CODEC_VP8) {
                    useSurface = useSurface2;
                } else if (properties.codecName.startsWith(qcomVp8HwProperties.codecPrefix)) {
                    useSurface = useSurface2;
                    if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
                        this.forcedKeyFrameMs = 15000;
                    } else if (Build.VERSION.SDK_INT == 23) {
                        this.forcedKeyFrameMs = QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS;
                    } else if (Build.VERSION.SDK_INT > 23) {
                        this.forcedKeyFrameMs = 15000;
                    }
                } else {
                    useSurface = useSurface2;
                }
                Logging.d(TAG, "Color format: " + this.colorFormat + ". Bitrate adjustment: " + this.bitrateAdjustmentType + ". Key frame interval: " + this.forcedKeyFrameMs + " . Initial fps: " + fps2);
                int i6 = i4 * 1000;
                this.targetBitrateBps = i6;
                this.targetFps = fps2;
                this.bitrateAccumulatorMax = ((double) i6) / 8.0d;
                this.bitrateAccumulator = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
                this.bitrateObservationTimeMs = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
                this.bitrateAdjustmentScaleExp = 0;
                this.mediaCodecThread = Thread.currentThread();
                try {
                    MediaFormat format = MediaFormat.createVideoFormat(mime, i2, i3);
                    format.setInteger("bitrate", this.targetBitrateBps);
                    format.setInteger("bitrate-mode", 2);
                    format.setInteger("color-format", properties.colorFormat);
                    format.setInteger("frame-rate", this.targetFps);
                    format.setInteger("i-frame-interval", keyFrameIntervalSec);
                    Logging.d(TAG, "  Format: " + format);
                    MediaCodec createByCodecName = createByCodecName(properties.codecName);
                    this.mediaCodec = createByCodecName;
                    this.type = videoCodecType;
                    if (createByCodecName == null) {
                        Logging.e(TAG, "Can not create media encoder");
                        release();
                        return false;
                    }
                    createByCodecName.configure(format, (Surface) null, (MediaCrypto) null, 1);
                    if (useSurface) {
                        this.eglBase = new EglBase14(context, EglBase.CONFIG_RECORDABLE);
                        Surface createInputSurface = this.mediaCodec.createInputSurface();
                        this.inputSurface = createInputSurface;
                        this.eglBase.createSurface(createInputSurface);
                        this.drawer = new GlRectDrawer();
                    }
                    this.mediaCodec.start();
                    this.outputBuffers = this.mediaCodec.getOutputBuffers();
                    Logging.d(TAG, "Output buffers: " + this.outputBuffers.length);
                    return true;
                } catch (IllegalStateException e) {
                    Logging.e(TAG, "initEncode failed", e);
                    release();
                    return false;
                }
            } else {
                throw new RuntimeException("Can not find HW encoder for " + videoCodecType);
            }
        } else {
            throw new RuntimeException("Forgot to release()?");
        }
    }

    /* access modifiers changed from: package-private */
    public ByteBuffer[] getInputBuffers() {
        ByteBuffer[] inputBuffers = this.mediaCodec.getInputBuffers();
        Logging.d(TAG, "Input buffers: " + inputBuffers.length);
        return inputBuffers;
    }

    /* access modifiers changed from: package-private */
    public void checkKeyFrameRequired(boolean requestedKeyFrame, long presentationTimestampUs) {
        long presentationTimestampMs = (500 + presentationTimestampUs) / 1000;
        if (this.lastKeyFrameMs < 0) {
            this.lastKeyFrameMs = presentationTimestampMs;
        }
        boolean forcedKeyFrame = false;
        if (!requestedKeyFrame) {
            long j = this.forcedKeyFrameMs;
            if (j > 0 && presentationTimestampMs > this.lastKeyFrameMs + j) {
                forcedKeyFrame = true;
            }
        }
        if (requestedKeyFrame || forcedKeyFrame) {
            if (requestedKeyFrame) {
                Logging.d(TAG, "Sync frame request");
            } else {
                Logging.d(TAG, "Sync frame forced");
            }
            Bundle b = new Bundle();
            b.putInt("request-sync", 0);
            this.mediaCodec.setParameters(b);
            this.lastKeyFrameMs = presentationTimestampMs;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean encodeBuffer(boolean isKeyframe, int inputBuffer, int size, long presentationTimestampUs) {
        checkOnMediaCodecThread();
        try {
            checkKeyFrameRequired(isKeyframe, presentationTimestampUs);
            this.mediaCodec.queueInputBuffer(inputBuffer, 0, size, presentationTimestampUs, 0);
            return true;
        } catch (IllegalStateException e) {
            Logging.e(TAG, "encodeBuffer failed", e);
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean encodeTexture(boolean isKeyframe, int oesTextureId, float[] transformationMatrix, long presentationTimestampUs) {
        checkOnMediaCodecThread();
        try {
            checkKeyFrameRequired(isKeyframe, presentationTimestampUs);
            this.eglBase.makeCurrent();
            GLES20.glClear(16384);
            this.drawer.drawOes(oesTextureId, transformationMatrix, this.width, this.height, 0, 0, this.width, this.height);
            this.eglBase.swapBuffers(TimeUnit.MICROSECONDS.toNanos(presentationTimestampUs));
            return true;
        } catch (RuntimeException e) {
            Logging.e(TAG, "encodeTexture failed", e);
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void release() {
        Logging.d(TAG, "Java releaseEncoder");
        checkOnMediaCodecThread();
        final AnonymousClass1CaughtException caughtException = new Object() {
            Exception e;
        };
        boolean stopHung = false;
        if (this.mediaCodec != null) {
            final CountDownLatch releaseDone = new CountDownLatch(1);
            new Thread(new Runnable() {
                public void run() {
                    Logging.d(MediaCodecVideoEncoder.TAG, "Java releaseEncoder on release thread");
                    try {
                        MediaCodecVideoEncoder.this.mediaCodec.stop();
                    } catch (Exception e) {
                        Logging.e(MediaCodecVideoEncoder.TAG, "Media encoder stop failed", e);
                    }
                    try {
                        MediaCodecVideoEncoder.this.mediaCodec.release();
                    } catch (Exception e2) {
                        Logging.e(MediaCodecVideoEncoder.TAG, "Media encoder release failed", e2);
                        caughtException.e = e2;
                    }
                    Logging.d(MediaCodecVideoEncoder.TAG, "Java releaseEncoder on release thread done");
                    releaseDone.countDown();
                }
            }).start();
            if (!ThreadUtils.awaitUninterruptibly(releaseDone, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS)) {
                Logging.e(TAG, "Media encoder release timeout");
                stopHung = true;
            }
            this.mediaCodec = null;
        }
        this.mediaCodecThread = null;
        GlRectDrawer glRectDrawer = this.drawer;
        if (glRectDrawer != null) {
            glRectDrawer.release();
            this.drawer = null;
        }
        EglBase14 eglBase14 = this.eglBase;
        if (eglBase14 != null) {
            eglBase14.release();
            this.eglBase = null;
        }
        Surface surface = this.inputSurface;
        if (surface != null) {
            surface.release();
            this.inputSurface = null;
        }
        runningInstance = null;
        if (stopHung) {
            codecErrors++;
            if (errorCallback != null) {
                Logging.e(TAG, "Invoke codec error callback. Errors: " + codecErrors);
                errorCallback.onMediaCodecVideoEncoderCriticalError(codecErrors);
            }
            throw new RuntimeException("Media encoder release timeout.");
        } else if (caughtException.e == null) {
            Logging.d(TAG, "Java releaseEncoder done");
        } else {
            RuntimeException runtimeException = new RuntimeException(caughtException.e);
            runtimeException.setStackTrace(ThreadUtils.concatStackTraces(caughtException.e.getStackTrace(), runtimeException.getStackTrace()));
            throw runtimeException;
        }
    }

    private boolean setRates(int kbps, int frameRate) {
        int i;
        checkOnMediaCodecThread();
        int codecBitrateBps = kbps * 1000;
        if (this.bitrateAdjustmentType == BitrateAdjustmentType.DYNAMIC_ADJUSTMENT) {
            this.bitrateAccumulatorMax = ((double) codecBitrateBps) / 8.0d;
            int i2 = this.targetBitrateBps;
            if (i2 > 0 && codecBitrateBps < i2) {
                this.bitrateAccumulator = (this.bitrateAccumulator * ((double) codecBitrateBps)) / ((double) i2);
            }
        }
        this.targetBitrateBps = codecBitrateBps;
        this.targetFps = frameRate;
        if (this.bitrateAdjustmentType == BitrateAdjustmentType.FRAMERATE_ADJUSTMENT && (i = this.targetFps) > 0) {
            codecBitrateBps = (this.targetBitrateBps * 30) / i;
            Logging.v(TAG, "setRates: " + kbps + " -> " + (codecBitrateBps / 1000) + " kbps. Fps: " + this.targetFps);
        } else if (this.bitrateAdjustmentType == BitrateAdjustmentType.DYNAMIC_ADJUSTMENT) {
            Logging.v(TAG, "setRates: " + kbps + " kbps. Fps: " + this.targetFps + ". ExpScale: " + this.bitrateAdjustmentScaleExp);
            int i3 = this.bitrateAdjustmentScaleExp;
            if (i3 != 0) {
                codecBitrateBps = (int) (((double) codecBitrateBps) * getBitrateScale(i3));
            }
        } else {
            Logging.v(TAG, "setRates: " + kbps + " kbps. Fps: " + this.targetFps);
        }
        try {
            Bundle params = new Bundle();
            params.putInt("video-bitrate", codecBitrateBps);
            this.mediaCodec.setParameters(params);
            return true;
        } catch (IllegalStateException e) {
            Logging.e(TAG, "setRates failed", e);
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public int dequeueInputBuffer() {
        checkOnMediaCodecThread();
        try {
            return this.mediaCodec.dequeueInputBuffer(0);
        } catch (IllegalStateException e) {
            Logging.e(TAG, "dequeueIntputBuffer failed", e);
            return -2;
        }
    }

    static class OutputBufferInfo {
        public final ByteBuffer buffer;
        public final int index;
        public final boolean isKeyFrame;
        public final long presentationTimestampUs;

        public OutputBufferInfo(int index2, ByteBuffer buffer2, boolean isKeyFrame2, long presentationTimestampUs2) {
            this.index = index2;
            this.buffer = buffer2;
            this.isKeyFrame = isKeyFrame2;
            this.presentationTimestampUs = presentationTimestampUs2;
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x00ac A[Catch:{ IllegalStateException -> 0x0179 }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0145 A[Catch:{ IllegalStateException -> 0x0179 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.webrtc.ali.MediaCodecVideoEncoder.OutputBufferInfo dequeueOutputBuffer() {
        /*
            r13 = this;
            java.lang.String r0 = "MediaCodecVideoEncoder"
            r13.checkOnMediaCodecThread()
            android.media.MediaCodec$BufferInfo r1 = new android.media.MediaCodec$BufferInfo     // Catch:{ IllegalStateException -> 0x0179 }
            r1.<init>()     // Catch:{ IllegalStateException -> 0x0179 }
            android.media.MediaCodec r2 = r13.mediaCodec     // Catch:{ IllegalStateException -> 0x0179 }
            r3 = 0
            int r2 = r2.dequeueOutputBuffer(r1, r3)     // Catch:{ IllegalStateException -> 0x0179 }
            r5 = 1
            r6 = 0
            if (r2 < 0) goto L_0x00a9
            int r7 = r1.flags     // Catch:{ IllegalStateException -> 0x0179 }
            r7 = r7 & 2
            if (r7 == 0) goto L_0x001e
            r7 = 1
            goto L_0x001f
        L_0x001e:
            r7 = 0
        L_0x001f:
            if (r7 == 0) goto L_0x00a9
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x0179 }
            r8.<init>()     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r9 = "Config frame generated. Offset: "
            r8.append(r9)     // Catch:{ IllegalStateException -> 0x0179 }
            int r9 = r1.offset     // Catch:{ IllegalStateException -> 0x0179 }
            r8.append(r9)     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r9 = ". Size: "
            r8.append(r9)     // Catch:{ IllegalStateException -> 0x0179 }
            int r9 = r1.size     // Catch:{ IllegalStateException -> 0x0179 }
            r8.append(r9)     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r8 = r8.toString()     // Catch:{ IllegalStateException -> 0x0179 }
            org.webrtc.ali.Logging.d(r0, r8)     // Catch:{ IllegalStateException -> 0x0179 }
            int r8 = r1.size     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.allocateDirect(r8)     // Catch:{ IllegalStateException -> 0x0179 }
            r13.configData = r8     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer[] r8 = r13.outputBuffers     // Catch:{ IllegalStateException -> 0x0179 }
            r8 = r8[r2]     // Catch:{ IllegalStateException -> 0x0179 }
            int r9 = r1.offset     // Catch:{ IllegalStateException -> 0x0179 }
            r8.position(r9)     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer[] r8 = r13.outputBuffers     // Catch:{ IllegalStateException -> 0x0179 }
            r8 = r8[r2]     // Catch:{ IllegalStateException -> 0x0179 }
            int r9 = r1.offset     // Catch:{ IllegalStateException -> 0x0179 }
            int r10 = r1.size     // Catch:{ IllegalStateException -> 0x0179 }
            int r9 = r9 + r10
            r8.limit(r9)     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer r8 = r13.configData     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer[] r9 = r13.outputBuffers     // Catch:{ IllegalStateException -> 0x0179 }
            r9 = r9[r2]     // Catch:{ IllegalStateException -> 0x0179 }
            r8.put(r9)     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r8 = ""
            r9 = 0
        L_0x006a:
            int r10 = r1.size     // Catch:{ IllegalStateException -> 0x0179 }
            r11 = 8
            if (r10 >= r11) goto L_0x0072
            int r11 = r1.size     // Catch:{ IllegalStateException -> 0x0179 }
        L_0x0072:
            if (r9 >= r11) goto L_0x0098
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x0179 }
            r10.<init>()     // Catch:{ IllegalStateException -> 0x0179 }
            r10.append(r8)     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer r11 = r13.configData     // Catch:{ IllegalStateException -> 0x0179 }
            byte r11 = r11.get(r9)     // Catch:{ IllegalStateException -> 0x0179 }
            r11 = r11 & 255(0xff, float:3.57E-43)
            java.lang.String r11 = java.lang.Integer.toHexString(r11)     // Catch:{ IllegalStateException -> 0x0179 }
            r10.append(r11)     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r11 = " "
            r10.append(r11)     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r10 = r10.toString()     // Catch:{ IllegalStateException -> 0x0179 }
            r8 = r10
            int r9 = r9 + 1
            goto L_0x006a
        L_0x0098:
            org.webrtc.ali.Logging.d(r0, r8)     // Catch:{ IllegalStateException -> 0x0179 }
            android.media.MediaCodec r9 = r13.mediaCodec     // Catch:{ IllegalStateException -> 0x0179 }
            r9.releaseOutputBuffer(r2, r6)     // Catch:{ IllegalStateException -> 0x0179 }
            android.media.MediaCodec r9 = r13.mediaCodec     // Catch:{ IllegalStateException -> 0x0179 }
            int r3 = r9.dequeueOutputBuffer(r1, r3)     // Catch:{ IllegalStateException -> 0x0179 }
            r2 = r3
            r8 = r2
            goto L_0x00aa
        L_0x00a9:
            r8 = r2
        L_0x00aa:
            if (r8 < 0) goto L_0x0145
            java.nio.ByteBuffer[] r2 = r13.outputBuffers     // Catch:{ IllegalStateException -> 0x0179 }
            r2 = r2[r8]     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer r2 = r2.duplicate()     // Catch:{ IllegalStateException -> 0x0179 }
            r9 = r2
            int r2 = r1.offset     // Catch:{ IllegalStateException -> 0x0179 }
            r9.position(r2)     // Catch:{ IllegalStateException -> 0x0179 }
            int r2 = r1.offset     // Catch:{ IllegalStateException -> 0x0179 }
            int r3 = r1.size     // Catch:{ IllegalStateException -> 0x0179 }
            int r2 = r2 + r3
            r9.limit(r2)     // Catch:{ IllegalStateException -> 0x0179 }
            int r2 = r1.size     // Catch:{ IllegalStateException -> 0x0179 }
            r13.reportEncodedFrame(r2)     // Catch:{ IllegalStateException -> 0x0179 }
            int r2 = r1.flags     // Catch:{ IllegalStateException -> 0x0179 }
            r2 = r2 & r5
            if (r2 == 0) goto L_0x00cd
            goto L_0x00ce
        L_0x00cd:
            r5 = 0
        L_0x00ce:
            r10 = r5
            if (r10 == 0) goto L_0x00d6
            java.lang.String r2 = "Sync frame generated"
            org.webrtc.ali.Logging.d(r0, r2)     // Catch:{ IllegalStateException -> 0x0179 }
        L_0x00d6:
            if (r10 == 0) goto L_0x0136
            org.webrtc.ali.MediaCodecVideoEncoder$VideoCodecType r2 = r13.type     // Catch:{ IllegalStateException -> 0x0179 }
            org.webrtc.ali.MediaCodecVideoEncoder$VideoCodecType r3 = org.webrtc.ali.MediaCodecVideoEncoder.VideoCodecType.VIDEO_CODEC_H264     // Catch:{ IllegalStateException -> 0x0179 }
            if (r2 != r3) goto L_0x0136
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x0179 }
            r2.<init>()     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r3 = "Appending config frame of size "
            r2.append(r3)     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer r3 = r13.configData     // Catch:{ IllegalStateException -> 0x0179 }
            int r3 = r3.capacity()     // Catch:{ IllegalStateException -> 0x0179 }
            r2.append(r3)     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r3 = " to output buffer with offset "
            r2.append(r3)     // Catch:{ IllegalStateException -> 0x0179 }
            int r3 = r1.offset     // Catch:{ IllegalStateException -> 0x0179 }
            r2.append(r3)     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r3 = ", size "
            r2.append(r3)     // Catch:{ IllegalStateException -> 0x0179 }
            int r3 = r1.size     // Catch:{ IllegalStateException -> 0x0179 }
            r2.append(r3)     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r2 = r2.toString()     // Catch:{ IllegalStateException -> 0x0179 }
            org.webrtc.ali.Logging.d(r0, r2)     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer r2 = r13.configData     // Catch:{ IllegalStateException -> 0x0179 }
            int r2 = r2.capacity()     // Catch:{ IllegalStateException -> 0x0179 }
            int r3 = r1.size     // Catch:{ IllegalStateException -> 0x0179 }
            int r2 = r2 + r3
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocateDirect(r2)     // Catch:{ IllegalStateException -> 0x0179 }
            r11 = r2
            java.nio.ByteBuffer r2 = r13.configData     // Catch:{ IllegalStateException -> 0x0179 }
            r2.rewind()     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer r2 = r13.configData     // Catch:{ IllegalStateException -> 0x0179 }
            r11.put(r2)     // Catch:{ IllegalStateException -> 0x0179 }
            r11.put(r9)     // Catch:{ IllegalStateException -> 0x0179 }
            r11.position(r6)     // Catch:{ IllegalStateException -> 0x0179 }
            org.webrtc.ali.MediaCodecVideoEncoder$OutputBufferInfo r12 = new org.webrtc.ali.MediaCodecVideoEncoder$OutputBufferInfo     // Catch:{ IllegalStateException -> 0x0179 }
            long r6 = r1.presentationTimeUs     // Catch:{ IllegalStateException -> 0x0179 }
            r2 = r12
            r3 = r8
            r4 = r11
            r5 = r10
            r2.<init>(r3, r4, r5, r6)     // Catch:{ IllegalStateException -> 0x0179 }
            return r12
        L_0x0136:
            org.webrtc.ali.MediaCodecVideoEncoder$OutputBufferInfo r11 = new org.webrtc.ali.MediaCodecVideoEncoder$OutputBufferInfo     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer r4 = r9.slice()     // Catch:{ IllegalStateException -> 0x0179 }
            long r6 = r1.presentationTimeUs     // Catch:{ IllegalStateException -> 0x0179 }
            r2 = r11
            r3 = r8
            r5 = r10
            r2.<init>(r3, r4, r5, r6)     // Catch:{ IllegalStateException -> 0x0179 }
            return r11
        L_0x0145:
            r2 = -3
            if (r8 != r2) goto L_0x0155
            android.media.MediaCodec r2 = r13.mediaCodec     // Catch:{ IllegalStateException -> 0x0179 }
            java.nio.ByteBuffer[] r2 = r2.getOutputBuffers()     // Catch:{ IllegalStateException -> 0x0179 }
            r13.outputBuffers = r2     // Catch:{ IllegalStateException -> 0x0179 }
            org.webrtc.ali.MediaCodecVideoEncoder$OutputBufferInfo r0 = r13.dequeueOutputBuffer()     // Catch:{ IllegalStateException -> 0x0179 }
            return r0
        L_0x0155:
            r2 = -2
            if (r8 != r2) goto L_0x015d
            org.webrtc.ali.MediaCodecVideoEncoder$OutputBufferInfo r0 = r13.dequeueOutputBuffer()     // Catch:{ IllegalStateException -> 0x0179 }
            return r0
        L_0x015d:
            r2 = -1
            if (r8 != r2) goto L_0x0162
            r0 = 0
            return r0
        L_0x0162:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x0179 }
            r3.<init>()     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r4 = "dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ IllegalStateException -> 0x0179 }
            r3.append(r8)     // Catch:{ IllegalStateException -> 0x0179 }
            java.lang.String r3 = r3.toString()     // Catch:{ IllegalStateException -> 0x0179 }
            r2.<init>(r3)     // Catch:{ IllegalStateException -> 0x0179 }
            throw r2     // Catch:{ IllegalStateException -> 0x0179 }
        L_0x0179:
            r1 = move-exception
            java.lang.String r2 = "dequeueOutputBuffer failed"
            org.webrtc.ali.Logging.e(r0, r2, r1)
            org.webrtc.ali.MediaCodecVideoEncoder$OutputBufferInfo r0 = new org.webrtc.ali.MediaCodecVideoEncoder$OutputBufferInfo
            r4 = -1
            r5 = 0
            r6 = 0
            r7 = -1
            r3 = r0
            r3.<init>(r4, r5, r6, r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.ali.MediaCodecVideoEncoder.dequeueOutputBuffer():org.webrtc.ali.MediaCodecVideoEncoder$OutputBufferInfo");
    }

    private double getBitrateScale(int bitrateAdjustmentScaleExp2) {
        return Math.pow(BITRATE_CORRECTION_MAX_SCALE, ((double) bitrateAdjustmentScaleExp2) / 20.0d);
    }

    private void reportEncodedFrame(int size) {
        if (this.targetFps == 0) {
            int i = size;
        } else if (this.bitrateAdjustmentType != BitrateAdjustmentType.DYNAMIC_ADJUSTMENT) {
            int i2 = size;
        } else {
            int i3 = this.targetFps;
            double d = this.bitrateAccumulator + (((double) size) - (((double) this.targetBitrateBps) / (((double) i3) * 8.0d)));
            this.bitrateAccumulator = d;
            this.bitrateObservationTimeMs += 1000.0d / ((double) i3);
            double bitrateAccumulatorCap = this.bitrateAccumulatorMax * BITRATE_CORRECTION_SEC;
            double min = Math.min(d, bitrateAccumulatorCap);
            this.bitrateAccumulator = min;
            this.bitrateAccumulator = Math.max(min, -bitrateAccumulatorCap);
            if (this.bitrateObservationTimeMs > 3000.0d) {
                Logging.d(TAG, "Acc: " + ((int) this.bitrateAccumulator) + ". Max: " + ((int) this.bitrateAccumulatorMax) + ". ExpScale: " + this.bitrateAdjustmentScaleExp);
                boolean bitrateAdjustmentScaleChanged = false;
                double d2 = this.bitrateAccumulator;
                double d3 = this.bitrateAccumulatorMax;
                if (d2 > d3) {
                    this.bitrateAdjustmentScaleExp -= (int) ((d2 / d3) + 0.5d);
                    this.bitrateAccumulator = d3;
                    bitrateAdjustmentScaleChanged = true;
                } else if (d2 < (-d3)) {
                    this.bitrateAdjustmentScaleExp += (int) (((-d2) / d3) + 0.5d);
                    this.bitrateAccumulator = -d3;
                    bitrateAdjustmentScaleChanged = true;
                }
                if (bitrateAdjustmentScaleChanged) {
                    int min2 = Math.min(this.bitrateAdjustmentScaleExp, 20);
                    this.bitrateAdjustmentScaleExp = min2;
                    this.bitrateAdjustmentScaleExp = Math.max(min2, -20);
                    Logging.d(TAG, "Adjusting bitrate scale to " + this.bitrateAdjustmentScaleExp + ". Value: " + getBitrateScale(this.bitrateAdjustmentScaleExp));
                    setRates(this.targetBitrateBps / 1000, this.targetFps);
                }
                this.bitrateObservationTimeMs = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean releaseOutputBuffer(int index) {
        checkOnMediaCodecThread();
        try {
            this.mediaCodec.releaseOutputBuffer(index, false);
            return true;
        } catch (IllegalStateException e) {
            Logging.e(TAG, "releaseOutputBuffer failed", e);
            return false;
        }
    }
}
