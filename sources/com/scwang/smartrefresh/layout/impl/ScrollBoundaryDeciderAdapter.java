package com.scwang.smartrefresh.layout.impl;

import android.graphics.PointF;
import android.view.View;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.scwang.smartrefresh.layout.util.SmartUtil;

public class ScrollBoundaryDeciderAdapter implements ScrollBoundaryDecider {
    public ScrollBoundaryDecider boundary;
    public PointF mActionEvent;
    public boolean mEnableLoadMoreWhenContentNotFull = true;

    public boolean canRefresh(View content) {
        ScrollBoundaryDecider scrollBoundaryDecider = this.boundary;
        if (scrollBoundaryDecider != null) {
            return scrollBoundaryDecider.canRefresh(content);
        }
        return SmartUtil.canRefresh(content, this.mActionEvent);
    }

    public boolean canLoadMore(View content) {
        ScrollBoundaryDecider scrollBoundaryDecider = this.boundary;
        if (scrollBoundaryDecider != null) {
            return scrollBoundaryDecider.canLoadMore(content);
        }
        return SmartUtil.canLoadMore(content, this.mActionEvent, this.mEnableLoadMoreWhenContentNotFull);
    }
}
