package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DialogObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.ArchiveHintCell;
import im.bclpbkiauv.ui.cells.DialogCell;
import im.bclpbkiauv.ui.cells.DialogMeUrlCell;
import im.bclpbkiauv.ui.cells.DialogsEmptyCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DialogsAdapter extends RecyclerListView.SelectionAdapter {
    private ArchiveHintCell archiveHintCell;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private int currentCount;
    /* access modifiers changed from: private */
    public boolean dialogsListFrozen;
    /* access modifiers changed from: private */
    public int dialogsType;
    /* access modifiers changed from: private */
    public int folderId;
    private boolean hasHints;
    private boolean isOnlySelect;
    private boolean isReordering;
    private long lastSortTime;
    private Context mContext;
    private ArrayList<TLRPC.Contact> onlineContacts;
    private long openedDialogId;
    private int prevContactsCount;
    private ArrayList<Long> selectedDialogs;
    private boolean showArchiveHint;

    public DialogsAdapter(Context context, int type, int folder, boolean onlySelect) {
        this.mContext = context;
        this.dialogsType = type;
        this.folderId = folder;
        this.isOnlySelect = onlySelect;
        this.hasHints = folder == 0 && type == 0 && !onlySelect;
        this.selectedDialogs = new ArrayList<>();
        if (this.folderId == 1) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            this.showArchiveHint = preferences.getBoolean("archivehint", true);
            preferences.edit().putBoolean("archivehint", false).commit();
            if (this.showArchiveHint) {
                this.archiveHintCell = new ArchiveHintCell(context);
            }
        }
    }

    public void setOpenedDialogId(long id) {
        this.openedDialogId = id;
    }

    public boolean hasSelectedDialogs() {
        ArrayList<Long> arrayList = this.selectedDialogs;
        return arrayList != null && !arrayList.isEmpty();
    }

    public boolean addOrRemoveSelectedDialog(long did, View cell) {
        if (this.selectedDialogs.contains(Long.valueOf(did))) {
            this.selectedDialogs.remove(Long.valueOf(did));
            if (cell instanceof DialogCell) {
                ((DialogCell) cell).setChecked(false, true);
            }
            return false;
        }
        this.selectedDialogs.add(Long.valueOf(did));
        if (cell instanceof DialogCell) {
            ((DialogCell) cell).setChecked(true, true);
        }
        return true;
    }

    public ArrayList<Long> getSelectedDialogs() {
        return this.selectedDialogs;
    }

    public void onReorderStateChanged(boolean reordering) {
        this.isReordering = reordering;
    }

    public int fixPosition(int position) {
        if (this.hasHints) {
            position -= MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
        }
        if (this.showArchiveHint) {
            return position - 2;
        }
        return position;
    }

    public boolean isDataSetChanged() {
        int current = this.currentCount;
        return current != getItemCount() || current == 1;
    }

    public int getItemCount() {
        int dialogsCount = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
        if (dialogsCount != 0 || (this.folderId == 0 && !MessagesController.getInstance(this.currentAccount).isLoadingDialogs(this.folderId))) {
            int count = dialogsCount;
            if (!MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId) || dialogsCount == 0) {
                count++;
            }
            boolean hasContacts = false;
            if (this.hasHints) {
                count += MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
            } else if (this.dialogsType == 0 && dialogsCount == 0 && this.folderId == 0) {
                if (ContactsController.getInstance(this.currentAccount).contacts.isEmpty() && ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
                    this.onlineContacts = null;
                    this.currentCount = 0;
                    return 0;
                } else if (!ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
                    if (this.onlineContacts == null || this.prevContactsCount != ContactsController.getInstance(this.currentAccount).contacts.size()) {
                        ArrayList<TLRPC.Contact> arrayList = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                        this.onlineContacts = arrayList;
                        this.prevContactsCount = arrayList.size();
                        int selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
                        int a = 0;
                        int N = this.onlineContacts.size();
                        while (true) {
                            if (a >= N) {
                                break;
                            } else if (this.onlineContacts.get(a).user_id == selfId) {
                                this.onlineContacts.remove(a);
                                break;
                            } else {
                                a++;
                            }
                        }
                        sortOnlineContacts(false);
                    }
                    count += this.onlineContacts.size() + 2;
                    hasContacts = true;
                }
            }
            if (!hasContacts && this.onlineContacts != null) {
                this.onlineContacts = null;
            }
            if (this.folderId == 1 && this.showArchiveHint) {
                count += 2;
            }
            if (this.folderId == 0 && dialogsCount != 0) {
                count++;
            }
            this.currentCount = count;
            return count;
        }
        this.onlineContacts = null;
        if (this.folderId != 1 || !this.showArchiveHint) {
            this.currentCount = 0;
            return 0;
        }
        this.currentCount = 2;
        return 2;
    }

    public TLObject getItem(int i) {
        ArrayList<TLRPC.Contact> arrayList = this.onlineContacts;
        if (arrayList != null) {
            int i2 = i - 3;
            if (i2 < 0 || i2 >= arrayList.size()) {
                return null;
            }
            return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(i2).user_id));
        }
        if (this.showArchiveHint) {
            i -= 2;
        }
        ArrayList<TLRPC.Dialog> arrayList2 = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
        if (this.hasHints) {
            int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            if (i < count + 2) {
                return MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1);
            }
            i -= count + 2;
        }
        if (i < 0 || i >= arrayList2.size()) {
            return null;
        }
        return arrayList2.get(i);
    }

    public void sortOnlineContacts(boolean notify) {
        if (this.onlineContacts == null) {
            return;
        }
        if (!notify || SystemClock.uptimeMillis() - this.lastSortTime >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
            this.lastSortTime = SystemClock.uptimeMillis();
            try {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                Collections.sort(this.onlineContacts, new Comparator(currentTime) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return DialogsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, this.f$1, (TLRPC.Contact) obj, (TLRPC.Contact) obj2);
                    }
                });
                if (notify) {
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ int lambda$sortOnlineContacts$0(MessagesController messagesController, int currentTime, TLRPC.Contact o1, TLRPC.Contact o2) {
        TLRPC.User user1 = messagesController.getUser(Integer.valueOf(o2.user_id));
        TLRPC.User user2 = messagesController.getUser(Integer.valueOf(o1.user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                    return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
                }
                return -1;
            } else if (status1 > status2) {
                return 1;
            } else {
                return status1 < status2 ? -1 : 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            return status1 < status2 ? -1 : 0;
        }
    }

    public void setDialogsListFrozen(boolean frozen) {
        this.dialogsListFrozen = frozen;
    }

    public ViewPager getArchiveHintCellPager() {
        ArchiveHintCell archiveHintCell2 = this.archiveHintCell;
        if (archiveHintCell2 != null) {
            return archiveHintCell2.getViewPager();
        }
        return null;
    }

    public void notifyDataSetChanged() {
        this.hasHints = this.folderId == 0 && this.dialogsType == 0 && !this.isOnlySelect && !MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty();
        super.notifyDataSetChanged();
    }

    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof DialogCell) {
            DialogCell dialogCell = (DialogCell) holder.itemView;
            dialogCell.onReorderStateChanged(this.isReordering, false);
            dialogCell.setDialogIndex(fixPosition(holder.getAdapterPosition()));
            dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialogCell.getDialogId())), false);
        }
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int viewType = holder.getItemViewType();
        return (viewType == 1 || viewType == 5 || viewType == 3 || viewType == 8 || viewType == 7 || viewType == 9 || viewType == 10) ? false : true;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                View view2 = new DialogCell(this.mContext, true, false);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
                break;
            case 1:
                view = new LoadingCell(this.mContext);
                break;
            case 2:
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setText(LocaleController.getString("RecentlyViewed", R.string.RecentlyViewed));
                TextView textView = new TextView(this.mContext);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
                textView.setText(LocaleController.getString("RecentlyViewedHide", R.string.RecentlyViewedHide));
                int i = 3;
                textView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
                if (!LocaleController.isRTL) {
                    i = 5;
                }
                headerCell.addView(textView, LayoutHelper.createFrame(-1.0f, -1.0f, i | 48, 17.0f, 15.0f, 17.0f, 0.0f));
                textView.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        DialogsAdapter.this.lambda$onCreateViewHolder$1$DialogsAdapter(view);
                    }
                });
                view = headerCell;
                break;
            case 3:
                AnonymousClass1 r0 = new FrameLayout(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), 1073741824));
                    }
                };
                r0.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                r0.addView(new View(this.mContext), LayoutHelper.createFrame(-1, -1.0f));
                AnonymousClass1 r4 = r0;
                view = r0;
                break;
            case 4:
                view = new DialogMeUrlCell(this.mContext);
                break;
            case 5:
                view = new DialogsEmptyCell(this.mContext);
                break;
            case 6:
                view = new UserCell(this.mContext, 8, 0, false);
                break;
            case 7:
                HeaderCell headerCell2 = new HeaderCell(this.mContext);
                headerCell2.setText(LocaleController.getString("YourContacts", R.string.YourContacts));
                HeaderCell headerCell3 = headerCell2;
                view = headerCell2;
                break;
            case 8:
                view = new ShadowSectionCell(this.mContext);
                break;
            case 9:
                View view3 = this.archiveHintCell;
                view = view3;
                if (this.archiveHintCell.getParent() != null) {
                    ((ViewGroup) this.archiveHintCell.getParent()).removeView(this.archiveHintCell);
                    view = view3;
                    break;
                }
                break;
            default:
                view = new View(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int height;
                        View parent;
                        int size = DialogsActivity.getDialogsArray(DialogsAdapter.this.currentAccount, DialogsAdapter.this.dialogsType, DialogsAdapter.this.folderId, DialogsAdapter.this.dialogsListFrozen).size();
                        int i = 0;
                        boolean hasArchive = MessagesController.getInstance(DialogsAdapter.this.currentAccount).dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
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
                            int cellHeight = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
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
                break;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, viewType == 5 ? -1 : -2));
        return new RecyclerListView.Holder(view);
    }

    public /* synthetic */ void lambda$onCreateViewHolder$1$DialogsAdapter(View view1) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        int itemViewType = holder.getItemViewType();
        int i2 = 0;
        boolean z = true;
        if (itemViewType == 0) {
            DialogCell cell = (DialogCell) holder.itemView;
            TLRPC.Dialog dialog = (TLRPC.Dialog) getItem(i);
            TLRPC.Dialog nextDialog = (TLRPC.Dialog) getItem(i + 1);
            if (this.folderId == 0) {
                cell.useSeparator = i != getItemCount() - 2;
                if (getItemCount() == 2) {
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i == 0) {
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i == getItemCount() - 2) {
                    cell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            } else {
                cell.useSeparator = i != getItemCount() - 1;
                if (getItemCount() == 1) {
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i == 0) {
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i == getItemCount() - 1) {
                    cell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            }
            cell.fullSeparator = dialog.pinned && nextDialog != null && !nextDialog.pinned;
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                if (dialog.id != this.openedDialogId) {
                    z = false;
                }
                cell.setDialogSelected(z);
            }
            cell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialog.id)), false);
            cell.setDialog(dialog, this.dialogsType, this.folderId);
        } else if (itemViewType == 4) {
            ((DialogMeUrlCell) holder.itemView).setRecentMeUrl((TLRPC.RecentMeUrl) getItem(i));
        } else if (itemViewType == 5) {
            DialogsEmptyCell cell2 = (DialogsEmptyCell) holder.itemView;
            if (this.onlineContacts != null) {
                i2 = 1;
            }
            cell2.setType(i2);
        } else if (itemViewType == 6) {
            ((UserCell) holder.itemView).setData(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(i - 3).user_id)), (CharSequence) null, (CharSequence) null, 0);
        }
    }

    public int getItemViewType(int i) {
        if (this.onlineContacts == null) {
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
            } else if (this.showArchiveHint) {
                if (i == 0) {
                    return 9;
                }
                if (i == 1) {
                    return 8;
                }
                i -= 2;
            }
            int size = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
            if (i == size) {
                if (!MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
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
        } else if (i == 0) {
            return 5;
        } else {
            if (i == 1) {
                return 8;
            }
            if (i == 2) {
                return 7;
            }
            return 6;
        }
    }

    public void notifyItemMoved(int fromPosition, int toPosition) {
        ArrayList<TLRPC.Dialog> dialogs = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
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
