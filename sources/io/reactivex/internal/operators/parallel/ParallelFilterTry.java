package io.reactivex.internal.operators.parallel;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.parallel.ParallelFailureHandling;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class ParallelFilterTry<T> extends ParallelFlowable<T> {
    final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
    final Predicate<? super T> predicate;
    final ParallelFlowable<T> source;

    public ParallelFilterTry(ParallelFlowable<T> source2, Predicate<? super T> predicate2, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler2) {
        this.source = source2;
        this.predicate = predicate2;
        this.errorHandler = errorHandler2;
    }

    public void subscribe(Subscriber<? super T>[] subscribers) {
        if (validate(subscribers)) {
            int n = subscribers.length;
            Subscriber<? super T>[] parents = new Subscriber[n];
            for (int i = 0; i < n; i++) {
                ConditionalSubscriber conditionalSubscriber = subscribers[i];
                if (conditionalSubscriber instanceof ConditionalSubscriber) {
                    parents[i] = new ParallelFilterConditionalSubscriber<>(conditionalSubscriber, this.predicate, this.errorHandler);
                } else {
                    parents[i] = new ParallelFilterSubscriber<>(conditionalSubscriber, this.predicate, this.errorHandler);
                }
            }
            this.source.subscribe(parents);
        }
    }

    public int parallelism() {
        return this.source.parallelism();
    }

    static abstract class BaseFilterSubscriber<T> implements ConditionalSubscriber<T>, Subscription {
        boolean done;
        final BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler;
        final Predicate<? super T> predicate;
        Subscription s;

        BaseFilterSubscriber(Predicate<? super T> predicate2, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler2) {
            this.predicate = predicate2;
            this.errorHandler = errorHandler2;
        }

        public final void request(long n) {
            this.s.request(n);
        }

        public final void cancel() {
            this.s.cancel();
        }

        public final void onNext(T t) {
            if (!tryOnNext(t) && !this.done) {
                this.s.request(1);
            }
        }
    }

    static final class ParallelFilterSubscriber<T> extends BaseFilterSubscriber<T> {
        final Subscriber<? super T> actual;

        ParallelFilterSubscriber(Subscriber<? super T> actual2, Predicate<? super T> predicate, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
            super(predicate, errorHandler);
            this.actual = actual2;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.s, s)) {
                this.s = s;
                this.actual.onSubscribe(this);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x0040  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x0051 A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean tryOnNext(T r10) {
            /*
                r9 = this;
                boolean r0 = r9.done
                r1 = 0
                if (r0 != 0) goto L_0x0069
                r2 = 0
            L_0x0007:
                r0 = 1
                io.reactivex.functions.Predicate r4 = r9.predicate     // Catch:{ all -> 0x0018 }
                boolean r4 = r4.test(r10)     // Catch:{ all -> 0x0018 }
                if (r4 == 0) goto L_0x0017
                org.reactivestreams.Subscriber<? super T> r1 = r9.actual
                r1.onNext(r10)
                return r0
            L_0x0017:
                return r1
            L_0x0018:
                r4 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r4)
                r5 = 2
                io.reactivex.functions.BiFunction r6 = r9.errorHandler     // Catch:{ all -> 0x0053 }
                r7 = 1
                long r7 = r7 + r2
                r2 = r7
                java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0053 }
                java.lang.Object r6 = r6.apply(r7, r4)     // Catch:{ all -> 0x0053 }
                java.lang.String r7 = "The errorHandler returned a null item"
                java.lang.Object r6 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r6, r7)     // Catch:{ all -> 0x0053 }
                io.reactivex.parallel.ParallelFailureHandling r6 = (io.reactivex.parallel.ParallelFailureHandling) r6     // Catch:{ all -> 0x0053 }
                int[] r7 = io.reactivex.internal.operators.parallel.ParallelFilterTry.AnonymousClass1.$SwitchMap$io$reactivex$parallel$ParallelFailureHandling
                int r8 = r6.ordinal()
                r7 = r7[r8]
                if (r7 == r0) goto L_0x0052
                if (r7 == r5) goto L_0x0051
                r0 = 3
                if (r7 == r0) goto L_0x004a
                r9.cancel()
                r9.onError(r4)
                return r1
            L_0x004a:
                r9.cancel()
                r9.onComplete()
                return r1
            L_0x0051:
                return r1
            L_0x0052:
                goto L_0x0007
            L_0x0053:
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
            L_0x0069:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.parallel.ParallelFilterTry.ParallelFilterSubscriber.tryOnNext(java.lang.Object):boolean");
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

    /* renamed from: io.reactivex.internal.operators.parallel.ParallelFilterTry$1  reason: invalid class name */
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

    static final class ParallelFilterConditionalSubscriber<T> extends BaseFilterSubscriber<T> {
        final ConditionalSubscriber<? super T> actual;

        ParallelFilterConditionalSubscriber(ConditionalSubscriber<? super T> actual2, Predicate<? super T> predicate, BiFunction<? super Long, ? super Throwable, ParallelFailureHandling> errorHandler) {
            super(predicate, errorHandler);
            this.actual = actual2;
        }

        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.s, s)) {
                this.s = s;
                this.actual.onSubscribe(this);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x0043  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x0054 A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean tryOnNext(T r10) {
            /*
                r9 = this;
                boolean r0 = r9.done
                r1 = 0
                if (r0 != 0) goto L_0x006c
                r2 = 0
            L_0x0007:
                r0 = 1
                io.reactivex.functions.Predicate r4 = r9.predicate     // Catch:{ all -> 0x001b }
                boolean r4 = r4.test(r10)     // Catch:{ all -> 0x001b }
                if (r4 == 0) goto L_0x001a
                io.reactivex.internal.fuseable.ConditionalSubscriber<? super T> r5 = r9.actual
                boolean r5 = r5.tryOnNext(r10)
                if (r5 == 0) goto L_0x001a
                r1 = 1
            L_0x001a:
                return r1
            L_0x001b:
                r4 = move-exception
                io.reactivex.exceptions.Exceptions.throwIfFatal(r4)
                r5 = 2
                io.reactivex.functions.BiFunction r6 = r9.errorHandler     // Catch:{ all -> 0x0056 }
                r7 = 1
                long r7 = r7 + r2
                r2 = r7
                java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0056 }
                java.lang.Object r6 = r6.apply(r7, r4)     // Catch:{ all -> 0x0056 }
                java.lang.String r7 = "The errorHandler returned a null item"
                java.lang.Object r6 = io.reactivex.internal.functions.ObjectHelper.requireNonNull(r6, r7)     // Catch:{ all -> 0x0056 }
                io.reactivex.parallel.ParallelFailureHandling r6 = (io.reactivex.parallel.ParallelFailureHandling) r6     // Catch:{ all -> 0x0056 }
                int[] r7 = io.reactivex.internal.operators.parallel.ParallelFilterTry.AnonymousClass1.$SwitchMap$io$reactivex$parallel$ParallelFailureHandling
                int r8 = r6.ordinal()
                r7 = r7[r8]
                if (r7 == r0) goto L_0x0055
                if (r7 == r5) goto L_0x0054
                r0 = 3
                if (r7 == r0) goto L_0x004d
                r9.cancel()
                r9.onError(r4)
                return r1
            L_0x004d:
                r9.cancel()
                r9.onComplete()
                return r1
            L_0x0054:
                return r1
            L_0x0055:
                goto L_0x0007
            L_0x0056:
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
            L_0x006c:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.parallel.ParallelFilterTry.ParallelFilterConditionalSubscriber.tryOnNext(java.lang.Object):boolean");
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
