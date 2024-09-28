package com.google.android.gms.internal.vision;

import java.util.Iterator;
import java.util.Map;

final class zzdt implements zzds {
    zzdt() {
    }

    public final int zzb(int i, Object obj, Object obj2) {
        zzdr zzdr = (zzdr) obj;
        if (zzdr.isEmpty()) {
            return 0;
        }
        Iterator it = zzdr.entrySet().iterator();
        if (!it.hasNext()) {
            return 0;
        }
        Map.Entry entry = (Map.Entry) it.next();
        entry.getKey();
        entry.getValue();
        throw new NoSuchMethodError();
    }

    public final Object zzb(Object obj, Object obj2) {
        zzdr zzdr = (zzdr) obj;
        zzdr zzdr2 = (zzdr) obj2;
        if (!zzdr2.isEmpty()) {
            if (!zzdr.isMutable()) {
                zzdr = zzdr.zzcq();
            }
            zzdr.zza(zzdr2);
        }
        return zzdr;
    }

    public final Map<?, ?> zzh(Object obj) {
        return (zzdr) obj;
    }

    public final Map<?, ?> zzi(Object obj) {
        return (zzdr) obj;
    }

    public final boolean zzj(Object obj) {
        return !((zzdr) obj).isMutable();
    }

    public final Object zzk(Object obj) {
        ((zzdr) obj).zzao();
        return obj;
    }

    public final Object zzl(Object obj) {
        return zzdr.zzcp().zzcq();
    }

    public final zzdq<?, ?> zzm(Object obj) {
        throw new NoSuchMethodError();
    }
}
