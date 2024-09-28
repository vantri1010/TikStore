package com.blankj.utilcode.util;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;
import android.view.View;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

public class ShadowUtils {
    private static final int SHADOW_TAG = -16;

    public static void apply(View... views) {
        if (views != null) {
            for (View view : views) {
                apply(view, new Config());
            }
        }
    }

    public static void apply(View view, Config builder) {
        if (view != null && builder != null) {
            Drawable background = view.getBackground();
            Object tag = view.getTag(SHADOW_TAG);
            if (tag instanceof Drawable) {
                ViewCompat.setBackground(view, (Drawable) tag);
                return;
            }
            Drawable background2 = builder.apply(background);
            ViewCompat.setBackground(view, background2);
            view.setTag(SHADOW_TAG, background2);
        }
    }

    public static class Config {
        private static final int SHADOW_COLOR_DEFAULT = -1342177280;
        private static final int SHADOW_SIZE = dp2px(8.0f);
        private boolean isCircle = false;
        private int mShadowColorNormal = SHADOW_COLOR_DEFAULT;
        private int mShadowColorPressed = SHADOW_COLOR_DEFAULT;
        private float mShadowMaxSizeNormal = -1.0f;
        private float mShadowMaxSizePressed = -1.0f;
        private float mShadowRadius = -1.0f;
        private float mShadowSizeNormal = -1.0f;
        private float mShadowSizePressed = -1.0f;

        public Config setShadowRadius(float radius) {
            this.mShadowRadius = radius;
            if (!this.isCircle) {
                return this;
            }
            throw new IllegalArgumentException("Set circle needn't set radius.");
        }

        public Config setCircle() {
            this.isCircle = true;
            if (this.mShadowRadius == -1.0f) {
                return this;
            }
            throw new IllegalArgumentException("Set circle needn't set radius.");
        }

        public Config setShadowSize(int size) {
            return setShadowSize(size, size);
        }

        public Config setShadowSize(int sizeNormal, int sizePressed) {
            this.mShadowSizeNormal = (float) sizeNormal;
            this.mShadowSizePressed = (float) sizePressed;
            return this;
        }

        public Config setShadowMaxSize(int maxSize) {
            return setShadowMaxSize(maxSize, maxSize);
        }

        public Config setShadowMaxSize(int maxSizeNormal, int maxSizePressed) {
            this.mShadowMaxSizeNormal = (float) maxSizeNormal;
            this.mShadowMaxSizePressed = (float) maxSizePressed;
            return this;
        }

        public Config setShadowColor(int color) {
            return setShadowColor(color, color);
        }

        public Config setShadowColor(int colorNormal, int colorPressed) {
            this.mShadowColorNormal = colorNormal;
            this.mShadowColorPressed = colorPressed;
            return this;
        }

        /* access modifiers changed from: package-private */
        public Drawable apply(Drawable src) {
            if (src == null) {
                src = new ColorDrawable(0);
            }
            StateListDrawable drawable = new StateListDrawable();
            Drawable drawable2 = src;
            drawable.addState(new int[]{16842919}, new ShadowDrawable(drawable2, getShadowRadius(), getShadowSizeNormal(), getShadowMaxSizeNormal(), this.mShadowColorPressed, this.isCircle));
            drawable.addState(StateSet.WILD_CARD, new ShadowDrawable(drawable2, getShadowRadius(), getShadowSizePressed(), getShadowMaxSizePressed(), this.mShadowColorNormal, this.isCircle));
            return drawable;
        }

        private float getShadowRadius() {
            if (this.mShadowRadius == -1.0f) {
                this.mShadowRadius = 0.0f;
            }
            return this.mShadowRadius;
        }

        private float getShadowSizeNormal() {
            if (this.mShadowSizeNormal == -1.0f) {
                this.mShadowSizeNormal = (float) SHADOW_SIZE;
            }
            return this.mShadowSizeNormal;
        }

        private float getShadowSizePressed() {
            if (this.mShadowSizePressed == -1.0f) {
                this.mShadowSizePressed = getShadowSizeNormal();
            }
            return this.mShadowSizePressed;
        }

