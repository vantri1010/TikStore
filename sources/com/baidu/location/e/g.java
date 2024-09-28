package com.baidu.location.e;

import android.location.OnNmeaMessageListener;

class g implements OnNmeaMessageListener {
    final /* synthetic */ f a;

    g(f fVar) {
        this.a = fVar;
    }

    public void onNmeaMessage(String str, long j) {
        if (this.a.b(str)) {
            this.a.a(str);
        }
    }
}
