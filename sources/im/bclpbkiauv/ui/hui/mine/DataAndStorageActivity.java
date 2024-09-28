package im.bclpbkiauv.ui.hui.mine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PasscodeActivity;
import im.bclpbkiauv.ui.PrivacyControlActivity;
import im.bclpbkiauv.ui.PrivacyUsersActivity;
import im.bclpbkiauv.ui.TwoStepVerificationActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.DialogCommonList;
import im.bclpbkiauv.ui.hcells.TextSettingCell;
import im.bclpbkiauv.ui.hui.mine.DataAndStorageActivity;
import im.bclpbkiauv.ui.hviews.sliding.SlidingLayout;
import java.util.ArrayList;
import java.util.List;

public class DataAndStorageActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int accountSectionRow;
    private int autoDownloadHeaderRow;
    private int autoGifRow;
    private int autoPlayHeaderRow;
    private int autoSavePicRow;
    private int autoVideoRow;
    /* access modifiers changed from: private */
    public int blockedRow;
    /* access modifiers changed from: private */
    public int callsRow;
    private boolean[] clear = new boolean[2];
    private boolean currentSuggest;
    private boolean currentSync;
    /* access modifiers changed from: private */
    public int dataUsageDetailRow;
    /* access modifiers changed from: private */
    public int dataUsageRow;
    /* access modifiers changed from: private */
    public int deleteAccountDetailRow;
    /* access modifiers changed from: private */
    public int deleteAccountRow;
    private int downloadBackgroundRow;
    private int emptyRow;
    /* access modifiers changed from: private */
    public int forwardsRow;
    /* access modifiers changed from: private */
    public int groupsDetailRow;
    /* access modifiers changed from: private */
    public int groupsRow;
    /* access modifiers changed from: private */
    public int lastSeenRow;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    private RecyclerListView listView;
    private int msgPreviewRow;
    private int networkRow;
    private boolean newSuggest;
    private boolean newSync;
    private int otherHeaderRow;
    /* access modifiers changed from: private */
    public int passcodeRow;
    /* access modifiers changed from: private */
    public int passwordRow;
    /* access modifiers changed from: private */
    public int phoneNumberRow;
    /* access modifiers changed from: private */
    public int privacySectionRow;
    /* access modifiers changed from: private */
    public int profilePhotoRow;
    private int proxyHeaderRow;
    private int proxyRow;
    private int resetRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int securitySectionRow;
    /* access modifiers changed from: private */
    public int sessionsDetailRow;
    /* access modifiers changed from: private */
    public int sessionsRow;
    private int storageRow;
    private int useMobileRow;
    private int useWifiRow;
    private int userLessRow;
    private int voiceHeaderRow;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getContactsController().loadPrivacySettings();
        getMessagesController().getBlockedUsers(true);
        boolean z = getUserConfig().syncContacts;
        this.newSync = z;
        this.currentSync = z;
        boolean z2 = getUserConfig().suggestContacts;
        this.newSuggest = z2;
        this.currentSuggest = z2;
        updateRows();
        loadPasswordSettings();
        getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        if (this.currentSync != this.newSync) {
            getUserConfig().syncContacts = this.newSync;
            getUserConfig().saveConfig(false);
            if (this.newSync) {
                getContactsController().forceImportContacts();
                if (getParentActivity() != null) {
                    ToastUtils.show((int) R.string.SyncContactsAdded);
                }
            }
        }
        boolean z = this.newSuggest;
        if (z != this.currentSuggest) {
            if (!z) {
                getMediaDataController().clearTopPeers();
            }
            getUserConfig().suggestContacts = this.newSuggest;
            getUserConfig().saveConfig(false);
            TLRPC.TL_contacts_toggleTopPeers req = new TLRPC.TL_contacts_toggleTopPeers();
            req.enabled = this.newSuggest;
            getConnectionsManager().sendRequest(req, $$Lambda$DataAndStorageActivity$LfKZD6PztcLTYpWVD9o6VPYLTj4.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject response, TLRPC.TL_error error) {
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PrivacySettings", R.string.PrivacySettings));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DataAndStorageActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new SlidingLayout(context);
        SlidingLayout slidingLayout = (SlidingLayout) this.fragmentView;
        slidingLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        slidingLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                DataAndStorageActivity.this.lambda$createView$1$DataAndStorageActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$DataAndStorageActivity(View view, int position) {
        if (view.isEnabled()) {
            if (position == this.blockedRow) {
                presentFragment(new PrivacyUsersActivity());
            } else if (position == this.sessionsRow) {
                presentFragment(new MrySessionsActivity(0));
            } else if (position == this.lastSeenRow) {
                presentFragment(new PrivacyControlActivity(0));
            } else if (position == this.phoneNumberRow) {
                presentFragment(new PrivacyControlActivity(6));
            } else if (position == this.groupsRow) {
                presentFragment(new PrivacyControlActivity(1));
            } else if (position == this.callsRow) {
                presentFragment(new PrivacyControlActivity(2));
            } else if (position == this.profilePhotoRow) {
                presentFragment(new PrivacyControlActivity(4));
            } else if (position == this.forwardsRow) {
                presentFragment(new PrivacyControlActivity(5));
            } else if (position == this.passwordRow) {
                presentFragment(new TwoStepVerificationActivity(0));
            } else if (position == this.passcodeRow) {
                if (SharedConfig.passcodeHash.length() > 0) {
                    presentFragment(new PasscodeActivity(2));
                } else {
                    presentFragment(new PasscodeActivity(0));
                }
            } else if (position == this.dataUsageRow) {
                presentFragment(new DataUsageActivity());
            } else if (position == this.deleteAccountRow) {
                List<String> list = new ArrayList<>();
                list.add(LocaleController.formatPluralString("Months", 1));
                list.add(LocaleController.formatPluralString("Months", 3));
                list.add(LocaleController.formatPluralString("Months", 6));
                list.add(LocaleController.formatPluralString("Years", 1));
                new DialogCommonList((Activity) getParentActivity(), list, (List<Integer>) null, 0, (DialogCommonList.RecyclerviewItemClickCallBack) new DialogCommonList.RecyclerviewItemClickCallBack() {
                    public void onRecyclerviewItemClick(int position) {
                        int value = 0;
                        if (position == 0) {
                            value = 30;
                        } else if (position == 1) {
                            value = 90;
                        } else if (position == 2) {
                            value = 182;
                        } else if (position == 3) {
                            value = 365;
                        }
                        TLRPC.TL_account_setAccountTTL req = new TLRPC.TL_account_setAccountTTL();
                        req.ttl = new TLRPC.TL_accountDaysTTL();
                        req.ttl.days = value;
                        DataAndStorageActivity.this.getConnectionsManager().sendRequest(req, new RequestDelegate(req) {
                            private final /* synthetic */ TLRPC.TL_account_setAccountTTL f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                DataAndStorageActivity.AnonymousClass3.this.lambda$onRecyclerviewItemClick$1$DataAndStorageActivity$3(this.f$1, tLObject, tL_error);
                            }
                        });
                    }

                    public /* synthetic */ void lambda$onRecyclerviewItemClick$1$DataAndStorageActivity$3(TLRPC.TL_account_setAccountTTL req, TLObject response, TLRPC.TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable(response, req) {
                            private final /* synthetic */ TLObject f$1;
                            private final /* synthetic */ TLRPC.TL_account_setAccountTTL f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                DataAndStorageActivity.AnonymousClass3.this.lambda$null$0$DataAndStorageActivity$3(this.f$1, this.f$2);
                            }
                        });
                    }

                    public /* synthetic */ void lambda$null$0$DataAndStorageActivity$3(TLObject response, TLRPC.TL_account_setAccountTTL req) {
                        if (response instanceof TLRPC.TL_boolTrue) {
                            DataAndStorageActivity.this.getContactsController().setDeleteAccountTTL(req.ttl.days);
                            DataAndStorageActivity.this.listAdapter.notifyDataSetChanged();
                        }
                    }
                }, 1).show();
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.privacyRulesUpdated) {
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            this.listAdapter.notifyItemChanged(this.blockedRow);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.securitySectionRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.blockedRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.sessionsRow = i2;
        this.passwordRow = -1;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.passcodeRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.sessionsDetailRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.privacySectionRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.phoneNumberRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.lastSeenRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.callsRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.groupsRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.groupsDetailRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.accountSectionRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.deleteAccountRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.deleteAccountDetailRow = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.dataUsageRow = i14;
        this.rowCount = i15 + 1;
        this.dataUsageDetailRow = i15;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void loadPasswordSettings() {
        if (!getUserConfig().hasSecureData) {
            getConnectionsManager().sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    DataAndStorageActivity.this.lambda$loadPasswordSettings$3$DataAndStorageActivity(tLObject, tL_error);
                }
            }, 10);
        }
    }

    public /* synthetic */ void lambda$loadPasswordSettings$3$DataAndStorageActivity(TLObject response, TLRPC.TL_error error) {
        if (response != null && ((TLRPC.TL_account_password) response).has_secure_values) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    DataAndStorageActivity.this.lambda$null$2$DataAndStorageActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$2$DataAndStorageActivity() {
        getUserConfig().hasSecureData = true;
        getUserConfig().saveConfig(false);
        updateRows();
    }

    public static String formatRulesString(AccountInstance accountInstance, int rulesType) {
        int i = rulesType;
        ArrayList<TLRPC.PrivacyRule> privacyRules = accountInstance.getContactsController().getPrivacyRules(i);
        if (privacyRules.size() != 0) {
            int type = -1;
            int plus = 0;
            int minus = 0;
            for (int a = 0; a < privacyRules.size(); a++) {
                TLRPC.PrivacyRule rule = privacyRules.get(a);
                if (rule instanceof TLRPC.TL_privacyValueAllowChatParticipants) {
                    TLRPC.TL_privacyValueAllowChatParticipants participants = (TLRPC.TL_privacyValueAllowChatParticipants) rule;
                    int N = participants.chats.size();
                    for (int b = 0; b < N; b++) {
                        TLRPC.Chat chat = accountInstance.getMessagesController().getChat(participants.chats.get(b));
                        if (chat != null) {
                            plus += chat.participants_count;
                        }
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowChatParticipants) {
                    TLRPC.TL_privacyValueDisallowChatParticipants participants2 = (TLRPC.TL_privacyValueDisallowChatParticipants) rule;
                    int N2 = participants2.chats.size();
                    for (int b2 = 0; b2 < N2; b2++) {
                        TLRPC.Chat chat2 = accountInstance.getMessagesController().getChat(participants2.chats.get(b2));
                        if (chat2 != null) {
                            minus += chat2.participants_count;
                        }
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueAllowUsers) {
                    plus += ((TLRPC.TL_privacyValueAllowUsers) rule).users.size();
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowUsers) {
                    minus += ((TLRPC.TL_privacyValueDisallowUsers) rule).users.size();
                } else if (type == -1) {
                    if (rule instanceof TLRPC.TL_privacyValueAllowAll) {
                        type = 0;
                    } else if (rule instanceof TLRPC.TL_privacyValueDisallowAll) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
            }
            if (type == 0 || (type == -1 && minus > 0)) {
                if (i == 3) {
                    if (minus == 0) {
                        return LocaleController.getString("P2PEverybody", R.string.P2PEverybody);
                    }
                    return LocaleController.formatString("P2PEverybodyMinus", R.string.P2PEverybodyMinus, Integer.valueOf(minus));
                } else if (minus == 0) {
                    return LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody);
                } else {
                    return LocaleController.formatString("LastSeenEverybodyMinus", R.string.LastSeenEverybodyMinus, Integer.valueOf(minus));
                }
            } else if (type == 2 || (type == -1 && minus > 0 && plus > 0)) {
                if (i == 3) {
                    if (plus == 0 && minus == 0) {
                        return LocaleController.getString("P2PContacts", R.string.P2PContacts);
                    }
                    if (plus != 0 && minus != 0) {
                        return LocaleController.formatString("P2PContactsMinusPlus", R.string.P2PContactsMinusPlus, Integer.valueOf(minus), Integer.valueOf(plus));
                    } else if (minus != 0) {
                        return LocaleController.formatString("P2PContactsMinus", R.string.P2PContactsMinus, Integer.valueOf(minus));
                    } else {
                        return LocaleController.formatString("P2PContactsPlus", R.string.P2PContactsPlus, Integer.valueOf(plus));
                    }
                } else if (plus == 0 && minus == 0) {
                    return LocaleController.getString("LastSeenContacts", R.string.LastSeenContacts);
                } else {
                    if (plus != 0 && minus != 0) {
                        return LocaleController.formatString("LastSeenContactsMinusPlus", R.string.LastSeenContactsMinusPlus, Integer.valueOf(minus), Integer.valueOf(plus));
                    } else if (minus != 0) {
                        return LocaleController.formatString("LastSeenContactsMinus", R.string.LastSeenContactsMinus, Integer.valueOf(minus));
                    } else {
                        return LocaleController.formatString("LastSeenContactsPlus", R.string.LastSeenContactsPlus, Integer.valueOf(plus));
                    }
                }
            } else if (type != 1 && plus <= 0) {
                return "unknown";
            } else {
                if (i == 3) {
                    if (plus == 0) {
                        return LocaleController.getString("P2PNobody", R.string.P2PNobody);
                    }
                    return LocaleController.formatString("P2PNobodyPlus", R.string.P2PNobodyPlus, Integer.valueOf(plus));
                } else if (plus == 0) {
                    return LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
                } else {
                    return LocaleController.formatString("LastSeenNobodyPlus", R.string.LastSeenNobodyPlus, Integer.valueOf(plus));
                }
            }
        } else if (i == 3) {
            return LocaleController.getString("P2PNobody", R.string.P2PNobody);
        } else {
            return LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == DataAndStorageActivity.this.passcodeRow || position == DataAndStorageActivity.this.passwordRow || position == DataAndStorageActivity.this.blockedRow || position == DataAndStorageActivity.this.sessionsRow || position == DataAndStorageActivity.this.dataUsageRow || (position == DataAndStorageActivity.this.groupsRow && !DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(1)) || ((position == DataAndStorageActivity.this.lastSeenRow && !DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(0)) || ((position == DataAndStorageActivity.this.callsRow && !DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(2)) || ((position == DataAndStorageActivity.this.profilePhotoRow && !DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(4)) || ((position == DataAndStorageActivity.this.forwardsRow && !DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(5)) || ((position == DataAndStorageActivity.this.phoneNumberRow && !DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(6)) || (position == DataAndStorageActivity.this.deleteAccountRow && !DataAndStorageActivity.this.getContactsController().getLoadingDeleteInfo()))))));
        }

        public int getItemCount() {
            return DataAndStorageActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new TextSettingCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (viewType != 2) {
                view = new TextCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            String value2;
            String value3;
            String value4;
            String value5;
            String value6;
            String value7;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingCell textCell = (TextSettingCell) holder.itemView;
                if (position == DataAndStorageActivity.this.sessionsRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("SessionsTitle", R.string.SessionsTitle), "", R.mipmap.ic_privacy_sessions, true, true);
                } else if (position == DataAndStorageActivity.this.passwordRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification), "", R.mipmap.ic_privacy_two_steps, true, true);
                } else if (position == DataAndStorageActivity.this.passcodeRow) {
                    textCell.setTextAndValueAndIcon(LocaleController.getString("Passcode", R.string.Passcode), "", R.mipmap.ic_privacy_lock_code, false, true);
                } else if (position == DataAndStorageActivity.this.blockedRow) {
                    int totalCount = DataAndStorageActivity.this.getMessagesController().totalBlockedCount;
                    if (totalCount == 0) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), LocaleController.getString("BlockedEmpty", R.string.BlockedEmpty), R.mipmap.ic_privacy_block, true, true);
                    } else if (totalCount > 0) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), String.format("%d", new Object[]{Integer.valueOf(totalCount)}), R.mipmap.ic_privacy_block, true, true);
                    } else {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("BlockedUsers", R.string.BlockedUsers), (int) R.mipmap.ic_privacy_block, true, true);
                    }
                } else if (position == DataAndStorageActivity.this.phoneNumberRow) {
                    if (DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(6)) {
                        value7 = LocaleController.getString("Loading", R.string.Loading);
                    } else {
                        value7 = DataAndStorageActivity.formatRulesString(DataAndStorageActivity.this.getAccountInstance(), 6);
                    }
                    textCell.setTextAndValue(LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone), value7, true, true);
                } else if (position == DataAndStorageActivity.this.lastSeenRow) {
                    if (DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(0)) {
                        value6 = LocaleController.getString("Loading", R.string.Loading);
                    } else {
                        value6 = DataAndStorageActivity.formatRulesString(DataAndStorageActivity.this.getAccountInstance(), 0);
                    }
                    textCell.setTextAndValue(LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen), value6, true, true);
                } else if (position == DataAndStorageActivity.this.groupsRow) {
                    if (DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(1)) {
                        value5 = LocaleController.getString("Loading", R.string.Loading);
                    } else {
                        value5 = DataAndStorageActivity.formatRulesString(DataAndStorageActivity.this.getAccountInstance(), 1);
                    }
                    textCell.setTextAndValue(LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels), value5, false, true);
                } else if (position == DataAndStorageActivity.this.callsRow) {
                    if (DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(2)) {
                        value4 = LocaleController.getString("Loading", R.string.Loading);
                    } else {
                        value4 = DataAndStorageActivity.formatRulesString(DataAndStorageActivity.this.getAccountInstance(), 2);
                    }
                    textCell.setTextAndValue(LocaleController.getString("Calls", R.string.Calls), value4, true, true);
                } else if (position == DataAndStorageActivity.this.profilePhotoRow) {
                    if (DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(4)) {
                        value3 = LocaleController.getString("Loading", R.string.Loading);
                    } else {
                        value3 = DataAndStorageActivity.formatRulesString(DataAndStorageActivity.this.getAccountInstance(), 4);
                    }
                    textCell.setTextAndValue(LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto), value3, true, true);
                } else if (position == DataAndStorageActivity.this.forwardsRow) {
                    if (DataAndStorageActivity.this.getContactsController().getLoadingPrivicyInfo(5)) {
                        value2 = LocaleController.getString("Loading", R.string.Loading);
                    } else {
                        value2 = DataAndStorageActivity.formatRulesString(DataAndStorageActivity.this.getAccountInstance(), 5);
                    }
                    textCell.setTextAndValue(LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards), value2, true, true);
                } else if (position == DataAndStorageActivity.this.deleteAccountRow) {
                    if (DataAndStorageActivity.this.getContactsController().getLoadingDeleteInfo()) {
                        value = LocaleController.getString("Loading", R.string.Loading);
                    } else {
                        int ttl = DataAndStorageActivity.this.getContactsController().getDeleteAccountTTL();
                        if (ttl <= 182) {
                            value = LocaleController.formatPluralString("Months", ttl / 30);
                        } else if (ttl == 365) {
                            value = LocaleController.formatPluralString("Years", ttl / 365);
                        } else {
                            value = LocaleController.formatPluralString("Days", ttl);
                        }
                    }
                    textCell.setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor2", R.string.DeleteAccountIfAwayFor2), value, false, true);
                } else if (position == DataAndStorageActivity.this.dataUsageRow) {
                    textCell.setText(LocaleController.getString("DataUsageSetting", R.string.DataUsageSetting), false, true);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                if (position == DataAndStorageActivity.this.deleteAccountDetailRow) {
                    privacyCell.setText(LocaleController.getString("DeleteAccountHelp", R.string.DeleteAccountHelp));
                    privacyCell.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                } else if (position == DataAndStorageActivity.this.groupsDetailRow) {
                    privacyCell.setText(LocaleController.getString("GroupsAndChannelsHelp", R.string.GroupsAndChannelsHelp));
                    privacyCell.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                } else if (position == DataAndStorageActivity.this.sessionsDetailRow) {
                    privacyCell.setText(LocaleController.getString("SessionsInfo", R.string.SessionsInfo));
                    privacyCell.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                } else if (position == DataAndStorageActivity.this.dataUsageDetailRow) {
                    privacyCell.setText(LocaleController.getString("DataUsageDetailText", R.string.DataUsageDetailText));
                    privacyCell.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                }
            } else if (itemViewType == 2) {
                HeaderCell headerCell = (HeaderCell) holder.itemView;
                if (position == DataAndStorageActivity.this.privacySectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyTitle", R.string.PrivacyTitle));
                } else if (position == DataAndStorageActivity.this.securitySectionRow) {
                    headerCell.setText(LocaleController.getString("SecurityTitle", R.string.SecurityTitle));
                } else if (position == DataAndStorageActivity.this.accountSectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyAdvanced", R.string.PrivacyAdvanced));
                }
            } else if (itemViewType == 3) {
                TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
            }
        }

        public int getItemViewType(int position) {
            if (position == DataAndStorageActivity.this.lastSeenRow || position == DataAndStorageActivity.this.phoneNumberRow || position == DataAndStorageActivity.this.blockedRow || position == DataAndStorageActivity.this.deleteAccountRow || position == DataAndStorageActivity.this.sessionsRow || position == DataAndStorageActivity.this.passwordRow || position == DataAndStorageActivity.this.passcodeRow || position == DataAndStorageActivity.this.groupsRow || position == DataAndStorageActivity.this.dataUsageRow) {
                return 0;
            }
            if (position == DataAndStorageActivity.this.deleteAccountDetailRow || position == DataAndStorageActivity.this.groupsDetailRow || position == DataAndStorageActivity.this.sessionsDetailRow || position == DataAndStorageActivity.this.dataUsageDetailRow) {
                return 1;
            }
            if (position == DataAndStorageActivity.this.securitySectionRow || position == DataAndStorageActivity.this.accountSectionRow || position == DataAndStorageActivity.this.privacySectionRow) {
                return 2;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked)};
    }
}
