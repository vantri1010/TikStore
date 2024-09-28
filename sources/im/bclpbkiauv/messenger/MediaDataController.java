package im.bclpbkiauv.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutManager;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.LongSparseArray;
import android.util.SparseArray;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.support.SparseLongArray;
import im.bclpbkiauv.sqlite.SQLiteCursor;
import im.bclpbkiauv.sqlite.SQLiteDatabase;
import im.bclpbkiauv.sqlite.SQLitePreparedStatement;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.NativeByteBuffer;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.TextStyleSpan;
import im.bclpbkiauv.ui.components.URLSpanReplacement;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MediaDataController extends BaseController {
    private static volatile MediaDataController[] Instance = new MediaDataController[3];
    public static final int MEDIA_AUDIO = 2;
    public static final int MEDIA_FILE = 1;
    public static final int MEDIA_MUSIC = 4;
    public static final int MEDIA_PHOTOVIDEO = 0;
    public static final int MEDIA_TYPES_COUNT = 5;
    public static final int MEDIA_URL = 3;
    public static final int TYPE_EMOJI = 4;
    public static final int TYPE_FAVE = 2;
    public static final int TYPE_FEATURED = 3;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    private static RectF bitmapRect;
    private static Comparator<TLRPC.MessageEntity> entityComparator = $$Lambda$MediaDataController$IwoHRXhvAPRxx0i8JLrMMvzjdf4.INSTANCE;
    private static Paint erasePaint;
    public static long installingStickerSetId = -1;
    private static Paint roundPaint;
    private static Path roundPath;
    private HashMap<String, ArrayList<TLRPC.Document>> allStickers = new HashMap<>();
    private HashMap<String, ArrayList<TLRPC.Document>> allStickersFeatured = new HashMap<>();
    private int[] archivedStickersCount = new int[2];
    private SparseArray<TLRPC.BotInfo> botInfos = new SparseArray<>();
    private LongSparseArray<TLRPC.Message> botKeyboards = new LongSparseArray<>();
    private SparseLongArray botKeyboardsByMids = new SparseLongArray();
    private HashMap<String, Boolean> currentFetchingEmoji = new HashMap<>();
    private LongSparseArray<TLRPC.Message> draftMessages = new LongSparseArray<>();
    private LongSparseArray<TLRPC.DraftMessage> drafts = new LongSparseArray<>();
    private ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = new ArrayList<>();
    private LongSparseArray<TLRPC.StickerSetCovered> featuredStickerSetsById = new LongSparseArray<>();
    private boolean featuredStickersLoaded;
    private LongSparseArray<TLRPC.TL_messages_stickerSet> groupStickerSets = new LongSparseArray<>();
    public ArrayList<TLRPC.TL_topPeer> hints = new ArrayList<>();
    private boolean inTransaction;
    public ArrayList<TLRPC.TL_topPeer> inlineBots = new ArrayList<>();
    private LongSparseArray<TLRPC.TL_messages_stickerSet> installedStickerSetsById = new LongSparseArray<>();
    private long lastMergeDialogId;
    private int lastReqId;
    private int lastReturnedNum;
    private String lastSearchQuery;
    private int[] loadDate = new int[5];
    private int loadFeaturedDate;
    private int loadFeaturedHash;
    private int[] loadHash = new int[5];
    boolean loaded;
    boolean loading;
    private boolean loadingDrafts;
    private boolean loadingFeaturedStickers;
    private boolean loadingRecentGifs;
    private boolean[] loadingRecentStickers = new boolean[3];
    private boolean[] loadingStickers = new boolean[5];
    private int mergeReqId;
    private int[] messagesSearchCount = {0, 0};
    private boolean[] messagesSearchEndReached = {false, false};
    private SharedPreferences preferences;
    private ArrayList<Long> readingStickerSets = new ArrayList<>();
    private ArrayList<TLRPC.Document> recentGifs = new ArrayList<>();
    private boolean recentGifsLoaded;
    private ArrayList<TLRPC.Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private boolean[] recentStickersLoaded = new boolean[3];
    private int reqId;
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private SparseArray<MessageObject>[] searchResultMessagesMap = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(0), new ArrayList<>(), new ArrayList<>()};
    private LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsById = new LongSparseArray<>();
    private HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByName = new HashMap<>();
    private LongSparseArray<String> stickersByEmoji = new LongSparseArray<>();
    private LongSparseArray<TLRPC.Document>[] stickersByIds = {new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>()};
    private boolean[] stickersLoaded = new boolean[5];
    private ArrayList<Long> unreadStickerSets = new ArrayList<>();

    public static class KeywordResult {
        public String emoji;
        public String keyword;
    }

    public interface KeywordResultCallback {
        void run(ArrayList<KeywordResult> arrayList, String str);
    }

    public static MediaDataController getInstance(int num) {
        MediaDataController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MediaDataController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    MediaDataController[] mediaDataControllerArr = Instance;
                    MediaDataController mediaDataController = new MediaDataController(num);
                    localInstance = mediaDataController;
                    mediaDataControllerArr[num] = mediaDataController;
                }
            }
        }
        return localInstance;
    }

    public MediaDataController(int num) {
        super(num);
        if (this.currentAccount == 0) {
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            this.preferences = context.getSharedPreferences("drafts" + this.currentAccount, 0);
        }
        for (Map.Entry<String, ?> entry : this.preferences.getAll().entrySet()) {
            try {
                String key = entry.getKey();
                long did = Utilities.parseLong(key).longValue();
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes((String) entry.getValue()));
                if (key.startsWith("r_")) {
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    message.readAttachPath(serializedData, getUserConfig().clientUserId);
                    if (message != null) {
                        this.draftMessages.put(did, message);
                    }
                } else {
                    TLRPC.DraftMessage draftMessage = TLRPC.DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (draftMessage != null) {
                        this.drafts.put(did, draftMessage);
                    }
                }
                serializedData.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void cleanup() {
        for (int a = 0; a < 3; a++) {
            this.recentStickers[a].clear();
            this.loadingRecentStickers[a] = false;
            this.recentStickersLoaded[a] = false;
        }
        for (int a2 = 0; a2 < 4; a2++) {
            this.loadHash[a2] = 0;
            this.loadDate[a2] = 0;
            this.stickerSets[a2].clear();
            this.loadingStickers[a2] = false;
            this.stickersLoaded[a2] = false;
        }
        this.featuredStickerSets.clear();
        this.loadFeaturedDate = 0;
        this.loadFeaturedHash = 0;
        this.allStickers.clear();
        this.allStickersFeatured.clear();
        this.stickersByEmoji.clear();
        this.featuredStickerSetsById.clear();
        this.featuredStickerSets.clear();
        this.unreadStickerSets.clear();
        this.recentGifs.clear();
        this.stickerSetsById.clear();
        this.installedStickerSetsById.clear();
        this.stickerSetsByName.clear();
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = false;
        this.loadingRecentGifs = false;
        this.recentGifsLoaded = false;
        this.currentFetchingEmoji.clear();
        if (Build.VERSION.SDK_INT >= 25) {
            Utilities.globalQueue.postRunnable($$Lambda$MediaDataController$tVzJyI6vEwQAloZN3NNP95hVyKg.INSTANCE);
        }
        this.loading = false;
        this.loaded = false;
        this.hints.clear();
        this.inlineBots.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        this.drafts.clear();
        this.draftMessages.clear();
        this.preferences.edit().clear().commit();
        this.botInfos.clear();
        this.botKeyboards.clear();
        this.botKeyboardsByMids.clear();
    }

    static /* synthetic */ void lambda$cleanup$0() {
        try {
            ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).removeAllDynamicShortcuts();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void checkStickers(int type) {
        if (this.loadingStickers[type]) {
            return;
        }
        if (!this.stickersLoaded[type] || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadDate[type])) >= 3600) {
            loadStickers(type, true, false);
        }
    }

    public void checkFeaturedStickers() {
        if (this.loadingFeaturedStickers) {
            return;
        }
        if (!this.featuredStickersLoaded || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadFeaturedDate)) >= 3600) {
            loadFeaturedStickers(true, false);
        }
    }

    public ArrayList<TLRPC.Document> getRecentStickers(int type) {
        ArrayList<TLRPC.Document> arrayList = this.recentStickers[type];
        return new ArrayList<>(arrayList.subList(0, Math.min(arrayList.size(), 20)));
    }

    public ArrayList<TLRPC.Document> getRecentStickersNoCopy(int type) {
        return this.recentStickers[type];
    }

    public boolean isStickerInFavorites(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < this.recentStickers[2].size(); a++) {
            TLRPC.Document d = this.recentStickers[2].get(a);
            if (d.id == document.id && d.dc_id == document.dc_id) {
                return true;
            }
        }
        return false;
    }

    public void addRecentSticker(int type, Object parentObject, TLRPC.Document document, int date, boolean remove) {
        boolean found;
        int maxCount;
        TLRPC.Document old;
        int i = type;
        TLRPC.Document document2 = document;
        boolean z = remove;
        if (MessageObject.isStickerDocument(document)) {
            int a = 0;
            while (true) {
                if (a >= this.recentStickers[i].size()) {
                    found = false;
                    break;
                }
                TLRPC.Document image = this.recentStickers[i].get(a);
                if (image.id == document2.id) {
                    this.recentStickers[i].remove(a);
                    if (!z) {
                        this.recentStickers[i].add(0, image);
                    }
                    found = true;
                } else {
                    a++;
                }
            }
            if (!found && !z) {
                this.recentStickers[i].add(0, document2);
            }
            if (i == 2) {
                if (z) {
                    ToastUtils.show((int) R.string.RemovedFromFavorites);
                } else {
                    ToastUtils.show((int) R.string.AddedToFavorites);
                }
                TLRPC.TL_messages_faveSticker req = new TLRPC.TL_messages_faveSticker();
                req.id = new TLRPC.TL_inputDocument();
                req.id.id = document2.id;
                req.id.access_hash = document2.access_hash;
                req.id.file_reference = document2.file_reference;
                if (req.id.file_reference == null) {
                    req.id.file_reference = new byte[0];
                }
                req.unfave = z;
                getConnectionsManager().sendRequest(req, new RequestDelegate(parentObject, req) {
                    private final /* synthetic */ Object f$1;
                    private final /* synthetic */ TLRPC.TL_messages_faveSticker f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$addRecentSticker$1$MediaDataController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
                maxCount = getMessagesController().maxFaveStickersCount;
            } else {
                Object obj = parentObject;
                maxCount = getMessagesController().maxRecentStickersCount;
            }
            if (this.recentStickers[i].size() > maxCount || z) {
                if (z) {
                    old = document2;
                } else {
                    ArrayList<TLRPC.Document>[] arrayListArr = this.recentStickers;
                    old = arrayListArr[i].remove(arrayListArr[i].size() - 1);
                }
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, old) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ TLRPC.Document f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$addRecentSticker$2$MediaDataController(this.f$1, this.f$2);
                    }
                });
            }
            if (!z) {
                ArrayList<TLRPC.Document> arrayList = new ArrayList<>();
                arrayList.add(document2);
                ArrayList<TLRPC.Document> arrayList2 = arrayList;
                processLoadedRecentDocuments(type, arrayList, false, date, false);
            }
            if (i == 2) {
                getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, false, Integer.valueOf(type));
            }
        }
    }

    public /* synthetic */ void lambda$addRecentSticker$1$MediaDataController(Object parentObject, TLRPC.TL_messages_faveSticker req, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text) && parentObject != null) {
            getFileRefController().requestReference(parentObject, req);
        }
    }

    public /* synthetic */ void lambda$addRecentSticker$2$MediaDataController(int type, TLRPC.Document old) {
        int cacheType;
        if (type == 0) {
            cacheType = 3;
        } else if (type == 1) {
            cacheType = 4;
        } else {
            cacheType = 5;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "' AND type = " + cacheType).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public ArrayList<TLRPC.Document> getRecentGifs() {
        return new ArrayList<>(this.recentGifs);
    }

    public void removeRecentGifById(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < this.recentGifs.size(); i++) {
                TLRPC.Document realDocument = this.recentGifs.get(i);
                if (realDocument.id == document.id) {
                    removeRecentGif(realDocument);
                    return;
                }
            }
        }
    }

    public void removeRecentGif(TLRPC.Document document) {
        this.recentGifs.remove(document);
        TLRPC.TL_messages_saveGif req = new TLRPC.TL_messages_saveGif();
        req.id = new TLRPC.TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.id.file_reference = document.file_reference;
        if (req.id.file_reference == null) {
            req.id.file_reference = new byte[0];
        }
        req.unsave = true;
        getConnectionsManager().sendRequest(req, new RequestDelegate(req) {
            private final /* synthetic */ TLRPC.TL_messages_saveGif f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.lambda$removeRecentGif$3$MediaDataController(this.f$1, tLObject, tL_error);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(document) {
            private final /* synthetic */ TLRPC.Document f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$removeRecentGif$4$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$removeRecentGif$3$MediaDataController(TLRPC.TL_messages_saveGif req, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text)) {
            getFileRefController().requestReference("gif", req);
        }
    }

    public /* synthetic */ void lambda$removeRecentGif$4$MediaDataController(TLRPC.Document document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + document.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean hasRecentGif(TLRPC.Document document) {
        for (int a = 0; a < this.recentGifs.size(); a++) {
            TLRPC.Document image = this.recentGifs.get(a);
            if (image.id == document.id) {
                this.recentGifs.remove(a);
                this.recentGifs.add(0, image);
                return true;
            }
        }
        return false;
    }

    public boolean hasRecentGifNoChangeINdex(TLRPC.Document document) {
        for (int a = 0; a < this.recentGifs.size(); a++) {
            if (this.recentGifs.get(a).id == document.id) {
                return true;
            }
        }
        return false;
    }

    public void addRecentGif(TLRPC.Document document, int date) {
        boolean found = false;
        int a = 0;
        while (true) {
            if (a >= this.recentGifs.size()) {
                break;
            }
            TLRPC.Document image = this.recentGifs.get(a);
            if (image.id == document.id) {
                this.recentGifs.remove(a);
                this.recentGifs.add(0, image);
                found = true;
                break;
            }
            a++;
        }
        if (!found) {
            this.recentGifs.add(0, document);
        }
        if (this.recentGifs.size() > getMessagesController().maxRecentGifsCount) {
            ArrayList<TLRPC.Document> arrayList = this.recentGifs;
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList.remove(arrayList.size() - 1)) {
                private final /* synthetic */ TLRPC.Document f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$addRecentGif$5$MediaDataController(this.f$1);
                }
            });
        }
        ArrayList<TLRPC.Document> arrayList2 = new ArrayList<>();
        arrayList2.add(document);
        processLoadedRecentDocuments(0, arrayList2, true, date, false);
    }

    public /* synthetic */ void lambda$addRecentGif$5$MediaDataController(TLRPC.Document old) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean isLoadingStickers(int type) {
        return this.loadingStickers[type];
    }

    public void replaceStickerSet(TLRPC.TL_messages_stickerSet set) {
        TLRPC.TL_messages_stickerSet existingSet = this.stickerSetsById.get(set.set.id);
        boolean isGroupSet = false;
        if (existingSet == null) {
            existingSet = this.stickerSetsByName.get(set.set.short_name);
        }
        if (existingSet == null && (existingSet = this.groupStickerSets.get(set.set.id)) != null) {
            isGroupSet = true;
        }
        if (existingSet != null) {
            boolean changed = false;
            if ("AnimatedEmojies".equals(set.set.short_name)) {
                changed = true;
                existingSet.documents = set.documents;
                existingSet.packs = set.packs;
                existingSet.set = set.set;
                AndroidUtilities.runOnUIThread(new Runnable(set) {
                    private final /* synthetic */ TLRPC.TL_messages_stickerSet f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$replaceStickerSet$6$MediaDataController(this.f$1);
                    }
                });
            } else {
                LongSparseArray<TLRPC.Document> documents = new LongSparseArray<>();
                int size = set.documents.size();
                for (int a = 0; a < size; a++) {
                    TLRPC.Document document = set.documents.get(a);
                    documents.put(document.id, document);
                }
                int size2 = existingSet.documents.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    TLRPC.Document newDocument = documents.get(set.documents.get(a2).id);
                    if (newDocument != null) {
                        existingSet.documents.set(a2, newDocument);
                        changed = true;
                    }
                }
            }
            if (!changed) {
                return;
            }
            if (isGroupSet) {
                putSetToCache(existingSet);
                return;
            }
            int type = set.set.masks;
            putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
            if ("AnimatedEmojies".equals(set.set.short_name)) {
                putStickersToCache(4, this.stickerSets[4], this.loadDate[4], this.loadHash[4]);
            }
        }
    }

    public /* synthetic */ void lambda$replaceStickerSet$6$MediaDataController(TLRPC.TL_messages_stickerSet set) {
        LongSparseArray<TLRPC.Document> stickersById = getStickerByIds(4);
        for (int b = 0; b < set.documents.size(); b++) {
            TLRPC.Document document = set.documents.get(b);
            stickersById.put(document.id, document);
        }
    }

    public TLRPC.TL_messages_stickerSet getStickerSetByName(String name) {
        return this.stickerSetsByName.get(name);
    }

    public TLRPC.TL_messages_stickerSet getStickerSetById(long id) {
        return this.stickerSetsById.get(id);
    }

    public TLRPC.TL_messages_stickerSet getGroupStickerSetById(TLRPC.StickerSet stickerSet) {
        TLRPC.TL_messages_stickerSet set = this.stickerSetsById.get(stickerSet.id);
        if (set == null) {
            set = this.groupStickerSets.get(stickerSet.id);
            if (set == null || set.set == null) {
                loadGroupStickerSet(stickerSet, true);
            } else if (set.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
        }
        return set;
    }

    public void putGroupStickerSet(TLRPC.TL_messages_stickerSet stickerSet) {
        this.groupStickerSets.put(stickerSet.set.id, stickerSet);
    }

    private void loadGroupStickerSet(TLRPC.StickerSet stickerSet, boolean cache) {
        if (cache) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(stickerSet) {
                private final /* synthetic */ TLRPC.StickerSet f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$loadGroupStickerSet$8$MediaDataController(this.f$1);
                }
            });
            return;
        }
        TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
        req.stickerset = new TLRPC.TL_inputStickerSetID();
        req.stickerset.id = stickerSet.id;
        req.stickerset.access_hash = stickerSet.access_hash;
        getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.lambda$loadGroupStickerSet$10$MediaDataController(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$8$MediaDataController(TLRPC.StickerSet stickerSet) {
        TLRPC.TL_messages_stickerSet set;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT document FROM web_recent_v3 WHERE id = 's_" + stickerSet.id + "'", new Object[0]);
            if (!cursor.next() || cursor.isNull(0)) {
                set = null;
            } else {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    set = TLRPC.TL_messages_stickerSet.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                } else {
                    set = null;
                }
            }
            cursor.dispose();
            if (set == null || set.set == null || set.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
            if (set != null && set.set != null) {
                AndroidUtilities.runOnUIThread(new Runnable(set) {
                    private final /* synthetic */ TLRPC.TL_messages_stickerSet f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$7$MediaDataController(this.f$1);
                    }
                });
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$7$MediaDataController(TLRPC.TL_messages_stickerSet set) {
        this.groupStickerSets.put(set.set.id, set);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(set.set.id));
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$10$MediaDataController(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_messages_stickerSet) response) {
                private final /* synthetic */ TLRPC.TL_messages_stickerSet f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$9$MediaDataController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$9$MediaDataController(TLRPC.TL_messages_stickerSet set) {
        this.groupStickerSets.put(set.set.id, set);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(set.set.id));
    }

    private void putSetToCache(TLRPC.TL_messages_stickerSet set) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(set) {
            private final /* synthetic */ TLRPC.TL_messages_stickerSet f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$putSetToCache$11$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$putSetToCache$11$MediaDataController(TLRPC.TL_messages_stickerSet set) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            state.requery();
            state.bindString(1, "s_" + set.set.id);
            state.bindInteger(2, 6);
            state.bindString(3, "");
            state.bindString(4, "");
            state.bindString(5, "");
            state.bindInteger(6, 0);
            state.bindInteger(7, 0);
            state.bindInteger(8, 0);
            state.bindInteger(9, 0);
            NativeByteBuffer data = new NativeByteBuffer(set.getObjectSize());
            set.serializeToStream(data);
            state.bindByteBuffer(10, data);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public HashMap<String, ArrayList<TLRPC.Document>> getAllStickers() {
        return this.allStickers;
    }

    public HashMap<String, ArrayList<TLRPC.Document>> getAllStickersFeatured() {
        return this.allStickersFeatured;
    }

    public TLRPC.Document getEmojiAnimatedSticker(CharSequence message) {
        String emoji = message.toString().replace("️", "");
        ArrayList<TLRPC.TL_messages_stickerSet> arrayList = getStickerSets(4);
        int N = arrayList.size();
        for (int a = 0; a < N; a++) {
            TLRPC.TL_messages_stickerSet set = arrayList.get(a);
            int N2 = set.packs.size();
            for (int b = 0; b < N2; b++) {
                TLRPC.TL_stickerPack pack = set.packs.get(b);
                if (!pack.documents.isEmpty() && TextUtils.equals(pack.emoticon, emoji)) {
                    return getStickerByIds(4).get(pack.documents.get(0).longValue());
                }
            }
        }
        return null;
    }

    public boolean canAddStickerToFavorites() {
        return !this.stickersLoaded[0] || this.stickerSets[0].size() >= 5 || !this.recentStickers[2].isEmpty();
    }

    public ArrayList<TLRPC.TL_messages_stickerSet> getStickerSets(int type) {
        if (type == 3) {
            return this.stickerSets[2];
        }
        return this.stickerSets[type];
    }

    public LongSparseArray<TLRPC.Document> getStickerByIds(int type) {
        return this.stickersByIds[type];
    }

    public ArrayList<TLRPC.StickerSetCovered> getFeaturedStickerSets() {
        return this.featuredStickerSets;
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets;
    }

    public boolean areAllTrendingStickerSetsUnread() {
        int N = this.featuredStickerSets.size();
        for (int a = 0; a < N; a++) {
            TLRPC.StickerSetCovered pack = this.featuredStickerSets.get(a);
            if (!isStickerPackInstalled(pack.set.id) && ((!pack.covers.isEmpty() || pack.cover != null) && !this.unreadStickerSets.contains(Long.valueOf(pack.set.id)))) {
                return false;
            }
        }
        return true;
    }

    public boolean isStickerPackInstalled(long id) {
        return this.installedStickerSetsById.indexOfKey(id) >= 0;
    }

    public boolean isStickerPackUnread(long id) {
        return this.unreadStickerSets.contains(Long.valueOf(id));
    }

    public boolean isStickerPackInstalled(String name) {
        return this.stickerSetsByName.containsKey(name);
    }

    public String getEmojiForSticker(long id) {
        String value = this.stickersByEmoji.get(id);
        return value != null ? value : "";
    }

    private static int calcDocumentsHash(ArrayList<TLRPC.Document> arrayList) {
        if (arrayList == null) {
            return 0;
        }
        long acc = 0;
        for (int a = 0; a < Math.min(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, arrayList.size()); a++) {
            TLRPC.Document document = arrayList.get(a);
            if (document != null) {
                acc = (((20261 * ((((acc * 20261) + 2147483648L) + ((long) ((int) (document.id >> 32)))) % 2147483648L)) + 2147483648L) + ((long) ((int) document.id))) % 2147483648L;
            }
        }
        return (int) acc;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_getFavedStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadRecents(int r11, boolean r12, boolean r13, boolean r14) {
        /*
            r10 = this;
            r0 = 1
            if (r12 == 0) goto L_0x0010
            boolean r1 = r10.loadingRecentGifs
            if (r1 == 0) goto L_0x0008
            return
        L_0x0008:
            r10.loadingRecentGifs = r0
            boolean r1 = r10.recentGifsLoaded
            if (r1 == 0) goto L_0x0020
            r13 = 0
            goto L_0x0020
        L_0x0010:
            boolean[] r1 = r10.loadingRecentStickers
            boolean r2 = r1[r11]
            if (r2 == 0) goto L_0x0017
            return
        L_0x0017:
            r1[r11] = r0
            boolean[] r1 = r10.recentStickersLoaded
            boolean r1 = r1[r11]
            if (r1 == 0) goto L_0x0020
            r13 = 0
        L_0x0020:
            if (r13 == 0) goto L_0x0034
            im.bclpbkiauv.messenger.MessagesStorage r0 = r10.getMessagesStorage()
            im.bclpbkiauv.messenger.DispatchQueue r0 = r0.getStorageQueue()
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$p2WmBsjqtwfXi5v2jMYpoa0ubfg r1 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$p2WmBsjqtwfXi5v2jMYpoa0ubfg
            r1.<init>(r12, r11)
            r0.postRunnable(r1)
            goto L_0x00cc
        L_0x0034:
            int r1 = r10.currentAccount
            android.content.SharedPreferences r1 = im.bclpbkiauv.messenger.MessagesController.getEmojiSettings(r1)
            r2 = 0
            if (r14 != 0) goto L_0x007a
            r3 = 0
            if (r12 == 0) goto L_0x0048
            java.lang.String r5 = "lastGifLoadTime"
            long r3 = r1.getLong(r5, r3)
            goto L_0x0060
        L_0x0048:
            if (r11 != 0) goto L_0x0051
            java.lang.String r5 = "lastStickersLoadTime"
            long r3 = r1.getLong(r5, r3)
            goto L_0x0060
        L_0x0051:
            if (r11 != r0) goto L_0x005a
            java.lang.String r5 = "lastStickersLoadTimeMask"
            long r3 = r1.getLong(r5, r3)
            goto L_0x0060
        L_0x005a:
            java.lang.String r5 = "lastStickersLoadTimeFavs"
            long r3 = r1.getLong(r5, r3)
        L_0x0060:
            long r5 = java.lang.System.currentTimeMillis()
            long r5 = r5 - r3
            long r5 = java.lang.Math.abs(r5)
            r7 = 3600000(0x36ee80, double:1.7786363E-317)
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x007a
            if (r12 == 0) goto L_0x0075
            r10.loadingRecentGifs = r2
            goto L_0x0079
        L_0x0075:
            boolean[] r0 = r10.loadingRecentStickers
            r0[r11] = r2
        L_0x0079:
            return
        L_0x007a:
            if (r12 == 0) goto L_0x0096
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_getSavedGifs r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_getSavedGifs
            r0.<init>()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r2 = r10.recentGifs
            int r2 = calcDocumentsHash(r2)
            r0.hash = r2
            im.bclpbkiauv.tgnet.ConnectionsManager r2 = r10.getConnectionsManager()
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$aRrgy7tNzUMXfVlRJ9YoKiX5jh0 r3 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$aRrgy7tNzUMXfVlRJ9YoKiX5jh0
            r3.<init>(r11, r12)
            r2.sendRequest(r0, r3)
            goto L_0x00cc
        L_0x0096:
            r3 = 2
            if (r11 != r3) goto L_0x00aa
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_getFavedStickers r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_getFavedStickers
            r0.<init>()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document>[] r2 = r10.recentStickers
            r2 = r2[r11]
            int r2 = calcDocumentsHash(r2)
            r0.hash = r2
            goto L_0x00c0
        L_0x00aa:
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_getRecentStickers r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_getRecentStickers
            r3.<init>()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document>[] r4 = r10.recentStickers
            r4 = r4[r11]
            int r4 = calcDocumentsHash(r4)
            r3.hash = r4
            if (r11 != r0) goto L_0x00bc
            goto L_0x00bd
        L_0x00bc:
            r0 = 0
        L_0x00bd:
            r3.attached = r0
            r0 = r3
        L_0x00c0:
            im.bclpbkiauv.tgnet.ConnectionsManager r2 = r10.getConnectionsManager()
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$XXuXlF4P_Xo7ix7PNOIydln7lN0 r3 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$XXuXlF4P_Xo7ix7PNOIydln7lN0
            r3.<init>(r11, r12)
            r2.sendRequest(r0, r3)
        L_0x00cc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.loadRecents(int, boolean, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$loadRecents$13$MediaDataController(boolean gif, int type) {
        int cacheType;
        if (gif) {
            cacheType = 2;
        } else if (type == 0) {
            cacheType = 3;
        } else if (type == 1) {
            cacheType = 4;
        } else {
            cacheType = 5;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor cursor = database.queryFinalized("SELECT document FROM web_recent_v3 WHERE type = " + cacheType + " ORDER BY date DESC", new Object[0]);
            ArrayList<TLRPC.Document> arrayList = new ArrayList<>();
            while (cursor.next()) {
                if (!cursor.isNull(0)) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLRPC.Document document = TLRPC.Document.TLdeserialize(data, data.readInt32(false), false);
                        if (document != null) {
                            arrayList.add(document);
                        }
                        data.reuse();
                    }
                }
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new Runnable(gif, arrayList, type) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$12$MediaDataController(this.f$1, this.f$2, this.f$3);
                }
            });
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$12$MediaDataController(boolean gif, ArrayList arrayList, int type) {
        if (gif) {
            this.recentGifs = arrayList;
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
        } else {
            this.recentStickers[type] = arrayList;
            this.loadingRecentStickers[type] = false;
            this.recentStickersLoaded[type] = true;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(gif), Integer.valueOf(type));
        loadRecents(type, gif, false, false);
    }

    public /* synthetic */ void lambda$loadRecents$14$MediaDataController(int type, boolean gif, TLObject response, TLRPC.TL_error error) {
        ArrayList<TLRPC.Document> arrayList = null;
        if (response instanceof TLRPC.TL_messages_savedGifs) {
            arrayList = ((TLRPC.TL_messages_savedGifs) response).gifs;
        }
        processLoadedRecentDocuments(type, arrayList, gif, 0, true);
    }

    public /* synthetic */ void lambda$loadRecents$15$MediaDataController(int type, boolean gif, TLObject response, TLRPC.TL_error error) {
        ArrayList<TLRPC.Document> arrayList = null;
        if (type == 2) {
            if (response instanceof TLRPC.TL_messages_favedStickers) {
                arrayList = ((TLRPC.TL_messages_favedStickers) response).stickers;
            }
        } else if (response instanceof TLRPC.TL_messages_recentStickers) {
            arrayList = ((TLRPC.TL_messages_recentStickers) response).stickers;
        }
        processLoadedRecentDocuments(type, arrayList, gif, 0, true);
    }

    /* access modifiers changed from: protected */
    public void processLoadedRecentDocuments(int type, ArrayList<TLRPC.Document> documents, boolean gif, int date, boolean replace) {
        if (documents != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(gif, type, documents, replace, date) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ ArrayList f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ int f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    MediaDataController.this.lambda$processLoadedRecentDocuments$16$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        }
        if (date == 0) {
            AndroidUtilities.runOnUIThread(new Runnable(gif, type, documents) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$processLoadedRecentDocuments$17$MediaDataController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$processLoadedRecentDocuments$16$MediaDataController(boolean gif, int type, ArrayList documents, boolean replace, int date) {
        int maxCount;
        int cacheType;
        int i = type;
        ArrayList arrayList = documents;
        SQLiteDatabase database = getMessagesStorage().getDatabase();
        if (database != null) {
            if (gif) {
                maxCount = getMessagesController().maxRecentGifsCount;
            } else if (i == 2) {
                maxCount = getMessagesController().maxFaveStickersCount;
            } else {
                maxCount = getMessagesController().maxRecentStickersCount;
            }
            try {
                database.beginTransaction();
            } catch (Exception e) {
                FileLog.e("processLoadedRecentDocuments ---> exception 1 ", (Throwable) e);
            }
            NativeByteBuffer data = null;
            SQLitePreparedStatement state = null;
            try {
                SQLitePreparedStatement state2 = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                int count = documents.size();
                if (gif) {
                    cacheType = 2;
                } else if (i == 0) {
                    cacheType = 3;
                } else if (i == 1) {
                    cacheType = 4;
                } else {
                    cacheType = 5;
                }
                if (replace) {
                    database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + cacheType).stepThis().dispose();
                }
                int a = 0;
                while (true) {
                    if (a >= count) {
                        break;
                    } else if (a == maxCount) {
                        break;
                    } else {
                        TLRPC.Document document = (TLRPC.Document) arrayList.get(a);
                        state2.requery();
                        state2.bindString(1, "" + document.id);
                        state2.bindInteger(2, cacheType);
                        state2.bindString(3, "");
                        state2.bindString(4, "");
                        state2.bindString(5, "");
                        state2.bindInteger(6, 0);
                        state2.bindInteger(7, 0);
                        state2.bindInteger(8, 0);
                        state2.bindInteger(9, date != 0 ? date : count - a);
                        NativeByteBuffer data2 = new NativeByteBuffer(document.getObjectSize());
                        document.serializeToStream(data2);
                        state2.bindByteBuffer(10, data2);
                        state2.step();
                        data2.reuse();
                        data = null;
                        a++;
                    }
                }
                state2.dispose();
                state = null;
                database.commitTransaction();
                if (documents.size() >= maxCount) {
                    try {
                        database.beginTransaction();
                    } catch (Exception e2) {
                        FileLog.e("processLoadedRecentDocuments ---> exception 2 ", (Throwable) e2);
                    }
                    for (int a2 = maxCount; a2 < documents.size(); a2++) {
                        database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC.Document) arrayList.get(a2)).id + "' AND type = " + cacheType).stepThis().dispose();
                    }
                    database.commitTransaction();
                }
                if (data != null) {
                    data.reuse();
                }
                if (0 == 0) {
                    return;
                }
            } catch (Exception e3) {
                FileLog.e("processLoadedRecentDocuments ---> exception 3 ", (Throwable) e3);
                if (data != null) {
                    data.reuse();
                }
                if (state == null) {
                    return;
                }
            } catch (Throwable th) {
                if (data != null) {
                    data.reuse();
                }
                if (state != null) {
                    state.dispose();
                }
                throw th;
            }
            state.dispose();
        }
    }

    public /* synthetic */ void lambda$processLoadedRecentDocuments$17$MediaDataController(boolean gif, int type, ArrayList documents) {
        SharedPreferences.Editor editor = MessagesController.getEmojiSettings(this.currentAccount).edit();
        if (gif) {
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
            editor.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
        } else {
            this.loadingRecentStickers[type] = false;
            this.recentStickersLoaded[type] = true;
            if (type == 0) {
                editor.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
            } else if (type == 1) {
                editor.putLong("lastStickersLoadTimeMask", System.currentTimeMillis()).commit();
            } else {
                editor.putLong("lastStickersLoadTimeFavs", System.currentTimeMillis()).commit();
            }
        }
        if (documents != null) {
            if (gif) {
                this.recentGifs = documents;
            } else {
                this.recentStickers[type] = documents;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(gif), Integer.valueOf(type));
        }
    }

    public void reorderStickers(int type, ArrayList<Long> order) {
        Collections.sort(this.stickerSets[type], new Comparator(order) {
            private final /* synthetic */ ArrayList f$0;

            {
                this.f$0 = r1;
            }

            public final int compare(Object obj, Object obj2) {
                return MediaDataController.lambda$reorderStickers$18(this.f$0, (TLRPC.TL_messages_stickerSet) obj, (TLRPC.TL_messages_stickerSet) obj2);
            }
        });
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
        loadStickers(type, false, true);
    }

    static /* synthetic */ int lambda$reorderStickers$18(ArrayList order, TLRPC.TL_messages_stickerSet lhs, TLRPC.TL_messages_stickerSet rhs) {
        int index1 = order.indexOf(Long.valueOf(lhs.set.id));
        int index2 = order.indexOf(Long.valueOf(rhs.set.id));
        if (index1 > index2) {
            return 1;
        }
        if (index1 < index2) {
            return -1;
        }
        return 0;
    }

    public void calcNewHash(int type) {
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
    }

    public void addNewStickerSet(TLRPC.TL_messages_stickerSet set) {
        if (this.stickerSetsById.indexOfKey(set.set.id) < 0 && !this.stickerSetsByName.containsKey(set.set.short_name)) {
            int type = set.set.masks;
            this.stickerSets[type].add(0, set);
            this.stickerSetsById.put(set.set.id, set);
            this.installedStickerSetsById.put(set.set.id, set);
            this.stickerSetsByName.put(set.set.short_name, set);
            LongSparseArray<TLRPC.Document> stickersById = new LongSparseArray<>();
            for (int a = 0; a < set.documents.size(); a++) {
                TLRPC.Document document = set.documents.get(a);
                stickersById.put(document.id, document);
            }
            for (int a2 = 0; a2 < set.packs.size(); a2++) {
                TLRPC.TL_stickerPack stickerPack = set.packs.get(a2);
                stickerPack.emoticon = stickerPack.emoticon.replace("️", "");
                ArrayList<TLRPC.Document> arrayList = this.allStickers.get(stickerPack.emoticon);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    this.allStickers.put(stickerPack.emoticon, arrayList);
                }
                for (int c = 0; c < stickerPack.documents.size(); c++) {
                    Long id = stickerPack.documents.get(c);
                    if (this.stickersByEmoji.indexOfKey(id.longValue()) < 0) {
                        this.stickersByEmoji.put(id.longValue(), stickerPack.emoticon);
                    }
                    TLRPC.Document sticker = stickersById.get(id.longValue());
                    if (sticker != null) {
                        arrayList.add(sticker);
                    }
                }
            }
            this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
            loadStickers(type, false, true);
        }
    }

    public void loadFeaturedStickers(boolean cache, boolean force) {
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (cache) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                    public final void run() {
                        MediaDataController.this.lambda$loadFeaturedStickers$19$MediaDataController();
                    }
                });
                return;
            }
            TLRPC.TL_messages_getFeaturedStickers req = new TLRPC.TL_messages_getFeaturedStickers();
            req.hash = force ? 0 : this.loadFeaturedHash;
            getConnectionsManager().sendRequest(req, new RequestDelegate(req) {
                private final /* synthetic */ TLRPC.TL_messages_getFeaturedStickers f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$loadFeaturedStickers$21$MediaDataController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0071, code lost:
        if (r4 != null) goto L_0x007a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0078, code lost:
        if (r4 == null) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x007a, code lost:
        r4.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x007d, code lost:
        r10 = r4;
        processLoadedFeaturedStickers(r0, r1, true, r2, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0089, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadFeaturedStickers$19$MediaDataController() {
        /*
            r11 = this;
            r0 = 0
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r2 = 0
            r3 = 0
            r4 = 0
            im.bclpbkiauv.messenger.MessagesStorage r5 = r11.getMessagesStorage()     // Catch:{ all -> 0x0074 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r5 = r5.getDatabase()     // Catch:{ all -> 0x0074 }
            java.lang.String r6 = "SELECT data, unread, date, hash FROM stickers_featured WHERE 1"
            r7 = 0
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x0074 }
            im.bclpbkiauv.sqlite.SQLiteCursor r5 = r5.queryFinalized(r6, r8)     // Catch:{ all -> 0x0074 }
            r4 = r5
            boolean r5 = r4.next()     // Catch:{ all -> 0x0074 }
            if (r5 == 0) goto L_0x0071
            im.bclpbkiauv.tgnet.NativeByteBuffer r5 = r4.byteBufferValue(r7)     // Catch:{ all -> 0x0074 }
            if (r5 == 0) goto L_0x0046
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0074 }
            r6.<init>()     // Catch:{ all -> 0x0074 }
            r0 = r6
            int r6 = r5.readInt32(r7)     // Catch:{ all -> 0x0074 }
            r8 = 0
        L_0x0032:
            if (r8 >= r6) goto L_0x0043
            int r9 = r5.readInt32(r7)     // Catch:{ all -> 0x0074 }
            im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered r9 = im.bclpbkiauv.tgnet.TLRPC.StickerSetCovered.TLdeserialize(r5, r9, r7)     // Catch:{ all -> 0x0074 }
            r0.add(r9)     // Catch:{ all -> 0x0074 }
            int r8 = r8 + 1
            goto L_0x0032
        L_0x0043:
            r5.reuse()     // Catch:{ all -> 0x0074 }
        L_0x0046:
            r6 = 1
            im.bclpbkiauv.tgnet.NativeByteBuffer r6 = r4.byteBufferValue(r6)     // Catch:{ all -> 0x0074 }
            r5 = r6
            if (r5 == 0) goto L_0x0066
            int r6 = r5.readInt32(r7)     // Catch:{ all -> 0x0074 }
            r8 = 0
        L_0x0053:
            if (r8 >= r6) goto L_0x0063
            long r9 = r5.readInt64(r7)     // Catch:{ all -> 0x0074 }
            java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ all -> 0x0074 }
            r1.add(r9)     // Catch:{ all -> 0x0074 }
            int r8 = r8 + 1
            goto L_0x0053
        L_0x0063:
            r5.reuse()     // Catch:{ all -> 0x0074 }
        L_0x0066:
            r6 = 2
            int r6 = r4.intValue(r6)     // Catch:{ all -> 0x0074 }
            r2 = r6
            int r6 = r11.calcFeaturedStickersHash(r0)     // Catch:{ all -> 0x0074 }
            r3 = r6
        L_0x0071:
            if (r4 == 0) goto L_0x007d
            goto L_0x007a
        L_0x0074:
            r5 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x008a }
            if (r4 == 0) goto L_0x007d
        L_0x007a:
            r4.dispose()
        L_0x007d:
            r8 = r2
            r9 = r3
            r10 = r4
            r5 = 1
            r2 = r11
            r3 = r0
            r4 = r1
            r6 = r8
            r7 = r9
            r2.processLoadedFeaturedStickers(r3, r4, r5, r6, r7)
            return
        L_0x008a:
            r5 = move-exception
            if (r4 == 0) goto L_0x0090
            r4.dispose()
        L_0x0090:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.lambda$loadFeaturedStickers$19$MediaDataController():void");
    }

    public /* synthetic */ void lambda$loadFeaturedStickers$21$MediaDataController(TLRPC.TL_messages_getFeaturedStickers req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response, req) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC.TL_messages_getFeaturedStickers f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MediaDataController.this.lambda$null$20$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$20$MediaDataController(TLObject response, TLRPC.TL_messages_getFeaturedStickers req) {
        TLObject tLObject = response;
        if (tLObject instanceof TLRPC.TL_messages_featuredStickers) {
            TLRPC.TL_messages_featuredStickers res = (TLRPC.TL_messages_featuredStickers) tLObject;
            processLoadedFeaturedStickers(res.sets, res.unread, false, (int) (System.currentTimeMillis() / 1000), res.hash);
            TLRPC.TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers = req;
            return;
        }
        processLoadedFeaturedStickers((ArrayList<TLRPC.StickerSetCovered>) null, (ArrayList<Long>) null, false, (int) (System.currentTimeMillis() / 1000), req.hash);
    }

    private void processLoadedFeaturedStickers(ArrayList<TLRPC.StickerSetCovered> res, ArrayList<Long> unreadStickers, boolean cache, int date, int hash) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                MediaDataController.this.lambda$processLoadedFeaturedStickers$22$MediaDataController();
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable(cache, res, date, hash, unreadStickers) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ ArrayList f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                MediaDataController.this.lambda$processLoadedFeaturedStickers$26$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$22$MediaDataController() {
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = true;
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$26$MediaDataController(boolean cache, ArrayList res, int date, int hash, ArrayList unreadStickers) {
        long j = 1000;
        if ((cache && (res == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) date)) >= 3600)) || (!cache && res == null && hash == 0)) {
            $$Lambda$MediaDataController$iXB6Rgf3SBcaULh9PA7GVaxLz9M r2 = new Runnable(res, hash) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$23$MediaDataController(this.f$1, this.f$2);
                }
            };
            if (res != null || cache) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(r2, j);
            if (res == null) {
                return;
            }
        }
        if (res != null) {
            try {
                ArrayList<TLRPC.StickerSetCovered> stickerSetsNew = new ArrayList<>();
                LongSparseArray<TLRPC.StickerSetCovered> stickerSetsByIdNew = new LongSparseArray<>();
                for (int a = 0; a < res.size(); a++) {
                    TLRPC.StickerSetCovered stickerSet = (TLRPC.StickerSetCovered) res.get(a);
                    stickerSetsNew.add(stickerSet);
                    stickerSetsByIdNew.put(stickerSet.set.id, stickerSet);
                }
                if (!cache) {
                    putFeaturedStickersToCache(stickerSetsNew, unreadStickers, date, hash);
                }
                AndroidUtilities.runOnUIThread(new Runnable(unreadStickers, stickerSetsByIdNew, stickerSetsNew, hash, date) {
                    private final /* synthetic */ ArrayList f$1;
                    private final /* synthetic */ LongSparseArray f$2;
                    private final /* synthetic */ ArrayList f$3;
                    private final /* synthetic */ int f$4;
                    private final /* synthetic */ int f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$24$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                    }
                });
            } catch (Throwable e) {
                FileLog.e(e);
            }
        } else if (!cache) {
            AndroidUtilities.runOnUIThread(new Runnable(date) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$25$MediaDataController(this.f$1);
                }
            });
            putFeaturedStickersToCache((ArrayList<TLRPC.StickerSetCovered>) null, (ArrayList<Long>) null, date, 0);
        }
    }

    public /* synthetic */ void lambda$null$23$MediaDataController(ArrayList res, int hash) {
        if (!(res == null || hash == 0)) {
            this.loadFeaturedHash = hash;
        }
        loadFeaturedStickers(false, false);
    }

    public /* synthetic */ void lambda$null$24$MediaDataController(ArrayList unreadStickers, LongSparseArray stickerSetsByIdNew, ArrayList stickerSetsNew, int hash, int date) {
        this.unreadStickerSets = unreadStickers;
        this.featuredStickerSetsById = stickerSetsByIdNew;
        this.featuredStickerSets = stickerSetsNew;
        this.loadFeaturedHash = hash;
        this.loadFeaturedDate = date;
        loadStickers(3, true, false);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
    }

    public /* synthetic */ void lambda$null$25$MediaDataController(int date) {
        this.loadFeaturedDate = date;
    }

    private void putFeaturedStickersToCache(ArrayList<TLRPC.StickerSetCovered> stickers, ArrayList<Long> unreadStickers, int date, int hash) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(stickers != null ? new ArrayList<>(stickers) : null, unreadStickers, date, hash) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$putFeaturedStickersToCache$27$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$putFeaturedStickersToCache$27$MediaDataController(ArrayList stickersFinal, ArrayList unreadStickers, int date, int hash) {
        if (stickersFinal != null) {
            try {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                state.requery();
                int size = 4;
                for (int a = 0; a < stickersFinal.size(); a++) {
                    size += ((TLRPC.StickerSetCovered) stickersFinal.get(a)).getObjectSize();
                }
                NativeByteBuffer data = new NativeByteBuffer(size);
                NativeByteBuffer data2 = new NativeByteBuffer((unreadStickers.size() * 8) + 4);
                data.writeInt32(stickersFinal.size());
                for (int a2 = 0; a2 < stickersFinal.size(); a2++) {
                    ((TLRPC.StickerSetCovered) stickersFinal.get(a2)).serializeToStream(data);
                }
                data2.writeInt32(unreadStickers.size());
                for (int a3 = 0; a3 < unreadStickers.size(); a3++) {
                    data2.writeInt64(((Long) unreadStickers.get(a3)).longValue());
                }
                state.bindInteger(1, 1);
                state.bindByteBuffer(2, data);
                state.bindByteBuffer(3, data2);
                state.bindInteger(4, date);
                state.bindInteger(5, hash);
                state.step();
                data.reuse();
                data2.reuse();
                state.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
            state2.requery();
            state2.bindInteger(1, date);
            state2.step();
            state2.dispose();
        }
    }

    private int calcFeaturedStickersHash(ArrayList<TLRPC.StickerSetCovered> sets) {
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            TLRPC.StickerSet set = sets.get(a).set;
            if (!set.archived) {
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (set.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) set.id))) % 2147483648L;
                if (this.unreadStickerSets.contains(Long.valueOf(set.id))) {
                    acc = (((20261 * acc) + 2147483648L) + 1) % 2147483648L;
                }
            }
        }
        return (int) acc;
    }

    public void markFaturedStickersAsRead(boolean query) {
        if (!this.unreadStickerSets.isEmpty()) {
            this.unreadStickerSets.clear();
            this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
            getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
            putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
            if (query) {
                getConnectionsManager().sendRequest(new TLRPC.TL_messages_readFeaturedStickers(), $$Lambda$MediaDataController$mMne2VOa3EF8gE4i7nDYzwklkGI.INSTANCE);
            }
        }
    }

    static /* synthetic */ void lambda$markFaturedStickersAsRead$28(TLObject response, TLRPC.TL_error error) {
    }

    public int getFeaturesStickersHashWithoutUnread() {
        long acc = 0;
        for (int a = 0; a < this.featuredStickerSets.size(); a++) {
            TLRPC.StickerSet set = this.featuredStickerSets.get(a).set;
            if (!set.archived) {
                acc = (((20261 * ((((acc * 20261) + 2147483648L) + ((long) ((int) (set.id >> 32)))) % 2147483648L)) + 2147483648L) + ((long) ((int) set.id))) % 2147483648L;
            }
        }
        return (int) acc;
    }

    public void markFaturedStickersByIdAsRead(long id) {
        if (this.unreadStickerSets.contains(Long.valueOf(id)) && !this.readingStickerSets.contains(Long.valueOf(id))) {
            this.readingStickerSets.add(Long.valueOf(id));
            TLRPC.TL_messages_readFeaturedStickers req = new TLRPC.TL_messages_readFeaturedStickers();
            req.id.add(Long.valueOf(id));
            getConnectionsManager().sendRequest(req, $$Lambda$MediaDataController$lb15p0ZR17yH2Dq5vlDzyrw0fUE.INSTANCE);
            AndroidUtilities.runOnUIThread(new Runnable(id) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$markFaturedStickersByIdAsRead$30$MediaDataController(this.f$1);
                }
            }, 1000);
        }
    }

    static /* synthetic */ void lambda$markFaturedStickersByIdAsRead$29(TLObject response, TLRPC.TL_error error) {
    }

    public /* synthetic */ void lambda$markFaturedStickersByIdAsRead$30$MediaDataController(long id) {
        this.unreadStickerSets.remove(Long.valueOf(id));
        this.readingStickerSets.remove(Long.valueOf(id));
        this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
        putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
    }

    public int getArchivedStickersCount(int type) {
        return this.archivedStickersCount[type];
    }

    public void loadArchivedStickersCount(int type, boolean cache) {
        boolean z = true;
        if (cache) {
            SharedPreferences preferences2 = MessagesController.getNotificationsSettings(this.currentAccount);
            int count = preferences2.getInt("archivedStickersCount" + type, -1);
            if (count == -1) {
                loadArchivedStickersCount(type, false);
                return;
            }
            this.archivedStickersCount[type] = count;
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(type));
            return;
        }
        TLRPC.TL_messages_getArchivedStickers req = new TLRPC.TL_messages_getArchivedStickers();
        req.limit = 0;
        if (type != 1) {
            z = false;
        }
        req.masks = z;
        getConnectionsManager().sendRequest(req, new RequestDelegate(type) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.lambda$loadArchivedStickersCount$32$MediaDataController(this.f$1, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadArchivedStickersCount$32$MediaDataController(int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, type) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$null$31$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$31$MediaDataController(TLRPC.TL_error error, TLObject response, int type) {
        if (error == null) {
            TLRPC.TL_messages_archivedStickers res = (TLRPC.TL_messages_archivedStickers) response;
            this.archivedStickersCount[type] = res.count;
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putInt("archivedStickersCount" + type, res.count).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(type));
        }
    }

    private void processLoadStickersResponse(int type, TLRPC.TL_messages_allStickers res) {
        TLRPC.TL_messages_allStickers tL_messages_allStickers = res;
        ArrayList<TLRPC.TL_messages_stickerSet> newStickerArray = new ArrayList<>();
        long j = 1000;
        if (tL_messages_allStickers.sets.isEmpty()) {
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers.hash);
            return;
        }
        LongSparseArray<TLRPC.TL_messages_stickerSet> newStickerSets = new LongSparseArray<>();
        int a = 0;
        while (a < tL_messages_allStickers.sets.size()) {
            TLRPC.StickerSet stickerSet = (TLRPC.StickerSet) tL_messages_allStickers.sets.get(a);
            TLRPC.TL_messages_stickerSet oldSet = this.stickerSetsById.get(stickerSet.id);
            if (oldSet == null || oldSet.set.hash != stickerSet.hash) {
                newStickerArray.add((Object) null);
                TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
                req.stickerset = new TLRPC.TL_inputStickerSetID();
                req.stickerset.id = stickerSet.id;
                req.stickerset.access_hash = stickerSet.access_hash;
                $$Lambda$MediaDataController$2imk9cWkZVd_6VrxeDc8GilIXSA r10 = r0;
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = oldSet;
                $$Lambda$MediaDataController$2imk9cWkZVd_6VrxeDc8GilIXSA r0 = new RequestDelegate(newStickerArray, a, newStickerSets, stickerSet, res, type) {
                    private final /* synthetic */ ArrayList f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ LongSparseArray f$3;
                    private final /* synthetic */ TLRPC.StickerSet f$4;
                    private final /* synthetic */ TLRPC.TL_messages_allStickers f$5;
                    private final /* synthetic */ int f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$processLoadStickersResponse$34$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
                    }
                };
                getConnectionsManager().sendRequest(req, r10);
            } else {
                oldSet.set.archived = stickerSet.archived;
                oldSet.set.installed = stickerSet.installed;
                oldSet.set.official = stickerSet.official;
                newStickerSets.put(oldSet.set.id, oldSet);
                newStickerArray.add(oldSet);
                if (newStickerSets.size() == tL_messages_allStickers.sets.size()) {
                    processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / j), tL_messages_allStickers.hash);
                }
            }
            a++;
            tL_messages_allStickers = res;
            j = 1000;
        }
    }

    public /* synthetic */ void lambda$processLoadStickersResponse$34$MediaDataController(ArrayList newStickerArray, int index, LongSparseArray newStickerSets, TLRPC.StickerSet stickerSet, TLRPC.TL_messages_allStickers res, int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response, newStickerArray, index, newStickerSets, stickerSet, res, type) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ LongSparseArray f$4;
            private final /* synthetic */ TLRPC.StickerSet f$5;
            private final /* synthetic */ TLRPC.TL_messages_allStickers f$6;
            private final /* synthetic */ int f$7;

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
                MediaDataController.this.lambda$null$33$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$33$MediaDataController(TLObject response, ArrayList newStickerArray, int index, LongSparseArray newStickerSets, TLRPC.StickerSet stickerSet, TLRPC.TL_messages_allStickers res, int type) {
        TLRPC.TL_messages_allStickers tL_messages_allStickers = res;
        TLRPC.TL_messages_stickerSet res1 = (TLRPC.TL_messages_stickerSet) response;
        newStickerArray.set(index, res1);
        newStickerSets.put(stickerSet.id, res1);
        if (newStickerSets.size() == tL_messages_allStickers.sets.size()) {
            Iterator<TLRPC.TL_messages_stickerSet> iterator = newStickerArray.iterator();
            while (iterator.hasNext()) {
                TLRPC.TL_messages_stickerSet set = iterator.next();
                if (set == null) {
                    iterator.remove();
                } else {
                    if (!isStickerPackInstalled(set.set.id) && set.set.id != installingStickerSetId) {
                        iterator.remove();
                    }
                }
            }
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers.hash);
            return;
        }
    }

    public void installStickerSet(Context context, int type, TLRPC.StickerSetCovered stickerSet) {
        if (stickerSet.set == null || !isStickerPackInstalled(stickerSet.set.id)) {
            installingStickerSetId = stickerSet.set.id;
            TLRPC.TL_messages_installStickerSet req = new TLRPC.TL_messages_installStickerSet();
            TLRPC.InputStickerSet inputStickerSet = new TLRPC.TL_inputStickerSetID();
            inputStickerSet.id = stickerSet.set.id;
            inputStickerSet.access_hash = stickerSet.set.access_hash;
            req.stickerset = inputStickerSet;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(type) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$installStickerSet$36$MediaDataController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$installStickerSet$36$MediaDataController(int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, type) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$null$35$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$35$MediaDataController(TLRPC.TL_error error, TLObject response, int type) {
        if (error == null) {
            try {
                if (response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[0]);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            ToastUtils.show((int) R.string.ErrorOccurred);
        }
        loadStickers(type, false, true);
    }

    public void loadStickers(int type, boolean cache, boolean force) {
        TLObject req;
        if (!this.loadingStickers[type]) {
            if (type == 3) {
                if (this.featuredStickerSets.isEmpty() || !getMessagesController().preloadFeaturedStickers) {
                    return;
                }
            } else if (type != 4) {
                loadArchivedStickersCount(type, cache);
            }
            this.loadingStickers[type] = true;
            if (cache) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(type) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$loadStickers$37$MediaDataController(this.f$1);
                    }
                });
            } else if (type == 3) {
                TLRPC.TL_messages_allStickers response = new TLRPC.TL_messages_allStickers();
                response.hash = this.loadFeaturedHash;
                int size = this.featuredStickerSets.size();
                for (int a = 0; a < size; a++) {
                    response.sets.add(this.featuredStickerSets.get(a).set);
                }
                processLoadStickersResponse(type, response);
            } else if (type == 4) {
                TLRPC.TL_messages_getStickerSet req2 = new TLRPC.TL_messages_getStickerSet();
                req2.stickerset = new TLRPC.TL_inputStickerSetAnimatedEmoji();
                getConnectionsManager().sendRequest(req2, new RequestDelegate(type) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$loadStickers$38$MediaDataController(this.f$1, tLObject, tL_error);
                    }
                });
            } else {
                int hash = 0;
                if (type == 0) {
                    req = new TLRPC.TL_messages_getAllStickers();
                    TLRPC.TL_messages_getAllStickers tL_messages_getAllStickers = (TLRPC.TL_messages_getAllStickers) req;
                    if (!force) {
                        hash = this.loadHash[type];
                    }
                    tL_messages_getAllStickers.hash = hash;
                } else {
                    req = new TLRPC.TL_messages_getMaskStickers();
                    TLRPC.TL_messages_getMaskStickers tL_messages_getMaskStickers = (TLRPC.TL_messages_getMaskStickers) req;
                    if (!force) {
                        hash = this.loadHash[type];
                    }
                    tL_messages_getMaskStickers.hash = hash;
                }
                getConnectionsManager().sendRequest(req, new RequestDelegate(type, hash) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$loadStickers$40$MediaDataController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x005d, code lost:
        if (r3 != null) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x005f, code lost:
        r3.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0067, code lost:
        if (r3 == null) goto L_0x006a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x006a, code lost:
        processLoadedStickers(r12, r0, true, r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0073, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadStickers$37$MediaDataController(int r12) {
        /*
            r11 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            im.bclpbkiauv.messenger.MessagesStorage r4 = r11.getMessagesStorage()     // Catch:{ all -> 0x0063 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ all -> 0x0063 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0063 }
            r5.<init>()     // Catch:{ all -> 0x0063 }
            java.lang.String r6 = "SELECT data, date, hash FROM stickers_v2 WHERE id = "
            r5.append(r6)     // Catch:{ all -> 0x0063 }
            int r6 = r12 + 1
            r5.append(r6)     // Catch:{ all -> 0x0063 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0063 }
            r6 = 0
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x0063 }
            im.bclpbkiauv.sqlite.SQLiteCursor r4 = r4.queryFinalized(r5, r7)     // Catch:{ all -> 0x0063 }
            r3 = r4
            boolean r4 = r3.next()     // Catch:{ all -> 0x0063 }
            if (r4 == 0) goto L_0x005d
            im.bclpbkiauv.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r6)     // Catch:{ all -> 0x0063 }
            if (r4 == 0) goto L_0x0052
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0063 }
            r5.<init>()     // Catch:{ all -> 0x0063 }
            r0 = r5
            int r5 = r4.readInt32(r6)     // Catch:{ all -> 0x0063 }
            r7 = 0
        L_0x003e:
            if (r7 >= r5) goto L_0x004f
            int r8 = r4.readInt32(r6)     // Catch:{ all -> 0x0063 }
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r8 = im.bclpbkiauv.tgnet.TLRPC.TL_messages_stickerSet.TLdeserialize(r4, r8, r6)     // Catch:{ all -> 0x0063 }
            r0.add(r8)     // Catch:{ all -> 0x0063 }
            int r7 = r7 + 1
            goto L_0x003e
        L_0x004f:
            r4.reuse()     // Catch:{ all -> 0x0063 }
        L_0x0052:
            r5 = 1
            int r5 = r3.intValue(r5)     // Catch:{ all -> 0x0063 }
            r1 = r5
            int r5 = calcStickersHash(r0)     // Catch:{ all -> 0x0063 }
            r2 = r5
        L_0x005d:
            if (r3 == 0) goto L_0x006a
        L_0x005f:
            r3.dispose()
            goto L_0x006a
        L_0x0063:
            r4 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x0074 }
            if (r3 == 0) goto L_0x006a
            goto L_0x005f
        L_0x006a:
            r8 = 1
            r5 = r11
            r6 = r12
            r7 = r0
            r9 = r1
            r10 = r2
            r5.processLoadedStickers(r6, r7, r8, r9, r10)
            return
        L_0x0074:
            r4 = move-exception
            if (r3 == 0) goto L_0x007a
            r3.dispose()
        L_0x007a:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.lambda$loadStickers$37$MediaDataController(int):void");
    }

    public /* synthetic */ void lambda$loadStickers$38$MediaDataController(int type, TLObject response, TLRPC.TL_error error) {
        TLObject tLObject = response;
        if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
            ArrayList<TLRPC.TL_messages_stickerSet> newStickerArray = new ArrayList<>();
            newStickerArray.add((TLRPC.TL_messages_stickerSet) tLObject);
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), calcStickersHash(newStickerArray));
            return;
        }
        processLoadedStickers(type, (ArrayList<TLRPC.TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), 0);
    }

    public /* synthetic */ void lambda$loadStickers$40$MediaDataController(int type, int hash, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response, type, hash) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$null$39$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$39$MediaDataController(TLObject response, int type, int hash) {
        if (response instanceof TLRPC.TL_messages_allStickers) {
            processLoadStickersResponse(type, (TLRPC.TL_messages_allStickers) response);
            return;
        }
        processLoadedStickers(type, (ArrayList<TLRPC.TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), hash);
    }

    private void putStickersToCache(int type, ArrayList<TLRPC.TL_messages_stickerSet> stickers, int date, int hash) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(stickers != null ? new ArrayList<>(stickers) : null, type, date, hash) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$putStickersToCache$41$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$putStickersToCache$41$MediaDataController(ArrayList stickersFinal, int type, int date, int hash) {
        if (stickersFinal != null) {
            try {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                state.requery();
                int size = 4;
                for (int a = 0; a < stickersFinal.size(); a++) {
                    size += ((TLRPC.TL_messages_stickerSet) stickersFinal.get(a)).getObjectSize();
                }
                NativeByteBuffer data = new NativeByteBuffer(size);
                data.writeInt32(stickersFinal.size());
                for (int a2 = 0; a2 < stickersFinal.size(); a2++) {
                    ((TLRPC.TL_messages_stickerSet) stickersFinal.get(a2)).serializeToStream(data);
                }
                state.bindInteger(1, type + 1);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, date);
                state.bindInteger(4, hash);
                state.step();
                data.reuse();
                state.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
            state2.requery();
            state2.bindInteger(1, date);
            state2.step();
            state2.dispose();
        }
    }

    public String getStickerSetName(long setId) {
        TLRPC.TL_messages_stickerSet stickerSet = this.stickerSetsById.get(setId);
        if (stickerSet != null) {
            return stickerSet.set.short_name;
        }
        TLRPC.StickerSetCovered stickerSetCovered = this.featuredStickerSetsById.get(setId);
        if (stickerSetCovered != null) {
            return stickerSetCovered.set.short_name;
        }
        return null;
    }

    public static long getStickerSetId(TLRPC.Document document) {
        int a = 0;
        while (a < document.attributes.size()) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetID) {
                return attribute.stickerset.id;
            } else {
                return -1;
            }
        }
        return -1;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Document document) {
        int a = 0;
        while (a < document.attributes.size()) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                return null;
            } else {
                return attribute.stickerset;
            }
        }
        return null;
    }

    private static int calcStickersHash(ArrayList<TLRPC.TL_messages_stickerSet> sets) {
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            TLRPC.StickerSet set = sets.get(a).set;
            if (!set.archived) {
                acc = (((20261 * acc) + 2147483648L) + ((long) set.hash)) % 2147483648L;
            }
        }
        return (int) acc;
    }

    private void processLoadedStickers(int type, ArrayList<TLRPC.TL_messages_stickerSet> res, boolean cache, int date, int hash) {
        AndroidUtilities.runOnUIThread(new Runnable(type) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$processLoadedStickers$42$MediaDataController(this.f$1);
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable(cache, res, date, hash, type) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                MediaDataController.this.lambda$processLoadedStickers$46$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedStickers$42$MediaDataController(int type) {
        this.loadingStickers[type] = false;
        this.stickersLoaded[type] = true;
    }

    public /* synthetic */ void lambda$processLoadedStickers$46$MediaDataController(boolean cache, ArrayList res, int date, int hash, int type) {
        HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByNameNew;
        LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsByIdNew;
        TLRPC.TL_messages_stickerSet stickerSet;
        TLRPC.TL_messages_stickerSet stickerSet2;
        HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByNameNew2;
        LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsByIdNew2;
        ArrayList arrayList = res;
        int i = date;
        int i2 = hash;
        int i3 = type;
        long j = 1000;
        if ((cache && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!cache && arrayList == null && i2 == 0)) {
            $$Lambda$MediaDataController$6MIBsUiZkv_WDVNpqso2Ck_nr6g r2 = new Runnable(arrayList, i2, i3) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$43$MediaDataController(this.f$1, this.f$2, this.f$3);
                }
            };
            if (arrayList != null || cache) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(r2, j);
            if (arrayList == null) {
                return;
            }
        }
        if (arrayList != null) {
            try {
                ArrayList<TLRPC.TL_messages_stickerSet> stickerSetsNew = new ArrayList<>();
                LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsByIdNew3 = new LongSparseArray<>();
                HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByNameNew3 = new HashMap<>();
                LongSparseArray<String> stickersByEmojiNew = new LongSparseArray<>();
                LongSparseArray<TLRPC.Document> stickersByIdNew = new LongSparseArray<>();
                HashMap<String, ArrayList<TLRPC.Document>> allStickersNew = new HashMap<>();
                int a = 0;
                while (a < res.size()) {
                    try {
                        TLRPC.TL_messages_stickerSet stickerSet3 = (TLRPC.TL_messages_stickerSet) arrayList.get(a);
                        if (stickerSet3 == null) {
                            stickerSetsByNameNew = stickerSetsByNameNew3;
                            stickerSetsByIdNew = stickerSetsByIdNew3;
                        } else {
                            stickerSetsNew.add(stickerSet3);
                            stickerSetsByIdNew3.put(stickerSet3.set.id, stickerSet3);
                            stickerSetsByNameNew3.put(stickerSet3.set.short_name, stickerSet3);
                            int b = 0;
                            while (b < stickerSet3.documents.size()) {
                                TLRPC.Document document = stickerSet3.documents.get(b);
                                if (document == null) {
                                    stickerSetsByNameNew2 = stickerSetsByNameNew3;
                                    stickerSetsByIdNew2 = stickerSetsByIdNew3;
                                } else if (document instanceof TLRPC.TL_documentEmpty) {
                                    stickerSetsByNameNew2 = stickerSetsByNameNew3;
                                    stickerSetsByIdNew2 = stickerSetsByIdNew3;
                                } else {
                                    stickerSetsByNameNew2 = stickerSetsByNameNew3;
                                    stickerSetsByIdNew2 = stickerSetsByIdNew3;
                                    stickersByIdNew.put(document.id, document);
                                }
                                b++;
                                stickerSetsByIdNew3 = stickerSetsByIdNew2;
                                stickerSetsByNameNew3 = stickerSetsByNameNew2;
                            }
                            stickerSetsByNameNew = stickerSetsByNameNew3;
                            stickerSetsByIdNew = stickerSetsByIdNew3;
                            if (!stickerSet3.set.archived) {
                                int b2 = 0;
                                while (b2 < stickerSet3.packs.size()) {
                                    TLRPC.TL_stickerPack stickerPack = stickerSet3.packs.get(b2);
                                    if (stickerPack == null) {
                                        stickerSet = stickerSet3;
                                    } else if (stickerPack.emoticon == null) {
                                        stickerSet = stickerSet3;
                                    } else {
                                        stickerPack.emoticon = stickerPack.emoticon.replace("️", "");
                                        ArrayList<TLRPC.Document> arrayList2 = allStickersNew.get(stickerPack.emoticon);
                                        if (arrayList2 == null) {
                                            arrayList2 = new ArrayList<>();
                                            allStickersNew.put(stickerPack.emoticon, arrayList2);
                                        }
                                        int c = 0;
                                        while (c < stickerPack.documents.size()) {
                                            Long id = stickerPack.documents.get(c);
                                            if (stickersByEmojiNew.indexOfKey(id.longValue()) < 0) {
                                                stickerSet2 = stickerSet3;
                                                stickersByEmojiNew.put(id.longValue(), stickerPack.emoticon);
                                            } else {
                                                stickerSet2 = stickerSet3;
                                            }
                                            TLRPC.Document sticker = stickersByIdNew.get(id.longValue());
                                            if (sticker != null) {
                                                arrayList2.add(sticker);
                                            }
                                            c++;
                                            int i4 = type;
                                            stickerSet3 = stickerSet2;
                                        }
                                        stickerSet = stickerSet3;
                                    }
                                    b2++;
                                    i3 = type;
                                    stickerSet3 = stickerSet;
                                }
                            }
                        }
                        a++;
                        i3 = type;
                        stickerSetsByIdNew3 = stickerSetsByIdNew;
                        stickerSetsByNameNew3 = stickerSetsByNameNew;
                    } catch (Throwable th) {
                        e = th;
                        int i5 = type;
                        FileLog.e(e);
                    }
                }
                HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByNameNew4 = stickerSetsByNameNew3;
                LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsByIdNew4 = stickerSetsByIdNew3;
                if (!cache) {
                    i3 = type;
                    putStickersToCache(i3, stickerSetsNew, i, i2);
                } else {
                    i3 = type;
                }
                int i6 = i3;
                try {
                    $$Lambda$MediaDataController$8kdHfr1YDulzFuvDguVtPMy6CXg r1 = new Runnable(type, stickerSetsByIdNew4, stickerSetsByNameNew4, stickerSetsNew, hash, date, stickersByIdNew, allStickersNew, stickersByEmojiNew) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ LongSparseArray f$2;
                        private final /* synthetic */ HashMap f$3;
                        private final /* synthetic */ ArrayList f$4;
                        private final /* synthetic */ int f$5;
                        private final /* synthetic */ int f$6;
                        private final /* synthetic */ LongSparseArray f$7;
                        private final /* synthetic */ HashMap f$8;
                        private final /* synthetic */ LongSparseArray f$9;

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
                            MediaDataController.this.lambda$null$44$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
                        }
                    };
                    AndroidUtilities.runOnUIThread(r1);
                } catch (Throwable th2) {
                    e = th2;
                }
            } catch (Throwable th3) {
                e = th3;
                int i7 = i3;
                FileLog.e(e);
            }
        } else {
            int i8 = i3;
            if (!cache) {
                AndroidUtilities.runOnUIThread(new Runnable(i8, i) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$45$MediaDataController(this.f$1, this.f$2);
                    }
                });
                putStickersToCache(i8, (ArrayList<TLRPC.TL_messages_stickerSet>) null, i, 0);
            }
        }
    }

    public /* synthetic */ void lambda$null$43$MediaDataController(ArrayList res, int hash, int type) {
        if (res != null && hash != 0) {
            this.loadHash[type] = hash;
        }
    }

    public /* synthetic */ void lambda$null$44$MediaDataController(int type, LongSparseArray stickerSetsByIdNew, HashMap stickerSetsByNameNew, ArrayList stickerSetsNew, int hash, int date, LongSparseArray stickersByIdNew, HashMap allStickersNew, LongSparseArray stickersByEmojiNew) {
        int i = type;
        LongSparseArray longSparseArray = stickerSetsByIdNew;
        HashMap hashMap = allStickersNew;
        for (int a = 0; a < this.stickerSets[i].size(); a++) {
            TLRPC.StickerSet set = this.stickerSets[i].get(a).set;
            this.stickerSetsById.remove(set.id);
            this.stickerSetsByName.remove(set.short_name);
            if (!(i == 3 || i == 4)) {
                this.installedStickerSetsById.remove(set.id);
            }
        }
        for (int a2 = 0; a2 < stickerSetsByIdNew.size(); a2++) {
            this.stickerSetsById.put(stickerSetsByIdNew.keyAt(a2), stickerSetsByIdNew.valueAt(a2));
            if (!(i == 3 || i == 4)) {
                this.installedStickerSetsById.put(stickerSetsByIdNew.keyAt(a2), stickerSetsByIdNew.valueAt(a2));
            }
        }
        HashMap hashMap2 = stickerSetsByNameNew;
        this.stickerSetsByName.putAll(stickerSetsByNameNew);
        this.stickerSets[i] = stickerSetsNew;
        this.loadHash[i] = hash;
        this.loadDate[i] = date;
        this.stickersByIds[i] = stickersByIdNew;
        if (i == 0) {
            this.allStickers = hashMap;
            this.stickersByEmoji = stickersByEmojiNew;
        } else {
            LongSparseArray longSparseArray2 = stickersByEmojiNew;
            if (i == 3) {
                this.allStickersFeatured = hashMap;
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
    }

    public /* synthetic */ void lambda$null$45$MediaDataController(int type, int date) {
        this.loadDate[type] = date;
    }

    public void removeStickersSet(Context context, TLRPC.StickerSet stickerSet, int hide, BaseFragment baseFragment, boolean showSettings) {
        TLRPC.StickerSet stickerSet2 = stickerSet;
        int i = hide;
        int type = stickerSet2.masks;
        TLRPC.TL_inputStickerSetID stickerSetID = new TLRPC.TL_inputStickerSetID();
        stickerSetID.access_hash = stickerSet2.access_hash;
        stickerSetID.id = stickerSet2.id;
        if (i != 0) {
            stickerSet2.archived = i == 1;
            int a = 0;
            while (true) {
                if (a >= this.stickerSets[type].size()) {
                    break;
                }
                TLRPC.TL_messages_stickerSet set = this.stickerSets[type].get(a);
                if (set.set.id == stickerSet2.id) {
                    this.stickerSets[type].remove(a);
                    if (i == 2) {
                        this.stickerSets[type].add(0, set);
                    } else {
                        this.stickerSetsById.remove(set.set.id);
                        this.installedStickerSetsById.remove(set.set.id);
                        this.stickerSetsByName.remove(set.set.short_name);
                    }
                } else {
                    a++;
                }
            }
            this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
            putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
            return;
        }
        TLRPC.TL_messages_uninstallStickerSet req = new TLRPC.TL_messages_uninstallStickerSet();
        req.stickerset = stickerSetID;
        getConnectionsManager().sendRequest(req, new RequestDelegate(stickerSet2, type) {
            private final /* synthetic */ TLRPC.StickerSet f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.lambda$removeStickersSet$48$MediaDataController(this.f$1, this.f$2, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$removeStickersSet$48$MediaDataController(TLRPC.StickerSet stickerSet, int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, stickerSet, type) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLRPC.StickerSet f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$null$47$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$47$MediaDataController(TLRPC.TL_error error, TLRPC.StickerSet stickerSet, int type) {
        if (error == null) {
            try {
                if (stickerSet.masks) {
                    ToastUtils.show((int) R.string.MasksRemoved);
                } else {
                    ToastUtils.show((int) R.string.StickersRemoved);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            ToastUtils.show((int) R.string.ErrorOccurred);
        }
        loadStickers(type, false, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0016, code lost:
        if (r1[1] != false) goto L_0x001a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getMask() {
        /*
            r4 = this;
            r0 = 0
            int r1 = r4.lastReturnedNum
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r2 = r4.searchResultMessages
            int r2 = r2.size()
            r3 = 1
            int r2 = r2 - r3
            if (r1 < r2) goto L_0x0018
            boolean[] r1 = r4.messagesSearchEndReached
            r2 = 0
            boolean r2 = r1[r2]
            if (r2 == 0) goto L_0x0018
            boolean r1 = r1[r3]
            if (r1 != 0) goto L_0x001a
        L_0x0018:
            r0 = r0 | 1
        L_0x001a:
            int r1 = r4.lastReturnedNum
            if (r1 <= 0) goto L_0x0020
            r0 = r0 | 2
        L_0x0020:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.getMask():int");
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isMessageFound(int r2, boolean r3) {
        /*
            r1 = this;
            android.util.SparseArray<im.bclpbkiauv.messenger.MessageObject>[] r0 = r1.searchResultMessagesMap
            r0 = r0[r3]
            int r0 = r0.indexOfKey(r2)
            if (r0 < 0) goto L_0x000c
            r0 = 1
            goto L_0x000d
        L_0x000c:
            r0 = 0
        L_0x000d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.isMessageFound(int, boolean):boolean");
    }

    public void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, TLRPC.User user) {
        searchMessagesInChat(query, dialog_id, mergeDialogId, guid, direction, false, user);
    }

    private void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, boolean internal, TLRPC.User user) {
        boolean firstQuery;
        String query2;
        int max_id;
        long queryWithDialog;
        long queryWithDialog2;
        int max_id2;
        String query3;
        long j = mergeDialogId;
        int i = direction;
        TLRPC.User user2 = user;
        int max_id3 = 0;
        long queryWithDialog3 = dialog_id;
        boolean firstQuery2 = !internal;
        if (this.reqId != 0) {
            getConnectionsManager().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.mergeReqId != 0) {
            getConnectionsManager().cancelRequest(this.mergeReqId, true);
            this.mergeReqId = 0;
        }
        if (query != null) {
            if (firstQuery2) {
                getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(guid));
                boolean[] zArr = this.messagesSearchEndReached;
                zArr[1] = false;
                zArr[0] = false;
                int[] iArr = this.messagesSearchCount;
                iArr[1] = 0;
                iArr[0] = 0;
                this.searchResultMessages.clear();
                this.searchResultMessagesMap[0].clear();
                this.searchResultMessagesMap[1].clear();
            }
            query2 = query;
            max_id = 0;
            firstQuery = firstQuery2;
        } else if (!this.searchResultMessages.isEmpty()) {
            if (i == 1) {
                int i2 = this.lastReturnedNum + 1;
                this.lastReturnedNum = i2;
                if (i2 < this.searchResultMessages.size()) {
                    MessageObject messageObject = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i3 = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr2 = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i3, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr2[0] + iArr2[1]));
                    return;
                }
                boolean[] zArr2 = this.messagesSearchEndReached;
                if (!zArr2[0] || j != 0 || !zArr2[1]) {
                    String query4 = this.lastSearchQuery;
                    ArrayList<MessageObject> arrayList = this.searchResultMessages;
                    MessageObject messageObject2 = arrayList.get(arrayList.size() - 1);
                    if (messageObject2.getDialogId() != dialog_id || this.messagesSearchEndReached[0]) {
                        if (messageObject2.getDialogId() == j) {
                            max_id3 = messageObject2.getId();
                        }
                        queryWithDialog3 = mergeDialogId;
                        this.messagesSearchEndReached[1] = false;
                    } else {
                        max_id3 = messageObject2.getId();
                        queryWithDialog3 = dialog_id;
                    }
                    max_id = max_id3;
                    firstQuery = false;
                    query2 = query4;
                } else {
                    this.lastReturnedNum--;
                    return;
                }
            } else if (i == 2) {
                int i4 = this.lastReturnedNum - 1;
                this.lastReturnedNum = i4;
                if (i4 < 0) {
                    this.lastReturnedNum = 0;
                    return;
                }
                if (i4 >= this.searchResultMessages.size()) {
                    this.lastReturnedNum = this.searchResultMessages.size() - 1;
                }
                MessageObject messageObject3 = this.searchResultMessages.get(this.lastReturnedNum);
                NotificationCenter notificationCenter2 = getNotificationCenter();
                int i5 = NotificationCenter.chatSearchResultsAvailable;
                int[] iArr3 = this.messagesSearchCount;
                notificationCenter2.postNotificationName(i5, Integer.valueOf(guid), Integer.valueOf(messageObject3.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject3.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr3[0] + iArr3[1]));
                return;
            } else {
                return;
            }
        } else {
            return;
        }
        boolean[] zArr3 = this.messagesSearchEndReached;
        if (!zArr3[0] || zArr3[1] || j == 0) {
            queryWithDialog = queryWithDialog3;
        } else {
            queryWithDialog = mergeDialogId;
        }
        String str = "";
        if (queryWithDialog != dialog_id || !firstQuery) {
            queryWithDialog2 = queryWithDialog;
            max_id2 = max_id;
            query3 = query2;
        } else if (j != 0) {
            TLRPC.InputPeer inputPeer = getMessagesController().getInputPeer((int) j);
            if (inputPeer != null) {
                TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
                req.peer = inputPeer;
                this.lastMergeDialogId = j;
                req.limit = 1;
                if (query2 != null) {
                    str = query2;
                }
                req.q = str;
                if (user2 != null) {
                    req.from_id = getMessagesController().getInputUser(user2);
                    req.flags |= 1;
                }
                req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
                long j2 = queryWithDialog;
                $$Lambda$MediaDataController$2lijbsSM10IHn8RWE0aPzOVi1qU r10 = r0;
                TLRPC.InputPeer inputPeer2 = inputPeer;
                int i6 = max_id;
                String str2 = query2;
                $$Lambda$MediaDataController$2lijbsSM10IHn8RWE0aPzOVi1qU r0 = new RequestDelegate(mergeDialogId, req, dialog_id, guid, direction, user) {
                    private final /* synthetic */ long f$1;
                    private final /* synthetic */ TLRPC.TL_messages_search f$2;
                    private final /* synthetic */ long f$3;
                    private final /* synthetic */ int f$4;
                    private final /* synthetic */ int f$5;
                    private final /* synthetic */ TLRPC.User f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r4;
                        this.f$3 = r5;
                        this.f$4 = r7;
                        this.f$5 = r8;
                        this.f$6 = r9;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$searchMessagesInChat$50$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
                    }
                };
                this.mergeReqId = getConnectionsManager().sendRequest(req, r10, 2);
                return;
            }
            return;
        } else {
            queryWithDialog2 = queryWithDialog;
            max_id2 = max_id;
            query3 = query2;
            this.lastMergeDialogId = 0;
            this.messagesSearchEndReached[1] = true;
            this.messagesSearchCount[1] = 0;
        }
        TLRPC.TL_messages_search req2 = new TLRPC.TL_messages_search();
        long queryWithDialog4 = queryWithDialog2;
        req2.peer = getMessagesController().getInputPeer((int) queryWithDialog4);
        if (req2.peer != null) {
            req2.limit = 21;
            if (query3 != null) {
                str = query3;
            }
            req2.q = str;
            req2.offset_id = max_id2;
            String query5 = query3;
            TLRPC.User user3 = user;
            if (user3 != null) {
                req2.from_id = getMessagesController().getInputUser(user3);
                req2.flags |= 1;
            }
            req2.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            int currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.lastSearchQuery = query5;
            long j3 = queryWithDialog4;
            String str3 = query5;
            $$Lambda$MediaDataController$3AVktO7UNjEAm3zvqgmRoTn0 r14 = r0;
            int i7 = max_id2;
            $$Lambda$MediaDataController$3AVktO7UNjEAm3zvqgmRoTn0 r02 = new RequestDelegate(currentReqId, req2, queryWithDialog4, dialog_id, guid, mergeDialogId, user) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ TLRPC.TL_messages_search f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ long f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ long f$6;
                private final /* synthetic */ TLRPC.User f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                    this.f$5 = r8;
                    this.f$6 = r9;
                    this.f$7 = r11;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$searchMessagesInChat$52$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
                }
            };
            this.reqId = getConnectionsManager().sendRequest(req2, r14, 2);
        }
    }

    public /* synthetic */ void lambda$searchMessagesInChat$50$MediaDataController(long mergeDialogId, TLRPC.TL_messages_search req, long dialog_id, int guid, int direction, TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(mergeDialogId, response, req, dialog_id, guid, direction, user) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_messages_search f$3;
            private final /* synthetic */ long f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ TLRPC.User f$7;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r8;
                this.f$6 = r9;
                this.f$7 = r10;
            }

            public final void run() {
                MediaDataController.this.lambda$null$49$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$49$MediaDataController(long mergeDialogId, TLObject response, TLRPC.TL_messages_search req, long dialog_id, int guid, int direction, TLRPC.User user) {
        if (this.lastMergeDialogId == mergeDialogId) {
            this.mergeReqId = 0;
            if (response != null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                this.messagesSearchEndReached[1] = res.messages.isEmpty();
                this.messagesSearchCount[1] = res instanceof TLRPC.TL_messages_messagesSlice ? res.count : res.messages.size();
                searchMessagesInChat(req.q, dialog_id, mergeDialogId, guid, direction, true, user);
                return;
            }
            TLRPC.TL_messages_search tL_messages_search = req;
            return;
        }
        TLRPC.TL_messages_search tL_messages_search2 = req;
    }

    public /* synthetic */ void lambda$searchMessagesInChat$52$MediaDataController(int currentReqId, TLRPC.TL_messages_search req, long queryWithDialogFinal, long dialog_id, int guid, long mergeDialogId, TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(currentReqId, response, req, queryWithDialogFinal, dialog_id, guid, mergeDialogId, user) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_messages_search f$3;
            private final /* synthetic */ long f$4;
            private final /* synthetic */ long f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ long f$7;
            private final /* synthetic */ TLRPC.User f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r7;
                this.f$6 = r9;
                this.f$7 = r10;
                this.f$8 = r12;
            }

            public final void run() {
                MediaDataController.this.lambda$null$51$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$51$MediaDataController(int currentReqId, TLObject response, TLRPC.TL_messages_search req, long queryWithDialogFinal, long dialog_id, int guid, long mergeDialogId, TLRPC.User user) {
        if (currentReqId == this.lastReqId) {
            this.reqId = 0;
            if (response != null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                int a = 0;
                while (a < res.messages.size()) {
                    TLRPC.Message message = res.messages.get(a);
                    if ((message instanceof TLRPC.TL_messageEmpty) || (message.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                        res.messages.remove(a);
                        a--;
                    }
                    a++;
                }
                getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                getMessagesController().putUsers(res.users, false);
                getMessagesController().putChats(res.chats, false);
                if (req.offset_id == 0 && queryWithDialogFinal == dialog_id) {
                    this.lastReturnedNum = 0;
                    this.searchResultMessages.clear();
                    this.searchResultMessagesMap[0].clear();
                    this.searchResultMessagesMap[1].clear();
                    this.messagesSearchCount[0] = 0;
                }
                boolean added = false;
                for (int a2 = 0; a2 < Math.min(res.messages.size(), 20); a2++) {
                    added = true;
                    MessageObject messageObject = new MessageObject(this.currentAccount, res.messages.get(a2), false);
                    this.searchResultMessages.add(messageObject);
                    this.searchResultMessagesMap[queryWithDialogFinal == dialog_id ? (char) 0 : 1].put(messageObject.getId(), messageObject);
                }
                this.messagesSearchEndReached[queryWithDialogFinal == dialog_id ? (char) 0 : 1] = res.messages.size() != 21;
                this.messagesSearchCount[queryWithDialogFinal == dialog_id ? (char) 0 : 1] = ((res instanceof TLRPC.TL_messages_messagesSlice) || (res instanceof TLRPC.TL_messages_channelMessages)) ? res.count : res.messages.size();
                if (this.searchResultMessages.isEmpty()) {
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), 0, Integer.valueOf(getMask()), 0L, 0, 0);
                } else if (added) {
                    if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    MessageObject messageObject2 = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i, Integer.valueOf(guid), Integer.valueOf(messageObject2.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject2.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]));
                }
                if (queryWithDialogFinal == dialog_id) {
                    boolean[] zArr = this.messagesSearchEndReached;
                    if (zArr[0] && mergeDialogId != 0 && !zArr[1]) {
                        searchMessagesInChat(this.lastSearchQuery, dialog_id, mergeDialogId, guid, 0, true, user);
                        return;
                    }
                    return;
                }
                return;
            }
            TLRPC.TL_messages_search tL_messages_search = req;
            return;
        }
        TLRPC.TL_messages_search tL_messages_search2 = req;
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x001d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadMedia(long r19, int r21, int r22, int r23, int r24, int r25) {
        /*
            r18 = this;
            r14 = r19
            r13 = r23
            int r0 = (int) r14
            r1 = 1
            if (r0 >= 0) goto L_0x0016
            int r0 = (int) r14
            int r0 = -r0
            r12 = r18
            int r2 = r12.currentAccount
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r0, r2)
            if (r0 == 0) goto L_0x0018
            r8 = 1
            goto L_0x001a
        L_0x0016:
            r12 = r18
        L_0x0018:
            r0 = 0
            r8 = 0
        L_0x001a:
            int r10 = (int) r14
            if (r24 != 0) goto L_0x00a4
            if (r10 != 0) goto L_0x0025
            r2 = r25
            r16 = r10
            goto L_0x00a8
        L_0x0025:
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_search r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_search
            r0.<init>()
            r9 = r0
            r11 = r21
            r9.limit = r11
            r7 = r22
            r9.offset_id = r7
            if (r13 != 0) goto L_0x003d
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo
            r0.<init>()
            r9.filter = r0
            goto L_0x0067
        L_0x003d:
            if (r13 != r1) goto L_0x0047
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterDocument r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterDocument
            r0.<init>()
            r9.filter = r0
            goto L_0x0067
        L_0x0047:
            r0 = 2
            if (r13 != r0) goto L_0x0052
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice
            r0.<init>()
            r9.filter = r0
            goto L_0x0067
        L_0x0052:
            r0 = 3
            if (r13 != r0) goto L_0x005d
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterUrl r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterUrl
            r0.<init>()
            r9.filter = r0
            goto L_0x0067
        L_0x005d:
            r0 = 4
            if (r13 != r0) goto L_0x0067
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterMusic r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMessagesFilterMusic
            r0.<init>()
            r9.filter = r0
        L_0x0067:
            java.lang.String r0 = ""
            r9.q = r0
            im.bclpbkiauv.messenger.MessagesController r0 = r18.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r0 = r0.getInputPeer(r10)
            r9.peer = r0
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r0 = r9.peer
            if (r0 != 0) goto L_0x007a
            return
        L_0x007a:
            im.bclpbkiauv.tgnet.ConnectionsManager r6 = r18.getConnectionsManager()
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$kectDau-n6rJM_99aZXxbb3HvnE r5 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$kectDau-n6rJM_99aZXxbb3HvnE
            r0 = r5
            r1 = r18
            r2 = r19
            r4 = r21
            r16 = r10
            r10 = r5
            r5 = r22
            r11 = r6
            r6 = r23
            r7 = r25
            r0.<init>(r2, r4, r5, r6, r7, r8)
            int r0 = r11.sendRequest(r9, r10)
            im.bclpbkiauv.tgnet.ConnectionsManager r1 = r18.getConnectionsManager()
            r2 = r25
            r1.bindRequestToGuid(r0, r2)
            r0 = r16
            goto L_0x00bd
        L_0x00a4:
            r2 = r25
            r16 = r10
        L_0x00a8:
            r9 = r18
            r0 = r16
            r10 = r19
            r12 = r21
            r13 = r22
            r14 = r23
            r15 = r25
            r16 = r8
            r17 = r24
            r9.loadMediaDatabase(r10, r12, r13, r14, r15, r16, r17)
        L_0x00bd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.loadMedia(long, int, int, int, int, int):void");
    }

    public /* synthetic */ void lambda$loadMedia$53$MediaDataController(long uid, int count, int max_id, int type, int classGuid, boolean isChannel, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            getMessagesController().removeDeletedMessagesFromArray(uid, res.messages);
            processLoadedMedia(res, uid, count, max_id, type, 0, classGuid, isChannel, res.messages.size() == 0);
            return;
        }
        long j = uid;
    }

    public void getMediaCounts(long uid, int classGuid) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(uid, classGuid) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$getMediaCounts$58$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$getMediaCounts$58$MediaDataController(long uid, int classGuid) {
        int[] counts;
        SQLiteCursor cursor;
        int a;
        int[] countsFinal;
        long j = uid;
        try {
            int i = -1;
            int i2 = 0;
            int i3 = 2;
            int i4 = 3;
            int[] counts2 = {-1, -1, -1, -1, -1};
            int[] countsFinal2 = {-1, -1, -1, -1, -1};
            int[] old = {0, 0, 0, 0, 0};
            SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT type, count, old FROM media_counts_v2 WHERE uid = %d", new Object[]{Long.valueOf(uid)}), new Object[0]);
            while (cursor2.next()) {
                int type = cursor2.intValue(0);
                if (type >= 0 && type < 5) {
                    int intValue = cursor2.intValue(1);
                    counts2[type] = intValue;
                    countsFinal2[type] = intValue;
                    old[type] = cursor2.intValue(2);
                }
            }
            cursor2.dispose();
            int lower_part = (int) j;
            if (lower_part == 0) {
                for (int a2 = 0; a2 < counts2.length; a2++) {
                    if (counts2[a2] == -1) {
                        SQLiteCursor cursor3 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(a2)}), new Object[0]);
                        if (cursor3.next()) {
                            counts2[a2] = cursor3.intValue(0);
                        } else {
                            counts2[a2] = 0;
                        }
                        cursor3.dispose();
                        putMediaCountDatabase(j, a2, counts2[a2]);
                        SQLiteCursor sQLiteCursor = cursor3;
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable(j, counts2) {
                    private final /* synthetic */ long f$1;
                    private final /* synthetic */ int[] f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r4;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$54$MediaDataController(this.f$1, this.f$2);
                    }
                });
                int i5 = classGuid;
                return;
            }
            int a3 = 0;
            boolean missing = false;
            while (a3 < counts2.length) {
                if (counts2[a3] != i) {
                    if (old[a3] != 1) {
                        a = a3;
                        cursor = cursor2;
                        counts = counts2;
                        countsFinal = countsFinal2;
                        int i6 = classGuid;
                        a3 = a + 1;
                        countsFinal2 = countsFinal;
                        cursor2 = cursor;
                        counts2 = counts;
                        i = -1;
                        i2 = 0;
                        i3 = 2;
                        i4 = 3;
                    }
                }
                int type2 = a3;
                TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
                req.limit = 1;
                req.offset_id = i2;
                if (a3 == 0) {
                    req.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
                } else if (a3 == 1) {
                    req.filter = new TLRPC.TL_inputMessagesFilterDocument();
                } else if (a3 == i3) {
                    req.filter = new TLRPC.TL_inputMessagesFilterRoundVoice();
                } else if (a3 == i4) {
                    req.filter = new TLRPC.TL_inputMessagesFilterUrl();
                } else if (a3 == 4) {
                    req.filter = new TLRPC.TL_inputMessagesFilterMusic();
                }
                req.q = "";
                req.peer = getMessagesController().getInputPeer(lower_part);
                if (req.peer == null) {
                    counts2[a3] = i2;
                    a = a3;
                    cursor = cursor2;
                    counts = counts2;
                    countsFinal = countsFinal2;
                    int i7 = classGuid;
                } else {
                    a = a3;
                    cursor = cursor2;
                    counts = counts2;
                    countsFinal = countsFinal2;
                    try {
                        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(counts2, type2, uid) {
                            private final /* synthetic */ int[] f$1;
                            private final /* synthetic */ int f$2;
                            private final /* synthetic */ long f$3;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                MediaDataController.this.lambda$null$56$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                            }
                        }), classGuid);
                        if (counts[a] == -1) {
                            missing = true;
                        } else if (old[a] == 1) {
                            counts[a] = -1;
                        }
                    } catch (Exception e) {
                        e = e;
                        FileLog.e((Throwable) e);
                    }
                }
                a3 = a + 1;
                countsFinal2 = countsFinal;
                cursor2 = cursor;
                counts2 = counts;
                i = -1;
                i2 = 0;
                i3 = 2;
                i4 = 3;
            }
            int i8 = a3;
            SQLiteCursor sQLiteCursor2 = cursor2;
            int[] iArr = counts2;
            int[] countsFinal3 = countsFinal2;
            int i9 = classGuid;
            if (!missing) {
                AndroidUtilities.runOnUIThread(new Runnable(j, countsFinal3) {
                    private final /* synthetic */ long f$1;
                    private final /* synthetic */ int[] f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r4;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$57$MediaDataController(this.f$1, this.f$2);
                    }
                });
            }
        } catch (Exception e2) {
            e = e2;
            int i10 = classGuid;
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$54$MediaDataController(long uid, int[] counts) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(uid), counts);
    }

    public /* synthetic */ void lambda$null$56$MediaDataController(int[] counts, int type, long uid, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            if (res instanceof TLRPC.TL_messages_messages) {
                counts[type] = res.messages.size();
            } else {
                counts[type] = res.count;
            }
            putMediaCountDatabase(uid, type, counts[type]);
        } else {
            counts[type] = 0;
        }
        boolean finished = true;
        int b = 0;
        while (true) {
            if (b >= counts.length) {
                break;
            } else if (counts[b] == -1) {
                finished = false;
                break;
            } else {
                b++;
            }
        }
        if (finished) {
            AndroidUtilities.runOnUIThread(new Runnable(uid, counts) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ int[] f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$55$MediaDataController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$55$MediaDataController(long uid, int[] counts) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(uid), counts);
    }

    public /* synthetic */ void lambda$null$57$MediaDataController(long uid, int[] countsFinal) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(uid), countsFinal);
    }

    public void getMediaCount(long uid, int type, int classGuid, boolean fromCache) {
        int lower_part = (int) uid;
        if (fromCache || lower_part == 0) {
            getMediaCountDatabase(uid, type, classGuid);
            return;
        }
        TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
        req.limit = 1;
        req.offset_id = 0;
        if (type == 0) {
            req.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
        } else if (type == 1) {
            req.filter = new TLRPC.TL_inputMessagesFilterDocument();
        } else if (type == 2) {
            req.filter = new TLRPC.TL_inputMessagesFilterRoundVoice();
        } else if (type == 3) {
            req.filter = new TLRPC.TL_inputMessagesFilterUrl();
        } else if (type == 4) {
            req.filter = new TLRPC.TL_inputMessagesFilterMusic();
        }
        req.q = "";
        req.peer = getMessagesController().getInputPeer(lower_part);
        if (req.peer != null) {
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(uid, type, classGuid) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$getMediaCount$60$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            }), classGuid);
        }
    }

    public /* synthetic */ void lambda$getMediaCount$60$MediaDataController(long uid, int type, int classGuid, TLObject response, TLRPC.TL_error error) {
        int count;
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            if (res instanceof TLRPC.TL_messages_messages) {
                count = res.messages.size();
            } else {
                count = res.count;
            }
            AndroidUtilities.runOnUIThread(new Runnable(res) {
                private final /* synthetic */ TLRPC.messages_Messages f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$59$MediaDataController(this.f$1);
                }
            });
            processLoadedMediaCount(count, uid, type, classGuid, false, 0);
            return;
        }
    }

    public /* synthetic */ void lambda$null$59$MediaDataController(TLRPC.messages_Messages res) {
        getMessagesController().putUsers(res.users, false);
        getMessagesController().putChats(res.chats, false);
    }

    public static int getMediaType(TLRPC.Message message) {
        if (message == null) {
            return -1;
        }
        if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
            return 0;
        }
        if (!(message.media instanceof TLRPC.TL_messageMediaDocument)) {
            if (!message.entities.isEmpty()) {
                for (int a = 0; a < message.entities.size(); a++) {
                    TLRPC.MessageEntity entity = message.entities.get(a);
                    if ((entity instanceof TLRPC.TL_messageEntityUrl) || (entity instanceof TLRPC.TL_messageEntityTextUrl) || (entity instanceof TLRPC.TL_messageEntityEmail)) {
                        return 3;
                    }
                }
            }
            return -1;
        } else if (MessageObject.isVoiceMessage(message) || MessageObject.isRoundVideoMessage(message)) {
            return 2;
        } else {
            if (MessageObject.isVideoMessage(message)) {
                return 0;
            }
            if (MessageObject.isStickerMessage(message) || MessageObject.isAnimatedStickerMessage(message)) {
                return -1;
            }
            if (MessageObject.isMusicMessage(message)) {
                return 4;
            }
            return 1;
        }
    }

    public static boolean canAddMessageToMedia(TLRPC.Message message) {
        if ((message instanceof TLRPC.TL_message_secret) && (((message.media instanceof TLRPC.TL_messageMediaPhoto) || MessageObject.isVideoMessage(message) || MessageObject.isGifMessage(message)) && message.media.ttl_seconds != 0 && message.media.ttl_seconds <= 60)) {
            return false;
        }
        if (!(message instanceof TLRPC.TL_message_secret) && (message instanceof TLRPC.TL_message) && (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (message.media instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0)) {
            return false;
        }
        if ((message.media instanceof TLRPC.TL_messageMediaPhoto) || ((message.media instanceof TLRPC.TL_messageMediaDocument) && !MessageObject.isGifDocument(message.media.document))) {
            return true;
        }
        if (!message.entities.isEmpty()) {
            for (int a = 0; a < message.entities.size(); a++) {
                TLRPC.MessageEntity entity = message.entities.get(a);
                if ((entity instanceof TLRPC.TL_messageEntityUrl) || (entity instanceof TLRPC.TL_messageEntityTextUrl) || (entity instanceof TLRPC.TL_messageEntityEmail)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void processLoadedMedia(TLRPC.messages_Messages res, long uid, int count, int max_id, int type, int fromCache, int classGuid, boolean isChannel, boolean topReached) {
        TLRPC.messages_Messages messages_messages = res;
        int i = fromCache;
        int lower_part = (int) uid;
        if (i == 0 || !messages_messages.messages.isEmpty() || lower_part == 0) {
            if (i == 0) {
                ImageLoader.saveMessagesThumbs(messages_messages.messages);
                getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                putMediaDatabase(uid, type, messages_messages.messages, max_id, topReached);
            }
            SparseArray<TLRPC.User> usersDict = new SparseArray<>();
            for (int a = 0; a < messages_messages.users.size(); a++) {
                TLRPC.User u = messages_messages.users.get(a);
                usersDict.put(u.id, u);
            }
            ArrayList<MessageObject> objects = new ArrayList<>();
            for (int a2 = 0; a2 < messages_messages.messages.size(); a2++) {
                objects.add(new MessageObject(this.currentAccount, messages_messages.messages.get(a2), usersDict, true));
            }
            ArrayList<MessageObject> arrayList = objects;
            AndroidUtilities.runOnUIThread(new Runnable(res, fromCache, uid, objects, classGuid, type, topReached) {
                private final /* synthetic */ TLRPC.messages_Messages f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ ArrayList f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ boolean f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                }

                public final void run() {
                    MediaDataController.this.lambda$processLoadedMedia$61$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        } else if (i != 2) {
            loadMedia(uid, count, max_id, type, 0, classGuid);
        }
    }

    public /* synthetic */ void lambda$processLoadedMedia$61$MediaDataController(TLRPC.messages_Messages res, int fromCache, long uid, ArrayList objects, int classGuid, int type, boolean topReached) {
        int totalCount = res.count;
        getMessagesController().putUsers(res.users, fromCache != 0);
        getMessagesController().putChats(res.chats, fromCache != 0);
        getNotificationCenter().postNotificationName(NotificationCenter.mediaDidLoad, Long.valueOf(uid), Integer.valueOf(totalCount), objects, Integer.valueOf(classGuid), Integer.valueOf(type), Boolean.valueOf(topReached));
    }

    private void processLoadedMediaCount(int count, long uid, int type, int classGuid, boolean fromCache, int old) {
        AndroidUtilities.runOnUIThread(new Runnable(uid, fromCache, count, type, old, classGuid) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                MediaDataController.this.lambda$processLoadedMediaCount$62$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001f, code lost:
        if (r10 != 0) goto L_0x0024;
     */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0035  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedMediaCount$62$MediaDataController(long r18, boolean r20, int r21, int r22, int r23, int r24) {
        /*
            r17 = this;
            r6 = r18
            r8 = r21
            r9 = r22
            int r10 = (int) r6
            r11 = 2
            r12 = -1
            r13 = 1
            r14 = 0
            if (r20 == 0) goto L_0x0017
            if (r8 == r12) goto L_0x0013
            if (r8 != 0) goto L_0x0017
            if (r9 != r11) goto L_0x0017
        L_0x0013:
            if (r10 == 0) goto L_0x0017
            r0 = 1
            goto L_0x0018
        L_0x0017:
            r0 = 0
        L_0x0018:
            r15 = r0
            if (r15 != 0) goto L_0x0022
            r5 = r23
            if (r5 != r13) goto L_0x0033
            if (r10 == 0) goto L_0x0033
            goto L_0x0024
        L_0x0022:
            r5 = r23
        L_0x0024:
            r16 = 0
            r0 = r17
            r1 = r18
            r3 = r22
            r4 = r24
            r5 = r16
            r0.getMediaCount(r1, r3, r4, r5)
        L_0x0033:
            if (r15 != 0) goto L_0x006b
            if (r20 != 0) goto L_0x003d
            r0 = r17
            r0.putMediaCountDatabase(r6, r9, r8)
            goto L_0x003f
        L_0x003d:
            r0 = r17
        L_0x003f:
            im.bclpbkiauv.messenger.NotificationCenter r1 = r17.getNotificationCenter()
            int r2 = im.bclpbkiauv.messenger.NotificationCenter.mediaCountDidLoad
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.Long r4 = java.lang.Long.valueOf(r18)
            r3[r14] = r4
            if (r20 == 0) goto L_0x0053
            if (r8 != r12) goto L_0x0053
            goto L_0x0054
        L_0x0053:
            r14 = r8
        L_0x0054:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r14)
            r3[r13] = r4
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r20)
            r3[r11] = r4
            r4 = 3
            java.lang.Integer r5 = java.lang.Integer.valueOf(r22)
            r3[r4] = r5
            r1.postNotificationName(r2, r3)
            goto L_0x006d
        L_0x006b:
            r0 = r17
        L_0x006d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.lambda$processLoadedMediaCount$62$MediaDataController(long, boolean, int, int, int, int):void");
    }

    private void putMediaCountDatabase(long uid, int type, int count) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(uid, type, count) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$putMediaCountDatabase$63$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$putMediaCountDatabase$63$MediaDataController(long uid, int type, int count) {
        try {
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
            state2.requery();
            state2.bindLong(1, uid);
            state2.bindInteger(2, type);
            state2.bindInteger(3, count);
            state2.bindInteger(4, 0);
            state2.step();
            state2.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void getMediaCountDatabase(long uid, int type, int classGuid) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(uid, type, classGuid) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$getMediaCountDatabase$64$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$getMediaCountDatabase$64$MediaDataController(long uid, int type, int classGuid) {
        int old;
        long j = uid;
        int count = -1;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
            if (cursor.next()) {
                count = cursor.intValue(0);
                old = cursor.intValue(1);
            } else {
                old = 0;
            }
            cursor.dispose();
            int lower_part = (int) j;
            if (count == -1 && lower_part == 0) {
                cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                if (cursor.next()) {
                    count = cursor.intValue(0);
                }
                cursor.dispose();
                if (count != -1) {
                    try {
                        putMediaCountDatabase(j, type, count);
                    } catch (Exception e) {
                        e = e;
                        FileLog.e((Throwable) e);
                    }
                } else {
                    int i = type;
                }
            } else {
                int i2 = type;
            }
            SQLiteCursor sQLiteCursor = cursor;
            processLoadedMediaCount(count, uid, type, classGuid, true, old);
        } catch (Exception e2) {
            e = e2;
            int i3 = type;
            FileLog.e((Throwable) e);
        }
    }

    private void loadMediaDatabase(long uid, int count, int max_id, int type, int classGuid, boolean isChannel, int fromCache) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(count, uid, max_id, isChannel, type, fromCache, classGuid) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ int f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
                this.f$7 = r9;
            }

            public final void run() {
                MediaDataController.this.lambda$loadMediaDatabase$65$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$loadMediaDatabase$65$MediaDataController(int count, long uid, int max_id, boolean isChannel, int type, int fromCache, int classGuid) {
        boolean topReached;
        TLRPC.TL_messages_messages res;
        ArrayList<Integer> usersToLoad;
        TLRPC.TL_messages_messages res2;
        ArrayList<Integer> chatsToLoad;
        boolean isEnd;
        SQLiteCursor cursor;
        TLRPC.TL_messages_messages res3;
        boolean topReached2;
        ArrayList<Integer> usersToLoad2;
        long holeMessageId;
        boolean isEnd2;
        int i = count;
        long j = uid;
        int i2 = max_id;
        TLRPC.TL_messages_messages res4 = new TLRPC.TL_messages_messages();
        try {
            ArrayList<Integer> usersToLoad3 = new ArrayList<>();
            ArrayList<Integer> chatsToLoad2 = new ArrayList<>();
            int countToLoad = i + 1;
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            if (((int) j) != 0) {
                int channelId = 0;
                res2 = res4;
                long messageMaxId = (long) i2;
                if (isChannel) {
                    channelId = -((int) j);
                }
                if (messageMaxId == 0 || channelId == 0) {
                    usersToLoad = usersToLoad3;
                    topReached = false;
                } else {
                    usersToLoad = usersToLoad3;
                    topReached = false;
                    messageMaxId |= ((long) channelId) << 32;
                }
                try {
                    SQLiteCursor cursor2 = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                    if (cursor2.next()) {
                        isEnd = cursor2.intValue(0) == 1;
                        cursor2.dispose();
                        int i3 = type;
                    } else {
                        cursor2.dispose();
                        cursor2 = database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                        if (cursor2.next()) {
                            try {
                                int mid = cursor2.intValue(0);
                                if (mid != 0) {
                                    SQLitePreparedStatement state = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                                    state.requery();
                                    state.bindLong(1, j);
                                    state.bindInteger(2, type);
                                    isEnd2 = false;
                                    state.bindInteger(3, 0);
                                    state.bindInteger(4, mid);
                                    state.step();
                                    state.dispose();
                                } else {
                                    int i4 = type;
                                    isEnd2 = false;
                                }
                            } catch (Exception e) {
                                e = e;
                                int i5 = type;
                                res = res2;
                                try {
                                    res.messages.clear();
                                    res.chats.clear();
                                    res.users.clear();
                                    FileLog.e((Throwable) e);
                                    processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                                    boolean z = topReached;
                                } catch (Throwable th) {
                                    th = th;
                                    processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                                    throw th;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                int i6 = type;
                                res = res2;
                                processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                                throw th;
                            }
                        } else {
                            int i7 = type;
                            isEnd2 = false;
                        }
                        cursor2.dispose();
                        isEnd = isEnd2;
                    }
                    if (messageMaxId != 0) {
                        long holeMessageId2 = 0;
                        SQLiteCursor sQLiteCursor = cursor2;
                        SQLiteCursor cursor3 = database.queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(type), Integer.valueOf(max_id)}), new Object[0]);
                        if (cursor3.next()) {
                            long holeMessageId3 = (long) cursor3.intValue(0);
                            if (channelId != 0) {
                                chatsToLoad = chatsToLoad2;
                                holeMessageId2 = holeMessageId3 | (((long) channelId) << 32);
                            } else {
                                chatsToLoad = chatsToLoad2;
                                holeMessageId2 = holeMessageId3;
                            }
                        } else {
                            chatsToLoad = chatsToLoad2;
                        }
                        cursor3.dispose();
                        if (holeMessageId2 > 1) {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(uid), Long.valueOf(messageMaxId), Long.valueOf(holeMessageId2), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                        } else {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(uid), Long.valueOf(messageMaxId), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                        }
                    } else {
                        SQLiteCursor sQLiteCursor2 = cursor2;
                        chatsToLoad = chatsToLoad2;
                        SQLiteCursor cursor4 = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                        if (cursor4.next()) {
                            holeMessageId = (long) cursor4.intValue(0);
                            if (channelId != 0) {
                                holeMessageId |= ((long) channelId) << 32;
                            }
                        } else {
                            holeMessageId = 0;
                        }
                        cursor4.dispose();
                        if (holeMessageId > 1) {
                            SQLiteCursor sQLiteCursor3 = cursor4;
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(uid), Long.valueOf(holeMessageId), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                        } else {
                            SQLiteCursor sQLiteCursor4 = cursor4;
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(uid), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                        }
                    }
                } catch (Exception e2) {
                    e = e2;
                } catch (Throwable th3) {
                    th = th3;
                    res = res2;
                    processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                    throw th;
                }
            } else {
                usersToLoad = usersToLoad3;
                topReached = false;
                chatsToLoad = chatsToLoad2;
                res2 = res4;
                isEnd = true;
                if (i2 != 0) {
                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(uid), Integer.valueOf(max_id), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                } else {
                    try {
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(uid), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                    } catch (Exception e3) {
                        e = e3;
                        res = res2;
                        res.messages.clear();
                        res.chats.clear();
                        res.users.clear();
                        FileLog.e((Throwable) e);
                        processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                        boolean z2 = topReached;
                    } catch (Throwable th4) {
                        th = th4;
                        res = res2;
                        processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                        throw th;
                    }
                }
            }
            while (cursor.next()) {
                try {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                        message.readAttachPath(data, getUserConfig().clientUserId);
                        data.reuse();
                        message.id = cursor.intValue(1);
                        message.dialog_id = j;
                        if (((int) j) == 0) {
                            message.random_id = cursor.longValue(2);
                        }
                        res3 = res2;
                        try {
                            res3.messages.add(message);
                            usersToLoad2 = usersToLoad;
                            MessagesStorage.addUsersAndChatsFromMessage(message, usersToLoad2, chatsToLoad);
                        } catch (Exception e4) {
                            e = e4;
                            res = res3;
                        } catch (Throwable th5) {
                            th = th5;
                            res = res3;
                            processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                            throw th;
                        }
                    } else {
                        res3 = res2;
                        usersToLoad2 = usersToLoad;
                    }
                    usersToLoad = usersToLoad2;
                    res2 = res3;
                } catch (Exception e5) {
                    e = e5;
                    res = res2;
                    res.messages.clear();
                    res.chats.clear();
                    res.users.clear();
                    FileLog.e((Throwable) e);
                    processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                    boolean z22 = topReached;
                } catch (Throwable th6) {
                    th = th6;
                    res = res2;
                    processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                    throw th;
                }
            }
            res3 = res2;
            ArrayList<Integer> usersToLoad4 = usersToLoad;
            try {
                cursor.dispose();
                if (!usersToLoad4.isEmpty()) {
                    getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad4), res3.users);
                }
                if (!chatsToLoad.isEmpty()) {
                    getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), res3.chats);
                }
                if (res3.messages.size() > i) {
                    try {
                        res3.messages.remove(res3.messages.size() - 1);
                        topReached2 = false;
                    } catch (Exception e6) {
                        e = e6;
                        topReached = false;
                        res = res3;
                        res.messages.clear();
                        res.chats.clear();
                        res.users.clear();
                        FileLog.e((Throwable) e);
                        processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                        boolean z222 = topReached;
                    } catch (Throwable th7) {
                        th = th7;
                        topReached = false;
                        res = res3;
                        processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                        throw th;
                    }
                } else {
                    topReached2 = isEnd;
                }
                TLRPC.TL_messages_messages tL_messages_messages = res3;
                processLoadedMedia(res3, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached2);
            } catch (Exception e7) {
                e = e7;
                res = res3;
                res.messages.clear();
                res.chats.clear();
                res.users.clear();
                FileLog.e((Throwable) e);
                processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                boolean z2222 = topReached;
            } catch (Throwable th8) {
                th = th8;
                res = res3;
                processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
                throw th;
            }
        } catch (Exception e8) {
            e = e8;
            topReached = false;
            res = res4;
            res.messages.clear();
            res.chats.clear();
            res.users.clear();
            FileLog.e((Throwable) e);
            processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
            boolean z22222 = topReached;
        } catch (Throwable th9) {
            th = th9;
            topReached = false;
            res = res4;
            processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
            throw th;
        }
    }

    private void putMediaDatabase(long uid, int type, ArrayList<TLRPC.Message> messages, int max_id, boolean topReached) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(messages, topReached, uid, max_id, type) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void run() {
                MediaDataController.this.lambda$putMediaDatabase$66$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0125  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$putMediaDatabase$66$MediaDataController(java.util.ArrayList r18, boolean r19, long r20, int r22, int r23) {
        /*
            r17 = this;
            r11 = r20
            r13 = r22
            r14 = r23
            r1 = 0
            r2 = 0
            boolean r0 = r18.isEmpty()     // Catch:{ Exception -> 0x010f }
            if (r0 != 0) goto L_0x0010
            if (r19 == 0) goto L_0x0028
        L_0x0010:
            im.bclpbkiauv.messenger.MessagesStorage r0 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x010f }
            r0.doneHolesInMedia(r11, r13, r14)     // Catch:{ Exception -> 0x010f }
            boolean r0 = r18.isEmpty()     // Catch:{ Exception -> 0x010f }
            if (r0 == 0) goto L_0x0028
            if (r1 == 0) goto L_0x0022
            r1.reuse()
        L_0x0022:
            if (r2 == 0) goto L_0x0027
            r2.dispose()
        L_0x0027:
            return
        L_0x0028:
            im.bclpbkiauv.messenger.MessagesStorage r0 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x0034 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x0034 }
            r0.beginTransaction()     // Catch:{ Exception -> 0x0034 }
            goto L_0x003a
        L_0x0034:
            r0 = move-exception
            java.lang.String r3 = "putMediaDatabase ---> exception 1 "
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r3, (java.lang.Throwable) r0)     // Catch:{ Exception -> 0x010f }
        L_0x003a:
            im.bclpbkiauv.messenger.MessagesStorage r0 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x010f }
            im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x010f }
            java.lang.String r3 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)"
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r0.executeFast(r3)     // Catch:{ Exception -> 0x010f }
            r2 = r0
            java.util.Iterator r0 = r18.iterator()     // Catch:{ Exception -> 0x010f }
            r15 = r1
        L_0x004e:
            boolean r1 = r0.hasNext()     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            r3 = 1
            if (r1 == 0) goto L_0x00a9
            java.lang.Object r1 = r0.next()     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = (im.bclpbkiauv.tgnet.TLRPC.Message) r1     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            boolean r4 = canAddMessageToMedia(r1)     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            if (r4 == 0) goto L_0x00a8
            int r4 = r1.id     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            long r4 = (long) r4     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            im.bclpbkiauv.tgnet.TLRPC$Peer r6 = r1.to_id     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            int r6 = r6.channel_id     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            if (r6 == 0) goto L_0x0073
            im.bclpbkiauv.tgnet.TLRPC$Peer r6 = r1.to_id     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            int r6 = r6.channel_id     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            long r6 = (long) r6     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            r8 = 32
            long r6 = r6 << r8
            long r4 = r4 | r6
        L_0x0073:
            r2.requery()     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            im.bclpbkiauv.tgnet.NativeByteBuffer r6 = new im.bclpbkiauv.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            int r7 = r1.getObjectSize()     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            r6.<init>((int) r7)     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            r1.serializeToStream(r6)     // Catch:{ Exception -> 0x00a4, all -> 0x00a0 }
            r2.bindLong(r3, r4)     // Catch:{ Exception -> 0x00a4, all -> 0x00a0 }
            r3 = 2
            r2.bindLong(r3, r11)     // Catch:{ Exception -> 0x00a4, all -> 0x00a0 }
            r3 = 3
            int r7 = r1.date     // Catch:{ Exception -> 0x00a4, all -> 0x00a0 }
            r2.bindInteger(r3, r7)     // Catch:{ Exception -> 0x00a4, all -> 0x00a0 }
            r3 = 4
            r2.bindInteger(r3, r14)     // Catch:{ Exception -> 0x00a4, all -> 0x00a0 }
            r3 = 5
            r2.bindByteBuffer((int) r3, (im.bclpbkiauv.tgnet.NativeByteBuffer) r6)     // Catch:{ Exception -> 0x00a4, all -> 0x00a0 }
            r2.step()     // Catch:{ Exception -> 0x00a4, all -> 0x00a0 }
            r6.reuse()     // Catch:{ Exception -> 0x00a4, all -> 0x00a0 }
            r3 = 0
            r15 = r3
            goto L_0x00a8
        L_0x00a0:
            r0 = move-exception
            r1 = r6
            goto L_0x0123
        L_0x00a4:
            r0 = move-exception
            r1 = r6
            goto L_0x0110
        L_0x00a8:
            goto L_0x004e
        L_0x00a9:
            r2.dispose()     // Catch:{ Exception -> 0x010a, all -> 0x0107 }
            r16 = 0
            if (r19 == 0) goto L_0x00b2
            if (r13 == 0) goto L_0x00e7
        L_0x00b2:
            if (r19 == 0) goto L_0x00b8
            r10 = r18
            r4 = 1
            goto L_0x00c8
        L_0x00b8:
            int r0 = r18.size()     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            int r0 = r0 - r3
            r10 = r18
            java.lang.Object r0 = r10.get(r0)     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = (im.bclpbkiauv.tgnet.TLRPC.Message) r0     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            int r0 = r0.id     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            r4 = r0
        L_0x00c8:
            if (r13 == 0) goto L_0x00d8
            im.bclpbkiauv.messenger.MessagesStorage r1 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            r2 = r20
            r5 = r22
            r6 = r23
            r1.closeHolesInMedia(r2, r4, r5, r6)     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            goto L_0x00e7
        L_0x00d8:
            im.bclpbkiauv.messenger.MessagesStorage r5 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            r9 = 2147483647(0x7fffffff, float:NaN)
            r6 = r20
            r8 = r4
            r10 = r23
            r5.closeHolesInMedia(r6, r8, r9, r10)     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
        L_0x00e7:
            im.bclpbkiauv.messenger.MessagesStorage r0 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            r0.commitTransaction()     // Catch:{ Exception -> 0x0102, all -> 0x00fd }
            if (r15 == 0) goto L_0x00f7
            r15.reuse()
        L_0x00f7:
            if (r16 == 0) goto L_0x0122
            r16.dispose()
            goto L_0x0122
        L_0x00fd:
            r0 = move-exception
            r1 = r15
            r2 = r16
            goto L_0x0123
        L_0x0102:
            r0 = move-exception
            r1 = r15
            r2 = r16
            goto L_0x0110
        L_0x0107:
            r0 = move-exception
            r1 = r15
            goto L_0x0123
        L_0x010a:
            r0 = move-exception
            r1 = r15
            goto L_0x0110
        L_0x010d:
            r0 = move-exception
            goto L_0x0123
        L_0x010f:
            r0 = move-exception
        L_0x0110:
            java.lang.String r3 = "putMediaDatabase ---> exception 2 "
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r3, (java.lang.Throwable) r0)     // Catch:{ all -> 0x010d }
            if (r1 == 0) goto L_0x011a
            r1.reuse()
        L_0x011a:
            if (r2 == 0) goto L_0x011f
            r2.dispose()
        L_0x011f:
            r15 = r1
            r16 = r2
        L_0x0122:
            return
        L_0x0123:
            if (r1 == 0) goto L_0x0128
            r1.reuse()
        L_0x0128:
            if (r2 == 0) goto L_0x012d
            r2.dispose()
        L_0x012d:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.lambda$putMediaDatabase$66$MediaDataController(java.util.ArrayList, boolean, long, int, int):void");
    }

    public void loadMusic(long uid, long max_id) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(uid, max_id) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$loadMusic$68$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:7:0x0068 A[Catch:{ Exception -> 0x009f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadMusic$68$MediaDataController(long r12, long r14) {
        /*
            r11 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r1 = (int) r12
            r2 = 4
            r3 = 2
            r4 = 3
            r5 = 1
            r6 = 0
            if (r1 == 0) goto L_0x0038
            im.bclpbkiauv.messenger.MessagesStorage r7 = r11.getMessagesStorage()     // Catch:{ Exception -> 0x009f }
            im.bclpbkiauv.sqlite.SQLiteDatabase r7 = r7.getDatabase()     // Catch:{ Exception -> 0x009f }
            java.util.Locale r8 = java.util.Locale.US     // Catch:{ Exception -> 0x009f }
            java.lang.String r9 = "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x009f }
            java.lang.Long r10 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x009f }
            r4[r6] = r10     // Catch:{ Exception -> 0x009f }
            java.lang.Long r10 = java.lang.Long.valueOf(r14)     // Catch:{ Exception -> 0x009f }
            r4[r5] = r10     // Catch:{ Exception -> 0x009f }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x009f }
            r4[r3] = r2     // Catch:{ Exception -> 0x009f }
            java.lang.String r2 = java.lang.String.format(r8, r9, r4)     // Catch:{ Exception -> 0x009f }
            java.lang.Object[] r3 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x009f }
            im.bclpbkiauv.sqlite.SQLiteCursor r2 = r7.queryFinalized(r2, r3)     // Catch:{ Exception -> 0x009f }
            goto L_0x0062
        L_0x0038:
            im.bclpbkiauv.messenger.MessagesStorage r7 = r11.getMessagesStorage()     // Catch:{ Exception -> 0x009f }
            im.bclpbkiauv.sqlite.SQLiteDatabase r7 = r7.getDatabase()     // Catch:{ Exception -> 0x009f }
            java.util.Locale r8 = java.util.Locale.US     // Catch:{ Exception -> 0x009f }
            java.lang.String r9 = "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x009f }
            java.lang.Long r10 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x009f }
            r4[r6] = r10     // Catch:{ Exception -> 0x009f }
            java.lang.Long r10 = java.lang.Long.valueOf(r14)     // Catch:{ Exception -> 0x009f }
            r4[r5] = r10     // Catch:{ Exception -> 0x009f }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x009f }
            r4[r3] = r2     // Catch:{ Exception -> 0x009f }
            java.lang.String r2 = java.lang.String.format(r8, r9, r4)     // Catch:{ Exception -> 0x009f }
            java.lang.Object[] r3 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x009f }
            im.bclpbkiauv.sqlite.SQLiteCursor r2 = r7.queryFinalized(r2, r3)     // Catch:{ Exception -> 0x009f }
        L_0x0062:
            boolean r3 = r2.next()     // Catch:{ Exception -> 0x009f }
            if (r3 == 0) goto L_0x009b
            im.bclpbkiauv.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r6)     // Catch:{ Exception -> 0x009f }
            if (r3 == 0) goto L_0x009a
            int r4 = r3.readInt32(r6)     // Catch:{ Exception -> 0x009f }
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = im.bclpbkiauv.tgnet.TLRPC.Message.TLdeserialize(r3, r4, r6)     // Catch:{ Exception -> 0x009f }
            im.bclpbkiauv.messenger.UserConfig r7 = r11.getUserConfig()     // Catch:{ Exception -> 0x009f }
            int r7 = r7.clientUserId     // Catch:{ Exception -> 0x009f }
            r4.readAttachPath(r3, r7)     // Catch:{ Exception -> 0x009f }
            r3.reuse()     // Catch:{ Exception -> 0x009f }
            boolean r7 = im.bclpbkiauv.messenger.MessageObject.isMusicMessage(r4)     // Catch:{ Exception -> 0x009f }
            if (r7 == 0) goto L_0x009a
            int r7 = r2.intValue(r5)     // Catch:{ Exception -> 0x009f }
            r4.id = r7     // Catch:{ Exception -> 0x009f }
            r4.dialog_id = r12     // Catch:{ Exception -> 0x009f }
            im.bclpbkiauv.messenger.MessageObject r7 = new im.bclpbkiauv.messenger.MessageObject     // Catch:{ Exception -> 0x009f }
            int r8 = r11.currentAccount     // Catch:{ Exception -> 0x009f }
            r7.<init>(r8, r4, r6)     // Catch:{ Exception -> 0x009f }
            r0.add(r6, r7)     // Catch:{ Exception -> 0x009f }
        L_0x009a:
            goto L_0x0062
        L_0x009b:
            r2.dispose()     // Catch:{ Exception -> 0x009f }
            goto L_0x00a3
        L_0x009f:
            r1 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x00a3:
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$u3JtnIw1FyN6w6h8PQTNXmk81nQ r1 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$u3JtnIw1FyN6w6h8PQTNXmk81nQ
            r1.<init>(r12, r0)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.lambda$loadMusic$68$MediaDataController(long, long):void");
    }

    public /* synthetic */ void lambda$null$67$MediaDataController(long uid, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.musicDidLoad, Long.valueOf(uid), arrayList);
    }

    public void buildShortcuts() {
        if (Build.VERSION.SDK_INT >= 25) {
            ArrayList<TLRPC.TL_topPeer> hintsFinal = new ArrayList<>();
            for (int a = 0; a < this.hints.size(); a++) {
                hintsFinal.add(this.hints.get(a));
                if (hintsFinal.size() == 3) {
                    break;
                }
            }
            Utilities.globalQueue.postRunnable(new Runnable(hintsFinal) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$buildShortcuts$69$MediaDataController(this.f$1);
                }
            });
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:105:0x02d7 A[Catch:{ all -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x02dc A[Catch:{ all -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x02f3 A[Catch:{ all -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x02fb A[Catch:{ all -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0314 A[Catch:{ all -> 0x0335 }] */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0318 A[Catch:{ all -> 0x0335 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$buildShortcuts$69$MediaDataController(java.util.ArrayList r30) {
        /*
            r29 = this;
            r1 = r30
            java.lang.String r0 = "NewConversationShortcut"
            android.content.Context r2 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0335 }
            java.lang.Class<android.content.pm.ShortcutManager> r3 = android.content.pm.ShortcutManager.class
            java.lang.Object r2 = r2.getSystemService(r3)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutManager r2 = (android.content.pm.ShortcutManager) r2     // Catch:{ all -> 0x0335 }
            java.util.List r3 = r2.getDynamicShortcuts()     // Catch:{ all -> 0x0335 }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x0335 }
            r4.<init>()     // Catch:{ all -> 0x0335 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0335 }
            r5.<init>()     // Catch:{ all -> 0x0335 }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0335 }
            r6.<init>()     // Catch:{ all -> 0x0335 }
            java.lang.String r7 = "did"
            java.lang.String r8 = "compose"
            if (r3 == 0) goto L_0x009f
            boolean r9 = r3.isEmpty()     // Catch:{ all -> 0x0335 }
            if (r9 != 0) goto L_0x009f
            r5.add(r8)     // Catch:{ all -> 0x0335 }
            r9 = 0
        L_0x0031:
            int r10 = r30.size()     // Catch:{ all -> 0x0335 }
            if (r9 >= r10) goto L_0x0071
            java.lang.Object r10 = r1.get(r9)     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$TL_topPeer r10 = (im.bclpbkiauv.tgnet.TLRPC.TL_topPeer) r10     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$Peer r11 = r10.peer     // Catch:{ all -> 0x0335 }
            int r11 = r11.user_id     // Catch:{ all -> 0x0335 }
            if (r11 == 0) goto L_0x0049
            im.bclpbkiauv.tgnet.TLRPC$Peer r11 = r10.peer     // Catch:{ all -> 0x0335 }
            int r11 = r11.user_id     // Catch:{ all -> 0x0335 }
            long r11 = (long) r11     // Catch:{ all -> 0x0335 }
            goto L_0x005b
        L_0x0049:
            im.bclpbkiauv.tgnet.TLRPC$Peer r11 = r10.peer     // Catch:{ all -> 0x0335 }
            int r11 = r11.chat_id     // Catch:{ all -> 0x0335 }
            int r11 = -r11
            long r11 = (long) r11     // Catch:{ all -> 0x0335 }
            r13 = 0
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 != 0) goto L_0x005b
            im.bclpbkiauv.tgnet.TLRPC$Peer r13 = r10.peer     // Catch:{ all -> 0x0335 }
            int r13 = r13.channel_id     // Catch:{ all -> 0x0335 }
            int r13 = -r13
            long r11 = (long) r13     // Catch:{ all -> 0x0335 }
        L_0x005b:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x0335 }
            r13.<init>()     // Catch:{ all -> 0x0335 }
            r13.append(r7)     // Catch:{ all -> 0x0335 }
            r13.append(r11)     // Catch:{ all -> 0x0335 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x0335 }
            r5.add(r13)     // Catch:{ all -> 0x0335 }
            int r9 = r9 + 1
            goto L_0x0031
        L_0x0071:
            r9 = 0
        L_0x0072:
            int r10 = r3.size()     // Catch:{ all -> 0x0335 }
            if (r9 >= r10) goto L_0x0092
            java.lang.Object r10 = r3.get(r9)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo r10 = (android.content.pm.ShortcutInfo) r10     // Catch:{ all -> 0x0335 }
            java.lang.String r10 = r10.getId()     // Catch:{ all -> 0x0335 }
            boolean r11 = r5.remove(r10)     // Catch:{ all -> 0x0335 }
            if (r11 != 0) goto L_0x008b
            r6.add(r10)     // Catch:{ all -> 0x0335 }
        L_0x008b:
            r4.add(r10)     // Catch:{ all -> 0x0335 }
            int r9 = r9 + 1
            goto L_0x0072
        L_0x0092:
            boolean r9 = r5.isEmpty()     // Catch:{ all -> 0x0335 }
            if (r9 == 0) goto L_0x009f
            boolean r9 = r6.isEmpty()     // Catch:{ all -> 0x0335 }
            if (r9 == 0) goto L_0x009f
            return
        L_0x009f:
            android.content.Intent r9 = new android.content.Intent     // Catch:{ all -> 0x0335 }
            android.content.Context r10 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0335 }
            java.lang.Class<im.bclpbkiauv.ui.LaunchActivity> r11 = im.bclpbkiauv.ui.LaunchActivity.class
            r9.<init>(r10, r11)     // Catch:{ all -> 0x0335 }
            java.lang.String r10 = "new_dialog"
            r9.setAction(r10)     // Catch:{ all -> 0x0335 }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ all -> 0x0335 }
            r10.<init>()     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo$Builder r11 = new android.content.pm.ShortcutInfo$Builder     // Catch:{ all -> 0x0335 }
            android.content.Context r12 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0335 }
            r11.<init>(r12, r8)     // Catch:{ all -> 0x0335 }
            r12 = 2131692155(0x7f0f0a7b, float:1.9013402E38)
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.getString(r0, r12)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo$Builder r11 = r11.setShortLabel(r13)     // Catch:{ all -> 0x0335 }
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0, r12)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo$Builder r0 = r11.setLongLabel(r0)     // Catch:{ all -> 0x0335 }
            android.content.Context r11 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0335 }
            r12 = 2131231574(0x7f080356, float:1.8079233E38)
            android.graphics.drawable.Icon r11 = android.graphics.drawable.Icon.createWithResource(r11, r12)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo$Builder r0 = r0.setIcon(r11)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo$Builder r0 = r0.setIntent(r9)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo r0 = r0.build()     // Catch:{ all -> 0x0335 }
            r10.add(r0)     // Catch:{ all -> 0x0335 }
            boolean r0 = r4.contains(r8)     // Catch:{ all -> 0x0335 }
            if (r0 == 0) goto L_0x00ee
            r2.updateShortcuts(r10)     // Catch:{ all -> 0x0335 }
            goto L_0x00f1
        L_0x00ee:
            r2.addDynamicShortcuts(r10)     // Catch:{ all -> 0x0335 }
        L_0x00f1:
            r10.clear()     // Catch:{ all -> 0x0335 }
            boolean r0 = r6.isEmpty()     // Catch:{ all -> 0x0335 }
            if (r0 != 0) goto L_0x00fd
            r2.removeDynamicShortcuts(r6)     // Catch:{ all -> 0x0335 }
        L_0x00fd:
            r0 = 0
            r8 = r0
        L_0x00ff:
            int r0 = r30.size()     // Catch:{ all -> 0x0335 }
            if (r8 >= r0) goto L_0x032c
            android.content.Intent r0 = new android.content.Intent     // Catch:{ all -> 0x0335 }
            android.content.Context r11 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0335 }
            java.lang.Class<im.bclpbkiauv.messenger.OpenChatReceiver> r12 = im.bclpbkiauv.messenger.OpenChatReceiver.class
            r0.<init>(r11, r12)     // Catch:{ all -> 0x0335 }
            r11 = r0
            java.lang.Object r0 = r1.get(r8)     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$TL_topPeer r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_topPeer) r0     // Catch:{ all -> 0x0335 }
            r12 = r0
            r0 = 0
            r13 = 0
            im.bclpbkiauv.tgnet.TLRPC$Peer r14 = r12.peer     // Catch:{ all -> 0x0335 }
            int r14 = r14.user_id     // Catch:{ all -> 0x0335 }
            if (r14 == 0) goto L_0x0140
            java.lang.String r14 = "userId"
            im.bclpbkiauv.tgnet.TLRPC$Peer r15 = r12.peer     // Catch:{ all -> 0x0335 }
            int r15 = r15.user_id     // Catch:{ all -> 0x0335 }
            r11.putExtra(r14, r15)     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.messenger.MessagesController r14 = r29.getMessagesController()     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$Peer r15 = r12.peer     // Catch:{ all -> 0x0335 }
            int r15 = r15.user_id     // Catch:{ all -> 0x0335 }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$User r14 = r14.getUser(r15)     // Catch:{ all -> 0x0335 }
            r0 = r14
            im.bclpbkiauv.tgnet.TLRPC$Peer r14 = r12.peer     // Catch:{ all -> 0x0335 }
            int r14 = r14.user_id     // Catch:{ all -> 0x0335 }
            long r14 = (long) r14     // Catch:{ all -> 0x0335 }
            r1 = r0
            goto L_0x0164
        L_0x0140:
            im.bclpbkiauv.tgnet.TLRPC$Peer r14 = r12.peer     // Catch:{ all -> 0x0335 }
            int r14 = r14.chat_id     // Catch:{ all -> 0x0335 }
            if (r14 != 0) goto L_0x014b
            im.bclpbkiauv.tgnet.TLRPC$Peer r15 = r12.peer     // Catch:{ all -> 0x0335 }
            int r15 = r15.channel_id     // Catch:{ all -> 0x0335 }
            r14 = r15
        L_0x014b:
            im.bclpbkiauv.messenger.MessagesController r15 = r29.getMessagesController()     // Catch:{ all -> 0x0335 }
            r16 = r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r14)     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r15.getChat(r0)     // Catch:{ all -> 0x0335 }
            r13 = r0
            java.lang.String r0 = "chatId"
            r11.putExtra(r0, r14)     // Catch:{ all -> 0x0335 }
            int r0 = -r14
            long r0 = (long) r0     // Catch:{ all -> 0x0335 }
            r14 = r0
            r1 = r16
        L_0x0164:
            if (r1 == 0) goto L_0x016c
            boolean r0 = im.bclpbkiauv.messenger.UserObject.isDeleted(r1)     // Catch:{ all -> 0x0335 }
            if (r0 == 0) goto L_0x0178
        L_0x016c:
            if (r13 != 0) goto L_0x0178
            r17 = r3
            r19 = r5
            r25 = r6
            r21 = r9
            goto L_0x031e
        L_0x0178:
            r0 = 0
            if (r1 == 0) goto L_0x0197
            r16 = r0
            java.lang.String r0 = r1.first_name     // Catch:{ all -> 0x0335 }
            r17 = r3
            java.lang.String r3 = r1.last_name     // Catch:{ all -> 0x0335 }
            java.lang.String r0 = im.bclpbkiauv.messenger.ContactsController.formatName(r0, r3)     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r3 = r1.photo     // Catch:{ all -> 0x0335 }
            if (r3 == 0) goto L_0x0192
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r3 = r1.photo     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r3 = r3.photo_small     // Catch:{ all -> 0x0335 }
            r16 = r0
            goto L_0x01ac
        L_0x0192:
            r3 = r16
            r16 = r0
            goto L_0x01ac
        L_0x0197:
            r16 = r0
            r17 = r3
            java.lang.String r0 = r13.title     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r3 = r13.photo     // Catch:{ all -> 0x0335 }
            if (r3 == 0) goto L_0x01a8
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r3 = r13.photo     // Catch:{ all -> 0x0335 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r3 = r3.photo_small     // Catch:{ all -> 0x0335 }
            r16 = r0
            goto L_0x01ac
        L_0x01a8:
            r3 = r16
            r16 = r0
        L_0x01ac:
            java.lang.String r0 = "currentAccount"
            r18 = r1
            r19 = r5
            r1 = r29
            int r5 = r1.currentAccount     // Catch:{ all -> 0x0335 }
            r11.putExtra(r0, r5)     // Catch:{ all -> 0x0335 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0335 }
            r0.<init>()     // Catch:{ all -> 0x0335 }
            java.lang.String r5 = "com.tmessages.openchat"
            r0.append(r5)     // Catch:{ all -> 0x0335 }
            r0.append(r14)     // Catch:{ all -> 0x0335 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0335 }
            r11.setAction(r0)     // Catch:{ all -> 0x0335 }
            r0 = 67108864(0x4000000, float:1.5046328E-36)
            r11.addFlags(r0)     // Catch:{ all -> 0x0335 }
            r5 = 0
            if (r3 == 0) goto L_0x02ba
            r0 = 1
            java.io.File r20 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r3, r0)     // Catch:{ all -> 0x02ad }
            java.lang.String r21 = r20.toString()     // Catch:{ all -> 0x02ad }
            android.graphics.Bitmap r21 = android.graphics.BitmapFactory.decodeFile(r21)     // Catch:{ all -> 0x02ad }
            r5 = r21
            if (r5 == 0) goto L_0x02a4
            r21 = 1111490560(0x42400000, float:48.0)
            int r21 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)     // Catch:{ all -> 0x02ad }
            r22 = r21
            android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x02ad }
            r1 = r22
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r1, r1, r0)     // Catch:{ all -> 0x02ad }
            r22 = r0
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ all -> 0x02ad }
            r23 = r3
            r3 = r22
            r0.<init>(r3)     // Catch:{ all -> 0x029c }
            r22 = r0
            android.graphics.Paint r0 = roundPaint     // Catch:{ all -> 0x029c }
            r24 = 1073741824(0x40000000, float:2.0)
            if (r0 != 0) goto L_0x025e
            android.graphics.Paint r0 = new android.graphics.Paint     // Catch:{ all -> 0x029c }
            r25 = r6
            r6 = 3
            r0.<init>(r6)     // Catch:{ all -> 0x0258 }
            roundPaint = r0     // Catch:{ all -> 0x0258 }
            android.graphics.RectF r0 = new android.graphics.RectF     // Catch:{ all -> 0x0258 }
            r0.<init>()     // Catch:{ all -> 0x0258 }
            bitmapRect = r0     // Catch:{ all -> 0x0258 }
            android.graphics.Paint r0 = new android.graphics.Paint     // Catch:{ all -> 0x0258 }
            r6 = 1
            r0.<init>(r6)     // Catch:{ all -> 0x0258 }
            erasePaint = r0     // Catch:{ all -> 0x0258 }
            android.graphics.PorterDuffXfermode r6 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x0258 }
            r21 = r9
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x0253 }
            r6.<init>(r9)     // Catch:{ all -> 0x0253 }
            r0.setXfermode(r6)     // Catch:{ all -> 0x0253 }
            android.graphics.Path r0 = new android.graphics.Path     // Catch:{ all -> 0x0253 }
            r0.<init>()     // Catch:{ all -> 0x0253 }
            roundPath = r0     // Catch:{ all -> 0x0253 }
            int r6 = r1 / 2
            float r6 = (float) r6     // Catch:{ all -> 0x0253 }
            int r9 = r1 / 2
            float r9 = (float) r9     // Catch:{ all -> 0x0253 }
            int r26 = r1 / 2
            int r27 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)     // Catch:{ all -> 0x0253 }
            r28 = r1
            int r1 = r26 - r27
            float r1 = (float) r1
            r26 = r12
            android.graphics.Path$Direction r12 = android.graphics.Path.Direction.CW     // Catch:{ all -> 0x029a }
            r0.addCircle(r6, r9, r1, r12)     // Catch:{ all -> 0x029a }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x029a }
            r0.toggleInverseFillType()     // Catch:{ all -> 0x029a }
            goto L_0x0266
        L_0x0253:
            r0 = move-exception
            r26 = r12
            goto L_0x02b6
        L_0x0258:
            r0 = move-exception
            r21 = r9
            r26 = r12
            goto L_0x02b6
        L_0x025e:
            r28 = r1
            r25 = r6
            r21 = r9
            r26 = r12
        L_0x0266:
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x029a }
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)     // Catch:{ all -> 0x029a }
            float r1 = (float) r1     // Catch:{ all -> 0x029a }
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)     // Catch:{ all -> 0x029a }
            float r6 = (float) r6     // Catch:{ all -> 0x029a }
            r9 = 1110966272(0x42380000, float:46.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x029a }
            float r12 = (float) r12     // Catch:{ all -> 0x029a }
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x029a }
            float r9 = (float) r9     // Catch:{ all -> 0x029a }
            r0.set(r1, r6, r12, r9)     // Catch:{ all -> 0x029a }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x029a }
            android.graphics.Paint r1 = roundPaint     // Catch:{ all -> 0x029a }
            r6 = 0
            r9 = r22
            r9.drawBitmap(r5, r6, r0, r1)     // Catch:{ all -> 0x029a }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x029a }
            android.graphics.Paint r1 = erasePaint     // Catch:{ all -> 0x029a }
            r9.drawPath(r0, r1)     // Catch:{ all -> 0x029a }
            r9.setBitmap(r6)     // Catch:{ Exception -> 0x0296 }
            goto L_0x0297
        L_0x0296:
            r0 = move-exception
        L_0x0297:
            r0 = r3
            r5 = r0
            goto L_0x02ac
        L_0x029a:
            r0 = move-exception
            goto L_0x02b6
        L_0x029c:
            r0 = move-exception
            r25 = r6
            r21 = r9
            r26 = r12
            goto L_0x02b6
        L_0x02a4:
            r23 = r3
            r25 = r6
            r21 = r9
            r26 = r12
        L_0x02ac:
            goto L_0x02c2
        L_0x02ad:
            r0 = move-exception
            r23 = r3
            r25 = r6
            r21 = r9
            r26 = r12
        L_0x02b6:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0335 }
            goto L_0x02c2
        L_0x02ba:
            r23 = r3
            r25 = r6
            r21 = r9
            r26 = r12
        L_0x02c2:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0335 }
            r0.<init>()     // Catch:{ all -> 0x0335 }
            r0.append(r7)     // Catch:{ all -> 0x0335 }
            r0.append(r14)     // Catch:{ all -> 0x0335 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0335 }
            boolean r1 = android.text.TextUtils.isEmpty(r16)     // Catch:{ all -> 0x0335 }
            if (r1 == 0) goto L_0x02dc
            java.lang.String r1 = " "
            r16 = r1
            goto L_0x02de
        L_0x02dc:
            r1 = r16
        L_0x02de:
            android.content.pm.ShortcutInfo$Builder r3 = new android.content.pm.ShortcutInfo$Builder     // Catch:{ all -> 0x0335 }
            android.content.Context r6 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0335 }
            r3.<init>(r6, r0)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo$Builder r3 = r3.setShortLabel(r1)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo$Builder r3 = r3.setLongLabel(r1)     // Catch:{ all -> 0x0335 }
            android.content.pm.ShortcutInfo$Builder r3 = r3.setIntent(r11)     // Catch:{ all -> 0x0335 }
            if (r5 == 0) goto L_0x02fb
            android.graphics.drawable.Icon r6 = android.graphics.drawable.Icon.createWithBitmap(r5)     // Catch:{ all -> 0x0335 }
            r3.setIcon(r6)     // Catch:{ all -> 0x0335 }
            goto L_0x0307
        L_0x02fb:
            android.content.Context r6 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0335 }
            r9 = 2131231575(0x7f080357, float:1.8079235E38)
            android.graphics.drawable.Icon r6 = android.graphics.drawable.Icon.createWithResource(r6, r9)     // Catch:{ all -> 0x0335 }
            r3.setIcon(r6)     // Catch:{ all -> 0x0335 }
        L_0x0307:
            android.content.pm.ShortcutInfo r6 = r3.build()     // Catch:{ all -> 0x0335 }
            r10.add(r6)     // Catch:{ all -> 0x0335 }
            boolean r6 = r4.contains(r0)     // Catch:{ all -> 0x0335 }
            if (r6 == 0) goto L_0x0318
            r2.updateShortcuts(r10)     // Catch:{ all -> 0x0335 }
            goto L_0x031b
        L_0x0318:
            r2.addDynamicShortcuts(r10)     // Catch:{ all -> 0x0335 }
        L_0x031b:
            r10.clear()     // Catch:{ all -> 0x0335 }
        L_0x031e:
            int r8 = r8 + 1
            r1 = r30
            r3 = r17
            r5 = r19
            r9 = r21
            r6 = r25
            goto L_0x00ff
        L_0x032c:
            r17 = r3
            r19 = r5
            r25 = r6
            r21 = r9
            goto L_0x0336
        L_0x0335:
            r0 = move-exception
        L_0x0336:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.lambda$buildShortcuts$69$MediaDataController(java.util.ArrayList):void");
    }

    public void loadHints(boolean cache) {
        if (!this.loading && getUserConfig().suggestContacts) {
            if (!cache) {
                this.loading = true;
                TLRPC.TL_contacts_getTopPeers req = new TLRPC.TL_contacts_getTopPeers();
                req.hash = 0;
                req.bots_pm = false;
                req.correspondents = true;
                req.groups = false;
                req.channels = false;
                req.bots_inline = true;
                req.offset = 0;
                req.limit = 20;
                getConnectionsManager().sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$loadHints$76$MediaDataController(tLObject, tL_error);
                    }
                });
            } else if (!this.loaded) {
                this.loading = true;
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                    public final void run() {
                        MediaDataController.this.lambda$loadHints$71$MediaDataController();
                    }
                });
                this.loaded = true;
            }
        }
    }

    public /* synthetic */ void lambda$loadHints$71$MediaDataController() {
        ArrayList<TLRPC.TL_topPeer> hintsNew = new ArrayList<>();
        ArrayList<TLRPC.TL_topPeer> inlineBotsNew = new ArrayList<>();
        ArrayList<TLRPC.User> users = new ArrayList<>();
        ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        int selfUserId = getUserConfig().getClientUserId();
        try {
            ArrayList<Integer> usersToLoad = new ArrayList<>();
            ArrayList arrayList = new ArrayList();
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
            while (cursor.next()) {
                int did = cursor.intValue(0);
                if (did != selfUserId) {
                    int type = cursor.intValue(1);
                    TLRPC.TL_topPeer peer = new TLRPC.TL_topPeer();
                    peer.rating = cursor.doubleValue(2);
                    if (did > 0) {
                        peer.peer = new TLRPC.TL_peerUser();
                        peer.peer.user_id = did;
                        usersToLoad.add(Integer.valueOf(did));
                    } else {
                        peer.peer = new TLRPC.TL_peerChat();
                        peer.peer.chat_id = -did;
                        arrayList.add(Integer.valueOf(-did));
                    }
                    if (type == 0) {
                        hintsNew.add(peer);
                    } else if (type == 1) {
                        inlineBotsNew.add(peer);
                    }
                }
            }
            cursor.dispose();
            if (!usersToLoad.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
            if (!arrayList.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList), chats);
            }
            AndroidUtilities.runOnUIThread(new Runnable(users, chats, hintsNew, inlineBotsNew) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;
                private final /* synthetic */ ArrayList f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$70$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$70$MediaDataController(ArrayList users, ArrayList chats, ArrayList hintsNew, ArrayList inlineBotsNew) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        this.loading = false;
        this.loaded = true;
        this.hints = hintsNew;
        this.inlineBots = inlineBotsNew;
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        if (Math.abs(getUserConfig().lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
            loadHints(false);
        }
    }

    public /* synthetic */ void lambda$loadHints$76$MediaDataController(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$74$MediaDataController(this.f$1);
                }
            });
        } else if (response instanceof TLRPC.TL_contacts_topPeersDisabled) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MediaDataController.this.lambda$null$75$MediaDataController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$74$MediaDataController(TLObject response) {
        TLRPC.TL_contacts_topPeers topPeers = (TLRPC.TL_contacts_topPeers) response;
        getMessagesController().putUsers(topPeers.users, false);
        getMessagesController().putChats(topPeers.chats, false);
        for (int a = 0; a < topPeers.categories.size(); a++) {
            TLRPC.TL_topPeerCategoryPeers category = topPeers.categories.get(a);
            if (category.category instanceof TLRPC.TL_topPeerCategoryBotsInline) {
                this.inlineBots = category.peers;
                getUserConfig().botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
            } else {
                this.hints = category.peers;
                int selfUserId = getUserConfig().getClientUserId();
                int b = 0;
                while (true) {
                    if (b >= this.hints.size()) {
                        break;
                    } else if (this.hints.get(b).peer.user_id == selfUserId) {
                        this.hints.remove(b);
                        break;
                    } else {
                        b++;
                    }
                }
                getUserConfig().ratingLoadTime = (int) (System.currentTimeMillis() / 1000);
            }
        }
        getUserConfig().saveConfig(false);
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(topPeers) {
            private final /* synthetic */ TLRPC.TL_contacts_topPeers f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$null$73$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$73$MediaDataController(TLRPC.TL_contacts_topPeers topPeers) {
        int type;
        int did;
        SQLitePreparedStatement state = null;
        try {
            state = getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1");
            state.stepThis().dispose();
            state = null;
            try {
                getMessagesStorage().getDatabase().beginTransaction();
            } catch (Exception e) {
                FileLog.e("loadHints ---> exception 1 ", (Throwable) e);
            }
            getMessagesStorage().putUsersAndChats(topPeers.users, topPeers.chats, false, true);
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            for (int a = 0; a < topPeers.categories.size(); a++) {
                TLRPC.TL_topPeerCategoryPeers category = topPeers.categories.get(a);
                if (category.category instanceof TLRPC.TL_topPeerCategoryBotsInline) {
                    type = 1;
                } else {
                    type = 0;
                }
                for (int b = 0; b < category.peers.size(); b++) {
                    TLRPC.TL_topPeer peer = category.peers.get(b);
                    if (peer.peer instanceof TLRPC.TL_peerUser) {
                        did = peer.peer.user_id;
                    } else if (peer.peer instanceof TLRPC.TL_peerChat) {
                        did = -peer.peer.chat_id;
                    } else {
                        did = -peer.peer.channel_id;
                    }
                    state2.requery();
                    state2.bindInteger(1, did);
                    state2.bindInteger(2, type);
                    state2.bindDouble(3, peer.rating);
                    state2.bindInteger(4, 0);
                    state2.step();
                }
            }
            state2.dispose();
            state = null;
            getMessagesStorage().getDatabase().commitTransaction();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MediaDataController.this.lambda$null$72$MediaDataController();
                }
            });
            if (0 == 0) {
                return;
            }
        } catch (Exception e2) {
            FileLog.e("loadHints ---> exception 2 ", (Throwable) e2);
            if (state == null) {
                return;
            }
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
        state.dispose();
    }

    public /* synthetic */ void lambda$null$72$MediaDataController() {
        getUserConfig().suggestContacts = true;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
    }

    public /* synthetic */ void lambda$null$75$MediaDataController() {
        getUserConfig().suggestContacts = false;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
        clearTopPeers();
    }

    public void clearTopPeers() {
        this.hints.clear();
        this.inlineBots.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                MediaDataController.this.lambda$clearTopPeers$77$MediaDataController();
            }
        });
        buildShortcuts();
    }

    public /* synthetic */ void lambda$clearTopPeers$77$MediaDataController() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
        } catch (Exception e) {
        }
    }

    public void increaseInlineRaiting(int uid) {
        int dt;
        if (getUserConfig().suggestContacts) {
            if (getUserConfig().botRatingLoadTime != 0) {
                dt = Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - getUserConfig().botRatingLoadTime);
            } else {
                dt = 60;
            }
            TLRPC.TL_topPeer peer = null;
            int a = 0;
            while (true) {
                if (a >= this.inlineBots.size()) {
                    break;
                }
                TLRPC.TL_topPeer p = this.inlineBots.get(a);
                if (p.peer.user_id == uid) {
                    peer = p;
                    break;
                }
                a++;
            }
            if (peer == null) {
                peer = new TLRPC.TL_topPeer();
                peer.peer = new TLRPC.TL_peerUser();
                peer.peer.user_id = uid;
                this.inlineBots.add(peer);
            }
            peer.rating += Math.exp((double) (dt / getMessagesController().ratingDecay));
            Collections.sort(this.inlineBots, $$Lambda$MediaDataController$r0yxURnd7QjcKCwyqd88eWI3Dg.INSTANCE);
            if (this.inlineBots.size() > 20) {
                ArrayList<TLRPC.TL_topPeer> arrayList = this.inlineBots;
                arrayList.remove(arrayList.size() - 1);
            }
            savePeer(uid, 1, peer.rating);
            getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
    }

    static /* synthetic */ int lambda$increaseInlineRaiting$78(TLRPC.TL_topPeer lhs, TLRPC.TL_topPeer rhs) {
        if (lhs.rating > rhs.rating) {
            return -1;
        }
        if (lhs.rating < rhs.rating) {
            return 1;
        }
        return 0;
    }

    public void removeInline(int uid) {
        for (int a = 0; a < this.inlineBots.size(); a++) {
            if (this.inlineBots.get(a).peer.user_id == uid) {
                this.inlineBots.remove(a);
                TLRPC.TL_contacts_resetTopPeerRating req = new TLRPC.TL_contacts_resetTopPeerRating();
                req.category = new TLRPC.TL_topPeerCategoryBotsInline();
                req.peer = getMessagesController().getInputPeer(uid);
                getConnectionsManager().sendRequest(req, $$Lambda$MediaDataController$9Vj3URNLBDMrXxyYoUB9X8OFMo.INSTANCE);
                deletePeer(uid, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    static /* synthetic */ void lambda$removeInline$79(TLObject response, TLRPC.TL_error error) {
    }

    public void removePeer(int uid) {
        for (int a = 0; a < this.hints.size(); a++) {
            if (this.hints.get(a).peer.user_id == uid) {
                this.hints.remove(a);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TLRPC.TL_contacts_resetTopPeerRating req = new TLRPC.TL_contacts_resetTopPeerRating();
                req.category = new TLRPC.TL_topPeerCategoryCorrespondents();
                req.peer = getMessagesController().getInputPeer(uid);
                deletePeer(uid, 0);
                getConnectionsManager().sendRequest(req, $$Lambda$MediaDataController$rU1OLImdNeTEmKTQXs57QbOr98Y.INSTANCE);
                return;
            }
        }
    }

    static /* synthetic */ void lambda$removePeer$80(TLObject response, TLRPC.TL_error error) {
    }

    public void increasePeerRaiting(long did) {
        int lower_id;
        if (getUserConfig().suggestContacts && (lower_id = (int) did) > 0) {
            TLRPC.User user = lower_id > 0 ? getMessagesController().getUser(Integer.valueOf(lower_id)) : null;
            if (user != null && !user.bot && !user.self) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(did, lower_id) {
                    private final /* synthetic */ long f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r4;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$increasePeerRaiting$83$MediaDataController(this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$increasePeerRaiting$83$MediaDataController(long did, int lower_id) {
        double dt = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        int lastTime = 0;
        int lastMid = 0;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[]{Long.valueOf(did)}), new Object[0]);
            if (cursor.next()) {
                lastMid = cursor.intValue(0);
                lastTime = cursor.intValue(1);
            }
            cursor.dispose();
            if (lastMid > 0 && getUserConfig().ratingLoadTime != 0) {
                dt = (double) (lastTime - getUserConfig().ratingLoadTime);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AndroidUtilities.runOnUIThread(new Runnable(lower_id, dt, did) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ double f$2;
            private final /* synthetic */ long f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$null$82$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$82$MediaDataController(int lower_id, double dtFinal, long did) {
        TLRPC.TL_topPeer p;
        TLRPC.TL_topPeer peer = null;
        int a = 0;
        while (true) {
            if (a >= this.hints.size()) {
                break;
            }
            p = this.hints.get(a);
            if ((lower_id >= 0 || !(p.peer.chat_id == (-lower_id) || p.peer.channel_id == (-lower_id))) && (lower_id <= 0 || p.peer.user_id != lower_id)) {
                a++;
            }
        }
        peer = p;
        if (peer == null) {
            peer = new TLRPC.TL_topPeer();
            if (lower_id > 0) {
                peer.peer = new TLRPC.TL_peerUser();
                peer.peer.user_id = lower_id;
            } else {
                peer.peer = new TLRPC.TL_peerChat();
                peer.peer.chat_id = -lower_id;
            }
            this.hints.add(peer);
        }
        peer.rating += Math.exp(dtFinal / ((double) getMessagesController().ratingDecay));
        Collections.sort(this.hints, $$Lambda$MediaDataController$oj9T0SqAGJ5Q57tsxLMoKqMS8OM.INSTANCE);
        savePeer((int) did, 0, peer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    }

    static /* synthetic */ int lambda$null$81(TLRPC.TL_topPeer lhs, TLRPC.TL_topPeer rhs) {
        if (lhs.rating > rhs.rating) {
            return -1;
        }
        if (lhs.rating < rhs.rating) {
            return 1;
        }
        return 0;
    }

    private void savePeer(int did, int type, double rating) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(did, type, rating) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ double f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$savePeer$84$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$savePeer$84$MediaDataController(int did, int type, double rating) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            state.requery();
            state.bindInteger(1, did);
            state.bindInteger(2, type);
            state.bindDouble(3, rating);
            state.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void deletePeer(int did, int type) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(did, type) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MediaDataController.this.lambda$deletePeer$85$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$deletePeer$85$MediaDataController(int did, int type) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(did), Integer.valueOf(type)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private Intent createIntrnalShortcutIntent(long did) {
        Intent shortcutIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        int lower_id = (int) did;
        int high_id = (int) (did >> 32);
        if (lower_id == 0) {
            shortcutIntent.putExtra("encId", high_id);
            if (getMessagesController().getEncryptedChat(Integer.valueOf(high_id)) == null) {
                return null;
            }
        } else if (lower_id > 0) {
            shortcutIntent.putExtra("userId", lower_id);
        } else if (lower_id >= 0) {
            return null;
        } else {
            shortcutIntent.putExtra("chatId", -lower_id);
        }
        shortcutIntent.putExtra("currentAccount", this.currentAccount);
        shortcutIntent.setAction("com.tmessages.openchat" + did);
        shortcutIntent.addFlags(ConnectionsManager.FileTypeFile);
        return shortcutIntent;
    }

    /* JADX WARNING: Removed duplicated region for block: B:105:0x0258 A[Catch:{ Exception -> 0x02bc }] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01e1 A[Catch:{ Exception -> 0x02bc }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void installShortcut(long r21) {
        /*
            r20 = this;
            r1 = r21
            android.content.Intent r0 = r20.createIntrnalShortcutIntent(r21)     // Catch:{ Exception -> 0x02bc }
            r3 = r0
            int r4 = (int) r1     // Catch:{ Exception -> 0x02bc }
            r0 = 32
            long r5 = r1 >> r0
            int r6 = (int) r5     // Catch:{ Exception -> 0x02bc }
            r0 = 0
            r5 = 0
            if (r4 != 0) goto L_0x0032
            im.bclpbkiauv.messenger.MessagesController r7 = r20.getMessagesController()     // Catch:{ Exception -> 0x02bc }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x02bc }
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r7 = r7.getEncryptedChat(r8)     // Catch:{ Exception -> 0x02bc }
            if (r7 != 0) goto L_0x0020
            return
        L_0x0020:
            im.bclpbkiauv.messenger.MessagesController r8 = r20.getMessagesController()     // Catch:{ Exception -> 0x02bc }
            int r9 = r7.user_id     // Catch:{ Exception -> 0x02bc }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x02bc }
            im.bclpbkiauv.tgnet.TLRPC$User r8 = r8.getUser(r9)     // Catch:{ Exception -> 0x02bc }
            r0 = r8
            r7 = r5
            r5 = r0
            goto L_0x0055
        L_0x0032:
            if (r4 <= 0) goto L_0x0044
            im.bclpbkiauv.messenger.MessagesController r7 = r20.getMessagesController()     // Catch:{ Exception -> 0x02bc }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x02bc }
            im.bclpbkiauv.tgnet.TLRPC$User r7 = r7.getUser(r8)     // Catch:{ Exception -> 0x02bc }
            r0 = r7
            r7 = r5
            r5 = r0
            goto L_0x0055
        L_0x0044:
            if (r4 >= 0) goto L_0x02bb
            im.bclpbkiauv.messenger.MessagesController r7 = r20.getMessagesController()     // Catch:{ Exception -> 0x02bc }
            int r8 = -r4
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x02bc }
            im.bclpbkiauv.tgnet.TLRPC$Chat r7 = r7.getChat(r8)     // Catch:{ Exception -> 0x02bc }
            r5 = r7
            r5 = r0
        L_0x0055:
            if (r5 != 0) goto L_0x005a
            if (r7 != 0) goto L_0x005a
            return
        L_0x005a:
            r0 = 0
            r8 = 0
            if (r5 == 0) goto L_0x008b
            boolean r9 = im.bclpbkiauv.messenger.UserObject.isUserSelf(r5)     // Catch:{ Exception -> 0x02bc }
            if (r9 == 0) goto L_0x0072
            java.lang.String r9 = "SavedMessages"
            r10 = 2131693693(0x7f0f107d, float:1.9016522E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)     // Catch:{ Exception -> 0x02bc }
            r8 = 1
            r10 = r9
            r9 = r8
            r8 = r0
            goto L_0x009d
        L_0x0072:
            java.lang.String r9 = r5.first_name     // Catch:{ Exception -> 0x02bc }
            java.lang.String r10 = r5.last_name     // Catch:{ Exception -> 0x02bc }
            java.lang.String r9 = im.bclpbkiauv.messenger.ContactsController.formatName(r9, r10)     // Catch:{ Exception -> 0x02bc }
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r10 = r5.photo     // Catch:{ Exception -> 0x02bc }
            if (r10 == 0) goto L_0x0087
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r10 = r5.photo     // Catch:{ Exception -> 0x02bc }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r10 = r10.photo_small     // Catch:{ Exception -> 0x02bc }
            r0 = r10
            r10 = r9
            r9 = r8
            r8 = r0
            goto L_0x009d
        L_0x0087:
            r10 = r9
            r9 = r8
            r8 = r0
            goto L_0x009d
        L_0x008b:
            java.lang.String r9 = r7.title     // Catch:{ Exception -> 0x02bc }
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r10 = r7.photo     // Catch:{ Exception -> 0x02bc }
            if (r10 == 0) goto L_0x009a
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r10 = r7.photo     // Catch:{ Exception -> 0x02bc }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r10 = r10.photo_small     // Catch:{ Exception -> 0x02bc }
            r0 = r10
            r10 = r9
            r9 = r8
            r8 = r0
            goto L_0x009d
        L_0x009a:
            r10 = r9
            r9 = r8
            r8 = r0
        L_0x009d:
            r11 = 0
            r13 = 0
            if (r9 != 0) goto L_0x00ae
            if (r8 == 0) goto L_0x00a4
            goto L_0x00ae
        L_0x00a4:
            r16 = r4
            r19 = r6
            r18 = r8
            r17 = r9
            goto L_0x01cf
        L_0x00ae:
            r0 = 1
            if (r9 != 0) goto L_0x00ca
            java.io.File r14 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r8, r0)     // Catch:{ all -> 0x00bf }
            java.lang.String r15 = r14.toString()     // Catch:{ all -> 0x00bf }
            android.graphics.Bitmap r15 = android.graphics.BitmapFactory.decodeFile(r15)     // Catch:{ all -> 0x00bf }
            r11 = r15
            goto L_0x00ca
        L_0x00bf:
            r0 = move-exception
            r16 = r4
            r19 = r6
            r18 = r8
            r17 = r9
            goto L_0x01cc
        L_0x00ca:
            if (r9 != 0) goto L_0x00d9
            if (r11 == 0) goto L_0x00cf
            goto L_0x00d9
        L_0x00cf:
            r16 = r4
            r19 = r6
            r18 = r8
            r17 = r9
            goto L_0x01b2
        L_0x00d9:
            r14 = 1114112000(0x42680000, float:58.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x01c3 }
            android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x01c3 }
            android.graphics.Bitmap r15 = android.graphics.Bitmap.createBitmap(r14, r14, r15)     // Catch:{ all -> 0x01c3 }
            r15.eraseColor(r13)     // Catch:{ all -> 0x01c3 }
            android.graphics.Canvas r12 = new android.graphics.Canvas     // Catch:{ all -> 0x01c3 }
            r12.<init>(r15)     // Catch:{ all -> 0x01c3 }
            if (r9 == 0) goto L_0x0118
            im.bclpbkiauv.ui.components.AvatarDrawable r13 = new im.bclpbkiauv.ui.components.AvatarDrawable     // Catch:{ all -> 0x010d }
            r13.<init>((im.bclpbkiauv.tgnet.TLRPC.User) r5)     // Catch:{ all -> 0x010d }
            r13.setAvatarType(r0)     // Catch:{ all -> 0x010d }
            r16 = r4
            r4 = 0
            r13.setBounds(r4, r4, r14, r14)     // Catch:{ all -> 0x0104 }
            r13.draw(r12)     // Catch:{ all -> 0x0104 }
            r19 = r6
            goto L_0x0173
        L_0x0104:
            r0 = move-exception
            r19 = r6
            r18 = r8
            r17 = r9
            goto L_0x01cc
        L_0x010d:
            r0 = move-exception
            r16 = r4
            r19 = r6
            r18 = r8
            r17 = r9
            goto L_0x01cc
        L_0x0118:
            r16 = r4
            android.graphics.BitmapShader r4 = new android.graphics.BitmapShader     // Catch:{ all -> 0x01bb }
            android.graphics.Shader$TileMode r13 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01bb }
            android.graphics.Shader$TileMode r0 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01bb }
            r4.<init>(r11, r13, r0)     // Catch:{ all -> 0x01bb }
            r0 = r4
            android.graphics.Paint r4 = roundPaint     // Catch:{ all -> 0x01bb }
            if (r4 != 0) goto L_0x0137
            android.graphics.Paint r4 = new android.graphics.Paint     // Catch:{ all -> 0x0104 }
            r13 = 1
            r4.<init>(r13)     // Catch:{ all -> 0x0104 }
            roundPaint = r4     // Catch:{ all -> 0x0104 }
            android.graphics.RectF r4 = new android.graphics.RectF     // Catch:{ all -> 0x0104 }
            r4.<init>()     // Catch:{ all -> 0x0104 }
            bitmapRect = r4     // Catch:{ all -> 0x0104 }
        L_0x0137:
            float r4 = (float) r14
            int r13 = r11.getWidth()     // Catch:{ all -> 0x01bb }
            float r13 = (float) r13     // Catch:{ all -> 0x01bb }
            float r4 = r4 / r13
            r12.save()     // Catch:{ all -> 0x01bb }
            r12.scale(r4, r4)     // Catch:{ all -> 0x01bb }
            android.graphics.Paint r13 = roundPaint     // Catch:{ all -> 0x01bb }
            r13.setShader(r0)     // Catch:{ all -> 0x01bb }
            android.graphics.RectF r13 = bitmapRect     // Catch:{ all -> 0x01bb }
            r17 = r0
            int r0 = r11.getWidth()     // Catch:{ all -> 0x01bb }
            float r0 = (float) r0     // Catch:{ all -> 0x01bb }
            r18 = r4
            int r4 = r11.getHeight()     // Catch:{ all -> 0x01bb }
            float r4 = (float) r4
            r19 = r6
            r6 = 0
            r13.set(r6, r6, r0, r4)     // Catch:{ all -> 0x01b5 }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x01b5 }
            int r4 = r11.getWidth()     // Catch:{ all -> 0x01b5 }
            float r4 = (float) r4     // Catch:{ all -> 0x01b5 }
            int r6 = r11.getHeight()     // Catch:{ all -> 0x01b5 }
            float r6 = (float) r6     // Catch:{ all -> 0x01b5 }
            android.graphics.Paint r13 = roundPaint     // Catch:{ all -> 0x01b5 }
            r12.drawRoundRect(r0, r4, r6, r13)     // Catch:{ all -> 0x01b5 }
            r12.restore()     // Catch:{ all -> 0x01b5 }
        L_0x0173:
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x01b5 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x01b5 }
            r4 = 2131558573(0x7f0d00ad, float:1.8742466E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)     // Catch:{ all -> 0x01b5 }
            r4 = r0
            r0 = 1097859072(0x41700000, float:15.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)     // Catch:{ all -> 0x01b5 }
            r6 = r0
            int r0 = r14 - r6
            r13 = 1073741824(0x40000000, float:2.0)
            int r17 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x01b5 }
            int r13 = r0 - r17
            int r0 = r14 - r6
            r17 = 1073741824(0x40000000, float:2.0)
            int r17 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)     // Catch:{ all -> 0x01b5 }
            r18 = r8
            int r8 = r0 - r17
            int r0 = r13 + r6
            r17 = r9
            int r9 = r8 + r6
            r4.setBounds(r13, r8, r0, r9)     // Catch:{ all -> 0x01b3 }
            r4.draw(r12)     // Catch:{ all -> 0x01b3 }
            r9 = 0
            r12.setBitmap(r9)     // Catch:{ Exception -> 0x01af }
            goto L_0x01b0
        L_0x01af:
            r0 = move-exception
        L_0x01b0:
            r0 = r15
            r11 = r0
        L_0x01b2:
            goto L_0x01cf
        L_0x01b3:
            r0 = move-exception
            goto L_0x01cc
        L_0x01b5:
            r0 = move-exception
            r18 = r8
            r17 = r9
            goto L_0x01cc
        L_0x01bb:
            r0 = move-exception
            r19 = r6
            r18 = r8
            r17 = r9
            goto L_0x01cc
        L_0x01c3:
            r0 = move-exception
            r16 = r4
            r19 = r6
            r18 = r8
            r17 = r9
        L_0x01cc:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x02bc }
        L_0x01cf:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02bc }
            r4 = 26
            r6 = 2131230873(0x7f080099, float:1.8077811E38)
            r8 = 2131230874(0x7f08009a, float:1.8077813E38)
            r9 = 2131230872(0x7f080098, float:1.807781E38)
            r12 = 2131230875(0x7f08009b, float:1.8077815E38)
            if (r0 < r4) goto L_0x0258
            android.content.pm.ShortcutInfo$Builder r0 = new android.content.pm.ShortcutInfo$Builder     // Catch:{ Exception -> 0x02bc }
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02bc }
            r13.<init>()     // Catch:{ Exception -> 0x02bc }
            java.lang.String r14 = "sdid_"
            r13.append(r14)     // Catch:{ Exception -> 0x02bc }
            r13.append(r1)     // Catch:{ Exception -> 0x02bc }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x02bc }
            r0.<init>(r4, r13)     // Catch:{ Exception -> 0x02bc }
            android.content.pm.ShortcutInfo$Builder r0 = r0.setShortLabel(r10)     // Catch:{ Exception -> 0x02bc }
            android.content.pm.ShortcutInfo$Builder r0 = r0.setIntent(r3)     // Catch:{ Exception -> 0x02bc }
            if (r11 == 0) goto L_0x020b
            android.graphics.drawable.Icon r4 = android.graphics.drawable.Icon.createWithBitmap(r11)     // Catch:{ Exception -> 0x02bc }
            r0.setIcon(r4)     // Catch:{ Exception -> 0x02bc }
            goto L_0x0244
        L_0x020b:
            if (r5 == 0) goto L_0x0225
            boolean r4 = r5.bot     // Catch:{ Exception -> 0x02bc }
            if (r4 == 0) goto L_0x021b
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            android.graphics.drawable.Icon r4 = android.graphics.drawable.Icon.createWithResource(r4, r9)     // Catch:{ Exception -> 0x02bc }
            r0.setIcon(r4)     // Catch:{ Exception -> 0x02bc }
            goto L_0x0244
        L_0x021b:
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            android.graphics.drawable.Icon r4 = android.graphics.drawable.Icon.createWithResource(r4, r12)     // Catch:{ Exception -> 0x02bc }
            r0.setIcon(r4)     // Catch:{ Exception -> 0x02bc }
            goto L_0x0244
        L_0x0225:
            if (r7 == 0) goto L_0x0244
            boolean r4 = im.bclpbkiauv.messenger.ChatObject.isChannel(r7)     // Catch:{ Exception -> 0x02bc }
            if (r4 == 0) goto L_0x023b
            boolean r4 = r7.megagroup     // Catch:{ Exception -> 0x02bc }
            if (r4 != 0) goto L_0x023b
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            android.graphics.drawable.Icon r4 = android.graphics.drawable.Icon.createWithResource(r4, r6)     // Catch:{ Exception -> 0x02bc }
            r0.setIcon(r4)     // Catch:{ Exception -> 0x02bc }
            goto L_0x0244
        L_0x023b:
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            android.graphics.drawable.Icon r4 = android.graphics.drawable.Icon.createWithResource(r4, r8)     // Catch:{ Exception -> 0x02bc }
            r0.setIcon(r4)     // Catch:{ Exception -> 0x02bc }
        L_0x0244:
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            java.lang.Class<android.content.pm.ShortcutManager> r6 = android.content.pm.ShortcutManager.class
            java.lang.Object r4 = r4.getSystemService(r6)     // Catch:{ Exception -> 0x02bc }
            android.content.pm.ShortcutManager r4 = (android.content.pm.ShortcutManager) r4     // Catch:{ Exception -> 0x02bc }
            android.content.pm.ShortcutInfo r6 = r0.build()     // Catch:{ Exception -> 0x02bc }
            r8 = 0
            r4.requestPinShortcut(r6, r8)     // Catch:{ Exception -> 0x02bc }
            goto L_0x02ba
        L_0x0258:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x02bc }
            r0.<init>()     // Catch:{ Exception -> 0x02bc }
            if (r11 == 0) goto L_0x0265
            java.lang.String r4 = "android.intent.extra.shortcut.ICON"
            r0.putExtra(r4, r11)     // Catch:{ Exception -> 0x02bc }
            goto L_0x02a0
        L_0x0265:
            java.lang.String r4 = "android.intent.extra.shortcut.ICON_RESOURCE"
            if (r5 == 0) goto L_0x0281
            boolean r6 = r5.bot     // Catch:{ Exception -> 0x02bc }
            if (r6 == 0) goto L_0x0277
            android.content.Context r6 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            android.content.Intent$ShortcutIconResource r6 = android.content.Intent.ShortcutIconResource.fromContext(r6, r9)     // Catch:{ Exception -> 0x02bc }
            r0.putExtra(r4, r6)     // Catch:{ Exception -> 0x02bc }
            goto L_0x02a0
        L_0x0277:
            android.content.Context r6 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            android.content.Intent$ShortcutIconResource r6 = android.content.Intent.ShortcutIconResource.fromContext(r6, r12)     // Catch:{ Exception -> 0x02bc }
            r0.putExtra(r4, r6)     // Catch:{ Exception -> 0x02bc }
            goto L_0x02a0
        L_0x0281:
            if (r7 == 0) goto L_0x02a0
            boolean r9 = im.bclpbkiauv.messenger.ChatObject.isChannel(r7)     // Catch:{ Exception -> 0x02bc }
            if (r9 == 0) goto L_0x0297
            boolean r9 = r7.megagroup     // Catch:{ Exception -> 0x02bc }
            if (r9 != 0) goto L_0x0297
            android.content.Context r8 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            android.content.Intent$ShortcutIconResource r6 = android.content.Intent.ShortcutIconResource.fromContext(r8, r6)     // Catch:{ Exception -> 0x02bc }
            r0.putExtra(r4, r6)     // Catch:{ Exception -> 0x02bc }
            goto L_0x02a0
        L_0x0297:
            android.content.Context r6 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            android.content.Intent$ShortcutIconResource r6 = android.content.Intent.ShortcutIconResource.fromContext(r6, r8)     // Catch:{ Exception -> 0x02bc }
            r0.putExtra(r4, r6)     // Catch:{ Exception -> 0x02bc }
        L_0x02a0:
            java.lang.String r4 = "android.intent.extra.shortcut.INTENT"
            r0.putExtra(r4, r3)     // Catch:{ Exception -> 0x02bc }
            java.lang.String r4 = "android.intent.extra.shortcut.NAME"
            r0.putExtra(r4, r10)     // Catch:{ Exception -> 0x02bc }
            java.lang.String r4 = "duplicate"
            r6 = 0
            r0.putExtra(r4, r6)     // Catch:{ Exception -> 0x02bc }
            java.lang.String r4 = "com.android.launcher.action.INSTALL_SHORTCUT"
            r0.setAction(r4)     // Catch:{ Exception -> 0x02bc }
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bc }
            r4.sendBroadcast(r0)     // Catch:{ Exception -> 0x02bc }
        L_0x02ba:
            goto L_0x02c0
        L_0x02bb:
            return
        L_0x02bc:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02c0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.installShortcut(long):void");
    }

    public void uninstallShortcut(long did) {
        String name;
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("sdid_" + did);
                ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).removeDynamicShortcuts(arrayList);
                return;
            }
            int lower_id = (int) did;
            int high_id = (int) (did >> 32);
            TLRPC.User user = null;
            TLRPC.Chat chat = null;
            if (lower_id == 0) {
                TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    user = getMessagesController().getUser(Integer.valueOf(encryptedChat.user_id));
                } else {
                    return;
                }
            } else if (lower_id > 0) {
                user = getMessagesController().getUser(Integer.valueOf(lower_id));
            } else if (lower_id < 0) {
                chat = getMessagesController().getChat(Integer.valueOf(-lower_id));
            } else {
                return;
            }
            if (user != null || chat != null) {
                if (user != null) {
                    name = ContactsController.formatName(user.first_name, user.last_name);
                } else {
                    name = chat.title;
                }
                Intent addIntent = new Intent();
                addIntent.putExtra("android.intent.extra.shortcut.INTENT", createIntrnalShortcutIntent(did));
                addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                addIntent.putExtra("duplicate", false);
                addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
                ApplicationLoader.applicationContext.sendBroadcast(addIntent);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ int lambda$static$86(TLRPC.MessageEntity entity1, TLRPC.MessageEntity entity2) {
        if (entity1.offset > entity2.offset) {
            return 1;
        }
        if (entity1.offset < entity2.offset) {
            return -1;
        }
        return 0;
    }

    public MessageObject loadPinnedMessage(long dialogId, int channelId, int mid, boolean useQueue) {
        if (!useQueue) {
            return loadPinnedMessageInternal(dialogId, channelId, mid, true);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(dialogId, channelId, mid) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$loadPinnedMessage$87$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
        return null;
    }

    public /* synthetic */ void lambda$loadPinnedMessage$87$MediaDataController(long dialogId, int channelId, int mid) {
        loadPinnedMessageInternal(dialogId, channelId, mid, false);
    }

    private MessageObject loadPinnedMessageInternal(long dialogId, int channelId, int mid, boolean returnValue) {
        long messageId;
        TLRPC.Message result;
        TLRPC.Message result2;
        ArrayList<Integer> usersToLoad;
        ArrayList<Integer> chatsToLoad;
        TLRPC.Message result3;
        NativeByteBuffer data;
        NativeByteBuffer data2;
        long j = dialogId;
        int i = channelId;
        int i2 = mid;
        if (i != 0) {
            messageId = ((long) i2) | (((long) i) << 32);
        } else {
            messageId = (long) i2;
        }
        try {
            ArrayList<TLRPC.User> users = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
            ArrayList<Integer> usersToLoad2 = new ArrayList<>();
            ArrayList<Integer> chatsToLoad2 = new ArrayList<>();
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageId)}), new Object[0]);
            if (!cursor.next() || (data2 = cursor.byteBufferValue(0)) == null) {
                result = null;
            } else {
                result = TLRPC.Message.TLdeserialize(data2, data2.readInt32(false), false);
                result.readAttachPath(data2, getUserConfig().clientUserId);
                data2.reuse();
                if (result.action instanceof TLRPC.TL_messageActionHistoryClear) {
                    result = null;
                } else {
                    result.id = cursor.intValue(1);
                    result.date = cursor.intValue(2);
                    result.dialog_id = j;
                    MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad2, chatsToLoad2);
                }
            }
            cursor.dispose();
            if (result == null) {
                SQLiteCursor sQLiteCursor = cursor;
                TLRPC.Message result4 = result;
                SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[]{Long.valueOf(dialogId)}), new Object[0]);
                if (!cursor2.next() || (data = cursor2.byteBufferValue(0)) == null) {
                    result3 = result4;
                } else {
                    TLRPC.Message result5 = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    result5.readAttachPath(data, getUserConfig().clientUserId);
                    data.reuse();
                    if (result5.id == i2) {
                        if (!(result5.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                            result5.dialog_id = j;
                            MessagesStorage.addUsersAndChatsFromMessage(result5, usersToLoad2, chatsToLoad2);
                            result3 = result5;
                        }
                    }
                    result3 = null;
                }
                cursor2.dispose();
                result2 = result3;
            } else {
                SQLiteCursor sQLiteCursor2 = cursor;
                result2 = result;
            }
            if (result2 == null) {
                if (i != 0) {
                    TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
                    req.channel = getMessagesController().getInputChannel(i);
                    req.id.add(Integer.valueOf(mid));
                    getConnectionsManager().sendRequest(req, new RequestDelegate(i) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MediaDataController.this.lambda$loadPinnedMessageInternal$88$MediaDataController(this.f$1, tLObject, tL_error);
                        }
                    });
                    return null;
                }
                TLRPC.TL_messages_getMessages req2 = new TLRPC.TL_messages_getMessages();
                req2.id.add(Integer.valueOf(mid));
                getConnectionsManager().sendRequest(req2, new RequestDelegate(i) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$loadPinnedMessageInternal$89$MediaDataController(this.f$1, tLObject, tL_error);
                    }
                });
                return null;
            } else if (returnValue) {
                ArrayList<Integer> arrayList = chatsToLoad2;
                ArrayList<Integer> arrayList2 = usersToLoad2;
                return broadcastPinnedMessage(result2, users, chats, true, returnValue);
            } else {
                ArrayList<Integer> chatsToLoad3 = chatsToLoad2;
                ArrayList<Integer> usersToLoad3 = usersToLoad2;
                if (!usersToLoad3.isEmpty()) {
                    usersToLoad = usersToLoad3;
                    getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), users);
                } else {
                    usersToLoad = usersToLoad3;
                }
                if (!chatsToLoad3.isEmpty()) {
                    chatsToLoad = chatsToLoad3;
                    getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                } else {
                    chatsToLoad = chatsToLoad3;
                }
                ArrayList<Integer> arrayList3 = chatsToLoad;
                ArrayList<Integer> arrayList4 = usersToLoad;
                broadcastPinnedMessage(result2, users, chats, true, false);
                return null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public /* synthetic */ void lambda$loadPinnedMessageInternal$88$MediaDataController(int channelId, TLObject response, TLRPC.TL_error error) {
        boolean ok = false;
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            if (!messagesRes.messages.isEmpty()) {
                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                broadcastPinnedMessage(messagesRes.messages.get(0), messagesRes.users, messagesRes.chats, false, false);
                getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                savePinnedMessage(messagesRes.messages.get(0));
                ok = true;
            }
        }
        if (!ok) {
            getMessagesStorage().updateChatPinnedMessage(channelId, 0);
        }
    }

    public /* synthetic */ void lambda$loadPinnedMessageInternal$89$MediaDataController(int channelId, TLObject response, TLRPC.TL_error error) {
        boolean ok = false;
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            if (!messagesRes.messages.isEmpty()) {
                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                broadcastPinnedMessage(messagesRes.messages.get(0), messagesRes.users, messagesRes.chats, false, false);
                getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                savePinnedMessage(messagesRes.messages.get(0));
                ok = true;
            }
        }
        if (!ok) {
            getMessagesStorage().updateChatPinnedMessage(channelId, 0);
        }
    }

    private void savePinnedMessage(TLRPC.Message result) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(result) {
            private final /* synthetic */ TLRPC.Message f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$savePinnedMessage$90$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$savePinnedMessage$90$MediaDataController(TLRPC.Message result) {
        long dialogId;
        NativeByteBuffer data = null;
        SQLitePreparedStatement state = null;
        try {
            if (result.to_id.channel_id != 0) {
                dialogId = (long) (-result.to_id.channel_id);
            } else if (result.to_id.chat_id != 0) {
                dialogId = (long) (-result.to_id.chat_id);
            } else if (result.to_id.user_id != 0) {
                dialogId = (long) result.to_id.user_id;
            } else {
                if (data != null) {
                    data.reuse();
                }
                if (state != null) {
                    state.dispose();
                    return;
                }
                return;
            }
            try {
                getMessagesStorage().getDatabase().beginTransaction();
            } catch (Exception e) {
                FileLog.e("savePinnedMessage ---> exception 1 ", (Throwable) e);
            }
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
            NativeByteBuffer data2 = new NativeByteBuffer(result.getObjectSize());
            result.serializeToStream(data2);
            state2.requery();
            state2.bindLong(1, dialogId);
            state2.bindInteger(2, result.id);
            state2.bindByteBuffer(3, data2);
            state2.step();
            data2.reuse();
            NativeByteBuffer data3 = null;
            state2.dispose();
            state = null;
            getMessagesStorage().getDatabase().commitTransaction();
            if (data3 != null) {
                data3.reuse();
            }
            if (0 == 0) {
                return;
            }
        } catch (Exception e2) {
            FileLog.e("savePinnedMessage ---> exception 2 ", (Throwable) e2);
            if (data != null) {
                data.reuse();
            }
            if (state == null) {
                return;
            }
        } catch (Throwable th) {
            if (data != null) {
                data.reuse();
            }
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
        state.dispose();
    }

    private MessageObject broadcastPinnedMessage(TLRPC.Message result, ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, boolean isCache, boolean returnValue) {
        SparseArray<TLRPC.User> usersDict = new SparseArray<>();
        for (int a = 0; a < users.size(); a++) {
            TLRPC.User user = users.get(a);
            usersDict.put(user.id, user);
        }
        ArrayList<TLRPC.User> arrayList = users;
        SparseArray<TLRPC.Chat> chatsDict = new SparseArray<>();
        for (int a2 = 0; a2 < chats.size(); a2++) {
            TLRPC.Chat chat = chats.get(a2);
            chatsDict.put(chat.id, chat);
        }
        ArrayList<TLRPC.Chat> arrayList2 = chats;
        if (returnValue) {
            return new MessageObject(this.currentAccount, result, usersDict, chatsDict, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(users, isCache, chats, result, usersDict, chatsDict) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ TLRPC.Message f$4;
            private final /* synthetic */ SparseArray f$5;
            private final /* synthetic */ SparseArray f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MediaDataController.this.lambda$broadcastPinnedMessage$91$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
        return null;
    }

    public /* synthetic */ void lambda$broadcastPinnedMessage$91$MediaDataController(ArrayList users, boolean isCache, ArrayList chats, TLRPC.Message result, SparseArray usersDict, SparseArray chatsDict) {
        boolean z = isCache;
        ArrayList arrayList = users;
        getMessagesController().putUsers(users, z);
        getMessagesController().putChats(chats, z);
        getNotificationCenter().postNotificationName(NotificationCenter.pinnedMessageDidLoad, new MessageObject(this.currentAccount, result, (SparseArray<TLRPC.User>) usersDict, (SparseArray<TLRPC.Chat>) chatsDict, false));
    }

    private static void removeEmptyMessages(ArrayList<TLRPC.Message> messages) {
        int a = 0;
        while (a < messages.size()) {
            TLRPC.Message message = messages.get(a);
            if (message == null || (message instanceof TLRPC.TL_messageEmpty) || (message.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                messages.remove(a);
                a--;
            }
            a++;
        }
    }

    public void loadReplyMessagesForMessages(ArrayList<MessageObject> messages, long dialogId, boolean scheduled) {
        ArrayList<MessageObject> arrayList = messages;
        if (((int) dialogId) == 0) {
            ArrayList<Long> replyMessages = new ArrayList<>();
            LongSparseArray<ArrayList<MessageObject>> replyMessageRandomOwners = new LongSparseArray<>();
            for (int a = 0; a < messages.size(); a++) {
                MessageObject messageObject = arrayList.get(a);
                if (messageObject.isReply() && messageObject.replyMessageObject == null) {
                    long id = messageObject.messageOwner.reply_to_random_id;
                    ArrayList<MessageObject> messageObjects = replyMessageRandomOwners.get(id);
                    if (messageObjects == null) {
                        messageObjects = new ArrayList<>();
                        replyMessageRandomOwners.put(id, messageObjects);
                    }
                    messageObjects.add(messageObject);
                    if (!replyMessages.contains(Long.valueOf(id))) {
                        replyMessages.add(Long.valueOf(id));
                    }
                }
            }
            if (replyMessages.isEmpty() == 0) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(replyMessages, dialogId, replyMessageRandomOwners) {
                    private final /* synthetic */ ArrayList f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ LongSparseArray f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$loadReplyMessagesForMessages$93$MediaDataController(this.f$1, this.f$2, this.f$3);
                    }
                });
                return;
            }
            return;
        }
        ArrayList<Integer> replyMessages2 = new ArrayList<>();
        SparseArray<ArrayList<MessageObject>> replyMessageOwners = new SparseArray<>();
        StringBuilder stringBuilder = new StringBuilder();
        int channelId = 0;
        for (int a2 = 0; a2 < messages.size(); a2++) {
            MessageObject messageObject2 = arrayList.get(a2);
            if (messageObject2.getId() > 0 && messageObject2.isReply() && messageObject2.replyMessageObject == null) {
                int id2 = messageObject2.messageOwner.reply_to_msg_id;
                long messageId = (long) id2;
                if (messageObject2.messageOwner.to_id.channel_id != 0) {
                    messageId |= ((long) messageObject2.messageOwner.to_id.channel_id) << 32;
                    channelId = messageObject2.messageOwner.to_id.channel_id;
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(',');
                }
                stringBuilder.append(messageId);
                ArrayList<MessageObject> messageObjects2 = replyMessageOwners.get(id2);
                if (messageObjects2 == null) {
                    messageObjects2 = new ArrayList<>();
                    replyMessageOwners.put(id2, messageObjects2);
                }
                messageObjects2.add(messageObject2);
                if (!replyMessages2.contains(Integer.valueOf(id2))) {
                    replyMessages2.add(Integer.valueOf(id2));
                }
            }
        }
        if (!replyMessages2.isEmpty()) {
            $$Lambda$MediaDataController$bxXXpxFuSviSuSkC5SEbVQ6lSM r0 = r1;
            DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
            $$Lambda$MediaDataController$bxXXpxFuSviSuSkC5SEbVQ6lSM r1 = new Runnable(stringBuilder, dialogId, replyMessages2, replyMessageOwners, channelId, scheduled) {
                private final /* synthetic */ StringBuilder f$1;
                private final /* synthetic */ long f$2;
                private final /* synthetic */ ArrayList f$3;
                private final /* synthetic */ SparseArray f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ boolean f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                }

                public final void run() {
                    MediaDataController.this.lambda$loadReplyMessagesForMessages$96$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            };
            storageQueue.postRunnable(r0);
        }
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$93$MediaDataController(ArrayList replyMessages, long dialogId, LongSparseArray replyMessageRandomOwners) {
        long j = dialogId;
        LongSparseArray longSparseArray = replyMessageRandomOwners;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            Locale locale = Locale.US;
            int i = 1;
            Object[] objArr = new Object[1];
            try {
                boolean z = false;
                objArr[0] = TextUtils.join(",", replyMessages);
                SQLiteCursor cursor = database.queryFinalized(String.format(locale, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", objArr), new Object[0]);
                while (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(z ? 1 : 0);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                        message.readAttachPath(data, getUserConfig().clientUserId);
                        data.reuse();
                        message.id = cursor.intValue(i);
                        message.date = cursor.intValue(2);
                        message.dialog_id = j;
                        long value = cursor.longValue(3);
                        ArrayList<MessageObject> arrayList = (ArrayList) longSparseArray.get(value);
                        longSparseArray.remove(value);
                        if (arrayList != null) {
                            MessageObject messageObject = new MessageObject(this.currentAccount, message, z);
                            for (int b = 0; b < arrayList.size(); b++) {
                                MessageObject object = arrayList.get(b);
                                object.replyMessageObject = messageObject;
                                object.messageOwner.reply_to_msg_id = messageObject.getId();
                                if (object.isMegagroup()) {
                                    object.replyMessageObject.messageOwner.flags |= Integer.MIN_VALUE;
                                }
                            }
                        }
                    }
                    i = 1;
                    z = false;
                }
                cursor.dispose();
                if (replyMessageRandomOwners.size() != 0) {
                    for (int b2 = 0; b2 < replyMessageRandomOwners.size(); b2++) {
                        ArrayList<MessageObject> arrayList2 = (ArrayList) longSparseArray.valueAt(b2);
                        for (int a = 0; a < arrayList2.size(); a++) {
                            arrayList2.get(a).messageOwner.reply_to_random_id = 0;
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable(j) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$92$MediaDataController(this.f$1);
                    }
                });
            } catch (Exception e) {
                e = e;
                FileLog.e((Throwable) e);
            }
        } catch (Exception e2) {
            e = e2;
            ArrayList arrayList3 = replyMessages;
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$92$MediaDataController(long dialogId) {
        getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(dialogId));
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$96$MediaDataController(StringBuilder stringBuilder, long dialogId, ArrayList replyMessages, SparseArray replyMessageOwners, int channelIdFinal, boolean scheduled) {
        ArrayList arrayList = replyMessages;
        int i = channelIdFinal;
        try {
            ArrayList<TLRPC.Message> result = new ArrayList<>();
            ArrayList<TLRPC.User> users = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder.toString()}), new Object[0]);
            while (cursor.next()) {
                try {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                        message.readAttachPath(data, getUserConfig().clientUserId);
                        data.reuse();
                        message.id = cursor.intValue(1);
                        message.date = cursor.intValue(2);
                        message.dialog_id = dialogId;
                        MessagesStorage.addUsersAndChatsFromMessage(message, arrayList2, arrayList3);
                        result.add(message);
                        arrayList.remove(Integer.valueOf(message.id));
                    } else {
                        long j = dialogId;
                    }
                } catch (Exception e) {
                    e = e;
                    long j2 = dialogId;
                    FileLog.e((Throwable) e);
                }
            }
            long j3 = dialogId;
            cursor.dispose();
            if (!arrayList2.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList2), users);
            }
            if (!arrayList3.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList3), chats);
            }
            broadcastReplyMessages(result, replyMessageOwners, users, chats, dialogId, true);
            if (replyMessages.isEmpty()) {
                return;
            }
            if (i != 0) {
                TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
                req.channel = getMessagesController().getInputChannel(i);
                req.id = arrayList;
                getConnectionsManager().sendRequest(req, new RequestDelegate(replyMessageOwners, dialogId, scheduled) {
                    private final /* synthetic */ SparseArray f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ boolean f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$null$94$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.TL_messages_getMessages req2 = new TLRPC.TL_messages_getMessages();
            req2.id = arrayList;
            getConnectionsManager().sendRequest(req2, new RequestDelegate(replyMessageOwners, dialogId, scheduled) {
                private final /* synthetic */ SparseArray f$1;
                private final /* synthetic */ long f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$null$95$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$94$MediaDataController(SparseArray replyMessageOwners, long dialogId, boolean scheduled, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, dialogId, false);
            getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
            SparseArray sparseArray = replyMessageOwners;
            boolean z = scheduled;
            saveReplyMessages(replyMessageOwners, messagesRes.messages, scheduled);
            return;
        }
        SparseArray sparseArray2 = replyMessageOwners;
        boolean z2 = scheduled;
    }

    public /* synthetic */ void lambda$null$95$MediaDataController(SparseArray replyMessageOwners, long dialogId, boolean scheduled, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, dialogId, false);
            getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
            SparseArray sparseArray = replyMessageOwners;
            boolean z = scheduled;
            saveReplyMessages(replyMessageOwners, messagesRes.messages, scheduled);
            return;
        }
        SparseArray sparseArray2 = replyMessageOwners;
        boolean z2 = scheduled;
    }

    private void saveReplyMessages(SparseArray<ArrayList<MessageObject>> replyMessageOwners, ArrayList<TLRPC.Message> result, boolean scheduled) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(scheduled, result, replyMessageOwners) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ SparseArray f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$saveReplyMessages$97$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$saveReplyMessages$97$MediaDataController(boolean scheduled, ArrayList result, SparseArray replyMessageOwners) {
        SQLitePreparedStatement state;
        NativeByteBuffer data = null;
        SQLitePreparedStatement state2 = null;
        try {
            getMessagesStorage().getDatabase().beginTransaction();
        } catch (Exception e) {
            try {
                FileLog.e("saveReplyMessages ---> exception 1 ", (Throwable) e);
            } catch (Exception e2) {
                FileLog.e("saveReplyMessages ---> exception 2 ", (Throwable) e2);
                if (data != null) {
                    data.reuse();
                }
                if (state2 == null) {
                    return;
                }
            } catch (Throwable th) {
                if (data != null) {
                    data.reuse();
                }
                if (state2 != null) {
                    state2.dispose();
                }
                throw th;
            }
        }
        if (scheduled) {
            state = getMessagesStorage().getDatabase().executeFast("UPDATE scheduled_messages SET replydata = ? WHERE mid = ?");
        } else {
            state = getMessagesStorage().getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
        }
        for (int a = 0; a < result.size(); a++) {
            TLRPC.Message message = (TLRPC.Message) result.get(a);
            ArrayList<MessageObject> messageObjects = (ArrayList) replyMessageOwners.get(message.id);
            if (messageObjects != null) {
                NativeByteBuffer data2 = new NativeByteBuffer(message.getObjectSize());
                message.serializeToStream(data2);
                for (int b = 0; b < messageObjects.size(); b++) {
                    MessageObject messageObject = messageObjects.get(b);
                    state.requery();
                    long messageId = (long) messageObject.getId();
                    if (messageObject.messageOwner.to_id.channel_id != 0) {
                        messageId |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                    }
                    state.bindByteBuffer(1, data2);
                    state.bindLong(2, messageId);
                    state.step();
                }
                data2.reuse();
                data = null;
            }
        }
        state.dispose();
        state2 = null;
        getMessagesStorage().getDatabase().commitTransaction();
        if (data != null) {
            data.reuse();
        }
        if (0 == 0) {
            return;
        }
        state2.dispose();
    }

    private void broadcastReplyMessages(ArrayList<TLRPC.Message> result, SparseArray<ArrayList<MessageObject>> replyMessageOwners, ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, long dialog_id, boolean isCache) {
        SparseArray<TLRPC.User> usersDict = new SparseArray<>();
        for (int a = 0; a < users.size(); a++) {
            TLRPC.User user = users.get(a);
            usersDict.put(user.id, user);
        }
        ArrayList<TLRPC.User> arrayList = users;
        SparseArray<TLRPC.Chat> chatsDict = new SparseArray<>();
        for (int a2 = 0; a2 < chats.size(); a2++) {
            TLRPC.Chat chat = chats.get(a2);
            chatsDict.put(chat.id, chat);
        }
        ArrayList<TLRPC.Chat> arrayList2 = chats;
        AndroidUtilities.runOnUIThread(new Runnable(users, isCache, chats, result, replyMessageOwners, usersDict, chatsDict, dialog_id) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ SparseArray f$5;
            private final /* synthetic */ SparseArray f$6;
            private final /* synthetic */ SparseArray f$7;
            private final /* synthetic */ long f$8;

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
                MediaDataController.this.lambda$broadcastReplyMessages$98$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$broadcastReplyMessages$98$MediaDataController(ArrayList users, boolean isCache, ArrayList chats, ArrayList result, SparseArray replyMessageOwners, SparseArray usersDict, SparseArray chatsDict, long dialog_id) {
        boolean z = isCache;
        getMessagesController().putUsers(users, z);
        getMessagesController().putChats(chats, z);
        boolean changed = false;
        for (int a = 0; a < result.size(); a++) {
            TLRPC.Message message = (TLRPC.Message) result.get(a);
            ArrayList<MessageObject> arrayList = (ArrayList) replyMessageOwners.get(message.id);
            if (arrayList != null) {
                MessageObject messageObject = new MessageObject(this.currentAccount, message, (SparseArray<TLRPC.User>) usersDict, (SparseArray<TLRPC.Chat>) chatsDict, false);
                for (int b = 0; b < arrayList.size(); b++) {
                    MessageObject m = arrayList.get(b);
                    m.replyMessageObject = messageObject;
                    if (m.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage) {
                        m.generatePinMessageText((TLRPC.User) null, (TLRPC.Chat) null);
                    } else if (m.messageOwner.action instanceof TLRPC.TL_messageActionGameScore) {
                        m.generateGameMessageText((TLRPC.User) null);
                    } else if (m.messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent) {
                        m.generatePaymentSentMessageText((TLRPC.User) null);
                    }
                    if (m.isMegagroup()) {
                        m.replyMessageObject.messageOwner.flags |= Integer.MIN_VALUE;
                    }
                }
                changed = true;
            }
        }
        ArrayList arrayList2 = result;
        SparseArray sparseArray = replyMessageOwners;
        if (changed) {
            getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(dialog_id));
        }
    }

    public static void sortEntities(ArrayList<TLRPC.MessageEntity> entities) {
        Collections.sort(entities, entityComparator);
    }

    private static boolean checkInclusion(int index, ArrayList<TLRPC.MessageEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            TLRPC.MessageEntity entity = entities.get(a);
            if (entity.offset <= index && entity.offset + entity.length > index) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkIntersection(int start, int end, ArrayList<TLRPC.MessageEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            TLRPC.MessageEntity entity = entities.get(a);
            if (entity.offset > start && entity.offset + entity.length <= end) {
                return true;
            }
        }
        return false;
    }

    private static void removeOffsetAfter(int start, int countToRemove, ArrayList<TLRPC.MessageEntity> entities) {
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            TLRPC.MessageEntity entity = entities.get(a);
            if (entity.offset > start) {
                entity.offset -= countToRemove;
            }
        }
    }

    public CharSequence substring(CharSequence source, int start, int end) {
        if (source instanceof SpannableStringBuilder) {
            return source.subSequence(start, end);
        }
        if (source instanceof SpannedString) {
            return source.subSequence(start, end);
        }
        return TextUtils.substring(source, start, end);
    }

    private static CharacterStyle createNewSpan(CharacterStyle baseSpan, TextStyleSpan.TextStyleRun textStyleRun, TextStyleSpan.TextStyleRun newStyleRun, boolean allowIntersection) {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun(textStyleRun);
        if (newStyleRun != null) {
            if (allowIntersection) {
                run.merge(newStyleRun);
            } else {
                run.replace(newStyleRun);
            }
        }
        if (baseSpan instanceof TextStyleSpan) {
            return new TextStyleSpan(run);
        }
        if (baseSpan instanceof URLSpanReplacement) {
            return new URLSpanReplacement(((URLSpanReplacement) baseSpan).getURL(), run);
        }
        return null;
    }

    public static void addStyleToText(TextStyleSpan span, int start, int end, Spannable editable, boolean allowIntersection) {
        TextStyleSpan.TextStyleRun textStyleRun;
        TextStyleSpan textStyleSpan = span;
        Spannable spannable = editable;
        boolean z = allowIntersection;
        int start2 = start;
        int end2 = end;
        try {
            CharacterStyle[] spans = (CharacterStyle[]) spannable.getSpans(start2, end2, CharacterStyle.class);
            if (spans != null && spans.length > 0) {
                int a = 0;
                while (a < spans.length) {
                    try {
                        CharacterStyle oldSpan = spans[a];
                        TextStyleSpan.TextStyleRun newStyleRun = textStyleSpan != null ? span.getTextStyleRun() : new TextStyleSpan.TextStyleRun();
                        if (oldSpan instanceof TextStyleSpan) {
                            textStyleRun = ((TextStyleSpan) oldSpan).getTextStyleRun();
                        } else if (!(oldSpan instanceof URLSpanReplacement)) {
                            a++;
                        } else {
                            TextStyleSpan.TextStyleRun textStyleRun2 = ((URLSpanReplacement) oldSpan).getTextStyleRun();
                            if (textStyleRun2 == null) {
                                textStyleRun = new TextStyleSpan.TextStyleRun();
                            } else {
                                textStyleRun = textStyleRun2;
                            }
                        }
                        if (textStyleRun != null) {
                            int spanStart = spannable.getSpanStart(oldSpan);
                            int spanEnd = spannable.getSpanEnd(oldSpan);
                            spannable.removeSpan(oldSpan);
                            if (spanStart <= start2 || end2 <= spanEnd) {
                                int startTemp = start2;
                                if (spanStart <= start2) {
                                    if (spanStart != start2) {
                                        spannable.setSpan(createNewSpan(oldSpan, textStyleRun, (TextStyleSpan.TextStyleRun) null, z), spanStart, start2, 33);
                                    }
                                    if (spanEnd > start2) {
                                        if (textStyleSpan != null) {
                                            spannable.setSpan(createNewSpan(oldSpan, textStyleRun, newStyleRun, z), start2, Math.min(spanEnd, end2), 33);
                                        }
                                        start2 = spanEnd;
                                    }
                                }
                                if (spanEnd >= end2) {
                                    if (spanEnd != end2) {
                                        spannable.setSpan(createNewSpan(oldSpan, textStyleRun, (TextStyleSpan.TextStyleRun) null, z), end2, spanEnd, 33);
                                    }
                                    if (end2 > spanStart && spanEnd <= startTemp) {
                                        if (textStyleSpan != null) {
                                            spannable.setSpan(createNewSpan(oldSpan, textStyleRun, newStyleRun, z), spanStart, Math.min(spanEnd, end2), 33);
                                        }
                                        end2 = spanStart;
                                    }
                                }
                            } else {
                                spannable.setSpan(createNewSpan(oldSpan, textStyleRun, newStyleRun, z), spanStart, spanEnd, 33);
                                if (textStyleSpan != null) {
                                    spannable.setSpan(new TextStyleSpan(new TextStyleSpan.TextStyleRun(newStyleRun)), spanEnd, end2, 33);
                                }
                                end2 = spanStart;
                            }
                        }
                        a++;
                    } catch (Exception e) {
                        e = e;
                        FileLog.e((Throwable) e);
                    }
                }
            }
            if (textStyleSpan != null && start2 < end2) {
                spannable.setSpan(textStyleSpan, start2, end2, 33);
            }
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    public static ArrayList<TextStyleSpan.TextStyleRun> getTextStyleRuns(ArrayList<TLRPC.MessageEntity> entities, CharSequence text) {
        ArrayList<TextStyleSpan.TextStyleRun> runs = new ArrayList<>();
        ArrayList<TLRPC.MessageEntity> entitiesCopy = new ArrayList<>(entities);
        Collections.sort(entitiesCopy, $$Lambda$MediaDataController$Iq0QsQDZEbL86xKwqyC0CXUWOhk.INSTANCE);
        int N = entitiesCopy.size();
        for (int a = 0; a < N; a++) {
            TLRPC.MessageEntity entity = entitiesCopy.get(a);
            if (entity.length > 0 && entity.offset >= 0 && entity.offset < text.length()) {
                if (entity.offset + entity.length > text.length()) {
                    entity.length = text.length() - entity.offset;
                }
                TextStyleSpan.TextStyleRun newRun = new TextStyleSpan.TextStyleRun();
                newRun.start = entity.offset;
                newRun.end = newRun.start + entity.length;
                if (entity instanceof TLRPC.TL_messageEntityStrike) {
                    newRun.flags = 8;
                } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                    newRun.flags = 16;
                } else if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                    newRun.flags = 32;
                } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                    newRun.flags = 1;
                } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                    newRun.flags = 2;
                } else if ((entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityPre)) {
                    newRun.flags = 4;
                } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                    newRun.flags = 64;
                    newRun.urlEntity = entity;
                } else if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    newRun.flags = 64;
                    newRun.urlEntity = entity;
                } else {
                    newRun.flags = 128;
                    newRun.urlEntity = entity;
                }
                int b = 0;
                int N2 = runs.size();
                while (b < N2) {
                    TextStyleSpan.TextStyleRun run = runs.get(b);
                    if (newRun.start > run.start) {
                        if (newRun.start < run.end) {
                            if (newRun.end < run.end) {
                                TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                                r.merge(run);
                                int b2 = b + 1;
                                runs.add(b2, r);
                                TextStyleSpan.TextStyleRun r2 = new TextStyleSpan.TextStyleRun(run);
                                r2.start = newRun.end;
                                b = b2 + 1;
                                N2 = N2 + 1 + 1;
                                runs.add(b, r2);
                            } else if (newRun.end >= run.end) {
                                TextStyleSpan.TextStyleRun r3 = new TextStyleSpan.TextStyleRun(newRun);
                                r3.merge(run);
                                r3.end = run.end;
                                b++;
                                N2++;
                                runs.add(b, r3);
                            }
                            int temp = newRun.start;
                            newRun.start = run.end;
                            run.end = temp;
                        }
                    } else if (run.start < newRun.end) {
                        int temp2 = run.start;
                        if (newRun.end == run.end) {
                            run.merge(newRun);
                        } else if (newRun.end < run.end) {
                            TextStyleSpan.TextStyleRun r4 = new TextStyleSpan.TextStyleRun(run);
                            r4.merge(newRun);
                            r4.end = newRun.end;
                            b++;
                            N2++;
                            runs.add(b, r4);
                            run.start = newRun.end;
                        } else {
                            TextStyleSpan.TextStyleRun r5 = new TextStyleSpan.TextStyleRun(newRun);
                            r5.start = run.end;
                            b++;
                            N2++;
                            runs.add(b, r5);
                            run.merge(newRun);
                        }
                        newRun.end = temp2;
                    }
                    b++;
                }
                if (newRun.start < newRun.end) {
                    runs.add(newRun);
                }
            }
        }
        return runs;
    }

    static /* synthetic */ int lambda$getTextStyleRuns$99(TLRPC.MessageEntity o1, TLRPC.MessageEntity o2) {
        if (o1.offset > o2.offset) {
            return 1;
        }
        if (o1.offset < o2.offset) {
            return -1;
        }
        return 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:183:0x03c9, code lost:
        if (r10 == 10) goto L_0x03ce;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.MessageEntity> getEntities(java.lang.CharSequence[] r25) {
        /*
            r24 = this;
            r1 = r24
            if (r25 == 0) goto L_0x04bb
            r2 = 0
            r0 = r25[r2]
            if (r0 != 0) goto L_0x000b
            goto L_0x04bb
        L_0x000b:
            r0 = 0
            r3 = -1
            r4 = 0
            r5 = 0
            java.lang.String r6 = "`"
            java.lang.String r7 = "```"
            java.lang.String r8 = "**"
            java.lang.String r9 = "__"
        L_0x0017:
            r10 = r25[r2]
            if (r5 != 0) goto L_0x001e
            java.lang.String r11 = "`"
            goto L_0x0020
        L_0x001e:
            java.lang.String r11 = "```"
        L_0x0020:
            int r10 = android.text.TextUtils.indexOf(r10, r11, r4)
            r11 = r10
            r13 = -1
            r14 = 32
            r15 = 2
            if (r10 == r13) goto L_0x01ae
            r10 = 96
            if (r3 != r13) goto L_0x0059
            r13 = r25[r2]
            int r13 = r13.length()
            int r13 = r13 - r11
            if (r13 <= r15) goto L_0x004e
            r13 = r25[r2]
            int r14 = r11 + 1
            char r13 = r13.charAt(r14)
            if (r13 != r10) goto L_0x004e
            r13 = r25[r2]
            int r14 = r11 + 2
            char r13 = r13.charAt(r14)
            if (r13 != r10) goto L_0x004e
            r10 = 1
            goto L_0x004f
        L_0x004e:
            r10 = 0
        L_0x004f:
            r5 = r10
            r3 = r11
            if (r5 == 0) goto L_0x0055
            r15 = 3
            goto L_0x0056
        L_0x0055:
            r15 = 1
        L_0x0056:
            int r4 = r11 + r15
            goto L_0x0017
        L_0x0059:
            if (r0 != 0) goto L_0x0061
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            r0 = r13
        L_0x0061:
            if (r5 == 0) goto L_0x0065
            r13 = 3
            goto L_0x0066
        L_0x0065:
            r13 = 1
        L_0x0066:
            int r13 = r13 + r11
        L_0x0067:
            r17 = r25[r2]
            int r12 = r17.length()
            if (r13 >= r12) goto L_0x007c
            r12 = r25[r2]
            char r12 = r12.charAt(r13)
            if (r12 != r10) goto L_0x007c
            int r11 = r11 + 1
            int r13 = r13 + 1
            goto L_0x0067
        L_0x007c:
            if (r5 == 0) goto L_0x0080
            r10 = 3
            goto L_0x0081
        L_0x0080:
            r10 = 1
        L_0x0081:
            int r10 = r10 + r11
            if (r5 == 0) goto L_0x015a
            if (r3 <= 0) goto L_0x008f
            r4 = r25[r2]
            int r12 = r3 + -1
            char r4 = r4.charAt(r12)
            goto L_0x0090
        L_0x008f:
            r4 = 0
        L_0x0090:
            if (r4 == r14) goto L_0x0099
            r12 = 10
            if (r4 != r12) goto L_0x0097
            goto L_0x0099
        L_0x0097:
            r12 = 0
            goto L_0x009a
        L_0x0099:
            r12 = 1
        L_0x009a:
            r13 = r25[r2]
            if (r12 == 0) goto L_0x00a1
            r17 = 1
            goto L_0x00a3
        L_0x00a1:
            r17 = 0
        L_0x00a3:
            int r15 = r3 - r17
            java.lang.CharSequence r13 = r1.substring(r13, r2, r15)
            r15 = r25[r2]
            int r14 = r3 + 3
            java.lang.CharSequence r14 = r1.substring(r15, r14, r11)
            int r15 = r11 + 3
            r19 = r25[r2]
            int r2 = r19.length()
            if (r15 >= r2) goto L_0x00c6
            r20 = 0
            r2 = r25[r20]
            int r15 = r11 + 3
            char r2 = r2.charAt(r15)
            goto L_0x00c9
        L_0x00c6:
            r20 = 0
            r2 = 0
        L_0x00c9:
            r4 = r25[r20]
            int r15 = r11 + 3
            r19 = r6
            r6 = 32
            if (r2 == r6) goto L_0x00da
            r6 = 10
            if (r2 != r6) goto L_0x00d8
            goto L_0x00da
        L_0x00d8:
            r6 = 0
            goto L_0x00db
        L_0x00da:
            r6 = 1
        L_0x00db:
            int r15 = r15 + r6
            r6 = 0
            r16 = r25[r6]
            int r6 = r16.length()
            java.lang.CharSequence r4 = r1.substring(r4, r15, r6)
            int r6 = r13.length()
            java.lang.String r15 = "\n"
            if (r6 == 0) goto L_0x00ff
            r16 = r2
            r6 = 2
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r6]
            r6 = 0
            r2[r6] = r13
            r6 = 1
            r2[r6] = r15
            java.lang.CharSequence r13 = im.bclpbkiauv.messenger.AndroidUtilities.concat(r2)
            goto L_0x0102
        L_0x00ff:
            r16 = r2
            r12 = 1
        L_0x0102:
            int r2 = r4.length()
            if (r2 == 0) goto L_0x0116
            r2 = 2
            java.lang.CharSequence[] r6 = new java.lang.CharSequence[r2]
            r2 = 0
            r6[r2] = r15
            r15 = 1
            r6[r15] = r4
            java.lang.CharSequence r4 = im.bclpbkiauv.messenger.AndroidUtilities.concat(r6)
            goto L_0x0118
        L_0x0116:
            r2 = 0
            r15 = 1
        L_0x0118:
            boolean r6 = android.text.TextUtils.isEmpty(r14)
            if (r6 != 0) goto L_0x0156
            r21 = r7
            r6 = 3
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r6]
            r7[r2] = r13
            r7[r15] = r14
            r6 = 2
            r7[r6] = r4
            java.lang.CharSequence r6 = im.bclpbkiauv.messenger.AndroidUtilities.concat(r7)
            r25[r2] = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityPre r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityPre
            r2.<init>()
            if (r12 == 0) goto L_0x0139
            r6 = 0
            goto L_0x013a
        L_0x0139:
            r6 = 1
        L_0x013a:
            int r6 = r6 + r3
            r2.offset = r6
            int r6 = r11 - r3
            r7 = 3
            int r6 = r6 - r7
            if (r12 == 0) goto L_0x0146
            r18 = 0
            goto L_0x0148
        L_0x0146:
            r18 = 1
        L_0x0148:
            int r6 = r6 + r18
            r2.length = r6
            java.lang.String r6 = ""
            r2.language = r6
            r0.add(r2)
            int r10 = r10 + -6
            goto L_0x0158
        L_0x0156:
            r21 = r7
        L_0x0158:
            r4 = r10
            goto L_0x01a5
        L_0x015a:
            r19 = r6
            r21 = r7
            int r2 = r3 + 1
            if (r2 == r11) goto L_0x01a4
            r2 = 3
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r2]
            r4 = 0
            r6 = r25[r4]
            java.lang.CharSequence r6 = r1.substring(r6, r4, r3)
            r2[r4] = r6
            r6 = r25[r4]
            int r7 = r3 + 1
            java.lang.CharSequence r6 = r1.substring(r6, r7, r11)
            r7 = 1
            r2[r7] = r6
            r6 = r25[r4]
            int r7 = r11 + 1
            r12 = r25[r4]
            int r12 = r12.length()
            java.lang.CharSequence r6 = r1.substring(r6, r7, r12)
            r7 = 2
            r2[r7] = r6
            java.lang.CharSequence r2 = im.bclpbkiauv.messenger.AndroidUtilities.concat(r2)
            r25[r4] = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityCode r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityCode
            r2.<init>()
            r2.offset = r3
            int r4 = r11 - r3
            r6 = 1
            int r4 = r4 - r6
            r2.length = r4
            r0.add(r2)
            int r10 = r10 + -2
            r4 = r10
            goto L_0x01a5
        L_0x01a4:
            r4 = r10
        L_0x01a5:
            r3 = -1
            r5 = 0
            r6 = r19
            r7 = r21
            r2 = 0
            goto L_0x0017
        L_0x01ae:
            r19 = r6
            r21 = r7
            if (r3 == r13) goto L_0x01ee
            if (r5 == 0) goto L_0x01ee
            r2 = 2
            java.lang.CharSequence[] r6 = new java.lang.CharSequence[r2]
            r2 = 0
            r7 = r25[r2]
            java.lang.CharSequence r7 = r1.substring(r7, r2, r3)
            r6[r2] = r7
            r7 = r25[r2]
            int r10 = r3 + 2
            r12 = r25[r2]
            int r12 = r12.length()
            java.lang.CharSequence r7 = r1.substring(r7, r10, r12)
            r10 = 1
            r6[r10] = r7
            java.lang.CharSequence r6 = im.bclpbkiauv.messenger.AndroidUtilities.concat(r6)
            r25[r2] = r6
            if (r0 != 0) goto L_0x01e1
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = r2
        L_0x01e1:
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityCode r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityCode
            r2.<init>()
            r2.offset = r3
            r6 = 1
            r2.length = r6
            r0.add(r2)
        L_0x01ee:
            r2 = 0
            r6 = r25[r2]
            boolean r6 = r6 instanceof android.text.Spanned
            if (r6 == 0) goto L_0x038f
            r6 = r25[r2]
            android.text.Spanned r6 = (android.text.Spanned) r6
            r7 = r25[r2]
            int r7 = r7.length()
            java.lang.Class<im.bclpbkiauv.ui.components.TextStyleSpan> r10 = im.bclpbkiauv.ui.components.TextStyleSpan.class
            java.lang.Object[] r7 = r6.getSpans(r2, r7, r10)
            r2 = r7
            im.bclpbkiauv.ui.components.TextStyleSpan[] r2 = (im.bclpbkiauv.ui.components.TextStyleSpan[]) r2
            if (r2 == 0) goto L_0x02bf
            int r7 = r2.length
            if (r7 <= 0) goto L_0x02bf
            r7 = 0
        L_0x020e:
            int r10 = r2.length
            if (r7 >= r10) goto L_0x02bc
            r10 = r2[r7]
            int r12 = r6.getSpanStart(r10)
            int r14 = r6.getSpanEnd(r10)
            boolean r15 = checkInclusion(r12, r0)
            if (r15 != 0) goto L_0x02b3
            boolean r15 = checkInclusion(r14, r0)
            if (r15 != 0) goto L_0x02b3
            boolean r15 = checkIntersection(r12, r14, r0)
            if (r15 == 0) goto L_0x0231
            r23 = r2
            goto L_0x02b5
        L_0x0231:
            if (r0 != 0) goto L_0x0239
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            r0 = r15
        L_0x0239:
            int r15 = r10.getStyleFlags()
            r22 = r15 & 1
            if (r22 == 0) goto L_0x0256
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityBold r22 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityBold
            r22.<init>()
            r23 = r22
            r13 = r23
            r13.offset = r12
            r23 = r2
            int r2 = r14 - r12
            r13.length = r2
            r0.add(r13)
            goto L_0x0258
        L_0x0256:
            r23 = r2
        L_0x0258:
            r2 = r15 & 2
            if (r2 == 0) goto L_0x026a
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityItalic r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityItalic
            r2.<init>()
            r2.offset = r12
            int r13 = r14 - r12
            r2.length = r13
            r0.add(r2)
        L_0x026a:
            r2 = r15 & 4
            if (r2 == 0) goto L_0x027c
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityCode r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityCode
            r2.<init>()
            r2.offset = r12
            int r13 = r14 - r12
            r2.length = r13
            r0.add(r2)
        L_0x027c:
            r2 = r15 & 8
            if (r2 == 0) goto L_0x028e
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityStrike r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityStrike
            r2.<init>()
            r2.offset = r12
            int r13 = r14 - r12
            r2.length = r13
            r0.add(r2)
        L_0x028e:
            r2 = r15 & 16
            if (r2 == 0) goto L_0x02a0
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityUnderline r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityUnderline
            r2.<init>()
            r2.offset = r12
            int r13 = r14 - r12
            r2.length = r13
            r0.add(r2)
        L_0x02a0:
            r2 = r15 & 32
            if (r2 == 0) goto L_0x02b5
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityBlockquote r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityBlockquote
            r2.<init>()
            r2.offset = r12
            int r13 = r14 - r12
            r2.length = r13
            r0.add(r2)
            goto L_0x02b5
        L_0x02b3:
            r23 = r2
        L_0x02b5:
            int r7 = r7 + 1
            r2 = r23
            r13 = -1
            goto L_0x020e
        L_0x02bc:
            r23 = r2
            goto L_0x02c1
        L_0x02bf:
            r23 = r2
        L_0x02c1:
            r2 = 0
            r7 = r25[r2]
            int r7 = r7.length()
            java.lang.Class<im.bclpbkiauv.ui.components.URLSpanUserMention> r10 = im.bclpbkiauv.ui.components.URLSpanUserMention.class
            java.lang.Object[] r7 = r6.getSpans(r2, r7, r10)
            r2 = r7
            im.bclpbkiauv.ui.components.URLSpanUserMention[] r2 = (im.bclpbkiauv.ui.components.URLSpanUserMention[]) r2
            if (r2 == 0) goto L_0x033d
            int r7 = r2.length
            if (r7 <= 0) goto L_0x033d
            if (r0 != 0) goto L_0x02de
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r0 = r7
        L_0x02de:
            r7 = 0
        L_0x02df:
            int r10 = r2.length
            if (r7 >= r10) goto L_0x033d
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMessageEntityMentionName r10 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMessageEntityMentionName
            r10.<init>()
            im.bclpbkiauv.messenger.MessagesController r12 = r24.getMessagesController()
            r13 = r2[r7]
            java.lang.String r13 = r13.getURL()
            java.lang.Integer r13 = im.bclpbkiauv.messenger.Utilities.parseInt(r13)
            int r13 = r13.intValue()
            im.bclpbkiauv.tgnet.TLRPC$InputUser r12 = r12.getInputUser((int) r13)
            r10.user_id = r12
            im.bclpbkiauv.tgnet.TLRPC$InputUser r12 = r10.user_id
            if (r12 == 0) goto L_0x033a
            r12 = r2[r7]
            int r12 = r6.getSpanStart(r12)
            r10.offset = r12
            r12 = r2[r7]
            int r12 = r6.getSpanEnd(r12)
            r13 = 0
            r14 = r25[r13]
            int r14 = r14.length()
            int r12 = java.lang.Math.min(r12, r14)
            int r14 = r10.offset
            int r12 = r12 - r14
            r10.length = r12
            r12 = r25[r13]
            int r13 = r10.offset
            int r14 = r10.length
            int r13 = r13 + r14
            r14 = 1
            int r13 = r13 - r14
            char r12 = r12.charAt(r13)
            r13 = 32
            if (r12 != r13) goto L_0x0337
            int r12 = r10.length
            int r12 = r12 - r14
            r10.length = r12
        L_0x0337:
            r0.add(r10)
        L_0x033a:
            int r7 = r7 + 1
            goto L_0x02df
        L_0x033d:
            r7 = 0
            r10 = r25[r7]
            int r10 = r10.length()
            java.lang.Class<im.bclpbkiauv.ui.components.URLSpanReplacement> r12 = im.bclpbkiauv.ui.components.URLSpanReplacement.class
            java.lang.Object[] r10 = r6.getSpans(r7, r10, r12)
            r7 = r10
            im.bclpbkiauv.ui.components.URLSpanReplacement[] r7 = (im.bclpbkiauv.ui.components.URLSpanReplacement[]) r7
            if (r7 == 0) goto L_0x038f
            int r10 = r7.length
            if (r10 <= 0) goto L_0x038f
            if (r0 != 0) goto L_0x035a
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            r0 = r10
        L_0x035a:
            r10 = 0
        L_0x035b:
            int r12 = r7.length
            if (r10 >= r12) goto L_0x038f
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityTextUrl r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityTextUrl
            r12.<init>()
            r13 = r7[r10]
            int r13 = r6.getSpanStart(r13)
            r12.offset = r13
            r13 = r7[r10]
            int r13 = r6.getSpanEnd(r13)
            r14 = 0
            r15 = r25[r14]
            int r14 = r15.length()
            int r13 = java.lang.Math.min(r13, r14)
            int r14 = r12.offset
            int r13 = r13 - r14
            r12.length = r13
            r13 = r7[r10]
            java.lang.String r13 = r13.getURL()
            r12.url = r13
            r0.add(r12)
            int r10 = r10 + 1
            goto L_0x035b
        L_0x038f:
            r2 = 0
        L_0x0390:
            r6 = 2
            if (r2 >= r6) goto L_0x04ba
            r4 = 0
            r3 = -1
            if (r2 != 0) goto L_0x039a
            java.lang.String r6 = "**"
            goto L_0x039c
        L_0x039a:
            java.lang.String r6 = "__"
        L_0x039c:
            if (r2 != 0) goto L_0x03a1
            r7 = 42
            goto L_0x03a3
        L_0x03a1:
            r7 = 95
        L_0x03a3:
            r10 = 0
            r12 = r25[r10]
            int r12 = android.text.TextUtils.indexOf(r12, r6, r4)
            r11 = r12
            r13 = -1
            if (r12 == r13) goto L_0x04b4
            if (r3 != r13) goto L_0x03d7
            if (r11 != 0) goto L_0x03b5
            r10 = 32
            goto L_0x03bd
        L_0x03b5:
            r12 = r25[r10]
            int r10 = r11 + -1
            char r10 = r12.charAt(r10)
        L_0x03bd:
            boolean r12 = checkInclusion(r11, r0)
            if (r12 != 0) goto L_0x03d0
            r12 = 32
            if (r10 == r12) goto L_0x03cc
            r14 = 10
            if (r10 != r14) goto L_0x03d4
            goto L_0x03ce
        L_0x03cc:
            r14 = 10
        L_0x03ce:
            r3 = r11
            goto L_0x03d4
        L_0x03d0:
            r12 = 32
            r14 = 10
        L_0x03d4:
            int r4 = r11 + 2
            goto L_0x03a3
        L_0x03d7:
            r12 = 32
            r14 = 10
            int r10 = r11 + 2
        L_0x03dd:
            r15 = 0
            r16 = r25[r15]
            int r12 = r16.length()
            if (r10 >= r12) goto L_0x03f5
            r12 = r25[r15]
            char r12 = r12.charAt(r10)
            if (r12 != r7) goto L_0x03f5
            int r11 = r11 + 1
            int r10 = r10 + 1
            r12 = 32
            goto L_0x03dd
        L_0x03f5:
            int r4 = r11 + 2
            boolean r10 = checkInclusion(r11, r0)
            if (r10 != 0) goto L_0x04af
            boolean r10 = checkIntersection(r3, r11, r0)
            if (r10 == 0) goto L_0x0407
            r13 = 0
            r14 = 2
            goto L_0x04b1
        L_0x0407:
            int r10 = r3 + 2
            if (r10 == r11) goto L_0x04aa
            if (r0 != 0) goto L_0x0414
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            r0 = r10
            goto L_0x0415
        L_0x0414:
            r10 = r0
        L_0x0415:
            r12 = 3
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r12]     // Catch:{ Exception -> 0x0446 }
            r15 = 0
            r12 = r25[r15]     // Catch:{ Exception -> 0x0446 }
            java.lang.CharSequence r12 = r1.substring(r12, r15, r3)     // Catch:{ Exception -> 0x0446 }
            r0[r15] = r12     // Catch:{ Exception -> 0x0446 }
            r12 = r25[r15]     // Catch:{ Exception -> 0x0446 }
            int r13 = r3 + 2
            java.lang.CharSequence r12 = r1.substring(r12, r13, r11)     // Catch:{ Exception -> 0x0446 }
            r13 = 1
            r0[r13] = r12     // Catch:{ Exception -> 0x0446 }
            r12 = r25[r15]     // Catch:{ Exception -> 0x0446 }
            int r13 = r11 + 2
            r16 = r25[r15]     // Catch:{ Exception -> 0x0446 }
            int r15 = r16.length()     // Catch:{ Exception -> 0x0446 }
            java.lang.CharSequence r12 = r1.substring(r12, r13, r15)     // Catch:{ Exception -> 0x0446 }
            r13 = 2
            r0[r13] = r12     // Catch:{ Exception -> 0x0446 }
            java.lang.CharSequence r0 = im.bclpbkiauv.messenger.AndroidUtilities.concat(r0)     // Catch:{ Exception -> 0x0446 }
            r12 = 0
            r25[r12] = r0     // Catch:{ Exception -> 0x0446 }
            r13 = 0
            goto L_0x0485
        L_0x0446:
            r0 = move-exception
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r13 = 0
            r15 = r25[r13]
            java.lang.CharSequence r15 = r1.substring(r15, r13, r3)
            java.lang.String r15 = r15.toString()
            r12.append(r15)
            r15 = r25[r13]
            int r14 = r3 + 2
            java.lang.CharSequence r14 = r1.substring(r15, r14, r11)
            java.lang.String r14 = r14.toString()
            r12.append(r14)
            r14 = r25[r13]
            int r15 = r11 + 2
            r20 = r25[r13]
            int r13 = r20.length()
            java.lang.CharSequence r13 = r1.substring(r14, r15, r13)
            java.lang.String r13 = r13.toString()
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r13 = 0
            r25[r13] = r12
        L_0x0485:
            if (r2 != 0) goto L_0x048d
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityBold r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityBold
            r0.<init>()
            goto L_0x0492
        L_0x048d:
            im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityItalic r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageEntityItalic
            r0.<init>()
        L_0x0492:
            r0.offset = r3
            int r12 = r11 - r3
            r14 = 2
            int r12 = r12 - r14
            r0.length = r12
            int r12 = r0.offset
            int r15 = r0.length
            int r12 = r12 + r15
            r15 = 4
            removeOffsetAfter(r12, r15, r10)
            r10.add(r0)
            int r4 = r4 + -4
            r0 = r10
            goto L_0x04ac
        L_0x04aa:
            r13 = 0
            r14 = 2
        L_0x04ac:
            r3 = -1
            goto L_0x03a3
        L_0x04af:
            r13 = 0
            r14 = 2
        L_0x04b1:
            r3 = -1
            goto L_0x03a3
        L_0x04b4:
            r13 = 0
            r14 = 2
            int r2 = r2 + 1
            goto L_0x0390
        L_0x04ba:
            return r0
        L_0x04bb:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.getEntities(java.lang.CharSequence[]):java.util.ArrayList");
    }

    public void loadDrafts() {
        if (!getUserConfig().draftsLoaded && !this.loadingDrafts) {
            this.loadingDrafts = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_messages_getAllDrafts(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$loadDrafts$101$MediaDataController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadDrafts$101$MediaDataController(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MediaDataController.this.lambda$null$100$MediaDataController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$100$MediaDataController() {
        getUserConfig().draftsLoaded = true;
        this.loadingDrafts = false;
        getUserConfig().saveConfig(false);
    }

    public TLRPC.DraftMessage getDraft(long did) {
        return this.drafts.get(did);
    }

    public TLRPC.Message getDraftMessage(long did) {
        return this.draftMessages.get(did);
    }

    public void saveDraft(long did, CharSequence message, ArrayList<TLRPC.MessageEntity> entities, TLRPC.Message replyToMessage, boolean noWebpage) {
        saveDraft(did, message, entities, replyToMessage, noWebpage, false);
    }

    public void saveDraft(long did, CharSequence message, ArrayList<TLRPC.MessageEntity> entities, TLRPC.Message replyToMessage, boolean noWebpage, boolean clean) {
        TLRPC.DraftMessage draftMessage;
        if (!TextUtils.isEmpty(message) || replyToMessage != null) {
            draftMessage = new TLRPC.TL_draftMessage();
        } else {
            draftMessage = new TLRPC.TL_draftMessageEmpty();
        }
        draftMessage.date = (int) (System.currentTimeMillis() / 1000);
        draftMessage.message = message == null ? "" : message.toString();
        draftMessage.no_webpage = noWebpage;
        if (replyToMessage != null) {
            draftMessage.reply_to_msg_id = replyToMessage.id;
            draftMessage.flags |= 1;
        }
        if (entities != null && !entities.isEmpty()) {
            draftMessage.entities = entities;
            draftMessage.flags |= 8;
        }
        TLRPC.DraftMessage currentDraft = this.drafts.get(did);
        if (!clean) {
            if (currentDraft != null && currentDraft.message.equals(draftMessage.message) && currentDraft.reply_to_msg_id == draftMessage.reply_to_msg_id && currentDraft.no_webpage == draftMessage.no_webpage) {
                return;
            }
            if (currentDraft == null && TextUtils.isEmpty(draftMessage.message) && draftMessage.reply_to_msg_id == 0) {
                return;
            }
        }
        saveDraft(did, draftMessage, replyToMessage, false);
        int lower_id = (int) did;
        if (lower_id != 0) {
            TLRPC.TL_messages_saveDraft req = new TLRPC.TL_messages_saveDraft();
            req.peer = getMessagesController().getInputPeer(lower_id);
            if (req.peer != null) {
                req.message = draftMessage.message;
                req.no_webpage = draftMessage.no_webpage;
                req.reply_to_msg_id = draftMessage.reply_to_msg_id;
                req.entities = draftMessage.entities;
                req.flags = draftMessage.flags;
                getConnectionsManager().sendRequest(req, $$Lambda$MediaDataController$pKWeovvMD5aoN9wXuiRGXgcw_Ak.INSTANCE);
            } else {
                return;
            }
        }
        getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    static /* synthetic */ void lambda$saveDraft$102(TLObject response, TLRPC.TL_error error) {
    }

    public void saveDraft(long did, TLRPC.DraftMessage draft, TLRPC.Message replyToMessage, boolean fromServer) {
        TLRPC.Chat chat;
        TLRPC.User user;
        int channelIdFinal;
        long messageId;
        long j = did;
        TLRPC.DraftMessage draftMessage = draft;
        TLRPC.Message message = replyToMessage;
        SharedPreferences.Editor editor = this.preferences.edit();
        if (draftMessage == null || (draftMessage instanceof TLRPC.TL_draftMessageEmpty)) {
            this.drafts.remove(j);
            this.draftMessages.remove(j);
            this.preferences.edit().remove("" + j).remove("r_" + j).commit();
        } else {
            this.drafts.put(j, draftMessage);
            try {
                SerializedData serializedData = new SerializedData(draft.getObjectSize());
                draftMessage.serializeToStream(serializedData);
                editor.putString("" + j, Utilities.bytesToHex(serializedData.toByteArray()));
                serializedData.cleanup();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (message == null) {
            this.draftMessages.remove(j);
            editor.remove("r_" + j);
        } else {
            this.draftMessages.put(j, message);
            SerializedData serializedData2 = new SerializedData(replyToMessage.getObjectSize());
            message.serializeToStream(serializedData2);
            editor.putString("r_" + j, Utilities.bytesToHex(serializedData2.toByteArray()));
            serializedData2.cleanup();
        }
        editor.commit();
        if (fromServer) {
            if (draftMessage.reply_to_msg_id != 0 && message == null) {
                int lower_id = (int) j;
                if (lower_id > 0) {
                    user = getMessagesController().getUser(Integer.valueOf(lower_id));
                    chat = null;
                } else {
                    user = null;
                    chat = getMessagesController().getChat(Integer.valueOf(-lower_id));
                }
                if (!(user == null && chat == null)) {
                    long messageId2 = (long) draftMessage.reply_to_msg_id;
                    if (ChatObject.isChannel(chat)) {
                        messageId = messageId2 | (((long) chat.id) << 32);
                        channelIdFinal = chat.id;
                    } else {
                        messageId = messageId2;
                        channelIdFinal = 0;
                    }
                    int i = lower_id;
                    DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                    $$Lambda$MediaDataController$RZDHj5VfnEUyGrH_gu2AK7kM14 r8 = r1;
                    $$Lambda$MediaDataController$RZDHj5VfnEUyGrH_gu2AK7kM14 r1 = new Runnable(messageId, channelIdFinal, did) {
                        private final /* synthetic */ long f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ long f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r4;
                            this.f$3 = r5;
                        }

                        public final void run() {
                            MediaDataController.this.lambda$saveDraft$105$MediaDataController(this.f$1, this.f$2, this.f$3);
                        }
                    };
                    storageQueue.postRunnable(r8);
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
        }
    }

    public /* synthetic */ void lambda$saveDraft$105$MediaDataController(long messageIdFinal, int channelIdFinal, long did) {
        NativeByteBuffer data;
        TLRPC.Message message = null;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageIdFinal)}), new Object[0]);
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                message.readAttachPath(data, getUserConfig().clientUserId);
                data.reuse();
            }
            cursor.dispose();
            if (message != null) {
                saveDraftReplyMessage(did, message);
            } else if (channelIdFinal != 0) {
                TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
                req.channel = getMessagesController().getInputChannel(channelIdFinal);
                req.id.add(Integer.valueOf((int) messageIdFinal));
                getConnectionsManager().sendRequest(req, new RequestDelegate(did) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$null$103$MediaDataController(this.f$1, tLObject, tL_error);
                    }
                });
            } else {
                TLRPC.TL_messages_getMessages req2 = new TLRPC.TL_messages_getMessages();
                req2.id.add(Integer.valueOf((int) messageIdFinal));
                getConnectionsManager().sendRequest(req2, new RequestDelegate(did) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$null$104$MediaDataController(this.f$1, tLObject, tL_error);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$103$MediaDataController(long did, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            if (!messagesRes.messages.isEmpty()) {
                saveDraftReplyMessage(did, messagesRes.messages.get(0));
            }
        }
    }

    public /* synthetic */ void lambda$null$104$MediaDataController(long did, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            if (!messagesRes.messages.isEmpty()) {
                saveDraftReplyMessage(did, messagesRes.messages.get(0));
            }
        }
    }

    private void saveDraftReplyMessage(long did, TLRPC.Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new Runnable(did, message) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ TLRPC.Message f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$saveDraftReplyMessage$106$MediaDataController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$saveDraftReplyMessage$106$MediaDataController(long did, TLRPC.Message message) {
        TLRPC.DraftMessage draftMessage = this.drafts.get(did);
        if (draftMessage != null && draftMessage.reply_to_msg_id == message.id) {
            this.draftMessages.put(did, message);
            SerializedData serializedData = new SerializedData(message.getObjectSize());
            message.serializeToStream(serializedData);
            SharedPreferences.Editor edit = this.preferences.edit();
            edit.putString("r_" + did, Utilities.bytesToHex(serializedData.toByteArray())).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
            serializedData.cleanup();
        }
    }

    public void clearAllDrafts() {
        this.drafts.clear();
        this.draftMessages.clear();
        this.preferences.edit().clear().commit();
        getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void cleanDraft(long did, boolean replyOnly) {
        TLRPC.DraftMessage draftMessage = this.drafts.get(did);
        if (draftMessage != null) {
            if (!replyOnly) {
                this.drafts.remove(did);
                this.draftMessages.remove(did);
                this.preferences.edit().remove("" + did).remove("r_" + did).commit();
                getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            } else if (draftMessage.reply_to_msg_id != 0) {
                draftMessage.reply_to_msg_id = 0;
                draftMessage.flags &= -2;
                saveDraft(did, draftMessage.message, draftMessage.entities, (TLRPC.Message) null, draftMessage.no_webpage, true);
            }
        }
    }

    public void beginTransaction() {
        this.inTransaction = true;
    }

    public void endTransaction() {
        this.inTransaction = false;
    }

    public void clearBotKeyboard(long did, ArrayList<Integer> messages) {
        AndroidUtilities.runOnUIThread(new Runnable(messages, did) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MediaDataController.this.lambda$clearBotKeyboard$107$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$clearBotKeyboard$107$MediaDataController(ArrayList messages, long did) {
        if (messages != null) {
            for (int a = 0; a < messages.size(); a++) {
                long did1 = this.botKeyboardsByMids.get(((Integer) messages.get(a)).intValue());
                if (did1 != 0) {
                    this.botKeyboards.remove(did1);
                    this.botKeyboardsByMids.delete(((Integer) messages.get(a)).intValue());
                    getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(did1));
                }
            }
            return;
        }
        this.botKeyboards.remove(did);
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(did));
    }

    public void loadBotKeyboard(long did) {
        TLRPC.Message keyboard = this.botKeyboards.get(did);
        if (keyboard != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, keyboard, Long.valueOf(did));
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(did) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$loadBotKeyboard$109$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$loadBotKeyboard$109$MediaDataController(long did) {
        NativeByteBuffer data;
        TLRPC.Message botKeyboard = null;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(did)}), new Object[0]);
            if (cursor.next() && !cursor.isNull(0) && (data = cursor.byteBufferValue(0)) != null) {
                botKeyboard = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
            }
            cursor.dispose();
            if (botKeyboard != null) {
                AndroidUtilities.runOnUIThread(new Runnable(botKeyboard, did) {
                    private final /* synthetic */ TLRPC.Message f$1;
                    private final /* synthetic */ long f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$108$MediaDataController(this.f$1, this.f$2);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$108$MediaDataController(TLRPC.Message botKeyboardFinal, long did) {
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, botKeyboardFinal, Long.valueOf(did));
    }

    public void loadBotInfo(int uid, boolean cache, int classGuid) {
        TLRPC.BotInfo botInfo;
        if (!cache || (botInfo = this.botInfos.get(uid)) == null) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(uid, classGuid) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaDataController.this.lambda$loadBotInfo$111$MediaDataController(this.f$1, this.f$2);
                }
            });
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(classGuid));
    }

    public /* synthetic */ void lambda$loadBotInfo$111$MediaDataController(int uid, int classGuid) {
        NativeByteBuffer data;
        TLRPC.BotInfo botInfo = null;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[]{Integer.valueOf(uid)}), new Object[0]);
            if (cursor.next() && !cursor.isNull(0) && (data = cursor.byteBufferValue(0)) != null) {
                botInfo = TLRPC.BotInfo.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
            }
            cursor.dispose();
            if (botInfo != null) {
                AndroidUtilities.runOnUIThread(new Runnable(botInfo, classGuid) {
                    private final /* synthetic */ TLRPC.BotInfo f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$110$MediaDataController(this.f$1, this.f$2);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$110$MediaDataController(TLRPC.BotInfo botInfoFinal, int classGuid) {
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfoFinal, Integer.valueOf(classGuid));
    }

    public void putBotKeyboard(long did, TLRPC.Message message) {
        if (message != null) {
            int mid = 0;
            try {
                SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(did)}), new Object[0]);
                if (cursor.next()) {
                    mid = cursor.intValue(0);
                }
                cursor.dispose();
                if (mid < message.id) {
                    SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
                    state.requery();
                    NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    state.bindLong(1, did);
                    state.bindInteger(2, message.id);
                    state.bindByteBuffer(3, data);
                    state.step();
                    data.reuse();
                    state.dispose();
                    AndroidUtilities.runOnUIThread(new Runnable(did, message) {
                        private final /* synthetic */ long f$1;
                        private final /* synthetic */ TLRPC.Message f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r4;
                        }

                        public final void run() {
                            MediaDataController.this.lambda$putBotKeyboard$112$MediaDataController(this.f$1, this.f$2);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$putBotKeyboard$112$MediaDataController(long did, TLRPC.Message message) {
        TLRPC.Message old = this.botKeyboards.get(did);
        this.botKeyboards.put(did, message);
        if (old != null) {
            this.botKeyboardsByMids.delete(old.id);
        }
        this.botKeyboardsByMids.put(message.id, did);
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(did));
    }

    public void putBotInfo(TLRPC.BotInfo botInfo) {
        if (botInfo != null) {
            this.botInfos.put(botInfo.user_id, botInfo);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(botInfo) {
                private final /* synthetic */ TLRPC.BotInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$putBotInfo$113$MediaDataController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$putBotInfo$113$MediaDataController(TLRPC.BotInfo botInfo) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(botInfo.getObjectSize());
            botInfo.serializeToStream(data);
            state.bindInteger(1, botInfo.user_id);
            state.bindByteBuffer(2, data);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void fetchNewEmojiKeywords(String[] langCodes) {
        if (langCodes != null) {
            int a = 0;
            while (a < langCodes.length) {
                String langCode = langCodes[a];
                if (!TextUtils.isEmpty(langCode) && this.currentFetchingEmoji.get(langCode) == null) {
                    this.currentFetchingEmoji.put(langCode, true);
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable(langCode) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            MediaDataController.this.lambda$fetchNewEmojiKeywords$119$MediaDataController(this.f$1);
                        }
                    });
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiKeywords} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v13, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$119$MediaDataController(java.lang.String r10) {
        /*
            r9 = this;
            r0 = -1
            r1 = 0
            r2 = 0
            im.bclpbkiauv.messenger.MessagesStorage r4 = r9.getMessagesStorage()     // Catch:{ Exception -> 0x0032 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ Exception -> 0x0032 }
            java.lang.String r5 = "SELECT alias, version, date FROM emoji_keywords_info_v2 WHERE lang = ?"
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0032 }
            r8 = 0
            r7[r8] = r10     // Catch:{ Exception -> 0x0032 }
            im.bclpbkiauv.sqlite.SQLiteCursor r4 = r4.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x0032 }
            boolean r5 = r4.next()     // Catch:{ Exception -> 0x0032 }
            if (r5 == 0) goto L_0x002e
            java.lang.String r5 = r4.stringValue(r8)     // Catch:{ Exception -> 0x0032 }
            r1 = r5
            int r5 = r4.intValue(r6)     // Catch:{ Exception -> 0x0032 }
            r0 = r5
            r5 = 2
            long r5 = r4.longValue(r5)     // Catch:{ Exception -> 0x0032 }
            r2 = r5
        L_0x002e:
            r4.dispose()     // Catch:{ Exception -> 0x0032 }
            goto L_0x0036
        L_0x0032:
            r4 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x0036:
            boolean r4 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION
            if (r4 != 0) goto L_0x0053
            long r4 = java.lang.System.currentTimeMillis()
            long r4 = r4 - r2
            long r4 = java.lang.Math.abs(r4)
            r6 = 3600000(0x36ee80, double:1.7786363E-317)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 >= 0) goto L_0x0053
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$_Ufg08dsPTJW06cdqFWfyASp820 r4 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$_Ufg08dsPTJW06cdqFWfyASp820
            r4.<init>(r10)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r4)
            return
        L_0x0053:
            r4 = -1
            if (r0 != r4) goto L_0x005f
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiKeywords r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiKeywords
            r4.<init>()
            r4.lang_code = r10
            goto L_0x0069
        L_0x005f:
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference
            r4.<init>()
            r4.lang_code = r10
            r4.from_version = r0
            r5 = r4
        L_0x0069:
            r5 = r1
            r6 = r0
            im.bclpbkiauv.tgnet.ConnectionsManager r7 = r9.getConnectionsManager()
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$2p0vkcn85of3JyNja2gT8HRLJHQ r8 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$2p0vkcn85of3JyNja2gT8HRLJHQ
            r8.<init>(r6, r5, r10)
            r7.sendRequest(r4, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.lambda$fetchNewEmojiKeywords$119$MediaDataController(java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$114$MediaDataController(String langCode) {
        Boolean remove = this.currentFetchingEmoji.remove(langCode);
    }

    public /* synthetic */ void lambda$null$118$MediaDataController(int versionFinal, String aliasFinal, String langCode, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_emojiKeywordsDifference res = (TLRPC.TL_emojiKeywordsDifference) response;
            if (versionFinal == -1 || res.lang_code.equals(aliasFinal)) {
                putEmojiKeywords(langCode, res);
            } else {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(langCode) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$116$MediaDataController(this.f$1);
                    }
                });
            }
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(langCode) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$117$MediaDataController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$116$MediaDataController(String langCode) {
        try {
            SQLitePreparedStatement deleteState = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_info_v2 WHERE lang = ?");
            deleteState.bindString(1, langCode);
            deleteState.step();
            deleteState.dispose();
            AndroidUtilities.runOnUIThread(new Runnable(langCode) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$115$MediaDataController(this.f$1);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$115$MediaDataController(String langCode) {
        this.currentFetchingEmoji.remove(langCode);
        fetchNewEmojiKeywords(new String[]{langCode});
    }

    public /* synthetic */ void lambda$null$117$MediaDataController(String langCode) {
        Boolean remove = this.currentFetchingEmoji.remove(langCode);
    }

    private void putEmojiKeywords(String lang, TLRPC.TL_emojiKeywordsDifference res) {
        if (res != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(res, lang) {
                private final /* synthetic */ TLRPC.TL_emojiKeywordsDifference f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaDataController.this.lambda$putEmojiKeywords$121$MediaDataController(this.f$1, this.f$2);
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        im.bclpbkiauv.messenger.FileLog.e("putEmojiKeywords ---> exception 1 ", (java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0116, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0117, code lost:
        r6 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x003c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0116 A[ExcHandler: all (th java.lang.Throwable), PHI: r3 r4 
      PHI: (r3v4 'insertState' im.bclpbkiauv.sqlite.SQLitePreparedStatement) = (r3v0 'insertState' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r3v6 'insertState' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r3v6 'insertState' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r3v6 'insertState' im.bclpbkiauv.sqlite.SQLitePreparedStatement) binds: [B:1:0x0007, B:6:0x0030, B:10:0x003f, B:7:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r4v4 'deleteState' im.bclpbkiauv.sqlite.SQLitePreparedStatement) = (r4v0 'deleteState' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r4v6 'deleteState' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r4v6 'deleteState' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r4v6 'deleteState' im.bclpbkiauv.sqlite.SQLitePreparedStatement) binds: [B:1:0x0007, B:6:0x0030, B:10:0x003f, B:7:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1:0x0007] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0124  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0138  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x013d  */
    /* JADX WARNING: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$putEmojiKeywords$121$MediaDataController(im.bclpbkiauv.tgnet.TLRPC.TL_emojiKeywordsDifference r17, java.lang.String r18) {
        /*
            r16 = this;
            r1 = r17
            r2 = r18
            r3 = 0
            r4 = 0
            r5 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$EmojiKeyword> r0 = r1.keywords     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r6 = 3
            r7 = 2
            r8 = 1
            if (r0 != 0) goto L_0x00cf
            im.bclpbkiauv.messenger.MessagesStorage r0 = r16.getMessagesStorage()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r9 = "REPLACE INTO emoji_keywords_v2 VALUES(?, ?, ?)"
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r0.executeFast(r9)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r3 = r0
            im.bclpbkiauv.messenger.MessagesStorage r0 = r16.getMessagesStorage()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r9 = "DELETE FROM emoji_keywords_v2 WHERE lang = ? AND keyword = ? AND emoji = ?"
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r0.executeFast(r9)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r4 = r0
            im.bclpbkiauv.messenger.MessagesStorage r0 = r16.getMessagesStorage()     // Catch:{ Exception -> 0x003c, all -> 0x0116 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x003c, all -> 0x0116 }
            r0.beginTransaction()     // Catch:{ Exception -> 0x003c, all -> 0x0116 }
            goto L_0x0042
        L_0x003c:
            r0 = move-exception
            java.lang.String r9 = "putEmojiKeywords ---> exception 1 "
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r9, (java.lang.Throwable) r0)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
        L_0x0042:
            r0 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$EmojiKeyword> r9 = r1.keywords     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            int r9 = r9.size()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
        L_0x0049:
            if (r0 >= r9) goto L_0x00bc
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$EmojiKeyword> r10 = r1.keywords     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.Object r10 = r10.get(r0)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            im.bclpbkiauv.tgnet.TLRPC$EmojiKeyword r10 = (im.bclpbkiauv.tgnet.TLRPC.EmojiKeyword) r10     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            boolean r11 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_emojiKeyword     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            if (r11 == 0) goto L_0x0086
            r11 = r10
            im.bclpbkiauv.tgnet.TLRPC$TL_emojiKeyword r11 = (im.bclpbkiauv.tgnet.TLRPC.TL_emojiKeyword) r11     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r12 = r11.keyword     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r12 = r12.toLowerCase()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r13 = 0
            java.util.ArrayList<java.lang.String> r14 = r11.emoticons     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
        L_0x0067:
            if (r13 >= r14) goto L_0x0085
            r3.requery()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r15 = r1.lang_code     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r3.bindString(r8, r15)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r3.bindString(r7, r12)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.util.ArrayList<java.lang.String> r15 = r11.emoticons     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.Object r15 = r15.get(r13)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r15 = (java.lang.String) r15     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r3.bindString(r6, r15)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r3.step()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            int r13 = r13 + 1
            goto L_0x0067
        L_0x0085:
            goto L_0x00b8
        L_0x0086:
            boolean r11 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_emojiKeywordDeleted     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            if (r11 == 0) goto L_0x00b8
            r11 = r10
            im.bclpbkiauv.tgnet.TLRPC$TL_emojiKeywordDeleted r11 = (im.bclpbkiauv.tgnet.TLRPC.TL_emojiKeywordDeleted) r11     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r12 = r11.keyword     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r12 = r12.toLowerCase()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r13 = 0
            java.util.ArrayList<java.lang.String> r14 = r11.emoticons     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
        L_0x009a:
            if (r13 >= r14) goto L_0x00b9
            r4.requery()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r15 = r1.lang_code     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r4.bindString(r8, r15)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r4.bindString(r7, r12)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.util.ArrayList<java.lang.String> r15 = r11.emoticons     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.Object r15 = r15.get(r13)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r15 = (java.lang.String) r15     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r4.bindString(r6, r15)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r4.step()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            int r13 = r13 + 1
            goto L_0x009a
        L_0x00b8:
        L_0x00b9:
            int r0 = r0 + 1
            goto L_0x0049
        L_0x00bc:
            im.bclpbkiauv.messenger.MessagesStorage r0 = r16.getMessagesStorage()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r0.commitTransaction()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r3.dispose()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r3 = 0
            r4.dispose()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r4 = 0
        L_0x00cf:
            im.bclpbkiauv.messenger.MessagesStorage r0 = r16.getMessagesStorage()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r9 = "REPLACE INTO emoji_keywords_info_v2 VALUES(?, ?, ?, ?)"
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r0.executeFast(r9)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r5 = r0
            r5.bindString(r8, r2)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            java.lang.String r0 = r1.lang_code     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r5.bindString(r7, r0)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            int r0 = r1.version     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r5.bindInteger(r6, r0)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r0 = 4
            long r6 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r5.bindLong(r0, r6)     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r5.step()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r5.dispose()     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r5 = 0
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$UH9iyvptdieHfug6JunOU8fEKRI r0 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$UH9iyvptdieHfug6JunOU8fEKRI     // Catch:{ Exception -> 0x011a, all -> 0x0116 }
            r6 = r16
            r0.<init>(r2)     // Catch:{ Exception -> 0x0114 }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x0114 }
            if (r3 == 0) goto L_0x0109
            r3.dispose()
        L_0x0109:
            if (r4 == 0) goto L_0x010e
            r4.dispose()
        L_0x010e:
            if (r5 == 0) goto L_0x012f
        L_0x0110:
            r5.dispose()
            goto L_0x012f
        L_0x0114:
            r0 = move-exception
            goto L_0x011d
        L_0x0116:
            r0 = move-exception
            r6 = r16
            goto L_0x0131
        L_0x011a:
            r0 = move-exception
            r6 = r16
        L_0x011d:
            java.lang.String r7 = "putEmojiKeywords ---> exception 2 "
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r7, (java.lang.Throwable) r0)     // Catch:{ all -> 0x0130 }
            if (r3 == 0) goto L_0x0127
            r3.dispose()
        L_0x0127:
            if (r4 == 0) goto L_0x012c
            r4.dispose()
        L_0x012c:
            if (r5 == 0) goto L_0x012f
            goto L_0x0110
        L_0x012f:
            return
        L_0x0130:
            r0 = move-exception
        L_0x0131:
            if (r3 == 0) goto L_0x0136
            r3.dispose()
        L_0x0136:
            if (r4 == 0) goto L_0x013b
            r4.dispose()
        L_0x013b:
            if (r5 == 0) goto L_0x0140
            r5.dispose()
        L_0x0140:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.lambda$putEmojiKeywords$121$MediaDataController(im.bclpbkiauv.tgnet.TLRPC$TL_emojiKeywordsDifference, java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$120$MediaDataController(String lang) {
        this.currentFetchingEmoji.remove(lang);
        getNotificationCenter().postNotificationName(NotificationCenter.newEmojiSuggestionsAvailable, lang);
    }

    public void getEmojiSuggestions(String[] langCodes, String keyword, boolean fullMatch, KeywordResultCallback callback) {
        getEmojiSuggestions(langCodes, keyword, fullMatch, callback, (CountDownLatch) null);
    }

    public void getEmojiSuggestions(String[] langCodes, String keyword, boolean fullMatch, KeywordResultCallback callback, CountDownLatch sync) {
        if (callback != null) {
            if (TextUtils.isEmpty(keyword) || langCodes == null) {
                callback.run(new ArrayList(), (String) null);
                return;
            }
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(langCodes, callback, keyword, fullMatch, new ArrayList<>(Emoji.recentEmoji), sync) {
                private final /* synthetic */ String[] f$1;
                private final /* synthetic */ MediaDataController.KeywordResultCallback f$2;
                private final /* synthetic */ String f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ ArrayList f$5;
                private final /* synthetic */ CountDownLatch f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    MediaDataController.this.lambda$getEmojiSuggestions$125$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
            if (sync != null) {
                try {
                    sync.await();
                } catch (Throwable th) {
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:52:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0156  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$getEmojiSuggestions$125$MediaDataController(java.lang.String[] r19, im.bclpbkiauv.messenger.MediaDataController.KeywordResultCallback r20, java.lang.String r21, boolean r22, java.util.ArrayList r23, java.util.concurrent.CountDownLatch r24) {
        /*
            r18 = this;
            r1 = r19
            r2 = r20
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r4 = r0
            r0 = 0
            r5 = 0
            r6 = 0
            r17 = r5
            r5 = r0
            r0 = r17
        L_0x0018:
            int r7 = r1.length     // Catch:{ Exception -> 0x013c }
            r8 = 0
            r9 = 1
            if (r6 >= r7) goto L_0x0045
            im.bclpbkiauv.messenger.MessagesStorage r7 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x013c }
            im.bclpbkiauv.sqlite.SQLiteDatabase r7 = r7.getDatabase()     // Catch:{ Exception -> 0x013c }
            java.lang.String r10 = "SELECT alias FROM emoji_keywords_info_v2 WHERE lang = ?"
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x013c }
            r11 = r1[r6]     // Catch:{ Exception -> 0x013c }
            r9[r8] = r11     // Catch:{ Exception -> 0x013c }
            im.bclpbkiauv.sqlite.SQLiteCursor r7 = r7.queryFinalized(r10, r9)     // Catch:{ Exception -> 0x013c }
            boolean r9 = r7.next()     // Catch:{ Exception -> 0x013c }
            if (r9 == 0) goto L_0x003c
            java.lang.String r8 = r7.stringValue(r8)     // Catch:{ Exception -> 0x013c }
            r5 = r8
        L_0x003c:
            r7.dispose()     // Catch:{ Exception -> 0x013c }
            if (r5 == 0) goto L_0x0042
            r0 = 1
        L_0x0042:
            int r6 = r6 + 1
            goto L_0x0018
        L_0x0045:
            if (r0 != 0) goto L_0x0052
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$2uRLjt_sos4WKXl1PHH1Y4Y4vLM r6 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$2uRLjt_sos4WKXl1PHH1Y4Y4vLM     // Catch:{ Exception -> 0x013c }
            r7 = r18
            r6.<init>(r1, r2, r3)     // Catch:{ Exception -> 0x013a }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r6)     // Catch:{ Exception -> 0x013a }
            return
        L_0x0052:
            r7 = r18
            java.lang.String r6 = r21.toLowerCase()     // Catch:{ Exception -> 0x013a }
            r10 = 0
        L_0x0059:
            r11 = 2
            if (r10 >= r11) goto L_0x0137
            if (r10 != r9) goto L_0x0072
            im.bclpbkiauv.messenger.LocaleController r12 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x013a }
            java.lang.String r12 = r12.getTranslitString(r6, r8, r8)     // Catch:{ Exception -> 0x013a }
            boolean r13 = r12.equals(r6)     // Catch:{ Exception -> 0x013a }
            if (r13 == 0) goto L_0x0071
            r16 = r0
            r15 = 1
            goto L_0x012f
        L_0x0071:
            r6 = r12
        L_0x0072:
            r12 = 0
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013a }
            r13.<init>(r6)     // Catch:{ Exception -> 0x013a }
            int r14 = r13.length()     // Catch:{ Exception -> 0x013a }
        L_0x007c:
            if (r14 <= 0) goto L_0x0094
            int r14 = r14 + -1
            char r15 = r13.charAt(r14)     // Catch:{ Exception -> 0x013a }
            int r11 = r15 + 1
            char r11 = (char) r11     // Catch:{ Exception -> 0x013a }
            r13.setCharAt(r14, r11)     // Catch:{ Exception -> 0x013a }
            if (r11 == 0) goto L_0x0092
            java.lang.String r15 = r13.toString()     // Catch:{ Exception -> 0x013a }
            r12 = r15
            goto L_0x0094
        L_0x0092:
            r11 = 2
            goto L_0x007c
        L_0x0094:
            if (r22 == 0) goto L_0x00ab
            im.bclpbkiauv.messenger.MessagesStorage r11 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x013a }
            im.bclpbkiauv.sqlite.SQLiteDatabase r11 = r11.getDatabase()     // Catch:{ Exception -> 0x013a }
            java.lang.String r15 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword = ?"
            java.lang.Object[] r8 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x013a }
            r16 = 0
            r8[r16] = r6     // Catch:{ Exception -> 0x013a }
            im.bclpbkiauv.sqlite.SQLiteCursor r8 = r11.queryFinalized(r15, r8)     // Catch:{ Exception -> 0x013a }
            goto L_0x00eb
        L_0x00ab:
            if (r12 == 0) goto L_0x00c5
            im.bclpbkiauv.messenger.MessagesStorage r8 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x013a }
            im.bclpbkiauv.sqlite.SQLiteDatabase r8 = r8.getDatabase()     // Catch:{ Exception -> 0x013a }
            java.lang.String r11 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword >= ? AND keyword < ?"
            r15 = 2
            java.lang.Object[] r15 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x013a }
            r16 = 0
            r15[r16] = r6     // Catch:{ Exception -> 0x013a }
            r15[r9] = r12     // Catch:{ Exception -> 0x013a }
            im.bclpbkiauv.sqlite.SQLiteCursor r8 = r8.queryFinalized(r11, r15)     // Catch:{ Exception -> 0x013a }
            goto L_0x00eb
        L_0x00c5:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013a }
            r8.<init>()     // Catch:{ Exception -> 0x013a }
            r8.append(r6)     // Catch:{ Exception -> 0x013a }
            java.lang.String r11 = "%"
            r8.append(r11)     // Catch:{ Exception -> 0x013a }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x013a }
            r6 = r8
            im.bclpbkiauv.messenger.MessagesStorage r8 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x013a }
            im.bclpbkiauv.sqlite.SQLiteDatabase r8 = r8.getDatabase()     // Catch:{ Exception -> 0x013a }
            java.lang.String r11 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword LIKE ?"
            java.lang.Object[] r15 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x013a }
            r16 = 0
            r15[r16] = r6     // Catch:{ Exception -> 0x013a }
            im.bclpbkiauv.sqlite.SQLiteCursor r8 = r8.queryFinalized(r11, r15)     // Catch:{ Exception -> 0x013a }
        L_0x00eb:
            boolean r11 = r8.next()     // Catch:{ Exception -> 0x013a }
            if (r11 == 0) goto L_0x0129
            r11 = 0
            java.lang.String r15 = r8.stringValue(r11)     // Catch:{ Exception -> 0x013a }
            java.lang.String r11 = "️"
            java.lang.String r9 = ""
            java.lang.String r9 = r15.replace(r11, r9)     // Catch:{ Exception -> 0x013a }
            java.lang.Object r11 = r4.get(r9)     // Catch:{ Exception -> 0x013a }
            if (r11 == 0) goto L_0x0109
            r16 = r0
            r15 = 1
            goto L_0x0125
        L_0x0109:
            r11 = 1
            java.lang.Boolean r15 = java.lang.Boolean.valueOf(r11)     // Catch:{ Exception -> 0x013a }
            r4.put(r9, r15)     // Catch:{ Exception -> 0x013a }
            im.bclpbkiauv.messenger.MediaDataController$KeywordResult r11 = new im.bclpbkiauv.messenger.MediaDataController$KeywordResult     // Catch:{ Exception -> 0x013a }
            r11.<init>()     // Catch:{ Exception -> 0x013a }
            r11.emoji = r9     // Catch:{ Exception -> 0x013a }
            r16 = r0
            r15 = 1
            java.lang.String r0 = r8.stringValue(r15)     // Catch:{ Exception -> 0x013a }
            r11.keyword = r0     // Catch:{ Exception -> 0x013a }
            r3.add(r11)     // Catch:{ Exception -> 0x013a }
        L_0x0125:
            r0 = r16
            r9 = 1
            goto L_0x00eb
        L_0x0129:
            r16 = r0
            r15 = 1
            r8.dispose()     // Catch:{ Exception -> 0x013a }
        L_0x012f:
            int r10 = r10 + 1
            r0 = r16
            r8 = 0
            r9 = 1
            goto L_0x0059
        L_0x0137:
            r16 = r0
            goto L_0x0142
        L_0x013a:
            r0 = move-exception
            goto L_0x013f
        L_0x013c:
            r0 = move-exception
            r7 = r18
        L_0x013f:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0142:
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$Vuap-UejkzQRUMb9VWhuk40vcQQ r0 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$Vuap-UejkzQRUMb9VWhuk40vcQQ
            r6 = r23
            r0.<init>(r6)
            java.util.Collections.sort(r3, r0)
            r0 = r5
            if (r24 == 0) goto L_0x0156
            r2.run(r3, r0)
            r24.countDown()
            goto L_0x015e
        L_0x0156:
            im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$rNiddELpNSjsnsN1KFv7VlkKIRo r8 = new im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$rNiddELpNSjsnsN1KFv7VlkKIRo
            r8.<init>(r3, r0)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r8)
        L_0x015e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaDataController.lambda$getEmojiSuggestions$125$MediaDataController(java.lang.String[], im.bclpbkiauv.messenger.MediaDataController$KeywordResultCallback, java.lang.String, boolean, java.util.ArrayList, java.util.concurrent.CountDownLatch):void");
    }

    public /* synthetic */ void lambda$null$122$MediaDataController(String[] langCodes, KeywordResultCallback callback, ArrayList result) {
        int a = 0;
        while (a < langCodes.length) {
            if (this.currentFetchingEmoji.get(langCodes[a]) == null) {
                a++;
            } else {
                return;
            }
        }
        callback.run(result, (String) null);
    }

    static /* synthetic */ int lambda$null$123(ArrayList recentEmoji, KeywordResult o1, KeywordResult o2) {
        int idx1 = recentEmoji.indexOf(o1.emoji);
        if (idx1 < 0) {
            idx1 = Integer.MAX_VALUE;
        }
        int idx2 = recentEmoji.indexOf(o2.emoji);
        if (idx2 < 0) {
            idx2 = Integer.MAX_VALUE;
        }
        if (idx1 < idx2) {
            return -1;
        }
        if (idx1 > idx2) {
            return 1;
        }
        int len1 = o1.keyword.length();
        int len2 = o2.keyword.length();
        if (len1 < len2) {
            return -1;
        }
        if (len1 > len2) {
            return 1;
        }
        return 0;
    }
}
