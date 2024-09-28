package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzcr;
import java.io.IOException;
import java.util.Map;

final class zzch extends zzcg<Object> {
    zzch() {
    }

    /* access modifiers changed from: package-private */
    public final int zza(Map.Entry<?, ?> entry) {
        entry.getKey();
        throw new NoSuchMethodError();
    }

    /* access modifiers changed from: package-private */
    public final void zza(zzfz zzfz, Map.Entry<?, ?> entry) throws IOException {
        entry.getKey();
        throw new NoSuchMethodError();
    }

    /* access modifiers changed from: package-private */
    public final void zza(Object obj, zzcj<Object> zzcj) {
        ((zzcr.zzc) obj).zzkx = zzcj;
    }

    /* access modifiers changed from: package-private */
    public final zzcj<Object> zzb(Object obj) {
        return ((zzcr.zzc) obj).zzkx;
    }

    /* access modifiers changed from: package-private */
    public final zzcj<Object> zzc(Object obj) {
        zzcj<Object> zzb = zzb(obj);
        if (!zzb.isImmutable()) {
            return zzb;
        }
        zzcj<Object> zzcj = (zzcj) zzb.clone();
        zza(obj, zzcj);
        return zzcj;
    }

    /* access modifiers changed from: package-private */
    public final void zzd(Object obj) {
        zzb(obj).zzao();
    }

    /* access modifiers changed from: package-private */
    public final boolean zze(zzdx zzdx) {
        return zzdx instanceof zzcr.zzc;
    }
}
