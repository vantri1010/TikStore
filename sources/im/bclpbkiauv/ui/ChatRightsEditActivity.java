package im.bclpbkiauv.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.TwoStepVerificationActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.DialogRadioCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.PollEditTextCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCheckCell2;
import im.bclpbkiauv.ui.cells.TextDetailCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.cells.UserCell2;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import java.util.Calendar;

public class ChatRightsEditActivity extends BaseFragment {
    private static final int MAX_RANK_LENGTH = 16;
    public static final int TYPE_ADMIN = 0;
    public static final int TYPE_BANNED = 1;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public int addAdminsRow;
    /* access modifiers changed from: private */
    public int addUsersRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatAdminRights adminRights;
    /* access modifiers changed from: private */
    public int banUsersRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatBannedRights bannedRights;
    /* access modifiers changed from: private */
    public boolean canEdit;
    /* access modifiers changed from: private */
    public int cantEditInfoRow;
    /* access modifiers changed from: private */
    public int changeInfoRow;
    private int chatId;
    private String currentBannedRights = "";
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    /* access modifiers changed from: private */
    public String currentRank;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public TLRPC.User currentUser;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatBannedRights defaultBannedRights;
    private ChatRightsEditActivityDelegate delegate;
    /* access modifiers changed from: private */
    public int deleteMessagesRow;
    /* access modifiers changed from: private */
    public int editMesagesRow;
    /* access modifiers changed from: private */
    public int embedLinksRow;
    private boolean initialIsSet;
    private String initialRank;
    private boolean isAddingNew;
    /* access modifiers changed from: private */
    public boolean isChannel;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatAdminRights myAdminRights;
    /* access modifiers changed from: private */
    public int pinMessagesRow;
    /* access modifiers changed from: private */
    public int postMessagesRow;
    /* access modifiers changed from: private */
    public int rankHeaderRow;
    /* access modifiers changed from: private */
    public int rankInfoRow;
    /* access modifiers changed from: private */
    public int rankRow;
    /* access modifiers changed from: private */
    public int removeAdminRow;
    /* access modifiers changed from: private */
    public int removeAdminShadowRow;
    /* access modifiers changed from: private */
    public int rightsShadowRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int sendMediaRow;
    /* access modifiers changed from: private */
    public int sendMessagesRow;
    /* access modifiers changed from: private */
    public int sendPollsRow;
    /* access modifiers changed from: private */
    public int sendStickersRow;
    /* access modifiers changed from: private */
    public int transferOwnerRow;
    /* access modifiers changed from: private */
    public int transferOwnerShadowRow;
    /* access modifiers changed from: private */
    public int untilDateRow;
    /* access modifiers changed from: private */
    public int untilSectionRow;

    public interface ChatRightsEditActivityDelegate {
        void didChangeOwner(TLRPC.User user);

