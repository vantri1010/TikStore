package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONObject;

public final class CacheDoubleStaticUtils {
    private static CacheDoubleUtils sDefaultCacheDoubleUtils;

    public static void setDefaultCacheDoubleUtils(CacheDoubleUtils cacheDoubleUtils) {
        sDefaultCacheDoubleUtils = cacheDoubleUtils;
    }

    public static void put(String key, byte[] value) {
        if (key != null) {
            put(key, value, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, byte[] value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static byte[] getBytes(String key) {
        if (key != null) {
            return getBytes(key, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static byte[] getBytes(String key, byte[] defaultValue) {
        if (key != null) {
            return getBytes(key, defaultValue, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, String value) {
        if (key != null) {
            put(key, value, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, String value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getString(String key) {
        if (key != null) {
            return getString(key, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getString(String key, String defaultValue) {
        if (key != null) {
            return getString(key, defaultValue, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, JSONObject value) {
        if (key != null) {
            put(key, value, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, JSONObject value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static JSONObject getJSONObject(String key) {
        if (key != null) {
            return getJSONObject(key, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static JSONObject getJSONObject(String key, JSONObject defaultValue) {
        if (key != null) {
            return getJSONObject(key, defaultValue, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, JSONArray value) {
        if (key != null) {
            put(key, value, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, JSONArray value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static JSONArray getJSONArray(String key) {
        if (key != null) {
            return getJSONArray(key, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static JSONArray getJSONArray(String key, JSONArray defaultValue) {
        if (key != null) {
            return getJSONArray(key, defaultValue, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Bitmap value) {
        if (key != null) {
            put(key, value, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Bitmap value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Bitmap getBitmap(String key) {
        if (key != null) {
            return getBitmap(key, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Bitmap getBitmap(String key, Bitmap defaultValue) {
        if (key != null) {
            return getBitmap(key, defaultValue, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Drawable value) {
        if (key != null) {
            put(key, value, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Drawable value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Drawable getDrawable(String key) {
        if (key != null) {
            return getDrawable(key, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Drawable getDrawable(String key, Drawable defaultValue) {
        if (key != null) {
            return getDrawable(key, defaultValue, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Parcelable value) {
        if (key != null) {
            put(key, value, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Parcelable value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static <T> T getParcelable(String key, Parcelable.Creator<T> creator) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator != null) {
            return getParcelable(key, creator, getDefaultCacheDoubleUtils());
        } else {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static <T> T getParcelable(String key, Parcelable.Creator<T> creator, T defaultValue) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator != null) {
            return getParcelable(key, creator, defaultValue, getDefaultCacheDoubleUtils());
        } else {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Serializable value) {
        if (key != null) {
            put(key, value, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Serializable value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Object getSerializable(String key) {
        if (key != null) {
            return getSerializable(key, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Object getSerializable(String key, Object defaultValue) {
        if (key != null) {
            return getSerializable(key, defaultValue, getDefaultCacheDoubleUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long getCacheDiskSize() {
        return getCacheDiskSize(getDefaultCacheDoubleUtils());
    }

    public static int getCacheDiskCount() {
        return getCacheDiskCount(getDefaultCacheDoubleUtils());
    }

    public static int getCacheMemoryCount() {
        return getCacheMemoryCount(getDefaultCacheDoubleUtils());
    }

    public static void remove(String key) {
        if (key != null) {
            remove(key, getDefaultCacheDoubleUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void clear() {
        clear(getDefaultCacheDoubleUtils());
    }

    public static void put(String key, byte[] value, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, byte[] value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static byte[] getBytes(String key, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getBytes(key);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static byte[] getBytes(String key, byte[] defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getBytes(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, String value, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, String value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getString(String key, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getString(key);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getString(String key, String defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getString(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, JSONObject value, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, JSONObject value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static JSONObject getJSONObject(String key, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getJSONObject(key);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static JSONObject getJSONObject(String key, JSONObject defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getJSONObject(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, JSONArray value, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, JSONArray value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static JSONArray getJSONArray(String key, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getJSONArray(key);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static JSONArray getJSONArray(String key, JSONArray defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getJSONArray(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Bitmap value, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Bitmap value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Bitmap getBitmap(String key, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getBitmap(key);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Bitmap getBitmap(String key, Bitmap defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getBitmap(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Drawable value, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Drawable value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Drawable getDrawable(String key, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getDrawable(key);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Drawable getDrawable(String key, Drawable defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getDrawable(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Parcelable value, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Parcelable value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static <T> T getParcelable(String key, Parcelable.Creator<T> creator, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator == null) {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getParcelable(key, creator);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static <T> T getParcelable(String key, Parcelable.Creator<T> creator, T defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator == null) {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getParcelable(key, creator, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Serializable value, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Serializable value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Object getSerializable(String key, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getSerializable(key);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Object getSerializable(String key, Object defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getSerializable(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static long getCacheDiskSize(CacheDoubleUtils cacheDoubleUtils) {
        if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getCacheDiskSize();
        }
        throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getCacheDiskCount(CacheDoubleUtils cacheDoubleUtils) {
        if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getCacheDiskCount();
        }
        throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getCacheMemoryCount(CacheDoubleUtils cacheDoubleUtils) {
        if (cacheDoubleUtils != null) {
            return cacheDoubleUtils.getCacheMemoryCount();
        }
        throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void remove(String key, CacheDoubleUtils cacheDoubleUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDoubleUtils != null) {
            cacheDoubleUtils.remove(key);
        } else {
            throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void clear(CacheDoubleUtils cacheDoubleUtils) {
        if (cacheDoubleUtils != null) {
            cacheDoubleUtils.clear();
            return;
        }
        throw new NullPointerException("Argument 'cacheDoubleUtils' of type CacheDoubleUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static CacheDoubleUtils getDefaultCacheDoubleUtils() {
        CacheDoubleUtils cacheDoubleUtils = sDefaultCacheDoubleUtils;
        return cacheDoubleUtils != null ? cacheDoubleUtils : CacheDoubleUtils.getInstance();
    }
}
