package com.scwang.smartrefresh.layout.internal;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalClassics;
import com.scwang.smartrefresh.layout.util.SmartUtil;

public abstract class InternalClassics<T extends InternalClassics> extends InternalAbstract implements RefreshInternal {
    public static final int ID_IMAGE_ARROW = R.id.srl_classics_arrow;
    public static final int ID_IMAGE_PROGRESS = R.id.srl_classics_progress;
    public static final int ID_TEXT_TITLE = R.id.srl_classics_title;
    protected PaintDrawable mArrowDrawable;
    protected ImageView mArrowView;
    protected int mBackgroundColor;
    protected int mFinishDuration = 500;
    protected int mMinHeightOfContent = 0;
    protected int mPaddingBottom = 20;
    protected int mPaddingTop = 20;
    protected PaintDrawable mProgressDrawable;
    protected ImageView mProgressView;
    protected RefreshKernel mRefreshKernel;
    protected boolean mSetAccentColor;
    protected boolean mSetPrimaryColor;
    protected TextView mTitleText;

    public InternalClassics(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mSpinnerStyle = SpinnerStyle.Translate;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mMinHeightOfContent == 0) {
            this.mPaddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            this.mPaddingBottom = paddingBottom;
            if (this.mPaddingTop == 0 || paddingBottom == 0) {
                int paddingLeft = getPaddingLeft();
                int paddingRight = getPaddingRight();
                int i = this.mPaddingTop;
                if (i == 0) {
                    i = SmartUtil.dp2px(20.0f);
                }
                this.mPaddingTop = i;
                int i2 = this.mPaddingBottom;
                if (i2 == 0) {
                    i2 = SmartUtil.dp2px(20.0f);
                }
                this.mPaddingBottom = i2;
                setPadding(paddingLeft, this.mPaddingTop, paddingRight, i2);
            }
            setClipToPadding(false);
        }
        if (View.MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
            int parentHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            int i3 = this.mMinHeightOfContent;
            if (parentHeight < i3) {
                int padding = (parentHeight - i3) / 2;
                setPadding(getPaddingLeft(), padding, getPaddingRight(), padding);
            } else {
                setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
            }
        } else {
            setPadding(getPaddingLeft(), this.mPaddingTop, getPaddingRight(), this.mPaddingBottom);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mMinHeightOfContent == 0) {
            for (int i4 = 0; i4 < getChildCount(); i4++) {
                int height = getChildAt(i4).getMeasuredHeight();
                if (this.mMinHeightOfContent < height) {
                    this.mMinHeightOfContent = height;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= 14) {
            View arrowView = this.mArrowView;
            View progressView = this.mProgressView;
            arrowView.animate().cancel();
            progressView.animate().cancel();
        }
        Drawable drawable = this.mProgressView.getDrawable();
        if ((drawable instanceof Animatable) && ((Animatable) drawable).isRunning()) {
            ((Animatable) drawable).stop();
        }
    }

    /* access modifiers changed from: protected */
    public T self() {
        return this;
    }

    public void onInitialized(RefreshKernel kernel, int height, int maxDragHeight) {
        this.mRefreshKernel = kernel;
        kernel.requestDrawBackgroundFor(this, this.mBackgroundColor);
    }

    public void onStartAnimator(RefreshLayout refreshLayout, int height, int maxDragHeight) {
        View progressView = this.mProgressView;
        if (progressView.getVisibility() != 0) {
            progressView.setVisibility(0);
            Drawable drawable = this.mProgressView.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            } else {
                progressView.animate().rotation(36000.0f).setDuration(100000);
            }
        }
    }

    public void onReleased(RefreshLayout refreshLayout, int height, int maxDragHeight) {
        onStartAnimator(refreshLayout, height, maxDragHeight);
    }

