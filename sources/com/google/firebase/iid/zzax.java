package com.google.firebase.iid;

import android.text.TextUtils;
import androidx.collection.ArrayMap;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;

/* compiled from: com.google.firebase:firebase-iid@@20.0.2 */
final class zzax {
    private int zza = 0;
    private final Map<Integer, TaskCompletionSource<Void>> zzb = new ArrayMap();
    private final zzat zzc;

    zzax(zzat zzat) {
        this.zzc = zzat;
    }

    /* access modifiers changed from: package-private */
    public final synchronized Task<Void> zza(String str) {
        String zza2;
        TaskCompletionSource taskCompletionSource;
        int i;
        synchronized (this.zzc) {
            zza2 = this.zzc.zza();
            zzat zzat = this.zzc;
            StringBuilder sb = new StringBuilder(String.valueOf(zza2).length() + 1 + String.valueOf(str).length());
            sb.append(zza2);
            sb.append(",");
            sb.append(str);
            zzat.zza(sb.toString());
        }
        taskCompletionSource = new TaskCompletionSource();
        Map<Integer, TaskCompletionSource<Void>> map = this.zzb;
        if (TextUtils.isEmpty(zza2)) {
            i = 0;
        } else {
            i = zza2.split(",").length - 1;
        }
        map.put(Integer.valueOf(this.zza + i), taskCompletionSource);
        return taskCompletionSource.getTask();
    }

