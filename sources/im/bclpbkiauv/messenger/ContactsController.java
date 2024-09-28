package im.bclpbkiauv.messenger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.exifinterface.media.ExifInterface;
import com.bjz.comm.net.premission.PermissionUtils;
import im.bclpbkiauv.messenger.support.SparseLongArray;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.hui.CharacterParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Marker;

public class ContactsController extends BaseController {
    private static volatile ContactsController[] Instance = new ContactsController[3];
    public static final int PRIVACY_RULES_TYPE_ADDED_BY_PHONE = 7;
    public static final int PRIVACY_RULES_TYPE_CALLS = 2;
    public static final int PRIVACY_RULES_TYPE_COUNT = 9;
    public static final int PRIVACY_RULES_TYPE_FORWARDS = 5;
    public static final int PRIVACY_RULES_TYPE_INVITE = 1;
    public static final int PRIVACY_RULES_TYPE_LASTSEEN = 0;
    public static final int PRIVACY_RULES_TYPE_MOMENT = 8;
    public static final int PRIVACY_RULES_TYPE_P2P = 3;
    public static final int PRIVACY_RULES_TYPE_PHONE = 6;
    public static final int PRIVACY_RULES_TYPE_PHOTO = 4;
    private ArrayList<TLRPC.PrivacyRule> addedByPhonePrivacyRules;
    private ArrayList<TLRPC.PrivacyRule> callPrivacyRules;
    private int completedRequestsCount;
    public ArrayList<TLRPC.Contact> contacts = new ArrayList<>();
    public HashMap<String, Contact> contactsBook = new HashMap<>();
    private boolean contactsBookLoaded;
    public HashMap<String, Contact> contactsBookSPhones = new HashMap<>();
    public HashMap<String, TLRPC.Contact> contactsByPhone = new HashMap<>();
    public HashMap<String, TLRPC.Contact> contactsByShortPhone = new HashMap<>();
    public ConcurrentHashMap<Integer, TLRPC.Contact> contactsDict = new ConcurrentHashMap<>(20, 1.0f, 2);
    public boolean contactsLoaded;
    private boolean contactsSyncInProgress;
    private ArrayList<Integer> delayedContactsUpdate = new ArrayList<>();
    private int deleteAccountTTL;
    private ArrayList<TLRPC.PrivacyRule> forwardsPrivacyRules;
    private ArrayList<TLRPC.PrivacyRule> groupPrivacyRules;
    /* access modifiers changed from: private */
    public boolean ignoreChanges;
    private String inviteLink;
    private String lastContactsVersions = "";
    private ArrayList<TLRPC.PrivacyRule> lastseenPrivacyRules;
    private final Object loadContactsSync = new Object();
    private boolean loadingContacts;
    private int loadingDeleteInfo;
    private int[] loadingPrivacyInfo = new int[9];
    private boolean migratingContacts;
    private ArrayList<TLRPC.PrivacyRule> momentPrivacyRules;
    /* access modifiers changed from: private */
    public final Object observerLock = new Object();
    private ArrayList<TLRPC.PrivacyRule> p2pPrivacyRules;
    public ArrayList<Contact> phoneBookContacts = new ArrayList<>();
    public ArrayList<String> phoneBookSectionsArray = new ArrayList<>();
    public HashMap<String, ArrayList<Object>> phoneBookSectionsDict = new HashMap<>();
    private ArrayList<TLRPC.PrivacyRule> phonePrivacyRules;
    private ArrayList<TLRPC.PrivacyRule> profilePhotoPrivacyRules;
    private String[] projectionNames = {"lookup", "data2", "data3", "data5"};
    private String[] projectionPhones = {"lookup", "data1", "data2", "data3", "display_name", "account_type"};
    private HashMap<String, String> sectionsToReplace = new HashMap<>();
    public ArrayList<String> sortedUsersMutualSectionsArray = new ArrayList<>();
    public ArrayList<String> sortedUsersSectionsArray = new ArrayList<>();
    private Account systemAccount;
    private boolean updatingInviteLink;
    public HashMap<String, ArrayList<TLRPC.Contact>> usersMutualSectionsDict = new HashMap<>();
    public HashMap<String, ArrayList<TLRPC.Contact>> usersSectionsDict = new HashMap<>();

    private class MyContentObserver extends ContentObserver {
        private Runnable checkRunnable = $$Lambda$ContactsController$MyContentObserver$uTs3wObbKE33T6QrHRA1_U3i5s.INSTANCE;

        static /* synthetic */ void lambda$new$0() {
            for (int a = 0; a < 3; a++) {
                if (UserConfig.getInstance(a).isClientActivated()) {
                    ConnectionsManager.getInstance(a).resumeNetworkMaybe();
                }
            }
        }

        public MyContentObserver() {
            super((Handler) null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized (ContactsController.this.observerLock) {
                if (!ContactsController.this.ignoreChanges) {
                    Utilities.globalQueue.cancelRunnable(this.checkRunnable);
                    Utilities.globalQueue.postRunnable(this.checkRunnable, 500);
                }
            }
        }

        public boolean deliverSelfNotifications() {
            return false;
        }
    }

    public static class Contact {
        public int contact_id;
        public String first_name;
        public int imported;
        public boolean isGoodProvider;
        public String key;
        public String last_name;
        public boolean namesFilled;
        public ArrayList<Integer> phoneDeleted = new ArrayList<>(4);
        public ArrayList<String> phoneTypes = new ArrayList<>(4);
        public ArrayList<String> phones = new ArrayList<>(4);
        public String provider;
        public ArrayList<String> shortPhones = new ArrayList<>(4);
        public TLRPC.User user;

        public String getLetter() {
            return getLetter(this.first_name, this.last_name);
        }

        public static String getLetter(String first_name2, String last_name2) {
            if (!TextUtils.isEmpty(first_name2)) {
                return first_name2.substring(0, 1);
            }
            if (!TextUtils.isEmpty(last_name2)) {
                return last_name2.substring(0, 1);
            }
            return "#";
        }
    }

