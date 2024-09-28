package com.bjz.comm.net.factory;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.api.ApiMiniProgram;
import com.bjz.comm.net.base.CallFactoryProxy;
import com.bjz.comm.net.interceptor.HeaderInterceptor;
import com.bjz.comm.net.utils.HttpUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiMPFactory {
    private static ApiMPFactory mInstance;
    private final String TAG = ApiMPFactory.class.getSimpleName();
    private ApiMiniProgram mApiMiniProgram;
    private final Retrofit mRetrofit;

    public static ApiMPFactory getInstance() {
        if (mInstance == null) {
            synchronized (ApiMPFactory.class) {
                if (mInstance == null) {
                    mInstance = new ApiMPFactory();
                }
            }
        }
        return mInstance;
    }

    private ApiMPFactory() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).addInterceptor(new HeaderInterceptor()).protocols(Collections.unmodifiableList(Arrays.asList(new Protocol[]{Protocol.HTTP_2, Protocol.HTTP_1_1}))).sslSocketFactory(SSLSocketClient.getSSLSocketFactory()).hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        if (BuildVars.LOG_VERSION) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        this.mRetrofit = new Retrofit.Builder().baseUrl(HttpUtils.getInstance().getMPBaseUrl()).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).callFactory(new CallFactoryProxy(builder.build()) {
            /* access modifiers changed from: protected */
            @Nullable
            public HttpUrl getNewUrl(Request request) {
                HttpUrl requestHttpUrl = request.url();
                HttpUrl newBaseUrl = HttpUrl.parse(HttpUtils.getInstance().getMPBaseUrl());
                if (!TextUtils.equals(requestHttpUrl.host(), newBaseUrl.host())) {
                    return requestHttpUrl.newBuilder().scheme(newBaseUrl.scheme()).host(newBaseUrl.host()).port(newBaseUrl.port()).build();
                }
                return null;
            }
        }).build();
    }

    public ApiMiniProgram getApiMiniProgram() {
        if (this.mApiMiniProgram == null) {
            this.mApiMiniProgram = (ApiMiniProgram) this.mRetrofit.create(ApiMiniProgram.class);
        }
        return this.mApiMiniProgram;
    }
}
