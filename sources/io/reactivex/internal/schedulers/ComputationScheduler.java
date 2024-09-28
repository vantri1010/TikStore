package io.reactivex.internal.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.disposables.ListCompositeDisposable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class ComputationScheduler extends Scheduler {
    private static final String KEY_COMPUTATION_PRIORITY = "rx2.computation-priority";
    static final String KEY_MAX_THREADS = "rx2.computation-threads";
    static final int MAX_THREADS = cap(Runtime.getRuntime().availableProcessors(), Integer.getInteger(KEY_MAX_THREADS, 0).intValue());
    static final FixedSchedulerPool NONE;
    static final PoolWorker SHUTDOWN_WORKER;
    static final RxThreadFactory THREAD_FACTORY;
    private static final String THREAD_NAME_PREFIX = "RxComputationThreadPool";
    final AtomicReference<FixedSchedulerPool> pool;
    final ThreadFactory threadFactory;

    static {
        PoolWorker poolWorker = new PoolWorker(new RxThreadFactory("RxComputationShutdown"));
        SHUTDOWN_WORKER = poolWorker;
        poolWorker.dispose();
        RxThreadFactory rxThreadFactory = new RxThreadFactory(THREAD_NAME_PREFIX, Math.max(1, Math.min(10, Integer.getInteger(KEY_COMPUTATION_PRIORITY, 5).intValue())), true);
        THREAD_FACTORY = rxThreadFactory;
        FixedSchedulerPool fixedSchedulerPool = new FixedSchedulerPool(0, rxThreadFactory);
        NONE = fixedSchedulerPool;
        fixedSchedulerPool.shutdown();
    }

    static int cap(int cpuCount, int paramThreads) {
        return (paramThreads <= 0 || paramThreads > cpuCount) ? cpuCount : paramThreads;
    }

    static final class FixedSchedulerPool {
        final int cores;
        final PoolWorker[] eventLoops;
        long n;

        FixedSchedulerPool(int maxThreads, ThreadFactory threadFactory) {
            this.cores = maxThreads;
            this.eventLoops = new PoolWorker[maxThreads];
            for (int i = 0; i < maxThreads; i++) {
                this.eventLoops[i] = new PoolWorker(threadFactory);
            }
        }

        public PoolWorker getEventLoop() {
            int c = this.cores;
            if (c == 0) {
                return ComputationScheduler.SHUTDOWN_WORKER;
            }
            PoolWorker[] poolWorkerArr = this.eventLoops;
            long j = this.n;
            this.n = 1 + j;
            return poolWorkerArr[(int) (j % ((long) c))];
        }

        public void shutdown() {
            for (PoolWorker w : this.eventLoops) {
                w.dispose();
            }
        }
    }

    public ComputationScheduler() {
        this(THREAD_FACTORY);
    }

    public ComputationScheduler(ThreadFactory threadFactory2) {
        this.threadFactory = threadFactory2;
        this.pool = new AtomicReference<>(NONE);
        start();
    }

    public Scheduler.Worker createWorker() {
        return new EventLoopWorker(this.pool.get().getEventLoop());
    }

    public Disposable scheduleDirect(Runnable run, long delay, TimeUnit unit) {
        return this.pool.get().getEventLoop().scheduleDirect(run, delay, unit);
    }

    public Disposable schedulePeriodicallyDirect(Runnable run, long initialDelay, long period, TimeUnit unit) {
        return this.pool.get().getEventLoop().schedulePeriodicallyDirect(run, initialDelay, period, unit);
    }

    public void start() {
        FixedSchedulerPool update = new FixedSchedulerPool(MAX_THREADS, this.threadFactory);
        if (!this.pool.compareAndSet(NONE, update)) {
            update.shutdown();
        }
    }

    public void shutdown() {
        FixedSchedulerPool curr;
        FixedSchedulerPool fixedSchedulerPool;
        do {
            curr = this.pool.get();
            fixedSchedulerPool = NONE;
            if (curr == fixedSchedulerPool) {
                return;
            }
        } while (!this.pool.compareAndSet(curr, fixedSchedulerPool));
        curr.shutdown();
    }

    static final class EventLoopWorker extends Scheduler.Worker {
        private final ListCompositeDisposable both;
        volatile boolean disposed;
        private final PoolWorker poolWorker;
        private final ListCompositeDisposable serial = new ListCompositeDisposable();
        private final CompositeDisposable timed = new CompositeDisposable();

        EventLoopWorker(PoolWorker poolWorker2) {
            this.poolWorker = poolWorker2;
            ListCompositeDisposable listCompositeDisposable = new ListCompositeDisposable();
            this.both = listCompositeDisposable;
            listCompositeDisposable.add(this.serial);
            this.both.add(this.timed);
        }

        public void dispose() {
            if (!this.disposed) {
                this.disposed = true;
                this.both.dispose();
            }
        }

        public boolean isDisposed() {
            return this.disposed;
        }

        public Disposable schedule(Runnable action) {
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            return this.poolWorker.scheduleActual(action, 0, TimeUnit.MILLISECONDS, this.serial);
        }

        public Disposable schedule(Runnable action, long delayTime, TimeUnit unit) {
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            return this.poolWorker.scheduleActual(action, delayTime, unit, this.timed);
        }
    }

    static final class PoolWorker extends NewThreadWorker {
        PoolWorker(ThreadFactory threadFactory) {
            super(threadFactory);
        }
    }
}
