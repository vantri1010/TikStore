package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.adapters.PhoneBookAdapter2;
import im.bclpbkiauv.ui.adapters.PhonebookSearchAdapter;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.io.File;

public class PhoneBookSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int search_button = 0;
    private PhoneBookSelectActivityDelegate delegate;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public PhoneBookAdapter2 listViewAdapter;
    private ChatActivity parentFragment;
    /* access modifiers changed from: private */
    public PhonebookSearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;

    public interface PhoneBookSelectActivityDelegate {
        void didSelectContact(TLRPC.User user, boolean z, int i);
    }

    public PhoneBookSelectActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectContact", R.string.SelectContact));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PhoneBookSelectActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = PhoneBookSelectActivity.this.searching = true;
            }

            public void onSearchCollapse() {
                PhoneBookSelectActivity.this.searchListViewAdapter.search((String) null);
                boolean unused = PhoneBookSelectActivity.this.searching = false;
                boolean unused2 = PhoneBookSelectActivity.this.searchWas = false;
                PhoneBookSelectActivity.this.listView.setAdapter(PhoneBookSelectActivity.this.listViewAdapter);
                PhoneBookSelectActivity.this.listView.setSectionsType(1);
                PhoneBookSelectActivity.this.listViewAdapter.notifyDataSetChanged();
                PhoneBookSelectActivity.this.listView.setFastScrollVisible(true);
                PhoneBookSelectActivity.this.listView.setVerticalScrollBarEnabled(false);
                PhoneBookSelectActivity.this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
            }

            public void onTextChanged(EditText editText) {
                if (PhoneBookSelectActivity.this.searchListViewAdapter != null) {
                    String text = editText.getText() + "";
                    if (!TextUtils.isEmpty(text)) {
                        boolean unused = PhoneBookSelectActivity.this.searchWas = true;
                        PhoneBookSelectActivity.this.searchListViewAdapter.search(text);
                        return;
                    }
                    boolean unused2 = PhoneBookSelectActivity.this.searchWas = false;
                    if (PhoneBookSelectActivity.this.listView != null && PhoneBookSelectActivity.this.listViewAdapter != null) {
                        PhoneBookSelectActivity.this.listView.setAdapter(PhoneBookSelectActivity.this.listViewAdapter);
                    }
                }
            }
        }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.searchListViewAdapter = new PhonebookSearchAdapter(context) {
            /* access modifiers changed from: protected */
            public void onUpdateSearchResults(String query) {
                if (!TextUtils.isEmpty(query) && PhoneBookSelectActivity.this.listView != null && PhoneBookSelectActivity.this.listView.getAdapter() != PhoneBookSelectActivity.this.searchListViewAdapter) {
                    PhoneBookSelectActivity.this.listView.setAdapter(PhoneBookSelectActivity.this.searchListViewAdapter);
                    PhoneBookSelectActivity.this.listView.setSectionsType(0);
                    PhoneBookSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
                    PhoneBookSelectActivity.this.listView.setFastScrollVisible(false);
                    PhoneBookSelectActivity.this.listView.setVerticalScrollBarEnabled(true);
                    PhoneBookSelectActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                }
            }
        };
        this.listViewAdapter = new PhoneBookAdapter2(context) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (PhoneBookSelectActivity.this.listView.getAdapter() == this) {
                    PhoneBookSelectActivity.this.listView.setFastScrollVisible(super.getItemCount() != 0);
                }
            }
        };
        this.fragmentView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (PhoneBookSelectActivity.this.listView.getAdapter() != PhoneBookSelectActivity.this.listViewAdapter) {
                    PhoneBookSelectActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (PhoneBookSelectActivity.this.emptyView.getVisibility() == 0) {
                    PhoneBookSelectActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setSectionsType(1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled();
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PhoneBookSelectActivity.this.lambda$createView$1$PhoneBookSelectActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && PhoneBookSelectActivity.this.searching && PhoneBookSelectActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(PhoneBookSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PhoneBookSelectActivity(View view, int position) {
        Object object;
        String name;
        ContactsController.Contact contact;
        TLRPC.User user;
        if (!this.searching || !this.searchWas) {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                object = this.listViewAdapter.getItem(section, row);
            } else {
                return;
            }
        } else {
            object = this.searchListViewAdapter.getItem(position);
        }
        if (object != null) {
            if (object instanceof ContactsController.Contact) {
                contact = object;
                if (contact.user != null) {
                    name = ContactsController.formatName(contact.user.first_name, contact.user.last_name);
                } else {
                    name = "";
                }
            } else {
                if (object instanceof TLRPC.Contact) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(object.user_id));
                } else {
                    user = object;
                }
                if (user == null) {
                    FileLog.e("ListView onItemClick user is null");
                    return;
                }
                ContactsController.Contact contact2 = new ContactsController.Contact();
                contact2.first_name = user.first_name;
                contact2.last_name = user.last_name;
                if (!TextUtils.isEmpty(user.phone)) {
                    contact2.phones.add(user.phone);
                } else {
                    contact2.phones.add("");
                }
                contact2.user = user;
                contact = contact2;
                name = ContactsController.formatName(contact2.first_name, contact2.last_name);
            }
            PhonebookShareActivity activity = new PhonebookShareActivity(contact, (Uri) null, (File) null, name);
            activity.setChatActivity(this.parentFragment);
            activity.setDelegate(new PhoneBookSelectActivityDelegate() {
                public final void didSelectContact(TLRPC.User user, boolean z, int i) {
                    PhoneBookSelectActivity.this.lambda$null$0$PhoneBookSelectActivity(user, z, i);
                }
            });
            presentFragment(activity);
        }
    }

    public /* synthetic */ void lambda$null$0$PhoneBookSelectActivity(TLRPC.User user, boolean notify, int scheduleDate) {
        removeSelfFromStack();
        this.delegate.didSelectContact(user, notify, scheduleDate);
    }

    public void onResume() {
        super.onResume();
        PhoneBookAdapter2 phoneBookAdapter2 = this.listViewAdapter;
        if (phoneBookAdapter2 != null) {
            phoneBookAdapter2.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.actionBar != null) {
            this.actionBar.closeSearchField();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoad) {
            PhoneBookAdapter2 phoneBookAdapter2 = this.listViewAdapter;
            if (phoneBookAdapter2 != null) {
                phoneBookAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public void setDelegate(PhoneBookSelectActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                PhoneBookSelectActivity.this.lambda$getThemeDescriptions$2$PhoneBookSelectActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollActive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollInactive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$2$PhoneBookSelectActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}
