package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;

class i {
    private SensorManager a = null;
    private Intent b = null;

    public i(Context ct) {
        this.a = (SensorManager) ct.getSystemService("sensor");
        this.b = ct.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    public String a() {
        Intent intent = this.b;
        if (intent == null) {
            return "-1";
        }
        int i = 0;
        int level = intent.getIntExtra("level", 0);
        int scale = this.b.getIntExtra("scale", 100);
        StringBuilder sb = new StringBuilder();
        sb.append("");
        if (scale != 0) {
            i = (level * 100) / scale;
        }
        sb.append(i);
        return sb.toString();
    }

    public String b() {
        if (this.b == null) {
            return "-1";
        }
        return "" + this.b.getIntExtra("voltage", 0);
    }

    public String c() {
        if (this.b == null) {
            return "-1";
        }
        return "" + this.b.getIntExtra("temperature", 0);
    }

    public String d() {
        int sensors = 0;
        SensorManager sensorManager = this.a;
        if (sensorManager == null) {
            return "" + 0;
        }
        if (sensorManager.getDefaultSensor(13) != null) {
            sensors = 0 | 1;
        }
        if (this.a.getDefaultSensor(2) != null) {
            sensors |= 2;
        }
        if (this.a.getDefaultSensor(4) != null) {
            sensors |= 4;
        }
        if (this.a.getDefaultSensor(5) != null) {
            sensors |= 8;
        }
        if (this.a.getDefaultSensor(11) != null) {
            sensors |= 16;
        }
        return "" + sensors;
    }
}
