package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueDisposable;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableFlatMap<T, U> extends AbstractObservableWithUpstream<T, U> {
    final int bufferSize;
    final boolean delayErrors;
    final Function<? super T, ? extends ObservableSource<? extends U>> mapper;
    final int maxConcurrency;

    public ObservableFlatMap(ObservableSource<T> source, Function<? super T, ? extends ObservableSource<? extends U>> mapper2, boolean delayErrors2, int maxConcurrency2, int bufferSize2) {
        super(source);
        this.mapper = mapper2;
        this.delayErrors = delayErrors2;
        this.maxConcurrency = maxConcurrency2;
        this.bufferSize = bufferSize2;
    }

    public void subscribeActual(Observer<? super U> t) {
        if (!ObservableScalarXMap.tryScalarXMapSubscribe(this.source, t, this.mapper)) {
            this.source.subscribe(new MergeObserver(t, this.mapper, this.delayErrors, this.maxConcurrency, this.bufferSize));
        }
    }

    static final class MergeObserver<T, U> extends AtomicInteger implements Disposable, Observer<T> {
        static final InnerObserver<?, ?>[] CANCELLED = new InnerObserver[0];
        static final InnerObserver<?, ?>[] EMPTY = new InnerObserver[0];
        private static final long serialVersionUID = -2117620485640801370L;
        final Observer<? super U> actual;
        final int bufferSize;
        volatile boolean cancelled;
        final boolean delayErrors;
        volatile boolean done;
        final AtomicThrowable errors = new AtomicThrowable();
        long lastId;
        int lastIndex;
        final Function<? super T, ? extends ObservableSource<? extends U>> mapper;
        final int maxConcurrency;
        final AtomicReference<InnerObserver<?, ?>[]> observers;
        volatile SimplePlainQueue<U> queue;
        Disposable s;
        Queue<ObservableSource<? extends U>> sources;
        long uniqueId;
        int wip;

        MergeObserver(Observer<? super U> actual2, Function<? super T, ? extends ObservableSource<? extends U>> mapper2, boolean delayErrors2, int maxConcurrency2, int bufferSize2) {
            this.actual = actual2;
            this.mapper = mapper2;
            this.delayErrors = delayErrors2;
            this.maxConcurrency = maxConcurrency2;
            this.bufferSize = bufferSize2;
            if (maxConcurrency2 != Integer.MAX_VALUE) {
                this.sources = new ArrayDeque(maxConcurrency2);
            }
            this.observers = new AtomicReference<>(EMPTY);
        }

        public void onSubscribe(Disposable s2) {
            if (DisposableHelper.validate(this.s, s2)) {
                this.s = s2;
                this.actual.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                try {
                    ObservableSource<? extends U> p = (ObservableSource) ObjectHelper.requireNonNull(this.mapper.apply(t), "The mapper returned a null ObservableSource");
                    if (this.maxConcurrency != Integer.MAX_VALUE) {
                        synchronized (this) {
                            if (this.wip == this.maxConcurrency) {
                                this.sources.offer(p);
                                return;
                            }
                            this.wip++;
                        }
                    }
                    subscribeInner(p);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    this.s.dispose();
                    onError(e);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void subscribeInner(ObservableSource<? extends U> p) {
            while (p instanceof Callable) {
                tryEmitScalar((Callable) p);
                if (this.maxConcurrency != Integer.MAX_VALUE) {
                    synchronized (this) {
                        p = this.sources.poll();
                        if (p == null) {
                            this.wip--;
                            return;
                        }
                    }
                } else {
                    return;
                }
            }
            long j = this.uniqueId;
            this.uniqueId = 1 + j;
            InnerObserver<T, U> inner = new InnerObserver<>(this, j);
            if (addInner(inner)) {
                p.subscribe(inner);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean addInner(InnerObserver<T, U> inner) {
            InnerObserver<?, ?>[] a;
            InnerObserver<?, ?>[] b;
            do {
                a = (InnerObserver[]) this.observers.get();
                if (a == CANCELLED) {
                    inner.dispose();
                    return false;
                }
                int n = a.length;
                b = new InnerObserver[(n + 1)];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = inner;
            } while (!this.observers.compareAndSet(a, b));
            return true;
        }

        /* access modifiers changed from: package-private */
        public void removeInner(InnerObserver<T, U> inner) {
            InnerObserver<?, ?>[] a;
            InnerObserver<?, ?>[] b;
            do {
                a = (InnerObserver[]) this.observers.get();
                int n = a.length;
                if (n != 0) {
                    int j = -1;
                    int i = 0;
                    while (true) {
                        if (i >= n) {
                            break;
                        } else if (a[i] == inner) {
                            j = i;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (j >= 0) {
                        if (n == 1) {
                            b = EMPTY;
                        } else {
                            InnerObserver<?, ?>[] b2 = new InnerObserver[(n - 1)];
                            System.arraycopy(a, 0, b2, 0, j);
                            System.arraycopy(a, j + 1, b2, j, (n - j) - 1);
                            b = b2;
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } while (!this.observers.compareAndSet(a, b));
        }

        /* access modifiers changed from: package-private */
        public void tryEmitScalar(Callable<? extends U> value) {
            try {
                U u = value.call();
                if (u != null) {
                    if (get() != 0 || !compareAndSet(0, 1)) {
                        SimplePlainQueue<U> q = this.queue;
                        if (q == null) {
                            if (this.maxConcurrency == Integer.MAX_VALUE) {
                                q = new SpscLinkedArrayQueue<>(this.bufferSize);
                            } else {
                                q = new SpscArrayQueue<>(this.maxConcurrency);
                            }
                            this.queue = q;
                        }
                        if (!q.offer(u)) {
                            onError(new IllegalStateException("Scalar queue full?!"));
                            return;
                        } else if (getAndIncrement() != 0) {
                            return;
                        }
                    } else {
                        this.actual.onNext(u);
                        if (decrementAndGet() == 0) {
                            return;
                        }
                    }
                    drainLoop();
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.errors.addThrowable(ex);
                drain();
            }
        }

        /* access modifiers changed from: package-private */
        public void tryEmit(U value, InnerObserver<T, U> inner) {
            if (get() != 0 || !compareAndSet(0, 1)) {
                SimpleQueue<U> q = inner.queue;
                if (q == null) {
                    q = new SpscLinkedArrayQueue<>(this.bufferSize);
                    inner.queue = q;
                }
                q.offer(value);
                if (getAndIncrement() != 0) {
                    return;
                }
            } else {
                this.actual.onNext(value);
                if (decrementAndGet() == 0) {
                    return;
                }
            }
            drainLoop();
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
            } else if (this.errors.addThrowable(t)) {
                this.done = true;
                drain();
            } else {
                RxJavaPlugins.onError(t);
            }
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                drain();
            }
        }

        public void dispose() {
            Throwable ex;
            if (!this.cancelled) {
                this.cancelled = true;
                if (disposeAll() && (ex = this.errors.terminate()) != null && ex != ExceptionHelper.TERMINATED) {
                    RxJavaPlugins.onError(ex);
                }
            }
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        /* access modifiers changed from: package-private */
        public void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x00ab, code lost:
            if (r0 != null) goto L_0x0097;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void drainLoop() {
            /*
                r17 = this;
                r1 = r17
                io.reactivex.Observer<? super U> r2 = r1.actual
                r0 = 1
                r3 = r0
            L_0x0006:
                boolean r0 = r17.checkTerminate()
                if (r0 == 0) goto L_0x000d
                return
            L_0x000d:
                io.reactivex.internal.fuseable.SimplePlainQueue<U> r0 = r1.queue
                if (r0 == 0) goto L_0x0027
            L_0x0011:
                boolean r4 = r17.checkTerminate()
                if (r4 == 0) goto L_0x0018
                return
            L_0x0018:
                java.lang.Object r4 = r0.poll()
                if (r4 != 0) goto L_0x0023
                if (r4 != 0) goto L_0x0022
                goto L_0x0027
            L_0x0022:
                goto L_0x0011
            L_0x0023:
                r2.onNext(r4)
                goto L_0x0011
            L_0x0027:
                boolean r4 = r1.done
                io.reactivex.internal.fuseable.SimplePlainQueue<U> r5 = r1.queue
                java.util.concurrent.atomic.AtomicReference<io.reactivex.internal.operators.observable.ObservableFlatMap$InnerObserver<?, ?>[]> r0 = r1.observers
                java.lang.Object r0 = r0.get()
                r6 = r0
                io.reactivex.internal.operators.observable.ObservableFlatMap$InnerObserver[] r6 = (io.reactivex.internal.operators.observable.ObservableFlatMap.InnerObserver[]) r6
                int r7 = r6.length
                if (r4 == 0) goto L_0x0055
                if (r5 == 0) goto L_0x003f
                boolean r0 = r5.isEmpty()
                if (r0 == 0) goto L_0x0055
            L_0x003f:
                if (r7 != 0) goto L_0x0055
                io.reactivex.internal.util.AtomicThrowable r0 = r1.errors
                java.lang.Throwable r0 = r0.terminate()
                java.lang.Throwable r8 = io.reactivex.internal.util.ExceptionHelper.TERMINATED
                if (r0 == r8) goto L_0x0054
                if (r0 != 0) goto L_0x0051
                r2.onComplete()
                goto L_0x0054
            L_0x0051:
                r2.onError(r0)
            L_0x0054:
                return
            L_0x0055:
                r0 = 0
                if (r7 == 0) goto L_0x010d
                long r8 = r1.lastId
                int r10 = r1.lastIndex
                if (r7 <= r10) goto L_0x0066
                r11 = r6[r10]
                long r11 = r11.id
                int r13 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
                if (r13 == 0) goto L_0x0087
            L_0x0066:
                if (r7 > r10) goto L_0x0069
                r10 = 0
            L_0x0069:
                r11 = r10
                r12 = 0
            L_0x006b:
                if (r12 >= r7) goto L_0x007e
                r13 = r6[r11]
                long r13 = r13.id
                int r15 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
                if (r15 != 0) goto L_0x0076
                goto L_0x007e
            L_0x0076:
                int r11 = r11 + 1
                if (r11 != r7) goto L_0x007b
                r11 = 0
            L_0x007b:
                int r12 = r12 + 1
                goto L_0x006b
            L_0x007e:
                r10 = r11
                r1.lastIndex = r11
                r12 = r6[r11]
                long r12 = r12.id
                r1.lastId = r12
            L_0x0087:
                r11 = r10
                r12 = 0
                r13 = r12
                r12 = r11
                r11 = r0
            L_0x008c:
                if (r13 >= r7) goto L_0x0102
                boolean r0 = r17.checkTerminate()
                if (r0 == 0) goto L_0x0095
                return
            L_0x0095:
                r14 = r6[r12]
            L_0x0097:
                boolean r0 = r17.checkTerminate()
                if (r0 == 0) goto L_0x009e
                return
            L_0x009e:
                io.reactivex.internal.fuseable.SimpleQueue<U> r15 = r14.queue
                if (r15 != 0) goto L_0x00a3
                goto L_0x00ae
            L_0x00a3:
                java.lang.Object r0 = r15.poll()     // Catch:{ all -> 0x00dd }
                if (r0 != 0) goto L_0x00d3
                if (r0 != 0) goto L_0x00d2
            L_0x00ae:
                boolean r0 = r14.done
                io.reactivex.internal.fuseable.SimpleQueue<U> r15 = r14.queue
                if (r0 == 0) goto L_0x00c7
                if (r15 == 0) goto L_0x00bc
                boolean r16 = r15.isEmpty()
                if (r16 == 0) goto L_0x00c7
            L_0x00bc:
                r1.removeInner(r14)
                boolean r16 = r17.checkTerminate()
                if (r16 == 0) goto L_0x00c6
                return
            L_0x00c6:
                r11 = 1
            L_0x00c7:
                int r12 = r12 + 1
                if (r12 != r7) goto L_0x00cf
                r12 = 0
                r16 = r2
                goto L_0x00fd
            L_0x00cf:
                r16 = r2
                goto L_0x00fd
            L_0x00d2:
                goto L_0x0097
            L_0x00d3:
                r2.onNext(r0)
                boolean r16 = r17.checkTerminate()
                if (r16 == 0) goto L_0x00a3
                return
            L_0x00dd:
                r0 = move-exception
                r16 = r0
                r0 = r16
                io.reactivex.exceptions.Exceptions.throwIfFatal(r0)
                r14.dispose()
                r16 = r2
                io.reactivex.internal.util.AtomicThrowable r2 = r1.errors
                r2.addThrowable(r0)
                boolean r2 = r17.checkTerminate()
                if (r2 == 0) goto L_0x00f6
                return
            L_0x00f6:
                r1.removeInner(r14)
                r2 = 1
                int r13 = r13 + 1
                r11 = r2
            L_0x00fd:
                int r13 = r13 + 1
                r2 = r16
                goto L_0x008c
            L_0x0102:
                r16 = r2
                r1.lastIndex = r12
                r0 = r6[r12]
                long r13 = r0.id
                r1.lastId = r13
                goto L_0x0110
            L_0x010d:
                r16 = r2
                r11 = r0
            L_0x0110:
                if (r11 == 0) goto L_0x0138
                int r0 = r1.maxConcurrency
                r2 = 2147483647(0x7fffffff, float:NaN)
                if (r0 == r2) goto L_0x0134
                monitor-enter(r17)
                java.util.Queue<io.reactivex.ObservableSource<? extends U>> r0 = r1.sources     // Catch:{ all -> 0x0131 }
                java.lang.Object r0 = r0.poll()     // Catch:{ all -> 0x0131 }
                io.reactivex.ObservableSource r0 = (io.reactivex.ObservableSource) r0     // Catch:{ all -> 0x0131 }
                if (r0 != 0) goto L_0x012c
                int r2 = r1.wip     // Catch:{ all -> 0x0131 }
                int r2 = r2 + -1
                r1.wip = r2     // Catch:{ all -> 0x0131 }
                monitor-exit(r17)     // Catch:{ all -> 0x0131 }
                goto L_0x0134
            L_0x012c:
                monitor-exit(r17)     // Catch:{ all -> 0x0131 }
                r1.subscribeInner(r0)
                goto L_0x0134
            L_0x0131:
                r0 = move-exception
                monitor-exit(r17)     // Catch:{ all -> 0x0131 }
                throw r0
            L_0x0134:
                r2 = r16
                goto L_0x0006
            L_0x0138:
                int r0 = -r3
                int r3 = r1.addAndGet(r0)
                if (r3 != 0) goto L_0x0141
                return
            L_0x0141:
                r2 = r16
                goto L_0x0006
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableFlatMap.MergeObserver.drainLoop():void");
        }

        /* access modifiers changed from: package-private */
        public boolean checkTerminate() {
            if (this.cancelled) {
                return true;
            }
            Throwable e = (Throwable) this.errors.get();
            if (this.delayErrors || e == null) {
                return false;
            }
            disposeAll();
            Throwable e2 = this.errors.terminate();
            if (e2 != ExceptionHelper.TERMINATED) {
                this.actual.onError(e2);
            }
            return true;
        }

        /* access modifiers changed from: package-private */
        public boolean disposeAll() {
            InnerObserver<?, ?>[] a;
            this.s.dispose();
            InnerObserver<?, ?>[] a2 = (InnerObserver[]) this.observers.get();
            InnerObserver<?, ?>[] innerObserverArr = CANCELLED;
            if (a2 == innerObserverArr || (a = (InnerObserver[]) this.observers.getAndSet(innerObserverArr)) == CANCELLED) {
                return false;
            }
            for (InnerObserver<?, ?> inner : a) {
                inner.dispose();
            }
            return true;
        }
    }

    static final class InnerObserver<T, U> extends AtomicReference<Disposable> implements Observer<U> {
        private static final long serialVersionUID = -4606175640614850599L;
        volatile boolean done;
        int fusionMode;
        final long id;
        final MergeObserver<T, U> parent;
        volatile SimpleQueue<U> queue;

        InnerObserver(MergeObserver<T, U> parent2, long id2) {
            this.id = id2;
            this.parent = parent2;
        }

        public void onSubscribe(Disposable s) {
            if (DisposableHelper.setOnce(this, s) && (s instanceof QueueDisposable)) {
                QueueDisposable<U> qd = (QueueDisposable) s;
                int m = qd.requestFusion(7);
                if (m == 1) {
                    this.fusionMode = m;
                    this.queue = qd;
                    this.done = true;
                    this.parent.drain();
                } else if (m == 2) {
                    this.fusionMode = m;
                    this.queue = qd;
                }
            }
        }

        public void onNext(U t) {
            if (this.fusionMode == 0) {
                this.parent.tryEmit(t, this);
            } else {
                this.parent.drain();
            }
        }

        public void onError(Throwable t) {
            if (this.parent.errors.addThrowable(t)) {
                if (!this.parent.delayErrors) {
                    this.parent.disposeAll();
                }
                this.done = true;
                this.parent.drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            this.done = true;
            this.parent.drain();
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }
    }
}
