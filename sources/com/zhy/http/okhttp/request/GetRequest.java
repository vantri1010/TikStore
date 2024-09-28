package com.zhy.http.okhttp.request;

import java.util.Map;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GetRequest extends OkHttpRequest {
    public GetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id) {
        super(url, tag, params, headers, id);
    }

    /* access modifiers changed from: protected */
    public RequestBody buildRequestBody() {
        return null;
    }

    /* access modifiers changed from: protected */
    public Request buildRequest(RequestBody requestBody) {
        return this.builder.get().build();
    }
}
