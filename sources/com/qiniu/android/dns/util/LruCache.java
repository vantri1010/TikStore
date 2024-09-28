package com.qiniu.android.dns.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LruCache<K, V> extends LinkedHashMap<K, V> {
    private int size;

    public LruCache() {
        this(256);
    }

    public LruCache(int size2) {
        super(size2, 1.0f, true);
        this.size = size2;
    }

    /* access modifiers changed from: protected */
    public boolean removeEldestEntry(Map.Entry<K, V> entry) {
        return size() > this.size;
    }
}
