package im.bclpbkiauv.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.StickersActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.StickerSetCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.components.URLSpanNoUnderline;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.DialogCommonList;
import im.bclpbkiauv.ui.hcells.MryTextCheckCell;
import im.bclpbkiauv.ui.hcells.TextSettingCell;
import im.bclpbkiauv.ui.hui.decoration.TopBottomDecoration;
import im.bclpbkiauv.ui.hviews.slidemenu.SwipeLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int archivedInfoRow;
    /* access modifiers changed from: private */
    public int archivedRow;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int featuredInfoRow;
    /* access modifiers changed from: private */
    public int featuredRow;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int loopInfoRow;
    /* access modifiers changed from: private */
    public int loopRow;
    /* access modifiers changed from: private */
    public int masksInfoRow = -1;
    /* access modifiers changed from: private */
    public int masksRow;
    /* access modifiers changed from: private */
    public boolean needReorder;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;
    /* access modifiers changed from: private */
    public int suggestInfoRow;
    /* access modifiers changed from: private */
    public int suggestRow;
    /* access modifiers changed from: private */
    public int yourStickerBagInfoRow;

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 0) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            StickersActivity.this.listAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                StickersActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public StickersActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(this.currentType);
        if (this.currentType == 0) {
            MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        sendReorder();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("StickersSetting", R.string.StickersSetting));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Masks", R.string.Masks));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    StickersActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.addItemDecoration(new TopBottomDecoration(10, 0));
        this.listView.setFocusable(true);
        this.listView.setTag(7);
        this.listView.setOverScrollMode(2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                StickersActivity.this.lambda$createView$0$StickersActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$StickersActivity(View view, int position) {
        if (position >= this.stickersStartRow && position < this.stickersEndRow && getParentActivity() != null) {
            sendReorder();
            TLRPC.TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType).get(position - this.stickersStartRow);
            ArrayList<TLRPC.Document> stickers = stickerSet.documents;
            if (stickers != null && !stickers.isEmpty()) {
                showDialog(new StickersAlert(getParentActivity(), this, (TLRPC.InputStickerSet) null, stickerSet, (StickersAlert.StickersAlertDelegate) null));
            }
        } else if (position == this.featuredRow) {
            sendReorder();
            presentFragment(new FeaturedStickersActivity());
        } else if (position == this.archivedRow) {
            sendReorder();
            presentFragment(new ArchivedStickersActivity(this.currentType));
        } else if (position == this.masksRow) {
            presentFragment(new StickersActivity(1));
        } else if (position == this.suggestRow) {
            showAlert();
        } else if (position == this.loopRow) {
            SharedConfig.toggleLoopStickers();
            if (view instanceof MryTextCheckCell) {
                ((MryTextCheckCell) view).setChecked(SharedConfig.loopStickers);
            }
        }
    }

    private void showAlert() {
        List<String> list = new ArrayList<>();
        list.add(LocaleController.getString("SuggestStickersAll", R.string.SuggestStickersAll));
        list.add(LocaleController.getString("SuggestStickersInstalled", R.string.SuggestStickersInstalled));
        list.add(LocaleController.getString("SuggestStickersNone", R.string.SuggestStickersNone));
        DialogCommonList dialogCommonList = new DialogCommonList((Activity) getParentActivity(), list, (List<Integer>) null, Color.parseColor("#3BBCFF"), (DialogCommonList.RecyclerviewItemClickCallBack) new DialogCommonList.RecyclerviewItemClickCallBack() {
            public final void onRecyclerviewItemClick(int i) {
                StickersActivity.this.lambda$showAlert$1$StickersActivity(i);
            }
        }, 1);
        dialogCommonList.setTitle(LocaleController.getString("SuggestStickers", R.string.SuggestStickers), -7631463, 15);
        dialogCommonList.show();
    }

    public /* synthetic */ void lambda$showAlert$1$StickersActivity(int position) {
        SharedConfig.setSuggestStickers(position);
        this.listAdapter.notifyItemChanged(this.suggestRow);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (args[0].intValue() == this.currentType) {
                updateRows();
            }
        } else if (id == NotificationCenter.featuredStickersDidLoad) {
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyItemChanged(0);
            }
        } else if (id == NotificationCenter.archivedStickersCountDidLoad && args[0].intValue() == this.currentType) {
            updateRows();
        }
    }

    /* access modifiers changed from: private */
    public void sendReorder() {
        if (this.needReorder) {
            MediaDataController.getInstance(this.currentAccount).calcNewHash(this.currentType);
            this.needReorder = false;
            TLRPC.TL_messages_reorderStickerSets req = new TLRPC.TL_messages_reorderStickerSets();
            req.masks = this.currentType == 1;
            ArrayList<TLRPC.TL_messages_stickerSet> arrayList = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int a = 0; a < arrayList.size(); a++) {
                req.order.add(Long.valueOf(arrayList.get(a).set.id));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, $$Lambda$StickersActivity$Ev4y10K9LswUiFBpS5iqz7YFEqg.INSTANCE);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(this.currentType));
        }
    }

    static /* synthetic */ void lambda$sendReorder$2(TLObject response, TLRPC.TL_error error) {
    }

    private void updateRows() {
        this.rowCount = 0;
        this.suggestInfoRow = -1;
        this.masksInfoRow = -1;
        this.yourStickerBagInfoRow = -1;
        if (this.currentType == 0) {
            int i = 0 + 1;
            this.rowCount = i;
            this.suggestRow = 0;
            int i2 = i + 1;
            this.rowCount = i2;
            this.featuredRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.masksRow = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.loopRow = i3;
            this.rowCount = i4 + 1;
            this.loopInfoRow = i4;
        } else {
            this.featuredRow = -1;
            this.featuredInfoRow = -1;
            this.masksRow = -1;
            this.loopRow = -1;
        }
        if (MediaDataController.getInstance(this.currentAccount).getArchivedStickersCount(this.currentType) != 0) {
            int i5 = this.rowCount;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.archivedRow = i5;
            this.rowCount = i6 + 1;
            this.archivedInfoRow = i6;
        } else {
            this.archivedRow = -1;
            this.archivedInfoRow = -1;
        }
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
        if (!stickerSets.isEmpty()) {
            if (this.currentType == 0) {
                int i7 = this.rowCount;
                this.rowCount = i7 + 1;
                this.yourStickerBagInfoRow = i7;
            }
            int i8 = this.rowCount;
            this.stickersStartRow = i8;
            this.stickersEndRow = i8 + stickerSets.size();
            int size = this.rowCount + stickerSets.size();
            this.rowCount = size;
            if (this.currentType == 0) {
                this.rowCount = size + 1;
                this.featuredInfoRow = size;
            }
        } else {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.featuredInfoRow = -1;
        }
        if (this.currentType == 1) {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.masksInfoRow = i9;
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
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
            return StickersActivity.this.rowCount;
        }

        public long getItemId(int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return MediaDataController.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType).get(i - StickersActivity.this.stickersStartRow).set.id;
            }
            if (i == StickersActivity.this.suggestRow || i == StickersActivity.this.suggestInfoRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.featuredRow || i == StickersActivity.this.featuredInfoRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.masksInfoRow) {
                return -2147483648L;
            }
            return (long) i;
        }

        private void processSelectionOption(int which, TLRPC.TL_messages_stickerSet stickerSet) {
            if (which == 0) {
                MediaDataController.getInstance(StickersActivity.this.currentAccount).removeStickersSet(StickersActivity.this.getParentActivity(), stickerSet.set, !stickerSet.set.archived ? 1 : 2, StickersActivity.this, true);
            } else if (which == 1) {
                MediaDataController.getInstance(StickersActivity.this.currentAccount).removeStickersSet(StickersActivity.this.getParentActivity(), stickerSet.set, 0, StickersActivity.this, true);
            } else if (which == 2) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    Locale locale = Locale.US;
                    intent.putExtra("android.intent.extra.TEXT", String.format(locale, "https://" + MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix + "/addstickers/%s", new Object[]{stickerSet.set.short_name}));
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", R.string.StickersShare)), 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (which == 3) {
                try {
                    Locale locale2 = Locale.US;
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(locale2, "https://" + MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix + "/addstickers/%s", new Object[]{stickerSet.set.short_name})));
                    ToastUtils.show((int) R.string.LinkCopied);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int[] rightColors;
            String[] rightTexts;
            int[] options;
            int[] rightTextColors;
            String value;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                SwipeLayout swipeLayout = (SwipeLayout) viewHolder.itemView;
                swipeLayout.setItemWidth(AndroidUtilities.dp(80.0f));
                StickerSetCell stickerSetCell = (StickerSetCell) swipeLayout.getMainLayout();
                ArrayList<TLRPC.TL_messages_stickerSet> arrayList = MediaDataController.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType);
                int row = i - StickersActivity.this.stickersStartRow;
                stickerSetCell.setStickersSet(arrayList.get(row), row != arrayList.size() + -1);
                StickersActivity.this.sendReorder();
                TLRPC.TL_messages_stickerSet stickerSet = stickerSetCell.getStickersSet();
                if (stickerSet == null || stickerSet.set == null || !stickerSet.set.official) {
                    options = new int[]{3, 2, 1, 0};
                    rightTexts = new String[]{LocaleController.getString("StickersCopy", R.string.StickersCopy), LocaleController.getString("StickersShare", R.string.StickersShare), LocaleController.getString("StickersRemove", R.string.StickersRemove), LocaleController.getString("StickersHide", R.string.StickersHide)};
                    rightColors = new int[]{-1250068, -28928, -2818048, -16540699};
                    rightTextColors = new int[]{-4539718, -1, -1, -1};
                } else {
                    options = new int[]{0};
                    rightTexts = new String[]{LocaleController.getString("StickersRemove", R.string.StickersHide)};
                    rightColors = new int[]{-16540699};
                    rightTextColors = new int[]{-1};
                }
                swipeLayout.setRightTexts(rightTexts);
                swipeLayout.setRightTextColors(rightTextColors);
                swipeLayout.setRightColors(rightColors);
                swipeLayout.setTextSize(AndroidUtilities.sp2px(14.0f));
                swipeLayout.rebuildLayout();
                swipeLayout.setOnSwipeItemClickListener(new SwipeLayout.OnSwipeItemClickListener(options, stickerSet) {
                    private final /* synthetic */ int[] f$1;
                    private final /* synthetic */ TLRPC.TL_messages_stickerSet f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onSwipeItemClick(boolean z, int i) {
                        StickersActivity.ListAdapter.this.lambda$onBindViewHolder$0$StickersActivity$ListAdapter(this.f$1, this.f$2, z, i);
                    }
                });
                if (StickersActivity.this.stickersEndRow - StickersActivity.this.stickersStartRow == 1) {
                    viewHolder.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i == StickersActivity.this.stickersStartRow) {
                    SwipeLayout swipeLayout2 = swipeLayout;
                    viewHolder.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else {
                    if (i == StickersActivity.this.stickersEndRow - 1) {
                        viewHolder.itemView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    }
                }
            } else if (itemViewType == 1) {
                if (i == StickersActivity.this.featuredInfoRow) {
                    String text = LocaleController.getString("FeaturedStickersInfo", R.string.FeaturedStickersInfo);
                    int index = text.indexOf("@stickers");
                    if (index != -1) {
                        try {
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                            stringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                                public void onClick(View widget) {
                                    MessagesController.getInstance(StickersActivity.this.currentAccount).openByUserName("stickers", StickersActivity.this, 1);
                                }
                            }, index, "@stickers".length() + index, 18);
                            ((TextInfoPrivacyCell) viewHolder.itemView).setText(stringBuilder);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                            ((TextInfoPrivacyCell) viewHolder.itemView).setText(text);
                        }
                    } else {
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(text);
                    }
                } else if (i == StickersActivity.this.archivedInfoRow) {
                    if (StickersActivity.this.currentType == 0) {
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedStickersInfo", R.string.ArchivedStickersInfo));
                    } else {
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedMasksInfo", R.string.ArchivedMasksInfo));
                    }
                } else if (i == StickersActivity.this.masksInfoRow) {
                    ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("MasksInfo", R.string.MasksInfo));
                } else if (i == StickersActivity.this.loopInfoRow) {
                    ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("StickerLoopAnimatorPlay", R.string.StickerLoopAnimatorPlay));
                } else if (i == StickersActivity.this.yourStickerBagInfoRow) {
                    ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("YourStickerPackage", R.string.YourStickerPackage));
                }
                viewHolder.itemView.setClickable(true);
                viewHolder.itemView.setFocusable(true);
                viewHolder.itemView.setFocusableInTouchMode(true);
            } else if (itemViewType != 2) {
                if (itemViewType == 4 && i == StickersActivity.this.loopRow) {
                    ((MryTextCheckCell) viewHolder.itemView).setTextAndCheck(LocaleController.getString("LoopAnimatedStickers", R.string.LoopAnimatedStickers), SharedConfig.loopStickers, false);
                    viewHolder.itemView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            } else if (i == StickersActivity.this.featuredRow) {
                int count = MediaDataController.getInstance(StickersActivity.this.currentAccount).getUnreadStickerSets().size();
                ((TextSettingCell) viewHolder.itemView).setTextAndValue(LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers), count != 0 ? String.format("%d", new Object[]{Integer.valueOf(count)}) : "", true, true);
            } else if (i == StickersActivity.this.archivedRow) {
                if (StickersActivity.this.currentType == 0) {
                    ((TextSettingCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers), false);
                } else {
                    ((TextSettingCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks), false);
                }
            } else if (i == StickersActivity.this.masksRow) {
                ((TextSettingCell) viewHolder.itemView).setText(LocaleController.getString("Masks", R.string.Masks), true, true);
            } else if (i == StickersActivity.this.suggestRow) {
                int i2 = SharedConfig.suggestStickers;
                if (i2 == 0) {
                    value = LocaleController.getString("SuggestStickersAll", R.string.SuggestStickersAll);
                } else if (i2 != 1) {
                    value = LocaleController.getString("SuggestStickersNone", R.string.SuggestStickersNone);
                } else {
                    value = LocaleController.getString("SuggestStickersInstalled", R.string.SuggestStickersInstalled);
                }
                ((TextSettingCell) viewHolder.itemView).setTextAndValue(LocaleController.getString("SuggestStickers", R.string.SuggestStickers), value, true, true);
                viewHolder.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
            }
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$StickersActivity$ListAdapter(int[] options, TLRPC.TL_messages_stickerSet stickerSet, boolean left, int index) {
            processSelectionOption(options[index], stickerSet);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2 || type == 4;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new SwipeLayout(this.mContext) {
                    public boolean onTouchEvent(MotionEvent event) {
                        if (isExpanded()) {
                            return true;
                        }
                        return super.onTouchEvent(event);
                    }
                };
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((SwipeLayout) view).setUpView(new StickerSetCell(this.mContext, 0));
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else if (viewType == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (viewType == 2) {
                view = new TextSettingCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 3) {
                view = new ShadowSectionCell(this.mContext);
            } else if (viewType == 4) {
                view = new MryTextCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == StickersActivity.this.featuredInfoRow || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.masksInfoRow || i == StickersActivity.this.loopInfoRow || i == StickersActivity.this.yourStickerBagInfoRow) {
                return 1;
            }
            if (i == StickersActivity.this.featuredRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.suggestRow) {
                return 2;
            }
            if (i == StickersActivity.this.suggestInfoRow) {
                return 3;
            }
            if (i == StickersActivity.this.loopRow) {
                return 4;
            }
            return 0;
        }

        public void swapElements(int fromIndex, int toIndex) {
            if (fromIndex != toIndex) {
                boolean unused = StickersActivity.this.needReorder = true;
            }
            ArrayList<TLRPC.TL_messages_stickerSet> arrayList = MediaDataController.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType);
            arrayList.set(fromIndex - StickersActivity.this.stickersStartRow, arrayList.get(toIndex - StickersActivity.this.stickersStartRow));
            arrayList.set(toIndex - StickersActivity.this.stickersStartRow, arrayList.get(fromIndex - StickersActivity.this.stickersStartRow));
            notifyItemMoved(fromIndex, toIndex);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class, TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteLinkText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_stickers_menuSelector), new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_stickers_menu)};
    }
}
