package im.bclpbkiauv.ui.hui.discovery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ManageChatUserCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ShareLocationDrawable;
import im.bclpbkiauv.ui.components.UndoView;
import im.bclpbkiauv.ui.dialogs.DialogNearPersonFilter;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import im.bclpbkiauv.ui.utils.NetworkUtils;
import im.bclpbkiauv.ui.utils.SimulatorUtil;
import java.util.ArrayList;

public class NearPersonAndGroupActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, LocationController.LocationFetchCallback {
    private static final int SHORT_POLL_TIMEOUT = 25000;
    /* access modifiers changed from: private */
    public ArrayList<View> animatingViews = new ArrayList<>();
    private AvatarDrawable avatarDrawable;
    private boolean canCreateGroup;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_peerLocated> chats = new ArrayList<>(getLocationController().getCachedNearbyChats());
    private int chatsCreateRow;
    private int chatsEndRow;
    private int chatsHeaderRow;
    private int chatsSectionRow;
    private int chatsStartRow;
    private Runnable checkExpiredRunnable;
    private boolean checkingCanCreate;
    private int currentChatId;
    private String currentGroupCreateAddress;
    private String currentGroupCreateDisplayAddress;
    private BDLocation currentGroupCreateLocation;
    /* access modifiers changed from: private */
    public CharSequence currentName;
    /* access modifiers changed from: private */
    public CharSequence currrntStatus;
    private boolean firstLoaded;
    private ActionIntroActivity groupCreateActivity;
    /* access modifiers changed from: private */
    public int groupRowCount = 1;
    private int helpRow;
    private boolean isAdmin;
    private TLRPC.FileLocation lastAvatar;
    private BDLocation lastLoadedLocation;
    private long lastLoadedLocationTime;
    private String lastName;
    private int lastStatus;
    /* access modifiers changed from: private */
    public ListAdapterGroup listAdapterGroup = null;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    private AlertDialog loadingDialog;
    /* access modifiers changed from: private */
    public TextView m_tvCurrent;
    /* access modifiers changed from: private */
    public TextView m_tvNearGroup;
    /* access modifiers changed from: private */
    public TextView m_tvNearPerson;
    private int reqId;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public Runnable shortPollRunnable = new Runnable() {
        public void run() {
            if (NearPersonAndGroupActivity.this.shortPollRunnable != null) {
                NearPersonAndGroupActivity.this.sendRequest(true);
                AndroidUtilities.cancelRunOnUIThread(NearPersonAndGroupActivity.this.shortPollRunnable);
                AndroidUtilities.runOnUIThread(NearPersonAndGroupActivity.this.shortPollRunnable, 25000);
            }
        }
    };
    /* access modifiers changed from: private */
    public AnimatorSet showProgressAnimation;
    private Runnable showProgressRunnable;
    private boolean showingLoadingProgress;
    private int statusColor;
    private int statusOnlineColor;
    private UndoView undoView;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_peerLocated> users = new ArrayList<>(getLocationController().getCachedNearbyUsers());
    private int usersEmptyRow;
    private int usersEndRow;
    private int usersHeaderRow;
    private int usersSectionRow;
    private int usersStartRow;

