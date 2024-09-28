package com.king.zxing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.WindowManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.king.zxing.camera.CameraManager;
import java.util.Collection;
import java.util.Map;

public class CaptureHandler extends Handler implements ResultPointCallback {
    private static final String TAG = CaptureHandler.class.getSimpleName();
    private final CameraManager cameraManager;
    private final DecodeThread decodeThread;
    private boolean isReturnBitmap;
    private boolean isSupportAutoZoom;
    private boolean isSupportLuminanceInvert;
    private boolean isSupportVerticalCode;
    private final OnCaptureListener onCaptureListener;
    private State state = State.SUCCESS;
    private final ViewfinderView viewfinderView;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    CaptureHandler(Activity activity, ViewfinderView viewfinderView2, OnCaptureListener onCaptureListener2, Collection<BarcodeFormat> decodeFormats, Map<DecodeHintType, Object> baseHints, String characterSet, CameraManager cameraManager2) {
        this.viewfinderView = viewfinderView2;
        this.onCaptureListener = onCaptureListener2;
        DecodeThread decodeThread2 = new DecodeThread(activity, cameraManager2, this, decodeFormats, baseHints, characterSet, this);
        this.decodeThread = decodeThread2;
        decodeThread2.start();
        this.cameraManager = cameraManager2;
        cameraManager2.startPreview();
        restartPreviewAndDecode();
    }

    public void handleMessage(Message message) {
        if (message.what == R.id.restart_preview) {
            restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
            this.state = State.SUCCESS;
            Bundle bundle = message.getData();
            Bitmap barcode = null;
            float scaleFactor = 1.0f;
            if (bundle != null) {
                byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, (BitmapFactory.Options) null).copy(Bitmap.Config.ARGB_8888, true);
                }
                scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
            }
            this.onCaptureListener.onHandleDecode((Result) message.obj, barcode, scaleFactor);
        } else if (message.what == R.id.decode_failed) {
            this.state = State.PREVIEW;
            this.cameraManager.requestPreviewFrame(this.decodeThread.getHandler(), R.id.decode);
        }
    }

    public void quitSynchronously() {
        this.state = State.DONE;
        this.cameraManager.stopPreview();
        Message.obtain(this.decodeThread.getHandler(), R.id.quit).sendToTarget();
        try {
            this.decodeThread.join(100);
        } catch (InterruptedException e) {
        }
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
        if (this.state == State.SUCCESS) {
            this.state = State.PREVIEW;
            this.cameraManager.requestPreviewFrame(this.decodeThread.getHandler(), R.id.decode);
            ViewfinderView viewfinderView2 = this.viewfinderView;
            if (viewfinderView2 != null) {
                viewfinderView2.drawViewfinder();
            }
        }
    }

    public void foundPossibleResultPoint(ResultPoint point) {
        if (this.viewfinderView != null) {
            this.viewfinderView.addPossibleResultPoint(transform(point));
        }
    }

    private boolean isScreenPortrait(Context context) {
        Display display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point screenResolution = new Point();
        display.getSize(screenResolution);
        return screenResolution.x < screenResolution.y;
    }

    private ResultPoint transform(ResultPoint originPoint) {
        float y;
        float x;
        Point screenPoint = this.cameraManager.getScreenResolution();
        Point cameraPoint = this.cameraManager.getCameraResolution();
        if (screenPoint.x < screenPoint.y) {
            float scaleX = (((float) screenPoint.x) * 1.0f) / ((float) cameraPoint.y);
            float scaleY = (((float) screenPoint.y) * 1.0f) / ((float) cameraPoint.x);
            x = (originPoint.getX() * scaleX) - ((float) (Math.max(screenPoint.x, cameraPoint.y) / 2));
            y = (originPoint.getY() * scaleY) - ((float) (Math.min(screenPoint.y, cameraPoint.x) / 2));
        } else {
            float scaleX2 = (((float) screenPoint.x) * 1.0f) / ((float) cameraPoint.x);
            float scaleY2 = (((float) screenPoint.y) * 1.0f) / ((float) cameraPoint.y);
            x = (originPoint.getX() * scaleX2) - ((float) (Math.min(screenPoint.y, cameraPoint.y) / 2));
            y = (originPoint.getY() * scaleY2) - ((float) (Math.max(screenPoint.x, cameraPoint.x) / 2));
        }
        return new ResultPoint(x, y);
    }

    public boolean isSupportVerticalCode() {
        return this.isSupportVerticalCode;
    }

    public void setSupportVerticalCode(boolean supportVerticalCode) {
        this.isSupportVerticalCode = supportVerticalCode;
    }

    public boolean isReturnBitmap() {
        return this.isReturnBitmap;
    }

    public void setReturnBitmap(boolean returnBitmap) {
        this.isReturnBitmap = returnBitmap;
    }

    public boolean isSupportAutoZoom() {
        return this.isSupportAutoZoom;
    }

    public void setSupportAutoZoom(boolean supportAutoZoom) {
        this.isSupportAutoZoom = supportAutoZoom;
    }

    public boolean isSupportLuminanceInvert() {
        return this.isSupportLuminanceInvert;
    }

    public void setSupportLuminanceInvert(boolean supportLuminanceInvert) {
        this.isSupportLuminanceInvert = supportLuminanceInvert;
    }
}
