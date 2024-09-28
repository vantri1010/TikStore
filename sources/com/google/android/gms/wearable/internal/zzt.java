package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.wearable.CapabilityApi;

final class zzt implements zzc<CapabilityApi.CapabilityListener> {
    private final /* synthetic */ IntentFilter[] zzbr;

    zzt(IntentFilter[] intentFilterArr) {
        this.zzbr = intentFilterArr;
    }

    public final /* synthetic */ void zza(zzhg zzhg, BaseImplementation.ResultHolder resultHolder, Object obj, ListenerHolder listenerHolder) throws RemoteException {
        zzhg.zza((BaseImplementation.ResultHolder<Status>) resultHolder, (CapabilityApi.CapabilityListener) obj, (ListenerHolder<CapabilityApi.CapabilityListener>) listenerHolder, this.zzbr);
    }
}
