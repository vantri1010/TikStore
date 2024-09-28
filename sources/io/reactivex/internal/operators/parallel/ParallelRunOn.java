package io.reactivex.internal.operators.parallel;

import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.queue.SpscArrayQueue;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class ParallelRunOn<T> extends ParallelFlowable<T> {
    final int prefetch;
    final Scheduler scheduler;
    final ParallelFlowable<? extends T> source;

    public ParallelRunOn(ParallelFlowable<? extends T> parent, Scheduler scheduler2, int prefetch2) {
        this.source = parent;
        this.scheduler = scheduler2;
        this.prefetch = prefetch2;
    }

    public void subscribe(Subscriber<? super T>[] subscribers) {
        if (validate(subscribers)) {
            int n = subscribers.length;
            Subscriber<T>[] parents = new Subscriber[n];
            int prefetch2 = this.prefetch;
            for (int i = 0; i < n; i++) {
                ConditionalSubscriber conditionalSubscriber = subscribers[i];
                Scheduler.Worker w = this.scheduler.createWorker();
                SpscArrayQueue<T> q = new SpscArrayQueue<>(prefetch2);
                if (conditionalSubscriber instanceof ConditionalSubscriber) {
                    parents[i] = new RunOnConditionalSubscriber<>(conditionalSubscriber, prefetch2, q, w);
                } else {
                    parents[i] = new RunOnSubscriber<>(conditionalSubscriber, prefetch2, q, w);
                }
            }
            this.source.subscribe(parents);
        }
    }

    public int parallelism() {
        return this.source.parallelism();
    }

    static abstract class BaseRunOnSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Subscription, Runnable {
        private static final long serialVersionUID = 9222303586456402150L;
        volatile boolean cancelled;
        int consumed;
        volatile boolean done;
        Throwable error;
        final int limit;
        final int prefetch;
        final SpscArrayQueue<T> queue;
        final AtomicLong requested = new AtomicLong();
        Subscription s;
        final Scheduler.Worker worker;

        BaseRunOnSubscriber(int prefetch2, SpscArrayQueue<T> queue2, Scheduler.Worker worker2) {
            this.prefetch = prefetch2;
            this.queue = queue2;
            this.limit = prefetch2 - (prefetch2 >> 2);
            this.worker = worker2;
        }

        public final void onNext(T t) {
            if (!this.done) {
                if (!this.queue.offer(t)) {
                    this.s.cancel();
                    onError(new MissingBackpressureException("Queue is full?!"));
                    return;
                }
                schedule();
            }
        }

        public final void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.error = t;
            this.done = true;
            schedule();
        }

        public final void onComplete() {
            if (!this.done) {
                this.done = true;
                schedule();
            }
        }

        public final void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this.requested, n);
                schedule();
            }
        }

        public final void cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                this.s.cancel();
                this.worker.dispose();
                if (getAndIncrement() == 0) {
                    this.queue.clear();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public final void schedule() {
            if (getAndIncrement() == 0) {
                this.worker.schedule(this);
            }
        }
    }

    static final class RunOnSubscriber<T> extends BaseRunOnSubscriber<T> {
        private static final long serialVersionUID = 1075119423897941642L;
        final Subscriber<? super T> actual;

        RunOnSubscriber(Subscriber<? super T> actual2, int prefetch, SpscArrayQueue<T> queue, Scheduler.Worker worker) {
            super(prefetch, queue, worker);
            this.actual = actual2;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.s, s)) {
                this.s = s;
                this.actual.onSubscribe(this);
                s.request((long) this.prefetch);
            }
        }

        public void run() {
            long e;
            Throwable ex;
            int missed = 1;
            int c = this.consumed;
            SpscArrayQueue<T> q = this.queue;
            Subscriber<? super T> a = this.actual;
            int lim = this.limit;
            while (true) {
                long r = this.requested.get();
                long e2 = 0;
                while (e2 != r) {
                    if (this.cancelled) {
                        q.clear();
                        return;
                    }
                    boolean d = this.done;
                    if (!d || (ex = this.error) == null) {
                        T v = q.poll();
                        boolean empty = v == null;
                        if (d && empty) {
                            a.onComplete();
                            this.worker.dispose();
                            return;
                        } else if (empty) {
                            break;
                        } else {
                            a.onNext(v);
                            long e3 = e2 + 1;
                            c++;
                            int p = c;
                            if (p == lim) {
                                c = 0;
                                e = e3;
                                this.s.request((long) p);
                            } else {
                                e = e3;
                            }
                            e2 = e;
                        }
                    } else {
                        q.clear();
                        a.onError(ex);
                        this.worker.dispose();
                        return;
                    }
                }
                if (e2 == r) {
                    if (this.cancelled) {
                        q.clear();
                        return;
                    } else if (this.done) {
                        Throwable ex2 = this.error;
                        if (ex2 != null) {
                            q.clear();
                            a.onError(ex2);
                            this.worker.dispose();
                            return;
                        } else if (q.isEmpty()) {
                            a.onComplete();
                            this.worker.dispose();
                            return;
                        }
                    }
                }
                if (!(e2 == 0 || r == Long.MAX_VALUE)) {
                    this.requested.addAndGet(-e2);
                }
                int w = get();
                if (w == missed) {
                    this.consumed = c;
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else {
                    missed = w;
                }
            }
        }
    }

    static final class RunOnConditionalSubscriber<T> extends BaseRunOnSubscriber<T> {
        private static final long serialVersionUID = 1075119423897941642L;
        final ConditionalSubscriber<? super T> actual;

        RunOnConditionalSubscriber(ConditionalSubscriber<? super T> actual2, int prefetch, SpscArrayQueue<T> queue, Scheduler.Worker worker) {
            super(prefetch, queue, worker);
            this.actual = actual2;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.s, s)) {
                this.s = s;
                this.actual.onSubscribe(this);
                s.request((long) this.prefetch);
            }
        }

        public void run() {
            long e;
            Throwable ex;
            int missed = 1;
            int c = this.consumed;
            SpscArrayQueue<T> q = this.queue;
            ConditionalSubscriber<? super T> a = this.actual;
            int lim = this.limit;
            while (true) {
                long r = this.requested.get();
                long e2 = 0;
                while (e2 != r) {
                    if (this.cancelled) {
                        q.clear();
                        return;
                    }
                    boolean d = this.done;
                    if (!d || (ex = this.error) == null) {
                        T v = q.poll();
                        boolean empty = v == null;
                        if (d && empty) {
                            a.onComplete();
                            this.worker.dispose();
                            return;
                        } else if (empty) {
                            break;
                        } else {
                            if (a.tryOnNext(v)) {
                                e2++;
                            }
                            c++;
                            int p = c;
                            if (p == lim) {
                                c = 0;
                                e = e2;
                                this.s.request((long) p);
                            } else {
                                e = e2;
                            }
                            e2 = e;
                        }
                    } else {
                        q.clear();
                        a.onError(ex);
                        this.worker.dispose();
                        return;
                    }
                }
                if (e2 == r) {
                    if (this.cancelled) {
                        q.clear();
                        return;
                    } else if (this.done) {
                        Throwable ex2 = this.error;
                        if (ex2 != null) {
                            q.clear();
                            a.onError(ex2);
                            this.worker.dispose();
                            return;
                        } else if (q.isEmpty()) {
                            a.onComplete();
                            this.worker.dispose();
                            return;
                        }
                    }
                }
                if (!(e2 == 0 || r == Long.MAX_VALUE)) {
                    this.requested.addAndGet(-e2);
                }
                int w = get();
                if (w == missed) {
                    this.consumed = c;
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else {
                    missed = w;
                }
            }
        }
    }
}
