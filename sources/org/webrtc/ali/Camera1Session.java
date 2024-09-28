package org.webrtc.ali;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
import android.view.WindowManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.webrtc.ali.CameraEnumerationAndroid;
import org.webrtc.ali.CameraSession;
import org.webrtc.ali.SurfaceTextureHelper;

class Camera1Session implements CameraSession {
    private static final int NUMBER_OF_CAPTURE_BUFFERS = 3;
    private static final String TAG = "Camera1Session";
    private static final Histogram camera1ResolutionHistogram = Histogram.createEnumeration("WebRTC.Android.Camera1.Resolution", CameraEnumerationAndroid.COMMON_RESOLUTIONS.size());
    /* access modifiers changed from: private */
    public static final Histogram camera1StartTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera1.StartTimeMs", 1, 10000, 50);
    private static final Histogram camera1StopTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera1.StopTimeMs", 1, 10000, 50);
    private final Context applicationContext;
    /* access modifiers changed from: private */
    public final Camera camera;
    private final int cameraId;
    private final Handler cameraThreadHandler;
    /* access modifiers changed from: private */
    public final CameraEnumerationAndroid.CaptureFormat captureFormat;
    private final boolean captureToTexture;
    /* access modifiers changed from: private */
    public final long constructionTimeNs;
    /* access modifiers changed from: private */
    public final CameraSession.Events events;
    /* access modifiers changed from: private */
    public boolean firstFrameReported = false;
    /* access modifiers changed from: private */
    public final Camera.CameraInfo info;
    /* access modifiers changed from: private */
    public SessionState state;
    /* access modifiers changed from: private */
    public final SurfaceTextureHelper surfaceTextureHelper;

    private enum SessionState {
        RUNNING,
        STOPPED
    }

