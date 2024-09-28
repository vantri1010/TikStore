package com.google.firebase.remoteconfig;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public interface FirebaseRemoteConfigInfo {
    FirebaseRemoteConfigSettings getConfigSettings();

    long getFetchTimeMillis();

    int getLastFetchStatus();
}
