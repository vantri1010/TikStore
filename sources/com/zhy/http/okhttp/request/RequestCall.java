package com.zhy.http.okhttp.request;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestCall {
    private Call call;
    private OkHttpClient clone;
    private long connTimeOut;
    private OkHttpRequest okHttpRequest;
    private long readTimeOut;
    private Request request;
    private long writeTimeOut;

    public RequestCall(OkHttpRequest request2) {
        this.okHttpRequest = request2;
    }

    public RequestCall readTimeOut(long readTimeOut2) {
        this.readTimeOut = readTimeOut2;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut2) {
        this.writeTimeOut = writeTimeOut2;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut2) {
        this.connTimeOut = connTimeOut2;
        return this;
    }

    public Call buildCall(Callback callback) {
        this.request = generateRequest(callback);
        if (this.readTimeOut > 0 || this.writeTimeOut > 0 || this.connTimeOut > 0) {
            long j = this.readTimeOut;
            long j2 = OkHttpUtils.DEFAULT_MILLISECONDS;
            if (j <= 0) {
                j = 10000;
            }
            this.readTimeOut = j;
            long j3 = this.writeTimeOut;
            if (j3 <= 0) {
                j3 = 10000;
            }
            this.writeTimeOut = j3;
            long j4 = this.connTimeOut;
            if (j4 > 0) {
                j2 = j4;
            }
            this.connTimeOut = j2;
            OkHttpClient build = OkHttpUtils.getInstance().getOkHttpClient().newBuilder().readTimeout(this.readTimeOut, TimeUnit.MILLISECONDS).writeTimeout(this.writeTimeOut, TimeUnit.MILLISECONDS).connectTimeout(this.connTimeOut, TimeUnit.MILLISECONDS).build();
            this.clone = build;
            this.call = build.newCall(this.request);
        } else {
            this.call = OkHttpUtils.getInstance().getOkHttpClient().newCall(this.request);
        }
        return this.call;
    }

    private Request generateRequest(Callback callback) {
        return this.okHttpRequest.generateRequest(callback);
    }

    public void execute(Callback callback) {
        buildCall(callback);
        if (callback != null) {
            callback.onBefore(this.request, getOkHttpRequest().getId());
        }
        OkHttpUtils.getInstance().execute(this, callback);
    }

    public Call getCall() {
        return this.call;
    }

    public Request getRequest() {
        return this.request;
    }

    public OkHttpRequest getOkHttpRequest() {
        return this.okHttpRequest;
    }

    public Response execute() throws IOException {
        buildCall((Callback) null);
        return this.call.execute();
    }

    public void cancel() {
        Call call2 = this.call;
        if (call2 != null) {
            call2.cancel();
        }
    }
}
