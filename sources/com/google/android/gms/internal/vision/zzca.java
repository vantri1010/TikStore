package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class zzca extends zzbn {
    private static final Logger logger = Logger.getLogger(zzca.class.getName());
    /* access modifiers changed from: private */
    public static final boolean zzhj = zzfl.zzdx();
    zzcc zzhk;

    static class zza extends zzca {
        private final byte[] buffer;
        private final int limit;
        private final int offset;
        private int position;

        zza(byte[] bArr, int i, int i2) {
            super();
            if (bArr != null) {
                int i3 = i2 + 0;
                if ((i2 | 0 | (bArr.length - i3)) >= 0) {
                    this.buffer = bArr;
                    this.offset = 0;
                    this.position = 0;
                    this.limit = i3;
                    return;
                }
                throw new IllegalArgumentException(String.format("Array range is invalid. Buffer.length=%d, offset=%d, length=%d", new Object[]{Integer.valueOf(bArr.length), 0, Integer.valueOf(i2)}));
            }
            throw new NullPointerException("buffer");
        }

        private final void write(byte[] bArr, int i, int i2) throws IOException {
            try {
                System.arraycopy(bArr, i, this.buffer, this.position, i2);
                this.position += i2;
            } catch (IndexOutOfBoundsException e) {
                throw new zzb(String.format("Pos: %d, limit: %d, len: %d", new Object[]{Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(i2)}), e);
            }
        }

        public final void zza(byte b) throws IOException {
            try {
                byte[] bArr = this.buffer;
                int i = this.position;
                this.position = i + 1;
                bArr[i] = b;
            } catch (IndexOutOfBoundsException e) {
                throw new zzb(String.format("Pos: %d, limit: %d, len: %d", new Object[]{Integer.valueOf(this.position), Integer.valueOf(this.limit), 1}), e);
            }
        }

        public final void zza(int i, long j) throws IOException {
            zzd(i, 0);
            zzb(j);
        }

        public final void zza(int i, zzbo zzbo) throws IOException {
            zzd(i, 2);
            zza(zzbo);
        }

        public final void zza(int i, zzdx zzdx) throws IOException {
            zzd(1, 3);
            zzf(2, i);
            zzd(3, 2);
            zzb(zzdx);
            zzd(1, 4);
        }

        /* access modifiers changed from: package-private */
        public final void zza(int i, zzdx zzdx, zzen zzen) throws IOException {
            zzd(i, 2);
            zzbf zzbf = (zzbf) zzdx;
            int zzal = zzbf.zzal();
            if (zzal == -1) {
                zzal = zzen.zzn(zzbf);
                zzbf.zzh(zzal);
            }
            zzq(zzal);
            zzen.zza(zzdx, this.zzhk);
        }

        public final void zza(int i, String str) throws IOException {
            zzd(i, 2);
            zzh(str);
        }

        public final void zza(zzbo zzbo) throws IOException {
            zzq(zzbo.size());
            zzbo.zza((zzbn) this);
        }

        public final void zza(byte[] bArr, int i, int i2) throws IOException {
            write(bArr, i, i2);
        }

        public final int zzaz() {
            return this.limit - this.position;
        }

        public final void zzb(int i, zzbo zzbo) throws IOException {
            zzd(1, 3);
            zzf(2, i);
            zza(3, zzbo);
            zzd(1, 4);
        }

        public final void zzb(int i, boolean z) throws IOException {
            zzd(i, 0);
            zza(z ? (byte) 1 : 0);
        }

        public final void zzb(long j) throws IOException {
            if (!zzca.zzhj || zzaz() < 10) {
                while ((j & -128) != 0) {
                    byte[] bArr = this.buffer;
                    int i = this.position;
                    this.position = i + 1;
                    bArr[i] = (byte) ((((int) j) & 127) | 128);
                    j >>>= 7;
                }
                try {
                    byte[] bArr2 = this.buffer;
                    int i2 = this.position;
                    this.position = i2 + 1;
                    bArr2[i2] = (byte) ((int) j);
                } catch (IndexOutOfBoundsException e) {
                    throw new zzb(String.format("Pos: %d, limit: %d, len: %d", new Object[]{Integer.valueOf(this.position), Integer.valueOf(this.limit), 1}), e);
                }
            } else {
                while ((j & -128) != 0) {
                    byte[] bArr3 = this.buffer;
                    int i3 = this.position;
                    this.position = i3 + 1;
                    zzfl.zza(bArr3, (long) i3, (byte) ((((int) j) & 127) | 128));
                    j >>>= 7;
                }
                byte[] bArr4 = this.buffer;
                int i4 = this.position;
                this.position = i4 + 1;
                zzfl.zza(bArr4, (long) i4, (byte) ((int) j));
            }
        }

        public final void zzb(zzdx zzdx) throws IOException {
            zzq(zzdx.zzbl());
            zzdx.zzb(this);
        }

        public final void zzc(int i, long j) throws IOException {
            zzd(i, 1);
            zzd(j);
        }

        public final void zzd(int i, int i2) throws IOException {
            zzq((i << 3) | i2);
        }

        public final void zzd(long j) throws IOException {
            try {
                byte[] bArr = this.buffer;
                int i = this.position;
                int i2 = i + 1;
                this.position = i2;
                bArr[i] = (byte) ((int) j);
                byte[] bArr2 = this.buffer;
                int i3 = i2 + 1;
                this.position = i3;
                bArr2[i2] = (byte) ((int) (j >> 8));
                byte[] bArr3 = this.buffer;
                int i4 = i3 + 1;
                this.position = i4;
                bArr3[i3] = (byte) ((int) (j >> 16));
                byte[] bArr4 = this.buffer;
                int i5 = i4 + 1;
                this.position = i5;
                bArr4[i4] = (byte) ((int) (j >> 24));
                byte[] bArr5 = this.buffer;
                int i6 = i5 + 1;
                this.position = i6;
                bArr5[i5] = (byte) ((int) (j >> 32));
                byte[] bArr6 = this.buffer;
                int i7 = i6 + 1;
                this.position = i7;
                bArr6[i6] = (byte) ((int) (j >> 40));
                byte[] bArr7 = this.buffer;
                int i8 = i7 + 1;
                this.position = i8;
                bArr7[i7] = (byte) ((int) (j >> 48));
                byte[] bArr8 = this.buffer;
                this.position = i8 + 1;
                bArr8[i8] = (byte) ((int) (j >> 56));
            } catch (IndexOutOfBoundsException e) {
                throw new zzb(String.format("Pos: %d, limit: %d, len: %d", new Object[]{Integer.valueOf(this.position), Integer.valueOf(this.limit), 1}), e);
            }
        }

        public final void zzd(byte[] bArr, int i, int i2) throws IOException {
            zzq(i2);
            write(bArr, 0, i2);
        }

        public final void zze(int i, int i2) throws IOException {
            zzd(i, 0);
            zzp(i2);
        }

        public final void zzf(int i, int i2) throws IOException {
            zzd(i, 0);
            zzq(i2);
        }

        public final void zzh(int i, int i2) throws IOException {
            zzd(i, 5);
            zzs(i2);
        }

        public final void zzh(String str) throws IOException {
            int i = this.position;
            try {
                int zzv = zzv(str.length() * 3);
                int zzv2 = zzv(str.length());
                if (zzv2 == zzv) {
                    int i2 = i + zzv2;
                    this.position = i2;
                    int zza = zzfn.zza(str, this.buffer, i2, zzaz());
                    this.position = i;
                    zzq((zza - i) - zzv2);
                    this.position = zza;
                    return;
                }
                zzq(zzfn.zza(str));
                this.position = zzfn.zza(str, this.buffer, this.position, zzaz());
            } catch (zzfq e) {
                this.position = i;
                zza(str, e);
            } catch (IndexOutOfBoundsException e2) {
                throw new zzb(e2);
            }
        }

        public final void zzp(int i) throws IOException {
            if (i >= 0) {
                zzq(i);
            } else {
                zzb((long) i);
            }
        }

        public final void zzq(int i) throws IOException {
            if (!zzca.zzhj || zzaz() < 10) {
                while ((i & -128) != 0) {
                    byte[] bArr = this.buffer;
                    int i2 = this.position;
                    this.position = i2 + 1;
                    bArr[i2] = (byte) ((i & 127) | 128);
                    i >>>= 7;
                }
                try {
                    byte[] bArr2 = this.buffer;
                    int i3 = this.position;
                    this.position = i3 + 1;
                    bArr2[i3] = (byte) i;
                } catch (IndexOutOfBoundsException e) {
                    throw new zzb(String.format("Pos: %d, limit: %d, len: %d", new Object[]{Integer.valueOf(this.position), Integer.valueOf(this.limit), 1}), e);
                }
            } else {
                while ((i & -128) != 0) {
                    byte[] bArr3 = this.buffer;
                    int i4 = this.position;
                    this.position = i4 + 1;
                    zzfl.zza(bArr3, (long) i4, (byte) ((i & 127) | 128));
                    i >>>= 7;
                }
                byte[] bArr4 = this.buffer;
                int i5 = this.position;
                this.position = i5 + 1;
                zzfl.zza(bArr4, (long) i5, (byte) i);
            }
        }

        public final void zzs(int i) throws IOException {
            try {
                byte[] bArr = this.buffer;
                int i2 = this.position;
                int i3 = i2 + 1;
                this.position = i3;
                bArr[i2] = (byte) i;
                byte[] bArr2 = this.buffer;
                int i4 = i3 + 1;
                this.position = i4;
                bArr2[i3] = (byte) (i >> 8);
                byte[] bArr3 = this.buffer;
                int i5 = i4 + 1;
                this.position = i5;
                bArr3[i4] = (byte) (i >> 16);
                byte[] bArr4 = this.buffer;
                this.position = i5 + 1;
                bArr4[i5] = i >> 24;
            } catch (IndexOutOfBoundsException e) {
                throw new zzb(String.format("Pos: %d, limit: %d, len: %d", new Object[]{Integer.valueOf(this.position), Integer.valueOf(this.limit), 1}), e);
            }
        }
    }

    public static class zzb extends IOException {
        zzb() {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.");
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        zzb(java.lang.String r3, java.lang.Throwable r4) {
            /*
                r2 = this;
                java.lang.String r0 = "CodedOutputStream was writing to a flat byte array and ran out of space.: "
                java.lang.String r0 = java.lang.String.valueOf(r0)
                java.lang.String r3 = java.lang.String.valueOf(r3)
                int r1 = r3.length()
                if (r1 == 0) goto L_0x0015
                java.lang.String r3 = r0.concat(r3)
                goto L_0x001a
            L_0x0015:
                java.lang.String r3 = new java.lang.String
                r3.<init>(r0)
            L_0x001a:
                r2.<init>(r3, r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzca.zzb.<init>(java.lang.String, java.lang.Throwable):void");
        }

        zzb(Throwable th) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.", th);
        }
    }

    private zzca() {
    }

    public static int zza(int i, zzde zzde) {
        int zzt = zzt(i);
        int zzbl = zzde.zzbl();
        return zzt + zzv(zzbl) + zzbl;
    }

    public static int zza(zzde zzde) {
        int zzbl = zzde.zzbl();
        return zzv(zzbl) + zzbl;
    }

    static int zza(zzdx zzdx, zzen zzen) {
        zzbf zzbf = (zzbf) zzdx;
        int zzal = zzbf.zzal();
        if (zzal == -1) {
            zzal = zzen.zzn(zzbf);
            zzbf.zzh(zzal);
        }
        return zzv(zzal) + zzal;
    }

    private static int zzaa(int i) {
        return (i >> 31) ^ (i << 1);
    }

    @Deprecated
    public static int zzab(int i) {
        return zzv(i);
    }

    public static int zzb(double d) {
        return 8;
    }

    public static int zzb(int i, double d) {
        return zzt(i) + 8;
    }

    public static int zzb(int i, float f) {
        return zzt(i) + 4;
    }

    public static int zzb(int i, zzde zzde) {
        return (zzt(1) << 1) + zzj(2, i) + zza(3, zzde);
    }

    public static int zzb(int i, zzdx zzdx) {
        return (zzt(1) << 1) + zzj(2, i) + zzt(3) + zzc(zzdx);
    }

    static int zzb(int i, zzdx zzdx, zzen zzen) {
        return zzt(i) + zza(zzdx, zzen);
    }

    public static int zzb(int i, String str) {
        return zzt(i) + zzi(str);
    }

    public static int zzb(zzbo zzbo) {
        int size = zzbo.size();
        return zzv(size) + size;
    }

    public static int zzb(boolean z) {
        return 1;
    }

    public static int zzc(int i, zzbo zzbo) {
        int zzt = zzt(i);
        int size = zzbo.size();
        return zzt + zzv(size) + size;
    }

    @Deprecated
    static int zzc(int i, zzdx zzdx, zzen zzen) {
        int zzt = zzt(i) << 1;
        zzbf zzbf = (zzbf) zzdx;
        int zzal = zzbf.zzal();
        if (zzal == -1) {
            zzal = zzen.zzn(zzbf);
            zzbf.zzh(zzal);
        }
        return zzt + zzal;
    }

    public static int zzc(int i, boolean z) {
        return zzt(i) + 1;
    }

    public static int zzc(zzdx zzdx) {
        int zzbl = zzdx.zzbl();
        return zzv(zzbl) + zzbl;
    }

    public static int zzd(float f) {
        return 4;
    }

    public static int zzd(int i, long j) {
        return zzt(i) + zzf(j);
    }

    public static int zzd(int i, zzbo zzbo) {
        return (zzt(1) << 1) + zzj(2, i) + zzc(3, zzbo);
    }

    @Deprecated
    public static int zzd(zzdx zzdx) {
        return zzdx.zzbl();
    }

    public static zzca zzd(byte[] bArr) {
        return new zza(bArr, 0, bArr.length);
    }

    public static int zze(int i, long j) {
        return zzt(i) + zzf(j);
    }

    public static int zze(long j) {
        return zzf(j);
    }

    public static int zze(byte[] bArr) {
        int length = bArr.length;
        return zzv(length) + length;
    }

    public static int zzf(int i, long j) {
        return zzt(i) + zzf(zzj(j));
    }

    public static int zzf(long j) {
        int i;
        if ((-128 & j) == 0) {
            return 1;
        }
        if (j < 0) {
            return 10;
        }
        if ((-34359738368L & j) != 0) {
            i = 6;
            j >>>= 28;
        } else {
            i = 2;
        }
        if ((-2097152 & j) != 0) {
            i += 2;
            j >>>= 14;
        }
        return (j & -16384) != 0 ? i + 1 : i;
    }

    public static int zzg(int i, long j) {
        return zzt(i) + 8;
    }

    public static int zzg(long j) {
        return zzf(zzj(j));
    }

    public static int zzh(int i, long j) {
        return zzt(i) + 8;
    }

    public static int zzh(long j) {
        return 8;
    }

    public static int zzi(int i, int i2) {
        return zzt(i) + zzu(i2);
    }

    public static int zzi(long j) {
        return 8;
    }

    public static int zzi(String str) {
        int i;
        try {
            i = zzfn.zza(str);
        } catch (zzfq e) {
            i = str.getBytes(zzct.UTF_8).length;
        }
        return zzv(i) + i;
    }

    public static int zzj(int i, int i2) {
        return zzt(i) + zzv(i2);
    }

    private static long zzj(long j) {
        return (j >> 63) ^ (j << 1);
    }

    public static int zzk(int i, int i2) {
        return zzt(i) + zzv(zzaa(i2));
    }

    public static int zzl(int i, int i2) {
        return zzt(i) + 4;
    }

    public static int zzm(int i, int i2) {
        return zzt(i) + 4;
    }

    public static int zzn(int i, int i2) {
        return zzt(i) + zzu(i2);
    }

    public static int zzt(int i) {
        return zzv(i << 3);
    }

    public static int zzu(int i) {
        if (i >= 0) {
            return zzv(i);
        }
        return 10;
    }

    public static int zzv(int i) {
        if ((i & -128) == 0) {
            return 1;
        }
        if ((i & -16384) == 0) {
            return 2;
        }
        if ((-2097152 & i) == 0) {
            return 3;
        }
        return (i & -268435456) == 0 ? 4 : 5;
    }

    public static int zzw(int i) {
        return zzv(zzaa(i));
    }

    public static int zzx(int i) {
        return 4;
    }

    public static int zzy(int i) {
        return 4;
    }

    public static int zzz(int i) {
        return zzu(i);
    }

    public abstract void zza(byte b) throws IOException;

    public final void zza(double d) throws IOException {
        zzd(Double.doubleToRawLongBits(d));
    }

    public final void zza(int i, double d) throws IOException {
        zzc(i, Double.doubleToRawLongBits(d));
    }

    public final void zza(int i, float f) throws IOException {
        zzh(i, Float.floatToRawIntBits(f));
    }

    public abstract void zza(int i, long j) throws IOException;

    public abstract void zza(int i, zzbo zzbo) throws IOException;

    public abstract void zza(int i, zzdx zzdx) throws IOException;

    /* access modifiers changed from: package-private */
    public abstract void zza(int i, zzdx zzdx, zzen zzen) throws IOException;

    public abstract void zza(int i, String str) throws IOException;

    public abstract void zza(zzbo zzbo) throws IOException;

    /* access modifiers changed from: package-private */
    public final void zza(String str, zzfq zzfq) throws IOException {
        logger.logp(Level.WARNING, "com.google.protobuf.CodedOutputStream", "inefficientWriteStringNoTag", "Converting ill-formed UTF-16. Your Protocol Buffer will not round trip correctly!", zzfq);
        byte[] bytes = str.getBytes(zzct.UTF_8);
        try {
            zzq(bytes.length);
            zza(bytes, 0, bytes.length);
        } catch (IndexOutOfBoundsException e) {
            throw new zzb(e);
        } catch (zzb e2) {
            throw e2;
        }
    }

    public final void zza(boolean z) throws IOException {
        zza(z ? (byte) 1 : 0);
    }

    public abstract int zzaz();

    public final void zzb(int i, long j) throws IOException {
        zza(i, zzj(j));
    }

    public abstract void zzb(int i, zzbo zzbo) throws IOException;

    public abstract void zzb(int i, boolean z) throws IOException;

    public abstract void zzb(long j) throws IOException;

    public abstract void zzb(zzdx zzdx) throws IOException;

    public final void zzba() {
        if (zzaz() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public final void zzc(float f) throws IOException {
        zzs(Float.floatToRawIntBits(f));
    }

    public abstract void zzc(int i, long j) throws IOException;

    public final void zzc(long j) throws IOException {
        zzb(zzj(j));
    }

    public abstract void zzd(int i, int i2) throws IOException;

    public abstract void zzd(long j) throws IOException;

    /* access modifiers changed from: package-private */
    public abstract void zzd(byte[] bArr, int i, int i2) throws IOException;

    public abstract void zze(int i, int i2) throws IOException;

    public abstract void zzf(int i, int i2) throws IOException;

    public final void zzg(int i, int i2) throws IOException {
        zzf(i, zzaa(i2));
    }

    public abstract void zzh(int i, int i2) throws IOException;

    public abstract void zzh(String str) throws IOException;

    public abstract void zzp(int i) throws IOException;

    public abstract void zzq(int i) throws IOException;

    public final void zzr(int i) throws IOException {
        zzq(zzaa(i));
    }

    public abstract void zzs(int i) throws IOException;
}
