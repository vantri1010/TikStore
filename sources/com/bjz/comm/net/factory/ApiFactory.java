package com.bjz.comm.net.factory;

import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.Nullable;
import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.api.ApiCommon;
import com.bjz.comm.net.api.ApiMomentForum;
import com.bjz.comm.net.base.CallFactoryProxy;
import com.bjz.comm.net.base.MyGsonConverterFactory;
import com.bjz.comm.net.interceptor.HeaderInterceptor;
import com.bjz.comm.net.utils.HttpUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class ApiFactory {
    private static volatile ApiFactory mInstance;
    /* access modifiers changed from: private */
    public final String TAG = ApiFactory.class.getSimpleName();
    private ApiCommon mApiCommon;
    private ApiMomentForum mApiMomentForum;
    private final Retrofit mRetrofit;

    public static ApiFactory getInstance() {
        if (mInstance == null) {
            synchronized (ApiFactory.class) {
                if (mInstance == null) {
                    mInstance = new ApiFactory();
                }
            }
        }
        return mInstance;
    }

    private ApiFactory() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).addInterceptor(new HeaderInterceptor()).protocols(Collections.unmodifiableList(Arrays.asList(new Protocol[]{Protocol.HTTP_2, Protocol.HTTP_1_1}))).sslSocketFactory(SSLSocketClient.getSSLSocketFactory()).hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        if (BuildVars.LOG_VERSION) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        this.mRetrofit = new Retrofit.Builder().baseUrl(HttpUtils.getInstance().getFcBaseUrl()).addConverterFactory(MyGsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).callFactory(new CallFactoryProxy(builder.build()) {
            /* access modifiers changed from: protected */
            @Nullable
            public HttpUrl getNewUrl(Request request) {
                Headers headers = request.headers();
                if (headers != null && headers.size() > 0 && TextUtils.equals(headers.get("BaseUrl"), "self")) {
                    return null;
                }
                HttpUrl requestHttpUrl = request.url();
                if (BuildVars.DEBUG_VERSION) {
                    String access$000 = ApiFactory.this.TAG;
                    Log.d(access$000, "requestFcUrl == " + requestHttpUrl.host());
                }
                HttpUrl newBaseUrl = HttpUrl.parse(HttpUtils.getInstance().getFcBaseUrl());
                if (TextUtils.equals(requestHttpUrl.host(), newBaseUrl.host())) {
                    return null;
                }
                if (BuildVars.DEBUG_VERSION) {
                    String access$0002 = ApiFactory.this.TAG;
                    Log.d(access$0002, "newBaseFcUrl == " + newBaseUrl.host());
                }
                return requestHttpUrl.newBuilder().scheme(newBaseUrl.scheme()).host(newBaseUrl.host()).port(newBaseUrl.port()).build();
            }
        }).build();
    }

    public ApiCommon getApiCommon() {
        if (this.mApiCommon == null) {
            this.mApiCommon = (ApiCommon) this.mRetrofit.create(ApiCommon.class);
        }
        return this.mApiCommon;
    }

    public ApiMomentForum getApiMomentForum() {
        if (this.mApiMomentForum == null) {
            this.mApiMomentForum = (ApiMomentForum) this.mRetrofit.create(ApiMomentForum.class);
        }
        return this.mApiMomentForum;
    }
}
