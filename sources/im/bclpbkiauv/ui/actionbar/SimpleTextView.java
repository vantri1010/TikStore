package im.bclpbkiauv.ui.actionbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class SimpleTextView extends View implements Drawable.Callback {
    private static final int DIST_BETWEEN_SCROLLING_TEXT = 16;
    private static final int PIXELS_PER_SECOND = 50;
    private static final int PIXELS_PER_SECOND_SLOW = 30;
    private static final int SCROLL_DELAY_MS = 500;
    private static final int SCROLL_SLOWDOWN_PX = 100;
    private int currentScrollDelay;
    private int drawablePadding = AndroidUtilities.dp(4.0f);
    private GradientDrawable fadeDrawable;
    private GradientDrawable fadeDrawableBack;
    private int gravity = 51;
    private long lastUpdateTime;
    private int lastWidth;
    private Layout layout;
    private Drawable leftDrawable;
    private int leftDrawableTopPadding;
    private int offsetX;
    private int offsetY;
    private Drawable rightDrawable;
    private int rightDrawableTopPadding;
    private boolean scrollNonFitText;
    private float scrollingOffset;
    private SpannableStringBuilder spannableStringBuilder;
    private CharSequence text;
    private boolean textDoesNotFit;
    private int textHeight;
    private TextPaint textPaint = new TextPaint(1);
    private int textWidth;
    private int totalWidth;
    private boolean wasLayout;

    public SimpleTextView(Context context) {
        super(context);
        setImportantForAccessibility(1);
    }

    public void setTextColor(int color) {
        this.textPaint.setColor(color);
        invalidate();
    }

    public void setLinkTextColor(int color) {
        this.textPaint.linkColor = color;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setTextSize(int size) {
        int newSize = AndroidUtilities.dp((float) size);
        if (((float) newSize) != this.textPaint.getTextSize()) {
            this.textPaint.setTextSize((float) newSize);
            if (!recreateLayoutMaybe()) {
                invalidate();
            }
        }
    }

    public void setScrollNonFitText(boolean value) {
        if (this.scrollNonFitText != value) {
            this.scrollNonFitText = value;
            if (value) {
                this.fadeDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{-1, 0});
                this.fadeDrawableBack = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0, -1});
            }
            requestLayout();
        }
    }

    public void setGravity(int value) {
        this.gravity = value;
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
    }

    public int getSideDrawablesSize() {
        int size = 0;
        Drawable drawable = this.leftDrawable;
        if (drawable != null) {
            size = 0 + drawable.getIntrinsicWidth() + this.drawablePadding;
        }
        Drawable drawable2 = this.rightDrawable;
        if (drawable2 != null) {
            return size + drawable2.getIntrinsicWidth() + this.drawablePadding;
        }
        return size;
    }

    public Paint getPaint() {
        return this.textPaint;
    }

    private void calcOffset(int width) {
        if (this.layout.getLineCount() > 0) {
            boolean z = false;
            this.textWidth = (int) Math.ceil((double) this.layout.getLineWidth(0));
            this.textHeight = this.layout.getLineBottom(0);
            if ((this.gravity & 7) == 3) {
                this.offsetX = -((int) this.layout.getLineLeft(0));
            } else if (this.layout.getLineLeft(0) == 0.0f) {
                this.offsetX = width - this.textWidth;
            } else {
                this.offsetX = -AndroidUtilities.dp(8.0f);
            }
            this.offsetX += getPaddingLeft();
            if (this.textWidth > width) {
                z = true;
            }
            this.textDoesNotFit = z;
        }
    }

    private boolean createLayout(int width) {
        CharSequence string;
        if (this.text != null) {
            try {
                if (this.leftDrawable != null) {
                    width = (width - this.leftDrawable.getIntrinsicWidth()) - this.drawablePadding;
                }
                if (this.rightDrawable != null) {
                    width = (width - this.rightDrawable.getIntrinsicWidth()) - this.drawablePadding;
                }
                if (this.scrollNonFitText) {
                    string = this.text;
                } else {
                    string = TextUtils.ellipsize(this.text, this.textPaint, (float) width, TextUtils.TruncateAt.END);
                }
                this.layout = new StaticLayout(string, 0, string.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                calcOffset(width);
            } catch (Exception e) {
            }
        } else {
            this.layout = null;
            this.textWidth = 0;
            this.textHeight = 0;
        }
        invalidate();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalHeight;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (this.lastWidth != AndroidUtilities.displaySize.x) {
            this.lastWidth = AndroidUtilities.displaySize.x;
            this.scrollingOffset = 0.0f;
            this.currentScrollDelay = 500;
        }
        createLayout((width - getPaddingLeft()) - getPaddingRight());
        if (View.MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
            finalHeight = height;
        } else {
            finalHeight = this.textHeight;
        }
        setMeasuredDimension(width, finalHeight);
        if ((this.gravity & 112) == 16) {
            this.offsetY = (getMeasuredHeight() - this.textHeight) / 2;
        } else {
            this.offsetY = 0;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.wasLayout = true;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int textOffsetX = 0;
        this.totalWidth = this.textWidth;
        Drawable drawable = this.leftDrawable;
        if (drawable != null) {
            int x = (int) (-this.scrollingOffset);
            int y = ((this.textHeight - drawable.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
            Drawable drawable2 = this.leftDrawable;
            drawable2.setBounds(x, y, drawable2.getIntrinsicWidth() + x, this.leftDrawable.getIntrinsicHeight() + y);
            this.leftDrawable.draw(canvas);
            if ((this.gravity & 7) == 3) {
                textOffsetX = 0 + this.drawablePadding + this.leftDrawable.getIntrinsicWidth();
            }
            this.totalWidth += this.drawablePadding + this.leftDrawable.getIntrinsicWidth();
        }
        Drawable drawable3 = this.rightDrawable;
        if (drawable3 != null) {
            int x2 = this.textWidth + textOffsetX + this.drawablePadding + ((int) (-this.scrollingOffset));
            int y2 = ((this.textHeight - drawable3.getIntrinsicHeight()) / 2) + this.rightDrawableTopPadding;
            Drawable drawable4 = this.rightDrawable;
            drawable4.setBounds(x2, y2, drawable4.getIntrinsicWidth() + x2, this.rightDrawable.getIntrinsicHeight() + y2);
            this.rightDrawable.draw(canvas);
            this.totalWidth += this.drawablePadding + this.rightDrawable.getIntrinsicWidth();
        }
        int nextScrollX = this.totalWidth + AndroidUtilities.dp(16.0f);
        float f = this.scrollingOffset;
        if (f != 0.0f) {
            Drawable drawable5 = this.leftDrawable;
            if (drawable5 != null) {
                int x3 = ((int) (-f)) + nextScrollX;
                int y3 = ((this.textHeight - drawable5.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
                Drawable drawable6 = this.leftDrawable;
                drawable6.setBounds(x3, y3, drawable6.getIntrinsicWidth() + x3, this.leftDrawable.getIntrinsicHeight() + y3);
                this.leftDrawable.draw(canvas);
            }
            Drawable drawable7 = this.rightDrawable;
            if (drawable7 != null) {
                int x4 = this.textWidth + textOffsetX + this.drawablePadding + ((int) (-this.scrollingOffset)) + nextScrollX;
                int y4 = ((this.textHeight - drawable7.getIntrinsicHeight()) / 2) + this.rightDrawableTopPadding;
                Drawable drawable8 = this.rightDrawable;
                drawable8.setBounds(x4, y4, drawable8.getIntrinsicWidth() + x4, this.rightDrawable.getIntrinsicHeight() + y4);
                this.rightDrawable.draw(canvas);
            }
        }
        if (this.layout != null) {
            if (!(this.offsetX + textOffsetX == 0 && this.offsetY == 0 && this.scrollingOffset == 0.0f)) {
                canvas.save();
                canvas.translate(((float) (this.offsetX + textOffsetX)) - this.scrollingOffset, (float) this.offsetY);
            }
            this.layout.draw(canvas);
            if (this.scrollingOffset != 0.0f) {
                canvas.translate((float) nextScrollX, 0.0f);
                this.layout.draw(canvas);
            }
            if (!(this.offsetX + textOffsetX == 0 && this.offsetY == 0 && this.scrollingOffset == 0.0f)) {
                canvas.restore();
            }
            if (this.scrollNonFitText && (this.textDoesNotFit || this.scrollingOffset != 0.0f)) {
                if (this.scrollingOffset < ((float) AndroidUtilities.dp(10.0f))) {
                    this.fadeDrawable.setAlpha((int) ((this.scrollingOffset / ((float) AndroidUtilities.dp(10.0f))) * 255.0f));
                } else if (this.scrollingOffset > ((float) ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)))) {
                    this.fadeDrawable.setAlpha((int) ((1.0f - ((this.scrollingOffset - ((float) ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)))) / ((float) AndroidUtilities.dp(10.0f)))) * 255.0f));
                } else {
                    this.fadeDrawable.setAlpha(255);
                }
                this.fadeDrawable.setBounds(0, 0, AndroidUtilities.dp(6.0f), getMeasuredHeight());
                this.fadeDrawable.draw(canvas);
                this.fadeDrawableBack.setBounds(getMeasuredWidth() - AndroidUtilities.dp(6.0f), 0, getMeasuredWidth(), getMeasuredHeight());
                this.fadeDrawableBack.draw(canvas);
            }
            updateScrollAnimation();
        }
    }

    public int getTextWidth() {
        int totalWidth2 = this.textWidth;
        Drawable drawable = this.leftDrawable;
        if (drawable != null) {
            totalWidth2 += drawable.getIntrinsicWidth() + this.drawablePadding;
        }
        Drawable drawable2 = this.rightDrawable;
        if (drawable2 != null) {
            return totalWidth2 + drawable2.getIntrinsicWidth() + this.drawablePadding;
        }
        return totalWidth2;
    }

    public int getTextHeight() {
        return this.textHeight;
    }

    public void setLeftDrawableTopPadding(int value) {
        this.leftDrawableTopPadding = value;
    }

    public void setRightDrawableTopPadding(int value) {
        this.rightDrawableTopPadding = value;
    }

    public void setLeftDrawable(int resId) {
        setLeftDrawable(resId == 0 ? null : getContext().getResources().getDrawable(resId));
    }

    public void setRightDrawable(int resId) {
        setRightDrawable(resId == 0 ? null : getContext().getResources().getDrawable(resId));
    }

    public void setLeftDrawable(Drawable drawable) {
        Drawable drawable2 = this.leftDrawable;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback((Drawable.Callback) null);
            }
            this.leftDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
            }
            if (!recreateLayoutMaybe()) {
                invalidate();
            }
        }
    }

    public Drawable getRightDrawable() {
        return this.rightDrawable;
    }

    public void setRightDrawable(Drawable drawable) {
        Drawable drawable2 = this.rightDrawable;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback((Drawable.Callback) null);
            }
            this.rightDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
            }
            if (!recreateLayoutMaybe()) {
                invalidate();
            }
        }
    }

    public void setSideDrawablesColor(int color) {
        Theme.setDrawableColor(this.rightDrawable, color);
        Theme.setDrawableColor(this.leftDrawable, color);
    }

    public boolean setText(CharSequence value) {
        return setText(value, false);
    }

    public boolean setText(CharSequence value, boolean force) {
        CharSequence charSequence;
        if (this.text == null && value == null) {
            return false;
        }
        if (!force && (charSequence = this.text) != null && charSequence.equals(value)) {
            return false;
        }
        this.text = value;
        this.scrollingOffset = 0.0f;
        this.currentScrollDelay = 500;
        recreateLayoutMaybe();
        return true;
    }

    public void setDrawablePadding(int value) {
        if (this.drawablePadding != value) {
            this.drawablePadding = value;
            if (!recreateLayoutMaybe()) {
                invalidate();
            }
        }
    }

    private boolean recreateLayoutMaybe() {
        if (!this.wasLayout || getMeasuredHeight() == 0) {
            requestLayout();
            return true;
        }
        boolean result = createLayout((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        if ((this.gravity & 112) == 16) {
            this.offsetY = (getMeasuredHeight() - this.textHeight) / 2;
        } else {
            this.offsetY = 0;
        }
        return result;
    }

    public CharSequence getText() {
        CharSequence charSequence = this.text;
        if (charSequence == null) {
            return "";
        }
        return charSequence;
    }

    public int getTextStartX() {
        if (this.layout == null) {
            return 0;
        }
        int textOffsetX = 0;
        Drawable drawable = this.leftDrawable;
        if (drawable != null && (this.gravity & 7) == 3) {
            textOffsetX = 0 + this.drawablePadding + drawable.getIntrinsicWidth();
        }
        return ((int) getX()) + this.offsetX + textOffsetX;
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    public int getTextStartY() {
        if (this.layout == null) {
            return 0;
        }
        return (int) getY();
    }

    public void setBackgroundColor(int color) {
        if (this.scrollNonFitText) {
            GradientDrawable gradientDrawable = this.fadeDrawable;
            if (gradientDrawable != null) {
                gradientDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                this.fadeDrawableBack.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                return;
            }
            return;
        }
        super.setBackgroundColor(color);
    }

    private void updateScrollAnimation() {
        float pixelsPerSecond;
        if (!this.scrollNonFitText) {
            return;
        }
        if (this.textDoesNotFit || this.scrollingOffset != 0.0f) {
            long newUpdateTime = SystemClock.uptimeMillis();
            long dt = newUpdateTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            int i = this.currentScrollDelay;
            if (i > 0) {
                this.currentScrollDelay = (int) (((long) i) - dt);
            } else {
                int totalDistance = this.totalWidth + AndroidUtilities.dp(16.0f);
                if (this.scrollingOffset < ((float) AndroidUtilities.dp(100.0f))) {
                    pixelsPerSecond = ((this.scrollingOffset / ((float) AndroidUtilities.dp(100.0f))) * 20.0f) + 30.0f;
                } else if (this.scrollingOffset >= ((float) (totalDistance - AndroidUtilities.dp(100.0f)))) {
                    pixelsPerSecond = 50.0f - (((this.scrollingOffset - ((float) (totalDistance - AndroidUtilities.dp(100.0f)))) / ((float) AndroidUtilities.dp(100.0f))) * 20.0f);
                } else {
                    pixelsPerSecond = 50.0f;
                }
                float dp = this.scrollingOffset + ((((float) dt) / 1000.0f) * ((float) AndroidUtilities.dp(pixelsPerSecond)));
                this.scrollingOffset = dp;
                this.lastUpdateTime = newUpdateTime;
                if (dp > ((float) totalDistance)) {
                    this.scrollingOffset = 0.0f;
                    this.currentScrollDelay = 500;
                }
            }
            invalidate();
        }
    }

    public void invalidateDrawable(Drawable who) {
        Drawable drawable = this.leftDrawable;
        if (who == drawable) {
            invalidate(drawable.getBounds());
            return;
        }
        Drawable drawable2 = this.rightDrawable;
        if (who == drawable2) {
            invalidate(drawable2.getBounds());
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setVisibleToUser(true);
        info.setClassName("android.widget.TextView");
        info.setText(this.text);
    }
}
