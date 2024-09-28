package com.zhy.http.okhttp.builder;

import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.request.RequestCall;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {
    protected Map<String, String> headers;
    protected int id;
    protected Map<String, String> params;
    protected Object tag;
    protected String url;

    public abstract RequestCall build();

    public T id(int id2) {
        this.id = id2;
        return this;
    }

    public T url(String url2) {
        this.url = url2;
        return this;
    }

    public T tag(Object tag2) {
        this.tag = tag2;
        return this;
    }

    public T headers(Map<String, String> headers2) {
        this.headers = headers2;
        return this;
    }

    public T addHeader(String key, String val) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap();
        }
        this.headers.put(key, val);
        return this;
    }
}
