package com.google.android.gms.internal.vision;

import java.util.Iterator;
import java.util.Map;

final class zzey implements Iterator<Map.Entry<K, V>> {
    private int pos;
    private Iterator<Map.Entry<K, V>> zzol;
    private final /* synthetic */ zzeq zzom;
    private boolean zzoq;

    private zzey(zzeq zzeq) {
        this.zzom = zzeq;
        this.pos = -1;
    }

    /* synthetic */ zzey(zzeq zzeq, zzer zzer) {
        this(zzeq);
    }

    private final Iterator<Map.Entry<K, V>> zzdq() {
        if (this.zzol == null) {
            this.zzol = this.zzom.zzoh.entrySet().iterator();
        }
        return this.zzol;
    }

    public final boolean hasNext() {
        return this.pos + 1 < this.zzom.zzog.size() || (!this.zzom.zzoh.isEmpty() && zzdq().hasNext());
    }

    public final /* synthetic */ Object next() {
        this.zzoq = true;
        int i = this.pos + 1;
        this.pos = i;
        return (Map.Entry) (i < this.zzom.zzog.size() ? this.zzom.zzog.get(this.pos) : zzdq().next());
    }

    public final void remove() {
        if (this.zzoq) {
            this.zzoq = false;
            this.zzom.zzdo();
            if (this.pos < this.zzom.zzog.size()) {
                zzeq zzeq = this.zzom;
                int i = this.pos;
                this.pos = i - 1;
                Object unused = zzeq.zzao(i);
                return;
            }
            zzdq().remove();
            return;
        }
        throw new IllegalStateException("remove() was called before next()");
    }
}
