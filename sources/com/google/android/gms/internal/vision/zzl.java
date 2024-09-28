package com.google.android.gms.internal.vision;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.dynamite.DynamiteModule;

public abstract class zzl<T> {
    private static String PREFIX = "com.google.android.gms.vision.dynamite";
    private final Object lock = new Object();
    private final String tag;
    private final String zzci;
    private final String zzcj;
    private boolean zzck = false;
    private T zzcl;
    private final Context zze;

    public zzl(Context context, String str, String str2) {
        this.zze = context;
        this.tag = str;
        String str3 = PREFIX;
        StringBuilder sb = new StringBuilder(String.valueOf(str3).length() + 1 + String.valueOf(str2).length());
        sb.append(str3);
        sb.append(".");
        sb.append(str2);
        this.zzci = sb.toString();
        this.zzcj = PREFIX;
    }

    public final boolean isOperational() {
        return zzp() != null;
    }

    /* access modifiers changed from: protected */
    public abstract T zza(DynamiteModule dynamiteModule, Context context) throws RemoteException, DynamiteModule.LoadingException;

    /* access modifiers changed from: protected */
    public abstract void zzm() throws RemoteException;

    public final void zzo() {
        synchronized (this.lock) {
            if (this.zzcl != null) {
                try {
                    zzm();
                } catch (RemoteException e) {
                    Log.e(this.tag, "Could not finalize native handle", e);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0017, code lost:
        r1 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        android.util.Log.d(r5.tag, "Cannot load feature, fall back to load whole module.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r1 = com.google.android.gms.dynamite.DynamiteModule.load(r5.zze, com.google.android.gms.dynamite.DynamiteModule.PREFER_HIGHEST_OR_REMOTE_VERSION, r5.zzcj);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002c, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        android.util.Log.e(r5.tag, "Error Loading module", r2);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0017 A[ExcHandler: RemoteException (e android.os.RemoteException), Splitter:B:9:0x000c] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x005a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final T zzp() {
        /*
            r5 = this;
            java.lang.Object r0 = r5.lock
            monitor-enter(r0)
            T r1 = r5.zzcl     // Catch:{ all -> 0x006d }
            if (r1 == 0) goto L_0x000b
            T r1 = r5.zzcl     // Catch:{ all -> 0x006d }
            monitor-exit(r0)     // Catch:{ all -> 0x006d }
            return r1
        L_0x000b:
            r1 = 0
            android.content.Context r2 = r5.zze     // Catch:{ LoadingException -> 0x0019, RemoteException -> 0x0017 }
            com.google.android.gms.dynamite.DynamiteModule$VersionPolicy r3 = com.google.android.gms.dynamite.DynamiteModule.PREFER_HIGHEST_OR_REMOTE_VERSION     // Catch:{ LoadingException -> 0x0019, RemoteException -> 0x0017 }
            java.lang.String r4 = r5.zzci     // Catch:{ LoadingException -> 0x0019, RemoteException -> 0x0017 }
            com.google.android.gms.dynamite.DynamiteModule r1 = com.google.android.gms.dynamite.DynamiteModule.load(r2, r3, r4)     // Catch:{ LoadingException -> 0x0019, RemoteException -> 0x0017 }
            goto L_0x0034
        L_0x0017:
            r1 = move-exception
            goto L_0x0040
        L_0x0019:
            r2 = move-exception
            java.lang.String r2 = r5.tag     // Catch:{ LoadingException -> 0x003f, RemoteException -> 0x0017 }
            java.lang.String r3 = "Cannot load feature, fall back to load whole module."
            android.util.Log.d(r2, r3)     // Catch:{ LoadingException -> 0x003f, RemoteException -> 0x0017 }
            android.content.Context r2 = r5.zze     // Catch:{ LoadingException -> 0x002c, RemoteException -> 0x0017 }
            com.google.android.gms.dynamite.DynamiteModule$VersionPolicy r3 = com.google.android.gms.dynamite.DynamiteModule.PREFER_HIGHEST_OR_REMOTE_VERSION     // Catch:{ LoadingException -> 0x002c, RemoteException -> 0x0017 }
            java.lang.String r4 = r5.zzcj     // Catch:{ LoadingException -> 0x002c, RemoteException -> 0x0017 }
            com.google.android.gms.dynamite.DynamiteModule r1 = com.google.android.gms.dynamite.DynamiteModule.load(r2, r3, r4)     // Catch:{ LoadingException -> 0x002c, RemoteException -> 0x0017 }
            goto L_0x0034
        L_0x002c:
            r2 = move-exception
            java.lang.String r3 = r5.tag     // Catch:{ LoadingException -> 0x003f, RemoteException -> 0x0017 }
            java.lang.String r4 = "Error Loading module"
            android.util.Log.e(r3, r4, r2)     // Catch:{ LoadingException -> 0x003f, RemoteException -> 0x0017 }
        L_0x0034:
            if (r1 == 0) goto L_0x0047
            android.content.Context r2 = r5.zze     // Catch:{ LoadingException -> 0x003f, RemoteException -> 0x0017 }
            java.lang.Object r1 = r5.zza(r1, r2)     // Catch:{ LoadingException -> 0x003f, RemoteException -> 0x0017 }
            r5.zzcl = r1     // Catch:{ LoadingException -> 0x003f, RemoteException -> 0x0017 }
            goto L_0x0047
        L_0x003f:
            r1 = move-exception
        L_0x0040:
            java.lang.String r2 = r5.tag     // Catch:{ all -> 0x006d }
            java.lang.String r3 = "Error creating remote native handle"
            android.util.Log.e(r2, r3, r1)     // Catch:{ all -> 0x006d }
        L_0x0047:
            boolean r1 = r5.zzck     // Catch:{ all -> 0x006d }
            if (r1 != 0) goto L_0x005a
            T r1 = r5.zzcl     // Catch:{ all -> 0x006d }
            if (r1 != 0) goto L_0x005a
            java.lang.String r1 = r5.tag     // Catch:{ all -> 0x006d }
            java.lang.String r2 = "Native handle not yet available. Reverting to no-op handle."
            android.util.Log.w(r1, r2)     // Catch:{ all -> 0x006d }
            r1 = 1
            r5.zzck = r1     // Catch:{ all -> 0x006d }
            goto L_0x0069
        L_0x005a:
            boolean r1 = r5.zzck     // Catch:{ all -> 0x006d }
            if (r1 == 0) goto L_0x0069
            T r1 = r5.zzcl     // Catch:{ all -> 0x006d }
            if (r1 == 0) goto L_0x0069
            java.lang.String r1 = r5.tag     // Catch:{ all -> 0x006d }
            java.lang.String r2 = "Native handle is now available."
            android.util.Log.w(r1, r2)     // Catch:{ all -> 0x006d }
        L_0x0069:
            T r1 = r5.zzcl     // Catch:{ all -> 0x006d }
            monitor-exit(r0)     // Catch:{ all -> 0x006d }
            return r1
        L_0x006d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x006d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzl.zzp():java.lang.Object");
    }
}
