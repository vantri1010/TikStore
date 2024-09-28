package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.wearable.CapabilityApi;

final class zzgr extends zzgm<CapabilityApi.GetCapabilityResult> {
    public zzgr(BaseImplementation.ResultHolder<CapabilityApi.GetCapabilityResult> resultHolder) {
        super(resultHolder);
    }

    public final void zza(zzdk zzdk) {
        zza(new zzy(zzgd.zzb(zzdk.statusCode), zzdk.zzdq == null ? null : new zzw(zzdk.zzdq)));
    }
}
