package io.reactivex.subjects;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class SingleSubject<T> extends Single<T> implements SingleObserver<T> {
    static final SingleDisposable[] EMPTY = new SingleDisposable[0];
    static final SingleDisposable[] TERMINATED = new SingleDisposable[0];
    Throwable error;
    final AtomicReference<SingleDisposable<T>[]> observers = new AtomicReference<>(EMPTY);
    final AtomicBoolean once = new AtomicBoolean();
    T value;

    @CheckReturnValue
    public static <T> SingleSubject<T> create() {
        return new SingleSubject<>();
    }

    SingleSubject() {
    }

    public void onSubscribe(Disposable d) {
        if (this.observers.get() == TERMINATED) {
            d.dispose();
        }
    }

    public void onSuccess(T value2) {
        if (value2 == null) {
            onError(new NullPointerException("Null values are not allowed in 2.x"));
            return;
        }
        if (this.once.compareAndSet(false, true)) {
            this.value = value2;
            for (SingleDisposable<T> md : (SingleDisposable[]) this.observers.getAndSet(TERMINATED)) {
                md.actual.onSuccess(value2);
            }
        }
    }

    public void onError(Throwable e) {
        if (e == null) {
            e = new NullPointerException("Null errors are not allowed in 2.x");
        }
        if (this.once.compareAndSet(false, true)) {
            this.error = e;
            for (SingleDisposable<T> md : (SingleDisposable[]) this.observers.getAndSet(TERMINATED)) {
                md.actual.onError(e);
            }
            return;
        }
        RxJavaPlugins.onError(e);
    }

    /* access modifiers changed from: protected */
    public void subscribeActual(SingleObserver<? super T> observer) {
        SingleDisposable<T> md = new SingleDisposable<>(observer, this);
        observer.onSubscribe(md);
        if (!add(md)) {
            Throwable ex = this.error;
            if (ex != null) {
                observer.onError(ex);
            } else {
                observer.onSuccess(this.value);
            }
        } else if (md.isDisposed()) {
            remove(md);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean add(SingleDisposable<T> inner) {
        SingleDisposable<T>[] a;
        SingleDisposable<T>[] b;
        do {
            a = (SingleDisposable[]) this.observers.get();
            if (a == TERMINATED) {
                return false;
            }
            int n = a.length;
            b = new SingleDisposable[(n + 1)];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = inner;
        } while (!this.observers.compareAndSet(a, b));
        return true;
    }

    /* access modifiers changed from: package-private */
    public void remove(SingleDisposable<T> inner) {
        SingleDisposable<T>[] a;
        SingleDisposable<T>[] b;
        do {
            a = (SingleDisposable[]) this.observers.get();
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
                        SingleDisposable<T>[] b2 = new SingleDisposable[(n - 1)];
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

    public T getValue() {
        if (this.observers.get() == TERMINATED) {
            return this.value;
        }
        return null;
    }

    public boolean hasValue() {
        return this.observers.get() == TERMINATED && this.value != null;
    }

    public Throwable getThrowable() {
        if (this.observers.get() == TERMINATED) {
            return this.error;
        }
        return null;
    }

    public boolean hasThrowable() {
        return this.observers.get() == TERMINATED && this.error != null;
    }

    public boolean hasObservers() {
        return ((SingleDisposable[]) this.observers.get()).length != 0;
    }

    /* access modifiers changed from: package-private */
    public int observerCount() {
        return ((SingleDisposable[]) this.observers.get()).length;
    }

    static final class SingleDisposable<T> extends AtomicReference<SingleSubject<T>> implements Disposable {
        private static final long serialVersionUID = -7650903191002190468L;
        final SingleObserver<? super T> actual;

        SingleDisposable(SingleObserver<? super T> actual2, SingleSubject<T> parent) {
            this.actual = actual2;
            lazySet(parent);
        }

        public void dispose() {
            SingleSubject<T> parent = (SingleSubject) getAndSet((Object) null);
            if (parent != null) {
                parent.remove(this);
            }
        }

        public boolean isDisposed() {
            return get() == null;
        }
    }
}
