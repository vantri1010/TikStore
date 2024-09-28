package com.google.android.datatransport.runtime.backends;

import android.content.Context;
import com.google.android.datatransport.runtime.time.Clock;

/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
public abstract class CreationContext {
    private static final String DEFAULT_BACKEND_NAME = "cct";

    public abstract Context getApplicationContext();

    public abstract String getBackendName();

    public abstract Clock getMonotonicClock();

    public abstract Clock getWallClock();

    public static CreationContext create(Context applicationContext, Clock wallClock, Clock monotonicClock) {
        return new AutoValue_CreationContext(applicationContext, wallClock, monotonicClock, DEFAULT_BACKEND_NAME);
    }

    public static CreationContext create(Context applicationContext, Clock wallClock, Clock monotonicClock, String backendName) {
        return new AutoValue_CreationContext(applicationContext, wallClock, monotonicClock, backendName);
    }
}
