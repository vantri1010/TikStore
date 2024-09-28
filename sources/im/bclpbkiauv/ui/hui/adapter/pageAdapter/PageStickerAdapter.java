package im.bclpbkiauv.ui.hui.adapter.pageAdapter;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.decoration.StickyXDecoration;
import im.bclpbkiauv.ui.decoration.cache.CacheUtil;
import im.bclpbkiauv.ui.decoration.listener.GroupXListener;
import im.bclpbkiauv.ui.decoration.listener.OnGroupClickListener;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;

public abstract class PageStickerAdapter<T, VH extends PageHolder> extends PageSelectionAdapter<T, VH> implements GroupXListener, OnGroupClickListener {
    private boolean mAutoAddSticker = true;
    private boolean mHasAddStickerDeration;
    private RecyclerView mRv;
    private CacheUtil<String> mStickerCache = new CacheUtil<>();
    private CacheUtil<PageHolder> mStickerHeaderCache = new CacheUtil<>();
    private int mStickerHeight;

    /* access modifiers changed from: protected */
    public abstract View getStickerHeader(int i, T t);

    /* access modifiers changed from: protected */
    public abstract String getStickerName(int i, T t);

    /* access modifiers changed from: protected */
    public abstract void onBindStickerHeaderHolder(PageHolder pageHolder, int i, T t);

    public PageStickerAdapter(Context context) {
        super(context);
    }

    private void cleanCache() {
        CacheUtil<String> cacheUtil = this.mStickerCache;
        if (cacheUtil == null) {
            this.mStickerCache = new CacheUtil<>();
        } else {
            cacheUtil.clean();
        }
        CacheUtil<PageHolder> cacheUtil2 = this.mStickerHeaderCache;
        if (cacheUtil2 == null) {
            this.mStickerHeaderCache = new CacheUtil<>();
        } else {
            cacheUtil2.clean();
        }
    }

    private void setStickerDeration() {
        RecyclerView recyclerView;
        if (this.mAutoAddSticker && (recyclerView = this.mRv) != null && !this.mHasAddStickerDeration) {
            this.mHasAddStickerDeration = true;
            recyclerView.addItemDecoration(getDefaultStickerDecoration(this.mStickerHeight));
        }
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        cleanCache();
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRv = recyclerView;
        setStickerDeration();
    }

    public void reLoadData() {
        super.reLoadData();
        cleanCache();
    }

    public final View getGroupView(int position) {
        PageHolder holder;
        View view = null;
        if (position >= 0 && position < getData().size()) {
            T item = getData().get(position);
            if (this.mStickerHeaderCache.get(position) == null) {
                view = getStickerHeader(position, item);
                holder = new PageHolder(view);
                this.mStickerHeaderCache.put(position, holder);
            } else {
                holder = this.mStickerHeaderCache.get(position);
                view = holder.itemView;
            }
            onBindStickerHeaderHolder(holder, position, item);
        }
        return view;
    }

    public final String getGroupName(int position) {
        String name = null;
        int count = getItemCount();
        if (position >= 0 && position < count && (name = this.mStickerCache.get(position)) == null) {
            if (position < getData().size()) {
                name = getStickerName(position, getItem(position));
            }
            if (name == null) {
                name = getGroupName(position - 1);
            }
            try {
                this.mStickerCache.put(position, name == null ? "" : name);
            } catch (Exception e) {
                FileLog.e("PageSelectionAdapter =====> " + e.getMessage());
            }
        }
        return name;
    }

    public void onClick(int position, int viewId) {
    }

    public int getStickerHeight() {
        return this.mStickerHeight;
    }

    public PageStickerAdapter<T, VH> setStickerHeight(int stickerHeight) {
        this.mStickerHeight = stickerHeight;
        return this;
    }

    public PageStickerAdapter<T, VH> setAutoAddSticker(boolean autoAddSticker) {
        this.mAutoAddSticker = autoAddSticker;
        return this;
    }

    public StickyXDecoration getDefaultStickerDecoration(int stickerHeight) {
        return getDefaultStickerDecoration(stickerHeight, true);
    }

    public StickyXDecoration getDefaultStickerDecoration(int stickerHeight, boolean isDpValue) {
        this.mStickerHeight = stickerHeight;
        StickyXDecoration.Builder decorationBuilder = StickyXDecoration.Builder.init(this).setOnClickListener(this).setGroupBackground(Theme.getColor(Theme.key_list_decorationBackground));
        if (stickerHeight > 0) {
            decorationBuilder.setGroupHeight(isDpValue ? AndroidUtilities.dp((float) stickerHeight) : stickerHeight);
        }
        return decorationBuilder.build();
    }

    public void destroy() {
        super.destroy();
        cleanCache();
        this.mRv = null;
    }
}
