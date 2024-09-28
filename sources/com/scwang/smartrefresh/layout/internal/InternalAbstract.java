package com.scwang.smartrefresh.layout.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.impl.RefreshFooterWrapper;
import com.scwang.smartrefresh.layout.impl.RefreshHeaderWrapper;
import com.scwang.smartrefresh.layout.listener.OnStateChangedListener;

public abstract class InternalAbstract extends RelativeLayout implements RefreshInternal {
    protected SpinnerStyle mSpinnerStyle;
    protected RefreshInternal mWrappedInternal;
    protected View mWrappedView;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    protected InternalAbstract(View wrapped) {
        this(wrapped, wrapped instanceof RefreshInternal ? (RefreshInternal) wrapped : null);
    }

    protected InternalAbstract(View wrappedView, RefreshInternal wrappedInternal) {
        super(wrappedView.getContext(), (AttributeSet) null, 0);
        this.mWrappedView = wrappedView;
        this.mWrappedInternal = wrappedInternal;
        if ((this instanceof RefreshFooterWrapper) && (wrappedInternal instanceof RefreshHeader) && wrappedInternal.getSpinnerStyle() == SpinnerStyle.MatchLayout) {
            wrappedInternal.getView().setScaleY(-1.0f);
        } else if (this instanceof RefreshHeaderWrapper) {
            RefreshInternal refreshInternal = this.mWrappedInternal;
            if ((refreshInternal instanceof RefreshFooter) && refreshInternal.getSpinnerStyle() == SpinnerStyle.MatchLayout) {
                wrappedInternal.getView().setScaleY(-1.0f);
            }
        }
    }

    protected InternalAbstract(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj instanceof RefreshInternal) || getView() != ((RefreshInternal) obj).getView()) {
            return false;
        }
        return true;
    }

    public View getView() {
        View view = this.mWrappedView;
        return view == null ? this : view;
    }

    public int onFinish(RefreshLayout refreshLayout, boolean success) {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        if (refreshInternal == null || refreshInternal == this) {
            return 0;
        }
        return refreshInternal.onFinish(refreshLayout, success);
    }

    public void setPrimaryColors(int... colors) {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        if (refreshInternal != null && refreshInternal != this) {
            refreshInternal.setPrimaryColors(colors);
        }
    }

    public SpinnerStyle getSpinnerStyle() {
        SpinnerStyle spinnerStyle = this.mSpinnerStyle;
        if (spinnerStyle != null) {
            return spinnerStyle;
        }
        RefreshInternal refreshInternal = this.mWrappedInternal;
        if (refreshInternal != null && refreshInternal != this) {
            return refreshInternal.getSpinnerStyle();
        }
        View view = this.mWrappedView;
        if (view != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params instanceof SmartRefreshLayout.LayoutParams) {
                SpinnerStyle spinnerStyle2 = ((SmartRefreshLayout.LayoutParams) params).spinnerStyle;
                this.mSpinnerStyle = spinnerStyle2;
                if (spinnerStyle2 != null) {
                    return spinnerStyle2;
                }
            }
            if (params != null && (params.height == 0 || params.height == -1)) {
                for (SpinnerStyle style : SpinnerStyle.values) {
                    if (style.scale) {
                        this.mSpinnerStyle = style;
                        return style;
                    }
                }
            }
        }
        SpinnerStyle spinnerStyle3 = SpinnerStyle.Translate;
        this.mSpinnerStyle = spinnerStyle3;
        return spinnerStyle3;
    }

    public void onInitialized(RefreshKernel kernel, int height, int maxDragHeight) {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        if (refreshInternal == null || refreshInternal == this) {
            View view = this.mWrappedView;
            if (view != null) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if (params instanceof SmartRefreshLayout.LayoutParams) {
                    kernel.requestDrawBackgroundFor(this, ((SmartRefreshLayout.LayoutParams) params).backgroundColor);
                    return;
                }
                return;
            }
            return;
        }
        refreshInternal.onInitialized(kernel, height, maxDragHeight);
    }

    public boolean isSupportHorizontalDrag() {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        return (refreshInternal == null || refreshInternal == this || !refreshInternal.isSupportHorizontalDrag()) ? false : true;
    }

    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        if (refreshInternal != null && refreshInternal != this) {
            refreshInternal.onHorizontalDrag(percentX, offsetX, offsetMax);
        }
    }

    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        if (refreshInternal != null && refreshInternal != this) {
            refreshInternal.onMoving(isDragging, percent, offset, height, maxDragHeight);
        }
    }

    public void onReleased(RefreshLayout refreshLayout, int height, int maxDragHeight) {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        if (refreshInternal != null && refreshInternal != this) {
            refreshInternal.onReleased(refreshLayout, height, maxDragHeight);
        }
    }

    public void onStartAnimator(RefreshLayout refreshLayout, int height, int maxDragHeight) {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        if (refreshInternal != null && refreshInternal != this) {
            refreshInternal.onStartAnimator(refreshLayout, height, maxDragHeight);
        }
    }

    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        if (refreshInternal != null && refreshInternal != this) {
            if ((this instanceof RefreshFooterWrapper) && (refreshInternal instanceof RefreshHeader)) {
                if (oldState.isFooter) {
                    oldState = oldState.toHeader();
                }
                if (newState.isFooter) {
                    newState = newState.toHeader();
                }
            } else if ((this instanceof RefreshHeaderWrapper) && (this.mWrappedInternal instanceof RefreshFooter)) {
                if (oldState.isHeader) {
                    oldState = oldState.toFooter();
                }
                if (newState.isHeader) {
                    newState = newState.toFooter();
                }
            }
            OnStateChangedListener listener = this.mWrappedInternal;
            if (listener != null) {
                listener.onStateChanged(refreshLayout, oldState, newState);
            }
        }
    }

    public boolean setNoMoreData(boolean noMoreData) {
        RefreshInternal refreshInternal = this.mWrappedInternal;
        return (refreshInternal instanceof RefreshFooter) && ((RefreshFooter) refreshInternal).setNoMoreData(noMoreData);
    }
}
