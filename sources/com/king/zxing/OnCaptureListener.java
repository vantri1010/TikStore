package com.king.zxing;

import android.graphics.Bitmap;
import com.google.zxing.Result;

public interface OnCaptureListener {
    void onHandleDecode(Result result, Bitmap bitmap, float f);
}
