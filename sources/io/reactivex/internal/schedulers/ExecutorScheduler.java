package io.reactivex.internal.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ExecutorScheduler extends Scheduler {
    static final Scheduler HELPER = Schedulers.single();
    final Executor executor;

    public ExecutorScheduler(Executor executor2) {
        this.executor = executor2;
    }

    public Scheduler.Worker createWorker() {
        return new ExecutorWorker(this.executor);
    }

    public Disposable scheduleDirect(Runnable run) {
        Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
        try {
            if (this.executor instanceof ExecutorService) {
                ScheduledDirectTask task = new ScheduledDirectTask(decoratedRun);
                task.setFuture(((ExecutorService) this.executor).submit(task));
                return task;
            }
            ExecutorWorker.BooleanRunnable br = new ExecutorWorker.BooleanRunnable(decoratedRun);
            this.executor.execute(br);
            return br;
        } catch (RejectedExecutionException ex) {
            RxJavaPlugins.onError(ex);
            return EmptyDisposable.INSTANCE;
        }
    }

    public Disposable scheduleDirect(Runnable run, long delay, TimeUnit unit) {
        Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
        if (this.executor instanceof ScheduledExecutorService) {
            try {
                ScheduledDirectTask task = new ScheduledDirectTask(decoratedRun);
                task.setFuture(((ScheduledExecutorService) this.executor).schedule(task, delay, unit));
                return task;
            } catch (RejectedExecutionException ex) {
                RxJavaPlugins.onError(ex);
                return EmptyDisposable.INSTANCE;
            }
        } else {
            DelayedRunnable dr = new DelayedRunnable(decoratedRun);
            dr.timed.replace(HELPER.scheduleDirect(new DelayedDispose(dr), delay, unit));
            return dr;
        }
    }

    public Disposable schedulePeriodicallyDirect(Runnable run, long initialDelay, long period, TimeUnit unit) {
        if (!(this.executor instanceof ScheduledExecutorService)) {
            return super.schedulePeriodicallyDirect(run, initialDelay, period, unit);
        }
        try {
            ScheduledDirectPeriodicTask task = new ScheduledDirectPeriodicTask(RxJavaPlugins.onSchedule(run));
            task.setFuture(((ScheduledExecutorService) this.executor).scheduleAtFixedRate(task, initialDelay, period, unit));
            return task;
        } catch (RejectedExecutionException ex) {
            RxJavaPlugins.onError(ex);
            return EmptyDisposable.INSTANCE;
        }
    }

    public static final class ExecutorWorker extends Scheduler.Worker implements Runnable {
        volatile boolean disposed;
        final Executor executor;
        final MpscLinkedQueue<Runnable> queue;
        final CompositeDisposable tasks = new CompositeDisposable();
        final AtomicInteger wip = new AtomicInteger();

        public ExecutorWorker(Executor executor2) {
            this.executor = executor2;
            this.queue = new MpscLinkedQueue<>();
        }

        public Disposable schedule(Runnable run) {
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            BooleanRunnable br = new BooleanRunnable(RxJavaPlugins.onSchedule(run));
            this.queue.offer(br);
            if (this.wip.getAndIncrement() == 0) {
                try {
                    this.executor.execute(this);
                } catch (RejectedExecutionException ex) {
                    this.disposed = true;
                    this.queue.clear();
                    RxJavaPlugins.onError(ex);
                    return EmptyDisposable.INSTANCE;
                }
            }
            return br;
        }

        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            if (delay <= 0) {
                return schedule(run);
            }
            if (this.disposed) {
                return EmptyDisposable.INSTANCE;
            }
            SequentialDisposable first = new SequentialDisposable();
            SequentialDisposable mar = new SequentialDisposable(first);
            ScheduledRunnable sr = new ScheduledRunnable(new SequentialDispose(mar, RxJavaPlugins.onSchedule(run)), this.tasks);
            this.tasks.add(sr);
            Executor executor2 = this.executor;
            if (executor2 instanceof ScheduledExecutorService) {
                try {
                    sr.setFuture(((ScheduledExecutorService) executor2).schedule(sr, delay, unit));
                } catch (RejectedExecutionException ex) {
                    this.disposed = true;
                    RxJavaPlugins.onError(ex);
                    return EmptyDisposable.INSTANCE;
                }
            } else {
                sr.setFuture(new DisposeOnCancel(ExecutorScheduler.HELPER.scheduleDirect(sr, delay, unit)));
            }
            first.replace(sr);
            return mar;
        }

        public void dispose() {
            if (!this.disposed) {
                this.disposed = true;
                this.tasks.dispose();
                if (this.wip.getAndIncrement() == 0) {
                    this.queue.clear();
                }
            }
        }

        public boolean isDisposed() {
            return this.disposed;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x001b, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001c, code lost:
            r0 = r4.wip.addAndGet(-r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0023, code lost:
            if (r0 != 0) goto L_0x0003;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x0016, code lost:
            if (r4.disposed == false) goto L_0x001c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0018, code lost:
            r1.clear();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r4 = this;
                r0 = 1
                io.reactivex.internal.queue.MpscLinkedQueue<java.lang.Runnable> r1 = r4.queue
            L_0x0003:
                boolean r2 = r4.disposed
                if (r2 == 0) goto L_0x000b
                r1.clear()
                return
            L_0x000b:
                java.lang.Object r2 = r1.poll()
                java.lang.Runnable r2 = (java.lang.Runnable) r2
                if (r2 != 0) goto L_0x0027
                boolean r2 = r4.disposed
                if (r2 == 0) goto L_0x001c
                r1.clear()
                return
            L_0x001c:
                java.util.concurrent.atomic.AtomicInteger r2 = r4.wip
                int r3 = -r0
                int r0 = r2.addAndGet(r3)
                if (r0 != 0) goto L_0x0003
                return
            L_0x0027:
                r2.run()
                boolean r3 = r4.disposed
                if (r3 == 0) goto L_0x0032
                r1.clear()
                return
            L_0x0032:
                goto L_0x000b
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.schedulers.ExecutorScheduler.ExecutorWorker.run():void");
        }

        static final class BooleanRunnable extends AtomicBoolean implements Runnable, Disposable {
            private static final long serialVersionUID = -2421395018820541164L;
            final Runnable actual;

            BooleanRunnable(Runnable actual2) {
                this.actual = actual2;
            }

            public void run() {
                if (!get()) {
                    try {
                        this.actual.run();
                    } finally {
                        lazySet(true);
                    }
                }
            }

            public void dispose() {
                lazySet(true);
            }

            public boolean isDisposed() {
                return get();
            }
        }

        final class SequentialDispose implements Runnable {
            private final Runnable decoratedRun;
            private final SequentialDisposable mar;

            SequentialDispose(SequentialDisposable mar2, Runnable decoratedRun2) {
                this.mar = mar2;
                this.decoratedRun = decoratedRun2;
            }

            public void run() {
                this.mar.replace(ExecutorWorker.this.schedule(this.decoratedRun));
            }
        }
    }

    static final class DelayedRunnable extends AtomicReference<Runnable> implements Runnable, Disposable {
        private static final long serialVersionUID = -4101336210206799084L;
        final SequentialDisposable direct = new SequentialDisposable();
        final SequentialDisposable timed = new SequentialDisposable();

        DelayedRunnable(Runnable run) {
            super(run);
        }

        public void run() {
            Runnable r = (Runnable) get();
            if (r != null) {
                try {
                    r.run();
                } finally {
                    lazySet((Object) null);
                    this.timed.lazySet(DisposableHelper.DISPOSED);
                    this.direct.lazySet(DisposableHelper.DISPOSED);
                }
            }
        }

        public boolean isDisposed() {
            return get() == null;
        }

        public void dispose() {
            if (getAndSet((Object) null) != null) {
                this.timed.dispose();
                this.direct.dispose();
            }
        }
    }

    final class DelayedDispose implements Runnable {
        private final DelayedRunnable dr;

        DelayedDispose(DelayedRunnable dr2) {
            this.dr = dr2;
        }

        public void run() {
            this.dr.direct.replace(ExecutorScheduler.this.scheduleDirect(this.dr));
        }
    }
}
