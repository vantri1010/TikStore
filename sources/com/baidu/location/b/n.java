package com.baidu.location.b;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.baidu.location.f;
import com.baidu.location.indoor.g;
import com.baidu.location.indoor.mapversion.IndoorJni;

public class n implements SensorEventListener {
    private static n d;
    private float[] a;
    private float[] b;
    private SensorManager c;
    private float e;
    private boolean f = false;
    private boolean g = false;
    private boolean h = false;

    private n() {
    }

    public static synchronized n a() {
        n nVar;
        synchronized (n.class) {
            if (d == null) {
                d = new n();
            }
            nVar = d;
        }
        return nVar;
    }

    public void a(boolean z) {
        this.f = z;
    }

    public synchronized void b() {
        if (!this.h) {
            if (this.f) {
                if (this.c == null) {
                    this.c = (SensorManager) f.getServiceContext().getSystemService("sensor");
                }
                if (this.c != null) {
                    Sensor defaultSensor = this.c.getDefaultSensor(11);
                    if (defaultSensor != null && this.f) {
                        this.c.registerListener(this, defaultSensor, 3);
                    }
                    Sensor defaultSensor2 = this.c.getDefaultSensor(2);
                    if (defaultSensor2 != null && this.f) {
                        this.c.registerListener(this, defaultSensor2, 3);
                    }
                }
                this.h = true;
            }
        }
    }

    public void b(boolean z) {
        this.g = z;
    }

    public synchronized void c() {
        if (this.h) {
            if (this.c != null) {
                this.c.unregisterListener(this);
                this.c = null;
            }
            this.h = false;
        }
    }

    public boolean d() {
        return this.f;
    }

    public float e() {
        return this.e;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        if (type == 2) {
            float[] fArr = (float[]) sensorEvent.values.clone();
            this.b = fArr;
            double sqrt = Math.sqrt((double) ((fArr[0] * fArr[0]) + (fArr[1] * fArr[1]) + (fArr[2] * fArr[2])));
            if (this.b != null) {
                try {
                    if (g.a().e()) {
                        IndoorJni.setPfGeomag(sqrt);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } else if (type == 11) {
            float[] fArr2 = (float[]) sensorEvent.values.clone();
            this.a = fArr2;
            if (fArr2 != null) {
                float[] fArr3 = new float[9];
                try {
                    SensorManager.getRotationMatrixFromVector(fArr3, fArr2);
                    float[] fArr4 = new float[3];
                    SensorManager.getOrientation(fArr3, fArr4);
                    float degrees = (float) Math.toDegrees((double) fArr4[0]);
                    this.e = degrees;
                    if (degrees < 0.0f) {
                        degrees += 360.0f;
                    }
                    this.e = (float) Math.floor((double) degrees);
                } catch (Exception e3) {
                    this.e = 0.0f;
                }
            }
        }
    }
}
