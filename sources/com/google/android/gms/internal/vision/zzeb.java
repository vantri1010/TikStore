package com.google.android.gms.internal.vision;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.misc.Unsafe;

final class zzeb<T> implements zzen<T> {
    private static final int[] zznc = new int[0];
    private static final Unsafe zznd = zzfl.zzdz();
    private final int[] zzne;
    private final Object[] zznf;
    private final int zzng;
    private final int zznh;
    private final zzdx zzni;
    private final boolean zznj;
    private final boolean zznk;
    private final boolean zznl;
    private final boolean zznm;
    private final int[] zznn;
    private final int zzno;
    private final int zznp;
    private final zzef zznq;
    private final zzdh zznr;
    private final zzff<?, ?> zzns;
    private final zzcg<?> zznt;
    private final zzds zznu;

    private zzeb(int[] iArr, Object[] objArr, int i, int i2, zzdx zzdx, boolean z, boolean z2, int[] iArr2, int i3, int i4, zzef zzef, zzdh zzdh, zzff<?, ?> zzff, zzcg<?> zzcg, zzds zzds) {
        this.zzne = iArr;
        this.zznf = objArr;
        this.zzng = i;
        this.zznh = i2;
        this.zznk = zzdx instanceof zzcr;
        this.zznl = z;
        this.zznj = zzcg != null && zzcg.zze(zzdx);
        this.zznm = false;
        this.zznn = iArr2;
        this.zzno = i3;
        this.zznp = i4;
        this.zznq = zzef;
        this.zznr = zzdh;
        this.zzns = zzff;
        this.zznt = zzcg;
        this.zzni = zzdx;
        this.zznu = zzds;
    }

    private static int zza(int i, byte[] bArr, int i2, int i3, Object obj, zzbl zzbl) throws IOException {
        return zzbk.zza(i, bArr, i2, i3, zzo(obj), zzbl);
    }

    private static int zza(zzen<?> zzen, int i, byte[] bArr, int i2, int i3, zzcw<?> zzcw, zzbl zzbl) throws IOException {
        int zza = zza((zzen) zzen, bArr, i2, i3, zzbl);
        while (true) {
            zzcw.add(zzbl.zzgq);
            if (zza >= i3) {
                break;
            }
            int zza2 = zzbk.zza(bArr, zza, zzbl);
            if (i != zzbl.zzgo) {
                break;
            }
            zza = zza((zzen) zzen, bArr, zza2, i3, zzbl);
        }
        return zza;
    }

