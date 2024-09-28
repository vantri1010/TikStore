package com.baidu.location.indoor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import com.baidu.location.indoor.mapversion.a;

class n implements SensorEventListener {
    final /* synthetic */ m a;

    n(m mVar) {
        this.a = mVar;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        if (type == 1) {
            float[] fArr = (float[]) sensorEvent.values.clone();
            float[] unused = this.a.q = (float[]) fArr.clone();
            if (this.a.k && a.b()) {
                a.a(1, fArr, sensorEvent.timestamp);
            }
            float[] a2 = this.a.a(fArr[0], fArr[1], fArr[2]);
            if (m.b(this.a) >= 20) {
                double d = (double) ((a2[0] * a2[0]) + (a2[1] * a2[1]) + (a2[2] * a2[2]));
                if (this.a.n == 0) {
                    if (d > 4.0d) {
                        int unused2 = this.a.n = 1;
                    }
                } else if (d < 0.009999999776482582d) {
                    int unused3 = this.a.n = 0;
                }
            }
        } else if (type == 3) {
            float[] fArr2 = (float[]) sensorEvent.values.clone();
            if (this.a.k && a.b()) {
                a.a(5, fArr2, sensorEvent.timestamp);
            }
            this.a.P[this.a.O] = (double) fArr2[0];
            m.f(this.a);
            if (this.a.O == this.a.N) {
                int unused4 = this.a.O = 0;
            }
            if (m.h(this.a) >= 20) {
                m mVar = this.a;
                boolean unused5 = mVar.Q = mVar.i();
                if (!this.a.Q) {
                    this.a.d.unregisterListener(this.a.b, this.a.h);
                }
                double[] m = this.a.r;
                m mVar2 = this.a;
                m[0] = mVar2.a(mVar2.r[0], (double) fArr2[0], 0.7d);
                this.a.r[1] = (double) fArr2[1];
                this.a.r[2] = (double) fArr2[2];
            }
        } else if (type == 4) {
            float[] fArr3 = (float[]) sensorEvent.values.clone();
            if (this.a.k && a.b()) {
                a.a(4, fArr3, sensorEvent.timestamp);
            }
        }
    }
}
