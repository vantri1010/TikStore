package com.google.android.datatransport.runtime.scheduling.jobscheduling;

import com.google.android.datatransport.runtime.TransportContext;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStore;
import com.google.android.datatransport.runtime.synchronization.SynchronizationGuard;
import java.util.concurrent.Executor;
import javax.inject.Inject;

/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
public class WorkInitializer {
    private final Executor executor;
    private final SynchronizationGuard guard;
    private final WorkScheduler scheduler;
    private final EventStore store;

    @Inject
    WorkInitializer(Executor executor2, EventStore store2, WorkScheduler scheduler2, SynchronizationGuard guard2) {
        this.executor = executor2;
        this.store = store2;
        this.scheduler = scheduler2;
        this.guard = guard2;
    }

    public void ensureContextsScheduled() {
        this.executor.execute(WorkInitializer$$Lambda$1.lambdaFactory$(this));
    }

    static /* synthetic */ Object lambda$ensureContextsScheduled$0(WorkInitializer workInitializer) {
        for (TransportContext context : workInitializer.store.loadActiveContexts()) {
            workInitializer.scheduler.schedule(context, 1);
        }
        return null;
    }
}
