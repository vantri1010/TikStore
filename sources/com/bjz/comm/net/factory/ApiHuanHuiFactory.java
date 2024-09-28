package com.bjz.comm.net.factory;

import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.api.ApiHuanHui;
import com.bjz.comm.net.base.MyGsonConverterFactory;
import com.bjz.comm.net.utils.HttpUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class ApiHuanHuiFactory {
    private static volatile ApiHuanHuiFactory mInstance;
    private final String TAG = ApiHuanHuiFactory.class.getSimpleName();
    private ApiHuanHui mApiHuanHui;
    private final Retrofit mRetrofit;

    public static ApiHuanHuiFactory getInstance() {
        if (mInstance == null) {
            synchronized (ApiHuanHuiFactory.class) {
                if (mInstance == null) {
                    mInstance = new ApiHuanHuiFactory();
                }
            }
        }
        return mInstance;
    }

    private ApiHuanHuiFactory() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).protocols(Collections.unmodifiableList(Arrays.asList(new Protocol[]{Protocol.HTTP_2, Protocol.HTTP_1_1}))).sslSocketFactory(SSLSocketClient.getSSLSocketFactory()).hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        if (BuildVars.LOG_VERSION) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        this.mRetrofit = new Retrofit.Builder().baseUrl(HttpUtils.getInstance().getHuanHuiHostUrl()).client(builder.build()).addConverterFactory(MyGsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
    }

    public ApiHuanHui getApiHuanHui() {
        if (this.mApiHuanHui == null) {
            this.mApiHuanHui = (ApiHuanHui) this.mRetrofit.create(ApiHuanHui.class);
        }
        return this.mApiHuanHui;
    }
}
