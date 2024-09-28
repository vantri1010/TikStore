package com.bjz.comm.net.mvp.model;

import com.android.tools.r8.annotations.SynthesizedClassMap;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.bjz.comm.net.bean.RespOthersFcListBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.utils.RxHelper;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

@SynthesizedClassMap({$$Lambda$77dzvAKl1g9CDlxEuR3k6XzTbI.class, $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s.class})
public class FcPageOthersModel implements BaseFcContract.IFcPageOthersModel {
    private static final String TAG = FcPageOthersModel.class.getSimpleName();

    public void getActionCount(long userId, DataListener<BResponse<RespFcUserStatisticsBean>> listener) {
        Observable<BResponse<RespFcUserStatisticsBean>> observable = ApiFactory.getInstance().getApiMomentForum().getActionCount(userId);
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r3 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendRequest(str, observable, r3, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void getFCList(int limit, long forumID, long userId, int roundNum, DataListener<BResponse<RespOthersFcListBean>> listener) {
        Observable<BResponse<RespOthersFcListBean>> observable = ApiFactory.getInstance().getApiMomentForum().getOthersFCList(limit, forumID, userId, roundNum);
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r3 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendRequest(str, observable, r3, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void checkIsFollowed(long followUID, DataListener<BResponse<Boolean>> listener) {
        Observable<BResponse<Boolean>> observable = ApiFactory.getInstance().getApiMomentForum().checkUserIsFollowed(followUID);
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r3 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendRequest(str, observable, r3, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void unSubscribeTask() {
        RxHelper.getInstance().lambda$sendSimpleRequest$0$RxHelper(TAG);
    }
}
