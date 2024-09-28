package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ManageChatTextCell;
import im.bclpbkiauv.ui.cells.ManageChatUserCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ShareLocationDrawable;
import im.bclpbkiauv.ui.components.UndoView;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.util.ArrayList;

public class PeopleNearbyActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, LocationController.LocationFetchCallback {
    private static final int SHORT_POLL_TIMEOUT = 25000;
    /* access modifiers changed from: private */
    public ArrayList<View> animatingViews = new ArrayList<>();
    private boolean canCreateGroup;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_peerLocated> chats = new ArrayList<>(getLocationController().getCachedNearbyChats());
    /* access modifiers changed from: private */
    public int chatsCreateRow;
    /* access modifiers changed from: private */
    public int chatsEndRow;
    /* access modifiers changed from: private */
    public int chatsHeaderRow;
    /* access modifiers changed from: private */
    public int chatsSectionRow;
    /* access modifiers changed from: private */
    public int chatsStartRow;
    private Runnable checkExpiredRunnable;
    private boolean checkingCanCreate;
    private int currentChatId;
    private String currentGroupCreateAddress;
    private String currentGroupCreateDisplayAddress;
    private BDLocation currentGroupCreateLocation;
    private boolean firstLoaded;
    private ActionIntroActivity groupCreateActivity;
    /* access modifiers changed from: private */
    public int helpRow;
    private BDLocation lastLoadedLocation;
    private long lastLoadedLocationTime;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private AlertDialog loadingDialog;
    private int reqId;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public Runnable shortPollRunnable = new Runnable() {
        public void run() {
            if (PeopleNearbyActivity.this.shortPollRunnable != null) {
                PeopleNearbyActivity.this.sendRequest(true);
                AndroidUtilities.cancelRunOnUIThread(PeopleNearbyActivity.this.shortPollRunnable);
                AndroidUtilities.runOnUIThread(PeopleNearbyActivity.this.shortPollRunnable, 25000);
            }
        }
    };
    /* access modifiers changed from: private */
    public AnimatorSet showProgressAnimation;
    private Runnable showProgressRunnable;
    /* access modifiers changed from: private */
    public boolean showingLoadingProgress;
    private UndoView undoView;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_peerLocated> users = new ArrayList<>(getLocationController().getCachedNearbyUsers());
    /* access modifiers changed from: private */
    public int usersEmptyRow;
    /* access modifiers changed from: private */
    public int usersEndRow;
    /* access modifiers changed from: private */
    public int usersHeaderRow;
    /* access modifiers changed from: private */
    public int usersSectionRow;
    /* access modifiers changed from: private */
    public int usersStartRow;

    public PeopleNearbyActivity() {
        checkForExpiredLocations(false);
        updateRows();
    }

