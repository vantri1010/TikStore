package com.google.firebase.remoteconfig;

import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
final /* synthetic */ class FirebaseRemoteConfig$$Lambda$10 implements SuccessContinuation {
    private static final FirebaseRemoteConfig$$Lambda$10 instance = new FirebaseRemoteConfig$$Lambda$10();

    private FirebaseRemoteConfig$$Lambda$10() {
    }

    public static SuccessContinuation lambdaFactory$() {
        return instance;
    }

    public Task then(Object obj) {
        return Tasks.forResult(null);
    }
}
