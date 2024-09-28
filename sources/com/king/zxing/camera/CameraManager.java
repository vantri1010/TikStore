package com.king.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceHolder;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.king.zxing.camera.open.OpenCamera;
import com.king.zxing.camera.open.OpenCameraInterface;
import com.king.zxing.util.LogUtils;
import java.io.IOException;

public final class CameraManager {
    private static final int MAX_FRAME_HEIGHT = 675;
    private static final int MAX_FRAME_WIDTH = 1200;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MIN_FRAME_WIDTH = 240;
    private AutoFocusManager autoFocusManager;
    private OpenCamera camera;
    private final CameraConfigurationManager configManager;
    private final Context context;
    private Rect framingRect;
    private int framingRectHorizontalOffset;
    private Rect framingRectInPreview;
    private float framingRectRatio;
    private int framingRectVerticalOffset;
    private boolean initialized;
    private boolean isFullScreenScan;
    private boolean isTorch;
    private OnSensorListener onSensorListener;
    private OnTorchListener onTorchListener;
    private final PreviewCallback previewCallback;
    private boolean previewing;
    private int requestedCameraId = -1;
    private int requestedFramingRectHeight;
    private int requestedFramingRectWidth;

    public interface OnSensorListener {
        void onSensorChanged(boolean z, boolean z2, float f);
    }

    public interface OnTorchListener {
        void onTorchChanged(boolean z);
    }

    public CameraManager(Context context2) {
        this.context = context2.getApplicationContext();
        this.configManager = new CameraConfigurationManager(context2);
        this.previewCallback = new PreviewCallback(this.configManager);
    }

    public void openDriver(SurfaceHolder holder) throws IOException {
        int i;
        OpenCamera theCamera = this.camera;
        if (theCamera == null) {
            theCamera = OpenCameraInterface.open(this.requestedCameraId);
            if (theCamera != null) {
                this.camera = theCamera;
            } else {
                throw new IOException("Camera.open() failed to return object from driver");
            }
        }
        if (!this.initialized) {
            this.initialized = true;
            this.configManager.initFromCameraParameters(theCamera);
            int i2 = this.requestedFramingRectWidth;
            if (i2 > 0 && (i = this.requestedFramingRectHeight) > 0) {
                setManualFramingRect(i2, i);
                this.requestedFramingRectWidth = 0;
                this.requestedFramingRectHeight = 0;
            }
        }
        Camera cameraObject = theCamera.getCamera();
        Camera.Parameters parameters = cameraObject.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten();
        try {
            this.configManager.setDesiredCameraParameters(theCamera, false);
        } catch (RuntimeException e) {
            LogUtils.w("Camera rejected parameters. Setting only minimal safe-mode parameters");
            LogUtils.i("Resetting to saved camera params: " + parametersFlattened);
            if (parametersFlattened != null) {
                Camera.Parameters parameters2 = cameraObject.getParameters();
                parameters2.unflatten(parametersFlattened);
                try {
                    cameraObject.setParameters(parameters2);
                    this.configManager.setDesiredCameraParameters(theCamera, true);
                } catch (RuntimeException e2) {
                    LogUtils.w("Camera rejected even safe-mode parameters! No configuration");
                }
            }
        }
        cameraObject.setPreviewDisplay(holder);
    }

    public synchronized boolean isOpen() {
        return this.camera != null;
    }

    public OpenCamera getOpenCamera() {
        return this.camera;
    }

    public void closeDriver() {
        OpenCamera openCamera = this.camera;
        if (openCamera != null) {
            openCamera.getCamera().release();
            this.camera = null;
            this.framingRect = null;
            this.framingRectInPreview = null;
        }
        this.isTorch = false;
        OnTorchListener onTorchListener2 = this.onTorchListener;
        if (onTorchListener2 != null) {
            onTorchListener2.onTorchChanged(false);
        }
    }

    public void startPreview() {
        OpenCamera theCamera = this.camera;
        if (theCamera != null && !this.previewing) {
            theCamera.getCamera().startPreview();
            this.previewing = true;
            this.autoFocusManager = new AutoFocusManager(this.context, theCamera.getCamera());
        }
    }

