package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

final class zzed<T> implements zzen<T> {
    private final zzdx zzni;
    private final boolean zznj;
    private final zzff<?, ?> zzns;
    private final zzcg<?> zznt;

    private zzed(zzff<?, ?> zzff, zzcg<?> zzcg, zzdx zzdx) {
        this.zzns = zzff;
        this.zznj = zzcg.zze(zzdx);
        this.zznt = zzcg;
        this.zzni = zzdx;
    }

    static <T> zzed<T> zza(zzff<?, ?> zzff, zzcg<?> zzcg, zzdx zzdx) {
        return new zzed<>(zzff, zzcg, zzdx);
    }

    public final boolean equals(T t, T t2) {
        if (!this.zzns.zzr(t).equals(this.zzns.zzr(t2))) {
            return false;
        }
        if (this.zznj) {
            return this.zznt.zzb(t).equals(this.zznt.zzb(t2));
        }
        return true;
    }

    public final int hashCode(T t) {
        int hashCode = this.zzns.zzr(t).hashCode();
        return this.zznj ? (hashCode * 53) + this.zznt.zzb(t).hashCode() : hashCode;
    }

    public final T newInstance() {
        return this.zzni.zzbv().zzbz();
    }

    public final void zza(T t, zzfz zzfz) throws IOException {
        Iterator<Map.Entry<?, Object>> it = this.zznt.zzb(t).iterator();
        while (it.hasNext()) {
            Map.Entry next = it.next();
            zzcl zzcl = (zzcl) next.getKey();
            if (zzcl.zzbp() != zzfy.MESSAGE || zzcl.zzbq() || zzcl.zzbr()) {
                throw new IllegalStateException("Found invalid MessageSet item.");
            }
            zzfz.zza(zzcl.zzbn(), next instanceof zzdc ? ((zzdc) next).zzcj().zzak() : next.getValue());
        }
        zzff<?, ?> zzff = this.zzns;
        zzff.zzc(zzff.zzr(t), zzfz);
    }

    public final void zza(T t, byte[] bArr, int i, int i2, zzbl zzbl) throws IOException {
        zzcr zzcr = (zzcr) t;
        zzfg zzfg = zzcr.zzkr;
        if (zzfg == zzfg.zzdu()) {
            zzfg = zzfg.zzdv();
            zzcr.zzkr = zzfg;
        }
        zzfg zzfg2 = zzfg;
        while (i < i2) {
            int zza = zzbk.zza(bArr, i, zzbl);
            int i3 = zzbl.zzgo;
            if (i3 != 11) {
                i = (i3 & 7) == 2 ? zzbk.zza(i3, bArr, zza, i2, zzfg2, zzbl) : zzbk.zza(i3, bArr, zza, i2, zzbl);
            } else {
                int i4 = 0;
                zzbo zzbo = null;
                while (zza < i2) {
                    zza = zzbk.zza(bArr, zza, zzbl);
                    int i5 = zzbl.zzgo;
                    int i6 = i5 >>> 3;
                    int i7 = i5 & 7;
                    if (i6 != 2) {
                        if (i6 == 3 && i7 == 2) {
                            zza = zzbk.zze(bArr, zza, zzbl);
                            zzbo = (zzbo) zzbl.zzgq;
                        }
                    } else if (i7 == 0) {
                        zza = zzbk.zza(bArr, zza, zzbl);
                        i4 = zzbl.zzgo;
                    }
                    if (i5 == 12) {
                        break;
                    }
                    zza = zzbk.zza(i5, bArr, zza, i2, zzbl);
                }
                if (zzbo != null) {
                    zzfg2.zzb((i4 << 3) | 2, zzbo);
                }
                i = zza;
            }
        }
        if (i != i2) {
            throw zzcx.zzcf();
        }
    }

    public final void zzc(T t, T t2) {
        zzep.zza(this.zzns, t, t2);
        if (this.zznj) {
            zzep.zza(this.zznt, t, t2);
        }
    }

    public final void zzd(T t) {
        this.zzns.zzd(t);
        this.zznt.zzd(t);
    }

    public final int zzn(T t) {
        zzff<?, ?> zzff = this.zzns;
        int zzs = zzff.zzs(zzff.zzr(t)) + 0;
        return this.zznj ? zzs + this.zznt.zzb(t).zzbm() : zzs;
    }

    public final boolean zzp(T t) {
        return this.zznt.zzb(t).isInitialized();
    }
}
