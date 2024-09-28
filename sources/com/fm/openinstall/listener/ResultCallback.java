package com.fm.openinstall.listener;

import com.fm.openinstall.model.Error;

public interface ResultCallback<Result> {
    void onResult(Result result, Error error);
}
