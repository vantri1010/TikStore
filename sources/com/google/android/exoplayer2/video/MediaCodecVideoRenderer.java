package com.google.android.exoplayer2.video;

import android.content.Context;
import android.graphics.Point;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Surface;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.mediacodec.MediaFormatUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.serenegiant.usb.UVCCamera;
import com.zhy.http.okhttp.OkHttpUtils;
import java.nio.ByteBuffer;
import java.util.List;

public class MediaCodecVideoRenderer extends MediaCodecRenderer {
    private static final float INITIAL_FORMAT_MAX_INPUT_SIZE_SCALE_FACTOR = 1.5f;
    private static final String KEY_CROP_BOTTOM = "crop-bottom";
    private static final String KEY_CROP_LEFT = "crop-left";
    private static final String KEY_CROP_RIGHT = "crop-right";
    private static final String KEY_CROP_TOP = "crop-top";
    private static final int MAX_PENDING_OUTPUT_STREAM_OFFSET_COUNT = 10;
    private static final int[] STANDARD_LONG_EDGE_VIDEO_PX = {1920, 1600, 1440, 1280, 960, 854, UVCCamera.DEFAULT_PREVIEW_WIDTH, 540, UVCCamera.DEFAULT_PREVIEW_HEIGHT};
    private static final String TAG = "MediaCodecVideoRenderer";
    private static boolean deviceNeedsSetOutputSurfaceWorkaround;
    private static boolean evaluatedDeviceNeedsSetOutputSurfaceWorkaround;
    private final long allowedJoiningTimeMs;
    private int buffersInCodecCount;
    private CodecMaxValues codecMaxValues;
    private boolean codecNeedsSetOutputSurfaceWorkaround;
    private int consecutiveDroppedFrameCount;
    private final Context context;
    private int currentHeight;
    private float currentPixelWidthHeightRatio;
    private int currentUnappliedRotationDegrees;
    private int currentWidth;
    private final boolean deviceNeedsNoPostProcessWorkaround;
    private long droppedFrameAccumulationStartTimeMs;
    private int droppedFrames;
    private Surface dummySurface;
    private final VideoRendererEventListener.EventDispatcher eventDispatcher;
    private VideoFrameMetadataListener frameMetadataListener;
    private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
    private long initialPositionUs;
    private long joiningDeadlineMs;
    private long lastInputTimeUs;
    private long lastRenderTimeUs;
    private final int maxDroppedFramesToNotify;
    private long outputStreamOffsetUs;
    private int pendingOutputStreamOffsetCount;
    private final long[] pendingOutputStreamOffsetsUs;
    private final long[] pendingOutputStreamSwitchTimesUs;
    private float pendingPixelWidthHeightRatio;
    private int pendingRotationDegrees;
    private boolean renderedFirstFrame;
    private int reportedHeight;
    private float reportedPixelWidthHeightRatio;
    private int reportedUnappliedRotationDegrees;
    private int reportedWidth;
    private int scalingMode;
    private Surface surface;
    private boolean tunneling;
    private int tunnelingAudioSessionId;
    OnFrameRenderedListenerV23 tunnelingOnFrameRenderedListener;

    public MediaCodecVideoRenderer(Context context2, MediaCodecSelector mediaCodecSelector) {
        this(context2, mediaCodecSelector, 0);
    }

    public MediaCodecVideoRenderer(Context context2, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs2) {
        this(context2, mediaCodecSelector, allowedJoiningTimeMs2, (Handler) null, (VideoRendererEventListener) null, -1);
    }

    public MediaCodecVideoRenderer(Context context2, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs2, Handler eventHandler, VideoRendererEventListener eventListener, int maxDroppedFramesToNotify2) {
        this(context2, mediaCodecSelector, allowedJoiningTimeMs2, (DrmSessionManager<FrameworkMediaCrypto>) null, false, eventHandler, eventListener, maxDroppedFramesToNotify2);
    }

    public MediaCodecVideoRenderer(Context context2, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs2, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, VideoRendererEventListener eventListener, int maxDroppedFramesToNotify2) {
        super(2, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, 30.0f);
        this.allowedJoiningTimeMs = allowedJoiningTimeMs2;
        this.maxDroppedFramesToNotify = maxDroppedFramesToNotify2;
        Context applicationContext = context2.getApplicationContext();
        this.context = applicationContext;
        this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(applicationContext);
        this.eventDispatcher = new VideoRendererEventListener.EventDispatcher(eventHandler, eventListener);
        this.deviceNeedsNoPostProcessWorkaround = deviceNeedsNoPostProcessWorkaround();
        this.pendingOutputStreamOffsetsUs = new long[10];
        this.pendingOutputStreamSwitchTimesUs = new long[10];
        this.outputStreamOffsetUs = C.TIME_UNSET;
        this.lastInputTimeUs = C.TIME_UNSET;
        this.joiningDeadlineMs = C.TIME_UNSET;
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = -1.0f;
        this.pendingPixelWidthHeightRatio = -1.0f;
        this.scalingMode = 1;
        clearReportedVideoSize();
    }

    /* access modifiers changed from: protected */
    public int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws MediaCodecUtil.DecoderQueryException {
        int tunnelingSupport = 0;
        if (!MimeTypes.isVideo(format.sampleMimeType)) {
            return 0;
        }
        boolean requiresSecureDecryption = false;
        DrmInitData drmInitData = format.drmInitData;
        if (drmInitData != null) {
            for (int i = 0; i < drmInitData.schemeDataCount; i++) {
                requiresSecureDecryption |= drmInitData.get(i).requiresSecureDecryption;
            }
        }
        List<MediaCodecInfo> decoderInfos = mediaCodecSelector.getDecoderInfos(format.sampleMimeType, requiresSecureDecryption);
        if (decoderInfos.isEmpty()) {
            if (!requiresSecureDecryption || mediaCodecSelector.getDecoderInfos(format.sampleMimeType, false).isEmpty()) {
                return 1;
            }
            return 2;
        } else if (!supportsFormatDrm(drmSessionManager, drmInitData)) {
            return 2;
        } else {
            MediaCodecInfo decoderInfo = decoderInfos.get(0);
            boolean isFormatSupported = decoderInfo.isFormatSupported(format);
            int adaptiveSupport = decoderInfo.isSeamlessAdaptationSupported(format) ? 16 : 8;
            if (decoderInfo.tunneling) {
                tunnelingSupport = 32;
            }
            return adaptiveSupport | tunnelingSupport | (isFormatSupported ? 4 : 3);
        }
    }

    /* access modifiers changed from: protected */
    public void onEnabled(boolean joining) throws ExoPlaybackException {
        super.onEnabled(joining);
        int oldTunnelingAudioSessionId = this.tunnelingAudioSessionId;
        int i = getConfiguration().tunnelingAudioSessionId;
        this.tunnelingAudioSessionId = i;
        this.tunneling = i != 0;
        if (this.tunnelingAudioSessionId != oldTunnelingAudioSessionId) {
            releaseCodec();
        }
        this.eventDispatcher.enabled(this.decoderCounters);
        this.frameReleaseTimeHelper.enable();
    }

