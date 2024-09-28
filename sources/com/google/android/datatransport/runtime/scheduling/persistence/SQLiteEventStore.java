package com.google.android.datatransport.runtime.scheduling.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.SystemClock;
import android.util.Base64;
import com.google.android.datatransport.Encoding;
import com.google.android.datatransport.runtime.EncodedPayload;
import com.google.android.datatransport.runtime.EventInternal;
import com.google.android.datatransport.runtime.TransportContext;
import com.google.android.datatransport.runtime.logging.Logging;
import com.google.android.datatransport.runtime.synchronization.SynchronizationException;
import com.google.android.datatransport.runtime.synchronization.SynchronizationGuard;
import com.google.android.datatransport.runtime.time.Clock;
import com.google.android.datatransport.runtime.util.PriorityMapping;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
public class SQLiteEventStore implements EventStore, SynchronizationGuard {
    private static final int LOCK_RETRY_BACK_OFF_MILLIS = 50;
    private static final String LOG_TAG = "SQLiteEventStore";
    static final int MAX_RETRIES = 10;
    private static final Encoding PROTOBUF_ENCODING = Encoding.of("proto");
    private final EventStoreConfig config;
    private final Clock monotonicClock;
    private final SchemaManager schemaManager;
    private final Clock wallClock;

    /* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
    interface Function<T, U> {
        U apply(T t);
    }

    /* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
    interface Producer<T> {
        T produce();
    }

    @Inject
    SQLiteEventStore(Clock wallClock2, Clock clock, EventStoreConfig config2, SchemaManager schemaManager2) {
        this.schemaManager = schemaManager2;
        this.wallClock = wallClock2;
        this.monotonicClock = clock;
        this.config = config2;
    }

    private SQLiteDatabase getDb() {
        SchemaManager schemaManager2 = this.schemaManager;
        schemaManager2.getClass();
        return (SQLiteDatabase) retryIfDbLocked(SQLiteEventStore$$Lambda$1.lambdaFactory$(schemaManager2), SQLiteEventStore$$Lambda$4.lambdaFactory$());
    }

    static /* synthetic */ SQLiteDatabase lambda$getDb$0(Throwable ex) {
        throw new SynchronizationException("Timed out while trying to open db.", ex);
    }

    public PersistedEvent persist(TransportContext transportContext, EventInternal event) {
        Logging.d(LOG_TAG, "Storing event with priority=%s, name=%s for destination %s", transportContext.getPriority(), event.getTransportName(), transportContext.getBackendName());
        long newRowId = ((Long) inTransaction(SQLiteEventStore$$Lambda$5.lambdaFactory$(this, transportContext, event))).longValue();
        if (newRowId < 1) {
            return null;
        }
        return PersistedEvent.create(newRowId, transportContext, event);
    }

    static /* synthetic */ Long lambda$persist$1(SQLiteEventStore sQLiteEventStore, TransportContext transportContext, EventInternal event, SQLiteDatabase db) {
        if (sQLiteEventStore.isStorageAtLimit()) {
            return -1L;
        }
        long contextId = sQLiteEventStore.ensureTransportContext(db, transportContext);
        ContentValues values = new ContentValues();
        values.put("context_id", Long.valueOf(contextId));
        values.put("transport_name", event.getTransportName());
        values.put("timestamp_ms", Long.valueOf(event.getEventMillis()));
        values.put("uptime_ms", Long.valueOf(event.getUptimeMillis()));
        values.put("payload_encoding", event.getEncodedPayload().getEncoding().getName());
        values.put("payload", event.getEncodedPayload().getBytes());
        values.put("code", event.getCode());
        values.put("num_attempts", 0);
        long newEventId = db.insert("events", (String) null, values);
        for (Map.Entry<String, String> entry : event.getMetadata().entrySet()) {
            ContentValues metadata = new ContentValues();
            metadata.put("event_id", Long.valueOf(newEventId));
            metadata.put("name", entry.getKey());
            metadata.put("value", entry.getValue());
            db.insert("event_metadata", (String) null, metadata);
        }
        return Long.valueOf(newEventId);
    }

