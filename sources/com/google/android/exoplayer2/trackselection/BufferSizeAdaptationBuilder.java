package com.google.android.exoplayer2.trackselection;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.BufferSizeAdaptationBuilder;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import java.util.List;

public final class BufferSizeAdaptationBuilder {
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
    public static final int DEFAULT_HYSTERESIS_BUFFER_MS = 5000;
    public static final int DEFAULT_MAX_BUFFER_MS = 50000;
    public static final int DEFAULT_MIN_BUFFER_MS = 15000;
    public static final float DEFAULT_START_UP_BANDWIDTH_FRACTION = 0.75f;
    public static final int DEFAULT_START_UP_MIN_BUFFER_FOR_QUALITY_INCREASE_MS = 10000;
    private DefaultAllocator allocator;
    private int bufferForPlaybackAfterRebufferMs = 5000;
    private int bufferForPlaybackMs = 2500;
    boolean buildCalled;
    /* access modifiers changed from: private */
    public Clock clock = Clock.DEFAULT;
    /* access modifiers changed from: private */
    public DynamicFormatFilter dynamicFormatFilter = DynamicFormatFilter.NO_FILTER;
    /* access modifiers changed from: private */
    public int hysteresisBufferMs = 5000;
    /* access modifiers changed from: private */
    public int maxBufferMs = 50000;
    /* access modifiers changed from: private */
    public int minBufferMs = 15000;
    private PriorityTaskManager priorityTaskManager;
    /* access modifiers changed from: private */
    public float startUpBandwidthFraction = 0.75f;
    /* access modifiers changed from: private */
    public int startUpMinBufferForQualityIncreaseMs = 10000;

    public interface DynamicFormatFilter {
        public static final DynamicFormatFilter NO_FILTER = $$Lambda$BufferSizeAdaptationBuilder$DynamicFormatFilter$hnu1HCrmBtPvCKLt3AEEwgWv3M.INSTANCE;

        boolean isFormatAllowed(Format format, int i);

