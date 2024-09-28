package com.google.android.datatransport.runtime.scheduling;

import com.google.android.datatransport.TransportScheduleCallback;
import com.google.android.datatransport.runtime.EventInternal;
import com.google.android.datatransport.runtime.TransportContext;
import com.google.android.datatransport.runtime.TransportRuntime;
import com.google.android.datatransport.runtime.backends.BackendRegistry;
import com.google.android.datatransport.runtime.backends.TransportBackend;
import com.google.android.datatransport.runtime.scheduling.jobscheduling.WorkScheduler;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStore;
import com.google.android.datatransport.runtime.synchronization.SynchronizationGuard;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import javax.inject.Inject;

/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
public class DefaultScheduler implements Scheduler {
    private static final Logger LOGGER = Logger.getLogger(TransportRuntime.class.getName());
    private final BackendRegistry backendRegistry;
    private final EventStore eventStore;
    private final Executor executor;
    private final SynchronizationGuard guard;
    private final WorkScheduler workScheduler;

    @Inject
    public DefaultScheduler(Executor executor2, BackendRegistry backendRegistry2, WorkScheduler workScheduler2, EventStore eventStore2, SynchronizationGuard guard2) {
        this.executor = executor2;
        this.backendRegistry = backendRegistry2;
        this.workScheduler = workScheduler2;
        this.eventStore = eventStore2;
        this.guard = guard2;
    }

    public void schedule(TransportContext transportContext, EventInternal event, TransportScheduleCallback callback) {
        this.executor.execute(DefaultScheduler$$Lambda$1.lambdaFactory$(this, transportContext, callback, event));
    }

    static /* synthetic */ void lambda$schedule$1(DefaultScheduler defaultScheduler, TransportContext transportContext, TransportScheduleCallback callback, EventInternal event) {
        try {
            TransportBackend transportBackend = defaultScheduler.backendRegistry.get(transportContext.getBackendName());
            if (transportBackend == null) {
                String errorMsg = String.format("Transport backend '%s' is not registered", new Object[]{transportContext.getBackendName()});
                LOGGER.warning(errorMsg);
                callback.onSchedule(new IllegalArgumentException(errorMsg));
                return;
            }
            defaultScheduler.guard.runCriticalSection(DefaultScheduler$$Lambda$2.lambdaFactory$(defaultScheduler, transportContext, transportBackend.decorate(event)));
            callback.onSchedule((Exception) null);
        } catch (Exception e) {
            Logger logger = LOGGER;
            logger.warning("Error scheduling event " + e.getMessage());
            callback.onSchedule(e);
        }
    }

    static /* synthetic */ Object lambda$schedule$0(DefaultScheduler defaultScheduler, TransportContext transportContext, EventInternal decoratedEvent) {
        defaultScheduler.eventStore.persist(transportContext, decoratedEvent);
        defaultScheduler.workScheduler.schedule(transportContext, 1);
        return null;
    }
}
