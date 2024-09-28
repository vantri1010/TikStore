package com.google.android.datatransport.runtime.backends;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
public final class MetadataBackendRegistry_Factory implements Factory<MetadataBackendRegistry> {
    private final Provider<Context> applicationContextProvider;
    private final Provider<CreationContextFactory> creationContextFactoryProvider;

    public MetadataBackendRegistry_Factory(Provider<Context> applicationContextProvider2, Provider<CreationContextFactory> creationContextFactoryProvider2) {
        this.applicationContextProvider = applicationContextProvider2;
        this.creationContextFactoryProvider = creationContextFactoryProvider2;
    }

    public MetadataBackendRegistry get() {
        return new MetadataBackendRegistry(this.applicationContextProvider.get(), this.creationContextFactoryProvider.get());
    }

    public static MetadataBackendRegistry_Factory create(Provider<Context> applicationContextProvider2, Provider<CreationContextFactory> creationContextFactoryProvider2) {
        return new MetadataBackendRegistry_Factory(applicationContextProvider2, creationContextFactoryProvider2);
    }

    public static MetadataBackendRegistry newInstance(Context applicationContext, Object creationContextFactory) {
        return new MetadataBackendRegistry(applicationContext, (CreationContextFactory) creationContextFactory);
    }
}
