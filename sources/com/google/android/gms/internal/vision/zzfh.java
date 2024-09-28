package com.google.android.gms.internal.vision;

import java.io.IOException;

final class zzfh extends zzff<zzfg, zzfg> {
    zzfh() {
    }

    private static void zza(Object obj, zzfg zzfg) {
        ((zzcr) obj).zzkr = zzfg;
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ void zza(Object obj, int i, long j) {
        ((zzfg) obj).zzb(i << 3, Long.valueOf(j));
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ void zza(Object obj, int i, zzbo zzbo) {
        ((zzfg) obj).zzb((i << 3) | 2, zzbo);
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ void zza(Object obj, zzfz zzfz) throws IOException {
        ((zzfg) obj).zzb(zzfz);
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ void zzc(Object obj, zzfz zzfz) throws IOException {
        ((zzfg) obj).zza(zzfz);
    }

    /* access modifiers changed from: package-private */
    public final void zzd(Object obj) {
        ((zzcr) obj).zzkr.zzao();
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ Object zzdt() {
        return zzfg.zzdv();
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ void zze(Object obj, Object obj2) {
        zza(obj, (zzfg) obj2);
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ void zzf(Object obj, Object obj2) {
        zza(obj, (zzfg) obj2);
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ Object zzg(Object obj, Object obj2) {
        zzfg zzfg = (zzfg) obj;
        zzfg zzfg2 = (zzfg) obj2;
        return zzfg2.equals(zzfg.zzdu()) ? zzfg : zzfg.zza(zzfg, zzfg2);
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ int zzn(Object obj) {
        return ((zzfg) obj).zzbl();
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ Object zzr(Object obj) {
        return ((zzcr) obj).zzkr;
    }

    /* access modifiers changed from: package-private */
    public final /* synthetic */ int zzs(Object obj) {
        return ((zzfg) obj).zzdw();
    }
}
