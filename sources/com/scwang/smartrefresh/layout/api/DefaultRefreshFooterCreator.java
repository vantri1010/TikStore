package com.scwang.smartrefresh.layout.api;

import android.content.Context;

public interface DefaultRefreshFooterCreator {
    RefreshFooter createRefreshFooter(Context context, RefreshLayout refreshLayout);
}
