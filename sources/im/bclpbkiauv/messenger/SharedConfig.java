package im.bclpbkiauv.messenger;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.zhy.http.okhttp.OkHttpUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.SerializedData;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;

public class SharedConfig {
    public static final int PERFORMANCE_CLASS_AVERAGE = 1;
    public static final int PERFORMANCE_CLASS_HIGH = 2;
    public static final int PERFORMANCE_CLASS_LOW = 0;
    public static boolean allowBigEmoji;
    public static boolean allowScreenCapture;
    public static boolean appLocked;
    public static boolean archiveHidden;
    public static int autoLockIn = 3600;
    public static boolean autoplayGifs = true;
    public static boolean autoplayVideo = true;
    public static int badPasscodeTries;
    private static boolean configLoaded;
    public static ProxyInfo currentProxy;
    public static boolean customTabs = true;
    private static int devicePerformanceClass;
    public static boolean directShare = true;
    public static long directShareHash;
    public static int distanceSystemType;
    public static boolean drawDialogIcons = true;
    public static int fontSize = AndroidUtilities.dp(16.0f);
    public static boolean hasCameraCache;
    public static boolean inappCamera = true;
    public static boolean isWaitingForPasscodeEnter;
    public static int keepMedia = 2;
    public static long lastAppPauseTime;
    public static int lastKeepMediaCheckTime;
    private static int lastLocalId = -210000;
    public static int lastPauseTime;
    public static String lastUpdateVersion;
    public static long lastUptimeMillis;
    private static final Object localIdSync = new Object();
    public static boolean loopStickers;
    public static int mapPreviewType = 2;
    public static boolean noSoundHintShowed = false;
    public static String passcodeHash = "";
    public static long passcodeRetryInMs;
    public static byte[] passcodeSalt = new byte[0];
    public static int passcodeType;
    public static int passportConfigHash;
    private static String passportConfigJson = "";
    private static HashMap<String, String> passportConfigMap;
    public static boolean playOrderReversed;
    public static ArrayList<ProxyInfo> proxyList = new ArrayList<>();
    private static boolean proxyListLoaded;
    public static byte[] pushAuthKey;
    public static byte[] pushAuthKeyId;
    public static String pushString = "";
    public static String pushStringStatus = "";
    public static boolean raiseToSpeak = true;
    public static int repeatMode;
    public static boolean roundCamera16to9 = true;
    public static boolean saveIncomingPhotos;
    public static boolean saveStreamMedia = true;
    public static boolean saveToGallery;
    public static boolean showNotificationsForAllAccounts = true;
    public static boolean shuffleMusic;
    public static boolean sortContactsByName;
    public static boolean streamAllVideo = false;
    public static boolean streamMedia = true;
    public static boolean streamMkv = false;
    public static int suggestStickers;
    private static final Object sync = new Object();
    public static boolean useFingerprint = true;
    public static boolean useSystemEmoji;
    public static boolean useThreeLinesLayout;

    static {
        loadConfig();
    }

    public static class ProxyInfo {
        public String address;
        public boolean available;
        public long availableCheckTime;
        public boolean checking;
        public String password;
        public long ping;
        public int port;
        public long proxyCheckPingId;
        public String secret;
        public String username;

