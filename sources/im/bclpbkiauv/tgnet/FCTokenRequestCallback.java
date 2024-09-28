package im.bclpbkiauv.tgnet;

import android.text.TextUtils;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.TokenRequest;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.listener.GetHttpTokenCallBack;
import com.google.gson.Gson;
import com.socks.library.KLog;
import im.bclpbkiauv.javaBean.AllTokenResponse;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.FCTokenRequestCallback;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCBasic;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class FCTokenRequestCallback implements GetHttpTokenCallBack {
    /* access modifiers changed from: private */
    public static final String TAG = FCTokenRequestCallback.class.getSimpleName();
    private Disposable disposable = null;

    /* renamed from: im  reason: collision with root package name */
    private boolean f28im = true;

    public static FCTokenRequestCallback getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        /* access modifiers changed from: private */
        public static final FCTokenRequestCallback INSTANCE = new FCTokenRequestCallback();

        private Holder() {
        }
    }

    public void requestToken(ObservableEmitter<String> ef) {
        KLog.e("TAG", "获取Token start");
        if (this.f28im) {
            Disposable disposable2 = this.disposable;
            if (disposable2 != null && !disposable2.isDisposed()) {
                this.disposable.dispose();
                this.disposable = null;
            }
            this.disposable = Observable.create(new ObservableOnSubscribe<String>() {
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    TLRPCBasic.TL_GetToken req = new TLRPCBasic.TL_GetToken();
                    req.friendCircle = true;
                    AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().sendRequest(req, new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FCTokenRequestCallback.AnonymousClass2.lambda$subscribe$0(ObservableEmitter.this, tLObject, tL_error);
                        }
                    });
                }

                static /* synthetic */ void lambda$subscribe$0(ObservableEmitter e, TLObject response, TLRPC.TL_error error) {
                    if (response instanceof TLRPCBasic.TL_AllToken) {
                        TLRPC.TL_dataJSON tokens = ((TLRPCBasic.TL_AllToken) response).tokens;
                        String str = "获取Token失败";
                        if (tokens != null) {
                            String data = tokens.data;
                            if (!TextUtils.isEmpty(data)) {
                                try {
                                    AllTokenResponse tokenResponse = (AllTokenResponse) new Gson().fromJson(data, AllTokenResponse.class);
                                    if (tokenResponse != null) {
                                        String momenttoken = tokenResponse.getMomenttoken();
                                        String access$200 = FCTokenRequestCallback.TAG;
                                        KLog.e(access$200, "获取朋友圈Token == " + momenttoken);
                                        e.onNext(momenttoken);
                                    }
                                } catch (Exception exception) {
                                    String access$2002 = FCTokenRequestCallback.TAG;
                                    KLog.e(access$2002, "获取朋友圈Token error == " + exception.getMessage());
                                    e.onError(exception);
                                }
                            } else {
                                KLog.e(FCTokenRequestCallback.TAG, "获取朋友圈Token data == null ");
                                e.onError(new Throwable(str));
                            }
                        } else {
                            String access$2003 = FCTokenRequestCallback.TAG;
                            KLog.e(access$2003, "获取朋友圈Token error == " + error.text);
                            if (error.text != null) {
                                str = error.text;
                            }
                            e.onError(new Throwable(str));
                        }
                    } else {
                        String access$2004 = FCTokenRequestCallback.TAG;
                        KLog.e(access$2004, "获取朋友圈Token error == " + error.text);
                    }
                }
            }).timeout(5, TimeUnit.SECONDS).retryWhen(new Function<Observable<Throwable>, ObservableSource<String>>() {
                /* access modifiers changed from: private */
                public int retryCount = 0;

                static /* synthetic */ int access$108(AnonymousClass1 x0) {
                    int i = x0.retryCount;
                    x0.retryCount = i + 1;
                    return i;
                }

                public ObservableSource<String> apply(Observable<Throwable> throwableObservable) throws Exception {
                    return throwableObservable.flatMap(new Function<Throwable, ObservableSource<String>>() {
                        public ObservableSource<String> apply(Throwable throwable) throws Exception {
                            AnonymousClass1.access$108(AnonymousClass1.this);
                            String access$200 = FCTokenRequestCallback.TAG;
                            KLog.e(access$200, "获取朋友圈Token retryThrowable = " + throwable.getMessage());
                            String access$2002 = FCTokenRequestCallback.TAG;
                            KLog.e(access$2002, "获取朋友圈Token retryCount = " + AnonymousClass1.this.retryCount);
                            if (AnonymousClass1.this.retryCount == 3) {
                                return Observable.just("");
                            }
                            if (AnonymousClass1.this.retryCount > 3) {
                                return Observable.error(throwable);
                            }
                            return Observable.just("");
                        }
                    });
                }
            }).subscribeOn(Schedulers.io()).subscribe(new Consumer(ef) {
                private final /* synthetic */ ObservableEmitter f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    FCTokenRequestCallback.this.lambda$requestToken$0$FCTokenRequestCallback(this.f$1, (String) obj);
                }
            }, new Consumer(ef) {
                private final /* synthetic */ ObservableEmitter f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    FCTokenRequestCallback.this.lambda$requestToken$1$FCTokenRequestCallback(this.f$1, (Throwable) obj);
                }
            });
            return;
        }
        ApiFactory.getInstance().getApiMomentForum().getToken(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()).subscribeOn(Schedulers.io()).subscribe(new Consumer() {
            public final void accept(Object obj) {
                FCTokenRequestCallback.lambda$requestToken$2(ObservableEmitter.this, (BResponse) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                ObservableEmitter.this.onError((Throwable) obj);
            }
        });
    }

    public /* synthetic */ void lambda$requestToken$0$FCTokenRequestCallback(ObservableEmitter ef, String o) throws Exception {
        Disposable disposable2 = this.disposable;
        if (disposable2 != null && !disposable2.isDisposed()) {
            this.disposable.dispose();
            this.disposable = null;
        }
        ef.onNext(o);
    }

    public /* synthetic */ void lambda$requestToken$1$FCTokenRequestCallback(ObservableEmitter ef, Throwable throwable) throws Exception {
        Disposable disposable2 = this.disposable;
        if (disposable2 != null && !disposable2.isDisposed()) {
            this.disposable.dispose();
            this.disposable = null;
        }
        ef.onError(throwable);
    }

    static /* synthetic */ void lambda$requestToken$2(ObservableEmitter ef, BResponse tokenRequestBResponse) throws Exception {
        if (!tokenRequestBResponse.isState() || tokenRequestBResponse.Data == null) {
            ef.onError(new Throwable("获取token失败"));
        } else {
            ef.onNext(((TokenRequest) tokenRequestBResponse.Data).getToken());
        }
    }
}
