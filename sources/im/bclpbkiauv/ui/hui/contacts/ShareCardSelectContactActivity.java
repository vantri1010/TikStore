package im.bclpbkiauv.ui.hui.contacts;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
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
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.NewContactActivity;
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
import im.bclpbkiauv.ui.components.GroupCreateSpan;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hcells.AvatarDelCell;
import im.bclpbkiauv.ui.hcells.UserBoxCell;
import im.bclpbkiauv.ui.hui.adapter.CreateGroupAdapter;
import im.bclpbkiauv.ui.hui.contacts.ShareCardSelectContactActivity;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;
import java.util.ArrayList;

public class ShareCardSelectContactActivity extends BaseSearchViewFragment implements NotificationCenter.NotificationCenterDelegate {
    private boolean allowBots = true;
    private boolean allowUsernameSearch = true;
    private boolean askAboutContacts = true;
    private int chatType = 4;
    private boolean checkPermission = true;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC.User> checkedMap = new SparseArray<>();
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean disableSections;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    private boolean floatingHidden;
    private CreateGroupHeaderAdapter headerAdapter;
    private FrameLayout headerLayout;
    /* access modifiers changed from: private */
    public RecyclerListView headerListView;
    private SparseArray<TLRPC.User> ignoreUsers;
    /* access modifiers changed from: private */
    public boolean isCharClicked;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public CreateGroupAdapter listViewAdapter;
    private int maxCount = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    private boolean needPhonebook;
    private MryTextView nextTextView;
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
    private SparseArray<GroupCreateSpan> selectedContacts = new SparseArray<>();
    /* access modifiers changed from: private */
    public SideBar sideBar;
    private boolean sortByName;
    /* access modifiers changed from: private */
    public MryTextView textInfoCell;

    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC.User user);
    }

    public ShareCardSelectContactActivity(Bundle args) {
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
        this.mblnMove = false;
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
                if (ShareCardSelectContactActivity.this.listView.getAdapter() != ShareCardSelectContactActivity.this.listViewAdapter) {
                    ShareCardSelectContactActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (ShareCardSelectContactActivity.this.emptyView.getVisibility() == 0) {
                    ShareCardSelectContactActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        super.createView(context);
        initHeaderView(frameLayout, context);
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
        this.searchView.setHintText(LocaleController.getString("Search", R.string.Search));
        this.searchView.setCancelTextColor(Color.parseColor("#999999"));
        this.searchLayout.addView(this.searchView, LayoutHelper.createFrame(-1.0f, 35.0f, 17, 10.0f, 10.0f, 10.0f, 10.0f));
        return this.searchView;
    }

    private void initActionBar() {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("SelectContact", R.string.SelectContact));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ShareCardSelectContactActivity.this.finishFragment();
                }
            }
        });
    }

    private void initHeaderView(FrameLayout frameLayout, Context context) {
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.headerLayout = frameLayout2;
        frameLayout2.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundGray)));
        frameLayout.addView(this.headerLayout, LayoutHelper.createFrame(-1, 35, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(55.0f), AndroidUtilities.dp(10.0f), 0));
        MryTextView createTextInfoCell = createTextInfoCell(context);
        this.textInfoCell = createTextInfoCell;
        this.headerLayout.addView(createTextInfoCell, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.headerListView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 0, false));
        this.headerListView.setHorizontalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.headerListView;
        CreateGroupHeaderAdapter createGroupHeaderAdapter = new CreateGroupHeaderAdapter();
        this.headerAdapter = createGroupHeaderAdapter;
        recyclerListView2.setAdapter(createGroupHeaderAdapter);
    }

    private void initList(FrameLayout frameLayout, Context context) {
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrameSearchWithoutActionBar(-1, -1));
        AnonymousClass3 r3 = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, true, 0) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (ShareCardSelectContactActivity.this.listView != null && ShareCardSelectContactActivity.this.listView.getAdapter() == this) {
                    super.getItemCount();
                }
            }
        };
        this.searchListViewAdapter = r3;
        r3.setMiViewType(1);
        AnonymousClass4 r2 = new CreateGroupAdapter(context) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (ShareCardSelectContactActivity.this.listView != null && ShareCardSelectContactActivity.this.listView.getAdapter() == this) {
                    ShareCardSelectContactActivity.this.emptyView.setVisibility(super.getItemCount() == 0 ? 0 : 8);
                }
            }
        };
        this.listViewAdapter = r2;
        r2.setDisableSections(true);
        this.listViewAdapter.setMiViewType(1);
        AnonymousClass5 r22 = new RecyclerListView(context) {
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (ShareCardSelectContactActivity.this.emptyView != null) {
                    ShareCardSelectContactActivity.this.emptyView.setPadding(left, top, right, bottom);
                }
            }
        };
        this.listView = r22;
        r22.setTag("rv_list");
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(83.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ShareCardSelectContactActivity.this.lambda$initList$0$ShareCardSelectContactActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean unused = ShareCardSelectContactActivity.this.isCharClicked = false;
                if (newState == 1) {
                    if (ShareCardSelectContactActivity.this.searching && ShareCardSelectContactActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(ShareCardSelectContactActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!ShareCardSelectContactActivity.this.isCharClicked) {
                    ShareCardSelectContactActivity.this.sideBar.setChooseChar(ShareCardSelectContactActivity.this.listViewAdapter.getLetter(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
                }
            }
        });
    }

    public /* synthetic */ void lambda$initList$0$ShareCardSelectContactActivity(View view, int position) {
        if (!this.searching || !this.searchWas) {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                Object item1 = this.listViewAdapter.getItem(section, row);
                if (item1 instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) item1;
                    if (!user.bot) {
                        if (this.checkedMap.indexOfKey(user.id) >= 0) {
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
                        this.listViewAdapter.setCheckedMap(this.checkedMap);
                    } else if (user.bot_nochats) {
                        ToastUtils.show((int) R.string.BotCantJoinGroups);
                        return;
                    }
                    ContactsActivityDelegate contactsActivityDelegate = this.delegate;
                    if (contactsActivityDelegate != null) {
                        contactsActivityDelegate.didSelectContact(user);
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
                    ContactsActivityDelegate contactsActivityDelegate2 = this.delegate;
                    if (contactsActivityDelegate2 != null) {
                        contactsActivityDelegate2.didSelectContact(user2);
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
        if (this.searchView != null && this.searchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
        }
        finishFragment();
    }

    private MryTextView createTextInfoCell(Context context) {
        MryTextView textView = new MryTextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(Theme.getColor(Theme.key_graySectionText));
        textView.setGravity(3);
        textView.setText(LocaleController.getString("BlockUserContactsTitle", R.string.BlockUserContactsTitle));
        return textView;
    }

    private void initSideBar(FrameLayout frameLayout, Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize((float) AndroidUtilities.dp(18.0f));
        textView.setGravity(17);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setVisibility(8);
        frameLayout.addView(textView, LayoutHelper.createFrame(AndroidUtilities.dp(25.0f), AndroidUtilities.dp(25.0f), 17));
        this.sideBar = new SideBar(context);
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        this.sideBar.setChars((String[]) sortedUsersSectionsArray.toArray(new String[sortedUsersSectionsArray.size()]));
        frameLayout.addView(this.sideBar, LayoutHelper.createFrame(35.0f, -2.0f, 5, 0.0f, 93.0f, 0.0f, 45.0f));
        this.sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public final void onTouchingLetterChanged(String str) {
                ShareCardSelectContactActivity.this.lambda$initSideBar$1$ShareCardSelectContactActivity(str);
            }
        });
    }

    public /* synthetic */ void lambda$initSideBar$1$ShareCardSelectContactActivity(String s) {
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
        CreateGroupAdapter createGroupAdapter = this.listViewAdapter;
        if (createGroupAdapter != null) {
            createGroupAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.searchView != null && this.searchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
        }
    }

    /* access modifiers changed from: private */
    public void updateHint() {
        if (this.checkedMap.size() != 0) {
            ActionBar actionBar = this.actionBar;
            actionBar.setSubtitle(this.checkedMap.size() + "/" + this.maxCount);
            return;
        }
        this.actionBar.setSubtitle((CharSequence) null);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        CreateGroupAdapter createGroupAdapter;
        if (id == NotificationCenter.contactsDidLoad) {
            CreateGroupAdapter createGroupAdapter2 = this.listViewAdapter;
            if (createGroupAdapter2 != null) {
                createGroupAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                updateVisibleRows(mask);
            }
            if ((mask & 4) != 0 && !this.sortByName && (createGroupAdapter = this.listViewAdapter) != null) {
                createGroupAdapter.sortOnlineContacts();
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

    public void setDelegate(ContactsActivityDelegate delegate2) {
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
                EmptyTextProgressView emptyTextProgressView = this.emptyView;
                if (emptyTextProgressView != null) {
                    this.listView.setEmptyView(emptyTextProgressView);
                    this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                }
            }
            this.searchListViewAdapter.searchDialogs(text);
        }
    }

    private class CreateGroupHeaderAdapter extends RecyclerListView.SelectionAdapter {
        private CreateGroupHeaderAdapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            AvatarDelCell cell = new AvatarDelCell(ShareCardSelectContactActivity.this.getParentActivity());
            cell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(65.0f), AndroidUtilities.dp(65.0f)));
            return new RecyclerListView.Holder(cell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            AvatarDelCell cell = (AvatarDelCell) holder.itemView;
            TLRPC.User user = (TLRPC.User) ShareCardSelectContactActivity.this.checkedMap.valueAt(position);
            cell.setUser(user);
            cell.setDelegate(new AvatarDelCell.AvatarDelDelegate(user) {
                private final /* synthetic */ TLRPC.User f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClickDelete() {
                    ShareCardSelectContactActivity.CreateGroupHeaderAdapter.this.lambda$onBindViewHolder$0$ShareCardSelectContactActivity$CreateGroupHeaderAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$ShareCardSelectContactActivity$CreateGroupHeaderAdapter(TLRPC.User user) {
            ShareCardSelectContactActivity.this.checkedMap.remove(user.id);
            notifyDataSetChanged();
            ShareCardSelectContactActivity.this.listViewAdapter.setCheckedMap(ShareCardSelectContactActivity.this.checkedMap);
            ShareCardSelectContactActivity.this.listViewAdapter.notifyDataSetChanged();
            ShareCardSelectContactActivity.this.updateHint();
        }

        public int getItemCount() {
            return ShareCardSelectContactActivity.this.checkedMap.size();
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (ShareCardSelectContactActivity.this.headerListView != null && ShareCardSelectContactActivity.this.headerListView.getAdapter() == this) {
                int count = getItemCount();
                int i = 8;
                ShareCardSelectContactActivity.this.headerListView.setVisibility(count == 0 ? 8 : 0);
                MryTextView access$1000 = ShareCardSelectContactActivity.this.textInfoCell;
                if (count == 0) {
                    i = 0;
                }
                access$1000.setVisibility(i);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ShareCardSelectContactActivity.this.lambda$getThemeDescriptions$2$ShareCardSelectContactActivity();
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

    public /* synthetic */ void lambda$getThemeDescriptions$2$ShareCardSelectContactActivity() {
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
