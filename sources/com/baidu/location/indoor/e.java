package com.baidu.location.indoor;

import android.bluetooth.le.ScanResult;
import java.util.HashMap;

class e implements Runnable {
    final /* synthetic */ d a;

    e(d dVar) {
        this.a = dVar;
    }

    public void run() {
        try {
            this.a.a((HashMap<String, ScanResult>) this.a.l);
        } catch (Exception e) {
        }
        if (this.a.f != null && this.a.f.isEnabled()) {
            this.a.a(false);
        }
        this.a.l.clear();
    }
}
