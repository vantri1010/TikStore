package com.google.android.gms.internal.wearable;

import java.io.IOException;

public final class zzi extends zzn<zzi> {
    private static volatile zzi[] zzgb;
    public int type = 1;
    public zzj zzgc = null;

    public static zzi[] zzi() {
        if (zzgb == null) {
            synchronized (zzr.zzhk) {
                if (zzgb == null) {
                    zzgb = new zzi[0];
                }
            }
        }
        return zzgb;
    }

    public zzi() {
        this.zzhc = null;
        this.zzhl = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzi)) {
            return false;
        }
        zzi zzi = (zzi) obj;
        if (this.type != zzi.type) {
            return false;
        }
        zzj zzj = this.zzgc;
        if (zzj == null) {
            if (zzi.zzgc != null) {
                return false;
            }
        } else if (!zzj.equals(zzi.zzgc)) {
            return false;
        }
        if (this.zzhc != null && !this.zzhc.isEmpty()) {
            return this.zzhc.equals(zzi.zzhc);
        }
        if (zzi.zzhc == null || zzi.zzhc.isEmpty()) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int hashCode = ((getClass().getName().hashCode() + 527) * 31) + this.type;
        zzj zzj = this.zzgc;
        int i = 0;
        int hashCode2 = ((hashCode * 31) + (zzj == null ? 0 : zzj.hashCode())) * 31;
        if (this.zzhc != null && !this.zzhc.isEmpty()) {
            i = this.zzhc.hashCode();
        }
        return hashCode2 + i;
    }

    public final void zza(zzl zzl) throws IOException {
        zzl.zzd(1, this.type);
        zzj zzj = this.zzgc;
        if (zzj != null) {
            zzl.zza(2, (zzt) zzj);
        }
        super.zza(zzl);
    }

    /* access modifiers changed from: protected */
    public final int zzg() {
        int zzg = super.zzg() + zzl.zze(1, this.type);
        zzj zzj = this.zzgc;
        if (zzj != null) {
            return zzg + zzl.zzb(2, (zzt) zzj);
        }
        return zzg;
    }

    /* access modifiers changed from: private */
    /* renamed from: zzb */
    public final zzi zza(zzk zzk) throws IOException {
        int zzk2;
        while (true) {
            int zzj = zzk.zzj();
            if (zzj == 0) {
                return this;
            }
            if (zzj == 8) {
                try {
                    zzk2 = zzk.zzk();
                    if (zzk2 <= 0 || zzk2 > 15) {
                        StringBuilder sb = new StringBuilder(36);
                        sb.append(zzk2);
                        sb.append(" is not a valid enum Type");
                    } else {
                        this.type = zzk2;
                    }
                } catch (IllegalArgumentException e) {
                    zzk.zzg(zzk.getPosition());
                    zza(zzk, zzj);
                }
            } else if (zzj == 18) {
                if (this.zzgc == null) {
                    this.zzgc = new zzj();
                }
                zzk.zza(this.zzgc);
            } else if (!super.zza(zzk, zzj)) {
                return this;
            }
        }
        StringBuilder sb2 = new StringBuilder(36);
        sb2.append(zzk2);
        sb2.append(" is not a valid enum Type");
        throw new IllegalArgumentException(sb2.toString());
    }
}
