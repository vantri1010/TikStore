package com.google.android.gms.signin;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.signin.internal.SignInClientImpl;

final class zab extends Api.AbstractClientBuilder<SignInClientImpl, SignInOptions> {
    zab() {
    }

    public final /* synthetic */ Api.Client buildClient(Context context, Looper looper, ClientSettings clientSettings, Object obj, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        SignInOptions signInOptions;
        SignInOptions signInOptions2 = (SignInOptions) obj;
        if (signInOptions2 == null) {
            signInOptions = SignInOptions.DEFAULT;
        } else {
            signInOptions = signInOptions2;
        }
        return new SignInClientImpl(context, looper, true, clientSettings, signInOptions, connectionCallbacks, onConnectionFailedListener);
    }
}
