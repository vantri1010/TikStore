package im.bclpbkiauv.ui.components.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class Engine {
    private boolean focusAlpha;
    private int mCompressQuality;
    private int srcHeight;
    private InputStreamProvider srcImg;
    private int srcWidth;
    private File tagImg;

    Engine(InputStreamProvider srcImg2, File tagImg2, boolean focusAlpha2, int compressQuality) throws IOException {
        this.tagImg = tagImg2;
        this.srcImg = srcImg2;
        this.focusAlpha = focusAlpha2;
        this.mCompressQuality = compressQuality;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeStream(srcImg2.open(), (Rect) null, options);
        this.srcWidth = options.outWidth;
        this.srcHeight = options.outHeight;
    }

    private int computeSize() {
        int i = this.srcWidth;
        if (i % 2 == 1) {
            i++;
        }
        this.srcWidth = i;
        int i2 = this.srcHeight;
        if (i2 % 2 == 1) {
            i2++;
        }
        this.srcHeight = i2;
        int longSide = Math.max(this.srcWidth, i2);
        float scale = ((float) Math.min(this.srcWidth, this.srcHeight)) / ((float) longSide);
        if (scale > 1.0f || ((double) scale) <= 0.5625d) {
            if (((double) scale) > 0.5625d || ((double) scale) <= 0.5d) {
                return (int) Math.ceil(((double) longSide) / (1280.0d / ((double) scale)));
            }
            if (longSide / 1280 == 0) {
                return 1;
            }
            return longSide / 1280;
        } else if (longSide < 1664) {
            return 1;
        } else {
            if (longSide < 4990) {
                return 2;
            }
            if (longSide > 4990 && longSide < 10240) {
                return 4;
            }
            if (longSide / 1280 == 0) {
                return 1;
            }
            return longSide / 1280;
        }
    }

    private Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /* access modifiers changed from: package-private */
    public File compress() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize();
        Bitmap tagBitmap = BitmapFactory.decodeStream(this.srcImg.open(), (Rect) null, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (Checker.SINGLE.isJPG(this.srcImg.open())) {
            tagBitmap = rotatingImage(tagBitmap, Checker.SINGLE.getOrientation(this.srcImg.open()));
        }
        tagBitmap.compress(this.focusAlpha ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, this.mCompressQuality, stream);
        tagBitmap.recycle();
        FileOutputStream fos = new FileOutputStream(this.tagImg);
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();
        stream.close();
        return this.tagImg;
    }
}
