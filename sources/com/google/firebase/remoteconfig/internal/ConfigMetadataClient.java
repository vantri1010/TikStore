package com.google.firebase.remoteconfig.internal;

import android.content.SharedPreferences;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import java.util.Date;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class ConfigMetadataClient {
    private static final String BACKOFF_END_TIME_IN_MILLIS_KEY = "backoff_end_time_in_millis";
    private static final String DEVELOPER_MODE_KEY = "is_developer_mode_enabled";
    private static final String FETCH_TIMEOUT_IN_SECONDS_KEY = "fetch_timeout_in_seconds";
    private static final String LAST_FETCH_ETAG_KEY = "last_fetch_etag";
    private static final String LAST_FETCH_STATUS_KEY = "last_fetch_status";
    public static final long LAST_FETCH_TIME_IN_MILLIS_NO_FETCH_YET = -1;
    static final Date LAST_FETCH_TIME_NO_FETCH_YET = new Date(-1);
    private static final String LAST_SUCCESSFUL_FETCH_TIME_IN_MILLIS_KEY = "last_fetch_time_in_millis";
    private static final String MINIMUM_FETCH_INTERVAL_IN_SECONDS_KEY = "minimum_fetch_interval_in_seconds";
    static final Date NO_BACKOFF_TIME = new Date(-1);
    private static final long NO_BACKOFF_TIME_IN_MILLIS = -1;
    static final int NO_FAILED_FETCHES = 0;
    private static final String NUM_FAILED_FETCHES_KEY = "num_failed_fetches";
    private final Object backoffMetadataLock = new Object();
    private final Object frcInfoLock = new Object();
    private final SharedPreferences frcMetadata;

    public ConfigMetadataClient(SharedPreferences frcMetadata2) {
        this.frcMetadata = frcMetadata2;
    }

    public boolean isDeveloperModeEnabled() {
        return this.frcMetadata.getBoolean(DEVELOPER_MODE_KEY, false);
    }

    public long getFetchTimeoutInSeconds() {
        return this.frcMetadata.getLong(FETCH_TIMEOUT_IN_SECONDS_KEY, 60);
    }

    public long getMinimumFetchIntervalInSeconds() {
        return this.frcMetadata.getLong(MINIMUM_FETCH_INTERVAL_IN_SECONDS_KEY, ConfigFetchHandler.DEFAULT_MINIMUM_FETCH_INTERVAL_IN_SECONDS);
    }

    /* access modifiers changed from: package-private */
    public int getLastFetchStatus() {
        return this.frcMetadata.getInt(LAST_FETCH_STATUS_KEY, 0);
    }

    /* access modifiers changed from: package-private */
    public Date getLastSuccessfulFetchTime() {
        return new Date(this.frcMetadata.getLong(LAST_SUCCESSFUL_FETCH_TIME_IN_MILLIS_KEY, -1));
    }

    /* access modifiers changed from: package-private */
    public String getLastFetchETag() {
        return this.frcMetadata.getString(LAST_FETCH_ETAG_KEY, (String) null);
    }

    public FirebaseRemoteConfigInfo getInfo() {
        FirebaseRemoteConfigInfoImpl build;
        synchronized (this.frcInfoLock) {
            long lastSuccessfulFetchTimeInMillis = this.frcMetadata.getLong(LAST_SUCCESSFUL_FETCH_TIME_IN_MILLIS_KEY, -1);
            int lastFetchStatus = this.frcMetadata.getInt(LAST_FETCH_STATUS_KEY, 0);
            build = FirebaseRemoteConfigInfoImpl.newBuilder().withLastFetchStatus(lastFetchStatus).withLastSuccessfulFetchTimeInMillis(lastSuccessfulFetchTimeInMillis).withConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(this.frcMetadata.getBoolean(DEVELOPER_MODE_KEY, false)).setFetchTimeoutInSeconds(this.frcMetadata.getLong(FETCH_TIMEOUT_IN_SECONDS_KEY, 60)).setMinimumFetchIntervalInSeconds(this.frcMetadata.getLong(MINIMUM_FETCH_INTERVAL_IN_SECONDS_KEY, ConfigFetchHandler.DEFAULT_MINIMUM_FETCH_INTERVAL_IN_SECONDS)).build()).build();
        }
        return build;
    }

    public void clear() {
        synchronized (this.frcInfoLock) {
            this.frcMetadata.edit().clear().commit();
        }
    }

    public void setConfigSettings(FirebaseRemoteConfigSettings settings) {
        synchronized (this.frcInfoLock) {
            this.frcMetadata.edit().putBoolean(DEVELOPER_MODE_KEY, settings.isDeveloperModeEnabled()).putLong(FETCH_TIMEOUT_IN_SECONDS_KEY, settings.getFetchTimeoutInSeconds()).putLong(MINIMUM_FETCH_INTERVAL_IN_SECONDS_KEY, settings.getMinimumFetchIntervalInSeconds()).commit();
        }
    }

    public void setConfigSettingsWithoutWaitingOnDiskWrite(FirebaseRemoteConfigSettings settings) {
        synchronized (this.frcInfoLock) {
            this.frcMetadata.edit().putBoolean(DEVELOPER_MODE_KEY, settings.isDeveloperModeEnabled()).putLong(FETCH_TIMEOUT_IN_SECONDS_KEY, settings.getFetchTimeoutInSeconds()).putLong(MINIMUM_FETCH_INTERVAL_IN_SECONDS_KEY, settings.getMinimumFetchIntervalInSeconds()).apply();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateLastFetchAsSuccessfulAt(Date fetchTime) {
        synchronized (this.frcInfoLock) {
            this.frcMetadata.edit().putInt(LAST_FETCH_STATUS_KEY, -1).putLong(LAST_SUCCESSFUL_FETCH_TIME_IN_MILLIS_KEY, fetchTime.getTime()).apply();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateLastFetchAsFailed() {
        synchronized (this.frcInfoLock) {
            this.frcMetadata.edit().putInt(LAST_FETCH_STATUS_KEY, 1).apply();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateLastFetchAsThrottled() {
        synchronized (this.frcInfoLock) {
            this.frcMetadata.edit().putInt(LAST_FETCH_STATUS_KEY, 2).apply();
        }
    }

    /* access modifiers changed from: package-private */
    public void setLastFetchETag(String eTag) {
        synchronized (this.frcInfoLock) {
            this.frcMetadata.edit().putString(LAST_FETCH_ETAG_KEY, eTag).apply();
        }
    }

    /* access modifiers changed from: package-private */
    public BackoffMetadata getBackoffMetadata() {
        BackoffMetadata backoffMetadata;
        synchronized (this.backoffMetadataLock) {
            backoffMetadata = new BackoffMetadata(this.frcMetadata.getInt(NUM_FAILED_FETCHES_KEY, 0), new Date(this.frcMetadata.getLong(BACKOFF_END_TIME_IN_MILLIS_KEY, -1)));
        }
        return backoffMetadata;
    }

    /* access modifiers changed from: package-private */
    public void setBackoffMetadata(int numFailedFetches, Date backoffEndTime) {
        synchronized (this.backoffMetadataLock) {
            this.frcMetadata.edit().putInt(NUM_FAILED_FETCHES_KEY, numFailedFetches).putLong(BACKOFF_END_TIME_IN_MILLIS_KEY, backoffEndTime.getTime()).apply();
        }
    }

    /* access modifiers changed from: package-private */
    public void resetBackoff() {
        setBackoffMetadata(0, NO_BACKOFF_TIME);
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    static class BackoffMetadata {
        private Date backoffEndTime;
        private int numFailedFetches;

        BackoffMetadata(int numFailedFetches2, Date backoffEndTime2) {
            this.numFailedFetches = numFailedFetches2;
            this.backoffEndTime = backoffEndTime2;
        }

        /* access modifiers changed from: package-private */
        public int getNumFailedFetches() {
            return this.numFailedFetches;
        }

        /* access modifiers changed from: package-private */
        public Date getBackoffEndTime() {
            return this.backoffEndTime;
        }
    }
}
