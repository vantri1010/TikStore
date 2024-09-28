package com.ta.utdid2.device;

import android.content.Context;
import com.ta.utdid2.a.a.e;
import com.ta.utdid2.a.a.g;
import java.util.zip.Adler32;

public class b {
    private static a a = null;
    static final Object d = new Object();

    static long a(a aVar) {
        if (aVar == null) {
            return 0;
        }
        String format = String.format("%s%s%s%s%s", new Object[]{aVar.f(), aVar.getDeviceId(), Long.valueOf(aVar.a()), aVar.getImsi(), aVar.e()});
        if (g.a(format)) {
            return 0;
        }
        Adler32 adler32 = new Adler32();
        adler32.reset();
        adler32.update(format.getBytes());
        return adler32.getValue();
    }

    private static a a(Context context) {
        if (context == null) {
            return null;
        }
        synchronized (d) {
            String value = c.a(context).getValue();
            if (g.a(value)) {
                return null;
            }
            if (value.endsWith("\n")) {
                value = value.substring(0, value.length() - 1);
            }
            a aVar = new a();
            long currentTimeMillis = System.currentTimeMillis();
            String a2 = e.a(context);
            String c = e.c(context);
            aVar.d(a2);
            aVar.b(a2);
            aVar.b(currentTimeMillis);
            aVar.c(c);
            aVar.e(value);
            aVar.a(a(aVar));
            return aVar;
        }
    }

    public static synchronized a b(Context context) {
        synchronized (b.class) {
            if (a != null) {
                a aVar = a;
                return aVar;
            } else if (context == null) {
                return null;
            } else {
                a a2 = a(context);
                a = a2;
                return a2;
            }
        }
    }
}
