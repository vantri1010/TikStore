package im.bclpbkiauv.messenger.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

public class BlurKit {
    private static BlurKit instance;
    private RenderScript rs;

    public static void init(Context context) {
        if (instance == null) {
            BlurKit blurKit = new BlurKit();
            instance = blurKit;
            blurKit.rs = RenderScript.create(context);
        }
    }

    public Bitmap blur(Bitmap src, int radius) {
        Allocation input = Allocation.createFromBitmap(this.rs, src);
        Allocation output = Allocation.createTyped(this.rs, input.getType());
        RenderScript renderScript = this.rs;
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setRadius((float) radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(src);
        return src;
    }

    public Bitmap blur(View src, int radius) {
        return blur(getBitmapForView(src, 1.0f), radius);
    }

    public Bitmap fastBlur(View src, int radius, float downscaleFactor) {
        return blur(getBitmapForView(src, downscaleFactor), radius);
    }

    private Bitmap getBitmapForView(View src, float downscaleFactor) {
        Bitmap bitmap = Bitmap.createBitmap((int) (((float) src.getWidth()) * downscaleFactor), (int) (((float) src.getHeight()) * downscaleFactor), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.preScale(downscaleFactor, downscaleFactor);
        canvas.setMatrix(matrix);
        src.draw(canvas);
        return bitmap;
    }

    public static BlurKit getInstance() {
        BlurKit blurKit = instance;
        if (blurKit != null) {
            return blurKit;
        }
        throw new RuntimeException("BlurKit not initialized!");
    }
}
