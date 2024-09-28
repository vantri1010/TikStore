package com.baidu.location.c;

import android.os.Handler;
import android.os.Message;

class i extends Handler {
    final /* synthetic */ h a;

    i(h hVar) {
        this.a = hVar;
    }

    public void handleMessage(Message message) {
        if (message.what == 1) {
            this.a.d();
        }
    }
}
