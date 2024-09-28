package im.bclpbkiauv.ui.hviews.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import java.lang.ref.WeakReference;

public class MryLayoutHelper implements MryLayout {
    private int mBorderColor;
    private RectF mBorderRect;
    private int mBorderWidth;
    private int mBottomDividerAlpha;
    private int mBottomDividerColor;
    private int mBottomDividerHeight;
    private int mBottomDividerInsetLeft;
    private int mBottomDividerInsetRight;
    private Paint mClipPaint;
    private Context mContext;
    private Paint mDividerPaint;
    private int mHeightLimit;
    private int mHeightMini;
    /* access modifiers changed from: private */
    public int mHideRadiusSide;
    /* access modifiers changed from: private */
    public boolean mIsOutlineExcludePadding;
    private boolean mIsShowBorderOnlyBeforeL;
    private int mLeftDividerAlpha;
    private int mLeftDividerColor;
    private int mLeftDividerInsetBottom;
    private int mLeftDividerInsetTop;
    private int mLeftDividerWidth;
    private PorterDuffXfermode mMode;
    private int mOuterNormalColor;
    /* access modifiers changed from: private */
    public int mOutlineInsetBottom;
    /* access modifiers changed from: private */
    public int mOutlineInsetLeft;
    /* access modifiers changed from: private */
    public int mOutlineInsetRight;
    /* access modifiers changed from: private */
    public int mOutlineInsetTop;
    private WeakReference<View> mOwner;
    private Path mPath;
    /* access modifiers changed from: private */
    public int mRadius;
    private float[] mRadiusArray;
    private int mRightDividerAlpha;
    private int mRightDividerColor;
    private int mRightDividerInsetBottom;
    private int mRightDividerInsetTop;
    private int mRightDividerWidth;
    /* access modifiers changed from: private */
    public float mShadowAlpha;
    private int mShadowColor;
    /* access modifiers changed from: private */
    public int mShadowElevation;
    private int mTopDividerAlpha;
    private int mTopDividerColor;
    private int mTopDividerHeight;
    private int mTopDividerInsetLeft;
    private int mTopDividerInsetRight;
    private int mWidthLimit;
    private int mWidthMini;

    public MryLayoutHelper(Context context, AttributeSet attrs, int defAttr, View owner) {
        this(context, attrs, defAttr, 0, owner);
    }

