package com.baidu.mapsdkplatform.comapi;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;
import com.baidu.mapsdkplatform.comapi.util.PermissionCheck;
import com.baidu.mapsdkplatform.comapi.util.SysUpdateObservable;
import com.baidu.mapsdkplatform.comapi.util.c;
import com.baidu.mapsdkplatform.comapi.util.h;

public class a implements PermissionCheck.c {
    private static final String a = a.class.getSimpleName();
    private static a g;
    private static int h = -100;
    private Context b;
    private Handler c;
    private f d;
    private String e;
    private int f;

    static {
        NativeLoader.getInstance().loadLibrary(VersionInfo.getKitName());
        com.baidu.mapsdkplatform.comjni.tools.a.b();
    }

    private a() {
    }

    public static a a() {
        if (g == null) {
            g = new a();
        }
        return g;
    }

    /* access modifiers changed from: private */
    public void a(Message message) {
        Intent intent;
        if (message.what != 2012) {
            if (message.arg2 == 3) {
                this.b.sendBroadcast(new Intent(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR));
            }
            if (message.arg2 == 2 || message.arg2 == 404 || message.arg2 == 5 || message.arg2 == 8) {
                intent = new Intent(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
            } else {
                return;
            }
        } else if (message.arg1 == 0) {
            intent = new Intent(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        } else {
            Intent intent2 = new Intent(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
            intent2.putExtra(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, message.arg1);
            intent2.putExtra(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_MESSAGE, (String) message.obj);
            intent = intent2;
        }
        this.b.sendBroadcast(intent);
    }

    private void f() {
        f fVar;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        Context context = this.b;
        if (context != null && (fVar = this.d) != null) {
            context.registerReceiver(fVar, intentFilter);
        }
    }

    private void g() {
        Context context;
        f fVar = this.d;
        if (fVar != null && (context = this.b) != null) {
            context.unregisterReceiver(fVar);
        }
    }

    public void a(Context context) {
        this.b = context;
    }

    public void a(PermissionCheck.b bVar) {
        if (bVar != null) {
            if (bVar.a == 0) {
                h.d = bVar.e;
                h.a(bVar.b, bVar.c);
            } else {
                Log.e("baidumapsdk", "Authentication Error\n" + bVar.toString());
            }
            if (!(bVar.a == PermissionCheck.b || bVar.a == PermissionCheck.a || bVar.a == PermissionCheck.c)) {
                c.a().a(bVar.f);
            }
            if (this.c != null && bVar.a != h) {
                h = bVar.a;
                Message obtainMessage = this.c.obtainMessage();
                obtainMessage.what = 2012;
                obtainMessage.arg1 = bVar.a;
                obtainMessage.obj = bVar.d;
                this.c.sendMessage(obtainMessage);
            }
        }
    }

    public void a(String str) {
        this.e = str;
    }

    public void b() {
        if (this.f == 0) {
            if (this.b != null) {
                this.d = new f();
                f();
                SysUpdateObservable.getInstance().updateNetworkInfo(this.b);
            } else {
                throw new IllegalStateException("BDMapSDKException: you have not supplyed the global app context info from SDKInitializer.initialize(Context) function.");
            }
        }
        this.f++;
    }

    public boolean c() {
        if (this.b != null) {
            this.c = new b(this);
            h.b(this.b);
            c.a().a(this.b);
            h.f();
            PermissionCheck.init(this.b);
            PermissionCheck.setPermissionCheckResultListener(this);
            PermissionCheck.permissionCheck();
            return true;
        }
        throw new IllegalStateException("BDMapSDKException: you have not supplyed the global app context info from SDKInitializer.initialize(Context) function.");
    }

    public void d() {
        int i = this.f - 1;
        this.f = i;
        if (i == 0) {
            g();
            h.a();
        }
    }

    public Context e() {
        Context context = this.b;
        if (context != null) {
            return context;
        }
        throw new IllegalStateException("BDMapSDKException: you have not supplyed the global app context info from SDKInitializer.initialize(Context) function.");
    }
}
