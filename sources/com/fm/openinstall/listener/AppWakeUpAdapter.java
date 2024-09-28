package com.fm.openinstall.listener;

import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

public abstract class AppWakeUpAdapter implements AppWakeUpListener {
    public abstract void onWakeUp(AppData appData);

    public void onWakeUpFinish(AppData appData, Error error) {
        if (error == null && appData != null && !appData.isEmpty()) {
            onWakeUp(appData);
        }
    }
}
