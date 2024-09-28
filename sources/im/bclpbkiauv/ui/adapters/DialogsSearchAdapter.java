package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.sqlite.SQLiteCursor;
import im.bclpbkiauv.sqlite.SQLitePreparedStatement;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.SearchAdapterHelper;
import im.bclpbkiauv.ui.cells.DialogCell;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.HashtagSearchCell;
import im.bclpbkiauv.ui.cells.HintDialogCell;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.cells.ProfileSearchCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DialogsSearchAdapter extends RecyclerListView.SelectionAdapter {
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public DialogsSearchAdapterDelegate delegate;
    private int dialogsType;
    private RecyclerListView innerListView;
    private String lastMessagesSearchString;
    private int lastReqId;
    private int lastSearchId;
    private String lastSearchText;
    /* access modifiers changed from: private */
    public Context mContext;
    private int mProfileSearchCellMarginRight;
    private boolean messagesSearchEndReached;
    private int needMessagesSearch;
    private int nextSearchRate;
    private ArrayList<RecentSearchObject> recentSearchObjects;
    private LongSparseArray<RecentSearchObject> recentSearchObjectsById;
    private int reqId;
    /* access modifiers changed from: private */
    public SearchAdapterHelper searchAdapterHelper;
    private ArrayList<TLObject> searchResult;
    /* access modifiers changed from: private */
    public ArrayList<String> searchResultHashtags;
    private ArrayList<MessageObject> searchResultMessages;
    private ArrayList<CharSequence> searchResultNames;
    private Runnable searchRunnable;
    private Runnable searchRunnable2;
    /* access modifiers changed from: private */
    public boolean searchWas;
    private int selfUserId;

    public interface DialogsSearchAdapterDelegate {
        void didPressedOnSubDialog(long j);

        void needClearList();

        void needRemoveHint(int i);

        void searchStateChanged(boolean z);
    }

    private class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        private DialogSearchResult() {
        }
    }

    protected static class RecentSearchObject {
        int date;
        long did;
        TLObject object;

        protected RecentSearchObject() {
        }
    }

    private class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter {
        private CategoryAdapterRecycler() {
        }

        public void setIndex(int value) {
            notifyDataSetChanged();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new HintDialogCell(DialogsSearchAdapter.this.mContext);
            view.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(86.0f)));
            return new RecyclerListView.Holder(view);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            HintDialogCell cell = (HintDialogCell) holder.itemView;
            TLRPC.TL_topPeer peer = MediaDataController.getInstance(DialogsSearchAdapter.this.currentAccount).hints.get(position);
            new TLRPC.TL_dialog();
            TLRPC.Chat chat = null;
            TLRPC.User user = null;
            int did = 0;
            if (peer.peer.user_id != 0) {
                did = peer.peer.user_id;
                user = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getUser(Integer.valueOf(peer.peer.user_id));
            } else if (peer.peer.channel_id != 0) {
                did = -peer.peer.channel_id;
                chat = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(peer.peer.channel_id));
            } else if (peer.peer.chat_id != 0) {
                did = -peer.peer.chat_id;
                chat = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(peer.peer.chat_id));
            }
            cell.setTag(Integer.valueOf(did));
            String name = "";
            if (user != null) {
                name = UserObject.getFirstName(user);
            } else if (chat != null) {
                name = chat.title;
            }
            cell.setDialog(did, true, name);
        }

        public int getItemCount() {
            return MediaDataController.getInstance(DialogsSearchAdapter.this.currentAccount).hints.size();
        }
    }

    public DialogsSearchAdapter(Context context, int messagesSearch, int type) {
        this(context, messagesSearch, type, 0);
    }

    public DialogsSearchAdapter(Context context, int messagesSearch, int type, int profileSearchCellMarginRight) {
        this.searchResult = new ArrayList<>();
        this.searchResultNames = new ArrayList<>();
        this.searchResultMessages = new ArrayList<>();
        this.searchResultHashtags = new ArrayList<>();
        this.reqId = 0;
        this.lastSearchId = 0;
        this.currentAccount = UserConfig.selectedAccount;
        this.recentSearchObjects = new ArrayList<>();
        this.recentSearchObjectsById = new LongSparseArray<>();
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ SparseArray<TLRPC.User> getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged() {
                boolean unused = DialogsSearchAdapter.this.searchWas = true;
                if (!DialogsSearchAdapter.this.searchAdapterHelper.isSearchInProgress() && DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                for (int a = 0; a < arrayList.size(); a++) {
                    DialogsSearchAdapter.this.searchResultHashtags.add(arrayList.get(a).hashtag);
                }
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
            }
        });
        this.mContext = context;
        this.needMessagesSearch = messagesSearch;
        this.dialogsType = type;
        this.selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        loadRecentSearch();
        MediaDataController.getInstance(this.currentAccount).loadHints(true);
        this.mProfileSearchCellMarginRight = profileSearchCellMarginRight;
    }

    public RecyclerListView getInnerListView() {
        return this.innerListView;
    }

    public void setDelegate(DialogsSearchAdapterDelegate delegate2) {
        this.delegate = delegate2;
    }

    public boolean isMessagesSearchEndReached() {
        return this.messagesSearchEndReached;
    }

    public void loadMoreSearchMessages() {
        searchMessagesInternal(this.lastMessagesSearchString);
    }

    public String getLastSearchString() {
        return this.lastMessagesSearchString;
    }

    private void searchMessagesInternal(String query) {
        if (this.needMessagesSearch == 0) {
            return;
        }
        if (!TextUtils.isEmpty(this.lastMessagesSearchString) || !TextUtils.isEmpty(query)) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchResultMessages.clear();
                this.lastReqId = 0;
                this.lastMessagesSearchString = null;
                this.searchWas = false;
                notifyDataSetChanged();
                DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                if (dialogsSearchAdapterDelegate != null) {
                    dialogsSearchAdapterDelegate.searchStateChanged(false);
                }
            }
        }
    }

    public boolean hasRecentRearch() {
        int i = this.dialogsType;
        return (i == 4 || i == 5 || i == 6 || (this.recentSearchObjects.isEmpty() && MediaDataController.getInstance(this.currentAccount).hints.isEmpty())) ? false : true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001f, code lost:
        r0 = r2.dialogsType;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isRecentSearchDisplayed() {
        /*
            r2 = this;
            int r0 = r2.needMessagesSearch
            r1 = 2
            if (r0 == r1) goto L_0x002c
            boolean r0 = r2.searchWas
            if (r0 != 0) goto L_0x002c
            java.util.ArrayList<im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$RecentSearchObject> r0 = r2.recentSearchObjects
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x001f
            int r0 = r2.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r0 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r0)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_topPeer> r0 = r0.hints
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x002c
        L_0x001f:
            int r0 = r2.dialogsType
            r1 = 4
            if (r0 == r1) goto L_0x002c
            r1 = 5
            if (r0 == r1) goto L_0x002c
            r1 = 6
            if (r0 == r1) goto L_0x002c
            r0 = 1
            goto L_0x002d
        L_0x002c:
            r0 = 0
        L_0x002d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.adapters.DialogsSearchAdapter.isRecentSearchDisplayed():boolean");
    }

    public void loadRecentSearch() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                DialogsSearchAdapter.this.lambda$loadRecentSearch$2$DialogsSearchAdapter();
            }
        });
    }

    public /* synthetic */ void lambda$loadRecentSearch$2$DialogsSearchAdapter() {
        ArrayList<Integer> chatsToLoad;
        try {
            int i = 0;
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
            ArrayList<Integer> usersToLoad = new ArrayList<>();
            ArrayList<Integer> chatsToLoad2 = new ArrayList<>();
            ArrayList<Integer> encryptedToLoad = new ArrayList<>();
            new ArrayList();
            ArrayList<RecentSearchObject> arrayList = new ArrayList<>();
            LongSparseArray<RecentSearchObject> hashMap = new LongSparseArray<>();
            while (cursor.next()) {
                long did = cursor.longValue(i);
                boolean add = false;
                int lower_id = (int) did;
                int high_id = (int) (did >> 32);
                if (lower_id != 0) {
                    if (lower_id > 0) {
                        if (this.dialogsType != 2 && !usersToLoad.contains(Integer.valueOf(lower_id))) {
                            usersToLoad.add(Integer.valueOf(lower_id));
                            add = true;
                        }
                    } else if (!chatsToLoad2.contains(Integer.valueOf(-lower_id))) {
                        chatsToLoad2.add(Integer.valueOf(-lower_id));
                        add = true;
                    }
                } else if ((this.dialogsType == 0 || this.dialogsType == 3) && !encryptedToLoad.contains(Integer.valueOf(high_id))) {
                    encryptedToLoad.add(Integer.valueOf(high_id));
                    add = true;
                }
                if (add) {
                    RecentSearchObject recentSearchObject = new RecentSearchObject();
                    recentSearchObject.did = did;
                    recentSearchObject.date = cursor.intValue(1);
                    arrayList.add(recentSearchObject);
                    chatsToLoad = chatsToLoad2;
                    hashMap.put(recentSearchObject.did, recentSearchObject);
                } else {
                    chatsToLoad = chatsToLoad2;
                }
                chatsToLoad2 = chatsToLoad;
                i = 0;
            }
            ArrayList<Integer> chatsToLoad3 = chatsToLoad2;
            cursor.dispose();
            ArrayList<TLRPC.User> users = new ArrayList<>();
            if (!encryptedToLoad.isEmpty()) {
                ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
                MessagesStorage.getInstance(this.currentAccount).getEncryptedChatsInternal(TextUtils.join(",", encryptedToLoad), encryptedChats, usersToLoad);
                for (int a = 0; a < encryptedChats.size(); a++) {
                    hashMap.get(((long) encryptedChats.get(a).id) << 32).object = encryptedChats.get(a);
                }
            }
            if (!chatsToLoad3.isEmpty()) {
                ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad3), chats);
                for (int a2 = 0; a2 < chats.size(); a2++) {
                    TLRPC.Chat chat = chats.get(a2);
                    long did2 = (long) (-chat.id);
                    if (chat.migrated_to != null) {
                        RecentSearchObject recentSearchObject2 = hashMap.get(did2);
                        hashMap.remove(did2);
                        if (recentSearchObject2 != null) {
                            arrayList.remove(recentSearchObject2);
                        }
                    } else {
                        hashMap.get(did2).object = chat;
                    }
                }
            }
            if (!usersToLoad.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                for (int a3 = 0; a3 < users.size(); a3++) {
                    TLRPC.User user = users.get(a3);
                    RecentSearchObject recentSearchObject3 = hashMap.get((long) user.id);
                    if (recentSearchObject3 != null) {
                        recentSearchObject3.object = user;
                    }
                }
            }
            Collections.sort(arrayList, $$Lambda$DialogsSearchAdapter$0H0Rn7MCqY28yx1jYh1gj6zBFko.INSTANCE);
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, hashMap) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ LongSparseArray f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DialogsSearchAdapter.this.lambda$null$1$DialogsSearchAdapter(this.f$1, this.f$2);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ int lambda$null$0(RecentSearchObject lhs, RecentSearchObject rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    public void putRecentSearch(long did, TLObject object) {
        RecentSearchObject recentSearchObject = this.recentSearchObjectsById.get(did);
        if (recentSearchObject == null) {
            recentSearchObject = new RecentSearchObject();
            this.recentSearchObjectsById.put(did, recentSearchObject);
        } else {
            this.recentSearchObjects.remove(recentSearchObject);
        }
        this.recentSearchObjects.add(0, recentSearchObject);
        recentSearchObject.did = did;
        recentSearchObject.object = object;
        recentSearchObject.date = (int) (System.currentTimeMillis() / 1000);
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(did) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DialogsSearchAdapter.this.lambda$putRecentSearch$3$DialogsSearchAdapter(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$putRecentSearch$3$DialogsSearchAdapter(long did) {
        try {
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
            state.requery();
            state.bindLong(1, did);
            state.bindInteger(2, (int) (System.currentTimeMillis() / 1000));
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void clearRecentSearch() {
        this.recentSearchObjectsById = new LongSparseArray<>();
        this.recentSearchObjects = new ArrayList<>();
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                DialogsSearchAdapter.this.lambda$clearRecentSearch$4$DialogsSearchAdapter();
            }
        });
    }

    public /* synthetic */ void lambda$clearRecentSearch$4$DialogsSearchAdapter() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void addHashtagsFromMessage(CharSequence message) {
        this.searchAdapterHelper.addHashtagsFromMessage(message);
    }

    /* access modifiers changed from: private */
    /* renamed from: setRecentSearch */
    public void lambda$null$1$DialogsSearchAdapter(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> hashMap) {
        this.recentSearchObjects = arrayList;
        this.recentSearchObjectsById = hashMap;
        for (int a = 0; a < this.recentSearchObjects.size(); a++) {
            RecentSearchObject recentSearchObject = this.recentSearchObjects.get(a);
            if (recentSearchObject.object instanceof TLRPC.User) {
                MessagesController.getInstance(this.currentAccount).putUser((TLRPC.User) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof TLRPC.Chat) {
                MessagesController.getInstance(this.currentAccount).putChat((TLRPC.Chat) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof TLRPC.EncryptedChat) {
                MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC.EncryptedChat) recentSearchObject.object, true);
            }
        }
        notifyDataSetChanged();
    }

    private void searchDialogsInternal(String query, int searchId) {
        if (this.needMessagesSearch != 2) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(query, searchId) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DialogsSearchAdapter.this.lambda$searchDialogsInternal$6$DialogsSearchAdapter(this.f$1, this.f$2);
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:168:0x03f9, code lost:
        r29 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0404, code lost:
        r2 = r5.byteBufferValue(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x0408, code lost:
        if (r2 == null) goto L_0x041f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x040a, code lost:
        r30 = r3;
        r31 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:?, code lost:
        r26 = im.bclpbkiauv.tgnet.TLRPC.EncryptedChat.TLdeserialize(r2, r2.readInt32(false), false);
        r2.reuse();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x041c, code lost:
        r3 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x041f, code lost:
        r30 = r3;
        r31 = r4;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:181:0x042a, code lost:
        r2 = r5.byteBufferValue(6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x042b, code lost:
        if (r2 == null) goto L_0x0440;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x042d, code lost:
        r26 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:?, code lost:
        r28 = im.bclpbkiauv.tgnet.TLRPC.User.TLdeserialize(r2, r2.readInt32(false), false);
        r2.reuse();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x043d, code lost:
        r4 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x0440, code lost:
        r26 = r10;
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x0444, code lost:
        if (r3 == null) goto L_0x0558;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0446, code lost:
        if (r4 == null) goto L_0x0558;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:?, code lost:
        r28 = r11;
        r10 = r14.get(((long) r3.id) << 32);
        r32 = r2;
        r3.user_id = r5.intValue(2);
        r3.a_or_b = r5.byteArrayValue(3);
        r3.auth_key = r5.byteArrayValue(4);
        r3.ttl = r5.intValue(5);
        r3.layer = r5.intValue(8);
        r3.seq_in = r5.intValue(9);
        r3.seq_out = r5.intValue(10);
        r11 = r5.intValue(11);
        r3.key_use_count_in = (short) (r11 >> 16);
        r3.key_use_count_out = (short) r11;
        r33 = r0;
        r3.exchange_id = r5.longValue(12);
        r3.key_create_date = r5.intValue(13);
        r3.future_key_fingerprint = r5.longValue(14);
        r3.future_auth_key = r5.byteArrayValue(15);
        r3.key_hash = r5.byteArrayValue(16);
        r3.in_seq_no = r5.intValue(17);
        r0 = r5.intValue(18);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x04d3, code lost:
        if (r0 == 0) goto L_0x04d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x04d5, code lost:
        r3.admin_id = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x04d7, code lost:
        r3.mtproto_seq = r5.intValue(19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x04e1, code lost:
        if (r4.status == null) goto L_0x04ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x04e3, code lost:
        r4.status.expires = r5.intValue(7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x04ed, code lost:
        if (r12 != 1) goto L_0x0520;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x04ef, code lost:
        r34 = r0;
        r10.name = new android.text.SpannableStringBuilder(im.bclpbkiauv.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
        r35 = r11;
        r36 = r12;
        ((android.text.SpannableStringBuilder) r10.name).setSpan(new android.text.style.ForegroundColorSpan(im.bclpbkiauv.ui.actionbar.Theme.getColor(im.bclpbkiauv.ui.actionbar.Theme.key_chats_secretName)), 0, r10.name.length(), 33);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0520, code lost:
        r34 = r0;
        r35 = r11;
        r36 = r12;
        r10.name = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName("@" + r4.username, (java.lang.String) null, "@" + r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x054d, code lost:
        r10.object = r3;
        r0 = r21;
        r0.add(r4);
        r13 = r13 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0558, code lost:
        r33 = r0;
        r32 = r2;
        r28 = r11;
        r36 = r12;
        r0 = r21;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0565 A[Catch:{ Exception -> 0x0779 }, LOOP:6: B:151:0x03d6->B:203:0x0565, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x073b A[Catch:{ Exception -> 0x0777 }, LOOP:10: B:240:0x0663->B:271:0x073b, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0201 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x03f9 A[EDGE_INSN: B:311:0x03f9->B:168:0x03f9 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x06c2 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0282 A[Catch:{ Exception -> 0x077d }, LOOP:2: B:66:0x01cc->B:92:0x0282, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$searchDialogsInternal$6$DialogsSearchAdapter(java.lang.String r38, int r39) {
        /*
            r37 = this;
            r1 = r37
            java.lang.String r0 = " "
            java.lang.String r2 = "SavedMessages"
            r3 = 2131693693(0x7f0f107d, float:1.9016522E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x077d }
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ Exception -> 0x077d }
            java.lang.String r3 = r38.trim()     // Catch:{ Exception -> 0x077d }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ Exception -> 0x077d }
            int r4 = r3.length()     // Catch:{ Exception -> 0x077d }
            r5 = -1
            if (r4 != 0) goto L_0x0037
            r1.lastSearchId = r5     // Catch:{ Exception -> 0x077d }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x077d }
            r0.<init>()     // Catch:{ Exception -> 0x077d }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x077d }
            r4.<init>()     // Catch:{ Exception -> 0x077d }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x077d }
            r5.<init>()     // Catch:{ Exception -> 0x077d }
            int r6 = r1.lastSearchId     // Catch:{ Exception -> 0x077d }
            r1.updateSearchResults(r0, r4, r5, r6)     // Catch:{ Exception -> 0x077d }
            return
        L_0x0037:
            im.bclpbkiauv.messenger.LocaleController r4 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x077d }
            java.lang.String r4 = r4.getTranslitString(r3)     // Catch:{ Exception -> 0x077d }
            boolean r6 = r3.equals(r4)     // Catch:{ Exception -> 0x077d }
            if (r6 != 0) goto L_0x004b
            int r6 = r4.length()     // Catch:{ Exception -> 0x077d }
            if (r6 != 0) goto L_0x004c
        L_0x004b:
            r4 = 0
        L_0x004c:
            r6 = 1
            r7 = 0
            if (r4 == 0) goto L_0x0052
            r8 = 1
            goto L_0x0053
        L_0x0052:
            r8 = 0
        L_0x0053:
            int r8 = r8 + r6
            java.lang.String[] r8 = new java.lang.String[r8]     // Catch:{ Exception -> 0x077d }
            r8[r7] = r3     // Catch:{ Exception -> 0x077d }
            if (r4 == 0) goto L_0x005c
            r8[r6] = r4     // Catch:{ Exception -> 0x077d }
        L_0x005c:
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ Exception -> 0x077d }
            r9.<init>()     // Catch:{ Exception -> 0x077d }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ Exception -> 0x077d }
            r10.<init>()     // Catch:{ Exception -> 0x077d }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ Exception -> 0x077d }
            r11.<init>()     // Catch:{ Exception -> 0x077d }
            java.util.ArrayList r12 = new java.util.ArrayList     // Catch:{ Exception -> 0x077d }
            r12.<init>()     // Catch:{ Exception -> 0x077d }
            r13 = 0
            android.util.LongSparseArray r14 = new android.util.LongSparseArray     // Catch:{ Exception -> 0x077d }
            r14.<init>()     // Catch:{ Exception -> 0x077d }
            int r15 = r1.currentAccount     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.messenger.MessagesStorage r15 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r15)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.sqlite.SQLiteDatabase r15 = r15.getDatabase()     // Catch:{ Exception -> 0x077d }
            java.lang.String r5 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 600"
            java.lang.Object[] r6 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.sqlite.SQLiteCursor r5 = r15.queryFinalized(r5, r6)     // Catch:{ Exception -> 0x077d }
        L_0x0088:
            boolean r6 = r5.next()     // Catch:{ Exception -> 0x077d }
            r15 = 0
            if (r6 == 0) goto L_0x012e
            long r19 = r5.longValue(r7)     // Catch:{ Exception -> 0x077d }
            r21 = r19
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$DialogSearchResult r6 = new im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$DialogSearchResult     // Catch:{ Exception -> 0x077d }
            r6.<init>()     // Catch:{ Exception -> 0x077d }
            r15 = 1
            int r7 = r5.intValue(r15)     // Catch:{ Exception -> 0x077d }
            r6.date = r7     // Catch:{ Exception -> 0x077d }
            r20 = r8
            r7 = r21
            r14.put(r7, r6)     // Catch:{ Exception -> 0x077d }
            int r15 = (int) r7     // Catch:{ Exception -> 0x077d }
            r21 = r12
            r22 = r13
            r16 = 32
            long r12 = r7 >> r16
            int r13 = (int) r12     // Catch:{ Exception -> 0x077d }
            if (r15 == 0) goto L_0x0107
            if (r15 <= 0) goto L_0x00e2
            int r12 = r1.dialogsType     // Catch:{ Exception -> 0x077d }
            r23 = r4
            r4 = 4
            if (r12 != r4) goto L_0x00cb
            int r4 = r1.selfUserId     // Catch:{ Exception -> 0x077d }
            if (r15 != r4) goto L_0x00cb
            r8 = r20
            r12 = r21
            r13 = r22
            r4 = r23
            r7 = 0
            goto L_0x0088
        L_0x00cb:
            int r4 = r1.dialogsType     // Catch:{ Exception -> 0x077d }
            r12 = 2
            if (r4 == r12) goto L_0x0123
            java.lang.Integer r4 = java.lang.Integer.valueOf(r15)     // Catch:{ Exception -> 0x077d }
            boolean r4 = r9.contains(r4)     // Catch:{ Exception -> 0x077d }
            if (r4 != 0) goto L_0x0123
            java.lang.Integer r4 = java.lang.Integer.valueOf(r15)     // Catch:{ Exception -> 0x077d }
            r9.add(r4)     // Catch:{ Exception -> 0x077d }
            goto L_0x0123
        L_0x00e2:
            r23 = r4
            int r4 = r1.dialogsType     // Catch:{ Exception -> 0x077d }
            r12 = 4
            if (r4 != r12) goto L_0x00f3
            r8 = r20
            r12 = r21
            r13 = r22
            r4 = r23
            r7 = 0
            goto L_0x0088
        L_0x00f3:
            int r4 = -r15
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x077d }
            boolean r4 = r10.contains(r4)     // Catch:{ Exception -> 0x077d }
            if (r4 != 0) goto L_0x0123
            int r4 = -r15
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x077d }
            r10.add(r4)     // Catch:{ Exception -> 0x077d }
            goto L_0x0123
        L_0x0107:
            r23 = r4
            int r4 = r1.dialogsType     // Catch:{ Exception -> 0x077d }
            if (r4 == 0) goto L_0x0112
            int r4 = r1.dialogsType     // Catch:{ Exception -> 0x077d }
            r12 = 3
            if (r4 != r12) goto L_0x0123
        L_0x0112:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x077d }
            boolean r4 = r11.contains(r4)     // Catch:{ Exception -> 0x077d }
            if (r4 != 0) goto L_0x0123
            java.lang.Integer r4 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x077d }
            r11.add(r4)     // Catch:{ Exception -> 0x077d }
        L_0x0123:
            r8 = r20
            r12 = r21
            r13 = r22
            r4 = r23
            r7 = 0
            goto L_0x0088
        L_0x012e:
            r23 = r4
            r20 = r8
            r21 = r12
            r22 = r13
            r5.dispose()     // Catch:{ Exception -> 0x077d }
            boolean r4 = r2.contains(r3)     // Catch:{ Exception -> 0x077d }
            if (r4 == 0) goto L_0x0160
            int r4 = r1.currentAccount     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.messenger.UserConfig r4 = im.bclpbkiauv.messenger.UserConfig.getInstance(r4)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getCurrentUser()     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$DialogSearchResult r6 = new im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$DialogSearchResult     // Catch:{ Exception -> 0x077d }
            r6.<init>()     // Catch:{ Exception -> 0x077d }
            r7 = 2147483647(0x7fffffff, float:NaN)
            r6.date = r7     // Catch:{ Exception -> 0x077d }
            r6.name = r2     // Catch:{ Exception -> 0x077d }
            r6.object = r4     // Catch:{ Exception -> 0x077d }
            int r7 = r4.id     // Catch:{ Exception -> 0x077d }
            long r7 = (long) r7     // Catch:{ Exception -> 0x077d }
            r14.put(r7, r6)     // Catch:{ Exception -> 0x077d }
            int r13 = r22 + 1
            goto L_0x0162
        L_0x0160:
            r13 = r22
        L_0x0162:
            boolean r4 = r9.isEmpty()     // Catch:{ Exception -> 0x077d }
            java.lang.String r6 = ";;;"
            java.lang.String r7 = ","
            java.lang.String r8 = "@"
            if (r4 != 0) goto L_0x02b4
            int r4 = r1.currentAccount     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.messenger.MessagesStorage r4 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r4)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.sqlite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ Exception -> 0x077d }
            java.util.Locale r12 = java.util.Locale.US     // Catch:{ Exception -> 0x077d }
            java.lang.String r15 = "SELECT data, status, name FROM users WHERE uid IN(%s)"
            r24 = r2
            r25 = r3
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x077d }
            java.lang.String r2 = android.text.TextUtils.join(r7, r9)     // Catch:{ Exception -> 0x077d }
            r26 = r5
            r5 = 0
            r3[r5] = r2     // Catch:{ Exception -> 0x077d }
            java.lang.String r2 = java.lang.String.format(r12, r15, r3)     // Catch:{ Exception -> 0x077d }
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.sqlite.SQLiteCursor r2 = r4.queryFinalized(r2, r3)     // Catch:{ Exception -> 0x077d }
            r5 = r2
        L_0x0197:
            boolean r2 = r5.next()     // Catch:{ Exception -> 0x077d }
            if (r2 == 0) goto L_0x02a8
            r2 = 2
            java.lang.String r3 = r5.stringValue(r2)     // Catch:{ Exception -> 0x077d }
            r2 = r3
            im.bclpbkiauv.messenger.LocaleController r3 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x077d }
            java.lang.String r3 = r3.getTranslitString(r2)     // Catch:{ Exception -> 0x077d }
            boolean r4 = r2.equals(r3)     // Catch:{ Exception -> 0x077d }
            if (r4 == 0) goto L_0x01b2
            r3 = 0
        L_0x01b2:
            r4 = 0
            int r12 = r2.lastIndexOf(r6)     // Catch:{ Exception -> 0x077d }
            r15 = -1
            if (r12 == r15) goto L_0x01c1
            int r15 = r12 + 3
            java.lang.String r15 = r2.substring(r15)     // Catch:{ Exception -> 0x077d }
            r4 = r15
        L_0x01c1:
            r15 = 0
            r27 = r9
            r9 = r20
            r20 = r12
            int r12 = r9.length     // Catch:{ Exception -> 0x077d }
            r26 = r15
            r15 = 0
        L_0x01cc:
            if (r15 >= r12) goto L_0x0294
            r28 = r9[r15]     // Catch:{ Exception -> 0x077d }
            r29 = r28
            r28 = r12
            r12 = r29
            boolean r29 = r2.contains(r12)     // Catch:{ Exception -> 0x077d }
            if (r29 != 0) goto L_0x01f9
            if (r3 == 0) goto L_0x01e5
            boolean r29 = r3.contains(r12)     // Catch:{ Exception -> 0x077d }
            if (r29 == 0) goto L_0x01e5
            goto L_0x01f9
        L_0x01e5:
            if (r4 == 0) goto L_0x01f4
            boolean r29 = r4.startsWith(r12)     // Catch:{ Exception -> 0x077d }
            if (r29 == 0) goto L_0x01f4
            r26 = 2
            r29 = r2
            r2 = r26
            goto L_0x01ff
        L_0x01f4:
            r29 = r2
            r2 = r26
            goto L_0x01ff
        L_0x01f9:
            r26 = 1
            r29 = r2
            r2 = r26
        L_0x01ff:
            if (r2 == 0) goto L_0x0282
            r15 = 0
            im.bclpbkiauv.tgnet.NativeByteBuffer r19 = r5.byteBufferValue(r15)     // Catch:{ Exception -> 0x077d }
            r26 = r19
            r15 = r26
            if (r15 == 0) goto L_0x0277
            r30 = r3
            r31 = r4
            r3 = 0
            int r4 = r15.readInt32(r3)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.tgnet.TLRPC$User r4 = im.bclpbkiauv.tgnet.TLRPC.User.TLdeserialize(r15, r4, r3)     // Catch:{ Exception -> 0x077d }
            r3 = r4
            r15.reuse()     // Catch:{ Exception -> 0x077d }
            int r4 = r3.id     // Catch:{ Exception -> 0x077d }
            r32 = r6
            r33 = r7
            long r6 = (long) r4     // Catch:{ Exception -> 0x077d }
            java.lang.Object r4 = r14.get(r6)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$DialogSearchResult r4 = (im.bclpbkiauv.ui.adapters.DialogsSearchAdapter.DialogSearchResult) r4     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r3.status     // Catch:{ Exception -> 0x077d }
            if (r6 == 0) goto L_0x023a
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r3.status     // Catch:{ Exception -> 0x077d }
            r26 = r15
            r7 = 1
            int r15 = r5.intValue(r7)     // Catch:{ Exception -> 0x077d }
            r6.expires = r15     // Catch:{ Exception -> 0x077d }
            goto L_0x023c
        L_0x023a:
            r26 = r15
        L_0x023c:
            r6 = 1
            if (r2 != r6) goto L_0x024a
            java.lang.String r6 = r3.first_name     // Catch:{ Exception -> 0x077d }
            java.lang.String r7 = r3.last_name     // Catch:{ Exception -> 0x077d }
            java.lang.CharSequence r6 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r6, r7, r12)     // Catch:{ Exception -> 0x077d }
            r4.name = r6     // Catch:{ Exception -> 0x077d }
            goto L_0x0271
        L_0x024a:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x077d }
            r6.<init>()     // Catch:{ Exception -> 0x077d }
            r6.append(r8)     // Catch:{ Exception -> 0x077d }
            java.lang.String r7 = r3.username     // Catch:{ Exception -> 0x077d }
            r6.append(r7)     // Catch:{ Exception -> 0x077d }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x077d }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x077d }
            r7.<init>()     // Catch:{ Exception -> 0x077d }
            r7.append(r8)     // Catch:{ Exception -> 0x077d }
            r7.append(r12)     // Catch:{ Exception -> 0x077d }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x077d }
            r15 = 0
            java.lang.CharSequence r6 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r6, r15, r7)     // Catch:{ Exception -> 0x077d }
            r4.name = r6     // Catch:{ Exception -> 0x077d }
        L_0x0271:
            r4.object = r3     // Catch:{ Exception -> 0x077d }
            int r13 = r13 + 1
            goto L_0x029e
        L_0x0277:
            r30 = r3
            r31 = r4
            r32 = r6
            r33 = r7
            r26 = r15
            goto L_0x029e
        L_0x0282:
            r30 = r3
            r31 = r4
            r32 = r6
            r33 = r7
            int r15 = r15 + 1
            r26 = r2
            r12 = r28
            r2 = r29
            goto L_0x01cc
        L_0x0294:
            r29 = r2
            r30 = r3
            r31 = r4
            r32 = r6
            r33 = r7
        L_0x029e:
            r20 = r9
            r9 = r27
            r6 = r32
            r7 = r33
            goto L_0x0197
        L_0x02a8:
            r32 = r6
            r33 = r7
            r27 = r9
            r9 = r20
            r5.dispose()     // Catch:{ Exception -> 0x077d }
            goto L_0x02c2
        L_0x02b4:
            r24 = r2
            r25 = r3
            r26 = r5
            r32 = r6
            r33 = r7
            r27 = r9
            r9 = r20
        L_0x02c2:
            boolean r2 = r10.isEmpty()     // Catch:{ Exception -> 0x077d }
            if (r2 != 0) goto L_0x037d
            int r2 = r1.currentAccount     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.messenger.MessagesStorage r2 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r2)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.sqlite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ Exception -> 0x077d }
            java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x077d }
            java.lang.String r4 = "SELECT data, name FROM chats WHERE uid IN(%s)"
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x077d }
            r6 = r33
            java.lang.String r12 = android.text.TextUtils.join(r6, r10)     // Catch:{ Exception -> 0x077d }
            r15 = 0
            r7[r15] = r12     // Catch:{ Exception -> 0x077d }
            java.lang.String r3 = java.lang.String.format(r3, r4, r7)     // Catch:{ Exception -> 0x077d }
            java.lang.Object[] r4 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.sqlite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch:{ Exception -> 0x077d }
            r5 = r2
        L_0x02ed:
            boolean r2 = r5.next()     // Catch:{ Exception -> 0x077d }
            if (r2 == 0) goto L_0x0379
            r2 = 1
            java.lang.String r3 = r5.stringValue(r2)     // Catch:{ Exception -> 0x077d }
            r2 = r3
            im.bclpbkiauv.messenger.LocaleController r3 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x077d }
            java.lang.String r3 = r3.getTranslitString(r2)     // Catch:{ Exception -> 0x077d }
            boolean r4 = r2.equals(r3)     // Catch:{ Exception -> 0x077d }
            if (r4 == 0) goto L_0x0308
            r3 = 0
        L_0x0308:
            int r4 = r9.length     // Catch:{ Exception -> 0x077d }
            r7 = 0
        L_0x030a:
            if (r7 >= r4) goto L_0x0373
            r12 = r9[r7]     // Catch:{ Exception -> 0x077d }
            boolean r15 = r2.contains(r12)     // Catch:{ Exception -> 0x077d }
            if (r15 != 0) goto L_0x0320
            if (r3 == 0) goto L_0x031d
            boolean r15 = r3.contains(r12)     // Catch:{ Exception -> 0x077d }
            if (r15 == 0) goto L_0x031d
            goto L_0x0320
        L_0x031d:
            int r7 = r7 + 1
            goto L_0x030a
        L_0x0320:
            r4 = 0
            im.bclpbkiauv.tgnet.NativeByteBuffer r7 = r5.byteBufferValue(r4)     // Catch:{ Exception -> 0x077d }
            if (r7 == 0) goto L_0x036e
            int r15 = r7.readInt32(r4)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.tgnet.TLRPC$Chat r15 = im.bclpbkiauv.tgnet.TLRPC.Chat.TLdeserialize(r7, r15, r4)     // Catch:{ Exception -> 0x077d }
            r4 = r15
            r7.reuse()     // Catch:{ Exception -> 0x077d }
            if (r4 == 0) goto L_0x0369
            boolean r15 = r4.deactivated     // Catch:{ Exception -> 0x077d }
            if (r15 != 0) goto L_0x0369
            boolean r15 = im.bclpbkiauv.messenger.ChatObject.isChannel(r4)     // Catch:{ Exception -> 0x077d }
            if (r15 == 0) goto L_0x034b
            boolean r15 = im.bclpbkiauv.messenger.ChatObject.isNotInChat(r4)     // Catch:{ Exception -> 0x077d }
            if (r15 != 0) goto L_0x0346
            goto L_0x034b
        L_0x0346:
            r20 = r2
            r26 = r3
            goto L_0x036d
        L_0x034b:
            int r15 = r4.id     // Catch:{ Exception -> 0x077d }
            int r15 = -r15
            r20 = r2
            r26 = r3
            long r2 = (long) r15     // Catch:{ Exception -> 0x077d }
            java.lang.Object r15 = r14.get(r2)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$DialogSearchResult r15 = (im.bclpbkiauv.ui.adapters.DialogsSearchAdapter.DialogSearchResult) r15     // Catch:{ Exception -> 0x077d }
            r28 = r2
            java.lang.String r2 = r4.title     // Catch:{ Exception -> 0x077d }
            r3 = 0
            java.lang.CharSequence r2 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r2, r3, r12)     // Catch:{ Exception -> 0x077d }
            r15.name = r2     // Catch:{ Exception -> 0x077d }
            r15.object = r4     // Catch:{ Exception -> 0x077d }
            int r13 = r13 + 1
            goto L_0x036d
        L_0x0369:
            r20 = r2
            r26 = r3
        L_0x036d:
            goto L_0x0377
        L_0x036e:
            r20 = r2
            r26 = r3
            goto L_0x0377
        L_0x0373:
            r20 = r2
            r26 = r3
        L_0x0377:
            goto L_0x02ed
        L_0x0379:
            r5.dispose()     // Catch:{ Exception -> 0x077d }
            goto L_0x037f
        L_0x037d:
            r6 = r33
        L_0x037f:
            boolean r2 = r11.isEmpty()     // Catch:{ Exception -> 0x077d }
            if (r2 != 0) goto L_0x05ad
            int r2 = r1.currentAccount     // Catch:{ Exception -> 0x0779 }
            im.bclpbkiauv.messenger.MessagesStorage r2 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r2)     // Catch:{ Exception -> 0x0779 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ Exception -> 0x0779 }
            java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x0779 }
            java.lang.String r4 = "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash, q.in_seq_no, q.admin_id, q.mtproto_seq FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)"
            r7 = 1
            java.lang.Object[] r12 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0779 }
            java.lang.String r6 = android.text.TextUtils.join(r6, r11)     // Catch:{ Exception -> 0x0779 }
            r7 = 0
            r12[r7] = r6     // Catch:{ Exception -> 0x0779 }
            java.lang.String r3 = java.lang.String.format(r3, r4, r12)     // Catch:{ Exception -> 0x0779 }
            java.lang.Object[] r4 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0779 }
            im.bclpbkiauv.sqlite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch:{ Exception -> 0x0779 }
            r5 = r2
        L_0x03a8:
            boolean r2 = r5.next()     // Catch:{ Exception -> 0x0779 }
            if (r2 == 0) goto L_0x059f
            r2 = 1
            java.lang.String r3 = r5.stringValue(r2)     // Catch:{ Exception -> 0x0779 }
            r2 = r3
            im.bclpbkiauv.messenger.LocaleController r3 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0779 }
            java.lang.String r3 = r3.getTranslitString(r2)     // Catch:{ Exception -> 0x0779 }
            boolean r4 = r2.equals(r3)     // Catch:{ Exception -> 0x0779 }
            if (r4 == 0) goto L_0x03c3
            r3 = 0
        L_0x03c3:
            r4 = 0
            r6 = r32
            int r7 = r2.lastIndexOf(r6)     // Catch:{ Exception -> 0x0779 }
            r12 = -1
            if (r7 == r12) goto L_0x03d4
            int r12 = r7 + 2
            java.lang.String r12 = r2.substring(r12)     // Catch:{ Exception -> 0x077d }
            r4 = r12
        L_0x03d4:
            r12 = 0
            r15 = 0
        L_0x03d6:
            r20 = r7
            int r7 = r9.length     // Catch:{ Exception -> 0x0779 }
            if (r15 >= r7) goto L_0x0581
            r7 = r9[r15]     // Catch:{ Exception -> 0x0779 }
            boolean r26 = r2.contains(r7)     // Catch:{ Exception -> 0x0779 }
            if (r26 != 0) goto L_0x03f6
            if (r3 == 0) goto L_0x03ec
            boolean r26 = r3.contains(r7)     // Catch:{ Exception -> 0x077d }
            if (r26 == 0) goto L_0x03ec
            goto L_0x03f6
        L_0x03ec:
            if (r4 == 0) goto L_0x03f7
            boolean r26 = r4.startsWith(r7)     // Catch:{ Exception -> 0x077d }
            if (r26 == 0) goto L_0x03f7
            r12 = 2
            goto L_0x03f7
        L_0x03f6:
            r12 = 1
        L_0x03f7:
            if (r12 == 0) goto L_0x0565
            r26 = 0
            r28 = 0
            r29 = r2
            r2 = 0
            im.bclpbkiauv.tgnet.NativeByteBuffer r19 = r5.byteBufferValue(r2)     // Catch:{ Exception -> 0x0779 }
            r30 = r19
            r2 = r30
            if (r2 == 0) goto L_0x041f
            r30 = r3
            r31 = r4
            r3 = 0
            int r4 = r2.readInt32(r3)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r4 = im.bclpbkiauv.tgnet.TLRPC.EncryptedChat.TLdeserialize(r2, r4, r3)     // Catch:{ Exception -> 0x077d }
            r26 = r4
            r2.reuse()     // Catch:{ Exception -> 0x077d }
            r3 = r26
            goto L_0x0425
        L_0x041f:
            r30 = r3
            r31 = r4
            r3 = r26
        L_0x0425:
            r4 = 6
            im.bclpbkiauv.tgnet.NativeByteBuffer r4 = r5.byteBufferValue(r4)     // Catch:{ Exception -> 0x0779 }
            r2 = r4
            if (r2 == 0) goto L_0x0440
            r26 = r10
            r4 = 0
            int r10 = r2.readInt32(r4)     // Catch:{ Exception -> 0x077d }
            im.bclpbkiauv.tgnet.TLRPC$User r10 = im.bclpbkiauv.tgnet.TLRPC.User.TLdeserialize(r2, r10, r4)     // Catch:{ Exception -> 0x077d }
            r28 = r10
            r2.reuse()     // Catch:{ Exception -> 0x077d }
            r4 = r28
            goto L_0x0444
        L_0x0440:
            r26 = r10
            r4 = r28
        L_0x0444:
            if (r3 == 0) goto L_0x0558
            if (r4 == 0) goto L_0x0558
            int r10 = r3.id     // Catch:{ Exception -> 0x0779 }
            r28 = r11
            long r10 = (long) r10     // Catch:{ Exception -> 0x0779 }
            r16 = 32
            long r10 = r10 << r16
            java.lang.Object r10 = r14.get(r10)     // Catch:{ Exception -> 0x0779 }
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$DialogSearchResult r10 = (im.bclpbkiauv.ui.adapters.DialogsSearchAdapter.DialogSearchResult) r10     // Catch:{ Exception -> 0x0779 }
            r32 = r2
            r11 = 2
            int r2 = r5.intValue(r11)     // Catch:{ Exception -> 0x0779 }
            r3.user_id = r2     // Catch:{ Exception -> 0x0779 }
            r2 = 3
            byte[] r11 = r5.byteArrayValue(r2)     // Catch:{ Exception -> 0x0779 }
            r3.a_or_b = r11     // Catch:{ Exception -> 0x0779 }
            r2 = 4
            byte[] r11 = r5.byteArrayValue(r2)     // Catch:{ Exception -> 0x0779 }
            r3.auth_key = r11     // Catch:{ Exception -> 0x0779 }
            r11 = 5
            int r11 = r5.intValue(r11)     // Catch:{ Exception -> 0x0779 }
            r3.ttl = r11     // Catch:{ Exception -> 0x0779 }
            r11 = 8
            int r11 = r5.intValue(r11)     // Catch:{ Exception -> 0x0779 }
            r3.layer = r11     // Catch:{ Exception -> 0x0779 }
            r11 = 9
            int r11 = r5.intValue(r11)     // Catch:{ Exception -> 0x0779 }
            r3.seq_in = r11     // Catch:{ Exception -> 0x0779 }
            r11 = 10
            int r11 = r5.intValue(r11)     // Catch:{ Exception -> 0x0779 }
            r3.seq_out = r11     // Catch:{ Exception -> 0x0779 }
            r11 = 11
            int r11 = r5.intValue(r11)     // Catch:{ Exception -> 0x0779 }
            int r2 = r11 >> 16
            short r2 = (short) r2     // Catch:{ Exception -> 0x0779 }
            r3.key_use_count_in = r2     // Catch:{ Exception -> 0x0779 }
            short r2 = (short) r11     // Catch:{ Exception -> 0x0779 }
            r3.key_use_count_out = r2     // Catch:{ Exception -> 0x0779 }
            r2 = 12
            r33 = r0
            long r0 = r5.longValue(r2)     // Catch:{ Exception -> 0x0779 }
            r3.exchange_id = r0     // Catch:{ Exception -> 0x0779 }
            r0 = 13
            int r0 = r5.intValue(r0)     // Catch:{ Exception -> 0x0779 }
            r3.key_create_date = r0     // Catch:{ Exception -> 0x0779 }
            r0 = 14
            long r0 = r5.longValue(r0)     // Catch:{ Exception -> 0x0779 }
            r3.future_key_fingerprint = r0     // Catch:{ Exception -> 0x0779 }
            r0 = 15
            byte[] r0 = r5.byteArrayValue(r0)     // Catch:{ Exception -> 0x0779 }
            r3.future_auth_key = r0     // Catch:{ Exception -> 0x0779 }
            r0 = 16
            byte[] r0 = r5.byteArrayValue(r0)     // Catch:{ Exception -> 0x0779 }
            r3.key_hash = r0     // Catch:{ Exception -> 0x0779 }
            r0 = 17
            int r0 = r5.intValue(r0)     // Catch:{ Exception -> 0x0779 }
            r3.in_seq_no = r0     // Catch:{ Exception -> 0x0779 }
            r0 = 18
            int r0 = r5.intValue(r0)     // Catch:{ Exception -> 0x0779 }
            if (r0 == 0) goto L_0x04d7
            r3.admin_id = r0     // Catch:{ Exception -> 0x0779 }
        L_0x04d7:
            r1 = 19
            int r1 = r5.intValue(r1)     // Catch:{ Exception -> 0x0779 }
            r3.mtproto_seq = r1     // Catch:{ Exception -> 0x0779 }
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r1 = r4.status     // Catch:{ Exception -> 0x0779 }
            if (r1 == 0) goto L_0x04ec
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r1 = r4.status     // Catch:{ Exception -> 0x0779 }
            r2 = 7
            int r2 = r5.intValue(r2)     // Catch:{ Exception -> 0x0779 }
            r1.expires = r2     // Catch:{ Exception -> 0x0779 }
        L_0x04ec:
            r1 = 1
            if (r12 != r1) goto L_0x0520
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0779 }
            java.lang.String r2 = r4.first_name     // Catch:{ Exception -> 0x0779 }
            r34 = r0
            java.lang.String r0 = r4.last_name     // Catch:{ Exception -> 0x0779 }
            java.lang.String r0 = im.bclpbkiauv.messenger.ContactsController.formatName(r2, r0)     // Catch:{ Exception -> 0x0779 }
            r1.<init>(r0)     // Catch:{ Exception -> 0x0779 }
            r10.name = r1     // Catch:{ Exception -> 0x0779 }
            java.lang.CharSequence r0 = r10.name     // Catch:{ Exception -> 0x0779 }
            android.text.SpannableStringBuilder r0 = (android.text.SpannableStringBuilder) r0     // Catch:{ Exception -> 0x0779 }
            android.text.style.ForegroundColorSpan r1 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0779 }
            java.lang.String r2 = "chats_secretName"
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)     // Catch:{ Exception -> 0x0779 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0779 }
            java.lang.CharSequence r2 = r10.name     // Catch:{ Exception -> 0x0779 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x0779 }
            r35 = r11
            r11 = 33
            r36 = r12
            r12 = 0
            r0.setSpan(r1, r12, r2, r11)     // Catch:{ Exception -> 0x0779 }
            goto L_0x054d
        L_0x0520:
            r34 = r0
            r35 = r11
            r36 = r12
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0779 }
            r0.<init>()     // Catch:{ Exception -> 0x0779 }
            r0.append(r8)     // Catch:{ Exception -> 0x0779 }
            java.lang.String r1 = r4.username     // Catch:{ Exception -> 0x0779 }
            r0.append(r1)     // Catch:{ Exception -> 0x0779 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0779 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0779 }
            r1.<init>()     // Catch:{ Exception -> 0x0779 }
            r1.append(r8)     // Catch:{ Exception -> 0x0779 }
            r1.append(r7)     // Catch:{ Exception -> 0x0779 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0779 }
            r2 = 0
            java.lang.CharSequence r0 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r0, r2, r1)     // Catch:{ Exception -> 0x0779 }
            r10.name = r0     // Catch:{ Exception -> 0x0779 }
        L_0x054d:
            r10.object = r3     // Catch:{ Exception -> 0x0779 }
            r0 = r21
            r0.add(r4)     // Catch:{ Exception -> 0x0779 }
            int r13 = r13 + 1
            goto L_0x0591
        L_0x0558:
            r33 = r0
            r32 = r2
            r28 = r11
            r36 = r12
            r0 = r21
            r16 = 32
            goto L_0x0591
        L_0x0565:
            r33 = r0
            r29 = r2
            r30 = r3
            r31 = r4
            r26 = r10
            r28 = r11
            r36 = r12
            r0 = r21
            r16 = 32
            int r15 = r15 + 1
            r1 = r37
            r7 = r20
            r0 = r33
            goto L_0x03d6
        L_0x0581:
            r33 = r0
            r29 = r2
            r30 = r3
            r31 = r4
            r26 = r10
            r28 = r11
            r0 = r21
            r16 = 32
        L_0x0591:
            r1 = r37
            r21 = r0
            r32 = r6
            r10 = r26
            r11 = r28
            r0 = r33
            goto L_0x03a8
        L_0x059f:
            r33 = r0
            r26 = r10
            r28 = r11
            r0 = r21
            r6 = r32
            r5.dispose()     // Catch:{ Exception -> 0x0779 }
            goto L_0x05b7
        L_0x05ad:
            r33 = r0
            r26 = r10
            r28 = r11
            r0 = r21
            r6 = r32
        L_0x05b7:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0779 }
            r1.<init>(r13)     // Catch:{ Exception -> 0x0779 }
            r2 = 0
        L_0x05bd:
            int r3 = r14.size()     // Catch:{ Exception -> 0x0779 }
            if (r2 >= r3) goto L_0x05d7
            java.lang.Object r3 = r14.valueAt(r2)     // Catch:{ Exception -> 0x0779 }
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$DialogSearchResult r3 = (im.bclpbkiauv.ui.adapters.DialogsSearchAdapter.DialogSearchResult) r3     // Catch:{ Exception -> 0x0779 }
            im.bclpbkiauv.tgnet.TLObject r4 = r3.object     // Catch:{ Exception -> 0x0779 }
            if (r4 == 0) goto L_0x05d4
            java.lang.CharSequence r4 = r3.name     // Catch:{ Exception -> 0x0779 }
            if (r4 == 0) goto L_0x05d4
            r1.add(r3)     // Catch:{ Exception -> 0x0779 }
        L_0x05d4:
            int r2 = r2 + 1
            goto L_0x05bd
        L_0x05d7:
            im.bclpbkiauv.ui.adapters.-$$Lambda$DialogsSearchAdapter$9SFH42pTuZzJv03lCDEvxOn44tI r2 = im.bclpbkiauv.ui.adapters.$$Lambda$DialogsSearchAdapter$9SFH42pTuZzJv03lCDEvxOn44tI.INSTANCE     // Catch:{ Exception -> 0x0779 }
            java.util.Collections.sort(r1, r2)     // Catch:{ Exception -> 0x0779 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0779 }
            r2.<init>()     // Catch:{ Exception -> 0x0779 }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x0779 }
            r3.<init>()     // Catch:{ Exception -> 0x0779 }
            r4 = 0
        L_0x05e7:
            int r7 = r1.size()     // Catch:{ Exception -> 0x0779 }
            if (r4 >= r7) goto L_0x0601
            java.lang.Object r7 = r1.get(r4)     // Catch:{ Exception -> 0x0779 }
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$DialogSearchResult r7 = (im.bclpbkiauv.ui.adapters.DialogsSearchAdapter.DialogSearchResult) r7     // Catch:{ Exception -> 0x0779 }
            im.bclpbkiauv.tgnet.TLObject r10 = r7.object     // Catch:{ Exception -> 0x0779 }
            r2.add(r10)     // Catch:{ Exception -> 0x0779 }
            java.lang.CharSequence r10 = r7.name     // Catch:{ Exception -> 0x0779 }
            r3.add(r10)     // Catch:{ Exception -> 0x0779 }
            int r4 = r4 + 1
            goto L_0x05e7
        L_0x0601:
            r4 = r37
            int r7 = r4.dialogsType     // Catch:{ Exception -> 0x0777 }
            r10 = 2
            if (r7 == r10) goto L_0x076b
            int r7 = r4.currentAccount     // Catch:{ Exception -> 0x0777 }
            im.bclpbkiauv.messenger.MessagesStorage r7 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r7)     // Catch:{ Exception -> 0x0777 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r7 = r7.getDatabase()     // Catch:{ Exception -> 0x0777 }
            java.lang.String r10 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid"
            r11 = 0
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x0777 }
            im.bclpbkiauv.sqlite.SQLiteCursor r7 = r7.queryFinalized(r10, r12)     // Catch:{ Exception -> 0x0777 }
            r5 = r7
        L_0x061c:
            boolean r7 = r5.next()     // Catch:{ Exception -> 0x0777 }
            if (r7 == 0) goto L_0x0763
            r7 = 3
            int r10 = r5.intValue(r7)     // Catch:{ Exception -> 0x0777 }
            long r11 = (long) r10     // Catch:{ Exception -> 0x0777 }
            int r11 = r14.indexOfKey(r11)     // Catch:{ Exception -> 0x0777 }
            if (r11 < 0) goto L_0x062f
            goto L_0x061c
        L_0x062f:
            r11 = 2
            java.lang.String r12 = r5.stringValue(r11)     // Catch:{ Exception -> 0x0777 }
            im.bclpbkiauv.messenger.LocaleController r15 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0777 }
            java.lang.String r15 = r15.getTranslitString(r12)     // Catch:{ Exception -> 0x0777 }
            boolean r16 = r12.equals(r15)     // Catch:{ Exception -> 0x0777 }
            if (r16 == 0) goto L_0x0643
            r15 = 0
        L_0x0643:
            r16 = 0
            int r17 = r12.lastIndexOf(r6)     // Catch:{ Exception -> 0x0777 }
            r18 = r17
            r7 = r18
            r11 = -1
            if (r7 == r11) goto L_0x0659
            int r11 = r7 + 3
            java.lang.String r11 = r12.substring(r11)     // Catch:{ Exception -> 0x0777 }
            r16 = r11
            goto L_0x065b
        L_0x0659:
            r11 = r16
        L_0x065b:
            r16 = 0
            r18 = r1
            int r1 = r9.length     // Catch:{ Exception -> 0x0777 }
            r32 = r6
            r6 = 0
        L_0x0663:
            if (r6 >= r1) goto L_0x0751
            r20 = r9[r6]     // Catch:{ Exception -> 0x0777 }
            r21 = r20
            r20 = r1
            r1 = r21
            boolean r21 = r12.startsWith(r1)     // Catch:{ Exception -> 0x0777 }
            if (r21 != 0) goto L_0x06b9
            r21 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0777 }
            r7.<init>()     // Catch:{ Exception -> 0x0777 }
            r29 = r9
            r9 = r33
            r7.append(r9)     // Catch:{ Exception -> 0x0777 }
            r7.append(r1)     // Catch:{ Exception -> 0x0777 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0777 }
            boolean r7 = r12.contains(r7)     // Catch:{ Exception -> 0x0777 }
            if (r7 != 0) goto L_0x06bf
            if (r15 == 0) goto L_0x06ac
            boolean r7 = r15.startsWith(r1)     // Catch:{ Exception -> 0x0777 }
            if (r7 != 0) goto L_0x06bf
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0777 }
            r7.<init>()     // Catch:{ Exception -> 0x0777 }
            r7.append(r9)     // Catch:{ Exception -> 0x0777 }
            r7.append(r1)     // Catch:{ Exception -> 0x0777 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0777 }
            boolean r7 = r15.contains(r7)     // Catch:{ Exception -> 0x0777 }
            if (r7 == 0) goto L_0x06ac
            goto L_0x06bf
        L_0x06ac:
            if (r11 == 0) goto L_0x06b6
            boolean r7 = r11.startsWith(r1)     // Catch:{ Exception -> 0x0777 }
            if (r7 == 0) goto L_0x06b6
            r7 = 2
            goto L_0x06c0
        L_0x06b6:
            r7 = r16
            goto L_0x06c0
        L_0x06b9:
            r21 = r7
            r29 = r9
            r9 = r33
        L_0x06bf:
            r7 = 1
        L_0x06c0:
            if (r7 == 0) goto L_0x073b
            r6 = 0
            im.bclpbkiauv.tgnet.NativeByteBuffer r16 = r5.byteBufferValue(r6)     // Catch:{ Exception -> 0x0777 }
            r19 = r16
            r6 = r19
            if (r6 == 0) goto L_0x0730
            r33 = r9
            r19 = r10
            r9 = 0
            int r10 = r6.readInt32(r9)     // Catch:{ Exception -> 0x0777 }
            im.bclpbkiauv.tgnet.TLRPC$User r10 = im.bclpbkiauv.tgnet.TLRPC.User.TLdeserialize(r6, r10, r9)     // Catch:{ Exception -> 0x0777 }
            r6.reuse()     // Catch:{ Exception -> 0x0777 }
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r9 = r10.status     // Catch:{ Exception -> 0x0777 }
            if (r9 == 0) goto L_0x06ef
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r9 = r10.status     // Catch:{ Exception -> 0x0777 }
            r20 = r6
            r30 = r11
            r6 = 1
            int r11 = r5.intValue(r6)     // Catch:{ Exception -> 0x0777 }
            r9.expires = r11     // Catch:{ Exception -> 0x0777 }
            goto L_0x06f3
        L_0x06ef:
            r20 = r6
            r30 = r11
        L_0x06f3:
            r9 = 1
            if (r7 != r9) goto L_0x0703
            java.lang.String r6 = r10.first_name     // Catch:{ Exception -> 0x0777 }
            java.lang.String r11 = r10.last_name     // Catch:{ Exception -> 0x0777 }
            java.lang.CharSequence r6 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r6, r11, r1)     // Catch:{ Exception -> 0x0777 }
            r3.add(r6)     // Catch:{ Exception -> 0x0777 }
            r9 = 0
            goto L_0x072b
        L_0x0703:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0777 }
            r6.<init>()     // Catch:{ Exception -> 0x0777 }
            r6.append(r8)     // Catch:{ Exception -> 0x0777 }
            java.lang.String r11 = r10.username     // Catch:{ Exception -> 0x0777 }
            r6.append(r11)     // Catch:{ Exception -> 0x0777 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0777 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0777 }
            r11.<init>()     // Catch:{ Exception -> 0x0777 }
            r11.append(r8)     // Catch:{ Exception -> 0x0777 }
            r11.append(r1)     // Catch:{ Exception -> 0x0777 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0777 }
            r9 = 0
            java.lang.CharSequence r6 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r6, r9, r11)     // Catch:{ Exception -> 0x0777 }
            r3.add(r6)     // Catch:{ Exception -> 0x0777 }
        L_0x072b:
            r2.add(r10)     // Catch:{ Exception -> 0x0777 }
            r10 = 0
            goto L_0x075b
        L_0x0730:
            r20 = r6
            r33 = r9
            r19 = r10
            r30 = r11
            r9 = 0
            r10 = 0
            goto L_0x075b
        L_0x073b:
            r33 = r9
            r19 = r10
            r30 = r11
            r9 = 0
            r10 = 0
            int r6 = r6 + 1
            r16 = r7
            r10 = r19
            r1 = r20
            r7 = r21
            r9 = r29
            goto L_0x0663
        L_0x0751:
            r21 = r7
            r29 = r9
            r19 = r10
            r30 = r11
            r9 = 0
            r10 = 0
        L_0x075b:
            r1 = r18
            r9 = r29
            r6 = r32
            goto L_0x061c
        L_0x0763:
            r18 = r1
            r29 = r9
            r5.dispose()     // Catch:{ Exception -> 0x0777 }
            goto L_0x076f
        L_0x076b:
            r18 = r1
            r29 = r9
        L_0x076f:
            r1 = r39
            r4.updateSearchResults(r2, r3, r0, r1)     // Catch:{ Exception -> 0x0775 }
            goto L_0x0784
        L_0x0775:
            r0 = move-exception
            goto L_0x0781
        L_0x0777:
            r0 = move-exception
            goto L_0x077f
        L_0x0779:
            r0 = move-exception
            r4 = r37
            goto L_0x077f
        L_0x077d:
            r0 = move-exception
            r4 = r1
        L_0x077f:
            r1 = r39
        L_0x0781:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0784:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.adapters.DialogsSearchAdapter.lambda$searchDialogsInternal$6$DialogsSearchAdapter(java.lang.String, int):void");
    }

    static /* synthetic */ int lambda$null$5(DialogSearchResult lhs, DialogSearchResult rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    private void updateSearchResults(ArrayList<TLObject> result, ArrayList<CharSequence> names, ArrayList<TLRPC.User> encUsers, int searchId) {
        AndroidUtilities.runOnUIThread(new Runnable(searchId, result, encUsers, names) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ ArrayList f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                DialogsSearchAdapter.this.lambda$updateSearchResults$7$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$updateSearchResults$7$DialogsSearchAdapter(int searchId, ArrayList result, ArrayList encUsers, ArrayList names) {
        if (searchId == this.lastSearchId) {
            this.searchWas = true;
            for (int a = 0; a < result.size(); a++) {
                TLObject obj = (TLObject) result.get(a);
                if (obj instanceof TLRPC.User) {
                    MessagesController.getInstance(this.currentAccount).putUser((TLRPC.User) obj, true);
                } else if (obj instanceof TLRPC.Chat) {
                    MessagesController.getInstance(this.currentAccount).putChat((TLRPC.Chat) obj, true);
                } else if (obj instanceof TLRPC.EncryptedChat) {
                    MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC.EncryptedChat) obj, true);
                }
            }
            MessagesController.getInstance(this.currentAccount).putUsers(encUsers, true);
            this.searchResult = result;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(result);
            notifyDataSetChanged();
            if (this.delegate == null) {
                return;
            }
            if (getItemCount() != 0 || (this.searchRunnable2 == null && !this.searchAdapterHelper.isSearchInProgress())) {
                this.delegate.searchStateChanged(false);
            } else {
                this.delegate.searchStateChanged(true);
            }
        }
    }

    public boolean isHashtagSearch() {
        return !this.searchResultHashtags.isEmpty();
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
    }

    public void searchDialogs(String text) {
        String query;
        String str = text;
        if (str == null || !str.equals(this.lastSearchText)) {
            this.lastSearchText = str;
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            Runnable runnable = this.searchRunnable2;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable2 = null;
            }
            if (str != null) {
                query = text.trim();
            } else {
                query = null;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchAdapterHelper.unloadRecentHashtags();
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchResultHashtags.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                if (this.needMessagesSearch != 2) {
                    this.searchAdapterHelper.queryServerSearch((String) null, true, true, true, true, 0, this.dialogsType == 0, 0);
                }
                this.searchWas = false;
                this.lastSearchId = -1;
                searchMessagesInternal((String) null);
                notifyDataSetChanged();
                return;
            }
            if (this.needMessagesSearch == 2 || !query.startsWith("#") || query.length() != 1) {
                this.searchResultHashtags.clear();
                notifyDataSetChanged();
            } else {
                this.messagesSearchEndReached = true;
                if (this.searchAdapterHelper.loadRecentHashtags()) {
                    this.searchResultMessages.clear();
                    this.searchResultHashtags.clear();
                    ArrayList<SearchAdapterHelper.HashtagObject> hashtags = this.searchAdapterHelper.getHashtags();
                    for (int a = 0; a < hashtags.size(); a++) {
                        this.searchResultHashtags.add(hashtags.get(a).hashtag);
                    }
                    DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                    if (dialogsSearchAdapterDelegate != null) {
                        dialogsSearchAdapterDelegate.searchStateChanged(false);
                    }
                }
                notifyDataSetChanged();
            }
            int searchId = this.lastSearchId + 1;
            this.lastSearchId = searchId;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$DialogsSearchAdapter$wO95aohU0D6TQR1dDmOWcOrnkk r5 = new Runnable(query, searchId, str) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    DialogsSearchAdapter.this.lambda$searchDialogs$9$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            };
            this.searchRunnable = r5;
            dispatchQueue.postRunnable(r5, 300);
        }
    }

    public /* synthetic */ void lambda$searchDialogs$9$DialogsSearchAdapter(String query, int searchId, String text) {
        this.searchRunnable = null;
        searchDialogsInternal(query, searchId);
        $$Lambda$DialogsSearchAdapter$uoZ1nlrsz2zZlXvaNm_R93GieLw r0 = new Runnable(searchId, query, text) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                DialogsSearchAdapter.this.lambda$null$8$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3);
            }
        };
        this.searchRunnable2 = r0;
        AndroidUtilities.runOnUIThread(r0);
    }

    public /* synthetic */ void lambda$null$8$DialogsSearchAdapter(int searchId, String query, String text) {
        this.searchRunnable2 = null;
        if (searchId == this.lastSearchId) {
            if (this.needMessagesSearch != 2) {
                this.searchAdapterHelper.queryServerSearch(query, true, this.dialogsType != 4, true, this.dialogsType != 4, 0, this.dialogsType == 0, 0);
            }
            searchMessagesInternal(text);
        }
    }

    public int getItemCount() {
        if (isRecentSearchDisplayed()) {
            int i = 0;
            int size = !this.recentSearchObjects.isEmpty() ? this.recentSearchObjects.size() + 1 : 0;
            if (!MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) {
                i = 2;
            }
            return size + i;
        } else if (!this.searchResultHashtags.isEmpty()) {
            return this.searchResultHashtags.size() + 1;
        } else {
            int count = this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
            int messagesCount = this.searchResultMessages.size();
            int count2 = count + localServerCount;
            if (globalCount != 0) {
                count2 += globalCount + 1;
            }
            if (phoneCount != 0) {
                count2 += phoneCount;
            }
            if (messagesCount != 0) {
                return count2 + messagesCount + 1 + (this.messagesSearchEndReached ^ true ? 1 : 0);
            }
            return count2;
        }
    }

    public Object getItem(int i) {
        TLObject chat;
        int messagesCount = 0;
        if (isRecentSearchDisplayed()) {
            if (!MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) {
                messagesCount = 2;
            }
            int offset = messagesCount;
            if (i <= offset || (i - 1) - offset >= this.recentSearchObjects.size()) {
                return null;
            }
            TLObject object = this.recentSearchObjects.get((i - 1) - offset).object;
            if (object instanceof TLRPC.User) {
                TLObject user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.User) object).id));
                if (user != null) {
                    return user;
                }
                return object;
            } else if (!(object instanceof TLRPC.Chat) || (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(((TLRPC.Chat) object).id))) == null) {
                return object;
            } else {
                return chat;
            }
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            ArrayList<Object> phoneSearch = this.searchAdapterHelper.getPhoneSearch();
            int localCount = this.searchResult.size();
            int localServerCount = localServerSearch.size();
            int phoneCount = phoneSearch.size();
            int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (!this.searchResultMessages.isEmpty()) {
                messagesCount = this.searchResultMessages.size() + 1;
            }
            if (i >= 0 && i < localCount) {
                return this.searchResult.get(i);
            }
            int i2 = i - localCount;
            if (i2 >= 0 && i2 < localServerCount) {
                return localServerSearch.get(i2);
            }
            int i3 = i2 - localServerCount;
            if (i3 >= 0 && i3 < phoneCount) {
                return phoneSearch.get(i3);
            }
            int i4 = i3 - phoneCount;
            if (i4 > 0 && i4 < globalCount) {
                return globalSearch.get(i4 - 1);
            }
            int i5 = i4 - globalCount;
            if (i5 <= 0 || i5 >= messagesCount) {
                return null;
            }
            return this.searchResultMessages.get(i5 - 1);
        } else if (i > 0) {
            return this.searchResultHashtags.get(i - 1);
        } else {
            return null;
        }
    }

    public boolean isGlobalSearch(int i) {
        if (isRecentSearchDisplayed() || !this.searchResultHashtags.isEmpty()) {
            return false;
        }
        ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
        ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
        int localCount = this.searchResult.size();
        int localServerCount = localServerSearch.size();
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        int messagesCount = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
        if (i >= 0 && i < localCount) {
            return false;
        }
        int i2 = i - localCount;
        if (i2 >= 0 && i2 < localServerCount) {
            return false;
        }
        int i3 = i2 - localServerCount;
        if (i3 > 0 && i3 < phoneCount) {
            return false;
        }
        int i4 = i3 - phoneCount;
        if (i4 > 0 && i4 < globalCount) {
            return true;
        }
        int i5 = i4 - globalCount;
        return (i5 <= 0 || i5 < messagesCount) ? false : false;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int type = holder.getItemViewType();
        return (type == 1 || type == 3) ? false : true;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int i;
        View view = null;
        switch (viewType) {
            case 0:
                view = new ProfileSearchCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                break;
            case 1:
                view = new GraySectionCell(this.mContext);
                break;
            case 2:
                view = new DialogCell(this.mContext, false, true);
                break;
            case 3:
                view = new LoadingCell(this.mContext);
                break;
            case 4:
                view = new HashtagSearchCell(this.mContext);
                break;
            case 5:
                RecyclerListView horizontalListView = new RecyclerListView(this.mContext) {
                    public boolean onInterceptTouchEvent(MotionEvent e) {
                        if (!(getParent() == null || getParent().getParent() == null)) {
                            getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        return super.onInterceptTouchEvent(e);
                    }
                };
                horizontalListView.setTag(9);
                horizontalListView.setItemAnimator((RecyclerView.ItemAnimator) null);
                horizontalListView.setLayoutAnimation((LayoutAnimationController) null);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this.mContext) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                layoutManager.setOrientation(0);
                horizontalListView.setLayoutManager(layoutManager);
                horizontalListView.setAdapter(new CategoryAdapterRecycler());
                horizontalListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                    public final void onItemClick(View view, int i) {
                        DialogsSearchAdapter.this.lambda$onCreateViewHolder$10$DialogsSearchAdapter(view, i);
                    }
                });
                horizontalListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                    public final boolean onItemClick(View view, int i) {
                        return DialogsSearchAdapter.this.lambda$onCreateViewHolder$11$DialogsSearchAdapter(view, i);
                    }
                });
                view = horizontalListView;
                this.innerListView = horizontalListView;
                break;
            case 6:
                view = new TextCell(this.mContext, 16);
                break;
        }
        if (viewType == 5) {
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(86.0f)));
        } else {
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
            if (viewType == 0 && (i = this.mProfileSearchCellMarginRight) != 0) {
                lp.rightMargin = i;
            }
            view.setLayoutParams(lp);
        }
        return new RecyclerListView.Holder(view);
    }

    public /* synthetic */ void lambda$onCreateViewHolder$10$DialogsSearchAdapter(View view1, int position) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.didPressedOnSubDialog((long) ((Integer) view1.getTag()).intValue());
        }
    }

    public /* synthetic */ boolean lambda$onCreateViewHolder$11$DialogsSearchAdapter(View view12, int position) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate == null) {
            return true;
        }
        dialogsSearchAdapterDelegate.needRemoveHint(((Integer) view12.getTag()).intValue());
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0418  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r29, int r30) {
        /*
            r28 = this;
            r1 = r28
            r2 = r29
            r3 = r30
            int r0 = r29.getItemViewType()
            r4 = 2
            r5 = 0
            r6 = 1
            if (r0 == 0) goto L_0x017f
            if (r0 == r6) goto L_0x00aa
            if (r0 == r4) goto L_0x0089
            r4 = 4
            if (r0 == r4) goto L_0x006a
            r4 = 5
            if (r0 == r4) goto L_0x0059
            r4 = 6
            if (r0 == r4) goto L_0x001e
            goto L_0x0427
        L_0x001e:
            java.lang.Object r0 = r1.getItem(r3)
            java.lang.String r0 = (java.lang.String) r0
            android.view.View r4 = r2.itemView
            im.bclpbkiauv.ui.cells.TextCell r4 = (im.bclpbkiauv.ui.cells.TextCell) r4
            r7 = 0
            java.lang.String r8 = "windowBackgroundWhiteBlueText2"
            r4.setColors(r7, r8)
            r7 = 2131689669(0x7f0f00c5, float:1.900836E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            im.bclpbkiauv.phoneformat.PhoneFormat r8 = im.bclpbkiauv.phoneformat.PhoneFormat.getInstance()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "+"
            r9.append(r10)
            r9.append(r0)
            java.lang.String r9 = r9.toString()
            java.lang.String r8 = r8.format(r9)
            r6[r5] = r8
            java.lang.String r8 = "AddContactByPhone"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.formatString(r8, r7, r6)
            r4.setText(r6, r5)
            goto L_0x0427
        L_0x0059:
            android.view.View r0 = r2.itemView
            im.bclpbkiauv.ui.components.RecyclerListView r0 = (im.bclpbkiauv.ui.components.RecyclerListView) r0
            androidx.recyclerview.widget.RecyclerView$Adapter r4 = r0.getAdapter()
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter$CategoryAdapterRecycler r4 = (im.bclpbkiauv.ui.adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r4
            int r5 = r3 / 2
            r4.setIndex(r5)
            goto L_0x0427
        L_0x006a:
            android.view.View r0 = r2.itemView
            im.bclpbkiauv.ui.cells.HashtagSearchCell r0 = (im.bclpbkiauv.ui.cells.HashtagSearchCell) r0
            java.util.ArrayList<java.lang.String> r4 = r1.searchResultHashtags
            int r7 = r3 + -1
            java.lang.Object r4 = r4.get(r7)
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4
            r0.setText(r4)
            java.util.ArrayList<java.lang.String> r4 = r1.searchResultHashtags
            int r4 = r4.size()
            if (r3 == r4) goto L_0x0084
            r5 = 1
        L_0x0084:
            r0.setNeedDivider(r5)
            goto L_0x0427
        L_0x0089:
            android.view.View r0 = r2.itemView
            im.bclpbkiauv.ui.cells.DialogCell r0 = (im.bclpbkiauv.ui.cells.DialogCell) r0
            int r4 = r28.getItemCount()
            int r4 = r4 - r6
            if (r3 == r4) goto L_0x0095
            r5 = 1
        L_0x0095:
            r0.useSeparator = r5
            java.lang.Object r4 = r1.getItem(r3)
            im.bclpbkiauv.messenger.MessageObject r4 = (im.bclpbkiauv.messenger.MessageObject) r4
            long r5 = r4.getDialogId()
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r4.messageOwner
            int r7 = r7.date
            r0.setDialog((long) r5, (im.bclpbkiauv.messenger.MessageObject) r4, (int) r7)
            goto L_0x0427
        L_0x00aa:
            android.view.View r0 = r2.itemView
            im.bclpbkiauv.ui.cells.GraySectionCell r0 = (im.bclpbkiauv.ui.cells.GraySectionCell) r0
            boolean r7 = r28.isRecentSearchDisplayed()
            r8 = 2131690610(0x7f0f0472, float:1.9010268E38)
            java.lang.String r9 = "ClearButton"
            if (r7 == 0) goto L_0x00ef
            int r6 = r1.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r6 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r6)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_topPeer> r6 = r6.hints
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x00c8
            goto L_0x00c9
        L_0x00c8:
            r4 = 0
        L_0x00c9:
            if (r3 >= r4) goto L_0x00d8
            r5 = 2131690513(0x7f0f0411, float:1.9010072E38)
            java.lang.String r6 = "ChatHints"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
            r0.setText(r5)
            goto L_0x00ed
        L_0x00d8:
            r5 = 2131693302(0x7f0f0ef6, float:1.9015729E38)
            java.lang.String r6 = "Recent"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
            im.bclpbkiauv.ui.adapters.-$$Lambda$DialogsSearchAdapter$tjqY0Bld-0wNTjT6MOsJnqiFSaU r7 = new im.bclpbkiauv.ui.adapters.-$$Lambda$DialogsSearchAdapter$tjqY0Bld-0wNTjT6MOsJnqiFSaU
            r7.<init>()
            r0.setText(r5, r6, r7)
        L_0x00ed:
            goto L_0x0427
        L_0x00ef:
            java.util.ArrayList<java.lang.String> r4 = r1.searchResultHashtags
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x010e
            r4 = 2131691569(0x7f0f0831, float:1.9012214E38)
            java.lang.String r5 = "Hashtags"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
            im.bclpbkiauv.ui.adapters.-$$Lambda$DialogsSearchAdapter$xFr3gTC_eVs9QJ7j3wYuePAlZiQ r6 = new im.bclpbkiauv.ui.adapters.-$$Lambda$DialogsSearchAdapter$xFr3gTC_eVs9QJ7j3wYuePAlZiQ
            r6.<init>()
            r0.setText(r4, r5, r6)
            goto L_0x0427
        L_0x010e:
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r4 = r1.searchAdapterHelper
            java.util.ArrayList r4 = r4.getGlobalSearch()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r7 = r1.searchResult
            int r7 = r7.size()
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r8 = r1.searchAdapterHelper
            java.util.ArrayList r8 = r8.getLocalServerSearch()
            int r8 = r8.size()
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r9 = r1.searchAdapterHelper
            java.util.ArrayList r9 = r9.getPhoneSearch()
            int r9 = r9.size()
            boolean r10 = r4.isEmpty()
            if (r10 == 0) goto L_0x0136
            r10 = 0
            goto L_0x013b
        L_0x0136:
            int r10 = r4.size()
            int r10 = r10 + r6
        L_0x013b:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r11 = r1.searchResultMessages
            boolean r11 = r11.isEmpty()
            if (r11 == 0) goto L_0x0144
            goto L_0x014b
        L_0x0144:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r5 = r1.searchResultMessages
            int r5 = r5.size()
            int r5 = r5 + r6
        L_0x014b:
            int r6 = r7 + r8
            int r3 = r3 - r6
            if (r3 < 0) goto L_0x015f
            if (r3 >= r9) goto L_0x015f
            r6 = 2131694568(0x7f0f13e8, float:1.9018296E38)
            java.lang.String r11 = "PhoneNumberSearch"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r6)
            r0.setText(r6)
            goto L_0x017d
        L_0x015f:
            int r3 = r3 - r9
            if (r3 < 0) goto L_0x0171
            if (r3 >= r10) goto L_0x0171
            r6 = 2131691482(0x7f0f07da, float:1.9012037E38)
            java.lang.String r11 = "GlobalSearch"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r6)
            r0.setText(r6)
            goto L_0x017d
        L_0x0171:
            r6 = 2131693732(0x7f0f10a4, float:1.90166E38)
            java.lang.String r11 = "SearchMessages"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r6)
            r0.setText(r6)
        L_0x017d:
            goto L_0x0429
        L_0x017f:
            android.view.View r0 = r2.itemView
            r14 = r0
            im.bclpbkiauv.ui.cells.ProfileSearchCell r14 = (im.bclpbkiauv.ui.cells.ProfileSearchCell) r14
            r0 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            java.lang.Object r15 = r1.getItem(r3)
            boolean r13 = r15 instanceof im.bclpbkiauv.tgnet.TLRPC.User
            if (r13 == 0) goto L_0x019d
            r0 = r15
            im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
            java.lang.String r12 = r0.username
            r4 = r0
            r13 = r7
            r17 = r8
            goto L_0x01f3
        L_0x019d:
            boolean r13 = r15 instanceof im.bclpbkiauv.tgnet.TLRPC.Chat
            if (r13 == 0) goto L_0x01c3
            int r13 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r13 = im.bclpbkiauv.messenger.MessagesController.getInstance(r13)
            r4 = r15
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r4
            int r4 = r4.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r13.getChat(r4)
            if (r4 != 0) goto L_0x01bb
            r4 = r15
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r4
            r7 = r4
            goto L_0x01bc
        L_0x01bb:
            r7 = r4
        L_0x01bc:
            java.lang.String r12 = r7.username
            r4 = r0
            r13 = r7
            r17 = r8
            goto L_0x01f3
        L_0x01c3:
            boolean r4 = r15 instanceof im.bclpbkiauv.tgnet.TLRPC.EncryptedChat
            if (r4 == 0) goto L_0x01ef
            int r4 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            r13 = r15
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r13 = (im.bclpbkiauv.tgnet.TLRPC.EncryptedChat) r13
            int r13 = r13.id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r8 = r4.getEncryptedChat(r13)
            int r4 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            int r13 = r8.user_id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r4.getUser(r13)
            r4 = r0
            r13 = r7
            r17 = r8
            goto L_0x01f3
        L_0x01ef:
            r4 = r0
            r13 = r7
            r17 = r8
        L_0x01f3:
            boolean r0 = r28.isRecentSearchDisplayed()
            if (r0 == 0) goto L_0x0209
            r11 = 1
            int r0 = r28.getItemCount()
            int r0 = r0 - r6
            if (r3 == r0) goto L_0x0203
            r0 = 1
            goto L_0x0204
        L_0x0203:
            r0 = 0
        L_0x0204:
            r14.useSeparator = r0
            r0 = r11
            goto L_0x03b0
        L_0x0209:
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.util.ArrayList r7 = r0.getGlobalSearch()
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.util.ArrayList r8 = r0.getPhoneSearch()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r0 = r1.searchResult
            int r18 = r0.size()
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.util.ArrayList r0 = r0.getLocalServerSearch()
            int r19 = r0.size()
            int r20 = r8.size()
            r0 = r20
            if (r20 <= 0) goto L_0x023b
            int r5 = r20 + -1
            java.lang.Object r5 = r8.get(r5)
            boolean r5 = r5 instanceof java.lang.String
            if (r5 == 0) goto L_0x023b
            int r0 = r0 + -2
            r5 = r0
            goto L_0x023c
        L_0x023b:
            r5 = r0
        L_0x023c:
            boolean r0 = r7.isEmpty()
            if (r0 == 0) goto L_0x0244
            r0 = 0
            goto L_0x0249
        L_0x0244:
            int r0 = r7.size()
            int r0 = r0 + r6
        L_0x0249:
            r21 = r0
            int r0 = r28.getItemCount()
            int r0 = r0 - r6
            if (r3 == r0) goto L_0x0264
            int r0 = r18 + r5
            int r0 = r0 + r19
            int r0 = r0 - r6
            if (r3 == r0) goto L_0x0264
            int r0 = r18 + r21
            int r0 = r0 + r20
            int r0 = r0 + r19
            int r0 = r0 - r6
            if (r3 == r0) goto L_0x0264
            r0 = 1
            goto L_0x0265
        L_0x0264:
            r0 = 0
        L_0x0265:
            r14.useSeparator = r0
            int r0 = r28.getItemCount()
            java.lang.String r22 = "windowBackgroundWhite"
            r23 = 1084227584(0x40a00000, float:5.0)
            if (r0 != r6) goto L_0x0284
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            float r0 = (float) r0
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r22)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r0, r6)
            r14.setBackground(r0)
            r25 = r5
            goto L_0x02bf
        L_0x0284:
            if (r3 != 0) goto L_0x029f
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            float r6 = (float) r6
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            float r0 = (float) r0
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r22)
            r25 = r5
            r5 = 0
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r6, r0, r5, r5, r2)
            r14.setBackground(r0)
            goto L_0x02bf
        L_0x029f:
            r25 = r5
            int r0 = r28.getItemCount()
            r2 = 1
            int r0 = r0 - r2
            if (r3 != r0) goto L_0x02bf
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            float r0 = (float) r0
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            float r2 = (float) r2
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r22)
            r6 = 0
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r6, r6, r0, r2, r5)
            r14.setBackground(r0)
        L_0x02bf:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r0 = r1.searchResult
            int r0 = r0.size()
            java.lang.String r2 = "@"
            if (r3 >= r0) goto L_0x0302
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.searchResultNames
            java.lang.Object r0 = r0.get(r3)
            r10 = r0
            java.lang.CharSequence r10 = (java.lang.CharSequence) r10
            if (r10 == 0) goto L_0x03af
            if (r4 == 0) goto L_0x03af
            java.lang.String r0 = r4.username
            if (r0 == 0) goto L_0x03af
            java.lang.String r0 = r4.username
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x03af
            java.lang.String r0 = r10.toString()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            java.lang.String r2 = r4.username
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x03af
            r9 = r10
            r10 = 0
            r0 = r11
            goto L_0x03b0
        L_0x0302:
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.lang.String r0 = r0.getLastFoundUsername()
            boolean r5 = android.text.TextUtils.isEmpty(r0)
            if (r5 != 0) goto L_0x03ad
            r5 = 0
            r6 = 0
            if (r4 == 0) goto L_0x031d
            java.lang.String r3 = r4.first_name
            r22 = r5
            java.lang.String r5 = r4.last_name
            java.lang.String r5 = im.bclpbkiauv.messenger.ContactsController.formatName(r3, r5)
            goto L_0x0326
        L_0x031d:
            r22 = r5
            if (r13 == 0) goto L_0x0324
            java.lang.String r5 = r13.title
            goto L_0x0326
        L_0x0324:
            r5 = r22
        L_0x0326:
            java.lang.String r22 = "windowBackgroundWhiteBlueText4"
            r3 = -1
            if (r5 == 0) goto L_0x035e
            r24 = r6
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r5, r0)
            r26 = r6
            if (r6 == r3) goto L_0x0357
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r5)
            android.text.style.ForegroundColorSpan r3 = new android.text.style.ForegroundColorSpan
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r22)
            r3.<init>(r6)
            int r6 = r0.length()
            r27 = r5
            r5 = r26
            int r6 = r6 + r5
            r26 = r7
            r7 = 33
            r2.setSpan(r3, r5, r6, r7)
            r10 = r2
            r0 = r11
            goto L_0x03b0
        L_0x0357:
            r27 = r5
            r5 = r26
            r26 = r7
            goto L_0x0364
        L_0x035e:
            r27 = r5
            r24 = r6
            r26 = r7
        L_0x0364:
            if (r12 == 0) goto L_0x03af
            boolean r5 = r0.startsWith(r2)
            if (r5 == 0) goto L_0x0373
            r5 = 1
            java.lang.String r0 = r0.substring(r5)
            r5 = r0
            goto L_0x0374
        L_0x0373:
            r5 = r0
        L_0x0374:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x03a6 }
            r0.<init>()     // Catch:{ Exception -> 0x03a6 }
            r0.append(r2)     // Catch:{ Exception -> 0x03a6 }
            r0.append(r12)     // Catch:{ Exception -> 0x03a6 }
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r12, r5)     // Catch:{ Exception -> 0x03a6 }
            r6 = r2
            if (r2 == r3) goto L_0x03a3
            int r2 = r5.length()     // Catch:{ Exception -> 0x03a6 }
            if (r6 != 0) goto L_0x038f
            int r2 = r2 + 1
            goto L_0x0391
        L_0x038f:
            int r6 = r6 + 1
        L_0x0391:
            android.text.style.ForegroundColorSpan r3 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x03a6 }
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r22)     // Catch:{ Exception -> 0x03a6 }
            r3.<init>(r7)     // Catch:{ Exception -> 0x03a6 }
            int r7 = r6 + r2
            r22 = r2
            r2 = 33
            r0.setSpan(r3, r6, r7, r2)     // Catch:{ Exception -> 0x03a6 }
        L_0x03a3:
            r9 = r0
            r0 = r11
            goto L_0x03b0
        L_0x03a6:
            r0 = move-exception
            r9 = r12
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r11
            goto L_0x03b0
        L_0x03ad:
            r26 = r7
        L_0x03af:
            r0 = r11
        L_0x03b0:
            r2 = 0
            if (r4 == 0) goto L_0x03c6
            int r3 = r4.id
            int r5 = r1.selfUserId
            if (r3 != r5) goto L_0x03c6
            r3 = 2131693693(0x7f0f107d, float:1.9016522E38)
            java.lang.String r5 = "SavedMessages"
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r9 = 0
            r2 = 1
            r3 = r10
            goto L_0x03c7
        L_0x03c6:
            r3 = r10
        L_0x03c7:
            if (r13 == 0) goto L_0x0413
            int r5 = r13.participants_count
            if (r5 == 0) goto L_0x0413
            boolean r5 = im.bclpbkiauv.messenger.ChatObject.isChannel(r13)
            if (r5 == 0) goto L_0x03e0
            boolean r5 = r13.megagroup
            if (r5 != 0) goto L_0x03e0
            int r5 = r13.participants_count
            java.lang.String r6 = "Subscribers"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r6, r5)
            goto L_0x03e8
        L_0x03e0:
            int r5 = r13.participants_count
            java.lang.String r6 = "Members"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r6, r5)
        L_0x03e8:
            boolean r6 = r9 instanceof android.text.SpannableStringBuilder
            java.lang.String r7 = ", "
            if (r6 == 0) goto L_0x03f9
            r6 = r9
            android.text.SpannableStringBuilder r6 = (android.text.SpannableStringBuilder) r6
            android.text.SpannableStringBuilder r6 = r6.append(r7)
            r6.append(r5)
            goto L_0x0413
        L_0x03f9:
            boolean r6 = android.text.TextUtils.isEmpty(r9)
            if (r6 != 0) goto L_0x0411
            r6 = 3
            java.lang.CharSequence[] r6 = new java.lang.CharSequence[r6]
            r8 = 0
            r6[r8] = r9
            r8 = 1
            r6[r8] = r7
            r7 = 2
            r6[r7] = r5
            java.lang.CharSequence r9 = android.text.TextUtils.concat(r6)
            r5 = r9
            goto L_0x0414
        L_0x0411:
            r9 = r5
            goto L_0x0414
        L_0x0413:
            r5 = r9
        L_0x0414:
            if (r4 == 0) goto L_0x0418
            r8 = r4
            goto L_0x0419
        L_0x0418:
            r8 = r13
        L_0x0419:
            r7 = r14
            r9 = r17
            r10 = r3
            r11 = r5
            r6 = r12
            r12 = r0
            r16 = r13
            r13 = r2
            r7.setData(r8, r9, r10, r11, r12, r13)
        L_0x0427:
            r3 = r30
        L_0x0429:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.adapters.DialogsSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    public /* synthetic */ void lambda$onBindViewHolder$12$DialogsSearchAdapter(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$13$DialogsSearchAdapter(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    public int getItemViewType(int i) {
        int i2 = 2;
        if (isRecentSearchDisplayed()) {
            if (MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) {
                i2 = 0;
            }
            int offset = i2;
            if (i > offset) {
                return 0;
            }
            if (i == offset || i % 2 == 0) {
                return 1;
            }
            return 5;
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            int localCount = this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
            int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            int messagesCount = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            if (i >= 0 && i < localCount) {
                return 0;
            }
            int i3 = i - localCount;
            if (i3 >= 0 && i3 < localServerCount) {
                return 0;
            }
            int i4 = i3 - localServerCount;
            if (i4 < 0 || i4 >= phoneCount) {
                int i5 = i4 - phoneCount;
                if (i5 >= 0 && i5 < globalCount) {
                    return i5 == 0 ? 1 : 0;
                }
                int i6 = i5 - globalCount;
                if (i6 < 0 || i6 >= messagesCount) {
                    return 3;
                }
                return i6 == 0 ? 1 : 2;
            }
            Object object = getItem(i4);
            if (!(object instanceof String)) {
                return 0;
            }
            if ("section".equals((String) object)) {
                return 1;
            }
            return 6;
        } else if (i == 0) {
            return 1;
        } else {
            return 4;
        }
    }
}
