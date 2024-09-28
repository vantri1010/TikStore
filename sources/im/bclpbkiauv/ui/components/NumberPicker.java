package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import java.util.Locale;

public class NumberPicker extends LinearLayout {
    private static final int DEFAULT_LAYOUT_RESOURCE_ID = 0;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    private int SELECTOR_MIDDLE_ITEM_INDEX = (3 / 2);
    private int SELECTOR_WHEEL_ITEM_COUNT = 3;
    private Scroller mAdjustScroller;
    /* access modifiers changed from: private */
    public int mBottomSelectionDividerBottom;
    private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
    private boolean mComputeMaxWidth;
    private int mCurrentScrollOffset;
    /* access modifiers changed from: private */
    public boolean mDecrementVirtualButtonPressed;
    private String[] mDisplayedValues;
    private Scroller mFlingScroller;
    private Formatter mFormatter;
    /* access modifiers changed from: private */
    public boolean mIncrementVirtualButtonPressed;
    private boolean mIngonreMoveEvents;
    private int mInitialScrollOffset = Integer.MIN_VALUE;
    private TextView mInputText;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode = -1;
    private int mLastHoveredChildVirtualViewId;
    /* access modifiers changed from: private */
    public long mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
    private int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private int mMinHeight;
    private int mMinValue;
    private int mMinWidth;
    private int mMinimumFlingVelocity;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private PressedStateHelper mPressedStateHelper;
    private int mPreviousScrollerY;
    private int mScrollState = 0;
    private Paint mSelectionDivider;
    private int mSelectionDividerHeight;
    private int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache = new SparseArray<>();
    private int[] mSelectorIndices = new int[this.SELECTOR_WHEEL_ITEM_COUNT];
    private int mSelectorTextGapHeight;
    private Paint mSelectorWheelPaint;
    private int mSolidColor;
    private int mTextSize;
    /* access modifiers changed from: private */
    public int mTopSelectionDividerTop;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private boolean mWrapSelectorWheel;
    private int textOffset;

    public interface Formatter {
        String format(int i);
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScrollStateChange(NumberPicker numberPicker, int i);
    }

    public interface OnValueChangeListener {
        void onValueChange(NumberPicker numberPicker, int i, int i2);
    }

    public void setItemCount(int count) {
        if (this.SELECTOR_WHEEL_ITEM_COUNT != count) {
            this.SELECTOR_WHEEL_ITEM_COUNT = count;
            this.SELECTOR_MIDDLE_ITEM_INDEX = count / 2;
            this.mSelectorIndices = new int[count];
            initializeSelectorWheelIndices();
        }
    }

