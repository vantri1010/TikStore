package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import java.util.List;

/* renamed from: com.google.android.exoplayer2.trackselection.-$$Lambda$TrackBitrateEstimator$2lQ5lBvmOkJuNPw2qehuzXBInmI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TrackBitrateEstimator$2lQ5lBvmOkJuNPw2qehuzXBInmI implements TrackBitrateEstimator {
    public static final /* synthetic */ $$Lambda$TrackBitrateEstimator$2lQ5lBvmOkJuNPw2qehuzXBInmI INSTANCE = new $$Lambda$TrackBitrateEstimator$2lQ5lBvmOkJuNPw2qehuzXBInmI();

    private /* synthetic */ $$Lambda$TrackBitrateEstimator$2lQ5lBvmOkJuNPw2qehuzXBInmI() {
    }

    public final int[] getBitrates(Format[] formatArr, List list, MediaChunkIterator[] mediaChunkIteratorArr, int[] iArr) {
        return TrackSelectionUtil.getFormatBitrates(formatArr, iArr);
    }
}
