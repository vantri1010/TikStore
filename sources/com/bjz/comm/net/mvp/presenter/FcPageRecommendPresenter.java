package com.bjz.comm.net.mvp.presenter;

import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.model.FcPageRecommendModel;
import com.bjz.comm.net.utils.RxHelper;
import java.util.ArrayList;

public class FcPageRecommendPresenter implements BaseFcContract.IFcPageRecommendPresenter {
    BaseFcContract.IFcPageRecommendView mView;
    private FcPageRecommendModel model = null;

    public FcPageRecommendPresenter(BaseFcContract.IFcPageRecommendView view) {
        this.mView = view;
        if (0 == 0) {
            this.model = new FcPageRecommendModel();
        }
    }

    public void getFcList(int limit, long forumID) {
        this.model.getFcList(limit, forumID, new DataListener<BResponse<ArrayList<RespFcListBean>>>() {
            public void onResponse(BResponse<ArrayList<RespFcListBean>> result) {
                if (result == null) {
                    FcPageRecommendPresenter.this.mView.getFcListFailed((String) null);
                } else if (result.isState()) {
                    FcPageRecommendPresenter.this.mView.getFcListSucc(result.Data == null ? new ArrayList() : (ArrayList) result.Data);
                } else {
                    FcPageRecommendPresenter.this.mView.getFcListFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FcPageRecommendPresenter.this.mView.getFcListFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void unSubscribeTask() {
        this.model.unSubscribeTask();
    }
}
