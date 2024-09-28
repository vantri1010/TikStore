package com.contrarywind.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.contrarywind.adapter.WheelAdapter;
import com.contrarywind.interfaces.IPickerViewData;
import com.contrarywind.listener.LoopViewGestureListener;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.timer.InertiaTimerTask;
import com.contrarywind.timer.MessageHandler;
import com.contrarywind.timer.SmoothScrollTimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WheelView extends View {
    private static final float SCALE_CONTENT = 0.8f;
    private static final String[] TIME_NUM = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09"};
    private static final int VELOCITY_FLING = 5;
    private float CENTER_CONTENT_OFFSET;
    private WheelAdapter adapter;
    private float centerY;
    private Context context;
    private int dividerColor;
    private DividerType dividerType;
    private int dividerWidth;
    private int drawCenterContentStart;
    private int drawOutContentStart;
    private float firstLineY;
    private GestureDetector gestureDetector;
    private Handler handler;
    private int initPosition;
    private boolean isAlphaGradient;
    private boolean isCenterLabel;
    private boolean isLoop;
    private boolean isOptions;
    private float itemHeight;
    private int itemsVisible;
    private String label;
    private float lineSpacingMultiplier;
    private ScheduledExecutorService mExecutor;
    private ScheduledFuture<?> mFuture;
    private int mGravity;
    private int mOffset;
    private int maxTextHeight;
    private int maxTextWidth;
    private int measuredHeight;
    private int measuredWidth;
    /* access modifiers changed from: private */
    public OnItemSelectedListener onItemSelectedListener;
    private Paint paintCenterText;
    private Paint paintIndicator;
    private Paint paintOuterText;
    private int preCurrentIndex;
    private float previousY;
    private int radius;
    private float secondLineY;
    private int selectedItem;
    private long startTime;
    private int textColorCenter;
    private int textColorOut;
    private int textSize;
    private int textXOffset;
    private float totalScrollY;
    private Typeface typeface;
    private int widthMeasureSpec;

    public enum ACTION {
        CLICK,
        FLING,
        DAGGLE
    }

    public enum DividerType {
        FILL,
        WRAP,
        CIRCLE
    }

    public WheelView(Context context2) {
        this(context2, (AttributeSet) null);
    }

    public WheelView(Context context2, AttributeSet attrs) {
        super(context2, attrs);
        this.isOptions = false;
        this.isCenterLabel = true;
        this.mExecutor = Executors.newSingleThreadScheduledExecutor();
        this.typeface = Typeface.MONOSPACE;
        this.lineSpacingMultiplier = 1.6f;
        this.itemsVisible = 11;
        this.mOffset = 0;
        this.previousY = 0.0f;
        this.startTime = 0;
        this.mGravity = 17;
        this.drawCenterContentStart = 0;
        this.drawOutContentStart = 0;
        this.isAlphaGradient = false;
        this.textSize = getResources().getDimensionPixelSize(R.dimen.pickerview_textsize);
        float density = getResources().getDisplayMetrics().density;
        if (density < 1.0f) {
            this.CENTER_CONTENT_OFFSET = 2.4f;
        } else if (1.0f <= density && density < 2.0f) {
            this.CENTER_CONTENT_OFFSET = 4.0f;
        } else if (2.0f <= density && density < 3.0f) {
            this.CENTER_CONTENT_OFFSET = 6.0f;
        } else if (density >= 3.0f) {
            this.CENTER_CONTENT_OFFSET = 2.5f * density;
        }
        if (attrs != null) {
            TypedArray a = context2.obtainStyledAttributes(attrs, R.styleable.pickerview, 0, 0);
            this.mGravity = a.getInt(R.styleable.pickerview_wheelview_gravity, 17);
            this.textColorOut = a.getColor(R.styleable.pickerview_wheelview_textColorOut, -5723992);
            this.textColorCenter = a.getColor(R.styleable.pickerview_wheelview_textColorCenter, -14013910);
            this.dividerColor = a.getColor(R.styleable.pickerview_wheelview_dividerColor, -2763307);
            this.dividerWidth = a.getDimensionPixelSize(R.styleable.pickerview_wheelview_dividerWidth, 2);
            this.textSize = a.getDimensionPixelOffset(R.styleable.pickerview_wheelview_textSize, this.textSize);
            this.lineSpacingMultiplier = a.getFloat(R.styleable.pickerview_wheelview_lineSpacingMultiplier, this.lineSpacingMultiplier);
            a.recycle();
        }
        judgeLineSpace();
        initLoopView(context2);
    }

    private void judgeLineSpace() {
        float f = this.lineSpacingMultiplier;
        if (f < 1.0f) {
            this.lineSpacingMultiplier = 1.0f;
        } else if (f > 4.0f) {
            this.lineSpacingMultiplier = 4.0f;
        }
    }

    private void initLoopView(Context context2) {
        this.context = context2;
        this.handler = new MessageHandler(this);
        GestureDetector gestureDetector2 = new GestureDetector(context2, new LoopViewGestureListener(this));
        this.gestureDetector = gestureDetector2;
        gestureDetector2.setIsLongpressEnabled(false);
        this.isLoop = true;
        this.totalScrollY = 0.0f;
        this.initPosition = -1;
        initPaints();
    }

    private void initPaints() {
        Paint paint = new Paint();
        this.paintOuterText = paint;
        paint.setColor(this.textColorOut);
        this.paintOuterText.setAntiAlias(true);
        this.paintOuterText.setTypeface(this.typeface);
        this.paintOuterText.setTextSize((float) this.textSize);
        Paint paint2 = new Paint();
        this.paintCenterText = paint2;
        paint2.setColor(this.textColorCenter);
        this.paintCenterText.setAntiAlias(true);
        this.paintCenterText.setTextScaleX(1.1f);
        this.paintCenterText.setTypeface(this.typeface);
        this.paintCenterText.setTextSize((float) this.textSize);
        Paint paint3 = new Paint();
        this.paintIndicator = paint3;
        paint3.setColor(this.dividerColor);
        this.paintIndicator.setAntiAlias(true);
        setLayerType(1, (Paint) null);
    }

    private void reMeasure() {
        if (this.adapter != null) {
            measureTextWidthHeight();
            int halfCircumference = (int) (this.itemHeight * ((float) (this.itemsVisible - 1)));
            this.measuredHeight = (int) (((double) (halfCircumference * 2)) / 3.141592653589793d);
            this.radius = (int) (((double) halfCircumference) / 3.141592653589793d);
            this.measuredWidth = View.MeasureSpec.getSize(this.widthMeasureSpec);
            int i = this.measuredHeight;
            float f = this.itemHeight;
            this.firstLineY = (((float) i) - f) / 2.0f;
            float f2 = (((float) i) + f) / 2.0f;
            this.secondLineY = f2;
            this.centerY = (f2 - ((f - ((float) this.maxTextHeight)) / 2.0f)) - this.CENTER_CONTENT_OFFSET;
            if (this.initPosition == -1) {
                if (this.isLoop) {
                    this.initPosition = (this.adapter.getItemsCount() + 1) / 2;
                } else {
                    this.initPosition = 0;
                }
            }
            this.preCurrentIndex = this.initPosition;
        }
    }

    private void measureTextWidthHeight() {
        Rect rect = new Rect();
        for (int i = 0; i < this.adapter.getItemsCount(); i++) {
            String s1 = getContentText(this.adapter.getItem(i));
            this.paintCenterText.getTextBounds(s1, 0, s1.length(), rect);
            int textWidth = rect.width();
            if (textWidth > this.maxTextWidth) {
                this.maxTextWidth = textWidth;
            }
        }
        this.paintCenterText.getTextBounds("星期", 0, 2, rect);
        int height = rect.height() + 2;
        this.maxTextHeight = height;
        this.itemHeight = this.lineSpacingMultiplier * ((float) height);
    }

    public void smoothScroll(ACTION action) {
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            float f = this.totalScrollY;
            float f2 = this.itemHeight;
            int i = (int) (((f % f2) + f2) % f2);
            this.mOffset = i;
            if (((float) i) > f2 / 2.0f) {
                this.mOffset = (int) (f2 - ((float) i));
            } else {
                this.mOffset = -i;
            }
        }
        this.mFuture = this.mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, this.mOffset), 0, 10, TimeUnit.MILLISECONDS);
    }

    public final void scrollBy(float velocityY) {
        cancelFuture();
        this.mFuture = this.mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, 5, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        ScheduledFuture<?> scheduledFuture = this.mFuture;
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            this.mFuture.cancel(true);
            this.mFuture = null;
        }
    }

    public final void setCyclic(boolean cyclic) {
        this.isLoop = cyclic;
    }

    public final void setTypeface(Typeface font) {
        this.typeface = font;
        this.paintOuterText.setTypeface(font);
        this.paintCenterText.setTypeface(this.typeface);
    }

    public final void setTextSize(float size) {
        if (size > 0.0f) {
            int i = (int) (this.context.getResources().getDisplayMetrics().density * size);
            this.textSize = i;
            this.paintOuterText.setTextSize((float) i);
            this.paintCenterText.setTextSize((float) this.textSize);
        }
    }

    public final void setCurrentItem(int currentItem) {
        this.selectedItem = currentItem;
        this.initPosition = currentItem;
        this.totalScrollY = 0.0f;
        invalidate();
    }

    public final void setOnItemSelectedListener(OnItemSelectedListener OnItemSelectedListener) {
        this.onItemSelectedListener = OnItemSelectedListener;
    }

    public final void setAdapter(WheelAdapter adapter2) {
        this.adapter = adapter2;
        reMeasure();
        invalidate();
    }

    public void setItemsVisibleCount(int visibleCount) {
        if (visibleCount % 2 == 0) {
            visibleCount++;
        }
        this.itemsVisible = visibleCount + 2;
    }

    public void setAlphaGradient(boolean alphaGradient) {
        this.isAlphaGradient = alphaGradient;
    }

    public final WheelAdapter getAdapter() {
        return this.adapter;
    }

    public final int getCurrentItem() {
        int i;
        WheelAdapter wheelAdapter = this.adapter;
        if (wheelAdapter == null) {
            return 0;
        }
        if (!this.isLoop || ((i = this.selectedItem) >= 0 && i < wheelAdapter.getItemsCount())) {
            return Math.max(0, Math.min(this.selectedItem, this.adapter.getItemsCount() - 1));
        }
        return Math.max(0, Math.min(Math.abs(Math.abs(this.selectedItem) - this.adapter.getItemsCount()), this.adapter.getItemsCount() - 1));
    }

    public final void onItemSelected() {
        if (this.onItemSelectedListener != null) {
            postDelayed(new Runnable() {
                public void run() {
                    WheelView.this.onItemSelectedListener.onItemSelected(WheelView.this.getCurrentItem());
                }
            }, 200);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Object showText;
        int change;
        String contentText;
        float startX;
        float startX2;
        float startX3;
        Canvas canvas2 = canvas;
        if (this.adapter != null) {
            int min = Math.min(Math.max(0, this.initPosition), this.adapter.getItemsCount() - 1);
            this.initPosition = min;
            int change2 = (int) (this.totalScrollY / this.itemHeight);
            try {
                this.preCurrentIndex = min + (change2 % this.adapter.getItemsCount());
            } catch (ArithmeticException e) {
                Log.e("WheelView", "出错了！adapter.getItemsCount() == 0，联动数据不匹配");
            }
            if (!this.isLoop) {
                if (this.preCurrentIndex < 0) {
                    this.preCurrentIndex = 0;
                }
                if (this.preCurrentIndex > this.adapter.getItemsCount() - 1) {
                    this.preCurrentIndex = this.adapter.getItemsCount() - 1;
                }
            } else {
                if (this.preCurrentIndex < 0) {
                    this.preCurrentIndex = this.adapter.getItemsCount() + this.preCurrentIndex;
                }
                if (this.preCurrentIndex > this.adapter.getItemsCount() - 1) {
                    this.preCurrentIndex -= this.adapter.getItemsCount();
                }
            }
            float itemHeightOffset = this.totalScrollY % this.itemHeight;
            float f = 0.0f;
            if (this.dividerType == DividerType.WRAP) {
                if (TextUtils.isEmpty(this.label)) {
                    startX2 = (float) (((this.measuredWidth - this.maxTextWidth) / 2) - 12);
                } else {
                    startX2 = (float) (((this.measuredWidth - this.maxTextWidth) / 4) - 12);
                }
                if (startX2 <= 0.0f) {
                    startX3 = 10.0f;
                } else {
                    startX3 = startX2;
                }
                float endX = ((float) this.measuredWidth) - startX3;
                float f2 = this.firstLineY;
                Canvas canvas3 = canvas;
                float f3 = startX3;
                float f4 = endX;
                canvas3.drawLine(f3, f2, f4, f2, this.paintIndicator);
                float f5 = this.secondLineY;
                canvas3.drawLine(f3, f5, f4, f5, this.paintIndicator);
            } else if (this.dividerType == DividerType.CIRCLE) {
                this.paintIndicator.setStyle(Paint.Style.STROKE);
                this.paintIndicator.setStrokeWidth((float) this.dividerWidth);
                if (TextUtils.isEmpty(this.label)) {
                    startX = (((float) (this.measuredWidth - this.maxTextWidth)) / 2.0f) - 12.0f;
                } else {
                    startX = (((float) (this.measuredWidth - this.maxTextWidth)) / 4.0f) - 12.0f;
                }
                if (startX <= 0.0f) {
                    startX = 10.0f;
                }
                canvas2.drawCircle(((float) this.measuredWidth) / 2.0f, ((float) this.measuredHeight) / 2.0f, Math.max((((float) this.measuredWidth) - startX) - startX, this.itemHeight) / 1.8f, this.paintIndicator);
            } else {
                float f6 = this.firstLineY;
                canvas.drawLine(0.0f, f6, (float) this.measuredWidth, f6, this.paintIndicator);
                float f7 = this.secondLineY;
                canvas.drawLine(0.0f, f7, (float) this.measuredWidth, f7, this.paintIndicator);
            }
            if (!TextUtils.isEmpty(this.label) && this.isCenterLabel) {
                canvas2.drawText(this.label, ((float) (this.measuredWidth - getTextWidth(this.paintCenterText, this.label))) - this.CENTER_CONTENT_OFFSET, this.centerY, this.paintCenterText);
            }
            int counter = 0;
            while (true) {
                int i = this.itemsVisible;
                if (counter < i) {
                    int index = this.preCurrentIndex - ((i / 2) - counter);
                    if (this.isLoop) {
                        index = getLoopMappingIndex(index);
                        showText = this.adapter.getItem(index);
                    } else if (index < 0) {
                        showText = "";
                    } else if (index > this.adapter.getItemsCount() - 1) {
                        showText = "";
                    } else {
                        showText = this.adapter.getItem(index);
                    }
                    canvas.save();
                    double radian = (double) (((this.itemHeight * ((float) counter)) - itemHeightOffset) / ((float) this.radius));
                    float angle = (float) (90.0d - ((radian / 3.141592653589793d) * 180.0d));
                    if (angle > 90.0f) {
                        int i2 = index;
                        change = change2;
                    } else if (angle < -90.0f) {
                        Object obj = showText;
                        int i3 = index;
                        change = change2;
                    } else {
                        if (this.isCenterLabel || TextUtils.isEmpty(this.label) || TextUtils.isEmpty(getContentText(showText))) {
                            contentText = getContentText(showText);
                        } else {
                            contentText = getContentText(showText) + this.label;
                        }
                        change = change2;
                        float offsetCoefficient = (float) Math.pow((double) (Math.abs(angle) / 90.0f), 2.2d);
                        reMeasureTextSize(contentText);
                        measuredCenterContentStart(contentText);
                        measuredOutContentStart(contentText);
                        String contentText2 = contentText;
                        Object obj2 = showText;
                        int i4 = index;
                        float translateY = (float) ((((double) this.radius) - (Math.cos(radian) * ((double) this.radius))) - ((Math.sin(radian) * ((double) this.maxTextHeight)) / 2.0d));
                        canvas2.translate(f, translateY);
                        float f8 = this.firstLineY;
                        if (translateY > f8 || ((float) this.maxTextHeight) + translateY < f8) {
                            float offsetCoefficient2 = offsetCoefficient;
                            String contentText3 = contentText2;
                            float offsetCoefficient3 = this.secondLineY;
                            if (translateY > offsetCoefficient3 || ((float) this.maxTextHeight) + translateY < offsetCoefficient3) {
                                if (translateY >= this.firstLineY) {
                                    int i5 = this.maxTextHeight;
                                    if (((float) i5) + translateY <= this.secondLineY) {
                                        canvas2.drawText(contentText3, (float) this.drawCenterContentStart, ((float) i5) - this.CENTER_CONTENT_OFFSET, this.paintCenterText);
                                        this.selectedItem = this.preCurrentIndex - ((this.itemsVisible / 2) - counter);
                                    }
                                }
                                canvas.save();
                                canvas2.clipRect(0, 0, this.measuredWidth, (int) this.itemHeight);
                                canvas2.scale(1.0f, ((float) Math.sin(radian)) * SCALE_CONTENT);
                                setOutPaintStyle(offsetCoefficient2, angle);
                                canvas2.drawText(contentText3, ((float) this.drawOutContentStart) + (((float) this.textXOffset) * offsetCoefficient2), (float) this.maxTextHeight, this.paintOuterText);
                                canvas.restore();
                            } else {
                                canvas.save();
                                canvas2.clipRect(f, f, (float) this.measuredWidth, this.secondLineY - translateY);
                                canvas2.scale(1.0f, ((float) Math.sin(radian)) * 1.0f);
                                canvas2.drawText(contentText3, (float) this.drawCenterContentStart, ((float) this.maxTextHeight) - this.CENTER_CONTENT_OFFSET, this.paintCenterText);
                                canvas.restore();
                                canvas.save();
                                canvas2.clipRect(0.0f, this.secondLineY - translateY, (float) this.measuredWidth, (float) ((int) this.itemHeight));
                                canvas2.scale(1.0f, ((float) Math.sin(radian)) * SCALE_CONTENT);
                                setOutPaintStyle(offsetCoefficient2, angle);
                                canvas2.drawText(contentText3, (float) this.drawOutContentStart, (float) this.maxTextHeight, this.paintOuterText);
                                canvas.restore();
                            }
                        } else {
                            canvas.save();
                            canvas2.clipRect(f, f, (float) this.measuredWidth, this.firstLineY - translateY);
                            canvas2.scale(1.0f, ((float) Math.sin(radian)) * SCALE_CONTENT);
                            setOutPaintStyle(offsetCoefficient, angle);
                            String contentText4 = contentText2;
                            canvas2.drawText(contentText4, (float) this.drawOutContentStart, (float) this.maxTextHeight, this.paintOuterText);
                            canvas.restore();
                            canvas.save();
                            canvas2.clipRect(f, this.firstLineY - translateY, (float) this.measuredWidth, (float) ((int) this.itemHeight));
                            float f9 = offsetCoefficient;
                            canvas2.scale(1.0f, ((float) Math.sin(radian)) * 1.0f);
                            canvas2.drawText(contentText4, (float) this.drawCenterContentStart, ((float) this.maxTextHeight) - this.CENTER_CONTENT_OFFSET, this.paintCenterText);
                            canvas.restore();
                        }
                        canvas.restore();
                        this.paintCenterText.setTextSize((float) this.textSize);
                        counter++;
                        change2 = change;
                        f = 0.0f;
                    }
                    canvas.restore();
                    counter++;
                    change2 = change;
                    f = 0.0f;
                } else {
                    return;
                }
            }
        }
    }

    private void setOutPaintStyle(float offsetCoefficient, float angle) {
        int multiplier = 0;
        int i = this.textXOffset;
        if (i > 0) {
            multiplier = 1;
        } else if (i < 0) {
            multiplier = -1;
        }
        this.paintOuterText.setTextSkewX(((float) ((angle > 0.0f ? -1 : 1) * multiplier)) * 0.5f * offsetCoefficient);
        this.paintOuterText.setAlpha(this.isAlphaGradient ? (int) (((90.0f - Math.abs(angle)) / 90.0f) * 255.0f) : 255);
    }

    private void reMeasureTextSize(String contentText) {
        Rect rect = new Rect();
        this.paintCenterText.getTextBounds(contentText, 0, contentText.length(), rect);
        int size = this.textSize;
        for (int width = rect.width(); width > this.measuredWidth; width = rect.width()) {
            size--;
            this.paintCenterText.setTextSize((float) size);
            this.paintCenterText.getTextBounds(contentText, 0, contentText.length(), rect);
        }
        this.paintOuterText.setTextSize((float) size);
    }

    private int getLoopMappingIndex(int index) {
        if (index < 0) {
            return getLoopMappingIndex(index + this.adapter.getItemsCount());
        }
        if (index > this.adapter.getItemsCount() - 1) {
            return getLoopMappingIndex(index - this.adapter.getItemsCount());
        }
        return index;
    }

    private String getContentText(Object item) {
        if (item == null) {
            return "";
        }
        if (item instanceof IPickerViewData) {
            return ((IPickerViewData) item).getPickerViewText();
        }
        if (item instanceof Integer) {
            return getFixNum(((Integer) item).intValue());
        }
        return item.toString();
    }

    private String getFixNum(int timeNum) {
        return (timeNum < 0 || timeNum >= 10) ? String.valueOf(timeNum) : TIME_NUM[timeNum];
    }

    private void measuredCenterContentStart(String content) {
        String str;
        Rect rect = new Rect();
        this.paintCenterText.getTextBounds(content, 0, content.length(), rect);
        int i = this.mGravity;
        if (i == 3) {
            this.drawCenterContentStart = 0;
        } else if (i == 5) {
            this.drawCenterContentStart = (this.measuredWidth - rect.width()) - ((int) this.CENTER_CONTENT_OFFSET);
        } else if (i == 17) {
            if (this.isOptions || (str = this.label) == null || str.equals("") || !this.isCenterLabel) {
                this.drawCenterContentStart = (int) (((double) (this.measuredWidth - rect.width())) * 0.5d);
            } else {
                this.drawCenterContentStart = (int) (((double) (this.measuredWidth - rect.width())) * 0.25d);
            }
        }
    }

    private void measuredOutContentStart(String content) {
        String str;
        Rect rect = new Rect();
        this.paintOuterText.getTextBounds(content, 0, content.length(), rect);
        int i = this.mGravity;
        if (i == 3) {
            this.drawOutContentStart = 0;
        } else if (i == 5) {
            this.drawOutContentStart = (this.measuredWidth - rect.width()) - ((int) this.CENTER_CONTENT_OFFSET);
        } else if (i == 17) {
            if (this.isOptions || (str = this.label) == null || str.equals("") || !this.isCenterLabel) {
                this.drawOutContentStart = (int) (((double) (this.measuredWidth - rect.width())) * 0.5d);
            } else {
                this.drawOutContentStart = (int) (((double) (this.measuredWidth - rect.width())) * 0.25d);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec2, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec2;
        reMeasure();
        setMeasuredDimension(this.measuredWidth, this.measuredHeight);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = this.gestureDetector.onTouchEvent(event);
        boolean isIgnore = false;
        float top = ((float) (-this.initPosition)) * this.itemHeight;
        float bottom = ((float) ((this.adapter.getItemsCount() - 1) - this.initPosition)) * this.itemHeight;
        int action = event.getAction();
        if (action == 0) {
            this.startTime = System.currentTimeMillis();
            cancelFuture();
            this.previousY = event.getRawY();
        } else if (action == 2) {
            float dy = this.previousY - event.getRawY();
            this.previousY = event.getRawY();
            float f = this.totalScrollY + dy;
            this.totalScrollY = f;
            if (!this.isLoop) {
                if ((f - (this.itemHeight * 0.25f) >= top || dy >= 0.0f) && (this.totalScrollY + (this.itemHeight * 0.25f) <= bottom || dy <= 0.0f)) {
                    isIgnore = false;
                } else {
                    this.totalScrollY -= dy;
                    isIgnore = true;
                }
            }
        } else if (!eventConsumed) {
            float y = event.getY();
            int i = this.radius;
            double L = Math.acos((double) ((((float) i) - y) / ((float) i))) * ((double) this.radius);
            float f2 = this.itemHeight;
            this.mOffset = (int) ((((float) (((int) ((((double) (f2 / 2.0f)) + L) / ((double) f2))) - (this.itemsVisible / 2))) * f2) - (((this.totalScrollY % f2) + f2) % f2));
            boolean z = eventConsumed;
            if (System.currentTimeMillis() - this.startTime > 120) {
                smoothScroll(ACTION.DAGGLE);
            } else {
                smoothScroll(ACTION.CLICK);
            }
        }
        if (isIgnore || event.getAction() == 0) {
            return true;
        }
        invalidate();
        return true;
    }

    public int getItemsCount() {
        WheelAdapter wheelAdapter = this.adapter;
        if (wheelAdapter != null) {
            return wheelAdapter.getItemsCount();
        }
        return 0;
    }

    public void setLabel(String label2) {
        this.label = label2;
    }

    public void isCenterLabel(boolean isCenterLabel2) {
        this.isCenterLabel = isCenterLabel2;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil((double) widths[j]);
            }
        }
        return iRet;
    }

    public void setIsOptions(boolean options) {
        this.isOptions = options;
    }

    public void setTextColorOut(int textColorOut2) {
        this.textColorOut = textColorOut2;
        this.paintOuterText.setColor(textColorOut2);
    }

    public void setTextColorCenter(int textColorCenter2) {
        this.textColorCenter = textColorCenter2;
        this.paintCenterText.setColor(textColorCenter2);
    }

    public void setTextXOffset(int textXOffset2) {
        this.textXOffset = textXOffset2;
        if (textXOffset2 != 0) {
            this.paintCenterText.setTextScaleX(1.0f);
        }
    }

    public void setDividerWidth(int dividerWidth2) {
        this.dividerWidth = dividerWidth2;
        this.paintIndicator.setStrokeWidth((float) dividerWidth2);
    }

    public void setDividerColor(int dividerColor2) {
        this.dividerColor = dividerColor2;
        this.paintIndicator.setColor(dividerColor2);
    }

    public void setDividerType(DividerType dividerType2) {
        this.dividerType = dividerType2;
    }

    public void setLineSpacingMultiplier(float lineSpacingMultiplier2) {
        if (lineSpacingMultiplier2 != 0.0f) {
            this.lineSpacingMultiplier = lineSpacingMultiplier2;
            judgeLineSpace();
        }
    }

    public boolean isLoop() {
        return this.isLoop;
    }

    public float getTotalScrollY() {
        return this.totalScrollY;
    }

    public void setTotalScrollY(float totalScrollY2) {
        this.totalScrollY = totalScrollY2;
    }

    public float getItemHeight() {
        return this.itemHeight;
    }

    public int getInitPosition() {
        return this.initPosition;
    }

    public Handler getHandler() {
        return this.handler;
    }
}
