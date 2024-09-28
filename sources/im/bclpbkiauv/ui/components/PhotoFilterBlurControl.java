package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class PhotoFilterBlurControl extends FrameLayout {
    private static final float BlurInsetProximity = ((float) AndroidUtilities.dp(20.0f));
    private static final float BlurMinimumDifference = 0.02f;
    private static final float BlurMinimumFalloff = 0.1f;
    private static final float BlurViewCenterInset = ((float) AndroidUtilities.dp(30.0f));
    private static final float BlurViewRadiusInset = ((float) AndroidUtilities.dp(30.0f));
    private final int GestureStateBegan = 1;
    private final int GestureStateCancelled = 4;
    private final int GestureStateChanged = 2;
    private final int GestureStateEnded = 3;
    private final int GestureStateFailed = 5;
    private BlurViewActiveControl activeControl;
    private Size actualAreaSize = new Size();
    private float angle;
    private Paint arcPaint = new Paint(1);
    private RectF arcRect = new RectF();
    private Point centerPoint = new Point(0.5f, 0.5f);
    private boolean checkForMoving = true;
    private boolean checkForZooming;
    private PhotoFilterLinearBlurControlDelegate delegate;
    private float falloff = 0.15f;
    private boolean isMoving;
    private boolean isZooming;
    private Paint paint = new Paint(1);
    private float pointerScale = 1.0f;
    private float pointerStartX;
    private float pointerStartY;
    private float size = 0.35f;
    private Point startCenterPoint = new Point();
    private float startDistance;
    private float startPointerDistance;
    private float startRadius;
    private int type;

    private enum BlurViewActiveControl {
        BlurViewActiveControlNone,
        BlurViewActiveControlCenter,
        BlurViewActiveControlInnerRadius,
        BlurViewActiveControlOuterRadius,
        BlurViewActiveControlWholeArea,
        BlurViewActiveControlRotation
    }

    public interface PhotoFilterLinearBlurControlDelegate {
        void valueChanged(Point point, float f, float f2, float f3);
    }

    public PhotoFilterBlurControl(Context context) {
        super(context);
        setWillNotDraw(false);
        this.paint.setColor(-1);
        this.arcPaint.setColor(-1);
        this.arcPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.arcPaint.setStyle(Paint.Style.STROKE);
    }

    public void setType(int blurType) {
        this.type = blurType;
        invalidate();
    }

    public void setDelegate(PhotoFilterLinearBlurControlDelegate delegate2) {
        this.delegate = delegate2;
    }

    private float getDistance(MotionEvent event) {
        if (event.getPointerCount() != 2) {
            return 0.0f;
        }
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        float x2 = event.getX(1);
        float y2 = event.getY(1);
        return (float) Math.sqrt((double) (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))));
    }

    private float degreesToRadians(float degrees) {
        return (3.1415927f * degrees) / 180.0f;
    }

    public boolean onTouchEvent(MotionEvent event) {
        MotionEvent motionEvent = event;
        int action = event.getActionMasked();
        if (action != 0) {
            if (action != 1) {
                if (action != 2) {
                    if (action != 3) {
                        if (action != 5) {
                            if (action != 6) {
                                int i = action;
                                return true;
                            }
                        }
                    }
                } else if (this.isMoving) {
                    handlePan(2, motionEvent);
                    int i2 = action;
                    return true;
                } else if (this.isZooming) {
                    handlePinch(2, motionEvent);
                    int i3 = action;
                    return true;
                } else {
                    int i4 = action;
                    return true;
                }
            }
            if (this.isMoving) {
                handlePan(3, motionEvent);
                this.isMoving = false;
            } else if (this.isZooming) {
                handlePinch(3, motionEvent);
                this.isZooming = false;
            }
            this.checkForMoving = true;
            this.checkForZooming = true;
            int i5 = action;
            return true;
        }
        if (event.getPointerCount() != 1) {
            if (this.isMoving != 0) {
                handlePan(3, motionEvent);
                this.checkForMoving = true;
                this.isMoving = false;
            }
            if (event.getPointerCount() != 2) {
                handlePinch(3, motionEvent);
                this.checkForZooming = true;
                this.isZooming = false;
                return true;
            } else if (!this.checkForZooming || this.isZooming) {
                return true;
            } else {
                handlePinch(1, motionEvent);
                this.isZooming = true;
                return true;
            }
        } else if (!this.checkForMoving || this.isMoving) {
            return true;
        } else {
            float locationX = event.getX();
            float locationY = event.getY();
            Point centerPoint2 = getActualCenterPoint();
            Point delta = new Point(locationX - centerPoint2.x, locationY - centerPoint2.y);
            float radialDistance = (float) Math.sqrt((double) ((delta.x * delta.x) + (delta.y * delta.y)));
            float innerRadius = getActualInnerRadius();
            float outerRadius = getActualOuterRadius();
            boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
            float outerRadiusInnerInset = 0.0f;
            float innerRadiusOuterInset = close ? 0.0f : BlurViewRadiusInset;
            if (!close) {
                outerRadiusInnerInset = BlurViewRadiusInset;
            }
            int i6 = this.type;
            if (i6 == 0) {
                int i7 = action;
                float f = locationX;
                Point point = centerPoint2;
                Point point2 = delta;
                float distance = (float) Math.abs((((double) delta.x) * Math.cos(((double) degreesToRadians(this.angle)) + 1.5707963267948966d)) + (((double) delta.y) * Math.sin(((double) degreesToRadians(this.angle)) + 1.5707963267948966d)));
                if (radialDistance < BlurViewCenterInset) {
                    this.isMoving = true;
                } else if (distance > innerRadius - BlurViewRadiusInset && distance < innerRadius + innerRadiusOuterInset) {
                    this.isMoving = true;
                } else if (distance <= outerRadius - outerRadiusInnerInset || distance >= BlurViewRadiusInset + outerRadius) {
                    float f2 = BlurViewRadiusInset;
                    if (distance <= innerRadius - f2 || distance >= f2 + outerRadius) {
                        this.isMoving = true;
                    }
                } else {
                    this.isMoving = true;
                }
            } else {
                float f3 = locationX;
                Point point3 = centerPoint2;
                Point point4 = delta;
                if (i6 == 1) {
                    if (radialDistance < BlurViewCenterInset) {
                        this.isMoving = true;
                    } else if (radialDistance > innerRadius - BlurViewRadiusInset && radialDistance < innerRadius + innerRadiusOuterInset) {
                        this.isMoving = true;
                    } else if (radialDistance > outerRadius - outerRadiusInnerInset && radialDistance < BlurViewRadiusInset + outerRadius) {
                        this.isMoving = true;
                    }
                }
            }
            this.checkForMoving = false;
            if (this.isMoving) {
                handlePan(1, motionEvent);
            }
            return true;
        }
    }

    private void handlePan(int state, MotionEvent event) {
        int i = state;
        float locationX = event.getX();
        float locationY = event.getY();
        Point actualCenterPoint = getActualCenterPoint();
        Point delta = new Point(locationX - actualCenterPoint.x, locationY - actualCenterPoint.y);
        float radialDistance = (float) Math.sqrt((double) ((delta.x * delta.x) + (delta.y * delta.y)));
        float shorterSide = this.actualAreaSize.width > this.actualAreaSize.height ? this.actualAreaSize.height : this.actualAreaSize.width;
        float innerRadius = this.falloff * shorterSide;
        float outerRadius = this.size * shorterSide;
        float distance = (float) Math.abs((((double) delta.x) * Math.cos(((double) degreesToRadians(this.angle)) + 1.5707963267948966d)) + (((double) delta.y) * Math.sin(((double) degreesToRadians(this.angle)) + 1.5707963267948966d)));
        if (i == 1) {
            this.pointerStartX = event.getX();
            this.pointerStartY = event.getY();
            boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
            float innerRadiusOuterInset = close ? 0.0f : BlurViewRadiusInset;
            float outerRadiusInnerInset = close ? 0.0f : BlurViewRadiusInset;
            int i2 = this.type;
            if (i2 == 0) {
                if (radialDistance < BlurViewCenterInset) {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                    this.startCenterPoint = actualCenterPoint;
                } else if (distance > innerRadius - BlurViewRadiusInset && distance < innerRadius + innerRadiusOuterInset) {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                    this.startDistance = distance;
                    this.startRadius = innerRadius;
                } else if (distance <= outerRadius - outerRadiusInnerInset || distance >= BlurViewRadiusInset + outerRadius) {
                    float f = BlurViewRadiusInset;
                    if (distance <= innerRadius - f || distance >= f + outerRadius) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlRotation;
                    }
                } else {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                    this.startDistance = distance;
                    this.startRadius = outerRadius;
                }
            } else if (i2 == 1) {
                if (radialDistance < BlurViewCenterInset) {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                    this.startCenterPoint = actualCenterPoint;
                } else if (radialDistance > innerRadius - BlurViewRadiusInset && radialDistance < innerRadius + innerRadiusOuterInset) {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                    this.startDistance = radialDistance;
                    this.startRadius = innerRadius;
                } else if (radialDistance > outerRadius - outerRadiusInnerInset && radialDistance < BlurViewRadiusInset + outerRadius) {
                    this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                    this.startDistance = radialDistance;
                    this.startRadius = outerRadius;
                }
            }
            setSelected(true, true);
        } else if (i == 2) {
            int i3 = this.type;
            if (i3 == 0) {
                int i4 = AnonymousClass1.$SwitchMap$im$bclpbkiauv$ui$components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()];
                if (i4 == 1) {
                    float translationX = locationX - this.pointerStartX;
                    Point point = delta;
                    Rect actualArea = new Rect((((float) getWidth()) - this.actualAreaSize.width) / 2.0f, (((float) getHeight()) - this.actualAreaSize.height) / 2.0f, this.actualAreaSize.width, this.actualAreaSize.height);
                    float f2 = translationX;
                    Point newPoint = new Point(Math.max(actualArea.x, Math.min(actualArea.x + actualArea.width, this.startCenterPoint.x + translationX)), Math.max(actualArea.y, Math.min(actualArea.y + actualArea.height, this.startCenterPoint.y + (locationY - this.pointerStartY))));
                    Point point2 = newPoint;
                    this.centerPoint = new Point((newPoint.x - actualArea.x) / this.actualAreaSize.width, ((newPoint.y - actualArea.y) + ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) / this.actualAreaSize.width);
                } else if (i4 == 2) {
                    this.falloff = Math.min(Math.max(0.1f, (this.startRadius + (distance - this.startDistance)) / shorterSide), this.size - BlurMinimumDifference);
                    Point point3 = delta;
                } else if (i4 == 3) {
                    this.size = Math.max(this.falloff + BlurMinimumDifference, (this.startRadius + (distance - this.startDistance)) / shorterSide);
                    Point point4 = delta;
                } else if (i4 != 4) {
                    Point point5 = delta;
                } else {
                    float translationX2 = locationX - this.pointerStartX;
                    float translationY = locationY - this.pointerStartY;
                    boolean clockwise = false;
                    boolean right = locationX > actualCenterPoint.x;
                    boolean bottom = locationY > actualCenterPoint.y;
                    if (right || bottom) {
                        if (!right || bottom) {
                            if (!right || !bottom) {
                                if (Math.abs(translationY) > Math.abs(translationX2)) {
                                    if (translationY < 0.0f) {
                                        clockwise = true;
                                    }
                                } else if (translationX2 < 0.0f) {
                                    clockwise = true;
                                }
                            } else if (Math.abs(translationY) > Math.abs(translationX2)) {
                                if (translationY > 0.0f) {
                                    clockwise = true;
                                }
                            } else if (translationX2 < 0.0f) {
                                clockwise = true;
                            }
                        } else if (Math.abs(translationY) > Math.abs(translationX2)) {
                            if (translationY > 0.0f) {
                                clockwise = true;
                            }
                        } else if (translationX2 > 0.0f) {
                            clockwise = true;
                        }
                    } else if (Math.abs(translationY) > Math.abs(translationX2)) {
                        if (translationY < 0.0f) {
                            clockwise = true;
                        }
                    } else if (translationX2 > 0.0f) {
                        clockwise = true;
                    }
                    float f3 = translationX2;
                    float f4 = translationY;
                    this.angle += ((((float) ((clockwise * true) - 1)) * ((float) Math.sqrt((double) ((translationX2 * translationX2) + (translationY * translationY))))) / 3.1415927f) / 1.15f;
                    this.pointerStartX = locationX;
                    this.pointerStartY = locationY;
                    Point point6 = delta;
                }
            } else {
                if (i3 == 1) {
                    int i5 = AnonymousClass1.$SwitchMap$im$bclpbkiauv$ui$components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()];
                    if (i5 == 1) {
                        float translationX3 = locationX - this.pointerStartX;
                        Rect actualArea2 = new Rect((((float) getWidth()) - this.actualAreaSize.width) / 2.0f, (((float) getHeight()) - this.actualAreaSize.height) / 2.0f, this.actualAreaSize.width, this.actualAreaSize.height);
                        float f5 = translationX3;
                        Point newPoint2 = new Point(Math.max(actualArea2.x, Math.min(actualArea2.x + actualArea2.width, this.startCenterPoint.x + translationX3)), Math.max(actualArea2.y, Math.min(actualArea2.y + actualArea2.height, this.startCenterPoint.y + (locationY - this.pointerStartY))));
                        Point point7 = newPoint2;
                        this.centerPoint = new Point((newPoint2.x - actualArea2.x) / this.actualAreaSize.width, ((newPoint2.y - actualArea2.y) + ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) / this.actualAreaSize.width);
                    } else if (i5 == 2) {
                        this.falloff = Math.min(Math.max(0.1f, (this.startRadius + (radialDistance - this.startDistance)) / shorterSide), this.size - BlurMinimumDifference);
                    } else if (i5 == 3) {
                        this.size = Math.max(this.falloff + BlurMinimumDifference, (this.startRadius + (radialDistance - this.startDistance)) / shorterSide);
                    }
                }
            }
            invalidate();
            PhotoFilterLinearBlurControlDelegate photoFilterLinearBlurControlDelegate = this.delegate;
            if (photoFilterLinearBlurControlDelegate != null) {
                photoFilterLinearBlurControlDelegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
            }
        } else if (i == 3 || i == 4 || i == 5) {
            this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
            setSelected(false, true);
            Point point8 = delta;
        } else {
            Point point9 = delta;
        }
    }

    /* renamed from: im.bclpbkiauv.ui.components.PhotoFilterBlurControl$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$im$bclpbkiauv$ui$components$PhotoFilterBlurControl$BlurViewActiveControl;

        static {
            int[] iArr = new int[BlurViewActiveControl.values().length];
            $SwitchMap$im$bclpbkiauv$ui$components$PhotoFilterBlurControl$BlurViewActiveControl = iArr;
            try {
                iArr[BlurViewActiveControl.BlurViewActiveControlCenter.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$PhotoFilterBlurControl$BlurViewActiveControl[BlurViewActiveControl.BlurViewActiveControlInnerRadius.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$PhotoFilterBlurControl$BlurViewActiveControl[BlurViewActiveControl.BlurViewActiveControlOuterRadius.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$components$PhotoFilterBlurControl$BlurViewActiveControl[BlurViewActiveControl.BlurViewActiveControlRotation.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private void handlePinch(int state, MotionEvent event) {
        if (state == 1) {
            this.startPointerDistance = getDistance(event);
            this.pointerScale = 1.0f;
            this.activeControl = BlurViewActiveControl.BlurViewActiveControlWholeArea;
            setSelected(true, true);
        } else if (state != 2) {
            if (state == 3 || state == 4 || state == 5) {
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
                return;
            }
            return;
        }
        float newDistance = getDistance(event);
        float f = this.pointerScale + (((newDistance - this.startPointerDistance) / AndroidUtilities.density) * 0.01f);
        this.pointerScale = f;
        float max = Math.max(0.1f, this.falloff * f);
        this.falloff = max;
        this.size = Math.max(max + BlurMinimumDifference, this.size * this.pointerScale);
        this.pointerScale = 1.0f;
        this.startPointerDistance = newDistance;
        invalidate();
        PhotoFilterLinearBlurControlDelegate photoFilterLinearBlurControlDelegate = this.delegate;
        if (photoFilterLinearBlurControlDelegate != null) {
            photoFilterLinearBlurControlDelegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
        }
    }

    private void setSelected(boolean selected, boolean animated) {
    }

    public void setActualAreaSize(float width, float height) {
        this.actualAreaSize.width = width;
        this.actualAreaSize.height = height;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        Point centerPoint2 = getActualCenterPoint();
        float innerRadius = getActualInnerRadius();
        float outerRadius = getActualOuterRadius();
        canvas2.translate(centerPoint2.x, centerPoint2.y);
        int i = this.type;
        if (i == 0) {
            canvas2.rotate(this.angle);
            float space = (float) AndroidUtilities.dp(6.0f);
            float length = (float) AndroidUtilities.dp(12.0f);
            float thickness = (float) AndroidUtilities.dp(1.5f);
            int i2 = 0;
            while (i2 < 30) {
                Canvas canvas3 = canvas;
                int i3 = i2;
                canvas3.drawRect((length + space) * ((float) i2), -innerRadius, (((float) i2) * (length + space)) + length, thickness - innerRadius, this.paint);
                canvas.drawRect(((((float) (-i3)) * (length + space)) - space) - length, -innerRadius, (((float) (-i3)) * (length + space)) - space, thickness - innerRadius, this.paint);
                canvas.drawRect((length + space) * ((float) i3), innerRadius, length + (((float) i3) * (length + space)), thickness + innerRadius, this.paint);
                canvas.drawRect(((((float) (-i3)) * (length + space)) - space) - length, innerRadius, (((float) (-i3)) * (length + space)) - space, thickness + innerRadius, this.paint);
                i2 = i3 + 1;
            }
            int i4 = i2;
            float length2 = (float) AndroidUtilities.dp(6.0f);
            for (int i5 = 0; i5 < 64; i5++) {
                canvas.drawRect((length2 + space) * ((float) i5), -outerRadius, length2 + (((float) i5) * (length2 + space)), thickness - outerRadius, this.paint);
                canvas.drawRect(((((float) (-i5)) * (length2 + space)) - space) - length2, -outerRadius, (((float) (-i5)) * (length2 + space)) - space, thickness - outerRadius, this.paint);
                canvas.drawRect((length2 + space) * ((float) i5), outerRadius, length2 + (((float) i5) * (length2 + space)), thickness + outerRadius, this.paint);
                canvas.drawRect(((((float) (-i5)) * (length2 + space)) - space) - length2, outerRadius, (((float) (-i5)) * (length2 + space)) - space, thickness + outerRadius, this.paint);
            }
        } else if (i == 1) {
            this.arcRect.set(-innerRadius, -innerRadius, innerRadius, innerRadius);
            for (int i6 = 0; i6 < 22; i6++) {
                canvas.drawArc(this.arcRect, (6.15f + 10.2f) * ((float) i6), 10.2f, false, this.arcPaint);
            }
            this.arcRect.set(-outerRadius, -outerRadius, outerRadius, outerRadius);
            for (int i7 = 0; i7 < 64; i7++) {
                canvas.drawArc(this.arcRect, (2.02f + 3.6f) * ((float) i7), 3.6f, false, this.arcPaint);
            }
        }
        canvas2.drawCircle(0.0f, 0.0f, (float) AndroidUtilities.dp(8.0f), this.paint);
    }

    private Point getActualCenterPoint() {
        return new Point(((((float) getWidth()) - this.actualAreaSize.width) / 2.0f) + (this.centerPoint.x * this.actualAreaSize.width), ((((float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) + ((((float) getHeight()) - this.actualAreaSize.height) / 2.0f)) - ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) + (this.centerPoint.y * this.actualAreaSize.width));
    }

    private float getActualInnerRadius() {
        return (this.actualAreaSize.width > this.actualAreaSize.height ? this.actualAreaSize.height : this.actualAreaSize.width) * this.falloff;
    }

    private float getActualOuterRadius() {
        return (this.actualAreaSize.width > this.actualAreaSize.height ? this.actualAreaSize.height : this.actualAreaSize.width) * this.size;
    }
}
