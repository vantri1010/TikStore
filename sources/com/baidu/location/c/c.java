package com.baidu.location.c;

import android.os.Handler;
import android.os.Message;

class c extends Handler {
    final /* synthetic */ b a;

    c(b bVar) {
        this.a = bVar;
    }

    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            this.a.f();
        } else if (i == 2) {
            try {
                this.a.g();
            } catch (Exception e) {
            }
        }
    }
}