    public void stopPreview() {
        AutoFocusManager autoFocusManager2 = this.autoFocusManager;
        if (autoFocusManager2 != null) {
            autoFocusManager2.stop();
            this.autoFocusManager = null;
        }
        OpenCamera openCamera = this.camera;
        if (openCamera != null && this.previewing) {
            openCamera.getCamera().stopPreview();
            this.previewCallback.setHandler((Handler) null, 0);
            this.previewing = false;
        }
    }

    public synchronized void setTorch(boolean newSetting) {
        OpenCamera theCamera = this.camera;
        if (!(theCamera == null || newSetting == this.configManager.getTorchState(theCamera.getCamera()))) {
            boolean wasAutoFocusManager = this.autoFocusManager != null;
            if (wasAutoFocusManager) {
                this.autoFocusManager.stop();
                this.autoFocusManager = null;
            }
            this.isTorch = newSetting;
            this.configManager.setTorch(theCamera.getCamera(), newSetting);
            if (wasAutoFocusManager) {
                AutoFocusManager autoFocusManager2 = new AutoFocusManager(this.context, theCamera.getCamera());
                this.autoFocusManager = autoFocusManager2;
                autoFocusManager2.start();
            }
            if (this.onTorchListener != null) {
                this.onTorchListener.onTorchChanged(newSetting);
            }
        }
    }

    public synchronized void requestPreviewFrame(Handler handler, int message) {
        OpenCamera theCamera = this.camera;
        if (theCamera != null && this.previewing) {
            this.previewCallback.setHandler(handler, message);
            theCamera.getCamera().setOneShotPreviewCallback(this.previewCallback);
        }
    }

