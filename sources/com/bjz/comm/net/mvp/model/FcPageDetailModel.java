package com.bjz.comm.net.mvp.model;

import com.android.tools.r8.annotations.SynthesizedClassMap;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.RespFcLikesBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespFcReplyBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.utils.RxHelper;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

@SynthesizedClassMap({$$Lambda$77dzvAKl1g9CDlxEuR3k6XzTbI.class, $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s.class})
public class FcPageDetailModel implements BaseFcContract.IFcPageDetailModel {
    private static final String TAG = FcPageDetailModel.class.getSimpleName();

    public void unSubscribeTask() {
        RxHelper.getInstance().lambda$sendSimpleRequest$0$RxHelper(TAG);
    }

    public void getDetail(long forumID, long forumUserId, DataListener<BResponse<RespFcListBean>> listener) {
        Observable<BResponse<RespFcListBean>> observable = ApiFactory.getInstance().getApiMomentForum().getDetail(forumID, forumUserId);
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

    public void getComments(long forumID, long commentId, long forumUserId, int limit, DataListener<BResponse<RespFcReplyBean>> listener) {
        DataListener<BResponse<RespFcReplyBean>> dataListener = listener;
        Observable<BResponse<RespFcReplyBean>> observable = ApiFactory.getInstance().getApiMomentForum().getComments(forumID, commentId, forumUserId, limit, 2);
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendRequest(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void getReplyList(FcReplyBean parentFcReplyBean, long commentId, int limit, DataListener<BResponse<RespFcReplyBean>> listener) {
        DataListener<BResponse<RespFcReplyBean>> dataListener = listener;
        Observable<BResponse<RespFcReplyBean>> observable = ApiFactory.getInstance().getApiMomentForum().getReplyList(parentFcReplyBean.getForumID(), commentId, parentFcReplyBean.getCommentID(), parentFcReplyBean.getCreateBy(), limit);
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendRequest(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void getLikeUserList(long forumId, long thumbId, int limit, DataListener<BResponse<RespFcLikesBean>> listener) {
        Observable<BResponse<RespFcLikesBean>> observable = ApiFactory.getInstance().getApiMomentForum().getLikeUserList(forumId, thumbId, 1, limit);
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
}
