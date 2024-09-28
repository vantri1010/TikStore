package com.blankj.utilcode.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.lang.Thread;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class ThreadUtils {
    /* access modifiers changed from: private */
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /* access modifiers changed from: private */
    public static final Map<Task, TaskInfo> TASK_TASKINFO_MAP = new ConcurrentHashMap();
    private static final Timer TIMER = new Timer();
    private static final byte TYPE_CACHED = -2;
    private static final byte TYPE_CPU = -8;
    private static final byte TYPE_IO = -4;
    private static final Map<Integer, Map<Integer, ExecutorService>> TYPE_PRIORITY_POOLS = new HashMap();
    private static final byte TYPE_SINGLE = -1;
    private static Executor sDeliver;

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static ExecutorService getFixedPool(int size) {
        return getPoolByTypeAndPriority(size);
    }

    public static ExecutorService getFixedPool(int size, int priority) {
        return getPoolByTypeAndPriority(size, priority);
    }

    public static ExecutorService getSinglePool() {
        return getPoolByTypeAndPriority(-1);
    }

    public static ExecutorService getSinglePool(int priority) {
        return getPoolByTypeAndPriority(-1, priority);
    }

    public static ExecutorService getCachedPool() {
        return getPoolByTypeAndPriority(-2);
    }

    public static ExecutorService getCachedPool(int priority) {
        return getPoolByTypeAndPriority(-2, priority);
    }

    public static ExecutorService getIoPool() {
        return getPoolByTypeAndPriority(-4);
    }

    public static ExecutorService getIoPool(int priority) {
        return getPoolByTypeAndPriority(-4, priority);
    }

    public static ExecutorService getCpuPool() {
        return getPoolByTypeAndPriority(-8);
    }

    public static ExecutorService getCpuPool(int priority) {
        return getPoolByTypeAndPriority(-8, priority);
    }

    public static <T> void executeByFixed(int size, Task<T> task) {
        execute(getPoolByTypeAndPriority(size), task);
    }

    public static <T> void executeByFixed(int size, Task<T> task, int priority) {
        execute(getPoolByTypeAndPriority(size, priority), task);
    }

    public static <T> void executeByFixedWithDelay(int size, Task<T> task, long delay, TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(size), task, delay, unit);
    }

    public static <T> void executeByFixedWithDelay(int size, Task<T> task, long delay, TimeUnit unit, int priority) {
        executeWithDelay(getPoolByTypeAndPriority(size, priority), task, delay, unit);
    }

    public static <T> void executeByFixedAtFixRate(int size, Task<T> task, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(size), task, 0, period, unit);
    }

    public static <T> void executeByFixedAtFixRate(int size, Task<T> task, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(size, priority), task, 0, period, unit);
    }

    public static <T> void executeByFixedAtFixRate(int size, Task<T> task, long initialDelay, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(size), task, initialDelay, period, unit);
    }

    public static <T> void executeByFixedAtFixRate(int size, Task<T> task, long initialDelay, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(size, priority), task, initialDelay, period, unit);
    }

    public static <T> void executeBySingle(Task<T> task) {
        execute(getPoolByTypeAndPriority(-1), task);
    }

    public static <T> void executeBySingle(Task<T> task, int priority) {
        execute(getPoolByTypeAndPriority(-1, priority), task);
    }

    public static <T> void executeBySingleWithDelay(Task<T> task, long delay, TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(-1), task, delay, unit);
    }

    public static <T> void executeBySingleWithDelay(Task<T> task, long delay, TimeUnit unit, int priority) {
        executeWithDelay(getPoolByTypeAndPriority(-1, priority), task, delay, unit);
    }

    public static <T> void executeBySingleAtFixRate(Task<T> task, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(-1), task, 0, period, unit);
    }

    public static <T> void executeBySingleAtFixRate(Task<T> task, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(-1, priority), task, 0, period, unit);
    }

    public static <T> void executeBySingleAtFixRate(Task<T> task, long initialDelay, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(-1), task, initialDelay, period, unit);
    }

    public static <T> void executeBySingleAtFixRate(Task<T> task, long initialDelay, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(-1, priority), task, initialDelay, period, unit);
    }

    public static <T> void executeByCached(Task<T> task) {
        execute(getPoolByTypeAndPriority(-2), task);
    }

    public static <T> void executeByCached(Task<T> task, int priority) {
        execute(getPoolByTypeAndPriority(-2, priority), task);
    }

    public static <T> void executeByCachedWithDelay(Task<T> task, long delay, TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(-2), task, delay, unit);
    }

    public static <T> void executeByCachedWithDelay(Task<T> task, long delay, TimeUnit unit, int priority) {
        executeWithDelay(getPoolByTypeAndPriority(-2, priority), task, delay, unit);
    }

    public static <T> void executeByCachedAtFixRate(Task<T> task, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(-2), task, 0, period, unit);
    }

    public static <T> void executeByCachedAtFixRate(Task<T> task, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(-2, priority), task, 0, period, unit);
    }

    public static <T> void executeByCachedAtFixRate(Task<T> task, long initialDelay, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(-2), task, initialDelay, period, unit);
    }

    public static <T> void executeByCachedAtFixRate(Task<T> task, long initialDelay, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(-2, priority), task, initialDelay, period, unit);
    }

    public static <T> void executeByIo(Task<T> task) {
        execute(getPoolByTypeAndPriority(-4), task);
    }

    public static <T> void executeByIo(Task<T> task, int priority) {
        execute(getPoolByTypeAndPriority(-4, priority), task);
    }

    public static <T> void executeByIoWithDelay(Task<T> task, long delay, TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(-4), task, delay, unit);
    }

    public static <T> void executeByIoWithDelay(Task<T> task, long delay, TimeUnit unit, int priority) {
        executeWithDelay(getPoolByTypeAndPriority(-4, priority), task, delay, unit);
    }

    public static <T> void executeByIoAtFixRate(Task<T> task, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(-4), task, 0, period, unit);
    }

    public static <T> void executeByIoAtFixRate(Task<T> task, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(-4, priority), task, 0, period, unit);
    }

    public static <T> void executeByIoAtFixRate(Task<T> task, long initialDelay, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(-4), task, initialDelay, period, unit);
    }

    public static <T> void executeByIoAtFixRate(Task<T> task, long initialDelay, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(-4, priority), task, initialDelay, period, unit);
    }

    public static <T> void executeByCpu(Task<T> task) {
        execute(getPoolByTypeAndPriority(-8), task);
    }

    public static <T> void executeByCpu(Task<T> task, int priority) {
        execute(getPoolByTypeAndPriority(-8, priority), task);
    }

    public static <T> void executeByCpuWithDelay(Task<T> task, long delay, TimeUnit unit) {
        executeWithDelay(getPoolByTypeAndPriority(-8), task, delay, unit);
    }

    public static <T> void executeByCpuWithDelay(Task<T> task, long delay, TimeUnit unit, int priority) {
        executeWithDelay(getPoolByTypeAndPriority(-8, priority), task, delay, unit);
    }

    public static <T> void executeByCpuAtFixRate(Task<T> task, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(-8), task, 0, period, unit);
    }

    public static <T> void executeByCpuAtFixRate(Task<T> task, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(-8, priority), task, 0, period, unit);
    }

    public static <T> void executeByCpuAtFixRate(Task<T> task, long initialDelay, long period, TimeUnit unit) {
        executeAtFixedRate(getPoolByTypeAndPriority(-8), task, initialDelay, period, unit);
    }

    public static <T> void executeByCpuAtFixRate(Task<T> task, long initialDelay, long period, TimeUnit unit, int priority) {
        executeAtFixedRate(getPoolByTypeAndPriority(-8, priority), task, initialDelay, period, unit);
    }

    public static <T> void executeByCustom(ExecutorService pool, Task<T> task) {
        execute(pool, task);
    }

    public static <T> void executeByCustomWithDelay(ExecutorService pool, Task<T> task, long delay, TimeUnit unit) {
        executeWithDelay(pool, task, delay, unit);
    }

    public static <T> void executeByCustomAtFixRate(ExecutorService pool, Task<T> task, long period, TimeUnit unit) {
        executeAtFixedRate(pool, task, 0, period, unit);
    }

    public static <T> void executeByCustomAtFixRate(ExecutorService pool, Task<T> task, long initialDelay, long period, TimeUnit unit) {
        executeAtFixedRate(pool, task, initialDelay, period, unit);
    }

    public static void cancel(Task task) {
        if (task != null) {
            task.cancel();
        }
    }

    public static void cancel(Task... tasks) {
        if (tasks != null && tasks.length != 0) {
            for (Task task : tasks) {
                if (task != null) {
                    task.cancel();
                }
            }
        }
    }

    public static void cancel(List<Task> tasks) {
        if (tasks != null && tasks.size() != 0) {
            for (Task task : tasks) {
                if (task != null) {
                    task.cancel();
                }
            }
        }
    }

    public static void cancel(ExecutorService executorService) {
        if (executorService instanceof ThreadPoolExecutor4Util) {
            for (Map.Entry<Task, TaskInfo> taskTaskInfoEntry : TASK_TASKINFO_MAP.entrySet()) {
                if (taskTaskInfoEntry.getValue().mService == executorService) {
                    cancel(taskTaskInfoEntry.getKey());
                }
            }
            return;
        }
        Log.e("LogUtils", "The executorService is not ThreadUtils's pool.");
    }

    public static void setDeliver(Executor deliver) {
        sDeliver = deliver;
    }

    private static <T> void execute(ExecutorService pool, Task<T> task) {
        execute(pool, task, 0, 0, (TimeUnit) null);
    }

    private static <T> void executeWithDelay(ExecutorService pool, Task<T> task, long delay, TimeUnit unit) {
        execute(pool, task, delay, 0, unit);
    }

    private static <T> void executeAtFixedRate(ExecutorService pool, Task<T> task, long delay, long period, TimeUnit unit) {
        execute(pool, task, delay, period, unit);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0024, code lost:
        if (r12 != 0) goto L_0x0040;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0028, code lost:
        if (r10 != 0) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002a, code lost:
        r8.execute(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
        r0 = new com.blankj.utilcode.util.ThreadUtils.AnonymousClass1();
        com.blankj.utilcode.util.ThreadUtils.TaskInfo.access$202(r1, r0);
        TIMER.schedule(r0, r14.toMillis(r10));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0040, code lost:
        com.blankj.utilcode.util.ThreadUtils.Task.access$300(r9, true);
        r0 = new com.blankj.utilcode.util.ThreadUtils.AnonymousClass2();
        com.blankj.utilcode.util.ThreadUtils.TaskInfo.access$202(r1, r0);
        TIMER.scheduleAtFixedRate(r0, r14.toMillis(r10), r14.toMillis(r12));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static <T> void execute(final java.util.concurrent.ExecutorService r8, final com.blankj.utilcode.util.ThreadUtils.Task<T> r9, long r10, long r12, java.util.concurrent.TimeUnit r14) {
        /*
            java.util.Map<com.blankj.utilcode.util.ThreadUtils$Task, com.blankj.utilcode.util.ThreadUtils$TaskInfo> r0 = TASK_TASKINFO_MAP
            monitor-enter(r0)
            java.util.Map<com.blankj.utilcode.util.ThreadUtils$Task, com.blankj.utilcode.util.ThreadUtils$TaskInfo> r1 = TASK_TASKINFO_MAP     // Catch:{ all -> 0x005b }
            java.lang.Object r1 = r1.get(r9)     // Catch:{ all -> 0x005b }
            if (r1 == 0) goto L_0x0014
            java.lang.String r1 = "ThreadUtils"
            java.lang.String r2 = "Task can only be executed once."
            android.util.Log.e(r1, r2)     // Catch:{ all -> 0x005b }
            monitor-exit(r0)     // Catch:{ all -> 0x005b }
            return
        L_0x0014:
            com.blankj.utilcode.util.ThreadUtils$TaskInfo r1 = new com.blankj.utilcode.util.ThreadUtils$TaskInfo     // Catch:{ all -> 0x005b }
            r2 = 0
            r1.<init>(r8)     // Catch:{ all -> 0x005b }
            java.util.Map<com.blankj.utilcode.util.ThreadUtils$Task, com.blankj.utilcode.util.ThreadUtils$TaskInfo> r2 = TASK_TASKINFO_MAP     // Catch:{ all -> 0x005b }
            r2.put(r9, r1)     // Catch:{ all -> 0x005b }
            monitor-exit(r0)     // Catch:{ all -> 0x005b }
            r2 = 0
            int r0 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x0040
            int r0 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x002e
            r8.execute(r9)
            goto L_0x005a
        L_0x002e:
            com.blankj.utilcode.util.ThreadUtils$1 r0 = new com.blankj.utilcode.util.ThreadUtils$1
            r0.<init>(r8, r9)
            java.util.TimerTask unused = r1.mTimerTask = r0
            java.util.Timer r2 = TIMER
            long r3 = r14.toMillis(r10)
            r2.schedule(r0, r3)
            goto L_0x005a
        L_0x0040:
            r0 = 1
            r9.setSchedule(r0)
            com.blankj.utilcode.util.ThreadUtils$2 r0 = new com.blankj.utilcode.util.ThreadUtils$2
            r0.<init>(r8, r9)
            java.util.TimerTask unused = r1.mTimerTask = r0
            java.util.Timer r2 = TIMER
            long r4 = r14.toMillis(r10)
            long r6 = r14.toMillis(r12)
            r3 = r0
            r2.scheduleAtFixedRate(r3, r4, r6)
        L_0x005a:
            return
        L_0x005b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.ThreadUtils.execute(java.util.concurrent.ExecutorService, com.blankj.utilcode.util.ThreadUtils$Task, long, long, java.util.concurrent.TimeUnit):void");
    }

    private static ExecutorService getPoolByTypeAndPriority(int type) {
        return getPoolByTypeAndPriority(type, 5);
    }

    private static ExecutorService getPoolByTypeAndPriority(int type, int priority) {
        ExecutorService pool;
        synchronized (TYPE_PRIORITY_POOLS) {
            Map<Integer, ExecutorService> priorityPools = TYPE_PRIORITY_POOLS.get(Integer.valueOf(type));
            if (priorityPools == null) {
                Map<Integer, ExecutorService> priorityPools2 = new ConcurrentHashMap<>();
                pool = ThreadPoolExecutor4Util.createPool(type, priority);
                priorityPools2.put(Integer.valueOf(priority), pool);
                TYPE_PRIORITY_POOLS.put(Integer.valueOf(type), priorityPools2);
            } else {
                pool = priorityPools.get(Integer.valueOf(priority));
                if (pool == null) {
                    pool = ThreadPoolExecutor4Util.createPool(type, priority);
                    priorityPools.put(Integer.valueOf(priority), pool);
                }
            }
        }
        return pool;
    }

    static final class ThreadPoolExecutor4Util extends ThreadPoolExecutor {
        private final AtomicInteger mSubmittedCount = new AtomicInteger();
        private LinkedBlockingQueue4Util mWorkQueue;

        /* access modifiers changed from: private */
        public static ExecutorService createPool(int type, int priority) {
            int i = type;
            int i2 = priority;
            if (i == -8) {
                return new ThreadPoolExecutor4Util(ThreadUtils.CPU_COUNT + 1, (ThreadUtils.CPU_COUNT * 2) + 1, 30, TimeUnit.SECONDS, new LinkedBlockingQueue4Util(true), new UtilsThreadFactory("cpu", i2));
            } else if (i == -4) {
                return new ThreadPoolExecutor4Util((ThreadUtils.CPU_COUNT * 2) + 1, (ThreadUtils.CPU_COUNT * 2) + 1, 30, TimeUnit.SECONDS, new LinkedBlockingQueue4Util(), new UtilsThreadFactory("io", i2));
            } else {
                if (i == -2) {
                    return new ThreadPoolExecutor4Util(0, 128, 60, TimeUnit.SECONDS, new LinkedBlockingQueue4Util(true), new UtilsThreadFactory("cached", i2));
                } else if (i == -1) {
                    return new ThreadPoolExecutor4Util(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue4Util(), new UtilsThreadFactory("single", i2));
                } else {
                    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
                    LinkedBlockingQueue4Util linkedBlockingQueue4Util = new LinkedBlockingQueue4Util();
                    return new ThreadPoolExecutor4Util(type, type, 0, timeUnit, linkedBlockingQueue4Util, new UtilsThreadFactory("fixed(" + i + SQLBuilder.PARENTHESES_RIGHT, i2));
                }
            }
        }

        ThreadPoolExecutor4Util(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, LinkedBlockingQueue4Util workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
            ThreadPoolExecutor4Util unused = workQueue.mPool = this;
            this.mWorkQueue = workQueue;
        }

        private int getSubmittedCount() {
            return this.mSubmittedCount.get();
        }

        /* access modifiers changed from: protected */
        public void afterExecute(Runnable r, Throwable t) {
            this.mSubmittedCount.decrementAndGet();
            super.afterExecute(r, t);
        }

        public void execute(Runnable command) {
            if (command == null) {
                throw new NullPointerException("Argument 'command' of type Runnable (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
            } else if (!isShutdown()) {
                this.mSubmittedCount.incrementAndGet();
                try {
                    super.execute(command);
                } catch (RejectedExecutionException e) {
                    Log.e("ThreadUtils", "This will not happen!");
                    this.mWorkQueue.offer(command);
                } catch (Throwable th) {
                    this.mSubmittedCount.decrementAndGet();
                }
            }
        }
    }

    private static final class LinkedBlockingQueue4Util extends LinkedBlockingQueue<Runnable> {
        private int mCapacity = Integer.MAX_VALUE;
        /* access modifiers changed from: private */
        public volatile ThreadPoolExecutor4Util mPool;

        LinkedBlockingQueue4Util() {
        }

        LinkedBlockingQueue4Util(boolean isAddSubThreadFirstThenAddQueue) {
            if (isAddSubThreadFirstThenAddQueue) {
                this.mCapacity = 0;
            }
        }

        LinkedBlockingQueue4Util(int capacity) {
            this.mCapacity = capacity;
        }

        public boolean offer(Runnable runnable) {
            if (runnable == null) {
                throw new NullPointerException("Argument 'runnable' of type Runnable (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
            } else if (this.mCapacity > size() || this.mPool == null || this.mPool.getPoolSize() >= this.mPool.getMaximumPoolSize()) {
                return super.offer(runnable);
            } else {
                return false;
            }
        }
    }

    private static final class UtilsThreadFactory extends AtomicLong implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private static final long serialVersionUID = -9209200509960368598L;
        private final boolean isDaemon;
        private final String namePrefix;
        private final int priority;

        UtilsThreadFactory(String prefix, int priority2) {
            this(prefix, priority2, false);
        }

        UtilsThreadFactory(String prefix, int priority2, boolean isDaemon2) {
            this.namePrefix = prefix + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
            this.priority = priority2;
            this.isDaemon = isDaemon2;
        }

        public Thread newThread(Runnable r) {
            if (r != null) {
                Thread t = new Thread(r, this.namePrefix + getAndIncrement()) {
                    public void run() {
                        try {
                            super.run();
                        } catch (Throwable t) {
                            Log.e("ThreadUtils", "Request threw uncaught throwable", t);
                        }
                    }
                };
                t.setDaemon(this.isDaemon);
                t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        System.out.println(e);
                    }
                });
                t.setPriority(this.priority);
                return t;
            }
            throw new NullPointerException("Argument 'r' of type Runnable (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static abstract class SimpleTask<T> extends Task<T> {
        public void onCancel() {
            Log.e("ThreadUtils", "onCancel: " + Thread.currentThread());
        }

        public void onFail(Throwable t) {
            Log.e("ThreadUtils", "onFail: ", t);
        }
    }

    public static abstract class Task<T> implements Runnable {
        private static final int CANCELLED = 4;
        private static final int COMPLETING = 3;
        private static final int EXCEPTIONAL = 2;
        private static final int INTERRUPTED = 5;
        private static final int NEW = 0;
        private static final int RUNNING = 1;
        private static final int TIMEOUT = 6;
        private Executor deliver;
        private volatile boolean isSchedule;
        private Timer mTimer;
        private volatile Thread runner;
        private final AtomicInteger state = new AtomicInteger(0);

        public interface OnTimeoutListener {
            void onTimeout();
        }

        public abstract T doInBackground() throws Throwable;

        public abstract void onCancel();

        public abstract void onFail(Throwable th);

        public abstract void onSuccess(T t);

        public void run() {
            if (this.isSchedule) {
                if (this.runner == null) {
                    if (this.state.compareAndSet(0, 1)) {
                        this.runner = Thread.currentThread();
                    } else {
                        return;
                    }
                } else if (this.state.get() != 1) {
                    return;
                }
            } else if (this.state.compareAndSet(0, 1)) {
                this.runner = Thread.currentThread();
            } else {
                return;
            }
            try {
                final T result = doInBackground();
                if (this.isSchedule) {
                    if (this.state.get() == 1) {
                        getDeliver().execute(new Runnable() {
                            public void run() {
                                Task.this.onSuccess(result);
                            }
                        });
                    }
                } else if (this.state.compareAndSet(1, 3)) {
                    getDeliver().execute(new Runnable() {
                        public void run() {
                            Task.this.onSuccess(result);
                            Task.this.onDone();
                        }
                    });
                }
            } catch (InterruptedException e) {
                this.state.compareAndSet(4, 5);
            } catch (Throwable throwable) {
                if (this.state.compareAndSet(1, 2)) {
                    getDeliver().execute(new Runnable() {
                        public void run() {
                            Task.this.onFail(throwable);
                            Task.this.onDone();
                        }
                    });
                }
            }
        }

        public void cancel() {
            cancel(true);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0019, code lost:
            if (r3.runner == null) goto L_0x0020;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x001b, code lost:
            r3.runner.interrupt();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
            getDeliver().execute(new com.blankj.utilcode.util.ThreadUtils.Task.AnonymousClass4(r3));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x002c, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0015, code lost:
            if (r4 == false) goto L_0x0020;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void cancel(boolean r4) {
            /*
                r3 = this;
                java.util.concurrent.atomic.AtomicInteger r0 = r3.state
                monitor-enter(r0)
                java.util.concurrent.atomic.AtomicInteger r1 = r3.state     // Catch:{ all -> 0x002d }
                int r1 = r1.get()     // Catch:{ all -> 0x002d }
                r2 = 1
                if (r1 <= r2) goto L_0x000e
                monitor-exit(r0)     // Catch:{ all -> 0x002d }
                return
            L_0x000e:
                java.util.concurrent.atomic.AtomicInteger r1 = r3.state     // Catch:{ all -> 0x002d }
                r2 = 4
                r1.set(r2)     // Catch:{ all -> 0x002d }
                monitor-exit(r0)     // Catch:{ all -> 0x002d }
                if (r4 == 0) goto L_0x0020
                java.lang.Thread r0 = r3.runner
                if (r0 == 0) goto L_0x0020
                java.lang.Thread r0 = r3.runner
                r0.interrupt()
            L_0x0020:
                java.util.concurrent.Executor r0 = r3.getDeliver()
                com.blankj.utilcode.util.ThreadUtils$Task$4 r1 = new com.blankj.utilcode.util.ThreadUtils$Task$4
                r1.<init>()
                r0.execute(r1)
                return
            L_0x002d:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x002d }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.ThreadUtils.Task.cancel(boolean):void");
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0017, code lost:
            if (r3.runner == null) goto L_0x001e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0019, code lost:
            r3.runner.interrupt();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x001e, code lost:
            onDone();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0021, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void timeout() {
            /*
                r3 = this;
                java.util.concurrent.atomic.AtomicInteger r0 = r3.state
                monitor-enter(r0)
                java.util.concurrent.atomic.AtomicInteger r1 = r3.state     // Catch:{ all -> 0x0022 }
                int r1 = r1.get()     // Catch:{ all -> 0x0022 }
                r2 = 1
                if (r1 <= r2) goto L_0x000e
                monitor-exit(r0)     // Catch:{ all -> 0x0022 }
                return
            L_0x000e:
                java.util.concurrent.atomic.AtomicInteger r1 = r3.state     // Catch:{ all -> 0x0022 }
                r2 = 6
                r1.set(r2)     // Catch:{ all -> 0x0022 }
                monitor-exit(r0)     // Catch:{ all -> 0x0022 }
                java.lang.Thread r0 = r3.runner
                if (r0 == 0) goto L_0x001e
                java.lang.Thread r0 = r3.runner
                r0.interrupt()
            L_0x001e:
                r3.onDone()
                return
            L_0x0022:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0022 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.ThreadUtils.Task.timeout():void");
        }

        public boolean isCanceled() {
            return this.state.get() >= 4;
        }

        public boolean isDone() {
            return this.state.get() > 1;
        }

        public Task<T> setDeliver(Executor deliver2) {
            this.deliver = deliver2;
            return this;
        }

        public void setTimeout(long timeoutMillis, final OnTimeoutListener listener) {
            Timer timer = new Timer();
            this.mTimer = timer;
            timer.schedule(new TimerTask() {
                public void run() {
                    if (!Task.this.isDone() && listener != null) {
                        Task.this.timeout();
                        listener.onTimeout();
                    }
                }
            }, timeoutMillis);
        }

        /* access modifiers changed from: private */
        public void setSchedule(boolean isSchedule2) {
            this.isSchedule = isSchedule2;
        }

        private Executor getDeliver() {
            Executor executor = this.deliver;
            if (executor == null) {
                return ThreadUtils.getGlobalDeliver();
            }
            return executor;
        }

        /* access modifiers changed from: protected */
        public void onDone() {
            ThreadUtils.TASK_TASKINFO_MAP.remove(this);
            Timer timer = this.mTimer;
            if (timer != null) {
                timer.cancel();
                this.mTimer = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public static Executor getGlobalDeliver() {
        if (sDeliver == null) {
            sDeliver = new Executor() {
                private final Handler mHandler = new Handler(Looper.getMainLooper());

                public void execute(Runnable command) {
                    if (command != null) {
                        this.mHandler.post(command);
                        return;
                    }
                    throw new NullPointerException("Argument 'command' of type Runnable (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
                }
            };
        }
        return sDeliver;
    }

    private static class TaskInfo {
        /* access modifiers changed from: private */
        public ExecutorService mService;
        /* access modifiers changed from: private */
        public TimerTask mTimerTask;

        private TaskInfo(ExecutorService service) {
            this.mService = service;
        }
    }
}
