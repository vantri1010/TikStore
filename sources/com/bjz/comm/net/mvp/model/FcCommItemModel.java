package com.bjz.comm.net.mvp.model;

import com.android.tools.r8.annotations.SynthesizedClassMap;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.BResponseNoData;
import com.bjz.comm.net.bean.FcBgBean;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.utils.JsonCreateUtils;
import com.bjz.comm.net.utils.RxHelper;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;

@SynthesizedClassMap({$$Lambda$77dzvAKl1g9CDlxEuR3k6XzTbI.class, $$Lambda$Q4eiTxgqNLBS9F1srtGjxo_XKCo.class, $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s.class})
public class FcCommItemModel implements BaseFcContract.IFcCommItemModel {
    private static final String TAG = FcCommItemModel.class.getSimpleName();

    public void getFcBackgroundUrl(long userId, DataListener<BResponse<FcBgBean>> listener) {
        Observable<BResponse<FcBgBean>> observable = ApiFactory.getInstance().getApiMomentForum().getFCBackground(userId);
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

    public void doFollow(long followUID, DataListener<BResponseNoData> listener) {
        Observable<BResponseNoData> observable = ApiFactory.getInstance().getApiMomentForum().doFollow(JsonCreateUtils.build().addParam("FollowUID", Long.valueOf(followUID)).getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$Q4eiTxgqNLBS9F1srtGjxo_XKCo r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponseNoData) obj);
            }
        };
        listener.getClass();
        instance.sendRequestNoData(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void doCancelFollowed(long followUID, DataListener<BResponseNoData> listener) {
        Observable<BResponseNoData> observable = ApiFactory.getInstance().getApiMomentForum().doCancelFollowed(JsonCreateUtils.build().addParam("FollowUID", Long.valueOf(followUID)).getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$Q4eiTxgqNLBS9F1srtGjxo_XKCo r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponseNoData) obj);
            }
        };
        listener.getClass();
        instance.sendRequestNoData(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void doLike(long forumID, long forumUID, long commentID, long commentUID, DataListener<BResponse<FcLikeBean>> listener) {
        DataListener<BResponse<FcLikeBean>> dataListener = listener;
        JsonCreateUtils.MapForJsonObject build = JsonCreateUtils.build();
        build.addParam("ForumID", Long.valueOf(forumID)).addParam("UpDown", 1);
        if (!(commentID == -1 || commentUID == -1)) {
            build.addParam("CommentID", Long.valueOf(commentID));
        }
        Observable<BResponse<FcLikeBean>> observable = ApiFactory.getInstance().getApiMomentForum().doLike(build.getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r6 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendRequest(str, observable, r6, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void doCancelLike(long forumID, long forumUID, long commentID, long commentUID, DataListener<BResponse<FcLikeBean>> listener) {
        DataListener<BResponse<FcLikeBean>> dataListener = listener;
        JsonCreateUtils.MapForJsonObject build = JsonCreateUtils.build();
        build.addParam("ForumID", Long.valueOf(forumID));
        build.addParam("ForumUID", Long.valueOf(forumUID));
        if (!(commentID == -1 || commentUID == -1)) {
            build.addParam("CommentID", Long.valueOf(commentID));
            build.addParam("CommentUID", Long.valueOf(commentUID));
        }
        Observable<BResponse<FcLikeBean>> observable = ApiFactory.getInstance().getApiMomentForum().doCancelLike(build.getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r6 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendRequest(str, observable, r6, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void doDeleteItem(long forumID, DataListener<BResponseNoData> listener) {
        Observable<BResponseNoData> observable = ApiFactory.getInstance().getApiMomentForum().doDeleteItem(JsonCreateUtils.build().addParam("ForumID", Long.valueOf(forumID)).getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$Q4eiTxgqNLBS9F1srtGjxo_XKCo r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponseNoData) obj);
            }
        };
        listener.getClass();
        instance.sendRequestNoData(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void doIgnoreItem(long forumID, DataListener<BResponseNoData> listener) {
        Observable<BResponseNoData> observable = ApiFactory.getInstance().getApiMomentForum().doIgnoreItem(JsonCreateUtils.build().addParam("ForumID", Long.valueOf(forumID)).getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$Q4eiTxgqNLBS9F1srtGjxo_XKCo r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponseNoData) obj);
            }
        };
        listener.getClass();
        instance.sendRequestNoData(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void doSetItemPermission(long forumID, int permission, DataListener<BResponseNoData> listener) {
        Observable<BResponseNoData> observable = ApiFactory.getInstance().getApiMomentForum().doSetItemPermission(JsonCreateUtils.build().addParam("ForumID", Long.valueOf(forumID)).addParam("Permission", Integer.valueOf(permission)).getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$Q4eiTxgqNLBS9F1srtGjxo_XKCo r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponseNoData) obj);
            }
        };
        listener.getClass();
        instance.sendRequestNoData(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void doAddIgnoreUser(ArrayList<FcIgnoreUserBean> ignores, DataListener<BResponse<ArrayList<FcIgnoreUserBean>>> listener) {
        Observable<BResponse<ArrayList<FcIgnoreUserBean>>> observable = ApiFactory.getInstance().getApiMomentForum().doAddIgnoreUser(JsonCreateUtils.build().addParam("Ignores", ignores).getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendCommRequest(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void doDeleteIgnoreUser(ArrayList<FcIgnoreUserBean> ignores, DataListener<BResponse<ArrayList<FcIgnoreUserBean>>> listener) {
        Observable<BResponse<ArrayList<FcIgnoreUserBean>>> observable = ApiFactory.getInstance().getApiMomentForum().doDeleteIgnoreUser(JsonCreateUtils.build().addParam("Ignores", ignores).getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendCommRequest(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void doReply(RequestReplyFcBean bean, DataListener<BResponse<FcReplyBean>> listener) {
        Observable<BResponse<FcReplyBean>> observable = ApiFactory.getInstance().getApiMomentForum().replyForum(JsonCreateUtils.build().addParam("ForumID", Long.valueOf(bean.getForumID())).addParam("ForumUser", Long.valueOf(bean.getForumUser())).addParam("SupID", Long.valueOf(bean.getSupID())).addParam("SupUser", Long.valueOf(bean.getSupUser())).addParam("ReplayID", Long.valueOf(bean.getReplayID())).addParam("ReplayUID", Long.valueOf(bean.getReplayUID())).addParam("Content", bean.getContent()).addParam("Entitys", bean.getEntitys()).getHttpBody());
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

    public void doDeleteComment(long commentID, long forumID, long forumUser, DataListener<BResponseNoData> listener) {
        Observable<BResponseNoData> observable = ApiFactory.getInstance().getApiMomentForum().deleteReplyForum(JsonCreateUtils.build().addParam("CommentID", Long.valueOf(commentID)).addParam("ForumID", Long.valueOf(forumID)).addParam("ForumUser", Long.valueOf(forumUser)).getHttpBody());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$Q4eiTxgqNLBS9F1srtGjxo_XKCo r4 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponseNoData) obj);
            }
        };
        listener.getClass();
        instance.sendRequestNoData(str, observable, r4, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void unSubscribeTask() {
        RxHelper.getInstance().lambda$sendSimpleRequest$0$RxHelper(TAG);
    }
}
