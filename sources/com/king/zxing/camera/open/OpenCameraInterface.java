package com.king.zxing.camera.open;

import android.hardware.Camera;
import com.king.zxing.util.LogUtils;

public final class OpenCameraInterface {
    public static final int NO_REQUESTED_CAMERA = -1;

    private OpenCameraInterface() {
    }

    public static OpenCamera open(int cameraId) {
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            LogUtils.w("No cameras!");
            return null;
        } else if (cameraId >= numCameras) {
            LogUtils.w("Requested camera does not exist: " + cameraId);
            return null;
        } else {
            if (cameraId <= -1) {
                cameraId = 0;
                while (cameraId < numCameras) {
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    Camera.getCameraInfo(cameraId, cameraInfo);
                    if (CameraFacing.values()[cameraInfo.facing] == CameraFacing.BACK) {
                        break;
                    }
                    cameraId++;
                }
                if (cameraId == numCameras) {
                    LogUtils.i("No camera facing " + CameraFacing.BACK + "; returning camera #0");
                    cameraId = 0;
                }
            }
            LogUtils.i("Opening camera #" + cameraId);
            Camera.CameraInfo cameraInfo2 = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo2);
            Camera camera = Camera.open(cameraId);
            if (camera == null) {
                return null;
            }
            return new OpenCamera(cameraId, camera, CameraFacing.values()[cameraInfo2.facing], cameraInfo2.orientation);
        }
    }
}
