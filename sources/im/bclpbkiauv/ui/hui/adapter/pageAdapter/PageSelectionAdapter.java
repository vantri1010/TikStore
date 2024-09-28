package im.bclpbkiauv.ui.hui.adapter.pageAdapter;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import java.util.ArrayList;
import java.util.List;

public abstract class PageSelectionAdapter<T, VH extends PageHolder> extends RecyclerListView.SelectionAdapter implements PageLoadMoreListener, OnRefreshLoadMoreListener, MryEmptyView.OnEmptyOrErrorClickListener {
    public static final String TAG = "PageSelectionAdapter";
    protected PageAdapterHelper<T, VH> mHelper;

    public abstract void onBindViewHolderForChild(VH vh, int i, T t);

    public abstract VH onCreateViewHolderForChild(ViewGroup viewGroup, int i);

    public PageSelectionAdapter(Context context) {
        PageAdapterHelper<T, VH> pageAdapterHelper = new PageAdapterHelper<>(context, this);
        this.mHelper = pageAdapterHelper;
        pageAdapterHelper.addPageLoadMoreListener(this);
        this.mHelper.setOnEmptyClickListener(this);
    }

    /* access modifiers changed from: protected */
    public int getItemViewTypeForChild(int position) {
        return super.getItemViewType(position);
    }

    /* access modifiers changed from: protected */
    public boolean isEnableForChild(VH vh) {
        return true;
    }

    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH vh = null;
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            vh = (PageHolder) pageAdapterHelper.onCreateViewHolder(parent, viewType);
        }
        if (vh == null) {
            return onCreateViewHolderForChild(parent, viewType);
        }
        return vh;
    }

    public final boolean isEnabled(RecyclerView.ViewHolder holder) {
        boolean enable = true;
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            enable = pageAdapterHelper.isEnabled(holder);
        }
        if (!enable) {
            return isEnableForChild((PageHolder) holder);
        }
        return enable;
    }

    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        boolean tag = false;
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            tag = pageAdapterHelper.onBindViewHolder(holder, position);
        }
        if (!tag && position >= 0 && position < getData().size()) {
            try {
                onBindViewHolderForChild((PageHolder) holder, position, getData().get(position));
            } catch (Exception e) {
                FileLog.e("PageSelectionAdapter =======> e = " + e.getMessage());
            }
        }
    }

    public final int getItemViewType(int position) {
        int itemViewType = -1;
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            itemViewType = pageAdapterHelper.getItemViewType(position);
        }
        if (itemViewType != -1) {
            return itemViewType;
        }
        return getItemViewTypeForChild(position);
    }

    public final int getItemCount() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            return pageAdapterHelper.getItemCount();
        }
        return 0;
    }

    public int getDataCount() {
        return getData().size();
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.onAttachedToRecyclerView(recyclerView);
        }
    }

    public boolean hasLoadMoreCountInItemCount() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            return pageAdapterHelper.hasLoadMoreCountInItemCount();
        }
        return false;
    }

    public T getItem(int position) {
        if (position < 0 || position >= getData().size()) {
            return null;
        }
        return getData().get(position);
    }

    public void setAdapterStateView(AdapterStateView adapterStateView) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setAdapterStateView(adapterStateView);
        }
    }

    public AdapterStateView getAdapterStateView() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            return pageAdapterHelper.getAdapterStateView();
        }
        return null;
    }

    public void setPageLoadMoreListener(PageLoadMoreListener pageLoadMoreListener) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.addPageLoadMoreListener(pageLoadMoreListener);
        }
    }

    public void setSpanSizeLookup(GridLayoutManager.SpanSizeLookup spanSizeLookup) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setSpanSizeLookup(spanSizeLookup);
        }
    }

    public void setData(List<T> data) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setData(data);
        }
    }

    public void addData(List<T> data) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.addData(data);
        }
    }

    public void addData(T itemData) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.addData(itemData);
        }
    }

    public boolean removeData(T itemData) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            return pageAdapterHelper.removeData(itemData);
        }
        return false;
    }

    public T removeData(int position) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            return pageAdapterHelper.removeData(position);
        }
        return null;
    }

    public Context getContext() {
        Context context = null;
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            context = pageAdapterHelper.getContext();
        }
        if (context == null) {
            return ApplicationLoader.applicationContext;
        }
        return context;
    }

    public List<T> getData() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        return pageAdapterHelper == null ? new ArrayList() : pageAdapterHelper.getData();
    }

    public void reLoadData() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.reLoadData(true);
        }
    }

    public void setIgnorePageLimit(boolean ignore) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setIgnorePageLimit(ignore);
        }
    }

    public void setPageLimit(int limit) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setPageLimit(limit);
        }
    }

    public int getPage() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper == null) {
            return 1;
        }
        return pageAdapterHelper.getPage();
    }

    public int getStartPage() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper == null) {
            return 1;
        }
        return pageAdapterHelper.getStartPage();
    }

    public void pageReset() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.pageReset();
        }
    }

    public void setStartPage(int startPage) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setStartPage(startPage);
        }
    }

    public void clearData() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.clearData();
        }
    }

    public void loadMoreFinish() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.loadMoreFinish();
        }
    }

    public void loadMoreNoMoreData() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.loadMoreNoMoreData();
        }
    }

    public void showLoading() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.showLoading();
        }
    }

    public void showContent() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.showContent();
        }
    }

    public void showEmpty() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.showEmpty();
        }
    }

    public void showError(CharSequence errorMsg) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.showError(errorMsg);
        }
    }

    public void setEmptyViewEmptyResId(int emptyResId) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setEmptyViewEmptyResId(emptyResId);
        }
    }

    public void setEmptyViewEmptyText(CharSequence emptyBtnText) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setEmptyText(emptyBtnText);
        }
    }

    public void setEmptyViewErrorResId(int errorResId) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setEmptyViewErrorResId(errorResId);
        }
    }

    public void setEmptyViewEmptyButtonText(CharSequence emptyBtnText) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setEmptyViewEmptyButtonText(emptyBtnText);
        }
    }

    public void setEmptyViewErrorButtonText(CharSequence emptyBtnText) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setEmptyViewErrorButtonText(emptyBtnText);
        }
    }

    public void emptyAttachView(ViewGroup rootView) {
        emptyAttachView(rootView, true);
    }

    public void emptyAttachView(ViewGroup rootView, boolean showLoading) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.emptyAttachView(rootView, showLoading);
        }
    }

    public boolean onEmptyViewButtonClick(boolean isEmptyButton) {
        return false;
    }

    public MryEmptyView getEmptyView() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            return pageAdapterHelper.getEmptyView();
        }
        return null;
    }

    public void setShowLoadMoreViewEnable(boolean showLoadMoreView) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.setShowLoadMoreViewEnable(showLoadMoreView);
        }
    }

    public boolean isShowLoadMoreViewEnable() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            return pageAdapterHelper.isShowLoadMoreViewEnable();
        }
        return true;
    }

    public final void onRefresh(RefreshLayout refreshLayout) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.onRefresh(refreshLayout);
        }
    }

    public final void onLoadMore(RefreshLayout refreshLayout) {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.onLoadMore(refreshLayout);
        }
    }

    public void loadData(int page) {
    }

    public void destroy() {
        PageAdapterHelper<T, VH> pageAdapterHelper = this.mHelper;
        if (pageAdapterHelper != null) {
            pageAdapterHelper.destroy();
            this.mHelper = null;
        }
    }
}
