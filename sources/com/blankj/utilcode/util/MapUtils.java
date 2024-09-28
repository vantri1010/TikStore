package com.blankj.utilcode.util;

import android.util.Pair;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapUtils {

    public interface Closure<K, V> {
        void execute(K k, V v);
    }

    public interface Transformer<K1, V1, K2, V2> {
        Pair<K2, V2> transform(K1 k1, V1 v1);
    }

    private MapUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    @SafeVarargs
    public static <K, V> Map<K, V> newUnmodifiableMap(Pair<K, V>... pairs) {
        return Collections.unmodifiableMap(newHashMap(pairs));
    }

    @SafeVarargs
    public static <K, V> HashMap<K, V> newHashMap(Pair<K, V>... pairs) {
        HashMap<K, V> map = new HashMap<>();
        if (pairs == null || pairs.length == 0) {
            return map;
        }
        for (Pair<K, V> pair : pairs) {
            if (pair != null) {
                map.put(pair.first, pair.second);
            }
        }
        return map;
    }

    @SafeVarargs
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Pair<K, V>... pairs) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        if (pairs == null || pairs.length == 0) {
            return map;
        }
        for (Pair<K, V> pair : pairs) {
            if (pair != null) {
                map.put(pair.first, pair.second);
            }
        }
        return map;
    }

    @SafeVarargs
    public static <K, V> TreeMap<K, V> newTreeMap(Comparator<K> comparator, Pair<K, V>... pairs) {
        if (comparator != null) {
            TreeMap<K, V> map = new TreeMap<>(comparator);
            if (pairs == null || pairs.length == 0) {
                return map;
            }
            for (Pair<K, V> pair : pairs) {
                if (pair != null) {
                    map.put(pair.first, pair.second);
                }
            }
            return map;
        }
        throw new IllegalArgumentException("comparator must not be null");
    }

    @SafeVarargs
    public static <K, V> Hashtable<K, V> newHashTable(Pair<K, V>... pairs) {
        Hashtable<K, V> map = new Hashtable<>();
        if (pairs == null || pairs.length == 0) {
            return map;
        }
        for (Pair<K, V> pair : pairs) {
            if (pair != null) {
                map.put(pair.first, pair.second);
            }
        }
        return map;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static int size(Map map) {
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public static <K, V> void forAllDo(Map<K, V> map, Closure<K, V> closure) {
        if (map != null && closure != null) {
            for (Map.Entry<K, V> kvEntry : map.entrySet()) {
                closure.execute(kvEntry.getKey(), kvEntry.getValue());
            }
        }
    }

    public static <K1, V1, K2, V2> Map<K2, V2> transform(Map<K1, V1> map, final Transformer<K1, V1, K2, V2> transformer) {
        if (map == null || transformer == null) {
            return null;
        }
        try {
            final Map<K2, V2> transMap = (Map) map.getClass().newInstance();
            forAllDo(map, new Closure<K1, V1>() {
                public void execute(K1 key, V1 value) {
                    Pair<K2, V2> pair = transformer.transform(key, value);
                    transMap.put(pair.first, pair.second);
                }
            });
            return transMap;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String toString(Map map) {
        if (map == null) {
            return "null";
        }
        return map.toString();
    }
}