        private float getShadowMaxSizeNormal() {
            if (this.mShadowMaxSizeNormal == -1.0f) {
                this.mShadowMaxSizeNormal = getShadowSizeNormal();
            }
            return this.mShadowMaxSizeNormal;
        }

        private float getShadowMaxSizePressed() {
            if (this.mShadowMaxSizePressed == -1.0f) {
                this.mShadowMaxSizePressed = getShadowSizePressed();
            }
            return this.mShadowMaxSizePressed;
        }

        private static int dp2px(float dpValue) {
            return (int) ((dpValue * Resources.getSystem().getDisplayMetrics().density) + 0.5f);
        }
    }

    public static class ShadowDrawable extends DrawableWrapper {
        private static final double COS_45 = Math.cos(Math.toRadians(45.0d));
        private boolean isCircle;
        private boolean mAddPaddingForCorners = false;
        private RectF mContentBounds;
        private float mCornerRadius;
        private Paint mCornerShadowPaint;
        private Path mCornerShadowPath;
        private boolean mDirty = true;
        private Paint mEdgeShadowPaint;
        private float mMaxShadowSize;
        private float mRawMaxShadowSize;
        private float mRawShadowSize;
        private float mRotation;
        private float mShadowBottomScale = 1.0f;
        private final int mShadowEndColor;
        private float mShadowHorizScale = 1.0f;
        private float mShadowMultiplier = 1.0f;
        private float mShadowSize;
        private final int mShadowStartColor;
        private float mShadowTopScale = 1.0f;

        public /* bridge */ /* synthetic */ int getChangingConfigurations() {
            return super.getChangingConfigurations();
        }

        public /* bridge */ /* synthetic */ Drawable getCurrent() {
            return super.getCurrent();
        }

        public /* bridge */ /* synthetic */ int getIntrinsicHeight() {
            return super.getIntrinsicHeight();
        }

        public /* bridge */ /* synthetic */ int getIntrinsicWidth() {
            return super.getIntrinsicWidth();
        }

        public /* bridge */ /* synthetic */ int getMinimumHeight() {
            return super.getMinimumHeight();
        }

        public /* bridge */ /* synthetic */ int getMinimumWidth() {
            return super.getMinimumWidth();
        }

        public /* bridge */ /* synthetic */ int[] getState() {
            return super.getState();
        }

        public /* bridge */ /* synthetic */ Region getTransparentRegion() {
            return super.getTransparentRegion();
        }

        public /* bridge */ /* synthetic */ Drawable getWrappedDrawable() {
            return super.getWrappedDrawable();
        }

        public /* bridge */ /* synthetic */ void invalidateDrawable(Drawable drawable) {
            super.invalidateDrawable(drawable);
        }

        public /* bridge */ /* synthetic */ boolean isAutoMirrored() {
            return super.isAutoMirrored();
        }

        public /* bridge */ /* synthetic */ boolean isStateful() {
            return super.isStateful();
        }

        public /* bridge */ /* synthetic */ void jumpToCurrentState() {
            super.jumpToCurrentState();
        }

        public /* bridge */ /* synthetic */ void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
            super.scheduleDrawable(drawable, runnable, j);
        }

        public /* bridge */ /* synthetic */ void setAutoMirrored(boolean z) {
            super.setAutoMirrored(z);
        }

        public /* bridge */ /* synthetic */ void setChangingConfigurations(int i) {
            super.setChangingConfigurations(i);
        }

        public /* bridge */ /* synthetic */ void setColorFilter(ColorFilter colorFilter) {
            super.setColorFilter(colorFilter);
        }

        public /* bridge */ /* synthetic */ void setDither(boolean z) {
            super.setDither(z);
        }

        public /* bridge */ /* synthetic */ void setFilterBitmap(boolean z) {
            super.setFilterBitmap(z);
        }

        public /* bridge */ /* synthetic */ void setHotspot(float f, float f2) {
            super.setHotspot(f, f2);
        }

