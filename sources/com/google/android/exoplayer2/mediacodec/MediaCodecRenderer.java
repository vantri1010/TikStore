package com.google.android.exoplayer2.mediacodec;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.TimedValueQueue;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public abstract class MediaCodecRenderer extends BaseRenderer {
    private static final byte[] ADAPTATION_WORKAROUND_BUFFER = Util.getBytesFromHexString("0000016742C00BDA259000000168CE0F13200000016588840DCE7118A0002FBF1C31C3275D78");
    private static final int ADAPTATION_WORKAROUND_MODE_ALWAYS = 2;
    private static final int ADAPTATION_WORKAROUND_MODE_NEVER = 0;
    private static final int ADAPTATION_WORKAROUND_MODE_SAME_RESOLUTION = 1;
    private static final int ADAPTATION_WORKAROUND_SLICE_WIDTH_HEIGHT = 32;
    protected static final float CODEC_OPERATING_RATE_UNSET = -1.0f;
    private static final int DRAIN_ACTION_FLUSH = 1;
    private static final int DRAIN_ACTION_NONE = 0;
    private static final int DRAIN_ACTION_REINITIALIZE = 2;
    private static final int DRAIN_STATE_NONE = 0;
    private static final int DRAIN_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int DRAIN_STATE_WAIT_END_OF_STREAM = 2;
    protected static final int KEEP_CODEC_RESULT_NO = 0;
    protected static final int KEEP_CODEC_RESULT_YES_WITHOUT_RECONFIGURATION = 3;
    protected static final int KEEP_CODEC_RESULT_YES_WITH_FLUSH = 1;
    protected static final int KEEP_CODEC_RESULT_YES_WITH_RECONFIGURATION = 2;
    private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000;
    private static final int RECONFIGURATION_STATE_NONE = 0;
    private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
    private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
    private static final String TAG = "MediaCodecRenderer";
    private final float assumedMinimumCodecOperatingRate;
    private ArrayDeque<MediaCodecInfo> availableCodecInfos;
    private final DecoderInputBuffer buffer;
    private MediaCodec codec;
    private int codecAdaptationWorkaroundMode;
    private int codecDrainAction;
    private int codecDrainState;
    private DrmSession<FrameworkMediaCrypto> codecDrmSession;
    private Format codecFormat;
    private long codecHotswapDeadlineMs;
    private MediaCodecInfo codecInfo;
    private boolean codecNeedsAdaptationWorkaroundBuffer;
    private boolean codecNeedsDiscardToSpsWorkaround;
    private boolean codecNeedsEosFlushWorkaround;
    private boolean codecNeedsEosOutputExceptionWorkaround;
    private boolean codecNeedsEosPropagation;
    private boolean codecNeedsFlushWorkaround;
    private boolean codecNeedsMonoChannelCountWorkaround;
    private boolean codecNeedsReconfigureWorkaround;
    private float codecOperatingRate;
    private boolean codecReceivedBuffers;
    private boolean codecReceivedEos;
    private int codecReconfigurationState;
    private boolean codecReconfigured;
    private final ArrayList<Long> decodeOnlyPresentationTimestamps;
    protected DecoderCounters decoderCounters;
    private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    private final DecoderInputBuffer flagsOnlyBuffer;
    private final FormatHolder formatHolder;
    private final TimedValueQueue<Format> formatQueue;
    private ByteBuffer[] inputBuffers;
    private Format inputFormat;
    private int inputIndex;
    private boolean inputStreamEnded;
    private final MediaCodecSelector mediaCodecSelector;
    private MediaCrypto mediaCrypto;
    private boolean mediaCryptoRequiresSecureDecoder;
    private ByteBuffer outputBuffer;
    private final MediaCodec.BufferInfo outputBufferInfo;
    private ByteBuffer[] outputBuffers;
    private Format outputFormat;
    private int outputIndex;
    private boolean outputStreamEnded;
    private final boolean playClearSamplesWithoutKeys;
    private DecoderInitializationException preferredDecoderInitializationException;
    private long renderTimeLimitMs;
    private float rendererOperatingRate;
    private boolean shouldSkipAdaptationWorkaroundOutputBuffer;
    private boolean shouldSkipOutputBuffer;
    private DrmSession<FrameworkMediaCrypto> sourceDrmSession;
    private boolean waitingForFirstSampleInFormat;
    private boolean waitingForFirstSyncSample;
    private boolean waitingForKeys;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    private @interface AdaptationWorkaroundMode {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    private @interface DrainAction {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    private @interface DrainState {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    protected @interface KeepCodecResult {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    private @interface ReconfigurationState {
    }

    /* access modifiers changed from: protected */
    public abstract void configureCodec(MediaCodecInfo mediaCodecInfo, MediaCodec mediaCodec, Format format, MediaCrypto mediaCrypto2, float f) throws MediaCodecUtil.DecoderQueryException;

    /* access modifiers changed from: protected */
    public abstract boolean processOutputBuffer(long j, long j2, MediaCodec mediaCodec, ByteBuffer byteBuffer, int i, int i2, long j3, boolean z, Format format) throws ExoPlaybackException;

    /* access modifiers changed from: protected */
    public abstract int supportsFormat(MediaCodecSelector mediaCodecSelector2, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager2, Format format) throws MediaCodecUtil.DecoderQueryException;

    public static class DecoderInitializationException extends Exception {
        private static final int CUSTOM_ERROR_CODE_BASE = -50000;
        private static final int DECODER_QUERY_ERROR = -49998;
        private static final int NO_SUITABLE_DECODER_ERROR = -49999;
        public final String decoderName;
        public final String diagnosticInfo;
        public final DecoderInitializationException fallbackDecoderInitializationException;
        public final String mimeType;
        public final boolean secureDecoderRequired;

        public DecoderInitializationException(Format format, Throwable cause, boolean secureDecoderRequired2, int errorCode) {
            this("Decoder init failed: [" + errorCode + "], " + format, cause, format.sampleMimeType, secureDecoderRequired2, (String) null, buildCustomDiagnosticInfo(errorCode), (DecoderInitializationException) null);
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public DecoderInitializationException(com.google.android.exoplayer2.Format r11, java.lang.Throwable r12, boolean r13, java.lang.String r14) {
            /*
                r10 = this;
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "Decoder init failed: "
                r0.append(r1)
                r0.append(r14)
                java.lang.String r1 = ", "
                r0.append(r1)
                r0.append(r11)
                java.lang.String r3 = r0.toString()
                java.lang.String r5 = r11.sampleMimeType
                int r0 = com.google.android.exoplayer2.util.Util.SDK_INT
                r1 = 21
                if (r0 < r1) goto L_0x0026
                java.lang.String r0 = getDiagnosticInfoV21(r12)
                goto L_0x0027
            L_0x0026:
                r0 = 0
            L_0x0027:
                r8 = r0
                r9 = 0
                r2 = r10
                r4 = r12
                r6 = r13
                r7 = r14
                r2.<init>(r3, r4, r5, r6, r7, r8, r9)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException.<init>(com.google.android.exoplayer2.Format, java.lang.Throwable, boolean, java.lang.String):void");
        }

        private DecoderInitializationException(String message, Throwable cause, String mimeType2, boolean secureDecoderRequired2, String decoderName2, String diagnosticInfo2, DecoderInitializationException fallbackDecoderInitializationException2) {
            super(message, cause);
            this.mimeType = mimeType2;
            this.secureDecoderRequired = secureDecoderRequired2;
            this.decoderName = decoderName2;
            this.diagnosticInfo = diagnosticInfo2;
            this.fallbackDecoderInitializationException = fallbackDecoderInitializationException2;
        }

        /* access modifiers changed from: private */
        public DecoderInitializationException copyWithFallbackException(DecoderInitializationException fallbackException) {
            return new DecoderInitializationException(getMessage(), getCause(), this.mimeType, this.secureDecoderRequired, this.decoderName, this.diagnosticInfo, fallbackException);
        }

        private static String getDiagnosticInfoV21(Throwable cause) {
            if (cause instanceof MediaCodec.CodecException) {
                return ((MediaCodec.CodecException) cause).getDiagnosticInfo();
            }
            return null;
        }

        private static String buildCustomDiagnosticInfo(int errorCode) {
            String sign = errorCode < 0 ? "neg_" : "";
            return "com.google.android.exoplayer.MediaCodecTrackRenderer_" + sign + Math.abs(errorCode);
        }
    }

    public MediaCodecRenderer(int trackType, MediaCodecSelector mediaCodecSelector2, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager2, boolean playClearSamplesWithoutKeys2, float assumedMinimumCodecOperatingRate2) {
        super(trackType);
        Assertions.checkState(Util.SDK_INT >= 16);
        this.mediaCodecSelector = (MediaCodecSelector) Assertions.checkNotNull(mediaCodecSelector2);
        this.drmSessionManager = drmSessionManager2;
        this.playClearSamplesWithoutKeys = playClearSamplesWithoutKeys2;
        this.assumedMinimumCodecOperatingRate = assumedMinimumCodecOperatingRate2;
        this.buffer = new DecoderInputBuffer(0);
        this.flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
        this.formatHolder = new FormatHolder();
        this.formatQueue = new TimedValueQueue<>();
        this.decodeOnlyPresentationTimestamps = new ArrayList<>();
        this.outputBufferInfo = new MediaCodec.BufferInfo();
        this.codecReconfigurationState = 0;
        this.codecDrainState = 0;
        this.codecDrainAction = 0;
        this.codecOperatingRate = -1.0f;
        this.rendererOperatingRate = 1.0f;
        this.renderTimeLimitMs = C.TIME_UNSET;
    }

    public void experimental_setRenderTimeLimitMs(long renderTimeLimitMs2) {
        this.renderTimeLimitMs = renderTimeLimitMs2;
    }

    public final int supportsMixedMimeTypeAdaptation() {
        return 8;
    }

    public final int supportsFormat(Format format) throws ExoPlaybackException {
        try {
            return supportsFormat(this.mediaCodecSelector, this.drmSessionManager, format);
        } catch (MediaCodecUtil.DecoderQueryException e) {
            throw ExoPlaybackException.createForRenderer(e, getIndex());
        }
    }

    /* access modifiers changed from: protected */
    public List<MediaCodecInfo> getDecoderInfos(MediaCodecSelector mediaCodecSelector2, Format format, boolean requiresSecureDecoder) throws MediaCodecUtil.DecoderQueryException {
        return mediaCodecSelector2.getDecoderInfos(format.sampleMimeType, requiresSecureDecoder);
    }

    /* access modifiers changed from: protected */
    public final void maybeInitCodec() throws ExoPlaybackException {
        if (this.codec == null && this.inputFormat != null) {
            setCodecDrmSession(this.sourceDrmSession);
            String mimeType = this.inputFormat.sampleMimeType;
            DrmSession<FrameworkMediaCrypto> drmSession = this.codecDrmSession;
            if (drmSession != null) {
                if (this.mediaCrypto == null) {
                    FrameworkMediaCrypto sessionMediaCrypto = drmSession.getMediaCrypto();
                    if (sessionMediaCrypto != null) {
                        try {
                            this.mediaCrypto = new MediaCrypto(sessionMediaCrypto.uuid, sessionMediaCrypto.sessionId);
                            this.mediaCryptoRequiresSecureDecoder = !sessionMediaCrypto.forceAllowInsecureDecoderComponents && this.mediaCrypto.requiresSecureDecoderComponent(mimeType);
                        } catch (MediaCryptoException e) {
                            throw ExoPlaybackException.createForRenderer(e, getIndex());
                        }
                    } else if (this.codecDrmSession.getError() == null) {
                        return;
                    }
                }
                if (deviceNeedsDrmKeysToConfigureCodecWorkaround()) {
                    int drmSessionState = this.codecDrmSession.getState();
                    if (drmSessionState == 1) {
                        throw ExoPlaybackException.createForRenderer(this.codecDrmSession.getError(), getIndex());
                    } else if (drmSessionState != 4) {
                        return;
                    }
                }
            }
            try {
                maybeInitCodecWithFallback(this.mediaCrypto, this.mediaCryptoRequiresSecureDecoder);
            } catch (DecoderInitializationException e2) {
                throw ExoPlaybackException.createForRenderer(e2, getIndex());
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldInitCodec(MediaCodecInfo codecInfo2) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean getCodecNeedsEosPropagation() {
        return false;
    }

    /* access modifiers changed from: protected */
    public final Format updateOutputFormatForTime(long presentationTimeUs) {
        Format format = this.formatQueue.pollFloor(presentationTimeUs);
        if (format != null) {
            this.outputFormat = format;
        }
        return format;
    }

    /* access modifiers changed from: protected */
    public final MediaCodec getCodec() {
        return this.codec;
    }

    /* access modifiers changed from: protected */
    public final MediaCodecInfo getCodecInfo() {
        return this.codecInfo;
    }

    /* access modifiers changed from: protected */
    public void onEnabled(boolean joining) throws ExoPlaybackException {
        this.decoderCounters = new DecoderCounters();
    }

    /* access modifiers changed from: protected */
    public void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        flushOrReinitCodec();
        this.formatQueue.clear();
    }

    public final void setOperatingRate(float operatingRate) throws ExoPlaybackException {
        this.rendererOperatingRate = operatingRate;
        if (this.codec != null && this.codecDrainAction != 2) {
            updateCodecOperatingRate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDisabled() {
        this.inputFormat = null;
        if (this.sourceDrmSession == null && this.codecDrmSession == null) {
            flushOrReleaseCodec();
        } else {
            onReset();
        }
    }

    /* access modifiers changed from: protected */
    public void onReset() {
        try {
            releaseCodec();
        } finally {
            setSourceDrmSession((DrmSession<FrameworkMediaCrypto>) null);
        }
    }

    /* access modifiers changed from: protected */
    public void releaseCodec() {
        this.availableCodecInfos = null;
        this.codecInfo = null;
        this.codecFormat = null;
        resetInputBuffer();
        resetOutputBuffer();
        resetCodecBuffers();
        this.waitingForKeys = false;
        this.codecHotswapDeadlineMs = C.TIME_UNSET;
        this.decodeOnlyPresentationTimestamps.clear();
        try {
            if (this.codec != null) {
                this.decoderCounters.decoderReleaseCount++;
                this.codec.stop();
                this.codec.release();
            }
            this.codec = null;
            try {
                if (this.mediaCrypto != null) {
                    this.mediaCrypto.release();
                }
            } finally {
                this.mediaCrypto = null;
                this.mediaCryptoRequiresSecureDecoder = false;
                setCodecDrmSession((DrmSession<FrameworkMediaCrypto>) null);
            }
        } catch (Throwable th) {
            this.codec = null;
            try {
                if (this.mediaCrypto != null) {
                    this.mediaCrypto.release();
                }
                throw th;
            } finally {
                this.mediaCrypto = null;
                this.mediaCryptoRequiresSecureDecoder = false;
                setCodecDrmSession((DrmSession<FrameworkMediaCrypto>) null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStarted() {
    }

    /* access modifiers changed from: protected */
    public void onStopped() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x004f A[LOOP:1: B:18:0x004f->B:21:0x0059, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void render(long r7, long r9) throws com.google.android.exoplayer2.ExoPlaybackException {
        /*
            r6 = this;
            boolean r0 = r6.outputStreamEnded
            if (r0 == 0) goto L_0x0008
            r6.renderToEndOfStream()
            return
        L_0x0008:
            com.google.android.exoplayer2.Format r0 = r6.inputFormat
            r1 = -4
            r2 = -5
            r3 = 1
            if (r0 != 0) goto L_0x0038
            com.google.android.exoplayer2.decoder.DecoderInputBuffer r0 = r6.flagsOnlyBuffer
            r0.clear()
            com.google.android.exoplayer2.FormatHolder r0 = r6.formatHolder
            com.google.android.exoplayer2.decoder.DecoderInputBuffer r4 = r6.flagsOnlyBuffer
            int r0 = r6.readSource(r0, r4, r3)
            if (r0 != r2) goto L_0x0026
            com.google.android.exoplayer2.FormatHolder r4 = r6.formatHolder
            com.google.android.exoplayer2.Format r4 = r4.format
            r6.onInputFormatChanged(r4)
            goto L_0x0038
        L_0x0026:
            if (r0 != r1) goto L_0x0037
            com.google.android.exoplayer2.decoder.DecoderInputBuffer r1 = r6.flagsOnlyBuffer
            boolean r1 = r1.isEndOfStream()
            com.google.android.exoplayer2.util.Assertions.checkState(r1)
            r6.inputStreamEnded = r3
            r6.processEndOfStream()
            return
        L_0x0037:
            return
        L_0x0038:
            r6.maybeInitCodec()
            android.media.MediaCodec r0 = r6.codec
            if (r0 == 0) goto L_0x0060
            long r0 = android.os.SystemClock.elapsedRealtime()
            java.lang.String r2 = "drainAndFeed"
            com.google.android.exoplayer2.util.TraceUtil.beginSection(r2)
        L_0x0048:
            boolean r2 = r6.drainOutputBuffer(r7, r9)
            if (r2 == 0) goto L_0x004f
            goto L_0x0048
        L_0x004f:
            boolean r2 = r6.feedInputBuffer()
            if (r2 == 0) goto L_0x005c
            boolean r2 = r6.shouldContinueFeeding(r0)
            if (r2 == 0) goto L_0x005c
            goto L_0x004f
        L_0x005c:
            com.google.android.exoplayer2.util.TraceUtil.endSection()
            goto L_0x0093
        L_0x0060:
            com.google.android.exoplayer2.decoder.DecoderCounters r0 = r6.decoderCounters
            int r4 = r0.skippedInputBufferCount
            int r5 = r6.skipSource(r7)
            int r4 = r4 + r5
            r0.skippedInputBufferCount = r4
            com.google.android.exoplayer2.decoder.DecoderInputBuffer r0 = r6.flagsOnlyBuffer
            r0.clear()
            com.google.android.exoplayer2.FormatHolder r0 = r6.formatHolder
            com.google.android.exoplayer2.decoder.DecoderInputBuffer r4 = r6.flagsOnlyBuffer
            r5 = 0
            int r0 = r6.readSource(r0, r4, r5)
            if (r0 != r2) goto L_0x0083
            com.google.android.exoplayer2.FormatHolder r1 = r6.formatHolder
            com.google.android.exoplayer2.Format r1 = r1.format
            r6.onInputFormatChanged(r1)
            goto L_0x0093
        L_0x0083:
            if (r0 != r1) goto L_0x0093
            com.google.android.exoplayer2.decoder.DecoderInputBuffer r1 = r6.flagsOnlyBuffer
            boolean r1 = r1.isEndOfStream()
            com.google.android.exoplayer2.util.Assertions.checkState(r1)
            r6.inputStreamEnded = r3
            r6.processEndOfStream()
        L_0x0093:
            com.google.android.exoplayer2.decoder.DecoderCounters r0 = r6.decoderCounters
            r0.ensureUpdated()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.render(long, long):void");
    }

    /* access modifiers changed from: protected */
    public final void flushOrReinitCodec() throws ExoPlaybackException {
        if (flushOrReleaseCodec()) {
            maybeInitCodec();
        }
    }

    /* access modifiers changed from: protected */
    public boolean flushOrReleaseCodec() {
        if (this.codec == null) {
            return false;
        }
        if (this.codecDrainAction == 2 || this.codecNeedsFlushWorkaround || (this.codecNeedsEosFlushWorkaround && this.codecReceivedEos)) {
            releaseCodec();
            return true;
        }
        this.codec.flush();
        resetInputBuffer();
        resetOutputBuffer();
        this.codecHotswapDeadlineMs = C.TIME_UNSET;
        this.codecReceivedEos = false;
        this.codecReceivedBuffers = false;
        this.waitingForFirstSyncSample = true;
        this.codecNeedsAdaptationWorkaroundBuffer = false;
        this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
        this.shouldSkipOutputBuffer = false;
        this.waitingForKeys = false;
        this.decodeOnlyPresentationTimestamps.clear();
        this.codecDrainState = 0;
        this.codecDrainAction = 0;
        this.codecReconfigurationState = this.codecReconfigured ? 1 : 0;
        return false;
    }

    private void maybeInitCodecWithFallback(MediaCrypto crypto, boolean mediaCryptoRequiresSecureDecoder2) throws DecoderInitializationException {
        if (this.availableCodecInfos == null) {
            try {
                this.availableCodecInfos = new ArrayDeque<>(getAvailableCodecInfos(mediaCryptoRequiresSecureDecoder2));
                this.preferredDecoderInitializationException = null;
            } catch (MediaCodecUtil.DecoderQueryException e) {
                throw new DecoderInitializationException(this.inputFormat, (Throwable) e, mediaCryptoRequiresSecureDecoder2, -49998);
            }
        }
        if (!this.availableCodecInfos.isEmpty()) {
            while (this.codec == null) {
                MediaCodecInfo codecInfo2 = this.availableCodecInfos.peekFirst();
                if (shouldInitCodec(codecInfo2)) {
                    try {
                        initCodec(codecInfo2, crypto);
                    } catch (Exception e2) {
                        Log.w(TAG, "Failed to initialize decoder: " + codecInfo2, e2);
                        this.availableCodecInfos.removeFirst();
                        DecoderInitializationException exception = new DecoderInitializationException(this.inputFormat, (Throwable) e2, mediaCryptoRequiresSecureDecoder2, codecInfo2.name);
                        DecoderInitializationException decoderInitializationException = this.preferredDecoderInitializationException;
                        if (decoderInitializationException == null) {
                            this.preferredDecoderInitializationException = exception;
                        } else {
                            this.preferredDecoderInitializationException = decoderInitializationException.copyWithFallbackException(exception);
                        }
                        if (this.availableCodecInfos.isEmpty()) {
                            throw this.preferredDecoderInitializationException;
                        }
                    }
                } else {
                    return;
                }
            }
            this.availableCodecInfos = null;
            return;
        }
        throw new DecoderInitializationException(this.inputFormat, (Throwable) null, mediaCryptoRequiresSecureDecoder2, -49999);
    }

    private List<MediaCodecInfo> getAvailableCodecInfos(boolean mediaCryptoRequiresSecureDecoder2) throws MediaCodecUtil.DecoderQueryException {
        List<MediaCodecInfo> codecInfos = getDecoderInfos(this.mediaCodecSelector, this.inputFormat, mediaCryptoRequiresSecureDecoder2);
        if (codecInfos.isEmpty() && mediaCryptoRequiresSecureDecoder2) {
            codecInfos = getDecoderInfos(this.mediaCodecSelector, this.inputFormat, false);
            if (!codecInfos.isEmpty()) {
                Log.w(TAG, "Drm session requires secure decoder for " + this.inputFormat.sampleMimeType + ", but no secure decoder available. Trying to proceed with " + codecInfos + ".");
            }
        }
        return codecInfos;
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x010a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initCodec(com.google.android.exoplayer2.mediacodec.MediaCodecInfo r19, android.media.MediaCrypto r20) throws java.lang.Exception {
        /*
            r18 = this;
            r7 = r18
            r8 = r19
            r1 = 0
            java.lang.String r9 = r8.name
            int r0 = com.google.android.exoplayer2.util.Util.SDK_INT
            r2 = 23
            if (r0 >= r2) goto L_0x0010
            r0 = -1082130432(0xffffffffbf800000, float:-1.0)
            goto L_0x001c
        L_0x0010:
            float r0 = r7.rendererOperatingRate
            com.google.android.exoplayer2.Format r2 = r7.inputFormat
            com.google.android.exoplayer2.Format[] r3 = r18.getStreamFormats()
            float r0 = r7.getCodecOperatingRateV23(r0, r2, r3)
        L_0x001c:
            float r2 = r7.assumedMinimumCodecOperatingRate
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0027
            r0 = -1082130432(0xffffffffbf800000, float:-1.0)
            r10 = r0
            goto L_0x0028
        L_0x0027:
            r10 = r0
        L_0x0028:
            long r2 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0107 }
            r11 = r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0107 }
            r0.<init>()     // Catch:{ Exception -> 0x0107 }
            java.lang.String r2 = "createCodec:"
            r0.append(r2)     // Catch:{ Exception -> 0x0107 }
            r0.append(r9)     // Catch:{ Exception -> 0x0107 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0107 }
            com.google.android.exoplayer2.util.TraceUtil.beginSection(r0)     // Catch:{ Exception -> 0x0107 }
            android.media.MediaCodec r0 = android.media.MediaCodec.createByCodecName(r9)     // Catch:{ Exception -> 0x0107 }
            r13 = r0
            com.google.android.exoplayer2.util.TraceUtil.endSection()     // Catch:{ Exception -> 0x0104 }
            java.lang.String r0 = "configureCodec"
            com.google.android.exoplayer2.util.TraceUtil.beginSection(r0)     // Catch:{ Exception -> 0x0104 }
            com.google.android.exoplayer2.Format r4 = r7.inputFormat     // Catch:{ Exception -> 0x0104 }
            r1 = r18
            r2 = r19
            r3 = r13
            r5 = r20
            r6 = r10
            r1.configureCodec(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0104 }
            com.google.android.exoplayer2.util.TraceUtil.endSection()     // Catch:{ Exception -> 0x0104 }
            java.lang.String r0 = "startCodec"
            com.google.android.exoplayer2.util.TraceUtil.beginSection(r0)     // Catch:{ Exception -> 0x0104 }
            r13.start()     // Catch:{ Exception -> 0x0104 }
            com.google.android.exoplayer2.util.TraceUtil.endSection()     // Catch:{ Exception -> 0x0104 }
            long r0 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0104 }
            r14 = r0
            r7.getCodecBuffers(r13)     // Catch:{ Exception -> 0x0104 }
            r7.codec = r13
            r7.codecInfo = r8
            r7.codecOperatingRate = r10
            com.google.android.exoplayer2.Format r0 = r7.inputFormat
            r7.codecFormat = r0
            int r0 = r7.codecAdaptationWorkaroundMode(r9)
            r7.codecAdaptationWorkaroundMode = r0
            boolean r0 = codecNeedsReconfigureWorkaround(r9)
            r7.codecNeedsReconfigureWorkaround = r0
            com.google.android.exoplayer2.Format r0 = r7.codecFormat
            boolean r0 = codecNeedsDiscardToSpsWorkaround(r9, r0)
            r7.codecNeedsDiscardToSpsWorkaround = r0
            boolean r0 = codecNeedsFlushWorkaround(r9)
            r7.codecNeedsFlushWorkaround = r0
            boolean r0 = codecNeedsEosFlushWorkaround(r9)
            r7.codecNeedsEosFlushWorkaround = r0
            boolean r0 = codecNeedsEosOutputExceptionWorkaround(r9)
            r7.codecNeedsEosOutputExceptionWorkaround = r0
            com.google.android.exoplayer2.Format r0 = r7.codecFormat
            boolean r0 = codecNeedsMonoChannelCountWorkaround(r9, r0)
            r7.codecNeedsMonoChannelCountWorkaround = r0
            boolean r0 = codecNeedsEosPropagationWorkaround(r19)
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x00bd
            boolean r0 = r18.getCodecNeedsEosPropagation()
            if (r0 == 0) goto L_0x00bb
            goto L_0x00bd
        L_0x00bb:
            r0 = 0
            goto L_0x00be
        L_0x00bd:
            r0 = 1
        L_0x00be:
            r7.codecNeedsEosPropagation = r0
            r18.resetInputBuffer()
            r18.resetOutputBuffer()
            int r0 = r18.getState()
            r3 = 2
            if (r0 != r3) goto L_0x00d6
            long r3 = android.os.SystemClock.elapsedRealtime()
            r5 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 + r5
            goto L_0x00db
        L_0x00d6:
            r3 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
        L_0x00db:
            r7.codecHotswapDeadlineMs = r3
            r7.codecReconfigured = r2
            r7.codecReconfigurationState = r2
            r7.codecReceivedEos = r2
            r7.codecReceivedBuffers = r2
            r7.codecDrainState = r2
            r7.codecDrainAction = r2
            r7.codecNeedsAdaptationWorkaroundBuffer = r2
            r7.shouldSkipAdaptationWorkaroundOutputBuffer = r2
            r7.shouldSkipOutputBuffer = r2
            r7.waitingForFirstSyncSample = r1
            com.google.android.exoplayer2.decoder.DecoderCounters r0 = r7.decoderCounters
            int r2 = r0.decoderInitCount
            int r2 = r2 + r1
            r0.decoderInitCount = r2
            long r16 = r14 - r11
            r1 = r18
            r2 = r9
            r3 = r14
            r5 = r16
            r1.onCodecInitialized(r2, r3, r5)
            return
        L_0x0104:
            r0 = move-exception
            r1 = r13
            goto L_0x0108
        L_0x0107:
            r0 = move-exception
        L_0x0108:
            if (r1 == 0) goto L_0x0110
            r18.resetCodecBuffers()
            r1.release()
        L_0x0110:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.initCodec(com.google.android.exoplayer2.mediacodec.MediaCodecInfo, android.media.MediaCrypto):void");
    }

    private boolean shouldContinueFeeding(long drainStartTimeMs) {
        return this.renderTimeLimitMs == C.TIME_UNSET || SystemClock.elapsedRealtime() - drainStartTimeMs < this.renderTimeLimitMs;
    }

    private void getCodecBuffers(MediaCodec codec2) {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = codec2.getInputBuffers();
            this.outputBuffers = codec2.getOutputBuffers();
        }
    }

    private void resetCodecBuffers() {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = null;
            this.outputBuffers = null;
        }
    }

    private ByteBuffer getInputBuffer(int inputIndex2) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getInputBuffer(inputIndex2);
        }
        return this.inputBuffers[inputIndex2];
    }

    private ByteBuffer getOutputBuffer(int outputIndex2) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getOutputBuffer(outputIndex2);
        }
        return this.outputBuffers[outputIndex2];
    }

    private boolean hasOutputBuffer() {
        return this.outputIndex >= 0;
    }

    private void resetInputBuffer() {
        this.inputIndex = -1;
        this.buffer.data = null;
    }

    private void resetOutputBuffer() {
        this.outputIndex = -1;
        this.outputBuffer = null;
    }

    private void setSourceDrmSession(DrmSession<FrameworkMediaCrypto> session) {
        DrmSession<FrameworkMediaCrypto> previous = this.sourceDrmSession;
        this.sourceDrmSession = session;
        releaseDrmSessionIfUnused(previous);
    }

    private void setCodecDrmSession(DrmSession<FrameworkMediaCrypto> session) {
        DrmSession<FrameworkMediaCrypto> previous = this.codecDrmSession;
        this.codecDrmSession = session;
        releaseDrmSessionIfUnused(previous);
    }

    private void releaseDrmSessionIfUnused(DrmSession<FrameworkMediaCrypto> session) {
        if (session != null && session != this.sourceDrmSession && session != this.codecDrmSession) {
            this.drmSessionManager.releaseSession(session);
        }
    }

    private boolean feedInputBuffer() throws ExoPlaybackException {
        int result;
        MediaCodec mediaCodec = this.codec;
        if (mediaCodec == null || this.codecDrainState == 2 || this.inputStreamEnded) {
            return false;
        }
        if (this.inputIndex < 0) {
            int dequeueInputBuffer = mediaCodec.dequeueInputBuffer(0);
            this.inputIndex = dequeueInputBuffer;
            if (dequeueInputBuffer < 0) {
                return false;
            }
            this.buffer.data = getInputBuffer(dequeueInputBuffer);
            this.buffer.clear();
        }
        if (this.codecDrainState == 1) {
            if (!this.codecNeedsEosPropagation) {
                this.codecReceivedEos = true;
                this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0, 4);
                resetInputBuffer();
            }
            this.codecDrainState = 2;
            return false;
        } else if (this.codecNeedsAdaptationWorkaroundBuffer) {
            this.codecNeedsAdaptationWorkaroundBuffer = false;
            this.buffer.data.put(ADAPTATION_WORKAROUND_BUFFER);
            this.codec.queueInputBuffer(this.inputIndex, 0, ADAPTATION_WORKAROUND_BUFFER.length, 0, 0);
            resetInputBuffer();
            this.codecReceivedBuffers = true;
            return true;
        } else {
            int adaptiveReconfigurationBytes = 0;
            if (this.waitingForKeys) {
                result = -4;
            } else {
                if (this.codecReconfigurationState == 1) {
                    for (int i = 0; i < this.codecFormat.initializationData.size(); i++) {
                        this.buffer.data.put(this.codecFormat.initializationData.get(i));
                    }
                    this.codecReconfigurationState = 2;
                }
                adaptiveReconfigurationBytes = this.buffer.data.position();
                result = readSource(this.formatHolder, this.buffer, false);
            }
            if (result == -3) {
                return false;
            }
            if (result == -5) {
                if (this.codecReconfigurationState == 2) {
                    this.buffer.clear();
                    this.codecReconfigurationState = 1;
                }
                onInputFormatChanged(this.formatHolder.format);
                return true;
            } else if (this.buffer.isEndOfStream()) {
                if (this.codecReconfigurationState == 2) {
                    this.buffer.clear();
                    this.codecReconfigurationState = 1;
                }
                this.inputStreamEnded = true;
                if (!this.codecReceivedBuffers) {
                    processEndOfStream();
                    return false;
                }
                try {
                    if (!this.codecNeedsEosPropagation) {
                        this.codecReceivedEos = true;
                        this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0, 4);
                        resetInputBuffer();
                    }
                    return false;
                } catch (MediaCodec.CryptoException e) {
                    throw ExoPlaybackException.createForRenderer(e, getIndex());
                }
            } else if (!this.waitingForFirstSyncSample || this.buffer.isKeyFrame()) {
                this.waitingForFirstSyncSample = false;
                boolean bufferEncrypted = this.buffer.isEncrypted();
                boolean shouldWaitForKeys = shouldWaitForKeys(bufferEncrypted);
                this.waitingForKeys = shouldWaitForKeys;
                if (shouldWaitForKeys) {
                    return false;
                }
                if (this.codecNeedsDiscardToSpsWorkaround && !bufferEncrypted) {
                    NalUnitUtil.discardToSps(this.buffer.data);
                    if (this.buffer.data.position() == 0) {
                        return true;
                    }
                    this.codecNeedsDiscardToSpsWorkaround = false;
                }
                try {
                    long presentationTimeUs = this.buffer.timeUs;
                    if (this.buffer.isDecodeOnly()) {
                        this.decodeOnlyPresentationTimestamps.add(Long.valueOf(presentationTimeUs));
                    }
                    if (this.waitingForFirstSampleInFormat) {
                        this.formatQueue.add(presentationTimeUs, this.inputFormat);
                        this.waitingForFirstSampleInFormat = false;
                    }
                    this.buffer.flip();
                    onQueueInputBuffer(this.buffer);
                    if (bufferEncrypted) {
                        this.codec.queueSecureInputBuffer(this.inputIndex, 0, getFrameworkCryptoInfo(this.buffer, adaptiveReconfigurationBytes), presentationTimeUs, 0);
                    } else {
                        this.codec.queueInputBuffer(this.inputIndex, 0, this.buffer.data.limit(), presentationTimeUs, 0);
                    }
                    resetInputBuffer();
                    this.codecReceivedBuffers = true;
                    this.codecReconfigurationState = 0;
                    this.decoderCounters.inputBufferCount++;
                    return true;
                } catch (MediaCodec.CryptoException e2) {
                    throw ExoPlaybackException.createForRenderer(e2, getIndex());
                }
            } else {
                this.buffer.clear();
                if (this.codecReconfigurationState == 2) {
                    this.codecReconfigurationState = 1;
                }
                return true;
            }
        }
    }

    private boolean shouldWaitForKeys(boolean bufferEncrypted) throws ExoPlaybackException {
        if (this.codecDrmSession == null || (!bufferEncrypted && this.playClearSamplesWithoutKeys)) {
            return false;
        }
        int drmSessionState = this.codecDrmSession.getState();
        if (drmSessionState == 1) {
            throw ExoPlaybackException.createForRenderer(this.codecDrmSession.getError(), getIndex());
        } else if (drmSessionState != 4) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
    }

    /* access modifiers changed from: protected */
    public void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        Format oldFormat = this.inputFormat;
        this.inputFormat = newFormat;
        boolean z = true;
        this.waitingForFirstSampleInFormat = true;
        if (!Util.areEqual(newFormat.drmInitData, oldFormat == null ? null : oldFormat.drmInitData)) {
            if (newFormat.drmInitData != null) {
                DrmSessionManager<FrameworkMediaCrypto> drmSessionManager2 = this.drmSessionManager;
                if (drmSessionManager2 != null) {
                    DrmSession<FrameworkMediaCrypto> session = drmSessionManager2.acquireSession(Looper.myLooper(), newFormat.drmInitData);
                    if (session == this.sourceDrmSession || session == this.codecDrmSession) {
                        this.drmSessionManager.releaseSession(session);
                    }
                    setSourceDrmSession(session);
                } else {
                    throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
                }
            } else {
                setSourceDrmSession((DrmSession<FrameworkMediaCrypto>) null);
            }
        }
        MediaCodec mediaCodec = this.codec;
        if (mediaCodec == null) {
            maybeInitCodec();
        } else if (this.sourceDrmSession != this.codecDrmSession) {
            drainAndReinitializeCodec();
        } else {
            int canKeepCodec = canKeepCodec(mediaCodec, this.codecInfo, this.codecFormat, newFormat);
            if (canKeepCodec == 0) {
                drainAndReinitializeCodec();
            } else if (canKeepCodec == 1) {
                drainAndFlushCodec();
                this.codecFormat = newFormat;
                updateCodecOperatingRate();
            } else if (canKeepCodec != 2) {
                if (canKeepCodec == 3) {
                    this.codecFormat = newFormat;
                    updateCodecOperatingRate();
                    return;
                }
                throw new IllegalStateException();
            } else if (this.codecNeedsReconfigureWorkaround) {
                drainAndReinitializeCodec();
            } else {
                this.codecReconfigured = true;
                this.codecReconfigurationState = 1;
                int i = this.codecAdaptationWorkaroundMode;
                if (!(i == 2 || (i == 1 && newFormat.width == this.codecFormat.width && newFormat.height == this.codecFormat.height))) {
                    z = false;
                }
                this.codecNeedsAdaptationWorkaroundBuffer = z;
                this.codecFormat = newFormat;
                updateCodecOperatingRate();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onOutputFormatChanged(MediaCodec codec2, MediaFormat outputFormat2) throws ExoPlaybackException {
    }

    /* access modifiers changed from: protected */
    public void onQueueInputBuffer(DecoderInputBuffer buffer2) {
    }

    /* access modifiers changed from: protected */
    public void onProcessedOutputBuffer(long presentationTimeUs) {
    }

    /* access modifiers changed from: protected */
    public int canKeepCodec(MediaCodec codec2, MediaCodecInfo codecInfo2, Format oldFormat, Format newFormat) {
        return 0;
    }

    public boolean isEnded() {
        return this.outputStreamEnded;
    }

    public boolean isReady() {
        return this.inputFormat != null && !this.waitingForKeys && (isSourceReady() || hasOutputBuffer() || (this.codecHotswapDeadlineMs != C.TIME_UNSET && SystemClock.elapsedRealtime() < this.codecHotswapDeadlineMs));
    }

    /* access modifiers changed from: protected */
    public long getDequeueOutputBufferTimeoutUs() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public float getCodecOperatingRateV23(float operatingRate, Format format, Format[] streamFormats) {
        return -1.0f;
    }

    private void updateCodecOperatingRate() throws ExoPlaybackException {
        if (Util.SDK_INT >= 23) {
            float newCodecOperatingRate = getCodecOperatingRateV23(this.rendererOperatingRate, this.codecFormat, getStreamFormats());
            float f = this.codecOperatingRate;
            if (f != newCodecOperatingRate) {
                if (newCodecOperatingRate == -1.0f) {
                    drainAndReinitializeCodec();
                } else if (f != -1.0f || newCodecOperatingRate > this.assumedMinimumCodecOperatingRate) {
                    Bundle codecParameters = new Bundle();
                    codecParameters.putFloat("operating-rate", newCodecOperatingRate);
                    this.codec.setParameters(codecParameters);
                    this.codecOperatingRate = newCodecOperatingRate;
                }
            }
        }
    }

    private void drainAndFlushCodec() {
        if (this.codecReceivedBuffers) {
            this.codecDrainState = 1;
            this.codecDrainAction = 1;
        }
    }

    private void drainAndReinitializeCodec() throws ExoPlaybackException {
        if (this.codecReceivedBuffers) {
            this.codecDrainState = 1;
            this.codecDrainAction = 2;
            return;
        }
        releaseCodec();
        maybeInitCodec();
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x00da  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean drainOutputBuffer(long r18, long r20) throws com.google.android.exoplayer2.ExoPlaybackException {
        /*
            r17 = this;
            r14 = r17
            boolean r0 = r17.hasOutputBuffer()
            r15 = 1
            r13 = 0
            if (r0 != 0) goto L_0x00a6
            boolean r0 = r14.codecNeedsEosOutputExceptionWorkaround
            if (r0 == 0) goto L_0x002b
            boolean r0 = r14.codecReceivedEos
            if (r0 == 0) goto L_0x002b
            android.media.MediaCodec r0 = r14.codec     // Catch:{ IllegalStateException -> 0x001f }
            android.media.MediaCodec$BufferInfo r1 = r14.outputBufferInfo     // Catch:{ IllegalStateException -> 0x001f }
            long r2 = r17.getDequeueOutputBufferTimeoutUs()     // Catch:{ IllegalStateException -> 0x001f }
            int r0 = r0.dequeueOutputBuffer(r1, r2)     // Catch:{ IllegalStateException -> 0x001f }
            goto L_0x0037
        L_0x001f:
            r0 = move-exception
            r17.processEndOfStream()
            boolean r1 = r14.outputStreamEnded
            if (r1 == 0) goto L_0x002a
            r17.releaseCodec()
        L_0x002a:
            return r13
        L_0x002b:
            android.media.MediaCodec r0 = r14.codec
            android.media.MediaCodec$BufferInfo r1 = r14.outputBufferInfo
            long r2 = r17.getDequeueOutputBufferTimeoutUs()
            int r0 = r0.dequeueOutputBuffer(r1, r2)
        L_0x0037:
            if (r0 >= 0) goto L_0x0058
            r1 = -2
            if (r0 != r1) goto L_0x0040
            r17.processOutputFormat()
            return r15
        L_0x0040:
            r1 = -3
            if (r0 != r1) goto L_0x0047
            r17.processOutputBuffersChanged()
            return r15
        L_0x0047:
            boolean r1 = r14.codecNeedsEosPropagation
            if (r1 == 0) goto L_0x0057
            boolean r1 = r14.inputStreamEnded
            if (r1 != 0) goto L_0x0054
            int r1 = r14.codecDrainState
            r2 = 2
            if (r1 != r2) goto L_0x0057
        L_0x0054:
            r17.processEndOfStream()
        L_0x0057:
            return r13
        L_0x0058:
            boolean r1 = r14.shouldSkipAdaptationWorkaroundOutputBuffer
            if (r1 == 0) goto L_0x0064
            r14.shouldSkipAdaptationWorkaroundOutputBuffer = r13
            android.media.MediaCodec r1 = r14.codec
            r1.releaseOutputBuffer(r0, r13)
            return r15
        L_0x0064:
            android.media.MediaCodec$BufferInfo r1 = r14.outputBufferInfo
            int r1 = r1.size
            if (r1 != 0) goto L_0x0076
            android.media.MediaCodec$BufferInfo r1 = r14.outputBufferInfo
            int r1 = r1.flags
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0076
            r17.processEndOfStream()
            return r13
        L_0x0076:
            r14.outputIndex = r0
            java.nio.ByteBuffer r1 = r14.getOutputBuffer(r0)
            r14.outputBuffer = r1
            if (r1 == 0) goto L_0x0095
            android.media.MediaCodec$BufferInfo r2 = r14.outputBufferInfo
            int r2 = r2.offset
            r1.position(r2)
            java.nio.ByteBuffer r1 = r14.outputBuffer
            android.media.MediaCodec$BufferInfo r2 = r14.outputBufferInfo
            int r2 = r2.offset
            android.media.MediaCodec$BufferInfo r3 = r14.outputBufferInfo
            int r3 = r3.size
            int r2 = r2 + r3
            r1.limit(r2)
        L_0x0095:
            android.media.MediaCodec$BufferInfo r1 = r14.outputBufferInfo
            long r1 = r1.presentationTimeUs
            boolean r1 = r14.shouldSkipOutputBuffer(r1)
            r14.shouldSkipOutputBuffer = r1
            android.media.MediaCodec$BufferInfo r1 = r14.outputBufferInfo
            long r1 = r1.presentationTimeUs
            r14.updateOutputFormatForTime(r1)
        L_0x00a6:
            boolean r0 = r14.codecNeedsEosOutputExceptionWorkaround
            if (r0 == 0) goto L_0x00de
            boolean r0 = r14.codecReceivedEos
            if (r0 == 0) goto L_0x00de
            android.media.MediaCodec r6 = r14.codec     // Catch:{ IllegalStateException -> 0x00d0 }
            java.nio.ByteBuffer r7 = r14.outputBuffer     // Catch:{ IllegalStateException -> 0x00d0 }
            int r8 = r14.outputIndex     // Catch:{ IllegalStateException -> 0x00d0 }
            android.media.MediaCodec$BufferInfo r0 = r14.outputBufferInfo     // Catch:{ IllegalStateException -> 0x00d0 }
            int r9 = r0.flags     // Catch:{ IllegalStateException -> 0x00d0 }
            android.media.MediaCodec$BufferInfo r0 = r14.outputBufferInfo     // Catch:{ IllegalStateException -> 0x00d0 }
            long r10 = r0.presentationTimeUs     // Catch:{ IllegalStateException -> 0x00d0 }
            boolean r12 = r14.shouldSkipOutputBuffer     // Catch:{ IllegalStateException -> 0x00d0 }
            com.google.android.exoplayer2.Format r0 = r14.outputFormat     // Catch:{ IllegalStateException -> 0x00d0 }
            r1 = r17
            r2 = r18
            r4 = r20
            r16 = 0
            r13 = r0
            boolean r0 = r1.processOutputBuffer(r2, r4, r6, r7, r8, r9, r10, r12, r13)     // Catch:{ IllegalStateException -> 0x00ce }
            goto L_0x00fc
        L_0x00ce:
            r0 = move-exception
            goto L_0x00d3
        L_0x00d0:
            r0 = move-exception
            r16 = 0
        L_0x00d3:
            r17.processEndOfStream()
            boolean r1 = r14.outputStreamEnded
            if (r1 == 0) goto L_0x00dd
            r17.releaseCodec()
        L_0x00dd:
            return r16
        L_0x00de:
            r16 = 0
            android.media.MediaCodec r6 = r14.codec
            java.nio.ByteBuffer r7 = r14.outputBuffer
            int r8 = r14.outputIndex
            android.media.MediaCodec$BufferInfo r0 = r14.outputBufferInfo
            int r9 = r0.flags
            android.media.MediaCodec$BufferInfo r0 = r14.outputBufferInfo
            long r10 = r0.presentationTimeUs
            boolean r12 = r14.shouldSkipOutputBuffer
            com.google.android.exoplayer2.Format r13 = r14.outputFormat
            r1 = r17
            r2 = r18
            r4 = r20
            boolean r0 = r1.processOutputBuffer(r2, r4, r6, r7, r8, r9, r10, r12, r13)
        L_0x00fc:
            if (r0 == 0) goto L_0x011a
            android.media.MediaCodec$BufferInfo r1 = r14.outputBufferInfo
            long r1 = r1.presentationTimeUs
            r14.onProcessedOutputBuffer(r1)
            android.media.MediaCodec$BufferInfo r1 = r14.outputBufferInfo
            int r1 = r1.flags
            r1 = r1 & 4
            if (r1 == 0) goto L_0x010f
            r13 = 1
            goto L_0x0110
        L_0x010f:
            r13 = 0
        L_0x0110:
            r1 = r13
            r17.resetOutputBuffer()
            if (r1 != 0) goto L_0x0117
            return r15
        L_0x0117:
            r17.processEndOfStream()
        L_0x011a:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.drainOutputBuffer(long, long):boolean");
    }

    private void processOutputFormat() throws ExoPlaybackException {
        MediaFormat format = this.codec.getOutputFormat();
        if (this.codecAdaptationWorkaroundMode != 0 && format.getInteger("width") == 32 && format.getInteger("height") == 32) {
            this.shouldSkipAdaptationWorkaroundOutputBuffer = true;
            return;
        }
        if (this.codecNeedsMonoChannelCountWorkaround) {
            format.setInteger("channel-count", 1);
        }
        onOutputFormatChanged(this.codec, format);
    }

    private void processOutputBuffersChanged() {
        if (Util.SDK_INT < 21) {
            this.outputBuffers = this.codec.getOutputBuffers();
        }
    }

    /* access modifiers changed from: protected */
    public void renderToEndOfStream() throws ExoPlaybackException {
    }

    private void processEndOfStream() throws ExoPlaybackException {
        int i = this.codecDrainAction;
        if (i == 1) {
            flushOrReinitCodec();
        } else if (i != 2) {
            this.outputStreamEnded = true;
            renderToEndOfStream();
        } else {
            releaseCodec();
            maybeInitCodec();
        }
    }

    private boolean shouldSkipOutputBuffer(long presentationTimeUs) {
        int size = this.decodeOnlyPresentationTimestamps.size();
        for (int i = 0; i < size; i++) {
            if (this.decodeOnlyPresentationTimestamps.get(i).longValue() == presentationTimeUs) {
                this.decodeOnlyPresentationTimestamps.remove(i);
                return true;
            }
        }
        return false;
    }

    private static MediaCodec.CryptoInfo getFrameworkCryptoInfo(DecoderInputBuffer buffer2, int adaptiveReconfigurationBytes) {
        MediaCodec.CryptoInfo cryptoInfo = buffer2.cryptoInfo.getFrameworkCryptoInfoV16();
        if (adaptiveReconfigurationBytes == 0) {
            return cryptoInfo;
        }
        if (cryptoInfo.numBytesOfClearData == null) {
            cryptoInfo.numBytesOfClearData = new int[1];
        }
        int[] iArr = cryptoInfo.numBytesOfClearData;
        iArr[0] = iArr[0] + adaptiveReconfigurationBytes;
        return cryptoInfo;
    }

    private boolean deviceNeedsDrmKeysToConfigureCodecWorkaround() {
        return "Amazon".equals(Util.MANUFACTURER) && ("AFTM".equals(Util.MODEL) || "AFTB".equals(Util.MODEL));
    }

    private static boolean codecNeedsFlushWorkaround(String name) {
        return Util.SDK_INT < 18 || (Util.SDK_INT == 18 && ("OMX.SEC.avc.dec".equals(name) || "OMX.SEC.avc.dec.secure".equals(name))) || (Util.SDK_INT == 19 && Util.MODEL.startsWith("SM-G800") && ("OMX.Exynos.avc.dec".equals(name) || "OMX.Exynos.avc.dec.secure".equals(name)));
    }

    private int codecAdaptationWorkaroundMode(String name) {
        if (Util.SDK_INT <= 25 && "OMX.Exynos.avc.dec.secure".equals(name) && (Util.MODEL.startsWith("SM-T585") || Util.MODEL.startsWith("SM-A510") || Util.MODEL.startsWith("SM-A520") || Util.MODEL.startsWith("SM-J700"))) {
            return 2;
        }
        if (Util.SDK_INT >= 24) {
            return 0;
        }
        if (!"OMX.Nvidia.h264.decode".equals(name) && !"OMX.Nvidia.h264.decode.secure".equals(name)) {
            return 0;
        }
        if ("flounder".equals(Util.DEVICE) || "flounder_lte".equals(Util.DEVICE) || "grouper".equals(Util.DEVICE) || "tilapia".equals(Util.DEVICE)) {
            return 1;
        }
        return 0;
    }

    private static boolean codecNeedsReconfigureWorkaround(String name) {
        return Util.MODEL.startsWith("SM-T230") && "OMX.MARVELL.VIDEO.HW.CODA7542DECODER".equals(name);
    }

    private static boolean codecNeedsDiscardToSpsWorkaround(String name, Format format) {
        return Util.SDK_INT < 21 && format.initializationData.isEmpty() && "OMX.MTK.VIDEO.DECODER.AVC".equals(name);
    }

    private static boolean codecNeedsEosPropagationWorkaround(MediaCodecInfo codecInfo2) {
        String name = codecInfo2.name;
        return (Util.SDK_INT <= 17 && ("OMX.rk.video_decoder.avc".equals(name) || "OMX.allwinner.video.decoder.avc".equals(name))) || ("Amazon".equals(Util.MANUFACTURER) && "AFTS".equals(Util.MODEL) && codecInfo2.secure);
    }

    private static boolean codecNeedsEosFlushWorkaround(String name) {
        return (Util.SDK_INT <= 23 && "OMX.google.vorbis.decoder".equals(name)) || (Util.SDK_INT <= 19 && (("hb2000".equals(Util.DEVICE) || "stvm8".equals(Util.DEVICE)) && ("OMX.amlogic.avc.decoder.awesome".equals(name) || "OMX.amlogic.avc.decoder.awesome.secure".equals(name))));
    }

    private static boolean codecNeedsEosOutputExceptionWorkaround(String name) {
        return Util.SDK_INT == 21 && "OMX.google.aac.decoder".equals(name);
    }

    private static boolean codecNeedsMonoChannelCountWorkaround(String name, Format format) {
        if (Util.SDK_INT > 18 || format.channelCount != 1 || !"OMX.MTK.AUDIO.DECODER.MP3".equals(name)) {
            return false;
        }
        return true;
    }
}
