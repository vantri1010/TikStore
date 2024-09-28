package io.openinstall.sdk;

import android.content.Context;
import android.text.TextUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ah {
    /* access modifiers changed from: private */
    public static final LinkedBlockingQueue<String> b = new LinkedBlockingQueue<>();
    private String a = null;

    static class a implements InvocationHandler {
        a() {
        }

        public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
            Object invoke;
            Class<?> cls;
            String str = "NULL";
            try {
                if ("OnSupport".equals(method.getName())) {
                    if (objArr[0] != null ? Boolean.parseBoolean(String.valueOf(objArr[0])) : false) {
                        try {
                            cls = Class.forName("com.bun.miitmdid.interfaces.IdSupplier");
                        } catch (ClassNotFoundException e) {
                            cls = Class.forName("com.bun.supplier.IdSupplier");
                        }
                        invoke = cls.getDeclaredMethod("getOAID", new Class[0]).invoke(objArr[1], new Object[0]);
                    } else {
                        if (cb.a) {
                            cb.b("IdSupplier isSupport = false", new Object[0]);
                        }
                        ah.b.offer(str);
                        return null;
                    }
                } else {
                    if ("onSupport".equals(method.getName())) {
                        Class<?> cls2 = Class.forName("com.bun.miitmdid.interfaces.IdSupplier");
                        Object invoke2 = cls2.getDeclaredMethod("isSupported", new Class[0]).invoke(objArr[0], new Object[0]);
                        if (invoke2 != null && Boolean.parseBoolean(invoke2.toString())) {
                            invoke = cls2.getDeclaredMethod("getOAID", new Class[0]).invoke(objArr[0], new Object[0]);
                        } else if (cb.a) {
                            cb.b("IdSupplier isSupport = false", new Object[0]);
                        }
                    } else {
                        if (cb.a) {
                            cb.b("IIdentifierListener invoke %s", method.getName());
                        }
                        str = null;
                    }
                    ah.b.offer(str);
                    return null;
                }
            } catch (ClassNotFoundException e2) {
                cls = Class.forName("com.bun.miitmdid.supplier.IdSupplier");
            } catch (Throwable th) {
                if (cb.a) {
                    cb.b("IdSupplier getOAID failed : %s", th.toString());
                }
            }
            str = (String) invoke;
            ah.b.offer(str);
            return null;
        }
    }

    public String a() {
        return this.a;
    }

    public void a(Context context) {
        Class<?> cls;
        try {
            Class.forName("com.bun.miitmdid.core.JLibrary").getDeclaredMethod("InitEntry", new Class[]{Context.class}).invoke((Object) null, new Object[]{context});
        } catch (ClassNotFoundException e) {
        }
        try {
            cls = Class.forName("com.bun.miitmdid.interfaces.IIdentifierListener");
        } catch (ClassNotFoundException e2) {
            try {
                cls = Class.forName("com.bun.supplier.IIdentifierListener");
            } catch (ClassNotFoundException e3) {
                cls = Class.forName("com.bun.miitmdid.core.IIdentifierListener");
            } catch (Throwable th) {
                if (cb.a) {
                    cb.b("MdidSdkHelper InitSdk failed : %s", th.toString());
                    return;
                }
                return;
            }
        }
        Object newProxyInstance = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{cls}, new a());
        Method declaredMethod = Class.forName("com.bun.miitmdid.core.MdidSdkHelper").getDeclaredMethod("InitSdk", new Class[]{Context.class, Boolean.TYPE, cls});
        String str = null;
        int i = 0;
        while (TextUtils.isEmpty(str) && i < 2) {
            Integer num = (Integer) declaredMethod.invoke((Object) null, new Object[]{context, true, newProxyInstance});
            if (cb.a) {
                cb.a("MdidSdkHelper InitSdk return value：" + num, new Object[0]);
            }
            str = b.poll(1100, TimeUnit.MILLISECONDS);
            if (!"NULL".equals(str)) {
                this.a = str;
                i++;
            } else {
                return;
            }
        }
    }
}
