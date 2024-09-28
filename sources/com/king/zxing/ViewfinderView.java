package com.king.zxing;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.google.zxing.ResultPoint;
import java.util.ArrayList;
import java.util.List;

public final class ViewfinderView extends View {
    private static final int CURRENT_POINT_OPACITY = 160;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 20;
    private int cornerColor;
    private int cornerRectHeight;
    private int cornerRectWidth;
    private Rect frame;
    private int frameColor;
    private int frameHeight;
    private int frameLineWidth;
    private float frameRatio;
    private int frameWidth;
    private int gridColumn;
    private int gridHeight;
    private boolean isShowResultPoint;
    private String labelText;
    private int labelTextColor;
    private TextLocation labelTextLocation;
    private float labelTextPadding;
    private float labelTextSize;
    private int laserColor;
    private LaserStyle laserStyle;
    private List<ResultPoint> lastPossibleResultPoints;
    private int maskColor;
    private Paint paint;
    private List<ResultPoint> possibleResultPoints;
    private int resultPointColor;
    private int scannerAnimationDelay;
    public int scannerEnd;
    private int scannerLineHeight;
    private int scannerLineMoveDistance;
    public int scannerStart;
    private int screenHeight;
    private int screenWidth;
    private TextPaint textPaint;

    public enum LaserStyle {
        NONE(0),
        LINE(1),
        GRID(2);
        
        /* access modifiers changed from: private */
        public int mValue;

        private LaserStyle(int value) {
            this.mValue = value;
        }

        /* access modifiers changed from: private */
        public static LaserStyle getFromInt(int value) {
            for (LaserStyle style : values()) {
                if (style.mValue == value) {
                    return style;
                }
            }
            return LINE;
        }
    }

    public enum TextLocation {
        TOP(0),
        BOTTOM(1);
        
        private int mValue;

        private TextLocation(int value) {
            this.mValue = value;
        }

        /* access modifiers changed from: private */
        public static TextLocation getFromInt(int value) {
            for (TextLocation location : values()) {
                if (location.mValue == value) {
                    return location;
                }
            }
            return TOP;
        }
    }

