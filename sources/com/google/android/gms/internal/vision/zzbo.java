package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Iterator;

public abstract class zzbo implements Serializable, Iterable<Byte> {
    public static final zzbo zzgt = new zzbv(zzct.zzlo);
    private static final zzbs zzgu = (zzbj.zzaq() ? new zzbw((zzbp) null) : new zzbq((zzbp) null));
    private int zzgv = 0;

    zzbo() {
    }

    static int zzb(int i, int i2, int i3) {
        int i4 = i2 - i;
        if ((i | i2 | i4 | (i3 - i2)) >= 0) {
            return i4;
        }
        if (i < 0) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("Beginning index: ");
            sb.append(i);
            sb.append(" < 0");
            throw new IndexOutOfBoundsException(sb.toString());
        } else if (i2 < i) {
            StringBuilder sb2 = new StringBuilder(66);
            sb2.append("Beginning index larger than ending index: ");
            sb2.append(i);
            sb2.append(", ");
            sb2.append(i2);
            throw new IndexOutOfBoundsException(sb2.toString());
        } else {
            StringBuilder sb3 = new StringBuilder(37);
            sb3.append("End index: ");
            sb3.append(i2);
            sb3.append(" >= ");
            sb3.append(i3);
            throw new IndexOutOfBoundsException(sb3.toString());
        }
    }

    public static zzbo zzb(byte[] bArr, int i, int i2) {
        return new zzbv(zzgu.zzc(bArr, i, i2));
    }

    public static zzbo zzg(String str) {
        return new zzbv(str.getBytes(zzct.UTF_8));
    }

    static zzbt zzm(int i) {
        return new zzbt(i, (zzbp) null);
    }

    public abstract boolean equals(Object obj);

    public final int hashCode() {
        int i = this.zzgv;
        if (i == 0) {
            int size = size();
            i = zza(size, 0, size);
            if (i == 0) {
                i = 1;
            }
            this.zzgv = i;
        }
        return i;
    }

    public /* synthetic */ Iterator iterator() {
        return new zzbp(this);
    }

    public abstract int size();

    public final String toString() {
        return String.format("<ByteString@%s size=%d>", new Object[]{Integer.toHexString(System.identityHashCode(this)), Integer.valueOf(size())});
    }

    /* access modifiers changed from: protected */
    public abstract int zza(int i, int i2, int i3);

    /* access modifiers changed from: protected */
    public abstract String zza(Charset charset);

    /* access modifiers changed from: package-private */
    public abstract void zza(zzbn zzbn) throws IOException;

    public final String zzas() {
        return size() == 0 ? "" : zza(zzct.UTF_8);
    }

    public abstract boolean zzat();

    /* access modifiers changed from: protected */
    public final int zzau() {
        return this.zzgv;
    }

    public abstract zzbo zzc(int i, int i2);

    public abstract byte zzl(int i);
}
