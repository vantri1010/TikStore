package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

public class CenteredImageSpan extends ImageSpan {
    public static final int ALIGN_CENTER = 2;

    public CenteredImageSpan(Drawable drawableRes) {
        super(drawableRes);
    }

    public CenteredImageSpan(Context context, Bitmap b) {
        super(context, b);
    }

    public CenteredImageSpan(Context context, Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

    public CenteredImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public CenteredImageSpan(Drawable d, String source) {
        super(d, source);
    }

    public CenteredImageSpan(Drawable d, String source, int verticalAlignment) {
        super(d, source, verticalAlignment);
    }

    public CenteredImageSpan(Context context, Uri uri) {
        super(context, uri);
    }

    public CenteredImageSpan(Context context, Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public CenteredImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public CenteredImageSpan(Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (this.mVerticalAlignment == 1 || this.mVerticalAlignment == 0) {
            return super.getSize(paint, text, start, end, fm);
        }
        Rect rect = getDrawable().getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;
            int top = (drHeight / 2) - (fontHeight / 4);
            int bottom = (drHeight / 2) + (fontHeight / 4);
            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        if (this.mVerticalAlignment == 1 || this.mVerticalAlignment == 0) {
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
            return;
        }
        Drawable b = getDrawable();
        canvas.save();
        canvas.translate(x, (float) ((((bottom - top) - b.getBounds().bottom) / 2) + top));
        b.draw(canvas);
        canvas.restore();
    }
}
