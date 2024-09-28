package io.reactivex.internal.operators.parallel;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.parallel.ParallelFailureHandling;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class ParallelMapTry<T, R> extends ParallelFlowable<R> {
    final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
    final Function<? super T, ? extends R> mapper;
    final ParallelFlowable<T> source;

    public ParallelMapTry(ParallelFlowable<T> source2, Function<? super T, ? extends R> mapper2, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler2) {
        this.source = source2;
        this.mapper = mapper2;
        this.errorHandler = errorHandler2;
    }

    public void subscribe(Subscriber<? super R>[] subscribers) {
        if (validate(subscribers)) {
            int n = subscribers.length;
            Subscriber<? super T>[] parents = new Subscriber[n];
            for (int i = 0; i < n; i++) {
                ConditionalSubscriber conditionalSubscriber = subscribers[i];
                if (conditionalSubscriber instanceof ConditionalSubscriber) {
                    parents[i] = new ParallelMapTryConditionalSubscriber<>(conditionalSubscriber, this.mapper, this.errorHandler);
                } else {
                    parents[i] = new ParallelMapTrySubscriber<>(conditionalSubscriber, this.mapper, this.errorHandler);
                }
            }
            this.source.subscribe(parents);
        }
    }

    public int parallelism() {
        return this.source.parallelism();
    }

    static final class ParallelMapTrySubscriber<T, R> implements ConditionalSubscriber<T>, Subscription {
        final Subscriber<? super R> actual;
        boolean done;
        final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
        final Function<? super T, ? extends R> mapper;
        Subscription s;

        ParallelMapTrySubscriber(Subscriber<? super R> actual2, Function<? super T, ? extends R> mapper2, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler2) {
            this.actual = actual2;
            this.mapper = mapper2;
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

        /* JADX WARNING: Removed duplicated region for block: B:16:0x0044  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0055 A[RETURN] */
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
                io.reactivex.functions.Function<? super T, ? extends R> r4 = r9.mapper     // Catch:{ all -> 0x001c }
                java.lang.Object r4 = r4.apply(r10)     // Catch:{ all -> 0x001c }
                java.lang.String r5 = "The mapper returned a null value"
                java.lang.Object r1 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r4, r5)     // Catch:{ all -> 0x001c }
                org.reactivestreams.Subscriber<? super R> r4 = r9.actual
                r4.onNext(r1)
                return r0
            L_0x001c:
                r4 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r4)
                r5 = 2
                io.reactivex.functions.BiFunction<? super java.lang.Long, ? super java.lang.Throwable, io.reactivex.parallel.ParallelFailureHandling> r6 = r9.errorHandler     // Catch:{ all -> 0x0057 }
                r7 = 1
                long r7 = r7 + r2
                r2 = r7
                java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0057 }
                java.lang.Object r6 = r6.apply(r7, r4)     // Catch:{ all -> 0x0057 }
                java.lang.String r7 = "The errorHandler returned a null item"
                java.lang.Object r6 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r6, r7)     // Catch:{ all -> 0x0057 }
                io.reactivex.parallel.ParallelFailureHandling r6 = (io.reactivex.parallel.ParallelFailureHandling) r6     // Catch:{ all -> 0x0057 }
                int[] r7 = io.reactivex.internal.operators.parallel.ParallelMapTry.AnonymousClass1.$SwitchMap$io$reactivex$parallel$ParallelFailureHandling
                int r8 = r6.ordinal()
                r7 = r7[r8]
                if (r7 == r0) goto L_0x0056
                if (r7 == r5) goto L_0x0055
                r0 = 3
                if (r7 == r0) goto L_0x004e
                r9.cancel()
                r9.onError(r4)
                return r1
            L_0x004e:
                r9.cancel()
                r9.onComplete()
                return r1
            L_0x0055:
                return r1
            L_0x0056:
                goto L_0x0008
            L_0x0057:
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
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.parallel.ParallelMapTry.ParallelMapTrySubscriber.tryOnNext(java.lang.Object):boolean");
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

    /* renamed from: io.reactivex.internal.operators.parallel.ParallelMapTry$1  reason: invalid class name */
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

    static final class ParallelMapTryConditionalSubscriber<T, R> implements ConditionalSubscriber<T>, Subscription {
        final ConditionalSubscriber<? super R> actual;
        boolean done;
        final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
        final Function<? super T, ? extends R> mapper;
        Subscription s;

        ParallelMapTryConditionalSubscriber(ConditionalSubscriber<? super R> actual2, Function<? super T, ? extends R> mapper2, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler2) {
            this.actual = actual2;
            this.mapper = mapper2;
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

        /* JADX WARNING: Removed duplicated region for block: B:15:0x0045  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0056 A[RETURN] */
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
                io.reactivex.functions.Function<? super T, ? extends R> r0 = r9.mapper     // Catch:{ all -> 0x001c }
                java.lang.Object r0 = r0.apply(r10)     // Catch:{ all -> 0x001c }
                java.lang.String r4 = "The mapper returned a null value"
                java.lang.Object r0 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r0, r4)     // Catch:{ all -> 0x001c }
                io.reactivex.internal.fuseable.ConditionalSubscriber<? super R> r1 = r9.actual
                boolean r1 = r1.tryOnNext(r0)
                return r1
            L_0x001c:
                r0 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r0)
                r4 = 1
                r5 = 2
                io.reactivex.functions.BiFunction<? super java.lang.Long, ? super java.lang.Throwable, io.reactivex.parallel.ParallelFailureHandling> r6 = r9.errorHandler     // Catch:{ all -> 0x0058 }
                r7 = 1
                long r7 = r7 + r2
                r2 = r7
                java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0058 }
                java.lang.Object r6 = r6.apply(r7, r0)     // Catch:{ all -> 0x0058 }
                java.lang.String r7 = "The errorHandler returned a null item"
                java.lang.Object r6 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r6, r7)     // Catch:{ all -> 0x0058 }
                io.reactivex.parallel.ParallelFailureHandling r6 = (io.reactivex.parallel.ParallelFailureHandling) r6     // Catch:{ all -> 0x0058 }
                int[] r7 = io.reactivex.internal.operators.parallel.ParallelMapTry.AnonymousClass1.$SwitchMap$io$reactivex$parallel$ParallelFailureHandling
                int r8 = r6.ordinal()
                r7 = r7[r8]
                if (r7 == r4) goto L_0x0057
                if (r7 == r5) goto L_0x0056
                r4 = 3
                if (r7 == r4) goto L_0x004f
                r9.cancel()
                r9.onError(r0)
                return r1
            L_0x004f:
                r9.cancel()
                r9.onComplete()
                return r1
            L_0x0056:
                return r1
            L_0x0057:
                goto L_0x0008
            L_0x0058:
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
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.parallel.ParallelMapTry.ParallelMapTryConditionalSubscriber.tryOnNext(java.lang.Object):boolean");
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
