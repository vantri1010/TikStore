package com.baidu.location.f;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.util.Log;
import com.baidu.location.LLSInterface;
import com.baidu.location.b.h;
import com.baidu.location.b.l;
import com.baidu.location.b.o;
import com.baidu.location.b.u;
import com.baidu.location.b.v;
import com.baidu.location.b.w;
import com.baidu.location.b.x;
import com.baidu.location.c.d;
import com.baidu.location.c.e;
import com.baidu.location.e.j;
import com.baidu.location.f;
import com.baidu.location.g.b;
import com.baidu.location.g.k;
import com.baidu.location.indoor.g;
import com.google.android.exoplayer2.offline.DownloadService;
import java.lang.ref.WeakReference;

public class a extends Service implements LLSInterface {
    static C0009a a = null;
    public static long c = 0;
    private static long g = 0;
    Messenger b = null;
    private Looper d = null;
    private HandlerThread e = null;
    private boolean f = false;
    /* access modifiers changed from: private */
    public int h = 0;

    /* renamed from: com.baidu.location.f.a$a  reason: collision with other inner class name */
    public static class C0009a extends Handler {
        private final WeakReference<a> a;

        public C0009a(Looper looper, a aVar) {
            super(looper);
            this.a = new WeakReference<>(aVar);
        }

        public void handleMessage(Message message) {
            a aVar = (a) this.a.get();
            if (aVar != null) {
                if (f.isServing) {
                    int i = message.what;
                    if (i == 11) {
                        aVar.a(message);
                    } else if (i == 12) {
                        aVar.b(message);
                    } else if (i == 15) {
                        aVar.c(message);
                    } else if (i == 22) {
                        l.c().b(message);
                    } else if (i == 41) {
                        l.c().i();
                    } else if (i == 401) {
                        try {
                            message.getData();
                        } catch (Exception e) {
                        }
                    } else if (i == 406) {
                        h.a().e();
                    } else if (i != 705) {
                        switch (i) {
                            case 110:
                                g.a().c();
                                break;
                            case 111:
                                g.a().d();
                                break;
                            case 112:
                                g.a().b();
                                break;
                            case 113:
                                Object obj = message.obj;
                                if (obj != null) {
                                    g.a().a((Bundle) obj);
                                    break;
                                }
                                break;
                            case 114:
                                Object obj2 = message.obj;
                                if (obj2 != null) {
                                    g.a().b((Bundle) obj2);
                                    break;
                                }
                                break;
                        }
                    } else {
                        com.baidu.location.b.a.a().a(message.getData().getBoolean(DownloadService.KEY_FOREGROUND));
                    }
                }
                if (message.what == 1) {
                    aVar.d();
                }
                if (message.what == 0) {
                    aVar.c();
                }
                super.handleMessage(message);
            }
        }
    }

    public static Handler a() {
        return a;
    }

    /* access modifiers changed from: private */
    public void a(Message message) {
        Log.d("baidu_location_service", "baidu location service register ...");
        com.baidu.location.b.a.a().a(message);
        e.a().d();
        if (!k.b()) {
            o.b().c();
        }
    }

    public static long b() {
        return g;
    }

    /* access modifiers changed from: private */
    public void b(Message message) {
        com.baidu.location.b.a.a().b(message);
    }

    /* access modifiers changed from: private */
    public void c() {
        com.baidu.location.a.a.a().a(f.getServiceContext());
        e.a().b();
        b.a();
        try {
            x.a().e();
        } catch (Exception e2) {
        }
        h.a().b();
        com.baidu.location.e.f.a().b();
        com.baidu.location.e.b.a().b();
        l.c().d();
        d.a().b();
        com.baidu.location.c.g.a().b();
        com.baidu.location.c.a.a().b();
        j.a().c();
        this.h = 2;
    }

    /* access modifiers changed from: private */
    public void c(Message message) {
        com.baidu.location.b.a.a().c(message);
    }

    /* access modifiers changed from: private */
    public void d() {
        com.baidu.location.e.f.a().e();
        j.a().e();
        x.a().f();
        e.a().c();
        d.a().c();
        com.baidu.location.c.b.a().c();
        com.baidu.location.c.a.a().c();
        com.baidu.location.b.b.a().b();
        com.baidu.location.e.b.a().c();
        l.c().e();
        g.a().d();
        h.a().c();
        w.d();
        com.baidu.location.b.a.a().b();
        v.a().d();
        this.h = 4;
        Log.d("baidu_location_service", "baidu location service has stoped ...");
        if (!this.f) {
            Process.killProcess(Process.myPid());
        }
    }

    public double getVersion() {
        return 8.220000267028809d;
    }

    public IBinder onBind(Intent intent) {
        boolean z;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            b.h = extras.getString("key");
            b.g = extras.getString("sign");
            this.f = extras.getBoolean("kill_process");
            z = extras.getBoolean("cache_exception");
        } else {
            z = false;
        }
        if (!z) {
            Thread.setDefaultUncaughtExceptionHandler(com.baidu.location.c.g.a());
        }
        return this.b.getBinder();
    }

    public void onCreate(Context context) {
        try {
            k.ax = context.getPackageName();
        } catch (Exception e2) {
        }
        g = System.currentTimeMillis();
        HandlerThread a2 = u.a();
        this.e = a2;
        if (a2 != null) {
            this.d = a2.getLooper();
        }
        a = this.d == null ? new C0009a(Looper.getMainLooper(), this) : new C0009a(this.d, this);
        c = System.currentTimeMillis();
        this.b = new Messenger(a);
        a.sendEmptyMessage(0);
        this.h = 1;
        Log.d("baidu_location_service", "baidu location service start1 ...20191010..." + Process.myPid());
    }

    public void onDestroy() {
        try {
            a.sendEmptyMessage(1);
        } catch (Exception e2) {
            Log.d("baidu_location_service", "baidu location service stop exception...");
            d();
            Process.killProcess(Process.myPid());
        }
        this.h = 3;
        new Handler(Looper.getMainLooper()).postDelayed(new b(this, new WeakReference(this)), 1000);
        Log.d("baidu_location_service", "baidu location service stop ...");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    public void onTaskRemoved(Intent intent) {
        Log.d("baidu_location_service", "baidu location service remove task...");
    }

    public boolean onUnBind(Intent intent) {
        return false;
    }
}
