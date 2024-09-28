package com.google.android.datatransport.cct.a;

import com.aliyun.security.yunceng.android.sdk.traceroute.d;
import com.google.android.datatransport.cct.a.zzg;

/* compiled from: com.google.android.datatransport:transport-backend-cct@@2.2.0 */
public abstract class zzq {

    /* compiled from: com.google.android.datatransport:transport-backend-cct@@2.2.0 */
    public static abstract class zza {
        public abstract zza zza(zza zza);

        public abstract zza zza(zzb zzb);

        public abstract zzq zza();
    }

    /* compiled from: com.google.android.datatransport:transport-backend-cct@@2.2.0 */
    public enum zzb {
        zza(0),
        ANDROID(4);

        static {
            zza = new zzb(d.a, 0, 0);
            zzb zzb = new zzb("ANDROID", 1, 4);
            ANDROID = zzb;
            zzb[] zzbArr = {zza, zzb};
        }

        private zzb(int i) {
        }
    }

    public static zza zza() {
        return new zzg.zza();
    }
}
