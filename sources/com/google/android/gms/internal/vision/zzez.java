package com.google.android.gms.internal.vision;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

class zzez extends AbstractSet<Map.Entry<K, V>> {
    private final /* synthetic */ zzeq zzom;

    private zzez(zzeq zzeq) {
        this.zzom = zzeq;
    }

    /* synthetic */ zzez(zzeq zzeq, zzer zzer) {
        this(zzeq);
    }

    public /* synthetic */ boolean add(Object obj) {
        Map.Entry entry = (Map.Entry) obj;
        if (contains(entry)) {
            return false;
        }
        this.zzom.put((Comparable) entry.getKey(), entry.getValue());
        return true;
    }

    public void clear() {
        this.zzom.clear();
    }

    public boolean contains(Object obj) {
        Map.Entry entry = (Map.Entry) obj;
        Object obj2 = this.zzom.get(entry.getKey());
        Object value = entry.getValue();
        if (obj2 != value) {
            return obj2 != null && obj2.equals(value);
        }
        return true;
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        return new zzey(this.zzom, (zzer) null);
    }

    public boolean remove(Object obj) {
        Map.Entry entry = (Map.Entry) obj;
        if (!contains(entry)) {
            return false;
        }
        this.zzom.remove(entry.getKey());
        return true;
    }

    public int size() {
        return this.zzom.size();
    }
}
