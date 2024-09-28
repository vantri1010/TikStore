package com.google.firebase.remoteconfig;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class FirebaseRemoteConfigServerException extends FirebaseRemoteConfigException {
    private final int httpStatusCode;

    public FirebaseRemoteConfigServerException(int httpStatusCode2, String detailMessage) {
        super(detailMessage);
        this.httpStatusCode = httpStatusCode2;
    }

    public FirebaseRemoteConfigServerException(int httpStatusCode2, String detailMessage, Throwable cause) {
        super(detailMessage, cause);
        this.httpStatusCode = httpStatusCode2;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }
}
