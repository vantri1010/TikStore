package im.bclpbkiauv.messenger.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import java.util.ArrayList;

public class CameraSession {
    public static final int ORIENTATION_HYSTERESIS = 5;
    private Camera.AutoFocusCallback autoFocusCallback = $$Lambda$CameraSession$nX4L5l5pahrtHloWqToeSdFva74.INSTANCE;
    protected CameraInfo cameraInfo;
    private String currentFlashMode;
    private int currentOrientation;
    private float currentZoom;
    private int diffOrientation;
    private boolean flipFront = true;
    /* access modifiers changed from: private */
    public boolean initied;
    /* access modifiers changed from: private */
    public boolean isVideo;
    /* access modifiers changed from: private */
    public int jpegOrientation;
    /* access modifiers changed from: private */
    public int lastDisplayOrientation = -1;
    /* access modifiers changed from: private */
    public int lastOrientation = -1;
    private int maxZoom;
    private boolean meteringAreaSupported;
    /* access modifiers changed from: private */
    public OrientationEventListener orientationEventListener;
    private final int pictureFormat;
    private final Size pictureSize;
    private final Size previewSize;
    private boolean sameTakePictureOrientation;

    /*  JADX ERROR: IndexOutOfBoundsException in pass: MethodInlineVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.visitors.MethodInlineVisitor.inlineMth(MethodInlineVisitor.java:57)
        	at jadx.core.dex.visitors.MethodInlineVisitor.visit(MethodInlineVisitor.java:47)
        */
    static /* synthetic */ void lambda$new$0(boolean r0, android.hardware.Camera r1) {
        /*
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.camera.CameraSession.lambda$new$0(boolean, android.hardware.Camera):void");
    }

    public CameraSession(CameraInfo info, Size preview, Size picture, int format) {
        this.previewSize = preview;
        this.pictureSize = picture;
        this.pictureFormat = format;
        this.cameraInfo = info;
        this.currentFlashMode = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).getString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", "off");
        AnonymousClass1 r1 = new OrientationEventListener(ApplicationLoader.applicationContext) {
            public void onOrientationChanged(int orientation) {
                if (CameraSession.this.orientationEventListener != null && CameraSession.this.initied && orientation != -1) {
                    CameraSession cameraSession = CameraSession.this;
                    int unused = cameraSession.jpegOrientation = cameraSession.roundOrientation(orientation, cameraSession.jpegOrientation);
                    int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                    if (CameraSession.this.lastOrientation != CameraSession.this.jpegOrientation || rotation != CameraSession.this.lastDisplayOrientation) {
                        if (!CameraSession.this.isVideo) {
                            CameraSession.this.configurePhotoCamera();
                        }
                        int unused2 = CameraSession.this.lastDisplayOrientation = rotation;
                        CameraSession cameraSession2 = CameraSession.this;
                        int unused3 = cameraSession2.lastOrientation = cameraSession2.jpegOrientation;
                    }
                }
            }
        };
        this.orientationEventListener = r1;
        if (r1.canDetectOrientation()) {
            this.orientationEventListener.enable();
            return;
        }
        this.orientationEventListener.disable();
        this.orientationEventListener = null;
    }

    /* access modifiers changed from: private */
    public int roundOrientation(int orientation, int orientationHistory) {
        int dist;
        if (orientationHistory == -1) {
            dist = 1;
        } else {
            int dist2 = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist2, 360 - dist2) >= 50 ? 1 : 0;
        }
        if (dist != 0) {
            return (((orientation + 45) / 90) * 90) % 360;
        }
        return orientationHistory;
    }

    public void checkFlashMode(String mode) {
        if (!CameraController.getInstance().availableFlashModes.contains(this.currentFlashMode)) {
            this.currentFlashMode = mode;
            configurePhotoCamera();
            ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", mode).commit();
        }
    }

    public void setCurrentFlashMode(String mode) {
        this.currentFlashMode = mode;
        configurePhotoCamera();
        ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", mode).commit();
    }

    public String getCurrentFlashMode() {
        return this.currentFlashMode;
    }

    public String getNextFlashMode() {
        ArrayList<String> modes = CameraController.getInstance().availableFlashModes;
        int a = 0;
        while (a < modes.size()) {
            if (!modes.get(a).equals(this.currentFlashMode)) {
                a++;
            } else if (a < modes.size() - 1) {
                return modes.get(a + 1);
            } else {
                return modes.get(0);
            }
        }
        return this.currentFlashMode;
    }

    public void setInitied() {
        this.initied = true;
    }

    public boolean isInitied() {
        return this.initied;
    }

    public int getCurrentOrientation() {
        return this.currentOrientation;
    }

    public boolean isFlipFront() {
        return this.flipFront;
    }

    public void setFlipFront(boolean value) {
        this.flipFront = value;
    }

    public int getWorldAngle() {
        return this.diffOrientation;
    }

    public boolean isSameTakePictureOrientation() {
        return this.sameTakePictureOrientation;
    }

