package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import androidx.appcompat.widget.AppCompatEditText;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class PasswordEditText extends AppCompatEditText implements InputType {
    private static final int TYPE_BOTTOM_LINE = 1;
    private static final int TYPE_RECT = 0;
    private GradientDrawable cursor;
    private Runnable cursorCallback;
    private int cursorColor;
    private int cursorFlashInterval;
    private float cursorHeight;
    /* access modifiers changed from: private */
    public boolean cursorIsVisible;
    private float cursorWidth;
    private int mCirclePointColor;
    private String mComparePassword;
    private int mCorners;
    private int mCrclePointRadius;
    private int mFocusedColor;
    private int mLineColor;
    private float mLineWidth;
    private OnPasswordListener mListener;
    private float mMarginEach;
    private Paint mPaint;
    private int mRealHeight;
    private int mRectBgColor;
    private boolean mTextVariationType;
    private int mType;
    private float mWidthItem;
    private int maxCount;
    private int position;
    private RectF rectF;
    private boolean showCursor;

    public interface OnPasswordListener {
        void inputFinished(String str);

        void onDifference(String str, String str2);

        void onEqual(String str);
    }

    public PasswordEditText(Context context) {
        this(context, (AttributeSet) null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.position = 0;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText);
        this.maxCount = typedArray.getInt(10, 6);
        this.mCirclePointColor = typedArray.getColor(0, Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.mCrclePointRadius = typedArray.getDimensionPixelOffset(1, AndroidUtilities.dp(5.0f));
        this.mLineWidth = typedArray.getDimension(8, 1.0f);
        this.mLineColor = typedArray.getColor(7, Theme.getColor(Theme.key_divider));
        this.mType = typedArray.getInt(11, 0);
        this.mCorners = typedArray.getDimensionPixelOffset(13, 0);
        this.mFocusedColor = typedArray.getColor(6, Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        this.mRectBgColor = typedArray.getColor(12, Theme.getColor(Theme.key_windowBackgroundWhite));
        this.mTextVariationType = typedArray.getBoolean(15, false);
        this.showCursor = typedArray.getBoolean(14, false);
        this.cursorColor = typedArray.getColor(2, Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        this.cursorWidth = typedArray.getDimension(5, (float) AndroidUtilities.dp(2.0f));
        this.cursorHeight = typedArray.getDimension(4, (float) AndroidUtilities.dp(20.0f));
        this.cursorFlashInterval = typedArray.getInteger(3, 3);
        if (1 == this.mType) {
            this.mMarginEach = typedArray.getDimension(9, (float) AndroidUtilities.dp(5.0f));
        } else {
            this.mMarginEach = typedArray.getDimension(9, (float) AndroidUtilities.dp(0.0f));
        }
        typedArray.recycle();
        setCursorVisible(false);
        setCursor(false);
        setBackgroundColor(0);
        setMaxCount(this.maxCount);
        AndroidUtilities.handleKeyboardShelterProblem(this);
        float f = this.mLineWidth;
        setPadding(((int) f) / 2, ((int) f) / 2, ((int) f) / 2, ((int) f) / 2);
        initOther();
    }

    private void initOther() {
        this.mPaint = new Paint(1);
        if (1 == this.mType) {
            setPaint(this.mLineWidth, Paint.Style.FILL, this.mLineColor);
        } else {
            setPaint(this.mLineWidth, Paint.Style.STROKE, this.mLineColor);
        }
        this.rectF = new RectF();
    }

    private void setPaint(float strokeWidth, Paint.Style style, int color) {
        this.mPaint.reset();
        this.mPaint.setStrokeWidth(strokeWidth);
        this.mPaint.setStyle(style);
        this.mPaint.setColor(color);
        this.mPaint.setAntiAlias(true);
        if (this.mTextVariationType) {
            this.mPaint.setTextSize(getTextSize());
            setCursorVisible(true);
        }
    }

    private void setCursor(boolean onlyCursorCallback) {
        if (this.cursor == null) {
            this.cursor = new GradientDrawable();
        }
        this.cursor.setColor(this.cursorColor);
        if (onlyCursorCallback) {
            if (this.cursorCallback == null) {
                this.cursorCallback = new Runnable() {
                    public void run() {
                        PasswordEditText passwordEditText = PasswordEditText.this;
                        boolean unused = passwordEditText.cursorIsVisible = !passwordEditText.cursorIsVisible;
                        Log.d("PaEditT", "cursorCallback ===> cursorIsVisible = " + PasswordEditText.this.cursorIsVisible);
                    }
                };
            }
            removeCallbacks(this.cursorCallback);
            int i = this.cursorFlashInterval;
            if (i > 0 && this.cursorWidth > 0.0f && this.cursorHeight > 0.0f) {
                postDelayed(this.cursorCallback, (long) i);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        int i;
        Runnable runnable;
        super.onAttachedToWindow();
        if (this.showCursor && this.cursorColor != 0 && (i = this.cursorFlashInterval) > 0 && (runnable = this.cursorCallback) != null) {
            postDelayed(runnable, (long) i);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        Runnable runnable;
        super.onDetachedFromWindow();
        if (this.showCursor && this.cursorColor != 0 && this.cursorFlashInterval > 0 && (runnable = this.cursorCallback) != null) {
            removeCallbacks(runnable);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float f = this.mMarginEach;
        if (f > 0.0f) {
            int i = this.maxCount;
            this.mWidthItem = ((((float) w) - (f * ((float) (i - 1)))) - ((this.mLineWidth * ((float) i)) * 2.0f)) / ((float) i);
        } else {
            float f2 = this.mLineWidth;
            int i2 = this.maxCount;
            this.mWidthItem = (((float) w) - (f2 * ((float) (i2 + 1)))) / ((float) i2);
        }
        this.mRealHeight = (int) (((float) h) - (this.mLineWidth / 2.0f));
        this.rectF.set(0.0f, 0.0f, (float) w, (float) h);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mType == 0) {
            drawRect(canvas);
        } else {
            drawBottomLine(canvas);
        }
        drawItemFocused(canvas, this.position);
        if (!this.mTextVariationType) {
            drawCirclePoint(canvas);
        } else {
            drawText(canvas);
        }
        drawCursor(canvas);
    }

    private void drawRect(Canvas canvas) {
        for (int i = 0; i < this.maxCount; i++) {
            getRectFByPosition(i);
            setPaint(this.mLineWidth, Paint.Style.FILL, this.mRectBgColor);
            RectF rectF2 = this.rectF;
            int i2 = this.mCorners;
            canvas.drawRoundRect(rectF2, (float) i2, (float) i2, this.mPaint);
            setPaint(this.mLineWidth, Paint.Style.STROKE, this.mLineColor);
            RectF rectF3 = this.rectF;
            int i3 = this.mCorners;
            canvas.drawRoundRect(rectF3, (float) i3, (float) i3, this.mPaint);
        }
    }

    private void drawBottomLine(Canvas canvas) {
        setPaint(this.mLineWidth, Paint.Style.FILL, this.mLineColor);
        float y = ((float) getMeasuredHeight()) - (this.mLineWidth / 2.0f);
        for (int i = 0; i < this.maxCount; i++) {
            float[] centers = getCenter(i);
            float f = centers[0];
            float f2 = this.mWidthItem;
            canvas.drawLine(f - (f2 / 2.0f), y, centers[0] + (f2 / 2.0f), y, this.mPaint);
        }
    }

    private void drawItemFocused(Canvas canvas, int position2) {
        if (position2 <= this.maxCount - 1) {
            setPaint(this.mLineWidth, Paint.Style.STROKE, this.mFocusedColor);
            if (this.mType == 0) {
                getRectFByPosition(position2);
                RectF rectF2 = this.rectF;
                int i = this.mCorners;
                canvas.drawRoundRect(rectF2, (float) i, (float) i, this.mPaint);
                return;
            }
            float y = ((float) getMeasuredHeight()) - (this.mLineWidth / 2.0f);
            float[] centers = getCenter(position2);
            float f = centers[0];
            float f2 = this.mWidthItem;
            canvas.drawLine(f - (f2 / 2.0f), y, centers[0] + (f2 / 2.0f), y, this.mPaint);
        }
    }

    private void drawCirclePoint(Canvas canvas) {
        setPaint(5.0f, Paint.Style.FILL, this.mCirclePointColor);
        for (int i = 0; i < this.position; i++) {
            float[] centers = getCenter(i);
            canvas.drawCircle(centers[0], centers[1], (float) this.mCrclePointRadius, this.mPaint);
        }
    }

    private void drawText(Canvas canvas) {
        setPaint(5.0f, Paint.Style.FILL, this.mCirclePointColor);
        String t = getText() + "";
        if (t.length() > 0) {
            for (int i = 0; i < this.position; i++) {
                float w = this.mPaint.measureText(t.substring(i, i + 1), 0, 1);
                Paint.FontMetrics fm = this.mPaint.getFontMetrics();
                float[] centers = getCenter(i);
                canvas.drawText(t.substring(i, i + 1), centers[0] - (w / 2.0f), (centers[1] + (((float) Math.ceil((double) (fm.descent - fm.ascent))) / 2.0f)) - fm.descent, this.mPaint);
            }
        }
    }

    private void drawCursor(Canvas canvas) {
        if (this.showCursor && this.cursorWidth > 0.0f) {
            float f = this.cursorHeight;
            if (f > 0.0f) {
                this.cursorHeight = Math.min(f, (float) this.mRealHeight);
                boolean z = !this.cursorIsVisible;
                this.cursorIsVisible = z;
                if (z) {
                    canvas.save();
                    this.cursor.setColor(this.cursorColor);
                    float[] centers = getCenter(this.position);
                    float f2 = centers[0];
                    float f3 = this.cursorWidth;
                    int l = (int) (f2 - (f3 / 2.0f));
                    float f4 = centers[1];
                    float f5 = this.cursorHeight;
                    int t = (int) (f4 - (f5 / 2.0f));
                    this.cursor.setBounds(l, t, ((int) f3) + l, ((int) f5) + t);
                    this.cursor.draw(canvas);
                    canvas.restore();
                }
                if (BuildVars.DEBUG_VERSION) {
                    Log.d("PaEditT", "drawCursor ===> dVisible = " + this.cursor.isVisible() + " , cursorIsVisible = " + this.cursorIsVisible + " , bounds = " + this.cursor.getBounds().toString());
                }
            }
        }
    }

    private RectF getRectFByPosition(int position2) {
        float left;
        float f = this.mMarginEach;
        if (f > 0.0f) {
            float f2 = this.mLineWidth;
            left = ((this.mWidthItem + f) * ((float) position2)) + (((float) (position2 * 2)) * f2) + (f2 / 2.0f);
        } else {
            float f3 = this.mWidthItem;
            float f4 = this.mLineWidth;
            left = (f4 / 2.0f) + ((f3 + f4) * ((float) position2));
        }
        RectF rectF2 = this.rectF;
        float f5 = this.mLineWidth;
        rectF2.set(left, f5 / 2.0f, this.mWidthItem + left + (f5 / 2.0f), (float) this.mRealHeight);
        return this.rectF;
    }

    private float[] getCenter(int position2) {
        float f = this.mWidthItem;
        return new float[]{(f / 2.0f) + (((float) position2) * (f + this.mMarginEach)) + (((float) ((position2 * 2) + 1)) * this.mLineWidth), ((float) getMeasuredHeight()) / 2.0f};
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        int i = start + lengthAfter;
        this.position = i;
        if (i == this.maxCount && this.mListener != null) {
            if (TextUtils.isEmpty(this.mComparePassword)) {
                this.mListener.inputFinished(getPasswordString());
            } else if (TextUtils.equals(this.mComparePassword, getPasswordString())) {
                this.mListener.onEqual(getPasswordString());
            } else {
                this.mListener.onDifference(this.mComparePassword, getPasswordString());
            }
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (selStart == selEnd && getText() != null) {
            setSelection(getText().length());
        }
    }

    public String getPasswordString() {
        return getText().toString().trim();
    }

    public PasswordEditText setMaxCount(int maxCount2) {
        this.maxCount = maxCount2;
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCount2)});
        return this;
    }

    public PasswordEditText setBottomLineColor(int bottomLineColor) {
        this.mLineColor = bottomLineColor;
        invalidate();
        return this;
    }

    public PasswordEditText setFocusedColor(int mFocusedColor2) {
        this.mFocusedColor = mFocusedColor2;
        invalidate();
        return this;
    }

    public PasswordEditText setShowCursor(boolean showCursor2) {
        this.showCursor = showCursor2;
        invalidate();
        return this;
    }

    public PasswordEditText setCursorColor(int cursorColor2) {
        this.cursorColor = cursorColor2;
        setCursor(false);
        invalidate();
        return this;
    }

    public PasswordEditText setCursorWidth(float cursorWidth2) {
        this.cursorWidth = cursorWidth2;
        setCursor(true);
        invalidate();
        return this;
    }

    public PasswordEditText setCursorHeight(float cursorHeight2) {
        this.cursorHeight = cursorHeight2;
        setCursor(true);
        invalidate();
        return this;
    }

    public PasswordEditText setCursorFlashInterval(int cursorFlashInterval2) {
        this.cursorFlashInterval = cursorFlashInterval2;
        setCursor(true);
        invalidate();
        return this;
    }

    public void cleanPsd() {
        setText("");
    }

    public PasswordEditText setComparePassword(String comparePassword, OnPasswordListener listener) {
        this.mComparePassword = comparePassword;
        this.mListener = listener;
        return this;
    }

    public PasswordEditText setComparePassword(OnPasswordListener listener) {
        this.mListener = listener;
        return this;
    }

    public void setComparePassword(String psd) {
        this.mComparePassword = psd;
    }

    public boolean isInEditMode() {
        return true;
    }

    public static abstract class OnPasswordListenerSimple implements OnPasswordListener {
        public void onDifference(String oldPsd, String newPsd) {
        }

        public void onEqual(String psd) {
        }
    }
}
