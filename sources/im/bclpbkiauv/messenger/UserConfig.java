package im.bclpbkiauv.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Base64;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.utils.AppUpdater;
import java.io.File;

public class UserConfig extends BaseController {
    private static volatile UserConfig[] Instance = new UserConfig[3];
    public static final int MAX_ACCOUNT_COUNT = 3;
    public static final int i_dialogsLoadOffsetAccess_1 = 5;
    public static final int i_dialogsLoadOffsetAccess_2 = 6;
    public static final int i_dialogsLoadOffsetChannelId = 4;
    public static final int i_dialogsLoadOffsetChatId = 3;
    public static final int i_dialogsLoadOffsetDate = 1;
    public static final int i_dialogsLoadOffsetId = 0;
    public static final int i_dialogsLoadOffsetUserId = 2;
    public static int selectedAccount;
    public long autoDownloadConfigLoadTime;
    public int botRatingLoadTime;
    public int clientUserId;
    private boolean configLoaded;
    public boolean contactsReimported;
    public int contactsSavedCount;
    private TLRPC.User currentUser;
    public boolean draftsLoaded;
    public boolean hasSecureData;
    public boolean hasValidDialogLoadIds;
    public int isCdnVip = -1;
    public int lastBroadcastId = -1;
    public int lastContactsSyncTime;
    public int lastHintsSyncTime;
    public int lastSendMessageId = -210000;
    public int loginTime;
    public long migrateOffsetAccess = -1;
    public int migrateOffsetChannelId = -1;
    public int migrateOffsetChatId = -1;
    public int migrateOffsetDate = -1;
    public int migrateOffsetId = -1;
    public int migrateOffsetUserId = -1;
    public boolean notificationsSettingsLoaded;
    public boolean notificationsSignUpSettingsLoaded;
    public int ratingLoadTime;
    public boolean registeredForPush;
    public volatile byte[] savedPasswordHash;
    public volatile long savedPasswordTime;
    public volatile byte[] savedSaltedPassword;
    public boolean suggestContacts = true;
    private final Object sync = new Object();
    public boolean syncContacts = true;
    public TLRPC.TL_account_tmpPassword tmpPassword;
    public TLRPC.TL_help_termsOfService unacceptedTermsOfService;
    public boolean unreadDialogsLoaded = true;

