package com.bjz.comm.net.mvp.presenter;

import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.RespFcLikesBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespFcReplyBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.model.FcPageDetailModel;
import com.bjz.comm.net.utils.RxHelper;

public class FcPageDetailPresenter implements BaseFcContract.IFcPageDetailPresenter {
    BaseFcContract.IFcPageDetailView mView;
    private FcPageDetailModel model = null;

    public FcPageDetailPresenter(BaseFcContract.IFcPageDetailView view) {
        this.mView = view;
        if (0 == 0) {
            this.model = new FcPageDetailModel();
        }
    }

    public void unSubscribeTask() {
        this.model.unSubscribeTask();
    }

    public void getDetail(long forumID, long forumUserId) {
        this.model.getDetail(forumID, forumUserId, new DataListener<BResponse<RespFcListBean>>() {
            public void onResponse(BResponse<RespFcListBean> result) {
                if (result == null) {
                    FcPageDetailPresenter.this.mView.getDetailFailed((String) null);
                } else if (result.isState()) {
                    FcPageDetailPresenter.this.mView.getDetailSucc((RespFcListBean) result.Data);
                } else {
                    FcPageDetailPresenter.this.mView.getDetailFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageDetailPresenter.this.mView.getDetailFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void getComments(long forumID, long commentId, long forumUserId, int limit) {
        this.model.getComments(forumID, commentId, forumUserId, limit, new DataListener<BResponse<RespFcReplyBean>>() {
            public void onResponse(BResponse<RespFcReplyBean> result) {
                if (result == null || result.Data == null) {
                    FcPageDetailPresenter.this.mView.getCommentsFailed((String) null);
                } else if (result.isState()) {
                    FcPageDetailPresenter.this.mView.getCommentsSucc(((RespFcReplyBean) result.Data).getComments());
                } else {
                    FcPageDetailPresenter.this.mView.getCommentsFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageDetailPresenter.this.mView.getCommentsFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void getReplyList(final FcReplyBean parentFcReplyBean, final int parentFcReplyPosition, long commentId, int limit) {
        this.model.getReplyList(parentFcReplyBean, commentId, limit, new DataListener<BResponse<RespFcReplyBean>>() {
            public void onResponse(BResponse<RespFcReplyBean> result) {
                if (result == null || result.Data == null) {
                    FcPageDetailPresenter.this.mView.getReplyListFailed(parentFcReplyBean, parentFcReplyPosition, (String) null);
                } else if (result.isState()) {
                    FcPageDetailPresenter.this.mView.getReplyListSucc(parentFcReplyBean, parentFcReplyPosition, ((RespFcReplyBean) result.Data).getComments());
                } else {
                    FcPageDetailPresenter.this.mView.getReplyListFailed(parentFcReplyBean, parentFcReplyPosition, result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageDetailPresenter.this.mView.getReplyListFailed(parentFcReplyBean, parentFcReplyPosition, RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void getLikeUserList(long forumId, long thumbId, int limit) {
        this.model.getLikeUserList(forumId, thumbId, limit, new DataListener<BResponse<RespFcLikesBean>>() {
            public void onResponse(BResponse<RespFcLikesBean> result) {
                if (result == null || result.Data == null) {
                    FcPageDetailPresenter.this.mView.getLikeUserListFiled((String) null);
                } else if (result.isState()) {
                    FcPageDetailPresenter.this.mView.getLikeUserListSucc((RespFcLikesBean) result.Data);
                } else {
                    FcPageDetailPresenter.this.mView.getLikeUserListFiled(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageDetailPresenter.this.mView.getLikeUserListFiled(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }
}
