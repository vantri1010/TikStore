package io.reactivex.internal.subscribers;

import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.FullArbiter;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import org.reactivestreams.Subscription;

public final class FullArbiterSubscriber<T> implements FlowableSubscriber<T> {
    final FullArbiter<T> arbiter;
    Subscription s;

    public FullArbiterSubscriber(FullArbiter<T> arbiter2) {
        this.arbiter = arbiter2;
    }

    public void onSubscribe(Subscription s2) {
        if (SubscriptionHelper.validate(this.s, s2)) {
            this.s = s2;
            this.arbiter.setSubscription(s2);
        }
    }

    public void onNext(T t) {
        this.arbiter.onNext(t, this.s);
    }

    public void onError(Throwable t) {
        this.arbiter.onError(t, this.s);
    }

    public void onComplete() {
        this.arbiter.onComplete(this.s);
    }
}
