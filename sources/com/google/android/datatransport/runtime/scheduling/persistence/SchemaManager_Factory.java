package com.google.android.datatransport.runtime.scheduling.persistence;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
public final class SchemaManager_Factory implements Factory<SchemaManager> {
    private final Provider<Context> contextProvider;
    private final Provider<Integer> schemaVersionProvider;

    public SchemaManager_Factory(Provider<Context> contextProvider2, Provider<Integer> schemaVersionProvider2) {
        this.contextProvider = contextProvider2;
        this.schemaVersionProvider = schemaVersionProvider2;
    }

    public SchemaManager get() {
        return new SchemaManager(this.contextProvider.get(), this.schemaVersionProvider.get().intValue());
    }

    public static SchemaManager_Factory create(Provider<Context> contextProvider2, Provider<Integer> schemaVersionProvider2) {
        return new SchemaManager_Factory(contextProvider2, schemaVersionProvider2);
    }

    public static SchemaManager newInstance(Context context, int schemaVersion) {
        return new SchemaManager(context, schemaVersion);
    }
}
