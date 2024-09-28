package com.google.android.gms.internal.vision;

final class zzbt {
    private final byte[] buffer;
    private final zzca zzgz;

    private zzbt(int i) {
        byte[] bArr = new byte[i];
        this.buffer = bArr;
        this.zzgz = zzca.zzd(bArr);
    }

    /* synthetic */ zzbt(int i, zzbp zzbp) {
        this(i);
    }

    public final zzbo zzaw() {
        this.zzgz.zzba();
        return new zzbv(this.buffer);
    }

    public final zzca zzax() {
        return this.zzgz;
    }
}
