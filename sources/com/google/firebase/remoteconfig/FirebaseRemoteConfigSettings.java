package com.google.firebase.remoteconfig;

import com.google.firebase.remoteconfig.internal.ConfigFetchHandler;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class FirebaseRemoteConfigSettings {
    private final boolean enableDeveloperMode;
    private final long fetchTimeoutInSeconds;
    private final long minimumFetchInterval;

    private FirebaseRemoteConfigSettings(Builder builder) {
        this.enableDeveloperMode = builder.enableDeveloperMode;
        this.fetchTimeoutInSeconds = builder.fetchTimeoutInSeconds;
        this.minimumFetchInterval = builder.minimumFetchInterval;
    }

    @Deprecated
    public boolean isDeveloperModeEnabled() {
        return this.enableDeveloperMode;
    }

    public long getFetchTimeoutInSeconds() {
        return this.fetchTimeoutInSeconds;
    }

    public long getMinimumFetchIntervalInSeconds() {
        return this.minimumFetchInterval;
    }

    public Builder toBuilder() {
        Builder frcBuilder = new Builder();
        frcBuilder.setDeveloperModeEnabled(isDeveloperModeEnabled());
        frcBuilder.setFetchTimeoutInSeconds(getFetchTimeoutInSeconds());
        frcBuilder.setMinimumFetchIntervalInSeconds(getMinimumFetchIntervalInSeconds());
        return frcBuilder;
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static class Builder {
        /* access modifiers changed from: private */
        public boolean enableDeveloperMode = false;
        /* access modifiers changed from: private */
        public long fetchTimeoutInSeconds = 60;
        /* access modifiers changed from: private */
        public long minimumFetchInterval = ConfigFetchHandler.DEFAULT_MINIMUM_FETCH_INTERVAL_IN_SECONDS;

        @Deprecated
        public Builder setDeveloperModeEnabled(boolean enabled) {
            this.enableDeveloperMode = enabled;
            return this;
        }

        public Builder setFetchTimeoutInSeconds(long duration) throws IllegalArgumentException {
            if (duration >= 0) {
                this.fetchTimeoutInSeconds = duration;
                return this;
            }
            throw new IllegalArgumentException(String.format("Fetch connection timeout has to be a non-negative number. %d is an invalid argument", new Object[]{Long.valueOf(duration)}));
        }

        public Builder setMinimumFetchIntervalInSeconds(long duration) {
            if (duration >= 0) {
                this.minimumFetchInterval = duration;
                return this;
            }
            throw new IllegalArgumentException("Minimum interval between fetches has to be a non-negative number. " + duration + " is an invalid argument");
        }

        public long getFetchTimeoutInSeconds() {
            return this.fetchTimeoutInSeconds;
        }

        public long getMinimumFetchIntervalInSeconds() {
            return this.minimumFetchInterval;
        }

        public FirebaseRemoteConfigSettings build() {
            return new FirebaseRemoteConfigSettings(this);
        }
    }
}
