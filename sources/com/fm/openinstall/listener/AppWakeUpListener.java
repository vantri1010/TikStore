package com.fm.openinstall.listener;

import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

public interface AppWakeUpListener {
    void onWakeUpFinish(AppData appData, Error error);
}
