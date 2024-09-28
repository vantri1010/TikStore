package com.litesuits.orm.kvdb;

import java.util.List;

public interface DataCache<K, V> {
    Object delete(K k);

    List<V> query(String str);

    Object save(K k, V v);

    Object update(K k, V v);
}