        public /* bridge */ /* synthetic */ void setHotspotBounds(int i, int i2, int i3, int i4) {
            super.setHotspotBounds(i, i2, i3, i4);
        }

        public /* bridge */ /* synthetic */ boolean setState(int[] iArr) {
            return super.setState(iArr);
        }

        public /* bridge */ /* synthetic */ void setTint(int i) {
            super.setTint(i);
        }

        public /* bridge */ /* synthetic */ void setTintList(ColorStateList colorStateList) {
            super.setTintList(colorStateList);
        }

        public /* bridge */ /* synthetic */ void setTintMode(PorterDuff.Mode mode) {
            super.setTintMode(mode);
        }

        public /* bridge */ /* synthetic */ boolean setVisible(boolean z, boolean z2) {
            return super.setVisible(z, z2);
        }

        public /* bridge */ /* synthetic */ void setWrappedDrawable(Drawable drawable) {
            super.setWrappedDrawable(drawable);
        }

        public /* bridge */ /* synthetic */ void unscheduleDrawable(Drawable drawable, Runnable runnable) {
            super.unscheduleDrawable(drawable, runnable);
        }

        public ShadowDrawable(Drawable content, float radius, float shadowSize, float maxShadowSize, int shadowColor, boolean isCircle2) {
            super(content);
            this.mShadowStartColor = shadowColor;
            this.mShadowEndColor = 0;
            this.isCircle = isCircle2;
            if (isCircle2) {
                this.mShadowMultiplier = 1.0f;
                this.mShadowTopScale = 1.0f;
                this.mShadowHorizScale = 1.0f;
                this.mShadowBottomScale = 1.0f;
            }
            Paint paint = new Paint(5);
            this.mCornerShadowPaint = paint;
            paint.setStyle(Paint.Style.FILL);
            this.mCornerRadius = (float) Math.round(radius);
            this.mContentBounds = new RectF();
            Paint paint2 = new Paint(this.mCornerShadowPaint);
            this.mEdgeShadowPaint = paint2;
            paint2.setAntiAlias(false);
            setShadowSize(shadowSize, maxShadowSize);
        }

        private static int toEven(float value) {
            int i = Math.round(value);
            return i % 2 == 1 ? i - 1 : i;
        }

        public void setAddPaddingForCorners(boolean addPaddingForCorners) {
            this.mAddPaddingForCorners = addPaddingForCorners;
            invalidateSelf();
        }

        public void setAlpha(int alpha) {
            super.setAlpha(alpha);
            this.mCornerShadowPaint.setAlpha(alpha);
            this.mEdgeShadowPaint.setAlpha(alpha);
        }

        /* access modifiers changed from: protected */
        public void onBoundsChange(Rect bounds) {
            this.mDirty = true;
        }

        /* access modifiers changed from: package-private */
        public void setShadowSize(float shadowSize, float maxShadowSize) {
            if (shadowSize < 0.0f || maxShadowSize < 0.0f) {
                throw new IllegalArgumentException("invalid shadow size");
            }
            float shadowSize2 = (float) toEven(shadowSize);
            float maxShadowSize2 = (float) toEven(maxShadowSize);
            if (shadowSize2 > maxShadowSize2) {
                shadowSize2 = maxShadowSize2;
            }
            if (this.mRawShadowSize != shadowSize2 || this.mRawMaxShadowSize != maxShadowSize2) {
                this.mRawShadowSize = shadowSize2;
                this.mRawMaxShadowSize = maxShadowSize2;
                this.mShadowSize = (float) Math.round(this.mShadowMultiplier * shadowSize2);
                this.mMaxShadowSize = maxShadowSize2;
                this.mDirty = true;
                invalidateSelf();
            }
        }