    /* access modifiers changed from: package-private */
    public final synchronized boolean zza() {
        return zzb() != null;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001c, code lost:
        if (zza(r5, r0) != false) goto L_0x0020;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001e, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2 = r4.zzb.remove(java.lang.Integer.valueOf(r4.zza));
        zzb(r0);
        r4.zza++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0037, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0038, code lost:
        if (r2 == null) goto L_0x0000;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003a, code lost:
        r2.setResult(null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0016, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean zza(com.google.firebase.iid.FirebaseInstanceId r5) throws java.io.IOException {
        /*
            r4 = this;
        L_0x0000:
            monitor-enter(r4)
            java.lang.String r0 = r4.zzb()     // Catch:{ all -> 0x0042 }
            r1 = 1
            if (r0 != 0) goto L_0x0017
            boolean r5 = com.google.firebase.iid.FirebaseInstanceId.zzd()     // Catch:{ all -> 0x0042 }
            if (r5 == 0) goto L_0x0015
            java.lang.String r5 = "FirebaseInstanceId"
            java.lang.String r0 = "topic sync succeeded"
            android.util.Log.d(r5, r0)     // Catch:{ all -> 0x0042 }
        L_0x0015:
            monitor-exit(r4)     // Catch:{ all -> 0x0042 }
            return r1
        L_0x0017:
            monitor-exit(r4)     // Catch:{ all -> 0x0042 }
            boolean r2 = zza(r5, r0)
            if (r2 != 0) goto L_0x0020
            r5 = 0
            return r5
        L_0x0020:
            monitor-enter(r4)
            java.util.Map<java.lang.Integer, com.google.android.gms.tasks.TaskCompletionSource<java.lang.Void>> r2 = r4.zzb     // Catch:{ all -> 0x003f }
            int r3 = r4.zza     // Catch:{ all -> 0x003f }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x003f }
            java.lang.Object r2 = r2.remove(r3)     // Catch:{ all -> 0x003f }
            com.google.android.gms.tasks.TaskCompletionSource r2 = (com.google.android.gms.tasks.TaskCompletionSource) r2     // Catch:{ all -> 0x003f }
            r4.zzb(r0)     // Catch:{ all -> 0x003f }
            int r0 = r4.zza     // Catch:{ all -> 0x003f }
            int r0 = r0 + r1
            r4.zza = r0     // Catch:{ all -> 0x003f }
            monitor-exit(r4)     // Catch:{ all -> 0x003f }
            if (r2 == 0) goto L_0x003e
            r0 = 0
            r2.setResult(r0)
        L_0x003e:
            goto L_0x0000
        L_0x003f:
            r5 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x003f }
            throw r5
        L_0x0042:
            r5 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0042 }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.iid.zzax.zza(com.google.firebase.iid.FirebaseInstanceId):boolean");
    }

    private final String zzb() {
        String zza2;
        synchronized (this.zzc) {
            zza2 = this.zzc.zza();
        }
        if (TextUtils.isEmpty(zza2)) {
            return null;
        }
        String[] split = zza2.split(",");
        if (split.length <= 1 || TextUtils.isEmpty(split[1])) {
            return null;
        }
        return split[1];
    }

    private final synchronized boolean zzb(String str) {
        synchronized (this.zzc) {
            String zza2 = this.zzc.zza();
            String valueOf = String.valueOf(",");
            String valueOf2 = String.valueOf(str);
            if (!zza2.startsWith(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf))) {
                return false;
            }
            String valueOf3 = String.valueOf(",");
            String valueOf4 = String.valueOf(str);
            this.zzc.zza(zza2.substring((valueOf4.length() != 0 ? valueOf3.concat(valueOf4) : new String(valueOf3)).length()));
            return true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0035 A[Catch:{ IOException -> 0x0057 }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0047 A[Catch:{ IOException -> 0x0057 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean zza(com.google.firebase.iid.FirebaseInstanceId r7, java.lang.String r8) throws java.io.IOException {
        /*
            java.lang.String r0 = "FirebaseInstanceId"
            java.lang.String r1 = "!"
            java.lang.String[] r8 = r8.split(r1)
            int r1 = r8.length
            r2 = 1
            r3 = 2
            if (r1 != r3) goto L_0x00a6
            r1 = 0
            r3 = r8[r1]
            r8 = r8[r2]
            r4 = -1
            int r5 = r3.hashCode()     // Catch:{ IOException -> 0x0057 }
            r6 = 83
            if (r5 == r6) goto L_0x002a
            r6 = 85
            if (r5 == r6) goto L_0x0020
        L_0x001f:
            goto L_0x0033
        L_0x0020:
            java.lang.String r5 = "U"
            boolean r3 = r3.equals(r5)     // Catch:{ IOException -> 0x0057 }
            if (r3 == 0) goto L_0x001f
            r4 = 1
            goto L_0x0033
        L_0x002a:
            java.lang.String r5 = "S"
            boolean r3 = r3.equals(r5)     // Catch:{ IOException -> 0x0057 }
            if (r3 == 0) goto L_0x001f
            r4 = 0
        L_0x0033:
            if (r4 == 0) goto L_0x0047
            if (r4 == r2) goto L_0x0038
            goto L_0x0056
        L_0x0038:
            r7.zzc(r8)     // Catch:{ IOException -> 0x0057 }
            boolean r7 = com.google.firebase.iid.FirebaseInstanceId.zzd()     // Catch:{ IOException -> 0x0057 }
            if (r7 == 0) goto L_0x0056
            java.lang.String r7 = "unsubscribe operation succeeded"
            android.util.Log.d(r0, r7)     // Catch:{ IOException -> 0x0057 }
            goto L_0x0056
        L_0x0047:
            r7.zzb((java.lang.String) r8)     // Catch:{ IOException -> 0x0057 }
            boolean r7 = com.google.firebase.iid.FirebaseInstanceId.zzd()     // Catch:{ IOException -> 0x0057 }
            if (r7 == 0) goto L_0x0056
            java.lang.String r7 = "subscribe operation succeeded"
            android.util.Log.d(r0, r7)     // Catch:{ IOException -> 0x0057 }
            goto L_0x00a6
        L_0x0056:
            goto L_0x00a6
        L_0x0057:
            r7 = move-exception
            java.lang.String r8 = r7.getMessage()
            java.lang.String r2 = "SERVICE_NOT_AVAILABLE"
            boolean r8 = r2.equals(r8)
            if (r8 != 0) goto L_0x007e
            java.lang.String r8 = r7.getMessage()
            java.lang.String r2 = "INTERNAL_SERVER_ERROR"
            boolean r8 = r2.equals(r8)
            if (r8 == 0) goto L_0x0071
            goto L_0x007e
        L_0x0071:
            java.lang.String r8 = r7.getMessage()
            if (r8 != 0) goto L_0x007d
            java.lang.String r7 = "Topic operation failed without exception message. Will retry Topic operation."
            android.util.Log.e(r0, r7)
            return r1
        L_0x007d:
            throw r7
        L_0x007e:
            java.lang.String r7 = r7.getMessage()
            java.lang.String r8 = java.lang.String.valueOf(r7)
            int r8 = r8.length()
            int r8 = r8 + 53
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>(r8)
            java.lang.String r8 = "Topic operation failed: "
            r2.append(r8)
            r2.append(r7)
            java.lang.String r7 = ". Will retry Topic operation."
            r2.append(r7)
            java.lang.String r7 = r2.toString()
            android.util.Log.e(r0, r7)
            return r1
        L_0x00a6:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.iid.zzax.zza(com.google.firebase.iid.FirebaseInstanceId, java.lang.String):boolean");
    }
}
