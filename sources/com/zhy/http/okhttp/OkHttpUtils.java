package com.zhy.http.okhttp;

import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.HeadBuilder;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;
import com.zhy.http.okhttp.builder.PostFileBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.RequestCall;
import com.zhy.http.okhttp.utils.Platform;
import java.io.IOException;
import java.util.concurrent.Executor;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class OkHttpUtils {
    public static final long DEFAULT_MILLISECONDS = 10000;
    private static volatile OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;

    public static class METHOD {
        public static final String DELETE = "DELETE";
        public static final String HEAD = "HEAD";
        public static final String PATCH = "PATCH";
        public static final String PUT = "PUT";
    }

    public OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            this.mOkHttpClient = new OkHttpClient();
        } else {
            this.mOkHttpClient = okHttpClient;
        }
        this.mPlatform = Platform.get();
    }

    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance() {
        return initClient((OkHttpClient) null);
    }

    public Executor getDelivery() {
        return this.mPlatform.defaultCallbackExecutor();
    }

    public OkHttpClient getOkHttpClient() {
        return this.mOkHttpClient;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static OtherRequestBuilder put() {
        return new OtherRequestBuilder(METHOD.PUT);
    }

    public static HeadBuilder head() {
        return new HeadBuilder();
    }

    public static OtherRequestBuilder delete() {
        return new OtherRequestBuilder(METHOD.DELETE);
    }

    public static OtherRequestBuilder patch() {
        return new OtherRequestBuilder(METHOD.PATCH);
    }

    public void execute(RequestCall requestCall, Callback callback) {
        if (callback == null) {
            callback = Callback.CALLBACK_DEFAULT;
        }
        final Callback finalCallback = callback;
        final int id = requestCall.getOkHttpRequest().getId();
        requestCall.getCall().enqueue(new okhttp3.Callback() {
            public void onFailure(Call call, IOException e) {
                OkHttpUtils.this.sendFailResultCallback(call, e, finalCallback, id);
            }

            public void onResponse(Call call, Response response) {
                try {
                    if (call.isCanceled()) {
                        OkHttpUtils.this.sendFailResultCallback(call, new IOException("Canceled!"), finalCallback, id);
                        if (response.body() != null) {
                            response.body().close();
                        }
                    } else if (!finalCallback.validateReponse(response, id)) {
                        OkHttpUtils okHttpUtils = OkHttpUtils.this;
                        okHttpUtils.sendFailResultCallback(call, new IOException("request failed , reponse's code is : " + response.code()), finalCallback, id);
                        if (response.body() != null) {
                            response.body().close();
                        }
                    } else {
                        OkHttpUtils.this.sendSuccessResultCallback(finalCallback.parseNetworkResponse(response, id), finalCallback, id);
                        if (response.body() == null) {
                            return;
                        }
                        response.body().close();
                    }
                } catch (Exception e) {
                    OkHttpUtils.this.sendFailResultCallback(call, e, finalCallback, id);
                    if (response.body() == null) {
                    }
                } catch (Throwable th) {
                    if (response.body() != null) {
                        response.body().close();
                    }
                    throw th;
                }
            }
        });
    }

    public void sendFailResultCallback(Call call, Exception e, Callback callback, int id) {
        if (callback != null) {
            final Callback callback2 = callback;
            final Call call2 = call;
            final Exception exc = e;
            final int i = id;
            this.mPlatform.execute(new Runnable() {
                public void run() {
                    callback2.onError(call2, exc, i);
                    callback2.onAfter(i);
                }
            });
        }
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback, final int id) {
        if (callback != null) {
            this.mPlatform.execute(new Runnable() {
                public void run() {
                    callback.onResponse(object, id);
                    callback.onAfter(id);
                }
            });
        }
    }

    public void cancelTag(Object tag) {
        for (Call call : this.mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call2 : this.mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call2.request().tag())) {
                call2.cancel();
            }
        }
    }
}
