package com.baidu.location.indoor.mapversion;

import android.os.Build;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class a {
    private static Lock a = new ReentrantLock();

    public static void a() {
        a.lock();
        try {
            IndoorJni.stopPdr();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            a.unlock();
            throw th;
        }
        a.unlock();
    }

    public static synchronized void a(int i, float[] fArr, long j) {
        Lock lock;
        synchronized (a.class) {
            a.lock();
            try {
                if (b() && fArr != null && fArr.length >= 3) {
                    IndoorJni.phs(i, fArr[0], fArr[1], fArr[2], j);
                }
                lock = a;
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                    lock = a;
                } catch (Throwable th) {
                    a.unlock();
                    throw th;
                }
            }
            lock.unlock();
        }
        return;
    }

    public static boolean b() {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        return IndoorJni.a;
    }

    /* JADX INFO: finally extract failed */
    public static float[] c() {
        a.lock();
        try {
            float[] pgo = IndoorJni.pgo();
            a.unlock();
            return pgo;
        } catch (Exception e) {
            e.printStackTrace();
            a.unlock();
            return null;
        } catch (Throwable th) {
            a.unlock();
            throw th;
        }
    }
}
