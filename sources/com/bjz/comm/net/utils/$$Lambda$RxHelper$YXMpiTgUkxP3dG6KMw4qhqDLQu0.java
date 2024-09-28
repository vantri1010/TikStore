package com.bjz.comm.net.utils;

import io.reactivex.Observable;
import java.util.concurrent.Callable;

/* renamed from: com.bjz.comm.net.utils.-$$Lambda$RxHelper$YXMpiTgUkxP3dG6KMw4qhqDLQu0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$RxHelper$YXMpiTgUkxP3dG6KMw4qhqDLQu0 implements Callable {
    public static final /* synthetic */ $$Lambda$RxHelper$YXMpiTgUkxP3dG6KMw4qhqDLQu0 INSTANCE = new $$Lambda$RxHelper$YXMpiTgUkxP3dG6KMw4qhqDLQu0();

    private /* synthetic */ $$Lambda$RxHelper$YXMpiTgUkxP3dG6KMw4qhqDLQu0() {
    }

    public final Object call() {
        return Observable.just(HttpUtils.getInstance().getAuthorization());
    }
}
