package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.List;

public class AdaptiveTrackSelection extends BaseTrackSelection {
    public static final float DEFAULT_BANDWIDTH_FRACTION = 0.75f;
    public static final float DEFAULT_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE = 0.75f;
    public static final int DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS = 25000;
    public static final int DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS = 10000;
    public static final int DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = 25000;
    public static final long DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS = 2000;
    private final BandwidthProvider bandwidthProvider;
    private final float bufferedFractionToLiveEdgeForQualityIncrease;
    private final Clock clock;
    private final int[] formatBitrates;
    private final Format[] formats;
    private long lastBufferEvaluationMs;
    private final long maxDurationForQualityDecreaseUs;
    private final long minDurationForQualityIncreaseUs;
    private final long minDurationToRetainAfterDiscardUs;
    private final long minTimeBetweenBufferReevaluationMs;
    private float playbackSpeed;
    private int reason;
    private int selectedIndex;
    private TrackBitrateEstimator trackBitrateEstimator;
    private final int[] trackBitrates;

    private interface BandwidthProvider {
        long getAllocatedBandwidth();
    }

    public static final class Factory implements TrackSelection.Factory {
        private final float bandwidthFraction;
        private final BandwidthMeter bandwidthMeter;
        private boolean blockFixedTrackSelectionBandwidth;
        private final float bufferedFractionToLiveEdgeForQualityIncrease;
        private final Clock clock;
        private final int maxDurationForQualityDecreaseMs;
        private final int minDurationForQualityIncreaseMs;
        private final int minDurationToRetainAfterDiscardMs;
        private final long minTimeBetweenBufferReevaluationMs;
        private TrackBitrateEstimator trackBitrateEstimator;

        @Deprecated
        public /* synthetic */ TrackSelection createTrackSelection(TrackGroup trackGroup, BandwidthMeter bandwidthMeter2, int... iArr) {
            return TrackSelection.Factory.CC.$default$createTrackSelection(this, trackGroup, bandwidthMeter2, iArr);
        }

