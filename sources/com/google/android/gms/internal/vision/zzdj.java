package com.google.android.gms.internal.vision;

import java.util.Collections;
import java.util.List;

final class zzdj extends zzdh {
    private static final Class<?> zzmq = Collections.unmodifiableList(Collections.emptyList()).getClass();

    private zzdj() {
        super();
    }

    private static <E> List<E> zzb(Object obj, long j) {
        return (List) zzfl.zzo(obj, j);
    }

    /* access modifiers changed from: package-private */
    public final void zza(Object obj, long j) {
        Object obj2;
        List list = (List) zzfl.zzo(obj, j);
        if (list instanceof zzdg) {
            obj2 = ((zzdg) list).zzcl();
        } else if (!zzmq.isAssignableFrom(list.getClass())) {
            obj2 = Collections.unmodifiableList(list);
        } else {
            return;
        }
        zzfl.zza(obj, j, obj2);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: com.google.android.gms.internal.vision.zzdf} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: com.google.android.gms.internal.vision.zzdf} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: com.google.android.gms.internal.vision.zzdf} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final <E> void zza(java.lang.Object r5, java.lang.Object r6, long r7) {
        /*
            r4 = this;
            java.util.List r6 = zzb(r6, r7)
            int r0 = r6.size()
            java.util.List r1 = zzb(r5, r7)
            boolean r2 = r1.isEmpty()
            if (r2 == 0) goto L_0x0025
            boolean r1 = r1 instanceof com.google.android.gms.internal.vision.zzdg
            if (r1 == 0) goto L_0x001c
            com.google.android.gms.internal.vision.zzdf r1 = new com.google.android.gms.internal.vision.zzdf
            r1.<init>((int) r0)
            goto L_0x0021
        L_0x001c:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>(r0)
        L_0x0021:
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r5, (long) r7, (java.lang.Object) r1)
            goto L_0x0057
        L_0x0025:
            java.lang.Class<?> r2 = zzmq
            java.lang.Class r3 = r1.getClass()
            boolean r2 = r2.isAssignableFrom(r3)
            if (r2 == 0) goto L_0x0043
            java.util.ArrayList r2 = new java.util.ArrayList
            int r3 = r1.size()
            int r3 = r3 + r0
            r2.<init>(r3)
            r2.addAll(r1)
        L_0x003e:
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r5, (long) r7, (java.lang.Object) r2)
            r1 = r2
            goto L_0x0057
        L_0x0043:
            boolean r2 = r1 instanceof com.google.android.gms.internal.vision.zzfi
            if (r2 == 0) goto L_0x0057
            com.google.android.gms.internal.vision.zzdf r2 = new com.google.android.gms.internal.vision.zzdf
            int r3 = r1.size()
            int r3 = r3 + r0
            r2.<init>((int) r3)
            com.google.android.gms.internal.vision.zzfi r1 = (com.google.android.gms.internal.vision.zzfi) r1
            r2.addAll(r1)
            goto L_0x003e
        L_0x0057:
            int r0 = r1.size()
            int r2 = r6.size()
            if (r0 <= 0) goto L_0x0066
            if (r2 <= 0) goto L_0x0066
            r1.addAll(r6)
        L_0x0066:
            if (r0 <= 0) goto L_0x0069
            r6 = r1
        L_0x0069:
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r5, (long) r7, (java.lang.Object) r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzdj.zza(java.lang.Object, java.lang.Object, long):void");
    }
}