    public MryLayoutHelper(Context context, AttributeSet attrs, int defAttr, int defStyleRes, View owner) {
        Context context2 = context;
        AttributeSet attributeSet = attrs;
        int i = defAttr;
        int i2 = defStyleRes;
        this.mWidthLimit = 0;
        this.mHeightLimit = 0;
        this.mWidthMini = 0;
        this.mHeightMini = 0;
        this.mTopDividerHeight = 0;
        this.mTopDividerInsetLeft = 0;
        this.mTopDividerInsetRight = 0;
        this.mTopDividerAlpha = 255;
        this.mBottomDividerHeight = 0;
        this.mBottomDividerInsetLeft = 0;
        this.mBottomDividerInsetRight = 0;
        this.mBottomDividerAlpha = 255;
        this.mLeftDividerWidth = 0;
        this.mLeftDividerInsetTop = 0;
        this.mLeftDividerInsetBottom = 0;
        this.mLeftDividerAlpha = 255;
        this.mRightDividerWidth = 0;
        this.mRightDividerInsetTop = 0;
        this.mRightDividerInsetBottom = 0;
        this.mRightDividerAlpha = 255;
        this.mHideRadiusSide = 0;
        this.mBorderColor = 0;
        int i3 = 1;
        this.mBorderWidth = 1;
        this.mOuterNormalColor = 0;
        this.mIsOutlineExcludePadding = false;
        this.mPath = new Path();
        this.mIsShowBorderOnlyBeforeL = true;
        this.mShadowElevation = 0;
        this.mShadowColor = -10066330;
        this.mOutlineInsetLeft = 0;
        this.mOutlineInsetRight = 0;
        this.mOutlineInsetTop = 0;
        this.mOutlineInsetBottom = 0;
        this.mContext = context2;
        this.mOwner = new WeakReference<>(owner);
        int color = Theme.getColor(Theme.key_divider);
        this.mTopDividerColor = color;
        this.mBottomDividerColor = color;
        this.mMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        Paint paint = new Paint();
        this.mClipPaint = paint;
        paint.setAntiAlias(true);
        this.mShadowAlpha = MryResHelper.getAttrFloatValue(context2, (int) R.attr.mryGeneralShadowAlpha);
        this.mBorderRect = new RectF();
        int radius = 0;
        int shadow = 0;
        boolean useThemeGeneralShadowElevation = false;
        if (!(attributeSet == null && i == 0 && i2 == 0)) {
            TypedArray ta = context2.obtainStyledAttributes(attributeSet, R.styleable.MryLayout, i, i2);
            int count = ta.getIndexCount();
            int i4 = 0;
            while (i4 < count) {
                int index = ta.getIndex(i4);
                if (index == 0) {
                    this.mWidthLimit = ta.getDimensionPixelSize(index, this.mWidthLimit);
                } else if (index == i3) {
                    this.mHeightLimit = ta.getDimensionPixelSize(index, this.mHeightLimit);
                } else if (index == 2) {
                    this.mWidthMini = ta.getDimensionPixelSize(index, this.mWidthMini);
                } else if (index == 3) {
                    this.mHeightMini = ta.getDimensionPixelSize(index, this.mHeightMini);
                } else if (index == 30) {
                    this.mTopDividerColor = ta.getColor(index, this.mTopDividerColor);
                } else if (index == 31) {
                    this.mTopDividerHeight = ta.getDimensionPixelSize(index, this.mTopDividerHeight);
                } else if (index == 32) {
                    this.mTopDividerInsetLeft = ta.getDimensionPixelSize(index, this.mTopDividerInsetLeft);
                } else if (index == 33) {
                    this.mTopDividerInsetRight = ta.getDimensionPixelSize(index, this.mTopDividerInsetRight);
                } else if (index == 6) {
                    this.mBottomDividerColor = ta.getColor(index, this.mBottomDividerColor);
                } else if (index == 7) {
                    this.mBottomDividerHeight = ta.getDimensionPixelSize(index, this.mBottomDividerHeight);
                } else if (index == 8) {
                    this.mBottomDividerInsetLeft = ta.getDimensionPixelSize(index, this.mBottomDividerInsetLeft);
                } else if (index == 9) {
                    this.mBottomDividerInsetRight = ta.getDimensionPixelSize(index, this.mBottomDividerInsetRight);
                } else if (index == 11) {
                    this.mLeftDividerColor = ta.getColor(index, this.mLeftDividerColor);
                } else if (index == 14) {
                    this.mLeftDividerWidth = ta.getDimensionPixelSize(index, this.mLeftDividerWidth);
                } else if (index == 13) {
                    this.mLeftDividerInsetTop = ta.getDimensionPixelSize(index, this.mLeftDividerInsetTop);
                } else if (index == 12) {
                    this.mLeftDividerInsetBottom = ta.getDimensionPixelSize(index, this.mLeftDividerInsetBottom);
                } else if (index == 22) {
                    this.mRightDividerColor = ta.getColor(index, this.mRightDividerColor);
                } else if (index == 25) {
                    this.mRightDividerWidth = ta.getDimensionPixelSize(index, this.mRightDividerWidth);
                } else if (index == 24) {
                    this.mRightDividerInsetTop = ta.getDimensionPixelSize(index, this.mRightDividerInsetTop);
                } else if (index == 23) {
                    this.mRightDividerInsetBottom = ta.getDimensionPixelSize(index, this.mRightDividerInsetBottom);
                } else if (index == 4) {
                    this.mBorderColor = ta.getColor(index, this.mBorderColor);
                } else if (index == 5) {
                    this.mBorderWidth = ta.getDimensionPixelSize(index, this.mBorderWidth);
                } else if (index == 21) {
                    radius = ta.getDimensionPixelSize(index, 0);
                } else if (index == 15) {
                    this.mOuterNormalColor = ta.getColor(index, this.mOuterNormalColor);
                } else if (index == 10) {
                    this.mHideRadiusSide = ta.getColor(index, this.mHideRadiusSide);
                } else if (index == 29) {
                    this.mIsShowBorderOnlyBeforeL = ta.getBoolean(index, this.mIsShowBorderOnlyBeforeL);
                } else if (index == 28) {
                    shadow = ta.getDimensionPixelSize(index, shadow);
                } else if (index == 26) {
                    this.mShadowAlpha = ta.getFloat(index, this.mShadowAlpha);
                } else if (index == 27) {
                    this.mShadowColor = ta.getColor(index, -10066330);
                } else if (index == 34) {
                    useThemeGeneralShadowElevation = ta.getBoolean(index, false);
                } else if (index == 18) {
                    this.mOutlineInsetLeft = ta.getDimensionPixelSize(index, 0);
                } else if (index == 19) {
                    this.mOutlineInsetRight = ta.getDimensionPixelSize(index, 0);
                } else if (index == 20) {
                    this.mOutlineInsetTop = ta.getDimensionPixelSize(index, 0);
                } else if (index == 17) {
                    this.mOutlineInsetBottom = ta.getDimensionPixelSize(index, 0);
                } else if (index == 16) {
                    this.mIsOutlineExcludePadding = ta.getBoolean(index, false);
                }
                i4++;
                i3 = 1;
            }
            ta.recycle();
        }
        if (shadow == 0 && useThemeGeneralShadowElevation) {
            shadow = MryResHelper.getAttrDimen(context2, R.attr.mryGeneralShadowElevation);
        }
        setRadiusAndShadow(radius, this.mHideRadiusSide, shadow, this.mShadowAlpha);
    }

