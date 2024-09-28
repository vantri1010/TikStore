package im.bclpbkiauv.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Xml;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.time.FastDateFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import kotlin.text.Typography;
import org.xmlpull.v1.XmlPullParser;

public class LocaleController {
    private static volatile LocaleController Instance = null;
    static final int QUANTITY_FEW = 8;
    static final int QUANTITY_MANY = 16;
    static final int QUANTITY_ONE = 2;
    static final int QUANTITY_OTHER = 0;
    static final int QUANTITY_TWO = 4;
    static final int QUANTITY_ZERO = 1;
    public static boolean is24HourFormat = false;
    public static boolean isRTL = false;
    public static int nameDisplayOrder = 1;
    private static Boolean useImperialSystemType;
    private HashMap<String, PluralRules> allRules = new HashMap<>();
    private boolean changingConfiguration = false;
    public FastDateFormat chatDate;
    public FastDateFormat chatFullDate;
    private HashMap<String, String> currencyValues;
    private Locale currentLocale;
    private LocaleInfo currentLocaleInfo;
    private PluralRules currentPluralRules;
    private String currentSystemLocale;
    public FastDateFormat formatterBannedUntil;
    public FastDateFormat formatterBannedUntilThisYear;
    public FastDateFormat formatterDay;
    public FastDateFormat formatterDayMonth;
    public FastDateFormat formatterDayNoly;
    public FastDateFormat formatterScheduleDay;
    public FastDateFormat[] formatterScheduleSend = new FastDateFormat[6];
    public FastDateFormat formatterScheduleYear;
    public FastDateFormat formatterStats;
    public FastDateFormat formatterWeek;
    public FastDateFormat formatterYear;
    public FastDateFormat formatterYearMax;
    private String languageOverride;
    public ArrayList<LocaleInfo> languages = new ArrayList<>();
    public HashMap<String, LocaleInfo> languagesDict = new HashMap<>();
    private boolean loadingRemoteLanguages;
    private HashMap<String, String> localeValues = new HashMap<>();
    private ArrayList<LocaleInfo> otherLanguages = new ArrayList<>();
    private boolean reloadLastFile;
    public ArrayList<LocaleInfo> remoteLanguages = new ArrayList<>();
    public HashMap<String, LocaleInfo> remoteLanguagesDict = new HashMap<>();
    private HashMap<String, String> ruTranslitChars;
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;
    public ArrayList<LocaleInfo> unofficialLanguages = new ArrayList<>();

    public static abstract class PluralRules {
        /* access modifiers changed from: package-private */
        public abstract int quantityForNumber(int i);
    }

