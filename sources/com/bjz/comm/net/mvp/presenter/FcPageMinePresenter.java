package com.bjz.comm.net.mvp.presenter;

import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.BResponseNoData;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.model.FcPageMineModel;
import com.bjz.comm.net.utils.RxHelper;
import java.util.ArrayList;

public class FcPageMinePresenter implements BaseFcContract.IFcPageMinePresenter {
    BaseFcContract.IFcPageMineView mView;
    private FcPageMineModel model = null;

    public FcPageMinePresenter(BaseFcContract.IFcPageMineView view) {
        this.mView = view;
        if (0 == 0) {
            this.model = new FcPageMineModel();
        }
    }

    public void getFCList(int limit, long forumID) {
        this.model.getFCList(limit, forumID, new DataListener<BResponse<ArrayList<RespFcListBean>>>() {
            public void onResponse(BResponse<ArrayList<RespFcListBean>> result) {
                if (result == null) {
                    FcPageMinePresenter.this.mView.getFCListFailed((String) null);
                } else if (result.isState()) {
                    FcPageMinePresenter.this.mView.getFCListSucc(result.Data == null ? new ArrayList() : (ArrayList) result.Data);
                } else {
                    FcPageMinePresenter.this.mView.getFCListFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageMinePresenter.this.mView.getFCListFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void getActionCount(long userId) {
        this.model.getActionCount(userId, new DataListener<BResponse<RespFcUserStatisticsBean>>() {
            public void onResponse(BResponse<RespFcUserStatisticsBean> result) {
                if (result == null) {
                    FcPageMinePresenter.this.mView.onError((String) null);
                } else if (result.isState()) {
                    FcPageMinePresenter.this.mView.getActionCountSucc((RespFcUserStatisticsBean) result.Data);
                } else {
                    FcPageMinePresenter.this.mView.onError(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageMinePresenter.this.mView.onError(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void setFcBackground(final String homeBackground) {
        this.model.setFcBackground(homeBackground, new DataListener<BResponseNoData>() {
            public void onResponse(BResponseNoData result) {
                if (result == null) {
                    FcPageMinePresenter.this.mView.setFcBackgroundFailed((String) null);
                } else if (result.isState()) {
                    FcPageMinePresenter.this.mView.setFcBackgroundSucc(homeBackground, result.Message);
                } else {
                    FcPageMinePresenter.this.mView.setFcBackgroundFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageMinePresenter.this.mView.setFcBackgroundFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void unSubscribeTask() {
        this.model.unSubscribeTask();
    }
}