    public void setUseThemeGeneralShadowElevation() {
        int attrDimen = MryResHelper.getAttrDimen(this.mContext, R.attr.mryGeneralShadowElevation);
        this.mShadowElevation = attrDimen;
        setRadiusAndShadow(this.mRadius, this.mHideRadiusSide, attrDimen, this.mShadowAlpha);
    }

    public void setOutlineExcludePadding(boolean outlineExcludePadding) {
        View owner;
        if (useFeature() && (owner = (View) this.mOwner.get()) != null) {
            this.mIsOutlineExcludePadding = outlineExcludePadding;
            owner.invalidateOutline();
        }
    }

    public boolean setWidthLimit(int widthLimit) {
        if (this.mWidthLimit == widthLimit) {
            return false;
        }
        this.mWidthLimit = widthLimit;
        return true;
    }

    public boolean setHeightLimit(int heightLimit) {
        if (this.mHeightLimit == heightLimit) {
            return false;
        }
        this.mHeightLimit = heightLimit;
        return true;
    }

    public void updateLeftSeparatorColor(int color) {
        if (this.mLeftDividerColor != color) {
            this.mLeftDividerColor = color;
            invalidate();
        }
    }

    public void updateBottomSeparatorColor(int color) {
        if (this.mBottomDividerColor != color) {
            this.mBottomDividerColor = color;
            invalidate();
        }
    }

    public void updateTopSeparatorColor(int color) {
        if (this.mTopDividerColor != color) {
            this.mTopDividerColor = color;
            invalidate();
        }
    }

    public void updateRightSeparatorColor(int color) {
        if (this.mRightDividerColor != color) {
            this.mRightDividerColor = color;
            invalidate();
        }
    }

    public int getShadowElevation() {
        return this.mShadowElevation;
    }

    public float getShadowAlpha() {
        return this.mShadowAlpha;
    }

    public int getShadowColor() {
        return this.mShadowColor;
    }

