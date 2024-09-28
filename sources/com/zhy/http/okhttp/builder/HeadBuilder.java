package com.zhy.http.okhttp.builder;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.request.OtherRequest;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.RequestBody;

public class HeadBuilder extends GetBuilder {
    public RequestCall build() {
        return new OtherRequest((RequestBody) null, (String) null, OkHttpUtils.METHOD.HEAD, this.url, this.tag, this.params, this.headers, this.id).build();
    }
}
