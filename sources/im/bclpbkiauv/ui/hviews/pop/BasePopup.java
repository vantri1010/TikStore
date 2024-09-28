package im.bclpbkiauv.ui.hviews.pop;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.Transition;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import androidx.core.widget.PopupWindowCompat;
import im.bclpbkiauv.messenger.utils.BlurKit;
import im.bclpbkiauv.ui.hviews.pop.BasePopup;

public abstract class BasePopup<T extends BasePopup> implements PopupWindow.OnDismissListener {
    private static final float DEFAULT_DIM = 0.7f;
    private static final String TAG = "EasyPopup";
    /* access modifiers changed from: private */
    public boolean isAtAnchorViewMethod = false;
    private boolean isBackgroundDim;
    private boolean isBlurBackground;
    /* access modifiers changed from: private */
    public boolean isNeedReMeasureWH = false;
    /* access modifiers changed from: private */
    public boolean isRealWHAlready = false;
    /* access modifiers changed from: private */
    public View mAnchorView;
    private int mAnimationStyle;
    private View mContentView;
    private Context mContext;
    private int mDimColor = -16777216;
    private float mDimValue = DEFAULT_DIM;
    private ViewGroup mDimView;
    private Transition mEnterTransition;
    private Transition mExitTransition;
    private boolean mFocusAndOutsideEnable = true;
    private boolean mFocusable = true;
    /* access modifiers changed from: private */
    public int mHeight = -2;
    private int mInputMethodMode = 0;
    private int mLayoutId;
    /* access modifiers changed from: private */
    public int mOffsetX;
    /* access modifiers changed from: private */
    public int mOffsetY;
    private PopupWindow.OnDismissListener mOnDismissListener;
    /* access modifiers changed from: private */
    public OnRealWHAlreadyListener mOnRealWHAlreadyListener;
    private boolean mOutsideTouchable = true;
    /* access modifiers changed from: private */
    public PopupWindow mPopupWindow;
    private int mSoftInputMode = 1;
    /* access modifiers changed from: private */
    public int mWidth = -2;
    /* access modifiers changed from: private */
    public int mXGravity = 1;
    /* access modifiers changed from: private */
    public int mYGravity = 2;

    public interface OnRealWHAlreadyListener {
        void onRealWHAlready(BasePopup basePopup, int i, int i2, int i3, int i4);
    }

    /* access modifiers changed from: protected */
    public abstract void initAttributes();

    /* access modifiers changed from: protected */
    public abstract void initViews(View view, T t);

    /* access modifiers changed from: protected */
    public T self() {
        return this;
    }

    public T apply() {
        if (this.mPopupWindow == null) {
            this.mPopupWindow = new PopupWindow();
        }
        onPopupWindowCreated();
        initContentViewAndWH();
        onPopupWindowViewCreated(this.mContentView);
        int i = this.mAnimationStyle;
        if (i != 0) {
            this.mPopupWindow.setAnimationStyle(i);
        }
        initFocusAndBack();
        this.mPopupWindow.setOnDismissListener(this);
        if (Build.VERSION.SDK_INT >= 23) {
            Transition transition = this.mEnterTransition;
            if (transition != null) {
                this.mPopupWindow.setEnterTransition(transition);
            }
            Transition transition2 = this.mExitTransition;
            if (transition2 != null) {
                this.mPopupWindow.setExitTransition(transition2);
            }
        }
        if (this.isBlurBackground) {
            BlurKit.init(this.mContext);
        }
        return self();
    }

