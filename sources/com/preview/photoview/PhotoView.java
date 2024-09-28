package com.preview.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Scroller;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.ItemTouchHelper;

public class PhotoView extends AppCompatImageView {
    private PhotoViewAttacher attacher;
    private Scroller mScroller;
    private ImageView.ScaleType pendingScaleType;

    public PhotoView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.attacher = new PhotoViewAttacher(this);
        super.setScaleType(ImageView.ScaleType.MATRIX);
        ImageView.ScaleType scaleType = this.pendingScaleType;
        if (scaleType != null) {
            setScaleType(scaleType);
            this.pendingScaleType = null;
        }
        this.mScroller = new Scroller(context);
    }

    public PhotoViewAttacher getAttacher() {
        return this.attacher;
    }

    public ImageView.ScaleType getScaleType() {
        return this.attacher.getScaleType();
    }

    public Matrix getImageMatrix() {
        return this.attacher.getImageMatrix();
    }

    public void setOnLongClickListener(View.OnLongClickListener l) {
        this.attacher.setOnLongClickListener(l);
    }

    public void setOnClickListener(View.OnClickListener l) {
        this.attacher.setOnClickListener(l);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        PhotoViewAttacher photoViewAttacher = this.attacher;
        if (photoViewAttacher == null) {
            this.pendingScaleType = scaleType;
        } else {
            photoViewAttacher.setScaleType(scaleType);
        }
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        PhotoViewAttacher photoViewAttacher = this.attacher;
        if (photoViewAttacher != null) {
            photoViewAttacher.update();
        }
    }

    public void setImageResource(int resId) {
        super.setImageResource(resId);
        PhotoViewAttacher photoViewAttacher = this.attacher;
        if (photoViewAttacher != null) {
            photoViewAttacher.update();
        }
    }

    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        PhotoViewAttacher photoViewAttacher = this.attacher;
        if (photoViewAttacher != null) {
            photoViewAttacher.update();
        }
    }

    /* access modifiers changed from: protected */
    public boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            this.attacher.update();
        }
        return changed;
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void smoothResetPosition() {
        this.mScroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), -getScrollY(), ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        invalidate();
    }

    public void setRotationTo(float rotationDegree) {
        this.attacher.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        this.attacher.setRotationBy(rotationDegree);
    }

    @Deprecated
    public boolean isZoomEnabled() {
        return this.attacher.isZoomEnabled();
    }

    public boolean isZoomable() {
        return this.attacher.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        this.attacher.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return this.attacher.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        this.attacher.getDisplayMatrix(matrix);
    }

    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return this.attacher.setDisplayMatrix(finalRectangle);
    }

    public void getSuppMatrix(Matrix matrix) {
        this.attacher.getSuppMatrix(matrix);
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return this.attacher.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return this.attacher.getMinimumScale();
    }

    public float getMediumScale() {
        return this.attacher.getMediumScale();
    }

    public float getMaximumScale() {
        return this.attacher.getMaximumScale();
    }

    public float getScale() {
        return this.attacher.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        this.attacher.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        this.attacher.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        this.attacher.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        this.attacher.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        this.attacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        this.attacher.setOnMatrixChangeListener(listener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        this.attacher.setOnPhotoTapListener(listener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        this.attacher.setOnOutsidePhotoTapListener(listener);
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        this.attacher.setOnViewTapListener(listener);
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        this.attacher.setOnViewDragListener(listener);
    }

    public void setScale(float scale) {
        this.attacher.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        this.attacher.setScale(scale, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        this.attacher.setScale(scale, focalX, focalY, animate);
    }

    public void setZoomTransitionDuration(int milliseconds) {
        this.attacher.setZoomTransitionDuration(milliseconds);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        this.attacher.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        this.attacher.setOnScaleChangeListener(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.attacher.setOnSingleFlingListener(onSingleFlingListener);
    }
}
