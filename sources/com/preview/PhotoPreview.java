package com.preview;

import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import com.preview.interfaces.ImageLoader;
import com.preview.interfaces.OnDismissListener;
import com.preview.interfaces.OnLongClickListener;
import java.util.Arrays;
import java.util.List;

public class PhotoPreview {
    private PreviewDialogFragment mDialogFragment;

    public PhotoPreview(AppCompatActivity activity, ImageLoader imageLoader) {
        PreviewDialogFragment previewDialogFragment = new PreviewDialogFragment();
        this.mDialogFragment = previewDialogFragment;
        previewDialogFragment.setActivity(activity);
        this.mDialogFragment.setImageLoader(imageLoader);
    }

    public PhotoPreview(FragmentActivity activity, ImageLoader imageLoader) {
        PreviewDialogFragment previewDialogFragment = new PreviewDialogFragment();
        this.mDialogFragment = previewDialogFragment;
        previewDialogFragment.setActivity(activity);
        this.mDialogFragment.setImageLoader(imageLoader);
    }

    public PhotoPreview(FragmentActivity activity, boolean isShowTitle, ImageLoader imageLoader) {
        PreviewDialogFragment previewDialogFragment = new PreviewDialogFragment();
        this.mDialogFragment = previewDialogFragment;
        previewDialogFragment.setActivity(activity);
        this.mDialogFragment.setIsShowTitle(isShowTitle);
        this.mDialogFragment.setImageLoader(imageLoader);
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.mDialogFragment.setImageLoader(imageLoader);
    }

    public void setLongClickListener(OnLongClickListener longClickListener) {
        this.mDialogFragment.setLongClickListener(longClickListener);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mDialogFragment.setOnDismissListener(onDismissListener);
    }

    public void setIndicatorType(int indicatorType) {
        this.mDialogFragment.setIndicatorType(indicatorType);
    }

    public void setDelayShowProgressTime(long delayShowProgressTime) {
        this.mDialogFragment.setDelayShowProgressTime(delayShowProgressTime);
    }

    public void setProgressColor(int progressColor) {
        this.mDialogFragment.setProgressColor(progressColor);
    }

    public void setProgressDrawable(Drawable progressDrawable) {
        this.mDialogFragment.setProgressDrawable(progressDrawable);
    }

    public void show(View srcImageContainer, Object... picUrls) {
        show(srcImageContainer, (List<?>) Arrays.asList(picUrls));
    }

    public void show(View srcImageContainer, List<?> picUrls) {
        show(srcImageContainer, 0, picUrls);
    }

    public void show(View srcImageContainer, int defaultShowPosition, Object... picUrls) {
        show(srcImageContainer, defaultShowPosition, (List<?>) Arrays.asList(picUrls));
    }

    public void show(View srcImageContainer, int defaultShowPosition, List<?> picUrls) {
        this.mDialogFragment.show(srcImageContainer, defaultShowPosition, picUrls);
    }
}
