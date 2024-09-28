package im.bclpbkiauv.ui.components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;

public class ScamDrawable extends Drawable {
    private Paint paint = new Paint(1);
    private RectF rect = new RectF();
    private String text;
    private TextPaint textPaint;
    private int textWidth;

    public ScamDrawable(int textSize) {
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp((float) textSize));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        String string = LocaleController.getString("ScamMessage", R.string.ScamMessage);
        this.text = string;
        this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(string));
    }

    public void checkText() {
        String newText = LocaleController.getString("ScamMessage", R.string.ScamMessage);
        if (!newText.equals(this.text)) {
            this.text = newText;
            this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(newText));
        }
    }

    public void setColor(int color) {
        this.textPaint.setColor(color);
        this.paint.setColor(color);
    }

    public void setAlpha(int alpha) {
    }

    public int getIntrinsicWidth() {
        return this.textWidth + AndroidUtilities.dp(10.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(16.0f);
    }

    public void draw(Canvas canvas) {
        this.rect.set(getBounds());
        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
        canvas.drawText(this.text, this.rect.left + ((float) AndroidUtilities.dp(5.0f)), this.rect.top + ((float) AndroidUtilities.dp(12.0f)), this.textPaint);
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -2;
    }
}
