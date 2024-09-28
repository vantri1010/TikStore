package com.blankj.utilcode.util;

import java.util.Map;
import java.util.Set;

public final class SPStaticUtils {
    private static SPUtils sDefaultSPUtils;

    public static void setDefaultSPUtils(SPUtils spUtils) {
        sDefaultSPUtils = spUtils;
    }

    public static void put(String key, String value) {
        if (key != null) {
            put(key, value, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, String value, boolean isCommit) {
        if (key != null) {
            put(key, value, isCommit, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getString(String key) {
        if (key != null) {
            return getString(key, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String getString(String key, String defaultValue) {
        if (key != null) {
            return getString(key, defaultValue, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, int value) {
        if (key != null) {
            put(key, value, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, int value, boolean isCommit) {
        if (key != null) {
            put(key, value, isCommit, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getInt(String key) {
        if (key != null) {
            return getInt(key, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getInt(String key, int defaultValue) {
        if (key != null) {
            return getInt(key, defaultValue, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, long value) {
        if (key != null) {
            put(key, value, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, long value, boolean isCommit) {
        if (key != null) {
            put(key, value, isCommit, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long getLong(String key) {
        if (key != null) {
            return getLong(key, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static long getLong(String key, long defaultValue) {
        if (key != null) {
            return getLong(key, defaultValue, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, float value) {
        if (key != null) {
            put(key, value, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, float value, boolean isCommit) {
        if (key != null) {
            put(key, value, isCommit, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static float getFloat(String key) {
        if (key != null) {
            return getFloat(key, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static float getFloat(String key, float defaultValue) {
        if (key != null) {
            return getFloat(key, defaultValue, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, boolean value) {
        if (key != null) {
            put(key, value, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, boolean value, boolean isCommit) {
        if (key != null) {
            put(key, value, isCommit, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean getBoolean(String key) {
        if (key != null) {
            return getBoolean(key, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        if (key != null) {
            return getBoolean(key, defaultValue, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Set<String> value) {
        if (key != null) {
            put(key, value, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void put(String key, Set<String> value, boolean isCommit) {
        if (key != null) {
            put(key, value, isCommit, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Set<String> getStringSet(String key) {
        if (key != null) {
            return getStringSet(key, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Set<String> getStringSet(String key, Set<String> defaultValue) {
        if (key != null) {
            return getStringSet(key, defaultValue, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Map<String, ?> getAll() {
        return getAll(getDefaultSPUtils());
    }

    public static boolean contains(String key) {
        if (key != null) {
            return contains(key, getDefaultSPUtils());
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void remove(String key) {
        if (key != null) {
            remove(key, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void remove(String key, boolean isCommit) {
        if (key != null) {
            remove(key, isCommit, getDefaultSPUtils());
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void clear() {
        clear(getDefaultSPUtils());
    }

    public static void clear(boolean isCommit) {
        clear(isCommit, getDefaultSPUtils());
    }

    public static void put(String key, String value, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, String value, boolean isCommit, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value, isCommit);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getString(String key, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getString(key);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getString(String key, String defaultValue, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getString(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, int value, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, int value, boolean isCommit, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value, isCommit);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static int getInt(String key, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getInt(key);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static int getInt(String key, int defaultValue, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getInt(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, long value, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, long value, boolean isCommit, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value, isCommit);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static long getLong(String key, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getLong(key);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static long getLong(String key, long defaultValue, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getLong(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, float value, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, float value, boolean isCommit, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value, isCommit);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static float getFloat(String key, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getFloat(key);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static float getFloat(String key, float defaultValue, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getFloat(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, boolean value, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, boolean value, boolean isCommit, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value, isCommit);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static boolean getBoolean(String key, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getBoolean(key);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getBoolean(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Set<String> value, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void put(String key, Set<String> value, boolean isCommit, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.put(key, value, isCommit);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Set<String> getStringSet(String key, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getStringSet(key);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Set<String> getStringSet(String key, Set<String> defaultValue, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.getStringSet(key, defaultValue);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Map<String, ?> getAll(SPUtils spUtils) {
        if (spUtils != null) {
            return spUtils.getAll();
        }
        throw new NullPointerException("Argument 'spUtils' of type SPUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean contains(String key, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            return spUtils.contains(key);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void remove(String key, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.remove(key);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void remove(String key, boolean isCommit, SPUtils spUtils) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (spUtils != null) {
            spUtils.remove(key, isCommit);
        } else {
            throw new NullPointerException("Argument 'spUtils' of type SPUtils (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void clear(SPUtils spUtils) {
        if (spUtils != null) {
            spUtils.clear();
            return;
        }
        throw new NullPointerException("Argument 'spUtils' of type SPUtils (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void clear(boolean isCommit, SPUtils spUtils) {
        if (spUtils != null) {
            spUtils.clear(isCommit);
            return;
        }
        throw new NullPointerException("Argument 'spUtils' of type SPUtils (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static SPUtils getDefaultSPUtils() {
        SPUtils sPUtils = sDefaultSPUtils;
        return sPUtils != null ? sPUtils : SPUtils.getInstance();
    }
}
