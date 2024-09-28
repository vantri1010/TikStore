package com.zhy.http.okhttp.request;

import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.utils.Exceptions;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class OkHttpRequest {
    protected Request.Builder builder = new Request.Builder();
    protected Map<String, String> headers;
    protected int id;
    protected Map<String, String> params;
    protected Object tag;
    protected String url;

    /* access modifiers changed from: protected */
    public abstract Request buildRequest(RequestBody requestBody);

    /* access modifiers changed from: protected */
    public abstract RequestBody buildRequestBody();

    protected OkHttpRequest(String url2, Object tag2, Map<String, String> params2, Map<String, String> headers2, int id2) {
        this.url = url2;
        this.tag = tag2;
        this.params = params2;
        this.headers = headers2;
        this.id = id2;
        if (url2 == null) {
            Exceptions.illegalArgument("url can not be null.", new Object[0]);
        }
        initBuilder();
    }

    private void initBuilder() {
        this.builder.url(this.url).tag(this.tag);
        appendHeaders();
    }

    /* access modifiers changed from: protected */
    public RequestBody wrapRequestBody(RequestBody requestBody, Callback callback) {
        return requestBody;
    }

    public RequestCall build() {
        return new RequestCall(this);
    }

    public Request generateRequest(Callback callback) {
        return buildRequest(wrapRequestBody(buildRequestBody(), callback));
    }

    /* access modifiers changed from: protected */
    public void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        Map<String, String> map = this.headers;
        if (map != null && !map.isEmpty()) {
            for (String key : this.headers.keySet()) {
                headerBuilder.add(key, this.headers.get(key));
            }
            this.builder.headers(headerBuilder.build());
        }
    }

    public int getId() {
        return this.id;
    }
}
