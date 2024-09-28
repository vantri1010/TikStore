package com.google.android.gms.internal.vision;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class zzes implements Iterator<Map.Entry<K, V>> {
    private int pos;
    private Iterator<Map.Entry<K, V>> zzol;
    private final /* synthetic */ zzeq zzom;

    private zzes(zzeq zzeq) {
        this.zzom = zzeq;
        this.pos = this.zzom.zzog.size();
    }

    /* synthetic */ zzes(zzeq zzeq, zzer zzer) {
        this(zzeq);
    }

    private final Iterator<Map.Entry<K, V>> zzdq() {
        if (this.zzol == null) {
            this.zzol = this.zzom.zzoj.entrySet().iterator();
        }
        return this.zzol;
    }

    public final boolean hasNext() {
        int i = this.pos;
        return (i > 0 && i <= this.zzom.zzog.size()) || zzdq().hasNext();
    }

    public final /* synthetic */ Object next() {
        Object obj;
        if (zzdq().hasNext()) {
            obj = zzdq().next();
        } else {
            List zzb = this.zzom.zzog;
            int i = this.pos - 1;
            this.pos = i;
            obj = zzb.get(i);
        }
        return (Map.Entry) obj;
    }

    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
