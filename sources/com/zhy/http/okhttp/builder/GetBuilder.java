package com.zhy.http.okhttp.builder;

import android.net.Uri;
import com.zhy.http.okhttp.request.GetRequest;
import com.zhy.http.okhttp.request.RequestCall;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> implements HasParamsable {
    public RequestCall build() {
        if (this.params != null) {
            this.url = appendParams(this.url, this.params);
        }
        return new GetRequest(this.url, this.tag, this.params, this.headers, this.id).build();
    }

    /* access modifiers changed from: protected */
    public String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (String key : params.keySet()) {
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }

    public GetBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public GetBuilder addParams(String key, String val) {
        if (this.params == null) {
            this.params = new LinkedHashMap();
        }
        this.params.put(key, val);
        return this;
    }
}
