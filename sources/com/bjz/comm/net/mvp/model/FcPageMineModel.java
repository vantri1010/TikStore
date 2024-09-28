package com.bjz.comm.net.mvp.model;

import com.android.tools.r8.annotations.SynthesizedClassMap;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.BResponseNoData;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.utils.JsonCreateUtils;
import com.bjz.comm.net.utils.RxHelper;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;

@SynthesizedClassMap({$$Lambda$77dzvAKl1g9CDlxEuR3k6XzTbI.class, $$Lambda$Q4eiTxgqNLBS9F1srtGjxo_XKCo.class, $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s.class})
public class FcPageMineModel implements BaseFcContract.IFcPageMineModel {
    private static final String TAG = FcPageMineModel.class.getSimpleName();

    public void getFCList(int limit, long forumID, DataListener<BResponse<ArrayList<RespFcListBean>>> listener) {
        Observable<BResponse<ArrayList<RespFcListBean>>> observable = ApiFactory.getInstance().getApiMomentForum().getMyFCList(limit, forumID);
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

    public void setFcBackground(String homeBackground, DataListener<BResponseNoData> listener) {
        Observable<BResponseNoData> observable = ApiFactory.getInstance().getApiMomentForum().setFcBackground(JsonCreateUtils.build().addParam("HomeBackground", homeBackground).getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$Q4eiTxgqNLBS9F1srtGjxo_XKCo r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponseNoData) obj);
            }
        };
        listener.getClass();
        instance.sendCommRequest(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void unSubscribeTask() {
        RxHelper.getInstance().lambda$sendSimpleRequest$0$RxHelper(TAG);
    }
}