        public boolean getPadding(Rect padding) {
            int vOffset = (int) Math.ceil((double) calculateVerticalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
            int hOffset = (int) Math.ceil((double) calculateHorizontalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
            padding.set(hOffset, vOffset, hOffset, vOffset);
            return true;
        }

        private float calculateVerticalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
            if (addPaddingForCorners) {
                return (float) (((double) (this.mShadowMultiplier * maxShadowSize)) + ((1.0d - COS_45) * ((double) cornerRadius)));
            }
            return this.mShadowMultiplier * maxShadowSize;
        }

        private static float calculateHorizontalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
            if (addPaddingForCorners) {
                return (float) (((double) maxShadowSize) + ((1.0d - COS_45) * ((double) cornerRadius)));
            }
            return maxShadowSize;
        }

        public int getOpacity() {
            return -3;
        }

        public void setCornerRadius(float radius) {
            float radius2 = (float) Math.round(radius);
            if (this.mCornerRadius != radius2) {
                this.mCornerRadius = radius2;
                this.mDirty = true;
                invalidateSelf();
            }
        }

        public void draw(Canvas canvas) {
            if (this.mDirty) {
                buildComponents(getBounds());
                this.mDirty = false;
            }
            drawShadow(canvas);
            super.draw(canvas);
        }

        /* access modifiers changed from: package-private */
        public final void setRotation(float rotation) {
            if (this.mRotation != rotation) {
                this.mRotation = rotation;
                invalidateSelf();
            }
        }

