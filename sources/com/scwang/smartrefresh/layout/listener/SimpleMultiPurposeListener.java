package com.scwang.smartrefresh.layout.listener;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

public class SimpleMultiPurposeListener implements OnMultiPurposeListener {
    public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
    }

    public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {
    }

    public void onHeaderStartAnimator(RefreshHeader header, int footerHeight, int maxDragHeight) {
    }

    public void onHeaderFinish(RefreshHeader header, boolean success) {
    }

    public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
    }

    public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {
    }

    public void onFooterStartAnimator(RefreshFooter footer, int headerHeight, int maxDragHeight) {
    }

    public void onFooterFinish(RefreshFooter footer, boolean success) {
    }

    public void onRefresh(RefreshLayout refreshLayout) {
    }

    public void onLoadMore(RefreshLayout refreshLayout) {
    }

    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
    }
}
