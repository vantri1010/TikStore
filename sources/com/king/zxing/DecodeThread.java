package com.king.zxing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.king.zxing.camera.CameraManager;
import com.king.zxing.util.LogUtils;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {
    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";
    private final CameraManager cameraManager;
    private CaptureHandler captureHandler;
    private final Context context;
    private Handler handler;
    private final CountDownLatch handlerInitLatch = new CountDownLatch(1);
    private final Map<DecodeHintType, Object> hints;

    DecodeThread(Context context2, CameraManager cameraManager2, CaptureHandler captureHandler2, Collection<BarcodeFormat> decodeFormats, Map<DecodeHintType, Object> baseHints, String characterSet, ResultPointCallback resultPointCallback) {
        this.context = context2;
        this.cameraManager = cameraManager2;
        this.captureHandler = captureHandler2;
        EnumMap enumMap = new EnumMap(DecodeHintType.class);
        this.hints = enumMap;
        if (baseHints != null) {
            enumMap.putAll(baseHints);
        }
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context2);
            decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
            if (prefs.getBoolean(Preferences.KEY_DECODE_1D_PRODUCT, true)) {
                decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_1D_INDUSTRIAL, true)) {
                decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_QR, true)) {
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_DATA_MATRIX, true)) {
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_AZTEC, false)) {
                decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_PDF417, false)) {
                decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
            }
        }
        this.hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        if (characterSet != null) {
            this.hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }
        this.hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
        LogUtils.i("Hints: " + this.hints);
    }

    /* access modifiers changed from: package-private */
    public Handler getHandler() {
        try {
            this.handlerInitLatch.await();
        } catch (InterruptedException e) {
        }
        return this.handler;
    }

    public void run() {
        Looper.prepare();
        this.handler = new DecodeHandler(this.context, this.cameraManager, this.captureHandler, this.hints);
        this.handlerInitLatch.countDown();
        Looper.loop();
    }
}
