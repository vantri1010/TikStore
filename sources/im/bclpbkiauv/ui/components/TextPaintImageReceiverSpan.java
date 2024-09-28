package im.bclpbkiauv.ui.components;

import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.tgnet.TLRPC;
import java.util.Locale;

public class TextPaintImageReceiverSpan extends ReplacementSpan {
    private boolean alignTop;
    private int height;
    private ImageReceiver imageReceiver;
    private int width;

    public TextPaintImageReceiverSpan(View parentView, TLRPC.Document document, Object parentObject, int w, int h, boolean top, boolean invert) {
        TLRPC.Document document2 = document;
        String filter = String.format(Locale.US, "%d_%d_i", new Object[]{Integer.valueOf(w), Integer.valueOf(h)});
        this.width = w;
        this.height = h;
        ImageReceiver imageReceiver2 = new ImageReceiver(parentView);
        this.imageReceiver = imageReceiver2;
        imageReceiver2.setInvalidateAll(true);
        if (invert) {
            this.imageReceiver.setDelegate($$Lambda$TextPaintImageReceiverSpan$9wMHLf1BUFFDhs7yc_zKHFk5hbs.INSTANCE);
        }
        String str = filter;
        this.imageReceiver.setImage(ImageLocation.getForDocument(document), str, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90), document2), filter, -1, (String) null, parentObject, 1);
        this.alignTop = top;
    }

    static /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver2, boolean set, boolean thumb) {
        if (imageReceiver2.canInvertBitmap()) {
            imageReceiver2.setColorFilter(new ColorMatrixColorFilter(new float[]{-1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, -1.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        }
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm != null) {
            if (this.alignTop) {
                int h = (fm.descent - fm.ascent) - AndroidUtilities.dp(4.0f);
                int i = this.height - h;
                fm.descent = i;
                fm.bottom = i;
                int i2 = 0 - h;
                fm.ascent = i2;
                fm.top = i2;
            } else {
                int dp = ((-this.height) / 2) - AndroidUtilities.dp(4.0f);
                fm.ascent = dp;
                fm.top = dp;
                int i3 = this.height;
                int dp2 = (i3 - (i3 / 2)) - AndroidUtilities.dp(4.0f);
                fm.descent = dp2;
                fm.bottom = dp2;
            }
        }
        return this.width;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        canvas.save();
        if (this.alignTop) {
            this.imageReceiver.setImageCoords((int) x, top - 1, this.width, this.height);
        } else {
            int i = this.height;
            this.imageReceiver.setImageCoords((int) x, ((((bottom - AndroidUtilities.dp(4.0f)) - top) - i) / 2) + top, this.width, i);
        }
        this.imageReceiver.draw(canvas);
        canvas.restore();
    }
}
