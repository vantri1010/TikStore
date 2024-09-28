package com.zhy.http.okhttp.request;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.CountingRequestBody;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostFormRequest extends OkHttpRequest {
    private List<PostFormBuilder.FileInput> files;

    public PostFormRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, List<PostFormBuilder.FileInput> files2, int id) {
        super(url, tag, params, headers, id);
        this.files = files2;
    }

    /* access modifiers changed from: protected */
    public RequestBody buildRequestBody() {
        List<PostFormBuilder.FileInput> list = this.files;
        if (list == null || list.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder);
            return builder.build();
        }
        MultipartBody.Builder builder2 = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addParams(builder2);
        for (int i = 0; i < this.files.size(); i++) {
            PostFormBuilder.FileInput fileInput = this.files.get(i);
            builder2.addFormDataPart(fileInput.key, fileInput.filename, RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file));
        }
        return builder2.build();
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
                        callback.inProgress((((float) j) * 1.0f) / ((float) j), j, PostFormRequest.this.id);
                    }
                });
            }
        });
    }

    /* access modifiers changed from: protected */
    public Request buildRequest(RequestBody requestBody) {
        return this.builder.post(requestBody).build();
    }

    private String guessMimeType(String path) {
        String contentTypeFor = null;
        try {
            contentTypeFor = URLConnection.getFileNameMap().getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            return "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (this.params != null && !this.params.isEmpty()) {
            for (String key : this.params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""), RequestBody.create((MediaType) null, (String) this.params.get(key)));
            }
        }
    }

    private void addParams(FormBody.Builder builder) {
        if (this.params != null) {
            for (String key : this.params.keySet()) {
                builder.add(key, (String) this.params.get(key));
            }
        }
    }
}
