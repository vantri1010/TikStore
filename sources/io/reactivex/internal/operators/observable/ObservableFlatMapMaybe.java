package io.reactivex.internal.operators.observable;

import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.queue.SpscLinkedArrayQueue;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableFlatMapMaybe<T, R> extends AbstractObservableWithUpstream<T, R> {
    final boolean delayErrors;
    final Function<? super T, ? extends MaybeSource<? extends R>> mapper;

    public ObservableFlatMapMaybe(ObservableSource<T> source, Function<? super T, ? extends MaybeSource<? extends R>> mapper2, boolean delayError) {
        super(source);
        this.mapper = mapper2;
        this.delayErrors = delayError;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(Observer<? super R> s) {
        this.source.subscribe(new FlatMapMaybeObserver(s, this.mapper, this.delayErrors));
    }

    static final class FlatMapMaybeObserver<T, R> extends AtomicInteger implements Observer<T>, Disposable {
        private static final long serialVersionUID = 8600231336733376951L;
        final AtomicInteger active = new AtomicInteger(1);
        final Observer<? super R> actual;
        volatile boolean cancelled;
        Disposable d;
        final boolean delayErrors;
        final AtomicThrowable errors = new AtomicThrowable();
        final Function<? super T, ? extends MaybeSource<? extends R>> mapper;
        final AtomicReference<SpscLinkedArrayQueue<R>> queue = new AtomicReference<>();
        final CompositeDisposable set = new CompositeDisposable();

        FlatMapMaybeObserver(Observer<? super R> actual2, Function<? super T, ? extends MaybeSource<? extends R>> mapper2, boolean delayErrors2) {
            this.actual = actual2;
            this.mapper = mapper2;
            this.delayErrors = delayErrors2;
        }

        public void onSubscribe(Disposable d2) {
            if (DisposableHelper.validate(this.d, d2)) {
                this.d = d2;
                this.actual.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            try {
                MaybeSource<? extends R> ms = (MaybeSource) ObjectHelper.requireNonNull(this.mapper.apply(t), "The mapper returned a null MaybeSource");
                this.active.getAndIncrement();
                FlatMapMaybeObserver<T, R>.InnerObserver inner = new InnerObserver();
                if (!this.cancelled && this.set.add(inner)) {
                    ms.subscribe(inner);
                }
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                this.d.dispose();
                onError(ex);
            }
        }

        public void onError(Throwable t) {
            this.active.decrementAndGet();
            if (this.errors.addThrowable(t)) {
                if (!this.delayErrors) {
                    this.set.dispose();
                }
                drain();
                return;
            }
            RxJavaPlugins.onError(t);
        }

        public void onComplete() {
            this.active.decrementAndGet();
            drain();
        }

        public void dispose() {
            this.cancelled = true;
            this.d.dispose();
            this.set.dispose();
        }

        public boolean isDisposed() {
            return this.cancelled;
        }

        /* access modifiers changed from: package-private */
        public void innerSuccess(FlatMapMaybeObserver<T, R>.InnerObserver inner, R value) {
            this.set.delete(inner);
            if (get() == 0) {
                boolean d2 = true;
                if (compareAndSet(0, 1)) {
                    this.actual.onNext(value);
                    if (this.active.decrementAndGet() != 0) {
                        d2 = false;
                    }
                    SpscLinkedArrayQueue<R> q = this.queue.get();
                    if (!d2 || (q != null && !q.isEmpty())) {
                        if (decrementAndGet() == 0) {
                            return;
                        }
                        drainLoop();
                    }
                    Throwable ex = this.errors.terminate();
                    if (ex != null) {
                        this.actual.onError(ex);
                        return;
                    } else {
                        this.actual.onComplete();
                        return;
                    }
                }
            }
            SpscLinkedArrayQueue<R> q2 = getOrCreateQueue();
            synchronized (q2) {
                q2.offer(value);
            }
            this.active.decrementAndGet();
            if (getAndIncrement() != 0) {
                return;
            }
            drainLoop();
        }

        /* access modifiers changed from: package-private */
        public SpscLinkedArrayQueue<R> getOrCreateQueue() {
            SpscLinkedArrayQueue<R> current;
            do {
                SpscLinkedArrayQueue<R> current2 = this.queue.get();
                if (current2 != null) {
                    return current2;
                }
                current = new SpscLinkedArrayQueue<>(Observable.bufferSize());
            } while (!this.queue.compareAndSet((Object) null, current));
            return current;
        }

        /* access modifiers changed from: package-private */
        public void innerError(FlatMapMaybeObserver<T, R>.InnerObserver inner, Throwable e) {
            this.set.delete(inner);
            if (this.errors.addThrowable(e)) {
                if (!this.delayErrors) {
                    this.d.dispose();
                    this.set.dispose();
                }
                this.active.decrementAndGet();
                drain();
                return;
            }
            RxJavaPlugins.onError(e);
        }

        /* access modifiers changed from: package-private */
        public void innerComplete(FlatMapMaybeObserver<T, R>.InnerObserver inner) {
            this.set.delete(inner);
            if (get() == 0) {
                boolean d2 = true;
                if (compareAndSet(0, 1)) {
                    if (this.active.decrementAndGet() != 0) {
                        d2 = false;
                    }
                    SpscLinkedArrayQueue<R> q = this.queue.get();
                    if (d2 && (q == null || q.isEmpty())) {
                        Throwable ex = this.errors.terminate();
                        if (ex != null) {
                            this.actual.onError(ex);
                            return;
                        } else {
                            this.actual.onComplete();
                            return;
                        }
                    } else if (decrementAndGet() != 0) {
                        drainLoop();
                        return;
                    } else {
                        return;
                    }
                }
            }
            this.active.decrementAndGet();
            drain();
        }

        /* access modifiers changed from: package-private */
        public void drain() {
            if (getAndIncrement() == 0) {
                drainLoop();
            }
        }

        /* access modifiers changed from: package-private */
        public void clear() {
            SpscLinkedArrayQueue<R> q = this.queue.get();
            if (q != null) {
                q.clear();
            }
        }

        /* access modifiers changed from: package-private */
        public void drainLoop() {
            int missed = 1;
            Observer<? super R> a = this.actual;
            AtomicInteger n = this.active;
            AtomicReference<SpscLinkedArrayQueue<R>> qr = this.queue;
            while (!this.cancelled) {
                if (this.delayErrors || ((Throwable) this.errors.get()) == null) {
                    boolean empty = true;
                    boolean d2 = n.get() == 0;
                    SpscLinkedArrayQueue<R> q = qr.get();
                    R v = q != null ? q.poll() : null;
                    if (v != null) {
                        empty = false;
                    }
                    if (d2 && empty) {
                        Throwable ex = this.errors.terminate();
                        if (ex != null) {
                            a.onError(ex);
                            return;
                        } else {
                            a.onComplete();
                            return;
                        }
                    } else if (empty) {
                        missed = addAndGet(-missed);
                        if (missed == 0) {
                            return;
                        }
                    } else {
                        a.onNext(v);
                    }
                } else {
                    Throwable ex2 = this.errors.terminate();
                    clear();
                    a.onError(ex2);
                    return;
                }
            }
            clear();
        }

        final class InnerObserver extends AtomicReference<Disposable> implements MaybeObserver<R>, Disposable {
            private static final long serialVersionUID = -502562646270949838L;

            InnerObserver() {
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            public void onSuccess(R value) {
                FlatMapMaybeObserver.this.innerSuccess(this, value);
            }

            public void onError(Throwable e) {
                FlatMapMaybeObserver.this.innerError(this, e);
            }

            public void onComplete() {
                FlatMapMaybeObserver.this.innerComplete(this);
            }

            public boolean isDisposed() {
                return DisposableHelper.isDisposed((Disposable) get());
            }

            public void dispose() {
                DisposableHelper.dispose(this);
            }
        }
    }
}
