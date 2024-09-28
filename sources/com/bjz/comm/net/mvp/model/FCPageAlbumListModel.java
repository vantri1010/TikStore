package com.bjz.comm.net.mvp.model;

import com.android.tools.r8.annotations.SynthesizedClassMap;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.RespFcAlbumListBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.utils.RxHelper;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;

@SynthesizedClassMap({$$Lambda$77dzvAKl1g9CDlxEuR3k6XzTbI.class, $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s.class})
public class FCPageAlbumListModel implements BaseFcContract.IFcPageAlbumListModel {
    private static final String TAG = FCPageAlbumListModel.class.getSimpleName();

    public void getAlbumList(int userId, long id, int limit, DataListener<BResponse<ArrayList<RespFcAlbumListBean>>> listener) {
        Observable<BResponse<ArrayList<RespFcAlbumListBean>>> observable = ApiFactory.getInstance().getApiMomentForum().getFcAlbumList(userId, id, limit);
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
