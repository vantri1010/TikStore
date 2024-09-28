package com.king.zxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.king.zxing.camera.CameraManager;
import com.king.zxing.camera.FrontLightMode;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.ui.components.banner.config.BannerConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CaptureHelper implements CaptureLifecycle, CaptureTouchEvent, CaptureManager, SurfaceHolder.Callback {
    private static final int DEVIATION = 6;
    private Activity activity;
    private AmbientLightManager ambientLightManager;
    private BeepManager beepManager;
    private float brightEnoughLux;
    private CameraManager cameraManager;
    private CaptureHandler captureHandler;
    private String characterSet;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, Object> decodeHints;
    private int framingRectHorizontalOffset;
    private float framingRectRatio;
    private int framingRectVerticalOffset;
    private boolean hasCameraFlash;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private boolean isAutoRestartPreviewAndDecode;
    private boolean isContinuousScan;
    private boolean isFullScreenScan;
    private boolean isPlayBeep;
    private boolean isReturnBitmap;
    private boolean isSupportAutoZoom;
    private boolean isSupportLuminanceInvert;
    private boolean isSupportVerticalCode;
    private boolean isSupportZoom;
    private boolean isVibrate;
    private View ivTorch;
    private float oldDistance;
    private OnCaptureCallback onCaptureCallback;
    private OnCaptureListener onCaptureListener;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private float tooDarkLux;
    private ViewfinderView viewfinderView;

    @Deprecated
    public CaptureHelper(Fragment fragment, SurfaceView surfaceView2, ViewfinderView viewfinderView2) {
        this(fragment, surfaceView2, viewfinderView2, (View) null);
    }

    public CaptureHelper(Fragment fragment, SurfaceView surfaceView2, ViewfinderView viewfinderView2, View ivTorch2) {
        this((Activity) fragment.getActivity(), surfaceView2, viewfinderView2, ivTorch2);
    }

    @Deprecated
    public CaptureHelper(Activity activity2, SurfaceView surfaceView2, ViewfinderView viewfinderView2) {
        this(activity2, surfaceView2, viewfinderView2, (View) null);
    }

    public CaptureHelper(Activity activity2, SurfaceView surfaceView2, ViewfinderView viewfinderView2, View ivTorch2) {
        this.isSupportZoom = true;
        this.isSupportAutoZoom = true;
        this.isSupportLuminanceInvert = false;
        this.isContinuousScan = false;
        this.isAutoRestartPreviewAndDecode = true;
        this.framingRectRatio = 0.9f;
        this.tooDarkLux = 45.0f;
        this.brightEnoughLux = 100.0f;
        this.activity = activity2;
        this.surfaceView = surfaceView2;
        this.viewfinderView = viewfinderView2;
        this.ivTorch = ivTorch2;
    }

    public void onCreate() {
        this.surfaceHolder = this.surfaceView.getHolder();
        this.hasSurface = false;
        this.inactivityTimer = new InactivityTimer(this.activity);
        this.beepManager = new BeepManager(this.activity);
        this.ambientLightManager = new AmbientLightManager(this.activity);
        this.hasCameraFlash = this.activity.getPackageManager().hasSystemFeature("android.hardware.camera.flash");
        initCameraManager();
        this.onCaptureListener = new OnCaptureListener() {
            public final void onHandleDecode(Result result, Bitmap bitmap, float f) {
                CaptureHelper.this.lambda$onCreate$0$CaptureHelper(result, bitmap, f);
            }
        };
        this.beepManager.setPlayBeep(this.isPlayBeep);
        this.beepManager.setVibrate(this.isVibrate);
        this.ambientLightManager.setTooDarkLux(this.tooDarkLux);
        this.ambientLightManager.setBrightEnoughLux(this.brightEnoughLux);
    }

    public /* synthetic */ void lambda$onCreate$0$CaptureHelper(Result result, Bitmap barcode, float scaleFactor) {
        this.inactivityTimer.onActivity();
        this.beepManager.playBeepSoundAndVibrate();
        onResult(result, barcode, scaleFactor);
    }

    public void onResume() {
        this.beepManager.updatePrefs();
        this.inactivityTimer.onResume();
        if (this.hasSurface) {
            initCamera(this.surfaceHolder);
        } else {
            this.surfaceHolder.addCallback(this);
        }
        this.ambientLightManager.start(this.cameraManager);
    }

    public void onPause() {
        CaptureHandler captureHandler2 = this.captureHandler;
        if (captureHandler2 != null) {
            captureHandler2.quitSynchronously();
            this.captureHandler = null;
        }
        this.inactivityTimer.onPause();
        this.ambientLightManager.stop();
        this.beepManager.close();
        this.cameraManager.closeDriver();
        if (!this.hasSurface) {
            this.surfaceHolder.removeCallback(this);
        }
        View view = this.ivTorch;
        if (view != null && view.getVisibility() == 0) {
            this.ivTorch.setSelected(false);
            this.ivTorch.setVisibility(4);
        }
    }

    public void onDestroy() {
        this.inactivityTimer.shutdown();
    }

    public boolean onTouchEvent(MotionEvent event) {
        Camera camera;
        if (!this.isSupportZoom || !this.cameraManager.isOpen() || (camera = this.cameraManager.getOpenCamera().getCamera()) == null || event.getPointerCount() <= 1) {
            return false;
        }
        int action = event.getAction() & 255;
        if (action == 2) {
            float newDistance = calcFingerSpacing(event);
            float f = this.oldDistance;
            if (newDistance > f + 6.0f) {
                handleZoom(true, camera);
            } else if (newDistance < f - 6.0f) {
                handleZoom(false, camera);
            }
            this.oldDistance = newDistance;
        } else if (action == 5) {
            this.oldDistance = calcFingerSpacing(event);
        }
        return true;
    }

    private void initCameraManager() {
        CameraManager cameraManager2 = new CameraManager(this.activity);
        this.cameraManager = cameraManager2;
        cameraManager2.setFullScreenScan(this.isFullScreenScan);
        this.cameraManager.setFramingRectRatio(this.framingRectRatio);
        this.cameraManager.setFramingRectVerticalOffset(this.framingRectVerticalOffset);
        this.cameraManager.setFramingRectHorizontalOffset(this.framingRectHorizontalOffset);
        View view = this.ivTorch;
        if (view != null && this.hasCameraFlash) {
            view.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    CaptureHelper.this.lambda$initCameraManager$1$CaptureHelper(view);
                }
            });
            this.cameraManager.setOnSensorListener(new CameraManager.OnSensorListener() {
                public final void onSensorChanged(boolean z, boolean z2, float f) {
                    CaptureHelper.this.lambda$initCameraManager$2$CaptureHelper(z, z2, f);
                }
            });
            this.cameraManager.setOnTorchListener(new CameraManager.OnTorchListener() {
                public final void onTorchChanged(boolean z) {
                    CaptureHelper.this.lambda$initCameraManager$3$CaptureHelper(z);
                }
            });
        }
    }

    public /* synthetic */ void lambda$initCameraManager$1$CaptureHelper(View v) {
        CameraManager cameraManager2 = this.cameraManager;
        if (cameraManager2 != null) {
            cameraManager2.setTorch(!this.ivTorch.isSelected());
        }
    }

    public /* synthetic */ void lambda$initCameraManager$2$CaptureHelper(boolean torch, boolean tooDark, float ambientLightLux) {
        if (tooDark) {
            if (this.ivTorch.getVisibility() != 0) {
                this.ivTorch.setVisibility(0);
            }
        } else if (!torch && this.ivTorch.getVisibility() == 0) {
            this.ivTorch.setVisibility(4);
        }
    }

    public /* synthetic */ void lambda$initCameraManager$3$CaptureHelper(boolean torch) {
        this.ivTorch.setSelected(torch);
    }

    private void initCamera(SurfaceHolder surfaceHolder2) {
        if (surfaceHolder2 == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        } else if (this.cameraManager.isOpen()) {
            LogUtils.w("initCamera() while already open -- late SurfaceView callback?");
        } else {
            try {
                this.cameraManager.openDriver(surfaceHolder2);
                if (this.captureHandler == null) {
                    CaptureHandler captureHandler2 = new CaptureHandler(this.activity, this.viewfinderView, this.onCaptureListener, this.decodeFormats, this.decodeHints, this.characterSet, this.cameraManager);
                    this.captureHandler = captureHandler2;
                    captureHandler2.setSupportVerticalCode(this.isSupportVerticalCode);
                    this.captureHandler.setReturnBitmap(this.isReturnBitmap);
                    this.captureHandler.setSupportAutoZoom(this.isSupportAutoZoom);
                    this.captureHandler.setSupportLuminanceInvert(this.isSupportLuminanceInvert);
                }
            } catch (IOException ioe) {
                LogUtils.w((Throwable) ioe);
            } catch (RuntimeException e) {
                LogUtils.w("Unexpected error initializing camera", e);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            LogUtils.w("*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!this.hasSurface) {
            this.hasSurface = true;
            initCamera(holder);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.hasSurface = false;
    }

    private void handleZoom(boolean isZoomIn, Camera camera) {
        Camera.Parameters params = camera.getParameters();
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            int zoom = params.getZoom();
            if (isZoomIn && zoom < maxZoom) {
                zoom++;
            } else if (zoom > 0) {
                zoom--;
            }
            params.setZoom(zoom);
            camera.setParameters(params);
            return;
        }
        LogUtils.i("zoom not supported");
    }

    @Deprecated
    private void focusOnTouch(MotionEvent event, Camera camera) {
        Camera.Parameters params = camera.getParameters();
        Camera.Size previewSize = params.getPreviewSize();
        Rect focusRect = calcTapArea(event.getRawX(), event.getRawY(), 1.0f, previewSize);
        Rect meteringRect = calcTapArea(event.getRawX(), event.getRawY(), 1.5f, previewSize);
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, BannerConfig.SCROLL_TIME));
            parameters.setFocusAreas(focusAreas);
        }
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(meteringRect, BannerConfig.SCROLL_TIME));
            parameters.setMeteringAreas(meteringAreas);
        }
        String currentFocusMode = params.getFocusMode();
        params.setFocusMode("macro");
        camera.setParameters(params);
        camera.autoFocus(new Camera.AutoFocusCallback(currentFocusMode) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final void onAutoFocus(boolean z, Camera camera) {
                CaptureHelper.lambda$focusOnTouch$4(this.f$0, z, camera);
            }
        });
    }

    static /* synthetic */ void lambda$focusOnTouch$4(String currentFocusMode, boolean success, Camera camera1) {
        Camera.Parameters params1 = camera1.getParameters();
        params1.setFocusMode(currentFocusMode);
        camera1.setParameters(params1);
    }

    private float calcFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    private Rect calcTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
        Camera.Size size = previewSize;
        int areaSize = Float.valueOf(200.0f * coefficient).intValue();
        int left = clamp(((int) (((x / ((float) size.width)) * 2000.0f) - 1000.0f)) - (areaSize / 2), -1000, 1000);
        int top = clamp(((int) (((y / ((float) size.height)) * 2000.0f) - 1000.0f)) - (areaSize / 2), -1000, 1000);
        RectF rectF = new RectF((float) left, (float) top, (float) (left + areaSize), (float) (top + areaSize));
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public void restartPreviewAndDecode() {
        CaptureHandler captureHandler2 = this.captureHandler;
        if (captureHandler2 != null) {
            captureHandler2.restartPreviewAndDecode();
        }
    }

    public void onResult(Result result, Bitmap barcode, float scaleFactor) {
        onResult(result);
    }

    public void onResult(Result result) {
        CaptureHandler captureHandler2;
        String text = result.getText();
        if (this.isContinuousScan) {
            OnCaptureCallback onCaptureCallback2 = this.onCaptureCallback;
            if (onCaptureCallback2 != null) {
                onCaptureCallback2.onResultCallback(text);
            }
            if (this.isAutoRestartPreviewAndDecode) {
                restartPreviewAndDecode();
            }
        } else if (!this.isPlayBeep || (captureHandler2 = this.captureHandler) == null) {
            OnCaptureCallback onCaptureCallback3 = this.onCaptureCallback;
            if (onCaptureCallback3 == null || !onCaptureCallback3.onResultCallback(text)) {
                Intent intent = new Intent();
                intent.putExtra("SCAN_RESULT", text);
                this.activity.setResult(-1, intent);
                this.activity.finish();
            }
        } else {
            captureHandler2.postDelayed(new Runnable(text) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    CaptureHelper.this.lambda$onResult$5$CaptureHelper(this.f$1);
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$onResult$5$CaptureHelper(String text) {
        OnCaptureCallback onCaptureCallback2 = this.onCaptureCallback;
        if (onCaptureCallback2 == null || !onCaptureCallback2.onResultCallback(text)) {
            Intent intent = new Intent();
            intent.putExtra("SCAN_RESULT", text);
            this.activity.setResult(-1, intent);
            this.activity.finish();
        }
    }

    public CaptureHelper continuousScan(boolean isContinuousScan2) {
        this.isContinuousScan = isContinuousScan2;
        return this;
    }

    public CaptureHelper autoRestartPreviewAndDecode(boolean isAutoRestartPreviewAndDecode2) {
        this.isAutoRestartPreviewAndDecode = isAutoRestartPreviewAndDecode2;
        return this;
    }

    public CaptureHelper playBeep(boolean playBeep) {
        this.isPlayBeep = playBeep;
        BeepManager beepManager2 = this.beepManager;
        if (beepManager2 != null) {
            beepManager2.setPlayBeep(playBeep);
        }
        return this;
    }

    public CaptureHelper vibrate(boolean vibrate) {
        this.isVibrate = vibrate;
        BeepManager beepManager2 = this.beepManager;
        if (beepManager2 != null) {
            beepManager2.setVibrate(vibrate);
        }
        return this;
    }

    public CaptureHelper supportZoom(boolean supportZoom) {
        this.isSupportZoom = supportZoom;
        return this;
    }

    public CaptureHelper decodeFormats(Collection<BarcodeFormat> decodeFormats2) {
        this.decodeFormats = decodeFormats2;
        return this;
    }

    public CaptureHelper decodeHints(Map<DecodeHintType, Object> decodeHints2) {
        this.decodeHints = decodeHints2;
        return this;
    }

    public CaptureHelper decodeHint(DecodeHintType key, Object value) {
        if (this.decodeHints == null) {
            this.decodeHints = new EnumMap(DecodeHintType.class);
        }
        this.decodeHints.put(key, value);
        return this;
    }

    public CaptureHelper characterSet(String characterSet2) {
        this.characterSet = characterSet2;
        return this;
    }

    public CaptureHelper supportVerticalCode(boolean supportVerticalCode) {
        this.isSupportVerticalCode = supportVerticalCode;
        CaptureHandler captureHandler2 = this.captureHandler;
        if (captureHandler2 != null) {
            captureHandler2.setSupportVerticalCode(supportVerticalCode);
        }
        return this;
    }

    public CaptureHelper frontLightMode(FrontLightMode mode) {
        FrontLightMode.put(this.activity, mode);
        if (!(this.ivTorch == null || mode == FrontLightMode.AUTO)) {
            this.ivTorch.setVisibility(4);
        }
        return this;
    }

    public CaptureHelper tooDarkLux(float tooDarkLux2) {
        this.tooDarkLux = tooDarkLux2;
        AmbientLightManager ambientLightManager2 = this.ambientLightManager;
        if (ambientLightManager2 != null) {
            ambientLightManager2.setTooDarkLux(tooDarkLux2);
        }
        return this;
    }

    public CaptureHelper brightEnoughLux(float brightEnoughLux2) {
        this.brightEnoughLux = brightEnoughLux2;
        AmbientLightManager ambientLightManager2 = this.ambientLightManager;
        if (ambientLightManager2 != null) {
            ambientLightManager2.setTooDarkLux(this.tooDarkLux);
        }
        return this;
    }

    public CaptureHelper returnBitmap(boolean returnBitmap) {
        this.isReturnBitmap = returnBitmap;
        CaptureHandler captureHandler2 = this.captureHandler;
        if (captureHandler2 != null) {
            captureHandler2.setReturnBitmap(returnBitmap);
        }
        return this;
    }

    public CaptureHelper supportAutoZoom(boolean supportAutoZoom) {
        this.isSupportAutoZoom = supportAutoZoom;
        CaptureHandler captureHandler2 = this.captureHandler;
        if (captureHandler2 != null) {
            captureHandler2.setSupportAutoZoom(supportAutoZoom);
        }
        return this;
    }

    public CaptureHelper supportLuminanceInvert(boolean supportLuminanceInvert) {
        this.isSupportLuminanceInvert = supportLuminanceInvert;
        CaptureHandler captureHandler2 = this.captureHandler;
        if (captureHandler2 != null) {
            captureHandler2.setSupportLuminanceInvert(supportLuminanceInvert);
        }
        return this;
    }

    public CaptureHelper fullScreenScan(boolean fullScreenScan) {
        this.isFullScreenScan = fullScreenScan;
        CameraManager cameraManager2 = this.cameraManager;
        if (cameraManager2 != null) {
            cameraManager2.setFullScreenScan(fullScreenScan);
        }
        return this;
    }

    public CaptureHelper framingRectRatio(float framingRectRatio2) {
        this.framingRectRatio = framingRectRatio2;
        CameraManager cameraManager2 = this.cameraManager;
        if (cameraManager2 != null) {
            cameraManager2.setFramingRectRatio(framingRectRatio2);
        }
        return this;
    }

    public CaptureHelper framingRectVerticalOffset(int framingRectVerticalOffset2) {
        this.framingRectVerticalOffset = framingRectVerticalOffset2;
        CameraManager cameraManager2 = this.cameraManager;
        if (cameraManager2 != null) {
            cameraManager2.setFramingRectVerticalOffset(framingRectVerticalOffset2);
        }
        return this;
    }

    public CaptureHelper framingRectHorizontalOffset(int framingRectHorizontalOffset2) {
        this.framingRectHorizontalOffset = framingRectHorizontalOffset2;
        CameraManager cameraManager2 = this.cameraManager;
        if (cameraManager2 != null) {
            cameraManager2.setFramingRectHorizontalOffset(framingRectHorizontalOffset2);
        }
        return this;
    }

    public CaptureHelper setOnCaptureCallback(OnCaptureCallback callback) {
        this.onCaptureCallback = callback;
        return this;
    }

    public CameraManager getCameraManager() {
        return this.cameraManager;
    }

    public BeepManager getBeepManager() {
        return this.beepManager;
    }

    public AmbientLightManager getAmbientLightManager() {
        return this.ambientLightManager;
    }

    public InactivityTimer getInactivityTimer() {
        return this.inactivityTimer;
    }
}
