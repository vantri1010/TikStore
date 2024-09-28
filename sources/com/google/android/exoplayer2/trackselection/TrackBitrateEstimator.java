package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import java.util.List;

public interface TrackBitrateEstimator {
    public static final TrackBitrateEstimator DEFAULT = $$Lambda$TrackBitrateEstimator$2lQ5lBvmOkJuNPw2qehuzXBInmI.INSTANCE;

    /* renamed from: com.google.android.exoplayer2.trackselection.TrackBitrateEstimator$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
    }

    int[] getBitrates(Format[] formatArr, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIteratorArr, int[] iArr);
}