    private static int zza(zzen zzen, byte[] bArr, int i, int i2, int i3, zzbl zzbl) throws IOException {
        zzeb zzeb = (zzeb) zzen;
        Object newInstance = zzeb.newInstance();
        int zza = zzeb.zza(newInstance, bArr, i, i2, i3, zzbl);
        zzeb.zzd(newInstance);
        zzbl.zzgq = newInstance;
        return zza;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: byte} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int zza(com.google.android.gms.internal.vision.zzen r6, byte[] r7, int r8, int r9, com.google.android.gms.internal.vision.zzbl r10) throws java.io.IOException {
        /*
            int r0 = r8 + 1
            byte r8 = r7[r8]
            if (r8 >= 0) goto L_0x000c
            int r0 = com.google.android.gms.internal.vision.zzbk.zza((int) r8, (byte[]) r7, (int) r0, (com.google.android.gms.internal.vision.zzbl) r10)
            int r8 = r10.zzgo
        L_0x000c:
            r3 = r0
            if (r8 < 0) goto L_0x0025
            int r9 = r9 - r3
            if (r8 > r9) goto L_0x0025
            java.lang.Object r9 = r6.newInstance()
            int r8 = r8 + r3
            r0 = r6
            r1 = r9
            r2 = r7
            r4 = r8
            r5 = r10
            r0.zza(r1, r2, r3, r4, r5)
            r6.zzd(r9)
            r10.zzgq = r9
            return r8
        L_0x0025:
            com.google.android.gms.internal.vision.zzcx r6 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zza(com.google.android.gms.internal.vision.zzen, byte[], int, int, com.google.android.gms.internal.vision.zzbl):int");
    }

    private static <UT, UB> int zza(zzff<UT, UB> zzff, T t) {
        return zzff.zzn(zzff.zzr(t));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00b7, code lost:
        r2 = r2 + r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x013f, code lost:
        r3 = java.lang.Integer.valueOf(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x014c, code lost:
        r3 = java.lang.Long.valueOf(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0150, code lost:
        r12.putObject(r1, r9, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x015e, code lost:
        r12.putObject(r1, r9, r2);
        r2 = r4 + 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x016f, code lost:
        r12.putObject(r1, r9, r2);
        r2 = r4 + 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0174, code lost:
        r12.putInt(r1, r13, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:?, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:?, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final int zza(T r17, byte[] r18, int r19, int r20, int r21, int r22, int r23, int r24, int r25, long r26, int r28, com.google.android.gms.internal.vision.zzbl r29) throws java.io.IOException {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r3 = r18
            r4 = r19
            r2 = r21
            r8 = r22
            r5 = r23
            r9 = r26
            r6 = r28
            r11 = r29
            sun.misc.Unsafe r12 = zznd
            int[] r7 = r0.zzne
            int r13 = r6 + 2
            r7 = r7[r13]
            r13 = 1048575(0xfffff, float:1.469367E-39)
            r7 = r7 & r13
            long r13 = (long) r7
            r7 = 5
            r15 = 2
            switch(r25) {
                case 51: goto L_0x0164;
                case 52: goto L_0x0154;
                case 53: goto L_0x0144;
                case 54: goto L_0x0144;
                case 55: goto L_0x0137;
                case 56: goto L_0x012b;
                case 57: goto L_0x0120;
                case 58: goto L_0x010a;
                case 59: goto L_0x00de;
                case 60: goto L_0x00ba;
                case 61: goto L_0x00a2;
                case 62: goto L_0x0137;
                case 63: goto L_0x0074;
                case 64: goto L_0x0120;
                case 65: goto L_0x012b;
                case 66: goto L_0x0066;
                case 67: goto L_0x0058;
                case 68: goto L_0x0028;
                default: goto L_0x0026;
            }
        L_0x0026:
            goto L_0x0178
        L_0x0028:
            r7 = 3
            if (r5 != r7) goto L_0x0178
            r2 = r2 & -8
            r7 = r2 | 4
            com.google.android.gms.internal.vision.zzen r2 = r0.zzag(r6)
            r3 = r18
            r4 = r19
            r5 = r20
            r6 = r7
            r7 = r29
            int r2 = zza((com.google.android.gms.internal.vision.zzen) r2, (byte[]) r3, (int) r4, (int) r5, (int) r6, (com.google.android.gms.internal.vision.zzbl) r7)
            int r3 = r12.getInt(r1, r13)
            if (r3 != r8) goto L_0x004b
            java.lang.Object r15 = r12.getObject(r1, r9)
            goto L_0x004c
        L_0x004b:
            r15 = 0
        L_0x004c:
            java.lang.Object r3 = r11.zzgq
            if (r15 != 0) goto L_0x0052
            goto L_0x0150
        L_0x0052:
            java.lang.Object r3 = com.google.android.gms.internal.vision.zzct.zza((java.lang.Object) r15, (java.lang.Object) r3)
            goto L_0x0150
        L_0x0058:
            if (r5 != 0) goto L_0x0178
            int r2 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r4, r11)
            long r3 = r11.zzgp
            long r3 = com.google.android.gms.internal.vision.zzbx.zza(r3)
            goto L_0x014c
        L_0x0066:
            if (r5 != 0) goto L_0x0178
            int r2 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r11)
            int r3 = r11.zzgo
            int r3 = com.google.android.gms.internal.vision.zzbx.zzo(r3)
            goto L_0x013f
        L_0x0074:
            if (r5 != 0) goto L_0x0178
            int r3 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r11)
            int r4 = r11.zzgo
            com.google.android.gms.internal.vision.zzcv r5 = r0.zzai(r6)
            if (r5 == 0) goto L_0x0098
            com.google.android.gms.internal.vision.zzcu r5 = r5.zzaf(r4)
            if (r5 == 0) goto L_0x0089
            goto L_0x0098
        L_0x0089:
            com.google.android.gms.internal.vision.zzfg r1 = zzo(r17)
            long r4 = (long) r4
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            r1.zzb(r2, r4)
            r2 = r3
            goto L_0x0179
        L_0x0098:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            r12.putObject(r1, r9, r2)
            r2 = r3
            goto L_0x0174
        L_0x00a2:
            if (r5 != r15) goto L_0x0178
            int r2 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r11)
            int r4 = r11.zzgo
            if (r4 != 0) goto L_0x00b0
            com.google.android.gms.internal.vision.zzbo r3 = com.google.android.gms.internal.vision.zzbo.zzgt
            goto L_0x0150
        L_0x00b0:
            com.google.android.gms.internal.vision.zzbo r3 = com.google.android.gms.internal.vision.zzbo.zzb((byte[]) r3, (int) r2, (int) r4)
            r12.putObject(r1, r9, r3)
        L_0x00b7:
            int r2 = r2 + r4
            goto L_0x0174
        L_0x00ba:
            if (r5 != r15) goto L_0x0178
            com.google.android.gms.internal.vision.zzen r2 = r0.zzag(r6)
            r5 = r20
            int r2 = zza((com.google.android.gms.internal.vision.zzen) r2, (byte[]) r3, (int) r4, (int) r5, (com.google.android.gms.internal.vision.zzbl) r11)
            int r3 = r12.getInt(r1, r13)
            if (r3 != r8) goto L_0x00d1
            java.lang.Object r15 = r12.getObject(r1, r9)
            goto L_0x00d2
        L_0x00d1:
            r15 = 0
        L_0x00d2:
            java.lang.Object r3 = r11.zzgq
            if (r15 != 0) goto L_0x00d8
            goto L_0x0150
        L_0x00d8:
            java.lang.Object r3 = com.google.android.gms.internal.vision.zzct.zza((java.lang.Object) r15, (java.lang.Object) r3)
            goto L_0x0150
        L_0x00de:
            if (r5 != r15) goto L_0x0178
            int r2 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r11)
            int r4 = r11.zzgo
            if (r4 != 0) goto L_0x00eb
            java.lang.String r3 = ""
            goto L_0x0150
        L_0x00eb:
            r5 = 536870912(0x20000000, float:1.0842022E-19)
            r5 = r24 & r5
            if (r5 == 0) goto L_0x00ff
            int r5 = r2 + r4
            boolean r5 = com.google.android.gms.internal.vision.zzfn.zze(r3, r2, r5)
            if (r5 == 0) goto L_0x00fa
            goto L_0x00ff
        L_0x00fa:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcg()
            throw r1
        L_0x00ff:
            java.lang.String r5 = new java.lang.String
            java.nio.charset.Charset r6 = com.google.android.gms.internal.vision.zzct.UTF_8
            r5.<init>(r3, r2, r4, r6)
            r12.putObject(r1, r9, r5)
            goto L_0x00b7
        L_0x010a:
            if (r5 != 0) goto L_0x0178
            int r2 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r4, r11)
            long r3 = r11.zzgp
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x011a
            r15 = 1
            goto L_0x011b
        L_0x011a:
            r15 = 0
        L_0x011b:
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r15)
            goto L_0x0150
        L_0x0120:
            if (r5 != r7) goto L_0x0178
            int r2 = com.google.android.gms.internal.vision.zzbk.zza(r18, r19)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            goto L_0x015e
        L_0x012b:
            r2 = 1
            if (r5 != r2) goto L_0x0178
            long r2 = com.google.android.gms.internal.vision.zzbk.zzb(r18, r19)
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            goto L_0x016f
        L_0x0137:
            if (r5 != 0) goto L_0x0178
            int r2 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r11)
            int r3 = r11.zzgo
        L_0x013f:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            goto L_0x0150
        L_0x0144:
            if (r5 != 0) goto L_0x0178
            int r2 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r4, r11)
            long r3 = r11.zzgp
        L_0x014c:
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
        L_0x0150:
            r12.putObject(r1, r9, r3)
            goto L_0x0174
        L_0x0154:
            if (r5 != r7) goto L_0x0178
            float r2 = com.google.android.gms.internal.vision.zzbk.zzd(r18, r19)
            java.lang.Float r2 = java.lang.Float.valueOf(r2)
        L_0x015e:
            r12.putObject(r1, r9, r2)
            int r2 = r4 + 4
            goto L_0x0174
        L_0x0164:
            r2 = 1
            if (r5 != r2) goto L_0x0178
            double r2 = com.google.android.gms.internal.vision.zzbk.zzc(r18, r19)
            java.lang.Double r2 = java.lang.Double.valueOf(r2)
        L_0x016f:
            r12.putObject(r1, r9, r2)
            int r2 = r4 + 8
        L_0x0174:
            r12.putInt(r1, r13, r8)
            goto L_0x0179
        L_0x0178:
            r2 = r4
        L_0x0179:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zza(java.lang.Object, byte[], int, int, int, int, int, int, int, long, int, com.google.android.gms.internal.vision.zzbl):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:131:0x025c, code lost:
        if (r7.zzgp != 0) goto L_0x025e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x025e, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0260, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0261, code lost:
        r11.addBoolean(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0264, code lost:
        if (r4 >= r5) goto L_0x03c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x0266, code lost:
        r6 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x026c, code lost:
        if (r2 != r7.zzgo) goto L_0x03c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x026e, code lost:
        r4 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r6, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x0276, code lost:
        if (r7.zzgp == 0) goto L_0x0260;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:?, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:?, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:?, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:?, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x013c, code lost:
        if (r4 == 0) goto L_0x013e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x013e, code lost:
        r11.add(com.google.android.gms.internal.vision.zzbo.zzgt);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0144, code lost:
        r11.add(com.google.android.gms.internal.vision.zzbo.zzb(r3, r1, r4));
        r1 = r1 + r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x014c, code lost:
        if (r1 >= r5) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x014e, code lost:
        r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0154, code lost:
        if (r2 != r7.zzgo) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0156, code lost:
        r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7);
        r4 = r7.zzgo;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x015c, code lost:
        if (r4 < 0) goto L_0x0161;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x015e, code lost:
        if (r4 != 0) goto L_0x0144;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0165, code lost:
        throw com.google.android.gms.internal.vision.zzcx.zzcc();
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01f1  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01ad  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final int zza(T r17, byte[] r18, int r19, int r20, int r21, int r22, int r23, int r24, long r25, int r27, long r28, com.google.android.gms.internal.vision.zzbl r30) throws java.io.IOException {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r3 = r18
            r4 = r19
            r5 = r20
            r2 = r21
            r6 = r23
            r8 = r24
            r9 = r28
            r7 = r30
            sun.misc.Unsafe r11 = zznd
            java.lang.Object r11 = r11.getObject(r1, r9)
            com.google.android.gms.internal.vision.zzcw r11 = (com.google.android.gms.internal.vision.zzcw) r11
            boolean r12 = r11.zzan()
            r13 = 1
            if (r12 != 0) goto L_0x0036
            int r12 = r11.size()
            if (r12 != 0) goto L_0x002c
            r12 = 10
            goto L_0x002d
        L_0x002c:
            int r12 = r12 << r13
        L_0x002d:
            com.google.android.gms.internal.vision.zzcw r11 = r11.zzk(r12)
            sun.misc.Unsafe r12 = zznd
            r12.putObject(r1, r9, r11)
        L_0x0036:
            r9 = 5
            r14 = 0
            r10 = 2
            switch(r27) {
                case 18: goto L_0x0387;
                case 19: goto L_0x0349;
                case 20: goto L_0x0311;
                case 21: goto L_0x0311;
                case 22: goto L_0x02f7;
                case 23: goto L_0x02b8;
                case 24: goto L_0x0279;
                case 25: goto L_0x0228;
                case 26: goto L_0x0185;
                case 27: goto L_0x016b;
                case 28: goto L_0x0132;
                case 29: goto L_0x02f7;
                case 30: goto L_0x00fa;
                case 31: goto L_0x0279;
                case 32: goto L_0x02b8;
                case 33: goto L_0x00ba;
                case 34: goto L_0x007a;
                case 35: goto L_0x0387;
                case 36: goto L_0x0349;
                case 37: goto L_0x0311;
                case 38: goto L_0x0311;
                case 39: goto L_0x02f7;
                case 40: goto L_0x02b8;
                case 41: goto L_0x0279;
                case 42: goto L_0x0228;
                case 43: goto L_0x02f7;
                case 44: goto L_0x00fa;
                case 45: goto L_0x0279;
                case 46: goto L_0x02b8;
                case 47: goto L_0x00ba;
                case 48: goto L_0x007a;
                case 49: goto L_0x003f;
                default: goto L_0x003d;
            }
        L_0x003d:
            goto L_0x03c5
        L_0x003f:
            r1 = 3
            if (r6 != r1) goto L_0x03c5
            com.google.android.gms.internal.vision.zzen r1 = r0.zzag(r8)
            r6 = r2 & -8
            r6 = r6 | 4
            r22 = r1
            r23 = r18
            r24 = r19
            r25 = r20
            r26 = r6
            r27 = r30
            int r4 = zza((com.google.android.gms.internal.vision.zzen) r22, (byte[]) r23, (int) r24, (int) r25, (int) r26, (com.google.android.gms.internal.vision.zzbl) r27)
        L_0x005a:
            java.lang.Object r8 = r7.zzgq
            r11.add(r8)
            if (r4 >= r5) goto L_0x03c5
            int r8 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r9 = r7.zzgo
            if (r2 != r9) goto L_0x03c5
            r22 = r1
            r23 = r18
            r24 = r8
            r25 = r20
            r26 = r6
            r27 = r30
            int r4 = zza((com.google.android.gms.internal.vision.zzen) r22, (byte[]) r23, (int) r24, (int) r25, (int) r26, (com.google.android.gms.internal.vision.zzbl) r27)
            goto L_0x005a
        L_0x007a:
            if (r6 != r10) goto L_0x009e
            com.google.android.gms.internal.vision.zzdl r11 = (com.google.android.gms.internal.vision.zzdl) r11
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r2 = r7.zzgo
            int r2 = r2 + r1
        L_0x0085:
            if (r1 >= r2) goto L_0x0095
            int r1 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r1, r7)
            long r4 = r7.zzgp
            long r4 = com.google.android.gms.internal.vision.zzbx.zza(r4)
            r11.zzl(r4)
            goto L_0x0085
        L_0x0095:
            if (r1 != r2) goto L_0x0099
            goto L_0x03c6
        L_0x0099:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r1
        L_0x009e:
            if (r6 != 0) goto L_0x03c5
            com.google.android.gms.internal.vision.zzdl r11 = (com.google.android.gms.internal.vision.zzdl) r11
        L_0x00a2:
            int r1 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r4, r7)
            long r8 = r7.zzgp
            long r8 = com.google.android.gms.internal.vision.zzbx.zza(r8)
            r11.zzl(r8)
            if (r1 >= r5) goto L_0x03c6
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7)
            int r6 = r7.zzgo
            if (r2 != r6) goto L_0x03c6
            goto L_0x00a2
        L_0x00ba:
            if (r6 != r10) goto L_0x00de
            com.google.android.gms.internal.vision.zzcs r11 = (com.google.android.gms.internal.vision.zzcs) r11
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r2 = r7.zzgo
            int r2 = r2 + r1
        L_0x00c5:
            if (r1 >= r2) goto L_0x00d5
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7)
            int r4 = r7.zzgo
            int r4 = com.google.android.gms.internal.vision.zzbx.zzo(r4)
            r11.zzae(r4)
            goto L_0x00c5
        L_0x00d5:
            if (r1 != r2) goto L_0x00d9
            goto L_0x03c6
        L_0x00d9:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r1
        L_0x00de:
            if (r6 != 0) goto L_0x03c5
            com.google.android.gms.internal.vision.zzcs r11 = (com.google.android.gms.internal.vision.zzcs) r11
        L_0x00e2:
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r4 = r7.zzgo
            int r4 = com.google.android.gms.internal.vision.zzbx.zzo(r4)
            r11.zzae(r4)
            if (r1 >= r5) goto L_0x03c6
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7)
            int r6 = r7.zzgo
            if (r2 != r6) goto L_0x03c6
            goto L_0x00e2
        L_0x00fa:
            if (r6 != r10) goto L_0x0101
            int r2 = com.google.android.gms.internal.vision.zzbk.zza((byte[]) r3, (int) r4, (com.google.android.gms.internal.vision.zzcw<?>) r11, (com.google.android.gms.internal.vision.zzbl) r7)
            goto L_0x0112
        L_0x0101:
            if (r6 != 0) goto L_0x03c5
            r2 = r21
            r3 = r18
            r4 = r19
            r5 = r20
            r6 = r11
            r7 = r30
            int r2 = com.google.android.gms.internal.vision.zzbk.zza((int) r2, (byte[]) r3, (int) r4, (int) r5, (com.google.android.gms.internal.vision.zzcw<?>) r6, (com.google.android.gms.internal.vision.zzbl) r7)
        L_0x0112:
            com.google.android.gms.internal.vision.zzcr r1 = (com.google.android.gms.internal.vision.zzcr) r1
            com.google.android.gms.internal.vision.zzfg r3 = r1.zzkr
            com.google.android.gms.internal.vision.zzfg r4 = com.google.android.gms.internal.vision.zzfg.zzdu()
            if (r3 != r4) goto L_0x011d
            r3 = 0
        L_0x011d:
            com.google.android.gms.internal.vision.zzcv r4 = r0.zzai(r8)
            com.google.android.gms.internal.vision.zzff<?, ?> r5 = r0.zzns
            r6 = r22
            java.lang.Object r3 = com.google.android.gms.internal.vision.zzep.zza(r6, r11, r4, r3, r5)
            com.google.android.gms.internal.vision.zzfg r3 = (com.google.android.gms.internal.vision.zzfg) r3
            if (r3 == 0) goto L_0x012f
            r1.zzkr = r3
        L_0x012f:
            r1 = r2
            goto L_0x03c6
        L_0x0132:
            if (r6 != r10) goto L_0x03c5
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r4 = r7.zzgo
            if (r4 < 0) goto L_0x0166
            if (r4 != 0) goto L_0x0144
        L_0x013e:
            com.google.android.gms.internal.vision.zzbo r4 = com.google.android.gms.internal.vision.zzbo.zzgt
            r11.add(r4)
            goto L_0x014c
        L_0x0144:
            com.google.android.gms.internal.vision.zzbo r6 = com.google.android.gms.internal.vision.zzbo.zzb((byte[]) r3, (int) r1, (int) r4)
            r11.add(r6)
            int r1 = r1 + r4
        L_0x014c:
            if (r1 >= r5) goto L_0x03c6
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7)
            int r6 = r7.zzgo
            if (r2 != r6) goto L_0x03c6
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r4 = r7.zzgo
            if (r4 < 0) goto L_0x0161
            if (r4 != 0) goto L_0x0144
            goto L_0x013e
        L_0x0161:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcc()
            throw r1
        L_0x0166:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcc()
            throw r1
        L_0x016b:
            if (r6 != r10) goto L_0x03c5
            com.google.android.gms.internal.vision.zzen r1 = r0.zzag(r8)
            r22 = r1
            r23 = r21
            r24 = r18
            r25 = r19
            r26 = r20
            r27 = r11
            r28 = r30
            int r1 = zza((com.google.android.gms.internal.vision.zzen<?>) r22, (int) r23, (byte[]) r24, (int) r25, (int) r26, (com.google.android.gms.internal.vision.zzcw<?>) r27, (com.google.android.gms.internal.vision.zzbl) r28)
            goto L_0x03c6
        L_0x0185:
            if (r6 != r10) goto L_0x03c5
            r8 = 536870912(0x20000000, double:2.652494739E-315)
            long r8 = r25 & r8
            java.lang.String r1 = ""
            int r6 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            if (r6 != 0) goto L_0x01d2
            int r6 = r7.zzgo
            if (r6 < 0) goto L_0x01cd
            if (r6 != 0) goto L_0x01a0
        L_0x019c:
            r11.add(r1)
            goto L_0x01ab
        L_0x01a0:
            java.lang.String r8 = new java.lang.String
            java.nio.charset.Charset r9 = com.google.android.gms.internal.vision.zzct.UTF_8
            r8.<init>(r3, r4, r6, r9)
        L_0x01a7:
            r11.add(r8)
            int r4 = r4 + r6
        L_0x01ab:
            if (r4 >= r5) goto L_0x03c5
            int r6 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r8 = r7.zzgo
            if (r2 != r8) goto L_0x03c5
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r6, r7)
            int r6 = r7.zzgo
            if (r6 < 0) goto L_0x01c8
            if (r6 != 0) goto L_0x01c0
            goto L_0x019c
        L_0x01c0:
            java.lang.String r8 = new java.lang.String
            java.nio.charset.Charset r9 = com.google.android.gms.internal.vision.zzct.UTF_8
            r8.<init>(r3, r4, r6, r9)
            goto L_0x01a7
        L_0x01c8:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcc()
            throw r1
        L_0x01cd:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcc()
            throw r1
        L_0x01d2:
            int r6 = r7.zzgo
            if (r6 < 0) goto L_0x0223
            if (r6 != 0) goto L_0x01dc
        L_0x01d8:
            r11.add(r1)
            goto L_0x01ef
        L_0x01dc:
            int r8 = r4 + r6
            boolean r9 = com.google.android.gms.internal.vision.zzfn.zze(r3, r4, r8)
            if (r9 == 0) goto L_0x021e
            java.lang.String r9 = new java.lang.String
            java.nio.charset.Charset r10 = com.google.android.gms.internal.vision.zzct.UTF_8
            r9.<init>(r3, r4, r6, r10)
        L_0x01eb:
            r11.add(r9)
            r4 = r8
        L_0x01ef:
            if (r4 >= r5) goto L_0x03c5
            int r6 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r8 = r7.zzgo
            if (r2 != r8) goto L_0x03c5
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r6, r7)
            int r6 = r7.zzgo
            if (r6 < 0) goto L_0x0219
            if (r6 != 0) goto L_0x0204
            goto L_0x01d8
        L_0x0204:
            int r8 = r4 + r6
            boolean r9 = com.google.android.gms.internal.vision.zzfn.zze(r3, r4, r8)
            if (r9 == 0) goto L_0x0214
            java.lang.String r9 = new java.lang.String
            java.nio.charset.Charset r10 = com.google.android.gms.internal.vision.zzct.UTF_8
            r9.<init>(r3, r4, r6, r10)
            goto L_0x01eb
        L_0x0214:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcg()
            throw r1
        L_0x0219:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcc()
            throw r1
        L_0x021e:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcg()
            throw r1
        L_0x0223:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcc()
            throw r1
        L_0x0228:
            r1 = 0
            if (r6 != r10) goto L_0x0250
            com.google.android.gms.internal.vision.zzbm r11 = (com.google.android.gms.internal.vision.zzbm) r11
            int r2 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r4 = r7.zzgo
            int r4 = r4 + r2
        L_0x0234:
            if (r2 >= r4) goto L_0x0247
            int r2 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r2, r7)
            long r5 = r7.zzgp
            int r8 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r8 == 0) goto L_0x0242
            r5 = 1
            goto L_0x0243
        L_0x0242:
            r5 = 0
        L_0x0243:
            r11.addBoolean(r5)
            goto L_0x0234
        L_0x0247:
            if (r2 != r4) goto L_0x024b
            goto L_0x012f
        L_0x024b:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r1
        L_0x0250:
            if (r6 != 0) goto L_0x03c5
            com.google.android.gms.internal.vision.zzbm r11 = (com.google.android.gms.internal.vision.zzbm) r11
            int r4 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r4, r7)
            long r8 = r7.zzgp
            int r6 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r6 == 0) goto L_0x0260
        L_0x025e:
            r6 = 1
            goto L_0x0261
        L_0x0260:
            r6 = 0
        L_0x0261:
            r11.addBoolean(r6)
            if (r4 >= r5) goto L_0x03c5
            int r6 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r8 = r7.zzgo
            if (r2 != r8) goto L_0x03c5
            int r4 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r6, r7)
            long r8 = r7.zzgp
            int r6 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r6 == 0) goto L_0x0260
            goto L_0x025e
        L_0x0279:
            if (r6 != r10) goto L_0x0299
            com.google.android.gms.internal.vision.zzcs r11 = (com.google.android.gms.internal.vision.zzcs) r11
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r2 = r7.zzgo
            int r2 = r2 + r1
        L_0x0284:
            if (r1 >= r2) goto L_0x0290
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1)
            r11.zzae(r4)
            int r1 = r1 + 4
            goto L_0x0284
        L_0x0290:
            if (r1 != r2) goto L_0x0294
            goto L_0x03c6
        L_0x0294:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r1
        L_0x0299:
            if (r6 != r9) goto L_0x03c5
            com.google.android.gms.internal.vision.zzcs r11 = (com.google.android.gms.internal.vision.zzcs) r11
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r18, r19)
            r11.zzae(r1)
        L_0x02a4:
            int r1 = r4 + 4
            if (r1 >= r5) goto L_0x03c6
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7)
            int r6 = r7.zzgo
            if (r2 != r6) goto L_0x03c6
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4)
            r11.zzae(r1)
            goto L_0x02a4
        L_0x02b8:
            if (r6 != r10) goto L_0x02d8
            com.google.android.gms.internal.vision.zzdl r11 = (com.google.android.gms.internal.vision.zzdl) r11
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r2 = r7.zzgo
            int r2 = r2 + r1
        L_0x02c3:
            if (r1 >= r2) goto L_0x02cf
            long r4 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r1)
            r11.zzl(r4)
            int r1 = r1 + 8
            goto L_0x02c3
        L_0x02cf:
            if (r1 != r2) goto L_0x02d3
            goto L_0x03c6
        L_0x02d3:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r1
        L_0x02d8:
            if (r6 != r13) goto L_0x03c5
            com.google.android.gms.internal.vision.zzdl r11 = (com.google.android.gms.internal.vision.zzdl) r11
            long r8 = com.google.android.gms.internal.vision.zzbk.zzb(r18, r19)
            r11.zzl(r8)
        L_0x02e3:
            int r1 = r4 + 8
            if (r1 >= r5) goto L_0x03c6
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7)
            int r6 = r7.zzgo
            if (r2 != r6) goto L_0x03c6
            long r8 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r4)
            r11.zzl(r8)
            goto L_0x02e3
        L_0x02f7:
            if (r6 != r10) goto L_0x02ff
            int r1 = com.google.android.gms.internal.vision.zzbk.zza((byte[]) r3, (int) r4, (com.google.android.gms.internal.vision.zzcw<?>) r11, (com.google.android.gms.internal.vision.zzbl) r7)
            goto L_0x03c6
        L_0x02ff:
            if (r6 != 0) goto L_0x03c5
            r22 = r18
            r23 = r19
            r24 = r20
            r25 = r11
            r26 = r30
            int r1 = com.google.android.gms.internal.vision.zzbk.zza((int) r21, (byte[]) r22, (int) r23, (int) r24, (com.google.android.gms.internal.vision.zzcw<?>) r25, (com.google.android.gms.internal.vision.zzbl) r26)
            goto L_0x03c6
        L_0x0311:
            if (r6 != r10) goto L_0x0331
            com.google.android.gms.internal.vision.zzdl r11 = (com.google.android.gms.internal.vision.zzdl) r11
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r2 = r7.zzgo
            int r2 = r2 + r1
        L_0x031c:
            if (r1 >= r2) goto L_0x0328
            int r1 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r1, r7)
            long r4 = r7.zzgp
            r11.zzl(r4)
            goto L_0x031c
        L_0x0328:
            if (r1 != r2) goto L_0x032c
            goto L_0x03c6
        L_0x032c:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r1
        L_0x0331:
            if (r6 != 0) goto L_0x03c5
            com.google.android.gms.internal.vision.zzdl r11 = (com.google.android.gms.internal.vision.zzdl) r11
        L_0x0335:
            int r1 = com.google.android.gms.internal.vision.zzbk.zzb(r3, r4, r7)
            long r8 = r7.zzgp
            r11.zzl(r8)
            if (r1 >= r5) goto L_0x03c6
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7)
            int r6 = r7.zzgo
            if (r2 != r6) goto L_0x03c6
            goto L_0x0335
        L_0x0349:
            if (r6 != r10) goto L_0x0368
            com.google.android.gms.internal.vision.zzcp r11 = (com.google.android.gms.internal.vision.zzcp) r11
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r2 = r7.zzgo
            int r2 = r2 + r1
        L_0x0354:
            if (r1 >= r2) goto L_0x0360
            float r4 = com.google.android.gms.internal.vision.zzbk.zzd(r3, r1)
            r11.zze(r4)
            int r1 = r1 + 4
            goto L_0x0354
        L_0x0360:
            if (r1 != r2) goto L_0x0363
            goto L_0x03c6
        L_0x0363:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r1
        L_0x0368:
            if (r6 != r9) goto L_0x03c5
            com.google.android.gms.internal.vision.zzcp r11 = (com.google.android.gms.internal.vision.zzcp) r11
            float r1 = com.google.android.gms.internal.vision.zzbk.zzd(r18, r19)
            r11.zze(r1)
        L_0x0373:
            int r1 = r4 + 4
            if (r1 >= r5) goto L_0x03c6
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7)
            int r6 = r7.zzgo
            if (r2 != r6) goto L_0x03c6
            float r1 = com.google.android.gms.internal.vision.zzbk.zzd(r3, r4)
            r11.zze(r1)
            goto L_0x0373
        L_0x0387:
            if (r6 != r10) goto L_0x03a6
            com.google.android.gms.internal.vision.zzcd r11 = (com.google.android.gms.internal.vision.zzcd) r11
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r3, r4, r7)
            int r2 = r7.zzgo
            int r2 = r2 + r1
        L_0x0392:
            if (r1 >= r2) goto L_0x039e
            double r4 = com.google.android.gms.internal.vision.zzbk.zzc(r3, r1)
            r11.zzc(r4)
            int r1 = r1 + 8
            goto L_0x0392
        L_0x039e:
            if (r1 != r2) goto L_0x03a1
            goto L_0x03c6
        L_0x03a1:
            com.google.android.gms.internal.vision.zzcx r1 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r1
        L_0x03a6:
            if (r6 != r13) goto L_0x03c5
            com.google.android.gms.internal.vision.zzcd r11 = (com.google.android.gms.internal.vision.zzcd) r11
            double r8 = com.google.android.gms.internal.vision.zzbk.zzc(r18, r19)
            r11.zzc(r8)
        L_0x03b1:
            int r1 = r4 + 8
            if (r1 >= r5) goto L_0x03c6
            int r4 = com.google.android.gms.internal.vision.zzbk.zza(r3, r1, r7)
            int r6 = r7.zzgo
            if (r2 != r6) goto L_0x03c6
            double r8 = com.google.android.gms.internal.vision.zzbk.zzc(r3, r4)
            r11.zzc(r8)
            goto L_0x03b1
        L_0x03c5:
            r1 = r4
        L_0x03c6:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zza(java.lang.Object, byte[], int, int, int, int, int, int, long, int, long, com.google.android.gms.internal.vision.zzbl):int");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: byte} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final <K, V> int zza(T r8, byte[] r9, int r10, int r11, int r12, long r13, com.google.android.gms.internal.vision.zzbl r15) throws java.io.IOException {
        /*
            r7 = this;
            sun.misc.Unsafe r0 = zznd
            java.lang.Object r12 = r7.zzah(r12)
            java.lang.Object r1 = r0.getObject(r8, r13)
            com.google.android.gms.internal.vision.zzds r2 = r7.zznu
            boolean r2 = r2.zzj(r1)
            if (r2 == 0) goto L_0x0021
            com.google.android.gms.internal.vision.zzds r2 = r7.zznu
            java.lang.Object r2 = r2.zzl(r12)
            com.google.android.gms.internal.vision.zzds r3 = r7.zznu
            r3.zzb(r2, r1)
            r0.putObject(r8, r13, r2)
            r1 = r2
        L_0x0021:
            com.google.android.gms.internal.vision.zzds r8 = r7.zznu
            com.google.android.gms.internal.vision.zzdq r8 = r8.zzm(r12)
            com.google.android.gms.internal.vision.zzds r12 = r7.zznu
            java.util.Map r12 = r12.zzh(r1)
            int r10 = com.google.android.gms.internal.vision.zzbk.zza(r9, r10, r15)
            int r13 = r15.zzgo
            if (r13 < 0) goto L_0x0097
            int r14 = r11 - r10
            if (r13 > r14) goto L_0x0097
            int r13 = r13 + r10
            K r14 = r8.zzmx
            V r0 = r8.zzew
        L_0x003e:
            if (r10 >= r13) goto L_0x008c
            int r1 = r10 + 1
            byte r10 = r9[r10]
            if (r10 >= 0) goto L_0x004c
            int r1 = com.google.android.gms.internal.vision.zzbk.zza((int) r10, (byte[]) r9, (int) r1, (com.google.android.gms.internal.vision.zzbl) r15)
            int r10 = r15.zzgo
        L_0x004c:
            r2 = r1
            int r1 = r10 >>> 3
            r3 = r10 & 7
            r4 = 1
            if (r1 == r4) goto L_0x0072
            r4 = 2
            if (r1 == r4) goto L_0x0058
            goto L_0x0087
        L_0x0058:
            com.google.android.gms.internal.vision.zzft r1 = r8.zzmy
            int r1 = r1.zzee()
            if (r3 != r1) goto L_0x0087
            com.google.android.gms.internal.vision.zzft r4 = r8.zzmy
            V r10 = r8.zzew
            java.lang.Class r5 = r10.getClass()
            r1 = r9
            r3 = r11
            r6 = r15
            int r10 = zza((byte[]) r1, (int) r2, (int) r3, (com.google.android.gms.internal.vision.zzft) r4, (java.lang.Class<?>) r5, (com.google.android.gms.internal.vision.zzbl) r6)
            java.lang.Object r0 = r15.zzgq
            goto L_0x003e
        L_0x0072:
            com.google.android.gms.internal.vision.zzft r1 = r8.zzmw
            int r1 = r1.zzee()
            if (r3 != r1) goto L_0x0087
            com.google.android.gms.internal.vision.zzft r4 = r8.zzmw
            r5 = 0
            r1 = r9
            r3 = r11
            r6 = r15
            int r10 = zza((byte[]) r1, (int) r2, (int) r3, (com.google.android.gms.internal.vision.zzft) r4, (java.lang.Class<?>) r5, (com.google.android.gms.internal.vision.zzbl) r6)
            java.lang.Object r14 = r15.zzgq
            goto L_0x003e
        L_0x0087:
            int r10 = com.google.android.gms.internal.vision.zzbk.zza(r10, r9, r2, r11, r15)
            goto L_0x003e
        L_0x008c:
            if (r10 != r13) goto L_0x0092
            r12.put(r14, r0)
            return r13
        L_0x0092:
            com.google.android.gms.internal.vision.zzcx r8 = com.google.android.gms.internal.vision.zzcx.zzcf()
            throw r8
        L_0x0097:
            com.google.android.gms.internal.vision.zzcx r8 = com.google.android.gms.internal.vision.zzcx.zzcb()
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zza(java.lang.Object, byte[], int, int, int, long, com.google.android.gms.internal.vision.zzbl):int");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v22, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v2, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v13, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v4, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v19, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v20, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v21, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v36, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v41, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v44, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v44, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v19, resolved type: byte} */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x0328, code lost:
        if (r0 == r15) goto L_0x0399;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0371, code lost:
        if (r0 == r15) goto L_0x0399;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ee, code lost:
        r12 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x01a7, code lost:
        r10.putObject(r14, r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x01e7, code lost:
        r6 = r6 | r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x020b, code lost:
        r13 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0223, code lost:
        r10.putInt(r14, r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x023d, code lost:
        r10.putLong(r29, r2, r4);
        r6 = r6 | r22;
        r3 = r8;
        r2 = r9;
        r9 = r11;
        r0 = r13;
        r1 = r23;
        r13 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x027e, code lost:
        r0 = r13 + 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x0280, code lost:
        r6 = r6 | r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0282, code lost:
        r13 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0284, code lost:
        r3 = r8;
        r2 = r9;
        r9 = r11;
        r1 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0289, code lost:
        r11 = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x028d, code lost:
        r19 = r9;
        r27 = r10;
        r2 = r13;
        r9 = r8;
        r8 = r33;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final int zza(T r29, byte[] r30, int r31, int r32, int r33, com.google.android.gms.internal.vision.zzbl r34) throws java.io.IOException {
        /*
            r28 = this;
            r15 = r28
            r14 = r29
            r12 = r30
            r13 = r32
            r11 = r33
            r9 = r34
            sun.misc.Unsafe r10 = zznd
            r16 = 0
            r0 = r31
            r1 = -1
            r2 = 0
            r3 = 0
            r6 = 0
            r7 = -1
        L_0x0017:
            r17 = 1048575(0xfffff, float:1.469367E-39)
            if (r0 >= r13) goto L_0x03db
            int r3 = r0 + 1
            byte r0 = r12[r0]
            if (r0 >= 0) goto L_0x002b
            int r0 = com.google.android.gms.internal.vision.zzbk.zza((int) r0, (byte[]) r12, (int) r3, (com.google.android.gms.internal.vision.zzbl) r9)
            int r3 = r9.zzgo
            r4 = r0
            r5 = r3
            goto L_0x002d
        L_0x002b:
            r5 = r0
            r4 = r3
        L_0x002d:
            int r3 = r5 >>> 3
            r0 = r5 & 7
            r8 = 3
            if (r3 <= r1) goto L_0x003a
            int r2 = r2 / r8
            int r1 = r15.zzr(r3, r2)
            goto L_0x003e
        L_0x003a:
            int r1 = r15.zzal(r3)
        L_0x003e:
            r2 = r1
            r1 = -1
            if (r2 != r1) goto L_0x004d
            r23 = r3
            r2 = r4
            r9 = r5
            r27 = r10
            r8 = r11
            r19 = 0
            goto L_0x039d
        L_0x004d:
            int[] r1 = r15.zzne
            int r19 = r2 + 1
            r8 = r1[r19]
            r19 = 267386880(0xff00000, float:2.3665827E-29)
            r19 = r8 & r19
            int r11 = r19 >>> 20
            r19 = r5
            r5 = r8 & r17
            long r12 = (long) r5
            r5 = 17
            r20 = r8
            if (r11 > r5) goto L_0x0297
            int r5 = r2 + 2
            r1 = r1[r5]
            int r5 = r1 >>> 20
            r8 = 1
            int r22 = r8 << r5
            r1 = r1 & r17
            r5 = -1
            if (r1 == r7) goto L_0x007e
            if (r7 == r5) goto L_0x0078
            long r8 = (long) r7
            r10.putInt(r14, r8, r6)
        L_0x0078:
            long r6 = (long) r1
            int r6 = r10.getInt(r14, r6)
            r7 = r1
        L_0x007e:
            r1 = 5
            switch(r11) {
                case 0: goto L_0x0267;
                case 1: goto L_0x024e;
                case 2: goto L_0x0228;
                case 3: goto L_0x0228;
                case 4: goto L_0x020e;
                case 5: goto L_0x01eb;
                case 6: goto L_0x01ce;
                case 7: goto L_0x01ab;
                case 8: goto L_0x0185;
                case 9: goto L_0x0156;
                case 10: goto L_0x013c;
                case 11: goto L_0x020e;
                case 12: goto L_0x010c;
                case 13: goto L_0x01ce;
                case 14: goto L_0x01eb;
                case 15: goto L_0x00f2;
                case 16: goto L_0x00d4;
                case 17: goto L_0x0090;
                default: goto L_0x0082;
            }
        L_0x0082:
            r12 = r30
            r11 = r34
            r9 = r2
            r23 = r3
            r13 = r4
            r8 = r19
            r19 = -1
            goto L_0x028d
        L_0x0090:
            r8 = 3
            if (r0 != r8) goto L_0x00ca
            int r0 = r3 << 3
            r8 = r0 | 4
            com.google.android.gms.internal.vision.zzen r0 = r15.zzag(r2)
            r1 = r30
            r9 = r2
            r2 = r4
            r23 = r3
            r3 = r32
            r4 = r8
            r8 = r19
            r19 = -1
            r5 = r34
            int r0 = zza((com.google.android.gms.internal.vision.zzen) r0, (byte[]) r1, (int) r2, (int) r3, (int) r4, (com.google.android.gms.internal.vision.zzbl) r5)
            r1 = r6 & r22
            r11 = r34
            if (r1 != 0) goto L_0x00b7
            java.lang.Object r1 = r11.zzgq
            goto L_0x00c1
        L_0x00b7:
            java.lang.Object r1 = r10.getObject(r14, r12)
            java.lang.Object r2 = r11.zzgq
            java.lang.Object r1 = com.google.android.gms.internal.vision.zzct.zza((java.lang.Object) r1, (java.lang.Object) r2)
        L_0x00c1:
            r10.putObject(r14, r12, r1)
            r6 = r6 | r22
            r12 = r30
            goto L_0x0282
        L_0x00ca:
            r11 = r34
            r9 = r2
            r23 = r3
            r8 = r19
            r19 = -1
            goto L_0x00ee
        L_0x00d4:
            r11 = r34
            r9 = r2
            r23 = r3
            r8 = r19
            r19 = -1
            if (r0 != 0) goto L_0x00ee
            r2 = r12
            r12 = r30
            int r13 = com.google.android.gms.internal.vision.zzbk.zzb(r12, r4, r11)
            long r0 = r11.zzgp
            long r4 = com.google.android.gms.internal.vision.zzbx.zza(r0)
            goto L_0x023d
        L_0x00ee:
            r12 = r30
            goto L_0x020b
        L_0x00f2:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r19 = -1
            r12 = r30
            if (r0 != 0) goto L_0x020b
            int r0 = com.google.android.gms.internal.vision.zzbk.zza(r12, r4, r11)
            int r1 = r11.zzgo
            int r1 = com.google.android.gms.internal.vision.zzbx.zzo(r1)
            goto L_0x0223
        L_0x010c:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r19 = -1
            r12 = r30
            if (r0 != 0) goto L_0x020b
            int r0 = com.google.android.gms.internal.vision.zzbk.zza(r12, r4, r11)
            int r1 = r11.zzgo
            com.google.android.gms.internal.vision.zzcv r4 = r15.zzai(r9)
            if (r4 == 0) goto L_0x0223
            com.google.android.gms.internal.vision.zzcu r4 = r4.zzaf(r1)
            if (r4 == 0) goto L_0x012e
            goto L_0x0223
        L_0x012e:
            com.google.android.gms.internal.vision.zzfg r2 = zzo(r29)
            long r3 = (long) r1
            java.lang.Long r1 = java.lang.Long.valueOf(r3)
            r2.zzb(r8, r1)
            goto L_0x0282
        L_0x013c:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r1 = 2
            r19 = -1
            r12 = r30
            if (r0 != r1) goto L_0x020b
            int r0 = com.google.android.gms.internal.vision.zzbk.zze(r12, r4, r11)
            java.lang.Object r1 = r11.zzgq
            r10.putObject(r14, r2, r1)
            goto L_0x0280
        L_0x0156:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r1 = 2
            r19 = -1
            r12 = r30
            if (r0 != r1) goto L_0x0181
            com.google.android.gms.internal.vision.zzen r0 = r15.zzag(r9)
            r13 = r32
            int r0 = zza((com.google.android.gms.internal.vision.zzen) r0, (byte[]) r12, (int) r4, (int) r13, (com.google.android.gms.internal.vision.zzbl) r11)
            r1 = r6 & r22
            if (r1 != 0) goto L_0x0176
            java.lang.Object r1 = r11.zzgq
            goto L_0x01a7
        L_0x0176:
            java.lang.Object r1 = r10.getObject(r14, r2)
            java.lang.Object r4 = r11.zzgq
            java.lang.Object r1 = com.google.android.gms.internal.vision.zzct.zza((java.lang.Object) r1, (java.lang.Object) r4)
            goto L_0x01a7
        L_0x0181:
            r13 = r32
            goto L_0x020b
        L_0x0185:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r1 = 2
            r19 = -1
            r12 = r30
            r13 = r32
            if (r0 != r1) goto L_0x020b
            r0 = 536870912(0x20000000, float:1.0842022E-19)
            r0 = r20 & r0
            if (r0 != 0) goto L_0x01a1
            int r0 = com.google.android.gms.internal.vision.zzbk.zzc(r12, r4, r11)
            goto L_0x01a5
        L_0x01a1:
            int r0 = com.google.android.gms.internal.vision.zzbk.zzd(r12, r4, r11)
        L_0x01a5:
            java.lang.Object r1 = r11.zzgq
        L_0x01a7:
            r10.putObject(r14, r2, r1)
            goto L_0x01e7
        L_0x01ab:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r19 = -1
            r12 = r30
            r13 = r32
            if (r0 != 0) goto L_0x020b
            int r0 = com.google.android.gms.internal.vision.zzbk.zzb(r12, r4, r11)
            long r4 = r11.zzgp
            r20 = 0
            int r1 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1))
            if (r1 == 0) goto L_0x01c9
            r1 = 1
            goto L_0x01ca
        L_0x01c9:
            r1 = 0
        L_0x01ca:
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r14, (long) r2, (boolean) r1)
            goto L_0x01e7
        L_0x01ce:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r19 = -1
            r12 = r30
            r13 = r32
            if (r0 != r1) goto L_0x020b
            int r0 = com.google.android.gms.internal.vision.zzbk.zza(r12, r4)
            r10.putInt(r14, r2, r0)
            int r0 = r4 + 4
        L_0x01e7:
            r6 = r6 | r22
            goto L_0x0284
        L_0x01eb:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r1 = 1
            r19 = -1
            r12 = r30
            r13 = r32
            if (r0 != r1) goto L_0x020b
            long r17 = com.google.android.gms.internal.vision.zzbk.zzb(r12, r4)
            r0 = r10
            r1 = r29
            r13 = r4
            r4 = r17
            r0.putLong(r1, r2, r4)
            goto L_0x027e
        L_0x020b:
            r13 = r4
            goto L_0x028d
        L_0x020e:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r19 = -1
            r12 = r30
            r13 = r4
            if (r0 != 0) goto L_0x028d
            int r0 = com.google.android.gms.internal.vision.zzbk.zza(r12, r13, r11)
            int r1 = r11.zzgo
        L_0x0223:
            r10.putInt(r14, r2, r1)
            goto L_0x0280
        L_0x0228:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r19 = -1
            r12 = r30
            r13 = r4
            if (r0 != 0) goto L_0x028d
            int r13 = com.google.android.gms.internal.vision.zzbk.zzb(r12, r13, r11)
            long r4 = r11.zzgp
        L_0x023d:
            r0 = r10
            r1 = r29
            r0.putLong(r1, r2, r4)
            r6 = r6 | r22
            r3 = r8
            r2 = r9
            r9 = r11
            r0 = r13
            r1 = r23
            r13 = r32
            goto L_0x0289
        L_0x024e:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r19 = -1
            r12 = r30
            r13 = r4
            if (r0 != r1) goto L_0x028d
            float r0 = com.google.android.gms.internal.vision.zzbk.zzd(r12, r13)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r14, (long) r2, (float) r0)
            int r0 = r13 + 4
            goto L_0x0280
        L_0x0267:
            r11 = r34
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r1 = 1
            r19 = -1
            r12 = r30
            r13 = r4
            if (r0 != r1) goto L_0x028d
            double r0 = com.google.android.gms.internal.vision.zzbk.zzc(r12, r13)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r14, (long) r2, (double) r0)
        L_0x027e:
            int r0 = r13 + 8
        L_0x0280:
            r6 = r6 | r22
        L_0x0282:
            r13 = r32
        L_0x0284:
            r3 = r8
            r2 = r9
            r9 = r11
            r1 = r23
        L_0x0289:
            r11 = r33
            goto L_0x0017
        L_0x028d:
            r19 = r9
            r27 = r10
            r2 = r13
            r9 = r8
            r8 = r33
            goto L_0x039d
        L_0x0297:
            r9 = r2
            r23 = r3
            r2 = r12
            r8 = r19
            r19 = -1
            r12 = r30
            r13 = r4
            r1 = 27
            if (r11 != r1) goto L_0x02f3
            r1 = 2
            if (r0 != r1) goto L_0x02e6
            java.lang.Object r0 = r10.getObject(r14, r2)
            com.google.android.gms.internal.vision.zzcw r0 = (com.google.android.gms.internal.vision.zzcw) r0
            boolean r1 = r0.zzan()
            if (r1 != 0) goto L_0x02c7
            int r1 = r0.size()
            if (r1 != 0) goto L_0x02be
            r1 = 10
            goto L_0x02c0
        L_0x02be:
            int r1 = r1 << 1
        L_0x02c0:
            com.google.android.gms.internal.vision.zzcw r0 = r0.zzk(r1)
            r10.putObject(r14, r2, r0)
        L_0x02c7:
            r5 = r0
            com.google.android.gms.internal.vision.zzen r0 = r15.zzag(r9)
            r1 = r8
            r2 = r30
            r3 = r13
            r4 = r32
            r18 = r6
            r6 = r34
            int r0 = zza((com.google.android.gms.internal.vision.zzen<?>) r0, (int) r1, (byte[]) r2, (int) r3, (int) r4, (com.google.android.gms.internal.vision.zzcw<?>) r5, (com.google.android.gms.internal.vision.zzbl) r6)
            r13 = r32
            r11 = r33
            r3 = r8
            r2 = r9
            r6 = r18
            r1 = r23
            goto L_0x03c3
        L_0x02e6:
            r18 = r6
            r22 = r7
            r26 = r8
            r19 = r9
            r27 = r10
            r15 = r13
            goto L_0x0374
        L_0x02f3:
            r18 = r6
            r1 = 49
            if (r11 > r1) goto L_0x0346
            r6 = r20
            long r5 = (long) r6
            r4 = r0
            r0 = r28
            r1 = r29
            r24 = r2
            r2 = r30
            r3 = r13
            r31 = r4
            r4 = r32
            r20 = r5
            r5 = r8
            r6 = r23
            r22 = r7
            r7 = r31
            r26 = r8
            r15 = -1
            r8 = r9
            r19 = r9
            r27 = r10
            r9 = r20
            r15 = r33
            r15 = r13
            r12 = r24
            r14 = r34
            int r0 = r0.zza(r1, (byte[]) r2, (int) r3, (int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (long) r9, (int) r11, (long) r12, (com.google.android.gms.internal.vision.zzbl) r14)
            if (r0 != r15) goto L_0x032c
            goto L_0x0399
        L_0x032c:
            r15 = r28
            r14 = r29
            r12 = r30
            r13 = r32
            r11 = r33
            r9 = r34
            r6 = r18
            r2 = r19
            r7 = r22
            r1 = r23
            r3 = r26
            r10 = r27
            goto L_0x0017
        L_0x0346:
            r31 = r0
            r24 = r2
            r22 = r7
            r26 = r8
            r19 = r9
            r27 = r10
            r15 = r13
            r6 = r20
            r0 = 50
            r7 = r31
            if (r11 != r0) goto L_0x037e
            r0 = 2
            if (r7 != r0) goto L_0x0374
            r0 = r28
            r1 = r29
            r2 = r30
            r3 = r15
            r4 = r32
            r5 = r19
            r6 = r24
            r8 = r34
            int r0 = r0.zza(r1, (byte[]) r2, (int) r3, (int) r4, (int) r5, (long) r6, (com.google.android.gms.internal.vision.zzbl) r8)
            if (r0 != r15) goto L_0x032c
            goto L_0x0399
        L_0x0374:
            r8 = r33
            r2 = r15
        L_0x0377:
            r6 = r18
            r7 = r22
            r9 = r26
            goto L_0x039d
        L_0x037e:
            r0 = r28
            r1 = r29
            r2 = r30
            r3 = r15
            r4 = r32
            r5 = r26
            r8 = r6
            r6 = r23
            r9 = r11
            r10 = r24
            r12 = r19
            r13 = r34
            int r0 = r0.zza(r1, (byte[]) r2, (int) r3, (int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (long) r10, (int) r12, (com.google.android.gms.internal.vision.zzbl) r13)
            if (r0 != r15) goto L_0x03c7
        L_0x0399:
            r8 = r33
            r2 = r0
            goto L_0x0377
        L_0x039d:
            if (r9 != r8) goto L_0x03a6
            if (r8 != 0) goto L_0x03a2
            goto L_0x03a6
        L_0x03a2:
            r0 = r7
            r1 = -1
            r7 = r2
            goto L_0x03e7
        L_0x03a6:
            r0 = r9
            r1 = r30
            r3 = r32
            r4 = r29
            r5 = r34
            int r0 = zza((int) r0, (byte[]) r1, (int) r2, (int) r3, (java.lang.Object) r4, (com.google.android.gms.internal.vision.zzbl) r5)
            r15 = r28
            r14 = r29
            r12 = r30
            r13 = r32
            r11 = r8
            r3 = r9
            r2 = r19
        L_0x03bf:
            r1 = r23
            r10 = r27
        L_0x03c3:
            r9 = r34
            goto L_0x0017
        L_0x03c7:
            r9 = r26
            r15 = r28
            r14 = r29
            r12 = r30
            r13 = r32
            r11 = r33
            r3 = r9
            r6 = r18
            r2 = r19
            r7 = r22
            goto L_0x03bf
        L_0x03db:
            r18 = r6
            r22 = r7
            r27 = r10
            r8 = r11
            r7 = r0
            r9 = r3
            r0 = r22
            r1 = -1
        L_0x03e7:
            if (r0 == r1) goto L_0x03f2
            long r0 = (long) r0
            r10 = r29
            r2 = r27
            r2.putInt(r10, r0, r6)
            goto L_0x03f4
        L_0x03f2:
            r10 = r29
        L_0x03f4:
            r0 = 0
            r11 = r28
            int r1 = r11.zzno
            r5 = r0
            r12 = r1
        L_0x03fb:
            int r0 = r11.zznp
            if (r12 >= r0) goto L_0x042f
            int[] r0 = r11.zznn
            r1 = r0[r12]
            com.google.android.gms.internal.vision.zzff<?, ?> r6 = r11.zzns
            int[] r0 = r11.zzne
            r2 = r0[r1]
            int r0 = r11.zzaj(r1)
            r0 = r0 & r17
            long r3 = (long) r0
            java.lang.Object r0 = com.google.android.gms.internal.vision.zzfl.zzo(r10, r3)
            if (r0 != 0) goto L_0x0417
            goto L_0x042a
        L_0x0417:
            com.google.android.gms.internal.vision.zzcv r4 = r11.zzai(r1)
            if (r4 != 0) goto L_0x041e
            goto L_0x042a
        L_0x041e:
            com.google.android.gms.internal.vision.zzds r3 = r11.zznu
            java.util.Map r3 = r3.zzh(r0)
            r0 = r28
            java.lang.Object r5 = r0.zza((int) r1, (int) r2, r3, (com.google.android.gms.internal.vision.zzcv<?>) r4, r5, r6)
        L_0x042a:
            com.google.android.gms.internal.vision.zzfg r5 = (com.google.android.gms.internal.vision.zzfg) r5
            int r12 = r12 + 1
            goto L_0x03fb
        L_0x042f:
            if (r5 == 0) goto L_0x0436
            com.google.android.gms.internal.vision.zzff<?, ?> r0 = r11.zzns
            r0.zzf(r10, r5)
        L_0x0436:
            r0 = r32
            if (r8 != 0) goto L_0x0442
            if (r7 != r0) goto L_0x043d
            goto L_0x0446
        L_0x043d:
            com.google.android.gms.internal.vision.zzcx r0 = com.google.android.gms.internal.vision.zzcx.zzcf()
            throw r0
        L_0x0442:
            if (r7 > r0) goto L_0x0447
            if (r9 != r8) goto L_0x0447
        L_0x0446:
            return r7
        L_0x0447:
            com.google.android.gms.internal.vision.zzcx r0 = com.google.android.gms.internal.vision.zzcx.zzcf()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zza(java.lang.Object, byte[], int, int, int, com.google.android.gms.internal.vision.zzbl):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x004d, code lost:
        r2 = java.lang.Integer.valueOf(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0051, code lost:
        r6.zzgq = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x006e, code lost:
        r6.zzgq = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x007b, code lost:
        r6.zzgq = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        return r2 + 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        return r2 + 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0042, code lost:
        r2 = java.lang.Long.valueOf(r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int zza(byte[] r1, int r2, int r3, com.google.android.gms.internal.vision.zzft r4, java.lang.Class<?> r5, com.google.android.gms.internal.vision.zzbl r6) throws java.io.IOException {
        /*
            int[] r0 = com.google.android.gms.internal.vision.zzec.zzhz
            int r4 = r4.ordinal()
            r4 = r0[r4]
            switch(r4) {
                case 1: goto L_0x0085;
                case 2: goto L_0x0080;
                case 3: goto L_0x0073;
                case 4: goto L_0x0066;
                case 5: goto L_0x0066;
                case 6: goto L_0x005d;
                case 7: goto L_0x005d;
                case 8: goto L_0x0054;
                case 9: goto L_0x0047;
                case 10: goto L_0x0047;
                case 11: goto L_0x0047;
                case 12: goto L_0x003c;
                case 13: goto L_0x003c;
                case 14: goto L_0x002f;
                case 15: goto L_0x0024;
                case 16: goto L_0x0019;
                case 17: goto L_0x0013;
                default: goto L_0x000b;
            }
        L_0x000b:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r2 = "unsupported field type."
            r1.<init>(r2)
            throw r1
        L_0x0013:
            int r1 = com.google.android.gms.internal.vision.zzbk.zzd(r1, r2, r6)
            goto L_0x0099
        L_0x0019:
            int r1 = com.google.android.gms.internal.vision.zzbk.zzb(r1, r2, r6)
            long r2 = r6.zzgp
            long r2 = com.google.android.gms.internal.vision.zzbx.zza(r2)
            goto L_0x0042
        L_0x0024:
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r1, r2, r6)
            int r2 = r6.zzgo
            int r2 = com.google.android.gms.internal.vision.zzbx.zzo(r2)
            goto L_0x004d
        L_0x002f:
            com.google.android.gms.internal.vision.zzek r4 = com.google.android.gms.internal.vision.zzek.zzdc()
            com.google.android.gms.internal.vision.zzen r4 = r4.zze(r5)
            int r1 = zza((com.google.android.gms.internal.vision.zzen) r4, (byte[]) r1, (int) r2, (int) r3, (com.google.android.gms.internal.vision.zzbl) r6)
            goto L_0x0099
        L_0x003c:
            int r1 = com.google.android.gms.internal.vision.zzbk.zzb(r1, r2, r6)
            long r2 = r6.zzgp
        L_0x0042:
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            goto L_0x0051
        L_0x0047:
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r1, r2, r6)
            int r2 = r6.zzgo
        L_0x004d:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
        L_0x0051:
            r6.zzgq = r2
            goto L_0x0099
        L_0x0054:
            float r1 = com.google.android.gms.internal.vision.zzbk.zzd(r1, r2)
            java.lang.Float r1 = java.lang.Float.valueOf(r1)
            goto L_0x006e
        L_0x005d:
            long r3 = com.google.android.gms.internal.vision.zzbk.zzb(r1, r2)
            java.lang.Long r1 = java.lang.Long.valueOf(r3)
            goto L_0x007b
        L_0x0066:
            int r1 = com.google.android.gms.internal.vision.zzbk.zza(r1, r2)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
        L_0x006e:
            r6.zzgq = r1
            int r1 = r2 + 4
            goto L_0x0099
        L_0x0073:
            double r3 = com.google.android.gms.internal.vision.zzbk.zzc(r1, r2)
            java.lang.Double r1 = java.lang.Double.valueOf(r3)
        L_0x007b:
            r6.zzgq = r1
            int r1 = r2 + 8
            goto L_0x0099
        L_0x0080:
            int r1 = com.google.android.gms.internal.vision.zzbk.zze(r1, r2, r6)
            goto L_0x0099
        L_0x0085:
            int r1 = com.google.android.gms.internal.vision.zzbk.zzb(r1, r2, r6)
            long r2 = r6.zzgp
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x0093
            r2 = 1
            goto L_0x0094
        L_0x0093:
            r2 = 0
        L_0x0094:
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)
            goto L_0x0051
        L_0x0099:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zza(byte[], int, int, com.google.android.gms.internal.vision.zzft, java.lang.Class, com.google.android.gms.internal.vision.zzbl):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:165:0x03a8  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x03f9  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x041f  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0422  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0427  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x042a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static <T> com.google.android.gms.internal.vision.zzeb<T> zza(java.lang.Class<T> r33, com.google.android.gms.internal.vision.zzdv r34, com.google.android.gms.internal.vision.zzef r35, com.google.android.gms.internal.vision.zzdh r36, com.google.android.gms.internal.vision.zzff<?, ?> r37, com.google.android.gms.internal.vision.zzcg<?> r38, com.google.android.gms.internal.vision.zzds r39) {
        /*
            r0 = r34
            boolean r1 = r0 instanceof com.google.android.gms.internal.vision.zzem
            if (r1 == 0) goto L_0x0475
            com.google.android.gms.internal.vision.zzem r0 = (com.google.android.gms.internal.vision.zzem) r0
            int r1 = r0.zzcv()
            int r2 = com.google.android.gms.internal.vision.zzcr.zzd.zzlh
            r3 = 0
            if (r1 != r2) goto L_0x0013
            r11 = 1
            goto L_0x0014
        L_0x0013:
            r11 = 0
        L_0x0014:
            java.lang.String r1 = r0.zzde()
            int r2 = r1.length()
            char r5 = r1.charAt(r3)
            r7 = 55296(0xd800, float:7.7486E-41)
            if (r5 < r7) goto L_0x003d
            r5 = r5 & 8191(0x1fff, float:1.1478E-41)
            r8 = 1
            r9 = 13
        L_0x002a:
            int r10 = r8 + 1
            char r8 = r1.charAt(r8)
            if (r8 < r7) goto L_0x003a
            r8 = r8 & 8191(0x1fff, float:1.1478E-41)
            int r8 = r8 << r9
            r5 = r5 | r8
            int r9 = r9 + 13
            r8 = r10
            goto L_0x002a
        L_0x003a:
            int r8 = r8 << r9
            r5 = r5 | r8
            goto L_0x003e
        L_0x003d:
            r10 = 1
        L_0x003e:
            int r8 = r10 + 1
            char r9 = r1.charAt(r10)
            if (r9 < r7) goto L_0x005d
            r9 = r9 & 8191(0x1fff, float:1.1478E-41)
            r10 = 13
        L_0x004a:
            int r12 = r8 + 1
            char r8 = r1.charAt(r8)
            if (r8 < r7) goto L_0x005a
            r8 = r8 & 8191(0x1fff, float:1.1478E-41)
            int r8 = r8 << r10
            r9 = r9 | r8
            int r10 = r10 + 13
            r8 = r12
            goto L_0x004a
        L_0x005a:
            int r8 = r8 << r10
            r9 = r9 | r8
            r8 = r12
        L_0x005d:
            if (r9 != 0) goto L_0x006a
            int[] r9 = zznc
            r14 = r9
            r6 = 0
            r9 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r15 = 0
            goto L_0x018a
        L_0x006a:
            int r9 = r8 + 1
            char r8 = r1.charAt(r8)
            if (r8 < r7) goto L_0x0089
            r8 = r8 & 8191(0x1fff, float:1.1478E-41)
            r10 = 13
        L_0x0076:
            int r12 = r9 + 1
            char r9 = r1.charAt(r9)
            if (r9 < r7) goto L_0x0086
            r9 = r9 & 8191(0x1fff, float:1.1478E-41)
            int r9 = r9 << r10
            r8 = r8 | r9
            int r10 = r10 + 13
            r9 = r12
            goto L_0x0076
        L_0x0086:
            int r9 = r9 << r10
            r8 = r8 | r9
            r9 = r12
        L_0x0089:
            int r10 = r9 + 1
            char r9 = r1.charAt(r9)
            if (r9 < r7) goto L_0x00a8
            r9 = r9 & 8191(0x1fff, float:1.1478E-41)
            r12 = 13
        L_0x0095:
            int r13 = r10 + 1
            char r10 = r1.charAt(r10)
            if (r10 < r7) goto L_0x00a5
            r10 = r10 & 8191(0x1fff, float:1.1478E-41)
            int r10 = r10 << r12
            r9 = r9 | r10
            int r12 = r12 + 13
            r10 = r13
            goto L_0x0095
        L_0x00a5:
            int r10 = r10 << r12
            r9 = r9 | r10
            r10 = r13
        L_0x00a8:
            int r12 = r10 + 1
            char r10 = r1.charAt(r10)
            if (r10 < r7) goto L_0x00c7
            r10 = r10 & 8191(0x1fff, float:1.1478E-41)
            r13 = 13
        L_0x00b4:
            int r14 = r12 + 1
            char r12 = r1.charAt(r12)
            if (r12 < r7) goto L_0x00c4
            r12 = r12 & 8191(0x1fff, float:1.1478E-41)
            int r12 = r12 << r13
            r10 = r10 | r12
            int r13 = r13 + 13
            r12 = r14
            goto L_0x00b4
        L_0x00c4:
            int r12 = r12 << r13
            r10 = r10 | r12
            r12 = r14
        L_0x00c7:
            int r13 = r12 + 1
            char r12 = r1.charAt(r12)
            if (r12 < r7) goto L_0x00e6
            r12 = r12 & 8191(0x1fff, float:1.1478E-41)
            r14 = 13
        L_0x00d3:
            int r15 = r13 + 1
            char r13 = r1.charAt(r13)
            if (r13 < r7) goto L_0x00e3
            r13 = r13 & 8191(0x1fff, float:1.1478E-41)
            int r13 = r13 << r14
            r12 = r12 | r13
            int r14 = r14 + 13
            r13 = r15
            goto L_0x00d3
        L_0x00e3:
            int r13 = r13 << r14
            r12 = r12 | r13
            r13 = r15
        L_0x00e6:
            int r14 = r13 + 1
            char r13 = r1.charAt(r13)
            if (r13 < r7) goto L_0x0107
            r13 = r13 & 8191(0x1fff, float:1.1478E-41)
            r15 = 13
        L_0x00f2:
            int r16 = r14 + 1
            char r14 = r1.charAt(r14)
            if (r14 < r7) goto L_0x0103
            r14 = r14 & 8191(0x1fff, float:1.1478E-41)
            int r14 = r14 << r15
            r13 = r13 | r14
            int r15 = r15 + 13
            r14 = r16
            goto L_0x00f2
        L_0x0103:
            int r14 = r14 << r15
            r13 = r13 | r14
            r14 = r16
        L_0x0107:
            int r15 = r14 + 1
            char r14 = r1.charAt(r14)
            if (r14 < r7) goto L_0x012a
            r14 = r14 & 8191(0x1fff, float:1.1478E-41)
            r16 = 13
        L_0x0113:
            int r17 = r15 + 1
            char r15 = r1.charAt(r15)
            if (r15 < r7) goto L_0x0125
            r15 = r15 & 8191(0x1fff, float:1.1478E-41)
            int r15 = r15 << r16
            r14 = r14 | r15
            int r16 = r16 + 13
            r15 = r17
            goto L_0x0113
        L_0x0125:
            int r15 = r15 << r16
            r14 = r14 | r15
            r15 = r17
        L_0x012a:
            int r16 = r15 + 1
            char r15 = r1.charAt(r15)
            if (r15 < r7) goto L_0x0150
            r15 = r15 & 8191(0x1fff, float:1.1478E-41)
            r3 = r16
            r16 = 13
        L_0x0138:
            int r17 = r3 + 1
            char r3 = r1.charAt(r3)
            if (r3 < r7) goto L_0x014a
            r3 = r3 & 8191(0x1fff, float:1.1478E-41)
            int r3 = r3 << r16
            r15 = r15 | r3
            int r16 = r16 + 13
            r3 = r17
            goto L_0x0138
        L_0x014a:
            int r3 = r3 << r16
            r15 = r15 | r3
            r3 = r17
            goto L_0x0152
        L_0x0150:
            r3 = r16
        L_0x0152:
            int r16 = r3 + 1
            char r3 = r1.charAt(r3)
            if (r3 < r7) goto L_0x0177
            r3 = r3 & 8191(0x1fff, float:1.1478E-41)
            r6 = r16
            r16 = 13
        L_0x0160:
            int r17 = r6 + 1
            char r6 = r1.charAt(r6)
            if (r6 < r7) goto L_0x0172
            r6 = r6 & 8191(0x1fff, float:1.1478E-41)
            int r6 = r6 << r16
            r3 = r3 | r6
            int r16 = r16 + 13
            r6 = r17
            goto L_0x0160
        L_0x0172:
            int r6 = r6 << r16
            r3 = r3 | r6
            r16 = r17
        L_0x0177:
            int r6 = r3 + r14
            int r6 = r6 + r15
            int[] r6 = new int[r6]
            int r15 = r8 << 1
            int r15 = r15 + r9
            r9 = r12
            r12 = r15
            r15 = r3
            r3 = r8
            r8 = r16
            r32 = r14
            r14 = r6
            r6 = r32
        L_0x018a:
            sun.misc.Unsafe r7 = zznd
            java.lang.Object[] r17 = r0.zzdf()
            com.google.android.gms.internal.vision.zzdx r18 = r0.zzcx()
            java.lang.Class r4 = r18.getClass()
            r18 = r8
            int r8 = r13 * 3
            int[] r8 = new int[r8]
            r19 = 1
            int r13 = r13 << 1
            java.lang.Object[] r13 = new java.lang.Object[r13]
            int r20 = r15 + r6
            r23 = r15
            r6 = r18
            r22 = r20
            r18 = 0
            r21 = 0
        L_0x01b0:
            if (r6 >= r2) goto L_0x044c
            int r24 = r6 + 1
            char r6 = r1.charAt(r6)
            r25 = r2
            r2 = 55296(0xd800, float:7.7486E-41)
            if (r6 < r2) goto L_0x01e4
            r6 = r6 & 8191(0x1fff, float:1.1478E-41)
            r2 = r24
            r24 = 13
        L_0x01c5:
            int r26 = r2 + 1
            char r2 = r1.charAt(r2)
            r27 = r15
            r15 = 55296(0xd800, float:7.7486E-41)
            if (r2 < r15) goto L_0x01de
            r2 = r2 & 8191(0x1fff, float:1.1478E-41)
            int r2 = r2 << r24
            r6 = r6 | r2
            int r24 = r24 + 13
            r2 = r26
            r15 = r27
            goto L_0x01c5
        L_0x01de:
            int r2 = r2 << r24
            r6 = r6 | r2
            r2 = r26
            goto L_0x01e8
        L_0x01e4:
            r27 = r15
            r2 = r24
        L_0x01e8:
            int r15 = r2 + 1
            char r2 = r1.charAt(r2)
            r24 = r15
            r15 = 55296(0xd800, float:7.7486E-41)
            if (r2 < r15) goto L_0x021a
            r2 = r2 & 8191(0x1fff, float:1.1478E-41)
            r15 = r24
            r24 = 13
        L_0x01fb:
            int r26 = r15 + 1
            char r15 = r1.charAt(r15)
            r28 = r11
            r11 = 55296(0xd800, float:7.7486E-41)
            if (r15 < r11) goto L_0x0214
            r11 = r15 & 8191(0x1fff, float:1.1478E-41)
            int r11 = r11 << r24
            r2 = r2 | r11
            int r24 = r24 + 13
            r15 = r26
            r11 = r28
            goto L_0x01fb
        L_0x0214:
            int r11 = r15 << r24
            r2 = r2 | r11
            r15 = r26
            goto L_0x021e
        L_0x021a:
            r28 = r11
            r15 = r24
        L_0x021e:
            r11 = r2 & 255(0xff, float:3.57E-43)
            r24 = r9
            r9 = r2 & 1024(0x400, float:1.435E-42)
            if (r9 == 0) goto L_0x022c
            int r9 = r18 + 1
            r14[r18] = r21
            r18 = r9
        L_0x022c:
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.MAP
            int r9 = r9.id()
            if (r11 <= r9) goto L_0x02f6
            int r9 = r15 + 1
            char r15 = r1.charAt(r15)
            r26 = r9
            r9 = 55296(0xd800, float:7.7486E-41)
            if (r15 < r9) goto L_0x0268
            r15 = r15 & 8191(0x1fff, float:1.1478E-41)
            r29 = 13
            r32 = r26
            r26 = r15
            r15 = r32
        L_0x024b:
            int r30 = r15 + 1
            char r15 = r1.charAt(r15)
            if (r15 < r9) goto L_0x0261
            r9 = r15 & 8191(0x1fff, float:1.1478E-41)
            int r9 = r9 << r29
            r26 = r26 | r9
            int r29 = r29 + 13
            r15 = r30
            r9 = 55296(0xd800, float:7.7486E-41)
            goto L_0x024b
        L_0x0261:
            int r9 = r15 << r29
            r15 = r26 | r9
            r9 = r30
            goto L_0x026a
        L_0x0268:
            r9 = r26
        L_0x026a:
            com.google.android.gms.internal.vision.zzcm r26 = com.google.android.gms.internal.vision.zzcm.MESSAGE
            int r26 = r26.id()
            r29 = r9
            int r9 = r26 + 51
            if (r11 == r9) goto L_0x02a2
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.GROUP
            int r9 = r9.id()
            int r9 = r9 + 51
            if (r11 != r9) goto L_0x0281
            goto L_0x02a2
        L_0x0281:
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.ENUM
            int r9 = r9.id()
            int r9 = r9 + 51
            if (r11 != r9) goto L_0x029e
            r9 = r5 & 1
            r26 = r10
            r10 = 1
            if (r9 != r10) goto L_0x02a0
            int r9 = r21 / 3
            int r9 = r9 << r10
            int r9 = r9 + r10
            int r10 = r12 + 1
            r12 = r17[r12]
            r13[r9] = r12
            r12 = r10
            goto L_0x02a0
        L_0x029e:
            r26 = r10
        L_0x02a0:
            r10 = 1
            goto L_0x02b1
        L_0x02a2:
            r26 = r10
            int r9 = r21 / 3
            r10 = 1
            int r9 = r9 << r10
            int r9 = r9 + r10
            int r19 = r12 + 1
            r12 = r17[r12]
            r13[r9] = r12
            r12 = r19
        L_0x02b1:
            int r9 = r15 << 1
            r10 = r17[r9]
            boolean r15 = r10 instanceof java.lang.reflect.Field
            if (r15 == 0) goto L_0x02bc
            java.lang.reflect.Field r10 = (java.lang.reflect.Field) r10
            goto L_0x02c4
        L_0x02bc:
            java.lang.String r10 = (java.lang.String) r10
            java.lang.reflect.Field r10 = zza((java.lang.Class<?>) r4, (java.lang.String) r10)
            r17[r9] = r10
        L_0x02c4:
            r30 = r0
            r31 = r1
            long r0 = r7.objectFieldOffset(r10)
            int r1 = (int) r0
            int r9 = r9 + 1
            r0 = r17[r9]
            boolean r10 = r0 instanceof java.lang.reflect.Field
            if (r10 == 0) goto L_0x02d8
            java.lang.reflect.Field r0 = (java.lang.reflect.Field) r0
            goto L_0x02e0
        L_0x02d8:
            java.lang.String r0 = (java.lang.String) r0
            java.lang.reflect.Field r0 = zza((java.lang.Class<?>) r4, (java.lang.String) r0)
            r17[r9] = r0
        L_0x02e0:
            long r9 = r7.objectFieldOffset(r0)
            int r0 = (int) r9
            r19 = r1
            r9 = r11
            r10 = r29
            r11 = r31
            r16 = 1
            r1 = r0
            r31 = r12
            r0 = 55296(0xd800, float:7.7486E-41)
            goto L_0x0406
        L_0x02f6:
            r30 = r0
            r31 = r1
            r26 = r10
            int r0 = r12 + 1
            r1 = r17[r12]
            java.lang.String r1 = (java.lang.String) r1
            java.lang.reflect.Field r1 = zza((java.lang.Class<?>) r4, (java.lang.String) r1)
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.MESSAGE
            int r9 = r9.id()
            if (r11 == r9) goto L_0x038a
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.GROUP
            int r9 = r9.id()
            if (r11 != r9) goto L_0x0318
            goto L_0x038a
        L_0x0318:
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.MESSAGE_LIST
            int r9 = r9.id()
            if (r11 == r9) goto L_0x037c
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.GROUP_LIST
            int r9 = r9.id()
            if (r11 != r9) goto L_0x0329
            goto L_0x037c
        L_0x0329:
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.ENUM
            int r9 = r9.id()
            if (r11 == r9) goto L_0x036c
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.ENUM_LIST
            int r9 = r9.id()
            if (r11 == r9) goto L_0x036c
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.ENUM_LIST_PACKED
            int r9 = r9.id()
            if (r11 != r9) goto L_0x0342
            goto L_0x036c
        L_0x0342:
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.MAP
            int r9 = r9.id()
            if (r11 != r9) goto L_0x036a
            int r9 = r23 + 1
            r14[r23] = r21
            int r10 = r21 / 3
            r12 = 1
            int r10 = r10 << r12
            int r12 = r0 + 1
            r0 = r17[r0]
            r13[r10] = r0
            r0 = r2 & 2048(0x800, float:2.87E-42)
            if (r0 == 0) goto L_0x0367
            int r10 = r10 + 1
            int r0 = r12 + 1
            r12 = r17[r12]
            r13[r10] = r12
            r23 = r9
            goto L_0x0395
        L_0x0367:
            r23 = r9
            goto L_0x0387
        L_0x036a:
            r10 = 1
            goto L_0x0395
        L_0x036c:
            r9 = r5 & 1
            r10 = 1
            if (r9 != r10) goto L_0x0395
            int r9 = r21 / 3
            int r9 = r9 << r10
            int r9 = r9 + r10
            int r12 = r0 + 1
            r0 = r17[r0]
            r13[r9] = r0
            goto L_0x0387
        L_0x037c:
            r10 = 1
            int r9 = r21 / 3
            int r9 = r9 << r10
            int r9 = r9 + r10
            int r12 = r0 + 1
            r0 = r17[r0]
            r13[r9] = r0
        L_0x0387:
            r9 = r11
            r0 = r12
            goto L_0x0396
        L_0x038a:
            r10 = 1
            int r9 = r21 / 3
            int r9 = r9 << r10
            int r9 = r9 + r10
            java.lang.Class r12 = r1.getType()
            r13[r9] = r12
        L_0x0395:
            r9 = r11
        L_0x0396:
            long r10 = r7.objectFieldOffset(r1)
            int r1 = (int) r10
            r10 = r5 & 1
            r11 = 1
            if (r10 != r11) goto L_0x03f9
            com.google.android.gms.internal.vision.zzcm r10 = com.google.android.gms.internal.vision.zzcm.GROUP
            int r10 = r10.id()
            if (r9 > r10) goto L_0x03f9
            int r10 = r15 + 1
            r11 = r31
            char r12 = r11.charAt(r15)
            r15 = 55296(0xd800, float:7.7486E-41)
            if (r12 < r15) goto L_0x03d0
            r12 = r12 & 8191(0x1fff, float:1.1478E-41)
            r16 = 13
        L_0x03b9:
            int r29 = r10 + 1
            char r10 = r11.charAt(r10)
            if (r10 < r15) goto L_0x03cb
            r10 = r10 & 8191(0x1fff, float:1.1478E-41)
            int r10 = r10 << r16
            r12 = r12 | r10
            int r16 = r16 + 13
            r10 = r29
            goto L_0x03b9
        L_0x03cb:
            int r10 = r10 << r16
            r12 = r12 | r10
            r10 = r29
        L_0x03d0:
            r16 = 1
            int r19 = r3 << 1
            int r29 = r12 / 32
            int r19 = r19 + r29
            r15 = r17[r19]
            r31 = r0
            boolean r0 = r15 instanceof java.lang.reflect.Field
            if (r0 == 0) goto L_0x03e3
            java.lang.reflect.Field r15 = (java.lang.reflect.Field) r15
            goto L_0x03eb
        L_0x03e3:
            java.lang.String r15 = (java.lang.String) r15
            java.lang.reflect.Field r15 = zza((java.lang.Class<?>) r4, (java.lang.String) r15)
            r17[r19] = r15
        L_0x03eb:
            r19 = r1
            long r0 = r7.objectFieldOffset(r15)
            int r0 = (int) r0
            int r12 = r12 % 32
            r1 = r0
            r0 = 55296(0xd800, float:7.7486E-41)
            goto L_0x0407
        L_0x03f9:
            r19 = r1
            r11 = r31
            r16 = 1
            r31 = r0
            r0 = 55296(0xd800, float:7.7486E-41)
            r10 = r15
            r1 = 0
        L_0x0406:
            r12 = 0
        L_0x0407:
            r15 = 18
            if (r9 < r15) goto L_0x0415
            r15 = 49
            if (r9 > r15) goto L_0x0415
            int r15 = r22 + 1
            r14[r22] = r19
            r22 = r15
        L_0x0415:
            int r15 = r21 + 1
            r8[r21] = r6
            int r6 = r15 + 1
            r0 = r2 & 512(0x200, float:7.175E-43)
            if (r0 == 0) goto L_0x0422
            r0 = 536870912(0x20000000, float:1.0842022E-19)
            goto L_0x0423
        L_0x0422:
            r0 = 0
        L_0x0423:
            r2 = r2 & 256(0x100, float:3.59E-43)
            if (r2 == 0) goto L_0x042a
            r2 = 268435456(0x10000000, float:2.5243549E-29)
            goto L_0x042b
        L_0x042a:
            r2 = 0
        L_0x042b:
            r0 = r0 | r2
            int r2 = r9 << 20
            r0 = r0 | r2
            r0 = r0 | r19
            r8[r15] = r0
            int r21 = r6 + 1
            int r0 = r12 << 20
            r0 = r0 | r1
            r8[r6] = r0
            r6 = r10
            r1 = r11
            r9 = r24
            r2 = r25
            r10 = r26
            r15 = r27
            r11 = r28
            r0 = r30
            r12 = r31
            goto L_0x01b0
        L_0x044c:
            r30 = r0
            r24 = r9
            r26 = r10
            r28 = r11
            r27 = r15
            com.google.android.gms.internal.vision.zzeb r0 = new com.google.android.gms.internal.vision.zzeb
            com.google.android.gms.internal.vision.zzdx r10 = r30.zzcx()
            r12 = 0
            r5 = r0
            r6 = r8
            r7 = r13
            r8 = r26
            r13 = r14
            r14 = r27
            r15 = r20
            r16 = r35
            r17 = r36
            r18 = r37
            r19 = r38
            r20 = r39
            r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            return r0
        L_0x0475:
            com.google.android.gms.internal.vision.zzfa r0 = (com.google.android.gms.internal.vision.zzfa) r0
            r0.zzcv()
            java.lang.NoSuchMethodError r0 = new java.lang.NoSuchMethodError
            r0.<init>()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zza(java.lang.Class, com.google.android.gms.internal.vision.zzdv, com.google.android.gms.internal.vision.zzef, com.google.android.gms.internal.vision.zzdh, com.google.android.gms.internal.vision.zzff, com.google.android.gms.internal.vision.zzcg, com.google.android.gms.internal.vision.zzds):com.google.android.gms.internal.vision.zzeb");
    }

    private final <K, V, UT, UB> UB zza(int i, int i2, Map<K, V> map, zzcv<?> zzcv, UB ub, zzff<UT, UB> zzff) {
        zzdq<?, ?> zzm = this.zznu.zzm(zzah(i));
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry next = it.next();
            if (zzcv.zzaf(((Integer) next.getValue()).intValue()) == null) {
                if (ub == null) {
                    ub = zzff.zzdt();
                }
                zzbt zzm2 = zzbo.zzm(zzdp.zza(zzm, next.getKey(), next.getValue()));
                try {
                    zzdp.zza(zzm2.zzax(), zzm, next.getKey(), next.getValue());
                    zzff.zza(ub, i2, zzm2.zzaw());
                    it.remove();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return ub;
    }

    private static Field zza(Class<?> cls, String str) {
        try {
            return cls.getDeclaredField(str);
        } catch (NoSuchFieldException e) {
            Field[] declaredFields = cls.getDeclaredFields();
            for (Field field : declaredFields) {
                if (str.equals(field.getName())) {
                    return field;
                }
            }
            String name = cls.getName();
            String arrays = Arrays.toString(declaredFields);
            StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 40 + String.valueOf(name).length() + String.valueOf(arrays).length());
            sb.append("Field ");
            sb.append(str);
            sb.append(" for ");
            sb.append(name);
            sb.append(" not found. Known fields are ");
            sb.append(arrays);
            throw new RuntimeException(sb.toString());
        }
    }

    private static void zza(int i, Object obj, zzfz zzfz) throws IOException {
        if (obj instanceof String) {
            zzfz.zza(i, (String) obj);
        } else {
            zzfz.zza(i, (zzbo) obj);
        }
    }

    private static <UT, UB> void zza(zzff<UT, UB> zzff, T t, zzfz zzfz) throws IOException {
        zzff.zza(zzff.zzr(t), zzfz);
    }

    private final <K, V> void zza(zzfz zzfz, int i, Object obj, int i2) throws IOException {
        if (obj != null) {
            zzfz.zza(i, this.zznu.zzm(zzah(i2)), this.zznu.zzi(obj));
        }
    }

    private final void zza(T t, T t2, int i) {
        long zzaj = (long) (zzaj(i) & 1048575);
        if (zza(t2, i)) {
            Object zzo = zzfl.zzo(t, zzaj);
            Object zzo2 = zzfl.zzo(t2, zzaj);
            if (zzo != null && zzo2 != null) {
                zzfl.zza((Object) t, zzaj, zzct.zza(zzo, zzo2));
                zzb(t, i);
            } else if (zzo2 != null) {
                zzfl.zza((Object) t, zzaj, zzo2);
                zzb(t, i);
            }
        }
    }

    private final boolean zza(T t, int i) {
        if (this.zznl) {
            int zzaj = zzaj(i);
            long j = (long) (zzaj & 1048575);
            switch ((zzaj & 267386880) >>> 20) {
                case 0:
                    return zzfl.zzn(t, j) != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
                case 1:
                    return zzfl.zzm(t, j) != 0.0f;
                case 2:
                    return zzfl.zzk(t, j) != 0;
                case 3:
                    return zzfl.zzk(t, j) != 0;
                case 4:
                    return zzfl.zzj(t, j) != 0;
                case 5:
                    return zzfl.zzk(t, j) != 0;
                case 6:
                    return zzfl.zzj(t, j) != 0;
                case 7:
                    return zzfl.zzl(t, j);
                case 8:
                    Object zzo = zzfl.zzo(t, j);
                    if (zzo instanceof String) {
                        return !((String) zzo).isEmpty();
                    }
                    if (zzo instanceof zzbo) {
                        return !zzbo.zzgt.equals(zzo);
                    }
                    throw new IllegalArgumentException();
                case 9:
                    return zzfl.zzo(t, j) != null;
                case 10:
                    return !zzbo.zzgt.equals(zzfl.zzo(t, j));
                case 11:
                    return zzfl.zzj(t, j) != 0;
                case 12:
                    return zzfl.zzj(t, j) != 0;
                case 13:
                    return zzfl.zzj(t, j) != 0;
                case 14:
                    return zzfl.zzk(t, j) != 0;
                case 15:
                    return zzfl.zzj(t, j) != 0;
                case 16:
                    return zzfl.zzk(t, j) != 0;
                case 17:
                    return zzfl.zzo(t, j) != null;
                default:
                    throw new IllegalArgumentException();
            }
        } else {
            int zzak = zzak(i);
            return (zzfl.zzj(t, (long) (zzak & 1048575)) & (1 << (zzak >>> 20))) != 0;
        }
    }

    private final boolean zza(T t, int i, int i2) {
        return zzfl.zzj(t, (long) (zzak(i2) & 1048575)) == i;
    }

    private final boolean zza(T t, int i, int i2, int i3) {
        return this.zznl ? zza(t, i) : (i2 & i3) != 0;
    }

    private static boolean zza(Object obj, int i, zzen zzen) {
        return zzen.zzp(zzfl.zzo(obj, (long) (i & 1048575)));
    }

    private final zzen zzag(int i) {
        int i2 = (i / 3) << 1;
        zzen zzen = (zzen) this.zznf[i2];
        if (zzen != null) {
            return zzen;
        }
        zzen zze = zzek.zzdc().zze((Class) this.zznf[i2 + 1]);
        this.zznf[i2] = zze;
        return zze;
    }

    private final Object zzah(int i) {
        return this.zznf[(i / 3) << 1];
    }

    private final zzcv<?> zzai(int i) {
        return (zzcv) this.zznf[((i / 3) << 1) + 1];
    }

    private final int zzaj(int i) {
        return this.zzne[i + 1];
    }

    private final int zzak(int i) {
        return this.zzne[i + 2];
    }

    private final int zzal(int i) {
        if (i < this.zzng || i > this.zznh) {
            return -1;
        }
        return zzs(i, 0);
    }

    private final void zzb(T t, int i) {
        if (!this.zznl) {
            int zzak = zzak(i);
            long j = (long) (zzak & 1048575);
            zzfl.zza((Object) t, j, zzfl.zzj(t, j) | (1 << (zzak >>> 20)));
        }
    }

    private final void zzb(T t, int i, int i2) {
        zzfl.zza((Object) t, (long) (zzak(i2) & 1048575), i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:170:0x0494  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x002d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void zzb(T r19, com.google.android.gms.internal.vision.zzfz r20) throws java.io.IOException {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            boolean r3 = r0.zznj
            if (r3 == 0) goto L_0x0021
            com.google.android.gms.internal.vision.zzcg<?> r3 = r0.zznt
            com.google.android.gms.internal.vision.zzcj r3 = r3.zzb(r1)
            boolean r5 = r3.isEmpty()
            if (r5 != 0) goto L_0x0021
            java.util.Iterator r3 = r3.iterator()
            java.lang.Object r5 = r3.next()
            java.util.Map$Entry r5 = (java.util.Map.Entry) r5
            goto L_0x0023
        L_0x0021:
            r3 = 0
            r5 = 0
        L_0x0023:
            r6 = -1
            int[] r7 = r0.zzne
            int r7 = r7.length
            sun.misc.Unsafe r8 = zznd
            r10 = 0
            r11 = 0
        L_0x002b:
            if (r10 >= r7) goto L_0x0492
            int r12 = r0.zzaj(r10)
            int[] r13 = r0.zzne
            r14 = r13[r10]
            r15 = 267386880(0xff00000, float:2.3665827E-29)
            r15 = r15 & r12
            int r15 = r15 >>> 20
            boolean r4 = r0.zznl
            r16 = 1048575(0xfffff, float:1.469367E-39)
            if (r4 != 0) goto L_0x005b
            r4 = 17
            if (r15 > r4) goto L_0x005b
            int r4 = r10 + 2
            r4 = r13[r4]
            r13 = r4 & r16
            r17 = r10
            if (r13 == r6) goto L_0x0055
            long r9 = (long) r13
            int r11 = r8.getInt(r1, r9)
            r6 = r13
        L_0x0055:
            int r4 = r4 >>> 20
            r9 = 1
            int r4 = r9 << r4
            goto L_0x005e
        L_0x005b:
            r17 = r10
            r4 = 0
        L_0x005e:
            if (r5 == 0) goto L_0x007c
            com.google.android.gms.internal.vision.zzcg<?> r9 = r0.zznt
            int r9 = r9.zza(r5)
            if (r9 > r14) goto L_0x007c
            com.google.android.gms.internal.vision.zzcg<?> r9 = r0.zznt
            r9.zza((com.google.android.gms.internal.vision.zzfz) r2, (java.util.Map.Entry<?, ?>) r5)
            boolean r5 = r3.hasNext()
            if (r5 == 0) goto L_0x007a
            java.lang.Object r5 = r3.next()
            java.util.Map$Entry r5 = (java.util.Map.Entry) r5
            goto L_0x005e
        L_0x007a:
            r5 = 0
            goto L_0x005e
        L_0x007c:
            r9 = r12 & r16
            long r9 = (long) r9
            r12 = r17
            switch(r15) {
                case 0: goto L_0x0483;
                case 1: goto L_0x0477;
                case 2: goto L_0x046b;
                case 3: goto L_0x045f;
                case 4: goto L_0x0453;
                case 5: goto L_0x0447;
                case 6: goto L_0x043b;
                case 7: goto L_0x042f;
                case 8: goto L_0x0423;
                case 9: goto L_0x0412;
                case 10: goto L_0x0403;
                case 11: goto L_0x03f6;
                case 12: goto L_0x03e9;
                case 13: goto L_0x03dc;
                case 14: goto L_0x03cf;
                case 15: goto L_0x03c2;
                case 16: goto L_0x03b5;
                case 17: goto L_0x03a4;
                case 18: goto L_0x0394;
                case 19: goto L_0x0384;
                case 20: goto L_0x0374;
                case 21: goto L_0x0364;
                case 22: goto L_0x0354;
                case 23: goto L_0x0344;
                case 24: goto L_0x0334;
                case 25: goto L_0x0324;
                case 26: goto L_0x0315;
                case 27: goto L_0x0302;
                case 28: goto L_0x02f3;
                case 29: goto L_0x02e3;
                case 30: goto L_0x02d3;
                case 31: goto L_0x02c3;
                case 32: goto L_0x02b3;
                case 33: goto L_0x02a3;
                case 34: goto L_0x0293;
                case 35: goto L_0x0283;
                case 36: goto L_0x0273;
                case 37: goto L_0x0263;
                case 38: goto L_0x0253;
                case 39: goto L_0x0243;
                case 40: goto L_0x0233;
                case 41: goto L_0x0223;
                case 42: goto L_0x0213;
                case 43: goto L_0x0203;
                case 44: goto L_0x01f3;
                case 45: goto L_0x01e3;
                case 46: goto L_0x01d3;
                case 47: goto L_0x01c3;
                case 48: goto L_0x01b3;
                case 49: goto L_0x01a0;
                case 50: goto L_0x0197;
                case 51: goto L_0x0188;
                case 52: goto L_0x0179;
                case 53: goto L_0x016a;
                case 54: goto L_0x015b;
                case 55: goto L_0x014c;
                case 56: goto L_0x013d;
                case 57: goto L_0x012e;
                case 58: goto L_0x011f;
                case 59: goto L_0x0110;
                case 60: goto L_0x00fd;
                case 61: goto L_0x00ed;
                case 62: goto L_0x00df;
                case 63: goto L_0x00d1;
                case 64: goto L_0x00c3;
                case 65: goto L_0x00b5;
                case 66: goto L_0x00a7;
                case 67: goto L_0x0099;
                case 68: goto L_0x0087;
                default: goto L_0x0084;
            }
        L_0x0084:
            r13 = 0
            goto L_0x048e
        L_0x0087:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            java.lang.Object r4 = r8.getObject(r1, r9)
            com.google.android.gms.internal.vision.zzen r9 = r0.zzag(r12)
            r2.zzb((int) r14, (java.lang.Object) r4, (com.google.android.gms.internal.vision.zzen) r9)
            goto L_0x0084
        L_0x0099:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            long r9 = zzh(r1, r9)
            r2.zzb((int) r14, (long) r9)
            goto L_0x0084
        L_0x00a7:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            int r4 = zzg(r1, r9)
            r2.zzg(r14, r4)
            goto L_0x0084
        L_0x00b5:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            long r9 = zzh(r1, r9)
            r2.zzj(r14, r9)
            goto L_0x0084
        L_0x00c3:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            int r4 = zzg(r1, r9)
            r2.zzo(r14, r4)
            goto L_0x0084
        L_0x00d1:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            int r4 = zzg(r1, r9)
            r2.zzp(r14, r4)
            goto L_0x0084
        L_0x00df:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            int r4 = zzg(r1, r9)
            r2.zzf(r14, r4)
            goto L_0x0084
        L_0x00ed:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            java.lang.Object r4 = r8.getObject(r1, r9)
            com.google.android.gms.internal.vision.zzbo r4 = (com.google.android.gms.internal.vision.zzbo) r4
            r2.zza((int) r14, (com.google.android.gms.internal.vision.zzbo) r4)
            goto L_0x0084
        L_0x00fd:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            java.lang.Object r4 = r8.getObject(r1, r9)
            com.google.android.gms.internal.vision.zzen r9 = r0.zzag(r12)
            r2.zza((int) r14, (java.lang.Object) r4, (com.google.android.gms.internal.vision.zzen) r9)
            goto L_0x0084
        L_0x0110:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            java.lang.Object r4 = r8.getObject(r1, r9)
            zza((int) r14, (java.lang.Object) r4, (com.google.android.gms.internal.vision.zzfz) r2)
            goto L_0x0084
        L_0x011f:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            boolean r4 = zzi(r1, r9)
            r2.zzb((int) r14, (boolean) r4)
            goto L_0x0084
        L_0x012e:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            int r4 = zzg(r1, r9)
            r2.zzh(r14, r4)
            goto L_0x0084
        L_0x013d:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            long r9 = zzh(r1, r9)
            r2.zzc(r14, r9)
            goto L_0x0084
        L_0x014c:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            int r4 = zzg(r1, r9)
            r2.zze(r14, r4)
            goto L_0x0084
        L_0x015b:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            long r9 = zzh(r1, r9)
            r2.zza((int) r14, (long) r9)
            goto L_0x0084
        L_0x016a:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            long r9 = zzh(r1, r9)
            r2.zzi(r14, r9)
            goto L_0x0084
        L_0x0179:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            float r4 = zzf(r1, r9)
            r2.zza((int) r14, (float) r4)
            goto L_0x0084
        L_0x0188:
            boolean r4 = r0.zza(r1, (int) r14, (int) r12)
            if (r4 == 0) goto L_0x0084
            double r9 = zze(r1, r9)
            r2.zza((int) r14, (double) r9)
            goto L_0x0084
        L_0x0197:
            java.lang.Object r4 = r8.getObject(r1, r9)
            r0.zza((com.google.android.gms.internal.vision.zzfz) r2, (int) r14, (java.lang.Object) r4, (int) r12)
            goto L_0x0084
        L_0x01a0:
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzen r10 = r0.zzag(r12)
            com.google.android.gms.internal.vision.zzep.zzb((int) r4, (java.util.List<?>) r9, (com.google.android.gms.internal.vision.zzfz) r2, (com.google.android.gms.internal.vision.zzen) r10)
            goto L_0x0084
        L_0x01b3:
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            r13 = 1
            com.google.android.gms.internal.vision.zzep.zze(r4, r9, r2, r13)
            goto L_0x0084
        L_0x01c3:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzj(r4, r9, r2, r13)
            goto L_0x0084
        L_0x01d3:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzg(r4, r9, r2, r13)
            goto L_0x0084
        L_0x01e3:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzl(r4, r9, r2, r13)
            goto L_0x0084
        L_0x01f3:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzm(r4, r9, r2, r13)
            goto L_0x0084
        L_0x0203:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzi(r4, r9, r2, r13)
            goto L_0x0084
        L_0x0213:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzn(r4, r9, r2, r13)
            goto L_0x0084
        L_0x0223:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzk(r4, r9, r2, r13)
            goto L_0x0084
        L_0x0233:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzf(r4, r9, r2, r13)
            goto L_0x0084
        L_0x0243:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzh(r4, r9, r2, r13)
            goto L_0x0084
        L_0x0253:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzd(r4, r9, r2, r13)
            goto L_0x0084
        L_0x0263:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzc(r4, r9, r2, r13)
            goto L_0x0084
        L_0x0273:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzb((int) r4, (java.util.List<java.lang.Float>) r9, (com.google.android.gms.internal.vision.zzfz) r2, (boolean) r13)
            goto L_0x0084
        L_0x0283:
            r13 = 1
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zza((int) r4, (java.util.List<java.lang.Double>) r9, (com.google.android.gms.internal.vision.zzfz) r2, (boolean) r13)
            goto L_0x0084
        L_0x0293:
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            r13 = 0
            com.google.android.gms.internal.vision.zzep.zze(r4, r9, r2, r13)
            goto L_0x048e
        L_0x02a3:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzj(r4, r9, r2, r13)
            goto L_0x048e
        L_0x02b3:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzg(r4, r9, r2, r13)
            goto L_0x048e
        L_0x02c3:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzl(r4, r9, r2, r13)
            goto L_0x048e
        L_0x02d3:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzm(r4, r9, r2, r13)
            goto L_0x048e
        L_0x02e3:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzi(r4, r9, r2, r13)
            goto L_0x048e
        L_0x02f3:
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzb(r4, r9, r2)
            goto L_0x0084
        L_0x0302:
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzen r10 = r0.zzag(r12)
            com.google.android.gms.internal.vision.zzep.zza((int) r4, (java.util.List<?>) r9, (com.google.android.gms.internal.vision.zzfz) r2, (com.google.android.gms.internal.vision.zzen) r10)
            goto L_0x0084
        L_0x0315:
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zza((int) r4, (java.util.List<java.lang.String>) r9, (com.google.android.gms.internal.vision.zzfz) r2)
            goto L_0x0084
        L_0x0324:
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            r13 = 0
            com.google.android.gms.internal.vision.zzep.zzn(r4, r9, r2, r13)
            goto L_0x048e
        L_0x0334:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzk(r4, r9, r2, r13)
            goto L_0x048e
        L_0x0344:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzf(r4, r9, r2, r13)
            goto L_0x048e
        L_0x0354:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzh(r4, r9, r2, r13)
            goto L_0x048e
        L_0x0364:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzd(r4, r9, r2, r13)
            goto L_0x048e
        L_0x0374:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzc(r4, r9, r2, r13)
            goto L_0x048e
        L_0x0384:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzb((int) r4, (java.util.List<java.lang.Float>) r9, (com.google.android.gms.internal.vision.zzfz) r2, (boolean) r13)
            goto L_0x048e
        L_0x0394:
            r13 = 0
            int[] r4 = r0.zzne
            r4 = r4[r12]
            java.lang.Object r9 = r8.getObject(r1, r9)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zza((int) r4, (java.util.List<java.lang.Double>) r9, (com.google.android.gms.internal.vision.zzfz) r2, (boolean) r13)
            goto L_0x048e
        L_0x03a4:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            java.lang.Object r4 = r8.getObject(r1, r9)
            com.google.android.gms.internal.vision.zzen r9 = r0.zzag(r12)
            r2.zzb((int) r14, (java.lang.Object) r4, (com.google.android.gms.internal.vision.zzen) r9)
            goto L_0x048e
        L_0x03b5:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            long r9 = r8.getLong(r1, r9)
            r2.zzb((int) r14, (long) r9)
            goto L_0x048e
        L_0x03c2:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            int r4 = r8.getInt(r1, r9)
            r2.zzg(r14, r4)
            goto L_0x048e
        L_0x03cf:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            long r9 = r8.getLong(r1, r9)
            r2.zzj(r14, r9)
            goto L_0x048e
        L_0x03dc:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            int r4 = r8.getInt(r1, r9)
            r2.zzo(r14, r4)
            goto L_0x048e
        L_0x03e9:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            int r4 = r8.getInt(r1, r9)
            r2.zzp(r14, r4)
            goto L_0x048e
        L_0x03f6:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            int r4 = r8.getInt(r1, r9)
            r2.zzf(r14, r4)
            goto L_0x048e
        L_0x0403:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            java.lang.Object r4 = r8.getObject(r1, r9)
            com.google.android.gms.internal.vision.zzbo r4 = (com.google.android.gms.internal.vision.zzbo) r4
            r2.zza((int) r14, (com.google.android.gms.internal.vision.zzbo) r4)
            goto L_0x048e
        L_0x0412:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            java.lang.Object r4 = r8.getObject(r1, r9)
            com.google.android.gms.internal.vision.zzen r9 = r0.zzag(r12)
            r2.zza((int) r14, (java.lang.Object) r4, (com.google.android.gms.internal.vision.zzen) r9)
            goto L_0x048e
        L_0x0423:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            java.lang.Object r4 = r8.getObject(r1, r9)
            zza((int) r14, (java.lang.Object) r4, (com.google.android.gms.internal.vision.zzfz) r2)
            goto L_0x048e
        L_0x042f:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            boolean r4 = com.google.android.gms.internal.vision.zzfl.zzl(r1, r9)
            r2.zzb((int) r14, (boolean) r4)
            goto L_0x048e
        L_0x043b:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            int r4 = r8.getInt(r1, r9)
            r2.zzh(r14, r4)
            goto L_0x048e
        L_0x0447:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            long r9 = r8.getLong(r1, r9)
            r2.zzc(r14, r9)
            goto L_0x048e
        L_0x0453:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            int r4 = r8.getInt(r1, r9)
            r2.zze(r14, r4)
            goto L_0x048e
        L_0x045f:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            long r9 = r8.getLong(r1, r9)
            r2.zza((int) r14, (long) r9)
            goto L_0x048e
        L_0x046b:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            long r9 = r8.getLong(r1, r9)
            r2.zzi(r14, r9)
            goto L_0x048e
        L_0x0477:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            float r4 = com.google.android.gms.internal.vision.zzfl.zzm(r1, r9)
            r2.zza((int) r14, (float) r4)
            goto L_0x048e
        L_0x0483:
            r13 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L_0x048e
            double r9 = com.google.android.gms.internal.vision.zzfl.zzn(r1, r9)
            r2.zza((int) r14, (double) r9)
        L_0x048e:
            int r10 = r12 + 3
            goto L_0x002b
        L_0x0492:
            if (r5 == 0) goto L_0x04a9
            com.google.android.gms.internal.vision.zzcg<?> r4 = r0.zznt
            r4.zza((com.google.android.gms.internal.vision.zzfz) r2, (java.util.Map.Entry<?, ?>) r5)
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x04a7
            java.lang.Object r4 = r3.next()
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4
            r5 = r4
            goto L_0x0492
        L_0x04a7:
            r5 = 0
            goto L_0x0492
        L_0x04a9:
            com.google.android.gms.internal.vision.zzff<?, ?> r3 = r0.zzns
            zza(r3, r1, (com.google.android.gms.internal.vision.zzfz) r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zzb(java.lang.Object, com.google.android.gms.internal.vision.zzfz):void");
    }

    private final void zzb(T t, T t2, int i) {
        int zzaj = zzaj(i);
        int i2 = this.zzne[i];
        long j = (long) (zzaj & 1048575);
        if (zza(t2, i2, i)) {
            Object zzo = zzfl.zzo(t, j);
            Object zzo2 = zzfl.zzo(t2, j);
            if (zzo != null && zzo2 != null) {
                zzfl.zza((Object) t, j, zzct.zza(zzo, zzo2));
                zzb(t, i2, i);
            } else if (zzo2 != null) {
                zzfl.zza((Object) t, j, zzo2);
                zzb(t, i2, i);
            }
        }
    }

    private final boolean zzc(T t, T t2, int i) {
        return zza(t, i) == zza(t2, i);
    }

    private static <E> List<E> zzd(Object obj, long j) {
        return (List) zzfl.zzo(obj, j);
    }

    private static <T> double zze(T t, long j) {
        return ((Double) zzfl.zzo(t, j)).doubleValue();
    }

    private static <T> float zzf(T t, long j) {
        return ((Float) zzfl.zzo(t, j)).floatValue();
    }

    private static <T> int zzg(T t, long j) {
        return ((Integer) zzfl.zzo(t, j)).intValue();
    }

    private static <T> long zzh(T t, long j) {
        return ((Long) zzfl.zzo(t, j)).longValue();
    }

    private static <T> boolean zzi(T t, long j) {
        return ((Boolean) zzfl.zzo(t, j)).booleanValue();
    }

    private static zzfg zzo(Object obj) {
        zzcr zzcr = (zzcr) obj;
        zzfg zzfg = zzcr.zzkr;
        if (zzfg != zzfg.zzdu()) {
            return zzfg;
        }
        zzfg zzdv = zzfg.zzdv();
        zzcr.zzkr = zzdv;
        return zzdv;
    }

    private final int zzr(int i, int i2) {
        if (i < this.zzng || i > this.zznh) {
            return -1;
        }
        return zzs(i, i2);
    }

    private final int zzs(int i, int i2) {
        int length = (this.zzne.length / 3) - 1;
        while (i2 <= length) {
            int i3 = (length + i2) >>> 1;
            int i4 = i3 * 3;
            int i5 = this.zzne[i4];
            if (i == i5) {
                return i4;
            }
            if (i < i5) {
                length = i3 - 1;
            } else {
                i2 = i3 + 1;
            }
        }
        return -1;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x005c, code lost:
        if (com.google.android.gms.internal.vision.zzep.zzd(com.google.android.gms.internal.vision.zzfl.zzo(r10, r6), com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)) != false) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0070, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzk(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0082, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzj(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0096, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzk(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a8, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzj(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00ba, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzj(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00cc, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzj(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00e2, code lost:
        if (com.google.android.gms.internal.vision.zzep.zzd(com.google.android.gms.internal.vision.zzfl.zzo(r10, r6), com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)) != false) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00f8, code lost:
        if (com.google.android.gms.internal.vision.zzep.zzd(com.google.android.gms.internal.vision.zzfl.zzo(r10, r6), com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)) != false) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x010e, code lost:
        if (com.google.android.gms.internal.vision.zzep.zzd(com.google.android.gms.internal.vision.zzfl.zzo(r10, r6), com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)) != false) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0120, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzl(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzl(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0132, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzj(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0145, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzk(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0156, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzj(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0169, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzk(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x017c, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzk(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x018d, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzj(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x01a0, code lost:
        if (com.google.android.gms.internal.vision.zzfl.zzk(r10, r6) == com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)) goto L_0x01a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x01a2, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0038, code lost:
        if (com.google.android.gms.internal.vision.zzep.zzd(com.google.android.gms.internal.vision.zzfl.zzo(r10, r6), com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)) != false) goto L_0x01a3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean equals(T r10, T r11) {
        /*
            r9 = this;
            int[] r0 = r9.zzne
            int r0 = r0.length
            r1 = 0
            r2 = 0
        L_0x0005:
            r3 = 1
            if (r2 >= r0) goto L_0x01aa
            int r4 = r9.zzaj(r2)
            r5 = 1048575(0xfffff, float:1.469367E-39)
            r6 = r4 & r5
            long r6 = (long) r6
            r8 = 267386880(0xff00000, float:2.3665827E-29)
            r4 = r4 & r8
            int r4 = r4 >>> 20
            switch(r4) {
                case 0: goto L_0x0190;
                case 1: goto L_0x017f;
                case 2: goto L_0x016c;
                case 3: goto L_0x0159;
                case 4: goto L_0x0148;
                case 5: goto L_0x0135;
                case 6: goto L_0x0124;
                case 7: goto L_0x0112;
                case 8: goto L_0x00fc;
                case 9: goto L_0x00e6;
                case 10: goto L_0x00d0;
                case 11: goto L_0x00be;
                case 12: goto L_0x00ac;
                case 13: goto L_0x009a;
                case 14: goto L_0x0086;
                case 15: goto L_0x0074;
                case 16: goto L_0x0060;
                case 17: goto L_0x004a;
                case 18: goto L_0x003c;
                case 19: goto L_0x003c;
                case 20: goto L_0x003c;
                case 21: goto L_0x003c;
                case 22: goto L_0x003c;
                case 23: goto L_0x003c;
                case 24: goto L_0x003c;
                case 25: goto L_0x003c;
                case 26: goto L_0x003c;
                case 27: goto L_0x003c;
                case 28: goto L_0x003c;
                case 29: goto L_0x003c;
                case 30: goto L_0x003c;
                case 31: goto L_0x003c;
                case 32: goto L_0x003c;
                case 33: goto L_0x003c;
                case 34: goto L_0x003c;
                case 35: goto L_0x003c;
                case 36: goto L_0x003c;
                case 37: goto L_0x003c;
                case 38: goto L_0x003c;
                case 39: goto L_0x003c;
                case 40: goto L_0x003c;
                case 41: goto L_0x003c;
                case 42: goto L_0x003c;
                case 43: goto L_0x003c;
                case 44: goto L_0x003c;
                case 45: goto L_0x003c;
                case 46: goto L_0x003c;
                case 47: goto L_0x003c;
                case 48: goto L_0x003c;
                case 49: goto L_0x003c;
                case 50: goto L_0x003c;
                case 51: goto L_0x001c;
                case 52: goto L_0x001c;
                case 53: goto L_0x001c;
                case 54: goto L_0x001c;
                case 55: goto L_0x001c;
                case 56: goto L_0x001c;
                case 57: goto L_0x001c;
                case 58: goto L_0x001c;
                case 59: goto L_0x001c;
                case 60: goto L_0x001c;
                case 61: goto L_0x001c;
                case 62: goto L_0x001c;
                case 63: goto L_0x001c;
                case 64: goto L_0x001c;
                case 65: goto L_0x001c;
                case 66: goto L_0x001c;
                case 67: goto L_0x001c;
                case 68: goto L_0x001c;
                default: goto L_0x001a;
            }
        L_0x001a:
            goto L_0x01a3
        L_0x001c:
            int r4 = r9.zzak(r2)
            r4 = r4 & r5
            long r4 = (long) r4
            int r8 = com.google.android.gms.internal.vision.zzfl.zzj(r10, r4)
            int r4 = com.google.android.gms.internal.vision.zzfl.zzj(r11, r4)
            if (r8 != r4) goto L_0x01a2
            java.lang.Object r4 = com.google.android.gms.internal.vision.zzfl.zzo(r10, r6)
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)
            boolean r4 = com.google.android.gms.internal.vision.zzep.zzd((java.lang.Object) r4, (java.lang.Object) r5)
            if (r4 != 0) goto L_0x01a3
            goto L_0x018f
        L_0x003c:
            java.lang.Object r3 = com.google.android.gms.internal.vision.zzfl.zzo(r10, r6)
            java.lang.Object r4 = com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)
            boolean r3 = com.google.android.gms.internal.vision.zzep.zzd((java.lang.Object) r3, (java.lang.Object) r4)
            goto L_0x01a3
        L_0x004a:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            java.lang.Object r4 = com.google.android.gms.internal.vision.zzfl.zzo(r10, r6)
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)
            boolean r4 = com.google.android.gms.internal.vision.zzep.zzd((java.lang.Object) r4, (java.lang.Object) r5)
            if (r4 != 0) goto L_0x01a3
            goto L_0x01a2
        L_0x0060:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            long r4 = com.google.android.gms.internal.vision.zzfl.zzk(r10, r6)
            long r6 = com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x01a3
            goto L_0x018f
        L_0x0074:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            int r4 = com.google.android.gms.internal.vision.zzfl.zzj(r10, r6)
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)
            if (r4 == r5) goto L_0x01a3
            goto L_0x01a2
        L_0x0086:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            long r4 = com.google.android.gms.internal.vision.zzfl.zzk(r10, r6)
            long r6 = com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x01a3
            goto L_0x018f
        L_0x009a:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            int r4 = com.google.android.gms.internal.vision.zzfl.zzj(r10, r6)
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)
            if (r4 == r5) goto L_0x01a3
            goto L_0x01a2
        L_0x00ac:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            int r4 = com.google.android.gms.internal.vision.zzfl.zzj(r10, r6)
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)
            if (r4 == r5) goto L_0x01a3
            goto L_0x018f
        L_0x00be:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            int r4 = com.google.android.gms.internal.vision.zzfl.zzj(r10, r6)
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)
            if (r4 == r5) goto L_0x01a3
            goto L_0x01a2
        L_0x00d0:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            java.lang.Object r4 = com.google.android.gms.internal.vision.zzfl.zzo(r10, r6)
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)
            boolean r4 = com.google.android.gms.internal.vision.zzep.zzd((java.lang.Object) r4, (java.lang.Object) r5)
            if (r4 != 0) goto L_0x01a3
            goto L_0x018f
        L_0x00e6:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            java.lang.Object r4 = com.google.android.gms.internal.vision.zzfl.zzo(r10, r6)
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)
            boolean r4 = com.google.android.gms.internal.vision.zzep.zzd((java.lang.Object) r4, (java.lang.Object) r5)
            if (r4 != 0) goto L_0x01a3
            goto L_0x01a2
        L_0x00fc:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            java.lang.Object r4 = com.google.android.gms.internal.vision.zzfl.zzo(r10, r6)
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r11, r6)
            boolean r4 = com.google.android.gms.internal.vision.zzep.zzd((java.lang.Object) r4, (java.lang.Object) r5)
            if (r4 != 0) goto L_0x01a3
            goto L_0x018f
        L_0x0112:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            boolean r4 = com.google.android.gms.internal.vision.zzfl.zzl(r10, r6)
            boolean r5 = com.google.android.gms.internal.vision.zzfl.zzl(r11, r6)
            if (r4 == r5) goto L_0x01a3
            goto L_0x01a2
        L_0x0124:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            int r4 = com.google.android.gms.internal.vision.zzfl.zzj(r10, r6)
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)
            if (r4 == r5) goto L_0x01a3
            goto L_0x018f
        L_0x0135:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            long r4 = com.google.android.gms.internal.vision.zzfl.zzk(r10, r6)
            long r6 = com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x01a3
            goto L_0x01a2
        L_0x0148:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            int r4 = com.google.android.gms.internal.vision.zzfl.zzj(r10, r6)
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)
            if (r4 == r5) goto L_0x01a3
            goto L_0x018f
        L_0x0159:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            long r4 = com.google.android.gms.internal.vision.zzfl.zzk(r10, r6)
            long r6 = com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x01a3
            goto L_0x01a2
        L_0x016c:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            long r4 = com.google.android.gms.internal.vision.zzfl.zzk(r10, r6)
            long r6 = com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x01a3
            goto L_0x018f
        L_0x017f:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            int r4 = com.google.android.gms.internal.vision.zzfl.zzj(r10, r6)
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r11, r6)
            if (r4 == r5) goto L_0x01a3
        L_0x018f:
            goto L_0x01a2
        L_0x0190:
            boolean r4 = r9.zzc(r10, r11, r2)
            if (r4 == 0) goto L_0x01a2
            long r4 = com.google.android.gms.internal.vision.zzfl.zzk(r10, r6)
            long r6 = com.google.android.gms.internal.vision.zzfl.zzk(r11, r6)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x01a3
        L_0x01a2:
            r3 = 0
        L_0x01a3:
            if (r3 != 0) goto L_0x01a6
            return r1
        L_0x01a6:
            int r2 = r2 + 3
            goto L_0x0005
        L_0x01aa:
            com.google.android.gms.internal.vision.zzff<?, ?> r0 = r9.zzns
            java.lang.Object r0 = r0.zzr(r10)
            com.google.android.gms.internal.vision.zzff<?, ?> r2 = r9.zzns
            java.lang.Object r2 = r2.zzr(r11)
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x01bd
            return r1
        L_0x01bd:
            boolean r0 = r9.zznj
            if (r0 == 0) goto L_0x01d2
            com.google.android.gms.internal.vision.zzcg<?> r0 = r9.zznt
            com.google.android.gms.internal.vision.zzcj r10 = r0.zzb(r10)
            com.google.android.gms.internal.vision.zzcg<?> r0 = r9.zznt
            com.google.android.gms.internal.vision.zzcj r11 = r0.zzb(r11)
            boolean r10 = r10.equals(r11)
            return r10
        L_0x01d2:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.equals(java.lang.Object, java.lang.Object):boolean");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0061, code lost:
        r3 = com.google.android.gms.internal.vision.zzfl.zzo(r9, r5);
        r2 = r2 * 53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0093, code lost:
        r2 = r2 * 53;
        r3 = zzg(r9, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00a8, code lost:
        r2 = r2 * 53;
        r3 = zzh(r9, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00ce, code lost:
        if (r3 != null) goto L_0x00e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00d1, code lost:
        r2 = r2 * 53;
        r3 = com.google.android.gms.internal.vision.zzfl.zzo(r9, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00d7, code lost:
        r3 = r3.hashCode();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00e0, code lost:
        if (r3 != null) goto L_0x00e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00e2, code lost:
        r7 = r3.hashCode();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00e6, code lost:
        r2 = (r2 * 53) + r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00ea, code lost:
        r2 = r2 * 53;
        r3 = ((java.lang.String) com.google.android.gms.internal.vision.zzfl.zzo(r9, r5)).hashCode();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00fd, code lost:
        r3 = com.google.android.gms.internal.vision.zzct.zzc(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0116, code lost:
        r3 = java.lang.Float.floatToIntBits(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0121, code lost:
        r3 = java.lang.Double.doubleToLongBits(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0125, code lost:
        r3 = com.google.android.gms.internal.vision.zzct.zzk(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0129, code lost:
        r2 = r2 + r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x012a, code lost:
        r1 = r1 + 3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final int hashCode(T r9) {
        /*
            r8 = this;
            int[] r0 = r8.zzne
            int r0 = r0.length
            r1 = 0
            r2 = 0
        L_0x0005:
            if (r1 >= r0) goto L_0x012e
            int r3 = r8.zzaj(r1)
            int[] r4 = r8.zzne
            r4 = r4[r1]
            r5 = 1048575(0xfffff, float:1.469367E-39)
            r5 = r5 & r3
            long r5 = (long) r5
            r7 = 267386880(0xff00000, float:2.3665827E-29)
            r3 = r3 & r7
            int r3 = r3 >>> 20
            r7 = 37
            switch(r3) {
                case 0: goto L_0x011b;
                case 1: goto L_0x0110;
                case 2: goto L_0x0109;
                case 3: goto L_0x0109;
                case 4: goto L_0x0102;
                case 5: goto L_0x0109;
                case 6: goto L_0x0102;
                case 7: goto L_0x00f7;
                case 8: goto L_0x00ea;
                case 9: goto L_0x00dc;
                case 10: goto L_0x00d1;
                case 11: goto L_0x0102;
                case 12: goto L_0x0102;
                case 13: goto L_0x0102;
                case 14: goto L_0x0109;
                case 15: goto L_0x0102;
                case 16: goto L_0x0109;
                case 17: goto L_0x00ca;
                case 18: goto L_0x00d1;
                case 19: goto L_0x00d1;
                case 20: goto L_0x00d1;
                case 21: goto L_0x00d1;
                case 22: goto L_0x00d1;
                case 23: goto L_0x00d1;
                case 24: goto L_0x00d1;
                case 25: goto L_0x00d1;
                case 26: goto L_0x00d1;
                case 27: goto L_0x00d1;
                case 28: goto L_0x00d1;
                case 29: goto L_0x00d1;
                case 30: goto L_0x00d1;
                case 31: goto L_0x00d1;
                case 32: goto L_0x00d1;
                case 33: goto L_0x00d1;
                case 34: goto L_0x00d1;
                case 35: goto L_0x00d1;
                case 36: goto L_0x00d1;
                case 37: goto L_0x00d1;
                case 38: goto L_0x00d1;
                case 39: goto L_0x00d1;
                case 40: goto L_0x00d1;
                case 41: goto L_0x00d1;
                case 42: goto L_0x00d1;
                case 43: goto L_0x00d1;
                case 44: goto L_0x00d1;
                case 45: goto L_0x00d1;
                case 46: goto L_0x00d1;
                case 47: goto L_0x00d1;
                case 48: goto L_0x00d1;
                case 49: goto L_0x00d1;
                case 50: goto L_0x00d1;
                case 51: goto L_0x00bd;
                case 52: goto L_0x00b0;
                case 53: goto L_0x00a2;
                case 54: goto L_0x009b;
                case 55: goto L_0x008d;
                case 56: goto L_0x0086;
                case 57: goto L_0x007f;
                case 58: goto L_0x0071;
                case 59: goto L_0x0069;
                case 60: goto L_0x005b;
                case 61: goto L_0x0053;
                case 62: goto L_0x004c;
                case 63: goto L_0x0045;
                case 64: goto L_0x003e;
                case 65: goto L_0x0036;
                case 66: goto L_0x002f;
                case 67: goto L_0x0027;
                case 68: goto L_0x0020;
                default: goto L_0x001e;
            }
        L_0x001e:
            goto L_0x012a
        L_0x0020:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x0061
        L_0x0027:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x00a8
        L_0x002f:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x004b
        L_0x0036:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x00a8
        L_0x003e:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x004b
        L_0x0045:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
        L_0x004b:
            goto L_0x0093
        L_0x004c:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x0093
        L_0x0053:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x00d1
        L_0x005b:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
        L_0x0061:
            java.lang.Object r3 = com.google.android.gms.internal.vision.zzfl.zzo(r9, r5)
            int r2 = r2 * 53
            goto L_0x00d7
        L_0x0069:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x00ea
        L_0x0071:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            int r2 = r2 * 53
            boolean r3 = zzi(r9, r5)
            goto L_0x00fd
        L_0x007f:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x0093
        L_0x0086:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x00a8
        L_0x008d:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
        L_0x0093:
            int r2 = r2 * 53
            int r3 = zzg(r9, r5)
            goto L_0x0129
        L_0x009b:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            goto L_0x00a8
        L_0x00a2:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
        L_0x00a8:
            int r2 = r2 * 53
            long r3 = zzh(r9, r5)
            goto L_0x0125
        L_0x00b0:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            int r2 = r2 * 53
            float r3 = zzf(r9, r5)
            goto L_0x0116
        L_0x00bd:
            boolean r3 = r8.zza(r9, (int) r4, (int) r1)
            if (r3 == 0) goto L_0x012a
            int r2 = r2 * 53
            double r3 = zze(r9, r5)
            goto L_0x0121
        L_0x00ca:
            java.lang.Object r3 = com.google.android.gms.internal.vision.zzfl.zzo(r9, r5)
            if (r3 == 0) goto L_0x00e6
            goto L_0x00e2
        L_0x00d1:
            int r2 = r2 * 53
            java.lang.Object r3 = com.google.android.gms.internal.vision.zzfl.zzo(r9, r5)
        L_0x00d7:
            int r3 = r3.hashCode()
            goto L_0x0129
        L_0x00dc:
            java.lang.Object r3 = com.google.android.gms.internal.vision.zzfl.zzo(r9, r5)
            if (r3 == 0) goto L_0x00e6
        L_0x00e2:
            int r7 = r3.hashCode()
        L_0x00e6:
            int r2 = r2 * 53
            int r2 = r2 + r7
            goto L_0x012a
        L_0x00ea:
            int r2 = r2 * 53
            java.lang.Object r3 = com.google.android.gms.internal.vision.zzfl.zzo(r9, r5)
            java.lang.String r3 = (java.lang.String) r3
            int r3 = r3.hashCode()
            goto L_0x0129
        L_0x00f7:
            int r2 = r2 * 53
            boolean r3 = com.google.android.gms.internal.vision.zzfl.zzl(r9, r5)
        L_0x00fd:
            int r3 = com.google.android.gms.internal.vision.zzct.zzc(r3)
            goto L_0x0129
        L_0x0102:
            int r2 = r2 * 53
            int r3 = com.google.android.gms.internal.vision.zzfl.zzj(r9, r5)
            goto L_0x0129
        L_0x0109:
            int r2 = r2 * 53
            long r3 = com.google.android.gms.internal.vision.zzfl.zzk(r9, r5)
            goto L_0x0125
        L_0x0110:
            int r2 = r2 * 53
            float r3 = com.google.android.gms.internal.vision.zzfl.zzm(r9, r5)
        L_0x0116:
            int r3 = java.lang.Float.floatToIntBits(r3)
            goto L_0x0129
        L_0x011b:
            int r2 = r2 * 53
            double r3 = com.google.android.gms.internal.vision.zzfl.zzn(r9, r5)
        L_0x0121:
            long r3 = java.lang.Double.doubleToLongBits(r3)
        L_0x0125:
            int r3 = com.google.android.gms.internal.vision.zzct.zzk(r3)
        L_0x0129:
            int r2 = r2 + r3
        L_0x012a:
            int r1 = r1 + 3
            goto L_0x0005
        L_0x012e:
            int r2 = r2 * 53
            com.google.android.gms.internal.vision.zzff<?, ?> r0 = r8.zzns
            java.lang.Object r0 = r0.zzr(r9)
            int r0 = r0.hashCode()
            int r2 = r2 + r0
            boolean r0 = r8.zznj
            if (r0 == 0) goto L_0x014c
            int r2 = r2 * 53
            com.google.android.gms.internal.vision.zzcg<?> r0 = r8.zznt
            com.google.android.gms.internal.vision.zzcj r9 = r0.zzb(r9)
            int r9 = r9.hashCode()
            int r2 = r2 + r9
        L_0x014c:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.hashCode(java.lang.Object):int");
    }

    public final T newInstance() {
        return this.zznq.newInstance(this.zzni);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x0385, code lost:
        r15.zzb(r9, com.google.android.gms.internal.vision.zzfl.zzo(r14, (long) (r8 & 1048575)), zzag(r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x03a0, code lost:
        r15.zzb(r9, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x03b1, code lost:
        r15.zzg(r9, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x03c2, code lost:
        r15.zzj(r9, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x03d3, code lost:
        r15.zzo(r9, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x03e4, code lost:
        r15.zzp(r9, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x03f5, code lost:
        r15.zzf(r9, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x0400, code lost:
        r15.zza(r9, (com.google.android.gms.internal.vision.zzbo) com.google.android.gms.internal.vision.zzfl.zzo(r14, (long) (r8 & 1048575)));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0413, code lost:
        r15.zza(r9, com.google.android.gms.internal.vision.zzfl.zzo(r14, (long) (r8 & 1048575)), zzag(r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0428, code lost:
        zza(r9, com.google.android.gms.internal.vision.zzfl.zzo(r14, (long) (r8 & 1048575)), r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x043f, code lost:
        r15.zzb(r9, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0450, code lost:
        r15.zzh(r9, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0460, code lost:
        r15.zzc(r9, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x0470, code lost:
        r15.zze(r9, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x0480, code lost:
        r15.zza(r9, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x0490, code lost:
        r15.zzi(r9, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x04a0, code lost:
        r15.zza(r9, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x04b0, code lost:
        r15.zza(r9, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0842, code lost:
        r15.zzb(r10, com.google.android.gms.internal.vision.zzfl.zzo(r14, (long) (r9 & 1048575)), zzag(r8));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x085d, code lost:
        r15.zzb(r10, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x086e, code lost:
        r15.zzg(r10, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x087f, code lost:
        r15.zzj(r10, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0890, code lost:
        r15.zzo(r10, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x08a1, code lost:
        r15.zzp(r10, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x08b2, code lost:
        r15.zzf(r10, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x08bd, code lost:
        r15.zza(r10, (com.google.android.gms.internal.vision.zzbo) com.google.android.gms.internal.vision.zzfl.zzo(r14, (long) (r9 & 1048575)));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x08d0, code lost:
        r15.zza(r10, com.google.android.gms.internal.vision.zzfl.zzo(r14, (long) (r9 & 1048575)), zzag(r8));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x08e5, code lost:
        zza(r10, com.google.android.gms.internal.vision.zzfl.zzo(r14, (long) (r9 & 1048575)), r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x08fc, code lost:
        r15.zzb(r10, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x090d, code lost:
        r15.zzh(r10, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x091d, code lost:
        r15.zzc(r10, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x092d, code lost:
        r15.zze(r10, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x093d, code lost:
        r15.zza(r10, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x094d, code lost:
        r15.zzi(r10, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x095d, code lost:
        r15.zza(r10, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x096d, code lost:
        r15.zza(r10, r11);
     */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0039  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x04b9  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x04f6  */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x0976  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zza(T r14, com.google.android.gms.internal.vision.zzfz r15) throws java.io.IOException {
        /*
            r13 = this;
            int r0 = r15.zzbc()
            int r1 = com.google.android.gms.internal.vision.zzcr.zzd.zzlk
            r2 = 267386880(0xff00000, float:2.3665827E-29)
            r3 = 0
            r4 = 1
            r5 = 0
            r6 = 1048575(0xfffff, float:1.469367E-39)
            if (r0 != r1) goto L_0x04cf
            com.google.android.gms.internal.vision.zzff<?, ?> r0 = r13.zzns
            zza(r0, r14, (com.google.android.gms.internal.vision.zzfz) r15)
            boolean r0 = r13.zznj
            if (r0 == 0) goto L_0x0030
            com.google.android.gms.internal.vision.zzcg<?> r0 = r13.zznt
            com.google.android.gms.internal.vision.zzcj r0 = r0.zzb(r14)
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x0030
            java.util.Iterator r0 = r0.descendingIterator()
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            goto L_0x0032
        L_0x0030:
            r0 = r3
            r1 = r0
        L_0x0032:
            int[] r7 = r13.zzne
            int r7 = r7.length
            int r7 = r7 + -3
        L_0x0037:
            if (r7 < 0) goto L_0x04b7
            int r8 = r13.zzaj(r7)
            int[] r9 = r13.zzne
            r9 = r9[r7]
        L_0x0041:
            if (r1 == 0) goto L_0x005f
            com.google.android.gms.internal.vision.zzcg<?> r10 = r13.zznt
            int r10 = r10.zza(r1)
            if (r10 <= r9) goto L_0x005f
            com.google.android.gms.internal.vision.zzcg<?> r10 = r13.zznt
            r10.zza((com.google.android.gms.internal.vision.zzfz) r15, (java.util.Map.Entry<?, ?>) r1)
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x005d
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            goto L_0x0041
        L_0x005d:
            r1 = r3
            goto L_0x0041
        L_0x005f:
            r10 = r8 & r2
            int r10 = r10 >>> 20
            switch(r10) {
                case 0: goto L_0x04a4;
                case 1: goto L_0x0494;
                case 2: goto L_0x0484;
                case 3: goto L_0x0474;
                case 4: goto L_0x0464;
                case 5: goto L_0x0454;
                case 6: goto L_0x0444;
                case 7: goto L_0x0433;
                case 8: goto L_0x0422;
                case 9: goto L_0x040d;
                case 10: goto L_0x03fa;
                case 11: goto L_0x03e9;
                case 12: goto L_0x03d8;
                case 13: goto L_0x03c7;
                case 14: goto L_0x03b6;
                case 15: goto L_0x03a5;
                case 16: goto L_0x0394;
                case 17: goto L_0x037f;
                case 18: goto L_0x036e;
                case 19: goto L_0x035d;
                case 20: goto L_0x034c;
                case 21: goto L_0x033b;
                case 22: goto L_0x032a;
                case 23: goto L_0x0319;
                case 24: goto L_0x0308;
                case 25: goto L_0x02f7;
                case 26: goto L_0x02e6;
                case 27: goto L_0x02d1;
                case 28: goto L_0x02c0;
                case 29: goto L_0x02af;
                case 30: goto L_0x029e;
                case 31: goto L_0x028d;
                case 32: goto L_0x027c;
                case 33: goto L_0x026b;
                case 34: goto L_0x025a;
                case 35: goto L_0x0249;
                case 36: goto L_0x0238;
                case 37: goto L_0x0227;
                case 38: goto L_0x0216;
                case 39: goto L_0x0205;
                case 40: goto L_0x01f4;
                case 41: goto L_0x01e3;
                case 42: goto L_0x01d2;
                case 43: goto L_0x01c1;
                case 44: goto L_0x01b0;
                case 45: goto L_0x019f;
                case 46: goto L_0x018e;
                case 47: goto L_0x017d;
                case 48: goto L_0x016c;
                case 49: goto L_0x0157;
                case 50: goto L_0x014c;
                case 51: goto L_0x013e;
                case 52: goto L_0x0130;
                case 53: goto L_0x0122;
                case 54: goto L_0x0114;
                case 55: goto L_0x0106;
                case 56: goto L_0x00f8;
                case 57: goto L_0x00ea;
                case 58: goto L_0x00dc;
                case 59: goto L_0x00d4;
                case 60: goto L_0x00cc;
                case 61: goto L_0x00c4;
                case 62: goto L_0x00b6;
                case 63: goto L_0x00a8;
                case 64: goto L_0x009a;
                case 65: goto L_0x008c;
                case 66: goto L_0x007e;
                case 67: goto L_0x0070;
                case 68: goto L_0x0068;
                default: goto L_0x0066;
            }
        L_0x0066:
            goto L_0x04b3
        L_0x0068:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            goto L_0x0385
        L_0x0070:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = zzh(r14, r10)
            goto L_0x03a0
        L_0x007e:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = zzg(r14, r10)
            goto L_0x03b1
        L_0x008c:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = zzh(r14, r10)
            goto L_0x03c2
        L_0x009a:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = zzg(r14, r10)
            goto L_0x03d3
        L_0x00a8:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = zzg(r14, r10)
            goto L_0x03e4
        L_0x00b6:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = zzg(r14, r10)
            goto L_0x03f5
        L_0x00c4:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            goto L_0x0400
        L_0x00cc:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            goto L_0x0413
        L_0x00d4:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            goto L_0x0428
        L_0x00dc:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            boolean r8 = zzi(r14, r10)
            goto L_0x043f
        L_0x00ea:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = zzg(r14, r10)
            goto L_0x0450
        L_0x00f8:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = zzh(r14, r10)
            goto L_0x0460
        L_0x0106:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = zzg(r14, r10)
            goto L_0x0470
        L_0x0114:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = zzh(r14, r10)
            goto L_0x0480
        L_0x0122:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = zzh(r14, r10)
            goto L_0x0490
        L_0x0130:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            float r8 = zzf(r14, r10)
            goto L_0x04a0
        L_0x013e:
            boolean r10 = r13.zza(r14, (int) r9, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            double r10 = zze(r14, r10)
            goto L_0x04b0
        L_0x014c:
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            r13.zza((com.google.android.gms.internal.vision.zzfz) r15, (int) r9, (java.lang.Object) r8, (int) r7)
            goto L_0x04b3
        L_0x0157:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzen r10 = r13.zzag(r7)
            com.google.android.gms.internal.vision.zzep.zzb((int) r9, (java.util.List<?>) r8, (com.google.android.gms.internal.vision.zzfz) r15, (com.google.android.gms.internal.vision.zzen) r10)
            goto L_0x04b3
        L_0x016c:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zze(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x017d:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzj(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x018e:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzg(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x019f:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzl(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x01b0:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzm(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x01c1:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzi(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x01d2:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzn(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x01e3:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzk(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x01f4:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzf(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x0205:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzh(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x0216:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzd(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x0227:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzc(r9, r8, r15, r4)
            goto L_0x04b3
        L_0x0238:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzb((int) r9, (java.util.List<java.lang.Float>) r8, (com.google.android.gms.internal.vision.zzfz) r15, (boolean) r4)
            goto L_0x04b3
        L_0x0249:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zza((int) r9, (java.util.List<java.lang.Double>) r8, (com.google.android.gms.internal.vision.zzfz) r15, (boolean) r4)
            goto L_0x04b3
        L_0x025a:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zze(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x026b:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzj(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x027c:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzg(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x028d:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzl(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x029e:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzm(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x02af:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzi(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x02c0:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzb(r9, r8, r15)
            goto L_0x04b3
        L_0x02d1:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzen r10 = r13.zzag(r7)
            com.google.android.gms.internal.vision.zzep.zza((int) r9, (java.util.List<?>) r8, (com.google.android.gms.internal.vision.zzfz) r15, (com.google.android.gms.internal.vision.zzen) r10)
            goto L_0x04b3
        L_0x02e6:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zza((int) r9, (java.util.List<java.lang.String>) r8, (com.google.android.gms.internal.vision.zzfz) r15)
            goto L_0x04b3
        L_0x02f7:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzn(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x0308:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzk(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x0319:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzf(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x032a:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzh(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x033b:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzd(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x034c:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzc(r9, r8, r15, r5)
            goto L_0x04b3
        L_0x035d:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zzb((int) r9, (java.util.List<java.lang.Float>) r8, (com.google.android.gms.internal.vision.zzfz) r15, (boolean) r5)
            goto L_0x04b3
        L_0x036e:
            int[] r9 = r13.zzne
            r9 = r9[r7]
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            java.util.List r8 = (java.util.List) r8
            com.google.android.gms.internal.vision.zzep.zza((int) r9, (java.util.List<java.lang.Double>) r8, (com.google.android.gms.internal.vision.zzfz) r15, (boolean) r5)
            goto L_0x04b3
        L_0x037f:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
        L_0x0385:
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            com.google.android.gms.internal.vision.zzen r10 = r13.zzag(r7)
            r15.zzb((int) r9, (java.lang.Object) r8, (com.google.android.gms.internal.vision.zzen) r10)
            goto L_0x04b3
        L_0x0394:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r10)
        L_0x03a0:
            r15.zzb((int) r9, (long) r10)
            goto L_0x04b3
        L_0x03a5:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r10)
        L_0x03b1:
            r15.zzg(r9, r8)
            goto L_0x04b3
        L_0x03b6:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r10)
        L_0x03c2:
            r15.zzj(r9, r10)
            goto L_0x04b3
        L_0x03c7:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r10)
        L_0x03d3:
            r15.zzo(r9, r8)
            goto L_0x04b3
        L_0x03d8:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r10)
        L_0x03e4:
            r15.zzp(r9, r8)
            goto L_0x04b3
        L_0x03e9:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r10)
        L_0x03f5:
            r15.zzf(r9, r8)
            goto L_0x04b3
        L_0x03fa:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
        L_0x0400:
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            com.google.android.gms.internal.vision.zzbo r8 = (com.google.android.gms.internal.vision.zzbo) r8
            r15.zza((int) r9, (com.google.android.gms.internal.vision.zzbo) r8)
            goto L_0x04b3
        L_0x040d:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
        L_0x0413:
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            com.google.android.gms.internal.vision.zzen r10 = r13.zzag(r7)
            r15.zza((int) r9, (java.lang.Object) r8, (com.google.android.gms.internal.vision.zzen) r10)
            goto L_0x04b3
        L_0x0422:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
        L_0x0428:
            r8 = r8 & r6
            long r10 = (long) r8
            java.lang.Object r8 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r10)
            zza((int) r9, (java.lang.Object) r8, (com.google.android.gms.internal.vision.zzfz) r15)
            goto L_0x04b3
        L_0x0433:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            boolean r8 = com.google.android.gms.internal.vision.zzfl.zzl(r14, r10)
        L_0x043f:
            r15.zzb((int) r9, (boolean) r8)
            goto L_0x04b3
        L_0x0444:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r10)
        L_0x0450:
            r15.zzh(r9, r8)
            goto L_0x04b3
        L_0x0454:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r10)
        L_0x0460:
            r15.zzc(r9, r10)
            goto L_0x04b3
        L_0x0464:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            int r8 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r10)
        L_0x0470:
            r15.zze(r9, r8)
            goto L_0x04b3
        L_0x0474:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r10)
        L_0x0480:
            r15.zza((int) r9, (long) r10)
            goto L_0x04b3
        L_0x0484:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            long r10 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r10)
        L_0x0490:
            r15.zzi(r9, r10)
            goto L_0x04b3
        L_0x0494:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            float r8 = com.google.android.gms.internal.vision.zzfl.zzm(r14, r10)
        L_0x04a0:
            r15.zza((int) r9, (float) r8)
            goto L_0x04b3
        L_0x04a4:
            boolean r10 = r13.zza(r14, (int) r7)
            if (r10 == 0) goto L_0x04b3
            r8 = r8 & r6
            long r10 = (long) r8
            double r10 = com.google.android.gms.internal.vision.zzfl.zzn(r14, r10)
        L_0x04b0:
            r15.zza((int) r9, (double) r10)
        L_0x04b3:
            int r7 = r7 + -3
            goto L_0x0037
        L_0x04b7:
            if (r1 == 0) goto L_0x04ce
            com.google.android.gms.internal.vision.zzcg<?> r14 = r13.zznt
            r14.zza((com.google.android.gms.internal.vision.zzfz) r15, (java.util.Map.Entry<?, ?>) r1)
            boolean r14 = r0.hasNext()
            if (r14 == 0) goto L_0x04cc
            java.lang.Object r14 = r0.next()
            java.util.Map$Entry r14 = (java.util.Map.Entry) r14
            r1 = r14
            goto L_0x04b7
        L_0x04cc:
            r1 = r3
            goto L_0x04b7
        L_0x04ce:
            return
        L_0x04cf:
            boolean r0 = r13.zznl
            if (r0 == 0) goto L_0x0990
            boolean r0 = r13.zznj
            if (r0 == 0) goto L_0x04ee
            com.google.android.gms.internal.vision.zzcg<?> r0 = r13.zznt
            com.google.android.gms.internal.vision.zzcj r0 = r0.zzb(r14)
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x04ee
            java.util.Iterator r0 = r0.iterator()
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            goto L_0x04f0
        L_0x04ee:
            r0 = r3
            r1 = r0
        L_0x04f0:
            int[] r7 = r13.zzne
            int r7 = r7.length
            r8 = 0
        L_0x04f4:
            if (r8 >= r7) goto L_0x0974
            int r9 = r13.zzaj(r8)
            int[] r10 = r13.zzne
            r10 = r10[r8]
        L_0x04fe:
            if (r1 == 0) goto L_0x051c
            com.google.android.gms.internal.vision.zzcg<?> r11 = r13.zznt
            int r11 = r11.zza(r1)
            if (r11 > r10) goto L_0x051c
            com.google.android.gms.internal.vision.zzcg<?> r11 = r13.zznt
            r11.zza((com.google.android.gms.internal.vision.zzfz) r15, (java.util.Map.Entry<?, ?>) r1)
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x051a
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            goto L_0x04fe
        L_0x051a:
            r1 = r3
            goto L_0x04fe
        L_0x051c:
            r11 = r9 & r2
            int r11 = r11 >>> 20
            switch(r11) {
                case 0: goto L_0x0961;
                case 1: goto L_0x0951;
                case 2: goto L_0x0941;
                case 3: goto L_0x0931;
                case 4: goto L_0x0921;
                case 5: goto L_0x0911;
                case 6: goto L_0x0901;
                case 7: goto L_0x08f0;
                case 8: goto L_0x08df;
                case 9: goto L_0x08ca;
                case 10: goto L_0x08b7;
                case 11: goto L_0x08a6;
                case 12: goto L_0x0895;
                case 13: goto L_0x0884;
                case 14: goto L_0x0873;
                case 15: goto L_0x0862;
                case 16: goto L_0x0851;
                case 17: goto L_0x083c;
                case 18: goto L_0x082b;
                case 19: goto L_0x081a;
                case 20: goto L_0x0809;
                case 21: goto L_0x07f8;
                case 22: goto L_0x07e7;
                case 23: goto L_0x07d6;
                case 24: goto L_0x07c5;
                case 25: goto L_0x07b4;
                case 26: goto L_0x07a3;
                case 27: goto L_0x078e;
                case 28: goto L_0x077d;
                case 29: goto L_0x076c;
                case 30: goto L_0x075b;
                case 31: goto L_0x074a;
                case 32: goto L_0x0739;
                case 33: goto L_0x0728;
                case 34: goto L_0x0717;
                case 35: goto L_0x0706;
                case 36: goto L_0x06f5;
                case 37: goto L_0x06e4;
                case 38: goto L_0x06d3;
                case 39: goto L_0x06c2;
                case 40: goto L_0x06b1;
                case 41: goto L_0x06a0;
                case 42: goto L_0x068f;
                case 43: goto L_0x067e;
                case 44: goto L_0x066d;
                case 45: goto L_0x065c;
                case 46: goto L_0x064b;
                case 47: goto L_0x063a;
                case 48: goto L_0x0629;
                case 49: goto L_0x0614;
                case 50: goto L_0x0609;
                case 51: goto L_0x05fb;
                case 52: goto L_0x05ed;
                case 53: goto L_0x05df;
                case 54: goto L_0x05d1;
                case 55: goto L_0x05c3;
                case 56: goto L_0x05b5;
                case 57: goto L_0x05a7;
                case 58: goto L_0x0599;
                case 59: goto L_0x0591;
                case 60: goto L_0x0589;
                case 61: goto L_0x0581;
                case 62: goto L_0x0573;
                case 63: goto L_0x0565;
                case 64: goto L_0x0557;
                case 65: goto L_0x0549;
                case 66: goto L_0x053b;
                case 67: goto L_0x052d;
                case 68: goto L_0x0525;
                default: goto L_0x0523;
            }
        L_0x0523:
            goto L_0x0970
        L_0x0525:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            goto L_0x0842
        L_0x052d:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = zzh(r14, r11)
            goto L_0x085d
        L_0x053b:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = zzg(r14, r11)
            goto L_0x086e
        L_0x0549:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = zzh(r14, r11)
            goto L_0x087f
        L_0x0557:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = zzg(r14, r11)
            goto L_0x0890
        L_0x0565:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = zzg(r14, r11)
            goto L_0x08a1
        L_0x0573:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = zzg(r14, r11)
            goto L_0x08b2
        L_0x0581:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            goto L_0x08bd
        L_0x0589:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            goto L_0x08d0
        L_0x0591:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            goto L_0x08e5
        L_0x0599:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            boolean r9 = zzi(r14, r11)
            goto L_0x08fc
        L_0x05a7:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = zzg(r14, r11)
            goto L_0x090d
        L_0x05b5:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = zzh(r14, r11)
            goto L_0x091d
        L_0x05c3:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = zzg(r14, r11)
            goto L_0x092d
        L_0x05d1:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = zzh(r14, r11)
            goto L_0x093d
        L_0x05df:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = zzh(r14, r11)
            goto L_0x094d
        L_0x05ed:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            float r9 = zzf(r14, r11)
            goto L_0x095d
        L_0x05fb:
            boolean r11 = r13.zza(r14, (int) r10, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            double r11 = zze(r14, r11)
            goto L_0x096d
        L_0x0609:
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            r13.zza((com.google.android.gms.internal.vision.zzfz) r15, (int) r10, (java.lang.Object) r9, (int) r8)
            goto L_0x0970
        L_0x0614:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzen r11 = r13.zzag(r8)
            com.google.android.gms.internal.vision.zzep.zzb((int) r10, (java.util.List<?>) r9, (com.google.android.gms.internal.vision.zzfz) r15, (com.google.android.gms.internal.vision.zzen) r11)
            goto L_0x0970
        L_0x0629:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zze(r10, r9, r15, r4)
            goto L_0x0970
        L_0x063a:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzj(r10, r9, r15, r4)
            goto L_0x0970
        L_0x064b:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzg(r10, r9, r15, r4)
            goto L_0x0970
        L_0x065c:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzl(r10, r9, r15, r4)
            goto L_0x0970
        L_0x066d:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzm(r10, r9, r15, r4)
            goto L_0x0970
        L_0x067e:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzi(r10, r9, r15, r4)
            goto L_0x0970
        L_0x068f:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzn(r10, r9, r15, r4)
            goto L_0x0970
        L_0x06a0:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzk(r10, r9, r15, r4)
            goto L_0x0970
        L_0x06b1:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzf(r10, r9, r15, r4)
            goto L_0x0970
        L_0x06c2:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzh(r10, r9, r15, r4)
            goto L_0x0970
        L_0x06d3:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzd(r10, r9, r15, r4)
            goto L_0x0970
        L_0x06e4:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzc(r10, r9, r15, r4)
            goto L_0x0970
        L_0x06f5:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzb((int) r10, (java.util.List<java.lang.Float>) r9, (com.google.android.gms.internal.vision.zzfz) r15, (boolean) r4)
            goto L_0x0970
        L_0x0706:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zza((int) r10, (java.util.List<java.lang.Double>) r9, (com.google.android.gms.internal.vision.zzfz) r15, (boolean) r4)
            goto L_0x0970
        L_0x0717:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zze(r10, r9, r15, r5)
            goto L_0x0970
        L_0x0728:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzj(r10, r9, r15, r5)
            goto L_0x0970
        L_0x0739:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzg(r10, r9, r15, r5)
            goto L_0x0970
        L_0x074a:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzl(r10, r9, r15, r5)
            goto L_0x0970
        L_0x075b:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzm(r10, r9, r15, r5)
            goto L_0x0970
        L_0x076c:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzi(r10, r9, r15, r5)
            goto L_0x0970
        L_0x077d:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzb(r10, r9, r15)
            goto L_0x0970
        L_0x078e:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzen r11 = r13.zzag(r8)
            com.google.android.gms.internal.vision.zzep.zza((int) r10, (java.util.List<?>) r9, (com.google.android.gms.internal.vision.zzfz) r15, (com.google.android.gms.internal.vision.zzen) r11)
            goto L_0x0970
        L_0x07a3:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zza((int) r10, (java.util.List<java.lang.String>) r9, (com.google.android.gms.internal.vision.zzfz) r15)
            goto L_0x0970
        L_0x07b4:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzn(r10, r9, r15, r5)
            goto L_0x0970
        L_0x07c5:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzk(r10, r9, r15, r5)
            goto L_0x0970
        L_0x07d6:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzf(r10, r9, r15, r5)
            goto L_0x0970
        L_0x07e7:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzh(r10, r9, r15, r5)
            goto L_0x0970
        L_0x07f8:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzd(r10, r9, r15, r5)
            goto L_0x0970
        L_0x0809:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzc(r10, r9, r15, r5)
            goto L_0x0970
        L_0x081a:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zzb((int) r10, (java.util.List<java.lang.Float>) r9, (com.google.android.gms.internal.vision.zzfz) r15, (boolean) r5)
            goto L_0x0970
        L_0x082b:
            int[] r10 = r13.zzne
            r10 = r10[r8]
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            java.util.List r9 = (java.util.List) r9
            com.google.android.gms.internal.vision.zzep.zza((int) r10, (java.util.List<java.lang.Double>) r9, (com.google.android.gms.internal.vision.zzfz) r15, (boolean) r5)
            goto L_0x0970
        L_0x083c:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
        L_0x0842:
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            com.google.android.gms.internal.vision.zzen r11 = r13.zzag(r8)
            r15.zzb((int) r10, (java.lang.Object) r9, (com.google.android.gms.internal.vision.zzen) r11)
            goto L_0x0970
        L_0x0851:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r11)
        L_0x085d:
            r15.zzb((int) r10, (long) r11)
            goto L_0x0970
        L_0x0862:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r11)
        L_0x086e:
            r15.zzg(r10, r9)
            goto L_0x0970
        L_0x0873:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r11)
        L_0x087f:
            r15.zzj(r10, r11)
            goto L_0x0970
        L_0x0884:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r11)
        L_0x0890:
            r15.zzo(r10, r9)
            goto L_0x0970
        L_0x0895:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r11)
        L_0x08a1:
            r15.zzp(r10, r9)
            goto L_0x0970
        L_0x08a6:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r11)
        L_0x08b2:
            r15.zzf(r10, r9)
            goto L_0x0970
        L_0x08b7:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
        L_0x08bd:
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            com.google.android.gms.internal.vision.zzbo r9 = (com.google.android.gms.internal.vision.zzbo) r9
            r15.zza((int) r10, (com.google.android.gms.internal.vision.zzbo) r9)
            goto L_0x0970
        L_0x08ca:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
        L_0x08d0:
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            com.google.android.gms.internal.vision.zzen r11 = r13.zzag(r8)
            r15.zza((int) r10, (java.lang.Object) r9, (com.google.android.gms.internal.vision.zzen) r11)
            goto L_0x0970
        L_0x08df:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
        L_0x08e5:
            r9 = r9 & r6
            long r11 = (long) r9
            java.lang.Object r9 = com.google.android.gms.internal.vision.zzfl.zzo(r14, r11)
            zza((int) r10, (java.lang.Object) r9, (com.google.android.gms.internal.vision.zzfz) r15)
            goto L_0x0970
        L_0x08f0:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            boolean r9 = com.google.android.gms.internal.vision.zzfl.zzl(r14, r11)
        L_0x08fc:
            r15.zzb((int) r10, (boolean) r9)
            goto L_0x0970
        L_0x0901:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r11)
        L_0x090d:
            r15.zzh(r10, r9)
            goto L_0x0970
        L_0x0911:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r11)
        L_0x091d:
            r15.zzc(r10, r11)
            goto L_0x0970
        L_0x0921:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            int r9 = com.google.android.gms.internal.vision.zzfl.zzj(r14, r11)
        L_0x092d:
            r15.zze(r10, r9)
            goto L_0x0970
        L_0x0931:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r11)
        L_0x093d:
            r15.zza((int) r10, (long) r11)
            goto L_0x0970
        L_0x0941:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            long r11 = com.google.android.gms.internal.vision.zzfl.zzk(r14, r11)
        L_0x094d:
            r15.zzi(r10, r11)
            goto L_0x0970
        L_0x0951:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            float r9 = com.google.android.gms.internal.vision.zzfl.zzm(r14, r11)
        L_0x095d:
            r15.zza((int) r10, (float) r9)
            goto L_0x0970
        L_0x0961:
            boolean r11 = r13.zza(r14, (int) r8)
            if (r11 == 0) goto L_0x0970
            r9 = r9 & r6
            long r11 = (long) r9
            double r11 = com.google.android.gms.internal.vision.zzfl.zzn(r14, r11)
        L_0x096d:
            r15.zza((int) r10, (double) r11)
        L_0x0970:
            int r8 = r8 + 3
            goto L_0x04f4
        L_0x0974:
            if (r1 == 0) goto L_0x098a
            com.google.android.gms.internal.vision.zzcg<?> r2 = r13.zznt
            r2.zza((com.google.android.gms.internal.vision.zzfz) r15, (java.util.Map.Entry<?, ?>) r1)
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0988
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            goto L_0x0974
        L_0x0988:
            r1 = r3
            goto L_0x0974
        L_0x098a:
            com.google.android.gms.internal.vision.zzff<?, ?> r0 = r13.zzns
            zza(r0, r14, (com.google.android.gms.internal.vision.zzfz) r15)
            return
        L_0x0990:
            r13.zzb(r14, (com.google.android.gms.internal.vision.zzfz) r15)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zza(java.lang.Object, com.google.android.gms.internal.vision.zzfz):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v2, resolved type: byte} */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0090, code lost:
        if (r6 == 0) goto L_0x0109;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x009b, code lost:
        r1 = r11.zzgq;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x009d, code lost:
        r9.putObject(r14, r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00f1, code lost:
        r2 = r4;
        r1 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0107, code lost:
        if (r6 == 0) goto L_0x0109;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0109, code lost:
        r0 = com.google.android.gms.internal.vision.zzbk.zza(r12, r8, r11);
        r1 = r11.zzgo;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x010f, code lost:
        r9.putInt(r14, r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0120, code lost:
        r0.putLong(r1, r2, r4);
        r0 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x013f, code lost:
        r0 = r8 + 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0141, code lost:
        r1 = r7;
        r2 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0145, code lost:
        r24 = r7;
        r15 = r8;
        r18 = r9;
        r19 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x01c8, code lost:
        if (r0 == r15) goto L_0x0214;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01f5, code lost:
        if (r0 == r15) goto L_0x0214;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0212, code lost:
        if (r0 == r15) goto L_0x0214;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zza(T r28, byte[] r29, int r30, int r31, com.google.android.gms.internal.vision.zzbl r32) throws java.io.IOException {
        /*
            r27 = this;
            r15 = r27
            r14 = r28
            r12 = r29
            r13 = r31
            r11 = r32
            boolean r0 = r15.zznl
            if (r0 == 0) goto L_0x023f
            sun.misc.Unsafe r9 = zznd
            r10 = -1
            r16 = 0
            r0 = r30
            r1 = -1
            r2 = 0
        L_0x0017:
            if (r0 >= r13) goto L_0x0236
            int r3 = r0 + 1
            byte r0 = r12[r0]
            if (r0 >= 0) goto L_0x0029
            int r0 = com.google.android.gms.internal.vision.zzbk.zza((int) r0, (byte[]) r12, (int) r3, (com.google.android.gms.internal.vision.zzbl) r11)
            int r3 = r11.zzgo
            r8 = r0
            r17 = r3
            goto L_0x002c
        L_0x0029:
            r17 = r0
            r8 = r3
        L_0x002c:
            int r7 = r17 >>> 3
            r6 = r17 & 7
            if (r7 <= r1) goto L_0x0039
            int r2 = r2 / 3
            int r0 = r15.zzr(r7, r2)
            goto L_0x003d
        L_0x0039:
            int r0 = r15.zzal(r7)
        L_0x003d:
            r4 = r0
            if (r4 != r10) goto L_0x004b
            r24 = r7
            r2 = r8
            r18 = r9
            r19 = 0
            r26 = -1
            goto L_0x0215
        L_0x004b:
            int[] r0 = r15.zzne
            int r1 = r4 + 1
            r5 = r0[r1]
            r0 = 267386880(0xff00000, float:2.3665827E-29)
            r0 = r0 & r5
            int r3 = r0 >>> 20
            r0 = 1048575(0xfffff, float:1.469367E-39)
            r0 = r0 & r5
            long r1 = (long) r0
            r0 = 17
            r10 = 2
            if (r3 > r0) goto L_0x014d
            r0 = 1
            switch(r3) {
                case 0: goto L_0x0134;
                case 1: goto L_0x0125;
                case 2: goto L_0x0113;
                case 3: goto L_0x0113;
                case 4: goto L_0x0105;
                case 5: goto L_0x00f5;
                case 6: goto L_0x00e4;
                case 7: goto L_0x00ce;
                case 8: goto L_0x00bc;
                case 9: goto L_0x00a1;
                case 10: goto L_0x0094;
                case 11: goto L_0x0105;
                case 12: goto L_0x008e;
                case 13: goto L_0x00e4;
                case 14: goto L_0x00f5;
                case 15: goto L_0x007e;
                case 16: goto L_0x0066;
                default: goto L_0x0064;
            }
        L_0x0064:
            goto L_0x018a
        L_0x0066:
            if (r6 != 0) goto L_0x018a
            int r6 = com.google.android.gms.internal.vision.zzbk.zzb(r12, r8, r11)
            r19 = r1
            long r0 = r11.zzgp
            long r21 = com.google.android.gms.internal.vision.zzbx.zza(r0)
            r0 = r9
            r2 = r19
            r1 = r28
            r10 = r4
            r4 = r21
            goto L_0x0120
        L_0x007e:
            r2 = r1
            r10 = r4
            if (r6 != 0) goto L_0x0145
            int r0 = com.google.android.gms.internal.vision.zzbk.zza(r12, r8, r11)
            int r1 = r11.zzgo
            int r1 = com.google.android.gms.internal.vision.zzbx.zzo(r1)
            goto L_0x010f
        L_0x008e:
            r2 = r1
            r10 = r4
            if (r6 != 0) goto L_0x0145
            goto L_0x0109
        L_0x0094:
            r2 = r1
            if (r6 != r10) goto L_0x018a
            int r0 = com.google.android.gms.internal.vision.zzbk.zze(r12, r8, r11)
        L_0x009b:
            java.lang.Object r1 = r11.zzgq
        L_0x009d:
            r9.putObject(r14, r2, r1)
            goto L_0x00f1
        L_0x00a1:
            r2 = r1
            if (r6 != r10) goto L_0x018a
            com.google.android.gms.internal.vision.zzen r0 = r15.zzag(r4)
            int r0 = zza((com.google.android.gms.internal.vision.zzen) r0, (byte[]) r12, (int) r8, (int) r13, (com.google.android.gms.internal.vision.zzbl) r11)
            java.lang.Object r1 = r9.getObject(r14, r2)
            if (r1 != 0) goto L_0x00b5
            java.lang.Object r1 = r11.zzgq
            goto L_0x009d
        L_0x00b5:
            java.lang.Object r5 = r11.zzgq
            java.lang.Object r1 = com.google.android.gms.internal.vision.zzct.zza((java.lang.Object) r1, (java.lang.Object) r5)
            goto L_0x009d
        L_0x00bc:
            r2 = r1
            if (r6 != r10) goto L_0x018a
            r0 = 536870912(0x20000000, float:1.0842022E-19)
            r0 = r0 & r5
            if (r0 != 0) goto L_0x00c9
            int r0 = com.google.android.gms.internal.vision.zzbk.zzc(r12, r8, r11)
            goto L_0x009b
        L_0x00c9:
            int r0 = com.google.android.gms.internal.vision.zzbk.zzd(r12, r8, r11)
            goto L_0x009b
        L_0x00ce:
            r2 = r1
            if (r6 != 0) goto L_0x018a
            int r1 = com.google.android.gms.internal.vision.zzbk.zzb(r12, r8, r11)
            long r5 = r11.zzgp
            r19 = 0
            int r8 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r8 == 0) goto L_0x00de
            goto L_0x00df
        L_0x00de:
            r0 = 0
        L_0x00df:
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r14, (long) r2, (boolean) r0)
            r0 = r1
            goto L_0x00f1
        L_0x00e4:
            r2 = r1
            r0 = 5
            if (r6 != r0) goto L_0x018a
            int r0 = com.google.android.gms.internal.vision.zzbk.zza(r12, r8)
            r9.putInt(r14, r2, r0)
            int r0 = r8 + 4
        L_0x00f1:
            r2 = r4
            r1 = r7
            goto L_0x0233
        L_0x00f5:
            r2 = r1
            if (r6 != r0) goto L_0x018a
            long r5 = com.google.android.gms.internal.vision.zzbk.zzb(r12, r8)
            r0 = r9
            r1 = r28
            r10 = r4
            r4 = r5
            r0.putLong(r1, r2, r4)
            goto L_0x013f
        L_0x0105:
            r2 = r1
            r10 = r4
            if (r6 != 0) goto L_0x0145
        L_0x0109:
            int r0 = com.google.android.gms.internal.vision.zzbk.zza(r12, r8, r11)
            int r1 = r11.zzgo
        L_0x010f:
            r9.putInt(r14, r2, r1)
            goto L_0x0141
        L_0x0113:
            r2 = r1
            r10 = r4
            if (r6 != 0) goto L_0x0145
            int r6 = com.google.android.gms.internal.vision.zzbk.zzb(r12, r8, r11)
            long r4 = r11.zzgp
            r0 = r9
            r1 = r28
        L_0x0120:
            r0.putLong(r1, r2, r4)
            r0 = r6
            goto L_0x0141
        L_0x0125:
            r2 = r1
            r10 = r4
            r0 = 5
            if (r6 != r0) goto L_0x0145
            float r0 = com.google.android.gms.internal.vision.zzbk.zzd(r12, r8)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r14, (long) r2, (float) r0)
            int r0 = r8 + 4
            goto L_0x0141
        L_0x0134:
            r2 = r1
            r10 = r4
            if (r6 != r0) goto L_0x0145
            double r0 = com.google.android.gms.internal.vision.zzbk.zzc(r12, r8)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r14, (long) r2, (double) r0)
        L_0x013f:
            int r0 = r8 + 8
        L_0x0141:
            r1 = r7
            r2 = r10
            goto L_0x0233
        L_0x0145:
            r24 = r7
            r15 = r8
            r18 = r9
            r19 = r10
            goto L_0x0191
        L_0x014d:
            r0 = 27
            if (r3 != r0) goto L_0x0195
            if (r6 != r10) goto L_0x018a
            java.lang.Object r0 = r9.getObject(r14, r1)
            com.google.android.gms.internal.vision.zzcw r0 = (com.google.android.gms.internal.vision.zzcw) r0
            boolean r3 = r0.zzan()
            if (r3 != 0) goto L_0x0171
            int r3 = r0.size()
            if (r3 != 0) goto L_0x0168
            r3 = 10
            goto L_0x016a
        L_0x0168:
            int r3 = r3 << 1
        L_0x016a:
            com.google.android.gms.internal.vision.zzcw r0 = r0.zzk(r3)
            r9.putObject(r14, r1, r0)
        L_0x0171:
            r5 = r0
            com.google.android.gms.internal.vision.zzen r0 = r15.zzag(r4)
            r1 = r17
            r2 = r29
            r3 = r8
            r19 = r4
            r4 = r31
            r6 = r32
            int r0 = zza((com.google.android.gms.internal.vision.zzen<?>) r0, (int) r1, (byte[]) r2, (int) r3, (int) r4, (com.google.android.gms.internal.vision.zzcw<?>) r5, (com.google.android.gms.internal.vision.zzbl) r6)
            r1 = r7
            r2 = r19
            goto L_0x0233
        L_0x018a:
            r19 = r4
            r24 = r7
            r15 = r8
            r18 = r9
        L_0x0191:
            r26 = -1
            goto L_0x01f8
        L_0x0195:
            r19 = r4
            r0 = 49
            if (r3 > r0) goto L_0x01cb
            long r4 = (long) r5
            r0 = r27
            r20 = r1
            r1 = r28
            r2 = r29
            r10 = r3
            r3 = r8
            r22 = r4
            r4 = r31
            r5 = r17
            r30 = r6
            r6 = r7
            r24 = r7
            r7 = r30
            r15 = r8
            r8 = r19
            r18 = r9
            r25 = r10
            r26 = -1
            r9 = r22
            r11 = r25
            r12 = r20
            r14 = r32
            int r0 = r0.zza(r1, (byte[]) r2, (int) r3, (int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (long) r9, (int) r11, (long) r12, (com.google.android.gms.internal.vision.zzbl) r14)
            if (r0 != r15) goto L_0x0223
            goto L_0x0214
        L_0x01cb:
            r20 = r1
            r25 = r3
            r30 = r6
            r24 = r7
            r15 = r8
            r18 = r9
            r26 = -1
            r0 = 50
            r9 = r25
            r7 = r30
            if (r9 != r0) goto L_0x01fa
            if (r7 != r10) goto L_0x01f8
            r0 = r27
            r1 = r28
            r2 = r29
            r3 = r15
            r4 = r31
            r5 = r19
            r6 = r20
            r8 = r32
            int r0 = r0.zza(r1, (byte[]) r2, (int) r3, (int) r4, (int) r5, (long) r6, (com.google.android.gms.internal.vision.zzbl) r8)
            if (r0 != r15) goto L_0x0223
            goto L_0x0214
        L_0x01f8:
            r2 = r15
            goto L_0x0215
        L_0x01fa:
            r0 = r27
            r1 = r28
            r2 = r29
            r3 = r15
            r4 = r31
            r8 = r5
            r5 = r17
            r6 = r24
            r10 = r20
            r12 = r19
            r13 = r32
            int r0 = r0.zza(r1, (byte[]) r2, (int) r3, (int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (long) r10, (int) r12, (com.google.android.gms.internal.vision.zzbl) r13)
            if (r0 != r15) goto L_0x0223
        L_0x0214:
            r2 = r0
        L_0x0215:
            r0 = r17
            r1 = r29
            r3 = r31
            r4 = r28
            r5 = r32
            int r0 = zza((int) r0, (byte[]) r1, (int) r2, (int) r3, (java.lang.Object) r4, (com.google.android.gms.internal.vision.zzbl) r5)
        L_0x0223:
            r15 = r27
            r14 = r28
            r12 = r29
            r13 = r31
            r11 = r32
            r9 = r18
            r2 = r19
            r1 = r24
        L_0x0233:
            r10 = -1
            goto L_0x0017
        L_0x0236:
            r4 = r13
            if (r0 != r4) goto L_0x023a
            return
        L_0x023a:
            com.google.android.gms.internal.vision.zzcx r0 = com.google.android.gms.internal.vision.zzcx.zzcf()
            throw r0
        L_0x023f:
            r4 = r13
            r5 = 0
            r0 = r27
            r1 = r28
            r2 = r29
            r3 = r30
            r4 = r31
            r6 = r32
            r0.zza(r1, (byte[]) r2, (int) r3, (int) r4, (int) r5, (com.google.android.gms.internal.vision.zzbl) r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zza(java.lang.Object, byte[], int, int, com.google.android.gms.internal.vision.zzbl):void");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0031, code lost:
        com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, r2, com.google.android.gms.internal.vision.zzfl.zzo(r8, r2));
        zzb(r7, r4, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0089, code lost:
        com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, r2, com.google.android.gms.internal.vision.zzfl.zzo(r8, r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00b3, code lost:
        com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, r2, com.google.android.gms.internal.vision.zzfl.zzj(r8, r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00c8, code lost:
        com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, r2, com.google.android.gms.internal.vision.zzfl.zzk(r8, r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00eb, code lost:
        zzb(r7, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00ee, code lost:
        r0 = r0 + 3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zzc(T r7, T r8) {
        /*
            r6 = this;
            if (r8 == 0) goto L_0x0105
            r0 = 0
        L_0x0003:
            int[] r1 = r6.zzne
            int r1 = r1.length
            if (r0 >= r1) goto L_0x00f2
            int r1 = r6.zzaj(r0)
            r2 = 1048575(0xfffff, float:1.469367E-39)
            r2 = r2 & r1
            long r2 = (long) r2
            int[] r4 = r6.zzne
            r4 = r4[r0]
            r5 = 267386880(0xff00000, float:2.3665827E-29)
            r1 = r1 & r5
            int r1 = r1 >>> 20
            switch(r1) {
                case 0: goto L_0x00de;
                case 1: goto L_0x00d0;
                case 2: goto L_0x00c2;
                case 3: goto L_0x00bb;
                case 4: goto L_0x00ad;
                case 5: goto L_0x00a6;
                case 6: goto L_0x009f;
                case 7: goto L_0x0091;
                case 8: goto L_0x0083;
                case 9: goto L_0x007e;
                case 10: goto L_0x0077;
                case 11: goto L_0x0070;
                case 12: goto L_0x0069;
                case 13: goto L_0x0062;
                case 14: goto L_0x005a;
                case 15: goto L_0x0053;
                case 16: goto L_0x004b;
                case 17: goto L_0x007e;
                case 18: goto L_0x0044;
                case 19: goto L_0x0044;
                case 20: goto L_0x0044;
                case 21: goto L_0x0044;
                case 22: goto L_0x0044;
                case 23: goto L_0x0044;
                case 24: goto L_0x0044;
                case 25: goto L_0x0044;
                case 26: goto L_0x0044;
                case 27: goto L_0x0044;
                case 28: goto L_0x0044;
                case 29: goto L_0x0044;
                case 30: goto L_0x0044;
                case 31: goto L_0x0044;
                case 32: goto L_0x0044;
                case 33: goto L_0x0044;
                case 34: goto L_0x0044;
                case 35: goto L_0x0044;
                case 36: goto L_0x0044;
                case 37: goto L_0x0044;
                case 38: goto L_0x0044;
                case 39: goto L_0x0044;
                case 40: goto L_0x0044;
                case 41: goto L_0x0044;
                case 42: goto L_0x0044;
                case 43: goto L_0x0044;
                case 44: goto L_0x0044;
                case 45: goto L_0x0044;
                case 46: goto L_0x0044;
                case 47: goto L_0x0044;
                case 48: goto L_0x0044;
                case 49: goto L_0x0044;
                case 50: goto L_0x003d;
                case 51: goto L_0x002b;
                case 52: goto L_0x002b;
                case 53: goto L_0x002b;
                case 54: goto L_0x002b;
                case 55: goto L_0x002b;
                case 56: goto L_0x002b;
                case 57: goto L_0x002b;
                case 58: goto L_0x002b;
                case 59: goto L_0x002b;
                case 60: goto L_0x0026;
                case 61: goto L_0x001f;
                case 62: goto L_0x001f;
                case 63: goto L_0x001f;
                case 64: goto L_0x001f;
                case 65: goto L_0x001f;
                case 66: goto L_0x001f;
                case 67: goto L_0x001f;
                case 68: goto L_0x0026;
                default: goto L_0x001d;
            }
        L_0x001d:
            goto L_0x00ee
        L_0x001f:
            boolean r1 = r6.zza(r8, (int) r4, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x0031
        L_0x0026:
            r6.zzb(r7, r8, (int) r0)
            goto L_0x00ee
        L_0x002b:
            boolean r1 = r6.zza(r8, (int) r4, (int) r0)
            if (r1 == 0) goto L_0x00ee
        L_0x0031:
            java.lang.Object r1 = com.google.android.gms.internal.vision.zzfl.zzo(r8, r2)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, (long) r2, (java.lang.Object) r1)
            r6.zzb(r7, (int) r4, (int) r0)
            goto L_0x00ee
        L_0x003d:
            com.google.android.gms.internal.vision.zzds r1 = r6.zznu
            com.google.android.gms.internal.vision.zzep.zza((com.google.android.gms.internal.vision.zzds) r1, r7, r8, (long) r2)
            goto L_0x00ee
        L_0x0044:
            com.google.android.gms.internal.vision.zzdh r1 = r6.zznr
            r1.zza(r7, r8, r2)
            goto L_0x00ee
        L_0x004b:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x00c8
        L_0x0053:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x006f
        L_0x005a:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x00c8
        L_0x0062:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x006f
        L_0x0069:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
        L_0x006f:
            goto L_0x00b3
        L_0x0070:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x00b3
        L_0x0077:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x0089
        L_0x007e:
            r6.zza(r7, r8, (int) r0)
            goto L_0x00ee
        L_0x0083:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
        L_0x0089:
            java.lang.Object r1 = com.google.android.gms.internal.vision.zzfl.zzo(r8, r2)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, (long) r2, (java.lang.Object) r1)
            goto L_0x00eb
        L_0x0091:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            boolean r1 = com.google.android.gms.internal.vision.zzfl.zzl(r8, r2)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, (long) r2, (boolean) r1)
            goto L_0x00eb
        L_0x009f:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x00b3
        L_0x00a6:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x00c8
        L_0x00ad:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
        L_0x00b3:
            int r1 = com.google.android.gms.internal.vision.zzfl.zzj(r8, r2)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, (long) r2, (int) r1)
            goto L_0x00eb
        L_0x00bb:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            goto L_0x00c8
        L_0x00c2:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
        L_0x00c8:
            long r4 = com.google.android.gms.internal.vision.zzfl.zzk(r8, r2)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, (long) r2, (long) r4)
            goto L_0x00eb
        L_0x00d0:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            float r1 = com.google.android.gms.internal.vision.zzfl.zzm(r8, r2)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, (long) r2, (float) r1)
            goto L_0x00eb
        L_0x00de:
            boolean r1 = r6.zza(r8, (int) r0)
            if (r1 == 0) goto L_0x00ee
            double r4 = com.google.android.gms.internal.vision.zzfl.zzn(r8, r2)
            com.google.android.gms.internal.vision.zzfl.zza((java.lang.Object) r7, (long) r2, (double) r4)
        L_0x00eb:
            r6.zzb(r7, (int) r0)
        L_0x00ee:
            int r0 = r0 + 3
            goto L_0x0003
        L_0x00f2:
            boolean r0 = r6.zznl
            if (r0 != 0) goto L_0x0104
            com.google.android.gms.internal.vision.zzff<?, ?> r0 = r6.zzns
            com.google.android.gms.internal.vision.zzep.zza(r0, r7, r8)
            boolean r0 = r6.zznj
            if (r0 == 0) goto L_0x0104
            com.google.android.gms.internal.vision.zzcg<?> r0 = r6.zznt
            com.google.android.gms.internal.vision.zzep.zza(r0, r7, r8)
        L_0x0104:
            return
        L_0x0105:
            r7 = 0
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zzc(java.lang.Object, java.lang.Object):void");
    }

    public final void zzd(T t) {
        int i;
        int i2 = this.zzno;
        while (true) {
            i = this.zznp;
            if (i2 >= i) {
                break;
            }
            long zzaj = (long) (zzaj(this.zznn[i2]) & 1048575);
            Object zzo = zzfl.zzo(t, zzaj);
            if (zzo != null) {
                zzfl.zza((Object) t, zzaj, this.zznu.zzk(zzo));
            }
            i2++;
        }
        int length = this.zznn.length;
        while (i < length) {
            this.zznr.zza(t, (long) this.zznn[i]);
            i++;
        }
        this.zzns.zzd(t);
        if (this.zznj) {
            this.zznt.zzd(t);
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x01d8, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x01e9, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x01fa, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x020b, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x020d, code lost:
        r2.putInt(r1, (long) r14, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x0211, code lost:
        r3 = (com.google.android.gms.internal.vision.zzca.zzt(r3) + com.google.android.gms.internal.vision.zzca.zzv(r5)) + r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0296, code lost:
        r13 = r13 + r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x029f, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzc(r3, (com.google.android.gms.internal.vision.zzdx) com.google.android.gms.internal.vision.zzfl.zzo(r1, r5), zzag(r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x02b8, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzf(r3, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x02c7, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzk(r3, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x02d2, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzh(r3, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x02dd, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzm(r3, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x02ec, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzn(r3, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x02fb, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzj(r3, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x0306, code lost:
        r5 = com.google.android.gms.internal.vision.zzfl.zzo(r1, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:0x030a, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzc(r3, (com.google.android.gms.internal.vision.zzbo) r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x0317, code lost:
        r3 = com.google.android.gms.internal.vision.zzep.zzc(r3, com.google.android.gms.internal.vision.zzfl.zzo(r1, r5), zzag(r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x0331, code lost:
        if ((r5 instanceof com.google.android.gms.internal.vision.zzbo) != false) goto L_0x030a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x0334, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzb(r3, (java.lang.String) r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x0342, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzc(r3, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x034e, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzl(r3, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x035a, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzg(r3, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x036a, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzi(r3, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x037a, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zze(r3, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x038a, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzd(r3, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x0396, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzb(r3, 0.0f);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x03a2, code lost:
        r3 = com.google.android.gms.internal.vision.zzca.zzb(r3, (double) com.google.firebase.remoteconfig.FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x03aa, code lost:
        r12 = r12 + 3;
        r3 = 267386880;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x0414, code lost:
        if (zza(r1, r15, r5) != false) goto L_0x06b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x0434, code lost:
        if (zza(r1, r15, r5) != false) goto L_0x06e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x043c, code lost:
        if (zza(r1, r15, r5) != false) goto L_0x06ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x045c, code lost:
        if (zza(r1, r15, r5) != false) goto L_0x0713;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0464, code lost:
        if (zza(r1, r15, r5) != false) goto L_0x0722;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x0474, code lost:
        if ((r4 instanceof com.google.android.gms.internal.vision.zzbo) != false) goto L_0x0717;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x047c, code lost:
        if (zza(r1, r15, r5) != false) goto L_0x0749;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0514, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0526, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0538, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x054a, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x055c, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x056e, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x0580, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0592, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x05a3, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x05b4, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05c5, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x05d6, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x05e7, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x05f8, code lost:
        if (r0.zznm != false) goto L_0x05fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x05fa, code lost:
        r2.putInt(r1, (long) r9, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x05fe, code lost:
        r9 = (com.google.android.gms.internal.vision.zzca.zzt(r15) + com.google.android.gms.internal.vision.zzca.zzv(r4)) + r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x06a9, code lost:
        r6 = r6 + r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x06ab, code lost:
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x06b4, code lost:
        if ((r12 & r18) != 0) goto L_0x06b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x06b6, code lost:
        r4 = com.google.android.gms.internal.vision.zzca.zzc(r15, (com.google.android.gms.internal.vision.zzdx) r2.getObject(r1, r10), zzag(r5));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x06cd, code lost:
        r4 = com.google.android.gms.internal.vision.zzca.zzf(r15, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x06da, code lost:
        r4 = com.google.android.gms.internal.vision.zzca.zzk(r15, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x06e1, code lost:
        if ((r12 & r18) != 0) goto L_0x06e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x06e3, code lost:
        r4 = com.google.android.gms.internal.vision.zzca.zzh(r15, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x06ec, code lost:
        if ((r12 & r18) != 0) goto L_0x06ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x06ee, code lost:
        r9 = com.google.android.gms.internal.vision.zzca.zzm(r15, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x06f3, code lost:
        r6 = r6 + r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x06fd, code lost:
        r4 = com.google.android.gms.internal.vision.zzca.zzn(r15, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x070a, code lost:
        r4 = com.google.android.gms.internal.vision.zzca.zzj(r15, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x0711, code lost:
        if ((r12 & r18) != 0) goto L_0x0713;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x0713, code lost:
        r4 = r2.getObject(r1, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x0717, code lost:
        r4 = com.google.android.gms.internal.vision.zzca.zzc(r15, (com.google.android.gms.internal.vision.zzbo) r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x0720, code lost:
        if ((r12 & r18) != 0) goto L_0x0722;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x0722, code lost:
        r4 = com.google.android.gms.internal.vision.zzep.zzc(r15, r2.getObject(r1, r10), zzag(r5));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x073a, code lost:
        if ((r4 instanceof com.google.android.gms.internal.vision.zzbo) != false) goto L_0x0717;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00ab, code lost:
        if ((r5 instanceof com.google.android.gms.internal.vision.zzbo) != false) goto L_0x030a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x073d, code lost:
        r4 = com.google.android.gms.internal.vision.zzca.zzb(r15, (java.lang.String) r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x0747, code lost:
        if ((r12 & r18) != 0) goto L_0x0749;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x0749, code lost:
        r4 = com.google.android.gms.internal.vision.zzca.zzc(r15, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0796, code lost:
        r6 = r6 + r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x07b8, code lost:
        r5 = r5 + 3;
        r9 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0127, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0139, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x014b, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x015d, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x016f, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0181, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0193, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x01a5, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01b6, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01c7, code lost:
        if (r0.zznm != false) goto L_0x020d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final int zzn(T r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            boolean r2 = r0.zznl
            r3 = 267386880(0xff00000, float:2.3665827E-29)
            r4 = 0
            r7 = 1
            r8 = 1048575(0xfffff, float:1.469367E-39)
            r9 = 0
            r11 = 0
            if (r2 == 0) goto L_0x03b8
            sun.misc.Unsafe r2 = zznd
            r12 = 0
            r13 = 0
        L_0x0016:
            int[] r14 = r0.zzne
            int r14 = r14.length
            if (r12 >= r14) goto L_0x03b0
            int r14 = r0.zzaj(r12)
            r15 = r14 & r3
            int r15 = r15 >>> 20
            int[] r3 = r0.zzne
            r3 = r3[r12]
            r14 = r14 & r8
            long r5 = (long) r14
            com.google.android.gms.internal.vision.zzcm r14 = com.google.android.gms.internal.vision.zzcm.DOUBLE_LIST_PACKED
            int r14 = r14.id()
            if (r15 < r14) goto L_0x0041
            com.google.android.gms.internal.vision.zzcm r14 = com.google.android.gms.internal.vision.zzcm.SINT64_LIST_PACKED
            int r14 = r14.id()
            if (r15 > r14) goto L_0x0041
            int[] r14 = r0.zzne
            int r17 = r12 + 2
            r14 = r14[r17]
            r14 = r14 & r8
            goto L_0x0042
        L_0x0041:
            r14 = 0
        L_0x0042:
            switch(r15) {
                case 0: goto L_0x039c;
                case 1: goto L_0x0390;
                case 2: goto L_0x0380;
                case 3: goto L_0x0370;
                case 4: goto L_0x0360;
                case 5: goto L_0x0354;
                case 6: goto L_0x0348;
                case 7: goto L_0x033c;
                case 8: goto L_0x0325;
                case 9: goto L_0x0311;
                case 10: goto L_0x0300;
                case 11: goto L_0x02f1;
                case 12: goto L_0x02e2;
                case 13: goto L_0x02d7;
                case 14: goto L_0x02cc;
                case 15: goto L_0x02bd;
                case 16: goto L_0x02ae;
                case 17: goto L_0x0299;
                case 18: goto L_0x028e;
                case 19: goto L_0x0285;
                case 20: goto L_0x027c;
                case 21: goto L_0x0273;
                case 22: goto L_0x026a;
                case 23: goto L_0x028e;
                case 24: goto L_0x0285;
                case 25: goto L_0x0261;
                case 26: goto L_0x0258;
                case 27: goto L_0x024b;
                case 28: goto L_0x0242;
                case 29: goto L_0x0239;
                case 30: goto L_0x0230;
                case 31: goto L_0x0285;
                case 32: goto L_0x028e;
                case 33: goto L_0x0227;
                case 34: goto L_0x021d;
                case 35: goto L_0x01fd;
                case 36: goto L_0x01ec;
                case 37: goto L_0x01db;
                case 38: goto L_0x01ca;
                case 39: goto L_0x01b9;
                case 40: goto L_0x01a8;
                case 41: goto L_0x0197;
                case 42: goto L_0x0185;
                case 43: goto L_0x0173;
                case 44: goto L_0x0161;
                case 45: goto L_0x014f;
                case 46: goto L_0x013d;
                case 47: goto L_0x012b;
                case 48: goto L_0x0119;
                case 49: goto L_0x010b;
                case 50: goto L_0x00fb;
                case 51: goto L_0x00f3;
                case 52: goto L_0x00eb;
                case 53: goto L_0x00df;
                case 54: goto L_0x00d3;
                case 55: goto L_0x00c7;
                case 56: goto L_0x00bf;
                case 57: goto L_0x00b7;
                case 58: goto L_0x00af;
                case 59: goto L_0x009f;
                case 60: goto L_0x0097;
                case 61: goto L_0x008f;
                case 62: goto L_0x0083;
                case 63: goto L_0x0077;
                case 64: goto L_0x006f;
                case 65: goto L_0x0067;
                case 66: goto L_0x005b;
                case 67: goto L_0x004f;
                case 68: goto L_0x0047;
                default: goto L_0x0045;
            }
        L_0x0045:
            goto L_0x03aa
        L_0x0047:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            goto L_0x029f
        L_0x004f:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            long r5 = zzh(r1, r5)
            goto L_0x02b8
        L_0x005b:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            int r5 = zzg(r1, r5)
            goto L_0x02c7
        L_0x0067:
            boolean r5 = r0.zza(r1, (int) r3, (int) r12)
            if (r5 == 0) goto L_0x03aa
            goto L_0x02d2
        L_0x006f:
            boolean r5 = r0.zza(r1, (int) r3, (int) r12)
            if (r5 == 0) goto L_0x03aa
            goto L_0x02dd
        L_0x0077:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            int r5 = zzg(r1, r5)
            goto L_0x02ec
        L_0x0083:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            int r5 = zzg(r1, r5)
            goto L_0x02fb
        L_0x008f:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            goto L_0x0306
        L_0x0097:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            goto L_0x0317
        L_0x009f:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r1, r5)
            boolean r6 = r5 instanceof com.google.android.gms.internal.vision.zzbo
            if (r6 == 0) goto L_0x0334
            goto L_0x0333
        L_0x00af:
            boolean r5 = r0.zza(r1, (int) r3, (int) r12)
            if (r5 == 0) goto L_0x03aa
            goto L_0x0342
        L_0x00b7:
            boolean r5 = r0.zza(r1, (int) r3, (int) r12)
            if (r5 == 0) goto L_0x03aa
            goto L_0x034e
        L_0x00bf:
            boolean r5 = r0.zza(r1, (int) r3, (int) r12)
            if (r5 == 0) goto L_0x03aa
            goto L_0x035a
        L_0x00c7:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            int r5 = zzg(r1, r5)
            goto L_0x036a
        L_0x00d3:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            long r5 = zzh(r1, r5)
            goto L_0x037a
        L_0x00df:
            boolean r14 = r0.zza(r1, (int) r3, (int) r12)
            if (r14 == 0) goto L_0x03aa
            long r5 = zzh(r1, r5)
            goto L_0x038a
        L_0x00eb:
            boolean r5 = r0.zza(r1, (int) r3, (int) r12)
            if (r5 == 0) goto L_0x03aa
            goto L_0x0396
        L_0x00f3:
            boolean r5 = r0.zza(r1, (int) r3, (int) r12)
            if (r5 == 0) goto L_0x03aa
            goto L_0x03a2
        L_0x00fb:
            com.google.android.gms.internal.vision.zzds r14 = r0.zznu
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r1, r5)
            java.lang.Object r6 = r0.zzah(r12)
            int r3 = r14.zzb(r3, r5, r6)
            goto L_0x0296
        L_0x010b:
            java.util.List r5 = zzd(r1, r5)
            com.google.android.gms.internal.vision.zzen r6 = r0.zzag(r12)
            int r3 = com.google.android.gms.internal.vision.zzep.zzd(r3, r5, r6)
            goto L_0x0296
        L_0x0119:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzc(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x012b:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzg(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x013d:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzi(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x014f:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzh(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x0161:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzd((java.util.List<java.lang.Integer>) r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x0173:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzf((java.util.List<java.lang.Integer>) r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x0185:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzj(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x0197:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzh(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x01a8:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzi(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x01b9:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zze(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x01ca:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzb(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x01db:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zza(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x01ec:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzh(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
            goto L_0x020d
        L_0x01fd:
            java.lang.Object r5 = r2.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            int r5 = com.google.android.gms.internal.vision.zzep.zzi(r5)
            if (r5 <= 0) goto L_0x03aa
            boolean r6 = r0.zznm
            if (r6 == 0) goto L_0x0211
        L_0x020d:
            long r14 = (long) r14
            r2.putInt(r1, r14, r5)
        L_0x0211:
            int r3 = com.google.android.gms.internal.vision.zzca.zzt(r3)
            int r6 = com.google.android.gms.internal.vision.zzca.zzv(r5)
            int r3 = r3 + r6
            int r3 = r3 + r5
            goto L_0x0296
        L_0x021d:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzq(r3, r5, r11)
            goto L_0x0296
        L_0x0227:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzu(r3, r5, r11)
            goto L_0x0296
        L_0x0230:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzr(r3, r5, r11)
            goto L_0x0296
        L_0x0239:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzt(r3, r5, r11)
            goto L_0x0296
        L_0x0242:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzd((int) r3, (java.util.List<com.google.android.gms.internal.vision.zzbo>) r5)
            goto L_0x0296
        L_0x024b:
            java.util.List r5 = zzd(r1, r5)
            com.google.android.gms.internal.vision.zzen r6 = r0.zzag(r12)
            int r3 = com.google.android.gms.internal.vision.zzep.zzc((int) r3, (java.util.List<?>) r5, (com.google.android.gms.internal.vision.zzen) r6)
            goto L_0x0296
        L_0x0258:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzc(r3, r5)
            goto L_0x0296
        L_0x0261:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzx(r3, r5, r11)
            goto L_0x0296
        L_0x026a:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzs(r3, r5, r11)
            goto L_0x0296
        L_0x0273:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzp(r3, r5, r11)
            goto L_0x0296
        L_0x027c:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzo(r3, r5, r11)
            goto L_0x0296
        L_0x0285:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzv(r3, r5, r11)
            goto L_0x0296
        L_0x028e:
            java.util.List r5 = zzd(r1, r5)
            int r3 = com.google.android.gms.internal.vision.zzep.zzw(r3, r5, r11)
        L_0x0296:
            int r13 = r13 + r3
            goto L_0x03aa
        L_0x0299:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
        L_0x029f:
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r1, r5)
            com.google.android.gms.internal.vision.zzdx r5 = (com.google.android.gms.internal.vision.zzdx) r5
            com.google.android.gms.internal.vision.zzen r6 = r0.zzag(r12)
            int r3 = com.google.android.gms.internal.vision.zzca.zzc(r3, r5, r6)
            goto L_0x0296
        L_0x02ae:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
            long r5 = com.google.android.gms.internal.vision.zzfl.zzk(r1, r5)
        L_0x02b8:
            int r3 = com.google.android.gms.internal.vision.zzca.zzf((int) r3, (long) r5)
            goto L_0x0296
        L_0x02bd:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r1, r5)
        L_0x02c7:
            int r3 = com.google.android.gms.internal.vision.zzca.zzk(r3, r5)
            goto L_0x0296
        L_0x02cc:
            boolean r5 = r0.zza(r1, (int) r12)
            if (r5 == 0) goto L_0x03aa
        L_0x02d2:
            int r3 = com.google.android.gms.internal.vision.zzca.zzh((int) r3, (long) r9)
            goto L_0x0296
        L_0x02d7:
            boolean r5 = r0.zza(r1, (int) r12)
            if (r5 == 0) goto L_0x03aa
        L_0x02dd:
            int r3 = com.google.android.gms.internal.vision.zzca.zzm(r3, r11)
            goto L_0x0296
        L_0x02e2:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r1, r5)
        L_0x02ec:
            int r3 = com.google.android.gms.internal.vision.zzca.zzn(r3, r5)
            goto L_0x0296
        L_0x02f1:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r1, r5)
        L_0x02fb:
            int r3 = com.google.android.gms.internal.vision.zzca.zzj(r3, r5)
            goto L_0x0296
        L_0x0300:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
        L_0x0306:
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r1, r5)
        L_0x030a:
            com.google.android.gms.internal.vision.zzbo r5 = (com.google.android.gms.internal.vision.zzbo) r5
            int r3 = com.google.android.gms.internal.vision.zzca.zzc((int) r3, (com.google.android.gms.internal.vision.zzbo) r5)
            goto L_0x0296
        L_0x0311:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
        L_0x0317:
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r1, r5)
            com.google.android.gms.internal.vision.zzen r6 = r0.zzag(r12)
            int r3 = com.google.android.gms.internal.vision.zzep.zzc((int) r3, (java.lang.Object) r5, (com.google.android.gms.internal.vision.zzen) r6)
            goto L_0x0296
        L_0x0325:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
            java.lang.Object r5 = com.google.android.gms.internal.vision.zzfl.zzo(r1, r5)
            boolean r6 = r5 instanceof com.google.android.gms.internal.vision.zzbo
            if (r6 == 0) goto L_0x0334
        L_0x0333:
            goto L_0x030a
        L_0x0334:
            java.lang.String r5 = (java.lang.String) r5
            int r3 = com.google.android.gms.internal.vision.zzca.zzb((int) r3, (java.lang.String) r5)
            goto L_0x0296
        L_0x033c:
            boolean r5 = r0.zza(r1, (int) r12)
            if (r5 == 0) goto L_0x03aa
        L_0x0342:
            int r3 = com.google.android.gms.internal.vision.zzca.zzc((int) r3, (boolean) r7)
            goto L_0x0296
        L_0x0348:
            boolean r5 = r0.zza(r1, (int) r12)
            if (r5 == 0) goto L_0x03aa
        L_0x034e:
            int r3 = com.google.android.gms.internal.vision.zzca.zzl(r3, r11)
            goto L_0x0296
        L_0x0354:
            boolean r5 = r0.zza(r1, (int) r12)
            if (r5 == 0) goto L_0x03aa
        L_0x035a:
            int r3 = com.google.android.gms.internal.vision.zzca.zzg((int) r3, (long) r9)
            goto L_0x0296
        L_0x0360:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
            int r5 = com.google.android.gms.internal.vision.zzfl.zzj(r1, r5)
        L_0x036a:
            int r3 = com.google.android.gms.internal.vision.zzca.zzi(r3, r5)
            goto L_0x0296
        L_0x0370:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
            long r5 = com.google.android.gms.internal.vision.zzfl.zzk(r1, r5)
        L_0x037a:
            int r3 = com.google.android.gms.internal.vision.zzca.zze((int) r3, (long) r5)
            goto L_0x0296
        L_0x0380:
            boolean r14 = r0.zza(r1, (int) r12)
            if (r14 == 0) goto L_0x03aa
            long r5 = com.google.android.gms.internal.vision.zzfl.zzk(r1, r5)
        L_0x038a:
            int r3 = com.google.android.gms.internal.vision.zzca.zzd((int) r3, (long) r5)
            goto L_0x0296
        L_0x0390:
            boolean r5 = r0.zza(r1, (int) r12)
            if (r5 == 0) goto L_0x03aa
        L_0x0396:
            int r3 = com.google.android.gms.internal.vision.zzca.zzb((int) r3, (float) r4)
            goto L_0x0296
        L_0x039c:
            boolean r5 = r0.zza(r1, (int) r12)
            if (r5 == 0) goto L_0x03aa
        L_0x03a2:
            r5 = 0
            int r3 = com.google.android.gms.internal.vision.zzca.zzb((int) r3, (double) r5)
            goto L_0x0296
        L_0x03aa:
            int r12 = r12 + 3
            r3 = 267386880(0xff00000, float:2.3665827E-29)
            goto L_0x0016
        L_0x03b0:
            com.google.android.gms.internal.vision.zzff<?, ?> r2 = r0.zzns
            int r1 = zza(r2, r1)
            int r13 = r13 + r1
            return r13
        L_0x03b8:
            sun.misc.Unsafe r2 = zznd
            r3 = -1
            r5 = 0
            r6 = 0
            r12 = 0
        L_0x03be:
            int[] r13 = r0.zzne
            int r13 = r13.length
            if (r5 >= r13) goto L_0x07bf
            int r13 = r0.zzaj(r5)
            int[] r14 = r0.zzne
            r15 = r14[r5]
            r16 = 267386880(0xff00000, float:2.3665827E-29)
            r17 = r13 & r16
            int r4 = r17 >>> 20
            r11 = 17
            if (r4 > r11) goto L_0x03e9
            int r11 = r5 + 2
            r11 = r14[r11]
            r14 = r11 & r8
            int r18 = r11 >>> 20
            int r18 = r7 << r18
            if (r14 == r3) goto L_0x03e7
            long r9 = (long) r14
            int r12 = r2.getInt(r1, r9)
            r3 = r14
        L_0x03e7:
            r9 = r11
            goto L_0x0408
        L_0x03e9:
            boolean r9 = r0.zznm
            if (r9 == 0) goto L_0x0405
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.DOUBLE_LIST_PACKED
            int r9 = r9.id()
            if (r4 < r9) goto L_0x0405
            com.google.android.gms.internal.vision.zzcm r9 = com.google.android.gms.internal.vision.zzcm.SINT64_LIST_PACKED
            int r9 = r9.id()
            if (r4 > r9) goto L_0x0405
            int[] r9 = r0.zzne
            int r10 = r5 + 2
            r9 = r9[r10]
            r9 = r9 & r8
            goto L_0x0406
        L_0x0405:
            r9 = 0
        L_0x0406:
            r18 = 0
        L_0x0408:
            r10 = r13 & r8
            long r10 = (long) r10
            switch(r4) {
                case 0: goto L_0x07a9;
                case 1: goto L_0x0799;
                case 2: goto L_0x0787;
                case 3: goto L_0x0777;
                case 4: goto L_0x0767;
                case 5: goto L_0x075b;
                case 6: goto L_0x074f;
                case 7: goto L_0x0745;
                case 8: goto L_0x0730;
                case 9: goto L_0x071e;
                case 10: goto L_0x070f;
                case 11: goto L_0x0702;
                case 12: goto L_0x06f5;
                case 13: goto L_0x06ea;
                case 14: goto L_0x06df;
                case 15: goto L_0x06d2;
                case 16: goto L_0x06c5;
                case 17: goto L_0x06b2;
                case 18: goto L_0x069e;
                case 19: goto L_0x0692;
                case 20: goto L_0x0686;
                case 21: goto L_0x067a;
                case 22: goto L_0x066e;
                case 23: goto L_0x069e;
                case 24: goto L_0x0692;
                case 25: goto L_0x0662;
                case 26: goto L_0x0657;
                case 27: goto L_0x0648;
                case 28: goto L_0x063d;
                case 29: goto L_0x0631;
                case 30: goto L_0x0624;
                case 31: goto L_0x0692;
                case 32: goto L_0x069e;
                case 33: goto L_0x0617;
                case 34: goto L_0x060a;
                case 35: goto L_0x05ea;
                case 36: goto L_0x05d9;
                case 37: goto L_0x05c8;
                case 38: goto L_0x05b7;
                case 39: goto L_0x05a6;
                case 40: goto L_0x0595;
                case 41: goto L_0x0584;
                case 42: goto L_0x0572;
                case 43: goto L_0x0560;
                case 44: goto L_0x054e;
                case 45: goto L_0x053c;
                case 46: goto L_0x052a;
                case 47: goto L_0x0518;
                case 48: goto L_0x0506;
                case 49: goto L_0x04f6;
                case 50: goto L_0x04e6;
                case 51: goto L_0x04d8;
                case 52: goto L_0x04cb;
                case 53: goto L_0x04bb;
                case 54: goto L_0x04ab;
                case 55: goto L_0x049b;
                case 56: goto L_0x048d;
                case 57: goto L_0x0480;
                case 58: goto L_0x0478;
                case 59: goto L_0x0468;
                case 60: goto L_0x0460;
                case 61: goto L_0x0458;
                case 62: goto L_0x044c;
                case 63: goto L_0x0440;
                case 64: goto L_0x0438;
                case 65: goto L_0x0430;
                case 66: goto L_0x0424;
                case 67: goto L_0x0418;
                case 68: goto L_0x0410;
                default: goto L_0x040e;
            }
        L_0x040e:
            goto L_0x06aa
        L_0x0410:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            goto L_0x06b6
        L_0x0418:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            long r9 = zzh(r1, r10)
            goto L_0x06cd
        L_0x0424:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            int r4 = zzg(r1, r10)
            goto L_0x06da
        L_0x0430:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            goto L_0x06e3
        L_0x0438:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            goto L_0x06ee
        L_0x0440:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            int r4 = zzg(r1, r10)
            goto L_0x06fd
        L_0x044c:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            int r4 = zzg(r1, r10)
            goto L_0x070a
        L_0x0458:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            goto L_0x0713
        L_0x0460:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            goto L_0x0722
        L_0x0468:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            java.lang.Object r4 = r2.getObject(r1, r10)
            boolean r9 = r4 instanceof com.google.android.gms.internal.vision.zzbo
            if (r9 == 0) goto L_0x073d
            goto L_0x073c
        L_0x0478:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            goto L_0x0749
        L_0x0480:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            r4 = 0
            int r9 = com.google.android.gms.internal.vision.zzca.zzl(r15, r4)
            goto L_0x06f3
        L_0x048d:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            r9 = 0
            int r4 = com.google.android.gms.internal.vision.zzca.zzg((int) r15, (long) r9)
            goto L_0x06a9
        L_0x049b:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            int r4 = zzg(r1, r10)
            int r4 = com.google.android.gms.internal.vision.zzca.zzi(r15, r4)
            goto L_0x06a9
        L_0x04ab:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            long r9 = zzh(r1, r10)
            int r4 = com.google.android.gms.internal.vision.zzca.zze((int) r15, (long) r9)
            goto L_0x06a9
        L_0x04bb:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            long r9 = zzh(r1, r10)
            int r4 = com.google.android.gms.internal.vision.zzca.zzd((int) r15, (long) r9)
            goto L_0x06a9
        L_0x04cb:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            r4 = 0
            int r9 = com.google.android.gms.internal.vision.zzca.zzb((int) r15, (float) r4)
            goto L_0x06f3
        L_0x04d8:
            boolean r4 = r0.zza(r1, (int) r15, (int) r5)
            if (r4 == 0) goto L_0x06aa
            r9 = 0
            int r4 = com.google.android.gms.internal.vision.zzca.zzb((int) r15, (double) r9)
            goto L_0x06a9
        L_0x04e6:
            com.google.android.gms.internal.vision.zzds r4 = r0.zznu
            java.lang.Object r9 = r2.getObject(r1, r10)
            java.lang.Object r10 = r0.zzah(r5)
            int r4 = r4.zzb(r15, r9, r10)
            goto L_0x06a9
        L_0x04f6:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            com.google.android.gms.internal.vision.zzen r9 = r0.zzag(r5)
            int r4 = com.google.android.gms.internal.vision.zzep.zzd(r15, r4, r9)
            goto L_0x06a9
        L_0x0506:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzc(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x0518:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzg(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x052a:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzi(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x053c:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzh(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x054e:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzd((java.util.List<java.lang.Integer>) r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x0560:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzf((java.util.List<java.lang.Integer>) r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x0572:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzj(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x0584:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzh(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x0595:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzi(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x05a6:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zze(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x05b7:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzb(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x05c8:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zza(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x05d9:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzh(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
            goto L_0x05fa
        L_0x05ea:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzi(r4)
            if (r4 <= 0) goto L_0x06aa
            boolean r10 = r0.zznm
            if (r10 == 0) goto L_0x05fe
        L_0x05fa:
            long r9 = (long) r9
            r2.putInt(r1, r9, r4)
        L_0x05fe:
            int r9 = com.google.android.gms.internal.vision.zzca.zzt(r15)
            int r10 = com.google.android.gms.internal.vision.zzca.zzv(r4)
            int r9 = r9 + r10
            int r9 = r9 + r4
            goto L_0x06f3
        L_0x060a:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            r9 = 0
            int r4 = com.google.android.gms.internal.vision.zzep.zzq(r15, r4, r9)
            goto L_0x06a9
        L_0x0617:
            r9 = 0
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzu(r15, r4, r9)
            goto L_0x06a9
        L_0x0624:
            r9 = 0
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzr(r15, r4, r9)
            goto L_0x06a9
        L_0x0631:
            r9 = 0
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzt(r15, r4, r9)
            goto L_0x06a9
        L_0x063d:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzd((int) r15, (java.util.List<com.google.android.gms.internal.vision.zzbo>) r4)
            goto L_0x06a9
        L_0x0648:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            com.google.android.gms.internal.vision.zzen r9 = r0.zzag(r5)
            int r4 = com.google.android.gms.internal.vision.zzep.zzc((int) r15, (java.util.List<?>) r4, (com.google.android.gms.internal.vision.zzen) r9)
            goto L_0x06a9
        L_0x0657:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzc(r15, r4)
            goto L_0x06a9
        L_0x0662:
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            r9 = 0
            int r4 = com.google.android.gms.internal.vision.zzep.zzx(r15, r4, r9)
            goto L_0x06a9
        L_0x066e:
            r9 = 0
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzs(r15, r4, r9)
            goto L_0x06a9
        L_0x067a:
            r9 = 0
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzp(r15, r4, r9)
            goto L_0x06a9
        L_0x0686:
            r9 = 0
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzo(r15, r4, r9)
            goto L_0x06a9
        L_0x0692:
            r9 = 0
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzv(r15, r4, r9)
            goto L_0x06a9
        L_0x069e:
            r9 = 0
            java.lang.Object r4 = r2.getObject(r1, r10)
            java.util.List r4 = (java.util.List) r4
            int r4 = com.google.android.gms.internal.vision.zzep.zzw(r15, r4, r9)
        L_0x06a9:
            int r6 = r6 + r4
        L_0x06aa:
            r4 = 0
        L_0x06ab:
            r9 = 0
            r10 = 0
            r13 = 0
            goto L_0x07b8
        L_0x06b2:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
        L_0x06b6:
            java.lang.Object r4 = r2.getObject(r1, r10)
            com.google.android.gms.internal.vision.zzdx r4 = (com.google.android.gms.internal.vision.zzdx) r4
            com.google.android.gms.internal.vision.zzen r9 = r0.zzag(r5)
            int r4 = com.google.android.gms.internal.vision.zzca.zzc(r15, r4, r9)
            goto L_0x06a9
        L_0x06c5:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
            long r9 = r2.getLong(r1, r10)
        L_0x06cd:
            int r4 = com.google.android.gms.internal.vision.zzca.zzf((int) r15, (long) r9)
            goto L_0x06a9
        L_0x06d2:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
            int r4 = r2.getInt(r1, r10)
        L_0x06da:
            int r4 = com.google.android.gms.internal.vision.zzca.zzk(r15, r4)
            goto L_0x06a9
        L_0x06df:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
        L_0x06e3:
            r9 = 0
            int r4 = com.google.android.gms.internal.vision.zzca.zzh((int) r15, (long) r9)
            goto L_0x06a9
        L_0x06ea:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
        L_0x06ee:
            r4 = 0
            int r9 = com.google.android.gms.internal.vision.zzca.zzm(r15, r4)
        L_0x06f3:
            int r6 = r6 + r9
            goto L_0x06aa
        L_0x06f5:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
            int r4 = r2.getInt(r1, r10)
        L_0x06fd:
            int r4 = com.google.android.gms.internal.vision.zzca.zzn(r15, r4)
            goto L_0x06a9
        L_0x0702:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
            int r4 = r2.getInt(r1, r10)
        L_0x070a:
            int r4 = com.google.android.gms.internal.vision.zzca.zzj(r15, r4)
            goto L_0x06a9
        L_0x070f:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
        L_0x0713:
            java.lang.Object r4 = r2.getObject(r1, r10)
        L_0x0717:
            com.google.android.gms.internal.vision.zzbo r4 = (com.google.android.gms.internal.vision.zzbo) r4
            int r4 = com.google.android.gms.internal.vision.zzca.zzc((int) r15, (com.google.android.gms.internal.vision.zzbo) r4)
            goto L_0x06a9
        L_0x071e:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
        L_0x0722:
            java.lang.Object r4 = r2.getObject(r1, r10)
            com.google.android.gms.internal.vision.zzen r9 = r0.zzag(r5)
            int r4 = com.google.android.gms.internal.vision.zzep.zzc((int) r15, (java.lang.Object) r4, (com.google.android.gms.internal.vision.zzen) r9)
            goto L_0x06a9
        L_0x0730:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
            java.lang.Object r4 = r2.getObject(r1, r10)
            boolean r9 = r4 instanceof com.google.android.gms.internal.vision.zzbo
            if (r9 == 0) goto L_0x073d
        L_0x073c:
            goto L_0x0717
        L_0x073d:
            java.lang.String r4 = (java.lang.String) r4
            int r4 = com.google.android.gms.internal.vision.zzca.zzb((int) r15, (java.lang.String) r4)
            goto L_0x06a9
        L_0x0745:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
        L_0x0749:
            int r4 = com.google.android.gms.internal.vision.zzca.zzc((int) r15, (boolean) r7)
            goto L_0x06a9
        L_0x074f:
            r4 = r12 & r18
            if (r4 == 0) goto L_0x06aa
            r4 = 0
            int r9 = com.google.android.gms.internal.vision.zzca.zzl(r15, r4)
            int r6 = r6 + r9
            goto L_0x06ab
        L_0x075b:
            r4 = 0
            r9 = r12 & r18
            r13 = 0
            if (r9 == 0) goto L_0x0797
            int r9 = com.google.android.gms.internal.vision.zzca.zzg((int) r15, (long) r13)
            goto L_0x0796
        L_0x0767:
            r4 = 0
            r13 = 0
            r9 = r12 & r18
            if (r9 == 0) goto L_0x0797
            int r9 = r2.getInt(r1, r10)
            int r9 = com.google.android.gms.internal.vision.zzca.zzi(r15, r9)
            goto L_0x0796
        L_0x0777:
            r4 = 0
            r13 = 0
            r9 = r12 & r18
            if (r9 == 0) goto L_0x0797
            long r9 = r2.getLong(r1, r10)
            int r9 = com.google.android.gms.internal.vision.zzca.zze((int) r15, (long) r9)
            goto L_0x0796
        L_0x0787:
            r4 = 0
            r13 = 0
            r9 = r12 & r18
            if (r9 == 0) goto L_0x0797
            long r9 = r2.getLong(r1, r10)
            int r9 = com.google.android.gms.internal.vision.zzca.zzd((int) r15, (long) r9)
        L_0x0796:
            int r6 = r6 + r9
        L_0x0797:
            r9 = 0
            goto L_0x07a6
        L_0x0799:
            r4 = 0
            r13 = 0
            r9 = r12 & r18
            if (r9 == 0) goto L_0x0797
            r9 = 0
            int r10 = com.google.android.gms.internal.vision.zzca.zzb((int) r15, (float) r9)
            int r6 = r6 + r10
        L_0x07a6:
            r10 = 0
            goto L_0x07b8
        L_0x07a9:
            r4 = 0
            r9 = 0
            r13 = 0
            r10 = r12 & r18
            if (r10 == 0) goto L_0x07a6
            r10 = 0
            int r15 = com.google.android.gms.internal.vision.zzca.zzb((int) r15, (double) r10)
            int r6 = r6 + r15
        L_0x07b8:
            int r5 = r5 + 3
            r9 = r13
            r4 = 0
            r11 = 0
            goto L_0x03be
        L_0x07bf:
            com.google.android.gms.internal.vision.zzff<?, ?> r2 = r0.zzns
            int r2 = zza(r2, r1)
            int r6 = r6 + r2
            boolean r2 = r0.zznj
            if (r2 == 0) goto L_0x07d5
            com.google.android.gms.internal.vision.zzcg<?> r2 = r0.zznt
            com.google.android.gms.internal.vision.zzcj r1 = r2.zzb(r1)
            int r1 = r1.zzbl()
            int r6 = r6 + r1
        L_0x07d5:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzeb.zzn(java.lang.Object):int");
    }

    public final boolean zzp(T t) {
        int i;
        int i2 = -1;
        int i3 = 0;
        int i4 = 0;
        while (true) {
            boolean z = true;
            if (i3 >= this.zzno) {
                return !this.zznj || this.zznt.zzb(t).isInitialized();
            }
            int i5 = this.zznn[i3];
            int i6 = this.zzne[i5];
            int zzaj = zzaj(i5);
            if (!this.zznl) {
                int i7 = this.zzne[i5 + 2];
                int i8 = i7 & 1048575;
                i = 1 << (i7 >>> 20);
                if (i8 != i2) {
                    i4 = zznd.getInt(t, (long) i8);
                    i2 = i8;
                }
            } else {
                i = 0;
            }
            if (((268435456 & zzaj) != 0) && !zza(t, i5, i4, i)) {
                return false;
            }
            int i9 = (267386880 & zzaj) >>> 20;
            if (i9 != 9 && i9 != 17) {
                if (i9 != 27) {
                    if (i9 == 60 || i9 == 68) {
                        if (zza(t, i6, i5) && !zza((Object) t, zzaj, zzag(i5))) {
                            return false;
                        }
                    } else if (i9 != 49) {
                        if (i9 != 50) {
                            continue;
                        } else {
                            Map<?, ?> zzi = this.zznu.zzi(zzfl.zzo(t, (long) (zzaj & 1048575)));
                            if (!zzi.isEmpty()) {
                                if (this.zznu.zzm(zzah(i5)).zzmy.zzed() == zzfy.MESSAGE) {
                                    zzen<?> zzen = null;
                                    Iterator<?> it = zzi.values().iterator();
                                    while (true) {
                                        if (!it.hasNext()) {
                                            break;
                                        }
                                        Object next = it.next();
                                        if (zzen == null) {
                                            zzen = zzek.zzdc().zze(next.getClass());
                                        }
                                        if (!zzen.zzp(next)) {
                                            z = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!z) {
                                return false;
                            }
                        }
                    }
                }
                List list = (List) zzfl.zzo(t, (long) (zzaj & 1048575));
                if (!list.isEmpty()) {
                    zzen zzag = zzag(i5);
                    int i10 = 0;
                    while (true) {
                        if (i10 >= list.size()) {
                            break;
                        } else if (!zzag.zzp(list.get(i10))) {
                            z = false;
                            break;
                        } else {
                            i10++;
                        }
                    }
                }
                if (!z) {
                    return false;
                }
            } else if (zza(t, i5, i4, i) && !zza((Object) t, zzaj, zzag(i5))) {
                return false;
            }
            i3++;
        }
    }
}
