package com.library.cache;

public interface CacheInterface<T> {
    void clean();

    T get(int i);

    void put(int i, T t);

    void remove(int i);
}
