package com.bjz.comm.net.premission.observer;

public interface PermissionObserver {
    void onRequestPermissionFail(int i);

    void onRequestPermissionSuccess(int i);
}
