package com.blankj.utilcode.util;

public final class CacheMemoryStaticUtils {
    private static CacheMemoryUtils sDefaultCacheMemoryUtils;

    public static void setDefaultCacheMemoryUtils(CacheMemoryUtils cacheMemoryUtils) {
        sDefaultCacheMemoryUtils = cacheMemoryUtils;
    }

    public static void put(String key, Object value) {
        if (key != null) {
            put(key, value, getDefaultCacheMemoryUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Object value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheMemoryUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static <T> T get(String key) {
        if (key != null) {
            return get(key, getDefaultCacheMemoryUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static <T> T get(String key, T defaultValue) {
        if (key != null) {
            return get(key, defaultValue, getDefaultCacheMemoryUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getCacheCount() {
        return getCacheCount(getDefaultCacheMemoryUtils());
    }

    public static Object remove(String key) {
        if (key != null) {
            return remove(key, getDefaultCacheMemoryUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void clear() {
        clear(getDefaultCacheMemoryUtils());
    }

    public static void put(String key, Object value, CacheMemoryUtils cacheMemoryUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheMemoryUtils != null) {
            cacheMemoryUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheMemoryUtils' of type CacheMemoryUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Object value, int saveTime, CacheMemoryUtils cacheMemoryUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheMemoryUtils != null) {
            cacheMemoryUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheMemoryUtils' of type CacheMemoryUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static <T> T get(String key, CacheMemoryUtils cacheMemoryUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheMemoryUtils != null) {
            return cacheMemoryUtils.get(key);
        } else {
            throw new NullPointerException("Argument 'cacheMemoryUtils' of type CacheMemoryUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static <T> T get(String key, T defaultValue, CacheMemoryUtils cacheMemoryUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheMemoryUtils != null) {
            return cacheMemoryUtils.get(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheMemoryUtils' of type CacheMemoryUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static int getCacheCount(CacheMemoryUtils cacheMemoryUtils) {
        if (cacheMemoryUtils != null) {
            return cacheMemoryUtils.getCacheCount();
        }
        throw new NullPointerException("Argument 'cacheMemoryUtils' of type CacheMemoryUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Object remove(String key, CacheMemoryUtils cacheMemoryUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheMemoryUtils != null) {
            return cacheMemoryUtils.remove(key);
        } else {
            throw new NullPointerException("Argument 'cacheMemoryUtils' of type CacheMemoryUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void clear(CacheMemoryUtils cacheMemoryUtils) {
        if (cacheMemoryUtils != null) {
            cacheMemoryUtils.clear();
            return;
        }
        throw new NullPointerException("Argument 'cacheMemoryUtils' of type CacheMemoryUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static CacheMemoryUtils getDefaultCacheMemoryUtils() {
        CacheMemoryUtils cacheMemoryUtils = sDefaultCacheMemoryUtils;
        return cacheMemoryUtils != null ? cacheMemoryUtils : CacheMemoryUtils.getInstance();
    }
}
