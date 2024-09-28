package io.reactivex.internal.operators.maybe;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MaybeAmb<T> extends Maybe<T> {
    private final MaybeSource<? extends T>[] sources;
    private final Iterable<? extends MaybeSource<? extends T>> sourcesIterable;

    public MaybeAmb(MaybeSource<? extends T>[] sources2, Iterable<? extends MaybeSource<? extends T>> sourcesIterable2) {
        this.sources = sources2;
        this.sourcesIterable = sourcesIterable2;
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(MaybeObserver<? super T> observer) {
        MaybeSource<? extends T>[] sources2 = this.sources;
        int count = 0;
        if (sources2 == null) {
            sources2 = new MaybeSource[8];
            try {
                for (MaybeSource<? extends T> element : this.sourcesIterable) {
                    if (element == null) {
                        EmptyDisposable.error((Throwable) new NullPointerException("One of the sources is null"), (MaybeObserver<?>) observer);
                        return;
                    }
                    if (count == sources2.length) {
                        MaybeSource<? extends T>[] b = new MaybeSource[((count >> 2) + count)];
                        System.arraycopy(sources2, 0, b, 0, count);
                        sources2 = b;
                    }
                    int count2 = count + 1;
                    try {
                        sources2[count] = element;
                        count = count2;
                    } catch (Throwable th) {
                        e = th;
                        int i = count2;
                        Exceptions.throwIfFatal(e);
                        EmptyDisposable.error(e, (MaybeObserver<?>) observer);
                        return;
                    }
                }
            } catch (Throwable th2) {
                e = th2;
                Exceptions.throwIfFatal(e);
                EmptyDisposable.error(e, (MaybeObserver<?>) observer);
                return;
            }
        } else {
            count = sources2.length;
        }
        AmbMaybeObserver<T> parent = new AmbMaybeObserver<>(observer);
        observer.onSubscribe(parent);
        int i2 = 0;
        while (i2 < count) {
            MaybeSource<? extends T> s = sources2[i2];
            if (!parent.isDisposed()) {
                if (s == null) {
                    parent.onError(new NullPointerException("One of the MaybeSources is null"));
                    return;
                } else {
                    s.subscribe(parent);
                    i2++;
                }
            } else {
                return;
            }
        }
        if (count == 0) {
            observer.onComplete();
        }
    }

    static final class AmbMaybeObserver<T> extends AtomicBoolean implements MaybeObserver<T>, Disposable {
        private static final long serialVersionUID = -7044685185359438206L;
        final MaybeObserver<? super T> actual;
        final CompositeDisposable set = new CompositeDisposable();

        AmbMaybeObserver(MaybeObserver<? super T> actual2) {
            this.actual = actual2;
        }

        public void dispose() {
            if (compareAndSet(false, true)) {
                this.set.dispose();
            }
        }

        public boolean isDisposed() {
            return get();
        }

        public void onSubscribe(Disposable d) {
            this.set.add(d);
        }

        public void onSuccess(T value) {
            if (compareAndSet(false, true)) {
                this.set.dispose();
                this.actual.onSuccess(value);
            }
        }

        public void onError(Throwable e) {
            if (compareAndSet(false, true)) {
                this.set.dispose();
                this.actual.onError(e);
                return;
            }
            RxJavaPlugins.onError(e);
        }

        public void onComplete() {
            if (compareAndSet(false, true)) {
                this.set.dispose();
                this.actual.onComplete();
            }
        }
    }
}
