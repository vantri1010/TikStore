package im.bclpbkiauv.ui.components.paint.views;

import android.view.MotionEvent;

public class RotationGestureDetector {
    private float angle;
    private float fX;
    private float fY;
    private OnRotationGestureListener mListener;
    private float sX;
    private float sY;
    private float startAngle;

    public interface OnRotationGestureListener {
        void onRotation(RotationGestureDetector rotationGestureDetector);

        void onRotationBegin(RotationGestureDetector rotationGestureDetector);

        void onRotationEnd(RotationGestureDetector rotationGestureDetector);
    }

    public float getAngle() {
        return this.angle;
    }

    public float getStartAngle() {
        return this.startAngle;
    }

    public RotationGestureDetector(OnRotationGestureListener listener) {
        this.mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() != 2) {
            return false;
        }
        int actionMasked = event.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    float nsX = event.getX(0);
                    float nsY = event.getY(0);
                    this.angle = angleBetweenLines(this.fX, this.fY, this.sX, this.sY, event.getX(1), event.getY(1), nsX, nsY);
                    if (this.mListener != null) {
                        if (Float.isNaN(this.startAngle)) {
                            this.startAngle = this.angle;
                            this.mListener.onRotationBegin(this);
                        } else {
                            this.mListener.onRotation(this);
                        }
                    }
                } else if (actionMasked != 3) {
                    if (actionMasked != 5) {
                        if (actionMasked == 6) {
                            this.startAngle = Float.NaN;
                            OnRotationGestureListener onRotationGestureListener = this.mListener;
                            if (onRotationGestureListener != null) {
                                onRotationGestureListener.onRotationEnd(this);
                            }
                        }
                    }
                }
                return true;
            }
            this.startAngle = Float.NaN;
            return true;
        }
        this.sX = event.getX(0);
        this.sY = event.getY(0);
        this.fX = event.getX(1);
        this.fY = event.getY(1);
        return true;
    }

    private float angleBetweenLines(float fX2, float fY2, float sX2, float sY2, float nfX, float nfY, float nsX, float nsY) {
        float angle2 = ((float) Math.toDegrees((double) (((float) Math.atan2((double) (fY2 - sY2), (double) (fX2 - sX2))) - ((float) Math.atan2((double) (nfY - nsY), (double) (nfX - nsX)))))) % 360.0f;
        if (angle2 < -180.0f) {
            angle2 += 360.0f;
        }
        if (angle2 > 180.0f) {
            return angle2 - 360.0f;
        }
        return angle2;
    }
}
