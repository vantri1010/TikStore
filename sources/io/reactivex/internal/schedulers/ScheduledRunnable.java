package io.reactivex.internal.schedulers;

import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableContainer;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class ScheduledRunnable extends AtomicReferenceArray<Object> implements Runnable, Callable<Object>, Disposable {
    static final Object DISPOSED = new Object();
    static final Object DONE = new Object();
    static final int FUTURE_INDEX = 1;
    static final int PARENT_INDEX = 0;
    static final int THREAD_INDEX = 2;
    private static final long serialVersionUID = -6120223772001106981L;
    final Runnable actual;

    public ScheduledRunnable(Runnable actual2, DisposableContainer parent) {
        super(3);
        this.actual = actual2;
        lazySet(0, parent);
    }

    public Object call() {
        run();
        return null;
    }

    public void run() {
        Object o;
        Object o2;
        lazySet(2, Thread.currentThread());
        try {
            this.actual.run();
        } catch (Throwable th) {
            lazySet(2, (Object) null);
            Object o3 = get(0);
            if (!(o3 == DISPOSED || o3 == null || !compareAndSet(0, o3, DONE))) {
                ((DisposableContainer) o3).delete(this);
            }
            do {
                o2 = get(1);
                if (o2 == DISPOSED || compareAndSet(1, o2, DONE)) {
                    throw th;
                }
                o2 = get(1);
                break;
            } while (compareAndSet(1, o2, DONE));
            throw th;
        }
        lazySet(2, (Object) null);
        Object o4 = get(0);
        if (!(o4 == DISPOSED || o4 == null || !compareAndSet(0, o4, DONE))) {
            ((DisposableContainer) o4).delete(this);
        }
        do {
            o = get(1);
            if (o == DISPOSED || compareAndSet(1, o, DONE)) {
            }
            o = get(1);
            return;
        } while (compareAndSet(1, o, DONE));
    }

    public void setFuture(Future<?> f) {
        Object o;
        do {
            boolean z = true;
            o = get(1);
            if (o != DONE) {
                if (o == DISPOSED) {
                    if (get(2) == Thread.currentThread()) {
                        z = false;
                    }
                    f.cancel(z);
                    return;
                }
            } else {
                return;
            }
        } while (!compareAndSet(1, o, f));
    }

    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispose() {
        /*
            r6 = this;
        L_0x0000:
            r0 = 1
            java.lang.Object r1 = r6.get(r0)
            java.lang.Object r2 = DONE
            r3 = 0
            if (r1 == r2) goto L_0x002c
            java.lang.Object r2 = DISPOSED
            if (r1 != r2) goto L_0x000f
            goto L_0x002c
        L_0x000f:
            boolean r2 = r6.compareAndSet(r0, r1, r2)
            if (r2 == 0) goto L_0x002b
            if (r1 == 0) goto L_0x002c
            r2 = r1
            java.util.concurrent.Future r2 = (java.util.concurrent.Future) r2
            r4 = 2
            java.lang.Object r4 = r6.get(r4)
            java.lang.Thread r5 = java.lang.Thread.currentThread()
            if (r4 == r5) goto L_0x0026
            goto L_0x0027
        L_0x0026:
            r0 = 0
        L_0x0027:
            r2.cancel(r0)
            goto L_0x002c
        L_0x002b:
            goto L_0x0000
        L_0x002c:
            java.lang.Object r0 = r6.get(r3)
            java.lang.Object r1 = DONE
            if (r0 == r1) goto L_0x0049
            java.lang.Object r1 = DISPOSED
            if (r0 == r1) goto L_0x0049
            if (r0 != 0) goto L_0x003b
            goto L_0x0049
        L_0x003b:
            boolean r1 = r6.compareAndSet(r3, r0, r1)
            if (r1 == 0) goto L_0x0048
            r1 = r0
            io.reactivex.internal.disposables.DisposableContainer r1 = (io.reactivex.internal.disposables.DisposableContainer) r1
            r1.delete(r6)
            return
        L_0x0048:
            goto L_0x002c
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.schedulers.ScheduledRunnable.dispose():void");
    }

    public boolean isDisposed() {
        Object o = get(1);
        if (o == DISPOSED || o == DONE) {
            return true;
        }
        return false;
    }
}
