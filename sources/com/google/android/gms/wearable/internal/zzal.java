package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.wearable.ChannelApi;

final class zzal implements zzc<ChannelApi.ChannelListener> {
    private final /* synthetic */ IntentFilter[] zzbr;

    zzal(IntentFilter[] intentFilterArr) {
        this.zzbr = intentFilterArr;
    }

    public final /* synthetic */ void zza(zzhg zzhg, BaseImplementation.ResultHolder resultHolder, Object obj, ListenerHolder listenerHolder) throws RemoteException {
        zzhg.zza((BaseImplementation.ResultHolder<Status>) resultHolder, (ChannelApi.ChannelListener) obj, (ListenerHolder<ChannelApi.ChannelListener>) listenerHolder, (String) null, this.zzbr);
    }
}
