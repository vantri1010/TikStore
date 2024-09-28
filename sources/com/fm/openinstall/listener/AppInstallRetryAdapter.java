package com.fm.openinstall.listener;

import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

public abstract class AppInstallRetryAdapter implements AppInstallListener {
    public abstract void onInstall(AppData appData, boolean z);

    public void onInstallFinish(AppData appData, Error error) {
        if (appData == null) {
            appData = new AppData();
        }
        onInstall(appData, error != null && error.shouldRetry());
    }
}
