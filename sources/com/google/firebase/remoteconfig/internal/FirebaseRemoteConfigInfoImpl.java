package com.google.firebase.remoteconfig.internal;

import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class FirebaseRemoteConfigInfoImpl implements FirebaseRemoteConfigInfo {
    private final FirebaseRemoteConfigSettings configSettings;
    private final int lastFetchStatus;
    private final long lastSuccessfulFetchTimeInMillis;

    private FirebaseRemoteConfigInfoImpl(long lastSuccessfulFetchTimeInMillis2, int lastFetchStatus2, FirebaseRemoteConfigSettings configSettings2) {
        this.lastSuccessfulFetchTimeInMillis = lastSuccessfulFetchTimeInMillis2;
        this.lastFetchStatus = lastFetchStatus2;
        this.configSettings = configSettings2;
    }

    public long getFetchTimeMillis() {
        return this.lastSuccessfulFetchTimeInMillis;
    }

    public int getLastFetchStatus() {
        return this.lastFetchStatus;
    }

    public FirebaseRemoteConfigSettings getConfigSettings() {
        return this.configSettings;
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static class Builder {
        private FirebaseRemoteConfigSettings builderConfigSettings;
        private int builderLastFetchStatus;
        private long builderLastSuccessfulFetchTimeInMillis;

        private Builder() {
        }

        public Builder withLastSuccessfulFetchTimeInMillis(long fetchTimeInMillis) {
            this.builderLastSuccessfulFetchTimeInMillis = fetchTimeInMillis;
            return this;
        }

        /* access modifiers changed from: package-private */
        public Builder withLastFetchStatus(int lastFetchStatus) {
            this.builderLastFetchStatus = lastFetchStatus;
            return this;
        }

        /* access modifiers changed from: package-private */
        public Builder withConfigSettings(FirebaseRemoteConfigSettings configSettings) {
            this.builderConfigSettings = configSettings;
            return this;
        }

        public FirebaseRemoteConfigInfoImpl build() {
            return new FirebaseRemoteConfigInfoImpl(this.builderLastSuccessfulFetchTimeInMillis, this.builderLastFetchStatus, this.builderConfigSettings);
        }
    }

    static Builder newBuilder() {
        return new Builder();
    }
}
