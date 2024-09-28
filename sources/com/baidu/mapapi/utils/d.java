package com.baidu.mapapi.utils;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.baidu.mapframework.open.aidl.a;

final class d implements ServiceConnection {
    d() {
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (b.v != null) {
            b.v.interrupt();
        }
        String b = b.c;
        Log.d(b, "onServiceConnected " + componentName);
        try {
            if (b.d != null) {
                a unused = b.d = null;
            }
            a unused2 = b.d = a.C0015a.a(iBinder);
            b.d.a(new e(this));
        } catch (RemoteException e) {
            Log.d(b.c, "getComOpenClient ", e);
            if (b.d != null) {
                a unused3 = b.d = null;
            }
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        String b = b.c;
        Log.d(b, "onServiceDisconnected " + componentName);
        if (b.d != null) {
            a unused = b.d = null;
            boolean unused2 = b.u = false;
        }
    }
}
