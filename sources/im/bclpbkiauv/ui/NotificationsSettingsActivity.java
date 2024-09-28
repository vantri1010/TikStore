package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.NotificationsCheckCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextDetailSettingsCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;

public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int accountsAllRow;
    /* access modifiers changed from: private */
    public int accountsInfoRow;
    /* access modifiers changed from: private */
    public int accountsSectionRow;
    private ListAdapter adapter;
    /* access modifiers changed from: private */
    public int androidAutoAlertRow;
    /* access modifiers changed from: private */
    public int badgeNumberMessagesRow;
    /* access modifiers changed from: private */
    public int badgeNumberMutedRow;
    /* access modifiers changed from: private */
    public int badgeNumberSection;
    /* access modifiers changed from: private */
    public int badgeNumberSection2Row;
    /* access modifiers changed from: private */
    public int badgeNumberShowRow;
    /* access modifiers changed from: private */
    public int callsRingtoneRow;
    /* access modifiers changed from: private */
    public int callsSection2Row;
    /* access modifiers changed from: private */
    public int callsSectionRow;
    /* access modifiers changed from: private */
    public int callsVibrateRow;
    /* access modifiers changed from: private */
    public int channelsRow;
    /* access modifiers changed from: private */
    public int contactJoinedRow;
    /* access modifiers changed from: private */
    public int eventsSection2Row;
    /* access modifiers changed from: private */
    public int eventsSectionRow;
    /* access modifiers changed from: private */
    public ArrayList<NotificationException> exceptionChannels = null;
    /* access modifiers changed from: private */
    public ArrayList<NotificationException> exceptionChats = null;
    /* access modifiers changed from: private */
    public ArrayList<NotificationException> exceptionUsers = null;
    /* access modifiers changed from: private */
    public int groupRow;
    /* access modifiers changed from: private */
    public int inappPreviewRow;
    /* access modifiers changed from: private */
    public int inappPriorityRow;
    /* access modifiers changed from: private */
    public int inappSectionRow;
    /* access modifiers changed from: private */
    public int inappSoundRow;
    /* access modifiers changed from: private */
    public int inappVibrateRow;
    /* access modifiers changed from: private */
    public int inchatSoundRow;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int notificationsSection2Row;
    /* access modifiers changed from: private */
    public int notificationsSectionRow;
    /* access modifiers changed from: private */
    public int notificationsServiceConnectionRow;
    /* access modifiers changed from: private */
    public int notificationsServiceRow;
    /* access modifiers changed from: private */
    public int otherSection2Row;
    /* access modifiers changed from: private */
    public int otherSectionRow;
    /* access modifiers changed from: private */
    public int pinnedMessageRow;
    /* access modifiers changed from: private */
    public int privateRow;
    /* access modifiers changed from: private */
    public int repeatRow;
    /* access modifiers changed from: private */
    public int resetNotificationsRow;
    /* access modifiers changed from: private */
    public int resetSection2Row;
    /* access modifiers changed from: private */
    public int resetSectionRow;
    private boolean reseting = false;
    /* access modifiers changed from: private */
    public int rowCount = 0;

    public static class NotificationException {
        public long did;
        public boolean hasCustom;
        public int muteUntil;
        public int notify;
    }

    public boolean onFragmentCreate() {
        MessagesController.getInstance(this.currentAccount).loadSignUpNotificationsSettings();
        loadExceptions();
        if (UserConfig.getActivatedAccountsCount() > 1) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.accountsSectionRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.accountsAllRow = i2;
            this.rowCount = i3 + 1;
            this.accountsInfoRow = i3;
        } else {
            this.accountsSectionRow = -1;
            this.accountsAllRow = -1;
            this.accountsInfoRow = -1;
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.notificationsSectionRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.privateRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.groupRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.channelsRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.notificationsSection2Row = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.callsSectionRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.callsVibrateRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.callsRingtoneRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.eventsSection2Row = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.badgeNumberSection = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.badgeNumberShowRow = i14;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.badgeNumberMutedRow = i15;
        int i17 = i16 + 1;
        this.rowCount = i17;
        this.badgeNumberMessagesRow = i16;
        int i18 = i17 + 1;
        this.rowCount = i18;
        this.badgeNumberSection2Row = i17;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.inappSectionRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.inappSoundRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.inappVibrateRow = i20;
        int i22 = i21 + 1;
        this.rowCount = i22;
        this.inappPreviewRow = i21;
        this.rowCount = i22 + 1;
        this.inchatSoundRow = i22;
        if (Build.VERSION.SDK_INT >= 21) {
            int i23 = this.rowCount;
            this.rowCount = i23 + 1;
            this.inappPriorityRow = i23;
        } else {
            this.inappPriorityRow = -1;
        }
        int i24 = this.rowCount;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.callsSection2Row = i24;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.eventsSectionRow = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.contactJoinedRow = i26;
        int i28 = i27 + 1;
        this.rowCount = i28;
        this.pinnedMessageRow = i27;
        int i29 = i28 + 1;
        this.rowCount = i29;
        this.otherSection2Row = i28;
        int i30 = i29 + 1;
        this.rowCount = i30;
        this.otherSectionRow = i29;
        int i31 = i30 + 1;
        this.rowCount = i31;
        this.notificationsServiceRow = i30;
        int i32 = i31 + 1;
        this.rowCount = i32;
        this.notificationsServiceConnectionRow = i31;
        this.androidAutoAlertRow = -1;
        int i33 = i32 + 1;
        this.rowCount = i33;
        this.repeatRow = i32;
        int i34 = i33 + 1;
        this.rowCount = i34;
        this.resetSection2Row = i33;
        int i35 = i34 + 1;
        this.rowCount = i35;
        this.resetSectionRow = i34;
        this.rowCount = i35 + 1;
        this.resetNotificationsRow = i35;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    private void loadExceptions() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                NotificationsSettingsActivity.this.lambda$loadExceptions$1$NotificationsSettingsActivity();
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:117:0x0320  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x033b A[LOOP:3: B:122:0x0339->B:123:0x033b, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0353  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x02d7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadExceptions$1$NotificationsSettingsActivity() {
        /*
            r26 = this;
            r9 = r26
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r10 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r11 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r12 = r0
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            r13 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r5 = r0
            int r0 = r9.currentAccount
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)
            int r4 = r0.clientUserId
            int r0 = r9.currentAccount
            android.content.SharedPreferences r3 = im.bclpbkiauv.messenger.MessagesController.getNotificationsSettings(r0)
            java.util.Map r2 = r3.getAll()
            java.util.Set r0 = r2.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x0058:
            boolean r1 = r0.hasNext()
            r16 = 32
            if (r1 == 0) goto L_0x0259
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r17 = r1.getKey()
            r18 = r0
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r17 = r6
            java.lang.String r6 = "notify2_"
            boolean r19 = r0.startsWith(r6)
            if (r19 == 0) goto L_0x023d
            r19 = r7
            java.lang.String r7 = ""
            java.lang.String r0 = r0.replace(r6, r7)
            java.lang.Long r6 = im.bclpbkiauv.messenger.Utilities.parseLong(r0)
            long r6 = r6.longValue()
            r20 = 0
            int r22 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r22 == 0) goto L_0x0230
            r20 = r11
            r21 = r12
            long r11 = (long) r4
            int r22 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r22 == 0) goto L_0x0221
            im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException r11 = new im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException
            r11.<init>()
            r11.did = r6
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r22 = r4
            java.lang.String r4 = "custom_"
            r12.append(r4)
            r12.append(r6)
            java.lang.String r4 = r12.toString()
            r12 = 0
            boolean r4 = r3.getBoolean(r4, r12)
            r11.hasCustom = r4
            java.lang.Object r4 = r1.getValue()
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            r11.notify = r4
            int r4 = r11.notify
            if (r4 == 0) goto L_0x00e9
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r12 = "notifyuntil_"
            r4.append(r12)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            java.lang.Object r4 = r2.get(r4)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x00e9
            int r12 = r4.intValue()
            r11.muteUntil = r12
        L_0x00e9:
            int r4 = (int) r6
            r23 = r0
            r12 = r1
            long r0 = r6 << r16
            int r1 = (int) r0
            if (r4 == 0) goto L_0x01af
            if (r4 <= 0) goto L_0x0130
            int r0 = r9.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            r24 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x0111
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            r14.add(r2)
            r13.put(r6, r11)
            goto L_0x0125
        L_0x0111:
            boolean r2 = r0.deleted
            if (r2 == 0) goto L_0x0125
            r6 = r17
            r0 = r18
            r7 = r19
            r11 = r20
            r12 = r21
            r4 = r22
            r2 = r24
            goto L_0x0058
        L_0x0125:
            r10.add(r11)
            r12 = r20
            r2 = r21
            r20 = r3
            goto L_0x0249
        L_0x0130:
            r24 = r2
            int r0 = r9.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            int r2 = -r4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            if (r0 != 0) goto L_0x015e
            int r2 = -r4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r15.add(r2)
            r13.put(r6, r11)
            r6 = r17
            r0 = r18
            r7 = r19
            r11 = r20
            r12 = r21
            r4 = r22
            r2 = r24
            goto L_0x0058
        L_0x015e:
            boolean r2 = r0.left
            if (r2 != 0) goto L_0x019b
            boolean r2 = r0.kicked
            if (r2 != 0) goto L_0x019b
            im.bclpbkiauv.tgnet.TLRPC$InputChannel r2 = r0.migrated_to
            if (r2 == 0) goto L_0x017a
            r6 = r17
            r0 = r18
            r7 = r19
            r11 = r20
            r12 = r21
            r4 = r22
            r2 = r24
            goto L_0x0058
        L_0x017a:
            boolean r2 = im.bclpbkiauv.messenger.ChatObject.isChannel(r0)
            if (r2 == 0) goto L_0x018e
            boolean r2 = r0.megagroup
            if (r2 != 0) goto L_0x018e
            r2 = r21
            r2.add(r11)
            r21 = r12
            r12 = r20
            goto L_0x0197
        L_0x018e:
            r2 = r21
            r21 = r12
            r12 = r20
            r12.add(r11)
        L_0x0197:
            r20 = r3
            goto L_0x0249
        L_0x019b:
            r2 = r21
            r21 = r12
            r12 = r20
            r11 = r12
            r6 = r17
            r0 = r18
            r7 = r19
            r4 = r22
            r12 = r2
            r2 = r24
            goto L_0x0058
        L_0x01af:
            r24 = r2
            r2 = r21
            r21 = r12
            r12 = r20
            if (r1 == 0) goto L_0x021a
            int r0 = r9.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            r20 = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r1)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r3)
            if (r0 != 0) goto L_0x01da
            java.lang.Integer r3 = java.lang.Integer.valueOf(r1)
            r8.add(r3)
            r13.put(r6, r11)
            r16 = r1
            r25 = r4
            goto L_0x0216
        L_0x01da:
            int r3 = r9.currentAccount
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r3)
            r16 = r1
            int r1 = r0.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            im.bclpbkiauv.tgnet.TLRPC$User r1 = r3.getUser(r1)
            if (r1 != 0) goto L_0x0200
            int r3 = r0.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r14.add(r3)
            int r3 = r0.user_id
            r25 = r4
            long r3 = (long) r3
            r13.put(r3, r11)
            goto L_0x0216
        L_0x0200:
            r25 = r4
            boolean r3 = r1.deleted
            if (r3 == 0) goto L_0x0216
            r11 = r12
            r6 = r17
            r0 = r18
            r7 = r19
            r3 = r20
            r4 = r22
            r12 = r2
            r2 = r24
            goto L_0x0058
        L_0x0216:
            r10.add(r11)
            goto L_0x0249
        L_0x021a:
            r16 = r1
            r20 = r3
            r25 = r4
            goto L_0x0249
        L_0x0221:
            r23 = r0
            r24 = r2
            r22 = r4
            r12 = r20
            r2 = r21
            r21 = r1
            r20 = r3
            goto L_0x0249
        L_0x0230:
            r23 = r0
            r21 = r1
            r24 = r2
            r20 = r3
            r22 = r4
            r2 = r12
            r12 = r11
            goto L_0x0249
        L_0x023d:
            r21 = r1
            r24 = r2
            r20 = r3
            r22 = r4
            r19 = r7
            r2 = r12
            r12 = r11
        L_0x0249:
            r11 = r12
            r6 = r17
            r0 = r18
            r7 = r19
            r3 = r20
            r4 = r22
            r12 = r2
            r2 = r24
            goto L_0x0058
        L_0x0259:
            r24 = r2
            r20 = r3
            r22 = r4
            r17 = r6
            r19 = r7
            r2 = r12
            r12 = r11
            int r0 = r13.size()
            if (r0 == 0) goto L_0x0373
            boolean r0 = r8.isEmpty()     // Catch:{ Exception -> 0x02c8 }
            java.lang.String r1 = ","
            if (r0 != 0) goto L_0x0287
            int r0 = r9.currentAccount     // Catch:{ Exception -> 0x0281 }
            im.bclpbkiauv.messenger.MessagesStorage r0 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x0281 }
            java.lang.String r3 = android.text.TextUtils.join(r1, r8)     // Catch:{ Exception -> 0x0281 }
            r0.getEncryptedChatsInternal(r3, r5, r14)     // Catch:{ Exception -> 0x0281 }
            goto L_0x0287
        L_0x0281:
            r0 = move-exception
            r6 = r17
            r7 = r19
            goto L_0x02cd
        L_0x0287:
            boolean r0 = r14.isEmpty()     // Catch:{ Exception -> 0x02c8 }
            if (r0 != 0) goto L_0x02a7
            int r0 = r9.currentAccount     // Catch:{ Exception -> 0x02a1 }
            im.bclpbkiauv.messenger.MessagesStorage r0 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x02a1 }
            java.lang.String r3 = android.text.TextUtils.join(r1, r14)     // Catch:{ Exception -> 0x02a1 }
            r7 = r19
            r0.getUsersInternal(r3, r7)     // Catch:{ Exception -> 0x029d }
            goto L_0x02a9
        L_0x029d:
            r0 = move-exception
            r6 = r17
            goto L_0x02cd
        L_0x02a1:
            r0 = move-exception
            r7 = r19
            r6 = r17
            goto L_0x02cd
        L_0x02a7:
            r7 = r19
        L_0x02a9:
            boolean r0 = r15.isEmpty()     // Catch:{ Exception -> 0x02c4 }
            if (r0 != 0) goto L_0x02c1
            int r0 = r9.currentAccount     // Catch:{ Exception -> 0x02c4 }
            im.bclpbkiauv.messenger.MessagesStorage r0 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x02c4 }
            java.lang.String r1 = android.text.TextUtils.join(r1, r15)     // Catch:{ Exception -> 0x02c4 }
            r6 = r17
            r0.getChatsInternal(r1, r6)     // Catch:{ Exception -> 0x02bf }
            goto L_0x02c3
        L_0x02bf:
            r0 = move-exception
            goto L_0x02cd
        L_0x02c1:
            r6 = r17
        L_0x02c3:
            goto L_0x02d0
        L_0x02c4:
            r0 = move-exception
            r6 = r17
            goto L_0x02cd
        L_0x02c8:
            r0 = move-exception
            r6 = r17
            r7 = r19
        L_0x02cd:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02d0:
            r0 = 0
            int r1 = r6.size()
        L_0x02d5:
            if (r0 >= r1) goto L_0x0318
            java.lang.Object r3 = r6.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r3
            boolean r4 = r3.left
            if (r4 != 0) goto L_0x0311
            boolean r4 = r3.kicked
            if (r4 != 0) goto L_0x0311
            im.bclpbkiauv.tgnet.TLRPC$InputChannel r4 = r3.migrated_to
            if (r4 == 0) goto L_0x02eb
            r11 = r8
            goto L_0x0312
        L_0x02eb:
            int r4 = r3.id
            int r4 = -r4
            r11 = r8
            long r8 = (long) r4
            java.lang.Object r4 = r13.get(r8)
            im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException r4 = (im.bclpbkiauv.ui.NotificationsSettingsActivity.NotificationException) r4
            int r8 = r3.id
            int r8 = -r8
            long r8 = (long) r8
            r13.remove(r8)
            if (r4 == 0) goto L_0x0312
            boolean r8 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r8 == 0) goto L_0x030d
            boolean r8 = r3.megagroup
            if (r8 != 0) goto L_0x030d
            r2.add(r4)
            goto L_0x0312
        L_0x030d:
            r12.add(r4)
            goto L_0x0312
        L_0x0311:
            r11 = r8
        L_0x0312:
            int r0 = r0 + 1
            r9 = r26
            r8 = r11
            goto L_0x02d5
        L_0x0318:
            r11 = r8
            r0 = 0
            int r1 = r7.size()
        L_0x031e:
            if (r0 >= r1) goto L_0x0334
            java.lang.Object r3 = r7.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$User r3 = (im.bclpbkiauv.tgnet.TLRPC.User) r3
            boolean r4 = r3.deleted
            if (r4 == 0) goto L_0x032b
            goto L_0x0331
        L_0x032b:
            int r4 = r3.id
            long r8 = (long) r4
            r13.remove(r8)
        L_0x0331:
            int r0 = r0 + 1
            goto L_0x031e
        L_0x0334:
            r0 = 0
            int r1 = r5.size()
        L_0x0339:
            if (r0 >= r1) goto L_0x034c
            java.lang.Object r3 = r5.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r3 = (im.bclpbkiauv.tgnet.TLRPC.EncryptedChat) r3
            int r4 = r3.id
            long r8 = (long) r4
            long r8 = r8 << r16
            r13.remove(r8)
            int r0 = r0 + 1
            goto L_0x0339
        L_0x034c:
            r0 = 0
            int r1 = r13.size()
        L_0x0351:
            if (r0 >= r1) goto L_0x0378
            long r3 = r13.keyAt(r0)
            int r8 = (int) r3
            if (r8 >= 0) goto L_0x0369
            java.lang.Object r8 = r13.valueAt(r0)
            r12.remove(r8)
            java.lang.Object r8 = r13.valueAt(r0)
            r2.remove(r8)
            goto L_0x0370
        L_0x0369:
            java.lang.Object r8 = r13.valueAt(r0)
            r10.remove(r8)
        L_0x0370:
            int r0 = r0 + 1
            goto L_0x0351
        L_0x0373:
            r11 = r8
            r6 = r17
            r7 = r19
        L_0x0378:
            im.bclpbkiauv.ui.-$$Lambda$NotificationsSettingsActivity$jq_9mOgIrn9j5OZGFb84Fiv0BH8 r0 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsSettingsActivity$jq_9mOgIrn9j5OZGFb84Fiv0BH8
            r1 = r0
            r9 = r2
            r16 = r24
            r2 = r26
            r17 = r20
            r3 = r7
            r18 = r22
            r4 = r6
            r19 = r5
            r20 = r6
            r6 = r10
            r21 = r7
            r7 = r12
            r8 = r9
            r1.<init>(r3, r4, r5, r6, r7, r8)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.NotificationsSettingsActivity.lambda$loadExceptions$1$NotificationsSettingsActivity():void");
    }

    public /* synthetic */ void lambda$null$0$NotificationsSettingsActivity(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList usersResult, ArrayList chatsResult, ArrayList channelsResult) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        this.exceptionUsers = usersResult;
        this.exceptionChats = chatsResult;
        this.exceptionChannels = channelsResult;
        this.adapter.notifyItemChanged(this.privateRow);
        this.adapter.notifyItemChanged(this.groupRow);
        this.adapter.notifyItemChanged(this.channelsRow);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NotificationsSettingsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass2 r3 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r3;
        recyclerListView2.setLayoutManager(r3);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                NotificationsSettingsActivity.this.lambda$createView$8$NotificationsSettingsActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$8$NotificationsSettingsActivity(View view, int position, float x, float y) {
        ArrayList<NotificationException> exceptions;
        int type;
        View view2 = view;
        int i = position;
        boolean enabled = false;
        if (getParentActivity() != null) {
            if (i == this.privateRow || i == this.groupRow || i == this.channelsRow) {
                if (i == this.privateRow) {
                    type = 1;
                    exceptions = this.exceptionUsers;
                } else if (i == this.groupRow) {
                    type = 0;
                    exceptions = this.exceptionChats;
                } else {
                    type = 2;
                    exceptions = this.exceptionChannels;
                }
                if (exceptions != null) {
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) view2;
                    enabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(type);
                    if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                        presentFragment(new NotificationsCustomSettingsActivity(type, exceptions));
                    } else {
                        NotificationsController.getInstance(this.currentAccount).setGlobalNotificationsEnabled(type, !enabled ? 0 : Integer.MAX_VALUE);
                        showExceptionsAlert(i);
                        checkCell.setChecked(!enabled, 0);
                        this.adapter.notifyItemChanged(i);
                    }
                } else {
                    return;
                }
            } else if (i == this.callsRingtoneRow) {
                try {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                    Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                    tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                    tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                    tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                    Uri currentSound = null;
                    String defaultPath = null;
                    Uri defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
                    if (defaultUri != null) {
                        defaultPath = defaultUri.getPath();
                    }
                    String path = preferences.getString("CallsRingtonePath", defaultPath);
                    if (path != null && !path.equals("NoSound")) {
                        currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                    }
                    tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                    startActivityForResult(tmpIntent, i);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (i == this.resetNotificationsRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setMessage(LocaleController.getString("ResetNotificationsAlert", R.string.ResetNotificationsAlert));
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("Reset", R.string.Reset), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        NotificationsSettingsActivity.this.lambda$null$4$NotificationsSettingsActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            } else if (i == this.inappSoundRow) {
                SharedPreferences preferences2 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor = preferences2.edit();
                enabled = preferences2.getBoolean("EnableInAppSounds", true);
                editor.putBoolean("EnableInAppSounds", !enabled);
                editor.commit();
            } else if (i == this.inappVibrateRow) {
                SharedPreferences preferences3 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor2 = preferences3.edit();
                enabled = preferences3.getBoolean("EnableInAppVibrate", true);
                editor2.putBoolean("EnableInAppVibrate", !enabled);
                editor2.commit();
            } else if (i == this.inappPreviewRow) {
                SharedPreferences preferences4 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor3 = preferences4.edit();
                enabled = preferences4.getBoolean("EnableInAppPreview", true);
                editor3.putBoolean("EnableInAppPreview", !enabled);
                editor3.commit();
            } else if (i == this.inchatSoundRow) {
                SharedPreferences preferences5 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor4 = preferences5.edit();
                enabled = preferences5.getBoolean("EnableInChatSound", true);
                editor4.putBoolean("EnableInChatSound", !enabled);
                editor4.commit();
                NotificationsController.getInstance(this.currentAccount).setInChatSoundEnabled(!enabled);
            } else if (i == this.inappPriorityRow) {
                SharedPreferences preferences6 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor5 = preferences6.edit();
                enabled = preferences6.getBoolean("EnableInAppPriority", false);
                editor5.putBoolean("EnableInAppPriority", !enabled);
                editor5.commit();
            } else if (i == this.contactJoinedRow) {
                SharedPreferences preferences7 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor6 = preferences7.edit();
                enabled = preferences7.getBoolean("EnableContactJoined", true);
                MessagesController.getInstance(this.currentAccount).enableJoined = !enabled;
                editor6.putBoolean("EnableContactJoined", !enabled);
                editor6.commit();
                TLRPC.TL_account_setContactSignUpNotification req = new TLRPC.TL_account_setContactSignUpNotification();
                req.silent = enabled;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, $$Lambda$NotificationsSettingsActivity$aMC9d9RXlabSsjTWBR1qwgavbq8.INSTANCE);
            } else if (i == this.pinnedMessageRow) {
                SharedPreferences preferences8 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor7 = preferences8.edit();
                enabled = preferences8.getBoolean("PinnedMessages", true);
                editor7.putBoolean("PinnedMessages", !enabled);
                editor7.commit();
            } else if (i == this.androidAutoAlertRow) {
                SharedPreferences preferences9 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor8 = preferences9.edit();
                enabled = preferences9.getBoolean("EnableAutoNotifications", false);
                editor8.putBoolean("EnableAutoNotifications", !enabled);
                editor8.commit();
            } else if (i == this.badgeNumberShowRow) {
                SharedPreferences.Editor editor9 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                enabled = NotificationsController.getInstance(this.currentAccount).showBadgeNumber;
                NotificationsController.getInstance(this.currentAccount).showBadgeNumber = !enabled;
                editor9.putBoolean("badgeNumber", NotificationsController.getInstance(this.currentAccount).showBadgeNumber);
                editor9.commit();
                NotificationsController.getInstance(this.currentAccount).updateBadge();
            } else if (i == this.badgeNumberMutedRow) {
                SharedPreferences.Editor editor10 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                enabled = NotificationsController.getInstance(this.currentAccount).showBadgeMuted;
                NotificationsController.getInstance(this.currentAccount).showBadgeMuted = !enabled;
                editor10.putBoolean("badgeNumberMuted", NotificationsController.getInstance(this.currentAccount).showBadgeMuted);
                editor10.commit();
                NotificationsController.getInstance(this.currentAccount).updateBadge();
            } else if (i == this.badgeNumberMessagesRow) {
                SharedPreferences.Editor editor11 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                enabled = NotificationsController.getInstance(this.currentAccount).showBadgeMessages;
                NotificationsController.getInstance(this.currentAccount).showBadgeMessages = !enabled;
                editor11.putBoolean("badgeNumberMessages", NotificationsController.getInstance(this.currentAccount).showBadgeMessages);
                editor11.commit();
                NotificationsController.getInstance(this.currentAccount).updateBadge();
            } else if (i == this.notificationsServiceConnectionRow) {
                SharedPreferences preferences10 = MessagesController.getNotificationsSettings(this.currentAccount);
                enabled = preferences10.getBoolean("pushConnection", true);
                SharedPreferences.Editor editor12 = preferences10.edit();
                editor12.putBoolean("pushConnection", !enabled);
                editor12.commit();
                if (!enabled) {
                    ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(true);
                } else {
                    ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(false);
                }
            } else if (i == this.accountsAllRow) {
                SharedPreferences preferences11 = MessagesController.getGlobalNotificationsSettings();
                enabled = preferences11.getBoolean("AllAccounts", true);
                SharedPreferences.Editor editor13 = preferences11.edit();
                editor13.putBoolean("AllAccounts", !enabled);
                editor13.commit();
                SharedConfig.showNotificationsForAllAccounts = !enabled;
                for (int a = 0; a < 3; a++) {
                    if (SharedConfig.showNotificationsForAllAccounts) {
                        NotificationsController.getInstance(a).showNotifications();
                    } else if (a == this.currentAccount) {
                        NotificationsController.getInstance(a).showNotifications();
                    } else {
                        NotificationsController.getInstance(a).hideNotifications();
                    }
                }
            } else if (i == this.notificationsServiceRow) {
                SharedPreferences preferences12 = MessagesController.getNotificationsSettings(this.currentAccount);
                enabled = preferences12.getBoolean("pushService", true);
                SharedPreferences.Editor editor14 = preferences12.edit();
                editor14.putBoolean("pushService", !enabled);
                editor14.commit();
                if (!enabled) {
                    ApplicationLoader.startPushService();
                } else {
                    ApplicationLoader.stopPushService();
                }
            } else if (i == this.callsVibrateRow) {
                if (getParentActivity() != null) {
                    String key = null;
                    if (i == this.callsVibrateRow) {
                        key = "vibrate_calls";
                    }
                    showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0, key, new Runnable(i) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            NotificationsSettingsActivity.this.lambda$null$6$NotificationsSettingsActivity(this.f$1);
                        }
                    }));
                } else {
                    return;
                }
            } else if (i == this.repeatRow) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setTitle(LocaleController.getString("RepeatNotifications", R.string.RepeatNotifications));
                builder2.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", R.string.RepeatDisabled), LocaleController.formatPluralString("Minutes", 5), LocaleController.formatPluralString("Minutes", 10), LocaleController.formatPluralString("Minutes", 30), LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Hours", 2), LocaleController.formatPluralString("Hours", 4)}, new DialogInterface.OnClickListener(i) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        NotificationsSettingsActivity.this.lambda$null$7$NotificationsSettingsActivity(this.f$1, dialogInterface, i);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder2.create());
            }
            if (view2 instanceof TextCheckCell) {
                ((TextCheckCell) view2).setChecked(!enabled);
            }
        }
    }

    public /* synthetic */ void lambda$null$4$NotificationsSettingsActivity(DialogInterface dialogInterface, int i) {
        if (!this.reseting) {
            this.reseting = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetNotifySettings(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    NotificationsSettingsActivity.this.lambda$null$3$NotificationsSettingsActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$3$NotificationsSettingsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                NotificationsSettingsActivity.this.lambda$null$2$NotificationsSettingsActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$2$NotificationsSettingsActivity() {
        MessagesController.getInstance(this.currentAccount).enableJoined = true;
        this.reseting = false;
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        editor.clear();
        editor.commit();
        this.exceptionChats.clear();
        this.exceptionUsers.clear();
        this.adapter.notifyDataSetChanged();
        if (getParentActivity() != null) {
            ToastUtils.show((int) R.string.ResetNotificationsText);
        }
    }

    static /* synthetic */ void lambda$null$5(TLObject response, TLRPC.TL_error error) {
    }

    public /* synthetic */ void lambda$null$6$NotificationsSettingsActivity(int position) {
        this.adapter.notifyItemChanged(position);
    }

    public /* synthetic */ void lambda$null$7$NotificationsSettingsActivity(int position, DialogInterface dialog, int which) {
        int minutes = 0;
        if (which == 1) {
            minutes = 5;
        } else if (which == 2) {
            minutes = 10;
        } else if (which == 3) {
            minutes = 30;
        } else if (which == 4) {
            minutes = 60;
        } else if (which == 5) {
            minutes = 120;
        } else if (which == 6) {
            minutes = PsExtractor.VIDEO_STREAM_MASK;
        }
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("repeat_messages", minutes).commit();
        this.adapter.notifyItemChanged(position);
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (!(ringtone == null || (rng = RingtoneManager.getRingtone(getParentActivity(), ringtone)) == null)) {
                if (requestCode == this.callsRingtoneRow) {
                    if (ringtone.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
                        name = LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                } else if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                } else {
                    name = rng.getTitle(getParentActivity());
                }
                rng.stop();
            }
            SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (requestCode == this.callsRingtoneRow) {
                if (name == null || ringtone == null) {
                    editor.putString("CallsRingtone", "NoSound");
                    editor.putString("CallsRingtonePath", "NoSound");
                } else {
                    editor.putString("CallsRingtone", name);
                    editor.putString("CallsRingtonePath", ringtone.toString());
                }
            }
            editor.commit();
            this.adapter.notifyItemChanged(requestCode);
        }
    }

    private void showExceptionsAlert(int position) {
        ArrayList<NotificationException> exceptions;
        String alertText = null;
        if (position == this.privateRow) {
            exceptions = this.exceptionUsers;
            if (exceptions != null && !exceptions.isEmpty()) {
                alertText = LocaleController.formatPluralString("ChatsException", exceptions.size());
            }
        } else if (position == this.groupRow) {
            exceptions = this.exceptionChats;
            if (exceptions != null && !exceptions.isEmpty()) {
                alertText = LocaleController.formatPluralString("Groups", exceptions.size());
            }
        } else {
            exceptions = this.exceptionChannels;
            if (exceptions != null && !exceptions.isEmpty()) {
                alertText = LocaleController.formatPluralString("Channels", exceptions.size());
            }
        }
        if (alertText != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            if (exceptions.size() == 1) {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsExceptionsSingleAlert", R.string.NotificationsExceptionsSingleAlert, alertText)));
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsExceptionsAlert", R.string.NotificationsExceptionsAlert, alertText)));
            }
            builder.setTitle(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions));
            builder.setNeutralButton(LocaleController.getString("ViewExceptions", R.string.ViewExceptions), new DialogInterface.OnClickListener(exceptions) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    NotificationsSettingsActivity.this.lambda$showExceptionsAlert$9$NotificationsSettingsActivity(this.f$1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showExceptionsAlert$9$NotificationsSettingsActivity(ArrayList exceptions, DialogInterface dialogInterface, int i) {
        presentFragment(new NotificationsCustomSettingsActivity(-1, exceptions));
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return (position == NotificationsSettingsActivity.this.notificationsSectionRow || position == NotificationsSettingsActivity.this.notificationsSection2Row || position == NotificationsSettingsActivity.this.inappSectionRow || position == NotificationsSettingsActivity.this.eventsSectionRow || position == NotificationsSettingsActivity.this.otherSectionRow || position == NotificationsSettingsActivity.this.resetSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection || position == NotificationsSettingsActivity.this.otherSection2Row || position == NotificationsSettingsActivity.this.resetSection2Row || position == NotificationsSettingsActivity.this.callsSection2Row || position == NotificationsSettingsActivity.this.callsSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection2Row || position == NotificationsSettingsActivity.this.accountsSectionRow || position == NotificationsSettingsActivity.this.accountsInfoRow) ? false : true;
        }

        public int getItemCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new HeaderCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else if (viewType == 1) {
                View view3 = new TextCheckCell(this.mContext);
                view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view3;
            } else if (viewType == 2) {
                View view4 = new TextDetailSettingsCell(this.mContext);
                view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view4;
            } else if (viewType == 3) {
                View view5 = new NotificationsCheckCell(this.mContext);
                view5.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view5;
            } else if (viewType == 4) {
                view = new ShadowSectionCell(this.mContext);
            } else if (viewType != 5) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else {
                View view6 = new TextSettingsCell(this.mContext);
                view6.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view6;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<NotificationException> exceptions;
            String text;
            int offUntil;
            int iconType;
            boolean enabled;
            String value;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            int itemViewType = holder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = false;
                if (itemViewType == 1) {
                    TextCheckCell checkCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppSounds", R.string.InAppSounds), preferences.getBoolean("EnableInAppSounds", true), true);
                    } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppVibrate", R.string.InAppVibrate), preferences.getBoolean("EnableInAppVibrate", true), true);
                    } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppPreview", R.string.InAppPreview), preferences.getBoolean("EnableInAppPreview", true), true);
                    } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), preferences.getBoolean("EnableInAppPriority", false), false);
                    } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("ContactJoined", R.string.ContactJoined), preferences.getBoolean("EnableContactJoined", true), true);
                    } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("PinnedMessages", R.string.PinnedMessages), preferences.getBoolean("PinnedMessages", true), false);
                    } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                        checkCell.setTextAndCheck("Android Auto", preferences.getBoolean("EnableAutoNotifications", false), true);
                    } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", R.string.NotificationsService), LocaleController.getString("NotificationsServiceInfo", R.string.NotificationsServiceInfo), preferences.getBoolean("pushService", true), true, true);
                    } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", R.string.NotificationsServiceConnection), LocaleController.getString("NotificationsServiceConnectionInfo", R.string.NotificationsServiceConnectionInfo), preferences.getBoolean("pushConnection", true), true, true);
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberShowRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberShow", R.string.BadgeNumberShow), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber, true);
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMutedRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberMutedChats", R.string.BadgeNumberMutedChats), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeMuted, true);
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberUnread", R.string.BadgeNumberUnread), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeMessages, false);
                    } else if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InChatSound", R.string.InChatSound), preferences.getBoolean("EnableInChatSound", true), true);
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Vibrate", R.string.Vibrate), preferences.getBoolean("EnableCallVibrate", true), true);
                    } else if (i == NotificationsSettingsActivity.this.accountsAllRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("AllAccounts", R.string.AllAccounts), MessagesController.getGlobalNotificationsSettings().getBoolean("AllAccounts", true), false);
                    }
                } else if (itemViewType == 2) {
                    TextDetailSettingsCell settingsCell = (TextDetailSettingsCell) viewHolder.itemView;
                    settingsCell.setMultilineDetail(true);
                    if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                        settingsCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", R.string.ResetAllNotifications), LocaleController.getString("UndoAllCustom", R.string.UndoAllCustom), false);
                    }
                } else if (itemViewType == 3) {
                    NotificationsCheckCell checkCell2 = (NotificationsCheckCell) viewHolder.itemView;
                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    int currentTime = ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).getCurrentTime();
                    if (i == NotificationsSettingsActivity.this.privateRow) {
                        text = LocaleController.getString("NotificationsPrivateChats", R.string.NotificationsPrivateChats);
                        exceptions = NotificationsSettingsActivity.this.exceptionUsers;
                        offUntil = preferences2.getInt("EnableAll2", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupRow) {
                        text = LocaleController.getString("NotificationsGroups", R.string.NotificationsGroups);
                        exceptions = NotificationsSettingsActivity.this.exceptionChats;
                        offUntil = preferences2.getInt("EnableGroup2", 0);
                    } else {
                        text = LocaleController.getString("NotificationsChannels", R.string.NotificationsChannels);
                        exceptions = NotificationsSettingsActivity.this.exceptionChannels;
                        offUntil = preferences2.getInt("EnableChannel2", 0);
                    }
                    boolean z2 = offUntil < currentTime;
                    boolean enabled2 = z2;
                    if (z2) {
                        iconType = 0;
                    } else if (offUntil - 31536000 >= currentTime) {
                        iconType = 0;
                    } else {
                        iconType = 2;
                    }
                    StringBuilder builder = new StringBuilder();
                    if (exceptions == null || exceptions.isEmpty()) {
                        builder.append(LocaleController.getString("TapToChange", R.string.TapToChange));
                        enabled = enabled2;
                    } else {
                        boolean z3 = offUntil < currentTime;
                        boolean enabled3 = z3;
                        if (z3) {
                            builder.append(LocaleController.getString("NotificationsOn", R.string.NotificationsOn));
                        } else if (offUntil - 31536000 >= currentTime) {
                            builder.append(LocaleController.getString("NotificationsOff", R.string.NotificationsOff));
                        } else {
                            builder.append(LocaleController.formatString("NotificationsOffUntil", R.string.NotificationsOffUntil, LocaleController.stringForMessageListDate((long) offUntil)));
                        }
                        if (builder.length() != 0) {
                            builder.append(", ");
                        }
                        builder.append(LocaleController.formatPluralString("Exception", exceptions.size()));
                        enabled = enabled3;
                    }
                    if (i != NotificationsSettingsActivity.this.channelsRow) {
                        z = true;
                    }
                    StringBuilder sb = builder;
                    int i2 = offUntil;
                    checkCell2.setTextAndValueAndCheck(text, builder, enabled, iconType, z);
                } else if (itemViewType == 5) {
                    TextSettingsCell textCell = (TextSettingsCell) viewHolder.itemView;
                    SharedPreferences preferences3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                        String value2 = preferences3.getString("CallsRingtone", LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone));
                        if (value2.equals("NoSound")) {
                            value2 = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", R.string.VoipSettingsRingtone), value2, false);
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        int value3 = 0;
                        if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                            value3 = preferences3.getInt("vibrate_calls", 0);
                        }
                        if (value3 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                        } else if (value3 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                        } else if (value3 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                        } else if (value3 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                        } else if (value3 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent), true);
                        }
                    } else if (i == NotificationsSettingsActivity.this.repeatRow) {
                        int minutes = preferences3.getInt("repeat_messages", 60);
                        if (minutes == 0) {
                            value = LocaleController.getString("RepeatNotificationsNever", R.string.RepeatNotificationsNever);
                        } else if (minutes < 60) {
                            value = LocaleController.formatPluralString("Minutes", minutes);
                        } else {
                            value = LocaleController.formatPluralString("Hours", minutes / 60);
                        }
                        textCell.setTextAndValue(LocaleController.getString("RepeatNotifications", R.string.RepeatNotifications), value, false);
                    }
                } else if (itemViewType == 6) {
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == NotificationsSettingsActivity.this.accountsInfoRow) {
                        textCell2.setText(LocaleController.getString("ShowNotificationsForInfo", R.string.ShowNotificationsForInfo));
                    }
                }
            } else {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == NotificationsSettingsActivity.this.notificationsSectionRow) {
                    headerCell.setText(LocaleController.getString("NotificationsForChats", R.string.NotificationsForChats));
                } else if (i == NotificationsSettingsActivity.this.inappSectionRow) {
                    headerCell.setText(LocaleController.getString("InAppNotifications", R.string.InAppNotifications));
                } else if (i == NotificationsSettingsActivity.this.eventsSectionRow) {
                    headerCell.setText(LocaleController.getString("Events", R.string.Events));
                } else if (i == NotificationsSettingsActivity.this.otherSectionRow) {
                    headerCell.setText(LocaleController.getString("NotificationsOther", R.string.NotificationsOther));
                } else if (i == NotificationsSettingsActivity.this.resetSectionRow) {
                    headerCell.setText(LocaleController.getString("Reset", R.string.Reset));
                } else if (i == NotificationsSettingsActivity.this.callsSectionRow) {
                    headerCell.setText(LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings));
                } else if (i == NotificationsSettingsActivity.this.badgeNumberSection) {
                    headerCell.setText(LocaleController.getString("BadgeNumber", R.string.BadgeNumber));
                } else if (i == NotificationsSettingsActivity.this.accountsSectionRow) {
                    headerCell.setText(LocaleController.getString("ShowNotificationsFor", R.string.ShowNotificationsFor));
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == NotificationsSettingsActivity.this.eventsSectionRow || position == NotificationsSettingsActivity.this.otherSectionRow || position == NotificationsSettingsActivity.this.resetSectionRow || position == NotificationsSettingsActivity.this.callsSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection || position == NotificationsSettingsActivity.this.inappSectionRow || position == NotificationsSettingsActivity.this.notificationsSectionRow || position == NotificationsSettingsActivity.this.accountsSectionRow) {
                return 0;
            }
            if (position == NotificationsSettingsActivity.this.inappSoundRow || position == NotificationsSettingsActivity.this.inappVibrateRow || position == NotificationsSettingsActivity.this.notificationsServiceConnectionRow || position == NotificationsSettingsActivity.this.inappPreviewRow || position == NotificationsSettingsActivity.this.contactJoinedRow || position == NotificationsSettingsActivity.this.pinnedMessageRow || position == NotificationsSettingsActivity.this.notificationsServiceRow || position == NotificationsSettingsActivity.this.badgeNumberMutedRow || position == NotificationsSettingsActivity.this.badgeNumberMessagesRow || position == NotificationsSettingsActivity.this.badgeNumberShowRow || position == NotificationsSettingsActivity.this.inappPriorityRow || position == NotificationsSettingsActivity.this.inchatSoundRow || position == NotificationsSettingsActivity.this.androidAutoAlertRow || position == NotificationsSettingsActivity.this.accountsAllRow) {
                return 1;
            }
            if (position == NotificationsSettingsActivity.this.resetNotificationsRow) {
                return 2;
            }
            if (position == NotificationsSettingsActivity.this.privateRow || position == NotificationsSettingsActivity.this.groupRow || position == NotificationsSettingsActivity.this.channelsRow) {
                return 3;
            }
            if (position == NotificationsSettingsActivity.this.eventsSection2Row || position == NotificationsSettingsActivity.this.notificationsSection2Row || position == NotificationsSettingsActivity.this.otherSection2Row || position == NotificationsSettingsActivity.this.resetSection2Row || position == NotificationsSettingsActivity.this.callsSection2Row || position == NotificationsSettingsActivity.this.badgeNumberSection2Row) {
                return 4;
            }
            if (position == NotificationsSettingsActivity.this.accountsInfoRow) {
                return 6;
            }
            return 5;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextSettingsCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteLinkText)};
    }
}
