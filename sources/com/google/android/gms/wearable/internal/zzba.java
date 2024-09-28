package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

final class zzba extends zzn<Status> {
    private final /* synthetic */ zzay zzcm;
    private final /* synthetic */ int zzcn;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    zzba(zzay zzay, GoogleApiClient googleApiClient, int i) {
        super(googleApiClient);
        this.zzcm = zzay;
        this.zzcn = i;
    }

    /* access modifiers changed from: protected */
    public final /* synthetic */ void doExecute(Api.AnyClient anyClient) throws RemoteException {
        ((zzep) ((zzhg) anyClient).getService()).zzb((zzek) new zzgo(this), this.zzcm.zzce, this.zzcn);
    }

    /* access modifiers changed from: protected */
    public final /* synthetic */ Result createFailedResult(Status status) {
        return status;
    }
}
