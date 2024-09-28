package com.google.firebase.remoteconfig;

import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.abt.FirebaseABTesting;
import com.google.firebase.abt.component.AbtComponent;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentContainer;
import com.google.firebase.components.ComponentRegistrar;
import com.google.firebase.components.Dependency;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.platforminfo.LibraryVersionComponent;
import java.util.Arrays;
import java.util.List;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class RemoteConfigRegistrar implements ComponentRegistrar {
    public List<Component<?>> getComponents() {
        return Arrays.asList(new Component[]{Component.builder(RemoteConfigComponent.class).add(Dependency.required(Context.class)).add(Dependency.required(FirebaseApp.class)).add(Dependency.required(FirebaseInstanceId.class)).add(Dependency.required(AbtComponent.class)).add(Dependency.optional(AnalyticsConnector.class)).factory(RemoteConfigRegistrar$$Lambda$1.lambdaFactory$()).eagerInDefaultApp().build(), LibraryVersionComponent.create("fire-rc", BuildConfig.VERSION_NAME)});
    }

    static /* synthetic */ RemoteConfigComponent lambda$getComponents$0(ComponentContainer container) {
        return new RemoteConfigComponent((Context) container.get(Context.class), (FirebaseApp) container.get(FirebaseApp.class), (FirebaseInstanceId) container.get(FirebaseInstanceId.class), ((AbtComponent) container.get(AbtComponent.class)).get(FirebaseABTesting.OriginService.REMOTE_CONFIG), (AnalyticsConnector) container.get(AnalyticsConnector.class));
    }
}
