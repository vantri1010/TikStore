package com.google.android.gms.internal.vision;

final class zzbr extends zzbv {
    private final int zzgx;
    private final int zzgy;

    zzbr(byte[] bArr, int i, int i2) {
        super(bArr);
        zzb(i, i + i2, bArr.length);
        this.zzgx = i;
        this.zzgy = i2;
    }

    public final int size() {
        return this.zzgy;
    }

    /* access modifiers changed from: protected */
    public final int zzav() {
        return this.zzgx;
    }

    public final byte zzl(int i) {
        int size = size();
        if (((size - (i + 1)) | i) >= 0) {
            return this.zzha[this.zzgx + i];
        }
        if (i < 0) {
            StringBuilder sb = new StringBuilder(22);
            sb.append("Index < 0: ");
            sb.append(i);
            throw new ArrayIndexOutOfBoundsException(sb.toString());
        }
        StringBuilder sb2 = new StringBuilder(40);
        sb2.append("Index > length: ");
        sb2.append(i);
        sb2.append(", ");
        sb2.append(size);
        throw new ArrayIndexOutOfBoundsException(sb2.toString());
    }
}