    private void init() {
        this.mSolidColor = 0;
        Paint paint = new Paint();
        this.mSelectionDivider = paint;
        paint.setColor(Theme.getColor(Theme.key_dialogButton));
        this.mSelectionDividerHeight = (int) TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics());
        this.mSelectionDividersDistance = (int) TypedValue.applyDimension(1, 48.0f, getResources().getDisplayMetrics());
        this.mMinHeight = -1;
        int applyDimension = (int) TypedValue.applyDimension(1, 180.0f, getResources().getDisplayMetrics());
        this.mMaxHeight = applyDimension;
        int i = this.mMinHeight;
        if (i == -1 || applyDimension == -1 || i <= applyDimension) {
            int applyDimension2 = (int) TypedValue.applyDimension(1, 64.0f, getResources().getDisplayMetrics());
            this.mMinWidth = applyDimension2;
            this.mMaxWidth = -1;
            if (applyDimension2 == -1 || -1 == -1 || applyDimension2 <= -1) {
                this.mComputeMaxWidth = this.mMaxWidth == -1;
                this.mPressedStateHelper = new PressedStateHelper();
                setWillNotDraw(false);
                TextView textView = new TextView(getContext());
                this.mInputText = textView;
                textView.setGravity(17);
                this.mInputText.setSingleLine(true);
                this.mInputText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                this.mInputText.setBackgroundResource(0);
                this.mInputText.setTextSize(1, 18.0f);
                this.mInputText.setVisibility(4);
                addView(this.mInputText, new LinearLayout.LayoutParams(-1, -2));
                ViewConfiguration configuration = ViewConfiguration.get(getContext());
                this.mTouchSlop = configuration.getScaledTouchSlop();
                this.mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity() / 8;
                this.mTextSize = (int) this.mInputText.getTextSize();
                Paint paint2 = new Paint();
                paint2.setAntiAlias(true);
                paint2.setTextAlign(Paint.Align.CENTER);
                paint2.setTextSize((float) this.mTextSize);
                paint2.setTypeface(this.mInputText.getTypeface());
                paint2.setColor(this.mInputText.getTextColors().getColorForState(ENABLED_STATE_SET, -1));
                this.mSelectorWheelPaint = paint2;
                this.mFlingScroller = new Scroller(getContext(), (Interpolator) null, true);
                this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
                updateInputTextView();
                return;
            }
            throw new IllegalArgumentException("minWidth > maxWidth");
        }
        throw new IllegalArgumentException("minHeight > maxHeight");
    }

    public void setTextColor(int color) {
        this.mInputText.setTextColor(color);
        this.mSelectorWheelPaint.setColor(color);
    }

    public void setSelectorColor(int color) {
        this.mSelectionDivider.setColor(color);
    }

    public NumberPicker(Context context) {
        super(context);
        init();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int msrdWdth = getMeasuredWidth();
        int msrdHght = getMeasuredHeight();
        int inptTxtMsrdWdth = this.mInputText.getMeasuredWidth();
        int inptTxtMsrdHght = this.mInputText.getMeasuredHeight();
        int inptTxtLeft = (msrdWdth - inptTxtMsrdWdth) / 2;
        int inptTxtTop = (msrdHght - inptTxtMsrdHght) / 2;
        this.mInputText.layout(inptTxtLeft, inptTxtTop, inptTxtLeft + inptTxtMsrdWdth, inptTxtTop + inptTxtMsrdHght);
        if (changed) {
            initializeSelectorWheel();
            initializeFadingEdges();
            int height = getHeight();
            int i = this.mSelectionDividersDistance;
            int i2 = this.mSelectionDividerHeight;
            int i3 = ((height - i) / 2) - i2;
            this.mTopSelectionDividerTop = i3;
            this.mBottomSelectionDividerBottom = i3 + (i2 * 2) + i;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(makeMeasureSpec(widthMeasureSpec, this.mMaxWidth), makeMeasureSpec(heightMeasureSpec, this.mMaxHeight));
        setMeasuredDimension(resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), widthMeasureSpec), resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), heightMeasureSpec));
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int amountToScroll = scroller.getFinalY() - scroller.getCurrY();
        int overshootAdjustment = this.mInitialScrollOffset - ((this.mCurrentScrollOffset + amountToScroll) % this.mSelectorElementHeight);
        if (overshootAdjustment == 0) {
            return false;
        }
        int abs = Math.abs(overshootAdjustment);
        int i = this.mSelectorElementHeight;
        if (abs > i / 2) {
            if (overshootAdjustment > 0) {
                overshootAdjustment -= i;
            } else {
                overshootAdjustment += i;
            }
        }
        scrollBy(0, amountToScroll + overshootAdjustment);
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isEnabled() || event.getActionMasked() != 0) {
            return false;
        }
        removeAllCallbacks();
        this.mInputText.setVisibility(4);
        float y = event.getY();
        this.mLastDownEventY = y;
        this.mLastDownOrMoveEventY = y;
        this.mLastDownEventTime = event.getEventTime();
        this.mIngonreMoveEvents = false;
        float f = this.mLastDownEventY;
        if (f < ((float) this.mTopSelectionDividerTop)) {
            if (this.mScrollState == 0) {
                this.mPressedStateHelper.buttonPressDelayed(2);
            }
        } else if (f > ((float) this.mBottomSelectionDividerBottom) && this.mScrollState == 0) {
            this.mPressedStateHelper.buttonPressDelayed(1);
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        if (!this.mFlingScroller.isFinished()) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
            onScrollStateChange(0);
        } else if (!this.mAdjustScroller.isFinished()) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
        } else {
            float f2 = this.mLastDownEventY;
            if (f2 < ((float) this.mTopSelectionDividerTop)) {
                postChangeCurrentByOneFromLongPress(false, (long) ViewConfiguration.getLongPressTimeout());
            } else if (f2 > ((float) this.mBottomSelectionDividerBottom)) {
                postChangeCurrentByOneFromLongPress(true, (long) ViewConfiguration.getLongPressTimeout());
            }
        }
        return true;
    }

    public void finishScroll() {
        if (!this.mFlingScroller.isFinished() || !this.mAdjustScroller.isFinished()) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        int action = event.getActionMasked();
        if (action == 1) {
            removeChangeCurrentByOneFromLongPress();
            this.mPressedStateHelper.cancel();
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumFlingVelocity);
            int initialVelocity = (int) velocityTracker.getYVelocity();
            if (Math.abs(initialVelocity) > this.mMinimumFlingVelocity) {
                fling(initialVelocity);
                onScrollStateChange(2);
            } else {
                int eventY = (int) event.getY();
                int deltaMoveY = (int) Math.abs(((float) eventY) - this.mLastDownEventY);
                long deltaTime = event.getEventTime() - this.mLastDownEventTime;
                if (deltaMoveY > this.mTouchSlop || deltaTime >= ((long) ViewConfiguration.getTapTimeout())) {
                    ensureScrollWheelAdjusted();
                } else {
                    int selectorIndexOffset = (eventY / this.mSelectorElementHeight) - this.SELECTOR_MIDDLE_ITEM_INDEX;
                    if (selectorIndexOffset > 0) {
                        changeValueByOne(true);
                        this.mPressedStateHelper.buttonTapped(1);
                    } else if (selectorIndexOffset < 0) {
                        changeValueByOne(false);
                        this.mPressedStateHelper.buttonTapped(2);
                    }
                }
                onScrollStateChange(0);
            }
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        } else if (action == 2 && !this.mIngonreMoveEvents) {
            float currentMoveY = event.getY();
            if (this.mScrollState == 1) {
                scrollBy(0, (int) (currentMoveY - this.mLastDownOrMoveEventY));
                invalidate();
            } else if (((int) Math.abs(currentMoveY - this.mLastDownEventY)) > this.mTouchSlop) {
                removeAllCallbacks();
                onScrollStateChange(1);
            }
            this.mLastDownOrMoveEventY = currentMoveY;
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == 1 || action == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTouchEvent(event);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0047, code lost:
        requestFocus();
        r5.mLastHandledDownDpadKeyCode = r0;
        removeAllCallbacks();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0055, code lost:
        if (r5.mFlingScroller.isFinished() == false) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0057, code lost:
        if (r0 != 20) goto L_0x005b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0059, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x005b, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x005c, code lost:
        changeValueByOne(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005f, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchKeyEvent(android.view.KeyEvent r6) {
        /*
            r5 = this;
            int r0 = r6.getKeyCode()
            r1 = 19
            r2 = 20
            if (r0 == r1) goto L_0x0019
            if (r0 == r2) goto L_0x0019
            r1 = 23
            if (r0 == r1) goto L_0x0015
            r1 = 66
            if (r0 == r1) goto L_0x0015
            goto L_0x0060
        L_0x0015:
            r5.removeAllCallbacks()
            goto L_0x0060
        L_0x0019:
            int r1 = r6.getAction()
            r3 = 1
            if (r1 == 0) goto L_0x002b
            if (r1 == r3) goto L_0x0023
            goto L_0x0060
        L_0x0023:
            int r1 = r5.mLastHandledDownDpadKeyCode
            if (r1 != r0) goto L_0x0060
            r1 = -1
            r5.mLastHandledDownDpadKeyCode = r1
            return r3
        L_0x002b:
            boolean r1 = r5.mWrapSelectorWheel
            if (r1 != 0) goto L_0x003d
            if (r0 != r2) goto L_0x0032
            goto L_0x003d
        L_0x0032:
            int r1 = r5.getValue()
            int r4 = r5.getMinValue()
            if (r1 <= r4) goto L_0x0060
            goto L_0x0047
        L_0x003d:
            int r1 = r5.getValue()
            int r4 = r5.getMaxValue()
            if (r1 >= r4) goto L_0x0060
        L_0x0047:
            r5.requestFocus()
            r5.mLastHandledDownDpadKeyCode = r0
            r5.removeAllCallbacks()
            im.bclpbkiauv.ui.components.Scroller r1 = r5.mFlingScroller
            boolean r1 = r1.isFinished()
            if (r1 == 0) goto L_0x005f
            if (r0 != r2) goto L_0x005b
            r1 = 1
            goto L_0x005c
        L_0x005b:
            r1 = 0
        L_0x005c:
            r5.changeValueByOne(r1)
        L_0x005f:
            return r3
        L_0x0060:
            boolean r1 = super.dispatchKeyEvent(r6)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.NumberPicker.dispatchKeyEvent(android.view.KeyEvent):boolean");
    }

    public boolean dispatchTrackballEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == 1 || action == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTrackballEvent(event);
    }

    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currentScrollerY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(0, currentScrollerY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currentScrollerY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mInputText.setEnabled(enabled);
    }

    public void scrollBy(int x, int y) {
        int i;
        int i2;
        int i3;
        int i4;
        int[] selectorIndices = this.mSelectorIndices;
        if (!this.mWrapSelectorWheel && y > 0 && selectorIndices[this.SELECTOR_MIDDLE_ITEM_INDEX] <= this.mMinValue && this.mCurrentScrollOffset + y > (i4 = this.mInitialScrollOffset)) {
            this.mCurrentScrollOffset = i4;
        } else if (this.mWrapSelectorWheel || y >= 0 || selectorIndices[this.SELECTOR_MIDDLE_ITEM_INDEX] < this.mMaxValue || this.mCurrentScrollOffset + y >= (i3 = this.mInitialScrollOffset)) {
            this.mCurrentScrollOffset += y;
            while (true) {
                int i5 = this.mCurrentScrollOffset;
                if (i5 - this.mInitialScrollOffset <= this.mSelectorTextGapHeight) {
                    break;
                }
                this.mCurrentScrollOffset = i5 - this.mSelectorElementHeight;
                decrementSelectorIndices(selectorIndices);
                setValueInternal(selectorIndices[this.SELECTOR_MIDDLE_ITEM_INDEX], true);
                if (!this.mWrapSelectorWheel && selectorIndices[this.SELECTOR_MIDDLE_ITEM_INDEX] <= this.mMinValue && this.mCurrentScrollOffset > (i2 = this.mInitialScrollOffset)) {
                    this.mCurrentScrollOffset = i2;
                }
            }
            while (true) {
                int i6 = this.mCurrentScrollOffset;
                if (i6 - this.mInitialScrollOffset < (-this.mSelectorTextGapHeight)) {
                    this.mCurrentScrollOffset = i6 + this.mSelectorElementHeight;
                    incrementSelectorIndices(selectorIndices);
                    setValueInternal(selectorIndices[this.SELECTOR_MIDDLE_ITEM_INDEX], true);
                    if (!this.mWrapSelectorWheel && selectorIndices[this.SELECTOR_MIDDLE_ITEM_INDEX] >= this.mMaxValue && this.mCurrentScrollOffset < (i = this.mInitialScrollOffset)) {
                        this.mCurrentScrollOffset = i;
                    }
                } else {
                    return;
                }
            }
        } else {
            this.mCurrentScrollOffset = i3;
        }
    }

    /* access modifiers changed from: protected */
    public int computeVerticalScrollOffset() {
        return this.mCurrentScrollOffset;
    }

    /* access modifiers changed from: protected */
    public int computeVerticalScrollRange() {
        return ((this.mMaxValue - this.mMinValue) + 1) * this.mSelectorElementHeight;
    }

    /* access modifiers changed from: protected */
    public int computeVerticalScrollExtent() {
        return getHeight();
    }

    public int getSolidColor() {
        return this.mSolidColor;
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        this.mOnValueChangeListener = onValueChangedListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setFormatter(Formatter formatter) {
        if (formatter != this.mFormatter) {
            this.mFormatter = formatter;
            initializeSelectorWheelIndices();
            updateInputTextView();
        }
    }

    public void setValue(int value) {
        setValueInternal(value, false);
    }

    public void setTextOffset(int value) {
        this.textOffset = value;
        invalidate();
    }

    private void tryComputeMaxWidth() {
        if (this.mComputeMaxWidth) {
            int maxTextWidth = 0;
            String[] strArr = this.mDisplayedValues;
            if (strArr == null) {
                float maxDigitWidth = 0.0f;
                for (int i = 0; i <= 9; i++) {
                    float digitWidth = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
                    if (digitWidth > maxDigitWidth) {
                        maxDigitWidth = digitWidth;
                    }
                }
                int numberOfDigits = 0;
                for (int current = this.mMaxValue; current > 0; current /= 10) {
                    numberOfDigits++;
                }
                maxTextWidth = (int) (((float) numberOfDigits) * maxDigitWidth);
            } else {
                for (String mDisplayedValue : strArr) {
                    float textWidth = this.mSelectorWheelPaint.measureText(mDisplayedValue);
                    if (textWidth > ((float) maxTextWidth)) {
                        maxTextWidth = (int) textWidth;
                    }
                }
            }
            int maxTextWidth2 = maxTextWidth + this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight();
            if (this.mMaxWidth != maxTextWidth2) {
                int i2 = this.mMinWidth;
                if (maxTextWidth2 > i2) {
                    this.mMaxWidth = maxTextWidth2;
                } else {
                    this.mMaxWidth = i2;
                }
                invalidate();
            }
        }
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        boolean wrappingAllowed = this.mMaxValue - this.mMinValue >= this.mSelectorIndices.length;
        if ((!wrapSelectorWheel || wrappingAllowed) && wrapSelectorWheel != this.mWrapSelectorWheel) {
            this.mWrapSelectorWheel = wrapSelectorWheel;
        }
    }

    public void setOnLongPressUpdateInterval(long intervalMillis) {
        this.mLongPressUpdateInterval = intervalMillis;
    }

    public int getValue() {
        return this.mValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public void setMinValue(int minValue) {
        if (this.mMinValue != minValue) {
            if (minValue >= 0) {
                this.mMinValue = minValue;
                if (minValue > this.mValue) {
                    this.mValue = minValue;
                }
                setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
                initializeSelectorWheelIndices();
                updateInputTextView();
                tryComputeMaxWidth();
                invalidate();
                return;
            }
            throw new IllegalArgumentException("minValue must be >= 0");
        }
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        if (this.mMaxValue != maxValue) {
            if (maxValue >= 0) {
                this.mMaxValue = maxValue;
                if (maxValue < this.mValue) {
                    this.mValue = maxValue;
                }
                setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
                initializeSelectorWheelIndices();
                updateInputTextView();
                tryComputeMaxWidth();
                invalidate();
                return;
            }
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public void setDisplayedValues(String[] displayedValues) {
        if (this.mDisplayedValues != displayedValues) {
            this.mDisplayedValues = displayedValues;
            updateInputTextView();
            initializeSelectorWheelIndices();
            tryComputeMaxWidth();
        }
    }

    /* access modifiers changed from: protected */
    public float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    /* access modifiers changed from: protected */
    public float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllCallbacks();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float x = (float) (((getRight() - getLeft()) / 2) + this.textOffset);
        float y = (float) this.mCurrentScrollOffset;
        int[] selectorIndices = this.mSelectorIndices;
        for (int i = 0; i < selectorIndices.length; i++) {
            String scrollSelectorValue = this.mSelectorIndexToStringCache.get(selectorIndices[i]);
            if (!(scrollSelectorValue == null || (i == this.SELECTOR_MIDDLE_ITEM_INDEX && this.mInputText.getVisibility() == 0))) {
                canvas.drawText(scrollSelectorValue, x, y, this.mSelectorWheelPaint);
            }
            y += (float) this.mSelectorElementHeight;
        }
        int topOfTopDivider = this.mTopSelectionDividerTop;
        canvas.drawRect(0.0f, (float) topOfTopDivider, (float) getRight(), (float) (this.mSelectionDividerHeight + topOfTopDivider), this.mSelectionDivider);
        int bottomOfBottomDivider = this.mBottomSelectionDividerBottom;
        canvas.drawRect(0.0f, (float) (bottomOfBottomDivider - this.mSelectionDividerHeight), (float) getRight(), (float) bottomOfBottomDivider, this.mSelectionDivider);
    }

    private int makeMeasureSpec(int measureSpec, int maxSize) {
        if (maxSize == -1) {
            return measureSpec;
        }
        int size = View.MeasureSpec.getSize(measureSpec);
        int mode = View.MeasureSpec.getMode(measureSpec);
        if (mode == Integer.MIN_VALUE) {
            return View.MeasureSpec.makeMeasureSpec(Math.min(size, maxSize), 1073741824);
        }
        if (mode == 0) {
            return View.MeasureSpec.makeMeasureSpec(maxSize, 1073741824);
        }
        if (mode == 1073741824) {
            return measureSpec;
        }
        throw new IllegalArgumentException("Unknown measure mode: " + mode);
    }

    private int resolveSizeAndStateRespectingMinSize(int minSize, int measuredSize, int measureSpec) {
        if (minSize != -1) {
            return resolveSizeAndState(Math.max(minSize, measuredSize), measureSpec, 0);
        }
        return measuredSize;
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if (specMode != Integer.MIN_VALUE) {
            if (specMode == 0) {
                result = size;
            } else if (specMode == 1073741824) {
                result = specSize;
            }
        } else if (specSize < size) {
            result = specSize | 16777216;
        } else {
            result = size;
        }
        return (-16777216 & childMeasuredState) | result;
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] selectorIndices = this.mSelectorIndices;
        int current = getValue();
        for (int i = 0; i < this.mSelectorIndices.length; i++) {
            int selectorIndex = (i - this.SELECTOR_MIDDLE_ITEM_INDEX) + current;
            if (this.mWrapSelectorWheel) {
                selectorIndex = getWrappedSelectorIndex(selectorIndex);
            }
            selectorIndices[i] = selectorIndex;
            ensureCachedScrollSelectorValue(selectorIndices[i]);
        }
    }

    private void setValueInternal(int current, boolean notifyChange) {
        int current2;
        if (this.mValue != current) {
            if (this.mWrapSelectorWheel) {
                current2 = getWrappedSelectorIndex(current);
            } else {
                current2 = Math.min(Math.max(current, this.mMinValue), this.mMaxValue);
            }
            int previous = this.mValue;
            this.mValue = current2;
            updateInputTextView();
            if (notifyChange) {
                notifyChange(previous, current2);
            }
            initializeSelectorWheelIndices();
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void changeValueByOne(boolean increment) {
        this.mInputText.setVisibility(4);
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = 0;
        if (increment) {
            this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        } else {
            this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        }
        invalidate();
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] selectorIndices = this.mSelectorIndices;
        int bottom = (int) ((((float) ((getBottom() - getTop()) - (selectorIndices.length * this.mTextSize))) / ((float) selectorIndices.length)) + 0.5f);
        this.mSelectorTextGapHeight = bottom;
        this.mSelectorElementHeight = this.mTextSize + bottom;
        int baseline = (this.mInputText.getBaseline() + this.mInputText.getTop()) - (this.mSelectorElementHeight * this.SELECTOR_MIDDLE_ITEM_INDEX);
        this.mInitialScrollOffset = baseline;
        this.mCurrentScrollOffset = baseline;
        updateInputTextView();
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((getBottom() - getTop()) - this.mTextSize) / 2);
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            if (!ensureScrollWheelAdjusted()) {
                updateInputTextView();
            }
            onScrollStateChange(0);
        } else if (this.mScrollState != 1) {
            updateInputTextView();
        }
    }

    private void onScrollStateChange(int scrollState) {
        if (this.mScrollState != scrollState) {
            this.mScrollState = scrollState;
            OnScrollListener onScrollListener = this.mOnScrollListener;
            if (onScrollListener != null) {
                onScrollListener.onScrollStateChange(this, scrollState);
            }
            if (scrollState == 0) {
                AccessibilityManager am = (AccessibilityManager) getContext().getSystemService("accessibility");
                if (am.isTouchExplorationEnabled()) {
                    String[] strArr = this.mDisplayedValues;
                    String text = strArr == null ? formatNumber(this.mValue) : strArr[this.mValue - this.mMinValue];
                    AccessibilityEvent event = AccessibilityEvent.obtain();
                    event.setEventType(16384);
                    event.getText().add(text);
                    am.sendAccessibilityEvent(event);
                }
            }
        }
    }

    private void fling(int velocityY) {
        this.mPreviousScrollerY = 0;
        if (velocityY > 0) {
            this.mFlingScroller.fling(0, 0, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        } else {
            this.mFlingScroller.fling(0, Integer.MAX_VALUE, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        }
        invalidate();
    }

    private int getWrappedSelectorIndex(int selectorIndex) {
        int i = this.mMaxValue;
        if (selectorIndex > i) {
            int i2 = this.mMinValue;
            return (i2 + ((selectorIndex - i) % (i - i2))) - 1;
        }
        int i3 = this.mMinValue;
        if (selectorIndex < i3) {
            return (i - ((i3 - selectorIndex) % (i - i3))) + 1;
        }
        return selectorIndex;
    }

    private void incrementSelectorIndices(int[] selectorIndices) {
        System.arraycopy(selectorIndices, 1, selectorIndices, 0, selectorIndices.length - 1);
        int nextScrollSelectorIndex = selectorIndices[selectorIndices.length - 2] + 1;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex > this.mMaxValue) {
            nextScrollSelectorIndex = this.mMinValue;
        }
        selectorIndices[selectorIndices.length - 1] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void decrementSelectorIndices(int[] selectorIndices) {
        System.arraycopy(selectorIndices, 0, selectorIndices, 1, selectorIndices.length - 1);
        int nextScrollSelectorIndex = selectorIndices[1] - 1;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex < this.mMinValue) {
            nextScrollSelectorIndex = this.mMaxValue;
        }
        selectorIndices[0] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void ensureCachedScrollSelectorValue(int selectorIndex) {
        String scrollSelectorValue;
        SparseArray<String> cache = this.mSelectorIndexToStringCache;
        if (cache.get(selectorIndex) == null) {
            int i = this.mMinValue;
            if (selectorIndex < i || selectorIndex > this.mMaxValue) {
                scrollSelectorValue = "";
            } else {
                String[] strArr = this.mDisplayedValues;
                if (strArr != null) {
                    scrollSelectorValue = strArr[selectorIndex - i];
                } else {
                    scrollSelectorValue = formatNumber(selectorIndex);
                }
            }
            cache.put(selectorIndex, scrollSelectorValue);
        }
    }

    private String formatNumber(int value) {
        Formatter formatter = this.mFormatter;
        return formatter != null ? formatter.format(value) : formatNumberWithLocale(value);
    }

    private boolean updateInputTextView() {
        String[] strArr = this.mDisplayedValues;
        String text = strArr == null ? formatNumber(this.mValue) : strArr[this.mValue - this.mMinValue];
        if (TextUtils.isEmpty(text) || text.equals(this.mInputText.getText().toString())) {
            return false;
        }
        this.mInputText.setText(text);
        return true;
    }

    private void notifyChange(int previous, int current) {
        OnValueChangeListener onValueChangeListener = this.mOnValueChangeListener;
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChange(this, previous, this.mValue);
        }
    }

    private void postChangeCurrentByOneFromLongPress(boolean increment, long delayMillis) {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        this.mChangeCurrentByOneFromLongPressCommand.setStep(increment);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, delayMillis);
    }

    private void removeChangeCurrentByOneFromLongPress() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
    }

    private void removeAllCallbacks() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        this.mPressedStateHelper.cancel();
    }

    private int getSelectedPos(String value) {
        if (this.mDisplayedValues == null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return this.mMinValue;
            }
        } else {
            for (int i = 0; i < this.mDisplayedValues.length; i++) {
                value = value.toLowerCase();
                if (this.mDisplayedValues[i].toLowerCase().startsWith(value)) {
                    return this.mMinValue + i;
                }
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e2) {
                return this.mMinValue;
            }
        }
    }

    private boolean ensureScrollWheelAdjusted() {
        int deltaY = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (deltaY == 0) {
            return false;
        }
        this.mPreviousScrollerY = 0;
        int abs = Math.abs(deltaY);
        int i = this.mSelectorElementHeight;
        if (abs > i / 2) {
            if (deltaY > 0) {
                i = -i;
            }
            deltaY += i;
        }
        this.mAdjustScroller.startScroll(0, 0, 0, deltaY, 800);
        invalidate();
        return true;
    }

    class PressedStateHelper implements Runnable {
        public static final int BUTTON_DECREMENT = 2;
        public static final int BUTTON_INCREMENT = 1;
        private final int MODE_PRESS = 1;
        private final int MODE_TAPPED = 2;
        private int mManagedButton;
        private int mMode;

        PressedStateHelper() {
        }

        public void cancel() {
            this.mMode = 0;
            this.mManagedButton = 0;
            NumberPicker.this.removeCallbacks(this);
            if (NumberPicker.this.mIncrementVirtualButtonPressed) {
                boolean unused = NumberPicker.this.mIncrementVirtualButtonPressed = false;
                NumberPicker numberPicker = NumberPicker.this;
                numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
            }
            boolean unused2 = NumberPicker.this.mDecrementVirtualButtonPressed = false;
            if (NumberPicker.this.mDecrementVirtualButtonPressed) {
                NumberPicker numberPicker2 = NumberPicker.this;
                numberPicker2.invalidate(0, 0, numberPicker2.getRight(), NumberPicker.this.mTopSelectionDividerTop);
            }
        }

        public void buttonPressDelayed(int button) {
            cancel();
            this.mMode = 1;
            this.mManagedButton = button;
            NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getTapTimeout());
        }

        public void buttonTapped(int button) {
            cancel();
            this.mMode = 2;
            this.mManagedButton = button;
            NumberPicker.this.post(this);
        }

        public void run() {
            int i = this.mMode;
            if (i == 1) {
                int i2 = this.mManagedButton;
                if (i2 == 1) {
                    boolean unused = NumberPicker.this.mIncrementVirtualButtonPressed = true;
                    NumberPicker numberPicker = NumberPicker.this;
                    numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                } else if (i2 == 2) {
                    boolean unused2 = NumberPicker.this.mDecrementVirtualButtonPressed = true;
                    NumberPicker numberPicker2 = NumberPicker.this;
                    numberPicker2.invalidate(0, 0, numberPicker2.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                }
            } else if (i == 2) {
                int i3 = this.mManagedButton;
                if (i3 == 1) {
                    if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
                        NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                    }
                    NumberPicker numberPicker3 = NumberPicker.this;
                    boolean unused3 = numberPicker3.mIncrementVirtualButtonPressed = !numberPicker3.mIncrementVirtualButtonPressed;
                    NumberPicker numberPicker4 = NumberPicker.this;
                    numberPicker4.invalidate(0, numberPicker4.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                } else if (i3 == 2) {
                    if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
                        NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                    }
                    NumberPicker numberPicker5 = NumberPicker.this;
                    boolean unused4 = numberPicker5.mDecrementVirtualButtonPressed = !numberPicker5.mDecrementVirtualButtonPressed;
                    NumberPicker numberPicker6 = NumberPicker.this;
                    numberPicker6.invalidate(0, 0, numberPicker6.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                }
            }
        }
    }

    class ChangeCurrentByOneFromLongPressCommand implements Runnable {
        private boolean mIncrement;

        ChangeCurrentByOneFromLongPressCommand() {
        }

        /* access modifiers changed from: private */
        public void setStep(boolean increment) {
            this.mIncrement = increment;
        }

        public void run() {
            NumberPicker.this.changeValueByOne(this.mIncrement);
            NumberPicker numberPicker = NumberPicker.this;
            numberPicker.postDelayed(this, numberPicker.mLongPressUpdateInterval);
        }
    }

    private static String formatNumberWithLocale(int value) {
        return String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(value)});
    }
}
