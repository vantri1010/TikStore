package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import com.blankj.utilcode.constant.CacheConstants;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public final class CacheDoubleUtils implements CacheConstants {
    private static final Map<String, CacheDoubleUtils> CACHE_MAP = new HashMap();
    private final CacheDiskUtils mCacheDiskUtils;
    private final CacheMemoryUtils mCacheMemoryUtils;

    public static CacheDoubleUtils getInstance() {
        return getInstance(CacheMemoryUtils.getInstance(), CacheDiskUtils.getInstance());
    }

    public static CacheDoubleUtils getInstance(CacheMemoryUtils cacheMemoryUtils, CacheDiskUtils cacheDiskUtils) {
        if (cacheMemoryUtils == null) {
            throw new NullPointerException("Argument 'cacheMemoryUtils' of type CacheMemoryUtils (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            String cacheKey = cacheDiskUtils.toString() + "_" + cacheMemoryUtils.toString();
            CacheDoubleUtils cache = CACHE_MAP.get(cacheKey);
            if (cache == null) {
                synchronized (CacheDoubleUtils.class) {
                    cache = CACHE_MAP.get(cacheKey);
                    if (cache == null) {
                        cache = new CacheDoubleUtils(cacheMemoryUtils, cacheDiskUtils);
                        CACHE_MAP.put(cacheKey, cache);
                    }
                }
            }
            return cache;
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    private CacheDoubleUtils(CacheMemoryUtils cacheMemoryUtils, CacheDiskUtils cacheUtils) {
        this.mCacheMemoryUtils = cacheMemoryUtils;
        this.mCacheDiskUtils = cacheUtils;
    }

    public void put(String key, byte[] value) {
        if (key != null) {
            put(key, value, -1);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, byte[] value, int saveTime) {
        if (key != null) {
            this.mCacheMemoryUtils.put(key, value, saveTime);
            this.mCacheDiskUtils.put(key, value, saveTime);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public byte[] getBytes(String key) {
        if (key != null) {
            return getBytes(key, (byte[]) null);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public byte[] getBytes(String key, byte[] defaultValue) {
        if (key != null) {
            byte[] obj = (byte[]) this.mCacheMemoryUtils.get(key);
            if (obj != null) {
                return obj;
            }
            return this.mCacheDiskUtils.getBytes(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, String value) {
        if (key != null) {
            put(key, value, -1);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, String value, int saveTime) {
        if (key != null) {
            this.mCacheMemoryUtils.put(key, value, saveTime);
            this.mCacheDiskUtils.put(key, value, saveTime);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public String getString(String key) {
        if (key != null) {
            return getString(key, (String) null);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public String getString(String key, String defaultValue) {
        if (key != null) {
            String obj = (String) this.mCacheMemoryUtils.get(key);
            if (obj != null) {
                return obj;
            }
            return this.mCacheDiskUtils.getString(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, JSONObject value) {
        if (key != null) {
            put(key, value, -1);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, JSONObject value, int saveTime) {
        if (key != null) {
            this.mCacheMemoryUtils.put(key, value, saveTime);
            this.mCacheDiskUtils.put(key, value, saveTime);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public JSONObject getJSONObject(String key) {
        if (key != null) {
            return getJSONObject(key, (JSONObject) null);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public JSONObject getJSONObject(String key, JSONObject defaultValue) {
        if (key != null) {
            JSONObject obj = (JSONObject) this.mCacheMemoryUtils.get(key);
            if (obj != null) {
                return obj;
            }
            return this.mCacheDiskUtils.getJSONObject(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, JSONArray value) {
        if (key != null) {
            put(key, value, -1);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, JSONArray value, int saveTime) {
        if (key != null) {
            this.mCacheMemoryUtils.put(key, value, saveTime);
            this.mCacheDiskUtils.put(key, value, saveTime);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public JSONArray getJSONArray(String key) {
        if (key != null) {
            return getJSONArray(key, (JSONArray) null);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public JSONArray getJSONArray(String key, JSONArray defaultValue) {
        if (key != null) {
            JSONArray obj = (JSONArray) this.mCacheMemoryUtils.get(key);
            if (obj != null) {
                return obj;
            }
            return this.mCacheDiskUtils.getJSONArray(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Bitmap value) {
        if (key != null) {
            put(key, value, -1);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Bitmap value, int saveTime) {
        if (key != null) {
            this.mCacheMemoryUtils.put(key, value, saveTime);
            this.mCacheDiskUtils.put(key, value, saveTime);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public Bitmap getBitmap(String key) {
        if (key != null) {
            return getBitmap(key, (Bitmap) null);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public Bitmap getBitmap(String key, Bitmap defaultValue) {
        if (key != null) {
            Bitmap obj = (Bitmap) this.mCacheMemoryUtils.get(key);
            if (obj != null) {
                return obj;
            }
            return this.mCacheDiskUtils.getBitmap(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Drawable value) {
        if (key != null) {
            put(key, value, -1);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Drawable value, int saveTime) {
        if (key != null) {
            this.mCacheMemoryUtils.put(key, value, saveTime);
            this.mCacheDiskUtils.put(key, value, saveTime);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public Drawable getDrawable(String key) {
        if (key != null) {
            return getDrawable(key, (Drawable) null);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public Drawable getDrawable(String key, Drawable defaultValue) {
        if (key != null) {
            Drawable obj = (Drawable) this.mCacheMemoryUtils.get(key);
            if (obj != null) {
                return obj;
            }
            return this.mCacheDiskUtils.getDrawable(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Parcelable value) {
        if (key != null) {
            put(key, value, -1);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Parcelable value, int saveTime) {
        if (key != null) {
            this.mCacheMemoryUtils.put(key, value, saveTime);
            this.mCacheDiskUtils.put(key, value, saveTime);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public <T> T getParcelable(String key, Parcelable.Creator<T> creator) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator != null) {
            return getParcelable(key, creator, (Object) null);
        } else {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public <T> T getParcelable(String key, Parcelable.Creator<T> creator, T defaultValue) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator != null) {
            T value = this.mCacheMemoryUtils.get(key);
            if (value != null) {
                return value;
            }
            return this.mCacheDiskUtils.getParcelable(key, creator, defaultValue);
        } else {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public void put(String key, Serializable value) {
        if (key != null) {
            put(key, value, -1);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Serializable value, int saveTime) {
        if (key != null) {
            this.mCacheMemoryUtils.put(key, value, saveTime);
            this.mCacheDiskUtils.put(key, value, saveTime);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public Object getSerializable(String key) {
        if (key != null) {
            return getSerializable(key, (Object) null);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public Object getSerializable(String key, Object defaultValue) {
        if (key != null) {
            Object obj = this.mCacheMemoryUtils.get(key);
            if (obj != null) {
                return obj;
            }
            return this.mCacheDiskUtils.getSerializable(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public long getCacheDiskSize() {
        return this.mCacheDiskUtils.getCacheSize();
    }

    public int getCacheDiskCount() {
        return this.mCacheDiskUtils.getCacheCount();
    }

    public int getCacheMemoryCount() {
        return this.mCacheMemoryUtils.getCacheCount();
    }

    public void remove(String key) {
        if (key != null) {
            this.mCacheMemoryUtils.remove(key);
            this.mCacheDiskUtils.remove(key);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void clear() {
        this.mCacheMemoryUtils.clear();
        this.mCacheDiskUtils.clear();
    }
}
