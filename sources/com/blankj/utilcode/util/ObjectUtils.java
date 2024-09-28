package com.blankj.utilcode.util;

import android.os.Build;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import androidx.collection.LongSparseArray;
import androidx.collection.SimpleArrayMap;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public final class ObjectUtils {
    private ObjectUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if ((obj instanceof CharSequence) && obj.toString().length() == 0) {
            return true;
        }
        if ((obj instanceof Collection) && ((Collection) obj).isEmpty()) {
            return true;
        }
        if ((obj instanceof Map) && ((Map) obj).isEmpty()) {
            return true;
        }
        if ((obj instanceof SimpleArrayMap) && ((SimpleArrayMap) obj).isEmpty()) {
            return true;
        }
        if ((obj instanceof SparseArray) && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if ((obj instanceof SparseBooleanArray) && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if ((obj instanceof SparseIntArray) && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= 18 && (obj instanceof SparseLongArray) && ((SparseLongArray) obj).size() == 0) {
            return true;
        }
        if ((obj instanceof LongSparseArray) && ((LongSparseArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT < 16 || !(obj instanceof android.util.LongSparseArray) || ((android.util.LongSparseArray) obj).size() != 0) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(CharSequence obj) {
        return obj == null || obj.toString().length() == 0;
    }

    public static boolean isEmpty(Collection obj) {
        return obj == null || obj.isEmpty();
    }

    public static boolean isEmpty(Map obj) {
        return obj == null || obj.isEmpty();
    }

    public static boolean isEmpty(SimpleArrayMap obj) {
        return obj == null || obj.isEmpty();
    }

    public static boolean isEmpty(SparseArray obj) {
        return obj == null || obj.size() == 0;
    }

    public static boolean isEmpty(SparseBooleanArray obj) {
        return obj == null || obj.size() == 0;
    }

    public static boolean isEmpty(SparseIntArray obj) {
        return obj == null || obj.size() == 0;
    }

    public static boolean isEmpty(LongSparseArray obj) {
        return obj == null || obj.size() == 0;
    }

    public static boolean isEmpty(SparseLongArray obj) {
        return obj == null || obj.size() == 0;
    }

    public static boolean isEmpty(android.util.LongSparseArray obj) {
        return obj == null || obj.size() == 0;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(CharSequence obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(Collection obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(Map obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(SimpleArrayMap obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(SparseArray obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(SparseBooleanArray obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(SparseIntArray obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(LongSparseArray obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(SparseLongArray obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(android.util.LongSparseArray obj) {
        return !isEmpty(obj);
    }

    public static boolean equals(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    public static void requireNonNull(Object... objects) {
        if (objects != null) {
            int length = objects.length;
            int i = 0;
            while (i < length) {
                if (objects[i] != null) {
                    i++;
                } else {
                    throw null;
                }
            }
            return;
        }
        throw null;
    }

    public static <T> T getOrDefault(T object, T defaultObject) {
        if (object == null) {
            return defaultObject;
        }
        return object;
    }

    public static int hashCode(Object o) {
        if (o != null) {
            return o.hashCode();
        }
        return 0;
    }
}
