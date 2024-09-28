package com.bjz.comm.net.mvp.presenter;

import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.BResponseNoData;
import com.bjz.comm.net.bean.FcBgBean;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.model.FcCommItemModel;
import com.bjz.comm.net.utils.RxHelper;
import java.util.ArrayList;

public class FcCommItemPresenter implements BaseFcContract.IFcCommItemPresenter {
    private String TAG = FcCommItemPresenter.class.getSimpleName();
    /* access modifiers changed from: private */
    public BaseFcContract.IFcCommItemView mView;
    private final FcCommItemModel model;

    public FcCommItemPresenter(BaseFcContract.IFcCommItemView view) {
        this.mView = view;
        this.model = new FcCommItemModel();
    }

    public void getFCBackground(long userId) {
        this.model.getFcBackgroundUrl(userId, new DataListener<BResponse<FcBgBean>>() {
            public void onResponse(BResponse<FcBgBean> result) {
                if (result != null && result.isState()) {
                    FcCommItemPresenter.this.mView.getFcBackgroundSucc((FcBgBean) result.Data);
                }
            }

            public void onError(Throwable throwable) {
            }
        });
    }

    public void doFollow(final long followUID, final int position) {
        this.model.doFollow(followUID, new DataListener<BResponseNoData>() {
            public void onResponse(BResponseNoData result) {
                if (result == null) {
                    FcCommItemPresenter.this.mView.doFollowFailed((String) null);
                } else if (result.isState()) {
                    FcCommItemPresenter.this.mView.doFollowSucc(followUID, position, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.doFollowFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.doFollowFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doCancelFollowed(final long followUID, final int position) {
        this.model.doCancelFollowed(followUID, new DataListener<BResponseNoData>() {
            public void onResponse(BResponseNoData result) {
                if (result == null) {
                    FcCommItemPresenter.this.mView.doCancelFollowedFailed((String) null);
                } else if (result.isState()) {
                    FcCommItemPresenter.this.mView.doCancelFollowedSucc(followUID, position, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.doCancelFollowedFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.doCancelFollowedFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doLike(long forumID, long forumUID, long commentID, long commentUID, int position) {
        final long j = forumID;
        final int i = position;
        this.model.doLike(forumID, forumUID, commentID, commentUID, new DataListener<BResponse<FcLikeBean>>() {
            public void onResponse(BResponse<FcLikeBean> result) {
                if (result == null) {
                    FcCommItemPresenter.this.mView.doLikeFailed(i, (String) null);
                } else if (result.isState()) {
                    FcCommItemPresenter.this.mView.doLikeSucc((FcLikeBean) result.Data, j, i, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.doLikeFailed(i, result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.doLikeFailed(i, RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doCancelLike(long forumID, long forumUID, long commentID, long commentUID, int position) {
        final long j = forumID;
        final int i = position;
        this.model.doCancelLike(forumID, forumUID, commentID, commentUID, new DataListener<BResponse<FcLikeBean>>() {
            public void onResponse(BResponse<FcLikeBean> result) {
                if (result == null) {
                    FcCommItemPresenter.this.mView.doCancelLikeFailed(i, (String) null);
                } else if (result.isState()) {
                    FcCommItemPresenter.this.mView.doCancelLikeSucc((FcLikeBean) result.Data, j, i, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.doCancelLikeFailed(i, result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.doCancelLikeFailed(i, RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doDeleteItem(final long forumID, final int position) {
        this.model.doDeleteItem(forumID, new DataListener<BResponseNoData>() {
            public void onResponse(BResponseNoData result) {
                if (result == null) {
                    FcCommItemPresenter.this.mView.doDeleteItemFailed((String) null);
                } else if (result.isState()) {
                    FcCommItemPresenter.this.mView.doDeleteItemSucc(forumID, position, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.doDeleteItemFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.doDeleteItemFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doIgnoreItem(final long forumID, final int position) {
        this.model.doIgnoreItem(forumID, new DataListener<BResponseNoData>() {
            public void onResponse(BResponseNoData result) {
                if (result == null) {
                    FcCommItemPresenter.this.mView.doDeleteItemFailed((String) null);
                } else if (result.isState()) {
                    FcCommItemPresenter.this.mView.doIgnoreItemSucc(forumID, position, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.doIgnoreItemFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.doDeleteItemFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doSetItemPermission(long forumID, int permission, int position) {
        final long j = forumID;
        final int i = permission;
        final int i2 = position;
        this.model.doSetItemPermission(forumID, permission, new DataListener<BResponseNoData>() {
            public void onResponse(BResponseNoData result) {
                if (result == null) {
                    FcCommItemPresenter.this.mView.doSetItemPermissionFailed((String) null);
                } else if (result.isState()) {
                    FcCommItemPresenter.this.mView.doSetItemPermissionSucc(j, i, i2, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.doSetItemPermissionFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.doSetItemPermissionFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doAddIgnoreUser(final ArrayList<FcIgnoreUserBean> ignores) {
        this.model.doAddIgnoreUser(ignores, new DataListener<BResponse<ArrayList<FcIgnoreUserBean>>>() {
            public void onResponse(BResponse<ArrayList<FcIgnoreUserBean>> result) {
                if (result == null) {
                    return;
                }
                if (result.isState()) {
                    FcCommItemPresenter.this.mView.doAddIgnoreUserSucc(ignores, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.onError(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.doAddIgnoreUserFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doDeleteIgnoreUser(final ArrayList<FcIgnoreUserBean> ignores) {
        this.model.doDeleteIgnoreUser(ignores, new DataListener<BResponse<ArrayList<FcIgnoreUserBean>>>() {
            public void onResponse(BResponse<ArrayList<FcIgnoreUserBean>> result) {
                if (result == null) {
                    FcCommItemPresenter.this.mView.onError((String) null);
                } else if (result.isState()) {
                    FcCommItemPresenter.this.mView.doDeleteIgnoreUserSucc(ignores, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.onError(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.onError(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doReply(final RequestReplyFcBean bean, final int replyParentPosition) {
        this.model.doReply(bean, new DataListener<BResponse<FcReplyBean>>() {
            public void onResponse(BResponse<FcReplyBean> result) {
                boolean z = true;
                if (result == null) {
                    BaseFcContract.IFcCommItemView access$000 = FcCommItemPresenter.this.mView;
                    int i = replyParentPosition;
                    if (!(bean.getSupID() == 0 && bean.getSupUser() == 0)) {
                        z = false;
                    }
                    access$000.doReplyFailed(i, z, (String) null, (String) null);
                } else if (!result.isState() || result.Data == null) {
                    BaseFcContract.IFcCommItemView access$0002 = FcCommItemPresenter.this.mView;
                    int i2 = replyParentPosition;
                    if (!(bean.getSupID() == 0 && bean.getSupUser() == 0)) {
                        z = false;
                    }
                    access$0002.doReplyFailed(i2, z, result.Code, result.Message);
                } else {
                    FcCommItemPresenter.this.mView.doReplySucc((FcReplyBean) result.Data, replyParentPosition);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.onError(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void doDeleteComment(long commentID, long forumID, long forumUser, int parentId, int childId) {
        final long j = forumID;
        final long j2 = commentID;
        final int i = parentId;
        final int i2 = childId;
        this.model.doDeleteComment(commentID, forumID, forumUser, new DataListener<BResponseNoData>() {
            public void onResponse(BResponseNoData result) {
                if (result == null) {
                    FcCommItemPresenter.this.mView.onError((String) null);
                } else if (result.isState()) {
                    FcCommItemPresenter.this.mView.doDeleteReplySucc(j, j2, i, i2);
                } else {
                    FcCommItemPresenter.this.mView.onError(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcCommItemPresenter.this.mView.onError(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void unSubscribeTask() {
        this.model.unSubscribeTask();
    }
}
