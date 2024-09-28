package com.king.zxing;

import com.king.zxing.camera.CameraManager;

public interface CaptureManager {
    AmbientLightManager getAmbientLightManager();

    BeepManager getBeepManager();

    CameraManager getCameraManager();

    InactivityTimer getInactivityTimer();
}
