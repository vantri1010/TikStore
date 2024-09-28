package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.nio.charset.Charset;

class zzbv extends zzbu {
    protected final byte[] zzha;

    zzbv(byte[] bArr) {
        this.zzha = bArr;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzbo) || size() != ((zzbo) obj).size()) {
            return false;
        }
        if (size() == 0) {
            return true;
        }
        if (!(obj instanceof zzbv)) {
            return obj.equals(this);
        }
        zzbv zzbv = (zzbv) obj;
        int zzau = zzau();
        int zzau2 = zzbv.zzau();
        if (zzau == 0 || zzau2 == 0 || zzau == zzau2) {
            return zza(zzbv, 0, size());
        }
        return false;
    }

    public int size() {
        return this.zzha.length;
    }

    /* access modifiers changed from: protected */
    public final int zza(int i, int i2, int i3) {
        return zzct.zza(i, this.zzha, zzav(), i3);
    }

    /* access modifiers changed from: protected */
    public final String zza(Charset charset) {
        return new String(this.zzha, zzav(), size(), charset);
    }

    /* access modifiers changed from: package-private */
    public final void zza(zzbn zzbn) throws IOException {
        zzbn.zza(this.zzha, zzav(), size());
    }

    /* access modifiers changed from: package-private */
    public final boolean zza(zzbo zzbo, int i, int i2) {
        if (i2 > zzbo.size()) {
            int size = size();
            StringBuilder sb = new StringBuilder(40);
            sb.append("Length too large: ");
            sb.append(i2);
            sb.append(size);
            throw new IllegalArgumentException(sb.toString());
        } else if (i2 > zzbo.size()) {
            int size2 = zzbo.size();
            StringBuilder sb2 = new StringBuilder(59);
            sb2.append("Ran off end of other: 0, ");
            sb2.append(i2);
            sb2.append(", ");
            sb2.append(size2);
            throw new IllegalArgumentException(sb2.toString());
        } else if (!(zzbo instanceof zzbv)) {
            return zzbo.zzc(0, i2).equals(zzc(0, i2));
        } else {
            zzbv zzbv = (zzbv) zzbo;
            byte[] bArr = this.zzha;
            byte[] bArr2 = zzbv.zzha;
            int zzav = zzav() + i2;
            int zzav2 = zzav();
            int zzav3 = zzbv.zzav();
            while (zzav2 < zzav) {
                if (bArr[zzav2] != bArr2[zzav3]) {
                    return false;
                }
                zzav2++;
                zzav3++;
            }
            return true;
        }
    }

    public final boolean zzat() {
        int zzav = zzav();
        return zzfn.zze(this.zzha, zzav, size() + zzav);
    }

    /* access modifiers changed from: protected */
    public int zzav() {
        return 0;
    }

    public final zzbo zzc(int i, int i2) {
        int zzb = zzb(0, i2, size());
        return zzb == 0 ? zzbo.zzgt : new zzbr(this.zzha, zzav(), zzb);
    }

    public byte zzl(int i) {
        return this.zzha[i];
    }
}
