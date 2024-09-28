package im.bclpbkiauv.ui.hui.chats;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChannelCreateActivity;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.GroupCreateActivity;
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
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.decoration.StickyDecoration;
import im.bclpbkiauv.ui.decoration.listener.GroupListener;
import im.bclpbkiauv.ui.hui.adapter.NewChatAdapter;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;
import java.util.ArrayList;

public class NewChatActivity extends BaseSearchViewFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int DONE_BUTTON = 1;
    private static final int search_button = 0;
    private static final int sort_button = 2;
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
    public NewChatAdapter listViewAdapter;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    /* access modifiers changed from: private */
    public boolean needPhonebook;
    private AlertDialog permissionDialog;
    private int prevPosition;
    private int prevTop;
    private boolean resetDelegate = true;
    private boolean scrollUpdated;
    private SearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private String selectAlertString = null;
    /* access modifiers changed from: private */
    public SideBar sideBar;
    private boolean sortByName;

    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC.User user, String str, NewChatActivity newChatActivity);
    }

    public NewChatActivity(Bundle args) {
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

    /* access modifiers changed from: protected */
    public MrySearchView getSearchView() {
        this.searchView = new MrySearchView(getParentActivity());
        ((FrameLayout) this.fragmentView).addView(this.searchView, LayoutHelper.createFrame(-1, 46.0f));
        return this.searchView;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.fragmentView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (NewChatActivity.this.listView.getAdapter() != NewChatActivity.this.listViewAdapter) {
                    NewChatActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (NewChatActivity.this.emptyView.getVisibility() == 0) {
                    NewChatActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        initActionBar();
        super.createView(context);
        initList(frameLayout, context);
        initSideBar(frameLayout, context);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("NewChat", R.string.NewChat));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    NewChatActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addRightItemView(1, LocaleController.getString("Cancel", R.string.Cancel));
    }

    /* access modifiers changed from: protected */
    public void initSearchView() {
        super.initSearchView();
        this.searchView.setHintText(LocaleController.getString("Search", R.string.Search));
        View headerShadow = new View(getParentActivity());
        headerShadow.setBackgroundResource(R.drawable.header_shadow);
        ((FrameLayout) this.fragmentView).addView(headerShadow, LayoutHelper.createFrame(-1, 1, 0, AndroidUtilities.dp(45.0f), 0, 0));
    }

    private void initList(FrameLayout frameLayout, Context context) {
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrameSearchWithoutActionBar(-1, -1));
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, true, 0);
        AnonymousClass3 r3 = new NewChatAdapter(context) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (NewChatActivity.this.listView != null && NewChatActivity.this.listView.getAdapter() == this) {
                    int count = super.getItemCount();
                    int i = 0;
                    if (NewChatActivity.this.needPhonebook) {
                        EmptyTextProgressView access$200 = NewChatActivity.this.emptyView;
                        if (count != 2) {
                            i = 8;
                        }
                        access$200.setVisibility(i);
                        return;
                    }
                    EmptyTextProgressView access$2002 = NewChatActivity.this.emptyView;
                    if (count != 0) {
                        i = 8;
                    }
                    access$2002.setVisibility(i);
                }
            }
        };
        this.listViewAdapter = r3;
        r3.setDisableSections(true);
        AnonymousClass4 r32 = new RecyclerListView(context) {
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (NewChatActivity.this.emptyView != null) {
                    NewChatActivity.this.emptyView.setPadding(left, top, right, bottom);
                }
            }
        };
        this.listView = r32;
        r32.setHasFixedSize(true);
        this.listView.setNestedScrollingEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.addItemDecoration(StickyDecoration.Builder.init(new GroupListener() {
            public String getGroupName(int position) {
                if (NewChatActivity.this.listViewAdapter.getItemCount() <= position || position <= -1) {
                    return null;
                }
                return NewChatActivity.this.listViewAdapter.getLetter(position);
            }
        }).setGroupBackground(Theme.getColor(Theme.key_list_decorationBackground)).setGroupTextColor(Theme.getColor(Theme.key_list_decorationTextColor)).setGroupTextSize(AndroidUtilities.dp(13.0f)).setGroupTextTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf")).setGroupHeight(AndroidUtilities.dp(24.0f)).setOffset(3).setTextSideMargin(AndroidUtilities.dp(15.0f)).build());
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrameSearchWithoutActionBar(-1, -1));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                NewChatActivity.this.lambda$initList$1$NewChatActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean unused = NewChatActivity.this.isCharClicked = false;
                if (newState == 1) {
                    if (NewChatActivity.this.searching && NewChatActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(NewChatActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!NewChatActivity.this.isCharClicked) {
                    NewChatActivity.this.sideBar.setChooseChar(NewChatActivity.this.listViewAdapter.getLetter(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
                }
            }
        });
    }

    public /* synthetic */ void lambda$initList$1$NewChatActivity(View view, int position) {
        if (!this.searching || !this.searchWas) {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                if (section != 0) {
                    Object item1 = this.listViewAdapter.getItem(section, row);
                    if (item1 instanceof TLRPC.User) {
                        Bundle args = new Bundle();
                        args.putInt("user_id", ((TLRPC.User) item1).id);
                        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                            presentFragment(new ChatActivity(args), true);
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
                                    NewChatActivity.this.lambda$null$0$NewChatActivity(this.f$1, dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                            showDialog(builder.create());
                        }
                    }
                } else if (this.needPhonebook) {
                    if (row == 0) {
                        presentFragment(new CreateSecureActivity(new Bundle()), false);
                    } else if (row == 1) {
                        presentFragment(new CreateGroupActivity(new Bundle()));
                    } else if (row == 2) {
                        Bundle args2 = new Bundle();
                        args2.putInt("step", 0);
                        presentFragment(new ChannelCreateActivity(args2));
                    }
                } else if (row == 0) {
                    presentFragment(new GroupCreateActivity(new Bundle()), false);
                } else if (row == 1) {
                    Bundle args3 = new Bundle();
                    args3.putBoolean("onlyUsers", true);
                    args3.putBoolean("destroyAfterSelect", true);
                    args3.putBoolean("createSecretChat", true);
                    args3.putBoolean("allowBots", false);
                    presentFragment(new NewChatActivity(args3), false);
                } else if (row == 2) {
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (BuildVars.DEBUG_VERSION || !preferences.getBoolean("channel_intro", false)) {
                        presentFragment(new ActionIntroActivity(0));
                        preferences.edit().putBoolean("channel_intro", true).commit();
                        return;
                    }
                    Bundle args4 = new Bundle();
                    args4.putInt("step", 0);
                    presentFragment(new ChannelCreateActivity(args4));
                }
            }
        } else {
            Object object = this.searchListViewAdapter.getItem(position);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                if (user != null) {
                    if (this.searchListViewAdapter.isGlobalSearch(position)) {
                        ArrayList<TLRPC.User> users = new ArrayList<>();
                        users.add(user);
                        MessagesController.getInstance(this.currentAccount).putUsers(users, false);
                        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
                    }
                    Bundle args5 = new Bundle();
                    args5.putInt("user_id", user.id);
                    if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args5, this)) {
                        presentFragment(new ChatActivity(args5), true);
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

    public /* synthetic */ void lambda$null$0$NewChatActivity(String arg1, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", arg1, (String) null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
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
                NewChatActivity.this.lambda$initSideBar$2$NewChatActivity(str);
            }
        });
    }

    public /* synthetic */ void lambda$initSideBar$2$NewChatActivity(String s) {
        int position;
        if ("↑".equals(s)) {
            this.listView.scrollToPosition(0);
        } else if (!"☆".equals(s) && (position = this.listViewAdapter.getPositionForSection(this.listViewAdapter.getSectionForChar(s.charAt(0)))) != -1) {
            this.listView.getLayoutManager().scrollToPosition(position);
            this.isCharClicked = true;
        }
    }

    public void onResume() {
        super.onResume();
        NewChatAdapter newChatAdapter = this.listViewAdapter;
        if (newChatAdapter != null) {
            newChatAdapter.notifyDataSetChanged();
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
        NewChatAdapter newChatAdapter;
        if (id == NotificationCenter.contactsDidLoad) {
            NewChatAdapter newChatAdapter2 = this.listViewAdapter;
            if (newChatAdapter2 != null) {
                newChatAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                updateVisibleRows(mask);
            }
            if ((mask & 4) != 0 && !this.sortByName && (newChatAdapter = this.listViewAdapter) != null) {
                newChatAdapter.sortOnlineContacts();
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

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                NewChatActivity.this.lambda$getThemeDescriptions$3$NewChatActivity();
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

    public /* synthetic */ void lambda$getThemeDescriptions$3$NewChatActivity() {
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