    /* access modifiers changed from: protected */
    public void configureRoundCamera() {
        int degrees;
        int temp;
        try {
            this.isVideo = true;
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.Parameters params = null;
                params = camera.getParameters();
                Camera.getCameraInfo(this.cameraInfo.getCameraId(), info);
                int displayOrientation = getDisplayOrientation(info, true);
                boolean z = false;
                if (!"samsung".equals(Build.MANUFACTURER) || !"sf2wifixx".equals(Build.PRODUCT)) {
                    int degrees2 = 0;
                    int temp2 = displayOrientation;
                    if (temp2 == 0) {
                        degrees2 = 0;
                    } else if (temp2 == 1) {
                        degrees2 = 90;
                    } else if (temp2 == 2) {
                        degrees2 = 180;
                    } else if (temp2 == 3) {
                        degrees2 = 270;
                    }
                    if (info.orientation % 90 != 0) {
                        info.orientation = 0;
                    }
                    if (info.facing == 1) {
                        temp = (360 - ((info.orientation + degrees2) % 360)) % 360;
                    } else {
                        temp = ((info.orientation - degrees2) + 360) % 360;
                    }
                    degrees = temp;
                } else {
                    degrees = 0;
                }
                this.currentOrientation = degrees;
                camera.setDisplayOrientation(degrees);
                this.diffOrientation = this.currentOrientation - displayOrientation;
                if (params != null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("set preview size = " + this.previewSize.getWidth() + " " + this.previewSize.getHeight());
                    }
                    params.setPreviewSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("set picture size = " + this.pictureSize.getWidth() + " " + this.pictureSize.getHeight());
                    }
                    params.setPictureSize(this.pictureSize.getWidth(), this.pictureSize.getHeight());
                    params.setPictureFormat(this.pictureFormat);
                    params.setRecordingHint(true);
                    if (params.getSupportedFocusModes().contains("continuous-video")) {
                        params.setFocusMode("continuous-video");
                    } else {
                        String desiredMode = "auto";
                        if (params.getSupportedFocusModes().contains(desiredMode)) {
                            params.setFocusMode(desiredMode);
                        }
                    }
                    int outputOrientation = 0;
                    if (this.jpegOrientation != -1) {
                        if (info.facing == 1) {
                            outputOrientation = ((info.orientation - this.jpegOrientation) + 360) % 360;
                        } else {
                            outputOrientation = (info.orientation + this.jpegOrientation) % 360;
                        }
                    }
                    try {
                        params.setRotation(outputOrientation);
                        if (info.facing == 1) {
                            if ((360 - displayOrientation) % 360 == outputOrientation) {
                                z = true;
                            }
                            this.sameTakePictureOrientation = z;
                        } else {
                            if (displayOrientation == outputOrientation) {
                                z = true;
                            }
                            this.sameTakePictureOrientation = z;
                        }
                    } catch (Exception e) {
                    }
                    params.setFlashMode("off");
                    try {
                        camera.setParameters(params);
                    } catch (Exception e2) {
                    }
                    if (params.getMaxNumMeteringAreas() > 0) {
                        this.meteringAreaSupported = true;
                    }
                }
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        } catch (Throwable e4) {
            FileLog.e(e4);
        }
    }

    /* access modifiers changed from: protected */
    public void configurePhotoCamera() {
        int degrees;
        int temp;
        try {
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.Parameters params = null;
                params = camera.getParameters();
                Camera.getCameraInfo(this.cameraInfo.getCameraId(), info);
                int displayOrientation = getDisplayOrientation(info, true);
                boolean z = false;
                if (!"samsung".equals(Build.MANUFACTURER) || !"sf2wifixx".equals(Build.PRODUCT)) {
                    int degrees2 = 0;
                    int temp2 = displayOrientation;
                    if (temp2 == 0) {
                        degrees2 = 0;
                    } else if (temp2 == 1) {
                        degrees2 = 90;
                    } else if (temp2 == 2) {
                        degrees2 = 180;
                    } else if (temp2 == 3) {
                        degrees2 = 270;
                    }
                    if (info.orientation % 90 != 0) {
                        info.orientation = 0;
                    }
                    if (info.facing == 1) {
                        temp = (360 - ((info.orientation + degrees2) % 360)) % 360;
                    } else {
                        temp = ((info.orientation - degrees2) + 360) % 360;
                    }
                    degrees = temp;
                } else {
                    degrees = 0;
                }
                this.currentOrientation = degrees;
                camera.setDisplayOrientation(degrees);
                if (params != null) {
                    params.setPreviewSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                    params.setPictureSize(this.pictureSize.getWidth(), this.pictureSize.getHeight());
                    params.setPictureFormat(this.pictureFormat);
                    params.setJpegQuality(100);
                    params.setJpegThumbnailQuality(100);
                    int maxZoom2 = params.getMaxZoom();
                    this.maxZoom = maxZoom2;
                    params.setZoom((int) (this.currentZoom * ((float) maxZoom2)));
                    if (params.getSupportedFocusModes().contains("continuous-picture")) {
                        params.setFocusMode("continuous-picture");
                    }
                    int outputOrientation = 0;
                    if (this.jpegOrientation != -1) {
                        if (info.facing == 1) {
                            outputOrientation = ((info.orientation - this.jpegOrientation) + 360) % 360;
                        } else {
                            outputOrientation = (info.orientation + this.jpegOrientation) % 360;
                        }
                    }
                    try {
                        params.setRotation(outputOrientation);
                        if (info.facing == 1) {
                            if ((360 - displayOrientation) % 360 == outputOrientation) {
                                z = true;
                            }
                            this.sameTakePictureOrientation = z;
                        } else {
                            if (displayOrientation == outputOrientation) {
                                z = true;
                            }
                            this.sameTakePictureOrientation = z;
                        }
                    } catch (Exception e) {
                    }
                    params.setFlashMode(this.currentFlashMode);
                    try {
                        camera.setParameters(params);
                    } catch (Exception e2) {
                    }
                    if (params.getMaxNumMeteringAreas() > 0) {
                        this.meteringAreaSupported = true;
                    }
                }
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        } catch (Throwable e4) {
            FileLog.e(e4);
        }
    }

    /* access modifiers changed from: protected */
    public void focusToRect(Rect focusRect, Rect meteringRect) {
        try {
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                camera.cancelAutoFocus();
                Camera.Parameters parameters = null;
                try {
                    parameters = camera.getParameters();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                if (parameters != null) {
                    parameters.setFocusMode("auto");
                    ArrayList<Camera.Area> meteringAreas = new ArrayList<>();
                    meteringAreas.add(new Camera.Area(focusRect, 1000));
                    parameters.setFocusAreas(meteringAreas);
                    if (this.meteringAreaSupported) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(new Camera.Area(meteringRect, 1000));
                        parameters.setMeteringAreas(arrayList);
                    }
                    try {
                        camera.setParameters(parameters);
                        camera.autoFocus(this.autoFocusCallback);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
    }

    /* access modifiers changed from: protected */
    public int getMaxZoom() {
        return this.maxZoom;
    }

    /* access modifiers changed from: protected */
    public void setZoom(float value) {
        this.currentZoom = value;
        configurePhotoCamera();
    }

    /* access modifiers changed from: protected */
    public void configureRecorder(int quality, MediaRecorder recorder) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(this.cameraInfo.cameraId, info);
        int displayOrientation = getDisplayOrientation(info, false);
        int outputOrientation = 0;
        if (this.jpegOrientation != -1) {
            if (info.facing == 1) {
                outputOrientation = ((info.orientation - this.jpegOrientation) + 360) % 360;
            } else {
                outputOrientation = (info.orientation + this.jpegOrientation) % 360;
            }
        }
        recorder.setOrientationHint(outputOrientation);
        int highProfile = getHigh();
        boolean canGoHigh = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, highProfile);
        boolean canGoLow = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
        if (canGoHigh && (quality == 1 || !canGoLow)) {
            recorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, highProfile));
        } else if (canGoLow) {
            recorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
        } else {
            throw new IllegalStateException("cannot find valid CamcorderProfile");
        }
        this.isVideo = true;
    }

    /* access modifiers changed from: protected */
    public void stopVideoRecording() {
        this.isVideo = false;
        configurePhotoCamera();
    }

    private int getHigh() {
        if (!"LGE".equals(Build.MANUFACTURER) || !"g3_tmo_us".equals(Build.PRODUCT)) {
            return 1;
        }
        return 4;
    }

    private int getDisplayOrientation(Camera.CameraInfo info, boolean isStillCapture) {
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        int degrees = 0;
        if (rotation == 0) {
            degrees = 0;
        } else if (rotation == 1) {
            degrees = 90;
        } else if (rotation == 2) {
            degrees = 180;
        } else if (rotation == 3) {
            degrees = 270;
        }
        if (info.facing != 1) {
            return ((info.orientation - degrees) + 360) % 360;
        }
        int displayOrientation = (360 - ((info.orientation + degrees) % 360)) % 360;
        if (!isStillCapture && displayOrientation == 90) {
            displayOrientation = 270;
        }
        if (isStillCapture || !"Huawei".equals(Build.MANUFACTURER) || !"angler".equals(Build.PRODUCT) || displayOrientation != 270) {
            return displayOrientation;
        }
        return 90;
    }

    public int getDisplayOrientation() {
        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(this.cameraInfo.getCameraId(), info);
            return getDisplayOrientation(info, true);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return 0;
        }
    }

    public void setPreviewCallback(Camera.PreviewCallback callback) {
        this.cameraInfo.camera.setPreviewCallback(callback);
    }

    public void setOneShotPreviewCallback(Camera.PreviewCallback callback) {
        CameraInfo cameraInfo2 = this.cameraInfo;
        if (cameraInfo2 != null && cameraInfo2.camera != null) {
            this.cameraInfo.camera.setOneShotPreviewCallback(callback);
        }
    }

    public void destroy() {
        this.initied = false;
        OrientationEventListener orientationEventListener2 = this.orientationEventListener;
        if (orientationEventListener2 != null) {
            orientationEventListener2.disable();
            this.orientationEventListener = null;
        }
    }
}
