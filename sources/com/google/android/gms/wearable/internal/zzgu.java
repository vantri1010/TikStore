package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.wearable.NodeApi;
import java.util.ArrayList;

final class zzgu extends zzgm<NodeApi.GetConnectedNodesResult> {
    public zzgu(BaseImplementation.ResultHolder<NodeApi.GetConnectedNodesResult> resultHolder) {
        super(resultHolder);
    }

    public final void zza(zzea zzea) {
        ArrayList arrayList = new ArrayList();
        if (zzea.zzdx != null) {
            arrayList.addAll(zzea.zzdx);
        }
        zza(new zzfj(zzgd.zzb(zzea.statusCode), arrayList));
    }
}
