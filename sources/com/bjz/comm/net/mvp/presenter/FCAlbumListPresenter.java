package com.bjz.comm.net.mvp.presenter;

import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.RespFcAlbumListBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.model.FCPageAlbumListModel;
import com.bjz.comm.net.utils.RxHelper;
import java.util.ArrayList;

public class FCAlbumListPresenter implements BaseFcContract.IFcPageAlbumListPresenter {
    /* access modifiers changed from: private */
    public BaseFcContract.IFcPageAlbumListView mView;
    private FCPageAlbumListModel model = null;

    public FCAlbumListPresenter(BaseFcContract.IFcPageAlbumListView view) {
        this.mView = view;
        if (0 == 0) {
            this.model = new FCPageAlbumListModel();
        }
    }

    public void unSubscribeTask() {
        this.model.unSubscribeTask();
    }

    public void getAlbumList(int userId, long id, int limit) {
        this.model.getAlbumList(userId, id, limit, new DataListener<BResponse<ArrayList<RespFcAlbumListBean>>>() {
            public void onResponse(BResponse<ArrayList<RespFcAlbumListBean>> result) {
                if (result == null) {
                    FCAlbumListPresenter.this.mView.getAlbumListFailed((String) null);
                } else if (result.isState()) {
                    FCAlbumListPresenter.this.mView.getAlbumListSucc(result.Data == null ? new ArrayList() : (ArrayList) result.Data);
                } else {
                    FCAlbumListPresenter.this.mView.getAlbumListFailed(result.Message);
                }
            }

            public void onError(Throwable throwable) {
                FCAlbumListPresenter.this.mView.getAlbumListFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }
}
