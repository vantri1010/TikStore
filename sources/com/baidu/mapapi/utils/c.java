package com.baidu.mapapi.utils;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.baidu.mapframework.open.aidl.IComOpenClient;
import com.baidu.mapframework.open.aidl.b;

final class c extends b.a {
    final /* synthetic */ int a;

    c(int i) {
        this.a = i;
    }

    public void a(IBinder iBinder) throws RemoteException {
        Log.d(b.c, "onClientReady");
        if (b.e != null) {
            IComOpenClient unused = b.e = null;
        }
        IComOpenClient unused2 = b.e = IComOpenClient.a.a(iBinder);
        b.a(this.a);
        boolean unused3 = b.t = true;
    }
}
