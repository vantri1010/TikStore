package com.bjz.comm.net.mvp.presenter;

import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.bjz.comm.net.bean.RespOthersFcListBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.model.FcPageOthersModel;
import com.bjz.comm.net.utils.RxHelper;

public class FcPageOthersPresenter implements BaseFcContract.IFcPageOthersPresenter {
    /* access modifiers changed from: private */
    public BaseFcContract.IFcPageOthersView mView;
    private FcPageOthersModel model = null;

    public FcPageOthersPresenter(BaseFcContract.IFcPageOthersView view) {
        this.mView = view;
        if (0 == 0) {
            this.model = new FcPageOthersModel();
        }
    }

    public void getActionCount(long userId) {
        this.model.getActionCount(userId, new DataListener<BResponse<RespFcUserStatisticsBean>>() {
            public void onResponse(BResponse<RespFcUserStatisticsBean> result) {
                if (result == null) {
                    FcPageOthersPresenter.this.mView.onError((String) null);
                } else if (result.isState()) {
                    FcPageOthersPresenter.this.mView.getActionCountSucc((RespFcUserStatisticsBean) result.Data);
                } else {
                    FcPageOthersPresenter.this.mView.onError(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageOthersPresenter.this.mView.onError(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void checkIsFollowed(long followUID) {
        this.model.checkIsFollowed(followUID, new DataListener<BResponse<Boolean>>() {
            public void onResponse(BResponse<Boolean> result) {
                if (result == null) {
                    FcPageOthersPresenter.this.mView.onError((String) null);
                } else if (result.isState()) {
                    FcPageOthersPresenter.this.mView.checkIsFollowedSucc((Boolean) result.Data);
                } else {
                    FcPageOthersPresenter.this.mView.onError(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageOthersPresenter.this.mView.onError(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void getFCList(int limit, long forumID, long userId, int roundNum) {
        this.model.getFCList(limit, forumID, userId, roundNum, new DataListener<BResponse<RespOthersFcListBean>>() {
            public void onResponse(BResponse<RespOthersFcListBean> result) {
                if (result == null) {
                    FcPageOthersPresenter.this.mView.getFCListFailed((String) null);
                } else if (result.isState()) {
                    FcPageOthersPresenter.this.mView.getFCListSucc(result.Code, (RespOthersFcListBean) result.Data);
                } else {
                    FcPageOthersPresenter.this.mView.getFCListFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageOthersPresenter.this.mView.getFCListFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void unSubscribeTask() {
        this.model.unSubscribeTask();
    }
}
