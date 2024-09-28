package com.preview.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.OverScroller;
import androidx.recyclerview.widget.ItemTouchHelper;

public class PhotoViewAttacher implements View.OnTouchListener, View.OnLayoutChangeListener {
    private static float DEFAULT_MAX_SCALE = 3.0f;
    private static float DEFAULT_MID_SCALE = 1.75f;
    /* access modifiers changed from: private */
    public static float DEFAULT_MIN_SCALE = 1.0f;
    private static int DEFAULT_ZOOM_DURATION = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    private static final int EDGE_BOTH = 2;
    private static final int EDGE_LEFT = 0;
    private static final int EDGE_NONE = -1;
    private static final int EDGE_RIGHT = 1;
    /* access modifiers changed from: private */
    public static int SINGLE_TOUCH = 1;
    static int STATE_DRAG = -2;
    static int STATE_FINGER_UP = -4;
    static int STATE_SCALE = -3;
    static int mCurrentState = -1;
    /* access modifiers changed from: private */
    public boolean isBottomDrag = false;
    /* access modifiers changed from: private */
    public boolean mAllowParentInterceptOnEdge = true;
    private final Matrix mBaseMatrix = new Matrix();
    private float mBaseRotation;
    /* access modifiers changed from: private */
    public boolean mBlockParentIntercept = false;
    /* access modifiers changed from: private */
    public FlingRunnable mCurrentFlingRunnable;
    private final RectF mDisplayRect = new RectF();
    private final Matrix mDrawMatrix = new Matrix();
    private GestureDetector mGestureDetector;
    /* access modifiers changed from: private */
    public ImageView mImageView;
    /* access modifiers changed from: private */
    public Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    /* access modifiers changed from: private */
    public View.OnLongClickListener mLongClickListener;
    private OnMatrixChangedListener mMatrixChangeListener;
    private final float[] mMatrixValues = new float[9];
    /* access modifiers changed from: private */
    public float mMaxScale = DEFAULT_MAX_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    /* access modifiers changed from: private */
    public float mMinScale = DEFAULT_MIN_SCALE;
    /* access modifiers changed from: private */
    public View.OnClickListener mOnClickListener;
    private OnFingerUpListener mOnFingerUpListener;
    /* access modifiers changed from: private */
    public OnViewDragListener mOnViewDragListener;
    /* access modifiers changed from: private */
    public OnOutsidePhotoTapListener mOutsidePhotoTapListener;
    /* access modifiers changed from: private */
    public OnPhotoTapListener mPhotoTapListener;
    /* access modifiers changed from: private */
    public OnScaleChangedListener mScaleChangeListener;
    /* access modifiers changed from: private */
    public CustomGestureDetector mScaleDragDetector;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;
    /* access modifiers changed from: private */
    public int mScrollEdge = 2;
    /* access modifiers changed from: private */
    public OnSingleFlingListener mSingleFlingListener;
    /* access modifiers changed from: private */
    public final Matrix mSuppMatrix = new Matrix();
    /* access modifiers changed from: private */
    public OnViewTapListener mViewTapListener;
    /* access modifiers changed from: private */
    public int mZoomDuration = DEFAULT_ZOOM_DURATION;
    private boolean mZoomEnabled = true;
    /* access modifiers changed from: private */
    public OnGestureListener onGestureListener = new OnGestureListener() {
        public void onDrag(float dx, float dy) {
            if (!PhotoViewAttacher.this.mScaleDragDetector.isScaling()) {
                ViewParent parent = PhotoViewAttacher.this.mImageView.getParent();
                if (!PhotoViewAttacher.this.isBottomDrag && ((double) (Math.abs(dy) - Math.abs(dx))) > 0.5d) {
                    boolean unused = PhotoViewAttacher.this.isBottomDrag = true;
                }
                if (PhotoViewAttacher.this.mOnViewDragListener != null && PhotoViewAttacher.this.isBottomDrag && PhotoViewAttacher.this.getScale() <= PhotoViewAttacher.this.mMinScale && PhotoViewAttacher.mCurrentState != PhotoViewAttacher.STATE_SCALE) {
                    PhotoViewAttacher.mCurrentState = PhotoViewAttacher.STATE_DRAG;
                    PhotoViewAttacher.this.mOnViewDragListener.onDrag(dx, dy);
                }
                PhotoViewAttacher.this.mSuppMatrix.postTranslate(dx, dy);
                PhotoViewAttacher.this.checkAndDisplayMatrix();
                if (PhotoViewAttacher.this.isBottomDrag) {
                    return;
                }
                if (!PhotoViewAttacher.this.mAllowParentInterceptOnEdge || PhotoViewAttacher.this.mScaleDragDetector.isScaling() || PhotoViewAttacher.this.mBlockParentIntercept) {
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                } else if ((PhotoViewAttacher.this.mScrollEdge == 2 || ((PhotoViewAttacher.this.mScrollEdge == 0 && dx >= 1.0f) || (PhotoViewAttacher.this.mScrollEdge == 1 && dx <= -1.0f))) && parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            }
        }

        public void onFling(float startX, float startY, float velocityX, float velocityY) {
            PhotoViewAttacher photoViewAttacher = PhotoViewAttacher.this;
            FlingRunnable unused = photoViewAttacher.mCurrentFlingRunnable = new FlingRunnable(photoViewAttacher.mImageView.getContext());
            FlingRunnable access$1000 = PhotoViewAttacher.this.mCurrentFlingRunnable;
            PhotoViewAttacher photoViewAttacher2 = PhotoViewAttacher.this;
            int access$1100 = photoViewAttacher2.getImageViewWidth(photoViewAttacher2.mImageView);
            PhotoViewAttacher photoViewAttacher3 = PhotoViewAttacher.this;
            access$1000.fling(access$1100, photoViewAttacher3.getImageViewHeight(photoViewAttacher3.mImageView), (int) velocityX, (int) velocityY);
            PhotoViewAttacher.this.mImageView.post(PhotoViewAttacher.this.mCurrentFlingRunnable);
        }

        public void onScale(float scaleFactor, float focusX, float focusY) {
            if (PhotoViewAttacher.this.getScale() >= PhotoViewAttacher.this.mMaxScale && scaleFactor >= 1.0f) {
                return;
            }
            if (PhotoViewAttacher.this.getScale() > PhotoViewAttacher.this.mMinScale || scaleFactor > 1.0f) {
                if (PhotoViewAttacher.this.mScaleChangeListener != null) {
                    PhotoViewAttacher.this.mScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
                }
                PhotoViewAttacher.this.mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                PhotoViewAttacher.this.checkAndDisplayMatrix();
            }
        }
    };

    public PhotoViewAttacher(ImageView imageView) {
        this.mImageView = imageView;
        imageView.setOnTouchListener(this);
        imageView.addOnLayoutChangeListener(this);
        if (!imageView.isInEditMode()) {
            this.mBaseRotation = 0.0f;
            this.mScaleDragDetector = new CustomGestureDetector(imageView.getContext(), this.onGestureListener);
            GestureDetector gestureDetector = new GestureDetector(imageView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                public void onLongPress(MotionEvent e) {
                    if (PhotoViewAttacher.this.mLongClickListener != null) {
                        PhotoViewAttacher.this.mLongClickListener.onLongClick(PhotoViewAttacher.this.mImageView);
                    }
                }

                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (PhotoViewAttacher.this.mSingleFlingListener == null || PhotoViewAttacher.this.getScale() > PhotoViewAttacher.DEFAULT_MIN_SCALE || e1.getPointerCount() > PhotoViewAttacher.SINGLE_TOUCH || e1.getPointerCount() > PhotoViewAttacher.SINGLE_TOUCH) {
                        return false;
                    }
                    return PhotoViewAttacher.this.mSingleFlingListener.onFling(e1, e2, velocityX, velocityY);
                }
            });
            this.mGestureDetector = gestureDetector;
            gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (PhotoViewAttacher.this.mOnClickListener != null) {
                        PhotoViewAttacher.this.mOnClickListener.onClick(PhotoViewAttacher.this.mImageView);
                    }
                    RectF displayRect = PhotoViewAttacher.this.getDisplayRect();
                    float x = e.getX();
                    float y = e.getY();
                    if (PhotoViewAttacher.this.mViewTapListener != null) {
                        PhotoViewAttacher.this.mViewTapListener.onViewTap(PhotoViewAttacher.this.mImageView, x, y);
                    }
                    if (displayRect == null) {
                        return false;
                    }
                    if (displayRect.contains(x, y)) {
                        float xResult = (x - displayRect.left) / displayRect.width();
                        float yResult = (y - displayRect.top) / displayRect.height();
                        if (PhotoViewAttacher.this.mPhotoTapListener == null) {
                            return true;
                        }
                        PhotoViewAttacher.this.mPhotoTapListener.onPhotoTap(PhotoViewAttacher.this.mImageView, xResult, yResult);
                        return true;
                    } else if (PhotoViewAttacher.this.mOutsidePhotoTapListener == null) {
                        return false;
                    } else {
                        PhotoViewAttacher.this.mOutsidePhotoTapListener.onOutsidePhotoTap(PhotoViewAttacher.this.mImageView);
                        return false;
                    }
                }

