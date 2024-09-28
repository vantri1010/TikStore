package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzcr;
import java.io.IOException;
import java.util.Arrays;

public final class zzfg {
    private static final zzfg zzot = new zzfg(0, new int[0], new Object[0], false);
    private int count;
    private boolean zzgl;
    private int zzks;
    private Object[] zznf;
    private int[] zzou;

    private zzfg() {
        this(0, new int[8], new Object[8], true);
    }

    private zzfg(int i, int[] iArr, Object[] objArr, boolean z) {
        this.zzks = -1;
        this.count = i;
        this.zzou = iArr;
        this.zznf = objArr;
        this.zzgl = z;
    }

    static zzfg zza(zzfg zzfg, zzfg zzfg2) {
        int i = zzfg.count + zzfg2.count;
        int[] copyOf = Arrays.copyOf(zzfg.zzou, i);
        System.arraycopy(zzfg2.zzou, 0, copyOf, zzfg.count, zzfg2.count);
        Object[] copyOf2 = Arrays.copyOf(zzfg.zznf, i);
        System.arraycopy(zzfg2.zznf, 0, copyOf2, zzfg.count, zzfg2.count);
        return new zzfg(i, copyOf, copyOf2, true);
    }

    private static void zzb(int i, Object obj, zzfz zzfz) throws IOException {
        int i2 = i >>> 3;
        int i3 = i & 7;
        if (i3 == 0) {
            zzfz.zzi(i2, ((Long) obj).longValue());
        } else if (i3 == 1) {
            zzfz.zzc(i2, ((Long) obj).longValue());
        } else if (i3 == 2) {
            zzfz.zza(i2, (zzbo) obj);
        } else if (i3 != 3) {
            if (i3 == 5) {
                zzfz.zzh(i2, ((Integer) obj).intValue());
                return;
            }
            throw new RuntimeException(zzcx.zzce());
        } else if (zzfz.zzbc() == zzcr.zzd.zzlj) {
            zzfz.zzac(i2);
            ((zzfg) obj).zzb(zzfz);
            zzfz.zzad(i2);
        } else {
            zzfz.zzad(i2);
            ((zzfg) obj).zzb(zzfz);
            zzfz.zzac(i2);
        }
    }

    public static zzfg zzdu() {
        return zzot;
    }

    static zzfg zzdv() {
        return new zzfg();
    }

    public final boolean equals(Object obj) {
        boolean z;
        boolean z2;
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof zzfg)) {
            return false;
        }
        zzfg zzfg = (zzfg) obj;
        int i = this.count;
        if (i == zzfg.count) {
            int[] iArr = this.zzou;
            int[] iArr2 = zzfg.zzou;
            int i2 = 0;
            while (true) {
                if (i2 >= i) {
                    z = true;
                    break;
                } else if (iArr[i2] != iArr2[i2]) {
                    z = false;
                    break;
                } else {
                    i2++;
                }
            }
            if (z) {
                Object[] objArr = this.zznf;
                Object[] objArr2 = zzfg.zznf;
                int i3 = this.count;
                int i4 = 0;
                while (true) {
                    if (i4 >= i3) {
                        z2 = true;
                        break;
                    } else if (!objArr[i4].equals(objArr2[i4])) {
                        z2 = false;
                        break;
                    } else {
                        i4++;
                    }
                }
                return z2;
            }
        }
    }

    public final int hashCode() {
        int i = this.count;
        int i2 = (i + 527) * 31;
        int[] iArr = this.zzou;
        int i3 = 17;
        int i4 = 17;
        for (int i5 = 0; i5 < i; i5++) {
            i4 = (i4 * 31) + iArr[i5];
        }
        int i6 = (i2 + i4) * 31;
        Object[] objArr = this.zznf;
        int i7 = this.count;
        for (int i8 = 0; i8 < i7; i8++) {
            i3 = (i3 * 31) + objArr[i8].hashCode();
        }
        return i6 + i3;
    }

    /* access modifiers changed from: package-private */
    public final void zza(zzfz zzfz) throws IOException {
        if (zzfz.zzbc() == zzcr.zzd.zzlk) {
            for (int i = this.count - 1; i >= 0; i--) {
                zzfz.zza(this.zzou[i] >>> 3, this.zznf[i]);
            }
            return;
        }
        for (int i2 = 0; i2 < this.count; i2++) {
            zzfz.zza(this.zzou[i2] >>> 3, this.zznf[i2]);
        }
    }

    /* access modifiers changed from: package-private */
    public final void zza(StringBuilder sb, int i) {
        for (int i2 = 0; i2 < this.count; i2++) {
            zzea.zza(sb, i, String.valueOf(this.zzou[i2] >>> 3), this.zznf[i2]);
        }
    }

    public final void zzao() {
        this.zzgl = false;
    }

    /* access modifiers changed from: package-private */
    public final void zzb(int i, Object obj) {
        if (this.zzgl) {
            int i2 = this.count;
            if (i2 == this.zzou.length) {
                int i3 = this.count + (i2 < 4 ? 8 : i2 >> 1);
                this.zzou = Arrays.copyOf(this.zzou, i3);
                this.zznf = Arrays.copyOf(this.zznf, i3);
            }
            int[] iArr = this.zzou;
            int i4 = this.count;
            iArr[i4] = i;
            this.zznf[i4] = obj;
            this.count = i4 + 1;
            return;
        }
        throw new UnsupportedOperationException();
    }

    public final void zzb(zzfz zzfz) throws IOException {
        if (this.count != 0) {
            if (zzfz.zzbc() == zzcr.zzd.zzlj) {
                for (int i = 0; i < this.count; i++) {
                    zzb(this.zzou[i], this.zznf[i], zzfz);
                }
                return;
            }
            for (int i2 = this.count - 1; i2 >= 0; i2--) {
                zzb(this.zzou[i2], this.zznf[i2], zzfz);
            }
        }
    }

    public final int zzbl() {
        int i;
        int i2 = this.zzks;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < this.count; i4++) {
            int i5 = this.zzou[i4];
            int i6 = i5 >>> 3;
            int i7 = i5 & 7;
            if (i7 == 0) {
                i = zzca.zze(i6, ((Long) this.zznf[i4]).longValue());
            } else if (i7 == 1) {
                i = zzca.zzg(i6, ((Long) this.zznf[i4]).longValue());
            } else if (i7 == 2) {
                i = zzca.zzc(i6, (zzbo) this.zznf[i4]);
            } else if (i7 == 3) {
                i = (zzca.zzt(i6) << 1) + ((zzfg) this.zznf[i4]).zzbl();
            } else if (i7 == 5) {
                i = zzca.zzl(i6, ((Integer) this.zznf[i4]).intValue());
            } else {
                throw new IllegalStateException(zzcx.zzce());
            }
            i3 += i;
        }
        this.zzks = i3;
        return i3;
    }

    public final int zzdw() {
        int i = this.zzks;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < this.count; i3++) {
            i2 += zzca.zzd(this.zzou[i3] >>> 3, (zzbo) this.zznf[i3]);
        }
        this.zzks = i2;
        return i2;
    }
}
