package com.google.firebase.heartbeatinfo;

import android.content.Context;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.Dependency;
import com.google.firebase.heartbeatinfo.HeartBeatInfo;

/* compiled from: com.google.firebase:firebase-common@@19.3.0 */
public class DefaultHeartBeatInfo implements HeartBeatInfo {
    private HeartBeatInfoStorage storage;

    private DefaultHeartBeatInfo(Context context) {
        this.storage = HeartBeatInfoStorage.getInstance(context);
    }

    DefaultHeartBeatInfo(HeartBeatInfoStorage testStorage) {
        this.storage = testStorage;
    }

    public HeartBeatInfo.HeartBeat getHeartBeatCode(String heartBeatTag) {
        long presentTime = System.currentTimeMillis();
        boolean shouldSendSdkHB = this.storage.shouldSendSdkHeartBeat(heartBeatTag, presentTime);
        boolean shouldSendGlobalHB = this.storage.shouldSendGlobalHeartBeat(presentTime);
        if (shouldSendSdkHB && shouldSendGlobalHB) {
            return HeartBeatInfo.HeartBeat.COMBINED;
        }
        if (shouldSendGlobalHB) {
            return HeartBeatInfo.HeartBeat.GLOBAL;
        }
        if (shouldSendSdkHB) {
            return HeartBeatInfo.HeartBeat.SDK;
        }
        return HeartBeatInfo.HeartBeat.NONE;
    }

    public static Component<HeartBeatInfo> component() {
        return Component.builder(HeartBeatInfo.class).add(Dependency.required(Context.class)).factory(DefaultHeartBeatInfo$$Lambda$1.lambdaFactory$()).build();
    }

    static /* synthetic */ HeartBeatInfo lambda$component$0(ComponentContainer c) {
        return new DefaultHeartBeatInfo((Context) c.get(Context.class));
    }
}
