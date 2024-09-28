package com.google.firebase.abt.component;

import android.content.Context;
import com.google.firebase.abt.FirebaseABTesting;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import java.util.HashMap;
import java.util.Map;

/* compiled from: com.google.firebase:firebase-abt@@19.0.0 */
public class AbtComponent {
    private final Map<String, FirebaseABTesting> abtOriginInstances = new HashMap();
    private final AnalyticsConnector analyticsConnector;
    private final Context appContext;

    protected AbtComponent(Context appContext2, AnalyticsConnector analyticsConnector2) {
        this.appContext = appContext2;
        this.analyticsConnector = analyticsConnector2;
    }

    public synchronized FirebaseABTesting get(String originService) {
        if (!this.abtOriginInstances.containsKey(originService)) {
            this.abtOriginInstances.put(originService, createAbtInstance(originService));
        }
        return this.abtOriginInstances.get(originService);
    }

    /* access modifiers changed from: protected */
    public FirebaseABTesting createAbtInstance(String originService) {
        return new FirebaseABTesting(this.appContext, this.analyticsConnector, originService);
    }
}