    public static void create(CameraSession.CreateSessionCallback callback, CameraSession.Events events2, boolean captureToTexture2, Context applicationContext2, SurfaceTextureHelper surfaceTextureHelper2, MediaRecorder mediaRecorder, int cameraId2, int width, int height, int framerate) {
        CameraSession.CreateSessionCallback createSessionCallback = callback;
        boolean z = captureToTexture2;
        int i = cameraId2;
        int i2 = width;
        int i3 = height;
        long constructionTimeNs2 = System.nanoTime();
        Logging.d(TAG, "Open camera " + i);
        events2.onCameraOpening();
        try {
            Camera camera2 = Camera.open(cameraId2);
            try {
                camera2.setPreviewTexture(surfaceTextureHelper2.getSurfaceTexture());
                Camera.CameraInfo info2 = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info2);
                Camera.Parameters parameters = camera2.getParameters();
                CameraEnumerationAndroid.CaptureFormat captureFormat2 = findClosestCaptureFormat(parameters, i2, i3, framerate);
                Size pictureSize = findClosestPictureSize(parameters, i2, i3);
                updateCameraParameters(camera2, parameters, captureFormat2, pictureSize, z);
                if (!z) {
                    int frameSize = captureFormat2.frameSize();
                    for (int i4 = 0; i4 < 3; i4++) {
                        camera2.addCallbackBuffer(ByteBuffer.allocateDirect(frameSize).array());
                    }
                }
                camera2.setDisplayOrientation(0);
                Camera1Session camera1Session = r2;
                Size size = pictureSize;
                Camera.Parameters parameters2 = parameters;
                Camera camera3 = camera2;
                Camera1Session camera1Session2 = new Camera1Session(events2, captureToTexture2, applicationContext2, surfaceTextureHelper2, mediaRecorder, cameraId2, camera2, info2, captureFormat2, constructionTimeNs2);
                createSessionCallback.onDone(camera1Session);
            } catch (IOException e) {
                camera2.release();
                createSessionCallback.onFailure(CameraSession.FailureType.ERROR, e.getMessage());
            }
        } catch (RuntimeException e2) {
            createSessionCallback.onFailure(CameraSession.FailureType.ERROR, e2.getMessage());
        }
    }

    private static void updateCameraParameters(Camera camera2, Camera.Parameters parameters, CameraEnumerationAndroid.CaptureFormat captureFormat2, Size pictureSize, boolean captureToTexture2) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        parameters.setPreviewFpsRange(captureFormat2.framerate.min, captureFormat2.framerate.max);
        parameters.setPreviewSize(captureFormat2.width, captureFormat2.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        if (!captureToTexture2) {
            captureFormat2.getClass();
            parameters.setPreviewFormat(17);
        }
        if (parameters.isVideoStabilizationSupported()) {
            parameters.setVideoStabilization(true);
        }
        if (focusModes.contains("continuous-video")) {
            parameters.setFocusMode("continuous-video");
        }
        camera2.setParameters(parameters);
    }

    private static CameraEnumerationAndroid.CaptureFormat findClosestCaptureFormat(Camera.Parameters parameters, int width, int height, int framerate) {
        List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> supportedFramerates = Camera1Enumerator.convertFramerates(parameters.getSupportedPreviewFpsRange());
        Logging.d(TAG, "Available fps ranges: " + supportedFramerates);
        CameraEnumerationAndroid.CaptureFormat.FramerateRange fpsRange = CameraEnumerationAndroid.getClosestSupportedFramerateRange(supportedFramerates, framerate);
        Size previewSize = CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(parameters.getSupportedPreviewSizes()), width, height);
        CameraEnumerationAndroid.reportCameraResolution(camera1ResolutionHistogram, previewSize);
        return new CameraEnumerationAndroid.CaptureFormat(previewSize.width, previewSize.height, fpsRange);
    }

    private static Size findClosestPictureSize(Camera.Parameters parameters, int width, int height) {
        return CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(parameters.getSupportedPictureSizes()), width, height);
    }

    private Camera1Session(CameraSession.Events events2, boolean captureToTexture2, Context applicationContext2, SurfaceTextureHelper surfaceTextureHelper2, MediaRecorder mediaRecorder, int cameraId2, Camera camera2, Camera.CameraInfo info2, CameraEnumerationAndroid.CaptureFormat captureFormat2, long constructionTimeNs2) {
        Logging.d(TAG, "Create new camera1 session on camera " + cameraId2);
        this.cameraThreadHandler = new Handler();
        this.events = events2;
        this.captureToTexture = captureToTexture2;
        this.applicationContext = applicationContext2;
        this.surfaceTextureHelper = surfaceTextureHelper2;
        this.cameraId = cameraId2;
        this.camera = camera2;
        this.info = info2;
        this.captureFormat = captureFormat2;
        this.constructionTimeNs = constructionTimeNs2;
        startCapturing();
        if (mediaRecorder != null) {
            camera2.unlock();
            mediaRecorder.setCamera(camera2);
        }
    }

    public void stop() {
        Logging.d(TAG, "Stop camera1 session on camera " + this.cameraId);
        checkIsOnCameraThread();
        if (this.state != SessionState.STOPPED) {
            long stopStartTime = System.nanoTime();
            stopInternal();
            camera1StopTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - stopStartTime));
        }
    }

    private void startCapturing() {
        Logging.d(TAG, "Start capturing");
        checkIsOnCameraThread();
        this.state = SessionState.RUNNING;
        this.camera.setErrorCallback(new Camera.ErrorCallback() {
            public void onError(int error, Camera camera) {
                String errorMessage;
                if (error == 100) {
                    errorMessage = "Camera server died!";
                } else {
                    errorMessage = "Camera error: " + error;
                }
                Logging.e(Camera1Session.TAG, errorMessage);
                Camera1Session.this.stopInternal();
                if (error == 2) {
                    Camera1Session.this.events.onCameraDisconnected(Camera1Session.this);
                } else {
                    Camera1Session.this.events.onCameraError(Camera1Session.this, errorMessage);
                }
            }
        });
        if (this.captureToTexture) {
            listenForTextureFrames();
        } else {
            listenForBytebufferFrames();
        }
        try {
            this.camera.startPreview();
        } catch (RuntimeException e) {
            stopInternal();
            this.events.onCameraError(this, e.getMessage());
        }
    }

    /* access modifiers changed from: private */
    public void stopInternal() {
        Logging.d(TAG, "Stop internal");
        checkIsOnCameraThread();
        if (this.state == SessionState.STOPPED) {
            Logging.d(TAG, "Camera is already stopped");
            return;
        }
        this.state = SessionState.STOPPED;
        this.surfaceTextureHelper.stopListening();
        this.camera.stopPreview();
        this.camera.release();
        this.events.onCameraClosed(this);
        Logging.d(TAG, "Stop done");
    }

    private void listenForTextureFrames() {
        this.surfaceTextureHelper.startListening(new SurfaceTextureHelper.OnTextureFrameAvailableListener() {
            public void onTextureFrameAvailable(int oesTextureId, float[] transformMatrix, long timestampNs) {
                Camera1Session.this.checkIsOnCameraThread();
                if (Camera1Session.this.state != SessionState.RUNNING) {
                    Logging.d(Camera1Session.TAG, "Texture frame captured but camera is no longer running.");
                    Camera1Session.this.surfaceTextureHelper.returnTextureFrame();
                    return;
                }
                if (!Camera1Session.this.firstFrameReported) {
                    Camera1Session.camera1StartTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - Camera1Session.this.constructionTimeNs));
                    boolean unused = Camera1Session.this.firstFrameReported = true;
                }
                int rotation = Camera1Session.this.getFrameOrientation();
                if (Camera1Session.this.info.facing == 1) {
                    transformMatrix = RendererCommon.multiplyMatrices(transformMatrix, RendererCommon.horizontalFlipMatrix());
                }
                CameraSession.Events access$100 = Camera1Session.this.events;
                Camera1Session camera1Session = Camera1Session.this;
                access$100.onTextureFrameCaptured(camera1Session, camera1Session.captureFormat.width, Camera1Session.this.captureFormat.height, oesTextureId, transformMatrix, rotation, timestampNs);
            }
        });
    }

    private void listenForBytebufferFrames() {
        this.camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera callbackCamera) {
                Camera1Session.this.checkIsOnCameraThread();
                if (callbackCamera != Camera1Session.this.camera) {
                    Logging.e(Camera1Session.TAG, "Callback from a different camera. This should never happen.");
                } else if (Camera1Session.this.state != SessionState.RUNNING) {
                    Logging.d(Camera1Session.TAG, "Bytebuffer frame captured but camera is no longer running.");
                } else {
                    long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
                    if (!Camera1Session.this.firstFrameReported) {
                        Camera1Session.camera1StartTimeMsHistogram.addSample((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - Camera1Session.this.constructionTimeNs));
                        boolean unused = Camera1Session.this.firstFrameReported = true;
                    }
                    CameraSession.Events access$100 = Camera1Session.this.events;
                    Camera1Session camera1Session = Camera1Session.this;
                    access$100.onByteBufferFrameCaptured(camera1Session, data, camera1Session.captureFormat.width, Camera1Session.this.captureFormat.height, Camera1Session.this.getFrameOrientation(), captureTimeNs);
                    Camera1Session.this.camera.addCallbackBuffer(data);
                }
            }
        });
    }

    private int getDeviceOrientation() {
        int rotation = ((WindowManager) this.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (rotation == 1) {
            return 90;
        }
        if (rotation == 2) {
            return 180;
        }
        if (rotation != 3) {
            return 0;
        }
        return 270;
    }

    /* access modifiers changed from: private */
    public int getFrameOrientation() {
        int rotation = getDeviceOrientation();
        if (this.info.facing == 0) {
            rotation = 360 - rotation;
        }
        return (this.info.orientation + rotation) % 360;
    }

    /* access modifiers changed from: private */
    public void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }
}
