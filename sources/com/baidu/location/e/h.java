package com.baidu.location.e;

import android.location.Location;
import android.os.Handler;
import android.os.Message;
import com.baidu.location.f;

class h extends Handler {
    final /* synthetic */ f a;

    h(f fVar) {
        this.a = fVar;
    }

    public void handleMessage(Message message) {
        Location location;
        String str;
        f fVar;
        if (f.isServing) {
            int i = message.what;
            if (i != 1) {
                if (i == 3) {
                    fVar = this.a;
                    location = (Location) message.obj;
                    str = "&og=1";
                } else if (i == 4) {
                    fVar = this.a;
                    location = (Location) message.obj;
                    str = "&og=2";
                } else {
                    return;
                }
                fVar.a(str, location);
                return;
            }
            this.a.e((Location) message.obj);
        }
    }
}
