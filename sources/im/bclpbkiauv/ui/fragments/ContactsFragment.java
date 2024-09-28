package im.bclpbkiauv.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SecretChatHelper;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChannelCreateActivity;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.ContactAddActivity;
import im.bclpbkiauv.ui.ContactsActivity;
import im.bclpbkiauv.ui.GroupCreateActivity;
import im.bclpbkiauv.ui.GroupInviteActivity;
import im.bclpbkiauv.ui.NewContactActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.MenuDrawable;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.SearchAdapter;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.fragments.adapter.FmtContactsAdapter;
import im.bclpbkiauv.ui.hcells.ContactUserCell;
import im.bclpbkiauv.ui.hui.chats.MryDialogsActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.contacts.MyGroupingActivity;
import im.bclpbkiauv.ui.hui.contacts.NewFriendsActivity;
import im.bclpbkiauv.ui.hui.decoration.TopDecorationWithSearch;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.hviews.MryEmptyTextProgressView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;
import im.bclpbkiauv.ui.hviews.swipelist.SlidingItemMenuRecyclerView;
import java.util.ArrayList;

public class ContactsFragment extends BaseFmts implements NotificationCenter.NotificationCenterDelegate {
    private static final int ADD_BUTTON = 1;
    private ActionBarMenuItem addItem;
    private boolean allowBots = true;
    private boolean allowUsernameSearch = true;
    private boolean askAboutContacts = true;
    private int channelId;
    private int chatId;
    private boolean checkPermission = true;
    private Context context;
    private boolean createSecretChat;
    private boolean creatingChat;
    private FmtContactsDelegate delegate;
    private boolean destroyAfterSelect;
    private boolean disableSections;
    /* access modifiers changed from: private */
    public MryEmptyTextProgressView emptyView;
    private boolean floatingHidden;
    private boolean hasGps;
    private SparseArray<TLRPC.User> ignoreUsers;
    /* access modifiers changed from: private */
    public boolean isCharClicked;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public SlidingItemMenuRecyclerView listView;
    /* access modifiers changed from: private */
    public FmtContactsAdapter listViewAdapter;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    private boolean needPhonebook;
    private boolean onlyUsers;
    private int prevPosition;
    private int prevTop;
    private boolean resetDelegate = true;
    private boolean returnAsResult;
    private boolean scrollUpdated;
    /* access modifiers changed from: private */
    public FrameLayout searchLayout;
    /* access modifiers changed from: private */
    public SearchAdapter searchListViewAdapter;
    private MrySearchView searchView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private String selectAlertString = null;
    /* access modifiers changed from: private */
    public SideBar sideBar;
    private boolean sortByName;
    /* access modifiers changed from: private */
    public ActionBarMenuItem sortItem;

    public interface FmtContactsDelegate {
        void updateContactsApplyCount(int i);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactApplyUpdateCount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
        this.checkPermission = UserConfig.getInstance(this.currentAccount).syncContacts;
        if (this.arguments != null) {
            this.onlyUsers = this.arguments.getBoolean("onlyUsers", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.channelId = this.arguments.getInt("channelId", 0);
            this.needFinishFragment = this.arguments.getBoolean("needFinishFragment", true);
            this.chatId = this.arguments.getInt("chat_id", 0);
            this.disableSections = this.arguments.getBoolean("disableSections", false);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", false);
        } else {
            this.needPhonebook = true;
        }
        if (!this.createSecretChat && !this.returnAsResult) {
            this.sortByName = SharedConfig.sortContactsByName;
        }
        ContactsController.getInstance(this.currentAccount).checkInviteText();
    }

