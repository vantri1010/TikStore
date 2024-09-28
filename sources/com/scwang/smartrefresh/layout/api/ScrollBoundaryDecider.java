package com.scwang.smartrefresh.layout.api;

import android.view.View;

public interface ScrollBoundaryDecider {
    boolean canLoadMore(View view);

    boolean canRefresh(View view);
}
