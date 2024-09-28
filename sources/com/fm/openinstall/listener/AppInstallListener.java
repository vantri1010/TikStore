package com.fm.openinstall.listener;

import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

public interface AppInstallListener {
    void onInstallFinish(AppData appData, Error error);
}