    public void setOutlineInset(int left, int top, int right, int bottom) {
        View owner;
        if (useFeature() && (owner = (View) this.mOwner.get()) != null) {
            this.mOutlineInsetLeft = left;
            this.mOutlineInsetRight = right;
            this.mOutlineInsetTop = top;
            this.mOutlineInsetBottom = bottom;
            owner.invalidateOutline();
        }
    }

    public void setShowBorderOnlyBeforeL(boolean showBorderOnlyBeforeL) {
        this.mIsShowBorderOnlyBeforeL = showBorderOnlyBeforeL;
        invalidate();
    }

    public void setShadowElevation(int elevation) {
        if (this.mShadowElevation != elevation) {
            this.mShadowElevation = elevation;
            invalidateOutline();
        }
    }

    public void setShadowAlpha(float shadowAlpha) {
        if (this.mShadowAlpha != shadowAlpha) {
            this.mShadowAlpha = shadowAlpha;
            invalidateOutline();
        }
    }

    public void setShadowColor(int shadowColor) {
        if (this.mShadowColor != shadowColor) {
            this.mShadowColor = shadowColor;
            setShadowColorInner(shadowColor);
        }
    }

    private void setShadowColorInner(int shadowColor) {
        View owner;
        if (Build.VERSION.SDK_INT >= 28 && (owner = (View) this.mOwner.get()) != null) {
            owner.setOutlineAmbientShadowColor(shadowColor);
            owner.setOutlineSpotShadowColor(shadowColor);
        }
    }

    private void invalidateOutline() {
        View owner;
        if (useFeature() && (owner = (View) this.mOwner.get()) != null) {
            int i = this.mShadowElevation;
            if (i == 0) {
                owner.setElevation(0.0f);
            } else {
                owner.setElevation((float) i);
            }
            owner.invalidateOutline();
        }
    }

    private void invalidate() {
        View owner = (View) this.mOwner.get();
        if (owner != null) {
            owner.invalidate();
        }
    }

    public void setHideRadiusSide(int hideRadiusSide) {
        if (this.mHideRadiusSide != hideRadiusSide) {
            setRadiusAndShadow(this.mRadius, hideRadiusSide, this.mShadowElevation, this.mShadowAlpha);
        }
    }

    public int getHideRadiusSide() {
        return this.mHideRadiusSide;
    }

    public void setRadius(int radius) {
        if (this.mRadius != radius) {
            setRadiusAndShadow(radius, this.mShadowElevation, this.mShadowAlpha);
        }
    }

    public void setRadius(int radius, int hideRadiusSide) {
        if (this.mRadius != radius || hideRadiusSide != this.mHideRadiusSide) {
            setRadiusAndShadow(radius, hideRadiusSide, this.mShadowElevation, this.mShadowAlpha);
        }
    }

    public int getRadius() {
        return this.mRadius;
    }

    public void setRadiusAndShadow(int radius, int shadowElevation, float shadowAlpha) {
        setRadiusAndShadow(radius, this.mHideRadiusSide, shadowElevation, shadowAlpha);
    }

    public void setRadiusAndShadow(int radius, int hideRadiusSide, int shadowElevation, float shadowAlpha) {
        setRadiusAndShadow(radius, hideRadiusSide, shadowElevation, this.mShadowColor, shadowAlpha);
    }