        public ProxyInfo(String a, int p, String u, String pw, String s) {
            this.address = a;
            this.port = p;
            this.username = u;
            this.password = pw;
            this.secret = s;
            if (a == null) {
                this.address = "";
            }
            if (this.password == null) {
                this.password = "";
            }
            if (this.username == null) {
                this.username = "";
            }
            if (this.secret == null) {
                this.secret = "";
            }
        }
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
                editor.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                editor.putString("passcodeHash1", passcodeHash);
                editor.putString("passcodeSalt", passcodeSalt.length > 0 ? Base64.encodeToString(passcodeSalt, 0) : "");
                editor.putBoolean("appLocked", appLocked);
                editor.putInt("passcodeType", passcodeType);
                editor.putLong("passcodeRetryInMs", passcodeRetryInMs);
                editor.putLong("lastUptimeMillis", lastUptimeMillis);
                editor.putInt("badPasscodeTries", badPasscodeTries);
                editor.putInt("autoLockIn", autoLockIn);
                editor.putInt("lastPauseTime", lastPauseTime);
                editor.putLong("lastAppPauseTime", lastAppPauseTime);
                editor.putString("lastUpdateVersion2", lastUpdateVersion);
                editor.putBoolean("useFingerprint", useFingerprint);
                editor.putBoolean("allowScreenCapture", allowScreenCapture);
                editor.putString("pushString2", pushString);
                editor.putString("pushAuthKey", pushAuthKey != null ? Base64.encodeToString(pushAuthKey, 0) : "");
                editor.putInt("lastLocalId", lastLocalId);
                editor.putString("passportConfigJson", passportConfigJson);
                editor.putInt("passportConfigHash", passportConfigHash);
                editor.putBoolean("sortContactsByName", sortContactsByName);
                editor.commit();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static int getLastLocalId() {
        int value;
        synchronized (localIdSync) {
            value = lastLocalId;
            lastLocalId = value - 1;
        }
        return value;
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (!configLoaded) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
                saveIncomingPhotos = preferences.getBoolean("saveIncomingPhotos", false);
                passcodeHash = preferences.getString("passcodeHash1", "");
                appLocked = preferences.getBoolean("appLocked", false);
                passcodeType = preferences.getInt("passcodeType", 0);
                passcodeRetryInMs = preferences.getLong("passcodeRetryInMs", 0);
                lastUptimeMillis = preferences.getLong("lastUptimeMillis", 0);
                badPasscodeTries = preferences.getInt("badPasscodeTries", 0);
                autoLockIn = preferences.getInt("autoLockIn", 3600);
                lastPauseTime = preferences.getInt("lastPauseTime", 0);
                lastAppPauseTime = preferences.getLong("lastAppPauseTime", 0);
                useFingerprint = preferences.getBoolean("useFingerprint", true);
                lastUpdateVersion = preferences.getString("lastUpdateVersion2", "3.5");
                allowScreenCapture = preferences.getBoolean("allowScreenCapture", false);
                lastLocalId = preferences.getInt("lastLocalId", -210000);
                pushString = preferences.getString("pushString2", "");
                passportConfigJson = preferences.getString("passportConfigJson", "");
                passportConfigHash = preferences.getInt("passportConfigHash", 0);
                String authKeyString = preferences.getString("pushAuthKey", (String) null);
                if (!TextUtils.isEmpty(authKeyString)) {
                    pushAuthKey = Base64.decode(authKeyString, 0);
                }
                if (passcodeHash.length() > 0 && lastPauseTime == 0) {
                    lastPauseTime = (int) ((System.currentTimeMillis() / 1000) - 600);
                }
                String passcodeSaltString = preferences.getString("passcodeSalt", "");
                if (passcodeSaltString.length() > 0) {
                    passcodeSalt = Base64.decode(passcodeSaltString, 0);
                } else {
                    passcodeSalt = new byte[0];
                }
                SharedPreferences preferences2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                saveToGallery = preferences2.getBoolean("save_gallery", false);
                autoplayGifs = preferences2.getBoolean("autoplay_gif", true);
                autoplayVideo = preferences2.getBoolean("autoplay_video", true);
                mapPreviewType = preferences2.getInt("mapPreviewType", 2);
                raiseToSpeak = preferences2.getBoolean("raise_to_speak", true);
                customTabs = preferences2.getBoolean("custom_tabs", true);
                directShare = preferences2.getBoolean("direct_share", true);
                shuffleMusic = preferences2.getBoolean("shuffleMusic", false);
                playOrderReversed = preferences2.getBoolean("playOrderReversed", false);
                inappCamera = preferences2.getBoolean("inappCamera", true);
                hasCameraCache = preferences2.contains("cameraCache");
                roundCamera16to9 = true;
                repeatMode = preferences2.getInt("repeatMode", 0);
                fontSize = preferences2.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
                allowBigEmoji = preferences2.getBoolean("allowBigEmoji", true);
                useSystemEmoji = preferences2.getBoolean("useSystemEmoji", false);
                streamMedia = preferences2.getBoolean("streamMedia", true);
                saveStreamMedia = preferences2.getBoolean("saveStreamMedia", true);
                streamAllVideo = preferences2.getBoolean("streamAllVideo", BuildVars.DEBUG_VERSION);
                streamMkv = preferences2.getBoolean("streamMkv", false);
                suggestStickers = preferences2.getInt("suggestStickers", 0);
                sortContactsByName = preferences2.getBoolean("sortContactsByName", false);
                noSoundHintShowed = preferences2.getBoolean("noSoundHintShowed", false);
                directShareHash = preferences2.getLong("directShareHash", 0);
                useThreeLinesLayout = preferences2.getBoolean("useThreeLinesLayout", false);
                archiveHidden = preferences2.getBoolean("archiveHidden", false);
                distanceSystemType = preferences2.getInt("distanceSystemType", 0);
                devicePerformanceClass = preferences2.getInt("devicePerformanceClass", -1);
                loopStickers = preferences2.getBoolean("loopStickers", true);
                keepMedia = preferences2.getInt("keep_media", 2);
                lastKeepMediaCheckTime = preferences2.getInt("lastKeepMediaCheckTime", 0);
                showNotificationsForAllAccounts = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("AllAccounts", true);
                configLoaded = true;
            }
        }
    }

    public static void increaseBadPasscodeTries() {
        int i = badPasscodeTries + 1;
        badPasscodeTries = i;
        if (i >= 3) {
            if (i == 3) {
                passcodeRetryInMs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
            } else if (i == 4) {
                passcodeRetryInMs = OkHttpUtils.DEFAULT_MILLISECONDS;
            } else if (i == 5) {
                passcodeRetryInMs = 15000;
            } else if (i == 6) {
                passcodeRetryInMs = 20000;
            } else if (i != 7) {
                passcodeRetryInMs = 30000;
            } else {
                passcodeRetryInMs = 25000;
            }
            lastUptimeMillis = SystemClock.elapsedRealtime();
        }
        saveConfig();
    }

    public static boolean isPassportConfigLoaded() {
        return passportConfigMap != null;
    }

    public static void setPassportConfig(String json, int hash) {
        passportConfigMap = null;
        passportConfigJson = json;
        passportConfigHash = hash;
        saveConfig();
        getCountryLangs();
    }

    public static HashMap<String, String> getCountryLangs() {
        if (passportConfigMap == null) {
            passportConfigMap = new HashMap<>();
            try {
                JSONObject object = new JSONObject(passportConfigJson);
                Iterator<String> iter = object.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    passportConfigMap.put(key.toUpperCase(), object.getString(key).toUpperCase());
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        return passportConfigMap;
    }

    public static boolean checkPasscode(String passcode) {
        if (passcodeSalt.length == 0) {
            boolean result = Utilities.MD5(passcode).equals(passcodeHash);
            if (result) {
                try {
                    passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(passcodeSalt);
                    byte[] passcodeBytes = passcode.getBytes("UTF-8");
                    byte[] bytes = new byte[(passcodeBytes.length + 32)];
                    System.arraycopy(passcodeSalt, 0, bytes, 0, 16);
                    System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                    System.arraycopy(passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
                    saveConfig();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            return result;
        }
        try {
            byte[] passcodeBytes2 = passcode.getBytes("UTF-8");
            byte[] bytes2 = new byte[(passcodeBytes2.length + 32)];
            System.arraycopy(passcodeSalt, 0, bytes2, 0, 16);
            System.arraycopy(passcodeBytes2, 0, bytes2, 16, passcodeBytes2.length);
            System.arraycopy(passcodeSalt, 0, bytes2, passcodeBytes2.length + 16, 16);
            return passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(bytes2, 0, bytes2.length)));
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return false;
        }
    }

    public static void clearConfig() {
        saveIncomingPhotos = false;
        appLocked = false;
        passcodeType = 0;
        passcodeRetryInMs = 0;
        lastUptimeMillis = 0;
        badPasscodeTries = 0;
        passcodeHash = "";
        passcodeSalt = new byte[0];
        autoLockIn = 3600;
        lastPauseTime = 0;
        useFingerprint = true;
        isWaitingForPasscodeEnter = false;
        allowScreenCapture = false;
        lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
        saveConfig();
    }

    public static void setSuggestStickers(int type) {
        suggestStickers = type;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("suggestStickers", suggestStickers);
        editor.commit();
    }

    public static void setKeepMedia(int value) {
        keepMedia = value;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("keep_media", keepMedia);
        editor.commit();
    }

    public static void checkKeepMedia() {
        int time = (int) (System.currentTimeMillis() / 1000);
        if (keepMedia != 2 && Math.abs(time - lastKeepMediaCheckTime) >= 86400) {
            lastKeepMediaCheckTime = time;
            Utilities.globalQueue.postRunnable(new Runnable(time) {
                private final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    SharedConfig.lambda$checkKeepMedia$0(this.f$0);
                }
            });
        }
    }

    static /* synthetic */ void lambda$checkKeepMedia$0(int time) {
        int days;
        int i = keepMedia;
        if (i == 0) {
            days = 7;
        } else if (i == 1) {
            days = 30;
        } else {
            days = 3;
        }
        long currentTime = (long) (time - (86400 * days));
        SparseArray<File> paths = ImageLoader.getInstance().createMediaPaths();
        for (int a = 0; a < paths.size(); a++) {
            if (paths.keyAt(a) != 4) {
                try {
                    Utilities.clearDir(paths.valueAt(a).getAbsolutePath(), 0, currentTime);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("lastKeepMediaCheckTime", lastKeepMediaCheckTime);
        editor.commit();
    }

    public static void toggleLoopStickers() {
        loopStickers = !loopStickers;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("loopStickers", loopStickers);
        editor.commit();
    }

    public static void toggleBigEmoji() {
        allowBigEmoji = !allowBigEmoji;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("allowBigEmoji", allowBigEmoji);
        editor.commit();
    }

    public static void toggleShuffleMusic(int type) {
        if (type == 2) {
            shuffleMusic = !shuffleMusic;
        } else {
            playOrderReversed = !playOrderReversed;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("shuffleMusic", shuffleMusic);
        editor.putBoolean("playOrderReversed", playOrderReversed);
        editor.commit();
    }

    public static void toggleRepeatMode() {
        int i = repeatMode + 1;
        repeatMode = i;
        if (i > 2) {
            repeatMode = 0;
        }
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("repeatMode", repeatMode);
        editor.commit();
    }

    public static void toggleSaveToGallery() {
        saveToGallery = !saveToGallery;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("save_gallery", saveToGallery);
        editor.commit();
        checkSaveToGalleryFiles();
    }

    public static void toggleAutoplayGifs() {
        autoplayGifs = !autoplayGifs;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("autoplay_gif", autoplayGifs);
        editor.commit();
    }

    public static void setUseThreeLinesLayout(boolean value) {
        useThreeLinesLayout = value;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("useThreeLinesLayout", useThreeLinesLayout);
        editor.commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.dialogsNeedReload, true);
    }

    public static void toggleArchiveHidden() {
        archiveHidden = !archiveHidden;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("archiveHidden", archiveHidden);
        editor.commit();
    }

    public static void toggleAutoplayVideo() {
        autoplayVideo = !autoplayVideo;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("autoplay_video", autoplayVideo);
        editor.commit();
    }

    public static boolean isSecretMapPreviewSet() {
        return MessagesController.getGlobalMainSettings().contains("mapPreviewType");
    }

    public static void setSecretMapPreviewType(int value) {
        mapPreviewType = value;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("mapPreviewType", mapPreviewType);
        editor.commit();
    }

    public static void setNoSoundHintShowed(boolean value) {
        if (noSoundHintShowed != value) {
            noSoundHintShowed = value;
            SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
            editor.putBoolean("noSoundHintShowed", noSoundHintShowed);
            editor.commit();
        }
    }

    public static void toogleRaiseToSpeak() {
        raiseToSpeak = !raiseToSpeak;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("raise_to_speak", raiseToSpeak);
        editor.commit();
    }

    public static void toggleCustomTabs() {
        customTabs = !customTabs;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("custom_tabs", customTabs);
        editor.commit();
    }

    public static void toggleDirectShare() {
        directShare = !directShare;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("direct_share", directShare);
        editor.commit();
    }

    public static void toggleStreamMedia() {
        streamMedia = !streamMedia;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("streamMedia", streamMedia);
        editor.commit();
    }

    public static void toggleSortContactsByName() {
        sortContactsByName = !sortContactsByName;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("sortContactsByName", sortContactsByName);
        editor.commit();
    }

    public static void toggleStreamAllVideo() {
        streamAllVideo = !streamAllVideo;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("streamAllVideo", streamAllVideo);
        editor.commit();
    }

    public static void toggleStreamMkv() {
        streamMkv = !streamMkv;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("streamMkv", streamMkv);
        editor.commit();
    }

    public static void toggleSaveStreamMedia() {
        saveStreamMedia = !saveStreamMedia;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("saveStreamMedia", saveStreamMedia);
        editor.commit();
    }

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("inappCamera", inappCamera);
        editor.commit();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 = !roundCamera16to9;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("roundCamera16to9", roundCamera16to9);
        editor.commit();
    }

    public static void setDistanceSystemType(int type) {
        distanceSystemType = type;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("distanceSystemType", distanceSystemType);
        editor.commit();
        LocaleController.resetImperialSystemType();
    }

    public static void loadProxyList() {
        if (!proxyListLoaded) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            String proxyAddress = preferences.getString("proxy_ip", "");
            String proxyUsername = preferences.getString("proxy_user", "");
            String proxyPassword = preferences.getString("proxy_pass", "");
            String proxySecret = preferences.getString("proxy_secret", "");
            int proxyPort = preferences.getInt("proxy_port", 1080);
            proxyListLoaded = true;
            proxyList.clear();
            currentProxy = null;
            String list = preferences.getString("proxy_list", (String) null);
            if (!TextUtils.isEmpty(list)) {
                SerializedData data = new SerializedData(Base64.decode(list, 0));
                int count = data.readInt32(false);
                for (int a = 0; a < count; a++) {
                    ProxyInfo proxyInfo = new ProxyInfo(data.readString(false), data.readInt32(false), data.readString(false), data.readString(false), data.readString(false));
                    proxyList.add(proxyInfo);
                    if (currentProxy == null && !TextUtils.isEmpty(proxyAddress) && proxyAddress.equals(proxyInfo.address) && proxyPort == proxyInfo.port && proxyUsername.equals(proxyInfo.username) && proxyPassword.equals(proxyInfo.password)) {
                        currentProxy = proxyInfo;
                    }
                }
                data.cleanup();
            }
            if (currentProxy == null && !TextUtils.isEmpty(proxyAddress)) {
                ProxyInfo info = new ProxyInfo(proxyAddress, proxyPort, proxyUsername, proxyPassword, proxySecret);
                currentProxy = info;
                proxyList.add(0, info);
            }
        }
    }

    public static void saveProxyList() {
        SerializedData serializedData = new SerializedData();
        int count = proxyList.size();
        serializedData.writeInt32(count);
        for (int a = 0; a < count; a++) {
            ProxyInfo info = proxyList.get(a);
            String str = "";
            serializedData.writeString(info.address != null ? info.address : str);
            serializedData.writeInt32(info.port);
            serializedData.writeString(info.username != null ? info.username : str);
            serializedData.writeString(info.password != null ? info.password : str);
            if (info.secret != null) {
                str = info.secret;
            }
            serializedData.writeString(str);
        }
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putString("proxy_list", Base64.encodeToString(serializedData.toByteArray(), 2)).commit();
        serializedData.cleanup();
    }

    public static ProxyInfo addProxy(ProxyInfo proxyInfo) {
        loadProxyList();
        int count = proxyList.size();
        for (int a = 0; a < count; a++) {
            ProxyInfo info = proxyList.get(a);
            if (proxyInfo.address.equals(info.address) && proxyInfo.port == info.port && proxyInfo.username.equals(info.username) && proxyInfo.password.equals(info.password) && proxyInfo.secret.equals(info.secret)) {
                return info;
            }
        }
        proxyList.add(proxyInfo);
        saveProxyList();
        return proxyInfo;
    }

    public static void deleteProxy(ProxyInfo proxyInfo) {
        if (currentProxy == proxyInfo) {
            currentProxy = null;
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean enabled = preferences.getBoolean("proxy_enabled", false);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("proxy_ip", "");
            editor.putString("proxy_pass", "");
            editor.putString("proxy_user", "");
            editor.putString("proxy_secret", "");
            editor.putInt("proxy_port", 1080);
            editor.putBoolean("proxy_enabled", false);
            editor.putBoolean("proxy_enabled_calls", false);
            editor.commit();
            if (enabled) {
                ConnectionsManager.setProxySettings(false, "", 0, "", "", "");
            }
        }
        proxyList.remove(proxyInfo);
        saveProxyList();
    }

    public static void checkSaveToGalleryFiles() {
        try {
            File path = new File(Environment.getExternalStorageDirectory(), "Yixin");
            File imagePath = new File(path, "Yixin Images");
            imagePath.mkdir();
            File videoPath = new File(path, "Yixin Video");
            videoPath.mkdir();
            if (saveToGallery) {
                if (imagePath.isDirectory()) {
                    new File(imagePath, ".nomedia").delete();
                }
                if (videoPath.isDirectory()) {
                    new File(videoPath, ".nomedia").delete();
                    return;
                }
                return;
            }
            if (imagePath.isDirectory()) {
                new File(imagePath, ".nomedia").createNewFile();
            }
            if (videoPath.isDirectory()) {
                new File(videoPath, ".nomedia").createNewFile();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static int getDevicePerfomanceClass() {
        if (devicePerformanceClass == -1) {
            int maxCpuFreq = -1;
            try {
                RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
                String line = reader.readLine();
                if (line != null) {
                    maxCpuFreq = Utilities.parseInt(line).intValue() / 1000;
                }
                reader.close();
            } catch (Throwable th) {
            }
            int androidVersion = Build.VERSION.SDK_INT;
            int cpuCount = ConnectionsManager.CPU_COUNT;
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
            if (androidVersion < 21 || cpuCount <= 2 || memoryClass <= 100 || ((cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250) || (cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21))) {
                devicePerformanceClass = 0;
            } else if (cpuCount < 8 || memoryClass <= 160 || (maxCpuFreq != -1 && maxCpuFreq <= 1650)) {
                devicePerformanceClass = 1;
            } else {
                devicePerformanceClass = 2;
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("device performance info (cpu_count = " + cpuCount + ", freq = " + maxCpuFreq + ", memoryClass = " + memoryClass + ", android version " + androidVersion + SQLBuilder.PARENTHESES_RIGHT);
            }
        }
        return devicePerformanceClass;
    }
}
