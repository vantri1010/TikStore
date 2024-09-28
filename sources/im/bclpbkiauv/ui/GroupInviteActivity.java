package im.bclpbkiauv.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.TextBlockCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;

public class GroupInviteActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int chat_id;
    /* access modifiers changed from: private */
    public int copyLinkRow;
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public TLRPC.ExportedChatInvite invite;
    /* access modifiers changed from: private */
    public int linkInfoRow;
    /* access modifiers changed from: private */
    public int linkRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    /* access modifiers changed from: private */
    public int revokeLinkRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int shadowRow;
    /* access modifiers changed from: private */
    public int shareLinkRow;

    public GroupInviteActivity(int cid) {
        this.chat_id = cid;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
        this.loading = true;
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.linkRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.linkInfoRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.copyLinkRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.revokeLinkRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.shareLinkRow = i4;
        this.rowCount = i5 + 1;
        this.shadowRow = i5;
        return true;
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLink", R.string.InviteLink));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupInviteActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                GroupInviteActivity.this.lambda$createView$1$GroupInviteActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$GroupInviteActivity(View view, int position) {
        if (getParentActivity() != null) {
            if (position == this.copyLinkRow || position == this.linkRow) {
                if (this.invite != null) {
                    try {
                        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                        ToastUtils.show((int) R.string.LinkCopied);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } else if (position == this.shareLinkRow) {
                if (this.invite != null) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        intent.putExtra("android.intent.extra.TEXT", this.invite.link);
                        getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink)), 500);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
            } else if (position == this.revokeLinkRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setMessage(LocaleController.getString("RevokeAlert", R.string.RevokeAlert));
                builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        GroupInviteActivity.this.lambda$null$0$GroupInviteActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        }
    }

    public /* synthetic */ void lambda$null$0$GroupInviteActivity(DialogInterface dialogInterface, int i) {
        generateLink(true);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            int guid = args[1].intValue();
            if (args[0].id == this.chat_id && guid == this.classGuid) {
                TLRPC.ExportedChatInvite exportedInvite = MessagesController.getInstance(this.currentAccount).getExportedInvite(this.chat_id);
                this.invite = exportedInvite;
                if (!(exportedInvite instanceof TLRPC.TL_chatInviteExported)) {
                    generateLink(false);
                    return;
                }
                this.loading = false;
                ListAdapter listAdapter2 = this.listAdapter;
                if (listAdapter2 != null) {
                    listAdapter2.notifyDataSetChanged();
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void generateLink(boolean newRequest) {
        this.loading = true;
        TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chat_id);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(newRequest) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupInviteActivity.this.lambda$generateLink$3$GroupInviteActivity(this.f$1, tLObject, tL_error);
            }
        }), this.classGuid);
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$generateLink$3$GroupInviteActivity(boolean newRequest, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, newRequest) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                GroupInviteActivity.this.lambda$null$2$GroupInviteActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$GroupInviteActivity(TLRPC.TL_error error, TLObject response, boolean newRequest) {
        if (error == null) {
            this.invite = (TLRPC.ExportedChatInvite) response;
            if (newRequest) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setMessage(LocaleController.getString("RevokeAlertNewLink", R.string.RevokeAlertNewLink));
                    builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                    builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                } else {
                    return;
                }
            }
        }
        this.loading = false;
        this.listAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == GroupInviteActivity.this.revokeLinkRow || position == GroupInviteActivity.this.copyLinkRow || position == GroupInviteActivity.this.shareLinkRow || position == GroupInviteActivity.this.linkRow;
        }

        public int getItemCount() {
            if (GroupInviteActivity.this.loading) {
                return 0;
            }
            return GroupInviteActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType != 1) {
                view = new TextBlockCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else {
                view = new TextInfoPrivacyCell(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                if (position == GroupInviteActivity.this.copyLinkRow) {
                    textCell.setText(LocaleController.getString("CopyLink", R.string.CopyLink), true);
                } else if (position == GroupInviteActivity.this.shareLinkRow) {
                    textCell.setText(LocaleController.getString("ShareLink", R.string.ShareLink), false);
                } else if (position == GroupInviteActivity.this.revokeLinkRow) {
                    textCell.setText(LocaleController.getString("RevokeLink", R.string.RevokeLink), true);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                if (position == GroupInviteActivity.this.shadowRow) {
                    privacyCell.setText("");
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                } else if (position == GroupInviteActivity.this.linkInfoRow) {
                    TLRPC.Chat chat = MessagesController.getInstance(GroupInviteActivity.this.currentAccount).getChat(Integer.valueOf(GroupInviteActivity.this.chat_id));
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        privacyCell.setText(LocaleController.getString("LinkInfo", R.string.LinkInfo));
                    } else {
                        privacyCell.setText(LocaleController.getString("ChannelLinkInfo", R.string.ChannelLinkInfo));
                    }
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                }
            } else if (itemViewType == 2) {
                ((TextBlockCell) holder.itemView).setText(GroupInviteActivity.this.invite != null ? GroupInviteActivity.this.invite.link : "error", false);
            }
        }

        public int getItemViewType(int position) {
            if (position == GroupInviteActivity.this.copyLinkRow || position == GroupInviteActivity.this.shareLinkRow || position == GroupInviteActivity.this.revokeLinkRow) {
                return 0;
            }
            if (position == GroupInviteActivity.this.shadowRow || position == GroupInviteActivity.this.linkInfoRow) {
                return 1;
            }
            if (position == GroupInviteActivity.this.linkRow) {
                return 2;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextBlockCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText)};
    }
}
