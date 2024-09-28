package com.bjz.comm.net.factory;

import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.api.ApiTranslate;
import com.bjz.comm.net.base.MyGsonConverterFactory;
import com.bjz.comm.net.interceptor.HeaderInterceptor;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class ApiTranslateAudioFactory {
    private static ApiTranslateAudioFactory mInstance;
    private ApiTranslate mApiTranslate;
    private final Retrofit mRetrofit;

    public static ApiTranslateAudioFactory getInstance() {
        if (mInstance == null) {
            synchronized (ApiTranslateAudioFactory.class) {
                if (mInstance == null) {
                    mInstance = new ApiTranslateAudioFactory();
                }
            }
        }
        return mInstance;
    }

    private ApiTranslateAudioFactory() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.addInterceptor(new HeaderInterceptor());
        if (BuildVars.DEBUG_VERSION) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        this.mRetrofit = new Retrofit.Builder().baseUrl("https://vop.baidu.com/").client(builder.build()).addConverterFactory(MyGsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
    }

    public ApiTranslate getApiTranslate() {
        if (this.mApiTranslate == null) {
            this.mApiTranslate = (ApiTranslate) this.mRetrofit.create(ApiTranslate.class);
        }
        return this.mApiTranslate;
    }
}
