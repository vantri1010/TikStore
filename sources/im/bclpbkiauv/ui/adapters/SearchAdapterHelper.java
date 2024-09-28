package im.bclpbkiauv.ui.adapters;

import android.util.SparseArray;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.sqlite.SQLiteCursor;
import im.bclpbkiauv.sqlite.SQLiteDatabase;
import im.bclpbkiauv.sqlite.SQLitePreparedStatement;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Marker;

public class SearchAdapterHelper {
    private boolean allResultsAreGlobal;
    private int channelLastReqId;
    private int channelReqId = 0;
    private int currentAccount = UserConfig.selectedAccount;
    private SearchAdapterHelperDelegate delegate;
    private ArrayList<TLObject> globalSearch = new ArrayList<>();
    private SparseArray<TLObject> globalSearchMap = new SparseArray<>();
    private ArrayList<TLObject> groupSearch = new ArrayList<>();
    private SparseArray<TLObject> groupSearchMap = new SparseArray<>();
    private ArrayList<HashtagObject> hashtags;
    private HashMap<String, HashtagObject> hashtagsByText;
    private boolean hashtagsLoadedFromDb = false;
    private String lastFoundChannel;
    private String lastFoundUsername = null;
    private int lastReqId;
    private ArrayList<TLObject> localSearchResults;
    private ArrayList<TLObject> localServerSearch = new ArrayList<>();
    private SparseArray<TLObject> phoneSearchMap = new SparseArray<>();
    private ArrayList<Object> phonesSearch = new ArrayList<>();
    private int reqId = 0;

    public static class HashtagObject {
        int date;
        String hashtag;
    }

    public interface SearchAdapterHelperDelegate {
        SparseArray<TLRPC.User> getExcludeUsers();

        void onDataSetChanged();

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);

