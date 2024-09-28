package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.NodeApi;
import java.util.ArrayList;

final class zzfi extends zzn<NodeApi.GetConnectedNodesResult> {
    zzfi(zzfg zzfg, GoogleApiClient googleApiClient) {
        super(googleApiClient);
    }

    /* access modifiers changed from: protected */
    public final /* synthetic */ void doExecute(Api.AnyClient anyClient) throws RemoteException {
        ((zzep) ((zzhg) anyClient).getService()).zzc(new zzgu(this));
    }

    /* access modifiers changed from: protected */
    public final /* synthetic */ Result createFailedResult(Status status) {
        return new zzfj(status, new ArrayList());
    }
}
