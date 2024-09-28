package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.wearable.CapabilityApi;

final class zzz extends zzn<Status> {
    private CapabilityApi.CapabilityListener zzbs;

    private zzz(GoogleApiClient googleApiClient, CapabilityApi.CapabilityListener capabilityListener) {
        super(googleApiClient);
        this.zzbs = capabilityListener;
    }

    /* access modifiers changed from: protected */
    public final /* synthetic */ void doExecute(Api.AnyClient anyClient) throws RemoteException {
        ((zzhg) anyClient).zza((BaseImplementation.ResultHolder<Status>) this, this.zzbs);
        this.zzbs = null;
    }

    public final /* synthetic */ Result createFailedResult(Status status) {
        this.zzbs = null;
        return status;
    }

    /* synthetic */ zzz(GoogleApiClient googleApiClient, CapabilityApi.CapabilityListener capabilityListener, zzp zzp) {
        this(googleApiClient, capabilityListener);
    }
}
