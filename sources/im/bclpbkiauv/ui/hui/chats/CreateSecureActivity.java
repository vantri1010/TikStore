package im.bclpbkiauv.ui.hui.chats;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SecretChatHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseSearchViewFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.adapters.SearchAdapter;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.cells.ProfileSearchCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.adapter.CreateSecureAdapter;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;
import java.util.ArrayList;

public class CreateSecureActivity extends BaseSearchViewFragment implements NotificationCenter.NotificationCenterDelegate {
    private boolean allowBots = true;
    private boolean allowUsernameSearch = true;
    private boolean askAboutContacts = true;
    private boolean checkPermission = true;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean disableSections;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    private boolean floatingHidden;
    private SparseArray<TLRPC.User> ignoreUsers;
    /* access modifiers changed from: private */
    public boolean isCharClicked;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public CreateSecureAdapter listViewAdapter;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    private boolean needPhonebook;
    private AlertDialog permissionDialog;
    private int prevPosition;
    private int prevTop;
    private boolean resetDelegate = true;
    private boolean scrollUpdated;
    private FrameLayout searchLayout;
    private SearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private String selectAlertString = null;
    /* access modifiers changed from: private */
    public SideBar sideBar;
    private boolean sortByName;
    /* access modifiers changed from: private */
    public MryTextView textInfoCell;

    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC.User user, String str, CreateSecureActivity createSecureActivity);
    }

    public CreateSecureActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        this.checkPermission = UserConfig.getInstance(this.currentAccount).syncContacts;
        this.needPhonebook = true;
        ContactsController.getInstance(this.currentAccount).checkInviteText();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        initActionBar();
        this.fragmentView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (CreateSecureActivity.this.listView.getAdapter() != CreateSecureActivity.this.listViewAdapter) {
                    CreateSecureActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (CreateSecureActivity.this.emptyView.getVisibility() == 0) {
                    CreateSecureActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        super.createView(context);
        initList(frameLayout, context);
        initSideBar(frameLayout, context);
        return this.fragmentView;
    }

    /* access modifiers changed from: protected */
    public MrySearchView getSearchView() {
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        this.searchLayout = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ((FrameLayout) this.fragmentView).addView(this.searchLayout, LayoutHelper.createFrame(-1, 55.0f));
        this.searchView = new MrySearchView(getParentActivity());
        this.searchView.setHintText(LocaleController.getString("SearchForPeopleAndGroups", R.string.SearchForPeopleAndGroups));
        this.searchLayout.addView(this.searchView, LayoutHelper.createFrame(-1.0f, 35.0f, 17, 10.0f, 10.0f, 10.0f, 10.0f));
        return this.searchView;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    private void initActionBar() {
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("NewSecretChat", R.string.NewSecretChat));
        this.actionBar.setBackTitle(LocaleController.getString("Cancel", R.string.Cancel));
        this.actionBar.getBackTitleTextView().setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CreateSecureActivity.this.lambda$initActionBar$0$CreateSecureActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initActionBar$0$CreateSecureActivity(View v) {
        finishFragment();
    }

    private void initList(FrameLayout frameLayout, Context context) {
        initTextInfoCell(context);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrameSearchWithoutActionBar(-1, -1));
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, true, 0) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (CreateSecureActivity.this.listView != null && CreateSecureActivity.this.listView.getAdapter() == this) {
                    CreateSecureActivity.this.textInfoCell.setVisibility(super.getItemCount() == 0 ? 8 : 0);
                }
            }
        };
        AnonymousClass3 r2 = new CreateSecureAdapter(context) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (CreateSecureActivity.this.listView != null && CreateSecureActivity.this.listView.getAdapter() == this) {
                    int count = super.getItemCount();
                    int i = 0;
                    CreateSecureActivity.this.emptyView.setVisibility(count == 0 ? 0 : 8);
                    MryTextView access$300 = CreateSecureActivity.this.textInfoCell;
                    if (count == 0) {
                        i = 8;
                    }
                    access$300.setVisibility(i);
                }
            }
        };
        this.listViewAdapter = r2;
        r2.setDisableSections(true);
        AnonymousClass4 r22 = new RecyclerListView(context) {
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (CreateSecureActivity.this.emptyView != null) {
                    CreateSecureActivity.this.emptyView.setPadding(left, top, right, bottom);
                }
            }
        };
        this.listView = r22;
        r22.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(130.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CreateSecureActivity.this.lambda$initList$1$CreateSecureActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean unused = CreateSecureActivity.this.isCharClicked = false;
                if (newState == 1) {
                    if (CreateSecureActivity.this.searching && CreateSecureActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(CreateSecureActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!CreateSecureActivity.this.isCharClicked) {
                    CreateSecureActivity.this.sideBar.setChooseChar(CreateSecureActivity.this.listViewAdapter.getLetter(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
                }
            }
        });
    }

    public /* synthetic */ void lambda$initList$1$CreateSecureActivity(View view, int position) {
        TLRPC.User user;
        if (!this.searching || !this.searchWas) {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                Object item1 = this.listViewAdapter.getItem(section, row);
                if (item1 instanceof TLRPC.User) {
                    this.creatingChat = true;
                    SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), (TLRPC.User) item1);
                    return;
                }
                return;
            }
            return;
        }
        Object object = this.searchListViewAdapter.getItem(position);
        if ((object instanceof TLRPC.User) && (user = (TLRPC.User) object) != null) {
            if (this.searchListViewAdapter.isGlobalSearch(position)) {
                ArrayList<TLRPC.User> users = new ArrayList<>();
                users.add(user);
                MessagesController.getInstance(this.currentAccount).putUsers(users, false);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
            }
            if (user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                if (this.searchView != null && this.searchView.isSearchFieldVisible()) {
                    this.searchView.closeSearchField();
                }
                this.creatingChat = true;
                SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
            }
        }
    }

    private void initTextInfoCell(Context context) {
        MryTextView mryTextView = new MryTextView(context);
        this.textInfoCell = mryTextView;
        mryTextView.setTextSize(1, 14.0f);
        this.textInfoCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textInfoCell.setTextColor(Theme.getColor(Theme.key_graySectionText));
        this.textInfoCell.setGravity(17);
        this.textInfoCell.setText(LocaleController.getString("CreateSecuriteText", R.string.CreateSecuriteText));
        this.textInfoCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
        ((FrameLayout) this.fragmentView).addView(this.textInfoCell, LayoutHelper.createFrame(-1, 65, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(55.0f), AndroidUtilities.dp(10.0f), 0));
    }

    private void initSideBar(FrameLayout frameLayout, Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(50.0f);
        textView.setGravity(17);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        frameLayout.addView(textView, LayoutHelper.createFrame(100, 100, 17));
        SideBar sideBar2 = new SideBar(context);
        this.sideBar = sideBar2;
        sideBar2.setTextView(textView);
        frameLayout.addView(this.sideBar, LayoutHelper.createFrame(35.0f, -1.0f, 21, 0.0f, 45.0f, 0.0f, 45.0f));
        this.sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public final void onTouchingLetterChanged(String str) {
                CreateSecureActivity.this.lambda$initSideBar$2$CreateSecureActivity(str);
            }
        });
    }

    public /* synthetic */ void lambda$initSideBar$2$CreateSecureActivity(String s) {
        if ("↑".equals(s)) {
            this.listView.scrollToPosition(0);
        } else if ("☆".equals(s)) {
            this.listView.scrollToPosition(0);
        } else {
            int position = this.listViewAdapter.getPositionForSection(this.listViewAdapter.getSectionForChar(s.charAt(0)));
            if (position != -1) {
                this.listView.getLayoutManager().scrollToPosition(position);
                this.isCharClicked = true;
            }
        }
    }

    public void onResume() {
        super.onResume();
        CreateSecureAdapter createSecureAdapter = this.listViewAdapter;
        if (createSecureAdapter != null) {
            createSecureAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.searchView != null && this.searchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        CreateSecureAdapter createSecureAdapter;
        if (id == NotificationCenter.contactsDidLoad) {
            CreateSecureAdapter createSecureAdapter2 = this.listViewAdapter;
            if (createSecureAdapter2 != null) {
                createSecureAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                updateVisibleRows(mask);
            }
            if ((mask & 4) != 0 && !this.sortByName && (createSecureAdapter = this.listViewAdapter) != null) {
                createSecureAdapter.sortOnlineContacts();
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                Bundle args2 = new Bundle();
                args2.putInt("enc_id", args[0].id);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args2), true);
            }
        } else if (id == NotificationCenter.closeChats && !this.creatingChat) {
            removeSelfFromStack();
        }
    }

    private void updateVisibleRows(int mask) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    public void setDelegate(ContactsActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                CreateSecureActivity.this.lambda$getThemeDescriptions$3$CreateSecureActivity();
            }
        };
        ActionBar actionBar = this.actionBar;
        ActionBar actionBar2 = this.actionBar;
        ActionBar actionBar3 = this.actionBar;
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = this.listView;
        RecyclerListView recyclerListView3 = recyclerListView2;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        Drawable[] drawableArr = {Theme.avatar_savedDrawable};
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        RecyclerListView recyclerListView4 = this.listView;
        RecyclerListView recyclerListView5 = recyclerListView4;
        RecyclerListView recyclerListView6 = this.listView;
        RecyclerListView recyclerListView7 = recyclerListView6;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(actionBar2, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch), new ThemeDescription(actionBar3, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder), new ThemeDescription(recyclerListView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) recyclerListView3, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollActive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollInactive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, drawableArr, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription((View) recyclerListView5, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) recyclerListView7, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_graySectionText), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_nameIcon), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_verifiedCheck), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_verifiedBackground), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText3), new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint, Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_name), new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint, Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_secretName)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$3$CreateSecureActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                } else if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                }
            }
        }
    }

    public void onSearchExpand() {
        this.searching = true;
    }

    public boolean canCollapseSearch() {
        this.searchListViewAdapter.searchDialogs((String) null);
        this.searching = false;
        this.searchWas = false;
        this.listView.setAdapter(this.listViewAdapter);
        this.listViewAdapter.notifyDataSetChanged();
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView((View) null);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        return true;
    }

    public void onSearchCollapse() {
        this.searching = false;
        this.searchWas = false;
    }

    public void onTextChange(String text) {
        if (this.searchListViewAdapter != null) {
            if (text.length() != 0) {
                this.searchWas = true;
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null) {
                    recyclerListView.setAdapter(this.searchListViewAdapter);
                    this.searchListViewAdapter.notifyDataSetChanged();
                    this.listView.setVerticalScrollBarEnabled(false);
                }
                EmptyTextProgressView emptyTextProgressView = this.emptyView;
                if (emptyTextProgressView != null) {
                    this.listView.setEmptyView(emptyTextProgressView);
                    this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                }
            }
            this.searchListViewAdapter.searchDialogs(text);
        }
    }
}