    public void setRadiusAndShadow(int radius, int hideRadiusSide, int shadowElevation, int shadowColor, float shadowAlpha) {
        int i = radius;
        int i2 = hideRadiusSide;
        View owner = (View) this.mOwner.get();
        if (owner != null) {
            this.mRadius = i;
            this.mHideRadiusSide = i2;
            boolean z = false;
            if (i > 0) {
                if (i2 == 1) {
                    this.mRadiusArray = new float[]{0.0f, 0.0f, 0.0f, 0.0f, (float) i, (float) i, (float) i, (float) i};
                } else if (i2 == 2) {
                    this.mRadiusArray = new float[]{(float) i, (float) i, 0.0f, 0.0f, 0.0f, 0.0f, (float) i, (float) i};
                } else if (i2 == 3) {
                    this.mRadiusArray = new float[]{(float) i, (float) i, (float) i, (float) i, 0.0f, 0.0f, 0.0f, 0.0f};
                } else if (i2 == 4) {
                    this.mRadiusArray = new float[]{0.0f, 0.0f, (float) i, (float) i, (float) i, (float) i, 0.0f, 0.0f};
                } else {
                    this.mRadiusArray = null;
                }
            }
            this.mShadowElevation = shadowElevation;
            this.mShadowAlpha = shadowAlpha;
            this.mShadowColor = shadowColor;
            if (useFeature()) {
                int i3 = this.mShadowElevation;
                if (i3 == 0) {
                    owner.setElevation(0.0f);
                } else {
                    owner.setElevation((float) i3);
                }
                setShadowColorInner(this.mShadowColor);
                owner.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        float shadowAlpha;
                        int w = view.getWidth();
                        int h = view.getHeight();
                        if (w != 0 && h != 0) {
                            if (MryLayoutHelper.this.isRadiusWithSideHidden()) {
                                int left = 0;
                                int top = 0;
                                int right = w;
                                int bottom = h;
                                if (MryLayoutHelper.this.mHideRadiusSide == 4) {
                                    left = 0 - MryLayoutHelper.this.mRadius;
                                } else if (MryLayoutHelper.this.mHideRadiusSide == 1) {
                                    top = 0 - MryLayoutHelper.this.mRadius;
                                } else if (MryLayoutHelper.this.mHideRadiusSide == 2) {
                                    right += MryLayoutHelper.this.mRadius;
                                } else if (MryLayoutHelper.this.mHideRadiusSide == 3) {
                                    bottom += MryLayoutHelper.this.mRadius;
                                }
                                outline.setRoundRect(left, top, right, bottom, (float) MryLayoutHelper.this.mRadius);
                                return;
                            }
                            int top2 = MryLayoutHelper.this.mOutlineInsetTop;
                            int bottom2 = Math.max(top2 + 1, h - MryLayoutHelper.this.mOutlineInsetBottom);
                            int left2 = MryLayoutHelper.this.mOutlineInsetLeft;
                            int right2 = w - MryLayoutHelper.this.mOutlineInsetRight;
                            if (MryLayoutHelper.this.mIsOutlineExcludePadding) {
                                left2 += view.getPaddingLeft();
                                top2 += view.getPaddingTop();
                                right2 = Math.max(left2 + 1, right2 - view.getPaddingRight());
                                bottom2 = Math.max(top2 + 1, bottom2 - view.getPaddingBottom());
                            }
                            float shadowAlpha2 = MryLayoutHelper.this.mShadowAlpha;
                            if (MryLayoutHelper.this.mShadowElevation == 0) {
                                shadowAlpha = 1.0f;
                            } else {
                                shadowAlpha = shadowAlpha2;
                            }
                            outline.setAlpha(shadowAlpha);
                            if (MryLayoutHelper.this.mRadius <= 0) {
                                outline.setRect(left2, top2, right2, bottom2);
                                return;
                            }
                            outline.setRoundRect(left2, top2, right2, bottom2, (float) MryLayoutHelper.this.mRadius);
                        }
                    }
                });
                if (this.mRadius > 0) {
                    z = true;
                }
                owner.setClipToOutline(z);
            }
            owner.invalidate();
        }
    }

    public boolean isRadiusWithSideHidden() {
        return this.mRadius > 0 && this.mHideRadiusSide != 0;
    }

    public void updateTopDivider(int topInsetLeft, int topInsetRight, int topDividerHeight, int topDividerColor) {
        this.mTopDividerInsetLeft = topInsetLeft;
        this.mTopDividerInsetRight = topInsetRight;
        this.mTopDividerHeight = topDividerHeight;
        this.mTopDividerColor = topDividerColor;
    }

    public void updateBottomDivider(int bottomInsetLeft, int bottomInsetRight, int bottomDividerHeight, int bottomDividerColor) {
        this.mBottomDividerInsetLeft = bottomInsetLeft;
        this.mBottomDividerInsetRight = bottomInsetRight;
        this.mBottomDividerColor = bottomDividerColor;
        this.mBottomDividerHeight = bottomDividerHeight;
    }

    public void updateLeftDivider(int leftInsetTop, int leftInsetBottom, int leftDividerWidth, int leftDividerColor) {
        this.mLeftDividerInsetTop = leftInsetTop;
        this.mLeftDividerInsetBottom = leftInsetBottom;
        this.mLeftDividerWidth = leftDividerWidth;
        this.mLeftDividerColor = leftDividerColor;
    }

    public void updateRightDivider(int rightInsetTop, int rightInsetBottom, int rightDividerWidth, int rightDividerColor) {
        this.mRightDividerInsetTop = rightInsetTop;
        this.mRightDividerInsetBottom = rightInsetBottom;
        this.mRightDividerWidth = rightDividerWidth;
        this.mRightDividerColor = rightDividerColor;
    }

    public void onlyShowTopDivider(int topInsetLeft, int topInsetRight, int topDividerHeight, int topDividerColor) {
        updateTopDivider(topInsetLeft, topInsetRight, topDividerHeight, topDividerColor);
        this.mLeftDividerWidth = 0;
        this.mRightDividerWidth = 0;
        this.mBottomDividerHeight = 0;
    }

    public void onlyShowBottomDivider(int bottomInsetLeft, int bottomInsetRight, int bottomDividerHeight, int bottomDividerColor) {
        updateBottomDivider(bottomInsetLeft, bottomInsetRight, bottomDividerHeight, bottomDividerColor);
        this.mLeftDividerWidth = 0;
        this.mRightDividerWidth = 0;
        this.mTopDividerHeight = 0;
    }

    public void onlyShowLeftDivider(int leftInsetTop, int leftInsetBottom, int leftDividerWidth, int leftDividerColor) {
        updateLeftDivider(leftInsetTop, leftInsetBottom, leftDividerWidth, leftDividerColor);
        this.mRightDividerWidth = 0;
        this.mTopDividerHeight = 0;
        this.mBottomDividerHeight = 0;
    }

    public void onlyShowRightDivider(int rightInsetTop, int rightInsetBottom, int rightDividerWidth, int rightDividerColor) {
        updateRightDivider(rightInsetTop, rightInsetBottom, rightDividerWidth, rightDividerColor);
        this.mLeftDividerWidth = 0;
        this.mTopDividerHeight = 0;
        this.mBottomDividerHeight = 0;
    }

    public void setTopDividerAlpha(int dividerAlpha) {
        this.mTopDividerAlpha = dividerAlpha;
    }

    public void setBottomDividerAlpha(int dividerAlpha) {
        this.mBottomDividerAlpha = dividerAlpha;
    }

    public void setLeftDividerAlpha(int dividerAlpha) {
        this.mLeftDividerAlpha = dividerAlpha;
    }

    public void setRightDividerAlpha(int dividerAlpha) {
        this.mRightDividerAlpha = dividerAlpha;
    }

    public int handleMiniWidth(int widthMeasureSpec, int measuredWidth) {
        int i;
        if (View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824 || measuredWidth >= (i = this.mWidthMini)) {
            return widthMeasureSpec;
        }
        return View.MeasureSpec.makeMeasureSpec(i, 1073741824);
    }

    public int handleMiniHeight(int heightMeasureSpec, int measuredHeight) {
        int i;
        if (View.MeasureSpec.getMode(heightMeasureSpec) == 1073741824 || measuredHeight >= (i = this.mHeightMini)) {
            return heightMeasureSpec;
        }
        return View.MeasureSpec.makeMeasureSpec(i, 1073741824);
    }

    public int getMeasuredWidthSpec(int widthMeasureSpec) {
        if (this.mWidthLimit <= 0 || View.MeasureSpec.getSize(widthMeasureSpec) <= this.mWidthLimit) {
            return widthMeasureSpec;
        }
        if (View.MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE) {
            return View.MeasureSpec.makeMeasureSpec(this.mWidthLimit, Integer.MIN_VALUE);
        }
        return View.MeasureSpec.makeMeasureSpec(this.mWidthLimit, 1073741824);
    }

    public int getMeasuredHeightSpec(int heightMeasureSpec) {
        if (this.mHeightLimit <= 0 || View.MeasureSpec.getSize(heightMeasureSpec) <= this.mHeightLimit) {
            return heightMeasureSpec;
        }
        if (View.MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE) {
            return View.MeasureSpec.makeMeasureSpec(this.mWidthLimit, Integer.MIN_VALUE);
        }
        return View.MeasureSpec.makeMeasureSpec(this.mWidthLimit, 1073741824);
    }

    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
    }

    public void setBorderWidth(int borderWidth) {
        this.mBorderWidth = borderWidth;
    }

    public void setOuterNormalColor(int color) {
        this.mOuterNormalColor = color;
        View owner = (View) this.mOwner.get();
        if (owner != null) {
            owner.invalidate();
        }
    }

    public boolean hasTopSeparator() {
        return this.mTopDividerHeight > 0;
    }

    public boolean hasRightSeparator() {
        return this.mRightDividerWidth > 0;
    }

    public boolean hasBottomSeparator() {
        return this.mBottomDividerHeight > 0;
    }

    public boolean hasLeftSeparator() {
        return this.mLeftDividerWidth > 0;
    }

    public boolean hasBorder() {
        return this.mBorderWidth > 0;
    }

    public void drawDividers(Canvas canvas, int w, int h) {
        View owner = (View) this.mOwner.get();
        if (owner != null) {
            if (this.mDividerPaint == null && (this.mTopDividerHeight > 0 || this.mBottomDividerHeight > 0 || this.mLeftDividerWidth > 0 || this.mRightDividerWidth > 0)) {
                this.mDividerPaint = new Paint();
            }
            canvas.save();
            canvas.translate((float) owner.getScrollX(), (float) owner.getScrollY());
            int i = this.mTopDividerHeight;
            if (i > 0) {
                this.mDividerPaint.setStrokeWidth((float) i);
                this.mDividerPaint.setColor(this.mTopDividerColor);
                int i2 = this.mTopDividerAlpha;
                if (i2 < 255) {
                    this.mDividerPaint.setAlpha(i2);
                }
                float y = (((float) this.mTopDividerHeight) * 1.0f) / 2.0f;
                canvas.drawLine((float) this.mTopDividerInsetLeft, y, (float) (w - this.mTopDividerInsetRight), y, this.mDividerPaint);
            }
            int i3 = this.mBottomDividerHeight;
            if (i3 > 0) {
                this.mDividerPaint.setStrokeWidth((float) i3);
                this.mDividerPaint.setColor(this.mBottomDividerColor);
                int i4 = this.mBottomDividerAlpha;
                if (i4 < 255) {
                    this.mDividerPaint.setAlpha(i4);
                }
                float y2 = (float) Math.floor((double) (((float) h) - ((((float) this.mBottomDividerHeight) * 1.0f) / 2.0f)));
                canvas.drawLine((float) this.mBottomDividerInsetLeft, y2, (float) (w - this.mBottomDividerInsetRight), y2, this.mDividerPaint);
            }
            int i5 = this.mLeftDividerWidth;
            if (i5 > 0) {
                this.mDividerPaint.setStrokeWidth((float) i5);
                this.mDividerPaint.setColor(this.mLeftDividerColor);
                int i6 = this.mLeftDividerAlpha;
                if (i6 < 255) {
                    this.mDividerPaint.setAlpha(i6);
                }
                canvas.drawLine(0.0f, (float) this.mLeftDividerInsetTop, 0.0f, (float) (h - this.mLeftDividerInsetBottom), this.mDividerPaint);
            }
            int i7 = this.mRightDividerWidth;
            if (i7 > 0) {
                this.mDividerPaint.setStrokeWidth((float) i7);
                this.mDividerPaint.setColor(this.mRightDividerColor);
                int i8 = this.mRightDividerAlpha;
                if (i8 < 255) {
                    this.mDividerPaint.setAlpha(i8);
                }
                canvas.drawLine((float) w, (float) this.mRightDividerInsetTop, (float) w, (float) (h - this.mRightDividerInsetBottom), this.mDividerPaint);
            }
            canvas.restore();
        }
    }

    public void dispatchRoundBorderDraw(Canvas canvas) {
        View owner = (View) this.mOwner.get();
        if (owner != null) {
            boolean z = true;
            boolean needCheckFakeOuterNormalDraw = this.mRadius > 0 && !useFeature() && this.mOuterNormalColor != 0;
            if (this.mBorderWidth <= 0 || this.mBorderColor == 0) {
                z = false;
            }
            boolean needDrawBorder = z;
            if (!needCheckFakeOuterNormalDraw && !needDrawBorder) {
                return;
            }
            if (!this.mIsShowBorderOnlyBeforeL || !useFeature() || this.mShadowElevation == 0) {
                int width = canvas.getWidth();
                int height = canvas.getHeight();
                canvas.save();
                canvas.translate((float) owner.getScrollX(), (float) owner.getScrollY());
                if (this.mIsOutlineExcludePadding) {
                    this.mBorderRect.set((float) owner.getPaddingLeft(), (float) owner.getPaddingTop(), (float) (width - owner.getPaddingRight()), (float) (height - owner.getPaddingBottom()));
                } else {
                    this.mBorderRect.set(0.0f, 0.0f, (float) width, (float) height);
                }
                if (needCheckFakeOuterNormalDraw) {
                    int layerId = canvas.saveLayer(0.0f, 0.0f, (float) width, (float) height, (Paint) null, 31);
                    canvas.drawColor(this.mOuterNormalColor);
                    this.mClipPaint.setColor(this.mOuterNormalColor);
                    this.mClipPaint.setStyle(Paint.Style.FILL);
                    this.mClipPaint.setXfermode(this.mMode);
                    float[] fArr = this.mRadiusArray;
                    if (fArr == null) {
                        RectF rectF = this.mBorderRect;
                        int i = this.mRadius;
                        canvas.drawRoundRect(rectF, (float) i, (float) i, this.mClipPaint);
                    } else {
                        drawRoundRect(canvas, this.mBorderRect, fArr, this.mClipPaint);
                    }
                    this.mClipPaint.setXfermode((Xfermode) null);
                    canvas.restoreToCount(layerId);
                }
                if (needDrawBorder) {
                    this.mClipPaint.setColor(this.mBorderColor);
                    this.mClipPaint.setStrokeWidth((float) this.mBorderWidth);
                    this.mClipPaint.setStyle(Paint.Style.STROKE);
                    float[] fArr2 = this.mRadiusArray;
                    if (fArr2 != null) {
                        drawRoundRect(canvas, this.mBorderRect, fArr2, this.mClipPaint);
                    } else {
                        int i2 = this.mRadius;
                        if (i2 <= 0) {
                            canvas.drawRect(this.mBorderRect, this.mClipPaint);
                        } else {
                            canvas.drawRoundRect(this.mBorderRect, (float) i2, (float) i2, this.mClipPaint);
                        }
                    }
                }
                canvas.restore();
            }
        }
    }

    private void drawRoundRect(Canvas canvas, RectF rect, float[] radiusArray, Paint paint) {
        this.mPath.reset();
        this.mPath.addRoundRect(rect, radiusArray, Path.Direction.CW);
        canvas.drawPath(this.mPath, paint);
    }

    public static boolean useFeature() {
        return Build.VERSION.SDK_INT >= 21;
    }
}
