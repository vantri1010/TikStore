package com.baidu.location.b;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.baidu.location.g.k;

public class x {
    private static Object a = new Object();
    private static x b = null;
    private HandlerThread c;
    /* access modifiers changed from: private */
    public Handler d;
    private boolean e = false;

    x() {
    }

    public static x a() {
        x xVar;
        synchronized (a) {
            if (b == null) {
                b = new x();
            }
            xVar = b;
        }
        return xVar;
    }

    public void a(Location location, int i) {
        if (this.e && location != null) {
            try {
                if (this.d != null) {
                    Message obtainMessage = this.d.obtainMessage(1);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("loc", new Location(location));
                    bundle.putInt("satnum", i);
                    obtainMessage.setData(bundle);
                    obtainMessage.sendToTarget();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void b() {
        if (this.e) {
            try {
                if (this.d != null) {
                    this.d.obtainMessage(3).sendToTarget();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void c() {
        if (this.e) {
            try {
                if (this.d != null) {
                    this.d.obtainMessage(2).sendToTarget();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void d() {
        if (this.e) {
            try {
                if (this.d != null) {
                    this.d.obtainMessage(7).sendToTarget();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void e() {
        if (!this.e) {
            this.e = true;
            if (this.c == null) {
                HandlerThread handlerThread = new HandlerThread("LocUploadThreadManager");
                this.c = handlerThread;
                handlerThread.start();
                if (this.c != null) {
                    this.d = new y(this, this.c.getLooper());
                }
            }
            try {
                if (this.d != null) {
                    this.d.obtainMessage(5).sendToTarget();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (this.d != null) {
                    this.d.sendEmptyMessageDelayed(4, (long) k.R);
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

    public void f() {
        if (this.e) {
            d.a().b();
            try {
                if (this.d != null) {
                    this.d.removeCallbacksAndMessages((Object) null);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.d = null;
            try {
                if (this.c != null) {
                    this.c.quit();
                    this.c.interrupt();
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            this.c = null;
            this.e = false;
        }
    }
}
