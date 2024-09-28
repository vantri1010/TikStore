package com.bjz.comm.net.interceptor;

import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.utils.HttpUtils;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private final String TAG = HeaderInterceptor.class.getSimpleName();

    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        int addHeaderType = 0;
        if (httpUrl != null) {
            String host = httpUrl.host();
            if (HttpUtils.getInstance().getFcBaseUrl().contains(host)) {
                addHeaderType = 1;
            } else if (HttpUtils.getInstance().getMPBaseUrl().contains(host)) {
                addHeaderType = 2;
            } else if (HttpUtils.getInstance().getGameBaseUrl().contains(host)) {
                addHeaderType = 3;
            }
        }
        Request.Builder builder = request.newBuilder();
        if (!BuildVars.DEBUG_VERSION) {
            if (addHeaderType == 1) {
                builder.addHeader("User-Agent", HttpUtils.getInstance().getUserAgentFC());
            } else if (addHeaderType == 2) {
                builder.addHeader("User-Agent", HttpUtils.getInstance().getUserAgentMP());
            } else if (addHeaderType == 3) {
                builder.addHeader("User-Agent", HttpUtils.getInstance().getUserAgentGame());
            }
        }
        if (addHeaderType != 0) {
            builder.addHeader("authorization", HttpUtils.getInstance().getAuthorization());
        }
        return chain.proceed(builder.build());
    }
}
