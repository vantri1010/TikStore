package com.bjz.comm.net.listener;

import io.reactivex.ObservableEmitter;

public interface GetHttpTokenCallBack {
    void requestToken(ObservableEmitter<String> observableEmitter);
}
