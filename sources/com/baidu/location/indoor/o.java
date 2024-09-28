package com.baidu.location.indoor;

import java.util.TimerTask;

class o extends TimerTask {
    final /* synthetic */ m a;

    o(m mVar) {
        this.a = mVar;
    }

    public void run() {
        try {
            this.a.l();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
