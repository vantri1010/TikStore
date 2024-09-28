package com.baidu.location;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

class b implements ServiceConnection {
    final /* synthetic */ LocationClient a;

    b(LocationClient locationClient) {
        this.a = locationClient;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Messenger unused = this.a.g = new Messenger(iBinder);
        if (this.a.g != null) {
            boolean unused2 = this.a.e = true;
            Log.d("baidu_location_client", "baidu location connected ...");
            if (this.a.z) {
                this.a.h.obtainMessage(2).sendToTarget();
                return;
            }
            try {
                Message obtain = Message.obtain((Handler) null, 11);
                obtain.replyTo = this.a.i;
                obtain.setData(this.a.d());
                this.a.g.send(obtain);
                boolean unused3 = this.a.e = true;
                if (this.a.c != null) {
                    this.a.C.booleanValue();
                    this.a.h.obtainMessage(4).sendToTarget();
                }
            } catch (Exception e) {
            }
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        Messenger unused = this.a.g = null;
        boolean unused2 = this.a.e = false;
    }
}