    private void initView(Context context) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.fragmentView.findViewById(R.id.rl_title_bar).getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.statusBarHeight;
        this.fragmentView.findViewById(R.id.rl_title_bar).setLayoutParams(layoutParams);
        this.m_tvNearPerson = (TextView) this.fragmentView.findViewById(R.id.tv_near_person);
        this.m_tvNearGroup = (TextView) this.fragmentView.findViewById(R.id.tv_near_group);
        TextView textView = this.m_tvNearPerson;
        this.m_tvCurrent = textView;
        textView.setText(LocaleController.getString("PeopleNearbyHeader", R.string.PeopleNearbyHeader));
        this.m_tvNearGroup.setText(LocaleController.getString("ChatsNearbyHeader", R.string.ChatsNearbyHeader));
        ((ImageView) this.fragmentView.findViewById(R.id.iv_back)).setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        this.fragmentView.findViewById(R.id.iv_back).setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarDefaultSelector)));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ((FrameLayout) this.fragmentView.findViewById(R.id.fl_container)).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        initListener(context);
    }

    private void initListener(final Context context) {
        this.m_tvNearPerson.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (NearPersonAndGroupActivity.this.m_tvCurrent.getId() != view.getId()) {
                    NearPersonAndGroupActivity.this.m_tvNearPerson.setTextColor(-1);
                    NearPersonAndGroupActivity.this.m_tvNearPerson.setBackground(context.getResources().getDrawable(R.drawable.near_person_tab1_bg));
                    NearPersonAndGroupActivity.this.m_tvCurrent.setTextColor(context.getResources().getColor(R.color.new_call_tab_text_color_unseled));
                    NearPersonAndGroupActivity.this.m_tvCurrent.setBackground(context.getResources().getDrawable(R.drawable.near_person_tab2_bg));
                    NearPersonAndGroupActivity nearPersonAndGroupActivity = NearPersonAndGroupActivity.this;
                    TextView unused = nearPersonAndGroupActivity.m_tvCurrent = nearPersonAndGroupActivity.m_tvNearPerson;
                    NearPersonAndGroupActivity.this.listView.setAdapter(NearPersonAndGroupActivity.this.listViewAdapter);
                }
            }
        });
        this.m_tvNearGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (NearPersonAndGroupActivity.this.m_tvCurrent.getId() != view.getId()) {
                    NearPersonAndGroupActivity.this.m_tvNearGroup.setTextColor(-1);
                    NearPersonAndGroupActivity.this.m_tvNearGroup.setBackground(context.getResources().getDrawable(R.drawable.near_person_tab2_unseled_bg));
                    NearPersonAndGroupActivity.this.m_tvCurrent.setTextColor(context.getResources().getColor(R.color.new_call_tab_text_color_unseled));
                    NearPersonAndGroupActivity.this.m_tvCurrent.setBackground(context.getResources().getDrawable(R.drawable.near_person_tab1_unseled_bg));
                    NearPersonAndGroupActivity nearPersonAndGroupActivity = NearPersonAndGroupActivity.this;
                    TextView unused = nearPersonAndGroupActivity.m_tvCurrent = nearPersonAndGroupActivity.m_tvNearGroup;
                    if (NearPersonAndGroupActivity.this.listAdapterGroup == null) {
                        NearPersonAndGroupActivity nearPersonAndGroupActivity2 = NearPersonAndGroupActivity.this;
                        ListAdapterGroup unused2 = nearPersonAndGroupActivity2.listAdapterGroup = new ListAdapterGroup(context);
                    }
                    NearPersonAndGroupActivity.this.listView.setAdapter(NearPersonAndGroupActivity.this.listAdapterGroup);
                }
            }
        });
        this.fragmentView.findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NearPersonAndGroupActivity.this.finishFragment();
            }
        });
        this.fragmentView.findViewById(R.id.rl_more).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new DialogNearPersonFilter(NearPersonAndGroupActivity.this.getParentActivity()).show();
            }
        });
    }

    public NearPersonAndGroupActivity() {
        checkForExpiredLocations(false);
        this.statusColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
        this.statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
        this.avatarDrawable = new AvatarDrawable();
        updateRows();
    }

    private void updateRows() {
        this.rowCount = 0;
        this.groupRowCount = 1;
        this.usersStartRow = -1;
        this.usersEndRow = -1;
        this.usersEmptyRow = -1;
        this.chatsStartRow = -1;
        this.chatsEndRow = -1;
        this.chatsCreateRow = -1;
        int i = 0 + 1;
        this.rowCount = i;
        this.helpRow = 0;
        this.rowCount = i + 1;
        this.usersHeaderRow = i;
        if (this.users.isEmpty()) {
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.usersEmptyRow = i2;
        } else {
            int i3 = this.rowCount;
            this.usersStartRow = i3;
            int size = i3 + this.users.size();
            this.rowCount = size;
            this.usersEndRow = size;
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.usersSectionRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.chatsHeaderRow = i5;
        this.rowCount = i6 + 1;
        this.chatsCreateRow = i6;
        if (!this.chats.isEmpty()) {
            int i7 = this.rowCount;
            this.chatsStartRow = i7;
            int size2 = i7 + this.chats.size();
            this.rowCount = size2;
            this.chatsEndRow = size2;
        }
        int i8 = this.rowCount;
        this.rowCount = i8 + 1;
        this.chatsSectionRow = i8;
        this.rowCount = this.users.size();
        this.groupRowCount = this.chats.size() + 1;
        if (this.m_tvCurrent == this.m_tvNearPerson) {
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        ListAdapterGroup listAdapterGroup2 = this.listAdapterGroup;
        if (listAdapterGroup2 != null) {
            listAdapterGroup2.notifyDataSetChanged();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.newLocationAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.newPeopleNearbyAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
        checkCanCreateGroup();
        sendRequest(false);
        AndroidUtilities.runOnUIThread(this.shortPollRunnable, 25000);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.newLocationAvailable);
        getNotificationCenter().removeObserver(this, NotificationCenter.newPeopleNearbyAvailable);
        getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.shortPollRunnable = null;
        }
        Runnable runnable2 = this.checkExpiredRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.checkExpiredRunnable = null;
        }
        Runnable runnable3 = this.showProgressRunnable;
        if (runnable3 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable3);
            this.showProgressRunnable = null;
        }
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public View createView(Context context) {
        this.actionBar.setAddToContainer(false);
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_nearperson_and_group, (ViewGroup) null, false);
        initView(context);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                NearPersonAndGroupActivity.this.lambda$createView$1$NearPersonAndGroupActivity(view, i);
            }
        });
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$NearPersonAndGroupActivity(View view, int position) {
        int chatId;
        TLRPC.User user;
        if (this.m_tvCurrent == this.m_tvNearPerson) {
            if (!this.users.isEmpty() && (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.users.get(position).peer.user_id))) != null) {
                Bundle args = new Bundle();
                args.putInt("from_type", 5);
                presentFragment(new AddContactsInfoActivity(args, user));
            }
        } else if (position != 0) {
            TLRPC.TL_peerLocated peerLocated = this.chats.get(position - 1);
            Bundle args1 = new Bundle();
            if (peerLocated.peer instanceof TLRPC.TL_peerChat) {
                chatId = peerLocated.peer.chat_id;
            } else {
                chatId = peerLocated.peer.channel_id;
            }
            args1.putInt("chat_id", chatId);
            presentFragment(new ChatActivity(args1));
        } else if (this.checkingCanCreate || this.currentGroupCreateAddress == null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.loadingDialog = alertDialog;
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    NearPersonAndGroupActivity.this.lambda$null$0$NearPersonAndGroupActivity(dialogInterface);
                }
            });
            this.loadingDialog.show();
        } else {
            openGroupCreate();
        }
    }

    public /* synthetic */ void lambda$null$0$NearPersonAndGroupActivity(DialogInterface dialog) {
        this.loadingDialog = null;
    }

    private void openGroupCreate() {
        if (!this.canCreateGroup) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("YourLocatedChannelsTooMuch", R.string.YourLocatedChannelsTooMuch));
            return;
        }
        ActionIntroActivity actionIntroActivity = new ActionIntroActivity(2);
        this.groupCreateActivity = actionIntroActivity;
        actionIntroActivity.setGroupCreateAddress(this.currentGroupCreateAddress, this.currentGroupCreateDisplayAddress, this.currentGroupCreateLocation);
        presentFragment(this.groupCreateActivity);
    }

    private void checkCanCreateGroup() {
        if (!this.checkingCanCreate) {
            this.checkingCanCreate = true;
            TLRPC.TL_channels_getAdminedPublicChannels req = new TLRPC.TL_channels_getAdminedPublicChannels();
            req.by_location = true;
            req.check_limit = true;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    NearPersonAndGroupActivity.this.lambda$checkCanCreateGroup$3$NearPersonAndGroupActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$checkCanCreateGroup$3$NearPersonAndGroupActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NearPersonAndGroupActivity.this.lambda$null$2$NearPersonAndGroupActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$NearPersonAndGroupActivity(TLRPC.TL_error error) {
        this.canCreateGroup = error == null;
        this.checkingCanCreate = false;
        AlertDialog alertDialog = this.loadingDialog;
        if (alertDialog != null && this.currentGroupCreateAddress != null) {
            try {
                alertDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.loadingDialog = null;
            openGroupCreate();
        }
    }

    private void showLoadingProgress(boolean show) {
        if (this.showingLoadingProgress != show) {
            this.showingLoadingProgress = show;
            AnimatorSet animatorSet = this.showProgressAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.showProgressAnimation = null;
            }
            if (this.listView != null) {
                ArrayList<Animator> animators = new ArrayList<>();
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    if (child instanceof HeaderCellProgress) {
                        HeaderCellProgress cell = (HeaderCellProgress) child;
                        this.animatingViews.add(cell);
                        RadialProgressView access$800 = cell.progressView;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = show ? 1.0f : 0.0f;
                        animators.add(ObjectAnimator.ofFloat(access$800, property, fArr));
                    }
                }
                if (animators.isEmpty() == 0) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.showProgressAnimation = animatorSet2;
                    animatorSet2.playTogether(animators);
                    this.showProgressAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            AnimatorSet unused = NearPersonAndGroupActivity.this.showProgressAnimation = null;
                            NearPersonAndGroupActivity.this.animatingViews.clear();
                        }
                    });
                    this.showProgressAnimation.setDuration(180);
                    this.showProgressAnimation.start();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendRequest(boolean shortpoll) {
        if (!this.firstLoaded) {
            $$Lambda$NearPersonAndGroupActivity$WSH0O8igsP2WBWYoZ0kHjxy_Y r0 = new Runnable() {
                public final void run() {
                    NearPersonAndGroupActivity.this.lambda$sendRequest$4$NearPersonAndGroupActivity();
                }
            };
            this.showProgressRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 1000);
            this.firstLoaded = true;
        }
        if (!NetworkUtils.hasSimCard(ApplicationLoader.applicationContext)) {
            FileLog.d("--------->no sim card:");
        } else if (SimulatorUtil.isSimulator(ApplicationLoader.applicationContext)) {
            FileLog.d("--------->is simulator");
        } else {
            BDLocation location = getLocationController().getLastKnownLocation();
            if (location == null) {
                FileLog.d("--------->location:" + null);
                return;
            }
            FileLog.d("--------->location is ok");
            this.currentGroupCreateLocation = location;
            if (!shortpoll && this.lastLoadedLocation != null) {
                double distance = DistanceUtil.getDistance(new LatLng(this.lastLoadedLocation.getLatitude(), this.lastLoadedLocation.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude()));
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("located distance = " + distance);
                }
                if (SystemClock.uptimeMillis() - this.lastLoadedLocationTime >= 3000 && distance > 20.0d) {
                    if (this.reqId != 0) {
                        getConnectionsManager().cancelRequest(this.reqId, true);
                        this.reqId = 0;
                    }
                } else {
                    return;
                }
            }
            if (this.reqId == 0) {
                this.lastLoadedLocation = location;
                this.lastLoadedLocationTime = SystemClock.uptimeMillis();
                LocationController.fetchLocationAddress(this.currentGroupCreateLocation, this);
                TLRPC.TL_contacts_getLocated req = new TLRPC.TL_contacts_getLocated();
                req.geo_point = new TLRPC.TL_inputGeoPoint();
                req.geo_point.lat = location.getLatitude();
                req.geo_point._long = location.getLongitude();
                this.reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        NearPersonAndGroupActivity.this.lambda$sendRequest$6$NearPersonAndGroupActivity(tLObject, tL_error);
                    }
                });
                getConnectionsManager().bindRequestToGuid(this.reqId, this.classGuid);
            }
        }
    }

    public /* synthetic */ void lambda$sendRequest$4$NearPersonAndGroupActivity() {
        showLoadingProgress(true);
        this.showProgressRunnable = null;
    }

    public /* synthetic */ void lambda$sendRequest$6$NearPersonAndGroupActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NearPersonAndGroupActivity.this.lambda$null$5$NearPersonAndGroupActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$NearPersonAndGroupActivity(TLObject response) {
        int chatId;
        this.reqId = 0;
        Runnable runnable = this.showProgressRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showProgressRunnable = null;
        }
        showLoadingProgress(false);
        if (response != null) {
            FileLog.d("--------> location: response is ok");
            TLRPC.Updates updates = (TLRPC.TL_updates) response;
            getMessagesController().putUsers(updates.users, false);
            getMessagesController().putChats(updates.chats, false);
            this.users.clear();
            this.chats.clear();
            int N = updates.updates.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Update baseUpdate = updates.updates.get(a);
                if (baseUpdate instanceof TLRPC.TL_updatePeerLocated) {
                    TLRPC.TL_updatePeerLocated update = (TLRPC.TL_updatePeerLocated) baseUpdate;
                    int N2 = update.peers.size();
                    for (int b = 0; b < N2; b++) {
                        TLRPC.TL_peerLocated peerLocated = update.peers.get(b);
                        if (peerLocated.peer instanceof TLRPC.TL_peerUser) {
                            this.users.add(peerLocated);
                        } else {
                            if (peerLocated.peer instanceof TLRPC.TL_peerChat) {
                                chatId = peerLocated.peer.chat_id;
                            } else {
                                chatId = peerLocated.peer.channel_id;
                            }
                            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(chatId));
                            if (chat != null && !(chat instanceof TLRPC.TL_channelForbidden)) {
                                this.chats.add(peerLocated);
                            }
                        }
                    }
                }
            }
            checkForExpiredLocations(true);
            updateRows();
        }
        Runnable runnable2 = this.shortPollRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            AndroidUtilities.runOnUIThread(this.shortPollRunnable, 25000);
        }
    }

    public void onResume() {
        Activity activity;
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (Build.VERSION.SDK_INT < 23 || (activity = getParentActivity()) == null || activity.checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) == 0) {
            getLocationController().startLocationLookupForPeopleNearby(false);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
    }

    public void onPause() {
        super.onPause();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
        getLocationController().startLocationLookupForPeopleNearby(true);
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        super.onBecomeFullyHidden();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public void onLocationAddressAvailable(String address, String displayAddress, BDLocation location) {
        this.currentGroupCreateAddress = address;
        this.currentGroupCreateDisplayAddress = displayAddress;
        this.currentGroupCreateLocation = location;
        ActionIntroActivity actionIntroActivity = this.groupCreateActivity;
        if (actionIntroActivity != null) {
            actionIntroActivity.setGroupCreateAddress(address, displayAddress, location);
        }
        AlertDialog alertDialog = this.loadingDialog;
        if (alertDialog != null && !this.checkingCanCreate) {
            try {
                alertDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.loadingDialog = null;
            openGroupCreate();
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        this.groupCreateActivity = null;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ArrayList<TLRPC.TL_peerLocated> arrayList;
        int i = id;
        if (i == NotificationCenter.newLocationAvailable) {
            sendRequest(false);
        } else if (i == NotificationCenter.newPeopleNearbyAvailable) {
            TLRPC.TL_updatePeerLocated update = args[0];
            int N2 = update.peers.size();
            for (int b = 0; b < N2; b++) {
                TLRPC.TL_peerLocated peerLocated = update.peers.get(b);
                boolean found = false;
                if (peerLocated.peer instanceof TLRPC.TL_peerUser) {
                    arrayList = this.users;
                } else {
                    arrayList = this.chats;
                }
                int N = arrayList.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.TL_peerLocated old = arrayList.get(a);
                    if ((old.peer.user_id != 0 && old.peer.user_id == peerLocated.peer.user_id) || ((old.peer.chat_id != 0 && old.peer.chat_id == peerLocated.peer.chat_id) || (old.peer.channel_id != 0 && old.peer.channel_id == peerLocated.peer.channel_id))) {
                        arrayList.set(a, peerLocated);
                        found = true;
                    }
                }
                if (!found) {
                    arrayList.add(peerLocated);
                }
            }
            checkForExpiredLocations(true);
            updateRows();
        } else if (i == NotificationCenter.needDeleteDialog && this.fragmentView != null && !this.isPaused) {
            long dialogId = args[0].longValue();
            TLRPC.User user = args[1];
            $$Lambda$NearPersonAndGroupActivity$8E4LmdaFtTS9TM8YIScyECeN57Y r0 = new Runnable(args[2], dialogId, args[3].booleanValue()) {
                private final /* synthetic */ TLRPC.Chat f$1;
                private final /* synthetic */ long f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                }

                public final void run() {
                    NearPersonAndGroupActivity.this.lambda$didReceivedNotification$7$NearPersonAndGroupActivity(this.f$1, this.f$2, this.f$3);
                }
            };
            UndoView undoView2 = this.undoView;
            if (undoView2 != null) {
                undoView2.showWithAction(dialogId, 1, (Runnable) r0);
            } else {
                r0.run();
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$7$NearPersonAndGroupActivity(TLRPC.Chat chat, long dialogId, boolean revoke) {
        if (chat == null) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else {
            getMessagesController().deleteUserFromChat((int) (-dialogId), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null, false, revoke);
        }
    }

    private void checkForExpiredLocations(boolean cache) {
        Runnable runnable = this.checkExpiredRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkExpiredRunnable = null;
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        int minExpired = Integer.MAX_VALUE;
        boolean changed = false;
        int a = 0;
        while (a < 2) {
            ArrayList<TLRPC.TL_peerLocated> arrayList = a == 0 ? this.users : this.chats;
            int b = 0;
            int N = arrayList.size();
            while (b < N) {
                TLRPC.TL_peerLocated peer = arrayList.get(b);
                if (peer.expires <= currentTime) {
                    arrayList.remove(b);
                    b--;
                    N--;
                    changed = true;
                } else {
                    minExpired = Math.min(minExpired, peer.expires);
                }
                b++;
            }
            a++;
        }
        if (changed && this.listViewAdapter != null) {
            updateRows();
        }
        if (changed || cache) {
            getLocationController().setCachedNearbyUsersAndChats(this.users, this.chats);
        }
        if (minExpired != Integer.MAX_VALUE) {
            $$Lambda$NearPersonAndGroupActivity$h1mg5yFYhdTEmr6EqmA68IUeA0w r3 = new Runnable() {
                public final void run() {
                    NearPersonAndGroupActivity.this.lambda$checkForExpiredLocations$8$NearPersonAndGroupActivity();
                }
            };
            this.checkExpiredRunnable = r3;
            AndroidUtilities.runOnUIThread(r3, (long) ((minExpired - currentTime) * 1000));
        }
    }

    public /* synthetic */ void lambda$checkForExpiredLocations$8$NearPersonAndGroupActivity() {
        this.checkExpiredRunnable = null;
        checkForExpiredLocations(false);
    }

    public class HeaderCellProgress extends HeaderCell {
        /* access modifiers changed from: private */
        public RadialProgressView progressView;

        public HeaderCellProgress(Context context) {
            super(context);
            setClipChildren(false);
            RadialProgressView radialProgressView = new RadialProgressView(context);
            this.progressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(14.0f));
            this.progressView.setStrokeWidth(2.0f);
            this.progressView.setAlpha(0.0f);
            this.progressView.setProgressColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            addView(this.progressView, LayoutHelper.createFrame(50.0f, 40.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 2.0f : 0.0f, 3.0f, LocaleController.isRTL ? 0.0f : 2.0f, 0.0f));
        }
    }

    public class HintInnerCell extends FrameLayout {
        private ImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(74.0f), Theme.getColor(Theme.key_chats_archiveBackground)));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 2));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(74.0f, 74.0f, 49, 0.0f, 27.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_chats_message));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PeopleNearbyInfo", R.string.PeopleNearbyInfo, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 52.0f, 125.0f, 52.0f, 27.0f));
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2;
        }

        public int getItemCount() {
            if (NearPersonAndGroupActivity.this.rowCount == 0) {
                return 1;
            }
            return NearPersonAndGroupActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(this.mContext).inflate(R.layout.item_near_person, (ViewGroup) null, false);
            view.setTag(Integer.valueOf(viewType));
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(70.0f)));
            return new RecyclerListView.Holder(view);
        }

        private String formatDistance(TLRPC.TL_peerLocated located) {
            return LocaleController.formatDistance((float) located.distance);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (NearPersonAndGroupActivity.this.users.isEmpty() || position >= NearPersonAndGroupActivity.this.users.size()) {
                holder.itemView.findViewById(R.id.tv_no_data).setVisibility(0);
                holder.itemView.findViewById(R.id.iv_head_img).setVisibility(8);
                holder.itemView.findViewById(R.id.tv_nick_name).setVisibility(8);
                holder.itemView.findViewById(R.id.tv_distance).setVisibility(8);
                ((TextView) holder.itemView.findViewById(R.id.tv_no_data)).setText(AndroidUtilities.replaceTags(LocaleController.getString("PeopleNearbyEmpty", R.string.PeopleNearbyEmpty)));
                return;
            }
            TLRPC.TL_peerLocated peerLocated = (TLRPC.TL_peerLocated) NearPersonAndGroupActivity.this.users.get(position);
            TLRPC.UserFull userFull = MessagesController.getInstance(NearPersonAndGroupActivity.this.currentAccount).getUserFull(peerLocated.peer.user_id);
            TLRPC.User user = NearPersonAndGroupActivity.this.getMessagesController().getUser(Integer.valueOf(peerLocated.peer.user_id));
            if (user != null) {
                if (position == 0) {
                    holder.itemView.findViewById(R.id.tv_no_data).setVisibility(8);
                    holder.itemView.findViewById(R.id.iv_head_img).setVisibility(0);
                    holder.itemView.findViewById(R.id.tv_nick_name).setVisibility(0);
                    holder.itemView.findViewById(R.id.tv_distance).setVisibility(0);
                }
                CharSequence unused = NearPersonAndGroupActivity.this.currrntStatus = formatDistance(peerLocated);
                CharSequence unused2 = NearPersonAndGroupActivity.this.currentName = null;
                NearPersonAndGroupActivity.this.update(user, 0, holder.itemView);
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            return position;
        }
    }

    public void update(TLObject currentObject, int mask, View v) {
        TLRPC.FileLocation fileLocation;
        TLObject tLObject = currentObject;
        View view = v;
        TextView tv_nick = (TextView) view.findViewById(R.id.tv_nick_name);
        TextView tv_distance = (TextView) view.findViewById(R.id.tv_distance);
        BackupImageView iv_head_img = (BackupImageView) view.findViewById(R.id.iv_head_img);
        if (tLObject == null) {
            this.currrntStatus = null;
            this.currentName = null;
            tv_nick.setText("");
            tv_distance.setText("");
            iv_head_img.setImageDrawable((Drawable) null);
        } else if (tLObject instanceof TLRPC.User) {
            TLRPC.User currentUser = (TLRPC.User) tLObject;
            TLRPC.FileLocation photo = null;
            String newName = null;
            if (currentUser.photo != null) {
                photo = currentUser.photo.photo_small;
            }
            if (mask != 0) {
                boolean continueUpdate = false;
                if ((mask & 2) != 0 && ((this.lastAvatar != null && photo == null) || ((this.lastAvatar == null && photo != null) || !((fileLocation = this.lastAvatar) == null || photo == null || (fileLocation.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id))))) {
                    continueUpdate = true;
                }
                if (!(currentUser == null || continueUpdate || (mask & 4) == 0)) {
                    int newStatus = 0;
                    if (currentUser.status != null) {
                        newStatus = currentUser.status.expires;
                    }
                    if (newStatus != this.lastStatus) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate && this.currentName == null && this.lastName != null && (mask & 1) != 0) {
                    newName = UserObject.getName(currentUser);
                    if (!newName.equals(this.lastName)) {
                        continueUpdate = true;
                    }
                }
                if (!continueUpdate) {
                    return;
                }
            }
            AvatarDrawable avatarDrawableNew = new AvatarDrawable();
            avatarDrawableNew.setInfo(currentUser);
            if (currentUser.status != null) {
                this.lastStatus = currentUser.status.expires;
            } else {
                this.lastStatus = 0;
            }
            CharSequence charSequence = this.currentName;
            if (charSequence != null) {
                this.lastName = null;
                tv_nick.setText(charSequence);
            } else {
                String name = newName == null ? UserObject.getName(currentUser) : newName;
                this.lastName = name;
                tv_nick.setText(name);
            }
            if (this.currrntStatus != null) {
                tv_distance.setTextColor(this.statusColor);
                tv_distance.setText(this.currrntStatus);
            } else if (currentUser.bot) {
                tv_distance.setTextColor(this.statusColor);
                if (currentUser.bot_chat_history || this.isAdmin) {
                    tv_distance.setText(LocaleController.getString("BotStatusRead", R.string.BotStatusRead));
                } else {
                    tv_distance.setText(LocaleController.getString("BotStatusCantRead", R.string.BotStatusCantRead));
                }
            } else if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((currentUser.status != null && currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(currentUser.id)))) {
                tv_distance.setTextColor(this.statusOnlineColor);
                tv_distance.setText(LocaleController.getString("Online", R.string.Online));
            } else {
                tv_distance.setTextColor(this.statusColor);
                tv_distance.setText(LocaleController.formatUserStatus(this.currentAccount, currentUser));
            }
            this.lastAvatar = photo;
            iv_head_img.setRoundRadius(AndroidUtilities.dp(25.0f));
            iv_head_img.setImage(ImageLocation.getForUser(currentUser, false), "50_50", (Drawable) avatarDrawableNew, (Object) currentUser);
        }
    }

    private class ListAdapterGroup extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapterGroup(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2;
        }

        public int getItemCount() {
            return NearPersonAndGroupActivity.this.groupRowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(this.mContext).inflate(R.layout.item_near_group, (ViewGroup) null, false);
            view.setTag(Integer.valueOf(viewType));
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(70.0f)));
            return new RecyclerListView.Holder(view);
        }

        private String formatDistance(TLRPC.TL_peerLocated located) {
            return LocaleController.formatDistance((float) located.distance);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int chatId;
            if (position == 0) {
                holder.itemView.findViewById(R.id.tv_create_group).setVisibility(0);
                ((BackupImageView) holder.itemView.findViewById(R.id.iv_group_head_img)).setRoundRadius(AndroidUtilities.dp(25.0f));
                ((BackupImageView) holder.itemView.findViewById(R.id.iv_group_head_img)).setImageDrawable(this.mContext.getResources().getDrawable(R.mipmap.ic_create_group));
                return;
            }
            holder.itemView.findViewById(R.id.tv_create_group).setVisibility(8);
            if (!NearPersonAndGroupActivity.this.chats.isEmpty() && position - 1 < NearPersonAndGroupActivity.this.chats.size()) {
                TLRPC.TL_peerLocated peerLocated = (TLRPC.TL_peerLocated) NearPersonAndGroupActivity.this.chats.get(position - 1);
                if (peerLocated.peer instanceof TLRPC.TL_peerChat) {
                    chatId = peerLocated.peer.chat_id;
                } else {
                    chatId = peerLocated.peer.channel_id;
                }
                TLRPC.Chat chat = NearPersonAndGroupActivity.this.getMessagesController().getChat(Integer.valueOf(chatId));
                if (chat != null) {
                    String subtitle = formatDistance(peerLocated);
                    if (chat.participants_count != 0) {
                        subtitle = String.format("%1$s, %2$s", new Object[]{subtitle, LocaleController.formatPluralString("Members", chat.participants_count)});
                    }
                    CharSequence unused = NearPersonAndGroupActivity.this.currrntStatus = subtitle;
                    CharSequence unused2 = NearPersonAndGroupActivity.this.currentName = null;
                    NearPersonAndGroupActivity.this.updateGroup(chat, 0, holder.itemView);
                }
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            return position;
        }
    }

    /* access modifiers changed from: private */
    public void updateGroup(TLObject currentObject, int mask, View v) {
        TLRPC.FileLocation fileLocation;
        View view = v;
        TextView tv_nick = (TextView) view.findViewById(R.id.tv_nick_name);
        TextView tv_distance = (TextView) view.findViewById(R.id.tv_distance);
        BackupImageView iv_head_img = (BackupImageView) view.findViewById(R.id.iv_group_head_img);
        if (currentObject == null) {
            this.currrntStatus = null;
            this.currentName = null;
            tv_nick.setText("");
            tv_distance.setText("");
            iv_head_img.setImageDrawable((Drawable) null);
            return;
        }
        iv_head_img.setImageDrawable((Drawable) null);
        TLRPC.Chat currentChat = (TLRPC.Chat) currentObject;
        TLRPC.FileLocation photo = null;
        String newName = null;
        if (currentChat.photo != null) {
            photo = currentChat.photo.photo_small;
        }
        if (mask != 0) {
            boolean continueUpdate = false;
            if ((mask & 2) != 0 && ((this.lastAvatar != null && photo == null) || ((this.lastAvatar == null && photo != null) || !((fileLocation = this.lastAvatar) == null || photo == null || (fileLocation.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id))))) {
                continueUpdate = true;
            }
            if (!continueUpdate && this.currentName == null && this.lastName != null && (mask & 1) != 0) {
                newName = currentChat.title;
                if (!newName.equals(this.lastName)) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate) {
                return;
            }
        }
        AvatarDrawable avatarDrawableNew = new AvatarDrawable();
        avatarDrawableNew.setInfo(currentChat);
        CharSequence charSequence = this.currentName;
        if (charSequence != null) {
            this.lastName = null;
            tv_nick.setText(charSequence);
        } else {
            String str = newName == null ? currentChat.title : newName;
            this.lastName = str;
            tv_nick.setText(str);
        }
        if (this.currrntStatus != null) {
            tv_distance.setTextColor(this.statusColor);
            tv_distance.setText(this.currrntStatus);
        } else {
            tv_distance.setTextColor(this.statusColor);
            if (currentChat.participants_count != 0) {
                tv_distance.setText(LocaleController.formatPluralString("Members", currentChat.participants_count));
            } else if (currentChat.has_geo) {
                tv_distance.setText(LocaleController.getString("MegaLocation", R.string.MegaLocation));
            } else if (TextUtils.isEmpty(currentChat.username)) {
                tv_distance.setText(LocaleController.getString("MegaPrivate", R.string.MegaPrivate));
            } else {
                tv_distance.setText(LocaleController.getString("MegaPublic", R.string.MegaPublic));
            }
        }
        this.lastAvatar = photo;
        iv_head_img.setRoundRadius(AndroidUtilities.dp(25.0f));
        iv_head_img.setImage(ImageLocation.getForChat(currentChat, false), "50_50", (Drawable) avatarDrawableNew, (Object) currentChat);
    }
}
