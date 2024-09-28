package io.openinstall.sdk;

import android.content.Context;
import android.os.Build;

public class z {
    private static z b;
    private static final Object c = new Object();
    private final aa a;

    private z(Context context) {
        if (c.a().e().isStorageDisabled() || !cd.b(context)) {
            this.a = new x();
        } else {
            this.a = Build.VERSION.SDK_INT >= 29 ? new w(context) : new y(context);
        }
    }

    public static z a(Context context) {
        synchronized (c) {
            if (b == null) {
                b = new z(context.getApplicationContext());
            }
        }
        return b;
    }

    public String a(String str) {
        return this.a.a(str);
    }

    public void a(String str, String str2) {
        this.a.a(str, str2);
    }

    public void b(String str, String str2) {
        String a2 = a(str);
        if (a2 == null || !a2.equals(str2)) {
            a(str, str2);
        }
    }
}
