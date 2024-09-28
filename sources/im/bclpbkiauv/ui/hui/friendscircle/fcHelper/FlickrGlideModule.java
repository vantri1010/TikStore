package im.bclpbkiauv.ui.hui.friendscircle.fcHelper;

import android.content.Context;
import com.bjz.comm.net.factory.SSLSocketClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import im.bclpbkiauv.ui.hui.friendscircle.fcHelper.OkHttpUrlLoader;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class FlickrGlideModule implements GlideModule {
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
    }

    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient.Builder().retryOnConnectionFailure(true).connectTimeout(15, TimeUnit.SECONDS).readTimeout(DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS, TimeUnit.MILLISECONDS).sslSocketFactory(SSLSocketClient.getSSLSocketFactory()).hostnameVerifier(SSLSocketClient.getHostnameVerifier()).build()));
    }
}
