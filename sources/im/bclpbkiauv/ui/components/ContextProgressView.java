package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class ContextProgressView extends View {
    private RectF cicleRect = new RectF();
    private int currentColorType;
    private String innerKey;
    private Paint innerPaint = new Paint(1);
    private long lastUpdateTime;
    private String outerKey;
    private Paint outerPaint = new Paint(1);
    private int radOffset = 0;

    public ContextProgressView(Context context, int colorType) {
        super(context);
        this.innerPaint.setStyle(Paint.Style.STROKE);
        this.innerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStyle(Paint.Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStrokeCap(Paint.Cap.ROUND);
        if (colorType == 0) {
            this.innerKey = Theme.key_contextProgressInner1;
            this.outerKey = Theme.key_contextProgressOuter1;
        } else if (colorType == 1) {
            this.innerKey = Theme.key_contextProgressInner2;
            this.outerKey = Theme.key_contextProgressOuter2;
        } else if (colorType == 2) {
            this.innerKey = Theme.key_contextProgressInner3;
            this.outerKey = Theme.key_contextProgressOuter3;
        } else if (colorType == 3) {
            this.innerKey = Theme.key_contextProgressInner4;
            this.outerKey = Theme.key_contextProgressOuter4;
        }
        updateColors();
    }

    public void updateColors() {
        this.innerPaint.setColor(Theme.getColor(this.innerKey));
        this.outerPaint.setColor(Theme.getColor(this.outerKey));
        invalidate();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            long newTime = System.currentTimeMillis();
            this.lastUpdateTime = newTime;
            this.radOffset = (int) (((float) this.radOffset) + (((float) (360 * (newTime - this.lastUpdateTime))) / 1000.0f));
            int x = (getMeasuredWidth() / 2) - AndroidUtilities.dp(9.0f);
            int y = (getMeasuredHeight() / 2) - AndroidUtilities.dp(9.0f);
            this.cicleRect.set((float) x, (float) y, (float) (AndroidUtilities.dp(18.0f) + x), (float) (AndroidUtilities.dp(18.0f) + y));
            canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(9.0f), this.innerPaint);
            canvas.drawArc(this.cicleRect, (float) (this.radOffset - 90), 90.0f, false, this.outerPaint);
            invalidate();
        }
    }
}
