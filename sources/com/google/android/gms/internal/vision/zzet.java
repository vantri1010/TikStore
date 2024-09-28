package com.google.android.gms.internal.vision;

import java.util.Iterator;
import java.util.Map;

final class zzet extends zzez {
    private final /* synthetic */ zzeq zzom;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    private zzet(zzeq zzeq) {
        super(zzeq, (zzer) null);
        this.zzom = zzeq;
    }

    /* synthetic */ zzet(zzeq zzeq, zzer zzer) {
        this(zzeq);
    }

    public final Iterator<Map.Entry<K, V>> iterator() {
        return new zzes(this.zzom, (zzer) null);
    }
}
