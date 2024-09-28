package org.webrtc.audio;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import org.webrtc.ali.ThreadUtils;
import org.webrtc.utils.AppRTCUtils;

public class AppRTCProximitySensor implements SensorEventListener {
    private static final String TAG = "AppRTCProximitySensor";
    private boolean lastStateReportIsNear = false;
    private final Runnable onSensorStateListener;
    private Sensor proximitySensor = null;
    private final SensorManager sensorManager;
    private final ThreadUtils.ThreadChecker threadChecker = new ThreadUtils.ThreadChecker();

    static AppRTCProximitySensor create(Context context, Runnable sensorStateListener) {
        return new AppRTCProximitySensor(context, sensorStateListener);
    }

    private AppRTCProximitySensor(Context context, Runnable sensorStateListener) {
        Log.d(TAG, TAG + AppRTCUtils.getThreadInfo());
        this.onSensorStateListener = sensorStateListener;
        this.sensorManager = (SensorManager) context.getSystemService("sensor");
    }

    public boolean start() {
        this.threadChecker.checkIsOnValidThread();
        Log.d(TAG, TtmlNode.START + AppRTCUtils.getThreadInfo());
        if (!initDefaultSensor()) {
            return false;
        }
        this.sensorManager.registerListener(this, this.proximitySensor, 3);
        return true;
    }

    public void stop() {
        this.threadChecker.checkIsOnValidThread();
        Log.d(TAG, "stop" + AppRTCUtils.getThreadInfo());
        Sensor sensor = this.proximitySensor;
        if (sensor != null) {
            this.sensorManager.unregisterListener(this, sensor);
        }
    }

    public boolean sensorReportsNearState() {
        this.threadChecker.checkIsOnValidThread();
        return this.lastStateReportIsNear;
    }

    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        this.threadChecker.checkIsOnValidThread();
        AppRTCUtils.assertIsTrue(sensor.getType() == 8);
        if (accuracy == 0) {
            Log.e(TAG, "The values returned by this sensor cannot be trusted");
        }
    }

    public final void onSensorChanged(SensorEvent event) {
        this.threadChecker.checkIsOnValidThread();
        AppRTCUtils.assertIsTrue(event.sensor.getType() == 8);
        if (event.values[0] < this.proximitySensor.getMaximumRange()) {
            Log.d(TAG, "Proximity sensor => NEAR state");
            this.lastStateReportIsNear = true;
        } else {
            Log.d(TAG, "Proximity sensor => FAR state");
            this.lastStateReportIsNear = false;
        }
        Runnable runnable = this.onSensorStateListener;
        if (runnable != null) {
            runnable.run();
        }
        Log.d(TAG, "onSensorChanged" + AppRTCUtils.getThreadInfo() + ": accuracy=" + event.accuracy + ", timestamp=" + event.timestamp + ", distance=" + event.values[0]);
    }

    private boolean initDefaultSensor() {
        if (this.proximitySensor != null) {
            return true;
        }
        Sensor defaultSensor = this.sensorManager.getDefaultSensor(8);
        this.proximitySensor = defaultSensor;
        if (defaultSensor == null) {
            return false;
        }
        logProximitySensorInfo();
        return true;
    }

    private void logProximitySensorInfo() {
        if (this.proximitySensor != null) {
            StringBuilder info = new StringBuilder("Proximity sensor: ");
            info.append("name=");
            info.append(this.proximitySensor.getName());
            info.append(", vendor: ");
            info.append(this.proximitySensor.getVendor());
            info.append(", power: ");
            info.append(this.proximitySensor.getPower());
            info.append(", resolution: ");
            info.append(this.proximitySensor.getResolution());
            info.append(", max range: ");
            info.append(this.proximitySensor.getMaximumRange());
            info.append(", min delay: ");
            info.append(this.proximitySensor.getMinDelay());
            if (Build.VERSION.SDK_INT >= 20) {
                info.append(", type: ");
                info.append(this.proximitySensor.getStringType());
            }
            if (Build.VERSION.SDK_INT >= 21) {
                info.append(", max delay: ");
                info.append(this.proximitySensor.getMaxDelay());
                info.append(", reporting mode: ");
                info.append(this.proximitySensor.getReportingMode());
                info.append(", isWakeUpSensor: ");
                info.append(this.proximitySensor.isWakeUpSensor());
            }
            Log.d(TAG, info.toString());
        }
    }
}
