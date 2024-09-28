package com.google.firebase.remoteconfig.internal;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class ConfigCacheClient {
    private static final Executor DIRECT_EXECUTOR = ConfigCacheClient$$Lambda$4.lambdaFactory$();
    static final long DISK_READ_TIMEOUT_IN_SECONDS = 5;
    private static final Map<String, ConfigCacheClient> clientInstances = new HashMap();
    private Task<ConfigContainer> cachedContainerTask = null;
    private final ExecutorService executorService;
    private final ConfigStorageClient storageClient;

    private ConfigCacheClient(ExecutorService executorService2, ConfigStorageClient storageClient2) {
        this.executorService = executorService2;
        this.storageClient = storageClient2;
    }

    public Task<ConfigContainer> putWithoutWaitingForDiskWrite(ConfigContainer configContainer) {
        updateInMemoryConfigContainer(configContainer);
        return put(configContainer, false);
    }

    public ConfigContainer getBlocking() {
        return getBlocking(DISK_READ_TIMEOUT_IN_SECONDS);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0024, code lost:
        return (com.google.firebase.remoteconfig.internal.ConfigContainer) await(get(), r4, java.util.concurrent.TimeUnit.SECONDS);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0025, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002a, code lost:
        android.util.Log.d(com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG, "Reading from storage file failed.", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0032, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.firebase.remoteconfig.internal.ConfigContainer getBlocking(long r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            com.google.android.gms.tasks.Task<com.google.firebase.remoteconfig.internal.ConfigContainer> r0 = r3.cachedContainerTask     // Catch:{ all -> 0x0033 }
            if (r0 == 0) goto L_0x0017
            com.google.android.gms.tasks.Task<com.google.firebase.remoteconfig.internal.ConfigContainer> r0 = r3.cachedContainerTask     // Catch:{ all -> 0x0033 }
            boolean r0 = r0.isSuccessful()     // Catch:{ all -> 0x0033 }
            if (r0 == 0) goto L_0x0017
            com.google.android.gms.tasks.Task<com.google.firebase.remoteconfig.internal.ConfigContainer> r0 = r3.cachedContainerTask     // Catch:{ all -> 0x0033 }
            java.lang.Object r0 = r0.getResult()     // Catch:{ all -> 0x0033 }
            com.google.firebase.remoteconfig.internal.ConfigContainer r0 = (com.google.firebase.remoteconfig.internal.ConfigContainer) r0     // Catch:{ all -> 0x0033 }
            monitor-exit(r3)     // Catch:{ all -> 0x0033 }
            return r0
        L_0x0017:
            monitor-exit(r3)     // Catch:{ all -> 0x0033 }
            com.google.android.gms.tasks.Task r0 = r3.get()     // Catch:{ InterruptedException -> 0x0029, ExecutionException -> 0x0027, TimeoutException -> 0x0025 }
            java.util.concurrent.TimeUnit r1 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ InterruptedException -> 0x0029, ExecutionException -> 0x0027, TimeoutException -> 0x0025 }
            java.lang.Object r0 = await(r0, r4, r1)     // Catch:{ InterruptedException -> 0x0029, ExecutionException -> 0x0027, TimeoutException -> 0x0025 }
            com.google.firebase.remoteconfig.internal.ConfigContainer r0 = (com.google.firebase.remoteconfig.internal.ConfigContainer) r0     // Catch:{ InterruptedException -> 0x0029, ExecutionException -> 0x0027, TimeoutException -> 0x0025 }
            return r0
        L_0x0025:
            r0 = move-exception
            goto L_0x002a
        L_0x0027:
            r0 = move-exception
            goto L_0x002a
        L_0x0029:
            r0 = move-exception
        L_0x002a:
            java.lang.String r1 = "FirebaseRemoteConfig"
            java.lang.String r2 = "Reading from storage file failed."
            android.util.Log.d(r1, r2, r0)
            r1 = 0
            return r1
        L_0x0033:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0033 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.remoteconfig.internal.ConfigCacheClient.getBlocking(long):com.google.firebase.remoteconfig.internal.ConfigContainer");
    }

    public Task<ConfigContainer> put(ConfigContainer configContainer) {
        return put(configContainer, true);
    }

    public Task<ConfigContainer> put(ConfigContainer configContainer, boolean shouldUpdateInMemoryContainer) {
        return Tasks.call(this.executorService, ConfigCacheClient$$Lambda$1.lambdaFactory$(this, configContainer)).onSuccessTask(this.executorService, ConfigCacheClient$$Lambda$2.lambdaFactory$(this, shouldUpdateInMemoryContainer, configContainer));
    }

    static /* synthetic */ Task lambda$put$1(ConfigCacheClient configCacheClient, boolean shouldUpdateInMemoryContainer, ConfigContainer configContainer, Void unusedVoid) throws Exception {
        if (shouldUpdateInMemoryContainer) {
            configCacheClient.updateInMemoryConfigContainer(configContainer);
        }
        return Tasks.forResult(configContainer);
    }

    public synchronized Task<ConfigContainer> get() {
        if (this.cachedContainerTask == null || (this.cachedContainerTask.isComplete() && !this.cachedContainerTask.isSuccessful())) {
            ExecutorService executorService2 = this.executorService;
            ConfigStorageClient configStorageClient = this.storageClient;
            configStorageClient.getClass();
            this.cachedContainerTask = Tasks.call(executorService2, ConfigCacheClient$$Lambda$3.lambdaFactory$(configStorageClient));
        }
        return this.cachedContainerTask;
    }

    public void clear() {
        synchronized (this) {
            this.cachedContainerTask = Tasks.forResult(null);
        }
        this.storageClient.clear();
    }

    private synchronized void updateInMemoryConfigContainer(ConfigContainer configContainer) {
        this.cachedContainerTask = Tasks.forResult(configContainer);
    }

    /* access modifiers changed from: package-private */
    public synchronized Task<ConfigContainer> getCachedContainerTask() {
        return this.cachedContainerTask;
    }

    public static synchronized ConfigCacheClient getInstance(ExecutorService executorService2, ConfigStorageClient storageClient2) {
        ConfigCacheClient configCacheClient;
        synchronized (ConfigCacheClient.class) {
            String fileName = storageClient2.getFileName();
            if (!clientInstances.containsKey(fileName)) {
                clientInstances.put(fileName, new ConfigCacheClient(executorService2, storageClient2));
            }
            configCacheClient = clientInstances.get(fileName);
        }
        return configCacheClient;
    }

    public static synchronized void clearInstancesForTest() {
        synchronized (ConfigCacheClient.class) {
            clientInstances.clear();
        }
    }

    private static <TResult> TResult await(Task<TResult> task, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        AwaitListener<TResult> waiter = new AwaitListener<>();
        task.addOnSuccessListener(DIRECT_EXECUTOR, (OnSuccessListener<? super TResult>) waiter);
        task.addOnFailureListener(DIRECT_EXECUTOR, (OnFailureListener) waiter);
        task.addOnCanceledListener(DIRECT_EXECUTOR, (OnCanceledListener) waiter);
        if (!waiter.await(timeout, unit)) {
            throw new TimeoutException("Task await timed out.");
        } else if (task.isSuccessful()) {
            return task.getResult();
        } else {
            throw new ExecutionException(task.getException());
        }
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    private static class AwaitListener<TResult> implements OnSuccessListener<TResult>, OnFailureListener, OnCanceledListener {
        private final CountDownLatch latch;

        private AwaitListener() {
            this.latch = new CountDownLatch(1);
        }

        public void onSuccess(TResult tresult) {
            this.latch.countDown();
        }

        public void onFailure(Exception e) {
            this.latch.countDown();
        }

        public void onCanceled() {
            this.latch.countDown();
        }

        public void await() throws InterruptedException {
            this.latch.await();
        }

        public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return this.latch.await(timeout, unit);
        }
    }
}
