package im.bclpbkiauv.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerMiddle;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DialogObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.XiaomiUtilities;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChannelCreateActivity;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.NewContactActivity;
import im.bclpbkiauv.ui.ProxyListActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BackDrawable;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.MenuDrawable;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.DialogsSearchAdapter;
import im.bclpbkiauv.ui.cell.FmtDialogCell;
import im.bclpbkiauv.ui.cells.AccountSelectCell;
import im.bclpbkiauv.ui.cells.HintDialogCell;
import im.bclpbkiauv.ui.cells.ProfileSearchCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ChatActivityEnterView;
import im.bclpbkiauv.ui.components.CubicBezierInterpolator;
import im.bclpbkiauv.ui.components.DialogsItemAnimator;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.JoinGroupAlert;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.NumberTextView;
import im.bclpbkiauv.ui.components.PacmanAnimation;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.components.UndoView;
import im.bclpbkiauv.ui.dialogs.BottomDialog;
import im.bclpbkiauv.ui.fragments.DialogsFragment;
import im.bclpbkiauv.ui.fragments.adapter.FmtDialogsAdapter;
import im.bclpbkiauv.ui.hui.chats.CreateGroupActivity;
import im.bclpbkiauv.ui.hui.chats.NewChatActivity;
import im.bclpbkiauv.ui.hui.chats.StartChatActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.discovery.QrScanActivity;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.slidemenu.SwipeLayout;
import java.util.ArrayList;
import java.util.Iterator;

public class DialogsFragment extends BaseFmts implements NotificationCenter.NotificationCenterDelegate {
    private static final String TAG = "****** J ****** DialogsFragment **** : ";
    private static final int create_new_chat = 11;
    public static boolean[] dialogsLoaded = new boolean[3];
    private static ArrayList<TLRPC.Dialog> frozenDialogsList = null;
    private static final int item_add_contact = 1003;
    private static final int item_camera_scan = 1004;
    private static final int item_edit_chat = 9999;
    private static final int item_edit_chat_completed = 9997;
    private static final int item_login_on_computer = 9998;
    private static final int item_start_channel = 1002;
    private static final int item_start_chat = 1000;
    private static final int item_start_group = 1001;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private String addToGroupAlertString;
    /* access modifiers changed from: private */
    public boolean allowMoving;
    /* access modifiers changed from: private */
    public boolean allowScrollToHiddenView;
    /* access modifiers changed from: private */
    public boolean allowSwipeDuringCurrentTouch;
    private boolean allowSwitchAccount;
    private ActionBarMenuSubItem archiveItem;
    private boolean askAboutContacts = true;
    private BackDrawable backDrawable;
    private int canClearCacheCount;
    private int canMuteCount;
    private int canPinCount;
    private int canReadCount;
    private int canUnmuteCount;
    private boolean cantSendToChannels;
    private boolean checkCanWrite;
    private boolean checkPermission = true;
    private ActionBarMenuSubItem clearItem;
    /* access modifiers changed from: private */
    public ChatActivityEnterView commentView;
    private FrameLayout containerLayout;
    /* access modifiers changed from: private */
    public ContentView contentView;
    /* access modifiers changed from: private */
    public ActionBarMenuItem createNewChat;
    private int currentConnectionState;
    private FmtConsumDelegate delegate;
    private ActionBarMenuItem deleteItem;
    /* access modifiers changed from: private */
    public int dialogChangeFinished;
    /* access modifiers changed from: private */
    public int dialogInsertFinished;
    /* access modifiers changed from: private */
    public int dialogRemoveFinished;
    /* access modifiers changed from: private */
    public FmtDialogsAdapter dialogsAdapter;
    /* access modifiers changed from: private */
    public DialogsItemAnimator dialogsItemAnimator;
    /* access modifiers changed from: private */
    public boolean dialogsListFrozen;
    /* access modifiers changed from: private */
    public DialogsSearchAdapter dialogsSearchAdapter;
    /* access modifiers changed from: private */
    public int dialogsType;
    /* access modifiers changed from: private */
    public View divider;
    /* access modifiers changed from: private */
    public int folderId;
    boolean isEditModel = false;
    /* access modifiers changed from: private */
    public int lastItemsCount;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public MenuDrawable menuDrawable;
    private int messagesCount;
    private FmtDialogCell movingView;
    private boolean movingWas;
    private ActionBarMenuItem muteItem;
    private SharedPreferences notificationsSettingsSP;
    /* access modifiers changed from: private */
    public long openedDialogId;
    /* access modifiers changed from: private */
    public PacmanAnimation pacmanAnimation;
    private ActionBarMenuItem passcodeItem;
    private AlertDialog permissionDialog;
    private ActionBarMenuItem pinItem;
    /* access modifiers changed from: private */
    public RadialProgressView progressView;
    private ActionBarMenuSubItem readItem;
    private boolean resetDelegate = true;
    private long searchDialogId;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    /* access modifiers changed from: private */
    public FrameLayout searchLayout;
    private TLObject searchObject;
    /* access modifiers changed from: private */
    public String searchString;
    /* access modifiers changed from: private */
    public MrySearchView searchView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private NumberTextView selectedDialogsCountTextView;
    private RecyclerView sideMenu;
    /* access modifiers changed from: private */
    public FmtDialogCell slidingView;
    /* access modifiers changed from: private */
    public boolean startedScrollAtTop;
    /* access modifiers changed from: private */
    public ActionBarMenuItem switchItem;
    /* access modifiers changed from: private */
    public int totalConsumedAmount;
    /* access modifiers changed from: private */
    public UndoView[] undoView = new UndoView[2];
    /* access modifiers changed from: private */
    public boolean waitingForScrollFinished;

    public interface FmtConsumDelegate {
        void changeUnreadCount(int i);

        void onEditModelChange(boolean z, boolean z2);

        void onUpdateState(boolean z, int i, int i2);
    }

    public void setDilogsType(int dialogsType2) {
        this.dialogsType = dialogsType2;
        FmtDialogsAdapter fmtDialogsAdapter = this.dialogsAdapter;
        if (fmtDialogsAdapter != null) {
            fmtDialogsAdapter.setDialogsType(dialogsType2);
            this.dialogsAdapter.notifyDataSetChanged();
        }
    }

    public void setDelegate(FmtConsumDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        FmtConsumDelegate fmtConsumDelegate;
        if ((key.equals("badgeNumberMuted") || key.equals("badgeNumberMessages")) && (fmtConsumDelegate = this.delegate) != null) {
            fmtConsumDelegate.changeUnreadCount(getUnreadCount());
        }
    }

    private class ContentView extends SizeNotifierFrameLayout {
        private int inputFieldHeight;

