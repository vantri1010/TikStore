package com.baidu.mapsdkplatform.comapi.map;

import android.os.Handler;
import android.os.Message;

class u extends Handler {
    final /* synthetic */ t a;

    u(t tVar) {
        this.a = tVar;
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        if (t.c != null) {
            this.a.d.a(message);
        }
    }
}