    private void initActionBar(FrameLayout frameLayout) {
        this.actionBar = createActionBar();
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Contacts", R.string.Contacts));
        this.actionBar.setBackButtonDrawable(new MenuDrawable());
        this.actionBar.getBackButton().setVisibility(8);
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setDelegate(new ActionBar.ActionBarDelegate() {
            public final void onSearchFieldVisibilityChanged(boolean z) {
                ContactsFragment.this.lambda$initActionBar$0$ContactsFragment(z);
            }
        });
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    ContactsFragment.this.presentFragment(new AddContactsActivity());
                }
            }
        });
        this.addItem = this.actionBar.createMenu().addItem(1, (int) R.mipmap.ic_add_circle);
    }

    public /* synthetic */ void lambda$initActionBar$0$ContactsFragment(boolean visible) {
        this.actionBar.getBackButton().setVisibility(visible ? 0 : 8);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = getActivity();
        this.searching = false;
        this.searchWas = false;
        this.fragmentView = new FrameLayout(this.context) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                ContactsFragment.this.emptyView.setTranslationY((float) AndroidUtilities.dp(250.0f));
                if (ContactsFragment.this.listView.getAdapter() == ContactsFragment.this.listViewAdapter) {
                    ContactsFragment.this.emptyView.getVisibility();
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        initActionBar(frameLayout);
        initList(frameLayout);
        initSearchView(frameLayout);
        initSideBar(frameLayout);
        return this.fragmentView;
    }

    private void initSearchView(final FrameLayout frameLayout) {
        FrameLayout frameLayout2 = new FrameLayout(this.context);
        this.searchLayout = frameLayout2;
        frameLayout.addView(frameLayout2, LayoutHelper.createFrameWithActionBar(-1, 55));
        MrySearchView mrySearchView = new MrySearchView(this.context);
        this.searchView = mrySearchView;
        mrySearchView.setHintText(LocaleController.getString("SearchForPeopleAndGroups", R.string.SearchForPeopleAndGroups));
        this.searchLayout.setBackgroundColor(Theme.getColor(Theme.key_searchview_solidColor));
        this.searchView.setEditTextBackground(getParentActivity().getDrawable(R.drawable.shape_edit_bg));
        this.searchLayout.addView(this.searchView, LayoutHelper.createFrame(-1.0f, 35.0f, 17, 10.0f, 10.0f, 10.0f, 10.0f));
        this.searchView.setiSearchViewDelegate(new MrySearchView.ISearchViewDelegate() {
            public void onStart(boolean focus) {
                if (focus) {
                    ContactsFragment.this.hideTitle(frameLayout);
                } else {
                    ContactsFragment.this.showTitle(frameLayout);
                }
            }

            public void onSearchExpand() {
                boolean unused = ContactsFragment.this.searching = true;
                if (ContactsFragment.this.sortItem != null) {
                    ContactsFragment.this.sortItem.setVisibility(8);
                }
            }

            public boolean canCollapseSearch() {
                ContactsFragment.this.searchListViewAdapter.searchDialogs((String) null);
                boolean unused = ContactsFragment.this.searching = false;
                boolean unused2 = ContactsFragment.this.searchWas = false;
                ContactsFragment.this.listView.setAdapter(ContactsFragment.this.listViewAdapter);
                ContactsFragment.this.listView.setSectionsType(1);
                ContactsFragment.this.listViewAdapter.notifyDataSetChanged();
                ContactsFragment.this.listView.setFastScrollVisible(true);
                ContactsFragment.this.listView.setVerticalScrollBarEnabled(false);
                ContactsFragment.this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
                ContactsFragment.this.emptyView.setTopImage(R.mipmap.img_empty_default);
                if (ContactsFragment.this.sortItem != null) {
                    ContactsFragment.this.sortItem.setVisibility(0);
                }
                return true;
            }

            public void onSearchCollapse() {
                boolean unused = ContactsFragment.this.searching = false;
                boolean unused2 = ContactsFragment.this.searchWas = false;
            }

            public void onTextChange(String text) {
                if (ContactsFragment.this.searchListViewAdapter != null) {
                    if (text.length() != 0) {
                        boolean unused = ContactsFragment.this.searchWas = true;
                        if (ContactsFragment.this.listView != null) {
                            ContactsFragment.this.listView.setAdapter(ContactsFragment.this.searchListViewAdapter);
                            ContactsFragment.this.listView.setSectionsType(0);
                            ContactsFragment.this.searchListViewAdapter.notifyDataSetChanged();
                            ContactsFragment.this.listView.setFastScrollVisible(false);
                            ContactsFragment.this.listView.setVerticalScrollBarEnabled(true);
                        }
                        if (ContactsFragment.this.emptyView != null) {
                            ContactsFragment.this.emptyView.setTopImage(R.mipmap.img_empty_default);
                            ContactsFragment.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                        }
                    }
                    ContactsFragment.this.searchListViewAdapter.searchDialogs(text);
                }
            }

            public void onActionSearch(String trim) {
            }
        });
    }

    private void initSideBar(FrameLayout frameLayout) {
        TextView textView = new TextView(this.context);
        textView.setTextSize(50.0f);
        textView.setGravity(17);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        frameLayout.addView(textView, LayoutHelper.createFrame(100, 100, 17));
        SideBar sideBar2 = new SideBar(this.context);
        this.sideBar = sideBar2;
        sideBar2.setTextView(textView);
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        this.sideBar.setChars((String[]) sortedUsersSectionsArray.toArray(new String[sortedUsersSectionsArray.size()]));
        frameLayout.addView(this.sideBar, LayoutHelper.createFrame(35.0f, -2.0f, 21, 0.0f, 90.0f, 0.0f, 0.0f));
        this.sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public final void onTouchingLetterChanged(String str) {
                ContactsFragment.this.lambda$initSideBar$1$ContactsFragment(str);
            }
        });
    }

    public /* synthetic */ void lambda$initSideBar$1$ContactsFragment(String s) {
        int position;
        if ("↑".equals(s)) {
            this.listView.scrollToPosition(0);
            this.searchLayout.setScrollY(0);
        } else if (!"☆".equals(s) && (position = this.listViewAdapter.getPositionForSection(this.listViewAdapter.getSectionForChar(s.charAt(0)))) != -1) {
            this.listView.getLayoutManager().scrollToPosition(position);
            this.isCharClicked = true;
        }
    }

    private void initList(FrameLayout frameLayout) {
        int inviteViaLink;
        LinearLayout container = new LinearLayout(this.context);
        frameLayout.addView(container, LayoutHelper.createFrameWithActionBar(-1, -1));
        this.searchListViewAdapter = new SearchAdapter(this.context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, true, 0);
        if (this.chatId != 0) {
            inviteViaLink = ChatObject.canUserDoAdminAction(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId)), 3);
        } else if (this.channelId != 0) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.channelId));
            inviteViaLink = (!ChatObject.canUserDoAdminAction(chat, 3) || !TextUtils.isEmpty(chat.username)) ? 0 : 2;
        } else {
            inviteViaLink = 0;
        }
        try {
            this.hasGps = ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature("android.hardware.location.gps");
        } catch (Throwable th) {
            this.hasGps = false;
        }
        AnonymousClass4 r6 = new FmtContactsAdapter(this.context, this.onlyUsers ? 1 : 0, this.needPhonebook, this.ignoreUsers, (int) inviteViaLink, this.hasGps) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (ContactsFragment.this.listView != null && ContactsFragment.this.listView.getAdapter() == this) {
                    ContactsFragment.this.emptyView.setVisibility(super.getItemCount() == 2 ? 0 : 8);
                }
            }
        };
        this.listViewAdapter = r6;
        r6.setDelegate(new FmtContactsAdapter.FmtContactsAdapterDelegate() {
            public final void onDeleteItem(int i) {
                ContactsFragment.this.lambda$initList$2$ContactsFragment(i);
            }
        });
        this.listViewAdapter.setOnContactHeaderItemClickListener(new FmtContactsAdapter.OnContactHeaderItemClickListener() {
            public final void onItemClick(View view) {
                ContactsFragment.this.lambda$initList$3$ContactsFragment(view);
            }
        });
        this.listViewAdapter.setSortType(1);
        this.listViewAdapter.setDisableSections(true);
        this.listViewAdapter.setClassGuid(getClassGuid());
        MryEmptyTextProgressView mryEmptyTextProgressView = new MryEmptyTextProgressView(this.context);
        this.emptyView = mryEmptyTextProgressView;
        mryEmptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.setTopImage(R.mipmap.img_empty_default);
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -2, 17));
        AnonymousClass5 r1 = new SlidingItemMenuRecyclerView(this.context) {
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (ContactsFragment.this.emptyView != null) {
                    ContactsFragment.this.emptyView.setPadding(left, 0, right, bottom);
                }
            }
        };
        this.listView = r1;
        r1.setOverScrollMode(2);
        this.listView.setSectionsType(1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.addItemDecoration(new TopDecorationWithSearch());
        SlidingItemMenuRecyclerView slidingItemMenuRecyclerView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context, 1, false);
        this.layoutManager = linearLayoutManager;
        slidingItemMenuRecyclerView.setLayoutManager(linearLayoutManager);
        this.listView.setAdapter(this.listViewAdapter);
        container.addView(this.listView, LayoutHelper.createLinear(-1, -1, 10.0f, 0.0f, 10.0f, 0.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener((int) inviteViaLink) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                ContactsFragment.this.lambda$initList$5$ContactsFragment(this.f$1, view, i);
            }
        });
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean unused = ContactsFragment.this.isCharClicked = false;
                if (newState == 1) {
                    if (ContactsFragment.this.searching && ContactsFragment.this.searchWas) {
                        AndroidUtilities.hideKeyboard(ContactsFragment.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int off = recyclerView.computeVerticalScrollOffset();
                if (off >= 0) {
                    ContactsFragment.this.searchLayout.setScrollY(off > AndroidUtilities.dp(55.0f) ? AndroidUtilities.dp(55.0f) : off);
                }
                if (!ContactsFragment.this.isCharClicked) {
                    int firstPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    String s = ContactsFragment.this.listViewAdapter.getLetter(firstPosition);
                    if (TextUtils.isEmpty(s) && ContactsFragment.this.listViewAdapter.getSectionForPosition(firstPosition) == 0) {
                        s = ContactsFragment.this.listViewAdapter.getLetter(ContactsFragment.this.listViewAdapter.getPositionForSection(1));
                    }
                    ContactsFragment.this.sideBar.setChooseChar(s);
                }
            }
        });
    }

    public /* synthetic */ void lambda$initList$2$ContactsFragment(int userId) {
        Bundle args = new Bundle();
        args.putInt("user_id", userId);
        presentFragment(new ContactAddActivity(args));
    }

    public /* synthetic */ void lambda$initList$3$ContactsFragment(View view) {
        int id = view.getId();
        if (id != R.id.ll_new_friend) {
            switch (id) {
                case R.id.ll_my_channel /*2131296940*/:
                    Bundle args2 = new Bundle();
                    args2.putInt("dialogsType", 5);
                    presentFragment(new MryDialogsActivity(args2));
                    return;
                case R.id.ll_my_group /*2131296941*/:
                    Bundle args1 = new Bundle();
                    args1.putInt("dialogsType", 6);
                    presentFragment(new MryDialogsActivity(args1));
                    return;
                case R.id.ll_my_grouping /*2131296942*/:
                    presentFragment(new MyGroupingActivity());
                    return;
                default:
                    return;
            }
        } else {
            presentFragment(new NewFriendsActivity());
        }
    }

    public /* synthetic */ void lambda$initList$5$ContactsFragment(int inviteViaLink, View view, int position) {
        if (!this.searching || !this.searchWas) {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                if ((this.onlyUsers && inviteViaLink == 0) || section != 0) {
                    Object item1 = this.listViewAdapter.getItem(section, row);
                    if (item1 instanceof TLRPC.User) {
                        TLRPC.User user = (TLRPC.User) item1;
                        if (this.returnAsResult) {
                            SparseArray<TLRPC.User> sparseArray = this.ignoreUsers;
                            if (sparseArray != null && sparseArray.indexOfKey(user.id) >= 0) {
                            }
                        } else if (this.createSecretChat) {
                            this.creatingChat = true;
                            SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                        } else {
                            Bundle args = new Bundle();
                            args.putInt("user_id", user.id);
                            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, getCurrentFragment())) {
                                presentFragment(new ChatActivity(args));
                            }
                        }
                    } else if (item1 instanceof ContactsController.Contact) {
                        ContactsController.Contact contact = (ContactsController.Contact) item1;
                        String usePhone = null;
                        if (!contact.phones.isEmpty()) {
                            usePhone = contact.phones.get(0);
                        }
                        if (usePhone != null && getParentActivity() != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                            builder.setMessage(LocaleController.getString("InviteUser", R.string.InviteUser));
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(usePhone) {
                                private final /* synthetic */ String f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ContactsFragment.this.lambda$null$4$ContactsFragment(this.f$1, dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                            showDialog(builder.create());
                        }
                    }
                } else if (this.needPhonebook) {
                } else {
                    if (inviteViaLink != 0) {
                        if (row == 0) {
                            int i = this.chatId;
                            if (i == 0) {
                                i = this.channelId;
                            }
                            presentFragment(new GroupInviteActivity(i));
                        }
                    } else if (row == 0) {
                        presentFragment(new GroupCreateActivity(new Bundle()));
                    } else if (row == 1) {
                        Bundle args2 = new Bundle();
                        args2.putBoolean("onlyUsers", true);
                        args2.putBoolean("destroyAfterSelect", true);
                        args2.putBoolean("createSecretChat", true);
                        args2.putBoolean("allowBots", false);
                        presentFragment(new ContactsActivity(args2), false);
                    } else if (row == 2) {
                        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                        if (BuildVars.DEBUG_VERSION || !preferences.getBoolean("channel_intro", false)) {
                            presentFragment(new ActionIntroActivity(0));
                            preferences.edit().putBoolean("channel_intro", true).commit();
                            return;
                        }
                        Bundle args3 = new Bundle();
                        args3.putInt("step", 0);
                        presentFragment(new ChannelCreateActivity(args3));
                    }
                }
            }
        } else {
            Object object = this.searchListViewAdapter.getItem(position);
            if (object instanceof TLRPC.User) {
                TLRPC.User user2 = (TLRPC.User) object;
                if (user2 != null) {
                    if (this.searchListViewAdapter.isGlobalSearch(position)) {
                        ArrayList<TLRPC.User> users = new ArrayList<>();
                        users.add(user2);
                        MessagesController.getInstance(this.currentAccount).putUsers(users, false);
                        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
                    }
                    if (this.returnAsResult) {
                        SparseArray<TLRPC.User> sparseArray2 = this.ignoreUsers;
                        if (sparseArray2 != null && sparseArray2.indexOfKey(user2.id) >= 0) {
                        }
                    } else if (!this.createSecretChat) {
                        if (!user2.contact && !user2.bot) {
                            getMessagesController();
                            if (!MessagesController.isSupportUser(user2)) {
                                presentFragment(new AddContactsInfoActivity((Bundle) null, user2));
                                return;
                            }
                        }
                        Bundle args4 = new Bundle();
                        args4.putInt("user_id", user2.id);
                        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args4, getCurrentFragment())) {
                            presentFragment(new ChatActivity(args4), false);
                        }
                    } else if (user2.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        this.creatingChat = true;
                        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user2);
                    }
                }
            } else if (object instanceof String) {
                String str = (String) object;
                if (!str.equals("section")) {
                    NewContactActivity activity = new NewContactActivity();
                    activity.setInitialPhoneNumber(str);
                    presentFragment(activity);
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$4$ContactsFragment(String arg1, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", arg1, (String) null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void lazyLoadData() {
        super.lazyLoadData();
        FmtContactsAdapter fmtContactsAdapter = this.listViewAdapter;
        if (fmtContactsAdapter != null) {
            fmtContactsAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onResumeForBaseFragment() {
        super.onResumeForBaseFragment();
        FmtContactsAdapter fmtContactsAdapter = this.listViewAdapter;
        if (fmtContactsAdapter != null) {
            fmtContactsAdapter.notifyDataSetChanged();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactApplyUpdateCount);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onPause() {
        super.onPause();
        closeSearchView(false);
    }

    public void closeSearchView(boolean anim) {
        MrySearchView mrySearchView = this.searchView;
        if (mrySearchView != null && mrySearchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField(anim);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        RecyclerView.ViewHolder holder;
        FmtContactsAdapter fmtContactsAdapter;
        if (id == NotificationCenter.contactsDidLoad) {
            FmtContactsAdapter fmtContactsAdapter2 = this.listViewAdapter;
            if (fmtContactsAdapter2 != null) {
                fmtContactsAdapter2.notifyDataSetChanged();
            }
            ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
            String[] chars = (String[]) sortedUsersSectionsArray.toArray(new String[sortedUsersSectionsArray.size()]);
            SideBar sideBar2 = this.sideBar;
            if (sideBar2 != null) {
                sideBar2.setChars(chars);
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                updateVisibleRows(mask);
            }
            if ((mask & 4) != 0 && !this.sortByName && (fmtContactsAdapter = this.listViewAdapter) != null) {
                fmtContactsAdapter.sortOnlineContacts();
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                Bundle args2 = new Bundle();
                args2.putInt("enc_id", args[0].id);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args2));
            }
        } else if (id == NotificationCenter.contactApplyUpdateCount) {
            FmtContactsAdapter fmtContactsAdapter3 = this.listViewAdapter;
            if (fmtContactsAdapter3 != null) {
                fmtContactsAdapter3.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.userFullInfoDidLoad && args[0] != null) {
            int userId = args[0].intValue();
            FmtContactsAdapter fmtContactsAdapter4 = this.listViewAdapter;
            if (fmtContactsAdapter4 != null && (holder = this.listView.findViewHolderForAdapterPosition(fmtContactsAdapter4.getItemPosition(userId))) != null) {
                ((ContactUserCell) holder.itemView.findViewById(R.id.contactUserCell)).setUserFull(args[1]);
            }
        }
    }

    private void updateVisibleRows(int mask) {
        SlidingItemMenuRecyclerView slidingItemMenuRecyclerView = this.listView;
        if (slidingItemMenuRecyclerView != null) {
            int count = slidingItemMenuRecyclerView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    public void setDelegate(FmtContactsDelegate delegate2) {
        this.delegate = delegate2;
    }

    public boolean onBackPressed() {
        MrySearchView mrySearchView = this.searchView;
        if (mrySearchView == null || !mrySearchView.isSearchFieldVisible()) {
            return super.onBackPressed();
        }
        this.searchView.closeSearchField();
        return true;
    }
}
