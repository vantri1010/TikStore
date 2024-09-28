package im.bclpbkiauv.ui.fragments.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DialogObject;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cell.FmtDialogCell;
import im.bclpbkiauv.ui.cells.DialogMeUrlCell;
import im.bclpbkiauv.ui.cells.DialogsEmptyCell;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.fragments.DialogsFragment;
import im.bclpbkiauv.ui.hviews.slidemenu.SwipeLayout;
import java.util.ArrayList;
import java.util.Collections;

public class FmtDialogsAdapter extends RecyclerListView.SelectionAdapter {
    private ArrayList<Long> allDialogIdsList;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private int currentCount;
    private int dCount;
    private FmtDialogDelegate delegate;
    /* access modifiers changed from: private */
    public boolean dialogsListFrozen;
    /* access modifiers changed from: private */
    public int dialogsType;
    private int folderId;
    private boolean hasHints;
    private boolean isEdit;
    private boolean isReordering;
    private Context mContext;
    private long openedDialogId;
    private ArrayList<Long> selectedDialogs;

    public interface FmtDialogDelegate {
        void onItemMenuClick(boolean z, int i, long j, int i2);
    }

    public void setEdit(boolean edit) {
        this.isEdit = edit;
    }

    public void setDelegate(FmtDialogDelegate delegate2) {
        this.delegate = delegate2;
    }

    public FmtDialogsAdapter(Context context, int dialogsType2, int folder) {
        this.mContext = context;
        this.folderId = folder;
        this.dialogsType = dialogsType2;
        this.selectedDialogs = new ArrayList<>();
    }

    public void setDialogsType(int type) {
        this.dialogsType = type;
    }

    public void setOpenedDialogId(long id) {
        this.openedDialogId = id;
    }

    public boolean addOrRemoveSelectedDialog(long did, View cell) {
        if (this.selectedDialogs.contains(Long.valueOf(did))) {
            this.selectedDialogs.remove(Long.valueOf(did));
            if (cell instanceof FmtDialogCell) {
                ((FmtDialogCell) cell).setChecked(false, true);
            }
            return false;
        }
        this.selectedDialogs.add(Long.valueOf(did));
        if (cell instanceof FmtDialogCell) {
            ((FmtDialogCell) cell).setChecked(true, true);
        }
        return true;
    }

    public ArrayList<Long> getSelectedDialogs() {
        return this.selectedDialogs;
    }

