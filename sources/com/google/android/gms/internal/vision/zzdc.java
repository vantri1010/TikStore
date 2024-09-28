package com.google.android.gms.internal.vision;

import java.util.Map;

final class zzdc<K> implements Map.Entry<K, Object> {
    private Map.Entry<K, zzda> zzmg;

    private zzdc(Map.Entry<K, zzda> entry) {
        this.zzmg = entry;
    }

    public final K getKey() {
        return this.zzmg.getKey();
    }

    public final Object getValue() {
        if (this.zzmg.getValue() == null) {
            return null;
        }
        return zzda.zzci();
    }

    public final Object setValue(Object obj) {
        if (obj instanceof zzdx) {
            return this.zzmg.getValue().zzi((zzdx) obj);
        }
        throw new IllegalArgumentException("LazyField now only used for MessageSet, and the value of MessageSet must be an instance of MessageLite");
    }

    public final zzda zzcj() {
        return this.zzmg.getValue();
    }
}