        /* renamed from: im.bclpbkiauv.ui.adapters.SearchAdapterHelper$SearchAdapterHelperDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSetHashtags(SearchAdapterHelperDelegate _this, ArrayList arrayList, HashMap hashMap) {
            }

            public static SparseArray $default$getExcludeUsers(SearchAdapterHelperDelegate _this) {
                return null;
            }
        }
    }

    protected static final class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        protected DialogSearchResult() {
        }
    }

    public SearchAdapterHelper(boolean global) {
        this.allResultsAreGlobal = global;
    }

    public boolean isSearchInProgress() {
        return (this.reqId == 0 && this.channelReqId == 0) ? false : true;
    }

    public void queryServerSearch(String query, boolean allowUsername, boolean allowChats, boolean allowBots, boolean allowSelf, int channelId, boolean phoneNumbers, int type) {
        String str = query;
        int i = channelId;
        int i2 = type;
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.channelReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.channelReqId, true);
            this.channelReqId = 0;
        }
        if (str == null) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            this.lastReqId = 0;
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged();
            return;
        }
        if (query.length() <= 0) {
            boolean z = allowSelf;
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged();
        } else if (i != 0) {
            TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
            if (i2 == 1) {
                req.filter = new TLRPC.TL_channelParticipantsAdmins();
            } else if (i2 == 3) {
                req.filter = new TLRPC.TL_channelParticipantsBanned();
            } else if (i2 == 0) {
                req.filter = new TLRPC.TL_channelParticipantsKicked();
            } else {
                req.filter = new TLRPC.TL_channelParticipantsSearch();
            }
            req.filter.q = str;
            req.limit = 50;
            req.offset = 0;
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(i);
            int currentReqId = this.channelLastReqId + 1;
            this.channelLastReqId = currentReqId;
            this.channelReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(currentReqId, str, allowSelf) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SearchAdapterHelper.this.lambda$queryServerSearch$1$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            }, 2);
        } else {
            boolean z2 = allowSelf;
            this.lastFoundChannel = query.toLowerCase();
        }
        if (allowUsername) {
            if (query.length() > 0) {
                TLRPC.TL_contacts_search req2 = new TLRPC.TL_contacts_search();
                req2.q = str;
                req2.limit = 50;
                int currentReqId2 = this.lastReqId + 1;
                this.lastReqId = currentReqId2;
                $$Lambda$SearchAdapterHelper$7h1KLHNRjppe_59g0CCmw_IU2EM r11 = r0;
                $$Lambda$SearchAdapterHelper$7h1KLHNRjppe_59g0CCmw_IU2EM r0 = new RequestDelegate(currentReqId2, allowChats, allowBots, allowSelf, query) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ boolean f$3;
                    private final /* synthetic */ boolean f$4;
                    private final /* synthetic */ String f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SearchAdapterHelper.this.lambda$queryServerSearch$3$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
                    }
                };
                this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, r11, 2);
            } else {
                this.globalSearch.clear();
                this.globalSearchMap.clear();
                this.localServerSearch.clear();
                this.lastReqId = 0;
                this.delegate.onDataSetChanged();
            }
        }
        if (phoneNumbers && str.startsWith(Marker.ANY_NON_NULL_MARKER) && query.length() > 3) {
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            String phone = PhoneFormat.stripExceptNumbers(query);
            ArrayList<TLRPC.Contact> arrayList = ContactsController.getInstance(this.currentAccount).contacts;
            boolean hasFullMatch = false;
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arrayList.get(a).user_id));
                if (!(user == null || user.phone == null || !user.phone.startsWith(phone))) {
                    if (!hasFullMatch) {
                        hasFullMatch = user.phone.length() == phone.length();
                    }
                    this.phonesSearch.add(user);
                    this.phoneSearchMap.put(user.id, user);
                }
            }
            if (!hasFullMatch) {
                this.phonesSearch.add("section");
                this.phonesSearch.add(phone);
            }
            this.delegate.onDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$queryServerSearch$1$SearchAdapterHelper(int currentReqId, String query, boolean allowSelf, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(currentReqId, error, response, query, allowSelf) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ String f$4;
            private final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                SearchAdapterHelper.this.lambda$null$0$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$SearchAdapterHelper(int currentReqId, TLRPC.TL_error error, TLObject response, String query, boolean allowSelf) {
        if (currentReqId == this.channelLastReqId && error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            this.lastFoundChannel = query.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.groupSearch.addAll(res.participants);
            int currentUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int N = res.participants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.ChannelParticipant participant = (TLRPC.ChannelParticipant) res.participants.get(a);
                if (allowSelf || participant.user_id != currentUserId) {
                    this.groupSearchMap.put(participant.user_id, participant);
                } else {
                    this.groupSearch.remove(participant);
                }
            }
            ArrayList<TLObject> arrayList = this.localSearchResults;
            if (arrayList != null) {
                mergeResults(arrayList);
            }
            this.delegate.onDataSetChanged();
        }
        this.channelReqId = 0;
    }

    public /* synthetic */ void lambda$queryServerSearch$3$SearchAdapterHelper(int currentReqId, boolean allowChats, boolean allowBots, boolean allowSelf, String query, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(currentReqId, error, response, allowChats, allowBots, allowSelf, query) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ boolean f$6;
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
                SearchAdapterHelper.this.lambda$null$2$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$2$SearchAdapterHelper(int r14, im.bclpbkiauv.tgnet.TLRPC.TL_error r15, im.bclpbkiauv.tgnet.TLObject r16, boolean r17, boolean r18, boolean r19, java.lang.String r20) {
        /*
            r13 = this;
            r0 = r13
            int r1 = r0.lastReqId
            r2 = r14
            if (r2 != r1) goto L_0x0174
            r1 = 0
            r0.reqId = r1
            if (r15 != 0) goto L_0x0174
            r3 = r16
            im.bclpbkiauv.tgnet.TLRPC$TL_contacts_found r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_contacts_found) r3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r4 = r0.globalSearch
            r4.clear()
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLObject> r4 = r0.globalSearchMap
            r4.clear()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r4 = r0.localServerSearch
            r4.clear()
            int r4 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r5 = r3.chats
            r4.putChats(r5, r1)
            int r4 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r5 = r3.users
            r4.putUsers(r5, r1)
            int r1 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesStorage r1 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r1)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r4 = r3.users
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r5 = r3.chats
            r6 = 1
            r1.putUsersAndChats(r4, r5, r6, r6)
            android.util.SparseArray r1 = new android.util.SparseArray
            r1.<init>()
            android.util.SparseArray r4 = new android.util.SparseArray
            r4.<init>()
            r5 = 0
        L_0x004d:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r6 = r3.chats
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x0065
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r6 = r3.chats
            java.lang.Object r6 = r6.get(r5)
            im.bclpbkiauv.tgnet.TLRPC$Chat r6 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r6
            int r7 = r6.id
            r1.put(r7, r6)
            int r5 = r5 + 1
            goto L_0x004d
        L_0x0065:
            r5 = 0
        L_0x0066:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r6 = r3.users
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x007e
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r6 = r3.users
            java.lang.Object r6 = r6.get(r5)
            im.bclpbkiauv.tgnet.TLRPC$User r6 = (im.bclpbkiauv.tgnet.TLRPC.User) r6
            int r7 = r6.id
            r4.put(r7, r6)
            int r5 = r5 + 1
            goto L_0x0066
        L_0x007e:
            r5 = 0
        L_0x007f:
            r6 = 2
            if (r5 >= r6) goto L_0x00fb
            if (r5 != 0) goto L_0x008d
            boolean r6 = r0.allResultsAreGlobal
            if (r6 != 0) goto L_0x008a
            goto L_0x00f8
        L_0x008a:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Peer> r6 = r3.my_results
            goto L_0x008f
        L_0x008d:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Peer> r6 = r3.results
        L_0x008f:
            r7 = 0
        L_0x0090:
            int r8 = r6.size()
            if (r7 >= r8) goto L_0x00f8
            java.lang.Object r8 = r6.get(r7)
            im.bclpbkiauv.tgnet.TLRPC$Peer r8 = (im.bclpbkiauv.tgnet.TLRPC.Peer) r8
            r9 = 0
            r10 = 0
            int r11 = r8.user_id
            if (r11 == 0) goto L_0x00ac
            int r11 = r8.user_id
            java.lang.Object r11 = r4.get(r11)
            r9 = r11
            im.bclpbkiauv.tgnet.TLRPC$User r9 = (im.bclpbkiauv.tgnet.TLRPC.User) r9
            goto L_0x00c7
        L_0x00ac:
            int r11 = r8.chat_id
            if (r11 == 0) goto L_0x00ba
            int r11 = r8.chat_id
            java.lang.Object r11 = r1.get(r11)
            r10 = r11
            im.bclpbkiauv.tgnet.TLRPC$Chat r10 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r10
            goto L_0x00c7
        L_0x00ba:
            int r11 = r8.channel_id
            if (r11 == 0) goto L_0x00c7
            int r11 = r8.channel_id
            java.lang.Object r11 = r1.get(r11)
            r10 = r11
            im.bclpbkiauv.tgnet.TLRPC$Chat r10 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r10
        L_0x00c7:
            if (r10 == 0) goto L_0x00da
            if (r17 != 0) goto L_0x00cc
            goto L_0x00f5
        L_0x00cc:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r11 = r0.globalSearch
            r11.add(r10)
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLObject> r11 = r0.globalSearchMap
            int r12 = r10.id
            int r12 = -r12
            r11.put(r12, r10)
            goto L_0x00f5
        L_0x00da:
            if (r9 == 0) goto L_0x00f5
            if (r18 != 0) goto L_0x00e2
            boolean r11 = r9.bot
            if (r11 != 0) goto L_0x00f5
        L_0x00e2:
            if (r19 != 0) goto L_0x00e9
            boolean r11 = r9.self
            if (r11 == 0) goto L_0x00e9
            goto L_0x00f5
        L_0x00e9:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r11 = r0.globalSearch
            r11.add(r9)
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLObject> r11 = r0.globalSearchMap
            int r12 = r9.id
            r11.put(r12, r9)
        L_0x00f5:
            int r7 = r7 + 1
            goto L_0x0090
        L_0x00f8:
            int r5 = r5 + 1
            goto L_0x007f
        L_0x00fb:
            boolean r5 = r0.allResultsAreGlobal
            if (r5 != 0) goto L_0x015f
            r5 = 0
        L_0x0100:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Peer> r6 = r3.my_results
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x015f
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Peer> r6 = r3.my_results
            java.lang.Object r6 = r6.get(r5)
            im.bclpbkiauv.tgnet.TLRPC$Peer r6 = (im.bclpbkiauv.tgnet.TLRPC.Peer) r6
            r7 = 0
            r8 = 0
            int r9 = r6.user_id
            if (r9 == 0) goto L_0x0120
            int r9 = r6.user_id
            java.lang.Object r9 = r4.get(r9)
            r7 = r9
            im.bclpbkiauv.tgnet.TLRPC$User r7 = (im.bclpbkiauv.tgnet.TLRPC.User) r7
            goto L_0x013b
        L_0x0120:
            int r9 = r6.chat_id
            if (r9 == 0) goto L_0x012e
            int r9 = r6.chat_id
            java.lang.Object r9 = r1.get(r9)
            r8 = r9
            im.bclpbkiauv.tgnet.TLRPC$Chat r8 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r8
            goto L_0x013b
        L_0x012e:
            int r9 = r6.channel_id
            if (r9 == 0) goto L_0x013b
            int r9 = r6.channel_id
            java.lang.Object r9 = r1.get(r9)
            r8 = r9
            im.bclpbkiauv.tgnet.TLRPC$Chat r8 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r8
        L_0x013b:
            if (r8 == 0) goto L_0x014e
            if (r17 != 0) goto L_0x0140
            goto L_0x015c
        L_0x0140:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r9 = r0.localServerSearch
            r9.add(r8)
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLObject> r9 = r0.globalSearchMap
            int r10 = r8.id
            int r10 = -r10
            r9.put(r10, r8)
            goto L_0x015c
        L_0x014e:
            if (r7 == 0) goto L_0x015c
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r9 = r0.localServerSearch
            r9.add(r7)
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLObject> r9 = r0.globalSearchMap
            int r10 = r7.id
            r9.put(r10, r7)
        L_0x015c:
            int r5 = r5 + 1
            goto L_0x0100
        L_0x015f:
            java.lang.String r5 = r20.toLowerCase()
            r0.lastFoundUsername = r5
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r5 = r0.localSearchResults
            if (r5 == 0) goto L_0x016c
            r13.mergeResults(r5)
        L_0x016c:
            r13.mergeExcludeResults()
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper$SearchAdapterHelperDelegate r5 = r0.delegate
            r5.onDataSetChanged()
        L_0x0174:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.adapters.SearchAdapterHelper.lambda$null$2$SearchAdapterHelper(int, im.bclpbkiauv.tgnet.TLRPC$TL_error, im.bclpbkiauv.tgnet.TLObject, boolean, boolean, boolean, java.lang.String):void");
    }

    public void unloadRecentHashtags() {
        this.hashtagsLoadedFromDb = false;
    }

    public boolean loadRecentHashtags() {
        if (this.hashtagsLoadedFromDb) {
            return true;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                SearchAdapterHelper.this.lambda$loadRecentHashtags$6$SearchAdapterHelper();
            }
        });
        return false;
    }

    public /* synthetic */ void lambda$loadRecentHashtags$6$SearchAdapterHelper() {
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
            ArrayList<HashtagObject> arrayList = new ArrayList<>();
            HashMap<String, HashtagObject> hashMap = new HashMap<>();
            while (cursor.next()) {
                HashtagObject hashtagObject = new HashtagObject();
                hashtagObject.hashtag = cursor.stringValue(0);
                hashtagObject.date = cursor.intValue(1);
                arrayList.add(hashtagObject);
                hashMap.put(hashtagObject.hashtag, hashtagObject);
            }
            cursor.dispose();
            Collections.sort(arrayList, $$Lambda$SearchAdapterHelper$1qBQsLEk_bUavW1b5lsa4HJYLRo.INSTANCE);
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, hashMap) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ HashMap f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SearchAdapterHelper.this.lambda$null$5$SearchAdapterHelper(this.f$1, this.f$2);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ int lambda$null$4(HashtagObject lhs, HashtagObject rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    public void mergeResults(ArrayList<TLObject> localResults) {
        TLRPC.Chat c;
        this.localSearchResults = localResults;
        if (this.globalSearchMap.size() != 0 && localResults != null) {
            int count = localResults.size();
            for (int a = 0; a < count; a++) {
                TLObject obj = localResults.get(a);
                if (obj instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) obj;
                    TLRPC.User u = (TLRPC.User) this.globalSearchMap.get(user.id);
                    if (u != null) {
                        this.globalSearch.remove(u);
                        this.localServerSearch.remove(u);
                        this.globalSearchMap.remove(u.id);
                    }
                    TLObject participant = this.groupSearchMap.get(user.id);
                    if (participant != null) {
                        this.groupSearch.remove(participant);
                        this.groupSearchMap.remove(user.id);
                    }
                    Object object = this.phoneSearchMap.get(user.id);
                    if (object != null) {
                        this.phonesSearch.remove(object);
                        this.phoneSearchMap.remove(user.id);
                    }
                } else if ((obj instanceof TLRPC.Chat) && (c = (TLRPC.Chat) this.globalSearchMap.get(-((TLRPC.Chat) obj).id)) != null) {
                    this.globalSearch.remove(c);
                    this.localServerSearch.remove(c);
                    this.globalSearchMap.remove(-c.id);
                }
            }
        }
    }

    public void mergeExcludeResults() {
        SparseArray<TLRPC.User> ignoreUsers;
        SearchAdapterHelperDelegate searchAdapterHelperDelegate = this.delegate;
        if (searchAdapterHelperDelegate != null && (ignoreUsers = searchAdapterHelperDelegate.getExcludeUsers()) != null) {
            int size = ignoreUsers.size();
            for (int a = 0; a < size; a++) {
                TLRPC.User u = (TLRPC.User) this.globalSearchMap.get(ignoreUsers.keyAt(a));
                if (u != null) {
                    this.globalSearch.remove(u);
                    this.localServerSearch.remove(u);
                    this.globalSearchMap.remove(u.id);
                }
            }
        }
    }

    public void setDelegate(SearchAdapterHelperDelegate searchAdapterHelperDelegate) {
        this.delegate = searchAdapterHelperDelegate;
    }

    public void addHashtagsFromMessage(CharSequence message) {
        if (message != null) {
            boolean changed = false;
            Matcher matcher = Pattern.compile("(^|\\s)#[\\w@.]+").matcher(message);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (!(message.charAt(start) == '@' || message.charAt(start) == '#')) {
                    start++;
                }
                String hashtag = message.subSequence(start, end).toString();
                if (this.hashtagsByText == null) {
                    this.hashtagsByText = new HashMap<>();
                    this.hashtags = new ArrayList<>();
                }
                HashtagObject hashtagObject = this.hashtagsByText.get(hashtag);
                if (hashtagObject == null) {
                    hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = hashtag;
                    this.hashtagsByText.put(hashtagObject.hashtag, hashtagObject);
                } else {
                    this.hashtags.remove(hashtagObject);
                }
                hashtagObject.date = (int) (System.currentTimeMillis() / 1000);
                this.hashtags.add(0, hashtagObject);
                changed = true;
            }
            if (changed) {
                putRecentHashtags(this.hashtags);
            }
        }
    }

    private void putRecentHashtags(ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SearchAdapterHelper.this.lambda$putRecentHashtags$7$SearchAdapterHelper(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$putRecentHashtags$7$SearchAdapterHelper(ArrayList arrayList) {
        SQLitePreparedStatement state = null;
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
        } catch (Exception e) {
            try {
                FileLog.e("putRecentHashtags ---> exception 1 ", (Throwable) e);
            } catch (Exception e2) {
                FileLog.e("putRecentHashtags ---> exception 3 ", (Throwable) e2);
                if (state == null) {
                    return;
                }
            } catch (Throwable th) {
                if (state != null) {
                    state.dispose();
                }
                throw th;
            }
        }
        SQLitePreparedStatement state2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
        int a = 0;
        while (true) {
            if (a >= arrayList.size()) {
                break;
            } else if (a == 100) {
                break;
            } else {
                HashtagObject hashtagObject = (HashtagObject) arrayList.get(a);
                state2.requery();
                state2.bindString(1, hashtagObject.hashtag);
                state2.bindInteger(2, hashtagObject.date);
                state2.step();
                a++;
            }
        }
        state2.dispose();
        state = null;
        MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
        if (arrayList.size() >= 100) {
            try {
                MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            } catch (Exception e3) {
                FileLog.e("putRecentHashtags ---> exception 2 ", (Throwable) e3);
            }
            for (int a2 = 100; a2 < arrayList.size(); a2++) {
                SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
                database.executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((HashtagObject) arrayList.get(a2)).hashtag + "'").stepThis().dispose();
            }
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
        }
        if (0 == 0) {
            return;
        }
        state.dispose();
    }

    public ArrayList<TLObject> getGlobalSearch() {
        return this.globalSearch;
    }

    public ArrayList<Object> getPhoneSearch() {
        return this.phonesSearch;
    }

    public ArrayList<TLObject> getLocalServerSearch() {
        return this.localServerSearch;
    }

    public ArrayList<TLObject> getGroupSearch() {
        return this.groupSearch;
    }

    public ArrayList<HashtagObject> getHashtags() {
        return this.hashtags;
    }

    public String getLastFoundUsername() {
        return this.lastFoundUsername;
    }

    public String getLastFoundChannel() {
        return this.lastFoundChannel;
    }

    public void clearRecentHashtags() {
        this.hashtags = new ArrayList<>();
        this.hashtagsByText = new HashMap<>();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                SearchAdapterHelper.this.lambda$clearRecentHashtags$8$SearchAdapterHelper();
            }
        });
    }

    public /* synthetic */ void lambda$clearRecentHashtags$8$SearchAdapterHelper() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: setHashtags */
    public void lambda$null$5$SearchAdapterHelper(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
