package im.bclpbkiauv.ui.hui.packet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.SearchAdapterHelper;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.cells.ManageChatUserCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.decoration.StickyDecoration;
import im.bclpbkiauv.ui.decoration.listener.GroupListener;
import im.bclpbkiauv.ui.hui.CharacterParser;
import im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity;
import im.bclpbkiauv.ui.hviews.MryEmptyTextProgressView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class SelecteContactsActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int chatId;
    /* access modifiers changed from: private */
    public StickyDecoration decoration;
    private ContactsActivityDelegate delegate;
    /* access modifiers changed from: private */
    public MryEmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean isCharClicked;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    private boolean loadingUsers = false;
    private Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.ChatParticipant> participants = new ArrayList<>();
    private FrameLayout searchLayout;
    /* access modifiers changed from: private */
    public SearchAdapter searchListViewAdapter;
    private EditText searchView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private final HashMap<String, ArrayList<TLRPC.User>> sectionsDict = new HashMap<>();
    /* access modifiers changed from: private */
    public SideBar sideBar;
    private final ArrayList<String> sortedSectionsArray = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean usersEndReached = false;

    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC.User user);
    }

    public void setDelegate(ContactsActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        if (chatFull != null) {
            this.chatId = chatFull.id;
        }
    }

    public void setParticipants(ArrayList<TLRPC.ChatParticipant> participants2) {
        this.participants = participants2;
    }

    /* access modifiers changed from: private */
    public void getChannelParticipants() {
        if (!this.loadingUsers) {
            this.loadingUsers = true;
            TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
            req.filter = new TLRPC.TL_channelParticipantsRecent();
            req.offset = this.participants.size();
            req.limit = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(req) {
                private final /* synthetic */ TLRPC.TL_channels_getParticipants f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SelecteContactsActivity.this.lambda$getChannelParticipants$1$SelecteContactsActivity(this.f$1, tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getChannelParticipants$1$SelecteContactsActivity(TLRPC.TL_channels_getParticipants req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, req) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_channels_getParticipants f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SelecteContactsActivity.this.lambda$null$0$SelecteContactsActivity(this.f$1, this.f$2, this.f$3);
            }
        }, req.offset == 0 ? 300 : 0);
    }

    public /* synthetic */ void lambda$null$0$SelecteContactsActivity(TLRPC.TL_error error, TLObject response, TLRPC.TL_channels_getParticipants req) {
        if (error == null) {
            if (response instanceof TLRPC.TL_channels_channelParticipants) {
                TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
                MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
                if (res.users.size() < 200) {
                    this.usersEndReached = true;
                }
                if (req.offset == 0) {
                    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, (ArrayList<TLRPC.Chat>) null, true, true);
                    MessagesStorage.getInstance(this.currentAccount).updateChannelUsers(this.chatId, res.participants);
                }
                ArrayList<TLRPC.ChatParticipant> temp = new ArrayList<>();
                for (int a = 0; a < res.participants.size(); a++) {
                    TLRPC.TL_chatChannelParticipant participant = new TLRPC.TL_chatChannelParticipant();
                    participant.channelParticipant = (TLRPC.ChannelParticipant) res.participants.get(a);
                    participant.inviter_id = participant.channelParticipant.inviter_id;
                    participant.user_id = participant.channelParticipant.user_id;
                    participant.date = participant.channelParticipant.date;
                    temp.add(participant);
                }
                grouping(temp);
                this.participants.addAll(temp);
            } else {
                return;
            }
        }
        this.loadingUsers = false;
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void grouping(ArrayList<TLRPC.ChatParticipant> participants2) {
        String key;
        Iterator<TLRPC.ChatParticipant> it = participants2.iterator();
        while (it.hasNext()) {
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(it.next().user_id));
            if (!(user == null || user.id == getUserConfig().clientUserId)) {
                String key2 = CharacterParser.getInstance().getSelling(UserObject.getName(user));
                if (key2.length() > 1) {
                    key2 = key2.substring(0, 1);
                }
                if (key2.length() == 0) {
                    key = "#";
                } else if ((key2.charAt(0) <= 'a' || key2.charAt(0) >= 'z') && (key2.charAt(0) <= 'A' || key2.charAt(0) >= 'Z')) {
                    key = "#";
                } else {
                    key = key2.toUpperCase();
                }
                ArrayList<TLRPC.User> arr = this.sectionsDict.get(key);
                if (arr == null) {
                    arr = new ArrayList<>();
                    this.sectionsDict.put(key, arr);
                    this.sortedSectionsArray.add(key);
                }
                arr.add(user);
            }
        }
        Collections.sort(this.sortedSectionsArray, $$Lambda$SelecteContactsActivity$pwlrMqmwi_qqnNTHY92PVMZci0k.INSTANCE);
    }

    static /* synthetic */ int lambda$grouping$2(String o1, String o2) {
        if ("#".equals(o1)) {
            return 1;
        }
        if ("#".equals(o2) || o1.charAt(0) < o2.charAt(0)) {
            return -1;
        }
        if (o1.charAt(0) > o2.charAt(0)) {
            return 1;
        }
        return 0;
    }

    public SelecteContactsActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getChannelParticipants();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.delegate = null;
    }

    public View createView(Context context) {
        super.createView(context);
        this.mContext = context;
        this.searching = false;
        this.searchWas = false;
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        initActionBar();
        initSearchLayot(frameLayout);
        initList(frameLayout, context);
        initSideBar(frameLayout, context);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString(R.string.redpacket_choose_person));
        this.actionBar.setBackTitle(LocaleController.getString("Cancel", R.string.Cancel));
        this.actionBar.getBackTitleTextView().setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SelecteContactsActivity.this.lambda$initActionBar$3$SelecteContactsActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initActionBar$3$SelecteContactsActivity(View v) {
        finishFragment();
    }

    private void initSearchLayot(FrameLayout frameLayout) {
        FrameLayout searchLayout2 = new FrameLayout(this.mContext);
        searchLayout2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        frameLayout.addView(searchLayout2, LayoutHelper.createFrame(-1, 48.0f));
        ImageView iconSearch = new ImageView(this.mContext);
        iconSearch.setImageResource(R.mipmap.ic_index_search);
        searchLayout2.addView(iconSearch, LayoutHelper.createFrame(15, 15, (int) GravityCompat.START));
        EditText editText = new EditText(this.mContext);
        this.searchView = editText;
        editText.setHint(LocaleController.getString(R.string.new_call_search_hint));
        this.searchView.setBackground((Drawable) null);
        this.searchView.setPadding(0, 0, AndroidUtilities.dp(28.0f), 0);
        searchLayout2.addView(this.searchView, LayoutHelper.createFrame(-1.0f, -2.0f, 16, 21.0f, 0.0f, 0.0f, 0.0f));
        final ImageView deleteIamge = new ImageView(this.mContext);
        deleteIamge.setVisibility(8);
        deleteIamge.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        searchLayout2.addView(deleteIamge, LayoutHelper.createFrame(24, -1, 5));
        deleteIamge.setImageResource(R.mipmap.ic_clear_round);
        this.searchView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                boolean unused = SelecteContactsActivity.this.searching = true;
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                deleteIamge.setVisibility(s.length() > 0 ? 0 : 8);
                if (SelecteContactsActivity.this.searchListViewAdapter != null) {
                    if (s.length() == 0) {
                        SelecteContactsActivity.this.searchListViewAdapter.searchDialogs((String) null);
                        boolean unused = SelecteContactsActivity.this.searching = false;
                        boolean unused2 = SelecteContactsActivity.this.searchWas = false;
                        SelecteContactsActivity.this.listView.addItemDecoration(SelecteContactsActivity.this.decoration);
                        SelecteContactsActivity.this.listView.setAdapter(SelecteContactsActivity.this.listViewAdapter);
                        SelecteContactsActivity.this.listViewAdapter.notifyDataSetChanged();
                        SelecteContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
                        SelecteContactsActivity.this.listView.setEmptyView((View) null);
                        SelecteContactsActivity.this.emptyView.setTopImage(R.mipmap.img_empty_default);
                        SelecteContactsActivity.this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
                        return;
                    }
                    boolean unused3 = SelecteContactsActivity.this.searchWas = true;
                    if (SelecteContactsActivity.this.listView != null) {
                        SelecteContactsActivity.this.listView.removeItemDecoration(SelecteContactsActivity.this.decoration);
                        SelecteContactsActivity.this.listView.setAdapter(SelecteContactsActivity.this.searchListViewAdapter);
                        SelecteContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        SelecteContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
                    }
                    if (SelecteContactsActivity.this.emptyView != null) {
                        SelecteContactsActivity.this.listView.setEmptyView(SelecteContactsActivity.this.emptyView);
                        SelecteContactsActivity.this.emptyView.setTopImage(R.mipmap.img_empty_default);
                        SelecteContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                    }
                    SelecteContactsActivity.this.searchListViewAdapter.searchDialogs(s.toString());
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        deleteIamge.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SelecteContactsActivity.this.lambda$initSearchLayot$4$SelecteContactsActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initSearchLayot$4$SelecteContactsActivity(View v) {
        this.searchView.setText("");
    }

    private void initList(FrameLayout frameLayout, Context context) {
        grouping(this.participants);
        MryEmptyTextProgressView mryEmptyTextProgressView = new MryEmptyTextProgressView(context);
        this.emptyView = mryEmptyTextProgressView;
        mryEmptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setTopImage(R.mipmap.img_empty_default);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrameSearchWithoutActionBar(-1, -1));
        this.searchListViewAdapter = new SearchAdapter(context);
        this.listViewAdapter = new ListAdapter(context, this.sectionsDict, this.sortedSectionsArray) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (SelecteContactsActivity.this.listView != null && SelecteContactsActivity.this.listView.getAdapter() == this) {
                    SelecteContactsActivity.this.emptyView.setVisibility(super.getItemCount() == 0 ? 0 : 8);
                }
            }
        };
        AnonymousClass3 r0 = new RecyclerListView(context) {
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (SelecteContactsActivity.this.emptyView != null) {
                    SelecteContactsActivity.this.emptyView.setPadding(left, top, right, bottom);
                }
            }
        };
        this.listView = r0;
        r0.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean unused = SelecteContactsActivity.this.isCharClicked = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!SelecteContactsActivity.this.searching && !SelecteContactsActivity.this.searchWas && !SelecteContactsActivity.this.usersEndReached && SelecteContactsActivity.this.layoutManager.findLastVisibleItemPosition() > SelecteContactsActivity.this.participants.size() - 8) {
                    SelecteContactsActivity.this.getChannelParticipants();
                }
                if (!SelecteContactsActivity.this.isCharClicked) {
                    SelecteContactsActivity.this.sideBar.setChooseChar(SelecteContactsActivity.this.listViewAdapter.getLetter(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
                }
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        this.listView.setLayoutManager(linearLayoutManager);
        StickyDecoration build = StickyDecoration.Builder.init(new GroupListener() {
            public final String getGroupName(int i) {
                return SelecteContactsActivity.this.lambda$initList$5$SelecteContactsActivity(i);
            }
        }).setGroupBackground(Theme.getColor(Theme.key_windowBackgroundGray)).setGroupTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText)).setGroupTextTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf")).setGroupHeight(AndroidUtilities.dp(24.0f)).setDivideColor(Color.parseColor("#EE96BC")).setGroupTextSize(AndroidUtilities.dp(14.0f)).setTextSideMargin(AndroidUtilities.dp(15.0f)).build();
        this.decoration = build;
        this.listView.addItemDecoration(build);
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 0, AndroidUtilities.dp(48.0f), 0, 0));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                SelecteContactsActivity.this.lambda$initList$6$SelecteContactsActivity(view, i);
            }
        });
    }

    public /* synthetic */ String lambda$initList$5$SelecteContactsActivity(int position) {
        if (this.listViewAdapter.getItemCount() <= position || position <= -1) {
            return null;
        }
        return this.listViewAdapter.getLetter(position);
    }

    public /* synthetic */ void lambda$initList$6$SelecteContactsActivity(View view, int position) {
        ContactsActivityDelegate contactsActivityDelegate;
        ContactsActivityDelegate contactsActivityDelegate2;
        if (!this.searching || !this.searchWas) {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                Object item1 = this.listViewAdapter.getItem(section, row);
                if (item1 instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) item1;
                    ContactsActivityDelegate contactsActivityDelegate3 = this.delegate;
                    if (contactsActivityDelegate3 != null) {
                        contactsActivityDelegate3.didSelectContact(user);
                    }
                    finishFragment();
                    return;
                }
                return;
            }
            return;
        }
        TLObject item = this.searchListViewAdapter.getItem(position);
        if (item instanceof TLRPC.User) {
            TLRPC.User user2 = (TLRPC.User) item;
            ContactsActivityDelegate contactsActivityDelegate4 = this.delegate;
            if (contactsActivityDelegate4 != null) {
                contactsActivityDelegate4.didSelectContact(user2);
            }
            finishFragment();
        } else if (item instanceof TLRPC.TL_chatChannelParticipant) {
            TLRPC.User user3 = getMessagesController().getUser(Integer.valueOf(((TLRPC.TL_chatChannelParticipant) item).user_id));
            if (!(user3 == null || (contactsActivityDelegate2 = this.delegate) == null)) {
                contactsActivityDelegate2.didSelectContact(user3);
            }
            finishFragment();
        } else if (item instanceof TLRPC.TL_channelParticipant) {
            TLRPC.User user4 = getMessagesController().getUser(Integer.valueOf(((TLRPC.TL_channelParticipant) item).user_id));
            if (!(user4 == null || (contactsActivityDelegate = this.delegate) == null)) {
                contactsActivityDelegate.didSelectContact(user4);
            }
            finishFragment();
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
        frameLayout.addView(this.sideBar, LayoutHelper.createFrame(35.0f, 420.0f, 21, 0.0f, 56.0f, 0.0f, 56.0f));
        this.sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public final void onTouchingLetterChanged(String str) {
                SelecteContactsActivity.this.lambda$initSideBar$7$SelecteContactsActivity(str);
            }
        });
    }

    public /* synthetic */ void lambda$initSideBar$7$SelecteContactsActivity(String s) {
        int position = this.listViewAdapter.getPositionForSection(this.listViewAdapter.getSectionForChar(s.charAt(0)));
        if (position != -1 && this.listView.getLayoutManager() != null) {
            this.listView.getLayoutManager().scrollToPosition(position);
            this.isCharClicked = true;
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
    }

    public boolean onBackPressed() {
        EditText editText = this.searchView;
        if (editText == null || TextUtils.isEmpty(editText.getText().toString().trim())) {
            return super.onBackPressed();
        }
        this.searchView.setText("");
        return false;
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int contactsStartRow;
        private int globalStartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<TLObject> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        private int totalCount;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate(SelecteContactsActivity.this) {
                public /* synthetic */ SparseArray<TLRPC.User> getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public void onDataSetChanged() {
                    SearchAdapter.this.notifyDataSetChanged();
                }

                public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                }
            });
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, false, false, true, false, SelecteContactsActivity.this.chatId, false, 2);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$SelecteContactsActivity$SearchAdapter$uia9C3WjYL8OwUgxL76ttexdnA r1 = new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SelecteContactsActivity.SearchAdapter.this.lambda$searchDialogs$0$SelecteContactsActivity$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchDialogs$0$SelecteContactsActivity$SearchAdapter(String query) {
            AndroidUtilities.runOnUIThread(new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SelecteContactsActivity.SearchAdapter.this.lambda$processSearch$2$SelecteContactsActivity$SearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$2$SelecteContactsActivity$SearchAdapter(String query) {
            this.searchRunnable = null;
            ArrayList<TLRPC.ChatParticipant> participantsCopy = new ArrayList<>(SelecteContactsActivity.this.participants);
            this.searchAdapterHelper.queryServerSearch(query, false, false, true, false, SelecteContactsActivity.this.chatId, false, 2);
            Utilities.searchQueue.postRunnable(new Runnable(query, participantsCopy, (ArrayList) null) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SelecteContactsActivity.SearchAdapter.this.lambda$null$1$SelecteContactsActivity$SearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x011d A[LOOP:1: B:28:0x00af->B:49:0x011d, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:93:0x00de A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$1$SelecteContactsActivity$SearchAdapter(java.lang.String r21, java.util.ArrayList r22, java.util.ArrayList r23) {
            /*
                r20 = this;
                r0 = r20
                r1 = r22
                r2 = r23
                java.lang.String r3 = r21.trim()
                java.lang.String r3 = r3.toLowerCase()
                int r4 = r3.length()
                if (r4 != 0) goto L_0x0027
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                java.util.ArrayList r5 = new java.util.ArrayList
                r5.<init>()
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                r0.updateSearchResults(r4, r5, r6)
                return
            L_0x0027:
                im.bclpbkiauv.messenger.LocaleController r4 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                java.lang.String r4 = r4.getTranslitString(r3)
                boolean r5 = r3.equals(r4)
                if (r5 != 0) goto L_0x003b
                int r5 = r4.length()
                if (r5 != 0) goto L_0x003c
            L_0x003b:
                r4 = 0
            L_0x003c:
                r5 = 0
                r6 = 1
                if (r4 == 0) goto L_0x0042
                r7 = 1
                goto L_0x0043
            L_0x0042:
                r7 = 0
            L_0x0043:
                int r7 = r7 + r6
                java.lang.String[] r7 = new java.lang.String[r7]
                r7[r5] = r3
                if (r4 == 0) goto L_0x004c
                r7[r6] = r4
            L_0x004c:
                java.util.ArrayList r8 = new java.util.ArrayList
                r8.<init>()
                java.util.ArrayList r9 = new java.util.ArrayList
                r9.<init>()
                java.util.ArrayList r10 = new java.util.ArrayList
                r10.<init>()
                java.lang.String r12 = "@"
                if (r1 == 0) goto L_0x013d
                r13 = 0
            L_0x0060:
                int r14 = r22.size()
                if (r13 >= r14) goto L_0x0138
                java.lang.Object r14 = r1.get(r13)
                im.bclpbkiauv.tgnet.TLRPC$ChatParticipant r14 = (im.bclpbkiauv.tgnet.TLRPC.ChatParticipant) r14
                im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity r15 = im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity.this
                im.bclpbkiauv.messenger.MessagesController r15 = r15.getMessagesController()
                int r5 = r14.user_id
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                im.bclpbkiauv.tgnet.TLRPC$User r5 = r15.getUser(r5)
                int r15 = r5.id
                im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity r11 = im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity.this
                im.bclpbkiauv.messenger.UserConfig r11 = r11.getUserConfig()
                int r11 = r11.getClientUserId()
                if (r15 != r11) goto L_0x0090
                r17 = r3
                r18 = r4
                goto L_0x012c
            L_0x0090:
                java.lang.String r11 = r5.first_name
                java.lang.String r15 = r5.last_name
                java.lang.String r11 = im.bclpbkiauv.messenger.ContactsController.formatName(r11, r15)
                java.lang.String r11 = r11.toLowerCase()
                im.bclpbkiauv.messenger.LocaleController r15 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                java.lang.String r15 = r15.getTranslitString(r11)
                boolean r16 = r11.equals(r15)
                if (r16 == 0) goto L_0x00ab
                r15 = 0
            L_0x00ab:
                r16 = 0
                int r6 = r7.length
                r1 = 0
            L_0x00af:
                if (r1 >= r6) goto L_0x0128
                r17 = r3
                r3 = r7[r1]
                boolean r18 = r11.contains(r3)
                if (r18 != 0) goto L_0x00d9
                if (r15 == 0) goto L_0x00c6
                boolean r18 = r15.contains(r3)
                if (r18 == 0) goto L_0x00c6
                r18 = r4
                goto L_0x00db
            L_0x00c6:
                r18 = r4
                java.lang.String r4 = r5.username
                if (r4 == 0) goto L_0x00d6
                java.lang.String r4 = r5.username
                boolean r4 = r4.contains(r3)
                if (r4 == 0) goto L_0x00d6
                r4 = 2
                goto L_0x00dc
            L_0x00d6:
                r4 = r16
                goto L_0x00dc
            L_0x00d9:
                r18 = r4
            L_0x00db:
                r4 = 1
            L_0x00dc:
                if (r4 == 0) goto L_0x011d
                r1 = 1
                if (r4 != r1) goto L_0x00ef
                java.lang.String r1 = r5.first_name
                java.lang.String r6 = r5.last_name
                java.lang.CharSequence r1 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r1, r6, r3)
                r9.add(r1)
                r19 = r3
                goto L_0x0119
            L_0x00ef:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r12)
                java.lang.String r6 = r5.username
                r1.append(r6)
                java.lang.String r1 = r1.toString()
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r12)
                r6.append(r3)
                java.lang.String r6 = r6.toString()
                r19 = r3
                r3 = 0
                java.lang.CharSequence r1 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r1, r3, r6)
                r9.add(r1)
            L_0x0119:
                r10.add(r5)
                goto L_0x012c
            L_0x011d:
                r19 = r3
                int r1 = r1 + 1
                r16 = r4
                r3 = r17
                r4 = r18
                goto L_0x00af
            L_0x0128:
                r17 = r3
                r18 = r4
            L_0x012c:
                int r13 = r13 + 1
                r1 = r22
                r3 = r17
                r4 = r18
                r5 = 0
                r6 = 1
                goto L_0x0060
            L_0x0138:
                r17 = r3
                r18 = r4
                goto L_0x0141
            L_0x013d:
                r17 = r3
                r18 = r4
            L_0x0141:
                if (r2 == 0) goto L_0x01fe
                r1 = 0
            L_0x0144:
                int r3 = r23.size()
                if (r1 >= r3) goto L_0x01fe
                java.lang.Object r3 = r2.get(r1)
                im.bclpbkiauv.tgnet.TLRPC$Contact r3 = (im.bclpbkiauv.tgnet.TLRPC.Contact) r3
                im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity r4 = im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity.this
                im.bclpbkiauv.messenger.MessagesController r4 = r4.getMessagesController()
                int r5 = r3.user_id
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r5)
                int r5 = r4.id
                im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity r6 = im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity.this
                im.bclpbkiauv.messenger.UserConfig r6 = r6.getUserConfig()
                int r6 = r6.getClientUserId()
                if (r5 != r6) goto L_0x0171
                r2 = 0
                goto L_0x01f8
            L_0x0171:
                java.lang.String r5 = r4.first_name
                java.lang.String r6 = r4.last_name
                java.lang.String r5 = im.bclpbkiauv.messenger.ContactsController.formatName(r5, r6)
                java.lang.String r5 = r5.toLowerCase()
                im.bclpbkiauv.messenger.LocaleController r6 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                java.lang.String r6 = r6.getTranslitString(r5)
                boolean r11 = r5.equals(r6)
                if (r11 == 0) goto L_0x018c
                r6 = 0
            L_0x018c:
                r11 = 0
                int r13 = r7.length
                r14 = 0
            L_0x018f:
                if (r14 >= r13) goto L_0x01f7
                r15 = r7[r14]
                boolean r16 = r5.contains(r15)
                if (r16 != 0) goto L_0x01b1
                if (r6 == 0) goto L_0x01a2
                boolean r16 = r6.contains(r15)
                if (r16 == 0) goto L_0x01a2
                goto L_0x01b1
            L_0x01a2:
                java.lang.String r2 = r4.username
                if (r2 == 0) goto L_0x01b3
                java.lang.String r2 = r4.username
                boolean r2 = r2.contains(r15)
                if (r2 == 0) goto L_0x01b3
                r2 = 2
                r11 = r2
                goto L_0x01b3
            L_0x01b1:
                r2 = 1
                r11 = r2
            L_0x01b3:
                if (r11 == 0) goto L_0x01f1
                r2 = 1
                if (r11 != r2) goto L_0x01c5
                java.lang.String r13 = r4.first_name
                java.lang.String r14 = r4.last_name
                java.lang.CharSequence r13 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r13, r14, r15)
                r9.add(r13)
                r2 = 0
                goto L_0x01ed
            L_0x01c5:
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                r13.append(r12)
                java.lang.String r14 = r4.username
                r13.append(r14)
                java.lang.String r13 = r13.toString()
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                r14.append(r12)
                r14.append(r15)
                java.lang.String r14 = r14.toString()
                r2 = 0
                java.lang.CharSequence r13 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r13, r2, r14)
                r9.add(r13)
            L_0x01ed:
                r8.add(r4)
                goto L_0x01f8
            L_0x01f1:
                r2 = 0
                int r14 = r14 + 1
                r2 = r23
                goto L_0x018f
            L_0x01f7:
                r2 = 0
            L_0x01f8:
                int r1 = r1 + 1
                r2 = r23
                goto L_0x0144
            L_0x01fe:
                r0.updateSearchResults(r8, r9, r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity.SearchAdapter.lambda$null$1$SelecteContactsActivity$SearchAdapter(java.lang.String, java.util.ArrayList, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> users, ArrayList<CharSequence> names, ArrayList<TLObject> participants) {
            AndroidUtilities.runOnUIThread(new Runnable(users, names, participants) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SelecteContactsActivity.SearchAdapter.this.lambda$updateSearchResults$3$SelecteContactsActivity$SearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$3$SelecteContactsActivity$SearchAdapter(ArrayList users, ArrayList names, ArrayList participants) {
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            ArrayList<TLObject> search = this.searchAdapterHelper.getGroupSearch();
            search.clear();
            search.addAll(participants);
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public int getItemCount() {
            int contactsCount = this.searchResult.size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int groupsCount = this.searchAdapterHelper.getGroupSearch().size();
            int count = 0;
            if (contactsCount != 0) {
                count = 0 + contactsCount + 1;
            }
            if (globalCount != 0) {
                count += globalCount + 1;
            }
            if (groupsCount != 0) {
                return count + groupsCount + 1;
            }
            return count;
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                this.groupStartRow = 0;
                this.totalCount += count + 1;
            } else {
                this.groupStartRow = -1;
            }
            int count2 = this.searchResult.size();
            if (count2 != 0) {
                int i = this.totalCount;
                this.contactsStartRow = i;
                this.totalCount = i + count2 + 1;
            } else {
                this.contactsStartRow = -1;
            }
            int count3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count3 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + count3 + 1;
            } else {
                this.globalStartRow = -1;
            }
            super.notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                if (count + 1 <= i) {
                    i -= count + 1;
                } else if (i == 0) {
                    return null;
                } else {
                    return this.searchAdapterHelper.getGroupSearch().get(i - 1);
                }
            }
            int count2 = this.searchResult.size();
            if (count2 != 0) {
                if (count2 + 1 <= i) {
                    i -= count2 + 1;
                } else if (i == 0) {
                    return null;
                } else {
                    return this.searchResult.get(i - 1);
                }
            }
            int count3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count3 == 0 || count3 + 1 <= i || i == 0) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(i - 1);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new GraySectionCell(this.mContext);
            } else {
                view = new ManageChatUserCell(this.mContext, 2, 2, true);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((ManageChatUserCell) view).setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                        return SelecteContactsActivity.SearchAdapter.this.lambda$onCreateViewHolder$4$SelecteContactsActivity$SearchAdapter(manageChatUserCell, z);
                    }
                });
            }
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$4$SelecteContactsActivity$SearchAdapter(ManageChatUserCell cell, boolean click) {
            if (!(getItem(((Integer) cell.getTag()).intValue()) instanceof TLRPC.ChannelParticipant)) {
                return false;
            }
            TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) getItem(((Integer) cell.getTag()).intValue());
            return false;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x0196  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            /*
                r20 = this;
                r1 = r20
                r2 = r21
                r0 = r22
                int r3 = r21.getItemViewType()
                r4 = 1
                if (r3 == 0) goto L_0x004b
                if (r3 == r4) goto L_0x0011
                goto L_0x01cd
            L_0x0011:
                android.view.View r3 = r2.itemView
                im.bclpbkiauv.ui.cells.GraySectionCell r3 = (im.bclpbkiauv.ui.cells.GraySectionCell) r3
                int r4 = r1.groupStartRow
                if (r0 != r4) goto L_0x0027
                r4 = 2131690438(0x7f0f03c6, float:1.900992E38)
                java.lang.String r5 = "ChannelMembers"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x01cd
            L_0x0027:
                int r4 = r1.globalStartRow
                if (r0 != r4) goto L_0x0039
                r4 = 2131691482(0x7f0f07da, float:1.9012037E38)
                java.lang.String r5 = "GlobalSearch"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x01cd
            L_0x0039:
                int r4 = r1.contactsStartRow
                if (r0 != r4) goto L_0x01cd
                r4 = 2131690709(0x7f0f04d5, float:1.901047E38)
                java.lang.String r5 = "Contacts"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x01cd
            L_0x004b:
                im.bclpbkiauv.tgnet.TLObject r3 = r1.getItem(r0)
                boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.User
                if (r5 == 0) goto L_0x0057
                r5 = r3
                im.bclpbkiauv.tgnet.TLRPC$User r5 = (im.bclpbkiauv.tgnet.TLRPC.User) r5
                goto L_0x0086
            L_0x0057:
                boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.ChannelParticipant
                if (r5 == 0) goto L_0x006f
                im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity r5 = im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity.this
                im.bclpbkiauv.messenger.MessagesController r5 = r5.getMessagesController()
                r6 = r3
                im.bclpbkiauv.tgnet.TLRPC$ChannelParticipant r6 = (im.bclpbkiauv.tgnet.TLRPC.ChannelParticipant) r6
                int r6 = r6.user_id
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r6)
                goto L_0x0086
            L_0x006f:
                boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.ChatParticipant
                if (r5 == 0) goto L_0x01ce
                im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity r5 = im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity.this
                im.bclpbkiauv.messenger.MessagesController r5 = r5.getMessagesController()
                r6 = r3
                im.bclpbkiauv.tgnet.TLRPC$ChatParticipant r6 = (im.bclpbkiauv.tgnet.TLRPC.ChatParticipant) r6
                int r6 = r6.user_id
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r6)
            L_0x0086:
                java.lang.String r6 = r5.username
                r7 = 0
                r8 = 0
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r9 = r1.searchAdapterHelper
                java.util.ArrayList r9 = r9.getGroupSearch()
                int r9 = r9.size()
                r10 = 0
                r11 = 0
                if (r9 == 0) goto L_0x00a7
                int r12 = r9 + 1
                if (r12 <= r0) goto L_0x00a4
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r12 = r1.searchAdapterHelper
                java.lang.String r11 = r12.getLastFoundChannel()
                r10 = 1
                goto L_0x00a7
            L_0x00a4:
                int r12 = r9 + 1
                int r0 = r0 - r12
            L_0x00a7:
                java.lang.String r12 = "@"
                if (r10 != 0) goto L_0x0109
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r13 = r1.searchResult
                int r9 = r13.size()
                if (r9 == 0) goto L_0x0101
                int r13 = r9 + 1
                if (r13 <= r0) goto L_0x00f6
                r10 = 1
                java.util.ArrayList<java.lang.CharSequence> r13 = r1.searchResultNames
                int r14 = r0 + -1
                java.lang.Object r13 = r13.get(r14)
                r8 = r13
                java.lang.CharSequence r8 = (java.lang.CharSequence) r8
                if (r8 == 0) goto L_0x00ee
                boolean r13 = android.text.TextUtils.isEmpty(r6)
                if (r13 != 0) goto L_0x00ee
                java.lang.String r13 = r8.toString()
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                r14.append(r12)
                r14.append(r6)
                java.lang.String r14 = r14.toString()
                boolean r13 = r13.startsWith(r14)
                if (r13 == 0) goto L_0x00ee
                r7 = r8
                r8 = 0
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
                goto L_0x0110
            L_0x00ee:
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
                goto L_0x0110
            L_0x00f6:
                int r13 = r9 + 1
                int r0 = r0 - r13
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
                goto L_0x0110
            L_0x0101:
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
                goto L_0x0110
            L_0x0109:
                r19 = r7
                r7 = r0
                r0 = r9
                r9 = r8
                r8 = r19
            L_0x0110:
                java.lang.String r14 = "windowBackgroundWhiteBlueText4"
                r15 = -1
                if (r10 != 0) goto L_0x0192
                if (r6 == 0) goto L_0x0192
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r13 = r1.searchAdapterHelper
                java.util.ArrayList r13 = r13.getGlobalSearch()
                int r13 = r13.size()
                if (r13 == 0) goto L_0x018e
                int r0 = r13 + 1
                if (r0 <= r7) goto L_0x018b
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.lang.String r0 = r0.getLastFoundUsername()
                boolean r16 = r0.startsWith(r12)
                if (r16 == 0) goto L_0x0139
                java.lang.String r0 = r0.substring(r4)
                r4 = r0
                goto L_0x013a
            L_0x0139:
                r4 = r0
            L_0x013a:
                android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0180 }
                r0.<init>()     // Catch:{ Exception -> 0x0180 }
                r0.append(r12)     // Catch:{ Exception -> 0x0180 }
                r0.append(r6)     // Catch:{ Exception -> 0x0180 }
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r6, r4)     // Catch:{ Exception -> 0x0180 }
                r16 = r12
                if (r12 == r15) goto L_0x0179
                int r12 = r4.length()     // Catch:{ Exception -> 0x0180 }
                if (r16 != 0) goto L_0x0158
                int r12 = r12 + 1
                r15 = r16
                goto L_0x015c
            L_0x0158:
                int r16 = r16 + 1
                r15 = r16
            L_0x015c:
                android.text.style.ForegroundColorSpan r1 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0180 }
                r17 = r3
                int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)     // Catch:{ Exception -> 0x0175 }
                r1.<init>(r3)     // Catch:{ Exception -> 0x0175 }
                int r3 = r15 + r12
                r18 = r4
                r4 = 33
                r0.setSpan(r1, r15, r3, r4)     // Catch:{ Exception -> 0x0173 }
                r16 = r15
                goto L_0x017d
            L_0x0173:
                r0 = move-exception
                goto L_0x0185
            L_0x0175:
                r0 = move-exception
                r18 = r4
                goto L_0x0185
            L_0x0179:
                r17 = r3
                r18 = r4
            L_0x017d:
                r8 = r0
                r0 = r13
                goto L_0x0194
            L_0x0180:
                r0 = move-exception
                r17 = r3
                r18 = r4
            L_0x0185:
                r8 = r6
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
                r0 = r13
                goto L_0x0194
            L_0x018b:
                r17 = r3
                goto L_0x0190
            L_0x018e:
                r17 = r3
            L_0x0190:
                r0 = r13
                goto L_0x0194
            L_0x0192:
                r17 = r3
            L_0x0194:
                if (r11 == 0) goto L_0x01bd
                java.lang.String r1 = im.bclpbkiauv.messenger.UserObject.getName(r5)
                android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
                r3.<init>(r1)
                r9 = r3
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r1, r11)
                r4 = -1
                if (r3 == r4) goto L_0x01bd
                r4 = r9
                android.text.SpannableStringBuilder r4 = (android.text.SpannableStringBuilder) r4
                android.text.style.ForegroundColorSpan r12 = new android.text.style.ForegroundColorSpan
                int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
                r12.<init>(r13)
                int r13 = r11.length()
                int r13 = r13 + r3
                r14 = 33
                r4.setSpan(r12, r3, r13, r14)
            L_0x01bd:
                android.view.View r1 = r2.itemView
                im.bclpbkiauv.ui.cells.ManageChatUserCell r1 = (im.bclpbkiauv.ui.cells.ManageChatUserCell) r1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r7)
                r1.setTag(r3)
                r3 = 0
                r1.setData(r5, r9, r8, r3)
                r0 = r7
            L_0x01cd:
                return
            L_0x01ce:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow) {
                return 1;
            }
            return 0;
        }
    }

    private static class ListAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;
        ArrayList<String> sortedUsersSectionsArray;
        HashMap<String, ArrayList<TLRPC.User>> usersSectionsDict;

        ListAdapter(Context mContext2, HashMap<String, ArrayList<TLRPC.User>> usersSectionsDict2, ArrayList<String> sortedUsersSectionsArray2) {
            this.mContext = mContext2;
            this.usersSectionsDict = usersSectionsDict2;
            this.sortedUsersSectionsArray = sortedUsersSectionsArray2;
        }

        public Object getItem(int section, int position) {
            if (section < 0 || section >= this.sortedUsersSectionsArray.size()) {
                return null;
            }
            ArrayList<TLRPC.User> arr = this.usersSectionsDict.get(this.sortedUsersSectionsArray.get(section));
            if (position < arr.size()) {
                return arr.get(position);
            }
            return null;
        }

        public String getLetter(int position) {
            int section = getSectionForPosition(position);
            if (section == -1) {
                section = this.sortedUsersSectionsArray.size() - 1;
            }
            if (section < 0 || section > this.sortedUsersSectionsArray.size()) {
                return null;
            }
            return this.sortedUsersSectionsArray.get(section);
        }

        public int getPositionForScrollProgress(float progress) {
            return (int) (((float) getItemCount()) * progress);
        }

        public int getSectionCount() {
            ArrayList<String> arrayList = this.sortedUsersSectionsArray;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }

        public int getCountForSection(int section) {
            ArrayList<TLRPC.User> arr;
            if (section >= this.sortedUsersSectionsArray.size() || (arr = this.usersSectionsDict.get(this.sortedUsersSectionsArray.get(section))) == null) {
                return 0;
            }
            int count = arr.size();
            if (section != this.sortedUsersSectionsArray.size() - 1) {
                return count + 1;
            }
            return count;
        }

        public boolean isEnabled(int section, int row) {
            if (section >= this.sortedUsersSectionsArray.size() || row < this.usersSectionsDict.get(this.sortedUsersSectionsArray.get(section)).size()) {
                return true;
            }
            return false;
        }

        public int getItemViewType(int section, int position) {
            if (section < this.sortedUsersSectionsArray.size()) {
                return position < this.usersSectionsDict.get(this.sortedUsersSectionsArray.get(section)).size() ? 0 : 3;
            }
            return 1;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new LetterSectionCell(this.mContext);
            }
            LetterSectionCell cell = (LetterSectionCell) view;
            if (section < this.sortedUsersSectionsArray.size()) {
                cell.setLetter(this.sortedUsersSectionsArray.get(section));
            } else {
                cell.setLetter("");
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new View(this.mContext);
            } else {
                view = new UserCell(this.mContext, 58, 1, false);
            }
            return new RecyclerListView.Holder(view);
        }

        public int getSectionForChar(char section) {
            for (int i = 0; i < getSectionCount() - 1; i++) {
                if (this.sortedUsersSectionsArray.get(i).toUpperCase().charAt(0) == section) {
                    return i;
                }
            }
            return -1;
        }

        public int getPositionForSection(int section) {
            if (section == -1) {
                return -1;
            }
            int positionStart = 0;
            int N = getSectionCount();
            for (int i = 0; i < N; i++) {
                if (i >= section) {
                    return positionStart;
                }
                positionStart += getCountForSection(i);
            }
            return -1;
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) holder.itemView;
                userCell.setAvatarPadding(6);
                userCell.setData(this.usersSectionsDict.get(this.sortedUsersSectionsArray.get(section)).get(position), (CharSequence) null, (CharSequence) null, 0);
            }
        }
    }
}