    public ViewfinderView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ViewfinderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewfinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.scannerStart = 0;
        this.scannerEnd = 0;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView);
        this.maskColor = array.getColor(R.styleable.ViewfinderView_maskColor, ContextCompat.getColor(context, R.color.viewfinder_mask));
        this.frameColor = array.getColor(R.styleable.ViewfinderView_frameColor, ContextCompat.getColor(context, R.color.viewfinder_frame));
        this.cornerColor = array.getColor(R.styleable.ViewfinderView_cornerColor, ContextCompat.getColor(context, R.color.viewfinder_corner));
        this.laserColor = array.getColor(R.styleable.ViewfinderView_laserColor, ContextCompat.getColor(context, R.color.viewfinder_laser));
        this.resultPointColor = array.getColor(R.styleable.ViewfinderView_resultPointColor, ContextCompat.getColor(context, R.color.viewfinder_result_point_color));
        this.labelText = array.getString(R.styleable.ViewfinderView_labelText);
        this.labelTextColor = array.getColor(R.styleable.ViewfinderView_labelTextColor, ContextCompat.getColor(context, R.color.viewfinder_text_color));
        this.labelTextSize = array.getDimension(R.styleable.ViewfinderView_labelTextSize, TypedValue.applyDimension(2, 14.0f, getResources().getDisplayMetrics()));
        this.labelTextPadding = array.getDimension(R.styleable.ViewfinderView_labelTextPadding, TypedValue.applyDimension(1, 24.0f, getResources().getDisplayMetrics()));
        this.labelTextLocation = TextLocation.getFromInt(array.getInt(R.styleable.ViewfinderView_labelTextLocation, 0));
        this.isShowResultPoint = array.getBoolean(R.styleable.ViewfinderView_showResultPoint, false);
        this.frameWidth = array.getDimensionPixelSize(R.styleable.ViewfinderView_frameWidth, 0);
        this.frameHeight = array.getDimensionPixelSize(R.styleable.ViewfinderView_frameHeight, 0);
        this.laserStyle = LaserStyle.getFromInt(array.getInt(R.styleable.ViewfinderView_laserStyle, LaserStyle.LINE.mValue));
        this.gridColumn = array.getInt(R.styleable.ViewfinderView_gridColumn, 20);
        this.gridHeight = (int) array.getDimension(R.styleable.ViewfinderView_gridHeight, TypedValue.applyDimension(1, 40.0f, getResources().getDisplayMetrics()));
        this.cornerRectWidth = (int) array.getDimension(R.styleable.ViewfinderView_cornerRectWidth, TypedValue.applyDimension(1, 4.0f, getResources().getDisplayMetrics()));
        this.cornerRectHeight = (int) array.getDimension(R.styleable.ViewfinderView_cornerRectHeight, TypedValue.applyDimension(1, 16.0f, getResources().getDisplayMetrics()));
        this.scannerLineMoveDistance = (int) array.getDimension(R.styleable.ViewfinderView_scannerLineMoveDistance, TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics()));
        this.scannerLineHeight = (int) array.getDimension(R.styleable.ViewfinderView_scannerLineHeight, TypedValue.applyDimension(1, 5.0f, getResources().getDisplayMetrics()));
        this.frameLineWidth = (int) array.getDimension(R.styleable.ViewfinderView_frameLineWidth, TypedValue.applyDimension(1, 1.0f, getResources().getDisplayMetrics()));
        this.scannerAnimationDelay = array.getInteger(R.styleable.ViewfinderView_scannerAnimationDelay, 15);
        this.frameRatio = array.getFloat(R.styleable.ViewfinderView_frameRatio, 0.625f);
        array.recycle();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        this.possibleResultPoints = new ArrayList(5);
        this.lastPossibleResultPoints = null;
        this.screenWidth = getDisplayMetrics().widthPixels;
        int i = getDisplayMetrics().heightPixels;
        this.screenHeight = i;
        int size = (int) (((float) Math.min(this.screenWidth, i)) * this.frameRatio);
        int i2 = this.frameWidth;
        if (i2 <= 0 || i2 > this.screenWidth) {
            this.frameWidth = size;
        }
        int i3 = this.frameHeight;
        if (i3 <= 0 || i3 > this.screenHeight) {
            this.frameHeight = size;
        }
    }

    private DisplayMetrics getDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }

    public void setLabelText(String labelText2) {
        this.labelText = labelText2;
    }

    public void setLabelTextColor(int color) {
        this.labelTextColor = color;
    }

    public void setLabelTextColorResource(int id) {
        this.labelTextColor = ContextCompat.getColor(getContext(), id);
    }

    public void setLabelTextSize(float textSize) {
        this.labelTextSize = textSize;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int leftOffset = (((this.screenWidth - this.frameWidth) / 2) + getPaddingLeft()) - getPaddingRight();
        int topOffset = (((this.screenHeight - this.frameHeight) / 2) + getPaddingTop()) - getPaddingBottom();
        this.frame = new Rect(leftOffset, topOffset, this.frameWidth + leftOffset, this.frameHeight + topOffset);
    }

    public void onDraw(Canvas canvas) {
        if (this.frame != null) {
            if (this.scannerStart == 0 || this.scannerEnd == 0) {
                this.scannerStart = this.frame.top;
                this.scannerEnd = this.frame.bottom - this.scannerLineHeight;
            }
            drawExterior(canvas, this.frame, canvas.getWidth(), canvas.getHeight());
            drawLaserScanner(canvas, this.frame);
            drawFrame(canvas, this.frame);
            drawCorner(canvas, this.frame);
            drawTextInfo(canvas, this.frame);
            drawResultPoint(canvas, this.frame);
            postInvalidateDelayed((long) this.scannerAnimationDelay, this.frame.left - 20, this.frame.top - 20, this.frame.right + 20, this.frame.bottom + 20);
        }
    }

    private void drawTextInfo(Canvas canvas, Rect frame2) {
        if (!TextUtils.isEmpty(this.labelText)) {
            this.textPaint.setColor(this.labelTextColor);
            this.textPaint.setTextSize(this.labelTextSize);
            this.textPaint.setTextAlign(Paint.Align.CENTER);
            StaticLayout staticLayout = new StaticLayout(this.labelText, this.textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            if (this.labelTextLocation == TextLocation.BOTTOM) {
                canvas.translate((float) (frame2.left + (frame2.width() / 2)), ((float) frame2.bottom) + this.labelTextPadding);
                staticLayout.draw(canvas);
                return;
            }
            canvas.translate((float) (frame2.left + (frame2.width() / 2)), (((float) frame2.top) - this.labelTextPadding) - ((float) staticLayout.getHeight()));
            staticLayout.draw(canvas);
        }
    }

    private void drawCorner(Canvas canvas, Rect frame2) {
        this.paint.setColor(this.cornerColor);
        canvas.drawRect((float) frame2.left, (float) frame2.top, (float) (frame2.left + this.cornerRectWidth), (float) (frame2.top + this.cornerRectHeight), this.paint);
        canvas.drawRect((float) frame2.left, (float) frame2.top, (float) (frame2.left + this.cornerRectHeight), (float) (frame2.top + this.cornerRectWidth), this.paint);
        canvas.drawRect((float) (frame2.right - this.cornerRectWidth), (float) frame2.top, (float) frame2.right, (float) (frame2.top + this.cornerRectHeight), this.paint);
        canvas.drawRect((float) (frame2.right - this.cornerRectHeight), (float) frame2.top, (float) frame2.right, (float) (frame2.top + this.cornerRectWidth), this.paint);
        canvas.drawRect((float) frame2.left, (float) (frame2.bottom - this.cornerRectWidth), (float) (frame2.left + this.cornerRectHeight), (float) frame2.bottom, this.paint);
        canvas.drawRect((float) frame2.left, (float) (frame2.bottom - this.cornerRectHeight), (float) (frame2.left + this.cornerRectWidth), (float) frame2.bottom, this.paint);
        canvas.drawRect((float) (frame2.right - this.cornerRectWidth), (float) (frame2.bottom - this.cornerRectHeight), (float) frame2.right, (float) frame2.bottom, this.paint);
        canvas.drawRect((float) (frame2.right - this.cornerRectHeight), (float) (frame2.bottom - this.cornerRectWidth), (float) frame2.right, (float) frame2.bottom, this.paint);
    }

    private void drawLaserScanner(Canvas canvas, Rect frame2) {
        if (this.laserStyle != null) {
            this.paint.setColor(this.laserColor);
            int i = AnonymousClass1.$SwitchMap$com$king$zxing$ViewfinderView$LaserStyle[this.laserStyle.ordinal()];
            if (i == 1) {
                drawLineScanner(canvas, frame2);
            } else if (i == 2) {
                drawGridScanner(canvas, frame2);
            }
            this.paint.setShader((Shader) null);
        }
    }

    /* renamed from: com.king.zxing.ViewfinderView$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$king$zxing$ViewfinderView$LaserStyle;

        static {
            int[] iArr = new int[LaserStyle.values().length];
            $SwitchMap$com$king$zxing$ViewfinderView$LaserStyle = iArr;
            try {
                iArr[LaserStyle.LINE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$king$zxing$ViewfinderView$LaserStyle[LaserStyle.GRID.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private void drawLineScanner(Canvas canvas, Rect frame2) {
        this.paint.setShader(new LinearGradient((float) frame2.left, (float) this.scannerStart, (float) frame2.left, (float) (this.scannerStart + this.scannerLineHeight), shadeColor(this.laserColor), this.laserColor, Shader.TileMode.MIRROR));
        if (this.scannerStart <= this.scannerEnd) {
            int i = frame2.right;
            int i2 = this.scannerLineHeight;
            canvas.drawOval(new RectF((float) (frame2.left + (this.scannerLineHeight * 2)), (float) this.scannerStart, (float) (i - (i2 * 2)), (float) (this.scannerStart + i2)), this.paint);
            this.scannerStart += this.scannerLineMoveDistance;
            return;
        }
        this.scannerStart = frame2.top;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000f, code lost:
        r3 = r0.scannerStart - r1.top;
        r4 = r0.gridHeight;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void drawGridScanner(android.graphics.Canvas r17, android.graphics.Rect r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r18
            r2 = 2
            android.graphics.Paint r3 = r0.paint
            float r4 = (float) r2
            r3.setStrokeWidth(r4)
            int r3 = r0.gridHeight
            if (r3 <= 0) goto L_0x001c
            int r3 = r0.scannerStart
            int r4 = r1.top
            int r3 = r3 - r4
            int r4 = r0.gridHeight
            if (r3 <= r4) goto L_0x001c
            int r3 = r0.scannerStart
            int r3 = r3 - r4
            goto L_0x001e
        L_0x001c:
            int r3 = r1.top
        L_0x001e:
            android.graphics.LinearGradient r12 = new android.graphics.LinearGradient
            int r4 = r1.left
            int r5 = r18.width()
            r6 = 2
            int r5 = r5 / r6
            int r4 = r4 + r5
            float r5 = (float) r4
            float r7 = (float) r3
            int r4 = r1.left
            int r8 = r18.width()
            int r8 = r8 / r6
            int r4 = r4 + r8
            float r8 = (float) r4
            int r4 = r0.scannerStart
            float r9 = (float) r4
            int[] r10 = new int[r6]
            r4 = 0
            int r11 = r0.laserColor
            int r11 = r0.shadeColor(r11)
            r10[r4] = r11
            int r4 = r0.laserColor
            r11 = 1
            r10[r11] = r4
            float[] r11 = new float[r6]
            r11 = {0, 1065353216} // fill-array
            android.graphics.Shader$TileMode r13 = android.graphics.Shader.TileMode.CLAMP
            r4 = r12
            r6 = r7
            r7 = r8
            r8 = r9
            r9 = r10
            r10 = r11
            r11 = r13
            r4.<init>(r5, r6, r7, r8, r9, r10, r11)
            android.graphics.Paint r5 = r0.paint
            r5.setShader(r4)
            int r5 = r18.width()
            float r5 = (float) r5
            r6 = 1065353216(0x3f800000, float:1.0)
            float r5 = r5 * r6
            int r6 = r0.gridColumn
            float r6 = (float) r6
            float r5 = r5 / r6
            r6 = r5
            r7 = 1
        L_0x006c:
            int r8 = r0.gridColumn
            if (r7 >= r8) goto L_0x008e
            int r8 = r1.left
            float r8 = (float) r8
            float r9 = (float) r7
            float r9 = r9 * r5
            float r11 = r8 + r9
            float r12 = (float) r3
            int r8 = r1.left
            float r8 = (float) r8
            float r9 = (float) r7
            float r9 = r9 * r5
            float r13 = r8 + r9
            int r8 = r0.scannerStart
            float r14 = (float) r8
            android.graphics.Paint r15 = r0.paint
            r10 = r17
            r10.drawLine(r11, r12, r13, r14, r15)
            int r7 = r7 + 1
            goto L_0x006c
        L_0x008e:
            int r7 = r0.gridHeight
            if (r7 <= 0) goto L_0x009c
            int r7 = r0.scannerStart
            int r8 = r1.top
            int r7 = r7 - r8
            int r8 = r0.gridHeight
            if (r7 <= r8) goto L_0x009c
            goto L_0x00a2
        L_0x009c:
            int r7 = r0.scannerStart
            int r8 = r1.top
            int r8 = r7 - r8
        L_0x00a2:
            r7 = r8
            r8 = 0
        L_0x00a4:
            float r9 = (float) r8
            float r10 = (float) r7
            float r10 = r10 / r6
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 > 0) goto L_0x00cb
            int r9 = r1.left
            float r11 = (float) r9
            int r9 = r0.scannerStart
            float r9 = (float) r9
            float r10 = (float) r8
            float r10 = r10 * r6
            float r12 = r9 - r10
            int r9 = r1.right
            float r13 = (float) r9
            int r9 = r0.scannerStart
            float r9 = (float) r9
            float r10 = (float) r8
            float r10 = r10 * r6
            float r14 = r9 - r10
            android.graphics.Paint r15 = r0.paint
            r10 = r17
            r10.drawLine(r11, r12, r13, r14, r15)
            int r8 = r8 + 1
            goto L_0x00a4
        L_0x00cb:
            int r8 = r0.scannerStart
            int r9 = r0.scannerEnd
            if (r8 >= r9) goto L_0x00d7
            int r9 = r0.scannerLineMoveDistance
            int r8 = r8 + r9
            r0.scannerStart = r8
            goto L_0x00db
        L_0x00d7:
            int r8 = r1.top
            r0.scannerStart = r8
        L_0x00db:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.king.zxing.ViewfinderView.drawGridScanner(android.graphics.Canvas, android.graphics.Rect):void");
    }

    public int shadeColor(int color) {
        String hax = Integer.toHexString(color);
        return Integer.valueOf("01" + hax.substring(2), 16).intValue();
    }

    private void drawFrame(Canvas canvas, Rect frame2) {
        this.paint.setColor(this.frameColor);
        canvas.drawRect((float) frame2.left, (float) frame2.top, (float) frame2.right, (float) (frame2.top + this.frameLineWidth), this.paint);
        canvas.drawRect((float) frame2.left, (float) frame2.top, (float) (frame2.left + this.frameLineWidth), (float) frame2.bottom, this.paint);
        canvas.drawRect((float) (frame2.right - this.frameLineWidth), (float) frame2.top, (float) frame2.right, (float) frame2.bottom, this.paint);
        canvas.drawRect((float) frame2.left, (float) (frame2.bottom - this.frameLineWidth), (float) frame2.right, (float) frame2.bottom, this.paint);
    }

    private void drawExterior(Canvas canvas, Rect frame2, int width, int height) {
        this.paint.setColor(this.maskColor);
        canvas.drawRect(0.0f, 0.0f, (float) width, (float) frame2.top, this.paint);
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, (float) frame2.top, (float) frame2.left, (float) frame2.bottom, this.paint);
        canvas2.drawRect((float) frame2.right, (float) frame2.top, (float) width, (float) frame2.bottom, this.paint);
        canvas2.drawRect(0.0f, (float) frame2.bottom, (float) width, (float) height, this.paint);
    }

    private void drawResultPoint(Canvas canvas, Rect frame2) {
        if (this.isShowResultPoint) {
            List<ResultPoint> currentPossible = this.possibleResultPoints;
            List<ResultPoint> currentLast = this.lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                this.lastPossibleResultPoints = null;
            } else {
                this.possibleResultPoints = new ArrayList(5);
                this.lastPossibleResultPoints = currentPossible;
                this.paint.setAlpha(CURRENT_POINT_OPACITY);
                this.paint.setColor(this.resultPointColor);
                synchronized (currentPossible) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(point.getX(), point.getY(), 10.0f, this.paint);
                    }
                }
            }
            if (currentLast != null) {
                this.paint.setAlpha(80);
                this.paint.setColor(this.resultPointColor);
                synchronized (currentLast) {
                    for (ResultPoint point2 : currentLast) {
                        canvas.drawCircle(point2.getX(), point2.getY(), 10.0f, this.paint);
                    }
                }
            }
        }
    }

    public void drawViewfinder() {
        invalidate();
    }

    public boolean isShowResultPoint() {
        return this.isShowResultPoint;
    }

    public void setLaserStyle(LaserStyle laserStyle2) {
        this.laserStyle = laserStyle2;
    }

    public void setShowResultPoint(boolean showResultPoint) {
        this.isShowResultPoint = showResultPoint;
    }

    public void addPossibleResultPoint(ResultPoint point) {
        if (this.isShowResultPoint) {
            List<ResultPoint> points = this.possibleResultPoints;
            synchronized (points) {
                points.add(point);
                int size = points.size();
                if (size > 20) {
                    points.subList(0, size - 10).clear();
                }
            }
        }
    }
}
