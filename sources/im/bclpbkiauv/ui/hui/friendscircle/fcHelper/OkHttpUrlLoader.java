package im.bclpbkiauv.ui.hui.friendscircle.fcHelper;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.OkHttpClient;

public class OkHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {
    private final Call.Factory client;

    public OkHttpUrlLoader(Call.Factory client2) {
        this.client = client2;
    }

    public boolean handles(GlideUrl url) {
        return true;
    }

    public ModelLoader.LoadData<InputStream> buildLoadData(GlideUrl model, int width, int height, Options options) {
        return new ModelLoader.LoadData<>(model, new OKHttpStreamFetcher(this.client, model));
    }

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private static volatile Call.Factory internalClient;
        private Call.Factory client;

        private static Call.Factory getInternalClient() {
            if (internalClient == null) {
                synchronized (Factory.class) {
                    if (internalClient == null) {
                        internalClient = new OkHttpClient();
                    }
                }
            }
            return internalClient;
        }

        public Factory() {
            this(getInternalClient());
        }

        public Factory(Call.Factory client2) {
            this.client = client2;
        }

        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new OkHttpUrlLoader(this.client);
        }

        public void teardown() {
        }
    }
}
