package com.google.android.gms.internal.vision;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public enum zzfy {
    INT(0),
    LONG(0L),
    FLOAT(Float.valueOf(0.0f)),
    DOUBLE(Double.valueOf(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE)),
    BOOLEAN(false),
    STRING(""),
    BYTE_STRING(zzbo.zzgt),
    ENUM((String) null),
    MESSAGE((String) null);
    
    private final Object zzme;

    private zzfy(Object obj) {
        this.zzme = obj;
    }
}
