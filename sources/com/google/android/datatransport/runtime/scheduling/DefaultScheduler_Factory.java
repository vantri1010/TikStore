package com.google.android.datatransport.runtime.scheduling;

import com.google.android.datatransport.runtime.backends.BackendRegistry;
import com.google.android.datatransport.runtime.scheduling.jobscheduling.WorkScheduler;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStore;
import com.google.android.datatransport.runtime.synchronization.SynchronizationGuard;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
public final class DefaultScheduler_Factory implements Factory<DefaultScheduler> {
    private final Provider<BackendRegistry> backendRegistryProvider;
    private final Provider<EventStore> eventStoreProvider;
    private final Provider<Executor> executorProvider;
    private final Provider<SynchronizationGuard> guardProvider;
    private final Provider<WorkScheduler> workSchedulerProvider;

    public DefaultScheduler_Factory(Provider<Executor> executorProvider2, Provider<BackendRegistry> backendRegistryProvider2, Provider<WorkScheduler> workSchedulerProvider2, Provider<EventStore> eventStoreProvider2, Provider<SynchronizationGuard> guardProvider2) {
        this.executorProvider = executorProvider2;
        this.backendRegistryProvider = backendRegistryProvider2;
        this.workSchedulerProvider = workSchedulerProvider2;
        this.eventStoreProvider = eventStoreProvider2;
        this.guardProvider = guardProvider2;
    }

    public DefaultScheduler get() {
        return new DefaultScheduler(this.executorProvider.get(), this.backendRegistryProvider.get(), this.workSchedulerProvider.get(), this.eventStoreProvider.get(), this.guardProvider.get());
    }

    public static DefaultScheduler_Factory create(Provider<Executor> executorProvider2, Provider<BackendRegistry> backendRegistryProvider2, Provider<WorkScheduler> workSchedulerProvider2, Provider<EventStore> eventStoreProvider2, Provider<SynchronizationGuard> guardProvider2) {
        return new DefaultScheduler_Factory(executorProvider2, backendRegistryProvider2, workSchedulerProvider2, eventStoreProvider2, guardProvider2);
    }

    public static DefaultScheduler newInstance(Executor executor, BackendRegistry backendRegistry, WorkScheduler workScheduler, EventStore eventStore, SynchronizationGuard guard) {
        return new DefaultScheduler(executor, backendRegistry, workScheduler, eventStore, guard);
    }
}
