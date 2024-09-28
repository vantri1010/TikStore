package com.google.android.gms.internal.vision;

import java.util.Iterator;
import java.util.Map;

final class zzdd<K> implements Iterator<Map.Entry<K, Object>> {
    private Iterator<Map.Entry<K, Object>> zzmh;

    public zzdd(Iterator<Map.Entry<K, Object>> it) {
        this.zzmh = it;
    }

    public final boolean hasNext() {
        return this.zzmh.hasNext();
    }

    public final /* synthetic */ Object next() {
        Map.Entry next = this.zzmh.next();
        return next.getValue() instanceof zzda ? new zzdc(next) : next;
    }

    public final void remove() {
        this.zzmh.remove();
    }
}
