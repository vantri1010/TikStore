package im.bclpbkiauv.ui.utils;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewOutlineProvider;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class CornerUtil {
    private boolean clicpWithPadding;
    public int color;
    private float[] conrnerDii;
    private Paint paint;
    private Paint.Style paintStyle;
    private Path path;
    public float radius;
    public float radiusBottom;
    public float radiusLeft;
    public float radiusRight;
    public float radiusTop;
    public RectF rectF;
    public float strokeWidth = ((float) AndroidUtilities.dp(2.0f));
    private View view;

    public CornerUtil(View view2) {
        this.view = view2;
        this.path = new Path();
        this.paint = new Paint(1);
        this.paintStyle = Paint.Style.FILL;
        this.rectF = new RectF();
    }

    public void onDraw(Canvas canvas) {
        if (this.view != null && canvas != null) {
            if (this.radius > 0.0f || this.radiusLeft > 0.0f || this.radiusTop > 0.0f || this.radiusRight > 0.0f || this.radiusBottom > 0.0f) {
                this.rectF.left = (float) (this.clicpWithPadding ? this.view.getLeft() - this.view.getPaddingLeft() : this.view.getLeft());
                this.rectF.top = (float) (this.clicpWithPadding ? this.view.getTop() - this.view.getPaddingTop() : this.view.getTop());
                this.rectF.right = (float) (this.clicpWithPadding ? this.view.getRight() - this.view.getPaddingRight() : this.view.getRight());
                this.rectF.bottom = (float) (this.clicpWithPadding ? this.view.getBottom() - this.view.getPaddingBottom() : this.view.getBottom());
                if (this.radiusLeft > 0.0f || this.radiusTop > 0.0f || this.radiusRight > 0.0f || this.radiusBottom > 0.0f) {
                    if (this.conrnerDii == null) {
                        float f = this.radiusLeft;
                        float f2 = this.radiusTop;
                        float f3 = this.radiusRight;
                        float f4 = this.radiusBottom;
                        this.conrnerDii = new float[]{f, f, f2, f2, f3, f3, f4, f4};
                    }
                    this.path.addRoundRect(this.rectF, this.conrnerDii, Path.Direction.CCW);
                } else {
                    Path path2 = this.path;
                    RectF rectF2 = this.rectF;
                    float f5 = this.radius;
                    path2.addRoundRect(rectF2, f5, f5, Path.Direction.CCW);
                }
                reset();
                canvas.clipPath(this.path);
            }
        }
    }

    private void reset() {
        if (this.color != this.paint.getColor() || this.strokeWidth != this.paint.getStrokeWidth() || this.paintStyle != this.paint.getStyle()) {
            this.paint.setColor(this.color);
            this.paint.setStrokeWidth(this.strokeWidth);
            this.paint.setStyle(this.paintStyle);
        }
    }

    public static void clipViewCircle(View view2) {
        view2.setClipToOutline(true);
        view2.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
    }

    public static void clipViewCornerByDp(View view2, final int pixel) {
        view2.setClipToOutline(true);
        view2.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) pixel);
            }
        });
    }
}
