package im.bclpbkiauv.messenger;

import android.text.TextUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.tgnet.TLRPC;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class WearDataLayerListenerService extends WearableListenerService {
    private static boolean watchConnected;
    private int currentAccount = UserConfig.selectedAccount;

    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("WearableDataLayer service created");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("WearableDataLayer service destroyed");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:133:0x02a6  */
    /* JADX WARNING: Removed duplicated region for block: B:140:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:43:0x0142=Splitter:B:43:0x0142, B:23:0x00c3=Splitter:B:23:0x00c3} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onChannelOpened(com.google.android.gms.wearable.Channel r20) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            com.google.android.gms.common.api.GoogleApiClient$Builder r0 = new com.google.android.gms.common.api.GoogleApiClient$Builder
            r0.<init>(r1)
            com.google.android.gms.common.api.Api<com.google.android.gms.wearable.Wearable$WearableOptions> r3 = com.google.android.gms.wearable.Wearable.API
            com.google.android.gms.common.api.GoogleApiClient$Builder r0 = r0.addApi(r3)
            com.google.android.gms.common.api.GoogleApiClient r3 = r0.build()
            com.google.android.gms.common.ConnectionResult r0 = r3.blockingConnect()
            boolean r0 = r0.isSuccess()
            if (r0 != 0) goto L_0x0027
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0026
            java.lang.String r0 = "failed to connect google api client"
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r0)
        L_0x0026:
            return
        L_0x0027:
            java.lang.String r4 = r20.getPath()
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0044
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "wear channel path: "
            r0.append(r5)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x0044:
            java.lang.String r0 = "/getCurrentUser"
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x028e }
            r5 = 2
            r6 = 1
            r7 = 0
            if (r0 == 0) goto L_0x010e
            java.io.DataOutputStream r0 = new java.io.DataOutputStream     // Catch:{ Exception -> 0x028e }
            java.io.BufferedOutputStream r8 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x028e }
            com.google.android.gms.common.api.PendingResult r9 = r2.getOutputStream(r3)     // Catch:{ Exception -> 0x028e }
            com.google.android.gms.common.api.Result r9 = r9.await()     // Catch:{ Exception -> 0x028e }
            com.google.android.gms.wearable.Channel$GetOutputStreamResult r9 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r9     // Catch:{ Exception -> 0x028e }
            java.io.OutputStream r9 = r9.getOutputStream()     // Catch:{ Exception -> 0x028e }
            r8.<init>(r9)     // Catch:{ Exception -> 0x028e }
            r0.<init>(r8)     // Catch:{ Exception -> 0x028e }
            r8 = r0
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)     // Catch:{ Exception -> 0x028e }
            boolean r0 = r0.isClientActivated()     // Catch:{ Exception -> 0x028e }
            if (r0 == 0) goto L_0x0103
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r0.getCurrentUser()     // Catch:{ Exception -> 0x028e }
            r9 = r0
            int r0 = r9.id     // Catch:{ Exception -> 0x028e }
            r8.writeInt(r0)     // Catch:{ Exception -> 0x028e }
            java.lang.String r0 = r9.first_name     // Catch:{ Exception -> 0x028e }
            r8.writeUTF(r0)     // Catch:{ Exception -> 0x028e }
            java.lang.String r0 = r9.last_name     // Catch:{ Exception -> 0x028e }
            r8.writeUTF(r0)     // Catch:{ Exception -> 0x028e }
            java.lang.String r0 = r9.phone     // Catch:{ Exception -> 0x028e }
            r8.writeUTF(r0)     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r0 = r9.photo     // Catch:{ Exception -> 0x028e }
            if (r0 == 0) goto L_0x00ff
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r0 = r9.photo     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ Exception -> 0x028e }
            java.io.File r0 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r0, r6)     // Catch:{ Exception -> 0x028e }
            r6 = r0
            java.util.concurrent.CyclicBarrier r0 = new java.util.concurrent.CyclicBarrier     // Catch:{ Exception -> 0x028e }
            r0.<init>(r5)     // Catch:{ Exception -> 0x028e }
            r5 = r0
            boolean r0 = r6.exists()     // Catch:{ Exception -> 0x028e }
            if (r0 != 0) goto L_0x00cb
            im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$euiMVvas8EjfB-huggPYuYXBiGo r0 = new im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$euiMVvas8EjfB-huggPYuYXBiGo     // Catch:{ Exception -> 0x028e }
            r0.<init>(r6, r5)     // Catch:{ Exception -> 0x028e }
            r10 = r0
            im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$bMT_0n-nwA1Fw3aud1sHWcK4Fz4 r0 = new im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$bMT_0n-nwA1Fw3aud1sHWcK4Fz4     // Catch:{ Exception -> 0x028e }
            r0.<init>(r10, r9)     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x028e }
            r11 = 10
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ Exception -> 0x00c2 }
            r5.await(r11, r0)     // Catch:{ Exception -> 0x00c2 }
            goto L_0x00c3
        L_0x00c2:
            r0 = move-exception
        L_0x00c3:
            im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$HA07xrNbSVkP3AHePP-Rs3fqXys r0 = new im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$HA07xrNbSVkP3AHePP-Rs3fqXys     // Catch:{ Exception -> 0x028e }
            r0.<init>(r10)     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x028e }
        L_0x00cb:
            boolean r0 = r6.exists()     // Catch:{ Exception -> 0x028e }
            if (r0 == 0) goto L_0x00fb
            long r10 = r6.length()     // Catch:{ Exception -> 0x028e }
            r12 = 52428800(0x3200000, double:2.5903269E-316)
            int r0 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r0 > 0) goto L_0x00fb
            long r10 = r6.length()     // Catch:{ Exception -> 0x028e }
            int r0 = (int) r10     // Catch:{ Exception -> 0x028e }
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x028e }
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ Exception -> 0x028e }
            r7.<init>(r6)     // Catch:{ Exception -> 0x028e }
            java.io.DataInputStream r10 = new java.io.DataInputStream     // Catch:{ Exception -> 0x028e }
            r10.<init>(r7)     // Catch:{ Exception -> 0x028e }
            r10.readFully(r0)     // Catch:{ Exception -> 0x028e }
            r7.close()     // Catch:{ Exception -> 0x028e }
            int r10 = r0.length     // Catch:{ Exception -> 0x028e }
            r8.writeInt(r10)     // Catch:{ Exception -> 0x028e }
            r8.write(r0)     // Catch:{ Exception -> 0x028e }
            goto L_0x00fe
        L_0x00fb:
            r8.writeInt(r7)     // Catch:{ Exception -> 0x028e }
        L_0x00fe:
            goto L_0x0102
        L_0x00ff:
            r8.writeInt(r7)     // Catch:{ Exception -> 0x028e }
        L_0x0102:
            goto L_0x0106
        L_0x0103:
            r8.writeInt(r7)     // Catch:{ Exception -> 0x028e }
        L_0x0106:
            r8.flush()     // Catch:{ Exception -> 0x028e }
            r8.close()     // Catch:{ Exception -> 0x028e }
            goto L_0x028d
        L_0x010e:
            java.lang.String r0 = "/waitForAuthCode"
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x028e }
            if (r0 == 0) goto L_0x017d
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r0)     // Catch:{ Exception -> 0x028e }
            r0.setAppPaused(r7, r7)     // Catch:{ Exception -> 0x028e }
            r0 = 0
            java.lang.String[] r0 = new java.lang.String[]{r0}     // Catch:{ Exception -> 0x028e }
            r8 = r0
            java.util.concurrent.CyclicBarrier r0 = new java.util.concurrent.CyclicBarrier     // Catch:{ Exception -> 0x028e }
            r0.<init>(r5)     // Catch:{ Exception -> 0x028e }
            r5 = r0
            im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$KZ65AsAAipM3Hw2PA-9w6vcbLw0 r0 = new im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$KZ65AsAAipM3Hw2PA-9w6vcbLw0     // Catch:{ Exception -> 0x028e }
            r0.<init>(r8, r5)     // Catch:{ Exception -> 0x028e }
            r9 = r0
            im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$tF-T_w0q9jJiFSV2ttyJPIiD-sg r0 = new im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$tF-T_w0q9jJiFSV2ttyJPIiD-sg     // Catch:{ Exception -> 0x028e }
            r0.<init>(r9)     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x028e }
            r10 = 30
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ Exception -> 0x0141 }
            r5.await(r10, r0)     // Catch:{ Exception -> 0x0141 }
            goto L_0x0142
        L_0x0141:
            r0 = move-exception
        L_0x0142:
            im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$7cHt9LFN7k-UECe1P0kp3jYLKGc r0 = new im.bclpbkiauv.messenger.-$$Lambda$WearDataLayerListenerService$7cHt9LFN7k-UECe1P0kp3jYLKGc     // Catch:{ Exception -> 0x028e }
            r0.<init>(r9)     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x028e }
            java.io.DataOutputStream r0 = new java.io.DataOutputStream     // Catch:{ Exception -> 0x028e }
            com.google.android.gms.common.api.PendingResult r10 = r2.getOutputStream(r3)     // Catch:{ Exception -> 0x028e }
            com.google.android.gms.common.api.Result r10 = r10.await()     // Catch:{ Exception -> 0x028e }
            com.google.android.gms.wearable.Channel$GetOutputStreamResult r10 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r10     // Catch:{ Exception -> 0x028e }
            java.io.OutputStream r10 = r10.getOutputStream()     // Catch:{ Exception -> 0x028e }
            r0.<init>(r10)     // Catch:{ Exception -> 0x028e }
            r10 = r8[r7]     // Catch:{ Exception -> 0x028e }
            if (r10 == 0) goto L_0x0167
            r10 = r8[r7]     // Catch:{ Exception -> 0x028e }
            r0.writeUTF(r10)     // Catch:{ Exception -> 0x028e }
            goto L_0x016c
        L_0x0167:
            java.lang.String r10 = ""
            r0.writeUTF(r10)     // Catch:{ Exception -> 0x028e }
        L_0x016c:
            r0.flush()     // Catch:{ Exception -> 0x028e }
            r0.close()     // Catch:{ Exception -> 0x028e }
            int r10 = r1.currentAccount     // Catch:{ Exception -> 0x028e }
            im.bclpbkiauv.tgnet.ConnectionsManager r10 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r10)     // Catch:{ Exception -> 0x028e }
            r10.setAppPaused(r6, r7)     // Catch:{ Exception -> 0x028e }
            goto L_0x028c
        L_0x017d:
            java.lang.String r0 = "/getChatPhoto"
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x028e }
            if (r0 == 0) goto L_0x028c
            java.io.DataInputStream r0 = new java.io.DataInputStream     // Catch:{ Exception -> 0x028a }
            com.google.android.gms.common.api.PendingResult r5 = r2.getInputStream(r3)     // Catch:{ Exception -> 0x028a }
            com.google.android.gms.common.api.Result r5 = r5.await()     // Catch:{ Exception -> 0x028a }
            com.google.android.gms.wearable.Channel$GetInputStreamResult r5 = (com.google.android.gms.wearable.Channel.GetInputStreamResult) r5     // Catch:{ Exception -> 0x028a }
            java.io.InputStream r5 = r5.getInputStream()     // Catch:{ Exception -> 0x028a }
            r0.<init>(r5)     // Catch:{ Exception -> 0x028a }
            r5 = r0
            java.io.DataOutputStream r0 = new java.io.DataOutputStream     // Catch:{ all -> 0x027b }
            com.google.android.gms.common.api.PendingResult r8 = r2.getOutputStream(r3)     // Catch:{ all -> 0x027b }
            com.google.android.gms.common.api.Result r8 = r8.await()     // Catch:{ all -> 0x027b }
            com.google.android.gms.wearable.Channel$GetOutputStreamResult r8 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r8     // Catch:{ all -> 0x027b }
            java.io.OutputStream r8 = r8.getOutputStream()     // Catch:{ all -> 0x027b }
            r0.<init>(r8)     // Catch:{ all -> 0x027b }
            r8 = r0
            java.lang.String r0 = r5.readUTF()     // Catch:{ all -> 0x026c }
            org.json.JSONObject r9 = new org.json.JSONObject     // Catch:{ all -> 0x026c }
            r9.<init>(r0)     // Catch:{ all -> 0x026c }
            java.lang.String r10 = "chat_id"
            int r10 = r9.getInt(r10)     // Catch:{ all -> 0x026c }
            java.lang.String r11 = "account_id"
            int r11 = r9.getInt(r11)     // Catch:{ all -> 0x026c }
            r12 = -1
            r13 = 0
        L_0x01c4:
            int r14 = im.bclpbkiauv.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ all -> 0x026c }
            if (r13 >= r14) goto L_0x01d9
            im.bclpbkiauv.messenger.UserConfig r14 = im.bclpbkiauv.messenger.UserConfig.getInstance(r13)     // Catch:{ all -> 0x026c }
            int r14 = r14.getClientUserId()     // Catch:{ all -> 0x026c }
            if (r14 != r11) goto L_0x01d6
            r12 = r13
            goto L_0x01d9
        L_0x01d6:
            int r13 = r13 + 1
            goto L_0x01c4
        L_0x01d9:
            r13 = -1
            if (r12 == r13) goto L_0x025d
            r13 = 0
            if (r10 <= 0) goto L_0x01f7
            im.bclpbkiauv.messenger.MessagesController r14 = im.bclpbkiauv.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x026c }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r10)     // Catch:{ all -> 0x026c }
            im.bclpbkiauv.tgnet.TLRPC$User r14 = r14.getUser(r15)     // Catch:{ all -> 0x026c }
            if (r14 == 0) goto L_0x01f6
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r15 = r14.photo     // Catch:{ all -> 0x026c }
            if (r15 == 0) goto L_0x01f6
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r15 = r14.photo     // Catch:{ all -> 0x026c }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r15 = r15.photo_small     // Catch:{ all -> 0x026c }
            r13 = r15
        L_0x01f6:
            goto L_0x020f
        L_0x01f7:
            im.bclpbkiauv.messenger.MessagesController r14 = im.bclpbkiauv.messenger.MessagesController.getInstance(r12)     // Catch:{ all -> 0x026c }
            int r15 = -r10
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)     // Catch:{ all -> 0x026c }
            im.bclpbkiauv.tgnet.TLRPC$Chat r14 = r14.getChat(r15)     // Catch:{ all -> 0x026c }
            if (r14 == 0) goto L_0x020f
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r15 = r14.photo     // Catch:{ all -> 0x026c }
            if (r15 == 0) goto L_0x020f
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r15 = r14.photo     // Catch:{ all -> 0x026c }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r15 = r15.photo_small     // Catch:{ all -> 0x026c }
            r13 = r15
        L_0x020f:
            if (r13 == 0) goto L_0x0257
            java.io.File r6 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r13, r6)     // Catch:{ all -> 0x026c }
            boolean r14 = r6.exists()     // Catch:{ all -> 0x026c }
            if (r14 == 0) goto L_0x0251
            long r14 = r6.length()     // Catch:{ all -> 0x026c }
            r16 = 102400(0x19000, double:5.05923E-319)
            int r18 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r18 >= 0) goto L_0x0251
            long r14 = r6.length()     // Catch:{ all -> 0x026c }
            int r15 = (int) r14     // Catch:{ all -> 0x026c }
            r8.writeInt(r15)     // Catch:{ all -> 0x026c }
            java.io.FileInputStream r14 = new java.io.FileInputStream     // Catch:{ all -> 0x026c }
            r14.<init>(r6)     // Catch:{ all -> 0x026c }
            r15 = 10240(0x2800, float:1.4349E-41)
            byte[] r15 = new byte[r15]     // Catch:{ all -> 0x026c }
        L_0x0237:
            int r16 = r14.read(r15)     // Catch:{ all -> 0x026c }
            r17 = r16
            if (r16 <= 0) goto L_0x0249
            r16 = r0
            r0 = r17
            r8.write(r15, r7, r0)     // Catch:{ all -> 0x026c }
            r0 = r16
            goto L_0x0237
        L_0x0249:
            r16 = r0
            r0 = r17
            r14.close()     // Catch:{ all -> 0x026c }
            goto L_0x0256
        L_0x0251:
            r16 = r0
            r8.writeInt(r7)     // Catch:{ all -> 0x026c }
        L_0x0256:
            goto L_0x025c
        L_0x0257:
            r16 = r0
            r8.writeInt(r7)     // Catch:{ all -> 0x026c }
        L_0x025c:
            goto L_0x0262
        L_0x025d:
            r16 = r0
            r8.writeInt(r7)     // Catch:{ all -> 0x026c }
        L_0x0262:
            r8.flush()     // Catch:{ all -> 0x026c }
            r8.close()     // Catch:{ all -> 0x027b }
            r5.close()     // Catch:{ Exception -> 0x028a }
            goto L_0x028d
        L_0x026c:
            r0 = move-exception
            r6 = r0
            throw r6     // Catch:{ all -> 0x026f }
        L_0x026f:
            r0 = move-exception
            r7 = r0
            r8.close()     // Catch:{ all -> 0x0275 }
            goto L_0x027a
        L_0x0275:
            r0 = move-exception
            r9 = r0
            r6.addSuppressed(r9)     // Catch:{ all -> 0x027b }
        L_0x027a:
            throw r7     // Catch:{ all -> 0x027b }
        L_0x027b:
            r0 = move-exception
            r6 = r0
            throw r6     // Catch:{ all -> 0x027e }
        L_0x027e:
            r0 = move-exception
            r7 = r0
            r5.close()     // Catch:{ all -> 0x0284 }
            goto L_0x0289
        L_0x0284:
            r0 = move-exception
            r8 = r0
            r6.addSuppressed(r8)     // Catch:{ Exception -> 0x028a }
        L_0x0289:
            throw r7     // Catch:{ Exception -> 0x028a }
        L_0x028a:
            r0 = move-exception
            goto L_0x028d
        L_0x028c:
        L_0x028d:
            goto L_0x0298
        L_0x028e:
            r0 = move-exception
            boolean r5 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r5 == 0) goto L_0x0298
            java.lang.String r5 = "error processing wear request"
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r5, (java.lang.Throwable) r0)
        L_0x0298:
            com.google.android.gms.common.api.PendingResult r0 = r2.close(r3)
            r0.await()
            r3.disconnect()
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02ab
            java.lang.String r0 = "WearableDataLayer channel thread exiting"
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x02ab:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.WearDataLayerListenerService.onChannelOpened(com.google.android.gms.wearable.Channel):void");
    }

    static /* synthetic */ void lambda$onChannelOpened$0(File photo, CyclicBarrier barrier, int id, int account, Object[] args) {
        if (id == NotificationCenter.fileDidLoad) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("file loaded: " + args[0] + " " + args[0].getClass().getName());
            }
            if (args[0].equals(photo.getName())) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("LOADED USER PHOTO");
                }
                try {
                    barrier.await(10, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                }
            }
        }
    }

    public /* synthetic */ void lambda$onChannelOpened$1$WearDataLayerListenerService(NotificationCenter.NotificationCenterDelegate listener, TLRPC.User user) {
        NotificationCenter.getInstance(this.currentAccount).addObserver(listener, NotificationCenter.fileDidLoad);
        FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForUser(user, false), user, (String) null, 1, 1);
    }

    public /* synthetic */ void lambda$onChannelOpened$2$WearDataLayerListenerService(NotificationCenter.NotificationCenterDelegate listener) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(listener, NotificationCenter.fileDidLoad);
    }

    static /* synthetic */ void lambda$onChannelOpened$3(String[] code, CyclicBarrier barrier, int id, int account, Object[] args) {
        if (id == NotificationCenter.didReceiveNewMessages && args[0].longValue() == 777000) {
            ArrayList<MessageObject> arr = args[1];
            if (arr.size() > 0) {
                MessageObject msg = arr.get(0);
                if (!TextUtils.isEmpty(msg.messageText)) {
                    Matcher matcher = Pattern.compile("[0-9]+").matcher(msg.messageText);
                    if (matcher.find()) {
                        code[0] = matcher.group();
                        try {
                            barrier.await(10, TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$onChannelOpened$4$WearDataLayerListenerService(NotificationCenter.NotificationCenterDelegate listener) {
        NotificationCenter.getInstance(this.currentAccount).addObserver(listener, NotificationCenter.didReceiveNewMessages);
    }

    public /* synthetic */ void lambda$onChannelOpened$5$WearDataLayerListenerService(NotificationCenter.NotificationCenterDelegate listener) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(listener, NotificationCenter.didReceiveNewMessages);
    }

    public void onMessageReceived(MessageEvent messageEvent) {
        if ("/reply".equals(messageEvent.getPath())) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    WearDataLayerListenerService.lambda$onMessageReceived$6(MessageEvent.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$onMessageReceived$6(MessageEvent messageEvent) {
        int currentAccount2;
        try {
            ApplicationLoader.postInitApplication();
            JSONObject r = new JSONObject(new String(messageEvent.getData(), "UTF-8"));
            CharSequence text = r.getString("text");
            if (text == null) {
                return;
            }
            if (text.length() != 0) {
                long dialog_id = r.getLong("chat_id");
                int max_id = r.getInt("max_id");
                int accountID = r.getInt("account_id");
                int i = 0;
                while (true) {
                    if (i >= UserConfig.getActivatedAccountsCount()) {
                        currentAccount2 = -1;
                        break;
                    } else if (UserConfig.getInstance(i).getClientUserId() == accountID) {
                        currentAccount2 = i;
                        break;
                    } else {
                        i++;
                    }
                }
                if (dialog_id == 0 || max_id == 0) {
                    int i2 = currentAccount2;
                    int i3 = accountID;
                } else if (currentAccount2 == -1) {
                    int i4 = currentAccount2;
                    int i5 = accountID;
                } else {
                    int i6 = accountID;
                    SendMessagesHelper.getInstance(currentAccount2).sendMessage(text.toString(), dialog_id, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                    MessagesController.getInstance(currentAccount2).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true, 0);
                }
            }
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e((Throwable) x);
            }
        }
    }

    public static void sendMessageToWatch(String path, byte[] data, String capability) {
        Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability(capability, 1).addOnCompleteListener(new OnCompleteListener(path, data) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ byte[] f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onComplete(Task task) {
                WearDataLayerListenerService.lambda$sendMessageToWatch$7(this.f$0, this.f$1, task);
            }
        });
    }

    static /* synthetic */ void lambda$sendMessageToWatch$7(String path, byte[] data, Task task) {
        CapabilityInfo info = (CapabilityInfo) task.getResult();
        if (info != null) {
            MessageClient mc = Wearable.getMessageClient(ApplicationLoader.applicationContext);
            for (Node node : info.getNodes()) {
                mc.sendMessage(node.getId(), path, data);
            }
        }
    }

    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        if ("remote_notifications".equals(capabilityInfo.getName())) {
            watchConnected = false;
            for (Node node : capabilityInfo.getNodes()) {
                if (node.isNearby()) {
                    watchConnected = true;
                }
            }
        }
    }

    public static void updateWatchConnectionState() {
        try {
            Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability("remote_notifications", 1).addOnCompleteListener($$Lambda$WearDataLayerListenerService$4s60kZm8316fSAYqx1Y_NLbGsR0.INSTANCE);
        } catch (Throwable th) {
        }
    }

    static /* synthetic */ void lambda$updateWatchConnectionState$8(Task task) {
        watchConnected = false;
        try {
            CapabilityInfo capabilityInfo = (CapabilityInfo) task.getResult();
            if (capabilityInfo != null) {
                for (Node node : capabilityInfo.getNodes()) {
                    if (node.isNearby()) {
                        watchConnected = true;
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public static boolean isWatchConnected() {
        return watchConnected;
    }
}