        public ContentView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            int heightSize2 = heightSize - getPaddingTop();
            measureChildWithMargins(DialogsFragment.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int keyboardSize = getKeyboardHeight();
            int childCount = getChildCount();
            if (DialogsFragment.this.commentView != null) {
                measureChildWithMargins(DialogsFragment.this.commentView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                Object tag = DialogsFragment.this.commentView.getTag();
                if (tag == null || !tag.equals(2)) {
                    this.inputFieldHeight = 0;
                } else {
                    if (keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                        heightSize2 -= DialogsFragment.this.commentView.getEmojiPadding();
                    }
                    this.inputFieldHeight = DialogsFragment.this.commentView.getMeasuredHeight();
                }
            }
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (!(child == null || child.getVisibility() == 8 || child == DialogsFragment.this.commentView || child == DialogsFragment.this.actionBar)) {
                    if (child == DialogsFragment.this.listView || child == DialogsFragment.this.progressView || child == DialogsFragment.this.searchEmptyView) {
                        if (DialogsFragment.this.searchView == null || !DialogsFragment.this.searchView.isSearchFieldVisible()) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (heightSize2 - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)), Integer.MIN_VALUE));
                        }
                    } else if (DialogsFragment.this.commentView == null || !DialogsFragment.this.commentView.isPopupView(child)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    } else if (!AndroidUtilities.isInMultiwindow) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, 1073741824));
                    } else if (AndroidUtilities.isTablet()) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), ((heightSize2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), 1073741824));
                    } else {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(((heightSize2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), 1073741824));
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int paddingBottom;
            int childLeft;
            int childTop;
            int count = getChildCount();
            Object tag = DialogsFragment.this.commentView != null ? DialogsFragment.this.commentView.getTag() : null;
            int i = 2;
            if (tag == null || !tag.equals(2)) {
                paddingBottom = 0;
            } else {
                paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : DialogsFragment.this.commentView.getEmojiPadding();
            }
            setBottomClip(paddingBottom);
            int i2 = 0;
            while (i2 < count) {
                View child = getChildAt(i2);
                if (child.getVisibility() != 8) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    int i3 = gravity & 7 & 7;
                    if (i3 == 1) {
                        childLeft = ((((r - l) - width) / i) + lp.leftMargin) - lp.rightMargin;
                    } else if (i3 != 5) {
                        childLeft = lp.leftMargin;
                    } else {
                        childLeft = (r - width) - lp.rightMargin;
                    }
                    if (verticalGravity == 16) {
                        childTop = (((((b - paddingBottom) - t) - height) / i) + lp.topMargin) - lp.bottomMargin;
                    } else if (verticalGravity == 48) {
                        childTop = lp.topMargin + getPaddingTop();
                    } else if (verticalGravity != 80) {
                        childTop = lp.topMargin;
                    } else {
                        childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                    }
                    if (DialogsFragment.this.commentView != null && DialogsFragment.this.commentView.isPopupView(child)) {
                        if (AndroidUtilities.isInMultiwindow) {
                            childTop = (DialogsFragment.this.commentView.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                        } else {
                            childTop = DialogsFragment.this.commentView.getBottom();
                        }
                    }
                    if (child == DialogsFragment.this.listView) {
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    } else {
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                i2++;
                i = 2;
            }
            notifyHeightChanged();
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            int action = ev.getActionMasked();
            boolean z = true;
            if (action == 0 || action == 1 || action == 3) {
                if (action == 0) {
                    int currentPosition = DialogsFragment.this.layoutManager.findFirstVisibleItemPosition();
                    DialogsFragment dialogsFragment = DialogsFragment.this;
                    if (currentPosition > 1) {
                        z = false;
                    }
                    boolean unused = dialogsFragment.startedScrollAtTop = z;
                } else if (DialogsFragment.this.actionBar.isActionModeShowed()) {
                    boolean unused2 = DialogsFragment.this.allowMoving = true;
                }
                int unused3 = DialogsFragment.this.totalConsumedAmount = 0;
                boolean unused4 = DialogsFragment.this.allowScrollToHiddenView = false;
            }
            return super.onInterceptTouchEvent(ev);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.cantSendToChannels = this.arguments.getBoolean("cantSendToChannels", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
            this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
            this.allowSwitchAccount = this.arguments.getBoolean("allowSwitchAccount");
            this.checkCanWrite = this.arguments.getBoolean("checkCanWrite", true);
            this.folderId = this.arguments.getInt("folderId", 0);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", true);
            this.messagesCount = this.arguments.getInt("messagesCount", 0);
        }
        this.askAboutContacts = MessagesController.getGlobalNotificationsSettings().getBoolean("askAboutContacts", true);
        SharedConfig.loadProxyList();
        if (this.searchString == null) {
            this.currentConnectionState = getConnectionsManager().getConnectionState();
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeSearchByActiveAction);
            getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().addObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().addObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().addObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().addObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().addObserver(this, NotificationCenter.folderBecomeEmpty);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushRemoteOpenChat);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
            ApplicationLoader.mbytMessageReged = 1;
        }
        if (!dialogsLoaded[this.currentAccount]) {
            getMessagesController().loadGlobalNotificationsSettings();
            getMessagesController().loadDialogs(this.folderId, 0, 100, true);
            getMessagesController().loadHintDialogs();
            getContactsController().checkInviteText();
            getMediaDataController().loadRecents(2, false, true, false);
            getMediaDataController().checkFeaturedStickers();
            dialogsLoaded[this.currentAccount] = true;
        }
        getMessagesController().loadPinnedDialogs(this.folderId, 0, (ArrayList<Long>) null);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    /* access modifiers changed from: protected */
    public void lazyLoadData() {
        super.lazyLoadData();
        initData();
    }

    public void onResume() {
        super.onResume();
    }

    public void onResumeForBaseFragment() {
        super.onResumeForBaseFragment();
        boolean hasNotStoragePermission = false;
        this.isPaused = false;
        FmtDialogsAdapter fmtDialogsAdapter = this.dialogsAdapter;
        if (fmtDialogsAdapter != null && !this.dialogsListFrozen) {
            fmtDialogsAdapter.notifyDataSetChanged();
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        if (this.folderId == 0) {
            getMediaDataController().checkStickers(4);
        }
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter2 != null) {
            dialogsSearchAdapter2.notifyDataSetChanged();
        }
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                if (activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                    hasNotStoragePermission = true;
                }
                if (!hasNotStoragePermission) {
                    return;
                }
                if (!hasNotStoragePermission || !activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                    askForPermissons(true);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsFragment.this.lambda$onResumeForBaseFragment$0$DialogsFragment(dialogInterface, i);
                    }
                });
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                this.permissionDialog = create;
                showDialog(create);
            }
        } else if (XiaomiUtilities.isMIUI() && Build.VERSION.SDK_INT >= 19 && !XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_SHOW_WHEN_LOCKED) && getActivity() != null && !MessagesController.getGlobalNotificationsSettings().getBoolean("askedAboutMiuiLockscreen", false)) {
            showDialog(new AlertDialog.Builder((Context) getActivity()).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("PermissionXiaomiLockscreen", R.string.PermissionXiaomiLockscreen)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsFragment.this.lambda$onResumeForBaseFragment$1$DialogsFragment(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), $$Lambda$DialogsFragment$wVgEwnFqqRTOL1cY7NfK_nZzigw.INSTANCE).create());
        }
    }

    public /* synthetic */ void lambda$onResumeForBaseFragment$0$DialogsFragment(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$onResumeForBaseFragment$1$DialogsFragment(DialogInterface dialog, int which) {
        Intent intent = XiaomiUtilities.getPermissionManagerIntent();
        if (intent != null) {
            try {
                getActivity().startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent2 = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent2.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                    getActivity().startActivity(intent2);
                } catch (Exception xx) {
                    FileLog.e((Throwable) xx);
                }
            }
        }
    }

    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity != null) {
            ArrayList<String> permissons = new ArrayList<>();
            if (activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                permissons.add(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE);
                permissons.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            if (!permissons.isEmpty()) {
                try {
                    activity.requestPermissions((String[]) permissons.toArray(new String[0]), 1);
                } catch (Exception e) {
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        this.isPaused = true;
        MrySearchView mrySearchView = this.searchView;
        if (mrySearchView != null && mrySearchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.searchString == null) {
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeSearchByActiveAction);
            getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().removeObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().removeObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().removeObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().removeObserver(this, NotificationCenter.folderBecomeEmpty);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        SharedPreferences sharedPreferences = this.notificationsSettingsSP;
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
                    DialogsFragment.this.onSharedPreferenceChanged(sharedPreferences, str);
                }
            });
            this.notificationsSettingsSP = null;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.searching = false;
        this.searchWas = false;
        this.pacmanAnimation = null;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                DialogsFragment.this.lambda$onCreateView$3$DialogsFragment();
            }
        });
        FrameLayout frameLayout = new FrameLayout(this.context);
        this.containerLayout = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = this.containerLayout;
        initActionBar(this.containerLayout);
        initView(this.containerLayout);
        initSearchView(this.containerLayout);
        initListener();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$onCreateView$3$DialogsFragment() {
        Theme.createChatResources(this.context, false);
    }

    /* access modifiers changed from: private */
    public void toggleEditModel() {
        boolean z = true;
        this.isEditModel = !this.isEditModel;
        this.actionBar.showActionMode();
        if (this.isEditModel) {
            this.actionBar.setBackTitle(LocaleController.getString(R.string.Done));
        } else {
            this.actionBar.setBackButtonImage(R.mipmap.ic_edit);
        }
        AndroidUtilities.clearDrawableAnimation(this.actionBar.getBackButton());
        AndroidUtilities.clearDrawableAnimation(this.actionBar.getBackTitleTextView());
        AndroidUtilities.clearDrawableAnimation(this.createNewChat);
        int pivotY = ActionBar.getCurrentActionBarHeight() / 2;
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                if (!DialogsFragment.this.isEditModel) {
                    if (DialogsFragment.this.actionBar.getBackButton() != null) {
                        DialogsFragment.this.actionBar.getBackButton().setVisibility(0);
                    }
                    if (DialogsFragment.this.createNewChat != null) {
                        DialogsFragment.this.createNewChat.setVisibility(0);
                    }
                } else if (DialogsFragment.this.actionBar.getBackTitleTextView() != null) {
                    DialogsFragment.this.actionBar.getBackTitleTextView().setVisibility(0);
                }
            }

            public void onAnimationEnd(Animator animation) {
                if (DialogsFragment.this.isEditModel) {
                    if (DialogsFragment.this.actionBar.getBackButton() != null) {
                        DialogsFragment.this.actionBar.getBackButton().setVisibility(8);
                    }
                    if (DialogsFragment.this.createNewChat != null) {
                        DialogsFragment.this.createNewChat.setVisibility(8);
                    }
                } else if (DialogsFragment.this.actionBar.getBackTitleTextView() != null) {
                    DialogsFragment.this.actionBar.getBackTitleTextView().setVisibility(8);
                }
            }
        };
        float f = 1.0f;
        if (this.actionBar.getBackButton() != null) {
            this.actionBar.getBackButton().setTag(-1);
            this.actionBar.getBackButton().setPivotY((float) pivotY);
            View backButton = this.actionBar.getBackButton();
            Property property = View.SCALE_Y;
            float[] fArr = new float[2];
            fArr[0] = this.isEditModel ? 1.0f : 0.1f;
            fArr[1] = this.isEditModel ? 0.1f : 1.0f;
            Animator animator = ObjectAnimator.ofFloat(backButton, property, fArr);
            animator.addListener(listener);
            animators.add(animator);
        }
        if (this.actionBar.getBackTitleTextView() != null) {
            this.actionBar.getBackTitleTextView().setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    DialogsFragment.this.lambda$toggleEditModel$4$DialogsFragment(view);
                }
            });
            this.actionBar.getBackTitleTextView().setPivotY((float) pivotY);
            TextView backTitleTextView = this.actionBar.getBackTitleTextView();
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[2];
            fArr2[0] = this.isEditModel ? 0.1f : 1.0f;
            fArr2[1] = this.isEditModel ? 1.0f : 0.1f;
            Animator animator2 = ObjectAnimator.ofFloat(backTitleTextView, property2, fArr2);
            animator2.addListener(listener);
            animators.add(animator2);
        }
        ActionBarMenuItem actionBarMenuItem = this.createNewChat;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setPivotY((float) pivotY);
            ActionBarMenuItem actionBarMenuItem2 = this.createNewChat;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[2];
            fArr3[0] = this.isEditModel ? 1.0f : 0.1f;
            if (this.isEditModel) {
                f = 0.1f;
            }
            fArr3[1] = f;
            Animator animator3 = ObjectAnimator.ofFloat(actionBarMenuItem2, property3, fArr3);
            animator3.addListener(listener);
            animators.add(animator3);
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(250);
        animatorSet.start();
        if (this.isEditModel) {
            updateCounters(false, false);
        } else {
            hideActionPanel();
        }
        this.dialogsAdapter.setEdit(this.isEditModel);
        this.dialogsAdapter.notifyDataSetChanged();
        FmtConsumDelegate fmtConsumDelegate = this.delegate;
        if (fmtConsumDelegate != null) {
            boolean z2 = this.isEditModel;
            if (getCanReadCountInAllDialogs() <= 0) {
                z = false;
            }
            fmtConsumDelegate.onEditModelChange(z2, z);
        }
    }

    public /* synthetic */ void lambda$toggleEditModel$4$DialogsFragment(View v) {
        toggleEditModel();
    }

    private void initActionBar(FrameLayout containerLayout2) {
        this.actionBar = createActionBar();
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Chats", R.string.Chats));
        this.actionBar.setCastShadows(true);
        containerLayout2.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.searchString == null && this.folderId == 0) {
            this.passcodeItem = menu.addItem(1, (int) R.drawable.lock_close);
            updatePasscodeButton();
        }
        this.actionBar.setBackButtonImage(R.mipmap.ic_edit);
        ActionBarMenuItem addItem = menu.addItem(11, (int) R.mipmap.ic_add_circle);
        this.createNewChat = addItem;
        addItem.addSubItem(1000, (int) R.mipmap.fmt_dialog_menu_chat, (CharSequence) LocaleController.getString("StartChats", R.string.StartChats));
        this.createNewChat.addSubItem(1001, (int) R.mipmap.fmt_dialog_menu_group, (CharSequence) LocaleController.getString("NewGroup", R.string.NewGroup));
        this.createNewChat.addSubItem(1003, (int) R.mipmap.fmt_dialog_menu_add, (CharSequence) LocaleController.getString("AddFriends", R.string.AddFriends));
        this.createNewChat.addSubItem(1004, (int) R.mipmap.fmt_dialog_menu_scan, (CharSequence) LocaleController.getString("Scan", R.string.Scan));
        if (this.folderId != 0) {
            this.actionBar.setTitle(LocaleController.getString("ArchivedChats", R.string.ArchivedChats));
        }
        this.actionBar.setSupportsHolidayImage(true);
        this.actionBar.setTitleActionRunnable(new Runnable() {
            public final void run() {
                DialogsFragment.this.lambda$initActionBar$5$DialogsFragment();
            }
        });
        if (this.allowSwitchAccount && UserConfig.getActivatedAccountsCount() > 1) {
            this.switchItem = menu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView imageView = new BackupImageView(this.context);
            imageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            this.switchItem.addView(imageView, LayoutHelper.createFrame(36, 36, 17));
            TLRPC.User user = getUserConfig().getCurrentUser();
            avatarDrawable.setInfo(user);
            imageView.getImageReceiver().setCurrentAccount(this.currentAccount);
            imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
            for (int a = 0; a < 3; a++) {
                if (AccountInstance.getInstance(a).getUserConfig().getCurrentUser() != null) {
                    AccountSelectCell cell = new AccountSelectCell(this.context);
                    cell.setAccount(a, true);
                    this.switchItem.addSubItem(a + 10, cell, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                }
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DialogsFragment.this.toggleEditModel();
                } else if (id == 1) {
                    SharedConfig.appLocked = true ^ SharedConfig.appLocked;
                    SharedConfig.saveConfig();
                    DialogsFragment.this.updatePasscodeButton();
                } else if (id == 2) {
                    DialogsFragment.this.presentFragment(new ProxyListActivity());
                } else if (id == 11) {
                    DialogsFragment.this.presentFragment(new NewChatActivity((Bundle) null));
                } else if (id < 10 || id >= 13) {
                    if (id == 1000) {
                        DialogsFragment.this.presentFragment(new StartChatActivity((Bundle) null));
                    } else if (id == 1001) {
                        DialogsFragment.this.presentFragment(new CreateGroupActivity(new Bundle()));
                    } else if (id == 1002) {
                        Bundle args = new Bundle();
                        args.putInt("step", 0);
                        DialogsFragment.this.presentFragment(new ChannelCreateActivity(args));
                    } else if (id == 1003) {
                        DialogsFragment.this.presentFragment(new AddContactsActivity());
                    } else if (id == 1004) {
                        DialogsFragment.this.presentFragment(new QrScanActivity());
                    }
                } else if (DialogsFragment.this.getParentActivity() != null) {
                }
            }
        });
    }

    public /* synthetic */ void lambda$initActionBar$5$DialogsFragment() {
        if (!this.isEditModel) {
            LinearLayoutManager linearLayoutManager = this.layoutManager;
            boolean hasHiddenArchive = hasHiddenArchive();
            linearLayoutManager.scrollToPositionWithOffset(hasHiddenArchive ? 1 : 0, AndroidUtilities.dp(55.0f));
        }
    }

    private void initSearchView(FrameLayout containerLayout2) {
        FrameLayout frameLayout = new FrameLayout(this.context);
        this.searchLayout = frameLayout;
        containerLayout2.addView(frameLayout, LayoutHelper.createFrameWithActionBar(-1, 55));
        MrySearchView mrySearchView = new MrySearchView(this.context);
        this.searchView = mrySearchView;
        mrySearchView.setHintText(LocaleController.getString("SearchMessageOrUser", R.string.SearchMessageOrUser));
        this.searchLayout.addView(this.searchView, LayoutHelper.createFrame(-1.0f, 35.0f, GravityCompat.START, 10.0f, 10.0f, 10.0f, 10.0f));
        this.searchLayout.setBackgroundColor(Theme.getColor(Theme.key_searchview_solidColor));
        this.searchView.setEditTextBackground(getParentActivity().getDrawable(R.drawable.shape_edit_bg));
        View view = new View(this.context);
        this.divider = view;
        view.setBackground(getResources().getDrawable(R.drawable.header_shadow).mutate());
        containerLayout2.addView(this.divider, LayoutHelper.createFrameWithActionBar(-1, 1));
        this.searchView.setiSearchViewDelegate(new MrySearchView.ISearchViewDelegate() {
            public void onStart(boolean focus) {
                if (focus) {
                    DialogsFragment.this.hideTitle();
                } else {
                    DialogsFragment.this.showTitle();
                }
                if (DialogsFragment.this.contentView != null) {
                    DialogsFragment.this.contentView.requestLayout();
                }
            }

            public void onSearchExpand() {
                DialogsFragment.this.searchLayout.setBackgroundColor(Theme.getColor(Theme.key_searchview_solidColor));
                boolean unused = DialogsFragment.this.searching = true;
                if (DialogsFragment.this.switchItem != null) {
                    DialogsFragment.this.switchItem.setVisibility(8);
                }
                if (!(DialogsFragment.this.listView == null || DialogsFragment.this.searchString == null)) {
                    DialogsFragment.this.listView.setEmptyView(DialogsFragment.this.searchEmptyView);
                    DialogsFragment.this.progressView.setVisibility(8);
                }
                DialogsFragment.this.updatePasscodeButton();
                DialogsFragment.this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", R.string.AccDescrGoBack));
            }

            public boolean canCollapseSearch() {
                if (DialogsFragment.this.switchItem != null) {
                    DialogsFragment.this.switchItem.setVisibility(0);
                }
                if (DialogsFragment.this.searchString != null) {
                    return false;
                }
                return true;
            }

            public void onSearchCollapse() {
                DialogsFragment.this.searchLayout.setBackgroundColor(Theme.getColor(Theme.key_searchview_solidColor));
                boolean unused = DialogsFragment.this.searching = false;
                boolean unused2 = DialogsFragment.this.searchWas = false;
                if (DialogsFragment.this.listView != null) {
                    DialogsFragment.this.listView.setEmptyView(DialogsFragment.this.folderId == 0 ? DialogsFragment.this.progressView : null);
                    DialogsFragment.this.searchEmptyView.setVisibility(8);
                    if (DialogsFragment.this.listView.getAdapter() != DialogsFragment.this.dialogsAdapter) {
                        DialogsFragment.this.listView.setAdapter(DialogsFragment.this.dialogsAdapter);
                        DialogsFragment.this.dialogsAdapter.notifyDataSetChanged();
                    }
                }
                if (DialogsFragment.this.dialogsSearchAdapter != null) {
                    DialogsFragment.this.dialogsSearchAdapter.searchDialogs((String) null);
                }
                DialogsFragment.this.updatePasscodeButton();
                if (DialogsFragment.this.menuDrawable != null) {
                    DialogsFragment.this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", R.string.AccDescrOpenMenu));
                }
            }

            public void onTextChange(String text) {
                if (text.length() != 0 || (DialogsFragment.this.dialogsSearchAdapter != null && DialogsFragment.this.dialogsSearchAdapter.hasRecentRearch())) {
                    boolean unused = DialogsFragment.this.searchWas = true;
                    if (!(DialogsFragment.this.dialogsSearchAdapter == null || DialogsFragment.this.listView.getAdapter() == DialogsFragment.this.dialogsSearchAdapter)) {
                        DialogsFragment.this.listView.setAdapter(DialogsFragment.this.dialogsSearchAdapter);
                        DialogsFragment.this.dialogsSearchAdapter.notifyDataSetChanged();
                    }
                    if (DialogsFragment.this.searchEmptyView != null && DialogsFragment.this.listView.getEmptyView() != DialogsFragment.this.searchEmptyView) {
                        DialogsFragment.this.progressView.setVisibility(8);
                        DialogsFragment.this.listView.setEmptyView(DialogsFragment.this.searchEmptyView);
                    }
                }
            }

            public void onActionSearch(String trim) {
            }
        });
    }

    private void initView(FrameLayout containerLayout2) {
        ContentView contentView2 = new ContentView(this.context);
        this.contentView = contentView2;
        containerLayout2.addView(contentView2, LayoutHelper.createFrameWithActionBar(-1, -2));
        this.contentView.setBackgroundColor(0);
        this.dialogsItemAnimator = new DialogsItemAnimator() {
            public void onRemoveFinished(RecyclerView.ViewHolder item) {
                if (DialogsFragment.this.dialogRemoveFinished == 2) {
                    int unused = DialogsFragment.this.dialogRemoveFinished = 1;
                }
            }

            public void onAddFinished(RecyclerView.ViewHolder item) {
                if (DialogsFragment.this.dialogInsertFinished == 2) {
                    int unused = DialogsFragment.this.dialogInsertFinished = 1;
                }
            }

            public void onChangeFinished(RecyclerView.ViewHolder item, boolean oldItem) {
                if (DialogsFragment.this.dialogChangeFinished == 2) {
                    int unused = DialogsFragment.this.dialogChangeFinished = 1;
                }
            }

            /* access modifiers changed from: protected */
            public void onAllAnimationsDone() {
                if (DialogsFragment.this.dialogRemoveFinished == 1 || DialogsFragment.this.dialogInsertFinished == 1 || DialogsFragment.this.dialogChangeFinished == 1) {
                    DialogsFragment.this.onDialogAnimationFinished();
                }
            }
        };
        AnonymousClass5 r0 = new RecyclerListView(this.context) {
            private boolean firstLayout = true;
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (DialogsFragment.this.slidingView != null && DialogsFragment.this.pacmanAnimation != null) {
                    DialogsFragment.this.pacmanAnimation.draw(canvas, DialogsFragment.this.slidingView.getTop() + (DialogsFragment.this.slidingView.getMeasuredHeight() / 2));
                }
            }

            public void setAdapter(RecyclerView.Adapter adapter) {
                super.setAdapter(adapter);
                this.firstLayout = true;
            }

            private void checkIfAdapterValid() {
                if (DialogsFragment.this.listView != null && DialogsFragment.this.dialogsAdapter != null && DialogsFragment.this.listView.getAdapter() == DialogsFragment.this.dialogsAdapter && DialogsFragment.this.lastItemsCount != DialogsFragment.this.dialogsAdapter.getItemCount()) {
                    this.ignoreLayout = true;
                    DialogsFragment.this.dialogsAdapter.notifyDataSetChanged();
                    this.ignoreLayout = false;
                }
            }

            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (DialogsFragment.this.searchEmptyView != null) {
                    DialogsFragment.this.searchEmptyView.setPadding(left, top, right, bottom);
                }
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthSpec, int heightSpec) {
                if (this.firstLayout && DialogsFragment.this.getMessagesController().dialogsLoaded) {
                    if (DialogsFragment.this.hasHiddenArchive()) {
                        this.ignoreLayout = true;
                        DialogsFragment.this.layoutManager.scrollToPositionWithOffset(1, AndroidUtilities.dp(55.0f));
                        this.ignoreLayout = false;
                    }
                    this.firstLayout = false;
                }
                checkIfAdapterValid();
                super.onMeasure(widthSpec, heightSpec);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (!(DialogsFragment.this.dialogRemoveFinished == 0 && DialogsFragment.this.dialogInsertFinished == 0 && DialogsFragment.this.dialogChangeFinished == 0) && !DialogsFragment.this.dialogsItemAnimator.isRunning()) {
                    DialogsFragment.this.onDialogAnimationFinished();
                }
            }

            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                return super.drawChild(canvas, child, drawingTime);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            public boolean onTouchEvent(MotionEvent e) {
                if (DialogsFragment.this.waitingForScrollFinished || DialogsFragment.this.dialogRemoveFinished != 0 || DialogsFragment.this.dialogInsertFinished != 0 || DialogsFragment.this.dialogChangeFinished != 0) {
                    return false;
                }
                int action = e.getAction();
                boolean result = super.onTouchEvent(e);
                if ((action == 1 || action == 3) && DialogsFragment.this.allowScrollToHiddenView) {
                    int currentPosition = DialogsFragment.this.layoutManager.findFirstVisibleItemPosition();
                    if (currentPosition == 1) {
                        View view = DialogsFragment.this.layoutManager.findViewByPosition(currentPosition);
                        int height = (AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 77.0f : 71.0f) / 4) * 3;
                        int diff = view.getTop() + view.getMeasuredHeight();
                        if (view != null) {
                            if (diff < height) {
                                DialogsFragment.this.listView.smoothScrollBy(0, AndroidUtilities.dp(55.0f) + diff, CubicBezierInterpolator.EASE_OUT_QUINT);
                            } else {
                                DialogsFragment.this.listView.smoothScrollBy(0, view.getTop() + AndroidUtilities.dp(55.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                            }
                        }
                    }
                    boolean unused = DialogsFragment.this.allowScrollToHiddenView = false;
                }
                return result;
            }

            public boolean onInterceptTouchEvent(MotionEvent e) {
                if (DialogsFragment.this.waitingForScrollFinished || DialogsFragment.this.dialogRemoveFinished != 0 || DialogsFragment.this.dialogInsertFinished != 0 || DialogsFragment.this.dialogChangeFinished != 0) {
                    return false;
                }
                if (e.getAction() == 0) {
                    DialogsFragment dialogsFragment = DialogsFragment.this;
                    boolean unused = dialogsFragment.allowSwipeDuringCurrentTouch = !dialogsFragment.actionBar.isActionModeShowed();
                    checkIfAdapterValid();
                }
                if (e.getAction() != 2 || (!DialogsFragment.this.isEditModel && !getLongPressCalled())) {
                    return super.onInterceptTouchEvent(e);
                }
                return true;
            }
        };
        this.listView = r0;
        int i = 2;
        r0.setOverScrollMode(2);
        this.listView.setItemAnimator(this.dialogsItemAnimator);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setInstantClick(true);
        this.listView.setTag(4);
        AnonymousClass6 r02 = new LinearLayoutManager(this.context) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                if (!DialogsFragment.this.hasHiddenArchive() || position != 1) {
                    LinearSmoothScrollerMiddle linearSmoothScroller = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                    linearSmoothScroller.setTargetPosition(position);
                    startSmoothScroll(linearSmoothScroller);
                    return;
                }
                super.smoothScrollToPosition(recyclerView, state, position);
            }

            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                View view;
                View view2;
                if (DialogsFragment.this.listView.getAdapter() == DialogsFragment.this.dialogsAdapter && !DialogsFragment.this.allowScrollToHiddenView && DialogsFragment.this.folderId == 0 && dy < 0 && DialogsFragment.this.getMessagesController().hasHiddenArchive()) {
                    int currentPosition = DialogsFragment.this.layoutManager.findFirstVisibleItemPosition();
                    int computeVerticalScrollOffset = computeVerticalScrollOffset(state);
                    if (currentPosition == 0 && (view2 = DialogsFragment.this.layoutManager.findViewByPosition(currentPosition)) != null && view2.getBottom() <= AndroidUtilities.dp(63.0f)) {
                        currentPosition = 1;
                    }
                    if (!(currentPosition == 0 || currentPosition == -1 || (view = DialogsFragment.this.layoutManager.findViewByPosition(currentPosition)) == null)) {
                        int canScrollDy = (-view.getTop()) + ((currentPosition - 1) * (AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 77.0f : 71.0f) + 1)) + AndroidUtilities.dp(55.0f);
                        if (canScrollDy < Math.abs(dy)) {
                            DialogsFragment dialogsFragment = DialogsFragment.this;
                            int unused = dialogsFragment.totalConsumedAmount = dialogsFragment.totalConsumedAmount + Math.abs(dy);
                            dy = -canScrollDy;
                            if (DialogsFragment.this.startedScrollAtTop && DialogsFragment.this.totalConsumedAmount >= AndroidUtilities.dp(150.0f)) {
                                boolean unused2 = DialogsFragment.this.allowScrollToHiddenView = true;
                                try {
                                    DialogsFragment.this.listView.performHapticFeedback(3, 2);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        };
        this.layoutManager = r02;
        r02.setOrientation(1);
        this.listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.top = AndroidUtilities.dp(55.0f);
                }
                RecyclerView.Adapter adapter = parent.getAdapter();
                if ((adapter instanceof FmtDialogsAdapter) && position == adapter.getItemCount() - 1) {
                    outRect.bottom = AndroidUtilities.dp(10.0f);
                }
            }
        });
        this.listView.setLayoutManager(this.layoutManager);
        RecyclerListView recyclerListView = this.listView;
        if (LocaleController.isRTL) {
            i = 1;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(this.context);
        this.searchEmptyView = emptyTextProgressView;
        emptyTextProgressView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setTopImage(R.drawable.settings_noresults);
        this.searchEmptyView.setText(LocaleController.getString("SettingsNoResults", R.string.SettingsNoResults));
        this.contentView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        RadialProgressView radialProgressView = new RadialProgressView(this.context);
        this.progressView = radialProgressView;
        radialProgressView.setVisibility(8);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
    }

    private void initListener() {
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                DialogsFragment.this.lambda$initListener$6$DialogsFragment(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            public boolean onItemClick(View clickView, int position, float x, float y) {
                TLRPC.Chat chat;
                if (DialogsFragment.this.getParentActivity() == null) {
                    int i = position;
                    return false;
                } else if (DialogsFragment.this.isEditModel) {
                    int i2 = position;
                    return false;
                } else {
                    RecyclerView.Adapter adapter = DialogsFragment.this.listView.getAdapter();
                    if (adapter == DialogsFragment.this.dialogsSearchAdapter) {
                        return false;
                    }
                    ArrayList<TLRPC.Dialog> dialogs = DialogsFragment.getDialogsArray(DialogsFragment.this.currentAccount, DialogsFragment.this.dialogsType, DialogsFragment.this.folderId, DialogsFragment.this.dialogsListFrozen);
                    int position2 = DialogsFragment.this.dialogsAdapter.fixPosition(position);
                    if (position2 < 0) {
                        return false;
                    } else if (position2 >= dialogs.size()) {
                        RecyclerView.Adapter adapter2 = adapter;
                        return false;
                    } else if (dialogs.get(position2) instanceof TLRPC.TL_dialogFolder) {
                        return false;
                    } else {
                        View view = ((SwipeLayout) clickView).getMainLayout();
                        if (AndroidUtilities.isTablet() || !(view instanceof FmtDialogCell)) {
                            return true;
                        }
                        FmtDialogCell cell = (FmtDialogCell) view;
                        long dialog_id = cell.getDialogId();
                        Bundle args = new Bundle();
                        int lower_part = (int) dialog_id;
                        int i3 = (int) (dialog_id >> 32);
                        int message_id = cell.getMessageId();
                        if (lower_part != 0) {
                            if (lower_part > 0) {
                                args.putInt("user_id", lower_part);
                            } else if (lower_part < 0) {
                                if (!(message_id == 0 || (chat = DialogsFragment.this.getMessagesController().getChat(Integer.valueOf(-lower_part))) == null || chat.migrated_to == null)) {
                                    args.putInt("migrated_to", lower_part);
                                    lower_part = -chat.migrated_to.channel_id;
                                }
                                args.putInt("chat_id", -lower_part);
                            }
                            if (message_id != 0) {
                                args.putInt("message_id", message_id);
                            }
                            if (DialogsFragment.this.searchString == null) {
                                if (!DialogsFragment.this.getMessagesController().checkCanOpenChat(args, DialogsFragment.this.getCurrentFragment())) {
                                    return true;
                                }
                                DialogsFragment.this.presentFragmentAsPreview(new ChatActivity(args));
                                return true;
                            } else if (DialogsFragment.this.getMessagesController().checkCanOpenChat(args, DialogsFragment.this.getCurrentFragment())) {
                                RecyclerView.Adapter adapter3 = adapter;
                                DialogsFragment.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                DialogsFragment.this.presentFragmentAsPreview(new ChatActivity(args));
                                return true;
                            } else {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }

            public void onLongClickRelease() {
                DialogsFragment.this.finishPreviewFragment();
            }

            public void onMove(float dx, float dy) {
                DialogsFragment.this.movePreviewFragment(dy);
            }
        });
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && DialogsFragment.this.searching && DialogsFragment.this.searchWas) {
                    AndroidUtilities.hideKeyboard(DialogsFragment.this.getParentActivity().getCurrentFocus());
                }
                if (DialogsFragment.this.waitingForScrollFinished && newState == 0) {
                    boolean unused = DialogsFragment.this.waitingForScrollFinished = false;
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean fromCache;
                int firstVisibleItem = DialogsFragment.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = Math.abs(DialogsFragment.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                DialogsFragment.this.dialogsItemAnimator.onListScroll(-dy);
                if (!DialogsFragment.this.searching || !DialogsFragment.this.searchWas) {
                    if (visibleItemCount > 0 && DialogsFragment.this.layoutManager.findLastVisibleItemPosition() >= DialogsFragment.getDialogsArray(DialogsFragment.this.currentAccount, DialogsFragment.this.dialogsType, DialogsFragment.this.folderId, DialogsFragment.this.dialogsListFrozen).size() - 10 && ((!DialogsFragment.this.getMessagesController().isDialogsEndReached(DialogsFragment.this.folderId)) || !DialogsFragment.this.getMessagesController().isServerDialogsEndReached(DialogsFragment.this.folderId))) {
                        AndroidUtilities.runOnUIThread(new Runnable(fromCache) {
                            private final /* synthetic */ boolean f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                DialogsFragment.AnonymousClass9.this.lambda$onScrolled$0$DialogsFragment$9(this.f$1);
                            }
                        });
                    }
                    boolean hasHiddenArchive = DialogsFragment.this.hasHiddenArchive();
                    int off = recyclerView.computeVerticalScrollOffset();
                    DialogsFragment.this.divider.setVisibility(off > AndroidUtilities.dp(55.0f) ? 0 : 8);
                    if (off < 0) {
                        return;
                    }
                    if (!hasHiddenArchive || firstVisibleItem == 1) {
                        DialogsFragment.this.searchLayout.setScrollY(Math.min(off, AndroidUtilities.dp(55.0f)));
                        return;
                    }
                    int m = off - AndroidUtilities.dp(55.0f);
                    if (m >= 0) {
                        DialogsFragment.this.searchLayout.setScrollY(Math.min(m, AndroidUtilities.dp(55.0f)));
                    } else {
                        DialogsFragment.this.searchLayout.setScrollY(0);
                    }
                } else if (visibleItemCount > 0 && DialogsFragment.this.layoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !DialogsFragment.this.dialogsSearchAdapter.isMessagesSearchEndReached()) {
                    DialogsFragment.this.dialogsSearchAdapter.loadMoreSearchMessages();
                }
            }

            public /* synthetic */ void lambda$onScrolled$0$DialogsFragment$9(boolean fromCache) {
                DialogsFragment.this.getMessagesController().loadDialogs(DialogsFragment.this.folderId, -1, 100, fromCache);
            }
        });
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        this.notificationsSettingsSP = notificationsSettings;
        notificationsSettings.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
                DialogsFragment.this.onSharedPreferenceChanged(sharedPreferences, str);
            }
        });
    }

    public /* synthetic */ void lambda$initListener$6$DialogsFragment(View clickView, int position) {
        long dialog_id;
        TLRPC.Chat chat;
        int i = position;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && recyclerListView.getAdapter() != null && getParentActivity() != null) {
            int message_id = 0;
            boolean isGlobalSearch = false;
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            FmtDialogsAdapter fmtDialogsAdapter = this.dialogsAdapter;
            if (adapter == fmtDialogsAdapter) {
                TLObject object = fmtDialogsAdapter.getItem(i);
                if (object instanceof TLRPC.User) {
                    dialog_id = (long) ((TLRPC.User) object).id;
                } else if (object instanceof TLRPC.Dialog) {
                    TLRPC.Dialog dialog = (TLRPC.Dialog) object;
                    if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                        dialog_id = dialog.id;
                        if (this.isEditModel) {
                            showOrUpdateActionMode(dialog, ((SwipeLayout) clickView).getMainLayout());
                            return;
                        } else if (this.dialogsType == 9) {
                            getMessagesController().dialogsUnreadOnly.remove(dialog);
                            this.dialogsAdapter.notifyItemRemoved(i);
                        } else {
                            getMessagesController().dialogsUnreadOnly.remove(dialog);
                        }
                    } else if (!this.actionBar.isActionModeShowed()) {
                        Bundle args = new Bundle();
                        args.putInt("folderId", ((TLRPC.TL_dialogFolder) dialog).folder.id);
                        presentFragment(new DialogsActivity(args));
                        return;
                    } else {
                        return;
                    }
                } else if (object instanceof TLRPC.TL_recentMeUrlChat) {
                    dialog_id = (long) (-((TLRPC.TL_recentMeUrlChat) object).chat_id);
                } else if (object instanceof TLRPC.TL_recentMeUrlUser) {
                    dialog_id = (long) ((TLRPC.TL_recentMeUrlUser) object).user_id;
                } else if (object instanceof TLRPC.TL_recentMeUrlChatInvite) {
                    TLRPC.TL_recentMeUrlChatInvite chatInvite = (TLRPC.TL_recentMeUrlChatInvite) object;
                    TLRPC.ChatInvite invite = chatInvite.chat_invite;
                    if ((invite.chat == null && (!invite.channel || invite.megagroup)) || (invite.chat != null && (!ChatObject.isChannel(invite.chat) || invite.chat.megagroup))) {
                        String hash = chatInvite.url;
                        int index = hash.indexOf(47);
                        if (index > 0) {
                            hash = hash.substring(index + 1);
                        }
                        showDialog(new JoinGroupAlert(getParentActivity(), invite, hash, getCurrentFragment()));
                        return;
                    } else if (invite.chat != null) {
                        dialog_id = (long) (-invite.chat.id);
                    } else {
                        return;
                    }
                } else if (object instanceof TLRPC.TL_recentMeUrlStickerSet) {
                    TLRPC.StickerSet stickerSet = ((TLRPC.TL_recentMeUrlStickerSet) object).set.set;
                    TLRPC.TL_inputStickerSetID set = new TLRPC.TL_inputStickerSetID();
                    set.id = stickerSet.id;
                    set.access_hash = stickerSet.access_hash;
                    StickersAlert stickersAlert = r10;
                    StickersAlert stickersAlert2 = new StickersAlert(getParentActivity(), getCurrentFragment(), set, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
                    showDialog(stickersAlert);
                    return;
                } else if (!(object instanceof TLRPC.TL_recentMeUrlUnknown)) {
                    return;
                } else {
                    return;
                }
            } else {
                DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
                if (adapter == dialogsSearchAdapter2) {
                    Object obj = dialogsSearchAdapter2.getItem(i);
                    isGlobalSearch = this.dialogsSearchAdapter.isGlobalSearch(i);
                    if (obj instanceof TLRPC.User) {
                        long dialog_id2 = (long) ((TLRPC.User) obj).id;
                        this.searchDialogId = dialog_id2;
                        this.searchObject = (TLRPC.User) obj;
                        dialog_id = dialog_id2;
                    } else if (obj instanceof TLRPC.Chat) {
                        long dialog_id3 = (long) (-((TLRPC.Chat) obj).id);
                        this.searchDialogId = dialog_id3;
                        this.searchObject = (TLRPC.Chat) obj;
                        dialog_id = dialog_id3;
                    } else if (obj instanceof TLRPC.EncryptedChat) {
                        long dialog_id4 = ((long) ((TLRPC.EncryptedChat) obj).id) << 32;
                        this.searchDialogId = dialog_id4;
                        this.searchObject = (TLRPC.EncryptedChat) obj;
                        dialog_id = dialog_id4;
                    } else if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        long dialog_id5 = messageObject.getDialogId();
                        message_id = messageObject.getId();
                        DialogsSearchAdapter dialogsSearchAdapter3 = this.dialogsSearchAdapter;
                        dialogsSearchAdapter3.addHashtagsFromMessage(dialogsSearchAdapter3.getLastSearchString());
                        dialog_id = dialog_id5;
                    } else {
                        if (obj instanceof String) {
                            String str = (String) obj;
                            if (this.dialogsSearchAdapter.isHashtagSearch()) {
                                this.searchView.openSearchField(str);
                            } else if (!str.equals("section")) {
                                NewContactActivity activity = new NewContactActivity();
                                activity.setInitialPhoneNumber(str);
                                presentFragment(activity);
                            }
                        }
                        dialog_id = 0;
                    }
                } else {
                    dialog_id = 0;
                }
            }
            if (dialog_id != 0) {
                Bundle args2 = new Bundle();
                int lower_part = (int) dialog_id;
                int high_id = (int) (dialog_id >> 32);
                if (lower_part == 0) {
                    args2.putInt("enc_id", high_id);
                } else if (lower_part > 0) {
                    args2.putInt("user_id", lower_part);
                    if (this.searching || this.searchWas) {
                        TLRPC.User user = getMessagesController().getUser(Integer.valueOf(lower_part));
                        if (!user.contact && !user.bot) {
                            getMessagesController();
                            if (!MessagesController.isSupportUser(user)) {
                                presentFragment(new AddContactsInfoActivity((Bundle) null, user));
                                return;
                            }
                        }
                    }
                } else if (lower_part < 0) {
                    if (!(message_id == 0 || (chat = getMessagesController().getChat(Integer.valueOf(-lower_part))) == null || chat.migrated_to == null)) {
                        args2.putInt("migrated_to", lower_part);
                        lower_part = -chat.migrated_to.channel_id;
                    }
                    args2.putInt("chat_id", -lower_part);
                }
                if (message_id != 0) {
                    args2.putInt("message_id", message_id);
                } else if (!isGlobalSearch) {
                    closeSearch();
                } else {
                    TLObject tLObject = this.searchObject;
                    if (tLObject != null) {
                        this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                        this.searchObject = null;
                    }
                }
                if (AndroidUtilities.isTablet()) {
                    if (this.openedDialogId != dialog_id || adapter == this.dialogsSearchAdapter) {
                        FmtDialogsAdapter fmtDialogsAdapter2 = this.dialogsAdapter;
                        if (fmtDialogsAdapter2 != null) {
                            this.openedDialogId = dialog_id;
                            fmtDialogsAdapter2.setOpenedDialogId(dialog_id);
                            updateVisibleRows(512);
                        }
                    } else {
                        return;
                    }
                }
                if (this.searchString != null) {
                    if (getMessagesController().checkCanOpenChat(args2, getCurrentFragment())) {
                        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        presentFragment(new ChatActivity(args2));
                    }
                } else if (getMessagesController().checkCanOpenChat(args2, getCurrentFragment())) {
                    presentFragment(new ChatActivity(args2));
                }
            }
        }
    }

    private void initData() {
        if (this.searchString == null) {
            AnonymousClass10 r0 = new FmtDialogsAdapter(this.context, this.dialogsType, this.folderId) {
                public void notifyDataSetChanged() {
                    int unused = DialogsFragment.this.lastItemsCount = getItemCount();
                    super.notifyDataSetChanged();
                }
            };
            this.dialogsAdapter = r0;
            r0.setDelegate(new FmtDialogsAdapter.FmtDialogDelegate() {
                public void onItemMenuClick(boolean left, int index, long dialog_id, int i) {
                    DialogsFragment.this.performMenuClick(left, index, dialog_id, i);
                }
            });
            if (AndroidUtilities.isTablet()) {
                long j = this.openedDialogId;
                if (j != 0) {
                    this.dialogsAdapter.setOpenedDialogId(j);
                }
            }
            this.listView.setAdapter(this.dialogsAdapter);
        }
        int type = 1;
        if (this.searchString != null) {
            type = 2;
        }
        DialogsSearchAdapter dialogsSearchAdapter2 = new DialogsSearchAdapter(this.context, type, 0, AndroidUtilities.dp(20.0f));
        this.dialogsSearchAdapter = dialogsSearchAdapter2;
        dialogsSearchAdapter2.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate() {
            public void searchStateChanged(boolean search) {
                if (DialogsFragment.this.searching && DialogsFragment.this.searchWas && DialogsFragment.this.searchEmptyView != null) {
                    if (search) {
                        DialogsFragment.this.searchEmptyView.showProgress();
                    } else {
                        DialogsFragment.this.searchEmptyView.showTextView();
                    }
                }
            }

            public void didPressedOnSubDialog(long did) {
                int lower_id = (int) did;
                Bundle args = new Bundle();
                if (lower_id > 0) {
                    args.putInt("user_id", lower_id);
                } else {
                    args.putInt("chat_id", -lower_id);
                }
                DialogsFragment.this.closeSearch();
                if (AndroidUtilities.isTablet() && DialogsFragment.this.dialogsAdapter != null) {
                    DialogsFragment.this.dialogsAdapter.setOpenedDialogId(DialogsFragment.this.openedDialogId = did);
                    DialogsFragment.this.updateVisibleRows(512);
                }
                if (DialogsFragment.this.searchString == null) {
                    TLRPC.User user = DialogsFragment.this.getMessagesController().getUser(Integer.valueOf(lower_id));
                    if (user != null && !user.contact && !user.bot) {
                        DialogsFragment.this.getMessagesController();
                        if (!MessagesController.isSupportUser(user)) {
                            DialogsFragment.this.presentFragment(new AddContactsInfoActivity((Bundle) null, user));
                            return;
                        }
                    }
                    if (DialogsFragment.this.getMessagesController().checkCanOpenChat(args, DialogsFragment.this.getCurrentFragment())) {
                        DialogsFragment.this.presentFragment(new ChatActivity(args));
                    }
                } else if (DialogsFragment.this.getMessagesController().checkCanOpenChat(args, DialogsFragment.this.getCurrentFragment())) {
                    DialogsFragment.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    DialogsFragment.this.presentFragment(new ChatActivity(args));
                }
            }

            public void needRemoveHint(int did) {
                TLRPC.User user;
                if (DialogsFragment.this.getParentActivity() != null && (user = DialogsFragment.this.getMessagesController().getUser(Integer.valueOf(did))) != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) DialogsFragment.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("ChatHintsDeleteAlertTitle", R.string.ChatHintsDeleteAlertTitle));
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatHintsDeleteAlert", R.string.ChatHintsDeleteAlert, ContactsController.formatName(user.first_name, user.last_name))));
                    builder.setPositiveButton(LocaleController.getString("StickersRemove", R.string.StickersRemove), new DialogInterface.OnClickListener(did) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            DialogsFragment.AnonymousClass12.this.lambda$needRemoveHint$0$DialogsFragment$12(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    AlertDialog dialog = builder.create();
                    DialogsFragment.this.showDialog(dialog);
                    TextView button = (TextView) dialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                    }
                }
            }

            public /* synthetic */ void lambda$needRemoveHint$0$DialogsFragment$12(int did, DialogInterface dialogInterface, int i) {
                DialogsFragment.this.getMediaDataController().removePeer(did);
            }

            public void needClearList() {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) DialogsFragment.this.getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", R.string.ClearSearchAlertTitle));
                builder.setMessage(LocaleController.getString("ClearSearchAlert", R.string.ClearSearchAlert));
                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsFragment.AnonymousClass12.this.lambda$needClearList$1$DialogsFragment$12(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder.create();
                DialogsFragment.this.showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }

            public /* synthetic */ void lambda$needClearList$1$DialogsFragment$12(DialogInterface dialogInterface, int i) {
                if (DialogsFragment.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    DialogsFragment.this.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    DialogsFragment.this.dialogsSearchAdapter.clearRecentHashtags();
                }
            }
        });
        this.listView.setEmptyView(this.folderId == 0 ? this.progressView : null);
        String str = this.searchString;
        if (str != null) {
            this.searchView.openSearchField(str);
        }
        for (int a = 0; a < 2; a++) {
            this.undoView[a] = new UndoView(this.context) {
                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    if (this == DialogsFragment.this.undoView[0] && DialogsFragment.this.undoView[1].getVisibility() != 0) {
                        getMeasuredHeight();
                        AndroidUtilities.dp(8.0f);
                    }
                }

                /* access modifiers changed from: protected */
                public boolean canUndo() {
                    return !DialogsFragment.this.dialogsItemAnimator.isRunning();
                }
            };
            this.contentView.addView(this.undoView[a], LayoutHelper.createFrame(-1.0f, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        if (this.folderId != 0) {
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultArchived));
            this.listView.setGlowColor(Theme.getColor(Theme.key_actionBarDefaultArchived));
            this.actionBar.setTitleColor(Theme.getColor(Theme.key_actionBarDefaultArchivedTitle));
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultArchivedIcon), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultArchivedSelector), false);
            this.actionBar.setSearchTextColor(Theme.getColor(Theme.key_actionBarDefaultArchivedSearch), false);
            this.actionBar.setSearchTextColor(Theme.getColor(Theme.key_actionBarDefaultArchivedSearchPlaceholder), true);
        }
    }

    /* access modifiers changed from: private */
    public void hideTitle() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this.containerLayout, "translationY", new float[]{0.0f, (float) (-ActionBar.getCurrentActionBarHeight())});
        animator.setDuration(300);
        animator.start();
        this.actionBar.setVisibility(4);
    }

    /* access modifiers changed from: private */
    public void showTitle() {
        ObjectAnimator.ofFloat(this.containerLayout, "translationY", new float[]{(float) (-ActionBar.getCurrentActionBarHeight()), 0.0f}).start();
        this.actionBar.setVisibility(0);
    }

    public NotificationCenter getNotificationCenter() {
        return getAccountInstance().getNotificationCenter();
    }

    private boolean waitingForDialogsAnimationEnd() {
        return (!this.dialogsItemAnimator.isRunning() && this.dialogRemoveFinished == 0 && this.dialogInsertFinished == 0 && this.dialogChangeFinished == 0) ? false : true;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    private UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            if (this.undoView[0].getParent() != null) {
                ((ViewGroup) this.undoView[0].getParent()).removeView(this.undoView[0]);
            }
            this.contentView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int i = id;
        Object[] objArr = args;
        if (i == NotificationCenter.dialogsNeedReload) {
            if (!this.dialogsListFrozen) {
                FmtDialogsAdapter fmtDialogsAdapter = this.dialogsAdapter;
                if (fmtDialogsAdapter != null) {
                    if (fmtDialogsAdapter.isDataSetChanged() || objArr.length > 0) {
                        this.dialogsAdapter.notifyDataSetChanged();
                    } else {
                        updateVisibleRows(2048);
                    }
                }
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null) {
                    try {
                        RadialProgressView radialProgressView = null;
                        if (recyclerListView.getAdapter() == this.dialogsAdapter) {
                            this.searchEmptyView.setVisibility(8);
                            RecyclerListView recyclerListView2 = this.listView;
                            if (this.folderId == 0) {
                                radialProgressView = this.progressView;
                            }
                            recyclerListView2.setEmptyView(radialProgressView);
                        } else {
                            if (!this.searching || !this.searchWas) {
                                this.searchEmptyView.setVisibility(8);
                                this.listView.setEmptyView((View) null);
                            } else {
                                this.listView.setEmptyView(this.searchEmptyView);
                            }
                            this.progressView.setVisibility(8);
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                FmtConsumDelegate fmtConsumDelegate = this.delegate;
                if (fmtConsumDelegate != null) {
                    fmtConsumDelegate.changeUnreadCount(getUnreadCount());
                }
            }
        } else if (i == NotificationCenter.emojiDidLoad) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.closeSearchByActiveAction) {
            if (this.actionBar != null) {
                this.searchView.closeSearchField();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            updateVisibleRows(((Integer) objArr[0]).intValue());
            FmtConsumDelegate fmtConsumDelegate2 = this.delegate;
            if (fmtConsumDelegate2 != null) {
                fmtConsumDelegate2.changeUnreadCount(getUnreadCount());
            }
        } else if (i == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.contactsDidLoad) {
            if (!this.dialogsListFrozen) {
                if (getMessagesController().getDialogs(this.folderId).isEmpty()) {
                    FmtDialogsAdapter fmtDialogsAdapter2 = this.dialogsAdapter;
                    if (fmtDialogsAdapter2 != null) {
                        fmtDialogsAdapter2.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                updateVisibleRows(0);
            }
        } else if (i == NotificationCenter.openedChatChanged) {
            if (AndroidUtilities.isTablet()) {
                boolean close = ((Boolean) objArr[1]).booleanValue();
                long dialog_id = ((Long) objArr[0]).longValue();
                if (!close) {
                    this.openedDialogId = dialog_id;
                } else if (dialog_id == this.openedDialogId) {
                    this.openedDialogId = 0;
                }
                FmtDialogsAdapter fmtDialogsAdapter3 = this.dialogsAdapter;
                if (fmtDialogsAdapter3 != null) {
                    fmtDialogsAdapter3.setOpenedDialogId(this.openedDialogId);
                }
                updateVisibleRows(512);
            }
        } else if (i == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
            FmtConsumDelegate fmtConsumDelegate3 = this.delegate;
            if (fmtConsumDelegate3 != null) {
                fmtConsumDelegate3.changeUnreadCount(getUnreadCount());
            }
        } else if (i == NotificationCenter.messageReceivedByAck || i == NotificationCenter.messageReceivedByServer || i == NotificationCenter.messageSendError) {
            updateVisibleRows(4096);
        } else if (i == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } else if (i == NotificationCenter.needReloadRecentDialogsSearch) {
            DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
            if (dialogsSearchAdapter2 != null) {
                dialogsSearchAdapter2.loadRecentSearch();
            }
        } else if (i == NotificationCenter.replyMessagesDidLoad) {
            updateVisibleRows(32768);
        } else if (i == NotificationCenter.reloadHints) {
            DialogsSearchAdapter dialogsSearchAdapter3 = this.dialogsSearchAdapter;
            if (dialogsSearchAdapter3 != null) {
                dialogsSearchAdapter3.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.didUpdateConnectionState) {
            int state = AccountInstance.getInstance(account).getConnectionsManager().getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
            }
        } else if (i != NotificationCenter.dialogsUnreadCounterChanged) {
            if (i == NotificationCenter.needDeleteDialog) {
                if (this.fragmentView != null && !this.isPaused) {
                    long dialogId = ((Long) objArr[0]).longValue();
                    TLRPC.User user = (TLRPC.User) objArr[1];
                    Runnable deleteRunnable = r1;
                    $$Lambda$DialogsFragment$Eub4Odsb8tdjBmeGz1bc7CNXNE r1 = new Runnable((TLRPC.Chat) objArr[2], dialogId, ((Boolean) objArr[3]).booleanValue()) {
                        private final /* synthetic */ TLRPC.Chat f$1;
                        private final /* synthetic */ long f$2;
                        private final /* synthetic */ boolean f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r5;
                        }

                        public final void run() {
                            DialogsFragment.this.lambda$didReceivedNotification$7$DialogsFragment(this.f$1, this.f$2, this.f$3);
                        }
                    };
                    if (this.undoView[0] != null) {
                        getUndoView().showWithAction(dialogId, 1, deleteRunnable);
                    } else {
                        deleteRunnable.run();
                    }
                }
            } else if (i == NotificationCenter.folderBecomeEmpty) {
                int intValue = ((Integer) objArr[0]).intValue();
                int i2 = this.folderId;
            } else if (i != NotificationCenter.pushRemoteOpenChat) {
            } else {
                if (getActivity() == null) {
                    new Thread(new Runnable(objArr) {
                        private final /* synthetic */ Object[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            DialogsFragment.this.lambda$didReceivedNotification$9$DialogsFragment(this.f$1);
                        }
                    }).start();
                } else {
                    jumpToChatWindow(Integer.parseInt(String.valueOf(objArr[0])));
                }
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$7$DialogsFragment(TLRPC.Chat chat, long dialogId, boolean revoke) {
        if (chat == null) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else {
            getMessagesController().deleteUserFromChat((int) (-dialogId), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null, false, revoke);
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    public /* synthetic */ void lambda$didReceivedNotification$9$DialogsFragment(Object[] args) {
        while (getActivity() == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(args) {
            private final /* synthetic */ Object[] f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DialogsFragment.this.lambda$null$8$DialogsFragment(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$8$DialogsFragment(Object[] args) {
        jumpToChatWindow(Integer.parseInt(String.valueOf(args[0])));
    }

    private void jumpToChatWindow(int dialog_id) {
        Bundle args = new Bundle();
        int lower_part = dialog_id;
        int high_id = dialog_id >> 32;
        if (lower_part == 0) {
            args.putInt("enc_id", high_id);
        } else if (lower_part > 0) {
            args.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args.putInt("chat_id", -lower_part);
        }
        if (getMessagesController().checkCanOpenChat(args, getCurrentFragment())) {
            presentFragment(new ChatActivity(args));
        }
    }

    private void setDialogsListFrozen(boolean frozen) {
        if (this.dialogsListFrozen != frozen) {
            if (frozen) {
                frozenDialogsList = new ArrayList<>(getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false));
            } else {
                frozenDialogsList = null;
            }
            this.dialogsListFrozen = frozen;
            this.dialogsAdapter.setDialogsListFrozen(frozen);
            if (!frozen) {
                this.dialogsAdapter.notifyDataSetChanged();
            }
        }
    }

    private int getUnreadCount() {
        int count = 0;
        ArrayList<TLRPC.Dialog> dialogsArray = getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
        if (dialogsArray == null) {
            return 0;
        }
        NotificationsController controller = getNotificationsController();
        Iterator<TLRPC.Dialog> it = dialogsArray.iterator();
        while (it.hasNext()) {
            TLRPC.Dialog dialog = it.next();
            if (controller.showBadgeNumber) {
                if (controller.showBadgeMessages) {
                    if (controller.showBadgeMuted || dialog.notify_settings == null || (!dialog.notify_settings.silent && dialog.notify_settings.mute_until <= getConnectionsManager().getCurrentTime())) {
                        count += dialog.unread_count;
                    }
                } else if ((controller.showBadgeMuted || dialog.notify_settings == null || (!dialog.notify_settings.silent && dialog.notify_settings.mute_until <= getConnectionsManager().getCurrentTime())) && dialog.unread_count != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public static ArrayList<TLRPC.Dialog> getDialogsArray(int currentAccount, int dialogsType2, int folderId2, boolean frozen) {
        ArrayList<TLRPC.Dialog> arrayList;
        if (frozen && (arrayList = frozenDialogsList) != null) {
            return arrayList;
        }
        MessagesController messagesController = AccountInstance.getInstance(currentAccount).getMessagesController();
        if (dialogsType2 == 0) {
            return messagesController.getDialogs(folderId2);
        }
        if (dialogsType2 == 1) {
            return messagesController.dialogsServerOnly;
        }
        if (dialogsType2 == 2) {
            return messagesController.dialogsCanAddUsers;
        }
        if (dialogsType2 == 3) {
            return messagesController.dialogsForward;
        }
        if (dialogsType2 == 4) {
            return messagesController.dialogsUsersOnly;
        }
        if (dialogsType2 == 5) {
            return messagesController.dialogsChannelsOnly;
        }
        if (dialogsType2 == 6) {
            return messagesController.dialogsGroupsOnly;
        }
        if (dialogsType2 == 9) {
            return messagesController.dialogsUnreadOnly;
        }
        if (dialogsType2 == 7) {
            ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>();
            Iterator<TLRPC.Dialog> it = messagesController.dialogsForward.iterator();
            while (it.hasNext()) {
                TLRPC.Dialog dialog = it.next();
                long dialogId = dialog.id;
                if (!(dialogId == 0 || ((int) dialogId) == 0)) {
                    dialogs.add(dialog);
                }
            }
            return dialogs;
        } else if (dialogsType2 != 8) {
            return null;
        } else {
            ArrayList<TLRPC.Dialog> dialogs2 = new ArrayList<>();
            Iterator<TLRPC.Dialog> it2 = messagesController.getDialogs(folderId2).iterator();
            while (it2.hasNext()) {
                TLRPC.Dialog dialog2 = it2.next();
                if (dialog2.unread_mentions_count != 0 || dialog2.unread_count != 0) {
                    dialogs2.add(dialog2);
                }
            }
            return dialogs2;
        }
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        recyclerView.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
        this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
    }

    /* access modifiers changed from: private */
    public void updatePasscodeButton() {
        if (this.passcodeItem != null) {
            if (SharedConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
                return;
            }
            this.passcodeItem.setVisibility(0);
            if (SharedConfig.appLocked) {
                this.passcodeItem.setIcon((int) R.drawable.lock_close);
                this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeUnlock", R.string.AccDescrPasscodeUnlock));
                return;
            }
            this.passcodeItem.setIcon((int) R.drawable.lock_open);
            this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeLock", R.string.AccDescrPasscodeLock));
        }
    }

    private void updateDialogIndices() {
        int index;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && recyclerListView.getAdapter() == this.dialogsAdapter) {
            ArrayList<TLRPC.Dialog> dialogs = getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof SwipeLayout) {
                    FmtDialogCell dialogCell = (FmtDialogCell) ((SwipeLayout) child).getMainLayout();
                    TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogCell.getDialogId());
                    if (dialog != null && (index = dialogs.indexOf(dialog)) >= 0) {
                        dialogCell.setDialogIndex(index);
                    }
                }
            }
        }
    }

    private void updateSwipeLayout(SwipeLayout swipeLayout, long dialog_id) {
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialog_id);
        if (dialog != null && !(dialog instanceof TLRPC.TL_dialogFolder)) {
            swipeLayout.setTextAtIndex(true, 1, LocaleController.getString(dialog.pinned ? R.string.UnpinFromTop : R.string.PinToTop));
            swipeLayout.setIconAtIndex(true, 1, dialog.pinned ? R.drawable.msg_unpin : R.drawable.msg_pin);
            boolean isDialogMuted = MessagesController.getInstance(UserConfig.selectedAccount).isDialogMuted(dialog.id);
            swipeLayout.setTextAtIndex(true, 0, LocaleController.getString(isDialogMuted ? R.string.ChatsUnmute : R.string.ChatsMute));
            swipeLayout.setIconAtIndex(true, 0, isDialogMuted ? R.drawable.msg_unmute : R.drawable.msg_mute);
        }
    }

    /* access modifiers changed from: private */
    public void updateVisibleRows(int mask) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && !this.dialogsListFrozen) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof SwipeLayout) {
                    if (this.listView.getAdapter() != this.dialogsSearchAdapter) {
                        SwipeLayout swipeLayout = (SwipeLayout) child;
                        FmtDialogCell cell = (FmtDialogCell) swipeLayout.getMainLayout();
                        boolean z = true;
                        if ((131072 & mask) != 0) {
                            cell.onReorderStateChanged(this.actionBar.isActionModeShowed(), true);
                        }
                        if ((65536 & mask) != 0) {
                            if ((mask & 8192) == 0) {
                                z = false;
                            }
                            cell.setChecked(false, z);
                        } else {
                            if ((mask & 2048) != 0) {
                                cell.checkCurrentDialogIndex(this.dialogsListFrozen);
                                if (AndroidUtilities.isTablet()) {
                                    if (cell.getDialogId() != this.openedDialogId) {
                                        z = false;
                                    }
                                    cell.setDialogSelected(z);
                                }
                                updateSwipeLayout(swipeLayout, cell.getDialogId());
                            } else if ((mask & 512) == 0) {
                                cell.update(mask);
                                updateSwipeLayout(swipeLayout, cell.getDialogId());
                            } else if (AndroidUtilities.isTablet()) {
                                if (cell.getDialogId() != this.openedDialogId) {
                                    z = false;
                                }
                                cell.setDialogSelected(z);
                            }
                            ArrayList<Long> selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
                            if (selectedDialogs != null) {
                                cell.setChecked(selectedDialogs.contains(Long.valueOf(cell.getDialogId())), false);
                            }
                        }
                    }
                } else if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                } else if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(mask);
                } else if (child instanceof RecyclerListView) {
                    RecyclerListView innerListView = (RecyclerListView) child;
                    int count2 = innerListView.getChildCount();
                    for (int b = 0; b < count2; b++) {
                        View child2 = innerListView.getChildAt(b);
                        if (child2 instanceof HintDialogCell) {
                            ((HintDialogCell) child2).update(mask);
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean hasHiddenArchive() {
        return this.listView.getAdapter() == this.dialogsAdapter && this.folderId == 0 && getMessagesController().hasHiddenArchive();
    }

    public void setSearchString(String string) {
        this.searchString = string;
    }

    /* access modifiers changed from: private */
    public void onDialogAnimationFinished() {
        this.dialogRemoveFinished = 0;
        this.dialogInsertFinished = 0;
        this.dialogChangeFinished = 0;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                DialogsFragment.this.lambda$onDialogAnimationFinished$10$DialogsFragment();
            }
        });
    }

    public /* synthetic */ void lambda$onDialogAnimationFinished$10$DialogsFragment() {
        if (this.folderId != 0 && frozenDialogsList.isEmpty()) {
            this.listView.setEmptyView((View) null);
            this.progressView.setVisibility(4);
        }
        setDialogsListFrozen(false);
        updateDialogIndices();
    }

    private boolean validateSlowModeDialog(long dialogId) {
        int lowerId;
        TLRPC.Chat chat;
        ChatActivityEnterView chatActivityEnterView;
        if ((this.messagesCount <= 1 && ((chatActivityEnterView = this.commentView) == null || chatActivityEnterView.getVisibility() != 0 || TextUtils.isEmpty(this.commentView.getFieldText()))) || (lowerId = (int) dialogId) >= 0 || (chat = getMessagesController().getChat(Integer.valueOf(-lowerId))) == null || ChatObject.hasAdminRights(chat) || !chat.slowmode_enabled) {
            return true;
        }
        AlertsCreator.showSimpleAlert(getCurrentFragment(), LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError));
        return false;
    }

    public void movePreviewFragment(float dy) {
        this.parentLayout.movePreviewFragment(dy);
    }

    public void finishPreviewFragment() {
        this.parentLayout.finishPreviewFragment();
    }

    public boolean presentFragmentAsPreview(BaseFragment fragment) {
        return this.parentLayout != null && this.parentLayout.presentFragmentAsPreview(fragment);
    }

    /* access modifiers changed from: private */
    public void performMenuClick(boolean left, int index, long dialog_id, int position) {
        int i;
        TLRPC.Chat chat;
        TLRPC.User user;
        String str;
        int i2;
        int maxPinnedCount;
        View view;
        boolean z = left;
        int i3 = index;
        int i4 = position;
        if (getParentActivity() != null) {
            int undoAction = 3;
            int i5 = 4;
            if (z) {
                if (i3 == 0) {
                    TLRPC.Dialog dialog = (TLRPC.Dialog) this.dialogsAdapter.getItem(i4);
                    View view2 = this.layoutManager.findViewByPosition(i4);
                    if (getMessagesController().isDialogMuted(dialog.id)) {
                        getNotificationsController().setDialogNotificationsSettings(dialog.id, 4);
                        if (view2 instanceof SwipeLayout) {
                            SwipeLayout swipeLayout = (SwipeLayout) view2;
                            swipeLayout.setTextAtIndex(z, i3, LocaleController.getString("ChatsMute", R.string.ChatsMute));
                            swipeLayout.setIconAtIndex(z, i3, R.drawable.msg_mute);
                            swipeLayout.setItemState(2, true);
                            return;
                        }
                        return;
                    }
                    NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(dialog.id, 3);
                    ((SwipeLayout) view2).setItemState(2, true);
                } else if (i3 == 1) {
                    View view3 = this.layoutManager.findViewByPosition(i4);
                    SwipeLayout swipeLayout2 = (SwipeLayout) view3;
                    boolean scrollToTop = false;
                    TLRPC.Dialog dialog2 = (TLRPC.Dialog) this.dialogsAdapter.getItem(i4);
                    if (!dialog2.pinned) {
                        int pinnedCount = 0;
                        int pinnedSecretCount = 0;
                        int newPinnedCount = 0;
                        int newPinnedSecretCount = 0;
                        ArrayList<TLRPC.Dialog> dialogs = getMessagesController().getDialogs(this.folderId);
                        int a = 0;
                        int N = dialogs.size();
                        while (true) {
                            if (a >= N) {
                                View view4 = view3;
                                break;
                            }
                            TLRPC.Dialog itemDialog = dialogs.get(a);
                            ArrayList<TLRPC.Dialog> dialogs2 = dialogs;
                            if (!(itemDialog instanceof TLRPC.TL_dialogFolder)) {
                                view = view3;
                                int lower_id = (int) dialog2.id;
                                if (!itemDialog.pinned) {
                                    break;
                                } else if (lower_id == 0) {
                                    pinnedSecretCount++;
                                } else {
                                    pinnedCount++;
                                }
                            } else {
                                view = view3;
                            }
                            a++;
                            dialogs = dialogs2;
                            view3 = view;
                        }
                        if (((int) dialog2.id) == 0) {
                            newPinnedSecretCount = 0 + 1;
                        } else {
                            newPinnedCount = 0 + 1;
                        }
                        if (this.folderId != 0) {
                            maxPinnedCount = getMessagesController().maxFolderPinnedDialogsCount;
                        } else {
                            maxPinnedCount = getMessagesController().maxPinnedDialogsCount;
                        }
                        if (newPinnedSecretCount + pinnedSecretCount > maxPinnedCount || newPinnedCount + pinnedCount > maxPinnedCount) {
                            int i6 = maxPinnedCount;
                            AlertsCreator.showSimpleAlert(getCurrentFragment(), LocaleController.formatString("PinToTopLimitReached", R.string.PinToTopLimitReached, LocaleController.formatPluralString("Chats", maxPinnedCount)));
                            AndroidUtilities.shakeView(this.pinItem, 2.0f, 0);
                            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
                            if (v != null) {
                                v.vibrate(200);
                                return;
                            }
                            return;
                        }
                    }
                    if (!dialog2.pinned) {
                        if (getMessagesController().pinDialog(dialog2.id, true, (TLRPC.InputPeer) null, -1)) {
                            swipeLayout2.setItemState(2, true);
                            scrollToTop = true;
                        }
                    } else if (getMessagesController().pinDialog(dialog2.id, false, (TLRPC.InputPeer) null, -1)) {
                        swipeLayout2.setItemState(2, true);
                        scrollToTop = true;
                    }
                    getMessagesController().reorderPinnedDialogs(this.folderId, (ArrayList<TLRPC.InputDialogPeer>) null, 0);
                    if (scrollToTop) {
                        this.layoutManager.scrollToPositionWithOffset(hasHiddenArchive() ? 1 : 0, AndroidUtilities.dp(55.0f));
                    }
                }
            } else if (i3 == 0) {
                TLRPC.Dialog dialog3 = (TLRPC.Dialog) this.dialogsAdapter.getItem(i4);
                TLRPC.Dialog dialog4 = getMessagesController().dialogs_dict.get(dialog_id);
                View view5 = this.layoutManager.findViewByPosition(i4);
                if (view5 instanceof SwipeLayout) {
                    SwipeLayout swipeLayout3 = (SwipeLayout) view5;
                    boolean hasUnread = dialog3.unread_count != 0 || dialog3.unread_mark;
                    if (!hasUnread) {
                        i2 = R.string.MarkAsRead;
                        str = "MarkAsRead";
                    } else {
                        i2 = R.string.MarkAsUnread;
                        str = "MarkAsUnread";
                    }
                    String text = LocaleController.getString(str, i2);
                    int icon = !hasUnread ? R.drawable.msg_markread : R.drawable.msg_markunread;
                    swipeLayout3.setTextAtIndex(z, i3, text);
                    swipeLayout3.setIconAtIndex(z, i3, icon);
                    swipeLayout3.setItemState(2, true);
                }
                if (dialog3.unread_count != 0 || dialog3.unread_mark) {
                    getMessagesController().markMentionsAsRead(dialog3.id);
                    getMessagesController().markDialogAsRead(dialog3.id, dialog3.top_message, dialog3.top_message, dialog3.last_message_date, false, 0, true, 0);
                    return;
                }
                getMessagesController().markDialogAsUnread(dialog3.id, (TLRPC.InputPeer) null, 0);
            } else {
                long j = dialog_id;
                if (i3 == 1) {
                    TLRPC.Dialog dialog5 = (TLRPC.Dialog) this.dialogsAdapter.getItem(i4);
                    int lower_id2 = (int) dialog5.id;
                    int high_id = (int) (dialog5.id >> 32);
                    if (lower_id2 == 0) {
                        TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(high_id));
                        if (encryptedChat != null) {
                            user = getMessagesController().getUser(Integer.valueOf(encryptedChat.user_id));
                            chat = null;
                        } else {
                            user = new TLRPC.TL_userEmpty();
                            chat = null;
                        }
                    } else if (lower_id2 > 0) {
                        user = getMessagesController().getUser(Integer.valueOf(lower_id2));
                        chat = null;
                    } else {
                        user = null;
                        chat = getMessagesController().getChat(Integer.valueOf(-lower_id2));
                    }
                    if (chat != null || user != null) {
                        boolean z2 = user != null && user.bot && !MessagesController.isSupportUser(user);
                        BottomDialog bottomDialog = new BottomDialog(getParentActivity());
                        bottomDialog.setTitleDivider(false);
                        bottomDialog.setDialogTextColor(getParentActivity().getResources().getColor(R.color.color_item_menu_red_f74c31));
                        bottomDialog.setCancelButtonColor(getParentActivity().getResources().getColor(R.color.color_text_black_222222));
                        bottomDialog.addDialogItem(new BottomDialog.NormalTextItem(0, LocaleController.getString("ClearHistory", R.string.ClearHistory), true));
                        bottomDialog.addDialogItem(new BottomDialog.NormalTextItem(1, LocaleController.getString("Delete", R.string.Delete), false));
                        BottomDialog bottomDialog2 = bottomDialog;
                        $$Lambda$DialogsFragment$snuhU_nQOS7MGptzS_OZUZygIOc r11 = r0;
                        TLRPC.User user2 = user;
                        $$Lambda$DialogsFragment$snuhU_nQOS7MGptzS_OZUZygIOc r0 = new BottomDialog.OnItemClickListener(chat, user, lower_id2, dialog5, bottomDialog2) {
                            private final /* synthetic */ TLRPC.Chat f$1;
                            private final /* synthetic */ TLRPC.User f$2;
                            private final /* synthetic */ int f$3;
                            private final /* synthetic */ TLRPC.Dialog f$4;
                            private final /* synthetic */ BottomDialog f$5;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r6;
                            }

                            public final void onItemClick(int i, View view) {
                                DialogsFragment.this.lambda$performMenuClick$15$DialogsFragment(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, i, view);
                            }
                        };
                        BottomDialog bottomDialog3 = bottomDialog2;
                        bottomDialog3.setOnItemClickListener(r11);
                        bottomDialog3.show();
                        bottomDialog3.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            public final void onDismiss(DialogInterface dialogInterface) {
                                SwipeLayout.this.setItemState(2, true);
                            }
                        });
                    }
                } else if (i3 == 2) {
                    ArrayList<Long> copy = new ArrayList<>();
                    copy.add(Long.valueOf(dialog_id));
                    getMessagesController().addDialogToFolder(copy, this.folderId == 0 ? 1 : 0, -1, (ArrayList<TLRPC.TL_inputFolderPeer>) null, 0);
                    if (this.folderId == 0) {
                        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                        boolean hintShowed = false;
                        if (preferences.getBoolean("archivehint_l", false) || SharedConfig.archiveHidden) {
                            hintShowed = true;
                        }
                        if (!hintShowed) {
                            i = 1;
                            preferences.edit().putBoolean("archivehint_l", true).commit();
                        } else {
                            i = 1;
                        }
                        if (hintShowed) {
                            if (copy.size() <= i) {
                                i5 = 2;
                            }
                            undoAction = i5;
                        } else if (copy.size() > i) {
                            undoAction = 5;
                        }
                        getUndoView().showWithAction(0, undoAction, (Runnable) null, new Runnable(copy) {
                            private final /* synthetic */ ArrayList f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                DialogsFragment.this.lambda$performMenuClick$17$DialogsFragment(this.f$1);
                            }
                        });
                    } else if (getMessagesController().getDialogs(this.folderId).isEmpty()) {
                        this.listView.setEmptyView((View) null);
                        this.progressView.setVisibility(4);
                    }
                    ((SwipeLayout) this.layoutManager.findViewByPosition(i4)).setItemState(2, true);
                }
            }
        }
    }

    public /* synthetic */ void lambda$performMenuClick$15$DialogsFragment(TLRPC.Chat chat, TLRPC.User finalUser, int lower_id, TLRPC.Dialog dialog, BottomDialog bottomDialog, int id, View v) {
        TLRPC.Chat chat2 = chat;
        TLRPC.Dialog dialog2 = dialog;
        int i = id;
        if (i == 0) {
            AlertsCreator.createClearOrDeleteDialogAlert(getCurrentFragment(), true, chat, finalUser, lower_id == 0, new MessagesStorage.BooleanCallback(chat, dialog) {
                private final /* synthetic */ TLRPC.Chat f$1;
                private final /* synthetic */ TLRPC.Dialog f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(boolean z) {
                    DialogsFragment.this.lambda$null$12$DialogsFragment(this.f$1, this.f$2, z);
                }
            });
            bottomDialog.dismiss();
        } else if (i == 1) {
            AlertsCreator.createClearOrDeleteDialogAlert(getCurrentFragment(), false, chat, finalUser, lower_id == 0, new MessagesStorage.BooleanCallback(dialog, chat) {
                private final /* synthetic */ TLRPC.Dialog f$1;
                private final /* synthetic */ TLRPC.Chat f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(boolean z) {
                    DialogsFragment.this.lambda$null$14$DialogsFragment(this.f$1, this.f$2, z);
                }
            });
            bottomDialog.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$12$DialogsFragment(TLRPC.Chat chat, TLRPC.Dialog dialog, boolean param) {
        if (!ChatObject.isChannel(chat) || (chat.megagroup && TextUtils.isEmpty(chat.username))) {
            getUndoView().showWithAction(dialog.id, 0, (Runnable) new Runnable(dialog, param) {
                private final /* synthetic */ TLRPC.Dialog f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DialogsFragment.this.lambda$null$11$DialogsFragment(this.f$1, this.f$2);
                }
            });
        } else {
            getMessagesController().deleteDialog(dialog.id, 2, param);
        }
    }

    public /* synthetic */ void lambda$null$11$DialogsFragment(TLRPC.Dialog dialog, boolean param) {
        getMessagesController().deleteDialog(dialog.id, 1, param);
    }

    public /* synthetic */ void lambda$null$14$DialogsFragment(TLRPC.Dialog dialog, TLRPC.Chat chat, boolean param) {
        if (this.folderId != 0 && getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false).size() == 1) {
            this.progressView.setVisibility(4);
        }
        getUndoView().showWithAction(dialog.id, 1, (Runnable) new Runnable(chat, dialog, param) {
            private final /* synthetic */ TLRPC.Chat f$1;
            private final /* synthetic */ TLRPC.Dialog f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                DialogsFragment.this.lambda$null$13$DialogsFragment(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$13$DialogsFragment(TLRPC.Chat chat, TLRPC.Dialog dialog, boolean param) {
        if (chat == null) {
            getMessagesController().deleteDialog(dialog.id, 0, param);
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(dialog.id, 0, param);
        } else {
            getMessagesController().deleteUserFromChat((int) (-dialog.id), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(dialog.id));
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    public /* synthetic */ void lambda$performMenuClick$17$DialogsFragment(ArrayList copy) {
        getMessagesController().addDialogToFolder(copy, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC.TL_inputFolderPeer>) null, 0);
    }

    public void showDeleteOrClearSheet() {
        BottomDialog bottomDialog = new BottomDialog(getParentActivity());
        bottomDialog.setDialogTextColor(getParentActivity().getResources().getColor(R.color.color_item_menu_red_f74c31));
        bottomDialog.setCancelButtonColor(getParentActivity().getResources().getColor(R.color.color_text_black_222222));
        int count = this.dialogsAdapter.getSelectedDialogs().size();
        bottomDialog.addDialogItem(new BottomDialog.NormalTextItem(0, String.format(LocaleController.getString("DeleteManyDialogs", R.string.DeleteManyDialogs), new Object[]{Integer.valueOf(count)}), true));
        bottomDialog.setOnItemClickListener(new BottomDialog.OnItemClickListener(bottomDialog) {
            private final /* synthetic */ BottomDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(int i, View view) {
                DialogsFragment.this.lambda$showDeleteOrClearSheet$18$DialogsFragment(this.f$1, i, view);
            }
        });
        bottomDialog.show();
    }

    public /* synthetic */ void lambda$showDeleteOrClearSheet$18$DialogsFragment(BottomDialog bottomDialog, int id, View v) {
        if (id == 0) {
            perfromSelectedDialogsAction(2);
            bottomDialog.dismiss();
        }
    }

    public void perfromSelectedDialogsAction(int action) {
        int count;
        ArrayList<Long> selectedDialogs;
        TLRPC.Chat chat;
        TLRPC.User user;
        int i;
        int undoAction;
        ArrayList<Long> selectedDialogs2;
        int count2;
        int i2 = action;
        if (getParentActivity() != null) {
            ArrayList<Long> selectedDialogs3 = this.dialogsAdapter.getSelectedDialogs();
            int count3 = selectedDialogs3.size();
            int i3 = 4;
            boolean hintShowed = false;
            if (count3 > 0) {
                selectedDialogs = selectedDialogs3;
                count = count3;
            } else if (i2 == 4 && (count2 = selectedDialogs2.size()) > 0) {
                updateCounters(true, false);
                selectedDialogs = this.dialogsAdapter.getAllDialogIdsList();
                count = count2;
            } else {
                return;
            }
            if (i2 == 1) {
                ArrayList<Long> copy = new ArrayList<>(selectedDialogs);
                getMessagesController().addDialogToFolder(copy, this.folderId == 0 ? 1 : 0, -1, (ArrayList<TLRPC.TL_inputFolderPeer>) null, 0);
                toggleEditModel();
                if (this.folderId == 0) {
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (preferences.getBoolean("archivehint_l", false) || SharedConfig.archiveHidden) {
                        hintShowed = true;
                    }
                    if (!hintShowed) {
                        preferences.edit().putBoolean("archivehint_l", true).commit();
                    }
                    if (hintShowed) {
                        if (copy.size() <= 1) {
                            i3 = 2;
                        }
                        undoAction = i3;
                    } else {
                        undoAction = copy.size() > 1 ? 5 : 3;
                    }
                    getUndoView().showWithAction(0, undoAction, (Runnable) null, new Runnable(copy) {
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            DialogsFragment.this.lambda$perfromSelectedDialogsAction$19$DialogsFragment(this.f$1);
                        }
                    });
                } else if (getMessagesController().getDialogs(this.folderId).isEmpty()) {
                    this.listView.setEmptyView((View) null);
                    this.progressView.setVisibility(4);
                }
            } else {
                for (int a = 0; a < count; a++) {
                    long selectedDialog = selectedDialogs.get(a).longValue();
                    TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(selectedDialog);
                    if (dialog != null) {
                        int lower_id = (int) selectedDialog;
                        int high_id = (int) (selectedDialog >> 32);
                        if (lower_id == 0) {
                            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(high_id));
                            if (encryptedChat != null) {
                                chat = null;
                                user = getMessagesController().getUser(Integer.valueOf(encryptedChat.user_id));
                            } else {
                                chat = null;
                                user = new TLRPC.TL_userEmpty();
                            }
                        } else if (lower_id > 0) {
                            chat = null;
                            user = getMessagesController().getUser(Integer.valueOf(lower_id));
                        } else {
                            chat = getMessagesController().getChat(Integer.valueOf(-lower_id));
                            user = null;
                        }
                        if (chat != null || user != null) {
                            boolean isBot = user != null && user.bot && !MessagesController.isSupportUser(user);
                            if (i2 != 4) {
                                long selectedDialog2 = selectedDialog;
                                int lower_id2 = lower_id;
                                if (i2 != 2) {
                                    i = 3;
                                    if (i2 != 3) {
                                    }
                                } else {
                                    i = 3;
                                }
                                if (count == 1) {
                                    int i4 = high_id;
                                    TLRPC.Dialog dialog2 = dialog;
                                    AlertsCreator.createClearOrDeleteDialogAlert(getCurrentFragment(), i2 == i, chat, user, lower_id2 == 0, new MessagesStorage.BooleanCallback(action, chat, selectedDialog2, isBot) {
                                        private final /* synthetic */ int f$1;
                                        private final /* synthetic */ TLRPC.Chat f$2;
                                        private final /* synthetic */ long f$3;
                                        private final /* synthetic */ boolean f$4;

                                        {
                                            this.f$1 = r2;
                                            this.f$2 = r3;
                                            this.f$3 = r4;
                                            this.f$4 = r6;
                                        }

                                        public final void run(boolean z) {
                                            DialogsFragment.this.lambda$perfromSelectedDialogsAction$21$DialogsFragment(this.f$1, this.f$2, this.f$3, this.f$4, z);
                                        }
                                    });
                                    return;
                                }
                                TLRPC.User user2 = user;
                                TLRPC.Dialog dialog3 = dialog;
                                if (i2 != 3 || this.canClearCacheCount == 0) {
                                    long selectedDialog3 = selectedDialog2;
                                    if (i2 == 3) {
                                        getMessagesController().deleteDialog(selectedDialog3, 1, false);
                                    } else {
                                        if (chat == null) {
                                            getMessagesController().deleteDialog(selectedDialog3, 0, false);
                                            if (isBot) {
                                                getMessagesController().blockUser((int) selectedDialog3);
                                            }
                                        } else if (ChatObject.isNotInChat(chat)) {
                                            getMessagesController().deleteDialog(selectedDialog3, 0, false);
                                        } else {
                                            getMessagesController().deleteUserFromChat((int) (-selectedDialog3), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null);
                                        }
                                        if (AndroidUtilities.isTablet()) {
                                            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(selectedDialog3));
                                        }
                                    }
                                } else {
                                    getMessagesController().deleteDialog(selectedDialog2, 2, false);
                                }
                            } else if (this.canReadCount != 0) {
                                getMessagesController().markMentionsAsRead(selectedDialog);
                                long j = selectedDialog;
                                int i5 = lower_id;
                                getMessagesController().markDialogAsRead(selectedDialog, dialog.top_message, dialog.top_message, dialog.last_message_date, false, 0, true, 0);
                            } else {
                                int i6 = lower_id;
                                getMessagesController().markDialogAsUnread(selectedDialog, (TLRPC.InputPeer) null, 0);
                            }
                        }
                    }
                }
                toggleEditModel();
            }
        }
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$19$DialogsFragment(ArrayList copy) {
        getMessagesController().addDialogToFolder(copy, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC.TL_inputFolderPeer>) null, 0);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$21$DialogsFragment(int action, TLRPC.Chat chat, long selectedDialog, boolean isBot, boolean param) {
        int i = action;
        TLRPC.Chat chat2 = chat;
        long j = selectedDialog;
        toggleEditModel();
        if (i != 3 || !ChatObject.isChannel(chat)) {
            boolean z = param;
        } else if (!chat2.megagroup || !TextUtils.isEmpty(chat2.username)) {
            getMessagesController().deleteDialog(j, 2, param);
            return;
        } else {
            boolean z2 = param;
        }
        if (i == 2 && this.folderId != 0 && getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false).size() == 1) {
            this.progressView.setVisibility(4);
        }
        UndoView undoView2 = getUndoView();
        int i2 = i == 3 ? 0 : 1;
        $$Lambda$DialogsFragment$w_zFkUhm6jWOJmbkuRJgRP8GkKw r8 = r0;
        $$Lambda$DialogsFragment$w_zFkUhm6jWOJmbkuRJgRP8GkKw r0 = new Runnable(action, selectedDialog, param, chat, isBot) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ TLRPC.Chat f$4;
            private final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void run() {
                DialogsFragment.this.lambda$null$20$DialogsFragment(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        };
        undoView2.showWithAction(j, i2, (Runnable) r8);
    }

    public /* synthetic */ void lambda$null$20$DialogsFragment(int action, long selectedDialog, boolean param, TLRPC.Chat chat, boolean isBot) {
        if (action == 3) {
            getMessagesController().deleteDialog(selectedDialog, 1, param);
            return;
        }
        if (chat == null) {
            getMessagesController().deleteDialog(selectedDialog, 0, param);
            if (isBot) {
                getMessagesController().blockUser((int) selectedDialog);
            }
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(selectedDialog, 0, param);
        } else {
            getMessagesController().deleteUserFromChat((int) (-selectedDialog), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(selectedDialog));
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    private void updateCounters(boolean markAllAsRead, boolean hide) {
        ArrayList<Long> selectedDialogs;
        int count;
        ArrayList<Long> selectedDialogs2;
        int canUnarchiveCount;
        TLRPC.User user;
        int canClearHistoryCount = 0;
        int canDeleteCount = 0;
        int canUnpinCount = 0;
        int canArchiveCount = 0;
        int high_id = 0;
        this.canUnmuteCount = 0;
        this.canMuteCount = 0;
        this.canPinCount = 0;
        this.canReadCount = 0;
        this.canClearCacheCount = 0;
        if (!hide) {
            if (!markAllAsRead) {
                selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
            } else {
                selectedDialogs = this.dialogsAdapter.getAllDialogIdsList();
            }
            int count2 = selectedDialogs.size();
            int a = 0;
            while (a < count2) {
                TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(selectedDialogs.get(a).longValue());
                if (dialog == null) {
                    selectedDialogs2 = selectedDialogs;
                    count = count2;
                } else {
                    long selectedDialog = dialog.id;
                    boolean pinned = dialog.pinned;
                    boolean hasUnread = dialog.unread_count != 0 || dialog.unread_mark;
                    if (getMessagesController().isDialogMuted(selectedDialog)) {
                        this.canUnmuteCount++;
                    } else {
                        this.canMuteCount++;
                    }
                    if (hasUnread) {
                        this.canReadCount++;
                    }
                    if (this.folderId == 1) {
                        canUnarchiveCount = high_id + 1;
                    } else {
                        int canUnarchiveCount2 = high_id;
                        if (selectedDialog != ((long) getUserConfig().getClientUserId()) && selectedDialog != 777000) {
                            if (!getMessagesController().isProxyDialog(selectedDialog, false)) {
                                canArchiveCount++;
                                canUnarchiveCount = canUnarchiveCount2;
                            }
                        }
                        canUnarchiveCount = canUnarchiveCount2;
                    }
                    int lower_id = (int) selectedDialog;
                    int canArchiveCount2 = canArchiveCount;
                    int canUnarchiveCount3 = canUnarchiveCount;
                    int high_id2 = (int) (selectedDialog >> 32);
                    if (DialogObject.isChannel(dialog)) {
                        TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(-lower_id));
                        selectedDialogs2 = selectedDialogs;
                        count = count2;
                        TLRPC.Dialog dialog2 = dialog;
                        if (getMessagesController().isProxyDialog(dialog.id, true)) {
                            this.canClearCacheCount++;
                        } else {
                            if (pinned) {
                                canUnpinCount++;
                            } else {
                                this.canPinCount++;
                            }
                            if (chat == null || !chat.megagroup) {
                                this.canClearCacheCount++;
                                canDeleteCount++;
                            } else {
                                if (TextUtils.isEmpty(chat.username)) {
                                    canClearHistoryCount++;
                                } else {
                                    this.canClearCacheCount++;
                                }
                                canDeleteCount++;
                            }
                        }
                        canArchiveCount = canArchiveCount2;
                        high_id = canUnarchiveCount3;
                    } else {
                        selectedDialogs2 = selectedDialogs;
                        count = count2;
                        TLRPC.Dialog dialog3 = dialog;
                        boolean isChat = lower_id < 0 && high_id2 != 1;
                        TLRPC.User user2 = null;
                        if (isChat) {
                            TLRPC.Chat chat2 = getMessagesController().getChat(Integer.valueOf(-lower_id));
                        }
                        if (lower_id == 0) {
                            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(high_id2));
                            if (encryptedChat != null) {
                                user = getMessagesController().getUser(Integer.valueOf(encryptedChat.user_id));
                            } else {
                                user = new TLRPC.TL_userEmpty();
                            }
                        } else {
                            if (!isChat && lower_id > 0 && high_id2 != 1) {
                                user2 = getMessagesController().getUser(Integer.valueOf(lower_id));
                            }
                            user = user2;
                        }
                        if (user == null || !user.bot || MessagesController.isSupportUser(user)) {
                        }
                        if (pinned) {
                            canUnpinCount++;
                        } else {
                            this.canPinCount++;
                        }
                        canClearHistoryCount++;
                        canDeleteCount++;
                        canArchiveCount = canArchiveCount2;
                        high_id = canUnarchiveCount3;
                    }
                }
                a++;
                selectedDialogs = selectedDialogs2;
                count2 = count;
            }
        }
    }

    private int getCanReadCountInAllDialogs() {
        int canReadCount2 = 0;
        if (this.isEditModel) {
            ArrayList<Long> dialogs = this.dialogsAdapter.getAllDialogIdsList();
            for (int a = 0; a < dialogs.size(); a++) {
                TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogs.get(a).longValue());
                if (dialog != null) {
                    if (dialog.unread_count != 0 || dialog.unread_mark) {
                        canReadCount2++;
                    }
                }
            }
        }
        return canReadCount2;
    }

    private void hideActionMode(boolean animateCheck) {
        this.actionBar.hideActionMode();
        if (this.menuDrawable != null) {
            this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", R.string.AccDescrOpenMenu));
        }
        this.dialogsAdapter.getSelectedDialogs().clear();
        MenuDrawable menuDrawable2 = this.menuDrawable;
        if (menuDrawable2 != null) {
            menuDrawable2.setRotation(0.0f, true);
        } else {
            BackDrawable backDrawable2 = this.backDrawable;
            if (backDrawable2 != null) {
                backDrawable2.setRotation(0.0f, true);
            }
        }
        int i = 0;
        this.allowMoving = false;
        if (this.movingWas) {
            getMessagesController().reorderPinnedDialogs(this.folderId, (ArrayList<TLRPC.InputDialogPeer>) null, 0);
            this.movingWas = false;
        }
        updateCounters(false, true);
        this.dialogsAdapter.onReorderStateChanged(false);
        if (animateCheck) {
            i = 8192;
        }
        updateVisibleRows(i | 196608);
    }

    private void hideActionPanel() {
        this.dialogsAdapter.getSelectedDialogs().clear();
        updateCounters(false, true);
        this.dialogsAdapter.onReorderStateChanged(false);
    }

    private int getPinnedCount() {
        int pinnedCount = 0;
        ArrayList<TLRPC.Dialog> dialogs = getMessagesController().getDialogs(this.folderId);
        int N = dialogs.size();
        for (int a = 0; a < N; a++) {
            TLRPC.Dialog dialog = dialogs.get(a);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                int i = (int) dialog.id;
                if (!dialog.pinned) {
                    break;
                }
                pinnedCount++;
            }
        }
        return pinnedCount;
    }

    private void showOrUpdateActionMode(TLRPC.Dialog dialog, View cell) {
        this.dialogsAdapter.addOrRemoveSelectedDialog(dialog.id, cell);
        boolean z = false;
        updateCounters(false, false);
        FmtConsumDelegate fmtConsumDelegate = this.delegate;
        if (fmtConsumDelegate != null) {
            if (getCanReadCountInAllDialogs() > 0) {
                z = true;
            }
            fmtConsumDelegate.onUpdateState(z, this.dialogsAdapter.getSelectedDialogs().size(), this.canReadCount);
        }
    }

    /* access modifiers changed from: private */
    public void closeSearch() {
        if (AndroidUtilities.isTablet()) {
            MrySearchView mrySearchView = this.searchView;
            if (mrySearchView != null && mrySearchView.isSearchFieldVisible()) {
                this.searchView.closeSearchField();
            }
            TLObject tLObject = this.searchObject;
            if (tLObject != null) {
                this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                this.searchObject = null;
                return;
            }
            return;
        }
        closeSearchView(true);
    }

    public void closeSearchView(boolean anim) {
        MrySearchView mrySearchView = this.searchView;
        if (mrySearchView != null && mrySearchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField(anim);
        }
    }

    public boolean onBackPressed() {
        MrySearchView mrySearchView = this.searchView;
        if (mrySearchView != null && mrySearchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
            return true;
        } else if (!this.isEditModel) {
            return super.onBackPressed();
        } else {
            toggleEditModel();
            return true;
        }
    }

    public static class ComputerLoginView extends AppCompatImageView {
        private int color;
        private boolean mIsLogin;
        private Paint mPointPaint;
        private RectF mRectF;
        private float mWidth = ((float) AndroidUtilities.dp(8.0f));

        public ComputerLoginView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.mPointPaint = paint;
            paint.setStyle(Paint.Style.FILL);
            this.mPointPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            float centerX = ((float) w) / 2.0f;
            float left = ((float) AndroidUtilities.dp(5.0f)) + centerX;
            float top = centerX - ((float) AndroidUtilities.dp(10.0f));
            float f = this.mWidth;
            this.mRectF = new RectF(left, top, left + f, f + top);
        }

        private void updatePaint() {
            if (Theme.isThemeDefault()) {
                this.color = -11010115;
            } else {
                this.color = Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton);
            }
            this.mPointPaint.setColor(this.color);
        }

        public void updateLoginStatus(boolean isLogin) {
            if (this.mIsLogin != isLogin) {
                this.mIsLogin = isLogin;
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.mIsLogin) {
                updatePaint();
                RectF rectF = this.mRectF;
                float f = this.mWidth;
                canvas.drawRoundRect(rectF, f / 2.0f, f / 2.0f, this.mPointPaint);
            }
        }
    }
}
