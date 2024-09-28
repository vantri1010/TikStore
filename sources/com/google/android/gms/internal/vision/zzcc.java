package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzcr;
import java.io.IOException;
import java.util.List;
import java.util.Map;

final class zzcc implements zzfz {
    private final zzca zzgz;

    private zzcc(zzca zzca) {
        zzca zzca2 = (zzca) zzct.zza(zzca, "output");
        this.zzgz = zzca2;
        zzca2.zzhk = this;
    }

    public static zzcc zza(zzca zzca) {
        return zzca.zzhk != null ? zzca.zzhk : new zzcc(zzca);
    }

    public final void zza(int i, double d) throws IOException {
        this.zzgz.zza(i, d);
    }

    public final void zza(int i, float f) throws IOException {
        this.zzgz.zza(i, f);
    }

    public final void zza(int i, long j) throws IOException {
        this.zzgz.zza(i, j);
    }

    public final void zza(int i, zzbo zzbo) throws IOException {
        this.zzgz.zza(i, zzbo);
    }

    public final <K, V> void zza(int i, zzdq<K, V> zzdq, Map<K, V> map) throws IOException {
        for (Map.Entry next : map.entrySet()) {
            this.zzgz.zzd(i, 2);
            this.zzgz.zzq(zzdp.zza(zzdq, next.getKey(), next.getValue()));
            zzdp.zza(this.zzgz, zzdq, next.getKey(), next.getValue());
        }
    }

    public final void zza(int i, Object obj) throws IOException {
        if (obj instanceof zzbo) {
            this.zzgz.zzb(i, (zzbo) obj);
        } else {
            this.zzgz.zza(i, (zzdx) obj);
        }
    }

    public final void zza(int i, Object obj, zzen zzen) throws IOException {
        this.zzgz.zza(i, (zzdx) obj, zzen);
    }

    public final void zza(int i, String str) throws IOException {
        this.zzgz.zza(i, str);
    }

    public final void zza(int i, List<String> list) throws IOException {
        int i2 = 0;
        if (list instanceof zzdg) {
            zzdg zzdg = (zzdg) list;
            while (i2 < list.size()) {
                Object raw = zzdg.getRaw(i2);
                if (raw instanceof String) {
                    this.zzgz.zza(i, (String) raw);
                } else {
                    this.zzgz.zza(i, (zzbo) raw);
                }
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zza(i, list.get(i2));
            i2++;
        }
    }

    public final void zza(int i, List<?> list, zzen zzen) throws IOException {
        for (int i2 = 0; i2 < list.size(); i2++) {
            zza(i, (Object) list.get(i2), zzen);
        }
    }

    public final void zza(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzu(list.get(i4).intValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzp(list.get(i2).intValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zze(i, list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzac(int i) throws IOException {
        this.zzgz.zzd(i, 3);
    }

    public final void zzad(int i) throws IOException {
        this.zzgz.zzd(i, 4);
    }

    public final void zzb(int i, long j) throws IOException {
        this.zzgz.zzb(i, j);
    }

    public final void zzb(int i, Object obj, zzen zzen) throws IOException {
        zzca zzca = this.zzgz;
        zzca.zzd(i, 3);
        zzen.zza((zzdx) obj, zzca.zzhk);
        zzca.zzd(i, 4);
    }

    public final void zzb(int i, List<zzbo> list) throws IOException {
        for (int i2 = 0; i2 < list.size(); i2++) {
            this.zzgz.zza(i, list.get(i2));
        }
    }

    public final void zzb(int i, List<?> list, zzen zzen) throws IOException {
        for (int i2 = 0; i2 < list.size(); i2++) {
            zzb(i, (Object) list.get(i2), zzen);
        }
    }

    public final void zzb(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzx(list.get(i4).intValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzs(list.get(i2).intValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zzh(i, list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzb(int i, boolean z) throws IOException {
        this.zzgz.zzb(i, z);
    }

    public final int zzbc() {
        return zzcr.zzd.zzlj;
    }

    public final void zzc(int i, long j) throws IOException {
        this.zzgz.zzc(i, j);
    }

    public final void zzc(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zze(list.get(i4).longValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzb(list.get(i2).longValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zza(i, list.get(i2).longValue());
            i2++;
        }
    }

    public final void zzd(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzf(list.get(i4).longValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzb(list.get(i2).longValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zza(i, list.get(i2).longValue());
            i2++;
        }
    }

    public final void zze(int i, int i2) throws IOException {
        this.zzgz.zze(i, i2);
    }

    public final void zze(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzh(list.get(i4).longValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzd(list.get(i2).longValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zzc(i, list.get(i2).longValue());
            i2++;
        }
    }

    public final void zzf(int i, int i2) throws IOException {
        this.zzgz.zzf(i, i2);
    }

    public final void zzf(int i, List<Float> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzd(list.get(i4).floatValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzc(list.get(i2).floatValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zza(i, list.get(i2).floatValue());
            i2++;
        }
    }

    public final void zzg(int i, int i2) throws IOException {
        this.zzgz.zzg(i, i2);
    }

    public final void zzg(int i, List<Double> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzb(list.get(i4).doubleValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zza(list.get(i2).doubleValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zza(i, list.get(i2).doubleValue());
            i2++;
        }
    }

    public final void zzh(int i, int i2) throws IOException {
        this.zzgz.zzh(i, i2);
    }

    public final void zzh(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzz(list.get(i4).intValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzp(list.get(i2).intValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zze(i, list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzi(int i, long j) throws IOException {
        this.zzgz.zza(i, j);
    }

    public final void zzi(int i, List<Boolean> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzb(list.get(i4).booleanValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zza(list.get(i2).booleanValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zzb(i, list.get(i2).booleanValue());
            i2++;
        }
    }

    public final void zzj(int i, long j) throws IOException {
        this.zzgz.zzc(i, j);
    }

    public final void zzj(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzv(list.get(i4).intValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzq(list.get(i2).intValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zzf(i, list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzk(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzy(list.get(i4).intValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzs(list.get(i2).intValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zzh(i, list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzl(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzi(list.get(i4).longValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzd(list.get(i2).longValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zzc(i, list.get(i2).longValue());
            i2++;
        }
    }

    public final void zzm(int i, List<Integer> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzw(list.get(i4).intValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzr(list.get(i2).intValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zzg(i, list.get(i2).intValue());
            i2++;
        }
    }

    public final void zzn(int i, List<Long> list, boolean z) throws IOException {
        int i2 = 0;
        if (z) {
            this.zzgz.zzd(i, 2);
            int i3 = 0;
            for (int i4 = 0; i4 < list.size(); i4++) {
                i3 += zzca.zzg(list.get(i4).longValue());
            }
            this.zzgz.zzq(i3);
            while (i2 < list.size()) {
                this.zzgz.zzc(list.get(i2).longValue());
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            this.zzgz.zzb(i, list.get(i2).longValue());
            i2++;
        }
    }

    public final void zzo(int i, int i2) throws IOException {
        this.zzgz.zzh(i, i2);
    }

    public final void zzp(int i, int i2) throws IOException {
        this.zzgz.zze(i, i2);
    }
}
