package com.google.firebase.remoteconfig;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class FirebaseRemoteConfigFetchThrottledException extends FirebaseRemoteConfigFetchException {
    private final long throttleEndTimeMillis;

    public FirebaseRemoteConfigFetchThrottledException(long throttleEndTimeMillis2) {
        this("Fetch was throttled.", throttleEndTimeMillis2);
    }

    public FirebaseRemoteConfigFetchThrottledException(String message, long throttledEndTimeInMillis) {
        super(message);
        this.throttleEndTimeMillis = throttledEndTimeInMillis;
    }

    public long getThrottleEndTimeMillis() {
        return this.throttleEndTimeMillis;
    }
}
