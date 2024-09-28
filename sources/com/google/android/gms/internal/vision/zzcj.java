package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzcl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class zzcj<FieldDescriptorType extends zzcl<FieldDescriptorType>> {
    private static final zzcj zzhx = new zzcj(true);
    private final zzeq<FieldDescriptorType, Object> zzhu = zzeq.zzam(16);
    private boolean zzhv;
    private boolean zzhw = false;

    private zzcj() {
    }

    private zzcj(boolean z) {
        zzao();
    }

    static int zza(zzft zzft, int i, Object obj) {
        int zzt = zzca.zzt(i);
        if (zzft == zzft.GROUP) {
            zzct.zzf((zzdx) obj);
            zzt <<= 1;
        }
        return zzt + zzb(zzft, obj);
    }

    private final Object zza(FieldDescriptorType fielddescriptortype) {
        Object obj = this.zzhu.get(fielddescriptortype);
        return obj instanceof zzda ? zzda.zzci() : obj;
    }

    static void zza(zzca zzca, zzft zzft, int i, Object obj) throws IOException {
        if (zzft == zzft.GROUP) {
            zzdx zzdx = (zzdx) obj;
            zzct.zzf(zzdx);
            zzca.zzd(i, 3);
            zzdx.zzb(zzca);
            zzca.zzd(i, 4);
            return;
        }
        zzca.zzd(i, zzft.zzee());
        switch (zzck.zzhz[zzft.ordinal()]) {
            case 1:
                zzca.zza(((Double) obj).doubleValue());
                return;
            case 2:
                zzca.zzc(((Float) obj).floatValue());
                return;
            case 3:
                zzca.zzb(((Long) obj).longValue());
                return;
            case 4:
                zzca.zzb(((Long) obj).longValue());
                return;
            case 5:
                zzca.zzp(((Integer) obj).intValue());
                return;
            case 6:
                zzca.zzd(((Long) obj).longValue());
                return;
            case 7:
                zzca.zzs(((Integer) obj).intValue());
                return;
            case 8:
                zzca.zza(((Boolean) obj).booleanValue());
                return;
            case 9:
                ((zzdx) obj).zzb(zzca);
                return;
            case 10:
                zzca.zzb((zzdx) obj);
                return;
            case 11:
                if (obj instanceof zzbo) {
                    zzca.zza((zzbo) obj);
                    return;
                } else {
                    zzca.zzh((String) obj);
                    return;
                }
            case 12:
                if (obj instanceof zzbo) {
                    zzca.zza((zzbo) obj);
                    return;
                }
                byte[] bArr = (byte[]) obj;
                zzca.zzd(bArr, 0, bArr.length);
                return;
            case 13:
                zzca.zzq(((Integer) obj).intValue());
                return;
            case 14:
                zzca.zzs(((Integer) obj).intValue());
                return;
            case 15:
                zzca.zzd(((Long) obj).longValue());
                return;
            case 16:
                zzca.zzr(((Integer) obj).intValue());
                return;
            case 17:
                zzca.zzc(((Long) obj).longValue());
                return;
            case 18:
                if (obj instanceof zzcu) {
                    zzca.zzp(((zzcu) obj).zzbn());
                    return;
                } else {
                    zzca.zzp(((Integer) obj).intValue());
                    return;
                }
            default:
                return;
        }
    }

    private final void zza(FieldDescriptorType fielddescriptortype, Object obj) {
        if (!fielddescriptortype.zzbq()) {
            zza(fielddescriptortype.zzbo(), obj);
        } else if (obj instanceof List) {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll((List) obj);
            ArrayList arrayList2 = arrayList;
            int size = arrayList2.size();
            int i = 0;
            while (i < size) {
                Object obj2 = arrayList2.get(i);
                i++;
                zza(fielddescriptortype.zzbo(), obj2);
            }
            obj = arrayList;
        } else {
            throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
        }
        if (obj instanceof zzda) {
            this.zzhw = true;
        }
        this.zzhu.put(fielddescriptortype, obj);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0026, code lost:
        if ((r3 instanceof com.google.android.gms.internal.vision.zzcu) == false) goto L_0x001e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002f, code lost:
        if ((r3 instanceof byte[]) == false) goto L_0x001e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001b, code lost:
        if ((r3 instanceof com.google.android.gms.internal.vision.zzda) == false) goto L_0x001e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001e, code lost:
        r0 = false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0046 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0047  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void zza(com.google.android.gms.internal.vision.zzft r2, java.lang.Object r3) {
        /*
            com.google.android.gms.internal.vision.zzct.checkNotNull(r3)
            int[] r0 = com.google.android.gms.internal.vision.zzck.zzhy
            com.google.android.gms.internal.vision.zzfy r2 = r2.zzed()
            int r2 = r2.ordinal()
            r2 = r0[r2]
            r0 = 1
            r1 = 0
            switch(r2) {
                case 1: goto L_0x0041;
                case 2: goto L_0x003e;
                case 3: goto L_0x003b;
                case 4: goto L_0x0038;
                case 5: goto L_0x0035;
                case 6: goto L_0x0032;
                case 7: goto L_0x0029;
                case 8: goto L_0x0020;
                case 9: goto L_0x0015;
                default: goto L_0x0014;
            }
        L_0x0014:
            goto L_0x0044
        L_0x0015:
            boolean r2 = r3 instanceof com.google.android.gms.internal.vision.zzdx
            if (r2 != 0) goto L_0x0043
            boolean r2 = r3 instanceof com.google.android.gms.internal.vision.zzda
            if (r2 == 0) goto L_0x001e
            goto L_0x0043
        L_0x001e:
            r0 = 0
            goto L_0x0043
        L_0x0020:
            boolean r2 = r3 instanceof java.lang.Integer
            if (r2 != 0) goto L_0x0043
            boolean r2 = r3 instanceof com.google.android.gms.internal.vision.zzcu
            if (r2 == 0) goto L_0x001e
            goto L_0x0043
        L_0x0029:
            boolean r2 = r3 instanceof com.google.android.gms.internal.vision.zzbo
            if (r2 != 0) goto L_0x0043
            boolean r2 = r3 instanceof byte[]
            if (r2 == 0) goto L_0x001e
            goto L_0x0043
        L_0x0032:
            boolean r0 = r3 instanceof java.lang.String
            goto L_0x0043
        L_0x0035:
            boolean r0 = r3 instanceof java.lang.Boolean
            goto L_0x0043
        L_0x0038:
            boolean r0 = r3 instanceof java.lang.Double
            goto L_0x0043
        L_0x003b:
            boolean r0 = r3 instanceof java.lang.Float
            goto L_0x0043
        L_0x003e:
            boolean r0 = r3 instanceof java.lang.Long
            goto L_0x0043
        L_0x0041:
            boolean r0 = r3 instanceof java.lang.Integer
        L_0x0043:
            r1 = r0
        L_0x0044:
            if (r1 == 0) goto L_0x0047
            return
        L_0x0047:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "Wrong object type used with protocol message reflection."
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzcj.zza(com.google.android.gms.internal.vision.zzft, java.lang.Object):void");
    }

    private static int zzb(zzcl<?> zzcl, Object obj) {
        zzft zzbo = zzcl.zzbo();
        int zzbn = zzcl.zzbn();
        if (!zzcl.zzbq()) {
            return zza(zzbo, zzbn, obj);
        }
        int i = 0;
        List<Object> list = (List) obj;
        if (zzcl.zzbr()) {
            for (Object zzb : list) {
                i += zzb(zzbo, zzb);
            }
            return zzca.zzt(zzbn) + i + zzca.zzab(i);
        }
        for (Object zza : list) {
            i += zza(zzbo, zzbn, zza);
        }
        return i;
    }

    private static int zzb(zzft zzft, Object obj) {
        switch (zzck.zzhz[zzft.ordinal()]) {
            case 1:
                return zzca.zzb(((Double) obj).doubleValue());
            case 2:
                return zzca.zzd(((Float) obj).floatValue());
            case 3:
                return zzca.zze(((Long) obj).longValue());
            case 4:
                return zzca.zzf(((Long) obj).longValue());
            case 5:
                return zzca.zzu(((Integer) obj).intValue());
            case 6:
                return zzca.zzh(((Long) obj).longValue());
            case 7:
                return zzca.zzx(((Integer) obj).intValue());
            case 8:
                return zzca.zzb(((Boolean) obj).booleanValue());
            case 9:
                return zzca.zzd((zzdx) obj);
            case 10:
                return obj instanceof zzda ? zzca.zza((zzde) (zzda) obj) : zzca.zzc((zzdx) obj);
            case 11:
                return obj instanceof zzbo ? zzca.zzb((zzbo) obj) : zzca.zzi((String) obj);
            case 12:
                return obj instanceof zzbo ? zzca.zzb((zzbo) obj) : zzca.zze((byte[]) obj);
            case 13:
                return zzca.zzv(((Integer) obj).intValue());
            case 14:
                return zzca.zzy(((Integer) obj).intValue());
            case 15:
                return zzca.zzi(((Long) obj).longValue());
            case 16:
                return zzca.zzw(((Integer) obj).intValue());
            case 17:
                return zzca.zzg(((Long) obj).longValue());
            case 18:
                return obj instanceof zzcu ? zzca.zzz(((zzcu) obj).zzbn()) : zzca.zzz(((Integer) obj).intValue());
            default:
                throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
        }
    }

    private static boolean zzb(Map.Entry<FieldDescriptorType, Object> entry) {
        zzcl zzcl = (zzcl) entry.getKey();
        if (zzcl.zzbp() == zzfy.MESSAGE) {
            boolean zzbq = zzcl.zzbq();
            Object value = entry.getValue();
            if (zzbq) {
                for (zzdx isInitialized : (List) value) {
                    if (!isInitialized.isInitialized()) {
                        return false;
                    }
                }
            } else if (value instanceof zzdx) {
                if (!((zzdx) value).isInitialized()) {
                    return false;
                }
            } else if (value instanceof zzda) {
                return true;
            } else {
                throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }
        }
        return true;
    }

    public static <T extends zzcl<T>> zzcj<T> zzbk() {
        return zzhx;
    }

    private final void zzc(Map.Entry<FieldDescriptorType, Object> entry) {
        zzcl zzcl = (zzcl) entry.getKey();
        Object value = entry.getValue();
        if (value instanceof zzda) {
            value = zzda.zzci();
        }
        if (zzcl.zzbq()) {
            Object zza = zza(zzcl);
            if (zza == null) {
                zza = new ArrayList();
            }
            for (Object zze : (List) value) {
                ((List) zza).add(zze(zze));
            }
            this.zzhu.put(zzcl, zza);
        } else if (zzcl.zzbp() == zzfy.MESSAGE) {
            Object zza2 = zza(zzcl);
            if (zza2 == null) {
                this.zzhu.put(zzcl, zze(value));
            } else {
                this.zzhu.put(zzcl, zza2 instanceof zzee ? zzcl.zza((zzee) zza2, (zzee) value) : zzcl.zza(((zzdx) zza2).zzbu(), (zzdx) value).zzca());
            }
        } else {
            this.zzhu.put(zzcl, zze(value));
        }
    }

    private static int zzd(Map.Entry<FieldDescriptorType, Object> entry) {
        zzcl zzcl = (zzcl) entry.getKey();
        Object value = entry.getValue();
        if (zzcl.zzbp() != zzfy.MESSAGE || zzcl.zzbq() || zzcl.zzbr()) {
            return zzb((zzcl<?>) zzcl, value);
        }
        boolean z = value instanceof zzda;
        int zzbn = ((zzcl) entry.getKey()).zzbn();
        return z ? zzca.zzb(zzbn, (zzde) (zzda) value) : zzca.zzb(zzbn, (zzdx) value);
    }

    private static Object zze(Object obj) {
        if (obj instanceof zzee) {
            return ((zzee) obj).zzcy();
        }
        if (!(obj instanceof byte[])) {
            return obj;
        }
        byte[] bArr = (byte[]) obj;
        byte[] bArr2 = new byte[bArr.length];
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        return bArr2;
    }

    public final /* synthetic */ Object clone() throws CloneNotSupportedException {
        zzcj zzcj = new zzcj();
        for (int i = 0; i < this.zzhu.zzdl(); i++) {
            Map.Entry<FieldDescriptorType, Object> zzan = this.zzhu.zzan(i);
            zzcj.zza((zzcl) zzan.getKey(), zzan.getValue());
        }
        for (Map.Entry next : this.zzhu.zzdm()) {
            zzcj.zza((zzcl) next.getKey(), next.getValue());
        }
        zzcj.zzhw = this.zzhw;
        return zzcj;
    }

    /* access modifiers changed from: package-private */
    public final Iterator<Map.Entry<FieldDescriptorType, Object>> descendingIterator() {
        return this.zzhw ? new zzdd(this.zzhu.zzdn().iterator()) : this.zzhu.zzdn().iterator();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzcj)) {
            return false;
        }
        return this.zzhu.equals(((zzcj) obj).zzhu);
    }

    public final int hashCode() {
        return this.zzhu.hashCode();
    }

    /* access modifiers changed from: package-private */
    public final boolean isEmpty() {
        return this.zzhu.isEmpty();
    }

    public final boolean isImmutable() {
        return this.zzhv;
    }

    public final boolean isInitialized() {
        for (int i = 0; i < this.zzhu.zzdl(); i++) {
            if (!zzb(this.zzhu.zzan(i))) {
                return false;
            }
        }
        for (Map.Entry<FieldDescriptorType, Object> zzb : this.zzhu.zzdm()) {
            if (!zzb(zzb)) {
                return false;
            }
        }
        return true;
    }

    public final Iterator<Map.Entry<FieldDescriptorType, Object>> iterator() {
        return this.zzhw ? new zzdd(this.zzhu.entrySet().iterator()) : this.zzhu.entrySet().iterator();
    }

    public final void zza(zzcj<FieldDescriptorType> zzcj) {
        for (int i = 0; i < zzcj.zzhu.zzdl(); i++) {
            zzc(zzcj.zzhu.zzan(i));
        }
        for (Map.Entry<FieldDescriptorType, Object> zzc : zzcj.zzhu.zzdm()) {
            zzc(zzc);
        }
    }

    public final void zzao() {
        if (!this.zzhv) {
            this.zzhu.zzao();
            this.zzhv = true;
        }
    }

    public final int zzbl() {
        int i = 0;
        for (int i2 = 0; i2 < this.zzhu.zzdl(); i2++) {
            Map.Entry<FieldDescriptorType, Object> zzan = this.zzhu.zzan(i2);
            i += zzb((zzcl<?>) (zzcl) zzan.getKey(), zzan.getValue());
        }
        for (Map.Entry next : this.zzhu.zzdm()) {
            i += zzb((zzcl<?>) (zzcl) next.getKey(), next.getValue());
        }
        return i;
    }

    public final int zzbm() {
        int i = 0;
        for (int i2 = 0; i2 < this.zzhu.zzdl(); i2++) {
            i += zzd(this.zzhu.zzan(i2));
        }
        for (Map.Entry<FieldDescriptorType, Object> zzd : this.zzhu.zzdm()) {
            i += zzd(zzd);
        }
        return i;
    }
}
