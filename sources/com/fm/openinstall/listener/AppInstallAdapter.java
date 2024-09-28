package com.fm.openinstall.listener;

import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

public abstract class AppInstallAdapter implements AppInstallListener {
    public abstract void onInstall(AppData appData);

    public void onInstallFinish(AppData appData, Error error) {
        if (appData == null) {
            appData = new AppData();
        }
        onInstall(appData);
    }
}
