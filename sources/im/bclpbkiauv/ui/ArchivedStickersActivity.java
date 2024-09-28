package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ArchivedStickersActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.ArchivedStickerSetCell;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.components.Switch;
import java.util.ArrayList;

public class ArchivedStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int currentType;
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    private boolean firstLoaded;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loadingStickers;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList<>();
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersLoadingRow;
    /* access modifiers changed from: private */
    public int stickersShadowRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;

    public ArchivedStickersActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getStickers();
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needReloadArchivedStickers);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needReloadArchivedStickers);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ArchivedStickersActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        if (this.currentType == 0) {
            emptyTextProgressView.setText(LocaleController.getString("ArchivedStickersEmpty", R.string.ArchivedStickersEmpty));
        } else {
            emptyTextProgressView.setText(LocaleController.getString("ArchivedMasksEmpty", R.string.ArchivedMasksEmpty));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.loadingStickers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setFocusable(true);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ArchivedStickersActivity.this.lambda$createView$0$ArchivedStickersActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!ArchivedStickersActivity.this.loadingStickers && !ArchivedStickersActivity.this.endReached && ArchivedStickersActivity.this.layoutManager.findLastVisibleItemPosition() > ArchivedStickersActivity.this.stickersLoadingRow - 2) {
                    ArchivedStickersActivity.this.getStickers();
                }
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$ArchivedStickersActivity(final View view, int position) {
        TLRPC.InputStickerSet inputStickerSet;
        if (position >= this.stickersStartRow && position < this.stickersEndRow && getParentActivity() != null) {
            TLRPC.StickerSetCovered stickerSet = this.sets.get(position);
            if (stickerSet.set.id != 0) {
                inputStickerSet = new TLRPC.TL_inputStickerSetID();
                inputStickerSet.id = stickerSet.set.id;
            } else {
                inputStickerSet = new TLRPC.TL_inputStickerSetShortName();
                inputStickerSet.short_name = stickerSet.set.short_name;
            }
            inputStickerSet.access_hash = stickerSet.set.access_hash;
            StickersAlert stickersAlert = new StickersAlert(getParentActivity(), this, inputStickerSet, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
            stickersAlert.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() {
                public void onStickerSetInstalled() {
                    ((ArchivedStickerSetCell) view).setChecked(true);
                }

                public void onStickerSetUninstalled() {
                    ((ArchivedStickerSetCell) view).setChecked(false);
                }
            });
            showDialog(stickersAlert);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (!this.sets.isEmpty()) {
            int i = this.rowCount;
            this.stickersStartRow = i;
            this.stickersEndRow = i + this.sets.size();
            int size = this.rowCount + this.sets.size();
            this.rowCount = size;
            if (!this.endReached) {
                this.rowCount = size + 1;
                this.stickersLoadingRow = size;
                this.stickersShadowRow = -1;
            } else {
                this.rowCount = size + 1;
                this.stickersShadowRow = size;
                this.stickersLoadingRow = -1;
            }
        } else {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersLoadingRow = -1;
            this.stickersShadowRow = -1;
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void getStickers() {
        long j;
        if (!this.loadingStickers && !this.endReached) {
            boolean z = true;
            this.loadingStickers = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null && !this.firstLoaded) {
                emptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
            TLRPC.TL_messages_getArchivedStickers req = new TLRPC.TL_messages_getArchivedStickers();
            if (this.sets.isEmpty()) {
                j = 0;
            } else {
                ArrayList<TLRPC.StickerSetCovered> arrayList = this.sets;
                j = arrayList.get(arrayList.size() - 1).set.id;
            }
            req.offset_id = j;
            req.limit = 15;
            if (this.currentType != 1) {
                z = false;
            }
            req.masks = z;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ArchivedStickersActivity.this.lambda$getStickers$2$ArchivedStickersActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getStickers$2$ArchivedStickersActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ArchivedStickersActivity.this.lambda$null$1$ArchivedStickersActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$ArchivedStickersActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_messages_archivedStickers res = (TLRPC.TL_messages_archivedStickers) response;
            this.sets.addAll(res.sets);
            this.endReached = res.sets.size() != 15;
            this.loadingStickers = false;
            this.firstLoaded = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
            updateRows();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.needReloadArchivedStickers) {
            this.firstLoaded = false;
            this.endReached = false;
            this.sets.clear();
            updateRows();
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showProgress();
            }
            getStickers();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ArchivedStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 0) {
                ArchivedStickerSetCell cell = (ArchivedStickerSetCell) holder.itemView;
                cell.setTag(Integer.valueOf(position));
                TLRPC.StickerSetCovered stickerSet = (TLRPC.StickerSetCovered) ArchivedStickersActivity.this.sets.get(position);
                boolean z = true;
                if (position == ArchivedStickersActivity.this.sets.size() - 1) {
                    z = false;
                }
                cell.setStickersSet(stickerSet, z);
                cell.setChecked(MediaDataController.getInstance(ArchivedStickersActivity.this.currentAccount).isStickerPackInstalled(stickerSet.set.id));
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new ArchivedStickerSetCell(this.mContext, true);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((ArchivedStickerSetCell) view).setOnCheckClick(new Switch.OnCheckedChangeListener() {
                    public final void onCheckedChanged(Switch switchR, boolean z) {
                        ArchivedStickersActivity.ListAdapter.this.lambda$onCreateViewHolder$0$ArchivedStickersActivity$ListAdapter(switchR, z);
                    }
                });
            } else if (viewType == 1) {
                view = new LoadingCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else if (viewType == 2) {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$ArchivedStickersActivity$ListAdapter(Switch buttonView, boolean isChecked) {
            int num = ((Integer) ((ArchivedStickerSetCell) buttonView.getParent()).getTag()).intValue();
            if (num < ArchivedStickersActivity.this.sets.size()) {
                MediaDataController.getInstance(ArchivedStickersActivity.this.currentAccount).removeStickersSet(ArchivedStickersActivity.this.getParentActivity(), ((TLRPC.StickerSetCovered) ArchivedStickersActivity.this.sets.get(num)).set, !isChecked ? 1 : 2, ArchivedStickersActivity.this, false);
            }
        }

        public int getItemViewType(int i) {
            if (i >= ArchivedStickersActivity.this.stickersStartRow && i < ArchivedStickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == ArchivedStickersActivity.this.stickersLoadingRow) {
                return 1;
            }
            if (i == ArchivedStickersActivity.this.stickersShadowRow) {
                return 2;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ArchivedStickerSetCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LoadingCell.class, TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription((View) this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_progressCircle), new ThemeDescription((View) this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked)};
    }
}
