package im.bclpbkiauv.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.NotificationsSettingsActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MrySwitch;
import java.util.ArrayList;

public class NoticeAndSoundSettingActivity extends BaseFragment {
    private ArrayList<NotificationsSettingsActivity.NotificationException> exceptionChannels = null;
    private ArrayList<NotificationsSettingsActivity.NotificationException> exceptionChats = null;
    private ArrayList<NotificationsSettingsActivity.NotificationException> exceptionUsers = null;
    private boolean reseting = false;

    public boolean onFragmentCreate() {
        MessagesController.getInstance(this.currentAccount).loadSignUpNotificationsSettings();
        loadExceptions();
        return super.onFragmentCreate();
    }

    private void loadExceptions() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                NoticeAndSoundSettingActivity.this.lambda$loadExceptions$1$NoticeAndSoundSettingActivity();
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:117:0x0320  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x033b A[LOOP:3: B:122:0x0339->B:123:0x033b, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0353  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x02d7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadExceptions$1$NoticeAndSoundSettingActivity() {
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
            im.bclpbkiauv.ui.settings.-$$Lambda$NoticeAndSoundSettingActivity$CIfniZASn3nnfTA9Xim5WdBwOs4 r0 = new im.bclpbkiauv.ui.settings.-$$Lambda$NoticeAndSoundSettingActivity$CIfniZASn3nnfTA9Xim5WdBwOs4
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
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.settings.NoticeAndSoundSettingActivity.lambda$loadExceptions$1$NoticeAndSoundSettingActivity():void");
    }

    public /* synthetic */ void lambda$null$0$NoticeAndSoundSettingActivity(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList usersResult, ArrayList chatsResult, ArrayList channelsResult) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        this.exceptionUsers = usersResult;
        this.exceptionChats = chatsResult;
        this.exceptionChannels = channelsResult;
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_setting_notice, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initView(context);
        initGlobalSetting();
        initListener();
        return this.fragmentView;
    }

