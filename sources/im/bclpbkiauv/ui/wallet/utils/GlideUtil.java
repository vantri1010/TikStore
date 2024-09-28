package im.bclpbkiauv.ui.wallet.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;

public class GlideUtil {
    public static RequestOptions getDefaultOptions() {
        return (RequestOptions) ((RequestOptions) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).priority(Priority.NORMAL);
    }

    public static RequestOptions getHighPriorityOptions() {
        return (RequestOptions) getDefaultOptions().priority(Priority.HIGH);
    }

    public static RequestOptions getOriginPriorityOptions() {
        return (RequestOptions) ((RequestOptions) getHighPriorityOptions().override(Integer.MIN_VALUE, Integer.MIN_VALUE)).format(DecodeFormat.PREFER_RGB_565);
    }

    public static void loadUrl(ImageView iv, String url) {
        loadUrl(iv, url, Priority.NORMAL);
    }

    public static void loadUrl(ImageView iv, String url, Priority priority) {
        loadUrl(iv, url, priority, false);
    }

    public static void loadUrl(ImageView iv, String url, boolean loadOrginSize) {
        loadUrl(iv, url, Priority.NORMAL, loadOrginSize);
    }

    public static void loadUrl(ImageView iv, String url, Priority priority, boolean loadOrginSize) {
        loadUrl(iv, url, priority, loadOrginSize, 0);
    }

    public static void loadUrl(ImageView iv, String url, int defaultResId) {
        loadUrl(iv, url, false, defaultResId);
    }

    public static void loadUrl(ImageView iv, String url, int defaultResId, int errorResId) {
        loadUrl(iv, url, false, defaultResId, errorResId, (Transformation<Bitmap>[]) new Transformation[0]);
    }

    public static void loadUrl(ImageView iv, String url, Priority priority, int defaultResId) {
        loadUrl(iv, url, priority, false, defaultResId);
    }

    public static void loadUrl(ImageView iv, String url, boolean loadOrginSize, int defaultResId) {
        loadUrl(iv, url, loadOrginSize, defaultResId, 0, (Transformation<Bitmap>[]) new Transformation[0]);
    }

    public static void loadUrl(ImageView iv, String url, Priority priority, boolean loadOrginSize, int defaultResId) {
        loadUrl(iv, url, priority, loadOrginSize, defaultResId, 0, (Transformation<Bitmap>[]) new Transformation[0]);
    }

    public static void loadUrl(ImageView iv, String url, boolean loadOrginSize, int radius, int defaultResId, int errorResId) {
        loadUrl(iv, url, Priority.NORMAL, loadOrginSize, radius, defaultResId, errorResId);
    }

    public static void loadUrl(ImageView iv, String url, Priority priority, boolean loadOrginSize, int radius, int defaultResId, int errorResId) {
        int i = radius;
        if (i > 0) {
            loadUrl(iv, url, priority, loadOrginSize, defaultResId, errorResId, (Transformation<Bitmap>[]) new Transformation[]{new RoundedCorners(i)});
            return;
        }
        loadUrl(iv, url, priority, loadOrginSize, defaultResId, errorResId, (Transformation<Bitmap>[]) new Transformation[0]);
    }

    @SafeVarargs
    public static void loadUrl(ImageView iv, String url, boolean loadOrginSize, int defaultResId, int errorResId, Transformation<Bitmap>... transformations) {
        loadUrl(iv, url, Priority.NORMAL, loadOrginSize, defaultResId, errorResId, transformations);
    }

    @SafeVarargs
    public static void loadUrl(ImageView iv, String url, Priority priority, boolean loadOrginSize, int defaultResId, int errorResId, Transformation<Bitmap>... transformations) {
        RequestOptions options = loadOrginSize ? getOriginPriorityOptions() : getDefaultOptions();
        if (!loadOrginSize) {
            options.priority(priority);
        }
        ((RequestOptions) ((RequestOptions) options.placeholder(defaultResId)).error(errorResId)).transform(transformations);
        loadUrl(iv, url, options);
    }

    public static void loadUrl(ImageView iv, String url, RequestOptions options) {
        if (iv != null) {
            Glide.with(iv.getContext()).asBitmap().transition(BitmapTransitionOptions.withCrossFade()).apply((BaseRequestOptions<?>) options).load(url).into(iv);
        }
    }
}
