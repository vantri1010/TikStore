package com.google.android.gms.internal.vision;

final class zzdk extends zzdh {
    private zzdk() {
        super();
    }

    private static <E> zzcw<E> zzc(Object obj, long j) {
        return (zzcw) zzfl.zzo(obj, j);
    }

    /* access modifiers changed from: package-private */
    public final void zza(Object obj, long j) {
        zzc(obj, j).zzao();
    }

    /* access modifiers changed from: package-private */
    public final <E> void zza(Object obj, Object obj2, long j) {
        zzcw zzc = zzc(obj, j);
        zzcw zzc2 = zzc(obj2, j);
        int size = zzc.size();
        int size2 = zzc2.size();
        if (size > 0 && size2 > 0) {
            if (!zzc.zzan()) {
                zzc = zzc.zzk(size2 + size);
            }
            zzc.addAll(zzc2);
        }
        if (size > 0) {
            zzc2 = zzc;
        }
        zzfl.zza(obj, j, (Object) zzc2);
    }
}
