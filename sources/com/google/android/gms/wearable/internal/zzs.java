package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi;

final class zzs extends zzn<CapabilityApi.RemoveLocalCapabilityResult> {
    private final /* synthetic */ String zzbp;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    zzs(zzo zzo, GoogleApiClient googleApiClient, String str) {
        super(googleApiClient);
        this.zzbp = str;
    }

    /* access modifiers changed from: protected */
    public final /* synthetic */ void doExecute(Api.AnyClient anyClient) throws RemoteException {
        ((zzep) ((zzhg) anyClient).getService()).zzb(new zzhd(this), this.zzbp);
    }

    /* access modifiers changed from: protected */
    public final /* synthetic */ Result createFailedResult(Status status) {
        return new zzu(status);
    }
}
