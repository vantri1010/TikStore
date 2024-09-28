package com.bjz.comm.net.utils;

import android.util.Log;
import com.android.tools.r8.annotations.SynthesizedClassMap;
import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.listener.GetHttpTokenCallBack;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.atomic.AtomicBoolean;

@SynthesizedClassMap({$$Lambda$TokenLoader$TnbL4evdANTsmds_gf9tEbMrFU.class, $$Lambda$TokenLoader$XuXb2hd7gTRKNPKG0MG8keR3jhs.class, $$Lambda$TokenLoader$mWi2QK4WdmlLaShjrdx2VvnylAE.class})
public class TokenLoader {
    public static final String TAG = TokenLoader.class.getSimpleName();
    private GetHttpTokenCallBack callBack;
    private PublishSubject<String> mPublishSubject;
    private AtomicBoolean mRefreshing;
    private Observable<String> mTokenObservable;

    private TokenLoader() {
        this.mRefreshing = new AtomicBoolean(false);
        this.mTokenObservable = Observable.create(new ObservableOnSubscribe() {
            public final void subscribe(ObservableEmitter observableEmitter) {
                TokenLoader.this.lambda$new$0$TokenLoader(observableEmitter);
            }
        }).doOnNext(new Consumer() {
            public final void accept(Object obj) {
                TokenLoader.this.lambda$new$1$TokenLoader((String) obj);
            }
        }).doOnError(new Consumer() {
            public final void accept(Object obj) {
                TokenLoader.this.lambda$new$2$TokenLoader((Throwable) obj);
            }
        }).subscribeOn(Schedulers.io());
    }

    public /* synthetic */ void lambda$new$0$TokenLoader(ObservableEmitter e) throws Exception {
        GetHttpTokenCallBack getHttpTokenCallBack = this.callBack;
        if (getHttpTokenCallBack != null && e != null) {
            getHttpTokenCallBack.requestToken(e);
        }
    }

    public /* synthetic */ void lambda$new$1$TokenLoader(String token) throws Exception {
        if (BuildVars.DEBUG_VERSION) {
            String str = TAG;
            Log.d(str, "存储Token=" + token);
        }
        setCacheToken(token);
        this.mRefreshing.set(false);
    }

    public /* synthetic */ void lambda$new$2$TokenLoader(Throwable throwable) throws Exception {
        this.mRefreshing.set(false);
    }

    public static TokenLoader getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        /* access modifiers changed from: private */
        public static final TokenLoader INSTANCE = new TokenLoader();

        private Holder() {
        }
    }

    public String getCacheToken() {
        return HttpUtils.getInstance().getAuthorization();
    }

    public void setCacheToken(String key) {
        HttpUtils.getInstance().setAuthorization(key);
    }

    public Observable<String> getNetTokenLocked() {
        if (this.mRefreshing.compareAndSet(false, true)) {
            if (BuildVars.DEBUG_VERSION) {
                Log.e(TAG, "没有请求，发起一次新的Token请求");
            }
            startTokenRequest();
        } else if (BuildVars.DEBUG_VERSION) {
            Log.e(TAG, "已经有请求，直接返回等待");
        }
        return this.mPublishSubject;
    }

    private void startTokenRequest() {
        PublishSubject<String> create = PublishSubject.create();
        this.mPublishSubject = create;
        this.mTokenObservable.subscribe(create);
    }

    public void setCallBack(GetHttpTokenCallBack callBack2) {
        this.callBack = callBack2;
    }
}
