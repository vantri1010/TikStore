package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.Media1Activity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.cells.SharedAudioCell;
import im.bclpbkiauv.ui.cells.SharedDocumentCell;
import im.bclpbkiauv.ui.cells.SharedLinkCell;
import im.bclpbkiauv.ui.cells.SharedMediaSectionCell;
import im.bclpbkiauv.ui.cells.SharedPhotoVideoCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AnimationProperties;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.EmbedBottomSheet;
import im.bclpbkiauv.ui.components.FragmentContextView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.NumberTextView;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ScrollSlidingTextTabStrip;
import im.bclpbkiauv.ui.components.banner.config.BannerConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Media1Activity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int delete = 4;
    private static final int forward = 3;
    private static final int gotochat = 7;
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = $$Lambda$Media1Activity$DQ3bBPLK2z7xXV0D5UI_vxcVPfM.INSTANCE;
    public final Property<Media1Activity, Float> SCROLL_Y;
    private View actionModeBackground;
    private ArrayList<View> actionModeViews;
    /* access modifiers changed from: private */
    public int additionalPadding;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    private SharedDocumentsAdapter audioAdapter;
    /* access modifiers changed from: private */
    public ArrayList<SharedAudioCell> audioCache;
    /* access modifiers changed from: private */
    public ArrayList<SharedAudioCell> audioCellCache;
    /* access modifiers changed from: private */
    public MediaSearchAdapter audioSearchAdapter;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    /* access modifiers changed from: private */
    public Paint backgroundPaint;
    /* access modifiers changed from: private */
    public ArrayList<SharedPhotoVideoCell> cache;
    /* access modifiers changed from: private */
    public int cantDeleteMessagesCount;
    /* access modifiers changed from: private */
    public ArrayList<SharedPhotoVideoCell> cellCache;
    /* access modifiers changed from: private */
    public int columnsCount;
    /* access modifiers changed from: private */
    public long dialog_id;
    private SharedDocumentsAdapter documentsAdapter;
    /* access modifiers changed from: private */
    public MediaSearchAdapter documentsSearchAdapter;
    /* access modifiers changed from: private */
    public FragmentContextView fragmentContextView;
    private ActionBarMenuItem gotoItem;
    private int[] hasMedia;
    /* access modifiers changed from: private */
    public boolean ignoreSearchCollapse;
    protected TLRPC.ChatFull info;
    private int initialTab;
    private SharedLinksAdapter linksAdapter;
    /* access modifiers changed from: private */
    public MediaSearchAdapter linksSearchAdapter;
    /* access modifiers changed from: private */
    public int maximumVelocity;
    /* access modifiers changed from: private */
    public MediaPage[] mediaPages;
    /* access modifiers changed from: private */
    public long mergeDialogId;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private Drawable pinnedHeaderShadowDrawable;
    private PhotoViewer.PhotoViewerProvider provider;
    /* access modifiers changed from: private */
    public ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    /* access modifiers changed from: private */
    public boolean scrolling;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public int searchItemState;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public SparseArray<MessageObject>[] selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate;
    /* access modifiers changed from: private */
    public SharedMediaData[] sharedMediaData;
    /* access modifiers changed from: private */
    public AnimatorSet tabsAnimation;
    /* access modifiers changed from: private */
    public boolean tabsAnimationInProgress;
    private SharedDocumentsAdapter voiceAdapter;

    private class MediaPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView emptyImageView;
        /* access modifiers changed from: private */
        public TextView emptyTextView;
        /* access modifiers changed from: private */
        public LinearLayout emptyView;
        /* access modifiers changed from: private */
        public LinearLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public RadialProgressView progressBar;
        /* access modifiers changed from: private */
        public LinearLayout progressView;
        /* access modifiers changed from: private */
        public int selectedType;

        public MediaPage(Context context) {
            super(context);
        }
    }

    static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    public static class SharedMediaData {
        /* access modifiers changed from: private */
        public boolean[] endReached = {false, true};
        /* access modifiers changed from: private */
        public boolean loading;
        /* access modifiers changed from: private */
        public int[] max_id = {0, 0};
        /* access modifiers changed from: private */
        public ArrayList<MessageObject> messages = new ArrayList<>();
        /* access modifiers changed from: private */
        public SparseArray<MessageObject>[] messagesDict = {new SparseArray<>(), new SparseArray<>()};
        /* access modifiers changed from: private */
        public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<String> sections = new ArrayList<>();
        /* access modifiers changed from: private */
        public int totalCount;

        public void setTotalCount(int count) {
            this.totalCount = count;
        }

        public void setMaxId(int num, int value) {
            this.max_id[num] = value;
        }

        public void setEndReached(int num, boolean value) {
            this.endReached[num] = value;
        }

        public boolean addMessage(MessageObject messageObject, int loadIndex, boolean isNew, boolean enc) {
            if (this.messagesDict[loadIndex].indexOfKey(messageObject.getId()) >= 0) {
                return false;
            }
            ArrayList<MessageObject> messageObjects = this.sectionArrays.get(messageObject.monthKey);
            if (messageObjects == null) {
                messageObjects = new ArrayList<>();
                this.sectionArrays.put(messageObject.monthKey, messageObjects);
                if (isNew) {
                    this.sections.add(0, messageObject.monthKey);
                } else {
                    this.sections.add(messageObject.monthKey);
                }
            }
            if (isNew) {
                messageObjects.add(0, messageObject);
                this.messages.add(0, messageObject);
            } else {
                messageObjects.add(messageObject);
                this.messages.add(messageObject);
            }
            this.messagesDict[loadIndex].put(messageObject.getId(), messageObject);
            if (enc) {
                this.max_id[loadIndex] = Math.max(messageObject.getId(), this.max_id[loadIndex]);
                return true;
            } else if (messageObject.getId() <= 0) {
                return true;
            } else {
                this.max_id[loadIndex] = Math.min(messageObject.getId(), this.max_id[loadIndex]);
                return true;
            }
        }

        public boolean deleteMessage(int mid, int loadIndex) {
            ArrayList<MessageObject> messageObjects;
            MessageObject messageObject = this.messagesDict[loadIndex].get(mid);
            if (messageObject == null || (messageObjects = this.sectionArrays.get(messageObject.monthKey)) == null) {
                return false;
            }
            messageObjects.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[loadIndex].remove(messageObject.getId());
            if (messageObjects.isEmpty()) {
                this.sectionArrays.remove(messageObject.monthKey);
                this.sections.remove(messageObject.monthKey);
            }
            this.totalCount--;
            return true;
        }

        public void replaceMid(int oldMid, int newMid) {
            MessageObject obj = this.messagesDict[0].get(oldMid);
            if (obj != null) {
                this.messagesDict[0].remove(oldMid);
                this.messagesDict[0].put(newMid, obj);
                obj.messageOwner.id = newMid;
            }
        }
    }

    public Media1Activity(Bundle args, int[] media) {
        this(args, media, (SharedMediaData[]) null, 0);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Media1Activity(Bundle args, int[] media, SharedMediaData[] mediaData, int initTab) {
        super(args);
        this.mediaPages = new MediaPage[2];
        this.cellCache = new ArrayList<>(10);
        this.cache = new ArrayList<>(10);
        this.audioCellCache = new ArrayList<>(10);
        this.audioCache = new ArrayList<>(10);
        this.backgroundPaint = new Paint();
        this.selectedFiles = new SparseArray[]{new SparseArray<>(), new SparseArray<>()};
        this.actionModeViews = new ArrayList<>();
        this.info = null;
        this.columnsCount = 4;
        this.SCROLL_Y = new AnimationProperties.FloatProperty<Media1Activity>("animationValue") {
            public void setValue(Media1Activity object, float value) {
                object.setScrollY(value);
                for (MediaPage access$200 : Media1Activity.this.mediaPages) {
                    access$200.listView.checkSection();
                }
            }

            public Float get(Media1Activity object) {
                return Float.valueOf(Media1Activity.this.actionBar.getTranslationY());
            }
        };
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
                MessageObject message;
                if (messageObject == null || (Media1Activity.this.mediaPages[0].selectedType != 0 && Media1Activity.this.mediaPages[0].selectedType != 1)) {
                    return null;
                }
                int count = Media1Activity.this.mediaPages[0].listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = Media1Activity.this.mediaPages[0].listView.getChildAt(a);
                    BackupImageView imageView = null;
                    if (view instanceof SharedPhotoVideoCell) {
                        SharedPhotoVideoCell cell = (SharedPhotoVideoCell) view;
                        int i = 0;
                        while (i < 6 && (message = cell.getMessageObject(i)) != null) {
                            if (message.getId() == messageObject.getId()) {
                                imageView = cell.getImageView(i);
                            }
                            i++;
                        }
                    } else if (view instanceof SharedDocumentCell) {
                        SharedDocumentCell cell2 = (SharedDocumentCell) view;
                        if (cell2.getMessage().getId() == messageObject.getId()) {
                            imageView = cell2.getImageView();
                        }
                    }
                    if (imageView != null) {
                        int[] coords = new int[2];
                        imageView.getLocationInWindow(coords);
                        PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                        object.viewX = coords[0];
                        object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                        object.parentView = Media1Activity.this.mediaPages[0].listView;
                        object.imageReceiver = imageView.getImageReceiver();
                        object.thumb = object.imageReceiver.getBitmapSafe();
                        object.parentView.getLocationInWindow(coords);
                        object.clipTopAddition = (int) (((float) Media1Activity.this.actionBar.getHeight()) + Media1Activity.this.actionBar.getTranslationY());
                        if (Media1Activity.this.fragmentContextView != null && Media1Activity.this.fragmentContextView.getVisibility() == 0) {
                            object.clipTopAddition += AndroidUtilities.dp(36.0f);
                        }
                        return object;
                    }
                }
                return null;
            }
        };
        this.sharedMediaData = new SharedMediaData[5];
        this.sharedLinkCellDelegate = new SharedLinkCell.SharedLinkCellDelegate() {
            public void needOpenWebView(TLRPC.WebPage webPage) {
                Media1Activity.this.openWebView(webPage);
            }

            public boolean canPerformActions() {
                return !Media1Activity.this.actionBar.isActionModeShowed();
            }

            public void onLinkLongPress(String urlFinal) {
                BottomSheet.Builder builder = new BottomSheet.Builder(Media1Activity.this.getParentActivity());
                builder.setTitle(urlFinal);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener(urlFinal) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        Media1Activity.AnonymousClass15.this.lambda$onLinkLongPress$0$Media1Activity$15(this.f$1, dialogInterface, i);
                    }
                });
                Media1Activity.this.showDialog(builder.create());
            }

            public /* synthetic */ void lambda$onLinkLongPress$0$Media1Activity$15(String urlFinal, DialogInterface dialog, int which) {
                if (which == 0) {
                    Browser.openUrl((Context) Media1Activity.this.getParentActivity(), urlFinal, true);
                } else if (which == 1) {
                    String url = urlFinal;
                    if (url.startsWith("mailto:")) {
                        url = url.substring(7);
                    } else if (url.startsWith("tel:")) {
                        url = url.substring(4);
                    }
                    AndroidUtilities.addToClipboard(url);
                }
            }
        };
        this.hasMedia = media;
        this.initialTab = initTab;
        this.dialog_id = args.getLong("dialog_id", 0);
        int a = 0;
        while (true) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (a < sharedMediaDataArr.length) {
                sharedMediaDataArr[a] = new SharedMediaData();
                this.sharedMediaData[a].max_id[0] = ((int) this.dialog_id) == 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                if (!(this.mergeDialogId == 0 || this.info == null)) {
                    this.sharedMediaData[a].max_id[1] = this.info.migrated_from_max_id;
                    this.sharedMediaData[a].endReached[1] = false;
                }
                if (mediaData != null) {
                    int unused = this.sharedMediaData[a].totalCount = mediaData[a].totalCount;
                    this.sharedMediaData[a].messages.addAll(mediaData[a].messages);
                    this.sharedMediaData[a].sections.addAll(mediaData[a].sections);
                    for (Map.Entry<String, ArrayList<MessageObject>> entry : mediaData[a].sectionArrays.entrySet()) {
                        this.sharedMediaData[a].sectionArrays.put(entry.getKey(), new ArrayList(entry.getValue()));
                    }
                    for (int i = 0; i < 2; i++) {
                        this.sharedMediaData[a].messagesDict[i] = mediaData[a].messagesDict[i].clone();
                        this.sharedMediaData[a].max_id[i] = mediaData[a].max_id[i];
                    }
                }
                a++;
            } else {
                return;
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
    }

    public View createView(Context context) {
        TLRPC.User user;
        Context context2 = context;
        for (int a = 0; a < 10; a++) {
            this.cellCache.add(new SharedPhotoVideoCell(context2));
            if (this.initialTab == 4) {
                SharedAudioCell cell = new SharedAudioCell(context2) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? Media1Activity.this.sharedMediaData[4].messages : null, false);
                            return result;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(Media1Activity.this.sharedMediaData[4].messages, messageObject);
                        } else {
                            return false;
                        }
                    }
                };
                cell.initStreamingIcons();
                this.audioCellCache.add(cell);
            }
        }
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.searching = false;
        this.searchWas = false;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAddToContainer(false);
        int i = 1;
        this.actionBar.setClipContent(true);
        int lower_id = (int) this.dialog_id;
        if (lower_id == 0) {
            TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            if (!(encryptedChat == null || (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id))) == null)) {
                this.actionBar.setTitle(ContactsController.formatName(user.first_name, user.last_name));
            }
        } else if (lower_id > 0) {
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            if (user2 != null) {
                if (user2.self) {
                    this.actionBar.setTitle(LocaleController.getString("SavedMessages", R.string.SavedMessages));
                } else {
                    this.actionBar.setTitle(ContactsController.formatName(user2.first_name, user2.last_name));
                }
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (chat != null) {
                this.actionBar.setTitle(chat.title);
            }
        }
        if (TextUtils.isEmpty(this.actionBar.getTitle())) {
            this.actionBar.setTitle(LocaleController.getString("SharedContentTitle", R.string.SharedContentTitle));
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                int i = id;
                if (i == -1) {
                    if (Media1Activity.this.actionBar.isActionModeShowed()) {
                        for (int a = 1; a >= 0; a--) {
                            Media1Activity.this.selectedFiles[a].clear();
                        }
                        int unused = Media1Activity.this.cantDeleteMessagesCount = 0;
                        Media1Activity.this.actionBar.hideActionMode();
                        Media1Activity.this.updateRowsSelection();
                        return;
                    }
                    Media1Activity.this.finishFragment();
                } else if (i == 4) {
                    TLRPC.Chat currentChat = null;
                    TLRPC.User currentUser = null;
                    TLRPC.EncryptedChat currentEncryptedChat = null;
                    int lower_id = (int) Media1Activity.this.dialog_id;
                    if (lower_id == 0) {
                        currentEncryptedChat = MessagesController.getInstance(Media1Activity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (Media1Activity.this.dialog_id >> 32)));
                    } else if (lower_id > 0) {
                        currentUser = MessagesController.getInstance(Media1Activity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                    } else {
                        currentChat = MessagesController.getInstance(Media1Activity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                    }
                    Media1Activity media1Activity = Media1Activity.this;
                    AlertsCreator.createDeleteMessagesAlert(media1Activity, currentUser, currentChat, currentEncryptedChat, (TLRPC.ChatFull) null, media1Activity.mergeDialogId, (MessageObject) null, Media1Activity.this.selectedFiles, (MessageObject.GroupedMessages) null, false, 1, new Runnable() {
                        public final void run() {
                            Media1Activity.AnonymousClass4.this.lambda$onItemClick$0$Media1Activity$4();
                        }
                    });
                } else if (i == 3) {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 3);
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                        public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                            Media1Activity.AnonymousClass4.this.lambda$onItemClick$1$Media1Activity$4(dialogsActivity, arrayList, charSequence, z);
                        }
                    });
                    Media1Activity.this.presentFragment(fragment);
                } else if (i == 7 && Media1Activity.this.selectedFiles[0].size() == 1) {
                    Bundle args2 = new Bundle();
                    int lower_part = (int) Media1Activity.this.dialog_id;
                    int high_id = (int) (Media1Activity.this.dialog_id >> 32);
                    if (lower_part == 0) {
                        args2.putInt("enc_id", high_id);
                    } else if (lower_part > 0) {
                        args2.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        TLRPC.Chat chat = MessagesController.getInstance(Media1Activity.this.currentAccount).getChat(Integer.valueOf(-lower_part));
                        if (!(chat == null || chat.migrated_to == null)) {
                            args2.putInt("migrated_to", lower_part);
                            lower_part = -chat.migrated_to.channel_id;
                        }
                        args2.putInt("chat_id", -lower_part);
                    }
                    args2.putInt("message_id", Media1Activity.this.selectedFiles[0].keyAt(0));
                    NotificationCenter.getInstance(Media1Activity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    Media1Activity.this.presentFragment(new ChatActivity(args2), true);
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$Media1Activity$4() {
                Media1Activity.this.actionBar.hideActionMode();
                Media1Activity.this.actionBar.closeSearchField();
                int unused = Media1Activity.this.cantDeleteMessagesCount = 0;
            }

            public /* synthetic */ void lambda$onItemClick$1$Media1Activity$4(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
                ArrayList arrayList = dids;
                ArrayList<MessageObject> fmessages = new ArrayList<>();
                for (int a = 1; a >= 0; a--) {
                    ArrayList<Integer> ids = new ArrayList<>();
                    for (int b = 0; b < Media1Activity.this.selectedFiles[a].size(); b++) {
                        ids.add(Integer.valueOf(Media1Activity.this.selectedFiles[a].keyAt(b)));
                    }
                    Collections.sort(ids);
                    Iterator<Integer> it = ids.iterator();
                    while (it.hasNext()) {
                        Integer id1 = it.next();
                        if (id1.intValue() > 0) {
                            fmessages.add(Media1Activity.this.selectedFiles[a].get(id1.intValue()));
                        }
                    }
                    Media1Activity.this.selectedFiles[a].clear();
                }
                int unused = Media1Activity.this.cantDeleteMessagesCount = 0;
                Media1Activity.this.actionBar.hideActionMode();
                if (dids.size() > 1 || ((Long) arrayList.get(0)).longValue() == ((long) UserConfig.getInstance(Media1Activity.this.currentAccount).getClientUserId())) {
                    DialogsActivity dialogsActivity = fragment1;
                } else if (message != null) {
                    DialogsActivity dialogsActivity2 = fragment1;
                } else {
                    long did = ((Long) arrayList.get(0)).longValue();
                    int lower_part = (int) did;
                    int high_part = (int) (did >> 32);
                    Bundle args1 = new Bundle();
                    args1.putBoolean("scrollToTopOnResume", true);
                    if (lower_part == 0) {
                        args1.putInt("enc_id", high_part);
                    } else if (lower_part > 0) {
                        args1.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        args1.putInt("chat_id", -lower_part);
                    }
                    if (lower_part == 0) {
                        DialogsActivity dialogsActivity3 = fragment1;
                    } else if (!MessagesController.getInstance(Media1Activity.this.currentAccount).checkCanOpenChat(args1, fragment1)) {
                        return;
                    }
                    NotificationCenter.getInstance(Media1Activity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    ChatActivity chatActivity = new ChatActivity(args1);
                    Media1Activity.this.presentFragment(chatActivity, true);
                    chatActivity.showFieldPanelForForward(true, fmessages);
                    if (!AndroidUtilities.isTablet()) {
                        Media1Activity.this.removeSelfFromStack();
                        return;
                    }
                    return;
                }
                Media1Activity.this.updateRowsSelection();
                for (int a2 = 0; a2 < dids.size(); a2++) {
                    long did2 = ((Long) arrayList.get(a2)).longValue();
                    if (message != null) {
                        SendMessagesHelper.getInstance(Media1Activity.this.currentAccount).sendMessage(message.toString(), did2, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                    }
                    SendMessagesHelper.getInstance(Media1Activity.this.currentAccount).sendMessage(fmessages, did2, true, 0);
                }
                fragment1.finishFragment();
            }
        });
        Drawable drawable = context.getResources().getDrawable(R.drawable.photos_header_shadow);
        this.pinnedHeaderShadowDrawable = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundGrayShadow), PorterDuff.Mode.MULTIPLY));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip2 != null) {
            this.initialTab = scrollSlidingTextTabStrip2.getCurrentTabId();
        }
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip3 = new ScrollSlidingTextTabStrip(context2);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip3;
        int i2 = this.initialTab;
        if (i2 != -1) {
            scrollSlidingTextTabStrip3.setInitialTabId(i2);
            this.initialTab = -1;
        }
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int id, boolean forward) {
                if (Media1Activity.this.mediaPages[0].selectedType != id) {
                    Media1Activity media1Activity = Media1Activity.this;
                    boolean unused = media1Activity.swipeBackEnabled = id == media1Activity.scrollSlidingTextTabStrip.getFirstTabId();
                    int unused2 = Media1Activity.this.mediaPages[1].selectedType = id;
                    Media1Activity.this.mediaPages[1].setVisibility(0);
                    Media1Activity.this.switchToCurrentSelectedMode(true);
                    boolean unused3 = Media1Activity.this.animatingForward = forward;
                }
            }

            public void onPageScrolled(float progress) {
                if (progress != 1.0f || Media1Activity.this.mediaPages[1].getVisibility() == 0) {
                    if (Media1Activity.this.animatingForward) {
                        Media1Activity.this.mediaPages[0].setTranslationX((-progress) * ((float) Media1Activity.this.mediaPages[0].getMeasuredWidth()));
                        Media1Activity.this.mediaPages[1].setTranslationX(((float) Media1Activity.this.mediaPages[0].getMeasuredWidth()) - (((float) Media1Activity.this.mediaPages[0].getMeasuredWidth()) * progress));
                    } else {
                        Media1Activity.this.mediaPages[0].setTranslationX(((float) Media1Activity.this.mediaPages[0].getMeasuredWidth()) * progress);
                        Media1Activity.this.mediaPages[1].setTranslationX((((float) Media1Activity.this.mediaPages[0].getMeasuredWidth()) * progress) - ((float) Media1Activity.this.mediaPages[0].getMeasuredWidth()));
                    }
                    if (Media1Activity.this.searchItemState == 1) {
                        Media1Activity.this.searchItem.setAlpha(progress);
                    } else if (Media1Activity.this.searchItemState == 2) {
                        Media1Activity.this.searchItem.setAlpha(1.0f - progress);
                    }
                    if (progress == 1.0f) {
                        MediaPage tempPage = Media1Activity.this.mediaPages[0];
                        Media1Activity.this.mediaPages[0] = Media1Activity.this.mediaPages[1];
                        Media1Activity.this.mediaPages[1] = tempPage;
                        Media1Activity.this.mediaPages[1].setVisibility(8);
                        if (Media1Activity.this.searchItemState == 2) {
                            Media1Activity.this.searchItem.setVisibility(4);
                        }
                        int unused = Media1Activity.this.searchItemState = 0;
                    }
                }
            }
        });
        for (int a2 = 1; a2 >= 0; a2--) {
            this.selectedFiles[a2].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = Media1Activity.this.searching = true;
                Media1Activity.this.resetScroll();
            }

            public void onSearchCollapse() {
                boolean unused = Media1Activity.this.searching = false;
                boolean unused2 = Media1Activity.this.searchWas = false;
                Media1Activity.this.documentsSearchAdapter.search((String) null);
                Media1Activity.this.linksSearchAdapter.search((String) null);
                Media1Activity.this.audioSearchAdapter.search((String) null);
                if (Media1Activity.this.ignoreSearchCollapse) {
                    boolean unused3 = Media1Activity.this.ignoreSearchCollapse = false;
                } else {
                    Media1Activity.this.switchToCurrentSelectedMode(false);
                }
            }

            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    boolean unused = Media1Activity.this.searchWas = true;
                    Media1Activity.this.switchToCurrentSelectedMode(false);
                } else {
                    boolean unused2 = Media1Activity.this.searchWas = false;
                    Media1Activity.this.switchToCurrentSelectedMode(false);
                }
                if (Media1Activity.this.mediaPages[0].selectedType == 1) {
                    if (Media1Activity.this.documentsSearchAdapter != null) {
                        Media1Activity.this.documentsSearchAdapter.search(text);
                    }
                } else if (Media1Activity.this.mediaPages[0].selectedType == 3) {
                    if (Media1Activity.this.linksSearchAdapter != null) {
                        Media1Activity.this.linksSearchAdapter.search(text);
                    }
                } else if (Media1Activity.this.mediaPages[0].selectedType == 4 && Media1Activity.this.audioSearchAdapter != null) {
                    Media1Activity.this.audioSearchAdapter.search(text);
                }
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
        this.searchItem.setVisibility(4);
        this.searchItemState = 0;
        this.hasOwnBackground = true;
        ActionBarMenu actionMode = this.actionBar.createActionMode(false);
        actionMode.setBackgroundDrawable((Drawable) null);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), true);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), true);
        View view = new View(context2);
        this.actionModeBackground = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_sharedMedia_actionMode));
        this.actionModeBackground.setAlpha(0.0f);
        this.actionBar.addView(this.actionModeBackground, this.actionBar.indexOfChild(actionMode));
        NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
        this.selectedMessagesCountTextView.setOnTouchListener($$Lambda$Media1Activity$1jpjJFMAwOfJuZC6aZg9FtJLVQ.INSTANCE);
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        if (((int) this.dialog_id) != 0) {
            ArrayList<View> arrayList = this.actionModeViews;
            ActionBarMenuItem addItemWithWidth = actionMode.addItemWithWidth(7, R.drawable.msg_message, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrGoToMessage", R.string.AccDescrGoToMessage));
            this.gotoItem = addItemWithWidth;
            arrayList.add(addItemWithWidth);
            this.actionModeViews.add(actionMode.addItemWithWidth(3, R.drawable.msg_forward, AndroidUtilities.dp(54.0f), LocaleController.getString("Forward", R.string.Forward)));
        }
        this.actionModeViews.add(actionMode.addItemWithWidth(4, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", R.string.Delete)));
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context2);
        this.documentsAdapter = new SharedDocumentsAdapter(context2, 1);
        int i3 = 2;
        this.voiceAdapter = new SharedDocumentsAdapter(context2, 2);
        this.audioAdapter = new SharedDocumentsAdapter(context2, 4);
        this.documentsSearchAdapter = new MediaSearchAdapter(context2, 1);
        this.audioSearchAdapter = new MediaSearchAdapter(context2, 4);
        this.linksSearchAdapter = new MediaSearchAdapter(context2, 3);
        this.linksAdapter = new SharedLinksAdapter(context2);
        FrameLayout r3 = new FrameLayout(context2) {
            private boolean globalIgnoreLayout;
            /* access modifiers changed from: private */
            public boolean maybeStartTracking;
            /* access modifiers changed from: private */
            public boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent ev, boolean forward) {
                int id = Media1Activity.this.scrollSlidingTextTabStrip.getNextPageId(forward);
                if (id < 0) {
                    return false;
                }
                if (Media1Activity.this.searchItemState != 0) {
                    if (Media1Activity.this.searchItemState == 2) {
                        Media1Activity.this.searchItem.setAlpha(1.0f);
                    } else if (Media1Activity.this.searchItemState == 1) {
                        Media1Activity.this.searchItem.setAlpha(0.0f);
                        Media1Activity.this.searchItem.setVisibility(4);
                    }
                    int unused = Media1Activity.this.searchItemState = 0;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) ev.getX();
                Media1Activity.this.actionBar.setEnabled(false);
                Media1Activity.this.scrollSlidingTextTabStrip.setEnabled(false);
                int unused2 = Media1Activity.this.mediaPages[1].selectedType = id;
                Media1Activity.this.mediaPages[1].setVisibility(0);
                boolean unused3 = Media1Activity.this.animatingForward = forward;
                Media1Activity.this.switchToCurrentSelectedMode(true);
                if (forward) {
                    Media1Activity.this.mediaPages[1].setTranslationX((float) Media1Activity.this.mediaPages[0].getMeasuredWidth());
                } else {
                    Media1Activity.this.mediaPages[1].setTranslationX((float) (-Media1Activity.this.mediaPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
                super.forceHasOverlappingRendering(hasOverlappingRendering);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                measureChildWithMargins(Media1Activity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = Media1Activity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int a = 0; a < Media1Activity.this.mediaPages.length; a++) {
                    if (Media1Activity.this.mediaPages[a] != null) {
                        if (Media1Activity.this.mediaPages[a].listView != null) {
                            Media1Activity.this.mediaPages[a].listView.setPadding(0, Media1Activity.this.additionalPadding + actionBarHeight, 0, AndroidUtilities.dp(4.0f));
                        }
                        if (Media1Activity.this.mediaPages[a].emptyView != null) {
                            Media1Activity.this.mediaPages[a].emptyView.setPadding(0, Media1Activity.this.additionalPadding + actionBarHeight, 0, 0);
                        }
                        if (Media1Activity.this.mediaPages[a].progressView != null) {
                            Media1Activity.this.mediaPages[a].progressView.setPadding(0, Media1Activity.this.additionalPadding + actionBarHeight, 0, 0);
                        }
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == Media1Activity.this.actionBar)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (Media1Activity.this.fragmentContextView != null) {
                    int y = Media1Activity.this.actionBar.getMeasuredHeight();
                    Media1Activity.this.fragmentContextView.layout(Media1Activity.this.fragmentContextView.getLeft(), Media1Activity.this.fragmentContextView.getTop() + y, Media1Activity.this.fragmentContextView.getRight(), Media1Activity.this.fragmentContextView.getBottom() + y);
                }
            }

            public void setPadding(int left, int top, int right, int bottom) {
                int unused = Media1Activity.this.additionalPadding = top;
                if (Media1Activity.this.fragmentContextView != null) {
                    Media1Activity.this.fragmentContextView.setTranslationY(((float) top) + Media1Activity.this.actionBar.getTranslationY());
                }
                int actionBarHeight = Media1Activity.this.actionBar.getMeasuredHeight();
                for (int a = 0; a < Media1Activity.this.mediaPages.length; a++) {
                    if (Media1Activity.this.mediaPages[a] != null) {
                        if (Media1Activity.this.mediaPages[a].emptyView != null) {
                            Media1Activity.this.mediaPages[a].emptyView.setPadding(0, Media1Activity.this.additionalPadding + actionBarHeight, 0, 0);
                        }
                        if (Media1Activity.this.mediaPages[a].progressView != null) {
                            Media1Activity.this.mediaPages[a].progressView.setPadding(0, Media1Activity.this.additionalPadding + actionBarHeight, 0, 0);
                        }
                        if (Media1Activity.this.mediaPages[a].listView != null) {
                            Media1Activity.this.mediaPages[a].listView.setPadding(0, Media1Activity.this.additionalPadding + actionBarHeight, 0, AndroidUtilities.dp(4.0f));
                            Media1Activity.this.mediaPages[a].listView.checkSection();
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (Media1Activity.this.parentLayout != null) {
                    Media1Activity.this.parentLayout.drawHeaderShadow(canvas, Media1Activity.this.actionBar.getMeasuredHeight() + ((int) Media1Activity.this.actionBar.getTranslationY()));
                }
            }

            public void requestLayout() {
                if (!this.globalIgnoreLayout) {
                    super.requestLayout();
                }
            }

            public boolean checkTabsAnimationInProgress() {
                if (!Media1Activity.this.tabsAnimationInProgress) {
                    return false;
                }
                boolean cancel = false;
                int i = -1;
                if (Media1Activity.this.backAnimation) {
                    if (Math.abs(Media1Activity.this.mediaPages[0].getTranslationX()) < 1.0f) {
                        Media1Activity.this.mediaPages[0].setTranslationX(0.0f);
                        MediaPage mediaPage = Media1Activity.this.mediaPages[1];
                        int measuredWidth = Media1Activity.this.mediaPages[0].getMeasuredWidth();
                        if (Media1Activity.this.animatingForward) {
                            i = 1;
                        }
                        mediaPage.setTranslationX((float) (measuredWidth * i));
                        cancel = true;
                    }
                } else if (Math.abs(Media1Activity.this.mediaPages[1].getTranslationX()) < 1.0f) {
                    MediaPage mediaPage2 = Media1Activity.this.mediaPages[0];
                    int measuredWidth2 = Media1Activity.this.mediaPages[0].getMeasuredWidth();
                    if (!Media1Activity.this.animatingForward) {
                        i = 1;
                    }
                    mediaPage2.setTranslationX((float) (measuredWidth2 * i));
                    Media1Activity.this.mediaPages[1].setTranslationX(0.0f);
                    cancel = true;
                }
                if (cancel) {
                    if (Media1Activity.this.tabsAnimation != null) {
                        Media1Activity.this.tabsAnimation.cancel();
                        AnimatorSet unused = Media1Activity.this.tabsAnimation = null;
                    }
                    boolean unused2 = Media1Activity.this.tabsAnimationInProgress = false;
                }
                return Media1Activity.this.tabsAnimationInProgress;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return checkTabsAnimationInProgress() || Media1Activity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                Media1Activity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                canvas.drawRect(0.0f, ((float) Media1Activity.this.actionBar.getMeasuredHeight()) + Media1Activity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), Media1Activity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent ev) {
                float dx;
                int duration;
                boolean z = false;
                if (Media1Activity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                if (ev != null && ev.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = ev.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) ev.getX();
                    this.startedTrackingY = (int) ev.getY();
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 != null) {
                        velocityTracker2.clear();
                    }
                } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int dx2 = (int) (ev.getX() - ((float) this.startedTrackingX));
                    int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(ev);
                    if (this.startedTracking && ((Media1Activity.this.animatingForward && dx2 > 0) || (!Media1Activity.this.animatingForward && dx2 < 0))) {
                        if (!prepareForMoving(ev, dx2 < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            Media1Activity.this.mediaPages[0].setTranslationX(0.0f);
                            if (Media1Activity.this.animatingForward) {
                                Media1Activity.this.mediaPages[1].setTranslationX((float) Media1Activity.this.mediaPages[0].getMeasuredWidth());
                            } else {
                                Media1Activity.this.mediaPages[1].setTranslationX((float) (-Media1Activity.this.mediaPages[0].getMeasuredWidth()));
                            }
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(dx2)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(dx2) / 3 > dy) {
                            if (dx2 < 0) {
                                z = true;
                            }
                            prepareForMoving(ev, z);
                        }
                    } else if (this.startedTracking) {
                        Media1Activity.this.mediaPages[0].setTranslationX((float) dx2);
                        if (Media1Activity.this.animatingForward) {
                            Media1Activity.this.mediaPages[1].setTranslationX((float) (Media1Activity.this.mediaPages[0].getMeasuredWidth() + dx2));
                        } else {
                            Media1Activity.this.mediaPages[1].setTranslationX((float) (dx2 - Media1Activity.this.mediaPages[0].getMeasuredWidth()));
                        }
                        float scrollProgress = ((float) Math.abs(dx2)) / ((float) Media1Activity.this.mediaPages[0].getMeasuredWidth());
                        if (Media1Activity.this.searchItemState == 2) {
                            Media1Activity.this.searchItem.setAlpha(1.0f - scrollProgress);
                        } else if (Media1Activity.this.searchItemState == 1) {
                            Media1Activity.this.searchItem.setAlpha(scrollProgress);
                        }
                        Media1Activity.this.scrollSlidingTextTabStrip.selectTabWithId(Media1Activity.this.mediaPages[1].selectedType, scrollProgress);
                    }
                } else if (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6)) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000, (float) Media1Activity.this.maximumVelocity);
                    if (!this.startedTracking) {
                        float velX = this.velocityTracker.getXVelocity();
                        float velY = this.velocityTracker.getYVelocity();
                        if (Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                            prepareForMoving(ev, velX < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float x = Media1Activity.this.mediaPages[0].getX();
                        AnimatorSet unused = Media1Activity.this.tabsAnimation = new AnimatorSet();
                        float velX2 = this.velocityTracker.getXVelocity();
                        boolean unused2 = Media1Activity.this.backAnimation = Math.abs(x) < ((float) Media1Activity.this.mediaPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX2) < 3500.0f || Math.abs(velX2) < Math.abs(this.velocityTracker.getYVelocity()));
                        if (Media1Activity.this.backAnimation) {
                            dx = Math.abs(x);
                            if (Media1Activity.this.animatingForward) {
                                Media1Activity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(Media1Activity.this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(Media1Activity.this.mediaPages[1], View.TRANSLATION_X, new float[]{(float) Media1Activity.this.mediaPages[1].getMeasuredWidth()})});
                            } else {
                                Media1Activity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(Media1Activity.this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(Media1Activity.this.mediaPages[1], View.TRANSLATION_X, new float[]{(float) (-Media1Activity.this.mediaPages[1].getMeasuredWidth())})});
                            }
                        } else {
                            dx = ((float) Media1Activity.this.mediaPages[0].getMeasuredWidth()) - Math.abs(x);
                            if (Media1Activity.this.animatingForward) {
                                Media1Activity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(Media1Activity.this.mediaPages[0], View.TRANSLATION_X, new float[]{(float) (-Media1Activity.this.mediaPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(Media1Activity.this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            } else {
                                Media1Activity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(Media1Activity.this.mediaPages[0], View.TRANSLATION_X, new float[]{(float) Media1Activity.this.mediaPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(Media1Activity.this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            }
                        }
                        Media1Activity.this.tabsAnimation.setInterpolator(Media1Activity.interpolator);
                        int width = getMeasuredWidth();
                        int halfWidth = width / 2;
                        float distance = ((float) halfWidth) + (((float) halfWidth) * AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (dx * 1.0f) / ((float) width))));
                        float velX3 = Math.abs(velX2);
                        if (velX3 > 0.0f) {
                            duration = Math.round(Math.abs(distance / velX3) * 1000.0f) * 4;
                        } else {
                            duration = (int) ((1.0f + (dx / ((float) getMeasuredWidth()))) * 100.0f);
                        }
                        Media1Activity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(duration, BannerConfig.SCROLL_TIME)));
                        Media1Activity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = Media1Activity.this.tabsAnimation = null;
                                if (Media1Activity.this.backAnimation) {
                                    Media1Activity.this.mediaPages[1].setVisibility(8);
                                    if (Media1Activity.this.searchItemState == 2) {
                                        Media1Activity.this.searchItem.setAlpha(1.0f);
                                    } else if (Media1Activity.this.searchItemState == 1) {
                                        Media1Activity.this.searchItem.setAlpha(0.0f);
                                        Media1Activity.this.searchItem.setVisibility(4);
                                    }
                                    int unused2 = Media1Activity.this.searchItemState = 0;
                                } else {
                                    MediaPage tempPage = Media1Activity.this.mediaPages[0];
                                    Media1Activity.this.mediaPages[0] = Media1Activity.this.mediaPages[1];
                                    Media1Activity.this.mediaPages[1] = tempPage;
                                    Media1Activity.this.mediaPages[1].setVisibility(8);
                                    if (Media1Activity.this.searchItemState == 2) {
                                        Media1Activity.this.searchItem.setVisibility(4);
                                    }
                                    int unused3 = Media1Activity.this.searchItemState = 0;
                                    boolean unused4 = Media1Activity.this.swipeBackEnabled = Media1Activity.this.mediaPages[0].selectedType == Media1Activity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    Media1Activity.this.scrollSlidingTextTabStrip.selectTabWithId(Media1Activity.this.mediaPages[0].selectedType, 1.0f);
                                }
                                boolean unused5 = Media1Activity.this.tabsAnimationInProgress = false;
                                boolean unused6 = AnonymousClass7.this.maybeStartTracking = false;
                                boolean unused7 = AnonymousClass7.this.startedTracking = false;
                                Media1Activity.this.actionBar.setEnabled(true);
                                Media1Activity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        Media1Activity.this.tabsAnimation.start();
                        boolean unused3 = Media1Activity.this.tabsAnimationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        Media1Activity.this.actionBar.setEnabled(true);
                        Media1Activity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    VelocityTracker velocityTracker3 = this.velocityTracker;
                    if (velocityTracker3 != null) {
                        velocityTracker3.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        FrameLayout frameLayout = r3;
        this.fragmentView = r3;
        frameLayout.setWillNotDraw(false);
        int scrollToPositionOnRecreate = -1;
        int scrollToOffsetOnRecreate = 0;
        int a3 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a3 >= mediaPageArr.length) {
                break;
            }
            if (!(a3 != 0 || mediaPageArr[a3] == null || mediaPageArr[a3].layoutManager == null)) {
                scrollToPositionOnRecreate = this.mediaPages[a3].layoutManager.findFirstVisibleItemPosition();
                if (scrollToPositionOnRecreate != this.mediaPages[a3].layoutManager.getItemCount() - i) {
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) this.mediaPages[a3].listView.findViewHolderForAdapterPosition(scrollToPositionOnRecreate);
                    if (holder != null) {
                        scrollToOffsetOnRecreate = holder.itemView.getTop();
                    } else {
                        scrollToPositionOnRecreate = -1;
                    }
                } else {
                    scrollToPositionOnRecreate = -1;
                }
            }
            final MediaPage mediaPage = new MediaPage(context2) {
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (Media1Activity.this.tabsAnimationInProgress && Media1Activity.this.mediaPages[0] == this) {
                        float scrollProgress = Math.abs(Media1Activity.this.mediaPages[0].getTranslationX()) / ((float) Media1Activity.this.mediaPages[0].getMeasuredWidth());
                        Media1Activity.this.scrollSlidingTextTabStrip.selectTabWithId(Media1Activity.this.mediaPages[1].selectedType, scrollProgress);
                        if (Media1Activity.this.searchItemState == 2) {
                            Media1Activity.this.searchItem.setAlpha(1.0f - scrollProgress);
                        } else if (Media1Activity.this.searchItemState == 1) {
                            Media1Activity.this.searchItem.setAlpha(scrollProgress);
                        }
                    }
                }
            };
            frameLayout.addView(mediaPage, LayoutHelper.createFrame(-1, -1.0f));
            MediaPage[] mediaPageArr2 = this.mediaPages;
            mediaPageArr2[a3] = mediaPage;
            final LinearLayoutManager layoutManager = mediaPageArr2[a3].layoutManager = new LinearLayoutManager(context2, i, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            RecyclerListView unused = this.mediaPages[a3].listView = new RecyclerListView(context2) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    Media1Activity.this.updateSections(this, true);
                }
            };
            this.mediaPages[a3].listView.setItemAnimator((RecyclerView.ItemAnimator) null);
            this.mediaPages[a3].listView.setClipToPadding(false);
            this.mediaPages[a3].listView.setSectionsType(i3);
            this.mediaPages[a3].listView.setLayoutManager(layoutManager);
            MediaPage[] mediaPageArr3 = this.mediaPages;
            ViewConfiguration configuration2 = configuration;
            mediaPageArr3[a3].addView(mediaPageArr3[a3].listView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a3].listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(mediaPage) {
                private final /* synthetic */ Media1Activity.MediaPage f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(View view, int i) {
                    Media1Activity.this.lambda$createView$2$Media1Activity(this.f$1, view, i);
                }
            });
            this.mediaPages[a3].listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1 && Media1Activity.this.searching && Media1Activity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(Media1Activity.this.getParentActivity().getCurrentFocus());
                    }
                    boolean unused = Media1Activity.this.scrolling = newState != 0;
                    if (newState != 1) {
                        int scrollY = (int) (-Media1Activity.this.actionBar.getTranslationY());
                        int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                        if (scrollY != 0 && scrollY != actionBarHeight) {
                            if (scrollY < actionBarHeight / 2) {
                                Media1Activity.this.mediaPages[0].listView.smoothScrollBy(0, -scrollY);
                            } else {
                                Media1Activity.this.mediaPages[0].listView.smoothScrollBy(0, actionBarHeight - scrollY);
                            }
                        }
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int type;
                    RecyclerView recyclerView2 = recyclerView;
                    if (!Media1Activity.this.searching || !Media1Activity.this.searchWas) {
                        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                        int totalItemCount = recyclerView.getAdapter().getItemCount();
                        if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2 && !Media1Activity.this.sharedMediaData[mediaPage.selectedType].loading) {
                            if (mediaPage.selectedType == 0) {
                                type = 0;
                            } else if (mediaPage.selectedType == 1) {
                                type = 1;
                            } else if (mediaPage.selectedType == 2) {
                                type = 2;
                            } else if (mediaPage.selectedType == 4) {
                                type = 4;
                            } else {
                                type = 3;
                            }
                            if (!Media1Activity.this.sharedMediaData[mediaPage.selectedType].endReached[0]) {
                                boolean unused = Media1Activity.this.sharedMediaData[mediaPage.selectedType].loading = true;
                                MediaDataController.getInstance(Media1Activity.this.currentAccount).loadMedia(Media1Activity.this.dialog_id, 50, Media1Activity.this.sharedMediaData[mediaPage.selectedType].max_id[0], type, 1, Media1Activity.this.classGuid);
                            } else if (Media1Activity.this.mergeDialogId != 0 && !Media1Activity.this.sharedMediaData[mediaPage.selectedType].endReached[1]) {
                                boolean unused2 = Media1Activity.this.sharedMediaData[mediaPage.selectedType].loading = true;
                                MediaDataController.getInstance(Media1Activity.this.currentAccount).loadMedia(Media1Activity.this.mergeDialogId, 50, Media1Activity.this.sharedMediaData[mediaPage.selectedType].max_id[1], type, 1, Media1Activity.this.classGuid);
                            }
                        }
                        if (recyclerView2 != Media1Activity.this.mediaPages[0].listView || Media1Activity.this.searching || Media1Activity.this.actionBar.isActionModeShowed()) {
                            int i = dy;
                        } else {
                            float currentTranslation = Media1Activity.this.actionBar.getTranslationY();
                            float newTranslation = currentTranslation - ((float) dy);
                            if (newTranslation < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                                newTranslation = (float) (-ActionBar.getCurrentActionBarHeight());
                            } else if (newTranslation > 0.0f) {
                                newTranslation = 0.0f;
                            }
                            if (newTranslation != currentTranslation) {
                                Media1Activity.this.setScrollY(newTranslation);
                            }
                        }
                        Media1Activity.this.updateSections(recyclerView2, false);
                    }
                }
            });
            this.mediaPages[a3].listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener(mediaPage) {
                private final /* synthetic */ Media1Activity.MediaPage f$1;

                {
                    this.f$1 = r2;
                }

                public final boolean onItemClick(View view, int i) {
                    return Media1Activity.this.lambda$createView$3$Media1Activity(this.f$1, view, i);
                }
            });
            if (a3 == 0 && scrollToPositionOnRecreate != -1) {
                layoutManager.scrollToPositionWithOffset(scrollToPositionOnRecreate, scrollToOffsetOnRecreate);
            }
            LinearLayout unused2 = this.mediaPages[a3].emptyView = new LinearLayout(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    Media1Activity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    canvas.drawRect(0.0f, ((float) Media1Activity.this.actionBar.getMeasuredHeight()) + Media1Activity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), Media1Activity.this.backgroundPaint);
                }
            };
            this.mediaPages[a3].emptyView.setWillNotDraw(false);
            this.mediaPages[a3].emptyView.setOrientation(1);
            this.mediaPages[a3].emptyView.setGravity(17);
            this.mediaPages[a3].emptyView.setVisibility(8);
            MediaPage[] mediaPageArr4 = this.mediaPages;
            mediaPageArr4[a3].addView(mediaPageArr4[a3].emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a3].emptyView.setOnTouchListener($$Lambda$Media1Activity$Afng4cAF2lE4DrChOEML3_RpC5Y.INSTANCE);
            ImageView unused3 = this.mediaPages[a3].emptyImageView = new ImageView(context2);
            this.mediaPages[a3].emptyView.addView(this.mediaPages[a3].emptyImageView, LayoutHelper.createLinear(-2, -2));
            TextView unused4 = this.mediaPages[a3].emptyTextView = new TextView(context2);
            this.mediaPages[a3].emptyTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.mediaPages[a3].emptyTextView.setGravity(17);
            this.mediaPages[a3].emptyTextView.setTextSize(1, 17.0f);
            this.mediaPages[a3].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            this.mediaPages[a3].emptyView.addView(this.mediaPages[a3].emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
            LinearLayout unused5 = this.mediaPages[a3].progressView = new LinearLayout(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    Media1Activity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    canvas.drawRect(0.0f, ((float) Media1Activity.this.actionBar.getMeasuredHeight()) + Media1Activity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), Media1Activity.this.backgroundPaint);
                }
            };
            this.mediaPages[a3].progressView.setWillNotDraw(false);
            this.mediaPages[a3].progressView.setGravity(17);
            i = 1;
            this.mediaPages[a3].progressView.setOrientation(1);
            this.mediaPages[a3].progressView.setVisibility(8);
            MediaPage[] mediaPageArr5 = this.mediaPages;
            mediaPageArr5[a3].addView(mediaPageArr5[a3].progressView, LayoutHelper.createFrame(-1, -1.0f));
            RadialProgressView unused6 = this.mediaPages[a3].progressBar = new RadialProgressView(context2);
            this.mediaPages[a3].progressView.addView(this.mediaPages[a3].progressBar, LayoutHelper.createLinear(-2, -2));
            if (a3 != 0) {
                this.mediaPages[a3].setVisibility(8);
            }
            a3++;
            configuration = configuration2;
            i3 = 2;
        }
        if (!AndroidUtilities.isTablet()) {
            FragmentContextView fragmentContextView2 = new FragmentContextView(context2, this, false);
            this.fragmentContextView = fragmentContextView2;
            frameLayout.addView(fragmentContextView2, LayoutHelper.createFrame(-1.0f, 39.0f, 51, 0.0f, 8.0f, 0.0f, 0.0f));
        }
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        boolean z = false;
        switchToCurrentSelectedMode(false);
        if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
            z = true;
        }
        this.swipeBackEnabled = z;
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$1(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$2$Media1Activity(MediaPage mediaPage, View view, int position) {
        if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            onItemClick(position, view, ((SharedDocumentCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
            onItemClick(position, view, ((SharedLinkCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
            onItemClick(position, view, ((SharedAudioCell) view).getMessage(), 0, mediaPage.selectedType);
        }
    }

    public /* synthetic */ boolean lambda$createView$3$Media1Activity(MediaPage mediaPage, View view, int position) {
        if (this.actionBar.isActionModeShowed()) {
            mediaPage.listView.getOnItemClickListener().onItemClick(view, position);
            return true;
        } else if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            return onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
        } else {
            if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
                return onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
            }
            if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
                return onItemLongClick(((SharedAudioCell) view).getMessage(), view, 0);
            }
            return false;
        }
    }

    static /* synthetic */ boolean lambda$createView$4(View v, MotionEvent event) {
        return true;
    }

    /* access modifiers changed from: private */
    public void setScrollY(float value) {
        this.actionBar.setTranslationY(value);
        FragmentContextView fragmentContextView2 = this.fragmentContextView;
        if (fragmentContextView2 != null) {
            fragmentContextView2.setTranslationY(((float) this.additionalPadding) + value);
        }
        int a = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a < mediaPageArr.length) {
                mediaPageArr[a].listView.setPinnedSectionOffsetY((int) value);
                a++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void resetScroll() {
        if (this.actionBar.getTranslationY() != 0.0f) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.SCROLL_Y, new float[]{0.0f})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int oldItemCount;
        int i = id;
        int i2 = 2;
        if (i == NotificationCenter.mediaDidLoad) {
            long uid = args[0].longValue();
            if (args[3].intValue() == this.classGuid) {
                int type = args[4].intValue();
                boolean unused = this.sharedMediaData[type].loading = false;
                int unused2 = this.sharedMediaData[type].totalCount = args[1].intValue();
                ArrayList<MessageObject> arr = args[2];
                boolean enc = ((int) this.dialog_id) == 0;
                int loadIndex = uid == this.dialog_id ? 0 : 1;
                RecyclerView.Adapter adapter = null;
                if (type == 0) {
                    adapter = this.photoVideoAdapter;
                } else if (type == 1) {
                    adapter = this.documentsAdapter;
                } else if (type == 2) {
                    adapter = this.voiceAdapter;
                } else if (type == 3) {
                    adapter = this.linksAdapter;
                } else if (type == 4) {
                    adapter = this.audioAdapter;
                }
                if (adapter != null) {
                    oldItemCount = adapter.getItemCount();
                    if (adapter instanceof RecyclerListView.SectionsAdapter) {
                        ((RecyclerListView.SectionsAdapter) adapter).notifySectionsChanged();
                    }
                } else {
                    oldItemCount = 0;
                }
                for (int a = 0; a < arr.size(); a++) {
                    this.sharedMediaData[type].addMessage(arr.get(a), loadIndex, false, enc);
                }
                this.sharedMediaData[type].endReached[loadIndex] = args[5].booleanValue();
                if (loadIndex != 0 || !this.sharedMediaData[type].endReached[loadIndex] || this.mergeDialogId == 0) {
                } else {
                    boolean unused3 = this.sharedMediaData[type].loading = true;
                    ArrayList<MessageObject> arrayList = arr;
                    MediaDataController.getInstance(this.currentAccount).loadMedia(this.mergeDialogId, 50, this.sharedMediaData[type].max_id[1], type, 1, this.classGuid);
                }
                if (adapter != null) {
                    int a2 = 0;
                    while (true) {
                        MediaPage[] mediaPageArr = this.mediaPages;
                        if (a2 >= mediaPageArr.length) {
                            break;
                        }
                        if (mediaPageArr[a2].listView.getAdapter() == adapter) {
                            this.mediaPages[a2].listView.stopScroll();
                        }
                        a2++;
                    }
                    int newItemCount = adapter.getItemCount();
                    if (oldItemCount > 1) {
                        adapter.notifyItemChanged(oldItemCount - 2);
                    }
                    if (newItemCount > oldItemCount) {
                        adapter.notifyItemRangeInserted(oldItemCount, newItemCount);
                    } else if (newItemCount < oldItemCount) {
                        adapter.notifyItemRangeRemoved(newItemCount, oldItemCount - newItemCount);
                    }
                }
                this.scrolling = true;
                int a3 = 0;
                while (true) {
                    MediaPage[] mediaPageArr2 = this.mediaPages;
                    if (a3 < mediaPageArr2.length) {
                        if (mediaPageArr2[a3].selectedType == type && !this.sharedMediaData[type].loading) {
                            if (this.mediaPages[a3].progressView != null) {
                                this.mediaPages[a3].progressView.setVisibility(8);
                            }
                            if (this.mediaPages[a3].selectedType == type && this.mediaPages[a3].listView != null && this.mediaPages[a3].listView.getEmptyView() == null) {
                                this.mediaPages[a3].listView.setEmptyView(this.mediaPages[a3].emptyView);
                            }
                        }
                        if (oldItemCount == 0 && this.actionBar.getTranslationY() != 0.0f && this.mediaPages[a3].listView.getAdapter() == adapter) {
                            this.mediaPages[a3].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
                        }
                        a3++;
                    } else {
                        return;
                    }
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!args[2].booleanValue()) {
                TLRPC.Chat currentChat = null;
                if (((int) this.dialog_id) < 0) {
                    currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
                }
                int channelId = args[1].intValue();
                int loadIndex2 = 0;
                if (ChatObject.isChannel(currentChat)) {
                    if (channelId == 0 && this.mergeDialogId != 0) {
                        loadIndex2 = 1;
                    } else if (channelId == currentChat.id) {
                        loadIndex2 = 0;
                    } else {
                        return;
                    }
                } else if (channelId != 0) {
                    return;
                }
                ArrayList<Integer> markAsDeletedMessages = args[0];
                boolean updated = false;
                int N = markAsDeletedMessages.size();
                for (int a4 = 0; a4 < N; a4++) {
                    int b = 0;
                    while (true) {
                        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                        if (b >= sharedMediaDataArr.length) {
                            break;
                        }
                        if (sharedMediaDataArr[b].deleteMessage(markAsDeletedMessages.get(a4).intValue(), loadIndex2)) {
                            updated = true;
                        }
                        b++;
                    }
                }
                if (updated) {
                    this.scrolling = true;
                    SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
                    if (sharedPhotoVideoAdapter != null) {
                        sharedPhotoVideoAdapter.notifyDataSetChanged();
                    }
                    SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
                    if (sharedDocumentsAdapter != null) {
                        sharedDocumentsAdapter.notifyDataSetChanged();
                    }
                    SharedDocumentsAdapter sharedDocumentsAdapter2 = this.voiceAdapter;
                    if (sharedDocumentsAdapter2 != null) {
                        sharedDocumentsAdapter2.notifyDataSetChanged();
                    }
                    SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
                    if (sharedLinksAdapter != null) {
                        sharedLinksAdapter.notifyDataSetChanged();
                    }
                    SharedDocumentsAdapter sharedDocumentsAdapter3 = this.audioAdapter;
                    if (sharedDocumentsAdapter3 != null) {
                        sharedDocumentsAdapter3.notifyDataSetChanged();
                    }
                }
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!args[2].booleanValue()) {
                long uid2 = args[0].longValue();
                long j = this.dialog_id;
                if (uid2 == j) {
                    ArrayList<MessageObject> arr2 = args[1];
                    boolean enc2 = ((int) j) == 0;
                    boolean updated2 = false;
                    for (int a5 = 0; a5 < arr2.size(); a5++) {
                        MessageObject obj = arr2.get(a5);
                        if (obj.messageOwner.media != null && !obj.needDrawBluredPreview()) {
                            int type2 = MediaDataController.getMediaType(obj.messageOwner);
                            if (type2 != -1) {
                                if (this.sharedMediaData[type2].addMessage(obj, obj.getDialogId() == this.dialog_id ? 0 : 1, true, enc2)) {
                                    this.hasMedia[type2] = 1;
                                    updated2 = true;
                                }
                            } else {
                                return;
                            }
                        }
                    }
                    if (updated2) {
                        this.scrolling = true;
                        int a6 = 0;
                        while (true) {
                            MediaPage[] mediaPageArr3 = this.mediaPages;
                            if (a6 < mediaPageArr3.length) {
                                RecyclerView.Adapter adapter2 = null;
                                if (mediaPageArr3[a6].selectedType == 0) {
                                    adapter2 = this.photoVideoAdapter;
                                } else if (this.mediaPages[a6].selectedType == 1) {
                                    adapter2 = this.documentsAdapter;
                                } else if (this.mediaPages[a6].selectedType == i2) {
                                    adapter2 = this.voiceAdapter;
                                } else if (this.mediaPages[a6].selectedType == 3) {
                                    adapter2 = this.linksAdapter;
                                } else if (this.mediaPages[a6].selectedType == 4) {
                                    adapter2 = this.audioAdapter;
                                }
                                if (adapter2 != null) {
                                    int count = adapter2.getItemCount();
                                    this.photoVideoAdapter.notifyDataSetChanged();
                                    this.documentsAdapter.notifyDataSetChanged();
                                    this.voiceAdapter.notifyDataSetChanged();
                                    this.linksAdapter.notifyDataSetChanged();
                                    this.audioAdapter.notifyDataSetChanged();
                                    if (count == 0) {
                                        if (this.actionBar.getTranslationY() != 0.0f) {
                                            this.mediaPages[a6].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
                                        }
                                    }
                                }
                                a6++;
                                i2 = 2;
                            } else {
                                updateTabs();
                                return;
                            }
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            if (!args[6].booleanValue()) {
                Integer msgId = args[0];
                Integer newMsgId = args[1];
                for (SharedMediaData data : this.sharedMediaData) {
                    data.replaceMid(msgId.intValue(), newMsgId.intValue());
                }
            }
        } else if (i != NotificationCenter.messagePlayingDidStart && i != NotificationCenter.messagePlayingPlayStateChanged && i != NotificationCenter.messagePlayingDidReset) {
        } else {
            if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
                int b2 = 0;
                while (true) {
                    MediaPage[] mediaPageArr4 = this.mediaPages;
                    if (b2 < mediaPageArr4.length) {
                        int count2 = mediaPageArr4[b2].listView.getChildCount();
                        for (int a7 = 0; a7 < count2; a7++) {
                            View view = this.mediaPages[b2].listView.getChildAt(a7);
                            if (view instanceof SharedAudioCell) {
                                SharedAudioCell cell = (SharedAudioCell) view;
                                if (cell.getMessage() != null) {
                                    cell.updateButtonState(false, true);
                                }
                            }
                        }
                        b2++;
                    } else {
                        return;
                    }
                }
            } else if (i == NotificationCenter.messagePlayingDidStart && args[0].eventId == 0) {
                int b3 = 0;
                while (true) {
                    MediaPage[] mediaPageArr5 = this.mediaPages;
                    if (b3 < mediaPageArr5.length) {
                        int count3 = mediaPageArr5[b3].listView.getChildCount();
                        for (int a8 = 0; a8 < count3; a8++) {
                            View view2 = this.mediaPages[b3].listView.getChildAt(a8);
                            if (view2 instanceof SharedAudioCell) {
                                SharedAudioCell cell2 = (SharedAudioCell) view2;
                                if (cell2.getMessage() != null) {
                                    cell2.updateButtonState(false, true);
                                }
                            }
                        }
                        b3++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        this.scrolling = true;
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        for (int a = 0; a < this.mediaPages.length; a++) {
            fixLayoutInternal(a);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int a = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a < mediaPageArr.length) {
                if (mediaPageArr[a].listView != null) {
                    final int num = a;
                    this.mediaPages[a].listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            Media1Activity.this.mediaPages[num].getViewTreeObserver().removeOnPreDrawListener(this);
                            Media1Activity.this.fixLayoutInternal(num);
                            return true;
                        }
                    });
                }
                a++;
            } else {
                return;
            }
        }
    }

    public boolean onBackPressed() {
        return this.actionBar.isEnabled();
    }

    /* access modifiers changed from: private */
    public void updateSections(ViewGroup listView, boolean checkBottom) {
        int count = listView.getChildCount();
        int minPositionDateHolder = Integer.MAX_VALUE;
        View minDateChild = null;
        float padding = ((float) listView.getPaddingTop()) + this.actionBar.getTranslationY();
        int maxBottom = 0;
        for (int a = 0; a < count; a++) {
            View view = listView.getChildAt(a);
            int bottom = view.getBottom();
            maxBottom = Math.max(bottom, maxBottom);
            if (((float) bottom) > padding) {
                int position = view.getBottom();
                if ((view instanceof SharedMediaSectionCell) || (view instanceof GraySectionCell)) {
                    if (view.getAlpha() != 1.0f) {
                        view.setAlpha(1.0f);
                    }
                    if (position < minPositionDateHolder) {
                        minPositionDateHolder = position;
                        minDateChild = view;
                    }
                }
            }
        }
        if (minDateChild != null) {
            if (((float) minDateChild.getTop()) > padding) {
                if (minDateChild.getAlpha() != 1.0f) {
                    minDateChild.setAlpha(1.0f);
                }
            } else if (minDateChild.getAlpha() != 0.0f) {
                minDateChild.setAlpha(0.0f);
            }
        }
        if (checkBottom && maxBottom != 0 && maxBottom < listView.getMeasuredHeight() - listView.getPaddingBottom()) {
            resetScroll();
        }
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        this.info = chatInfo;
        if (chatInfo != null && chatInfo.migrated_from_chat_id != 0 && this.mergeDialogId == 0) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
            int a = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (a < sharedMediaDataArr.length) {
                    sharedMediaDataArr[a].max_id[1] = this.info.migrated_from_max_id;
                    this.sharedMediaData[a].endReached[1] = false;
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    public void updateAdapters() {
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter2 = this.voiceAdapter;
        if (sharedDocumentsAdapter2 != null) {
            sharedDocumentsAdapter2.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter3 = this.audioAdapter;
        if (sharedDocumentsAdapter3 != null) {
            sharedDocumentsAdapter3.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void updateRowsSelection() {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                int count = mediaPageArr[i].listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.mediaPages[i].listView.getChildAt(a);
                    if (child instanceof SharedDocumentCell) {
                        ((SharedDocumentCell) child).setChecked(false, true);
                    } else if (child instanceof SharedPhotoVideoCell) {
                        for (int b = 0; b < 6; b++) {
                            ((SharedPhotoVideoCell) child).setChecked(b, false, true);
                        }
                    } else if (child instanceof SharedLinkCell) {
                        ((SharedLinkCell) child).setChecked(false, true);
                    } else if (child instanceof SharedAudioCell) {
                        ((SharedAudioCell) child).setChecked(false, true);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void setMergeDialogId(long did) {
        this.mergeDialogId = did;
    }

    private void updateTabs() {
        if (this.scrollSlidingTextTabStrip != null) {
            boolean changed = false;
            int[] iArr = this.hasMedia;
            if ((iArr[0] != 0 || (iArr[1] == 0 && iArr[2] == 0 && iArr[3] == 0 && iArr[4] == 0)) && !this.scrollSlidingTextTabStrip.hasTab(0)) {
                changed = true;
            }
            if (this.hasMedia[1] != 0 && !this.scrollSlidingTextTabStrip.hasTab(1)) {
                changed = true;
            }
            if (((int) this.dialog_id) != 0) {
                if (this.hasMedia[3] != 0 && !this.scrollSlidingTextTabStrip.hasTab(3)) {
                    changed = true;
                }
                if (this.hasMedia[4] != 0 && !this.scrollSlidingTextTabStrip.hasTab(4)) {
                    changed = true;
                }
            } else {
                TLRPC.EncryptedChat currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
                if (currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) >= 46 && this.hasMedia[4] != 0 && !this.scrollSlidingTextTabStrip.hasTab(4)) {
                    changed = true;
                }
            }
            if (this.hasMedia[2] != 0 && !this.scrollSlidingTextTabStrip.hasTab(2)) {
                changed = true;
            }
            if (changed) {
                this.scrollSlidingTextTabStrip.removeTabs();
                int[] iArr2 = this.hasMedia;
                if ((iArr2[0] != 0 || (iArr2[1] == 0 && iArr2[2] == 0 && iArr2[3] == 0 && iArr2[4] == 0)) && !this.scrollSlidingTextTabStrip.hasTab(0)) {
                    this.scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("SharedMediaTab", R.string.SharedMediaTab));
                }
                if (this.hasMedia[1] != 0 && !this.scrollSlidingTextTabStrip.hasTab(1)) {
                    this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("SharedFilesTab", R.string.SharedFilesTab));
                }
                if (((int) this.dialog_id) != 0) {
                    if (this.hasMedia[3] != 0 && !this.scrollSlidingTextTabStrip.hasTab(3)) {
                        this.scrollSlidingTextTabStrip.addTextTab(3, LocaleController.getString("SharedLinksTab", R.string.SharedLinksTab));
                    }
                    if (this.hasMedia[4] != 0 && !this.scrollSlidingTextTabStrip.hasTab(4)) {
                        this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab", R.string.SharedMusicTab));
                    }
                } else {
                    TLRPC.EncryptedChat currentEncryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
                    if (currentEncryptedChat2 != null && AndroidUtilities.getPeerLayerVersion(currentEncryptedChat2.layer) >= 46 && this.hasMedia[4] != 0 && !this.scrollSlidingTextTabStrip.hasTab(4)) {
                        this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab", R.string.SharedMusicTab));
                    }
                }
                if (this.hasMedia[2] != 0 && !this.scrollSlidingTextTabStrip.hasTab(2)) {
                    this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("SharedVoiceTab", R.string.SharedVoiceTab));
                }
            }
            if (this.scrollSlidingTextTabStrip.getTabsCount() <= 1) {
                this.scrollSlidingTextTabStrip.setVisibility(8);
                this.actionBar.setExtraHeight(0);
            } else {
                this.scrollSlidingTextTabStrip.setVisibility(0);
                this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
            }
            int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (id >= 0) {
                int unused = this.mediaPages[0].selectedType = id;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    /* access modifiers changed from: private */
    public void switchToCurrentSelectedMode(boolean animated) {
        MediaPage[] mediaPageArr;
        MediaSearchAdapter mediaSearchAdapter;
        int a = 0;
        while (true) {
            mediaPageArr = this.mediaPages;
            if (a >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[a].listView.stopScroll();
            a++;
        }
        int a2 = animated;
        RecyclerView.Adapter currentAdapter = mediaPageArr[a2].listView.getAdapter();
        if (!this.searching || !this.searchWas) {
            this.mediaPages[a2].emptyTextView.setTextSize(1, 17.0f);
            this.mediaPages[a2].emptyImageView.setVisibility(0);
            this.mediaPages[a2].listView.setPinnedHeaderShadowDrawable((Drawable) null);
            if (this.mediaPages[a2].selectedType == 0) {
                if (currentAdapter != this.photoVideoAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.photoVideoAdapter);
                }
                this.mediaPages[a2].listView.setPinnedHeaderShadowDrawable(this.pinnedHeaderShadowDrawable);
                this.mediaPages[a2].emptyImageView.setImageResource(R.drawable.tip1);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoMediaSecret", R.string.NoMediaSecret));
                } else {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoMedia", R.string.NoMedia));
                }
            } else if (this.mediaPages[a2].selectedType == 1) {
                if (currentAdapter != this.documentsAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.documentsAdapter);
                }
                this.mediaPages[a2].emptyImageView.setImageResource(R.drawable.tip2);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", R.string.NoSharedFilesSecret));
                } else {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoSharedFiles", R.string.NoSharedFiles));
                }
            } else if (this.mediaPages[a2].selectedType == 2) {
                if (currentAdapter != this.voiceAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.voiceAdapter);
                }
                this.mediaPages[a2].emptyImageView.setImageResource(R.drawable.tip5);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoSharedVoiceSecret", R.string.NoSharedVoiceSecret));
                } else {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoSharedVoice", R.string.NoSharedVoice));
                }
            } else if (this.mediaPages[a2].selectedType == 3) {
                if (currentAdapter != this.linksAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.linksAdapter);
                }
                this.mediaPages[a2].emptyImageView.setImageResource(R.drawable.tip3);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", R.string.NoSharedLinksSecret));
                } else {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoSharedLinks", R.string.NoSharedLinks));
                }
            } else if (this.mediaPages[a2].selectedType == 4) {
                if (currentAdapter != this.audioAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.audioAdapter);
                }
                this.mediaPages[a2].emptyImageView.setImageResource(R.drawable.tip4);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", R.string.NoSharedAudioSecret));
                } else {
                    this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoSharedAudio", R.string.NoSharedAudio));
                }
            }
            this.mediaPages[a2].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            if (this.mediaPages[a2].selectedType == 0 || this.mediaPages[a2].selectedType == 2) {
                if (animated) {
                    this.searchItemState = 2;
                } else {
                    this.searchItemState = 0;
                    this.searchItem.setVisibility(4);
                }
            } else if (animated) {
                if (this.searchItem.getVisibility() != 4 || this.actionBar.isSearchFieldVisible()) {
                    this.searchItemState = 0;
                } else {
                    this.searchItemState = 1;
                    this.searchItem.setVisibility(0);
                    this.searchItem.setAlpha(0.0f);
                }
            } else if (this.searchItem.getVisibility() == 4) {
                this.searchItemState = 0;
                this.searchItem.setAlpha(1.0f);
                this.searchItem.setVisibility(0);
            }
            if (!this.sharedMediaData[this.mediaPages[a2].selectedType].loading && !this.sharedMediaData[this.mediaPages[a2].selectedType].endReached[0] && this.sharedMediaData[this.mediaPages[a2].selectedType].messages.isEmpty()) {
                boolean unused = this.sharedMediaData[this.mediaPages[a2].selectedType].loading = true;
                MediaDataController.getInstance(this.currentAccount).loadMedia(this.dialog_id, 50, 0, this.mediaPages[a2].selectedType, 1, this.classGuid);
            }
            if (!this.sharedMediaData[this.mediaPages[a2].selectedType].loading || !this.sharedMediaData[this.mediaPages[a2].selectedType].messages.isEmpty()) {
                this.mediaPages[a2].progressView.setVisibility(8);
                this.mediaPages[a2].listView.setEmptyView(this.mediaPages[a2].emptyView);
            } else {
                this.mediaPages[a2].progressView.setVisibility(0);
                this.mediaPages[a2].listView.setEmptyView((View) null);
                this.mediaPages[a2].emptyView.setVisibility(8);
            }
            this.mediaPages[a2].listView.setVisibility(0);
        } else if (!animated) {
            if (this.mediaPages[a2].listView != null) {
                if (this.mediaPages[a2].selectedType == 1) {
                    if (currentAdapter != this.documentsSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a2].listView.setAdapter(this.documentsSearchAdapter);
                    }
                    this.documentsSearchAdapter.notifyDataSetChanged();
                } else if (this.mediaPages[a2].selectedType == 3) {
                    if (currentAdapter != this.linksSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a2].listView.setAdapter(this.linksSearchAdapter);
                    }
                    this.linksSearchAdapter.notifyDataSetChanged();
                } else if (this.mediaPages[a2].selectedType == 4) {
                    if (currentAdapter != this.audioSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a2].listView.setAdapter(this.audioSearchAdapter);
                    }
                    this.audioSearchAdapter.notifyDataSetChanged();
                }
            }
            if (!(this.searchItemState == 2 || this.mediaPages[a2].emptyTextView == null)) {
                this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                this.mediaPages[a2].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(30.0f));
                this.mediaPages[a2].emptyTextView.setTextSize(1, 20.0f);
                this.mediaPages[a2].emptyImageView.setVisibility(8);
            }
        } else if (this.mediaPages[a2].selectedType == 0 || this.mediaPages[a2].selectedType == 2) {
            this.searching = false;
            this.searchWas = false;
            switchToCurrentSelectedMode(true);
            return;
        } else {
            String text = this.searchItem.getSearchField().getText().toString();
            if (this.mediaPages[a2].selectedType == 1) {
                MediaSearchAdapter mediaSearchAdapter2 = this.documentsSearchAdapter;
                if (mediaSearchAdapter2 != null) {
                    mediaSearchAdapter2.search(text);
                    if (currentAdapter != this.documentsSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a2].listView.setAdapter(this.documentsSearchAdapter);
                    }
                }
            } else if (this.mediaPages[a2].selectedType == 3) {
                MediaSearchAdapter mediaSearchAdapter3 = this.linksSearchAdapter;
                if (mediaSearchAdapter3 != null) {
                    mediaSearchAdapter3.search(text);
                    if (currentAdapter != this.linksSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a2].listView.setAdapter(this.linksSearchAdapter);
                    }
                }
            } else if (this.mediaPages[a2].selectedType == 4 && (mediaSearchAdapter = this.audioSearchAdapter) != null) {
                mediaSearchAdapter.search(text);
                if (currentAdapter != this.audioSearchAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.audioSearchAdapter);
                }
            }
            if (!(this.searchItemState == 2 || this.mediaPages[a2].emptyTextView == null)) {
                this.mediaPages[a2].emptyTextView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                this.mediaPages[a2].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(30.0f));
                this.mediaPages[a2].emptyTextView.setTextSize(1, 20.0f);
                this.mediaPages[a2].emptyImageView.setVisibility(8);
            }
        }
        if (this.searchItemState == 2 && this.actionBar.isSearchFieldVisible()) {
            this.ignoreSearchCollapse = true;
            this.actionBar.closeSearchField();
        }
        if (this.actionBar.getTranslationY() != 0.0f) {
            this.mediaPages[a2].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(MessageObject item, View view, int a) {
        MessageObject messageObject = item;
        View view2 = view;
        if (this.actionBar.isActionModeShowed()) {
            int i = a;
        } else if (getParentActivity() == null) {
            int i2 = a;
        } else {
            AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
            this.selectedFiles[item.getDialogId() == this.dialog_id ? (char) 0 : 1].put(item.getId(), messageObject);
            if (!messageObject.canDeleteMessage(false, (TLRPC.Chat) null)) {
                this.cantDeleteMessagesCount++;
            }
            this.actionBar.createActionMode().getItem(4).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
            ActionBarMenuItem actionBarMenuItem = this.gotoItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(0);
            }
            this.selectedMessagesCountTextView.setNumber(1, false);
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            for (int i3 = 0; i3 < this.actionModeViews.size(); i3++) {
                View view22 = this.actionModeViews.get(i3);
                AndroidUtilities.clearDrawableAnimation(view22);
                animators.add(ObjectAnimator.ofFloat(view22, View.SCALE_Y, new float[]{0.1f, 1.0f}));
            }
            animatorSet.playTogether(animators);
            animatorSet.setDuration(250);
            animatorSet.start();
            this.scrolling = false;
            if (view2 instanceof SharedDocumentCell) {
                ((SharedDocumentCell) view2).setChecked(true, true);
                int i4 = a;
            } else if (view2 instanceof SharedPhotoVideoCell) {
                ((SharedPhotoVideoCell) view2).setChecked(a, true, true);
            } else {
                int i5 = a;
                if (view2 instanceof SharedLinkCell) {
                    ((SharedLinkCell) view2).setChecked(true, true);
                } else if (view2 instanceof SharedAudioCell) {
                    ((SharedAudioCell) view2).setChecked(true, true);
                }
            }
            if (!this.actionBar.isActionModeShowed()) {
                this.actionBar.showActionMode((View) null, this.actionModeBackground, (View[]) null, (boolean[]) null, (View) null, 0);
                resetScroll();
            }
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void onItemClick(int index, View view, MessageObject message, int a, int selectedMode) {
        View view2 = view;
        MessageObject messageObject = message;
        int i = selectedMode;
        if (messageObject != null) {
            boolean z = false;
            if (this.actionBar.isActionModeShowed()) {
                int loadIndex = message.getDialogId() == this.dialog_id ? 0 : 1;
                if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                    this.selectedFiles[loadIndex].remove(message.getId());
                    if (!messageObject.canDeleteMessage(false, (TLRPC.Chat) null)) {
                        this.cantDeleteMessagesCount--;
                    }
                } else if (this.selectedFiles[0].size() + this.selectedFiles[1].size() < 100) {
                    this.selectedFiles[loadIndex].put(message.getId(), messageObject);
                    if (!messageObject.canDeleteMessage(false, (TLRPC.Chat) null)) {
                        this.cantDeleteMessagesCount++;
                    }
                } else {
                    return;
                }
                if (this.selectedFiles[0].size() == 0 && this.selectedFiles[1].size() == 0) {
                    this.actionBar.hideActionMode();
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
                    int i2 = 8;
                    this.actionBar.createActionMode().getItem(4).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                    ActionBarMenuItem actionBarMenuItem = this.gotoItem;
                    if (actionBarMenuItem != null) {
                        if (this.selectedFiles[0].size() == 1) {
                            i2 = 0;
                        }
                        actionBarMenuItem.setVisibility(i2);
                    }
                }
                this.scrolling = false;
                if (view2 instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view2;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, true);
                    int i3 = a;
                } else if (view2 instanceof SharedPhotoVideoCell) {
                    SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) view2;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedPhotoVideoCell.setChecked(a, z, true);
                } else {
                    int i4 = a;
                    if (view2 instanceof SharedLinkCell) {
                        SharedLinkCell sharedLinkCell = (SharedLinkCell) view2;
                        if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                            z = true;
                        }
                        sharedLinkCell.setChecked(z, true);
                    } else if (view2 instanceof SharedAudioCell) {
                        SharedAudioCell sharedAudioCell = (SharedAudioCell) view2;
                        if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                            z = true;
                        }
                        sharedAudioCell.setChecked(z, true);
                    }
                }
            } else {
                int i5 = a;
                if (i == 0) {
                    PhotoViewer.getInstance().setParentActivity(getParentActivity());
                    PhotoViewer.getInstance().openPhoto((ArrayList<MessageObject>) this.sharedMediaData[i].messages, index, this.dialog_id, this.mergeDialogId, this.provider);
                } else if (i == 2 || i == 4) {
                    if (view2 instanceof SharedAudioCell) {
                        ((SharedAudioCell) view2).didPressedButton();
                    }
                } else if (i == 1) {
                    if (view2 instanceof SharedDocumentCell) {
                        SharedDocumentCell cell = (SharedDocumentCell) view2;
                        TLRPC.Document document = message.getDocument();
                        if (cell.isLoaded()) {
                            if (message.canPreviewDocument()) {
                                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                                int index2 = this.sharedMediaData[i].messages.indexOf(messageObject);
                                if (index2 < 0) {
                                    ArrayList<MessageObject> documents = new ArrayList<>();
                                    documents.add(messageObject);
                                    PhotoViewer.getInstance().openPhoto(documents, 0, 0, 0, this.provider);
                                    return;
                                }
                                PhotoViewer.getInstance().openPhoto((ArrayList<MessageObject>) this.sharedMediaData[i].messages, index2, this.dialog_id, this.mergeDialogId, this.provider);
                                return;
                            }
                            AndroidUtilities.openDocument(messageObject, getParentActivity(), this);
                        } else if (!cell.isLoading()) {
                            FileLoader.getInstance(this.currentAccount).loadFile(document, cell.getMessage(), 0, 0);
                            cell.updateFileExistIcon();
                        } else {
                            FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
                            cell.updateFileExistIcon();
                        }
                    }
                } else if (i == 3) {
                    try {
                        TLRPC.WebPage webPage = messageObject.messageOwner.media.webpage;
                        String link = null;
                        if (webPage != null && !(webPage instanceof TLRPC.TL_webPageEmpty)) {
                            if (webPage.cached_page != null) {
                                ArticleViewer.getInstance().setParentActivity(getParentActivity(), this);
                                ArticleViewer.getInstance().open(messageObject);
                                return;
                            } else if (webPage.embed_url == null || webPage.embed_url.length() == 0) {
                                link = webPage.url;
                            } else {
                                openWebView(webPage);
                                return;
                            }
                        }
                        if (link == null) {
                            link = ((SharedLinkCell) view2).getLink(0);
                        }
                        if (link != null) {
                            Browser.openUrl((Context) getParentActivity(), link);
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void openWebView(TLRPC.WebPage webPage) {
        EmbedBottomSheet.show(getParentActivity(), webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height);
    }

    private void recycleAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof SharedPhotoVideoAdapter) {
            this.cellCache.addAll(this.cache);
            this.cache.clear();
        } else if (adapter == this.audioAdapter) {
            this.audioCellCache.addAll(this.audioCache);
            this.audioCache.clear();
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal(int num) {
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (num == 0) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
        if (AndroidUtilities.isTablet()) {
            this.columnsCount = 4;
            this.mediaPages[num].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        } else if (rotation == 3 || rotation == 1) {
            this.columnsCount = 6;
            this.mediaPages[num].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        } else {
            this.columnsCount = 4;
            this.mediaPages[num].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        }
        if (num == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
    }

    private class SharedLinksAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isEnabled(int section, int row) {
            return row != 0;
        }

        public int getSectionCount() {
            int size = Media1Activity.this.sharedMediaData[3].sections.size();
            int i = 1;
            if (Media1Activity.this.sharedMediaData[3].sections.isEmpty() || (Media1Activity.this.sharedMediaData[3].endReached[0] && Media1Activity.this.sharedMediaData[3].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        public int getCountForSection(int section) {
            if (section < Media1Activity.this.sharedMediaData[3].sections.size()) {
                return ((ArrayList) Media1Activity.this.sharedMediaData[3].sectionArrays.get(Media1Activity.this.sharedMediaData[3].sections.get(section))).size() + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_graySection) & -218103809);
            }
            if (section < Media1Activity.this.sharedMediaData[3].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((ArrayList) Media1Activity.this.sharedMediaData[3].sectionArrays.get((String) Media1Activity.this.sharedMediaData[3].sections.get(section))).get(0).messageOwner.date));
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new GraySectionCell(this.mContext);
            } else if (viewType != 1) {
                view = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(54.0f));
            } else {
                view = new SharedLinkCell(this.mContext);
                ((SharedLinkCell) view).setDelegate(Media1Activity.this.sharedLinkCellDelegate);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = (ArrayList) Media1Activity.this.sharedMediaData[3].sectionArrays.get((String) Media1Activity.this.sharedMediaData[3].sections.get(section));
                int itemViewType = holder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    ((GraySectionCell) holder.itemView).setText(LocaleController.formatSectionDate((long) messageObjects.get(0).messageOwner.date));
                } else if (itemViewType == 1) {
                    SharedLinkCell sharedLinkCell = (SharedLinkCell) holder.itemView;
                    MessageObject messageObject = messageObjects.get(position - 1);
                    sharedLinkCell.setLink(messageObject, position != messageObjects.size() || (section == Media1Activity.this.sharedMediaData[3].sections.size() - 1 && Media1Activity.this.sharedMediaData[3].loading));
                    if (Media1Activity.this.actionBar.isActionModeShowed()) {
                        if (Media1Activity.this.selectedFiles[messageObject.getDialogId() == Media1Activity.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedLinkCell.setChecked(z, !Media1Activity.this.scrolling);
                        return;
                    }
                    sharedLinkCell.setChecked(false, !Media1Activity.this.scrolling);
                }
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= Media1Activity.this.sharedMediaData[3].sections.size()) {
                return 2;
            }
            if (position == 0) {
                return 0;
            }
            return 1;
        }

        public String getLetter(int position) {
            return null;
        }

        public int getPositionForScrollProgress(float progress) {
            return 0;
        }
    }

    private class SharedDocumentsAdapter extends RecyclerListView.SectionsAdapter {
        /* access modifiers changed from: private */
        public int currentType;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int type) {
            this.mContext = context;
            this.currentType = type;
        }

        public boolean isEnabled(int section, int row) {
            return row != 0;
        }

        public int getSectionCount() {
            int size = Media1Activity.this.sharedMediaData[this.currentType].sections.size();
            int i = 1;
            if (Media1Activity.this.sharedMediaData[this.currentType].sections.isEmpty() || (Media1Activity.this.sharedMediaData[this.currentType].endReached[0] && Media1Activity.this.sharedMediaData[this.currentType].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public int getCountForSection(int section) {
            if (section < Media1Activity.this.sharedMediaData[this.currentType].sections.size()) {
                return ((ArrayList) Media1Activity.this.sharedMediaData[this.currentType].sectionArrays.get(Media1Activity.this.sharedMediaData[this.currentType].sections.get(section))).size() + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_graySection) & -218103809);
            }
            if (section < Media1Activity.this.sharedMediaData[this.currentType].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((ArrayList) Media1Activity.this.sharedMediaData[this.currentType].sectionArrays.get((String) Media1Activity.this.sharedMediaData[this.currentType].sections.get(section))).get(0).messageOwner.date));
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new GraySectionCell(this.mContext);
            } else if (viewType == 1) {
                view = new SharedDocumentCell(this.mContext);
            } else if (viewType != 2) {
                if (this.currentType != 4 || Media1Activity.this.audioCellCache.isEmpty()) {
                    view = new SharedAudioCell(this.mContext) {
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                boolean result = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(result ? Media1Activity.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages : null, false);
                                return result;
                            } else if (messageObject.isMusic()) {
                                return MediaController.getInstance().setPlaylist(Media1Activity.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages, messageObject);
                            } else {
                                return false;
                            }
                        }
                    };
                } else {
                    view = (View) Media1Activity.this.audioCellCache.get(0);
                    Media1Activity.this.audioCellCache.remove(0);
                    ViewGroup p = (ViewGroup) view.getParent();
                    if (p != null) {
                        p.removeView(view);
                    }
                }
                if (this.currentType == 4) {
                    Media1Activity.this.audioCache.add((SharedAudioCell) view);
                }
            } else {
                view = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(54.0f));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = (ArrayList) Media1Activity.this.sharedMediaData[this.currentType].sectionArrays.get((String) Media1Activity.this.sharedMediaData[this.currentType].sections.get(section));
                int itemViewType = holder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    ((GraySectionCell) holder.itemView).setText(LocaleController.formatSectionDate((long) messageObjects.get(0).messageOwner.date));
                } else if (itemViewType == 1) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) holder.itemView;
                    MessageObject messageObject = messageObjects.get(position - 1);
                    sharedDocumentCell.setDocument(messageObject, position != messageObjects.size() || (section == Media1Activity.this.sharedMediaData[this.currentType].sections.size() - 1 && Media1Activity.this.sharedMediaData[this.currentType].loading));
                    if (Media1Activity.this.actionBar.isActionModeShowed()) {
                        if (Media1Activity.this.selectedFiles[messageObject.getDialogId() == Media1Activity.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(z, true ^ Media1Activity.this.scrolling);
                        return;
                    }
                    sharedDocumentCell.setChecked(false, true ^ Media1Activity.this.scrolling);
                } else if (itemViewType == 3) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                    MessageObject messageObject2 = messageObjects.get(position - 1);
                    sharedAudioCell.setMessageObject(messageObject2, position != messageObjects.size() || (section == Media1Activity.this.sharedMediaData[this.currentType].sections.size() - 1 && Media1Activity.this.sharedMediaData[this.currentType].loading));
                    if (Media1Activity.this.actionBar.isActionModeShowed()) {
                        if (Media1Activity.this.selectedFiles[messageObject2.getDialogId() == Media1Activity.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject2.getId()) >= 0) {
                            z = true;
                        }
                        sharedAudioCell.setChecked(z, true ^ Media1Activity.this.scrolling);
                        return;
                    }
                    sharedAudioCell.setChecked(false, true ^ Media1Activity.this.scrolling);
                }
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= Media1Activity.this.sharedMediaData[this.currentType].sections.size()) {
                return 2;
            }
            if (position == 0) {
                return 0;
            }
            int i = this.currentType;
            if (i == 2 || i == 4) {
                return 3;
            }
            return 1;
        }

        public String getLetter(int position) {
            return null;
        }

        public int getPositionForScrollProgress(float progress) {
            return 0;
        }
    }

    private class SharedPhotoVideoAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isEnabled(int section, int row) {
            return false;
        }

        public int getSectionCount() {
            int i = 0;
            int size = Media1Activity.this.sharedMediaData[0].sections.size();
            if (!Media1Activity.this.sharedMediaData[0].sections.isEmpty() && (!Media1Activity.this.sharedMediaData[0].endReached[0] || !Media1Activity.this.sharedMediaData[0].endReached[1])) {
                i = 1;
            }
            return size + i;
        }

        public int getCountForSection(int section) {
            if (section < Media1Activity.this.sharedMediaData[0].sections.size()) {
                return ((int) Math.ceil((double) (((float) ((ArrayList) Media1Activity.this.sharedMediaData[0].sectionArrays.get(Media1Activity.this.sharedMediaData[0].sections.get(section))).size()) / ((float) Media1Activity.this.columnsCount)))) + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new SharedMediaSectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite) & -436207617);
            }
            if (section < Media1Activity.this.sharedMediaData[0].sections.size()) {
                ((SharedMediaSectionCell) view).setText(LocaleController.formatSectionDate((long) ((ArrayList) Media1Activity.this.sharedMediaData[0].sectionArrays.get((String) Media1Activity.this.sharedMediaData[0].sections.get(section))).get(0).messageOwner.date));
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new SharedMediaSectionCell(this.mContext);
            } else if (viewType != 1) {
                view = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(74.0f));
            } else {
                if (!Media1Activity.this.cellCache.isEmpty()) {
                    view = (View) Media1Activity.this.cellCache.get(0);
                    Media1Activity.this.cellCache.remove(0);
                    ViewGroup p = (ViewGroup) view.getParent();
                    if (p != null) {
                        p.removeView(view);
                    }
                } else {
                    view = new SharedPhotoVideoCell(this.mContext);
                }
                ((SharedPhotoVideoCell) view).setDelegate(new SharedPhotoVideoCell.SharedPhotoVideoCellDelegate() {
                    public void didClickItem(SharedPhotoVideoCell cell, int index, MessageObject messageObject, int a) {
                        Media1Activity.this.onItemClick(index, cell, messageObject, a, 0);
                    }

                    public boolean didLongClickItem(SharedPhotoVideoCell cell, int index, MessageObject messageObject, int a) {
                        if (!Media1Activity.this.actionBar.isActionModeShowed()) {
                            return Media1Activity.this.onItemLongClick(messageObject, cell, a);
                        }
                        didClickItem(cell, index, messageObject, a);
                        return true;
                    }
                });
                Media1Activity.this.cache.add((SharedPhotoVideoCell) view);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            int i = position;
            RecyclerView.ViewHolder viewHolder = holder;
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = (ArrayList) Media1Activity.this.sharedMediaData[0].sectionArrays.get((String) Media1Activity.this.sharedMediaData[0].sections.get(section));
                int itemViewType = holder.getItemViewType();
                if (itemViewType == 0) {
                    ((SharedMediaSectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate((long) messageObjects.get(0).messageOwner.date));
                } else if (itemViewType == 1) {
                    SharedPhotoVideoCell cell = (SharedPhotoVideoCell) viewHolder.itemView;
                    cell.setItemsCount(Media1Activity.this.columnsCount);
                    cell.setIsFirst(i == 1);
                    for (int a = 0; a < Media1Activity.this.columnsCount; a++) {
                        int index = ((i - 1) * Media1Activity.this.columnsCount) + a;
                        if (index < messageObjects.size()) {
                            MessageObject messageObject = messageObjects.get(index);
                            cell.setItem(a, Media1Activity.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                            if (Media1Activity.this.actionBar.isActionModeShowed()) {
                                cell.setChecked(a, Media1Activity.this.selectedFiles[(messageObject.getDialogId() > Media1Activity.this.dialog_id ? 1 : (messageObject.getDialogId() == Media1Activity.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0, !Media1Activity.this.scrolling);
                            } else {
                                cell.setChecked(a, false, !Media1Activity.this.scrolling);
                            }
                        } else {
                            cell.setItem(a, index, (MessageObject) null);
                        }
                    }
                    cell.requestLayout();
                }
            } else {
                int i2 = section;
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= Media1Activity.this.sharedMediaData[0].sections.size()) {
                return 2;
            }
            if (position == 0) {
                return 0;
            }
            return 1;
        }

        public String getLetter(int position) {
            return null;
        }

        public int getPositionForScrollProgress(float progress) {
            return 0;
        }
    }

    public class MediaSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int currentType;
        protected ArrayList<MessageObject> globalSearch = new ArrayList<>();
        private int lastReqId;
        private Context mContext;
        private int reqId = 0;
        /* access modifiers changed from: private */
        public ArrayList<MessageObject> searchResult = new ArrayList<>();
        private Runnable searchRunnable;
        private int searchesInProgress;

        public MediaSearchAdapter(Context context, int type) {
            this.mContext = context;
            this.currentType = type;
        }

        public void queryServerSearch(String query, int max_id, long did) {
            int uid = (int) did;
            if (uid != 0) {
                if (this.reqId != 0) {
                    ConnectionsManager.getInstance(Media1Activity.this.currentAccount).cancelRequest(this.reqId, true);
                    this.reqId = 0;
                    this.searchesInProgress--;
                }
                if (query == null || query.length() == 0) {
                    this.globalSearch.clear();
                    this.lastReqId = 0;
                    notifyDataSetChanged();
                    return;
                }
                TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
                req.limit = 50;
                req.offset_id = max_id;
                int i = this.currentType;
                if (i == 1) {
                    req.filter = new TLRPC.TL_inputMessagesFilterDocument();
                } else if (i == 3) {
                    req.filter = new TLRPC.TL_inputMessagesFilterUrl();
                } else if (i == 4) {
                    req.filter = new TLRPC.TL_inputMessagesFilterMusic();
                }
                req.q = query;
                req.peer = MessagesController.getInstance(Media1Activity.this.currentAccount).getInputPeer(uid);
                if (req.peer != null) {
                    int currentReqId = this.lastReqId + 1;
                    this.lastReqId = currentReqId;
                    this.searchesInProgress++;
                    this.reqId = ConnectionsManager.getInstance(Media1Activity.this.currentAccount).sendRequest(req, new RequestDelegate(max_id, currentReqId) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            Media1Activity.MediaSearchAdapter.this.lambda$queryServerSearch$1$Media1Activity$MediaSearchAdapter(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    }, 2);
                    ConnectionsManager.getInstance(Media1Activity.this.currentAccount).bindRequestToGuid(this.reqId, Media1Activity.this.classGuid);
                }
            }
        }

        public /* synthetic */ void lambda$queryServerSearch$1$Media1Activity$MediaSearchAdapter(int max_id, int currentReqId, TLObject response, TLRPC.TL_error error) {
            ArrayList<MessageObject> messageObjects = new ArrayList<>();
            if (error == null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                for (int a = 0; a < res.messages.size(); a++) {
                    TLRPC.Message message = res.messages.get(a);
                    if (max_id == 0 || message.id <= max_id) {
                        messageObjects.add(new MessageObject(Media1Activity.this.currentAccount, message, false));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable(currentReqId, messageObjects) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    Media1Activity.MediaSearchAdapter.this.lambda$null$0$Media1Activity$MediaSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$Media1Activity$MediaSearchAdapter(int currentReqId, ArrayList messageObjects) {
            if (this.reqId != 0) {
                if (currentReqId == this.lastReqId) {
                    this.globalSearch = messageObjects;
                    this.searchesInProgress--;
                    int count = getItemCount();
                    notifyDataSetChanged();
                    int a = 0;
                    while (true) {
                        if (a < Media1Activity.this.mediaPages.length) {
                            if (Media1Activity.this.mediaPages[a].listView.getAdapter() == this && count == 0 && Media1Activity.this.actionBar.getTranslationY() != 0.0f) {
                                Media1Activity.this.mediaPages[a].layoutManager.scrollToPositionWithOffset(0, (int) Media1Activity.this.actionBar.getTranslationY());
                                break;
                            }
                            a++;
                        } else {
                            break;
                        }
                    }
                }
                this.reqId = 0;
            }
        }

        public void search(String query) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty() || this.searchesInProgress != 0) {
                    this.searchResult.clear();
                    this.globalSearch.clear();
                    if (this.reqId != 0) {
                        ConnectionsManager.getInstance(Media1Activity.this.currentAccount).cancelRequest(this.reqId, true);
                        this.reqId = 0;
                        this.searchesInProgress--;
                    }
                }
                notifyDataSetChanged();
                return;
            }
            for (int a = 0; a < Media1Activity.this.mediaPages.length; a++) {
                if (Media1Activity.this.mediaPages[a].selectedType == this.currentType) {
                    if (getItemCount() != 0) {
                        Media1Activity.this.mediaPages[a].listView.setEmptyView(Media1Activity.this.mediaPages[a].emptyView);
                        Media1Activity.this.mediaPages[a].progressView.setVisibility(8);
                    } else {
                        Media1Activity.this.mediaPages[a].listView.setEmptyView((View) null);
                        Media1Activity.this.mediaPages[a].emptyView.setVisibility(8);
                        Media1Activity.this.mediaPages[a].progressView.setVisibility(0);
                    }
                }
            }
            $$Lambda$Media1Activity$MediaSearchAdapter$GjZWlgQNPdpi6RtL9ZNJVdQwq1U r0 = new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    Media1Activity.MediaSearchAdapter.this.lambda$search$3$Media1Activity$MediaSearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 300);
        }

        public /* synthetic */ void lambda$search$3$Media1Activity$MediaSearchAdapter(String query) {
            int i;
            if (!Media1Activity.this.sharedMediaData[this.currentType].messages.isEmpty() && ((i = this.currentType) == 1 || i == 4)) {
                MessageObject messageObject = (MessageObject) Media1Activity.this.sharedMediaData[this.currentType].messages.get(Media1Activity.this.sharedMediaData[this.currentType].messages.size() - 1);
                queryServerSearch(query, messageObject.getId(), messageObject.getDialogId());
            } else if (this.currentType == 3) {
                queryServerSearch(query, 0, Media1Activity.this.dialog_id);
            }
            int i2 = this.currentType;
            if (i2 == 1 || i2 == 4) {
                ArrayList<MessageObject> copy = new ArrayList<>(Media1Activity.this.sharedMediaData[this.currentType].messages);
                this.searchesInProgress++;
                Utilities.searchQueue.postRunnable(new Runnable(query, copy) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ ArrayList f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        Media1Activity.MediaSearchAdapter.this.lambda$null$2$Media1Activity$MediaSearchAdapter(this.f$1, this.f$2);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$null$2$Media1Activity$MediaSearchAdapter(String query, ArrayList copy) {
            TLRPC.Document document;
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList());
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<MessageObject> resultArray = new ArrayList<>();
            for (int a = 0; a < copy.size(); a++) {
                MessageObject messageObject = (MessageObject) copy.get(a);
                int b = 0;
                while (true) {
                    if (b >= search.length) {
                        break;
                    }
                    String q = search[b];
                    String name = messageObject.getDocumentName();
                    if (!(name == null || name.length() == 0)) {
                        if (name.toLowerCase().contains(q)) {
                            resultArray.add(messageObject);
                            break;
                        } else if (this.currentType != 4) {
                            continue;
                        } else {
                            if (messageObject.type == 0) {
                                document = messageObject.messageOwner.media.webpage.document;
                            } else {
                                document = messageObject.messageOwner.media.document;
                            }
                            boolean ok = false;
                            int c = 0;
                            while (true) {
                                if (c >= document.attributes.size()) {
                                    break;
                                }
                                TLRPC.DocumentAttribute attribute = document.attributes.get(c);
                                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                                    if (attribute.performer != null) {
                                        ok = attribute.performer.toLowerCase().contains(q);
                                    }
                                    if (!ok && attribute.title != null) {
                                        ok = attribute.title.toLowerCase().contains(q);
                                    }
                                } else {
                                    c++;
                                }
                            }
                            if (ok) {
                                resultArray.add(messageObject);
                                break;
                            }
                        }
                    }
                    b++;
                }
            }
            ArrayList arrayList = copy;
            updateSearchResults(resultArray);
        }

        private void updateSearchResults(ArrayList<MessageObject> documents) {
            AndroidUtilities.runOnUIThread(new Runnable(documents) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    Media1Activity.MediaSearchAdapter.this.lambda$updateSearchResults$4$Media1Activity$MediaSearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$4$Media1Activity$MediaSearchAdapter(ArrayList documents) {
            this.searchesInProgress--;
            this.searchResult = documents;
            int count = getItemCount();
            notifyDataSetChanged();
            for (int a = 0; a < Media1Activity.this.mediaPages.length; a++) {
                if (Media1Activity.this.mediaPages[a].listView.getAdapter() == this && count == 0 && Media1Activity.this.actionBar.getTranslationY() != 0.0f) {
                    Media1Activity.this.mediaPages[a].layoutManager.scrollToPositionWithOffset(0, (int) Media1Activity.this.actionBar.getTranslationY());
                    return;
                }
            }
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this.searchesInProgress == 0) {
                for (int a = 0; a < Media1Activity.this.mediaPages.length; a++) {
                    if (Media1Activity.this.mediaPages[a].selectedType == this.currentType) {
                        Media1Activity.this.mediaPages[a].listView.setEmptyView(Media1Activity.this.mediaPages[a].emptyView);
                        Media1Activity.this.mediaPages[a].progressView.setVisibility(8);
                    }
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != this.searchResult.size() + this.globalSearch.size();
        }

        public int getItemCount() {
            int count = this.searchResult.size();
            int globalCount = this.globalSearch.size();
            if (globalCount != 0) {
                return count + globalCount;
            }
            return count;
        }

        public boolean isGlobalSearch(int i) {
            int localCount = this.searchResult.size();
            int globalCount = this.globalSearch.size();
            if ((i < 0 || i >= localCount) && i > localCount && i <= globalCount + localCount) {
                return true;
            }
            return false;
        }

        public MessageObject getItem(int i) {
            if (i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            return this.globalSearch.get(i - this.searchResult.size());
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int i = this.currentType;
            if (i == 1) {
                view = new SharedDocumentCell(this.mContext);
            } else if (i == 4) {
                view = new SharedAudioCell(this.mContext) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? MediaSearchAdapter.this.searchResult : null, false);
                            if (messageObject.isRoundVideo()) {
                                MediaController.getInstance().setCurrentVideoVisible(false);
                            }
                            return result;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(MediaSearchAdapter.this.searchResult, messageObject);
                        } else {
                            return false;
                        }
                    }
                };
            } else {
                view = new SharedLinkCell(this.mContext);
                ((SharedLinkCell) view).setDelegate(Media1Activity.this.sharedLinkCellDelegate);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int i = this.currentType;
            boolean z = false;
            if (i == 1) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) holder.itemView;
                MessageObject messageObject = getItem(position);
                sharedDocumentCell.setDocument(messageObject, position != getItemCount() - 1);
                if (Media1Activity.this.actionBar.isActionModeShowed()) {
                    if (Media1Activity.this.selectedFiles[messageObject.getDialogId() == Media1Activity.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, true ^ Media1Activity.this.scrolling);
                    return;
                }
                sharedDocumentCell.setChecked(false, true ^ Media1Activity.this.scrolling);
            } else if (i == 3) {
                SharedLinkCell sharedLinkCell = (SharedLinkCell) holder.itemView;
                MessageObject messageObject2 = getItem(position);
                sharedLinkCell.setLink(messageObject2, position != getItemCount() - 1);
                if (Media1Activity.this.actionBar.isActionModeShowed()) {
                    if (Media1Activity.this.selectedFiles[messageObject2.getDialogId() == Media1Activity.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject2.getId()) >= 0) {
                        z = true;
                    }
                    sharedLinkCell.setChecked(z, true ^ Media1Activity.this.scrolling);
                    return;
                }
                sharedLinkCell.setChecked(false, true ^ Media1Activity.this.scrolling);
            } else if (i == 4) {
                SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                MessageObject messageObject3 = getItem(position);
                sharedAudioCell.setMessageObject(messageObject3, position != getItemCount() - 1);
                if (Media1Activity.this.actionBar.isActionModeShowed()) {
                    if (Media1Activity.this.selectedFiles[messageObject3.getDialogId() == Media1Activity.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject3.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, true ^ Media1Activity.this.scrolling);
                    return;
                }
                sharedAudioCell.setChecked(false, true ^ Media1Activity.this.scrolling);
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuBackground));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItem));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItemIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionModeBackground, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_actionMode));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerBackground));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, 0, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPlayPause));
        FragmentContextView fragmentContextView2 = this.fragmentContextView;
        FragmentContextView fragmentContextView3 = fragmentContextView2;
        arrayList.add(new ThemeDescription((View) fragmentContextView3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerTitle));
        FragmentContextView fragmentContextView4 = this.fragmentContextView;
        FragmentContextView fragmentContextView5 = fragmentContextView4;
        arrayList.add(new ThemeDescription((View) fragmentContextView5, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPerformer));
        FragmentContextView fragmentContextView6 = this.fragmentContextView;
        FragmentContextView fragmentContextView7 = fragmentContextView6;
        arrayList.add(new ThemeDescription((View) fragmentContextView7, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerClose));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarTabActiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarTabUnactiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarTabLine));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarTabSelector));
        for (int a = 0; a < this.mediaPages.length; a++) {
            ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate(a) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSetColor() {
                    Media1Activity.this.lambda$getThemeDescriptions$5$Media1Activity(this.f$1);
                }
            };
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray));
            arrayList.add(new ThemeDescription(this.mediaPages[a].progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[a].progressBar, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_startStopLoadIcon));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_startStopLoadIcon));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIcon));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_iconText));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkSelection));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholderText));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{SharedPhotoVideoCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_photoPlaceholder));
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.pinnedHeaderShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow));
        }
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }

    public /* synthetic */ void lambda$getThemeDescriptions$5$Media1Activity(int num) {
        if (this.mediaPages[num].listView != null) {
            int count = this.mediaPages[num].listView.getChildCount();
            for (int a1 = 0; a1 < count; a1++) {
                View child = this.mediaPages[num].listView.getChildAt(a1);
                if (child instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) child).updateCheckboxColor();
                }
            }
        }
    }
}
