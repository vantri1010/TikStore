package com.bjz.comm.net.base;

public interface DataListener<T> {
    void onError(Throwable th);

    void onResponse(T t);
}
