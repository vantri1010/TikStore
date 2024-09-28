package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class WallpaperParallaxEffect implements SensorEventListener {
    private Sensor accelerometer;
    private int bufferOffset;
    private Callback callback;
    private boolean enabled;
    private float[] pitchBuffer = new float[3];
    private float[] rollBuffer = new float[3];
    private SensorManager sensorManager;
    private WindowManager wm;

    public interface Callback {
        void onOffsetsChanged(int i, int i2);
    }

    public WallpaperParallaxEffect(Context context) {
        this.wm = (WindowManager) context.getSystemService("window");
        SensorManager sensorManager2 = (SensorManager) context.getSystemService("sensor");
        this.sensorManager = sensorManager2;
        this.accelerometer = sensorManager2.getDefaultSensor(1);
    }

    public void setEnabled(boolean enabled2) {
        if (this.enabled != enabled2) {
            this.enabled = enabled2;
            Sensor sensor = this.accelerometer;
            if (sensor != null) {
                if (enabled2) {
                    this.sensorManager.registerListener(this, sensor, 1);
                } else {
                    this.sensorManager.unregisterListener(this);
                }
            }
        }
    }

    public void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    public float getScale(int boundsWidth, int boundsHeight) {
        int offset = AndroidUtilities.dp(16.0f);
        return Math.max((((float) boundsWidth) + ((float) (offset * 2))) / ((float) boundsWidth), (((float) boundsHeight) + ((float) (offset * 2))) / ((float) boundsHeight));
    }

    public void onSensorChanged(SensorEvent event) {
        float[] fArr;
        SensorEvent sensorEvent = event;
        int rotation = this.wm.getDefaultDisplay().getRotation();
        float x = sensorEvent.values[0] / 9.80665f;
        float y = sensorEvent.values[1] / 9.80665f;
        float z = sensorEvent.values[2] / 9.80665f;
        float pitch = (float) ((Math.atan2((double) x, Math.sqrt((double) ((y * y) + (z * z)))) / 3.141592653589793d) * 2.0d);
        float f = z;
        float roll = (float) ((Math.atan2((double) y, Math.sqrt((double) ((x * x) + (z * z)))) / 3.141592653589793d) * 2.0d);
        if (rotation == 1) {
            float tmp = pitch;
            pitch = roll;
            roll = tmp;
        } else if (rotation == 2) {
            roll = -roll;
            pitch = -pitch;
        } else if (rotation == 3) {
            float tmp2 = -pitch;
            pitch = roll;
            roll = tmp2;
        }
        float[] fArr2 = this.rollBuffer;
        int i = this.bufferOffset;
        fArr2[i] = roll;
        this.pitchBuffer[i] = pitch;
        this.bufferOffset = (i + 1) % fArr2.length;
        float pitch2 = 0.0f;
        float roll2 = 0.0f;
        int i2 = 0;
        while (true) {
            fArr = this.rollBuffer;
            if (i2 >= fArr.length) {
                break;
            }
            roll2 += fArr[i2];
            pitch2 += this.pitchBuffer[i2];
            i2++;
        }
        float roll3 = roll2 / ((float) fArr.length);
        float pitch3 = pitch2 / ((float) fArr.length);
        if (roll3 > 1.0f) {
            roll3 = 2.0f - roll3;
        } else if (roll3 < -1.0f) {
            roll3 = -2.0f - roll3;
        }
        int offsetX = Math.round(AndroidUtilities.dpf2(16.0f) * pitch3);
        int offsetY = Math.round(AndroidUtilities.dpf2(16.0f) * roll3);
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onOffsetsChanged(offsetX, offsetY);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