        public Factory() {
            this(10000, 25000, 25000, 0.75f, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter2) {
            this(bandwidthMeter2, 10000, 25000, 25000, 0.75f, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        public Factory(int minDurationForQualityIncreaseMs2, int maxDurationForQualityDecreaseMs2, int minDurationToRetainAfterDiscardMs2, float bandwidthFraction2) {
            this(minDurationForQualityIncreaseMs2, maxDurationForQualityDecreaseMs2, minDurationToRetainAfterDiscardMs2, bandwidthFraction2, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter2, int minDurationForQualityIncreaseMs2, int maxDurationForQualityDecreaseMs2, int minDurationToRetainAfterDiscardMs2, float bandwidthFraction2) {
            this(bandwidthMeter2, minDurationForQualityIncreaseMs2, maxDurationForQualityDecreaseMs2, minDurationToRetainAfterDiscardMs2, bandwidthFraction2, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        public Factory(int minDurationForQualityIncreaseMs2, int maxDurationForQualityDecreaseMs2, int minDurationToRetainAfterDiscardMs2, float bandwidthFraction2, float bufferedFractionToLiveEdgeForQualityIncrease2, long minTimeBetweenBufferReevaluationMs2, Clock clock2) {
            this((BandwidthMeter) null, minDurationForQualityIncreaseMs2, maxDurationForQualityDecreaseMs2, minDurationToRetainAfterDiscardMs2, bandwidthFraction2, bufferedFractionToLiveEdgeForQualityIncrease2, minTimeBetweenBufferReevaluationMs2, clock2);
        }

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter2, int minDurationForQualityIncreaseMs2, int maxDurationForQualityDecreaseMs2, int minDurationToRetainAfterDiscardMs2, float bandwidthFraction2, float bufferedFractionToLiveEdgeForQualityIncrease2, long minTimeBetweenBufferReevaluationMs2, Clock clock2) {
            this.bandwidthMeter = bandwidthMeter2;
            this.minDurationForQualityIncreaseMs = minDurationForQualityIncreaseMs2;
            this.maxDurationForQualityDecreaseMs = maxDurationForQualityDecreaseMs2;
            this.minDurationToRetainAfterDiscardMs = minDurationToRetainAfterDiscardMs2;
            this.bandwidthFraction = bandwidthFraction2;
            this.bufferedFractionToLiveEdgeForQualityIncrease = bufferedFractionToLiveEdgeForQualityIncrease2;
            this.minTimeBetweenBufferReevaluationMs = minTimeBetweenBufferReevaluationMs2;
            this.clock = clock2;
            this.trackBitrateEstimator = TrackBitrateEstimator.DEFAULT;
        }

        public void experimental_setTrackBitrateEstimator(TrackBitrateEstimator trackBitrateEstimator2) {
            this.trackBitrateEstimator = trackBitrateEstimator2;
        }

        public void experimental_enableBlockFixedTrackSelectionBandwidth() {
            this.blockFixedTrackSelectionBandwidth = true;
        }

        public TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitions, BandwidthMeter bandwidthMeter2) {
            TrackSelection[] selections = new TrackSelection[definitions.length];
            AdaptiveTrackSelection adaptiveSelection = null;
            int totalFixedBandwidth = 0;
            for (int i = 0; i < definitions.length; i++) {
                TrackSelection.Definition definition = definitions[i];
                if (definition != null) {
                    if (definition.tracks.length > 1) {
                        adaptiveSelection = createAdaptiveTrackSelection(definition.group, bandwidthMeter2, definition.tracks);
                        selections[i] = adaptiveSelection;
                    } else {
                        selections[i] = new FixedTrackSelection(definition.group, definition.tracks[0]);
                        int trackBitrate = definition.group.getFormat(definition.tracks[0]).bitrate;
                        if (trackBitrate != -1) {
                            totalFixedBandwidth += trackBitrate;
                        }
                    }
                }
            }
            if (!(this.blockFixedTrackSelectionBandwidth == 0 || adaptiveSelection == null)) {
                adaptiveSelection.experimental_setNonAllocatableBandwidth((long) totalFixedBandwidth);
            }
            return selections;
        }

        private AdaptiveTrackSelection createAdaptiveTrackSelection(TrackGroup group, BandwidthMeter bandwidthMeter2, int[] tracks) {
            BandwidthMeter bandwidthMeter3;
            if (this.bandwidthMeter != null) {
                bandwidthMeter3 = this.bandwidthMeter;
            } else {
                bandwidthMeter3 = bandwidthMeter2;
            }
            AdaptiveTrackSelection adaptiveTrackSelection = new AdaptiveTrackSelection(group, tracks, (BandwidthProvider) new DefaultBandwidthProvider(bandwidthMeter3, this.bandwidthFraction), (long) this.minDurationForQualityIncreaseMs, (long) this.maxDurationForQualityDecreaseMs, (long) this.minDurationToRetainAfterDiscardMs, this.bufferedFractionToLiveEdgeForQualityIncrease, this.minTimeBetweenBufferReevaluationMs, this.clock);
            adaptiveTrackSelection.experimental_setTrackBitrateEstimator(this.trackBitrateEstimator);
            return adaptiveTrackSelection;
        }
    }

    public AdaptiveTrackSelection(TrackGroup group, int[] tracks, BandwidthMeter bandwidthMeter) {
        this(group, tracks, bandwidthMeter, (long) OkHttpUtils.DEFAULT_MILLISECONDS, 25000, 25000, 0.75f, 0.75f, (long) DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
    }

    public AdaptiveTrackSelection(TrackGroup group, int[] tracks, BandwidthMeter bandwidthMeter, long minDurationForQualityIncreaseMs, long maxDurationForQualityDecreaseMs, long minDurationToRetainAfterDiscardMs, float bandwidthFraction, float bufferedFractionToLiveEdgeForQualityIncrease2, long minTimeBetweenBufferReevaluationMs2, Clock clock2) {
        this(group, tracks, new DefaultBandwidthProvider(bandwidthMeter, bandwidthFraction), minDurationForQualityIncreaseMs, maxDurationForQualityDecreaseMs, minDurationToRetainAfterDiscardMs, bufferedFractionToLiveEdgeForQualityIncrease2, minTimeBetweenBufferReevaluationMs2, clock2);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    private AdaptiveTrackSelection(TrackGroup group, int[] tracks, BandwidthProvider bandwidthProvider2, long minDurationForQualityIncreaseMs, long maxDurationForQualityDecreaseMs, long minDurationToRetainAfterDiscardMs, float bufferedFractionToLiveEdgeForQualityIncrease2, long minTimeBetweenBufferReevaluationMs2, Clock clock2) {
        super(group, tracks);
        this.bandwidthProvider = bandwidthProvider2;
        this.minDurationForQualityIncreaseUs = minDurationForQualityIncreaseMs * 1000;
        this.maxDurationForQualityDecreaseUs = maxDurationForQualityDecreaseMs * 1000;
        this.minDurationToRetainAfterDiscardUs = 1000 * minDurationToRetainAfterDiscardMs;
        this.bufferedFractionToLiveEdgeForQualityIncrease = bufferedFractionToLiveEdgeForQualityIncrease2;
        this.minTimeBetweenBufferReevaluationMs = minTimeBetweenBufferReevaluationMs2;
        this.clock = clock2;
        this.playbackSpeed = 1.0f;
        this.reason = 0;
        this.lastBufferEvaluationMs = C.TIME_UNSET;
        this.trackBitrateEstimator = TrackBitrateEstimator.DEFAULT;
        this.formats = new Format[this.length];
        this.formatBitrates = new int[this.length];
        this.trackBitrates = new int[this.length];
        for (int i = 0; i < this.length; i++) {
            Format format = getFormat(i);
            Format[] formatArr = this.formats;
            formatArr[i] = format;
            this.formatBitrates[i] = formatArr[i].bitrate;
        }
    }

    public void experimental_setTrackBitrateEstimator(TrackBitrateEstimator trackBitrateEstimator2) {
        this.trackBitrateEstimator = trackBitrateEstimator2;
    }

    public void experimental_setNonAllocatableBandwidth(long nonAllocatableBandwidth) {
        ((DefaultBandwidthProvider) this.bandwidthProvider).experimental_setNonAllocatableBandwidth(nonAllocatableBandwidth);
    }

    public void enable() {
        this.lastBufferEvaluationMs = C.TIME_UNSET;
    }

    public void onPlaybackSpeed(float playbackSpeed2) {
        this.playbackSpeed = playbackSpeed2;
    }

    public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> queue, MediaChunkIterator[] mediaChunkIterators) {
        long nowMs = this.clock.elapsedRealtime();
        this.trackBitrateEstimator.getBitrates(this.formats, queue, mediaChunkIterators, this.trackBitrates);
        if (this.reason == 0) {
            this.reason = 1;
            this.selectedIndex = determineIdealSelectedIndex(nowMs, this.trackBitrates);
            return;
        }
        int currentSelectedIndex = this.selectedIndex;
        int determineIdealSelectedIndex = determineIdealSelectedIndex(nowMs, this.trackBitrates);
        this.selectedIndex = determineIdealSelectedIndex;
        if (determineIdealSelectedIndex != currentSelectedIndex) {
            if (!isBlacklisted(currentSelectedIndex, nowMs)) {
                Format currentFormat = getFormat(currentSelectedIndex);
                Format selectedFormat = getFormat(this.selectedIndex);
                if (selectedFormat.bitrate <= currentFormat.bitrate) {
                    long j = availableDurationUs;
                } else if (bufferedDurationUs < minDurationForQualityIncreaseUs(availableDurationUs)) {
                    this.selectedIndex = currentSelectedIndex;
                }
                if (selectedFormat.bitrate < currentFormat.bitrate && bufferedDurationUs >= this.maxDurationForQualityDecreaseUs) {
                    this.selectedIndex = currentSelectedIndex;
                }
            } else {
                long j2 = availableDurationUs;
            }
            if (this.selectedIndex != currentSelectedIndex) {
                this.reason = 3;
            }
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public int getSelectionReason() {
        return this.reason;
    }

    public Object getSelectionData() {
        return null;
    }

    public int evaluateQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        AdaptiveTrackSelection adaptiveTrackSelection = this;
        List<? extends MediaChunk> list = queue;
        long nowMs = adaptiveTrackSelection.clock.elapsedRealtime();
        if (!adaptiveTrackSelection.shouldEvaluateQueueSize(nowMs)) {
            return queue.size();
        }
        adaptiveTrackSelection.lastBufferEvaluationMs = nowMs;
        if (queue.isEmpty()) {
            return 0;
        }
        int queueSize = queue.size();
        long playoutBufferedDurationBeforeLastChunkUs = Util.getPlayoutDurationForMediaDuration(((MediaChunk) list.get(queueSize - 1)).startTimeUs - playbackPositionUs, adaptiveTrackSelection.playbackSpeed);
        long minDurationToRetainAfterDiscardUs2 = getMinDurationToRetainAfterDiscardUs();
        if (playoutBufferedDurationBeforeLastChunkUs < minDurationToRetainAfterDiscardUs2) {
            return queueSize;
        }
        Format idealFormat = adaptiveTrackSelection.getFormat(adaptiveTrackSelection.determineIdealSelectedIndex(nowMs, adaptiveTrackSelection.formatBitrates));
        int i = 0;
        while (i < queueSize) {
            MediaChunk chunk = (MediaChunk) list.get(i);
            Format format = chunk.trackFormat;
            long nowMs2 = nowMs;
            if (Util.getPlayoutDurationForMediaDuration(chunk.startTimeUs - playbackPositionUs, adaptiveTrackSelection.playbackSpeed) >= minDurationToRetainAfterDiscardUs2 && format.bitrate < idealFormat.bitrate && format.height != -1 && format.height < 720 && format.width != -1 && format.width < 1280 && format.height < idealFormat.height) {
                return i;
            }
            i++;
            adaptiveTrackSelection = this;
            list = queue;
            nowMs = nowMs2;
        }
        return queueSize;
    }

    /* access modifiers changed from: protected */
    public boolean canSelectFormat(Format format, int trackBitrate, float playbackSpeed2, long effectiveBitrate) {
        return ((long) Math.round(((float) trackBitrate) * playbackSpeed2)) <= effectiveBitrate;
    }

    /* access modifiers changed from: protected */
    public boolean shouldEvaluateQueueSize(long nowMs) {
        long j = this.lastBufferEvaluationMs;
        return j == C.TIME_UNSET || nowMs - j >= this.minTimeBetweenBufferReevaluationMs;
    }

    /* access modifiers changed from: protected */
    public long getMinDurationToRetainAfterDiscardUs() {
        return this.minDurationToRetainAfterDiscardUs;
    }

    private int determineIdealSelectedIndex(long nowMs, int[] trackBitrates2) {
        long effectiveBitrate = this.bandwidthProvider.getAllocatedBandwidth();
        int lowestBitrateNonBlacklistedIndex = 0;
        for (int i = 0; i < this.length; i++) {
            if (nowMs == Long.MIN_VALUE || !isBlacklisted(i, nowMs)) {
                if (canSelectFormat(getFormat(i), trackBitrates2[i], this.playbackSpeed, effectiveBitrate)) {
                    return i;
                }
                lowestBitrateNonBlacklistedIndex = i;
            }
        }
        return lowestBitrateNonBlacklistedIndex;
    }

    private long minDurationForQualityIncreaseUs(long availableDurationUs) {
        return (availableDurationUs > C.TIME_UNSET ? 1 : (availableDurationUs == C.TIME_UNSET ? 0 : -1)) != 0 && (availableDurationUs > this.minDurationForQualityIncreaseUs ? 1 : (availableDurationUs == this.minDurationForQualityIncreaseUs ? 0 : -1)) <= 0 ? (long) (((float) availableDurationUs) * this.bufferedFractionToLiveEdgeForQualityIncrease) : this.minDurationForQualityIncreaseUs;
    }

    private static final class DefaultBandwidthProvider implements BandwidthProvider {
        private final float bandwidthFraction;
        private final BandwidthMeter bandwidthMeter;
        private long nonAllocatableBandwidth;

        DefaultBandwidthProvider(BandwidthMeter bandwidthMeter2, float bandwidthFraction2) {
            this.bandwidthMeter = bandwidthMeter2;
            this.bandwidthFraction = bandwidthFraction2;
        }

        public long getAllocatedBandwidth() {
            return Math.max(0, ((long) (((float) this.bandwidthMeter.getBitrateEstimate()) * this.bandwidthFraction)) - this.nonAllocatableBandwidth);
        }

        /* access modifiers changed from: package-private */
        public void experimental_setNonAllocatableBandwidth(long nonAllocatableBandwidth2) {
            this.nonAllocatableBandwidth = nonAllocatableBandwidth2;
        }
    }
}
