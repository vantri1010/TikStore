package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
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
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCChats;
import im.bclpbkiauv.ui.ChatRightsEditActivity;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.GroupCreateActivity;
import im.bclpbkiauv.ui.MediaActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.ProfileActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.AboutLinkCell;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.NotificationsCheckCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.TextDetailCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.IdenticonDrawable;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ScamDrawable;
import im.bclpbkiauv.ui.components.UndoView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import im.bclpbkiauv.ui.hui.chats.GroupShareActivity;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.mine.QrCodeActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Marker;

public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    private static final int add_contact = 1;
    private static final int add_member = 18;
    private static final int add_shortcut = 14;
    private static final int block_contact = 2;
    private static final int call_item = 15;
    private static final int delete_contact = 5;
    private static final int edit_channel = 12;
    private static final int edit_contact = 4;
    private static final int invite_to_group = 9;
    private static final int leave_group = 7;
    private static final int search_members = 17;
    private static final int share = 10;
    private static final int share_contact = 3;
    private static final int statistics = 19;
    /* access modifiers changed from: private */
    public int addMemberRow;
    /* access modifiers changed from: private */
    public int administratorsRow;
    private boolean allowProfileAnimation = true;
    /* access modifiers changed from: private */
    public ActionBarMenuItem animatingItem;
    private float animationProgress;
    /* access modifiers changed from: private */
    public int audioRow;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    private int banFromGroup;
    /* access modifiers changed from: private */
    public int blockedUsersRow;
    /* access modifiers changed from: private */
    public TLRPC.BotInfo botInfo;
    private ActionBarMenuItem callItem;
    /* access modifiers changed from: private */
    public int channelInfoRow;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull chatInfo;
    /* access modifiers changed from: private */
    public int chat_id;
    private boolean creatingChat;
    private TLRPC.ChannelParticipant currentChannelParticipant;
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    /* access modifiers changed from: private */
    public TLRPC.EncryptedChat currentEncryptedChat;
    /* access modifiers changed from: private */
    public long dialog_id;
    private ActionBarMenuItem editItem;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public int extraHeight;
    /* access modifiers changed from: private */
    public int filesRow;
    /* access modifiers changed from: private */
    public int forbidAddContact;
    /* access modifiers changed from: private */
    public int groupsInCommonRow;
    /* access modifiers changed from: private */
    public int infoHeaderRow;
    /* access modifiers changed from: private */
    public int infoSectionRow;
    private int initialAnimationExtraHeight;
    /* access modifiers changed from: private */
    public boolean isBot;
    private boolean[] isOnline = new boolean[1];
    /* access modifiers changed from: private */
    public int joinRow;
    /* access modifiers changed from: private */
    public int[] lastMediaCount = {-1, -1, -1, -1, -1};
    /* access modifiers changed from: private */
    public int lastSectionRow;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public int leaveChannelRow;
    /* access modifiers changed from: private */
    public int linksRow;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private boolean loadingUsers;
    /* access modifiers changed from: private */
    public int locationRow;
    private MediaActivity mediaActivity;
    private int[] mediaCount = {-1, -1, -1, -1, -1};
    private int[] mediaMergeCount = {-1, -1, -1, -1, -1};
    /* access modifiers changed from: private */
    public int membersEndRow;
    /* access modifiers changed from: private */
    public int membersHeaderRow;
    /* access modifiers changed from: private */
    public int membersSectionRow;
    /* access modifiers changed from: private */
    public int membersStartRow;
    private long mergeDialogId;
    private SimpleTextView[] nameTextView = new SimpleTextView[2];
    /* access modifiers changed from: private */
    public int notificationsDividerRow;
    /* access modifiers changed from: private */
    public int notificationsRow;
    private int onlineCount = -1;
    private SimpleTextView[] onlineTextView = new SimpleTextView[2];
    private boolean openAnimationInProgress;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC.ChatParticipant> participantsMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public int phoneRow;
    /* access modifiers changed from: private */
    public int photosRow;
    /* access modifiers changed from: private */
    public boolean playProfileAnimation;
    private int[] prevMediaCount = {-1, -1, -1, -1, -1};
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            TLRPC.Chat chat;
            if (fileLocation == null) {
                return null;
            }
            TLRPC.FileLocation photoBig = null;
            if (ProfileActivity.this.user_id != 0) {
                TLRPC.User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                    photoBig = user.photo.photo_big;
                }
            } else if (!(ProfileActivity.this.chat_id == 0 || (chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id))) == null || chat.photo == null || chat.photo.photo_big == null)) {
                photoBig = chat.photo.photo_big;
            }
            if (photoBig == null || photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
                return null;
            }
            int[] coords = new int[2];
            ProfileActivity.this.avatarImage.getLocationInWindow(coords);
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            int i = 0;
            object.viewX = coords[0];
            int i2 = coords[1];
            if (Build.VERSION.SDK_INT < 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            object.viewY = i2 - i;
            object.parentView = ProfileActivity.this.avatarImage;
            object.imageReceiver = ProfileActivity.this.avatarImage.getImageReceiver();
            if (ProfileActivity.this.user_id != 0) {
                object.dialogId = ProfileActivity.this.user_id;
            } else if (ProfileActivity.this.chat_id != 0) {
                object.dialogId = -ProfileActivity.this.chat_id;
            }
            object.thumb = object.imageReceiver.getBitmapSafe();
            object.size = -1;
            object.radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
            object.scale = ProfileActivity.this.avatarImage.getScaleX();
            return object;
        }

        public void willHidePhotoViewer() {
            ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    };
    /* access modifiers changed from: private */
    public int qrCodeRow;
    private boolean recreateMenuAfterAnimation;
    /* access modifiers changed from: private */
    public boolean reportSpam;
    /* access modifiers changed from: private */
    public int rowCount;
    private ScamDrawable scamDrawable;
    private int selectedUser;
    /* access modifiers changed from: private */
    public int settingsKeyRow;
    /* access modifiers changed from: private */
    public int settingsSectionRow;
    /* access modifiers changed from: private */
    public int settingsTimerRow;
    /* access modifiers changed from: private */
    public int sharedHeaderRow;
    private MediaActivity.SharedMediaData[] sharedMediaData;
    /* access modifiers changed from: private */
    public int sharedSectionRow;
    /* access modifiers changed from: private */
    public ArrayList<Integer> sortedUsers;
    /* access modifiers changed from: private */
    public int startSecretChatRow;
    /* access modifiers changed from: private */
    public int subscribersRow;
    private TopView topView;
    /* access modifiers changed from: private */
    public int unblockRow;
    /* access modifiers changed from: private */
    public UndoView undoView;
    /* access modifiers changed from: private */
    public boolean userBlocked;
    /* access modifiers changed from: private */
    public TLRPC.UserFull userInfo;
    /* access modifiers changed from: private */
    public int userInfoRow;
    /* access modifiers changed from: private */
    public int user_id;
    /* access modifiers changed from: private */
    public int usernameRow;
    /* access modifiers changed from: private */
    public boolean usersEndReached;
    /* access modifiers changed from: private */
    public int voiceRow;
    private ImageView writeButton;
    /* access modifiers changed from: private */
    public AnimatorSet writeButtonAnimation;

    private class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(91.0f));
        }

        public void setBackgroundColor(int color) {
            if (color != this.currentColor) {
                this.paint.setColor(color);
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            canvas2.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (ProfileActivity.this.extraHeight + (getMeasuredHeight() - AndroidUtilities.dp(91.0f))), this.paint);
        }
    }

    public ProfileActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        this.user_id = this.arguments.getInt("user_id", 0);
        this.chat_id = this.arguments.getInt("chat_id", 0);
        this.banFromGroup = this.arguments.getInt("ban_chat_id", 0);
        this.reportSpam = this.arguments.getBoolean("reportSpam", false);
        if (this.user_id != 0) {
            long j = this.arguments.getLong("dialog_id", 0);
            this.dialog_id = j;
            if (j != 0) {
                this.currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            }
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user == null) {
                return false;
            }
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
            this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0;
            if (user.bot) {
                this.isBot = true;
                MediaDataController.getInstance(this.currentAccount).loadBotInfo(user.id, true, this.classGuid);
            }
            this.userInfo = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
            MessagesController.getInstance(this.currentAccount).loadFullUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
            this.participantsMap = null;
        } else if (this.chat_id == 0) {
            return false;
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            this.currentChat = chat;
            if (chat == null) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(countDownLatch) {
                    private final /* synthetic */ CountDownLatch f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ProfileActivity.this.lambda$onFragmentCreate$0$ProfileActivity(this.f$1);
                    }
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                if (this.currentChat == null) {
                    return false;
                }
                MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
            }
            if (this.currentChat.megagroup) {
                getChannelParticipants(true);
            } else {
                this.participantsMap = null;
            }
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            this.sortedUsers = new ArrayList<>();
            updateOnlineCount();
            if (this.chatInfo == null) {
                this.chatInfo = getMessagesController().getChatFull(this.chat_id);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
            } else if (this.chatInfo == null) {
                this.chatInfo = getMessagesStorage().loadChatInfo(this.chat_id, (CountDownLatch) null, false, false);
            }
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
                loadMediaCounts();
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountsDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
                updateRowsIds();
                return true;
            }
        }
    }

    public /* synthetic */ void lambda$onFragmentCreate$0$ProfileActivity(CountDownLatch countDownLatch) {
        this.currentChat = MessagesStorage.getInstance(this.currentAccount).getChat(this.chat_id);
        countDownLatch.countDown();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        if (this.user_id != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
            MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(this.user_id);
        } else if (this.chat_id != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatOnlineCountDidLoad);
        }
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent event) {
                return super.onTouchEvent(event);
            }
        };
        boolean z = false;
        actionBar.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        actionBar.setBackButtonImage(R.mipmap.ic_back);
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        if (Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet()) {
            z = true;
        }
        actionBar.setOccupyStatusBar(z);
        return actionBar;
    }

    public View createView(Context context) {
        Context context2 = context;
        Theme.createProfileResources(context);
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                int did;
                long did2;
                int i = id;
                if (ProfileActivity.this.getParentActivity() != null) {
                    if (i == -1) {
                        ProfileActivity.this.finishFragment();
                    } else if (i == 2) {
                        TLRPC.User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user != null) {
                            if (!ProfileActivity.this.isBot || MessagesController.isSupportUser(user)) {
                                if (ProfileActivity.this.userBlocked) {
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                                    AlertsCreator.showSimpleToast(ProfileActivity.this, LocaleController.getString("UserUnblocked", R.string.UserUnblocked));
                                } else if (ProfileActivity.this.reportSpam) {
                                    ProfileActivity profileActivity = ProfileActivity.this;
                                    AlertsCreator.showBlockReportSpamAlert(profileActivity, (long) profileActivity.user_id, user, (TLRPC.Chat) null, ProfileActivity.this.currentEncryptedChat, false, (TLRPC.ChatFull) null, new MessagesStorage.IntCallback() {
                                        public final void run(int i) {
                                            ProfileActivity.AnonymousClass3.this.lambda$onItemClick$0$ProfileActivity$3(i);
                                        }
                                    });
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) ProfileActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("BlockUser", R.string.BlockUser));
                                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", R.string.AreYouSureBlockContact2, ContactsController.formatName(user.first_name, user.last_name))));
                                    builder.setPositiveButton(LocaleController.getString("BlockContact", R.string.BlockContact), new DialogInterface.OnClickListener() {
                                        public final void onClick(DialogInterface dialogInterface, int i) {
                                            ProfileActivity.AnonymousClass3.this.lambda$onItemClick$1$ProfileActivity$3(dialogInterface, i);
                                        }
                                    });
                                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                                    AlertDialog dialog = builder.create();
                                    ProfileActivity.this.showDialog(dialog);
                                    TextView button = (TextView) dialog.getButton(-1);
                                    if (button != null) {
                                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                                    }
                                }
                            } else if (!ProfileActivity.this.userBlocked) {
                                MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                            } else {
                                MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                                SendMessagesHelper.getInstance(ProfileActivity.this.currentAccount).sendMessage("/start", (long) ProfileActivity.this.user_id, (MessageObject) null, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                                ProfileActivity.this.finishFragment();
                            }
                        }
                    } else if (i == 1) {
                        TLRPC.User user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        Bundle args = new Bundle();
                        args.putInt("user_id", user2.id);
                        args.putBoolean("addContact", true);
                        ProfileActivity.this.presentFragment(new ContactAddActivity(args));
                    } else if (i == 3) {
                        Bundle args2 = new Bundle();
                        args2.putBoolean("onlySelect", true);
                        args2.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendContactTo));
                        args2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                        DialogsActivity fragment = new DialogsActivity(args2);
                        fragment.setDelegate(ProfileActivity.this);
                        ProfileActivity.this.presentFragment(fragment);
                    } else if (i == 4) {
                        Bundle args3 = new Bundle();
                        args3.putInt("user_id", ProfileActivity.this.user_id);
                        ProfileActivity.this.presentFragment(new ContactAddActivity(args3));
                    } else if (i == 5) {
                        TLRPC.User user3 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user3 != null && ProfileActivity.this.getParentActivity() != null) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) ProfileActivity.this.getParentActivity());
                            builder2.setTitle(LocaleController.getString("DeleteContact", R.string.DeleteContact));
                            builder2.setMessage(LocaleController.getString("AreYouSureDeleteContact", R.string.AreYouSureDeleteContact));
                            builder2.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener(user3) {
                                private final /* synthetic */ TLRPC.User f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ProfileActivity.AnonymousClass3.this.lambda$onItemClick$2$ProfileActivity$3(this.f$1, dialogInterface, i);
                                }
                            });
                            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                            AlertDialog dialog2 = builder2.create();
                            ProfileActivity.this.showDialog(dialog2);
                            TextView button2 = (TextView) dialog2.getButton(-1);
                            if (button2 != null) {
                                button2.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                            }
                        }
                    } else if (i == 7) {
                        ProfileActivity.this.leaveChatPressed();
                    } else if (i == 12) {
                        Bundle args4 = new Bundle();
                        args4.putInt("chat_id", ProfileActivity.this.chat_id);
                        ChatEditActivity fragment2 = new ChatEditActivity(args4);
                        fragment2.setInfo(ProfileActivity.this.chatInfo);
                        ProfileActivity.this.presentFragment(fragment2);
                    } else if (i == 9) {
                        TLRPC.User user4 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user4 != null) {
                            Bundle args5 = new Bundle();
                            args5.putBoolean("onlySelect", true);
                            args5.putInt("dialogsType", 2);
                            args5.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", R.string.AddToTheGroupTitle, UserObject.getName(user4), "%1$s"));
                            DialogsActivity fragment3 = new DialogsActivity(args5);
                            fragment3.setDelegate(new DialogsActivity.DialogsActivityDelegate(user4) {
                                private final /* synthetic */ TLRPC.User f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                                    ProfileActivity.AnonymousClass3.this.lambda$onItemClick$3$ProfileActivity$3(this.f$1, dialogsActivity, arrayList, charSequence, z);
                                }
                            });
                            ProfileActivity.this.presentFragment(fragment3);
                        }
                    } else if (i == 10) {
                        try {
                            TLRPC.User user5 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                            if (user5 != null) {
                                Intent intent = new Intent("android.intent.action.SEND");
                                intent.setType("text/plain");
                                if (ProfileActivity.this.botInfo != null) {
                                    if (ProfileActivity.this.userInfo != null && !TextUtils.isEmpty(ProfileActivity.this.userInfo.about)) {
                                        intent.putExtra("android.intent.extra.TEXT", String.format("%s https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{ProfileActivity.this.userInfo.about, user5.username}));
                                        ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
                                    }
                                }
                                intent.putExtra("android.intent.extra.TEXT", String.format("https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{user5.username}));
                                ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else if (i == 14) {
                        try {
                            if (ProfileActivity.this.currentEncryptedChat != null) {
                                did2 = ((long) ProfileActivity.this.currentEncryptedChat.id) << 32;
                            } else if (ProfileActivity.this.user_id != 0) {
                                did2 = (long) ProfileActivity.this.user_id;
                            } else if (ProfileActivity.this.chat_id != 0) {
                                did2 = (long) (-ProfileActivity.this.chat_id);
                            } else {
                                return;
                            }
                            MediaDataController.getInstance(ProfileActivity.this.currentAccount).installShortcut(did2);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    } else if (i == 15) {
                        TLRPC.User user6 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user6 != null) {
                            VoIPHelper.startCall(user6, ProfileActivity.this.getParentActivity(), ProfileActivity.this.userInfo);
                        }
                    } else if (i == 17) {
                        Bundle args6 = new Bundle();
                        args6.putInt("chat_id", ProfileActivity.this.chat_id);
                        args6.putInt("type", 2);
                        args6.putBoolean("open_search", true);
                        ChatUsersActivity fragment4 = new ChatUsersActivity(args6);
                        fragment4.setInfo(ProfileActivity.this.chatInfo);
                        ProfileActivity.this.presentFragment(fragment4);
                    } else if (i == 18) {
                        ProfileActivity.this.openAddMember();
                    } else if (i == 19) {
                        if (ProfileActivity.this.user_id != 0) {
                            did = ProfileActivity.this.user_id;
                        } else {
                            did = -ProfileActivity.this.chat_id;
                        }
                        AlertDialog[] progressDialog = {new AlertDialog(ProfileActivity.this.getParentActivity(), 3)};
                        TLRPC.TL_messages_getStatsURL req = new TLRPC.TL_messages_getStatsURL();
                        req.peer = MessagesController.getInstance(ProfileActivity.this.currentAccount).getInputPeer(did);
                        req.dark = Theme.getCurrentTheme().isDark();
                        req.params = "";
                        int requestId = ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog) {
                            private final /* synthetic */ AlertDialog[] f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                ProfileActivity.AnonymousClass3.this.lambda$onItemClick$5$ProfileActivity$3(this.f$1, tLObject, tL_error);
                            }
                        });
                        if (progressDialog[0] != null) {
                            progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener(requestId) {
                                private final /* synthetic */ int f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onCancel(DialogInterface dialogInterface) {
                                    ProfileActivity.AnonymousClass3.this.lambda$onItemClick$6$ProfileActivity$3(this.f$1, dialogInterface);
                                }
                            });
                            ProfileActivity.this.showDialog(progressDialog[0]);
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ProfileActivity$3(int param) {
                if (param == 1) {
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    boolean unused = ProfileActivity.this.playProfileAnimation = false;
                    ProfileActivity.this.finishFragment();
                    return;
                }
                ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf((long) ProfileActivity.this.user_id));
            }

            public /* synthetic */ void lambda$onItemClick$1$ProfileActivity$3(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                AlertsCreator.showSimpleToast(ProfileActivity.this, LocaleController.getString("UserBlocked", R.string.UserBlocked));
            }

            public /* synthetic */ void lambda$onItemClick$2$ProfileActivity$3(TLRPC.User user, DialogInterface dialogInterface, int i) {
                ArrayList<TLRPC.User> arrayList = new ArrayList<>();
                arrayList.add(user);
                ContactsController.getInstance(ProfileActivity.this.currentAccount).deleteContact(arrayList);
            }

            public /* synthetic */ void lambda$onItemClick$3$ProfileActivity$3(TLRPC.User user, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
                long did = ((Long) dids.get(0)).longValue();
                Bundle args1 = new Bundle();
                args1.putBoolean("scrollToTopOnResume", true);
                args1.putInt("chat_id", -((int) did));
                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(args1, fragment1)) {
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(-((int) did), user, (TLRPC.ChatFull) null, 0, (String) null, ProfileActivity.this, (Runnable) null);
                    ProfileActivity.this.presentFragment(new ChatActivity(args1), true);
                    ProfileActivity.this.removeSelfFromStack();
                }
            }

            public /* synthetic */ void lambda$onItemClick$5$ProfileActivity$3(AlertDialog[] progressDialog, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable(progressDialog, response) {
                    private final /* synthetic */ AlertDialog[] f$1;
                    private final /* synthetic */ TLObject f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        ProfileActivity.AnonymousClass3.this.lambda$null$4$ProfileActivity$3(this.f$1, this.f$2);
                    }
                });
            }

            public /* synthetic */ void lambda$null$4$ProfileActivity$3(AlertDialog[] progressDialog, TLObject response) {
                try {
                    progressDialog[0].dismiss();
                } catch (Throwable th) {
                }
                progressDialog[0] = null;
                if (response != null) {
                    ProfileActivity.this.presentFragment(new WebviewActivity(((TLRPC.TL_statsURL) response).url, (long) (-ProfileActivity.this.chat_id)));
                }
            }

            public /* synthetic */ void lambda$onItemClick$6$ProfileActivity$3(int requestId, DialogInterface dialog) {
                ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).cancelRequest(requestId, true);
            }
        });
        createActionBarMenu();
        this.listAdapter = new ListAdapter(context2);
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setProfile(true);
        this.fragmentView = new FrameLayout(context2) {
            public boolean hasOverlappingRendering() {
                return false;
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                ProfileActivity.this.checkListViewScroll();
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        AnonymousClass5 r5 = new RecyclerListView(context2) {
            private Paint paint = new Paint();

            public boolean hasOverlappingRendering() {
                return false;
            }

            public void onDraw(Canvas c) {
                RecyclerView.ViewHolder holder;
                int bottom;
                if (ProfileActivity.this.lastSectionRow != -1) {
                    holder = findViewHolderForAdapterPosition(ProfileActivity.this.lastSectionRow);
                } else if (ProfileActivity.this.sharedSectionRow != -1 && (ProfileActivity.this.membersSectionRow == -1 || ProfileActivity.this.membersSectionRow < ProfileActivity.this.sharedSectionRow)) {
                    holder = findViewHolderForAdapterPosition(ProfileActivity.this.sharedSectionRow);
                } else if (ProfileActivity.this.membersSectionRow != -1 && (ProfileActivity.this.sharedSectionRow == -1 || ProfileActivity.this.membersSectionRow > ProfileActivity.this.sharedSectionRow)) {
                    holder = findViewHolderForAdapterPosition(ProfileActivity.this.membersSectionRow);
                } else if (ProfileActivity.this.settingsSectionRow != -1) {
                    holder = findViewHolderForAdapterPosition(ProfileActivity.this.settingsSectionRow);
                } else if (ProfileActivity.this.infoSectionRow != -1) {
                    holder = findViewHolderForAdapterPosition(ProfileActivity.this.infoSectionRow);
                } else {
                    holder = null;
                }
                int height = getMeasuredHeight();
                if (holder != null) {
                    bottom = holder.itemView.getBottom();
                    if (holder.itemView.getBottom() >= height) {
                        bottom = height;
                    }
                } else {
                    bottom = height;
                }
                this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                c.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) bottom, this.paint);
                if (bottom != height) {
                    this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    c.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) height, this.paint);
                }
            }
        };
        this.listView = r5;
        r5.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setClipToPadding(false);
        AnonymousClass6 r52 = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r52;
        r52.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setGlowColor(AvatarDrawable.getProfileBackColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1.0f, -1.0f, 48, 10.0f, 0.0f, 10.0f, 0.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                ProfileActivity.this.lambda$createView$6$ProfileActivity(view, i, f, f2);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ProfileActivity.this.lambda$createView$9$ProfileActivity(view, i);
            }
        });
        if (this.banFromGroup != 0) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.banFromGroup));
            if (this.currentChannelParticipant == null) {
                TLRPC.TL_channels_getParticipant req = new TLRPC.TL_channels_getParticipant();
                req.channel = MessagesController.getInputChannel(chat);
                req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user_id);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ProfileActivity.this.lambda$createView$11$ProfileActivity(tLObject, tL_error);
                    }
                });
            }
            FrameLayout frameLayout1 = new FrameLayout(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                    Theme.chat_composeShadowDrawable.draw(canvas);
                    canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                }
            };
            frameLayout1.setWillNotDraw(false);
            frameLayout.addView(frameLayout1, LayoutHelper.createFrame(-1, 51, 83));
            frameLayout1.setOnClickListener(new View.OnClickListener(chat) {
                private final /* synthetic */ TLRPC.Chat f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ProfileActivity.this.lambda$createView$12$ProfileActivity(this.f$1, view);
                }
            });
            TextView textView = new TextView(context2);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
            textView.setTextSize(1, 15.0f);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("BanFromTheGroup", R.string.BanFromTheGroup));
            frameLayout1.addView(textView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, AndroidUtilities.dp(48.0f));
            this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        } else {
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        }
        TopView topView2 = new TopView(context2);
        this.topView = topView2;
        topView2.setBackgroundColor(AvatarDrawable.getProfileBackColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id));
        frameLayout.addView(this.topView);
        frameLayout.addView(this.actionBar);
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42.0f, 42.0f, 51, 59.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ProfileActivity.this.lambda$createView$13$ProfileActivity(view);
            }
        });
        this.avatarImage.setContentDescription(LocaleController.getString("AccDescrProfilePicture", R.string.AccDescrProfilePicture));
        int a = 0;
        while (a < 2) {
            if (this.playProfileAnimation || a != 0) {
                this.nameTextView[a] = new SimpleTextView(context2);
                if (a == 1) {
                    this.nameTextView[a].setTextColor(Theme.getColor(Theme.key_profile_title));
                } else {
                    this.nameTextView[a].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                }
                this.nameTextView[a].setTextSize(14);
                this.nameTextView[a].setGravity(3);
                this.nameTextView[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[a].setPivotX(0.0f);
                this.nameTextView[a].setPivotY(0.0f);
                float f = 1.0f;
                this.nameTextView[a].setAlpha(a == 0 ? 0.0f : 1.0f);
                if (a == 1) {
                    this.nameTextView[a].setScrollNonFitText(true);
                    this.nameTextView[a].setBackgroundColor(AvatarDrawable.getProfileBackColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id));
                }
                frameLayout.addView(this.nameTextView[a], LayoutHelper.createFrame(-2.0f, -2.0f, 51, 102.0f, 0.0f, a == 0 ? 48.0f : 0.0f, 0.0f));
                this.onlineTextView[a] = new SimpleTextView(context2);
                this.onlineTextView[a].setTextColor(AvatarDrawable.getProfileTextColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id));
                this.onlineTextView[a].setTextSize(11);
                this.onlineTextView[a].setGravity(3);
                SimpleTextView simpleTextView = this.onlineTextView[a];
                if (a == 0) {
                    f = 0.0f;
                }
                simpleTextView.setAlpha(f);
                frameLayout.addView(this.onlineTextView[a], LayoutHelper.createFrame(-2.0f, -2.0f, 51, 102.0f, -6.0f, a == 0 ? 48.0f : 8.0f, 0.0f));
            }
            a++;
        }
        if (this.user_id != 0) {
            this.writeButton = new ImageView(context2);
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                drawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(drawable);
            this.writeButton.setImageResource(R.drawable.profile_newmsg);
            this.writeButton.setContentDescription(LocaleController.getString("AccDescrOpenChat", R.string.AccDescrOpenChat));
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), PorterDuff.Mode.MULTIPLY));
            this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.writeButton.setStateListAnimator(animator);
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            frameLayout.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            this.writeButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ProfileActivity.this.lambda$createView$14$ProfileActivity(view);
                }
            });
        }
        needLayout();
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ProfileActivity.this.checkListViewScroll();
                if (ProfileActivity.this.participantsMap != null && !ProfileActivity.this.usersEndReached && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.membersEndRow - 8) {
                    ProfileActivity.this.getChannelParticipants(false);
                }
            }
        });
        UndoView undoView2 = new UndoView(context2);
        this.undoView = undoView2;
        frameLayout.addView(undoView2, LayoutHelper.createFrame(-1.0f, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$6$ProfileActivity(View view, int position, float x, float y) {
        int tab;
        int user_id2;
        TLRPC.User user;
        TLRPC.Chat chat;
        long did;
        long did2;
        long flags;
        int i = position;
        if (getParentActivity() != null) {
            if (i == this.photosRow || i == this.filesRow || i == this.linksRow || i == this.audioRow || i == this.voiceRow) {
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
                } else {
                    args.putLong("dialog_id", (long) (-this.chat_id));
                }
                int[] media = new int[5];
                System.arraycopy(this.lastMediaCount, 0, media, 0, media.length);
                MediaActivity mediaActivity2 = new MediaActivity(args, media, this.sharedMediaData, tab);
                this.mediaActivity = mediaActivity2;
                mediaActivity2.setChatInfo(this.chatInfo);
                presentFragment(this.mediaActivity);
            } else if (i == this.groupsInCommonRow) {
                presentFragment(new CommonGroupsActivity(this.user_id));
            } else if (i == this.settingsKeyRow) {
                Bundle args2 = new Bundle();
                args2.putInt("chat_id", (int) (this.dialog_id >> 32));
                presentFragment(new IdenticonActivity(args2));
            } else if (i == this.settingsTimerRow) {
                showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat).create());
            } else if (i == this.notificationsRow) {
                if (this.dialog_id != 0) {
                    did2 = this.dialog_id;
                } else {
                    int i3 = this.user_id;
                    if (i3 != 0) {
                        did2 = (long) i3;
                    } else {
                        did2 = (long) (-this.chat_id);
                    }
                }
                NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                boolean checked = !checkCell.isChecked();
                boolean defaultEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(did2);
                if (checked) {
                    SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (defaultEnabled) {
                        editor.remove("notify2_" + did2);
                    } else {
                        editor.putInt("notify2_" + did2, 0);
                    }
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(did2, 0);
                    editor.commit();
                    TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(did2);
                    if (dialog != null) {
                        dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(did2);
                } else {
                    SharedPreferences.Editor editor2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (!defaultEnabled) {
                        editor2.remove("notify2_" + did2);
                        flags = 0;
                    } else {
                        editor2.putInt("notify2_" + did2, 2);
                        flags = 1;
                    }
                    NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(did2);
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(did2, flags);
                    editor2.commit();
                    TLRPC.Dialog dialog2 = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(did2);
                    if (dialog2 != null) {
                        dialog2.notify_settings = new TLRPC.TL_peerNotifySettings();
                        if (defaultEnabled) {
                            dialog2.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(did2);
                }
                checkCell.setChecked(checked);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                }
            } else if (i == this.forbidAddContact) {
                if (this.dialog_id != 0) {
                    did = this.dialog_id;
                } else {
                    int i4 = this.user_id;
                    if (i4 != 0) {
                        did = (long) i4;
                    } else {
                        did = (long) (-this.chat_id);
                    }
                }
                TLRPCChats.CL_channel_setParticipantBanMode req = new TLRPCChats.CL_channel_setParticipantBanMode();
                req.ban_add_contact = !((NotificationsCheckCell) view).isChecked();
                req.peer = getMessagesController().getInputPeer((int) did);
                getConnectionsManager().sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ProfileActivity.this.lambda$null$2$ProfileActivity(tLObject, tL_error);
                    }
                });
            } else if (i == this.startSecretChatRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("AreYouSureSecretChatTitle", R.string.AreYouSureSecretChatTitle));
                builder.setMessage(LocaleController.getString("AreYouSureSecretChat", R.string.AreYouSureSecretChat));
                builder.setPositiveButton(LocaleController.getString("Start", R.string.Start), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ProfileActivity.this.lambda$null$3$ProfileActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            } else if (i == this.unblockRow) {
                MessagesController.getInstance(this.currentAccount).unblockUser(this.user_id);
                AlertsCreator.showSimpleToast(this, LocaleController.getString("UserUnblocked", R.string.UserUnblocked));
            } else if (i < this.membersStartRow || i >= this.membersEndRow) {
                if (i == this.addMemberRow) {
                    openAddMember();
                } else if (i == this.usernameRow) {
                    if (this.currentChat != null) {
                        Bundle args3 = new Bundle();
                        args3.putInt("dialogsType", 3);
                        args3.putBoolean("onlySelect", true);
                        args3.putString("selectAlertString", "要分享群号给%s");
                        args3.putString("selectAlertStringGroup", "要分享群号给 %s");
                        DialogsActivity fragment = new DialogsActivity(args3);
                        fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate(fragment) {
                            private final /* synthetic */ DialogsActivity f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                                ProfileActivity.this.lambda$null$5$ProfileActivity(this.f$1, dialogsActivity, arrayList, charSequence, z);
                            }
                        });
                        presentFragment(fragment);
                    }
                } else if (i == this.qrCodeRow) {
                    if (this.userInfo != null) {
                        presentFragment(new QrCodeActivity(getUserConfig().getClientUserId()));
                    } else if (this.chatInfo != null) {
                        GroupShareActivity shareActivity = new GroupShareActivity();
                        shareActivity.setChatInfo(this.chatInfo);
                        shareActivity.setChat(this.currentChat);
                        presentFragment(shareActivity);
                    }
                } else if (i == this.locationRow) {
                    if (this.chatInfo.location instanceof TLRPC.TL_channelLocation) {
                        NewLocationActivity fragment2 = new NewLocationActivity(3);
                        fragment2.setChatLocation(this.chat_id, (TLRPC.TL_channelLocation) this.chatInfo.location);
                        presentFragment(fragment2);
                    }
                } else if (i == this.leaveChannelRow) {
                    leaveChatPressed();
                } else if (i == this.joinRow) {
                    MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), (TLRPC.ChatFull) null, 0, (String) null, this, (Runnable) null);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
                } else if (i == this.subscribersRow) {
                    Bundle args4 = new Bundle();
                    args4.putInt("chat_id", this.chat_id);
                    args4.putInt("type", 2);
                    ChatUsersActivity fragment3 = new ChatUsersActivity(args4);
                    fragment3.setInfo(this.chatInfo);
                    presentFragment(fragment3);
                } else if (i == this.administratorsRow) {
                    Bundle args5 = new Bundle();
                    args5.putInt("chat_id", this.chat_id);
                    args5.putInt("type", 1);
                    ChatUsersActivity fragment4 = new ChatUsersActivity(args5);
                    fragment4.setInfo(this.chatInfo);
                    presentFragment(fragment4);
                } else if (i == this.blockedUsersRow) {
                    Bundle args6 = new Bundle();
                    args6.putInt("chat_id", this.chat_id);
                    args6.putInt("type", 0);
                    ChatUsersActivity fragment5 = new ChatUsersActivity(args6);
                    fragment5.setInfo(this.chatInfo);
                    presentFragment(fragment5);
                } else {
                    processOnClickOrPress(i);
                }
            } else if (this.currentChat instanceof TLRPC.TL_channelForbidden) {
                getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                AlertDialog dialog3 = new AlertDialog(getParentActivity(), 0);
                dialog3.setTitle(LocaleController.getString("AppName", R.string.AppName));
                dialog3.setMessage(LocaleController.getString("DeleteThisGroup", R.string.DeleteThisGroup));
                dialog3.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ProfileActivity.this.lambda$null$4$ProfileActivity(dialogInterface, i);
                    }
                });
                dialog3.setCancelable(false);
                dialog3.setCanceledOnTouchOutside(false);
                showDialog(dialog3);
            } else {
                if (!this.sortedUsers.isEmpty()) {
                    user_id2 = this.chatInfo.participants.participants.get(this.sortedUsers.get(i - this.membersStartRow).intValue()).user_id;
                } else {
                    user_id2 = this.chatInfo.participants.participants.get(i - this.membersStartRow).user_id;
                }
                if (user_id2 != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    TLRPC.Chat chat2 = this.currentChat;
                    if ((chat2 != null && chat2.megagroup && !this.currentChat.creator && this.currentChat.admin_rights == null) || (user = getMessagesController().getUser(Integer.valueOf(user_id2))) == null) {
                        return;
                    }
                    if (user.self || (chat = this.currentChat) == null || !chat.megagroup || (this.currentChat.flags & ConnectionsManager.FileTypeVideo) == 0 || user.mutual_contact || ChatObject.hasAdminRights(this.currentChat)) {
                        Bundle args7 = new Bundle();
                        args7.putInt("user_id", user_id2);
                        args7.putBoolean("forbid_add_contact", this.currentChat.megagroup && (33554432 & this.currentChat.flags) != 0);
                        args7.putBoolean("has_admin_right", ChatObject.hasAdminRights(this.currentChat));
                        args7.putInt("from_type", 2);
                        if (user.bot) {
                            MessagesController.openChatOrProfileWith(user, (TLRPC.Chat) null, this, 0, true);
                        } else {
                            presentFragment(new NewProfileActivity(args7));
                        }
                    } else {
                        ToastUtils.show((int) R.string.ForbidViewUserInfoTips);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$2$ProfileActivity(TLObject response, TLRPC.TL_error error) {
        if (error == null && (response instanceof TLRPC.Updates)) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ProfileActivity.this.lambda$null$1$ProfileActivity();
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$null$1$ProfileActivity() {
        this.currentChat = MessagesStorage.getInstance(this.currentAccount).getChat(this.currentChat.id);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.forbidAddContact);
        if (holder != null) {
            this.listAdapter.onBindViewHolder(holder, this.forbidAddContact);
        }
    }

    public /* synthetic */ void lambda$null$3$ProfileActivity(DialogInterface dialogInterface, int i) {
        this.creatingChat = true;
        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)));
    }

    public /* synthetic */ void lambda$null$4$ProfileActivity(DialogInterface dialog1, int which) {
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.chatInfo, true, false);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$5$ProfileActivity(DialogsActivity fragment, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        SendMessagesHelper instance = SendMessagesHelper.getInstance(this.currentAccount);
        instance.sendMessage("@" + this.currentChat.username, did, (MessageObject) null, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        fragment.finishFragment();
    }

    public /* synthetic */ boolean lambda$createView$9$ProfileActivity(View view, int position) {
        TLRPC.ChatParticipant participant;
        boolean canRestrict;
        boolean allowKick;
        boolean canEditAdmin;
        boolean editingAdmin;
        TLRPC.ChannelParticipant channelParticipant;
        boolean hasRemove;
        String str;
        int i;
        int i2 = position;
        if (i2 < this.membersStartRow || i2 >= this.membersEndRow) {
            return processOnClickOrPress(i2);
        }
        if (getParentActivity() == null) {
            return false;
        }
        if (!this.sortedUsers.isEmpty()) {
            participant = this.chatInfo.participants.participants.get(this.sortedUsers.get(i2 - this.membersStartRow).intValue());
        } else {
            participant = this.chatInfo.participants.participants.get(i2 - this.membersStartRow);
        }
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(participant.user_id));
        if (user == null || participant.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return false;
        }
        this.selectedUser = participant.user_id;
        if (ChatObject.isChannel(this.currentChat)) {
            TLRPC.ChannelParticipant channelParticipant2 = ((TLRPC.TL_chatChannelParticipant) participant).channelParticipant;
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(participant.user_id));
            boolean canEditAdmin2 = ChatObject.canAddAdmins(this.currentChat);
            if (canEditAdmin2 && ((channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator) || ((channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !channelParticipant2.can_edit))) {
                canEditAdmin2 = false;
            }
            boolean allowKick2 = ChatObject.canBlockUsers(this.currentChat) && ((!(channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator)) || channelParticipant2.can_edit);
            channelParticipant = channelParticipant2;
            editingAdmin = channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin;
            canEditAdmin = canEditAdmin2;
            allowKick = allowKick2;
            canRestrict = allowKick2;
        } else {
            boolean allowKick3 = this.currentChat.creator || ((participant instanceof TLRPC.TL_chatParticipant) && (ChatObject.canBlockUsers(this.currentChat) || participant.inviter_id == UserConfig.getInstance(this.currentAccount).getClientUserId()));
            channelParticipant = null;
            editingAdmin = participant instanceof TLRPC.TL_chatParticipantAdmin;
            canEditAdmin = this.currentChat.creator;
            allowKick = allowKick3;
            canRestrict = this.currentChat.creator;
        }
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Integer> icons = new ArrayList<>();
        ArrayList<Integer> actions = new ArrayList<>();
        if (canEditAdmin) {
            if (editingAdmin) {
                i = R.string.EditAdminRights;
                str = "EditAdminRights";
            } else {
                i = R.string.SetAsAdmin;
                str = "SetAsAdmin";
            }
            items.add(LocaleController.getString(str, i));
            icons.add(Integer.valueOf(R.drawable.actions_addadmin));
            actions.add(0);
        }
        if (canRestrict) {
            items.add(LocaleController.getString("ChangePermissions", R.string.ChangePermissions));
            icons.add(Integer.valueOf(R.drawable.actions_permissions));
            actions.add(1);
        }
        if (allowKick) {
            items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
            icons.add(Integer.valueOf(R.drawable.actions_remove_user));
            actions.add(2);
            hasRemove = true;
        } else {
            hasRemove = false;
        }
        if (items.isEmpty()) {
            return false;
        }
        boolean z = editingAdmin;
        boolean z2 = canEditAdmin;
        boolean z3 = allowKick;
        boolean z4 = canRestrict;
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        ArrayList<Integer> arrayList = actions;
        ArrayList<Integer> arrayList2 = icons;
        ArrayList<String> items2 = items;
        builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(icons), new DialogInterface.OnClickListener(actions, channelParticipant, participant, user) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ TLRPC.ChannelParticipant f$2;
            private final /* synthetic */ TLRPC.ChatParticipant f$3;
            private final /* synthetic */ TLRPC.User f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.lambda$null$8$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
            }
        });
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        if (!hasRemove) {
            return true;
        }
        alertDialog.setItemColor(items2.size() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0035, code lost:
        if ((r19 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_chatParticipantAdmin) != false) goto L_0x003a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$8$ProfileActivity(java.util.ArrayList r17, im.bclpbkiauv.tgnet.TLRPC.ChannelParticipant r18, im.bclpbkiauv.tgnet.TLRPC.ChatParticipant r19, im.bclpbkiauv.tgnet.TLRPC.User r20, android.content.DialogInterface r21, int r22) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            r9 = r18
            r10 = r20
            r11 = r22
            java.lang.Object r0 = r8.get(r11)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = 2
            if (r0 != r1) goto L_0x0020
            int r0 = r7.selectedUser
            r7.kickUser(r0)
            r13 = r19
            goto L_0x00bc
        L_0x0020:
            java.lang.Object r0 = r8.get(r11)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r12 = r0.intValue()
            r0 = 1
            if (r12 != r0) goto L_0x0099
            boolean r1 = r9 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelParticipantAdmin
            if (r1 != 0) goto L_0x0038
            r13 = r19
            boolean r1 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_chatParticipantAdmin
            if (r1 == 0) goto L_0x009b
            goto L_0x003a
        L_0x0038:
            r13 = r19
        L_0x003a:
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r1 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder
            androidx.fragment.app.FragmentActivity r2 = r16.getParentActivity()
            r1.<init>((android.content.Context) r2)
            r6 = r1
            r1 = 2131689824(0x7f0f0160, float:1.9008674E38)
            java.lang.String r2 = "AppName"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r6.setTitle(r1)
            r1 = 2131689722(0x7f0f00fa, float:1.9008467E38)
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r2 = 0
            java.lang.String r3 = r10.first_name
            java.lang.String r4 = r10.last_name
            java.lang.String r3 = im.bclpbkiauv.messenger.ContactsController.formatName(r3, r4)
            r0[r2] = r3
            java.lang.String r2 = "AdminWillBeRemoved"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r1, r0)
            r6.setMessage(r0)
            r0 = 2131692462(0x7f0f0bae, float:1.9014025E38)
            java.lang.String r1 = "OK"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            im.bclpbkiauv.ui.-$$Lambda$ProfileActivity$PnBvKK8QqIvW7wmslFIM0UtL8r4 r15 = new im.bclpbkiauv.ui.-$$Lambda$ProfileActivity$PnBvKK8QqIvW7wmslFIM0UtL8r4
            r0 = r15
            r1 = r16
            r2 = r18
            r3 = r12
            r4 = r20
            r5 = r19
            r0.<init>(r2, r3, r4, r5)
            r6.setPositiveButton(r14, r15)
            r0 = 2131690308(0x7f0f0344, float:1.9009656E38)
            java.lang.String r1 = "Cancel"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r1 = 0
            r6.setNegativeButton(r0, r1)
            im.bclpbkiauv.ui.actionbar.AlertDialog r0 = r6.create()
            r7.showDialog(r0)
            goto L_0x00bc
        L_0x0099:
            r13 = r19
        L_0x009b:
            if (r9 == 0) goto L_0x00ae
            int r2 = r10.id
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r4 = r9.admin_rights
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r5 = r9.banned_rights
            java.lang.String r6 = r9.rank
            r0 = r16
            r1 = r12
            r3 = r19
            r0.openRightsEdit(r1, r2, r3, r4, r5, r6)
            goto L_0x00bc
        L_0x00ae:
            int r2 = r10.id
            r4 = 0
            r5 = 0
            java.lang.String r6 = ""
            r0 = r16
            r1 = r12
            r3 = r19
            r0.openRightsEdit(r1, r2, r3, r4, r5, r6)
        L_0x00bc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ProfileActivity.lambda$null$8$ProfileActivity(java.util.ArrayList, im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant, im.bclpbkiauv.tgnet.TLRPC$ChatParticipant, im.bclpbkiauv.tgnet.TLRPC$User, android.content.DialogInterface, int):void");
    }

    public /* synthetic */ void lambda$null$7$ProfileActivity(TLRPC.ChannelParticipant channelParticipant, int action, TLRPC.User user, TLRPC.ChatParticipant participant, DialogInterface dialog, int which) {
        TLRPC.ChannelParticipant channelParticipant2 = channelParticipant;
        TLRPC.User user2 = user;
        if (channelParticipant2 != null) {
            openRightsEdit(action, user2.id, participant, channelParticipant2.admin_rights, channelParticipant2.banned_rights, channelParticipant2.rank);
            return;
        }
        openRightsEdit(action, user2.id, participant, (TLRPC.TL_chatAdminRights) null, (TLRPC.TL_chatBannedRights) null, "");
    }

    public /* synthetic */ void lambda$createView$11$ProfileActivity(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ProfileActivity.this.lambda$null$10$ProfileActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$10$ProfileActivity(TLObject response) {
        this.currentChannelParticipant = ((TLRPC.TL_channels_channelParticipant) response).participant;
    }

    public /* synthetic */ void lambda$createView$12$ProfileActivity(TLRPC.Chat chat, View v) {
        int i = this.user_id;
        int i2 = this.banFromGroup;
        TLRPC.TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
        TLRPC.ChannelParticipant channelParticipant = this.currentChannelParticipant;
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(i, i2, (TLRPC.TL_chatAdminRights) null, tL_chatBannedRights, channelParticipant != null ? channelParticipant.banned_rights : null, "", 1, true, false);
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                ProfileActivity.this.removeSelfFromStack();
            }

            public void didChangeOwner(TLRPC.User user) {
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) user);
            }
        });
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$createView$13$ProfileActivity(View v) {
        if (this.user_id != 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user.photo != null && user.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                if (user.photo.dc_id != 0) {
                    user.photo.photo_big.dc_id = user.photo.dc_id;
                }
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
            }
        } else if (this.chat_id != 0) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            if (chat.photo != null && chat.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                if (chat.photo.dc_id != 0) {
                    chat.photo.photo_big.dc_id = chat.photo.dc_id;
                }
                PhotoViewer.getInstance().openPhoto(chat.photo.photo_big, this.provider);
            }
        }
    }

    public /* synthetic */ void lambda$createView$14$ProfileActivity(View v) {
        if (!this.playProfileAnimation || !(this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user != null && !(user instanceof TLRPC.TL_userEmpty)) {
                Bundle args = new Bundle();
                args.putInt("user_id", this.user_id);
                if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    presentFragment(new ChatActivity(args), true);
                    return;
                }
                return;
            }
            return;
        }
        finishFragment();
    }

    private void openRightsEdit(final int action, int user_id2, final TLRPC.ChatParticipant participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank) {
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user_id2, this.chat_id, adminRights, this.currentChat.default_banned_rights, bannedRights, rank, action, true, false);
        int i = action;
        TLRPC.ChatParticipant chatParticipant = participant;
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                TLRPC.ChatParticipant newParticipant;
                int i = action;
                if (i == 0) {
                    TLRPC.ChatParticipant chatParticipant = participant;
                    if (chatParticipant instanceof TLRPC.TL_chatChannelParticipant) {
                        TLRPC.TL_chatChannelParticipant channelParticipant1 = (TLRPC.TL_chatChannelParticipant) chatParticipant;
                        if (rights == 1) {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipantAdmin();
                            channelParticipant1.channelParticipant.flags |= 4;
                        } else {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipant();
                        }
                        channelParticipant1.channelParticipant.inviter_id = UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId();
                        channelParticipant1.channelParticipant.user_id = participant.user_id;
                        channelParticipant1.channelParticipant.date = participant.date;
                        channelParticipant1.channelParticipant.banned_rights = rightsBanned;
                        channelParticipant1.channelParticipant.admin_rights = rightsAdmin;
                        channelParticipant1.channelParticipant.rank = rank;
                    } else if (chatParticipant instanceof TLRPC.ChatParticipant) {
                        if (rights == 1) {
                            newParticipant = new TLRPC.TL_chatParticipantAdmin();
                        } else {
                            newParticipant = new TLRPC.TL_chatParticipant();
                        }
                        newParticipant.user_id = participant.user_id;
                        newParticipant.date = participant.date;
                        newParticipant.inviter_id = participant.inviter_id;
                        int index = ProfileActivity.this.chatInfo.participants.participants.indexOf(participant);
                        if (index >= 0) {
                            ProfileActivity.this.chatInfo.participants.participants.set(index, newParticipant);
                        }
                    }
                } else if (i == 1 && rights == 0 && ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                    boolean changed = false;
                    int a = 0;
                    while (true) {
                        if (a >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                            break;
                        } else if (((TLRPC.TL_chatChannelParticipant) ProfileActivity.this.chatInfo.participants.participants.get(a)).channelParticipant.user_id == participant.user_id) {
                            if (ProfileActivity.this.chatInfo != null) {
                                ProfileActivity.this.chatInfo.participants_count--;
                            }
                            ProfileActivity.this.chatInfo.participants.participants.remove(a);
                            changed = true;
                        } else {
                            a++;
                        }
                    }
                    if (ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                        int a2 = 0;
                        while (true) {
                            if (a2 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                                break;
                            } else if (ProfileActivity.this.chatInfo.participants.participants.get(a2).user_id == participant.user_id) {
                                ProfileActivity.this.chatInfo.participants.participants.remove(a2);
                                changed = true;
                                break;
                            } else {
                                a2++;
                            }
                        }
                    }
                    if (changed) {
                        ProfileActivity.this.updateOnlineCount();
                        ProfileActivity.this.updateRowsIds();
                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            }

            public void didChangeOwner(TLRPC.User user) {
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) user);
            }
        });
        presentFragment(fragment);
    }

    private boolean processOnClickOrPress(int position) {
        String username;
        TLRPC.Chat chat;
        if (position == this.usernameRow) {
            if (this.user_id != 0) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                if (user == null || user.username == null) {
                    return false;
                }
                username = user.username;
            } else if (this.chat_id == 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id))) == null || chat.username == null) {
                return false;
            } else {
                username = chat.username;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener(username) {
                private final /* synthetic */ String f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.lambda$processOnClickOrPress$15(this.f$0, dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return true;
        } else if (position == this.phoneRow) {
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user2 == null || user2.phone == null || user2.phone.length() == 0 || getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            ArrayList<CharSequence> items = new ArrayList<>();
            ArrayList<Integer> actions = new ArrayList<>();
            TLRPC.UserFull userFull = this.userInfo;
            if (userFull != null && userFull.phone_calls_available) {
                items.add(LocaleController.getString("CallViaApp", R.string.CallViaApp));
                actions.add(2);
            }
            items.add(LocaleController.getString("Call", R.string.Call));
            actions.add(0);
            items.add(LocaleController.getString("Copy", R.string.Copy));
            actions.add(1);
            builder2.setItems((CharSequence[]) items.toArray(new CharSequence[0]), new DialogInterface.OnClickListener(actions, user2) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ TLRPC.User f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$processOnClickOrPress$16$ProfileActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            showDialog(builder2.create());
            return true;
        } else if (position != this.channelInfoRow && position != this.userInfoRow && position != this.locationRow) {
            return false;
        } else {
            AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
            builder3.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener(position) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$processOnClickOrPress$17$ProfileActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder3.create());
            return true;
        }
    }

    static /* synthetic */ void lambda$processOnClickOrPress$15(String username, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + username));
                ToastUtils.show((int) R.string.TextCopied);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$16$ProfileActivity(ArrayList actions, TLRPC.User user, DialogInterface dialogInterface, int i) {
        int i2 = ((Integer) actions.get(i)).intValue();
        if (i2 == 0) {
            try {
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + user.phone));
                intent.addFlags(C.ENCODING_PCM_MU_LAW);
                getParentActivity().startActivityForResult(intent, 500);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (i2 == 1) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", Marker.ANY_NON_NULL_MARKER + user.phone));
                ToastUtils.show((int) R.string.PhoneCopied);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } else if (i2 == 2) {
            VoIPHelper.startCall(user, getParentActivity(), this.userInfo);
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$17$ProfileActivity(int position, DialogInterface dialogInterface, int i) {
        String about;
        try {
            String str = null;
            if (position == this.locationRow) {
                if (this.chatInfo != null && (this.chatInfo.location instanceof TLRPC.TL_channelLocation)) {
                    str = ((TLRPC.TL_channelLocation) this.chatInfo.location).address;
                }
                about = str;
            } else if (position == this.channelInfoRow) {
                if (this.chatInfo != null) {
                    str = this.chatInfo.about;
                }
                about = str;
            } else {
                if (this.userInfo != null) {
                    str = this.userInfo.about;
                }
                about = str;
            }
            if (!TextUtils.isEmpty(about)) {
                AndroidUtilities.addToClipboard(about);
                ToastUtils.show((int) R.string.TextCopied);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void leaveChatPressed() {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, (TLRPC.User) null, false, new MessagesStorage.BooleanCallback() {
            public final void run(boolean z) {
                ProfileActivity.this.lambda$leaveChatPressed$18$ProfileActivity(z);
            }
        });
    }

    public /* synthetic */ void lambda$leaveChatPressed$18$ProfileActivity(boolean param) {
        this.playProfileAnimation = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        finishFragment();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf((long) (-this.currentChat.id)), null, this.currentChat, Boolean.valueOf(param));
    }

    /* access modifiers changed from: private */
    public void getChannelParticipants(boolean reload) {
        SparseArray<TLRPC.ChatParticipant> sparseArray;
        if (!this.loadingUsers && (sparseArray = this.participantsMap) != null && this.chatInfo != null) {
            this.loadingUsers = true;
            int i = 0;
            int delay = (sparseArray.size() == 0 || !reload) ? 0 : 300;
            TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
            req.filter = new TLRPC.TL_channelParticipantsRecent();
            if (!reload) {
                i = this.participantsMap.size();
            }
            req.offset = i;
            req.limit = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(req, delay) {
                private final /* synthetic */ TLRPC.TL_channels_getParticipants f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ProfileActivity.this.lambda$getChannelParticipants$20$ProfileActivity(this.f$1, this.f$2, tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getChannelParticipants$20$ProfileActivity(TLRPC.TL_channels_getParticipants req, int delay, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, req) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_channels_getParticipants f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ProfileActivity.this.lambda$null$19$ProfileActivity(this.f$1, this.f$2, this.f$3);
            }
        }, (long) delay);
    }

    public /* synthetic */ void lambda$null$19$ProfileActivity(TLRPC.TL_error error, TLObject response, TLRPC.TL_channels_getParticipants req) {
        if (error == null && (response instanceof TLRPC.TL_channels_channelParticipants)) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            if (res.users.size() < 200) {
                this.usersEndReached = true;
            }
            if (req.offset == 0) {
                this.participantsMap.clear();
                this.chatInfo.participants = new TLRPC.TL_chatParticipants();
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, (ArrayList<TLRPC.Chat>) null, true, true);
                MessagesStorage.getInstance(this.currentAccount).updateChannelUsers(this.chat_id, res.participants);
            }
            for (int a = 0; a < res.participants.size(); a++) {
                TLRPC.TL_chatChannelParticipant participant = new TLRPC.TL_chatChannelParticipant();
                participant.channelParticipant = (TLRPC.ChannelParticipant) res.participants.get(a);
                participant.inviter_id = participant.channelParticipant.inviter_id;
                participant.user_id = participant.channelParticipant.user_id;
                participant.date = participant.channelParticipant.date;
                if (this.participantsMap.indexOfKey(participant.user_id) < 0) {
                    this.chatInfo.participants.participants.add(participant);
                    this.participantsMap.put(participant.user_id, participant);
                }
            }
        }
        updateOnlineCount();
        this.loadingUsers = false;
        updateRowsIds();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void openAddMember() {
        Bundle args = new Bundle();
        args.putBoolean("addToGroup", true);
        args.putInt("chatId", this.currentChat.id);
        GroupCreateActivity fragment = new GroupCreateActivity(args);
        fragment.setInfo(this.chatInfo);
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (!(chatFull == null || chatFull.participants == null)) {
            SparseArray<TLObject> users = new SparseArray<>();
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                users.put(this.chatInfo.participants.participants.get(a).user_id, (Object) null);
            }
            fragment.setIgnoreUsers(users);
        }
        fragment.setDelegate((GroupCreateActivity.ContactsAddActivityDelegate) new GroupCreateActivity.ContactsAddActivityDelegate() {
            public final void didSelectUsers(ArrayList arrayList, int i) {
                ProfileActivity.this.lambda$openAddMember$21$ProfileActivity(arrayList, i);
            }

            public /* synthetic */ void needAddBot(TLRPC.User user) {
                GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, user);
            }
        });
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$openAddMember$21$ProfileActivity(ArrayList users, int fwdCount) {
        int N = users.size();
        for (int a = 0; a < N; a++) {
            MessagesController.getInstance(this.currentAccount).addUserToChat(this.chat_id, (TLRPC.User) users.get(a), this.chatInfo, fwdCount, (String) null, this, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    public void checkListViewScroll() {
        if (this.listView.getChildCount() > 0 && !this.openAnimationInProgress) {
            boolean z = false;
            View child = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
            int top = child.getTop();
            int newOffset = 0;
            if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
                newOffset = top;
            }
            if (this.extraHeight != newOffset) {
                this.extraHeight = newOffset;
                this.topView.invalidate();
                if (this.playProfileAnimation) {
                    if (this.extraHeight != 0) {
                        z = true;
                    }
                    this.allowProfileAnimation = z;
                }
                needLayout();
            }
        }
    }

    /* access modifiers changed from: private */
    public void needLayout() {
        int viewWidth;
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && !this.openAnimationInProgress) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarImage != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset(this.extraHeight);
            ImageView imageView = this.writeButton;
            float f = 0.0f;
            int i = 2;
            float f2 = 1.0f;
            int i2 = 1;
            if (imageView != null) {
                imageView.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    boolean setVisible = diff > 0.2f;
                    if (setVisible != (this.writeButton.getTag() == null)) {
                        if (setVisible) {
                            this.writeButton.setTag((Object) null);
                        } else {
                            this.writeButton.setTag(0);
                        }
                        if (this.writeButtonAnimation != null) {
                            AnimatorSet old = this.writeButtonAnimation;
                            this.writeButtonAnimation = null;
                            old.cancel();
                        }
                        AnimatorSet animatorSet = new AnimatorSet();
                        this.writeButtonAnimation = animatorSet;
                        if (setVisible) {
                            animatorSet.setInterpolator(new DecelerateInterpolator());
                            this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f})});
                        } else {
                            animatorSet.setInterpolator(new AccelerateInterpolator());
                            this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f})});
                        }
                        this.writeButtonAnimation.setDuration(150);
                        this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animation)) {
                                    AnimatorSet unused = ProfileActivity.this.writeButtonAnimation = null;
                                }
                            }
                        });
                        this.writeButtonAnimation.start();
                    }
                }
            }
            float avatarY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (diff + 1.0f))) - (AndroidUtilities.density * 21.0f)) + (AndroidUtilities.density * 27.0f * diff);
            this.avatarImage.setScaleX(((diff * 18.0f) + 32.0f) / 42.0f);
            this.avatarImage.setScaleY(((18.0f * diff) + 32.0f) / 42.0f);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            int iTop = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(32.0f)) / 2) + ((Build.VERSION.SDK_INT < 21 || !this.actionBar.getOccupyStatusBar()) ? 0 : AndroidUtilities.statusBarHeight);
            if (avatarY <= ((float) iTop)) {
                avatarY = (float) iTop;
            }
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            int a = 0;
            while (a < i) {
                SimpleTextView[] simpleTextViewArr = this.nameTextView;
                if (simpleTextViewArr[a] != null) {
                    simpleTextViewArr[a].setTranslationX(AndroidUtilities.density * -21.0f * diff);
                    this.nameTextView[a].setTranslationY(((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(1.3f)) + (((float) AndroidUtilities.dp(7.0f)) * diff));
                    this.onlineTextView[a].setTranslationX(AndroidUtilities.density * -21.0f * diff);
                    this.onlineTextView[a].setTranslationY(((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(24.0f)) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
                    float scale = (0.12f * diff) + f2;
                    this.nameTextView[a].setScaleX(scale);
                    this.nameTextView[a].setScaleY(scale);
                    if (a == i2 && !this.openAnimationInProgress) {
                        if (AndroidUtilities.isTablet()) {
                            viewWidth = AndroidUtilities.dp(490.0f);
                        } else {
                            viewWidth = AndroidUtilities.displaySize.x;
                        }
                        int buttonsWidth = AndroidUtilities.dp((float) (126 + 40 + ((this.callItem == null && this.editItem == null) ? 0 : 48)));
                        int minWidth = viewWidth - buttonsWidth;
                        int width = (int) ((((float) viewWidth) - (((float) buttonsWidth) * Math.max(f, f2 - (diff != f2 ? (0.15f * diff) / (f2 - diff) : 1.0f)))) - this.nameTextView[a].getTranslationX());
                        float width2 = (this.nameTextView[a].getPaint().measureText(this.nameTextView[a].getText().toString()) * scale) + ((float) this.nameTextView[a].getSideDrawablesSize());
                        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[a].getLayoutParams();
                        if (((float) width) < width2) {
                            layoutParams2.width = Math.max(minWidth, (int) Math.ceil((double) (((float) (width - AndroidUtilities.dp(24.0f))) / (((1.12f - scale) * 7.0f) + scale))));
                            int i3 = minWidth;
                            float f3 = width2;
                        } else {
                            int i4 = minWidth;
                            layoutParams2.width = (int) Math.ceil((double) width2);
                        }
                        layoutParams2.width = (int) Math.min(((((float) viewWidth) - this.nameTextView[a].getX()) / scale) - ((float) AndroidUtilities.dp(8.0f)), (float) layoutParams2.width);
                        this.nameTextView[a].setLayoutParams(layoutParams2);
                        float width22 = this.onlineTextView[a].getPaint().measureText(this.onlineTextView[a].getText().toString());
                        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.onlineTextView[a].getLayoutParams();
                        layoutParams3.rightMargin = (int) Math.ceil((double) (this.onlineTextView[a].getTranslationX() + ((float) AndroidUtilities.dp(8.0f)) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - diff))));
                        if (((float) width) < width22) {
                            layoutParams3.width = (int) Math.ceil((double) width);
                        } else {
                            layoutParams3.width = -2;
                        }
                        this.onlineTextView[a].setLayoutParams(layoutParams3);
                    }
                }
                a++;
                f = 0.0f;
                i = 2;
                f2 = 1.0f;
                i2 = 1;
            }
        }
    }

    private void loadMediaCounts() {
        if (this.dialog_id != 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.dialog_id, this.classGuid);
        } else if (this.user_id != 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts((long) this.user_id, this.classGuid);
        } else if (this.chat_id > 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts((long) (-this.chat_id), this.classGuid);
            if (this.mergeDialogId != 0) {
                MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.mergeDialogId, this.classGuid);
            }
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ProfileActivity.this.fragmentView == null) {
                        return true;
                    }
                    ProfileActivity.this.checkListViewScroll();
                    ProfileActivity.this.needLayout();
                    ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        MediaActivity mediaActivity2;
        long did;
        TLRPC.ChatFull chatFull;
        TLRPC.Chat chat;
        RecyclerListView recyclerListView;
        RecyclerListView recyclerListView2;
        RecyclerListView.Holder holder;
        int i = id;
        Object[] objArr = args;
        boolean loadChannelParticipants = false;
        if (i == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) objArr[0]).intValue();
            if (this.user_id != 0) {
                if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                    updateProfileData();
                }
                if ((mask & 1024) != 0 && (recyclerListView2 = this.listView) != null && (holder = (RecyclerListView.Holder) recyclerListView2.findViewHolderForPosition(this.phoneRow)) != null) {
                    this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                }
            } else if (this.chat_id != 0) {
                if (!((mask & 8192) == 0 && (mask & 8) == 0 && (mask & 16) == 0 && (mask & 32) == 0 && (mask & 4) == 0)) {
                    updateOnlineCount();
                    updateProfileData();
                }
                if ((mask & 8192) != 0) {
                    updateRowsIds();
                    ListAdapter listAdapter2 = this.listAdapter;
                    if (listAdapter2 != null) {
                        listAdapter2.notifyDataSetChanged();
                    }
                }
                if (((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) && (recyclerListView = this.listView) != null) {
                    int count = recyclerListView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof UserCell) {
                            ((UserCell) child).update(mask);
                        }
                    }
                }
            }
        } else {
            int loadIndex = 1;
            if (i == NotificationCenter.chatOnlineCountDidLoad) {
                Integer chatId = (Integer) objArr[0];
                if (this.chatInfo != null && (chat = this.currentChat) != null && chat.id == chatId.intValue()) {
                    this.chatInfo.online_count = ((Integer) objArr[1]).intValue();
                    updateOnlineCount();
                    updateProfileData();
                }
            } else if (i == NotificationCenter.contactsDidLoad) {
                createActionBarMenu();
            } else if (i == NotificationCenter.mediaDidLoad) {
                long uid = ((Long) objArr[0]).longValue();
                if (((Integer) objArr[3]).intValue() == this.classGuid) {
                    long did2 = this.dialog_id;
                    if (did2 == 0) {
                        int i2 = this.user_id;
                        if (i2 != 0) {
                            did2 = (long) i2;
                        } else {
                            int i3 = this.chat_id;
                            if (i3 != 0) {
                                did2 = (long) (-i3);
                            }
                        }
                    }
                    int type = ((Integer) objArr[4]).intValue();
                    this.sharedMediaData[type].setTotalCount(((Integer) objArr[1]).intValue());
                    ArrayList<MessageObject> arr = (ArrayList) objArr[2];
                    boolean enc = ((int) did2) == 0;
                    if (uid == did2) {
                        loadIndex = 0;
                    }
                    if (!arr.isEmpty()) {
                        this.sharedMediaData[type].setEndReached(loadIndex, ((Boolean) objArr[5]).booleanValue());
                    }
                    for (int a2 = 0; a2 < arr.size(); a2++) {
                        this.sharedMediaData[type].addMessage(arr.get(a2), loadIndex, false, enc);
                    }
                }
            } else if (i == NotificationCenter.mediaCountsDidLoad) {
                long uid2 = ((Long) objArr[0]).longValue();
                long did3 = this.dialog_id;
                if (did3 == 0) {
                    int i4 = this.user_id;
                    if (i4 != 0) {
                        did3 = (long) i4;
                    } else {
                        int i5 = this.chat_id;
                        if (i5 != 0) {
                            did3 = (long) (-i5);
                        }
                    }
                }
                if (uid2 == did3 || uid2 == this.mergeDialogId) {
                    int[] counts = (int[]) objArr[1];
                    if (uid2 == did3) {
                        this.mediaCount = counts;
                    } else {
                        this.mediaMergeCount = counts;
                    }
                    int[] iArr = this.lastMediaCount;
                    int[] iArr2 = this.prevMediaCount;
                    System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
                    int a3 = 0;
                    while (true) {
                        int[] iArr3 = this.lastMediaCount;
                        if (a3 < iArr3.length) {
                            int[] iArr4 = this.mediaCount;
                            if (iArr4[a3] >= 0) {
                                int[] iArr5 = this.mediaMergeCount;
                                if (iArr5[a3] >= 0) {
                                    iArr3[a3] = iArr4[a3] + iArr5[a3];
                                    if (uid2 == did3 && this.lastMediaCount[a3] != 0) {
                                        MediaDataController.getInstance(this.currentAccount).loadMedia(did3, 50, 0, a3, 2, this.classGuid);
                                    }
                                    a3++;
                                }
                            }
                            int[] iArr6 = this.mediaCount;
                            if (iArr6[a3] >= 0) {
                                this.lastMediaCount[a3] = iArr6[a3];
                            } else {
                                int[] iArr7 = this.mediaMergeCount;
                                if (iArr7[a3] >= 0) {
                                    this.lastMediaCount[a3] = iArr7[a3];
                                } else {
                                    this.lastMediaCount[a3] = 0;
                                }
                            }
                            MediaDataController.getInstance(this.currentAccount).loadMedia(did3, 50, 0, a3, 2, this.classGuid);
                            a3++;
                        } else {
                            updateSharedMediaRows();
                            return;
                        }
                    }
                }
            } else if (i == NotificationCenter.mediaCountDidLoad) {
                long uid3 = ((Long) objArr[0]).longValue();
                long did4 = this.dialog_id;
                if (did4 == 0) {
                    int i6 = this.user_id;
                    if (i6 != 0) {
                        did4 = (long) i6;
                    } else {
                        int i7 = this.chat_id;
                        if (i7 != 0) {
                            did4 = (long) (-i7);
                        }
                    }
                }
                if (uid3 == did4 || uid3 == this.mergeDialogId) {
                    int type2 = ((Integer) objArr[3]).intValue();
                    int mCount = ((Integer) objArr[1]).intValue();
                    if (uid3 == did4) {
                        this.mediaCount[type2] = mCount;
                    } else {
                        this.mediaMergeCount[type2] = mCount;
                    }
                    int[] iArr8 = this.prevMediaCount;
                    int[] iArr9 = this.lastMediaCount;
                    iArr8[type2] = iArr9[type2];
                    int[] iArr10 = this.mediaCount;
                    if (iArr10[type2] >= 0) {
                        int[] iArr11 = this.mediaMergeCount;
                        if (iArr11[type2] >= 0) {
                            iArr9[type2] = iArr10[type2] + iArr11[type2];
                            updateSharedMediaRows();
                        }
                    }
                    int[] iArr12 = this.mediaCount;
                    if (iArr12[type2] >= 0) {
                        this.lastMediaCount[type2] = iArr12[type2];
                    } else {
                        int[] iArr13 = this.mediaMergeCount;
                        if (iArr13[type2] >= 0) {
                            this.lastMediaCount[type2] = iArr13[type2];
                        } else {
                            this.lastMediaCount[type2] = 0;
                        }
                    }
                    updateSharedMediaRows();
                }
            } else if (i == NotificationCenter.encryptedChatCreated) {
                if (this.creatingChat) {
                    AndroidUtilities.runOnUIThread(new Runnable(objArr) {
                        private final /* synthetic */ Object[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ProfileActivity.this.lambda$didReceivedNotification$22$ProfileActivity(this.f$1);
                        }
                    });
                }
            } else if (i == NotificationCenter.encryptedChatUpdated) {
                TLRPC.EncryptedChat chat2 = (TLRPC.EncryptedChat) objArr[0];
                if (this.currentEncryptedChat != null && chat2.id == this.currentEncryptedChat.id) {
                    this.currentEncryptedChat = chat2;
                    updateRowsIds();
                    ListAdapter listAdapter3 = this.listAdapter;
                    if (listAdapter3 != null) {
                        listAdapter3.notifyDataSetChanged();
                    }
                }
            } else if (i == NotificationCenter.blockedUsersDidLoad) {
                boolean oldValue = this.userBlocked;
                if (MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0) {
                    loadChannelParticipants = true;
                }
                this.userBlocked = loadChannelParticipants;
                if (oldValue != loadChannelParticipants) {
                    createActionBarMenu();
                    updateRowsIds();
                    this.listAdapter.notifyDataSetChanged();
                }
            } else if (i == NotificationCenter.chatInfoDidLoad) {
                TLRPC.ChatFull chatFull2 = (TLRPC.ChatFull) objArr[0];
                if (chatFull2.id == this.chat_id) {
                    boolean byChannelUsers = ((Boolean) objArr[2]).booleanValue();
                    if ((this.chatInfo instanceof TLRPC.TL_channelFull) && chatFull2.participants == null && (chatFull = this.chatInfo) != null) {
                        chatFull2.participants = chatFull.participants;
                    }
                    if (this.chatInfo == null && (chatFull2 instanceof TLRPC.TL_channelFull)) {
                        loadChannelParticipants = true;
                    }
                    this.chatInfo = chatFull2;
                    if (this.mergeDialogId == 0 && chatFull2.migrated_from_chat_id != 0) {
                        this.mergeDialogId = (long) (-this.chatInfo.migrated_from_chat_id);
                        MediaDataController.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                    }
                    fetchUsersFromChannelInfo();
                    updateOnlineCount();
                    updateRowsIds();
                    ListAdapter listAdapter4 = this.listAdapter;
                    if (listAdapter4 != null) {
                        listAdapter4.notifyDataSetChanged();
                    }
                    TLRPC.Chat newChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                    if (newChat != null) {
                        this.currentChat = newChat;
                        createActionBarMenu();
                    }
                    if (!this.currentChat.megagroup) {
                        return;
                    }
                    if (loadChannelParticipants || !byChannelUsers) {
                        getChannelParticipants(true);
                    }
                }
            } else if (i == NotificationCenter.closeChats) {
                removeSelfFromStack();
            } else if (i == NotificationCenter.botInfoDidLoad) {
                TLRPC.BotInfo info = (TLRPC.BotInfo) objArr[0];
                if (info.user_id == this.user_id) {
                    this.botInfo = info;
                    updateRowsIds();
                    ListAdapter listAdapter5 = this.listAdapter;
                    if (listAdapter5 != null) {
                        listAdapter5.notifyDataSetChanged();
                    }
                }
            } else if (i == NotificationCenter.userFullInfoDidLoad) {
                if (((Integer) objArr[0]).intValue() == this.user_id) {
                    this.userInfo = (TLRPC.UserFull) objArr[1];
                    if (this.openAnimationInProgress || this.callItem != null) {
                        this.recreateMenuAfterAnimation = true;
                    } else {
                        createActionBarMenu();
                    }
                    updateRowsIds();
                    ListAdapter listAdapter6 = this.listAdapter;
                    if (listAdapter6 != null) {
                        listAdapter6.notifyDataSetChanged();
                    }
                }
            } else if (i == NotificationCenter.didReceiveNewMessages) {
                if (!((Boolean) objArr[2]).booleanValue()) {
                    if (this.dialog_id != 0) {
                        did = this.dialog_id;
                    } else {
                        int i8 = this.user_id;
                        if (i8 != 0) {
                            did = (long) i8;
                        } else {
                            did = (long) (-this.chat_id);
                        }
                    }
                    if (did == ((Long) objArr[0]).longValue()) {
                        boolean enc2 = ((int) did) == 0;
                        ArrayList<MessageObject> arr2 = (ArrayList) objArr[1];
                        int a4 = 0;
                        while (a4 < arr2.size()) {
                            MessageObject obj = arr2.get(a4);
                            if (this.currentEncryptedChat != null && (obj.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction) && (obj.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
                                TLRPC.TL_decryptedMessageActionSetMessageTTL tL_decryptedMessageActionSetMessageTTL = (TLRPC.TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction;
                                ListAdapter listAdapter7 = this.listAdapter;
                                if (listAdapter7 != null) {
                                    listAdapter7.notifyDataSetChanged();
                                }
                            }
                            int type3 = MediaDataController.getMediaType(obj.messageOwner);
                            if (type3 != -1) {
                                this.sharedMediaData[type3].addMessage(obj, 0, true, enc2);
                                a4++;
                            } else {
                                return;
                            }
                        }
                        loadMediaCounts();
                    }
                }
            } else if (i == NotificationCenter.messagesDeleted && !((Boolean) objArr[2]).booleanValue()) {
                int channelId = ((Integer) objArr[1]).intValue();
                if (ChatObject.isChannel(this.currentChat)) {
                    if ((channelId != 0 || this.mergeDialogId == 0) && channelId != this.currentChat.id) {
                        return;
                    }
                } else if (channelId != 0) {
                    return;
                }
                ArrayList<Integer> markAsDeletedMessages = (ArrayList) objArr[0];
                boolean updated = false;
                int N = markAsDeletedMessages.size();
                for (int a5 = 0; a5 < N; a5++) {
                    int b = 0;
                    while (true) {
                        MediaActivity.SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                        if (b >= sharedMediaDataArr.length) {
                            break;
                        }
                        if (sharedMediaDataArr[b].deleteMessage(markAsDeletedMessages.get(a5).intValue(), 0)) {
                            updated = true;
                        }
                        b++;
                    }
                }
                if (updated && (mediaActivity2 = this.mediaActivity) != null) {
                    mediaActivity2.updateAdapters();
                }
                loadMediaCounts();
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$22$ProfileActivity(Object[] args) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        Bundle args2 = new Bundle();
        args2.putInt("enc_id", args[0].id);
        presentFragment(new ChatActivity(args2), true);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        updateProfileData();
        fixLayout();
        SimpleTextView[] simpleTextViewArr = this.nameTextView;
        if (simpleTextViewArr[1] != null) {
            setParentActivityTitle(simpleTextViewArr[1].getText());
        }
    }

    public void onPause() {
        super.onPause();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public void setPlayProfileAnimation(boolean value) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet() && preferences.getBoolean("view_animations", true)) {
            this.playProfileAnimation = value;
        }
    }

    private void updateSharedMediaRows() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        if (this.listAdapter != null) {
            int sharedHeaderRowPrev = this.sharedHeaderRow;
            int photosRowPrev = this.photosRow;
            int filesRowPrev = this.filesRow;
            int linksRowPrev = this.linksRow;
            int audioRowPrev = this.audioRow;
            int voiceRowPrev = this.voiceRow;
            int groupsInCommonRowPrev = this.groupsInCommonRow;
            int i12 = this.sharedSectionRow;
            updateRowsIds();
            if (sharedHeaderRowPrev == -1 && this.sharedHeaderRow != -1) {
                int newRowsCount = 2;
                if (this.photosRow != -1) {
                    newRowsCount = 2 + 1;
                }
                if (this.filesRow != -1) {
                    newRowsCount++;
                }
                if (this.linksRow != -1) {
                    newRowsCount++;
                }
                if (this.audioRow != -1) {
                    newRowsCount++;
                }
                if (this.voiceRow != -1) {
                    newRowsCount++;
                }
                if (this.groupsInCommonRow != -1) {
                    newRowsCount++;
                }
                this.listAdapter.notifyItemRangeInserted(this.sharedHeaderRow, newRowsCount);
            } else if (sharedHeaderRowPrev != -1 && this.sharedHeaderRow != -1) {
                if (!(photosRowPrev == -1 || (i11 = this.photosRow) == -1 || this.prevMediaCount[0] == this.lastMediaCount[0])) {
                    this.listAdapter.notifyItemChanged(i11);
                }
                if (!(filesRowPrev == -1 || (i10 = this.filesRow) == -1 || this.prevMediaCount[1] == this.lastMediaCount[1])) {
                    this.listAdapter.notifyItemChanged(i10);
                }
                if (!(linksRowPrev == -1 || (i9 = this.linksRow) == -1 || this.prevMediaCount[3] == this.lastMediaCount[3])) {
                    this.listAdapter.notifyItemChanged(i9);
                }
                if (!(audioRowPrev == -1 || (i8 = this.audioRow) == -1 || this.prevMediaCount[4] == this.lastMediaCount[4])) {
                    this.listAdapter.notifyItemChanged(i8);
                }
                if (!(voiceRowPrev == -1 || (i7 = this.voiceRow) == -1 || this.prevMediaCount[2] == this.lastMediaCount[2])) {
                    this.listAdapter.notifyItemChanged(i7);
                }
                if (photosRowPrev == -1 && (i6 = this.photosRow) != -1) {
                    this.listAdapter.notifyItemInserted(i6);
                } else if (photosRowPrev != -1 && this.photosRow == -1) {
                    this.listAdapter.notifyItemRemoved(photosRowPrev);
                }
                if (filesRowPrev == -1 && (i5 = this.filesRow) != -1) {
                    this.listAdapter.notifyItemInserted(i5);
                } else if (filesRowPrev != -1 && this.filesRow == -1) {
                    this.listAdapter.notifyItemRemoved(filesRowPrev);
                }
                if (linksRowPrev == -1 && (i4 = this.linksRow) != -1) {
                    this.listAdapter.notifyItemInserted(i4);
                } else if (linksRowPrev != -1 && this.linksRow == -1) {
                    this.listAdapter.notifyItemRemoved(linksRowPrev);
                }
                if (audioRowPrev == -1 && (i3 = this.audioRow) != -1) {
                    this.listAdapter.notifyItemInserted(i3);
                } else if (audioRowPrev != -1 && this.audioRow == -1) {
                    this.listAdapter.notifyItemRemoved(audioRowPrev);
                }
                if (voiceRowPrev == -1 && (i2 = this.voiceRow) != -1) {
                    this.listAdapter.notifyItemInserted(i2);
                } else if (voiceRowPrev != -1 && this.voiceRow == -1) {
                    this.listAdapter.notifyItemRemoved(voiceRowPrev);
                }
                if (groupsInCommonRowPrev == -1 && (i = this.groupsInCommonRow) != -1) {
                    this.listAdapter.notifyItemInserted(i);
                } else if (groupsInCommonRowPrev != -1 && this.groupsInCommonRow == -1) {
                    this.listAdapter.notifyItemRemoved(groupsInCommonRowPrev);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        if (((!isOpen && backward) || (isOpen && !backward)) && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = true;
        }
        if (isOpen) {
            NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad});
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            if (!backward && this.playProfileAnimation && this.allowProfileAnimation) {
                this.openAnimationInProgress = false;
                if (this.recreateMenuAfterAnimation) {
                    createActionBarMenu();
                }
            }
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        }
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    public void setAnimationProgress(float progress) {
        int color;
        int rD;
        int color2;
        int iconColor;
        float f = progress;
        this.animationProgress = f;
        this.listView.setAlpha(f);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f));
        int color3 = AvatarDrawable.getProfileBackColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id);
        int actionBarColor = Theme.getColor(Theme.key_actionBarDefault);
        int r = Color.red(actionBarColor);
        int g = Color.green(actionBarColor);
        int b = Color.blue(actionBarColor);
        int red = (int) (((float) (Color.red(color3) - r)) * f);
        int green = (int) (((float) (Color.green(color3) - g)) * f);
        int blue = (int) (((float) (Color.blue(color3) - b)) * f);
        this.topView.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        int color4 = AvatarDrawable.getIconColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id);
        int iconColor2 = Theme.getColor(Theme.key_actionBarDefaultIcon);
        int r2 = Color.red(iconColor2);
        int g2 = Color.green(iconColor2);
        int b2 = Color.blue(iconColor2);
        int rD2 = (int) (((float) (Color.red(color4) - r2)) * f);
        int gD = (int) (((float) (Color.green(color4) - g2)) * f);
        int bD = (int) (((float) (Color.blue(color4) - b2)) * f);
        int color5 = Theme.getColor(Theme.key_profile_title);
        int titleColor = Theme.getColor(Theme.key_actionBarDefaultTitle);
        int r3 = Color.red(titleColor);
        int g3 = Color.green(titleColor);
        int b3 = Color.blue(titleColor);
        int a = Color.alpha(titleColor);
        int rD3 = (int) (((float) (Color.red(color5) - r3)) * f);
        int gD2 = (int) (((float) (Color.green(color5) - g3)) * f);
        int bD2 = (int) (((float) (Color.blue(color5) - b3)) * f);
        int alpha = (int) (((float) (Color.alpha(color5) - a)) * f);
        int i = 0;
        while (true) {
            int color6 = color5;
            if (i >= 2) {
                break;
            }
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (simpleTextViewArr[i] == null) {
                iconColor = iconColor2;
            } else {
                iconColor = iconColor2;
                simpleTextViewArr[i].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
            }
            i++;
            color5 = color6;
            iconColor2 = iconColor;
        }
        if (this.isOnline[0]) {
            color = Theme.getColor(Theme.key_profile_status);
        } else {
            color = AvatarDrawable.getProfileTextColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id);
        }
        int subtitleColor = Theme.getColor(this.isOnline[0] ? Theme.key_chat_status : Theme.key_actionBarDefaultSubtitle);
        int r4 = Color.red(subtitleColor);
        int g4 = Color.green(subtitleColor);
        int b4 = Color.blue(subtitleColor);
        int a2 = Color.alpha(subtitleColor);
        int rD4 = (int) (((float) (Color.red(color) - r4)) * f);
        int green2 = (int) (((float) (Color.green(color) - g4)) * f);
        int blue2 = (int) (((float) (Color.blue(color) - b4)) * f);
        int alpha2 = (int) (((float) (Color.alpha(color) - a2)) * f);
        int i2 = 0;
        while (true) {
            rD = rD4;
            if (i2 >= 2) {
                break;
            }
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (simpleTextViewArr2[i2] == null) {
                color2 = color;
            } else {
                color2 = color;
                simpleTextViewArr2[i2].setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
            }
            i2++;
            rD4 = rD;
            color = color2;
        }
        this.extraHeight = (int) (((float) this.initialAnimationExtraHeight) * f);
        int i3 = this.user_id;
        if (i3 == 0) {
            i3 = this.chat_id;
        }
        int color7 = AvatarDrawable.getProfileColorForId(i3);
        int i4 = this.user_id;
        if (i4 == 0) {
            i4 = this.chat_id;
        }
        int color22 = AvatarDrawable.getColorForId(i4);
        if (color7 != color22) {
            int rD5 = (int) (((float) (Color.red(color7) - Color.red(color22))) * f);
            int i5 = color7;
            int i6 = color22;
            this.avatarDrawable.setColor(Color.rgb(Color.red(color22) + rD5, Color.green(color22) + ((int) (((float) (Color.green(color7) - Color.green(color22))) * f)), Color.blue(color22) + ((int) (((float) (Color.blue(color7) - Color.blue(color22))) * f))));
            this.avatarImage.invalidate();
            int i7 = rD5;
        } else {
            int i8 = color22;
            int color8 = rD;
        }
        needLayout();
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation) {
            Runnable runnable = callback;
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        this.listView.setLayerType(2, (Paint) null);
        ActionBarMenu menu = this.actionBar.createMenu();
        if (menu.getItem(10) == null && this.animatingItem == null) {
            this.animatingItem = menu.addItem(10, (int) R.drawable.ic_ab_other);
        }
        if (isOpen) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            layoutParams.rightMargin = (int) ((AndroidUtilities.density * -21.0f) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView[1].setLayoutParams(layoutParams);
            int width = (int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (AndroidUtilities.density * 21.0f)));
            float width2 = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * 1.12f) + ((float) this.nameTextView[1].getSideDrawablesSize());
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            if (((float) width) < width2) {
                layoutParams2.width = (int) Math.ceil((double) (((float) width) / 1.12f));
            } else {
                layoutParams2.width = -2;
            }
            this.nameTextView[1].setLayoutParams(layoutParams2);
            this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0f);
            setAnimationProgress(0.0f);
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{0.0f, 1.0f}));
            ImageView imageView = this.writeButton;
            if (imageView != null) {
                imageView.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f}));
            }
            int a = 0;
            while (a < 2) {
                this.onlineTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                this.nameTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                SimpleTextView simpleTextView = this.onlineTextView[a];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = a == 0 ? 0.0f : 1.0f;
                animators.add(ObjectAnimator.ofFloat(simpleTextView, property, fArr));
                SimpleTextView simpleTextView2 = this.nameTextView[a];
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = a == 0 ? 0.0f : 1.0f;
                animators.add(ObjectAnimator.ofFloat(simpleTextView2, property2, fArr2));
                a++;
            }
            ActionBarMenuItem actionBarMenuItem = this.animatingItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(1.0f);
                animators.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{0.0f}));
            }
            ActionBarMenuItem actionBarMenuItem2 = this.callItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{1.0f}));
            }
            ActionBarMenuItem actionBarMenuItem3 = this.editItem;
            if (actionBarMenuItem3 != null) {
                actionBarMenuItem3.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, new float[]{1.0f}));
            }
            animatorSet.playTogether(animators);
        } else {
            this.initialAnimationExtraHeight = this.extraHeight;
            ArrayList<Animator> animators2 = new ArrayList<>();
            animators2.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{1.0f, 0.0f}));
            ImageView imageView2 = this.writeButton;
            if (imageView2 != null) {
                animators2.add(ObjectAnimator.ofFloat(imageView2, View.SCALE_X, new float[]{0.2f}));
                animators2.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f}));
                animators2.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f}));
            }
            int a2 = 0;
            while (a2 < 2) {
                SimpleTextView simpleTextView3 = this.onlineTextView[a2];
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = a2 == 0 ? 1.0f : 0.0f;
                animators2.add(ObjectAnimator.ofFloat(simpleTextView3, property3, fArr3));
                SimpleTextView simpleTextView4 = this.nameTextView[a2];
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = a2 == 0 ? 1.0f : 0.0f;
                animators2.add(ObjectAnimator.ofFloat(simpleTextView4, property4, fArr4));
                a2++;
            }
            ActionBarMenuItem actionBarMenuItem4 = this.animatingItem;
            if (actionBarMenuItem4 != null) {
                actionBarMenuItem4.setAlpha(0.0f);
                animators2.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{1.0f}));
            }
            ActionBarMenuItem actionBarMenuItem5 = this.callItem;
            if (actionBarMenuItem5 != null) {
                actionBarMenuItem5.setAlpha(1.0f);
                animators2.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{0.0f}));
            }
            ActionBarMenuItem actionBarMenuItem6 = this.editItem;
            if (actionBarMenuItem6 != null) {
                actionBarMenuItem6.setAlpha(1.0f);
                animators2.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, new float[]{0.0f}));
            }
            animatorSet.playTogether(animators2);
        }
        final Runnable runnable2 = callback;
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ProfileActivity.this.listView.setLayerType(0, (Paint) null);
                if (ProfileActivity.this.animatingItem != null) {
                    ProfileActivity.this.actionBar.createMenu().clearItems();
                    ActionBarMenuItem unused = ProfileActivity.this.animatingItem = null;
                }
                runnable2.run();
            }
        });
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.getClass();
        AndroidUtilities.runOnUIThread(new Runnable(animatorSet) {
            private final /* synthetic */ AnimatorSet f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.start();
            }
        }, 50);
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public void updateOnlineCount() {
        int i;
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        this.sortedUsers.clear();
        TLRPC.ChatFull chatFull = this.chatInfo;
        if ((chatFull instanceof TLRPC.TL_chatFull) || ((chatFull instanceof TLRPC.TL_channelFull) && chatFull.participants_count <= 200 && this.chatInfo.participants != null)) {
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.chatInfo.participants.participants.get(a).user_id));
                if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(a));
            }
            try {
                Collections.sort(this.sortedUsers, new Comparator(currentTime) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return ProfileActivity.this.lambda$updateOnlineCount$23$ProfileActivity(this.f$1, (Integer) obj, (Integer) obj2);
                    }
                });
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null && (i = this.membersStartRow) > 0) {
                listAdapter2.notifyItemRangeChanged(i, this.sortedUsers.size());
                return;
            }
            return;
        }
        TLRPC.ChatFull chatFull2 = this.chatInfo;
        if ((chatFull2 instanceof TLRPC.TL_channelFull) && chatFull2.participants_count > 200) {
            this.onlineCount = this.chatInfo.online_count;
        }
    }

    public /* synthetic */ int lambda$updateOnlineCount$23$ProfileActivity(int currentTime, Integer lhs, Integer rhs) {
        TLRPC.User user1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.chatInfo.participants.participants.get(rhs.intValue()).user_id));
        TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.chatInfo.participants.participants.get(lhs.intValue()).user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.bot) {
                status1 = -110;
            } else if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.bot) {
                status2 = -110;
            } else if (user2.self) {
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

    public void setChatInfo(TLRPC.ChatFull value) {
        this.chatInfo = value;
        if (!(value == null || value.migrated_from_chat_id == 0 || this.mergeDialogId != 0)) {
            this.mergeDialogId = (long) (-this.chatInfo.migrated_from_chat_id);
            MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.mergeDialogId, this.classGuid);
        }
        fetchUsersFromChannelInfo();
    }

    public void setUserInfo(TLRPC.UserFull value) {
        this.userInfo = value;
    }

    private void fetchUsersFromChannelInfo() {
        TLRPC.Chat chat = this.currentChat;
        if (chat != null && chat.megagroup) {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if ((chatFull instanceof TLRPC.TL_channelFull) && chatFull.participants != null) {
                for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                    TLRPC.ChatParticipant chatParticipant = this.chatInfo.participants.participants.get(a);
                    this.participantsMap.put(chatParticipant.user_id, chatParticipant);
                }
            }
        }
    }

    private void kickUser(int uid) {
        if (uid != 0) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid)), this.chatInfo);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chat_id)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.chatInfo);
        this.playProfileAnimation = false;
        finishFragment();
    }

    public boolean isChat() {
        return this.chat_id != 0;
    }

    /* access modifiers changed from: private */
    public void updateRowsIds() {
        TLRPC.ChatFull chatFull;
        TLRPC.ChatFull chatFull2;
        TLRPC.UserFull userFull;
        this.rowCount = 0;
        this.emptyRow = -1;
        this.infoHeaderRow = -1;
        this.phoneRow = -1;
        this.userInfoRow = -1;
        this.locationRow = -1;
        this.channelInfoRow = -1;
        this.usernameRow = -1;
        this.settingsTimerRow = -1;
        this.settingsKeyRow = -1;
        this.notificationsDividerRow = -1;
        this.notificationsRow = -1;
        this.forbidAddContact = -1;
        this.infoSectionRow = -1;
        this.settingsSectionRow = -1;
        this.membersHeaderRow = -1;
        this.membersStartRow = -1;
        this.membersEndRow = -1;
        this.addMemberRow = -1;
        this.subscribersRow = -1;
        this.administratorsRow = -1;
        this.blockedUsersRow = -1;
        this.membersSectionRow = -1;
        this.sharedHeaderRow = -1;
        this.photosRow = -1;
        this.filesRow = -1;
        this.linksRow = -1;
        this.audioRow = -1;
        this.voiceRow = -1;
        this.groupsInCommonRow = -1;
        this.sharedSectionRow = -1;
        this.qrCodeRow = -1;
        this.unblockRow = -1;
        this.startSecretChatRow = -1;
        this.leaveChannelRow = -1;
        this.joinRow = -1;
        this.lastSectionRow = -1;
        boolean hasMedia = false;
        int a = 0;
        while (true) {
            int[] iArr = this.lastMediaCount;
            if (a >= iArr.length) {
                break;
            } else if (iArr[a] > 0) {
                hasMedia = true;
                break;
            } else {
                a++;
            }
        }
        int a2 = this.rowCount;
        this.rowCount = a2 + 1;
        this.emptyRow = a2;
        if (this.user_id != 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            TLRPC.UserFull userFull2 = this.userInfo;
            boolean hasInfo = (userFull2 != null && !TextUtils.isEmpty(userFull2.about)) || (user != null && !TextUtils.isEmpty(user.username));
            boolean hasPhone = user != null && !TextUtils.isEmpty(user.phone);
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.infoHeaderRow = i;
            if (!this.isBot && (hasPhone || (!hasPhone && !hasInfo))) {
                int i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.phoneRow = i2;
            }
            TLRPC.UserFull userFull3 = this.userInfo;
            if (userFull3 != null && !TextUtils.isEmpty(userFull3.about)) {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.userInfoRow = i3;
            }
            if (user.id == getUserConfig().clientUserId) {
                int i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.qrCodeRow = i4;
            }
            if (!(this.phoneRow == -1 && this.userInfoRow == -1 && this.usernameRow == -1)) {
                int i5 = this.rowCount;
                this.rowCount = i5 + 1;
                this.notificationsDividerRow = i5;
            }
            if (this.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                int i6 = this.rowCount;
                this.rowCount = i6 + 1;
                this.notificationsRow = i6;
            }
            int i7 = this.rowCount;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.infoSectionRow = i7;
            if (this.currentEncryptedChat instanceof TLRPC.TL_encryptedChat) {
                int i9 = i8 + 1;
                this.rowCount = i9;
                this.settingsTimerRow = i8;
                int i10 = i9 + 1;
                this.rowCount = i10;
                this.settingsKeyRow = i9;
                this.rowCount = i10 + 1;
                this.settingsSectionRow = i10;
            }
            if (hasMedia || !((userFull = this.userInfo) == null || userFull.common_chats_count == 0)) {
                int i11 = this.rowCount;
                int i12 = i11 + 1;
                this.rowCount = i12;
                this.sharedHeaderRow = i11;
                if (this.lastMediaCount[0] > 0) {
                    this.rowCount = i12 + 1;
                    this.photosRow = i12;
                } else {
                    this.photosRow = -1;
                }
                if (this.lastMediaCount[1] > 0) {
                    int i13 = this.rowCount;
                    this.rowCount = i13 + 1;
                    this.filesRow = i13;
                } else {
                    this.filesRow = -1;
                }
                if (this.lastMediaCount[3] > 0) {
                    int i14 = this.rowCount;
                    this.rowCount = i14 + 1;
                    this.linksRow = i14;
                } else {
                    this.linksRow = -1;
                }
                if (this.lastMediaCount[4] > 0) {
                    int i15 = this.rowCount;
                    this.rowCount = i15 + 1;
                    this.audioRow = i15;
                } else {
                    this.audioRow = -1;
                }
                if (this.lastMediaCount[2] > 0) {
                    int i16 = this.rowCount;
                    this.rowCount = i16 + 1;
                    this.voiceRow = i16;
                } else {
                    this.voiceRow = -1;
                }
                TLRPC.UserFull userFull4 = this.userInfo;
                if (!(userFull4 == null || userFull4.common_chats_count == 0)) {
                    int i17 = this.rowCount;
                    this.rowCount = i17 + 1;
                    this.groupsInCommonRow = i17;
                }
                int i18 = this.rowCount;
                this.rowCount = i18 + 1;
                this.sharedSectionRow = i18;
            }
            if (user != null && !this.isBot && this.currentEncryptedChat == null && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                if (this.userBlocked) {
                    int i19 = this.rowCount;
                    this.rowCount = i19 + 1;
                    this.unblockRow = i19;
                } else {
                    int i20 = this.rowCount;
                    this.rowCount = i20 + 1;
                    this.startSecretChatRow = i20;
                }
                int i21 = this.rowCount;
                this.rowCount = i21 + 1;
                this.lastSectionRow = i21;
                return;
            }
            return;
        }
        int i22 = this.chat_id;
        if (i22 == 0) {
            return;
        }
        if (i22 > 0) {
            TLRPC.ChatFull chatFull3 = this.chatInfo;
            if ((chatFull3 != null && (!TextUtils.isEmpty(chatFull3.about) || (this.chatInfo.location instanceof TLRPC.TL_channelLocation))) || !TextUtils.isEmpty(this.currentChat.username)) {
                int i23 = this.rowCount;
                this.rowCount = i23 + 1;
                this.infoHeaderRow = i23;
                TLRPC.ChatFull chatFull4 = this.chatInfo;
                if (chatFull4 != null) {
                    if (!TextUtils.isEmpty(chatFull4.about)) {
                        int i24 = this.rowCount;
                        this.rowCount = i24 + 1;
                        this.channelInfoRow = i24;
                    }
                    if (this.chatInfo.location instanceof TLRPC.TL_channelLocation) {
                        int i25 = this.rowCount;
                        this.rowCount = i25 + 1;
                        this.locationRow = i25;
                    }
                }
                if (!TextUtils.isEmpty(this.currentChat.username)) {
                    int i26 = this.rowCount;
                    int i27 = i26 + 1;
                    this.rowCount = i27;
                    this.usernameRow = i26;
                    this.rowCount = i27 + 1;
                    this.qrCodeRow = i27;
                }
            }
            if (this.infoHeaderRow != -1) {
                int i28 = this.rowCount;
                this.rowCount = i28 + 1;
                this.notificationsDividerRow = i28;
            }
            int i29 = this.rowCount;
            this.rowCount = i29 + 1;
            this.notificationsRow = i29;
            if (ChatObject.isChannel(this.currentChat) && this.currentChat.megagroup && ChatObject.hasAdminRights(this.currentChat)) {
                int i30 = this.rowCount;
                this.rowCount = i30 + 1;
                this.forbidAddContact = i30;
            }
            int i31 = this.rowCount;
            this.rowCount = i31 + 1;
            this.infoSectionRow = i31;
            if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup && this.chatInfo != null && (this.currentChat.creator || this.chatInfo.can_view_participants)) {
                int i32 = this.rowCount;
                int i33 = i32 + 1;
                this.rowCount = i33;
                this.membersHeaderRow = i32;
                int i34 = i33 + 1;
                this.rowCount = i34;
                this.subscribersRow = i33;
                this.rowCount = i34 + 1;
                this.administratorsRow = i34;
                if (!(this.chatInfo.banned_count == 0 && this.chatInfo.kicked_count == 0)) {
                    int i35 = this.rowCount;
                    this.rowCount = i35 + 1;
                    this.blockedUsersRow = i35;
                }
                int i36 = this.rowCount;
                this.rowCount = i36 + 1;
                this.membersSectionRow = i36;
            }
            if (hasMedia) {
                int i37 = this.rowCount;
                int i38 = i37 + 1;
                this.rowCount = i38;
                this.sharedHeaderRow = i37;
                if (this.lastMediaCount[0] > 0) {
                    this.rowCount = i38 + 1;
                    this.photosRow = i38;
                } else {
                    this.photosRow = -1;
                }
                if (this.lastMediaCount[1] > 0) {
                    int i39 = this.rowCount;
                    this.rowCount = i39 + 1;
                    this.filesRow = i39;
                } else {
                    this.filesRow = -1;
                }
                if (this.lastMediaCount[3] > 0) {
                    int i40 = this.rowCount;
                    this.rowCount = i40 + 1;
                    this.linksRow = i40;
                } else {
                    this.linksRow = -1;
                }
                if (this.lastMediaCount[4] > 0) {
                    int i41 = this.rowCount;
                    this.rowCount = i41 + 1;
                    this.audioRow = i41;
                } else {
                    this.audioRow = -1;
                }
                if (this.lastMediaCount[2] > 0) {
                    int i42 = this.rowCount;
                    this.rowCount = i42 + 1;
                    this.voiceRow = i42;
                } else {
                    this.voiceRow = -1;
                }
                int i43 = this.rowCount;
                this.rowCount = i43 + 1;
                this.sharedSectionRow = i43;
            }
            if (ChatObject.isChannel(this.currentChat)) {
                if (!this.currentChat.creator && !this.currentChat.left && !this.currentChat.kicked && !this.currentChat.megagroup) {
                    int i44 = this.rowCount;
                    int i45 = i44 + 1;
                    this.rowCount = i45;
                    this.leaveChannelRow = i44;
                    this.rowCount = i45 + 1;
                    this.lastSectionRow = i45;
                }
                if (this.chatInfo != null && this.currentChat.megagroup && this.chatInfo.participants != null && !this.chatInfo.participants.participants.isEmpty()) {
                    if (ChatObject.isNotInChat(this.currentChat) || !this.currentChat.megagroup || !ChatObject.canAddUsers(this.currentChat) || ((chatFull2 = this.chatInfo) != null && chatFull2.participants_count >= MessagesController.getInstance(this.currentAccount).maxMegagroupCount)) {
                        int i46 = this.rowCount;
                        this.rowCount = i46 + 1;
                        this.membersHeaderRow = i46;
                    } else {
                        int i47 = this.rowCount;
                        this.rowCount = i47 + 1;
                        this.addMemberRow = i47;
                    }
                    int i48 = this.rowCount;
                    this.membersStartRow = i48;
                    int size = i48 + this.chatInfo.participants.participants.size();
                    this.rowCount = size;
                    this.membersEndRow = size;
                    this.rowCount = size + 1;
                    this.membersSectionRow = size;
                }
                if (this.lastSectionRow == -1 && this.currentChat.left && !this.currentChat.kicked) {
                    int i49 = this.rowCount;
                    int i50 = i49 + 1;
                    this.rowCount = i50;
                    this.joinRow = i49;
                    this.rowCount = i50 + 1;
                    this.lastSectionRow = i50;
                    return;
                }
                return;
            }
            TLRPC.ChatFull chatFull5 = this.chatInfo;
            if (chatFull5 != null && !(chatFull5.participants instanceof TLRPC.TL_chatParticipantsForbidden)) {
                if (ChatObject.canAddUsers(this.currentChat) || this.currentChat.default_banned_rights == null || !this.currentChat.default_banned_rights.invite_users) {
                    int i51 = this.rowCount;
                    this.rowCount = i51 + 1;
                    this.addMemberRow = i51;
                } else {
                    int i52 = this.rowCount;
                    this.rowCount = i52 + 1;
                    this.membersHeaderRow = i52;
                }
                int i53 = this.rowCount;
                this.membersStartRow = i53;
                int size2 = i53 + this.chatInfo.participants.participants.size();
                this.rowCount = size2;
                this.membersEndRow = size2;
                this.rowCount = size2 + 1;
                this.membersSectionRow = size2;
            }
        } else if (!ChatObject.isChannel(this.currentChat) && (chatFull = this.chatInfo) != null && !(chatFull.participants instanceof TLRPC.TL_chatParticipantsForbidden)) {
            int i54 = this.rowCount;
            int i55 = i54 + 1;
            this.rowCount = i55;
            this.membersHeaderRow = i54;
            this.membersStartRow = i55;
            int size3 = i55 + this.chatInfo.participants.participants.size();
            this.rowCount = size3;
            this.membersEndRow = size3;
            int i56 = size3 + 1;
            this.rowCount = i56;
            this.membersSectionRow = size3;
            this.rowCount = i56 + 1;
            this.addMemberRow = i56;
        }
    }

    private Drawable getScamDrawable() {
        if (this.scamDrawable == null) {
            ScamDrawable scamDrawable2 = new ScamDrawable(11);
            this.scamDrawable = scamDrawable2;
            scamDrawable2.setColor(AvatarDrawable.getProfileTextColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id));
        }
        return this.scamDrawable;
    }

    private void updateProfileData() {
        String onlineTextOverride;
        String newString;
        String str;
        String onlineTextOverride2;
        int currentConnectionState;
        String onlineTextOverride3;
        TLRPC.ChatFull chatFull;
        String newString2;
        if (this.avatarImage != null && this.nameTextView != null) {
            int currentConnectionState2 = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            if (currentConnectionState2 == 2) {
                onlineTextOverride = LocaleController.getString("WaitingForNetwork", R.string.WaitingForNetwork);
            } else if (currentConnectionState2 == 1) {
                onlineTextOverride = LocaleController.getString("Connecting", R.string.Connecting);
            } else if (currentConnectionState2 == 5) {
                onlineTextOverride = LocaleController.getString("Updating", R.string.Updating);
            } else if (currentConnectionState2 == 4) {
                onlineTextOverride = LocaleController.getString("ConnectingToProxy", R.string.ConnectingToProxy);
            } else {
                onlineTextOverride = null;
            }
            if (this.user_id != 0) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                TLRPC.FileLocation photoBig = null;
                if (user.photo != null) {
                    photoBig = user.photo.photo_big;
                }
                this.avatarDrawable.setInfo(user);
                this.avatarImage.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForUser(user, true), user, (String) null, 0, 1);
                String newString3 = UserObject.getName(user);
                if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    newString2 = LocaleController.getString("ChatYourSelf", R.string.ChatYourSelf);
                    newString3 = LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName);
                } else if (user.id == 333000 || user.id == 777000 || user.id == 42777) {
                    newString2 = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                } else if (MessagesController.isSupportUser(user)) {
                    newString2 = LocaleController.getString("SupportStatus", R.string.SupportStatus);
                } else if (this.isBot) {
                    newString2 = LocaleController.getString("Bot", R.string.Bot);
                } else {
                    this.isOnline[0] = false;
                    newString2 = LocaleController.formatUserStatus(this.currentAccount, user, this.isOnline);
                    if (this.onlineTextView[1] != null) {
                        String key = this.isOnline[0] ? Theme.key_profile_status : Theme.key_avatar_subtitleInProfileBlue;
                        this.onlineTextView[1].setTag(key);
                        this.onlineTextView[1].setTextColor(Theme.getColor(key));
                    }
                }
                for (int a = 0; a < 2; a++) {
                    if (this.nameTextView[a] != null) {
                        if (a == 0 && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && user.id / 1000 != 777 && user.id / 1000 != 333 && user.phone != null && user.phone.length() != 0 && ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.id)) == null && (ContactsController.getInstance(this.currentAccount).contactsDict.size() != 0 || !ContactsController.getInstance(this.currentAccount).isLoadingContacts())) {
                            String phoneString = PhoneFormat.getInstance().format(Marker.ANY_NON_NULL_MARKER + user.phone);
                            if (!this.nameTextView[a].getText().equals(phoneString)) {
                                this.nameTextView[a].setText(phoneString);
                            }
                        } else if (!this.nameTextView[a].getText().equals(newString3)) {
                            this.nameTextView[a].setText(newString3);
                        }
                        if (a == 0 && onlineTextOverride != null) {
                            this.onlineTextView[a].setText(onlineTextOverride);
                        } else if (!this.onlineTextView[a].getText().equals(newString2)) {
                            this.onlineTextView[a].setText(newString2);
                        }
                        Drawable leftIcon = this.currentEncryptedChat != null ? Theme.chat_lockIconDrawable : null;
                        Drawable rightIcon = null;
                        if (a == 0) {
                            if (user.scam) {
                                rightIcon = getScamDrawable();
                            } else {
                                MessagesController instance = MessagesController.getInstance(this.currentAccount);
                                long j = this.dialog_id;
                                if (j == 0) {
                                    j = (long) this.user_id;
                                }
                                rightIcon = instance.isDialogMuted(j) ? Theme.chat_muteIconDrawable : null;
                            }
                        } else if (user.scam) {
                            rightIcon = getScamDrawable();
                        } else if (user.verified) {
                            rightIcon = new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable);
                        }
                        this.nameTextView[a].setLeftDrawable(leftIcon);
                        this.nameTextView[a].setRightDrawable(rightIcon);
                    }
                }
                this.avatarImage.getImageReceiver().setVisible(true ^ PhotoViewer.isShowingImage(photoBig), false);
                int i = currentConnectionState2;
                String str2 = onlineTextOverride;
            } else if (this.chat_id != 0) {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (chat != null) {
                    this.currentChat = chat;
                } else {
                    chat = this.currentChat;
                }
                String str3 = "MegaPublic";
                if (!ChatObject.isChannel(chat)) {
                    int count = chat.participants_count;
                    TLRPC.ChatFull chatFull2 = this.chatInfo;
                    if (chatFull2 != null) {
                        count = chatFull2.participants.participants.size();
                    }
                    newString = LocaleController.formatPluralString("Members", count);
                } else if (this.chatInfo == null || (!this.currentChat.megagroup && (this.chatInfo.participants_count == 0 || ChatObject.hasAdminRights(this.currentChat) || this.chatInfo.can_view_participants))) {
                    if (this.currentChat.megagroup) {
                        newString = LocaleController.getString("Loading", R.string.Loading).toLowerCase();
                    } else if ((chat.flags & 64) != 0) {
                        newString = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                    } else {
                        newString = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                    }
                } else if (!this.currentChat.megagroup) {
                    String formatShortNumber = LocaleController.formatShortNumber(this.chatInfo.participants_count, new int[1]);
                    if (this.currentChat.megagroup) {
                        newString = LocaleController.formatPluralString("Members", this.chatInfo.participants_count);
                    } else {
                        newString = LocaleController.formatPluralString("Subscribers", this.chatInfo.participants_count);
                    }
                } else if (this.chatInfo.participants_count != 0) {
                    newString = LocaleController.formatPluralString("Members", this.chatInfo.participants_count);
                } else if (chat.has_geo) {
                    newString = LocaleController.getString("MegaLocation", R.string.MegaLocation).toLowerCase();
                } else if (!TextUtils.isEmpty(chat.username)) {
                    newString = LocaleController.getString(str3, R.string.MegaPublic).toLowerCase();
                } else {
                    newString = LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase();
                }
                boolean changed = false;
                String newString4 = newString;
                int a2 = 0;
                for (int i2 = 2; a2 < i2; i2 = 2) {
                    if (this.nameTextView[a2] == null) {
                        currentConnectionState = currentConnectionState2;
                        onlineTextOverride2 = onlineTextOverride;
                        str = str3;
                    } else {
                        if (chat.title != null && !this.nameTextView[a2].getText().equals(chat.title) && this.nameTextView[a2].setText(chat.title)) {
                            changed = true;
                        }
                        this.nameTextView[a2].setLeftDrawable((Drawable) null);
                        if (a2 != 0) {
                            if (chat.scam) {
                                this.nameTextView[a2].setRightDrawable(getScamDrawable());
                                onlineTextOverride3 = onlineTextOverride;
                            } else if (chat.verified) {
                                this.nameTextView[a2].setRightDrawable((Drawable) new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable));
                                onlineTextOverride3 = onlineTextOverride;
                            } else {
                                this.nameTextView[a2].setRightDrawable((Drawable) null);
                                onlineTextOverride3 = onlineTextOverride;
                            }
                        } else if (chat.scam) {
                            this.nameTextView[a2].setRightDrawable(getScamDrawable());
                            onlineTextOverride3 = onlineTextOverride;
                        } else {
                            onlineTextOverride3 = onlineTextOverride;
                            this.nameTextView[a2].setRightDrawable(MessagesController.getInstance(this.currentAccount).isDialogMuted((long) (-this.chat_id)) ? Theme.chat_muteIconDrawable : null);
                        }
                        if (a2 != 0 || onlineTextOverride3 == null) {
                            String onlineTextOverride4 = onlineTextOverride3;
                            if (!this.currentChat.megagroup || this.chatInfo == null || this.onlineCount <= 0) {
                                if (a2 != 0 || !ChatObject.isChannel(this.currentChat) || (chatFull = this.chatInfo) == null || chatFull.participants_count == 0) {
                                    currentConnectionState = currentConnectionState2;
                                    onlineTextOverride2 = onlineTextOverride4;
                                    str = str3;
                                } else if (this.currentChat.megagroup || this.currentChat.broadcast) {
                                    int[] result = new int[1];
                                    String shortNumber = LocaleController.formatShortNumber(this.chatInfo.participants_count, result);
                                    if (!this.currentChat.megagroup) {
                                        currentConnectionState = currentConnectionState2;
                                        onlineTextOverride2 = onlineTextOverride4;
                                        str = str3;
                                        this.onlineTextView[a2].setText(LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber));
                                    } else if (this.chatInfo.participants_count != 0) {
                                        currentConnectionState = currentConnectionState2;
                                        onlineTextOverride2 = onlineTextOverride4;
                                        str = str3;
                                        this.onlineTextView[a2].setText(LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber));
                                    } else if (chat.has_geo) {
                                        newString4 = LocaleController.getString("MegaLocation", R.string.MegaLocation).toLowerCase();
                                        currentConnectionState = currentConnectionState2;
                                        onlineTextOverride2 = onlineTextOverride4;
                                        str = str3;
                                    } else if (!TextUtils.isEmpty(chat.username)) {
                                        newString4 = LocaleController.getString(str3, R.string.MegaPublic).toLowerCase();
                                        currentConnectionState = currentConnectionState2;
                                        onlineTextOverride2 = onlineTextOverride4;
                                        str = str3;
                                    } else {
                                        newString4 = LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase();
                                        currentConnectionState = currentConnectionState2;
                                        onlineTextOverride2 = onlineTextOverride4;
                                        str = str3;
                                    }
                                } else {
                                    currentConnectionState = currentConnectionState2;
                                    onlineTextOverride2 = onlineTextOverride4;
                                    str = str3;
                                }
                                if (!this.onlineTextView[a2].getText().equals(newString4)) {
                                    this.onlineTextView[a2].setText(newString4);
                                }
                            } else if (!this.onlineTextView[a2].getText().equals(newString4)) {
                                this.onlineTextView[a2].setText(newString4);
                                currentConnectionState = currentConnectionState2;
                                onlineTextOverride2 = onlineTextOverride4;
                                str = str3;
                            } else {
                                currentConnectionState = currentConnectionState2;
                                onlineTextOverride2 = onlineTextOverride4;
                                str = str3;
                            }
                        } else {
                            String onlineTextOverride5 = onlineTextOverride3;
                            this.onlineTextView[a2].setText(onlineTextOverride5);
                            currentConnectionState = currentConnectionState2;
                            onlineTextOverride2 = onlineTextOverride5;
                            str = str3;
                        }
                    }
                    a2++;
                    currentConnectionState2 = currentConnectionState;
                    onlineTextOverride = onlineTextOverride2;
                    str3 = str;
                }
                String str4 = onlineTextOverride;
                if (changed) {
                    needLayout();
                }
                TLRPC.FileLocation photoBig2 = null;
                if (chat.photo != null) {
                    photoBig2 = chat.photo.photo_big;
                }
                this.avatarDrawable.setInfo(chat);
                this.avatarImage.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
                String str5 = newString4;
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForChat(chat, true), chat, (String) null, 0, 1);
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig2), false);
            } else {
                String str6 = onlineTextOverride;
            }
        }
    }

    private void createActionBarMenu() {
        TLRPC.ChatFull chatFull;
        String str;
        int i;
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.clearItems();
        this.animatingItem = null;
        ActionBarMenuItem item = null;
        if (this.user_id == 0) {
            int i2 = this.chat_id;
            if (i2 != 0 && i2 > 0) {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (ChatObject.isChannel(chat)) {
                    if (ChatObject.hasAdminRights(chat) || (chat.megagroup && ChatObject.canChangeChatInfo(chat))) {
                        this.editItem = menu.addItem(12, (int) R.drawable.group_edit_profile);
                    }
                    if (!chat.megagroup && (chatFull = this.chatInfo) != null && chatFull.can_view_stats) {
                        if (0 == 0) {
                            item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                        }
                        item.addSubItem(19, (int) R.drawable.msg_stats, (CharSequence) LocaleController.getString("Statistics", R.string.Statistics));
                    }
                    if (chat.megagroup) {
                        if (item == null) {
                            item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                        }
                        if (this.currentChat.creator || this.currentChat.admin_rights != null) {
                            item.addSubItem(17, (int) R.drawable.msg_search, (CharSequence) LocaleController.getString("SearchMembers", R.string.SearchMembers));
                        }
                        if (!chat.creator && !chat.left && !chat.kicked) {
                            item.addSubItem(7, (int) R.drawable.msg_leave, (CharSequence) LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu));
                        }
                    }
                } else {
                    if (ChatObject.canChangeChatInfo(chat)) {
                        this.editItem = menu.addItem(12, (int) R.drawable.group_edit_profile);
                    }
                    item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                    if (this.currentChat.creator || this.currentChat.admin_rights != null) {
                        item.addSubItem(17, (int) R.drawable.msg_search, (CharSequence) LocaleController.getString("SearchMembers", R.string.SearchMembers));
                    }
                    item.addSubItem(7, (int) R.drawable.msg_leave, (CharSequence) LocaleController.getString("DeleteAndExit", R.string.DeleteAndExit));
                }
            }
        } else if (UserConfig.getInstance(this.currentAccount).getClientUserId() != this.user_id) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user != null) {
                TLRPC.UserFull userFull = this.userInfo;
                if (userFull != null && userFull.phone_calls_available) {
                    this.callItem = menu.addItem(15, (int) R.drawable.ic_call);
                }
                if (this.isBot || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user_id)) == null) {
                    item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                    if (!MessagesController.isSupportUser(user)) {
                        if (this.isBot) {
                            if (!user.bot_nochats) {
                                item.addSubItem(9, (int) R.drawable.msg_addbot, (CharSequence) LocaleController.getString("BotInvite", R.string.BotInvite));
                            }
                            item.addSubItem(10, (int) R.drawable.msg_share, (CharSequence) LocaleController.getString("BotShare", R.string.BotShare));
                        } else {
                            item.addSubItem(1, (int) R.drawable.msg_addcontact, (CharSequence) LocaleController.getString("AddContact", R.string.AddContact));
                        }
                        if (!TextUtils.isEmpty(user.phone)) {
                            item.addSubItem(3, (int) R.drawable.msg_share, (CharSequence) LocaleController.getString("ShareContact", R.string.ShareContact));
                        }
                        if (this.isBot) {
                            int i3 = !this.userBlocked ? R.drawable.msg_block : R.drawable.msg_retry;
                            if (!this.userBlocked) {
                                i = R.string.BotStop;
                                str = "BotStop";
                            } else {
                                i = R.string.BotRestart;
                                str = "BotRestart";
                            }
                            item.addSubItem(2, i3, (CharSequence) LocaleController.getString(str, i));
                        } else {
                            boolean z = this.userBlocked;
                            item.addSubItem(2, (int) R.drawable.msg_block, (CharSequence) !this.userBlocked ? LocaleController.getString("BlockContact", R.string.BlockContact) : LocaleController.getString("Unblock", R.string.Unblock));
                        }
                    } else if (this.userBlocked) {
                        item.addSubItem(2, (int) R.drawable.msg_block, (CharSequence) LocaleController.getString("Unblock", R.string.Unblock));
                    }
                } else {
                    item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                    if (!TextUtils.isEmpty(user.phone)) {
                        item.addSubItem(3, (int) R.drawable.msg_share, (CharSequence) LocaleController.getString("ShareContact", R.string.ShareContact));
                    }
                    boolean z2 = this.userBlocked;
                    item.addSubItem(2, (int) R.drawable.msg_block, (CharSequence) !this.userBlocked ? LocaleController.getString("BlockContact", R.string.BlockContact) : LocaleController.getString("Unblock", R.string.Unblock));
                    item.addSubItem(4, (int) R.drawable.msg_edit, (CharSequence) LocaleController.getString("EditContact", R.string.EditContact));
                    item.addSubItem(5, (int) R.drawable.msg_delete, (CharSequence) LocaleController.getString("DeleteContact", R.string.DeleteContact));
                }
            } else {
                return;
            }
        } else {
            item = menu.addItem(10, (int) R.drawable.ic_ab_other);
            item.addSubItem(3, (int) R.drawable.msg_share, (CharSequence) LocaleController.getString("ShareContact", R.string.ShareContact));
        }
        if (item == null) {
            item = menu.addItem(10, (int) R.drawable.ic_ab_other);
        }
        item.addSubItem(14, (int) R.drawable.msg_home, (CharSequence) LocaleController.getString("AddShortcut", R.string.AddShortcut));
        item.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        ActionBarMenuItem actionBarMenuItem = this.editItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setContentDescription(LocaleController.getString("Edit", R.string.Edit));
        }
        ActionBarMenuItem actionBarMenuItem2 = this.callItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setContentDescription(LocaleController.getString("Call", R.string.Call));
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
    }

    public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        long did = dids.get(0).longValue();
        Bundle args = new Bundle();
        args.putBoolean("scrollToTopOnResume", true);
        int lower_part = (int) did;
        if (lower_part == 0) {
            args.putInt("enc_id", (int) (did >> 32));
        } else if (lower_part > 0) {
            args.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, fragment)) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(args), true);
            removeSelfFromStack();
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), did, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        TLRPC.User user;
        if (requestCode == 101 && (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id))) != null) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null);
            } else {
                VoIPHelper.startCall(user, getParentActivity(), this.userInfo);
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType != 11) {
                switch (viewType) {
                    case 1:
                        view = new HeaderCell(this.mContext, 23);
                        break;
                    case 2:
                        view = new TextDetailCell(this.mContext);
                        break;
                    case 3:
                        view = new AboutLinkCell(this.mContext) {
                            /* access modifiers changed from: protected */
                            public void didPressUrl(String url) {
                                if (url.startsWith("@")) {
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).openByUserName(url.substring(1), ProfileActivity.this, 0);
                                } else if (url.startsWith("#")) {
                                    DialogsActivity fragment = new DialogsActivity((Bundle) null);
                                    fragment.setSearchString(url);
                                    ProfileActivity.this.presentFragment(fragment);
                                } else if (url.startsWith("/") && ProfileActivity.this.parentLayout.fragmentsStack.size() > 1) {
                                    BaseFragment previousFragment = ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                                    if (previousFragment instanceof ChatActivity) {
                                        ProfileActivity.this.finishFragment();
                                        ((ChatActivity) previousFragment).chatActivityEnterView.setCommand((MessageObject) null, url, false, false);
                                    }
                                }
                            }
                        };
                        break;
                    case 4:
                        view = new TextCell(this.mContext);
                        break;
                    case 5:
                        view = new DividerCell(this.mContext);
                        view.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f), 0, 0);
                        break;
                    case 6:
                        view = new NotificationsCheckCell(this.mContext, 23, 70);
                        break;
                    case 7:
                        view = new ShadowSectionCell(this.mContext);
                        break;
                    case 8:
                        view = new UserCell(this.mContext, ProfileActivity.this.addMemberRow == -1 ? 9 : 6, 0, true);
                        break;
                }
            } else {
                view = new EmptyCell(this.mContext, 35);
            }
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String text;
            String value;
            String str;
            int i;
            long did;
            String val;
            String str2;
            int i2;
            TLRPC.ChatParticipant part;
            String role;
            RecyclerView.ViewHolder viewHolder = holder;
            int i3 = position;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 1) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i3 == ProfileActivity.this.infoHeaderRow) {
                    if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup || ProfileActivity.this.channelInfoRow == -1) {
                        headerCell.setText(LocaleController.getString("Info", R.string.Info));
                    } else {
                        headerCell.setText(LocaleController.getString("ReportChatDescription", R.string.ReportChatDescription));
                    }
                } else if (i3 == ProfileActivity.this.sharedHeaderRow) {
                    headerCell.setText(LocaleController.getString("SharedContent", R.string.SharedContent));
                } else if (i3 == ProfileActivity.this.membersHeaderRow) {
                    headerCell.setText(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                }
            } else if (itemViewType == 2) {
                TextDetailCell detailCell = (TextDetailCell) viewHolder.itemView;
                if (i3 == ProfileActivity.this.phoneRow) {
                    TLRPC.User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (!TextUtils.isEmpty(user.phone)) {
                        text = PhoneFormat.getInstance().format(Marker.ANY_NON_NULL_MARKER + user.phone);
                    } else {
                        text = LocaleController.getString("PhoneHidden", R.string.PhoneHidden);
                    }
                    detailCell.setTextAndValue(text, LocaleController.getString("PhoneMobile", R.string.PhoneMobile), false);
                } else if (i3 == ProfileActivity.this.locationRow && ProfileActivity.this.chatInfo != null && (ProfileActivity.this.chatInfo.location instanceof TLRPC.TL_channelLocation)) {
                    detailCell.setTextAndValue(((TLRPC.TL_channelLocation) ProfileActivity.this.chatInfo.location).address, LocaleController.getString("AttachLocation", R.string.AttachLocation), false);
                }
            } else if (itemViewType == 3) {
                AboutLinkCell aboutLinkCell = (AboutLinkCell) viewHolder.itemView;
                if (i3 == ProfileActivity.this.userInfoRow) {
                    aboutLinkCell.setTextAndValue(ProfileActivity.this.userInfo.about, LocaleController.getString("UserBio", R.string.UserBio), ProfileActivity.this.isBot);
                } else if (i3 == ProfileActivity.this.channelInfoRow) {
                    String text2 = ProfileActivity.this.chatInfo.about;
                    while (text2.contains("\n\n\n")) {
                        text2 = text2.replace("\n\n\n", "\n\n");
                    }
                    aboutLinkCell.setText(text2, true);
                }
            } else if (itemViewType == 4) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                textCell.setColors(Theme.key_windowBackgroundWhiteGrayIcon, Theme.key_windowBackgroundWhiteBlackText);
                textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                if (i3 == ProfileActivity.this.photosRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("SharedPhotosAndVideos", R.string.SharedPhotosAndVideos), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[0])}), R.drawable.profile_photos, i3 != ProfileActivity.this.sharedSectionRow - 1);
                } else if (i3 == ProfileActivity.this.filesRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("FilesDataUsage", R.string.FilesDataUsage), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[1])}), R.drawable.profile_file, i3 != ProfileActivity.this.sharedSectionRow - 1);
                } else if (i3 == ProfileActivity.this.linksRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("SharedLinks", R.string.SharedLinks), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[3])}), R.drawable.profile_link, i3 != ProfileActivity.this.sharedSectionRow - 1);
                } else if (i3 == ProfileActivity.this.audioRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("SharedAudioFiles", R.string.SharedAudioFiles), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[4])}), R.drawable.profile_audio, i3 != ProfileActivity.this.sharedSectionRow - 1);
                } else if (i3 == ProfileActivity.this.voiceRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("AudioAutodownload", R.string.AudioAutodownload), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[2])}), R.drawable.profile_voice, i3 != ProfileActivity.this.sharedSectionRow - 1);
                } else if (i3 == ProfileActivity.this.groupsInCommonRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("GroupsInCommonTitle", R.string.GroupsInCommonTitle), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.userInfo.common_chats_count)}), R.drawable.actions_viewmembers, i3 != ProfileActivity.this.sharedSectionRow - 1);
                } else if (i3 == ProfileActivity.this.settingsTimerRow) {
                    TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32)));
                    if (encryptedChat.ttl == 0) {
                        value = LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
                    } else {
                        value = LocaleController.formatTTLString(encryptedChat.ttl);
                    }
                    textCell.setTextAndValue(LocaleController.getString("MessageLifetime", R.string.MessageLifetime), value, false);
                } else if (i3 == ProfileActivity.this.unblockRow) {
                    textCell.setText(LocaleController.getString("Unblock", R.string.Unblock), false);
                    textCell.setColors((String) null, Theme.key_windowBackgroundWhiteRedText5);
                } else if (i3 == ProfileActivity.this.startSecretChatRow) {
                    textCell.setText(LocaleController.getString("StartEncryptedChat", R.string.StartEncryptedChat), false);
                    textCell.setColors((String) null, Theme.key_windowBackgroundWhiteGreenText2);
                } else if (i3 == ProfileActivity.this.settingsKeyRow) {
                    IdenticonDrawable identiconDrawable = new IdenticonDrawable();
                    identiconDrawable.setEncryptedChat(MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32))));
                    textCell.setTextAndValueDrawable(LocaleController.getString("EncryptionKey", R.string.EncryptionKey), identiconDrawable, false);
                } else if (i3 == ProfileActivity.this.leaveChannelRow) {
                    textCell.setColors((String) null, Theme.key_windowBackgroundWhiteRedText5);
                    textCell.setText(LocaleController.getString("LeaveChannel", R.string.LeaveChannel), false);
                    textCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i3 == ProfileActivity.this.joinRow) {
                    textCell.setColors((String) null, Theme.key_windowBackgroundWhiteBlueText2);
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2));
                    if (ProfileActivity.this.currentChat.megagroup) {
                        textCell.setText(LocaleController.getString("ProfileJoinGroup", R.string.ProfileJoinGroup), false);
                    } else {
                        textCell.setText(LocaleController.getString("ProfileJoinChannel", R.string.ProfileJoinChannel), false);
                    }
                    textCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i3 == ProfileActivity.this.subscribersRow) {
                    if (ProfileActivity.this.chatInfo != null) {
                        if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.participants_count)}), R.drawable.actions_viewmembers, i3 != ProfileActivity.this.membersSectionRow - 1);
                        } else {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.participants_count)}), R.drawable.actions_viewmembers, i3 != ProfileActivity.this.membersSectionRow - 1);
                        }
                    } else if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                        textCell.setTextAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), R.drawable.actions_viewmembers, i3 != ProfileActivity.this.membersSectionRow - 1);
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), R.drawable.actions_viewmembers, i3 != ProfileActivity.this.membersSectionRow - 1);
                    }
                } else if (i3 == ProfileActivity.this.administratorsRow) {
                    if (ProfileActivity.this.chatInfo != null) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.admins_count)}), R.drawable.actions_addadmin, i3 != ProfileActivity.this.membersSectionRow - 1);
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), R.drawable.actions_addadmin, i3 != ProfileActivity.this.membersSectionRow - 1);
                    }
                } else if (i3 == ProfileActivity.this.blockedUsersRow) {
                    if (ProfileActivity.this.chatInfo != null) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist), String.format("%d", new Object[]{Integer.valueOf(Math.max(ProfileActivity.this.chatInfo.banned_count, ProfileActivity.this.chatInfo.kicked_count))}), R.drawable.actions_removed, i3 != ProfileActivity.this.membersSectionRow - 1);
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist), R.drawable.actions_removed, i3 != ProfileActivity.this.membersSectionRow - 1);
                    }
                } else if (i3 == ProfileActivity.this.addMemberRow) {
                    textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                    if (ProfileActivity.this.chat_id > 0) {
                        textCell.setTextAndIcon(LocaleController.getString("AddMember", R.string.AddMember), R.drawable.actions_addmember2, true);
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("AddRecipient", R.string.AddRecipient), R.drawable.actions_addmember2, true);
                    }
                } else if (i3 == ProfileActivity.this.qrCodeRow) {
                    textCell.setText(LocaleController.getString("QRCode", R.string.QRCode), false);
                } else if (i3 == ProfileActivity.this.usernameRow && ProfileActivity.this.currentChat != null) {
                    textCell.setText(MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id)).username, true);
                    textCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlue));
                }
                if (i3 != ProfileActivity.this.photosRow && i3 != ProfileActivity.this.filesRow && i3 != ProfileActivity.this.linksRow && i3 != ProfileActivity.this.audioRow && i3 != ProfileActivity.this.voiceRow && i3 != ProfileActivity.this.administratorsRow) {
                    return;
                }
                if ((i3 != ProfileActivity.this.administratorsRow || ProfileActivity.this.chatInfo == null) && i3 == ProfileActivity.this.rowCount - 2) {
                    textCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            } else if (itemViewType == 6) {
                NotificationsCheckCell checkCell = (NotificationsCheckCell) viewHolder.itemView;
                if (i3 == ProfileActivity.this.notificationsRow) {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount);
                    if (ProfileActivity.this.dialog_id != 0) {
                        did = ProfileActivity.this.dialog_id;
                    } else if (ProfileActivity.this.user_id != 0) {
                        did = (long) ProfileActivity.this.user_id;
                    } else {
                        did = (long) (-ProfileActivity.this.chat_id);
                    }
                    boolean enabled = false;
                    boolean custom = preferences.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + did, false);
                    boolean hasOverride = preferences.contains("notify2_" + did);
                    int value2 = preferences.getInt("notify2_" + did, 0);
                    int delta = preferences.getInt("notifyuntil_" + did, 0);
                    if (value2 != 3 || delta == Integer.MAX_VALUE) {
                        if (value2 == 0) {
                            if (hasOverride) {
                                enabled = true;
                            } else {
                                enabled = NotificationsController.getInstance(ProfileActivity.this.currentAccount).isGlobalNotificationsEnabled(did);
                            }
                        } else if (value2 == 1) {
                            enabled = true;
                        } else if (value2 == 2) {
                            enabled = false;
                        } else {
                            enabled = false;
                        }
                        if (!enabled || !custom) {
                            if (enabled) {
                                i2 = R.string.NotificationsOn;
                                str2 = "NotificationsOn";
                            } else {
                                i2 = R.string.NotificationsOff;
                                str2 = "NotificationsOff";
                            }
                            val = LocaleController.getString(str2, i2);
                        } else {
                            val = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                        }
                    } else {
                        int delta2 = delta - ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime();
                        if (delta2 <= 0) {
                            if (custom) {
                                val = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                            } else {
                                val = LocaleController.getString("NotificationsOn", R.string.NotificationsOn);
                            }
                            enabled = true;
                            SharedPreferences sharedPreferences = preferences;
                        } else if (delta2 < 3600) {
                            SharedPreferences sharedPreferences2 = preferences;
                            val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta2 / 60));
                        } else {
                            if (delta2 < 86400) {
                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta2) / 60.0f) / 60.0f))));
                                enabled = false;
                            } else if (delta2 < 31536000) {
                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta2) / 60.0f) / 60.0f) / 24.0f))));
                                enabled = false;
                            } else {
                                val = null;
                                enabled = false;
                            }
                        }
                    }
                    if (val == null) {
                        val = LocaleController.getString("NotificationsOff", R.string.NotificationsOff);
                    }
                    checkCell.setTextAndValueAndCheck(LocaleController.getString("Notifications", R.string.Notifications), val, enabled, ProfileActivity.this.forbidAddContact != -1);
                    checkCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
                if (i3 == ProfileActivity.this.forbidAddContact) {
                    boolean enabled2 = (ProfileActivity.this.currentChat.flags & ConnectionsManager.FileTypeVideo) != 0;
                    if (enabled2) {
                        i = R.string.Forbidden;
                        str = "Forbidden";
                    } else {
                        i = R.string.Allow;
                        str = "Allow";
                    }
                    checkCell.setTextAndValueAndCheck(LocaleController.getString("ForbidAddContacts", R.string.ForbidAddContacts), LocaleController.getString(str, i), enabled2, false);
                }
            } else if (itemViewType == 7) {
                View sectionCell = viewHolder.itemView;
                sectionCell.setTag(Integer.valueOf(position));
                sectionCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            } else if (itemViewType == 8) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                if (ProfileActivity.this.currentChat.creator || ProfileActivity.this.currentChat.admin_rights != null) {
                    if (!ProfileActivity.this.sortedUsers.isEmpty()) {
                        part = ProfileActivity.this.chatInfo.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get(i3 - ProfileActivity.this.membersStartRow)).intValue());
                    } else {
                        part = ProfileActivity.this.chatInfo.participants.participants.get(i3 - ProfileActivity.this.membersStartRow);
                    }
                    if (part != null) {
                        if (part instanceof TLRPC.TL_chatChannelParticipant) {
                            TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_chatChannelParticipant) part).channelParticipant;
                            if (!TextUtils.isEmpty(channelParticipant.rank)) {
                                role = channelParticipant.rank;
                            } else if (channelParticipant instanceof TLRPC.TL_channelParticipantCreator) {
                                role = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                            } else if (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) {
                                role = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                            } else {
                                role = null;
                            }
                        } else if (part instanceof TLRPC.TL_chatParticipantCreator) {
                            role = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                        } else if (part instanceof TLRPC.TL_chatParticipantAdmin) {
                            role = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                        } else {
                            role = null;
                        }
                        userCell.setAdminRole(role);
                        userCell.setData(MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(part.user_id)), (CharSequence) null, (CharSequence) null, 0, i3 != ProfileActivity.this.membersEndRow - 1);
                    }
                    if (i3 == ProfileActivity.this.membersEndRow - 1) {
                        userCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                        return;
                    }
                    return;
                }
                userCell.setVisibility(8);
            } else if (itemViewType == 11) {
                viewHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 1 || type == 5 || type == 7 || type == 9 || type == 10 || type == 11) ? false : true;
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.infoHeaderRow || i == ProfileActivity.this.sharedHeaderRow || i == ProfileActivity.this.membersHeaderRow) {
                return 1;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.locationRow) {
                return 2;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow) {
                return 3;
            }
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.photosRow || i == ProfileActivity.this.filesRow || i == ProfileActivity.this.linksRow || i == ProfileActivity.this.audioRow || i == ProfileActivity.this.voiceRow || i == ProfileActivity.this.groupsInCommonRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.joinRow || i == ProfileActivity.this.unblockRow || i == ProfileActivity.this.qrCodeRow || i == ProfileActivity.this.usernameRow) {
                return 4;
            }
            if (i == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (i == ProfileActivity.this.notificationsRow || i == ProfileActivity.this.forbidAddContact) {
                return 6;
            }
            if (i == ProfileActivity.this.infoSectionRow || i == ProfileActivity.this.sharedSectionRow || i == ProfileActivity.this.lastSectionRow || i == ProfileActivity.this.membersSectionRow || i == ProfileActivity.this.settingsSectionRow) {
                return 7;
            }
            if (i >= ProfileActivity.this.membersStartRow && i < ProfileActivity.this.membersEndRow) {
                return 8;
            }
            if (i == ProfileActivity.this.emptyRow) {
                return 11;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ProfileActivity.this.lambda$getThemeDescriptions$24$ProfileActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItem), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItemIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundActionBarBlue), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundActionBarBlue), new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundActionBarBlue), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_actionBarSelectorBlue), new ThemeDescription(this.nameTextView[1], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_title), new ThemeDescription(this.nameTextView[1], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundActionBarBlue), new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_status), new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_subtitleInProfileBlue), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, new Drawable[]{this.avatarDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundInProfileBlue), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_actionIcon), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_actionBackground), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_actionPressedBackground), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGreenText2), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText5), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueText2), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueButton), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueIcon), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_profile_creatorIcon), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_background), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_cancelColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_cancelColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText), new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkSelection), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.profile_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_verifiedCheck), new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.profile_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_verifiedBackground)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$24$ProfileActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}
