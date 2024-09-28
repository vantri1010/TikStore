package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

final class zzep {
    private static final Class<?> zzob = zzdj();
    private static final zzff<?, ?> zzoc = zzd(false);
    private static final zzff<?, ?> zzod = zzd(true);
    private static final zzff<?, ?> zzoe = new zzfh();

    static int zza(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdl) {
            zzdl zzdl = (zzdl) list;
            i = 0;
            while (i2 < size) {
                i += zzca.zze(zzdl.getLong(i2));
                i2++;
            }
        } else {
            int i3 = 0;
            while (i2 < size) {
                i3 = i + zzca.zze(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    private static <UT, UB> UB zza(int i, int i2, UB ub, zzff<UT, UB> zzff) {
        if (ub == null) {
            ub = zzff.zzdt();
        }
        zzff.zza(ub, i, (long) i2);
        return ub;
    }

    static <UT, UB> UB zza(int i, List<Integer> list, zzcv<?> zzcv, UB ub, zzff<UT, UB> zzff) {
        if (zzcv == null) {
            return ub;
        }
        if (list instanceof RandomAccess) {
            int size = list.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                int intValue = list.get(i3).intValue();
                if (zzcv.zzaf(intValue) != null) {
                    if (i3 != i2) {
                        list.set(i2, Integer.valueOf(intValue));
                    }
                    i2++;
                } else {
                    ub = zza(i, intValue, ub, zzff);
                }
            }
            if (i2 != size) {
                list.subList(i2, size).clear();
            }
        } else {
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                int intValue2 = it.next().intValue();
                if (zzcv.zzaf(intValue2) == null) {
                    ub = zza(i, intValue2, ub, zzff);
                    it.remove();
                }
            }
        }
        return ub;
    }

    public static void zza(int i, List<String> list, zzfz zzfz) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zza(i, list);
        }
    }

    public static void zza(int i, List<?> list, zzfz zzfz, zzen zzen) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zza(i, list, zzen);
        }
    }

    public static void zza(int i, List<Double> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzg(i, list, z);
        }
    }

    static <T, FT extends zzcl<FT>> void zza(zzcg<FT> zzcg, T t, T t2) {
        zzcj<FT> zzb = zzcg.zzb(t2);
        if (!zzb.isEmpty()) {
            zzcg.zzc(t).zza(zzb);
        }
    }

    static <T> void zza(zzds zzds, T t, T t2, long j) {
        zzfl.zza((Object) t, j, zzds.zzb(zzfl.zzo(t, j), zzfl.zzo(t2, j)));
    }

    static <T, UT, UB> void zza(zzff<UT, UB> zzff, T t, T t2) {
        zzff.zze(t, zzff.zzg(zzff.zzr(t), zzff.zzr(t2)));
    }

    static int zzb(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdl) {
            zzdl zzdl = (zzdl) list;
            i = 0;
            while (i2 < size) {
                i += zzca.zzf(zzdl.getLong(i2));
                i2++;
            }
        } else {
            int i3 = 0;
            while (i2 < size) {
                i3 = i + zzca.zzf(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static void zzb(int i, List<zzbo> list, zzfz zzfz) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzb(i, list);
        }
    }

    public static void zzb(int i, List<?> list, zzfz zzfz, zzen zzen) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzb(i, list, zzen);
        }
    }

    public static void zzb(int i, List<Float> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzf(i, list, z);
        }
    }

    static int zzc(int i, Object obj, zzen zzen) {
        return obj instanceof zzde ? zzca.zza(i, (zzde) obj) : zzca.zzb(i, (zzdx) obj, zzen);
    }

    static int zzc(int i, List<?> list) {
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        int zzt = zzca.zzt(i) * size;
        if (list instanceof zzdg) {
            zzdg zzdg = (zzdg) list;
            while (i2 < size) {
                Object raw = zzdg.getRaw(i2);
                zzt += raw instanceof zzbo ? zzca.zzb((zzbo) raw) : zzca.zzi((String) raw);
                i2++;
            }
        } else {
            while (i2 < size) {
                Object obj = list.get(i2);
                zzt += obj instanceof zzbo ? zzca.zzb((zzbo) obj) : zzca.zzi((String) obj);
                i2++;
            }
        }
        return zzt;
    }

    static int zzc(int i, List<?> list, zzen zzen) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zzt = zzca.zzt(i) * size;
        for (int i2 = 0; i2 < size; i2++) {
            Object obj = list.get(i2);
            zzt += obj instanceof zzde ? zzca.zza((zzde) obj) : zzca.zza((zzdx) obj, zzen);
        }
        return zzt;
    }

    static int zzc(List<Long> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzdl) {
            zzdl zzdl = (zzdl) list;
            i = 0;
            while (i2 < size) {
                i += zzca.zzg(zzdl.getLong(i2));
                i2++;
            }
        } else {
            int i3 = 0;
            while (i2 < size) {
                i3 = i + zzca.zzg(list.get(i2).longValue());
                i2++;
            }
        }
        return i;
    }

    public static void zzc(int i, List<Long> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzc(i, list, z);
        }
    }

    static int zzd(int i, List<zzbo> list) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int zzt = size * zzca.zzt(i);
        for (int i2 = 0; i2 < list.size(); i2++) {
            zzt += zzca.zzb(list.get(i2));
        }
        return zzt;
    }

    static int zzd(int i, List<zzdx> list, zzen zzen) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            i2 += zzca.zzc(i, list.get(i3), zzen);
        }
        return i2;
    }

    static int zzd(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzcs) {
            zzcs zzcs = (zzcs) list;
            i = 0;
            while (i2 < size) {
                i += zzca.zzz(zzcs.getInt(i2));
                i2++;
            }
        } else {
            int i3 = 0;
            while (i2 < size) {
                i3 = i + zzca.zzz(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    private static zzff<?, ?> zzd(boolean z) {
        try {
            Class<?> zzdk = zzdk();
            if (zzdk == null) {
                return null;
            }
            return (zzff) zzdk.getConstructor(new Class[]{Boolean.TYPE}).newInstance(new Object[]{Boolean.valueOf(z)});
        } catch (Throwable th) {
            return null;
        }
    }

    public static void zzd(int i, List<Long> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzd(i, list, z);
        }
    }

    static boolean zzd(Object obj, Object obj2) {
        if (obj != obj2) {
            return obj != null && obj.equals(obj2);
        }
        return true;
    }

    public static zzff<?, ?> zzdg() {
        return zzoc;
    }

    public static zzff<?, ?> zzdh() {
        return zzod;
    }

    public static zzff<?, ?> zzdi() {
        return zzoe;
    }

    private static Class<?> zzdj() {
        try {
            return Class.forName("com.google.protobuf.GeneratedMessage");
        } catch (Throwable th) {
            return null;
        }
    }

    private static Class<?> zzdk() {
        try {
            return Class.forName("com.google.protobuf.UnknownFieldSetSchema");
        } catch (Throwable th) {
            return null;
        }
    }

    static int zze(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzcs) {
            zzcs zzcs = (zzcs) list;
            i = 0;
            while (i2 < size) {
                i += zzca.zzu(zzcs.getInt(i2));
                i2++;
            }
        } else {
            int i3 = 0;
            while (i2 < size) {
                i3 = i + zzca.zzu(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static void zze(int i, List<Long> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzn(i, list, z);
        }
    }

    static int zzf(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzcs) {
            zzcs zzcs = (zzcs) list;
            i = 0;
            while (i2 < size) {
                i += zzca.zzv(zzcs.getInt(i2));
                i2++;
            }
        } else {
            int i3 = 0;
            while (i2 < size) {
                i3 = i + zzca.zzv(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static void zzf(int i, List<Long> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zze(i, list, z);
        }
    }

    public static void zzf(Class<?> cls) {
        Class<?> cls2;
        if (!zzcr.class.isAssignableFrom(cls) && (cls2 = zzob) != null && !cls2.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Message classes must extend GeneratedMessage or GeneratedMessageLite");
        }
    }

    static int zzg(List<Integer> list) {
        int i;
        int size = list.size();
        int i2 = 0;
        if (size == 0) {
            return 0;
        }
        if (list instanceof zzcs) {
            zzcs zzcs = (zzcs) list;
            i = 0;
            while (i2 < size) {
                i += zzca.zzw(zzcs.getInt(i2));
                i2++;
            }
        } else {
            int i3 = 0;
            while (i2 < size) {
                i3 = i + zzca.zzw(list.get(i2).intValue());
                i2++;
            }
        }
        return i;
    }

    public static void zzg(int i, List<Long> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzl(i, list, z);
        }
    }

    static int zzh(List<?> list) {
        return list.size() << 2;
    }

    public static void zzh(int i, List<Integer> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zza(i, list, z);
        }
    }

    static int zzi(List<?> list) {
        return list.size() << 3;
    }

    public static void zzi(int i, List<Integer> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzj(i, list, z);
        }
    }

    static int zzj(List<?> list) {
        return list.size();
    }

    public static void zzj(int i, List<Integer> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzm(i, list, z);
        }
    }

    public static void zzk(int i, List<Integer> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzb(i, list, z);
        }
    }

    public static void zzl(int i, List<Integer> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzk(i, list, z);
        }
    }

    public static void zzm(int i, List<Integer> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzh(i, list, z);
        }
    }

    public static void zzn(int i, List<Boolean> list, zzfz zzfz, boolean z) throws IOException {
        if (list != null && !list.isEmpty()) {
            zzfz.zzi(i, list, z);
        }
    }

    static int zzo(int i, List<Long> list, boolean z) {
        if (list.size() == 0) {
            return 0;
        }
        return zza(list) + (list.size() * zzca.zzt(i));
    }

    static int zzp(int i, List<Long> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzb(list) + (size * zzca.zzt(i));
    }

    static int zzq(int i, List<Long> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzc(list) + (size * zzca.zzt(i));
    }

    static int zzr(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzd(list) + (size * zzca.zzt(i));
    }

    static int zzs(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zze(list) + (size * zzca.zzt(i));
    }

    static int zzt(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzf(list) + (size * zzca.zzt(i));
    }

    static int zzu(int i, List<Integer> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return zzg(list) + (size * zzca.zzt(i));
    }

    static int zzv(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzca.zzl(i, 0);
    }

    static int zzw(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzca.zzg(i, 0);
    }

    static int zzx(int i, List<?> list, boolean z) {
        int size = list.size();
        if (size == 0) {
            return 0;
        }
        return size * zzca.zzc(i, true);
    }
}