        private void drawShadow(Canvas canvas) {
            float shadowScaleHorizontal;
            float shadowScaleBottom;
            float shadowOffsetHorizontal;
            int saved;
            float shadowScaleTop;
            float shadowScaleBottom2;
            float shadowScaleHorizontal2;
            Canvas canvas2 = canvas;
            if (this.isCircle) {
                int saved2 = canvas.save();
                canvas2.translate(this.mContentBounds.centerX(), this.mContentBounds.centerY());
                canvas2.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
                canvas2.restoreToCount(saved2);
                return;
            }
            int rotateSaved = canvas.save();
            canvas2.rotate(this.mRotation, this.mContentBounds.centerX(), this.mContentBounds.centerY());
            float edgeShadowTop = (-this.mCornerRadius) - this.mShadowSize;
            float shadowOffset = this.mCornerRadius;
            boolean z = true;
            boolean drawHorizontalEdges = this.mContentBounds.width() - (shadowOffset * 2.0f) > 0.0f;
            if (this.mContentBounds.height() - (shadowOffset * 2.0f) <= 0.0f) {
                z = false;
            }
            boolean drawVerticalEdges = z;
            float f = this.mRawShadowSize;
            float shadowOffsetTop = f - (this.mShadowTopScale * f);
            float shadowOffsetHorizontal2 = f - (this.mShadowHorizScale * f);
            float shadowOffsetBottom = f - (this.mShadowBottomScale * f);
            float shadowScaleHorizontal3 = shadowOffset == 0.0f ? 1.0f : shadowOffset / (shadowOffset + shadowOffsetHorizontal2);
            float shadowScaleTop2 = shadowOffset == 0.0f ? 1.0f : shadowOffset / (shadowOffset + shadowOffsetTop);
            float shadowScaleBottom3 = shadowOffset == 0.0f ? 1.0f : shadowOffset / (shadowOffset + shadowOffsetBottom);
            int saved3 = canvas.save();
            canvas2.translate(this.mContentBounds.left + shadowOffset, this.mContentBounds.top + shadowOffset);
            canvas2.scale(shadowScaleHorizontal3, shadowScaleTop2);
            canvas2.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
            if (drawHorizontalEdges) {
                canvas2.scale(1.0f / shadowScaleHorizontal3, 1.0f);
                float f2 = shadowOffsetTop;
                saved = saved3;
                shadowScaleTop = shadowScaleTop2;
                shadowScaleBottom = shadowScaleBottom3;
                shadowScaleHorizontal = shadowScaleHorizontal3;
                float f3 = shadowOffsetHorizontal2;
                shadowOffsetHorizontal = 1.0f;
                canvas.drawRect(0.0f, edgeShadowTop, this.mContentBounds.width() - (shadowOffset * 2.0f), -this.mCornerRadius, this.mEdgeShadowPaint);
            } else {
                shadowScaleTop = shadowScaleTop2;
                shadowScaleBottom = shadowScaleBottom3;
                shadowScaleHorizontal = shadowScaleHorizontal3;
                float f4 = shadowOffsetTop;
                float f5 = shadowOffsetHorizontal2;
                shadowOffsetHorizontal = 1.0f;
                saved = saved3;
            }
            canvas2.restoreToCount(saved);
            int saved4 = canvas.save();
            canvas2.translate(this.mContentBounds.right - shadowOffset, this.mContentBounds.bottom - shadowOffset);
            float shadowScaleBottom4 = shadowScaleBottom;
            float shadowScaleHorizontal4 = shadowScaleHorizontal;
            canvas2.scale(shadowScaleHorizontal4, shadowScaleBottom4);
            canvas2.rotate(180.0f);
            canvas2.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
            if (drawHorizontalEdges) {
                canvas2.scale(shadowOffsetHorizontal / shadowScaleHorizontal4, shadowOffsetHorizontal);
                shadowScaleBottom2 = shadowScaleBottom4;
                boolean z2 = drawHorizontalEdges;
                shadowScaleHorizontal2 = shadowScaleHorizontal4;
                canvas.drawRect(0.0f, edgeShadowTop, this.mContentBounds.width() - (shadowOffset * 2.0f), -this.mCornerRadius, this.mEdgeShadowPaint);
            } else {
                shadowScaleBottom2 = shadowScaleBottom4;
                boolean z3 = drawHorizontalEdges;
                shadowScaleHorizontal2 = shadowScaleHorizontal4;
            }
            canvas2.restoreToCount(saved4);
            int saved5 = canvas.save();
            canvas2.translate(this.mContentBounds.left + shadowOffset, this.mContentBounds.bottom - shadowOffset);
            canvas2.scale(shadowScaleHorizontal2, shadowScaleBottom2);
            canvas2.rotate(270.0f);
            canvas2.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
            if (drawVerticalEdges) {
                canvas2.scale(1.0f / shadowScaleBottom2, 1.0f);
                canvas.drawRect(0.0f, edgeShadowTop, this.mContentBounds.height() - (shadowOffset * 2.0f), -this.mCornerRadius, this.mEdgeShadowPaint);
            }
            canvas2.restoreToCount(saved5);
            int saved6 = canvas.save();
            canvas2.translate(this.mContentBounds.right - shadowOffset, this.mContentBounds.top + shadowOffset);
            canvas2.scale(shadowScaleHorizontal2, shadowScaleTop);
            canvas2.rotate(90.0f);
            canvas2.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
            if (drawVerticalEdges) {
                canvas2.scale(1.0f / shadowScaleTop, 1.0f);
                canvas.drawRect(0.0f, edgeShadowTop, this.mContentBounds.height() - (2.0f * shadowOffset), -this.mCornerRadius, this.mEdgeShadowPaint);
            }
            canvas2.restoreToCount(saved6);
            canvas2.restoreToCount(rotateSaved);
        }

