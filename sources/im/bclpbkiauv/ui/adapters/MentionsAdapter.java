package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.MentionsAdapter;
import im.bclpbkiauv.ui.adapters.SearchAdapterHelper;
import im.bclpbkiauv.ui.cells.BotSwitchCell;
import im.bclpbkiauv.ui.cells.ContextLinkCell;
import im.bclpbkiauv.ui.cells.MentionCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.util.ArrayList;
import java.util.HashMap;

public class MentionsAdapter extends RecyclerListView.SelectionAdapter {
    private static final String punctuationsChars = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n";
    private SparseArray<TLRPC.BotInfo> botInfo;
    private int botsCount;
    private Runnable cancelDelayRunnable;
    /* access modifiers changed from: private */
    public int channelLastReqId;
    /* access modifiers changed from: private */
    public int channelReqId;
    private boolean contextMedia;
    private int contextQueryReqid;
    /* access modifiers changed from: private */
    public Runnable contextQueryRunnable;
    /* access modifiers changed from: private */
    public int contextUsernameReqid;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public MentionsAdapterDelegate delegate;
    private long dialog_id;
    /* access modifiers changed from: private */
    public TLRPC.User foundContextBot;
    private TLRPC.ChatFull info;
    private boolean inlineMediaEnabled = true;
    private boolean isDarkTheme;
    /* access modifiers changed from: private */
    public boolean isSearchingMentions;
    /* access modifiers changed from: private */
    public Location lastKnownLocation;
    /* access modifiers changed from: private */
    public int lastPosition;
    private String[] lastSearchKeyboardLanguage;
    /* access modifiers changed from: private */
    public String lastText;
    /* access modifiers changed from: private */
    public boolean lastUsernameOnly;
    private SendMessagesHelper.LocationProvider locationProvider = new SendMessagesHelper.LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            if (MentionsAdapter.this.foundContextBot != null && MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                Location unused = MentionsAdapter.this.lastKnownLocation = location;
                MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                mentionsAdapter.searchForContextBotResults(true, mentionsAdapter.foundContextBot, MentionsAdapter.this.searchingContextQuery, "");
            }
        }

        public void onUnableLocationAcquire() {
            MentionsAdapter.this.onLocationUnavailable();
        }
    }) {
        public void stop() {
            super.stop();
            Location unused = MentionsAdapter.this.lastKnownLocation = null;
        }
    };
    private Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> messages;
    private boolean needBotContext = true;
    private boolean needPannel = true;
    private boolean needUsernames = true;
    private String nextQueryOffset;
    /* access modifiers changed from: private */
    public boolean noUserName;
    private ChatActivity parentFragment;
    private int resultLength;
    private int resultStartPosition;
    private SearchAdapterHelper searchAdapterHelper;
    /* access modifiers changed from: private */
    public Runnable searchGlobalRunnable;
    private ArrayList<TLRPC.BotInlineResult> searchResultBotContext;
    private TLRPC.TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<TLRPC.User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<MediaDataController.KeywordResult> searchResultSuggestions;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.User> searchResultUsernames;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC.User> searchResultUsernamesMap;
    /* access modifiers changed from: private */
    public String searchingContextQuery;
    /* access modifiers changed from: private */
    public String searchingContextUsername;

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(TLRPC.BotInlineResult botInlineResult);

        void onContextSearch(boolean z);
    }

    static /* synthetic */ int access$1604(MentionsAdapter x0) {
        int i = x0.channelLastReqId + 1;
        x0.channelLastReqId = i;
        return i;
    }

    public MentionsAdapter(Context context, boolean darkTheme, long did, MentionsAdapterDelegate mentionsAdapterDelegate) {
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = darkTheme;
        this.dialog_id = did;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ SparseArray<TLRPC.User> getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged() {
                MentionsAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                if (MentionsAdapter.this.lastText != null) {
                    MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                    mentionsAdapter.searchUsernameOrHashtag(mentionsAdapter.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages, MentionsAdapter.this.lastUsernameOnly);
                }
            }
        });
    }

    public void onDestroy() {
        SendMessagesHelper.LocationProvider locationProvider2 = this.locationProvider;
        if (locationProvider2 != null) {
            locationProvider2.stop();
        }
        Runnable runnable = this.contextQueryRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.contextQueryRunnable = null;
        }
        if (this.contextUsernameReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
            this.contextUsernameReqid = 0;
        }
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        this.foundContextBot = null;
        this.inlineMediaEnabled = true;
        this.searchingContextUsername = null;
        this.searchingContextQuery = null;
        this.noUserName = false;
    }

    public void setParentFragment(ChatActivity fragment) {
        this.parentFragment = fragment;
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        ChatActivity chatActivity;
        TLRPC.Chat chat;
        this.currentAccount = UserConfig.selectedAccount;
        this.info = chatInfo;
        if (!(this.inlineMediaEnabled || this.foundContextBot == null || (chatActivity = this.parentFragment) == null || (chat = chatActivity.getCurrentChat()) == null)) {
            boolean canSendStickers = ChatObject.canSendStickers(chat);
            this.inlineMediaEnabled = canSendStickers;
            if (canSendStickers) {
                this.searchResultUsernames = null;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(false);
                processFoundUser(this.foundContextBot);
            }
        }
        String str = this.lastText;
        if (str != null) {
            searchUsernameOrHashtag(str, this.lastPosition, this.messages, this.lastUsernameOnly);
        }
    }

    public void setNeedUsernames(boolean value) {
        this.needUsernames = value;
    }

    public void setNeedBotContext(boolean value) {
        this.needBotContext = value;
    }

    public void setBotInfo(SparseArray<TLRPC.BotInfo> info2) {
        this.botInfo = info2;
    }

    public void setBotsCount(int count) {
        this.botsCount = count;
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        if (mentionsAdapterDelegate != null) {
            mentionsAdapterDelegate.needChangePanelVisibility(false);
        }
    }

    public TLRPC.TL_inlineBotSwitchPM getBotContextSwitch() {
        return this.searchResultBotContextSwitch;
    }

    public int getContextBotId() {
        TLRPC.User user = this.foundContextBot;
        if (user != null) {
            return user.id;
        }
        return 0;
    }

    public TLRPC.User getContextBotUser() {
        return this.foundContextBot;
    }

    public String getContextBotName() {
        TLRPC.User user = this.foundContextBot;
        return user != null ? user.username : "";
    }

    /* access modifiers changed from: private */
    public void processFoundUser(TLRPC.User user) {
        ChatActivity chatActivity;
        TLRPC.Chat chat;
        this.contextUsernameReqid = 0;
        this.locationProvider.stop();
        if (user == null || !user.bot || user.bot_inline_placeholder == null) {
            this.foundContextBot = null;
            this.inlineMediaEnabled = true;
        } else {
            this.foundContextBot = user;
            ChatActivity chatActivity2 = this.parentFragment;
            if (!(chatActivity2 == null || (chat = chatActivity2.getCurrentChat()) == null)) {
                boolean canSendStickers = ChatObject.canSendStickers(chat);
                this.inlineMediaEnabled = canSendStickers;
                if (!canSendStickers) {
                    notifyDataSetChanged();
                    this.delegate.needChangePanelVisibility(true);
                    return;
                }
            }
            if (this.foundContextBot.bot_inline_geo) {
                SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                if (preferences.getBoolean("inlinegeo_" + this.foundContextBot.id, false) || (chatActivity = this.parentFragment) == null || chatActivity.getParentActivity() == null) {
                    checkLocationPermissionsOrStart();
                } else {
                    TLRPC.User foundContextBotFinal = this.foundContextBot;
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentFragment.getParentActivity());
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", R.string.ShareYouLocationTitle));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInline", R.string.ShareYouLocationInline));
                    boolean[] buttonClicked = new boolean[1];
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(buttonClicked, foundContextBotFinal) {
                        private final /* synthetic */ boolean[] f$1;
                        private final /* synthetic */ TLRPC.User f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$0$MentionsAdapter(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener(buttonClicked) {
                        private final /* synthetic */ boolean[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$1$MentionsAdapter(this.f$1, dialogInterface, i);
                        }
                    });
                    this.parentFragment.showDialog(builder.create(), new DialogInterface.OnDismissListener(buttonClicked) {
                        private final /* synthetic */ boolean[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onDismiss(DialogInterface dialogInterface) {
                            MentionsAdapter.this.lambda$processFoundUser$2$MentionsAdapter(this.f$1, dialogInterface);
                        }
                    });
                }
            }
        }
        if (this.foundContextBot == null) {
            this.noUserName = true;
            return;
        }
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        if (mentionsAdapterDelegate != null) {
            mentionsAdapterDelegate.onContextSearch(true);
        }
        searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
    }

    public /* synthetic */ void lambda$processFoundUser$0$MentionsAdapter(boolean[] buttonClicked, TLRPC.User foundContextBotFinal, DialogInterface dialogInterface, int i) {
        buttonClicked[0] = true;
        if (foundContextBotFinal != null) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean("inlinegeo_" + foundContextBotFinal.id, true).commit();
            checkLocationPermissionsOrStart();
        }
    }

    public /* synthetic */ void lambda$processFoundUser$1$MentionsAdapter(boolean[] buttonClicked, DialogInterface dialog, int which) {
        buttonClicked[0] = true;
        onLocationUnavailable();
    }

    public /* synthetic */ void lambda$processFoundUser$2$MentionsAdapter(boolean[] buttonClicked, DialogInterface dialog) {
        if (!buttonClicked[0]) {
            onLocationUnavailable();
        }
    }

    private void searchForContextBot(String username, String query) {
        String str;
        String str2;
        TLRPC.User user = this.foundContextBot;
        if (user == null || user.username == null || !this.foundContextBot.username.equals(username) || (str2 = this.searchingContextQuery) == null || !str2.equals(query)) {
            this.searchResultBotContext = null;
            this.searchResultBotContextSwitch = null;
            notifyDataSetChanged();
            if (this.foundContextBot != null) {
                if (this.inlineMediaEnabled || username == null || query == null) {
                    this.delegate.needChangePanelVisibility(false);
                } else {
                    return;
                }
            }
            Runnable runnable = this.contextQueryRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.contextQueryRunnable = null;
            }
            if (TextUtils.isEmpty(username) || ((str = this.searchingContextUsername) != null && !str.equals(username))) {
                if (this.contextUsernameReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
                    this.contextUsernameReqid = 0;
                }
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.foundContextBot = null;
                this.inlineMediaEnabled = true;
                this.searchingContextUsername = null;
                this.searchingContextQuery = null;
                this.locationProvider.stop();
                this.noUserName = false;
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
                if (username == null || username.length() == 0) {
                    return;
                }
            }
            if (query == null) {
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.searchingContextQuery = null;
                MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
                if (mentionsAdapterDelegate2 != null) {
                    mentionsAdapterDelegate2.onContextSearch(false);
                    return;
                }
                return;
            }
            MentionsAdapterDelegate mentionsAdapterDelegate3 = this.delegate;
            if (mentionsAdapterDelegate3 != null) {
                if (this.foundContextBot != null) {
                    mentionsAdapterDelegate3.onContextSearch(true);
                } else if (username.equals("gif")) {
                    this.searchingContextUsername = "gif";
                    this.delegate.onContextSearch(false);
                }
            }
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            this.searchingContextQuery = query;
            final String str3 = query;
            final String str4 = username;
            final MessagesController messagesController2 = messagesController;
            final MessagesStorage messagesStorage2 = messagesStorage;
            AnonymousClass4 r1 = new Runnable() {
                public void run() {
                    if (MentionsAdapter.this.contextQueryRunnable == this) {
                        Runnable unused = MentionsAdapter.this.contextQueryRunnable = null;
                        if (MentionsAdapter.this.foundContextBot == null && !MentionsAdapter.this.noUserName) {
                            String unused2 = MentionsAdapter.this.searchingContextUsername = str4;
                            TLObject object = messagesController2.getUserOrChat(MentionsAdapter.this.searchingContextUsername);
                            if (object instanceof TLRPC.User) {
                                MentionsAdapter.this.processFoundUser((TLRPC.User) object);
                                return;
                            }
                            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                            req.username = MentionsAdapter.this.searchingContextUsername;
                            MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                            int unused3 = mentionsAdapter.contextUsernameReqid = ConnectionsManager.getInstance(mentionsAdapter.currentAccount).sendRequest(req, new RequestDelegate(str4, messagesController2, messagesStorage2) {
                                private final /* synthetic */ String f$1;
                                private final /* synthetic */ MessagesController f$2;
                                private final /* synthetic */ MessagesStorage f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    MentionsAdapter.AnonymousClass4.this.lambda$run$1$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                                }
                            });
                        } else if (!MentionsAdapter.this.noUserName) {
                            MentionsAdapter mentionsAdapter2 = MentionsAdapter.this;
                            mentionsAdapter2.searchForContextBotResults(true, mentionsAdapter2.foundContextBot, str3, "");
                        }
                    }
                }

                public /* synthetic */ void lambda$run$1$MentionsAdapter$4(String username, MessagesController messagesController, MessagesStorage messagesStorage, TLObject response, TLRPC.TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable(username, error, response, messagesController, messagesStorage) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ TLRPC.TL_error f$2;
                        private final /* synthetic */ TLObject f$3;
                        private final /* synthetic */ MessagesController f$4;
                        private final /* synthetic */ MessagesStorage f$5;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                        }

                        public final void run() {
                            MentionsAdapter.AnonymousClass4.this.lambda$null$0$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                        }
                    });
                }

                public /* synthetic */ void lambda$null$0$MentionsAdapter$4(String username, TLRPC.TL_error error, TLObject response, MessagesController messagesController, MessagesStorage messagesStorage) {
                    if (MentionsAdapter.this.searchingContextUsername != null && MentionsAdapter.this.searchingContextUsername.equals(username)) {
                        TLRPC.User user = null;
                        if (error == null) {
                            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                            if (!res.users.isEmpty()) {
                                user = res.users.get(0);
                                messagesController.putUser(user, false);
                                messagesStorage.putUsersAndChats(res.users, (ArrayList<TLRPC.Chat>) null, true, true);
                            }
                        }
                        MentionsAdapter.this.processFoundUser(user);
                    }
                }
            };
            this.contextQueryRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, 400);
        }
    }

    /* access modifiers changed from: private */
    public void onLocationUnavailable() {
        TLRPC.User user = this.foundContextBot;
        if (user != null && user.bot_inline_geo) {
            Location location = new Location("network");
            this.lastKnownLocation = location;
            location.setLatitude(-1000.0d);
            this.lastKnownLocation.setLongitude(-1000.0d);
            searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
        }
    }

    private void checkLocationPermissionsOrStart() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && chatActivity.getParentActivity() != null) {
            if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) == 0) {
                TLRPC.User user = this.foundContextBot;
                if (user != null && user.bot_inline_geo) {
                    this.locationProvider.start();
                    return;
                }
                return;
            }
            this.parentFragment.getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, "android.permission.ACCESS_FINE_LOCATION"}, 2);
        }
    }

    public void setSearchingMentions(boolean value) {
        this.isSearchingMentions = value;
    }

    public String getBotCaption() {
        TLRPC.User user = this.foundContextBot;
        if (user != null) {
            return user.bot_inline_placeholder;
        }
        String str = this.searchingContextUsername;
        if (str == null || !str.equals("gif")) {
            return null;
        }
        return "Search GIFs";
    }

    public void searchForContextBotForNextOffset() {
        String str;
        TLRPC.User user;
        String str2;
        if (this.contextQueryReqid == 0 && (str = this.nextQueryOffset) != null && str.length() != 0 && (user = this.foundContextBot) != null && (str2 = this.searchingContextQuery) != null) {
            searchForContextBotResults(true, user, str2, this.nextQueryOffset);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006a, code lost:
        r1 = r8.lastKnownLocation;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchForContextBotResults(boolean r17, im.bclpbkiauv.tgnet.TLRPC.User r18, java.lang.String r19, java.lang.String r20) {
        /*
            r16 = this;
            r8 = r16
            r9 = r18
            r10 = r19
            r11 = r20
            int r0 = r8.contextQueryReqid
            r1 = 0
            r12 = 1
            if (r0 == 0) goto L_0x001b
            int r0 = r8.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r0)
            int r2 = r8.contextQueryReqid
            r0.cancelRequest(r2, r12)
            r8.contextQueryReqid = r1
        L_0x001b:
            boolean r0 = r8.inlineMediaEnabled
            if (r0 != 0) goto L_0x0027
            im.bclpbkiauv.ui.adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            if (r0 == 0) goto L_0x0026
            r0.onContextSearch(r1)
        L_0x0026:
            return
        L_0x0027:
            if (r10 == 0) goto L_0x0130
            if (r9 != 0) goto L_0x002d
            goto L_0x0130
        L_0x002d:
            boolean r0 = r9.bot_inline_geo
            if (r0 == 0) goto L_0x0036
            android.location.Location r0 = r8.lastKnownLocation
            if (r0 != 0) goto L_0x0036
            return
        L_0x0036:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            long r1 = r8.dialog_id
            r0.append(r1)
            java.lang.String r1 = "_"
            r0.append(r1)
            r0.append(r10)
            r0.append(r1)
            r0.append(r11)
            r0.append(r1)
            long r2 = r8.dialog_id
            r0.append(r2)
            r0.append(r1)
            int r2 = r9.id
            r0.append(r2)
            r0.append(r1)
            boolean r1 = r9.bot_inline_geo
            r13 = -4571364728013586432(0xc08f400000000000, double:-1000.0)
            if (r1 == 0) goto L_0x0088
            android.location.Location r1 = r8.lastKnownLocation
            if (r1 == 0) goto L_0x0088
            double r1 = r1.getLatitude()
            int r3 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0088
            android.location.Location r1 = r8.lastKnownLocation
            double r1 = r1.getLatitude()
            android.location.Location r3 = r8.lastKnownLocation
            double r3 = r3.getLongitude()
            double r1 = r1 + r3
            java.lang.Double r1 = java.lang.Double.valueOf(r1)
            goto L_0x008a
        L_0x0088:
            java.lang.String r1 = ""
        L_0x008a:
            r0.append(r1)
            java.lang.String r15 = r0.toString()
            int r0 = r8.currentAccount
            im.bclpbkiauv.messenger.MessagesStorage r7 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r0)
            im.bclpbkiauv.ui.adapters.-$$Lambda$MentionsAdapter$M-RFWksaRmPWlAScP0dG0lfZS9o r6 = new im.bclpbkiauv.ui.adapters.-$$Lambda$MentionsAdapter$M-RFWksaRmPWlAScP0dG0lfZS9o
            r0 = r6
            r1 = r16
            r2 = r19
            r3 = r17
            r4 = r18
            r5 = r20
            r12 = r6
            r6 = r7
            r13 = r7
            r7 = r15
            r0.<init>(r2, r3, r4, r5, r6, r7)
            if (r17 == 0) goto L_0x00b2
            r13.getBotCache(r15, r12)
            goto L_0x012f
        L_0x00b2:
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_getInlineBotResults r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_getInlineBotResults
            r0.<init>()
            int r1 = r8.currentAccount
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r1)
            im.bclpbkiauv.tgnet.TLRPC$InputUser r1 = r1.getInputUser((im.bclpbkiauv.tgnet.TLRPC.User) r9)
            r0.bot = r1
            r0.query = r10
            r0.offset = r11
            boolean r1 = r9.bot_inline_geo
            if (r1 == 0) goto L_0x0105
            android.location.Location r1 = r8.lastKnownLocation
            if (r1 == 0) goto L_0x0105
            double r1 = r1.getLatitude()
            r3 = -4571364728013586432(0xc08f400000000000, double:-1000.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0105
            int r1 = r0.flags
            r2 = 1
            r1 = r1 | r2
            r0.flags = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_inputGeoPoint r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputGeoPoint
            r1.<init>()
            r0.geo_point = r1
            im.bclpbkiauv.tgnet.TLRPC$InputGeoPoint r1 = r0.geo_point
            android.location.Location r2 = r8.lastKnownLocation
            double r2 = r2.getLatitude()
            double r2 = im.bclpbkiauv.messenger.AndroidUtilities.fixLocationCoord(r2)
            r1.lat = r2
            im.bclpbkiauv.tgnet.TLRPC$InputGeoPoint r1 = r0.geo_point
            android.location.Location r2 = r8.lastKnownLocation
            double r2 = r2.getLongitude()
            double r2 = im.bclpbkiauv.messenger.AndroidUtilities.fixLocationCoord(r2)
            r1._long = r2
        L_0x0105:
            long r1 = r8.dialog_id
            int r3 = (int) r1
            r4 = 32
            long r1 = r1 >> r4
            int r2 = (int) r1
            if (r3 == 0) goto L_0x011b
            int r1 = r8.currentAccount
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r1)
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer(r3)
            r0.peer = r1
            goto L_0x0122
        L_0x011b:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerEmpty r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerEmpty
            r1.<init>()
            r0.peer = r1
        L_0x0122:
            int r1 = r8.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r1 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r1)
            r4 = 2
            int r1 = r1.sendRequest(r0, r12, r4)
            r8.contextQueryReqid = r1
        L_0x012f:
            return
        L_0x0130:
            r0 = 0
            r8.searchingContextQuery = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.adapters.MentionsAdapter.searchForContextBotResults(boolean, im.bclpbkiauv.tgnet.TLRPC$User, java.lang.String, java.lang.String):void");
    }

    public /* synthetic */ void lambda$searchForContextBotResults$4$MentionsAdapter(String query, boolean cache, TLRPC.User user, String offset, MessagesStorage messagesStorage, String key, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(query, cache, response, user, offset, messagesStorage, key) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ TLRPC.User f$4;
            private final /* synthetic */ String f$5;
            private final /* synthetic */ MessagesStorage f$6;
            private final /* synthetic */ String f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                MentionsAdapter.this.lambda$null$3$MentionsAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$MentionsAdapter(String query, boolean cache, TLObject response, TLRPC.User user, String offset, MessagesStorage messagesStorage, String key) {
        if (query.equals(this.searchingContextQuery)) {
            boolean z = false;
            this.contextQueryReqid = 0;
            if (!cache || response != null) {
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
            } else {
                searchForContextBotResults(false, user, query, offset);
            }
            if (response instanceof TLRPC.TL_messages_botResults) {
                TLRPC.TL_messages_botResults res = (TLRPC.TL_messages_botResults) response;
                if (!cache && res.cache_time != 0) {
                    messagesStorage.saveBotCache(key, res);
                }
                this.nextQueryOffset = res.next_offset;
                if (this.searchResultBotContextSwitch == null) {
                    this.searchResultBotContextSwitch = res.switch_pm;
                }
                int a = 0;
                while (a < res.results.size()) {
                    TLRPC.BotInlineResult result = (TLRPC.BotInlineResult) res.results.get(a);
                    if (!(result.document instanceof TLRPC.TL_document) && !(result.photo instanceof TLRPC.TL_photo) && !"game".equals(result.type) && result.content == null && (result.send_message instanceof TLRPC.TL_botInlineMessageMediaAuto)) {
                        res.results.remove(a);
                        a--;
                    }
                    result.query_id = res.query_id;
                    a++;
                }
                boolean added = false;
                if (this.searchResultBotContext == null || offset.length() == 0) {
                    this.searchResultBotContext = res.results;
                    this.contextMedia = res.gallery;
                } else {
                    added = true;
                    this.searchResultBotContext.addAll(res.results);
                    if (res.results.isEmpty()) {
                        this.nextQueryOffset = "";
                    }
                }
                Runnable runnable = this.cancelDelayRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.cancelDelayRunnable = null;
                }
                this.searchResultHashtags = null;
                this.searchResultUsernames = null;
                this.searchResultUsernamesMap = null;
                this.searchResultCommands = null;
                this.searchResultSuggestions = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                if (added) {
                    boolean hasTop = this.searchResultBotContextSwitch != null;
                    notifyItemChanged(((this.searchResultBotContext.size() - res.results.size()) + (hasTop ? 1 : 0)) - 1);
                    notifyItemRangeInserted((this.searchResultBotContext.size() - res.results.size()) + (hasTop ? 1 : 0), res.results.size());
                } else {
                    notifyDataSetChanged();
                }
                MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
                if (!this.searchResultBotContext.isEmpty() || this.searchResultBotContextSwitch != null) {
                    z = true;
                }
                mentionsAdapterDelegate2.needChangePanelVisibility(z);
            }
        }
    }

    public void setNeedPannel(boolean need) {
        this.needPannel = need;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:117:0x01f1, code lost:
        r15 = -1;
        r17 = -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x014a, code lost:
        if (r7.info != null) goto L_0x015b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x014c, code lost:
        if (r3 == 0) goto L_0x015b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x014e, code lost:
        r7.lastText = r8;
        r7.lastPosition = r9;
        r7.messages = r10;
        r7.delegate.needChangePanelVisibility(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x015a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x015b, code lost:
        r7.resultStartPosition = r3;
        r7.resultLength = r13.length() + 1;
        r15 = 0;
        r17 = r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchUsernameOrHashtag(java.lang.String r24, int r25, java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r26, boolean r27) {
        /*
            r23 = this;
            r7 = r23
            r8 = r24
            r9 = r25
            r10 = r26
            r11 = r27
            java.lang.Runnable r0 = r7.cancelDelayRunnable
            r1 = 0
            if (r0 == 0) goto L_0x0014
            im.bclpbkiauv.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r7.cancelDelayRunnable = r1
        L_0x0014:
            int r0 = r7.channelReqId
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L_0x0027
            int r0 = r7.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r0)
            int r4 = r7.channelReqId
            r0.cancelRequest(r4, r3)
            r7.channelReqId = r2
        L_0x0027:
            java.lang.Runnable r0 = r7.searchGlobalRunnable
            if (r0 == 0) goto L_0x0030
            im.bclpbkiauv.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r7.searchGlobalRunnable = r1
        L_0x0030:
            boolean r0 = android.text.TextUtils.isEmpty(r24)
            if (r0 == 0) goto L_0x0041
            r7.searchForContextBot(r1, r1)
            im.bclpbkiauv.ui.adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r7.delegate
            r0.needChangePanelVisibility(r2)
            r7.lastText = r1
            return
        L_0x0041:
            r0 = r25
            int r4 = r24.length()
            if (r4 <= 0) goto L_0x004d
            int r0 = r0 + -1
            r12 = r0
            goto L_0x004e
        L_0x004d:
            r12 = r0
        L_0x004e:
            r7.lastText = r1
            r7.lastUsernameOnly = r11
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r13 = r0
            r0 = -1
            r4 = 64
            r5 = 32
            if (r11 != 0) goto L_0x00ed
            boolean r6 = r7.needBotContext
            if (r6 == 0) goto L_0x00ed
            char r6 = r8.charAt(r2)
            if (r6 != r4) goto L_0x00ed
            int r6 = r8.indexOf(r5)
            int r14 = r24.length()
            r15 = 0
            r16 = 0
            if (r6 <= 0) goto L_0x0083
            java.lang.String r15 = r8.substring(r3, r6)
            int r4 = r6 + 1
            java.lang.String r16 = r8.substring(r4)
            r4 = r16
            goto L_0x00af
        L_0x0083:
            int r4 = r14 + -1
            char r4 = r8.charAt(r4)
            r5 = 116(0x74, float:1.63E-43)
            if (r4 != r5) goto L_0x00aa
            int r4 = r14 + -2
            char r4 = r8.charAt(r4)
            r5 = 111(0x6f, float:1.56E-43)
            if (r4 != r5) goto L_0x00aa
            int r4 = r14 + -3
            char r4 = r8.charAt(r4)
            r5 = 98
            if (r4 != r5) goto L_0x00aa
            java.lang.String r15 = r8.substring(r3)
            java.lang.String r16 = ""
            r4 = r16
            goto L_0x00af
        L_0x00aa:
            r7.searchForContextBot(r1, r1)
            r4 = r16
        L_0x00af:
            if (r15 == 0) goto L_0x00e7
            int r5 = r15.length()
            if (r5 < r3) goto L_0x00e7
            r5 = 1
        L_0x00b8:
            int r2 = r15.length()
            if (r5 >= r2) goto L_0x00e6
            char r2 = r15.charAt(r5)
            r3 = 48
            if (r2 < r3) goto L_0x00ca
            r3 = 57
            if (r2 <= r3) goto L_0x00e1
        L_0x00ca:
            r3 = 97
            if (r2 < r3) goto L_0x00d2
            r3 = 122(0x7a, float:1.71E-43)
            if (r2 <= r3) goto L_0x00e1
        L_0x00d2:
            r3 = 65
            if (r2 < r3) goto L_0x00da
            r3 = 90
            if (r2 <= r3) goto L_0x00e1
        L_0x00da:
            r3 = 95
            if (r2 == r3) goto L_0x00e1
            java.lang.String r15 = ""
            goto L_0x00e6
        L_0x00e1:
            int r5 = r5 + 1
            r2 = 0
            r3 = 1
            goto L_0x00b8
        L_0x00e6:
            goto L_0x00e9
        L_0x00e7:
            java.lang.String r15 = ""
        L_0x00e9:
            r7.searchForContextBot(r15, r4)
            goto L_0x00f0
        L_0x00ed:
            r7.searchForContextBot(r1, r1)
        L_0x00f0:
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r7.foundContextBot
            if (r2 == 0) goto L_0x00f5
            return
        L_0x00f5:
            int r2 = r7.currentAccount
            im.bclpbkiauv.messenger.MessagesController r14 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
            r2 = -1
            if (r11 == 0) goto L_0x0115
            r3 = 1
            java.lang.String r4 = r8.substring(r3)
            r13.append(r4)
            r3 = 0
            r7.resultStartPosition = r3
            int r3 = r13.length()
            r7.resultLength = r3
            r0 = 0
            r15 = r0
            r17 = r2
            goto L_0x01f4
        L_0x0115:
            r3 = r12
        L_0x0116:
            if (r3 < 0) goto L_0x01f1
            int r4 = r24.length()
            if (r3 < r4) goto L_0x0120
            goto L_0x01ed
        L_0x0120:
            char r4 = r8.charAt(r3)
            if (r3 == 0) goto L_0x013a
            int r5 = r3 + -1
            char r5 = r8.charAt(r5)
            r6 = 32
            if (r5 == r6) goto L_0x013a
            int r5 = r3 + -1
            char r5 = r8.charAt(r5)
            r6 = 10
            if (r5 != r6) goto L_0x01e4
        L_0x013a:
            r5 = 64
            if (r4 != r5) goto L_0x016c
            boolean r6 = r7.needUsernames
            if (r6 != 0) goto L_0x0148
            boolean r6 = r7.needBotContext
            if (r6 == 0) goto L_0x01e4
            if (r3 != 0) goto L_0x01e4
        L_0x0148:
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r5 = r7.info
            if (r5 != 0) goto L_0x015b
            if (r3 == 0) goto L_0x015b
            r7.lastText = r8
            r7.lastPosition = r9
            r7.messages = r10
            im.bclpbkiauv.ui.adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r7.delegate
            r5 = 0
            r1.needChangePanelVisibility(r5)
            return
        L_0x015b:
            r2 = r3
            r0 = 0
            r7.resultStartPosition = r3
            int r5 = r13.length()
            r6 = 1
            int r5 = r5 + r6
            r7.resultLength = r5
            r15 = r0
            r17 = r2
            goto L_0x01f4
        L_0x016c:
            r6 = 35
            if (r4 != r6) goto L_0x0199
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r5 = r7.searchAdapterHelper
            boolean r5 = r5.loadRecentHashtags()
            if (r5 == 0) goto L_0x018c
            r0 = 1
            r7.resultStartPosition = r3
            int r5 = r13.length()
            r6 = 1
            int r5 = r5 + r6
            r7.resultLength = r5
            r5 = 0
            r13.insert(r5, r4)
            r15 = r0
            r17 = r2
            goto L_0x01f4
        L_0x018c:
            r5 = 0
            r7.lastText = r8
            r7.lastPosition = r9
            r7.messages = r10
            im.bclpbkiauv.ui.adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r7.delegate
            r1.needChangePanelVisibility(r5)
            return
        L_0x0199:
            if (r3 != 0) goto L_0x01b2
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$BotInfo> r6 = r7.botInfo
            if (r6 == 0) goto L_0x01b2
            r6 = 47
            if (r4 != r6) goto L_0x01b2
            r0 = 2
            r7.resultStartPosition = r3
            int r5 = r13.length()
            r6 = 1
            int r5 = r5 + r6
            r7.resultLength = r5
            r15 = r0
            r17 = r2
            goto L_0x01f4
        L_0x01b2:
            r6 = 58
            if (r4 != r6) goto L_0x01e4
            int r6 = r13.length()
            if (r6 <= 0) goto L_0x01e4
            r6 = 0
            char r15 = r13.charAt(r6)
            java.lang.String r6 = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n"
            int r6 = r6.indexOf(r15)
            if (r6 < 0) goto L_0x01cb
            r6 = 1
            goto L_0x01cc
        L_0x01cb:
            r6 = 0
        L_0x01cc:
            if (r6 == 0) goto L_0x01d5
            int r15 = r13.length()
            r5 = 1
            if (r15 <= r5) goto L_0x01e4
        L_0x01d5:
            r0 = 3
            r7.resultStartPosition = r3
            int r5 = r13.length()
            r15 = 1
            int r5 = r5 + r15
            r7.resultLength = r5
            r15 = r0
            r17 = r2
            goto L_0x01f4
        L_0x01e4:
            r5 = 32
            if (r4 != r5) goto L_0x01e9
            goto L_0x01f1
        L_0x01e9:
            r5 = 0
            r13.insert(r5, r4)
        L_0x01ed:
            int r3 = r3 + -1
            goto L_0x0116
        L_0x01f1:
            r15 = r0
            r17 = r2
        L_0x01f4:
            boolean r0 = r7.needPannel
            if (r0 != 0) goto L_0x01f9
            return
        L_0x01f9:
            r0 = -1
            if (r15 != r0) goto L_0x0203
            im.bclpbkiauv.ui.adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r7.delegate
            r1 = 0
            r0.needChangePanelVisibility(r1)
            return
        L_0x0203:
            if (r15 != 0) goto L_0x048b
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r6 = r2
            r2 = 0
        L_0x020c:
            r3 = 100
            int r4 = r26.size()
            int r3 = java.lang.Math.min(r3, r4)
            if (r2 >= r3) goto L_0x0236
            java.lang.Object r3 = r10.get(r2)
            im.bclpbkiauv.messenger.MessageObject r3 = (im.bclpbkiauv.messenger.MessageObject) r3
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r3 = r3.from_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            boolean r4 = r6.contains(r4)
            if (r4 != 0) goto L_0x0233
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            r6.add(r4)
        L_0x0233:
            int r2 = r2 + 1
            goto L_0x020c
        L_0x0236:
            java.lang.String r2 = r13.toString()
            java.lang.String r5 = r2.toLowerCase()
            r2 = 32
            int r2 = r5.indexOf(r2)
            if (r2 < 0) goto L_0x0248
            r2 = 1
            goto L_0x0249
        L_0x0248:
            r2 = 0
        L_0x0249:
            r16 = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r4 = r2
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            r3 = r2
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            int r1 = r7.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r1 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r1)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_topPeer> r1 = r1.inlineBots
            if (r11 != 0) goto L_0x02d2
            boolean r0 = r7.needBotContext
            if (r0 == 0) goto L_0x02d2
            if (r17 != 0) goto L_0x02d2
            boolean r0 = r1.isEmpty()
            if (r0 != 0) goto L_0x02d2
            r0 = 0
            r21 = 0
            r8 = r21
        L_0x0277:
            int r9 = r1.size()
            if (r8 >= r9) goto L_0x02cf
            java.lang.Object r9 = r1.get(r8)
            im.bclpbkiauv.tgnet.TLRPC$TL_topPeer r9 = (im.bclpbkiauv.tgnet.TLRPC.TL_topPeer) r9
            im.bclpbkiauv.tgnet.TLRPC$Peer r9 = r9.peer
            int r9 = r9.user_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            im.bclpbkiauv.tgnet.TLRPC$User r9 = r14.getUser(r9)
            if (r9 != 0) goto L_0x0294
            r21 = r1
            goto L_0x02c8
        L_0x0294:
            r21 = r1
            java.lang.String r1 = r9.username
            if (r1 == 0) goto L_0x02c4
            java.lang.String r1 = r9.username
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x02c4
            int r1 = r5.length()
            if (r1 <= 0) goto L_0x02b4
            java.lang.String r1 = r9.username
            java.lang.String r1 = r1.toLowerCase()
            boolean r1 = r1.startsWith(r5)
            if (r1 != 0) goto L_0x02ba
        L_0x02b4:
            int r1 = r5.length()
            if (r1 != 0) goto L_0x02c4
        L_0x02ba:
            r4.add(r9)
            int r1 = r9.id
            r3.put(r1, r9)
            int r0 = r0 + 1
        L_0x02c4:
            r1 = 5
            if (r0 != r1) goto L_0x02c8
            goto L_0x02d4
        L_0x02c8:
            int r8 = r8 + 1
            r9 = r25
            r1 = r21
            goto L_0x0277
        L_0x02cf:
            r21 = r1
            goto L_0x02d4
        L_0x02d2:
            r21 = r1
        L_0x02d4:
            im.bclpbkiauv.ui.ChatActivity r0 = r7.parentFragment
            if (r0 == 0) goto L_0x02de
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r0.getCurrentChat()
            r8 = r0
            goto L_0x02f0
        L_0x02de:
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r0 = r7.info
            if (r0 == 0) goto L_0x02ee
            int r0 = r0.id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r14.getChat(r0)
            r8 = r0
            goto L_0x02f0
        L_0x02ee:
            r0 = 0
            r8 = r0
        L_0x02f0:
            if (r8 == 0) goto L_0x041a
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r0 = r7.info
            if (r0 == 0) goto L_0x041a
            im.bclpbkiauv.tgnet.TLRPC$ChatParticipants r0 = r0.participants
            if (r0 == 0) goto L_0x041a
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r8)
            if (r0 == 0) goto L_0x0309
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x0305
            goto L_0x0309
        L_0x0305:
            r22 = r3
            goto L_0x041c
        L_0x0309:
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r0 = r7.info
            im.bclpbkiauv.tgnet.TLRPC$ChatParticipants r0 = r0.participants
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$ChatParticipant> r0 = r0.participants
            int r0 = r0.size()
            r1 = 1
            if (r0 <= r1) goto L_0x032c
            im.bclpbkiauv.tgnet.TLRPC$TL_user r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_user
            r0.<init>()
            java.lang.String r1 = "all"
            r0.first_name = r1
            r0.last_name = r1
            r1 = -1
            r0.id = r1
            r4.add(r0)
            int r1 = r0.id
            r2.put(r1, r0)
        L_0x032c:
            r0 = 0
        L_0x032d:
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r1 = r7.info
            im.bclpbkiauv.tgnet.TLRPC$ChatParticipants r1 = r1.participants
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x0417
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r1 = r7.info
            im.bclpbkiauv.tgnet.TLRPC$ChatParticipants r1 = r1.participants
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            java.lang.Object r1 = r1.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$ChatParticipant r1 = (im.bclpbkiauv.tgnet.TLRPC.ChatParticipant) r1
            int r9 = r1.user_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            im.bclpbkiauv.tgnet.TLRPC$User r9 = r14.getUser(r9)
            if (r9 == 0) goto L_0x040d
            if (r11 != 0) goto L_0x035e
            boolean r19 = im.bclpbkiauv.messenger.UserObject.isUserSelf(r9)
            if (r19 != 0) goto L_0x035a
            goto L_0x035e
        L_0x035a:
            r22 = r3
            goto L_0x0411
        L_0x035e:
            r19 = r1
            int r1 = r9.id
            int r1 = r3.indexOfKey(r1)
            if (r1 < 0) goto L_0x036c
            r22 = r3
            goto L_0x0411
        L_0x036c:
            int r1 = r5.length()
            if (r1 != 0) goto L_0x0381
            boolean r1 = r9.deleted
            if (r1 != 0) goto L_0x037d
            r4.add(r9)
            r22 = r3
            goto L_0x0411
        L_0x037d:
            r22 = r3
            goto L_0x0411
        L_0x0381:
            java.lang.String r1 = r9.username
            if (r1 == 0) goto L_0x03a5
            java.lang.String r1 = r9.username
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x03a5
            java.lang.String r1 = r9.username
            java.lang.String r1 = r1.toLowerCase()
            boolean r1 = r1.startsWith(r5)
            if (r1 == 0) goto L_0x03a5
            r4.add(r9)
            int r1 = r9.id
            r2.put(r1, r9)
            r22 = r3
            goto L_0x0411
        L_0x03a5:
            java.lang.String r1 = r9.first_name
            if (r1 == 0) goto L_0x03c8
            java.lang.String r1 = r9.first_name
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x03c8
            java.lang.String r1 = r9.first_name
            java.lang.String r1 = r1.toLowerCase()
            boolean r1 = r1.startsWith(r5)
            if (r1 == 0) goto L_0x03c8
            r4.add(r9)
            int r1 = r9.id
            r2.put(r1, r9)
            r22 = r3
            goto L_0x0411
        L_0x03c8:
            java.lang.String r1 = r9.last_name
            if (r1 == 0) goto L_0x03eb
            java.lang.String r1 = r9.last_name
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x03eb
            java.lang.String r1 = r9.last_name
            java.lang.String r1 = r1.toLowerCase()
            boolean r1 = r1.startsWith(r5)
            if (r1 == 0) goto L_0x03eb
            r4.add(r9)
            int r1 = r9.id
            r2.put(r1, r9)
            r22 = r3
            goto L_0x0411
        L_0x03eb:
            if (r16 == 0) goto L_0x040a
            java.lang.String r1 = r9.first_name
            r22 = r3
            java.lang.String r3 = r9.last_name
            java.lang.String r1 = im.bclpbkiauv.messenger.ContactsController.formatName(r1, r3)
            java.lang.String r1 = r1.toLowerCase()
            boolean r1 = r1.startsWith(r5)
            if (r1 == 0) goto L_0x0411
            r4.add(r9)
            int r1 = r9.id
            r2.put(r1, r9)
            goto L_0x0411
        L_0x040a:
            r22 = r3
            goto L_0x0411
        L_0x040d:
            r19 = r1
            r22 = r3
        L_0x0411:
            int r0 = r0 + 1
            r3 = r22
            goto L_0x032d
        L_0x0417:
            r22 = r3
            goto L_0x041c
        L_0x041a:
            r22 = r3
        L_0x041c:
            im.bclpbkiauv.ui.adapters.-$$Lambda$MentionsAdapter$orlxjR3EGwccYRzW6x9pU7zcguk r0 = new im.bclpbkiauv.ui.adapters.-$$Lambda$MentionsAdapter$orlxjR3EGwccYRzW6x9pU7zcguk
            r0.<init>(r2, r6)
            java.util.Collections.sort(r4, r0)
            r0 = 0
            r7.searchResultHashtags = r0
            r7.searchResultCommands = r0
            r7.searchResultCommandsHelp = r0
            r7.searchResultCommandsUsers = r0
            r7.searchResultSuggestions = r0
            if (r8 == 0) goto L_0x0475
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x0475
            int r0 = r5.length()
            if (r0 <= 0) goto L_0x0475
            int r0 = r4.size()
            r1 = 5
            if (r0 >= r1) goto L_0x044f
            im.bclpbkiauv.ui.adapters.-$$Lambda$MentionsAdapter$bGZHL2cBMYEORbRW19qs4TGECX0 r0 = new im.bclpbkiauv.ui.adapters.-$$Lambda$MentionsAdapter$bGZHL2cBMYEORbRW19qs4TGECX0
            r0.<init>(r4, r2)
            r7.cancelDelayRunnable = r0
            r9 = 1000(0x3e8, double:4.94E-321)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0, r9)
            goto L_0x0453
        L_0x044f:
            r0 = 1
            r7.showUsersResult(r4, r2, r0)
        L_0x0453:
            im.bclpbkiauv.ui.adapters.MentionsAdapter$5 r9 = new im.bclpbkiauv.ui.adapters.MentionsAdapter$5
            r0 = r9
            r10 = r21
            r1 = r23
            r3 = r2
            r2 = r8
            r19 = r3
            r18 = r22
            r3 = r5
            r20 = r4
            r21 = r5
            r5 = r19
            r22 = r6
            r6 = r14
            r0.<init>(r2, r3, r4, r5, r6)
            r7.searchGlobalRunnable = r9
            r0 = 200(0xc8, double:9.9E-322)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r9, r0)
            goto L_0x0489
        L_0x0475:
            r19 = r2
            r20 = r4
            r10 = r21
            r18 = r22
            r21 = r5
            r22 = r6
            r1 = r19
            r0 = r20
            r2 = 1
            r7.showUsersResult(r0, r1, r2)
        L_0x0489:
            goto L_0x05aa
        L_0x048b:
            r2 = 1
            if (r15 != r2) goto L_0x04e3
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = r13.toString()
            java.lang.String r1 = r1.toLowerCase()
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r2 = r7.searchAdapterHelper
            java.util.ArrayList r2 = r2.getHashtags()
            r3 = 0
        L_0x04a2:
            int r4 = r2.size()
            if (r3 >= r4) goto L_0x04c4
            java.lang.Object r4 = r2.get(r3)
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper$HashtagObject r4 = (im.bclpbkiauv.ui.adapters.SearchAdapterHelper.HashtagObject) r4
            if (r4 == 0) goto L_0x04c1
            java.lang.String r5 = r4.hashtag
            if (r5 == 0) goto L_0x04c1
            java.lang.String r5 = r4.hashtag
            boolean r5 = r5.startsWith(r1)
            if (r5 == 0) goto L_0x04c1
            java.lang.String r5 = r4.hashtag
            r0.add(r5)
        L_0x04c1:
            int r3 = r3 + 1
            goto L_0x04a2
        L_0x04c4:
            r7.searchResultHashtags = r0
            r3 = 0
            r7.searchResultUsernames = r3
            r7.searchResultUsernamesMap = r3
            r7.searchResultCommands = r3
            r7.searchResultCommandsHelp = r3
            r7.searchResultCommandsUsers = r3
            r7.searchResultSuggestions = r3
            r23.notifyDataSetChanged()
            im.bclpbkiauv.ui.adapters.MentionsAdapter$MentionsAdapterDelegate r3 = r7.delegate
            boolean r4 = r0.isEmpty()
            r5 = 1
            r4 = r4 ^ r5
            r3.needChangePanelVisibility(r4)
            goto L_0x05aa
        L_0x04e3:
            r0 = 2
            if (r15 != r0) goto L_0x0579
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.lang.String r3 = r13.toString()
            java.lang.String r3 = r3.toLowerCase()
            r4 = 0
        L_0x04fe:
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$BotInfo> r5 = r7.botInfo
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x055b
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$BotInfo> r5 = r7.botInfo
            java.lang.Object r5 = r5.valueAt(r4)
            im.bclpbkiauv.tgnet.TLRPC$BotInfo r5 = (im.bclpbkiauv.tgnet.TLRPC.BotInfo) r5
            r6 = 0
        L_0x050f:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_botCommand> r8 = r5.commands
            int r8 = r8.size()
            if (r6 >= r8) goto L_0x0558
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_botCommand> r8 = r5.commands
            java.lang.Object r8 = r8.get(r6)
            im.bclpbkiauv.tgnet.TLRPC$TL_botCommand r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_botCommand) r8
            if (r8 == 0) goto L_0x0555
            java.lang.String r9 = r8.command
            if (r9 == 0) goto L_0x0555
            java.lang.String r9 = r8.command
            boolean r9 = r9.startsWith(r3)
            if (r9 == 0) goto L_0x0555
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "/"
            r9.append(r10)
            java.lang.String r10 = r8.command
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0.add(r9)
            java.lang.String r9 = r8.description
            r1.add(r9)
            int r9 = r5.user_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            im.bclpbkiauv.tgnet.TLRPC$User r9 = r14.getUser(r9)
            r2.add(r9)
        L_0x0555:
            int r6 = r6 + 1
            goto L_0x050f
        L_0x0558:
            int r4 = r4 + 1
            goto L_0x04fe
        L_0x055b:
            r4 = 0
            r7.searchResultHashtags = r4
            r7.searchResultUsernames = r4
            r7.searchResultUsernamesMap = r4
            r7.searchResultSuggestions = r4
            r7.searchResultCommands = r0
            r7.searchResultCommandsHelp = r1
            r7.searchResultCommandsUsers = r2
            r23.notifyDataSetChanged()
            im.bclpbkiauv.ui.adapters.MentionsAdapter$MentionsAdapterDelegate r4 = r7.delegate
            boolean r5 = r0.isEmpty()
            r6 = 1
            r5 = r5 ^ r6
            r4.needChangePanelVisibility(r5)
            goto L_0x05a9
        L_0x0579:
            r0 = 3
            if (r15 != r0) goto L_0x05a9
            java.lang.String[] r0 = im.bclpbkiauv.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
            java.lang.String[] r1 = r7.lastSearchKeyboardLanguage
            boolean r1 = java.util.Arrays.equals(r0, r1)
            if (r1 != 0) goto L_0x0591
            int r1 = r7.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r1 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r1)
            r1.fetchNewEmojiKeywords(r0)
        L_0x0591:
            r7.lastSearchKeyboardLanguage = r0
            int r1 = r7.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r1 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r1)
            java.lang.String[] r2 = r7.lastSearchKeyboardLanguage
            java.lang.String r3 = r13.toString()
            im.bclpbkiauv.ui.adapters.-$$Lambda$MentionsAdapter$UsEjW8hv9cA4Hbun_-29MHCLPdQ r4 = new im.bclpbkiauv.ui.adapters.-$$Lambda$MentionsAdapter$UsEjW8hv9cA4Hbun_-29MHCLPdQ
            r4.<init>()
            r5 = 0
            r1.getEmojiSuggestions(r2, r3, r5, r4)
            goto L_0x05aa
        L_0x05a9:
        L_0x05aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.adapters.MentionsAdapter.searchUsernameOrHashtag(java.lang.String, int, java.util.ArrayList, boolean):void");
    }

    static /* synthetic */ int lambda$searchUsernameOrHashtag$5(SparseArray newMap, ArrayList users, TLRPC.User lhs, TLRPC.User rhs) {
        if (newMap.indexOfKey(lhs.id) >= 0 && newMap.indexOfKey(rhs.id) >= 0) {
            return 0;
        }
        if (newMap.indexOfKey(lhs.id) >= 0) {
            return -1;
        }
        if (newMap.indexOfKey(rhs.id) >= 0) {
            return 1;
        }
        int lhsNum = users.indexOf(Integer.valueOf(lhs.id));
        int rhsNum = users.indexOf(Integer.valueOf(rhs.id));
        if (lhsNum == -1 || rhsNum == -1) {
            if (lhsNum != -1 && rhsNum == -1) {
                return -1;
            }
            if (lhsNum != -1 || rhsNum == -1) {
                return 0;
            }
            return 1;
        } else if (lhsNum < rhsNum) {
            return -1;
        } else {
            if (lhsNum == rhsNum) {
                return 0;
            }
            return 1;
        }
    }

    public /* synthetic */ void lambda$searchUsernameOrHashtag$6$MentionsAdapter(ArrayList newResult, SparseArray newMap) {
        this.cancelDelayRunnable = null;
        showUsersResult(newResult, newMap, true);
    }

    public /* synthetic */ void lambda$searchUsernameOrHashtag$7$MentionsAdapter(ArrayList param, String alias) {
        this.searchResultSuggestions = param;
        this.searchResultHashtags = null;
        this.searchResultUsernames = null;
        this.searchResultUsernamesMap = null;
        this.searchResultCommands = null;
        this.searchResultCommandsHelp = null;
        this.searchResultCommandsUsers = null;
        notifyDataSetChanged();
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        ArrayList<MediaDataController.KeywordResult> arrayList = this.searchResultSuggestions;
        mentionsAdapterDelegate.needChangePanelVisibility(arrayList != null && !arrayList.isEmpty());
    }

    /* access modifiers changed from: private */
    public void showUsersResult(ArrayList<TLRPC.User> newResult, SparseArray<TLRPC.User> newMap, boolean notify) {
        this.searchResultUsernames = newResult;
        this.searchResultUsernamesMap = newMap;
        Runnable runnable = this.cancelDelayRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.cancelDelayRunnable = null;
        }
        if (notify) {
            notifyDataSetChanged();
            this.delegate.needChangePanelVisibility(!this.searchResultUsernames.isEmpty());
        }
    }

    public int getResultStartPosition() {
        return this.resultStartPosition;
    }

    public int getResultLength() {
        return this.resultLength;
    }

    public ArrayList<TLRPC.BotInlineResult> getSearchResultBotContext() {
        return this.searchResultBotContext;
    }

    public int getItemCount() {
        int i = 1;
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 1;
        }
        ArrayList<TLRPC.BotInlineResult> arrayList = this.searchResultBotContext;
        if (arrayList != null) {
            int size = arrayList.size();
            if (this.searchResultBotContextSwitch == null) {
                i = 0;
            }
            return size + i;
        }
        ArrayList<TLRPC.User> arrayList2 = this.searchResultUsernames;
        if (arrayList2 != null) {
            return arrayList2.size();
        }
        ArrayList<String> arrayList3 = this.searchResultHashtags;
        if (arrayList3 != null) {
            return arrayList3.size();
        }
        ArrayList<String> arrayList4 = this.searchResultCommands;
        if (arrayList4 != null) {
            return arrayList4.size();
        }
        ArrayList<MediaDataController.KeywordResult> arrayList5 = this.searchResultSuggestions;
        if (arrayList5 != null) {
            return arrayList5.size();
        }
        return 0;
    }

    public int getItemViewType(int position) {
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 3;
        }
        if (this.searchResultBotContext == null) {
            return 0;
        }
        if (position != 0 || this.searchResultBotContextSwitch == null) {
            return 1;
        }
        return 2;
    }

    public void addHashtagsFromMessage(CharSequence message) {
        this.searchAdapterHelper.addHashtagsFromMessage(message);
    }

    public int getItemPosition(int i) {
        if (this.searchResultBotContext == null || this.searchResultBotContextSwitch == null) {
            return i;
        }
        return i - 1;
    }

    public Object getItem(int i) {
        if (this.searchResultBotContext != null) {
            TLRPC.TL_inlineBotSwitchPM tL_inlineBotSwitchPM = this.searchResultBotContextSwitch;
            if (tL_inlineBotSwitchPM != null) {
                if (i == 0) {
                    return tL_inlineBotSwitchPM;
                }
                i--;
            }
            if (i < 0 || i >= this.searchResultBotContext.size()) {
                return null;
            }
            return this.searchResultBotContext.get(i);
        }
        ArrayList<TLRPC.User> arrayList = this.searchResultUsernames;
        if (arrayList == null) {
            ArrayList<String> arrayList2 = this.searchResultHashtags;
            if (arrayList2 == null) {
                ArrayList<MediaDataController.KeywordResult> arrayList3 = this.searchResultSuggestions;
                if (arrayList3 == null) {
                    ArrayList<String> arrayList4 = this.searchResultCommands;
                    if (arrayList4 == null || i < 0 || i >= arrayList4.size()) {
                        return null;
                    }
                    if (this.searchResultCommandsUsers == null || (this.botsCount == 1 && !(this.info instanceof TLRPC.TL_channelFull))) {
                        return this.searchResultCommands.get(i);
                    }
                    if (this.searchResultCommandsUsers.get(i) != null) {
                        Object[] objArr = new Object[2];
                        objArr[0] = this.searchResultCommands.get(i);
                        objArr[1] = this.searchResultCommandsUsers.get(i) != null ? this.searchResultCommandsUsers.get(i).username : "";
                        return String.format("%s@%s", objArr);
                    }
                    return String.format("%s", new Object[]{this.searchResultCommands.get(i)});
                } else if (i < 0 || i >= arrayList3.size()) {
                    return null;
                } else {
                    return this.searchResultSuggestions.get(i);
                }
            } else if (i < 0 || i >= arrayList2.size()) {
                return null;
            } else {
                return this.searchResultHashtags.get(i);
            }
        } else if (i < 0 || i >= arrayList.size()) {
            return null;
        } else {
            return this.searchResultUsernames.get(i);
        }
    }

    public boolean isLongClickEnabled() {
        return (this.searchResultHashtags == null && this.searchResultCommands == null) ? false : true;
    }

    public boolean isBotCommands() {
        return this.searchResultCommands != null;
    }

    public boolean isBotContext() {
        return this.searchResultBotContext != null;
    }

    public boolean isBannedInline() {
        return this.foundContextBot != null && !this.inlineMediaEnabled;
    }

    public boolean isMediaLayout() {
        return this.contextMedia;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return this.foundContextBot == null || this.inlineMediaEnabled;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = new MentionCell(this.mContext);
            ((MentionCell) view).setIsDarkTheme(this.isDarkTheme);
        } else if (viewType == 1) {
            view = new ContextLinkCell(this.mContext);
            ((ContextLinkCell) view).setDelegate(new ContextLinkCell.ContextLinkCellDelegate() {
                public final void didPressedImage(ContextLinkCell contextLinkCell) {
                    MentionsAdapter.this.lambda$onCreateViewHolder$8$MentionsAdapter(contextLinkCell);
                }
            });
        } else if (viewType != 2) {
            TextView textView = new TextView(this.mContext);
            textView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            view = textView;
        } else {
            view = new BotSwitchCell(this.mContext);
        }
        return new RecyclerListView.Holder(view);
    }

    public /* synthetic */ void lambda$onCreateViewHolder$8$MentionsAdapter(ContextLinkCell cell) {
        this.delegate.onContextClick(cell.getResult());
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        boolean z = false;
        if (holder.getItemViewType() == 3) {
            TextView textView = (TextView) holder.itemView;
            TLRPC.Chat chat = this.parentFragment.getCurrentChat();
            if (chat == null) {
                return;
            }
            if (!ChatObject.hasAdminRights(chat) && chat.default_banned_rights != null && chat.default_banned_rights.send_inline) {
                textView.setText(LocaleController.getString("GlobalAttachInlineRestricted", R.string.GlobalAttachInlineRestricted));
            } else if (AndroidUtilities.isBannedForever(chat.banned_rights)) {
                textView.setText(LocaleController.getString("AttachInlineRestrictedForever", R.string.AttachInlineRestrictedForever));
            } else {
                textView.setText(LocaleController.formatString("AttachInlineRestricted", R.string.AttachInlineRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
            }
        } else if (this.searchResultBotContext != null) {
            boolean hasTop = this.searchResultBotContextSwitch != null;
            if (holder.getItemViewType() != 2) {
                if (hasTop) {
                    position--;
                }
                ContextLinkCell contextLinkCell = (ContextLinkCell) holder.itemView;
                TLRPC.BotInlineResult botInlineResult = this.searchResultBotContext.get(position);
                boolean z2 = this.contextMedia;
                boolean z3 = position != this.searchResultBotContext.size() - 1;
                if (hasTop && position == 0) {
                    z = true;
                }
                contextLinkCell.setLink(botInlineResult, z2, z3, z);
            } else if (hasTop) {
                ((BotSwitchCell) holder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else if (this.searchResultUsernames != null) {
            ((MentionCell) holder.itemView).setUser(this.searchResultUsernames.get(position));
        } else if (this.searchResultHashtags != null) {
            ((MentionCell) holder.itemView).setText(this.searchResultHashtags.get(position));
        } else if (this.searchResultSuggestions != null) {
            ((MentionCell) holder.itemView).setEmojiSuggestion(this.searchResultSuggestions.get(position));
        } else if (this.searchResultCommands != null) {
            MentionCell mentionCell = (MentionCell) holder.itemView;
            String str = this.searchResultCommands.get(position);
            String str2 = this.searchResultCommandsHelp.get(position);
            ArrayList<TLRPC.User> arrayList = this.searchResultCommandsUsers;
            mentionCell.setBotCommand(str, str2, arrayList != null ? arrayList.get(position) : null);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        TLRPC.User user;
        if (requestCode == 2 && (user = this.foundContextBot) != null && user.bot_inline_geo) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                onLocationUnavailable();
            } else {
                this.locationProvider.start();
            }
        }
    }
}
