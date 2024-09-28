package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.SearchAdapterHelper;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.ProfileSearchCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SearchAdapter extends RecyclerListView.SelectionAdapter {
    private boolean allowBots;
    private boolean allowChats;
    private boolean allowPhoneNumbers;
    private boolean allowUsernameSearch;
    private int channelId;
    private SparseArray<?> checkedMap;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC.User> ignoreUsers;
    private Context mContext;
    private int miViewType = 0;
    private boolean onlyMutual;
    private SearchAdapterHelper searchAdapterHelper;
    private ArrayList<TLObject> searchResult = new ArrayList<>();
    private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
    /* access modifiers changed from: private */
    public Timer searchTimer;
    private boolean useUserCell;

    public SearchAdapter(Context context, SparseArray<TLRPC.User> arg1, boolean usernameSearch, boolean mutual, boolean chats, boolean bots, boolean phones, int searchChannelId) {
        this.mContext = context;
        this.ignoreUsers = arg1;
        this.onlyMutual = mutual;
        this.allowUsernameSearch = usernameSearch;
        this.allowChats = chats;
        this.allowBots = bots;
        this.channelId = searchChannelId;
        this.allowPhoneNumbers = phones;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public void onDataSetChanged() {
                SearchAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
            }

            public SparseArray<TLRPC.User> getExcludeUsers() {
                return SearchAdapter.this.ignoreUsers;
            }
        });
    }

    public void setCheckedMap(SparseArray<?> map) {
        this.checkedMap = map;
    }

    public void setUseUserCell(boolean value) {
        this.useUserCell = value;
    }

    public void searchDialogs(String query) {
        final String str = query;
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (str == null) {
            this.searchResult.clear();
            this.searchResultNames.clear();
            if (this.allowUsernameSearch) {
                this.searchAdapterHelper.queryServerSearch((String) null, true, this.allowChats, this.allowBots, true, this.channelId, this.allowPhoneNumbers, 0);
            }
            notifyDataSetChanged();
            return;
        }
        Timer timer = new Timer();
        this.searchTimer = timer;
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    SearchAdapter.this.searchTimer.cancel();
                    Timer unused = SearchAdapter.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                SearchAdapter.this.processSearch(str);
            }
        }, 200, 300);
    }

    public void setMiViewType(int miViewType2) {
        this.miViewType = miViewType2;
    }

    /* access modifiers changed from: private */
    public void processSearch(String query) {
        AndroidUtilities.runOnUIThread(new Runnable(query) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SearchAdapter.this.lambda$processSearch$1$SearchAdapter(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$processSearch$1$SearchAdapter(String query) {
        if (this.allowUsernameSearch) {
            this.searchAdapterHelper.queryServerSearch(query, true, this.allowChats, this.allowBots, true, this.channelId, this.allowPhoneNumbers, -1);
        }
        int currentAccount = UserConfig.selectedAccount;
        Utilities.searchQueue.postRunnable(new Runnable(query, new ArrayList<>(ContactsController.getInstance(currentAccount).contacts), currentAccount) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SearchAdapter.this.lambda$null$0$SearchAdapter(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x0119 A[LOOP:1: B:35:0x00b0->B:55:0x0119, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00dc A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$0$SearchAdapter(java.lang.String r18, java.util.ArrayList r19, int r20) {
        /*
            r17 = this;
            r0 = r17
            java.lang.String r1 = r18.trim()
            java.lang.String r1 = r1.toLowerCase()
            int r2 = r1.length()
            if (r2 != 0) goto L_0x001e
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0.updateSearchResults(r2, r3)
            return
        L_0x001e:
            im.bclpbkiauv.messenger.LocaleController r2 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            java.lang.String r2 = r2.getTranslitString(r1)
            boolean r3 = r1.equals(r2)
            if (r3 != 0) goto L_0x0032
            int r3 = r2.length()
            if (r3 != 0) goto L_0x0033
        L_0x0032:
            r2 = 0
        L_0x0033:
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x0039
            r5 = 1
            goto L_0x003a
        L_0x0039:
            r5 = 0
        L_0x003a:
            int r5 = r5 + r4
            java.lang.String[] r5 = new java.lang.String[r5]
            r5[r3] = r1
            if (r2 == 0) goto L_0x0043
            r5[r4] = r2
        L_0x0043:
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r8 = 0
        L_0x004e:
            int r9 = r19.size()
            if (r8 >= r9) goto L_0x012c
            r9 = r19
            java.lang.Object r10 = r9.get(r8)
            im.bclpbkiauv.tgnet.TLRPC$Contact r10 = (im.bclpbkiauv.tgnet.TLRPC.Contact) r10
            im.bclpbkiauv.messenger.MessagesController r11 = im.bclpbkiauv.messenger.MessagesController.getInstance(r20)
            int r12 = r10.user_id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            im.bclpbkiauv.tgnet.TLRPC$User r11 = r11.getUser(r12)
            int r12 = r11.id
            im.bclpbkiauv.messenger.UserConfig r13 = im.bclpbkiauv.messenger.UserConfig.getInstance(r20)
            int r13 = r13.getClientUserId()
            if (r12 == r13) goto L_0x0122
            boolean r12 = r0.onlyMutual
            if (r12 == 0) goto L_0x0083
            boolean r12 = r11.mutual_contact
            if (r12 == 0) goto L_0x007f
            goto L_0x0083
        L_0x007f:
            r16 = r1
            goto L_0x0124
        L_0x0083:
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$User> r12 = r0.ignoreUsers
            if (r12 == 0) goto L_0x0093
            int r13 = r10.user_id
            int r12 = r12.indexOfKey(r13)
            if (r12 < 0) goto L_0x0093
            r16 = r1
            goto L_0x0124
        L_0x0093:
            java.lang.String r12 = r11.first_name
            java.lang.String r13 = r11.last_name
            java.lang.String r12 = im.bclpbkiauv.messenger.ContactsController.formatName(r12, r13)
            java.lang.String r12 = r12.toLowerCase()
            im.bclpbkiauv.messenger.LocaleController r13 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            java.lang.String r13 = r13.getTranslitString(r12)
            boolean r14 = r12.equals(r13)
            if (r14 == 0) goto L_0x00ae
            r13 = 0
        L_0x00ae:
            r14 = 0
            int r15 = r5.length
        L_0x00b0:
            if (r3 >= r15) goto L_0x011f
            r4 = r5[r3]
            boolean r16 = r12.contains(r4)
            if (r16 != 0) goto L_0x00d6
            if (r13 == 0) goto L_0x00c5
            boolean r16 = r13.contains(r4)
            if (r16 == 0) goto L_0x00c5
            r16 = r1
            goto L_0x00d8
        L_0x00c5:
            r16 = r1
            java.lang.String r1 = r11.username
            if (r1 == 0) goto L_0x00da
            java.lang.String r1 = r11.username
            boolean r1 = r1.contains(r4)
            if (r1 == 0) goto L_0x00da
            r1 = 2
            r14 = r1
            goto L_0x00da
        L_0x00d6:
            r16 = r1
        L_0x00d8:
            r1 = 1
            r14 = r1
        L_0x00da:
            if (r14 == 0) goto L_0x0119
            r1 = 1
            if (r14 != r1) goto L_0x00eb
            java.lang.String r3 = r11.first_name
            java.lang.String r15 = r11.last_name
            java.lang.CharSequence r3 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r3, r15, r4)
            r7.add(r3)
            goto L_0x0115
        L_0x00eb:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r15 = "@"
            r3.append(r15)
            java.lang.String r1 = r11.username
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r15 = 0
            java.lang.CharSequence r1 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r1, r15, r3)
            r7.add(r1)
        L_0x0115:
            r6.add(r11)
            goto L_0x0124
        L_0x0119:
            int r3 = r3 + 1
            r1 = r16
            r4 = 1
            goto L_0x00b0
        L_0x011f:
            r16 = r1
            goto L_0x0124
        L_0x0122:
            r16 = r1
        L_0x0124:
            int r8 = r8 + 1
            r1 = r16
            r3 = 0
            r4 = 1
            goto L_0x004e
        L_0x012c:
            r0.updateSearchResults(r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.adapters.SearchAdapter.lambda$null$0$SearchAdapter(java.lang.String, java.util.ArrayList, int):void");
    }

    private void updateSearchResults(ArrayList<TLObject> users, ArrayList<CharSequence> names) {
        AndroidUtilities.runOnUIThread(new Runnable(users, names) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SearchAdapter.this.lambda$updateSearchResults$2$SearchAdapter(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$updateSearchResults$2$SearchAdapter(ArrayList users, ArrayList names) {
        this.searchResult = users;
        this.searchResultNames = names;
        this.searchAdapterHelper.mergeResults(users);
        notifyDataSetChanged();
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int type = holder.getItemViewType();
        return type == 0 || type == 2;
    }

    public int getItemCount() {
        int count = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        if (globalCount != 0) {
            count += globalCount + 1;
        }
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (phoneCount != 0) {
            return count + phoneCount;
        }
        return count;
    }

    public boolean isGlobalSearch(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (i >= 0 && i < localCount) {
            return false;
        }
        if ((i <= localCount || i >= localCount + phoneCount) && i > localCount + phoneCount && i <= globalCount + phoneCount + localCount) {
            return true;
        }
        return false;
    }

    public Object getItem(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (i >= 0 && i < localCount) {
            return this.searchResult.get(i);
        }
        int i2 = i - localCount;
        if (i2 >= 0 && i2 < phoneCount) {
            return this.searchAdapterHelper.getPhoneSearch().get(i2);
        }
        int i3 = i2 - phoneCount;
        if (i3 <= 0 || i3 > globalCount) {
            return null;
        }
        return this.searchAdapterHelper.getGlobalSearch().get(i3 - 1);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            if (this.useUserCell) {
                view = new UserCell(this.mContext, 1, 1, false);
                if (this.checkedMap != null) {
                    ((UserCell) view).setChecked(false, false);
                }
            } else {
                view = new ProfileSearchCell(this.mContext);
            }
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        } else if (viewType != 1) {
            view = new TextCell(this.mContext, 16);
        } else {
            view = new GraySectionCell(this.mContext);
        }
        return new RecyclerListView.Holder(view);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0167  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r20
            int r0 = r19.getItemViewType()
            r4 = 0
            r5 = 0
            r6 = 1
            if (r0 == 0) goto L_0x0076
            if (r0 == r6) goto L_0x0050
            r7 = 2
            if (r0 == r7) goto L_0x0016
            goto L_0x01f6
        L_0x0016:
            java.lang.Object r0 = r1.getItem(r3)
            java.lang.String r0 = (java.lang.String) r0
            android.view.View r7 = r2.itemView
            im.bclpbkiauv.ui.cells.TextCell r7 = (im.bclpbkiauv.ui.cells.TextCell) r7
            java.lang.String r8 = "windowBackgroundWhiteBlueText2"
            r7.setColors(r4, r8)
            r4 = 2131689669(0x7f0f00c5, float:1.900836E38)
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
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.formatString(r8, r4, r6)
            r7.setText(r4, r5)
            goto L_0x01f6
        L_0x0050:
            android.view.View r0 = r2.itemView
            im.bclpbkiauv.ui.cells.GraySectionCell r0 = (im.bclpbkiauv.ui.cells.GraySectionCell) r0
            java.lang.Object r4 = r1.getItem(r3)
            if (r4 != 0) goto L_0x0068
            r4 = 2131691482(0x7f0f07da, float:1.9012037E38)
            java.lang.String r5 = "GlobalSearch"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x01f6
        L_0x0068:
            r4 = 2131694568(0x7f0f13e8, float:1.9018296E38)
            java.lang.String r5 = "PhoneNumberSearch"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x01f6
        L_0x0076:
            java.lang.Object r0 = r1.getItem(r3)
            r14 = r0
            im.bclpbkiauv.tgnet.TLObject r14 = (im.bclpbkiauv.tgnet.TLObject) r14
            if (r14 == 0) goto L_0x01f6
            r0 = 0
            r7 = 0
            boolean r8 = r14 instanceof im.bclpbkiauv.tgnet.TLRPC.User
            if (r8 == 0) goto L_0x0092
            r8 = r14
            im.bclpbkiauv.tgnet.TLRPC$User r8 = (im.bclpbkiauv.tgnet.TLRPC.User) r8
            java.lang.String r7 = r8.username
            r8 = r14
            im.bclpbkiauv.tgnet.TLRPC$User r8 = (im.bclpbkiauv.tgnet.TLRPC.User) r8
            int r0 = r8.id
            r15 = r0
            r13 = r7
            goto L_0x00a5
        L_0x0092:
            boolean r8 = r14 instanceof im.bclpbkiauv.tgnet.TLRPC.Chat
            if (r8 == 0) goto L_0x00a3
            r8 = r14
            im.bclpbkiauv.tgnet.TLRPC$Chat r8 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r8
            java.lang.String r7 = r8.username
            r8 = r14
            im.bclpbkiauv.tgnet.TLRPC$Chat r8 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r8
            int r0 = r8.id
            r15 = r0
            r13 = r7
            goto L_0x00a5
        L_0x00a3:
            r15 = r0
            r13 = r7
        L_0x00a5:
            r7 = 0
            r8 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r0 = r1.searchResult
            int r0 = r0.size()
            java.lang.String r9 = "@"
            if (r3 >= r0) goto L_0x00e2
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.searchResultNames
            java.lang.Object r0 = r0.get(r3)
            r8 = r0
            java.lang.CharSequence r8 = (java.lang.CharSequence) r8
            if (r8 == 0) goto L_0x013a
            if (r13 == 0) goto L_0x013a
            int r0 = r13.length()
            if (r0 <= 0) goto L_0x013a
            java.lang.String r0 = r8.toString()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r9)
            r10.append(r13)
            java.lang.String r9 = r10.toString()
            boolean r0 = r0.startsWith(r9)
            if (r0 == 0) goto L_0x013a
            r7 = r8
            r8 = 0
            r0 = r7
            r4 = r8
            goto L_0x013c
        L_0x00e2:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r0 = r1.searchResult
            int r0 = r0.size()
            if (r3 <= r0) goto L_0x013a
            if (r13 == 0) goto L_0x013a
            im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.lang.String r0 = r0.getLastFoundUsername()
            boolean r10 = r0.startsWith(r9)
            if (r10 == 0) goto L_0x00fe
            java.lang.String r0 = r0.substring(r6)
            r10 = r0
            goto L_0x00ff
        L_0x00fe:
            r10 = r0
        L_0x00ff:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0132 }
            r0.<init>()     // Catch:{ Exception -> 0x0132 }
            r0.append(r9)     // Catch:{ Exception -> 0x0132 }
            r0.append(r13)     // Catch:{ Exception -> 0x0132 }
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r13, r10)     // Catch:{ Exception -> 0x0132 }
            r11 = r9
            r12 = -1
            if (r9 == r12) goto L_0x012f
            int r9 = r10.length()     // Catch:{ Exception -> 0x0132 }
            if (r11 != 0) goto L_0x011b
            int r9 = r9 + 1
            goto L_0x011d
        L_0x011b:
            int r11 = r11 + 1
        L_0x011d:
            android.text.style.ForegroundColorSpan r12 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0132 }
            java.lang.String r16 = "windowBackgroundWhiteBlueText4"
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)     // Catch:{ Exception -> 0x0132 }
            r12.<init>(r6)     // Catch:{ Exception -> 0x0132 }
            int r6 = r11 + r9
            r4 = 33
            r0.setSpan(r12, r11, r6, r4)     // Catch:{ Exception -> 0x0132 }
        L_0x012f:
            r7 = r0
            r4 = r8
            goto L_0x013c
        L_0x0132:
            r0 = move-exception
            r7 = r13
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r7
            r4 = r8
            goto L_0x013c
        L_0x013a:
            r0 = r7
            r4 = r8
        L_0x013c:
            boolean r6 = r1.useUserCell
            if (r6 == 0) goto L_0x0167
            android.view.View r6 = r2.itemView
            im.bclpbkiauv.ui.cells.UserCell r6 = (im.bclpbkiauv.ui.cells.UserCell) r6
            int r7 = r1.miViewType
            r6.setMiViewType(r7)
            int r7 = r1.miViewType
            if (r7 != 0) goto L_0x0151
            r6.setData(r14, r4, r0, r5)
            goto L_0x0155
        L_0x0151:
            r7 = 0
            r6.setData(r14, r4, r7, r5)
        L_0x0155:
            android.util.SparseArray<?> r7 = r1.checkedMap
            if (r7 == 0) goto L_0x0165
            int r7 = r7.indexOfKey(r15)
            if (r7 < 0) goto L_0x0161
            r7 = 1
            goto L_0x0162
        L_0x0161:
            r7 = 0
        L_0x0162:
            r6.setChecked(r7, r5)
        L_0x0165:
            goto L_0x01f6
        L_0x0167:
            android.view.View r6 = r2.itemView
            im.bclpbkiauv.ui.cells.ProfileSearchCell r6 = (im.bclpbkiauv.ui.cells.ProfileSearchCell) r6
            int r7 = r1.miViewType
            r6.setMiViewType(r7)
            int r7 = r1.miViewType
            if (r7 != 0) goto L_0x0184
            r9 = 0
            r12 = 0
            r16 = 0
            r7 = r6
            r8 = r14
            r10 = r4
            r11 = r0
            r17 = r13
            r13 = r16
            r7.setData(r8, r9, r10, r11, r12, r13)
            goto L_0x0190
        L_0x0184:
            r17 = r13
            r9 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r7 = r6
            r8 = r14
            r10 = r4
            r7.setData(r8, r9, r10, r11, r12, r13)
        L_0x0190:
            int r7 = r18.getItemCount()
            r8 = 1
            int r7 = r7 - r8
            if (r3 == r7) goto L_0x01a2
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r7 = r1.searchResult
            int r7 = r7.size()
            int r7 = r7 - r8
            if (r3 == r7) goto L_0x01a2
            r5 = 1
        L_0x01a2:
            r6.useSeparator = r5
            int r5 = r18.getItemCount()
            java.lang.String r7 = "windowBackgroundWhite"
            r8 = 1084227584(0x40a00000, float:5.0)
            r9 = 1
            if (r5 != r9) goto L_0x01c0
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r5 = (float) r5
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r5, r7)
            r6.setBackground(r5)
            goto L_0x01f6
        L_0x01c0:
            r5 = 0
            if (r3 != 0) goto L_0x01d9
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r9 = (float) r9
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r9, r8, r5, r5, r7)
            r6.setBackground(r5)
            goto L_0x01f6
        L_0x01d9:
            int r9 = r18.getItemCount()
            r10 = 1
            int r9 = r9 - r10
            if (r3 != r9) goto L_0x01f6
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r9 = (float) r9
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r5, r5, r9, r8, r7)
            r6.setBackground(r5)
        L_0x01f6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.adapters.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    public int getItemViewType(int i) {
        Object item = getItem(i);
        if (item == null) {
            return 1;
        }
        if (!(item instanceof String)) {
            return 0;
        }
        if ("section".equals((String) item)) {
            return 1;
        }
        return 2;
    }
}
