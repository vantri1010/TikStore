package com.king.zxing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import com.king.zxing.camera.CameraManager;
import com.king.zxing.camera.FrontLightMode;

final class AmbientLightManager implements SensorEventListener {
    protected static final float BRIGHT_ENOUGH_LUX = 100.0f;
    protected static final float TOO_DARK_LUX = 45.0f;
    private float brightEnoughLux = BRIGHT_ENOUGH_LUX;
    private CameraManager cameraManager;
    private final Context context;
    private Sensor lightSensor;
    private float tooDarkLux = TOO_DARK_LUX;

    AmbientLightManager(Context context2) {
        this.context = context2;
    }

    /* access modifiers changed from: package-private */
    public void start(CameraManager cameraManager2) {
        this.cameraManager = cameraManager2;
        if (FrontLightMode.readPref(PreferenceManager.getDefaultSharedPreferences(this.context)) == FrontLightMode.AUTO) {
            SensorManager sensorManager = (SensorManager) this.context.getSystemService("sensor");
            Sensor defaultSensor = sensorManager.getDefaultSensor(5);
            this.lightSensor = defaultSensor;
            if (defaultSensor != null) {
                sensorManager.registerListener(this, defaultSensor, 3);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void stop() {
        if (this.lightSensor != null) {
            ((SensorManager) this.context.getSystemService("sensor")).unregisterListener(this);
            this.cameraManager = null;
            this.lightSensor = null;
        }
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        float ambientLightLux = sensorEvent.values[0];
        CameraManager cameraManager2 = this.cameraManager;
        if (cameraManager2 == null) {
            return;
        }
        if (ambientLightLux <= this.tooDarkLux) {
            cameraManager2.sensorChanged(true, ambientLightLux);
        } else if (ambientLightLux >= this.brightEnoughLux) {
            cameraManager2.sensorChanged(false, ambientLightLux);
        }
    }

    public void setTooDarkLux(float tooDarkLux2) {
        this.tooDarkLux = tooDarkLux2;
    }

    public void setBrightEnoughLux(float brightEnoughLux2) {
        this.brightEnoughLux = brightEnoughLux2;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
