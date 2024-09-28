package im.bclpbkiauv.ui.components.crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class CropAreaView extends View {
    private Control activeControl;
    private RectF actualRect;
    /* access modifiers changed from: private */
    public Animator animator;
    private RectF bottomEdge;
    private RectF bottomLeftCorner;
    private float bottomPadding;
    private RectF bottomRightCorner;
    private Bitmap circleBitmap;
    Paint dimPaint;
    private boolean dimVisibile;
    private Paint eraserPaint;
    Paint framePaint;
    private boolean frameVisible;
    private boolean freeform;
    /* access modifiers changed from: private */
    public Animator gridAnimator;
    private float gridProgress;
    private GridType gridType;
    Paint handlePaint;
    AccelerateDecelerateInterpolator interpolator;
    private boolean isDragging;
    boolean isFcCrop;
    private RectF leftEdge;
    Paint linePaint;
    private AreaViewListener listener;
    private float lockAspectRatio;
    private float minWidth;
    private GridType previousGridType;
    private int previousX;
    private int previousY;
    private RectF rightEdge;
    Paint shadowPaint;
    private float sidePadding;
    private RectF tempRect;
    private RectF topEdge;
    private RectF topLeftCorner;
    private RectF topRightCorner;

    interface AreaViewListener {
        void onAreaChange();

        void onAreaChangeBegan();

        void onAreaChangeEnded();
    }

    private enum Control {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    enum GridType {
        NONE,
        MINOR,
        MAJOR
    }

    public CropAreaView(Context context) {
        super(context);
        this.topLeftCorner = new RectF();
        this.topRightCorner = new RectF();
        this.bottomLeftCorner = new RectF();
        this.bottomRightCorner = new RectF();
        this.topEdge = new RectF();
        this.leftEdge = new RectF();
        this.bottomEdge = new RectF();
        this.rightEdge = new RectF();
        this.actualRect = new RectF();
        this.tempRect = new RectF();
        this.interpolator = new AccelerateDecelerateInterpolator();
        this.freeform = true;
        this.frameVisible = true;
        this.dimVisibile = true;
        this.sidePadding = (float) AndroidUtilities.dp(16.0f);
        this.minWidth = (float) AndroidUtilities.dp(32.0f);
        this.gridType = GridType.NONE;
        Paint paint = new Paint();
        this.dimPaint = paint;
        paint.setColor(-872415232);
        Paint paint2 = new Paint();
        this.shadowPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.shadowPaint.setColor(436207616);
        this.shadowPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        Paint paint3 = new Paint();
        this.linePaint = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.linePaint.setColor(-1);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        Paint paint4 = new Paint();
        this.handlePaint = paint4;
        paint4.setStyle(Paint.Style.FILL);
        this.handlePaint.setColor(-1);
        Paint paint5 = new Paint();
        this.framePaint = paint5;
        paint5.setStyle(Paint.Style.FILL);
        this.framePaint.setColor(-1291845633);
        Paint paint6 = new Paint(1);
        this.eraserPaint = paint6;
        paint6.setColor(0);
        this.eraserPaint.setStyle(Paint.Style.FILL);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public CropAreaView(Context context, boolean isFcCrop2) {
        super(context);
        this.topLeftCorner = new RectF();
        this.topRightCorner = new RectF();
        this.bottomLeftCorner = new RectF();
        this.bottomRightCorner = new RectF();
        this.topEdge = new RectF();
        this.leftEdge = new RectF();
        this.bottomEdge = new RectF();
        this.rightEdge = new RectF();
        this.actualRect = new RectF();
        this.tempRect = new RectF();
        this.interpolator = new AccelerateDecelerateInterpolator();
        this.freeform = true;
        this.isFcCrop = isFcCrop2;
        this.frameVisible = true;
        this.dimVisibile = true;
        this.sidePadding = (float) AndroidUtilities.dp(16.0f);
        this.minWidth = (float) AndroidUtilities.dp(32.0f);
        this.gridType = GridType.NONE;
        Paint paint = new Paint();
        this.dimPaint = paint;
        paint.setColor(-872415232);
        Paint paint2 = new Paint();
        this.shadowPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.shadowPaint.setColor(436207616);
        this.shadowPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        Paint paint3 = new Paint();
        this.linePaint = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.linePaint.setColor(-1);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        Paint paint4 = new Paint();
        this.handlePaint = paint4;
        paint4.setStyle(Paint.Style.FILL);
        this.handlePaint.setColor(-1);
        Paint paint5 = new Paint();
        this.framePaint = paint5;
        paint5.setStyle(Paint.Style.FILL);
        this.framePaint.setColor(-1291845633);
        Paint paint6 = new Paint(1);
        this.eraserPaint = paint6;
        paint6.setColor(0);
        this.eraserPaint.setStyle(Paint.Style.FILL);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setDimVisibility(boolean visible) {
        this.dimVisibile = visible;
    }

    public void setFrameVisibility(boolean visible) {
        this.frameVisible = visible;
    }

    public void setBottomPadding(float value) {
        this.bottomPadding = value;
    }

    public Interpolator getInterpolator() {
        return this.interpolator;
    }

    public void setListener(AreaViewListener l) {
        this.listener = l;
    }

    public void setBitmap(Bitmap bitmap, boolean sideward, boolean fform) {
        float aspectRatio;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.freeform = fform;
            if (sideward) {
                aspectRatio = ((float) bitmap.getHeight()) / ((float) bitmap.getWidth());
            } else {
                aspectRatio = ((float) bitmap.getWidth()) / ((float) bitmap.getHeight());
            }
            if (!this.freeform) {
                aspectRatio = 1.0f;
                this.lockAspectRatio = 1.0f;
            }
            setActualRect(aspectRatio);
        }
    }

    public void setFreeform(boolean fform) {
        this.freeform = fform;
    }

    public void setActualRect(float aspectRatio) {
        calculateRect(this.actualRect, aspectRatio);
        updateTouchAreas();
        invalidate();
    }

    public void setActualRect(RectF rect) {
        this.actualRect.set(rect);
        updateTouchAreas();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int lineThickness;
        int height;
        int handleSize;
        int lineThickness2;
        int height2;
        int handleSize2;
        if (this.freeform) {
            int lineThickness3 = AndroidUtilities.dp(2.0f);
            int handleSize3 = AndroidUtilities.dp(16.0f);
            int handleThickness = AndroidUtilities.dp(3.0f);
            int originX = ((int) this.actualRect.left) - lineThickness3;
            int originY = ((int) this.actualRect.top) - lineThickness3;
            int width = ((int) (this.actualRect.right - this.actualRect.left)) + (lineThickness3 * 2);
            int height3 = ((int) (this.actualRect.bottom - this.actualRect.top)) + (lineThickness3 * 2);
            if (this.dimVisibile) {
                canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) (originY + lineThickness3), this.dimPaint);
                Canvas canvas2 = canvas;
                canvas2.drawRect(0.0f, (float) (originY + lineThickness3), (float) (originX + lineThickness3), (float) ((originY + height3) - lineThickness3), this.dimPaint);
                canvas.drawRect((float) ((originX + width) - lineThickness3), (float) (originY + lineThickness3), (float) getWidth(), (float) ((originY + height3) - lineThickness3), this.dimPaint);
                canvas2.drawRect(0.0f, (float) ((originY + height3) - lineThickness3), (float) getWidth(), (float) getHeight(), this.dimPaint);
            }
            if (this.frameVisible) {
                int inset = handleThickness - lineThickness3;
                int gridWidth = width - (handleThickness * 2);
                int gridHeight = height3 - (handleThickness * 2);
                GridType type = this.gridType;
                if (type == GridType.NONE && this.gridProgress > 0.0f) {
                    type = this.previousGridType;
                }
                this.shadowPaint.setAlpha((int) (this.gridProgress * 26.0f));
                this.linePaint.setAlpha((int) (this.gridProgress * 178.0f));
                int i = 0;
                while (true) {
                    char c = 3;
                    if (i < 3) {
                        if (type == GridType.MINOR) {
                            int j = 1;
                            while (j < 4) {
                                if (i == 2 && j == c) {
                                    lineThickness2 = lineThickness3;
                                    handleSize2 = handleSize3;
                                    height2 = height3;
                                } else {
                                    handleSize2 = handleSize3;
                                    height2 = height3;
                                    lineThickness2 = lineThickness3;
                                    canvas.drawLine((float) (originX + handleThickness + (((gridWidth / 3) / 3) * j) + ((gridWidth / 3) * i)), (float) (originY + handleThickness), (float) (originX + handleThickness + (((gridWidth / 3) / 3) * j) + ((gridWidth / 3) * i)), (float) (originY + handleThickness + gridHeight), this.shadowPaint);
                                    canvas.drawLine((float) (originX + handleThickness + (((gridWidth / 3) / 3) * j) + ((gridWidth / 3) * i)), (float) (originY + handleThickness), (float) (originX + handleThickness + (((gridWidth / 3) / 3) * j) + ((gridWidth / 3) * i)), (float) (originY + handleThickness + gridHeight), this.linePaint);
                                    canvas.drawLine((float) (originX + handleThickness), (float) (originY + handleThickness + (((gridHeight / 3) / 3) * j) + ((gridHeight / 3) * i)), (float) (originX + handleThickness + gridWidth), (float) (originY + handleThickness + (((gridHeight / 3) / 3) * j) + ((gridHeight / 3) * i)), this.shadowPaint);
                                    canvas.drawLine((float) (originX + handleThickness), (float) (originY + handleThickness + (((gridHeight / 3) / 3) * j) + ((gridHeight / 3) * i)), (float) (originX + handleThickness + gridWidth), (float) (originY + handleThickness + (((gridHeight / 3) / 3) * j) + ((gridHeight / 3) * i)), this.linePaint);
                                }
                                j++;
                                handleSize3 = handleSize2;
                                height3 = height2;
                                lineThickness3 = lineThickness2;
                                c = 3;
                            }
                            lineThickness = lineThickness3;
                            handleSize = handleSize3;
                            height = height3;
                        } else {
                            lineThickness = lineThickness3;
                            handleSize = handleSize3;
                            height = height3;
                            if (type == GridType.MAJOR && i > 0) {
                                Canvas canvas3 = canvas;
                                canvas3.drawLine((float) (originX + handleThickness + ((gridWidth / 3) * i)), (float) (originY + handleThickness), (float) (originX + handleThickness + ((gridWidth / 3) * i)), (float) (originY + handleThickness + gridHeight), this.shadowPaint);
                                canvas3.drawLine((float) (originX + handleThickness + ((gridWidth / 3) * i)), (float) (originY + handleThickness), (float) (originX + handleThickness + ((gridWidth / 3) * i)), (float) (originY + handleThickness + gridHeight), this.linePaint);
                                canvas3.drawLine((float) (originX + handleThickness), (float) (originY + handleThickness + ((gridHeight / 3) * i)), (float) (originX + handleThickness + gridWidth), (float) (originY + handleThickness + ((gridHeight / 3) * i)), this.shadowPaint);
                                canvas3.drawLine((float) (originX + handleThickness), (float) (originY + handleThickness + ((gridHeight / 3) * i)), (float) (originX + handleThickness + gridWidth), (float) (originY + handleThickness + ((gridHeight / 3) * i)), this.linePaint);
                            }
                        }
                        i++;
                        handleSize3 = handleSize;
                        height3 = height;
                        lineThickness3 = lineThickness;
                    } else {
                        int lineThickness4 = lineThickness3;
                        int handleSize4 = handleSize3;
                        int height4 = height3;
                        Canvas canvas4 = canvas;
                        canvas4.drawRect((float) (originX + inset), (float) (originY + inset), (float) ((originX + width) - inset), (float) (originY + inset + lineThickness4), this.framePaint);
                        canvas4.drawRect((float) (originX + inset), (float) (originY + inset), (float) (originX + inset + lineThickness4), (float) ((originY + height4) - inset), this.framePaint);
                        canvas4.drawRect((float) (originX + inset), (float) (((originY + height4) - inset) - lineThickness4), (float) ((originX + width) - inset), (float) ((originY + height4) - inset), this.framePaint);
                        canvas4.drawRect((float) (((originX + width) - inset) - lineThickness4), (float) (originY + inset), (float) ((originX + width) - inset), (float) ((originY + height4) - inset), this.framePaint);
                        canvas.drawRect((float) originX, (float) originY, (float) (originX + handleSize4), (float) (originY + handleThickness), this.handlePaint);
                        canvas.drawRect((float) originX, (float) originY, (float) (originX + handleThickness), (float) (originY + handleSize4), this.handlePaint);
                        Canvas canvas5 = canvas;
                        canvas5.drawRect((float) ((originX + width) - handleSize4), (float) originY, (float) (originX + width), (float) (originY + handleThickness), this.handlePaint);
                        canvas5.drawRect((float) ((originX + width) - handleThickness), (float) originY, (float) (originX + width), (float) (originY + handleSize4), this.handlePaint);
                        canvas.drawRect((float) originX, (float) ((originY + height4) - handleThickness), (float) (originX + handleSize4), (float) (originY + height4), this.handlePaint);
                        canvas.drawRect((float) originX, (float) ((originY + height4) - handleSize4), (float) (originX + handleThickness), (float) (originY + height4), this.handlePaint);
                        Canvas canvas6 = canvas;
                        canvas6.drawRect((float) ((originX + width) - handleSize4), (float) ((originY + height4) - handleThickness), (float) (originX + width), (float) (originY + height4), this.handlePaint);
                        canvas6.drawRect((float) ((originX + width) - handleThickness), (float) ((originY + height4) - handleSize4), (float) (originX + width), (float) (originY + height4), this.handlePaint);
                        Canvas canvas7 = canvas;
                        return;
                    }
                }
            }
        } else {
            Bitmap bitmap = this.circleBitmap;
            if (bitmap == null || ((float) bitmap.getWidth()) != this.actualRect.width()) {
                Bitmap bitmap2 = this.circleBitmap;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                    this.circleBitmap = null;
                }
                try {
                    this.circleBitmap = Bitmap.createBitmap((int) this.actualRect.width(), (int) this.actualRect.height(), Bitmap.Config.ARGB_8888);
                    Canvas circleCanvas = new Canvas(this.circleBitmap);
                    if (this.isFcCrop) {
                        circleCanvas.drawRect(0.0f, 0.0f, this.actualRect.width(), this.actualRect.height(), this.dimPaint);
                        circleCanvas.drawRect(0.0f, 0.0f, this.actualRect.height(), this.actualRect.width(), this.eraserPaint);
                    } else {
                        circleCanvas.drawRect(0.0f, 0.0f, this.actualRect.width(), this.actualRect.height(), this.dimPaint);
                        circleCanvas.drawCircle(this.actualRect.width() / 2.0f, this.actualRect.height() / 2.0f, this.actualRect.width() / 2.0f, this.eraserPaint);
                    }
                    circleCanvas.setBitmap((Bitmap) null);
                } catch (Throwable th) {
                }
            }
            canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) ((int) this.actualRect.top), this.dimPaint);
            canvas.drawRect(0.0f, (float) ((int) this.actualRect.top), (float) ((int) this.actualRect.left), (float) ((int) this.actualRect.bottom), this.dimPaint);
            canvas.drawRect((float) ((int) this.actualRect.right), (float) ((int) this.actualRect.top), (float) getWidth(), (float) ((int) this.actualRect.bottom), this.dimPaint);
            canvas.drawRect(0.0f, (float) ((int) this.actualRect.bottom), (float) getWidth(), (float) getHeight(), this.dimPaint);
            canvas.drawBitmap(this.circleBitmap, (float) ((int) this.actualRect.left), (float) ((int) this.actualRect.top), (Paint) null);
            if (this.isFcCrop) {
                int side = AndroidUtilities.dp(1.0f);
                int rectX = ((int) this.actualRect.left) - 15;
                int rectY = ((int) this.actualRect.top) - 15;
                int rectSizeX = (int) this.actualRect.width();
                int rectSizeY = (int) this.actualRect.height();
                Paint circlePaint = new Paint();
                circlePaint.setColor(-1);
                Paint paint = circlePaint;
                canvas.drawRect((float) (rectX + side), (float) (rectY + side), (float) (rectX + side + AndroidUtilities.dp(20.0f)), (float) ((side * 3) + rectY), paint);
                canvas.drawRect((float) (rectX + side), (float) (rectY + side), (float) ((side * 3) + rectX), (float) (rectY + side + AndroidUtilities.dp(20.0f)), paint);
                canvas.drawRect((float) ((((rectX + rectSizeX) - side) - AndroidUtilities.dp(20.0f)) + 30), (float) (rectY + side), (float) (((rectX + rectSizeX) - side) + 30), (float) ((side * 3) + rectY), paint);
                canvas.drawRect((float) (((rectX + rectSizeX) - (side * 3)) + 30), (float) (rectY + side), (float) (((rectX + rectSizeX) - side) + 30), (float) (rectY + side + AndroidUtilities.dp(20.0f)), paint);
                canvas.drawRect((float) (rectX + side), (float) ((((rectY + rectSizeY) - side) - AndroidUtilities.dp(20.0f)) + 30), (float) ((side * 3) + rectX), (float) (((rectY + rectSizeY) - side) + 30), paint);
                canvas.drawRect((float) (rectX + side), (float) (((rectY + rectSizeY) - (side * 3)) + 30), (float) (rectX + side + AndroidUtilities.dp(20.0f)), (float) (((rectY + rectSizeY) - side) + 30), paint);
                canvas.drawRect((float) ((((rectX + rectSizeX) - side) - AndroidUtilities.dp(20.0f)) + 30), (float) (((rectY + rectSizeY) - (side * 3)) + 30), (float) (((rectX + rectSizeX) - side) + 30), (float) (((rectY + rectSizeY) - side) + 30), paint);
                canvas.drawRect((float) (((rectX + rectSizeX) - (side * 3)) + 30), (float) ((((rectY + rectSizeY) - side) - AndroidUtilities.dp(20.0f)) + 30), (float) (((rectX + rectSizeX) - side) + 30), (float) (((rectY + rectSizeY) - side) + 30), paint);
            }
        }
    }

    private void updateTouchAreas() {
        int touchPadding = AndroidUtilities.dp(16.0f);
        this.topLeftCorner.set(this.actualRect.left - ((float) touchPadding), this.actualRect.top - ((float) touchPadding), this.actualRect.left + ((float) touchPadding), this.actualRect.top + ((float) touchPadding));
        this.topRightCorner.set(this.actualRect.right - ((float) touchPadding), this.actualRect.top - ((float) touchPadding), this.actualRect.right + ((float) touchPadding), this.actualRect.top + ((float) touchPadding));
        this.bottomLeftCorner.set(this.actualRect.left - ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding), this.actualRect.left + ((float) touchPadding), this.actualRect.bottom + ((float) touchPadding));
        this.bottomRightCorner.set(this.actualRect.right - ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding), this.actualRect.right + ((float) touchPadding), this.actualRect.bottom + ((float) touchPadding));
        this.topEdge.set(this.actualRect.left + ((float) touchPadding), this.actualRect.top - ((float) touchPadding), this.actualRect.right - ((float) touchPadding), this.actualRect.top + ((float) touchPadding));
        this.leftEdge.set(this.actualRect.left - ((float) touchPadding), this.actualRect.top + ((float) touchPadding), this.actualRect.left + ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding));
        this.rightEdge.set(this.actualRect.right - ((float) touchPadding), this.actualRect.top + ((float) touchPadding), this.actualRect.right + ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding));
        this.bottomEdge.set(this.actualRect.left + ((float) touchPadding), this.actualRect.bottom - ((float) touchPadding), this.actualRect.right - ((float) touchPadding), this.actualRect.bottom + ((float) touchPadding));
    }

    public float getLockAspectRatio() {
        return this.lockAspectRatio;
    }

    public void setLockedAspectRatio(float aspectRatio) {
        this.lockAspectRatio = aspectRatio;
    }

    public void setGridType(GridType type, boolean animated) {
        if (this.gridAnimator != null && (!animated || this.gridType != type)) {
            this.gridAnimator.cancel();
            this.gridAnimator = null;
        }
        GridType gridType2 = this.gridType;
        if (gridType2 != type) {
            this.previousGridType = gridType2;
            this.gridType = type;
            float targetProgress = type == GridType.NONE ? 0.0f : 1.0f;
            if (!animated) {
                this.gridProgress = targetProgress;
                invalidate();
                return;
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "gridProgress", new float[]{this.gridProgress, targetProgress});
            this.gridAnimator = ofFloat;
            ofFloat.setDuration(200);
            this.gridAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    Animator unused = CropAreaView.this.gridAnimator = null;
                }
            });
            if (type == GridType.NONE) {
                this.gridAnimator.setStartDelay(200);
            }
            this.gridAnimator.start();
        }
    }

    private void setGridProgress(float value) {
        this.gridProgress = value;
        invalidate();
    }

    private float getGridProgress() {
        return this.gridProgress;
    }

    public float getAspectRatio() {
        return (this.actualRect.right - this.actualRect.left) / (this.actualRect.bottom - this.actualRect.top);
    }

    public void fill(final RectF targetRect, Animator scaleAnimator, boolean animated) {
        if (animated) {
            Animator animator2 = this.animator;
            if (animator2 != null) {
                animator2.cancel();
                this.animator = null;
            }
            AnimatorSet set = new AnimatorSet();
            this.animator = set;
            set.setDuration(300);
            Animator[] animators = new Animator[5];
            animators[0] = ObjectAnimator.ofFloat(this, "cropLeft", new float[]{targetRect.left});
            animators[0].setInterpolator(this.interpolator);
            animators[1] = ObjectAnimator.ofFloat(this, "cropTop", new float[]{targetRect.top});
            animators[1].setInterpolator(this.interpolator);
            animators[2] = ObjectAnimator.ofFloat(this, "cropRight", new float[]{targetRect.right});
            animators[2].setInterpolator(this.interpolator);
            animators[3] = ObjectAnimator.ofFloat(this, "cropBottom", new float[]{targetRect.bottom});
            animators[3].setInterpolator(this.interpolator);
            animators[4] = scaleAnimator;
            animators[4].setInterpolator(this.interpolator);
            set.playTogether(animators);
            set.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    CropAreaView.this.setActualRect(targetRect);
                    Animator unused = CropAreaView.this.animator = null;
                }
            });
            set.start();
            return;
        }
        setActualRect(targetRect);
    }

    public void resetAnimator() {
        Animator animator2 = this.animator;
        if (animator2 != null) {
            animator2.cancel();
            this.animator = null;
        }
    }

    private void setCropLeft(float value) {
        this.actualRect.left = value;
        invalidate();
    }

    public float getCropLeft() {
        return this.actualRect.left;
    }

    private void setCropTop(float value) {
        this.actualRect.top = value;
        invalidate();
    }

    public float getCropTop() {
        return this.actualRect.top;
    }

    private void setCropRight(float value) {
        this.actualRect.right = value;
        invalidate();
    }

    public float getCropRight() {
        return this.actualRect.right;
    }

    private void setCropBottom(float value) {
        this.actualRect.bottom = value;
        invalidate();
    }

    public float getCropBottom() {
        return this.actualRect.bottom;
    }

    public float getCropCenterX() {
        return this.actualRect.left + ((this.actualRect.right - this.actualRect.left) / 2.0f);
    }

    public float getCropCenterY() {
        return this.actualRect.top + ((this.actualRect.bottom - this.actualRect.top) / 2.0f);
    }

    public float getCropWidth() {
        return this.actualRect.right - this.actualRect.left;
    }

    public float getCropHeight() {
        return this.actualRect.bottom - this.actualRect.top;
    }

    public RectF getTargetRectToFill() {
        RectF rect = new RectF();
        calculateRect(rect, getAspectRatio());
        return rect;
    }

    public void calculateRect(RectF rect, float cropAspectRatio) {
        float right;
        float top;
        float left;
        float bottom;
        float statusBarHeight = (float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        float measuredHeight = (((float) getMeasuredHeight()) - this.bottomPadding) - statusBarHeight;
        float aspectRatio = ((float) getMeasuredWidth()) / measuredHeight;
        float minSide = Math.min((float) getMeasuredWidth(), measuredHeight) - (this.sidePadding * 2.0f);
        float f = this.sidePadding;
        float width = ((float) getMeasuredWidth()) - (f * 2.0f);
        float height = measuredHeight - (f * 2.0f);
        float centerX = ((float) getMeasuredWidth()) / 2.0f;
        float centerY = (measuredHeight / 2.0f) + statusBarHeight;
        if (((double) Math.abs(1.0f - cropAspectRatio)) < 1.0E-4d) {
            left = centerX - (minSide / 2.0f);
            top = centerY - (minSide / 2.0f);
            right = (minSide / 2.0f) + centerX;
            bottom = (minSide / 2.0f) + centerY;
        } else if (cropAspectRatio > aspectRatio) {
            left = centerX - (width / 2.0f);
            top = centerY - ((width / cropAspectRatio) / 2.0f);
            right = (width / 2.0f) + centerX;
            bottom = centerY + ((width / cropAspectRatio) / 2.0f);
        } else {
            left = centerX - ((height * cropAspectRatio) / 2.0f);
            top = centerY - (height / 2.0f);
            right = ((height * cropAspectRatio) / 2.0f) + centerX;
            bottom = (height / 2.0f) + centerY;
        }
        rect.set(left, top, right, bottom);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) (event.getX() - ((ViewGroup) getParent()).getX());
        int y = (int) (event.getY() - ((ViewGroup) getParent()).getY());
        float statusBarHeight = (float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        int action = event.getActionMasked();
        if (action == 0) {
            if (this.freeform) {
                if (this.topLeftCorner.contains((float) x, (float) y)) {
                    this.activeControl = Control.TOP_LEFT;
                } else if (this.topRightCorner.contains((float) x, (float) y)) {
                    this.activeControl = Control.TOP_RIGHT;
                } else if (this.bottomLeftCorner.contains((float) x, (float) y)) {
                    this.activeControl = Control.BOTTOM_LEFT;
                } else if (this.bottomRightCorner.contains((float) x, (float) y)) {
                    this.activeControl = Control.BOTTOM_RIGHT;
                } else if (this.leftEdge.contains((float) x, (float) y)) {
                    this.activeControl = Control.LEFT;
                } else if (this.topEdge.contains((float) x, (float) y)) {
                    this.activeControl = Control.TOP;
                } else if (this.rightEdge.contains((float) x, (float) y)) {
                    this.activeControl = Control.RIGHT;
                } else if (this.bottomEdge.contains((float) x, (float) y)) {
                    this.activeControl = Control.BOTTOM;
                } else {
                    this.activeControl = Control.NONE;
                    return false;
                }
                this.previousX = x;
                this.previousY = y;
                setGridType(GridType.MAJOR, false);
                this.isDragging = true;
                AreaViewListener areaViewListener = this.listener;
                if (areaViewListener != null) {
                    areaViewListener.onAreaChangeBegan();
                }
                return true;
            }
            this.activeControl = Control.NONE;
            return false;
        } else if (action == 1 || action == 3) {
            this.isDragging = false;
            if (this.activeControl == Control.NONE) {
                return false;
            }
            this.activeControl = Control.NONE;
            AreaViewListener areaViewListener2 = this.listener;
            if (areaViewListener2 != null) {
                areaViewListener2.onAreaChangeEnded();
            }
            return true;
        } else if (action != 2 || this.activeControl == Control.NONE) {
            return false;
        } else {
            this.tempRect.set(this.actualRect);
            float translationX = (float) (x - this.previousX);
            float translationY = (float) (y - this.previousY);
            this.previousX = x;
            this.previousY = y;
            switch (AnonymousClass3.$SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control[this.activeControl.ordinal()]) {
                case 1:
                    this.tempRect.left += translationX;
                    this.tempRect.top += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        float w = this.tempRect.width();
                        float h = this.tempRect.height();
                        if (Math.abs(translationX) > Math.abs(translationY)) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        this.tempRect.left -= this.tempRect.width() - w;
                        this.tempRect.top -= this.tempRect.width() - h;
                        break;
                    }
                    break;
                case 2:
                    this.tempRect.right += translationX;
                    this.tempRect.top += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        float h2 = this.tempRect.height();
                        if (Math.abs(translationX) > Math.abs(translationY)) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        this.tempRect.top -= this.tempRect.width() - h2;
                        break;
                    }
                    break;
                case 3:
                    this.tempRect.left += translationX;
                    this.tempRect.bottom += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        float w2 = this.tempRect.width();
                        if (Math.abs(translationX) > Math.abs(translationY)) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        this.tempRect.left -= this.tempRect.width() - w2;
                        break;
                    }
                    break;
                case 4:
                    this.tempRect.right += translationX;
                    this.tempRect.bottom += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        if (Math.abs(translationX) <= Math.abs(translationY)) {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                            break;
                        } else {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                            break;
                        }
                    }
                    break;
                case 5:
                    this.tempRect.top += translationY;
                    float f = this.lockAspectRatio;
                    if (f > 0.0f) {
                        constrainRectByHeight(this.tempRect, f);
                        break;
                    }
                    break;
                case 6:
                    this.tempRect.left += translationX;
                    float f2 = this.lockAspectRatio;
                    if (f2 > 0.0f) {
                        constrainRectByWidth(this.tempRect, f2);
                        break;
                    }
                    break;
                case 7:
                    this.tempRect.right += translationX;
                    float f3 = this.lockAspectRatio;
                    if (f3 > 0.0f) {
                        constrainRectByWidth(this.tempRect, f3);
                        break;
                    }
                    break;
                case 8:
                    this.tempRect.bottom += translationY;
                    float f4 = this.lockAspectRatio;
                    if (f4 > 0.0f) {
                        constrainRectByHeight(this.tempRect, f4);
                        break;
                    }
                    break;
            }
            if (this.tempRect.left < this.sidePadding) {
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF = this.tempRect;
                    rectF.bottom = rectF.top + ((this.tempRect.right - this.sidePadding) / this.lockAspectRatio);
                }
                this.tempRect.left = this.sidePadding;
            } else if (this.tempRect.right > ((float) getWidth()) - this.sidePadding) {
                this.tempRect.right = ((float) getWidth()) - this.sidePadding;
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF2 = this.tempRect;
                    rectF2.bottom = rectF2.top + (this.tempRect.width() / this.lockAspectRatio);
                }
            }
            float f5 = this.sidePadding;
            float topPadding = statusBarHeight + f5;
            float finalBottomPadidng = this.bottomPadding + f5;
            if (this.tempRect.top < topPadding) {
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF3 = this.tempRect;
                    rectF3.right = rectF3.left + ((this.tempRect.bottom - topPadding) * this.lockAspectRatio);
                }
                this.tempRect.top = topPadding;
            } else if (this.tempRect.bottom > ((float) getHeight()) - finalBottomPadidng) {
                this.tempRect.bottom = ((float) getHeight()) - finalBottomPadidng;
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF4 = this.tempRect;
                    rectF4.right = rectF4.left + (this.tempRect.height() * this.lockAspectRatio);
                }
            }
            if (this.tempRect.width() < this.minWidth) {
                RectF rectF5 = this.tempRect;
                rectF5.right = rectF5.left + this.minWidth;
            }
            if (this.tempRect.height() < this.minWidth) {
                RectF rectF6 = this.tempRect;
                rectF6.bottom = rectF6.top + this.minWidth;
            }
            float f6 = this.lockAspectRatio;
            if (f6 > 0.0f) {
                if (f6 < 1.0f) {
                    if (this.tempRect.width() <= this.minWidth) {
                        RectF rectF7 = this.tempRect;
                        rectF7.right = rectF7.left + this.minWidth;
                        RectF rectF8 = this.tempRect;
                        rectF8.bottom = rectF8.top + (this.tempRect.width() / this.lockAspectRatio);
                    }
                } else if (this.tempRect.height() <= this.minWidth) {
                    RectF rectF9 = this.tempRect;
                    rectF9.bottom = rectF9.top + this.minWidth;
                    RectF rectF10 = this.tempRect;
                    rectF10.right = rectF10.left + (this.tempRect.height() * this.lockAspectRatio);
                }
            }
            setActualRect(this.tempRect);
            AreaViewListener areaViewListener3 = this.listener;
            if (areaViewListener3 != null) {
                areaViewListener3.onAreaChange();
            }
            return true;
        }
    }

    /* renamed from: im.bclpbkiauv.ui.components.crop.CropAreaView$3  reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control;

        static {
            int[] iArr = new int[Control.values().length];
            $SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control = iArr;
            try {
                iArr[Control.TOP_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control[Control.TOP_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control[Control.BOTTOM_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control[Control.BOTTOM_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control[Control.TOP.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control[Control.LEFT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control[Control.RIGHT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$crop$CropAreaView$Control[Control.BOTTOM.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    private void constrainRectByWidth(RectF rect, float aspectRatio) {
        float w = rect.width();
        rect.right = rect.left + w;
        rect.bottom = rect.top + (w / aspectRatio);
    }

    private void constrainRectByHeight(RectF rect, float aspectRatio) {
        float h = rect.height();
        rect.right = rect.left + (h * aspectRatio);
        rect.bottom = rect.top + h;
    }

    public void getCropRect(RectF rect) {
        rect.set(this.actualRect);
    }
}
