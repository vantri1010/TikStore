package com.alivc.component.capture;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoPusher implements Camera.ErrorCallback {
    private static final int SCREEN_LANDSCAPE_LEFT = 90;
    private static final int SCREEN_LANDSCAPE_RIGHT = 270;
    private static final int SCREEN_PORTRAIT = 0;
    private static final String TAG = "VideoPusher";
    private static byte[] buffer = null;
    private static byte[] buffer1 = null;
    private static byte[] buffer2 = null;
    private static List<Integer> sSupportedFormat = new ArrayList();
    private static Map<Integer, List<Camera.Size>> sSupportedResolutionMap = new HashMap();
    private static int supportMaxWH = 0;
    private final float ACCEL_DELTAXYZ_DIFF = 1.4f;
    private final float ACCEL_DELTA_DIFF = 0.6f;
    private final float INITIATE_VALUE = -1.0f;
    private final float MAG_DELTA_DIFF = 5.0f;
    private final int TIME_MILLISECOND = 1000;
    private Sensor mAccelSensor = null;
    private Application.ActivityLifecycleCallbacks mActivityCallbacks = new Application.ActivityLifecycleCallbacks() {
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityResumed(Activity activity) {
            if (VideoPusher.this.mCameraDisConnected) {
                boolean unused = VideoPusher.this.mCameraDisConnected = false;
                VideoPusher.this.stopInner(false);
                try {
                    VideoPusher.this.startInner();
                } catch (Exception e) {
                }
            }
        }

        public void onActivityPaused(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onActivityDestroyed(Activity activity) {
        }
    };
    private boolean mAutoFocus = false;
    /* access modifiers changed from: private */
    public boolean mAutoFocusing = false;
    /* access modifiers changed from: private */
    public int mBelowMinFpsNumberTimes = 0;
    /* access modifiers changed from: private */
    public Camera mCamera;
    /* access modifiers changed from: private */
    public boolean mCameraDisConnected = false;
    private Context mContext = null;
    /* access modifiers changed from: private */
    public int mCurrentFps = 0;
    /* access modifiers changed from: private */
    public int mCustomRotation = 0;
    /* access modifiers changed from: private */
    public int mDataOrientation;
    private int mDestHeight;
    private int mDestWidth;
    private boolean mFlashOn = false;
    /* access modifiers changed from: private */
    public long mLastCaptureTime = 0;
    /* access modifiers changed from: private */
    public long mLastFpsCountTime = 0;
    /* access modifiers changed from: private */
    public int mLastFpsCounter = 0;
    /* access modifiers changed from: private */
    public float mLastXAccel = -1.0f;
    /* access modifiers changed from: private */
    public float mLastXMag = -1.0f;
    /* access modifiers changed from: private */
    public float mLastYAccel = -1.0f;
    /* access modifiers changed from: private */
    public float mLastYMag = -1.0f;
    /* access modifiers changed from: private */
    public float mLastZAccel = -1.0f;
    /* access modifiers changed from: private */
    public float mLastZMag = -1.0f;
    private Sensor mMagneticSensor = null;
    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            long currentTimeMillis = System.currentTimeMillis();
            if (VideoPusher.this.mVideoSourceTextureListener != null) {
                if (VideoPusher.this.mTimeDelta == 0) {
                    long unused = VideoPusher.this.mTimeDelta = (System.currentTimeMillis() * 1000) - (System.nanoTime() / 1000);
                }
                if (VideoPusher.this.mParam != null) {
                    if (VideoPusher.this.mCustomRotation > 0) {
                        VideoPusher.this.mVideoSourceTextureListener.onVideoFrame(((System.nanoTime() / 1000) + VideoPusher.this.mTimeDelta) / 1, VideoPusher.this.mParam.getCameraId(), VideoPusher.this.mCustomRotation, VideoPusher.this.mParam.getWidth(), VideoPusher.this.mParam.getHeight(), 17);
                    } else {
                        VideoPusher.this.mVideoSourceTextureListener.onVideoFrame(((System.nanoTime() / 1000) + VideoPusher.this.mTimeDelta) / 1, VideoPusher.this.mParam.getCameraId(), VideoPusher.this.mDataOrientation, VideoPusher.this.mParam.getWidth(), VideoPusher.this.mParam.getHeight(), 17);
                    }
                }
            }
            long unused2 = VideoPusher.this.mLastCaptureTime = System.currentTimeMillis();
            VideoPusher.access$708(VideoPusher.this);
        }
    };
    private int mOrientation;
    /* access modifiers changed from: private */
    public VideoParam mParam;
    /* access modifiers changed from: private */
    public boolean mPause = false;
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera camera2 = camera;
            long currentTimeMillis = System.currentTimeMillis();
            if (VideoPusher.this.mVideoSourceListener != null) {
                if (VideoPusher.this.mTimeDelta == 0) {
                    long unused = VideoPusher.this.mTimeDelta = (System.currentTimeMillis() * 1000) - (System.nanoTime() / 1000);
                }
                if (!VideoPusher.this.mPause && VideoPusher.this.mParam != null) {
                    if (VideoPusher.this.mCustomRotation > 0) {
                        VideoPusher.this.mVideoSourceListener.onVideoFrame(data, ((System.nanoTime() / 1000) + VideoPusher.this.mTimeDelta) / 1, VideoPusher.this.mParam.getCameraId(), VideoPusher.this.mCustomRotation, VideoPusher.this.mParam.getWidth(), VideoPusher.this.mParam.getHeight(), 17);
                    } else {
                        VideoPusher.this.mVideoSourceListener.onVideoFrame(data, ((System.nanoTime() / 1000) + VideoPusher.this.mTimeDelta) / 1, VideoPusher.this.mParam.getCameraId(), VideoPusher.this.mDataOrientation, VideoPusher.this.mParam.getWidth(), VideoPusher.this.mParam.getHeight(), 17);
                    }
                }
            }
            long unused2 = VideoPusher.this.mLastCaptureTime = System.currentTimeMillis();
            VideoPusher.access$708(VideoPusher.this);
            if (VideoPusher.this.mLastFpsCountTime == 0) {
                VideoPusher videoPusher = VideoPusher.this;
                long unused3 = videoPusher.mLastFpsCountTime = videoPusher.mLastCaptureTime;
            }
            if (VideoPusher.this.mLastCaptureTime - VideoPusher.this.mLastFpsCountTime > 1000) {
                Log.d(VideoPusher.TAG, "StatLog: video capture fps = " + VideoPusher.this.mLastFpsCounter);
                VideoPusher videoPusher2 = VideoPusher.this;
                int unused4 = videoPusher2.mCurrentFps = videoPusher2.mLastFpsCounter;
                if (VideoPusher.this.mLastFpsCounter >= 12) {
                    int unused5 = VideoPusher.this.mBelowMinFpsNumberTimes = 0;
                } else if (VideoPusher.this.mBelowMinFpsNumberTimes > 5) {
                    int unused6 = VideoPusher.this.mBelowMinFpsNumberTimes = 0;
                } else {
                    VideoPusher.access$1208(VideoPusher.this);
                }
                int unused7 = VideoPusher.this.mLastFpsCounter = 0;
                VideoPusher videoPusher3 = VideoPusher.this;
                long unused8 = videoPusher3.mLastFpsCountTime = videoPusher3.mLastCaptureTime;
            }
            if (camera2 != null) {
                camera2.addCallbackBuffer(data);
            } else {
                byte[] bArr = data;
            }
        }
    };
    private boolean mPreviewRunning;
    private int mScreen;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (VideoPusher.this.isPreviewRunning() && VideoPusher.this.mParam.getCameraId() == 0) {
                if (event.sensor.getType() == 2) {
                    if (VideoPusher.this.mLastXMag == -1.0f) {
                        float unused = VideoPusher.this.mLastXMag = event.values[0];
                        float unused2 = VideoPusher.this.mLastYMag = event.values[1];
                        float unused3 = VideoPusher.this.mLastZMag = event.values[2];
                        return;
                    }
                    float deltaX = Math.abs(VideoPusher.this.mLastXMag - event.values[0]);
                    float deltaY = Math.abs(VideoPusher.this.mLastYMag - event.values[1]);
                    float deltaZ = Math.abs(VideoPusher.this.mLastZMag - event.values[2]);
                    if ((deltaX > 5.0f || deltaY > 5.0f || deltaZ > 5.0f) && !VideoPusher.this.mAutoFocusing) {
                        cameraAutoFocus();
                        float unused4 = VideoPusher.this.mLastXMag = event.values[0];
                        float unused5 = VideoPusher.this.mLastYMag = event.values[1];
                        float unused6 = VideoPusher.this.mLastZMag = event.values[2];
                    }
                }
                if (event.sensor.getType() != 1) {
                    return;
                }
                if (VideoPusher.this.mLastXAccel == -1.0f) {
                    float unused7 = VideoPusher.this.mLastXAccel = event.values[0];
                    float unused8 = VideoPusher.this.mLastYAccel = event.values[1];
                    float unused9 = VideoPusher.this.mLastZAccel = event.values[2];
                    return;
                }
                float deltaX2 = Math.abs(VideoPusher.this.mLastXAccel - event.values[0]);
                float deltaY2 = Math.abs(VideoPusher.this.mLastYAccel - event.values[1]);
                float deltaZ2 = Math.abs(VideoPusher.this.mLastZAccel - event.values[2]);
                if (Math.sqrt((double) ((deltaX2 * deltaX2) + (deltaY2 * deltaY2) + (deltaZ2 * deltaZ2))) > 1.399999976158142d && !VideoPusher.this.mAutoFocusing) {
                    cameraAutoFocus();
                    float unused10 = VideoPusher.this.mLastXAccel = event.values[0];
                    float unused11 = VideoPusher.this.mLastYAccel = event.values[1];
                    float unused12 = VideoPusher.this.mLastZAccel = event.values[2];
                } else if ((deltaX2 > 0.6f || deltaY2 > 0.6f || deltaZ2 > 0.6f) && !VideoPusher.this.mAutoFocusing) {
                    cameraAutoFocus();
                    float unused13 = VideoPusher.this.mLastXAccel = event.values[0];
                    float unused14 = VideoPusher.this.mLastYAccel = event.values[1];
                    float unused15 = VideoPusher.this.mLastZAccel = event.values[2];
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        private void cameraAutoFocus() {
            boolean unused = VideoPusher.this.mAutoFocusing = true;
            if (!VideoPusher.this.mCamera.getParameters().getFocusMode().equals("continuous-video")) {
                if (!VideoPusher.this.mCamera.getParameters().getFocusMode().equals("auto")) {
                    try {
                        Camera.Parameters parameters = VideoPusher.this.mCamera.getParameters();
                        parameters.setFocusMode("auto");
                        VideoPusher.this.mCamera.setParameters(parameters);
                    } catch (Exception e) {
                    }
                }
                VideoPusher.this.mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    public void onAutoFocus(boolean success, Camera camera) {
                        boolean unused = VideoPusher.this.mAutoFocusing = false;
                    }
                });
            }
        }
    };
    private boolean mSensorFocus = false;
    private SensorManager mSensorManager = null;
    private boolean mSurfaceCbMode = false;
    private SurfaceTexture mSurfaceTexture = null;
    private int mSurfaceTextureId = -1;
    private boolean mSwitchCamera = false;
    /* access modifiers changed from: private */
    public long mTimeDelta = 0;
    /* access modifiers changed from: private */
    public VideoSourceListener mVideoSourceListener = null;
    /* access modifiers changed from: private */
    public VideoSourceTextureListener mVideoSourceTextureListener = null;

    public interface VideoSourceListener {
        void onVideoFrame(byte[] bArr, long j, int i, int i2, int i3, int i4, int i5);
    }

    public interface VideoSourceTextureListener {
        void onVideoFrame(long j, int i, int i2, int i3, int i4, int i5);
    }

    static /* synthetic */ int access$1208(VideoPusher x0) {
        int i = x0.mBelowMinFpsNumberTimes;
        x0.mBelowMinFpsNumberTimes = i + 1;
        return i;
    }

    static /* synthetic */ int access$708(VideoPusher x0) {
        int i = x0.mLastFpsCounter;
        x0.mLastFpsCounter = i + 1;
        return i;
    }

    public void init(int source, int width, int height, int fps, int rotationMode, int customRotation, boolean surfaceCBMode, boolean focusBySensor, Context context) {
        SensorManager sensorManager;
        SensorManager sensorManager2;
        boolean z = surfaceCBMode;
        Context context2 = context;
        Context applicationContext = context2 != null ? context.getApplicationContext() : null;
        this.mContext = applicationContext;
        this.mCameraDisConnected = false;
        if (applicationContext instanceof Application) {
            ((Application) applicationContext).registerActivityLifecycleCallbacks(this.mActivityCallbacks);
        }
        int rotation = convertRotationMode(rotationMode);
        this.mParam = new VideoParam(width, height, fps, source, rotation);
        this.mSurfaceCbMode = z;
        this.mCustomRotation = customRotation;
        if (!z) {
            this.mSurfaceTexture = new SurfaceTexture(10);
        }
        if (this.mSensorManager == null && context2 != null) {
            this.mSensorManager = (SensorManager) context2.getSystemService("sensor");
        }
        if (this.mAccelSensor == null && (sensorManager2 = this.mSensorManager) != null) {
            this.mAccelSensor = sensorManager2.getDefaultSensor(1);
        }
        if (this.mMagneticSensor == null && (sensorManager = this.mSensorManager) != null) {
            this.mMagneticSensor = sensorManager.getDefaultSensor(2);
        }
        if (Build.MODEL.contains("MI MAX") || "MIX".equals(Build.MODEL) || "MIX 2".equals(Build.MODEL)) {
            this.mSensorFocus = true;
        }
        if (focusBySensor) {
            this.mSensorFocus = true;
            Log.d(TAG, "set focus by Sensor");
        }
        this.mDestWidth = this.mParam.getWidth();
        this.mDestHeight = this.mParam.getHeight();
        StringBuilder sb = new StringBuilder();
        sb.append("====> Init src: ");
        int i = source;
        sb.append(source);
        sb.append(", width: ");
        int i2 = width;
        sb.append(width);
        sb.append(", height:");
        sb.append(height);
        sb.append(", fps:");
        sb.append(fps);
        sb.append(", rotation:");
        sb.append(rotation);
        sb.append(", mSurfaceCbMode: ");
        sb.append(this.mSurfaceCbMode);
        Log.d(TAG, sb.toString());
    }

    private int convertRotationMode(int mode) {
        if (mode == 0) {
            return 0;
        }
        if (mode == 1) {
            return SCREEN_LANDSCAPE_RIGHT;
        }
        if (mode != 2) {
            return 0;
        }
        return 90;
    }

    private void setPreviewAdvancedParameters(Camera.Parameters parameters) {
        List<String> whiteBalances = parameters.getSupportedWhiteBalance();
        if (whiteBalances != null && whiteBalances.contains("auto")) {
            Log.d(TAG, "Contains Camera.Parameters.WHITE_BALANCE_AUTO");
            parameters.setWhiteBalance("auto");
        }
        List<String> antiBandings = parameters.getSupportedAntibanding();
        if (antiBandings != null && antiBandings.contains("auto")) {
            Log.d(TAG, "Contains Camera.Parameters.ANTIBANDING_AUTO");
            parameters.setAntibanding("auto");
        }
        List<String> sceneModes = parameters.getSupportedSceneModes();
        if (!(sceneModes == null || !sceneModes.contains("auto") || parameters.getSceneMode() == "auto")) {
            Log.d(TAG, "Contains getSupportedSceneModes auto");
            parameters.setSceneMode("auto");
        }
        String mauFacture = Build.MANUFACTURER.toLowerCase();
        String model = Build.MODEL.toLowerCase();
        if (mauFacture.equals("xiaomi") && model.contains("mi note")) {
            parameters.set("scene-detect", "on");
            parameters.set("xiaomi-still-beautify-values", "i:3");
            parameters.set("skinToneEnhancement", "enable");
            parameters.set("auto-exposure", "center-weighted");
        }
        if (mauFacture.equals("oppo") && model.contains("r7c")) {
            parameters.set("skinToneEnhancement", 1);
            parameters.set("face-beautify", 100);
            parameters.set("auto-exposure", "center-weighted");
        }
    }

    private static void turnLightOn(Camera mCamera2) {
        Camera.Parameters parameters;
        List<String> flashModes;
        if (mCamera2 != null && (parameters = mCamera2.getParameters()) != null && (flashModes = parameters.getSupportedFlashModes()) != null && !"torch".equals(parameters.getFlashMode()) && flashModes.contains("torch")) {
            parameters.setFlashMode("torch");
            try {
                mCamera2.setParameters(parameters);
            } catch (Throwable th) {
                Log.e(TAG, "VideoPusherJNI set flash mode on failed");
            }
        }
    }

    private static void turnLightOff(Camera mCamera2) {
        Camera.Parameters parameters;
        if (mCamera2 != null && (parameters = mCamera2.getParameters()) != null) {
            List<String> flashModes = parameters.getSupportedFlashModes();
            String flashMode = parameters.getFlashMode();
            if (flashModes == null || "off".equals(flashMode)) {
                return;
            }
            if (flashModes.contains("off")) {
                parameters.setFlashMode("off");
                try {
                    mCamera2.setParameters(parameters);
                } catch (Throwable th) {
                    Log.e(TAG, "VideoPusherJNI set flash off mode failed");
                }
            } else {
                Log.e(TAG, "FLASH_MODE_OFF not supported");
            }
        }
    }

    public void destroy() {
        Log.d(TAG, "destroy.");
        Context context = this.mContext;
        if (context instanceof Application) {
            ((Application) context).unregisterActivityLifecycleCallbacks(this.mActivityCallbacks);
        }
        SurfaceTexture surfaceTexture = this.mSurfaceTexture;
        if (surfaceTexture != null) {
            surfaceTexture.release();
        }
        this.mSurfaceTexture = null;
        this.mParam = null;
        this.mContext = null;
    }

    public void switchCamera() throws Exception {
        Log.d(TAG, "switchCamera.");
        stopInner(false);
        if (this.mParam.getCameraId() == 0) {
            this.mParam.setCameraId(1);
        } else {
            this.mParam.setCameraId(0);
        }
        startInner();
    }

    public void setOrientation(int orientation) throws Exception {
        Log.d(TAG, "setOrientation.");
    }

    public void stopInner(boolean endStoped) {
        Camera camera;
        Log.d(TAG, "stopInner.");
        SensorManager sensorManager = this.mSensorManager;
        if (sensorManager != null && this.mSensorFocus) {
            try {
                sensorManager.unregisterListener(this.mSensorEventListener);
            } catch (Exception e) {
            }
        }
        if (this.mPreviewRunning && (camera = this.mCamera) != null) {
            try {
                camera.stopPreview();
                this.mCamera.setPreviewCallback((Camera.PreviewCallback) null);
                if (endStoped && ((Build.MANUFACTURER.equalsIgnoreCase("Google") && Build.MODEL.equals("Pixel 2")) || ((Build.MANUFACTURER.equalsIgnoreCase("motorola") && Build.MODEL.equals("Nexus 6")) || (Build.MANUFACTURER.equalsIgnoreCase("LGE") && Build.MODEL.equals("Nexus 5"))))) {
                    try {
                        this.mCamera.unlock();
                    } catch (Throwable th) {
                    }
                }
                this.mCamera.release();
            } catch (Throwable th2) {
            }
            this.mCamera = null;
            this.mPreviewRunning = false;
            Log.d(TAG, "stopInner over.");
        }
    }

    public void stop() {
        SurfaceTexture surfaceTexture;
        Log.d(TAG, "stop.");
        stopInner(true);
        if (this.mSurfaceCbMode && (surfaceTexture = this.mSurfaceTexture) != null) {
            surfaceTexture.release();
            this.mSurfaceTexture = null;
            this.mSurfaceTextureId = -1;
        }
    }

    public void start(int surfaceId) throws Exception {
        Log.d(TAG, "start.");
        if (this.mSurfaceCbMode && surfaceId >= 0) {
            this.mSurfaceTextureId = surfaceId;
            SurfaceTexture surfaceTexture = this.mSurfaceTexture;
            if (surfaceTexture != null) {
                surfaceTexture.release();
                this.mSurfaceTexture = null;
            }
            this.mSurfaceTexture = new SurfaceTexture(this.mSurfaceTextureId);
        } else if (this.mSurfaceCbMode && surfaceId < 0) {
            this.mSurfaceCbMode = false;
            if (this.mSurfaceTexture == null) {
                this.mSurfaceTexture = new SurfaceTexture(10);
            }
        }
        try {
            startInner();
        } catch (Exception e) {
            throw e;
        }
    }

    public void startInner() throws Exception {
        boolean isStartPreviewed;
        try {
            isStartPreviewed = startPreview0();
        } catch (Throwable th) {
            stopInner(false);
            try {
                isStartPreviewed = startPreview0();
            } catch (Exception e) {
                throw e;
            }
        }
        if (isStartPreviewed) {
            boolean z = this.mFlashOn;
            if (z) {
                setFlashOn(z);
            }
            boolean z2 = this.mAutoFocus;
            if (z2) {
                setAutoFocus(z2);
            }
            SensorManager sensorManager = this.mSensorManager;
            if (sensorManager != null && this.mSensorFocus) {
                sensorManager.registerListener(this.mSensorEventListener, this.mAccelSensor, 2);
                this.mSensorManager.registerListener(this.mSensorEventListener, this.mMagneticSensor, 2);
            }
        }
    }

    private boolean isHasPermission() {
        try {
            Field filedPass = this.mCamera.getClass().getDeclaredField("mHasPermission");
            filedPass.setAccessible(true);
            return ((Boolean) filedPass.get(this.mCamera)).booleanValue();
        } catch (Exception e) {
            return true;
        }
    }

    private boolean startPreview0() throws Exception {
        if (this.mPreviewRunning) {
            return true;
        }
        if (!isHasPermission()) {
            return false;
        }
        try {
            Camera open = Camera.open(this.mParam.getCameraId());
            this.mCamera = open;
            if (open != null) {
                try {
                    open.setErrorCallback(this);
                } catch (Exception e) {
                }
                try {
                    Camera.Parameters parameters = this.mCamera.getParameters();
                    if (!sSupportedResolutionMap.containsKey(Integer.valueOf(this.mParam.getCameraId()))) {
                        sSupportedResolutionMap.put(Integer.valueOf(this.mParam.getCameraId()), parameters.getSupportedPreviewSizes());
                    }
                    if (sSupportedFormat.size() <= 0) {
                        sSupportedFormat = parameters.getSupportedPictureFormats();
                    }
                    if (isHasPermission()) {
                        if (!this.mSurfaceCbMode) {
                            parameters.setPreviewFormat(17);
                        } else {
                            parameters.setRecordingHint(true);
                        }
                        if (parameters.isZoomSupported()) {
                            if (this.mParam.getCurrentZoom() >= parameters.getMaxZoom()) {
                                this.mParam.setCurrentZoom(parameters.getMaxZoom());
                            }
                            parameters.setZoom(this.mParam.getCurrentZoom());
                            this.mParam.setMaxZoom(parameters.getMaxZoom());
                        }
                        setPreviewOrientation(parameters, this.mParam.getRotation());
                        int i = this.mDataOrientation;
                        if (i == 90 || i == SCREEN_LANDSCAPE_RIGHT) {
                            this.mParam.setWidth(this.mDestHeight);
                            this.mParam.setHeight(this.mDestWidth);
                        }
                        setPreviewSize(parameters);
                        setPreviewFpsRange(parameters);
                        try {
                            this.mCamera.setParameters(parameters);
                        } catch (Exception e2) {
                        }
                        Log.d(TAG, "start camera, parameters " + parameters.getPreviewSize().width + ", " + parameters.getPreviewSize().height);
                        byte[] bArr = buffer;
                        if (bArr == null) {
                            buffer = new byte[(((this.mParam.getWidth() * this.mParam.getHeight()) * 3) / 2)];
                        } else if (bArr.length < ((this.mParam.getWidth() * this.mParam.getHeight()) * 3) / 2) {
                            buffer = new byte[(((this.mParam.getWidth() * this.mParam.getHeight()) * 3) / 2)];
                        }
                        byte[] bArr2 = buffer1;
                        if (bArr2 == null) {
                            buffer1 = new byte[(((this.mParam.getWidth() * this.mParam.getHeight()) * 3) / 2)];
                        } else if (bArr2.length < ((this.mParam.getWidth() * this.mParam.getHeight()) * 3) / 2) {
                            buffer1 = new byte[(((this.mParam.getWidth() * this.mParam.getHeight()) * 3) / 2)];
                        }
                        byte[] bArr3 = buffer2;
                        if (bArr3 == null) {
                            buffer2 = new byte[(((this.mParam.getWidth() * this.mParam.getHeight()) * 3) / 2)];
                        } else if (bArr3.length < ((this.mParam.getWidth() * this.mParam.getHeight()) * 3) / 2) {
                            buffer2 = new byte[(((this.mParam.getWidth() * this.mParam.getHeight()) * 3) / 2)];
                        }
                        if (!this.mSurfaceCbMode || this.mSurfaceTextureId < 0) {
                            this.mCamera.setPreviewCallbackWithBuffer((Camera.PreviewCallback) null);
                            this.mCamera.addCallbackBuffer(buffer);
                            this.mCamera.addCallbackBuffer(buffer1);
                            this.mCamera.addCallbackBuffer(buffer2);
                            this.mCamera.setPreviewCallbackWithBuffer(this.mPreviewCallback);
                        } else {
                            this.mSurfaceTexture.setOnFrameAvailableListener(this.mOnFrameAvailableListener);
                        }
                        this.mCamera.setPreviewTexture(this.mSurfaceTexture);
                        this.mCamera.startPreview();
                        this.mPreviewRunning = true;
                        this.mCameraDisConnected = false;
                        Log.d(TAG, "start preivew over.");
                        return true;
                    }
                    this.mCamera.setPreviewCallback((Camera.PreviewCallback) null);
                    this.mCamera.release();
                    this.mCamera = null;
                    throw new Exception("permission not allowed");
                } catch (Exception e3) {
                    this.mCamera.setPreviewCallback((Camera.PreviewCallback) null);
                    this.mCamera.release();
                    this.mCamera = null;
                    throw new Exception("permission not allowed");
                }
            } else {
                throw new Exception("permission not allowed");
            }
        } catch (Exception e4) {
            Camera camera = this.mCamera;
            if (camera != null) {
                camera.setPreviewCallback((Camera.PreviewCallback) null);
                this.mCamera.release();
            }
            this.mCamera = null;
            throw new Exception("permission not allowed");
        }
    }

    public void pause(boolean useEmptyData) {
        Log.d(TAG, "pause preview.");
        this.mPause = true;
    }

    public void resume() {
        Log.d(TAG, "resume preview.");
        if (this.mPause) {
            stopInner(false);
            try {
                startInner();
                this.mPause = false;
            } catch (Exception e) {
                Log.e("VideoPusherJNI", "VideoPusherJNI resume Failed");
            }
        }
    }

    private void setPreviewSize(Camera.Parameters parameters) {
        int i;
        int i2;
        int i3;
        for (Integer integer : parameters.getSupportedPreviewFormats()) {
            System.out.println("支持:" + integer);
        }
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size greaterClosePreviewSize = null;
        int greaterCloseArea = 0;
        Camera.Size size = null;
        Camera.Size rotateSize = null;
        for (Camera.Size next : supportedPreviewSizes) {
            Log.d(TAG, "支持 " + next.width + "x" + next.height);
            if (next.width == this.mParam.getWidth() && next.height == this.mParam.getHeight()) {
                size = next;
            }
            if (next.width == this.mParam.getHeight() && next.height == this.mParam.getWidth()) {
                rotateSize = next;
            }
            if ((next.width >= this.mParam.getWidth() && next.height >= this.mParam.getHeight()) || (next.width >= this.mParam.getHeight() && next.height >= this.mParam.getWidth())) {
                if (greaterCloseArea == 0) {
                    greaterClosePreviewSize = next;
                    greaterCloseArea = next.width * next.height;
                } else if (next.width * next.height < greaterCloseArea) {
                    greaterClosePreviewSize = next;
                    greaterCloseArea = next.width * next.height;
                } else if (next.width * next.height == greaterCloseArea && ((next.width < next.height && this.mParam.getWidth() < this.mParam.getHeight()) || (next.width > next.height && this.mParam.getWidth() > this.mParam.getHeight()))) {
                    greaterClosePreviewSize = next;
                }
            }
            int i4 = next.width * next.height;
            int i5 = supportMaxWH;
            if (i4 > i5) {
                i5 = next.height * next.width;
            }
            supportMaxWH = i5;
        }
        if (size == null && rotateSize != null) {
            size = rotateSize;
        }
        if (size == null && 0 != 0) {
            size = null;
        }
        if (size == null && greaterClosePreviewSize != null) {
            size = greaterClosePreviewSize;
        }
        if (size == null) {
            size = supportedPreviewSizes.get(0);
        }
        if (buffer == null && (i3 = supportMaxWH) > 0) {
            buffer = new byte[((i3 * 3) / 2)];
        }
        if (buffer1 == null && (i2 = supportMaxWH) > 0) {
            buffer1 = new byte[((i2 * 3) / 2)];
        }
        if (buffer2 == null && (i = supportMaxWH) > 0) {
            buffer2 = new byte[((i * 3) / 2)];
        }
        this.mParam.setWidth(size.width);
        this.mParam.setHeight(size.height);
        try {
            parameters.setPreviewSize(this.mParam.getWidth(), this.mParam.getHeight());
        } catch (Exception e) {
        }
        Log.d(TAG, "预览分辨率 width:" + this.mParam.getWidth() + " height:" + this.mParam.getHeight());
    }

    private void setPreviewFpsRange(Camera.Parameters parameters) {
        int targetFps = this.mParam.getFps() * 1000;
        int minRange = 0;
        List<int[]> supportedFpsRanges = parameters.getSupportedPreviewFpsRange();
        int[] targetFpsRange = new int[2];
        if (supportedFpsRanges.size() > 0) {
            int[] range = supportedFpsRanges.get(0);
            minRange = Math.abs(range[0] - targetFps) + Math.abs(range[1] - targetFps);
            targetFpsRange[0] = range[0];
            targetFpsRange[1] = range[1];
        }
        for (int i = 1; i < supportedFpsRanges.size(); i++) {
            int[] range2 = supportedFpsRanges.get(i);
            int currentRange = Math.abs(range2[0] - targetFps) + Math.abs(range2[1] - targetFps);
            if (currentRange < minRange) {
                targetFpsRange[0] = range2[0];
                targetFpsRange[1] = range2[1];
                minRange = currentRange;
            }
        }
        try {
            if (!Build.BRAND.equals("Coolpad") && !Build.BRAND.equals("360") && !Build.BRAND.equals("YOTA") && !Build.MODEL.contains("Redmi")) {
                parameters.setPreviewFpsRange(targetFpsRange[0], targetFpsRange[1]);
                parameters.setPreviewFrameRate(this.mParam.getFps());
            }
        } catch (Exception e) {
        }
        Log.d(TAG, "预览帧率 fps:" + targetFpsRange[0] + " - " + targetFpsRange[1]);
    }

    private void preparePublisher(int pushWidth, int pushHeight) {
        Log.d(TAG, "prepare publisher. " + this.mOrientation + " " + this.mParam.getCameraId());
        if (this.mSwitchCamera || this.mPause) {
            this.mSwitchCamera = false;
        } else {
            Log.d(TAG, "prepare publisher over.");
        }
    }

    private void setPreviewOrientation(Camera.Parameters parameters, int rotation) {
        Log.d(TAG, "SetRotation : " + rotation);
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(this.mParam.getCameraId(), info);
        this.mScreen = 0;
        this.mOrientation = this.mParam.getRotation();
        if (info.facing == 1) {
            this.mDataOrientation = (info.orientation + this.mOrientation) % 360;
        } else {
            this.mDataOrientation = ((info.orientation - this.mOrientation) + 360) % 360;
        }
        this.mCamera.setDisplayOrientation(this.mDataOrientation);
    }

    public void setFlashOn(boolean flash) {
        if (this.mCamera != null) {
            if (!flash || this.mParam.getCameraId() != 0) {
                turnLightOff(this.mCamera);
            } else {
                turnLightOn(this.mCamera);
            }
        }
        this.mFlashOn = flash;
    }

    public void setAutoFocus(boolean autoFocus) {
        if (Build.MODEL.contains("OPPO A79k")) {
            autoFocus = false;
        }
        if (this.mCamera != null && this.mParam.getCameraId() == 0) {
            this.mCamera.cancelAutoFocus();
            Camera.Parameters parameters = this.mCamera.getParameters();
            if (!autoFocus) {
                parameters.setFocusMode("auto");
            } else if (this.mSensorFocus) {
                parameters.setFocusMode("auto");
                this.mCamera.autoFocus((Camera.AutoFocusCallback) null);
            } else {
                parameters.setFocusMode("continuous-video");
            }
            try {
                this.mCamera.setParameters(parameters);
            } catch (Throwable th) {
                Log.e(TAG, "VideoPusherJNI set autofocus " + autoFocus + " mode failed");
            }
        }
        this.mAutoFocus = autoFocus;
    }

    public boolean isSupportAutoFocus() {
        Camera camera = this.mCamera;
        if (camera == null) {
            return false;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes() == null || parameters.getSupportedFocusModes().size() <= 0) {
            return false;
        }
        return parameters.getSupportedFocusModes().contains("continuous-video");
    }

    public boolean isSupportFlash() {
        Camera camera = this.mCamera;
        if (camera == null) {
            return false;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFlashModes() == null || parameters.getSupportedFlashModes().size() <= 0) {
            return false;
        }
        return parameters.getSupportedFlashModes().contains("torch");
    }

    public void setExposure(int exposure) {
        Camera camera = this.mCamera;
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (exposure >= parameters.getMinExposureCompensation() && exposure <= parameters.getMaxExposureCompensation()) {
                parameters.setExposureCompensation(this.mParam.getCurrentZoom());
            }
        }
    }

    public int getCameraSource() {
        return this.mParam.getCameraId();
    }

    public int getCurrentExposure() {
        Camera camera = this.mCamera;
        if (camera != null) {
            return camera.getParameters().getExposureCompensation();
        }
        return 0;
    }

    public void setZoom(int zoom) {
        Camera camera = this.mCamera;
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters.isZoomSupported() && zoom >= 0 && zoom <= parameters.getMaxZoom()) {
                this.mParam.setCurrentZoom(zoom);
                parameters.setZoom(this.mParam.getCurrentZoom());
                try {
                    this.mCamera.setParameters(parameters);
                } catch (Throwable th) {
                    Log.e(TAG, "VideoPusherJNI set zoom " + zoom + " failed");
                }
            }
        }
    }

    public int getCurrentZoom() {
        Camera camera = this.mCamera;
        if (camera == null) {
            return 1;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.isZoomSupported()) {
            return parameters.getZoom();
        }
        return 1;
    }

    public int getMaxZoom() {
        Camera camera = this.mCamera;
        if (camera == null) {
            return 0;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.isZoomSupported()) {
            return parameters.getMaxZoom();
        }
        return 0;
    }

    public void setZoom(float scaleFactor) {
        Camera camera = this.mCamera;
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters.isZoomSupported()) {
                int zoomScaled = (int) (((float) this.mParam.getCurrentZoom()) * scaleFactor);
                if (zoomScaled <= 1) {
                    this.mParam.setCurrentZoom(1);
                } else if (zoomScaled >= parameters.getMaxZoom()) {
                    this.mParam.setCurrentZoom(parameters.getMaxZoom());
                } else {
                    this.mParam.setCurrentZoom(zoomScaled);
                }
                parameters.setZoom(this.mParam.getCurrentZoom());
                try {
                    this.mCamera.setParameters(parameters);
                } catch (Throwable th) {
                    Log.e(TAG, "VideoPusherJNI set zoom failed");
                }
            }
        }
    }

    public boolean isSupportFocusPoint() {
        Camera.Parameters parameters;
        Camera camera = this.mCamera;
        if (camera == null || (parameters = camera.getParameters()) == null || parameters.getMaxNumFocusAreas() <= 0) {
            return false;
        }
        return true;
    }

    public boolean isSupportExposurePoint() {
        Camera.Parameters parameters;
        Camera camera = this.mCamera;
        if (camera == null || (parameters = camera.getParameters()) == null || parameters.getMaxNumMeteringAreas() <= 0) {
            return false;
        }
        return true;
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

    public void setFocus(float xRatio, float yRatio) {
        Camera camera = this.mCamera;
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            Log.d(TAG, "focusAreas is " + parameters.getMaxNumFocusAreas());
            if (parameters == null || parameters.getMaxNumFocusAreas() != 0) {
                parameters.setFocusMode("auto");
                int left = clamp(((int) ((xRatio * 2000.0f) - 1000.0f)) - (ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION / 2), -1000, 1000);
                int top = clamp(((int) ((2000.0f * yRatio) - 1000.0f)) - (ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION / 2), -1000, 1000);
                Camera.Area area = new Camera.Area(new Rect(left, top, clamp((ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION / 2) + left, -1000, 1000), clamp((ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION / 2) + top, -1000, 1000)), 1);
                List<Camera.Area> areas = new ArrayList<>();
                areas.add(area);
                parameters.setFocusAreas(areas);
                try {
                    this.mCamera.setParameters(parameters);
                } catch (Throwable th) {
                    Log.e(TAG, "VideoPusherJNI set focus area failed");
                }
                this.mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    public void onAutoFocus(boolean success, Camera camera) {
                        camera.cancelAutoFocus();
                    }
                });
            }
        }
    }

    public void setExposurePoint(float xRatio, float yRatio) {
        Camera camera = this.mCamera;
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            Log.d(TAG, "MeteringAreas is " + parameters.getMaxNumMeteringAreas());
            if (parameters == null || parameters.getMaxNumMeteringAreas() != 0) {
                int left = clamp(((int) ((xRatio * 2000.0f) - 1000.0f)) - (ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION / 2), -1000, 1000);
                int top = clamp(((int) ((2000.0f * yRatio) - 1000.0f)) - (ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION / 2), -1000, 1000);
                Camera.Area area = new Camera.Area(new Rect(left, top, clamp((ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION / 2) + left, -1000, 1000), clamp((ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION / 2) + top, -1000, 1000)), 1);
                List<Camera.Area> areas = new ArrayList<>();
                areas.add(area);
                parameters.setMeteringAreas(areas);
                try {
                    this.mCamera.setParameters(parameters);
                } catch (Throwable th) {
                    Log.e(TAG, "VideoPusherJNI set Metering area failed");
                }
            }
        }
    }

    public void setVideoSourceListener(VideoSourceListener listener) {
        this.mVideoSourceListener = listener;
    }

    public void setVideoSourceTextureListener(VideoSourceTextureListener listener) {
        this.mVideoSourceTextureListener = listener;
    }

    public long getLastCaptureTime() {
        return this.mLastCaptureTime;
    }

    public void setLastCaptureTime(long lastCaptureTime) {
        this.mLastCaptureTime = lastCaptureTime;
    }

    public int getCurrentFps() {
        return this.mCurrentFps;
    }

    public int updateTexImage() {
        SurfaceTexture surfaceTexture = this.mSurfaceTexture;
        if (surfaceTexture == null) {
            return -1;
        }
        surfaceTexture.updateTexImage();
        return 0;
    }

    public void getTransformMatrix(float[] matrix) {
        SurfaceTexture surfaceTexture = this.mSurfaceTexture;
        if (surfaceTexture == null) {
            Log.d("VideoPusherRotation", "getTransformMatrix return null !");
        } else {
            surfaceTexture.getTransformMatrix(matrix);
        }
    }

    public static List<Camera.Size> getSupportedResolutions(int source) {
        if (!sSupportedResolutionMap.containsKey(Integer.valueOf(source))) {
            try {
                Camera camera = Camera.open(source);
                if (camera == null) {
                    return null;
                }
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    camera.setPreviewCallback((Camera.PreviewCallback) null);
                    camera.release();
                    List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                    Collections.sort(sizes, new Comparator<Camera.Size>() {
                        public int compare(Camera.Size a, Camera.Size b) {
                            return (a.width * a.height) - (b.width * b.height);
                        }
                    });
                    sSupportedResolutionMap.put(Integer.valueOf(source), sizes);
                } catch (Exception e) {
                    camera.setPreviewCallback((Camera.PreviewCallback) null);
                    camera.release();
                    return null;
                }
            } catch (Exception e2) {
                return null;
            }
        }
        return sSupportedResolutionMap.get(Integer.valueOf(source));
    }

    public static List<Integer> getSupportedFormats() {
        if (sSupportedFormat.size() <= 0) {
            Camera camera = null;
            try {
                Camera camera2 = Camera.open(0);
                if (camera2 == null) {
                    return null;
                }
                try {
                    Camera.Parameters parameters = camera2.getParameters();
                    camera2.setPreviewCallback((Camera.PreviewCallback) null);
                    camera2.release();
                    sSupportedFormat = parameters.getSupportedPreviewFormats();
                } catch (Exception e) {
                    camera2.setPreviewCallback((Camera.PreviewCallback) null);
                    camera2.release();
                    return null;
                }
            } catch (Exception e2) {
                if (camera != null) {
                    camera.setPreviewCallback((Camera.PreviewCallback) null);
                    camera.release();
                }
                return null;
            }
        }
        return sSupportedFormat;
    }

    public boolean isPreviewRunning() {
        return this.mPreviewRunning;
    }

    public void onError(int error, Camera camera) {
        if (error == 2 || error == 1) {
            this.mCameraDisConnected = true;
        }
    }
}
