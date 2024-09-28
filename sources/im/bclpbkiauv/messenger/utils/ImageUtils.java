package im.bclpbkiauv.messenger.utils;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;

public class ImageUtils {
    public static void LoadRoundedCornerImg(Context context, ImageView iv, Object object, int iRadius, boolean blnSkipMemoryCached, DiskCacheStrategy diskCacheStrategy, int iPlaceholderImgId, int iErrorImgId) {
        int iHeight = iv.getHeight();
        int iWidth = iv.getWidth();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) (RequestOptions) ((RequestOptions) ((RequestOptions) ((RequestOptions) ((RequestOptions) RequestOptions.bitmapTransform(new RoundedCorners(iRadius)).override(iWidth, iHeight)).skipMemoryCache(blnSkipMemoryCached)).diskCacheStrategy(diskCacheStrategy)).placeholder(iPlaceholderImgId)).error(iErrorImgId)).into(iv);
    }

    public static void LoadRoundedCornerImg(Context context, ImageView iv, Object object, int iRadius) {
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) RequestOptions.bitmapTransform(new RoundedCorners(iRadius))).into(iv);
    }

    public static void LoadRoundedCornerImg(Context context, ImageView iv, Object object, int iRadius, boolean blnSkipMemoryCached) {
        int iHeight = iv.getHeight();
        int iWidth = iv.getWidth();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) (RequestOptions) ((RequestOptions) RequestOptions.bitmapTransform(new RoundedCorners(iRadius)).skipMemoryCache(blnSkipMemoryCached)).override(iWidth, iHeight)).into(iv);
    }

    public static void LoadRoundedCornerImg(Context context, ImageView iv, Object object, int iRadius, DiskCacheStrategy diskCacheStrategy) {
        int iHeight = iv.getHeight();
        int iWidth = iv.getWidth();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) (RequestOptions) ((RequestOptions) RequestOptions.bitmapTransform(new RoundedCorners(iRadius)).diskCacheStrategy(diskCacheStrategy)).override(iWidth, iHeight)).into(iv);
    }

    public static void LoadRoundedCornerImg(Context context, ImageView iv, Object object, int iRadius, boolean blnSkipMemoryCached, DiskCacheStrategy diskCacheStrategy) {
        int iHeight = iv.getHeight();
        int iWidth = iv.getWidth();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) (RequestOptions) ((RequestOptions) ((RequestOptions) RequestOptions.bitmapTransform(new RoundedCorners(iRadius)).skipMemoryCache(blnSkipMemoryCached)).diskCacheStrategy(diskCacheStrategy)).override(iWidth, iHeight)).into(iv);
    }

    public static void LoadRoundedCornerImg(Context context, ImageView iv, Object object, int iRadius, int iPlaceholderImgId, int iErrorImgId) {
        int iHeight = iv.getHeight();
        int iWidth = iv.getWidth();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) (RequestOptions) ((RequestOptions) ((RequestOptions) RequestOptions.bitmapTransform(new RoundedCorners(iRadius)).placeholder(iPlaceholderImgId)).error(iErrorImgId)).override(iWidth, iHeight)).into(iv);
    }

    public static void LoadNormalImg(Context context, ImageView iv, Object object, boolean blnSkipMemoryCached, DiskCacheStrategy diskCacheStrategy, int iPlaceholderImgId, int iErrorImgId) {
        int iHeight = iv.getHeight();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) ((RequestOptions) ((RequestOptions) ((RequestOptions) ((RequestOptions) new RequestOptions().skipMemoryCache(blnSkipMemoryCached)).diskCacheStrategy(diskCacheStrategy)).placeholder(iPlaceholderImgId)).override(iv.getWidth(), iHeight)).error(iErrorImgId)).into(iv);
    }

    public static void LoadNormalImg(Context context, ImageView iv, Object object) {
        int iHeight = iv.getHeight();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) new RequestOptions().override(iv.getWidth(), iHeight)).into(iv);
    }

    public static void LoadNormalImg(Context context, ImageView iv, Object object, boolean blnSkipMemoryCached) {
        int iHeight = iv.getHeight();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) ((RequestOptions) new RequestOptions().skipMemoryCache(blnSkipMemoryCached)).override(iv.getWidth(), iHeight)).into(iv);
    }

    public static void LoadNormalImg(Context context, ImageView iv, Object object, DiskCacheStrategy diskCacheStrategy) {
        int iHeight = iv.getHeight();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) ((RequestOptions) new RequestOptions().diskCacheStrategy(diskCacheStrategy)).override(iv.getWidth(), iHeight)).into(iv);
    }

    public static void LoadNormalImg(Context context, ImageView iv, Object object, boolean blnSkipMemoryCached, DiskCacheStrategy diskCacheStrategy) {
        int iHeight = iv.getHeight();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) ((RequestOptions) ((RequestOptions) new RequestOptions().skipMemoryCache(blnSkipMemoryCached)).diskCacheStrategy(diskCacheStrategy)).override(iv.getWidth(), iHeight)).into(iv);
    }

    public static void LoadNormalImg(Context context, ImageView iv, Object object, int iPlaceholderImgId, int iErrorImgId) {
        int iHeight = iv.getHeight();
        Glide.with(context).load(object).apply((BaseRequestOptions<?>) ((RequestOptions) ((RequestOptions) new RequestOptions().placeholder(iPlaceholderImgId)).error(iErrorImgId)).override(iv.getWidth(), iHeight)).into(iv);
    }
}
