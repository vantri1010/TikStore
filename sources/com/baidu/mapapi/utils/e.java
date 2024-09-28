package com.baidu.mapapi.utils;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.baidu.mapframework.open.aidl.IComOpenClient;
import com.baidu.mapframework.open.aidl.b;

class e extends b.a {
    final /* synthetic */ d a;

    e(d dVar) {
        this.a = dVar;
    }

    public void a(IBinder iBinder) throws RemoteException {
        Log.d(b.c, "onClientReady");
        if (b.e != null) {
            IComOpenClient unused = b.e = null;
        }
        IComOpenClient unused2 = b.e = IComOpenClient.a.a(iBinder);
        if (!b.t) {
            b.a(b.a);
        }
        boolean unused3 = b.t = true;
    }
}
