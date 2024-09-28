package com.baidu.location.b;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.baidu.location.c.d;
import com.baidu.location.c.h;
import com.baidu.location.e.a;
import com.baidu.location.e.i;
import com.baidu.location.e.j;
import com.baidu.location.g.k;

class y extends Handler {
    final /* synthetic */ x a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    y(x xVar, Looper looper) {
        super(looper);
        this.a = xVar;
    }

    public void handleMessage(Message message) {
        a aVar;
        String str;
        Location location;
        i iVar;
        switch (message.what) {
            case 1:
                Bundle data = message.getData();
                Location location2 = (Location) data.getParcelable("loc");
                data.getInt("satnum");
                if (location2 != null) {
                    d.a().a(location2);
                    return;
                }
                return;
            case 2:
                aVar = t.c();
                iVar = j.a().o();
                location = t.d();
                str = t.a();
                break;
            case 3:
                aVar = t.c();
                iVar = null;
                location = t.d();
                str = a.a().d();
                break;
            case 4:
                boolean j = j.j();
                boolean z = false;
                if (k.b()) {
                    j = false;
                }
                if (!j) {
                    z = j;
                } else if (h.a().d() != 1) {
                    z = true;
                }
                if (z) {
                    d.a().e();
                    h.a().c();
                    if (d.a().e()) {
                        w.a().c();
                    }
                }
                try {
                    if (this.a.d != null) {
                        this.a.d.sendEmptyMessageDelayed(4, (long) k.R);
                        return;
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            case 5:
                h.a().b();
                return;
            case 7:
                w.a().c();
                h.a().c();
                return;
            case 8:
            case 9:
                message.getData();
                return;
            default:
                return;
        }
        w.a(aVar, iVar, location, str);
    }
}
