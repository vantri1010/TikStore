package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.internal.fuseable.HasUpstreamPublisher;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.fuseable.SimpleQueue;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowablePublish<T> extends ConnectableFlowable<T> implements HasUpstreamPublisher<T> {
    static final long CANCELLED = Long.MIN_VALUE;
    final int bufferSize;
    final AtomicReference<PublishSubscriber<T>> current;
    final Publisher<T> onSubscribe;
    final Flowable<T> source;

    public static <T> ConnectableFlowable<T> create(Flowable<T> source2, int bufferSize2) {
        AtomicReference<PublishSubscriber<T>> curr = new AtomicReference<>();
        return RxJavaPlugins.onAssembly(new FlowablePublish(new FlowablePublisher<>(curr, bufferSize2), source2, curr, bufferSize2));
    }

    private FlowablePublish(Publisher<T> onSubscribe2, Flowable<T> source2, AtomicReference<PublishSubscriber<T>> current2, int bufferSize2) {
        this.onSubscribe = onSubscribe2;
        this.source = source2;
        this.current = current2;
        this.bufferSize = bufferSize2;
    }

    public Publisher<T> source() {
        return this.source;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(Subscriber<? super T> s) {
        this.onSubscribe.subscribe(s);
    }

    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(io.reactivex.functions.Consumer<? super io.reactivex.disposables.Disposable> r5) {
        /*
            r4 = this;
        L_0x0000:
            java.util.concurrent.atomic.AtomicReference<io.reactivex.internal.operators.flowable.FlowablePublish$PublishSubscriber<T>> r0 = r4.current
            java.lang.Object r0 = r0.get()
            io.reactivex.internal.operators.flowable.FlowablePublish$PublishSubscriber r0 = (io.reactivex.internal.operators.flowable.FlowablePublish.PublishSubscriber) r0
            if (r0 == 0) goto L_0x0010
            boolean r1 = r0.isDisposed()
            if (r1 == 0) goto L_0x0023
        L_0x0010:
            io.reactivex.internal.operators.flowable.FlowablePublish$PublishSubscriber r1 = new io.reactivex.internal.operators.flowable.FlowablePublish$PublishSubscriber
            java.util.concurrent.atomic.AtomicReference<io.reactivex.internal.operators.flowable.FlowablePublish$PublishSubscriber<T>> r2 = r4.current
            int r3 = r4.bufferSize
            r1.<init>(r2, r3)
            java.util.concurrent.atomic.AtomicReference<io.reactivex.internal.operators.flowable.FlowablePublish$PublishSubscriber<T>> r2 = r4.current
            boolean r2 = r2.compareAndSet(r0, r1)
            if (r2 != 0) goto L_0x0022
            goto L_0x0000
        L_0x0022:
            r0 = r1
        L_0x0023:
            java.util.concurrent.atomic.AtomicBoolean r1 = r0.shouldConnect
            boolean r1 = r1.get()
            r2 = 1
            r3 = 0
            if (r1 != 0) goto L_0x0036
            java.util.concurrent.atomic.AtomicBoolean r1 = r0.shouldConnect
            boolean r1 = r1.compareAndSet(r3, r2)
            if (r1 == 0) goto L_0x0036
            goto L_0x0037
        L_0x0036:
            r2 = 0
        L_0x0037:
            r1 = r2
            r5.accept(r0)     // Catch:{ all -> 0x0045 }
            if (r1 == 0) goto L_0x0044
            io.reactivex.Flowable<T> r2 = r4.source
            r2.subscribe(r0)
        L_0x0044:
            return
        L_0x0045:
            r2 = move-exception
            io.reactivex.exceptions.Exceptions.throwIfFatal(r2)
            java.lang.RuntimeException r3 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.flowable.FlowablePublish.connect(io.reactivex.functions.Consumer):void");
    }

    static final class PublishSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Disposable {
        static final InnerSubscriber[] EMPTY = new InnerSubscriber[0];
        static final InnerSubscriber[] TERMINATED = new InnerSubscriber[0];
        private static final long serialVersionUID = -202316842419149694L;
        final int bufferSize;
        final AtomicReference<PublishSubscriber<T>> current;
        volatile SimpleQueue<T> queue;
        final AtomicReference<Subscription> s = new AtomicReference<>();
        final AtomicBoolean shouldConnect;
        int sourceMode;
        final AtomicReference<InnerSubscriber[]> subscribers = new AtomicReference<>(EMPTY);
        volatile Object terminalEvent;

        PublishSubscriber(AtomicReference<PublishSubscriber<T>> current2, int bufferSize2) {
            this.current = current2;
            this.shouldConnect = new AtomicBoolean();
            this.bufferSize = bufferSize2;
        }

        public void dispose() {
            InnerSubscriber[] innerSubscriberArr = this.subscribers.get();
            InnerSubscriber[] innerSubscriberArr2 = TERMINATED;
            if (innerSubscriberArr != innerSubscriberArr2 && this.subscribers.getAndSet(innerSubscriberArr2) != TERMINATED) {
                this.current.compareAndSet(this, (Object) null);
                SubscriptionHelper.cancel(this.s);
            }
        }

        public boolean isDisposed() {
            return this.subscribers.get() == TERMINATED;
        }

        public void onSubscribe(Subscription s2) {
            if (SubscriptionHelper.setOnce(this.s, s2)) {
                if (s2 instanceof QueueSubscription) {
                    QueueSubscription<T> qs = (QueueSubscription) s2;
                    int m = qs.requestFusion(3);
                    if (m == 1) {
                        this.sourceMode = m;
                        this.queue = qs;
                        this.terminalEvent = NotificationLite.complete();
                        dispatch();
                        return;
                    } else if (m == 2) {
                        this.sourceMode = m;
                        this.queue = qs;
                        s2.request((long) this.bufferSize);
                        return;
                    }
                }
                this.queue = new SpscArrayQueue(this.bufferSize);
                s2.request((long) this.bufferSize);
            }
        }

        public void onNext(T t) {
            if (this.sourceMode != 0 || this.queue.offer(t)) {
                dispatch();
            } else {
                onError(new MissingBackpressureException("Prefetch queue is full?!"));
            }
        }

        public void onError(Throwable e) {
            if (this.terminalEvent == null) {
                this.terminalEvent = NotificationLite.error(e);
                dispatch();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        public void onComplete() {
            if (this.terminalEvent == null) {
                this.terminalEvent = NotificationLite.complete();
                dispatch();
            }
        }

        /* access modifiers changed from: package-private */
        public boolean add(InnerSubscriber<T> producer) {
            InnerSubscriber[] c;
            InnerSubscriber[] u;
            do {
                c = this.subscribers.get();
                if (c == TERMINATED) {
                    return false;
                }
                int len = c.length;
                u = new InnerSubscriber[(len + 1)];
                System.arraycopy(c, 0, u, 0, len);
                u[len] = producer;
            } while (!this.subscribers.compareAndSet(c, u));
            return true;
        }

        /* access modifiers changed from: package-private */
        public void remove(InnerSubscriber<T> producer) {
            InnerSubscriber[] c;
            InnerSubscriber[] u;
            do {
                c = this.subscribers.get();
                int len = c.length;
                if (len != 0) {
                    int j = -1;
                    int i = 0;
                    while (true) {
                        if (i >= len) {
                            break;
                        } else if (c[i].equals(producer)) {
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
                            InnerSubscriber[] u2 = new InnerSubscriber[(len - 1)];
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

        /* access modifiers changed from: package-private */
        public boolean checkTerminated(Object term, boolean empty) {
            int i = 0;
            if (term != null) {
                if (!NotificationLite.isComplete(term)) {
                    Throwable t = NotificationLite.getError(term);
                    this.current.compareAndSet(this, (Object) null);
                    InnerSubscriber<?>[] a = (InnerSubscriber[]) this.subscribers.getAndSet(TERMINATED);
                    if (a.length != 0) {
                        int length = a.length;
                        while (i < length) {
                            a[i].child.onError(t);
                            i++;
                        }
                    } else {
                        RxJavaPlugins.onError(t);
                    }
                    return true;
                } else if (empty) {
                    this.current.compareAndSet(this, (Object) null);
                    InnerSubscriber<?>[] innerSubscriberArr = (InnerSubscriber[]) this.subscribers.getAndSet(TERMINATED);
                    int length2 = innerSubscriberArr.length;
                    while (i < length2) {
                        innerSubscriberArr[i].child.onComplete();
                        i++;
                    }
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public void dispatch() {
            T term;
            SimpleQueue<T> q;
            T v;
            T v2;
            Object term2;
            if (getAndIncrement() == 0) {
                int missed = 1;
                while (true) {
                    Object term3 = this.terminalEvent;
                    SimpleQueue<T> q2 = this.queue;
                    boolean empty = q2 == null || q2.isEmpty();
                    if (!checkTerminated(term3, empty)) {
                        if (!empty) {
                            InnerSubscriber<T>[] ps = (InnerSubscriber[]) this.subscribers.get();
                            int len = ps.length;
                            long maxRequested = Long.MAX_VALUE;
                            int cancelled = 0;
                            for (InnerSubscriber<T> ip : ps) {
                                long r = ip.get();
                                if (r >= 0) {
                                    maxRequested = Math.min(maxRequested, r);
                                } else if (r == Long.MIN_VALUE) {
                                    cancelled++;
                                }
                            }
                            if (len == cancelled) {
                                Object term4 = this.terminalEvent;
                                try {
                                    term2 = q2.poll();
                                } catch (Throwable v3) {
                                    Throwable ex = v3;
                                    Exceptions.throwIfFatal(ex);
                                    this.s.get().cancel();
                                    term4 = NotificationLite.error(ex);
                                    this.terminalEvent = term4;
                                    term2 = null;
                                }
                                if (!checkTerminated(term4, term2 == null)) {
                                    if (this.sourceMode != 1) {
                                        this.s.get().request(1);
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                int d = 0;
                                while (true) {
                                    if (((long) d) >= maxRequested) {
                                        break;
                                    }
                                    T term5 = this.terminalEvent;
                                    try {
                                        term = q2.poll();
                                    } catch (Throwable v4) {
                                        Throwable ex2 = v4;
                                        Exceptions.throwIfFatal(ex2);
                                        this.s.get().cancel();
                                        term5 = NotificationLite.error(ex2);
                                        this.terminalEvent = term5;
                                        term = null;
                                    }
                                    empty = term == null;
                                    if (!checkTerminated(term5, empty)) {
                                        if (empty) {
                                            SimpleQueue<T> simpleQueue = q2;
                                            T v5 = term5;
                                            break;
                                        }
                                        T value = NotificationLite.getValue(term);
                                        int length = ps.length;
                                        int i = 0;
                                        while (i < length) {
                                            InnerSubscriber<T> ip2 = ps[i];
                                            if (ip2.get() > 0) {
                                                v = term;
                                                ip2.child.onNext(value);
                                                q = q2;
                                                v2 = term5;
                                                ip2.produced(1);
                                            } else {
                                                v = term;
                                                q = q2;
                                                v2 = term5;
                                            }
                                            i++;
                                            term5 = v2;
                                            term = v;
                                            q2 = q;
                                        }
                                        T v6 = term;
                                        T v7 = term5;
                                        d++;
                                        q2 = q2;
                                    } else {
                                        return;
                                    }
                                }
                                if (d > 0 && this.sourceMode != 1) {
                                    this.s.get().request((long) d);
                                }
                                if (maxRequested != 0 && !empty) {
                                }
                            }
                        }
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    static final class InnerSubscriber<T> extends AtomicLong implements Subscription {
        private static final long serialVersionUID = -4453897557930727610L;
        final Subscriber<? super T> child;
        volatile PublishSubscriber<T> parent;

        InnerSubscriber(Subscriber<? super T> child2) {
            this.child = child2;
        }

        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.addCancel(this, n);
                PublishSubscriber<T> p = this.parent;
                if (p != null) {
                    p.dispatch();
                }
            }
        }

        public long produced(long n) {
            return BackpressureHelper.producedCancel(this, n);
        }

        public void cancel() {
            PublishSubscriber<T> p;
            if (get() != Long.MIN_VALUE && getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE && (p = this.parent) != null) {
                p.remove(this);
                p.dispatch();
            }
        }
    }

    static final class FlowablePublisher<T> implements Publisher<T> {
        private final int bufferSize;
        private final AtomicReference<PublishSubscriber<T>> curr;

        FlowablePublisher(AtomicReference<PublishSubscriber<T>> curr2, int bufferSize2) {
            this.curr = curr2;
            this.bufferSize = bufferSize2;
        }

        public void subscribe(Subscriber<? super T> child) {
            PublishSubscriber<T> r;
            InnerSubscriber<T> inner = new InnerSubscriber<>(child);
            child.onSubscribe(inner);
            while (true) {
                r = this.curr.get();
                if (r == null || r.isDisposed()) {
                    PublishSubscriber<T> u = new PublishSubscriber<>(this.curr, this.bufferSize);
                    if (!this.curr.compareAndSet(r, u)) {
                        continue;
                    } else {
                        r = u;
                    }
                }
                if (r.add(inner)) {
                    break;
                }
            }
            if (inner.get() == Long.MIN_VALUE) {
                r.remove(inner);
            } else {
                inner.parent = r;
            }
            r.dispatch();
        }
    }
}
