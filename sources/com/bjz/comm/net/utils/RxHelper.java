package com.bjz.comm.net.utils;

import android.text.TextUtils;
import android.util.Log;
import com.android.tools.r8.annotations.SynthesizedClassMap;
import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.BResponseNoData;
import com.bjz.comm.net.exception.ApiException;
import com.bjz.comm.net.exception.KeyNotValidThrowable;
import com.bjz.comm.net.utils.RxHelper;
import com.google.gson.JsonSyntaxException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

@SynthesizedClassMap({$$Lambda$RxHelper$9Fpeevt_AEBx4Inh91E9OBxsjI.class, $$Lambda$RxHelper$L4h52ayeOJhEGoRaDCAiLt5cFSM.class, $$Lambda$RxHelper$TCkWU_RHRI6J4gvtltD_EDaVs0.class, $$Lambda$RxHelper$WbTnyinO42jR_Y9WXZuZpFouOpU.class, $$Lambda$RxHelper$YXMpiTgUkxP3dG6KMw4qhqDLQu0.class, $$Lambda$RxHelper$dShrgWvM2sG2Y2G_WXd9Fe4Io9I.class})
public class RxHelper {
    /* access modifiers changed from: private */
    public static String TAG = RxHelper.class.getSimpleName();
    private HashMap<String, CompositeDisposable> mTaskDisposable;

    private static class RxHelperHolder {
        /* access modifiers changed from: private */
        public static RxHelper instance = new RxHelper();

        private RxHelperHolder() {
        }
    }

    private RxHelper() {
        this.mTaskDisposable = new HashMap<>();
    }

    public static RxHelper getInstance() {
        return RxHelperHolder.instance;
    }

    private void addTaskDisposable(String tag, Disposable disposable) {
        if (this.mTaskDisposable.get(tag) != null) {
            this.mTaskDisposable.get(tag).add(disposable);
            return;
        }
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable);
        this.mTaskDisposable.put(tag, compositeDisposable);
    }

    public <T> void sendSimpleRequest(String tag, Observable<T> observable, Consumer<T> onNext, Consumer<Throwable> onError) {
        addTaskDisposable(tag, observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action(tag) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RxHelper.this.lambda$sendSimpleRequest$0$RxHelper(this.f$1);
            }
        }).subscribe(onNext, onError));
    }

    public <R> void sendRequest(String tag, Observable<BResponse<R>> observable, Consumer<BResponse<R>> onNext, Consumer<Throwable> onError) {
        addTaskDisposable(tag, send(observable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action(tag) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RxHelper.this.lambda$sendRequest$1$RxHelper(this.f$1);
            }
        }).subscribe(onNext, onError));
    }

    private <R> Observable send(Observable<R> observable) {
        return Observable.defer($$Lambda$RxHelper$YXMpiTgUkxP3dG6KMw4qhqDLQu0.INSTANCE).flatMap(new Function() {
            public final Object apply(Object obj) {
                return RxHelper.lambda$send$3(Observable.this, (String) obj);
            }
        }).retryWhen(new Function<Observable<? extends Throwable>, ObservableSource<?>>() {
            private int retryCount = 0;

            public ObservableSource<?> apply(Observable<? extends Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function() {
                    public final Object apply(Object obj) {
                        return RxHelper.AnonymousClass1.this.lambda$apply$0$RxHelper$1((Throwable) obj);
                    }
                });
            }

            public /* synthetic */ ObservableSource lambda$apply$0$RxHelper$1(Throwable throwable) throws Exception {
                if (BuildVars.DEBUG_VERSION) {
                    String access$200 = RxHelper.TAG;
                    Log.e(access$200, "TokenRequest retryCount = " + this.retryCount);
                }
                if (throwable instanceof KeyNotValidThrowable) {
                    if (BuildVars.DEBUG_VERSION) {
                        Log.e(RxHelper.TAG, "TokenRequest KeyNotValidThrowable");
                    }
                    int i = this.retryCount;
                    if (i > 0) {
                        return Observable.error(throwable);
                    }
                    this.retryCount = i + 1;
                    return TokenLoader.getInstance().getNetTokenLocked();
                }
                if (throwable instanceof ApiException) {
                    if (BuildVars.DEBUG_VERSION) {
                        Log.e(RxHelper.TAG, "TokenRequest ApiException");
                    }
                    if (((ApiException) throwable).getCode() == 400) {
                        int i2 = this.retryCount;
                        if (i2 > 0) {
                            return Observable.error(throwable);
                        }
                        this.retryCount = i2 + 1;
                        return TokenLoader.getInstance().getNetTokenLocked();
                    }
                }
                return Observable.error(throwable);
            }
        });
    }

    static /* synthetic */ ObservableSource lambda$send$3(Observable observable, String key) throws Exception {
        if (TextUtils.isEmpty(key)) {
            if (BuildVars.DEBUG_VERSION) {
                Log.e(TAG, "TokenRequest = null");
            }
            return Observable.error((Throwable) new KeyNotValidThrowable());
        }
        if (BuildVars.DEBUG_VERSION) {
            String str = TAG;
            Log.e(str, "TokenRequest = " + key);
        }
        return observable;
    }

    public void sendRequestNoData(String tag, Observable<BResponseNoData> observable, Consumer<BResponseNoData> onNext, Consumer<Throwable> onError) {
        addTaskDisposable(tag, send(observable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action(tag) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RxHelper.this.lambda$sendRequestNoData$4$RxHelper(this.f$1);
            }
        }).subscribe(onNext, onError));
    }

    public <R> void sendCommRequest(String tag, Observable<R> observable, Consumer<R> onNext, Consumer<Throwable> onError) {
        addTaskDisposable(tag, send(observable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action(tag) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RxHelper.this.lambda$sendCommRequest$5$RxHelper(this.f$1);
            }
        }).subscribe(onNext, onError));
    }

    /* renamed from: unSubscribeTask */
    public void lambda$sendSimpleRequest$0$RxHelper(String tag) {
        Disposable dis = this.mTaskDisposable.get(tag);
        if (dis != null) {
            if (!dis.isDisposed()) {
                dis.dispose();
            }
            this.mTaskDisposable.remove(tag);
        }
    }

    public String getErrorInfo(Throwable throwable) {
        if (BuildVars.DEBUG_VERSION) {
            Log.e("TAG", "" + throwable.getMessage());
        }
        if (throwable instanceof SocketTimeoutException) {
            return "请求超时";
        }
        if (throwable instanceof UnknownHostException) {
            return "网络异常";
        }
        if (throwable instanceof IOException) {
            return "服务器异常";
        }
        if (throwable instanceof JsonSyntaxException) {
            return "返回数据异常";
        }
        return "请求失败";
    }
}
