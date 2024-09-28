package im.bclpbkiauv.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.cells.EmojiReplacementCell;
import im.bclpbkiauv.ui.cells.StickerCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class StickersAdapter extends RecyclerListView.SelectionAdapter implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount = UserConfig.selectedAccount;
    private boolean delayLocalResults;
    private StickersAdapterDelegate delegate;
    private ArrayList<MediaDataController.KeywordResult> keywordResults;
    private int lastReqId;
    private String[] lastSearchKeyboardLanguage;
    private String lastSticker;
    private Context mContext;
    private Runnable searchRunnable;
    private ArrayList<StickerResult> stickers;
    private HashMap<String, TLRPC.Document> stickersMap;
    private ArrayList<String> stickersToLoad = new ArrayList<>();
    private boolean visible;

    public interface StickersAdapterDelegate {
        void needChangePanelVisibility(boolean z);
    }

    private class StickerResult {
        public Object parent;
        public TLRPC.Document sticker;

        public StickerResult(TLRPC.Document s, Object p) {
            this.sticker = s;
            this.parent = p;
        }
    }

    public StickersAdapter(Context context, StickersAdapterDelegate delegate2) {
        this.mContext = context;
        this.delegate = delegate2;
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileDidLoad || id == NotificationCenter.fileDidFailToLoad) {
            ArrayList<StickerResult> arrayList = this.stickers;
            if (arrayList != null && !arrayList.isEmpty() && !this.stickersToLoad.isEmpty() && this.visible) {
                boolean show = false;
                this.stickersToLoad.remove(args[0]);
                if (this.stickersToLoad.isEmpty()) {
                    ArrayList<StickerResult> arrayList2 = this.stickers;
                    if (arrayList2 != null && !arrayList2.isEmpty() && this.stickersToLoad.isEmpty()) {
                        show = true;
                    }
                    if (show) {
                        this.keywordResults = null;
                    }
                    this.delegate.needChangePanelVisibility(show);
                }
            }
        } else if (id == NotificationCenter.newEmojiSuggestionsAvailable) {
            ArrayList<MediaDataController.KeywordResult> arrayList3 = this.keywordResults;
            if ((arrayList3 == null || arrayList3.isEmpty()) && !TextUtils.isEmpty(this.lastSticker) && getItemCount() == 0) {
                searchEmojiByKeyword();
            }
        }
    }

    private boolean checkStickerFilesExistAndDownload() {
        if (this.stickers == null) {
            return false;
        }
        this.stickersToLoad.clear();
        int size = Math.min(6, this.stickers.size());
        for (int a = 0; a < size; a++) {
            StickerResult result = this.stickers.get(a);
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(result.sticker.thumbs, 90);
            if ((thumb instanceof TLRPC.TL_photoSize) && !FileLoader.getPathToAttach(thumb, "webp", true).exists()) {
                this.stickersToLoad.add(FileLoader.getAttachFileName(thumb, "webp"));
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForDocument(thumb, result.sticker), result.parent, "webp", 1, 1);
            }
        }
        return this.stickersToLoad.isEmpty();
    }

    private boolean isValidSticker(TLRPC.Document document, String emoji) {
        int b = 0;
        int size2 = document.attributes.size();
        while (b < size2) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(b);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                b++;
            } else if (attribute.alt == null || !attribute.alt.contains(emoji)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void addStickerToResult(TLRPC.Document document, Object parent) {
        if (document != null) {
            String key = document.dc_id + "_" + document.id;
            HashMap<String, TLRPC.Document> hashMap = this.stickersMap;
            if (hashMap == null || !hashMap.containsKey(key)) {
                if (this.stickers == null) {
                    this.stickers = new ArrayList<>();
                    this.stickersMap = new HashMap<>();
                }
                this.stickers.add(new StickerResult(document, parent));
                this.stickersMap.put(key, document);
            }
        }
    }

    private void addStickersToResult(ArrayList<TLRPC.Document> documents, Object parent) {
        if (documents != null && !documents.isEmpty()) {
            int size = documents.size();
            for (int a = 0; a < size; a++) {
                TLRPC.Document document = documents.get(a);
                String key = document.dc_id + "_" + document.id;
                HashMap<String, TLRPC.Document> hashMap = this.stickersMap;
                if (hashMap == null || !hashMap.containsKey(key)) {
                    if (this.stickers == null) {
                        this.stickers = new ArrayList<>();
                        this.stickersMap = new HashMap<>();
                    }
                    int b = 0;
                    int size2 = document.attributes.size();
                    while (true) {
                        if (b >= size2) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = document.attributes.get(b);
                        if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                            parent = attribute.stickerset;
                            break;
                        }
                        b++;
                    }
                    this.stickers.add(new StickerResult(document, parent));
                    this.stickersMap.put(key, document);
                }
            }
        }
    }

    public void hide() {
        ArrayList<MediaDataController.KeywordResult> arrayList;
        if (!this.visible) {
            return;
        }
        if (this.stickers != null || ((arrayList = this.keywordResults) != null && !arrayList.isEmpty())) {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
        }
    }

    private void cancelEmojiSearch() {
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable = null;
        }
    }

    private void searchEmojiByKeyword() {
        String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
        if (!Arrays.equals(newLanguage, this.lastSearchKeyboardLanguage)) {
            MediaDataController.getInstance(this.currentAccount).fetchNewEmojiKeywords(newLanguage);
        }
        this.lastSearchKeyboardLanguage = newLanguage;
        String query = this.lastSticker;
        cancelEmojiSearch();
        this.searchRunnable = new Runnable(query) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                StickersAdapter.this.lambda$searchEmojiByKeyword$1$StickersAdapter(this.f$1);
            }
        };
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 1000);
        } else {
            this.searchRunnable.run();
        }
    }

    public /* synthetic */ void lambda$searchEmojiByKeyword$1$StickersAdapter(String query) {
        MediaDataController.getInstance(this.currentAccount).getEmojiSuggestions(this.lastSearchKeyboardLanguage, query, true, new MediaDataController.KeywordResultCallback(query) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(ArrayList arrayList, String str) {
                StickersAdapter.this.lambda$null$0$StickersAdapter(this.f$1, arrayList, str);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$StickersAdapter(String query, ArrayList param, String alias) {
        if (query.equals(this.lastSticker)) {
            if (!param.isEmpty()) {
                this.keywordResults = param;
            }
            notifyDataSetChanged();
            StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
            boolean z = !param.isEmpty();
            this.visible = z;
            stickersAdapterDelegate.needChangePanelVisibility(z);
        }
    }

    public void loadStikersForEmoji(CharSequence emoji, boolean emojiOnly) {
        ArrayList<MediaDataController.KeywordResult> arrayList;
        TLRPC.Document animatedSticker;
        boolean searchEmoji = emoji != null && emoji.length() > 0 && emoji.length() <= 14;
        String originalEmoji = emoji.toString();
        int a = 0;
        int length = emoji.length();
        CharSequence emoji2 = emoji;
        while (a < length) {
            if (a < length - 1 && ((emoji2.charAt(a) == 55356 && emoji2.charAt(a + 1) >= 57339 && emoji2.charAt(a + 1) <= 57343) || (emoji2.charAt(a) == 8205 && (emoji2.charAt(a + 1) == 9792 || emoji2.charAt(a + 1) == 9794)))) {
                emoji2 = TextUtils.concat(new CharSequence[]{emoji2.subSequence(0, a), emoji2.subSequence(a + 2, emoji2.length())});
                length -= 2;
                a--;
            } else if (emoji2.charAt(a) == 65039) {
                emoji2 = TextUtils.concat(new CharSequence[]{emoji2.subSequence(0, a), emoji2.subSequence(a + 1, emoji2.length())});
                length--;
                a--;
            }
            a++;
        }
        this.lastSticker = emoji2.toString();
        this.stickersToLoad.clear();
        boolean isValidEmoji = searchEmoji && (Emoji.isValidEmoji(originalEmoji) || Emoji.isValidEmoji(this.lastSticker));
        if (isValidEmoji && (animatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoji2)) != null) {
            ArrayList<TLRPC.TL_messages_stickerSet> sets = MediaDataController.getInstance(this.currentAccount).getStickerSets(4);
            if (!FileLoader.getPathToAttach(animatedSticker, true).exists()) {
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForDocument(animatedSticker), sets.get(0), (String) null, 1, 1);
            }
        }
        if (emojiOnly || SharedConfig.suggestStickers == 2 || !isValidEmoji) {
            if (this.visible && ((arrayList = this.keywordResults) == null || arrayList.isEmpty())) {
                this.visible = false;
                this.delegate.needChangePanelVisibility(false);
                notifyDataSetChanged();
            }
            if (!isValidEmoji) {
                searchEmojiByKeyword();
                return;
            }
            return;
        }
        cancelEmojiSearch();
        this.stickers = null;
        this.stickersMap = null;
        if (this.lastReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
            this.lastReqId = 0;
        }
        this.delayLocalResults = false;
        final ArrayList<TLRPC.Document> recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickersNoCopy(0);
        final ArrayList<TLRPC.Document> favsStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickersNoCopy(2);
        int recentsAdded = 0;
        int size = recentStickers.size();
        for (int a2 = 0; a2 < size; a2++) {
            TLRPC.Document document = recentStickers.get(a2);
            if (isValidSticker(document, this.lastSticker)) {
                addStickerToResult(document, "recent");
                recentsAdded++;
                if (recentsAdded >= 5) {
                    break;
                }
            }
        }
        int size2 = favsStickers.size();
        for (int a3 = 0; a3 < size2; a3++) {
            TLRPC.Document document2 = favsStickers.get(a3);
            if (isValidSticker(document2, this.lastSticker)) {
                addStickerToResult(document2, "fav");
            }
        }
        HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(this.currentAccount).getAllStickers();
        ArrayList<TLRPC.Document> newStickers = allStickers != null ? allStickers.get(this.lastSticker) : null;
        if (newStickers != null && !newStickers.isEmpty()) {
            addStickersToResult(newStickers, (Object) null);
        }
        ArrayList<StickerResult> arrayList2 = this.stickers;
        if (arrayList2 != null) {
            Collections.sort(arrayList2, new Comparator<StickerResult>() {
                private int getIndex(long id) {
                    for (int a = 0; a < favsStickers.size(); a++) {
                        if (((TLRPC.Document) favsStickers.get(a)).id == id) {
                            return a + 1000;
                        }
                    }
                    for (int a2 = 0; a2 < recentStickers.size(); a2++) {
                        if (((TLRPC.Document) recentStickers.get(a2)).id == id) {
                            return a2;
                        }
                    }
                    return -1;
                }

                public int compare(StickerResult lhs, StickerResult rhs) {
                    boolean isAnimated1 = MessageObject.isAnimatedStickerDocument(lhs.sticker);
                    boolean isAnimated2 = MessageObject.isAnimatedStickerDocument(rhs.sticker);
                    if (isAnimated1 != isAnimated2) {
                        return (!isAnimated1 || isAnimated2) ? 1 : -1;
                    }
                    int idx1 = getIndex(lhs.sticker.id);
                    int idx2 = getIndex(rhs.sticker.id);
                    if (idx1 > idx2) {
                        return -1;
                    }
                    if (idx1 < idx2) {
                        return 1;
                    }
                    return 0;
                }
            });
        }
        if (SharedConfig.suggestStickers == 0) {
            searchServerStickers(this.lastSticker, originalEmoji);
        }
        ArrayList<StickerResult> arrayList3 = this.stickers;
        if (arrayList3 != null && !arrayList3.isEmpty()) {
            if (SharedConfig.suggestStickers != 0 || this.stickers.size() >= 5) {
                checkStickerFilesExistAndDownload();
                boolean show = this.stickersToLoad.isEmpty();
                if (show) {
                    this.keywordResults = null;
                }
                this.delegate.needChangePanelVisibility(show);
                this.visible = true;
            } else {
                this.delayLocalResults = true;
                this.delegate.needChangePanelVisibility(false);
                this.visible = false;
            }
            notifyDataSetChanged();
        } else if (this.visible) {
            this.delegate.needChangePanelVisibility(false);
            this.visible = false;
        }
    }

    private void searchServerStickers(String emoji, String originalEmoji) {
        TLRPC.TL_messages_getStickers req = new TLRPC.TL_messages_getStickers();
        req.emoticon = originalEmoji;
        req.hash = 0;
        this.lastReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(emoji) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersAdapter.this.lambda$searchServerStickers$3$StickersAdapter(this.f$1, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$searchServerStickers$3$StickersAdapter(String emoji, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(emoji, response) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                StickersAdapter.this.lambda$null$2$StickersAdapter(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$StickersAdapter(String emoji, TLObject response) {
        ArrayList<StickerResult> arrayList;
        int newCount = 0;
        this.lastReqId = 0;
        if (emoji.equals(this.lastSticker) && (response instanceof TLRPC.TL_messages_stickers)) {
            this.delayLocalResults = false;
            TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
            ArrayList<StickerResult> arrayList2 = this.stickers;
            int oldCount = arrayList2 != null ? arrayList2.size() : 0;
            ArrayList<TLRPC.Document> arrayList3 = res.stickers;
            addStickersToResult(arrayList3, "sticker_search_" + emoji);
            ArrayList<StickerResult> arrayList4 = this.stickers;
            if (arrayList4 != null) {
                newCount = arrayList4.size();
            }
            if (!this.visible && (arrayList = this.stickers) != null && !arrayList.isEmpty()) {
                checkStickerFilesExistAndDownload();
                boolean show = this.stickersToLoad.isEmpty();
                if (show) {
                    this.keywordResults = null;
                }
                this.delegate.needChangePanelVisibility(show);
                this.visible = true;
            }
            if (oldCount != newCount) {
                notifyDataSetChanged();
            }
        }
    }

    public void clearStickers() {
        if (!this.delayLocalResults && this.lastReqId == 0) {
            if (this.stickersToLoad.isEmpty()) {
                this.lastSticker = null;
                this.stickers = null;
                this.stickersMap = null;
            }
            this.keywordResults = null;
            notifyDataSetChanged();
            if (this.lastReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
                this.lastReqId = 0;
            }
        }
    }

    public boolean isShowingKeywords() {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        return arrayList != null && !arrayList.isEmpty();
    }

    public int getItemCount() {
        ArrayList<StickerResult> arrayList;
        ArrayList<MediaDataController.KeywordResult> arrayList2 = this.keywordResults;
        if (arrayList2 != null && !arrayList2.isEmpty()) {
            return this.keywordResults.size();
        }
        if (this.delayLocalResults || (arrayList = this.stickers) == null) {
            return 0;
        }
        return arrayList.size();
    }

    public Object getItem(int i) {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList != null && !arrayList.isEmpty()) {
            return this.keywordResults.get(i).emoji;
        }
        ArrayList<StickerResult> arrayList2 = this.stickers;
        if (arrayList2 == null || i < 0 || i >= arrayList2.size()) {
            return null;
        }
        return this.stickers.get(i).sticker;
    }

    public Object getItemParent(int i) {
        ArrayList<StickerResult> arrayList;
        ArrayList<MediaDataController.KeywordResult> arrayList2 = this.keywordResults;
        if ((arrayList2 == null || arrayList2.isEmpty()) && (arrayList = this.stickers) != null && i >= 0 && i < arrayList.size()) {
            return this.stickers.get(i).parent;
        }
        return null;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return false;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType != 0) {
            view = new EmojiReplacementCell(this.mContext);
        } else {
            view = new StickerCell(this.mContext);
        }
        return new RecyclerListView.Holder(view);
    }

    public int getItemViewType(int position) {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty()) {
            return 0;
        }
        return 1;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 0) {
            int side = 0;
            if (position == 0) {
                if (this.stickers.size() == 1) {
                    side = 2;
                } else {
                    side = -1;
                }
            } else if (position == this.stickers.size() - 1) {
                side = 1;
            }
            StickerCell stickerCell = (StickerCell) holder.itemView;
            StickerResult result = this.stickers.get(position);
            stickerCell.setSticker(result.sticker, result.parent, side);
            stickerCell.setClearsInputField(true);
        } else if (itemViewType == 1) {
            int side2 = 0;
            if (position == 0) {
                if (this.keywordResults.size() == 1) {
                    side2 = 2;
                } else {
                    side2 = -1;
                }
            } else if (position == this.keywordResults.size() - 1) {
                side2 = 1;
            }
            ((EmojiReplacementCell) holder.itemView).setEmoji(this.keywordResults.get(position).emoji, side2);
        }
    }
}
