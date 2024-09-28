package com.google.android.gms.internal.vision;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public final class zzo {
    public static Bitmap zzb(Bitmap bitmap, zzm zzm) {
        int i;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (zzm.rotation != 0) {
            Matrix matrix = new Matrix();
            int i2 = zzm.rotation;
            if (i2 == 0) {
                i = 0;
            } else if (i2 == 1) {
                i = 90;
            } else if (i2 == 2) {
                i = 180;
            } else if (i2 == 3) {
                i = 270;
            } else {
                throw new IllegalArgumentException("Unsupported rotation degree.");
            }
            matrix.postRotate((float) i);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        }
        if (zzm.rotation == 1 || zzm.rotation == 3) {
            zzm.width = height;
            zzm.height = width;
        }
        return bitmap;
    }
}