    private void initContentViewAndWH() {
        Context context;
        if (this.mContentView == null) {
            if (this.mLayoutId == 0 || (context = this.mContext) == null) {
                throw new IllegalArgumentException("The content view is null,the layoutId=" + this.mLayoutId + ",context=" + this.mContext);
            }
            this.mContentView = LayoutInflater.from(context).inflate(this.mLayoutId, (ViewGroup) null);
        }
        this.mPopupWindow.setContentView(this.mContentView);
        int i = this.mWidth;
        if (i > 0 || i == -2 || i == -1) {
            this.mPopupWindow.setWidth(this.mWidth);
        } else {
            this.mPopupWindow.setWidth(-2);
        }
        int i2 = this.mHeight;
        if (i2 > 0 || i2 == -2 || i2 == -1) {
            this.mPopupWindow.setHeight(this.mHeight);
        } else {
            this.mPopupWindow.setHeight(-2);
        }
        measureContentView();
        registerOnGlobalLayoutListener();
        this.mPopupWindow.setInputMethodMode(this.mInputMethodMode);
        this.mPopupWindow.setSoftInputMode(this.mSoftInputMode);
    }

    private void initFocusAndBack() {
        if (!this.mFocusAndOutsideEnable) {
            this.mPopupWindow.setFocusable(true);
            this.mPopupWindow.setOutsideTouchable(false);
            this.mPopupWindow.setBackgroundDrawable((Drawable) null);
            this.mPopupWindow.getContentView().setFocusable(true);
            this.mPopupWindow.getContentView().setFocusableInTouchMode(true);
            this.mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode != 4) {
                        return false;
                    }
                    BasePopup.this.mPopupWindow.dismiss();
                    return true;
                }
            });
            this.mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    if (event.getAction() == 0 && (x < 0 || x >= BasePopup.this.mWidth || y < 0 || y >= BasePopup.this.mHeight)) {
                        Log.d(BasePopup.TAG, "onTouch outside:mWidth=" + BasePopup.this.mWidth + ",mHeight=" + BasePopup.this.mHeight);
                        return true;
                    } else if (event.getAction() != 4) {
                        return false;
                    } else {
                        Log.d(BasePopup.TAG, "onTouch outside event:mWidth=" + BasePopup.this.mWidth + ",mHeight=" + BasePopup.this.mHeight);
                        return true;
                    }
                }
            });
            return;
        }
        this.mPopupWindow.setFocusable(this.mFocusable);
        this.mPopupWindow.setOutsideTouchable(this.mOutsideTouchable);
        this.mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
    }

    /* access modifiers changed from: protected */
    public void onPopupWindowCreated() {
        initAttributes();
    }

    /* access modifiers changed from: protected */
    public void onPopupWindowViewCreated(View contentView) {
        initViews(contentView, self());
    }

    /* access modifiers changed from: protected */
    public void onPopupWindowDismiss() {
    }

    private void measureContentView() {
        View contentView = getContentView();
        if (this.mWidth <= 0 || this.mHeight <= 0) {
            contentView.measure(0, 0);
            if (this.mWidth <= 0) {
                this.mWidth = contentView.getMeasuredWidth();
            }
            if (this.mHeight <= 0) {
                this.mHeight = contentView.getMeasuredHeight();
            }
        }
    }

    private void registerOnGlobalLayoutListener() {
        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                BasePopup.this.getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                BasePopup basePopup = BasePopup.this;
                int unused = basePopup.mWidth = basePopup.getContentView().getWidth();
                BasePopup basePopup2 = BasePopup.this;
                int unused2 = basePopup2.mHeight = basePopup2.getContentView().getHeight();
                boolean unused3 = BasePopup.this.isRealWHAlready = true;
                boolean unused4 = BasePopup.this.isNeedReMeasureWH = false;
                if (BasePopup.this.mOnRealWHAlreadyListener != null) {
                    OnRealWHAlreadyListener access$500 = BasePopup.this.mOnRealWHAlreadyListener;
                    BasePopup basePopup3 = BasePopup.this;
                    access$500.onRealWHAlready(basePopup3, basePopup3.mWidth, BasePopup.this.mHeight, BasePopup.this.mAnchorView == null ? 0 : BasePopup.this.mAnchorView.getWidth(), BasePopup.this.mAnchorView == null ? 0 : BasePopup.this.mAnchorView.getHeight());
                }
                if (BasePopup.this.isShowing() && BasePopup.this.isAtAnchorViewMethod) {
                    BasePopup basePopup4 = BasePopup.this;
                    basePopup4.updateLocation(basePopup4.mWidth, BasePopup.this.mHeight, BasePopup.this.mAnchorView, BasePopup.this.mYGravity, BasePopup.this.mXGravity, BasePopup.this.mOffsetX, BasePopup.this.mOffsetY);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateLocation(int width, int height, View anchor, int yGravity, int xGravity, int x, int y) {
        if (this.mPopupWindow != null) {
            this.mPopupWindow.update(anchor, calculateX(anchor, xGravity, width, x), calculateY(anchor, yGravity, height, y), width, height);
        }
    }

    public T setContext(Context context) {
        this.mContext = context;
        return self();
    }

    public T setContentView(View contentView) {
        this.mContentView = contentView;
        this.mLayoutId = 0;
        return self();
    }

    public T setContentView(int layoutId) {
        this.mContentView = null;
        this.mLayoutId = layoutId;
        return self();
    }

    public T setContentView(Context context, int layoutId) {
        this.mContext = context;
        this.mContentView = null;
        this.mLayoutId = layoutId;
        return self();
    }

    public T setContentView(View contentView, int width, int height) {
        this.mContentView = contentView;
        this.mLayoutId = 0;
        this.mWidth = width;
        this.mHeight = height;
        return self();
    }

    public T setContentView(int layoutId, int width, int height) {
        this.mContentView = null;
        this.mLayoutId = layoutId;
        this.mWidth = width;
        this.mHeight = height;
        return self();
    }

    public T setContentView(Context context, int layoutId, int width, int height) {
        this.mContext = context;
        this.mContentView = null;
        this.mLayoutId = layoutId;
        this.mWidth = width;
        this.mHeight = height;
        return self();
    }

    public T setWidth(int width) {
        this.mWidth = width;
        return self();
    }

    public T setHeight(int height) {
        this.mHeight = height;
        return self();
    }

    public T setAnchorView(View view) {
        this.mAnchorView = view;
        return self();
    }

    public T setYGravity(int yGravity) {
        this.mYGravity = yGravity;
        return self();
    }

    public T setXGravity(int xGravity) {
        this.mXGravity = xGravity;
        return self();
    }

    public T setOffsetX(int offsetX) {
        this.mOffsetX = offsetX;
        return self();
    }

    public T setOffsetY(int offsetY) {
        this.mOffsetY = offsetY;
        return self();
    }

    public T setAnimationStyle(int animationStyle) {
        this.mAnimationStyle = animationStyle;
        return self();
    }

    public T setFocusable(boolean focusable) {
        this.mFocusable = focusable;
        return self();
    }

    public T setOutsideTouchable(boolean outsideTouchable) {
        this.mOutsideTouchable = outsideTouchable;
        return self();
    }

    public T setFocusAndOutsideEnable(boolean focusAndOutsideEnable) {
        this.mFocusAndOutsideEnable = focusAndOutsideEnable;
        return self();
    }

    public T setBlurBackground(boolean isBlur) {
        this.isBlurBackground = isBlur;
        return self();
    }

    public T setBackgroundDimEnable(boolean isDim) {
        this.isBackgroundDim = isDim;
        return self();
    }

    public T setDimValue(float dimValue) {
        this.mDimValue = dimValue;
        return self();
    }

    public T setDimColor(int color) {
        this.mDimColor = color;
        return self();
    }

    public T setDimView(ViewGroup dimView) {
        this.mDimView = dimView;
        return self();
    }

    public T setEnterTransition(Transition enterTransition) {
        this.mEnterTransition = enterTransition;
        return self();
    }

    public T setExitTransition(Transition exitTransition) {
        this.mExitTransition = exitTransition;
        return self();
    }

    public T setInputMethodMode(int mode) {
        this.mInputMethodMode = mode;
        return self();
    }

    public T setSoftInputMode(int mode) {
        this.mSoftInputMode = mode;
        return self();
    }

    public T setNeedReMeasureWH(boolean needReMeasureWH) {
        this.isNeedReMeasureWH = needReMeasureWH;
        return self();
    }

    private void checkIsApply(boolean isAtAnchorView) {
        if (this.isAtAnchorViewMethod != isAtAnchorView) {
            this.isAtAnchorViewMethod = isAtAnchorView;
        }
        if (this.mPopupWindow == null) {
            apply();
        }
    }

    public void showAsDropDown() {
        View view = this.mAnchorView;
        if (view != null) {
            showAsDropDown(view, this.mOffsetX, this.mOffsetY);
        }
    }

    public void showAsDropDown(View anchor, int offsetX, int offsetY) {
        checkIsApply(false);
        handleBackgroundDim();
        this.mAnchorView = anchor;
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;
        if (this.isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        this.mPopupWindow.showAsDropDown(anchor, this.mOffsetX, this.mOffsetY);
    }

    public void showAsDropDown(View anchor) {
        checkIsApply(false);
        handleBackgroundDim();
        this.mAnchorView = anchor;
        if (this.isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        this.mPopupWindow.showAsDropDown(anchor);
    }

    public void showAsDropDown(View anchor, int offsetX, int offsetY, int gravity) {
        checkIsApply(false);
        handleBackgroundDim();
        this.mAnchorView = anchor;
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;
        if (this.isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        PopupWindowCompat.showAsDropDown(this.mPopupWindow, anchor, this.mOffsetX, this.mOffsetY, gravity);
    }

    public void showAtLocation(View parent, int gravity, int offsetX, int offsetY) {
        checkIsApply(false);
        handleBackgroundDim();
        this.mAnchorView = parent;
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;
        if (this.isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        this.mPopupWindow.showAtLocation(parent, gravity, this.mOffsetX, this.mOffsetY);
    }

    public void showAtAnchorView() {
        View view = this.mAnchorView;
        if (view != null) {
            showAtAnchorView(view, this.mYGravity, this.mXGravity);
        }
    }

    public void showAtAnchorView(View anchor, int vertGravity, int horizGravity) {
        showAtAnchorView(anchor, vertGravity, horizGravity, 0, 0);
    }

    public void showAtAnchorView(View anchor, int vertGravity, int horizGravity, int x, int y) {
        checkIsApply(true);
        this.mAnchorView = anchor;
        this.mOffsetX = x;
        this.mOffsetY = y;
        this.mYGravity = vertGravity;
        this.mXGravity = horizGravity;
        if (Build.VERSION.SDK_INT >= 18) {
            handleBlurBackground();
        }
        int x2 = calculateX(anchor, horizGravity, this.mWidth, this.mOffsetX);
        int y2 = calculateY(anchor, vertGravity, this.mHeight, this.mOffsetY);
        if (this.isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        PopupWindowCompat.showAsDropDown(this.mPopupWindow, anchor, x2, y2, 0);
    }

    private int calculateY(View anchor, int vertGravity, int measuredH, int y) {
        if (vertGravity == 0) {
            return y - ((anchor.getHeight() / 2) + (measuredH / 2));
        }
        if (vertGravity == 1) {
            return y - (anchor.getHeight() + measuredH);
        }
        if (vertGravity == 3) {
            return y - anchor.getHeight();
        }
        if (vertGravity != 4) {
            return y;
        }
        return y - measuredH;
    }

    private int calculateX(View anchor, int horizGravity, int measuredW, int x) {
        if (horizGravity == 0) {
            return x + ((anchor.getWidth() / 2) - (measuredW / 2));
        }
        if (horizGravity == 1) {
            return x - measuredW;
        }
        if (horizGravity == 2) {
            return x + anchor.getWidth();
        }
        if (horizGravity != 4) {
            return x;
        }
        return x - (measuredW - anchor.getWidth());
    }

    public T setOnDismissListener(PopupWindow.OnDismissListener listener) {
        this.mOnDismissListener = listener;
        return self();
    }

    public T setOnRealWHAlreadyListener(OnRealWHAlreadyListener listener) {
        this.mOnRealWHAlreadyListener = listener;
        return self();
    }

    private void handleBackgroundDim() {
        if (Build.VERSION.SDK_INT >= 18 && this.isBackgroundDim) {
            ViewGroup viewGroup = this.mDimView;
            if (viewGroup != null) {
                applyDim(viewGroup);
            } else if (getContentView() != null && getContentView().getContext() != null && (getContentView().getContext() instanceof Activity)) {
                applyDim((Activity) getContentView().getContext());
            }
        }
    }

    private void handleBlurBackground() {
        if (this.isBlurBackground && Build.VERSION.SDK_INT >= 17) {
            ViewGroup viewGroup = this.mDimView;
            if (viewGroup != null) {
                applyBlurDim(viewGroup);
            } else if (getContentView() != null && getContentView().getContext() != null && (getContentView().getContext() instanceof Activity)) {
                applyBlurDim((Activity) getContentView().getContext());
            }
        }
    }

    private void applyBlurDim(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        Drawable dimDrawable = new BitmapDrawable((Resources) null, BlurKit.getInstance().fastBlur(parent, 10, 0.25f));
        dimDrawable.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        parent.getOverlay().add(dimDrawable);
    }

    private void applyBlurDim(ViewGroup dimView) {
        dimView.getOverlay().add(new BitmapDrawable((Resources) null, BlurKit.getInstance().fastBlur(dimView, 10, 0.25f)));
    }

    private void applyDim(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        Drawable dimDrawable = new ColorDrawable(this.mDimColor);
        dimDrawable.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dimDrawable.setAlpha((int) (this.mDimValue * 255.0f));
        parent.getOverlay().add(dimDrawable);
    }

    private void applyDim(ViewGroup dimView) {
        Drawable dimDrawable = new ColorDrawable(this.mDimColor);
        dimDrawable.setBounds(0, 0, dimView.getWidth(), dimView.getHeight());
        dimDrawable.setAlpha((int) (this.mDimValue * 255.0f));
        dimView.getOverlay().add(dimDrawable);
    }

    private void clearBackgroundDim() {
        Activity activity;
        if (Build.VERSION.SDK_INT < 18) {
            return;
        }
        if (this.isBackgroundDim || this.isBlurBackground) {
            ViewGroup viewGroup = this.mDimView;
            if (viewGroup != null) {
                clearDim(viewGroup);
            } else if (getContentView() != null && (activity = (Activity) getContentView().getContext()) != null) {
                clearDim(activity);
            }
        }
    }

    private void clearDim(Activity activity) {
        ((ViewGroup) activity.getWindow().getDecorView().getRootView()).getOverlay().clear();
    }

    private void clearDim(ViewGroup dimView) {
        dimView.getOverlay().clear();
    }

    public View getContentView() {
        PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow != null) {
            return popupWindow.getContentView();
        }
        return null;
    }

    public PopupWindow getPopupWindow() {
        return this.mPopupWindow;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getXGravity() {
        return this.mXGravity;
    }

    public int getYGravity() {
        return this.mYGravity;
    }

    public int getOffsetX() {
        return this.mOffsetX;
    }

    public int getOffsetY() {
        return this.mOffsetY;
    }

    public boolean isShowing() {
        PopupWindow popupWindow = this.mPopupWindow;
        return popupWindow != null && popupWindow.isShowing();
    }

    public boolean isRealWHAlready() {
        return this.isRealWHAlready;
    }

    public <T extends View> T findViewById(int viewId) {
        if (getContentView() != null) {
            return getContentView().findViewById(viewId);
        }
        return null;
    }

    public void dismiss() {
        PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void onDismiss() {
        handleDismiss();
    }

    private void handleDismiss() {
        PopupWindow.OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
        clearBackgroundDim();
        PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow != null && popupWindow.isShowing()) {
            this.mPopupWindow.dismiss();
        }
        onPopupWindowDismiss();
    }
}
