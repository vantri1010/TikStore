package io.openinstall.sdk;

import android.content.Context;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ad {
    /* access modifiers changed from: private */
    public static final CountDownLatch a = new CountDownLatch(1);
    /* access modifiers changed from: private */
    public static String b;
    /* access modifiers changed from: private */
    public static Object c;

    static class a implements InvocationHandler {
        a() {
        }

        public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
            if ("onInstallReferrerSetupFinished".equalsIgnoreCase(method.getName())) {
                try {
                    int intValue = objArr[0].intValue();
                    if (cb.a) {
                        cb.a("StateListenerHandler : onInstallReferrerSetupFinished code=" + intValue, new Object[0]);
                    }
                    if (intValue == 0) {
                        Class<?> cls = Class.forName("com.android.installreferrer.api.InstallReferrerClient");
                        String unused = ad.b = (String) Class.forName("com.android.installreferrer.api.ReferrerDetails").getDeclaredMethod("getInstallReferrer", new Class[0]).invoke(cls.getDeclaredMethod("getInstallReferrer", new Class[0]).invoke(ad.c, new Object[0]), new Object[0]);
                        cls.getDeclaredMethod("endConnection", new Class[0]).invoke(ad.c, new Object[0]);
                    }
                } catch (Throwable th) {
                    if (cb.a) {
                        cb.a("InstallReferrerClient getInstallReferrer failed", new Object[0]);
                    }
                }
                ad.a.countDown();
                return null;
            } else if ("onInstallReferrerServiceDisconnected".equalsIgnoreCase(method.getName())) {
                if (!cb.a) {
                    return null;
                }
                cb.a("StateListenerHandler : InstallReferrerService Disconnected", new Object[0]);
                return null;
            } else if (!cb.a) {
                return null;
            } else {
                cb.a("StateListenerHandler : no such method : " + method.getName(), new Object[0]);
                return null;
            }
        }
    }

    public String a() {
        try {
            a.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        if (cb.a) {
            cb.a("PlayInstallReferrer getReferrer : %s", b);
        }
        return b;
    }

    public void a(Context context) {
        try {
            Class<?> cls = Class.forName("com.android.installreferrer.api.InstallReferrerClient");
            c = Class.forName("com.android.installreferrer.api.InstallReferrerClient$Builder").getDeclaredMethod("build", new Class[0]).invoke(cls.getDeclaredMethod("newBuilder", new Class[]{Context.class}).invoke((Object) null, new Object[]{context}), new Object[0]);
            Class<?> cls2 = Class.forName("com.android.installreferrer.api.InstallReferrerStateListener");
            a aVar = new a();
            Object newProxyInstance = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{cls2}, aVar);
            cls.getDeclaredMethod("startConnection", new Class[]{cls2}).invoke(c, new Object[]{newProxyInstance});
        } catch (Throwable th) {
            a.countDown();
            if (cb.a) {
                cb.b("InstallReferrerClient Connection Failed", new Object[0]);
            }
        }
    }
}
