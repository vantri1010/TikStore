package im.bclpbkiauv.ui.hui.friendscircle.fcHelper;

import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OKHttpStreamFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "OkHttpFetcher";
    private volatile Call call;
    private final Call.Factory client;
    ResponseBody responseBody;
    InputStream stream;
    private final GlideUrl url;

    public OKHttpStreamFetcher(Call.Factory client2, GlideUrl url2) {
        this.client = client2;
        this.url = url2;
    }

    public void loadData(Priority priority, final DataFetcher.DataCallback<? super InputStream> callback) {
        Request.Builder requestBuilder = new Request.Builder().url(this.url.toStringUrl());
        for (Map.Entry<String, String> headerEntry : this.url.getHeaders().entrySet()) {
            requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        this.call = this.client.newCall(requestBuilder.build());
        this.call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                if (Log.isLoggable(OKHttpStreamFetcher.TAG, 3)) {
                    Log.d(OKHttpStreamFetcher.TAG, "OkHttp failed to obtain result", e);
                }
                callback.onLoadFailed(e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                OKHttpStreamFetcher.this.responseBody = response.body();
                if (response.isSuccessful()) {
                    long contentLength = OKHttpStreamFetcher.this.responseBody.contentLength();
                    OKHttpStreamFetcher oKHttpStreamFetcher = OKHttpStreamFetcher.this;
                    oKHttpStreamFetcher.stream = ContentLengthInputStream.obtain(oKHttpStreamFetcher.responseBody.byteStream(), contentLength);
                    callback.onDataReady(OKHttpStreamFetcher.this.stream);
                    return;
                }
                callback.onLoadFailed(new HttpException(response.message(), response.code()));
            }
        });
    }

    public void cleanup() {
        try {
            if (this.stream != null) {
                this.stream.close();
            }
        } catch (IOException e) {
        }
        ResponseBody responseBody2 = this.responseBody;
        if (responseBody2 != null) {
            responseBody2.close();
        }
    }

    public void cancel() {
        Call local = this.call;
        if (local != null) {
            local.cancel();
        }
    }

    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
