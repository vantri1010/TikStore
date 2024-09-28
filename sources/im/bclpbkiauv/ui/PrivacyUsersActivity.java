package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ContactsActivity;
import im.bclpbkiauv.ui.PrivacyUsersActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ManageChatTextCell;
import im.bclpbkiauv.ui.cells.ManageChatUserCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.contacts.AddGroupingUserActivity;
import im.bclpbkiauv.ui.hui.decoration.TopBottomDecoration;
import java.util.ArrayList;
import java.util.Iterator;
import org.slf4j.Marker;

public class PrivacyUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ContactsActivity.ContactsActivityDelegate {
    public static final int PRIVACY_RULES_TYPE_ADDED_BY_PHONE = 7;
    public static final int PRIVACY_RULES_TYPE_CALLS = 2;
    public static final int PRIVACY_RULES_TYPE_FORWARDS = 5;
    public static final int PRIVACY_RULES_TYPE_INVITE = 1;
    public static final int PRIVACY_RULES_TYPE_LASTSEEN = 0;
    public static final int PRIVACY_RULES_TYPE_MOMENT = 8;
    public static final int PRIVACY_RULES_TYPE_P2P = 3;
    public static final int PRIVACY_RULES_TYPE_PHONE = 6;
    public static final int PRIVACY_RULES_TYPE_PHOTO = 4;
    /* access modifiers changed from: private */
    public int blockUserDetailRow;
    /* access modifiers changed from: private */
    public int blockUserRow;
    /* access modifiers changed from: private */
    public boolean blockedUsersActivity = true;
    private int currentSubType;
    private int currentType;
    private PrivacyActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private boolean isAlwaysShare;
    private boolean isGroup;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public int rowCount;
    private int rulesType;
    /* access modifiers changed from: private */
    public ArrayList<Integer> uidArray;
    /* access modifiers changed from: private */
    public int usersDetailRow;
    /* access modifiers changed from: private */
    public int usersEndRow;
    /* access modifiers changed from: private */
    public int usersHeaderRow;
    /* access modifiers changed from: private */
    public int usersStartRow;

    public interface PrivacyActivityDelegate {
        void didUpdateUserList(ArrayList<Integer> arrayList, boolean z);
    }

    public PrivacyUsersActivity() {
    }

