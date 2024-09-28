package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import java.util.List;

public final class FixedTrackSelection extends BaseTrackSelection {
    private final Object data;
    private final int reason;

    @Deprecated
    public static final class Factory implements TrackSelection.Factory {
        private final Object data;
        private final int reason;

        @Deprecated
        public /* synthetic */ TrackSelection createTrackSelection(TrackGroup trackGroup, BandwidthMeter bandwidthMeter, int... iArr) {
            return TrackSelection.Factory.CC.$default$createTrackSelection(this, trackGroup, bandwidthMeter, iArr);
        }

        public Factory() {
            this.reason = 0;
            this.data = null;
        }

        public Factory(int reason2, Object data2) {
            this.reason = reason2;
            this.data = data2;
        }

        public TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitions, BandwidthMeter bandwidthMeter) {
            return TrackSelectionUtil.createTrackSelectionsForDefinitions(definitions, new TrackSelectionUtil.AdaptiveTrackSelectionFactory() {
                public final TrackSelection createAdaptiveTrackSelection(TrackSelection.Definition definition) {
                    return FixedTrackSelection.Factory.this.lambda$createTrackSelections$0$FixedTrackSelection$Factory(definition);
                }
            });
        }

        public /* synthetic */ TrackSelection lambda$createTrackSelections$0$FixedTrackSelection$Factory(TrackSelection.Definition definition) {
            return new FixedTrackSelection(definition.group, definition.tracks[0], this.reason, this.data);
        }
    }

    public FixedTrackSelection(TrackGroup group, int track) {
        this(group, track, 0, (Object) null);
    }

    public FixedTrackSelection(TrackGroup group, int track, int reason2, Object data2) {
        super(group, track);
        this.reason = reason2;
        this.data = data2;
    }

    public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIterators) {
    }

    public int getSelectedIndex() {
        return 0;
    }

    public int getSelectionReason() {
        return this.reason;
    }

    public Object getSelectionData() {
        return this.data;
    }
}