    public ArrayList<Long> getAllDialogIdsList() {
        ArrayList<Long> arrayList = this.allDialogIdsList;
        if (arrayList == null) {
            this.allDialogIdsList = new ArrayList<>();
        } else {
            arrayList.clear();
        }
        ArrayList<TLRPC.Dialog> arrayList2 = DialogsFragment.getDialogsArray(this.currentAccount, this.dialogsType, 0, this.dialogsListFrozen);
        int i = 0;
        while (i < getItemCount()) {
            boolean loopNetIndex = false;
            if (this.hasHints) {
                int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
                if (i < count + 2) {
                    this.allDialogIdsList.add(Long.valueOf((long) MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1).chat_id));
                    loopNetIndex = true;
                } else {
                    i -= count + 2;
                }
            }
            if (!loopNetIndex && i >= 0 && i < arrayList2.size()) {
                this.allDialogIdsList.add(Long.valueOf(arrayList2.get(i).id));
            }
            i++;
        }
        return this.allDialogIdsList;
    }

    public void onReorderStateChanged(boolean reordering) {
        this.isReordering = reordering;
    }

    public int fixPosition(int position) {
        if (this.hasHints) {
            return position - (MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2);
        }
        return position;
    }

    public boolean isDataSetChanged() {
        int current = this.currentCount;
        return (current == getItemCount() && this.dCount == this.dCount && current != 1) ? false : true;
    }

    public int getItemCount() {
        int dialogsCount = DialogsFragment.getDialogsArray(this.currentAccount, this.dialogsType, 0, this.dialogsListFrozen).size();
        this.dCount = dialogsCount;
        if (dialogsCount != 0 || !MessagesController.getInstance(this.currentAccount).isLoadingDialogs(0)) {
            int count = dialogsCount;
            if (!MessagesController.getInstance(this.currentAccount).isDialogsEndReached(0) || dialogsCount == 0) {
                count++;
            }
            if (this.hasHints) {
                count += MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
            } else if (dialogsCount == 0 && ContactsController.getInstance(this.currentAccount).contacts.isEmpty() && ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
                this.currentCount = 0;
                return 0;
            }
            this.currentCount = count;
            return count;
        }
        this.currentCount = 0;
        return 0;
    }

    public TLObject getItem(int i) {
        ArrayList<TLRPC.Dialog> arrayList = DialogsFragment.getDialogsArray(this.currentAccount, this.dialogsType, 0, this.dialogsListFrozen);
        if (this.hasHints) {
            int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            if (i < count + 2) {
                return MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1);
            }
            i -= count + 2;
        }
        if (i < 0 || i >= arrayList.size()) {
            return null;
        }
        return arrayList.get(i);
    }

    public void setDialogsListFrozen(boolean frozen) {
        this.dialogsListFrozen = frozen;
    }

    public void notifyDataSetChanged() {
        this.hasHints = !MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty();
        super.notifyDataSetChanged();
    }

    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof SwipeLayout) {
            FmtDialogCell dialogCell = (FmtDialogCell) ((SwipeLayout) holder.itemView).getMainLayout();
            dialogCell.onReorderStateChanged(this.isReordering, false);
            dialogCell.setDialogIndex(fixPosition(holder.getAdapterPosition()));
            dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialogCell.getDialogId())), false);
        }
    }

    public boolean hasSelectedDialogs() {
        ArrayList<Long> arrayList = this.selectedDialogs;
        return arrayList != null && !arrayList.isEmpty();
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int viewType = holder.getItemViewType();
        return (viewType == 1 || viewType == 5 || viewType == 3 || viewType == 8 || viewType == 7 || viewType == 9 || viewType == 10) ? false : true;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        int i = viewType;
        int i2 = -2;
        if (i == 0) {
            view = new SwipeLayout(this.mContext) {
                public boolean onTouchEvent(MotionEvent event) {
                    if (isExpanded()) {
                        return true;
                    }
                    return super.onTouchEvent(event);
                }
            };
            ((ViewGroup) view).setClipChildren(false);
            ((SwipeLayout) view).setUpView(new FmtDialogCell(this.mContext, false));
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        } else if (i != 1) {
            int i3 = 3;
            if (i == 2) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setText(LocaleController.getString("RecentlyViewed", R.string.RecentlyViewed));
                TextView textView = new TextView(this.mContext);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
                textView.setText(LocaleController.getString("RecentlyViewedHide", R.string.RecentlyViewedHide));
                textView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
                if (!LocaleController.isRTL) {
                    i3 = 5;
                }
                headerCell.addView(textView, LayoutHelper.createFrame(-1.0f, -1.0f, i3 | 48, 17.0f, 15.0f, 17.0f, 0.0f));
                textView.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        FmtDialogsAdapter.this.lambda$onCreateViewHolder$0$FmtDialogsAdapter(view);
                    }
                });
                view = headerCell;
                if (i == 5) {
                    i2 = -1;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, i2));
            } else if (i == 3) {
                FrameLayout frameLayout = new FrameLayout(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), 1073741824));
                    }
                };
                frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                View v = new View(this.mContext);
                v.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                frameLayout.addView(v, LayoutHelper.createFrame(-1, -1.0f));
                View view2 = frameLayout;
                if (i == 5) {
                    i2 = -1;
                }
                view2.setLayoutParams(new RecyclerView.LayoutParams(-1, i2));
                view = view2;
            } else if (i == 4) {
                view = new DialogMeUrlCell(this.mContext);
                if (i == 5) {
                    i2 = -1;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, i2));
            } else if (i == 5) {
                view = new DialogsEmptyCell(this.mContext);
                if (i == 5) {
                    i2 = -1;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, i2));
            } else if (i != 11) {
                view = new View(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int height;
                        View parent;
                        int i = 0;
                        int size = DialogsFragment.getDialogsArray(FmtDialogsAdapter.this.currentAccount, FmtDialogsAdapter.this.dialogsType, 0, FmtDialogsAdapter.this.dialogsListFrozen).size();
                        boolean hasArchive = MessagesController.getInstance(FmtDialogsAdapter.this.currentAccount).dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
                        if (size == 0 || !hasArchive) {
                            height = 0;
                        } else {
                            int height2 = View.MeasureSpec.getSize(heightMeasureSpec);
                            if (height2 == 0 && (parent = (View) getParent()) != null) {
                                height2 = parent.getMeasuredHeight();
                            }
                            if (height2 == 0) {
                                int currentActionBarHeight = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();
                                if (Build.VERSION.SDK_INT >= 21) {
                                    i = AndroidUtilities.statusBarHeight;
                                }
                                height2 = currentActionBarHeight - i;
                            }
                            int cellHeight = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 68.0f : 62.0f);
                            int dialogsHeight = (size * cellHeight) + (size - 1);
                            if (dialogsHeight < height2) {
                                height = (height2 - dialogsHeight) + cellHeight + 1;
                            } else if (dialogsHeight - height2 < cellHeight + 1) {
                                height = (cellHeight + 1) - (dialogsHeight - height2);
                            } else {
                                height = 0;
                            }
                        }
                        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), height);
                    }
                };
                if (i == 5) {
                    i2 = -1;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, i2));
            } else {
                view = new EmptyCell(this.mContext, AndroidUtilities.dp(46.0f));
                if (i == 5) {
                    i2 = -1;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, i2));
            }
        } else {
            view = new LoadingCell(this.mContext);
            if (i == 5) {
                i2 = -1;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, i2));
        }
        return new RecyclerListView.Holder(view);
    }

    public /* synthetic */ void lambda$onCreateViewHolder$0$FmtDialogsAdapter(View view1) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        Drawable bg;
        int i2;
        int i3;
        RecyclerView.ViewHolder viewHolder = holder;
        int i4 = i;
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 0) {
            SwipeLayout swipeLayout = (SwipeLayout) viewHolder.itemView;
            swipeLayout.setItemWidth(AndroidUtilities.dp(65.0f));
            FmtDialogCell cell = (FmtDialogCell) swipeLayout.getMainLayout();
            cell.setCheckBoxVisible(this.isEdit, true, i4);
            int radius = AndroidUtilities.dp(5.0f);
            if (getItemCount() == 1) {
                bg = Theme.createRoundRectDrawable((float) radius, Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (i4 != 0 && i4 != getItemCount() - 1) {
                bg = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (i4 == 0) {
                bg = Theme.createRoundRectDrawable((float) radius, (float) radius, 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite));
            } else {
                bg = Theme.createRoundRectDrawable(0.0f, 0.0f, (float) radius, (float) radius, Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            swipeLayout.setBackground(this.isEdit ? bg : null);
            cell.setBackground(bg);
            TLRPC.Dialog dialog = (TLRPC.Dialog) getItem(i4);
            int lower_id = (int) dialog.id;
            int high_id = (int) (dialog.id >> 32);
            boolean isChat = lower_id < 0 && high_id != 1;
            int[] leftColors = {-6710887, -16725760};
            int[] iArr = new int[2];
            Drawable drawable = bg;
            iArr[0] = MessagesController.getInstance(UserConfig.selectedAccount).isDialogMuted(dialog.id) ? R.drawable.msg_unmute : R.drawable.msg_mute;
            iArr[1] = dialog.pinned ? R.drawable.msg_unpin : R.drawable.msg_pin;
            int[] leftIcons = iArr;
            int[] leftIconColors = {-1, -1};
            String[] strArr = new String[2];
            boolean z = isChat;
            strArr[0] = LocaleController.getString(MessagesController.getInstance(UserConfig.selectedAccount).isDialogMuted(dialog.id) ? R.string.ChatsUnmute : R.string.ChatsMute);
            if (dialog.pinned) {
                i2 = R.string.UnpinFromTop;
            } else {
                i2 = R.string.PinToTop;
            }
            strArr[1] = LocaleController.getString(i2);
            String[] leftTexts = strArr;
            int[] leftTextColors = {-1, -1};
            int[] rightColors = {-6908264, -570319};
            int[] rightIcons = new int[2];
            rightIcons[0] = dialog.unread_count != 0 ? R.drawable.msg_markread : R.drawable.msg_markunread;
            rightIcons[1] = R.drawable.msg_delete;
            int i5 = radius;
            int[] rightIconColors = {-1, -1};
            int i6 = high_id;
            String[] rightTexts = new String[2];
            if (dialog.unread_count != 0) {
                i3 = R.string.MarkAsRead;
            } else {
                i3 = R.string.MarkAsUnread;
            }
            rightTexts[0] = LocaleController.getString(i3);
            rightTexts[1] = LocaleController.getString(R.string.Delete);
            swipeLayout.setLeftIcons(leftIcons);
            swipeLayout.setLeftIconColors(leftIconColors);
            swipeLayout.setLeftTexts(leftTexts);
            swipeLayout.setLeftTextColors(leftTextColors);
            swipeLayout.setLeftColors(leftColors);
            swipeLayout.setRightIcons(rightIcons);
            swipeLayout.setRightIconColors(rightIconColors);
            swipeLayout.setRightTexts(rightTexts);
            swipeLayout.setRightTextColors(-1, -1);
            swipeLayout.setRightColors(rightColors);
            int[] iArr2 = rightIcons;
            swipeLayout.setCanFullSwipeFromRight(true);
            swipeLayout.setCanFullSwipeFromLeft(true);
            swipeLayout.setIconSize(AndroidUtilities.dp(24.0f));
            swipeLayout.setTextSize(AndroidUtilities.sp2px(12.0f));
            swipeLayout.setAutoHideSwipe(true);
            swipeLayout.setOnlyOneSwipe(true);
            swipeLayout.rebuildLayout();
            swipeLayout.setOnSwipeItemClickListener(new SwipeLayout.OnSwipeItemClickListener(i4) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onSwipeItemClick(boolean z, int i) {
                    FmtDialogsAdapter.this.lambda$onBindViewHolder$1$FmtDialogsAdapter(this.f$1, z, i);
                }
            });
            TLRPC.Dialog nextDialog = (TLRPC.Dialog) getItem(i4 + 1);
            SwipeLayout swipeLayout2 = swipeLayout;
            cell.useSeparator = i4 != getItemCount() + -1;
            cell.fullSeparator = dialog.pinned && nextDialog != null && !nextDialog.pinned;
            if (AndroidUtilities.isTablet()) {
                TLRPC.Dialog dialog2 = nextDialog;
                int[] iArr3 = rightIconColors;
                int[] iArr4 = leftIcons;
                cell.setDialogSelected(dialog.id == this.openedDialogId);
            } else {
                int[] iArr5 = rightIconColors;
                int[] iArr6 = leftIcons;
            }
            cell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialog.id)), false);
            cell.setDialog(dialog, this.dialogsType, 0);
        } else if (itemViewType == 4) {
            ((DialogMeUrlCell) viewHolder.itemView).setRecentMeUrl((TLRPC.RecentMeUrl) getItem(i4));
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$1$FmtDialogsAdapter(int i, boolean left, int index) {
        if (this.delegate != null) {
            this.delegate.onItemMenuClick(left, index, ((TLRPC.Dialog) getItem(i)).id, i);
        }
    }

    public int getItemViewType(int i) {
        if (this.hasHints) {
            int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            if (i >= count + 2) {
                i -= count + 2;
            } else if (i == 0) {
                return 2;
            } else {
                if (i == count + 1) {
                    return 3;
                }
                return 4;
            }
        }
        int size = DialogsFragment.getDialogsArray(this.currentAccount, this.dialogsType, 0, this.dialogsListFrozen).size();
        if (i == size) {
            if (!MessagesController.getInstance(this.currentAccount).isDialogsEndReached(0)) {
                return 1;
            }
            if (size == 0) {
                return 5;
            }
            return 10;
        } else if (i > size) {
            return 10;
        } else {
            return 0;
        }
    }

    public void notifyItemMoved(int fromPosition, int toPosition) {
        ArrayList<TLRPC.Dialog> dialogs = DialogsFragment.getDialogsArray(this.currentAccount, this.dialogsType, 0, false);
        int fromIndex = fixPosition(fromPosition);
        int toIndex = fixPosition(toPosition);
        TLRPC.Dialog fromDialog = dialogs.get(fromIndex);
        TLRPC.Dialog toDialog = dialogs.get(toIndex);
        int oldNum = fromDialog.pinnedNum;
        fromDialog.pinnedNum = toDialog.pinnedNum;
        toDialog.pinnedNum = oldNum;
        Collections.swap(dialogs, fromIndex, toIndex);
        super.notifyItemMoved(fromPosition, toPosition);
    }
}
