package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.adapters.ContactsAdapter;
import im.bclpbkiauv.ui.adapters.SearchAdapter;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.cells.ProfileSearchCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.util.ArrayList;

public class ContactsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int search_button = 0;
    private static final int sort_button = 1;
    private boolean allowBots = true;
    private boolean allowUsernameSearch = true;
    private boolean askAboutContacts = true;
    private int channelId;
    private int chatId;
    private boolean checkPermission = true;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private boolean disableSections;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    private ImageView floatingButton;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public boolean floatingHidden;
    private AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private boolean hasGps;
    private SparseArray<TLRPC.User> ignoreUsers;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ContactsAdapter listViewAdapter;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    /* access modifiers changed from: private */
    public boolean needPhonebook;
    private boolean onlyUsers;
    private AlertDialog permissionDialog;
    /* access modifiers changed from: private */
    public int prevPosition;
    /* access modifiers changed from: private */
    public int prevTop;
    private boolean resetDelegate = true;
    private boolean returnAsResult;
    /* access modifiers changed from: private */
    public boolean scrollUpdated;
    /* access modifiers changed from: private */
    public SearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private String selectAlertString = null;
    /* access modifiers changed from: private */
    public boolean sortByName;
    /* access modifiers changed from: private */
    public ActionBarMenuItem sortItem;

    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity);
    }

    public ContactsActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
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
        int inviteViaLink;
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (!this.destroyAfterSelect) {
            this.actionBar.setTitle(LocaleController.getString("Contacts", R.string.Contacts));
        } else if (this.returnAsResult) {
            this.actionBar.setTitle(LocaleController.getString("SelectContact", R.string.SelectContact));
        } else if (this.createSecretChat) {
            this.actionBar.setTitle(LocaleController.getString("NewSecretChat", R.string.NewSecretChat));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", R.string.NewMessageTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ContactsActivity.this.finishFragment();
                    return;
                }
                int i = 1;
                if (id == 1) {
                    SharedConfig.toggleSortContactsByName();
                    boolean unused = ContactsActivity.this.sortByName = SharedConfig.sortContactsByName;
                    ContactsAdapter access$100 = ContactsActivity.this.listViewAdapter;
                    if (!ContactsActivity.this.sortByName) {
                        i = 2;
                    }
                    access$100.setSortType(i);
                    ContactsActivity.this.sortItem.setIcon(ContactsActivity.this.sortByName ? R.drawable.contacts_sort_time : R.drawable.contacts_sort_name);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = ContactsActivity.this.searching = true;
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    ContactsActivity.this.floatingButtonContainer.setVisibility(8);
                }
                if (ContactsActivity.this.sortItem != null) {
                    ContactsActivity.this.sortItem.setVisibility(8);
                }
            }

            public void onSearchCollapse() {
                ContactsActivity.this.searchListViewAdapter.searchDialogs((String) null);
                boolean unused = ContactsActivity.this.searching = false;
                boolean unused2 = ContactsActivity.this.searchWas = false;
                ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
                ContactsActivity.this.listView.setSectionsType(1);
                ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
                ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
                ContactsActivity.this.listView.setEmptyView((View) null);
                ContactsActivity.this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    ContactsActivity.this.floatingButtonContainer.setVisibility(0);
                    boolean unused3 = ContactsActivity.this.floatingHidden = true;
                    ContactsActivity.this.floatingButtonContainer.setTranslationY((float) AndroidUtilities.dp(100.0f));
                    ContactsActivity.this.hideFloatingButton(false);
                }
                if (ContactsActivity.this.sortItem != null) {
                    ContactsActivity.this.sortItem.setVisibility(0);
                }
            }

            public void onTextChanged(EditText editText) {
                if (ContactsActivity.this.searchListViewAdapter != null) {
                    String text = editText.getText().toString();
                    if (text.length() != 0) {
                        boolean unused = ContactsActivity.this.searchWas = true;
                        if (ContactsActivity.this.listView != null) {
                            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                            ContactsActivity.this.listView.setSectionsType(0);
                            ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                            ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                        }
                        if (ContactsActivity.this.emptyView != null) {
                            ContactsActivity.this.listView.setEmptyView(ContactsActivity.this.emptyView);
                            ContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                        }
                    }
                    ContactsActivity.this.searchListViewAdapter.searchDialogs(text);
                }
            }
        });
        item.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        item.setContentDescription(LocaleController.getString("Search", R.string.Search));
        if (!this.createSecretChat && !this.returnAsResult) {
            ActionBarMenuItem addItem = menu.addItem(1, this.sortByName ? R.drawable.contacts_sort_time : R.drawable.contacts_sort_name);
            this.sortItem = addItem;
            addItem.setContentDescription(LocaleController.getString("AccDescrContactSorting", R.string.AccDescrContactSorting));
        }
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, true, 0);
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
        ActionBarMenuItem actionBarMenuItem = item;
        ActionBarMenu actionBarMenu = menu;
        Context context3 = context2;
        AnonymousClass3 r1 = new ContactsAdapter(context, this.onlyUsers ? 1 : 0, this.needPhonebook, this.ignoreUsers, (int) inviteViaLink, this.hasGps) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (ContactsActivity.this.listView != null && ContactsActivity.this.listView.getAdapter() == this) {
                    int count = super.getItemCount();
                    int i = 0;
                    if (ContactsActivity.this.needPhonebook) {
                        EmptyTextProgressView access$800 = ContactsActivity.this.emptyView;
                        if (count != 2) {
                            i = 8;
                        }
                        access$800.setVisibility(i);
                        return;
                    }
                    EmptyTextProgressView access$8002 = ContactsActivity.this.emptyView;
                    if (count != 0) {
                        i = 8;
                    }
                    access$8002.setVisibility(i);
                }
            }
        };
        this.listViewAdapter = r1;
        r1.setSortType(this.sortItem != null ? this.sortByName ? 1 : 2 : 0);
        this.listViewAdapter.setDisableSections(this.disableSections);
        this.fragmentView = new FrameLayout(context3) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (ContactsActivity.this.listView.getAdapter() != ContactsActivity.this.listViewAdapter) {
                    ContactsActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (ContactsActivity.this.emptyView.getVisibility() == 0) {
                    ContactsActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context3);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass5 r12 = new RecyclerListView(context3) {
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (ContactsActivity.this.emptyView != null) {
                    ContactsActivity.this.emptyView.setPadding(left, top, right, bottom);
                }
            }
        };
        this.listView = r12;
        r12.setSectionsType(1);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context3, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener((int) inviteViaLink) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                ContactsActivity.this.lambda$createView$1$ContactsActivity(this.f$1, view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    if (ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean goingDown;
                super.onScrolled(recyclerView, dx, dy);
                if (ContactsActivity.this.floatingButtonContainer != null && ContactsActivity.this.floatingButtonContainer.getVisibility() != 8) {
                    int firstVisibleItem = ContactsActivity.this.layoutManager.findFirstVisibleItemPosition();
                    boolean z = false;
                    View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean changed = true;
                    if (ContactsActivity.this.prevPosition == firstVisibleItem) {
                        int topDelta = ContactsActivity.this.prevTop - firstViewTop;
                        goingDown = firstViewTop < ContactsActivity.this.prevTop;
                        if (Math.abs(topDelta) > 1) {
                            z = true;
                        }
                        changed = z;
                    } else {
                        if (firstVisibleItem > ContactsActivity.this.prevPosition) {
                            z = true;
                        }
                        goingDown = z;
                    }
                    if (changed && ContactsActivity.this.scrollUpdated && (goingDown || (!goingDown && this.scrollingManually))) {
                        ContactsActivity.this.hideFloatingButton(goingDown);
                    }
                    int unused = ContactsActivity.this.prevPosition = firstVisibleItem;
                    int unused2 = ContactsActivity.this.prevTop = firstViewTop;
                    boolean unused3 = ContactsActivity.this.scrollUpdated = true;
                }
            }
        });
        if (!this.createSecretChat && !this.returnAsResult) {
            FrameLayout frameLayout2 = new FrameLayout(context3);
            this.floatingButtonContainer = frameLayout2;
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame((float) ((Build.VERSION.SDK_INT >= 21 ? 56 : 60) + 20), (float) ((Build.VERSION.SDK_INT >= 21 ? 56 : 60) + 14), (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 4.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 4.0f, 0.0f));
            this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ContactsActivity.this.lambda$createView$2$ContactsActivity(view);
                }
            });
            ImageView imageView = new ImageView(context3);
            this.floatingButton = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                drawable = combinedDrawable;
            }
            this.floatingButton.setBackgroundDrawable(drawable);
            this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
            this.floatingButton.setImageResource(R.drawable.add_contact_new);
            this.floatingButtonContainer.setContentDescription(LocaleController.getString("CreateNewContact", R.string.CreateNewContact));
            if (Build.VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.floatingButton.setStateListAnimator(animator);
                this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.floatingButtonContainer.addView(this.floatingButton, LayoutHelper.createFrame((float) (Build.VERSION.SDK_INT >= 21 ? 56 : 60), (float) (Build.VERSION.SDK_INT >= 21 ? 56 : 60), 51, 10.0f, 0.0f, 10.0f, 0.0f));
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$ContactsActivity(int inviteViaLink, View view, int position) {
        Activity activity;
        boolean z = false;
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
                            if (sparseArray == null || sparseArray.indexOfKey(user.id) < 0) {
                                didSelectResult(user, true, (String) null);
                            }
                        } else if (this.createSecretChat) {
                            this.creatingChat = true;
                            SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                        } else {
                            Bundle args = new Bundle();
                            args.putInt("user_id", user.id);
                            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                                presentFragment(new ChatActivity(args), true);
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
                                    ContactsActivity.this.lambda$null$0$ContactsActivity(this.f$1, dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                            showDialog(builder.create());
                        }
                    }
                } else if (this.needPhonebook) {
                    if (row == 0) {
                        presentFragment(new InviteContactsActivity());
                    } else if (row == 1 && this.hasGps) {
                        if (Build.VERSION.SDK_INT < 23 || (activity = getParentActivity()) == null || activity.checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) == 0) {
                            boolean enabled = true;
                            if (Build.VERSION.SDK_INT >= 28) {
                                enabled = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
                            } else if (Build.VERSION.SDK_INT >= 19) {
                                try {
                                    if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) != 0) {
                                        z = true;
                                    }
                                    enabled = z;
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            }
                            if (!enabled) {
                                presentFragment(new ActionIntroActivity(4));
                            } else {
                                presentFragment(new PeopleNearbyActivity());
                            }
                        } else {
                            presentFragment(new ActionIntroActivity(1));
                        }
                    }
                } else if (inviteViaLink != 0) {
                    if (row == 0) {
                        int i = this.chatId;
                        if (i == 0) {
                            i = this.channelId;
                        }
                        presentFragment(new GroupInviteActivity(i));
                    }
                } else if (row == 0) {
                    presentFragment(new GroupCreateActivity(new Bundle()), false);
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
                        if (sparseArray2 == null || sparseArray2.indexOfKey(user2.id) < 0) {
                            didSelectResult(user2, true, (String) null);
                        }
                    } else if (!this.createSecretChat) {
                        Bundle args4 = new Bundle();
                        args4.putInt("user_id", user2.id);
                        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args4, this)) {
                            presentFragment(new ChatActivity(args4), true);
                        }
                    } else if (user2.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        this.creatingChat = true;
                        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user2);
                    }
                }
            } else if (object instanceof String) {
                String str = (String) object;
                if (!str.equals("section")) {
                    NewContactActivity activity2 = new NewContactActivity();
                    activity2.setInitialPhoneNumber(str);
                    presentFragment(activity2);
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$0$ContactsActivity(String arg1, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", arg1, (String) null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$createView$2$ContactsActivity(View v) {
        presentFragment(new NewContactActivity());
    }

    private void didSelectResult(TLRPC.User user, boolean useAlert, String param) {
        TLRPC.User user2 = user;
        String str = param;
        if (!useAlert || this.selectAlertString == null) {
            ContactsActivityDelegate contactsActivityDelegate = this.delegate;
            if (contactsActivityDelegate != null) {
                contactsActivityDelegate.didSelectContact(user2, str, this);
                if (this.resetDelegate) {
                    this.delegate = null;
                }
            }
            if (this.needFinishFragment) {
                finishFragment();
            }
        } else if (getParentActivity() != null) {
            if (user2.bot) {
                if (user2.bot_nochats) {
                    ToastUtils.show((int) R.string.BotCantJoinGroups);
                    return;
                } else if (this.channelId != 0) {
                    TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.channelId));
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("AddBotAsAdmin", R.string.AddBotAsAdmin));
                        builder.setPositiveButton(LocaleController.getString("MakeAdmin", R.string.MakeAdmin), new DialogInterface.OnClickListener(user2, str) {
                            private final /* synthetic */ TLRPC.User f$1;
                            private final /* synthetic */ String f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ContactsActivity.this.lambda$didSelectResult$3$ContactsActivity(this.f$1, this.f$2, dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    } else {
                        builder.setMessage(LocaleController.getString("CantAddBotAsAdmin", R.string.CantAddBotAsAdmin));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                    }
                    showDialog(builder.create());
                    return;
                }
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            String message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getName(user));
            EditText editText = null;
            if (!user2.bot && this.needForwardCount) {
                message = String.format("%s\n\n%s", new Object[]{message, LocaleController.getString("AddToTheGroupForwardCount", R.string.AddToTheGroupForwardCount)});
                editText = new EditText(getParentActivity());
                editText.setTextSize(1, 18.0f);
                editText.setText("50");
                editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                editText.setGravity(17);
                editText.setInputType(2);
                editText.setImeOptions(6);
                editText.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
                final EditText editTextFinal = editText;
                editText.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        try {
                            String str = s.toString();
                            if (str.length() != 0) {
                                int value = Utilities.parseInt(str).intValue();
                                if (value < 0) {
                                    editTextFinal.setText("0");
                                    editTextFinal.setSelection(editTextFinal.length());
                                } else if (value > 300) {
                                    editTextFinal.setText("300");
                                    editTextFinal.setSelection(editTextFinal.length());
                                } else {
                                    if (!str.equals("" + value)) {
                                        EditText editText = editTextFinal;
                                        editText.setText("" + value);
                                        editTextFinal.setSelection(editTextFinal.length());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                builder2.setView(editText);
            }
            builder2.setMessage(message);
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(user2, editText) {
                private final /* synthetic */ TLRPC.User f$1;
                private final /* synthetic */ EditText f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ContactsActivity.this.lambda$didSelectResult$4$ContactsActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
            if (editText != null) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
                if (layoutParams != null) {
                    if (layoutParams instanceof FrameLayout.LayoutParams) {
                        ((FrameLayout.LayoutParams) layoutParams).gravity = 1;
                    }
                    int dp = AndroidUtilities.dp(24.0f);
                    layoutParams.leftMargin = dp;
                    layoutParams.rightMargin = dp;
                    layoutParams.height = AndroidUtilities.dp(36.0f);
                    editText.setLayoutParams(layoutParams);
                }
                editText.setSelection(editText.getText().length());
            }
        }
    }

    public /* synthetic */ void lambda$didSelectResult$3$ContactsActivity(TLRPC.User user, String param, DialogInterface dialogInterface, int i) {
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(user, param, this);
            this.delegate = null;
        }
    }

    public /* synthetic */ void lambda$didSelectResult$4$ContactsActivity(TLRPC.User user, EditText finalEditText, DialogInterface dialogInterface, int i) {
        didSelectResult(user, false, finalEditText != null ? finalEditText.getText().toString() : "0");
    }

    public void onResume() {
        Activity activity;
        super.onResume();
        ContactsAdapter contactsAdapter = this.listViewAdapter;
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (activity.checkSelfPermission(com.bjz.comm.net.premission.PermissionUtils.LINKMAIN) == 0) {
                return;
            }
            if (activity.shouldShowRequestPermissionRationale(com.bjz.comm.net.premission.PermissionUtils.LINKMAIN)) {
                AlertDialog create = AlertsCreator.createContactsPermissionDialog(activity, new MessagesStorage.IntCallback() {
                    public final void run(int i) {
                        ContactsActivity.this.lambda$onResume$5$ContactsActivity(i);
                    }
                }).create();
                this.permissionDialog = create;
                showDialog(create);
                return;
            }
            askForPermissons(true);
        }
    }

    public /* synthetic */ void lambda$onResume$5$ContactsActivity(int param) {
        this.askAboutContacts = param != 0;
        if (param != 0) {
            askForPermissons(false);
        }
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    ContactsActivity.this.floatingButtonContainer.setTranslationY((float) (ContactsActivity.this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0));
                    ContactsActivity.this.floatingButtonContainer.setClickable(!ContactsActivity.this.floatingHidden);
                    if (ContactsActivity.this.floatingButtonContainer != null) {
                        ContactsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        AlertDialog alertDialog = this.permissionDialog;
        if (alertDialog != null && dialog == alertDialog && getParentActivity() != null && this.askAboutContacts) {
            askForPermissons(false);
        }
    }

    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity != null && UserConfig.getInstance(this.currentAccount).syncContacts && activity.checkSelfPermission(com.bjz.comm.net.premission.PermissionUtils.LINKMAIN) != 0) {
            if (!alert || !this.askAboutContacts) {
                ArrayList<String> permissons = new ArrayList<>();
                permissons.add(com.bjz.comm.net.premission.PermissionUtils.LINKMAIN);
                activity.requestPermissions((String[]) permissons.toArray(new String[0]), 1);
                return;
            }
            showDialog(AlertsCreator.createContactsPermissionDialog(activity, new MessagesStorage.IntCallback() {
                public final void run(int i) {
                    ContactsActivity.this.lambda$askForPermissons$6$ContactsActivity(i);
                }
            }).create());
        }
    }

    public /* synthetic */ void lambda$askForPermissons$6$ContactsActivity(int param) {
        this.askAboutContacts = param != 0;
        if (param != 0) {
            askForPermissons(false);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length > a && com.bjz.comm.net.premission.PermissionUtils.LINKMAIN.equals(permissions[a])) {
                    if (grantResults[a] == 0) {
                        ContactsController.getInstance(this.currentAccount).forceImportContacts();
                    } else {
                        SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                        this.askAboutContacts = false;
                        edit.putBoolean("askAboutContacts", false).commit();
                    }
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.actionBar != null) {
            this.actionBar.closeSearchField();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ContactsAdapter contactsAdapter;
        if (id == NotificationCenter.contactsDidLoad) {
            ContactsAdapter contactsAdapter2 = this.listViewAdapter;
            if (contactsAdapter2 != null) {
                contactsAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                updateVisibleRows(mask);
            }
            if ((mask & 4) != 0 && !this.sortByName && (contactsAdapter = this.listViewAdapter) != null) {
                contactsAdapter.sortOnlineContacts();
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
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

    /* access modifiers changed from: private */
    public void hideFloatingButton(boolean hide) {
        if (this.floatingHidden != hide) {
            this.floatingHidden = hide;
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            FrameLayout frameLayout = this.floatingButtonContainer;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = (float) (this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0);
            animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(!hide);
            animatorSet.start();
        }
    }

    public void setDelegate(ContactsActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setIgnoreUsers(SparseArray<TLRPC.User> users) {
        this.ignoreUsers = users;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ContactsActivity.this.lambda$getThemeDescriptions$7$ContactsActivity();
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
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(actionBar2, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch), new ThemeDescription(actionBar3, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder), new ThemeDescription(recyclerListView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) recyclerListView3, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollActive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollInactive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, drawableArr, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription((View) recyclerListView5, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) recyclerListView7, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionIcon), new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionBackground), new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionPressedBackground), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_graySectionText), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_nameIcon), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_verifiedCheck), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_verifiedBackground), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText3), new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint, Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_name), new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint, Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_secretName)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$7$ContactsActivity() {
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