    private long ensureTransportContext(SQLiteDatabase db, TransportContext transportContext) {
        Long existingId = getTransportContextId(db, transportContext);
        if (existingId != null) {
            return existingId.longValue();
        }
        ContentValues record = new ContentValues();
        record.put("backend_name", transportContext.getBackendName());
        record.put("priority", Integer.valueOf(PriorityMapping.toInt(transportContext.getPriority())));
        record.put("next_request_ms", 0);
        if (transportContext.getExtras() != null) {
            record.put("extras", Base64.encodeToString(transportContext.getExtras(), 0));
        }
        return db.insert("transport_contexts", (String) null, record);
    }

    private Long getTransportContextId(SQLiteDatabase db, TransportContext transportContext) {
        StringBuilder selection = new StringBuilder("backend_name = ? and priority = ?");
        ArrayList<String> selectionArgs = new ArrayList<>(Arrays.asList(new String[]{transportContext.getBackendName(), String.valueOf(PriorityMapping.toInt(transportContext.getPriority()))}));
        if (transportContext.getExtras() != null) {
            selection.append(" and extras = ?");
            selectionArgs.add(Base64.encodeToString(transportContext.getExtras(), 0));
        }
        return (Long) tryWithCursor(db.query("transport_contexts", new String[]{"_id"}, selection.toString(), (String[]) selectionArgs.toArray(new String[0]), (String) null, (String) null, (String) null), SQLiteEventStore$$Lambda$6.lambdaFactory$());
    }

    static /* synthetic */ Long lambda$getTransportContextId$2(Cursor cursor) {
        if (!cursor.moveToNext()) {
            return null;
        }
        return Long.valueOf(cursor.getLong(0));
    }

    public void recordFailure(Iterable<PersistedEvent> events) {
        if (events.iterator().hasNext()) {
            inTransaction(SQLiteEventStore$$Lambda$7.lambdaFactory$("UPDATE events SET num_attempts = num_attempts + 1 WHERE _id in " + toIdList(events)));
        }
    }

    static /* synthetic */ Object lambda$recordFailure$3(String query, SQLiteDatabase db) {
        db.compileStatement(query).execute();
        db.compileStatement("DELETE FROM events WHERE num_attempts >= 10").execute();
        return null;
    }

    public void recordSuccess(Iterable<PersistedEvent> events) {
        if (events.iterator().hasNext()) {
            getDb().compileStatement("DELETE FROM events WHERE _id in " + toIdList(events)).execute();
        }
    }

    private static String toIdList(Iterable<PersistedEvent> events) {
        StringBuilder idList = new StringBuilder(SQLBuilder.PARENTHESES_LEFT);
        Iterator<PersistedEvent> iterator = events.iterator();
        while (iterator.hasNext()) {
            idList.append(iterator.next().getId());
            if (iterator.hasNext()) {
                idList.append(',');
            }
        }
        idList.append(')');
        return idList.toString();
    }

    public long getNextCallTime(TransportContext transportContext) {
        return ((Long) tryWithCursor(getDb().rawQuery("SELECT next_request_ms FROM transport_contexts WHERE backend_name = ? and priority = ?", new String[]{transportContext.getBackendName(), String.valueOf(PriorityMapping.toInt(transportContext.getPriority()))}), SQLiteEventStore$$Lambda$8.lambdaFactory$())).longValue();
    }

    static /* synthetic */ Long lambda$getNextCallTime$4(Cursor cursor) {
        if (cursor.moveToNext()) {
            return Long.valueOf(cursor.getLong(0));
        }
        return 0L;
    }

    public boolean hasPendingEventsFor(TransportContext transportContext) {
        return ((Boolean) inTransaction(SQLiteEventStore$$Lambda$9.lambdaFactory$(this, transportContext))).booleanValue();
    }

    static /* synthetic */ Boolean lambda$hasPendingEventsFor$5(SQLiteEventStore sQLiteEventStore, TransportContext transportContext, SQLiteDatabase db) {
        Long contextId = sQLiteEventStore.getTransportContextId(db, transportContext);
        if (contextId == null) {
            return false;
        }
        return (Boolean) tryWithCursor(sQLiteEventStore.getDb().rawQuery("SELECT 1 FROM events WHERE context_id = ? LIMIT 1", new String[]{contextId.toString()}), SQLiteEventStore$$Lambda$20.lambdaFactory$());
    }

