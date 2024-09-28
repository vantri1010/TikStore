package com.blankj.utilcode.util;

import android.content.SharedPreferences;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SPUtils {
    private static final Map<String, SPUtils> SP_UTILS_MAP = new HashMap();
    private SharedPreferences sp;

    public static SPUtils getInstance() {
        return getInstance("", 0);
    }

    public static SPUtils getInstance(int mode) {
        return getInstance("", mode);
    }

    public static SPUtils getInstance(String spName) {
        return getInstance(spName, 0);
    }

    public static SPUtils getInstance(String spName, int mode) {
        if (isSpace(spName)) {
            spName = "spUtils";
        }
        SPUtils spUtils = SP_UTILS_MAP.get(spName);
        if (spUtils == null) {
            synchronized (SPUtils.class) {
                spUtils = SP_UTILS_MAP.get(spName);
                if (spUtils == null) {
                    spUtils = new SPUtils(spName, mode);
                    SP_UTILS_MAP.put(spName, spUtils);
                }
            }
        }
        return spUtils;
    }

    private SPUtils(String spName) {
        this.sp = Utils.getApp().getSharedPreferences(spName, 0);
    }

    private SPUtils(String spName, int mode) {
        this.sp = Utils.getApp().getSharedPreferences(spName, mode);
    }

    public void put(String key, String value) {
        if (key != null) {
            put(key, value, false);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, String value, boolean isCommit) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isCommit) {
            this.sp.edit().putString(key, value).commit();
        } else {
            this.sp.edit().putString(key, value).apply();
        }
    }

    public String getString(String key) {
        if (key != null) {
            return getString(key, "");
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public String getString(String key, String defaultValue) {
        if (key != null) {
            return this.sp.getString(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, int value) {
        if (key != null) {
            put(key, value, false);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, int value, boolean isCommit) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isCommit) {
            this.sp.edit().putInt(key, value).commit();
        } else {
            this.sp.edit().putInt(key, value).apply();
        }
    }

    public int getInt(String key) {
        if (key != null) {
            return getInt(key, -1);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public int getInt(String key, int defaultValue) {
        if (key != null) {
            return this.sp.getInt(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, long value) {
        if (key != null) {
            put(key, value, false);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, long value, boolean isCommit) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isCommit) {
            this.sp.edit().putLong(key, value).commit();
        } else {
            this.sp.edit().putLong(key, value).apply();
        }
    }

    public long getLong(String key) {
        if (key != null) {
            return getLong(key, -1);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public long getLong(String key, long defaultValue) {
        if (key != null) {
            return this.sp.getLong(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, float value) {
        if (key != null) {
            put(key, value, false);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, float value, boolean isCommit) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isCommit) {
            this.sp.edit().putFloat(key, value).commit();
        } else {
            this.sp.edit().putFloat(key, value).apply();
        }
    }

    public float getFloat(String key) {
        if (key != null) {
            return getFloat(key, -1.0f);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public float getFloat(String key, float defaultValue) {
        if (key != null) {
            return this.sp.getFloat(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, boolean value) {
        if (key != null) {
            put(key, value, false);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, boolean value, boolean isCommit) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isCommit) {
            this.sp.edit().putBoolean(key, value).commit();
        } else {
            this.sp.edit().putBoolean(key, value).apply();
        }
    }

    public boolean getBoolean(String key) {
        if (key != null) {
            return getBoolean(key, false);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (key != null) {
            return this.sp.getBoolean(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Set<String> value) {
        if (key != null) {
            put(key, value, false);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void put(String key, Set<String> value, boolean isCommit) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isCommit) {
            this.sp.edit().putStringSet(key, value).commit();
        } else {
            this.sp.edit().putStringSet(key, value).apply();
        }
    }

    public Set<String> getStringSet(String key) {
        if (key != null) {
            return getStringSet(key, Collections.emptySet());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        if (key != null) {
            return this.sp.getStringSet(key, defaultValue);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public Map<String, ?> getAll() {
        return this.sp.getAll();
    }

    public boolean contains(String key) {
        if (key != null) {
            return this.sp.contains(key);
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void remove(String key) {
        if (key != null) {
            remove(key, false);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void remove(String key, boolean isCommit) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isCommit) {
            this.sp.edit().remove(key).commit();
        } else {
            this.sp.edit().remove(key).apply();
        }
    }

    public void clear() {
        clear(false);
    }

    public void clear(boolean isCommit) {
        if (isCommit) {
            this.sp.edit().clear().commit();
        } else {
            this.sp.edit().clear().apply();
        }
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
