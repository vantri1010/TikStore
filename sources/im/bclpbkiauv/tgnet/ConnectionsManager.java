package im.bclpbkiauv.tgnet;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import com.google.android.exoplayer2.util.Log;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.king.zxing.util.LogUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BaseController;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.KeepAliveJob;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.StatsController;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsManager extends BaseController {
    private static final int CORE_POOL_SIZE;
    public static final int CPU_COUNT;
    public static final int ConnectionStateConnected = 3;
    public static final int ConnectionStateConnecting = 1;
    public static final int ConnectionStateConnectingToProxy = 4;
    public static final int ConnectionStateUpdating = 5;
    public static final int ConnectionStateWaitingForNetwork = 2;
    public static final int ConnectionTypeDownload = 2;
    public static final int ConnectionTypeDownload2 = 65538;
    public static final int ConnectionTypeGeneric = 1;
    public static final int ConnectionTypePush = 8;
    public static final int ConnectionTypeUpload = 4;
    public static final int DEFAULT_DATACENTER_ID = Integer.MAX_VALUE;
    public static final Executor DNS_THREAD_POOL_EXECUTOR;
    public static final int FileTypeAudio = 50331648;
    public static final int FileTypeFile = 67108864;
    public static final int FileTypePhoto = 16777216;
    public static final int FileTypeVideo = 33554432;
    private static volatile ConnectionsManager[] Instance = new ConnectionsManager[3];
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT * 2) + 1);
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    /* access modifiers changed from: private */
    public static AsyncTask currentTask;
    /* access modifiers changed from: private */
    public static HashMap<String, ResolvedDomain> dnsCache = new HashMap<>();
    private static int lastClassGuid = 1;
    private static long lastDnsRequestTime;
    /* access modifiers changed from: private */
    public static HashMap<String, ResolveHostByNameTask> resolvingHostnameTasks = new HashMap<>();
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(128);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "DnsAsyncTask #" + this.mCount.getAndIncrement());
        }
    };
    private boolean appPaused = true;
    private int appResumeCount;
    private int connectionState;
    private String currentAddress;
    private boolean isUpdating;
    private long lastPauseTime = System.currentTimeMillis();
    private AtomicInteger lastRequestToken = new AtomicInteger(1);

    public static native void native_applyBackupConfig(int i, long j);

    public static native void native_applyBackupIp(int i, String str, int i2, int i3);

    public static native void native_applyDatacenterAddress(int i, int i2, String str, int i3);

    public static native void native_applyDnsConfig(int i, long j, String str, int i2);

    public static native void native_bindRequestToGuid(int i, int i2, int i3);

    public static native void native_cancelRequest(int i, int i2, boolean z);

    public static native void native_cancelRequestsForGuid(int i, int i2);

    public static native long native_checkProxy(int i, String str, int i2, String str2, String str3, String str4, RequestTimeDelegate requestTimeDelegate);

    public static native void native_cleanUp(int i, boolean z);

    public static native long native_getAuthKeyId(int i);

    public static native int native_getConnectionState(int i);

    public static native int native_getCurrentTime(int i);

    public static native long native_getCurrentTimeMillis(int i);

    public static native int native_getTimeDifference(int i);

    public static native void native_init(int i, int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i5, boolean z, boolean z2, int i6, String str9);

    public static native int native_isTestBackend(int i);

    public static native void native_onHostNameResolved(String str, long j, String str2);

    public static native void native_pauseNetwork(int i);

    public static native void native_resumeNetwork(int i, boolean z);

    public static native void native_seSystemLangCode(int i, String str);

    public static native void native_sendRequest(int i, long j, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setIpPortDefaultAddress(int i, String str, int i2);

    public static native void native_setJava(boolean z);

    public static native void native_setLangCode(int i, String str);

    public static native void native_setNetworkAvailable(int i, boolean z, int i2, boolean z2);

    public static native void native_setProxySettings(int i, String str, int i2, String str2, String str3, String str4);

    public static native void native_setPushConnectionEnabled(int i, boolean z);

    public static native void native_setRegId(int i, String str);

    public static native void native_setSystemLangCode(int i, String str);

    public static native void native_setUseIpv6(int i, boolean z);

    public static native void native_setUserId(int i, int i2);

    public static native void native_switchBackend(int i);

    public static native void native_updateDcSettings(int i);

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        CORE_POOL_SIZE = Math.max(2, Math.min(availableProcessors - 1, 4));
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 30, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        DNS_THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    private static class ResolvedDomain {
        public ArrayList<String> addresses;
        long ttl;

        public ResolvedDomain(ArrayList<String> a, long t) {
            this.addresses = a;
            this.ttl = t;
        }

        public String getAddress() {
            return this.addresses.get(Utilities.random.nextInt(this.addresses.size()));
        }
    }

    public static ConnectionsManager getInstance(int num) {
        ConnectionsManager localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    ConnectionsManager[] connectionsManagerArr = Instance;
                    ConnectionsManager connectionsManager = new ConnectionsManager(num);
                    localInstance = connectionsManager;
                    connectionsManagerArr[num] = connectionsManager;
                }
            }
        }
        return localInstance;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ConnectionsManager(int instance) {
        super(instance);
        File config;
        String langCode;
        String appVersion;
        String langCode2;
        String systemVersion;
        String systemLangCode;
        String deviceModel;
        String appVersion2;
        String systemVersion2;
        String pushString;
        int i = instance;
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("ConnectionsManager.java ===> constructor , currentAccount=" + this.currentAccount + " ,newAccount=" + i);
        }
        this.connectionState = native_getConnectionState(this.currentAccount);
        File config2 = ApplicationLoader.getFilesDirFixed();
        if (i != 0) {
            File config3 = new File(config2, "account" + i);
            config3.mkdirs();
            config = config3;
        } else {
            config = config2;
        }
        String configPath = config.toString();
        SharedPreferences preferences = MessagesController.getGlobalNotificationsSettings();
        boolean enablePushConnection = preferences.getBoolean("pushConnection", true);
        try {
            systemLangCode = LocaleController.getSystemLocaleStringIso639().toLowerCase();
            String langCode3 = LocaleController.getLocaleStringIso639().toLowerCase();
            langCode2 = Build.MANUFACTURER + Build.MODEL;
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            appVersion = pInfo.versionName + " (" + pInfo.versionCode + SQLBuilder.PARENTHESES_RIGHT;
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
            langCode = langCode3;
        } catch (Exception e) {
            appVersion = "App version unknown";
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
            systemLangCode = "en";
            langCode = "";
            langCode2 = "Android unknown";
        }
        systemLangCode = systemLangCode.trim().length() == 0 ? "en" : systemLangCode;
        if (langCode2.trim().length() == 0) {
            deviceModel = "Android unknown";
        } else {
            deviceModel = langCode2;
        }
        if (appVersion.trim().length() == 0) {
            appVersion2 = "App version unknown";
        } else {
            appVersion2 = appVersion;
        }
        if (systemVersion.trim().length() == 0) {
            systemVersion2 = "SDK Unknown";
        } else {
            systemVersion2 = systemVersion;
        }
        getUserConfig().loadConfig();
        String pushString2 = SharedConfig.pushString;
        if (!TextUtils.isEmpty(pushString2) || TextUtils.isEmpty(SharedConfig.pushStringStatus)) {
            pushString = pushString2;
        } else {
            pushString = SharedConfig.pushStringStatus;
        }
        SharedPreferences sharedPreferences = preferences;
        init(BuildVars.BUILD_VERSION, 105, BuildVars.APP_ID, deviceModel, systemVersion2, appVersion2, langCode, systemLangCode, configPath, FileLog.getNetworkLogPath(), pushString, getUserConfig().getClientUserId(), enablePushConnection);
    }

    public long getCurrentTimeMillis() {
        return native_getCurrentTimeMillis(this.currentAccount);
    }

    public int getCurrentTime() {
        return native_getCurrentTime(this.currentAccount);
    }

    public int getTimeDifference() {
        return native_getTimeDifference(this.currentAccount);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock) {
        return sendRequest(object, completionBlock, (QuickAckDelegate) null, 0);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags) {
        return sendRequest(object, completionBlock, (QuickAckDelegate) null, (WriteToSocketDelegate) null, flags, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags, int connetionType) {
        return sendRequest(object, completionBlock, (QuickAckDelegate) null, (WriteToSocketDelegate) null, flags, Integer.MAX_VALUE, connetionType, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, QuickAckDelegate quickAckBlock, int flags) {
        return sendRequest(object, completionBlock, quickAckBlock, (WriteToSocketDelegate) null, flags, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate onComplete, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate) {
        int requestToken = this.lastRequestToken.getAndIncrement();
        Utilities.stageQueue.postRunnable(new Runnable(object, requestToken, onComplete, onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ RequestDelegate f$3;
            private final /* synthetic */ QuickAckDelegate f$4;
            private final /* synthetic */ WriteToSocketDelegate f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ int f$7;
            private final /* synthetic */ int f$8;
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
            }

            public final void run() {
                ConnectionsManager.this.lambda$sendRequest$2$ConnectionsManager(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
            }
        });
        return requestToken;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendRequest$2$ConnectionsManager(im.bclpbkiauv.tgnet.TLObject r17, int r18, im.bclpbkiauv.tgnet.RequestDelegate r19, im.bclpbkiauv.tgnet.QuickAckDelegate r20, im.bclpbkiauv.tgnet.WriteToSocketDelegate r21, int r22, int r23, int r24, boolean r25) {
        /*
            r16 = this;
            r1 = r17
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0025
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "send request "
            r0.append(r2)
            r0.append(r1)
            java.lang.String r2 = " with token = "
            r0.append(r2)
            r2 = r18
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.messenger.FileLog.d(r0)
            goto L_0x0027
        L_0x0025:
            r2 = r18
        L_0x0027:
            im.bclpbkiauv.tgnet.NativeByteBuffer r0 = new im.bclpbkiauv.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0059 }
            int r3 = r17.getObjectSize()     // Catch:{ Exception -> 0x0059 }
            r0.<init>((int) r3)     // Catch:{ Exception -> 0x0059 }
            r1.serializeToStream(r0)     // Catch:{ Exception -> 0x0059 }
            r17.freeResources()     // Catch:{ Exception -> 0x0059 }
            r14 = r16
            int r3 = r14.currentAccount     // Catch:{ Exception -> 0x0057 }
            long r4 = r0.address     // Catch:{ Exception -> 0x0057 }
            im.bclpbkiauv.tgnet.-$$Lambda$ConnectionsManager$lE8VdWvtIH3k51Ht51aw-WaP23E r6 = new im.bclpbkiauv.tgnet.-$$Lambda$ConnectionsManager$lE8VdWvtIH3k51Ht51aw-WaP23E     // Catch:{ Exception -> 0x0057 }
            r15 = r19
            r6.<init>(r15)     // Catch:{ Exception -> 0x0055 }
            r7 = r20
            r8 = r21
            r9 = r22
            r10 = r23
            r11 = r24
            r12 = r25
            r13 = r18
            native_sendRequest(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0055 }
            goto L_0x007d
        L_0x0055:
            r0 = move-exception
            goto L_0x005e
        L_0x0057:
            r0 = move-exception
            goto L_0x005c
        L_0x0059:
            r0 = move-exception
            r14 = r16
        L_0x005c:
            r15 = r19
        L_0x005e:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            boolean r3 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x007d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "java request outer layer exception "
            r3.append(r4)
            java.lang.String r4 = r0.toString()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r3)
        L_0x007d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.tgnet.ConnectionsManager.lambda$sendRequest$2$ConnectionsManager(im.bclpbkiauv.tgnet.TLObject, int, im.bclpbkiauv.tgnet.RequestDelegate, im.bclpbkiauv.tgnet.QuickAckDelegate, im.bclpbkiauv.tgnet.WriteToSocketDelegate, int, int, int, boolean):void");
    }

    static /* synthetic */ void lambda$null$1(TLObject object, RequestDelegate onComplete, long response, int errorCode, String errorText, int networkType) {
        TLObject resp = null;
        TLRPC.TL_error error = null;
        if (response != 0) {
            try {
                NativeByteBuffer buff = NativeByteBuffer.wrap(response);
                buff.reused = true;
                resp = object.deserializeResponse(buff, buff.readInt32(true), true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("java parse inner layer exception " + e.toString());
                    return;
                }
                return;
            }
        } else if (errorText != null) {
            error = new TLRPC.TL_error();
            error.code = errorCode;
            error.text = errorText;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(object + " got error " + error.code + " " + error.text);
            }
        }
        if (resp != null) {
            resp.networkType = networkType;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("java received " + resp + " error = " + error);
        }
        Utilities.stageQueue.postRunnable(new Runnable(resp, error) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ConnectionsManager.lambda$null$0(RequestDelegate.this, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$null$0(RequestDelegate onComplete, TLObject finalResponse, TLRPC.TL_error finalError) {
        onComplete.run(finalResponse, finalError);
        if (finalResponse != null) {
            finalResponse.freeResources();
        }
    }

    public void cancelRequest(int token, boolean notifyServer) {
        native_cancelRequest(this.currentAccount, token, notifyServer);
    }

    public void cleanup(boolean resetKeys) {
        native_cleanUp(this.currentAccount, resetKeys);
    }

    public void cancelRequestsForGuid(int guid) {
        native_cancelRequestsForGuid(this.currentAccount, guid);
    }

    public void bindRequestToGuid(int requestToken, int guid) {
        native_bindRequestToGuid(this.currentAccount, requestToken, guid);
    }

    public void applyDatacenterAddress(int datacenterId, String ipAddress, int port) {
        this.currentAddress = ipAddress + LogUtils.COLON + port;
        native_applyDatacenterAddress(this.currentAccount, datacenterId, ipAddress, port);
    }

    public int getConnectionState() {
        if (this.connectionState != 3 || !this.isUpdating) {
            return this.connectionState;
        }
        return 5;
    }

    public void setUserId(int id) {
        native_setUserId(this.currentAccount, id);
    }

    public void checkConnection() {
        native_setUseIpv6(this.currentAccount, useIpv6Address());
        native_setNetworkAvailable(this.currentAccount, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType(), ApplicationLoader.isConnectionSlow());
    }

    public void setPushConnectionEnabled(boolean value) {
        native_setPushConnectionEnabled(this.currentAccount, value);
    }

    public void init(int version, int layer, int apiId, String deviceModel, String systemVersion, String appVersion, String langCode, String systemLangCode, String configPath, String logPath, String regId, int userId, boolean enablePushConnection) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String proxyAddress = preferences.getString("proxy_ip", "");
        String proxyUsername = preferences.getString("proxy_user", "");
        String proxyPassword = preferences.getString("proxy_pass", "");
        String proxySecret = preferences.getString("proxy_secret", "");
        int proxyPort = preferences.getInt("proxy_port", 1080);
        if (preferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(proxyAddress)) {
            native_setProxySettings(this.currentAccount, proxyAddress, proxyPort, proxyUsername, proxyPassword, proxySecret);
        }
        native_init(this.currentAccount, version, layer, apiId, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, logPath, regId, userId, enablePushConnection, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType(), "Yixin");
        checkConnection();
    }

    public static void setLangCode(String langCode) {
        String langCode2 = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < 3; a++) {
            native_setLangCode(a, langCode2);
        }
    }

    public static void setRegId(String regId, String status) {
        String pushString = regId;
        if (TextUtils.isEmpty(pushString) && !TextUtils.isEmpty(status)) {
            pushString = status;
        }
        for (int a = 0; a < 3; a++) {
            native_setRegId(a, pushString);
        }
    }

    public static void setSystemLangCode(String langCode) {
        String langCode2 = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < 3; a++) {
            native_setSystemLangCode(a, langCode2);
        }
    }

    public void switchBackend() {
        MessagesController.getGlobalMainSettings().edit().remove("language_showed2").commit();
        native_switchBackend(this.currentAccount);
    }

    public void resumeNetworkMaybe() {
        native_resumeNetwork(this.currentAccount, true);
    }

    public void updateDcSettings() {
        native_updateDcSettings(this.currentAccount);
    }

    public long getPauseTime() {
        return this.lastPauseTime;
    }

    public long checkProxy(String address, int port, String username, String password, String secret, RequestTimeDelegate requestTimeDelegate) {
        if (TextUtils.isEmpty(address)) {
            return 0;
        }
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        return native_checkProxy(this.currentAccount, address, port, username, password, secret, requestTimeDelegate);
    }

    public void setAppPaused(boolean value, boolean byScreenState) {
        if (!byScreenState) {
            this.appPaused = value;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app paused = " + value);
            }
            if (value) {
                this.appResumeCount--;
            } else {
                this.appResumeCount++;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app resume count " + this.appResumeCount);
            }
            if (this.appResumeCount < 0) {
                this.appResumeCount = 0;
            }
        }
        if (this.appResumeCount == 0) {
            if (this.lastPauseTime == 0) {
                this.lastPauseTime = System.currentTimeMillis();
            }
            native_pauseNetwork(this.currentAccount);
        } else if (!this.appPaused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset app pause time");
            }
            this.lastPauseTime = 0;
            native_resumeNetwork(this.currentAccount, false);
        }
    }

    public static void onUnparsedMessageReceived(long address, int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            int constructor = buff.readInt32(true);
            TLObject message = TLClassStore.Instance().TLdeserialize(buff, constructor, true);
            if (message instanceof TLRPC.Updates) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("java received " + message);
                }
                KeepAliveJob.finishJob();
                Utilities.stageQueue.postRunnable(new Runnable(currentAccount, message) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ TLObject f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        AccountInstance.getInstance(this.f$0).getMessagesController().processUpdates((TLRPC.Updates) this.f$1, false);
                    }
                });
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d(String.format("java received unknown constructor 0x%x", new Object[]{Integer.valueOf(constructor)}));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onUpdate(int currentAccount) {
        Utilities.stageQueue.postRunnable(new Runnable(currentAccount) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                AccountInstance.getInstance(this.f$0).getMessagesController().updateTimerProc();
            }
        });
    }

    public static void onSessionCreated(int currentAccount) {
        Utilities.stageQueue.postRunnable(new Runnable(currentAccount) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                AccountInstance.getInstance(this.f$0).getMessagesController().getDifference();
            }
        });
    }

    public static void onConnectionStateChanged(int state, int currentAccount) {
        AndroidUtilities.runOnUIThread(new Runnable(currentAccount, state) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                ConnectionsManager.lambda$onConnectionStateChanged$6(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$onConnectionStateChanged$6(int currentAccount, int state) {
        getInstance(currentAccount).connectionState = state;
        AccountInstance.getInstance(currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
    }

    public static void onLogout(int currentAccount) {
        AndroidUtilities.runOnUIThread(new Runnable(currentAccount) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                ConnectionsManager.lambda$onLogout$7(this.f$0);
            }
        });
    }

    static /* synthetic */ void lambda$onLogout$7(int currentAccount) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("ConnectionManager.java received signal of logout.");
        }
        AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
        if (accountInstance.getUserConfig().getClientUserId() != 0) {
            accountInstance.getUserConfig().clearConfig();
            accountInstance.getMessagesController().performLogout(0);
        }
    }

    public static int getInitFlags() {
        return 0;
    }

    public static void onBytesSent(int amount, int networkType, int currentAccount) {
        try {
            AccountInstance.getInstance(currentAccount).getStatsController().incrementSentBytesCount(networkType, 6, (long) amount);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onRequestNewServerIpAndPort(int second, int currentAccount) {
        Utilities.stageQueue.postRunnable(new Runnable(second, currentAccount) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                ConnectionsManager.lambda$onRequestNewServerIpAndPort$8(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$onRequestNewServerIpAndPort$8(int second, int currentAccount) {
        if (currentTask == null && ((second != 0 || Math.abs(lastDnsRequestTime - System.currentTimeMillis()) >= 6000) && ApplicationLoader.isNetworkOnline())) {
            lastDnsRequestTime = System.currentTimeMillis();
            if (second == 1) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start dns txt task");
                }
                DnsTxtLoadTask task = new DnsTxtLoadTask(currentAccount);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = task;
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start firebase task");
            }
            FirebaseTask task2 = new FirebaseTask(currentAccount);
            task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            currentTask = task2;
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("don't start task, current task = " + currentTask + " next task = " + second + " time diff = " + Math.abs(lastDnsRequestTime - System.currentTimeMillis()) + " network = " + ApplicationLoader.isNetworkOnline());
        }
    }

    public static void onProxyError() {
        AndroidUtilities.runOnUIThread($$Lambda$ConnectionsManager$TU_xLWdFYEtEAYj_fyKJPwteolQ.INSTANCE);
    }

    public static void getHostByName(String hostName, long address) {
        AndroidUtilities.runOnUIThread(new Runnable(hostName, address) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ long f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                ConnectionsManager.lambda$getHostByName$10(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$getHostByName$10(String hostName, long address) {
        ResolvedDomain resolvedDomain = dnsCache.get(hostName);
        if (resolvedDomain == null || SystemClock.elapsedRealtime() - resolvedDomain.ttl >= 300000) {
            ResolveHostByNameTask task = resolvingHostnameTasks.get(hostName);
            if (task == null) {
                task = new ResolveHostByNameTask(hostName);
                try {
                    task.executeOnExecutor(DNS_THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    resolvingHostnameTasks.put(hostName, task);
                } catch (Throwable e) {
                    FileLog.e(e);
                    native_onHostNameResolved(hostName, address, "");
                    return;
                }
            }
            task.addAddress(address);
            return;
        }
        native_onHostNameResolved(hostName, address, resolvedDomain.getAddress());
    }

    public static void onBytesReceived(int amount, int networkType, int currentAccount) {
        try {
            StatsController.getInstance(currentAccount).incrementReceivedBytesCount(networkType, 6, (long) amount);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onUpdateConfig(long address, int currentAccount) {
        AndroidUtilities.runOnUIThread(new Runnable(currentAccount) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                ConnectionsManager.lambda$onUpdateConfig$11(this.f$0);
            }
        });
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            TLRPC.TL_config message = TLRPC.TL_config.TLdeserialize(buff, buff.readInt32(true), true);
            if (message != null) {
                Utilities.stageQueue.postRunnable(new Runnable(currentAccount, message) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ TLRPC.TL_config f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        AccountInstance.getInstance(this.f$0).getMessagesController().updateConfig(this.f$1);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$onUpdateConfig$11(int currentAccount) {
        NetworkConfig.serverIndex = 3;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.getBackupIpStatus, "server 3");
    }

    public static void onInternalPushReceived(int currentAccount) {
        KeepAliveJob.startJob();
    }

    public static void setProxySettings(boolean enabled, String address, int port, String username, String password, String secret) {
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        for (int a = 0; a < 3; a++) {
            if (!enabled || TextUtils.isEmpty(address)) {
                native_setProxySettings(a, "", 1080, "", "", "");
            } else {
                native_setProxySettings(a, address, port, username, password, secret);
            }
            AccountInstance accountInstance = AccountInstance.getInstance(a);
            if (accountInstance.getUserConfig().isClientActivated()) {
                accountInstance.getMessagesController().checkProxyInfo(true);
            }
        }
    }

    public long getAuthKeyId(int currentAccount) {
        return native_getAuthKeyId(currentAccount);
    }

    public static int generateClassGuid() {
        int i = lastClassGuid;
        lastClassGuid = i + 1;
        return i;
    }

    public void setIsUpdating(boolean value) {
        AndroidUtilities.runOnUIThread(new Runnable(value) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ConnectionsManager.this.lambda$setIsUpdating$13$ConnectionsManager(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setIsUpdating$13$ConnectionsManager(boolean value) {
        if (this.isUpdating != value) {
            this.isUpdating = value;
            if (this.connectionState == 3) {
                AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
            }
        }
    }

    protected static boolean useIpv6Address() {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        if (BuildVars.LOGS_ENABLED) {
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                        if (!networkInterface.getInterfaceAddresses().isEmpty()) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("valid interface: " + networkInterface);
                            }
                            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                            for (int a = 0; a < interfaceAddresses.size(); a++) {
                                InetAddress inetAddress = interfaceAddresses.get(a).getAddress();
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("address: " + inetAddress.getHostAddress());
                                }
                                if (!inetAddress.isLinkLocalAddress() && !inetAddress.isLoopbackAddress()) {
                                    if (!inetAddress.isMulticastAddress()) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("address is good");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        try {
            Enumeration<NetworkInterface> networkInterfaces2 = NetworkInterface.getNetworkInterfaces();
            boolean hasIpv4 = false;
            boolean hasIpv6 = false;
            while (networkInterfaces2.hasMoreElements()) {
                NetworkInterface networkInterface2 = networkInterfaces2.nextElement();
                if (networkInterface2.isUp()) {
                    if (!networkInterface2.isLoopback()) {
                        List<InterfaceAddress> interfaceAddresses2 = networkInterface2.getInterfaceAddresses();
                        for (int a2 = 0; a2 < interfaceAddresses2.size(); a2++) {
                            InetAddress inetAddress2 = interfaceAddresses2.get(a2).getAddress();
                            if (!inetAddress2.isLinkLocalAddress() && !inetAddress2.isLoopbackAddress()) {
                                if (!inetAddress2.isMulticastAddress()) {
                                    if (inetAddress2 instanceof Inet6Address) {
                                        hasIpv6 = true;
                                    } else if ((inetAddress2 instanceof Inet4Address) && !inetAddress2.getHostAddress().startsWith("192.0.0.")) {
                                        hasIpv4 = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (hasIpv4 || !hasIpv6) {
                return false;
            }
            return true;
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
    }

    private static class ResolveHostByNameTask extends AsyncTask<Void, Void, ResolvedDomain> {
        private ArrayList<Long> addresses = new ArrayList<>();
        private String currentHostName;

        public ResolveHostByNameTask(String hostName) {
            this.currentHostName = hostName;
        }

        public void addAddress(long address) {
            if (!this.addresses.contains(Long.valueOf(address))) {
                this.addresses.add(Long.valueOf(address));
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0073, code lost:
            r0 = r7.getJSONArray("Answer");
         */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00dd A[SYNTHETIC, Splitter:B:46:0x00dd] */
        /* JADX WARNING: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public im.bclpbkiauv.tgnet.ConnectionsManager.ResolvedDomain doInBackground(java.lang.Void... r14) {
            /*
                r13 = this;
                java.lang.String r0 = "Answer"
                r1 = 0
                r2 = 0
                r3 = 0
                java.net.URL r4 = new java.net.URL     // Catch:{ all -> 0x00c5 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c5 }
                r5.<init>()     // Catch:{ all -> 0x00c5 }
                java.lang.String r6 = "https://www.google.com/resolve?name="
                r5.append(r6)     // Catch:{ all -> 0x00c5 }
                java.lang.String r6 = r13.currentHostName     // Catch:{ all -> 0x00c5 }
                r5.append(r6)     // Catch:{ all -> 0x00c5 }
                java.lang.String r6 = "&type=A"
                r5.append(r6)     // Catch:{ all -> 0x00c5 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00c5 }
                r4.<init>(r5)     // Catch:{ all -> 0x00c5 }
                java.net.URLConnection r5 = r4.openConnection()     // Catch:{ all -> 0x00c5 }
                java.lang.String r6 = "User-Agent"
                java.lang.String r7 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r5.addRequestProperty(r6, r7)     // Catch:{ all -> 0x00c5 }
                java.lang.String r6 = "Host"
                java.lang.String r7 = "dns.google.com"
                r5.addRequestProperty(r6, r7)     // Catch:{ all -> 0x00c5 }
                r6 = 1000(0x3e8, float:1.401E-42)
                r5.setConnectTimeout(r6)     // Catch:{ all -> 0x00c5 }
                r6 = 2000(0x7d0, float:2.803E-42)
                r5.setReadTimeout(r6)     // Catch:{ all -> 0x00c5 }
                r5.connect()     // Catch:{ all -> 0x00c5 }
                java.io.InputStream r6 = r5.getInputStream()     // Catch:{ all -> 0x00c5 }
                r2 = r6
                java.io.ByteArrayOutputStream r6 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00c5 }
                r6.<init>()     // Catch:{ all -> 0x00c5 }
                r1 = r6
                r6 = 32768(0x8000, float:4.5918E-41)
                byte[] r6 = new byte[r6]     // Catch:{ all -> 0x00c5 }
            L_0x0051:
                int r7 = r2.read(r6)     // Catch:{ all -> 0x00c5 }
                if (r7 <= 0) goto L_0x005c
                r8 = 0
                r1.write(r6, r8, r7)     // Catch:{ all -> 0x00c5 }
                goto L_0x0051
            L_0x005c:
                r8 = -1
                if (r7 != r8) goto L_0x005f
            L_0x005f:
                org.json.JSONObject r7 = new org.json.JSONObject     // Catch:{ all -> 0x00c5 }
                java.lang.String r8 = new java.lang.String     // Catch:{ all -> 0x00c5 }
                byte[] r9 = r1.toByteArray()     // Catch:{ all -> 0x00c5 }
                r8.<init>(r9)     // Catch:{ all -> 0x00c5 }
                r7.<init>(r8)     // Catch:{ all -> 0x00c5 }
                boolean r8 = r7.has(r0)     // Catch:{ all -> 0x00c5 }
                if (r8 == 0) goto L_0x00b1
                org.json.JSONArray r0 = r7.getJSONArray(r0)     // Catch:{ all -> 0x00c5 }
                int r8 = r0.length()     // Catch:{ all -> 0x00c5 }
                if (r8 <= 0) goto L_0x00b1
                java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ all -> 0x00c5 }
                r9.<init>(r8)     // Catch:{ all -> 0x00c5 }
                r10 = 0
            L_0x0083:
                if (r10 >= r8) goto L_0x0095
                org.json.JSONObject r11 = r0.getJSONObject(r10)     // Catch:{ all -> 0x00c5 }
                java.lang.String r12 = "data"
                java.lang.String r11 = r11.getString(r12)     // Catch:{ all -> 0x00c5 }
                r9.add(r11)     // Catch:{ all -> 0x00c5 }
                int r10 = r10 + 1
                goto L_0x0083
            L_0x0095:
                im.bclpbkiauv.tgnet.ConnectionsManager$ResolvedDomain r10 = new im.bclpbkiauv.tgnet.ConnectionsManager$ResolvedDomain     // Catch:{ all -> 0x00c5 }
                long r11 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x00c5 }
                r10.<init>(r9, r11)     // Catch:{ all -> 0x00c5 }
                if (r2 == 0) goto L_0x00a9
                r2.close()     // Catch:{ all -> 0x00a4 }
                goto L_0x00a9
            L_0x00a4:
                r11 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r11)
                goto L_0x00aa
            L_0x00a9:
            L_0x00aa:
                r1.close()     // Catch:{ Exception -> 0x00af }
                goto L_0x00b0
            L_0x00af:
                r11 = move-exception
            L_0x00b0:
                return r10
            L_0x00b1:
                r3 = 1
                if (r2 == 0) goto L_0x00bd
                r2.close()     // Catch:{ all -> 0x00b8 }
                goto L_0x00bd
            L_0x00b8:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x00be
            L_0x00bd:
            L_0x00be:
                r1.close()     // Catch:{ Exception -> 0x00c3 }
            L_0x00c2:
                goto L_0x00db
            L_0x00c3:
                r0 = move-exception
                goto L_0x00db
            L_0x00c5:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0100 }
                if (r2 == 0) goto L_0x00d4
                r2.close()     // Catch:{ all -> 0x00cf }
                goto L_0x00d4
            L_0x00cf:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x00d5
            L_0x00d4:
            L_0x00d5:
                if (r1 == 0) goto L_0x00c2
                r1.close()     // Catch:{ Exception -> 0x00c3 }
                goto L_0x00c2
            L_0x00db:
                if (r3 != 0) goto L_0x00fe
                java.lang.String r0 = r13.currentHostName     // Catch:{ Exception -> 0x00fa }
                java.net.InetAddress r0 = java.net.InetAddress.getByName(r0)     // Catch:{ Exception -> 0x00fa }
                java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x00fa }
                r5 = 1
                r4.<init>(r5)     // Catch:{ Exception -> 0x00fa }
                java.lang.String r5 = r0.getHostAddress()     // Catch:{ Exception -> 0x00fa }
                r4.add(r5)     // Catch:{ Exception -> 0x00fa }
                im.bclpbkiauv.tgnet.ConnectionsManager$ResolvedDomain r5 = new im.bclpbkiauv.tgnet.ConnectionsManager$ResolvedDomain     // Catch:{ Exception -> 0x00fa }
                long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x00fa }
                r5.<init>(r4, r6)     // Catch:{ Exception -> 0x00fa }
                return r5
            L_0x00fa:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00fe:
                r0 = 0
                return r0
            L_0x0100:
                r0 = move-exception
                if (r2 == 0) goto L_0x010c
                r2.close()     // Catch:{ all -> 0x0107 }
                goto L_0x010c
            L_0x0107:
                r4 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r4)
                goto L_0x010d
            L_0x010c:
            L_0x010d:
                if (r1 == 0) goto L_0x0115
                r1.close()     // Catch:{ Exception -> 0x0113 }
                goto L_0x0115
            L_0x0113:
                r4 = move-exception
                goto L_0x0116
            L_0x0115:
            L_0x0116:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.tgnet.ConnectionsManager.ResolveHostByNameTask.doInBackground(java.lang.Void[]):im.bclpbkiauv.tgnet.ConnectionsManager$ResolvedDomain");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(ResolvedDomain result) {
            if (result != null) {
                ConnectionsManager.dnsCache.put(this.currentHostName, result);
                int N = this.addresses.size();
                for (int a = 0; a < N; a++) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, this.addresses.get(a).longValue(), result.getAddress());
                }
            } else {
                int N2 = this.addresses.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, this.addresses.get(a2).longValue(), "");
                }
            }
            ConnectionsManager.resolvingHostnameTasks.remove(this.currentHostName);
        }
    }

    private static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public DnsTxtLoadTask(int instance) {
            this.currentAccount = instance;
        }

        /* access modifiers changed from: protected */
        public NativeByteBuffer doInBackground(Void... voids) {
            if (BuildVars.DEBUG_VERSION) {
                Log.i("connection", "java DnsTxtLoadTask doInBackground ===> ");
            }
            NetworkConfig.getInstance().applyNetconfig(this.currentAccount);
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new Runnable(result) {
                private final /* synthetic */ NativeByteBuffer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ConnectionsManager.DnsTxtLoadTask.this.lambda$onPostExecute$0$ConnectionsManager$DnsTxtLoadTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$0$ConnectionsManager$DnsTxtLoadTask(NativeByteBuffer result) {
            if (result != null) {
                ConnectionsManager.native_applyBackupConfig(this.currentAccount, result.address);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get dns txt result");
            }
            AsyncTask unused = ConnectionsManager.currentTask = null;
        }
    }

    private static class FirebaseTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private FirebaseRemoteConfig firebaseRemoteConfig;

        public FirebaseTask(int instance) {
            this.currentAccount = instance;
        }

        /* access modifiers changed from: protected */
        public NativeByteBuffer doInBackground(Void... voids) {
            if (BuildVars.DEBUG_VERSION) {
                Log.i("connection", "java FirebaseTask doInBackground ===> ");
            }
            NetworkConfig.getInstance().applyNetconfig(this.currentAccount);
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new Runnable(result) {
                private final /* synthetic */ NativeByteBuffer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ConnectionsManager.FirebaseTask.this.lambda$onPostExecute$0$ConnectionsManager$FirebaseTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$0$ConnectionsManager$FirebaseTask(NativeByteBuffer result) {
            if (result != null) {
                ConnectionsManager.native_applyBackupConfig(this.currentAccount, result.address);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get dns txt result");
            }
            AsyncTask unused = ConnectionsManager.currentTask = null;
        }
    }
}
