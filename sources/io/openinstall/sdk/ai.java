package io.openinstall.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import java.lang.ref.WeakReference;

public class ai {
    private static ai a;
    private final boolean b;
    private an c;
    private boolean d = true;
    /* access modifiers changed from: private */
    public WeakReference<Activity> e = null;
    private Application f;
    private Application.ActivityLifecycleCallbacks g;
    /* access modifiers changed from: private */
    public final Runnable h = new ak(this);

    private ai(Context context) {
        boolean booleanValue = c.a().f().booleanValue();
        this.b = booleanValue;
        if (booleanValue) {
            this.c = new an(context);
            this.f = (Application) context.getApplicationContext();
            aj ajVar = new aj(this);
            this.g = ajVar;
            this.f.registerActivityLifecycleCallbacks(ajVar);
        } else if (cb.a) {
            cb.a("clipBoardEnabled = false", new Object[0]);
        }
    }

    public static ai a(Context context) {
        if (a == null) {
            synchronized (ai.class) {
                if (a == null) {
                    a = new ai(context);
                }
            }
        }
        return a;
    }

    public void a(String str) {
        if (this.b && this.d) {
            if (cb.a) {
                cb.a("%s release", str);
            }
            this.c.b();
        }
    }

    public void a(WeakReference<Activity> weakReference) {
        if (this.b && weakReference != null) {
            this.c.a(weakReference);
        }
    }

    public void a(boolean z) {
        this.d = z;
    }

    public boolean a() {
        return this.b;
    }

    public al b() {
        return b(false);
    }

    public al b(boolean z) {
        Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;
        if (!this.b) {
            return null;
        }
        al a2 = al.a(z ? this.c.e() : this.c.d());
        if (a2 != null) {
            if (cb.a) {
                cb.a("data type is %d", Integer.valueOf(a2.c()));
            }
            Application application = this.f;
            if (!(application == null || (activityLifecycleCallbacks = this.g) == null)) {
                application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
                this.g = null;
            }
        } else if (cb.a) {
            cb.a("data is null", new Object[0]);
        }
        return a2;
    }

    public void b(String str) {
        if (this.b && this.d) {
            if (cb.a) {
                cb.a("%s access", str);
            }
            this.c.a();
        }
    }
}
