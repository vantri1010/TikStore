package com.scwang.smartrefresh.layout.listener;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public interface OnStateChangedListener {
    void onStateChanged(RefreshLayout refreshLayout, RefreshState refreshState, RefreshState refreshState2);
}
