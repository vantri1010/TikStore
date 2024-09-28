package im.bclpbkiauv.ui.hui.contacts;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.SpanUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.NewContactActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
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
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hcells.UserBoxCell;
import im.bclpbkiauv.ui.hui.adapter.grouping.AddGroupingUserAdapter;
import im.bclpbkiauv.ui.hviews.MryEmptyTextProgressView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;
import java.util.ArrayList;
import java.util.List;

public class AddGroupingUserActivity extends BaseSearchViewFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int DONE_BUTTON = 1;
    public static final int type_add_grouping_user = 2;
    public static final int type_create_grouping = 1;
    private boolean allowBots = true;
    private boolean allowUsernameSearch = true;
    private boolean askAboutContacts = true;
    private boolean checkPermission = true;
    private SparseArray<TLRPC.User> checkedMap = new SparseArray<>();
    private boolean creatingChat;
    private AddGroupingUserActivityDelegate delegate;
    private boolean disableSections;
    private MryTextView doneTextView;
    /* access modifiers changed from: private */
    public MryEmptyTextProgressView emptyView;
    private boolean floatingHidden;
    private SparseArray<TLRPC.User> ignoreUsers;
    /* access modifiers changed from: private */
    public boolean isCharClicked;
    private boolean isShowSubTitle = true;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public AddGroupingUserAdapter listViewAdapter;
    private int maxCount = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;
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
    private String showTitle;
    /* access modifiers changed from: private */
    public SideBar sideBar;
    private boolean sortByName;
    private int type;

    public interface AddGroupingUserActivityDelegate {
        void didSelectedContact(ArrayList<TLRPC.User> arrayList);
    }

    public AddGroupingUserActivity(List<TLRPC.User> users, int type2, String showTitle2, boolean isShowSubTitle2) {
        for (TLRPC.User user : users) {
            this.checkedMap.put(user.id, user);
        }
        this.type = type2;
        this.showTitle = showTitle2;
        this.isShowSubTitle = isShowSubTitle2;
    }

    public AddGroupingUserActivity(List<TLRPC.User> users, int type2) {
        for (TLRPC.User user : users) {
            this.checkedMap.put(user.id, user);
        }
        this.type = type2;
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
                if (AddGroupingUserActivity.this.listView.getAdapter() != AddGroupingUserActivity.this.listViewAdapter) {
                    AddGroupingUserActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (AddGroupingUserActivity.this.emptyView.getVisibility() == 0) {
                    AddGroupingUserActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        super.createView(context);
        initList(frameLayout, context);
        initSideBar(frameLayout, context);
        updateHint();
        return this.fragmentView;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

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

    private void initActionBar() {
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setCastShadows(false);
        if (TextUtils.isEmpty(this.showTitle)) {
            this.showTitle = LocaleController.getString("SelectContact", R.string.SelectContact);
        }
        this.actionBar.setTitle(this.showTitle);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    AddGroupingUserActivity.this.finishFragment();
                } else if (id == 1) {
                    AddGroupingUserActivity.this.onDonePressed();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        MryTextView mryTextView = new MryTextView(getParentActivity());
        this.doneTextView = mryTextView;
        mryTextView.setText(LocaleController.getString("Done", R.string.Done));
        this.doneTextView.setTextSize(1, 14.0f);
        this.doneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.doneTextView.setGravity(16);
        menu.addItemView(1, this.doneTextView);
    }

    private void initList(FrameLayout frameLayout, Context context) {
        MryEmptyTextProgressView mryEmptyTextProgressView = new MryEmptyTextProgressView(context);
        this.emptyView = mryEmptyTextProgressView;
        mryEmptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.setTopImage(R.mipmap.img_empty_default);
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrameSearchWithoutActionBar(-1, -1));
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, true, 0);
        AnonymousClass3 r0 = new AddGroupingUserAdapter(context, this.type) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (AddGroupingUserActivity.this.listView != null && AddGroupingUserActivity.this.listView.getAdapter() == this) {
                    AddGroupingUserActivity.this.emptyView.setVisibility(super.getItemCount() == 0 ? 0 : 8);
                }
            }
        };
        this.listViewAdapter = r0;
        r0.setCheckedMap(this.checkedMap);
        this.listViewAdapter.setDisableSections(true);
        AnonymousClass4 r02 = new RecyclerListView(context) {
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (AddGroupingUserActivity.this.emptyView != null) {
                    AddGroupingUserActivity.this.emptyView.setPadding(left, top, right, bottom);
                }
            }
        };
        this.listView = r02;
        r02.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(55.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                AddGroupingUserActivity.this.lambda$initList$0$AddGroupingUserActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean unused = AddGroupingUserActivity.this.isCharClicked = true;
                if (newState == 1) {
                    if (AddGroupingUserActivity.this.searching && AddGroupingUserActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(AddGroupingUserActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!AddGroupingUserActivity.this.isCharClicked) {
                    AddGroupingUserActivity.this.sideBar.setChooseChar(AddGroupingUserActivity.this.listViewAdapter.getLetter(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
                }
            }
        });
    }

    public /* synthetic */ void lambda$initList$0$AddGroupingUserActivity(View view, int position) {
        if (!this.searching || !this.searchWas) {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                Object item = this.listViewAdapter.getItem(section, row);
                if (item instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) item;
                    if (user.bot) {
                        if (user.bot_nochats) {
                            ToastUtils.show((int) R.string.BotCantJoinGroups);
                            return;
                        }
                    } else if (this.checkedMap.indexOfKey(user.id) >= 0) {
                        this.checkedMap.remove(user.id);
                        View childAt = this.layoutManager.findViewByPosition(position);
                        if (childAt instanceof UserBoxCell) {
                            ((UserBoxCell) childAt).setChecked(false, true);
                        }
                    } else {
                        this.checkedMap.put(user.id, user);
                        View childAt2 = this.layoutManager.findViewByPosition(position);
                        if (childAt2 instanceof UserBoxCell) {
                            ((UserBoxCell) childAt2).setChecked(true, true);
                        }
                    }
                }
            } else {
                return;
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
                    if (!user2.bot) {
                        if (this.checkedMap.indexOfKey(user2.id) >= 0) {
                            this.checkedMap.remove(user2.id);
                        } else {
                            this.checkedMap.put(user2.id, user2);
                        }
                        this.listViewAdapter.setCheckedMap(this.checkedMap);
                        this.listViewAdapter.notifyDataSetChanged();
                    } else if (user2.bot_nochats) {
                        ToastUtils.show((int) R.string.BotCantJoinGroups);
                        return;
                    }
                } else {
                    return;
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
        updateHint();
        if (this.searchView != null && this.searchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
        }
    }

    private void initSideBar(FrameLayout frameLayout, Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize((float) AndroidUtilities.dp(18.0f));
        textView.setGravity(17);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setVisibility(8);
        frameLayout.addView(textView, LayoutHelper.createFrame(AndroidUtilities.dp(25.0f), AndroidUtilities.dp(25.0f), 17));
        SideBar sideBar2 = new SideBar(context);
        this.sideBar = sideBar2;
        sideBar2.setTextView(textView);
        frameLayout.addView(this.sideBar, LayoutHelper.createFrame(35.0f, -1.0f, 21, 0.0f, 45.0f, 0.0f, 45.0f));
        this.sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public final void onTouchingLetterChanged(String str) {
                AddGroupingUserActivity.this.lambda$initSideBar$1$AddGroupingUserActivity(str);
            }
        });
    }

    public /* synthetic */ void lambda$initSideBar$1$AddGroupingUserActivity(String s) {
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
        AddGroupingUserAdapter addGroupingUserAdapter = this.listViewAdapter;
        if (addGroupingUserAdapter != null) {
            addGroupingUserAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.searchView != null && this.searchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
        }
    }

    /* access modifiers changed from: private */
    public void onDonePressed() {
        if (this.checkedMap.size() > 0) {
            ArrayList<TLRPC.User> selectedUsers = new ArrayList<>();
            for (int a = 0; a < this.checkedMap.size(); a++) {
                selectedUsers.add(this.checkedMap.valueAt(a));
            }
            AddGroupingUserActivityDelegate addGroupingUserActivityDelegate = this.delegate;
            if (addGroupingUserActivityDelegate != null) {
                addGroupingUserActivityDelegate.didSelectedContact(selectedUsers);
            }
            finishFragment();
            return;
        }
        ToastUtils.show((int) R.string.AddGroupingUserTips);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        AddGroupingUserAdapter addGroupingUserAdapter;
        if (id == NotificationCenter.contactsDidLoad) {
            AddGroupingUserAdapter addGroupingUserAdapter2 = this.listViewAdapter;
            if (addGroupingUserAdapter2 != null) {
                addGroupingUserAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                updateVisibleRows(mask);
            }
            if ((mask & 4) != 0 && !this.sortByName && (addGroupingUserAdapter = this.listViewAdapter) != null) {
                addGroupingUserAdapter.sortOnlineContacts();
            }
        } else if (id != NotificationCenter.encryptedChatCreated && id == NotificationCenter.closeChats && !this.creatingChat) {
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

    private void updateHint() {
        if (this.isShowSubTitle) {
            if (this.checkedMap.size() != 0) {
                this.actionBar.setSubtitle(new SpanUtils().append(String.valueOf(this.checkedMap.size())).setForegroundColor(-16711808).append("/").append(String.valueOf(this.maxCount)).create());
            } else {
                ActionBar actionBar = this.actionBar;
                actionBar.setSubtitle("0/" + this.maxCount);
            }
        }
        updateDoneView(this.checkedMap.size() != 0);
    }

    private void updateDoneView(boolean en) {
        this.doneTextView.setEnabled(en);
    }

    public void setDelegate(AddGroupingUserActivityDelegate delegate2) {
        this.delegate = delegate2;
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
                MryEmptyTextProgressView mryEmptyTextProgressView = this.emptyView;
                if (mryEmptyTextProgressView != null) {
                    this.listView.setEmptyView(mryEmptyTextProgressView);
                    this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                }
            }
            this.searchListViewAdapter.searchDialogs(text);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                AddGroupingUserActivity.this.lambda$getThemeDescriptions$2$AddGroupingUserActivity();
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

    public /* synthetic */ void lambda$getThemeDescriptions$2$AddGroupingUserActivity() {
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
}