    private void updateRows() {
        this.rowCount = 0;
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
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
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
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PeopleNearby", R.string.PeopleNearby));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PeopleNearbyActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setTag(Theme.key_windowBackgroundGray);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PeopleNearbyActivity.this.lambda$createView$1$PeopleNearbyActivity(view, i);
            }
        });
        UndoView undoView2 = new UndoView(context);
        this.undoView = undoView2;
        frameLayout.addView(undoView2, LayoutHelper.createFrame(-1.0f, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PeopleNearbyActivity(View view, int position) {
        int chatId;
        int i = this.usersStartRow;
        if (position < i || position >= this.usersEndRow) {
            int i2 = this.chatsStartRow;
            if (position >= i2 && position < this.chatsEndRow) {
                TLRPC.TL_peerLocated peerLocated = this.chats.get(position - i2);
                Bundle args1 = new Bundle();
                if (peerLocated.peer instanceof TLRPC.TL_peerChat) {
                    chatId = peerLocated.peer.chat_id;
                } else {
                    chatId = peerLocated.peer.channel_id;
                }
                args1.putInt("chat_id", chatId);
                presentFragment(new ChatActivity(args1));
            } else if (position != this.chatsCreateRow) {
            } else {
                if (this.checkingCanCreate || this.currentGroupCreateAddress == null) {
                    AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                    this.loadingDialog = alertDialog;
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public final void onCancel(DialogInterface dialogInterface) {
                            PeopleNearbyActivity.this.lambda$null$0$PeopleNearbyActivity(dialogInterface);
                        }
                    });
                    this.loadingDialog.show();
                    return;
                }
                openGroupCreate();
            }
        } else {
            getUserInfo(this.users.get(position - i).peer.user_id);
        }
    }

    public /* synthetic */ void lambda$null$0$PeopleNearbyActivity(DialogInterface dialog) {
        this.loadingDialog = null;
    }

    private void getUserInfo(int userId) {
        TLRPC.TL_users_getFullUser req = new TLRPC.TL_users_getFullUser();
        req.id = getMessagesController().getInputUser(userId);
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PeopleNearbyActivity.this.lambda$getUserInfo$3$PeopleNearbyActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getUserInfo$3$PeopleNearbyActivity(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PeopleNearbyActivity.this.lambda$null$2$PeopleNearbyActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$2$PeopleNearbyActivity(TLObject response) {
        TLRPC.UserFull userFull = (TLRPC.UserFull) response;
        getMessagesController().putUser(userFull.user, false);
        if (userFull.user != null) {
            if (userFull.user.self || userFull.user.contact) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", userFull.user.id);
                presentFragment(new NewProfileActivity(bundle));
                return;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putInt("from_type", 5);
            presentFragment(new AddContactsInfoActivity(bundle2, userFull.user));
        }
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
                    PeopleNearbyActivity.this.lambda$checkCanCreateGroup$5$PeopleNearbyActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$checkCanCreateGroup$5$PeopleNearbyActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PeopleNearbyActivity.this.lambda$null$4$PeopleNearbyActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$PeopleNearbyActivity(TLRPC.TL_error error) {
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
                        RadialProgressView access$200 = cell.progressView;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = show ? 1.0f : 0.0f;
                        animators.add(ObjectAnimator.ofFloat(access$200, property, fArr));
                    }
                }
                if (animators.isEmpty() == 0) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.showProgressAnimation = animatorSet2;
                    animatorSet2.playTogether(animators);
                    this.showProgressAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            AnimatorSet unused = PeopleNearbyActivity.this.showProgressAnimation = null;
                            PeopleNearbyActivity.this.animatingViews.clear();
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
            $$Lambda$PeopleNearbyActivity$x9up9YiwTEWs3W4rtDrAoW0QLFc r0 = new Runnable() {
                public final void run() {
                    PeopleNearbyActivity.this.lambda$sendRequest$6$PeopleNearbyActivity();
                }
            };
            this.showProgressRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 1000);
            this.firstLoaded = true;
        }
        BDLocation location = getLocationController().getLastKnownLocation();
        if (location != null) {
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
                        PeopleNearbyActivity.this.lambda$sendRequest$8$PeopleNearbyActivity(tLObject, tL_error);
                    }
                });
                getConnectionsManager().bindRequestToGuid(this.reqId, this.classGuid);
            }
        }
    }

    public /* synthetic */ void lambda$sendRequest$6$PeopleNearbyActivity() {
        showLoadingProgress(true);
        this.showProgressRunnable = null;
    }

    public /* synthetic */ void lambda$sendRequest$8$PeopleNearbyActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PeopleNearbyActivity.this.lambda$null$7$PeopleNearbyActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$7$PeopleNearbyActivity(TLObject response) {
        this.reqId = 0;
        Runnable runnable = this.showProgressRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showProgressRunnable = null;
        }
        showLoadingProgress(false);
        if (response != null) {
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
                            this.chats.add(peerLocated);
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
            $$Lambda$PeopleNearbyActivity$BHfNhxxjzIIbVG_s749bVlvDoBA r0 = new Runnable(args[2], dialogId, args[3].booleanValue()) {
                private final /* synthetic */ TLRPC.Chat f$1;
                private final /* synthetic */ long f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                }

                public final void run() {
                    PeopleNearbyActivity.this.lambda$didReceivedNotification$9$PeopleNearbyActivity(this.f$1, this.f$2, this.f$3);
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

    public /* synthetic */ void lambda$didReceivedNotification$9$PeopleNearbyActivity(TLRPC.Chat chat, long dialogId, boolean revoke) {
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
            $$Lambda$PeopleNearbyActivity$aHYU1jyVq_Ynnh475N4xe6lub4 r3 = new Runnable() {
                public final void run() {
                    PeopleNearbyActivity.this.lambda$checkForExpiredLocations$10$PeopleNearbyActivity();
                }
            };
            this.checkExpiredRunnable = r3;
            AndroidUtilities.runOnUIThread(r3, (long) ((minExpired - currentTime) * 1000));
        }
    }

    public /* synthetic */ void lambda$checkForExpiredLocations$10$PeopleNearbyActivity() {
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
            return PeopleNearbyActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new ManageChatUserCell(this.mContext, 6, 2, false);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else if (viewType == 1) {
                view = new ShadowSectionCell(this.mContext, 22);
            } else if (viewType == 2) {
                View manageChatTextCell = new ManageChatTextCell(this.mContext);
                manageChatTextCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = manageChatTextCell;
            } else if (viewType == 3) {
                View headerCellProgress = new HeaderCellProgress(this.mContext);
                headerCellProgress.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = headerCellProgress;
            } else if (viewType != 4) {
                view = new HintInnerCell(this.mContext);
            } else {
                AnonymousClass1 r0 = new TextView(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(67.0f), 1073741824));
                    }
                };
                r0.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                r0.setPadding(0, 0, AndroidUtilities.dp(3.0f), 0);
                r0.setTextSize(1, 14.0f);
                r0.setGravity(17);
                r0.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                AnonymousClass1 r1 = r0;
                view = r0;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 3 && !PeopleNearbyActivity.this.animatingViews.contains(holder.itemView)) {
                ((HeaderCellProgress) holder.itemView).progressView.setAlpha(PeopleNearbyActivity.this.showingLoadingProgress ? 1.0f : 0.0f);
            }
        }

        private String formatDistance(TLRPC.TL_peerLocated located) {
            return LocaleController.formatDistance((float) located.distance);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int chatId;
            int itemViewType = holder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                userCell.setTag(Integer.valueOf(position));
                if (position >= PeopleNearbyActivity.this.usersStartRow && position < PeopleNearbyActivity.this.usersEndRow) {
                    int index = position - PeopleNearbyActivity.this.usersStartRow;
                    TLRPC.TL_peerLocated peerLocated = (TLRPC.TL_peerLocated) PeopleNearbyActivity.this.users.get(index);
                    TLRPC.User user = PeopleNearbyActivity.this.getMessagesController().getUser(Integer.valueOf(peerLocated.peer.user_id));
                    if (user != null) {
                        String formatDistance = formatDistance(peerLocated);
                        if (index != PeopleNearbyActivity.this.users.size() - 1) {
                            z = true;
                        }
                        userCell.setData(user, (CharSequence) null, formatDistance, z);
                    }
                } else if (position >= PeopleNearbyActivity.this.chatsStartRow && position < PeopleNearbyActivity.this.chatsEndRow) {
                    int index2 = position - PeopleNearbyActivity.this.chatsStartRow;
                    TLRPC.TL_peerLocated peerLocated2 = (TLRPC.TL_peerLocated) PeopleNearbyActivity.this.chats.get(index2);
                    if (peerLocated2.peer instanceof TLRPC.TL_peerChat) {
                        chatId = peerLocated2.peer.chat_id;
                    } else {
                        chatId = peerLocated2.peer.channel_id;
                    }
                    TLRPC.Chat chat = PeopleNearbyActivity.this.getMessagesController().getChat(Integer.valueOf(chatId));
                    if (chat != null) {
                        String subtitle = formatDistance(peerLocated2);
                        if (chat.participants_count != 0) {
                            subtitle = String.format("%1$s, %2$s", new Object[]{subtitle, LocaleController.formatPluralString("Members", chat.participants_count)});
                        }
                        if (index2 != PeopleNearbyActivity.this.chats.size() - 1) {
                            z = true;
                        }
                        userCell.setData(chat, (CharSequence) null, subtitle, z);
                    }
                }
            } else if (itemViewType == 1) {
                ShadowSectionCell privacyCell = (ShadowSectionCell) holder.itemView;
                if (position == PeopleNearbyActivity.this.usersSectionRow) {
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                } else if (position == PeopleNearbyActivity.this.chatsSectionRow) {
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                }
            } else if (itemViewType == 2) {
                ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                if (position == PeopleNearbyActivity.this.chatsCreateRow) {
                    String string = LocaleController.getString("NearbyCreateGroup", R.string.NearbyCreateGroup);
                    if (PeopleNearbyActivity.this.chatsStartRow != -1) {
                        z = true;
                    }
                    actionCell.setText(string, (String) null, R.drawable.groups_create, z);
                }
            } else if (itemViewType == 3) {
                HeaderCellProgress headerCell = (HeaderCellProgress) holder.itemView;
                if (position == PeopleNearbyActivity.this.usersHeaderRow) {
                    headerCell.setText(LocaleController.getString("PeopleNearbyHeader", R.string.PeopleNearbyHeader));
                } else if (position == PeopleNearbyActivity.this.chatsHeaderRow) {
                    headerCell.setText(LocaleController.getString("ChatsNearbyHeader", R.string.ChatsNearbyHeader));
                }
            } else if (itemViewType == 4) {
                TextView textView = (TextView) holder.itemView;
                if (position == PeopleNearbyActivity.this.usersEmptyRow) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("PeopleNearbyEmpty", R.string.PeopleNearbyEmpty)));
                }
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if (position == PeopleNearbyActivity.this.helpRow) {
                return 5;
            }
            if (position == PeopleNearbyActivity.this.chatsCreateRow) {
                return 2;
            }
            if (position == PeopleNearbyActivity.this.usersHeaderRow || position == PeopleNearbyActivity.this.chatsHeaderRow) {
                return 3;
            }
            if (position == PeopleNearbyActivity.this.usersSectionRow || position == PeopleNearbyActivity.this.chatsSectionRow) {
                return 1;
            }
            if (position == PeopleNearbyActivity.this.usersEmptyRow) {
                return 4;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                PeopleNearbyActivity.this.lambda$getThemeDescriptions$11$PeopleNearbyActivity();
            }
        };
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        Drawable[] drawableArr = {Theme.avatar_savedDrawable};
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
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
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class, TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{HeaderCellProgress.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, drawableArr, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription((View) recyclerListView4, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{HintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_archiveBackground), new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_message), new ThemeDescription((View) recyclerListView6, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) recyclerListView8, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) recyclerListView10, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueButton), new ThemeDescription((View) recyclerListView12, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueIcon), new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_background), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_cancelColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_cancelColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_undo_infoColor)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$11$PeopleNearbyActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