    public void recordNextCallTime(TransportContext transportContext, long timestampMs) {
        inTransaction(SQLiteEventStore$$Lambda$10.lambdaFactory$(timestampMs, transportContext));
    }

    static /* synthetic */ Object lambda$recordNextCallTime$6(long timestampMs, TransportContext transportContext, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("next_request_ms", Long.valueOf(timestampMs));
        if (db.update("transport_contexts", values, "backend_name = ? and priority = ?", new String[]{transportContext.getBackendName(), String.valueOf(PriorityMapping.toInt(transportContext.getPriority()))}) < 1) {
            values.put("backend_name", transportContext.getBackendName());
            values.put("priority", Integer.valueOf(PriorityMapping.toInt(transportContext.getPriority())));
            db.insert("transport_contexts", (String) null, values);
        }
        return null;
    }

    public Iterable<PersistedEvent> loadBatch(TransportContext transportContext) {
        return (Iterable) inTransaction(SQLiteEventStore$$Lambda$11.lambdaFactory$(this, transportContext));
    }

    public Iterable<TransportContext> loadActiveContexts() {
        return (Iterable) inTransaction(SQLiteEventStore$$Lambda$12.lambdaFactory$());
    }

    static /* synthetic */ List lambda$loadActiveContexts$9(SQLiteDatabase db) {
        return (List) tryWithCursor(db.rawQuery("SELECT distinct t._id, t.backend_name, t.priority, t.extras FROM transport_contexts AS t, events AS e WHERE e.context_id = t._id", new String[0]), SQLiteEventStore$$Lambda$19.lambdaFactory$());
    }