        private void buildShadowCorners() {
            if (this.isCircle) {
                float size = (this.mContentBounds.width() / 2.0f) - 1.0f;
                RectF innerBounds = new RectF(-size, -size, size, size);
                RectF outerBounds = new RectF(innerBounds);
                float f = this.mShadowSize;
                outerBounds.inset(-f, -f);
                Path path = this.mCornerShadowPath;
                if (path == null) {
                    this.mCornerShadowPath = new Path();
                } else {
                    path.reset();
                }
                this.mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
                this.mCornerShadowPath.moveTo(-size, 0.0f);
                this.mCornerShadowPath.rLineTo(-this.mShadowSize, 0.0f);
                this.mCornerShadowPath.arcTo(outerBounds, 180.0f, 180.0f, false);
                this.mCornerShadowPath.arcTo(outerBounds, 360.0f, 180.0f, false);
                this.mCornerShadowPath.arcTo(innerBounds, 540.0f, -180.0f, false);
                this.mCornerShadowPath.arcTo(innerBounds, 360.0f, -180.0f, false);
                this.mCornerShadowPath.close();
                float shadowRadius = -outerBounds.top;
                if (shadowRadius > 0.0f) {
                    Paint paint = this.mCornerShadowPaint;
                    RadialGradient radialGradient = r11;
                    Paint paint2 = paint;
                    RadialGradient radialGradient2 = new RadialGradient(0.0f, 0.0f, shadowRadius, new int[]{0, this.mShadowStartColor, this.mShadowEndColor}, new float[]{0.0f, size / shadowRadius, 1.0f}, Shader.TileMode.CLAMP);
                    paint2.setShader(radialGradient);
                    return;
                }
                return;
            }
            float f2 = this.mCornerRadius;
            RectF innerBounds2 = new RectF(-f2, -f2, f2, f2);
            RectF outerBounds2 = new RectF(innerBounds2);
            float f3 = this.mShadowSize;
            outerBounds2.inset(-f3, -f3);
            Path path2 = this.mCornerShadowPath;
            if (path2 == null) {
                this.mCornerShadowPath = new Path();
            } else {
                path2.reset();
            }
            this.mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
            this.mCornerShadowPath.moveTo(-this.mCornerRadius, 0.0f);
            this.mCornerShadowPath.rLineTo(-this.mShadowSize, 0.0f);
            this.mCornerShadowPath.arcTo(outerBounds2, 180.0f, 90.0f, false);
            this.mCornerShadowPath.arcTo(innerBounds2, 270.0f, -90.0f, false);
            this.mCornerShadowPath.close();
            float shadowRadius2 = -outerBounds2.top;
            if (shadowRadius2 > 0.0f) {
                float startRatio = this.mCornerRadius / shadowRadius2;
                Paint paint3 = this.mCornerShadowPaint;
                int[] iArr = {0, this.mShadowStartColor, this.mShadowEndColor};
                RadialGradient radialGradient3 = r10;
                int[] iArr2 = iArr;
                Paint paint4 = paint3;
                RadialGradient radialGradient4 = new RadialGradient(0.0f, 0.0f, shadowRadius2, iArr2, new float[]{0.0f, startRatio, 1.0f}, Shader.TileMode.CLAMP);
                paint4.setShader(radialGradient3);
            }
            this.mEdgeShadowPaint.setShader(new LinearGradient(0.0f, innerBounds2.top, 0.0f, outerBounds2.top, new int[]{this.mShadowStartColor, this.mShadowEndColor}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
            this.mEdgeShadowPaint.setAntiAlias(false);
        }

        private void buildComponents(Rect bounds) {
            if (this.isCircle) {
                this.mCornerRadius = (float) (bounds.width() / 2);
            }
            float verticalOffset = this.mRawMaxShadowSize * this.mShadowMultiplier;
            this.mContentBounds.set(((float) bounds.left) + this.mRawMaxShadowSize, ((float) bounds.top) + verticalOffset, ((float) bounds.right) - this.mRawMaxShadowSize, ((float) bounds.bottom) - verticalOffset);
            getWrappedDrawable().setBounds((int) this.mContentBounds.left, (int) this.mContentBounds.top, (int) this.mContentBounds.right, (int) this.mContentBounds.bottom);
            buildShadowCorners();
        }

        public float getCornerRadius() {
            return this.mCornerRadius;
        }

        public void setShadowSize(float size) {
            setShadowSize(size, this.mRawMaxShadowSize);
        }

        public void setMaxShadowSize(float size) {
            setShadowSize(this.mRawShadowSize, size);
        }

        public float getShadowSize() {
            return this.mRawShadowSize;
        }

        public float getMaxShadowSize() {
            return this.mRawMaxShadowSize;
        }

        public float getMinWidth() {
            float f = this.mRawMaxShadowSize;
            return (this.mRawMaxShadowSize * 2.0f) + (Math.max(f, this.mCornerRadius + (f / 2.0f)) * 2.0f);
        }

        public float getMinHeight() {
            float f = this.mRawMaxShadowSize;
            return (this.mRawMaxShadowSize * this.mShadowMultiplier * 2.0f) + (Math.max(f, this.mCornerRadius + ((this.mShadowMultiplier * f) / 2.0f)) * 2.0f);
        }
    }

    static class DrawableWrapper extends Drawable implements Drawable.Callback {
        private Drawable mDrawable;

        public DrawableWrapper(Drawable drawable) {
            setWrappedDrawable(drawable);
        }

        public void draw(Canvas canvas) {
            this.mDrawable.draw(canvas);
        }

        /* access modifiers changed from: protected */
        public void onBoundsChange(Rect bounds) {
            this.mDrawable.setBounds(bounds);
        }

        public void setChangingConfigurations(int configs) {
            this.mDrawable.setChangingConfigurations(configs);
        }

        public int getChangingConfigurations() {
            return this.mDrawable.getChangingConfigurations();
        }

        public void setDither(boolean dither) {
            this.mDrawable.setDither(dither);
        }

        public void setFilterBitmap(boolean filter) {
            this.mDrawable.setFilterBitmap(filter);
        }

        public void setAlpha(int alpha) {
            this.mDrawable.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter cf) {
            this.mDrawable.setColorFilter(cf);
        }

        public boolean isStateful() {
            return this.mDrawable.isStateful();
        }

        public boolean setState(int[] stateSet) {
            return this.mDrawable.setState(stateSet);
        }

        public int[] getState() {
            return this.mDrawable.getState();
        }

        public void jumpToCurrentState() {
            DrawableCompat.jumpToCurrentState(this.mDrawable);
        }

        public Drawable getCurrent() {
            return this.mDrawable.getCurrent();
        }

        public boolean setVisible(boolean visible, boolean restart) {
            return super.setVisible(visible, restart) || this.mDrawable.setVisible(visible, restart);
        }

        public int getOpacity() {
            return this.mDrawable.getOpacity();
        }

        public Region getTransparentRegion() {
            return this.mDrawable.getTransparentRegion();
        }

        public int getIntrinsicWidth() {
            return this.mDrawable.getIntrinsicWidth();
        }

        public int getIntrinsicHeight() {
            return this.mDrawable.getIntrinsicHeight();
        }

        public int getMinimumWidth() {
            return this.mDrawable.getMinimumWidth();
        }

        public int getMinimumHeight() {
            return this.mDrawable.getMinimumHeight();
        }

        public boolean getPadding(Rect padding) {
            return this.mDrawable.getPadding(padding);
        }

        public void invalidateDrawable(Drawable who) {
            invalidateSelf();
        }

        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            scheduleSelf(what, when);
        }

        public void unscheduleDrawable(Drawable who, Runnable what) {
            unscheduleSelf(what);
        }

        /* access modifiers changed from: protected */
        public boolean onLevelChange(int level) {
            return this.mDrawable.setLevel(level);
        }

        public void setAutoMirrored(boolean mirrored) {
            DrawableCompat.setAutoMirrored(this.mDrawable, mirrored);
        }

        public boolean isAutoMirrored() {
            return DrawableCompat.isAutoMirrored(this.mDrawable);
        }

        public void setTint(int tint) {
            DrawableCompat.setTint(this.mDrawable, tint);
        }

        public void setTintList(ColorStateList tint) {
            DrawableCompat.setTintList(this.mDrawable, tint);
        }

        public void setTintMode(PorterDuff.Mode tintMode) {
            DrawableCompat.setTintMode(this.mDrawable, tintMode);
        }

        public void setHotspot(float x, float y) {
            DrawableCompat.setHotspot(this.mDrawable, x, y);
        }

        public void setHotspotBounds(int left, int top, int right, int bottom) {
            DrawableCompat.setHotspotBounds(this.mDrawable, left, top, right, bottom);
        }

        public Drawable getWrappedDrawable() {
            return this.mDrawable;
        }

        public void setWrappedDrawable(Drawable drawable) {
            Drawable drawable2 = this.mDrawable;
            if (drawable2 != null) {
                drawable2.setCallback((Drawable.Callback) null);
            }
            this.mDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
            }
        }
    }
}
