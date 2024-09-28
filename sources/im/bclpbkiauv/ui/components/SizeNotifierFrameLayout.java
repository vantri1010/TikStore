package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.components.WallpaperParallaxEffect;

public class SizeNotifierFrameLayout extends FrameLayout {
    private Drawable backgroundDrawable;
    private int bottomClip;
    private SizeNotifierFrameLayoutDelegate delegate;
    private int keyboardHeight;
    private boolean occupyStatusBar = true;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale = 1.0f;
    private boolean paused = true;
    private Rect rect = new Rect();
    private float translationX;
    private float translationY;

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int i, boolean z);
    }

    public SizeNotifierFrameLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public void setBackgroundImage(Drawable bitmap, boolean motion) {
        this.backgroundDrawable = bitmap;
        if (motion) {
            if (this.parallaxEffect == null) {
                WallpaperParallaxEffect wallpaperParallaxEffect = new WallpaperParallaxEffect(getContext());
                this.parallaxEffect = wallpaperParallaxEffect;
                wallpaperParallaxEffect.setCallback(new WallpaperParallaxEffect.Callback() {
                    public final void onOffsetsChanged(int i, int i2) {
                        SizeNotifierFrameLayout.this.lambda$setBackgroundImage$0$SizeNotifierFrameLayout(i, i2);
                    }
                });
                if (!(getMeasuredWidth() == 0 || getMeasuredHeight() == 0)) {
                    this.parallaxScale = this.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                }
            }
            if (!this.paused) {
                this.parallaxEffect.setEnabled(true);
            }
        } else {
            WallpaperParallaxEffect wallpaperParallaxEffect2 = this.parallaxEffect;
            if (wallpaperParallaxEffect2 != null) {
                wallpaperParallaxEffect2.setEnabled(false);
                this.parallaxEffect = null;
                this.parallaxScale = 1.0f;
                this.translationX = 0.0f;
                this.translationY = 0.0f;
            }
        }
        invalidate();
    }

    public /* synthetic */ void lambda$setBackgroundImage$0$SizeNotifierFrameLayout(int offsetX, int offsetY) {
        this.translationX = (float) offsetX;
        this.translationY = (float) offsetY;
        invalidate();
    }

    public Drawable getBackgroundImage() {
        return this.backgroundDrawable;
    }

    public void setDelegate(SizeNotifierFrameLayoutDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
    }

    public void onPause() {
        WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
        if (wallpaperParallaxEffect != null) {
            wallpaperParallaxEffect.setEnabled(false);
        }
        this.paused = true;
    }

    public void onResume() {
        WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
        if (wallpaperParallaxEffect != null) {
            wallpaperParallaxEffect.setEnabled(true);
        }
        this.paused = false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        if (this.rect.bottom == 0 && this.rect.top == 0) {
            return 0;
        }
        return Math.max(0, ((rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top));
    }

    public void notifyHeightChanged() {
        WallpaperParallaxEffect wallpaperParallaxEffect = this.parallaxEffect;
        if (wallpaperParallaxEffect != null) {
            this.parallaxScale = wallpaperParallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
        }
        if (this.delegate != null) {
            this.keyboardHeight = getKeyboardHeight();
            post(new Runnable(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SizeNotifierFrameLayout.this.lambda$notifyHeightChanged$1$SizeNotifierFrameLayout(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$notifyHeightChanged$1$SizeNotifierFrameLayout(boolean isWidthGreater) {
        SizeNotifierFrameLayoutDelegate sizeNotifierFrameLayoutDelegate = this.delegate;
        if (sizeNotifierFrameLayoutDelegate != null) {
            sizeNotifierFrameLayoutDelegate.onSizeChanged(this.keyboardHeight, isWidthGreater);
        }
    }

    public void setBottomClip(int value) {
        this.bottomClip = value;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Drawable drawable = this.backgroundDrawable;
        if (drawable == null) {
            super.onDraw(canvas);
        } else if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
            if (this.bottomClip != 0) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - this.bottomClip);
            }
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.backgroundDrawable.draw(canvas);
            if (this.bottomClip != 0) {
                canvas.restore();
            }
        } else if (!(drawable instanceof BitmapDrawable)) {
        } else {
            if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                canvas.save();
                float scale = 2.0f / AndroidUtilities.density;
                canvas.scale(scale, scale);
                this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / scale)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / scale)));
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
                return;
            }
            int actionBarHeight = (isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
            int viewHeight = getMeasuredHeight() - actionBarHeight;
            float scaleX = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
            float scaleY = ((float) (this.keyboardHeight + viewHeight)) / ((float) this.backgroundDrawable.getIntrinsicHeight());
            float scale2 = scaleX < scaleY ? scaleY : scaleX;
            int width = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * scale2 * this.parallaxScale));
            int height = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * scale2 * this.parallaxScale));
            int x = ((getMeasuredWidth() - width) / 2) + ((int) this.translationX);
            int y = (((viewHeight - height) + this.keyboardHeight) / 2) + actionBarHeight + ((int) this.translationY);
            canvas.save();
            canvas.clipRect(0, actionBarHeight, width, getMeasuredHeight() - this.bottomClip);
            this.backgroundDrawable.setAlpha(255);
            this.backgroundDrawable.setBounds(x, y, x + width, y + height);
            this.backgroundDrawable.draw(canvas);
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isActionBarVisible() {
        return true;
    }
}
