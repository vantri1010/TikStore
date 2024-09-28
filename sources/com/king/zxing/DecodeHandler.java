package com.king.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.view.WindowManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.king.zxing.camera.CameraManager;
import com.king.zxing.util.LogUtils;
import java.io.ByteArrayOutputStream;
import java.util.Map;

final class DecodeHandler extends Handler {
    private final CameraManager cameraManager;
    private final Context context;
    private final CaptureHandler handler;
    private long lastZoomTime;
    private final MultiFormatReader multiFormatReader;
    private boolean running = true;

    DecodeHandler(Context context2, CameraManager cameraManager2, CaptureHandler handler2, Map<DecodeHintType, Object> hints) {
        MultiFormatReader multiFormatReader2 = new MultiFormatReader();
        this.multiFormatReader = multiFormatReader2;
        multiFormatReader2.setHints(hints);
        this.context = context2;
        this.cameraManager = cameraManager2;
        this.handler = handler2;
    }

    public void handleMessage(Message message) {
        if (message != null && this.running) {
            if (message.what == R.id.decode) {
                decode((byte[]) message.obj, message.arg1, message.arg2, isScreenPortrait(), this.handler.isSupportVerticalCode());
            } else if (message.what == R.id.quit) {
                this.running = false;
                Looper.myLooper().quit();
            }
        }
    }

    private boolean isScreenPortrait() {
        Display display = ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay();
        Point screenResolution = new Point();
        display.getSize(screenResolution);
        return screenResolution.x < screenResolution.y;
    }

    private void decode(byte[] data, int width, int height, boolean isScreenPortrait, boolean isSupportVerticalCode) {
        boolean isReDecode;
        int i = width;
        long start = System.currentTimeMillis();
        Result rawResult = null;
        PlanarYUVLuminanceSource source = buildPlanarYUVLuminanceSource(data, width, height, isScreenPortrait);
        if (source != null) {
            try {
                rawResult = this.multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
                isReDecode = false;
            } catch (Exception e) {
                isReDecode = true;
            }
            if (isReDecode && this.handler.isSupportLuminanceInvert()) {
                try {
                    rawResult = this.multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source.invert())));
                    isReDecode = false;
                } catch (Exception e2) {
                    isReDecode = true;
                }
            }
            if (isReDecode) {
                try {
                    rawResult = this.multiFormatReader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
                    isReDecode = false;
                } catch (Exception e3) {
                    isReDecode = true;
                }
            }
            if (!isReDecode || !isSupportVerticalCode) {
                byte[] bArr = data;
                int i2 = height;
            } else {
                source = buildPlanarYUVLuminanceSource(data, i, height, !isScreenPortrait);
                if (source != null) {
                    try {
                        rawResult = this.multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
                    } catch (Exception e4) {
                    }
                }
            }
            this.multiFormatReader.reset();
        } else {
            byte[] bArr2 = data;
            int i3 = height;
        }
        if (rawResult != null) {
            long end = System.currentTimeMillis();
            LogUtils.d("Found barcode in " + (end - start) + " ms");
            BarcodeFormat barcodeFormat = rawResult.getBarcodeFormat();
            CaptureHandler captureHandler = this.handler;
            if (captureHandler == null || !captureHandler.isSupportAutoZoom() || barcodeFormat != BarcodeFormat.QR_CODE) {
                long j = start;
            } else {
                ResultPoint[] resultPoints = rawResult.getResultPoints();
                if (resultPoints.length >= 3) {
                    if (handleAutoZoom((int) Math.max(Math.max(ResultPoint.distance(resultPoints[0], resultPoints[1]), ResultPoint.distance(resultPoints[1], resultPoints[2])), ResultPoint.distance(resultPoints[0], resultPoints[2])), i)) {
                        BarcodeFormat barcodeFormat2 = barcodeFormat;
                        Message message = Message.obtain();
                        message.what = R.id.decode_succeeded;
                        message.obj = rawResult;
                        if (this.handler.isReturnBitmap()) {
                            Bundle bundle = new Bundle();
                            bundleThumbnail(source, bundle);
                            message.setData(bundle);
                        }
                        long j2 = start;
                        this.handler.sendMessageDelayed(message, 300);
                        return;
                    }
                    long j3 = start;
                } else {
                    long j4 = start;
                }
            }
            CaptureHandler captureHandler2 = this.handler;
            if (captureHandler2 != null) {
                Message message2 = Message.obtain(captureHandler2, R.id.decode_succeeded, rawResult);
                if (this.handler.isReturnBitmap()) {
                    Bundle bundle2 = new Bundle();
                    bundleThumbnail(source, bundle2);
                    message2.setData(bundle2);
                }
                message2.sendToTarget();
                return;
            }
            return;
        }
        CaptureHandler captureHandler3 = this.handler;
        if (captureHandler3 != null) {
            Message.obtain(captureHandler3, R.id.decode_failed).sendToTarget();
        }
    }

    private PlanarYUVLuminanceSource buildPlanarYUVLuminanceSource(byte[] data, int width, int height, boolean isRotate) {
        if (!isRotate) {
            return this.cameraManager.buildLuminanceSource(data, width, height);
        }
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedData[(((x * height) + height) - y) - 1] = data[(y * width) + x];
            }
        }
        int tmp = width;
        return this.cameraManager.buildLuminanceSource(rotatedData, height, tmp);
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, source.getThumbnailHeight(), Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
        bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, ((float) width) / ((float) source.getWidth()));
    }

    private boolean handleAutoZoom(int length, int width) {
        Camera camera;
        if (this.lastZoomTime > System.currentTimeMillis() - 1000) {
            return true;
        }
        if (length >= width / 5 || (camera = this.cameraManager.getOpenCamera().getCamera()) == null) {
            return false;
        }
        Camera.Parameters params = camera.getParameters();
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            params.setZoom(Math.min((maxZoom / 5) + params.getZoom(), maxZoom));
            camera.setParameters(params);
            this.lastZoomTime = System.currentTimeMillis();
            return true;
        }
        LogUtils.d("Zoom not supported");
        return false;
    }
}