    public static ContactsController getInstance(int num) {
        ContactsController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (ContactsController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    ContactsController[] contactsControllerArr = Instance;
                    ContactsController contactsController = new ContactsController(num);
                    localInstance = contactsController;
                    contactsControllerArr[num] = contactsController;
                }
            }
        }
        return localInstance;
    }

    public ContactsController(int instance) {
        super(instance);
        if (MessagesController.getMainSettings(this.currentAccount).getBoolean("needGetStatuses", false)) {
            reloadContactsStatuses();
        }
        this.sectionsToReplace.put("À", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS);
        this.sectionsToReplace.put("Á", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS);
        this.sectionsToReplace.put("Ä", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS);
        this.sectionsToReplace.put("Ù", "U");
        this.sectionsToReplace.put("Ú", "U");
        this.sectionsToReplace.put("Ü", "U");
        this.sectionsToReplace.put("Ì", "I");
        this.sectionsToReplace.put("Í", "I");
        this.sectionsToReplace.put("Ï", "I");
        this.sectionsToReplace.put("È", ExifInterface.LONGITUDE_EAST);
        this.sectionsToReplace.put("É", ExifInterface.LONGITUDE_EAST);
        this.sectionsToReplace.put("Ê", ExifInterface.LONGITUDE_EAST);
        this.sectionsToReplace.put("Ë", ExifInterface.LONGITUDE_EAST);
        this.sectionsToReplace.put("Ò", "O");
        this.sectionsToReplace.put("Ó", "O");
        this.sectionsToReplace.put("Ö", "O");
        this.sectionsToReplace.put("Ç", "C");
        this.sectionsToReplace.put("Ñ", "N");
        this.sectionsToReplace.put("Ÿ", "Y");
        this.sectionsToReplace.put("Ý", "Y");
        this.sectionsToReplace.put("Ţ", "Y");
        if (instance == 0) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    ContactsController.this.lambda$new$0$ContactsController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$new$0$ContactsController() {
        try {
            if (hasContactsPermission()) {
                ApplicationLoader.applicationContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, new MyContentObserver());
            }
        } catch (Throwable th) {
        }
    }

    public void cleanup() {
        this.contactsBook.clear();
        this.contactsBookSPhones.clear();
        this.phoneBookContacts.clear();
        this.contacts.clear();
        this.contactsDict.clear();
        this.usersSectionsDict.clear();
        this.usersMutualSectionsDict.clear();
        this.sortedUsersSectionsArray.clear();
        this.sortedUsersMutualSectionsArray.clear();
        this.delayedContactsUpdate.clear();
        this.contactsByPhone.clear();
        this.contactsByShortPhone.clear();
        this.phoneBookSectionsDict.clear();
        this.phoneBookSectionsArray.clear();
        this.loadingContacts = false;
        this.contactsSyncInProgress = false;
        this.contactsLoaded = false;
        this.contactsBookLoaded = false;
        this.lastContactsVersions = "";
        this.loadingDeleteInfo = 0;
        this.deleteAccountTTL = 0;
        int a = 0;
        while (true) {
            int[] iArr = this.loadingPrivacyInfo;
            if (a < iArr.length) {
                iArr[a] = 0;
                a++;
            } else {
                this.lastseenPrivacyRules = null;
                this.groupPrivacyRules = null;
                this.callPrivacyRules = null;
                this.p2pPrivacyRules = null;
                this.profilePhotoPrivacyRules = null;
                this.forwardsPrivacyRules = null;
                this.phonePrivacyRules = null;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        ContactsController.this.lambda$cleanup$1$ContactsController();
                    }
                });
                return;
            }
        }
    }

    public /* synthetic */ void lambda$cleanup$1$ContactsController() {
        this.migratingContacts = false;
        this.completedRequestsCount = 0;
    }

    public void checkInviteText() {
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        this.inviteLink = preferences.getString("invitelink", (String) null);
        int time = preferences.getInt("invitelinktime", 0);
        if (this.updatingInviteLink) {
            return;
        }
        if (this.inviteLink == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) time)) >= 86400) {
            this.updatingInviteLink = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_help_getInviteText(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ContactsController.this.lambda$checkInviteText$3$ContactsController(tLObject, tL_error);
                }
            }, 2);
        }
    }

    public /* synthetic */ void lambda$checkInviteText$3$ContactsController(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_help_inviteText res = (TLRPC.TL_help_inviteText) response;
            if (res.message.length() != 0) {
                AndroidUtilities.runOnUIThread(new Runnable(res) {
                    private final /* synthetic */ TLRPC.TL_help_inviteText f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ContactsController.this.lambda$null$2$ContactsController(this.f$1);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$null$2$ContactsController(TLRPC.TL_help_inviteText res) {
        this.updatingInviteLink = false;
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        String str = res.message;
        this.inviteLink = str;
        editor.putString("invitelink", str);
        editor.putInt("invitelinktime", (int) (System.currentTimeMillis() / 1000));
        editor.commit();
    }

    public String getInviteText(int contacts2) {
        String link = this.inviteLink;
        if (link == null) {
            link = "https://m12345.com/dl";
        }
        if (contacts2 <= 1) {
            return LocaleController.formatString("InviteText2", R.string.InviteText2, link);
        }
        try {
            return String.format(LocaleController.getPluralString("InviteTextNum", contacts2), new Object[]{Integer.valueOf(contacts2), link});
        } catch (Exception e) {
            return LocaleController.formatString("InviteText2", R.string.InviteText2, link);
        }
    }

    public void checkAppAccount() {
        AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accounts = am.getAccountsByType(BuildConfig.APPLICATION_ID);
            this.systemAccount = null;
            for (int a = 0; a < accounts.length; a++) {
                Account acc = accounts[a];
                boolean found = false;
                int b = 0;
                while (true) {
                    if (b >= 3) {
                        break;
                    }
                    TLRPC.User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        String str = acc.name;
                        if (str.equals("" + user.id)) {
                            if (b == this.currentAccount) {
                                this.systemAccount = acc;
                            }
                            found = true;
                        }
                    }
                    b++;
                }
                if (!found) {
                    try {
                        am.removeAccount(accounts[a], (AccountManagerCallback) null, (Handler) null);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Throwable th) {
        }
        if (getUserConfig().isClientActivated()) {
            readContacts();
            if (this.systemAccount == null) {
                try {
                    Account account = new Account("" + getUserConfig().getClientUserId(), BuildConfig.APPLICATION_ID);
                    this.systemAccount = account;
                    am.addAccountExplicitly(account, "", (Bundle) null);
                } catch (Exception e2) {
                }
            }
        }
    }

    public void deleteUnknownAppAccounts() {
        try {
            this.systemAccount = null;
            AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
            Account[] accounts = am.getAccountsByType(BuildConfig.APPLICATION_ID);
            for (int a = 0; a < accounts.length; a++) {
                Account acc = accounts[a];
                boolean found = false;
                int b = 0;
                while (true) {
                    if (b >= 3) {
                        break;
                    }
                    TLRPC.User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        String str = acc.name;
                        if (str.equals("" + user.id)) {
                            found = true;
                            break;
                        }
                    }
                    b++;
                }
                if (!found) {
                    try {
                        am.removeAccount(accounts[a], (AccountManagerCallback) null, (Handler) null);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void checkContacts() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                ContactsController.this.lambda$checkContacts$4$ContactsController();
            }
        });
    }

    public /* synthetic */ void lambda$checkContacts$4$ContactsController() {
        if (checkContactsInternal()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("detected contacts change");
            }
            performSyncPhoneBook(getContactsCopy(this.contactsBook), true, false, true, false, true, false);
        }
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                ContactsController.this.lambda$forceImportContacts$5$ContactsController();
            }
        });
    }

    public /* synthetic */ void lambda$forceImportContacts$5$ContactsController() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("force import contacts");
        }
        performSyncPhoneBook(new HashMap(), true, true, true, true, false, false);
    }

    public void syncPhoneBookByAlert(HashMap<String, Contact> contacts2, boolean first, boolean schedule, boolean cancel) {
        Utilities.globalQueue.postRunnable(new Runnable(contacts2, first, schedule, cancel) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ContactsController.this.lambda$syncPhoneBookByAlert$6$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$syncPhoneBookByAlert$6$ContactsController(HashMap contacts2, boolean first, boolean schedule, boolean cancel) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("sync contacts by alert");
        }
        performSyncPhoneBook(contacts2, true, first, schedule, false, false, cancel);
    }

    public void deleteAllContacts(Runnable runnable) {
        resetImportedContacts();
        TLRPC.TL_contacts_deleteContacts req = new TLRPC.TL_contacts_deleteContacts();
        int size = this.contacts.size();
        for (int a = 0; a < size; a++) {
            req.id.add(getMessagesController().getInputUser(this.contacts.get(a).user_id));
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate(runnable) {
            private final /* synthetic */ Runnable f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ContactsController.this.lambda$deleteAllContacts$8$ContactsController(this.f$1, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$deleteAllContacts$8$ContactsController(Runnable runnable, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            this.contactsBookSPhones.clear();
            this.contactsBook.clear();
            this.completedRequestsCount = 0;
            this.migratingContacts = false;
            this.contactsSyncInProgress = false;
            this.contactsLoaded = false;
            this.loadingContacts = false;
            this.contactsBookLoaded = false;
            this.lastContactsVersions = "";
            AndroidUtilities.runOnUIThread(new Runnable(runnable) {
                private final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContactsController.this.lambda$null$7$ContactsController(this.f$1);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public /* synthetic */ void lambda$null$7$ContactsController(Runnable runnable) {
        AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            Account[] accounts = am.getAccountsByType(BuildConfig.APPLICATION_ID);
            this.systemAccount = null;
            for (Account acc : accounts) {
                int b = 0;
                while (true) {
                    if (b >= 3) {
                        break;
                    }
                    TLRPC.User user = UserConfig.getInstance(b).getCurrentUser();
                    if (user != null) {
                        if (acc.name.equals("" + user.id)) {
                            am.removeAccount(acc, (AccountManagerCallback) null, (Handler) null);
                            break;
                        }
                    }
                    b++;
                }
            }
        } catch (Throwable th) {
        }
        try {
            Account account = new Account("" + getUserConfig().getClientUserId(), BuildConfig.APPLICATION_ID);
            this.systemAccount = account;
            am.addAccountExplicitly(account, "", (Bundle) null);
        } catch (Exception e) {
        }
        getMessagesStorage().putCachedPhoneBook(new HashMap(), false, true);
        getMessagesStorage().putContacts(new ArrayList(), true);
        this.phoneBookContacts.clear();
        this.contacts.clear();
        this.contactsDict.clear();
        this.usersSectionsDict.clear();
        this.usersMutualSectionsDict.clear();
        this.sortedUsersSectionsArray.clear();
        this.phoneBookSectionsDict.clear();
        this.phoneBookSectionsArray.clear();
        this.delayedContactsUpdate.clear();
        this.sortedUsersMutualSectionsArray.clear();
        this.contactsByPhone.clear();
        this.contactsByShortPhone.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        loadContacts(false, 0);
        runnable.run();
    }

    public void resetImportedContacts() {
        getConnectionsManager().sendRequest(new TLRPC.TL_contacts_resetSaved(), $$Lambda$ContactsController$0TvfSvE0QVCnGh5h0e48q215VdY.INSTANCE);
    }

    static /* synthetic */ void lambda$resetImportedContacts$9(TLObject response, TLRPC.TL_error error) {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0052, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0053, code lost:
        if (r2 != null) goto L_0x0055;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x005d, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkContactsInternal() {
        /*
            r9 = this;
            java.lang.String r0 = "version"
            r1 = 0
            boolean r2 = r9.hasContactsPermission()     // Catch:{ Exception -> 0x0069 }
            if (r2 != 0) goto L_0x000c
            r0 = 0
            return r0
        L_0x000c:
            android.content.Context r2 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0069 }
            android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ Exception -> 0x0069 }
            android.net.Uri r4 = android.provider.ContactsContract.RawContacts.CONTENT_URI     // Catch:{ Exception -> 0x0064 }
            java.lang.String[] r5 = new java.lang.String[]{r0}     // Catch:{ Exception -> 0x0064 }
            r6 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r2 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0064 }
            if (r2 == 0) goto L_0x005e
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0050 }
            r4.<init>()     // Catch:{ all -> 0x0050 }
        L_0x0026:
            boolean r5 = r2.moveToNext()     // Catch:{ all -> 0x0050 }
            if (r5 == 0) goto L_0x0038
            int r5 = r2.getColumnIndex(r0)     // Catch:{ all -> 0x0050 }
            java.lang.String r5 = r2.getString(r5)     // Catch:{ all -> 0x0050 }
            r4.append(r5)     // Catch:{ all -> 0x0050 }
            goto L_0x0026
        L_0x0038:
            java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x0050 }
            java.lang.String r5 = r9.lastContactsVersions     // Catch:{ all -> 0x0050 }
            int r5 = r5.length()     // Catch:{ all -> 0x0050 }
            if (r5 == 0) goto L_0x004d
            java.lang.String r5 = r9.lastContactsVersions     // Catch:{ all -> 0x0050 }
            boolean r5 = r5.equals(r0)     // Catch:{ all -> 0x0050 }
            if (r5 != 0) goto L_0x004d
            r1 = 1
        L_0x004d:
            r9.lastContactsVersions = r0     // Catch:{ all -> 0x0050 }
            goto L_0x005e
        L_0x0050:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0052 }
        L_0x0052:
            r4 = move-exception
            if (r2 == 0) goto L_0x005d
            r2.close()     // Catch:{ all -> 0x0059 }
            goto L_0x005d
        L_0x0059:
            r5 = move-exception
            r0.addSuppressed(r5)     // Catch:{ Exception -> 0x0064 }
        L_0x005d:
            throw r4     // Catch:{ Exception -> 0x0064 }
        L_0x005e:
            if (r2 == 0) goto L_0x0063
            r2.close()     // Catch:{ Exception -> 0x0064 }
        L_0x0063:
            goto L_0x0068
        L_0x0064:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0069 }
        L_0x0068:
            goto L_0x006d
        L_0x0069:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x006d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ContactsController.checkContactsInternal():boolean");
    }

    public void readContacts() {
        synchronized (this.loadContactsSync) {
            if (!this.loadingContacts) {
                this.loadingContacts = true;
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public final void run() {
                        ContactsController.this.lambda$readContacts$10$ContactsController();
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$readContacts$10$ContactsController() {
        if (!this.contacts.isEmpty() || this.contactsLoaded) {
            synchronized (this.loadContactsSync) {
                this.loadingContacts = false;
            }
            return;
        }
        loadContacts(true, 0);
    }

    public void syncRemoteContacts() {
        synchronized (this.loadContactsSync) {
            if (!this.loadingContacts) {
                this.loadingContacts = true;
                loadContacts(false, getContactsHash(this.contacts));
            }
        }
    }

    private boolean isNotValidNameString(String src) {
        if (TextUtils.isEmpty(src)) {
            return true;
        }
        int count = 0;
        int len = src.length();
        for (int a = 0; a < len; a++) {
            char c = src.charAt(a);
            if (c >= '0' && c <= '9') {
                count++;
            }
        }
        if (count > 3) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:194:0x038b A[Catch:{ all -> 0x039e }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x0390 A[SYNTHETIC, Splitter:B:196:0x0390] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0396  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0398  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.HashMap<java.lang.String, im.bclpbkiauv.messenger.ContactsController.Contact> readContactsFromPhoneBook() {
        /*
            r32 = this;
            r1 = r32
            im.bclpbkiauv.messenger.UserConfig r0 = r32.getUserConfig()
            boolean r0 = r0.syncContacts
            if (r0 != 0) goto L_0x0019
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0013
            java.lang.String r0 = "contacts sync disabled"
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x0013:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            return r0
        L_0x0019:
            boolean r0 = r32.hasContactsPermission()
            if (r0 != 0) goto L_0x002e
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0028
            java.lang.String r0 = "app has no contacts permissions"
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x0028:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            return r0
        L_0x002e:
            r2 = 0
            r3 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0384 }
            r0.<init>()     // Catch:{ all -> 0x0384 }
            r4 = r0
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0384 }
            android.content.ContentResolver r5 = r0.getContentResolver()     // Catch:{ all -> 0x0384 }
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ all -> 0x0384 }
            r0.<init>()     // Catch:{ all -> 0x0384 }
            r11 = r0
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0384 }
            r0.<init>()     // Catch:{ all -> 0x0384 }
            r12 = r0
            android.net.Uri r6 = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI     // Catch:{ all -> 0x0384 }
            java.lang.String[] r7 = r1.projectionPhones     // Catch:{ all -> 0x0384 }
            r8 = 0
            r9 = 0
            r10 = 0
            android.database.Cursor r0 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0384 }
            r2 = r0
            r0 = 1
            r8 = 0
            java.lang.String r9 = ""
            r10 = 1
            if (r2 == 0) goto L_0x0241
            int r13 = r2.getCount()     // Catch:{ all -> 0x0380 }
            if (r13 <= 0) goto L_0x0232
            if (r3 != 0) goto L_0x0069
            java.util.HashMap r14 = new java.util.HashMap     // Catch:{ all -> 0x0384 }
            r14.<init>(r13)     // Catch:{ all -> 0x0384 }
            r3 = r14
        L_0x0069:
            boolean r14 = r2.moveToNext()     // Catch:{ all -> 0x0380 }
            if (r14 == 0) goto L_0x022a
            java.lang.String r14 = r2.getString(r10)     // Catch:{ all -> 0x0380 }
            r15 = 5
            java.lang.String r15 = r2.getString(r15)     // Catch:{ all -> 0x0380 }
            if (r15 != 0) goto L_0x007b
            r15 = r9
        L_0x007b:
            java.lang.String r6 = ".sim"
            int r6 = r15.indexOf(r6)     // Catch:{ all -> 0x0380 }
            if (r6 == 0) goto L_0x0085
            r6 = 1
            goto L_0x0086
        L_0x0085:
            r6 = 0
        L_0x0086:
            boolean r17 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x0380 }
            if (r17 == 0) goto L_0x0090
            r21 = r13
            goto L_0x00fe
        L_0x0090:
            java.lang.String r17 = im.bclpbkiauv.phoneformat.PhoneFormat.stripExceptNumbers(r14, r10)     // Catch:{ all -> 0x0380 }
            r14 = r17
            boolean r17 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x0380 }
            if (r17 == 0) goto L_0x009f
            r21 = r13
            goto L_0x00fe
        L_0x009f:
            r17 = r14
            java.lang.String r7 = "+"
            boolean r7 = r14.startsWith(r7)     // Catch:{ all -> 0x0380 }
            if (r7 == 0) goto L_0x00b0
            java.lang.String r7 = r14.substring(r10)     // Catch:{ all -> 0x0384 }
            r17 = r7
            goto L_0x00b2
        L_0x00b0:
            r7 = r17
        L_0x00b2:
            java.lang.String r17 = r2.getString(r8)     // Catch:{ all -> 0x0380 }
            r19 = r17
            r4.setLength(r8)     // Catch:{ all -> 0x0380 }
            r10 = r19
            android.database.DatabaseUtils.appendEscapedSQLString(r4, r10)     // Catch:{ all -> 0x0380 }
            java.lang.String r19 = r4.toString()     // Catch:{ all -> 0x0380 }
            r20 = r19
            java.lang.Object r19 = r11.get(r7)     // Catch:{ all -> 0x0380 }
            im.bclpbkiauv.messenger.ContactsController$Contact r19 = (im.bclpbkiauv.messenger.ContactsController.Contact) r19     // Catch:{ all -> 0x0380 }
            r21 = r19
            r8 = r21
            if (r8 == 0) goto L_0x0104
            r21 = r13
            boolean r13 = r8.isGoodProvider     // Catch:{ all -> 0x0384 }
            if (r13 != 0) goto L_0x00fc
            java.lang.String r13 = r8.provider     // Catch:{ all -> 0x0384 }
            boolean r13 = r15.equals(r13)     // Catch:{ all -> 0x0384 }
            if (r13 != 0) goto L_0x00fc
            r13 = 0
            r4.setLength(r13)     // Catch:{ all -> 0x0384 }
            java.lang.String r13 = r8.key     // Catch:{ all -> 0x0384 }
            android.database.DatabaseUtils.appendEscapedSQLString(r4, r13)     // Catch:{ all -> 0x0384 }
            java.lang.String r13 = r4.toString()     // Catch:{ all -> 0x0384 }
            r12.remove(r13)     // Catch:{ all -> 0x0384 }
            r13 = r20
            r12.add(r13)     // Catch:{ all -> 0x0384 }
            r8.key = r10     // Catch:{ all -> 0x0384 }
            r8.isGoodProvider = r6     // Catch:{ all -> 0x0384 }
            r8.provider = r15     // Catch:{ all -> 0x0384 }
            goto L_0x00fe
        L_0x00fc:
            r13 = r20
        L_0x00fe:
            r13 = r21
            r8 = 0
            r10 = 1
            goto L_0x0069
        L_0x0104:
            r21 = r13
            r13 = r20
            boolean r20 = r12.contains(r13)     // Catch:{ all -> 0x0380 }
            if (r20 != 0) goto L_0x0111
            r12.add(r13)     // Catch:{ all -> 0x0384 }
        L_0x0111:
            r20 = r4
            r4 = 2
            int r22 = r2.getInt(r4)     // Catch:{ all -> 0x0380 }
            r4 = r22
            java.lang.Object r22 = r3.get(r10)     // Catch:{ all -> 0x0380 }
            im.bclpbkiauv.messenger.ContactsController$Contact r22 = (im.bclpbkiauv.messenger.ContactsController.Contact) r22     // Catch:{ all -> 0x0380 }
            if (r22 != 0) goto L_0x0185
            im.bclpbkiauv.messenger.ContactsController$Contact r23 = new im.bclpbkiauv.messenger.ContactsController$Contact     // Catch:{ all -> 0x0380 }
            r23.<init>()     // Catch:{ all -> 0x0380 }
            r22 = r23
            r23 = r8
            r8 = 4
            java.lang.String r8 = r2.getString(r8)     // Catch:{ all -> 0x0380 }
            if (r8 != 0) goto L_0x0134
            r8 = r9
            goto L_0x013a
        L_0x0134:
            java.lang.String r24 = r8.trim()     // Catch:{ all -> 0x0380 }
            r8 = r24
        L_0x013a:
            boolean r24 = r1.isNotValidNameString(r8)     // Catch:{ all -> 0x0380 }
            if (r24 == 0) goto L_0x014b
            r24 = r13
            r13 = r22
            r13.first_name = r8     // Catch:{ all -> 0x0384 }
            r13.last_name = r9     // Catch:{ all -> 0x0384 }
            r25 = r5
            goto L_0x0176
        L_0x014b:
            r24 = r13
            r13 = r22
            r25 = r5
            r5 = 32
            int r5 = r8.lastIndexOf(r5)     // Catch:{ all -> 0x0380 }
            r1 = -1
            if (r5 == r1) goto L_0x0172
            r1 = 0
            java.lang.String r22 = r8.substring(r1, r5)     // Catch:{ all -> 0x0380 }
            java.lang.String r1 = r22.trim()     // Catch:{ all -> 0x0380 }
            r13.first_name = r1     // Catch:{ all -> 0x0380 }
            int r1 = r5 + 1
            java.lang.String r1 = r8.substring(r1)     // Catch:{ all -> 0x0380 }
            java.lang.String r1 = r1.trim()     // Catch:{ all -> 0x0380 }
            r13.last_name = r1     // Catch:{ all -> 0x0380 }
            goto L_0x0176
        L_0x0172:
            r13.first_name = r8     // Catch:{ all -> 0x0380 }
            r13.last_name = r9     // Catch:{ all -> 0x0380 }
        L_0x0176:
            r13.provider = r15     // Catch:{ all -> 0x0380 }
            r13.isGoodProvider = r6     // Catch:{ all -> 0x0380 }
            r13.key = r10     // Catch:{ all -> 0x0380 }
            int r1 = r0 + 1
            r13.contact_id = r0     // Catch:{ all -> 0x0380 }
            r3.put(r10, r13)     // Catch:{ all -> 0x0380 }
            r0 = r1
            goto L_0x018d
        L_0x0185:
            r25 = r5
            r23 = r8
            r24 = r13
            r13 = r22
        L_0x018d:
            java.util.ArrayList<java.lang.String> r1 = r13.shortPhones     // Catch:{ all -> 0x0380 }
            r1.add(r7)     // Catch:{ all -> 0x0380 }
            java.util.ArrayList<java.lang.String> r1 = r13.phones     // Catch:{ all -> 0x0380 }
            r1.add(r14)     // Catch:{ all -> 0x0380 }
            java.util.ArrayList<java.lang.Integer> r1 = r13.phoneDeleted     // Catch:{ all -> 0x0380 }
            r5 = 0
            java.lang.Integer r8 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0380 }
            r1.add(r8)     // Catch:{ all -> 0x0380 }
            java.lang.String r5 = "PhoneMobile"
            if (r4 != 0) goto L_0x01c2
            r8 = 3
            java.lang.String r22 = r2.getString(r8)     // Catch:{ all -> 0x0380 }
            r8 = r22
            java.util.ArrayList<java.lang.String> r1 = r13.phoneTypes     // Catch:{ all -> 0x0380 }
            if (r8 == 0) goto L_0x01b4
            r26 = r0
            r0 = r8
            goto L_0x01bd
        L_0x01b4:
            r26 = r0
            r0 = 2131693035(0x7f0f0deb, float:1.9015187E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r0)     // Catch:{ all -> 0x0380 }
        L_0x01bd:
            r1.add(r0)     // Catch:{ all -> 0x0380 }
            goto L_0x0219
        L_0x01c2:
            r26 = r0
            r1 = 1
            if (r4 != r1) goto L_0x01d6
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0380 }
            java.lang.String r1 = "PhoneHome"
            r5 = 2131693033(0x7f0f0de9, float:1.9015183E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x0380 }
            r0.add(r1)     // Catch:{ all -> 0x0380 }
            goto L_0x0219
        L_0x01d6:
            r1 = 2
            if (r4 != r1) goto L_0x01e6
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0380 }
            r1 = 2131693035(0x7f0f0deb, float:1.9015187E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r1)     // Catch:{ all -> 0x0380 }
            r0.add(r1)     // Catch:{ all -> 0x0380 }
            goto L_0x0219
        L_0x01e6:
            r1 = 3
            if (r4 != r1) goto L_0x01f8
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0380 }
            java.lang.String r1 = "PhoneWork"
            r5 = 2131693045(0x7f0f0df5, float:1.9015207E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x0380 }
            r0.add(r1)     // Catch:{ all -> 0x0380 }
            goto L_0x0219
        L_0x01f8:
            r0 = 12
            if (r4 != r0) goto L_0x020b
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0380 }
            java.lang.String r1 = "PhoneMain"
            r5 = 2131693034(0x7f0f0dea, float:1.9015185E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x0380 }
            r0.add(r1)     // Catch:{ all -> 0x0380 }
            goto L_0x0219
        L_0x020b:
            java.util.ArrayList<java.lang.String> r0 = r13.phoneTypes     // Catch:{ all -> 0x0380 }
            java.lang.String r1 = "PhoneOther"
            r5 = 2131693044(0x7f0f0df4, float:1.9015205E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r5)     // Catch:{ all -> 0x0380 }
            r0.add(r1)     // Catch:{ all -> 0x0380 }
        L_0x0219:
            r11.put(r7, r13)     // Catch:{ all -> 0x0380 }
            r1 = r32
            r4 = r20
            r13 = r21
            r5 = r25
            r0 = r26
            r8 = 0
            r10 = 1
            goto L_0x0069
        L_0x022a:
            r20 = r4
            r25 = r5
            r21 = r13
            r1 = r0
            goto L_0x0239
        L_0x0232:
            r20 = r4
            r25 = r5
            r21 = r13
            r1 = r0
        L_0x0239:
            r2.close()     // Catch:{ Exception -> 0x023d }
            goto L_0x023e
        L_0x023d:
            r0 = move-exception
        L_0x023e:
            r0 = 0
            r2 = r0
            goto L_0x0246
        L_0x0241:
            r20 = r4
            r25 = r5
            r1 = r0
        L_0x0246:
            java.lang.String r0 = ","
            java.lang.String r0 = android.text.TextUtils.join(r0, r12)     // Catch:{ all -> 0x0380 }
            r4 = r0
            android.net.Uri r27 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ all -> 0x0380 }
            r6 = r32
            java.lang.String[] r0 = r6.projectionNames     // Catch:{ all -> 0x037e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x037e }
            r5.<init>()     // Catch:{ all -> 0x037e }
            java.lang.String r7 = "lookup IN ("
            r5.append(r7)     // Catch:{ all -> 0x037e }
            r5.append(r4)     // Catch:{ all -> 0x037e }
            java.lang.String r7 = ") AND "
            r5.append(r7)     // Catch:{ all -> 0x037e }
            java.lang.String r7 = "mimetype"
            r5.append(r7)     // Catch:{ all -> 0x037e }
            java.lang.String r7 = " = '"
            r5.append(r7)     // Catch:{ all -> 0x037e }
            java.lang.String r7 = "vnd.android.cursor.item/name"
            r5.append(r7)     // Catch:{ all -> 0x037e }
            java.lang.String r7 = "'"
            r5.append(r7)     // Catch:{ all -> 0x037e }
            java.lang.String r29 = r5.toString()     // Catch:{ all -> 0x037e }
            r30 = 0
            r31 = 0
            r5 = r25
            r26 = r5
            r28 = r0
            android.database.Cursor r0 = r26.query(r27, r28, r29, r30, r31)     // Catch:{ all -> 0x037e }
            r2 = r0
            if (r2 == 0) goto L_0x0370
        L_0x028f:
            boolean r0 = r2.moveToNext()     // Catch:{ all -> 0x037e }
            if (r0 == 0) goto L_0x036a
            r7 = 0
            java.lang.String r0 = r2.getString(r7)     // Catch:{ all -> 0x037e }
            r8 = 1
            java.lang.String r10 = r2.getString(r8)     // Catch:{ all -> 0x037e }
            r8 = r10
            r10 = 2
            java.lang.String r13 = r2.getString(r10)     // Catch:{ all -> 0x037e }
            r14 = 3
            java.lang.String r15 = r2.getString(r14)     // Catch:{ all -> 0x037e }
            java.lang.Object r16 = r3.get(r0)     // Catch:{ all -> 0x037e }
            im.bclpbkiauv.messenger.ContactsController$Contact r16 = (im.bclpbkiauv.messenger.ContactsController.Contact) r16     // Catch:{ all -> 0x037e }
            r18 = r16
            r7 = r18
            if (r7 == 0) goto L_0x0365
            boolean r10 = r7.namesFilled     // Catch:{ all -> 0x037e }
            if (r10 != 0) goto L_0x0365
            boolean r10 = r7.isGoodProvider     // Catch:{ all -> 0x037e }
            java.lang.String r14 = " "
            if (r10 == 0) goto L_0x02fe
            if (r8 == 0) goto L_0x02c5
            r7.first_name = r8     // Catch:{ all -> 0x037e }
            goto L_0x02c7
        L_0x02c5:
            r7.first_name = r9     // Catch:{ all -> 0x037e }
        L_0x02c7:
            if (r13 == 0) goto L_0x02cc
            r7.last_name = r13     // Catch:{ all -> 0x037e }
            goto L_0x02ce
        L_0x02cc:
            r7.last_name = r9     // Catch:{ all -> 0x037e }
        L_0x02ce:
            boolean r10 = android.text.TextUtils.isEmpty(r15)     // Catch:{ all -> 0x037e }
            if (r10 != 0) goto L_0x02fb
            java.lang.String r10 = r7.first_name     // Catch:{ all -> 0x037e }
            boolean r10 = android.text.TextUtils.isEmpty(r10)     // Catch:{ all -> 0x037e }
            if (r10 != 0) goto L_0x02f6
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x037e }
            r10.<init>()     // Catch:{ all -> 0x037e }
            r21 = r0
            java.lang.String r0 = r7.first_name     // Catch:{ all -> 0x037e }
            r10.append(r0)     // Catch:{ all -> 0x037e }
            r10.append(r14)     // Catch:{ all -> 0x037e }
            r10.append(r15)     // Catch:{ all -> 0x037e }
            java.lang.String r0 = r10.toString()     // Catch:{ all -> 0x037e }
            r7.first_name = r0     // Catch:{ all -> 0x037e }
            goto L_0x0361
        L_0x02f6:
            r21 = r0
            r7.first_name = r15     // Catch:{ all -> 0x037e }
            goto L_0x0361
        L_0x02fb:
            r21 = r0
            goto L_0x0361
        L_0x02fe:
            r21 = r0
            boolean r0 = r6.isNotValidNameString(r8)     // Catch:{ all -> 0x037e }
            if (r0 != 0) goto L_0x0316
            java.lang.String r0 = r7.first_name     // Catch:{ all -> 0x037e }
            boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x037e }
            if (r0 != 0) goto L_0x032c
            java.lang.String r0 = r7.first_name     // Catch:{ all -> 0x037e }
            boolean r0 = r8.contains(r0)     // Catch:{ all -> 0x037e }
            if (r0 != 0) goto L_0x032c
        L_0x0316:
            boolean r0 = r6.isNotValidNameString(r13)     // Catch:{ all -> 0x037e }
            if (r0 != 0) goto L_0x0361
            java.lang.String r0 = r7.last_name     // Catch:{ all -> 0x037e }
            boolean r0 = r0.contains(r13)     // Catch:{ all -> 0x037e }
            if (r0 != 0) goto L_0x032c
            java.lang.String r0 = r7.last_name     // Catch:{ all -> 0x037e }
            boolean r0 = r8.contains(r0)     // Catch:{ all -> 0x037e }
            if (r0 == 0) goto L_0x0361
        L_0x032c:
            if (r8 == 0) goto L_0x0331
            r7.first_name = r8     // Catch:{ all -> 0x037e }
            goto L_0x0333
        L_0x0331:
            r7.first_name = r9     // Catch:{ all -> 0x037e }
        L_0x0333:
            boolean r0 = android.text.TextUtils.isEmpty(r15)     // Catch:{ all -> 0x037e }
            if (r0 != 0) goto L_0x035a
            java.lang.String r0 = r7.first_name     // Catch:{ all -> 0x037e }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x037e }
            if (r0 != 0) goto L_0x0358
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x037e }
            r0.<init>()     // Catch:{ all -> 0x037e }
            java.lang.String r10 = r7.first_name     // Catch:{ all -> 0x037e }
            r0.append(r10)     // Catch:{ all -> 0x037e }
            r0.append(r14)     // Catch:{ all -> 0x037e }
            r0.append(r15)     // Catch:{ all -> 0x037e }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x037e }
            r7.first_name = r0     // Catch:{ all -> 0x037e }
            goto L_0x035a
        L_0x0358:
            r7.first_name = r15     // Catch:{ all -> 0x037e }
        L_0x035a:
            if (r13 == 0) goto L_0x035f
            r7.last_name = r13     // Catch:{ all -> 0x037e }
            goto L_0x0361
        L_0x035f:
            r7.last_name = r9     // Catch:{ all -> 0x037e }
        L_0x0361:
            r10 = 1
            r7.namesFilled = r10     // Catch:{ all -> 0x037e }
            goto L_0x0368
        L_0x0365:
            r21 = r0
            r10 = 1
        L_0x0368:
            goto L_0x028f
        L_0x036a:
            r2.close()     // Catch:{ Exception -> 0x036e }
            goto L_0x036f
        L_0x036e:
            r0 = move-exception
        L_0x036f:
            r2 = 0
        L_0x0370:
            if (r2 == 0) goto L_0x037d
            r2.close()     // Catch:{ Exception -> 0x0376 }
            goto L_0x037d
        L_0x0376:
            r0 = move-exception
            r1 = r0
            r0 = r1
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0394
        L_0x037d:
            goto L_0x0394
        L_0x037e:
            r0 = move-exception
            goto L_0x0386
        L_0x0380:
            r0 = move-exception
            r6 = r32
            goto L_0x0386
        L_0x0384:
            r0 = move-exception
            r6 = r1
        L_0x0386:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x039e }
            if (r3 == 0) goto L_0x038e
            r3.clear()     // Catch:{ all -> 0x039e }
        L_0x038e:
            if (r2 == 0) goto L_0x037d
            r2.close()     // Catch:{ Exception -> 0x0376 }
            goto L_0x037d
        L_0x0394:
            if (r3 == 0) goto L_0x0398
            r0 = r3
            goto L_0x039d
        L_0x0398:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
        L_0x039d:
            return r0
        L_0x039e:
            r0 = move-exception
            r1 = r0
            if (r2 == 0) goto L_0x03ad
            r2.close()     // Catch:{ Exception -> 0x03a6 }
            goto L_0x03ad
        L_0x03a6:
            r0 = move-exception
            r4 = r0
            r0 = r4
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x03ae
        L_0x03ad:
        L_0x03ae:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ContactsController.readContactsFromPhoneBook():java.util.HashMap");
    }

    public HashMap<String, Contact> getContactsCopy(HashMap<String, Contact> original) {
        HashMap<String, Contact> ret = new HashMap<>();
        for (Map.Entry<String, Contact> entry : original.entrySet()) {
            Contact copyContact = new Contact();
            Contact originalContact = entry.getValue();
            copyContact.phoneDeleted.addAll(originalContact.phoneDeleted);
            copyContact.phones.addAll(originalContact.phones);
            copyContact.phoneTypes.addAll(originalContact.phoneTypes);
            copyContact.shortPhones.addAll(originalContact.shortPhones);
            copyContact.first_name = originalContact.first_name;
            copyContact.last_name = originalContact.last_name;
            copyContact.contact_id = originalContact.contact_id;
            copyContact.key = originalContact.key;
            ret.put(copyContact.key, copyContact);
        }
        return ret;
    }

    /* access modifiers changed from: protected */
    public void migratePhoneBookToV7(SparseArray<Contact> contactHashMap) {
        Utilities.globalQueue.postRunnable(new Runnable(contactHashMap) {
            private final /* synthetic */ SparseArray f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ContactsController.this.lambda$migratePhoneBookToV7$11$ContactsController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$migratePhoneBookToV7$11$ContactsController(SparseArray contactHashMap) {
        if (!this.migratingContacts) {
            this.migratingContacts = true;
            HashMap<String, Contact> migratedMap = new HashMap<>();
            HashMap<String, Contact> contactsMap = readContactsFromPhoneBook();
            HashMap<String, String> contactsBookShort = new HashMap<>();
            for (Map.Entry<String, Contact> entry : contactsMap.entrySet()) {
                Contact value = entry.getValue();
                for (int a = 0; a < value.shortPhones.size(); a++) {
                    contactsBookShort.put(value.shortPhones.get(a), value.key);
                }
            }
            for (int b = 0; b < contactHashMap.size(); b++) {
                Contact value2 = (Contact) contactHashMap.valueAt(b);
                int a2 = 0;
                while (true) {
                    if (a2 >= value2.shortPhones.size()) {
                        break;
                    }
                    String key = contactsBookShort.get(value2.shortPhones.get(a2));
                    if (key != null) {
                        value2.key = key;
                        migratedMap.put(key, value2);
                        break;
                    }
                    a2++;
                }
            }
            if (BuildVars.LOGS_ENABLED != 0) {
                FileLog.d("migrated contacts " + migratedMap.size() + " of " + contactHashMap.size());
            }
            getMessagesStorage().putCachedPhoneBook(migratedMap, true, false);
        }
    }

    public void checkPhonebookUsers() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                ContactsController.this.lambda$checkPhonebookUsers$14$ContactsController();
            }
        });
    }

    public /* synthetic */ void lambda$checkPhonebookUsers$14$ContactsController() {
        HashMap<String, Contact> contactsMap = readContactsFromPhoneBook();
        ArrayList<TLRPC.TL_inputPhoneContact> toImport = new ArrayList<>();
        HashMap<String, TLRPC.TL_inputPhoneContact> inputPhoneContactsMap = new HashMap<>();
        for (Map.Entry<String, Contact> pair : contactsMap.entrySet()) {
            Contact value = pair.getValue();
            String key = pair.getKey();
            for (int a = 0; a < value.phones.size(); a++) {
                TLRPC.TL_inputPhoneContact imp = new TLRPC.TL_inputPhoneContact();
                imp.client_id = (long) value.contact_id;
                imp.client_id |= ((long) a) << 32;
                imp.first_name = value.first_name;
                imp.last_name = value.last_name;
                imp.phone = value.phones.get(a);
                toImport.add(imp);
                inputPhoneContactsMap.put(value.phones.get(a), imp);
            }
        }
        this.completedRequestsCount = 0;
        ArrayList<TLRPC.User> userList = new ArrayList<>();
        int count = (int) Math.ceil(((double) toImport.size()) / 500.0d);
        for (int a2 = 0; a2 < count; a2++) {
            TLRPC.TL_contacts_importContacts req = new TLRPC.TL_contacts_importContacts();
            int start = a2 * 500;
            req.contacts = new ArrayList<>(toImport.subList(start, Math.min(start + 500, toImport.size())));
            getConnectionsManager().sendRequest(req, new RequestDelegate(userList, count, inputPhoneContactsMap) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ HashMap f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ContactsController.this.lambda$null$13$ContactsController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            }, 6);
        }
    }

    public /* synthetic */ void lambda$null$13$ContactsController(ArrayList userList, int count, HashMap inputPhoneContactsMap, TLObject response, TLRPC.TL_error error) {
        this.completedRequestsCount++;
        if (error == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("contacts imported");
            }
            TLRPC.TL_contacts_importedContacts res = (TLRPC.TL_contacts_importedContacts) response;
            if (res.users != null && !res.users.isEmpty()) {
                userList.addAll(res.users);
            }
        }
        if (this.completedRequestsCount == count) {
            AndroidUtilities.runOnUIThread(new Runnable(userList, inputPhoneContactsMap) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ HashMap f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ContactsController.this.lambda$null$12$ContactsController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$12$ContactsController(ArrayList userList, HashMap inputPhoneContactsMap) {
        getNotificationCenter().postNotificationName(NotificationCenter.contactAboutPhonebookLoaded, userList, inputPhoneContactsMap);
    }

    /* access modifiers changed from: protected */
    public void performSyncPhoneBook(HashMap<String, Contact> contactHashMap, boolean request, boolean first, boolean schedule, boolean force, boolean checkCount, boolean canceled) {
        if (!first) {
            if (!this.contactsBookLoaded) {
                return;
            }
        }
        Utilities.globalQueue.postRunnable(new Runnable(contactHashMap, schedule, request, first, force, checkCount, canceled) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ boolean f$6;
            private final /* synthetic */ boolean f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                ContactsController.this.lambda$performSyncPhoneBook$27$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0162, code lost:
        if (r2.first_name.equals(r1.first_name) != false) goto L_0x0167;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0177, code lost:
        if (r2.last_name.equals(r1.last_name) == false) goto L_0x0179;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0179, code lost:
        r0 = true;
     */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02df  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x022f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$performSyncPhoneBook$27$ContactsController(java.util.HashMap r35, boolean r36, boolean r37, boolean r38, boolean r39, boolean r40, boolean r41) {
        /*
            r34 = this;
            r13 = r34
            r14 = r35
            r0 = 0
            r1 = 0
            r15 = 1
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r12 = r2
            java.util.Set r2 = r35.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x0015:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x003d
            java.lang.Object r3 = r2.next()
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3
            java.lang.Object r4 = r3.getValue()
            im.bclpbkiauv.messenger.ContactsController$Contact r4 = (im.bclpbkiauv.messenger.ContactsController.Contact) r4
            r5 = 0
        L_0x0028:
            java.util.ArrayList<java.lang.String> r6 = r4.shortPhones
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x003c
            java.util.ArrayList<java.lang.String> r6 = r4.shortPhones
            java.lang.Object r6 = r6.get(r5)
            r12.put(r6, r4)
            int r5 = r5 + 1
            goto L_0x0028
        L_0x003c:
            goto L_0x0015
        L_0x003d:
            boolean r2 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0047
            java.lang.String r2 = "start read contacts from phone"
            im.bclpbkiauv.messenger.FileLog.d(r2)
        L_0x0047:
            if (r36 != 0) goto L_0x004c
            r34.checkContactsInternal()
        L_0x004c:
            java.util.HashMap r11 = r34.readContactsFromPhoneBook()
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r10 = r2
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r9 = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r8 = r2
            java.util.Set r2 = r11.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x006a:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x00ce
            java.lang.Object r3 = r2.next()
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3
            java.lang.Object r4 = r3.getValue()
            im.bclpbkiauv.messenger.ContactsController$Contact r4 = (im.bclpbkiauv.messenger.ContactsController.Contact) r4
            r5 = 0
            java.util.ArrayList<java.lang.String> r6 = r4.shortPhones
            int r6 = r6.size()
        L_0x0083:
            if (r5 >= r6) goto L_0x00aa
            java.util.ArrayList<java.lang.String> r7 = r4.shortPhones
            java.lang.Object r7 = r7.get(r5)
            java.lang.String r7 = (java.lang.String) r7
            int r17 = r7.length()
            r18 = r0
            int r0 = r17 + -7
            r17 = r1
            r1 = 0
            int r0 = java.lang.Math.max(r1, r0)
            java.lang.String r0 = r7.substring(r0)
            r9.put(r0, r4)
            int r5 = r5 + 1
            r1 = r17
            r0 = r18
            goto L_0x0083
        L_0x00aa:
            r18 = r0
            r17 = r1
            java.lang.String r0 = r4.getLetter()
            java.lang.Object r1 = r10.get(r0)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 != 0) goto L_0x00c6
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r1 = r5
            r10.put(r0, r1)
            r8.add(r0)
        L_0x00c6:
            r1.add(r4)
            r1 = r17
            r0 = r18
            goto L_0x006a
        L_0x00ce:
            r18 = r0
            r17 = r1
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7 = r0
            int r6 = r35.size()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r5 = r0
            boolean r0 = r35.isEmpty()
            java.lang.String r2 = ""
            if (r0 != 0) goto L_0x0451
            java.util.Set r0 = r11.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x00f2:
            boolean r4 = r0.hasNext()
            if (r4 == 0) goto L_0x03ea
            java.lang.Object r4 = r0.next()
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4
            java.lang.Object r19 = r4.getKey()
            r3 = r19
            java.lang.String r3 = (java.lang.String) r3
            java.lang.Object r19 = r4.getValue()
            r1 = r19
            im.bclpbkiauv.messenger.ContactsController$Contact r1 = (im.bclpbkiauv.messenger.ContactsController.Contact) r1
            java.lang.Object r19 = r14.get(r3)
            im.bclpbkiauv.messenger.ContactsController$Contact r19 = (im.bclpbkiauv.messenger.ContactsController.Contact) r19
            if (r19 != 0) goto L_0x0142
            r22 = 0
            r23 = r0
            r0 = r22
        L_0x011c:
            r22 = r2
            java.util.ArrayList<java.lang.String> r2 = r1.shortPhones
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x0146
            java.util.ArrayList<java.lang.String> r2 = r1.shortPhones
            java.lang.Object r2 = r2.get(r0)
            java.lang.Object r2 = r12.get(r2)
            im.bclpbkiauv.messenger.ContactsController$Contact r2 = (im.bclpbkiauv.messenger.ContactsController.Contact) r2
            if (r2 == 0) goto L_0x013b
            r19 = r2
            r24 = r2
            java.lang.String r3 = r2.key
            goto L_0x0148
        L_0x013b:
            r24 = r2
            int r0 = r0 + 1
            r2 = r22
            goto L_0x011c
        L_0x0142:
            r23 = r0
            r22 = r2
        L_0x0146:
            r2 = r19
        L_0x0148:
            if (r2 == 0) goto L_0x014e
            int r0 = r2.imported
            r1.imported = r0
        L_0x014e:
            if (r2 == 0) goto L_0x017b
            java.lang.String r0 = r1.first_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0165
            java.lang.String r0 = r2.first_name
            r19 = r4
            java.lang.String r4 = r1.first_name
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x0179
            goto L_0x0167
        L_0x0165:
            r19 = r4
        L_0x0167:
            java.lang.String r0 = r1.last_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x017d
            java.lang.String r0 = r2.last_name
            java.lang.String r4 = r1.last_name
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x017d
        L_0x0179:
            r0 = 1
            goto L_0x017e
        L_0x017b:
            r19 = r4
        L_0x017d:
            r0 = 0
        L_0x017e:
            if (r2 == 0) goto L_0x0334
            if (r0 == 0) goto L_0x0193
            r33 = r6
            r32 = r7
            r26 = r8
            r27 = r9
            r30 = r10
            r25 = r12
            r31 = r15
            r15 = r11
            goto L_0x0343
        L_0x0193:
            r24 = 0
            r4 = r24
        L_0x0197:
            r25 = r12
            java.util.ArrayList<java.lang.String> r12 = r1.phones
            int r12 = r12.size()
            if (r4 >= r12) goto L_0x0318
            java.util.ArrayList<java.lang.String> r12 = r1.shortPhones
            java.lang.Object r12 = r12.get(r4)
            java.lang.String r12 = (java.lang.String) r12
            int r26 = r12.length()
            r27 = r9
            int r9 = r26 + -7
            r26 = r8
            r8 = 0
            int r9 = java.lang.Math.max(r8, r9)
            java.lang.String r8 = r12.substring(r9)
            r7.put(r12, r1)
            java.util.ArrayList<java.lang.String> r9 = r2.shortPhones
            int r9 = r9.indexOf(r12)
            r28 = 0
            if (r37 == 0) goto L_0x0224
            r29 = r9
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r9 = r13.contactsByPhone
            java.lang.Object r9 = r9.get(r12)
            im.bclpbkiauv.tgnet.TLRPC$Contact r9 = (im.bclpbkiauv.tgnet.TLRPC.Contact) r9
            if (r9 == 0) goto L_0x0213
            r30 = r10
            im.bclpbkiauv.messenger.MessagesController r10 = r34.getMessagesController()
            r31 = r15
            int r15 = r9.user_id
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            im.bclpbkiauv.tgnet.TLRPC$User r10 = r10.getUser(r15)
            if (r10 == 0) goto L_0x0210
            int r17 = r17 + 1
            java.lang.String r15 = r10.first_name
            boolean r15 = android.text.TextUtils.isEmpty(r15)
            if (r15 == 0) goto L_0x0210
            java.lang.String r15 = r10.last_name
            boolean r15 = android.text.TextUtils.isEmpty(r15)
            if (r15 == 0) goto L_0x0210
            java.lang.String r15 = r1.first_name
            boolean r15 = android.text.TextUtils.isEmpty(r15)
            if (r15 == 0) goto L_0x020b
            java.lang.String r15 = r1.last_name
            boolean r15 = android.text.TextUtils.isEmpty(r15)
            if (r15 != 0) goto L_0x0210
        L_0x020b:
            r15 = -1
            r28 = 1
            r29 = r15
        L_0x0210:
            r9 = r29
            goto L_0x022c
        L_0x0213:
            r30 = r10
            r31 = r15
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r10 = r13.contactsByShortPhone
            boolean r10 = r10.containsKey(r8)
            if (r10 == 0) goto L_0x022a
            int r17 = r17 + 1
            r9 = r29
            goto L_0x022c
        L_0x0224:
            r29 = r9
            r30 = r10
            r31 = r15
        L_0x022a:
            r9 = r29
        L_0x022c:
            r10 = -1
            if (r9 != r10) goto L_0x02df
            if (r37 == 0) goto L_0x02d7
            if (r28 != 0) goto L_0x02a6
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r10 = r13.contactsByPhone
            java.lang.Object r10 = r10.get(r12)
            im.bclpbkiauv.tgnet.TLRPC$Contact r10 = (im.bclpbkiauv.tgnet.TLRPC.Contact) r10
            if (r10 == 0) goto L_0x0297
            im.bclpbkiauv.messenger.MessagesController r15 = r34.getMessagesController()
            r29 = r12
            int r12 = r10.user_id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            im.bclpbkiauv.tgnet.TLRPC$User r12 = r15.getUser(r12)
            if (r12 == 0) goto L_0x0290
            int r17 = r17 + 1
            java.lang.String r15 = r12.first_name
            if (r15 == 0) goto L_0x0258
            java.lang.String r15 = r12.first_name
            goto L_0x025a
        L_0x0258:
            r15 = r22
        L_0x025a:
            r32 = r10
            java.lang.String r10 = r12.last_name
            if (r10 == 0) goto L_0x0263
            java.lang.String r10 = r12.last_name
            goto L_0x0265
        L_0x0263:
            r10 = r22
        L_0x0265:
            r33 = r12
            java.lang.String r12 = r1.first_name
            boolean r12 = r15.equals(r12)
            if (r12 == 0) goto L_0x0277
            java.lang.String r12 = r1.last_name
            boolean r12 = r10.equals(r12)
            if (r12 != 0) goto L_0x0288
        L_0x0277:
            java.lang.String r12 = r1.first_name
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 == 0) goto L_0x028f
            java.lang.String r12 = r1.last_name
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 == 0) goto L_0x028f
        L_0x0288:
            r33 = r6
            r32 = r7
            r15 = r11
            goto L_0x0305
        L_0x028f:
            goto L_0x0296
        L_0x0290:
            r32 = r10
            r33 = r12
            int r18 = r18 + 1
        L_0x0296:
            goto L_0x02a8
        L_0x0297:
            r32 = r10
            r29 = r12
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r10 = r13.contactsByShortPhone
            boolean r10 = r10.containsKey(r8)
            if (r10 == 0) goto L_0x0296
            int r17 = r17 + 1
            goto L_0x02a8
        L_0x02a6:
            r29 = r12
        L_0x02a8:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoneContact r10 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoneContact
            r10.<init>()
            int r12 = r1.contact_id
            r15 = r11
            long r11 = (long) r12
            r10.client_id = r11
            long r11 = r10.client_id
            r33 = r6
            r32 = r7
            long r6 = (long) r4
            r21 = 32
            long r6 = r6 << r21
            long r6 = r6 | r11
            r10.client_id = r6
            java.lang.String r6 = r1.first_name
            r10.first_name = r6
            java.lang.String r6 = r1.last_name
            r10.last_name = r6
            java.util.ArrayList<java.lang.String> r6 = r1.phones
            java.lang.Object r6 = r6.get(r4)
            java.lang.String r6 = (java.lang.String) r6
            r10.phone = r6
            r5.add(r10)
            goto L_0x0305
        L_0x02d7:
            r33 = r6
            r32 = r7
            r15 = r11
            r29 = r12
            goto L_0x0305
        L_0x02df:
            r33 = r6
            r32 = r7
            r15 = r11
            r29 = r12
            java.util.ArrayList<java.lang.Integer> r6 = r1.phoneDeleted
            java.util.ArrayList<java.lang.Integer> r7 = r2.phoneDeleted
            java.lang.Object r7 = r7.get(r9)
            r6.set(r4, r7)
            java.util.ArrayList<java.lang.String> r6 = r2.phones
            r6.remove(r9)
            java.util.ArrayList<java.lang.String> r6 = r2.shortPhones
            r6.remove(r9)
            java.util.ArrayList<java.lang.Integer> r6 = r2.phoneDeleted
            r6.remove(r9)
            java.util.ArrayList<java.lang.String> r6 = r2.phoneTypes
            r6.remove(r9)
        L_0x0305:
            int r4 = r4 + 1
            r11 = r15
            r12 = r25
            r8 = r26
            r9 = r27
            r10 = r30
            r15 = r31
            r7 = r32
            r6 = r33
            goto L_0x0197
        L_0x0318:
            r33 = r6
            r32 = r7
            r26 = r8
            r27 = r9
            r30 = r10
            r31 = r15
            r15 = r11
            java.util.ArrayList<java.lang.String> r4 = r2.phones
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x0330
            r14.remove(r3)
        L_0x0330:
            r8 = r32
            goto L_0x03d6
        L_0x0334:
            r33 = r6
            r32 = r7
            r26 = r8
            r27 = r9
            r30 = r10
            r25 = r12
            r31 = r15
            r15 = r11
        L_0x0343:
            r4 = 0
        L_0x0344:
            java.util.ArrayList<java.lang.String> r6 = r1.phones
            int r6 = r6.size()
            if (r4 >= r6) goto L_0x03cf
            java.util.ArrayList<java.lang.String> r6 = r1.shortPhones
            java.lang.Object r6 = r6.get(r4)
            java.lang.String r6 = (java.lang.String) r6
            int r7 = r6.length()
            int r7 = r7 + -7
            r8 = 0
            int r7 = java.lang.Math.max(r8, r7)
            java.lang.String r7 = r6.substring(r7)
            r8 = r32
            r8.put(r6, r1)
            if (r2 == 0) goto L_0x0388
            java.util.ArrayList<java.lang.String> r9 = r2.shortPhones
            int r9 = r9.indexOf(r6)
            r10 = -1
            if (r9 == r10) goto L_0x0388
            java.util.ArrayList<java.lang.Integer> r11 = r2.phoneDeleted
            java.lang.Object r11 = r11.get(r9)
            java.lang.Integer r11 = (java.lang.Integer) r11
            java.util.ArrayList<java.lang.Integer> r12 = r1.phoneDeleted
            r12.set(r4, r11)
            int r12 = r11.intValue()
            r10 = 1
            if (r12 != r10) goto L_0x0388
            goto L_0x03c9
        L_0x0388:
            if (r37 == 0) goto L_0x03c6
            if (r0 != 0) goto L_0x0399
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r9 = r13.contactsByPhone
            boolean r9 = r9.containsKey(r6)
            if (r9 == 0) goto L_0x0397
            int r17 = r17 + 1
            goto L_0x03c9
        L_0x0397:
            int r18 = r18 + 1
        L_0x0399:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoneContact r9 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoneContact
            r9.<init>()
            int r10 = r1.contact_id
            long r10 = (long) r10
            r9.client_id = r10
            long r10 = r9.client_id
            r12 = r6
            r28 = r7
            long r6 = (long) r4
            r21 = 32
            long r6 = r6 << r21
            long r6 = r6 | r10
            r9.client_id = r6
            java.lang.String r6 = r1.first_name
            r9.first_name = r6
            java.lang.String r6 = r1.last_name
            r9.last_name = r6
            java.util.ArrayList<java.lang.String> r6 = r1.phones
            java.lang.Object r6 = r6.get(r4)
            java.lang.String r6 = (java.lang.String) r6
            r9.phone = r6
            r5.add(r9)
            goto L_0x03c9
        L_0x03c6:
            r12 = r6
            r28 = r7
        L_0x03c9:
            int r4 = r4 + 1
            r32 = r8
            goto L_0x0344
        L_0x03cf:
            r8 = r32
            if (r2 == 0) goto L_0x03d6
            r14.remove(r3)
        L_0x03d6:
            r7 = r8
            r11 = r15
            r2 = r22
            r0 = r23
            r12 = r25
            r8 = r26
            r9 = r27
            r10 = r30
            r15 = r31
            r6 = r33
            goto L_0x00f2
        L_0x03ea:
            r33 = r6
            r26 = r8
            r27 = r9
            r30 = r10
            r25 = r12
            r31 = r15
            r8 = r7
            r15 = r11
            if (r38 != 0) goto L_0x0418
            boolean r0 = r35.isEmpty()
            if (r0 == 0) goto L_0x0418
            boolean r0 = r5.isEmpty()
            if (r0 == 0) goto L_0x0418
            int r0 = r15.size()
            r6 = r33
            if (r6 != r0) goto L_0x041a
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0417
            java.lang.String r0 = "contacts not changed!"
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x0417:
            return
        L_0x0418:
            r6 = r33
        L_0x041a:
            if (r37 == 0) goto L_0x044a
            boolean r0 = r35.isEmpty()
            if (r0 != 0) goto L_0x044a
            boolean r0 = r15.isEmpty()
            if (r0 != 0) goto L_0x044a
            boolean r0 = r5.isEmpty()
            if (r0 == 0) goto L_0x0438
            im.bclpbkiauv.messenger.MessagesStorage r0 = r34.getMessagesStorage()
            r11 = r15
            r1 = 0
            r0.putCachedPhoneBook(r11, r1, r1)
            goto L_0x0439
        L_0x0438:
            r11 = r15
        L_0x0439:
            if (r31 != 0) goto L_0x044b
            boolean r0 = r35.isEmpty()
            if (r0 != 0) goto L_0x044b
            im.bclpbkiauv.messenger.-$$Lambda$ContactsController$u_-P1OdYNzG2VaYiEsf62HBo9BI r0 = new im.bclpbkiauv.messenger.-$$Lambda$ContactsController$u_-P1OdYNzG2VaYiEsf62HBo9BI
            r0.<init>(r14)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x044b
        L_0x044a:
            r11 = r15
        L_0x044b:
            r12 = r17
            r15 = r18
            goto L_0x0556
        L_0x0451:
            r22 = r2
            r26 = r8
            r27 = r9
            r30 = r10
            r25 = r12
            r31 = r15
            r8 = r7
            if (r37 == 0) goto L_0x0552
            java.util.Set r0 = r11.entrySet()
            java.util.Iterator r0 = r0.iterator()
            r1 = r17
        L_0x046a:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x054e
            java.lang.Object r2 = r0.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r3 = r2.getValue()
            im.bclpbkiauv.messenger.ContactsController$Contact r3 = (im.bclpbkiauv.messenger.ContactsController.Contact) r3
            java.lang.Object r4 = r2.getKey()
            java.lang.String r4 = (java.lang.String) r4
            r7 = 0
        L_0x0483:
            java.util.ArrayList<java.lang.String> r9 = r3.phones
            int r9 = r9.size()
            if (r7 >= r9) goto L_0x0547
            if (r39 != 0) goto L_0x0512
            java.util.ArrayList<java.lang.String> r9 = r3.shortPhones
            java.lang.Object r9 = r9.get(r7)
            java.lang.String r9 = (java.lang.String) r9
            int r10 = r9.length()
            int r10 = r10 + -7
            r12 = 0
            int r10 = java.lang.Math.max(r12, r10)
            java.lang.String r10 = r9.substring(r10)
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r12 = r13.contactsByPhone
            java.lang.Object r12 = r12.get(r9)
            im.bclpbkiauv.tgnet.TLRPC$Contact r12 = (im.bclpbkiauv.tgnet.TLRPC.Contact) r12
            if (r12 == 0) goto L_0x0505
            im.bclpbkiauv.messenger.MessagesController r15 = r34.getMessagesController()
            r19 = r0
            int r0 = r12.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r15.getUser(r0)
            if (r0 == 0) goto L_0x0502
            int r1 = r1 + 1
            java.lang.String r15 = r0.first_name
            if (r15 == 0) goto L_0x04c9
            java.lang.String r15 = r0.first_name
            goto L_0x04cb
        L_0x04c9:
            r15 = r22
        L_0x04cb:
            r17 = r1
            java.lang.String r1 = r0.last_name
            if (r1 == 0) goto L_0x04d4
            java.lang.String r1 = r0.last_name
            goto L_0x04d6
        L_0x04d4:
            r1 = r22
        L_0x04d6:
            r23 = r0
            java.lang.String r0 = r3.first_name
            boolean r0 = r15.equals(r0)
            if (r0 == 0) goto L_0x04e8
            java.lang.String r0 = r3.last_name
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x04f9
        L_0x04e8:
            java.lang.String r0 = r3.first_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x04ff
            java.lang.String r0 = r3.last_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x04ff
        L_0x04f9:
            r12 = r2
            r1 = r17
            r17 = 32
            goto L_0x0540
        L_0x04ff:
            r1 = r17
            goto L_0x0504
        L_0x0502:
            r23 = r0
        L_0x0504:
            goto L_0x0514
        L_0x0505:
            r19 = r0
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r0 = r13.contactsByShortPhone
            boolean r0 = r0.containsKey(r10)
            if (r0 == 0) goto L_0x0504
            int r1 = r1 + 1
            goto L_0x0514
        L_0x0512:
            r19 = r0
        L_0x0514:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoneContact r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoneContact
            r0.<init>()
            int r9 = r3.contact_id
            long r9 = (long) r9
            r0.client_id = r9
            long r9 = r0.client_id
            r15 = r1
            r12 = r2
            long r1 = (long) r7
            r17 = 32
            long r1 = r1 << r17
            long r1 = r1 | r9
            r0.client_id = r1
            java.lang.String r1 = r3.first_name
            r0.first_name = r1
            java.lang.String r1 = r3.last_name
            r0.last_name = r1
            java.util.ArrayList<java.lang.String> r1 = r3.phones
            java.lang.Object r1 = r1.get(r7)
            java.lang.String r1 = (java.lang.String) r1
            r0.phone = r1
            r5.add(r0)
            r1 = r15
        L_0x0540:
            int r7 = r7 + 1
            r2 = r12
            r0 = r19
            goto L_0x0483
        L_0x0547:
            r19 = r0
            r12 = r2
            r17 = 32
            goto L_0x046a
        L_0x054e:
            r12 = r1
            r15 = r18
            goto L_0x0556
        L_0x0552:
            r12 = r17
            r15 = r18
        L_0x0556:
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x055f
            java.lang.String r0 = "done processing contacts"
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x055f:
            if (r37 == 0) goto L_0x06f9
            boolean r0 = r5.isEmpty()
            if (r0 != 0) goto L_0x06cf
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0571
            java.lang.String r0 = "start import contacts"
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r0)
        L_0x0571:
            if (r40 == 0) goto L_0x059c
            if (r15 == 0) goto L_0x059c
            r0 = 30
            if (r15 < r0) goto L_0x057d
            r0 = 1
            r17 = r0
            goto L_0x059f
        L_0x057d:
            if (r38 == 0) goto L_0x0598
            if (r6 != 0) goto L_0x0598
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r0 = r13.contactsByPhone
            int r0 = r0.size()
            int r0 = r0 - r12
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r1 = r13.contactsByPhone
            int r1 = r1.size()
            int r1 = r1 / 3
            int r1 = r1 * 2
            if (r0 <= r1) goto L_0x0598
            r0 = 2
            r17 = r0
            goto L_0x059f
        L_0x0598:
            r0 = 0
            r17 = r0
            goto L_0x059f
        L_0x059c:
            r0 = 0
            r17 = r0
        L_0x059f:
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05cd
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "new phone book contacts "
            r0.append(r1)
            r0.append(r15)
            java.lang.String r1 = " serverContactsInPhonebook "
            r0.append(r1)
            r0.append(r12)
            java.lang.String r1 = " totalContacts "
            r0.append(r1)
            java.util.HashMap<java.lang.String, im.bclpbkiauv.tgnet.TLRPC$Contact> r1 = r13.contactsByPhone
            int r1 = r1.size()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x05cd:
            if (r17 == 0) goto L_0x05e4
            im.bclpbkiauv.messenger.-$$Lambda$ContactsController$pv09Lya6fFGJQEgLPcKSdBDpRSU r7 = new im.bclpbkiauv.messenger.-$$Lambda$ContactsController$pv09Lya6fFGJQEgLPcKSdBDpRSU
            r0 = r7
            r1 = r34
            r2 = r17
            r3 = r35
            r4 = r38
            r10 = r5
            r5 = r36
            r0.<init>(r2, r3, r4, r5)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r7)
            return
        L_0x05e4:
            r10 = r5
            if (r41 == 0) goto L_0x0604
            im.bclpbkiauv.messenger.DispatchQueue r9 = im.bclpbkiauv.messenger.Utilities.stageQueue
            im.bclpbkiauv.messenger.-$$Lambda$ContactsController$JIcMjnjHkuIIOB4xtq6ngzy2Ymg r7 = new im.bclpbkiauv.messenger.-$$Lambda$ContactsController$JIcMjnjHkuIIOB4xtq6ngzy2Ymg
            r0 = r7
            r1 = r34
            r2 = r8
            r3 = r11
            r4 = r38
            r5 = r30
            r18 = r6
            r6 = r26
            r19 = r8
            r8 = r7
            r7 = r27
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r9.postRunnable(r8)
            return
        L_0x0604:
            r18 = r6
            r19 = r8
            r0 = 1
            boolean[] r4 = new boolean[r0]
            r0 = 0
            r4[r0] = r0
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>(r11)
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r9 = r0
            java.util.Set r0 = r2.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x0621:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x063b
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r3 = r1.getValue()
            im.bclpbkiauv.messenger.ContactsController$Contact r3 = (im.bclpbkiauv.messenger.ContactsController.Contact) r3
            int r5 = r3.contact_id
            java.lang.String r6 = r3.key
            r9.put(r5, r6)
            goto L_0x0621
        L_0x063b:
            r7 = 0
            r13.completedRequestsCount = r7
            int r0 = r10.size()
            double r0 = (double) r0
            r5 = 4647503709213818880(0x407f400000000000, double:500.0)
            double r0 = r0 / r5
            double r0 = java.lang.Math.ceil(r0)
            int r8 = (int) r0
            r0 = 0
            r7 = r0
        L_0x0650:
            if (r7 >= r8) goto L_0x06bb
            im.bclpbkiauv.tgnet.TLRPC$TL_contacts_importContacts r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_contacts_importContacts
            r0.<init>()
            r6 = r0
            int r5 = r7 * 500
            int r0 = r5 + 500
            int r1 = r10.size()
            int r3 = java.lang.Math.min(r0, r1)
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.List r1 = r10.subList(r5, r3)
            r0.<init>(r1)
            r6.contacts = r0
            im.bclpbkiauv.tgnet.ConnectionsManager r1 = r34.getConnectionsManager()
            im.bclpbkiauv.messenger.-$$Lambda$ContactsController$xwkOoSHjp9yVdtrOHgJ2JfxSEX0 r0 = new im.bclpbkiauv.messenger.-$$Lambda$ContactsController$xwkOoSHjp9yVdtrOHgJ2JfxSEX0
            r16 = r0
            r13 = r1
            r1 = r34
            r20 = r3
            r3 = r9
            r21 = r5
            r5 = r11
            r22 = r6
            r23 = r7
            r7 = r8
            r24 = r26
            r26 = r8
            r8 = r19
            r28 = r9
            r9 = r38
            r29 = r30
            r30 = r10
            r10 = r29
            r32 = r11
            r11 = r24
            r33 = r12
            r12 = r27
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r0 = 6
            r3 = r16
            r1 = r22
            r13.sendRequest(r1, r3, r0)
            int r7 = r23 + 1
            r13 = r34
            r8 = r26
            r9 = r28
            r10 = r30
            r11 = r32
            r12 = r33
            r26 = r24
            r30 = r29
            goto L_0x0650
        L_0x06bb:
            r23 = r7
            r28 = r9
            r32 = r11
            r33 = r12
            r24 = r26
            r29 = r30
            r26 = r8
            r30 = r10
            r1 = r32
            goto L_0x0734
        L_0x06cf:
            r18 = r6
            r19 = r8
            r32 = r11
            r33 = r12
            r24 = r26
            r29 = r30
            r30 = r5
            im.bclpbkiauv.messenger.DispatchQueue r8 = im.bclpbkiauv.messenger.Utilities.stageQueue
            im.bclpbkiauv.messenger.-$$Lambda$ContactsController$RYKh38MmoswsCcqyc44hYRHJPDc r9 = new im.bclpbkiauv.messenger.-$$Lambda$ContactsController$RYKh38MmoswsCcqyc44hYRHJPDc
            r0 = r9
            r1 = r34
            r2 = r19
            r3 = r32
            r4 = r38
            r5 = r29
            r6 = r24
            r7 = r27
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            r1 = r32
            goto L_0x0734
        L_0x06f9:
            r18 = r6
            r19 = r8
            r32 = r11
            r33 = r12
            r24 = r26
            r29 = r30
            r7 = 0
            r30 = r5
            im.bclpbkiauv.messenger.DispatchQueue r8 = im.bclpbkiauv.messenger.Utilities.stageQueue
            im.bclpbkiauv.messenger.-$$Lambda$ContactsController$DhyQf8DMb5fNtjo-ZXpH8W3mdh4 r9 = new im.bclpbkiauv.messenger.-$$Lambda$ContactsController$DhyQf8DMb5fNtjo-ZXpH8W3mdh4
            r0 = r9
            r1 = r34
            r2 = r19
            r3 = r32
            r4 = r38
            r5 = r29
            r6 = r24
            r10 = 0
            r7 = r27
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.postRunnable(r9)
            boolean r0 = r32.isEmpty()
            if (r0 != 0) goto L_0x0732
            im.bclpbkiauv.messenger.MessagesStorage r0 = r34.getMessagesStorage()
            r1 = r32
            r0.putCachedPhoneBook(r1, r10, r10)
            goto L_0x0734
        L_0x0732:
            r1 = r32
        L_0x0734:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ContactsController.lambda$performSyncPhoneBook$27$ContactsController(java.util.HashMap, boolean, boolean, boolean, boolean, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$null$15$ContactsController(HashMap contactHashMap) {
        ArrayList<TLRPC.User> toDelete = new ArrayList<>();
        if (contactHashMap != null && !contactHashMap.isEmpty()) {
            try {
                HashMap<String, TLRPC.User> contactsPhonesShort = new HashMap<>();
                for (int a = 0; a < this.contacts.size(); a++) {
                    TLRPC.User user = getMessagesController().getUser(Integer.valueOf(this.contacts.get(a).user_id));
                    if (user != null) {
                        if (!TextUtils.isEmpty(user.phone)) {
                            contactsPhonesShort.put(user.phone, user);
                        }
                    }
                }
                int removed = 0;
                for (Map.Entry<String, Contact> entry : contactHashMap.entrySet()) {
                    Contact contact = entry.getValue();
                    boolean was = false;
                    int a2 = 0;
                    while (a2 < contact.shortPhones.size()) {
                        TLRPC.User user2 = contactsPhonesShort.get(contact.shortPhones.get(a2));
                        if (user2 != null) {
                            was = true;
                            toDelete.add(user2);
                            contact.shortPhones.remove(a2);
                            a2--;
                        }
                        a2++;
                    }
                    if (!was || contact.shortPhones.size() == 0) {
                        removed++;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (!toDelete.isEmpty()) {
            deleteContact(toDelete);
        }
    }

    public /* synthetic */ void lambda$null$16$ContactsController(int checkType, HashMap contactHashMap, boolean first, boolean schedule) {
        getNotificationCenter().postNotificationName(NotificationCenter.hasNewContactsToImport, Integer.valueOf(checkType), contactHashMap, Boolean.valueOf(first), Boolean.valueOf(schedule));
    }

    public /* synthetic */ void lambda$null$18$ContactsController(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC.User>) null, (ArrayList<TLRPC.Contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        getMessagesStorage().putCachedPhoneBook(contactsMap, false, false);
        AndroidUtilities.runOnUIThread(new Runnable(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ HashMap f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$17$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$17$ContactsController(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        lambda$null$25$ContactsController(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    public /* synthetic */ void lambda$null$22$ContactsController(HashMap contactsMapToSave, SparseArray contactIdToKey, boolean[] hasErrors, HashMap contactsMap, TLRPC.TL_contacts_importContacts req, int count, HashMap contactsBookShort, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal, TLObject response, TLRPC.TL_error error) {
        HashMap hashMap = contactsMapToSave;
        SparseArray sparseArray = contactIdToKey;
        TLRPC.TL_contacts_importContacts tL_contacts_importContacts = req;
        TLRPC.TL_error tL_error = error;
        this.completedRequestsCount++;
        if (tL_error == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("contacts imported");
            }
            TLRPC.TL_contacts_importedContacts res = (TLRPC.TL_contacts_importedContacts) response;
            if (!res.retry_contacts.isEmpty()) {
                for (int a1 = 0; a1 < res.retry_contacts.size(); a1++) {
                    hashMap.remove(sparseArray.get((int) res.retry_contacts.get(a1).longValue()));
                }
                hasErrors[0] = true;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("result has retry contacts");
                }
            }
            for (int a12 = 0; a12 < res.popular_invites.size(); a12++) {
                TLRPC.TL_popularContact popularContact = res.popular_invites.get(a12);
                Contact contact = (Contact) contactsMap.get(sparseArray.get((int) popularContact.client_id));
                if (contact != null) {
                    contact.imported = popularContact.importers;
                }
            }
            HashMap hashMap2 = contactsMap;
            getMessagesStorage().putUsersAndChats(res.users, (ArrayList<TLRPC.Chat>) null, true, true);
            ArrayList<TLRPC.Contact> cArr = new ArrayList<>();
            for (int a13 = 0; a13 < res.imported.size(); a13++) {
                TLRPC.Contact contact2 = new TLRPC.Contact();
                contact2.user_id = res.imported.get(a13).user_id;
                cArr.add(contact2);
            }
            processLoadedContacts(cArr, res.users, 2);
        } else {
            HashMap hashMap3 = contactsMap;
            for (int a14 = 0; a14 < tL_contacts_importContacts.contacts.size(); a14++) {
                hashMap.remove(sparseArray.get((int) tL_contacts_importContacts.contacts.get(a14).client_id));
            }
            hasErrors[0] = true;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("import contacts error " + tL_error.text);
            }
        }
        if (this.completedRequestsCount == count) {
            if (!contactsMapToSave.isEmpty()) {
                getMessagesStorage().putCachedPhoneBook(hashMap, false, false);
            }
            $$Lambda$ContactsController$6BHddagl_EtVTTuuyjJ6InKw26s r9 = r0;
            DispatchQueue dispatchQueue = Utilities.stageQueue;
            $$Lambda$ContactsController$6BHddagl_EtVTTuuyjJ6InKw26s r0 = new Runnable(contactsBookShort, contactsMap, first, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal, hasErrors) {
                private final /* synthetic */ HashMap f$1;
                private final /* synthetic */ HashMap f$2;
                private final /* synthetic */ boolean f$3;
                private final /* synthetic */ HashMap f$4;
                private final /* synthetic */ ArrayList f$5;
                private final /* synthetic */ HashMap f$6;
                private final /* synthetic */ boolean[] f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run() {
                    ContactsController.this.lambda$null$21$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            };
            dispatchQueue.postRunnable(r9);
        }
    }

    public /* synthetic */ void lambda$null$21$ContactsController(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal, boolean[] hasErrors) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC.User>) null, (ArrayList<TLRPC.Contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ HashMap f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$19$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
        if (hasErrors[0]) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    ContactsController.this.lambda$null$20$ContactsController();
                }
            }, 300000);
        }
    }

    public /* synthetic */ void lambda$null$19$ContactsController(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        lambda$null$25$ContactsController(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    public /* synthetic */ void lambda$null$20$ContactsController() {
        getMessagesStorage().getCachedPhoneBook(true);
    }

    public /* synthetic */ void lambda$null$24$ContactsController(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC.User>) null, (ArrayList<TLRPC.Contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ HashMap f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$23$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$23$ContactsController(HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        lambda$null$25$ContactsController(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal);
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsImported, new Object[0]);
    }

    public /* synthetic */ void lambda$null$26$ContactsController(HashMap contactsBookShort, HashMap contactsMap, boolean first, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookByShortPhonesFinal) {
        this.contactsBookSPhones = contactsBookShort;
        this.contactsBook = contactsMap;
        this.contactsSyncInProgress = false;
        this.contactsBookLoaded = true;
        if (first) {
            this.contactsLoaded = true;
        }
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC.User>) null, (ArrayList<TLRPC.Contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable(phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal, phoneBookByShortPhonesFinal) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ HashMap f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$25$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public boolean isLoadingContacts() {
        boolean z;
        synchronized (this.loadContactsSync) {
            z = this.loadingContacts;
        }
        return z;
    }

    private int getContactsHash(ArrayList<TLRPC.Contact> contacts2) {
        long j;
        long j2;
        long acc = 0;
        ArrayList arrayList = new ArrayList(contacts2);
        Collections.sort(arrayList, $$Lambda$ContactsController$kvAfqPIMyIvBnU9JylebvTIy8.INSTANCE);
        int count = arrayList.size();
        for (int a = -1; a < count; a++) {
            if (a == -1) {
                j2 = (20261 * acc) + 2147483648L;
                j = (long) getUserConfig().contactsSavedCount;
            } else {
                j2 = (20261 * acc) + 2147483648L;
                j = (long) ((TLRPC.Contact) arrayList.get(a)).user_id;
            }
            acc = (j2 + j) % 2147483648L;
        }
        return (int) acc;
    }

    static /* synthetic */ int lambda$getContactsHash$28(TLRPC.Contact tl_contact, TLRPC.Contact tl_contact2) {
        if (tl_contact.user_id > tl_contact2.user_id) {
            return 1;
        }
        if (tl_contact.user_id < tl_contact2.user_id) {
            return -1;
        }
        return 0;
    }

    public void loadContacts(boolean fromCache, int hash) {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = true;
        }
        if (fromCache) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts from cache");
            }
            getMessagesStorage().getContacts();
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load contacts from server");
        }
        TLRPCContacts.TL_getContactsV1 req = new TLRPCContacts.TL_getContactsV1();
        req.hash = hash;
        getConnectionsManager().sendRequest(req, new RequestDelegate(hash) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ContactsController.this.lambda$loadContacts$30$ContactsController(this.f$1, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadContacts$30$ContactsController(int hash, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.contacts_Contacts res = (TLRPC.contacts_Contacts) response;
            if (hash == 0 || !(res instanceof TLRPC.TL_contacts_contactsNotModified)) {
                getUserConfig().contactsSavedCount = res.saved_count;
                getUserConfig().saveConfig(false);
                processLoadedContacts(res.contacts, res.users, 0);
                return;
            }
            this.contactsLoaded = true;
            if (!this.delayedContactsUpdate.isEmpty() && this.contactsBookLoaded) {
                applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC.User>) null, (ArrayList<TLRPC.Contact>) null, (ArrayList<Integer>) null);
                this.delayedContactsUpdate.clear();
            }
            getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            getUserConfig().saveConfig(false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ContactsController.this.lambda$null$29$ContactsController();
                }
            });
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("load contacts don't change");
            }
        }
    }

    public /* synthetic */ void lambda$null$29$ContactsController() {
        synchronized (this.loadContactsSync) {
            this.loadingContacts = false;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processLoadedContacts(ArrayList<TLRPC.Contact> contactsArr, ArrayList<TLRPC.User> usersArr, int from) {
        AndroidUtilities.runOnUIThread(new Runnable(usersArr, from, contactsArr) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ ArrayList f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$processLoadedContacts$38$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedContacts$38$ContactsController(ArrayList usersArr, int from, ArrayList contactsArr) {
        getMessagesController().putUsers(usersArr, from == 1);
        SparseArray<TLRPC.User> usersDict = new SparseArray<>();
        ArrayList arrayList = new ArrayList();
        boolean isEmpty = contactsArr.isEmpty();
        if (!this.contacts.isEmpty()) {
            HashMap<Integer, TLRPC.Contact> remoteContactsMap = new HashMap<>();
            int a = 0;
            while (a < contactsArr.size()) {
                TLRPC.Contact contact = (TLRPC.Contact) contactsArr.get(a);
                remoteContactsMap.put(Integer.valueOf(contact.user_id), contact);
                if (this.contactsDict.get(Integer.valueOf(contact.user_id)) != null) {
                    contactsArr.remove(a);
                    a--;
                }
                a++;
            }
            int i = 0;
            while (i < this.contacts.size()) {
                if (remoteContactsMap.get(Integer.valueOf(this.contacts.get(i).user_id)) == null) {
                    this.contacts.remove(i);
                    i--;
                }
                i++;
            }
            contactsArr.addAll(this.contacts);
        }
        for (int a2 = 0; a2 < contactsArr.size(); a2++) {
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(((TLRPC.Contact) contactsArr.get(a2)).user_id));
            if (user != null) {
                usersDict.put(user.id, user);
                arrayList.add(Integer.valueOf(user.id));
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable(from, contactsArr, usersDict, usersArr, isEmpty) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ SparseArray f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                ContactsController.this.lambda$null$37$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$37$ContactsController(int from, ArrayList contactsArr, SparseArray usersDict, ArrayList usersArr, boolean isEmpty) {
        HashMap<String, TLRPC.Contact> contactsByPhonesDict;
        HashMap<String, TLRPC.Contact> contactsByPhonesDict2;
        HashMap<String, TLRPC.Contact> contactsByPhonesDictFinal;
        HashMap<String, TLRPC.Contact> contactsByPhonesShortDict;
        String key;
        ArrayList<TLRPC.Contact> arr;
        int i = from;
        ArrayList arrayList = contactsArr;
        SparseArray sparseArray = usersDict;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("done loading contactsfrom = " + i);
        }
        if (i == 1 && (contactsArr.isEmpty() || Math.abs((System.currentTimeMillis() / 1000) - ((long) getUserConfig().lastContactsSyncTime)) >= 7200)) {
            loadContacts(false, getContactsHash(arrayList));
            if (contactsArr.isEmpty()) {
                return;
            }
        }
        if (i == 0) {
            getUserConfig().lastContactsSyncTime = (int) (System.currentTimeMillis() / 1000);
            getUserConfig().saveConfig(false);
        }
        int a = 0;
        while (a < contactsArr.size()) {
            TLRPC.Contact contact = (TLRPC.Contact) arrayList.get(a);
            if (sparseArray.get(contact.user_id) != null || contact.user_id == getUserConfig().getClientUserId()) {
                a++;
            } else {
                loadContacts(false, 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("contacts are broken, load from server");
                    return;
                }
                return;
            }
        }
        if (i != 1) {
            getMessagesStorage().putUsersAndChats(usersArr, (ArrayList<TLRPC.Chat>) null, true, true);
            getMessagesStorage().putContacts(arrayList, i != 2);
        } else {
            ArrayList arrayList2 = usersArr;
        }
        Collections.sort(arrayList, new Comparator(sparseArray) {
            private final /* synthetic */ SparseArray f$0;

            {
                this.f$0 = r1;
            }

            public final int compare(Object obj, Object obj2) {
                return UserObject.getFirstName((TLRPC.User) this.f$0.get(((TLRPC.Contact) obj).user_id)).compareTo(UserObject.getFirstName((TLRPC.User) this.f$0.get(((TLRPC.Contact) obj2).user_id)));
            }
        });
        ConcurrentHashMap<Integer, TLRPC.Contact> contactsDictionary = new ConcurrentHashMap<>(20, 1.0f, 2);
        HashMap<String, ArrayList<TLRPC.Contact>> sectionsDict = new HashMap<>();
        HashMap<String, ArrayList<TLRPC.Contact>> sectionsDictMutual = new HashMap<>();
        ArrayList<String> sortedSectionsArray = new ArrayList<>();
        ArrayList<String> sortedSectionsArrayMutual = new ArrayList<>();
        HashMap<String, TLRPC.Contact> contactsByPhonesShortDict2 = null;
        if (!this.contactsBookLoaded) {
            HashMap<String, TLRPC.Contact> contactsByPhonesDict3 = new HashMap<>();
            contactsByPhonesShortDict2 = new HashMap<>();
            contactsByPhonesDict = contactsByPhonesDict3;
        } else {
            contactsByPhonesDict = null;
        }
        HashMap<String, TLRPC.Contact> contactsByPhonesDictFinal2 = contactsByPhonesDict;
        HashMap<String, TLRPC.Contact> contactsByPhonesShortDictFinal = contactsByPhonesShortDict2;
        int a2 = 0;
        while (a2 < contactsArr.size()) {
            TLRPC.Contact value = (TLRPC.Contact) arrayList.get(a2);
            TLRPC.User user = (TLRPC.User) sparseArray.get(value.user_id);
            if (user == null) {
                contactsByPhonesDictFinal = contactsByPhonesDictFinal2;
                contactsByPhonesShortDict = contactsByPhonesShortDict2;
                contactsByPhonesDict2 = contactsByPhonesDict;
            } else {
                contactsByPhonesDictFinal = contactsByPhonesDictFinal2;
                contactsDictionary.put(Integer.valueOf(value.user_id), value);
                if (contactsByPhonesDict == null || TextUtils.isEmpty(user.phone)) {
                    contactsByPhonesDict2 = contactsByPhonesDict;
                } else {
                    contactsByPhonesDict.put(user.phone, value);
                    contactsByPhonesDict2 = contactsByPhonesDict;
                    contactsByPhonesShortDict2.put(user.phone.substring(Math.max(0, user.phone.length() - 7)), value);
                }
                String key2 = CharacterParser.getInstance().getSelling(UserObject.getFirstName(user));
                if (key2.length() > 1) {
                    key2 = key2.substring(0, 1);
                }
                if (key2.length() == 0) {
                    key = "#";
                } else {
                    key = key2.toUpperCase();
                }
                String replace = this.sectionsToReplace.get(key);
                if (replace != null) {
                    key = replace;
                }
                ArrayList<TLRPC.Contact> arr2 = sectionsDict.get(key);
                if (arr2 == null) {
                    arr = new ArrayList<>();
                    sectionsDict.put(key, arr);
                    sortedSectionsArray.add(key);
                } else {
                    arr = arr2;
                }
                arr.add(value);
                contactsByPhonesShortDict = contactsByPhonesShortDict2;
                if (user.mutual_contact) {
                    ArrayList<TLRPC.Contact> arr3 = sectionsDictMutual.get(key);
                    if (arr3 == null) {
                        arr3 = new ArrayList<>();
                        sectionsDictMutual.put(key, arr3);
                        sortedSectionsArrayMutual.add(key);
                    }
                    arr3.add(value);
                }
            }
            a2++;
            ArrayList arrayList3 = usersArr;
            contactsByPhonesShortDict2 = contactsByPhonesShortDict;
            contactsByPhonesDictFinal2 = contactsByPhonesDictFinal;
            contactsByPhonesDict = contactsByPhonesDict2;
        }
        HashMap<String, TLRPC.Contact> hashMap = contactsByPhonesShortDict2;
        Collections.sort(sortedSectionsArray, $$Lambda$ContactsController$oEUlLnAYkdyA4VzOsuTJ1LEt0gs.INSTANCE);
        Collections.sort(sortedSectionsArrayMutual, $$Lambda$ContactsController$3b9GffP9YYy9THvchQtAciXn3LQ.INSTANCE);
        HashMap<String, TLRPC.Contact> contactsByPhonesDictFinal3 = contactsByPhonesDictFinal2;
        HashMap<String, TLRPC.Contact> hashMap2 = contactsByPhonesDict;
        HashMap<String, ArrayList<TLRPC.Contact>> hashMap3 = sectionsDictMutual;
        HashMap<String, ArrayList<TLRPC.Contact>> hashMap4 = sectionsDict;
        ConcurrentHashMap<Integer, TLRPC.Contact> concurrentHashMap = contactsDictionary;
        AndroidUtilities.runOnUIThread(new Runnable(contactsArr, contactsDictionary, sectionsDict, sectionsDictMutual, sortedSectionsArray, sortedSectionsArrayMutual, from, isEmpty) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ ConcurrentHashMap f$2;
            private final /* synthetic */ HashMap f$3;
            private final /* synthetic */ HashMap f$4;
            private final /* synthetic */ ArrayList f$5;
            private final /* synthetic */ ArrayList f$6;
            private final /* synthetic */ int f$7;
            private final /* synthetic */ boolean f$8;

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

            public final void run() {
                ContactsController.this.lambda$null$34$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
        if (!this.delayedContactsUpdate.isEmpty() && this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(this.delayedContactsUpdate, (ConcurrentHashMap<Integer, TLRPC.User>) null, (ArrayList<TLRPC.Contact>) null, (ArrayList<Integer>) null);
            this.delayedContactsUpdate.clear();
        }
        if (contactsByPhonesDictFinal3 != null) {
            AndroidUtilities.runOnUIThread(new Runnable(contactsByPhonesDictFinal3, contactsByPhonesShortDictFinal) {
                private final /* synthetic */ HashMap f$1;
                private final /* synthetic */ HashMap f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ContactsController.this.lambda$null$36$ContactsController(this.f$1, this.f$2);
                }
            });
            return;
        }
        this.contactsLoaded = true;
    }

    static /* synthetic */ int lambda$null$32(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    static /* synthetic */ int lambda$null$33(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    public /* synthetic */ void lambda$null$34$ContactsController(ArrayList contactsArr, ConcurrentHashMap contactsDictionary, HashMap sectionsDict, HashMap sectionsDictMutual, ArrayList sortedSectionsArray, ArrayList sortedSectionsArrayMutual, int from, boolean isEmpty) {
        this.contacts = contactsArr;
        this.contactsDict = contactsDictionary;
        this.usersSectionsDict = sectionsDict;
        this.usersMutualSectionsDict = sectionsDictMutual;
        this.sortedUsersSectionsArray = sortedSectionsArray;
        this.sortedUsersMutualSectionsArray = sortedSectionsArrayMutual;
        if (from != 2) {
            synchronized (this.loadContactsSync) {
                this.loadingContacts = false;
            }
        }
        performWriteContactsToPhoneBook();
        updateUnregisteredContacts();
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
        if (from == 1 || isEmpty) {
            reloadContactsStatusesMaybe();
        } else {
            saveContactsLoadTime();
        }
    }

    public /* synthetic */ void lambda$null$36$ContactsController(HashMap contactsByPhonesDictFinal, HashMap contactsByPhonesShortDictFinal) {
        Utilities.globalQueue.postRunnable(new Runnable(contactsByPhonesDictFinal, contactsByPhonesShortDictFinal) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ HashMap f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ContactsController.this.lambda$null$35$ContactsController(this.f$1, this.f$2);
            }
        });
        if (!this.contactsSyncInProgress) {
            this.contactsSyncInProgress = true;
            getMessagesStorage().getCachedPhoneBook(false);
        }
    }

    public /* synthetic */ void lambda$null$35$ContactsController(HashMap contactsByPhonesDictFinal, HashMap contactsByPhonesShortDictFinal) {
        this.contactsByPhone = contactsByPhonesDictFinal;
        this.contactsByShortPhone = contactsByPhonesShortDictFinal;
    }

    public boolean isContact(int uid) {
        return this.contactsDict.get(Integer.valueOf(uid)) != null;
    }

    private void reloadContactsStatusesMaybe() {
        try {
            if (MessagesController.getMainSettings(this.currentAccount).getLong("lastReloadStatusTime", 0) < System.currentTimeMillis() - 86400000) {
                reloadContactsStatuses();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void saveContactsLoadTime() {
        try {
            MessagesController.getMainSettings(this.currentAccount).edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: mergePhonebookAndAppContacts */
    public void lambda$null$25$ContactsController(HashMap<String, ArrayList<Object>> phoneBookSectionsDictFinal, ArrayList<String> phoneBookSectionsArrayFinal, HashMap<String, Contact> phoneBookByShortPhonesFinal) {
        Utilities.globalQueue.postRunnable(new Runnable(new ArrayList<>(this.contacts), phoneBookByShortPhonesFinal, phoneBookSectionsDictFinal, phoneBookSectionsArrayFinal) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ HashMap f$2;
            private final /* synthetic */ HashMap f$3;
            private final /* synthetic */ ArrayList f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ContactsController.this.lambda$mergePhonebookAndAppContacts$42$ContactsController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$mergePhonebookAndAppContacts$42$ContactsController(ArrayList contactsCopy, HashMap phoneBookByShortPhonesFinal, HashMap phoneBookSectionsDictFinal, ArrayList phoneBookSectionsArrayFinal) {
        int size = contactsCopy.size();
        for (int a = 0; a < size; a++) {
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(((TLRPC.Contact) contactsCopy.get(a)).user_id));
            if (user != null && !TextUtils.isEmpty(user.phone)) {
                Contact contact = (Contact) phoneBookByShortPhonesFinal.get(user.phone.substring(Math.max(0, user.phone.length() - 7)));
                if (contact == null) {
                    String key = Contact.getLetter(user.first_name, user.last_name);
                    ArrayList<Object> arrayList = (ArrayList) phoneBookSectionsDictFinal.get(key);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                        phoneBookSectionsDictFinal.put(key, arrayList);
                        phoneBookSectionsArrayFinal.add(key);
                    }
                    arrayList.add(user);
                } else if (contact.user == null) {
                    contact.user = user;
                }
            }
        }
        for (ArrayList<Object> arrayList2 : phoneBookSectionsDictFinal.values()) {
            Collections.sort(arrayList2, $$Lambda$ContactsController$dxXzOjhH9vdpGWVwSpGtHYKLc.INSTANCE);
        }
        Collections.sort(phoneBookSectionsArrayFinal, $$Lambda$ContactsController$YF7HsDJc1jYWS8SSQ0OJ6Err1Mk.INSTANCE);
        AndroidUtilities.runOnUIThread(new Runnable(phoneBookSectionsArrayFinal, phoneBookSectionsDictFinal) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ HashMap f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ContactsController.this.lambda$null$41$ContactsController(this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ int lambda$null$39(Object o1, Object o2) {
        String name1;
        String name2;
        if (o1 instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) o1;
            name1 = formatName(user.first_name, user.last_name);
        } else if (o1 instanceof Contact) {
            Contact contact = (Contact) o1;
            if (contact.user != null) {
                name1 = formatName(contact.user.first_name, contact.user.last_name);
            } else {
                name1 = formatName(contact.first_name, contact.last_name);
            }
        } else {
            name1 = "";
        }
        if (o2 instanceof TLRPC.User) {
            TLRPC.User user2 = (TLRPC.User) o2;
            name2 = formatName(user2.first_name, user2.last_name);
        } else if (o2 instanceof Contact) {
            Contact contact2 = (Contact) o2;
            if (contact2.user != null) {
                name2 = formatName(contact2.user.first_name, contact2.user.last_name);
            } else {
                name2 = formatName(contact2.first_name, contact2.last_name);
            }
        } else {
            name2 = "";
        }
        return name1.compareTo(name2);
    }

    static /* synthetic */ int lambda$null$40(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    public /* synthetic */ void lambda$null$41$ContactsController(ArrayList phoneBookSectionsArrayFinal, HashMap phoneBookSectionsDictFinal) {
        this.phoneBookSectionsArray = phoneBookSectionsArrayFinal;
        this.phoneBookSectionsDict = phoneBookSectionsDictFinal;
    }

    private void updateUnregisteredContacts() {
        HashMap<String, TLRPC.Contact> contactsPhonesShort = new HashMap<>();
        int size = this.contacts.size();
        for (int a = 0; a < size; a++) {
            TLRPC.Contact value = this.contacts.get(a);
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(value.user_id));
            if (user != null && !TextUtils.isEmpty(user.phone)) {
                contactsPhonesShort.put(user.phone, value);
            }
        }
        ArrayList<Contact> sortedPhoneBookContacts = new ArrayList<>();
        for (Map.Entry<String, Contact> pair : this.contactsBook.entrySet()) {
            Contact value2 = pair.getValue();
            boolean skip = false;
            int a2 = 0;
            while (true) {
                if (a2 >= value2.phones.size()) {
                    break;
                } else if (contactsPhonesShort.containsKey(value2.shortPhones.get(a2)) || value2.phoneDeleted.get(a2).intValue() == 1) {
                    skip = true;
                } else {
                    a2++;
                }
            }
            skip = true;
            if (!skip) {
                sortedPhoneBookContacts.add(value2);
            }
        }
        Collections.sort(sortedPhoneBookContacts, $$Lambda$ContactsController$zfHd1ZbKuxRVpWQgHxiySknxE2s.INSTANCE);
        this.phoneBookContacts = sortedPhoneBookContacts;
    }

    static /* synthetic */ int lambda$updateUnregisteredContacts$43(Contact contact, Contact contact2) {
        String toComapre1 = contact.first_name;
        if (toComapre1.length() == 0) {
            toComapre1 = contact.last_name;
        }
        String toComapre2 = contact2.first_name;
        if (toComapre2.length() == 0) {
            toComapre2 = contact2.last_name;
        }
        return toComapre1.compareTo(toComapre2);
    }

    private void buildContactsSectionsArrays(boolean sort) {
        String key;
        if (sort) {
            Collections.sort(this.contacts, new Comparator() {
                public final int compare(Object obj, Object obj2) {
                    return ContactsController.this.lambda$buildContactsSectionsArrays$44$ContactsController((TLRPC.Contact) obj, (TLRPC.Contact) obj2);
                }
            });
        }
        HashMap<String, ArrayList<TLRPC.Contact>> sectionsDict = new HashMap<>();
        ArrayList<String> sortedSectionsArray = new ArrayList<>();
        for (int a = 0; a < this.contacts.size(); a++) {
            TLRPC.Contact value = this.contacts.get(a);
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(value.user_id));
            if (user != null) {
                String key2 = CharacterParser.getInstance().getSelling(UserObject.getFirstName(user));
                if (key2.length() > 1) {
                    key2 = key2.substring(0, 1);
                }
                if (key2.length() == 0) {
                    key = "#";
                } else {
                    key = key2.toUpperCase();
                }
                String replace = this.sectionsToReplace.get(key);
                if (replace != null) {
                    key = replace;
                }
                ArrayList<TLRPC.Contact> arr = sectionsDict.get(key);
                if (arr == null) {
                    arr = new ArrayList<>();
                    sectionsDict.put(key, arr);
                    sortedSectionsArray.add(key);
                }
                arr.add(value);
            }
        }
        Collections.sort(sortedSectionsArray, $$Lambda$ContactsController$sThSr4QihTQHxss0XZMyvaXZoxQ.INSTANCE);
        this.usersSectionsDict = sectionsDict;
        this.sortedUsersSectionsArray = sortedSectionsArray;
    }

    public /* synthetic */ int lambda$buildContactsSectionsArrays$44$ContactsController(TLRPC.Contact tl_contact, TLRPC.Contact tl_contact2) {
        return UserObject.getFirstName(getMessagesController().getUser(Integer.valueOf(tl_contact.user_id))).compareTo(UserObject.getFirstName(getMessagesController().getUser(Integer.valueOf(tl_contact2.user_id))));
    }

    static /* synthetic */ int lambda$buildContactsSectionsArrays$45(String s, String s2) {
        char cv1 = s.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s.compareTo(s2);
    }

    private boolean hasContactsPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            Cursor cursor = null;
            try {
                Cursor cursor2 = ApplicationLoader.applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, this.projectionPhones, (String) null, (String[]) null, (String) null);
                if (cursor2 == null || cursor2.getCount() == 0) {
                    if (cursor2 != null) {
                        try {
                            cursor2.close();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    return false;
                }
                if (cursor2 != null) {
                    try {
                        cursor2.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                return true;
            } catch (Throwable th) {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                throw th;
            }
        } else if (ApplicationLoader.applicationContext.checkSelfPermission(PermissionUtils.LINKMAIN) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: performWriteContactsToPhoneBookInternal */
    public void lambda$performWriteContactsToPhoneBook$46$ContactsController(ArrayList<TLRPC.Contact> contactsArray) {
        Cursor cursor = null;
        try {
            if (hasContactsPermission()) {
                cursor = ApplicationLoader.applicationContext.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build(), new String[]{"_id", "sync2"}, (String) null, (String[]) null, (String) null);
                SparseLongArray bookContacts = new SparseLongArray();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        bookContacts.put(cursor.getInt(1), cursor.getLong(0));
                    }
                    cursor.close();
                    cursor = null;
                    for (int a = 0; a < contactsArray.size(); a++) {
                        TLRPC.Contact u = contactsArray.get(a);
                        if (bookContacts.indexOfKey(u.user_id) < 0) {
                            addContactToPhoneBook(getMessagesController().getUser(Integer.valueOf(u.user_id)), false);
                        }
                    }
                }
                if (cursor == null) {
                    return;
                }
                cursor.close();
            } else if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (cursor == null) {
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    private void performWriteContactsToPhoneBook() {
        Utilities.phoneBookQueue.postRunnable(new Runnable(new ArrayList<>(this.contacts)) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ContactsController.this.lambda$performWriteContactsToPhoneBook$46$ContactsController(this.f$1);
            }
        });
    }

    private void applyContactsUpdates(ArrayList<Integer> ids, ConcurrentHashMap<Integer, TLRPC.User> userDict, ArrayList<TLRPC.Contact> newC, ArrayList<Integer> contactsTD) {
        ArrayList<Integer> contactsTD2;
        ArrayList<TLRPC.Contact> newC2;
        int i;
        boolean z;
        int index;
        ConcurrentHashMap<Integer, TLRPC.User> concurrentHashMap = userDict;
        if (newC == null || contactsTD == null) {
            newC2 = new ArrayList<>();
            contactsTD2 = new ArrayList<>();
            for (int a = 0; a < ids.size(); a++) {
                Integer uid = ids.get(a);
                if (uid.intValue() > 0) {
                    TLRPC.Contact contact = new TLRPC.Contact();
                    contact.user_id = uid.intValue();
                    newC2.add(contact);
                } else if (uid.intValue() < 0) {
                    contactsTD2.add(Integer.valueOf(-uid.intValue()));
                }
            }
            ArrayList<Integer> arrayList = ids;
        } else {
            ArrayList<Integer> arrayList2 = ids;
            newC2 = newC;
            contactsTD2 = contactsTD;
        }
        if (BuildVars.LOGS_ENABLED != 0) {
            FileLog.d("process update - contacts add = " + newC2.size() + " delete = " + contactsTD2.size());
        }
        StringBuilder toAdd = new StringBuilder();
        StringBuilder toDelete = new StringBuilder();
        boolean reloadContacts = false;
        int a2 = 0;
        while (true) {
            i = -1;
            z = true;
            if (a2 >= newC2.size()) {
                break;
            }
            TLRPC.Contact newContact = newC2.get(a2);
            TLRPC.User user = null;
            if (concurrentHashMap != null) {
                user = concurrentHashMap.get(Integer.valueOf(newContact.user_id));
            }
            if (user == null) {
                user = getMessagesController().getUser(Integer.valueOf(newContact.user_id));
            } else {
                getMessagesController().putUser(user, true);
            }
            if (user == null || TextUtils.isEmpty(user.phone)) {
                reloadContacts = true;
            } else {
                Contact contact2 = this.contactsBookSPhones.get(user.phone);
                if (!(contact2 == null || (index = contact2.shortPhones.indexOf(user.phone)) == -1)) {
                    contact2.phoneDeleted.set(index, 0);
                }
                if (toAdd.length() != 0) {
                    toAdd.append(",");
                }
                toAdd.append(user.phone);
            }
            a2++;
        }
        int a3 = 0;
        while (a3 < contactsTD2.size()) {
            Integer uid2 = contactsTD2.get(a3);
            Utilities.phoneBookQueue.postRunnable(new Runnable(uid2) {
                private final /* synthetic */ Integer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContactsController.this.lambda$applyContactsUpdates$47$ContactsController(this.f$1);
                }
            });
            TLRPC.User user2 = null;
            if (concurrentHashMap != null) {
                user2 = concurrentHashMap.get(uid2);
            }
            if (user2 == null) {
                user2 = getMessagesController().getUser(uid2);
            } else {
                getMessagesController().putUser(user2, z);
            }
            if (user2 == null) {
                reloadContacts = true;
            } else if (!TextUtils.isEmpty(user2.phone)) {
                Contact contact3 = this.contactsBookSPhones.get(user2.phone);
                if (contact3 != null) {
                    int index2 = contact3.shortPhones.indexOf(user2.phone);
                    if (index2 != i) {
                        contact3.phoneDeleted.set(index2, 1);
                    }
                }
                if (toDelete.length() != 0) {
                    toDelete.append(",");
                }
                toDelete.append(user2.phone);
            }
            a3++;
            i = -1;
            z = true;
        }
        if (!(toAdd.length() == 0 && toDelete.length() == 0)) {
            getMessagesStorage().applyPhoneBookUpdates(toAdd.toString(), toDelete.toString());
        }
        if (reloadContacts) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public final void run() {
                    ContactsController.this.lambda$applyContactsUpdates$48$ContactsController();
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(newC2, contactsTD2) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ContactsController.this.lambda$applyContactsUpdates$49$ContactsController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyContactsUpdates$47$ContactsController(Integer uid) {
        deleteContactFromPhoneBook(uid.intValue());
    }

    public /* synthetic */ void lambda$applyContactsUpdates$48$ContactsController() {
        loadContacts(false, 0);
    }

    public /* synthetic */ void lambda$applyContactsUpdates$49$ContactsController(ArrayList newContacts, ArrayList contactsToDelete) {
        for (int a = 0; a < newContacts.size(); a++) {
            TLRPC.Contact contact = (TLRPC.Contact) newContacts.get(a);
            if (this.contactsDict.get(Integer.valueOf(contact.user_id)) == null) {
                this.contacts.add(contact);
                this.contactsDict.put(Integer.valueOf(contact.user_id), contact);
            }
        }
        for (int a2 = 0; a2 < contactsToDelete.size(); a2++) {
            Integer uid = (Integer) contactsToDelete.get(a2);
            TLRPC.Contact contact2 = this.contactsDict.get(uid);
            if (contact2 != null) {
                this.contacts.remove(contact2);
                this.contactsDict.remove(uid);
            }
        }
        if (newContacts.isEmpty() == 0) {
            updateUnregisteredContacts();
            performWriteContactsToPhoneBook();
        }
        performSyncPhoneBook(getContactsCopy(this.contactsBook), false, false, false, false, true, false);
        buildContactsSectionsArrays(!newContacts.isEmpty());
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void processContactsUpdates(ArrayList<Integer> ids, ConcurrentHashMap<Integer, TLRPC.User> userDict) {
        int idx;
        int idx2;
        ArrayList<TLRPC.Contact> newContacts = new ArrayList<>();
        ArrayList<Integer> contactsToDelete = new ArrayList<>();
        Iterator<Integer> it = ids.iterator();
        while (it.hasNext()) {
            Integer uid = it.next();
            if (uid.intValue() > 0) {
                TLRPC.Contact contact = new TLRPC.Contact();
                contact.user_id = uid.intValue();
                newContacts.add(contact);
                if (!this.delayedContactsUpdate.isEmpty() && (idx2 = this.delayedContactsUpdate.indexOf(Integer.valueOf(-uid.intValue()))) != -1) {
                    this.delayedContactsUpdate.remove(idx2);
                }
            } else if (uid.intValue() < 0) {
                contactsToDelete.add(Integer.valueOf(-uid.intValue()));
                if (!this.delayedContactsUpdate.isEmpty() && (idx = this.delayedContactsUpdate.indexOf(Integer.valueOf(-uid.intValue()))) != -1) {
                    this.delayedContactsUpdate.remove(idx);
                }
            }
        }
        contactsToDelete.isEmpty();
        if (!newContacts.isEmpty()) {
            getMessagesStorage().putContacts(newContacts, false);
        }
        if (!this.contactsLoaded || !this.contactsBookLoaded) {
            this.delayedContactsUpdate.addAll(ids);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay update - contacts add = " + newContacts.size() + " delete = " + contactsToDelete.size());
                return;
            }
            return;
        }
        applyContactsUpdates(ids, userDict, newContacts, contactsToDelete);
    }

    public long addContactToPhoneBook(TLRPC.User user, boolean check) {
        String str;
        long res;
        if (this.systemAccount == null || user == null || !hasContactsPermission()) {
            return -1;
        }
        long res2 = -1;
        synchronized (this.observerLock) {
            this.ignoreChanges = true;
        }
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        if (check) {
            try {
                contentResolver.delete(ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build(), "sync2 = " + user.id, (String[]) null);
            } catch (Exception e) {
            }
        }
        ArrayList arrayList = new ArrayList();
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue("account_name", this.systemAccount.name);
        builder.withValue("account_type", this.systemAccount.type);
        builder.withValue("sync1", TextUtils.isEmpty(user.phone) ? "" : user.phone);
        builder.withValue("sync2", Integer.valueOf(user.id));
        arrayList.add(builder.build());
        ContentProviderOperation.Builder builder2 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder2.withValueBackReference("raw_contact_id", 0);
        builder2.withValue("mimetype", "vnd.android.cursor.item/name");
        builder2.withValue("data2", user.first_name);
        builder2.withValue("data3", user.last_name);
        arrayList.add(builder2.build());
        ContentProviderOperation.Builder builder3 = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder3.withValueBackReference("raw_contact_id", 0);
        builder3.withValue("mimetype", "vnd.android.cursor.item/vnd.im.bclpbkiauv.messenger.android.profile");
        builder3.withValue("data1", Integer.valueOf(user.id));
        builder3.withValue("data2", LocaleController.getString(R.string.AppName) + " Profile");
        if (TextUtils.isEmpty(user.phone)) {
            str = formatName(user.first_name, user.last_name);
        } else {
            str = Marker.ANY_NON_NULL_MARKER + user.phone;
        }
        builder3.withValue("data3", str);
        builder3.withValue("data4", Integer.valueOf(user.id));
        arrayList.add(builder3.build());
        try {
            ContentProviderResult[] result = contentResolver.applyBatch("com.android.contacts", arrayList);
            if (!(result == null || result.length <= 0 || result[0].uri == null)) {
                res2 = Long.parseLong(result[0].uri.getLastPathSegment());
            }
            res = res2;
        } catch (Exception e2) {
            res = -1;
        }
        synchronized (this.observerLock) {
            this.ignoreChanges = false;
        }
        return res;
    }

    private void deleteContactFromPhoneBook(int uid) {
        if (hasContactsPermission()) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            try {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.systemAccount.name).appendQueryParameter("account_type", this.systemAccount.type).build();
                contentResolver.delete(rawContactUri, "sync2 = " + uid, (String[]) null);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void markAsContacted(String contactId) {
        if (contactId != null) {
            Utilities.phoneBookQueue.postRunnable(new Runnable(contactId) {
                private final /* synthetic */ String f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    ContactsController.lambda$markAsContacted$50(this.f$0);
                }
            });
        }
    }

    static /* synthetic */ void lambda$markAsContacted$50(String contactId) {
        Uri uri = Uri.parse(contactId);
        ContentValues values = new ContentValues();
        values.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
        ApplicationLoader.applicationContext.getContentResolver().update(uri, values, (String) null, (String[]) null);
    }

    public void addContact(TLRPC.User user, boolean exception) {
        if (user != null) {
            TLRPC.TL_contacts_addContact req = new TLRPC.TL_contacts_addContact();
            req.id = getMessagesController().getInputUser(user);
            req.first_name = user.first_name;
            req.last_name = user.last_name;
            req.phone = user.phone;
            req.add_phone_privacy_exception = exception;
            if (req.phone == null) {
                req.phone = "";
            } else if (req.phone.length() > 0 && !req.phone.startsWith(Marker.ANY_NON_NULL_MARKER)) {
                req.phone = Marker.ANY_NON_NULL_MARKER + req.phone;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate(user) {
                private final /* synthetic */ TLRPC.User f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ContactsController.this.lambda$addContact$53$ContactsController(this.f$1, tLObject, tL_error);
                }
            }, 6);
        }
    }

    public /* synthetic */ void lambda$addContact$53$ContactsController(TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        int index;
        if (error == null) {
            TLRPC.Updates res = (TLRPC.Updates) response;
            getMessagesController().processUpdates(res, false);
            for (int a = 0; a < res.users.size(); a++) {
                TLRPC.User u = res.users.get(a);
                if (u.id == user.id) {
                    Utilities.phoneBookQueue.postRunnable(new Runnable(u) {
                        private final /* synthetic */ TLRPC.User f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ContactsController.this.lambda$null$51$ContactsController(this.f$1);
                        }
                    });
                    TLRPC.Contact newContact = new TLRPC.Contact();
                    newContact.user_id = u.id;
                    ArrayList<TLRPC.Contact> arrayList = new ArrayList<>();
                    arrayList.add(newContact);
                    getMessagesStorage().putContacts(arrayList, false);
                    if (!TextUtils.isEmpty(u.phone)) {
                        String formatName = formatName(u.first_name, u.last_name);
                        getMessagesStorage().applyPhoneBookUpdates(u.phone, "");
                        Contact contact = this.contactsBookSPhones.get(u.phone);
                        if (!(contact == null || (index = contact.shortPhones.indexOf(u.phone)) == -1)) {
                            contact.phoneDeleted.set(index, 0);
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable(res) {
                private final /* synthetic */ TLRPC.Updates f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContactsController.this.lambda$null$52$ContactsController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$51$ContactsController(TLRPC.User u) {
        addContactToPhoneBook(u, true);
    }

    public /* synthetic */ void lambda$null$52$ContactsController(TLRPC.Updates res) {
        for (int a = 0; a < res.users.size(); a++) {
            TLRPC.User u = res.users.get(a);
            if (u.contact && this.contactsDict.get(Integer.valueOf(u.id)) == null) {
                TLRPC.Contact newContact = new TLRPC.Contact();
                newContact.user_id = u.id;
                this.contacts.add(newContact);
                this.contactsDict.put(Integer.valueOf(newContact.user_id), newContact);
            }
        }
        buildContactsSectionsArrays(true);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void deleteContact(ArrayList<TLRPC.User> users) {
        if (users != null && !users.isEmpty()) {
            TLRPC.TL_contacts_deleteContacts req = new TLRPC.TL_contacts_deleteContacts();
            ArrayList<Integer> uids = new ArrayList<>();
            Iterator<TLRPC.User> it = users.iterator();
            while (it.hasNext()) {
                TLRPC.User user = it.next();
                TLRPC.InputUser inputUser = getMessagesController().getInputUser(user);
                if (inputUser != null) {
                    user.contact = false;
                    uids.add(Integer.valueOf(user.id));
                    req.id.add(inputUser);
                }
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate(uids, users) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ContactsController.this.lambda$deleteContact$56$ContactsController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$deleteContact$56$ContactsController(ArrayList uids, ArrayList users, TLObject response, TLRPC.TL_error error) {
        int index;
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            getMessagesStorage().deleteContacts(uids);
            Utilities.phoneBookQueue.postRunnable(new Runnable(users) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContactsController.this.lambda$null$54$ContactsController(this.f$1);
                }
            });
            for (int a = 0; a < users.size(); a++) {
                TLRPC.User user = (TLRPC.User) users.get(a);
                if (!TextUtils.isEmpty(user.phone)) {
                    getMessagesStorage().applyPhoneBookUpdates(user.phone, "");
                    Contact contact = this.contactsBookSPhones.get(user.phone);
                    if (!(contact == null || (index = contact.shortPhones.indexOf(user.phone)) == -1)) {
                        contact.phoneDeleted.set(index, 1);
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable(users) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ContactsController.this.lambda$null$55$ContactsController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$54$ContactsController(ArrayList users) {
        Iterator it = users.iterator();
        while (it.hasNext()) {
            deleteContactFromPhoneBook(((TLRPC.User) it.next()).id);
        }
    }

    public /* synthetic */ void lambda$null$55$ContactsController(ArrayList users) {
        boolean remove = false;
        Iterator it = users.iterator();
        while (it.hasNext()) {
            TLRPC.User user = (TLRPC.User) it.next();
            TLRPC.Contact contact = this.contactsDict.get(Integer.valueOf(user.id));
            if (contact != null) {
                remove = true;
                this.contacts.remove(contact);
                this.contactsDict.remove(Integer.valueOf(user.id));
            }
        }
        if (remove) {
            buildContactsSectionsArrays(false);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 1);
        getNotificationCenter().postNotificationName(NotificationCenter.contactsDidLoad, new Object[0]);
    }

    public void reloadContactsStatuses() {
        saveContactsLoadTime();
        getMessagesController().clearFullUsers();
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        editor.putBoolean("needGetStatuses", true).commit();
        getConnectionsManager().sendRequest(new TLRPC.TL_contacts_getStatuses(), new RequestDelegate(editor) {
            private final /* synthetic */ SharedPreferences.Editor f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ContactsController.this.lambda$reloadContactsStatuses$58$ContactsController(this.f$1, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$reloadContactsStatuses$58$ContactsController(SharedPreferences.Editor editor, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(editor, response) {
                private final /* synthetic */ SharedPreferences.Editor f$1;
                private final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ContactsController.this.lambda$null$57$ContactsController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$57$ContactsController(SharedPreferences.Editor editor, TLObject response) {
        editor.remove("needGetStatuses").commit();
        TLRPC.Vector vector = (TLRPC.Vector) response;
        if (!vector.objects.isEmpty()) {
            ArrayList<TLRPC.User> dbUsersStatus = new ArrayList<>();
            Iterator<Object> it = vector.objects.iterator();
            while (it.hasNext()) {
                Object object = it.next();
                TLRPC.User toDbUser = new TLRPC.TL_user();
                TLRPC.TL_contactStatus status = (TLRPC.TL_contactStatus) object;
                if (status != null) {
                    if (status.status instanceof TLRPC.TL_userStatusRecently) {
                        status.status.expires = -100;
                    } else if (status.status instanceof TLRPC.TL_userStatusLastWeek) {
                        status.status.expires = -101;
                    } else if (status.status instanceof TLRPC.TL_userStatusLastMonth) {
                        status.status.expires = -102;
                    }
                    TLRPC.User user = getMessagesController().getUser(Integer.valueOf(status.user_id));
                    if (user != null) {
                        user.status = status.status;
                    }
                    toDbUser.status = status.status;
                    dbUsersStatus.add(toDbUser);
                }
            }
            getMessagesStorage().updateUsers(dbUsersStatus, true, true, true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 4);
    }

    public void loadPrivacySettings() {
        if (this.loadingDeleteInfo == 0) {
            this.loadingDeleteInfo = 1;
            getConnectionsManager().sendRequest(new TLRPC.TL_account_getAccountTTL(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ContactsController.this.lambda$loadPrivacySettings$60$ContactsController(tLObject, tL_error);
                }
            });
        }
        int a = 0;
        while (true) {
            int[] iArr = this.loadingPrivacyInfo;
            if (a < iArr.length) {
                if (iArr[a] == 0) {
                    iArr[a] = 1;
                    int num = a;
                    TLRPC.TL_account_getPrivacy req = new TLRPC.TL_account_getPrivacy();
                    switch (num) {
                        case 0:
                            req.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
                            break;
                        case 1:
                            req.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
                            break;
                        case 2:
                            req.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
                            break;
                        case 3:
                            req.key = new TLRPC.TL_inputPrivacyKeyPhoneP2P();
                            break;
                        case 4:
                            req.key = new TLRPC.TL_inputPrivacyKeyProfilePhoto();
                            break;
                        case 5:
                            req.key = new TLRPC.TL_inputPrivacyKeyForwards();
                            break;
                        case 6:
                            req.key = new TLRPC.TL_inputPrivacyKeyPhoneNumber();
                            break;
                        case 8:
                            req.key = new TLRPC.TL_inputPrivacyKeyMoment();
                            break;
                        default:
                            req.key = new TLRPC.TL_inputPrivacyKeyAddedByPhone();
                            break;
                    }
                    getConnectionsManager().sendRequest(req, new RequestDelegate(num) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            ContactsController.this.lambda$loadPrivacySettings$62$ContactsController(this.f$1, tLObject, tL_error);
                        }
                    });
                }
                a++;
            } else {
                getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                return;
            }
        }
    }

    public /* synthetic */ void lambda$loadPrivacySettings$60$ContactsController(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ContactsController.this.lambda$null$59$ContactsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$59$ContactsController(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            this.deleteAccountTTL = ((TLRPC.TL_accountDaysTTL) response).days;
            this.loadingDeleteInfo = 2;
        } else {
            this.loadingDeleteInfo = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    public /* synthetic */ void lambda$loadPrivacySettings$62$ContactsController(int num, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, num) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ContactsController.this.lambda$null$61$ContactsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$61$ContactsController(TLRPC.TL_error error, TLObject response, int num) {
        if (error == null) {
            TLRPC.TL_account_privacyRules rules = (TLRPC.TL_account_privacyRules) response;
            getMessagesController().putUsers(rules.users, false);
            getMessagesController().putChats(rules.chats, false);
            switch (num) {
                case 0:
                    this.lastseenPrivacyRules = rules.rules;
                    break;
                case 1:
                    this.groupPrivacyRules = rules.rules;
                    break;
                case 2:
                    this.callPrivacyRules = rules.rules;
                    break;
                case 3:
                    this.p2pPrivacyRules = rules.rules;
                    break;
                case 4:
                    this.profilePhotoPrivacyRules = rules.rules;
                    break;
                case 5:
                    this.forwardsPrivacyRules = rules.rules;
                    break;
                case 6:
                    this.phonePrivacyRules = rules.rules;
                    break;
                case 8:
                    this.momentPrivacyRules = rules.rules;
                    break;
                default:
                    this.addedByPhonePrivacyRules = rules.rules;
                    break;
            }
            this.loadingPrivacyInfo[num] = 2;
        } else {
            this.loadingPrivacyInfo[num] = 0;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    public void setDeleteAccountTTL(int ttl) {
        this.deleteAccountTTL = ttl;
    }

    public int getDeleteAccountTTL() {
        return this.deleteAccountTTL;
    }

    public boolean getLoadingDeleteInfo() {
        return this.loadingDeleteInfo != 2;
    }

    public boolean getLoadingPrivicyInfo(int type) {
        return this.loadingPrivacyInfo[type] != 2;
    }

    public ArrayList<TLRPC.PrivacyRule> getPrivacyRules(int type) {
        switch (type) {
            case 0:
                return this.lastseenPrivacyRules;
            case 1:
                return this.groupPrivacyRules;
            case 2:
                return this.callPrivacyRules;
            case 3:
                return this.p2pPrivacyRules;
            case 4:
                return this.profilePhotoPrivacyRules;
            case 5:
                return this.forwardsPrivacyRules;
            case 6:
                return this.phonePrivacyRules;
            case 7:
                return this.addedByPhonePrivacyRules;
            case 8:
                return this.momentPrivacyRules;
            default:
                return null;
        }
    }

    public void setPrivacyRules(ArrayList<TLRPC.PrivacyRule> rules, int type) {
        switch (type) {
            case 0:
                this.lastseenPrivacyRules = rules;
                break;
            case 1:
                this.groupPrivacyRules = rules;
                break;
            case 2:
                this.callPrivacyRules = rules;
                break;
            case 3:
                this.p2pPrivacyRules = rules;
                break;
            case 4:
                this.profilePhotoPrivacyRules = rules;
                break;
            case 5:
                this.forwardsPrivacyRules = rules;
                break;
            case 6:
                this.phonePrivacyRules = rules;
                break;
            case 7:
                this.addedByPhonePrivacyRules = rules;
                break;
            case 8:
                this.momentPrivacyRules = rules;
                break;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
        reloadContactsStatuses();
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x00d1 A[Catch:{ Exception -> 0x0298 }] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0122 A[SYNTHETIC, Splitter:B:20:0x0122] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0286 A[Catch:{ Exception -> 0x0292 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createOrUpdateConnectionServiceContact(int r27, java.lang.String r28, java.lang.String r29) {
        /*
            r26 = this;
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            java.lang.String r0 = "raw_contact_id=? AND mimetype=?"
            java.lang.String r5 = "vnd.android.cursor.item/group_membership"
            java.lang.String r6 = "AppConnectionService"
            java.lang.String r7 = "true"
            java.lang.String r8 = "caller_is_syncadapter"
            java.lang.String r9 = "mimetype"
            java.lang.String r10 = ""
            java.lang.String r11 = "raw_contact_id"
            boolean r12 = r26.hasContactsPermission()
            if (r12 != 0) goto L_0x0021
            return
        L_0x0021:
            android.content.Context r12 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x029d }
            android.content.ContentResolver r12 = r12.getContentResolver()     // Catch:{ Exception -> 0x029d }
            java.util.ArrayList r13 = new java.util.ArrayList     // Catch:{ Exception -> 0x029d }
            r13.<init>()     // Catch:{ Exception -> 0x029d }
            r15 = r13
            android.net.Uri r13 = android.provider.ContactsContract.Groups.CONTENT_URI     // Catch:{ Exception -> 0x029d }
            android.net.Uri$Builder r13 = r13.buildUpon()     // Catch:{ Exception -> 0x029d }
            android.net.Uri$Builder r13 = r13.appendQueryParameter(r8, r7)     // Catch:{ Exception -> 0x029d }
            android.net.Uri r13 = r13.build()     // Catch:{ Exception -> 0x029d }
            r14 = r13
            android.net.Uri r13 = android.provider.ContactsContract.RawContacts.CONTENT_URI     // Catch:{ Exception -> 0x029d }
            android.net.Uri$Builder r13 = r13.buildUpon()     // Catch:{ Exception -> 0x029d }
            android.net.Uri$Builder r7 = r13.appendQueryParameter(r8, r7)     // Catch:{ Exception -> 0x029d }
            android.net.Uri r7 = r7.build()     // Catch:{ Exception -> 0x029d }
            java.lang.String r8 = "_id"
            java.lang.String[] r8 = new java.lang.String[]{r8}     // Catch:{ Exception -> 0x029d }
            java.lang.String r16 = "title=? AND account_type=? AND account_name=?"
            r13 = 3
            r19 = r9
            java.lang.String[] r9 = new java.lang.String[r13]     // Catch:{ Exception -> 0x029d }
            r4 = 0
            r9[r4] = r6     // Catch:{ Exception -> 0x0298 }
            android.accounts.Account r13 = r1.systemAccount     // Catch:{ Exception -> 0x0298 }
            java.lang.String r13 = r13.type     // Catch:{ Exception -> 0x0298 }
            r4 = 1
            r9[r4] = r13     // Catch:{ Exception -> 0x0298 }
            android.accounts.Account r13 = r1.systemAccount     // Catch:{ Exception -> 0x0298 }
            java.lang.String r13 = r13.name     // Catch:{ Exception -> 0x0298 }
            r4 = 2
            r9[r4] = r13     // Catch:{ Exception -> 0x0298 }
            r18 = 0
            r20 = 3
            r13 = r12
            r21 = r14
            r22 = r15
            r15 = r8
            r17 = r9
            android.database.Cursor r8 = r13.query(r14, r15, r16, r17, r18)     // Catch:{ Exception -> 0x0298 }
            java.lang.String r9 = "account_name"
            java.lang.String r15 = "account_type"
            if (r8 == 0) goto L_0x0090
            boolean r13 = r8.moveToFirst()     // Catch:{ Exception -> 0x0298 }
            if (r13 == 0) goto L_0x0090
            r6 = 0
            int r13 = r8.getInt(r6)     // Catch:{ Exception -> 0x0298 }
            r6 = r13
            r16 = r15
            r4 = r21
            goto L_0x00cf
        L_0x0090:
            android.content.ContentValues r13 = new android.content.ContentValues     // Catch:{ Exception -> 0x0298 }
            r13.<init>()     // Catch:{ Exception -> 0x0298 }
            android.accounts.Account r14 = r1.systemAccount     // Catch:{ Exception -> 0x0298 }
            java.lang.String r14 = r14.type     // Catch:{ Exception -> 0x0298 }
            r13.put(r15, r14)     // Catch:{ Exception -> 0x0298 }
            android.accounts.Account r14 = r1.systemAccount     // Catch:{ Exception -> 0x0298 }
            java.lang.String r14 = r14.name     // Catch:{ Exception -> 0x0298 }
            r13.put(r9, r14)     // Catch:{ Exception -> 0x0298 }
            java.lang.String r14 = "group_visible"
            r16 = 0
            java.lang.Integer r4 = java.lang.Integer.valueOf(r16)     // Catch:{ Exception -> 0x0298 }
            r13.put(r14, r4)     // Catch:{ Exception -> 0x0298 }
            java.lang.String r4 = "group_is_read_only"
            r16 = r15
            r14 = 1
            java.lang.Integer r15 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0298 }
            r13.put(r4, r15)     // Catch:{ Exception -> 0x0298 }
            java.lang.String r4 = "title"
            r13.put(r4, r6)     // Catch:{ Exception -> 0x0298 }
            r4 = r21
            android.net.Uri r6 = r12.insert(r4, r13)     // Catch:{ Exception -> 0x0298 }
            java.lang.String r14 = r6.getLastPathSegment()     // Catch:{ Exception -> 0x0298 }
            int r14 = java.lang.Integer.parseInt(r14)     // Catch:{ Exception -> 0x0298 }
            r6 = r14
        L_0x00cf:
            if (r8 == 0) goto L_0x00d4
            r8.close()     // Catch:{ Exception -> 0x0298 }
        L_0x00d4:
            android.net.Uri r14 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0298 }
            java.lang.String[] r15 = new java.lang.String[]{r11}     // Catch:{ Exception -> 0x0298 }
            java.lang.String r17 = "mimetype=? AND data1=?"
            r21 = r4
            r13 = 2
            java.lang.String[] r4 = new java.lang.String[r13]     // Catch:{ Exception -> 0x0298 }
            r13 = 0
            r4[r13] = r5     // Catch:{ Exception -> 0x0298 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0298 }
            r13.<init>()     // Catch:{ Exception -> 0x0298 }
            r13.append(r6)     // Catch:{ Exception -> 0x0298 }
            r13.append(r10)     // Catch:{ Exception -> 0x0298 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0298 }
            r18 = 1
            r4[r18] = r13     // Catch:{ Exception -> 0x0298 }
            r18 = 0
            r13 = r12
            r23 = r8
            r8 = r16
            r16 = r17
            r17 = r4
            android.database.Cursor r4 = r13.query(r14, r15, r16, r17, r18)     // Catch:{ Exception -> 0x0298 }
            int r13 = r22.size()     // Catch:{ Exception -> 0x0298 }
            java.lang.String r14 = "+99084"
            java.lang.String r15 = "vnd.android.cursor.item/phone_v2"
            r16 = r12
            java.lang.String r12 = "data3"
            r17 = r6
            java.lang.String r6 = "data2"
            r18 = r5
            java.lang.String r5 = "vnd.android.cursor.item/name"
            r23 = r11
            java.lang.String r11 = "data1"
            if (r4 == 0) goto L_0x01e0
            boolean r24 = r4.moveToFirst()     // Catch:{ Exception -> 0x01db }
            if (r24 == 0) goto L_0x01e0
            r8 = 0
            int r9 = r4.getInt(r8)     // Catch:{ Exception -> 0x01db }
            r8 = r9
            android.content.ContentProviderOperation$Builder r9 = android.content.ContentProviderOperation.newUpdate(r7)     // Catch:{ Exception -> 0x01db }
            r24 = r4
            java.lang.String r4 = "_id=?"
            r25 = r13
            r13 = 1
            java.lang.String[] r1 = new java.lang.String[r13]     // Catch:{ Exception -> 0x01db }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01db }
            r13.<init>()     // Catch:{ Exception -> 0x01db }
            r13.append(r8)     // Catch:{ Exception -> 0x01db }
            r13.append(r10)     // Catch:{ Exception -> 0x01db }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x01db }
            r18 = 0
            r1[r18] = r13     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation$Builder r1 = r9.withSelection(r4, r1)     // Catch:{ Exception -> 0x01db }
            java.lang.String r4 = "deleted"
            java.lang.Integer r9 = java.lang.Integer.valueOf(r18)     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation$Builder r1 = r1.withValue(r4, r9)     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation r1 = r1.build()     // Catch:{ Exception -> 0x01db }
            r4 = r22
            r4.add(r1)     // Catch:{ Exception -> 0x01db }
            android.net.Uri r1 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation$Builder r1 = android.content.ContentProviderOperation.newUpdate(r1)     // Catch:{ Exception -> 0x01db }
            r9 = 2
            java.lang.String[] r13 = new java.lang.String[r9]     // Catch:{ Exception -> 0x01db }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01db }
            r9.<init>()     // Catch:{ Exception -> 0x01db }
            r9.append(r8)     // Catch:{ Exception -> 0x01db }
            r9.append(r10)     // Catch:{ Exception -> 0x01db }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x01db }
            r18 = 0
            r13[r18] = r9     // Catch:{ Exception -> 0x01db }
            r9 = 1
            r13[r9] = r15     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation$Builder r1 = r1.withSelection(r0, r13)     // Catch:{ Exception -> 0x01db }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01db }
            r9.<init>()     // Catch:{ Exception -> 0x01db }
            r9.append(r14)     // Catch:{ Exception -> 0x01db }
            r9.append(r2)     // Catch:{ Exception -> 0x01db }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation$Builder r1 = r1.withValue(r11, r9)     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation r1 = r1.build()     // Catch:{ Exception -> 0x01db }
            r4.add(r1)     // Catch:{ Exception -> 0x01db }
            android.net.Uri r1 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation$Builder r1 = android.content.ContentProviderOperation.newUpdate(r1)     // Catch:{ Exception -> 0x01db }
            r9 = 2
            java.lang.String[] r9 = new java.lang.String[r9]     // Catch:{ Exception -> 0x01db }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01db }
            r11.<init>()     // Catch:{ Exception -> 0x01db }
            r11.append(r8)     // Catch:{ Exception -> 0x01db }
            r11.append(r10)     // Catch:{ Exception -> 0x01db }
            java.lang.String r10 = r11.toString()     // Catch:{ Exception -> 0x01db }
            r11 = 0
            r9[r11] = r10     // Catch:{ Exception -> 0x01db }
            r10 = 1
            r9[r10] = r5     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation$Builder r0 = r1.withSelection(r0, r9)     // Catch:{ Exception -> 0x01db }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r6, r3)     // Catch:{ Exception -> 0x01db }
            r1 = r29
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r12, r1)     // Catch:{ Exception -> 0x0294 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0294 }
            r4.add(r0)     // Catch:{ Exception -> 0x0294 }
            r10 = r26
            r8 = r25
            goto L_0x0284
        L_0x01db:
            r0 = move-exception
            r1 = r29
            goto L_0x0295
        L_0x01e0:
            r1 = r29
            r24 = r4
            r25 = r13
            r4 = r22
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r7)     // Catch:{ Exception -> 0x0294 }
            r10 = r26
            android.accounts.Account r13 = r10.systemAccount     // Catch:{ Exception -> 0x0292 }
            java.lang.String r13 = r13.type     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r8, r13)     // Catch:{ Exception -> 0x0292 }
            android.accounts.Account r8 = r10.systemAccount     // Catch:{ Exception -> 0x0292 }
            java.lang.String r8 = r8.name     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r9, r8)     // Catch:{ Exception -> 0x0292 }
            java.lang.String r8 = "raw_contact_is_read_only"
            r9 = 1
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r8, r9)     // Catch:{ Exception -> 0x0292 }
            java.lang.String r8 = "aggregation_mode"
            java.lang.Integer r9 = java.lang.Integer.valueOf(r20)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r8, r9)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0292 }
            r4.add(r0)     // Catch:{ Exception -> 0x0292 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x0292 }
            r9 = r23
            r8 = r25
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r9, r8)     // Catch:{ Exception -> 0x0292 }
            r13 = r19
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r13, r5)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r6, r3)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r12, r1)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0292 }
            r4.add(r0)     // Catch:{ Exception -> 0x0292 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r9, r8)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r13, r15)     // Catch:{ Exception -> 0x0292 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0292 }
            r5.<init>()     // Catch:{ Exception -> 0x0292 }
            r5.append(r14)     // Catch:{ Exception -> 0x0292 }
            r5.append(r2)     // Catch:{ Exception -> 0x0292 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r11, r5)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0292 }
            r4.add(r0)     // Catch:{ Exception -> 0x0292 }
            android.net.Uri r0 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValueBackReference(r9, r8)     // Catch:{ Exception -> 0x0292 }
            r5 = r18
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r13, r5)     // Catch:{ Exception -> 0x0292 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r17)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation$Builder r0 = r0.withValue(r11, r5)     // Catch:{ Exception -> 0x0292 }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Exception -> 0x0292 }
            r4.add(r0)     // Catch:{ Exception -> 0x0292 }
        L_0x0284:
            if (r24 == 0) goto L_0x0289
            r24.close()     // Catch:{ Exception -> 0x0292 }
        L_0x0289:
            java.lang.String r0 = "com.android.contacts"
            r5 = r16
            r5.applyBatch(r0, r4)     // Catch:{ Exception -> 0x0292 }
            goto L_0x02a3
        L_0x0292:
            r0 = move-exception
            goto L_0x02a0
        L_0x0294:
            r0 = move-exception
        L_0x0295:
            r10 = r26
            goto L_0x02a0
        L_0x0298:
            r0 = move-exception
            r10 = r1
            r1 = r29
            goto L_0x02a0
        L_0x029d:
            r0 = move-exception
            r10 = r1
            r1 = r4
        L_0x02a0:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ContactsController.createOrUpdateConnectionServiceContact(int, java.lang.String, java.lang.String):void");
    }

    public void deleteConnectionServiceContact() {
        if (hasContactsPermission()) {
            try {
                ContentResolver resolver = ApplicationLoader.applicationContext.getContentResolver();
                ContentResolver contentResolver = resolver;
                Cursor cursor = contentResolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{"_id"}, "title=? AND account_type=? AND account_name=?", new String[]{"AppConnectionService", this.systemAccount.type, this.systemAccount.name}, (String) null);
                if (cursor != null && cursor.moveToFirst()) {
                    int groupID = cursor.getInt(0);
                    cursor.close();
                    String[] strArr = {"vnd.android.cursor.item/group_membership", groupID + ""};
                    Cursor cursor2 = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{"raw_contact_id"}, "mimetype=? AND data1=?", strArr, (String) null);
                    if (cursor2 != null && cursor2.moveToFirst()) {
                        int contactID = cursor2.getInt(0);
                        cursor2.close();
                        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
                        resolver.delete(uri, "_id=?", new String[]{contactID + ""});
                    } else if (cursor2 != null) {
                        cursor2.close();
                    }
                } else if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception x) {
                FileLog.e((Throwable) x);
            }
        }
    }

    public static String formatName(String firstName, String lastName) {
        if (!TextUtils.isEmpty(firstName)) {
            return firstName.trim();
        }
        return LocaleController.getString("UnKnown", R.string.UnKnown);
    }
}
