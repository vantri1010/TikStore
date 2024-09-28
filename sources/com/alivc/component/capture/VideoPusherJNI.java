package com.alivc.component.capture;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import com.alivc.component.capture.VideoPusher;
import java.util.List;

public class VideoPusherJNI {
    private long mNativeHandler = 0;
    private VideoPusher mVideoPusher = null;
    private VideoPusher.VideoSourceListener mVideoPusherDataListener = new VideoPusher.VideoSourceListener() {
        public void onVideoFrame(byte[] videoFrame, long timestamp, int cameraId, int orientation, int width, int height, int format) {
            int unused = VideoPusherJNI.this.onData(videoFrame, timestamp, cameraId, orientation, width, height, format);
        }
    };
    private VideoPusher.VideoSourceTextureListener mVideoPusherTextureListener = new VideoPusher.VideoSourceTextureListener() {
        public void onVideoFrame(long timestamp, int cameraId, int orientation, int width, int height, int format) {
            int unused = VideoPusherJNI.this.onTexture(timestamp, cameraId, orientation, width, height, format);
        }
    };

    /* access modifiers changed from: private */
    public native int onData(byte[] bArr, long j, int i, int i2, int i3, int i4, int i5);

    private native int onStarted();

    private native int onStopped();

    /* access modifiers changed from: private */
    public native int onTexture(long j, int i, int i2, int i3, int i4, int i5);

    public long getVideoHandler() {
        return this.mNativeHandler;
    }

    public VideoPusherJNI(long nativeHandler) {
        Log.d("VideoPusherJNI", "ME ME ME, VideoPusherJNI construct");
        if (this.mVideoPusher == null) {
            VideoPusher videoPusher = new VideoPusher();
            this.mVideoPusher = videoPusher;
            videoPusher.setVideoSourceListener(this.mVideoPusherDataListener);
            this.mVideoPusher.setVideoSourceTextureListener(this.mVideoPusherTextureListener);
        }
        this.mNativeHandler = nativeHandler;
    }

