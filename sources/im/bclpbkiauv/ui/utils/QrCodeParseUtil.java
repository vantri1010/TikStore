package im.bclpbkiauv.ui.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.fragment.app.Fragment;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.actionbar.ActionBarLayout;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.fragments.BaseFmts;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import java.util.ArrayList;

public class QrCodeParseUtil {
    public static void tryParseQrCode(Object hostObj, int currentAccount, String url, boolean removeLast, boolean forceWithoutAnimation, boolean openBowser, boolean allowCustom) {
        tryParseQrCode(hostObj, currentAccount, url, true, removeLast, forceWithoutAnimation, true, false, openBowser, allowCustom);
    }

    /* JADX WARNING: type inference failed for: r7v4, types: [int] */
    /* JADX WARNING: type inference failed for: r7v7 */
    /* JADX WARNING: type inference failed for: r7v9 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x02d0  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x02f0  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void tryParseQrCode(java.lang.Object r30, int r31, java.lang.String r32, boolean r33, boolean r34, boolean r35, boolean r36, boolean r37, boolean r38, boolean r39) {
        /*
            r13 = r30
            r14 = r32
            r15 = r39
            checkHost(r30)
            boolean r0 = android.text.TextUtils.isEmpty(r32)
            if (r0 == 0) goto L_0x0010
            return
        L_0x0010:
            r12 = r32
            android.net.Uri r11 = android.net.Uri.parse(r32)
            java.lang.String r0 = r11.getScheme()
            java.lang.String r1 = ""
            if (r0 == 0) goto L_0x0027
            java.lang.String r0 = r11.getScheme()
            java.lang.String r0 = r0.toLowerCase()
            goto L_0x0028
        L_0x0027:
            r0 = r1
        L_0x0028:
            r10 = r0
            im.bclpbkiauv.messenger.MessagesController r0 = getMessagesController(r31)
            java.lang.String r7 = r0.sharePrefix
            boolean r0 = android.text.TextUtils.isEmpty(r7)
            if (r0 != 0) goto L_0x003a
            android.net.Uri r0 = android.net.Uri.parse(r7)
            goto L_0x003b
        L_0x003a:
            r0 = 0
        L_0x003b:
            r16 = r0
            if (r16 == 0) goto L_0x0044
            java.lang.String r0 = r16.getHost()
            goto L_0x0045
        L_0x0044:
            r0 = 0
        L_0x0045:
            r9 = r0
            java.lang.String r0 = "http"
            boolean r0 = r0.equals(r10)
            if (r0 != 0) goto L_0x0063
            java.lang.String r0 = "https"
            boolean r0 = r0.equals(r10)
            if (r0 == 0) goto L_0x0057
            goto L_0x0063
        L_0x0057:
            r1 = r35
            r27 = r9
            r24 = r10
            r29 = r11
            r3 = r12
            r2 = r15
            goto L_0x02f7
        L_0x0063:
            java.lang.String r6 = r11.getHost()
            java.lang.String r0 = "www.shareinstall.com.cn"
            boolean r0 = r6.equals(r0)
            java.lang.String r2 = "Uname"
            java.lang.String r3 = "#"
            java.lang.String r4 = "%3D"
            java.lang.String r8 = "="
            if (r0 == 0) goto L_0x00dc
            java.lang.String r0 = "Key"
            java.lang.String r0 = r11.getQueryParameter(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x00d8
            java.lang.String r4 = r0.replace(r4, r8)
            r1 = 0
            byte[] r0 = android.util.Base64.decode(r4, r1)
            java.lang.String r5 = new java.lang.String
            r5.<init>(r0)
            java.lang.String[] r20 = r5.split(r3)
            r3 = r20[r1]
            java.lang.String[] r3 = r3.split(r8)
            r19 = 1
            r21 = r3[r19]
            r3 = r20[r19]
            java.lang.String[] r3 = r3.split(r8)
            r22 = r3[r19]
            boolean r2 = r5.contains(r2)
            if (r2 == 0) goto L_0x00bd
            r2 = 2
            r2 = r20[r2]
            java.lang.String[] r2 = r2.split(r8)
            r2 = r2[r19]
            tryToGroupOrChannelByUserName(r13, r2)
            r24 = r10
            r10 = r6
            goto L_0x00db
        L_0x00bd:
            r2 = 1
            r8 = r0
            r0 = r30
            r18 = 0
            r1 = r31
            r3 = r21
            r19 = r4
            r4 = r22
            r18 = r5
            r5 = r34
            r24 = r10
            r10 = r6
            r6 = r35
            tryToUser(r0, r1, r2, r3, r4, r5, r6)
            goto L_0x00db
        L_0x00d8:
            r24 = r10
            r10 = r6
        L_0x00db:
            goto L_0x00ee
        L_0x00dc:
            r24 = r10
            r10 = r6
            java.lang.String r0 = "m12345.com"
            boolean r0 = r10.equals(r0)
            if (r0 != 0) goto L_0x00fa
            boolean r0 = r10.equals(r9)
            if (r0 == 0) goto L_0x00ee
            goto L_0x00fa
        L_0x00ee:
            r22 = r7
            r27 = r9
            r28 = r10
            r29 = r11
            r15 = r12
            r7 = 0
            goto L_0x02ce
        L_0x00fa:
            boolean r0 = android.text.TextUtils.isEmpty(r7)
            if (r0 != 0) goto L_0x017d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            java.lang.String r5 = "&Key="
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            r7 = r0
            boolean r0 = r14.startsWith(r0)
            if (r0 == 0) goto L_0x0179
            int r0 = r7.length()
            java.lang.String r0 = r14.substring(r0)
            java.lang.String r14 = r0.replace(r4, r8)
            r6 = 0
            byte[] r5 = android.util.Base64.decode(r14, r6)
            java.lang.String r0 = new java.lang.String
            r0.<init>(r5)
            r4 = r0
            java.lang.String[] r20 = r4.split(r3)
            r0 = r20[r6]
            java.lang.String[] r0 = r0.split(r8)
            r1 = 1
            r19 = r0[r1]
            r0 = r20[r1]
            java.lang.String[] r0 = r0.split(r8)
            r21 = r0[r1]
            boolean r0 = r4.contains(r2)
            if (r0 == 0) goto L_0x015a
            r0 = 2
            r0 = r20[r0]
            java.lang.String[] r0 = r0.split(r8)
            r0 = r0[r1]
            tryToGroupOrChannelByUserName(r13, r0)
            r22 = r7
            r7 = 0
            goto L_0x0170
        L_0x015a:
            r2 = 1
            r0 = r30
            r1 = r31
            r3 = r19
            r8 = r4
            r4 = r21
            r18 = r5
            r5 = r34
            r22 = r7
            r7 = 0
            r6 = r35
            tryToUser(r0, r1, r2, r3, r4, r5, r6)
        L_0x0170:
            r27 = r9
            r28 = r10
            r29 = r11
            r15 = r12
            goto L_0x02ce
        L_0x0179:
            r22 = r7
            r7 = 0
            goto L_0x0181
        L_0x017d:
            r0 = r7
            r7 = 0
            r22 = r0
        L_0x0181:
            java.lang.String r0 = r11.getPath()
            if (r0 == 0) goto L_0x02c7
            int r2 = r0.length()
            r3 = 1
            if (r2 <= r3) goto L_0x02c7
            r8 = 0
            r20 = 0
            r21 = 0
            r23 = 0
            r25 = 0
            r2 = 1
            java.lang.String r6 = r0.substring(r2)
            java.lang.String r0 = "login/"
            boolean r2 = r6.startsWith(r0)
            if (r2 == 0) goto L_0x01ac
            java.lang.String r8 = r6.replace(r0, r1)
            r18 = r8
            goto L_0x022c
        L_0x01ac:
            java.lang.String r0 = "joinchat/"
            boolean r2 = r6.startsWith(r0)
            if (r2 == 0) goto L_0x01bc
            java.lang.String r21 = r6.replace(r0, r1)
            r18 = r8
            goto L_0x022c
        L_0x01bc:
            java.lang.String r0 = "g/"
            boolean r1 = r6.startsWith(r0)
            if (r1 != 0) goto L_0x0269
            java.lang.String r1 = "u/"
            boolean r1 = r6.startsWith(r1)
            if (r1 == 0) goto L_0x01d7
            r26 = r6
            r27 = r9
            r28 = r10
            r29 = r11
            r15 = r12
            goto L_0x0272
        L_0x01d7:
            java.lang.String r0 = "authtoken/"
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L_0x01e0
            return
        L_0x01e0:
            java.lang.String r0 = "socks"
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L_0x01f0
            android.content.Context r0 = getContext(r30)
            im.bclpbkiauv.messenger.browser.Browser.openUrl((android.content.Context) r0, (java.lang.String) r12, (boolean) r15)
            return
        L_0x01f0:
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.List r1 = r11.getPathSegments()
            r0.<init>(r1)
            int r1 = r0.size()
            if (r1 <= 0) goto L_0x0210
            java.lang.Object r1 = r0.get(r7)
            java.lang.String r1 = (java.lang.String) r1
            java.lang.String r2 = "s"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0210
            r0.remove(r7)
        L_0x0210:
            int r1 = r0.size()
            if (r1 <= 0) goto L_0x021e
            java.lang.Object r1 = r0.get(r7)
            java.lang.String r1 = (java.lang.String) r1
            r20 = r1
        L_0x021e:
            java.lang.String r1 = "start"
            java.lang.String r23 = r11.getQueryParameter(r1)
            java.lang.String r1 = "startgroup"
            java.lang.String r25 = r11.getQueryParameter(r1)
            r18 = r8
        L_0x022c:
            if (r18 != 0) goto L_0x0240
            if (r20 != 0) goto L_0x0240
            if (r21 != 0) goto L_0x0240
            if (r23 != 0) goto L_0x0240
            if (r25 == 0) goto L_0x0237
            goto L_0x0240
        L_0x0237:
            r27 = r9
            r28 = r10
            r29 = r11
            r15 = r12
            goto L_0x02ce
        L_0x0240:
            r2 = 1
            r17 = 0
            r0 = r30
            r1 = r31
            r3 = r18
            r4 = r20
            r5 = r21
            r8 = r6
            r6 = r23
            r7 = r25
            r26 = r8
            r8 = r34
            r27 = r9
            r9 = r35
            r28 = r10
            r10 = r36
            r29 = r11
            r11 = r37
            r15 = r12
            r12 = r17
            runLinkRequest(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            return
        L_0x0269:
            r26 = r6
            r27 = r9
            r28 = r10
            r29 = r11
            r15 = r12
        L_0x0272:
            java.lang.String r1 = "/"
            int r2 = r14.lastIndexOf(r1)
            r3 = 1
            int r2 = r2 + r3
            java.lang.String r9 = r14.substring(r2)
            byte[] r10 = android.util.Base64.decode(r9, r7)
            java.lang.String r2 = new java.lang.String
            r2.<init>(r10)
            r11 = r26
            boolean r0 = r11.startsWith(r0)
            if (r0 == 0) goto L_0x02a0
            int r0 = r2.lastIndexOf(r1)
            r1 = 1
            int r0 = r0 + r1
            java.lang.String r0 = r2.substring(r0)
            r1 = r0
            tryToGroupOrChannelByUserName(r13, r1)
            r21 = r1
            goto L_0x02c6
        L_0x02a0:
            int r0 = r2.length()
            int r0 = r0 + -4
            java.lang.String r12 = r2.substring(r7, r0)
            java.lang.String r0 = "&"
            r1 = 2
            java.lang.String[] r14 = r12.split(r0, r1)
            r7 = r14[r7]
            r0 = 1
            r17 = r14[r0]
            r2 = 1
            r0 = r30
            r1 = r31
            r3 = r7
            r4 = r17
            r5 = r34
            r6 = r35
            tryToUser(r0, r1, r2, r3, r4, r5, r6)
            r0 = r12
        L_0x02c6:
            return
        L_0x02c7:
            r27 = r9
            r28 = r10
            r29 = r11
            r15 = r12
        L_0x02ce:
            if (r38 == 0) goto L_0x02f0
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.customTabs
            if (r0 == 0) goto L_0x02e3
            im.bclpbkiauv.ui.WebviewActivity r0 = new im.bclpbkiauv.ui.WebviewActivity
            r1 = 0
            r0.<init>((java.lang.String) r15, (java.lang.String) r1)
            r1 = r35
            presentFragment(r13, r0, r7, r1)
            r2 = r39
            r3 = r15
            goto L_0x02f5
        L_0x02e3:
            r1 = r35
            android.content.Context r0 = getContext(r28)
            r2 = r39
            r3 = r15
            im.bclpbkiauv.messenger.browser.Browser.openUrl((android.content.Context) r0, (java.lang.String) r3, (boolean) r2)
            goto L_0x02f5
        L_0x02f0:
            r1 = r35
            r2 = r39
            r3 = r15
        L_0x02f5:
            r7 = r22
        L_0x02f7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.utils.QrCodeParseUtil.tryParseQrCode(java.lang.Object, int, java.lang.String, boolean, boolean, boolean, boolean, boolean, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public static void runLinkRequest(Object hostObj, int currentAccount, boolean showProgressDialog, String code, String username, String group, String botUser, String botChat, boolean removeLast, boolean forceWithoutAnimation, boolean check, boolean preview, int state) {
        AlertDialog progressDialog;
        AlertDialog progressDialog2;
        int i;
        Object obj = hostObj;
        int i2 = currentAccount;
        String str = username;
        String str2 = group;
        int i3 = state;
        if (code == null) {
            if (showProgressDialog) {
                progressDialog = new AlertDialog(getContext(hostObj), 3);
            } else {
                progressDialog = null;
            }
            int requestId = 0;
            AlertDialog finalProgressDialog = progressDialog;
            if (str != null) {
                TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                req.username = str;
                $$Lambda$QrCodeParseUtil$s4qfQBeGbDeSbdZZJADcaCUU5U r13 = r1;
                ConnectionsManager instance = ConnectionsManager.getInstance(currentAccount);
                progressDialog2 = progressDialog;
                $$Lambda$QrCodeParseUtil$s4qfQBeGbDeSbdZZJADcaCUU5U r1 = new RequestDelegate(hostObj, currentAccount, botChat, removeLast, forceWithoutAnimation, check, preview, botUser) {
                    private final /* synthetic */ Object f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ String f$3;
                    private final /* synthetic */ boolean f$4;
                    private final /* synthetic */ boolean f$5;
                    private final /* synthetic */ boolean f$6;
                    private final /* synthetic */ boolean f$7;
                    private final /* synthetic */ String f$8;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                        this.f$7 = r8;
                        this.f$8 = r9;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tL_error, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8) {
                            private final /* synthetic */ TLObject f$1;
                            private final /* synthetic */ String f$10;
                            private final /* synthetic */ TLRPC.TL_error f$2;
                            private final /* synthetic */ Object f$3;
                            private final /* synthetic */ int f$4;
                            private final /* synthetic */ String f$5;
                            private final /* synthetic */ boolean f$6;
                            private final /* synthetic */ boolean f$7;
                            private final /* synthetic */ boolean f$8;
                            private final /* synthetic */ boolean f$9;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r6;
                                this.f$6 = r7;
                                this.f$7 = r8;
                                this.f$8 = r9;
                                this.f$9 = r10;
                                this.f$10 = r11;
                            }

                            public final void run() {
                                QrCodeParseUtil.lambda$null$1(AlertDialog.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
                            }
                        });
                    }
                };
                requestId = instance.sendRequest(req, r13);
                i = currentAccount;
                int i4 = i3;
                String str3 = str2;
                Object obj2 = obj;
                AlertDialog alertDialog = finalProgressDialog;
            } else {
                AlertDialog finalProgressDialog2 = finalProgressDialog;
                progressDialog2 = progressDialog;
                if (str2 == null) {
                    i = currentAccount;
                    int i5 = i3;
                    String str4 = str2;
                    Object obj3 = obj;
                    AlertDialog alertDialog2 = finalProgressDialog2;
                } else if (i3 == 0) {
                    TLRPC.TL_messages_checkChatInvite req2 = new TLRPC.TL_messages_checkChatInvite();
                    req2.hash = str2;
                    $$Lambda$QrCodeParseUtil$y6E6m1164fKz0B3N5vpgnTZ6cDo r15 = r1;
                    TLRPC.TL_messages_checkChatInvite req3 = req2;
                    ConnectionsManager instance2 = ConnectionsManager.getInstance(currentAccount);
                    $$Lambda$QrCodeParseUtil$y6E6m1164fKz0B3N5vpgnTZ6cDo r12 = new RequestDelegate(hostObj, currentAccount, group, showProgressDialog, code, username, botUser, botChat, removeLast, forceWithoutAnimation, check, preview) {
                        private final /* synthetic */ Object f$1;
                        private final /* synthetic */ boolean f$10;
                        private final /* synthetic */ boolean f$11;
                        private final /* synthetic */ boolean f$12;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ String f$3;
                        private final /* synthetic */ boolean f$4;
                        private final /* synthetic */ String f$5;
                        private final /* synthetic */ String f$6;
                        private final /* synthetic */ String f$7;
                        private final /* synthetic */ String f$8;
                        private final /* synthetic */ boolean f$9;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                            this.f$8 = r9;
                            this.f$9 = r10;
                            this.f$10 = r11;
                            this.f$11 = r12;
                            this.f$12 = r13;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable(tL_error, this.f$1, tLObject, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12) {
                                private final /* synthetic */ TLRPC.TL_error f$1;
                                private final /* synthetic */ String f$10;
                                private final /* synthetic */ boolean f$11;
                                private final /* synthetic */ boolean f$12;
                                private final /* synthetic */ boolean f$13;
                                private final /* synthetic */ boolean f$14;
                                private final /* synthetic */ Object f$2;
                                private final /* synthetic */ TLObject f$3;
                                private final /* synthetic */ int f$4;
                                private final /* synthetic */ String f$5;
                                private final /* synthetic */ boolean f$6;
                                private final /* synthetic */ String f$7;
                                private final /* synthetic */ String f$8;
                                private final /* synthetic */ String f$9;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                    this.f$4 = r5;
                                    this.f$5 = r6;
                                    this.f$6 = r7;
                                    this.f$7 = r8;
                                    this.f$8 = r9;
                                    this.f$9 = r10;
                                    this.f$10 = r11;
                                    this.f$11 = r12;
                                    this.f$12 = r13;
                                    this.f$13 = r14;
                                    this.f$14 = r15;
                                }

                                public final void run() {
                                    QrCodeParseUtil.lambda$null$4(AlertDialog.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14);
                                }
                            });
                        }
                    };
                    requestId = instance2.sendRequest(req3, r15, 2);
                    Object obj4 = hostObj;
                    i = currentAccount;
                    String str5 = group;
                    int i6 = state;
                    AlertDialog alertDialog3 = finalProgressDialog2;
                } else if (state == 1) {
                    TLRPC.TL_messages_importChatInvite req4 = new TLRPC.TL_messages_importChatInvite();
                    req4.hash = group;
                    i = currentAccount;
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req4, new RequestDelegate(i, finalProgressDialog2, hostObj) {
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ AlertDialog f$1;
                        private final /* synthetic */ Object f$2;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            QrCodeParseUtil.lambda$runLinkRequest$7(this.f$0, this.f$1, this.f$2, tLObject, tL_error);
                        }
                    }, 2);
                } else {
                    Object obj5 = hostObj;
                    i = currentAccount;
                    String str6 = group;
                    AlertDialog alertDialog4 = finalProgressDialog2;
                }
            }
            if (requestId == 0 || !showProgressDialog) {
                return;
            }
            AlertDialog progressDialog3 = progressDialog2;
            progressDialog3.setOnCancelListener(new DialogInterface.OnCancelListener(i, requestId) {
                private final /* synthetic */ int f$0;
                private final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    ConnectionsManager.getInstance(this.f$0).cancelRequest(this.f$1, true);
                }
            });
            try {
                progressDialog3.show();
            } catch (Exception e) {
            }
        } else if (NotificationCenter.getGlobalInstance().hasObservers(NotificationCenter.didReceiveSmsCode)) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, code);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(hostObj));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("OtherLoginCode", R.string.OtherLoginCode, code)));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(obj, builder.create());
        }
    }

    static /* synthetic */ void lambda$null$1(AlertDialog finalProgressDialog, TLObject response, TLRPC.TL_error error, Object hostObj, int currentAccount, String botChat, boolean removeLast, boolean forceWithoutAnimation, boolean check, boolean preview, String botUser) {
        long dialog_id;
        String str = botUser;
        if (finalProgressDialog != null) {
            try {
                finalProgressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
        if (error != null || getActionLayout(hostObj) == null) {
            Object obj = hostObj;
            boolean z = removeLast;
            boolean z2 = forceWithoutAnimation;
            try {
                AlertsCreator.createSimpleAlert(getContext(hostObj), LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist)).show();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } else {
            MessagesController.getInstance(currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(currentAccount).putChats(res.chats, false);
            MessagesStorage.getInstance(currentAccount).putUsersAndChats(res.users, res.chats, false, true);
            if (botChat != null) {
                TLRPC.User user = !res.users.isEmpty() ? res.users.get(0) : null;
                if (user == null || (user.bot && user.bot_nochats)) {
                    try {
                        ToastUtils.show((int) R.string.BotCantJoinGroups);
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                } else {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 2);
                    args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", R.string.AddToTheGroupTitle, UserObject.getName(user), "%1$s"));
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate(currentAccount, hostObj, user, botChat, removeLast, forceWithoutAnimation, check, preview) {
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ Object f$1;
                        private final /* synthetic */ TLRPC.User f$2;
                        private final /* synthetic */ String f$3;
                        private final /* synthetic */ boolean f$4;
                        private final /* synthetic */ boolean f$5;
                        private final /* synthetic */ boolean f$6;
                        private final /* synthetic */ boolean f$7;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                        }

                        public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                            QrCodeParseUtil.lambda$null$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, dialogsActivity, arrayList, charSequence, z);
                        }
                    });
                    presentFragment(hostObj, fragment, removeLast, forceWithoutAnimation, check, preview);
                    Object obj2 = hostObj;
                    boolean z3 = removeLast;
                    boolean z4 = forceWithoutAnimation;
                }
            } else {
                boolean isBot = false;
                Bundle args2 = new Bundle();
                if (!res.chats.isEmpty()) {
                    args2.putInt("chat_id", res.chats.get(0).id);
                    dialog_id = (long) (-res.chats.get(0).id);
                } else {
                    args2.putInt("user_id", res.users.get(0).id);
                    dialog_id = (long) res.users.get(0).id;
                }
                if (str != null && res.users.size() > 0 && res.users.get(0).bot) {
                    args2.putString("botUser", str);
                    isBot = true;
                }
                BaseFragment lastFragment = getLastFragment(hostObj);
                if (lastFragment != null && !MessagesController.getInstance(currentAccount).checkCanOpenChat(args2, lastFragment)) {
                    Object obj3 = hostObj;
                    boolean z5 = removeLast;
                    boolean z6 = forceWithoutAnimation;
                } else if (!isBot || !(lastFragment instanceof ChatActivity) || ((ChatActivity) lastFragment).getDialogId() != dialog_id) {
                    presentFragment(hostObj, new ChatActivity(args2), removeLast, forceWithoutAnimation);
                } else {
                    ((ChatActivity) lastFragment).setBotUser(str);
                    Object obj4 = hostObj;
                    boolean z7 = removeLast;
                    boolean z8 = forceWithoutAnimation;
                }
            }
        }
    }

    static /* synthetic */ void lambda$null$0(int currentAccount, Object hostObj, TLRPC.User user, String botChat, boolean removeLast, boolean forceWithoutAnimation, boolean check, boolean preview, DialogsActivity fragment12, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args12 = new Bundle();
        args12.putBoolean("scrollToTopOnResume", true);
        args12.putInt("chat_id", -((int) did));
        if (MessagesController.getInstance(currentAccount).checkCanOpenChat(args12, getLastFragment(hostObj))) {
            MessagesController.getInstance(currentAccount).addUserToChat(-((int) did), user, (TLRPC.ChatFull) null, 0, botChat, (BaseFragment) null, (Runnable) null);
            presentFragment(hostObj, new ChatActivity(args12), removeLast, forceWithoutAnimation, check, preview);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00f3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$null$4(im.bclpbkiauv.ui.actionbar.AlertDialog r17, im.bclpbkiauv.tgnet.TLRPC.TL_error r18, java.lang.Object r19, im.bclpbkiauv.tgnet.TLObject r20, int r21, java.lang.String r22, boolean r23, java.lang.String r24, java.lang.String r25, java.lang.String r26, java.lang.String r27, boolean r28, boolean r29, boolean r30, boolean r31) {
        /*
            r1 = r18
            r15 = r19
            if (r17 == 0) goto L_0x0011
            r17.dismiss()     // Catch:{ Exception -> 0x000a }
            goto L_0x0011
        L_0x000a:
            r0 = move-exception
            r2 = r0
            r0 = r2
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0012
        L_0x0011:
        L_0x0012:
            r0 = 2131692462(0x7f0f0bae, float:1.9014025E38)
            java.lang.String r2 = "OK"
            r3 = 2131689824(0x7f0f0160, float:1.9008674E38)
            java.lang.String r4 = "AppName"
            r14 = 0
            if (r1 != 0) goto L_0x0143
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r5 = getActionLayout(r19)
            if (r5 == 0) goto L_0x0143
            r13 = r20
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r13 = (im.bclpbkiauv.tgnet.TLRPC.ChatInvite) r13
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            r6 = 1
            r7 = 0
            if (r5 == 0) goto L_0x008f
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            boolean r5 = im.bclpbkiauv.messenger.ChatObject.isLeftFromChat(r5)
            if (r5 == 0) goto L_0x0047
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            boolean r5 = r5.kicked
            if (r5 != 0) goto L_0x008f
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            java.lang.String r5 = r5.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x008f
        L_0x0047:
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r21)
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r13.chat
            r0.putChat(r2, r7)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r13.chat
            r0.add(r2)
            im.bclpbkiauv.messenger.MessagesStorage r2 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r21)
            r2.putUsersAndChats(r14, r0, r7, r6)
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            r8 = r2
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r13.chat
            int r2 = r2.id
            java.lang.String r3 = "chat_id"
            r8.putInt(r3, r2)
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r21)
            im.bclpbkiauv.ui.actionbar.BaseFragment r3 = getLastFragment(r19)
            boolean r2 = r2.checkCanOpenChat(r8, r3)
            if (r2 == 0) goto L_0x008c
            im.bclpbkiauv.ui.ChatActivity r3 = new im.bclpbkiauv.ui.ChatActivity
            r3.<init>(r8)
            r4 = 0
            r5 = 1
            r6 = 1
            r7 = 0
            r2 = r19
            presentFragment(r2, r3, r4, r5, r6, r7)
        L_0x008c:
            r5 = r15
            goto L_0x0140
        L_0x008f:
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            if (r5 != 0) goto L_0x009b
            boolean r5 = r13.channel
            if (r5 == 0) goto L_0x00b1
            boolean r5 = r13.megagroup
            if (r5 != 0) goto L_0x00b1
        L_0x009b:
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            if (r5 == 0) goto L_0x00d2
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            boolean r5 = im.bclpbkiauv.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x00b1
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            boolean r5 = r5.megagroup
            if (r5 == 0) goto L_0x00ae
            goto L_0x00b1
        L_0x00ae:
            r12 = r22
            goto L_0x00d4
        L_0x00b1:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r5 = getActionLayout(r19)
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r5 = r5.fragmentsStack
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x00d2
            im.bclpbkiauv.ui.actionbar.BaseFragment r0 = getLastFragment(r19)
            im.bclpbkiauv.ui.components.JoinGroupAlert r2 = new im.bclpbkiauv.ui.components.JoinGroupAlert
            android.content.Context r3 = getContext(r19)
            r12 = r22
            r2.<init>(r3, r13, r12, r0)
            r0.showDialog(r2)
            r5 = r15
            goto L_0x0140
        L_0x00d2:
            r12 = r22
        L_0x00d4:
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r5 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder
            android.content.Context r8 = getContext(r19)
            r5.<init>((android.content.Context) r8)
            r11 = r5
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r11.setTitle(r3)
            r3 = 2131690431(0x7f0f03bf, float:1.9009905E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            if (r5 == 0) goto L_0x00f3
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r13.chat
            java.lang.String r5 = r5.title
            goto L_0x00f5
        L_0x00f3:
            java.lang.String r5 = r13.title
        L_0x00f5:
            r4[r7] = r5
            java.lang.String r5 = "ChannelJoinTo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r3, r4)
            r11.setMessage(r3)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            im.bclpbkiauv.ui.utils.-$$Lambda$QrCodeParseUtil$8qYVHFV7Mg5fxFdOWS15xNGX9bw r10 = new im.bclpbkiauv.ui.utils.-$$Lambda$QrCodeParseUtil$8qYVHFV7Mg5fxFdOWS15xNGX9bw
            r2 = r10
            r3 = r19
            r4 = r21
            r5 = r23
            r6 = r24
            r7 = r25
            r8 = r22
            r9 = r26
            r1 = r10
            r10 = r27
            r15 = r11
            r11 = r28
            r12 = r29
            r16 = r13
            r13 = r30
            r14 = r31
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
            r15.setPositiveButton(r0, r1)
            r0 = 2131690308(0x7f0f0344, float:1.9009656E38)
            java.lang.String r1 = "Cancel"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r1 = 0
            r15.setNegativeButton(r0, r1)
            im.bclpbkiauv.ui.actionbar.AlertDialog r0 = r15.create()
            r5 = r19
            r1 = r15
            showDialog(r5, r0)
        L_0x0140:
            r3 = r18
            goto L_0x0188
        L_0x0143:
            r1 = r14
            r5 = r15
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r6 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder
            android.content.Context r7 = getContext(r19)
            r6.<init>((android.content.Context) r7)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r6.setTitle(r3)
            r3 = r18
            java.lang.String r4 = r3.text
            java.lang.String r7 = "FLOOD_WAIT"
            boolean r4 = r4.startsWith(r7)
            if (r4 == 0) goto L_0x016e
            r4 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r7 = "FloodWait"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r4)
            r6.setMessage(r4)
            goto L_0x017a
        L_0x016e:
            r4 = 2131691729(0x7f0f08d1, float:1.9012538E38)
            java.lang.String r7 = "JoinToGroupErrorNotExist"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r4)
            r6.setMessage(r4)
        L_0x017a:
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            r6.setPositiveButton(r0, r1)
            im.bclpbkiauv.ui.actionbar.AlertDialog r0 = r6.create()
            showDialog(r5, r0)
        L_0x0188:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.utils.QrCodeParseUtil.lambda$null$4(im.bclpbkiauv.ui.actionbar.AlertDialog, im.bclpbkiauv.tgnet.TLRPC$TL_error, java.lang.Object, im.bclpbkiauv.tgnet.TLObject, int, java.lang.String, boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, boolean, boolean, boolean):void");
    }

    static /* synthetic */ void lambda$runLinkRequest$7(int currentAccount, AlertDialog finalProgressDialog, Object hostObj, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            MessagesController.getInstance(currentAccount).processUpdates((TLRPC.Updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(error, hostObj, response, currentAccount) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ Object f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                QrCodeParseUtil.lambda$null$6(AlertDialog.this, this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    static /* synthetic */ void lambda$null$6(AlertDialog finalProgressDialog, TLRPC.TL_error error, Object hostObj, TLObject response, int currentAccount) {
        if (finalProgressDialog != null) {
            try {
                finalProgressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (error != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(hostObj));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (error.text.startsWith("FLOOD_WAIT")) {
                builder.setMessage(LocaleController.getString("FloodWait", R.string.FloodWait));
            } else if (error.text.equals("USERS_TOO_MUCH")) {
                builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", R.string.JoinToGroupErrorFull));
            } else {
                builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(hostObj, builder.create());
        } else if (getActionLayout(hostObj) != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            if (!updates.chats.isEmpty()) {
                TLRPC.Chat chat = updates.chats.get(0);
                chat.left = false;
                chat.kicked = false;
                MessagesController.getInstance(currentAccount).putUsers(updates.users, false);
                MessagesController.getInstance(currentAccount).putChats(updates.chats, false);
                Bundle args = new Bundle();
                args.putInt("chat_id", chat.id);
                if (MessagesController.getInstance(currentAccount).checkCanOpenChat(args, getLastFragment(hostObj))) {
                    presentFragment(hostObj, new ChatActivity(args), false, true, true, false);
                }
            }
        }
    }

    private static void tryToGroupOrChannelByUserName(Object hostObj, String userName) {
        MessagesController.getInstance(UserConfig.selectedAccount).openByUserName(userName, getLastFragment(hostObj), 1, true);
    }

    private static void tryToUser(Object hostObj, int currentAccount, boolean showProgressDialog, String userId, String userHash, boolean removeLast, boolean forceWithoutAnimation) {
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userHash)) {
            TLRPC.User user = new TLRPC.TL_user();
            user.id = Utilities.parseInt(userId).intValue();
            user.access_hash = Utilities.parseLong(userHash).longValue();
            TLRPC.UserFull userFull = getMessagesController(currentAccount).getUserFull(user.id);
            if (userFull != null) {
                toUser(hostObj, currentAccount, true, userFull, removeLast, forceWithoutAnimation);
                return;
            }
            TLRPC.TL_users_getFullUser req = new TLRPC.TL_users_getFullUser();
            req.id = getMessagesController(currentAccount).getInputUser(user);
            AlertDialog progressDialog = null;
            if (showProgressDialog) {
                progressDialog = new AlertDialog(getContext(hostObj), 3);
                showDialog(hostObj, progressDialog);
            }
            int reqId = getConnectionsManager(currentAccount).sendRequest(req, new RequestDelegate(hostObj, currentAccount, removeLast, forceWithoutAnimation) {
                private final /* synthetic */ Object f$0;
                private final /* synthetic */ int f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(this.f$0, this.f$1, tLObject, this.f$2, this.f$3) {
                        private final /* synthetic */ Object f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ TLObject f$3;
                        private final /* synthetic */ boolean f$4;
                        private final /* synthetic */ boolean f$5;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                        }

                        public final void run() {
                            QrCodeParseUtil.lambda$null$9(TLRPC.TL_error.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                        }
                    });
                }
            });
            if (progressDialog != null) {
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(currentAccount, reqId) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ int f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        QrCodeParseUtil.getConnectionsManager(this.f$0).cancelRequest(this.f$1, false);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$null$9(TLRPC.TL_error error, Object hostObj, int currentAccount, TLObject response, boolean removeLast, boolean forceWithoutAnimation) {
        if (error == null) {
            toUser(hostObj, currentAccount, false, (TLRPC.UserFull) response, removeLast, forceWithoutAnimation);
            return;
        }
        ToastUtils.show((int) R.string.NoUsernameFound);
    }

    private static void toUser(Object hostObj, int currentAccount, boolean fromCache, TLRPC.UserFull userFull, boolean removeLast, boolean forceWithoutAnimation) {
        getMessagesController(currentAccount).putUser(userFull.user, false);
        if (userFull.user.self || userFull.user.contact) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", userFull.user.id);
            presentFragment(hostObj, new NewProfileActivity(bundle), removeLast, forceWithoutAnimation);
            return;
        }
        Bundle bundle2 = new Bundle();
        bundle2.putInt("from_type", 1);
        presentFragment(hostObj, new AddContactsInfoActivity(bundle2, userFull.user), removeLast, forceWithoutAnimation);
    }

    private static void presentFragment(Object hostObj, BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        presentFragment(hostObj, fragment, removeLast, forceWithoutAnimation, true, false);
    }

    private static void presentFragment(Object hostObj, BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, boolean check, boolean preview) {
        ActionBarLayout actionBarLayout;
        if (fragment != null && (actionBarLayout = getActionLayout(hostObj)) != null) {
            actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, check, preview);
        }
    }

    private static MessagesController getMessagesController(int currentAccount) {
        return MessagesController.getInstance(currentAccount);
    }

    private static MessagesStorage getMessagesStorage(int currentAccount) {
        return MessagesStorage.getInstance(currentAccount);
    }

    private static ConnectionsManager getConnectionsManager(int currentAccount) {
        return ConnectionsManager.getInstance(currentAccount);
    }

    private static Dialog showDialog(Object host, Dialog dialog) {
        if (dialog == null) {
            return null;
        }
        if (host instanceof BaseFragment) {
            return ((BaseFragment) host).showDialog(dialog);
        }
        if (host instanceof BaseFmts) {
            return ((BaseFmts) host).showDialog(dialog);
        }
        if ((host instanceof LaunchActivity) && (dialog instanceof AlertDialog)) {
            return ((LaunchActivity) host).showAlertDialog((AlertDialog) dialog);
        }
        dialog.show();
        return dialog;
    }

    private static void checkHost(Object host) {
        if (!(host instanceof BaseFragment) && !(host instanceof BaseFmts) && !(host instanceof Activity) && !(host instanceof Fragment) && !(host instanceof View) && !(host instanceof Context)) {
            throw new IllegalArgumentException("host must be one of the BaseFragment, BaseFmts, Activity, Fragment, View");
        }
    }

    private static BaseFragment getLastFragment(Object host) {
        ActionBarLayout actionBarLayout = getActionLayout(host);
        if (actionBarLayout != null) {
            return actionBarLayout.getLastFragment();
        }
        return null;
    }

    private static ActionBarLayout getActionLayout(Object host) {
        if (host instanceof BaseFragment) {
            return ((BaseFragment) host).getParentLayout();
        }
        if (host instanceof BaseFmts) {
            return ((BaseFmts) host).getActionBarLayout();
        }
        if (host instanceof LaunchActivity) {
            return ((LaunchActivity) host).getActionBarLayout();
        }
        if (host instanceof ActionBarLayout) {
            return (ActionBarLayout) host;
        }
        return null;
    }

    private static Context getContext(Object host) {
        if (host instanceof BaseFragment) {
            return ((BaseFragment) host).getParentActivity();
        }
        if (host instanceof BaseFmts) {
            return ((BaseFmts) host).getParentActivity();
        }
        if (host instanceof Activity) {
            return (Activity) host;
        }
        if (host instanceof Fragment) {
            return ((Fragment) host).getActivity();
        }
        if (host instanceof View) {
            return ((View) host).getContext();
        }
        if (host instanceof Context) {
            return (Context) host;
        }
        return null;
    }
}
