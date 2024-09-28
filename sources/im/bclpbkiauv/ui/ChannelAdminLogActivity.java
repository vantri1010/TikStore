package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerMiddle;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BackDrawable;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.ChatActionCell;
import im.bclpbkiauv.ui.cells.ChatLoadingCell;
import im.bclpbkiauv.ui.cells.ChatMessageCell;
import im.bclpbkiauv.ui.cells.ChatUnreadCell;
import im.bclpbkiauv.ui.components.AdminLogFilterAlert;
import im.bclpbkiauv.ui.components.ChatAvatarContainer;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.PipRoundVideoView;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ChannelAdminLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<TLRPC.ChannelParticipant> admins;
    /* access modifiers changed from: private */
    public Paint aspectPaint;
    /* access modifiers changed from: private */
    public Path aspectPath;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout aspectRatioFrameLayout;
    /* access modifiers changed from: private */
    public ChatAvatarContainer avatarContainer;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private ImageView bottomOverlayImage;
    private ChatActivityAdapter chatAdapter;
    /* access modifiers changed from: private */
    public LinearLayoutManager chatLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView chatListView;
    /* access modifiers changed from: private */
    public ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean checkTextureViewPosition;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout contentView;
    protected TLRPC.Chat currentChat;
    private TLRPC.TL_channelAdminLogEventsFilter currentFilter = null;
    private boolean currentFloatingDateOnScreen;
    /* access modifiers changed from: private */
    public boolean currentFloatingTopIsNotMessage;
    private TextView emptyView;
    /* access modifiers changed from: private */
    public FrameLayout emptyViewContainer;
    /* access modifiers changed from: private */
    public boolean endReached;
    /* access modifiers changed from: private */
    public AnimatorSet floatingDateAnimation;
    /* access modifiers changed from: private */
    public ChatActionCell floatingDateView;
    private boolean loading;
    /* access modifiers changed from: private */
    public int loadsCount;
    protected ArrayList<MessageObject> messages = new ArrayList<>();
    private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap<>();
    private LongSparseArray<MessageObject> messagesDict = new LongSparseArray<>();
    private int[] mid = {2};
    private int minDate;
    private long minEventId;
    private boolean openAnimationEnded;
    private boolean paused = true;
    private RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public FrameLayout progressView;
    private View progressView2;
    /* access modifiers changed from: private */
    public PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x003d, code lost:
            r6 = (im.bclpbkiauv.ui.cells.ChatActionCell) r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:5:0x0021, code lost:
            r6 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r5;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public im.bclpbkiauv.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(im.bclpbkiauv.messenger.MessageObject r16, im.bclpbkiauv.tgnet.TLRPC.FileLocation r17, int r18, boolean r19) {
            /*
                r15 = this;
                r0 = r15
                r1 = r17
                im.bclpbkiauv.ui.ChannelAdminLogActivity r2 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                im.bclpbkiauv.ui.components.RecyclerListView r2 = r2.chatListView
                int r2 = r2.getChildCount()
                r3 = 0
            L_0x000e:
                if (r3 >= r2) goto L_0x00c6
                r4 = 0
                im.bclpbkiauv.ui.ChannelAdminLogActivity r5 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                im.bclpbkiauv.ui.components.RecyclerListView r5 = r5.chatListView
                android.view.View r5 = r5.getChildAt(r3)
                boolean r6 = r5 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
                if (r6 == 0) goto L_0x0039
                if (r16 == 0) goto L_0x0088
                r6 = r5
                im.bclpbkiauv.ui.cells.ChatMessageCell r6 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r6
                im.bclpbkiauv.messenger.MessageObject r7 = r6.getMessageObject()
                if (r7 == 0) goto L_0x0038
                int r8 = r7.getId()
                int r9 = r16.getId()
                if (r8 != r9) goto L_0x0038
                im.bclpbkiauv.messenger.ImageReceiver r4 = r6.getPhotoImage()
            L_0x0038:
                goto L_0x0088
            L_0x0039:
                boolean r6 = r5 instanceof im.bclpbkiauv.ui.cells.ChatActionCell
                if (r6 == 0) goto L_0x0088
                r6 = r5
                im.bclpbkiauv.ui.cells.ChatActionCell r6 = (im.bclpbkiauv.ui.cells.ChatActionCell) r6
                im.bclpbkiauv.messenger.MessageObject r7 = r6.getMessageObject()
                if (r7 == 0) goto L_0x0088
                if (r16 == 0) goto L_0x0057
                int r8 = r7.getId()
                int r9 = r16.getId()
                if (r8 != r9) goto L_0x0088
                im.bclpbkiauv.messenger.ImageReceiver r4 = r6.getPhotoImage()
                goto L_0x0088
            L_0x0057:
                if (r1 == 0) goto L_0x0088
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r8 = r7.photoThumbs
                if (r8 == 0) goto L_0x0088
                r8 = 0
            L_0x005e:
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r9 = r7.photoThumbs
                int r9 = r9.size()
                if (r8 >= r9) goto L_0x0088
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r9 = r7.photoThumbs
                java.lang.Object r9 = r9.get(r8)
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r9
                im.bclpbkiauv.tgnet.TLRPC$FileLocation r10 = r9.location
                long r10 = r10.volume_id
                long r12 = r1.volume_id
                int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                if (r14 != 0) goto L_0x0085
                im.bclpbkiauv.tgnet.TLRPC$FileLocation r10 = r9.location
                int r10 = r10.local_id
                int r11 = r1.local_id
                if (r10 != r11) goto L_0x0085
                im.bclpbkiauv.messenger.ImageReceiver r4 = r6.getPhotoImage()
                goto L_0x0088
            L_0x0085:
                int r8 = r8 + 1
                goto L_0x005e
            L_0x0088:
                if (r4 == 0) goto L_0x00c2
                r6 = 2
                int[] r6 = new int[r6]
                r5.getLocationInWindow(r6)
                im.bclpbkiauv.ui.PhotoViewer$PlaceProviderObject r7 = new im.bclpbkiauv.ui.PhotoViewer$PlaceProviderObject
                r7.<init>()
                r8 = 0
                r9 = r6[r8]
                r7.viewX = r9
                r9 = 1
                r10 = r6[r9]
                int r11 = android.os.Build.VERSION.SDK_INT
                r12 = 21
                if (r11 < r12) goto L_0x00a4
                goto L_0x00a6
            L_0x00a4:
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.statusBarHeight
            L_0x00a6:
                int r10 = r10 - r8
                r7.viewY = r10
                im.bclpbkiauv.ui.ChannelAdminLogActivity r8 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                im.bclpbkiauv.ui.components.RecyclerListView r8 = r8.chatListView
                r7.parentView = r8
                r7.imageReceiver = r4
                im.bclpbkiauv.messenger.ImageReceiver$BitmapHolder r8 = r4.getBitmapSafe()
                r7.thumb = r8
                int r8 = r4.getRoundRadius()
                r7.radius = r8
                r7.isEvent = r9
                return r7
            L_0x00c2:
                int r3 = r3 + 1
                goto L_0x000e
            L_0x00c6:
                r3 = 0
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChannelAdminLogActivity.AnonymousClass1.getPlaceForPhoto(im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.tgnet.TLRPC$FileLocation, int, boolean):im.bclpbkiauv.ui.PhotoViewer$PlaceProviderObject");
        }
    };
    /* access modifiers changed from: private */
    public FrameLayout roundVideoContainer;
    private MessageObject scrollToMessage;
    /* access modifiers changed from: private */
    public int scrollToOffsetOnRecreate = 0;
    /* access modifiers changed from: private */
    public int scrollToPositionOnRecreate = -1;
    /* access modifiers changed from: private */
    public boolean scrollingFloatingDate;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public String searchQuery = "";
    private ImageView searchUpButton;
    /* access modifiers changed from: private */
    public boolean searchWas;
    private SparseArray<TLRPC.User> selectedAdmins;
    private MessageObject selectedObject;
    private TextureView videoTextureView;
    private boolean wasPaused = false;

    public ChannelAdminLogActivity(TLRPC.Chat chat) {
        this.currentChat = chat;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        loadMessages(true);
        loadAdmins();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
    }

    private void updateEmptyPlaceholder() {
        if (this.emptyView != null) {
            if (!TextUtils.isEmpty(this.searchQuery)) {
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EventLogEmptyTextSearch", R.string.EventLogEmptyTextSearch, this.searchQuery)));
            } else if (this.selectedAdmins == null && this.currentFilter == null) {
                this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                if (this.currentChat.megagroup) {
                    this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmpty", R.string.EventLogEmpty)));
                } else {
                    this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptyChannel", R.string.EventLogEmptyChannel)));
                }
            } else {
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptySearch", R.string.EventLogEmptySearch)));
            }
        }
    }

    /* access modifiers changed from: private */
    public void loadMessages(boolean reset) {
        ChatActivityAdapter chatActivityAdapter;
        if (!this.loading) {
            if (reset) {
                this.minEventId = Long.MAX_VALUE;
                FrameLayout frameLayout = this.progressView;
                if (frameLayout != null) {
                    frameLayout.setVisibility(0);
                    this.emptyViewContainer.setVisibility(4);
                    this.chatListView.setEmptyView((View) null);
                }
                this.messagesDict.clear();
                this.messages.clear();
                this.messagesByDays.clear();
            }
            this.loading = true;
            TLRPC.TL_channels_getAdminLog req = new TLRPC.TL_channels_getAdminLog();
            req.channel = MessagesController.getInputChannel(this.currentChat);
            req.q = this.searchQuery;
            req.limit = 50;
            if (reset || this.messages.isEmpty()) {
                req.max_id = 0;
            } else {
                req.max_id = this.minEventId;
            }
            req.min_id = 0;
            if (this.currentFilter != null) {
                req.flags = 1 | req.flags;
                req.events_filter = this.currentFilter;
            }
            if (this.selectedAdmins != null) {
                req.flags |= 2;
                for (int a = 0; a < this.selectedAdmins.size(); a++) {
                    req.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser(this.selectedAdmins.valueAt(a)));
                }
            }
            updateEmptyPlaceholder();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelAdminLogActivity.this.lambda$loadMessages$1$ChannelAdminLogActivity(tLObject, tL_error);
                }
            });
            if (reset && (chatActivityAdapter = this.chatAdapter) != null) {
                chatActivityAdapter.notifyDataSetChanged();
            }
        }
    }

    public /* synthetic */ void lambda$loadMessages$1$ChannelAdminLogActivity(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_channels_adminLogResults) response) {
                private final /* synthetic */ TLRPC.TL_channels_adminLogResults f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChannelAdminLogActivity.this.lambda$null$0$ChannelAdminLogActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$0$ChannelAdminLogActivity(TLRPC.TL_channels_adminLogResults res) {
        int i = 0;
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        boolean added = false;
        int oldRowsCount = this.messages.size();
        for (int a = 0; a < res.events.size(); a++) {
            TLRPC.TL_channelAdminLogEvent event = res.events.get(a);
            if (this.messagesDict.indexOfKey(event.id) < 0 && (!(event.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin) || !(event.action.prev_participant instanceof TLRPC.TL_channelParticipantCreator) || (event.action.new_participant instanceof TLRPC.TL_channelParticipantCreator))) {
                this.minEventId = Math.min(this.minEventId, event.id);
                added = true;
                MessageObject messageObject = new MessageObject(this.currentAccount, event, this.messages, this.messagesByDays, this.currentChat, this.mid);
                if (messageObject.contentType >= 0) {
                    this.messagesDict.put(event.id, messageObject);
                }
            }
        }
        int newRowsCount = this.messages.size() - oldRowsCount;
        this.loading = false;
        if (!added) {
            this.endReached = true;
        }
        this.progressView.setVisibility(4);
        this.chatListView.setEmptyView(this.emptyViewContainer);
        if (newRowsCount != 0) {
            boolean end = false;
            if (this.endReached) {
                end = true;
                this.chatAdapter.notifyItemRangeChanged(0, 2);
            }
            int firstVisPos = this.chatLayoutManager.findLastVisibleItemPosition();
            View firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
            if (firstVisView != null) {
                i = firstVisView.getTop();
            }
            int top = i - this.chatListView.getPaddingTop();
            if (newRowsCount - end > 0) {
                int insertStart = (!end) + true;
                this.chatAdapter.notifyItemChanged(insertStart);
                this.chatAdapter.notifyItemRangeInserted(insertStart, newRowsCount - end);
            }
            if (firstVisPos != -1) {
                this.chatLayoutManager.scrollToPositionWithOffset((firstVisPos + newRowsCount) - end, top);
            }
        } else if (this.endReached) {
            this.chatAdapter.notifyItemRemoved(0);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0044, code lost:
        r6 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00aa, code lost:
        r4 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0123, code lost:
        r5 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r11, int r12, java.lang.Object... r13) {
        /*
            r10 = this;
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.emojiDidLoad
            if (r11 != r0) goto L_0x000d
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r10.chatListView
            if (r0 == 0) goto L_0x0153
            r0.invalidateViews()
            goto L_0x0153
        L_0x000d:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingDidStart
            r1 = 0
            r2 = 1
            if (r11 != r0) goto L_0x0083
            r0 = r13[r1]
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            boolean r3 = r0.isRoundVideo()
            if (r3 == 0) goto L_0x002f
            im.bclpbkiauv.messenger.MediaController r3 = im.bclpbkiauv.messenger.MediaController.getInstance()
            android.view.TextureView r4 = r10.createTextureView(r2)
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r5 = r10.aspectRatioFrameLayout
            android.widget.FrameLayout r6 = r10.roundVideoContainer
            r3.setTextureView(r4, r5, r6, r2)
            r10.updateTextureViewPosition()
        L_0x002f:
            im.bclpbkiauv.ui.components.RecyclerListView r3 = r10.chatListView
            if (r3 == 0) goto L_0x0081
            int r3 = r3.getChildCount()
            r4 = 0
        L_0x0038:
            if (r4 >= r3) goto L_0x0081
            im.bclpbkiauv.ui.components.RecyclerListView r5 = r10.chatListView
            android.view.View r5 = r5.getChildAt(r4)
            boolean r6 = r5 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
            if (r6 == 0) goto L_0x007e
            r6 = r5
            im.bclpbkiauv.ui.cells.ChatMessageCell r6 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r6
            im.bclpbkiauv.messenger.MessageObject r7 = r6.getMessageObject()
            if (r7 == 0) goto L_0x007e
            boolean r8 = r7.isVoice()
            if (r8 != 0) goto L_0x007b
            boolean r8 = r7.isMusic()
            if (r8 == 0) goto L_0x005a
            goto L_0x007b
        L_0x005a:
            boolean r8 = r7.isRoundVideo()
            if (r8 == 0) goto L_0x007e
            r6.checkVideoPlayback(r1)
            im.bclpbkiauv.messenger.MediaController r8 = im.bclpbkiauv.messenger.MediaController.getInstance()
            boolean r8 = r8.isPlayingMessage(r7)
            if (r8 != 0) goto L_0x007e
            float r8 = r7.audioProgress
            r9 = 0
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 == 0) goto L_0x007e
            r7.resetPlayingProgress()
            r6.invalidate()
            goto L_0x007e
        L_0x007b:
            r6.updateButtonState(r1, r2, r1)
        L_0x007e:
            int r4 = r4 + 1
            goto L_0x0038
        L_0x0081:
            goto L_0x0153
        L_0x0083:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingDidReset
            if (r11 == r0) goto L_0x010e
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r11 != r0) goto L_0x008d
            goto L_0x010e
        L_0x008d:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingProgressDidChanged
            if (r11 != r0) goto L_0x00da
            r0 = r13[r1]
            java.lang.Integer r0 = (java.lang.Integer) r0
            im.bclpbkiauv.ui.components.RecyclerListView r1 = r10.chatListView
            if (r1 == 0) goto L_0x010d
            int r1 = r1.getChildCount()
            r2 = 0
        L_0x009e:
            if (r2 >= r1) goto L_0x010d
            im.bclpbkiauv.ui.components.RecyclerListView r3 = r10.chatListView
            android.view.View r3 = r3.getChildAt(r2)
            boolean r4 = r3 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
            if (r4 == 0) goto L_0x00d7
            r4 = r3
            im.bclpbkiauv.ui.cells.ChatMessageCell r4 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r4
            im.bclpbkiauv.messenger.MessageObject r5 = r4.getMessageObject()
            if (r5 == 0) goto L_0x00d7
            int r6 = r5.getId()
            int r7 = r0.intValue()
            if (r6 != r7) goto L_0x00d7
            im.bclpbkiauv.messenger.MediaController r6 = im.bclpbkiauv.messenger.MediaController.getInstance()
            im.bclpbkiauv.messenger.MessageObject r6 = r6.getPlayingMessageObject()
            if (r6 == 0) goto L_0x010d
            float r7 = r6.audioProgress
            r5.audioProgress = r7
            int r7 = r6.audioProgressSec
            r5.audioProgressSec = r7
            int r7 = r6.audioPlayerDuration
            r5.audioPlayerDuration = r7
            r4.updatePlayingMessageProgress()
            goto L_0x010d
        L_0x00d7:
            int r2 = r2 + 1
            goto L_0x009e
        L_0x00da:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.didSetNewWallpapper
            if (r11 != r0) goto L_0x010d
            android.view.View r0 = r10.fragmentView
            if (r0 == 0) goto L_0x0153
            im.bclpbkiauv.ui.components.SizeNotifierFrameLayout r0 = r10.contentView
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.getCachedWallpaper()
            boolean r2 = im.bclpbkiauv.ui.actionbar.Theme.isWallpaperMotion()
            r0.setBackgroundImage(r1, r2)
            android.view.View r0 = r10.progressView2
            android.graphics.drawable.Drawable r0 = r0.getBackground()
            android.graphics.PorterDuffColorFilter r1 = im.bclpbkiauv.ui.actionbar.Theme.colorFilter
            r0.setColorFilter(r1)
            android.widget.TextView r0 = r10.emptyView
            if (r0 == 0) goto L_0x0107
            android.graphics.drawable.Drawable r0 = r0.getBackground()
            android.graphics.PorterDuffColorFilter r1 = im.bclpbkiauv.ui.actionbar.Theme.colorFilter
            r0.setColorFilter(r1)
        L_0x0107:
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r10.chatListView
            r0.invalidateViews()
            goto L_0x0153
        L_0x010d:
            goto L_0x0153
        L_0x010e:
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r10.chatListView
            if (r0 == 0) goto L_0x0153
            int r0 = r0.getChildCount()
            r3 = 0
        L_0x0117:
            if (r3 >= r0) goto L_0x0153
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r10.chatListView
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
            if (r5 == 0) goto L_0x0150
            r5 = r4
            im.bclpbkiauv.ui.cells.ChatMessageCell r5 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r5
            im.bclpbkiauv.messenger.MessageObject r6 = r5.getMessageObject()
            if (r6 == 0) goto L_0x0150
            boolean r7 = r6.isVoice()
            if (r7 != 0) goto L_0x014d
            boolean r7 = r6.isMusic()
            if (r7 == 0) goto L_0x0139
            goto L_0x014d
        L_0x0139:
            boolean r7 = r6.isRoundVideo()
            if (r7 == 0) goto L_0x0150
            im.bclpbkiauv.messenger.MediaController r7 = im.bclpbkiauv.messenger.MediaController.getInstance()
            boolean r7 = r7.isPlayingMessage(r6)
            if (r7 != 0) goto L_0x0150
            r5.checkVideoPlayback(r2)
            goto L_0x0150
        L_0x014d:
            r5.updateButtonState(r1, r2, r1)
        L_0x0150:
            int r3 = r3 + 1
            goto L_0x0117
        L_0x0153:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChannelAdminLogActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    public void updateBottomOverlay() {
    }

    public View createView(Context context) {
        Context context2 = context;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int a = 0; a < 8; a++) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context2));
            }
        }
        this.searchWas = false;
        this.hasOwnBackground = true;
        Theme.createChatResources(context2, false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet());
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelAdminLogActivity.this.finishFragment();
                }
            }
        });
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context2, (ChatActivity) null, false);
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2.0f, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchCollapse() {
                String unused = ChannelAdminLogActivity.this.searchQuery = "";
                ChannelAdminLogActivity.this.avatarContainer.setVisibility(0);
                if (ChannelAdminLogActivity.this.searchWas) {
                    boolean unused2 = ChannelAdminLogActivity.this.searchWas = false;
                    ChannelAdminLogActivity.this.loadMessages(true);
                }
                ChannelAdminLogActivity.this.updateBottomOverlay();
            }

            public void onSearchExpand() {
                ChannelAdminLogActivity.this.avatarContainer.setVisibility(8);
                ChannelAdminLogActivity.this.updateBottomOverlay();
            }

            public void onSearchPressed(EditText editText) {
                boolean unused = ChannelAdminLogActivity.this.searchWas = true;
                String unused2 = ChannelAdminLogActivity.this.searchQuery = editText.getText().toString();
                ChannelAdminLogActivity.this.loadMessages(true);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.avatarContainer.setEnabled(false);
        this.avatarContainer.setTitle(this.currentChat.title);
        this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", R.string.EventLogAllEvents));
        this.avatarContainer.setChatAvatar(this.currentChat);
        this.fragmentView = new SizeNotifierFrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isRoundVideo() && messageObject.eventId != 0 && messageObject.getDialogId() == ((long) (-ChannelAdminLogActivity.this.currentChat.id))) {
                    MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.parentLayout != null) {
                    ChannelAdminLogActivity.this.parentLayout.drawHeaderShadow(canvas, ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return result;
            }

            /* access modifiers changed from: protected */
            public boolean isActionBarVisible() {
                return ChannelAdminLogActivity.this.actionBar.getVisibility() == 0;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                int heightSize2 = heightSize - getPaddingTop();
                measureChildWithMargins(ChannelAdminLogActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                if (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                    heightSize2 -= actionBarHeight;
                }
                int keyboardHeight = getKeyboardHeight();
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == ChannelAdminLogActivity.this.actionBar)) {
                        if (child == ChannelAdminLogActivity.this.chatListView || child == ChannelAdminLogActivity.this.progressView) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), heightSize2 - AndroidUtilities.dp(50.0f)), 1073741824));
                        } else if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(heightSize2, 1073741824));
                        } else {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        int i2 = gravity & 7 & 7;
                        if (i2 == 1) {
                            childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                        } else if (i2 != 5) {
                            childLeft = lp.leftMargin;
                        } else {
                            childLeft = (r - width) - lp.rightMargin;
                        }
                        if (verticalGravity == 16) {
                            childTop = ((((b - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        } else if (verticalGravity != 48) {
                            childTop = verticalGravity != 80 ? lp.topMargin : ((b - t) - height) - lp.bottomMargin;
                        } else {
                            childTop = lp.topMargin + getPaddingTop();
                            if (child != ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                                childTop += ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                            }
                        }
                        if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                            childTop -= AndroidUtilities.dp(24.0f) - (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() / 2 : 0);
                        } else if (child == ChannelAdminLogActivity.this.actionBar) {
                            childTop -= getPaddingTop();
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
                notifyHeightChanged();
            }
        };
        SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
        this.contentView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        FrameLayout frameLayout = new FrameLayout(context2);
        this.emptyViewContainer = frameLayout;
        frameLayout.setVisibility(4);
        this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener($$Lambda$ChannelAdminLogActivity$q9vMnzPUHYnhBd9lfbnuylWEx0Q.INSTANCE);
        TextView textView = new TextView(context2);
        this.emptyView = textView;
        textView.setTextSize(1, 14.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
        this.emptyView.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(10.0f), Theme.getServiceMessageColor()));
        this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.emptyViewContainer.addView(this.emptyView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        AnonymousClass5 r6 = new RecyclerListView(context2) {
            /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
                r1 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r10;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean drawChild(android.graphics.Canvas r9, android.view.View r10, long r11) {
                /*
                    r8 = this;
                    boolean r0 = super.drawChild(r9, r10, r11)
                    boolean r1 = r10 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
                    if (r1 == 0) goto L_0x00c0
                    r1 = r10
                    im.bclpbkiauv.ui.cells.ChatMessageCell r1 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r1
                    im.bclpbkiauv.messenger.ImageReceiver r2 = r1.getAvatarImage()
                    if (r2 == 0) goto L_0x00c0
                    int r3 = r10.getTop()
                    boolean r4 = r1.isPinnedBottom()
                    if (r4 == 0) goto L_0x0047
                    im.bclpbkiauv.ui.ChannelAdminLogActivity r4 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                    im.bclpbkiauv.ui.components.RecyclerListView r4 = r4.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r4.getChildViewHolder(r10)
                    if (r4 == 0) goto L_0x0047
                    im.bclpbkiauv.ui.ChannelAdminLogActivity r5 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                    im.bclpbkiauv.ui.components.RecyclerListView r5 = r5.chatListView
                    int r6 = r4.getAdapterPosition()
                    int r6 = r6 + 1
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r5.findViewHolderForAdapterPosition(r6)
                    if (r4 == 0) goto L_0x0047
                    r5 = 1148846080(0x447a0000, float:1000.0)
                    int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                    int r5 = -r5
                    r2.setImageY(r5)
                    r2.draw(r9)
                    return r0
                L_0x0047:
                    boolean r4 = r1.isPinnedTop()
                    if (r4 == 0) goto L_0x0081
                    im.bclpbkiauv.ui.ChannelAdminLogActivity r4 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                    im.bclpbkiauv.ui.components.RecyclerListView r4 = r4.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r4.getChildViewHolder(r10)
                    if (r4 == 0) goto L_0x0081
                L_0x0059:
                    im.bclpbkiauv.ui.ChannelAdminLogActivity r5 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                    im.bclpbkiauv.ui.components.RecyclerListView r5 = r5.chatListView
                    int r6 = r4.getAdapterPosition()
                    int r6 = r6 + -1
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r5.findViewHolderForAdapterPosition(r6)
                    if (r4 == 0) goto L_0x0081
                    android.view.View r5 = r4.itemView
                    int r3 = r5.getTop()
                    android.view.View r5 = r4.itemView
                    boolean r5 = r5 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
                    if (r5 == 0) goto L_0x0081
                    android.view.View r5 = r4.itemView
                    im.bclpbkiauv.ui.cells.ChatMessageCell r5 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r5
                    boolean r5 = r5.isPinnedTop()
                    if (r5 != 0) goto L_0x0059
                L_0x0081:
                    int r4 = r10.getTop()
                    int r5 = r1.getLayoutHeight()
                    int r4 = r4 + r5
                    im.bclpbkiauv.ui.ChannelAdminLogActivity r5 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                    im.bclpbkiauv.ui.components.RecyclerListView r5 = r5.chatListView
                    int r5 = r5.getHeight()
                    im.bclpbkiauv.ui.ChannelAdminLogActivity r6 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                    im.bclpbkiauv.ui.components.RecyclerListView r6 = r6.chatListView
                    int r6 = r6.getPaddingBottom()
                    int r5 = r5 - r6
                    if (r4 <= r5) goto L_0x00a2
                    r4 = r5
                L_0x00a2:
                    r6 = 1111490560(0x42400000, float:48.0)
                    int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                    int r7 = r4 - r7
                    if (r7 >= r3) goto L_0x00b2
                    int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                    int r4 = r3 + r6
                L_0x00b2:
                    r6 = 1110441984(0x42300000, float:44.0)
                    int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                    int r6 = r4 - r6
                    r2.setImageY(r6)
                    r2.draw(r9)
                L_0x00c0:
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChannelAdminLogActivity.AnonymousClass5.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
            }
        };
        this.chatListView = r6;
        r6.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ChannelAdminLogActivity.this.lambda$createView$3$ChannelAdminLogActivity(view, i);
            }
        });
        this.chatListView.setTag(1);
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.chatListView;
        ChatActivityAdapter chatActivityAdapter = new ChatActivityAdapter(context2);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        this.chatListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.chatListView.setLayoutAnimation((LayoutAnimationController) null);
        AnonymousClass6 r62 = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScrollerMiddle linearSmoothScroller = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.chatLayoutManager = r62;
        r62.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private final int scrollValue = AndroidUtilities.dp(100.0f);
            private float totalDy = 0.0f;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    boolean unused = ChannelAdminLogActivity.this.scrollingFloatingDate = true;
                    boolean unused2 = ChannelAdminLogActivity.this.checkTextureViewPosition = true;
                } else if (newState == 0) {
                    boolean unused3 = ChannelAdminLogActivity.this.scrollingFloatingDate = false;
                    boolean unused4 = ChannelAdminLogActivity.this.checkTextureViewPosition = false;
                    ChannelAdminLogActivity.this.hideFloatingDateView(true);
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChannelAdminLogActivity.this.chatListView.invalidate();
                if (dy != 0 && ChannelAdminLogActivity.this.scrollingFloatingDate && !ChannelAdminLogActivity.this.currentFloatingTopIsNotMessage && ChannelAdminLogActivity.this.floatingDateView.getTag() == null) {
                    if (ChannelAdminLogActivity.this.floatingDateAnimation != null) {
                        ChannelAdminLogActivity.this.floatingDateAnimation.cancel();
                    }
                    ChannelAdminLogActivity.this.floatingDateView.setTag(1);
                    AnimatorSet unused = ChannelAdminLogActivity.this.floatingDateAnimation = new AnimatorSet();
                    ChannelAdminLogActivity.this.floatingDateAnimation.setDuration(150);
                    ChannelAdminLogActivity.this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChannelAdminLogActivity.this.floatingDateView, "alpha", new float[]{1.0f})});
                    ChannelAdminLogActivity.this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                                AnimatorSet unused = ChannelAdminLogActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    ChannelAdminLogActivity.this.floatingDateAnimation.start();
                }
                ChannelAdminLogActivity.this.checkScrollForLoad(true);
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
            }
        });
        int i = this.scrollToPositionOnRecreate;
        if (i != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(i, this.scrollToOffsetOnRecreate);
            this.scrollToPositionOnRecreate = -1;
        }
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.progressView = frameLayout2;
        frameLayout2.setVisibility(4);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context2);
        this.progressView2 = view;
        view.setBackgroundResource(R.drawable.system_loader);
        this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
        this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
        RadialProgressView radialProgressView = new RadialProgressView(context2);
        this.progressBar = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor(Theme.key_chat_serviceText));
        this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
        ChatActionCell chatActionCell = new ChatActionCell(context2);
        this.floatingDateView = chatActionCell;
        chatActionCell.setAlpha(0.0f);
        this.contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2.0f, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.contentView.addView(this.actionBar);
        AnonymousClass8 r63 = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat = r63;
        r63.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChannelAdminLogActivity.this.lambda$createView$5$ChannelAdminLogActivity(view);
            }
        });
        TextView textView2 = new TextView(context2);
        this.bottomOverlayChatText = textView2;
        textView2.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        this.bottomOverlayChatText.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS).toUpperCase());
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        ImageView imageView = new ImageView(context2);
        this.bottomOverlayImage = imageView;
        imageView.setImageResource(R.drawable.log_info);
        this.bottomOverlayImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_fieldOverlayText), PorterDuff.Mode.MULTIPLY));
        this.bottomOverlayImage.setScaleType(ImageView.ScaleType.CENTER);
        this.bottomOverlayChat.addView(this.bottomOverlayImage, LayoutHelper.createFrame(48.0f, 48.0f, 53, 3.0f, 0.0f, 0.0f, 0.0f));
        this.bottomOverlayImage.setContentDescription(LocaleController.getString("BotHelp", R.string.BotHelp));
        this.bottomOverlayImage.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChannelAdminLogActivity.this.lambda$createView$6$ChannelAdminLogActivity(view);
            }
        });
        AnonymousClass9 r5 = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchContainer = r5;
        r5.setWillNotDraw(false);
        this.searchContainer.setVisibility(4);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        ImageView imageView2 = new ImageView(context2);
        this.searchCalendarButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.searchCalendarButton.setImageResource(R.drawable.msg_calendar);
        this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), PorterDuff.Mode.MULTIPLY));
        this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
        this.searchCalendarButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChannelAdminLogActivity.this.lambda$createView$10$ChannelAdminLogActivity(view);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.searchCountText = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_chat_searchPanelText));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1.0f, -2.0f, 19, 108.0f, 0.0f, 0.0f, 0.0f));
        this.chatAdapter.updateRows();
        if (!this.loading || !this.messages.isEmpty()) {
            this.progressView.setVisibility(4);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        } else {
            this.progressView.setVisibility(0);
            this.chatListView.setEmptyView((View) null);
        }
        updateEmptyPlaceholder();
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$2(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$3$ChannelAdminLogActivity(View view, int position) {
        createMenu(view);
    }

    public /* synthetic */ void lambda$createView$5$ChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AdminLogFilterAlert adminLogFilterAlert = new AdminLogFilterAlert(getParentActivity(), this.currentFilter, this.selectedAdmins, this.currentChat.megagroup);
            adminLogFilterAlert.setCurrentAdmins(this.admins);
            adminLogFilterAlert.setAdminLogFilterAlertDelegate(new AdminLogFilterAlert.AdminLogFilterAlertDelegate() {
                public final void didSelectRights(TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray sparseArray) {
                    ChannelAdminLogActivity.this.lambda$null$4$ChannelAdminLogActivity(tL_channelAdminLogEventsFilter, sparseArray);
                }
            });
            showDialog(adminLogFilterAlert);
        }
    }

    public /* synthetic */ void lambda$null$4$ChannelAdminLogActivity(TLRPC.TL_channelAdminLogEventsFilter filter, SparseArray admins2) {
        this.currentFilter = filter;
        this.selectedAdmins = admins2;
        if (filter == null && admins2 == null) {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", R.string.EventLogAllEvents));
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", R.string.EventLogSelectedEvents));
        }
        loadMessages(true);
    }

    public /* synthetic */ void lambda$createView$6$ChannelAdminLogActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (this.currentChat.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetail", R.string.EventLogInfoDetail)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetailChannel", R.string.EventLogInfoDetailChannel)));
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("EventLogInfoTitle", R.string.EventLogInfoTitle));
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$createView$10$ChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
            Calendar calendar = Calendar.getInstance();
            try {
                DatePickerDialog dialog = new DatePickerDialog(getParentActivity(), new DatePickerDialog.OnDateSetListener() {
                    public final void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        ChannelAdminLogActivity.this.lambda$null$7$ChannelAdminLogActivity(datePicker, i, i2, i3);
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5));
                DatePicker datePicker = dialog.getDatePicker();
                datePicker.setMinDate(1375315200000L);
                datePicker.setMaxDate(System.currentTimeMillis());
                dialog.setButton(-1, LocaleController.getString("JumpToDate", R.string.JumpToDate), dialog);
                dialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), $$Lambda$ChannelAdminLogActivity$3r9oe3SdtYbAWi89wqktqMTQVdI.INSTANCE);
                if (Build.VERSION.SDK_INT >= 21) {
                    dialog.setOnShowListener(new DialogInterface.OnShowListener(datePicker) {
                        private final /* synthetic */ DatePicker f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void onShow(DialogInterface dialogInterface) {
                            ChannelAdminLogActivity.lambda$null$9(this.f$0, dialogInterface);
                        }
                    });
                }
                showDialog(dialog);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$null$7$ChannelAdminLogActivity(DatePicker view1, int year1, int month, int dayOfMonth1) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set(year1, month, dayOfMonth1);
        int time = (int) (calendar1.getTime().getTime() / 1000);
        loadMessages(true);
    }

    static /* synthetic */ void lambda$null$8(DialogInterface dialog12, int which) {
    }

    static /* synthetic */ void lambda$null$9(DatePicker datePicker, DialogInterface dialog1) {
        int count = datePicker.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = datePicker.getChildAt(a);
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.width = -1;
            child.setLayoutParams(layoutParams);
        }
    }

    /* access modifiers changed from: private */
    public void createMenu(View v) {
        MessageObject message;
        TLRPC.InputStickerSet stickerSet;
        View view = v;
        if (view instanceof ChatMessageCell) {
            message = ((ChatMessageCell) view).getMessageObject();
        } else if (view instanceof ChatActionCell) {
            message = ((ChatActionCell) view).getMessageObject();
        } else {
            message = null;
        }
        if (message != null) {
            int type = getMessageType(message);
            this.selectedObject = message;
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                ArrayList arrayList = new ArrayList();
                ArrayList<Integer> options = new ArrayList<>();
                if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
                    arrayList.add(LocaleController.getString("Copy", R.string.Copy));
                    options.add(3);
                }
                if (type == 1) {
                    if (this.selectedObject.currentEvent != null && (this.selectedObject.currentEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeStickerSet)) {
                        TLRPC.InputStickerSet stickerSet2 = this.selectedObject.currentEvent.action.new_stickerset;
                        if (stickerSet2 == null || (stickerSet2 instanceof TLRPC.TL_inputStickerSetEmpty)) {
                            stickerSet = this.selectedObject.currentEvent.action.prev_stickerset;
                        } else {
                            stickerSet = stickerSet2;
                        }
                        if (stickerSet != null) {
                            showDialog(new StickersAlert(getParentActivity(), this, stickerSet, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                            return;
                        }
                    }
                } else if (type == 3) {
                    if ((this.selectedObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)) {
                        arrayList.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                        options.add(11);
                    }
                } else if (type == 4) {
                    if (this.selectedObject.isVideo()) {
                        arrayList.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                        options.add(4);
                        arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                        options.add(6);
                    } else if (this.selectedObject.isMusic()) {
                        arrayList.add(LocaleController.getString("SaveToMusic", R.string.SaveToMusic));
                        options.add(10);
                        arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                        options.add(6);
                    } else if (this.selectedObject.getDocument() != null) {
                        if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                            arrayList.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                            options.add(11);
                        }
                        arrayList.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                        options.add(10);
                        arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                        options.add(6);
                    } else {
                        arrayList.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                        options.add(4);
                    }
                } else if (type == 5) {
                    arrayList.add(LocaleController.getString("ApplyLocalizationFile", R.string.ApplyLocalizationFile));
                    options.add(5);
                    arrayList.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                    options.add(10);
                    arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                    options.add(6);
                } else if (type == 10) {
                    arrayList.add(LocaleController.getString("ApplyThemeFile", R.string.ApplyThemeFile));
                    options.add(5);
                    arrayList.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                    options.add(10);
                    arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                    options.add(6);
                } else if (type == 6) {
                    arrayList.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                    options.add(7);
                    arrayList.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                    options.add(10);
                    arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                    options.add(6);
                } else if (type == 7) {
                    if (this.selectedObject.isMask()) {
                        arrayList.add(LocaleController.getString("AddToMasks", R.string.AddToMasks));
                    } else {
                        arrayList.add(LocaleController.getString("AddToStickers", R.string.AddToStickers));
                    }
                    options.add(9);
                } else if (type == 8) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                    if (!(user == null || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.id)) != null)) {
                        arrayList.add(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
                        options.add(15);
                    }
                    if (!(this.selectedObject.messageOwner.media.phone_number == null && this.selectedObject.messageOwner.media.phone_number.length() == 0)) {
                        arrayList.add(LocaleController.getString("Copy", R.string.Copy));
                        options.add(16);
                        arrayList.add(LocaleController.getString("Call", R.string.Call));
                        options.add(17);
                    }
                }
                if (!options.isEmpty()) {
                    builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new DialogInterface.OnClickListener(options) {
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChannelAdminLogActivity.this.lambda$createMenu$11$ChannelAdminLogActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setTitle(LocaleController.getString("Message", R.string.Message));
                    showDialog(builder.create());
                }
            }
        }
    }

    public /* synthetic */ void lambda$createMenu$11$ChannelAdminLogActivity(ArrayList options, DialogInterface dialogInterface, int i) {
        if (this.selectedObject != null && i >= 0 && i < options.size()) {
            processSelectedOption(((Integer) options.get(i)).intValue());
        }
    }

    private String getMessageContent(MessageObject messageObject, int previousUid, boolean name) {
        TLRPC.Chat chat;
        String str = "";
        if (name && previousUid != messageObject.messageOwner.from_id) {
            if (messageObject.messageOwner.from_id > 0) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (user != null) {
                    str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                }
            } else if (messageObject.messageOwner.from_id < 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-messageObject.messageOwner.from_id))) != null) {
                str = chat.title + ":\n";
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            return str + messageObject.messageOwner.message;
        } else if (messageObject.messageOwner.media == null || messageObject.messageOwner.message == null) {
            return str + messageObject.messageText;
        } else {
            return str + messageObject.messageOwner.message;
        }
    }

    /* access modifiers changed from: private */
    public TextureView createTextureView(boolean add) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                AnonymousClass10 r0 = new FrameLayout(getParentActivity()) {
                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }
                };
                this.roundVideoContainer = r0;
                r0.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                    }
                });
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    /* access modifiers changed from: protected */
                    public void onSizeChanged(int w, int h, int oldw, int oldh) {
                        super.onSizeChanged(w, h, oldw, oldh);
                        ChannelAdminLogActivity.this.aspectPath.reset();
                        ChannelAdminLogActivity.this.aspectPath.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Path.Direction.CW);
                        ChannelAdminLogActivity.this.aspectPath.toggleInverseFillType();
                    }

                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }

                    public void setVisibility(int visibility) {
                        super.setVisibility(visibility);
                        if (visibility == 0) {
                            setLayerType(2, (Paint) null);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(ChannelAdminLogActivity.this.aspectPath, ChannelAdminLogActivity.this.aspectPaint);
                    }
                };
                this.aspectPath = new Path();
                Paint paint = new Paint(1);
                this.aspectPaint = paint;
                paint.setColor(-16777216);
                this.aspectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            this.roundVideoContainer.setWillNotDraw(false);
            this.roundVideoContainer.setVisibility(4);
            AspectRatioFrameLayout aspectRatioFrameLayout2 = new AspectRatioFrameLayout(getParentActivity());
            this.aspectRatioFrameLayout = aspectRatioFrameLayout2;
            aspectRatioFrameLayout2.setBackgroundColor(0);
            if (add) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            TextureView textureView = new TextureView(getParentActivity());
            this.videoTextureView = textureView;
            textureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (this.roundVideoContainer.getParent() == null) {
            this.contentView.addView(this.roundVideoContainer, 1, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    private void destroyTextureView() {
        FrameLayout frameLayout = this.roundVideoContainer;
        if (frameLayout != null && frameLayout.getParent() != null) {
            this.contentView.removeView(this.roundVideoContainer);
            this.aspectRatioFrameLayout.setDrawingReady(false);
            this.roundVideoContainer.setVisibility(4);
            if (Build.VERSION.SDK_INT < 21) {
                this.roundVideoContainer.setLayerType(0, (Paint) null);
            }
        }
    }

    private void processSelectedOption(int option) {
        String path;
        MessageObject messageObject = this.selectedObject;
        if (messageObject != null) {
            int i = 3;
            int i2 = 0;
            switch (option) {
                case 3:
                    AndroidUtilities.addToClipboard(getMessageContent(messageObject, 0, true));
                    break;
                case 4:
                    String path2 = messageObject.messageOwner.attachPath;
                    if (path2 != null && path2.length() > 0 && !new File(path2).exists()) {
                        path2 = null;
                    }
                    if (path2 == null || path2.length() == 0) {
                        path2 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (this.selectedObject.type == 3 || this.selectedObject.type == 1) {
                        if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                            FragmentActivity parentActivity = getParentActivity();
                            if (this.selectedObject.type == 3) {
                                i2 = 1;
                            }
                            MediaController.saveFile(path2, parentActivity, i2, (String) null, (String) null);
                            break;
                        } else {
                            getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                            this.selectedObject = null;
                            return;
                        }
                    }
                    break;
                case 5:
                    File locFile = null;
                    if (!(messageObject.messageOwner.attachPath == null || this.selectedObject.messageOwner.attachPath.length() == 0)) {
                        File f = new File(this.selectedObject.messageOwner.attachPath);
                        if (f.exists()) {
                            locFile = f;
                        }
                    }
                    if (locFile == null) {
                        File f2 = FileLoader.getPathToMessage(this.selectedObject.messageOwner);
                        if (f2.exists()) {
                            locFile = f2;
                        }
                    }
                    if (locFile != null) {
                        if (!locFile.getName().toLowerCase().endsWith("attheme")) {
                            if (!LocaleController.getInstance().applyLanguageFile(locFile, this.currentAccount)) {
                                if (getParentActivity() != null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setMessage(LocaleController.getString("IncorrectLocalization", R.string.IncorrectLocalization));
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                                    showDialog(builder.create());
                                    break;
                                } else {
                                    this.selectedObject = null;
                                    return;
                                }
                            } else {
                                presentFragment(new LanguageSelectActivity());
                                break;
                            }
                        } else {
                            LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
                            if (linearLayoutManager != null) {
                                if (linearLayoutManager.findLastVisibleItemPosition() < this.chatLayoutManager.getItemCount() - 1) {
                                    int findFirstVisibleItemPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
                                    this.scrollToPositionOnRecreate = findFirstVisibleItemPosition;
                                    RecyclerListView.Holder holder = (RecyclerListView.Holder) this.chatListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                                    if (holder != null) {
                                        this.scrollToOffsetOnRecreate = holder.itemView.getTop();
                                    } else {
                                        this.scrollToPositionOnRecreate = -1;
                                    }
                                } else {
                                    this.scrollToPositionOnRecreate = -1;
                                }
                            }
                            Theme.ThemeInfo themeInfo = Theme.applyThemeFile(locFile, this.selectedObject.getDocumentName(), (TLRPC.TL_theme) null, true);
                            if (themeInfo == null) {
                                this.scrollToPositionOnRecreate = -1;
                                if (getParentActivity() != null) {
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                                    builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder2.setMessage(LocaleController.getString("IncorrectTheme", R.string.IncorrectTheme));
                                    builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                                    showDialog(builder2.create());
                                    break;
                                } else {
                                    this.selectedObject = null;
                                    return;
                                }
                            } else {
                                presentFragment(new ThemePreviewActivity(themeInfo));
                                break;
                            }
                        }
                    }
                    break;
                case 6:
                    String path3 = messageObject.messageOwner.attachPath;
                    if (path3 != null && path3.length() > 0 && !new File(path3).exists()) {
                        path3 = null;
                    }
                    if (path3 == null || path3.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    } else {
                        path = path3;
                    }
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType(this.selectedObject.getDocument().mime_type);
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), "im.bclpbkiauv.messenger.provider", new File(path)));
                            intent.setFlags(1);
                        } catch (Exception e) {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
                        }
                    } else {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
                    }
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                    break;
                case 7:
                    String path4 = messageObject.messageOwner.attachPath;
                    if (path4 != null && path4.length() > 0 && !new File(path4).exists()) {
                        path4 = null;
                    }
                    if (path4 == null || path4.length() == 0) {
                        path4 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        MediaController.saveFile(path4, getParentActivity(), 0, (String) null, (String) null);
                        break;
                    } else {
                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                        this.selectedObject = null;
                        return;
                    }
                    break;
                case 9:
                    showDialog(new StickersAlert(getParentActivity(), this, this.selectedObject.getInputStickerSet(), (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                    break;
                case 10:
                    if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        String fileName = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
                        if (TextUtils.isEmpty(fileName)) {
                            fileName = this.selectedObject.getFileName();
                        }
                        String path5 = this.selectedObject.messageOwner.attachPath;
                        if (path5 != null && path5.length() > 0 && !new File(path5).exists()) {
                            path5 = null;
                        }
                        if (path5 == null || path5.length() == 0) {
                            path5 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                        }
                        FragmentActivity parentActivity2 = getParentActivity();
                        if (!this.selectedObject.isMusic()) {
                            i = 2;
                        }
                        MediaController.saveFile(path5, parentActivity2, i, fileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : "");
                        break;
                    } else {
                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                        this.selectedObject = null;
                        return;
                    }
                case 11:
                    MessagesController.getInstance(this.currentAccount).saveGif(this.selectedObject, messageObject.getDocument());
                    break;
                case 15:
                    Bundle args = new Bundle();
                    args.putInt("user_id", this.selectedObject.messageOwner.media.user_id);
                    args.putString("phone", this.selectedObject.messageOwner.media.phone_number);
                    args.putBoolean("addContact", true);
                    presentFragment(new ContactAddActivity(args));
                    break;
                case 16:
                    AndroidUtilities.addToClipboard(messageObject.messageOwner.media.phone_number);
                    break;
                case 17:
                    try {
                        Intent intent2 = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
                        intent2.addFlags(C.ENCODING_PCM_MU_LAW);
                        getParentActivity().startActivityForResult(intent2, 500);
                        break;
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                        break;
                    }
            }
            this.selectedObject = null;
        }
    }

    private int getMessageType(MessageObject messageObject) {
        String mime;
        if (messageObject == null || messageObject.type == 6) {
            return -1;
        }
        if (messageObject.type == 10 || messageObject.type == 11 || messageObject.type == 16) {
            if (messageObject.getId() == 0) {
                return -1;
            }
            return 1;
        } else if (messageObject.isVoice()) {
            return 2;
        } else {
            if (messageObject.isSticker() || messageObject.isAnimatedSticker()) {
                TLRPC.InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetID) {
                    if (!MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                        return 7;
                    }
                } else if (!(inputStickerSet instanceof TLRPC.TL_inputStickerSetShortName) || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                    return 2;
                } else {
                    return 7;
                }
            } else if ((!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) && ((messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
                boolean canSave = false;
                if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                    canSave = true;
                }
                if (!canSave && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                    canSave = true;
                }
                if (canSave) {
                    if (messageObject.getDocument() == null || (mime = messageObject.getDocument().mime_type) == null) {
                        return 4;
                    }
                    if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
                        return 10;
                    }
                    if (mime.endsWith("/xml")) {
                        return 5;
                    }
                    if (mime.endsWith("/png") || mime.endsWith("/jpg") || mime.endsWith("/jpeg")) {
                        return 6;
                    }
                    return 4;
                }
            } else if (messageObject.type == 12) {
                return 8;
            } else {
                if (messageObject.isMediaEmpty()) {
                    return 3;
                }
            }
            return 2;
        }
    }

    private void loadAdmins() {
        TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = MessagesController.getInputChannel(this.currentChat);
        req.filter = new TLRPC.TL_channelParticipantsAdmins();
        req.offset = 0;
        req.limit = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelAdminLogActivity.this.lambda$loadAdmins$13$ChannelAdminLogActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadAdmins$13$ChannelAdminLogActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ChannelAdminLogActivity.this.lambda$null$12$ChannelAdminLogActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$12$ChannelAdminLogActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.admins = res.participants;
            if (this.visibleDialog instanceof AdminLogFilterAlert) {
                ((AdminLogFilterAlert) this.visibleDialog).setCurrentAdmins(this.admins);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRemoveFromParent() {
        MediaController.getInstance().setTextureView(this.videoTextureView, (AspectRatioFrameLayout) null, (FrameLayout) null, false);
    }

    /* access modifiers changed from: private */
    public void hideFloatingDateView(boolean animated) {
        if (this.floatingDateView.getTag() != null && !this.currentFloatingDateOnScreen) {
            if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
                this.floatingDateView.setTag((Object) null);
                if (animated) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    this.floatingDateAnimation = animatorSet;
                    animatorSet.setDuration(150);
                    this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, "alpha", new float[]{0.0f})});
                    this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                                AnimatorSet unused = ChannelAdminLogActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    this.floatingDateAnimation.setStartDelay(500);
                    this.floatingDateAnimation.start();
                    return;
                }
                AnimatorSet animatorSet2 = this.floatingDateAnimation;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.floatingDateAnimation = null;
                }
                this.floatingDateView.setAlpha(0.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkScrollForLoad(boolean scroll) {
        int checkLoadCount;
        LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
        if (linearLayoutManager != null && !this.paused) {
            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            if ((firstVisibleItem == -1 ? 0 : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1) > 0) {
                int itemCount = this.chatAdapter.getItemCount();
                if (scroll) {
                    checkLoadCount = 25;
                } else {
                    checkLoadCount = 5;
                }
                if (firstVisibleItem <= checkLoadCount && !this.loading && !this.endReached) {
                    loadMessages(false);
                }
            }
        }
    }

    private void moveScrollToLastMessage() {
        if (this.chatListView != null && !this.messages.isEmpty()) {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
        }
    }

    private void updateTextureViewPosition() {
        boolean foundTextureViewMessage = false;
        int count = this.chatListView.getChildCount();
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            View view = this.chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) view;
                MessageObject messageObject = messageCell.getMessageObject();
                if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                    ImageReceiver imageReceiver = messageCell.getPhotoImage();
                    this.roundVideoContainer.setTranslationX((float) imageReceiver.getImageX());
                    this.roundVideoContainer.setTranslationY((float) (this.fragmentView.getPaddingTop() + messageCell.getTop() + imageReceiver.getImageY()));
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    foundTextureViewMessage = true;
                    break;
                }
            }
            a++;
        }
        if (this.roundVideoContainer != null) {
            MessageObject messageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (!foundTextureViewMessage) {
                this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                this.fragmentView.invalidate();
                if (messageObject2 != null && messageObject2.isRoundVideo()) {
                    if (this.checkTextureViewPosition || PipRoundVideoView.getInstance() != null) {
                        MediaController.getInstance().setCurrentVideoVisible(false);
                        return;
                    }
                    return;
                }
                return;
            }
            MediaController.getInstance().setCurrentVideoVisible(true);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01da  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateMessagesVisisblePart() {
        /*
            r21 = this;
            r0 = r21
            im.bclpbkiauv.ui.components.RecyclerListView r1 = r0.chatListView
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            int r1 = r1.getChildCount()
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.chatListView
            int r2 = r2.getMeasuredHeight()
            r3 = 2147483647(0x7fffffff, float:NaN)
            r4 = 2147483647(0x7fffffff, float:NaN)
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
        L_0x001c:
            r10 = 0
            if (r9 >= r1) goto L_0x00e4
            im.bclpbkiauv.ui.components.RecyclerListView r12 = r0.chatListView
            android.view.View r12 = r12.getChildAt(r9)
            boolean r13 = r12 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
            if (r13 == 0) goto L_0x0098
            r13 = r12
            im.bclpbkiauv.ui.cells.ChatMessageCell r13 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r13
            int r14 = r13.getTop()
            int r15 = r13.getBottom()
            if (r14 < 0) goto L_0x0037
            goto L_0x0038
        L_0x0037:
            int r10 = -r14
        L_0x0038:
            int r11 = r13.getMeasuredHeight()
            if (r11 <= r2) goto L_0x0040
            int r11 = r10 + r2
        L_0x0040:
            r16 = r1
            int r1 = r11 - r10
            r13.setVisiblePart(r10, r1)
            im.bclpbkiauv.messenger.MessageObject r1 = r13.getMessageObject()
            r17 = r2
            android.widget.FrameLayout r2 = r0.roundVideoContainer
            if (r2 == 0) goto L_0x0093
            boolean r2 = r1.isRoundVideo()
            if (r2 == 0) goto L_0x0093
            im.bclpbkiauv.messenger.MediaController r2 = im.bclpbkiauv.messenger.MediaController.getInstance()
            boolean r2 = r2.isPlayingMessage(r1)
            if (r2 == 0) goto L_0x0093
            im.bclpbkiauv.messenger.ImageReceiver r2 = r13.getPhotoImage()
            r18 = r1
            android.widget.FrameLayout r1 = r0.roundVideoContainer
            r19 = r10
            int r10 = r2.getImageX()
            float r10 = (float) r10
            r1.setTranslationX(r10)
            android.widget.FrameLayout r1 = r0.roundVideoContainer
            android.view.View r10 = r0.fragmentView
            int r10 = r10.getPaddingTop()
            int r10 = r10 + r14
            int r20 = r2.getImageY()
            int r10 = r10 + r20
            float r10 = (float) r10
            r1.setTranslationY(r10)
            android.view.View r1 = r0.fragmentView
            r1.invalidate()
            android.widget.FrameLayout r1 = r0.roundVideoContainer
            r1.invalidate()
            r1 = 1
            r8 = r1
            goto L_0x009c
        L_0x0093:
            r18 = r1
            r19 = r10
            goto L_0x009c
        L_0x0098:
            r16 = r1
            r17 = r2
        L_0x009c:
            int r1 = r12.getBottom()
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.chatListView
            int r2 = r2.getPaddingTop()
            if (r1 > r2) goto L_0x00a9
            goto L_0x00dc
        L_0x00a9:
            int r1 = r12.getBottom()
            if (r1 >= r3) goto L_0x00ba
            r3 = r1
            boolean r2 = r12 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
            if (r2 != 0) goto L_0x00b8
            boolean r2 = r12 instanceof im.bclpbkiauv.ui.cells.ChatActionCell
            if (r2 == 0) goto L_0x00b9
        L_0x00b8:
            r7 = r12
        L_0x00b9:
            r6 = r12
        L_0x00ba:
            boolean r2 = r12 instanceof im.bclpbkiauv.ui.cells.ChatActionCell
            if (r2 == 0) goto L_0x00dc
            r2 = r12
            im.bclpbkiauv.ui.cells.ChatActionCell r2 = (im.bclpbkiauv.ui.cells.ChatActionCell) r2
            im.bclpbkiauv.messenger.MessageObject r2 = r2.getMessageObject()
            boolean r2 = r2.isDateObject
            if (r2 == 0) goto L_0x00dc
            float r2 = r12.getAlpha()
            r10 = 1065353216(0x3f800000, float:1.0)
            int r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r2 == 0) goto L_0x00d6
            r12.setAlpha(r10)
        L_0x00d6:
            if (r1 >= r4) goto L_0x00dc
            r2 = r1
            r4 = r12
            r5 = r4
            r4 = r2
        L_0x00dc:
            int r9 = r9 + 1
            r1 = r16
            r2 = r17
            goto L_0x001c
        L_0x00e4:
            r16 = r1
            r17 = r2
            android.widget.FrameLayout r1 = r0.roundVideoContainer
            r2 = 1
            if (r1 == 0) goto L_0x0120
            if (r8 != 0) goto L_0x0119
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.roundMessageSize
            int r9 = -r9
            int r9 = r9 + -100
            float r9 = (float) r9
            r1.setTranslationY(r9)
            android.view.View r1 = r0.fragmentView
            r1.invalidate()
            im.bclpbkiauv.messenger.MediaController r1 = im.bclpbkiauv.messenger.MediaController.getInstance()
            im.bclpbkiauv.messenger.MessageObject r1 = r1.getPlayingMessageObject()
            if (r1 == 0) goto L_0x0118
            boolean r9 = r1.isRoundVideo()
            if (r9 == 0) goto L_0x0118
            boolean r9 = r0.checkTextureViewPosition
            if (r9 == 0) goto L_0x0118
            im.bclpbkiauv.messenger.MediaController r9 = im.bclpbkiauv.messenger.MediaController.getInstance()
            r9.setCurrentVideoVisible(r10)
        L_0x0118:
            goto L_0x0120
        L_0x0119:
            im.bclpbkiauv.messenger.MediaController r1 = im.bclpbkiauv.messenger.MediaController.getInstance()
            r1.setCurrentVideoVisible(r2)
        L_0x0120:
            if (r7 == 0) goto L_0x013e
            boolean r1 = r7 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
            if (r1 == 0) goto L_0x012e
            r1 = r7
            im.bclpbkiauv.ui.cells.ChatMessageCell r1 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r1
            im.bclpbkiauv.messenger.MessageObject r1 = r1.getMessageObject()
            goto L_0x0135
        L_0x012e:
            r1 = r7
            im.bclpbkiauv.ui.cells.ChatActionCell r1 = (im.bclpbkiauv.ui.cells.ChatActionCell) r1
            im.bclpbkiauv.messenger.MessageObject r1 = r1.getMessageObject()
        L_0x0135:
            im.bclpbkiauv.ui.cells.ChatActionCell r9 = r0.floatingDateView
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r1.messageOwner
            int r11 = r11.date
            r9.setCustomDate(r11, r10)
        L_0x013e:
            r0.currentFloatingDateOnScreen = r10
            boolean r1 = r6 instanceof im.bclpbkiauv.ui.cells.ChatMessageCell
            if (r1 != 0) goto L_0x0149
            boolean r1 = r6 instanceof im.bclpbkiauv.ui.cells.ChatActionCell
            if (r1 != 0) goto L_0x0149
            r10 = 1
        L_0x0149:
            r0.currentFloatingTopIsNotMessage = r10
            r1 = 0
            if (r5 == 0) goto L_0x01e0
            int r9 = r5.getTop()
            im.bclpbkiauv.ui.components.RecyclerListView r10 = r0.chatListView
            int r10 = r10.getPaddingTop()
            if (r9 > r10) goto L_0x019b
            boolean r9 = r0.currentFloatingTopIsNotMessage
            if (r9 == 0) goto L_0x0161
            r10 = 1065353216(0x3f800000, float:1.0)
            goto L_0x019d
        L_0x0161:
            float r9 = r5.getAlpha()
            int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x016c
            r5.setAlpha(r1)
        L_0x016c:
            android.animation.AnimatorSet r9 = r0.floatingDateAnimation
            if (r9 == 0) goto L_0x0176
            r9.cancel()
            r9 = 0
            r0.floatingDateAnimation = r9
        L_0x0176:
            im.bclpbkiauv.ui.cells.ChatActionCell r9 = r0.floatingDateView
            java.lang.Object r9 = r9.getTag()
            if (r9 != 0) goto L_0x0187
            im.bclpbkiauv.ui.cells.ChatActionCell r9 = r0.floatingDateView
            java.lang.Integer r10 = java.lang.Integer.valueOf(r2)
            r9.setTag(r10)
        L_0x0187:
            im.bclpbkiauv.ui.cells.ChatActionCell r9 = r0.floatingDateView
            float r9 = r9.getAlpha()
            r10 = 1065353216(0x3f800000, float:1.0)
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 == 0) goto L_0x0198
            im.bclpbkiauv.ui.cells.ChatActionCell r9 = r0.floatingDateView
            r9.setAlpha(r10)
        L_0x0198:
            r0.currentFloatingDateOnScreen = r2
            goto L_0x01ae
        L_0x019b:
            r10 = 1065353216(0x3f800000, float:1.0)
        L_0x019d:
            float r9 = r5.getAlpha()
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 == 0) goto L_0x01a8
            r5.setAlpha(r10)
        L_0x01a8:
            boolean r9 = r0.currentFloatingTopIsNotMessage
            r2 = r2 ^ r9
            r0.hideFloatingDateView(r2)
        L_0x01ae:
            int r2 = r5.getBottom()
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.chatListView
            int r9 = r9.getPaddingTop()
            int r2 = r2 - r9
            im.bclpbkiauv.ui.cells.ChatActionCell r9 = r0.floatingDateView
            int r9 = r9.getMeasuredHeight()
            if (r2 <= r9) goto L_0x01da
            im.bclpbkiauv.ui.cells.ChatActionCell r9 = r0.floatingDateView
            int r9 = r9.getMeasuredHeight()
            int r9 = r9 * 2
            if (r2 >= r9) goto L_0x01da
            im.bclpbkiauv.ui.cells.ChatActionCell r1 = r0.floatingDateView
            int r9 = r1.getMeasuredHeight()
            int r9 = -r9
            int r9 = r9 * 2
            int r9 = r9 + r2
            float r9 = (float) r9
            r1.setTranslationY(r9)
            goto L_0x01df
        L_0x01da:
            im.bclpbkiauv.ui.cells.ChatActionCell r9 = r0.floatingDateView
            r9.setTranslationY(r1)
        L_0x01df:
            goto L_0x01e8
        L_0x01e0:
            r0.hideFloatingDateView(r2)
            im.bclpbkiauv.ui.cells.ChatActionCell r2 = r0.floatingDateView
            r2.setTranslationY(r1)
        L_0x01e8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChannelAdminLogActivity.updateMessagesVisisblePart():void");
    }

    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        if (isOpen) {
            NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad});
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
            this.openAnimationEnded = false;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
            this.openAnimationEnded = true;
        }
    }

    public void onResume() {
        super.onResume();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onResume();
        }
        this.paused = false;
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
            if (chatActivityAdapter != null) {
                chatActivityAdapter.notifyDataSetChanged();
            }
        }
        fixLayout();
    }

    public void onPause() {
        super.onPause();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onPause();
        }
        this.paused = true;
        this.wasPaused = true;
    }

    public void viewContacts(int user_id) {
        TLRPC.User user;
        if (user_id != 0 && (user = getMessagesController().getUser(Integer.valueOf(user_id))) != null) {
            if (user.self || user.contact) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", user.id);
                presentFragment(new NewProfileActivity(bundle));
                return;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putInt("from_type", 6);
            presentFragment(new AddContactsInfoActivity(bundle2, user));
        }
    }

    private void fixLayout() {
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        if (chatAvatarContainer != null) {
            chatAvatarContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ChannelAdminLogActivity.this.avatarContainer == null) {
                        return true;
                    }
                    ChannelAdminLogActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        fixLayout();
        if (this.visibleDialog instanceof DatePickerDialog) {
            this.visibleDialog.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void alertUserOpenError(MessageObject message) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            if (message.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", R.string.NoPlayerInstalled));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", R.string.NoHandleAppInstalled, message.getDocument().mime_type));
            }
            showDialog(builder.create());
        }
    }

    public TLRPC.Chat getCurrentChat() {
        return this.currentChat;
    }

    /* access modifiers changed from: private */
    public void addCanBanUser(Bundle bundle, int uid) {
        if (this.currentChat.megagroup && this.admins != null && ChatObject.canBlockUsers(this.currentChat)) {
            int a = 0;
            while (true) {
                if (a >= this.admins.size()) {
                    break;
                }
                TLRPC.ChannelParticipant channelParticipant = this.admins.get(a);
                if (channelParticipant.user_id != uid) {
                    a++;
                } else if (!channelParticipant.can_edit) {
                    return;
                }
            }
            bundle.putInt("ban_chat_id", this.currentChat.id);
        }
    }

    public void showOpenUrlAlert(String url, boolean ask) {
        if (Browser.isInternalUrl(url, (boolean[]) null) || !ask) {
            Browser.openUrl((Context) getParentActivity(), url, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("OpenUrlTitle", R.string.OpenUrlTitle));
        builder.setMessage(LocaleController.formatString("OpenUrlAlert2", R.string.OpenUrlAlert2, url));
        builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new DialogInterface.OnClickListener(url) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelAdminLogActivity.this.lambda$showOpenUrlAlert$14$ChannelAdminLogActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$showOpenUrlAlert$14$ChannelAdminLogActivity(String url, DialogInterface dialogInterface, int i) {
        Browser.openUrl((Context) getParentActivity(), url, true);
    }

    private void removeMessageObject(MessageObject messageObject) {
        int index = this.messages.indexOf(messageObject);
        if (index != -1) {
            this.messages.remove(index);
            ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
            if (chatActivityAdapter != null) {
                chatActivityAdapter.notifyItemRemoved(((chatActivityAdapter.messagesStartRow + this.messages.size()) - index) - 1);
            }
        }
    }

    public class ChatActivityAdapter extends RecyclerView.Adapter {
        private int loadingUpRow;
        /* access modifiers changed from: private */
        public Context mContext;
        private int messagesEndRow;
        /* access modifiers changed from: private */
        public int messagesStartRow;
        private int rowCount;

        public ChatActivityAdapter(Context context) {
            this.mContext = context;
        }

        public void updateRows() {
            this.rowCount = 0;
            if (!ChannelAdminLogActivity.this.messages.isEmpty()) {
                if (!ChannelAdminLogActivity.this.endReached) {
                    int i = this.rowCount;
                    this.rowCount = i + 1;
                    this.loadingUpRow = i;
                } else {
                    this.loadingUpRow = -1;
                }
                int i2 = this.rowCount;
                this.messagesStartRow = i2;
                int size = i2 + ChannelAdminLogActivity.this.messages.size();
                this.rowCount = size;
                this.messagesEndRow = size;
                return;
            }
            this.loadingUpRow = -1;
            this.messagesStartRow = -1;
            this.messagesEndRow = -1;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public long getItemId(int i) {
            return -1;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: im.bclpbkiauv.ui.cells.ChatActionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: im.bclpbkiauv.ui.cells.ChatActionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: im.bclpbkiauv.ui.cells.ChatLoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: im.bclpbkiauv.ui.cells.BotHelpCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: im.bclpbkiauv.ui.cells.ChatUnreadCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: im.bclpbkiauv.ui.cells.ChatActionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v15, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: android.view.View} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                r0 = 0
                r1 = 1
                if (r6 != 0) goto L_0x003f
                im.bclpbkiauv.ui.ChannelAdminLogActivity r2 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                java.util.ArrayList r2 = r2.chatMessageCellsCache
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x0028
                im.bclpbkiauv.ui.ChannelAdminLogActivity r2 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                java.util.ArrayList r2 = r2.chatMessageCellsCache
                r3 = 0
                java.lang.Object r2 = r2.get(r3)
                r0 = r2
                android.view.View r0 = (android.view.View) r0
                im.bclpbkiauv.ui.ChannelAdminLogActivity r2 = im.bclpbkiauv.ui.ChannelAdminLogActivity.this
                java.util.ArrayList r2 = r2.chatMessageCellsCache
                r2.remove(r3)
                goto L_0x0030
            L_0x0028:
                im.bclpbkiauv.ui.cells.ChatMessageCell r2 = new im.bclpbkiauv.ui.cells.ChatMessageCell
                android.content.Context r3 = r4.mContext
                r2.<init>(r3)
                r0 = r2
            L_0x0030:
                r2 = r0
                im.bclpbkiauv.ui.cells.ChatMessageCell r2 = (im.bclpbkiauv.ui.cells.ChatMessageCell) r2
                im.bclpbkiauv.ui.ChannelAdminLogActivity$ChatActivityAdapter$1 r3 = new im.bclpbkiauv.ui.ChannelAdminLogActivity$ChatActivityAdapter$1
                r3.<init>()
                r2.setDelegate(r3)
                r2.setAllowAssistant(r1)
                goto L_0x0083
            L_0x003f:
                if (r6 != r1) goto L_0x0055
                im.bclpbkiauv.ui.cells.ChatActionCell r1 = new im.bclpbkiauv.ui.cells.ChatActionCell
                android.content.Context r2 = r4.mContext
                r1.<init>(r2)
                r0 = r1
                r1 = r0
                im.bclpbkiauv.ui.cells.ChatActionCell r1 = (im.bclpbkiauv.ui.cells.ChatActionCell) r1
                im.bclpbkiauv.ui.ChannelAdminLogActivity$ChatActivityAdapter$2 r2 = new im.bclpbkiauv.ui.ChannelAdminLogActivity$ChatActivityAdapter$2
                r2.<init>()
                r1.setDelegate(r2)
                goto L_0x0083
            L_0x0055:
                r1 = 2
                if (r6 != r1) goto L_0x0061
                im.bclpbkiauv.ui.cells.ChatUnreadCell r1 = new im.bclpbkiauv.ui.cells.ChatUnreadCell
                android.content.Context r2 = r4.mContext
                r1.<init>(r2)
                r0 = r1
                goto L_0x0083
            L_0x0061:
                r1 = 3
                if (r6 != r1) goto L_0x0078
                im.bclpbkiauv.ui.cells.BotHelpCell r1 = new im.bclpbkiauv.ui.cells.BotHelpCell
                android.content.Context r2 = r4.mContext
                r1.<init>(r2)
                r0 = r1
                r1 = r0
                im.bclpbkiauv.ui.cells.BotHelpCell r1 = (im.bclpbkiauv.ui.cells.BotHelpCell) r1
                im.bclpbkiauv.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$at5c2Ta0SSRdpbNye5FnZcIiF_A r2 = new im.bclpbkiauv.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$at5c2Ta0SSRdpbNye5FnZcIiF_A
                r2.<init>()
                r1.setDelegate(r2)
                goto L_0x0083
            L_0x0078:
                r1 = 4
                if (r6 != r1) goto L_0x0083
                im.bclpbkiauv.ui.cells.ChatLoadingCell r1 = new im.bclpbkiauv.ui.cells.ChatLoadingCell
                android.content.Context r2 = r4.mContext
                r1.<init>(r2)
                r0 = r1
            L_0x0083:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -1
                r3 = -2
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r1 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChannelAdminLogActivity.ChatActivityAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$ChannelAdminLogActivity$ChatActivityAdapter(String url) {
            if (url.startsWith("@")) {
                MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(url.substring(1), ChannelAdminLogActivity.this, 0);
            } else if (url.startsWith("#")) {
                DialogsActivity fragment = new DialogsActivity((Bundle) null);
                fragment.setSearchString(url);
                ChannelAdminLogActivity.this.presentFragment(fragment);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean pinnedBotton;
            boolean z = false;
            boolean pinnedTop = true;
            if (position == this.loadingUpRow) {
                ChatLoadingCell loadingCell = (ChatLoadingCell) holder.itemView;
                if (ChannelAdminLogActivity.this.loadsCount > 1) {
                    z = true;
                }
                loadingCell.setProgressVisible(z);
            } else if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                MessageObject message = ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow)) - 1);
                View view = holder.itemView;
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    messageCell.isChat = true;
                    int nextType = getItemViewType(position + 1);
                    int prevType = getItemViewType(position - 1);
                    if ((message.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                        pinnedBotton = false;
                    } else {
                        MessageObject nextMessage = ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - ((position + 1) - this.messagesStartRow)) - 1);
                        pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && nextMessage.messageOwner.from_id == message.messageOwner.from_id && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                    }
                    if (prevType == holder.getItemViewType()) {
                        MessageObject prevMessage = ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow));
                        if ((prevMessage.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || prevMessage.isOutOwner() != message.isOutOwner() || prevMessage.messageOwner.from_id != message.messageOwner.from_id || Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) > 300) {
                            pinnedTop = false;
                        }
                    } else {
                        pinnedTop = false;
                    }
                    messageCell.setMessageObject(message, (MessageObject.GroupedMessages) null, pinnedBotton, pinnedTop);
                    messageCell.setHighlighted(false);
                    messageCell.setHighlightedText((String) null);
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell actionCell = (ChatActionCell) view;
                    actionCell.setMessageObject(message);
                    actionCell.setAlpha(1.0f);
                }
            }
        }

        public int getItemViewType(int position) {
            if (position < this.messagesStartRow || position >= this.messagesEndRow) {
                return 4;
            }
            return ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow)) - 1).contentType;
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ChatMessageCell) {
                final ChatMessageCell messageCell = (ChatMessageCell) holder.itemView;
                MessageObject messageObject = messageCell.getMessageObject();
                messageCell.setBackgroundDrawable((Drawable) null);
                messageCell.setCheckPressed(!false, (0 == 0 || 0 == 0) ? false : true);
                messageCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        messageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                        int height = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight();
                        int top = messageCell.getTop();
                        int bottom = messageCell.getBottom();
                        int viewTop = top >= 0 ? 0 : -top;
                        int viewBottom = messageCell.getMeasuredHeight();
                        if (viewBottom > height) {
                            viewBottom = viewTop + height;
                        }
                        messageCell.setVisiblePart(viewTop, viewBottom - viewTop);
                        return true;
                    }
                });
                messageCell.setHighlighted(false);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int index = ChannelAdminLogActivity.this.messages.indexOf(messageObject);
            if (index != -1) {
                notifyItemChanged(((this.messagesStartRow + ChannelAdminLogActivity.this.messages.size()) - index) - 1);
            }
        }

        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[209];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_wallpaper);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuBackground);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItem);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItemIcon);
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault);
        themeDescriptionArr[9] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault);
        themeDescriptionArr[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[11] = new ThemeDescription(this.avatarContainer.getTitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[12] = new ThemeDescription((View) this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubtitle, (Object) null);
        themeDescriptionArr[13] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[14] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text);
        themeDescriptionArr[15] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[16] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[17] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[18] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[19] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[20] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[21] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[22] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_nameInMessageRed);
        themeDescriptionArr[23] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_nameInMessageOrange);
        themeDescriptionArr[24] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_nameInMessageViolet);
        themeDescriptionArr[25] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_nameInMessageGreen);
        themeDescriptionArr[26] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_nameInMessageCyan);
        themeDescriptionArr[27] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_nameInMessageBlue);
        themeDescriptionArr[28] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_nameInMessagePink);
        themeDescriptionArr[29] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubble);
        themeDescriptionArr[30] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleSelected);
        themeDescriptionArr[31] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleShadow);
        themeDescriptionArr[32] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubble);
        themeDescriptionArr[33] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleSelected);
        themeDescriptionArr[34] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleShadow);
        themeDescriptionArr[35] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceText);
        themeDescriptionArr[36] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceLink);
        themeDescriptionArr[37] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_shareIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawalbe, Theme.chat_goIconDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceIcon);
        themeDescriptionArr[38] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[39] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceBackgroundSelected);
        themeDescriptionArr[40] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextIn);
        themeDescriptionArr[41] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextOut);
        themeDescriptionArr[42] = new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageLinkIn, (Object) null);
        themeDescriptionArr[43] = new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageLinkOut, (Object) null);
        themeDescriptionArr[44] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheck);
        themeDescriptionArr[45] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckSelected);
        themeDescriptionArr[46] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckRead);
        themeDescriptionArr[47] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckReadSelected);
        themeDescriptionArr[48] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentClock);
        themeDescriptionArr[49] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentClockSelected);
        themeDescriptionArr[50] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inSentClock);
        themeDescriptionArr[51] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inSentClockSelected);
        themeDescriptionArr[52] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaSentCheck);
        themeDescriptionArr[53] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgStickerHalfCheckDrawable, Theme.chat_msgStickerCheckDrawable, Theme.chat_msgStickerClockDrawable, Theme.chat_msgStickerViewsDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceText);
        themeDescriptionArr[54] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaSentClock);
        themeDescriptionArr[55] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutViewsDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outViews);
        themeDescriptionArr[56] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outViewsSelected);
        themeDescriptionArr[57] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInViewsDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inViews);
        themeDescriptionArr[58] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inViewsSelected);
        themeDescriptionArr[59] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaViewsDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaViews);
        themeDescriptionArr[60] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outMenu);
        themeDescriptionArr[61] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outMenuSelected);
        themeDescriptionArr[62] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inMenu);
        themeDescriptionArr[63] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inMenuSelected);
        themeDescriptionArr[64] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaMenu);
        themeDescriptionArr[65] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutInstantDrawable, Theme.chat_msgOutCallDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outInstant);
        themeDescriptionArr[66] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCallSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outInstantSelected);
        themeDescriptionArr[67] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_msgInCallDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inInstant);
        themeDescriptionArr[68] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInCallSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inInstantSelected);
        themeDescriptionArr[69] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outGreenCall);
        themeDescriptionArr[70] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgCallDownRedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inRedCall);
        themeDescriptionArr[71] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgCallDownGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inGreenCall);
        themeDescriptionArr[72] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_sentError);
        themeDescriptionArr[73] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgErrorDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_sentErrorIcon);
        themeDescriptionArr[74] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_previewDurationText);
        themeDescriptionArr[75] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_previewGameText);
        themeDescriptionArr[76] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inPreviewInstantText);
        themeDescriptionArr[77] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outPreviewInstantText);
        themeDescriptionArr[78] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inPreviewInstantSelectedText);
        themeDescriptionArr[79] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outPreviewInstantSelectedText);
        themeDescriptionArr[80] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_secretTimeText);
        themeDescriptionArr[81] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_stickerNameText);
        themeDescriptionArr[82] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_botButtonText);
        themeDescriptionArr[83] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_botProgress);
        themeDescriptionArr[84] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inForwardedNameText);
        themeDescriptionArr[85] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outForwardedNameText);
        themeDescriptionArr[86] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inViaBotNameText);
        themeDescriptionArr[87] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outViaBotNameText);
        themeDescriptionArr[88] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_stickerViaBotNameText);
        themeDescriptionArr[89] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyLine);
        themeDescriptionArr[90] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyLine);
        themeDescriptionArr[91] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_stickerReplyLine);
        themeDescriptionArr[92] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyNameText);
        themeDescriptionArr[93] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyNameText);
        themeDescriptionArr[94] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_stickerReplyNameText);
        themeDescriptionArr[95] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMessageText);
        themeDescriptionArr[96] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMessageText);
        themeDescriptionArr[97] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMediaMessageText);
        themeDescriptionArr[98] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMediaMessageText);
        themeDescriptionArr[99] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMediaMessageSelectedText);
        themeDescriptionArr[100] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMediaMessageSelectedText);
        themeDescriptionArr[101] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_stickerReplyMessageText);
        themeDescriptionArr[102] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inPreviewLine);
        themeDescriptionArr[103] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outPreviewLine);
        themeDescriptionArr[104] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inSiteNameText);
        themeDescriptionArr[105] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSiteNameText);
        themeDescriptionArr[106] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inContactNameText);
        themeDescriptionArr[107] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outContactNameText);
        themeDescriptionArr[108] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inContactPhoneText);
        themeDescriptionArr[109] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outContactPhoneText);
        themeDescriptionArr[110] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaProgress);
        themeDescriptionArr[111] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioProgress);
        themeDescriptionArr[112] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioProgress);
        themeDescriptionArr[113] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioSelectedProgress);
        themeDescriptionArr[114] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioSelectedProgress);
        themeDescriptionArr[115] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaTimeText);
        themeDescriptionArr[116] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeText);
        themeDescriptionArr[117] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeText);
        themeDescriptionArr[118] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeSelectedText);
        themeDescriptionArr[119] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeSelectedText);
        themeDescriptionArr[120] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioPerformerText);
        themeDescriptionArr[121] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioPerformerText);
        themeDescriptionArr[122] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioTitleText);
        themeDescriptionArr[123] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioTitleText);
        themeDescriptionArr[124] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioDurationText);
        themeDescriptionArr[125] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioDurationText);
        themeDescriptionArr[126] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioDurationSelectedText);
        themeDescriptionArr[127] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioDurationSelectedText);
        themeDescriptionArr[128] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioSeekbar);
        themeDescriptionArr[129] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioSeekbar);
        themeDescriptionArr[130] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioSeekbarSelected);
        themeDescriptionArr[131] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioSeekbarSelected);
        themeDescriptionArr[132] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioSeekbarFill);
        themeDescriptionArr[133] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inAudioCacheSeekbar);
        themeDescriptionArr[134] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioSeekbarFill);
        themeDescriptionArr[135] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outAudioCacheSeekbar);
        themeDescriptionArr[136] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inVoiceSeekbar);
        themeDescriptionArr[137] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outVoiceSeekbar);
        themeDescriptionArr[138] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inVoiceSeekbarSelected);
        themeDescriptionArr[139] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outVoiceSeekbarSelected);
        themeDescriptionArr[140] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inVoiceSeekbarFill);
        themeDescriptionArr[141] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outVoiceSeekbarFill);
        themeDescriptionArr[142] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inFileProgress);
        themeDescriptionArr[143] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outFileProgress);
        themeDescriptionArr[144] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inFileProgressSelected);
        themeDescriptionArr[145] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outFileProgressSelected);
        themeDescriptionArr[146] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inFileNameText);
        themeDescriptionArr[147] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outFileNameText);
        themeDescriptionArr[148] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inFileInfoText);
        themeDescriptionArr[149] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outFileInfoText);
        themeDescriptionArr[150] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inFileInfoSelectedText);
        themeDescriptionArr[151] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outFileInfoSelectedText);
        themeDescriptionArr[152] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inFileBackground);
        themeDescriptionArr[153] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outFileBackground);
        themeDescriptionArr[154] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inFileBackgroundSelected);
        themeDescriptionArr[155] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outFileBackgroundSelected);
        themeDescriptionArr[156] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inVenueInfoText);
        themeDescriptionArr[157] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outVenueInfoText);
        themeDescriptionArr[158] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inVenueInfoSelectedText);
        themeDescriptionArr[159] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outVenueInfoSelectedText);
        themeDescriptionArr[160] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaInfoText);
        themeDescriptionArr[161] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_linkSelectBackground);
        themeDescriptionArr[162] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_textSelectBackground);
        themeDescriptionArr[163] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outLoader);
        themeDescriptionArr[164] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outMediaIcon);
        themeDescriptionArr[165] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outLoaderSelected);
        themeDescriptionArr[166] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outMediaIconSelected);
        themeDescriptionArr[167] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inLoader);
        themeDescriptionArr[168] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inMediaIcon);
        themeDescriptionArr[169] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inLoaderSelected);
        themeDescriptionArr[170] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inMediaIconSelected);
        themeDescriptionArr[171] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaLoaderPhoto);
        themeDescriptionArr[172] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaLoaderPhotoIcon);
        themeDescriptionArr[173] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaLoaderPhotoSelected);
        themeDescriptionArr[174] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaLoaderPhotoIconSelected);
        themeDescriptionArr[175] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outLoaderPhoto);
        themeDescriptionArr[176] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outLoaderPhotoIcon);
        themeDescriptionArr[177] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outLoaderPhotoSelected);
        themeDescriptionArr[178] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outLoaderPhotoIconSelected);
        themeDescriptionArr[179] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inLoaderPhoto);
        themeDescriptionArr[180] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inLoaderPhotoIcon);
        themeDescriptionArr[181] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inLoaderPhotoSelected);
        themeDescriptionArr[182] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inLoaderPhotoIconSelected);
        themeDescriptionArr[183] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[9][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outFileIcon);
        themeDescriptionArr[184] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[9][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outFileSelectedIcon);
        ImageView imageView = null;
        themeDescriptionArr[185] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[12][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inFileIcon);
        themeDescriptionArr[186] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[12][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inFileSelectedIcon);
        themeDescriptionArr[187] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inContactBackground);
        themeDescriptionArr[188] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inContactIcon);
        themeDescriptionArr[189] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outContactBackground);
        themeDescriptionArr[190] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outContactIcon);
        themeDescriptionArr[191] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_locationDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inLocationBackground);
        themeDescriptionArr[192] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_locationDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inLocationIcon);
        themeDescriptionArr[193] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_locationDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outLocationBackground);
        themeDescriptionArr[194] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_locationDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outLocationIcon);
        themeDescriptionArr[195] = new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[196] = new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[197] = new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_fieldOverlayText);
        themeDescriptionArr[198] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceText);
        themeDescriptionArr[199] = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceText);
        themeDescriptionArr[200] = new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_unreadMessagesStartBackground);
        themeDescriptionArr[201] = new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_unreadMessagesStartArrowIcon);
        themeDescriptionArr[202] = new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_unreadMessagesStartText);
        themeDescriptionArr[203] = new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[204] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[205] = new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[206] = new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceText);
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        themeDescriptionArr[207] = new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTimeItem() : null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_secretTimerBackground);
        ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
        if (chatAvatarContainer2 != null) {
            imageView = chatAvatarContainer2.getTimeItem();
        }
        themeDescriptionArr[208] = new ThemeDescription(imageView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_secretTimerText);
        return themeDescriptionArr;
    }
}