    public static UserConfig getInstance(int num) {
        UserConfig localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (UserConfig.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    UserConfig[] userConfigArr = Instance;
                    UserConfig userConfig = new UserConfig(num);
                    localInstance = userConfig;
                    userConfigArr[num] = userConfig;
                }
            }
        }
        return localInstance;
    }

    public UserConfig(int instance) {
        super(instance);
    }

    private SharedPreferences getPreferences() {
        if (this.currentAccount == 0) {
            return ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
        }
        Context context = ApplicationLoader.applicationContext;
        return context.getSharedPreferences("userconfig" + this.currentAccount, 0);
    }

    public static int getActivatedAccountsCount() {
        int count = 0;
        for (int a = 0; a < 3; a++) {
            if (AccountInstance.getInstance(a).getUserConfig().isClientActivated()) {
                count++;
            }
        }
        return count;
    }

    public int getNewMessageId() {
        int id;
        synchronized (this.sync) {
            id = this.lastSendMessageId;
            this.lastSendMessageId--;
        }
        return id;
    }

    public void saveConfig(boolean withFile) {
        saveConfig(withFile, (File) null);
    }

    public void saveConfig(boolean withFile, File oldFile) {
        synchronized (this.sync) {
            try {
                SharedPreferences.Editor editor = getPreferences().edit();
                if (this.currentAccount == 0) {
                    editor.putInt("selectedAccount", selectedAccount);
                }
                editor.putBoolean("registeredForPush", this.registeredForPush);
                editor.putInt("lastSendMessageId", this.lastSendMessageId);
                editor.putInt("contactsSavedCount", this.contactsSavedCount);
                editor.putInt("lastBroadcastId", this.lastBroadcastId);
                editor.putInt("lastContactsSyncTime", this.lastContactsSyncTime);
                editor.putInt("lastHintsSyncTime", this.lastHintsSyncTime);
                editor.putBoolean("draftsLoaded", this.draftsLoaded);
                editor.putBoolean("unreadDialogsLoaded", this.unreadDialogsLoaded);
                editor.putInt("ratingLoadTime", this.ratingLoadTime);
                editor.putInt("botRatingLoadTime", this.botRatingLoadTime);
                editor.putBoolean("contactsReimported", this.contactsReimported);
                editor.putInt("loginTime", this.loginTime);
                editor.putBoolean("syncContacts", this.syncContacts);
                editor.putBoolean("suggestContacts", this.suggestContacts);
                editor.putBoolean("hasSecureData", this.hasSecureData);
                editor.putBoolean("notificationsSettingsLoaded3", this.notificationsSettingsLoaded);
                editor.putBoolean("notificationsSignUpSettingsLoaded", this.notificationsSignUpSettingsLoaded);
                editor.putLong("autoDownloadConfigLoadTime", this.autoDownloadConfigLoadTime);
                editor.putBoolean("hasValidDialogLoadIds", this.hasValidDialogLoadIds);
                editor.putInt("6migrateOffsetId", this.migrateOffsetId);
                if (this.migrateOffsetId != -1) {
                    editor.putInt("6migrateOffsetDate", this.migrateOffsetDate);
                    editor.putInt("6migrateOffsetUserId", this.migrateOffsetUserId);
                    editor.putInt("6migrateOffsetChatId", this.migrateOffsetChatId);
                    editor.putInt("6migrateOffsetChannelId", this.migrateOffsetChannelId);
                    editor.putLong("6migrateOffsetAccess", this.migrateOffsetAccess);
                }
                if (this.unacceptedTermsOfService != null) {
                    try {
                        SerializedData data = new SerializedData(this.unacceptedTermsOfService.getObjectSize());
                        this.unacceptedTermsOfService.serializeToStream(data);
                        editor.putString("terms", Base64.encodeToString(data.toByteArray(), 0));
                        data.cleanup();
                    } catch (Exception e) {
                    }
                } else {
                    editor.remove("terms");
                }
                SharedConfig.saveConfig();
                if (this.tmpPassword != null) {
                    SerializedData data2 = new SerializedData();
                    this.tmpPassword.serializeToStream(data2);
                    editor.putString("tmpPassword", Base64.encodeToString(data2.toByteArray(), 0));
                    data2.cleanup();
                } else {
                    editor.remove("tmpPassword");
                }
                if (this.currentUser == null) {
                    editor.remove("user");
                } else if (withFile) {
                    SerializedData data3 = new SerializedData();
                    this.currentUser.serializeToStream(data3);
                    editor.putString("user", Base64.encodeToString(data3.toByteArray(), 0));
                    data3.cleanup();
                }
                editor.commit();
                if (oldFile != null) {
                    oldFile.delete();
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public boolean isClientActivated() {
        boolean z;
        synchronized (this.sync) {
            z = this.currentUser != null;
        }
        return z;
    }

    public int getClientUserId() {
        int i;
        synchronized (this.sync) {
            i = this.currentUser != null ? this.currentUser.id : 0;
        }
        return i;
    }

    public String getClientPhone() {
        String str;
        synchronized (this.sync) {
            str = (this.currentUser == null || this.currentUser.phone == null) ? "" : this.currentUser.phone;
        }
        return str;
    }

    public TLRPC.User getCurrentUser() {
        TLRPC.User user;
        synchronized (this.sync) {
            user = this.currentUser;
        }
        return user;
    }

    public void setCurrentUser(TLRPC.User user) {
        synchronized (this.sync) {
            this.currentUser = user;
            this.clientUserId = user.id;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0113 A[Catch:{ Exception -> 0x0105 }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0181 A[Catch:{ Exception -> 0x0105 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadConfig() {
        /*
            r12 = this;
            java.lang.Object r0 = r12.sync
            monitor-enter(r0)
            boolean r1 = r12.configLoaded     // Catch:{ all -> 0x018b }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x018b }
            return
        L_0x0009:
            android.content.SharedPreferences r1 = r12.getPreferences()     // Catch:{ all -> 0x018b }
            int r2 = r12.currentAccount     // Catch:{ all -> 0x018b }
            r3 = 0
            if (r2 != 0) goto L_0x001a
            java.lang.String r2 = "selectedAccount"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x018b }
            selectedAccount = r2     // Catch:{ all -> 0x018b }
        L_0x001a:
            java.lang.String r2 = "registeredForPush"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x018b }
            r12.registeredForPush = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "lastSendMessageId"
            r4 = -210000(0xfffffffffffccbb0, float:NaN)
            int r2 = r1.getInt(r2, r4)     // Catch:{ all -> 0x018b }
            r12.lastSendMessageId = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "contactsSavedCount"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x018b }
            r12.contactsSavedCount = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "lastBroadcastId"
            r4 = -1
            int r2 = r1.getInt(r2, r4)     // Catch:{ all -> 0x018b }
            r12.lastBroadcastId = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "lastContactsSyncTime"
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x018b }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r7
            int r6 = (int) r5     // Catch:{ all -> 0x018b }
            r5 = 82800(0x14370, float:1.16028E-40)
            int r6 = r6 - r5
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x018b }
            r12.lastContactsSyncTime = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "lastHintsSyncTime"
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x018b }
            long r5 = r5 / r7
            int r6 = (int) r5     // Catch:{ all -> 0x018b }
            r5 = 90000(0x15f90, float:1.26117E-40)
            int r6 = r6 - r5
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x018b }
            r12.lastHintsSyncTime = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "draftsLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x018b }
            r12.draftsLoaded = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "unreadDialogsLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x018b }
            r12.unreadDialogsLoaded = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "contactsReimported"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x018b }
            r12.contactsReimported = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "ratingLoadTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x018b }
            r12.ratingLoadTime = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "botRatingLoadTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x018b }
            r12.botRatingLoadTime = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "loginTime"
            int r5 = r12.currentAccount     // Catch:{ all -> 0x018b }
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x018b }
            r12.loginTime = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "syncContacts"
            r5 = 1
            boolean r2 = r1.getBoolean(r2, r5)     // Catch:{ all -> 0x018b }
            r12.syncContacts = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "suggestContacts"
            boolean r2 = r1.getBoolean(r2, r5)     // Catch:{ all -> 0x018b }
            r12.suggestContacts = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "hasSecureData"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x018b }
            r12.hasSecureData = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "notificationsSettingsLoaded3"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x018b }
            r12.notificationsSettingsLoaded = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "notificationsSignUpSettingsLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x018b }
            r12.notificationsSignUpSettingsLoaded = r2     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "autoDownloadConfigLoadTime"
            r6 = 0
            long r8 = r1.getLong(r2, r6)     // Catch:{ all -> 0x018b }
            r12.autoDownloadConfigLoadTime = r8     // Catch:{ all -> 0x018b }
            java.lang.String r2 = "2dialogsLoadOffsetId"
            boolean r2 = r1.contains(r2)     // Catch:{ all -> 0x018b }
            if (r2 != 0) goto L_0x00df
            java.lang.String r2 = "hasValidDialogLoadIds"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x018b }
            if (r2 == 0) goto L_0x00dd
            goto L_0x00df
        L_0x00dd:
            r2 = 0
            goto L_0x00e0
        L_0x00df:
            r2 = 1
        L_0x00e0:
            r12.hasValidDialogLoadIds = r2     // Catch:{ all -> 0x018b }
            r2 = 0
            java.lang.String r8 = "terms"
            java.lang.String r8 = r1.getString(r8, r2)     // Catch:{ Exception -> 0x0105 }
            if (r8 == 0) goto L_0x0104
            byte[] r9 = android.util.Base64.decode(r8, r3)     // Catch:{ Exception -> 0x0105 }
            if (r9 == 0) goto L_0x0104
            im.bclpbkiauv.tgnet.SerializedData r10 = new im.bclpbkiauv.tgnet.SerializedData     // Catch:{ Exception -> 0x0105 }
            r10.<init>((byte[]) r9)     // Catch:{ Exception -> 0x0105 }
            int r11 = r10.readInt32(r3)     // Catch:{ Exception -> 0x0105 }
            im.bclpbkiauv.tgnet.TLRPC$TL_help_termsOfService r11 = im.bclpbkiauv.tgnet.TLRPC.TL_help_termsOfService.TLdeserialize(r10, r11, r3)     // Catch:{ Exception -> 0x0105 }
            r12.unacceptedTermsOfService = r11     // Catch:{ Exception -> 0x0105 }
            r10.cleanup()     // Catch:{ Exception -> 0x0105 }
        L_0x0104:
            goto L_0x0109
        L_0x0105:
            r8 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ all -> 0x018b }
        L_0x0109:
            java.lang.String r8 = "6migrateOffsetId"
            int r8 = r1.getInt(r8, r3)     // Catch:{ all -> 0x018b }
            r12.migrateOffsetId = r8     // Catch:{ all -> 0x018b }
            if (r8 == r4) goto L_0x013b
            java.lang.String r4 = "6migrateOffsetDate"
            int r4 = r1.getInt(r4, r3)     // Catch:{ all -> 0x018b }
            r12.migrateOffsetDate = r4     // Catch:{ all -> 0x018b }
            java.lang.String r4 = "6migrateOffsetUserId"
            int r4 = r1.getInt(r4, r3)     // Catch:{ all -> 0x018b }
            r12.migrateOffsetUserId = r4     // Catch:{ all -> 0x018b }
            java.lang.String r4 = "6migrateOffsetChatId"
            int r4 = r1.getInt(r4, r3)     // Catch:{ all -> 0x018b }
            r12.migrateOffsetChatId = r4     // Catch:{ all -> 0x018b }
            java.lang.String r4 = "6migrateOffsetChannelId"
            int r4 = r1.getInt(r4, r3)     // Catch:{ all -> 0x018b }
            r12.migrateOffsetChannelId = r4     // Catch:{ all -> 0x018b }
            java.lang.String r4 = "6migrateOffsetAccess"
            long r6 = r1.getLong(r4, r6)     // Catch:{ all -> 0x018b }
            r12.migrateOffsetAccess = r6     // Catch:{ all -> 0x018b }
        L_0x013b:
            java.lang.String r4 = "tmpPassword"
            java.lang.String r4 = r1.getString(r4, r2)     // Catch:{ all -> 0x018b }
            if (r4 == 0) goto L_0x015c
            byte[] r6 = android.util.Base64.decode(r4, r3)     // Catch:{ all -> 0x018b }
            if (r6 == 0) goto L_0x015c
            im.bclpbkiauv.tgnet.SerializedData r7 = new im.bclpbkiauv.tgnet.SerializedData     // Catch:{ all -> 0x018b }
            r7.<init>((byte[]) r6)     // Catch:{ all -> 0x018b }
            int r8 = r7.readInt32(r3)     // Catch:{ all -> 0x018b }
            im.bclpbkiauv.tgnet.TLRPC$TL_account_tmpPassword r8 = im.bclpbkiauv.tgnet.TLRPC.TL_account_tmpPassword.TLdeserialize(r7, r8, r3)     // Catch:{ all -> 0x018b }
            r12.tmpPassword = r8     // Catch:{ all -> 0x018b }
            r7.cleanup()     // Catch:{ all -> 0x018b }
        L_0x015c:
            java.lang.String r6 = "user"
            java.lang.String r2 = r1.getString(r6, r2)     // Catch:{ all -> 0x018b }
            if (r2 == 0) goto L_0x017d
            byte[] r4 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x018b }
            if (r4 == 0) goto L_0x017d
            im.bclpbkiauv.tgnet.SerializedData r6 = new im.bclpbkiauv.tgnet.SerializedData     // Catch:{ all -> 0x018b }
            r6.<init>((byte[]) r4)     // Catch:{ all -> 0x018b }
            int r7 = r6.readInt32(r3)     // Catch:{ all -> 0x018b }
            im.bclpbkiauv.tgnet.TLRPC$User r3 = im.bclpbkiauv.tgnet.TLRPC.User.TLdeserialize(r6, r7, r3)     // Catch:{ all -> 0x018b }
            r12.currentUser = r3     // Catch:{ all -> 0x018b }
            r6.cleanup()     // Catch:{ all -> 0x018b }
        L_0x017d:
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r12.currentUser     // Catch:{ all -> 0x018b }
            if (r3 == 0) goto L_0x0187
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r12.currentUser     // Catch:{ all -> 0x018b }
            int r3 = r3.id     // Catch:{ all -> 0x018b }
            r12.clientUserId = r3     // Catch:{ all -> 0x018b }
        L_0x0187:
            r12.configLoaded = r5     // Catch:{ all -> 0x018b }
            monitor-exit(r0)     // Catch:{ all -> 0x018b }
            return
        L_0x018b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x018b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.UserConfig.loadConfig():void");
    }

    public void savePassword(byte[] hash, byte[] salted) {
        this.savedPasswordTime = SystemClock.elapsedRealtime();
        this.savedPasswordHash = hash;
        this.savedSaltedPassword = salted;
    }

    public void checkSavedPassword() {
        if (!(this.savedSaltedPassword == null && this.savedPasswordHash == null) && Math.abs(SystemClock.elapsedRealtime() - this.savedPasswordTime) >= 1800000) {
            resetSavedPassword();
        }
    }

    public void resetSavedPassword() {
        this.savedPasswordTime = 0;
        if (this.savedPasswordHash != null) {
            for (int a = 0; a < this.savedPasswordHash.length; a++) {
                this.savedPasswordHash[a] = 0;
            }
            this.savedPasswordHash = null;
        }
        if (this.savedSaltedPassword != null) {
            for (int a2 = 0; a2 < this.savedSaltedPassword.length; a2++) {
                this.savedSaltedPassword[a2] = 0;
            }
            this.savedSaltedPassword = null;
        }
    }

    public void clearConfig() {
        getPreferences().edit().clear().commit();
        this.currentUser = null;
        this.clientUserId = 0;
        this.registeredForPush = false;
        this.contactsSavedCount = 0;
        this.lastSendMessageId = -210000;
        this.lastBroadcastId = -1;
        this.notificationsSettingsLoaded = false;
        this.notificationsSignUpSettingsLoaded = false;
        this.migrateOffsetId = -1;
        this.migrateOffsetDate = -1;
        this.migrateOffsetUserId = -1;
        this.migrateOffsetChatId = -1;
        this.migrateOffsetChannelId = -1;
        this.migrateOffsetAccess = -1;
        this.ratingLoadTime = 0;
        this.botRatingLoadTime = 0;
        this.draftsLoaded = true;
        this.contactsReimported = true;
        this.syncContacts = true;
        this.suggestContacts = true;
        this.unreadDialogsLoaded = true;
        this.hasValidDialogLoadIds = true;
        this.unacceptedTermsOfService = null;
        this.hasSecureData = false;
        this.loginTime = (int) (System.currentTimeMillis() / 1000);
        this.lastContactsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 82800;
        this.lastHintsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 90000;
        AppUpdater.pendingAppUpdate = null;
        resetSavedPassword();
        boolean hasActivated = false;
        int a = 0;
        while (true) {
            if (a >= 3) {
                break;
            } else if (AccountInstance.getInstance(a).getUserConfig().isClientActivated()) {
                hasActivated = true;
                break;
            } else {
                a++;
            }
        }
        if (!hasActivated) {
            SharedConfig.clearConfig();
        }
        saveConfig(true);
    }

    public boolean isPinnedDialogsLoaded(int folderId) {
        SharedPreferences preferences = getPreferences();
        return preferences.getBoolean("2pinnedDialogsLoaded" + folderId, false);
    }

    public void setPinnedDialogsLoaded(int folderId, boolean loaded) {
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.putBoolean("2pinnedDialogsLoaded" + folderId, loaded).commit();
    }

    public int getTotalDialogsCount(int folderId) {
        SharedPreferences preferences = getPreferences();
        StringBuilder sb = new StringBuilder();
        sb.append("2totalDialogsLoadCount");
        sb.append(folderId == 0 ? "" : Integer.valueOf(folderId));
        return preferences.getInt(sb.toString(), 0);
    }

    public void setTotalDialogsCount(int folderId, int totalDialogsLoadCount) {
        SharedPreferences.Editor edit = getPreferences().edit();
        StringBuilder sb = new StringBuilder();
        sb.append("2totalDialogsLoadCount");
        sb.append(folderId == 0 ? "" : Integer.valueOf(folderId));
        edit.putInt(sb.toString(), totalDialogsLoadCount).commit();
    }

    public int[] getDialogLoadOffsets(int folderId) {
        SharedPreferences preferences = getPreferences();
        StringBuilder sb = new StringBuilder();
        sb.append("2dialogsLoadOffsetId");
        Object obj = "";
        sb.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        int i = -1;
        int dialogsLoadOffsetId = preferences.getInt(sb.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("2dialogsLoadOffsetDate");
        sb2.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        int dialogsLoadOffsetDate = preferences.getInt(sb2.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("2dialogsLoadOffsetUserId");
        sb3.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        int dialogsLoadOffsetUserId = preferences.getInt(sb3.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("2dialogsLoadOffsetChatId");
        sb4.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        int dialogsLoadOffsetChatId = preferences.getInt(sb4.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("2dialogsLoadOffsetChannelId");
        sb5.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        String sb6 = sb5.toString();
        if (this.hasValidDialogLoadIds) {
            i = 0;
        }
        int dialogsLoadOffsetChannelId = preferences.getInt(sb6, i);
        StringBuilder sb7 = new StringBuilder();
        sb7.append("2dialogsLoadOffsetAccess");
        if (folderId != 0) {
            obj = Integer.valueOf(folderId);
        }
        sb7.append(obj);
        long dialogsLoadOffsetAccess = preferences.getLong(sb7.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        return new int[]{dialogsLoadOffsetId, dialogsLoadOffsetDate, dialogsLoadOffsetUserId, dialogsLoadOffsetChatId, dialogsLoadOffsetChannelId, (int) dialogsLoadOffsetAccess, (int) (dialogsLoadOffsetAccess >> 32)};
    }

    public void setDialogsLoadOffset(int folderId, int dialogsLoadOffsetId, int dialogsLoadOffsetDate, int dialogsLoadOffsetUserId, int dialogsLoadOffsetChatId, int dialogsLoadOffsetChannelId, long dialogsLoadOffsetAccess) {
        SharedPreferences.Editor editor = getPreferences().edit();
        StringBuilder sb = new StringBuilder();
        sb.append("2dialogsLoadOffsetId");
        Object obj = "";
        sb.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putInt(sb.toString(), dialogsLoadOffsetId);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("2dialogsLoadOffsetDate");
        sb2.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putInt(sb2.toString(), dialogsLoadOffsetDate);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("2dialogsLoadOffsetUserId");
        sb3.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putInt(sb3.toString(), dialogsLoadOffsetUserId);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("2dialogsLoadOffsetChatId");
        sb4.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putInt(sb4.toString(), dialogsLoadOffsetChatId);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("2dialogsLoadOffsetChannelId");
        sb5.append(folderId == 0 ? obj : Integer.valueOf(folderId));
        editor.putInt(sb5.toString(), dialogsLoadOffsetChannelId);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("2dialogsLoadOffsetAccess");
        if (folderId != 0) {
            obj = Integer.valueOf(folderId);
        }
        sb6.append(obj);
        editor.putLong(sb6.toString(), dialogsLoadOffsetAccess);
        editor.putBoolean("hasValidDialogLoadIds", true);
        editor.commit();
    }
}
