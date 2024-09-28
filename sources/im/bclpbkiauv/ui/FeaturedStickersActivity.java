package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.FeaturedStickersActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.FeaturedStickerSetCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.hui.decoration.TopBottomDecoration;
import java.util.ArrayList;

public class FeaturedStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets = new LongSparseArray<>();
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;
    /* access modifiers changed from: private */
    public ArrayList<Long> unreadStickers = null;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        ArrayList<Long> arrayList = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
        if (arrayList != null) {
            this.unreadStickers = new ArrayList<>(arrayList);
        }
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FeaturedStickersActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        AnonymousClass2 r2 = new RecyclerListView(context) {
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.listView = r2;
        r2.addItemDecoration(new TopBottomDecoration());
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setOverScrollMode(2);
        this.listView.setFocusable(true);
        this.listView.setTag(14);
        AnonymousClass3 r22 = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r22;
        r22.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                FeaturedStickersActivity.this.lambda$createView$0$FeaturedStickersActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$FeaturedStickersActivity(final View view, int position) {
        TLRPC.InputStickerSet inputStickerSet;
        if (position >= this.stickersStartRow && position < this.stickersEndRow && getParentActivity() != null) {
            final TLRPC.StickerSetCovered stickerSet = MediaDataController.getInstance(this.currentAccount).getFeaturedStickerSets().get(position);
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
                    ((FeaturedStickerSetCell) view).setDrawProgress(true);
                    FeaturedStickersActivity.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                }

                public void onStickerSetUninstalled() {
                }
            });
            showDialog(stickersAlert);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.featuredStickersDidLoad) {
            if (this.unreadStickers == null) {
                this.unreadStickers = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
            }
            updateRows();
        } else if (id == NotificationCenter.stickersDidLoad) {
            updateVisibleTrendingSets();
        }
    }

    private void updateVisibleTrendingSets() {
        int first;
        int last;
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        if (linearLayoutManager != null && (first = linearLayoutManager.findFirstVisibleItemPosition()) != -1 && (last = this.layoutManager.findLastVisibleItemPosition()) != -1) {
            this.listAdapter.notifyItemRangeChanged(first, (last - first) + 1);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        ArrayList<TLRPC.StickerSetCovered> stickerSets = MediaDataController.getInstance(this.currentAccount).getFeaturedStickerSets();
        if (!stickerSets.isEmpty()) {
            int i = this.rowCount;
            this.stickersStartRow = i;
            this.stickersEndRow = i + stickerSets.size();
            this.rowCount += stickerSets.size();
        } else {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        MediaDataController.getInstance(this.currentAccount).markFaturedStickersAsRead(true);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return FeaturedStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 0) {
                ArrayList<TLRPC.StickerSetCovered> arrayList = MediaDataController.getInstance(FeaturedStickersActivity.this.currentAccount).getFeaturedStickerSets();
                FeaturedStickerSetCell cell = (FeaturedStickerSetCell) holder.itemView;
                cell.setTag(Integer.valueOf(position));
                TLRPC.StickerSetCovered stickerSet = arrayList.get(position);
                cell.setStickersSet(stickerSet, position != arrayList.size() - 1, FeaturedStickersActivity.this.unreadStickers != null && FeaturedStickersActivity.this.unreadStickers.contains(Long.valueOf(stickerSet.set.id)));
                boolean installing = FeaturedStickersActivity.this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                if (installing && cell.isInstalled()) {
                    FeaturedStickersActivity.this.installingStickerSets.remove(stickerSet.set.id);
                    installing = false;
                    cell.setDrawProgress(false);
                }
                cell.setDrawProgress(installing);
                if (position == FeaturedStickersActivity.this.stickersStartRow) {
                    holder.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (position == FeaturedStickersActivity.this.stickersEndRow - 1) {
                    holder.itemView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new FeaturedStickerSetCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((FeaturedStickerSetCell) view).setAddOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        FeaturedStickersActivity.ListAdapter.this.lambda$onCreateViewHolder$0$FeaturedStickersActivity$ListAdapter(view);
                    }
                });
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$FeaturedStickersActivity$ListAdapter(View v) {
            FeaturedStickerSetCell parent1 = (FeaturedStickerSetCell) v.getParent();
            TLRPC.StickerSetCovered pack = parent1.getStickerSet();
            if (FeaturedStickersActivity.this.installingStickerSets.indexOfKey(pack.set.id) < 0) {
                FeaturedStickersActivity.this.installingStickerSets.put(pack.set.id, pack);
                MediaDataController.getInstance(FeaturedStickersActivity.this.currentAccount).installStickerSet(this.mContext, 0, pack);
                parent1.setDrawProgress(true);
            }
        }

        public int getItemViewType(int i) {
            return (i < FeaturedStickersActivity.this.stickersStartRow || i < FeaturedStickersActivity.this.stickersEndRow) ? 0 : 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FeaturedStickerSetCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_featuredStickers_buttonProgress), new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_featuredStickers_buttonText), new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_featuredStickers_addedIcon), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_featuredStickers_addButton), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_featuredStickers_addButtonPressed)};
    }
}
