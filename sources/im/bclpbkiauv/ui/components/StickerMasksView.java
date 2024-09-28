package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ContentPreviewViewer;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.cells.StickerEmojiCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ScrollSlidingTabStrip;
import java.util.ArrayList;
import java.util.HashMap;

public class StickerMasksView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public int currentType = 1;
    private int lastNotifyWidth;
    private Listener listener;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>()};
    /* access modifiers changed from: private */
    public int recentTabBum = -2;
    private ScrollSlidingTabStrip scrollSlidingTabStrip;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>()};
    private TextView stickersEmptyView;
    /* access modifiers changed from: private */
    public StickersGridAdapter stickersGridAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView stickersGridView;
    /* access modifiers changed from: private */
    public GridLayoutManager stickersLayoutManager;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    /* access modifiers changed from: private */
    public int stickersTabOffset;

    public interface Listener {
        void onStickerSelected(Object obj, TLRPC.Document document);

        void onTypeChanged();
    }

    public StickerMasksView(Context context) {
        super(context);
        setBackgroundColor(-14540254);
        setClickable(true);
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        AnonymousClass1 r0 = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return super.onInterceptTouchEvent(event) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), (ContentPreviewViewer.ContentPreviewViewerDelegate) null);
            }
        };
        this.stickersGridView = r0;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
        this.stickersLayoutManager = gridLayoutManager;
        r0.setLayoutManager(gridLayoutManager);
        this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                if (position == StickerMasksView.this.stickersGridAdapter.totalItems) {
                    return StickerMasksView.this.stickersGridAdapter.stickersPerRow;
                }
                return 1;
            }
        });
        this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        this.stickersGridView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.stickersGridView;
        StickersGridAdapter stickersGridAdapter2 = new StickersGridAdapter(context);
        this.stickersGridAdapter = stickersGridAdapter2;
        recyclerListView.setAdapter(stickersGridAdapter2);
        this.stickersGridView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return StickerMasksView.this.lambda$new$0$StickerMasksView(view, motionEvent);
            }
        });
        $$Lambda$StickerMasksView$24Z7KmxEXNjKE3H6lKfIXj6yuG0 r02 = new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                StickerMasksView.this.lambda$new$1$StickerMasksView(view, i);
            }
        };
        this.stickersOnItemClickListener = r02;
        this.stickersGridView.setOnItemClickListener((RecyclerListView.OnItemClickListener) r02);
        this.stickersGridView.setGlowColor(-657673);
        addView(this.stickersGridView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.stickersEmptyView = textView;
        textView.setTextSize(1, 18.0f);
        this.stickersEmptyView.setTextColor(-7829368);
        addView(this.stickersEmptyView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
        this.stickersGridView.setEmptyView(this.stickersEmptyView);
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = new ScrollSlidingTabStrip(context);
        this.scrollSlidingTabStrip = scrollSlidingTabStrip2;
        scrollSlidingTabStrip2.setBackgroundColor(-16777216);
        this.scrollSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0f));
        this.scrollSlidingTabStrip.setIndicatorColor(-10305560);
        this.scrollSlidingTabStrip.setUnderlineColor(-15066598);
        this.scrollSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(1.0f) + 1);
        addView(this.scrollSlidingTabStrip, LayoutHelper.createFrame(-1, 48, 51));
        updateStickerTabs();
        this.scrollSlidingTabStrip.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() {
            public final void onPageSelected(int i) {
                StickerMasksView.this.lambda$new$2$StickerMasksView(i);
            }
        });
        this.stickersGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                StickerMasksView.this.checkScroll();
            }
        });
    }

    public /* synthetic */ boolean lambda$new$0$StickerMasksView(View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, (ContentPreviewViewer.ContentPreviewViewerDelegate) null);
    }

    public /* synthetic */ void lambda$new$1$StickerMasksView(View view, int position) {
        if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell cell = (StickerEmojiCell) view;
            if (!cell.isDisabled()) {
                TLRPC.Document document = cell.getSticker();
                Object parent = cell.getParentObject();
                this.listener.onStickerSelected(parent, document);
                MediaDataController.getInstance(this.currentAccount).addRecentSticker(1, parent, document, (int) (System.currentTimeMillis() / 1000), false);
                MessagesController.getInstance(this.currentAccount).saveRecentSticker(parent, document, true);
            }
        }
    }

    public /* synthetic */ void lambda$new$2$StickerMasksView(int page) {
        if (page == 0) {
            if (this.currentType == 0) {
                this.currentType = 1;
            } else {
                this.currentType = 0;
            }
            Listener listener2 = this.listener;
            if (listener2 != null) {
                listener2.onTypeChanged();
            }
            this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
            this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
            updateStickerTabs();
            reloadStickersAdapter();
            checkDocuments();
            checkPanels();
        } else if (page == this.recentTabBum + 1) {
            this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
        } else {
            int index = (page - 1) - this.stickersTabOffset;
            if (index >= this.stickerSets[this.currentType].size()) {
                index = this.stickerSets[this.currentType].size() - 1;
            }
            this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack(this.stickerSets[this.currentType].get(index)), 0);
            checkScroll();
        }
    }

    /* access modifiers changed from: private */
    public void checkScroll() {
        int firstVisibleItem = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItem != -1) {
            checkStickersScroll(firstVisibleItem);
        }
    }

    private void checkStickersScroll(int firstVisibleItem) {
        if (this.stickersGridView != null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.scrollSlidingTabStrip;
            int tabForPosition = this.stickersGridAdapter.getTabForPosition(firstVisibleItem) + 1;
            int i = this.recentTabBum;
            if (i <= 0) {
                i = this.stickersTabOffset;
            }
            scrollSlidingTabStrip2.onPageScrolled(tabForPosition, i + 1);
        }
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void updateStickerTabs() {
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.scrollSlidingTabStrip;
        if (scrollSlidingTabStrip2 != null) {
            this.recentTabBum = -2;
            this.stickersTabOffset = 0;
            int lastPosition = scrollSlidingTabStrip2.getCurrentPosition();
            this.scrollSlidingTabStrip.removeTabs();
            if (this.currentType == 0) {
                Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_masks_msk1);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.scrollSlidingTabStrip.addIconTab(drawable);
                this.stickersEmptyView.setText(LocaleController.getString("NoStickers", R.string.NoStickers));
            } else {
                Drawable drawable2 = getContext().getResources().getDrawable(R.drawable.ic_masks_sticker1);
                Theme.setDrawableColorByKey(drawable2, Theme.key_chat_emojiPanelIcon);
                this.scrollSlidingTabStrip.addIconTab(drawable2);
                this.stickersEmptyView.setText(LocaleController.getString("NoMasks", R.string.NoMasks));
            }
            if (!this.recentStickers[this.currentType].isEmpty()) {
                int i = this.stickersTabOffset;
                this.recentTabBum = i;
                this.stickersTabOffset = i + 1;
                this.scrollSlidingTabStrip.addIconTab(Theme.createEmojiIconSelectorDrawable(getContext(), R.drawable.ic_masks_recent1, Theme.getColor(Theme.key_chat_emojiPanelMasksIcon), Theme.getColor(Theme.key_chat_emojiPanelMasksIconSelected)));
            }
            this.stickerSets[this.currentType].clear();
            ArrayList<TLRPC.TL_messages_stickerSet> packs = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int a = 0; a < packs.size(); a++) {
                TLRPC.TL_messages_stickerSet pack = packs.get(a);
                if (!pack.set.archived && pack.documents != null && !pack.documents.isEmpty()) {
                    this.stickerSets[this.currentType].add(pack);
                }
            }
            for (int a2 = 0; a2 < this.stickerSets[this.currentType].size(); a2++) {
                TLRPC.TL_messages_stickerSet set = this.stickerSets[this.currentType].get(a2);
                TLRPC.Document document = set.documents.get(0);
                this.scrollSlidingTabStrip.addStickerTab(document, document, set);
            }
            this.scrollSlidingTabStrip.updateTabStyles();
            if (lastPosition != 0) {
                this.scrollSlidingTabStrip.onPageScrolled(lastPosition, lastPosition);
            }
            checkPanels();
        }
    }

    private void checkPanels() {
        int position;
        if (this.scrollSlidingTabStrip != null && (position = this.stickersLayoutManager.findFirstVisibleItemPosition()) != -1) {
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.scrollSlidingTabStrip;
            int tabForPosition = this.stickersGridAdapter.getTabForPosition(position) + 1;
            int i = this.recentTabBum;
            if (i <= 0) {
                i = this.stickersTabOffset;
            }
            scrollSlidingTabStrip2.onPageScrolled(tabForPosition, i + 1);
        }
    }

    public void addRecentSticker(TLRPC.Document document) {
        if (document != null) {
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(this.currentType, (Object) null, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean wasEmpty = this.recentStickers[this.currentType].isEmpty();
            this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
            StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
            if (stickersGridAdapter2 != null) {
                stickersGridAdapter2.notifyDataSetChanged();
            }
            if (wasEmpty) {
                updateStickerTabs();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            reloadStickersAdapter();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    private void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    public void setListener(Listener value) {
        this.listener = value;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoad);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                StickerMasksView.this.lambda$onAttachedToWindow$3$StickerMasksView();
            }
        });
    }

    public /* synthetic */ void lambda$onAttachedToWindow$3$StickerMasksView() {
        updateStickerTabs();
        reloadStickersAdapter();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 8) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
            updateStickerTabs();
            reloadStickersAdapter();
            checkDocuments();
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(1, false, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
        }
    }

    public void onDestroy() {
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
        }
    }

    private void checkDocuments() {
        int previousCount = this.recentStickers[this.currentType].size();
        this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        if (previousCount != this.recentStickers[this.currentType].size()) {
            updateStickerTabs();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (args[0].intValue() == this.currentType) {
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (id == NotificationCenter.recentDocumentsDidLoad && !args[0].booleanValue() && args[1].intValue() == this.currentType) {
            checkDocuments();
        }
    }

    private class StickersGridAdapter extends RecyclerListView.SelectionAdapter {
        private SparseArray<TLRPC.Document> cache = new SparseArray<>();
        private Context context;
        private HashMap<TLRPC.TL_messages_stickerSet, Integer> packStartRow = new HashMap<>();
        private SparseArray<TLRPC.TL_messages_stickerSet> positionsToSets = new SparseArray<>();
        private SparseArray<TLRPC.TL_messages_stickerSet> rowStartPack = new SparseArray<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int totalItems;

        public StickersGridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(TLRPC.TL_messages_stickerSet stickerSet) {
            return this.packStartRow.get(stickerSet).intValue() * this.stickersPerRow;
        }

        public int getItemViewType(int position) {
            if (this.cache.get(position) != null) {
                return 0;
            }
            return 1;
        }

        public int getTabForPosition(int position) {
            if (this.stickersPerRow == 0) {
                int width = StickerMasksView.this.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            }
            TLRPC.TL_messages_stickerSet pack = this.rowStartPack.get(position / this.stickersPerRow);
            if (pack == null) {
                return StickerMasksView.this.recentTabBum;
            }
            return StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].indexOf(pack) + StickerMasksView.this.stickersTabOffset;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new StickerEmojiCell(this.context) {
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                    }
                };
            } else if (viewType == 1) {
                view = new EmptyCell(this.context);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType != 0) {
                int i = 1;
                if (itemViewType == 1) {
                    if (position == this.totalItems) {
                        TLRPC.TL_messages_stickerSet pack = this.rowStartPack.get((position - 1) / this.stickersPerRow);
                        if (pack == null) {
                            ((EmptyCell) holder.itemView).setHeight(1);
                            return;
                        }
                        int height = StickerMasksView.this.stickersGridView.getMeasuredHeight() - (((int) Math.ceil((double) (((float) pack.documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                        EmptyCell emptyCell = (EmptyCell) holder.itemView;
                        if (height > 0) {
                            i = height;
                        }
                        emptyCell.setHeight(i);
                        return;
                    }
                    ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                    return;
                }
                return;
            }
            ((StickerEmojiCell) holder.itemView).setSticker(this.cache.get(position), this.positionsToSets.get(position), false);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void notifyDataSetChanged() {
            /*
                r11 = this;
                im.bclpbkiauv.ui.components.StickerMasksView r0 = im.bclpbkiauv.ui.components.StickerMasksView.this
                int r0 = r0.getMeasuredWidth()
                if (r0 != 0) goto L_0x000c
                android.graphics.Point r1 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r0 = r1.x
            L_0x000c:
                r1 = 1116733440(0x42900000, float:72.0)
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
                int r1 = r0 / r1
                r11.stickersPerRow = r1
                im.bclpbkiauv.ui.components.StickerMasksView r1 = im.bclpbkiauv.ui.components.StickerMasksView.this
                androidx.recyclerview.widget.GridLayoutManager r1 = r1.stickersLayoutManager
                int r2 = r11.stickersPerRow
                r1.setSpanCount(r2)
                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet> r1 = r11.rowStartPack
                r1.clear()
                java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Integer> r1 = r11.packStartRow
                r1.clear()
                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$Document> r1 = r11.cache
                r1.clear()
                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet> r1 = r11.positionsToSets
                r1.clear()
                r1 = 0
                r11.totalItems = r1
                im.bclpbkiauv.ui.components.StickerMasksView r1 = im.bclpbkiauv.ui.components.StickerMasksView.this
                java.util.ArrayList[] r1 = r1.stickerSets
                im.bclpbkiauv.ui.components.StickerMasksView r2 = im.bclpbkiauv.ui.components.StickerMasksView.this
                int r2 = r2.currentType
                r1 = r1[r2]
                r2 = -1
            L_0x0047:
                int r3 = r1.size()
                if (r2 >= r3) goto L_0x00c4
                r3 = 0
                int r4 = r11.totalItems
                int r5 = r11.stickersPerRow
                int r4 = r4 / r5
                r5 = -1
                if (r2 != r5) goto L_0x0065
                im.bclpbkiauv.ui.components.StickerMasksView r5 = im.bclpbkiauv.ui.components.StickerMasksView.this
                java.util.ArrayList[] r5 = r5.recentStickers
                im.bclpbkiauv.ui.components.StickerMasksView r6 = im.bclpbkiauv.ui.components.StickerMasksView.this
                int r6 = r6.currentType
                r5 = r5[r6]
                goto L_0x0077
            L_0x0065:
                java.lang.Object r5 = r1.get(r2)
                r3 = r5
                im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_stickerSet) r3
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r5 = r3.documents
                java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Integer> r6 = r11.packStartRow
                java.lang.Integer r7 = java.lang.Integer.valueOf(r4)
                r6.put(r3, r7)
            L_0x0077:
                boolean r6 = r5.isEmpty()
                if (r6 == 0) goto L_0x007e
                goto L_0x00c1
            L_0x007e:
                int r6 = r5.size()
                float r6 = (float) r6
                int r7 = r11.stickersPerRow
                float r7 = (float) r7
                float r6 = r6 / r7
                double r6 = (double) r6
                double r6 = java.lang.Math.ceil(r6)
                int r6 = (int) r6
                r7 = 0
            L_0x008e:
                int r8 = r5.size()
                if (r7 >= r8) goto L_0x00ab
                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$Document> r8 = r11.cache
                int r9 = r11.totalItems
                int r9 = r9 + r7
                java.lang.Object r10 = r5.get(r7)
                r8.put(r9, r10)
                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet> r8 = r11.positionsToSets
                int r9 = r11.totalItems
                int r9 = r9 + r7
                r8.put(r9, r3)
                int r7 = r7 + 1
                goto L_0x008e
            L_0x00ab:
                int r7 = r11.totalItems
                int r8 = r11.stickersPerRow
                int r8 = r8 * r6
                int r7 = r7 + r8
                r11.totalItems = r7
                r7 = 0
            L_0x00b5:
                if (r7 >= r6) goto L_0x00c1
                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet> r8 = r11.rowStartPack
                int r9 = r4 + r7
                r8.put(r9, r3)
                int r7 = r7 + 1
                goto L_0x00b5
            L_0x00c1:
                int r2 = r2 + 1
                goto L_0x0047
            L_0x00c4:
                super.notifyDataSetChanged()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.StickerMasksView.StickersGridAdapter.notifyDataSetChanged():void");
        }
    }
}