    private void initView(Context context) {
        this.fragmentView.findViewById(R.id.rl_show_notice).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_preview_message).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_sound).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_group_show_notice).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_group_preview_message).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_group_sound).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_channel_show_notice).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_channel_preview_message).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_channel_sound).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_app_show_notice).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_app_shake_notice).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_app_preview_notice).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_include_closed_dialog).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_msg_count_statistics).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_new_contacter_add).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_reset).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    private void initListener() {
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NoticeAndSoundSettingActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView.findViewById(R.id.rl_show_notice).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$3$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_preview_message).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$4$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_sound).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$5$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_group_show_notice).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$7$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_group_preview_message).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$8$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_group_sound).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$9$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_channel_show_notice).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$11$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_channel_preview_message).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$12$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_channel_sound).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$13$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_app_show_notice).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$14$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_app_shake_notice).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$15$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_app_preview_notice).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$16$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_include_closed_dialog).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$17$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_msg_count_statistics).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$18$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_new_contacter_add).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$20$NoticeAndSoundSettingActivity(view);
            }
        });
        this.fragmentView.findViewById(R.id.rl_reset).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NoticeAndSoundSettingActivity.this.lambda$initListener$24$NoticeAndSoundSettingActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initListener$3$NoticeAndSoundSettingActivity(View view) {
        boolean enabled = getNotificationsController().isGlobalNotificationsEnabled(1);
        if (!enabled) {
            getNotificationsController().setGlobalNotificationsEnabled(1, 0);
            ((MrySwitch) this.fragmentView.findViewById(R.id.switch_show)).setChecked(!enabled, true);
            setPrivateSettingEnabled(((MrySwitch) this.fragmentView.findViewById(R.id.switch_show)).isChecked());
            return;
        }
        AlertsCreator.showCustomNotificationsDialog(this, 0, 1, this.exceptionUsers, this.currentAccount, new MessagesStorage.IntCallback() {
            public final void run(int i) {
                NoticeAndSoundSettingActivity.this.lambda$null$2$NoticeAndSoundSettingActivity(i);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$NoticeAndSoundSettingActivity(int param) {
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_show)).setChecked(getNotificationsController().isGlobalNotificationsEnabled(1), true);
        setPrivateSettingEnabled(((MrySwitch) this.fragmentView.findViewById(R.id.switch_show)).isChecked());
    }

    public /* synthetic */ void lambda$initListener$4$NoticeAndSoundSettingActivity(View view) {
        if (view.isEnabled()) {
            SharedPreferences preferences = getNotificationsSettings();
            SharedPreferences.Editor editor = preferences.edit();
            boolean enabled = preferences.getBoolean("EnablePreviewAll", true);
            editor.putBoolean("EnablePreviewAll", !enabled);
            editor.commit();
            getNotificationsController().updateServerNotificationsSettings(1);
            ((MrySwitch) this.fragmentView.findViewById(R.id.switch_preview_message)).setChecked(!enabled, true);
        }
    }

    public /* synthetic */ void lambda$initListener$5$NoticeAndSoundSettingActivity(View view) {
        if (view.isEnabled()) {
            try {
                SharedPreferences preferences = getNotificationsSettings();
                Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                Uri currentSound = null;
                String defaultPath = null;
                Uri defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                if (defaultUri != null) {
                    defaultPath = defaultUri.getPath();
                }
                String path = preferences.getString("GlobalSoundPath", defaultPath);
                if (path != null && !path.equals("NoSound")) {
                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                }
                tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                startActivityForResult(tmpIntent, 1);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$initListener$7$NoticeAndSoundSettingActivity(View view) {
        boolean enabled = getNotificationsController().isGlobalNotificationsEnabled(0);
        if (!enabled) {
            getNotificationsController().setGlobalNotificationsEnabled(0, 0);
            ((MrySwitch) this.fragmentView.findViewById(R.id.switch_group_show)).setChecked(!enabled, true);
            setGroupSettingEnabled(((MrySwitch) this.fragmentView.findViewById(R.id.switch_group_show)).isChecked());
            return;
        }
        AlertsCreator.showCustomNotificationsDialog(this, 0, 0, this.exceptionChats, this.currentAccount, new MessagesStorage.IntCallback() {
            public final void run(int i) {
                NoticeAndSoundSettingActivity.this.lambda$null$6$NoticeAndSoundSettingActivity(i);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$NoticeAndSoundSettingActivity(int param) {
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_group_show)).setChecked(getNotificationsController().isGlobalNotificationsEnabled(0), true);
        setGroupSettingEnabled(((MrySwitch) this.fragmentView.findViewById(R.id.switch_group_show)).isChecked());
    }

    public /* synthetic */ void lambda$initListener$8$NoticeAndSoundSettingActivity(View view) {
        if (view.isEnabled()) {
            SharedPreferences preferences = getNotificationsSettings();
            SharedPreferences.Editor editor = preferences.edit();
            boolean enabled = preferences.getBoolean("EnablePreviewGroup", true);
            editor.putBoolean("EnablePreviewGroup", !enabled);
            editor.commit();
            getNotificationsController().updateServerNotificationsSettings(0);
            ((MrySwitch) this.fragmentView.findViewById(R.id.switch_group_preview_message)).setChecked(!enabled, true);
        }
    }

    public /* synthetic */ void lambda$initListener$9$NoticeAndSoundSettingActivity(View view) {
        if (view.isEnabled()) {
            try {
                SharedPreferences preferences = getNotificationsSettings();
                Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                Uri currentSound = null;
                String defaultPath = null;
                Uri defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                if (defaultUri != null) {
                    defaultPath = defaultUri.getPath();
                }
                String path = preferences.getString("GroupSoundPath", defaultPath);
                if (path != null && !path.equals("NoSound")) {
                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                }
                tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                startActivityForResult(tmpIntent, 0);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$initListener$11$NoticeAndSoundSettingActivity(View view) {
        boolean enabled = getNotificationsController().isGlobalNotificationsEnabled(2);
        if (!enabled) {
            getNotificationsController().setGlobalNotificationsEnabled(2, 0);
            ((MrySwitch) this.fragmentView.findViewById(R.id.switch_channel_show)).setChecked(!enabled, true);
            setChannelSettingEnabled(((MrySwitch) this.fragmentView.findViewById(R.id.switch_channel_show)).isChecked());
            return;
        }
        AlertsCreator.showCustomNotificationsDialog(this, 0, 2, this.exceptionChannels, this.currentAccount, new MessagesStorage.IntCallback() {
            public final void run(int i) {
                NoticeAndSoundSettingActivity.this.lambda$null$10$NoticeAndSoundSettingActivity(i);
            }
        });
    }

    public /* synthetic */ void lambda$null$10$NoticeAndSoundSettingActivity(int param) {
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_channel_show)).setChecked(getNotificationsController().isGlobalNotificationsEnabled(2), true);
        setChannelSettingEnabled(((MrySwitch) this.fragmentView.findViewById(R.id.switch_channel_show)).isChecked());
    }

    public /* synthetic */ void lambda$initListener$12$NoticeAndSoundSettingActivity(View view) {
        if (view.isEnabled()) {
            SharedPreferences preferences = getNotificationsSettings();
            SharedPreferences.Editor editor = preferences.edit();
            boolean enabled = preferences.getBoolean("EnablePreviewChannel", true);
            editor.putBoolean("EnablePreviewChannel", !enabled);
            editor.commit();
            getNotificationsController().updateServerNotificationsSettings(2);
            ((MrySwitch) this.fragmentView.findViewById(R.id.switch_channel_preview_message)).setChecked(!enabled, true);
        }
    }

    public /* synthetic */ void lambda$initListener$13$NoticeAndSoundSettingActivity(View view) {
        if (view.isEnabled()) {
            try {
                SharedPreferences preferences = getNotificationsSettings();
                Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                Uri currentSound = null;
                String defaultPath = null;
                Uri defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                if (defaultUri != null) {
                    defaultPath = defaultUri.getPath();
                }
                String path = preferences.getString("ChannelSoundPath", defaultPath);
                if (path != null && !path.equals("NoSound")) {
                    currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                }
                tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                startActivityForResult(tmpIntent, 2);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$initListener$14$NoticeAndSoundSettingActivity(View view) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        SharedPreferences.Editor editor = preferences.edit();
        boolean enabled = preferences.getBoolean("EnableInAppSounds", true);
        editor.putBoolean("EnableInAppSounds", !enabled);
        editor.commit();
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_app_show)).setChecked(!enabled, true);
    }

    public /* synthetic */ void lambda$initListener$15$NoticeAndSoundSettingActivity(View view) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        SharedPreferences.Editor editor = preferences.edit();
        boolean enabled = preferences.getBoolean("EnableInAppVibrate", true);
        editor.putBoolean("EnableInAppVibrate", !enabled);
        editor.commit();
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_app_shake)).setChecked(!enabled, true);
    }

    public /* synthetic */ void lambda$initListener$16$NoticeAndSoundSettingActivity(View view) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        SharedPreferences.Editor editor = preferences.edit();
        boolean enabled = preferences.getBoolean("EnableInAppPreview", true);
        editor.putBoolean("EnableInAppPreview", !enabled);
        editor.commit();
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_app_preview_notice)).setChecked(!enabled, true);
    }

    public /* synthetic */ void lambda$initListener$17$NoticeAndSoundSettingActivity(View view) {
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        boolean enabled = NotificationsController.getInstance(this.currentAccount).showBadgeMuted;
        NotificationsController.getInstance(this.currentAccount).showBadgeMuted = !enabled;
        editor.putBoolean("badgeNumberMuted", NotificationsController.getInstance(this.currentAccount).showBadgeMuted);
        editor.commit();
        NotificationsController.getInstance(this.currentAccount).updateBadge();
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_include_closed_dialog)).setChecked(!enabled, true);
    }

    public /* synthetic */ void lambda$initListener$18$NoticeAndSoundSettingActivity(View view) {
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        boolean enabled = NotificationsController.getInstance(this.currentAccount).showBadgeMessages;
        NotificationsController.getInstance(this.currentAccount).showBadgeMessages = !enabled;
        editor.putBoolean("badgeNumberMessages", NotificationsController.getInstance(this.currentAccount).showBadgeMessages);
        editor.commit();
        NotificationsController.getInstance(this.currentAccount).updateBadge();
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_msg_count_statistics)).setChecked(!enabled, true);
    }

    public /* synthetic */ void lambda$initListener$20$NoticeAndSoundSettingActivity(View view) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        SharedPreferences.Editor editor = preferences.edit();
        boolean enabled = preferences.getBoolean("EnableContactJoined", true);
        MessagesController.getInstance(this.currentAccount).enableJoined = !enabled;
        editor.putBoolean("EnableContactJoined", !enabled);
        editor.commit();
        TLRPC.TL_account_setContactSignUpNotification req = new TLRPC.TL_account_setContactSignUpNotification();
        req.silent = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, $$Lambda$NoticeAndSoundSettingActivity$_EaEdEaLQCiFfYVe4ZdSNSpf7g.INSTANCE);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_new_contacter_add)).setChecked(!enabled, true);
    }

    static /* synthetic */ void lambda$null$19(TLObject response, TLRPC.TL_error error) {
    }

    public /* synthetic */ void lambda$initListener$24$NoticeAndSoundSettingActivity(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("ResetNotificationsAlert", R.string.ResetNotificationsAlert));
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("Reset", R.string.Reset), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                NoticeAndSoundSettingActivity.this.lambda$null$23$NoticeAndSoundSettingActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$23$NoticeAndSoundSettingActivity(DialogInterface dialogInterface, int i) {
        if (!this.reseting) {
            this.reseting = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetNotifySettings(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    NoticeAndSoundSettingActivity.this.lambda$null$22$NoticeAndSoundSettingActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$22$NoticeAndSoundSettingActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                NoticeAndSoundSettingActivity.this.lambda$null$21$NoticeAndSoundSettingActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$21$NoticeAndSoundSettingActivity() {
        MessagesController.getInstance(this.currentAccount).enableJoined = true;
        this.reseting = false;
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        editor.clear();
        editor.commit();
        this.exceptionChats.clear();
        this.exceptionUsers.clear();
        initGlobalSetting();
        if (getParentActivity() != null) {
            ToastUtils.show((int) R.string.ResetNotificationsText);
        }
    }

    private void initGlobalSetting() {
        StringBuilder builder = new StringBuilder();
        SharedPreferences preferences = getNotificationsSettings();
        int offUntil = preferences.getInt("EnableAll2", 0);
        int currentTime = getConnectionsManager().getCurrentTime();
        boolean z = offUntil < currentTime;
        boolean enabled = z;
        if (z) {
            builder.append(LocaleController.getString("NotificationsOn", R.string.NotificationsOn));
        } else if (offUntil - 31536000 >= currentTime) {
            builder.append(LocaleController.getString("NotificationsOff", R.string.NotificationsOff));
        } else {
            builder.append(LocaleController.formatString("NotificationsOffUntil", R.string.NotificationsOffUntil, LocaleController.stringForMessageListDate((long) offUntil)));
        }
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_show)).setChecked(enabled, true);
        setPrivateSettingEnabled(enabled);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_preview_message)).setChecked(preferences.getBoolean("EnablePreviewAll", true), true);
        String value = preferences.getString("GlobalSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
        if (value.equals("NoSound")) {
            value = LocaleController.getString("NoSound", R.string.NoSound);
        }
        ((TextView) this.fragmentView.findViewById(R.id.tv_sound_type)).setText(value);
        initGroupSetting();
        initChannelSetting();
        initOtherSetting();
    }

    private void initGroupSetting() {
        StringBuilder builder = new StringBuilder();
        SharedPreferences preferences = getNotificationsSettings();
        int offUntil = preferences.getInt("EnableGroup2", 0);
        int currentTime = getConnectionsManager().getCurrentTime();
        boolean z = offUntil < currentTime;
        boolean enabled = z;
        if (z) {
            builder.append(LocaleController.getString("NotificationsOn", R.string.NotificationsOn));
        } else if (offUntil - 31536000 >= currentTime) {
            builder.append(LocaleController.getString("NotificationsOff", R.string.NotificationsOff));
        } else {
            builder.append(LocaleController.formatString("NotificationsOffUntil", R.string.NotificationsOffUntil, LocaleController.stringForMessageListDate((long) offUntil)));
        }
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_group_show)).setChecked(enabled, true);
        setGroupSettingEnabled(enabled);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_group_preview_message)).setChecked(preferences.getBoolean("EnablePreviewGroup", true), true);
        String value = preferences.getString("GroupSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
        if (value.equals("NoSound")) {
            value = LocaleController.getString("NoSound", R.string.NoSound);
        }
        ((TextView) this.fragmentView.findViewById(R.id.tv_group_sound_type)).setText(value);
    }

    private void initChannelSetting() {
        StringBuilder builder = new StringBuilder();
        SharedPreferences preferences = getNotificationsSettings();
        int offUntil = preferences.getInt("EnableChannel2", 0);
        int currentTime = getConnectionsManager().getCurrentTime();
        boolean z = offUntil < currentTime;
        boolean enabled = z;
        if (z) {
            builder.append(LocaleController.getString("NotificationsOn", R.string.NotificationsOn));
        } else if (offUntil - 31536000 >= currentTime) {
            builder.append(LocaleController.getString("NotificationsOff", R.string.NotificationsOff));
        } else {
            builder.append(LocaleController.formatString("NotificationsOffUntil", R.string.NotificationsOffUntil, LocaleController.stringForMessageListDate((long) offUntil)));
        }
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_channel_show)).setChecked(enabled, true);
        setChannelSettingEnabled(enabled);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_channel_preview_message)).setChecked(preferences.getBoolean("EnablePreviewChannel", true), true);
        String value = preferences.getString("ChannelSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
        if (value.equals("NoSound")) {
            value = LocaleController.getString("NoSound", R.string.NoSound);
        }
        ((TextView) this.fragmentView.findViewById(R.id.tv_channel_sound_type)).setText(value);
    }

    private void initOtherSetting() {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_app_show)).setChecked(preferences.getBoolean("EnableInAppSounds", true), true);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_app_shake)).setChecked(preferences.getBoolean("EnableInAppVibrate", true), true);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_app_preview_notice)).setChecked(preferences.getBoolean("EnableInAppPreview", true), true);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_include_closed_dialog)).setChecked(NotificationsController.getInstance(this.currentAccount).showBadgeMuted, true);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_msg_count_statistics)).setChecked(NotificationsController.getInstance(this.currentAccount).showBadgeMessages, true);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_new_contacter_add)).setChecked(preferences.getBoolean("EnableContactJoined", true), true);
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (!(ringtone == null || (rng = RingtoneManager.getRingtone(getParentActivity(), ringtone)) == null)) {
                if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                } else {
                    name = rng.getTitle(getParentActivity());
                }
                rng.stop();
            }
            SharedPreferences.Editor editor = getNotificationsSettings().edit();
            if (requestCode == 1) {
                if (name == null || ringtone == null) {
                    editor.putString("GlobalSound", "NoSound");
                    editor.putString("GlobalSoundPath", "NoSound");
                } else {
                    editor.putString("GlobalSound", name);
                    editor.putString("GlobalSoundPath", ringtone.toString());
                }
                ((TextView) this.fragmentView.findViewById(R.id.tv_sound_type)).setText(name == null ? LocaleController.getString("NoSound", R.string.NoSound) : name);
            } else if (requestCode == 0) {
                if (name == null || ringtone == null) {
                    editor.putString("GroupSound", "NoSound");
                    editor.putString("GroupSoundPath", "NoSound");
                } else {
                    editor.putString("GroupSound", name);
                    editor.putString("GroupSoundPath", ringtone.toString());
                }
                ((TextView) this.fragmentView.findViewById(R.id.tv_group_sound_type)).setText(name == null ? LocaleController.getString("NoSound", R.string.NoSound) : name);
            } else if (requestCode == 2) {
                if (name == null || ringtone == null) {
                    editor.putString("ChannelSound", "NoSound");
                    editor.putString("ChannelSoundPath", "NoSound");
                } else {
                    editor.putString("ChannelSound", name);
                    editor.putString("ChannelSoundPath", ringtone.toString());
                }
                ((TextView) this.fragmentView.findViewById(R.id.tv_channel_sound_type)).setText(name == null ? LocaleController.getString("NoSound", R.string.NoSound) : name);
            }
            editor.commit();
            getNotificationsController().updateServerNotificationsSettings(requestCode);
        }
    }

    private void setGroupSettingEnabled(boolean blnEnable) {
        float f = 1.0f;
        this.fragmentView.findViewById(R.id.tv_group_preview_message).setAlpha(blnEnable ? 1.0f : 0.5f);
        this.fragmentView.findViewById(R.id.switch_group_preview_message).setAlpha(blnEnable ? 1.0f : 0.5f);
        this.fragmentView.findViewById(R.id.tv_group_sound).setAlpha(blnEnable ? 1.0f : 0.5f);
        View findViewById = this.fragmentView.findViewById(R.id.tv_group_sound_type);
        if (!blnEnable) {
            f = 0.5f;
        }
        findViewById.setAlpha(f);
        this.fragmentView.findViewById(R.id.rl_group_sound).setEnabled(blnEnable);
        this.fragmentView.findViewById(R.id.rl_group_preview_message).setEnabled(blnEnable);
    }

    private void setChannelSettingEnabled(boolean blnEnable) {
        float f = 1.0f;
        this.fragmentView.findViewById(R.id.tv_channel_preview_message).setAlpha(blnEnable ? 1.0f : 0.5f);
        this.fragmentView.findViewById(R.id.switch_channel_preview_message).setAlpha(blnEnable ? 1.0f : 0.5f);
        this.fragmentView.findViewById(R.id.tv_channel_sound).setAlpha(blnEnable ? 1.0f : 0.5f);
        View findViewById = this.fragmentView.findViewById(R.id.tv_channel_sound_type);
        if (!blnEnable) {
            f = 0.5f;
        }
        findViewById.setAlpha(f);
        this.fragmentView.findViewById(R.id.rl_channel_sound).setEnabled(blnEnable);
        this.fragmentView.findViewById(R.id.rl_channel_preview_message).setEnabled(blnEnable);
    }

    private void setPrivateSettingEnabled(boolean blnEnable) {
        float f = 1.0f;
        this.fragmentView.findViewById(R.id.tv_private_preview_message).setAlpha(blnEnable ? 1.0f : 0.5f);
        this.fragmentView.findViewById(R.id.switch_preview_message).setAlpha(blnEnable ? 1.0f : 0.5f);
        this.fragmentView.findViewById(R.id.tv_private_sound).setAlpha(blnEnable ? 1.0f : 0.5f);
        View findViewById = this.fragmentView.findViewById(R.id.tv_sound_type);
        if (!blnEnable) {
            f = 0.5f;
        }
        findViewById.setAlpha(f);
        this.fragmentView.findViewById(R.id.rl_sound).setEnabled(blnEnable);
        this.fragmentView.findViewById(R.id.rl_preview_message).setEnabled(blnEnable);
    }

    private void setColors() {
        this.fragmentView.findViewById(R.id.rl_show_notice).setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
    }
}
