package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.fuseable.HasUpstreamPublisher;
import io.reactivex.internal.subscribers.SubscriberResourceWrapper;
import io.reactivex.internal.subscriptions.EmptySubscription;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Timed;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowableReplay<T> extends ConnectableFlowable<T> implements HasUpstreamPublisher<T>, Disposable {
    static final Callable DEFAULT_UNBOUNDED_FACTORY = new DefaultUnboundedFactory();
    final Callable<? extends ReplayBuffer<T>> bufferFactory;
    final AtomicReference<ReplaySubscriber<T>> current;
    final Publisher<T> onSubscribe;
    final Flowable<T> source;

    interface ReplayBuffer<T> {
        void complete();

        void error(Throwable th);

        void next(T t);

        void replay(InnerSubscription<T> innerSubscription);
    }

    public static <U, R> Flowable<R> multicastSelector(Callable<? extends ConnectableFlowable<U>> connectableFactory, Function<? super Flowable<U>, ? extends Publisher<R>> selector) {
        return Flowable.unsafeCreate(new MultiCastPublisher(connectableFactory, selector));
    }

    public static <T> ConnectableFlowable<T> observeOn(ConnectableFlowable<T> co, Scheduler scheduler) {
        return RxJavaPlugins.onAssembly(new ConnectableFlowableReplay(co, co.observeOn(scheduler)));
    }

    public static <T> ConnectableFlowable<T> createFrom(Flowable<? extends T> source2) {
        return create(source2, DEFAULT_UNBOUNDED_FACTORY);
    }

    public static <T> ConnectableFlowable<T> create(Flowable<T> source2, int bufferSize) {
        if (bufferSize == Integer.MAX_VALUE) {
            return createFrom(source2);
        }
        return create(source2, new ReplayBufferTask(bufferSize));
    }

    public static <T> ConnectableFlowable<T> create(Flowable<T> source2, long maxAge, TimeUnit unit, Scheduler scheduler) {
        return create(source2, maxAge, unit, scheduler, Integer.MAX_VALUE);
    }

    public static <T> ConnectableFlowable<T> create(Flowable<T> source2, long maxAge, TimeUnit unit, Scheduler scheduler, int bufferSize) {
        return create(source2, new ScheduledReplayBufferTask(bufferSize, maxAge, unit, scheduler));
    }

    static <T> ConnectableFlowable<T> create(Flowable<T> source2, Callable<? extends ReplayBuffer<T>> bufferFactory2) {
        AtomicReference<ReplaySubscriber<T>> curr = new AtomicReference<>();
        return RxJavaPlugins.onAssembly(new FlowableReplay(new ReplayPublisher<>(curr, bufferFactory2), source2, curr, bufferFactory2));
    }

    private FlowableReplay(Publisher<T> onSubscribe2, Flowable<T> source2, AtomicReference<ReplaySubscriber<T>> current2, Callable<? extends ReplayBuffer<T>> bufferFactory2) {
        this.onSubscribe = onSubscribe2;
        this.source = source2;
        this.current = current2;
        this.bufferFactory = bufferFactory2;
    }

    public Publisher<T> source() {
        return this.source;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(Subscriber<? super T> s) {
        this.onSubscribe.subscribe(s);
    }

    public void dispose() {
        this.current.lazySet((Object) null);
    }

    public boolean isDisposed() {
        Disposable d = this.current.get();
        return d == null || d.isDisposed();
    }

    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(io.reactivex.functions.Consumer<? super io.reactivex.disposables.Disposable> r7) {
        /*
            r6 = this;
        L_0x0000:
            java.util.concurrent.atomic.AtomicReference<io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber<T>> r0 = r6.current
            java.lang.Object r0 = r0.get()
            io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber r0 = (io.reactivex.internal.operators.flowable.FlowableReplay.ReplaySubscriber) r0
            if (r0 == 0) goto L_0x0010
            boolean r1 = r0.isDisposed()
            if (r1 == 0) goto L_0x0028
        L_0x0010:
            java.util.concurrent.Callable<? extends io.reactivex.internal.operators.flowable.FlowableReplay$ReplayBuffer<T>> r1 = r6.bufferFactory     // Catch:{ all -> 0x005a }
            java.lang.Object r1 = r1.call()     // Catch:{ all -> 0x005a }
            io.reactivex.internal.operators.flowable.FlowableReplay$ReplayBuffer r1 = (io.reactivex.internal.operators.flowable.FlowableReplay.ReplayBuffer) r1     // Catch:{ all -> 0x005a }
            io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber r2 = new io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber
            r2.<init>(r1)
            java.util.concurrent.atomic.AtomicReference<io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber<T>> r3 = r6.current
            boolean r3 = r3.compareAndSet(r0, r2)
            if (r3 != 0) goto L_0x0027
            goto L_0x0000
        L_0x0027:
            r0 = r2
        L_0x0028:
            java.util.concurrent.atomic.AtomicBoolean r1 = r0.shouldConnect
            boolean r1 = r1.get()
            r2 = 1
            r3 = 0
            if (r1 != 0) goto L_0x003c
            java.util.concurrent.atomic.AtomicBoolean r1 = r0.shouldConnect
            boolean r1 = r1.compareAndSet(r3, r2)
            if (r1 == 0) goto L_0x003c
            r1 = 1
            goto L_0x003d
        L_0x003c:
            r1 = 0
        L_0x003d:
            r7.accept(r0)     // Catch:{ all -> 0x004a }
            if (r1 == 0) goto L_0x0049
            io.reactivex.Flowable<T> r2 = r6.source
            r2.subscribe(r0)
        L_0x0049:
            return
        L_0x004a:
            r4 = move-exception
            if (r1 == 0) goto L_0x0052
            java.util.concurrent.atomic.AtomicBoolean r5 = r0.shouldConnect
            r5.compareAndSet(r2, r3)
        L_0x0052:
            io.reactivex.exceptions.Exceptions.throwIfFatal(r4)
            java.lang.RuntimeException r2 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r4)
            throw r2
        L_0x005a:
            r1 = move-exception
            io.reactivex.exceptions.Exceptions.throwIfFatal(r1)
            java.lang.RuntimeException r2 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.connect(io.reactivex.functions.Consumer):void");
    }

    static final class ReplaySubscriber<T> extends AtomicReference<Subscription> implements FlowableSubscriber<T>, Disposable {
        static final InnerSubscription[] EMPTY = new InnerSubscription[0];
        static final InnerSubscription[] TERMINATED = new InnerSubscription[0];
        private static final long serialVersionUID = 7224554242710036740L;
        final ReplayBuffer<T> buffer;
        boolean done;
        final AtomicInteger management = new AtomicInteger();
        long maxChildRequested;
        long maxUpstreamRequested;
        final AtomicBoolean shouldConnect = new AtomicBoolean();
        final AtomicReference<InnerSubscription<T>[]> subscribers = new AtomicReference<>(EMPTY);

        ReplaySubscriber(ReplayBuffer<T> buffer2) {
            this.buffer = buffer2;
        }

        public boolean isDisposed() {
            return this.subscribers.get() == TERMINATED;
        }

        public void dispose() {
            this.subscribers.set(TERMINATED);
            SubscriptionHelper.cancel(this);
        }

        /* access modifiers changed from: package-private */
        public boolean add(InnerSubscription<T> producer) {
            InnerSubscription<T>[] c;
            InnerSubscription<T>[] u;
            if (producer != null) {
                do {
                    c = (InnerSubscription[]) this.subscribers.get();
                    if (c == TERMINATED) {
                        return false;
                    }
                    int len = c.length;
                    u = new InnerSubscription[(len + 1)];
                    System.arraycopy(c, 0, u, 0, len);
                    u[len] = producer;
                } while (!this.subscribers.compareAndSet(c, u));
                return true;
            }
            throw null;
        }

        /* access modifiers changed from: package-private */
        public void remove(InnerSubscription<T> p) {
            InnerSubscription<T>[] c;
            InnerSubscription<T>[] u;
            do {
                c = (InnerSubscription[]) this.subscribers.get();
                int len = c.length;
                if (len != 0) {
                    int j = -1;
                    int i = 0;
                    while (true) {
                        if (i >= len) {
                            break;
                        } else if (c[i].equals(p)) {
                            j = i;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (j >= 0) {
                        if (len == 1) {
                            u = EMPTY;
                        } else {
                            InnerSubscription<T>[] u2 = new InnerSubscription[(len - 1)];
                            System.arraycopy(c, 0, u2, 0, j);
                            System.arraycopy(c, j + 1, u2, j, (len - j) - 1);
                            u = u2;
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } while (!this.subscribers.compareAndSet(c, u));
        }

        public void onSubscribe(Subscription p) {
            if (SubscriptionHelper.setOnce(this, p)) {
                manageRequests();
                for (InnerSubscription<T> rp : (InnerSubscription[]) this.subscribers.get()) {
                    this.buffer.replay(rp);
                }
            }
        }

        public void onNext(T t) {
            if (!this.done) {
                this.buffer.next(t);
                for (InnerSubscription<T> rp : (InnerSubscription[]) this.subscribers.get()) {
                    this.buffer.replay(rp);
                }
            }
        }

        public void onError(Throwable e) {
            if (!this.done) {
                this.done = true;
                this.buffer.error(e);
                for (InnerSubscription<T> rp : (InnerSubscription[]) this.subscribers.getAndSet(TERMINATED)) {
                    this.buffer.replay(rp);
                }
                return;
            }
            RxJavaPlugins.onError(e);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.buffer.complete();
                for (InnerSubscription<T> rp : (InnerSubscription[]) this.subscribers.getAndSet(TERMINATED)) {
                    this.buffer.replay(rp);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void manageRequests() {
            if (this.management.getAndIncrement() == 0) {
                int missed = 1;
                while (!isDisposed()) {
                    InnerSubscription<T>[] a = (InnerSubscription[]) this.subscribers.get();
                    long ri = this.maxChildRequested;
                    long maxTotalRequests = ri;
                    for (InnerSubscription<T> rp : a) {
                        maxTotalRequests = Math.max(maxTotalRequests, rp.totalRequested.get());
                    }
                    long ur = this.maxUpstreamRequested;
                    Subscription p = (Subscription) get();
                    long diff = maxTotalRequests - ri;
                    if (diff != 0) {
                        this.maxChildRequested = maxTotalRequests;
                        if (p == null) {
                            long u = ur + diff;
                            if (u < 0) {
                                u = Long.MAX_VALUE;
                            }
                            this.maxUpstreamRequested = u;
                        } else if (ur != 0) {
                            this.maxUpstreamRequested = 0;
                            p.request(ur + diff);
                        } else {
                            p.request(diff);
                        }
                    } else if (!(ur == 0 || p == null)) {
                        this.maxUpstreamRequested = 0;
                        p.request(ur);
                    }
                    missed = this.management.addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }
    }

    static final class InnerSubscription<T> extends AtomicLong implements Subscription, Disposable {
        static final long CANCELLED = Long.MIN_VALUE;
        private static final long serialVersionUID = -4453897557930727610L;
        final Subscriber<? super T> child;
        boolean emitting;
        Object index;
        boolean missed;
        final ReplaySubscriber<T> parent;
        final AtomicLong totalRequested = new AtomicLong();

        InnerSubscription(ReplaySubscriber<T> parent2, Subscriber<? super T> child2) {
            this.parent = parent2;
            this.child = child2;
        }

        public void request(long n) {
            long r;
            if (SubscriptionHelper.validate(n)) {
                do {
                    r = get();
                    if (r != Long.MIN_VALUE) {
                        if (r >= 0 && n == 0) {
                            return;
                        }
                    } else {
                        return;
                    }
                } while (!compareAndSet(r, BackpressureHelper.addCap(r, n)));
                BackpressureHelper.add(this.totalRequested, n);
                this.parent.manageRequests();
                this.parent.buffer.replay(this);
            }
        }

        public long produced(long n) {
            return BackpressureHelper.producedCancel(this, n);
        }

        public boolean isDisposed() {
            return get() == Long.MIN_VALUE;
        }

        public void cancel() {
            dispose();
        }

        public void dispose() {
            if (getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
                this.parent.remove(this);
                this.parent.manageRequests();
            }
        }

        /* access modifiers changed from: package-private */
        public <U> U index() {
            return this.index;
        }
    }

    static final class UnboundedReplayBuffer<T> extends ArrayList<Object> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 7063189396499112664L;
        volatile int size;

        UnboundedReplayBuffer(int capacityHint) {
            super(capacityHint);
        }

        public void next(T value) {
            add(NotificationLite.next(value));
            this.size++;
        }

        public void error(Throwable e) {
            add(NotificationLite.error(e));
            this.size++;
        }

        public void complete() {
            add(NotificationLite.complete());
            this.size++;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0013, code lost:
            if (r15.isDisposed() == false) goto L_0x0016;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0015, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0016, code lost:
            r1 = r14.size;
            r2 = (java.lang.Integer) r15.index();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x001f, code lost:
            if (r2 == null) goto L_0x0026;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0021, code lost:
            r4 = r2.intValue();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0026, code lost:
            r4 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0027, code lost:
            r5 = r15.get();
            r7 = r5;
            r9 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0032, code lost:
            if (r5 == 0) goto L_0x0067;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0034, code lost:
            if (r4 >= r1) goto L_0x0067;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0036, code lost:
            r11 = get(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x003e, code lost:
            if (io.reactivex.internal.util.NotificationLite.accept(r11, r0) == false) goto L_0x0041;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0040, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0046, code lost:
            if (r15.isDisposed() == false) goto L_0x0049;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0048, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0049, code lost:
            r4 = r4 + 1;
            r5 = r5 - 1;
            r9 = r9 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0050, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x0051, code lost:
            io.reactivex.exceptions.Exceptions.throwIfFatal(r3);
            r15.dispose();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x005b, code lost:
            if (io.reactivex.internal.util.NotificationLite.isError(r11) != false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x0063, code lost:
            r0.onError(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x0069, code lost:
            if (r9 == 0) goto L_0x007d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x006b, code lost:
            r15.index = java.lang.Integer.valueOf(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0078, code lost:
            if (r7 == Long.MAX_VALUE) goto L_0x007d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x007a, code lost:
            r15.produced(r9);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x007d, code lost:
            monitor-enter(r15);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:0x0080, code lost:
            if (r15.missed != false) goto L_0x0086;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:0x0082, code lost:
            r15.emitting = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:0x0084, code lost:
            monitor-exit(r15);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x0085, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x0086, code lost:
            r15.missed = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x0088, code lost:
            monitor-exit(r15);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:69:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x000d, code lost:
            r0 = r15.child;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void replay(io.reactivex.internal.operators.flowable.FlowableReplay.InnerSubscription<T> r15) {
            /*
                r14 = this;
                monitor-enter(r15)
                boolean r0 = r15.emitting     // Catch:{ all -> 0x008d }
                r1 = 1
                if (r0 == 0) goto L_0x000a
                r15.missed = r1     // Catch:{ all -> 0x008d }
                monitor-exit(r15)     // Catch:{ all -> 0x008d }
                return
            L_0x000a:
                r15.emitting = r1     // Catch:{ all -> 0x008d }
                monitor-exit(r15)     // Catch:{ all -> 0x008d }
                org.reactivestreams.Subscriber<? super T> r0 = r15.child
            L_0x000f:
                boolean r1 = r15.isDisposed()
                if (r1 == 0) goto L_0x0016
                return
            L_0x0016:
                int r1 = r14.size
                java.lang.Object r2 = r15.index()
                java.lang.Integer r2 = (java.lang.Integer) r2
                r3 = 0
                if (r2 == 0) goto L_0x0026
                int r4 = r2.intValue()
                goto L_0x0027
            L_0x0026:
                r4 = 0
            L_0x0027:
                long r5 = r15.get()
                r7 = r5
                r9 = 0
            L_0x002e:
                r11 = 0
                int r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
                if (r13 == 0) goto L_0x0067
                if (r4 >= r1) goto L_0x0067
                java.lang.Object r11 = r14.get(r4)
                boolean r12 = io.reactivex.internal.util.NotificationLite.accept((java.lang.Object) r11, r0)     // Catch:{ all -> 0x0050 }
                if (r12 == 0) goto L_0x0041
                return
            L_0x0041:
                boolean r12 = r15.isDisposed()
                if (r12 == 0) goto L_0x0049
                return
            L_0x0049:
                int r4 = r4 + 1
                r12 = 1
                long r5 = r5 - r12
                long r9 = r9 + r12
                goto L_0x002e
            L_0x0050:
                r3 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r3)
                r15.dispose()
                boolean r12 = io.reactivex.internal.util.NotificationLite.isError(r11)
                if (r12 != 0) goto L_0x0066
                boolean r12 = io.reactivex.internal.util.NotificationLite.isComplete(r11)
                if (r12 != 0) goto L_0x0066
                r0.onError(r3)
            L_0x0066:
                return
            L_0x0067:
                int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
                if (r13 == 0) goto L_0x007d
                java.lang.Integer r11 = java.lang.Integer.valueOf(r4)
                r15.index = r11
                r11 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
                int r13 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
                if (r13 == 0) goto L_0x007d
                r15.produced(r9)
            L_0x007d:
                monitor-enter(r15)
                boolean r11 = r15.missed     // Catch:{ all -> 0x008a }
                if (r11 != 0) goto L_0x0086
                r15.emitting = r3     // Catch:{ all -> 0x008a }
                monitor-exit(r15)     // Catch:{ all -> 0x008a }
                return
            L_0x0086:
                r15.missed = r3     // Catch:{ all -> 0x008a }
                monitor-exit(r15)     // Catch:{ all -> 0x008a }
                goto L_0x000f
            L_0x008a:
                r3 = move-exception
                monitor-exit(r15)     // Catch:{ all -> 0x008a }
                throw r3
            L_0x008d:
                r0 = move-exception
                monitor-exit(r15)     // Catch:{ all -> 0x008d }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.UnboundedReplayBuffer.replay(io.reactivex.internal.operators.flowable.FlowableReplay$InnerSubscription):void");
        }
    }

    static final class Node extends AtomicReference<Node> {
        private static final long serialVersionUID = 245354315435971818L;
        final long index;
        final Object value;

        Node(Object value2, long index2) {
            this.value = value2;
            this.index = index2;
        }
    }

    static class BoundedReplayBuffer<T> extends AtomicReference<Node> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 2346567790059478686L;
        long index;
        int size;
        Node tail;

        BoundedReplayBuffer() {
            Node n = new Node((Object) null, 0);
            this.tail = n;
            set(n);
        }

        /* access modifiers changed from: package-private */
        public final void addLast(Node n) {
            this.tail.set(n);
            this.tail = n;
            this.size++;
        }

        /* access modifiers changed from: package-private */
        public final void removeFirst() {
            Node next = (Node) ((Node) get()).get();
            if (next != null) {
                this.size--;
                setFirst(next);
                return;
            }
            throw new IllegalStateException("Empty list!");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: io.reactivex.internal.operators.flowable.FlowableReplay$Node} */
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void removeSome(int r3) {
            /*
                r2 = this;
                java.lang.Object r0 = r2.get()
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r0 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r0
            L_0x0006:
                if (r3 <= 0) goto L_0x0018
                java.lang.Object r1 = r0.get()
                r0 = r1
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r0 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r0
                int r3 = r3 + -1
                int r1 = r2.size
                int r1 = r1 + -1
                r2.size = r1
                goto L_0x0006
            L_0x0018:
                r2.setFirst(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.BoundedReplayBuffer.removeSome(int):void");
        }

        /* access modifiers changed from: package-private */
        public final void setFirst(Node n) {
            set(n);
        }

        public final void next(T value) {
            Object o = enterTransform(NotificationLite.next(value));
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncate();
        }

        public final void error(Throwable e) {
            Object o = enterTransform(NotificationLite.error(e));
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncateFinal();
        }

        public final void complete() {
            Object o = enterTransform(NotificationLite.complete());
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncateFinal();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0011, code lost:
            if (r14.isDisposed() == false) goto L_0x0014;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0013, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0014, code lost:
            r2 = r14.get();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
            if (r2 != Long.MAX_VALUE) goto L_0x0024;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0022, code lost:
            r4 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0024, code lost:
            r4 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0025, code lost:
            r5 = 0;
            r7 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r14.index();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x002d, code lost:
            if (r7 != null) goto L_0x003c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x002f, code lost:
            r7 = getHead();
            r14.index = r7;
            io.reactivex.internal.util.BackpressureHelper.add(r14.totalRequested, r7.index);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0040, code lost:
            if (r2 == 0) goto L_0x0085;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0042, code lost:
            r10 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r7.get();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0048, code lost:
            if (r10 == null) goto L_0x0085;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x004a, code lost:
            r8 = leaveTransform(r10.value);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0057, code lost:
            if (io.reactivex.internal.util.NotificationLite.accept(r8, r14.child) == false) goto L_0x005c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0059, code lost:
            r14.index = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x005b, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x005c, code lost:
            r5 = r5 + 1;
            r2 = r2 - 1;
            r7 = r10;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0066, code lost:
            if (r14.isDisposed() == false) goto L_0x003c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x0068, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x006a, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x006b, code lost:
            io.reactivex.exceptions.Exceptions.throwIfFatal(r0);
            r14.index = null;
            r14.dispose();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0077, code lost:
            if (io.reactivex.internal.util.NotificationLite.isError(r8) != false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x007f, code lost:
            r14.child.onError(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0087, code lost:
            if (r5 == 0) goto L_0x0090;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x0089, code lost:
            r14.index = r7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x008b, code lost:
            if (r4 != false) goto L_0x0090;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x008d, code lost:
            r14.produced(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:43:0x0090, code lost:
            monitor-enter(r14);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:0x0093, code lost:
            if (r14.missed != false) goto L_0x0099;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x0095, code lost:
            r14.emitting = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x0097, code lost:
            monitor-exit(r14);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x0098, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x0099, code lost:
            r14.missed = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:51:0x009b, code lost:
            monitor-exit(r14);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:70:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:72:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void replay(io.reactivex.internal.operators.flowable.FlowableReplay.InnerSubscription<T> r14) {
            /*
                r13 = this;
                monitor-enter(r14)
                boolean r0 = r14.emitting     // Catch:{ all -> 0x00a1 }
                r1 = 1
                if (r0 == 0) goto L_0x000a
                r14.missed = r1     // Catch:{ all -> 0x00a1 }
                monitor-exit(r14)     // Catch:{ all -> 0x00a1 }
                return
            L_0x000a:
                r14.emitting = r1     // Catch:{ all -> 0x00a1 }
                monitor-exit(r14)     // Catch:{ all -> 0x00a1 }
            L_0x000d:
                boolean r0 = r14.isDisposed()
                if (r0 == 0) goto L_0x0014
                return
            L_0x0014:
                long r2 = r14.get()
                r4 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
                r0 = 0
                int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r6 != 0) goto L_0x0024
                r4 = 1
                goto L_0x0025
            L_0x0024:
                r4 = 0
            L_0x0025:
                r5 = 0
                java.lang.Object r7 = r14.index()
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r7 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r7
                if (r7 != 0) goto L_0x003c
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r7 = r13.getHead()
                r14.index = r7
                java.util.concurrent.atomic.AtomicLong r8 = r14.totalRequested
                long r9 = r7.index
                io.reactivex.internal.util.BackpressureHelper.add(r8, r9)
            L_0x003c:
                r8 = 0
                int r10 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r10 == 0) goto L_0x0085
                java.lang.Object r10 = r7.get()
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r10 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r10
                if (r10 == 0) goto L_0x0085
                java.lang.Object r8 = r10.value
                java.lang.Object r8 = r13.leaveTransform(r8)
                r9 = 0
                org.reactivestreams.Subscriber<? super T> r11 = r14.child     // Catch:{ all -> 0x006a }
                boolean r11 = io.reactivex.internal.util.NotificationLite.accept((java.lang.Object) r8, r11)     // Catch:{ all -> 0x006a }
                if (r11 == 0) goto L_0x005c
                r14.index = r9     // Catch:{ all -> 0x006a }
                return
            L_0x005c:
                r11 = 1
                long r5 = r5 + r11
                long r2 = r2 - r11
                r7 = r10
                boolean r8 = r14.isDisposed()
                if (r8 == 0) goto L_0x0069
                return
            L_0x0069:
                goto L_0x003c
            L_0x006a:
                r0 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r0)
                r14.index = r9
                r14.dispose()
                boolean r1 = io.reactivex.internal.util.NotificationLite.isError(r8)
                if (r1 != 0) goto L_0x0084
                boolean r1 = io.reactivex.internal.util.NotificationLite.isComplete(r8)
                if (r1 != 0) goto L_0x0084
                org.reactivestreams.Subscriber<? super T> r1 = r14.child
                r1.onError(r0)
            L_0x0084:
                return
            L_0x0085:
                int r10 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
                if (r10 == 0) goto L_0x0090
                r14.index = r7
                if (r4 != 0) goto L_0x0090
                r14.produced(r5)
            L_0x0090:
                monitor-enter(r14)
                boolean r8 = r14.missed     // Catch:{ all -> 0x009e }
                if (r8 != 0) goto L_0x0099
                r14.emitting = r0     // Catch:{ all -> 0x009e }
                monitor-exit(r14)     // Catch:{ all -> 0x009e }
                return
            L_0x0099:
                r14.missed = r0     // Catch:{ all -> 0x009e }
                monitor-exit(r14)     // Catch:{ all -> 0x009e }
                goto L_0x000d
            L_0x009e:
                r0 = move-exception
                monitor-exit(r14)     // Catch:{ all -> 0x009e }
                throw r0
            L_0x00a1:
                r0 = move-exception
                monitor-exit(r14)     // Catch:{ all -> 0x00a1 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.BoundedReplayBuffer.replay(io.reactivex.internal.operators.flowable.FlowableReplay$InnerSubscription):void");
        }

        /* access modifiers changed from: package-private */
        public Object enterTransform(Object value) {
            return value;
        }

        /* access modifiers changed from: package-private */
        public Object leaveTransform(Object value) {
            return value;
        }

        /* access modifiers changed from: package-private */
        public void truncate() {
        }

        /* access modifiers changed from: package-private */
        public void truncateFinal() {
        }

        /* access modifiers changed from: package-private */
        public final void collect(Collection<? super T> output) {
            Node n = getHead();
            while (true) {
                Node next = (Node) n.get();
                if (next != null) {
                    Object v = leaveTransform(next.value);
                    if (!NotificationLite.isComplete(v) && !NotificationLite.isError(v)) {
                        output.add(NotificationLite.getValue(v));
                        n = next;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean hasError() {
            return this.tail.value != null && NotificationLite.isError(leaveTransform(this.tail.value));
        }

        /* access modifiers changed from: package-private */
        public boolean hasCompleted() {
            return this.tail.value != null && NotificationLite.isComplete(leaveTransform(this.tail.value));
        }

        /* access modifiers changed from: package-private */
        public Node getHead() {
            return (Node) get();
        }
    }

    static final class SizeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = -5898283885385201806L;
        final int limit;

        SizeBoundReplayBuffer(int limit2) {
            this.limit = limit2;
        }

        /* access modifiers changed from: package-private */
        public void truncate() {
            if (this.size > this.limit) {
                removeFirst();
            }
        }
    }

    static final class SizeAndTimeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = 3457957419649567404L;
        final int limit;
        final long maxAge;
        final Scheduler scheduler;
        final TimeUnit unit;

        SizeAndTimeBoundReplayBuffer(int limit2, long maxAge2, TimeUnit unit2, Scheduler scheduler2) {
            this.scheduler = scheduler2;
            this.limit = limit2;
            this.maxAge = maxAge2;
            this.unit = unit2;
        }

        /* access modifiers changed from: package-private */
        public Object enterTransform(Object value) {
            return new Timed(value, this.scheduler.now(this.unit), this.unit);
        }

        /* access modifiers changed from: package-private */
        public Object leaveTransform(Object value) {
            return ((Timed) value).value();
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: io.reactivex.internal.operators.flowable.FlowableReplay$Node} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: io.reactivex.internal.operators.flowable.FlowableReplay$Node} */
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void truncate() {
            /*
                r9 = this;
                io.reactivex.Scheduler r0 = r9.scheduler
                java.util.concurrent.TimeUnit r1 = r9.unit
                long r0 = r0.now(r1)
                long r2 = r9.maxAge
                long r0 = r0 - r2
                java.lang.Object r2 = r9.get()
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r2 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r2
                java.lang.Object r3 = r2.get()
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r3 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r3
                r4 = 0
            L_0x0018:
                if (r3 == 0) goto L_0x004e
                int r5 = r9.size
                int r6 = r9.limit
                if (r5 <= r6) goto L_0x0031
                int r4 = r4 + 1
                int r5 = r9.size
                int r5 = r5 + -1
                r9.size = r5
                r2 = r3
                java.lang.Object r5 = r3.get()
                r3 = r5
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r3 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r3
                goto L_0x0018
            L_0x0031:
                java.lang.Object r5 = r3.value
                io.reactivex.schedulers.Timed r5 = (io.reactivex.schedulers.Timed) r5
                long r6 = r5.time()
                int r8 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
                if (r8 > 0) goto L_0x004e
                int r4 = r4 + 1
                int r6 = r9.size
                int r6 = r6 + -1
                r9.size = r6
                r2 = r3
                java.lang.Object r6 = r3.get()
                r3 = r6
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r3 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r3
                goto L_0x0018
            L_0x004e:
                if (r4 == 0) goto L_0x0053
                r9.setFirst(r2)
            L_0x0053:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.SizeAndTimeBoundReplayBuffer.truncate():void");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: io.reactivex.internal.operators.flowable.FlowableReplay$Node} */
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void truncateFinal() {
            /*
                r10 = this;
                io.reactivex.Scheduler r0 = r10.scheduler
                java.util.concurrent.TimeUnit r1 = r10.unit
                long r0 = r0.now(r1)
                long r2 = r10.maxAge
                long r0 = r0 - r2
                java.lang.Object r2 = r10.get()
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r2 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r2
                java.lang.Object r3 = r2.get()
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r3 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r3
                r4 = 0
            L_0x0018:
                if (r3 == 0) goto L_0x003b
                int r5 = r10.size
                r6 = 1
                if (r5 <= r6) goto L_0x003b
                java.lang.Object r5 = r3.value
                io.reactivex.schedulers.Timed r5 = (io.reactivex.schedulers.Timed) r5
                long r7 = r5.time()
                int r9 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
                if (r9 > 0) goto L_0x003b
                int r4 = r4 + 1
                int r7 = r10.size
                int r7 = r7 - r6
                r10.size = r7
                r2 = r3
                java.lang.Object r6 = r3.get()
                r3 = r6
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r3 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r3
                goto L_0x0018
            L_0x003b:
                if (r4 == 0) goto L_0x0040
                r10.setFirst(r2)
            L_0x0040:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.SizeAndTimeBoundReplayBuffer.truncateFinal():void");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: io.reactivex.internal.operators.flowable.FlowableReplay$Node} */
        /* access modifiers changed from: package-private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public io.reactivex.internal.operators.flowable.FlowableReplay.Node getHead() {
            /*
                r8 = this;
                io.reactivex.Scheduler r0 = r8.scheduler
                java.util.concurrent.TimeUnit r1 = r8.unit
                long r0 = r0.now(r1)
                long r2 = r8.maxAge
                long r0 = r0 - r2
                java.lang.Object r2 = r8.get()
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r2 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r2
                java.lang.Object r3 = r2.get()
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r3 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r3
            L_0x0017:
                if (r3 != 0) goto L_0x001a
                goto L_0x0044
            L_0x001a:
                java.lang.Object r4 = r3.value
                io.reactivex.schedulers.Timed r4 = (io.reactivex.schedulers.Timed) r4
                java.lang.Object r5 = r4.value()
                boolean r5 = io.reactivex.internal.util.NotificationLite.isComplete(r5)
                if (r5 != 0) goto L_0x0044
                java.lang.Object r5 = r4.value()
                boolean r5 = io.reactivex.internal.util.NotificationLite.isError(r5)
                if (r5 == 0) goto L_0x0033
                goto L_0x0044
            L_0x0033:
                long r5 = r4.time()
                int r7 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
                if (r7 > 0) goto L_0x0044
                r2 = r3
                java.lang.Object r5 = r3.get()
                r3 = r5
                io.reactivex.internal.operators.flowable.FlowableReplay$Node r3 = (io.reactivex.internal.operators.flowable.FlowableReplay.Node) r3
                goto L_0x0017
            L_0x0044:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.SizeAndTimeBoundReplayBuffer.getHead():io.reactivex.internal.operators.flowable.FlowableReplay$Node");
        }
    }

    static final class MultiCastPublisher<R, U> implements Publisher<R> {
        private final Callable<? extends ConnectableFlowable<U>> connectableFactory;
        private final Function<? super Flowable<U>, ? extends Publisher<R>> selector;

        MultiCastPublisher(Callable<? extends ConnectableFlowable<U>> connectableFactory2, Function<? super Flowable<U>, ? extends Publisher<R>> selector2) {
            this.connectableFactory = connectableFactory2;
            this.selector = selector2;
        }

        public void subscribe(Subscriber<? super R> child) {
            try {
                ConnectableFlowable<U> co = (ConnectableFlowable) ObjectHelper.requireNonNull(this.connectableFactory.call(), "The connectableFactory returned null");
                try {
                    Publisher<R> observable = (Publisher) ObjectHelper.requireNonNull(this.selector.apply(co), "The selector returned a null Publisher");
                    SubscriberResourceWrapper<R> srw = new SubscriberResourceWrapper<>(child);
                    observable.subscribe(srw);
                    co.connect(new DisposableConsumer(srw));
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    EmptySubscription.error(e, child);
                }
            } catch (Throwable e2) {
                Exceptions.throwIfFatal(e2);
                EmptySubscription.error(e2, child);
            }
        }

        final class DisposableConsumer implements Consumer<Disposable> {
            private final SubscriberResourceWrapper<R> srw;

            DisposableConsumer(SubscriberResourceWrapper<R> srw2) {
                this.srw = srw2;
            }

            public void accept(Disposable r) {
                this.srw.setResource(r);
            }
        }
    }

    static final class ConnectableFlowableReplay<T> extends ConnectableFlowable<T> {
        private final ConnectableFlowable<T> co;
        private final Flowable<T> observable;

        ConnectableFlowableReplay(ConnectableFlowable<T> co2, Flowable<T> observable2) {
            this.co = co2;
            this.observable = observable2;
        }

        public void connect(Consumer<? super Disposable> connection) {
            this.co.connect(connection);
        }

        /* access modifiers changed from: protected */
        public void subscribeActual(Subscriber<? super T> s) {
            this.observable.subscribe(s);
        }
    }

    static final class ReplayBufferTask<T> implements Callable<ReplayBuffer<T>> {
        private final int bufferSize;

        ReplayBufferTask(int bufferSize2) {
            this.bufferSize = bufferSize2;
        }

        public ReplayBuffer<T> call() {
            return new SizeBoundReplayBuffer(this.bufferSize);
        }
    }

    static final class ScheduledReplayBufferTask<T> implements Callable<ReplayBuffer<T>> {
        private final int bufferSize;
        private final long maxAge;
        private final Scheduler scheduler;
        private final TimeUnit unit;

        ScheduledReplayBufferTask(int bufferSize2, long maxAge2, TimeUnit unit2, Scheduler scheduler2) {
            this.bufferSize = bufferSize2;
            this.maxAge = maxAge2;
            this.unit = unit2;
            this.scheduler = scheduler2;
        }

        public ReplayBuffer<T> call() {
            return new SizeAndTimeBoundReplayBuffer(this.bufferSize, this.maxAge, this.unit, this.scheduler);
        }
    }

    static final class ReplayPublisher<T> implements Publisher<T> {
        private final Callable<? extends ReplayBuffer<T>> bufferFactory;
        private final AtomicReference<ReplaySubscriber<T>> curr;

        ReplayPublisher(AtomicReference<ReplaySubscriber<T>> curr2, Callable<? extends ReplayBuffer<T>> bufferFactory2) {
            this.curr = curr2;
            this.bufferFactory = bufferFactory2;
        }

        /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void subscribe(org.reactivestreams.Subscriber<? super T> r6) {
            /*
                r5 = this;
            L_0x0000:
                java.util.concurrent.atomic.AtomicReference<io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber<T>> r0 = r5.curr
                java.lang.Object r0 = r0.get()
                io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber r0 = (io.reactivex.internal.operators.flowable.FlowableReplay.ReplaySubscriber) r0
                if (r0 != 0) goto L_0x002d
                java.util.concurrent.Callable<? extends io.reactivex.internal.operators.flowable.FlowableReplay$ReplayBuffer<T>> r1 = r5.bufferFactory     // Catch:{ all -> 0x0024 }
                java.lang.Object r1 = r1.call()     // Catch:{ all -> 0x0024 }
                io.reactivex.internal.operators.flowable.FlowableReplay$ReplayBuffer r1 = (io.reactivex.internal.operators.flowable.FlowableReplay.ReplayBuffer) r1     // Catch:{ all -> 0x0024 }
                io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber r2 = new io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber
                r2.<init>(r1)
                java.util.concurrent.atomic.AtomicReference<io.reactivex.internal.operators.flowable.FlowableReplay$ReplaySubscriber<T>> r3 = r5.curr
                r4 = 0
                boolean r3 = r3.compareAndSet(r4, r2)
                if (r3 != 0) goto L_0x0022
                goto L_0x0000
            L_0x0022:
                r0 = r2
                goto L_0x002d
            L_0x0024:
                r1 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r1)
                java.lang.RuntimeException r2 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r1)
                throw r2
            L_0x002d:
                io.reactivex.internal.operators.flowable.FlowableReplay$InnerSubscription r1 = new io.reactivex.internal.operators.flowable.FlowableReplay$InnerSubscription
                r1.<init>(r0, r6)
                r6.onSubscribe(r1)
                r0.add(r1)
                boolean r2 = r1.isDisposed()
                if (r2 == 0) goto L_0x0042
                r0.remove(r1)
                return
            L_0x0042:
                r0.manageRequests()
                io.reactivex.internal.operators.flowable.FlowableReplay$ReplayBuffer<T> r2 = r0.buffer
                r2.replay(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowableReplay.ReplayPublisher.subscribe(org.reactivestreams.Subscriber):void");
        }
    }

    static final class DefaultUnboundedFactory implements Callable<Object> {
        DefaultUnboundedFactory() {
        }

        public Object call() {
            return new UnboundedReplayBuffer(16);
        }
    }
}
