package io.reactivex.internal.operators.parallel;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.parallel.ParallelFailureHandling;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class ParallelDoOnNextTry<T> extends ParallelFlowable<T> {
    final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
    final Consumer<? super T> onNext;
    final ParallelFlowable<T> source;

    public ParallelDoOnNextTry(ParallelFlowable<T> source2, Consumer<? super T> onNext2, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler2) {
        this.source = source2;
        this.onNext = onNext2;
        this.errorHandler = errorHandler2;
    }

    public void subscribe(Subscriber<? super T>[] subscribers) {
        if (validate(subscribers)) {
            int n = subscribers.length;
            Subscriber<? super T>[] parents = new Subscriber[n];
            for (int i = 0; i < n; i++) {
                ConditionalSubscriber conditionalSubscriber = subscribers[i];
                if (conditionalSubscriber instanceof ConditionalSubscriber) {
                    parents[i] = new ParallelDoOnNextConditionalSubscriber<>(conditionalSubscriber, this.onNext, this.errorHandler);
                } else {
                    parents[i] = new ParallelDoOnNextSubscriber<>(conditionalSubscriber, this.onNext, this.errorHandler);
                }
            }
            this.source.subscribe(parents);
        }
    }

    public int parallelism() {
        return this.source.parallelism();
    }

    static final class ParallelDoOnNextSubscriber<T> implements ConditionalSubscriber<T>, Subscription {
        final Subscriber<? super T> actual;
        boolean done;
        final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
        final Consumer<? super T> onNext;
        Subscription s;

        ParallelDoOnNextSubscriber(Subscriber<? super T> actual2, Consumer<? super T> onNext2, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler2) {
            this.actual = actual2;
            this.onNext = onNext2;
            this.errorHandler = errorHandler2;
        }

        public void request(long n) {
            this.s.request(n);
        }

        public void cancel() {
            this.s.cancel();
        }

        public void onSubscribe(Subscription s2) {
            if (SubscriptionHelper.validate(this.s, s2)) {
                this.s = s2;
                this.actual.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            if (!tryOnNext(t)) {
                this.s.request(1);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:16:0x003d  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x004e A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean tryOnNext(T r10) {
            /*
                r9 = this;
                boolean r0 = r9.done
                r1 = 0
                if (r0 == 0) goto L_0x0006
                return r1
            L_0x0006:
                r2 = 0
            L_0x0008:
                r0 = 1
                io.reactivex.functions.Consumer<? super T> r4 = r9.onNext     // Catch:{ all -> 0x0015 }
                r4.accept(r10)     // Catch:{ all -> 0x0015 }
                org.reactivestreams.Subscriber<? super T> r1 = r9.actual
                r1.onNext(r10)
                return r0
            L_0x0015:
                r4 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r4)
                r5 = 2
                io.reactivex.functions.BiFunction<? super java.lang.Long, ? super java.lang.Throwable, io.reactivex.parallel.ParallelFailureHandling> r6 = r9.errorHandler     // Catch:{ all -> 0x0050 }
                r7 = 1
                long r7 = r7 + r2
                r2 = r7
                java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0050 }
                java.lang.Object r6 = r6.apply(r7, r4)     // Catch:{ all -> 0x0050 }
                java.lang.String r7 = "The errorHandler returned a null item"
                java.lang.Object r6 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r6, r7)     // Catch:{ all -> 0x0050 }
                io.reactivex.parallel.ParallelFailureHandling r6 = (io.reactivex.parallel.ParallelFailureHandling) r6     // Catch:{ all -> 0x0050 }
                int[] r7 = io.reactivex.internal.operators.parallel.ParallelDoOnNextTry.AnonymousClass1.$SwitchMap$io$reactivex$parallel$ParallelFailureHandling
                int r8 = r6.ordinal()
                r7 = r7[r8]
                if (r7 == r0) goto L_0x004f
                if (r7 == r5) goto L_0x004e
                r0 = 3
                if (r7 == r0) goto L_0x0047
                r9.cancel()
                r9.onError(r4)
                return r1
            L_0x0047:
                r9.cancel()
                r9.onComplete()
                return r1
            L_0x004e:
                return r1
            L_0x004f:
                goto L_0x0008
            L_0x0050:
                r6 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r6)
                r9.cancel()
                io.reactivex.exceptions.CompositeException r7 = new io.reactivex.exceptions.CompositeException
                java.lang.Throwable[] r5 = new java.lang.Throwable[r5]
                r5[r1] = r4
                r5[r0] = r6
                r7.<init>((java.lang.Throwable[]) r5)
                r9.onError(r7)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.parallel.ParallelDoOnNextTry.ParallelDoOnNextSubscriber.tryOnNext(java.lang.Object):boolean");
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.actual.onError(t);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.actual.onComplete();
            }
        }
    }

    /* renamed from: io.reactivex.internal.operators.parallel.ParallelDoOnNextTry$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$io$reactivex$parallel$ParallelFailureHandling;

        static {
            int[] iArr = new int[ParallelFailureHandling.values().length];
            $SwitchMap$io$reactivex$parallel$ParallelFailureHandling = iArr;
            try {
                iArr[ParallelFailureHandling.RETRY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$reactivex$parallel$ParallelFailureHandling[ParallelFailureHandling.SKIP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$io$reactivex$parallel$ParallelFailureHandling[ParallelFailureHandling.STOP.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    static final class ParallelDoOnNextConditionalSubscriber<T> implements ConditionalSubscriber<T>, Subscription {
        final ConditionalSubscriber<? super T> actual;
        boolean done;
        final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
        final Consumer<? super T> onNext;
        Subscription s;

        ParallelDoOnNextConditionalSubscriber(ConditionalSubscriber<? super T> actual2, Consumer<? super T> onNext2, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler2) {
            this.actual = actual2;
            this.onNext = onNext2;
            this.errorHandler = errorHandler2;
        }

        public void request(long n) {
            this.s.request(n);
        }

        public void cancel() {
            this.s.cancel();
        }

        public void onSubscribe(Subscription s2) {
            if (SubscriptionHelper.validate(this.s, s2)) {
                this.s = s2;
                this.actual.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            if (!tryOnNext(t) && !this.done) {
                this.s.request(1);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:15:0x003e  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x004f A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean tryOnNext(T r10) {
            /*
                r9 = this;
                boolean r0 = r9.done
                r1 = 0
                if (r0 == 0) goto L_0x0006
                return r1
            L_0x0006:
                r2 = 0
            L_0x0008:
                io.reactivex.functions.Consumer<? super T> r0 = r9.onNext     // Catch:{ all -> 0x0015 }
                r0.accept(r10)     // Catch:{ all -> 0x0015 }
                io.reactivex.internal.fuseable.ConditionalSubscriber<? super T> r0 = r9.actual
                boolean r0 = r0.tryOnNext(r10)
                return r0
            L_0x0015:
                r0 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r0)
                r4 = 1
                r5 = 2
                io.reactivex.functions.BiFunction<? super java.lang.Long, ? super java.lang.Throwable, io.reactivex.parallel.ParallelFailureHandling> r6 = r9.errorHandler     // Catch:{ all -> 0x0051 }
                r7 = 1
                long r7 = r7 + r2
                r2 = r7
                java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0051 }
                java.lang.Object r6 = r6.apply(r7, r0)     // Catch:{ all -> 0x0051 }
                java.lang.String r7 = "The errorHandler returned a null item"
                java.lang.Object r6 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r6, r7)     // Catch:{ all -> 0x0051 }
                io.reactivex.parallel.ParallelFailureHandling r6 = (io.reactivex.parallel.ParallelFailureHandling) r6     // Catch:{ all -> 0x0051 }
                int[] r7 = io.reactivex.internal.operators.parallel.ParallelDoOnNextTry.AnonymousClass1.$SwitchMap$io$reactivex$parallel$ParallelFailureHandling
                int r8 = r6.ordinal()
                r7 = r7[r8]
                if (r7 == r4) goto L_0x0050
                if (r7 == r5) goto L_0x004f
                r4 = 3
                if (r7 == r4) goto L_0x0048
                r9.cancel()
                r9.onError(r0)
                return r1
            L_0x0048:
                r9.cancel()
                r9.onComplete()
                return r1
            L_0x004f:
                return r1
            L_0x0050:
                goto L_0x0008
            L_0x0051:
                r6 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r6)
                r9.cancel()
                io.reactivex.exceptions.CompositeException r7 = new io.reactivex.exceptions.CompositeException
                java.lang.Throwable[] r5 = new java.lang.Throwable[r5]
                r5[r1] = r0
                r5[r4] = r6
                r7.<init>((java.lang.Throwable[]) r5)
                r9.onError(r7)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.parallel.ParallelDoOnNextTry.ParallelDoOnNextConditionalSubscriber.tryOnNext(java.lang.Object):boolean");
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.onError(t);
                return;
            }
            this.done = true;
            this.actual.onError(t);
        }

        public void onComplete() {
            if (!this.done) {
                this.done = true;
                this.actual.onComplete();
            }
        }
    }
}