    private class TimeZoneChangedReceiver extends BroadcastReceiver {
        private TimeZoneChangedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new Runnable() {
                public final void run() {
                    LocaleController.TimeZoneChangedReceiver.this.lambda$onReceive$0$LocaleController$TimeZoneChangedReceiver();
                }
            });
        }

        public /* synthetic */ void lambda$onReceive$0$LocaleController$TimeZoneChangedReceiver() {
            if (!LocaleController.this.formatterDayMonth.getTimeZone().equals(TimeZone.getDefault())) {
                LocaleController.getInstance().recreateFormatters();
            }
        }
    }

    public static class LocaleInfo {
        public String baseLangCode;
        public int baseVersion;
        public boolean builtIn;
        public boolean isRtl;
        public String name;
        public String nameEnglish;
        public String pathToFile;
        public String pluralLangCode;
        public int serverIndex;
        public String shortName;
        public int version;

        public String getSaveString() {
            String langCode = this.baseLangCode;
            if (langCode == null) {
                langCode = "";
            }
            if (TextUtils.isEmpty(this.pluralLangCode)) {
                String str = this.shortName;
            } else {
                String str2 = this.pluralLangCode;
            }
            return this.name + LogUtils.VERTICAL + this.nameEnglish + LogUtils.VERTICAL + this.shortName + LogUtils.VERTICAL + this.pathToFile + LogUtils.VERTICAL + this.version + LogUtils.VERTICAL + langCode + LogUtils.VERTICAL + this.pluralLangCode + LogUtils.VERTICAL + (this.isRtl ? 1 : 0) + LogUtils.VERTICAL + this.baseVersion + LogUtils.VERTICAL + this.serverIndex;
        }

        public static LocaleInfo createWithString(String string) {
            if (string == null || string.length() == 0) {
                return null;
            }
            String[] args = string.split("\\|");
            LocaleInfo localeInfo = null;
            if (args.length >= 4) {
                localeInfo = new LocaleInfo();
                localeInfo.name = args[0];
                localeInfo.nameEnglish = args[1];
                localeInfo.shortName = args[2].toLowerCase();
                localeInfo.pathToFile = args[3];
                if (args.length >= 5) {
                    localeInfo.version = Utilities.parseInt(args[4]).intValue();
                }
                localeInfo.baseLangCode = args.length >= 6 ? args[5] : "";
                localeInfo.pluralLangCode = args.length >= 7 ? args[6] : localeInfo.shortName;
                if (args.length >= 9) {
                    localeInfo.baseVersion = Utilities.parseInt(args[8]).intValue();
                }
                if (args.length >= 10) {
                    localeInfo.serverIndex = Utilities.parseInt(args[9]).intValue();
                } else {
                    localeInfo.serverIndex = Integer.MAX_VALUE;
                }
                if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    localeInfo.baseLangCode = localeInfo.baseLangCode.replace("-", "_");
                }
            }
            return localeInfo;
        }

        public File getPathToFile() {
            if (isRemote()) {
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                return new File(filesDirFixed, "remote_" + this.shortName + ".xml");
            } else if (isUnofficial()) {
                File filesDirFixed2 = ApplicationLoader.getFilesDirFixed();
                return new File(filesDirFixed2, "unofficial_" + this.shortName + ".xml");
            } else if (!TextUtils.isEmpty(this.pathToFile)) {
                return new File(this.pathToFile);
            } else {
                return null;
            }
        }

        public File getPathToBaseFile() {
            if (!isUnofficial()) {
                return null;
            }
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            return new File(filesDirFixed, "unofficial_base_" + this.shortName + ".xml");
        }

        public String getKey() {
            if (this.pathToFile != null && !isRemote() && !isUnofficial()) {
                return "local_" + this.shortName;
            } else if (!isUnofficial()) {
                return this.shortName;
            } else {
                return "unofficial_" + this.shortName;
            }
        }

        public boolean hasBaseLang() {
            return isUnofficial() && !TextUtils.isEmpty(this.baseLangCode) && !this.baseLangCode.equals(this.shortName);
        }

        public boolean isRemote() {
            return "remote".equals(this.pathToFile);
        }

        public boolean isUnofficial() {
            return "unofficial".equals(this.pathToFile);
        }

        public boolean isLocal() {
            return !TextUtils.isEmpty(this.pathToFile) && !isRemote() && !isUnofficial();
        }

        public boolean isBuiltIn() {
            return this.builtIn;
        }

        public String getLangCode() {
            return this.shortName.replace("_", "-");
        }

        public String getBaseLangCode() {
            String str = this.baseLangCode;
            return str == null ? "" : str.replace("_", "-");
        }
    }

    public static LocaleController getInstance() {
        LocaleController localInstance = Instance;
        if (localInstance == null) {
            synchronized (LocaleController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    LocaleController localeController = new LocaleController();
                    localInstance = localeController;
                    Instance = localeController;
                }
            }
        }
        return localInstance;
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x0074 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0077 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x007a A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x007e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0081 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0084 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0085 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0088 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getLanguageShortNametoBdTransName() {
        /*
            r4 = this;
            im.bclpbkiauv.messenger.LocaleController$LocaleInfo r0 = r4.currentLocaleInfo
            java.lang.String r0 = r0.shortName
            int r1 = r0.hashCode()
            r2 = 3201(0xc81, float:4.486E-42)
            java.lang.String r3 = "de"
            if (r1 == r2) goto L_0x0068
            r2 = 3276(0xccc, float:4.59E-42)
            if (r1 == r2) goto L_0x005e
            r2 = 3383(0xd37, float:4.74E-42)
            if (r1 == r2) goto L_0x0054
            r2 = 3428(0xd64, float:4.804E-42)
            if (r1 == r2) goto L_0x004a
            r2 = 3763(0xeb3, float:5.273E-42)
            if (r1 == r2) goto L_0x003f
            r2 = 115862300(0x6e7eb1c, float:8.7238005E-35)
            if (r1 == r2) goto L_0x0034
            r2 = 115862836(0x6e7ed34, float:8.724108E-35)
            if (r1 == r2) goto L_0x0029
        L_0x0028:
            goto L_0x0070
        L_0x0029:
            java.lang.String r1 = "zh_tw"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0028
            r0 = 6
            goto L_0x0071
        L_0x0034:
            java.lang.String r1 = "zh_cn"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0028
            r0 = 5
            goto L_0x0071
        L_0x003f:
            java.lang.String r1 = "vi"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0028
            r0 = 0
            goto L_0x0071
        L_0x004a:
            java.lang.String r1 = "ko"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0028
            r0 = 3
            goto L_0x0071
        L_0x0054:
            java.lang.String r1 = "ja"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0028
            r0 = 4
            goto L_0x0071
        L_0x005e:
            java.lang.String r1 = "fr"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0028
            r0 = 1
            goto L_0x0071
        L_0x0068:
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0028
            r0 = 2
            goto L_0x0071
        L_0x0070:
            r0 = -1
        L_0x0071:
            switch(r0) {
                case 0: goto L_0x0088;
                case 1: goto L_0x0085;
                case 2: goto L_0x0084;
                case 3: goto L_0x0081;
                case 4: goto L_0x007e;
                case 5: goto L_0x007a;
                case 6: goto L_0x0077;
                default: goto L_0x0074;
            }
        L_0x0074:
            java.lang.String r0 = "en"
            return r0
        L_0x0077:
            java.lang.String r0 = "cht"
            return r0
        L_0x007a:
            java.lang.String r0 = "zh"
            return r0
        L_0x007e:
            java.lang.String r0 = "jp"
            return r0
        L_0x0081:
            java.lang.String r0 = "kor"
            return r0
        L_0x0084:
            return r3
        L_0x0085:
            java.lang.String r0 = "fra"
            return r0
        L_0x0088:
            java.lang.String r0 = "vie"
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.LocaleController.getLanguageShortNametoBdTransName():java.lang.String");
    }

    public LocaleController() {
        addRules(new String[]{"bem", "brx", "da", "de", "el", "en", "eo", "es", "et", "fi", "fo", "gl", "he", "iw", "it", "nb", "nl", "nn", "no", "sv", "af", "bg", "bn", "ca", "eu", "fur", "fy", "gu", "ha", "is", "ku", "lb", "ml", "mr", "nah", "ne", "om", "or", "pa", "pap", "ps", "so", "sq", "sw", "ta", "te", "tk", "ur", "zu", "mn", "gsw", "chr", "rm", "pt", "an", "ast"}, new PluralRules_One());
        addRules(new String[]{"cs", "sk"}, new PluralRules_Czech());
        addRules(new String[]{"ff", "fr", "kab"}, new PluralRules_French());
        addRules(new String[]{"hr", "ru", "sr", "uk", "be", "bs", "sh"}, new PluralRules_Balkan());
        addRules(new String[]{"lv"}, new PluralRules_Latvian());
        addRules(new String[]{"lt"}, new PluralRules_Lithuanian());
        addRules(new String[]{"pl"}, new PluralRules_Polish());
        addRules(new String[]{"ro", "mo"}, new PluralRules_Romanian());
        addRules(new String[]{"sl"}, new PluralRules_Slovenian());
        addRules(new String[]{"ar"}, new PluralRules_Arabic());
        addRules(new String[]{"mk"}, new PluralRules_Macedonian());
        addRules(new String[]{"cy"}, new PluralRules_Welsh());
        addRules(new String[]{TtmlNode.TAG_BR}, new PluralRules_Breton());
        addRules(new String[]{"lag"}, new PluralRules_Langi());
        addRules(new String[]{"shi"}, new PluralRules_Tachelhit());
        addRules(new String[]{"mt"}, new PluralRules_Maltese());
        addRules(new String[]{"ga", "se", "sma", "smi", "smj", "smn", "sms"}, new PluralRules_Two());
        addRules(new String[]{"ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa"}, new PluralRules_Zero());
        addRules(new String[]{"az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to", "tr", "vi", "wo", "yo", "zh", "bo", "dz", TtmlNode.ATTR_ID, "jv", "jw", "ka", "km", "kn", "ms", "th", "in"}, new PluralRules_None());
        LocaleInfo localeInfo = new LocaleInfo();
        localeInfo.name = "English";
        localeInfo.nameEnglish = "English";
        localeInfo.pluralLangCode = "en";
        localeInfo.shortName = "en";
        localeInfo.pathToFile = null;
        localeInfo.builtIn = true;
        this.languages.add(localeInfo);
        this.languagesDict.put(localeInfo.shortName, localeInfo);
        LocaleInfo localeInfo2 = new LocaleInfo();
        localeInfo2.name = "Français";
        localeInfo2.nameEnglish = "French";
        localeInfo2.pluralLangCode = "fr";
        localeInfo2.shortName = "fr";
        localeInfo2.builtIn = true;
        this.languages.add(localeInfo2);
        this.languagesDict.put(localeInfo2.shortName, localeInfo2);
        LocaleInfo localeInfo3 = new LocaleInfo();
        localeInfo3.name = "Português";
        localeInfo3.nameEnglish = "Portuguese";
        localeInfo3.pluralLangCode = "pt";
        localeInfo3.shortName = "pt";
        localeInfo3.builtIn = true;
        this.languages.add(localeInfo3);
        this.languagesDict.put(localeInfo3.shortName, localeInfo3);
        LocaleInfo localeInfo4 = new LocaleInfo();
        localeInfo4.name = "Ελληνικά";
        localeInfo4.nameEnglish = "Greek";
        localeInfo4.pluralLangCode = "el";
        localeInfo4.shortName = "el";
        localeInfo4.builtIn = true;
        this.languages.add(localeInfo4);
        this.languagesDict.put(localeInfo4.shortName, localeInfo4);
        LocaleInfo localeInfo5 = new LocaleInfo();
        localeInfo5.name = "Bahasa Indonesia";
        localeInfo5.nameEnglish = "Indonesian";
        localeInfo5.pluralLangCode = "in-rID";
        localeInfo5.shortName = "in-rID";
        localeInfo5.builtIn = true;
        this.languages.add(localeInfo5);
        this.languagesDict.put(localeInfo5.shortName, localeInfo5);
        LocaleInfo localeInfo6 = new LocaleInfo();
        localeInfo6.name = "Melayu";
        localeInfo6.nameEnglish = "Malay";
        localeInfo6.pluralLangCode = "ms";
        localeInfo6.shortName = "ms";
        localeInfo6.builtIn = true;
        this.languages.add(localeInfo6);
        this.languagesDict.put(localeInfo6.shortName, localeInfo6);
        LocaleInfo localeInfo7 = new LocaleInfo();
        localeInfo7.name = "Tiếng Việt";
        localeInfo7.nameEnglish = "Vietnamese";
        localeInfo7.pluralLangCode = "vi";
        localeInfo7.shortName = "vi";
        localeInfo7.pathToFile = null;
        localeInfo7.builtIn = true;
        this.languages.add(localeInfo7);
        this.languagesDict.put(localeInfo7.shortName, localeInfo7);
        LocaleInfo localeInfo8 = new LocaleInfo();
        localeInfo8.name = "日语";
        localeInfo8.nameEnglish = "Japanese";
        localeInfo8.pluralLangCode = "ja";
        localeInfo8.shortName = "ja";
        localeInfo8.pathToFile = null;
        localeInfo8.builtIn = true;
        this.languages.add(localeInfo8);
        this.languagesDict.put(localeInfo8.shortName, localeInfo8);
        LocaleInfo localeInfo9 = new LocaleInfo();
        localeInfo9.name = "简体中文";
        localeInfo9.nameEnglish = "Simplified Chinese";
        localeInfo9.shortName = "zh_cn";
        localeInfo9.pathToFile = null;
        this.languages.add(localeInfo9);
        this.languagesDict.put(localeInfo9.shortName, localeInfo9);
        LocaleInfo localeInfo10 = new LocaleInfo();
        localeInfo10.name = "繁體中文";
        localeInfo10.nameEnglish = "Traditional Chinese";
        localeInfo10.shortName = "zh_tw";
        localeInfo10.pathToFile = null;
        this.languages.add(localeInfo10);
        this.languagesDict.put(localeInfo10.shortName, localeInfo10);
        loadOtherLanguages();
        if (this.remoteLanguages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    LocaleController.this.lambda$new$0$LocaleController();
                }
            });
        }
        for (int a = 0; a < this.otherLanguages.size(); a++) {
            LocaleInfo locale = this.otherLanguages.get(a);
            this.languages.add(locale);
            this.languagesDict.put(locale.getKey(), locale);
        }
        for (int a2 = 0; a2 < this.remoteLanguages.size(); a2++) {
            LocaleInfo locale2 = this.remoteLanguages.get(a2);
            LocaleInfo existingLocale = getLanguageFromDict(locale2.getKey());
            if (existingLocale != null) {
                existingLocale.pathToFile = locale2.pathToFile;
                existingLocale.version = locale2.version;
                existingLocale.baseVersion = locale2.baseVersion;
                existingLocale.serverIndex = locale2.serverIndex;
                this.remoteLanguages.set(a2, existingLocale);
            } else {
                this.languages.add(locale2);
                this.languagesDict.put(locale2.getKey(), locale2);
            }
        }
        for (int a3 = 0; a3 < this.unofficialLanguages.size(); a3++) {
            LocaleInfo locale3 = this.unofficialLanguages.get(a3);
            LocaleInfo existingLocale2 = getLanguageFromDict(locale3.getKey());
            if (existingLocale2 != null) {
                existingLocale2.pathToFile = locale3.pathToFile;
                existingLocale2.version = locale3.version;
                existingLocale2.baseVersion = locale3.baseVersion;
                existingLocale2.serverIndex = locale3.serverIndex;
                this.unofficialLanguages.set(a3, existingLocale2);
            } else {
                this.languagesDict.put(locale3.getKey(), locale3);
            }
        }
        this.systemDefaultLocale = Locale.getDefault();
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        LocaleInfo currentInfo = null;
        boolean override = false;
        try {
            String lang = MessagesController.getGlobalMainSettings().getString("language", (String) null);
            if (!(lang == null || (currentInfo = getLanguageFromDict(lang)) == null)) {
                override = true;
            }
            if (currentInfo == null && this.systemDefaultLocale.getLanguage() != null) {
                currentInfo = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (currentInfo == null && (currentInfo = getLanguageFromDict(getLocaleString(this.systemDefaultLocale))) == null) {
                currentInfo = getLanguageFromDict("en");
            }
            applyLanguage(currentInfo, override, true, UserConfig.selectedAccount);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(), new IntentFilter("android.intent.action.TIMEZONE_CHANGED"));
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LocaleController.this.lambda$new$1$LocaleController();
            }
        });
    }

    public /* synthetic */ void lambda$new$0$LocaleController() {
        loadRemoteLanguages(UserConfig.selectedAccount);
    }

    public /* synthetic */ void lambda$new$1$LocaleController() {
        this.currentSystemLocale = getSystemLocaleStringIso639();
    }

    public LocaleInfo getLanguageFromDict(String key) {
        if (key == null) {
            return null;
        }
        return this.languagesDict.get(key.toLowerCase().replace("-", "_"));
    }

    private void addRules(String[] languages2, PluralRules rules) {
        for (String language : languages2) {
            this.allRules.put(language, rules);
        }
    }

    private String stringForQuantity(int quantity) {
        if (quantity == 1) {
            return "zero";
        }
        if (quantity == 2) {
            return "one";
        }
        if (quantity == 4) {
            return "two";
        }
        if (quantity == 8) {
            return "few";
        }
        if (quantity != 16) {
            return "other";
        }
        return "many";
    }

    public Locale getSystemDefaultLocale() {
        return this.systemDefaultLocale;
    }

    public boolean isCurrentLocalLocale() {
        return this.currentLocaleInfo.isLocal();
    }

    public String getCurrentLanguage() {
        return this.currentLocaleInfo.shortName;
    }

    public void reloadCurrentRemoteLocale(int currentAccount, String langCode) {
        if (langCode != null) {
            langCode = langCode.replace("-", "_");
        }
        if (langCode != null) {
            LocaleInfo localeInfo = this.currentLocaleInfo;
            if (localeInfo == null) {
                return;
            }
            if (!langCode.equals(localeInfo.shortName) && !langCode.equals(this.currentLocaleInfo.baseLangCode)) {
                return;
            }
        }
        applyRemoteLanguage(this.currentLocaleInfo, langCode, true, currentAccount);
    }

    public void checkUpdateForCurrentRemoteLocale(int currentAccount, int version, int baseVersion) {
        LocaleInfo localeInfo = this.currentLocaleInfo;
        if (localeInfo == null) {
            return;
        }
        if (localeInfo == null || localeInfo.isRemote() || this.currentLocaleInfo.isUnofficial()) {
            if (this.currentLocaleInfo.hasBaseLang() && this.currentLocaleInfo.baseVersion < baseVersion) {
                LocaleInfo localeInfo2 = this.currentLocaleInfo;
                applyRemoteLanguage(localeInfo2, localeInfo2.baseLangCode, false, currentAccount);
            }
            if (this.currentLocaleInfo.version < version) {
                LocaleInfo localeInfo3 = this.currentLocaleInfo;
                applyRemoteLanguage(localeInfo3, localeInfo3.shortName, false, currentAccount);
            }
        }
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('_');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public static String getSystemLocaleStringIso639() {
        Locale locale = getInstance().getSystemDefaultLocale();
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('-');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    public static String getLocaleStringIso639() {
        LocaleInfo info = getInstance().currentLocaleInfo;
        if (info != null) {
            return info.getLangCode();
        }
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            return "en";
        }
        String languageCode = locale.getLanguage();
        String countryCode = locale.getCountry();
        String variantCode = locale.getVariant();
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return "en";
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        if (countryCode.length() > 0 || variantCode.length() > 0) {
            result.append('-');
        }
        result.append(countryCode);
        if (variantCode.length() > 0) {
            result.append('_');
        }
        result.append(variantCode);
        return result.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:65:0x00c0 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c1 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c2 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c3 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c4 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00c5 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c6 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00c7 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00c8 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c9 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00ca A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00cb A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00cc A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getLocaleAlias(java.lang.String r16) {
        /*
            r0 = r16
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            int r3 = r16.hashCode()
            r4 = 3325(0xcfd, float:4.66E-42)
            java.lang.String r5 = "fil"
            java.lang.String r6 = "yi"
            java.lang.String r7 = "tl"
            java.lang.String r8 = "no"
            java.lang.String r9 = "nb"
            java.lang.String r10 = "jw"
            java.lang.String r11 = "jv"
            java.lang.String r12 = "ji"
            java.lang.String r13 = "iw"
            java.lang.String r14 = "in"
            java.lang.String r15 = "id"
            java.lang.String r2 = "he"
            if (r3 == r4) goto L_0x00b4
            r4 = 3355(0xd1b, float:4.701E-42)
            if (r3 == r4) goto L_0x00ac
            r4 = 3365(0xd25, float:4.715E-42)
            if (r3 == r4) goto L_0x00a4
            r4 = 3374(0xd2e, float:4.728E-42)
            if (r3 == r4) goto L_0x009c
            r4 = 3391(0xd3f, float:4.752E-42)
            if (r3 == r4) goto L_0x0094
            r4 = 3508(0xdb4, float:4.916E-42)
            if (r3 == r4) goto L_0x008b
            r4 = 3521(0xdc1, float:4.934E-42)
            if (r3 == r4) goto L_0x0083
            r4 = 3704(0xe78, float:5.19E-42)
            if (r3 == r4) goto L_0x007b
            r4 = 3856(0xf10, float:5.403E-42)
            if (r3 == r4) goto L_0x0072
            r4 = 101385(0x18c09, float:1.4207E-40)
            if (r3 == r4) goto L_0x0069
            r4 = 3404(0xd4c, float:4.77E-42)
            if (r3 == r4) goto L_0x0060
            r4 = 3405(0xd4d, float:4.771E-42)
            if (r3 == r4) goto L_0x0057
        L_0x0055:
            goto L_0x00bc
        L_0x0057:
            boolean r3 = r0.equals(r10)
            if (r3 == 0) goto L_0x0055
            r3 = 2
            goto L_0x00bd
        L_0x0060:
            boolean r3 = r0.equals(r11)
            if (r3 == 0) goto L_0x0055
            r3 = 8
            goto L_0x00bd
        L_0x0069:
            boolean r3 = r0.equals(r5)
            if (r3 == 0) goto L_0x0055
            r3 = 10
            goto L_0x00bd
        L_0x0072:
            boolean r3 = r0.equals(r6)
            if (r3 == 0) goto L_0x0055
            r3 = 11
            goto L_0x00bd
        L_0x007b:
            boolean r3 = r0.equals(r7)
            if (r3 == 0) goto L_0x0055
            r3 = 4
            goto L_0x00bd
        L_0x0083:
            boolean r3 = r0.equals(r8)
            if (r3 == 0) goto L_0x0055
            r3 = 3
            goto L_0x00bd
        L_0x008b:
            boolean r3 = r0.equals(r9)
            if (r3 == 0) goto L_0x0055
            r3 = 9
            goto L_0x00bd
        L_0x0094:
            boolean r3 = r0.equals(r12)
            if (r3 == 0) goto L_0x0055
            r3 = 5
            goto L_0x00bd
        L_0x009c:
            boolean r3 = r0.equals(r13)
            if (r3 == 0) goto L_0x0055
            r3 = 1
            goto L_0x00bd
        L_0x00a4:
            boolean r3 = r0.equals(r14)
            if (r3 == 0) goto L_0x0055
            r3 = 0
            goto L_0x00bd
        L_0x00ac:
            boolean r3 = r0.equals(r15)
            if (r3 == 0) goto L_0x0055
            r3 = 6
            goto L_0x00bd
        L_0x00b4:
            boolean r3 = r0.equals(r2)
            if (r3 == 0) goto L_0x0055
            r3 = 7
            goto L_0x00bd
        L_0x00bc:
            r3 = -1
        L_0x00bd:
            switch(r3) {
                case 0: goto L_0x00cc;
                case 1: goto L_0x00cb;
                case 2: goto L_0x00ca;
                case 3: goto L_0x00c9;
                case 4: goto L_0x00c8;
                case 5: goto L_0x00c7;
                case 6: goto L_0x00c6;
                case 7: goto L_0x00c5;
                case 8: goto L_0x00c4;
                case 9: goto L_0x00c3;
                case 10: goto L_0x00c2;
                case 11: goto L_0x00c1;
                default: goto L_0x00c0;
            }
        L_0x00c0:
            return r1
        L_0x00c1:
            return r12
        L_0x00c2:
            return r7
        L_0x00c3:
            return r8
        L_0x00c4:
            return r10
        L_0x00c5:
            return r13
        L_0x00c6:
            return r14
        L_0x00c7:
            return r6
        L_0x00c8:
            return r5
        L_0x00c9:
            return r9
        L_0x00ca:
            return r11
        L_0x00cb:
            return r2
        L_0x00cc:
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.LocaleController.getLocaleAlias(java.lang.String):java.lang.String");
    }

    public boolean applyLanguageFile(File file, int currentAccount) {
        LocaleInfo localeInfo;
        try {
            HashMap<String, String> stringMap = getLocaleFileStrings(file);
            String languageName = stringMap.get("LanguageName");
            String languageNameInEnglish = stringMap.get("LanguageNameInEnglish");
            String languageCode = stringMap.get("LanguageCode");
            if (languageName == null || languageName.length() <= 0 || languageNameInEnglish == null) {
                File file2 = file;
                return false;
            } else if (languageNameInEnglish.length() <= 0 || languageCode == null) {
                File file3 = file;
                return false;
            } else if (languageCode.length() > 0) {
                if (languageName.contains("&")) {
                    File file4 = file;
                } else if (languageName.contains(LogUtils.VERTICAL)) {
                    File file5 = file;
                } else {
                    if (languageNameInEnglish.contains("&")) {
                        File file6 = file;
                    } else if (languageNameInEnglish.contains(LogUtils.VERTICAL)) {
                        File file7 = file;
                    } else {
                        if (languageCode.contains("&") || languageCode.contains(LogUtils.VERTICAL) || languageCode.contains("/")) {
                            File file8 = file;
                        } else if (languageCode.contains("\\")) {
                            File file9 = file;
                        } else {
                            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                            File finalFile = new File(filesDirFixed, languageCode + ".xml");
                            try {
                                if (!AndroidUtilities.copyFile(file, finalFile)) {
                                    return false;
                                }
                                LocaleInfo localeInfo2 = getLanguageFromDict("local_" + languageCode.toLowerCase());
                                if (localeInfo2 == null) {
                                    LocaleInfo localeInfo3 = new LocaleInfo();
                                    localeInfo3.name = languageName;
                                    localeInfo3.nameEnglish = languageNameInEnglish;
                                    localeInfo3.shortName = languageCode.toLowerCase();
                                    localeInfo3.pluralLangCode = localeInfo3.shortName;
                                    localeInfo3.pathToFile = finalFile.getAbsolutePath();
                                    this.languages.add(localeInfo3);
                                    this.languagesDict.put(localeInfo3.getKey(), localeInfo3);
                                    this.otherLanguages.add(localeInfo3);
                                    saveOtherLanguages();
                                    localeInfo = localeInfo3;
                                } else {
                                    localeInfo = localeInfo2;
                                }
                                this.localeValues = stringMap;
                                applyLanguage(localeInfo, true, false, true, false, currentAccount);
                                return true;
                            } catch (Exception e) {
                                e = e;
                                FileLog.e((Throwable) e);
                                return false;
                            }
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            } else {
                File file10 = file;
                return false;
            }
        } catch (Exception e2) {
            e = e2;
            File file11 = file;
            FileLog.e((Throwable) e);
            return false;
        }
    }

    private void saveOtherLanguages() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < this.otherLanguages.size(); a++) {
            String loc = this.otherLanguages.get(a).getSaveString();
            if (loc != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc);
            }
        }
        editor.putString("locales", stringBuilder.toString());
        stringBuilder.setLength(0);
        for (int a2 = 0; a2 < this.remoteLanguages.size(); a2++) {
            String loc2 = this.remoteLanguages.get(a2).getSaveString();
            if (loc2 != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc2);
            }
        }
        editor.putString("remote", stringBuilder.toString());
        stringBuilder.setLength(0);
        for (int a3 = 0; a3 < this.unofficialLanguages.size(); a3++) {
            String loc3 = this.unofficialLanguages.get(a3).getSaveString();
            if (loc3 != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(loc3);
            }
        }
        editor.putString("unofficial", stringBuilder.toString());
        editor.commit();
    }

    public boolean deleteLanguage(LocaleInfo localeInfo, int currentAccount) {
        if (localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        if (this.currentLocaleInfo == localeInfo) {
            LocaleInfo info = null;
            if (this.systemDefaultLocale.getLanguage() != null) {
                info = getLanguageFromDict(this.systemDefaultLocale.getLanguage());
            }
            if (info == null) {
                info = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
            }
            if (info == null) {
                info = getLanguageFromDict("en");
            }
            applyLanguage(info, true, false, currentAccount);
        }
        this.unofficialLanguages.remove(localeInfo);
        this.remoteLanguages.remove(localeInfo);
        this.remoteLanguagesDict.remove(localeInfo.getKey());
        this.otherLanguages.remove(localeInfo);
        this.languages.remove(localeInfo);
        this.languagesDict.remove(localeInfo.getKey());
        new File(localeInfo.pathToFile).delete();
        saveOtherLanguages();
        return true;
    }

    private void loadOtherLanguages() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        String locales = preferences.getString("locales", (String) null);
        if (!TextUtils.isEmpty(locales)) {
            for (String locale : locales.split("&")) {
                LocaleInfo localeInfo = LocaleInfo.createWithString(locale);
                if (localeInfo != null) {
                    this.otherLanguages.add(localeInfo);
                }
            }
        }
        String locales2 = preferences.getString("remote", (String) null);
        if (!TextUtils.isEmpty(locales2)) {
            for (String locale2 : locales2.split("&")) {
                LocaleInfo localeInfo2 = LocaleInfo.createWithString(locale2);
                localeInfo2.shortName = localeInfo2.shortName.replace("-", "_");
                if (!this.remoteLanguagesDict.containsKey(localeInfo2.getKey()) && localeInfo2 != null) {
                    this.remoteLanguages.add(localeInfo2);
                    this.remoteLanguagesDict.put(localeInfo2.getKey(), localeInfo2);
                }
            }
        }
        String locales3 = preferences.getString("unofficial", (String) null);
        if (!TextUtils.isEmpty(locales3)) {
            for (String locale3 : locales3.split("&")) {
                LocaleInfo localeInfo3 = LocaleInfo.createWithString(locale3);
                localeInfo3.shortName = localeInfo3.shortName.replace("-", "_");
                if (localeInfo3 != null) {
                    this.unofficialLanguages.add(localeInfo3);
                }
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    private HashMap<String, String> getLocaleFileStrings(File file, boolean preserveEscapes) {
        FileInputStream stream = null;
        this.reloadLastFile = false;
        try {
            if (!file.exists()) {
                HashMap<String, String> hashMap = new HashMap<>();
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                return hashMap;
            }
            HashMap<String, String> stringMap = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            stream = new FileInputStream(file);
            parser.setInput(stream, "UTF-8");
            String name = null;
            String value = null;
            String attrName = null;
            for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                if (eventType == 2) {
                    name = parser.getName();
                    if (parser.getAttributeCount() > 0) {
                        attrName = parser.getAttributeValue(0);
                    }
                } else if (eventType == 4) {
                    if (!(attrName == null || (value = parser.getText()) == null)) {
                        String value2 = value.trim();
                        if (preserveEscapes) {
                            value = value2.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\\'").replace("& ", "&amp; ");
                        } else {
                            String old = value2.replace("\\n", "\n").replace("\\", "");
                            value = old.replace("&lt;", "<");
                            if (!this.reloadLastFile && !value.equals(old)) {
                                this.reloadLastFile = true;
                            }
                        }
                    }
                } else if (eventType == 3) {
                    value = null;
                    attrName = null;
                    name = null;
                }
                if (!(name == null || !name.equals("string") || value == null || attrName == null || value.length() == 0 || attrName.length() == 0)) {
                    stringMap.put(attrName, value);
                    name = null;
                    value = null;
                    attrName = null;
                }
            }
            try {
                stream.close();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            return stringMap;
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
            this.reloadLastFile = true;
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            return new HashMap<>();
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e5) {
                    FileLog.e((Throwable) e5);
                }
            }
            throw th;
        }
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean override, boolean init, int currentAccount) {
        applyLanguage(localeInfo, override, init, false, false, currentAccount);
    }

    public void applyLanguage(LocaleInfo localeInfo, boolean override, boolean init, boolean fromFile, boolean force, int currentAccount) {
        String[] args;
        Locale newLocale;
        LocaleInfo localeInfo2 = localeInfo;
        int i = currentAccount;
        if (localeInfo2 != null) {
            boolean hasBase = localeInfo.hasBaseLang();
            File pathToFile = localeInfo.getPathToFile();
            File pathToBaseFile = localeInfo.getPathToBaseFile();
            String str = localeInfo2.shortName;
            if (!init) {
                ConnectionsManager.setLangCode(localeInfo.getLangCode());
            }
            if (getLanguageFromDict(localeInfo.getKey()) == null) {
                if (localeInfo.isRemote()) {
                    this.remoteLanguages.add(localeInfo2);
                    this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo2);
                    this.languages.add(localeInfo2);
                    this.languagesDict.put(localeInfo.getKey(), localeInfo2);
                    saveOtherLanguages();
                } else if (localeInfo.isUnofficial()) {
                    this.unofficialLanguages.add(localeInfo2);
                    this.languagesDict.put(localeInfo.getKey(), localeInfo2);
                    saveOtherLanguages();
                }
            }
            if ((localeInfo.isRemote() || localeInfo.isUnofficial()) && (force || !pathToFile.exists() || (hasBase && !pathToBaseFile.exists()))) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("reload locale because one of file doesn't exist" + pathToFile + " " + pathToBaseFile);
                }
                if (init) {
                    AndroidUtilities.runOnUIThread(new Runnable(localeInfo2, i) {
                        private final /* synthetic */ LocaleController.LocaleInfo f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            LocaleController.this.lambda$applyLanguage$2$LocaleController(this.f$1, this.f$2);
                        }
                    });
                } else {
                    applyRemoteLanguage(localeInfo2, (String) null, true, i);
                }
            }
            try {
                if (!TextUtils.isEmpty(localeInfo2.pluralLangCode)) {
                    args = localeInfo2.pluralLangCode.split("_");
                } else if (!TextUtils.isEmpty(localeInfo2.baseLangCode)) {
                    args = localeInfo2.baseLangCode.split("_");
                } else {
                    args = localeInfo2.shortName.split("_");
                }
                if (args.length == 1) {
                    newLocale = new Locale(args[0]);
                } else {
                    newLocale = new Locale(args[0], args[1]);
                }
                if (override) {
                    this.languageOverride = localeInfo2.shortName;
                    SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                    editor.putString("language", localeInfo.getKey());
                    editor.commit();
                }
                if (pathToFile == null) {
                    this.localeValues.clear();
                } else if (!fromFile) {
                    HashMap<String, String> localeFileStrings = getLocaleFileStrings(hasBase ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                    this.localeValues = localeFileStrings;
                    if (hasBase) {
                        localeFileStrings.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                    }
                }
                this.currentLocale = newLocale;
                this.currentLocaleInfo = localeInfo2;
                if (localeInfo2 != null && !TextUtils.isEmpty(localeInfo2.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    PluralRules pluralRules = this.allRules.get(args[0]);
                    this.currentPluralRules = pluralRules;
                    if (pluralRules == null) {
                        PluralRules pluralRules2 = this.allRules.get(this.currentLocale.getLanguage());
                        this.currentPluralRules = pluralRules2;
                        if (pluralRules2 == null) {
                            this.currentPluralRules = new PluralRules_None();
                        }
                    }
                }
                this.changingConfiguration = true;
                Locale.setDefault(this.currentLocale);
                Configuration config = new Configuration();
                config.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(config, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
                if (this.reloadLastFile) {
                    if (init) {
                        AndroidUtilities.runOnUIThread(new Runnable(i) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                LocaleController.this.lambda$applyLanguage$3$LocaleController(this.f$1);
                            }
                        });
                    } else {
                        reloadCurrentRemoteLocale(i, (String) null);
                    }
                    this.reloadLastFile = false;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                this.changingConfiguration = false;
            }
            recreateFormatters();
        }
    }

    public /* synthetic */ void lambda$applyLanguage$2$LocaleController(LocaleInfo localeInfo, int currentAccount) {
        applyRemoteLanguage(localeInfo, (String) null, true, currentAccount);
    }

    public /* synthetic */ void lambda$applyLanguage$3$LocaleController(int currentAccount) {
        reloadCurrentRemoteLocale(currentAccount, (String) null);
    }

    public LocaleInfo getCurrentLocaleInfo() {
        return this.currentLocaleInfo;
    }

    public static String getCurrentLanguageName() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        return (localeInfo == null || TextUtils.isEmpty(localeInfo.name)) ? getString("LanguageName", R.string.LanguageName) : localeInfo.name;
    }

    private String getStringInternal(String key, int res) {
        String value = BuildVars.USE_CLOUD_STRINGS ? this.localeValues.get(key) : null;
        if (value == null) {
            try {
                value = ApplicationLoader.applicationContext.getString(res);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (value != null) {
            return value;
        }
        return "LOC_ERR:" + key;
    }

    public static String getServerString(String key) {
        int resourceId;
        String value = getInstance().localeValues.get(key);
        if (value != null || (resourceId = ApplicationLoader.applicationContext.getResources().getIdentifier(key, "string", ApplicationLoader.applicationContext.getPackageName())) == 0) {
            return value;
        }
        return ApplicationLoader.applicationContext.getString(resourceId);
    }

    public static String getString(int res) {
        return getString(res + "", res);
    }

    public static String getString(String key, int res) {
        return getInstance().getStringInternal(key, res);
    }

    public static String getPluralString(String key, int plural) {
        if (key == null || key.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + key;
        }
        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
        return getString(param, ApplicationLoader.applicationContext.getResources().getIdentifier(param, "string", ApplicationLoader.applicationContext.getPackageName()));
    }

    public static String formatPluralString(String key, int plural) {
        if (key == null || key.length() == 0 || getInstance().currentPluralRules == null) {
            return "LOC_ERR:" + key;
        }
        String param = key + "_" + getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(plural));
        return formatString(param, ApplicationLoader.applicationContext.getResources().getIdentifier(param, "string", ApplicationLoader.applicationContext.getPackageName()), Integer.valueOf(plural));
    }

    public static String formatString(String key, int res, Object... args) {
        try {
            String value = BuildVars.USE_CLOUD_STRINGS ? getInstance().localeValues.get(key) : null;
            if (value == null) {
                value = ApplicationLoader.applicationContext.getString(res);
            }
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, value, args);
            }
            return String.format(value, args);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: " + key;
        }
    }

    public static String formatTTLString(int ttl) {
        if (ttl < 60) {
            return formatPluralString("Seconds", ttl);
        }
        if (ttl < 3600) {
            return formatPluralString("Minutes", ttl / 60);
        }
        if (ttl < 86400) {
            return formatPluralString("Hours", (ttl / 60) / 60);
        }
        if (ttl < 604800) {
            return formatPluralString("Days", ((ttl / 60) / 60) / 24);
        }
        int days = ((ttl / 60) / 60) / 24;
        if (ttl % 7 == 0) {
            return formatPluralString("Weeks", days / 7);
        }
        return String.format("%s %s", new Object[]{formatPluralString("Weeks", days / 7), formatPluralString("Days", days % 7)});
    }

    public String formatCurrencyString(long amount, String type) {
        double doubleAmount;
        String customFormat;
        String type2 = type.toUpperCase();
        boolean discount = amount < 0;
        long amount2 = Math.abs(amount);
        Currency currency = Currency.getInstance(type2);
        char c = 65535;
        switch (type2.hashCode()) {
            case 65726:
                if (type2.equals("BHD")) {
                    c = 2;
                    break;
                }
                break;
            case 65759:
                if (type2.equals("BIF")) {
                    c = 9;
                    break;
                }
                break;
            case 66267:
                if (type2.equals("BYR")) {
                    c = 10;
                    break;
                }
                break;
            case 66813:
                if (type2.equals("CLF")) {
                    c = 0;
                    break;
                }
                break;
            case 66823:
                if (type2.equals("CLP")) {
                    c = 11;
                    break;
                }
                break;
            case 67122:
                if (type2.equals("CVE")) {
                    c = 12;
                    break;
                }
                break;
            case 67712:
                if (type2.equals("DJF")) {
                    c = 13;
                    break;
                }
                break;
            case 70719:
                if (type2.equals("GNF")) {
                    c = 14;
                    break;
                }
                break;
            case 72732:
                if (type2.equals("IQD")) {
                    c = 3;
                    break;
                }
                break;
            case 72777:
                if (type2.equals("IRR")) {
                    c = 1;
                    break;
                }
                break;
            case 72801:
                if (type2.equals("ISK")) {
                    c = 15;
                    break;
                }
                break;
            case 73631:
                if (type2.equals("JOD")) {
                    c = 4;
                    break;
                }
                break;
            case 73683:
                if (type2.equals("JPY")) {
                    c = 16;
                    break;
                }
                break;
            case 74532:
                if (type2.equals("KMF")) {
                    c = 17;
                    break;
                }
                break;
            case 74704:
                if (type2.equals("KRW")) {
                    c = 18;
                    break;
                }
                break;
            case 74840:
                if (type2.equals("KWD")) {
                    c = 5;
                    break;
                }
                break;
            case 75863:
                if (type2.equals("LYD")) {
                    c = 6;
                    break;
                }
                break;
            case 76263:
                if (type2.equals("MGA")) {
                    c = 19;
                    break;
                }
                break;
            case 76618:
                if (type2.equals("MRO")) {
                    c = 29;
                    break;
                }
                break;
            case 78388:
                if (type2.equals("OMR")) {
                    c = 7;
                    break;
                }
                break;
            case 79710:
                if (type2.equals("PYG")) {
                    c = 20;
                    break;
                }
                break;
            case 81569:
                if (type2.equals("RWF")) {
                    c = 21;
                    break;
                }
                break;
            case 83210:
                if (type2.equals("TND")) {
                    c = 8;
                    break;
                }
                break;
            case 83974:
                if (type2.equals("UGX")) {
                    c = 22;
                    break;
                }
                break;
            case 84517:
                if (type2.equals("UYI")) {
                    c = 23;
                    break;
                }
                break;
            case 85132:
                if (type2.equals("VND")) {
                    c = 24;
                    break;
                }
                break;
            case 85367:
                if (type2.equals("VUV")) {
                    c = 25;
                    break;
                }
                break;
            case 86653:
                if (type2.equals("XAF")) {
                    c = 26;
                    break;
                }
                break;
            case 87087:
                if (type2.equals("XOF")) {
                    c = 27;
                    break;
                }
                break;
            case 87118:
                if (type2.equals("XPF")) {
                    c = 28;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                customFormat = " %.4f";
                doubleAmount = ((double) amount2) / 10000.0d;
                break;
            case 1:
                doubleAmount = (double) (((float) amount2) / 100.0f);
                if (amount2 % 100 != 0) {
                    customFormat = " %.2f";
                    break;
                } else {
                    customFormat = " %.0f";
                    break;
                }
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                customFormat = " %.3f";
                doubleAmount = ((double) amount2) / 1000.0d;
                break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
                customFormat = " %.0f";
                doubleAmount = (double) amount2;
                break;
            case 29:
                customFormat = " %.1f";
                doubleAmount = ((double) amount2) / 10.0d;
                break;
            default:
                customFormat = " %.2f";
                doubleAmount = ((double) amount2) / 100.0d;
                break;
        }
        String str = "-";
        if (currency != null) {
            Locale locale = this.currentLocale;
            if (locale == null) {
                locale = this.systemDefaultLocale;
            }
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            format.setCurrency(currency);
            if (type2.equals("IRR")) {
                format.setMaximumFractionDigits(0);
            }
            StringBuilder sb = new StringBuilder();
            if (!discount) {
                str = "";
            }
            sb.append(str);
            sb.append(format.format(doubleAmount));
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        if (!discount) {
            str = "";
        }
        sb2.append(str);
        Locale locale2 = Locale.US;
        sb2.append(String.format(locale2, type2 + customFormat, new Object[]{Double.valueOf(doubleAmount)}));
        return sb2.toString();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String formatCurrencyDecimalString(long r10, java.lang.String r12, boolean r13) {
        /*
            r9 = this;
            java.lang.String r12 = r12.toUpperCase()
            long r10 = java.lang.Math.abs(r10)
            int r0 = r12.hashCode()
            r1 = 1
            r2 = 0
            switch(r0) {
                case 65726: goto L_0x015f;
                case 65759: goto L_0x0154;
                case 66267: goto L_0x0149;
                case 66813: goto L_0x013f;
                case 66823: goto L_0x0134;
                case 67122: goto L_0x0129;
                case 67712: goto L_0x011e;
                case 70719: goto L_0x0113;
                case 72732: goto L_0x0109;
                case 72777: goto L_0x00ff;
                case 72801: goto L_0x00f3;
                case 73631: goto L_0x00e8;
                case 73683: goto L_0x00dc;
                case 74532: goto L_0x00d0;
                case 74704: goto L_0x00c4;
                case 74840: goto L_0x00b9;
                case 75863: goto L_0x00ae;
                case 76263: goto L_0x00a2;
                case 76618: goto L_0x0096;
                case 78388: goto L_0x008b;
                case 79710: goto L_0x007f;
                case 81569: goto L_0x0073;
                case 83210: goto L_0x0067;
                case 83974: goto L_0x005b;
                case 84517: goto L_0x004f;
                case 85132: goto L_0x0043;
                case 85367: goto L_0x0037;
                case 86653: goto L_0x002b;
                case 87087: goto L_0x001f;
                case 87118: goto L_0x0013;
                default: goto L_0x0011;
            }
        L_0x0011:
            goto L_0x0169
        L_0x0013:
            java.lang.String r0 = "XPF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 28
            goto L_0x016a
        L_0x001f:
            java.lang.String r0 = "XOF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 27
            goto L_0x016a
        L_0x002b:
            java.lang.String r0 = "XAF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 26
            goto L_0x016a
        L_0x0037:
            java.lang.String r0 = "VUV"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 25
            goto L_0x016a
        L_0x0043:
            java.lang.String r0 = "VND"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 24
            goto L_0x016a
        L_0x004f:
            java.lang.String r0 = "UYI"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 23
            goto L_0x016a
        L_0x005b:
            java.lang.String r0 = "UGX"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 22
            goto L_0x016a
        L_0x0067:
            java.lang.String r0 = "TND"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 8
            goto L_0x016a
        L_0x0073:
            java.lang.String r0 = "RWF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 21
            goto L_0x016a
        L_0x007f:
            java.lang.String r0 = "PYG"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 20
            goto L_0x016a
        L_0x008b:
            java.lang.String r0 = "OMR"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 7
            goto L_0x016a
        L_0x0096:
            java.lang.String r0 = "MRO"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 29
            goto L_0x016a
        L_0x00a2:
            java.lang.String r0 = "MGA"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 19
            goto L_0x016a
        L_0x00ae:
            java.lang.String r0 = "LYD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 6
            goto L_0x016a
        L_0x00b9:
            java.lang.String r0 = "KWD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 5
            goto L_0x016a
        L_0x00c4:
            java.lang.String r0 = "KRW"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 18
            goto L_0x016a
        L_0x00d0:
            java.lang.String r0 = "KMF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 17
            goto L_0x016a
        L_0x00dc:
            java.lang.String r0 = "JPY"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 16
            goto L_0x016a
        L_0x00e8:
            java.lang.String r0 = "JOD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 4
            goto L_0x016a
        L_0x00f3:
            java.lang.String r0 = "ISK"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 15
            goto L_0x016a
        L_0x00ff:
            java.lang.String r0 = "IRR"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 1
            goto L_0x016a
        L_0x0109:
            java.lang.String r0 = "IQD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 3
            goto L_0x016a
        L_0x0113:
            java.lang.String r0 = "GNF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 14
            goto L_0x016a
        L_0x011e:
            java.lang.String r0 = "DJF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 13
            goto L_0x016a
        L_0x0129:
            java.lang.String r0 = "CVE"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 12
            goto L_0x016a
        L_0x0134:
            java.lang.String r0 = "CLP"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 11
            goto L_0x016a
        L_0x013f:
            java.lang.String r0 = "CLF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 0
            goto L_0x016a
        L_0x0149:
            java.lang.String r0 = "BYR"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 10
            goto L_0x016a
        L_0x0154:
            java.lang.String r0 = "BIF"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 9
            goto L_0x016a
        L_0x015f:
            java.lang.String r0 = "BHD"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x0011
            r0 = 2
            goto L_0x016a
        L_0x0169:
            r0 = -1
        L_0x016a:
            switch(r0) {
                case 0: goto L_0x019e;
                case 1: goto L_0x0189;
                case 2: goto L_0x017f;
                case 3: goto L_0x017f;
                case 4: goto L_0x017f;
                case 5: goto L_0x017f;
                case 6: goto L_0x017f;
                case 7: goto L_0x017f;
                case 8: goto L_0x017f;
                case 9: goto L_0x017b;
                case 10: goto L_0x017b;
                case 11: goto L_0x017b;
                case 12: goto L_0x017b;
                case 13: goto L_0x017b;
                case 14: goto L_0x017b;
                case 15: goto L_0x017b;
                case 16: goto L_0x017b;
                case 17: goto L_0x017b;
                case 18: goto L_0x017b;
                case 19: goto L_0x017b;
                case 20: goto L_0x017b;
                case 21: goto L_0x017b;
                case 22: goto L_0x017b;
                case 23: goto L_0x017b;
                case 24: goto L_0x017b;
                case 25: goto L_0x017b;
                case 26: goto L_0x017b;
                case 27: goto L_0x017b;
                case 28: goto L_0x017b;
                case 29: goto L_0x0174;
                default: goto L_0x016d;
            }
        L_0x016d:
            java.lang.String r0 = " %.2f"
            double r3 = (double) r10
            r5 = 4636737291354636288(0x4059000000000000, double:100.0)
            double r3 = r3 / r5
            goto L_0x01a8
        L_0x0174:
            java.lang.String r0 = " %.1f"
            double r3 = (double) r10
            r5 = 4621819117588971520(0x4024000000000000, double:10.0)
            double r3 = r3 / r5
            goto L_0x01a8
        L_0x017b:
            java.lang.String r0 = " %.0f"
            double r3 = (double) r10
            goto L_0x01a8
        L_0x017f:
            java.lang.String r0 = " %.3f"
            double r3 = (double) r10
            r5 = 4652007308841189376(0x408f400000000000, double:1000.0)
            double r3 = r3 / r5
            goto L_0x01a8
        L_0x0189:
            float r0 = (float) r10
            r3 = 1120403456(0x42c80000, float:100.0)
            float r0 = r0 / r3
            double r3 = (double) r0
            r5 = 100
            long r5 = r10 % r5
            r7 = 0
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x019b
            java.lang.String r0 = " %.0f"
            goto L_0x01a8
        L_0x019b:
            java.lang.String r0 = " %.2f"
            goto L_0x01a8
        L_0x019e:
            java.lang.String r0 = " %.4f"
            double r3 = (double) r10
            r5 = 4666723172467343360(0x40c3880000000000, double:10000.0)
            double r3 = r3 / r5
        L_0x01a8:
            java.util.Locale r5 = java.util.Locale.US
            if (r13 == 0) goto L_0x01ae
            r6 = r12
            goto L_0x01bf
        L_0x01ae:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = ""
            r6.append(r7)
            r6.append(r0)
            java.lang.String r6 = r6.toString()
        L_0x01bf:
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.Double r7 = java.lang.Double.valueOf(r3)
            r1[r2] = r7
            java.lang.String r1 = java.lang.String.format(r5, r6, r1)
            java.lang.String r1 = r1.trim()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.LocaleController.formatCurrencyDecimalString(long, java.lang.String, boolean):java.lang.String");
    }

    public static String formatStringSimple(String string, Object... args) {
        try {
            if (getInstance().currentLocale != null) {
                return String.format(getInstance().currentLocale, string, args);
            }
            return String.format(string, args);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: " + string;
        }
    }

    public static String formatCallDuration(int duration) {
        if (duration > 3600) {
            String result = formatPluralString("Hours", duration / 3600);
            int minutes = (duration % 3600) / 60;
            if (minutes <= 0) {
                return result;
            }
            return result + ", " + formatPluralString("Minutes", minutes);
        } else if (duration > 60) {
            return formatPluralString("Minutes", duration / 60);
        } else {
            return formatPluralString("Seconds", duration);
        }
    }

    public void onDeviceConfigurationChange(Configuration newConfig) {
        if (!this.changingConfiguration) {
            is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
            this.systemDefaultLocale = newConfig.locale;
            if (this.languageOverride != null) {
                LocaleInfo toSet = this.currentLocaleInfo;
                this.currentLocaleInfo = null;
                applyLanguage(toSet, false, false, UserConfig.selectedAccount);
            } else {
                Locale newLocale = newConfig.locale;
                if (newLocale != null) {
                    String d1 = newLocale.getDisplayName();
                    String d2 = this.currentLocale.getDisplayName();
                    if (!(d1 == null || d2 == null || d1.equals(d2))) {
                        recreateFormatters();
                    }
                    this.currentLocale = newLocale;
                    LocaleInfo localeInfo = this.currentLocaleInfo;
                    if (localeInfo != null && !TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                        this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                    }
                    if (this.currentPluralRules == null) {
                        PluralRules pluralRules = this.allRules.get(this.currentLocale.getLanguage());
                        this.currentPluralRules = pluralRules;
                        if (pluralRules == null) {
                            this.currentPluralRules = this.allRules.get("en");
                        }
                    }
                }
            }
            String newSystemLocale = getSystemLocaleStringIso639();
            String str = this.currentSystemLocale;
            if (str != null && !newSystemLocale.equals(str)) {
                this.currentSystemLocale = newSystemLocale;
                ConnectionsManager.setSystemLangCode(newSystemLocale);
            }
        }
    }

    public static String formatDateChat(long date) {
        return formatDateChat(date, false);
    }

    public static String formatDateChat(long date, boolean checkYear) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentYear = calendar.get(1);
            long date2 = date * 1000;
            calendar.setTimeInMillis(date2);
            if ((!checkYear || currentYear != calendar.get(1)) && (checkYear || Math.abs(System.currentTimeMillis() - date2) >= 31536000000L)) {
                return getInstance().chatFullDate.format(date2);
            }
            return getInstance().chatDate.format(date2);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: formatDateChat";
        }
    }

    public static String formatDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return getInstance().formatterDay.format(new Date(date2));
            }
            if (dateDay + 1 == day && year == dateYear) {
                return getString("Yesterday", R.string.Yesterday);
            }
            if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return getInstance().formatterDayMonth.format(new Date(date2));
            }
            return getInstance().formatterYear.format(new Date(date2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR: formatDate";
        }
    }

    public static String formatDateAudio(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(date2)));
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2)));
            } else if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            } else {
                return formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatDateCallLog(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return getInstance().formatterDay.format(new Date(date2));
            }
            if (dateDay + 1 == day && year == dateYear) {
                return formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2)));
            } else if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().chatDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            } else {
                return formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().chatFullDate.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2)));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationUpdateDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                int diff = ((int) (((long) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) - (date2 / 1000))) / 60;
                if (diff < 1) {
                    return getString("LocationUpdatedJustNow", R.string.LocationUpdatedJustNow);
                }
                if (diff < 60) {
                    return formatPluralString("UpdatedMinutes", diff);
                }
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(date2))));
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2))));
            } else if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))));
            } else {
                return formatString("LocationUpdatedFormatted", R.string.LocationUpdatedFormatted, formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatLocationLeftTime(int time) {
        int hours = (time / 60) / 60;
        int time2 = time - ((hours * 60) * 60);
        int minutes = time2 / 60;
        int time3 = time2 - (minutes * 60);
        int i = 1;
        if (hours != 0) {
            Object[] objArr = new Object[1];
            if (minutes <= 30) {
                i = 0;
            }
            objArr[0] = Integer.valueOf(i + hours);
            return String.format("%dh", objArr);
        } else if (minutes != 0) {
            Object[] objArr2 = new Object[1];
            if (time3 <= 30) {
                i = 0;
            }
            objArr2[0] = Integer.valueOf(i + minutes);
            return String.format("%d", objArr2);
        } else {
            return String.format("%d", new Object[]{Integer.valueOf(time3)});
        }
    }

    public static String formatDateOnline(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                return formatString("LastSeenFormatted", R.string.LastSeenFormatted, formatString("TodayAtFormatted", R.string.TodayAtFormatted, getInstance().formatterDay.format(new Date(date2))));
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("LastSeenFormatted", R.string.LastSeenFormatted, formatString("YesterdayAtFormatted", R.string.YesterdayAtFormatted, getInstance().formatterDay.format(new Date(date2))));
            } else if (Math.abs(System.currentTimeMillis() - date2) < 31536000000L) {
                return formatString("LastSeenDateFormatted", R.string.LastSeenDateFormatted, formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterDayMonth.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))));
            } else {
                return formatString("LastSeenDateFormatted", R.string.LastSeenDateFormatted, formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date2)), getInstance().formatterDay.format(new Date(date2))));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatDateOnlineNew(long date) {
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date * 1000);
            int dateDay = rightNow.get(6);
            int dateYear = rightNow.get(1);
            if (dateDay == day && year == dateYear) {
                int diff = ((int) (((long) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) - date)) / 60;
                if (diff < 1) {
                    return getString("New_Online", R.string.New_Online);
                }
                if (diff < 60) {
                    return formatString("LastSeenMins", R.string.LastSeenMins, Integer.valueOf(diff));
                }
                return formatString("LastSeenHours", R.string.LastSeenHours, Integer.valueOf((int) Math.ceil((double) (((float) diff) / 60.0f))));
            } else if (dateDay + 1 == day && year == dateYear) {
                return formatString("LastSeenDays", R.string.LastSeenDays, 1);
            } else {
                long currentTimeMillis = System.currentTimeMillis();
                Long.signum(date);
                if (Math.abs(currentTimeMillis - (date * 1000)) < 31536000000L) {
                    long dis = Math.abs(System.currentTimeMillis() - (1000 * date));
                    if (dis < 2592000000L) {
                        return formatString("LastSeenDays", R.string.LastSeenDays, Integer.valueOf((int) Math.ceil((double) (((float) dis) / 8.64E7f))));
                    }
                    return formatString("LastSeenMonths", R.string.LastSeenMonths, Integer.valueOf((int) Math.ceil((double) (((float) dis) / 2.592E9f))));
                }
                return formatString("LastSeenDateFormatted", R.string.LastSeenDateFormatted, formatString("formatDateAtTime", R.string.formatDateAtTime, getInstance().formatterYear.format(new Date(date * 1000)), getInstance().formatterDay.format(new Date(1000 * date))));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    private FastDateFormat createFormatter(Locale locale, String format, String defaultFormat) {
        if (format == null || format.length() == 0) {
            format = defaultFormat;
        }
        try {
            return FastDateFormat.getInstance(format, locale);
        } catch (Exception e) {
            return FastDateFormat.getInstance(defaultFormat, locale);
        }
    }

    public void recreateFormatters() {
        String str;
        int i;
        String str2;
        int i2;
        String str3;
        int i3;
        Locale locale = this.currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String lang = locale.getLanguage();
        if (lang == null) {
            lang = "en";
        }
        String lang2 = lang.toLowerCase();
        nameDisplayOrder = lang2.equals("ko") ? 2 : 1;
        this.formatterDayMonth = createFormatter(locale, getStringInternal("formatterMonth", R.string.formatterMonth), "dd MMM");
        this.formatterYear = createFormatter(locale, getStringInternal("formatterYear", R.string.formatterYear), "dd.MM.yy");
        this.formatterYearMax = createFormatter(locale, getStringInternal("formatterYearMax", R.string.formatterYearMax), "dd.MM.yyyy");
        this.chatDate = createFormatter(locale, getStringInternal("chatDate", R.string.chatDate), "d MMMM");
        this.chatFullDate = createFormatter(locale, getStringInternal("chatFullDate", R.string.chatFullDate), "d MMMM yyyy");
        this.formatterWeek = createFormatter(locale, getStringInternal("formatterWeek", R.string.formatterWeek), "EEE");
        this.formatterScheduleDay = createFormatter(locale, getStringInternal("formatDateSchedule", R.string.formatDateSchedule), "MMM d");
        this.formatterScheduleYear = createFormatter(locale, getStringInternal("formatDateScheduleYear", R.string.formatDateScheduleYear), "MMM d yyyy");
        String str4 = "HH:mm";
        this.formatterDay = createFormatter((lang2.toLowerCase().equals("ar") || lang2.toLowerCase().equals("ko")) ? locale : Locale.US, is24HourFormat ? getStringInternal("formatterDay24H", R.string.formatterDay24H) : getStringInternal("formatterDay12H", R.string.formatterDay12H), is24HourFormat ? str4 : "h:mm a");
        Locale locale2 = (lang2.toLowerCase().equals("ar") || lang2.toLowerCase().equals("ko")) ? locale : Locale.US;
        String stringInternal = is24HourFormat ? getStringInternal("formatterDay24H", R.string.formatterDay24H) : getStringInternal("formatterDay12H", R.string.formatterDay12Honly);
        if (!is24HourFormat) {
            str4 = "h:mm";
        }
        this.formatterDayNoly = createFormatter(locale2, stringInternal, str4);
        if (is24HourFormat) {
            i = R.string.formatterStats24H;
            str = "formatterStats24H";
        } else {
            i = R.string.formatterStats12H;
            str = "formatterStats12H";
        }
        String str5 = "MMM dd yyyy, HH:mm";
        this.formatterStats = createFormatter(locale, getStringInternal(str, i), is24HourFormat ? str5 : "MMM dd yyyy, h:mm a");
        if (is24HourFormat) {
            i2 = R.string.formatterBannedUntil24H;
            str2 = "formatterBannedUntil24H";
        } else {
            i2 = R.string.formatterBannedUntil12H;
            str2 = "formatterBannedUntil12H";
        }
        String stringInternal2 = getStringInternal(str2, i2);
        if (!is24HourFormat) {
            str5 = "MMM dd yyyy, h:mm a";
        }
        this.formatterBannedUntil = createFormatter(locale, stringInternal2, str5);
        if (is24HourFormat) {
            i3 = R.string.formatterBannedUntilThisYear24H;
            str3 = "formatterBannedUntilThisYear24H";
        } else {
            i3 = R.string.formatterBannedUntilThisYear12H;
            str3 = "formatterBannedUntilThisYear12H";
        }
        this.formatterBannedUntilThisYear = createFormatter(locale, getStringInternal(str3, i3), is24HourFormat ? "MMM dd, HH:mm" : "MMM dd, h:mm a");
        this.formatterScheduleSend[0] = createFormatter(locale, getStringInternal("SendTodayAt", R.string.SendTodayAt), "'Send today at' HH:mm");
        this.formatterScheduleSend[1] = createFormatter(locale, getStringInternal("SendDayAt", R.string.SendDayAt), "'Send on' MMM d 'at' HH:mm");
        this.formatterScheduleSend[2] = createFormatter(locale, getStringInternal("SendDayYearAt", R.string.SendDayYearAt), "'Send on' MMM d yyyy 'at' HH:mm");
        this.formatterScheduleSend[3] = createFormatter(locale, getStringInternal("RemindTodayAt", R.string.RemindTodayAt), "'Remind today at' HH:mm");
        this.formatterScheduleSend[4] = createFormatter(locale, getStringInternal("RemindDayAt", R.string.RemindDayAt), "'Remind on' MMM d 'at' HH:mm");
        this.formatterScheduleSend[5] = createFormatter(locale, getStringInternal("RemindDayYearAt", R.string.RemindDayYearAt), "'Remind on' MMM d yyyy 'at' HH:mm");
    }

    public static boolean isRTLCharacter(char ch) {
        return Character.getDirectionality(ch) == 1 || Character.getDirectionality(ch) == 2 || Character.getDirectionality(ch) == 16 || Character.getDirectionality(ch) == 17;
    }

    public static String formatSectionDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            int dateYear = rightNow.get(1);
            int month = rightNow.get(2);
            String[] months = {getString("January", R.string.January), getString("February", R.string.February), getString("March", R.string.March), getString("April", R.string.April), getString("May", R.string.May), getString("June", R.string.June), getString("July", R.string.July), getString("August", R.string.August), getString("September", R.string.September), getString("October", R.string.October), getString("November", R.string.November), getString("December", R.string.December)};
            if (year == dateYear) {
                return months[month];
            }
            return months[month] + " " + dateYear;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatDateForBan(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(1);
            rightNow.setTimeInMillis(date2);
            if (year == rightNow.get(1)) {
                return getInstance().formatterBannedUntilThisYear.format(new Date(date2));
            }
            return getInstance().formatterBannedUntil.format(new Date(date2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String stringForMessageListDate(long date) {
        long date2 = date * 1000;
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(6);
            rightNow.setTimeInMillis(date2);
            int dateDay = rightNow.get(6);
            if (Math.abs(System.currentTimeMillis() - date2) >= 31536000000L) {
                return getInstance().formatterYear.format(new Date(date2));
            }
            int dayDiff = dateDay - day;
            if (dayDiff != 0) {
                if (dayDiff != -1 || System.currentTimeMillis() - date2 >= 28800000) {
                    if (dayDiff <= -7 || dayDiff > -1) {
                        return getInstance().formatterDayMonth.format(new Date(date2));
                    }
                    return getInstance().formatterWeek.format(new Date(date2));
                }
            }
            return getInstance().formatterDay.format(new Date(date2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "LOC_ERR";
        }
    }

    public static String formatShortNumber(int number, int[] rounded) {
        StringBuilder K = new StringBuilder();
        int lastDec = 0;
        while (number / 1000 > 0) {
            K.append("K");
            lastDec = (number % 1000) / 100;
            number /= 1000;
        }
        if (rounded != null) {
            double value = ((double) number) + (((double) lastDec) / 10.0d);
            for (int a = 0; a < K.length(); a++) {
                value *= 1000.0d;
            }
            rounded[0] = (int) value;
        }
        if (lastDec == 0 || K.length() <= 0) {
            if (K.length() == 2) {
                return String.format(Locale.US, "%dM", new Object[]{Integer.valueOf(number)});
            }
            return String.format(Locale.US, "%d%s", new Object[]{Integer.valueOf(number), K.toString()});
        } else if (K.length() == 2) {
            return String.format(Locale.US, "%d.%dM", new Object[]{Integer.valueOf(number), Integer.valueOf(lastDec)});
        } else {
            return String.format(Locale.US, "%d.%d%s", new Object[]{Integer.valueOf(number), Integer.valueOf(lastDec), K.toString()});
        }
    }

    public static String formatUserStatus(int currentAccount, TLRPC.User user) {
        return formatUserStatus(currentAccount, user, (boolean[]) null);
    }

    public static String formatUserStatus(int currentAccount, TLRPC.User user, boolean[] isOnline) {
        if (!(user == null || user.status == null || user.status.expires != 0)) {
            if (user.status instanceof TLRPC.TL_userStatusRecently) {
                user.status.expires = -100;
            } else if (user.status instanceof TLRPC.TL_userStatusLastWeek) {
                user.status.expires = -101;
            } else if (user.status instanceof TLRPC.TL_userStatusLastMonth) {
                user.status.expires = -102;
            }
        }
        if (user != null && user.status != null && user.status.expires <= 0 && MessagesController.getInstance(currentAccount).onlinePrivacy.containsKey(Integer.valueOf(user.id))) {
            if (isOnline != null) {
                isOnline[0] = true;
            }
            return getString("Online", R.string.Online);
        } else if (user == null || user.status == null || user.status.expires == 0 || UserObject.isDeleted(user) || (user instanceof TLRPC.TL_userEmpty)) {
            return getString("ALongTimeAgo", R.string.ALongTimeAgo);
        } else {
            if (user.status.expires > ConnectionsManager.getInstance(currentAccount).getCurrentTime()) {
                if (isOnline != null) {
                    isOnline[0] = true;
                }
                return getString("Online", R.string.Online);
            } else if (user.status.expires == -1) {
                return getString("Invisible", R.string.Invisible);
            } else {
                if (user.status.expires == -100) {
                    return getString("Lately", R.string.Lately);
                }
                if (user.status.expires == -101) {
                    return getString("WithinAWeek", R.string.WithinAWeek);
                }
                if (user.status.expires == -102) {
                    return getString("WithinAMonth", R.string.WithinAMonth);
                }
                return formatDateOnline((long) user.status.expires);
            }
        }
    }

    public static String formatUserStatusNew(int currentAccount, TLRPC.User user, boolean[] isOnline) {
        if (!(user == null || user.status == null || user.status.expires != 0)) {
            if (user.status instanceof TLRPC.TL_userStatusRecently) {
                user.status.expires = -100;
            } else if (user.status instanceof TLRPC.TL_userStatusLastWeek) {
                user.status.expires = -101;
            } else if (user.status instanceof TLRPC.TL_userStatusLastMonth) {
                user.status.expires = -102;
            }
        }
        if (user != null && user.status != null && user.status.expires <= 0 && MessagesController.getInstance(currentAccount).onlinePrivacy.containsKey(Integer.valueOf(user.id))) {
            if (isOnline != null) {
                isOnline[0] = true;
            }
            return getString("New_Online", R.string.New_Online);
        } else if (user == null || user.status == null || user.status.expires == 0 || UserObject.isDeleted(user) || (user instanceof TLRPC.TL_userEmpty)) {
            return getString("ALongTimeAgo", R.string.ALongTimeAgo);
        } else {
            if (user.status.expires > ConnectionsManager.getInstance(currentAccount).getCurrentTime()) {
                if (isOnline != null) {
                    isOnline[0] = true;
                }
                return getString("New_Online", R.string.New_Online);
            } else if (user.status.expires == -1) {
                return getString("Invisible", R.string.Invisible);
            } else {
                if (user.status.expires == -100) {
                    return formatString("LastSeenDays", R.string.LastSeenDays, 1);
                } else if (user.status.expires == -101) {
                    return formatString("LastSeenDays", R.string.LastSeenDays, 3);
                } else if (user.status.expires != -102) {
                    return formatDateOnlineNew((long) user.status.expires);
                } else {
                    return formatString("LastSeenDays", R.string.LastSeenDays, 15);
                }
            }
        }
    }

    private String escapeString(String str) {
        if (str.contains("[CDATA")) {
            return str;
        }
        return str.replace("<", "&lt;").replace(">", "&gt;").replace("& ", "&amp; ");
    }

    public void saveRemoteLocaleStringsForCurrentLocale(TLRPC.TL_langPackDifference difference, int currentAccount) {
        if (this.currentLocaleInfo != null) {
            String langCode = difference.lang_code.replace('-', '_').toLowerCase();
            if (langCode.equals(this.currentLocaleInfo.shortName) || langCode.equals(this.currentLocaleInfo.baseLangCode)) {
                lambda$null$9$LocaleController(this.currentLocaleInfo, difference, currentAccount);
            }
        }
    }

    /* renamed from: saveRemoteLocaleStrings */
    public void lambda$null$9$LocaleController(LocaleInfo localeInfo, TLRPC.TL_langPackDifference difference, int currentAccount) {
        int type;
        File finalFile;
        HashMap<String, String> values;
        LocaleInfo localeInfo2 = localeInfo;
        TLRPC.TL_langPackDifference tL_langPackDifference = difference;
        if (tL_langPackDifference != null && !tL_langPackDifference.strings.isEmpty() && localeInfo2 != null && !localeInfo.isLocal()) {
            String langCode = tL_langPackDifference.lang_code.replace('-', '_').toLowerCase();
            if (langCode.equals(localeInfo2.shortName)) {
                type = 0;
            } else if (langCode.equals(localeInfo2.baseLangCode)) {
                type = 1;
            } else {
                type = -1;
            }
            if (type != -1) {
                if (type == 0) {
                    finalFile = localeInfo.getPathToFile();
                } else {
                    finalFile = localeInfo.getPathToBaseFile();
                }
                try {
                    if (tL_langPackDifference.from_version == 0) {
                        values = new HashMap<>();
                    } else {
                        values = getLocaleFileStrings(finalFile, true);
                    }
                    for (int a = 0; a < tL_langPackDifference.strings.size(); a++) {
                        TLRPC.LangPackString string = tL_langPackDifference.strings.get(a);
                        if (string instanceof TLRPC.TL_langPackString) {
                            values.put(string.key, escapeString(string.value));
                        } else if (string instanceof TLRPC.TL_langPackStringPluralized) {
                            String str = "";
                            values.put(string.key + "_zero", string.zero_value != null ? escapeString(string.zero_value) : str);
                            values.put(string.key + "_one", string.one_value != null ? escapeString(string.one_value) : str);
                            values.put(string.key + "_two", string.two_value != null ? escapeString(string.two_value) : str);
                            values.put(string.key + "_few", string.few_value != null ? escapeString(string.few_value) : str);
                            values.put(string.key + "_many", string.many_value != null ? escapeString(string.many_value) : str);
                            String str2 = string.key + "_other";
                            if (string.other_value != null) {
                                str = escapeString(string.other_value);
                            }
                            values.put(str2, str);
                        } else if (string instanceof TLRPC.TL_langPackStringDeleted) {
                            values.remove(string.key);
                        }
                    }
                    if (BuildVars.LOGS_ENABLED != 0) {
                        FileLog.d("save locale file to " + finalFile);
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(finalFile));
                    writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                    writer.write("<resources>\n");
                    for (Map.Entry<String, String> entry : values.entrySet()) {
                        writer.write(String.format("<string name=\"%1$s\">%2$s</string>\n", new Object[]{entry.getKey(), entry.getValue()}));
                    }
                    writer.write("</resources>");
                    writer.close();
                    boolean hasBase = localeInfo.hasBaseLang();
                    HashMap<String, String> valuesToSet = getLocaleFileStrings(hasBase ? localeInfo.getPathToBaseFile() : localeInfo.getPathToFile());
                    if (hasBase) {
                        valuesToSet.putAll(getLocaleFileStrings(localeInfo.getPathToFile()));
                    }
                    AndroidUtilities.runOnUIThread(new Runnable(localeInfo, type, difference, valuesToSet) {
                        private final /* synthetic */ LocaleController.LocaleInfo f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ TLRPC.TL_langPackDifference f$3;
                        private final /* synthetic */ HashMap f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            LocaleController.this.lambda$saveRemoteLocaleStrings$4$LocaleController(this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                } catch (Exception e) {
                }
            }
        }
    }

    public /* synthetic */ void lambda$saveRemoteLocaleStrings$4$LocaleController(LocaleInfo localeInfo, int type, TLRPC.TL_langPackDifference difference, HashMap valuesToSet) {
        String[] args;
        Locale newLocale;
        if (localeInfo != null) {
            if (type == 0) {
                localeInfo.version = difference.version;
            } else {
                localeInfo.baseVersion = difference.version;
            }
        }
        saveOtherLanguages();
        try {
            if (this.currentLocaleInfo == localeInfo) {
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    args = localeInfo.pluralLangCode.split("_");
                } else if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    args = localeInfo.baseLangCode.split("_");
                } else {
                    args = localeInfo.shortName.split("_");
                }
                if (args.length == 1) {
                    newLocale = new Locale(args[0]);
                } else {
                    newLocale = new Locale(args[0], args[1]);
                }
                this.languageOverride = localeInfo.shortName;
                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putString("language", localeInfo.getKey());
                editor.commit();
                this.localeValues = valuesToSet;
                this.currentLocale = newLocale;
                this.currentLocaleInfo = localeInfo;
                if (localeInfo != null && !TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    PluralRules pluralRules = this.allRules.get(this.currentLocale.getLanguage());
                    this.currentPluralRules = pluralRules;
                    if (pluralRules == null) {
                        this.currentPluralRules = this.allRules.get("en");
                    }
                }
                this.changingConfiguration = true;
                Locale.setDefault(this.currentLocale);
                Configuration config = new Configuration();
                config.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(config, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.changingConfiguration = false;
        }
        recreateFormatters();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reloadInterface, new Object[0]);
    }

    public void loadRemoteLanguages(int currentAccount) {
    }

    private /* synthetic */ void lambda$loadRemoteLanguages$6(int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(response, currentAccount) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    LocaleController.this.lambda$null$5$LocaleController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$5$LocaleController(TLObject response, int currentAccount) {
        this.loadingRemoteLanguages = false;
        TLRPC.Vector res = (TLRPC.Vector) response;
        int size = this.remoteLanguages.size();
        for (int a = 0; a < size; a++) {
            this.remoteLanguages.get(a).serverIndex = Integer.MAX_VALUE;
        }
        int size2 = res.objects.size();
        for (int a2 = 0; a2 < size2; a2++) {
            TLRPC.TL_langPackLanguage language = (TLRPC.TL_langPackLanguage) res.objects.get(a2);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded lang " + language.name);
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.nameEnglish = language.name;
            localeInfo.name = language.native_name;
            localeInfo.shortName = language.lang_code.replace('-', '_').toLowerCase();
            if (language.base_lang_code != null) {
                localeInfo.baseLangCode = language.base_lang_code.replace('-', '_').toLowerCase();
            } else {
                localeInfo.baseLangCode = "";
            }
            localeInfo.pluralLangCode = language.plural_code.replace('-', '_').toLowerCase();
            localeInfo.pathToFile = "remote";
            localeInfo.serverIndex = a2;
            LocaleInfo existing = getLanguageFromDict(localeInfo.getKey());
            if (existing == null) {
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
            } else {
                existing.nameEnglish = localeInfo.nameEnglish;
                existing.name = localeInfo.name;
                existing.baseLangCode = localeInfo.baseLangCode;
                existing.pluralLangCode = localeInfo.pluralLangCode;
                existing.pathToFile = localeInfo.pathToFile;
                existing.serverIndex = localeInfo.serverIndex;
                localeInfo = existing;
            }
            if (!this.remoteLanguagesDict.containsKey(localeInfo.getKey())) {
                this.remoteLanguages.add(localeInfo);
                this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo);
            }
        }
        int a3 = 0;
        while (a3 < this.remoteLanguages.size()) {
            LocaleInfo info = this.remoteLanguages.get(a3);
            if (info.serverIndex == Integer.MAX_VALUE && info != this.currentLocaleInfo) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("remove lang " + info.getKey());
                }
                this.remoteLanguages.remove(a3);
                this.remoteLanguagesDict.remove(info.getKey());
                this.languages.remove(info);
                this.languagesDict.remove(info.getKey());
                a3--;
            }
            a3++;
        }
        saveOtherLanguages();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.suggestedLangpack, new Object[0]);
        applyLanguage(this.currentLocaleInfo, true, false, currentAccount);
    }

    private void applyRemoteLanguage(LocaleInfo localeInfo, String langCode, boolean force, int currentAccount) {
        if (localeInfo == null) {
            return;
        }
        if (localeInfo == null || localeInfo.isRemote() || localeInfo.isUnofficial()) {
            if (localeInfo.hasBaseLang() && (langCode == null || langCode.equals(localeInfo.baseLangCode))) {
                if (localeInfo.baseVersion == 0 || force) {
                    TLRPC.TL_langpack_getLangPack req = new TLRPC.TL_langpack_getLangPack();
                    req.lang_code = localeInfo.getBaseLangCode();
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate(localeInfo, currentAccount) {
                        private final /* synthetic */ LocaleController.LocaleInfo f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LocaleController.this.lambda$applyRemoteLanguage$10$LocaleController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    }, 8);
                } else if (localeInfo.hasBaseLang()) {
                    TLRPC.TL_langpack_getDifference req2 = new TLRPC.TL_langpack_getDifference();
                    req2.from_version = localeInfo.baseVersion;
                    req2.lang_code = localeInfo.getBaseLangCode();
                    req2.lang_pack = "";
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req2, new RequestDelegate(localeInfo, currentAccount) {
                        private final /* synthetic */ LocaleController.LocaleInfo f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LocaleController.this.lambda$applyRemoteLanguage$8$LocaleController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    }, 8);
                }
            }
            if (langCode != null && !langCode.equals(localeInfo.shortName)) {
                return;
            }
            if (localeInfo.version == 0 || force) {
                for (int a = 0; a < 3; a++) {
                    ConnectionsManager.setLangCode(localeInfo.getLangCode());
                }
                TLRPC.TL_langpack_getLangPack req3 = new TLRPC.TL_langpack_getLangPack();
                req3.lang_code = localeInfo.getLangCode();
                ConnectionsManager.getInstance(currentAccount).sendRequest(req3, new RequestDelegate(localeInfo, currentAccount) {
                    private final /* synthetic */ LocaleController.LocaleInfo f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LocaleController.this.lambda$applyRemoteLanguage$14$LocaleController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                }, 8);
                return;
            }
            TLRPC.TL_langpack_getDifference req4 = new TLRPC.TL_langpack_getDifference();
            req4.from_version = localeInfo.version;
            req4.lang_code = localeInfo.getLangCode();
            req4.lang_pack = "";
            ConnectionsManager.getInstance(currentAccount).sendRequest(req4, new RequestDelegate(localeInfo, currentAccount) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocaleController.this.lambda$applyRemoteLanguage$12$LocaleController(this.f$1, this.f$2, tLObject, tL_error);
                }
            }, 8);
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$8$LocaleController(LocaleInfo localeInfo, int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(localeInfo, response, currentAccount) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocaleController.this.lambda$null$7$LocaleController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$10$LocaleController(LocaleInfo localeInfo, int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(localeInfo, response, currentAccount) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocaleController.this.lambda$null$9$LocaleController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$12$LocaleController(LocaleInfo localeInfo, int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(localeInfo, response, currentAccount) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocaleController.this.lambda$null$11$LocaleController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyRemoteLanguage$14$LocaleController(LocaleInfo localeInfo, int currentAccount, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(localeInfo, response, currentAccount) {
                private final /* synthetic */ LocaleController.LocaleInfo f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocaleController.this.lambda$null$13$LocaleController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public String getTranslitString(String src) {
        return getTranslitString(src, true, false);
    }

    public String getTranslitString(String src, boolean onlyEnglish) {
        return getTranslitString(src, true, onlyEnglish);
    }

    public String getTranslitString(String src, boolean ru, boolean onlyEnglish) {
        Object obj;
        Object obj2;
        Object obj3;
        Object obj4;
        Object obj5;
        Object obj6;
        Object obj7;
        Object obj8;
        Object obj9;
        if (src == null) {
            return null;
        }
        Object obj10 = "h";
        Object obj11 = "t";
        Object obj12 = "u";
        Object obj13 = "s";
        Object obj14 = "r";
        if (this.ruTranslitChars == null) {
            Object obj15 = TtmlNode.TAG_P;
            HashMap<String, String> hashMap = new HashMap<>(33);
            this.ruTranslitChars = hashMap;
            hashMap.put("а", "a");
            this.ruTranslitChars.put("б", "b");
            this.ruTranslitChars.put("в", "v");
            this.ruTranslitChars.put("г", ImageLoader.AUTOPLAY_FILTER);
            this.ruTranslitChars.put("д", "d");
            this.ruTranslitChars.put("е", "e");
            HashMap<String, String> hashMap2 = this.ruTranslitChars;
            obj = ImageLoader.AUTOPLAY_FILTER;
            hashMap2.put("ё", "yo");
            this.ruTranslitChars.put("ж", "zh");
            this.ruTranslitChars.put("з", "z");
            this.ruTranslitChars.put("и", "i");
            this.ruTranslitChars.put("й", "i");
            this.ruTranslitChars.put("к", "k");
            this.ruTranslitChars.put("л", "l");
            this.ruTranslitChars.put("м", "m");
            this.ruTranslitChars.put("н", "n");
            this.ruTranslitChars.put("о", "o");
            Object obj16 = obj15;
            this.ruTranslitChars.put("п", obj16);
            obj2 = "m";
            obj9 = obj14;
            this.ruTranslitChars.put("р", obj9);
            obj3 = "z";
            Object obj17 = obj13;
            this.ruTranslitChars.put("с", obj17);
            obj4 = "v";
            this.ruTranslitChars.put("т", obj11);
            obj8 = obj12;
            this.ruTranslitChars.put("у", obj8);
            obj5 = obj17;
            this.ruTranslitChars.put("ф", "f");
            obj7 = obj10;
            this.ruTranslitChars.put("х", obj7);
            obj6 = obj16;
            this.ruTranslitChars.put("ц", "ts");
            this.ruTranslitChars.put("ч", "ch");
            this.ruTranslitChars.put("ш", "sh");
            this.ruTranslitChars.put("щ", "sch");
            this.ruTranslitChars.put("ы", "i");
            this.ruTranslitChars.put("ь", "");
            this.ruTranslitChars.put("ъ", "");
            this.ruTranslitChars.put("э", "e");
            this.ruTranslitChars.put("ю", "yu");
            this.ruTranslitChars.put("я", "ya");
        } else {
            obj2 = "m";
            obj = ImageLoader.AUTOPLAY_FILTER;
            obj9 = obj14;
            obj3 = "z";
            obj7 = obj10;
            obj6 = TtmlNode.TAG_P;
            Object obj18 = obj13;
            obj4 = "v";
            obj8 = obj12;
            obj5 = obj18;
        }
        if (this.translitChars == null) {
            HashMap<String, String> hashMap3 = new HashMap<>(487);
            this.translitChars = hashMap3;
            hashMap3.put("ȼ", "c");
            this.translitChars.put("ᶇ", "n");
            this.translitChars.put("ɖ", "d");
            this.translitChars.put("ỿ", "y");
            this.translitChars.put("ᴓ", "o");
            this.translitChars.put("ø", "o");
            this.translitChars.put("ḁ", "a");
            this.translitChars.put("ʯ", obj7);
            this.translitChars.put("ŷ", "y");
            this.translitChars.put("ʞ", "k");
            this.translitChars.put("ừ", obj8);
            Object obj19 = obj8;
            this.translitChars.put("ꜳ", "aa");
            this.translitChars.put("ĳ", "ij");
            this.translitChars.put("ḽ", "l");
            this.translitChars.put("ɪ", "i");
            this.translitChars.put("ḇ", "b");
            this.translitChars.put("ʀ", obj9);
            this.translitChars.put("ě", "e");
            this.translitChars.put("ﬃ", "ffi");
            this.translitChars.put("ơ", "o");
            this.translitChars.put("ⱹ", obj9);
            this.translitChars.put("ồ", "o");
            this.translitChars.put("ǐ", "i");
            Object obj20 = obj6;
            this.translitChars.put("ꝕ", obj20);
            this.translitChars.put("ý", "y");
            this.translitChars.put("ḝ", "e");
            this.translitChars.put("ₒ", "o");
            this.translitChars.put("ⱥ", "a");
            this.translitChars.put("ʙ", "b");
            this.translitChars.put("ḛ", "e");
            this.translitChars.put("ƈ", "c");
            this.translitChars.put("ɦ", obj7);
            this.translitChars.put("ᵬ", "b");
            Object obj21 = obj7;
            Object obj22 = obj5;
            this.translitChars.put("ṣ", obj22);
            this.translitChars.put("đ", "d");
            this.translitChars.put("ỗ", "o");
            this.translitChars.put("ɟ", "j");
            this.translitChars.put("ẚ", "a");
            this.translitChars.put("ɏ", "y");
            this.translitChars.put("ʌ", obj4);
            this.translitChars.put("ꝓ", obj20);
            this.translitChars.put("ﬁ", "fi");
            this.translitChars.put("ᶄ", "k");
            this.translitChars.put("ḏ", "d");
            this.translitChars.put("ᴌ", "l");
            this.translitChars.put("ė", "e");
            this.translitChars.put("ᴋ", "k");
            this.translitChars.put("ċ", "c");
            this.translitChars.put("ʁ", obj9);
            this.translitChars.put("ƕ", "hv");
            this.translitChars.put("ƀ", "b");
            this.translitChars.put("ṍ", "o");
            this.translitChars.put("ȣ", "ou");
            this.translitChars.put("ǰ", "j");
            Object obj23 = obj;
            this.translitChars.put("ᶃ", obj23);
            Object obj24 = obj20;
            Object obj25 = "n";
            this.translitChars.put("ṋ", obj25);
            this.translitChars.put("ɉ", "j");
            this.translitChars.put("ǧ", obj23);
            this.translitChars.put("ǳ", "dz");
            Object obj26 = obj3;
            this.translitChars.put("ź", obj26);
            this.translitChars.put("ꜷ", "au");
            Object obj27 = obj19;
            this.translitChars.put("ǖ", obj27);
            this.translitChars.put("ᵹ", obj23);
            this.translitChars.put("ȯ", "o");
            this.translitChars.put("ɐ", "a");
            this.translitChars.put("ą", "a");
            this.translitChars.put("õ", "o");
            this.translitChars.put("ɻ", obj9);
            this.translitChars.put("ꝍ", "o");
            this.translitChars.put("ǟ", "a");
            this.translitChars.put("ȴ", "l");
            this.translitChars.put("ʂ", obj22);
            this.translitChars.put("ﬂ", "fl");
            Object obj28 = "i";
            this.translitChars.put("ȉ", obj28);
            this.translitChars.put("ⱻ", "e");
            this.translitChars.put("ṉ", obj25);
            this.translitChars.put("ï", obj28);
            this.translitChars.put("ñ", obj25);
            this.translitChars.put("ᴉ", obj28);
            Object obj29 = obj25;
            Object obj30 = obj11;
            this.translitChars.put("ʇ", obj30);
            this.translitChars.put("ẓ", obj26);
            this.translitChars.put("ỷ", "y");
            this.translitChars.put("ȳ", "y");
            this.translitChars.put("ṩ", obj22);
            this.translitChars.put("ɽ", obj9);
            this.translitChars.put("ĝ", obj23);
            this.translitChars.put("ᴝ", obj27);
            this.translitChars.put("ḳ", "k");
            this.translitChars.put("ꝫ", "et");
            this.translitChars.put("ī", obj28);
            this.translitChars.put("ť", obj30);
            this.translitChars.put("ꜿ", "c");
            this.translitChars.put("ʟ", "l");
            this.translitChars.put("ꜹ", "av");
            this.translitChars.put("û", obj27);
            this.translitChars.put("æ", "ae");
            this.translitChars.put("ă", "a");
            this.translitChars.put("ǘ", obj27);
            this.translitChars.put("ꞅ", obj22);
            this.translitChars.put("ᵣ", obj9);
            this.translitChars.put("ᴀ", "a");
            Object obj31 = "b";
            this.translitChars.put("ƃ", obj31);
            Object obj32 = "l";
            Object obj33 = obj21;
            this.translitChars.put("ḩ", obj33);
            this.translitChars.put("ṧ", obj22);
            this.translitChars.put("ₑ", "e");
            this.translitChars.put("ʜ", obj33);
            Object obj34 = obj22;
            this.translitChars.put("ẋ", "x");
            this.translitChars.put("ꝅ", "k");
            Object obj35 = "d";
            this.translitChars.put("ḋ", obj35);
            Object obj36 = obj30;
            this.translitChars.put("ƣ", "oi");
            Object obj37 = obj24;
            this.translitChars.put("ꝑ", obj37);
            this.translitChars.put("ħ", obj33);
            Object obj38 = obj33;
            Object obj39 = obj4;
            this.translitChars.put("ⱴ", obj39);
            Object obj40 = obj28;
            this.translitChars.put("ẇ", "w");
            Object obj41 = obj29;
            this.translitChars.put("ǹ", obj41);
            Object obj42 = obj2;
            this.translitChars.put("ɯ", obj42);
            this.translitChars.put("ɡ", obj23);
            this.translitChars.put("ɴ", obj41);
            this.translitChars.put("ᴘ", obj37);
            this.translitChars.put("ᵥ", obj39);
            this.translitChars.put("ū", obj27);
            this.translitChars.put("ḃ", obj31);
            this.translitChars.put("ṗ", obj37);
            this.translitChars.put("å", "a");
            this.translitChars.put("ɕ", "c");
            Object obj43 = obj37;
            Object obj44 = "o";
            this.translitChars.put("ọ", obj44);
            this.translitChars.put("ắ", "a");
            Object obj45 = obj23;
            this.translitChars.put("ƒ", "f");
            this.translitChars.put("ǣ", "ae");
            this.translitChars.put("ꝡ", "vy");
            this.translitChars.put("ﬀ", "ff");
            this.translitChars.put("ᶉ", obj9);
            this.translitChars.put("ô", obj44);
            this.translitChars.put("ǿ", obj44);
            this.translitChars.put("ṳ", obj27);
            this.translitChars.put("ȥ", obj26);
            this.translitChars.put("ḟ", "f");
            this.translitChars.put("ḓ", obj35);
            this.translitChars.put("ȇ", "e");
            this.translitChars.put("ȕ", obj27);
            this.translitChars.put("ȵ", obj41);
            this.translitChars.put("ʠ", "q");
            this.translitChars.put("ấ", "a");
            Object obj46 = "k";
            this.translitChars.put("ǩ", obj46);
            Object obj47 = obj42;
            this.translitChars.put("ĩ", obj40);
            this.translitChars.put("ṵ", obj27);
            Object obj48 = obj36;
            this.translitChars.put("ŧ", obj48);
            this.translitChars.put("ɾ", obj9);
            this.translitChars.put("ƙ", obj46);
            this.translitChars.put("ṫ", obj48);
            Object obj49 = obj46;
            this.translitChars.put("ꝗ", "q");
            this.translitChars.put("ậ", "a");
            this.translitChars.put("ʄ", "j");
            this.translitChars.put("ƚ", obj32);
            this.translitChars.put("ᶂ", "f");
            Object obj50 = obj34;
            this.translitChars.put("ᵴ", obj50);
            this.translitChars.put("ꞃ", obj9);
            this.translitChars.put("ᶌ", obj39);
            this.translitChars.put("ɵ", obj44);
            this.translitChars.put("ḉ", "c");
            this.translitChars.put("ᵤ", obj27);
            this.translitChars.put("ẑ", obj26);
            this.translitChars.put("ṹ", obj27);
            this.translitChars.put("ň", obj41);
            Object obj51 = "c";
            Object obj52 = "w";
            this.translitChars.put("ʍ", obj52);
            this.translitChars.put("ầ", "a");
            Object obj53 = obj39;
            this.translitChars.put("ǉ", "lj");
            this.translitChars.put("ɓ", obj31);
            this.translitChars.put("ɼ", obj9);
            this.translitChars.put("ò", obj44);
            this.translitChars.put("ẘ", obj52);
            this.translitChars.put("ɗ", obj35);
            this.translitChars.put("ꜽ", "ay");
            this.translitChars.put("ư", obj27);
            this.translitChars.put("ᶀ", obj31);
            this.translitChars.put("ǜ", obj27);
            this.translitChars.put("ẹ", "e");
            this.translitChars.put("ǡ", "a");
            Object obj54 = obj38;
            this.translitChars.put("ɥ", obj54);
            this.translitChars.put("ṏ", obj44);
            this.translitChars.put("ǔ", obj27);
            Object obj55 = obj31;
            Object obj56 = "y";
            this.translitChars.put("ʎ", obj56);
            this.translitChars.put("ȱ", obj44);
            this.translitChars.put("ệ", "e");
            this.translitChars.put("ế", "e");
            Object obj57 = obj40;
            this.translitChars.put("ĭ", obj57);
            this.translitChars.put("ⱸ", "e");
            this.translitChars.put("ṯ", obj48);
            this.translitChars.put("ᶑ", obj35);
            this.translitChars.put("ḧ", obj54);
            this.translitChars.put("ṥ", obj50);
            this.translitChars.put("ë", "e");
            Object obj58 = obj48;
            Object obj59 = obj47;
            this.translitChars.put("ᴍ", obj59);
            this.translitChars.put("ö", obj44);
            this.translitChars.put("é", "e");
            this.translitChars.put("ı", obj57);
            this.translitChars.put("ď", obj35);
            this.translitChars.put("ᵯ", obj59);
            this.translitChars.put("ỵ", obj56);
            this.translitChars.put("ŵ", obj52);
            this.translitChars.put("ề", "e");
            this.translitChars.put("ứ", obj27);
            this.translitChars.put("ƶ", obj26);
            this.translitChars.put("ĵ", "j");
            this.translitChars.put("ḍ", obj35);
            this.translitChars.put("ŭ", obj27);
            this.translitChars.put("ʝ", "j");
            this.translitChars.put("ê", "e");
            this.translitChars.put("ǚ", obj27);
            this.translitChars.put("ġ", obj45);
            this.translitChars.put("ṙ", obj9);
            this.translitChars.put("ƞ", obj41);
            this.translitChars.put("ḗ", "e");
            this.translitChars.put("ẝ", obj50);
            this.translitChars.put("ᶁ", obj35);
            Object obj60 = obj49;
            this.translitChars.put("ķ", obj60);
            Object obj61 = obj57;
            this.translitChars.put("ᴂ", "ae");
            this.translitChars.put("ɘ", "e");
            this.translitChars.put("ợ", obj44);
            this.translitChars.put("ḿ", obj59);
            this.translitChars.put("ꜰ", "f");
            Object obj62 = "a";
            this.translitChars.put("ẵ", obj62);
            Object obj63 = obj44;
            this.translitChars.put("ꝏ", "oo");
            this.translitChars.put("ᶆ", obj59);
            Object obj64 = obj43;
            this.translitChars.put("ᵽ", obj64);
            this.translitChars.put("ữ", obj27);
            this.translitChars.put("ⱪ", obj60);
            this.translitChars.put("ḥ", obj54);
            Object obj65 = obj60;
            Object obj66 = obj58;
            this.translitChars.put("ţ", obj66);
            this.translitChars.put("ᵱ", obj64);
            this.translitChars.put("ṁ", obj59);
            this.translitChars.put("á", obj62);
            this.translitChars.put("ᴎ", obj41);
            Object obj67 = obj50;
            Object obj68 = obj53;
            this.translitChars.put("ꝟ", obj68);
            this.translitChars.put("è", "e");
            this.translitChars.put("ᶎ", obj26);
            this.translitChars.put("ꝺ", obj35);
            this.translitChars.put("ᶈ", obj64);
            Object obj69 = obj64;
            Object obj70 = obj32;
            this.translitChars.put("ɫ", obj70);
            this.translitChars.put("ᴢ", obj26);
            this.translitChars.put("ɱ", obj59);
            this.translitChars.put("ṝ", obj9);
            this.translitChars.put("ṽ", obj68);
            this.translitChars.put("ũ", obj27);
            Object obj71 = obj59;
            this.translitChars.put("ß", DownloadAction.TYPE_SS);
            this.translitChars.put("ĥ", obj54);
            this.translitChars.put("ᵵ", obj66);
            this.translitChars.put("ʐ", obj26);
            this.translitChars.put("ṟ", obj9);
            this.translitChars.put("ɲ", obj41);
            this.translitChars.put("à", obj62);
            this.translitChars.put("ẙ", obj56);
            this.translitChars.put("ỳ", obj56);
            this.translitChars.put("ᴔ", "oe");
            this.translitChars.put("ₓ", "x");
            this.translitChars.put("ȗ", obj27);
            this.translitChars.put("ⱼ", "j");
            this.translitChars.put("ẫ", obj62);
            this.translitChars.put("ʑ", obj26);
            Object obj72 = obj67;
            this.translitChars.put("ẛ", obj72);
            Object obj73 = obj41;
            Object obj74 = obj61;
            this.translitChars.put("ḭ", obj74);
            Object obj75 = obj68;
            this.translitChars.put("ꜵ", "ao");
            this.translitChars.put("ɀ", obj26);
            this.translitChars.put("ÿ", obj56);
            this.translitChars.put("ǝ", "e");
            Object obj76 = obj63;
            this.translitChars.put("ǭ", obj76);
            this.translitChars.put("ᴅ", obj35);
            this.translitChars.put("ᶅ", obj70);
            this.translitChars.put("ù", obj27);
            this.translitChars.put("ạ", obj62);
            Object obj77 = obj35;
            this.translitChars.put("ḅ", obj55);
            this.translitChars.put("ụ", obj27);
            this.translitChars.put("ằ", obj62);
            this.translitChars.put("ᴛ", obj66);
            this.translitChars.put("ƴ", obj56);
            this.translitChars.put("ⱦ", obj66);
            this.translitChars.put("ⱡ", obj70);
            this.translitChars.put("ȷ", "j");
            this.translitChars.put("ᵶ", obj26);
            this.translitChars.put("ḫ", obj54);
            Object obj78 = obj52;
            this.translitChars.put("ⱳ", obj78);
            Object obj79 = obj54;
            this.translitChars.put("ḵ", obj65);
            this.translitChars.put("ờ", obj76);
            this.translitChars.put("î", obj74);
            this.translitChars.put("ģ", obj45);
            this.translitChars.put("ȅ", "e");
            this.translitChars.put("ȧ", obj62);
            this.translitChars.put("ẳ", obj62);
            this.translitChars.put("ɋ", "q");
            this.translitChars.put("ṭ", obj66);
            this.translitChars.put("ꝸ", "um");
            this.translitChars.put("ᴄ", obj51);
            this.translitChars.put("ẍ", "x");
            this.translitChars.put("ủ", obj27);
            this.translitChars.put("ỉ", obj74);
            this.translitChars.put("ᴚ", obj9);
            this.translitChars.put("ś", obj72);
            this.translitChars.put("ꝋ", obj76);
            this.translitChars.put("ỹ", obj56);
            this.translitChars.put("ṡ", obj72);
            this.translitChars.put("ǌ", "nj");
            this.translitChars.put("ȁ", obj62);
            this.translitChars.put("ẗ", obj66);
            this.translitChars.put("ĺ", obj70);
            this.translitChars.put("ž", obj26);
            this.translitChars.put("ᵺ", "th");
            Object obj80 = obj77;
            this.translitChars.put("ƌ", obj80);
            this.translitChars.put("ș", obj72);
            this.translitChars.put("š", obj72);
            this.translitChars.put("ᶙ", obj27);
            this.translitChars.put("ẽ", "e");
            this.translitChars.put("ẜ", obj72);
            this.translitChars.put("ɇ", "e");
            this.translitChars.put("ṷ", obj27);
            this.translitChars.put("ố", obj76);
            this.translitChars.put("ȿ", obj72);
            Object obj81 = obj56;
            Object obj82 = obj75;
            this.translitChars.put("ᴠ", obj82);
            Object obj83 = obj70;
            this.translitChars.put("ꝭ", "is");
            this.translitChars.put("ᴏ", obj76);
            this.translitChars.put("ɛ", "e");
            this.translitChars.put("ǻ", obj62);
            this.translitChars.put("ﬄ", "ffl");
            this.translitChars.put("ⱺ", obj76);
            this.translitChars.put("ȋ", obj74);
            this.translitChars.put("ᵫ", "ue");
            this.translitChars.put("ȡ", obj80);
            this.translitChars.put("ⱬ", obj26);
            this.translitChars.put("ẁ", obj78);
            this.translitChars.put("ᶏ", obj62);
            this.translitChars.put("ꞇ", obj66);
            Object obj84 = obj45;
            this.translitChars.put("ğ", obj84);
            Object obj85 = obj78;
            Object obj86 = obj73;
            this.translitChars.put("ɳ", obj86);
            this.translitChars.put("ʛ", obj84);
            this.translitChars.put("ᴜ", obj27);
            this.translitChars.put("ẩ", obj62);
            this.translitChars.put("ṅ", obj86);
            this.translitChars.put("ɨ", obj74);
            this.translitChars.put("ᴙ", obj9);
            this.translitChars.put("ǎ", obj62);
            this.translitChars.put("ſ", obj72);
            this.translitChars.put("ȫ", obj76);
            this.translitChars.put("ɿ", obj9);
            this.translitChars.put("ƭ", obj66);
            this.translitChars.put("ḯ", obj74);
            this.translitChars.put("ǽ", "ae");
            this.translitChars.put("ⱱ", obj82);
            this.translitChars.put("ɶ", "oe");
            this.translitChars.put("ṃ", obj71);
            this.translitChars.put("ż", obj26);
            this.translitChars.put("ĕ", "e");
            this.translitChars.put("ꜻ", "av");
            this.translitChars.put("ở", obj76);
            this.translitChars.put("ễ", "e");
            Object obj87 = obj83;
            this.translitChars.put("ɬ", obj87);
            this.translitChars.put("ị", obj74);
            this.translitChars.put("ᵭ", obj80);
            Object obj88 = obj82;
            this.translitChars.put("ﬆ", "st");
            this.translitChars.put("ḷ", obj87);
            this.translitChars.put("ŕ", obj9);
            this.translitChars.put("ᴕ", "ou");
            this.translitChars.put("ʈ", obj66);
            this.translitChars.put("ā", obj62);
            this.translitChars.put("ḙ", "e");
            this.translitChars.put("ᴑ", obj76);
            Object obj89 = obj51;
            this.translitChars.put("ç", obj89);
            this.translitChars.put("ᶊ", obj72);
            this.translitChars.put("ặ", obj62);
            this.translitChars.put("ų", obj27);
            this.translitChars.put("ả", obj62);
            this.translitChars.put("ǥ", obj84);
            Object obj90 = obj27;
            Object obj91 = obj65;
            this.translitChars.put("ꝁ", obj91);
            this.translitChars.put("ẕ", obj26);
            this.translitChars.put("ŝ", obj72);
            this.translitChars.put("ḕ", "e");
            this.translitChars.put("ɠ", obj84);
            this.translitChars.put("ꝉ", obj87);
            this.translitChars.put("ꝼ", "f");
            this.translitChars.put("ᶍ", "x");
            this.translitChars.put("ǒ", obj76);
            this.translitChars.put("ę", "e");
            this.translitChars.put("ổ", obj76);
            this.translitChars.put("ƫ", obj66);
            this.translitChars.put("ǫ", obj76);
            this.translitChars.put("i̇", obj74);
            Object obj92 = obj73;
            this.translitChars.put("ṇ", obj92);
            this.translitChars.put("ć", obj89);
            this.translitChars.put("ᵷ", obj84);
            Object obj93 = obj89;
            Object obj94 = obj85;
            this.translitChars.put("ẅ", obj94);
            this.translitChars.put("ḑ", obj80);
            this.translitChars.put("ḹ", obj87);
            this.translitChars.put("œ", "oe");
            this.translitChars.put("ᵳ", obj9);
            this.translitChars.put("ļ", obj87);
            this.translitChars.put("ȑ", obj9);
            this.translitChars.put("ȭ", obj76);
            this.translitChars.put("ᵰ", obj92);
            this.translitChars.put("ᴁ", "ae");
            this.translitChars.put("ŀ", obj87);
            this.translitChars.put("ä", obj62);
            Object obj95 = obj69;
            this.translitChars.put("ƥ", obj95);
            this.translitChars.put("ỏ", obj76);
            this.translitChars.put("į", obj74);
            this.translitChars.put("ȓ", obj9);
            Object obj96 = obj72;
            this.translitChars.put("ǆ", "dz");
            this.translitChars.put("ḡ", obj84);
            Object obj97 = obj90;
            this.translitChars.put("ṻ", obj97);
            this.translitChars.put("ō", obj76);
            this.translitChars.put("ľ", obj87);
            this.translitChars.put("ẃ", obj94);
            this.translitChars.put("ț", obj66);
            this.translitChars.put("ń", obj92);
            this.translitChars.put("ɍ", obj9);
            this.translitChars.put("ȃ", obj62);
            this.translitChars.put("ü", obj97);
            this.translitChars.put("ꞁ", obj87);
            this.translitChars.put("ᴐ", obj76);
            this.translitChars.put("ớ", obj76);
            Object obj98 = obj91;
            this.translitChars.put("ᴃ", obj55);
            this.translitChars.put("ɹ", obj9);
            this.translitChars.put("ᵲ", obj9);
            this.translitChars.put("ʏ", obj81);
            this.translitChars.put("ᵮ", "f");
            Object obj99 = obj79;
            this.translitChars.put("ⱨ", obj99);
            this.translitChars.put("ŏ", obj76);
            this.translitChars.put("ú", obj97);
            this.translitChars.put("ṛ", obj9);
            this.translitChars.put("ʮ", obj99);
            this.translitChars.put("ó", obj76);
            this.translitChars.put("ů", obj97);
            this.translitChars.put("ỡ", obj76);
            this.translitChars.put("ṕ", obj95);
            this.translitChars.put("ᶖ", obj74);
            this.translitChars.put("ự", obj97);
            this.translitChars.put("ã", obj62);
            this.translitChars.put("ᵢ", obj74);
            this.translitChars.put("ṱ", obj66);
            this.translitChars.put("ể", "e");
            this.translitChars.put("ử", obj97);
            this.translitChars.put("í", obj74);
            this.translitChars.put("ɔ", obj76);
            this.translitChars.put("ɺ", obj9);
            this.translitChars.put("ɢ", obj84);
            this.translitChars.put("ř", obj9);
            this.translitChars.put("ẖ", obj99);
            this.translitChars.put("ű", obj97);
            this.translitChars.put("ȍ", obj76);
            this.translitChars.put("ḻ", obj87);
            this.translitChars.put("ḣ", obj99);
            this.translitChars.put("ȶ", obj66);
            this.translitChars.put("ņ", obj92);
            this.translitChars.put("ᶒ", "e");
            this.translitChars.put("ì", obj74);
            this.translitChars.put("ẉ", obj94);
            this.translitChars.put("ē", "e");
            this.translitChars.put("ᴇ", "e");
            this.translitChars.put("ł", obj87);
            this.translitChars.put("ộ", obj76);
            this.translitChars.put("ɭ", obj87);
            this.translitChars.put("ẏ", obj81);
            this.translitChars.put("ᴊ", "j");
            Object obj100 = obj98;
            this.translitChars.put("ḱ", obj100);
            Object obj101 = obj88;
            this.translitChars.put("ṿ", obj101);
            this.translitChars.put("ȩ", "e");
            this.translitChars.put("â", obj62);
            Object obj102 = obj96;
            this.translitChars.put("ş", obj102);
            this.translitChars.put("ŗ", obj9);
            this.translitChars.put("ʋ", obj101);
            this.translitChars.put("ₐ", obj62);
            Object obj103 = obj93;
            this.translitChars.put("ↄ", obj103);
            this.translitChars.put("ᶓ", "e");
            this.translitChars.put("ɰ", obj71);
            this.translitChars.put("ᴡ", obj94);
            this.translitChars.put("ȏ", obj76);
            this.translitChars.put("č", obj103);
            this.translitChars.put("ǵ", obj84);
            this.translitChars.put("ĉ", obj103);
            this.translitChars.put("ᶗ", obj76);
            this.translitChars.put("ꝃ", obj100);
            this.translitChars.put("ꝙ", "q");
            this.translitChars.put("ṑ", obj76);
            this.translitChars.put("ꜱ", obj102);
            this.translitChars.put("ṓ", obj76);
            this.translitChars.put("ȟ", obj99);
            this.translitChars.put("ő", obj76);
            this.translitChars.put("ꜩ", "tz");
            this.translitChars.put("ẻ", "e");
        }
        StringBuilder dst = new StringBuilder(src.length());
        int len = src.length();
        boolean upperCase = false;
        for (int a = 0; a < len; a++) {
            String ch = src.substring(a, a + 1);
            if (onlyEnglish) {
                String lower = ch.toLowerCase();
                upperCase = !ch.equals(lower);
                ch = lower;
            }
            String tch = this.translitChars.get(ch);
            if (tch == null && ru) {
                tch = this.ruTranslitChars.get(ch);
            }
            if (tch != null) {
                if (onlyEnglish && upperCase) {
                    if (tch.length() > 1) {
                        tch = tch.substring(0, 1).toUpperCase() + tch.substring(1);
                    } else {
                        tch = tch.toUpperCase();
                    }
                }
                dst.append(tch);
            } else {
                if (onlyEnglish) {
                    char c = ch.charAt(0);
                    if ((c < 'a' || c > 'z' || c < '0' || c > '9') && c != ' ' && c != '\'' && c != ',' && c != '.' && c != '&' && c != '-' && c != '/') {
                        return null;
                    }
                    if (upperCase) {
                        ch = ch.toUpperCase();
                    }
                }
                dst.append(ch);
            }
        }
        String str = src;
        return dst.toString();
    }

    public static class PluralRules_Zero extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0 || count == 1) {
                return 2;
            }
            return 0;
        }
    }

    public static class PluralRules_Welsh extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            if (count == 3) {
                return 8;
            }
            if (count == 6) {
                return 16;
            }
            return 0;
        }
    }

    public static class PluralRules_Two extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            return 0;
        }
    }

    public static class PluralRules_Tachelhit extends PluralRules {
        public int quantityForNumber(int count) {
            if (count >= 0 && count <= 1) {
                return 2;
            }
            if (count < 2 || count > 10) {
                return 0;
            }
            return 8;
        }
    }

    public static class PluralRules_Slovenian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (rem100 == 1) {
                return 2;
            }
            if (rem100 == 2) {
                return 4;
            }
            if (rem100 < 3 || rem100 > 4) {
                return 0;
            }
            return 8;
        }
    }

    public static class PluralRules_Romanian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 1) {
                return 2;
            }
            if (count == 0) {
                return 8;
            }
            if (rem100 < 1 || rem100 > 19) {
                return 0;
            }
            return 8;
        }
    }

    public static class PluralRules_Polish extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (count == 1) {
                return 2;
            }
            if (rem10 >= 2 && rem10 <= 4 && (rem100 < 12 || rem100 > 14)) {
                return 8;
            }
            if (rem10 >= 0 && rem10 <= 1) {
                return 16;
            }
            if (rem10 >= 5 && rem10 <= 9) {
                return 16;
            }
            if (rem100 < 12 || rem100 > 14) {
                return 0;
            }
            return 16;
        }
    }

    public static class PluralRules_One extends PluralRules {
        public int quantityForNumber(int count) {
            return count == 1 ? 2 : 0;
        }
    }

    public static class PluralRules_None extends PluralRules {
        public int quantityForNumber(int count) {
            return 0;
        }
    }

    public static class PluralRules_Maltese extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 1) {
                return 2;
            }
            if (count == 0) {
                return 8;
            }
            if (rem100 >= 2 && rem100 <= 10) {
                return 8;
            }
            if (rem100 < 11 || rem100 > 19) {
                return 0;
            }
            return 16;
        }
    }

    public static class PluralRules_Macedonian extends PluralRules {
        public int quantityForNumber(int count) {
            if (count % 10 != 1 || count == 11) {
                return 0;
            }
            return 2;
        }
    }

    public static class PluralRules_Lithuanian extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 == 1 && (rem100 < 11 || rem100 > 19)) {
                return 2;
            }
            if (rem10 < 2 || rem10 > 9) {
                return 0;
            }
            if (rem100 < 11 || rem100 > 19) {
                return 8;
            }
            return 0;
        }
    }

    public static class PluralRules_Latvian extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count % 10 != 1 || count % 100 == 11) {
                return 0;
            }
            return 2;
        }
    }

    public static class PluralRules_Langi extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count <= 0 || count >= 2) {
                return 0;
            }
            return 2;
        }
    }

    public static class PluralRules_French extends PluralRules {
        public int quantityForNumber(int count) {
            if (count < 0 || count >= 2) {
                return 0;
            }
            return 2;
        }
    }

    public static class PluralRules_Czech extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 1) {
                return 2;
            }
            if (count < 2 || count > 4) {
                return 0;
            }
            return 8;
        }
    }

    public static class PluralRules_Breton extends PluralRules {
        public int quantityForNumber(int count) {
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            if (count == 3) {
                return 8;
            }
            if (count == 6) {
                return 16;
            }
            return 0;
        }
    }

    public static class PluralRules_Balkan extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            int rem10 = count % 10;
            if (rem10 == 1 && rem100 != 11) {
                return 2;
            }
            if (rem10 >= 2 && rem10 <= 4 && (rem100 < 12 || rem100 > 14)) {
                return 8;
            }
            if (rem10 == 0) {
                return 16;
            }
            if (rem10 >= 5 && rem10 <= 9) {
                return 16;
            }
            if (rem100 < 11 || rem100 > 14) {
                return 0;
            }
            return 16;
        }
    }

    public static class PluralRules_Arabic extends PluralRules {
        public int quantityForNumber(int count) {
            int rem100 = count % 100;
            if (count == 0) {
                return 1;
            }
            if (count == 1) {
                return 2;
            }
            if (count == 2) {
                return 4;
            }
            if (rem100 >= 3 && rem100 <= 10) {
                return 8;
            }
            if (rem100 < 11 || rem100 > 99) {
                return 0;
            }
            return 16;
        }
    }

    public static String addNbsp(String src) {
        return src.replace(' ', Typography.nbsp);
    }

    public static void resetImperialSystemType() {
        useImperialSystemType = null;
    }

    public static String formatDistance(float distance) {
        String arg;
        String arg2;
        boolean z;
        if (useImperialSystemType == null) {
            if (SharedConfig.distanceSystemType == 0) {
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                    if (telephonyManager != null) {
                        String country = telephonyManager.getSimCountryIso().toUpperCase();
                        if (!"US".equals(country) && !"GB".equals(country) && !"MM".equals(country)) {
                            if (!"LR".equals(country)) {
                                z = false;
                                useImperialSystemType = Boolean.valueOf(z);
                            }
                        }
                        z = true;
                        useImperialSystemType = Boolean.valueOf(z);
                    }
                } catch (Exception e) {
                    useImperialSystemType = false;
                    FileLog.e((Throwable) e);
                }
            } else {
                useImperialSystemType = Boolean.valueOf(SharedConfig.distanceSystemType == 2);
            }
        }
        if (useImperialSystemType.booleanValue()) {
            float distance2 = distance * 3.28084f;
            if (distance2 < 1000.0f) {
                return formatString("FootsAway", R.string.FootsAway, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, distance2))}));
            }
            if (distance2 % 5280.0f == 0.0f) {
                arg2 = String.format("%d", new Object[]{Integer.valueOf((int) (distance2 / 5280.0f))});
            } else {
                arg2 = String.format("%.2f", new Object[]{Float.valueOf(distance2 / 5280.0f)});
            }
            return formatString("MilesAway", R.string.MilesAway, arg2);
        } else if (distance < 1000.0f) {
            return formatString("MetersAway2", R.string.MetersAway2, String.format("%d", new Object[]{Integer.valueOf((int) Math.max(1.0f, distance))}));
        } else {
            if (distance % 1000.0f == 0.0f) {
                arg = String.format("%d", new Object[]{Integer.valueOf((int) (distance / 1000.0f))});
            } else {
                arg = String.format("%.2f", new Object[]{Float.valueOf(distance / 1000.0f)});
            }
            return formatString("KMetersAway2", R.string.KMetersAway2, arg);
        }
    }
}