    public int onFinish(RefreshLayout refreshLayout, boolean success) {
        View progressView = this.mProgressView;
        Drawable drawable = this.mProgressView.getDrawable();
        if (!(drawable instanceof Animatable)) {
            progressView.animate().rotation(0.0f).setDuration(0);
        } else if (((Animatable) drawable).isRunning()) {
            ((Animatable) drawable).stop();
        }
        progressView.setVisibility(8);
        return this.mFinishDuration;
    }

    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            if (!(getBackground() instanceof BitmapDrawable) && !this.mSetPrimaryColor) {
                setPrimaryColor(colors[0]);
                this.mSetPrimaryColor = false;
            }
            if (!this.mSetAccentColor) {
                if (colors.length > 1) {
                    setAccentColor(colors[1]);
                } else {
                    int i = -1;
                    if (colors[0] == -1) {
                        i = -10066330;
                    }
                    setAccentColor(i);
                }
                this.mSetAccentColor = false;
            }
        }
    }

    public T setProgressDrawable(Drawable drawable) {
        this.mProgressDrawable = null;
        this.mProgressView.setImageDrawable(drawable);
        return self();
    }

    public T setProgressResource(int resId) {
        this.mProgressDrawable = null;
        this.mProgressView.setImageResource(resId);
        return self();
    }

    public T setArrowDrawable(Drawable drawable) {
        this.mArrowDrawable = null;
        this.mArrowView.setImageDrawable(drawable);
        return self();
    }

    public T setArrowResource(int resId) {
        this.mArrowDrawable = null;
        this.mArrowView.setImageResource(resId);
        return self();
    }

    public T setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return self();
    }

    public T setPrimaryColor(int primaryColor) {
        this.mSetPrimaryColor = true;
        this.mBackgroundColor = primaryColor;
        RefreshKernel refreshKernel = this.mRefreshKernel;
        if (refreshKernel != null) {
            refreshKernel.requestDrawBackgroundFor(this, primaryColor);
        }
        return self();
    }

    public T setAccentColor(int accentColor) {
        this.mSetAccentColor = true;
        this.mTitleText.setTextColor(accentColor);
        PaintDrawable paintDrawable = this.mArrowDrawable;
        if (paintDrawable != null) {
            paintDrawable.setColor(accentColor);
            this.mArrowView.invalidateDrawable(this.mArrowDrawable);
        }
        PaintDrawable paintDrawable2 = this.mProgressDrawable;
        if (paintDrawable2 != null) {
            paintDrawable2.setColor(accentColor);
            this.mProgressView.invalidateDrawable(this.mProgressDrawable);
        }
        return self();
    }

    public T setPrimaryColorId(int colorId) {
        setPrimaryColor(ContextCompat.getColor(getContext(), colorId));
        return self();
    }

    public T setAccentColorId(int colorId) {
        setAccentColor(ContextCompat.getColor(getContext(), colorId));
        return self();
    }

    public T setFinishDuration(int delay) {
        this.mFinishDuration = delay;
        return self();
    }

    public T setTextSizeTitle(float size) {
        this.mTitleText.setTextSize(size);
        RefreshKernel refreshKernel = this.mRefreshKernel;
        if (refreshKernel != null) {
            refreshKernel.requestRemeasureHeightFor(this);
        }
        return self();
    }

    public T setDrawableMarginRight(float dp) {
        View arrowView = this.mArrowView;
        View progressView = this.mProgressView;
        ViewGroup.MarginLayoutParams lpArrow = (ViewGroup.MarginLayoutParams) arrowView.getLayoutParams();
        ViewGroup.MarginLayoutParams lpProgress = (ViewGroup.MarginLayoutParams) progressView.getLayoutParams();
        int dp2px = SmartUtil.dp2px(dp);
        lpProgress.rightMargin = dp2px;
        lpArrow.rightMargin = dp2px;
        arrowView.setLayoutParams(lpArrow);
        progressView.setLayoutParams(lpProgress);
        return self();
    }

    public T setDrawableSize(float dp) {
        View arrowView = this.mArrowView;
        View progressView = this.mProgressView;
        ViewGroup.LayoutParams lpArrow = arrowView.getLayoutParams();
        ViewGroup.LayoutParams lpProgress = progressView.getLayoutParams();
        int dp2px = SmartUtil.dp2px(dp);
        lpProgress.width = dp2px;
        lpArrow.width = dp2px;
        int dp2px2 = SmartUtil.dp2px(dp);
        lpProgress.height = dp2px2;
        lpArrow.height = dp2px2;
        arrowView.setLayoutParams(lpArrow);
        progressView.setLayoutParams(lpProgress);
        return self();
    }

    public T setDrawableArrowSize(float dp) {
        View arrowView = this.mArrowView;
        ViewGroup.LayoutParams lpArrow = arrowView.getLayoutParams();
        int dp2px = SmartUtil.dp2px(dp);
        lpArrow.width = dp2px;
        lpArrow.height = dp2px;
        arrowView.setLayoutParams(lpArrow);
        return self();
    }

    public T setDrawableProgressSize(float dp) {
        View progressView = this.mProgressView;
        ViewGroup.LayoutParams lpProgress = progressView.getLayoutParams();
        int dp2px = SmartUtil.dp2px(dp);
        lpProgress.width = dp2px;
        lpProgress.height = dp2px;
        progressView.setLayoutParams(lpProgress);
        return self();
    }
}
