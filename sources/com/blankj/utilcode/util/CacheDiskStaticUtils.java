package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONObject;

public final class CacheDiskStaticUtils {
    private static CacheDiskUtils sDefaultCacheDiskUtils;

    public static void setDefaultCacheDiskUtils(CacheDiskUtils cacheDiskUtils) {
        sDefaultCacheDiskUtils = cacheDiskUtils;
    }

    public static void put(String key, byte[] value) {
        if (key != null) {
            put(key, value, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, byte[] value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static byte[] getBytes(String key) {
        if (key != null) {
            return getBytes(key, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static byte[] getBytes(String key, byte[] defaultValue) {
        if (key != null) {
            return getBytes(key, defaultValue, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, String value) {
        if (key != null) {
            put(key, value, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, String value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getString(String key) {
        if (key != null) {
            return getString(key, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getString(String key, String defaultValue) {
        if (key != null) {
            return getString(key, defaultValue, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, JSONObject value) {
        if (key != null) {
            put(key, value, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, JSONObject value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static JSONObject getJSONObject(String key) {
        if (key != null) {
            return getJSONObject(key, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static JSONObject getJSONObject(String key, JSONObject defaultValue) {
        if (key != null) {
            return getJSONObject(key, defaultValue, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, JSONArray value) {
        if (key != null) {
            put(key, value, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, JSONArray value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static JSONArray getJSONArray(String key) {
        if (key != null) {
            return getJSONArray(key, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static JSONArray getJSONArray(String key, JSONArray defaultValue) {
        if (key != null) {
            return getJSONArray(key, defaultValue, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Bitmap value) {
        if (key != null) {
            put(key, value, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Bitmap value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Bitmap getBitmap(String key) {
        if (key != null) {
            return getBitmap(key, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Bitmap getBitmap(String key, Bitmap defaultValue) {
        if (key != null) {
            return getBitmap(key, defaultValue, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Drawable value) {
        if (key != null) {
            put(key, value, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Drawable value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Drawable getDrawable(String key) {
        if (key != null) {
            return getDrawable(key, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Drawable getDrawable(String key, Drawable defaultValue) {
        if (key != null) {
            return getDrawable(key, defaultValue, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Parcelable value) {
        if (key != null) {
            put(key, value, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Parcelable value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static <T> T getParcelable(String key, Parcelable.Creator<T> creator) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator != null) {
            return getParcelable(key, creator, getDefaultCacheDiskUtils());
        } else {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static <T> T getParcelable(String key, Parcelable.Creator<T> creator, T defaultValue) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator != null) {
            return getParcelable(key, creator, defaultValue, getDefaultCacheDiskUtils());
        } else {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Serializable value) {
        if (key != null) {
            put(key, value, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Serializable value, int saveTime) {
        if (key != null) {
            put(key, value, saveTime, getDefaultCacheDiskUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Object getSerializable(String key) {
        if (key != null) {
            return getSerializable(key, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Object getSerializable(String key, Object defaultValue) {
        if (key != null) {
            return getSerializable(key, defaultValue, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long getCacheSize() {
        return getCacheSize(getDefaultCacheDiskUtils());
    }

    public static int getCacheCount() {
        return getCacheCount(getDefaultCacheDiskUtils());
    }

    public static boolean remove(String key) {
        if (key != null) {
            return remove(key, getDefaultCacheDiskUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean clear() {
        return clear(getDefaultCacheDiskUtils());
    }

    public static void put(String key, byte[] value, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, byte[] value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static byte[] getBytes(String key, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getBytes(key);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static byte[] getBytes(String key, byte[] defaultValue, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getBytes(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, String value, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, String value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getString(String key, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getString(key);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getString(String key, String defaultValue, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getString(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, JSONObject value, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, JSONObject value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static JSONObject getJSONObject(String key, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getJSONObject(key);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static JSONObject getJSONObject(String key, JSONObject defaultValue, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getJSONObject(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, JSONArray value, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, JSONArray value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static JSONArray getJSONArray(String key, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getJSONArray(key);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static JSONArray getJSONArray(String key, JSONArray defaultValue, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getJSONArray(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Bitmap value, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Bitmap value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Bitmap getBitmap(String key, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getBitmap(key);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Bitmap getBitmap(String key, Bitmap defaultValue, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getBitmap(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Drawable value, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Drawable value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Drawable getDrawable(String key, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getDrawable(key);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Drawable getDrawable(String key, Drawable defaultValue, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getDrawable(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Parcelable value, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Parcelable value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static <T> T getParcelable(String key, Parcelable.Creator<T> creator, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator == null) {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getParcelable(key, creator);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static <T> T getParcelable(String key, Parcelable.Creator<T> creator, T defaultValue, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (creator == null) {
            throw new NullPointerException("Argument 'creator' of type Parcelable.Creator<T> (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getParcelable(key, creator, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Serializable value, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Serializable value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            cacheDiskUtils.put(key, value, saveTime);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Object getSerializable(String key, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getSerializable(key);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Object getSerializable(String key, Object defaultValue, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.getSerializable(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static long getCacheSize(CacheDiskUtils cacheDiskUtils) {
        if (cacheDiskUtils != null) {
            return cacheDiskUtils.getCacheSize();
        }
        throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getCacheCount(CacheDiskUtils cacheDiskUtils) {
        if (cacheDiskUtils != null) {
            return cacheDiskUtils.getCacheCount();
        }
        throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean remove(String key, CacheDiskUtils cacheDiskUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cacheDiskUtils != null) {
            return cacheDiskUtils.remove(key);
        } else {
            throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static boolean clear(CacheDiskUtils cacheDiskUtils) {
        if (cacheDiskUtils != null) {
            return cacheDiskUtils.clear();
        }
        throw new NullPointerException("Argument 'cacheDiskUtils' of type CacheDiskUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static CacheDiskUtils getDefaultCacheDiskUtils() {
        CacheDiskUtils cacheDiskUtils = sDefaultCacheDiskUtils;
        return cacheDiskUtils != null ? cacheDiskUtils : CacheDiskUtils.getInstance();
    }
}
