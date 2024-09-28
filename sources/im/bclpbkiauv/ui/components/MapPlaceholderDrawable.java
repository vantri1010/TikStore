package im.bclpbkiauv.ui.components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class MapPlaceholderDrawable extends Drawable {
    private Paint linePaint;
    private Paint paint;

    public MapPlaceholderDrawable() {
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setColor(-2172970);
        Paint paint3 = new Paint();
        this.linePaint = paint3;
        paint3.setColor(-3752002);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(getBounds(), this.paint);
        int gap = AndroidUtilities.dp(9.0f);
        int xcount = getBounds().width() / gap;
        int ycount = getBounds().height() / gap;
        int x = getBounds().left;
        int y = getBounds().top;
        for (int a = 0; a < xcount; a++) {
            canvas.drawLine((float) (((a + 1) * gap) + x), (float) y, (float) (((a + 1) * gap) + x), (float) (getBounds().height() + y), this.linePaint);
        }
        for (int a2 = 0; a2 < ycount; a2++) {
            canvas.drawLine((float) x, (float) (((a2 + 1) * gap) + y), (float) (getBounds().width() + x), (float) (((a2 + 1) * gap) + y), this.linePaint);
        }
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return 0;
    }

    public int getIntrinsicHeight() {
        return 0;
    }
}