    public synchronized Rect getFramingRect() {
        if (this.framingRect == null) {
            if (this.camera == null) {
                return null;
            }
            Point point = this.configManager.getCameraResolution();
            if (point == null) {
                return null;
            }
            int width = point.x;
            int height = point.y;
            if (this.isFullScreenScan) {
                this.framingRect = new Rect(0, 0, width, height);
            } else {
                int size = (int) (((float) Math.min(width, height)) * this.framingRectRatio);
                int leftOffset = ((width - size) / 2) + this.framingRectHorizontalOffset;
                int topOffset = ((height - size) / 2) + this.framingRectVerticalOffset;
                this.framingRect = new Rect(leftOffset, topOffset, leftOffset + size, topOffset + size);
            }
        }
        return this.framingRect;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0054, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized android.graphics.Rect getFramingRectInPreview() {
        /*
            r6 = this;
            monitor-enter(r6)
            android.graphics.Rect r0 = r6.framingRectInPreview     // Catch:{ all -> 0x0059 }
            if (r0 != 0) goto L_0x0055
            android.graphics.Rect r0 = r6.getFramingRect()     // Catch:{ all -> 0x0059 }
            r1 = 0
            if (r0 != 0) goto L_0x000e
            monitor-exit(r6)
            return r1
        L_0x000e:
            android.graphics.Rect r2 = new android.graphics.Rect     // Catch:{ all -> 0x0059 }
            r2.<init>(r0)     // Catch:{ all -> 0x0059 }
            com.king.zxing.camera.CameraConfigurationManager r3 = r6.configManager     // Catch:{ all -> 0x0059 }
            android.graphics.Point r3 = r3.getCameraResolution()     // Catch:{ all -> 0x0059 }
            com.king.zxing.camera.CameraConfigurationManager r4 = r6.configManager     // Catch:{ all -> 0x0059 }
            android.graphics.Point r4 = r4.getScreenResolution()     // Catch:{ all -> 0x0059 }
            if (r3 == 0) goto L_0x0053
            if (r4 != 0) goto L_0x0024
            goto L_0x0053
        L_0x0024:
            int r1 = r2.left     // Catch:{ all -> 0x0059 }
            int r5 = r3.y     // Catch:{ all -> 0x0059 }
            int r1 = r1 * r5
            int r5 = r4.x     // Catch:{ all -> 0x0059 }
            int r1 = r1 / r5
            r2.left = r1     // Catch:{ all -> 0x0059 }
            int r1 = r2.right     // Catch:{ all -> 0x0059 }
            int r5 = r3.y     // Catch:{ all -> 0x0059 }
            int r1 = r1 * r5
            int r5 = r4.x     // Catch:{ all -> 0x0059 }
            int r1 = r1 / r5
            r2.right = r1     // Catch:{ all -> 0x0059 }
            int r1 = r2.top     // Catch:{ all -> 0x0059 }
            int r5 = r3.x     // Catch:{ all -> 0x0059 }
            int r1 = r1 * r5
            int r5 = r4.y     // Catch:{ all -> 0x0059 }
            int r1 = r1 / r5
            r2.top = r1     // Catch:{ all -> 0x0059 }
            int r1 = r2.bottom     // Catch:{ all -> 0x0059 }
            int r5 = r3.x     // Catch:{ all -> 0x0059 }
            int r1 = r1 * r5
            int r5 = r4.y     // Catch:{ all -> 0x0059 }
            int r1 = r1 / r5
            r2.bottom = r1     // Catch:{ all -> 0x0059 }
            r6.framingRectInPreview = r2     // Catch:{ all -> 0x0059 }
            goto L_0x0055
        L_0x0053:
            monitor-exit(r6)
            return r1
        L_0x0055:
            android.graphics.Rect r0 = r6.framingRectInPreview     // Catch:{ all -> 0x0059 }
            monitor-exit(r6)
            return r0
        L_0x0059:
            r0 = move-exception
            monitor-exit(r6)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.king.zxing.camera.CameraManager.getFramingRectInPreview():android.graphics.Rect");
    }

    public void setFullScreenScan(boolean fullScreenScan) {
        this.isFullScreenScan = fullScreenScan;
    }

    public void setFramingRectRatio(float framingRectRatio2) {
        this.framingRectRatio = framingRectRatio2;
    }

    public void setFramingRectVerticalOffset(int framingRectVerticalOffset2) {
        this.framingRectVerticalOffset = framingRectVerticalOffset2;
    }

    public void setFramingRectHorizontalOffset(int framingRectHorizontalOffset2) {
        this.framingRectHorizontalOffset = framingRectHorizontalOffset2;
    }

    public Point getCameraResolution() {
        return this.configManager.getCameraResolution();
    }

    public Point getScreenResolution() {
        return this.configManager.getScreenResolution();
    }

    public synchronized void setManualCameraId(int cameraId) {
        this.requestedCameraId = cameraId;
    }

    public synchronized void setManualFramingRect(int width, int height) {
        if (this.initialized) {
            Point screenResolution = this.configManager.getScreenResolution();
            if (width > screenResolution.x) {
                width = screenResolution.x;
            }
            if (height > screenResolution.y) {
                height = screenResolution.y;
            }
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            this.framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            LogUtils.d("Calculated manual framing rect: " + this.framingRect);
            this.framingRectInPreview = null;
        } else {
            this.requestedFramingRectWidth = width;
            this.requestedFramingRectHeight = height;
        }
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        if (getFramingRectInPreview() == null) {
            return null;
        }
        if (this.isFullScreenScan) {
            return new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
        }
        int size = (int) (((float) Math.min(width, height)) * this.framingRectRatio);
        return new PlanarYUVLuminanceSource(data, width, height, ((width - size) / 2) + this.framingRectHorizontalOffset, ((height - size) / 2) + this.framingRectVerticalOffset, size, size, false);
    }

    public void setOnTorchListener(OnTorchListener listener) {
        this.onTorchListener = listener;
    }

    public void setOnSensorListener(OnSensorListener listener) {
        this.onSensorListener = listener;
    }

    public void sensorChanged(boolean tooDark, float ambientLightLux) {
        OnSensorListener onSensorListener2 = this.onSensorListener;
        if (onSensorListener2 != null) {
            onSensorListener2.onSensorChanged(this.isTorch, tooDark, ambientLightLux);
        }
    }
}
