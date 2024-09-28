package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class HashtagSearchCell extends TextView {
    private boolean needDivider;

    public HashtagSearchCell(Context context) {
        super(context);
        setGravity(16);
        setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        setTextSize(1, 17.0f);
        setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    public void setNeedDivider(boolean value) {
        this.needDivider = value;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(48.0f) + 1);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