        void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str);
    }

    public ChatRightsEditActivity(int userId, int channelId, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBannedDefault, TLRPC.TL_chatBannedRights rightsBanned, String rank, int type, boolean edit, boolean addingNew) {
        this.isAddingNew = addingNew;
        this.chatId = channelId;
        this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userId));
        this.currentType = type;
        this.canEdit = edit;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        rank = rank == null ? "" : rank;
        this.currentRank = rank;
        this.initialRank = rank;
        TLRPC.Chat chat = this.currentChat;
        boolean z = true;
        if (chat != null) {
            this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
            this.myAdminRights = this.currentChat.admin_rights;
        }
        if (this.myAdminRights == null) {
            TLRPC.TL_chatAdminRights tL_chatAdminRights = new TLRPC.TL_chatAdminRights();
            this.myAdminRights = tL_chatAdminRights;
            tL_chatAdminRights.add_admins = true;
            tL_chatAdminRights.pin_messages = true;
            tL_chatAdminRights.invite_users = true;
            tL_chatAdminRights.ban_users = true;
            tL_chatAdminRights.delete_messages = true;
            tL_chatAdminRights.edit_messages = true;
            tL_chatAdminRights.post_messages = true;
            tL_chatAdminRights.change_info = true;
        }
        if (type == 0) {
            TLRPC.TL_chatAdminRights tL_chatAdminRights2 = new TLRPC.TL_chatAdminRights();
            this.adminRights = tL_chatAdminRights2;
            if (rightsAdmin == null) {
                tL_chatAdminRights2.change_info = this.myAdminRights.change_info;
                this.adminRights.post_messages = this.myAdminRights.post_messages;
                this.adminRights.edit_messages = this.myAdminRights.edit_messages;
                this.adminRights.delete_messages = this.myAdminRights.delete_messages;
                this.adminRights.ban_users = this.myAdminRights.ban_users;
                this.adminRights.invite_users = this.myAdminRights.invite_users;
                this.adminRights.pin_messages = this.myAdminRights.pin_messages;
                this.initialIsSet = false;
            } else {
                tL_chatAdminRights2.change_info = rightsAdmin.change_info;
                this.adminRights.post_messages = rightsAdmin.post_messages;
                this.adminRights.edit_messages = rightsAdmin.edit_messages;
                this.adminRights.delete_messages = rightsAdmin.delete_messages;
                this.adminRights.ban_users = rightsAdmin.ban_users;
                this.adminRights.invite_users = rightsAdmin.invite_users;
                this.adminRights.pin_messages = rightsAdmin.pin_messages;
                this.adminRights.add_admins = rightsAdmin.add_admins;
                if (!this.adminRights.change_info && !this.adminRights.post_messages && !this.adminRights.edit_messages && !this.adminRights.delete_messages && !this.adminRights.ban_users && !this.adminRights.invite_users && !this.adminRights.pin_messages && !this.adminRights.add_admins) {
                    z = false;
                }
                this.initialIsSet = z;
            }
        } else {
            this.defaultBannedRights = rightsBannedDefault;
            if (rightsBannedDefault == null) {
                TLRPC.TL_chatBannedRights tL_chatBannedRights = new TLRPC.TL_chatBannedRights();
                this.defaultBannedRights = tL_chatBannedRights;
                tL_chatBannedRights.pin_messages = false;
                tL_chatBannedRights.change_info = false;
                tL_chatBannedRights.invite_users = false;
                tL_chatBannedRights.send_polls = false;
                tL_chatBannedRights.send_inline = false;
                tL_chatBannedRights.send_games = false;
                tL_chatBannedRights.send_gifs = false;
                tL_chatBannedRights.send_stickers = false;
                tL_chatBannedRights.embed_links = false;
                tL_chatBannedRights.send_messages = false;
                tL_chatBannedRights.send_media = false;
                tL_chatBannedRights.view_messages = false;
            }
            TLRPC.TL_chatBannedRights tL_chatBannedRights2 = new TLRPC.TL_chatBannedRights();
            this.bannedRights = tL_chatBannedRights2;
            if (rightsBanned == null) {
                tL_chatBannedRights2.pin_messages = false;
                tL_chatBannedRights2.change_info = false;
                tL_chatBannedRights2.invite_users = false;
                tL_chatBannedRights2.send_polls = false;
                tL_chatBannedRights2.send_inline = false;
                tL_chatBannedRights2.send_games = false;
                tL_chatBannedRights2.send_gifs = false;
                tL_chatBannedRights2.send_stickers = false;
                tL_chatBannedRights2.embed_links = false;
                tL_chatBannedRights2.send_messages = false;
                tL_chatBannedRights2.send_media = false;
                tL_chatBannedRights2.view_messages = false;
            } else {
                tL_chatBannedRights2.view_messages = rightsBanned.view_messages;
                this.bannedRights.send_messages = rightsBanned.send_messages;
                this.bannedRights.send_media = rightsBanned.send_media;
                this.bannedRights.send_stickers = rightsBanned.send_stickers;
                this.bannedRights.send_gifs = rightsBanned.send_gifs;
                this.bannedRights.send_games = rightsBanned.send_games;
                this.bannedRights.send_inline = rightsBanned.send_inline;
                this.bannedRights.embed_links = rightsBanned.embed_links;
                this.bannedRights.send_polls = rightsBanned.send_polls;
                this.bannedRights.invite_users = rightsBanned.invite_users;
                this.bannedRights.change_info = rightsBanned.change_info;
                this.bannedRights.pin_messages = rightsBanned.pin_messages;
                this.bannedRights.until_date = rightsBanned.until_date;
            }
            if (this.defaultBannedRights.view_messages) {
                this.bannedRights.view_messages = true;
            }
            if (this.defaultBannedRights.send_messages) {
                this.bannedRights.send_messages = true;
            }
            if (this.defaultBannedRights.send_media) {
                this.bannedRights.send_media = true;
            }
            if (this.defaultBannedRights.send_stickers) {
                this.bannedRights.send_stickers = true;
            }
            if (this.defaultBannedRights.send_gifs) {
                this.bannedRights.send_gifs = true;
            }
            if (this.defaultBannedRights.send_games) {
                this.bannedRights.send_games = true;
            }
            if (this.defaultBannedRights.send_inline) {
                this.bannedRights.send_inline = true;
            }
            if (this.defaultBannedRights.embed_links) {
                this.bannedRights.embed_links = true;
            }
            if (this.defaultBannedRights.send_polls) {
                this.bannedRights.send_polls = true;
            }
            if (this.defaultBannedRights.invite_users) {
                this.bannedRights.invite_users = true;
            }
            if (this.defaultBannedRights.change_info) {
                this.bannedRights.change_info = true;
            }
            if (this.defaultBannedRights.pin_messages) {
                this.bannedRights.pin_messages = true;
            }
            this.currentBannedRights = ChatObject.getBannedRightsString(this.bannedRights);
            if (rightsBanned != null && rightsBanned.view_messages) {
                z = false;
            }
            this.initialIsSet = z;
        }
        updateRows(false);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("EditAdmin", R.string.EditAdmin));
        } else {
            this.actionBar.setTitle(LocaleController.getString("UserRestrictions", R.string.UserRestrictions));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatRightsEditActivity.this.checkDiscard()) {
                        ChatRightsEditActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    ChatRightsEditActivity.this.onDonePressed();
                }
            }
        });
        if (this.canEdit || (!this.isChannel && this.currentChat.creator && UserObject.isUserSelf(this.currentUser))) {
            this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setFocusableInTouchMode(true);
        this.listView = new RecyclerListView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(context) {
            private final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                ChatRightsEditActivity.this.lambda$createView$7$ChatRightsEditActivity(this.f$1, view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$7$ChatRightsEditActivity(Context context, View view, int position) {
        String text;
        TLRPC.Chat chat;
        Context context2 = context;
        View view2 = view;
        int i = position;
        if (this.canEdit) {
            int i2 = 2;
            boolean z = false;
            if (i == 0) {
                if (this.currentUser.self || (chat = this.currentChat) == null || !chat.megagroup || (this.currentChat.flags & ConnectionsManager.FileTypeVideo) == 0 || this.currentUser.mutual_contact || ChatObject.hasAdminRights(this.currentChat)) {
                    Bundle args = new Bundle();
                    args.putInt("user_id", this.currentUser.id);
                    if (this.currentChat.megagroup && (33554432 & this.currentChat.flags) != 0) {
                        z = true;
                    }
                    args.putBoolean("forbid_add_contact", z);
                    args.putBoolean("has_admin_right", ChatObject.hasAdminRights(this.currentChat));
                    args.putInt("from_type", 2);
                    presentFragment(new NewProfileActivity(args));
                    return;
                }
                ToastUtils.show((int) R.string.ForbidViewUserInfoTips);
            } else if (i == this.removeAdminRow) {
                int i3 = this.currentType;
                if (i3 == 0) {
                    MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, this.currentUser, new TLRPC.TL_chatAdminRights(), this.currentRank, this.isChannel, getFragmentForAlert(0), this.isAddingNew);
                } else if (i3 == 1) {
                    TLRPC.TL_chatBannedRights tL_chatBannedRights = new TLRPC.TL_chatBannedRights();
                    this.bannedRights = tL_chatBannedRights;
                    tL_chatBannedRights.view_messages = true;
                    this.bannedRights.send_media = true;
                    this.bannedRights.send_messages = true;
                    this.bannedRights.send_stickers = true;
                    this.bannedRights.send_gifs = true;
                    this.bannedRights.send_games = true;
                    this.bannedRights.send_inline = true;
                    this.bannedRights.embed_links = true;
                    this.bannedRights.pin_messages = true;
                    this.bannedRights.send_polls = true;
                    this.bannedRights.invite_users = true;
                    this.bannedRights.change_info = true;
                    this.bannedRights.until_date = 0;
                    MessagesController.getInstance(this.currentAccount).setUserBannedRole(this.chatId, this.currentUser, this.bannedRights, this.isChannel, getFragmentForAlert(0));
                }
                ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
                if (chatRightsEditActivityDelegate != null) {
                    chatRightsEditActivityDelegate.didSetRights(0, this.adminRights, this.bannedRights, this.currentRank);
                }
                finishFragment();
            } else if (i == this.transferOwnerRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                if (this.isChannel) {
                    builder.setTitle(LocaleController.getString("EditAdminChannelTransfer", R.string.EditAdminChannelTransfer));
                } else {
                    builder.setTitle(LocaleController.getString("EditAdminGroupTransfer", R.string.EditAdminGroupTransfer));
                }
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferReadyAlertText", R.string.EditAdminTransferReadyAlertText, this.currentChat.title, UserObject.getFirstName(this.currentUser))));
                builder.setPositiveButton(LocaleController.getString("EditAdminTransferChangeOwner", R.string.EditAdminTransferChangeOwner), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatRightsEditActivity.this.lambda$null$0$ChatRightsEditActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            } else if (i == this.untilDateRow) {
                if (getParentActivity() != null) {
                    BottomSheet.Builder builder2 = new BottomSheet.Builder(context2);
                    builder2.setApplyTopPadding(false);
                    LinearLayout linearLayout = new LinearLayout(context2);
                    linearLayout.setOrientation(1);
                    HeaderCell headerCell = new HeaderCell(context, true, 23, 15, false);
                    headerCell.setHeight(47);
                    headerCell.setText(LocaleController.getString("UserRestrictionsDuration", R.string.UserRestrictionsDuration));
                    linearLayout.addView(headerCell);
                    LinearLayout linearLayoutInviteContainer = new LinearLayout(context2);
                    linearLayoutInviteContainer.setOrientation(1);
                    linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
                    BottomSheet.BottomSheetCell[] buttons = new BottomSheet.BottomSheetCell[5];
                    int a = 0;
                    while (a < buttons.length) {
                        buttons[a] = new BottomSheet.BottomSheetCell(context2, z);
                        buttons[a].setPadding(AndroidUtilities.dp(7.0f), z ? 1 : 0, AndroidUtilities.dp(7.0f), z);
                        buttons[a].setTag(Integer.valueOf(a));
                        buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(z));
                        if (a == 0) {
                            text = LocaleController.getString("UserRestrictionsUntilForever", R.string.UserRestrictionsUntilForever);
                        } else if (a == 1) {
                            text = LocaleController.formatPluralString("Days", 1);
                        } else if (a == i2) {
                            text = LocaleController.formatPluralString("Weeks", 1);
                        } else if (a != 3) {
                            text = LocaleController.getString("UserRestrictionsCustom", R.string.UserRestrictionsCustom);
                        } else {
                            text = LocaleController.formatPluralString("Months", 1);
                        }
                        buttons[a].setTextAndIcon(text, z);
                        linearLayoutInviteContainer.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
                        buttons[a].setOnClickListener(new View.OnClickListener(builder2) {
                            private final /* synthetic */ BottomSheet.Builder f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                ChatRightsEditActivity.this.lambda$null$6$ChatRightsEditActivity(this.f$1, view);
                            }
                        });
                        a++;
                        i2 = 2;
                        z = false;
                    }
                    builder2.setCustomView(linearLayout);
                    showDialog(builder2.create());
                }
            } else if (view2 instanceof TextCheckCell2) {
                TextCheckCell2 checkCell = (TextCheckCell2) view2;
                if (checkCell.hasIcon()) {
                    ToastUtils.show((int) R.string.UserRestrictionsDisabled);
                } else if (checkCell.isEnabled()) {
                    checkCell.setChecked(!checkCell.isChecked());
                    if (i == this.changeInfoRow) {
                        if (this.currentType == 0) {
                            TLRPC.TL_chatAdminRights tL_chatAdminRights = this.adminRights;
                            tL_chatAdminRights.change_info = !tL_chatAdminRights.change_info;
                        } else {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights2 = this.bannedRights;
                            tL_chatBannedRights2.change_info = !tL_chatBannedRights2.change_info;
                        }
                    } else if (i == this.postMessagesRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights2 = this.adminRights;
                        tL_chatAdminRights2.post_messages = !tL_chatAdminRights2.post_messages;
                    } else if (i == this.editMesagesRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights3 = this.adminRights;
                        tL_chatAdminRights3.edit_messages = !tL_chatAdminRights3.edit_messages;
                    } else if (i == this.deleteMessagesRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights4 = this.adminRights;
                        tL_chatAdminRights4.delete_messages = !tL_chatAdminRights4.delete_messages;
                    } else if (i == this.addAdminsRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights5 = this.adminRights;
                        tL_chatAdminRights5.add_admins = !tL_chatAdminRights5.add_admins;
                    } else if (i == this.banUsersRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights6 = this.adminRights;
                        tL_chatAdminRights6.ban_users = !tL_chatAdminRights6.ban_users;
                    } else if (i == this.addUsersRow) {
                        if (this.currentType == 0) {
                            TLRPC.TL_chatAdminRights tL_chatAdminRights7 = this.adminRights;
                            tL_chatAdminRights7.invite_users = !tL_chatAdminRights7.invite_users;
                        } else {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights3 = this.bannedRights;
                            tL_chatBannedRights3.invite_users = !tL_chatBannedRights3.invite_users;
                        }
                    } else if (i == this.pinMessagesRow) {
                        if (this.currentType == 0) {
                            TLRPC.TL_chatAdminRights tL_chatAdminRights8 = this.adminRights;
                            tL_chatAdminRights8.pin_messages = !tL_chatAdminRights8.pin_messages;
                        } else {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights4 = this.bannedRights;
                            tL_chatBannedRights4.pin_messages = !tL_chatBannedRights4.pin_messages;
                        }
                    } else if (this.bannedRights != null) {
                        boolean disabled = !checkCell.isChecked();
                        if (i == this.sendMessagesRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights5 = this.bannedRights;
                            tL_chatBannedRights5.send_messages = !tL_chatBannedRights5.send_messages;
                        } else if (i == this.sendMediaRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights6 = this.bannedRights;
                            tL_chatBannedRights6.send_media = !tL_chatBannedRights6.send_media;
                        } else if (i == this.sendStickersRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights7 = this.bannedRights;
                            boolean z2 = !tL_chatBannedRights7.send_stickers;
                            tL_chatBannedRights7.send_inline = z2;
                            tL_chatBannedRights7.send_gifs = z2;
                            tL_chatBannedRights7.send_games = z2;
                            tL_chatBannedRights7.send_stickers = z2;
                        } else if (i == this.embedLinksRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights8 = this.bannedRights;
                            tL_chatBannedRights8.embed_links = !tL_chatBannedRights8.embed_links;
                        } else if (i == this.sendPollsRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights9 = this.bannedRights;
                            tL_chatBannedRights9.send_polls = !tL_chatBannedRights9.send_polls;
                        }
                        if (disabled) {
                            if (this.bannedRights.view_messages && !this.bannedRights.send_messages) {
                                this.bannedRights.send_messages = true;
                                RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                if (holder != null) {
                                    ((TextCheckCell2) holder.itemView).setChecked(false);
                                }
                            }
                            if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_media) {
                                this.bannedRights.send_media = true;
                                RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                                if (holder2 != null) {
                                    ((TextCheckCell2) holder2.itemView).setChecked(false);
                                }
                            }
                            if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_polls) {
                                this.bannedRights.send_polls = true;
                                RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                                if (holder3 != null) {
                                    ((TextCheckCell2) holder3.itemView).setChecked(false);
                                }
                            }
                            if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_stickers) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights10 = this.bannedRights;
                                tL_chatBannedRights10.send_inline = true;
                                tL_chatBannedRights10.send_gifs = true;
                                tL_chatBannedRights10.send_games = true;
                                tL_chatBannedRights10.send_stickers = true;
                                RecyclerView.ViewHolder holder4 = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                                if (holder4 != null) {
                                    ((TextCheckCell2) holder4.itemView).setChecked(false);
                                }
                            }
                            if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.embed_links) {
                                this.bannedRights.embed_links = true;
                                RecyclerView.ViewHolder holder5 = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                                if (holder5 != null) {
                                    ((TextCheckCell2) holder5.itemView).setChecked(false);
                                }
                            }
                        } else {
                            if ((!this.bannedRights.send_messages || !this.bannedRights.embed_links || !this.bannedRights.send_inline || !this.bannedRights.send_media || !this.bannedRights.send_polls) && this.bannedRights.view_messages) {
                                this.bannedRights.view_messages = false;
                            }
                            if ((!this.bannedRights.embed_links || !this.bannedRights.send_inline || !this.bannedRights.send_media || !this.bannedRights.send_polls) && this.bannedRights.send_messages) {
                                this.bannedRights.send_messages = false;
                                RecyclerView.ViewHolder holder6 = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                if (holder6 != null) {
                                    ((TextCheckCell2) holder6.itemView).setChecked(true);
                                }
                            }
                        }
                    }
                    updateRows(true);
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$0$ChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        lambda$null$9$ChatRightsEditActivity((TLRPC.InputCheckPasswordSRP) null, (TwoStepVerificationActivity) null);
    }

    public /* synthetic */ void lambda$null$6$ChatRightsEditActivity(BottomSheet.Builder builder, View v2) {
        int intValue = ((Integer) v2.getTag()).intValue();
        if (intValue == 0) {
            this.bannedRights.until_date = 0;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 1) {
            this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 86400;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 2) {
            this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 604800;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 3) {
            this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 2592000;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 4) {
            Calendar calendar = Calendar.getInstance();
            try {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getParentActivity(), new DatePickerDialog.OnDateSetListener() {
                    public final void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        ChatRightsEditActivity.this.lambda$null$3$ChatRightsEditActivity(datePicker, i, i2, i3);
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(System.currentTimeMillis());
                date.set(11, date.getMinimum(11));
                date.set(12, date.getMinimum(12));
                date.set(13, date.getMinimum(13));
                date.set(14, date.getMinimum(14));
                datePicker.setMinDate(date.getTimeInMillis());
                date.setTimeInMillis(System.currentTimeMillis() + 31536000000L);
                date.set(11, date.getMaximum(11));
                date.set(12, date.getMaximum(12));
                date.set(13, date.getMaximum(13));
                date.set(14, date.getMaximum(14));
                datePicker.setMaxDate(date.getTimeInMillis());
                datePickerDialog.setButton(-1, LocaleController.getString("Set", R.string.Set), datePickerDialog);
                datePickerDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), $$Lambda$ChatRightsEditActivity$dwC9x8TIL6sNGQtrPPaVjhu5SI0.INSTANCE);
                if (Build.VERSION.SDK_INT >= 21) {
                    datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener(datePicker) {
                        private final /* synthetic */ DatePicker f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void onShow(DialogInterface dialogInterface) {
                            ChatRightsEditActivity.lambda$null$5(this.f$0, dialogInterface);
                        }
                    });
                }
                showDialog(datePickerDialog);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        builder.getDismissRunnable().run();
    }

    public /* synthetic */ void lambda$null$3$ChatRightsEditActivity(DatePicker view1, int year1, int month, int dayOfMonth1) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set(year1, month, dayOfMonth1);
        try {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getParentActivity(), new TimePickerDialog.OnTimeSetListener((int) (calendar1.getTime().getTime() / 1000)) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onTimeSet(TimePicker timePicker, int i, int i2) {
                    ChatRightsEditActivity.this.lambda$null$1$ChatRightsEditActivity(this.f$1, timePicker, i, i2);
                }
            }, 0, 0, true);
            timePickerDialog.setButton(-1, LocaleController.getString("Set", R.string.Set), timePickerDialog);
            timePickerDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), $$Lambda$ChatRightsEditActivity$lPgPTn4Vp8USCdmQDFMWs0FwHTs.INSTANCE);
            showDialog(timePickerDialog);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$1$ChatRightsEditActivity(int time, TimePicker view11, int hourOfDay, int minute) {
        this.bannedRights.until_date = (hourOfDay * 3600) + time + (minute * 60);
        this.listViewAdapter.notifyItemChanged(this.untilDateRow);
    }

    static /* synthetic */ void lambda$null$2(DialogInterface dialog131, int which) {
    }

    static /* synthetic */ void lambda$null$4(DialogInterface dialog1, int which) {
    }

    static /* synthetic */ void lambda$null$5(DatePicker datePicker, DialogInterface dialog12) {
        int count = datePicker.getChildCount();
        for (int b = 0; b < count; b++) {
            View child = datePicker.getChildAt(b);
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.width = -1;
            child.setLayoutParams(layoutParams);
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    private boolean isDefaultAdminRights() {
        return (this.adminRights.change_info && this.adminRights.delete_messages && this.adminRights.ban_users && this.adminRights.invite_users && this.adminRights.pin_messages && !this.adminRights.add_admins) || (!this.adminRights.change_info && !this.adminRights.delete_messages && !this.adminRights.ban_users && !this.adminRights.invite_users && !this.adminRights.pin_messages && !this.adminRights.add_admins);
    }

    private boolean hasAllAdminRights() {
        if (this.isChannel) {
            if (!this.adminRights.change_info || !this.adminRights.post_messages || !this.adminRights.edit_messages || !this.adminRights.delete_messages || !this.adminRights.invite_users || !this.adminRights.add_admins) {
                return false;
            }
            return true;
        } else if (!this.adminRights.change_info || !this.adminRights.delete_messages || !this.adminRights.ban_users || !this.adminRights.invite_users || !this.adminRights.pin_messages || !this.adminRights.add_admins) {
            return false;
        } else {
            return true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: initTransfer */
    public void lambda$null$9$ChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment) {
        if (getParentActivity() != null) {
            if (srp == null || ChatObject.isChannel(this.currentChat)) {
                TLRPC.TL_channels_editCreator req = new TLRPC.TL_channels_editCreator();
                if (ChatObject.isChannel(this.currentChat)) {
                    req.channel = new TLRPC.TL_inputChannel();
                    req.channel.channel_id = this.currentChat.id;
                    req.channel.access_hash = this.currentChat.access_hash;
                } else {
                    req.channel = new TLRPC.TL_inputChannelEmpty();
                }
                req.password = srp != null ? srp : new TLRPC.TL_inputCheckPasswordEmpty();
                req.user_id = getMessagesController().getInputUser(this.currentUser);
                getConnectionsManager().sendRequest(req, new RequestDelegate(srp, passwordFragment, req) {
                    private final /* synthetic */ TLRPC.InputCheckPasswordSRP f$1;
                    private final /* synthetic */ TwoStepVerificationActivity f$2;
                    private final /* synthetic */ TLRPC.TL_channels_editCreator f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ChatRightsEditActivity.this.lambda$initTransfer$15$ChatRightsEditActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                    }
                });
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.IntCallback(srp, passwordFragment) {
                private final /* synthetic */ TLRPC.InputCheckPasswordSRP f$1;
                private final /* synthetic */ TwoStepVerificationActivity f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(int i) {
                    ChatRightsEditActivity.this.lambda$initTransfer$8$ChatRightsEditActivity(this.f$1, this.f$2, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$initTransfer$8$ChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, int param) {
        this.chatId = param;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(param));
        lambda$null$9$ChatRightsEditActivity(srp, passwordFragment);
    }

    public /* synthetic */ void lambda$initTransfer$15$ChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, TLRPC.TL_channels_editCreator req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, srp, passwordFragment, req) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLRPC.InputCheckPasswordSRP f$2;
            private final /* synthetic */ TwoStepVerificationActivity f$3;
            private final /* synthetic */ TLRPC.TL_channels_editCreator f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ChatRightsEditActivity.this.lambda$null$14$ChatRightsEditActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$14$ChatRightsEditActivity(TLRPC.TL_error error, TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, TLRPC.TL_channels_editCreator req) {
        TLRPC.TL_error tL_error = error;
        TwoStepVerificationActivity twoStepVerificationActivity = passwordFragment;
        if (tL_error == null) {
            TLRPC.TL_channels_editCreator tL_channels_editCreator = req;
            if (srp != null) {
                this.delegate.didChangeOwner(this.currentUser);
                removeSelfFromStack();
                passwordFragment.needHideProgress();
                passwordFragment.finishFragment();
                return;
            }
            this.delegate.didChangeOwner(this.currentUser);
            finishFragment();
        } else if (getParentActivity() != null) {
            if (!"PASSWORD_HASH_INVALID".equals(tL_error.text)) {
                if ("PASSWORD_MISSING".equals(tL_error.text) || tL_error.text.startsWith("PASSWORD_TOO_FRESH_")) {
                    TLRPC.TL_channels_editCreator tL_channels_editCreator2 = req;
                } else if (tL_error.text.startsWith("SESSION_TOO_FRESH_")) {
                    TLRPC.TL_channels_editCreator tL_channels_editCreator3 = req;
                } else if ("SRP_ID_INVALID".equals(tL_error.text)) {
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate(twoStepVerificationActivity) {
                        private final /* synthetic */ TwoStepVerificationActivity f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            ChatRightsEditActivity.this.lambda$null$13$ChatRightsEditActivity(this.f$1, tLObject, tL_error);
                        }
                    }, 8);
                    TLRPC.TL_channels_editCreator tL_channels_editCreator4 = req;
                    return;
                } else {
                    if (twoStepVerificationActivity != null) {
                        passwordFragment.needHideProgress();
                        passwordFragment.finishFragment();
                    }
                    AlertsCreator.showAddUserAlert(tL_error.text, this, this.isChannel, req);
                    return;
                }
                if (twoStepVerificationActivity != null) {
                    passwordFragment.needHideProgress();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("EditAdminTransferAlertTitle", R.string.EditAdminTransferAlertTitle));
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(24.0f), 0);
                linearLayout.setOrientation(1);
                builder.setView(linearLayout);
                TextView messageTextView = new TextView(getParentActivity());
                messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                messageTextView.setTextSize(1, 16.0f);
                int i = 3;
                messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                if (this.isChannel) {
                    messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EditChannelAdminTransferAlertText", R.string.EditChannelAdminTransferAlertText, UserObject.getFirstName(this.currentUser))));
                } else {
                    messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferAlertText", R.string.EditAdminTransferAlertText, UserObject.getFirstName(this.currentUser))));
                }
                linearLayout.addView(messageTextView, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
                linearLayout2.setOrientation(0);
                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                ImageView dotImageView = new ImageView(getParentActivity());
                dotImageView.setImageResource(R.drawable.list_circle);
                dotImageView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
                dotImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
                TextView messageTextView2 = new TextView(getParentActivity());
                messageTextView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                messageTextView2.setTextSize(1, 16.0f);
                messageTextView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                messageTextView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText1", R.string.EditAdminTransferAlertText1)));
                if (LocaleController.isRTL) {
                    linearLayout2.addView(messageTextView2, LayoutHelper.createLinear(-1, -2));
                    linearLayout2.addView(dotImageView, LayoutHelper.createLinear(-2, -2, 5));
                } else {
                    linearLayout2.addView(dotImageView, LayoutHelper.createLinear(-2, -2));
                    linearLayout2.addView(messageTextView2, LayoutHelper.createLinear(-1, -2));
                }
                LinearLayout linearLayout22 = new LinearLayout(getParentActivity());
                linearLayout22.setOrientation(0);
                linearLayout.addView(linearLayout22, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                ImageView dotImageView2 = new ImageView(getParentActivity());
                dotImageView2.setImageResource(R.drawable.list_circle);
                dotImageView2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
                dotImageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
                TextView messageTextView3 = new TextView(getParentActivity());
                messageTextView3.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                messageTextView3.setTextSize(1, 16.0f);
                messageTextView3.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                messageTextView3.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText2", R.string.EditAdminTransferAlertText2)));
                if (LocaleController.isRTL) {
                    linearLayout22.addView(messageTextView3, LayoutHelper.createLinear(-1, -2));
                    linearLayout22.addView(dotImageView2, LayoutHelper.createLinear(-2, -2, 5));
                } else {
                    linearLayout22.addView(dotImageView2, LayoutHelper.createLinear(-2, -2));
                    linearLayout22.addView(messageTextView3, LayoutHelper.createLinear(-1, -2));
                }
                if ("PASSWORD_MISSING".equals(tL_error.text)) {
                    builder.setPositiveButton(LocaleController.getString("EditAdminTransferSetPassword", R.string.EditAdminTransferSetPassword), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatRightsEditActivity.this.lambda$null$11$ChatRightsEditActivity(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                } else {
                    TextView messageTextView4 = new TextView(getParentActivity());
                    messageTextView4.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    messageTextView4.setTextSize(1, 16.0f);
                    if (LocaleController.isRTL) {
                        i = 5;
                    }
                    messageTextView4.setGravity(i | 48);
                    messageTextView4.setText(LocaleController.getString("EditAdminTransferAlertText3", R.string.EditAdminTransferAlertText3));
                    linearLayout.addView(messageTextView4, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                    builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                }
                showDialog(builder.create());
            } else if (srp == null) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                if (this.isChannel) {
                    builder2.setTitle(LocaleController.getString("EditAdminChannelTransfer", R.string.EditAdminChannelTransfer));
                } else {
                    builder2.setTitle(LocaleController.getString("EditAdminGroupTransfer", R.string.EditAdminGroupTransfer));
                }
                builder2.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferReadyAlertText", R.string.EditAdminTransferReadyAlertText, this.currentChat.title, UserObject.getFirstName(this.currentUser))));
                builder2.setPositiveButton(LocaleController.getString("EditAdminTransferChangeOwner", R.string.EditAdminTransferChangeOwner), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatRightsEditActivity.this.lambda$null$10$ChatRightsEditActivity(dialogInterface, i);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder2.create());
                TLRPC.TL_channels_editCreator tL_channels_editCreator5 = req;
            } else {
                TLRPC.TL_channels_editCreator tL_channels_editCreator6 = req;
            }
        }
    }

    public /* synthetic */ void lambda$null$10$ChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(0);
        fragment.setDelegate(new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate(fragment) {
            private final /* synthetic */ TwoStepVerificationActivity f$1;

            {
                this.f$1 = r2;
            }

            public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
                ChatRightsEditActivity.this.lambda$null$9$ChatRightsEditActivity(this.f$1, inputCheckPasswordSRP);
            }
        });
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$null$11$ChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new TwoStepVerificationActivity(0));
    }

    public /* synthetic */ void lambda$null$13$ChatRightsEditActivity(TwoStepVerificationActivity passwordFragment, TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable(error2, response2, passwordFragment) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TwoStepVerificationActivity f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatRightsEditActivity.this.lambda$null$12$ChatRightsEditActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$12$ChatRightsEditActivity(TLRPC.TL_error error2, TLObject response2, TwoStepVerificationActivity passwordFragment) {
        if (error2 == null) {
            TLRPC.TL_account_password currentPassword = (TLRPC.TL_account_password) response2;
            passwordFragment.setCurrentPasswordInfo((byte[]) null, currentPassword);
            TwoStepVerificationActivity.initPasswordNewAlgo(currentPassword);
            lambda$null$9$ChatRightsEditActivity(passwordFragment.getNewSrpPassword(), passwordFragment);
        }
    }

    private void updateRows(boolean update) {
        int i;
        int transferOwnerShadowRowPrev = Math.min(this.transferOwnerShadowRow, this.transferOwnerRow);
        this.changeInfoRow = -1;
        this.postMessagesRow = -1;
        this.editMesagesRow = -1;
        this.deleteMessagesRow = -1;
        this.addAdminsRow = -1;
        this.banUsersRow = -1;
        this.addUsersRow = -1;
        this.pinMessagesRow = -1;
        this.rightsShadowRow = -1;
        this.removeAdminRow = -1;
        this.removeAdminShadowRow = -1;
        this.cantEditInfoRow = -1;
        this.transferOwnerShadowRow = -1;
        this.transferOwnerRow = -1;
        this.rankHeaderRow = -1;
        this.rankRow = -1;
        this.rankInfoRow = -1;
        this.sendMessagesRow = -1;
        this.sendMediaRow = -1;
        this.sendStickersRow = -1;
        this.sendPollsRow = -1;
        this.embedLinksRow = -1;
        this.untilSectionRow = -1;
        this.untilDateRow = -1;
        this.rowCount = 3;
        int i2 = this.currentType;
        if (i2 == 0) {
            if (this.isChannel) {
                int i3 = 3 + 1;
                this.rowCount = i3;
                this.changeInfoRow = 3;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.postMessagesRow = i3;
                int i5 = i4 + 1;
                this.rowCount = i5;
                this.editMesagesRow = i4;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.deleteMessagesRow = i5;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.addUsersRow = i6;
                this.rowCount = i7 + 1;
                this.addAdminsRow = i7;
            } else {
                int i8 = 3 + 1;
                this.rowCount = i8;
                this.changeInfoRow = 3;
                int i9 = i8 + 1;
                this.rowCount = i9;
                this.deleteMessagesRow = i8;
                int i10 = i9 + 1;
                this.rowCount = i10;
                this.banUsersRow = i9;
                int i11 = i10 + 1;
                this.rowCount = i11;
                this.addUsersRow = i10;
                int i12 = i11 + 1;
                this.rowCount = i12;
                this.pinMessagesRow = i11;
                this.rowCount = i12 + 1;
                this.addAdminsRow = i12;
            }
        } else if (i2 == 1) {
            int i13 = 3 + 1;
            this.rowCount = i13;
            this.sendMessagesRow = 3;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.sendMediaRow = i13;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.sendStickersRow = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.sendPollsRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.embedLinksRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.addUsersRow = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.pinMessagesRow = i18;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.changeInfoRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.untilSectionRow = i20;
            this.rowCount = i21 + 1;
            this.untilDateRow = i21;
        }
        boolean z = this.canEdit;
        if (z) {
            if (!this.isChannel && this.currentType == 0) {
                int i22 = this.rowCount;
                int i23 = i22 + 1;
                this.rowCount = i23;
                this.rightsShadowRow = i22;
                int i24 = i23 + 1;
                this.rowCount = i24;
                this.rankHeaderRow = i23;
                int i25 = i24 + 1;
                this.rowCount = i25;
                this.rankRow = i24;
                this.rowCount = i25 + 1;
                this.rankInfoRow = i25;
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat != null && chat.creator && this.currentType == 0 && hasAllAdminRights() && !this.currentUser.bot) {
                int i26 = this.rowCount;
                int i27 = i26 + 1;
                this.rowCount = i27;
                this.transferOwnerRow = i26;
                this.rowCount = i27 + 1;
                this.transferOwnerShadowRow = i27;
            }
            if (this.initialIsSet) {
                int i28 = this.rowCount;
                int i29 = i28 + 1;
                this.rowCount = i29;
                this.rightsShadowRow = i28;
                int i30 = i29 + 1;
                this.rowCount = i30;
                this.removeAdminRow = i29;
                this.rowCount = i30 + 1;
                this.removeAdminShadowRow = i30;
                this.cantEditInfoRow = -1;
            }
        } else {
            this.removeAdminRow = -1;
            this.removeAdminShadowRow = -1;
            if (this.currentType != 0 || z) {
                int i31 = this.rowCount;
                this.rowCount = i31 + 1;
                this.rightsShadowRow = i31;
            } else {
                this.rightsShadowRow = -1;
                int i32 = this.rowCount;
                this.rowCount = i32 + 1;
                this.cantEditInfoRow = i32;
            }
        }
        if (!update) {
            return;
        }
        if (transferOwnerShadowRowPrev == -1 && (i = this.transferOwnerShadowRow) != -1) {
            this.listViewAdapter.notifyItemRangeInserted(Math.min(i, this.transferOwnerRow), 2);
        } else if (transferOwnerShadowRowPrev != -1 && this.transferOwnerShadowRow == -1) {
            this.listViewAdapter.notifyItemRangeRemoved(transferOwnerShadowRowPrev, 2);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0027, code lost:
        if (r0.codePointCount(0, r0.length()) > 16) goto L_0x0029;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDonePressed() {
        /*
            r13 = this;
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r13.currentChat
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r0)
            r1 = 16
            r2 = -1
            r3 = 1
            r4 = 0
            if (r0 != 0) goto L_0x003e
            int r0 = r13.currentType
            if (r0 == r3) goto L_0x0029
            if (r0 != 0) goto L_0x003e
            boolean r0 = r13.isDefaultAdminRights()
            if (r0 == 0) goto L_0x0029
            int r0 = r13.rankRow
            if (r0 == r2) goto L_0x003e
            java.lang.String r0 = r13.currentRank
            int r5 = r0.length()
            int r0 = r0.codePointCount(r4, r5)
            if (r0 <= r1) goto L_0x003e
        L_0x0029:
            int r0 = r13.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            androidx.fragment.app.FragmentActivity r1 = r13.getParentActivity()
            int r2 = r13.chatId
            im.bclpbkiauv.ui.-$$Lambda$ChatRightsEditActivity$-u4L9DQE4SL9vC0_X9teUygD8yc r3 = new im.bclpbkiauv.ui.-$$Lambda$ChatRightsEditActivity$-u4L9DQE4SL9vC0_X9teUygD8yc
            r3.<init>()
            r0.convertToMegaGroup(r1, r2, r13, r3)
            return
        L_0x003e:
            int r0 = r13.currentType
            if (r0 != 0) goto L_0x00e9
            int r0 = r13.rankRow
            if (r0 == r2) goto L_0x007f
            java.lang.String r0 = r13.currentRank
            int r2 = r0.length()
            int r0 = r0.codePointCount(r4, r2)
            if (r0 <= r1) goto L_0x007f
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r13.listView
            int r1 = r13.rankRow
            r0.smoothScrollToPosition(r1)
            androidx.fragment.app.FragmentActivity r0 = r13.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x006d
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x006d:
            im.bclpbkiauv.ui.components.RecyclerListView r1 = r13.listView
            int r2 = r13.rankHeaderRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r1.findViewHolderForAdapterPosition(r2)
            if (r1 == 0) goto L_0x007e
            android.view.View r2 = r1.itemView
            r3 = 1073741824(0x40000000, float:2.0)
            im.bclpbkiauv.messenger.AndroidUtilities.shakeView(r2, r3, r4)
        L_0x007e:
            return
        L_0x007f:
            boolean r0 = r13.isChannel
            if (r0 == 0) goto L_0x008a
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r0 = r13.adminRights
            r0.ban_users = r4
            r0.pin_messages = r4
            goto L_0x0090
        L_0x008a:
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r0 = r13.adminRights
            r0.edit_messages = r4
            r0.post_messages = r4
        L_0x0090:
            int r0 = r13.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            int r6 = r13.chatId
            im.bclpbkiauv.tgnet.TLRPC$User r7 = r13.currentUser
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r8 = r13.adminRights
            java.lang.String r9 = r13.currentRank
            boolean r10 = r13.isChannel
            im.bclpbkiauv.ui.actionbar.BaseFragment r11 = r13.getFragmentForAlert(r3)
            boolean r12 = r13.isAddingNew
            r5.setUserAdminRole(r6, r7, r8, r9, r10, r11, r12)
            im.bclpbkiauv.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r0 = r13.delegate
            if (r0 == 0) goto L_0x013f
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r1 = r13.adminRights
            boolean r1 = r1.change_info
            if (r1 != 0) goto L_0x00df
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r1 = r13.adminRights
            boolean r1 = r1.post_messages
            if (r1 != 0) goto L_0x00df
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r1 = r13.adminRights
            boolean r1 = r1.edit_messages
            if (r1 != 0) goto L_0x00df
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r1 = r13.adminRights
            boolean r1 = r1.delete_messages
            if (r1 != 0) goto L_0x00df
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r1 = r13.adminRights
            boolean r1 = r1.ban_users
            if (r1 != 0) goto L_0x00df
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r1 = r13.adminRights
            boolean r1 = r1.invite_users
            if (r1 != 0) goto L_0x00df
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r1 = r13.adminRights
            boolean r1 = r1.pin_messages
            if (r1 != 0) goto L_0x00df
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r1 = r13.adminRights
            boolean r1 = r1.add_admins
            if (r1 == 0) goto L_0x00de
            goto L_0x00df
        L_0x00de:
            r3 = 0
        L_0x00df:
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r1 = r13.adminRights
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r2 = r13.bannedRights
            java.lang.String r4 = r13.currentRank
            r0.didSetRights(r3, r1, r2, r4)
            goto L_0x013f
        L_0x00e9:
            if (r0 != r3) goto L_0x013f
            int r0 = r13.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            int r6 = r13.chatId
            im.bclpbkiauv.tgnet.TLRPC$User r7 = r13.currentUser
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r8 = r13.bannedRights
            boolean r9 = r13.isChannel
            im.bclpbkiauv.ui.actionbar.BaseFragment r10 = r13.getFragmentForAlert(r3)
            r5.setUserBannedRole(r6, r7, r8, r9, r10)
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r0 = r13.bannedRights
            boolean r0 = r0.send_messages
            if (r0 != 0) goto L_0x0131
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r0 = r13.bannedRights
            boolean r0 = r0.send_stickers
            if (r0 != 0) goto L_0x0131
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r0 = r13.bannedRights
            boolean r0 = r0.embed_links
            if (r0 != 0) goto L_0x0131
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r0 = r13.bannedRights
            boolean r0 = r0.send_media
            if (r0 != 0) goto L_0x0131
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r0 = r13.bannedRights
            boolean r0 = r0.send_gifs
            if (r0 != 0) goto L_0x0131
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r0 = r13.bannedRights
            boolean r0 = r0.send_games
            if (r0 != 0) goto L_0x0131
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r0 = r13.bannedRights
            boolean r0 = r0.send_inline
            if (r0 == 0) goto L_0x012b
            goto L_0x0131
        L_0x012b:
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r0 = r13.bannedRights
            r0.until_date = r4
            r0 = 2
            goto L_0x0132
        L_0x0131:
            r0 = 1
        L_0x0132:
            im.bclpbkiauv.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r1 = r13.delegate
            if (r1 == 0) goto L_0x013f
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r2 = r13.adminRights
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r13.bannedRights
            java.lang.String r4 = r13.currentRank
            r1.didSetRights(r0, r2, r3, r4)
        L_0x013f:
            r13.finishFragment()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatRightsEditActivity.onDonePressed():void");
    }

    public /* synthetic */ void lambda$onDonePressed$16$ChatRightsEditActivity(int param) {
        this.chatId = param;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(param));
        onDonePressed();
    }

    public void setDelegate(ChatRightsEditActivityDelegate channelRightsEditActivityDelegate) {
        this.delegate = channelRightsEditActivityDelegate;
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        boolean changed;
        if (this.currentType == 1) {
            changed = !this.currentBannedRights.equals(ChatObject.getBannedRightsString(this.bannedRights));
        } else {
            changed = !this.initialRank.equals(this.currentRank);
        }
        if (!changed) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("UserRestrictionsApplyChangesText", R.string.UserRestrictionsApplyChangesText, MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId)).title)));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatRightsEditActivity.this.lambda$checkDiscard$17$ChatRightsEditActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatRightsEditActivity.this.lambda$checkDiscard$18$ChatRightsEditActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$17$ChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        onDonePressed();
    }

    public /* synthetic */ void lambda$checkDiscard$18$ChatRightsEditActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View cell) {
        if (cell instanceof HeaderCell) {
            HeaderCell headerCell = (HeaderCell) cell;
            String str = this.currentRank;
            int left = 16 - (str != null ? str.codePointCount(0, str.length()) : 0);
            if (((float) left) <= 4.8f) {
                headerCell.setText2(String.format("%d", new Object[]{Integer.valueOf(left)}));
                SimpleTextView textView = headerCell.getTextView2();
                String key = left < 0 ? Theme.key_windowBackgroundWhiteRedText5 : Theme.key_windowBackgroundWhiteGrayText3;
                textView.setTextColor(Theme.getColor(key));
                textView.setTag(key);
                return;
            }
            headerCell.setText2("");
        }
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public boolean ignoreTextChange;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (!ChatRightsEditActivity.this.canEdit) {
                return false;
            }
            int type = holder.getItemViewType();
            if (ChatRightsEditActivity.this.currentType == 0 && type == 4) {
                int position = holder.getAdapterPosition();
                if (position == ChatRightsEditActivity.this.changeInfoRow) {
                    return ChatRightsEditActivity.this.myAdminRights.change_info;
                }
                if (position == ChatRightsEditActivity.this.postMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.post_messages;
                }
                if (position == ChatRightsEditActivity.this.editMesagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.edit_messages;
                }
                if (position == ChatRightsEditActivity.this.deleteMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.delete_messages;
                }
                if (position == ChatRightsEditActivity.this.addAdminsRow) {
                    return ChatRightsEditActivity.this.myAdminRights.add_admins;
                }
                if (position == ChatRightsEditActivity.this.banUsersRow) {
                    return ChatRightsEditActivity.this.myAdminRights.ban_users;
                }
                if (position == ChatRightsEditActivity.this.addUsersRow) {
                    return ChatRightsEditActivity.this.myAdminRights.invite_users;
                }
                if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.pin_messages;
                }
            }
            if ((ChatRightsEditActivity.this.currentType == 1 && type == 0) || type == 3 || type == 1 || type == 5) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ChatRightsEditActivity.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: im.bclpbkiauv.ui.cells.UserCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: im.bclpbkiauv.ui.cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: im.bclpbkiauv.ui.cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: im.bclpbkiauv.ui.cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: im.bclpbkiauv.ui.cells.TextDetailCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: im.bclpbkiauv.ui.cells.PollEditTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v16, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r0v9, types: [im.bclpbkiauv.ui.cells.ShadowSectionCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                java.lang.String r0 = "windowBackgroundWhite"
                switch(r10) {
                    case 0: goto L_0x0084;
                    case 1: goto L_0x006c;
                    case 2: goto L_0x005d;
                    case 3: goto L_0x0047;
                    case 4: goto L_0x0038;
                    case 5: goto L_0x002f;
                    case 6: goto L_0x0020;
                    default: goto L_0x0006;
                }
            L_0x0006:
                im.bclpbkiauv.ui.cells.PollEditTextCell r1 = new im.bclpbkiauv.ui.cells.PollEditTextCell
                android.content.Context r2 = r8.mContext
                r3 = 0
                r1.<init>(r2, r3)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                im.bclpbkiauv.ui.ChatRightsEditActivity$ListAdapter$1 r0 = new im.bclpbkiauv.ui.ChatRightsEditActivity$ListAdapter$1
                r0.<init>()
                r1.addTextWatcher(r0)
                r0 = r1
                goto L_0x0095
            L_0x0020:
                im.bclpbkiauv.ui.cells.TextDetailCell r1 = new im.bclpbkiauv.ui.cells.TextDetailCell
                android.content.Context r2 = r8.mContext
                r1.<init>(r2)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x0095
            L_0x002f:
                im.bclpbkiauv.ui.cells.ShadowSectionCell r0 = new im.bclpbkiauv.ui.cells.ShadowSectionCell
                android.content.Context r1 = r8.mContext
                r0.<init>(r1)
                r1 = r0
                goto L_0x0095
            L_0x0038:
                im.bclpbkiauv.ui.cells.TextCheckCell2 r1 = new im.bclpbkiauv.ui.cells.TextCheckCell2
                android.content.Context r2 = r8.mContext
                r1.<init>(r2)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x0095
            L_0x0047:
                im.bclpbkiauv.ui.cells.HeaderCell r1 = new im.bclpbkiauv.ui.cells.HeaderCell
                android.content.Context r3 = r8.mContext
                r4 = 0
                r5 = 21
                r6 = 15
                r7 = 1
                r2 = r1
                r2.<init>(r3, r4, r5, r6, r7)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x0095
            L_0x005d:
                im.bclpbkiauv.ui.cells.TextSettingsCell r1 = new im.bclpbkiauv.ui.cells.TextSettingsCell
                android.content.Context r2 = r8.mContext
                r1.<init>(r2)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x0095
            L_0x006c:
                im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
                android.content.Context r1 = r8.mContext
                r0.<init>(r1)
                r1 = r0
                android.content.Context r0 = r8.mContext
                r2 = 2131231061(0x7f080155, float:1.8078192E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r0, (int) r2, (java.lang.String) r3)
                r1.setBackgroundDrawable(r0)
                goto L_0x0095
            L_0x0084:
                im.bclpbkiauv.ui.cells.UserCell2 r1 = new im.bclpbkiauv.ui.cells.UserCell2
                android.content.Context r2 = r8.mContext
                r3 = 4
                r4 = 0
                r1.<init>(r2, r3, r4)
                int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
            L_0x0095:
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r0 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatRightsEditActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String hint;
            String value;
            String hint2;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ((UserCell2) holder.itemView).setData(ChatRightsEditActivity.this.currentUser, (CharSequence) null, (CharSequence) null, 0);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == ChatRightsEditActivity.this.cantEditInfoRow) {
                        privacyCell.setText(LocaleController.getString("EditAdminCantEdit", R.string.EditAdminCantEdit));
                        return;
                    } else if (position == ChatRightsEditActivity.this.rankInfoRow) {
                        if (!UserObject.isUserSelf(ChatRightsEditActivity.this.currentUser) || !ChatRightsEditActivity.this.currentChat.creator) {
                            hint = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                        } else {
                            hint = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                        }
                        privacyCell.setText(LocaleController.formatString("EditAdminRankInfo", R.string.EditAdminRankInfo, hint));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextSettingsCell actionCell = (TextSettingsCell) holder.itemView;
                    if (position == ChatRightsEditActivity.this.removeAdminRow) {
                        actionCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
                        actionCell.setTag(Theme.key_windowBackgroundWhiteRedText5);
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            actionCell.setText(LocaleController.getString("EditAdminRemoveAdmin", R.string.EditAdminRemoveAdmin), false);
                            return;
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            actionCell.setText(LocaleController.getString("UserRestrictionsBlock", R.string.UserRestrictionsBlock), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ChatRightsEditActivity.this.transferOwnerRow) {
                        actionCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        actionCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        if (ChatRightsEditActivity.this.isChannel) {
                            actionCell.setText(LocaleController.getString("EditAdminChannelTransfer", R.string.EditAdminChannelTransfer), false);
                            return;
                        } else {
                            actionCell.setText(LocaleController.getString("EditAdminGroupTransfer", R.string.EditAdminGroupTransfer), false);
                            return;
                        }
                    } else {
                        return;
                    }
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == 2) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            headerCell.setText(LocaleController.getString("EditAdminWhatCanDo", R.string.EditAdminWhatCanDo));
                            return;
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            headerCell.setText(LocaleController.getString("UserRestrictionsCanDo", R.string.UserRestrictionsCanDo));
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ChatRightsEditActivity.this.rankHeaderRow) {
                        headerCell.setText(LocaleController.getString("EditAdminRank", R.string.EditAdminRank));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextCheckCell2 checkCell = (TextCheckCell2) holder.itemView;
                    int access$400 = ChatRightsEditActivity.this.changeInfoRow;
                    int i = R.drawable.permission_locked;
                    if (position == access$400) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatRightsEditActivity.this.isChannel) {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", R.string.EditAdminChangeChannelInfo), ChatRightsEditActivity.this.adminRights.change_info, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", R.string.EditAdminChangeGroupInfo), ChatRightsEditActivity.this.adminRights.change_info, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsChangeInfo", R.string.UserRestrictionsChangeInfo), !ChatRightsEditActivity.this.bannedRights.change_info && !ChatRightsEditActivity.this.defaultBannedRights.change_info, false);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.change_info) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.postMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", R.string.EditAdminPostMessages), ChatRightsEditActivity.this.adminRights.post_messages, true);
                    } else if (position == ChatRightsEditActivity.this.editMesagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", R.string.EditAdminEditMessages), ChatRightsEditActivity.this.adminRights.edit_messages, true);
                    } else if (position == ChatRightsEditActivity.this.deleteMessagesRow) {
                        if (ChatRightsEditActivity.this.isChannel) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", R.string.EditAdminDeleteMessages), ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        } else {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", R.string.EditAdminGroupDeleteMessages), ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        }
                    } else if (position == ChatRightsEditActivity.this.addAdminsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", R.string.EditAdminAddAdmins), ChatRightsEditActivity.this.adminRights.add_admins, false);
                    } else if (position == ChatRightsEditActivity.this.banUsersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", R.string.EditAdminBanUsers), ChatRightsEditActivity.this.adminRights.ban_users, true);
                    } else if (position == ChatRightsEditActivity.this.addUsersRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatObject.isActionBannedByDefault(ChatRightsEditActivity.this.currentChat, 3)) {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsers", R.string.EditAdminAddUsers), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", R.string.EditAdminAddUsersViaLink), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsInviteUsers", R.string.UserRestrictionsInviteUsers), !ChatRightsEditActivity.this.bannedRights.invite_users && !ChatRightsEditActivity.this.defaultBannedRights.invite_users, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.invite_users) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", R.string.EditAdminPinMessages), ChatRightsEditActivity.this.adminRights.pin_messages, true);
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsPinMessages", R.string.UserRestrictionsPinMessages), !ChatRightsEditActivity.this.bannedRights.pin_messages && !ChatRightsEditActivity.this.defaultBannedRights.pin_messages, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.pin_messages) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSend", R.string.UserRestrictionsSend), !ChatRightsEditActivity.this.bannedRights.send_messages && !ChatRightsEditActivity.this.defaultBannedRights.send_messages, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_messages) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    } else if (position == ChatRightsEditActivity.this.sendMediaRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendMedia", R.string.UserRestrictionsSendMedia), !ChatRightsEditActivity.this.bannedRights.send_media && !ChatRightsEditActivity.this.defaultBannedRights.send_media, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_media) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    } else if (position == ChatRightsEditActivity.this.sendStickersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendStickers", R.string.UserRestrictionsSendStickers), !ChatRightsEditActivity.this.bannedRights.send_stickers && !ChatRightsEditActivity.this.defaultBannedRights.send_stickers, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_stickers) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    } else if (position == ChatRightsEditActivity.this.embedLinksRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsEmbedLinks", R.string.UserRestrictionsEmbedLinks), !ChatRightsEditActivity.this.bannedRights.embed_links && !ChatRightsEditActivity.this.defaultBannedRights.embed_links, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.embed_links) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    } else if (position == ChatRightsEditActivity.this.sendPollsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendPolls", R.string.UserRestrictionsSendPolls), !ChatRightsEditActivity.this.bannedRights.send_polls && !ChatRightsEditActivity.this.defaultBannedRights.send_polls, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_polls) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    }
                    if (position == ChatRightsEditActivity.this.sendMediaRow || position == ChatRightsEditActivity.this.sendStickersRow || position == ChatRightsEditActivity.this.embedLinksRow || position == ChatRightsEditActivity.this.sendPollsRow) {
                        if (!ChatRightsEditActivity.this.bannedRights.send_messages && !ChatRightsEditActivity.this.bannedRights.view_messages && !ChatRightsEditActivity.this.defaultBannedRights.send_messages && !ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                            z = true;
                        }
                        checkCell.setEnabled(z);
                        return;
                    } else if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                        if (!ChatRightsEditActivity.this.bannedRights.view_messages && !ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                            z = true;
                        }
                        checkCell.setEnabled(z);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    ShadowSectionCell shadowCell = (ShadowSectionCell) holder.itemView;
                    int access$3400 = ChatRightsEditActivity.this.rightsShadowRow;
                    int i2 = R.drawable.greydivider;
                    if (position == access$3400) {
                        Context context = this.mContext;
                        if (ChatRightsEditActivity.this.removeAdminRow == -1 && ChatRightsEditActivity.this.rankRow == -1) {
                            i2 = R.drawable.greydivider_bottom;
                        }
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(context, i2, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == ChatRightsEditActivity.this.removeAdminShadowRow) {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == ChatRightsEditActivity.this.rankInfoRow) {
                        Context context2 = this.mContext;
                        if (!ChatRightsEditActivity.this.canEdit) {
                            i2 = R.drawable.greydivider_bottom;
                        }
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, i2, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 6:
                    TextDetailCell detailCell = (TextDetailCell) holder.itemView;
                    if (position == ChatRightsEditActivity.this.untilDateRow) {
                        if (ChatRightsEditActivity.this.bannedRights.until_date == 0 || Math.abs(((long) ChatRightsEditActivity.this.bannedRights.until_date) - (System.currentTimeMillis() / 1000)) > 315360000) {
                            value = LocaleController.getString("UserRestrictionsUntilForever", R.string.UserRestrictionsUntilForever);
                        } else {
                            value = LocaleController.formatDateForBan((long) ChatRightsEditActivity.this.bannedRights.until_date);
                        }
                        detailCell.setTextAndValue(LocaleController.getString("UserRestrictionsDuration", R.string.UserRestrictionsDuration), value, false);
                        return;
                    }
                    return;
                case 7:
                    PollEditTextCell textCell = (PollEditTextCell) holder.itemView;
                    if (!UserObject.isUserSelf(ChatRightsEditActivity.this.currentUser) || !ChatRightsEditActivity.this.currentChat.creator) {
                        hint2 = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                    } else {
                        hint2 = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                    }
                    this.ignoreTextChange = true;
                    textCell.getTextView().setEnabled(ChatRightsEditActivity.this.canEdit || ChatRightsEditActivity.this.currentChat.creator);
                    textCell.getTextView().setSingleLine(true);
                    textCell.getTextView().setImeOptions(6);
                    textCell.setTextAndHint(ChatRightsEditActivity.this.currentRank, hint2, false);
                    this.ignoreTextChange = false;
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == ChatRightsEditActivity.this.rankHeaderRow) {
                ChatRightsEditActivity.this.setTextLeft(holder.itemView);
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == ChatRightsEditActivity.this.rankRow && ChatRightsEditActivity.this.getParentActivity() != null) {
                AndroidUtilities.hideKeyboard(ChatRightsEditActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            if (position == 1 || position == ChatRightsEditActivity.this.rightsShadowRow || position == ChatRightsEditActivity.this.removeAdminShadowRow || position == ChatRightsEditActivity.this.untilSectionRow || position == ChatRightsEditActivity.this.transferOwnerShadowRow) {
                return 5;
            }
            if (position == 2 || position == ChatRightsEditActivity.this.rankHeaderRow) {
                return 3;
            }
            if (position == ChatRightsEditActivity.this.changeInfoRow || position == ChatRightsEditActivity.this.postMessagesRow || position == ChatRightsEditActivity.this.editMesagesRow || position == ChatRightsEditActivity.this.deleteMessagesRow || position == ChatRightsEditActivity.this.addAdminsRow || position == ChatRightsEditActivity.this.banUsersRow || position == ChatRightsEditActivity.this.addUsersRow || position == ChatRightsEditActivity.this.pinMessagesRow || position == ChatRightsEditActivity.this.sendMessagesRow || position == ChatRightsEditActivity.this.sendMediaRow || position == ChatRightsEditActivity.this.sendStickersRow || position == ChatRightsEditActivity.this.embedLinksRow || position == ChatRightsEditActivity.this.sendPollsRow) {
                return 4;
            }
            if (position == ChatRightsEditActivity.this.cantEditInfoRow || position == ChatRightsEditActivity.this.rankInfoRow) {
                return 1;
            }
            if (position == ChatRightsEditActivity.this.untilDateRow) {
                return 6;
            }
            if (position == ChatRightsEditActivity.this.rankRow) {
                return 7;
            }
            return 2;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatRightsEditActivity.this.lambda$getThemeDescriptions$19$ChatRightsEditActivity();
            }
        };
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        RecyclerListView recyclerListView3 = this.listView;
        RecyclerListView recyclerListView4 = recyclerListView3;
        RecyclerListView recyclerListView5 = this.listView;
        RecyclerListView recyclerListView6 = recyclerListView5;
        RecyclerListView recyclerListView7 = this.listView;
        RecyclerListView recyclerListView8 = recyclerListView7;
        RecyclerListView recyclerListView9 = this.listView;
        RecyclerListView recyclerListView10 = recyclerListView9;
        RecyclerListView recyclerListView11 = this.listView;
        RecyclerListView recyclerListView12 = recyclerListView11;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell2.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class, TextDetailCell.class, PollEditTextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText5), new ThemeDescription((View) recyclerListView4, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switch2Track), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switch2TrackChecked), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) recyclerListView6, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText5), new ThemeDescription((View) recyclerListView8, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) recyclerListView10, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) recyclerListView12, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteHintText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_dialogTextBlack), new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_dialogTextGray2), new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_dialogRadioBackground), new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_dialogRadioBackgroundChecked)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$19$ChatRightsEditActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell2) {
                    ((UserCell2) child).update(0);
                }
            }
        }
    }
}
