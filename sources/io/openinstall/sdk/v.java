package io.openinstall.sdk;

import android.content.Context;

public abstract class v implements aa {
    protected Context a;
    private volatile String b = null;
    private volatile boolean c = false;

    public v(Context context) {
        this.a = context;
    }

    public synchronized String a(String str) {
        if (!this.c) {
            return b(str);
        }
        return this.b;
    }

    public synchronized void a(String str, String str2) {
        if (str2 != null) {
            if (!this.c || !str2.equals(this.b)) {
                if (b(str, str2)) {
                    this.c = true;
                } else {
                    this.c = false;
                }
                this.b = str2;
            }
        }
    }

    public abstract String b(String str);

    public abstract boolean b(String str, String str2);
}