    /* access modifiers changed from: protected */
    public void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        if (this.outputStreamOffsetUs == C.TIME_UNSET) {
            this.outputStreamOffsetUs = offsetUs;
        } else {
            int i = this.pendingOutputStreamOffsetCount;
            if (i == this.pendingOutputStreamOffsetsUs.length) {
                Log.w(TAG, "Too many stream changes, so dropping offset: " + this.pendingOutputStreamOffsetsUs[this.pendingOutputStreamOffsetCount - 1]);
            } else {
                this.pendingOutputStreamOffsetCount = i + 1;
            }
            long[] jArr = this.pendingOutputStreamOffsetsUs;
            int i2 = this.pendingOutputStreamOffsetCount;
            jArr[i2 - 1] = offsetUs;
            this.pendingOutputStreamSwitchTimesUs[i2 - 1] = this.lastInputTimeUs;
        }
        super.onStreamChanged(formats, offsetUs);
    }

    /* access modifiers changed from: protected */
    public void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        super.onPositionReset(positionUs, joining);
        clearRenderedFirstFrame();
        this.initialPositionUs = C.TIME_UNSET;
        this.consecutiveDroppedFrameCount = 0;
        this.lastInputTimeUs = C.TIME_UNSET;
        int i = this.pendingOutputStreamOffsetCount;
        if (i != 0) {
            this.outputStreamOffsetUs = this.pendingOutputStreamOffsetsUs[i - 1];
            this.pendingOutputStreamOffsetCount = 0;
        }
        if (joining) {
            setJoiningDeadlineMs();
        } else {
            this.joiningDeadlineMs = C.TIME_UNSET;
        }
    }

    public boolean isReady() {
        Surface surface2;
        if (super.isReady() && (this.renderedFirstFrame || (((surface2 = this.dummySurface) != null && this.surface == surface2) || getCodec() == null || this.tunneling))) {
            this.joiningDeadlineMs = C.TIME_UNSET;
            return true;
        } else if (this.joiningDeadlineMs == C.TIME_UNSET) {
            return false;
        } else {
            if (SystemClock.elapsedRealtime() < this.joiningDeadlineMs) {
                return true;
            }
            this.joiningDeadlineMs = C.TIME_UNSET;
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void onStarted() {
        super.onStarted();
        this.droppedFrames = 0;
        this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
    }

    /* access modifiers changed from: protected */
    public void onStopped() {
        this.joiningDeadlineMs = C.TIME_UNSET;
        maybeNotifyDroppedFrames();
        super.onStopped();
    }

    /* access modifiers changed from: protected */
    public void onDisabled() {
        this.lastInputTimeUs = C.TIME_UNSET;
        this.outputStreamOffsetUs = C.TIME_UNSET;
        this.pendingOutputStreamOffsetCount = 0;
        clearReportedVideoSize();
        clearRenderedFirstFrame();
        this.frameReleaseTimeHelper.disable();
        this.tunnelingOnFrameRenderedListener = null;
        try {
            super.onDisabled();
        } finally {
            this.eventDispatcher.disabled(this.decoderCounters);
        }
    }

    /* access modifiers changed from: protected */
    public void onReset() {
        try {
            super.onReset();
        } finally {
            Surface surface2 = this.dummySurface;
            if (surface2 != null) {
                if (this.surface == surface2) {
                    this.surface = null;
                }
                this.dummySurface.release();
                this.dummySurface = null;
            }
        }
    }

    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        if (messageType == 1) {
            setSurface((Surface) message);
        } else if (messageType == 4) {
            this.scalingMode = ((Integer) message).intValue();
            MediaCodec codec = getCodec();
            if (codec != null) {
                codec.setVideoScalingMode(this.scalingMode);
            }
        } else if (messageType == 6) {
            this.frameMetadataListener = (VideoFrameMetadataListener) message;
        } else {
            super.handleMessage(messageType, message);
        }
    }

    private void setSurface(Surface surface2) throws ExoPlaybackException {
        if (surface2 == null) {
            if (this.dummySurface != null) {
                surface2 = this.dummySurface;
            } else {
                MediaCodecInfo codecInfo = getCodecInfo();
                if (codecInfo != null && shouldUseDummySurface(codecInfo)) {
                    this.dummySurface = DummySurface.newInstanceV17(this.context, codecInfo.secure);
                    surface2 = this.dummySurface;
                }
            }
        }
        if (this.surface != surface2) {
            this.surface = surface2;
            int state = getState();
            MediaCodec codec = getCodec();
            if (codec != null) {
                if (Util.SDK_INT < 23 || surface2 == null || this.codecNeedsSetOutputSurfaceWorkaround) {
                    releaseCodec();
                    maybeInitCodec();
                } else {
                    setOutputSurfaceV23(codec, surface2);
                }
            }
            if (surface2 == null || surface2 == this.dummySurface) {
                clearReportedVideoSize();
                clearRenderedFirstFrame();
                return;
            }
            maybeRenotifyVideoSizeChanged();
            clearRenderedFirstFrame();
            if (state == 2) {
                setJoiningDeadlineMs();
            }
        } else if (surface2 != null && surface2 != this.dummySurface) {
            maybeRenotifyVideoSizeChanged();
            maybeRenotifyRenderedFirstFrame();
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldInitCodec(MediaCodecInfo codecInfo) {
        return this.surface != null || shouldUseDummySurface(codecInfo);
    }

    /* access modifiers changed from: protected */
    public boolean getCodecNeedsEosPropagation() {
        return this.tunneling;
    }

    /* access modifiers changed from: protected */
    public void configureCodec(MediaCodecInfo codecInfo, MediaCodec codec, Format format, MediaCrypto crypto, float codecOperatingRate) throws MediaCodecUtil.DecoderQueryException {
        CodecMaxValues codecMaxValues2 = getCodecMaxValues(codecInfo, format, getStreamFormats());
        this.codecMaxValues = codecMaxValues2;
        MediaFormat mediaFormat = getMediaFormat(format, codecMaxValues2, codecOperatingRate, this.deviceNeedsNoPostProcessWorkaround, this.tunnelingAudioSessionId);
        if (this.surface == null) {
            Assertions.checkState(shouldUseDummySurface(codecInfo));
            if (this.dummySurface == null) {
                this.dummySurface = DummySurface.newInstanceV17(this.context, codecInfo.secure);
            }
            this.surface = this.dummySurface;
        }
        codec.configure(mediaFormat, this.surface, crypto, 0);
        if (Util.SDK_INT >= 23 && this.tunneling) {
            this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(codec);
        }
    }

    /* access modifiers changed from: protected */
    public int canKeepCodec(MediaCodec codec, MediaCodecInfo codecInfo, Format oldFormat, Format newFormat) {
        if (!codecInfo.isSeamlessAdaptationSupported(oldFormat, newFormat, true) || newFormat.width > this.codecMaxValues.width || newFormat.height > this.codecMaxValues.height || getMaxInputSize(codecInfo, newFormat) > this.codecMaxValues.inputSize) {
            return 0;
        }
        return oldFormat.initializationDataEquals(newFormat) ? 3 : 2;
    }

    /* access modifiers changed from: protected */
    public void releaseCodec() {
        try {
            super.releaseCodec();
        } finally {
            this.buffersInCodecCount = 0;
        }
    }

    /* access modifiers changed from: protected */
    public boolean flushOrReleaseCodec() {
        try {
            return super.flushOrReleaseCodec();
        } finally {
            this.buffersInCodecCount = 0;
        }
    }

    /* access modifiers changed from: protected */
    public float getCodecOperatingRateV23(float operatingRate, Format format, Format[] streamFormats) {
        float maxFrameRate = -1.0f;
        for (Format streamFormat : streamFormats) {
            float streamFrameRate = streamFormat.frameRate;
            if (streamFrameRate != -1.0f) {
                maxFrameRate = Math.max(maxFrameRate, streamFrameRate);
            }
        }
        if (maxFrameRate == -1.0f) {
            return -1.0f;
        }
        return maxFrameRate * operatingRate;
    }

    /* access modifiers changed from: protected */
    public void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
        this.eventDispatcher.decoderInitialized(name, initializedTimestampMs, initializationDurationMs);
        this.codecNeedsSetOutputSurfaceWorkaround = codecNeedsSetOutputSurfaceWorkaround(name);
    }

    /* access modifiers changed from: protected */
    public void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        super.onInputFormatChanged(newFormat);
        this.eventDispatcher.inputFormatChanged(newFormat);
        this.pendingPixelWidthHeightRatio = newFormat.pixelWidthHeightRatio;
        this.pendingRotationDegrees = newFormat.rotationDegrees;
    }

    /* access modifiers changed from: protected */
    public void onQueueInputBuffer(DecoderInputBuffer buffer) {
        this.buffersInCodecCount++;
        this.lastInputTimeUs = Math.max(buffer.timeUs, this.lastInputTimeUs);
        if (Util.SDK_INT < 23 && this.tunneling) {
            onProcessedTunneledBuffer(buffer.timeUs);
        }
    }

    /* access modifiers changed from: protected */
    public void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat) {
        int width;
        int height;
        boolean hasCrop = outputFormat.containsKey(KEY_CROP_RIGHT) && outputFormat.containsKey(KEY_CROP_LEFT) && outputFormat.containsKey(KEY_CROP_BOTTOM) && outputFormat.containsKey(KEY_CROP_TOP);
        if (hasCrop) {
            width = (outputFormat.getInteger(KEY_CROP_RIGHT) - outputFormat.getInteger(KEY_CROP_LEFT)) + 1;
        } else {
            width = outputFormat.getInteger("width");
        }
        if (hasCrop) {
            height = (outputFormat.getInteger(KEY_CROP_BOTTOM) - outputFormat.getInteger(KEY_CROP_TOP)) + 1;
        } else {
            height = outputFormat.getInteger("height");
        }
        processOutputFormat(codec, width, height);
    }

    /* access modifiers changed from: protected */
    public boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec, ByteBuffer buffer, int bufferIndex, int bufferFlags, long bufferPresentationTimeUs, boolean shouldSkip, Format format) throws ExoPlaybackException {
        long presentationTimeUs;
        long presentationTimeUs2;
        long unadjustedFrameReleaseTimeNs;
        long j = positionUs;
        long j2 = elapsedRealtimeUs;
        MediaCodec mediaCodec = codec;
        int i = bufferIndex;
        long j3 = bufferPresentationTimeUs;
        if (this.initialPositionUs == C.TIME_UNSET) {
            this.initialPositionUs = j;
        }
        long presentationTimeUs3 = j3 - this.outputStreamOffsetUs;
        if (shouldSkip) {
            skipOutputBuffer(mediaCodec, i, presentationTimeUs3);
            return true;
        }
        long earlyUs = j3 - j;
        if (this.surface != this.dummySurface) {
            long elapsedRealtimeNowUs = SystemClock.elapsedRealtime() * 1000;
            boolean isStarted = getState() == 2;
            if (!this.renderedFirstFrame) {
                presentationTimeUs = presentationTimeUs3;
            } else if (!isStarted || !shouldForceRenderOutputBuffer(earlyUs, elapsedRealtimeNowUs - this.lastRenderTimeUs)) {
                if (!isStarted) {
                } else if (j == this.initialPositionUs) {
                    long j4 = presentationTimeUs3;
                } else {
                    long systemTimeNs = System.nanoTime();
                    long unadjustedFrameReleaseTimeNs2 = systemTimeNs + ((earlyUs - (elapsedRealtimeNowUs - j2)) * 1000);
                    long adjustedReleaseTimeNs = this.frameReleaseTimeHelper.adjustReleaseTime(j3, unadjustedFrameReleaseTimeNs2);
                    long earlyUs2 = (adjustedReleaseTimeNs - systemTimeNs) / 1000;
                    if (shouldDropBuffersToKeyframe(earlyUs2, j2)) {
                        long j5 = unadjustedFrameReleaseTimeNs2;
                        unadjustedFrameReleaseTimeNs = earlyUs2;
                        presentationTimeUs2 = presentationTimeUs3;
                        if (maybeDropBuffersToKeyframe(codec, bufferIndex, presentationTimeUs3, positionUs)) {
                            return false;
                        }
                    } else {
                        presentationTimeUs2 = presentationTimeUs3;
                        long j6 = unadjustedFrameReleaseTimeNs2;
                        unadjustedFrameReleaseTimeNs = earlyUs2;
                    }
                    if (shouldDropOutputBuffer(unadjustedFrameReleaseTimeNs, j2)) {
                        dropOutputBuffer(mediaCodec, i, presentationTimeUs2);
                        return true;
                    }
                    long presentationTimeUs4 = presentationTimeUs2;
                    if (Util.SDK_INT < 21) {
                        long presentationTimeUs5 = presentationTimeUs4;
                        if (unadjustedFrameReleaseTimeNs < 30000) {
                            if (unadjustedFrameReleaseTimeNs > 11000) {
                                try {
                                    Thread.sleep((unadjustedFrameReleaseTimeNs - OkHttpUtils.DEFAULT_MILLISECONDS) / 1000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return false;
                                }
                            }
                            notifyFrameMetadataListener(presentationTimeUs5, adjustedReleaseTimeNs, format);
                            renderOutputBuffer(mediaCodec, i, presentationTimeUs5);
                            return true;
                        }
                    } else if (unadjustedFrameReleaseTimeNs < 50000) {
                        notifyFrameMetadataListener(presentationTimeUs4, adjustedReleaseTimeNs, format);
                        renderOutputBufferV21(codec, bufferIndex, presentationTimeUs4, adjustedReleaseTimeNs);
                        return true;
                    }
                    return false;
                }
                return false;
            } else {
                presentationTimeUs = presentationTimeUs3;
            }
            long releaseTimeNs = System.nanoTime();
            long j7 = earlyUs;
            long presentationTimeUs6 = presentationTimeUs;
            notifyFrameMetadataListener(presentationTimeUs, releaseTimeNs, format);
            if (Util.SDK_INT >= 21) {
                renderOutputBufferV21(codec, bufferIndex, presentationTimeUs6, releaseTimeNs);
                long j8 = presentationTimeUs6;
                return true;
            }
            renderOutputBuffer(mediaCodec, i, presentationTimeUs6);
            return true;
        } else if (!isBufferLate(earlyUs)) {
            return false;
        } else {
            skipOutputBuffer(mediaCodec, i, presentationTimeUs3);
            return true;
        }
    }

    private void processOutputFormat(MediaCodec codec, int width, int height) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.currentPixelWidthHeightRatio = this.pendingPixelWidthHeightRatio;
        if (Util.SDK_INT >= 21) {
            int i = this.pendingRotationDegrees;
            if (i == 90 || i == 270) {
                int rotatedHeight = this.currentWidth;
                this.currentWidth = this.currentHeight;
                this.currentHeight = rotatedHeight;
                this.currentPixelWidthHeightRatio = 1.0f / this.currentPixelWidthHeightRatio;
            }
        } else {
            this.currentUnappliedRotationDegrees = this.pendingRotationDegrees;
        }
        codec.setVideoScalingMode(this.scalingMode);
    }

    private void notifyFrameMetadataListener(long presentationTimeUs, long releaseTimeNs, Format format) {
        VideoFrameMetadataListener videoFrameMetadataListener = this.frameMetadataListener;
        if (videoFrameMetadataListener != null) {
            videoFrameMetadataListener.onVideoFrameAboutToBeRendered(presentationTimeUs, releaseTimeNs, format);
        }
    }

    /* access modifiers changed from: protected */
    public long getOutputStreamOffsetUs() {
        return this.outputStreamOffsetUs;
    }

    /* access modifiers changed from: protected */
    public void onProcessedTunneledBuffer(long presentationTimeUs) {
        Format format = updateOutputFormatForTime(presentationTimeUs);
        if (format != null) {
            processOutputFormat(getCodec(), format.width, format.height);
        }
        maybeNotifyVideoSizeChanged();
        maybeNotifyRenderedFirstFrame();
        onProcessedOutputBuffer(presentationTimeUs);
    }

    /* access modifiers changed from: protected */
    public void onProcessedOutputBuffer(long presentationTimeUs) {
        this.buffersInCodecCount--;
        while (true) {
            int i = this.pendingOutputStreamOffsetCount;
            if (i != 0 && presentationTimeUs >= this.pendingOutputStreamSwitchTimesUs[0]) {
                long[] jArr = this.pendingOutputStreamOffsetsUs;
                this.outputStreamOffsetUs = jArr[0];
                int i2 = i - 1;
                this.pendingOutputStreamOffsetCount = i2;
                System.arraycopy(jArr, 1, jArr, 0, i2);
                long[] jArr2 = this.pendingOutputStreamSwitchTimesUs;
                System.arraycopy(jArr2, 1, jArr2, 0, this.pendingOutputStreamOffsetCount);
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldDropOutputBuffer(long earlyUs, long elapsedRealtimeUs) {
        return isBufferLate(earlyUs);
    }

    /* access modifiers changed from: protected */
    public boolean shouldDropBuffersToKeyframe(long earlyUs, long elapsedRealtimeUs) {
        return isBufferVeryLate(earlyUs);
    }

    /* access modifiers changed from: protected */
    public boolean shouldForceRenderOutputBuffer(long earlyUs, long elapsedSinceLastRenderUs) {
        return isBufferLate(earlyUs) && elapsedSinceLastRenderUs > 100000;
    }

    /* access modifiers changed from: protected */
    public void skipOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        TraceUtil.beginSection("skipVideoBuffer");
        codec.releaseOutputBuffer(index, false);
        TraceUtil.endSection();
        this.decoderCounters.skippedOutputBufferCount++;
    }

    /* access modifiers changed from: protected */
    public void dropOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        TraceUtil.beginSection("dropVideoBuffer");
        codec.releaseOutputBuffer(index, false);
        TraceUtil.endSection();
        updateDroppedBufferCounters(1);
    }

    /* access modifiers changed from: protected */
    public boolean maybeDropBuffersToKeyframe(MediaCodec codec, int index, long presentationTimeUs, long positionUs) throws ExoPlaybackException {
        int droppedSourceBufferCount = skipSource(positionUs);
        if (droppedSourceBufferCount == 0) {
            return false;
        }
        this.decoderCounters.droppedToKeyframeCount++;
        updateDroppedBufferCounters(this.buffersInCodecCount + droppedSourceBufferCount);
        flushOrReinitCodec();
        return true;
    }

    /* access modifiers changed from: protected */
    public void updateDroppedBufferCounters(int droppedBufferCount) {
        this.decoderCounters.droppedBufferCount += droppedBufferCount;
        this.droppedFrames += droppedBufferCount;
        this.consecutiveDroppedFrameCount += droppedBufferCount;
        this.decoderCounters.maxConsecutiveDroppedBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.decoderCounters.maxConsecutiveDroppedBufferCount);
        int i = this.maxDroppedFramesToNotify;
        if (i > 0 && this.droppedFrames >= i) {
            maybeNotifyDroppedFrames();
        }
    }

    /* access modifiers changed from: protected */
    public void renderOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(index, true);
        TraceUtil.endSection();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
        this.decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        maybeNotifyRenderedFirstFrame();
    }

    /* access modifiers changed from: protected */
    public void renderOutputBufferV21(MediaCodec codec, int index, long presentationTimeUs, long releaseTimeNs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(index, releaseTimeNs);
        TraceUtil.endSection();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
        this.decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        maybeNotifyRenderedFirstFrame();
    }

    private boolean shouldUseDummySurface(MediaCodecInfo codecInfo) {
        return Util.SDK_INT >= 23 && !this.tunneling && !codecNeedsSetOutputSurfaceWorkaround(codecInfo.name) && (!codecInfo.secure || DummySurface.isSecureSupported(this.context));
    }

    private void setJoiningDeadlineMs() {
        this.joiningDeadlineMs = this.allowedJoiningTimeMs > 0 ? SystemClock.elapsedRealtime() + this.allowedJoiningTimeMs : C.TIME_UNSET;
    }

    private void clearRenderedFirstFrame() {
        MediaCodec codec;
        this.renderedFirstFrame = false;
        if (Util.SDK_INT >= 23 && this.tunneling && (codec = getCodec()) != null) {
            this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(codec);
        }
    }

    /* access modifiers changed from: package-private */
    public void maybeNotifyRenderedFirstFrame() {
        if (!this.renderedFirstFrame) {
            this.renderedFirstFrame = true;
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void maybeRenotifyRenderedFirstFrame() {
        if (this.renderedFirstFrame) {
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void clearReportedVideoSize() {
        this.reportedWidth = -1;
        this.reportedHeight = -1;
        this.reportedPixelWidthHeightRatio = -1.0f;
        this.reportedUnappliedRotationDegrees = -1;
    }

    private void maybeNotifyVideoSizeChanged() {
        if (this.currentWidth != -1 || this.currentHeight != -1) {
            if (this.reportedWidth != this.currentWidth || this.reportedHeight != this.currentHeight || this.reportedUnappliedRotationDegrees != this.currentUnappliedRotationDegrees || this.reportedPixelWidthHeightRatio != this.currentPixelWidthHeightRatio) {
                this.eventDispatcher.videoSizeChanged(this.currentWidth, this.currentHeight, this.currentUnappliedRotationDegrees, this.currentPixelWidthHeightRatio);
                this.reportedWidth = this.currentWidth;
                this.reportedHeight = this.currentHeight;
                this.reportedUnappliedRotationDegrees = this.currentUnappliedRotationDegrees;
                this.reportedPixelWidthHeightRatio = this.currentPixelWidthHeightRatio;
            }
        }
    }

    private void maybeRenotifyVideoSizeChanged() {
        if (this.reportedWidth != -1 || this.reportedHeight != -1) {
            this.eventDispatcher.videoSizeChanged(this.reportedWidth, this.reportedHeight, this.reportedUnappliedRotationDegrees, this.reportedPixelWidthHeightRatio);
        }
    }

    private void maybeNotifyDroppedFrames() {
        if (this.droppedFrames > 0) {
            long now = SystemClock.elapsedRealtime();
            this.eventDispatcher.droppedFrames(this.droppedFrames, now - this.droppedFrameAccumulationStartTimeMs);
            this.droppedFrames = 0;
            this.droppedFrameAccumulationStartTimeMs = now;
        }
    }

    private static boolean isBufferLate(long earlyUs) {
        return earlyUs < -30000;
    }

    private static boolean isBufferVeryLate(long earlyUs) {
        return earlyUs < -500000;
    }

    private static void setOutputSurfaceV23(MediaCodec codec, Surface surface2) {
        codec.setOutputSurface(surface2);
    }

    private static void configureTunnelingV21(MediaFormat mediaFormat, int tunnelingAudioSessionId2) {
        mediaFormat.setFeatureEnabled("tunneled-playback", true);
        mediaFormat.setInteger("audio-session-id", tunnelingAudioSessionId2);
    }

    /* access modifiers changed from: protected */
    public MediaFormat getMediaFormat(Format format, CodecMaxValues codecMaxValues2, float codecOperatingRate, boolean deviceNeedsNoPostProcessWorkaround2, int tunnelingAudioSessionId2) {
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString("mime", format.sampleMimeType);
        mediaFormat.setInteger("width", format.width);
        mediaFormat.setInteger("height", format.height);
        MediaFormatUtil.setCsdBuffers(mediaFormat, format.initializationData);
        MediaFormatUtil.maybeSetFloat(mediaFormat, "frame-rate", format.frameRate);
        MediaFormatUtil.maybeSetInteger(mediaFormat, "rotation-degrees", format.rotationDegrees);
        MediaFormatUtil.maybeSetColorInfo(mediaFormat, format.colorInfo);
        mediaFormat.setInteger("max-width", codecMaxValues2.width);
        mediaFormat.setInteger("max-height", codecMaxValues2.height);
        MediaFormatUtil.maybeSetInteger(mediaFormat, "max-input-size", codecMaxValues2.inputSize);
        if (Util.SDK_INT >= 23) {
            mediaFormat.setInteger("priority", 0);
            if (codecOperatingRate != -1.0f) {
                mediaFormat.setFloat("operating-rate", codecOperatingRate);
            }
        }
        if (deviceNeedsNoPostProcessWorkaround2) {
            mediaFormat.setInteger("no-post-process", 1);
            mediaFormat.setInteger("auto-frc", 0);
        }
        if (tunnelingAudioSessionId2 != 0) {
            configureTunnelingV21(mediaFormat, tunnelingAudioSessionId2);
        }
        return mediaFormat;
    }

    /* access modifiers changed from: protected */
    public CodecMaxValues getCodecMaxValues(MediaCodecInfo codecInfo, Format format, Format[] streamFormats) throws MediaCodecUtil.DecoderQueryException {
        int codecMaxInputSize;
        int maxWidth = format.width;
        int maxHeight = format.height;
        int maxInputSize = getMaxInputSize(codecInfo, format);
        if (streamFormats.length == 1) {
            if (!(maxInputSize == -1 || (codecMaxInputSize = getCodecMaxInputSize(codecInfo, format.sampleMimeType, format.width, format.height)) == -1)) {
                maxInputSize = Math.min((int) (((float) maxInputSize) * INITIAL_FORMAT_MAX_INPUT_SIZE_SCALE_FACTOR), codecMaxInputSize);
            }
            return new CodecMaxValues(maxWidth, maxHeight, maxInputSize);
        }
        boolean haveUnknownDimensions = false;
        for (Format streamFormat : streamFormats) {
            if (codecInfo.isSeamlessAdaptationSupported(format, streamFormat, false)) {
                haveUnknownDimensions |= streamFormat.width == -1 || streamFormat.height == -1;
                maxWidth = Math.max(maxWidth, streamFormat.width);
                maxHeight = Math.max(maxHeight, streamFormat.height);
                maxInputSize = Math.max(maxInputSize, getMaxInputSize(codecInfo, streamFormat));
            }
        }
        if (haveUnknownDimensions) {
            Log.w(TAG, "Resolutions unknown. Codec max resolution: " + maxWidth + "x" + maxHeight);
            Point codecMaxSize = getCodecMaxSize(codecInfo, format);
            if (codecMaxSize != null) {
                maxWidth = Math.max(maxWidth, codecMaxSize.x);
                maxHeight = Math.max(maxHeight, codecMaxSize.y);
                maxInputSize = Math.max(maxInputSize, getCodecMaxInputSize(codecInfo, format.sampleMimeType, maxWidth, maxHeight));
                Log.w(TAG, "Codec max resolution adjusted to: " + maxWidth + "x" + maxHeight);
            }
        }
        return new CodecMaxValues(maxWidth, maxHeight, maxInputSize);
    }

    private static Point getCodecMaxSize(MediaCodecInfo codecInfo, Format format) throws MediaCodecUtil.DecoderQueryException {
        float aspectRatio;
        int formatShortEdgePx;
        MediaCodecInfo mediaCodecInfo = codecInfo;
        Format format2 = format;
        int i = 0;
        boolean isVerticalVideo = format2.height > format2.width;
        int formatLongEdgePx = isVerticalVideo ? format2.height : format2.width;
        int formatShortEdgePx2 = isVerticalVideo ? format2.width : format2.height;
        float aspectRatio2 = ((float) formatShortEdgePx2) / ((float) formatLongEdgePx);
        int[] iArr = STANDARD_LONG_EDGE_VIDEO_PX;
        int length = iArr.length;
        while (i < length) {
            int longEdgePx = iArr[i];
            int shortEdgePx = (int) (((float) longEdgePx) * aspectRatio2);
            if (longEdgePx <= formatLongEdgePx) {
                float f = aspectRatio2;
            } else if (shortEdgePx <= formatShortEdgePx2) {
                int i2 = formatShortEdgePx2;
                float f2 = aspectRatio2;
            } else {
                if (Util.SDK_INT >= 21) {
                    Point alignedSize = mediaCodecInfo.alignVideoSizeV21(isVerticalVideo ? shortEdgePx : longEdgePx, isVerticalVideo ? longEdgePx : shortEdgePx);
                    formatShortEdgePx = formatShortEdgePx2;
                    aspectRatio = aspectRatio2;
                    if (mediaCodecInfo.isVideoSizeAndRateSupportedV21(alignedSize.x, alignedSize.y, (double) format2.frameRate)) {
                        return alignedSize;
                    }
                } else {
                    formatShortEdgePx = formatShortEdgePx2;
                    aspectRatio = aspectRatio2;
                    int longEdgePx2 = Util.ceilDivide(longEdgePx, 16) * 16;
                    int shortEdgePx2 = Util.ceilDivide(shortEdgePx, 16) * 16;
                    if (longEdgePx2 * shortEdgePx2 <= MediaCodecUtil.maxH264DecodableFrameSize()) {
                        return new Point(isVerticalVideo ? shortEdgePx2 : longEdgePx2, isVerticalVideo ? longEdgePx2 : shortEdgePx2);
                    }
                }
                i++;
                formatShortEdgePx2 = formatShortEdgePx;
                aspectRatio2 = aspectRatio;
            }
            return null;
        }
        return null;
    }

    private static int getMaxInputSize(MediaCodecInfo codecInfo, Format format) {
        if (format.maxInputSize == -1) {
            return getCodecMaxInputSize(codecInfo, format.sampleMimeType, format.width, format.height);
        }
        int totalInitializationDataSize = 0;
        int initializationDataCount = format.initializationData.size();
        for (int i = 0; i < initializationDataCount; i++) {
            totalInitializationDataSize += format.initializationData.get(i).length;
        }
        return format.maxInputSize + totalInitializationDataSize;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getCodecMaxInputSize(com.google.android.exoplayer2.mediacodec.MediaCodecInfo r7, java.lang.String r8, int r9, int r10) {
        /*
            r0 = -1
            if (r9 == r0) goto L_0x00b6
            if (r10 != r0) goto L_0x0007
            goto L_0x00b6
        L_0x0007:
            int r1 = r8.hashCode()
            r2 = 5
            r3 = 4
            r4 = 1
            r5 = 3
            r6 = 2
            switch(r1) {
                case -1664118616: goto L_0x004b;
                case -1662541442: goto L_0x0040;
                case 1187890754: goto L_0x0035;
                case 1331836730: goto L_0x002a;
                case 1599127256: goto L_0x001f;
                case 1599127257: goto L_0x0014;
                default: goto L_0x0013;
            }
        L_0x0013:
            goto L_0x0056
        L_0x0014:
            java.lang.String r1 = "video/x-vnd.on2.vp9"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x0013
            r1 = 5
            goto L_0x0057
        L_0x001f:
            java.lang.String r1 = "video/x-vnd.on2.vp8"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x0013
            r1 = 3
            goto L_0x0057
        L_0x002a:
            java.lang.String r1 = "video/avc"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x0013
            r1 = 2
            goto L_0x0057
        L_0x0035:
            java.lang.String r1 = "video/mp4v-es"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x0013
            r1 = 1
            goto L_0x0057
        L_0x0040:
            java.lang.String r1 = "video/hevc"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x0013
            r1 = 4
            goto L_0x0057
        L_0x004b:
            java.lang.String r1 = "video/3gpp"
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x0013
            r1 = 0
            goto L_0x0057
        L_0x0056:
            r1 = -1
        L_0x0057:
            if (r1 == 0) goto L_0x00ac
            if (r1 == r4) goto L_0x00ac
            if (r1 == r6) goto L_0x006c
            if (r1 == r5) goto L_0x0068
            if (r1 == r3) goto L_0x0064
            if (r1 == r2) goto L_0x0064
            return r0
        L_0x0064:
            int r0 = r9 * r10
            r1 = 4
            goto L_0x00b0
        L_0x0068:
            int r0 = r9 * r10
            r1 = 2
            goto L_0x00b0
        L_0x006c:
            java.lang.String r1 = com.google.android.exoplayer2.util.Util.MODEL
            java.lang.String r2 = "BRAVIA 4K 2015"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x00ab
            java.lang.String r1 = com.google.android.exoplayer2.util.Util.MANUFACTURER
            java.lang.String r2 = "Amazon"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0099
            java.lang.String r1 = com.google.android.exoplayer2.util.Util.MODEL
            java.lang.String r2 = "KFSOWI"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x00ab
            java.lang.String r1 = com.google.android.exoplayer2.util.Util.MODEL
            java.lang.String r2 = "AFTS"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0099
            boolean r1 = r7.secure
            if (r1 == 0) goto L_0x0099
            goto L_0x00ab
        L_0x0099:
            r0 = 16
            int r1 = com.google.android.exoplayer2.util.Util.ceilDivide((int) r9, (int) r0)
            int r2 = com.google.android.exoplayer2.util.Util.ceilDivide((int) r10, (int) r0)
            int r1 = r1 * r2
            int r1 = r1 * 16
            int r0 = r1 * 16
            r1 = 2
            goto L_0x00b0
        L_0x00ab:
            return r0
        L_0x00ac:
            int r0 = r9 * r10
            r1 = 2
        L_0x00b0:
            int r2 = r0 * 3
            int r3 = r1 * 2
            int r2 = r2 / r3
            return r2
        L_0x00b6:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.video.MediaCodecVideoRenderer.getCodecMaxInputSize(com.google.android.exoplayer2.mediacodec.MediaCodecInfo, java.lang.String, int, int):int");
    }

    private static boolean deviceNeedsNoPostProcessWorkaround() {
        return "NVIDIA".equals(Util.MANUFACTURER);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:408:0x062b A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean codecNeedsSetOutputSurfaceWorkaround(java.lang.String r8) {
        /*
            r7 = this;
            java.lang.String r0 = "OMX.google"
            boolean r0 = r8.startsWith(r0)
            r1 = 0
            if (r0 == 0) goto L_0x000a
            return r1
        L_0x000a:
            java.lang.Class<com.google.android.exoplayer2.video.MediaCodecVideoRenderer> r0 = com.google.android.exoplayer2.video.MediaCodecVideoRenderer.class
            monitor-enter(r0)
            boolean r2 = evaluatedDeviceNeedsSetOutputSurfaceWorkaround     // Catch:{ all -> 0x0637 }
            if (r2 != 0) goto L_0x0633
            int r2 = com.google.android.exoplayer2.util.Util.SDK_INT     // Catch:{ all -> 0x0637 }
            r3 = 27
            r4 = 1
            if (r2 > r3) goto L_0x0026
            java.lang.String r2 = "dangal"
            java.lang.String r5 = com.google.android.exoplayer2.util.Util.DEVICE     // Catch:{ all -> 0x0637 }
            boolean r2 = r2.equals(r5)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0026
            deviceNeedsSetOutputSurfaceWorkaround = r4     // Catch:{ all -> 0x0637 }
            goto L_0x0631
        L_0x0026:
            int r2 = com.google.android.exoplayer2.util.Util.SDK_INT     // Catch:{ all -> 0x0637 }
            if (r2 < r3) goto L_0x002c
            goto L_0x0631
        L_0x002c:
            java.lang.String r2 = com.google.android.exoplayer2.util.Util.DEVICE     // Catch:{ all -> 0x0637 }
            int r5 = r2.hashCode()     // Catch:{ all -> 0x0637 }
            r6 = -1
            switch(r5) {
                case -2144781245: goto L_0x05f1;
                case -2144781185: goto L_0x05e6;
                case -2144781160: goto L_0x05db;
                case -2097309513: goto L_0x05d0;
                case -2022874474: goto L_0x05c5;
                case -1978993182: goto L_0x05ba;
                case -1978990237: goto L_0x05af;
                case -1936688988: goto L_0x05a4;
                case -1936688066: goto L_0x0599;
                case -1936688065: goto L_0x058d;
                case -1931988508: goto L_0x0581;
                case -1696512866: goto L_0x0575;
                case -1680025915: goto L_0x0569;
                case -1615810839: goto L_0x055d;
                case -1554255044: goto L_0x0550;
                case -1481772737: goto L_0x0544;
                case -1481772730: goto L_0x0538;
                case -1481772729: goto L_0x052c;
                case -1320080169: goto L_0x0520;
                case -1217592143: goto L_0x0514;
                case -1180384755: goto L_0x0508;
                case -1139198265: goto L_0x04fc;
                case -1052835013: goto L_0x04f0;
                case -993250464: goto L_0x04e5;
                case -965403638: goto L_0x04d9;
                case -958336948: goto L_0x04cd;
                case -879245230: goto L_0x04c0;
                case -842500323: goto L_0x04b4;
                case -821392978: goto L_0x04a9;
                case -797483286: goto L_0x049d;
                case -794946968: goto L_0x0490;
                case -788334647: goto L_0x0483;
                case -782144577: goto L_0x0477;
                case -575125681: goto L_0x046b;
                case -521118391: goto L_0x045f;
                case -430914369: goto L_0x0453;
                case -290434366: goto L_0x0446;
                case -282781963: goto L_0x043a;
                case -277133239: goto L_0x042e;
                case -173639913: goto L_0x0422;
                case -56598463: goto L_0x0415;
                case 2126: goto L_0x0409;
                case 2564: goto L_0x03fd;
                case 2715: goto L_0x03f1;
                case 2719: goto L_0x03e5;
                case 3483: goto L_0x03d9;
                case 73405: goto L_0x03cd;
                case 75739: goto L_0x03c1;
                case 76779: goto L_0x03b5;
                case 78669: goto L_0x03a9;
                case 79305: goto L_0x039d;
                case 80618: goto L_0x0391;
                case 88274: goto L_0x0385;
                case 98846: goto L_0x0379;
                case 98848: goto L_0x036d;
                case 99329: goto L_0x0361;
                case 101481: goto L_0x0355;
                case 1513190: goto L_0x034a;
                case 1514184: goto L_0x033f;
                case 1514185: goto L_0x0334;
                case 2436959: goto L_0x0328;
                case 2463773: goto L_0x031c;
                case 2464648: goto L_0x0310;
                case 2689555: goto L_0x0304;
                case 3154429: goto L_0x02f8;
                case 3284551: goto L_0x02ec;
                case 3351335: goto L_0x02e0;
                case 3386211: goto L_0x02d4;
                case 41325051: goto L_0x02c8;
                case 55178625: goto L_0x02bc;
                case 61542055: goto L_0x02b1;
                case 65355429: goto L_0x02a5;
                case 66214468: goto L_0x0299;
                case 66214470: goto L_0x028d;
                case 66214473: goto L_0x0281;
                case 66215429: goto L_0x0275;
                case 66215431: goto L_0x0269;
                case 66215433: goto L_0x025d;
                case 66216390: goto L_0x0251;
                case 76402249: goto L_0x0245;
                case 76404105: goto L_0x0239;
                case 76404911: goto L_0x022d;
                case 80963634: goto L_0x0221;
                case 82882791: goto L_0x0215;
                case 98715550: goto L_0x0209;
                case 102844228: goto L_0x01fd;
                case 165221241: goto L_0x01f2;
                case 182191441: goto L_0x01e6;
                case 245388979: goto L_0x01da;
                case 287431619: goto L_0x01ce;
                case 307593612: goto L_0x01c2;
                case 308517133: goto L_0x01b6;
                case 316215098: goto L_0x01aa;
                case 316215116: goto L_0x019e;
                case 316246811: goto L_0x0192;
                case 316246818: goto L_0x0186;
                case 407160593: goto L_0x017a;
                case 507412548: goto L_0x016e;
                case 793982701: goto L_0x0162;
                case 794038622: goto L_0x0156;
                case 794040393: goto L_0x014a;
                case 835649806: goto L_0x013e;
                case 917340916: goto L_0x0133;
                case 958008161: goto L_0x0127;
                case 1060579533: goto L_0x011b;
                case 1150207623: goto L_0x010f;
                case 1176899427: goto L_0x0103;
                case 1280332038: goto L_0x00f7;
                case 1306947716: goto L_0x00eb;
                case 1349174697: goto L_0x00df;
                case 1522194893: goto L_0x00d2;
                case 1691543273: goto L_0x00c6;
                case 1709443163: goto L_0x00ba;
                case 1865889110: goto L_0x00ae;
                case 1906253259: goto L_0x00a2;
                case 1977196784: goto L_0x0096;
                case 2006372676: goto L_0x008a;
                case 2029784656: goto L_0x007e;
                case 2030379515: goto L_0x0072;
                case 2033393791: goto L_0x0066;
                case 2047190025: goto L_0x005a;
                case 2047252157: goto L_0x0050;
                case 2048319463: goto L_0x0044;
                case 2048855701: goto L_0x0038;
                default: goto L_0x0036;
            }     // Catch:{ all -> 0x0637 }
        L_0x0036:
            goto L_0x05fc
        L_0x0038:
            java.lang.String r3 = "HWWAS-H"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 54
            goto L_0x05fd
        L_0x0044:
            java.lang.String r3 = "HWVNS-H"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 53
            goto L_0x05fd
        L_0x0050:
            java.lang.String r5 = "ELUGA_Prim"
            boolean r2 = r2.equals(r5)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            goto L_0x05fd
        L_0x005a:
            java.lang.String r3 = "ELUGA_Note"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 26
            goto L_0x05fd
        L_0x0066:
            java.lang.String r3 = "ASUS_X00AD_2"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 11
            goto L_0x05fd
        L_0x0072:
            java.lang.String r3 = "HWCAM-H"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 52
            goto L_0x05fd
        L_0x007e:
            java.lang.String r3 = "HWBLN-H"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 51
            goto L_0x05fd
        L_0x008a:
            java.lang.String r3 = "BRAVIA_ATV3_4K"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 15
            goto L_0x05fd
        L_0x0096:
            java.lang.String r3 = "Infinix-X572"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 57
            goto L_0x05fd
        L_0x00a2:
            java.lang.String r3 = "PB2-670M"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 85
            goto L_0x05fd
        L_0x00ae:
            java.lang.String r3 = "santoni"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 101(0x65, float:1.42E-43)
            goto L_0x05fd
        L_0x00ba:
            java.lang.String r3 = "iball8735_9806"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 56
            goto L_0x05fd
        L_0x00c6:
            java.lang.String r3 = "CPH1609"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 19
            goto L_0x05fd
        L_0x00d2:
            java.lang.String r3 = "woods_f"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 117(0x75, float:1.64E-43)
            goto L_0x05fd
        L_0x00df:
            java.lang.String r3 = "htc_e56ml_dtul"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 49
            goto L_0x05fd
        L_0x00eb:
            java.lang.String r3 = "EverStar_S"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 29
            goto L_0x05fd
        L_0x00f7:
            java.lang.String r3 = "hwALE-H"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 50
            goto L_0x05fd
        L_0x0103:
            java.lang.String r3 = "itel_S41"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 59
            goto L_0x05fd
        L_0x010f:
            java.lang.String r3 = "LS-5017"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 65
            goto L_0x05fd
        L_0x011b:
            java.lang.String r3 = "panell_d"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 81
            goto L_0x05fd
        L_0x0127:
            java.lang.String r3 = "j2xlteins"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 60
            goto L_0x05fd
        L_0x0133:
            java.lang.String r3 = "A7000plus"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 7
            goto L_0x05fd
        L_0x013e:
            java.lang.String r3 = "manning"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 67
            goto L_0x05fd
        L_0x014a:
            java.lang.String r3 = "GIONEE_WBL7519"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 47
            goto L_0x05fd
        L_0x0156:
            java.lang.String r3 = "GIONEE_WBL7365"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 46
            goto L_0x05fd
        L_0x0162:
            java.lang.String r3 = "GIONEE_WBL5708"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 45
            goto L_0x05fd
        L_0x016e:
            java.lang.String r3 = "QM16XE_U"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 99
            goto L_0x05fd
        L_0x017a:
            java.lang.String r3 = "Pixi5-10_4G"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 91
            goto L_0x05fd
        L_0x0186:
            java.lang.String r3 = "TB3-850M"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 109(0x6d, float:1.53E-43)
            goto L_0x05fd
        L_0x0192:
            java.lang.String r3 = "TB3-850F"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 108(0x6c, float:1.51E-43)
            goto L_0x05fd
        L_0x019e:
            java.lang.String r3 = "TB3-730X"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 107(0x6b, float:1.5E-43)
            goto L_0x05fd
        L_0x01aa:
            java.lang.String r3 = "TB3-730F"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 106(0x6a, float:1.49E-43)
            goto L_0x05fd
        L_0x01b6:
            java.lang.String r3 = "A7020a48"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 9
            goto L_0x05fd
        L_0x01c2:
            java.lang.String r3 = "A7010a48"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 8
            goto L_0x05fd
        L_0x01ce:
            java.lang.String r3 = "griffin"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 48
            goto L_0x05fd
        L_0x01da:
            java.lang.String r3 = "marino_f"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 68
            goto L_0x05fd
        L_0x01e6:
            java.lang.String r3 = "CPY83_I00"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 20
            goto L_0x05fd
        L_0x01f2:
            java.lang.String r3 = "A2016a40"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 5
            goto L_0x05fd
        L_0x01fd:
            java.lang.String r3 = "le_x6"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 64
            goto L_0x05fd
        L_0x0209:
            java.lang.String r3 = "i9031"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 55
            goto L_0x05fd
        L_0x0215:
            java.lang.String r3 = "X3_HK"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 119(0x77, float:1.67E-43)
            goto L_0x05fd
        L_0x0221:
            java.lang.String r3 = "V23GB"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 112(0x70, float:1.57E-43)
            goto L_0x05fd
        L_0x022d:
            java.lang.String r3 = "Q4310"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 97
            goto L_0x05fd
        L_0x0239:
            java.lang.String r3 = "Q4260"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 95
            goto L_0x05fd
        L_0x0245:
            java.lang.String r3 = "PRO7S"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 93
            goto L_0x05fd
        L_0x0251:
            java.lang.String r3 = "F3311"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 36
            goto L_0x05fd
        L_0x025d:
            java.lang.String r3 = "F3215"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 35
            goto L_0x05fd
        L_0x0269:
            java.lang.String r3 = "F3213"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 34
            goto L_0x05fd
        L_0x0275:
            java.lang.String r3 = "F3211"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 33
            goto L_0x05fd
        L_0x0281:
            java.lang.String r3 = "F3116"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 32
            goto L_0x05fd
        L_0x028d:
            java.lang.String r3 = "F3113"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 31
            goto L_0x05fd
        L_0x0299:
            java.lang.String r3 = "F3111"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 30
            goto L_0x05fd
        L_0x02a5:
            java.lang.String r3 = "E5643"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 24
            goto L_0x05fd
        L_0x02b1:
            java.lang.String r3 = "A1601"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 4
            goto L_0x05fd
        L_0x02bc:
            java.lang.String r3 = "Aura_Note_2"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 12
            goto L_0x05fd
        L_0x02c8:
            java.lang.String r3 = "MEIZU_M5"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 69
            goto L_0x05fd
        L_0x02d4:
            java.lang.String r3 = "p212"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 78
            goto L_0x05fd
        L_0x02e0:
            java.lang.String r3 = "mido"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 71
            goto L_0x05fd
        L_0x02ec:
            java.lang.String r3 = "kate"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 63
            goto L_0x05fd
        L_0x02f8:
            java.lang.String r3 = "fugu"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 38
            goto L_0x05fd
        L_0x0304:
            java.lang.String r3 = "XE2X"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 120(0x78, float:1.68E-43)
            goto L_0x05fd
        L_0x0310:
            java.lang.String r3 = "Q427"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 96
            goto L_0x05fd
        L_0x031c:
            java.lang.String r3 = "Q350"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 94
            goto L_0x05fd
        L_0x0328:
            java.lang.String r3 = "P681"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 79
            goto L_0x05fd
        L_0x0334:
            java.lang.String r3 = "1714"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 2
            goto L_0x05fd
        L_0x033f:
            java.lang.String r3 = "1713"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 1
            goto L_0x05fd
        L_0x034a:
            java.lang.String r3 = "1601"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 0
            goto L_0x05fd
        L_0x0355:
            java.lang.String r3 = "flo"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 37
            goto L_0x05fd
        L_0x0361:
            java.lang.String r3 = "deb"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 23
            goto L_0x05fd
        L_0x036d:
            java.lang.String r3 = "cv3"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 22
            goto L_0x05fd
        L_0x0379:
            java.lang.String r3 = "cv1"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 21
            goto L_0x05fd
        L_0x0385:
            java.lang.String r3 = "Z80"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 123(0x7b, float:1.72E-43)
            goto L_0x05fd
        L_0x0391:
            java.lang.String r3 = "QX1"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 100
            goto L_0x05fd
        L_0x039d:
            java.lang.String r3 = "PLE"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 92
            goto L_0x05fd
        L_0x03a9:
            java.lang.String r3 = "P85"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 80
            goto L_0x05fd
        L_0x03b5:
            java.lang.String r3 = "MX6"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 72
            goto L_0x05fd
        L_0x03c1:
            java.lang.String r3 = "M5c"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 66
            goto L_0x05fd
        L_0x03cd:
            java.lang.String r3 = "JGZ"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 61
            goto L_0x05fd
        L_0x03d9:
            java.lang.String r3 = "mh"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 70
            goto L_0x05fd
        L_0x03e5:
            java.lang.String r3 = "V5"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 113(0x71, float:1.58E-43)
            goto L_0x05fd
        L_0x03f1:
            java.lang.String r3 = "V1"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 111(0x6f, float:1.56E-43)
            goto L_0x05fd
        L_0x03fd:
            java.lang.String r3 = "Q5"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 98
            goto L_0x05fd
        L_0x0409:
            java.lang.String r3 = "C1"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 16
            goto L_0x05fd
        L_0x0415:
            java.lang.String r3 = "woods_fn"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 118(0x76, float:1.65E-43)
            goto L_0x05fd
        L_0x0422:
            java.lang.String r3 = "ELUGA_A3_Pro"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 25
            goto L_0x05fd
        L_0x042e:
            java.lang.String r3 = "Z12_PRO"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 122(0x7a, float:1.71E-43)
            goto L_0x05fd
        L_0x043a:
            java.lang.String r3 = "BLACK-1X"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 13
            goto L_0x05fd
        L_0x0446:
            java.lang.String r3 = "taido_row"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 105(0x69, float:1.47E-43)
            goto L_0x05fd
        L_0x0453:
            java.lang.String r3 = "Pixi4-7_3G"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 90
            goto L_0x05fd
        L_0x045f:
            java.lang.String r3 = "GIONEE_GBL7360"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 41
            goto L_0x05fd
        L_0x046b:
            java.lang.String r3 = "GiONEE_CBL7513"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 39
            goto L_0x05fd
        L_0x0477:
            java.lang.String r3 = "OnePlus5T"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 77
            goto L_0x05fd
        L_0x0483:
            java.lang.String r3 = "whyred"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 116(0x74, float:1.63E-43)
            goto L_0x05fd
        L_0x0490:
            java.lang.String r3 = "watson"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 115(0x73, float:1.61E-43)
            goto L_0x05fd
        L_0x049d:
            java.lang.String r3 = "SVP-DTV15"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 103(0x67, float:1.44E-43)
            goto L_0x05fd
        L_0x04a9:
            java.lang.String r3 = "A7000-a"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 6
            goto L_0x05fd
        L_0x04b4:
            java.lang.String r3 = "nicklaus_f"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 74
            goto L_0x05fd
        L_0x04c0:
            java.lang.String r3 = "tcl_eu"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 110(0x6e, float:1.54E-43)
            goto L_0x05fd
        L_0x04cd:
            java.lang.String r3 = "ELUGA_Ray_X"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 28
            goto L_0x05fd
        L_0x04d9:
            java.lang.String r3 = "s905x018"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 104(0x68, float:1.46E-43)
            goto L_0x05fd
        L_0x04e5:
            java.lang.String r3 = "A10-70F"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 3
            goto L_0x05fd
        L_0x04f0:
            java.lang.String r3 = "namath"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 73
            goto L_0x05fd
        L_0x04fc:
            java.lang.String r3 = "Slate_Pro"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 102(0x66, float:1.43E-43)
            goto L_0x05fd
        L_0x0508:
            java.lang.String r3 = "iris60"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 58
            goto L_0x05fd
        L_0x0514:
            java.lang.String r3 = "BRAVIA_ATV2"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 14
            goto L_0x05fd
        L_0x0520:
            java.lang.String r3 = "GiONEE_GBL7319"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 40
            goto L_0x05fd
        L_0x052c:
            java.lang.String r3 = "panell_dt"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 84
            goto L_0x05fd
        L_0x0538:
            java.lang.String r3 = "panell_ds"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 83
            goto L_0x05fd
        L_0x0544:
            java.lang.String r3 = "panell_dl"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 82
            goto L_0x05fd
        L_0x0550:
            java.lang.String r3 = "vernee_M5"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 114(0x72, float:1.6E-43)
            goto L_0x05fd
        L_0x055d:
            java.lang.String r3 = "Phantom6"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 89
            goto L_0x05fd
        L_0x0569:
            java.lang.String r3 = "ComioS1"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 17
            goto L_0x05fd
        L_0x0575:
            java.lang.String r3 = "XT1663"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 121(0x79, float:1.7E-43)
            goto L_0x05fd
        L_0x0581:
            java.lang.String r3 = "AquaPowerM"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 10
            goto L_0x05fd
        L_0x058d:
            java.lang.String r3 = "PGN611"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 88
            goto L_0x05fd
        L_0x0599:
            java.lang.String r3 = "PGN610"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 87
            goto L_0x05fd
        L_0x05a4:
            java.lang.String r3 = "PGN528"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 86
            goto L_0x05fd
        L_0x05af:
            java.lang.String r3 = "NX573J"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 76
            goto L_0x05fd
        L_0x05ba:
            java.lang.String r3 = "NX541J"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 75
            goto L_0x05fd
        L_0x05c5:
            java.lang.String r3 = "CP8676_I02"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 18
            goto L_0x05fd
        L_0x05d0:
            java.lang.String r3 = "K50a40"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 62
            goto L_0x05fd
        L_0x05db:
            java.lang.String r3 = "GIONEE_SWW1631"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 44
            goto L_0x05fd
        L_0x05e6:
            java.lang.String r3 = "GIONEE_SWW1627"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 43
            goto L_0x05fd
        L_0x05f1:
            java.lang.String r3 = "GIONEE_SWW1609"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0036
            r3 = 42
            goto L_0x05fd
        L_0x05fc:
            r3 = -1
        L_0x05fd:
            switch(r3) {
                case 0: goto L_0x0601;
                case 1: goto L_0x0601;
                case 2: goto L_0x0601;
                case 3: goto L_0x0601;
                case 4: goto L_0x0601;
                case 5: goto L_0x0601;
                case 6: goto L_0x0601;
                case 7: goto L_0x0601;
                case 8: goto L_0x0601;
                case 9: goto L_0x0601;
                case 10: goto L_0x0601;
                case 11: goto L_0x0601;
                case 12: goto L_0x0601;
                case 13: goto L_0x0601;
                case 14: goto L_0x0601;
                case 15: goto L_0x0601;
                case 16: goto L_0x0601;
                case 17: goto L_0x0601;
                case 18: goto L_0x0601;
                case 19: goto L_0x0601;
                case 20: goto L_0x0601;
                case 21: goto L_0x0601;
                case 22: goto L_0x0601;
                case 23: goto L_0x0601;
                case 24: goto L_0x0601;
                case 25: goto L_0x0601;
                case 26: goto L_0x0601;
                case 27: goto L_0x0601;
                case 28: goto L_0x0601;
                case 29: goto L_0x0601;
                case 30: goto L_0x0601;
                case 31: goto L_0x0601;
                case 32: goto L_0x0601;
                case 33: goto L_0x0601;
                case 34: goto L_0x0601;
                case 35: goto L_0x0601;
                case 36: goto L_0x0601;
                case 37: goto L_0x0601;
                case 38: goto L_0x0601;
                case 39: goto L_0x0601;
                case 40: goto L_0x0601;
                case 41: goto L_0x0601;
                case 42: goto L_0x0601;
                case 43: goto L_0x0601;
                case 44: goto L_0x0601;
                case 45: goto L_0x0601;
                case 46: goto L_0x0601;
                case 47: goto L_0x0601;
                case 48: goto L_0x0601;
                case 49: goto L_0x0601;
                case 50: goto L_0x0601;
                case 51: goto L_0x0601;
                case 52: goto L_0x0601;
                case 53: goto L_0x0601;
                case 54: goto L_0x0601;
                case 55: goto L_0x0601;
                case 56: goto L_0x0601;
                case 57: goto L_0x0601;
                case 58: goto L_0x0601;
                case 59: goto L_0x0601;
                case 60: goto L_0x0601;
                case 61: goto L_0x0601;
                case 62: goto L_0x0601;
                case 63: goto L_0x0601;
                case 64: goto L_0x0601;
                case 65: goto L_0x0601;
                case 66: goto L_0x0601;
                case 67: goto L_0x0601;
                case 68: goto L_0x0601;
                case 69: goto L_0x0601;
                case 70: goto L_0x0601;
                case 71: goto L_0x0601;
                case 72: goto L_0x0601;
                case 73: goto L_0x0601;
                case 74: goto L_0x0601;
                case 75: goto L_0x0601;
                case 76: goto L_0x0601;
                case 77: goto L_0x0601;
                case 78: goto L_0x0601;
                case 79: goto L_0x0601;
                case 80: goto L_0x0601;
                case 81: goto L_0x0601;
                case 82: goto L_0x0601;
                case 83: goto L_0x0601;
                case 84: goto L_0x0601;
                case 85: goto L_0x0601;
                case 86: goto L_0x0601;
                case 87: goto L_0x0601;
                case 88: goto L_0x0601;
                case 89: goto L_0x0601;
                case 90: goto L_0x0601;
                case 91: goto L_0x0601;
                case 92: goto L_0x0601;
                case 93: goto L_0x0601;
                case 94: goto L_0x0601;
                case 95: goto L_0x0601;
                case 96: goto L_0x0601;
                case 97: goto L_0x0601;
                case 98: goto L_0x0601;
                case 99: goto L_0x0601;
                case 100: goto L_0x0601;
                case 101: goto L_0x0601;
                case 102: goto L_0x0601;
                case 103: goto L_0x0601;
                case 104: goto L_0x0601;
                case 105: goto L_0x0601;
                case 106: goto L_0x0601;
                case 107: goto L_0x0601;
                case 108: goto L_0x0601;
                case 109: goto L_0x0601;
                case 110: goto L_0x0601;
                case 111: goto L_0x0601;
                case 112: goto L_0x0601;
                case 113: goto L_0x0601;
                case 114: goto L_0x0601;
                case 115: goto L_0x0601;
                case 116: goto L_0x0601;
                case 117: goto L_0x0601;
                case 118: goto L_0x0601;
                case 119: goto L_0x0601;
                case 120: goto L_0x0601;
                case 121: goto L_0x0601;
                case 122: goto L_0x0601;
                case 123: goto L_0x0601;
                default: goto L_0x0600;
            }     // Catch:{ all -> 0x0637 }
        L_0x0600:
            goto L_0x0604
        L_0x0601:
            deviceNeedsSetOutputSurfaceWorkaround = r4     // Catch:{ all -> 0x0637 }
        L_0x0604:
            java.lang.String r2 = com.google.android.exoplayer2.util.Util.MODEL     // Catch:{ all -> 0x0637 }
            int r3 = r2.hashCode()     // Catch:{ all -> 0x0637 }
            r5 = 2006354(0x1e9d52, float:2.811501E-39)
            if (r3 == r5) goto L_0x061f
            r1 = 2006367(0x1e9d5f, float:2.811519E-39)
            if (r3 == r1) goto L_0x0615
        L_0x0614:
            goto L_0x0628
        L_0x0615:
            java.lang.String r1 = "AFTN"
            boolean r1 = r2.equals(r1)     // Catch:{ all -> 0x0637 }
            if (r1 == 0) goto L_0x0614
            r1 = 1
            goto L_0x0629
        L_0x061f:
            java.lang.String r3 = "AFTA"
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x0637 }
            if (r2 == 0) goto L_0x0614
            goto L_0x0629
        L_0x0628:
            r1 = -1
        L_0x0629:
            if (r1 == 0) goto L_0x062e
            if (r1 == r4) goto L_0x062e
            goto L_0x0631
        L_0x062e:
            deviceNeedsSetOutputSurfaceWorkaround = r4     // Catch:{ all -> 0x0637 }
        L_0x0631:
            evaluatedDeviceNeedsSetOutputSurfaceWorkaround = r4     // Catch:{ all -> 0x0637 }
        L_0x0633:
            monitor-exit(r0)     // Catch:{ all -> 0x0637 }
            boolean r0 = deviceNeedsSetOutputSurfaceWorkaround
            return r0
        L_0x0637:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0637 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.video.MediaCodecVideoRenderer.codecNeedsSetOutputSurfaceWorkaround(java.lang.String):boolean");
    }

    protected static final class CodecMaxValues {
        public final int height;
        public final int inputSize;
        public final int width;

        public CodecMaxValues(int width2, int height2, int inputSize2) {
            this.width = width2;
            this.height = height2;
            this.inputSize = inputSize2;
        }
    }

    private final class OnFrameRenderedListenerV23 implements MediaCodec.OnFrameRenderedListener {
        private OnFrameRenderedListenerV23(MediaCodec codec) {
            codec.setOnFrameRenderedListener(this, new Handler());
        }

        public void onFrameRendered(MediaCodec codec, long presentationTimeUs, long nanoTime) {
            if (this == MediaCodecVideoRenderer.this.tunnelingOnFrameRenderedListener) {
                MediaCodecVideoRenderer.this.onProcessedTunneledBuffer(presentationTimeUs);
            }
        }
    }
}