                public boolean onDoubleTap(MotionEvent ev) {
                    try {
                        float scale = PhotoViewAttacher.this.getScale();
                        float x = ev.getX();
                        float y = ev.getY();
                        if (scale < PhotoViewAttacher.this.getMediumScale()) {
                            PhotoViewAttacher.this.setScale(PhotoViewAttacher.this.getMediumScale(), x, y, true);
                        } else if (scale < PhotoViewAttacher.this.getMediumScale() || scale >= PhotoViewAttacher.this.getMaximumScale()) {
                            PhotoViewAttacher.this.setScale(PhotoViewAttacher.this.getMinimumScale(), x, y, true);
                        } else {
                            PhotoViewAttacher.this.setScale(PhotoViewAttacher.this.getMaximumScale(), x, y, true);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                    return true;
                }

                public boolean onDoubleTapEvent(MotionEvent e) {
                    return false;
                }
            });
        }
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener newOnDoubleTapListener) {
        this.mGestureDetector.setOnDoubleTapListener(newOnDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangeListener) {
        this.mScaleChangeListener = onScaleChangeListener;
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.mSingleFlingListener = onSingleFlingListener;
    }

    @Deprecated
    public boolean isZoomEnabled() {
        return this.mZoomEnabled;
    }

    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if (finalMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        } else if (this.mImageView.getDrawable() == null) {
            return false;
        } else {
            this.mSuppMatrix.set(finalMatrix);
            checkAndDisplayMatrix();
            return true;
        }
    }

    public void setBaseRotation(float degrees) {
        this.mBaseRotation = degrees % 360.0f;
        update();
        setRotationBy(this.mBaseRotation);
        checkAndDisplayMatrix();
    }

    public void setRotationTo(float degrees) {
        this.mSuppMatrix.setRotate(degrees % 360.0f);
        checkAndDisplayMatrix();
    }

    public void setRotationBy(float degrees) {
        this.mSuppMatrix.postRotate(degrees % 360.0f);
        checkAndDisplayMatrix();
    }

    public float getMinimumScale() {
        return this.mMinScale;
    }

    public float getMediumScale() {
        return this.mMidScale;
    }

    public float getMaximumScale() {
        return this.mMaxScale;
    }

    public float getScale() {
        return getScale(this.mSuppMatrix);
    }

    public float getScale(Matrix matrix) {
        return (float) Math.sqrt((double) (((float) Math.pow((double) getValue(matrix, 0), 2.0d)) + ((float) Math.pow((double) getValue(matrix, 3), 2.0d))));
    }

    public ImageView.ScaleType getScaleType() {
        return this.mScaleType;
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
            updateBaseMatrix(this.mImageView.getDrawable());
        }
    }

    public boolean onTouch(View v, MotionEvent ev) {
        RectF rect;
        boolean handled = false;
        if (!this.mZoomEnabled || !Util.hasDrawable((ImageView) v)) {
            return false;
        }
        int action = ev.getAction();
        boolean z = false;
        if (action == 0) {
            ViewParent parent = v.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            cancelFling();
        } else if (action == 1 || action == 3) {
            if (mCurrentState == STATE_SCALE) {
                if (getScale() < this.mMinScale) {
                    RectF rect2 = getDisplayRect();
                    if (rect2 != null) {
                        v.post(new AnimatedZoomRunnable(getScale(), this.mMinScale, rect2.centerX(), rect2.centerY()));
                        handled = true;
                    }
                } else if (getScale() > this.mMaxScale && (rect = getDisplayRect()) != null) {
                    v.post(new AnimatedZoomRunnable(getScale(), this.mMaxScale, rect.centerX(), rect.centerY()));
                    handled = true;
                }
            }
            mCurrentState = STATE_FINGER_UP;
            if (this.mOnFingerUpListener != null) {
                ViewParent parent1 = v.getParent();
                if (parent1 != null) {
                    parent1.requestDisallowInterceptTouchEvent(false);
                }
                this.isBottomDrag = false;
                if (mCurrentState != STATE_SCALE) {
                    this.mOnFingerUpListener.onFingerUp();
                }
            }
        }
        CustomGestureDetector customGestureDetector = this.mScaleDragDetector;
        if (customGestureDetector != null) {
            boolean wasScaling = customGestureDetector.isScaling();
            boolean wasDragging = this.mScaleDragDetector.isDragging();
            handled = this.mScaleDragDetector.onTouchEvent(ev);
            boolean didntScale = !wasScaling && !this.mScaleDragDetector.isScaling();
            boolean didntDrag = !wasDragging && !this.mScaleDragDetector.isDragging();
            if (didntScale && didntDrag) {
                z = true;
            }
            this.mBlockParentIntercept = z;
        }
        GestureDetector gestureDetector = this.mGestureDetector;
        if (gestureDetector == null || !gestureDetector.onTouchEvent(ev)) {
            return handled;
        }
        return true;
    }

    public void setOnFingerUpListener(OnFingerUpListener onFingerUpListener) {
        this.mOnFingerUpListener = onFingerUpListener;
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        this.mAllowParentInterceptOnEdge = allow;
    }

    public void setMinimumScale(float minimumScale) {
        Util.checkZoomLevels(minimumScale, this.mMidScale, this.mMaxScale);
        this.mMinScale = minimumScale;
    }

    public void setMediumScale(float mediumScale) {
        Util.checkZoomLevels(this.mMinScale, mediumScale, this.mMaxScale);
        this.mMidScale = mediumScale;
    }

    public void setMaximumScale(float maximumScale) {
        Util.checkZoomLevels(this.mMinScale, this.mMidScale, maximumScale);
        this.mMaxScale = maximumScale;
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        Util.checkZoomLevels(minimumScale, mediumScale, maximumScale);
        this.mMinScale = minimumScale;
        this.mMidScale = mediumScale;
        this.mMaxScale = maximumScale;
    }

    public void setOnLongClickListener(View.OnLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        this.mMatrixChangeListener = listener;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        this.mPhotoTapListener = listener;
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener mOutsidePhotoTapListener2) {
        this.mOutsidePhotoTapListener = mOutsidePhotoTapListener2;
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        this.mViewTapListener = listener;
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        this.mOnViewDragListener = listener;
    }

    public void setScale(float scale) {
        setScale(scale, false);
    }

    public void setScale(float scale, boolean animate) {
        setScale(scale, (float) (this.mImageView.getRight() / 2), (float) (this.mImageView.getBottom() / 2), animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        if (animate) {
            this.mImageView.post(new AnimatedZoomRunnable(getScale(), scale, focalX, focalY));
            return;
        }
        this.mSuppMatrix.setScale(scale, scale, focalX, focalY);
        checkAndDisplayMatrix();
    }

    public void setZoomInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        if (Util.isSupportedScaleType(scaleType) && scaleType != this.mScaleType) {
            this.mScaleType = scaleType;
            update();
        }
    }

    public boolean isZoomable() {
        return this.mZoomEnabled;
    }

    public void setZoomable(boolean zoomable) {
        this.mZoomEnabled = zoomable;
        update();
    }

    public void update() {
        if (this.mZoomEnabled) {
            updateBaseMatrix(this.mImageView.getDrawable());
        } else {
            resetMatrix();
        }
    }

    public void getDisplayMatrix(Matrix matrix) {
        matrix.set(getDrawMatrix());
    }

    public void getSuppMatrix(Matrix matrix) {
        matrix.set(this.mSuppMatrix);
    }

    private Matrix getDrawMatrix() {
        this.mDrawMatrix.set(this.mBaseMatrix);
        this.mDrawMatrix.postConcat(this.mSuppMatrix);
        return this.mDrawMatrix;
    }

    public Matrix getImageMatrix() {
        return this.mDrawMatrix;
    }

    public void setZoomTransitionDuration(int milliseconds) {
        this.mZoomDuration = milliseconds;
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[whichValue];
    }

    private void resetMatrix() {
        this.mSuppMatrix.reset();
        setRotationBy(this.mBaseRotation);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        RectF displayRect;
        this.mImageView.setImageMatrix(matrix);
        if (this.mMatrixChangeListener != null && (displayRect = getDisplayRect(matrix)) != null) {
            this.mMatrixChangeListener.onMatrixChanged(displayRect);
        }
    }

    /* access modifiers changed from: private */
    public void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = this.mImageView.getDrawable();
        if (d == null) {
            return null;
        }
        this.mDisplayRect.set(0.0f, 0.0f, (float) d.getIntrinsicWidth(), (float) d.getIntrinsicHeight());
        matrix.mapRect(this.mDisplayRect);
        return this.mDisplayRect;
    }

    private void updateBaseMatrix(Drawable drawable) {
        if (drawable != null) {
            float viewWidth = (float) getImageViewWidth(this.mImageView);
            float viewHeight = (float) getImageViewHeight(this.mImageView);
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            this.mBaseMatrix.reset();
            float widthScale = viewWidth / ((float) drawableWidth);
            float heightScale = viewHeight / ((float) drawableHeight);
            if (this.mScaleType == ImageView.ScaleType.CENTER) {
                this.mBaseMatrix.postTranslate((viewWidth - ((float) drawableWidth)) / 2.0f, (viewHeight - ((float) drawableHeight)) / 2.0f);
            } else if (this.mScaleType == ImageView.ScaleType.CENTER_CROP) {
                float scale = Math.max(widthScale, heightScale);
                this.mBaseMatrix.postScale(scale, scale);
                this.mBaseMatrix.postTranslate((viewWidth - (((float) drawableWidth) * scale)) / 2.0f, (viewHeight - (((float) drawableHeight) * scale)) / 2.0f);
            } else if (this.mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
                float scale2 = Math.min(1.0f, Math.min(widthScale, heightScale));
                this.mBaseMatrix.postScale(scale2, scale2);
                this.mBaseMatrix.postTranslate((viewWidth - (((float) drawableWidth) * scale2)) / 2.0f, (viewHeight - (((float) drawableHeight) * scale2)) / 2.0f);
            } else {
                RectF mTempSrc = new RectF(0.0f, 0.0f, (float) drawableWidth, (float) drawableHeight);
                RectF mTempDst = new RectF(0.0f, 0.0f, viewWidth, viewHeight);
                if (((int) this.mBaseRotation) % 180 != 0) {
                    mTempSrc = new RectF(0.0f, 0.0f, (float) drawableHeight, (float) drawableWidth);
                }
                int i = AnonymousClass4.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()];
                if (i == 1) {
                    this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);
                } else if (i == 2) {
                    this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.START);
                } else if (i == 3) {
                    this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.END);
                } else if (i == 4) {
                    this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL);
                }
            }
            resetMatrix();
        }
    }

    /* renamed from: com.preview.photoview.PhotoViewAttacher$4  reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType;

        static {
            int[] iArr = new int[ImageView.ScaleType.values().length];
            $SwitchMap$android$widget$ImageView$ScaleType = iArr;
            try {
                iArr[ImageView.ScaleType.FIT_CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_START.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_END.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_XY.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private boolean checkMatrixBounds() {
        RectF rect = getDisplayRect(getDrawMatrix());
        if (rect == null) {
            return false;
        }
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0.0f;
        float deltaY = 0.0f;
        int viewHeight = getImageViewHeight(this.mImageView);
        if (height <= ((float) viewHeight)) {
            int i = AnonymousClass4.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()];
            if (i == 2) {
                deltaY = -rect.top;
            } else if (i != 3) {
                deltaY = ((((float) viewHeight) - height) / 2.0f) - rect.top;
            } else {
                deltaY = (((float) viewHeight) - height) - rect.top;
            }
        } else if (rect.top > 0.0f) {
            deltaY = -rect.top;
        } else if (rect.bottom < ((float) viewHeight)) {
            deltaY = ((float) viewHeight) - rect.bottom;
        }
        int viewWidth = getImageViewWidth(this.mImageView);
        if (width <= ((float) viewWidth)) {
            int i2 = AnonymousClass4.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()];
            if (i2 == 2) {
                deltaX = -rect.left;
            } else if (i2 != 3) {
                deltaX = ((((float) viewWidth) - width) / 2.0f) - rect.left;
            } else {
                deltaX = (((float) viewWidth) - width) - rect.left;
            }
            this.mScrollEdge = 2;
        } else if (rect.left > 0.0f) {
            this.mScrollEdge = 0;
            deltaX = -rect.left;
        } else if (rect.right < ((float) viewWidth)) {
            deltaX = ((float) viewWidth) - rect.right;
            this.mScrollEdge = 1;
        } else {
            this.mScrollEdge = -1;
        }
        this.mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    /* access modifiers changed from: private */
    public int getImageViewWidth(ImageView imageView) {
        return (imageView.getWidth() - imageView.getPaddingLeft()) - imageView.getPaddingRight();
    }

    /* access modifiers changed from: private */
    public int getImageViewHeight(ImageView imageView) {
        return (imageView.getHeight() - imageView.getPaddingTop()) - imageView.getPaddingBottom();
    }

    private void cancelFling() {
        FlingRunnable flingRunnable = this.mCurrentFlingRunnable;
        if (flingRunnable != null) {
            flingRunnable.cancelFling();
            this.mCurrentFlingRunnable = null;
        }
    }

    private class AnimatedZoomRunnable implements Runnable {
        private final float mFocalX;
        private final float mFocalY;
        private final long mStartTime = System.currentTimeMillis();
        private final float mZoomEnd;
        private final float mZoomStart;

        public AnimatedZoomRunnable(float currentZoom, float targetZoom, float focalX, float focalY) {
            this.mFocalX = focalX;
            this.mFocalY = focalY;
            this.mZoomStart = currentZoom;
            this.mZoomEnd = targetZoom;
        }

        public void run() {
            float t = interpolate();
            float f = this.mZoomStart;
            PhotoViewAttacher.this.onGestureListener.onScale((f + ((this.mZoomEnd - f) * t)) / PhotoViewAttacher.this.getScale(), this.mFocalX, this.mFocalY);
            if (t < 1.0f) {
                Compat.postOnAnimation(PhotoViewAttacher.this.mImageView, this);
            }
        }

        private float interpolate() {
            return PhotoViewAttacher.this.mInterpolator.getInterpolation(Math.min(1.0f, (((float) (System.currentTimeMillis() - this.mStartTime)) * 1.0f) / ((float) PhotoViewAttacher.this.mZoomDuration)));
        }
    }

    private class FlingRunnable implements Runnable {
        private int mCurrentX;
        private int mCurrentY;
        private final OverScroller mScroller;

        public FlingRunnable(Context context) {
            this.mScroller = new OverScroller(context);
        }

        public void cancelFling() {
            this.mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY) {
            int minX;
            int maxX;
            int minY;
            int maxY;
            int i = viewWidth;
            int i2 = viewHeight;
            RectF rect = PhotoViewAttacher.this.getDisplayRect();
            if (rect != null) {
                int startX = Math.round(-rect.left);
                if (((float) i) < rect.width()) {
                    minX = 0;
                    maxX = Math.round(rect.width() - ((float) i));
                } else {
                    minX = startX;
                    maxX = startX;
                }
                int startY = Math.round(-rect.top);
                if (((float) i2) < rect.height()) {
                    minY = 0;
                    maxY = Math.round(rect.height() - ((float) i2));
                } else {
                    minY = startY;
                    maxY = startY;
                }
                this.mCurrentX = startX;
                this.mCurrentY = startY;
                if (startX == maxX && startY == maxY) {
                    int i3 = maxY;
                    int i4 = startY;
                    int i5 = maxX;
                    return;
                }
                int i6 = maxY;
                int i7 = startY;
                int i8 = maxX;
                this.mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
            }
        }

        public void run() {
            if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
                int newX = this.mScroller.getCurrX();
                int newY = this.mScroller.getCurrY();
                PhotoViewAttacher.this.mSuppMatrix.postTranslate((float) (this.mCurrentX - newX), (float) (this.mCurrentY - newY));
                PhotoViewAttacher.this.checkAndDisplayMatrix();
                this.mCurrentX = newX;
                this.mCurrentY = newY;
                Compat.postOnAnimation(PhotoViewAttacher.this.mImageView, this);
            }
        }
    }
}
