package im.bclpbkiauv.ui.hui.chats;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import com.bjz.comm.net.utils.HttpUtils;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SecretChatHelper;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.CommonGroupsActivity;
import im.bclpbkiauv.ui.IdenticonActivity;
import im.bclpbkiauv.ui.MediaActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.IdenticonDrawable;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.DialogCommonList;
import im.bclpbkiauv.ui.hcells.MryTextCheckCell;
import im.bclpbkiauv.ui.hcells.PhotoCell;
import im.bclpbkiauv.ui.hcells.TextSettingCell;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.contacts.ContactsUtils;
import im.bclpbkiauv.ui.hui.contacts.GreetEditActivity;
import im.bclpbkiauv.ui.hui.contacts.NoteAndGroupingEditActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageMineActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageOthersActivity;
import im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int addContactsEmptyRow;
    /* access modifiers changed from: private */
    public int addContactsRow;
    /* access modifiers changed from: private */
    public ArrayList<String> albumUrls;
    /* access modifiers changed from: private */
    public int appCodeRow;
    /* access modifiers changed from: private */
    public int audioRow;
    /* access modifiers changed from: private */
    public int blockRow;
    private boolean creatingChat;
    private TLRPC.EncryptedChat currentEncryptedChat;
    /* access modifiers changed from: private */
    public int deleteContactRow;
    /* access modifiers changed from: private */
    public long dialog_id;
    boolean enableFriendMoment = false;
    /* access modifiers changed from: private */
    public int encriptEmptyRow;
    /* access modifiers changed from: private */
    public int encriptRow;
    /* access modifiers changed from: private */
    public int filesRow;
    private boolean forbidAddContact;
    /* access modifiers changed from: private */
    public int fromType;
    /* access modifiers changed from: private */
    public int groupRow;
    /* access modifiers changed from: private */
    public int groupingAndRemarksRow;
    private boolean hasAdminRights;
    /* access modifiers changed from: private */
    public int headerEmptyRow;
    /* access modifiers changed from: private */
    public int headerRow;
    /* access modifiers changed from: private */
    public int hubEmptyRow;
    /* access modifiers changed from: private */
    public int hubRow;
    private boolean isBot;
    /* access modifiers changed from: private */
    public int[] lastMediaCount = {-1, -1, -1, -1, -1};
    /* access modifiers changed from: private */
    public int linksRow;
    @BindView(2131296893)
    RecyclerListView listView;
    private MyAdapter mAdapter;
    /* access modifiers changed from: private */
    public Context mContext;
    @BindView(2131296931)
    LinearLayout mLlBottomBtn;
    private MediaActivity mediaActivity;
    private int[] mediaCount = {-1, -1, -1, -1, -1};
    /* access modifiers changed from: private */
    public int moreInfoRow;
    /* access modifiers changed from: private */
    public int notifyRow;
    private Runnable parseUserFriendAlbumRunnable;
    private boolean parseUserFriendAlbumRunnableIsRunning;
    /* access modifiers changed from: private */
    public int phoneRow;
    /* access modifiers changed from: private */
    public int photosRow;
    private int[] prevMediaCount = {-1, -1, -1, -1, -1};
    private boolean reportSpam;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int sendToSelfEmptyRow;
    /* access modifiers changed from: private */
    public int sendToSelfRow;
    private MediaActivity.SharedMediaData[] sharedMediaData;
    /* access modifiers changed from: private */
    public int signEmptyRow;
    /* access modifiers changed from: private */
    public int signRow;
    @BindView(2131297713)
    TextView tvAddFriend;
    @BindView(2131297843)
    TextView tvSecretChat;
    @BindView(2131297846)
    TextView tvSendMessage;
    /* access modifiers changed from: private */
    public TLRPC.User user;
    /* access modifiers changed from: private */
    public boolean userBlocked;
    private int userGroupId;
    /* access modifiers changed from: private */
    public TLRPCContacts.CL_userFull_v1 userInfo;
    private String userNote = "";
    /* access modifiers changed from: private */
    public int user_id;
    /* access modifiers changed from: private */
    public int voiceRow;

    public NewProfileActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        if (this.arguments != null) {
            this.user_id = this.arguments.getInt("user_id", 0);
            this.forbidAddContact = this.arguments.getBoolean("forbid_add_contact", false);
            this.hasAdminRights = this.arguments.getBoolean("has_admin_right", false);
            this.reportSpam = this.arguments.getBoolean("reportSpam", false);
            this.fromType = this.arguments.getInt("from_type", 0);
            if (this.user_id == 0) {
                return false;
            }
            this.dialog_id = this.arguments.getLong("dialog_id", 0);
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            this.user = user2;
            if (user2 == null) {
                return false;
            }
            if (this.dialog_id != 0) {
                this.currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            }
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
            this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0;
            if (this.user.bot) {
                this.isBot = true;
                MediaDataController.getInstance(this.currentAccount).loadBotInfo(this.user.id, true, this.classGuid);
            }
            TLRPC.UserFull full = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
            if (full instanceof TLRPCContacts.CL_userFull_v1) {
                this.userInfo = (TLRPCContacts.CL_userFull_v1) full;
            }
            MessagesController.getInstance(this.currentAccount).loadFullUser(this.user_id, this.classGuid, true);
        }
        this.sharedMediaData = new MediaActivity.SharedMediaData[5];
        int a = 0;
        while (true) {
            MediaActivity.SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (a < sharedMediaDataArr.length) {
                sharedMediaDataArr[a] = new MediaActivity.SharedMediaData();
                this.sharedMediaData[a].setMaxId(0, this.dialog_id != 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                a++;
            } else {
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountsDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupingChanged);
                return true;
            }
        }
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_new_profile, (ViewGroup) null);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        initActionBar();
        initView();
        updateRow();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("PersonalInfo", R.string.PersonalInfo));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NewProfileActivity.this.finishFragment();
                } else if (id == 0) {
                    Bundle args = new Bundle();
                    args.putInt("user_id", NewProfileActivity.this.user_id);
                    NewProfileActivity.this.presentFragmentFromBottom(new UserProfileShareStepOneActivity(args), false, false);
                }
            }
        });
        if (this.user_id != 777000 && this.user.mutual_contact) {
            this.actionBar.createMenu().addItem(0, (int) R.drawable.msg_shareout);
        }
    }

    private void initView() {
        this.listView.setLayoutManager(new LinearLayoutManager(this.mContext));
        RecyclerListView recyclerListView = this.listView;
        MyAdapter myAdapter = new MyAdapter();
        this.mAdapter = myAdapter;
        recyclerListView.setAdapter(myAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                NewProfileActivity.this.lambda$initView$2$NewProfileActivity(view, i);
            }
        });
        this.mLlBottomBtn.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.tvAddFriend.setTextColor(Theme.getColor(Theme.key_actionBarDefault));
        this.tvSendMessage.setTextColor(Theme.getColor(Theme.key_actionBarDefault));
        this.tvSecretChat.setTextColor(Theme.getColor(Theme.key_actionBarDefault));
        this.tvSecretChat.setText(LocaleController.getString("chat_encrypt", R.string.chat_encrypt));
        this.tvAddFriend.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_bottomBarSelectedColor)));
        this.tvAddFriend.setText(LocaleController.getString("chat_add_contacts", R.string.chat_add_contacts));
        this.tvSendMessage.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_bottomBarSelectedColor)));
        this.tvSendMessage.setText(LocaleController.getString("chat_send_messages", R.string.chat_send_messages));
        this.tvSecretChat.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_bottomBarSelectedColor)));
    }

    public /* synthetic */ void lambda$initView$2$NewProfileActivity(View view, int position) {
        int tab;
        long did;
        long flags;
        int i = position;
        if (getParentActivity() != null) {
            if (i == this.groupRow) {
                presentFragment(new CommonGroupsActivity(this.user_id));
            } else if (i == this.notifyRow) {
                if (this.dialog_id != 0) {
                    did = this.dialog_id;
                } else {
                    did = (long) this.user_id;
                }
                MryTextCheckCell checkCell = (MryTextCheckCell) view;
                boolean checked = true ^ checkCell.isChecked();
                boolean defaultEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(did);
                if (checked) {
                    SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (defaultEnabled) {
                        editor.remove("notify2_" + did);
                    } else {
                        editor.putInt("notify2_" + did, 0);
                    }
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(did, 0);
                    editor.commit();
                    TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(did);
                    if (dialog != null) {
                        dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(did);
                } else {
                    SharedPreferences.Editor editor2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (!defaultEnabled) {
                        editor2.remove("notify2_" + did);
                        flags = 0;
                    } else {
                        editor2.putInt("notify2_" + did, 2);
                        flags = 1;
                    }
                    NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(did);
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(did, flags);
                    editor2.commit();
                    TLRPC.Dialog dialog2 = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(did);
                    if (dialog2 != null) {
                        dialog2.notify_settings = new TLRPC.TL_peerNotifySettings();
                        if (defaultEnabled) {
                            dialog2.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(did);
                }
                checkCell.setChecked(checked);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notifyRow);
                if (holder != null) {
                    this.mAdapter.onBindViewHolder(holder, this.notifyRow);
                }
            } else if (i == this.blockRow) {
                TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                if (user2 != null) {
                    if (!this.isBot || MessagesController.isSupportUser(user2)) {
                        if (this.userBlocked) {
                            MessagesController.getInstance(this.currentAccount).unblockUser(this.user_id);
                            AlertsCreator.showSimpleToast(this, LocaleController.getString("UserUnBlacklisted", R.string.UserUnBlacklisted));
                        } else if (this.reportSpam) {
                            AlertsCreator.showBlockReportSpamAlert(this, (long) this.user_id, user2, (TLRPC.Chat) null, this.currentEncryptedChat, false, (TLRPC.ChatFull) null, new MessagesStorage.IntCallback() {
                                public final void run(int i) {
                                    NewProfileActivity.this.lambda$null$0$NewProfileActivity(i);
                                }
                            });
                        } else {
                            List<String> arrList = new ArrayList<>();
                            arrList.add(LocaleController.formatString("AreYouSureBlockContact3", R.string.AreYouSureBlockContact3, ContactsController.formatName(user2.first_name, user2.last_name)));
                            arrList.add(LocaleController.getString("OK", R.string.OK));
                            DialogCommonList dialogCommonList = new DialogCommonList((Activity) getParentActivity(), arrList, (List<Integer>) null, new int[]{-7631463, -570319}, (DialogCommonList.RecyclerviewItemClickCallBack) new DialogCommonList.RecyclerviewItemClickCallBack() {
                                public final void onRecyclerviewItemClick(int i) {
                                    NewProfileActivity.this.lambda$null$1$NewProfileActivity(i);
                                }
                            }, 1);
                            dialogCommonList.setCancle(Color.parseColor("#222222"), 16);
                            dialogCommonList.show();
                        }
                    } else if (!this.userBlocked) {
                        MessagesController.getInstance(this.currentAccount).blockUser(this.user_id);
                    } else {
                        MessagesController.getInstance(this.currentAccount).unblockUser(this.user_id);
                        SendMessagesHelper.getInstance(this.currentAccount).sendMessage("/start", (long) this.user_id, (MessageObject) null, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                        finishFragment();
                    }
                }
            } else if (i == this.photosRow || i == this.filesRow || i == this.linksRow || i == this.audioRow || i == this.voiceRow) {
                if (i == this.photosRow) {
                    tab = 0;
                } else if (i == this.filesRow) {
                    tab = 1;
                } else if (i == this.linksRow) {
                    tab = 3;
                } else if (i == this.audioRow) {
                    tab = 4;
                } else {
                    tab = 2;
                }
                Bundle args = new Bundle();
                int i2 = this.user_id;
                if (i2 != 0) {
                    long j = this.dialog_id;
                    if (j == 0) {
                        j = (long) i2;
                    }
                    args.putLong("dialog_id", j);
                }
                int[] media = new int[5];
                System.arraycopy(this.lastMediaCount, 0, media, 0, media.length);
                MediaActivity mediaActivity2 = new MediaActivity(args, media, this.sharedMediaData, tab);
                this.mediaActivity = mediaActivity2;
                presentFragment(mediaActivity2);
            } else if (i == this.hubRow) {
                if (this.user != null || this.user_id != 0) {
                    if (this.user_id == getUserConfig().getClientUserId()) {
                        presentFragment(new FcPageMineActivity());
                    } else {
                        presentFragment(new FcPageOthersActivity(this.user_id, this.user.access_hash));
                    }
                }
            } else if (i == this.encriptRow) {
                if (MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32))) instanceof TLRPC.TL_encryptedChat) {
                    Bundle args2 = new Bundle();
                    args2.putInt("chat_id", (int) (this.dialog_id >> 32));
                    presentFragment(new IdenticonActivity(args2));
                }
            } else if (i == this.groupingAndRemarksRow) {
                TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = this.userInfo;
                if (cL_userFull_v1 != null && cL_userFull_v1.getExtendBean() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", this.user_id);
                    bundle.putInt("groupId", this.userInfo.getExtendBean().group_id);
                    bundle.putString("groupName", this.userInfo.getExtendBean().group_name);
                    bundle.putInt("type", 2);
                    presentFragment(new NoteAndGroupingEditActivity(bundle));
                }
            } else if (i == this.moreInfoRow) {
                MoreUserInfoActivity fragment = new MoreUserInfoActivity(this.user_id, this.dialog_id, this.lastMediaCount);
                TLRPCContacts.CL_userFull_v1 cL_userFull_v12 = this.userInfo;
                if (cL_userFull_v12 != null) {
                    fragment.setUserInfo(cL_userFull_v12);
                }
                presentFragment(fragment);
            } else if (i == this.deleteContactRow) {
                deleteContact();
            } else if (i == this.sendToSelfRow) {
                sendMessage();
            } else if (i == this.addContactsRow) {
                jumpToEditGreetActivity();
            }
        }
    }

    public /* synthetic */ void lambda$null$0$NewProfileActivity(int param) {
        if (param == 1) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            finishFragment();
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf((long) this.user_id));
    }

    public /* synthetic */ void lambda$null$1$NewProfileActivity(int which) {
        if (which == 1) {
            MessagesController.getInstance(this.currentAccount).blockUser(this.user_id);
            AlertsCreator.showSimpleToast(this, LocaleController.getString("UserBlacklisted", R.string.UserBlacklisted));
        }
    }

    private void updateRow() {
        TLRPC.User user2 = this.user;
        if (user2 != null) {
            int i = 0;
            if (user2.bot) {
                this.enableFriendMoment = false;
            }
            setViewData();
            this.rowCount = 0;
            this.headerRow = -1;
            this.phoneRow = -1;
            this.headerEmptyRow = -1;
            this.appCodeRow = -1;
            this.groupingAndRemarksRow = -1;
            this.hubRow = -1;
            this.hubEmptyRow = -1;
            this.signRow = -1;
            this.signEmptyRow = -1;
            this.notifyRow = -1;
            this.encriptEmptyRow = -1;
            this.encriptRow = -1;
            this.groupRow = -1;
            this.blockRow = -1;
            this.moreInfoRow = -1;
            this.photosRow = -1;
            this.filesRow = -1;
            this.linksRow = -1;
            this.audioRow = -1;
            this.voiceRow = -1;
            this.deleteContactRow = -1;
            this.sendToSelfEmptyRow = -1;
            this.sendToSelfRow = -1;
            this.addContactsEmptyRow = -1;
            this.addContactsRow = -1;
            if (!this.forbidAddContact || this.user.contact) {
                int i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.headerRow = i2;
                TLRPC.User user3 = this.user;
                if (user3 == null || TextUtils.isEmpty(user3.phone)) {
                }
                TLRPC.User user4 = this.user;
                if (user4 == null || TextUtils.isEmpty(user4.username) || (!this.user.contact && !this.user.self)) {
                }
                if (this.user.contact) {
                    int i3 = this.rowCount;
                    this.rowCount = i3 + 1;
                    this.groupingAndRemarksRow = i3;
                }
                int i4 = this.rowCount;
                int i5 = i4 + 1;
                this.rowCount = i5;
                this.headerEmptyRow = i4;
                if (this.enableFriendMoment) {
                    this.rowCount = i5 + 1;
                    this.hubRow = i5;
                }
                if (this.currentEncryptedChat != null) {
                    int i6 = this.rowCount;
                    int i7 = i6 + 1;
                    this.rowCount = i7;
                    this.encriptEmptyRow = i6;
                    this.rowCount = i7 + 1;
                    this.encriptRow = i7;
                }
                if (this.user.contact) {
                    int i8 = this.rowCount;
                    int i9 = i8 + 1;
                    this.rowCount = i9;
                    this.notifyRow = i8;
                    int i10 = i9 + 1;
                    this.rowCount = i10;
                    this.moreInfoRow = i9;
                    this.rowCount = i10 + 1;
                    this.deleteContactRow = i10;
                } else if (!this.user.self && this.fromType == 2) {
                    int i11 = this.rowCount;
                    int i12 = i11 + 1;
                    this.rowCount = i12;
                    this.signEmptyRow = i11;
                    int i13 = i12 + 1;
                    this.rowCount = i13;
                    this.groupRow = i12;
                    int i14 = i13 + 1;
                    this.rowCount = i14;
                    this.addContactsEmptyRow = i13;
                    this.rowCount = i14 + 1;
                    this.addContactsRow = i14;
                } else if (UserObject.isDeleted(this.user)) {
                    int i15 = this.rowCount;
                    int i16 = i15 + 1;
                    this.rowCount = i16;
                    this.signEmptyRow = i15;
                    this.rowCount = i16 + 1;
                    this.deleteContactRow = i16;
                }
            } else if (this.hasAdminRights) {
                int i17 = this.rowCount;
                this.rowCount = i17 + 1;
                this.headerRow = i17;
                TLRPC.User user5 = this.user;
                if (user5 == null || TextUtils.isEmpty(user5.phone)) {
                }
                TLRPC.User user6 = this.user;
                if (user6 == null || TextUtils.isEmpty(user6.username) || (!this.user.contact && !this.user.self)) {
                }
                if (this.user.contact) {
                    int i18 = this.rowCount;
                    this.rowCount = i18 + 1;
                    this.groupingAndRemarksRow = i18;
                }
                int i19 = this.rowCount;
                int i20 = i19 + 1;
                this.rowCount = i20;
                this.headerEmptyRow = i19;
                if (this.enableFriendMoment) {
                    this.rowCount = i20 + 1;
                    this.hubRow = i20;
                }
                if (this.user.contact) {
                    int i21 = this.rowCount;
                    int i22 = i21 + 1;
                    this.rowCount = i22;
                    this.notifyRow = i21;
                    int i23 = i22 + 1;
                    this.rowCount = i23;
                    this.moreInfoRow = i22;
                    this.rowCount = i23 + 1;
                    this.deleteContactRow = i23;
                } else if (!this.user.self && this.fromType == 2) {
                    int i24 = this.rowCount;
                    int i25 = i24 + 1;
                    this.rowCount = i25;
                    this.groupRow = i24;
                    int i26 = i25 + 1;
                    this.rowCount = i26;
                    this.addContactsEmptyRow = i25;
                    this.rowCount = i26 + 1;
                    this.addContactsRow = i26;
                }
            } else {
                int i27 = this.rowCount;
                int i28 = i27 + 1;
                this.rowCount = i28;
                this.headerRow = i27;
                int i29 = i28 + 1;
                this.rowCount = i29;
                this.headerEmptyRow = i28;
                if (this.enableFriendMoment) {
                    this.rowCount = i29 + 1;
                    this.hubRow = i29;
                }
            }
            if (this.user.self) {
                int i30 = this.rowCount;
                int i31 = i30 + 1;
                this.rowCount = i31;
                this.sendToSelfEmptyRow = i30;
                this.rowCount = i31 + 1;
                this.sendToSelfRow = i31;
                LinearLayout linearLayout = this.mLlBottomBtn;
                if (linearLayout != null) {
                    linearLayout.setVisibility(8);
                }
            } else {
                LinearLayout linearLayout2 = this.mLlBottomBtn;
                if (linearLayout2 != null) {
                    if (UserObject.isDeleted(this.user) || this.user.bot || ((!this.user.contact && this.fromType == 2) || (this.forbidAddContact && !this.user.contact && !this.hasAdminRights))) {
                        i = 8;
                    }
                    linearLayout2.setVisibility(i);
                }
            }
            MyAdapter myAdapter = this.mAdapter;
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setViewData() {
        TLRPC.User user2 = this.user;
        if (user2 != null) {
            if (user2.self) {
                this.tvAddFriend.setVisibility(8);
                this.tvSecretChat.setVisibility(8);
            } else if (!this.forbidAddContact || this.user.contact) {
                if (!this.user.contact) {
                    this.tvSendMessage.setVisibility(8);
                    this.tvSecretChat.setVisibility(8);
                    if (this.user.verified || this.user.bot || this.user.support) {
                        this.tvAddFriend.setVisibility(8);
                    }
                } else {
                    this.tvAddFriend.setVisibility(8);
                    if (this.currentEncryptedChat != null) {
                        this.tvSecretChat.setVisibility(8);
                    }
                }
            } else if (!this.hasAdminRights) {
                this.mLlBottomBtn.setVisibility(8);
            }
        }
        TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = this.userInfo;
        if (!(cL_userFull_v1 == null || cL_userFull_v1.getExtendBean() == null || this.userInfo.getExtendBean().userAlbumsReq == null)) {
            if (this.parseUserFriendAlbumRunnable == null) {
                this.parseUserFriendAlbumRunnable = new Runnable() {
                    public final void run() {
                        NewProfileActivity.this.parseUserFriendAlbums();
                    }
                };
            }
            Utilities.stageQueue.postRunnable(this.parseUserFriendAlbumRunnable);
            this.parseUserFriendAlbumRunnableIsRunning = true;
        }
        this.tvSecretChat.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void parseUserFriendAlbums() {
        TLRPCContacts.CL_userFull_v1_Bean.UserAlbumsBean userAlbumsReq;
        try {
            if (this.userInfo.getExtendBean().moment && (userAlbumsReq = this.userInfo.getExtendBean().userAlbumsReq) != null) {
                ArrayList<TLRPCContacts.CL_userFull_v1_Bean.Albums> albums = userAlbumsReq.albums;
                this.albumUrls = new ArrayList<>();
                if (albums != null && albums.size() > 0) {
                    for (int i = 0; i < albums.size(); i++) {
                        if (albums.get(i) != null && !TextUtils.isEmpty(albums.get(i).Thum)) {
                            ArrayList<String> arrayList = this.albumUrls;
                            arrayList.add(HttpUtils.getInstance().getDownloadFileUrl() + albums.get(i).Thum);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.parseUserFriendAlbumRunnableIsRunning = false;
    }

    private void loadMediaCounts() {
        if (this.dialog_id != 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.dialog_id, this.classGuid);
        } else if (this.user_id != 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts((long) this.user_id, this.classGuid);
        }
    }

    @OnClick({2131297713, 2131297846, 2131297843})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_add_friend) {
            addContact();
        } else if (id == R.id.tv_secret_chat) {
            startSecretChat();
        } else if (id == R.id.tv_send_message) {
            sendMessage();
        }
    }

    private void addContact() {
        if (this.user != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("from_type", this.fromType);
            presentFragment(new AddContactsInfoActivity(bundle, this.user));
        }
    }

    private void sendMessage() {
        TLRPC.User user2 = this.user;
        if (user2 != null && !(user2 instanceof TLRPC.TL_userEmpty)) {
            Bundle args = new Bundle();
            args.putInt("user_id", this.user_id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args), true);
            }
        }
    }

    private void jumpToEditGreetActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        GreetEditActivity greetEditActivity = new GreetEditActivity(bundle);
        greetEditActivity.setDelegate(new GreetEditActivity.GreetEditDelegate() {
            public final void onFinish(String str) {
                NewProfileActivity.this.startContactApply(str);
            }
        });
        presentFragment(greetEditActivity);
    }

    /* access modifiers changed from: private */
    public void startContactApply(String greet) {
        XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
        progressDialog.setLoadingText(LocaleController.getString(R.string.ApplySending));
        TLRPCContacts.ContactsRequestApply req = new TLRPCContacts.ContactsRequestApply();
        req.flag = 0;
        req.from_type = this.fromType;
        req.inputUser = getMessagesController().getInputUser(this.user);
        req.first_name = this.user.first_name;
        req.last_name = this.userNote;
        req.greet = greet;
        req.group_id = this.userGroupId;
        ConnectionsManager connectionsManager = getConnectionsManager();
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ XAlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                NewProfileActivity.this.lambda$startContactApply$5$NewProfileActivity(this.f$1, tLObject, tL_error);
            }
        });
        connectionsManager.bindRequestToGuid(reqId, this.classGuid);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                NewProfileActivity.this.lambda$startContactApply$6$NewProfileActivity(this.f$1, dialogInterface);
            }
        });
        progressDialog.show();
    }

    public /* synthetic */ void lambda$startContactApply$5$NewProfileActivity(XAlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, progressDialog, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ XAlertDialog f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                NewProfileActivity.this.lambda$null$4$NewProfileActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$NewProfileActivity(TLRPC.TL_error error, XAlertDialog progressDialog, TLObject response) {
        TLRPC.TL_updates updates;
        if (error != null) {
            progressDialog.dismiss();
            ToastUtils.show((CharSequence) ContactsUtils.getAboutContactsErrText(error));
            return;
        }
        if (!(!(response instanceof TLRPC.TL_updates) || (updates = (TLRPC.TL_updates) response) == null || updates.updates == null)) {
            getMessagesController().processUpdates(updates, false);
            for (int i = 0; i < updates.updates.size(); i++) {
                if (updates.updates.get(i) instanceof TLRPCContacts.ContactApplyResp) {
                    getMessagesController().saveContactsAppliesId(((TLRPCContacts.ContactApplyResp) updates.updates.get(i)).applyInfo.id);
                }
            }
        }
        progressDialog.setLoadingImage(this.mContext.getResources().getDrawable(R.mipmap.ic_apply_send_done), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(20.0f));
        progressDialog.setLoadingText(LocaleController.getString(R.string.ApplySent));
        this.fragmentView.postDelayed(new Runnable() {
            public final void run() {
                XAlertDialog.this.dismiss();
            }
        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
    }

    public /* synthetic */ void lambda$startContactApply$6$NewProfileActivity(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    private void startSecretChat() {
        XDialog.Builder builder = new XDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AreYouSureSecretChatTitle", R.string.AreYouSureSecretChatTitle));
        builder.setMessage(LocaleController.getString("AreYouSureSecretChat", R.string.AreYouSureSecretChat));
        builder.setPositiveButton(LocaleController.getString("Start", R.string.Start), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                NewProfileActivity.this.lambda$startSecretChat$7$NewProfileActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$startSecretChat$7$NewProfileActivity(DialogInterface dialogInterface, int i) {
        this.creatingChat = true;
        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)));
    }

    private void deleteContact() {
        if (this.user != null && getParentActivity() != null) {
            XDialog.Builder builder = new XDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("DeleteContact", R.string.DeleteContact));
            builder.setMessage(LocaleController.getString("AreYouSureDeleteContact", R.string.AreYouSureDeleteContact));
            builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    NewProfileActivity.this.lambda$deleteContact$9$NewProfileActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            XDialog dialog = builder.create();
            showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }
    }

    public /* synthetic */ void lambda$deleteContact$9$NewProfileActivity(DialogInterface dialogInterface, int i) {
        ArrayList<TLRPC.User> arrayList = new ArrayList<>();
        arrayList.add(this.user);
        ContactsController.getInstance(this.currentAccount).deleteContact(arrayList);
        getMessagesController().deleteDialog((long) this.user.id, 0);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                NewProfileActivity.this.lambda$null$8$NewProfileActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$8$NewProfileActivity() {
        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        finishFragment();
    }

    private void showBolckedUserListDialog() {
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int i;
        int i2;
        RecyclerListView recyclerListView;
        RecyclerListView.Holder holder;
        RecyclerListView.Holder holder2;
        int i3 = id;
        Object[] objArr = args;
        boolean z = false;
        if (i3 == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) objArr[0]).intValue();
            if (this.user_id != 0) {
                if (!(((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0) || (holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.headerRow)) == null)) {
                    this.mAdapter.onBindViewHolder(holder2, this.headerRow);
                }
                if ((mask & 1024) != 0 && (recyclerListView = this.listView) != null && (holder = (RecyclerListView.Holder) recyclerListView.findViewHolderForPosition(this.phoneRow)) != null) {
                    this.mAdapter.onBindViewHolder(holder, this.phoneRow);
                }
            }
        } else if (i3 == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new Runnable(objArr) {
                    private final /* synthetic */ Object[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        NewProfileActivity.this.lambda$didReceivedNotification$10$NewProfileActivity(this.f$1);
                    }
                });
            }
        } else if (i3 == NotificationCenter.blockedUsersDidLoad) {
            boolean oldValue = this.userBlocked;
            if (MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0) {
                z = true;
            }
            this.userBlocked = z;
            if (oldValue != z) {
                updateRow();
                this.mAdapter.notifyDataSetChanged();
            }
        } else if (i3 == NotificationCenter.mediaCountsDidLoad) {
            long uid = ((Long) objArr[0]).longValue();
            long did = this.dialog_id;
            if (did == 0 && (i2 = this.user_id) != 0) {
                did = (long) i2;
            }
            if (uid == did) {
                int[] counts = (int[]) objArr[1];
                if (uid == did) {
                    this.mediaCount = counts;
                }
                int[] iArr = this.lastMediaCount;
                int[] iArr2 = this.prevMediaCount;
                System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
                int a = 0;
                while (true) {
                    int[] iArr3 = this.lastMediaCount;
                    if (a < iArr3.length) {
                        int[] iArr4 = this.mediaCount;
                        if (iArr4[a] >= 0) {
                            iArr3[a] = iArr4[a];
                        } else if (iArr4[a] >= 0) {
                            iArr3[a] = iArr4[a];
                        } else {
                            iArr3[a] = 0;
                        }
                        if (uid == did && this.lastMediaCount[a] != 0) {
                            MediaDataController.getInstance(this.currentAccount).loadMedia(did, 50, 0, a, 2, this.classGuid);
                        }
                        a++;
                    } else {
                        updateRow();
                        return;
                    }
                }
            }
        } else if (i3 == NotificationCenter.mediaCountDidLoad) {
            long uid2 = ((Long) objArr[0]).longValue();
            long did2 = this.dialog_id;
            if (did2 == 0 && (i = this.user_id) != 0) {
                did2 = (long) i;
            }
            if (uid2 == did2) {
                int type = ((Integer) objArr[3]).intValue();
                int mCount = ((Integer) objArr[1]).intValue();
                if (uid2 == did2) {
                    this.mediaCount[type] = mCount;
                }
                int[] iArr5 = this.prevMediaCount;
                int[] iArr6 = this.lastMediaCount;
                iArr5[type] = iArr6[type];
                int[] iArr7 = this.mediaCount;
                if (iArr7[type] >= 0) {
                    iArr6[type] = iArr7[type];
                } else if (iArr7[type] >= 0) {
                    iArr6[type] = iArr7[type];
                } else {
                    iArr6[type] = 0;
                }
                updateRow();
            }
        } else if (i3 == NotificationCenter.userFullInfoDidLoad) {
            if (((Integer) objArr[0]).intValue() == this.user_id && (objArr[1] instanceof TLRPCContacts.CL_userFull_v1)) {
                TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = (TLRPCContacts.CL_userFull_v1) objArr[1];
                this.userInfo = cL_userFull_v1;
                this.user = cL_userFull_v1.user;
                updateRow();
            }
        } else if (i3 == NotificationCenter.groupingChanged) {
            MessagesController.getInstance(this.currentAccount).loadFullUser(this.user_id, this.classGuid, true);
        } else {
            int i4 = NotificationCenter.botInfoDidLoad;
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$10$NewProfileActivity(Object[] args) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        Bundle args2 = new Bundle();
        args2.putInt("enc_id", args[0].id);
        presentFragment(new ChatActivity(args2), true);
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupingChanged);
        super.onFragmentDestroy();
        if (this.parseUserFriendAlbumRunnable != null && this.parseUserFriendAlbumRunnableIsRunning) {
            Utilities.stageQueue.cancelRunnable(this.parseUserFriendAlbumRunnable);
        }
        this.userInfo = null;
        this.currentEncryptedChat = null;
        this.sharedMediaData = null;
        this.parseUserFriendAlbumRunnableIsRunning = false;
    }

    private class MyAdapter extends RecyclerListView.SelectionAdapter {
        private TextView mTvNickName;

        private MyAdapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == NewProfileActivity.this.phoneRow || position == NewProfileActivity.this.hubRow || position == NewProfileActivity.this.photosRow || position == NewProfileActivity.this.encriptRow || position == NewProfileActivity.this.filesRow || position == NewProfileActivity.this.linksRow || position == NewProfileActivity.this.audioRow || position == NewProfileActivity.this.voiceRow || position == NewProfileActivity.this.notifyRow || position == NewProfileActivity.this.groupRow || position == NewProfileActivity.this.appCodeRow || position == NewProfileActivity.this.blockRow || position == NewProfileActivity.this.groupingAndRemarksRow || position == NewProfileActivity.this.moreInfoRow || position == NewProfileActivity.this.deleteContactRow || position == NewProfileActivity.this.sendToSelfRow;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new TextSettingCell(NewProfileActivity.this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 1) {
                view = LayoutInflater.from(NewProfileActivity.this.mContext).inflate(R.layout.item_profile_header, parent, false);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else if (viewType == 2) {
                view = new MryTextCheckCell(NewProfileActivity.this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 3) {
                view = new ShadowSectionCell(NewProfileActivity.this.mContext);
            } else if (viewType == 4) {
                view = LayoutInflater.from(NewProfileActivity.this.mContext).inflate(R.layout.item_send_to_self, parent, false);
            } else if (viewType == 8) {
                view = new PhotoCell(NewProfileActivity.this.mContext);
                view.setPadding(AndroidUtilities.dp(20.0f), 0, 0, 0);
                view.setLayoutParams(new RelativeLayout.LayoutParams(-1, AndroidUtilities.dp(65.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final TLRPC.User user;
            String str;
            int i;
            long did;
            int i2 = position;
            View view = holder.itemView;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingCell textCell = (TextSettingCell) view;
                textCell.getTextView().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                if (i2 == NewProfileActivity.this.appCodeRow) {
                    TLRPC.User user2 = null;
                    if (!(NewProfileActivity.this.userInfo == null || NewProfileActivity.this.userInfo.user == null)) {
                        user2 = NewProfileActivity.this.userInfo.user;
                    }
                    if (user2 == null) {
                        user2 = NewProfileActivity.this.getMessagesController().getUser(Integer.valueOf(NewProfileActivity.this.user_id));
                    }
                    if (user2 == null || TextUtils.isEmpty(user2.username)) {
                        textCell.setVisibility(8);
                    } else {
                        textCell.setTextAndValue(LocaleController.getString("AppNameCode", R.string.AppNameCode), user2.username, (NewProfileActivity.this.phoneRow == -1 && NewProfileActivity.this.groupingAndRemarksRow == -1) ? false : true, false);
                    }
                } else if (i2 == NewProfileActivity.this.phoneRow) {
                    TLRPC.User user3 = null;
                    if (!(NewProfileActivity.this.userInfo == null || NewProfileActivity.this.userInfo.user == null)) {
                        user3 = NewProfileActivity.this.userInfo.user;
                    }
                    if (user3 == null) {
                        user3 = NewProfileActivity.this.getMessagesController().getUser(Integer.valueOf(NewProfileActivity.this.user_id));
                    }
                    if (user3 == null || TextUtils.isEmpty(user3.phone)) {
                        textCell.setVisibility(8);
                    } else {
                        textCell.setText(LocaleController.getString("PhoneNumberSearch", R.string.PhoneNumberSearch) + ": " + user3.phone, NewProfileActivity.this.groupingAndRemarksRow != -1, false);
                    }
                } else if (i2 == NewProfileActivity.this.groupingAndRemarksRow) {
                    textCell.setTextAndValue(LocaleController.getString("GroupingAndRemarks", R.string.GroupingAndRemarks), (NewProfileActivity.this.userInfo == null || NewProfileActivity.this.userInfo.getExtendBean() == null) ? "" : NewProfileActivity.this.userInfo.getExtendBean().group_name, false, true);
                } else if (i2 == NewProfileActivity.this.hubRow) {
                    textCell.setText(LocaleController.getString("FriendHub", R.string.FriendHub), true, true);
                } else if (i2 == NewProfileActivity.this.signRow) {
                    textCell.setTextAndValue(LocaleController.getString("BioDesc", R.string.BioDesc), (NewProfileActivity.this.userInfo == null || TextUtils.isEmpty(NewProfileActivity.this.userInfo.about)) ? LocaleController.getString(R.string.BioNothing) : NewProfileActivity.this.userInfo.about, false, false);
                } else if (i2 == NewProfileActivity.this.photosRow) {
                    textCell.setTextAndValue(LocaleController.getString("SharedPhotosAndVideos", R.string.SharedPhotosAndVideos), String.format("%d", new Object[]{Integer.valueOf(NewProfileActivity.this.lastMediaCount[0])}), i2 != NewProfileActivity.this.rowCount - 1, true);
                } else if (i2 == NewProfileActivity.this.filesRow) {
                    textCell.setTextAndValue(LocaleController.getString("FilesDataUsage", R.string.FilesDataUsage), String.format("%d", new Object[]{Integer.valueOf(NewProfileActivity.this.lastMediaCount[1])}), i2 != NewProfileActivity.this.rowCount - 1, true);
                } else if (i2 == NewProfileActivity.this.linksRow) {
                    textCell.setTextAndValue(LocaleController.getString("SharedLinks", R.string.SharedLinks), String.format("%d", new Object[]{Integer.valueOf(NewProfileActivity.this.lastMediaCount[3])}), i2 != NewProfileActivity.this.rowCount - 1, true);
                } else if (i2 == NewProfileActivity.this.audioRow) {
                    textCell.setTextAndValue(LocaleController.getString("SharedAudioFiles", R.string.SharedAudioFiles), String.format("%d", new Object[]{Integer.valueOf(NewProfileActivity.this.lastMediaCount[4])}), i2 != NewProfileActivity.this.rowCount - 1, true);
                } else if (i2 == NewProfileActivity.this.voiceRow) {
                    textCell.setTextAndValue(LocaleController.getString("AudioAutodownload", R.string.AudioAutodownload), String.format("%d", new Object[]{Integer.valueOf(NewProfileActivity.this.lastMediaCount[2])}), i2 != NewProfileActivity.this.rowCount - 1, true);
                } else if (i2 == NewProfileActivity.this.groupRow) {
                    String string = LocaleController.getString("GroupsInCommonTitle", R.string.GroupsInCommonTitle);
                    Object[] objArr = new Object[1];
                    objArr[0] = Integer.valueOf(NewProfileActivity.this.userInfo != null ? NewProfileActivity.this.userInfo.common_chats_count : 0);
                    textCell.setTextAndValue(string, String.format("%d个", objArr), i2 != NewProfileActivity.this.rowCount - 1, true);
                } else if (i2 == NewProfileActivity.this.encriptRow) {
                    IdenticonDrawable identiconDrawable = new IdenticonDrawable();
                    TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance(NewProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (NewProfileActivity.this.dialog_id >> 32)));
                    if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
                        identiconDrawable.setEncryptedChat(encryptedChat);
                        textCell.setTextAndValueDrawable(LocaleController.getString("EncryptionKey", R.string.EncryptionKey), identiconDrawable, false);
                        textCell.setEnabled(true);
                    } else {
                        textCell.setTextAndValue(LocaleController.getString("EncryptionKey", R.string.EncryptionKey), "loading", false);
                        textCell.setEnabled(false);
                    }
                } else if (i2 == NewProfileActivity.this.moreInfoRow) {
                    textCell.setText(LocaleController.getString("MoreInformation", R.string.MoreInformation), i2 != NewProfileActivity.this.rowCount - 1, true);
                } else if (i2 == NewProfileActivity.this.deleteContactRow) {
                    textCell.getTextView().setTextColor(NewProfileActivity.this.mContext.getResources().getColor(R.color.color_item_menu_red_f74c31));
                    textCell.setText(LocaleController.getString("DeleteContact", R.string.DeleteContact), false, false);
                }
                if ((NewProfileActivity.this.user.self && i2 == NewProfileActivity.this.rowCount - 3) || (!NewProfileActivity.this.user.self && i2 == NewProfileActivity.this.rowCount - 1)) {
                    view.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            } else if (itemViewType == 1) {
                TextView txtAccount = (TextView) view.findViewById(R.id.tv_account);
                final BackupImageView ivAvatar = (BackupImageView) view.findViewById(R.id.iv_avatar);
                ivAvatar.setRoundRadius(AndroidUtilities.dp(7.5f));
                ivAvatar.setPivotX(0.0f);
                ivAvatar.setPivotY(0.0f);
                ivAvatar.setContentDescription(LocaleController.getString("AccDescrProfilePicture", R.string.AccDescrProfilePicture));
                TextView textView = (TextView) view.findViewById(R.id.tv_nick_name);
                this.mTvNickName = textView;
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                ImageView ivGender = (ImageView) view.findViewById(R.id.iv_gender);
                ivGender.setVisibility(8);
                TextView tvUpdateTime = (TextView) view.findViewById(R.id.tv_update_time);
                MryRoundButton btnBotFollow = (MryRoundButton) view.findViewById(R.id.btnBotFollow);
                ImageView ivCall = (ImageView) view.findViewById(R.id.iv_call);
                View divider = view.findViewById(R.id.divider);
                divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
                if (NewProfileActivity.this.phoneRow == -1 && NewProfileActivity.this.appCodeRow == -1 && NewProfileActivity.this.groupingAndRemarksRow == -1) {
                    divider.setVisibility(8);
                }
                ivCall.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton), PorterDuff.Mode.SRC_IN));
                ivCall.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarDefaultSelector)));
                ivCall.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        NewProfileActivity.MyAdapter.this.lambda$onBindViewHolder$0$NewProfileActivity$MyAdapter(view);
                    }
                });
                ivCall.setVisibility(8);
                if (NewProfileActivity.this.userInfo == null || NewProfileActivity.this.userInfo.getExtendBean() == null) {
                    ivGender.setVisibility(8);
                } else {
                    int sex = NewProfileActivity.this.userInfo.getExtendBean().sex;
                    ivGender.setImageResource(sex == 1 ? R.mipmap.ic_male : sex == 2 ? R.mipmap.ic_female : 0);
                    ivGender.setVisibility(0);
                }
                if (NewProfileActivity.this.userInfo == null || NewProfileActivity.this.userInfo.user == null) {
                    user = NewProfileActivity.this.getMessagesController().getUser(Integer.valueOf(NewProfileActivity.this.user_id));
                } else {
                    user = NewProfileActivity.this.userInfo.user;
                }
                if (user == null || !user.contact || user.username == null || "null".equals(user.username)) {
                    txtAccount.setVisibility(8);
                } else {
                    txtAccount.setText(LocaleController.getString("AppNameCode", R.string.AppNameCode) + LogUtils.COLON + user.username);
                }
                if ((user.self && i2 == NewProfileActivity.this.rowCount - 3) || (!user.self && i2 == NewProfileActivity.this.rowCount - 1)) {
                    view.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
                if (user != null) {
                    AvatarDrawable avatarDrawable = new AvatarDrawable(user);
                    avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
                    ivAvatar.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
                    if (user.bot) {
                        tvUpdateTime.setText(LocaleController.getString(R.string.Bot));
                    } else {
                        tvUpdateTime.setText(LocaleController.formatUserStatus(NewProfileActivity.this.currentAccount, user, (boolean[]) null));
                    }
                    this.mTvNickName.setText(user.first_name);
                    if (!user.contact || user.bot) {
                        ivCall.setVisibility(8);
                    }
                    if (user.bot && NewProfileActivity.this.userInfo != null) {
                        btnBotFollow.setPrimaryRadiusAdjustBoundsStrokeStyle();
                        if (!NewProfileActivity.this.userBlocked) {
                            btnBotFollow.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(NewProfileActivity.this.getParentActivity(), R.mipmap.ic_bot_followed), (Drawable) null, (Drawable) null, (Drawable) null);
                            btnBotFollow.setStrokeData(AndroidUtilities.dp(0.5f), -4473925);
                            btnBotFollow.setTextColor(-8882056);
                            btnBotFollow.setText(LocaleController.getString(R.string.attentioned));
                        } else {
                            btnBotFollow.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(NewProfileActivity.this.getParentActivity(), R.mipmap.ic_bot_follow), (Drawable) null, (Drawable) null, (Drawable) null);
                            btnBotFollow.setStrokeData(AndroidUtilities.dp(0.5f), -367616);
                            btnBotFollow.setTextColor(-367616);
                            btnBotFollow.setText(LocaleController.getString(R.string.attention));
                        }
                        if (btnBotFollow.getVisibility() != 0) {
                            btnBotFollow.setVisibility(0);
                        }
                        btnBotFollow.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (!NewProfileActivity.this.userBlocked) {
                                    List<String> arrList = new ArrayList<>();
                                    arrList.add(LocaleController.formatString("AreYouSureBlockContact3", R.string.AreYouSureBlockContact3, ContactsController.formatName(user.first_name, user.last_name)));
                                    arrList.add(LocaleController.getString("fc_cancel_followed", R.string.fc_cancel_followed));
                                    DialogCommonList dialogCommonList = new DialogCommonList((Activity) NewProfileActivity.this.getParentActivity(), arrList, (List<Integer>) null, new int[]{-7631463, -570319}, (DialogCommonList.RecyclerviewItemClickCallBack) 
                                    /*  JADX ERROR: Method code generation error
                                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0052: CONSTRUCTOR  (r1v11 'dialogCommonList' im.bclpbkiauv.ui.dialogs.DialogCommonList) = 
                                          (wrap: androidx.fragment.app.FragmentActivity : 0x0045: INVOKE  (r2v4 androidx.fragment.app.FragmentActivity) = 
                                          (wrap: im.bclpbkiauv.ui.hui.chats.NewProfileActivity : 0x0043: IGET  (r1v10 im.bclpbkiauv.ui.hui.chats.NewProfileActivity) = 
                                          (wrap: im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter : 0x0041: IGET  (r1v9 im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter) = 
                                          (r13v0 'this' im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter$1 A[THIS])
                                         im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.1.this$1 im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter)
                                         im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.this$0 im.bclpbkiauv.ui.hui.chats.NewProfileActivity)
                                         im.bclpbkiauv.ui.hui.chats.NewProfileActivity.getParentActivity():androidx.fragment.app.FragmentActivity type: VIRTUAL)
                                          (r0v15 'arrList' java.util.List<java.lang.String>)
                                          (null java.util.List)
                                          (wrap: int[] : 0x003a: FILLED_NEW_ARRAY  (r5v3 'iTextColor' int[]) = (-7631463 int), (-570319 int) elemType: int)
                                          (wrap: im.bclpbkiauv.ui.hui.chats.-$$Lambda$NewProfileActivity$MyAdapter$1$i4SDDWcIjFXRmVK2PiLd_RxIMs8 : 0x004c: CONSTRUCTOR  (r6v1 im.bclpbkiauv.ui.hui.chats.-$$Lambda$NewProfileActivity$MyAdapter$1$i4SDDWcIjFXRmVK2PiLd_RxIMs8) = 
                                          (r13v0 'this' im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter$1 A[THIS])
                                         call: im.bclpbkiauv.ui.hui.chats.-$$Lambda$NewProfileActivity$MyAdapter$1$i4SDDWcIjFXRmVK2PiLd_RxIMs8.<init>(im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter$1):void type: CONSTRUCTOR)
                                          (1 int)
                                         call: im.bclpbkiauv.ui.dialogs.DialogCommonList.<init>(android.app.Activity, java.util.List, java.util.List, int[], im.bclpbkiauv.ui.dialogs.DialogCommonList$RecyclerviewItemClickCallBack, int):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.1.onClick(android.view.View):void, dex: classes6.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x004c: CONSTRUCTOR  (r6v1 im.bclpbkiauv.ui.hui.chats.-$$Lambda$NewProfileActivity$MyAdapter$1$i4SDDWcIjFXRmVK2PiLd_RxIMs8) = 
                                          (r13v0 'this' im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter$1 A[THIS])
                                         call: im.bclpbkiauv.ui.hui.chats.-$$Lambda$NewProfileActivity$MyAdapter$1$i4SDDWcIjFXRmVK2PiLd_RxIMs8.<init>(im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter$1):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.1.onClick(android.view.View):void, dex: classes6.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	... 100 more
                                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.hui.chats.-$$Lambda$NewProfileActivity$MyAdapter$1$i4SDDWcIjFXRmVK2PiLd_RxIMs8, state: NOT_LOADED
                                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	... 106 more
                                        */
                                    /*
                                        this = this;
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.this
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.this
                                        boolean r0 = r0.userBlocked
                                        if (r0 != 0) goto L_0x0064
                                        java.util.ArrayList r0 = new java.util.ArrayList
                                        r0.<init>()
                                        r1 = 2131689884(0x7f0f019c, float:1.9008796E38)
                                        r2 = 1
                                        java.lang.Object[] r2 = new java.lang.Object[r2]
                                        r3 = 0
                                        im.bclpbkiauv.tgnet.TLRPC$User r4 = r11
                                        java.lang.String r4 = r4.first_name
                                        im.bclpbkiauv.tgnet.TLRPC$User r5 = r11
                                        java.lang.String r5 = r5.last_name
                                        java.lang.String r4 = im.bclpbkiauv.messenger.ContactsController.formatName(r4, r5)
                                        r2[r3] = r4
                                        java.lang.String r3 = "AreYouSureBlockContact3"
                                        java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r1, r2)
                                        r0.add(r1)
                                        r1 = 2131695063(0x7f0f15d7, float:1.90193E38)
                                        java.lang.String r2 = "fc_cancel_followed"
                                        java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                                        r0.add(r1)
                                        r1 = 2
                                        int[] r5 = new int[r1]
                                        r5 = {-7631463, -570319} // fill-array
                                        im.bclpbkiauv.ui.dialogs.DialogCommonList r8 = new im.bclpbkiauv.ui.dialogs.DialogCommonList
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter r1 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.this
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity r1 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.this
                                        androidx.fragment.app.FragmentActivity r2 = r1.getParentActivity()
                                        r4 = 0
                                        im.bclpbkiauv.ui.hui.chats.-$$Lambda$NewProfileActivity$MyAdapter$1$i4SDDWcIjFXRmVK2PiLd_RxIMs8 r6 = new im.bclpbkiauv.ui.hui.chats.-$$Lambda$NewProfileActivity$MyAdapter$1$i4SDDWcIjFXRmVK2PiLd_RxIMs8
                                        r6.<init>(r13)
                                        r7 = 1
                                        r1 = r8
                                        r3 = r0
                                        r1.<init>((android.app.Activity) r2, (java.util.List<java.lang.String>) r3, (java.util.List<java.lang.Integer>) r4, (int[]) r5, (im.bclpbkiauv.ui.dialogs.DialogCommonList.RecyclerviewItemClickCallBack) r6, (int) r7)
                                        java.lang.String r2 = "#222222"
                                        int r2 = android.graphics.Color.parseColor(r2)
                                        r3 = 16
                                        r1.setCancle(r2, r3)
                                        r1.show()
                                        goto L_0x00a4
                                    L_0x0064:
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.this
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.this
                                        int r0 = r0.currentAccount
                                        im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter r1 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.this
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity r1 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.this
                                        int r1 = r1.user_id
                                        r0.unblockUser(r1)
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.this
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.this
                                        int r0 = r0.currentAccount
                                        im.bclpbkiauv.messenger.SendMessagesHelper r1 = im.bclpbkiauv.messenger.SendMessagesHelper.getInstance(r0)
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.this
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.this
                                        int r0 = r0.user_id
                                        long r3 = (long) r0
                                        r5 = 0
                                        r6 = 0
                                        r7 = 0
                                        r8 = 0
                                        r9 = 0
                                        r10 = 0
                                        r11 = 1
                                        r12 = 0
                                        java.lang.String r2 = "/start"
                                        r1.sendMessage(r2, r3, r5, r6, r7, r8, r9, r10, r11, r12)
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity$MyAdapter r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.this
                                        im.bclpbkiauv.ui.hui.chats.NewProfileActivity r0 = im.bclpbkiauv.ui.hui.chats.NewProfileActivity.this
                                        r0.finishFragment()
                                    L_0x00a4:
                                        return
                                    */
                                    throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.chats.NewProfileActivity.MyAdapter.AnonymousClass1.onClick(android.view.View):void");
                                }

                                public /* synthetic */ void lambda$onClick$0$NewProfileActivity$MyAdapter$1(int which) {
                                    if (which == 1) {
                                        MessagesController.getInstance(NewProfileActivity.this.currentAccount).blockUser(NewProfileActivity.this.user_id);
                                    }
                                }
                            });
                        } else if (btnBotFollow.getVisibility() != 8) {
                            btnBotFollow.setVisibility(8);
                        }
                        ivAvatar.setOnClickListener(new View.OnClickListener(new PhotoViewer.EmptyPhotoViewerProvider() {
                            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
                                TLRPC.Chat chat;
                                if (fileLocation == null || NewProfileActivity.this.isFinishing()) {
                                    return null;
                                }
                                TLRPC.FileLocation photoBig = null;
                                if (NewProfileActivity.this.user_id != 0) {
                                    TLRPC.User user = MessagesController.getInstance(NewProfileActivity.this.currentAccount).getUser(Integer.valueOf(NewProfileActivity.this.user_id));
                                    if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                                        photoBig = user.photo.photo_big;
                                    }
                                } else if (!(NewProfileActivity.this.dialog_id == 0 || (chat = MessagesController.getInstance(NewProfileActivity.this.currentAccount).getChat(Integer.valueOf((int) NewProfileActivity.this.dialog_id))) == null || chat.photo == null || chat.photo.photo_big == null)) {
                                    photoBig = chat.photo.photo_big;
                                }
                                if (photoBig == null || photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
                                    return null;
                                }
                                int[] coords = new int[2];
                                ivAvatar.getLocationInWindow(coords);
                                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                                object.viewX = coords[0];
                                object.viewY = coords[1];
                                object.parentView = ivAvatar;
                                object.imageReceiver = ivAvatar.getImageReceiver();
                                if (NewProfileActivity.this.user_id != 0) {
                                    object.dialogId = NewProfileActivity.this.user_id;
                                } else if (NewProfileActivity.this.dialog_id != 0) {
                                    object.dialogId = (int) (-NewProfileActivity.this.dialog_id);
                                }
                                object.thumb = object.imageReceiver.getBitmapSafe();
                                object.size = -1;
                                object.radius = ivAvatar.getImageReceiver().getRoundRadius();
                                object.scale = ivAvatar.getScaleX();
                                return object;
                            }

                            public void willHidePhotoViewer() {
                                ivAvatar.getImageReceiver().setVisible(true, true);
                            }
                        }) {
                            private final /* synthetic */ PhotoViewer.PhotoViewerProvider f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                NewProfileActivity.MyAdapter.this.lambda$onBindViewHolder$1$NewProfileActivity$MyAdapter(this.f$1, view);
                            }
                        });
                    }
                    this.mTvNickName.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                } else if (itemViewType == 2) {
                    MryTextCheckCell checkCell = (MryTextCheckCell) view;
                    if (i2 == NewProfileActivity.this.notifyRow) {
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(NewProfileActivity.this.currentAccount);
                        if (NewProfileActivity.this.dialog_id != 0) {
                            did = NewProfileActivity.this.dialog_id;
                        } else {
                            did = (long) NewProfileActivity.this.user_id;
                        }
                        boolean enabled = false;
                        boolean hasOverride = preferences.contains("notify2_" + did);
                        int value = preferences.getInt("notify2_" + did, 0);
                        int delta = preferences.getInt("notifyuntil_" + did, 0);
                        if (value != 3 || delta == Integer.MAX_VALUE) {
                            if (value == 0) {
                                if (hasOverride) {
                                    enabled = true;
                                } else {
                                    enabled = NotificationsController.getInstance(NewProfileActivity.this.currentAccount).isGlobalNotificationsEnabled(did);
                                }
                            } else if (value == 1) {
                                enabled = true;
                            } else if (value == 2) {
                                enabled = false;
                            } else {
                                enabled = false;
                            }
                        } else if (delta - ConnectionsManager.getInstance(NewProfileActivity.this.currentAccount).getCurrentTime() <= 0) {
                            enabled = true;
                        }
                        checkCell.setTextAndCheck(LocaleController.getString("MessageNotifications", R.string.MessageNotifications), enabled, true);
                    } else if (i2 == NewProfileActivity.this.blockRow) {
                        if (NewProfileActivity.this.userBlocked) {
                            i = R.string.RemoveToBlacklist;
                            str = "RemoveToBlacklist";
                        } else {
                            i = R.string.AddToBlacklist;
                            str = "AddToBlacklist";
                        }
                        checkCell.setTextAndCheck(LocaleController.getString(str, i), NewProfileActivity.this.userBlocked, (NewProfileActivity.this.user.contact || NewProfileActivity.this.fromType != 2) && i2 != NewProfileActivity.this.rowCount - 1);
                        if (i2 == NewProfileActivity.this.rowCount - 1) {
                            checkCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                        }
                    }
                } else if (itemViewType == 4) {
                    ImageView ivSend = (ImageView) view.findViewById(R.id.iv_send);
                    MryTextView tvSend = (MryTextView) view.findViewById(R.id.tv_send);
                    if (i2 == NewProfileActivity.this.sendToSelfRow) {
                        view.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    } else if (i2 == NewProfileActivity.this.addContactsRow) {
                        tvSend.setText(LocaleController.getString("AddToContacts", R.string.AddToContacts));
                        ivSend.setVisibility(8);
                        view.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    }
                } else if (itemViewType == 8) {
                    PhotoCell photoCell = (PhotoCell) view;
                    photoCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    photoCell.setText(LocaleController.getString("FriendHub", R.string.FriendHub), true);
                    photoCell.setData(NewProfileActivity.this.albumUrls == null ? new ArrayList() : NewProfileActivity.this.albumUrls);
                    photoCell.setListener(new PhotoCell.OnPhotoCellClickListener() {
                        public void onPhotoClick(ImageView view, int position, String url) {
                            if (NewProfileActivity.this.user != null || NewProfileActivity.this.user_id != 0) {
                                if (NewProfileActivity.this.user_id == NewProfileActivity.this.getUserConfig().getClientUserId()) {
                                    NewProfileActivity.this.presentFragment(new FcPageMineActivity());
                                } else {
                                    NewProfileActivity.this.presentFragment(new FcPageOthersActivity(NewProfileActivity.this.user_id, NewProfileActivity.this.user.access_hash));
                                }
                            }
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$onBindViewHolder$0$NewProfileActivity$MyAdapter(View v) {
                TLRPC.User user = MessagesController.getInstance(NewProfileActivity.this.currentAccount).getUser(Integer.valueOf(NewProfileActivity.this.user_id));
                if (user != null) {
                    NewProfileActivity.this.startCall(user);
                }
            }

            public /* synthetic */ void lambda$onBindViewHolder$1$NewProfileActivity$MyAdapter(PhotoViewer.PhotoViewerProvider provider, View v) {
                if (NewProfileActivity.this.isFinishing()) {
                    return;
                }
                if (NewProfileActivity.this.user_id != 0) {
                    TLRPC.User user1 = MessagesController.getInstance(NewProfileActivity.this.currentAccount).getUser(Integer.valueOf(NewProfileActivity.this.user_id));
                    if (user1.photo != null && user1.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(NewProfileActivity.this.getParentActivity());
                        if (user1.photo.dc_id != 0) {
                            user1.photo.photo_big.dc_id = user1.photo.dc_id;
                        }
                        PhotoViewer.getInstance().openPhoto(user1.photo.photo_big, provider);
                    }
                } else if (NewProfileActivity.this.dialog_id != 0) {
                    TLRPC.Chat chat = MessagesController.getInstance(NewProfileActivity.this.currentAccount).getChat(Integer.valueOf((int) NewProfileActivity.this.dialog_id));
                    if (chat.photo != null && chat.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(NewProfileActivity.this.getParentActivity());
                        if (chat.photo.dc_id != 0) {
                            chat.photo.photo_big.dc_id = chat.photo.dc_id;
                        }
                        PhotoViewer.getInstance().openPhoto(chat.photo.photo_big, provider);
                    }
                }
            }

            public int getItemCount() {
                return NewProfileActivity.this.rowCount;
            }

            public int getItemViewType(int position) {
                if (position == NewProfileActivity.this.headerRow) {
                    return 1;
                }
                if (position == NewProfileActivity.this.notifyRow || position == NewProfileActivity.this.blockRow) {
                    return 2;
                }
                if (position == NewProfileActivity.this.hubEmptyRow || position == NewProfileActivity.this.signEmptyRow || position == NewProfileActivity.this.headerEmptyRow || position == NewProfileActivity.this.sendToSelfEmptyRow || position == NewProfileActivity.this.addContactsEmptyRow || position == NewProfileActivity.this.encriptEmptyRow) {
                    return 3;
                }
                if (position == NewProfileActivity.this.sendToSelfRow || position == NewProfileActivity.this.addContactsRow) {
                    return 4;
                }
                if (position == NewProfileActivity.this.hubRow) {
                    return 8;
                }
                return 0;
            }
        }

        /* access modifiers changed from: private */
        public void startCall(TLRPC.User user2) {
            List<String> list = new ArrayList<>();
            list.add(LocaleController.getString("menu_voice_chat", R.string.menu_voice_chat));
            list.add(LocaleController.getString("menu_video_chat", R.string.menu_video_chat));
            List<Integer> list1 = new ArrayList<>();
            list1.add(Integer.valueOf(R.drawable.menu_voice_call));
            list1.add(Integer.valueOf(R.drawable.menu_video_call));
            new DialogCommonList((Activity) getParentActivity(), list, list1, Color.parseColor("#222222"), (DialogCommonList.RecyclerviewItemClickCallBack) new DialogCommonList.RecyclerviewItemClickCallBack(user2) {
                private final /* synthetic */ TLRPC.User f$1;

                {
                    this.f$1 = r2;
                }

                public final void onRecyclerviewItemClick(int i) {
                    NewProfileActivity.this.lambda$startCall$11$NewProfileActivity(this.f$1, i);
                }
            }, 1).show();
        }

        public /* synthetic */ void lambda$startCall$11$NewProfileActivity(TLRPC.User user2, int position) {
            if (position == 0) {
                if (ApplicationLoader.mbytAVideoCallBusy != 0) {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_busing_tip", R.string.visual_call_busing_tip));
                } else if (user2.mutual_contact) {
                    int currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
                    if (currentConnectionState == 2 || currentConnectionState == 1) {
                        ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_network", R.string.visual_call_no_network));
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(getParentActivity(), VisualCallActivity.class);
                    intent.putExtra("CallType", 1);
                    ArrayList<Integer> ArrInputPeers = new ArrayList<>();
                    ArrInputPeers.add(Integer.valueOf(user2.id));
                    intent.putExtra("ArrayUser", ArrInputPeers);
                    intent.putExtra("channel", new ArrayList());
                    getParentActivity().startActivity(intent);
                } else {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_friend_tip", R.string.visual_call_no_friend_tip));
                }
            } else if (position != 1) {
            } else {
                if (ApplicationLoader.mbytAVideoCallBusy != 0) {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_busing_tip", R.string.visual_call_busing_tip));
                } else if (user2.mutual_contact) {
                    int currentConnectionState2 = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
                    if (currentConnectionState2 == 2 || currentConnectionState2 == 1) {
                        ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_network", R.string.visual_call_no_network));
                        return;
                    }
                    Intent intent2 = new Intent();
                    intent2.setClass(getParentActivity(), VisualCallActivity.class);
                    intent2.putExtra("CallType", 2);
                    ArrayList<Integer> ArrInputPeers2 = new ArrayList<>();
                    ArrInputPeers2.add(Integer.valueOf(user2.id));
                    intent2.putExtra("ArrayUser", ArrInputPeers2);
                    intent2.putExtra("channel", new ArrayList());
                    getParentActivity().startActivity(intent2);
                } else {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_friend_tip", R.string.visual_call_no_friend_tip));
                }
            }
        }
    }
