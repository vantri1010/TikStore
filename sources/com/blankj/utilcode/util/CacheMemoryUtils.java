package com.blankj.utilcode.util;

import androidx.collection.LruCache;
import com.blankj.utilcode.constant.CacheConstants;
import java.util.HashMap;
import java.util.Map;

public final class CacheMemoryUtils implements CacheConstants {
    private static final Map<String, CacheMemoryUtils> CACHE_MAP = new HashMap();
    private static final int DEFAULT_MAX_COUNT = 256;
    private final String mCacheKey;
    private final LruCache<String, CacheValue> mMemoryCache;

    public static CacheMemoryUtils getInstance() {
        return getInstance(256);
    }

    public static CacheMemoryUtils getInstance(int maxCount) {
        return getInstance(String.valueOf(maxCount), maxCount);
    }

    public static CacheMemoryUtils getInstance(String cacheKey, int maxCount) {
        CacheMemoryUtils cache = CACHE_MAP.get(cacheKey);
        if (cache == null) {
            synchronized (CacheMemoryUtils.class) {
                cache = CACHE_MAP.get(cacheKey);
                if (cache == null) {
                    cache = new CacheMemoryUtils(cacheKey, new LruCache(maxCount));
                    CACHE_MAP.put(cacheKey, cache);
                }
            }
        }
        return cache;
    }

    private CacheMemoryUtils(String cacheKey, LruCache<String, CacheValue> memoryCache) {
        this.mCacheKey = cacheKey;
        this.mMemoryCache = memoryCache;
    }

    public String toString() {
        return this.mCacheKey + "@" + Integer.toHexString(hashCode());
    }

    public void put(String key, Object value) {
        if (key != null) {
            put(key, value, -1);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Object value, int saveTime) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (value != null) {
            this.mMemoryCache.put(key, new CacheValue(saveTime < 0 ? -1 : System.currentTimeMillis() + ((long) (saveTime * 1000)), value));
        }
    }

    public <T> T get(String key) {
        if (key != null) {
            return get(key, (Object) null);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public <T> T get(String key, T defaultValue) {
        if (key != null) {
            CacheValue val = this.mMemoryCache.get(key);
            if (val == null) {
                return defaultValue;
            }
            if (val.dueTime == -1 || val.dueTime >= System.currentTimeMillis()) {
                return val.value;
            }
            this.mMemoryCache.remove(key);
            return defaultValue;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public int getCacheCount() {
        return this.mMemoryCache.size();
    }

    public Object remove(String key) {
        if (key != null) {
            CacheValue remove = this.mMemoryCache.remove(key);
            if (remove == null) {
                return null;
            }
            return remove.value;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void clear() {
        this.mMemoryCache.evictAll();
    }

    private static final class CacheValue {
        long dueTime;
        Object value;

        CacheValue(long dueTime2, Object value2) {
            this.dueTime = dueTime2;
            this.value = value2;
        }
    }
}
