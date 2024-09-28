package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.audioinfo.AudioInfo;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.AudioPlayerCell;
import im.bclpbkiauv.ui.components.AudioPlayerAlert;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class AudioPlayerAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, DownloadController.FileDownloadProgressListener {
    private int TAG;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private TextView authorTextView;
    /* access modifiers changed from: private */
    public ChatAvatarContainer avatarContainer;
    /* access modifiers changed from: private */
    public View[] buttons = new View[5];
    private TextView durationTextView;
    /* access modifiers changed from: private */
    public float endTranslation;
    /* access modifiers changed from: private */
    public float fullAnimationProgress;
    /* access modifiers changed from: private */
    public int hasNoCover;
    /* access modifiers changed from: private */
    public boolean hasOptions = true;
    /* access modifiers changed from: private */
    public boolean inFullSize;
    /* access modifiers changed from: private */
    public boolean isInFullMode;
    private int lastTime;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ActionBarMenuItem menuItem;
    /* access modifiers changed from: private */
    public Drawable noCoverDrawable;
    private ActionBarMenuItem optionsButton;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public float panelEndTranslation;
    private float panelStartTranslation;
    private LaunchActivity parentActivity;
    /* access modifiers changed from: private */
    public BackupImageView placeholderImageView;
    private ImageView playButton;
    private Drawable[] playOrderButtons = new Drawable[2];
    /* access modifiers changed from: private */
    public FrameLayout playerLayout;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> playlist;
    private LineProgressView progressView;
    private ImageView repeatButton;
    /* access modifiers changed from: private */
    public int scrollOffsetY = Integer.MAX_VALUE;
    /* access modifiers changed from: private */
    public boolean scrollToSong = true;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public int searchOpenOffset;
    /* access modifiers changed from: private */
    public int searchOpenPosition = -1;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private SeekBarView seekBarView;
    /* access modifiers changed from: private */
    public View shadow;
    private View shadow2;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private ActionBarMenuItem shuffleButton;
    private float startTranslation;
    /* access modifiers changed from: private */
    public float thumbMaxScale;
    /* access modifiers changed from: private */
    public int thumbMaxX;
    /* access modifiers changed from: private */
    public int thumbMaxY;
    private SimpleTextView timeTextView;
    private TextView titleTextView;
    private int topBeforeSwitch;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AudioPlayerAlert(android.content.Context r28) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
            r2 = 1
            r3 = 0
            r0.<init>(r1, r2, r3)
            r4 = 5
            android.view.View[] r4 = new android.view.View[r4]
            r0.buttons = r4
            r4 = 2
            android.graphics.drawable.Drawable[] r5 = new android.graphics.drawable.Drawable[r4]
            r0.playOrderButtons = r5
            r0.hasOptions = r2
            r0.scrollToSong = r2
            r5 = -1
            r0.searchOpenPosition = r5
            android.graphics.Paint r6 = new android.graphics.Paint
            r6.<init>(r2)
            r0.paint = r6
            r6 = 2147483647(0x7fffffff, float:NaN)
            r0.scrollOffsetY = r6
            im.bclpbkiauv.messenger.MediaController r6 = im.bclpbkiauv.messenger.MediaController.getInstance()
            im.bclpbkiauv.messenger.MessageObject r6 = r6.getPlayingMessageObject()
            if (r6 == 0) goto L_0x0035
            int r7 = r6.currentAccount
            r0.currentAccount = r7
            goto L_0x0039
        L_0x0035:
            int r7 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            r0.currentAccount = r7
        L_0x0039:
            r7 = r1
            im.bclpbkiauv.ui.LaunchActivity r7 = (im.bclpbkiauv.ui.LaunchActivity) r7
            r0.parentActivity = r7
            android.content.res.Resources r7 = r28.getResources()
            r8 = 2131231382(0x7f080296, float:1.8078843E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r0.noCoverDrawable = r7
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            java.lang.String r9 = "player_placeholder"
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r9, r10)
            r7.setColorFilter(r8)
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.DownloadController r7 = im.bclpbkiauv.messenger.DownloadController.getInstance(r7)
            int r7 = r7.generateObserverTag()
            r0.TAG = r7
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r7 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r7)
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingDidReset
            r7.addObserver(r0, r8)
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r7 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r7)
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingPlayStateChanged
            r7.addObserver(r0, r8)
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r7 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r7)
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingDidStart
            r7.addObserver(r0, r8)
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r7 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r7)
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingProgressDidChanged
            r7.addObserver(r0, r8)
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r7 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r7)
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.musicDidLoad
            r7.addObserver(r0, r8)
            android.content.res.Resources r7 = r28.getResources()
            r8 = 2131231572(0x7f080354, float:1.8079229E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r0.shadowDrawable = r7
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            java.lang.String r9 = "player_background"
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r10, r11)
            r7.setColorFilter(r8)
            android.graphics.Paint r7 = r0.paint
            java.lang.String r8 = "player_placeholderBackground"
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r7.setColor(r8)
            im.bclpbkiauv.ui.components.AudioPlayerAlert$1 r7 = new im.bclpbkiauv.ui.components.AudioPlayerAlert$1
            r7.<init>(r1)
            r0.containerView = r7
            android.view.ViewGroup r7 = r0.containerView
            r7.setWillNotDraw(r3)
            android.view.ViewGroup r7 = r0.containerView
            int r8 = r0.backgroundPaddingLeft
            int r10 = r0.backgroundPaddingLeft
            r7.setPadding(r8, r3, r10, r3)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = new im.bclpbkiauv.ui.actionbar.ActionBar
            r7.<init>(r1)
            r0.actionBar = r7
            java.lang.String r8 = "player_actionBar"
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r7.setBackgroundColor(r8)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            r8 = 2131558496(0x7f0d0060, float:1.874231E38)
            r7.setBackButtonImage(r8)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            java.lang.String r8 = "player_actionBarItems"
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r7.setItemsColor(r10, r3)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            java.lang.String r10 = "player_actionBarSelector"
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r7.setItemsBackgroundColor(r10, r3)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            java.lang.String r10 = "player_actionBarTitle"
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r7.setTitleColor(r11)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            java.lang.String r11 = "player_actionBarSubtitle"
            int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            r7.setSubtitleColor(r12)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            r12 = 0
            r7.setAlpha(r12)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            java.lang.String r13 = "1"
            r7.setTitle(r13)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            r7.setSubtitle(r13)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            im.bclpbkiauv.ui.actionbar.SimpleTextView r7 = r7.getTitleTextView()
            r7.setAlpha(r12)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            im.bclpbkiauv.ui.actionbar.SimpleTextView r7 = r7.getSubtitleTextView()
            r7.setAlpha(r12)
            im.bclpbkiauv.ui.components.ChatAvatarContainer r7 = new im.bclpbkiauv.ui.components.ChatAvatarContainer
            r13 = 0
            r7.<init>(r1, r13, r3)
            r0.avatarContainer = r7
            r7.setEnabled(r3)
            im.bclpbkiauv.ui.components.ChatAvatarContainer r7 = r0.avatarContainer
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            r7.setTitleColors(r14, r11)
            if (r6 == 0) goto L_0x01eb
            long r14 = r6.getDialogId()
            int r7 = (int) r14
            r11 = 32
            r16 = r6
            long r5 = r14 >> r11
            int r6 = (int) r5
            if (r7 == 0) goto L_0x01b6
            if (r7 <= 0) goto L_0x0198
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r7)
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r11)
            if (r5 == 0) goto L_0x0197
            im.bclpbkiauv.ui.components.ChatAvatarContainer r11 = r0.avatarContainer
            java.lang.String r13 = r5.first_name
            java.lang.String r12 = r5.last_name
            java.lang.String r12 = im.bclpbkiauv.messenger.ContactsController.formatName(r13, r12)
            r11.setTitle(r12)
            im.bclpbkiauv.ui.components.ChatAvatarContainer r11 = r0.avatarContainer
            r11.setUserAvatar(r5)
        L_0x0197:
            goto L_0x01ed
        L_0x0198:
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            int r11 = -r7
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r5.getChat(r11)
            if (r5 == 0) goto L_0x01b5
            im.bclpbkiauv.ui.components.ChatAvatarContainer r11 = r0.avatarContainer
            java.lang.String r12 = r5.title
            r11.setTitle(r12)
            im.bclpbkiauv.ui.components.ChatAvatarContainer r11 = r0.avatarContainer
            r11.setChatAvatar(r5)
        L_0x01b5:
            goto L_0x01ed
        L_0x01b6:
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r6)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r5 = r5.getEncryptedChat(r11)
            if (r5 == 0) goto L_0x01ed
            int r11 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r11 = im.bclpbkiauv.messenger.MessagesController.getInstance(r11)
            int r12 = r5.user_id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            im.bclpbkiauv.tgnet.TLRPC$User r11 = r11.getUser(r12)
            if (r11 == 0) goto L_0x01ed
            im.bclpbkiauv.ui.components.ChatAvatarContainer r12 = r0.avatarContainer
            java.lang.String r13 = r11.first_name
            java.lang.String r4 = r11.last_name
            java.lang.String r4 = im.bclpbkiauv.messenger.ContactsController.formatName(r13, r4)
            r12.setTitle(r4)
            im.bclpbkiauv.ui.components.ChatAvatarContainer r4 = r0.avatarContainer
            r4.setUserAvatar(r11)
            goto L_0x01ed
        L_0x01eb:
            r16 = r6
        L_0x01ed:
            im.bclpbkiauv.ui.components.ChatAvatarContainer r4 = r0.avatarContainer
            r5 = 2131689968(0x7f0f01f0, float:1.9008966E38)
            java.lang.String r6 = "AudioTitle"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
            r4.setSubtitle(r5)
            im.bclpbkiauv.ui.actionbar.ActionBar r4 = r0.actionBar
            im.bclpbkiauv.ui.components.ChatAvatarContainer r5 = r0.avatarContainer
            r18 = -1073741824(0xffffffffc0000000, float:-2.0)
            r19 = -1082130432(0xffffffffbf800000, float:-1.0)
            r20 = 51
            r21 = 1113587712(0x42600000, float:56.0)
            r22 = 0
            r23 = 1109393408(0x42200000, float:40.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r4.addView(r5, r3, r6)
            im.bclpbkiauv.ui.actionbar.ActionBar r4 = r0.actionBar
            im.bclpbkiauv.ui.actionbar.ActionBarMenu r4 = r4.createMenu()
            r5 = 2131231075(0x7f080163, float:1.807822E38)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r4.addItem((int) r3, (int) r5)
            r0.menuItem = r6
            r7 = 2131691348(0x7f0f0754, float:1.9011765E38)
            java.lang.String r11 = "Forward"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r7)
            r13 = 2131231306(0x7f08024a, float:1.807869E38)
            r6.addSubItem((int) r2, (int) r13, (java.lang.CharSequence) r12)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r0.menuItem
            r12 = 2131231344(0x7f080270, float:1.8078766E38)
            r14 = 2131693926(0x7f0f1166, float:1.9016994E38)
            java.lang.String r15 = "ShareFile"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r15 = 2
            r6.addSubItem((int) r15, (int) r12, (java.lang.CharSequence) r14)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r0.menuItem
            r12 = 2131231323(0x7f08025b, float:1.8078724E38)
            r14 = 2131693966(0x7f0f118e, float:1.9017075E38)
            java.lang.String r15 = "ShowInChat"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r15 = 4
            r6.addSubItem((int) r15, (int) r12, (java.lang.CharSequence) r14)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r0.menuItem
            r12 = 2131689518(0x7f0f002e, float:1.9008054E38)
            java.lang.String r14 = "AccDescrMoreOptions"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r12)
            r6.setContentDescription(r12)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r0.menuItem
            r12 = 1111490560(0x42400000, float:48.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r6.setTranslationX(r12)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r0.menuItem
            r12 = 0
            r6.setAlpha(r12)
            r6 = 2131231078(0x7f080166, float:1.8078227E38)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r4.addItem((int) r3, (int) r6)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r6.setIsSearchField(r2)
            im.bclpbkiauv.ui.components.AudioPlayerAlert$2 r12 = new im.bclpbkiauv.ui.components.AudioPlayerAlert$2
            r12.<init>()
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r6.setActionBarMenuItemSearchListener(r12)
            r0.searchItem = r6
            r12 = 2131693714(0x7f0f1092, float:1.9016564E38)
            java.lang.String r14 = "Search"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r12)
            r6.setContentDescription(r12)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r6 = r0.searchItem
            im.bclpbkiauv.ui.components.EditTextBoldCursor r6 = r6.getSearchField()
            r12 = 2131693714(0x7f0f1092, float:1.9016564E38)
            java.lang.String r14 = "Search"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r12)
            r6.setHint(r12)
            int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r6.setTextColor(r12)
            java.lang.String r12 = "player_time"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r6.setHintTextColor(r14)
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r6.setCursorColor(r14)
            boolean r14 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r14 != 0) goto L_0x02d7
            im.bclpbkiauv.ui.actionbar.ActionBar r14 = r0.actionBar
            r14.showActionModeTop()
            im.bclpbkiauv.ui.actionbar.ActionBar r14 = r0.actionBar
            java.lang.String r18 = "player_actionBarTop"
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r18)
            r14.setActionModeTopColor(r15)
        L_0x02d7:
            im.bclpbkiauv.ui.actionbar.ActionBar r14 = r0.actionBar
            im.bclpbkiauv.ui.components.AudioPlayerAlert$3 r15 = new im.bclpbkiauv.ui.components.AudioPlayerAlert$3
            r15.<init>()
            r14.setActionBarMenuOnItemClick(r15)
            android.view.View r14 = new android.view.View
            r14.<init>(r1)
            r0.shadow = r14
            r15 = 0
            r14.setAlpha(r15)
            android.view.View r14 = r0.shadow
            r13 = 2131231071(0x7f08015f, float:1.8078213E38)
            r14.setBackgroundResource(r13)
            android.view.View r13 = new android.view.View
            r13.<init>(r1)
            r0.shadow2 = r13
            r13.setAlpha(r15)
            android.view.View r13 = r0.shadow2
            r14 = 2131231071(0x7f08015f, float:1.8078213E38)
            r13.setBackgroundResource(r14)
            android.widget.FrameLayout r13 = new android.widget.FrameLayout
            r13.<init>(r1)
            r0.playerLayout = r13
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            r13.setBackgroundColor(r9)
            im.bclpbkiauv.ui.components.AudioPlayerAlert$4 r9 = new im.bclpbkiauv.ui.components.AudioPlayerAlert$4
            r9.<init>(r1)
            r0.placeholderImageView = r9
            r13 = 1101004800(0x41a00000, float:20.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            r9.setRoundRadius(r13)
            im.bclpbkiauv.ui.components.BackupImageView r9 = r0.placeholderImageView
            r13 = 0
            r9.setPivotX(r13)
            im.bclpbkiauv.ui.components.BackupImageView r9 = r0.placeholderImageView
            r9.setPivotY(r13)
            im.bclpbkiauv.ui.components.BackupImageView r9 = r0.placeholderImageView
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$MwCpCx5K3LQYXcbDn1wqsCzJ6bs r13 = new im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$MwCpCx5K3LQYXcbDn1wqsCzJ6bs
            r13.<init>()
            r9.setOnClickListener(r13)
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            r0.titleTextView = r9
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r9.setTextColor(r10)
            android.widget.TextView r9 = r0.titleTextView
            r10 = 1097859072(0x41700000, float:15.0)
            r9.setTextSize(r2, r10)
            android.widget.TextView r9 = r0.titleTextView
            java.lang.String r10 = "fonts/rmedium.ttf"
            android.graphics.Typeface r10 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r10)
            r9.setTypeface(r10)
            android.widget.TextView r9 = r0.titleTextView
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r9.setEllipsize(r10)
            android.widget.TextView r9 = r0.titleTextView
            r9.setSingleLine(r2)
            android.widget.FrameLayout r9 = r0.playerLayout
            android.widget.TextView r10 = r0.titleTextView
            r20 = -1082130432(0xffffffffbf800000, float:-1.0)
            r21 = -1073741824(0xffffffffc0000000, float:-2.0)
            r22 = 51
            r23 = 1116733440(0x42900000, float:72.0)
            r24 = 1099956224(0x41900000, float:18.0)
            r25 = 1114636288(0x42700000, float:60.0)
            r26 = 0
            android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r9.addView(r10, r13)
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            r0.authorTextView = r9
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r9.setTextColor(r10)
            android.widget.TextView r9 = r0.authorTextView
            r10 = 1096810496(0x41600000, float:14.0)
            r9.setTextSize(r2, r10)
            android.widget.TextView r9 = r0.authorTextView
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r9.setEllipsize(r10)
            android.widget.TextView r9 = r0.authorTextView
            r9.setSingleLine(r2)
            android.widget.FrameLayout r9 = r0.playerLayout
            android.widget.TextView r10 = r0.authorTextView
            r24 = 1109393408(0x42200000, float:40.0)
            android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r9.addView(r10, r13)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r9 = new im.bclpbkiauv.ui.actionbar.ActionBarMenuItem
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r10 = 0
            r9.<init>(r1, r10, r3, r8)
            r0.optionsButton = r9
            r9.setLongClickEnabled(r3)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r8 = r0.optionsButton
            r8.setIcon((int) r5)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r5 = r0.optionsButton
            r8 = 1123024896(0x42f00000, float:120.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r8 = -r8
            r5.setAdditionalYOffset(r8)
            android.widget.FrameLayout r5 = r0.playerLayout
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r8 = r0.optionsButton
            r20 = 1109393408(0x42200000, float:40.0)
            r21 = 1109393408(0x42200000, float:40.0)
            r22 = 53
            r23 = 0
            r24 = 1100480512(0x41980000, float:19.0)
            r25 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r5.addView(r8, r9)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r5 = r0.optionsButton
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r7)
            r8 = 2131231306(0x7f08024a, float:1.807869E38)
            r5.addSubItem((int) r2, (int) r8, (java.lang.CharSequence) r7)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r5 = r0.optionsButton
            r7 = 2131231344(0x7f080270, float:1.8078766E38)
            r8 = 2131693926(0x7f0f1166, float:1.9016994E38)
            java.lang.String r9 = "ShareFile"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
            r9 = 2
            r5.addSubItem((int) r9, (int) r7, (java.lang.CharSequence) r8)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r5 = r0.optionsButton
            r7 = 2131231323(0x7f08025b, float:1.8078724E38)
            r8 = 2131693966(0x7f0f118e, float:1.9017075E38)
            java.lang.String r9 = "ShowInChat"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
            r9 = 4
            r5.addSubItem((int) r9, (int) r7, (java.lang.CharSequence) r8)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r5 = r0.optionsButton
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$n_4KtB6--QxunUFsYj7ddJwTUEQ r7 = new im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$n_4KtB6--QxunUFsYj7ddJwTUEQ
            r7.<init>()
            r5.setOnClickListener(r7)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r5 = r0.optionsButton
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$P7b099MLWV2AamhGMIZvkearawg r7 = new im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$P7b099MLWV2AamhGMIZvkearawg
            r7.<init>()
            r5.setDelegate(r7)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r5 = r0.optionsButton
            r7 = 2131689518(0x7f0f002e, float:1.9008054E38)
            java.lang.String r8 = "AccDescrMoreOptions"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r5.setContentDescription(r7)
            im.bclpbkiauv.ui.components.SeekBarView r5 = new im.bclpbkiauv.ui.components.SeekBarView
            r5.<init>(r1)
            r0.seekBarView = r5
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$_9wr7c4h5x1d8vOyAbImFiRXqLU r7 = im.bclpbkiauv.ui.components.$$Lambda$AudioPlayerAlert$_9wr7c4h5x1d8vOyAbImFiRXqLU.INSTANCE
            r5.setDelegate(r7)
            android.widget.FrameLayout r5 = r0.playerLayout
            im.bclpbkiauv.ui.components.SeekBarView r7 = r0.seekBarView
            r20 = -1082130432(0xffffffffbf800000, float:-1.0)
            r21 = 1106247680(0x41f00000, float:30.0)
            r22 = 51
            r23 = 1090519040(0x41000000, float:8.0)
            r24 = 1115160576(0x42780000, float:62.0)
            r25 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r5.addView(r7, r8)
            im.bclpbkiauv.ui.components.LineProgressView r5 = new im.bclpbkiauv.ui.components.LineProgressView
            r5.<init>(r1)
            r0.progressView = r5
            r7 = 4
            r5.setVisibility(r7)
            im.bclpbkiauv.ui.components.LineProgressView r5 = r0.progressView
            java.lang.String r7 = "player_progressBackground"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r5.setBackgroundColor(r7)
            im.bclpbkiauv.ui.components.LineProgressView r5 = r0.progressView
            java.lang.String r7 = "player_progress"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r5.setProgressColor(r7)
            android.widget.FrameLayout r5 = r0.playerLayout
            im.bclpbkiauv.ui.components.LineProgressView r7 = r0.progressView
            r21 = 1073741824(0x40000000, float:2.0)
            r23 = 1101004800(0x41a00000, float:20.0)
            r24 = 1117519872(0x429c0000, float:78.0)
            r25 = 1101004800(0x41a00000, float:20.0)
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r5.addView(r7, r8)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r5 = new im.bclpbkiauv.ui.actionbar.SimpleTextView
            r5.<init>(r1)
            r0.timeTextView = r5
            r7 = 12
            r5.setTextSize(r7)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r5 = r0.timeTextView
            java.lang.String r7 = "0:00"
            r5.setText(r7)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r5 = r0.timeTextView
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r5.setTextColor(r7)
            android.widget.FrameLayout r5 = r0.playerLayout
            im.bclpbkiauv.ui.actionbar.SimpleTextView r7 = r0.timeTextView
            r20 = 1120403456(0x42c80000, float:100.0)
            r21 = -1073741824(0xffffffffc0000000, float:-2.0)
            r24 = 1119354880(0x42b80000, float:92.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r5.addView(r7, r8)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r0.durationTextView = r5
            r7 = 1094713344(0x41400000, float:12.0)
            r5.setTextSize(r2, r7)
            android.widget.TextView r5 = r0.durationTextView
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r5.setTextColor(r7)
            android.widget.TextView r5 = r0.durationTextView
            r7 = 17
            r5.setGravity(r7)
            android.widget.FrameLayout r5 = r0.playerLayout
            android.widget.TextView r7 = r0.durationTextView
            r8 = -1073741824(0xffffffffc0000000, float:-2.0)
            r9 = -1073741824(0xffffffffc0000000, float:-2.0)
            r10 = 53
            r11 = 0
            r12 = 1119092736(0x42b40000, float:90.0)
            r13 = 1101004800(0x41a00000, float:20.0)
            r14 = 0
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r5.addView(r7, r8)
            im.bclpbkiauv.ui.components.AudioPlayerAlert$6 r5 = new im.bclpbkiauv.ui.components.AudioPlayerAlert$6
            r5.<init>(r1)
            android.widget.FrameLayout r7 = r0.playerLayout
            r8 = -1082130432(0xffffffffbf800000, float:-1.0)
            r9 = 1115947008(0x42840000, float:66.0)
            r10 = 51
            r12 = 1121189888(0x42d40000, float:106.0)
            r13 = 0
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r7.addView(r5, r8)
            android.view.View[] r7 = r0.buttons
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r8 = new im.bclpbkiauv.ui.actionbar.ActionBarMenuItem
            r9 = 0
            r8.<init>(r1, r9, r3, r3)
            r0.shuffleButton = r8
            r7[r3] = r8
            r8.setLongClickEnabled(r3)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r7 = r0.shuffleButton
            r8 = 1092616192(0x41200000, float:10.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r8 = -r8
            r7.setAdditionalYOffset(r8)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r7 = r0.shuffleButton
            r8 = 51
            r9 = 48
            android.widget.FrameLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r8)
            r5.addView(r7, r10)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r7 = r0.shuffleButton
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$eC0TcYGMBxY_CQt9O0-6GYGVCKU r10 = new im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$eC0TcYGMBxY_CQt9O0-6GYGVCKU
            r10.<init>()
            r7.setOnClickListener(r10)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r7 = r0.shuffleButton
            r10 = 2131693532(0x7f0f0fdc, float:1.9016195E38)
            java.lang.String r11 = "ReverseOrder"
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            android.widget.TextView r7 = r7.addSubItem(r2, r10)
            r10 = 1090519040(0x41000000, float:8.0)
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r12 = 1098907648(0x41800000, float:16.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            r7.setPadding(r11, r3, r12, r3)
            android.graphics.drawable.Drawable[] r11 = r0.playOrderButtons
            android.content.res.Resources r12 = r28.getResources()
            r13 = 2131231359(0x7f08027f, float:1.8078797E38)
            android.graphics.drawable.Drawable r12 = r12.getDrawable(r13)
            android.graphics.drawable.Drawable r12 = r12.mutate()
            r11[r3] = r12
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r7.setCompoundDrawablePadding(r11)
            android.graphics.drawable.Drawable[] r11 = r0.playOrderButtons
            r11 = r11[r3]
            r12 = 0
            r7.setCompoundDrawablesWithIntrinsicBounds(r11, r12, r12, r12)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r11 = r0.shuffleButton
            r12 = 2131693971(0x7f0f1193, float:1.9017085E38)
            java.lang.String r13 = "Shuffle"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r12)
            r13 = 2
            android.widget.TextView r7 = r11.addSubItem(r13, r12)
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r12 = 1098907648(0x41800000, float:16.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            r7.setPadding(r11, r3, r12, r3)
            android.graphics.drawable.Drawable[] r11 = r0.playOrderButtons
            android.content.res.Resources r12 = r28.getResources()
            r13 = 2131231453(0x7f0802dd, float:1.8078987E38)
            android.graphics.drawable.Drawable r12 = r12.getDrawable(r13)
            android.graphics.drawable.Drawable r12 = r12.mutate()
            r11[r2] = r12
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r7.setCompoundDrawablePadding(r11)
            android.graphics.drawable.Drawable[] r11 = r0.playOrderButtons
            r11 = r11[r2]
            r12 = 0
            r7.setCompoundDrawablesWithIntrinsicBounds(r11, r12, r12, r12)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r11 = r0.shuffleButton
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$8RpAS6YowKp7Y9gA62C0kviFt4U r12 = new im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$8RpAS6YowKp7Y9gA62C0kviFt4U
            r12.<init>()
            r11.setDelegate(r12)
            android.view.View[] r11 = r0.buttons
            android.widget.ImageView r12 = new android.widget.ImageView
            r12.<init>(r1)
            r13 = r12
            r11[r2] = r12
            android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.CENTER
            r13.setScaleType(r11)
            r11 = 2131231450(0x7f0802da, float:1.8078981E38)
            java.lang.String r12 = "player_button"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            java.lang.String r15 = "player_buttonActive"
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.createSimpleSelectorDrawable(r1, r11, r14, r2)
            r13.setImageDrawable(r2)
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r8)
            r5.addView(r13, r2)
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$Xxm2j3lWao5EOHV_XLM5F0T6Ch8 r2 = im.bclpbkiauv.ui.components.$$Lambda$AudioPlayerAlert$Xxm2j3lWao5EOHV_XLM5F0T6Ch8.INSTANCE
            r13.setOnClickListener(r2)
            r2 = 2131689534(0x7f0f003e, float:1.9008086E38)
            java.lang.String r11 = "AccDescrPrevious"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r2)
            r13.setContentDescription(r2)
            android.view.View[] r2 = r0.buttons
            android.widget.ImageView r11 = new android.widget.ImageView
            r11.<init>(r1)
            r0.playButton = r11
            r14 = 2
            r2[r14] = r11
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r11.setScaleType(r2)
            android.widget.ImageView r2 = r0.playButton
            r11 = 2131231449(0x7f0802d9, float:1.807898E38)
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.createSimpleSelectorDrawable(r1, r11, r14, r3)
            r2.setImageDrawable(r3)
            android.widget.ImageView r2 = r0.playButton
            android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r8)
            r5.addView(r2, r3)
            android.widget.ImageView r2 = r0.playButton
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$FuTfwq1-Ge_Su5jUSoP70H7S4uw r3 = im.bclpbkiauv.ui.components.$$Lambda$AudioPlayerAlert$FuTfwq1Ge_Su5jUSoP70H7S4uw.INSTANCE
            r2.setOnClickListener(r3)
            android.view.View[] r2 = r0.buttons
            r3 = 3
            android.widget.ImageView r11 = new android.widget.ImageView
            r11.<init>(r1)
            r14 = r11
            r2[r3] = r11
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r14.setScaleType(r2)
            r2 = 2131231447(0x7f0802d7, float:1.8078975E38)
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.createSimpleSelectorDrawable(r1, r2, r3, r11)
            r14.setImageDrawable(r2)
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r8)
            r5.addView(r14, r2)
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$KG1GZS2H5QsWYMkGbH3J1P5csvo r2 = im.bclpbkiauv.ui.components.$$Lambda$AudioPlayerAlert$KG1GZS2H5QsWYMkGbH3J1P5csvo.INSTANCE
            r14.setOnClickListener(r2)
            r2 = 2131692179(0x7f0f0a93, float:1.901345E38)
            java.lang.String r3 = "Next"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r14.setContentDescription(r2)
            android.view.View[] r2 = r0.buttons
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r1)
            r0.repeatButton = r3
            r11 = 4
            r2[r11] = r3
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r2)
            android.widget.ImageView r2 = r0.repeatButton
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r11 = 0
            r2.setPadding(r11, r11, r3, r11)
            android.widget.ImageView r2 = r0.repeatButton
            r3 = 50
            android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r3, (int) r9, (int) r8)
            r5.addView(r2, r3)
            android.widget.ImageView r2 = r0.repeatButton
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$jJ7xt6dYjWVoiQMbvTsuzXeoBF8 r3 = new im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$jJ7xt6dYjWVoiQMbvTsuzXeoBF8
            r3.<init>()
            r2.setOnClickListener(r3)
            im.bclpbkiauv.ui.components.AudioPlayerAlert$7 r2 = new im.bclpbkiauv.ui.components.AudioPlayerAlert$7
            r2.<init>(r1)
            r0.listView = r2
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r9 = 0
            r2.setPadding(r9, r9, r9, r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.listView
            r2.setClipToPadding(r9)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.listView
            androidx.recyclerview.widget.LinearLayoutManager r3 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r10 = r27.getContext()
            r11 = 1
            r3.<init>(r10, r11, r9)
            r0.layoutManager = r3
            r2.setLayoutManager(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.listView
            r2.setHorizontalScrollBarEnabled(r9)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.listView
            r2.setVerticalScrollBarEnabled(r9)
            android.view.ViewGroup r2 = r0.containerView
            im.bclpbkiauv.ui.components.RecyclerListView r3 = r0.listView
            r9 = -1
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r8)
            r2.addView(r3, r8)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.listView
            im.bclpbkiauv.ui.components.AudioPlayerAlert$ListAdapter r3 = new im.bclpbkiauv.ui.components.AudioPlayerAlert$ListAdapter
            r3.<init>(r1)
            r0.listAdapter = r3
            r2.setAdapter(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.listView
            java.lang.String r3 = "dialogScrollGlow"
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r2.setGlowColor(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.listView
            im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$Q7y5Rnb2HVc114wO3TlO0RxxCs0 r3 = im.bclpbkiauv.ui.components.$$Lambda$AudioPlayerAlert$Q7y5Rnb2HVc114wO3TlO0RxxCs0.INSTANCE
            r2.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r0.listView
            im.bclpbkiauv.ui.components.AudioPlayerAlert$8 r3 = new im.bclpbkiauv.ui.components.AudioPlayerAlert$8
            r3.<init>()
            r2.setOnScrollListener(r3)
            im.bclpbkiauv.messenger.MediaController r2 = im.bclpbkiauv.messenger.MediaController.getInstance()
            java.util.ArrayList r2 = r2.getPlaylist()
            r0.playlist = r2
            im.bclpbkiauv.ui.components.AudioPlayerAlert$ListAdapter r2 = r0.listAdapter
            r2.notifyDataSetChanged()
            android.view.ViewGroup r2 = r0.containerView
            android.widget.FrameLayout r3 = r0.playerLayout
            r8 = 1127350272(0x43320000, float:178.0)
            r9 = -1
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r9, r8)
            r2.addView(r3, r8)
            android.view.ViewGroup r2 = r0.containerView
            android.view.View r3 = r0.shadow2
            r8 = 1077936128(0x40400000, float:3.0)
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r9, r8)
            r2.addView(r3, r8)
            android.view.ViewGroup r2 = r0.containerView
            im.bclpbkiauv.ui.components.BackupImageView r3 = r0.placeholderImageView
            r17 = 1109393408(0x42200000, float:40.0)
            r18 = 1109393408(0x42200000, float:40.0)
            r19 = 51
            r20 = 1099431936(0x41880000, float:17.0)
            r21 = 1100480512(0x41980000, float:19.0)
            r22 = 0
            r23 = 0
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r2.addView(r3, r8)
            android.view.ViewGroup r2 = r0.containerView
            android.view.View r3 = r0.shadow
            r8 = 1077936128(0x40400000, float:3.0)
            r9 = -1
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r9, r8)
            r2.addView(r3, r8)
            android.view.ViewGroup r2 = r0.containerView
            im.bclpbkiauv.ui.actionbar.ActionBar r3 = r0.actionBar
            r2.addView(r3)
            r2 = 0
            r0.updateTitle(r2)
            r27.updateRepeatButton()
            r27.updateShuffleButton()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.AudioPlayerAlert.<init>(android.content.Context):void");
    }

    public /* synthetic */ void lambda$new$0$AudioPlayerAlert(View view) {
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        this.animatorSet = new AnimatorSet();
        float f = 0.0f;
        if (this.scrollOffsetY <= this.actionBar.getMeasuredHeight()) {
            AnimatorSet animatorSet3 = this.animatorSet;
            Animator[] animatorArr = new Animator[1];
            float[] fArr = new float[1];
            if (!this.isInFullMode) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(this, "fullAnimationProgress", fArr);
            animatorSet3.playTogether(animatorArr);
        } else {
            AnimatorSet animatorSet4 = this.animatorSet;
            Animator[] animatorArr2 = new Animator[4];
            float[] fArr2 = new float[1];
            fArr2[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr2[0] = ObjectAnimator.ofFloat(this, "fullAnimationProgress", fArr2);
            ActionBar actionBar2 = this.actionBar;
            float[] fArr3 = new float[1];
            fArr3[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr2[1] = ObjectAnimator.ofFloat(actionBar2, "alpha", fArr3);
            View view2 = this.shadow;
            float[] fArr4 = new float[1];
            fArr4[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr2[2] = ObjectAnimator.ofFloat(view2, "alpha", fArr4);
            View view3 = this.shadow2;
            float[] fArr5 = new float[1];
            if (!this.isInFullMode) {
                f = 1.0f;
            }
            fArr5[0] = f;
            animatorArr2[3] = ObjectAnimator.ofFloat(view3, "alpha", fArr5);
            animatorSet4.playTogether(animatorArr2);
        }
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(250);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(AudioPlayerAlert.this.animatorSet)) {
                    if (!AudioPlayerAlert.this.isInFullMode) {
                        AudioPlayerAlert.this.listView.setScrollEnabled(true);
                        if (AudioPlayerAlert.this.hasOptions) {
                            AudioPlayerAlert.this.menuItem.setVisibility(4);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(0);
                    } else {
                        if (AudioPlayerAlert.this.hasOptions) {
                            AudioPlayerAlert.this.menuItem.setVisibility(0);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(4);
                    }
                    AnimatorSet unused = AudioPlayerAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        if (this.hasOptions) {
            this.menuItem.setVisibility(0);
        }
        this.searchItem.setVisibility(0);
        this.isInFullMode = !this.isInFullMode;
        this.listView.setScrollEnabled(false);
        if (this.isInFullMode) {
            this.shuffleButton.setAdditionalYOffset(-AndroidUtilities.dp(68.0f));
        } else {
            this.shuffleButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        }
    }

    public /* synthetic */ void lambda$new$1$AudioPlayerAlert(View v) {
        this.optionsButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$3$AudioPlayerAlert(View v) {
        this.shuffleButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$4$AudioPlayerAlert(int id) {
        MediaController.getInstance().toggleShuffleMusic(id);
        updateShuffleButton();
        this.listAdapter.notifyDataSetChanged();
    }

    static /* synthetic */ void lambda$new$6(View v) {
        if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    public /* synthetic */ void lambda$new$8$AudioPlayerAlert(View v) {
        SharedConfig.toggleRepeatMode();
        updateRepeatButton();
    }

    static /* synthetic */ void lambda$new$9(View view, int position) {
        if (view instanceof AudioPlayerCell) {
            ((AudioPlayerCell) view).didPressedButton();
        }
    }

    public void setFullAnimationProgress(float value) {
        this.fullAnimationProgress = value;
        this.placeholderImageView.setRoundRadius(AndroidUtilities.dp((1.0f - value) * 20.0f));
        float scale = (this.thumbMaxScale * this.fullAnimationProgress) + 1.0f;
        this.placeholderImageView.setScaleX(scale);
        this.placeholderImageView.setScaleY(scale);
        float translationY = this.placeholderImageView.getTranslationY();
        this.placeholderImageView.setTranslationX(((float) this.thumbMaxX) * this.fullAnimationProgress);
        BackupImageView backupImageView = this.placeholderImageView;
        float f = this.startTranslation;
        backupImageView.setTranslationY(f + ((this.endTranslation - f) * this.fullAnimationProgress));
        FrameLayout frameLayout = this.playerLayout;
        float f2 = this.panelStartTranslation;
        frameLayout.setTranslationY(f2 + ((this.panelEndTranslation - f2) * this.fullAnimationProgress));
        View view = this.shadow2;
        float f3 = this.panelStartTranslation;
        view.setTranslationY(f3 + ((this.panelEndTranslation - f3) * this.fullAnimationProgress) + ((float) this.playerLayout.getMeasuredHeight()));
        this.menuItem.setAlpha(this.fullAnimationProgress);
        this.searchItem.setAlpha(1.0f - this.fullAnimationProgress);
        this.avatarContainer.setAlpha(1.0f - this.fullAnimationProgress);
        this.actionBar.getTitleTextView().setAlpha(this.fullAnimationProgress);
        this.actionBar.getSubtitleTextView().setAlpha(this.fullAnimationProgress);
    }

    public float getFullAnimationProgress() {
        return this.fullAnimationProgress;
    }

    /* access modifiers changed from: private */
    public void onSubItemClick(int id) {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject != null && this.parentActivity != null) {
            if (id == 1) {
                if (UserConfig.selectedAccount != this.currentAccount) {
                    this.parentActivity.switchToAccount(this.currentAccount, true);
                }
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 3);
                DialogsActivity fragment = new DialogsActivity(args);
                ArrayList<MessageObject> fmessages = new ArrayList<>();
                fmessages.add(messageObject);
                fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate(fmessages) {
                    private final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                        AudioPlayerAlert.this.lambda$onSubItemClick$10$AudioPlayerAlert(this.f$1, dialogsActivity, arrayList, charSequence, z);
                    }
                });
                this.parentActivity.lambda$runLinkRequest$28$LaunchActivity(fragment);
                dismiss();
            } else if (id == 2) {
                File f = null;
                try {
                    if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                        f = new File(messageObject.messageOwner.attachPath);
                        if (!f.exists()) {
                            f = null;
                        }
                    }
                    if (f == null) {
                        f = FileLoader.getPathToMessage(messageObject.messageOwner);
                    }
                    if (f.exists()) {
                        Intent intent = new Intent("android.intent.action.SEND");
                        if (messageObject != null) {
                            intent.setType(messageObject.getMimeType());
                        } else {
                            intent.setType("audio/mp3");
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            try {
                                intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ApplicationLoader.applicationContext, "im.bclpbkiauv.messenger.provider", f));
                                intent.setFlags(1);
                            } catch (Exception e) {
                                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                            }
                        } else {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                        }
                        this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                    builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                    builder.show();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            } else if (id == 4) {
                if (UserConfig.selectedAccount != this.currentAccount) {
                    this.parentActivity.switchToAccount(this.currentAccount, true);
                }
                Bundle args2 = new Bundle();
                long did = messageObject.getDialogId();
                int lower_part = (int) did;
                int high_id = (int) (did >> 32);
                if (lower_part == 0) {
                    args2.putInt("enc_id", high_id);
                } else if (lower_part > 0) {
                    args2.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_part));
                    if (!(chat == null || chat.migrated_to == null)) {
                        args2.putInt("migrated_to", lower_part);
                        lower_part = -chat.migrated_to.channel_id;
                    }
                    args2.putInt("chat_id", -lower_part);
                }
                args2.putInt("message_id", messageObject.getId());
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                this.parentActivity.presentFragment(new ChatActivity(args2), false, false);
                dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$onSubItemClick$10$AudioPlayerAlert(ArrayList fmessages, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        ArrayList arrayList = dids;
        if (dids.size() > 1 || ((Long) arrayList.get(0)).longValue() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId())) {
            ArrayList arrayList2 = fmessages;
        } else if (message != null) {
            ArrayList arrayList3 = fmessages;
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            ChatActivity chatActivity = new ChatActivity(args1);
            if (this.parentActivity.presentFragment(chatActivity, true, false)) {
                chatActivity.showFieldPanelForForward(true, fmessages);
                return;
            }
            ArrayList arrayList4 = fmessages;
            fragment1.finishFragment();
            return;
        }
        for (int a = 0; a < dids.size(); a++) {
            long did2 = ((Long) arrayList.get(a)).longValue();
            if (message != null) {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(message.toString(), did2, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            }
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(fmessages, did2, true, 0);
        }
        fragment1.finishFragment();
    }

    private int getCurrentTop() {
        if (this.listView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.listView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
            i = child.getTop();
        }
        return paddingTop - i;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        AudioPlayerCell cell;
        MessageObject messageObject;
        AudioPlayerCell cell2;
        MessageObject messageObject1;
        if (id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged || id == NotificationCenter.messagePlayingDidReset) {
            updateTitle(id == NotificationCenter.messagePlayingDidReset && args[1].booleanValue());
            if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = this.listView.getChildAt(a);
                    if ((view instanceof AudioPlayerCell) && (messageObject = cell.getMessageObject()) != null && (messageObject.isVoice() || messageObject.isMusic())) {
                        (cell = (AudioPlayerCell) view).updateButtonState(false, true);
                    }
                }
            } else if (id == NotificationCenter.messagePlayingDidStart && args[0].eventId == 0) {
                int count2 = this.listView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View view2 = this.listView.getChildAt(a2);
                    if ((view2 instanceof AudioPlayerCell) && (messageObject1 = cell2.getMessageObject()) != null && (messageObject1.isVoice() || messageObject1.isMusic())) {
                        (cell2 = (AudioPlayerCell) view2).updateButtonState(false, true);
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            MessageObject messageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject2 != null && messageObject2.isMusic()) {
                updateProgress(messageObject2);
            }
        } else if (id == NotificationCenter.musicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.listView.getChildCount() > 0) {
            View child = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
            int top = child.getTop();
            int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (this.searchWas || this.searching) {
                newOffset = 0;
            }
            if (this.scrollOffsetY != newOffset) {
                RecyclerListView recyclerListView = this.listView;
                this.scrollOffsetY = newOffset;
                recyclerListView.setTopGlowOffset(newOffset);
                this.playerLayout.setTranslationY((float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
                this.placeholderImageView.setTranslationY((float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
                this.shadow2.setTranslationY((float) (Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY) + this.playerLayout.getMeasuredHeight()));
                this.containerView.invalidate();
                if ((!this.inFullSize || this.scrollOffsetY > this.actionBar.getMeasuredHeight()) && !this.searchWas) {
                    if (this.actionBar.getTag() != null) {
                        AnimatorSet animatorSet2 = this.actionBarAnimation;
                        if (animatorSet2 != null) {
                            animatorSet2.cancel();
                        }
                        this.actionBar.setTag((Object) null);
                        AnimatorSet animatorSet3 = new AnimatorSet();
                        this.actionBarAnimation = animatorSet3;
                        animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.shadow, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[]{0.0f})});
                        this.actionBarAnimation.setDuration(180);
                        this.actionBarAnimation.start();
                    }
                } else if (this.actionBar.getTag() == null) {
                    AnimatorSet animatorSet4 = this.actionBarAnimation;
                    if (animatorSet4 != null) {
                        animatorSet4.cancel();
                    }
                    this.actionBar.setTag(1);
                    AnimatorSet animatorSet5 = new AnimatorSet();
                    this.actionBarAnimation = animatorSet5;
                    animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{1.0f}), ObjectAnimator.ofFloat(this.shadow, "alpha", new float[]{1.0f}), ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[]{1.0f})});
                    this.actionBarAnimation.setDuration(180);
                    this.actionBarAnimation.start();
                }
            }
            this.startTranslation = (float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
            this.panelStartTranslation = (float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
        }
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoad);
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    public void onBackPressed() {
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 == null || !actionBar2.isSearchFieldVisible()) {
            super.onBackPressed();
        } else {
            this.actionBar.closeSearchField();
        }
    }

    public void onFailedDownload(String fileName, boolean canceled) {
    }

    public void onSuccessDownload(String fileName) {
    }

    public void onProgressDownload(String fileName, float progress) {
        this.progressView.setProgress(progress, true);
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateShuffleButton() {
        boolean z = SharedConfig.shuffleMusic;
        String str = Theme.key_player_button;
        if (z) {
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.pl_shuffle).mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), PorterDuff.Mode.MULTIPLY));
            this.shuffleButton.setIcon(drawable);
            this.shuffleButton.setContentDescription(LocaleController.getString("Shuffle", R.string.Shuffle));
        } else {
            Drawable drawable2 = getContext().getResources().getDrawable(R.drawable.music_reverse).mutate();
            if (SharedConfig.playOrderReversed) {
                drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), PorterDuff.Mode.MULTIPLY));
            } else {
                drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
            }
            this.shuffleButton.setIcon(drawable2);
            this.shuffleButton.setContentDescription(LocaleController.getString("ReverseOrder", R.string.ReverseOrder));
        }
        this.playOrderButtons[0].setColorFilter(new PorterDuffColorFilter(Theme.getColor(SharedConfig.playOrderReversed ? Theme.key_player_buttonActive : str), PorterDuff.Mode.MULTIPLY));
        Drawable drawable3 = this.playOrderButtons[1];
        if (SharedConfig.shuffleMusic) {
            str = Theme.key_player_buttonActive;
        }
        drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
    }

    private void updateRepeatButton() {
        int mode = SharedConfig.repeatMode;
        if (mode == 0) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat);
            this.repeatButton.setTag(Theme.key_player_button);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_button), PorterDuff.Mode.MULTIPLY));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOff", R.string.AccDescrRepeatOff));
        } else if (mode == 1) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat);
            this.repeatButton.setTag(Theme.key_player_buttonActive);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), PorterDuff.Mode.MULTIPLY));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatList", R.string.AccDescrRepeatList));
        } else if (mode == 2) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat1);
            this.repeatButton.setTag(Theme.key_player_buttonActive);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), PorterDuff.Mode.MULTIPLY));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOne", R.string.AccDescrRepeatOne));
        }
    }

    private void updateProgress(MessageObject messageObject) {
        SeekBarView seekBarView2 = this.seekBarView;
        if (seekBarView2 != null) {
            if (!seekBarView2.isDragging()) {
                this.seekBarView.setProgress(messageObject.audioProgress);
                this.seekBarView.setBufferedProgress(messageObject.bufferedProgress);
            }
            if (this.lastTime != messageObject.audioProgressSec) {
                this.lastTime = messageObject.audioProgressSec;
                this.timeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(messageObject.audioProgressSec / 60), Integer.valueOf(messageObject.audioProgressSec % 60)}));
            }
        }
    }

    private void checkIfMusicDownloaded(MessageObject messageObject) {
        File cacheFile = null;
        if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
            cacheFile = new File(messageObject.messageOwner.attachPath);
            if (!cacheFile.exists()) {
                cacheFile = null;
            }
        }
        if (cacheFile == null) {
            cacheFile = FileLoader.getPathToMessage(messageObject.messageOwner);
        }
        boolean canStream = SharedConfig.streamMedia && ((int) messageObject.getDialogId()) != 0 && messageObject.isMusic();
        if (cacheFile.exists() || canStream) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.progressView.setVisibility(4);
            this.seekBarView.setVisibility(0);
            this.playButton.setEnabled(true);
            return;
        }
        String fileName = messageObject.getFileName();
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
        Float progress = ImageLoader.getInstance().getFileProgress(fileName);
        this.progressView.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
        this.progressView.setVisibility(0);
        this.seekBarView.setVisibility(4);
        this.playButton.setEnabled(false);
    }

    private void updateTitle(boolean shutdown) {
        String str;
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if ((messageObject == null && shutdown) || (messageObject != null && !messageObject.isMusic())) {
            dismiss();
        } else if (messageObject != null) {
            if (messageObject.eventId != 0 || messageObject.getId() <= -2000000000) {
                this.hasOptions = false;
                this.menuItem.setVisibility(4);
                this.optionsButton.setVisibility(4);
            } else {
                this.hasOptions = true;
                if (!this.actionBar.isSearchFieldVisible()) {
                    this.menuItem.setVisibility(0);
                }
                this.optionsButton.setVisibility(0);
            }
            checkIfMusicDownloaded(messageObject);
            updateProgress(messageObject);
            if (MediaController.getInstance().isMessagePaused()) {
                ImageView imageView = this.playButton;
                imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(imageView.getContext(), R.drawable.pl_play, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
            } else {
                ImageView imageView2 = this.playButton;
                imageView2.setImageDrawable(Theme.createSimpleSelectorDrawable(imageView2.getContext(), R.drawable.pl_pause, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", R.string.AccActionPause));
            }
            String title = messageObject.getMusicTitle();
            String author = messageObject.getMusicAuthor();
            this.titleTextView.setText(title);
            this.authorTextView.setText(author);
            this.actionBar.setTitle(title);
            this.actionBar.setSubtitle(author);
            String str2 = author + " " + title;
            AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
            if (audioInfo == null || audioInfo.getCover() == null) {
                String artworkUrl = messageObject.getArtworkUrl(false);
                if (!TextUtils.isEmpty(artworkUrl)) {
                    this.placeholderImageView.setImage(artworkUrl, (String) null, (Drawable) null);
                    this.hasNoCover = 2;
                } else {
                    this.placeholderImageView.setImageDrawable((Drawable) null);
                    this.hasNoCover = 1;
                }
                this.placeholderImageView.invalidate();
            } else {
                this.hasNoCover = 0;
                this.placeholderImageView.setImageBitmap(audioInfo.getCover());
            }
            if (this.durationTextView != null) {
                int duration = messageObject.getDuration();
                TextView textView = this.durationTextView;
                if (duration != 0) {
                    str = String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)});
                } else {
                    str = "-:--";
                }
                textView.setText(str);
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private ArrayList<MessageObject> searchResult = new ArrayList<>();
        /* access modifiers changed from: private */
        public Timer searchTimer;

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            if (AudioPlayerAlert.this.searchWas) {
                return this.searchResult.size();
            }
            if (AudioPlayerAlert.this.searching) {
                return AudioPlayerAlert.this.playlist.size();
            }
            return AudioPlayerAlert.this.playlist.size() + 1;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return AudioPlayerAlert.this.searchWas || holder.getAdapterPosition() > 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new AudioPlayerCell(this.context);
            } else {
                view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(178.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                AudioPlayerCell cell = (AudioPlayerCell) holder.itemView;
                if (AudioPlayerAlert.this.searchWas) {
                    cell.setMessageObject(this.searchResult.get(position));
                } else if (AudioPlayerAlert.this.searching) {
                    if (SharedConfig.playOrderReversed) {
                        cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(position));
                    } else {
                        cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get((AudioPlayerAlert.this.playlist.size() - position) - 1));
                    }
                } else if (position <= 0) {
                } else {
                    if (SharedConfig.playOrderReversed) {
                        cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(position - 1));
                    } else {
                        cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(AudioPlayerAlert.this.playlist.size() - position));
                    }
                }
            }
        }

        public int getItemViewType(int i) {
            if (AudioPlayerAlert.this.searchWas || AudioPlayerAlert.this.searching || i != 0) {
                return 1;
            }
            return 0;
        }

        public void search(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (query == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            Timer timer = new Timer();
            this.searchTimer = timer;
            timer.schedule(new TimerTask() {
                public void run() {
                    try {
                        ListAdapter.this.searchTimer.cancel();
                        Timer unused = ListAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    ListAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        /* access modifiers changed from: private */
        public void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$processSearch$1$AudioPlayerAlert$ListAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$1$AudioPlayerAlert$ListAdapter(String query) {
            Utilities.searchQueue.postRunnable(new Runnable(query, new ArrayList<>(AudioPlayerAlert.this.playlist)) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$null$0$AudioPlayerAlert$ListAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$AudioPlayerAlert$ListAdapter(String query, ArrayList copy) {
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
                        }
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
                    AudioPlayerAlert.ListAdapter.this.lambda$updateSearchResults$2$AudioPlayerAlert$ListAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$2$AudioPlayerAlert$ListAdapter(ArrayList documents) {
            boolean unused = AudioPlayerAlert.this.searchWas = true;
            this.searchResult = documents;
            notifyDataSetChanged();
            AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
        }
    }
}
