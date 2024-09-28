package com.google.android.gms.internal.wearable;

import java.io.IOException;

public final class zzg extends zzn<zzg> {
    public zzh[] zzfy = zzh.zzh();

    public zzg() {
        this.zzhc = null;
        this.zzhl = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzg)) {
            return false;
        }
        zzg zzg = (zzg) obj;
        if (!zzr.equals((Object[]) this.zzfy, (Object[]) zzg.zzfy)) {
            return false;
        }
        if (this.zzhc != null && !this.zzhc.isEmpty()) {
            return this.zzhc.equals(zzg.zzhc);
        }
        if (zzg.zzhc == null || zzg.zzhc.isEmpty()) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int i;
        int hashCode = (((getClass().getName().hashCode() + 527) * 31) + zzr.hashCode((Object[]) this.zzfy)) * 31;
        if (this.zzhc == null || this.zzhc.isEmpty()) {
            i = 0;
        } else {
            i = this.zzhc.hashCode();
        }
        return hashCode + i;
    }

    public final void zza(zzl zzl) throws IOException {
        zzh[] zzhArr = this.zzfy;
        if (zzhArr != null && zzhArr.length > 0) {
            int i = 0;
            while (true) {
                zzh[] zzhArr2 = this.zzfy;
                if (i >= zzhArr2.length) {
                    break;
                }
                zzh zzh = zzhArr2[i];
                if (zzh != null) {
                    zzl.zza(1, (zzt) zzh);
                }
                i++;
            }
        }
        super.zza(zzl);
    }

    /* access modifiers changed from: protected */
    public final int zzg() {
        int zzg = super.zzg();
        zzh[] zzhArr = this.zzfy;
        if (zzhArr != null && zzhArr.length > 0) {
            int i = 0;
            while (true) {
                zzh[] zzhArr2 = this.zzfy;
                if (i >= zzhArr2.length) {
                    break;
                }
                zzh zzh = zzhArr2[i];
                if (zzh != null) {
                    zzg += zzl.zzb(1, (zzt) zzh);
                }
                i++;
            }
        }
        return zzg;
    }

    public static zzg zza(byte[] bArr) throws zzs {
        return (zzg) zzt.zza(new zzg(), bArr, 0, bArr.length);
    }

    public final /* synthetic */ zzt zza(zzk zzk) throws IOException {
        while (true) {
            int zzj = zzk.zzj();
            if (zzj == 0) {
                return this;
            }
            if (zzj == 10) {
                int zzb = zzw.zzb(zzk, 10);
                zzh[] zzhArr = this.zzfy;
                int length = zzhArr == null ? 0 : zzhArr.length;
                int i = zzb + length;
                zzh[] zzhArr2 = new zzh[i];
                if (length != 0) {
                    System.arraycopy(this.zzfy, 0, zzhArr2, 0, length);
                }
                while (length < i - 1) {
                    zzhArr2[length] = new zzh();
                    zzk.zza(zzhArr2[length]);
                    zzk.zzj();
                    length++;
                }
                zzhArr2[length] = new zzh();
                zzk.zza(zzhArr2[length]);
                this.zzfy = zzhArr2;
            } else if (!super.zza(zzk, zzj)) {
                return this;
            }
        }
    }
}
