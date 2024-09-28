package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableFlatMap<T, U> extends AbstractFlowableWithUpstream<T, U> {
    final int bufferSize;
    final boolean delayErrors;
    final Function<? super T, ? extends Publisher<? extends U>> mapper;
    final int maxConcurrency;

    public FlowableFlatMap(Flowable<T> source, Function<? super T, ? extends Publisher<? extends U>> mapper2, boolean delayErrors2, int maxConcurrency2, int bufferSize2) {
        super(source);
        this.mapper = mapper2;
        this.delayErrors = delayErrors2;
        this.maxConcurrency = maxConcurrency2;
        this.bufferSize = bufferSize2;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(Subscriber<? super U> s) {
        if (!FlowableScalarXMap.tryScalarXMapSubscribe(this.source, s, this.mapper)) {
            this.source.subscribe(subscribe(s, this.mapper, this.delayErrors, this.maxConcurrency, this.bufferSize));
        }
    }

    public static <T, U> FlowableSubscriber<T> subscribe(Subscriber<? super U> s, Function<? super T, ? extends Publisher<? extends U>> mapper2, boolean delayErrors2, int maxConcurrency2, int bufferSize2) {
        return new MergeSubscriber(s, mapper2, delayErrors2, maxConcurrency2, bufferSize2);
    }

    static final class MergeSubscriber<T, U> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {
        static final InnerSubscriber<?, ?>[] CANCELLED = new InnerSubscriber[0];
        static final InnerSubscriber<?, ?>[] EMPTY = new InnerSubscriber[0];
        private static final long serialVersionUID = -2117620485640801370L;
        final Subscriber<? super U> actual;
        final int bufferSize;
        volatile boolean cancelled;
        final boolean delayErrors;
        volatile boolean done;
        final AtomicThrowable errs = new AtomicThrowable();
        long lastId;
        int lastIndex;
        final Function<? super T, ? extends Publisher<? extends U>> mapper;
        final int maxConcurrency;
        volatile SimplePlainQueue<U> queue;
        final AtomicLong requested = new AtomicLong();
        Subscription s;
        int scalarEmitted;
        final int scalarLimit;
        final AtomicReference<InnerSubscriber<?, ?>[]> subscribers = new AtomicReference<>();
        long uniqueId;

        MergeSubscriber(Subscriber<? super U> actual2, Function<? super T, ? extends Publisher<? extends U>> mapper2, boolean delayErrors2, int maxConcurrency2, int bufferSize2) {
            this.actual = actual2;
            this.mapper = mapper2;
            this.delayErrors = delayErrors2;
            this.maxConcurrency = maxConcurrency2;
            this.bufferSize = bufferSize2;
            this.scalarLimit = Math.max(1, maxConcurrency2 >> 1);
            this.subscribers.lazySet(EMPTY);
        }

        public void onSubscribe(Subscription s2) {
            if (SubscriptionHelper.validate(this.s, s2)) {
                this.s = s2;
                this.actual.onSubscribe(this);
                if (!this.cancelled) {
                    int i = this.maxConcurrency;
                    if (i == Integer.MAX_VALUE) {
                        s2.request(Long.MAX_VALUE);
                    } else {
                        s2.request((long) i);
                    }
                }
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                try {
                    Publisher<? extends U> p = (Publisher) ObjectHelper.requireNonNull(this.mapper.apply(t), "The mapper returned a null Publisher");
                    if (p instanceof Callable) {
                        try {
                            U u = ((Callable) p).call();
                            if (u != null) {
                                tryEmitScalar(u);
                            } else if (this.maxConcurrency != Integer.MAX_VALUE && !this.cancelled) {
                                int i = this.scalarEmitted + 1;
                                this.scalarEmitted = i;
                                int i2 = this.scalarLimit;
                                if (i == i2) {
                                    this.scalarEmitted = 0;
                                    this.s.request((long) i2);
                                }
                            }
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            this.errs.addThrowable(ex);
                            drain();
                        }
                    } else {
                        long j = this.uniqueId;
                        this.uniqueId = 1 + j;
                        InnerSubscriber<T, U> inner = new InnerSubscriber<>(this, j);
                        if (addInner(inner)) {
                            p.subscribe(inner);
                        }
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    this.s.cancel();
                    onError(e);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean addInner(InnerSubscriber<T, U> inner) {
            InnerSubscriber<?, ?>[] a;
            InnerSubscriber<?, ?>[] b;
            do {
                a = (InnerSubscriber[]) this.subscribers.get();
                if (a == CANCELLED) {
                    inner.dispose();
                    return false;
                }
                int n = a.length;
                b = new InnerSubscriber[(n + 1)];
                System.arraycopy(a, 0, b, 0, n);
                b[n] = inner;
            } while (!this.subscribers.compareAndSet(a, b));
            return true;
        }

        /* access modifiers changed from: package-private */
        public void removeInner(InnerSubscriber<T, U> inner) {
            InnerSubscriber<?, ?>[] a;
            InnerSubscriber<?, ?>[] b;
            do {
                a = (InnerSubscriber[]) this.subscribers.get();
                if (a != CANCELLED && a != EMPTY) {
                    int n = a.length;
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
                            InnerSubscriber<?, ?>[] b2 = new InnerSubscriber[(n - 1)];
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
            } while (!this.subscribers.compareAndSet(a, b));
        }

        /* access modifiers changed from: package-private */
        public SimpleQueue<U> getMainQueue() {
            SimplePlainQueue<U> q = this.queue;
            if (q == null) {
                if (this.maxConcurrency == Integer.MAX_VALUE) {
                    q = new SpscLinkedArrayQueue<>(this.bufferSize);
                } else {
                    q = new SpscArrayQueue<>(this.maxConcurrency);
                }
                this.queue = q;
            }
            return q;
        }

        /* access modifiers changed from: package-private */
        public void tryEmitScalar(U value) {
            if (get() == 0 && compareAndSet(0, 1)) {
                long r = this.requested.get();
                SimpleQueue<U> q = this.queue;
                if (r == 0 || (q != null && !q.isEmpty())) {
                    if (q == null) {
                        q = getMainQueue();
                    }
                    if (!q.offer(value)) {
                        onError(new IllegalStateException("Scalar queue full?!"));
                        return;
                    }
                } else {
                    this.actual.onNext(value);
                    if (r != Long.MAX_VALUE) {
                        this.requested.decrementAndGet();
                    }
                    if (this.maxConcurrency != Integer.MAX_VALUE && !this.cancelled) {
                        int i = this.scalarEmitted + 1;
                        this.scalarEmitted = i;
                        int i2 = this.scalarLimit;
                        if (i == i2) {
                            this.scalarEmitted = 0;
                            this.s.request((long) i2);
                        }
                    }
                }
                if (decrementAndGet() == 0) {
                    return;
                }
            } else if (!getMainQueue().offer(value)) {
                onError(new IllegalStateException("Scalar queue full?!"));
                return;
            } else if (getAndIncrement() != 0) {
                return;
            }
            drainLoop();
        }

        /* access modifiers changed from: package-private */
        public SimpleQueue<U> getInnerQueue(InnerSubscriber<T, U> inner) {
            SimpleQueue<U> q = inner.queue;
            if (q != null) {
                return q;
            }
            SimpleQueue<U> q2 = new SpscArrayQueue<>(this.bufferSize);
            inner.queue = q2;
            return q2;
        }

        /* access modifiers changed from: package-private */
        public void tryEmit(U value, InnerSubscriber<T, U> inner) {
            if (get() != 0 || !compareAndSet(0, 1)) {
                SimpleQueue<U> q = inner.queue;
                if (q == null) {
                    q = new SpscArrayQueue<>(this.bufferSize);
                    inner.queue = q;
                }
                if (!q.offer(value)) {
                    onError(new MissingBackpressureException("Inner queue full?!"));
                    return;
                } else if (getAndIncrement() != 0) {
                    return;
                }
            } else {
                long r = this.requested.get();
                SimpleQueue<U> q2 = inner.queue;
                if (r == 0 || (q2 != null && !q2.isEmpty())) {
                    if (q2 == null) {
                        q2 = getInnerQueue(inner);
                    }
                    if (!q2.offer(value)) {
                        onError(new MissingBackpressureException("Inner queue full?!"));
                        return;
                    }
                } else {
                    this.actual.onNext(value);
                    if (r != Long.MAX_VALUE) {
                        this.requested.decrementAndGet();
                    }
                    inner.requestMore(1);
                }
                if (decrementAndGet() == 0) {
                    return;
                }
            }
            drainLoop();
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
            } else if (this.errs.addThrowable(t)) {
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

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                drain();
            }
        }

        public void cancel() {
            SimpleQueue<U> q;
            if (!this.cancelled) {
                this.cancelled = true;
                this.s.cancel();
                disposeAll();
                if (getAndIncrement() == 0 && (q = this.queue) != null) {
                    q.clear();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Removed duplicated region for block: B:155:0x01ed A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x00f7  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void drainLoop() {
            /*
                r33 = this;
                r1 = r33
                org.reactivestreams.Subscriber<? super U> r2 = r1.actual
                r0 = 1
                r3 = r0
            L_0x0006:
                boolean r0 = r33.checkTerminate()
                if (r0 == 0) goto L_0x000d
                return
            L_0x000d:
                io.reactivex.internal.fuseable.SimplePlainQueue<U> r0 = r1.queue
                java.util.concurrent.atomic.AtomicLong r4 = r1.requested
                long r4 = r4.get()
                r6 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
                int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r9 != 0) goto L_0x0020
                r6 = 1
                goto L_0x0021
            L_0x0020:
                r6 = 0
            L_0x0021:
                r9 = 0
                r11 = 1
                r13 = 0
                if (r0 == 0) goto L_0x0077
            L_0x0029:
                r15 = 0
                r7 = 0
                r17 = r15
            L_0x002e:
                int r15 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
                if (r15 == 0) goto L_0x0052
                java.lang.Object r7 = r0.poll()
                boolean r15 = r33.checkTerminate()
                if (r15 == 0) goto L_0x003d
                return
            L_0x003d:
                if (r7 != 0) goto L_0x0044
                r19 = r9
                r8 = r17
                goto L_0x0056
            L_0x0044:
                r2.onNext(r7)
                long r9 = r9 + r11
                r19 = r9
                r8 = r17
                long r17 = r8 + r11
                long r4 = r4 - r11
                r9 = r19
                goto L_0x002e
            L_0x0052:
                r19 = r9
                r8 = r17
            L_0x0056:
                int r10 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
                if (r10 == 0) goto L_0x0069
                if (r6 == 0) goto L_0x0062
                r4 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
                goto L_0x0069
            L_0x0062:
                java.util.concurrent.atomic.AtomicLong r10 = r1.requested
                long r11 = -r8
                long r4 = r10.addAndGet(r11)
            L_0x0069:
                int r10 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
                if (r10 == 0) goto L_0x0075
                if (r7 != 0) goto L_0x0070
                goto L_0x0075
            L_0x0070:
                r9 = r19
                r11 = 1
                goto L_0x0029
            L_0x0075:
                r9 = r19
            L_0x0077:
                boolean r7 = r1.done
                io.reactivex.internal.fuseable.SimplePlainQueue<U> r8 = r1.queue
                java.util.concurrent.atomic.AtomicReference<io.reactivex.internal.operators.flowable.FlowableFlatMap$InnerSubscriber<?, ?>[]> r0 = r1.subscribers
                java.lang.Object r0 = r0.get()
                r11 = r0
                io.reactivex.internal.operators.flowable.FlowableFlatMap$InnerSubscriber[] r11 = (io.reactivex.internal.operators.flowable.FlowableFlatMap.InnerSubscriber[]) r11
                int r12 = r11.length
                if (r7 == 0) goto L_0x00a5
                if (r8 == 0) goto L_0x008f
                boolean r0 = r8.isEmpty()
                if (r0 == 0) goto L_0x00a5
            L_0x008f:
                if (r12 != 0) goto L_0x00a5
                io.reactivex.internal.util.AtomicThrowable r0 = r1.errs
                java.lang.Throwable r0 = r0.terminate()
                java.lang.Throwable r13 = io.reactivex.internal.util.ExceptionHelper.TERMINATED
                if (r0 == r13) goto L_0x00a4
                if (r0 != 0) goto L_0x00a1
                r2.onComplete()
                goto L_0x00a4
            L_0x00a1:
                r2.onError(r0)
            L_0x00a4:
                return
            L_0x00a5:
                r0 = 0
                if (r12 == 0) goto L_0x0202
                long r13 = r1.lastId
                int r15 = r1.lastIndex
                if (r12 <= r15) goto L_0x00c0
                r21 = r0
                r0 = r11[r15]
                r22 = r4
                long r4 = r0.id
                int r0 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
                if (r0 == 0) goto L_0x00bb
                goto L_0x00c4
            L_0x00bb:
                r24 = r7
                r25 = r8
                goto L_0x00f1
            L_0x00c0:
                r21 = r0
                r22 = r4
            L_0x00c4:
                if (r12 > r15) goto L_0x00c7
                r15 = 0
            L_0x00c7:
                r0 = r15
                r4 = 0
            L_0x00c9:
                if (r4 >= r12) goto L_0x00e4
                r5 = r11[r0]
                r24 = r7
                r25 = r8
                long r7 = r5.id
                int r5 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
                if (r5 != 0) goto L_0x00d8
                goto L_0x00e8
            L_0x00d8:
                int r0 = r0 + 1
                if (r0 != r12) goto L_0x00dd
                r0 = 0
            L_0x00dd:
                int r4 = r4 + 1
                r7 = r24
                r8 = r25
                goto L_0x00c9
            L_0x00e4:
                r24 = r7
                r25 = r8
            L_0x00e8:
                r15 = r0
                r1.lastIndex = r0
                r4 = r11[r0]
                long r4 = r4.id
                r1.lastId = r4
            L_0x00f1:
                r0 = r15
                r4 = 0
                r5 = r4
                r4 = r0
            L_0x00f5:
                if (r5 >= r12) goto L_0x01ed
                boolean r0 = r33.checkTerminate()
                if (r0 == 0) goto L_0x00fe
                return
            L_0x00fe:
                r7 = r11[r4]
                r0 = 0
            L_0x0101:
                boolean r8 = r33.checkTerminate()
                if (r8 == 0) goto L_0x0108
                return
            L_0x0108:
                io.reactivex.internal.fuseable.SimpleQueue<U> r8 = r7.queue
                if (r8 != 0) goto L_0x0116
                r30 = r2
                r32 = r5
                r31 = r6
                r28 = r13
                goto L_0x01ac
            L_0x0116:
                r26 = 0
                r28 = r13
                r13 = r26
                r26 = r22
                r22 = r0
            L_0x0120:
                r18 = 0
                int r0 = (r26 > r18 ? 1 : (r26 == r18 ? 0 : -1))
                if (r0 == 0) goto L_0x016c
                java.lang.Object r0 = r8.poll()     // Catch:{ all -> 0x0143 }
                if (r0 != 0) goto L_0x0130
                r30 = r2
                goto L_0x0170
            L_0x0130:
                r2.onNext(r0)
                boolean r22 = r33.checkTerminate()
                if (r22 == 0) goto L_0x013a
                return
            L_0x013a:
                r16 = 1
                long r26 = r26 - r16
                long r13 = r13 + r16
                r22 = r0
                goto L_0x0120
            L_0x0143:
                r0 = move-exception
                r23 = r0
                r0 = r23
                io.reactivex.exceptions.Exceptions.throwIfFatal(r0)
                r7.dispose()
                r30 = r2
                io.reactivex.internal.util.AtomicThrowable r2 = r1.errs
                r2.addThrowable(r0)
                boolean r2 = r33.checkTerminate()
                if (r2 == 0) goto L_0x015c
                return
            L_0x015c:
                r1.removeInner(r7)
                r2 = 1
                int r5 = r5 + 1
                r21 = r2
                r31 = r6
                r22 = r26
                r13 = 1
                goto L_0x01e3
            L_0x016c:
                r30 = r2
                r0 = r22
            L_0x0170:
                r18 = 0
                int r2 = (r13 > r18 ? 1 : (r13 == r18 ? 0 : -1))
                if (r2 == 0) goto L_0x0193
                if (r6 != 0) goto L_0x0184
                java.util.concurrent.atomic.AtomicLong r2 = r1.requested
                r32 = r5
                r31 = r6
                long r5 = -r13
                long r5 = r2.addAndGet(r5)
                goto L_0x018d
            L_0x0184:
                r32 = r5
                r31 = r6
                r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            L_0x018d:
                r7.requestMore(r13)
                r22 = r5
                goto L_0x0199
            L_0x0193:
                r32 = r5
                r31 = r6
                r22 = r26
            L_0x0199:
                r5 = 0
                int r2 = (r22 > r5 ? 1 : (r22 == r5 ? 0 : -1))
                if (r2 == 0) goto L_0x01ac
                if (r0 != 0) goto L_0x01a2
                goto L_0x01ac
            L_0x01a2:
                r13 = r28
                r2 = r30
                r6 = r31
                r5 = r32
                goto L_0x0101
            L_0x01ac:
                boolean r2 = r7.done
                io.reactivex.internal.fuseable.SimpleQueue<U> r5 = r7.queue
                if (r2 == 0) goto L_0x01ce
                if (r5 == 0) goto L_0x01be
                boolean r6 = r5.isEmpty()
                if (r6 == 0) goto L_0x01bb
                goto L_0x01be
            L_0x01bb:
                r13 = 1
                goto L_0x01d0
            L_0x01be:
                r1.removeInner(r7)
                boolean r6 = r33.checkTerminate()
                if (r6 == 0) goto L_0x01c8
                return
            L_0x01c8:
                r13 = 1
                long r9 = r9 + r13
                r21 = 1
                goto L_0x01d0
            L_0x01ce:
                r13 = 1
            L_0x01d0:
                r16 = 0
                int r6 = (r22 > r16 ? 1 : (r22 == r16 ? 0 : -1))
                if (r6 != 0) goto L_0x01d9
                r0 = r21
                goto L_0x01f7
            L_0x01d9:
                int r4 = r4 + 1
                if (r4 != r12) goto L_0x01e1
                r4 = 0
                r5 = r32
                goto L_0x01e3
            L_0x01e1:
                r5 = r32
            L_0x01e3:
                r2 = 1
                int r5 = r5 + r2
                r13 = r28
                r2 = r30
                r6 = r31
                goto L_0x00f5
            L_0x01ed:
                r30 = r2
                r32 = r5
                r31 = r6
                r28 = r13
                r0 = r21
            L_0x01f7:
                r1.lastIndex = r4
                r2 = r11[r4]
                long r5 = r2.id
                r1.lastId = r5
                r4 = r22
                goto L_0x020e
            L_0x0202:
                r21 = r0
                r30 = r2
                r22 = r4
                r31 = r6
                r24 = r7
                r25 = r8
            L_0x020e:
                r6 = 0
                int r2 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
                if (r2 == 0) goto L_0x021d
                boolean r2 = r1.cancelled
                if (r2 != 0) goto L_0x021d
                org.reactivestreams.Subscription r2 = r1.s
                r2.request(r9)
            L_0x021d:
                if (r0 == 0) goto L_0x0223
                r2 = r30
                goto L_0x0006
            L_0x0223:
                int r2 = -r3
                int r3 = r1.addAndGet(r2)
                if (r3 != 0) goto L_0x022c
                return
            L_0x022c:
                r2 = r30
                goto L_0x0006
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableFlatMap.MergeSubscriber.drainLoop():void");
        }

        /* access modifiers changed from: package-private */
        public boolean checkTerminate() {
            if (this.cancelled) {
                clearScalarQueue();
                return true;
            } else if (this.delayErrors || this.errs.get() == null) {
                return false;
            } else {
                clearScalarQueue();
                Throwable ex = this.errs.terminate();
                if (ex != ExceptionHelper.TERMINATED) {
                    this.actual.onError(ex);
                }
                return true;
            }
        }

        /* access modifiers changed from: package-private */
        public void clearScalarQueue() {
            SimpleQueue<U> q = this.queue;
            if (q != null) {
                q.clear();
            }
        }

        /* access modifiers changed from: package-private */
        public void disposeAll() {
            InnerSubscriber<?, ?>[] a;
            InnerSubscriber<?, ?>[] a2 = (InnerSubscriber[]) this.subscribers.get();
            InnerSubscriber<?, ?>[] innerSubscriberArr = CANCELLED;
            if (a2 != innerSubscriberArr && (a = (InnerSubscriber[]) this.subscribers.getAndSet(innerSubscriberArr)) != CANCELLED) {
                for (InnerSubscriber<?, ?> inner : a) {
                    inner.dispose();
                }
                Throwable ex = this.errs.terminate();
                if (ex != null && ex != ExceptionHelper.TERMINATED) {
                    RxJavaPlugins.onError(ex);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void innerError(InnerSubscriber<T, U> inner, Throwable t) {
            if (this.errs.addThrowable(t)) {
                inner.done = true;
                if (!this.delayErrors) {
                    this.s.cancel();
                    for (InnerSubscriber<?, ?> a : (InnerSubscriber[]) this.subscribers.getAndSet(CANCELLED)) {
                        a.dispose();
                    }
                }
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }
    }

    static final class InnerSubscriber<T, U> extends AtomicReference<Subscription> implements FlowableSubscriber<U>, Disposable {
        private static final long serialVersionUID = -4606175640614850599L;
        final int bufferSize;
        volatile boolean done;
        int fusionMode;
        final long id;
        final int limit;
        final MergeSubscriber<T, U> parent;
        long produced;
        volatile SimpleQueue<U> queue;

        InnerSubscriber(MergeSubscriber<T, U> parent2, long id2) {
            this.id = id2;
            this.parent = parent2;
            int i = parent2.bufferSize;
            this.bufferSize = i;
            this.limit = i >> 2;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.setOnce(this, s)) {
                if (s instanceof QueueSubscription) {
                    QueueSubscription<U> qs = (QueueSubscription) s;
                    int m = qs.requestFusion(7);
                    if (m == 1) {
                        this.fusionMode = m;
                        this.queue = qs;
                        this.done = true;
                        this.parent.drain();
                        return;
                    } else if (m == 2) {
                        this.fusionMode = m;
                        this.queue = qs;
                    }
                }
                s.request((long) this.bufferSize);
            }
        }

        public void onNext(U t) {
            if (this.fusionMode != 2) {
                this.parent.tryEmit(t, this);
            } else {
                this.parent.drain();
            }
        }

        public void onError(Throwable t) {
            lazySet(SubscriptionHelper.CANCELLED);
            this.parent.innerError(this, t);
        }

        public void onComplete() {
            this.done = true;
            this.parent.drain();
        }

        /* access modifiers changed from: package-private */
        public void requestMore(long n) {
            if (this.fusionMode != 1) {
                long p = this.produced + n;
                if (p >= ((long) this.limit)) {
                    this.produced = 0;
                    ((Subscription) get()).request(p);
                    return;
                }
                this.produced = p;
            }
        }

        public void dispose() {
            SubscriptionHelper.cancel(this);
        }

        public boolean isDisposed() {
            return get() == SubscriptionHelper.CANCELLED;
        }
    }
}
