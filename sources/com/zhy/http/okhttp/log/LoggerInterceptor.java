package com.zhy.http.okhttp.log;

import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LoggerInterceptor implements Interceptor {
    public static final String TAG = "OkHttpUtils";
    private boolean showResponse;
    private String tag;

    public LoggerInterceptor(String tag2, boolean showResponse2) {
        tag2 = TextUtils.isEmpty(tag2) ? TAG : tag2;
        this.showResponse = showResponse2;
        this.tag = tag2;
    }

    public LoggerInterceptor(String tag2) {
        this(tag2, false);
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        return logForResponse(chain.proceed(request));
    }

    private Response logForResponse(Response response) {
        ResponseBody body;
        MediaType mediaType;
        try {
            Log.e(this.tag, "========response'log=======");
            Response clone = response.newBuilder().build();
            String str = this.tag;
            Log.e(str, "url : " + clone.request().url());
            String str2 = this.tag;
            Log.e(str2, "code : " + clone.code());
            String str3 = this.tag;
            Log.e(str3, "protocol : " + clone.protocol());
            if (!TextUtils.isEmpty(clone.message())) {
                String str4 = this.tag;
                Log.e(str4, "message : " + clone.message());
            }
            if (!(!this.showResponse || (body = clone.body()) == null || (mediaType = body.contentType()) == null)) {
                String str5 = this.tag;
                Log.e(str5, "responseBody's contentType : " + mediaType.toString());
                if (isText(mediaType)) {
                    String resp = body.string();
                    String str6 = this.tag;
                    Log.e(str6, "responseBody's content : " + resp);
                    return response.newBuilder().body(ResponseBody.create(mediaType, resp)).build();
                }
                Log.e(this.tag, "responseBody's content :  maybe [file part] , too large too print , ignored!");
            }
            Log.e(this.tag, "========response'log=======end");
        } catch (Exception e) {
        }
        return response;
    }

    private void logForRequest(Request request) {
        MediaType mediaType;
        try {
            String url = request.url().toString();
            Headers headers = request.headers();
            Log.e(this.tag, "========request'log=======");
            String str = this.tag;
            Log.e(str, "method : " + request.method());
            String str2 = this.tag;
            Log.e(str2, "url : " + url);
            if (headers != null && headers.size() > 0) {
                String str3 = this.tag;
                Log.e(str3, "headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (!(requestBody == null || (mediaType = requestBody.contentType()) == null)) {
                String str4 = this.tag;
                Log.e(str4, "requestBody's contentType : " + mediaType.toString());
                if (isText(mediaType)) {
                    String str5 = this.tag;
                    Log.e(str5, "requestBody's content : " + bodyToString(request));
                } else {
                    Log.e(this.tag, "requestBody's content :  maybe [file part] , too large too print , ignored!");
                }
            }
            Log.e(this.tag, "========request'log=======end");
        } catch (Exception e) {
        }
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() == null) {
            return false;
        }
        if (mediaType.subtype().equals("json") || mediaType.subtype().equals("xml") || mediaType.subtype().equals("html") || mediaType.subtype().equals("webviewhtml")) {
            return true;
        }
        return false;
    }

    private String bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "something error when show requestBody.";
        }
    }
}