        /* renamed from: com.google.android.exoplayer2.trackselection.BufferSizeAdaptationBuilder$DynamicFormatFilter$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static /* synthetic */ boolean lambda$static$0(Format format, int trackBitrate) {
                return true;
            }
        }
    }

    public BufferSizeAdaptationBuilder setClock(Clock clock2) {
        Assertions.checkState(!this.buildCalled);
        this.clock = clock2;
        return this;
    }

    public BufferSizeAdaptationBuilder setAllocator(DefaultAllocator allocator2) {
        Assertions.checkState(!this.buildCalled);
        this.allocator = allocator2;
        return this;
    }

    public BufferSizeAdaptationBuilder setBufferDurationsMs(int minBufferMs2, int maxBufferMs2, int bufferForPlaybackMs2, int bufferForPlaybackAfterRebufferMs2) {
        Assertions.checkState(!this.buildCalled);
        this.minBufferMs = minBufferMs2;
        this.maxBufferMs = maxBufferMs2;
        this.bufferForPlaybackMs = bufferForPlaybackMs2;
        this.bufferForPlaybackAfterRebufferMs = bufferForPlaybackAfterRebufferMs2;
        return this;
    }

    public BufferSizeAdaptationBuilder setHysteresisBufferMs(int hysteresisBufferMs2) {
        Assertions.checkState(!this.buildCalled);
        this.hysteresisBufferMs = hysteresisBufferMs2;
        return this;
    }

    public BufferSizeAdaptationBuilder setStartUpTrackSelectionParameters(float bandwidthFraction, int minBufferForQualityIncreaseMs) {
        Assertions.checkState(!this.buildCalled);
        this.startUpBandwidthFraction = bandwidthFraction;
        this.startUpMinBufferForQualityIncreaseMs = minBufferForQualityIncreaseMs;
        return this;
    }

    public BufferSizeAdaptationBuilder setPriorityTaskManager(PriorityTaskManager priorityTaskManager2) {
        Assertions.checkState(!this.buildCalled);
        this.priorityTaskManager = priorityTaskManager2;
        return this;
    }

    public BufferSizeAdaptationBuilder setDynamicFormatFilter(DynamicFormatFilter dynamicFormatFilter2) {
        Assertions.checkState(!this.buildCalled);
        this.dynamicFormatFilter = dynamicFormatFilter2;
        return this;
    }

    public Pair<TrackSelection.Factory, LoadControl> buildPlayerComponents() {
        Assertions.checkArgument(this.hysteresisBufferMs < this.maxBufferMs - this.minBufferMs);
        Assertions.checkState(!this.buildCalled);
        this.buildCalled = true;
        DefaultLoadControl.Builder targetBufferBytes = new DefaultLoadControl.Builder().setTargetBufferBytes(Integer.MAX_VALUE);
        int i = this.maxBufferMs;
        DefaultLoadControl.Builder loadControlBuilder = targetBufferBytes.setBufferDurationsMs(i, i, this.bufferForPlaybackMs, this.bufferForPlaybackAfterRebufferMs);
        PriorityTaskManager priorityTaskManager2 = this.priorityTaskManager;
        if (priorityTaskManager2 != null) {
            loadControlBuilder.setPriorityTaskManager(priorityTaskManager2);
        }
        DefaultAllocator defaultAllocator = this.allocator;
        if (defaultAllocator != null) {
            loadControlBuilder.setAllocator(defaultAllocator);
        }
        return Pair.create(new TrackSelection.Factory() {
            @Deprecated
            public /* synthetic */ TrackSelection createTrackSelection(TrackGroup trackGroup, BandwidthMeter bandwidthMeter, int... iArr) {
                return TrackSelection.Factory.CC.$default$createTrackSelection(this, trackGroup, bandwidthMeter, iArr);
            }

            public TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitions, BandwidthMeter bandwidthMeter) {
                return TrackSelectionUtil.createTrackSelectionsForDefinitions(definitions, new TrackSelectionUtil.AdaptiveTrackSelectionFactory(bandwidthMeter) {
                    private final /* synthetic */ BandwidthMeter f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final TrackSelection createAdaptiveTrackSelection(TrackSelection.Definition definition) {
                        return BufferSizeAdaptationBuilder.AnonymousClass1.this.lambda$createTrackSelections$0$BufferSizeAdaptationBuilder$1(this.f$1, definition);
                    }
                });
            }

            public /* synthetic */ TrackSelection lambda$createTrackSelections$0$BufferSizeAdaptationBuilder$1(BandwidthMeter bandwidthMeter, TrackSelection.Definition definition) {
                return new BufferSizeAdaptiveTrackSelection(definition.group, definition.tracks, bandwidthMeter, BufferSizeAdaptationBuilder.this.minBufferMs, BufferSizeAdaptationBuilder.this.maxBufferMs, BufferSizeAdaptationBuilder.this.hysteresisBufferMs, BufferSizeAdaptationBuilder.this.startUpBandwidthFraction, BufferSizeAdaptationBuilder.this.startUpMinBufferForQualityIncreaseMs, BufferSizeAdaptationBuilder.this.dynamicFormatFilter, BufferSizeAdaptationBuilder.this.clock);
            }
        }, loadControlBuilder.createDefaultLoadControl());
    }

    private static final class BufferSizeAdaptiveTrackSelection extends BaseTrackSelection {
        private static final int BITRATE_BLACKLISTED = -1;
        private final BandwidthMeter bandwidthMeter;
        private final double bitrateToBufferFunctionIntercept;
        private final double bitrateToBufferFunctionSlope;
        private final Clock clock;
        private final DynamicFormatFilter dynamicFormatFilter;
        private final int[] formatBitrates;
        private final long hysteresisBufferUs;
        private boolean isInSteadyState;
        private final int maxBitrate;
        private final long maxBufferUs;
        private final int minBitrate;
        private final long minBufferUs;
        private float playbackSpeed;
        private int selectedIndex;
        private int selectionReason;
        private final float startUpBandwidthFraction;
        private final long startUpMinBufferForQualityIncreaseUs;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        private BufferSizeAdaptiveTrackSelection(TrackGroup trackGroup, int[] tracks, BandwidthMeter bandwidthMeter2, int minBufferMs, int maxBufferMs, int hysteresisBufferMs, float startUpBandwidthFraction2, int startUpMinBufferForQualityIncreaseMs, DynamicFormatFilter dynamicFormatFilter2, Clock clock2) {
            super(trackGroup, tracks);
            this.bandwidthMeter = bandwidthMeter2;
            this.minBufferUs = C.msToUs((long) minBufferMs);
            this.maxBufferUs = C.msToUs((long) maxBufferMs);
            this.hysteresisBufferUs = C.msToUs((long) hysteresisBufferMs);
            this.startUpBandwidthFraction = startUpBandwidthFraction2;
            this.startUpMinBufferForQualityIncreaseUs = C.msToUs((long) startUpMinBufferForQualityIncreaseMs);
            this.dynamicFormatFilter = dynamicFormatFilter2;
            this.clock = clock2;
            this.formatBitrates = new int[this.length];
            this.maxBitrate = getFormat(0).bitrate;
            int i = getFormat(this.length - 1).bitrate;
            this.minBitrate = i;
            this.selectionReason = 1;
            this.playbackSpeed = 1.0f;
            double log = ((double) ((this.maxBufferUs - this.hysteresisBufferUs) - this.minBufferUs)) / Math.log((double) (this.maxBitrate / i));
            this.bitrateToBufferFunctionSlope = log;
            this.bitrateToBufferFunctionIntercept = ((double) this.minBufferUs) - (log * Math.log((double) this.minBitrate));
            updateFormatBitrates(Long.MIN_VALUE);
            this.selectedIndex = selectIdealIndexUsingBandwidth();
        }

        public void onPlaybackSpeed(float playbackSpeed2) {
            this.playbackSpeed = playbackSpeed2;
        }

        public void onDiscontinuity() {
            this.isInSteadyState = false;
        }

        public int getSelectedIndex() {
            return this.selectedIndex;
        }

        public int getSelectionReason() {
            return this.selectionReason;
        }

        public Object getSelectionData() {
            return null;
        }

        public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIterators) {
            updateFormatBitrates(this.clock.elapsedRealtime());
            long bufferUs = getCurrentPeriodBufferedDurationUs(playbackPositionUs, bufferedDurationUs);
            int oldSelectedIndex = this.selectedIndex;
            if (this.isInSteadyState) {
                selectIndexSteadyState(bufferUs);
            } else {
                selectIndexStartUpPhase(bufferUs);
            }
            if (this.selectedIndex != oldSelectedIndex) {
                this.selectionReason = 3;
            }
        }

        private void selectIndexSteadyState(long bufferUs) {
            if (isOutsideHysteresis(bufferUs)) {
                this.selectedIndex = selectIdealIndexUsingBufferSize(bufferUs);
            }
        }

        private boolean isOutsideHysteresis(long bufferUs) {
            int[] iArr = this.formatBitrates;
            int i = this.selectedIndex;
            if (iArr[i] != -1 && Math.abs(bufferUs - getTargetBufferForBitrateUs(iArr[i])) <= this.hysteresisBufferUs) {
                return false;
            }
            return true;
        }

        private int selectIdealIndexUsingBufferSize(long bufferUs) {
            int lowestBitrateNonBlacklistedIndex = 0;
            int i = 0;
            while (true) {
                int[] iArr = this.formatBitrates;
                if (i >= iArr.length) {
                    return lowestBitrateNonBlacklistedIndex;
                }
                if (iArr[i] != -1) {
                    if (getTargetBufferForBitrateUs(iArr[i]) < bufferUs && this.dynamicFormatFilter.isFormatAllowed(getFormat(i), this.formatBitrates[i])) {
                        return i;
                    }
                    lowestBitrateNonBlacklistedIndex = i;
                }
                i++;
            }
        }

        private void selectIndexStartUpPhase(long bufferUs) {
            int startUpSelectedIndex = selectIdealIndexUsingBandwidth();
            int steadyStateSelectedIndex = selectIdealIndexUsingBufferSize(bufferUs);
            int i = this.selectedIndex;
            if (steadyStateSelectedIndex <= i) {
                this.selectedIndex = steadyStateSelectedIndex;
                this.isInSteadyState = true;
            } else if (bufferUs >= this.startUpMinBufferForQualityIncreaseUs || startUpSelectedIndex >= i || this.formatBitrates[i] == -1) {
                this.selectedIndex = startUpSelectedIndex;
            }
        }

        private int selectIdealIndexUsingBandwidth() {
            long effectiveBitrate = (long) (((float) this.bandwidthMeter.getBitrateEstimate()) * this.startUpBandwidthFraction);
            int lowestBitrateNonBlacklistedIndex = 0;
            int i = 0;
            while (true) {
                int[] iArr = this.formatBitrates;
                if (i >= iArr.length) {
                    return lowestBitrateNonBlacklistedIndex;
                }
                if (iArr[i] != -1) {
                    if (((long) Math.round(((float) iArr[i]) * this.playbackSpeed)) <= effectiveBitrate && this.dynamicFormatFilter.isFormatAllowed(getFormat(i), this.formatBitrates[i])) {
                        return i;
                    }
                    lowestBitrateNonBlacklistedIndex = i;
                }
                i++;
            }
        }

        private void updateFormatBitrates(long nowMs) {
            for (int i = 0; i < this.length; i++) {
                if (nowMs == Long.MIN_VALUE || !isBlacklisted(i, nowMs)) {
                    this.formatBitrates[i] = getFormat(i).bitrate;
                } else {
                    this.formatBitrates[i] = -1;
                }
            }
        }

        private long getTargetBufferForBitrateUs(int bitrate) {
            if (bitrate <= this.minBitrate) {
                return this.minBufferUs;
            }
            if (bitrate >= this.maxBitrate) {
                return this.maxBufferUs - this.hysteresisBufferUs;
            }
            return (long) ((int) ((this.bitrateToBufferFunctionSlope * Math.log((double) bitrate)) + this.bitrateToBufferFunctionIntercept));
        }

        private static long getCurrentPeriodBufferedDurationUs(long playbackPositionUs, long bufferedDurationUs) {
            return playbackPositionUs >= 0 ? bufferedDurationUs : playbackPositionUs + bufferedDurationUs;
        }
    }
}
