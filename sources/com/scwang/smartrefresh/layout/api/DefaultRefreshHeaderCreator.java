package com.scwang.smartrefresh.layout.api;

import android.content.Context;

public interface DefaultRefreshHeaderCreator {
    RefreshHeader createRefreshHeader(Context context, RefreshLayout refreshLayout);
}
