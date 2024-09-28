package com.google.android.gms.internal.vision;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class zzek {
    private static final zzek zznx = new zzek();
    private final zzeo zzny;
    private final ConcurrentMap<Class<?>, zzen<?>> zznz = new ConcurrentHashMap();

    private zzek() {
        String[] strArr = {"com.google.protobuf.AndroidProto3SchemaFactory"};
        zzeo zzeo = null;
        for (int i = 0; i <= 0; i++) {
            zzeo = zzk(strArr[0]);
            if (zzeo != null) {
                break;
            }
        }
        this.zzny = zzeo == null ? new zzdm() : zzeo;
    }

    public static zzek zzdc() {
        return zznx;
    }

    private static zzeo zzk(String str) {
        try {
            return (zzeo) Class.forName(str).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Throwable th) {
            return null;
        }
    }

    public final <T> zzen<T> zze(Class<T> cls) {
        zzct.zza(cls, "messageType");
        zzen<T> zzen = (zzen) this.zznz.get(cls);
        if (zzen != null) {
            return zzen;
        }
        zzen<T> zzd = this.zzny.zzd(cls);
        zzct.zza(cls, "messageType");
        zzct.zza(zzd, "schema");
        zzen<T> putIfAbsent = this.zznz.putIfAbsent(cls, zzd);
        return putIfAbsent != null ? putIfAbsent : zzd;
    }

    public final <T> zzen<T> zzq(T t) {
        return zze(t.getClass());
    }
}
