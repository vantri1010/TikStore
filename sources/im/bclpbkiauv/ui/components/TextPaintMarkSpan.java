package im.bclpbkiauv.ui.components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TextPaintMarkSpan extends MetricAffectingSpan {
    private TextPaint textPaint;

    public TextPaintMarkSpan(TextPaint paint) {
        this.textPaint = paint;
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    public void updateMeasureState(TextPaint p) {
        TextPaint textPaint2 = this.textPaint;
        if (textPaint2 != null) {
            p.setColor(textPaint2.getColor());
            p.setTypeface(this.textPaint.getTypeface());
            p.setFlags(this.textPaint.getFlags());
            p.setTextSize(this.textPaint.getTextSize());
            p.baselineShift = this.textPaint.baselineShift;
            p.bgColor = this.textPaint.bgColor;
        }
    }

    public void updateDrawState(TextPaint p) {
        TextPaint textPaint2 = this.textPaint;
        if (textPaint2 != null) {
            p.setColor(textPaint2.getColor());
            p.setTypeface(this.textPaint.getTypeface());
            p.setFlags(this.textPaint.getFlags());
            p.setTextSize(this.textPaint.getTextSize());
            p.baselineShift = this.textPaint.baselineShift;
            p.bgColor = this.textPaint.bgColor;
        }
    }
}