    public void init(int source, int width, int height, int fps, int rotation, int customRotation, boolean surfaceMode, boolean focusBySensor, Context context) {
        Log.d("VideoPusherJNI", "VideoPusherJNI init source " + source + ", widht " + width + ",height " + height + ", fps " + fps + ", rotation " + rotation);
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            videoPusher.init(source, width, height, fps, rotation, customRotation, surfaceMode, focusBySensor, context);
        }
    }

    public int start(int surfaceId) {
        Log.d("VideoPusherJNI", "VideoPusherJNI start");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher == null) {
            return -1;
        }
        try {
            videoPusher.start(surfaceId);
            return 0;
        } catch (Exception e) {
            Log.e("VideoPusherJNI", "VideoPusherJNI start Failed");
            return -1;
        }
    }

    public void pause(boolean useEmptyData) {
        Log.d("VideoPusherJNI", "VideoPusherJNI pause " + useEmptyData);
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            videoPusher.pause(useEmptyData);
        }
    }

    public int resume() {
        Log.d("VideoPusherJNI", "VideoPusherJNI resume");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher == null) {
            return -1;
        }
        try {
            videoPusher.resume();
            return 0;
        } catch (Exception e) {
            Log.e("VideoPusherJNI", "VideoPusherJNI resume Failed");
            return -1;
        }
    }

    public void stop() {
        Log.d("VideoPusherJNI", "VideoPusherJNI stop");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            videoPusher.stop();
        }
    }

    public void destroy() {
        Log.d("VideoPusherJNI", "VideoPusherJNI destroy");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            videoPusher.destroy();
            this.mVideoPusher = null;
        }
        this.mNativeHandler = 0;
    }

    public void switchCamera() {
        Log.d("VideoPusherJNI", "VideoPusherJNI switchCamera");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            try {
                videoPusher.switchCamera();
            } catch (Exception e) {
                Log.e("VideoPusherJNI", "VideoPusherJNI switchCamera Failed");
            }
        }
    }

    public void setOrientation(int orientation) {
        Log.d("VideoPusherJNI", "VideoPusherJNI setOrientation");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            try {
                videoPusher.setOrientation(orientation);
            } catch (Exception e) {
                Log.e("VideoPusherJNI", "VideoPusherJNI setOrientation Failed");
            }
        }
    }

    public void setAutoFocus(boolean auto, float x, float y) {
        Log.d("VideoPusherJNI", "VideoPusherJNI setAutoFocus " + auto + ", x" + x + ", y" + y);
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            videoPusher.setAutoFocus(auto);
            if (x > 0.0f || y > 0.0f) {
                this.mVideoPusher.setFocus(x, y);
            }
        }
    }

    public void setZoom(int zoom) {
        Log.d("VideoPusherJNI", "VideoPusherJNI setzoom " + zoom);
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            videoPusher.setZoom(zoom);
        }
    }

    public boolean isSupportFocusPoint() {
        Log.d("VideoPusherJNI", "VideoPusherJNI isSupportFocusPoint ");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            return videoPusher.isSupportFocusPoint();
        }
        return false;
    }

    public boolean isSupportExposurePoint() {
        Log.d("VideoPusherJNI", "VideoPusherJNI isSupportExposurePoint ");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            return videoPusher.isSupportExposurePoint();
        }
        return false;
    }

    public void setFocusPoint(float x, float y) {
        Log.d("VideoPusherJNI", "VideoPusherJNI setFocusPoint x" + x + ", y" + y);
        if (this.mVideoPusher == null) {
            return;
        }
        if (x > 0.0f || y > 0.0f) {
            this.mVideoPusher.setFocus(x, y);
        }
    }

    public void setExposurePoint(float x, float y) {
        Log.d("VideoPusherJNI", "VideoPusherJNI setExposurePoint x" + x + ", y" + y);
        if (this.mVideoPusher == null) {
            return;
        }
        if (x > 0.0f || y > 0.0f) {
            this.mVideoPusher.setExposurePoint(x, y);
        }
    }

    public int getMaxZoom() {
        Log.d("VideoPusherJNI", "VideoPusherJNI getMaxZoom ");
        if (this.mVideoPusher == null) {
            return 0;
        }
        Log.d("VideoPusherJNI", "VideoPusherJNI getMaxZoom " + this.mVideoPusher.getMaxZoom());
        return this.mVideoPusher.getMaxZoom();
    }

    public int getCameraSource() {
        Log.d("VideoPusherJNI", "VideoPusherJNI getCameraSource ");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            return videoPusher.getCameraSource();
        }
        return 0;
    }

    public int getCurrentZoom() {
        Log.d("VideoPusherJNI", "VideoPusherJNI getCurrentZoom ");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            return videoPusher.getCurrentZoom();
        }
        return 0;
    }

    public void setExposureCompensation(int exposure) {
        Log.d("VideoPusherJNI", "VideoPusherJNI setExposureCompensation " + exposure);
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            videoPusher.setExposure(exposure);
        }
    }

    public int getCurrentExposureCompensation() {
        Log.d("VideoPusherJNI", "VideoPusherJNI getCurrentExposureCompensation ");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            return videoPusher.getCurrentExposure();
        }
        return 0;
    }

    public void setTorch(boolean on) {
        Log.d("VideoPusherJNI", "VideoPusherJNI setTorch " + on);
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            videoPusher.setFlashOn(on);
        }
    }

    public boolean isSupportAutoFocus() {
        Log.d("VideoPusherJNI", "VideoPusherJNI isSupportAutoFocus ");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            return videoPusher.isSupportAutoFocus();
        }
        return false;
    }

    public boolean isSupportFlash() {
        Log.d("VideoPusherJNI", "VideoPusherJNI isSupportFlash ");
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            return videoPusher.isSupportFlash();
        }
        return false;
    }

    public int updateTexImage() {
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            return videoPusher.updateTexImage();
        }
        return -1;
    }

    public void getTransformMatrix(float[] matrix) {
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            videoPusher.getTransformMatrix(matrix);
        }
    }

    public static String getSupportedResolutions(int source) {
        List<Camera.Size> sizes = VideoPusher.getSupportedResolutions(source);
        String strSize = null;
        if (sizes != null) {
            for (Camera.Size size : sizes) {
                if (strSize == null) {
                    strSize = size.width + "," + size.height;
                } else {
                    strSize = strSize + "," + size.width + "," + size.height;
                }
            }
        }
        return strSize;
    }

    public static String getSupportedFormats() {
        List<Integer> formats = VideoPusher.getSupportedFormats();
        String strFmt = null;
        if (formats != null) {
            for (Integer fmt : formats) {
                Integer nativeFmt = Integer.valueOf(JNIUtils.VideoFormatToNative(fmt.intValue()));
                if (strFmt == null) {
                    strFmt = nativeFmt.toString();
                } else {
                    strFmt = strFmt + "," + nativeFmt.toString();
                }
            }
        }
        return strFmt;
    }

    public boolean isCapturing() {
        VideoPusher videoPusher = this.mVideoPusher;
        if (videoPusher != null) {
            return videoPusher.isPreviewRunning();
        }
        return false;
    }
}
