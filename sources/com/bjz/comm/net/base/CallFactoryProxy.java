package com.bjz.comm.net.base;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;

public abstract class CallFactoryProxy implements Call.Factory {
    private final Call.Factory delegate;

    /* access modifiers changed from: protected */
    @Nullable
    public abstract HttpUrl getNewUrl(Request request);

    public CallFactoryProxy(Call.Factory delegate2) {
        this.delegate = delegate2;
    }

    public Call newCall(Request request) {
        HttpUrl newHttpUrl = getNewUrl(request);
        if (newHttpUrl == null) {
            return this.delegate.newCall(request);
        }
        return this.delegate.newCall(request.newBuilder().url(newHttpUrl).build());
    }
}