    static /* synthetic */ List lambda$loadActiveContexts$8(Cursor cursor) {
        List<TransportContext> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(TransportContext.builder().setBackendName(cursor.getString(1)).setPriority(PriorityMapping.valueOf(cursor.getInt(2))).setExtras(maybeBase64Decode(cursor.getString(3))).build());
        }
        return results;
    }

    public int cleanUp() {
        return ((Integer) inTransaction(SQLiteEventStore$$Lambda$13.lambdaFactory$(this.wallClock.getTime() - this.config.getEventCleanUpAge()))).intValue();
    }

    public void close() {
        this.schemaManager.close();
    }

    public void clearDb() {
        inTransaction(SQLiteEventStore$$Lambda$14.lambdaFactory$());
    }

    static /* synthetic */ Object lambda$clearDb$11(SQLiteDatabase db) {
        db.delete("events", (String) null, new String[0]);
        db.delete("transport_contexts", (String) null, new String[0]);
        return null;
    }

    private static byte[] maybeBase64Decode(String value) {
        if (value == null) {
            return null;
        }
        return Base64.decode(value, 0);
    }

    /* access modifiers changed from: private */
    public List<PersistedEvent> loadEvents(SQLiteDatabase db, TransportContext transportContext) {
        List<PersistedEvent> events = new ArrayList<>();
        Long contextId = getTransportContextId(db, transportContext);
        if (contextId == null) {
            return events;
        }
        SQLiteDatabase sQLiteDatabase = db;
        tryWithCursor(sQLiteDatabase.query("events", new String[]{"_id", "transport_name", "timestamp_ms", "uptime_ms", "payload_encoding", "payload", "code"}, "context_id = ?", new String[]{contextId.toString()}, (String) null, (String) null, (String) null, String.valueOf(this.config.getLoadBatchSize())), SQLiteEventStore$$Lambda$15.lambdaFactory$(events, transportContext));
        return events;
    }

    static /* synthetic */ Object lambda$loadEvents$12(List events, TransportContext transportContext, Cursor cursor) {
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            EventInternal.Builder event = EventInternal.builder().setTransportName(cursor.getString(1)).setEventMillis(cursor.getLong(2)).setUptimeMillis(cursor.getLong(3)).setEncodedPayload(new EncodedPayload(toEncoding(cursor.getString(4)), cursor.getBlob(5)));
            if (!cursor.isNull(6)) {
                event.setCode(Integer.valueOf(cursor.getInt(6)));
            }
            events.add(PersistedEvent.create(id, transportContext, event.build()));
        }
        return null;
    }

    private static Encoding toEncoding(String value) {
        if (value == null) {
            return PROTOBUF_ENCODING;
        }
        return Encoding.of(value);
    }

    private Map<Long, Set<Metadata>> loadMetadata(SQLiteDatabase db, List<PersistedEvent> events) {
        Map<Long, Set<Metadata>> metadataIndex = new HashMap<>();
        StringBuilder whereClause = new StringBuilder("event_id IN (");
        for (int i = 0; i < events.size(); i++) {
            whereClause.append(events.get(i).getId());
            if (i < events.size() - 1) {
                whereClause.append(',');
            }
        }
        whereClause.append(')');
        tryWithCursor(db.query("event_metadata", new String[]{"event_id", "name", "value"}, whereClause.toString(), (String[]) null, (String) null, (String) null, (String) null), SQLiteEventStore$$Lambda$16.lambdaFactory$(metadataIndex));
        return metadataIndex;
    }

    static /* synthetic */ Object lambda$loadMetadata$13(Map metadataIndex, Cursor cursor) {
        while (cursor.moveToNext()) {
            long eventId = cursor.getLong(0);
            Set<Metadata> currentSet = (Set) metadataIndex.get(Long.valueOf(eventId));
            if (currentSet == null) {
                currentSet = new HashSet<>();
                metadataIndex.put(Long.valueOf(eventId), currentSet);
            }
            currentSet.add(new Metadata(cursor.getString(1), cursor.getString(2)));
        }
        return null;
    }

    private List<PersistedEvent> join(List<PersistedEvent> events, Map<Long, Set<Metadata>> metadataIndex) {
        ListIterator<PersistedEvent> iterator = events.listIterator();
        while (iterator.hasNext()) {
            PersistedEvent current = iterator.next();
            if (metadataIndex.containsKey(Long.valueOf(current.getId()))) {
                EventInternal.Builder newEvent = current.getEvent().toBuilder();
                for (Metadata metadata : metadataIndex.get(Long.valueOf(current.getId()))) {
                    newEvent.addMetadata(metadata.key, metadata.value);
                }
                iterator.set(PersistedEvent.create(current.getId(), current.getTransportContext(), newEvent.build()));
            }
        }
        return events;
    }

    private <T> T retryIfDbLocked(Producer<T> retriable, Function<Throwable, T> failureHandler) {
        long startTime = this.monotonicClock.getTime();
        while (true) {
            try {
                return retriable.produce();
            } catch (SQLiteDatabaseLockedException ex) {
                if (this.monotonicClock.getTime() >= ((long) this.config.getCriticalSectionEnterTimeoutMs()) + startTime) {
                    return failureHandler.apply(ex);
                }
                SystemClock.sleep(50);
            }
        }
    }

    private void ensureBeginTransaction(SQLiteDatabase db) {
        retryIfDbLocked(SQLiteEventStore$$Lambda$17.lambdaFactory$(db), SQLiteEventStore$$Lambda$18.lambdaFactory$());
    }

    static /* synthetic */ Object lambda$ensureBeginTransaction$15(Throwable ex) {
        throw new SynchronizationException("Timed out while trying to acquire the lock.", ex);
    }

    public <T> T runCriticalSection(SynchronizationGuard.CriticalSection<T> criticalSection) {
        SQLiteDatabase db = getDb();
        ensureBeginTransaction(db);
        try {
            T result = criticalSection.execute();
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    private <T> T inTransaction(Function<SQLiteDatabase, T> function) {
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        try {
            T result = function.apply(db);
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    /* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
    private static class Metadata {
        final String key;
        final String value;

        private Metadata(String key2, String value2) {
            this.key = key2;
            this.value = value2;
        }
    }

    private boolean isStorageAtLimit() {
        return getPageCount() * getPageSize() >= this.config.getMaxStorageSizeInBytes();
    }

    /* access modifiers changed from: package-private */
    public long getByteSize() {
        return getPageCount() * getPageSize();
    }

    private long getPageSize() {
        return getDb().compileStatement("PRAGMA page_size").simpleQueryForLong();
    }

    private long getPageCount() {
        return getDb().compileStatement("PRAGMA page_count").simpleQueryForLong();
    }

    private static <T> T tryWithCursor(Cursor c, Function<Cursor, T> function) {
        try {
            return function.apply(c);
        } finally {
            c.close();
        }
    }
}
