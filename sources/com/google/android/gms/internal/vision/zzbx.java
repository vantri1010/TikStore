package com.google.android.gms.internal.vision;

public abstract class zzbx {
    private static volatile boolean zzhe = false;
    private int zzhb;
    private int zzhc;
    private boolean zzhd;

    private zzbx() {
        this.zzhb = 100;
        this.zzhc = Integer.MAX_VALUE;
        this.zzhd = false;
    }

    public static long zza(long j) {
        return (-(j & 1)) ^ (j >>> 1);
    }

    static zzbx zza(byte[] bArr, int i, int i2, boolean z) {
        zzbz zzbz = new zzbz(bArr, 0, i2, false);
        try {
            zzbz.zzn(i2);
            return zzbz;
        } catch (zzcx e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static int zzo(int i) {
        return (-(i & 1)) ^ (i >>> 1);
    }

    public abstract int zzay();

    public abstract int zzn(int i) throws zzcx;
}
