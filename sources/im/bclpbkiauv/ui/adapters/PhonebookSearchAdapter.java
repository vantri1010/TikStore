package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Marker;

public class PhonebookSearchAdapter extends RecyclerListView.SelectionAdapter {
    private Context mContext;
    private ArrayList<Object> searchResult = new ArrayList<>();
    private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
    /* access modifiers changed from: private */
    public Timer searchTimer;

    public PhonebookSearchAdapter(Context context) {
        this.mContext = context;
    }

    public void search(final String query) {
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (query == null) {
            this.searchResult.clear();
            this.searchResultNames.clear();
            notifyDataSetChanged();
            return;
        }
        Timer timer = new Timer();
        this.searchTimer = timer;
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    PhonebookSearchAdapter.this.searchTimer.cancel();
                    Timer unused = PhonebookSearchAdapter.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                PhonebookSearchAdapter.this.processSearch(query);
            }
        }, 200, 300);
    }

    /* access modifiers changed from: private */
    public void processSearch(String query) {
        AndroidUtilities.runOnUIThread(new Runnable(query) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PhonebookSearchAdapter.this.lambda$processSearch$1$PhonebookSearchAdapter(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$processSearch$1$PhonebookSearchAdapter(String query) {
        int currentAccount = UserConfig.selectedAccount;
        Utilities.searchQueue.postRunnable(new Runnable(query, new ArrayList<>(ContactsController.getInstance(currentAccount).contactsBook.values()), new ArrayList<>(ContactsController.getInstance(currentAccount).contacts), currentAccount) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                PhonebookSearchAdapter.this.lambda$null$0$PhonebookSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00cc, code lost:
        if (r2.contains(" " + r3) == false) goto L_0x00ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00e9, code lost:
        if (r5.contains(" " + r3) != false) goto L_0x00eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0139, code lost:
        if (r12.contains(" " + r3) != false) goto L_0x013f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x023a, code lost:
        if (r10.contains(" " + r2) != false) goto L_0x024e;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x028e A[LOOP:3: B:82:0x01fe->B:105:0x028e, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0142 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0252 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x019e A[LOOP:1: B:27:0x00ab->B:70:0x019e, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$0$PhonebookSearchAdapter(java.lang.String r22, java.util.ArrayList r23, java.util.ArrayList r24, int r25) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            java.lang.String r2 = r22.trim()
            java.lang.String r2 = r2.toLowerCase()
            int r3 = r2.length()
            if (r3 != 0) goto L_0x0020
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r0.updateSearchResults(r1, r3, r4)
            return
        L_0x0020:
            im.bclpbkiauv.messenger.LocaleController r3 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            java.lang.String r3 = r3.getTranslitString(r2)
            boolean r4 = r2.equals(r3)
            if (r4 != 0) goto L_0x0034
            int r4 = r3.length()
            if (r4 != 0) goto L_0x0035
        L_0x0034:
            r3 = 0
        L_0x0035:
            r4 = 0
            r5 = 1
            if (r3 == 0) goto L_0x003b
            r6 = 1
            goto L_0x003c
        L_0x003b:
            r6 = 0
        L_0x003c:
            int r6 = r6 + r5
            java.lang.String[] r6 = new java.lang.String[r6]
            r6[r4] = r2
            if (r3 == 0) goto L_0x0045
            r6[r5] = r3
        L_0x0045:
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            android.util.SparseBooleanArray r9 = new android.util.SparseBooleanArray
            r9.<init>()
            r10 = 0
        L_0x0055:
            int r11 = r23.size()
            java.lang.String r13 = "@"
            java.lang.String r14 = " "
            if (r10 >= r11) goto L_0x01b4
            r11 = r23
            java.lang.Object r15 = r11.get(r10)
            im.bclpbkiauv.messenger.ContactsController$Contact r15 = (im.bclpbkiauv.messenger.ContactsController.Contact) r15
            java.lang.String r4 = r15.first_name
            java.lang.String r12 = r15.last_name
            java.lang.String r4 = im.bclpbkiauv.messenger.ContactsController.formatName(r4, r12)
            java.lang.String r4 = r4.toLowerCase()
            im.bclpbkiauv.messenger.LocaleController r12 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            java.lang.String r12 = r12.getTranslitString(r4)
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r15.user
            if (r5 == 0) goto L_0x009a
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r15.user
            java.lang.String r5 = r5.first_name
            r16 = r2
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r15.user
            java.lang.String r2 = r2.last_name
            java.lang.String r2 = im.bclpbkiauv.messenger.ContactsController.formatName(r5, r2)
            java.lang.String r2 = r2.toLowerCase()
            im.bclpbkiauv.messenger.LocaleController r5 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            java.lang.String r5 = r5.getTranslitString(r4)
            goto L_0x009e
        L_0x009a:
            r16 = r2
            r2 = 0
            r5 = 0
        L_0x009e:
            boolean r17 = r4.equals(r12)
            if (r17 == 0) goto L_0x00a5
            r12 = 0
        L_0x00a5:
            r17 = 0
            r18 = r3
            int r3 = r6.length
            r11 = 0
        L_0x00ab:
            if (r11 >= r3) goto L_0x01a8
            r19 = r3
            r3 = r6[r11]
            if (r2 == 0) goto L_0x00ce
            boolean r20 = r2.startsWith(r3)
            if (r20 != 0) goto L_0x00eb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            boolean r0 = r2.contains(r0)
            if (r0 != 0) goto L_0x00eb
        L_0x00ce:
            if (r5 == 0) goto L_0x00ed
            boolean r0 = r5.startsWith(r3)
            if (r0 != 0) goto L_0x00eb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            boolean r0 = r5.contains(r0)
            if (r0 == 0) goto L_0x00ed
        L_0x00eb:
            r0 = 1
            goto L_0x0140
        L_0x00ed:
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r15.user
            if (r0 == 0) goto L_0x0103
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r15.user
            java.lang.String r0 = r0.username
            if (r0 == 0) goto L_0x0103
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r15.user
            java.lang.String r0 = r0.username
            boolean r0 = r0.startsWith(r3)
            if (r0 == 0) goto L_0x0103
            r0 = 2
            goto L_0x0140
        L_0x0103:
            boolean r0 = r4.startsWith(r3)
            if (r0 != 0) goto L_0x013f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            boolean r0 = r4.contains(r0)
            if (r0 != 0) goto L_0x013f
            if (r12 == 0) goto L_0x013c
            boolean r0 = r12.startsWith(r3)
            if (r0 != 0) goto L_0x013f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            boolean r0 = r12.contains(r0)
            if (r0 == 0) goto L_0x013c
            goto L_0x013f
        L_0x013c:
            r0 = r17
            goto L_0x0140
        L_0x013f:
            r0 = 3
        L_0x0140:
            if (r0 == 0) goto L_0x019e
            r11 = 3
            if (r0 != r11) goto L_0x0151
            java.lang.String r11 = r15.first_name
            java.lang.String r13 = r15.last_name
            java.lang.CharSequence r11 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r11, r13, r3)
            r8.add(r11)
            goto L_0x018e
        L_0x0151:
            r11 = 1
            if (r0 != r11) goto L_0x0164
            im.bclpbkiauv.tgnet.TLRPC$User r11 = r15.user
            java.lang.String r11 = r11.first_name
            im.bclpbkiauv.tgnet.TLRPC$User r13 = r15.user
            java.lang.String r13 = r13.last_name
            java.lang.CharSequence r11 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r11, r13, r3)
            r8.add(r11)
            goto L_0x018e
        L_0x0164:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r13)
            im.bclpbkiauv.tgnet.TLRPC$User r14 = r15.user
            java.lang.String r14 = r14.username
            r11.append(r14)
            java.lang.String r11 = r11.toString()
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r13)
            r14.append(r3)
            java.lang.String r13 = r14.toString()
            r14 = 0
            java.lang.CharSequence r11 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r11, r14, r13)
            r8.add(r11)
        L_0x018e:
            im.bclpbkiauv.tgnet.TLRPC$User r11 = r15.user
            if (r11 == 0) goto L_0x019a
            im.bclpbkiauv.tgnet.TLRPC$User r11 = r15.user
            int r11 = r11.id
            r13 = 1
            r9.put(r11, r13)
        L_0x019a:
            r7.add(r15)
            goto L_0x01a8
        L_0x019e:
            int r11 = r11 + 1
            r17 = r0
            r3 = r19
            r0 = r21
            goto L_0x00ab
        L_0x01a8:
            int r10 = r10 + 1
            r4 = 0
            r5 = 1
            r0 = r21
            r2 = r16
            r3 = r18
            goto L_0x0055
        L_0x01b4:
            r16 = r2
            r18 = r3
            r0 = 0
        L_0x01b9:
            int r2 = r24.size()
            if (r0 >= r2) goto L_0x029e
            r2 = r24
            java.lang.Object r3 = r2.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$Contact r3 = (im.bclpbkiauv.tgnet.TLRPC.Contact) r3
            int r4 = r3.user_id
            int r4 = r9.indexOfKey(r4)
            if (r4 < 0) goto L_0x01d2
            r3 = 0
            goto L_0x029a
        L_0x01d2:
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r25)
            int r5 = r3.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r5)
            java.lang.String r5 = r4.first_name
            java.lang.String r10 = r4.last_name
            java.lang.String r5 = im.bclpbkiauv.messenger.ContactsController.formatName(r5, r10)
            java.lang.String r5 = r5.toLowerCase()
            im.bclpbkiauv.messenger.LocaleController r10 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            java.lang.String r10 = r10.getTranslitString(r5)
            boolean r11 = r5.equals(r10)
            if (r11 == 0) goto L_0x01fb
            r10 = 0
        L_0x01fb:
            r11 = 0
            int r12 = r6.length
            r15 = 0
        L_0x01fe:
            if (r15 >= r12) goto L_0x0297
            r2 = r6[r15]
            boolean r17 = r5.startsWith(r2)
            if (r17 != 0) goto L_0x024c
            r17 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            boolean r3 = r5.contains(r3)
            if (r3 != 0) goto L_0x024e
            if (r10 == 0) goto L_0x023d
            boolean r3 = r10.startsWith(r2)
            if (r3 != 0) goto L_0x024e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            boolean r3 = r10.contains(r3)
            if (r3 == 0) goto L_0x023d
            goto L_0x024e
        L_0x023d:
            java.lang.String r3 = r4.username
            if (r3 == 0) goto L_0x0250
            java.lang.String r3 = r4.username
            boolean r3 = r3.startsWith(r2)
            if (r3 == 0) goto L_0x0250
            r3 = 2
            r11 = r3
            goto L_0x0250
        L_0x024c:
            r17 = r3
        L_0x024e:
            r3 = 1
            r11 = r3
        L_0x0250:
            if (r11 == 0) goto L_0x028e
            r3 = 1
            if (r11 != r3) goto L_0x0262
            java.lang.String r12 = r4.first_name
            java.lang.String r15 = r4.last_name
            java.lang.CharSequence r12 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r12, r15, r2)
            r8.add(r12)
            r3 = 0
            goto L_0x028a
        L_0x0262:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r13)
            java.lang.String r15 = r4.username
            r12.append(r15)
            java.lang.String r12 = r12.toString()
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r13)
            r15.append(r2)
            java.lang.String r15 = r15.toString()
            r3 = 0
            java.lang.CharSequence r12 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r12, r3, r15)
            r8.add(r12)
        L_0x028a:
            r7.add(r4)
            goto L_0x029a
        L_0x028e:
            r3 = 0
            int r15 = r15 + 1
            r2 = r24
            r3 = r17
            goto L_0x01fe
        L_0x0297:
            r17 = r3
            r3 = 0
        L_0x029a:
            int r0 = r0 + 1
            goto L_0x01b9
        L_0x029e:
            r0 = r21
            r0.updateSearchResults(r1, r7, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.adapters.PhonebookSearchAdapter.lambda$null$0$PhonebookSearchAdapter(java.lang.String, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    /* access modifiers changed from: protected */
    public void onUpdateSearchResults(String query) {
    }

    private void updateSearchResults(String query, ArrayList<Object> users, ArrayList<CharSequence> names) {
        AndroidUtilities.runOnUIThread(new Runnable(query, users, names) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ ArrayList f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PhonebookSearchAdapter.this.lambda$updateSearchResults$2$PhonebookSearchAdapter(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$updateSearchResults$2$PhonebookSearchAdapter(String query, ArrayList users, ArrayList names) {
        onUpdateSearchResults(query);
        this.searchResult = users;
        this.searchResultNames = names;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.searchResult.size();
    }

    public Object getItem(int i) {
        return this.searchResult.get(i);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserCell userCell = new UserCell(this.mContext, 8, 0, false);
        userCell.setNameTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        return new RecyclerListView.Holder(userCell);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            UserCell userCell = (UserCell) holder.itemView;
            Object object = getItem(position);
            TLRPC.User user = null;
            if (object instanceof ContactsController.Contact) {
                ContactsController.Contact contact = (ContactsController.Contact) object;
                if (contact.user != null) {
                    user = contact.user;
                } else {
                    userCell.setCurrentId(contact.contact_id);
                    userCell.setData((TLObject) null, this.searchResultNames.get(position), contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format(contact.phones.get(0)), 0);
                }
            } else {
                user = (TLRPC.User) object;
            }
            if (user != null) {
                PhoneFormat instance = PhoneFormat.getInstance();
                userCell.setData(user, this.searchResultNames.get(position), instance.format(Marker.ANY_NON_NULL_MARKER + user.phone), 0);
            }
        }
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }

    public int getItemViewType(int i) {
        return 0;
    }
}