    public PrivacyUsersActivity(ArrayList<Integer> users, boolean group, boolean always, int rulesType2, int currentType2, int currentSubType2) {
        this.uidArray = users;
        this.isAlwaysShare = always;
        this.isGroup = group;
        this.rulesType = rulesType2;
        this.currentType = currentType2;
        this.currentSubType = currentSubType2;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        if (!this.blockedUsersActivity) {
            return true;
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        if (this.blockedUsersActivity) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.blockedUsersActivity) {
            this.actionBar.setTitle(LocaleController.getString("BlockedUsers", R.string.BlockedUsers));
        } else if (this.isGroup) {
            if (this.isAlwaysShare) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", R.string.NeverAllow));
            }
        } else if (this.isAlwaysShare) {
            this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", R.string.AlwaysShareWithTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PrivacyUsersActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        if (this.blockedUsersActivity) {
            emptyTextProgressView.setText(LocaleController.getString("NoBlocked", R.string.NoBlocked));
        } else {
            emptyTextProgressView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setOverScrollMode(2);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        RecyclerListView recyclerListView4 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView4.setVerticalScrollbarPosition(i);
        this.listView.addItemDecoration(new TopBottomDecoration());
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PrivacyUsersActivity.this.lambda$createView$1$PrivacyUsersActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return PrivacyUsersActivity.this.lambda$createView$2$PrivacyUsersActivity(view, i);
            }
        });
        if (this.blockedUsersActivity) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (!PrivacyUsersActivity.this.getMessagesController().blockedEndReached) {
                        int visibleItemCount = Math.abs(PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() - PrivacyUsersActivity.this.layoutManager.findFirstVisibleItemPosition()) + 1;
                        int totalItemCount = recyclerView.getAdapter().getItemCount();
                        if (visibleItemCount > 0 && PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() >= totalItemCount - 10) {
                            PrivacyUsersActivity.this.getMessagesController().getBlockedUsers(false);
                        }
                    }
                }
            });
        }
        if (getMessagesController().totalBlockedCount < 0) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PrivacyUsersActivity(View view, int position) {
        Integer userId;
        if (position != this.blockUserRow) {
            int i = this.usersStartRow;
            if (position >= i && position < this.usersEndRow) {
                if (this.blockedUsersActivity) {
                    userId = Integer.valueOf(getMessagesController().blockedUsers.keyAt(position - this.usersStartRow));
                } else {
                    userId = this.uidArray.get(position - i);
                    if (userId.intValue() < 0) {
                        userId = Integer.valueOf(-userId.intValue());
                    }
                }
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(userId);
                if (user != null) {
                    Bundle args = new Bundle();
                    args.putInt("user_id", userId.intValue());
                    if (user.contact) {
                        presentFragment(new NewProfileActivity(args));
                    } else {
                        presentFragment(new AddContactsInfoActivity((Bundle) null, user));
                    }
                }
            }
        } else if (this.blockedUsersActivity) {
            presentFragment(new DialogOrContactPickerActivity());
        } else {
            AddGroupingUserActivity fragment = new AddGroupingUserActivity(new ArrayList<>(), 1, LocaleController.getString("EmpryUsersPlaceholder", R.string.EmpryUsersPlaceholder), false);
            fragment.setDelegate(new AddGroupingUserActivity.AddGroupingUserActivityDelegate() {
                public final void didSelectedContact(ArrayList arrayList) {
                    PrivacyUsersActivity.this.lambda$null$0$PrivacyUsersActivity(arrayList);
                }
            });
            presentFragment(fragment);
        }
    }

    public /* synthetic */ void lambda$null$0$PrivacyUsersActivity(ArrayList users) {
        ArrayList<Integer> ids = new ArrayList<>();
        if (users != null && users.size() > 0) {
            Iterator it = users.iterator();
            while (it.hasNext()) {
                TLRPC.User user = (TLRPC.User) it.next();
                if (user != null && user.id > 0) {
                    ids.add(Integer.valueOf(user.id));
                }
            }
        }
        Iterator<Integer> it2 = ids.iterator();
        while (it2.hasNext()) {
            Integer id1 = it2.next();
            if (!this.uidArray.contains(id1)) {
                this.uidArray.add(id1);
            }
        }
        if (!this.blockedUsersActivity) {
            processDone();
        }
        updateRows();
        PrivacyActivityDelegate privacyActivityDelegate = this.delegate;
        if (privacyActivityDelegate != null) {
            privacyActivityDelegate.didUpdateUserList(this.uidArray, true);
        }
    }

    public /* synthetic */ boolean lambda$createView$2$PrivacyUsersActivity(View view, int position) {
        int i = this.usersStartRow;
        if (position < i || position >= this.usersEndRow) {
            return false;
        }
        if (this.blockedUsersActivity) {
            showUnblockAlert(getMessagesController().blockedUsers.keyAt(position - this.usersStartRow));
            return true;
        }
        showUnblockAlert(this.uidArray.get(position - i).intValue());
        return true;
    }

    private void processDone() {
        if (getParentActivity() != null) {
            if (this.currentType != 0 && this.rulesType == 0) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (!preferences.getBoolean("privacyAlertShowed", false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (this.rulesType == 1) {
                        builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", R.string.WhoCanAddMeInfo));
                    } else {
                        builder.setMessage(LocaleController.getString("CustomHelp", R.string.CustomHelp));
                    }
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(preferences) {
                        private final /* synthetic */ SharedPreferences f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PrivacyUsersActivity.this.lambda$processDone$3$PrivacyUsersActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                    return;
                }
            }
            applyCurrentPrivacySettings();
        }
    }

    public /* synthetic */ void lambda$processDone$3$PrivacyUsersActivity(SharedPreferences preferences, DialogInterface dialogInterface, int i) {
        applyCurrentPrivacySettings();
        preferences.edit().putBoolean("privacyAlertShowed", true).commit();
    }

    private void applyCurrentPrivacySettings() {
        TLRPC.InputUser inputUser;
        TLRPC.InputUser inputUser2;
        TLRPC.TL_account_setPrivacy req = new TLRPC.TL_account_setPrivacy();
        int i = this.rulesType;
        if (i == 6) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneNumber();
            if (this.currentType == 1) {
                TLRPC.TL_account_setPrivacy req2 = new TLRPC.TL_account_setPrivacy();
                req2.key = new TLRPC.TL_inputPrivacyKeyAddedByPhone();
                if (this.currentSubType == 0) {
                    req2.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
                } else {
                    req2.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PrivacyUsersActivity.this.lambda$applyCurrentPrivacySettings$5$PrivacyUsersActivity(tLObject, tL_error);
                    }
                }, 2);
            }
        } else if (i == 5) {
            req.key = new TLRPC.TL_inputPrivacyKeyForwards();
        } else if (i == 4) {
            req.key = new TLRPC.TL_inputPrivacyKeyProfilePhoto();
        } else if (i == 3) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneP2P();
        } else if (i == 2) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
        } else if (i == 1) {
            req.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
        } else if (i == 8) {
            req.key = new TLRPC.TL_inputPrivacyKeyMoment();
        } else {
            req.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.uidArray.size() > 0) {
            TLRPC.TL_inputPrivacyValueAllowUsers usersRule = new TLRPC.TL_inputPrivacyValueAllowUsers();
            TLRPC.TL_inputPrivacyValueAllowChatParticipants chatsRule = new TLRPC.TL_inputPrivacyValueAllowChatParticipants();
            for (int a = 0; a < this.uidArray.size(); a++) {
                int id = this.uidArray.get(a).intValue();
                if (id > 0) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(id));
                    if (!(user == null || (inputUser2 = MessagesController.getInstance(this.currentAccount).getInputUser(user)) == null)) {
                        usersRule.users.add(inputUser2);
                    }
                } else {
                    chatsRule.chats.add(Integer.valueOf(-id));
                }
            }
            req.rules.add(usersRule);
            req.rules.add(chatsRule);
        } else if (this.currentType != 1 && this.uidArray.size() > 0) {
            TLRPC.TL_inputPrivacyValueDisallowUsers usersRule2 = new TLRPC.TL_inputPrivacyValueDisallowUsers();
            TLRPC.TL_inputPrivacyValueDisallowChatParticipants chatsRule2 = new TLRPC.TL_inputPrivacyValueDisallowChatParticipants();
            for (int a2 = 0; a2 < this.uidArray.size(); a2++) {
                int id2 = this.uidArray.get(a2).intValue();
                if (id2 > 0) {
                    TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(id2));
                    if (!(user2 == null || (inputUser = getMessagesController().getInputUser(user2)) == null)) {
                        usersRule2.users.add(inputUser);
                    }
                } else {
                    chatsRule2.chats.add(Integer.valueOf(-id2));
                }
            }
            req.rules.add(usersRule2);
            req.rules.add(chatsRule2);
        }
        int i2 = this.currentType;
        if (i2 == 0) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
        } else if (i2 == 1) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueDisallowAll());
        } else if (i2 == 2) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacyUsersActivity.this.lambda$applyCurrentPrivacySettings$7$PrivacyUsersActivity(tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$applyCurrentPrivacySettings$5$PrivacyUsersActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PrivacyUsersActivity.this.lambda$null$4$PrivacyUsersActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$PrivacyUsersActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(((TLRPC.TL_account_privacyRules) response).rules, 7);
        }
    }

    public /* synthetic */ void lambda$applyCurrentPrivacySettings$7$PrivacyUsersActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PrivacyUsersActivity.this.lambda$null$6$PrivacyUsersActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$PrivacyUsersActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_account_privacyRules privacyRules = (TLRPC.TL_account_privacyRules) response;
            MessagesController.getInstance(this.currentAccount).putUsers(privacyRules.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(privacyRules.chats, false);
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(privacyRules.rules, this.rulesType);
            return;
        }
        showErrorAlert();
    }

    private void showErrorAlert() {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", R.string.PrivacyFloodControlError));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public void setDelegate(PrivacyActivityDelegate privacyActivityDelegate) {
        this.delegate = privacyActivityDelegate;
    }

    /* access modifiers changed from: private */
    public void showUnblockAlert(int uid) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setItems(this.blockedUsersActivity ? new CharSequence[]{LocaleController.getString("Unblock", R.string.Unblock)} : new CharSequence[]{LocaleController.getString("Delete", R.string.Delete)}, new DialogInterface.OnClickListener(uid) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    PrivacyUsersActivity.this.lambda$showUnblockAlert$8$PrivacyUsersActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showUnblockAlert$8$PrivacyUsersActivity(int uid, DialogInterface dialogInterface, int i) {
        if (i != 0) {
            return;
        }
        if (this.blockedUsersActivity) {
            getMessagesController().unblockUser(uid);
            return;
        }
        this.uidArray.remove(Integer.valueOf(uid));
        if (!this.blockedUsersActivity) {
            processDone();
        }
        updateRows();
        PrivacyActivityDelegate privacyActivityDelegate = this.delegate;
        if (privacyActivityDelegate != null) {
            privacyActivityDelegate.didUpdateUserList(this.uidArray, false);
        }
        if (this.uidArray.isEmpty()) {
            finishFragment();
        }
    }

    private void updateRows() {
        int count;
        this.rowCount = 0;
        if (!this.blockedUsersActivity || getMessagesController().totalBlockedCount >= 0) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.blockUserRow = i;
            this.rowCount = i2 + 1;
            this.blockUserDetailRow = i2;
            if (this.blockedUsersActivity) {
                count = getMessagesController().blockedUsers.size();
            } else {
                count = this.uidArray.size();
            }
            if (count != 0) {
                int i3 = this.rowCount;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.usersHeaderRow = i3;
                this.usersStartRow = i4;
                int i5 = i4 + count;
                this.rowCount = i5;
                this.usersEndRow = i5;
                this.rowCount = i5 + 1;
                this.usersDetailRow = i5;
            } else {
                this.usersHeaderRow = -1;
                this.usersStartRow = -1;
                this.usersEndRow = -1;
                this.usersDetailRow = -1;
            }
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0) {
                updateVisibleRows(mask);
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            this.emptyView.showTextView();
            updateRows();
        }
    }

    private void updateVisibleRows(int mask) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(mask);
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didSelectContact(TLRPC.User user, String param, ContactsActivity activity) {
        if (user != null) {
            getMessagesController().blockUser(user.id);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PrivacyUsersActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType == 0 || viewType == 2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new ManageChatUserCell(this.mContext, 7, 6, true);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((ManageChatUserCell) view2).setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                        return PrivacyUsersActivity.ListAdapter.this.lambda$onCreateViewHolder$0$PrivacyUsersActivity$ListAdapter(manageChatUserCell, z);
                    }
                });
                view = view2;
            } else if (viewType == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (viewType != 2) {
                HeaderCell headerCell = new HeaderCell(this.mContext, false, 21, 11, false);
                headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                headerCell.setHeight(43);
                view = headerCell;
            } else {
                View manageChatTextCell = new ManageChatTextCell(this.mContext);
                manageChatTextCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = manageChatTextCell;
            }
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$0$PrivacyUsersActivity$ListAdapter(ManageChatUserCell cell, boolean click) {
            if (!click) {
                return true;
            }
            PrivacyUsersActivity.this.showUnblockAlert(((Integer) cell.getTag()).intValue());
            return true;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int uid;
            String subtitle;
            String number;
            int itemViewType = holder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                if (PrivacyUsersActivity.this.blockedUsersActivity) {
                    uid = PrivacyUsersActivity.this.getMessagesController().blockedUsers.keyAt(position - PrivacyUsersActivity.this.usersStartRow);
                } else {
                    uid = ((Integer) PrivacyUsersActivity.this.uidArray.get(position - PrivacyUsersActivity.this.usersStartRow)).intValue();
                }
                userCell.setTag(Integer.valueOf(uid));
                if (uid > 0) {
                    TLRPC.User user = PrivacyUsersActivity.this.getMessagesController().getUser(Integer.valueOf(uid));
                    if (user != null) {
                        if (user.bot) {
                            number = LocaleController.getString("Bot", R.string.Bot).substring(0, 1).toUpperCase() + LocaleController.getString("Bot", R.string.Bot).substring(1);
                        } else if (user.phone == null || user.phone.length() == 0) {
                            number = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                        } else {
                            number = PhoneFormat.getInstance().format(Marker.ANY_NON_NULL_MARKER + user.phone);
                        }
                        if (position != PrivacyUsersActivity.this.usersEndRow - 1) {
                            z = true;
                        }
                        userCell.setData(user, (CharSequence) null, number, z);
                    }
                } else {
                    TLRPC.Chat chat = PrivacyUsersActivity.this.getMessagesController().getChat(Integer.valueOf(-uid));
                    if (chat != null) {
                        if (chat.participants_count != 0) {
                            subtitle = LocaleController.formatPluralString("Members", chat.participants_count);
                        } else if (chat.has_geo) {
                            subtitle = LocaleController.getString("MegaLocation", R.string.MegaLocation);
                        } else if (TextUtils.isEmpty(chat.username)) {
                            subtitle = LocaleController.getString("MegaPrivate", R.string.MegaPrivate);
                        } else {
                            subtitle = LocaleController.getString("MegaPublic", R.string.MegaPublic);
                        }
                        if (position != PrivacyUsersActivity.this.usersEndRow - 1) {
                            z = true;
                        }
                        userCell.setData(chat, (CharSequence) null, subtitle, z);
                    }
                }
                if (position == PrivacyUsersActivity.this.usersEndRow - 1) {
                    userCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                if (position == PrivacyUsersActivity.this.blockUserDetailRow) {
                    if (PrivacyUsersActivity.this.blockedUsersActivity) {
                        privacyCell.setText(LocaleController.getString("BlockedUsersInfo", R.string.BlockedUsersInfo));
                    } else {
                        privacyCell.setText((CharSequence) null);
                    }
                } else if (position == PrivacyUsersActivity.this.usersDetailRow) {
                    privacyCell.setText("");
                }
            } else if (itemViewType == 2) {
                ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                if (PrivacyUsersActivity.this.blockedUsersActivity) {
                    actionCell.setText(LocaleController.getString("BlockUser", R.string.BlockUser), (String) null, R.drawable.actions_addmember2, false);
                } else {
                    actionCell.setText(LocaleController.getString("PrivacyAddAnException", R.string.PrivacyAddAnException), (String) null, R.drawable.actions_addmember2, false);
                }
                actionCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
            } else if (itemViewType == 3) {
                HeaderCell headerCell = (HeaderCell) holder.itemView;
                if (position == PrivacyUsersActivity.this.usersHeaderRow) {
                    if (PrivacyUsersActivity.this.blockedUsersActivity) {
                        headerCell.setText(LocaleController.formatPluralString("BlockedUsersCount", PrivacyUsersActivity.this.getMessagesController().totalBlockedCount));
                    } else {
                        headerCell.setText(LocaleController.getString("PrivacyExceptions", R.string.PrivacyExceptions));
                    }
                    headerCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == PrivacyUsersActivity.this.usersHeaderRow) {
                return 3;
            }
            if (position == PrivacyUsersActivity.this.blockUserRow) {
                return 2;
            }
            if (position == PrivacyUsersActivity.this.blockUserDetailRow || position == PrivacyUsersActivity.this.usersDetailRow) {
                return 1;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                PrivacyUsersActivity.this.lambda$getThemeDescriptions$9$PrivacyUsersActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        RecyclerListView recyclerListView3 = this.listView;
        RecyclerListView recyclerListView4 = recyclerListView3;
        RecyclerListView recyclerListView5 = this.listView;
        RecyclerListView recyclerListView6 = recyclerListView5;
        RecyclerListView recyclerListView7 = this.listView;
        RecyclerListView recyclerListView8 = recyclerListView7;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) recyclerListView4, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) recyclerListView6, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueButton), new ThemeDescription((View) recyclerListView8, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueIcon)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$9$PrivacyUsersActivity() {
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
