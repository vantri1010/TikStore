package com.zhy.http.okhttp.request;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.CountingRequestBody;
import com.zhy.http.okhttp.utils.Exceptions;
import java.io.File;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostFileRequest extends OkHttpRequest {
    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    private File file;
    private MediaType mediaType;

    public PostFileRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, File file2, MediaType mediaType2, int id) {
        super(url, tag, params, headers, id);
        this.file = file2;
        this.mediaType = mediaType2;
        if (file2 == null) {
            Exceptions.illegalArgument("the file can not be null !", new Object[0]);
        }
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_STREAM;
        }
    }

    /* access modifiers changed from: protected */
    public RequestBody buildRequestBody() {
        return RequestBody.create(this.mediaType, this.file);
    }

    /* access modifiers changed from: protected */
    public RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback) {
        if (callback == null) {
            return requestBody;
        }
        return new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
            public void onRequestProgress(long bytesWritten, long contentLength) {
                final long j = bytesWritten;
                final long j2 = contentLength;
                OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                    public void run() {
                        Callback callback = callback;
                        long j = j2;
                        callback.inProgress((((float) j) * 1.0f) / ((float) j), j, PostFileRequest.this.id);
                    }
                });
            }
        });
    }

    /* access modifiers changed from: protected */
    public Request buildRequest(RequestBody requestBody) {
        return this.builder.post(requestBody).build();
    }
}
