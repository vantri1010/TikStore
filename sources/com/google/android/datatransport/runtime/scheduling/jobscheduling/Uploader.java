package com.google.android.datatransport.runtime.scheduling.jobscheduling;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.datatransport.runtime.EventInternal;
import com.google.android.datatransport.runtime.TransportContext;
import com.google.android.datatransport.runtime.backends.BackendRegistry;
import com.google.android.datatransport.runtime.backends.BackendRequest;
import com.google.android.datatransport.runtime.backends.BackendResponse;
import com.google.android.datatransport.runtime.backends.TransportBackend;
import com.google.android.datatransport.runtime.logging.Logging;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStore;
import com.google.android.datatransport.runtime.scheduling.persistence.PersistedEvent;
import com.google.android.datatransport.runtime.synchronization.SynchronizationException;
import com.google.android.datatransport.runtime.synchronization.SynchronizationGuard;
import com.google.android.datatransport.runtime.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;

/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
public class Uploader {
    private static final String LOG_TAG = "Uploader";
    private final BackendRegistry backendRegistry;
    private final Clock clock;
    private final Context context;
    private final EventStore eventStore;
    private final Executor executor;
    private final SynchronizationGuard guard;
    private final WorkScheduler workScheduler;

    @Inject
    public Uploader(Context context2, BackendRegistry backendRegistry2, EventStore eventStore2, WorkScheduler workScheduler2, Executor executor2, SynchronizationGuard guard2, Clock clock2) {
        this.context = context2;
        this.backendRegistry = backendRegistry2;
        this.eventStore = eventStore2;
        this.workScheduler = workScheduler2;
        this.executor = executor2;
        this.guard = guard2;
        this.clock = clock2;
    }

    /* access modifiers changed from: package-private */
    public boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void upload(TransportContext transportContext, int attemptNumber, Runnable callback) {
        this.executor.execute(Uploader$$Lambda$1.lambdaFactory$(this, transportContext, attemptNumber, callback));
    }

    static /* synthetic */ void lambda$upload$1(Uploader uploader, TransportContext transportContext, int attemptNumber, Runnable callback) {
        try {
            SynchronizationGuard synchronizationGuard = uploader.guard;
            EventStore eventStore2 = uploader.eventStore;
            eventStore2.getClass();
            synchronizationGuard.runCriticalSection(Uploader$$Lambda$4.lambdaFactory$(eventStore2));
            if (!uploader.isNetworkAvailable()) {
                uploader.guard.runCriticalSection(Uploader$$Lambda$5.lambdaFactory$(uploader, transportContext, attemptNumber));
            } else {
                uploader.logAndUpdateState(transportContext, attemptNumber);
            }
        } catch (SynchronizationException e) {
            uploader.workScheduler.schedule(transportContext, attemptNumber + 1);
        } catch (Throwable th) {
            callback.run();
            throw th;
        }
        callback.run();
    }

    /* access modifiers changed from: package-private */
    public void logAndUpdateState(TransportContext transportContext, int attemptNumber) {
        BackendResponse response;
        TransportBackend backend = this.backendRegistry.get(transportContext.getBackendName());
        Iterable<PersistedEvent> persistedEvents = (Iterable) this.guard.runCriticalSection(Uploader$$Lambda$2.lambdaFactory$(this, transportContext));
        if (persistedEvents.iterator().hasNext()) {
            if (backend == null) {
                Logging.d(LOG_TAG, "Unknown backend for %s, deleting event batch for it...", (Object) transportContext);
                response = BackendResponse.fatalError();
            } else {
                List<EventInternal> eventInternals = new ArrayList<>();
                for (PersistedEvent persistedEvent : persistedEvents) {
                    eventInternals.add(persistedEvent.getEvent());
                }
                response = backend.send(BackendRequest.builder().setEvents(eventInternals).setExtras(transportContext.getExtras()).build());
            }
            this.guard.runCriticalSection(Uploader$$Lambda$3.lambdaFactory$(this, response, persistedEvents, transportContext, attemptNumber));
        }
    }

    static /* synthetic */ Object lambda$logAndUpdateState$3(Uploader uploader, BackendResponse response, Iterable persistedEvents, TransportContext transportContext, int attemptNumber) {
        if (response.getStatus() == BackendResponse.Status.TRANSIENT_ERROR) {
            uploader.eventStore.recordFailure(persistedEvents);
            uploader.workScheduler.schedule(transportContext, attemptNumber + 1);
            return null;
        }
        uploader.eventStore.recordSuccess(persistedEvents);
        if (response.getStatus() == BackendResponse.Status.OK) {
            uploader.eventStore.recordNextCallTime(transportContext, uploader.clock.getTime() + response.getNextRequestWaitMillis());
        }
        if (!uploader.eventStore.hasPendingEventsFor(transportContext)) {
            return null;
        }
        uploader.workScheduler.schedule(transportContext, 1);
        return null;
    }
}
