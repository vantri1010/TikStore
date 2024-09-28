package com.google.android.gms.internal.vision;

final class zzbz extends zzbx {
    private final byte[] buffer;
    private int limit;
    private int pos;
    private final boolean zzhf;
    private int zzhg;
    private int zzhh;
    private int zzhi;

    private zzbz(byte[] bArr, int i, int i2, boolean z) {
        super();
        this.zzhi = Integer.MAX_VALUE;
        this.buffer = bArr;
        this.limit = i2 + i;
        this.pos = i;
        this.zzhh = i;
        this.zzhf = z;
    }

    public final int zzay() {
        return this.pos - this.zzhh;
    }

    public final int zzn(int i) throws zzcx {
        if (i >= 0) {
            int zzay = i + zzay();
            int i2 = this.zzhi;
            if (zzay <= i2) {
                this.zzhi = zzay;
                int i3 = this.limit + this.zzhg;
                this.limit = i3;
                int i4 = i3 - this.zzhh;
                if (i4 > zzay) {
                    int i5 = i4 - zzay;
                    this.zzhg = i5;
                    this.limit = i3 - i5;
                } else {
                    this.zzhg = 0;
                }
                return i2;
            }
            throw zzcx.zzcb();
        }
        throw zzcx.zzcc();
    }
}
