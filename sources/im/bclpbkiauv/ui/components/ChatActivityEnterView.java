package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.os.BuildCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.VideoEditedInfo;
import im.bclpbkiauv.messenger.camera.CameraController;
import im.bclpbkiauv.messenger.utils.RegexUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.GroupStickersActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.StickersActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem;
import im.bclpbkiauv.ui.actionbar.ActionBarPopupWindow;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.BotKeyboardView;
import im.bclpbkiauv.ui.components.ChatActivityEnterView;
import im.bclpbkiauv.ui.components.EditTextCaption;
import im.bclpbkiauv.ui.components.EmojiView;
import im.bclpbkiauv.ui.components.EnterMenuView;
import im.bclpbkiauv.ui.components.SeekBar;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.components.TextStyleSpan;
import im.bclpbkiauv.ui.components.VideoTimelineView;
import im.bclpbkiauv.ui.components.mentionspan.MentionSpanWatcher;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.constants.ChatEnterMenuType;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.NoCopySpanEditableFactory;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hviews.MryRoundButtonDrawable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivityEnterView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate {
    /* access modifiers changed from: private */
    public AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
    private ImageView attachButton;
    private ArrayList<Integer> attachIcons = new ArrayList<>();
    /* access modifiers changed from: private */
    public LinearLayout attachLayout;
    private ArrayList<String> attachTexts = new ArrayList<>();
    private ArrayList<ChatEnterMenuType> attachTypes = new ArrayList<>();
    /* access modifiers changed from: private */
    public ImageView audioSendButton;
    private TLRPC.TL_document audioToSend;
    /* access modifiers changed from: private */
    public MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    /* access modifiers changed from: private */
    public AnimatorSet audioVideoButtonAnimation;
    /* access modifiers changed from: private */
    public FrameLayout audioVideoButtonContainer;
    /* access modifiers changed from: private */
    public ImageView botButton;
    /* access modifiers changed from: private */
    public MessageObject botButtonsMessageObject;
    private int botCount;
    private BotKeyboardView botKeyboardView;
    private MessageObject botMessageObject;
    private TLRPC.TL_replyKeyboardMarkup botReplyMarkup;
    /* access modifiers changed from: private */
    public boolean calledRecordRunnable;
    /* access modifiers changed from: private */
    public Drawable cameraDrawable;
    /* access modifiers changed from: private */
    public boolean canWriteToChannel;
    /* access modifiers changed from: private */
    public ImageView cancelBotButton;
    /* access modifiers changed from: private */
    public boolean closeAnimationInProgress;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private int currentEmojiIcon = -1;
    /* access modifiers changed from: private */
    public int currentPopupContentType = -1;
    private Animator currentResizeAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet currentTopViewAnimation;
    /* access modifiers changed from: private */
    public ChatActivityEnterViewDelegate delegate;
    /* access modifiers changed from: private */
    public boolean destroyed;
    /* access modifiers changed from: private */
    public long dialog_id;
    private float distCanMove = ((float) AndroidUtilities.dp(80.0f));
    /* access modifiers changed from: private */
    public AnimatorSet doneButtonAnimation;
    private FrameLayout doneButtonContainer;
    /* access modifiers changed from: private */
    public ImageView doneButtonImage;
    /* access modifiers changed from: private */
    public ContextProgressView doneButtonProgress;
    /* access modifiers changed from: private */
    public Paint dotPaint;
    private boolean editingCaption;
    /* access modifiers changed from: private */
    public MessageObject editingMessageObject;
    private int editingMessageReqId;
    /* access modifiers changed from: private */
    public ImageView[] emojiButton = new ImageView[2];
    /* access modifiers changed from: private */
    public AnimatorSet emojiButtonAnimation;
    private int emojiPadding;
    /* access modifiers changed from: private */
    public boolean emojiTabOpen;
    /* access modifiers changed from: private */
    public EmojiView emojiView;
    /* access modifiers changed from: private */
    public boolean emojiViewVisible;
    /* access modifiers changed from: private */
    public ImageView expandStickersButton;
    private Runnable focusRunnable;
    private boolean forceShowSendButton;
    private boolean gifsTabOpen;
    private boolean hasBotCommands;
    private boolean hasRecordVideo;
    /* access modifiers changed from: private */
    public boolean ignoreTextChange;
    /* access modifiers changed from: private */
    public Drawable inactinveSendButtonDrawable;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public int innerTextChange;
    private boolean isPaused = true;
    /* access modifiers changed from: private */
    public int keyboardHeight;
    /* access modifiers changed from: private */
    public int keyboardHeightLand;
    /* access modifiers changed from: private */
    public boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private String lastTimeString;
    private long lastTypingSendTime;
    /* access modifiers changed from: private */
    public long lastTypingTimeSend;
    /* access modifiers changed from: private */
    public Drawable lockArrowDrawable;
    /* access modifiers changed from: private */
    public Drawable lockBackgroundDrawable;
    /* access modifiers changed from: private */
    public Drawable lockDrawable;
    /* access modifiers changed from: private */
    public Drawable lockShadowDrawable;
    /* access modifiers changed from: private */
    public Drawable lockTopDrawable;
    private View.AccessibilityDelegate mediaMessageButtonsDelegate = new View.AccessibilityDelegate() {
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setClassName("android.widget.ImageButton");
            info.setClickable(true);
            info.setLongClickable(true);
        }
    };
    private EnterMenuView menuView;
    private boolean menuViewVisible;
    /* access modifiers changed from: private */
    public EditTextCaption messageEditText;
    private TLRPC.WebPage messageWebPage;
    /* access modifiers changed from: private */
    public boolean messageWebPageSearch = true;
    /* access modifiers changed from: private */
    public Drawable micDrawable;
    private boolean needShowTopView;
    private ImageView notifyButton;
    /* access modifiers changed from: private */
    public Runnable onFinishInitCameraRunnable = new Runnable() {
        public void run() {
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.needStartRecordVideo(0, true, 0);
            }
        }
    };
    /* access modifiers changed from: private */
    public Runnable openKeyboardRunnable = new Runnable() {
        public void run() {
            if (!ChatActivityEnterView.this.destroyed && ChatActivityEnterView.this.messageEditText != null && ChatActivityEnterView.this.waitingForKeyboardOpen && !ChatActivityEnterView.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow) {
                ChatActivityEnterView.this.messageEditText.requestFocus();
                AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
                AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100);
            }
        }
    };
    private int originalViewHeight;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public Paint paintRecord = new Paint(1);
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
    private Drawable pauseDrawable;
    private TLRPC.KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    private Drawable playDrawable;
    private CloseProgressDrawable2 progressDrawable;
    private Runnable recordAudioVideoRunnable = new Runnable() {
        public void run() {
            if (ChatActivityEnterView.this.delegate != null && ChatActivityEnterView.this.parentActivity != null) {
                ChatActivityEnterView.this.delegate.onPreAudioVideoRecord();
                boolean unused = ChatActivityEnterView.this.calledRecordRunnable = true;
                boolean unused2 = ChatActivityEnterView.this.recordAudioVideoRunnableStarted = false;
                ChatActivityEnterView.this.recordCircle.setLockTranslation(10000.0f);
                ChatActivityEnterView.this.recordSendText.setAlpha(0.0f);
                ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                ChatActivityEnterView.this.slideText.setTranslationY(0.0f);
                if (ChatActivityEnterView.this.videoSendButton != null && ChatActivityEnterView.this.videoSendButton.getTag() != null) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        boolean hasAudio = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0;
                        boolean hasVideo = ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.CAMERA") == 0;
                        if (!hasAudio || !hasVideo) {
                            String[] permissions = new String[((hasAudio || hasVideo) ? 1 : 2)];
                            if (!hasAudio && !hasVideo) {
                                permissions[0] = "android.permission.RECORD_AUDIO";
                                permissions[1] = "android.permission.CAMERA";
                            } else if (!hasAudio) {
                                permissions[0] = "android.permission.RECORD_AUDIO";
                            } else {
                                permissions[0] = "android.permission.CAMERA";
                            }
                            ChatActivityEnterView.this.parentActivity.requestPermissions(permissions, 3);
                            return;
                        }
                    }
                    if (!CameraController.getInstance().isCameraInitied()) {
                        CameraController.getInstance().initCamera(ChatActivityEnterView.this.onFinishInitCameraRunnable);
                    } else {
                        ChatActivityEnterView.this.onFinishInitCameraRunnable.run();
                    }
                } else if (ChatActivityEnterView.this.parentFragment == null || Build.VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                    ChatActivityEnterView.this.delegate.needStartRecordAudio(1);
                    float unused3 = ChatActivityEnterView.this.startedDraggingX = -1.0f;
                    MediaController.getInstance().startRecording(ChatActivityEnterView.this.currentAccount, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.recordingGuid);
                    ChatActivityEnterView.this.updateRecordIntefrace();
                    ChatActivityEnterView.this.audioVideoButtonContainer.getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean recordAudioVideoRunnableStarted;
    private ImageView recordCancelImage;
    private TextView recordCancelText;
    /* access modifiers changed from: private */
    public RecordCircle recordCircle;
    private Property<RecordCircle, Float> recordCircleScale = new Property<RecordCircle, Float>(Float.class, "scale") {
        public Float get(RecordCircle object) {
            return Float.valueOf(object.getScale());
        }

        public void set(RecordCircle object, Float value) {
            object.setScale(value.floatValue());
        }
    };
    private ImageView recordDeleteImageView;
    private RecordDot recordDot;
    private int recordInterfaceState;
    /* access modifiers changed from: private */
    public FrameLayout recordPanel;
    /* access modifiers changed from: private */
    public TextView recordSendText;
    private LinearLayout recordTimeContainer;
    private TextView recordTimeText;
    private View recordedAudioBackground;
    /* access modifiers changed from: private */
    public FrameLayout recordedAudioPanel;
    private ImageView recordedAudioPlayButton;
    private SeekBarWaveformView recordedAudioSeekBar;
    private TextView recordedAudioTimeTextView;
    private boolean recordingAudioVideo;
    /* access modifiers changed from: private */
    public int recordingGuid;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public Paint redDotPaint = new Paint(1);
    /* access modifiers changed from: private */
    public MessageObject replyingMessageObject;
    private Property<View, Integer> roundedTranslationYProperty = new Property<View, Integer>(Integer.class, "translationY") {
        public Integer get(View object) {
            return Integer.valueOf(Math.round(object.getTranslationY()));
        }

        public void set(View object, Integer value) {
            object.setTranslationY((float) value.intValue());
        }
    };
    /* access modifiers changed from: private */
    public AnimatorSet runningAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet runningAnimation2;
    /* access modifiers changed from: private */
    public AnimatorSet runningAnimationAudio;
    /* access modifiers changed from: private */
    public int runningAnimationType;
    private boolean scheduleButtonHidden;
    /* access modifiers changed from: private */
    public ImageView scheduledButton;
    /* access modifiers changed from: private */
    public AnimatorSet scheduledButtonAnimation;
    /* access modifiers changed from: private */
    public int searchingType;
    /* access modifiers changed from: private */
    public SeekBarWaveform seekBarWaveform;
    /* access modifiers changed from: private */
    public View sendButton;
    private FrameLayout sendButtonContainer;
    /* access modifiers changed from: private */
    public Drawable sendButtonDrawable;
    /* access modifiers changed from: private */
    public Drawable sendButtonInverseDrawable;
    /* access modifiers changed from: private */
    public boolean sendByEnter;
    /* access modifiers changed from: private */
    public Drawable sendDrawable;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private boolean showKeyboardOnResume;
    private boolean silent;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout sizeNotifierLayout;
    /* access modifiers changed from: private */
    public LinearLayout slideText;
    /* access modifiers changed from: private */
    public SimpleTextView slowModeButton;
    /* access modifiers changed from: private */
    public int slowModeTimer;
    /* access modifiers changed from: private */
    public float startedDraggingX = -1.0f;
    private AnimatedArrowDrawable stickersArrow;
    /* access modifiers changed from: private */
    public boolean stickersDragging;
    /* access modifiers changed from: private */
    public boolean stickersExpanded;
    /* access modifiers changed from: private */
    public int stickersExpandedHeight;
    /* access modifiers changed from: private */
    public Animator stickersExpansionAnim;
    /* access modifiers changed from: private */
    public float stickersExpansionProgress;
    /* access modifiers changed from: private */
    public boolean stickersTabOpen;
    private LinearLayout textFieldContainer;
    /* access modifiers changed from: private */
    public View topLineView;
    /* access modifiers changed from: private */
    public View topView;
    private boolean topViewShowed;
    /* access modifiers changed from: private */
    public Runnable updateExpandabilityRunnable = new Runnable() {
        private int lastKnownPage = -1;

        public void run() {
            int curPage;
            if (ChatActivityEnterView.this.emojiView != null && (curPage = ChatActivityEnterView.this.emojiView.getCurrentPage()) != this.lastKnownPage) {
                this.lastKnownPage = curPage;
                boolean prevOpen = ChatActivityEnterView.this.stickersTabOpen;
                int i = 2;
                boolean unused = ChatActivityEnterView.this.stickersTabOpen = curPage == 1 || curPage == 2;
                boolean prevOpen2 = ChatActivityEnterView.this.emojiTabOpen;
                boolean unused2 = ChatActivityEnterView.this.emojiTabOpen = curPage == 0;
                if (ChatActivityEnterView.this.stickersExpanded) {
                    if (!ChatActivityEnterView.this.stickersTabOpen && ChatActivityEnterView.this.searchingType == 0) {
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            int unused3 = ChatActivityEnterView.this.searchingType = 0;
                            ChatActivityEnterView.this.emojiView.closeSearch(true);
                            ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                        }
                        ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                    } else if (ChatActivityEnterView.this.searchingType != 0) {
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        if (curPage != 0) {
                            i = 1;
                        }
                        int unused4 = chatActivityEnterView.searchingType = i;
                        ChatActivityEnterView.this.checkStickresExpandHeight();
                    }
                }
                if (prevOpen != ChatActivityEnterView.this.stickersTabOpen || prevOpen2 != ChatActivityEnterView.this.emojiTabOpen) {
                    ChatActivityEnterView.this.checkSendButton(true);
                }
            }
        }
    };
    private Runnable updateSlowModeRunnable;
    /* access modifiers changed from: private */
    public ImageView videoSendButton;
    private VideoTimelineView videoTimelineView;
    /* access modifiers changed from: private */
    public VideoEditedInfo videoToSendMessageObject;
    /* access modifiers changed from: private */
    public boolean waitingForKeyboardOpen;
    private PowerManager.WakeLock wakeLock;

    public interface ChatActivityEnterViewDelegate {
        void didPressedAttachButton(int i, ChatEnterMenuType chatEnterMenuType);

        boolean hasScheduledMessages();

        void needChangeVideoPreviewState(int i, float f);

        void needSendTyping();

        void needShowMediaBanHint();

        void needStartRecordAudio(int i);

        void needStartRecordVideo(int i, boolean z, int i2);

        void onAttachButtonHidden();

        void onAttachButtonShow();

        void onMessageEditEnd(boolean z);

        void onMessageSend(CharSequence charSequence, boolean z, int i);

        void onPreAudioVideoRecord();

        void onStickersExpandedChange();

        void onStickersTab(boolean z);

        void onSwitchRecordMode(boolean z);

        void onTextChanged(CharSequence charSequence, boolean z);

        void onTextSelectionChanged(int i, int i2);

        void onTextSpansChanged(CharSequence charSequence);

        void onUpdateSlowModeButton(View view, boolean z, CharSequence charSequence);

        void onWindowSizeChanged(int i);

        void openScheduledMessages();

        void scrollToSendingMessage();

        /* renamed from: im.bclpbkiauv.ui.components.ChatActivityEnterView$ChatActivityEnterViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$scrollToSendingMessage(ChatActivityEnterViewDelegate _this) {
            }

            public static void $default$openScheduledMessages(ChatActivityEnterViewDelegate _this) {
            }

            public static boolean $default$hasScheduledMessages(ChatActivityEnterViewDelegate _this) {
                return true;
            }
        }
    }

    private class SeekBarWaveformView extends View {
        public SeekBarWaveformView(Context context) {
            super(context);
            SeekBarWaveform unused = ChatActivityEnterView.this.seekBarWaveform = new SeekBarWaveform(context);
            ChatActivityEnterView.this.seekBarWaveform.setDelegate(new SeekBar.SeekBarDelegate() {
                public /* synthetic */ void onSeekBarContinuousDrag(float f) {
                    SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
                }

                public final void onSeekBarDrag(float f) {
                    ChatActivityEnterView.SeekBarWaveformView.this.lambda$new$0$ChatActivityEnterView$SeekBarWaveformView(f);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$ChatActivityEnterView$SeekBarWaveformView(float progress) {
            if (ChatActivityEnterView.this.audioToSendMessageObject != null) {
                ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = progress;
                MediaController.getInstance().seekToProgress(ChatActivityEnterView.this.audioToSendMessageObject, progress);
            }
        }

        public void setWaveform(byte[] waveform) {
            ChatActivityEnterView.this.seekBarWaveform.setWaveform(waveform);
            invalidate();
        }

        public void setProgress(float progress) {
            ChatActivityEnterView.this.seekBarWaveform.setProgress(progress);
            invalidate();
        }

        public boolean isDragging() {
            return ChatActivityEnterView.this.seekBarWaveform.isDragging();
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean result = ChatActivityEnterView.this.seekBarWaveform.onTouch(event.getAction(), event.getX(), event.getY());
            if (result) {
                if (event.getAction() == 0) {
                    ChatActivityEnterView.this.requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
            }
            if (result || super.onTouchEvent(event)) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            ChatActivityEnterView.this.seekBarWaveform.setSize(right - left, bottom - top);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ChatActivityEnterView.this.seekBarWaveform.setColors(Theme.getColor(Theme.key_chat_recordedVoiceProgress), Theme.getColor(Theme.key_chat_recordedVoiceProgressInner), Theme.getColor(Theme.key_chat_recordedVoiceProgress));
            ChatActivityEnterView.this.seekBarWaveform.draw(canvas);
        }
    }

    private class RecordDot extends View {
        private float alpha;
        private boolean isIncr;
        private long lastUpdateTime;

        public RecordDot(Context context) {
            super(context);
            ChatActivityEnterView.this.redDotPaint.setColor(Theme.getColor(Theme.key_chat_recordedVoiceDot));
        }

        public void resetAlpha() {
            this.alpha = 1.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            this.isIncr = false;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            ChatActivityEnterView.this.redDotPaint.setAlpha((int) (this.alpha * 255.0f));
            long dt = System.currentTimeMillis() - this.lastUpdateTime;
            if (!this.isIncr) {
                float f = this.alpha - (((float) dt) / 400.0f);
                this.alpha = f;
                if (f <= 0.0f) {
                    this.alpha = 0.0f;
                    this.isIncr = true;
                }
            } else {
                float f2 = this.alpha + (((float) dt) / 400.0f);
                this.alpha = f2;
                if (f2 >= 1.0f) {
                    this.alpha = 1.0f;
                    this.isIncr = false;
                }
            }
            this.lastUpdateTime = System.currentTimeMillis();
            canvas.drawCircle((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.redDotPaint);
            invalidate();
        }
    }

    private class RecordCircle extends View {
        private float amplitude;
        private float animateAmplitudeDiff;
        private float animateToAmplitude;
        private long lastUpdateTime;
        private float lockAnimatedTranslation;
        private boolean pressed;
        private float scale;
        private boolean sendButtonVisible;
        /* access modifiers changed from: private */
        public float startTranslation;
        private VirtualViewHelper virtualViewHelper;

        public RecordCircle(Context context) {
            super(context);
            ChatActivityEnterView.this.paint.setColor(Theme.getColor(Theme.key_chat_messagePanelVoiceBackground));
            ChatActivityEnterView.this.paintRecord.setColor(Theme.getColor(Theme.key_chat_messagePanelVoiceShadow));
            Drawable unused = ChatActivityEnterView.this.lockDrawable = getResources().getDrawable(R.drawable.lock_middle);
            ChatActivityEnterView.this.lockDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLock), PorterDuff.Mode.MULTIPLY));
            Drawable unused2 = ChatActivityEnterView.this.lockTopDrawable = getResources().getDrawable(R.drawable.lock_top);
            ChatActivityEnterView.this.lockTopDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLock), PorterDuff.Mode.MULTIPLY));
            Drawable unused3 = ChatActivityEnterView.this.lockArrowDrawable = getResources().getDrawable(R.drawable.lock_arrow);
            ChatActivityEnterView.this.lockArrowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLock), PorterDuff.Mode.MULTIPLY));
            Drawable unused4 = ChatActivityEnterView.this.lockBackgroundDrawable = getResources().getDrawable(R.drawable.lock_round);
            ChatActivityEnterView.this.lockBackgroundDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLockBackground), PorterDuff.Mode.MULTIPLY));
            Drawable unused5 = ChatActivityEnterView.this.lockShadowDrawable = getResources().getDrawable(R.drawable.lock_round_shadow);
            ChatActivityEnterView.this.lockShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceLockShadow), PorterDuff.Mode.MULTIPLY));
            Drawable unused6 = ChatActivityEnterView.this.micDrawable = getResources().getDrawable(R.drawable.input_mic).mutate();
            ChatActivityEnterView.this.micDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.SRC_IN));
            Drawable unused7 = ChatActivityEnterView.this.cameraDrawable = getResources().getDrawable(R.drawable.input_video).mutate();
            ChatActivityEnterView.this.cameraDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY));
            Drawable unused8 = ChatActivityEnterView.this.sendDrawable = getResources().getDrawable(R.drawable.ic_send).mutate();
            ChatActivityEnterView.this.sendDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY));
            VirtualViewHelper virtualViewHelper2 = new VirtualViewHelper(this);
            this.virtualViewHelper = virtualViewHelper2;
            ViewCompat.setAccessibilityDelegate(this, virtualViewHelper2);
        }

        public void setAmplitude(double value) {
            float min = ((float) Math.min(100.0d, value)) / 100.0f;
            this.animateToAmplitude = min;
            this.animateAmplitudeDiff = (min - this.amplitude) / 150.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }

        public float getScale() {
            return this.scale;
        }

        public void setScale(float value) {
            this.scale = value;
            invalidate();
        }

        public void setLockAnimatedTranslation(float value) {
            this.lockAnimatedTranslation = value;
            invalidate();
        }

        public float getLockAnimatedTranslation() {
            return this.lockAnimatedTranslation;
        }

        public boolean isSendButtonVisible() {
            return this.sendButtonVisible;
        }

        public void setSendButtonInvisible() {
            this.sendButtonVisible = false;
            invalidate();
        }

        public int setLockTranslation(float value) {
            if (value == 10000.0f) {
                this.sendButtonVisible = false;
                this.lockAnimatedTranslation = -1.0f;
                this.startTranslation = -1.0f;
                invalidate();
                return 0;
            } else if (this.sendButtonVisible) {
                return 2;
            } else {
                if (this.lockAnimatedTranslation == -1.0f) {
                    this.startTranslation = value;
                }
                this.lockAnimatedTranslation = value;
                invalidate();
                if (this.startTranslation - this.lockAnimatedTranslation < ((float) AndroidUtilities.dp(57.0f))) {
                    return 1;
                }
                this.sendButtonVisible = true;
                return 2;
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (this.sendButtonVisible) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (event.getAction() == 0) {
                    boolean contains = ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(x, y);
                    this.pressed = contains;
                    return contains;
                } else if (this.pressed) {
                    if (event.getAction() == 1 && ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains(x, y)) {
                        if (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) {
                            MediaController.getInstance().stopRecording(2, true, 0);
                            ChatActivityEnterView.this.delegate.needStartRecordAudio(0);
                        } else {
                            ChatActivityEnterView.this.delegate.needStartRecordVideo(3, true, 0);
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float sc;
            float alpha;
            Drawable drawable;
            int lockMiddleY;
            int lockTopY;
            int lockSize;
            int lockY;
            int lockArrowY;
            Canvas canvas2 = canvas;
            int cx = getMeasuredWidth() / 2;
            int cy = AndroidUtilities.dp(170.0f);
            float yAdd = 0.0f;
            float f = this.lockAnimatedTranslation;
            if (f != 10000.0f) {
                yAdd = (float) Math.max(0, (int) (this.startTranslation - f));
                if (yAdd > ((float) AndroidUtilities.dp(57.0f))) {
                    yAdd = (float) AndroidUtilities.dp(57.0f);
                }
            }
            int cy2 = (int) (((float) cy) - yAdd);
            float f2 = this.scale;
            if (f2 <= 0.5f) {
                alpha = f2 / 0.5f;
                sc = alpha;
            } else if (f2 <= 0.75f) {
                sc = 1.0f - (((f2 - 0.5f) / 0.25f) * 0.1f);
                alpha = 1.0f;
            } else {
                sc = 0.9f + (((f2 - 0.75f) / 0.25f) * 0.1f);
                alpha = 1.0f;
            }
            long dt = System.currentTimeMillis() - this.lastUpdateTime;
            float f3 = this.animateToAmplitude;
            float f4 = this.amplitude;
            if (f3 != f4) {
                float f5 = this.animateAmplitudeDiff;
                float f6 = f4 + (((float) dt) * f5);
                this.amplitude = f6;
                if (f5 > 0.0f) {
                    if (f6 > f3) {
                        this.amplitude = f3;
                    }
                } else if (f6 < f3) {
                    this.amplitude = f3;
                }
                invalidate();
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (this.amplitude != 0.0f) {
                canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) cy2, (((float) AndroidUtilities.dp(42.0f)) + (((float) AndroidUtilities.dp(20.0f)) * this.amplitude)) * this.scale, ChatActivityEnterView.this.paintRecord);
            }
            canvas2.drawCircle(((float) getMeasuredWidth()) / 2.0f, (float) cy2, ((float) AndroidUtilities.dp(42.0f)) * sc, ChatActivityEnterView.this.paint);
            if (isSendButtonVisible()) {
                drawable = ChatActivityEnterView.this.sendDrawable;
            } else {
                drawable = (ChatActivityEnterView.this.videoSendButton == null || ChatActivityEnterView.this.videoSendButton.getTag() == null) ? ChatActivityEnterView.this.micDrawable : ChatActivityEnterView.this.cameraDrawable;
            }
            drawable.setBounds(cx - (drawable.getIntrinsicWidth() / 2), cy2 - (drawable.getIntrinsicHeight() / 2), (drawable.getIntrinsicWidth() / 2) + cx, (drawable.getIntrinsicHeight() / 2) + cy2);
            drawable.setAlpha((int) (alpha * 255.0f));
            drawable.draw(canvas2);
            float moveProgress = 1.0f - (yAdd / ((float) AndroidUtilities.dp(57.0f)));
            float moveProgress2 = Math.max(0.0f, 1.0f - ((yAdd / ((float) AndroidUtilities.dp(57.0f))) * 2.0f));
            int intAlpha = (int) (255.0f * alpha);
            if (isSendButtonVisible()) {
                lockSize = AndroidUtilities.dp(31.0f);
                int lockY2 = AndroidUtilities.dp(57.0f) + ((int) (((((float) AndroidUtilities.dp(30.0f)) * (1.0f - sc)) - yAdd) + (((float) AndroidUtilities.dp(20.0f)) * moveProgress)));
                lockTopY = lockY2 + AndroidUtilities.dp(5.0f);
                lockMiddleY = lockY2 + AndroidUtilities.dp(11.0f);
                int i = cy2;
                float f7 = alpha;
                intAlpha = (int) (((float) intAlpha) * (yAdd / ((float) AndroidUtilities.dp(57.0f))));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(255);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) intAlpha) * moveProgress2));
                lockArrowY = lockY2 + AndroidUtilities.dp(25.0f);
                lockY = lockY2;
            } else {
                float f8 = alpha;
                lockSize = AndroidUtilities.dp(31.0f) + ((int) (((float) AndroidUtilities.dp(29.0f)) * moveProgress));
                int lockY3 = (AndroidUtilities.dp(57.0f) + ((int) (((float) AndroidUtilities.dp(30.0f)) * (1.0f - sc)))) - ((int) yAdd);
                lockTopY = lockY3 + AndroidUtilities.dp(5.0f) + ((int) (((float) AndroidUtilities.dp(4.0f)) * moveProgress));
                lockMiddleY = lockY3 + AndroidUtilities.dp(11.0f) + ((int) (((float) AndroidUtilities.dp(10.0f)) * moveProgress));
                int lockArrowY2 = lockY3 + AndroidUtilities.dp(25.0f) + ((int) (((float) AndroidUtilities.dp(16.0f)) * moveProgress));
                ChatActivityEnterView.this.lockBackgroundDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockShadowDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockTopDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockDrawable.setAlpha(intAlpha);
                ChatActivityEnterView.this.lockArrowDrawable.setAlpha((int) (((float) intAlpha) * moveProgress2));
                lockArrowY = lockArrowY2;
                lockY = lockY3;
            }
            float f9 = yAdd;
            Drawable drawable2 = drawable;
            float f10 = sc;
            int i2 = intAlpha;
            ChatActivityEnterView.this.lockBackgroundDrawable.setBounds(cx - AndroidUtilities.dp(15.0f), lockY, cx + AndroidUtilities.dp(15.0f), lockY + lockSize);
            ChatActivityEnterView.this.lockBackgroundDrawable.draw(canvas2);
            long j = dt;
            ChatActivityEnterView.this.lockShadowDrawable.setBounds(cx - AndroidUtilities.dp(16.0f), lockY - AndroidUtilities.dp(1.0f), cx + AndroidUtilities.dp(16.0f), lockY + lockSize + AndroidUtilities.dp(1.0f));
            ChatActivityEnterView.this.lockShadowDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockTopDrawable.setBounds(cx - AndroidUtilities.dp(6.0f), lockTopY, AndroidUtilities.dp(6.0f) + cx, AndroidUtilities.dp(14.0f) + lockTopY);
            ChatActivityEnterView.this.lockTopDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockDrawable.setBounds(cx - AndroidUtilities.dp(7.0f), lockMiddleY, AndroidUtilities.dp(7.0f) + cx, AndroidUtilities.dp(12.0f) + lockMiddleY);
            ChatActivityEnterView.this.lockDrawable.draw(canvas2);
            ChatActivityEnterView.this.lockArrowDrawable.setBounds(cx - AndroidUtilities.dp(7.5f), lockArrowY, AndroidUtilities.dp(7.5f) + cx, AndroidUtilities.dp(9.0f) + lockArrowY);
            ChatActivityEnterView.this.lockArrowDrawable.draw(canvas2);
            if (isSendButtonVisible()) {
                ChatActivityEnterView.this.redDotPaint.setAlpha(255);
                ChatActivityEnterView.this.rect.set((float) (cx - AndroidUtilities.dp2(6.5f)), (float) (AndroidUtilities.dp(9.0f) + lockY), (float) (AndroidUtilities.dp(6.5f) + cx), (float) (AndroidUtilities.dp(22.0f) + lockY));
                canvas2.drawRoundRect(ChatActivityEnterView.this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ChatActivityEnterView.this.redDotPaint);
            }
        }

        /* access modifiers changed from: protected */
        public boolean dispatchHoverEvent(MotionEvent event) {
            return super.dispatchHoverEvent(event) || this.virtualViewHelper.dispatchHoverEvent(event);
        }

        private class VirtualViewHelper extends ExploreByTouchHelper {
            public VirtualViewHelper(View host) {
                super(host);
            }

            /* access modifiers changed from: protected */
            public int getVirtualViewAt(float x, float y) {
                if (!RecordCircle.this.isSendButtonVisible()) {
                    return -1;
                }
                if (ChatActivityEnterView.this.sendDrawable.getBounds().contains((int) x, (int) y)) {
                    return 1;
                }
                if (ChatActivityEnterView.this.lockBackgroundDrawable.getBounds().contains((int) x, (int) y)) {
                    return 2;
                }
                return -1;
            }

            /* access modifiers changed from: protected */
            public void getVisibleVirtualViews(List<Integer> list) {
                if (RecordCircle.this.isSendButtonVisible()) {
                    list.add(1);
                    list.add(2);
                }
            }

            /* access modifiers changed from: protected */
            public void onPopulateNodeForVirtualView(int id, AccessibilityNodeInfoCompat info) {
                if (id == 1) {
                    info.setBoundsInParent(ChatActivityEnterView.this.sendDrawable.getBounds());
                    info.setText(LocaleController.getString("Send", R.string.Send));
                } else if (id == 2) {
                    info.setBoundsInParent(ChatActivityEnterView.this.lockBackgroundDrawable.getBounds());
                    info.setText(LocaleController.getString("Stop", R.string.Stop));
                }
            }

            /* access modifiers changed from: protected */
            public boolean onPerformActionForVirtualView(int id, int action, Bundle args) {
                return true;
            }
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatActivityEnterView(Activity context, SizeNotifierFrameLayout parent, ChatActivity fragment, boolean isChat) {
        super(context);
        String str;
        int i;
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        Activity activity = context;
        SizeNotifierFrameLayout sizeNotifierFrameLayout = parent;
        ChatActivity chatActivity = fragment;
        Paint paint2 = new Paint(1);
        this.dotPaint = paint2;
        paint2.setColor(Theme.getColor(Theme.key_chat_emojiPanelNewTrending));
        setFocusable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.sendingMessagesChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        this.parentActivity = activity;
        this.parentFragment = chatActivity;
        if (chatActivity != null) {
            this.recordingGuid = fragment.getClassGuid();
        }
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setDelegate(this);
        this.sendByEnter = MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false);
        LinearLayout linearLayout = new LinearLayout(activity);
        this.textFieldContainer = linearLayout;
        linearLayout.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        this.textFieldContainer.setOrientation(0);
        this.textFieldContainer.setClipChildren(false);
        this.textFieldContainer.setClipToPadding(false);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1.0f, -2.0f, 83, 0.0f, 2.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(activity) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (ChatActivityEnterView.this.scheduledButton != null) {
                    int x = (getMeasuredWidth() - AndroidUtilities.dp((ChatActivityEnterView.this.botButton == null || ChatActivityEnterView.this.botButton.getVisibility() != 0) ? 48.0f : 96.0f)) - AndroidUtilities.dp(48.0f);
                    ChatActivityEnterView.this.scheduledButton.layout(x, ChatActivityEnterView.this.scheduledButton.getTop(), ChatActivityEnterView.this.scheduledButton.getMeasuredWidth() + x, ChatActivityEnterView.this.scheduledButton.getBottom());
                }
            }
        };
        this.textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f, 80));
        AnonymousClass9 r11 = new EditTextCaption(activity) {
            /* access modifiers changed from: private */
            /* renamed from: send */
            public void lambda$null$0$ChatActivityEnterView$9(InputContentInfoCompat inputContentInfo, boolean notify, int scheduleDate) {
                if (inputContentInfo.getDescription().hasMimeType("image/gif")) {
                    SendMessagesHelper.prepareSendingDocument(ChatActivityEnterView.this.accountInstance, (String) null, (String) null, inputContentInfo.getContentUri(), (String) null, "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, inputContentInfo, (MessageObject) null, notify, 0);
                } else {
                    SendMessagesHelper.prepareSendingPhoto(ChatActivityEnterView.this.accountInstance, (String) null, inputContentInfo.getContentUri(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, (CharSequence) null, (ArrayList<TLRPC.MessageEntity>) null, (ArrayList<TLRPC.InputDocument>) null, inputContentInfo, 0, (MessageObject) null, notify, 0);
                }
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.onMessageSend((CharSequence) null, true, scheduleDate);
                } else {
                    int i = scheduleDate;
                }
            }

            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                InputConnection ic = super.onCreateInputConnection(editorInfo);
                try {
                    EditorInfoCompat.setContentMimeTypes(editorInfo, new String[]{"image/gif", "image/*", "image/jpg", "image/png"});
                    return InputConnectionCompat.createWrapper(ic, editorInfo, new InputConnectionCompat.OnCommitContentListener() {
                        public final boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
                            return ChatActivityEnterView.AnonymousClass9.this.lambda$onCreateInputConnection$1$ChatActivityEnterView$9(inputContentInfoCompat, i, bundle);
                        }
                    });
                } catch (Throwable e) {
                    FileLog.e(e);
                    return ic;
                }
            }

            public /* synthetic */ boolean lambda$onCreateInputConnection$1$ChatActivityEnterView$9(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
                if (BuildCompat.isAtLeastNMR1() && (flags & 1) != 0) {
                    try {
                        inputContentInfo.requestPermission();
                    } catch (Exception e) {
                        return false;
                    }
                }
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, UserObject.isUserSelf(ChatActivityEnterView.this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(inputContentInfo) {
                        private final /* synthetic */ InputContentInfoCompat f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            ChatActivityEnterView.AnonymousClass9.this.lambda$null$0$ChatActivityEnterView$9(this.f$1, z, i);
                        }
                    });
                } else {
                    lambda$null$0$ChatActivityEnterView$9(inputContentInfo, true, 0);
                }
                return true;
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (ChatActivityEnterView.this.isPopupShowing() && event.getAction() == 0) {
                    if (ChatActivityEnterView.this.searchingType != 0) {
                        int unused = ChatActivityEnterView.this.searchingType = 0;
                        ChatActivityEnterView.this.emojiView.closeSearch(false);
                    }
                    ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2, 0);
                    ChatActivityEnterView.this.openKeyboardInternal();
                }
                try {
                    return super.onTouchEvent(event);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return false;
                }
            }

            /* access modifiers changed from: protected */
            public void onSelectionChanged(int selStart, int selEnd) {
                super.onSelectionChanged(selStart, selEnd);
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.onTextSelectionChanged(selStart, selEnd);
                }
            }
        };
        this.messageEditText = r11;
        r11.setDelegate(new EditTextCaption.EditTextCaptionDelegate() {
            public final void onSpansChanged() {
                ChatActivityEnterView.this.lambda$new$0$ChatActivityEnterView();
            }
        });
        ChatActivity chatActivity2 = this.parentFragment;
        TLRPC.EncryptedChat encryptedChat = chatActivity2 != null ? chatActivity2.getCurrentEncryptedChat() : null;
        this.messageEditText.setAllowTextEntitiesIntersection(encryptedChat == null || (encryptedChat != null && AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 101));
        updateFieldHint();
        this.messageEditText.setImeOptions(encryptedChat != null ? 268435456 | 16777216 : C.ENCODING_PCM_MU_LAW);
        EditTextCaption editTextCaption = this.messageEditText;
        editTextCaption.setInputType(editTextCaption.getInputType() | 16384 | 131072);
        this.messageEditText.setSingleLine(false);
        this.messageEditText.setMaxLines(6);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(16);
        this.messageEditText.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(2.0f));
        this.messageEditText.setMinimumHeight(AndroidUtilities.dp(38.0f));
        MryRoundButtonDrawable messageEditTextBackground = new MryRoundButtonDrawable();
        messageEditTextBackground.setIsRadiusAdjustBounds(false);
        messageEditTextBackground.setCornerRadius((float) AndroidUtilities.dp(8.0f));
        messageEditTextBackground.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhite)));
        messageEditTextBackground.setStrokeData(AndroidUtilities.dp(0.05f), ColorStateList.valueOf(Theme.getColor(Theme.key_divider)));
        this.messageEditText.setBackgroundDrawable(messageEditTextBackground);
        this.messageEditText.setTextColor(Theme.getColor(Theme.key_chat_messagePanelText));
        this.messageEditText.setHintColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        this.messageEditText.setHintTextColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        this.messageEditText.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor));
        frameLayout.addView(this.messageEditText, LayoutHelper.createFrame(-1.0f, -2.0f, 80, 52.0f, 5.0f, isChat ? 50.0f : 2.0f, 6.0f));
        this.messageEditText.setOnKeyListener(new View.OnKeyListener() {
            boolean ctrlPressed = false;

            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean z = false;
                if (i == 4 && !ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing()) {
                    if (keyEvent.getAction() == 1) {
                        if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                            MessagesController.getMainSettings(ChatActivityEnterView.this.currentAccount).edit().putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                        }
                        if (ChatActivityEnterView.this.searchingType != 0) {
                            int unused = ChatActivityEnterView.this.searchingType = 0;
                            ChatActivityEnterView.this.emojiView.closeSearch(true);
                            ChatActivityEnterView.this.messageEditText.requestFocus();
                        } else {
                            ChatActivityEnterView.this.showPopup(0, 0);
                        }
                    }
                    return true;
                } else if (i == 66 && ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null)) {
                    if (ChatActivityEnterView.this.slowModeTimer <= 0) {
                        ChatActivityEnterView.this.sendMessage();
                    }
                    return true;
                } else if (i == 113 || i == 114) {
                    if (keyEvent.getAction() == 0) {
                        z = true;
                    }
                    this.ctrlPressed = z;
                    return true;
                } else if (i != 67 || keyEvent.getAction() != 0) {
                    return false;
                } else {
                    int selectionStart = Selection.getSelectionStart(ChatActivityEnterView.this.messageEditText.getText());
                    int selectionEnd = Selection.getSelectionEnd(ChatActivityEnterView.this.messageEditText.getText());
                    for (URLSpanUserMention span : (URLSpanUserMention[]) ChatActivityEnterView.this.messageEditText.getText().getSpans(selectionStart, selectionEnd, URLSpanUserMention.class)) {
                        if (span != null && ChatActivityEnterView.this.messageEditText.getText().getSpanEnd(span) == selectionStart) {
                            Selection.setSelection(ChatActivityEnterView.this.messageEditText.getText(), ChatActivityEnterView.this.messageEditText.getText().getSpanStart(span), ChatActivityEnterView.this.messageEditText.getText().getSpanEnd(span));
                            if (selectionStart == selectionEnd) {
                                return true;
                            }
                            return false;
                        }
                    }
                    return false;
                }
            }
        });
        this.messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            boolean ctrlPressed = false;

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == 4) {
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                }
                boolean z = false;
                if (keyEvent != null && i == 0) {
                    if ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null) {
                        ChatActivityEnterView.this.sendMessage();
                        return true;
                    } else if (i == 113 || i == 114) {
                        if (keyEvent.getAction() == 0) {
                            z = true;
                        }
                        this.ctrlPressed = z;
                        return true;
                    }
                }
                return false;
            }
        });
        this.messageEditText.setEditableFactory(new NoCopySpanEditableFactory(new MentionSpanWatcher()));
        this.messageEditText.addTextChangedListener(new TextWatcher() {
            boolean processChange = false;

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (ChatActivityEnterView.this.innerTextChange != 1) {
                    ChatActivityEnterView.this.checkSendButton(true);
                    CharSequence message = AndroidUtilities.getTrimmedString(charSequence.toString());
                    if (ChatActivityEnterView.this.delegate != null && !ChatActivityEnterView.this.ignoreTextChange) {
                        if (count > 2 || charSequence == null || charSequence.length() == 0) {
                            boolean unused = ChatActivityEnterView.this.messageWebPageSearch = true;
                        }
                        ChatActivityEnterView.this.delegate.onTextChanged(charSequence, before > count + 1 || count - before > 2);
                    }
                    if (ChatActivityEnterView.this.innerTextChange != 2 && count - before > 1) {
                        this.processChange = true;
                    }
                    if (ChatActivityEnterView.this.editingMessageObject == null && !ChatActivityEnterView.this.canWriteToChannel && message.length() != 0 && ChatActivityEnterView.this.lastTypingTimeSend < System.currentTimeMillis() - DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS && !ChatActivityEnterView.this.ignoreTextChange) {
                        int currentTime = ConnectionsManager.getInstance(ChatActivityEnterView.this.currentAccount).getCurrentTime();
                        TLRPC.User currentUser = null;
                        if (((int) ChatActivityEnterView.this.dialog_id) > 0) {
                            currentUser = ChatActivityEnterView.this.accountInstance.getMessagesController().getUser(Integer.valueOf((int) ChatActivityEnterView.this.dialog_id));
                        }
                        if (currentUser != null) {
                            if (currentUser.id == UserConfig.getInstance(ChatActivityEnterView.this.currentAccount).getClientUserId()) {
                                return;
                            }
                            if (currentUser.status != null && currentUser.status.expires < currentTime && !ChatActivityEnterView.this.accountInstance.getMessagesController().onlinePrivacy.containsKey(Integer.valueOf(currentUser.id))) {
                                return;
                            }
                        }
                        long unused2 = ChatActivityEnterView.this.lastTypingTimeSend = System.currentTimeMillis();
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.needSendTyping();
                        }
                    }
                }
            }

            public void afterTextChanged(Editable editable) {
                if (ChatActivityEnterView.this.innerTextChange == 0) {
                    if (ChatActivityEnterView.this.sendByEnter && editable.length() > 0 && editable.charAt(editable.length() - 1) == 10 && ChatActivityEnterView.this.editingMessageObject == null) {
                        ChatActivityEnterView.this.sendMessage();
                    }
                    if (this.processChange) {
                        ImageSpan[] spans = (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class);
                        for (ImageSpan removeSpan : spans) {
                            editable.removeSpan(removeSpan);
                        }
                        Emoji.replaceEmoji(editable, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        this.processChange = false;
                    }
                }
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(activity);
        this.attachLayout = linearLayout2;
        linearLayout2.setOrientation(0);
        this.attachLayout.setEnabled(false);
        this.attachLayout.setPivotX((float) AndroidUtilities.dp(48.0f));
        frameLayout.addView(this.attachLayout, LayoutHelper.createFrame(-2.0f, 48.0f, 85, 0.0f, 0.0f, 0.0f, 2.0f));
        if (isChat) {
            ImageView imageView = new ImageView(activity);
            this.attachButton = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            this.attachButton.setImageResource(R.drawable.input_attach);
            this.attachButton.setScaleType(ImageView.ScaleType.CENTER);
            frameLayout.addView(this.attachButton, LayoutHelper.createFrame(48.0f, 48.0f, 83, 3.0f, 0.0f, 0.0f, 2.0f));
            this.attachButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$1$ChatActivityEnterView(view);
                }
            });
            this.attachButton.setContentDescription(LocaleController.getString("AccDescrAttachButton", R.string.AccDescrAttachButton));
            if (this.parentFragment != null) {
                Drawable drawable1 = context.getResources().getDrawable(R.drawable.input_calendar1).mutate();
                Drawable drawable2 = context.getResources().getDrawable(R.drawable.input_calendar2).mutate();
                drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
                drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_recordedVoiceDot), PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);
                ImageView imageView2 = new ImageView(activity);
                this.scheduledButton = imageView2;
                imageView2.setImageDrawable(combinedDrawable);
                this.scheduledButton.setVisibility(8);
                this.scheduledButton.setContentDescription(LocaleController.getString("ScheduledMessages", R.string.ScheduledMessages));
                this.scheduledButton.setScaleType(ImageView.ScaleType.CENTER);
                frameLayout.addView(this.scheduledButton, LayoutHelper.createFrame(48, 48, 85));
                this.scheduledButton.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChatActivityEnterView.this.lambda$new$2$ChatActivityEnterView(view);
                    }
                });
            }
            ImageView imageView3 = new ImageView(activity);
            this.botButton = imageView3;
            imageView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            this.botButton.setImageResource(R.drawable.input_bot2);
            this.botButton.setScaleType(ImageView.ScaleType.CENTER);
            this.botButton.setVisibility(8);
            this.attachLayout.addView(this.botButton, LayoutHelper.createLinear(48, 48));
            this.botButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$3$ChatActivityEnterView(view);
                }
            });
            ImageView imageView4 = new ImageView(activity);
            this.notifyButton = imageView4;
            imageView4.setImageResource(this.silent ? R.drawable.input_notify_off : R.drawable.input_notify_on);
            ImageView imageView5 = this.notifyButton;
            if (this.silent) {
                i = R.string.AccDescrChanSilentOn;
                str = "AccDescrChanSilentOn";
            } else {
                i = R.string.AccDescrChanSilentOff;
                str = "AccDescrChanSilentOff";
            }
            imageView5.setContentDescription(LocaleController.getString(str, i));
            this.notifyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            this.notifyButton.setScaleType(ImageView.ScaleType.CENTER);
            this.notifyButton.setVisibility((!this.canWriteToChannel || ((chatActivityEnterViewDelegate = this.delegate) != null && chatActivityEnterViewDelegate.hasScheduledMessages())) ? 8 : 0);
            this.attachLayout.addView(this.notifyButton, LayoutHelper.createLinear(48, 48));
            this.notifyButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$4$ChatActivityEnterView(view);
                }
            });
        }
        FrameLayout emojiButtonContainer = new FrameLayout(activity);
        this.attachLayout.addView(emojiButtonContainer, LayoutHelper.createLinear(48, 48));
        for (int a = 0; a < 2; a++) {
            this.emojiButton[a] = new ImageView(activity) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (getTag() != null && ChatActivityEnterView.this.attachLayout != null && !ChatActivityEnterView.this.emojiViewVisible && !MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).getUnreadStickerSets().isEmpty() && ChatActivityEnterView.this.dotPaint != null) {
                        canvas.drawCircle((float) ((getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), ChatActivityEnterView.this.dotPaint);
                    }
                }
            };
            this.emojiButton[a].setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            this.emojiButton[a].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            emojiButtonContainer.addView(this.emojiButton[a], LayoutHelper.createFrame(48.0f, 48.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
            this.emojiButton[a].setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatActivityEnterView.this.lambda$new$5$ChatActivityEnterView(view);
                }
            });
            this.emojiButton[a].setContentDescription(LocaleController.getString("AccDescrEmojiButton", R.string.AccDescrEmojiButton));
            if (a == 1) {
                this.emojiButton[a].setVisibility(4);
                this.emojiButton[a].setAlpha(0.0f);
                this.emojiButton[a].setScaleX(0.1f);
                this.emojiButton[a].setScaleY(0.1f);
            }
        }
        setEmojiButtonImage(false, false);
        FrameLayout frameLayout2 = new FrameLayout(activity);
        this.recordedAudioPanel = frameLayout2;
        frameLayout2.setVisibility(this.audioToSend == null ? 8 : 0);
        this.recordedAudioPanel.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        this.recordedAudioPanel.setFocusable(true);
        this.recordedAudioPanel.setFocusableInTouchMode(true);
        this.recordedAudioPanel.setClickable(true);
        frameLayout.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        ImageView imageView6 = new ImageView(activity);
        this.recordDeleteImageView = imageView6;
        imageView6.setScaleType(ImageView.ScaleType.CENTER);
        this.recordDeleteImageView.setImageResource(R.drawable.msg_delete);
        this.recordDeleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoiceDelete), PorterDuff.Mode.MULTIPLY));
        this.recordDeleteImageView.setContentDescription(LocaleController.getString("Delete", R.string.Delete));
        this.recordedAudioPanel.addView(this.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0f));
        this.recordDeleteImageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$6$ChatActivityEnterView(view);
            }
        });
        VideoTimelineView videoTimelineView2 = new VideoTimelineView(activity);
        this.videoTimelineView = videoTimelineView2;
        videoTimelineView2.setColor(Theme.getColor(Theme.key_chat_messagePanelVideoFrame));
        this.videoTimelineView.setRoundFrames(true);
        this.videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate() {
            public void onLeftProgressChanged(float progress) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.videoToSendMessageObject.startTime = (long) (((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * progress);
                    ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, progress);
                }
            }

            public void onRightProgressChanged(float progress) {
                if (ChatActivityEnterView.this.videoToSendMessageObject != null) {
                    ChatActivityEnterView.this.videoToSendMessageObject.endTime = (long) (((float) ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration) * progress);
                    ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, progress);
                }
            }

            public void didStartDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(1, 0.0f);
            }

            public void didStopDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(0, 0.0f);
            }
        });
        this.recordedAudioPanel.addView(this.videoTimelineView, LayoutHelper.createFrame(-1.0f, 32.0f, 19, 40.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(activity);
        this.recordedAudioBackground = view;
        view.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(18.0f), Theme.getColor(Theme.key_chat_recordedVoiceBackground)));
        this.recordedAudioPanel.addView(this.recordedAudioBackground, LayoutHelper.createFrame(-1.0f, 36.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        SeekBarWaveformView seekBarWaveformView = new SeekBarWaveformView(activity);
        this.recordedAudioSeekBar = seekBarWaveformView;
        this.recordedAudioPanel.addView(seekBarWaveformView, LayoutHelper.createFrame(-1.0f, 32.0f, 19, 92.0f, 0.0f, 52.0f, 0.0f));
        this.playDrawable = Theme.createSimpleSelectorDrawable(activity, R.drawable.s_play, Theme.getColor(Theme.key_chat_recordedVoicePlayPause), Theme.getColor(Theme.key_chat_recordedVoicePlayPausePressed));
        this.pauseDrawable = Theme.createSimpleSelectorDrawable(activity, R.drawable.s_pause, Theme.getColor(Theme.key_chat_recordedVoicePlayPause), Theme.getColor(Theme.key_chat_recordedVoicePlayPausePressed));
        ImageView imageView7 = new ImageView(activity);
        this.recordedAudioPlayButton = imageView7;
        imageView7.setImageDrawable(this.playDrawable);
        this.recordedAudioPlayButton.setScaleType(ImageView.ScaleType.CENTER);
        this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
        this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48.0f, 48.0f, 83, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioPlayButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$7$ChatActivityEnterView(view);
            }
        });
        TextView textView = new TextView(activity);
        this.recordedAudioTimeTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_chat_messagePanelVoiceDuration));
        this.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        this.recordedAudioPanel.addView(this.recordedAudioTimeTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 21, 0.0f, 0.0f, 13.0f, 0.0f));
        FrameLayout frameLayout3 = new FrameLayout(activity);
        this.recordPanel = frameLayout3;
        frameLayout3.setVisibility(8);
        this.recordPanel.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        frameLayout.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48, 80));
        this.recordPanel.setOnTouchListener($$Lambda$ChatActivityEnterView$eJlVzGPWXzFTAJ0grv2keZTVJhs.INSTANCE);
        LinearLayout linearLayout3 = new LinearLayout(activity);
        this.slideText = linearLayout3;
        linearLayout3.setOrientation(0);
        this.recordPanel.addView(this.slideText, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 30.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView8 = new ImageView(activity);
        this.recordCancelImage = imageView8;
        imageView8.setImageResource(R.drawable.slidearrow);
        this.recordCancelImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_recordVoiceCancel), PorterDuff.Mode.MULTIPLY));
        this.slideText.addView(this.recordCancelImage, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
        TextView textView2 = new TextView(activity);
        this.recordCancelText = textView2;
        textView2.setText(LocaleController.getString("SlideToCancel", R.string.SlideToCancel));
        this.recordCancelText.setTextColor(Theme.getColor(Theme.key_chat_recordVoiceCancel));
        this.recordCancelText.setTextSize(1, 12.0f);
        this.slideText.addView(this.recordCancelText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        TextView textView3 = new TextView(activity);
        this.recordSendText = textView3;
        textView3.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        this.recordSendText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        this.recordSendText.setTextSize(1, 16.0f);
        this.recordSendText.setGravity(17);
        this.recordSendText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.recordSendText.setAlpha(0.0f);
        this.recordSendText.setPadding(AndroidUtilities.dp(36.0f), 0, 0, 0);
        this.recordSendText.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$9$ChatActivityEnterView(view);
            }
        });
        this.recordPanel.addView(this.recordSendText, LayoutHelper.createFrame(-2.0f, -1.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout4 = new LinearLayout(activity);
        this.recordTimeContainer = linearLayout4;
        linearLayout4.setOrientation(0);
        this.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        this.recordTimeContainer.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        this.recordPanel.addView(this.recordTimeContainer, LayoutHelper.createFrame(-2, -2, 16));
        RecordDot recordDot2 = new RecordDot(activity);
        this.recordDot = recordDot2;
        this.recordTimeContainer.addView(recordDot2, LayoutHelper.createLinear(11, 11, 16, 0, 1, 0, 0));
        TextView textView4 = new TextView(activity);
        this.recordTimeText = textView4;
        textView4.setTextColor(Theme.getColor(Theme.key_chat_recordTime));
        this.recordTimeText.setTextSize(1, 16.0f);
        this.recordTimeContainer.addView(this.recordTimeText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        FrameLayout frameLayout4 = new FrameLayout(activity);
        this.sendButtonContainer = frameLayout4;
        frameLayout4.setClipChildren(false);
        this.sendButtonContainer.setClipToPadding(false);
        this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createLinear(48.0f, 48.0f, 80, 0.0f, 0.0f, 0.0f, 2.0f));
        FrameLayout frameLayout5 = new FrameLayout(activity);
        this.audioVideoButtonContainer = frameLayout5;
        frameLayout5.setBackgroundColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
        this.audioVideoButtonContainer.setSoundEffectsEnabled(false);
        this.sendButtonContainer.addView(this.audioVideoButtonContainer, LayoutHelper.createFrame(48, 48.0f));
        this.audioVideoButtonContainer.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return ChatActivityEnterView.this.lambda$new$14$ChatActivityEnterView(view, motionEvent);
            }
        });
        ImageView imageView9 = new ImageView(activity);
        this.audioSendButton = imageView9;
        imageView9.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.audioSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
        this.audioSendButton.setImageResource(R.drawable.input_mic);
        this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.audioSendButton.setContentDescription(LocaleController.getString("AccDescrVoiceMessage", R.string.AccDescrVoiceMessage));
        this.audioSendButton.setFocusable(true);
        this.audioSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
        this.audioVideoButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
        if (isChat) {
            ImageView imageView10 = new ImageView(activity);
            this.videoSendButton = imageView10;
            imageView10.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            this.videoSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            this.videoSendButton.setImageResource(R.drawable.input_video);
            this.videoSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
            this.videoSendButton.setContentDescription(LocaleController.getString("AccDescrVideoMessage", R.string.AccDescrVideoMessage));
            this.videoSendButton.setFocusable(true);
            this.videoSendButton.setAccessibilityDelegate(this.mediaMessageButtonsDelegate);
            this.audioVideoButtonContainer.addView(this.videoSendButton, LayoutHelper.createFrame(48, 48.0f));
        }
        RecordCircle recordCircle2 = new RecordCircle(activity);
        this.recordCircle = recordCircle2;
        recordCircle2.setVisibility(8);
        this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(124.0f, 194.0f, 85, 0.0f, 0.0f, -36.0f, 0.0f));
        ImageView imageView11 = new ImageView(activity);
        this.cancelBotButton = imageView11;
        imageView11.setVisibility(4);
        this.cancelBotButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageView imageView12 = this.cancelBotButton;
        CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
        this.progressDrawable = closeProgressDrawable2;
        imageView12.setImageDrawable(closeProgressDrawable2);
        this.cancelBotButton.setContentDescription(LocaleController.getString("Cancel", R.string.Cancel));
        this.progressDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelCancelInlineBot), PorterDuff.Mode.MULTIPLY));
        this.cancelBotButton.setSoundEffectsEnabled(false);
        this.cancelBotButton.setScaleX(0.1f);
        this.cancelBotButton.setScaleY(0.1f);
        this.cancelBotButton.setAlpha(0.0f);
        this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0f));
        this.cancelBotButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$15$ChatActivityEnterView(view);
            }
        });
        if (isInScheduleMode()) {
            this.sendButtonDrawable = context.getResources().getDrawable(R.drawable.input_schedule).mutate();
            this.sendButtonInverseDrawable = context.getResources().getDrawable(R.drawable.input_schedule).mutate();
            this.inactinveSendButtonDrawable = context.getResources().getDrawable(R.drawable.input_schedule).mutate();
        } else {
            this.sendButtonDrawable = context.getResources().getDrawable(R.drawable.ic_send).mutate();
            this.sendButtonInverseDrawable = context.getResources().getDrawable(R.drawable.ic_send).mutate();
            this.inactinveSendButtonDrawable = context.getResources().getDrawable(R.drawable.ic_send).mutate();
        }
        AnonymousClass15 r2 = new View(activity) {
            private float animateBounce;
            private float animationDuration;
            private float animationProgress;
            private int drawableColor;
            private long lastAnimationTime;

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                int color;
                Canvas canvas2 = canvas;
                int x = (getMeasuredWidth() - ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth()) / 2;
                int y = (getMeasuredHeight() - ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight()) / 2;
                if (ChatActivityEnterView.this.isInScheduleMode()) {
                    y -= AndroidUtilities.dp(1.0f);
                } else {
                    x += AndroidUtilities.dp(2.0f);
                }
                boolean z = ChatActivityEnterView.this.sendPopupWindow != null && ChatActivityEnterView.this.sendPopupWindow.isShowing();
                boolean showingPopup = z;
                if (z) {
                    color = Theme.getColor(Theme.key_chat_messagePanelVoicePressed);
                } else {
                    color = Theme.getColor(Theme.key_chat_messagePanelSend);
                }
                if (color != this.drawableColor) {
                    this.lastAnimationTime = SystemClock.uptimeMillis();
                    if (showingPopup) {
                        this.animationProgress = 0.0f;
                        this.animationDuration = 200.0f;
                    } else if (this.drawableColor != 0) {
                        this.animationProgress = 0.0f;
                        this.animationDuration = 120.0f;
                    } else {
                        this.animationProgress = 1.0f;
                    }
                    this.drawableColor = color;
                    ChatActivityEnterView.this.sendButtonDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelSend), PorterDuff.Mode.MULTIPLY));
                    int c = Theme.getColor(Theme.key_chat_messagePanelIcons);
                    ChatActivityEnterView.this.inactinveSendButtonDrawable.setColorFilter(new PorterDuffColorFilter(Color.argb(180, Color.red(c), Color.green(c), Color.blue(c)), PorterDuff.Mode.MULTIPLY));
                    ChatActivityEnterView.this.sendButtonInverseDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY));
                }
                if (this.animationProgress < 1.0f) {
                    long newTime = SystemClock.uptimeMillis();
                    float f = this.animationProgress + (((float) (newTime - this.lastAnimationTime)) / this.animationDuration);
                    this.animationProgress = f;
                    if (f > 1.0f) {
                        this.animationProgress = 1.0f;
                    }
                    this.lastAnimationTime = newTime;
                    invalidate();
                }
                if (!showingPopup) {
                    if (ChatActivityEnterView.this.slowModeTimer != Integer.MAX_VALUE || ChatActivityEnterView.this.isInScheduleMode()) {
                        ChatActivityEnterView.this.sendButtonDrawable.setBounds(x, y, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + x, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + y);
                        ChatActivityEnterView.this.sendButtonDrawable.draw(canvas2);
                    } else {
                        ChatActivityEnterView.this.inactinveSendButtonDrawable.setBounds(x, y, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + x, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + y);
                        ChatActivityEnterView.this.inactinveSendButtonDrawable.draw(canvas2);
                    }
                }
                if (showingPopup || this.animationProgress != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_chat_messagePanelSend));
                    int rad = AndroidUtilities.dp(20.0f);
                    if (showingPopup) {
                        ChatActivityEnterView.this.sendButtonInverseDrawable.setAlpha(255);
                        float p = this.animationProgress;
                        if (p <= 0.25f) {
                            rad = (int) (((float) rad) + (((float) AndroidUtilities.dp(2.0f)) * CubicBezierInterpolator.EASE_IN.getInterpolation(p / 0.25f)));
                        } else {
                            float p2 = p - 0.25f;
                            if (p2 <= 0.5f) {
                                rad = (int) (((float) rad) + (((float) AndroidUtilities.dp(2.0f)) - (((float) AndroidUtilities.dp(3.0f)) * CubicBezierInterpolator.EASE_IN.getInterpolation(p2 / 0.5f))));
                            } else {
                                rad = (int) (((float) rad) + ((float) (-AndroidUtilities.dp(1.0f))) + (((float) AndroidUtilities.dp(1.0f)) * CubicBezierInterpolator.EASE_IN.getInterpolation((p2 - 0.5f) / 0.25f)));
                            }
                        }
                    } else {
                        int alpha = (int) ((1.0f - this.animationProgress) * 255.0f);
                        Theme.dialogs_onlineCirclePaint.setAlpha(alpha);
                        ChatActivityEnterView.this.sendButtonInverseDrawable.setAlpha(alpha);
                    }
                    canvas2.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) rad, Theme.dialogs_onlineCirclePaint);
                    ChatActivityEnterView.this.sendButtonInverseDrawable.setBounds(x, y, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicWidth() + x, ChatActivityEnterView.this.sendButtonDrawable.getIntrinsicHeight() + y);
                    ChatActivityEnterView.this.sendButtonInverseDrawable.draw(canvas2);
                }
            }
        };
        this.sendButton = r2;
        r2.setVisibility(4);
        int color = Theme.getColor(Theme.key_chat_messagePanelSend);
        this.sendButton.setContentDescription(LocaleController.getString("Send", R.string.Send));
        this.sendButton.setSoundEffectsEnabled(false);
        this.sendButton.setScaleX(0.1f);
        this.sendButton.setScaleY(0.1f);
        this.sendButton.setAlpha(0.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            this.sendButton.setBackgroundDrawable(Theme.createSelectorDrawable(Color.argb(24, Color.red(color), Color.green(color), Color.blue(color)), 1));
        }
        this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48.0f, 48.0f, 80, 0.0f, 0.0f, 0.0f, -2.0f));
        this.sendButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$16$ChatActivityEnterView(view);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(activity);
        this.slowModeButton = simpleTextView;
        simpleTextView.setTextSize(18);
        this.slowModeButton.setVisibility(4);
        this.slowModeButton.setSoundEffectsEnabled(false);
        this.slowModeButton.setScaleX(0.1f);
        this.slowModeButton.setScaleY(0.1f);
        this.slowModeButton.setAlpha(0.0f);
        this.slowModeButton.setPadding(0, 0, AndroidUtilities.dp(13.0f), 0);
        this.slowModeButton.setGravity(21);
        this.slowModeButton.setTextColor(Theme.getColor(Theme.key_chat_messagePanelIcons));
        this.sendButtonContainer.addView(this.slowModeButton, LayoutHelper.createFrame(64, 48, 53));
        this.slowModeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$17$ChatActivityEnterView(view);
            }
        });
        ImageView imageView13 = new ImageView(activity);
        this.expandStickersButton = imageView13;
        imageView13.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.expandStickersButton.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView14 = this.expandStickersButton;
        AnimatedArrowDrawable animatedArrowDrawable = new AnimatedArrowDrawable(Theme.getColor(Theme.key_chat_messagePanelIcons), false);
        this.stickersArrow = animatedArrowDrawable;
        imageView14.setImageDrawable(animatedArrowDrawable);
        this.expandStickersButton.setVisibility(8);
        this.expandStickersButton.setScaleX(0.1f);
        this.expandStickersButton.setScaleY(0.1f);
        this.expandStickersButton.setAlpha(0.0f);
        this.sendButtonContainer.addView(this.expandStickersButton, LayoutHelper.createFrame(48, 48.0f));
        this.expandStickersButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$18$ChatActivityEnterView(view);
            }
        });
        this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", R.string.AccDescrExpandPanel));
        FrameLayout frameLayout6 = new FrameLayout(activity);
        this.doneButtonContainer = frameLayout6;
        frameLayout6.setVisibility(8);
        this.textFieldContainer.addView(this.doneButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        this.doneButtonContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatActivityEnterView.this.lambda$new$19$ChatActivityEnterView(view);
            }
        });
        Drawable drawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), Theme.getColor(Theme.key_chat_messagePanelSend));
        Drawable checkDrawable = context.getResources().getDrawable(R.drawable.input_done).mutate();
        checkDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelVoicePressed), PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable2 = new CombinedDrawable(drawable, checkDrawable, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable2.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        ImageView imageView15 = new ImageView(activity);
        this.doneButtonImage = imageView15;
        imageView15.setScaleType(ImageView.ScaleType.CENTER);
        this.doneButtonImage.setImageDrawable(combinedDrawable2);
        this.doneButtonImage.setContentDescription(LocaleController.getString("Done", R.string.Done));
        int i2 = color;
        this.doneButtonContainer.addView(this.doneButtonImage, LayoutHelper.createFrame(48, 48.0f));
        ContextProgressView contextProgressView = new ContextProgressView(activity, 0);
        this.doneButtonProgress = contextProgressView;
        contextProgressView.setVisibility(4);
        this.doneButtonContainer.addView(this.doneButtonProgress, LayoutHelper.createFrame(-1, -1.0f));
        SharedPreferences sharedPreferences = MessagesController.getGlobalEmojiSettings();
        this.keyboardHeight = sharedPreferences.getInt("kbd_height", AndroidUtilities.dp(236.0f));
        this.keyboardHeightLand = sharedPreferences.getInt("kbd_height_land3", AndroidUtilities.dp(236.0f));
        setRecordVideoButtonVisible(false, false);
        checkSendButton(false);
        checkChannelRights();
        addView(new View(activity) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                int width = getWidth();
                int height = getHeight();
                canvas.drawLine(0.0f, (float) (height - 1), (float) width, (float) (height - 1), Theme.dividerPaint);
            }
        }, LayoutHelper.createFrame(-1.0f, 0.5f, 80));
    }

    public /* synthetic */ void lambda$new$0$ChatActivityEnterView() {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onTextSpansChanged(this.messageEditText.getText());
        }
    }

    public /* synthetic */ void lambda$new$1$ChatActivityEnterView(View v) {
        showPopup(3, 3);
    }

    public /* synthetic */ void lambda$new$2$ChatActivityEnterView(View v) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.openScheduledMessages();
        }
    }

    public /* synthetic */ void lambda$new$3$ChatActivityEnterView(View v) {
        int i;
        if (this.searchingType != 0) {
            this.searchingType = 0;
            this.emojiView.closeSearch(false);
            this.messageEditText.requestFocus();
        }
        if (this.botReplyMarkup != null) {
            if (!isPopupShowing() || (i = this.currentPopupContentType) != 1) {
                showPopup(1, 1);
                SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
                edit.remove("hidekeyboard_" + this.dialog_id).commit();
            } else {
                if (i == 1 && this.botButtonsMessageObject != null) {
                    SharedPreferences.Editor edit2 = MessagesController.getMainSettings(this.currentAccount).edit();
                    edit2.putInt("hidekeyboard_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
                }
                openKeyboardInternal();
            }
        } else if (this.hasBotCommands) {
            setFieldText("/");
            this.messageEditText.requestFocus();
            openKeyboard();
        }
        if (this.stickersExpanded) {
            setStickersExpanded(false, false, false);
        }
    }

    public /* synthetic */ void lambda$new$4$ChatActivityEnterView(View v) {
        String str;
        int i;
        boolean z = !this.silent;
        this.silent = z;
        this.notifyButton.setImageResource(z ? R.drawable.input_notify_off : R.drawable.input_notify_on);
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putBoolean("silent_" + this.dialog_id, this.silent).commit();
        NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(this.dialog_id);
        if (this.silent) {
            ToastUtils.show((int) R.string.ChannelNotifyMembersInfoOff);
        } else {
            ToastUtils.show((int) R.string.ChannelNotifyMembersInfoOn);
        }
        ImageView imageView = this.notifyButton;
        if (this.silent) {
            i = R.string.AccDescrChanSilentOn;
            str = "AccDescrChanSilentOn";
        } else {
            i = R.string.AccDescrChanSilentOff;
            str = "AccDescrChanSilentOff";
        }
        imageView.setContentDescription(LocaleController.getString(str, i));
        updateFieldHint();
    }

    public /* synthetic */ void lambda$new$5$ChatActivityEnterView(View view) {
        boolean z = false;
        if (!isPopupShowing() || this.currentPopupContentType != 0) {
            showPopup(1, 0);
            EmojiView emojiView2 = this.emojiView;
            if (this.messageEditText.length() > 0) {
                z = true;
            }
            emojiView2.onOpen(z);
            return;
        }
        if (this.searchingType != 0) {
            this.searchingType = 0;
            this.emojiView.closeSearch(false);
            this.messageEditText.requestFocus();
        }
        openKeyboardInternal();
    }

    public /* synthetic */ void lambda$new$6$ChatActivityEnterView(View v) {
        if (this.videoToSendMessageObject != null) {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(2, true, 0);
        } else {
            MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
            if (playing != null && playing == this.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
        }
        if (this.audioToSendPath != null) {
            new File(this.audioToSendPath).delete();
        }
        hideRecordedAudioPanel();
        checkSendButton(true);
    }

    public /* synthetic */ void lambda$new$7$ChatActivityEnterView(View v) {
        if (this.audioToSend != null) {
            if (!MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.recordedAudioPlayButton.setImageDrawable(this.pauseDrawable);
                MediaController.getInstance().playMessage(this.audioToSendMessageObject);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPause", R.string.AccActionPause));
                return;
            }
            MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.audioToSendMessageObject);
            this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
            this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
        }
    }

    static /* synthetic */ boolean lambda$new$8(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$new$9$ChatActivityEnterView(View v) {
        if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
            this.delegate.needStartRecordAudio(0);
            MediaController.getInstance().stopRecording(0, false, 0);
        } else {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(2, true, 0);
        }
        this.recordingAudioVideo = false;
        updateRecordIntefrace();
    }

    public /* synthetic */ boolean lambda$new$14$ChatActivityEnterView(View view, MotionEvent motionEvent) {
        TLRPC.Chat chat;
        int i = 3;
        boolean z = false;
        if (motionEvent.getAction() == 0) {
            if (this.recordCircle.isSendButtonVisible()) {
                if (!this.hasRecordVideo || this.calledRecordRunnable) {
                    this.startedDraggingX = -1.0f;
                    if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                        if (this.recordingAudioVideo && isInScheduleMode()) {
                            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, UserObject.isUserSelf(this.parentFragment.getCurrentUser()), $$Lambda$ChatActivityEnterView$0_dAq_r6wsgGbYUSi2ktHvyVC3g.INSTANCE, $$Lambda$ChatActivityEnterView$Q5hlyJjVbEaxrpIqaqlTOSc6HJc.INSTANCE);
                        }
                        this.delegate.needStartRecordAudio(0);
                        MediaController instance = MediaController.getInstance();
                        if (!isInScheduleMode()) {
                            i = 1;
                        }
                        instance.stopRecording(i, true, 0);
                    } else {
                        this.delegate.needStartRecordVideo(1, true, 0);
                    }
                    this.recordingAudioVideo = false;
                    updateRecordIntefrace();
                }
                return false;
            }
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && (chat = chatActivity.getCurrentChat()) != null && !ChatObject.canSendMedia(chat)) {
                this.delegate.needShowMediaBanHint();
                return false;
            } else if (this.hasRecordVideo) {
                this.calledRecordRunnable = false;
                this.recordAudioVideoRunnableStarted = true;
                AndroidUtilities.runOnUIThread(this.recordAudioVideoRunnable, 150);
            } else {
                this.recordAudioVideoRunnable.run();
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.recordCircle.isSendButtonVisible() || this.recordedAudioPanel.getVisibility() == 0) {
                return false;
            }
            if (this.recordAudioVideoRunnableStarted) {
                AndroidUtilities.cancelRunOnUIThread(this.recordAudioVideoRunnable);
                this.delegate.onSwitchRecordMode(this.videoSendButton.getTag() == null);
                if (this.videoSendButton.getTag() == null) {
                    z = true;
                }
                setRecordVideoButtonVisible(z, true);
                performHapticFeedback(3);
                sendAccessibilityEvent(1);
            } else if (!this.hasRecordVideo || this.calledRecordRunnable) {
                this.startedDraggingX = -1.0f;
                if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                    if (this.recordingAudioVideo && isInScheduleMode()) {
                        AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, UserObject.isUserSelf(this.parentFragment.getCurrentUser()), $$Lambda$ChatActivityEnterView$sV7GG5biwD2YPIhHhdRLRPp8Cb4.INSTANCE, $$Lambda$ChatActivityEnterView$rpH_n2Nj5ZoAzBsQTu5UdlTDaM4.INSTANCE);
                    }
                    this.delegate.needStartRecordAudio(0);
                    MediaController instance2 = MediaController.getInstance();
                    if (!isInScheduleMode()) {
                        i = 1;
                    }
                    instance2.stopRecording(i, true, 0);
                } else {
                    CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                    this.delegate.needStartRecordVideo(1, true, 0);
                }
                this.recordingAudioVideo = false;
                updateRecordIntefrace();
            }
        } else if (motionEvent.getAction() == 2 && this.recordingAudioVideo) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (this.recordCircle.isSendButtonVisible()) {
                return false;
            }
            if (this.recordCircle.setLockTranslation(y) == 2) {
                AnimatorSet animatorSet = new AnimatorSet();
                RecordCircle recordCircle2 = this.recordCircle;
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(recordCircle2, "lockAnimatedTranslation", new float[]{recordCircle2.startTranslation}), ObjectAnimator.ofFloat(this.slideText, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.slideText, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(20.0f)}), ObjectAnimator.ofFloat(this.recordSendText, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.recordSendText, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(20.0f)), 0.0f})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(150);
                animatorSet.start();
                return false;
            }
            if (x < (-this.distCanMove)) {
                if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
                    this.delegate.needStartRecordAudio(0);
                    MediaController.getInstance().stopRecording(0, false, 0);
                } else {
                    CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
                    this.delegate.needStartRecordVideo(2, true, 0);
                }
                this.recordingAudioVideo = false;
                updateRecordIntefrace();
            }
            float x2 = x + this.audioVideoButtonContainer.getX();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.slideText.getLayoutParams();
            float f = this.startedDraggingX;
            if (f != -1.0f) {
                float dist = x2 - f;
                params.leftMargin = AndroidUtilities.dp(30.0f) + ((int) dist);
                this.slideText.setLayoutParams(params);
                float alpha = (dist / this.distCanMove) + 1.0f;
                if (alpha > 1.0f) {
                    alpha = 1.0f;
                } else if (alpha < 0.0f) {
                    alpha = 0.0f;
                }
                this.slideText.setAlpha(alpha);
            }
            if (x2 <= this.slideText.getX() + ((float) this.slideText.getWidth()) + ((float) AndroidUtilities.dp(30.0f)) && this.startedDraggingX == -1.0f) {
                this.startedDraggingX = x2;
                float measuredWidth = ((float) ((this.recordPanel.getMeasuredWidth() - this.slideText.getMeasuredWidth()) - AndroidUtilities.dp(48.0f))) / 2.0f;
                this.distCanMove = measuredWidth;
                if (measuredWidth <= 0.0f) {
                    this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                } else if (measuredWidth > ((float) AndroidUtilities.dp(80.0f))) {
                    this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                }
            }
            if (params.leftMargin > AndroidUtilities.dp(30.0f)) {
                params.leftMargin = AndroidUtilities.dp(30.0f);
                this.slideText.setLayoutParams(params);
                this.slideText.setAlpha(1.0f);
                this.startedDraggingX = -1.0f;
            }
        }
        view.onTouchEvent(motionEvent);
        return true;
    }

    public /* synthetic */ void lambda$new$15$ChatActivityEnterView(View view) {
        String text = this.messageEditText.getText().toString();
        int idx = text.indexOf(32);
        if (idx == -1 || idx == text.length() - 1) {
            setFieldText("");
        } else {
            setFieldText(text.substring(0, idx + 1));
        }
    }

    public /* synthetic */ void lambda$new$16$ChatActivityEnterView(View view) {
        sendMessage();
    }

    public /* synthetic */ void lambda$new$17$ChatActivityEnterView(View v) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            SimpleTextView simpleTextView = this.slowModeButton;
            chatActivityEnterViewDelegate.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
        }
    }

    public /* synthetic */ void lambda$new$18$ChatActivityEnterView(View v) {
        EmojiView emojiView2;
        if (this.expandStickersButton.getVisibility() == 0 && this.expandStickersButton.getAlpha() == 1.0f) {
            if (this.stickersExpanded) {
                if (this.searchingType != 0) {
                    this.searchingType = 0;
                    this.emojiView.closeSearch(true);
                    this.emojiView.hideSearchKeyboard();
                    if (this.emojiTabOpen) {
                        checkSendButton(true);
                    }
                } else if (!this.stickersDragging && (emojiView2 = this.emojiView) != null) {
                    emojiView2.showSearchField(false);
                }
            } else if (!this.stickersDragging) {
                this.emojiView.showSearchField(true);
            }
            if (!this.stickersDragging) {
                setStickersExpanded(!this.stickersExpanded, true, false);
            }
        }
    }

    public /* synthetic */ void lambda$new$19$ChatActivityEnterView(View view) {
        doneEditingMessage();
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == this.topView) {
            canvas.save();
            canvas.clipRect(0, 0, getMeasuredWidth(), child.getLayoutParams().height + AndroidUtilities.dp(2.0f));
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.topView) {
            canvas.restore();
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        View view = this.topView;
        int top = (view == null || view.getVisibility() != 0) ? 0 : (int) this.topView.getTranslationY();
        int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + top;
        Theme.chat_composeShadowDrawable.setBounds(0, top, getMeasuredWidth(), bottom);
        Theme.chat_composeShadowDrawable.draw(canvas);
        canvas.drawRect(0.0f, (float) bottom, (float) getWidth(), (float) getHeight(), Theme.chat_composeBackgroundPaint);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    private boolean onSendLongClick(View view) {
        int y;
        View view2 = view;
        if (this.parentFragment == null || isInScheduleMode() || this.parentFragment.getCurrentEncryptedChat() != null) {
            return false;
        }
        TLRPC.Chat currentChat = this.parentFragment.getCurrentChat();
        TLRPC.User user = this.parentFragment.getCurrentUser();
        if (this.sendPopupLayout == null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
            this.sendPopupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() != 0 || ChatActivityEnterView.this.sendPopupWindow == null || !ChatActivityEnterView.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    v.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                        return false;
                    }
                    ChatActivityEnterView.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    ChatActivityEnterView.this.lambda$onSendLongClick$20$ChatActivityEnterView(keyEvent);
                }
            });
            this.sendPopupLayout.setShowedFromBotton(false);
            for (int a = 0; a < 2; a++) {
                if (a != 1 || (!UserObject.isUserSelf(user) && (this.slowModeTimer <= 0 || isInScheduleMode()))) {
                    int num = a;
                    ActionBarMenuSubItem cell = new ActionBarMenuSubItem(getContext());
                    if (num == 0) {
                        if (UserObject.isUserSelf(user)) {
                            cell.setTextAndIcon(LocaleController.getString("SetReminder", R.string.SetReminder), R.drawable.msg_schedule);
                        } else {
                            cell.setTextAndIcon(LocaleController.getString("ScheduleMessage", R.string.ScheduleMessage), R.drawable.msg_schedule);
                        }
                    } else if (num == 1) {
                        cell.setTextAndIcon(LocaleController.getString("SendWithoutSound", R.string.SendWithoutSound), R.drawable.input_notify_off);
                    }
                    cell.setMinimumWidth(AndroidUtilities.dp(196.0f));
                    this.sendPopupLayout.addView(cell, LayoutHelper.createFrame(-1.0f, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (a * 48), 0.0f, 0.0f));
                    cell.setOnClickListener(new View.OnClickListener(num, user) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ TLRPC.User f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(View view) {
                            ChatActivityEnterView.this.lambda$onSendLongClick$21$ChatActivityEnterView(this.f$1, this.f$2, view);
                        }
                    });
                }
            }
            AnonymousClass18 r5 = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2) {
                public void dismiss() {
                    super.dismiss();
                    ChatActivityEnterView.this.sendButton.invalidate();
                }
            };
            this.sendPopupWindow = r5;
            r5.setAnimationEnabled(false);
            this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
            this.sendPopupWindow.setOutsideTouchable(true);
            this.sendPopupWindow.setClippingEnabled(true);
            this.sendPopupWindow.setInputMethodMode(2);
            this.sendPopupWindow.setSoftInputMode(0);
            this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        }
        this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view2.getLocationInWindow(location);
        if (this.keyboardVisible) {
            int measuredHeight = getMeasuredHeight();
            View view3 = this.topView;
            if (measuredHeight > AndroidUtilities.dp((view3 == null || view3.getVisibility() != 0) ? 58.0f : 106.0f)) {
                y = location[1] + view.getMeasuredHeight();
                this.sendPopupWindow.showAtLocation(view2, 51, ((location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), y);
                this.sendPopupWindow.dimBehind();
                this.sendButton.invalidate();
                view2.performHapticFeedback(3, 2);
                return false;
            }
        }
        y = (location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f);
        this.sendPopupWindow.showAtLocation(view2, 51, ((location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), y);
        this.sendPopupWindow.dimBehind();
        this.sendButton.invalidate();
        view2.performHapticFeedback(3, 2);
        return false;
    }

    public /* synthetic */ void lambda$onSendLongClick$20$ChatActivityEnterView(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$onSendLongClick$21$ChatActivityEnterView(int num, TLRPC.User user, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (num == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, UserObject.isUserSelf(user), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    ChatActivityEnterView.this.sendMessageInternal(z, i);
                }
            });
        } else if (num == 1) {
            sendMessageInternal(false, 0);
        }
    }

    public boolean isSendButtonVisible() {
        return this.sendButton.getVisibility() == 0;
    }

    private void setRecordVideoButtonVisible(boolean visible, boolean animated) {
        boolean z = visible;
        ImageView imageView = this.videoSendButton;
        if (imageView != null) {
            imageView.setTag(z ? 1 : null);
            AnimatorSet animatorSet = this.audioVideoButtonAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.audioVideoButtonAnimation = null;
            }
            float f = 0.0f;
            float f2 = 0.1f;
            if (animated) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                boolean isChannel = false;
                if (((int) this.dialog_id) < 0) {
                    TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
                    isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
                }
                preferences.edit().putBoolean(isChannel ? "currentModeVideoChannel" : "currentModeVideo", z).commit();
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.audioVideoButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[6];
                ImageView imageView2 = this.videoSendButton;
                Property property = View.SCALE_X;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.1f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                ImageView imageView3 = this.videoSendButton;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
                ImageView imageView4 = this.videoSendButton;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = z ? 1.0f : 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                ImageView imageView5 = this.audioSendButton;
                Property property4 = View.SCALE_X;
                float[] fArr4 = new float[1];
                fArr4[0] = z ? 0.1f : 1.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(imageView5, property4, fArr4);
                ImageView imageView6 = this.audioSendButton;
                Property property5 = View.SCALE_Y;
                float[] fArr5 = new float[1];
                if (!z) {
                    f2 = 1.0f;
                }
                fArr5[0] = f2;
                animatorArr[4] = ObjectAnimator.ofFloat(imageView6, property5, fArr5);
                ImageView imageView7 = this.audioSendButton;
                Property property6 = View.ALPHA;
                float[] fArr6 = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr6[0] = f;
                animatorArr[5] = ObjectAnimator.ofFloat(imageView7, property6, fArr6);
                animatorSet2.playTogether(animatorArr);
                this.audioVideoButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(ChatActivityEnterView.this.audioVideoButtonAnimation)) {
                            AnimatorSet unused = ChatActivityEnterView.this.audioVideoButtonAnimation = null;
                        }
                        (ChatActivityEnterView.this.videoSendButton.getTag() == null ? ChatActivityEnterView.this.audioSendButton : ChatActivityEnterView.this.videoSendButton).sendAccessibilityEvent(8);
                    }
                });
                this.audioVideoButtonAnimation.setInterpolator(new DecelerateInterpolator());
                this.audioVideoButtonAnimation.setDuration(150);
                this.audioVideoButtonAnimation.start();
                return;
            }
            this.videoSendButton.setScaleX(z ? 1.0f : 0.1f);
            this.videoSendButton.setScaleY(z ? 1.0f : 0.1f);
            this.videoSendButton.setAlpha(z ? 1.0f : 0.0f);
            this.audioSendButton.setScaleX(z ? 0.1f : 1.0f);
            ImageView imageView8 = this.audioSendButton;
            if (!z) {
                f2 = 1.0f;
            }
            imageView8.setScaleY(f2);
            ImageView imageView9 = this.audioSendButton;
            if (!z) {
                f = 1.0f;
            }
            imageView9.setAlpha(f);
        }
    }

    public boolean isRecordingAudioVideo() {
        return this.recordingAudioVideo;
    }

    public boolean isRecordLocked() {
        return this.recordingAudioVideo && this.recordCircle.isSendButtonVisible();
    }

    public void cancelRecordingAudioVideo() {
        if (!this.hasRecordVideo || this.videoSendButton.getTag() == null) {
            this.delegate.needStartRecordAudio(0);
            MediaController.getInstance().stopRecording(0, false, 0);
        } else {
            CameraController.getInstance().cancelOnInitRunnable(this.onFinishInitCameraRunnable);
            this.delegate.needStartRecordVideo(2, true, 0);
        }
        this.recordingAudioVideo = false;
        updateRecordIntefrace();
    }

    public void showContextProgress(boolean show) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 != null) {
            if (show) {
                closeProgressDrawable2.startAnimation();
            } else {
                closeProgressDrawable2.stopAnimation();
            }
        }
    }

    public void setCaption(String caption) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setCaption(caption);
            checkSendButton(true);
        }
    }

    public void setSlowModeTimer(int time) {
        this.slowModeTimer = time;
        updateSlowModeText();
    }

    public CharSequence getSlowModeTimer() {
        if (this.slowModeTimer > 0) {
            return this.slowModeButton.getText();
        }
        return null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateSlowModeText() {
        /*
            r9 = this;
            int r0 = r9.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r0)
            int r0 = r0.getCurrentTime()
            java.lang.Runnable r1 = r9.updateSlowModeRunnable
            im.bclpbkiauv.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
            r1 = 0
            r9.updateSlowModeRunnable = r1
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r1 = r9.info
            r2 = 2147483646(0x7ffffffe, float:NaN)
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x0068
            int r1 = r1.slowmode_seconds
            if (r1 == 0) goto L_0x0068
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r1 = r9.info
            int r1 = r1.slowmode_next_send_date
            if (r1 > r0) goto L_0x0068
            int r1 = r9.currentAccount
            im.bclpbkiauv.messenger.SendMessagesHelper r1 = im.bclpbkiauv.messenger.SendMessagesHelper.getInstance(r1)
            long r5 = r9.dialog_id
            boolean r1 = r1.isUploadingMessageIdDialog(r5)
            r5 = r1
            if (r1 != 0) goto L_0x0042
            int r1 = r9.currentAccount
            im.bclpbkiauv.messenger.SendMessagesHelper r1 = im.bclpbkiauv.messenger.SendMessagesHelper.getInstance(r1)
            long r6 = r9.dialog_id
            boolean r1 = r1.isSendingMessageIdDialog(r6)
            if (r1 == 0) goto L_0x0068
        L_0x0042:
            im.bclpbkiauv.messenger.AccountInstance r1 = r9.accountInstance
            im.bclpbkiauv.messenger.MessagesController r1 = r1.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r6 = r9.info
            int r6 = r6.id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r1.getChat(r6)
            boolean r6 = im.bclpbkiauv.messenger.ChatObject.hasAdminRights(r1)
            if (r6 != 0) goto L_0x0066
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r6 = r9.info
            int r6 = r6.slowmode_seconds
            if (r5 == 0) goto L_0x0063
            r2 = 2147483647(0x7fffffff, float:NaN)
        L_0x0063:
            r9.slowModeTimer = r2
            goto L_0x0067
        L_0x0066:
            r6 = 0
        L_0x0067:
            goto L_0x0081
        L_0x0068:
            int r1 = r9.slowModeTimer
            if (r1 < r2) goto L_0x007f
            r6 = 0
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r1 = r9.info
            if (r1 == 0) goto L_0x0081
            im.bclpbkiauv.messenger.AccountInstance r1 = r9.accountInstance
            im.bclpbkiauv.messenger.MessagesController r1 = r1.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r9.info
            int r2 = r2.id
            r1.loadFullChat(r2, r4, r3)
            goto L_0x0081
        L_0x007f:
            int r6 = r1 - r0
        L_0x0081:
            int r1 = r9.slowModeTimer
            if (r1 == 0) goto L_0x00c6
            if (r6 <= 0) goto L_0x00c6
            int r1 = r6 / 60
            int r2 = r1 * 60
            int r2 = r6 - r2
            if (r1 != 0) goto L_0x0092
            if (r2 != 0) goto L_0x0092
            r2 = 1
        L_0x0092:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r5 = r9.slowModeButton
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.Integer r8 = java.lang.Integer.valueOf(r1)
            r7[r4] = r8
            java.lang.Integer r8 = java.lang.Integer.valueOf(r2)
            r7[r3] = r8
            java.lang.String r8 = "%d:%02d"
            java.lang.String r7 = java.lang.String.format(r8, r7)
            r5.setText(r7)
            im.bclpbkiauv.ui.components.ChatActivityEnterView$ChatActivityEnterViewDelegate r5 = r9.delegate
            if (r5 == 0) goto L_0x00b9
            im.bclpbkiauv.ui.actionbar.SimpleTextView r7 = r9.slowModeButton
            java.lang.CharSequence r8 = r7.getText()
            r5.onUpdateSlowModeButton(r7, r4, r8)
        L_0x00b9:
            im.bclpbkiauv.ui.components.-$$Lambda$ChatActivityEnterView$m1r8Ucp2575u-msn2FPTNqat9sA r4 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatActivityEnterView$m1r8Ucp2575u-msn2FPTNqat9sA
            r4.<init>()
            r9.updateSlowModeRunnable = r4
            r7 = 100
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r4, r7)
            goto L_0x00c8
        L_0x00c6:
            r9.slowModeTimer = r4
        L_0x00c8:
            boolean r1 = r9.isInScheduleMode()
            if (r1 != 0) goto L_0x00d1
            r9.checkSendButton(r3)
        L_0x00d1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ChatActivityEnterView.updateSlowModeText():void");
    }

    public void addTopView(View view, View lineView, int height) {
        if (view != null) {
            this.topLineView = lineView;
            lineView.setVisibility(8);
            this.topLineView.setAlpha(0.0f);
            addView(this.topLineView, LayoutHelper.createFrame(-1.0f, 1.0f, 51, 0.0f, (float) (height + 1), 0.0f, 0.0f));
            this.topView = view;
            view.setVisibility(8);
            this.topView.setTranslationY((float) height);
            addView(this.topView, 0, LayoutHelper.createFrame(-1.0f, (float) height, 51, 0.0f, 2.0f, 0.0f, 0.0f));
            this.needShowTopView = false;
        }
    }

    public void setForceShowSendButton(boolean value, boolean animated) {
        this.forceShowSendButton = value;
        checkSendButton(animated);
    }

    public void setAllowStickersAndGifs(boolean value, boolean value2) {
        if (!((this.allowStickers == value && this.allowGifs == value2) || this.emojiView == null)) {
            if (this.emojiViewVisible) {
                hidePopup(false);
            }
            this.sizeNotifierLayout.removeView(this.emojiView);
            this.emojiView = null;
        }
        this.allowStickers = value;
        this.allowGifs = value2;
        setEmojiButtonImage(false, !this.isPaused);
    }

    public void addEmojiToRecent(String code) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(code);
    }

    public void setOpenGifsTabFirst() {
        createEmojiView();
        MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
        this.emojiView.switchToGifRecent();
    }

    public void showTopView(boolean animated, boolean openKeyboard) {
        if (this.topView != null && !this.topViewShowed && getVisibility() == 0) {
            this.needShowTopView = true;
            this.topViewShowed = true;
            if (this.allowShowTopView) {
                this.topView.setVisibility(0);
                this.topLineView.setVisibility(0);
                AnimatorSet animatorSet = this.currentTopViewAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.currentTopViewAnimation = null;
                }
                resizeForTopView(true);
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentTopViewAnimation = animatorSet2;
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.topView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.topLineView, View.ALPHA, new float[]{1.0f})});
                    this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                AnimatorSet unused = ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }
                    });
                    this.currentTopViewAnimation.setDuration(250);
                    this.currentTopViewAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.currentTopViewAnimation.start();
                } else {
                    this.topView.setTranslationY(0.0f);
                    this.topLineView.setAlpha(1.0f);
                }
                if (this.recordedAudioPanel.getVisibility() == 0) {
                    return;
                }
                if (!this.forceShowSendButton || openKeyboard) {
                    this.messageEditText.requestFocus();
                    openKeyboard();
                }
            }
        } else if (this.recordedAudioPanel.getVisibility() == 0) {
        } else {
            if (!this.forceShowSendButton || openKeyboard) {
                openKeyboard();
            }
        }
    }

    public void onEditTimeExpired() {
        this.doneButtonContainer.setVisibility(8);
    }

    public void showEditDoneProgress(boolean show, boolean animated) {
        final boolean z = show;
        AnimatorSet animatorSet = this.doneButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animated) {
            this.doneButtonAnimation = new AnimatorSet();
            if (z) {
                this.doneButtonProgress.setVisibility(0);
                this.doneButtonContainer.setEnabled(false);
                this.doneButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, new float[]{1.0f})});
            } else {
                this.doneButtonImage.setVisibility(0);
                this.doneButtonContainer.setEnabled(true);
                this.doneButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneButtonProgress, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneButtonImage, View.ALPHA, new float[]{1.0f})});
            }
            this.doneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animation)) {
                        if (!z) {
                            ChatActivityEnterView.this.doneButtonProgress.setVisibility(4);
                        } else {
                            ChatActivityEnterView.this.doneButtonImage.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatActivityEnterView.this.doneButtonAnimation != null && ChatActivityEnterView.this.doneButtonAnimation.equals(animation)) {
                        AnimatorSet unused = ChatActivityEnterView.this.doneButtonAnimation = null;
                    }
                }
            });
            this.doneButtonAnimation.setDuration(150);
            this.doneButtonAnimation.start();
        } else if (z) {
            this.doneButtonImage.setScaleX(0.1f);
            this.doneButtonImage.setScaleY(0.1f);
            this.doneButtonImage.setAlpha(0.0f);
            this.doneButtonProgress.setScaleX(1.0f);
            this.doneButtonProgress.setScaleY(1.0f);
            this.doneButtonProgress.setAlpha(1.0f);
            this.doneButtonImage.setVisibility(4);
            this.doneButtonProgress.setVisibility(0);
            this.doneButtonContainer.setEnabled(false);
        } else {
            this.doneButtonProgress.setScaleX(0.1f);
            this.doneButtonProgress.setScaleY(0.1f);
            this.doneButtonProgress.setAlpha(0.0f);
            this.doneButtonImage.setScaleX(1.0f);
            this.doneButtonImage.setScaleY(1.0f);
            this.doneButtonImage.setAlpha(1.0f);
            this.doneButtonImage.setVisibility(0);
            this.doneButtonProgress.setVisibility(4);
            this.doneButtonContainer.setEnabled(true);
        }
    }

    public void hideTopView(boolean animated) {
        if (this.topView != null && this.topViewShowed) {
            this.topViewShowed = false;
            this.needShowTopView = false;
            if (this.allowShowTopView) {
                AnimatorSet animatorSet = this.currentTopViewAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.currentTopViewAnimation = null;
                }
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentTopViewAnimation = animatorSet2;
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.topView, View.TRANSLATION_Y, new float[]{(float) this.topView.getLayoutParams().height}), ObjectAnimator.ofFloat(this.topLineView, View.ALPHA, new float[]{0.0f})});
                    this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                ChatActivityEnterView.this.topView.setVisibility(8);
                                ChatActivityEnterView.this.topLineView.setVisibility(8);
                                ChatActivityEnterView.this.resizeForTopView(false);
                                AnimatorSet unused = ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                AnimatorSet unused = ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }
                    });
                    this.currentTopViewAnimation.setDuration(200);
                    this.currentTopViewAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.currentTopViewAnimation.start();
                    return;
                }
                this.topView.setVisibility(8);
                this.topLineView.setVisibility(8);
                this.topLineView.setAlpha(0.0f);
                resizeForTopView(false);
                View view = this.topView;
                view.setTranslationY((float) view.getLayoutParams().height);
            }
        }
    }

    public boolean isTopViewVisible() {
        View view = this.topView;
        return view != null && view.getVisibility() == 0;
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onWindowSizeChanged(size);
        }
        if (this.topView == null) {
            return;
        }
        if (size < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
            if (this.allowShowTopView) {
                this.allowShowTopView = false;
                if (this.needShowTopView) {
                    this.topView.setVisibility(8);
                    this.topLineView.setVisibility(8);
                    this.topLineView.setAlpha(0.0f);
                    resizeForTopView(false);
                    View view = this.topView;
                    view.setTranslationY((float) view.getLayoutParams().height);
                }
            }
        } else if (!this.allowShowTopView) {
            this.allowShowTopView = true;
            if (this.needShowTopView) {
                this.topView.setVisibility(0);
                this.topLineView.setVisibility(0);
                this.topLineView.setAlpha(1.0f);
                resizeForTopView(true);
                this.topView.setTranslationY(0.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void resizeForTopView(boolean show) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textFieldContainer.getLayoutParams();
        layoutParams.topMargin = AndroidUtilities.dp(2.0f) + (show ? this.topView.getLayoutParams().height : 0);
        this.textFieldContainer.setLayoutParams(layoutParams);
        setMinimumHeight(AndroidUtilities.dp(51.0f) + (show ? this.topView.getLayoutParams().height : 0));
        if (!this.stickersExpanded) {
            return;
        }
        if (this.searchingType == 0) {
            setStickersExpanded(false, true, false);
        } else {
            checkStickresExpandHeight();
        }
    }

    public void onDestroy() {
        this.destroyed = true;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.sendingMessagesChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.onDestroy();
        }
        Runnable runnable = this.updateSlowModeRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateSlowModeRunnable = null;
        }
        PowerManager.WakeLock wakeLock2 = this.wakeLock;
        if (wakeLock2 != null) {
            try {
                wakeLock2.release();
                this.wakeLock = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.setDelegate((SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate) null);
        }
    }

    public void checkChannelRights() {
        TLRPC.Chat chat;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && (chat = chatActivity.getCurrentChat()) != null) {
            this.audioVideoButtonContainer.setAlpha(ChatObject.canSendMedia(chat) ? 1.0f : 0.5f);
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.setStickersBanned(!ChatObject.canSendStickers(chat), chat.id);
            }
        }
    }

    public void onBeginHide() {
        Runnable runnable = this.focusRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.focusRunnable = null;
        }
    }

    public void onPause() {
        this.isPaused = true;
        closeKeyboard();
    }

    public void onResume() {
        this.isPaused = false;
        int visibility = getVisibility();
        if (this.showKeyboardOnResume) {
            this.showKeyboardOnResume = false;
            if (this.searchingType == 0) {
                this.messageEditText.requestFocus();
            }
            AndroidUtilities.showKeyboard(this.messageEditText);
            if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
                this.waitingForKeyboardOpen = true;
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
            }
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.messageEditText.setEnabled(visibility == 0);
    }

    public void setDialogId(long id, int account) {
        this.dialog_id = id;
        int i = this.currentAccount;
        if (i != account) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.recordStarted);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStartError);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStopped);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioDidSent);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRouteChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.sendingMessagesChanged);
            this.currentAccount = account;
            this.accountInstance = AccountInstance.getInstance(account);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStarted);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStartError);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordStopped);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioDidSent);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.audioRouteChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.sendingMessagesChanged);
        }
        updateScheduleButton(false);
        checkRoundVideo();
        updateFieldHint();
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        this.info = chatInfo;
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.setChatInfo(chatInfo);
        }
        setSlowModeTimer(chatInfo.slowmode_next_send_date);
    }

    public void checkRoundVideo() {
        if (!this.hasRecordVideo) {
            if (this.attachLayout == null || Build.VERSION.SDK_INT < 18) {
                this.hasRecordVideo = false;
                setRecordVideoButtonVisible(false, false);
                return;
            }
            long j = this.dialog_id;
            int lower_id = (int) j;
            int high_id = (int) (j >> 32);
            boolean z = true;
            if (lower_id != 0 || high_id == 0) {
                this.hasRecordVideo = true;
            } else if (AndroidUtilities.getPeerLayerVersion(this.accountInstance.getMessagesController().getEncryptedChat(Integer.valueOf(high_id)).layer) >= 66) {
                this.hasRecordVideo = true;
            }
            boolean isChannel = false;
            if (((int) this.dialog_id) < 0) {
                TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    z = false;
                }
                isChannel = z;
                if (isChannel && !chat.creator && (chat.admin_rights == null || !chat.admin_rights.post_messages)) {
                    this.hasRecordVideo = false;
                }
            }
            if (!SharedConfig.inappCamera) {
                this.hasRecordVideo = false;
            }
            if (this.hasRecordVideo) {
                if (SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera((Runnable) null);
                }
                setRecordVideoButtonVisible(MessagesController.getGlobalMainSettings().getBoolean(isChannel ? "currentModeVideoChannel" : "currentModeVideo", isChannel), false);
                return;
            }
            setRecordVideoButtonVisible(false, false);
        }
    }

    public boolean isInVideoMode() {
        return this.videoSendButton.getTag() != null;
    }

    public boolean hasRecordVideo() {
        return this.hasRecordVideo;
    }

    private void updateFieldHint() {
        boolean isChannel = false;
        if (((int) this.dialog_id) < 0) {
            TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
            isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
        }
        if (this.editingMessageObject == null && isChannel) {
        }
    }

    public void setReplyingMessageObject(MessageObject messageObject) {
        MessageObject messageObject2;
        if (messageObject != null) {
            if (this.botMessageObject == null && (messageObject2 = this.botButtonsMessageObject) != this.replyingMessageObject) {
                this.botMessageObject = messageObject2;
            }
            this.replyingMessageObject = messageObject;
            setButtons(messageObject, true);
        } else if (messageObject == null && this.replyingMessageObject == this.botButtonsMessageObject) {
            this.replyingMessageObject = null;
            setButtons(this.botMessageObject, false);
            this.botMessageObject = null;
        } else {
            this.replyingMessageObject = messageObject;
        }
        MediaController.getInstance().setReplyingMessage(messageObject);
    }

    public void setWebPage(TLRPC.WebPage webPage, boolean searchWebPages) {
        this.messageWebPage = webPage;
        this.messageWebPageSearch = searchWebPages;
    }

    public boolean isMessageWebPageSearchEnabled() {
        return this.messageWebPageSearch;
    }

    private void hideRecordedAudioPanel() {
        this.audioToSendPath = null;
        this.audioToSend = null;
        this.audioToSendMessageObject = null;
        this.videoToSendMessageObject = null;
        this.videoTimelineView.destroy();
        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordedAudioPanel, View.ALPHA, new float[]{0.0f})});
        AnimatorSet.setDuration(200);
        AnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
            }
        });
        AnimatorSet.start();
    }

    /* access modifiers changed from: private */
    public void sendMessage() {
        if (isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    ChatActivityEnterView.this.sendMessageInternal(z, i);
                }
            });
        } else {
            sendMessageInternal(true, 0);
        }
    }

    /* access modifiers changed from: private */
    public void sendMessageInternal(boolean notify, int scheduleDate) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        TLRPC.Chat chat;
        boolean z = notify;
        int i = scheduleDate;
        if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                TLRPC.Chat chat2 = chatActivity.getCurrentChat();
                if (this.parentFragment.getCurrentUser() != null || ((ChatObject.isChannel(chat2) && chat2.megagroup) || !ChatObject.isChannel(chat2))) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    edit.putBoolean("silent_" + this.dialog_id, !z).commit();
                }
            }
            if (this.stickersExpanded) {
                setStickersExpanded(false, true, false);
                if (this.searchingType != 0) {
                    this.emojiView.closeSearch(false);
                    this.emojiView.hideSearchKeyboard();
                }
            }
            if (this.videoToSendMessageObject != null) {
                this.delegate.needStartRecordVideo(4, z, i);
                hideRecordedAudioPanel();
                checkSendButton(true);
            } else if (this.audioToSend != null) {
                MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
                if (playing != null && playing == this.audioToSendMessageObject) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                }
                MessageObject messageObject = playing;
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.audioToSend, (VideoEditedInfo) null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, (String) null, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate, 0, (Object) null);
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                if (chatActivityEnterViewDelegate2 != null) {
                    chatActivityEnterViewDelegate2.onMessageSend((CharSequence) null, notify, scheduleDate);
                } else {
                    boolean z2 = notify;
                    int i2 = scheduleDate;
                }
                hideRecordedAudioPanel();
                checkSendButton(true);
            } else {
                int i3 = i;
                boolean z3 = z;
                CharSequence message = this.messageEditText.getText();
                ChatActivity chatActivity2 = this.parentFragment;
                if (chatActivity2 != null && (chat = chatActivity2.getCurrentChat()) != null && chat.slowmode_enabled && !ChatObject.hasAdminRights(chat)) {
                    if (message.length() > this.accountInstance.getMessagesController().maxMessageLength) {
                        AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendErrorTooLong", R.string.SlowmodeSendErrorTooLong));
                        return;
                    } else if (this.forceShowSendButton && message.length() > 0) {
                        AlertsCreator.showSimpleAlert(this.parentFragment, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError));
                        return;
                    }
                }
                if (processSendingText(message, z3, i3)) {
                    this.messageEditText.setText("");
                    this.lastTypingTimeSend = 0;
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                    if (chatActivityEnterViewDelegate3 != null) {
                        chatActivityEnterViewDelegate3.onMessageSend(message, z3, i3);
                    }
                } else if (this.forceShowSendButton && (chatActivityEnterViewDelegate = this.delegate) != null) {
                    chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, z3, i3);
                }
            }
        } else {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
            if (chatActivityEnterViewDelegate4 != null) {
                chatActivityEnterViewDelegate4.scrollToSendingMessage();
            }
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject != null) {
            this.delegate.onMessageEditEnd(true);
            showEditDoneProgress(true, true);
            CharSequence[] message = {this.messageEditText.getText()};
            this.editingMessageReqId = SendMessagesHelper.getInstance(this.currentAccount).editMessage(this.editingMessageObject, message[0].toString(), this.messageWebPageSearch, this.parentFragment, MediaDataController.getInstance(this.currentAccount).getEntities(message), this.editingMessageObject.scheduled ? this.editingMessageObject.messageOwner.date : 0, new Runnable() {
                public final void run() {
                    ChatActivityEnterView.this.lambda$doneEditingMessage$22$ChatActivityEnterView();
                }
            });
        }
    }

    public /* synthetic */ void lambda$doneEditingMessage$22$ChatActivityEnterView() {
        this.editingMessageReqId = 0;
        setEditingMessageObject((MessageObject) null, false);
    }

    public boolean processSendingText(CharSequence text, boolean notify, int scheduleDate) {
        CharSequence text2 = AndroidUtilities.getTrimmedString(text);
        int maxLength = this.accountInstance.getMessagesController().maxMessageLength;
        if (text2.length() == 0) {
            return false;
        }
        int count = (int) Math.ceil((double) (((float) text2.length()) / ((float) maxLength)));
        int a = 0;
        while (a < count) {
            CharSequence[] message = {text2.subSequence(a * maxLength, Math.min((a + 1) * maxLength, text2.length()))};
            ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(message);
            int lower_part = (int) this.dialog_id;
            if (lower_part >= 0 || ChatObject.canUserDoAction(MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(-lower_part)), 9) || !RegexUtils.hasLink(message[0].toString())) {
                int i = lower_part;
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(message[0].toString(), this.dialog_id, this.replyingMessageObject, this.messageWebPage, this.messageWebPageSearch, entities, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate);
                a++;
            } else {
                WalletDialogUtil.showSingleBtnWalletDialog(this.parentFragment, LocaleController.getString(R.string.YouHaveNoPermissionToSendMsgTips), (String) null, true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void checkSendButton(boolean animated) {
        boolean animated2;
        int color;
        int i;
        int i2;
        if (this.editingMessageObject == null) {
            if (this.isPaused) {
                animated2 = false;
            } else {
                animated2 = animated;
            }
            CharSequence message = AndroidUtilities.getTrimmedString(this.messageEditText.getText());
            int i3 = this.slowModeTimer;
            if (i3 <= 0 || i3 == Integer.MAX_VALUE || isInScheduleMode()) {
                if (message.length() <= 0 && !this.forceShowSendButton && this.audioToSend == null && this.videoToSendMessageObject == null) {
                    if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
                        if (this.emojiView == null || !this.emojiViewVisible || ((!this.stickersTabOpen && (!this.emojiTabOpen || this.searchingType != 2)) || AndroidUtilities.isInMultiwindow)) {
                            if (this.sendButton.getVisibility() != 0 && this.cancelBotButton.getVisibility() != 0 && this.expandStickersButton.getVisibility() != 0 && this.slowModeButton.getVisibility() != 0) {
                                return;
                            }
                            if (!animated2) {
                                this.slowModeButton.setScaleX(0.1f);
                                this.slowModeButton.setScaleY(0.1f);
                                this.slowModeButton.setAlpha(0.0f);
                                this.slowModeButton.setVisibility(8);
                                this.sendButton.setScaleX(0.1f);
                                this.sendButton.setScaleY(0.1f);
                                this.sendButton.setAlpha(0.0f);
                                this.sendButton.setVisibility(8);
                                this.cancelBotButton.setScaleX(0.1f);
                                this.cancelBotButton.setScaleY(0.1f);
                                this.cancelBotButton.setAlpha(0.0f);
                                this.cancelBotButton.setVisibility(8);
                                this.expandStickersButton.setScaleX(0.1f);
                                this.expandStickersButton.setScaleY(0.1f);
                                this.expandStickersButton.setAlpha(0.0f);
                                this.expandStickersButton.setVisibility(8);
                                this.audioVideoButtonContainer.setScaleX(1.0f);
                                this.audioVideoButtonContainer.setScaleY(1.0f);
                                this.audioVideoButtonContainer.setAlpha(1.0f);
                                this.audioVideoButtonContainer.setVisibility(0);
                                if (this.attachLayout != null) {
                                    if (getVisibility() == 0) {
                                        this.delegate.onAttachButtonShow();
                                    }
                                    this.attachLayout.setAlpha(1.0f);
                                    this.attachLayout.setScaleX(1.0f);
                                    this.attachLayout.setVisibility(0);
                                    updateFieldRight(1);
                                }
                                this.scheduleButtonHidden = false;
                                if (this.scheduledButton != null) {
                                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                                    if (chatActivityEnterViewDelegate != null && chatActivityEnterViewDelegate.hasScheduledMessages()) {
                                        this.scheduledButton.setVisibility(0);
                                        this.scheduledButton.setTag(1);
                                    }
                                    this.scheduledButton.setAlpha(1.0f);
                                    this.scheduledButton.setScaleX(1.0f);
                                    this.scheduledButton.setScaleY(1.0f);
                                    this.scheduledButton.setTranslationX(0.0f);
                                    return;
                                }
                                return;
                            } else if (this.runningAnimationType != 2) {
                                AnimatorSet animatorSet = this.runningAnimation;
                                if (animatorSet != null) {
                                    animatorSet.cancel();
                                    this.runningAnimation = null;
                                }
                                AnimatorSet animatorSet2 = this.runningAnimation2;
                                if (animatorSet2 != null) {
                                    animatorSet2.cancel();
                                    this.runningAnimation2 = null;
                                }
                                LinearLayout linearLayout = this.attachLayout;
                                if (linearLayout != null) {
                                    linearLayout.setVisibility(0);
                                    this.runningAnimation2 = new AnimatorSet();
                                    ArrayList<Animator> animators = new ArrayList<>();
                                    animators.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f}));
                                    animators.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f}));
                                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                                    boolean hasScheduled = chatActivityEnterViewDelegate2 != null && chatActivityEnterViewDelegate2.hasScheduledMessages();
                                    this.scheduleButtonHidden = false;
                                    ImageView imageView = this.scheduledButton;
                                    if (imageView != null) {
                                        if (hasScheduled) {
                                            imageView.setVisibility(0);
                                            this.scheduledButton.setTag(1);
                                            this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                            animators.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                            animators.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                                            animators.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f}));
                                        } else {
                                            imageView.setAlpha(1.0f);
                                            this.scheduledButton.setScaleX(1.0f);
                                            this.scheduledButton.setScaleY(1.0f);
                                            this.scheduledButton.setTranslationX(0.0f);
                                        }
                                    }
                                    this.runningAnimation2.playTogether(animators);
                                    this.runningAnimation2.setDuration(100);
                                    this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animation) {
                                            if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                            }
                                        }

                                        public void onAnimationCancel(Animator animation) {
                                            if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                            }
                                        }
                                    });
                                    this.runningAnimation2.start();
                                    updateFieldRight(1);
                                    if (getVisibility() == 0) {
                                        this.delegate.onAttachButtonShow();
                                    }
                                }
                                this.audioVideoButtonContainer.setVisibility(0);
                                this.runningAnimation = new AnimatorSet();
                                this.runningAnimationType = 2;
                                ArrayList<Animator> animators2 = new ArrayList<>();
                                animators2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{1.0f}));
                                animators2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{1.0f}));
                                animators2.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f}));
                                if (this.cancelBotButton.getVisibility() == 0) {
                                    animators2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                                } else if (this.expandStickersButton.getVisibility() == 0) {
                                    animators2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                                } else if (this.slowModeButton.getVisibility() == 0) {
                                    animators2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                                } else {
                                    animators2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                                    animators2.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                                }
                                this.runningAnimation.playTogether(animators2);
                                this.runningAnimation.setDuration(150);
                                this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                            ChatActivityEnterView.this.sendButton.setVisibility(8);
                                            ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                            ChatActivityEnterView.this.slowModeButton.setVisibility(8);
                                            ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                            int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animation) {
                                        if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                        }
                                    }
                                });
                                this.runningAnimation.start();
                                return;
                            } else {
                                return;
                            }
                        } else if (!animated2) {
                            this.slowModeButton.setScaleX(0.1f);
                            this.slowModeButton.setScaleY(0.1f);
                            this.slowModeButton.setAlpha(0.0f);
                            this.slowModeButton.setVisibility(8);
                            this.sendButton.setScaleX(0.1f);
                            this.sendButton.setScaleY(0.1f);
                            this.sendButton.setAlpha(0.0f);
                            this.sendButton.setVisibility(8);
                            this.cancelBotButton.setScaleX(0.1f);
                            this.cancelBotButton.setScaleY(0.1f);
                            this.cancelBotButton.setAlpha(0.0f);
                            this.cancelBotButton.setVisibility(8);
                            this.audioVideoButtonContainer.setScaleX(0.1f);
                            this.audioVideoButtonContainer.setScaleY(0.1f);
                            this.audioVideoButtonContainer.setAlpha(0.0f);
                            this.audioVideoButtonContainer.setVisibility(8);
                            this.expandStickersButton.setScaleX(1.0f);
                            this.expandStickersButton.setScaleY(1.0f);
                            this.expandStickersButton.setAlpha(1.0f);
                            this.expandStickersButton.setVisibility(0);
                            if (this.attachLayout != null) {
                                if (getVisibility() == 0) {
                                    this.delegate.onAttachButtonShow();
                                }
                                this.attachLayout.setVisibility(0);
                                updateFieldRight(1);
                            }
                            this.scheduleButtonHidden = false;
                            if (this.scheduledButton != null) {
                                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate3 = this.delegate;
                                if (chatActivityEnterViewDelegate3 != null && chatActivityEnterViewDelegate3.hasScheduledMessages()) {
                                    this.scheduledButton.setVisibility(0);
                                    this.scheduledButton.setTag(1);
                                }
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                                this.scheduledButton.setScaleY(1.0f);
                                this.scheduledButton.setTranslationX(0.0f);
                                return;
                            }
                            return;
                        } else if (this.runningAnimationType != 4) {
                            AnimatorSet animatorSet3 = this.runningAnimation;
                            if (animatorSet3 != null) {
                                animatorSet3.cancel();
                                this.runningAnimation = null;
                            }
                            AnimatorSet animatorSet4 = this.runningAnimation2;
                            if (animatorSet4 != null) {
                                animatorSet4.cancel();
                                this.runningAnimation2 = null;
                            }
                            LinearLayout linearLayout2 = this.attachLayout;
                            if (linearLayout2 != null) {
                                linearLayout2.setVisibility(0);
                                this.runningAnimation2 = new AnimatorSet();
                                ArrayList<Animator> animators3 = new ArrayList<>();
                                animators3.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{1.0f}));
                                animators3.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{1.0f}));
                                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate4 = this.delegate;
                                boolean hasScheduled2 = chatActivityEnterViewDelegate4 != null && chatActivityEnterViewDelegate4.hasScheduledMessages();
                                this.scheduleButtonHidden = false;
                                ImageView imageView2 = this.scheduledButton;
                                if (imageView2 != null) {
                                    imageView2.setScaleY(1.0f);
                                    if (hasScheduled2) {
                                        this.scheduledButton.setVisibility(0);
                                        this.scheduledButton.setTag(1);
                                        this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                        animators3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                        animators3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                                        animators3.add(ObjectAnimator.ofFloat(this.scheduledButton, View.TRANSLATION_X, new float[]{0.0f}));
                                    } else {
                                        this.scheduledButton.setAlpha(1.0f);
                                        this.scheduledButton.setScaleX(1.0f);
                                        this.scheduledButton.setTranslationX(0.0f);
                                    }
                                }
                                this.runningAnimation2.playTogether(animators3);
                                this.runningAnimation2.setDuration(100);
                                this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animation) {
                                        if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                        }
                                    }
                                });
                                this.runningAnimation2.start();
                                updateFieldRight(1);
                                if (getVisibility() == 0) {
                                    this.delegate.onAttachButtonShow();
                                }
                            }
                            this.expandStickersButton.setVisibility(0);
                            this.runningAnimation = new AnimatorSet();
                            this.runningAnimationType = 4;
                            ArrayList<Animator> animators4 = new ArrayList<>();
                            animators4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{1.0f}));
                            animators4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{1.0f}));
                            animators4.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{1.0f}));
                            if (this.cancelBotButton.getVisibility() == 0) {
                                animators4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                            } else if (this.audioVideoButtonContainer.getVisibility() == 0) {
                                animators4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                            } else if (this.slowModeButton.getVisibility() == 0) {
                                animators4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                            } else {
                                animators4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                                animators4.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                            }
                            this.runningAnimation.playTogether(animators4);
                            this.runningAnimation.setDuration(150);
                            this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                        ChatActivityEnterView.this.sendButton.setVisibility(8);
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                        ChatActivityEnterView.this.slowModeButton.setVisibility(8);
                                        ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                        ChatActivityEnterView.this.expandStickersButton.setVisibility(0);
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                        int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                                    }
                                }

                                public void onAnimationCancel(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                    }
                                }
                            });
                            this.runningAnimation.start();
                            return;
                        } else {
                            return;
                        }
                    }
                }
                final String caption = this.messageEditText.getCaption();
                boolean showBotButton = caption != null && (this.sendButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
                boolean showSendButton = caption == null && (this.cancelBotButton.getVisibility() == 0 || this.expandStickersButton.getVisibility() == 0);
                if (this.slowModeTimer != Integer.MAX_VALUE || isInScheduleMode()) {
                    color = Theme.getColor(Theme.key_chat_messagePanelSend);
                } else {
                    color = Theme.getColor(Theme.key_chat_messagePanelIcons);
                }
                Theme.setSelectorDrawableColor(this.sendButton.getBackground(), Color.argb(24, Color.red(color), Color.green(color), Color.blue(color)), true);
                if (this.audioVideoButtonContainer.getVisibility() != 0 && this.slowModeButton.getVisibility() != 0 && !showBotButton && !showSendButton) {
                    return;
                }
                if (!animated2) {
                    this.audioVideoButtonContainer.setScaleX(0.1f);
                    this.audioVideoButtonContainer.setScaleY(0.1f);
                    this.audioVideoButtonContainer.setAlpha(0.0f);
                    this.audioVideoButtonContainer.setVisibility(8);
                    if (this.slowModeButton.getVisibility() == 0) {
                        this.slowModeButton.setScaleX(0.1f);
                        this.slowModeButton.setScaleY(0.1f);
                        this.slowModeButton.setAlpha(0.0f);
                        this.slowModeButton.setVisibility(8);
                    }
                    if (caption != null) {
                        this.sendButton.setScaleX(0.1f);
                        this.sendButton.setScaleY(0.1f);
                        this.sendButton.setAlpha(0.0f);
                        this.sendButton.setVisibility(8);
                        this.cancelBotButton.setScaleX(1.0f);
                        this.cancelBotButton.setScaleY(1.0f);
                        this.cancelBotButton.setAlpha(1.0f);
                        this.cancelBotButton.setVisibility(0);
                    } else {
                        this.cancelBotButton.setScaleX(0.1f);
                        this.cancelBotButton.setScaleY(0.1f);
                        this.cancelBotButton.setAlpha(0.0f);
                        this.sendButton.setVisibility(0);
                        this.sendButton.setScaleX(1.0f);
                        this.sendButton.setScaleY(1.0f);
                        this.sendButton.setAlpha(1.0f);
                        this.cancelBotButton.setVisibility(8);
                    }
                    if (this.expandStickersButton.getVisibility() == 0) {
                        this.expandStickersButton.setScaleX(0.1f);
                        this.expandStickersButton.setScaleY(0.1f);
                        this.expandStickersButton.setAlpha(0.0f);
                        i = 8;
                        this.expandStickersButton.setVisibility(8);
                    } else {
                        i = 8;
                    }
                    LinearLayout linearLayout3 = this.attachLayout;
                    if (linearLayout3 != null) {
                        linearLayout3.setVisibility(i);
                        if (this.delegate != null && getVisibility() == 0) {
                            this.delegate.onAttachButtonHidden();
                        }
                        updateFieldRight(0);
                    }
                    this.scheduleButtonHidden = true;
                    if (this.scheduledButton != null) {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate5 = this.delegate;
                        if (chatActivityEnterViewDelegate5 != null && chatActivityEnterViewDelegate5.hasScheduledMessages()) {
                            this.scheduledButton.setVisibility(8);
                            this.scheduledButton.setTag((Object) null);
                        }
                        this.scheduledButton.setAlpha(0.0f);
                        this.scheduledButton.setScaleX(0.0f);
                        this.scheduledButton.setScaleY(1.0f);
                        ImageView imageView3 = this.scheduledButton;
                        ImageView imageView4 = this.botButton;
                        imageView3.setTranslationX((float) AndroidUtilities.dp((imageView4 == null || imageView4.getVisibility() == 8) ? 48.0f : 96.0f));
                    }
                } else if (this.runningAnimationType != 1 || this.messageEditText.getCaption() != null) {
                    if (this.runningAnimationType != 3 || caption == null) {
                        AnimatorSet animatorSet5 = this.runningAnimation;
                        if (animatorSet5 != null) {
                            animatorSet5.cancel();
                            this.runningAnimation = null;
                        }
                        AnimatorSet animatorSet6 = this.runningAnimation2;
                        if (animatorSet6 != null) {
                            animatorSet6.cancel();
                            this.runningAnimation2 = null;
                        }
                        if (this.attachLayout != null) {
                            this.runningAnimation2 = new AnimatorSet();
                            ArrayList<Animator> animators5 = new ArrayList<>();
                            animators5.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f}));
                            animators5.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{0.0f}));
                            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate6 = this.delegate;
                            final boolean hasScheduled3 = chatActivityEnterViewDelegate6 != null && chatActivityEnterViewDelegate6.hasScheduledMessages();
                            this.scheduleButtonHidden = true;
                            ImageView imageView5 = this.scheduledButton;
                            if (imageView5 != null) {
                                imageView5.setScaleY(1.0f);
                                if (hasScheduled3) {
                                    this.scheduledButton.setTag((Object) null);
                                    animators5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{0.0f}));
                                    animators5.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{0.0f}));
                                    ImageView imageView6 = this.scheduledButton;
                                    Property property = View.TRANSLATION_X;
                                    float[] fArr = new float[1];
                                    ImageView imageView7 = this.botButton;
                                    fArr[0] = (float) AndroidUtilities.dp((imageView7 == null || imageView7.getVisibility() == 8) ? 48.0f : 96.0f);
                                    animators5.add(ObjectAnimator.ofFloat(imageView6, property, fArr));
                                } else {
                                    this.scheduledButton.setAlpha(0.0f);
                                    this.scheduledButton.setScaleX(0.0f);
                                    ImageView imageView8 = this.scheduledButton;
                                    ImageView imageView9 = this.botButton;
                                    imageView8.setTranslationX((float) AndroidUtilities.dp((imageView9 == null || imageView9.getVisibility() == 8) ? 48.0f : 96.0f));
                                }
                            }
                            this.runningAnimation2.playTogether(animators5);
                            this.runningAnimation2.setDuration(100);
                            this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                        ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                        if (hasScheduled3) {
                                            ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                                        }
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }

                                public void onAnimationCancel(Animator animation) {
                                    if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }
                            });
                            this.runningAnimation2.start();
                            updateFieldRight(0);
                            if (this.delegate != null && getVisibility() == 0) {
                                this.delegate.onAttachButtonHidden();
                            }
                        }
                        this.runningAnimation = new AnimatorSet();
                        ArrayList<Animator> animators6 = new ArrayList<>();
                        if (this.audioVideoButtonContainer.getVisibility() == 0) {
                            animators6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                        }
                        if (this.expandStickersButton.getVisibility() == 0) {
                            animators6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                        }
                        if (this.slowModeButton.getVisibility() == 0) {
                            animators6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{0.0f}));
                        }
                        if (showBotButton) {
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                        } else if (showSendButton) {
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                        }
                        if (caption != null) {
                            this.runningAnimationType = 3;
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{1.0f}));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{1.0f}));
                            animators6.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{1.0f}));
                            this.cancelBotButton.setVisibility(0);
                        } else {
                            this.runningAnimationType = 1;
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{1.0f}));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{1.0f}));
                            animators6.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{1.0f}));
                            this.sendButton.setVisibility(0);
                        }
                        this.runningAnimation.playTogether(animators6);
                        this.runningAnimation.setDuration(150);
                        this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    if (caption != null) {
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(0);
                                        ChatActivityEnterView.this.sendButton.setVisibility(8);
                                    } else {
                                        ChatActivityEnterView.this.sendButton.setVisibility(0);
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                    }
                                    ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                    ChatActivityEnterView.this.expandStickersButton.setVisibility(8);
                                    ChatActivityEnterView.this.slowModeButton.setVisibility(8);
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                    int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                }
                            }
                        });
                        this.runningAnimation.start();
                    }
                }
            } else if (this.slowModeButton.getVisibility() == 0) {
            } else {
                if (!animated2) {
                    this.slowModeButton.setScaleX(1.0f);
                    this.slowModeButton.setScaleY(1.0f);
                    this.slowModeButton.setAlpha(1.0f);
                    this.slowModeButton.setVisibility(0);
                    this.audioVideoButtonContainer.setScaleX(0.1f);
                    this.audioVideoButtonContainer.setScaleY(0.1f);
                    this.audioVideoButtonContainer.setAlpha(0.0f);
                    this.audioVideoButtonContainer.setVisibility(8);
                    this.sendButton.setScaleX(0.1f);
                    this.sendButton.setScaleY(0.1f);
                    this.sendButton.setAlpha(0.0f);
                    this.sendButton.setVisibility(8);
                    this.cancelBotButton.setScaleX(0.1f);
                    this.cancelBotButton.setScaleY(0.1f);
                    this.cancelBotButton.setAlpha(0.0f);
                    this.cancelBotButton.setVisibility(8);
                    if (this.expandStickersButton.getVisibility() == 0) {
                        this.expandStickersButton.setScaleX(0.1f);
                        this.expandStickersButton.setScaleY(0.1f);
                        this.expandStickersButton.setAlpha(0.0f);
                        i2 = 8;
                        this.expandStickersButton.setVisibility(8);
                    } else {
                        i2 = 8;
                    }
                    LinearLayout linearLayout4 = this.attachLayout;
                    if (linearLayout4 != null) {
                        linearLayout4.setVisibility(i2);
                        updateFieldRight(0);
                    }
                    this.scheduleButtonHidden = false;
                    if (this.scheduledButton != null) {
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate7 = this.delegate;
                        if (chatActivityEnterViewDelegate7 != null && chatActivityEnterViewDelegate7.hasScheduledMessages()) {
                            this.scheduledButton.setVisibility(0);
                            this.scheduledButton.setTag(1);
                        }
                        ImageView imageView10 = this.scheduledButton;
                        ImageView imageView11 = this.botButton;
                        imageView10.setTranslationX((float) AndroidUtilities.dp((imageView11 == null || imageView11.getVisibility() != 0) ? 48.0f : 96.0f));
                        this.scheduledButton.setAlpha(1.0f);
                        this.scheduledButton.setScaleX(1.0f);
                        this.scheduledButton.setScaleY(1.0f);
                    }
                } else if (this.runningAnimationType != 5) {
                    AnimatorSet animatorSet7 = this.runningAnimation;
                    if (animatorSet7 != null) {
                        animatorSet7.cancel();
                        this.runningAnimation = null;
                    }
                    AnimatorSet animatorSet8 = this.runningAnimation2;
                    if (animatorSet8 != null) {
                        animatorSet8.cancel();
                        this.runningAnimation2 = null;
                    }
                    if (this.attachLayout != null) {
                        this.runningAnimation2 = new AnimatorSet();
                        ArrayList<Animator> animators7 = new ArrayList<>();
                        animators7.add(ObjectAnimator.ofFloat(this.attachLayout, View.ALPHA, new float[]{0.0f}));
                        animators7.add(ObjectAnimator.ofFloat(this.attachLayout, View.SCALE_X, new float[]{0.0f}));
                        this.scheduleButtonHidden = false;
                        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate8 = this.delegate;
                        boolean hasScheduled4 = chatActivityEnterViewDelegate8 != null && chatActivityEnterViewDelegate8.hasScheduledMessages();
                        ImageView imageView12 = this.scheduledButton;
                        if (imageView12 != null) {
                            imageView12.setScaleY(1.0f);
                            if (hasScheduled4) {
                                this.scheduledButton.setVisibility(0);
                                this.scheduledButton.setTag(1);
                                this.scheduledButton.setPivotX((float) AndroidUtilities.dp(48.0f));
                                ImageView imageView13 = this.scheduledButton;
                                Property property2 = View.TRANSLATION_X;
                                float[] fArr2 = new float[1];
                                ImageView imageView14 = this.botButton;
                                fArr2[0] = (float) AndroidUtilities.dp((imageView14 == null || imageView14.getVisibility() != 0) ? 48.0f : 96.0f);
                                animators7.add(ObjectAnimator.ofFloat(imageView13, property2, fArr2));
                                animators7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.ALPHA, new float[]{1.0f}));
                                animators7.add(ObjectAnimator.ofFloat(this.scheduledButton, View.SCALE_X, new float[]{1.0f}));
                            } else {
                                ImageView imageView15 = this.scheduledButton;
                                ImageView imageView16 = this.botButton;
                                imageView15.setTranslationX((float) AndroidUtilities.dp((imageView16 == null || imageView16.getVisibility() != 0) ? 48.0f : 96.0f));
                                this.scheduledButton.setAlpha(1.0f);
                                this.scheduledButton.setScaleX(1.0f);
                            }
                        }
                        this.runningAnimation2.playTogether(animators7);
                        this.runningAnimation2.setDuration(100);
                        this.runningAnimation2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    ChatActivityEnterView.this.attachLayout.setVisibility(8);
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (animation.equals(ChatActivityEnterView.this.runningAnimation2)) {
                                    AnimatorSet unused = ChatActivityEnterView.this.runningAnimation2 = null;
                                }
                            }
                        });
                        this.runningAnimation2.start();
                        updateFieldRight(0);
                    }
                    this.runningAnimationType = 5;
                    this.runningAnimation = new AnimatorSet();
                    ArrayList<Animator> animators8 = new ArrayList<>();
                    if (this.audioVideoButtonContainer.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_X, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.SCALE_Y, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.expandStickersButton.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_X, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.SCALE_Y, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.expandStickersButton, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.sendButton.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_X, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.SCALE_Y, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.sendButton, View.ALPHA, new float[]{0.0f}));
                    }
                    if (this.cancelBotButton.getVisibility() == 0) {
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_X, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.SCALE_Y, new float[]{0.1f}));
                        animators8.add(ObjectAnimator.ofFloat(this.cancelBotButton, View.ALPHA, new float[]{0.0f}));
                    }
                    animators8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_X, new float[]{1.0f}));
                    animators8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.SCALE_Y, new float[]{1.0f}));
                    animators8.add(ObjectAnimator.ofFloat(this.slowModeButton, View.ALPHA, new float[]{1.0f}));
                    this.slowModeButton.setVisibility(0);
                    this.runningAnimation.playTogether(animators8);
                    this.runningAnimation.setDuration(150);
                    this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                ChatActivityEnterView.this.sendButton.setVisibility(8);
                                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                                ChatActivityEnterView.this.expandStickersButton.setVisibility(8);
                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                                int unused2 = ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(ChatActivityEnterView.this.runningAnimation)) {
                                AnimatorSet unused = ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    this.runningAnimation.start();
                }
            }
        }
    }

    private void updateFieldRight(int attachVisible) {
        ImageView imageView;
        ImageView imageView2;
        ImageView imageView3;
        ImageView imageView4;
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null && this.editingMessageObject == null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) editTextCaption.getLayoutParams();
            if (attachVisible == 1) {
                ImageView imageView5 = this.botButton;
                if ((imageView5 == null || imageView5.getVisibility() != 0) && (((imageView3 = this.notifyButton) == null || imageView3.getVisibility() != 0) && ((imageView4 = this.scheduledButton) == null || imageView4.getTag() == null))) {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                }
            } else if (attachVisible != 2) {
                ImageView imageView6 = this.scheduledButton;
                if (imageView6 == null || imageView6.getTag() == null) {
                    layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                }
            } else if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
                ImageView imageView7 = this.botButton;
                if ((imageView7 == null || imageView7.getVisibility() != 0) && (((imageView = this.notifyButton) == null || imageView.getVisibility() != 0) && ((imageView2 = this.scheduledButton) == null || imageView2.getTag() == null))) {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                }
            }
            this.messageEditText.setLayoutParams(layoutParams);
        }
    }

    /* access modifiers changed from: private */
    public void updateRecordIntefrace() {
        if (!this.recordingAudioVideo) {
            PowerManager.WakeLock wakeLock2 = this.wakeLock;
            if (wakeLock2 != null) {
                try {
                    wakeLock2.release();
                    this.wakeLock = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            AndroidUtilities.unlockOrientation(this.parentActivity);
            if (this.recordInterfaceState != 0) {
                this.recordInterfaceState = 0;
                AnimatorSet animatorSet = this.runningAnimationAudio;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.runningAnimationAudio = animatorSet2;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordPanel, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.displaySize.x}), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{0.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{1.0f})});
                this.runningAnimationAudio.setDuration(300);
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                            params.leftMargin = AndroidUtilities.dp(30.0f);
                            ChatActivityEnterView.this.slideText.setLayoutParams(params);
                            ChatActivityEnterView.this.slideText.setAlpha(1.0f);
                            ChatActivityEnterView.this.recordPanel.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setSendButtonInvisible();
                            AnimatorSet unused = ChatActivityEnterView.this.runningAnimationAudio = null;
                        }
                    }
                });
                this.runningAnimationAudio.setInterpolator(new AccelerateInterpolator());
                this.runningAnimationAudio.start();
            }
        } else if (this.recordInterfaceState != 1) {
            this.recordInterfaceState = 1;
            try {
                if (this.wakeLock == null) {
                    PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(536870918, "hchat:audio_record_lock");
                    this.wakeLock = newWakeLock;
                    newWakeLock.acquire();
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            AndroidUtilities.lockOrientation(this.parentActivity);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.needStartRecordAudio(0);
            }
            this.recordPanel.setVisibility(0);
            this.recordCircle.setVisibility(0);
            this.recordCircle.setAmplitude(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
            this.recordTimeText.setText(String.format("%02d:%02d.%02d", new Object[]{0, 0, 0}));
            this.recordDot.resetAlpha();
            this.lastTimeString = null;
            this.lastTypingSendTime = -1;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.slideText.getLayoutParams();
            params.leftMargin = AndroidUtilities.dp(30.0f);
            this.slideText.setLayoutParams(params);
            this.slideText.setAlpha(1.0f);
            this.recordPanel.setX((float) AndroidUtilities.displaySize.x);
            AnimatorSet animatorSet3 = this.runningAnimationAudio;
            if (animatorSet3 != null) {
                animatorSet3.cancel();
            }
            AnimatorSet animatorSet4 = new AnimatorSet();
            this.runningAnimationAudio = animatorSet4;
            animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.recordPanel, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.recordCircle, this.recordCircleScale, new float[]{1.0f}), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, View.ALPHA, new float[]{0.0f})});
            this.runningAnimationAudio.setDuration(300);
            this.runningAnimationAudio.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChatActivityEnterView.this.runningAnimationAudio)) {
                        ChatActivityEnterView.this.recordPanel.setX(0.0f);
                        AnimatorSet unused = ChatActivityEnterView.this.runningAnimationAudio = null;
                    }
                }
            });
            this.runningAnimationAudio.setInterpolator(new DecelerateInterpolator());
            this.runningAnimationAudio.start();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.recordingAudioVideo) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setDelegate(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
        this.delegate = chatActivityEnterViewDelegate;
    }

    public void setCommand(MessageObject messageObject, String command, boolean longPress, boolean username) {
        String text;
        MessageObject messageObject2 = messageObject;
        String str = command;
        if (str != null && getVisibility() == 0) {
            TLRPC.User user = null;
            if (longPress) {
                String text2 = this.messageEditText.getText().toString();
                if (messageObject2 != null && ((int) this.dialog_id) < 0) {
                    user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                }
                if ((this.botCount != 1 || username) && user != null && user.bot && !str.contains("@")) {
                    text = String.format(Locale.US, "%s@%s", new Object[]{str, user.username}) + " " + text2.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
                } else {
                    text = str + " " + text2.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");
                }
                this.ignoreTextChange = true;
                this.messageEditText.setText(text);
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setSelection(editTextCaption.getText().length());
                this.ignoreTextChange = false;
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                if (chatActivityEnterViewDelegate != null) {
                    chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
                }
                if (!this.keyboardVisible && this.currentPopupContentType == -1) {
                    openKeyboard();
                }
            } else if (this.slowModeTimer <= 0 || isInScheduleMode()) {
                if (messageObject2 != null && ((int) this.dialog_id) < 0) {
                    user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                }
                TLRPC.User user2 = user;
                if ((this.botCount != 1 || username) && user2 != null && user2.bot && !str.contains("@")) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(String.format(Locale.US, "%s@%s", new Object[]{str, user2.username}), this.dialog_id, this.replyingMessageObject, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                    return;
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(command, this.dialog_id, this.replyingMessageObject, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            } else {
                ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
                if (chatActivityEnterViewDelegate2 != null) {
                    SimpleTextView simpleTextView = this.slowModeButton;
                    chatActivityEnterViewDelegate2.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
                }
            }
        }
    }

    public void setEditingMessageObject(MessageObject messageObject, boolean caption) {
        CharSequence editingText;
        MessageObject messageObject2 = messageObject;
        boolean z = caption;
        if (this.audioToSend == null && this.videoToSendMessageObject == null && this.editingMessageObject != messageObject2) {
            int i = 1;
            if (this.editingMessageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.editingMessageReqId, true);
                this.editingMessageReqId = 0;
            }
            this.editingMessageObject = messageObject2;
            this.editingCaption = z;
            if (messageObject2 != null) {
                AnimatorSet animatorSet = this.doneButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.doneButtonAnimation = null;
                }
                this.doneButtonContainer.setVisibility(0);
                showEditDoneProgress(true, false);
                InputFilter[] inputFilters = new InputFilter[1];
                if (z) {
                    inputFilters[0] = new InputFilter.LengthFilter(this.accountInstance.getMessagesController().maxCaptionLength);
                    editingText = this.editingMessageObject.caption;
                } else {
                    inputFilters[0] = new InputFilter.LengthFilter(this.accountInstance.getMessagesController().maxMessageLength);
                    editingText = this.editingMessageObject.messageText;
                }
                if (editingText != null) {
                    ArrayList<TLRPC.MessageEntity> entities = this.editingMessageObject.messageOwner.entities;
                    MediaDataController.sortEntities(entities);
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(editingText);
                    Object[] spansToRemove = stringBuilder.getSpans(0, stringBuilder.length(), Object.class);
                    if (spansToRemove != null && spansToRemove.length > 0) {
                        for (Object removeSpan : spansToRemove) {
                            stringBuilder.removeSpan(removeSpan);
                        }
                    }
                    if (entities != null) {
                        int a = 0;
                        while (a < entities.size()) {
                            try {
                                TLRPC.MessageEntity entity = entities.get(a);
                                if (entity.offset + entity.length <= stringBuilder.length()) {
                                    if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                                        if (entity.offset + entity.length < stringBuilder.length() && stringBuilder.charAt(entity.offset + entity.length) == ' ') {
                                            entity.length += i;
                                        }
                                        stringBuilder.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName) entity).user_id.user_id, i), entity.offset, entity.offset + entity.length, 33);
                                    } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                                        if (entity.offset + entity.length < stringBuilder.length() && stringBuilder.charAt(entity.offset + entity.length) == ' ') {
                                            entity.length++;
                                        }
                                        stringBuilder.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_messageEntityMentionName) entity).user_id, 1), entity.offset, entity.offset + entity.length, 33);
                                    } else {
                                        if (!(entity instanceof TLRPC.TL_messageEntityCode)) {
                                            if (!(entity instanceof TLRPC.TL_messageEntityPre)) {
                                                if (entity instanceof TLRPC.TL_messageEntityBold) {
                                                    TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                                                    run.flags |= 1;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(run), entity.offset, entity.offset + entity.length, stringBuilder, true);
                                                } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                                                    TextStyleSpan.TextStyleRun run2 = new TextStyleSpan.TextStyleRun();
                                                    run2.flags |= 2;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(run2), entity.offset, entity.offset + entity.length, stringBuilder, true);
                                                } else if (entity instanceof TLRPC.TL_messageEntityStrike) {
                                                    TextStyleSpan.TextStyleRun run3 = new TextStyleSpan.TextStyleRun();
                                                    run3.flags |= 8;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(run3), entity.offset, entity.offset + entity.length, stringBuilder, true);
                                                } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                                                    TextStyleSpan.TextStyleRun run4 = new TextStyleSpan.TextStyleRun();
                                                    run4.flags |= 16;
                                                    MediaDataController.addStyleToText(new TextStyleSpan(run4), entity.offset, entity.offset + entity.length, stringBuilder, true);
                                                } else if (entity instanceof TLRPC.TL_messageEntityTextUrl) {
                                                    stringBuilder.setSpan(new URLSpanReplacement(entity.url), entity.offset, entity.offset + entity.length, 33);
                                                }
                                            }
                                        }
                                        TextStyleSpan.TextStyleRun run5 = new TextStyleSpan.TextStyleRun();
                                        run5.flags |= 4;
                                        MediaDataController.addStyleToText(new TextStyleSpan(run5), entity.offset, entity.offset + entity.length, stringBuilder, true);
                                    }
                                }
                                a++;
                                i = 1;
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        }
                    }
                    setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(stringBuilder), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                } else {
                    setFieldText("");
                }
                this.messageEditText.setFilters(inputFilters);
                openKeyboard();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.messageEditText.getLayoutParams();
                layoutParams.rightMargin = AndroidUtilities.dp(4.0f);
                this.messageEditText.setLayoutParams(layoutParams);
                this.sendButton.setVisibility(8);
                this.slowModeButton.setVisibility(8);
                this.cancelBotButton.setVisibility(8);
                this.audioVideoButtonContainer.setVisibility(8);
                this.attachLayout.setVisibility(8);
                this.sendButtonContainer.setVisibility(8);
                ImageView imageView = this.scheduledButton;
                if (imageView != null) {
                    imageView.setVisibility(8);
                }
            } else {
                this.doneButtonContainer.setVisibility(8);
                this.messageEditText.setFilters(new InputFilter[0]);
                this.delegate.onMessageEditEnd(false);
                this.sendButtonContainer.setVisibility(0);
                this.cancelBotButton.setScaleX(0.1f);
                this.cancelBotButton.setScaleY(0.1f);
                this.cancelBotButton.setAlpha(0.0f);
                this.cancelBotButton.setVisibility(8);
                if (this.slowModeTimer <= 0 || isInScheduleMode()) {
                    this.sendButton.setScaleX(0.1f);
                    this.sendButton.setScaleY(0.1f);
                    this.sendButton.setAlpha(0.0f);
                    this.sendButton.setVisibility(8);
                    this.slowModeButton.setScaleX(0.1f);
                    this.slowModeButton.setScaleY(0.1f);
                    this.slowModeButton.setAlpha(0.0f);
                    this.slowModeButton.setVisibility(8);
                    this.attachLayout.setScaleX(1.0f);
                    this.attachLayout.setAlpha(1.0f);
                    this.attachLayout.setVisibility(0);
                    this.audioVideoButtonContainer.setScaleX(1.0f);
                    this.audioVideoButtonContainer.setScaleY(1.0f);
                    this.audioVideoButtonContainer.setAlpha(1.0f);
                    this.audioVideoButtonContainer.setVisibility(0);
                } else {
                    if (this.slowModeTimer == Integer.MAX_VALUE) {
                        this.sendButton.setScaleX(1.0f);
                        this.sendButton.setScaleY(1.0f);
                        this.sendButton.setAlpha(1.0f);
                        this.sendButton.setVisibility(0);
                        this.slowModeButton.setScaleX(0.1f);
                        this.slowModeButton.setScaleY(0.1f);
                        this.slowModeButton.setAlpha(0.0f);
                        this.slowModeButton.setVisibility(8);
                    } else {
                        this.sendButton.setScaleX(0.1f);
                        this.sendButton.setScaleY(0.1f);
                        this.sendButton.setAlpha(0.0f);
                        this.sendButton.setVisibility(8);
                        this.slowModeButton.setScaleX(1.0f);
                        this.slowModeButton.setScaleY(1.0f);
                        this.slowModeButton.setAlpha(1.0f);
                        this.slowModeButton.setVisibility(0);
                    }
                    this.attachLayout.setScaleX(0.01f);
                    this.attachLayout.setAlpha(0.0f);
                    this.attachLayout.setVisibility(8);
                    this.audioVideoButtonContainer.setScaleX(0.1f);
                    this.audioVideoButtonContainer.setScaleY(0.1f);
                    this.audioVideoButtonContainer.setAlpha(0.0f);
                    this.audioVideoButtonContainer.setVisibility(8);
                }
                if (this.scheduledButton.getTag() != null) {
                    this.scheduledButton.setScaleX(1.0f);
                    this.scheduledButton.setScaleY(1.0f);
                    this.scheduledButton.setAlpha(1.0f);
                    this.scheduledButton.setVisibility(0);
                }
                this.messageEditText.setText("");
                if (getVisibility() == 0) {
                    this.delegate.onAttachButtonShow();
                }
                updateFieldRight(1);
            }
            updateFieldHint();
        }
    }

    public ImageView getAttachButton() {
        return this.attachButton;
    }

    public View getSendButton() {
        return this.sendButton.getVisibility() == 0 ? this.sendButton : this.audioVideoButtonContainer;
    }

    public EmojiView getEmojiView() {
        return this.emojiView;
    }

    public void setFieldText(CharSequence text) {
        setFieldText(text, true);
    }

    public void setFieldText(CharSequence text, boolean ignoreChange) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate;
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            this.ignoreTextChange = ignoreChange;
            editTextCaption.setText(text);
            EditTextCaption editTextCaption2 = this.messageEditText;
            editTextCaption2.setSelection(editTextCaption2.getText().length());
            this.ignoreTextChange = false;
            if (ignoreChange && (chatActivityEnterViewDelegate = this.delegate) != null) {
                chatActivityEnterViewDelegate.onTextChanged(this.messageEditText.getText(), true);
            }
        }
    }

    public void setSelection(int start) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setSelection(start, editTextCaption.length());
        }
    }

    public int getCursorPosition() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        return editTextCaption.getSelectionStart();
    }

    public int getSelectionLength() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        try {
            return editTextCaption.getSelectionEnd() - this.messageEditText.getSelectionStart();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return 0;
        }
    }

    public void replaceWithText(int start, int len, CharSequence text, boolean parseEmoji) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.messageEditText.getText());
            builder.replace(start, start + len, text);
            if (parseEmoji) {
                Emoji.replaceEmoji(builder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(builder);
            this.messageEditText.setSelection(text.length() + start);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void addMentionText1(int start, int len, CharSequence text, boolean parseEmoji) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.messageEditText.getText());
            builder.replace(start, start + len, text);
            if (parseEmoji) {
                Emoji.replaceEmoji(builder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(builder);
            this.messageEditText.setSelection(text.length() + start);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void mentionAll(int start, int len, CharSequence text, boolean parseEmoji) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.messageEditText.getText());
            builder.replace(start, start + len, text);
            if (parseEmoji) {
                Emoji.replaceEmoji(builder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(builder);
            this.messageEditText.setSelection(text.length() + start);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void addMentionText(int start, int len, CharSequence text, boolean parseEmoji) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.messageEditText.getText());
            Editable editable = this.messageEditText.getText();
            if (editable != null) {
                String content = editable.toString();
                if (!content.isEmpty() && !content.endsWith(" ")) {
                    builder.append(" ");
                    start++;
                }
            }
            builder.append("@");
            builder.replace(start, start + len, text);
            if (parseEmoji) {
                Emoji.replaceEmoji(builder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(builder);
            this.messageEditText.setSelection(text.length() + start);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setFieldFocused() {
        AccessibilityManager am = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText != null && !am.isTouchExplorationEnabled()) {
            try {
                this.messageEditText.requestFocus();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void setFieldFocused(boolean focus) {
        AccessibilityManager am = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
        if (this.messageEditText != null && !am.isTouchExplorationEnabled()) {
            if (!focus) {
                EditTextCaption editTextCaption = this.messageEditText;
                if (editTextCaption != null && editTextCaption.isFocused() && !this.keyboardVisible) {
                    this.messageEditText.clearFocus();
                }
            } else if (this.searchingType == 0 && !this.messageEditText.isFocused()) {
                $$Lambda$ChatActivityEnterView$CtiWL40tjtNy43kyCuj89UTc3dM r1 = new Runnable() {
                    public final void run() {
                        ChatActivityEnterView.this.lambda$setFieldFocused$23$ChatActivityEnterView();
                    }
                };
                this.focusRunnable = r1;
                AndroidUtilities.runOnUIThread(r1, 600);
            }
        }
    }

    public /* synthetic */ void lambda$setFieldFocused$23$ChatActivityEnterView() {
        boolean allowFocus;
        EditTextCaption editTextCaption;
        this.focusRunnable = null;
        if (AndroidUtilities.isTablet()) {
            Activity activity = this.parentActivity;
            if (activity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) activity;
                if (launchActivity != null) {
                    View layout = launchActivity.getLayersActionBarLayout();
                    allowFocus = layout == null || layout.getVisibility() != 0;
                } else {
                    allowFocus = true;
                }
            } else {
                allowFocus = true;
            }
        } else {
            allowFocus = true;
        }
        if (!this.isPaused && allowFocus && (editTextCaption = this.messageEditText) != null) {
            try {
                editTextCaption.requestFocus();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public boolean hasText() {
        EditTextCaption editTextCaption = this.messageEditText;
        return editTextCaption != null && editTextCaption.length() > 0;
    }

    public EditTextCaption getEditField() {
        return this.messageEditText;
    }

    public CharSequence getFieldText() {
        if (hasText()) {
            return this.messageEditText.getText();
        }
        return null;
    }

    public void updateScheduleButton(boolean animated) {
        ImageView imageView;
        ImageView imageView2;
        boolean notifyVisible = false;
        int i = 0;
        if (((int) this.dialog_id) < 0) {
            TLRPC.Chat currentChat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            this.silent = notificationsSettings.getBoolean("silent_" + this.dialog_id, false);
            this.canWriteToChannel = ChatObject.isChannel(currentChat) && (currentChat.creator || (currentChat.admin_rights != null && currentChat.admin_rights.post_messages)) && !currentChat.megagroup;
            ImageView imageView3 = this.notifyButton;
            if (imageView3 != null) {
                notifyVisible = this.canWriteToChannel;
                imageView3.setImageResource(this.silent ? R.drawable.input_notify_off : R.drawable.input_notify_on);
            }
            LinearLayout linearLayout = this.attachLayout;
            if (linearLayout != null) {
                updateFieldRight(linearLayout.getVisibility() == 0 ? 1 : 0);
            }
        }
        boolean hasScheduled = this.delegate != null && !isInScheduleMode() && this.delegate.hasScheduledMessages();
        final boolean visible = hasScheduled && !this.scheduleButtonHidden;
        ImageView imageView4 = this.scheduledButton;
        float f = 96.0f;
        if (imageView4 != null) {
            if ((imageView4.getTag() == null || !visible) && (this.scheduledButton.getTag() != null || visible)) {
                this.scheduledButton.setTag(visible ? 1 : null);
            } else if (this.notifyButton != null) {
                if (hasScheduled || !notifyVisible || this.scheduledButton.getVisibility() == 0) {
                    i = 8;
                }
                int newVisibility = i;
                if (newVisibility != this.notifyButton.getVisibility()) {
                    this.notifyButton.setVisibility(newVisibility);
                    LinearLayout linearLayout2 = this.attachLayout;
                    if (linearLayout2 != null) {
                        ImageView imageView5 = this.botButton;
                        if ((imageView5 == null || imageView5.getVisibility() == 8) && ((imageView2 = this.notifyButton) == null || imageView2.getVisibility() == 8)) {
                            f = 48.0f;
                        }
                        linearLayout2.setPivotX((float) AndroidUtilities.dp(f));
                        return;
                    }
                    return;
                }
                return;
            } else {
                return;
            }
        }
        AnimatorSet animatorSet = this.scheduledButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.scheduledButtonAnimation = null;
        }
        float f2 = 0.1f;
        if (!animated || notifyVisible) {
            ImageView imageView6 = this.scheduledButton;
            if (imageView6 != null) {
                imageView6.setVisibility(visible ? 0 : 8);
                this.scheduledButton.setAlpha(visible ? 1.0f : 0.0f);
                this.scheduledButton.setScaleX(visible ? 1.0f : 0.1f);
                ImageView imageView7 = this.scheduledButton;
                if (visible) {
                    f2 = 1.0f;
                }
                imageView7.setScaleY(f2);
            }
            ImageView imageView8 = this.notifyButton;
            if (imageView8 != null) {
                if (!notifyVisible || this.scheduledButton.getVisibility() == 0) {
                    i = 8;
                }
                imageView8.setVisibility(i);
            }
        } else {
            if (visible) {
                this.scheduledButton.setVisibility(0);
            }
            this.scheduledButton.setPivotX((float) AndroidUtilities.dp(24.0f));
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.scheduledButtonAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[3];
            ImageView imageView9 = this.scheduledButton;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = visible ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(imageView9, property, fArr);
            ImageView imageView10 = this.scheduledButton;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = visible ? 1.0f : 0.1f;
            animatorArr[1] = ObjectAnimator.ofFloat(imageView10, property2, fArr2);
            ImageView imageView11 = this.scheduledButton;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            if (visible) {
                f2 = 1.0f;
            }
            fArr3[0] = f2;
            animatorArr[2] = ObjectAnimator.ofFloat(imageView11, property3, fArr3);
            animatorSet2.playTogether(animatorArr);
            this.scheduledButtonAnimation.setDuration(180);
            this.scheduledButtonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = ChatActivityEnterView.this.scheduledButtonAnimation = null;
                    if (!visible) {
                        ChatActivityEnterView.this.scheduledButton.setVisibility(8);
                    }
                }
            });
            this.scheduledButtonAnimation.start();
        }
        LinearLayout linearLayout3 = this.attachLayout;
        if (linearLayout3 != null) {
            ImageView imageView12 = this.botButton;
            if ((imageView12 == null || imageView12.getVisibility() == 8) && ((imageView = this.notifyButton) == null || imageView.getVisibility() == 8)) {
                f = 48.0f;
            }
            linearLayout3.setPivotX((float) AndroidUtilities.dp(f));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0085, code lost:
        r1 = r4.notifyButton;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateBotButton() {
        /*
            r4 = this;
            android.widget.ImageView r0 = r4.botButton
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r1 = r4.hasBotCommands
            r2 = 8
            if (r1 != 0) goto L_0x0014
            im.bclpbkiauv.tgnet.TLRPC$TL_replyKeyboardMarkup r1 = r4.botReplyMarkup
            if (r1 == 0) goto L_0x0010
            goto L_0x0014
        L_0x0010:
            r0.setVisibility(r2)
            goto L_0x0075
        L_0x0014:
            android.widget.ImageView r0 = r4.botButton
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x0022
            android.widget.ImageView r0 = r4.botButton
            r1 = 0
            r0.setVisibility(r1)
        L_0x0022:
            im.bclpbkiauv.tgnet.TLRPC$TL_replyKeyboardMarkup r0 = r4.botReplyMarkup
            if (r0 == 0) goto L_0x005f
            boolean r0 = r4.isPopupShowing()
            if (r0 == 0) goto L_0x0048
            int r0 = r4.currentPopupContentType
            r1 = 1
            if (r0 != r1) goto L_0x0048
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131231180(0x7f0801cc, float:1.8078434E38)
            r0.setImageResource(r1)
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131689549(0x7f0f004d, float:1.9008117E38)
            java.lang.String r3 = "AccDescrShowKeyboard"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            goto L_0x0075
        L_0x0048:
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131231173(0x7f0801c5, float:1.807842E38)
            r0.setImageResource(r1)
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131689491(0x7f0f0013, float:1.9007999E38)
            java.lang.String r3 = "AccDescrBotKeyboard"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            goto L_0x0075
        L_0x005f:
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131231172(0x7f0801c4, float:1.8078418E38)
            r0.setImageResource(r1)
            android.widget.ImageView r0 = r4.botButton
            r1 = 2131689490(0x7f0f0012, float:1.9007997E38)
            java.lang.String r3 = "AccDescrBotCommands"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
        L_0x0075:
            r0 = 2
            r4.updateFieldRight(r0)
            android.widget.LinearLayout r0 = r4.attachLayout
            android.widget.ImageView r1 = r4.botButton
            if (r1 == 0) goto L_0x0085
            int r1 = r1.getVisibility()
            if (r1 != r2) goto L_0x0090
        L_0x0085:
            android.widget.ImageView r1 = r4.notifyButton
            if (r1 == 0) goto L_0x0093
            int r1 = r1.getVisibility()
            if (r1 != r2) goto L_0x0090
            goto L_0x0093
        L_0x0090:
            r1 = 1119879168(0x42c00000, float:96.0)
            goto L_0x0095
        L_0x0093:
            r1 = 1111490560(0x42400000, float:48.0)
        L_0x0095:
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setPivotX(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ChatActivityEnterView.updateBotButton():void");
    }

    public boolean isRtlText() {
        try {
            return this.messageEditText.getLayout().getParagraphDirection(0) == -1;
        } catch (Throwable th) {
            return false;
        }
    }

    public void setBotsCount(int count, boolean hasCommands) {
        this.botCount = count;
        if (this.hasBotCommands != hasCommands) {
            this.hasBotCommands = hasCommands;
            updateBotButton();
        }
    }

    public void setButtons(MessageObject messageObject) {
        setButtons(messageObject, true);
    }

    public void setButtons(MessageObject messageObject, boolean openKeyboard) {
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 != null && messageObject2 == this.botButtonsMessageObject && messageObject2 != messageObject) {
            this.botMessageObject = messageObject;
        } else if (this.botButton != null) {
            MessageObject messageObject3 = this.botButtonsMessageObject;
            if (messageObject3 != null && messageObject3 == messageObject) {
                return;
            }
            if (this.botButtonsMessageObject != null || messageObject != null) {
                if (this.botKeyboardView == null) {
                    BotKeyboardView botKeyboardView2 = new BotKeyboardView(this.parentActivity);
                    this.botKeyboardView = botKeyboardView2;
                    botKeyboardView2.setVisibility(8);
                    this.botKeyboardView.setDelegate(new BotKeyboardView.BotKeyboardViewDelegate() {
                        public final void didPressedButton(TLRPC.KeyboardButton keyboardButton) {
                            ChatActivityEnterView.this.lambda$setButtons$24$ChatActivityEnterView(keyboardButton);
                        }
                    });
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                    sizeNotifierFrameLayout.addView(this.botKeyboardView, sizeNotifierFrameLayout.getChildCount() - 1);
                }
                this.botButtonsMessageObject = messageObject;
                this.botReplyMarkup = (messageObject == null || !(messageObject.messageOwner.reply_markup instanceof TLRPC.TL_replyKeyboardMarkup)) ? null : (TLRPC.TL_replyKeyboardMarkup) messageObject.messageOwner.reply_markup;
                this.botKeyboardView.setPanelHeight(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight);
                this.botKeyboardView.setButtons(this.botReplyMarkup);
                if (this.botReplyMarkup != null) {
                    SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
                    StringBuilder sb = new StringBuilder();
                    sb.append("hidekeyboard_");
                    sb.append(this.dialog_id);
                    boolean keyboardHidden = preferences.getInt(sb.toString(), 0) == messageObject.getId();
                    boolean showPopup = true;
                    if (this.botButtonsMessageObject != this.replyingMessageObject && this.botReplyMarkup.single_use) {
                        if (preferences.getInt("answered_" + this.dialog_id, 0) == messageObject.getId()) {
                            showPopup = false;
                        }
                    }
                    if (showPopup && !keyboardHidden && this.messageEditText.length() == 0 && !isPopupShowing()) {
                        showPopup(1, 1);
                    }
                } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                    if (openKeyboard) {
                        openKeyboardInternal();
                    } else {
                        showPopup(0, 1);
                    }
                }
                updateBotButton();
            }
        }
    }

    public /* synthetic */ void lambda$setButtons$24$ChatActivityEnterView(TLRPC.KeyboardButton button) {
        MessageObject object = this.replyingMessageObject;
        if (object == null) {
            object = ((int) this.dialog_id) < 0 ? this.botButtonsMessageObject : null;
        }
        MessageObject messageObject = this.replyingMessageObject;
        if (messageObject == null) {
            messageObject = this.botButtonsMessageObject;
        }
        didPressedBotButton(button, object, messageObject);
        if (this.replyingMessageObject != null) {
            openKeyboardInternal();
            setButtons(this.botMessageObject, false);
        } else if (this.botButtonsMessageObject.messageOwner.reply_markup.single_use) {
            openKeyboardInternal();
            SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
            edit.putInt("answered_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, 0);
        }
    }

    public void didPressedBotButton(TLRPC.KeyboardButton button, MessageObject replyMessageObject, MessageObject messageObject) {
        TLRPC.KeyboardButton keyboardButton = button;
        MessageObject messageObject2 = messageObject;
        if (keyboardButton != null && messageObject2 != null) {
            if (keyboardButton instanceof TLRPC.TL_keyboardButton) {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(keyboardButton.text, this.dialog_id, replyMessageObject, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonUrl) {
                this.parentFragment.showOpenUrlAlert(keyboardButton.url, true);
            } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonRequestPhone) {
                this.parentFragment.shareMyContact(2, messageObject2);
            } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", R.string.ShareYouLocationTitle));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", R.string.ShareYouLocationInfo));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(messageObject2, keyboardButton) {
                    private final /* synthetic */ MessageObject f$1;
                    private final /* synthetic */ TLRPC.KeyboardButton f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatActivityEnterView.this.lambda$didPressedBotButton$25$ChatActivityEnterView(this.f$1, this.f$2, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                this.parentFragment.showDialog(builder.create());
            } else if ((keyboardButton instanceof TLRPC.TL_keyboardButtonCallback) || (keyboardButton instanceof TLRPC.TL_keyboardButtonGame) || (keyboardButton instanceof TLRPC.TL_keyboardButtonBuy) || (keyboardButton instanceof TLRPC.TL_keyboardButtonUrlAuth)) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCallback(true, messageObject2, keyboardButton, this.parentFragment);
            } else if ((keyboardButton instanceof TLRPC.TL_keyboardButtonSwitchInline) && !this.parentFragment.processSwitchButton((TLRPC.TL_keyboardButtonSwitchInline) keyboardButton)) {
                if (keyboardButton.same_peer) {
                    int uid = messageObject2.messageOwner.from_id;
                    if (messageObject2.messageOwner.via_bot_id != 0) {
                        uid = messageObject2.messageOwner.via_bot_id;
                    }
                    TLRPC.User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(uid));
                    if (user != null) {
                        setFieldText("@" + user.username + " " + keyboardButton.query);
                        return;
                    }
                    return;
                }
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 1);
                DialogsActivity fragment = new DialogsActivity(args);
                fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate(messageObject2, keyboardButton) {
                    private final /* synthetic */ MessageObject f$1;
                    private final /* synthetic */ TLRPC.KeyboardButton f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                        ChatActivityEnterView.this.lambda$didPressedBotButton$26$ChatActivityEnterView(this.f$1, this.f$2, dialogsActivity, arrayList, charSequence, z);
                    }
                });
                this.parentFragment.presentFragment(fragment);
            }
        }
    }

    public /* synthetic */ void lambda$didPressedBotButton$25$ChatActivityEnterView(MessageObject messageObject, TLRPC.KeyboardButton button, DialogInterface dialogInterface, int i) {
        if (Build.VERSION.SDK_INT < 23 || this.parentActivity.checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) == 0) {
            SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(messageObject, button);
            return;
        }
        this.parentActivity.requestPermissions(new String[]{PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, "android.permission.ACCESS_FINE_LOCATION"}, 2);
        this.pendingMessageObject = messageObject;
        this.pendingLocationButton = button;
    }

    public /* synthetic */ void lambda$didPressedBotButton$26$ChatActivityEnterView(MessageObject messageObject, TLRPC.KeyboardButton button, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        MessageObject messageObject2 = messageObject;
        int uid = messageObject2.messageOwner.from_id;
        if (messageObject2.messageOwner.via_bot_id != 0) {
            uid = messageObject2.messageOwner.via_bot_id;
        }
        TLRPC.User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(uid));
        if (user == null) {
            fragment1.finishFragment();
            return;
        }
        long did = ((Long) dids.get(0)).longValue();
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        instance.saveDraft(did, "@" + user.username + " " + button.query, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.Message) null, true);
        if (did != this.dialog_id) {
            int lower_part = (int) did;
            if (lower_part != 0) {
                Bundle args1 = new Bundle();
                if (lower_part > 0) {
                    args1.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    args1.putInt("chat_id", -lower_part);
                }
                if (this.accountInstance.getMessagesController().checkCanOpenChat(args1, fragment1)) {
                    if (!this.parentFragment.presentFragment(new ChatActivity(args1), true)) {
                        fragment1.finishFragment();
                    } else if (!AndroidUtilities.isTablet()) {
                        this.parentFragment.removeSelfFromStack();
                    }
                }
            } else {
                DialogsActivity dialogsActivity = fragment1;
                fragment1.finishFragment();
            }
        } else {
            DialogsActivity dialogsActivity2 = fragment1;
            fragment1.finishFragment();
        }
    }

    public boolean isPopupView(View view) {
        return view == this.botKeyboardView || view == this.emojiView || view == this.menuView;
    }

    public boolean isRecordCircle(View view) {
        return view == this.recordCircle;
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            EmojiView emojiView2 = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity, true, this.info);
            this.emojiView = emojiView2;
            emojiView2.setVisibility(8);
            this.emojiView.setDelegate(new EmojiView.EmojiViewDelegate() {
                public boolean onBackspace() {
                    if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                        return false;
                    }
                    ChatActivityEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
                    return true;
                }

                public void onEmojiSelected(String symbol) {
                    int i = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
                    if (i < 0) {
                        i = 0;
                    }
                    try {
                        int unused = ChatActivityEnterView.this.innerTextChange = 2;
                        CharSequence localCharSequence = Emoji.replaceEmoji(symbol, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(i, localCharSequence));
                        int j = localCharSequence.length() + i;
                        ChatActivityEnterView.this.messageEditText.setSelection(j, j);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th) {
                        int unused2 = ChatActivityEnterView.this.innerTextChange = 0;
                        throw th;
                    }
                    int unused3 = ChatActivityEnterView.this.innerTextChange = 0;
                }

                public void onStickerSelected(View view, TLRPC.Document sticker, Object parent, boolean notify, int scheduleDate) {
                    if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                int unused = ChatActivityEnterView.this.searchingType = 0;
                                ChatActivityEnterView.this.emojiView.closeSearch(true, MessageObject.getStickerSetId(sticker));
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        ChatActivityEnterView.this.lambda$onStickerSelected$28$ChatActivityEnterView(sticker, parent, false, notify, scheduleDate);
                        if (((int) ChatActivityEnterView.this.dialog_id) == 0 && MessageObject.isGifDocument(sticker)) {
                            ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(parent, sticker);
                        }
                    } else if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onUpdateSlowModeButton(view != null ? view : ChatActivityEnterView.this.slowModeButton, true, ChatActivityEnterView.this.slowModeButton.getText());
                    }
                }

                public void onStickersSettingsClick() {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
                    }
                }

                /* renamed from: onGifSelected */
                public void lambda$onGifSelected$0$ChatActivityEnterView$35(View view, Object gif, Object parent, boolean notify, int scheduleDate) {
                    View view2 = view;
                    Object obj = gif;
                    Object obj2 = parent;
                    int i = scheduleDate;
                    if (isInScheduleMode() && i == 0) {
                        AlertsCreator.createScheduleDatePickerDialog(ChatActivityEnterView.this.parentActivity, UserObject.isUserSelf(ChatActivityEnterView.this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(view2, obj, obj2) {
                            private final /* synthetic */ View f$1;
                            private final /* synthetic */ Object f$2;
                            private final /* synthetic */ Object f$3;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void didSelectDate(boolean z, int i) {
                                ChatActivityEnterView.AnonymousClass35.this.lambda$onGifSelected$0$ChatActivityEnterView$35(this.f$1, this.f$2, this.f$3, z, i);
                            }
                        });
                        boolean z = notify;
                    } else if (ChatActivityEnterView.this.slowModeTimer <= 0 || isInScheduleMode()) {
                        if (ChatActivityEnterView.this.stickersExpanded) {
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                            ChatActivityEnterView.this.setStickersExpanded(false, true, false);
                        }
                        if (obj instanceof TLRPC.Document) {
                            TLRPC.Document document = (TLRPC.Document) obj;
                            SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendSticker(document, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, parent, notify, scheduleDate);
                            MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
                            if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                                ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj2, document);
                            }
                        } else if (obj instanceof TLRPC.BotInlineResult) {
                            TLRPC.BotInlineResult result = (TLRPC.BotInlineResult) obj;
                            if (result.document != null) {
                                MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).addRecentGif(result.document, (int) (System.currentTimeMillis() / 1000));
                                if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                                    ChatActivityEnterView.this.accountInstance.getMessagesController().saveGif(obj2, result.document);
                                }
                            }
                            TLRPC.User user = (TLRPC.User) obj2;
                            HashMap<String, String> params = new HashMap<>();
                            params.put(TtmlNode.ATTR_ID, result.id);
                            params.put("query_id", "" + result.query_id);
                            HashMap<String, String> hashMap = params;
                            TLRPC.BotInlineResult botInlineResult = result;
                            SendMessagesHelper.prepareSendingBotContextResult(ChatActivityEnterView.this.accountInstance, result, params, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, notify, scheduleDate);
                            if (ChatActivityEnterView.this.searchingType != 0) {
                                int unused = ChatActivityEnterView.this.searchingType = 0;
                                ChatActivityEnterView.this.emojiView.closeSearch(true);
                                ChatActivityEnterView.this.emojiView.hideSearchKeyboard();
                            }
                        }
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onMessageSend((CharSequence) null, notify, i);
                        } else {
                            boolean z2 = notify;
                        }
                    } else if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onUpdateSlowModeButton(view2 != null ? view2 : ChatActivityEnterView.this.slowModeButton, true, ChatActivityEnterView.this.slowModeButton.getText());
                    }
                }

                public void onTabOpened(int type) {
                    ChatActivityEnterView.this.delegate.onStickersTab(type == 3);
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.post(chatActivityEnterView.updateExpandabilityRunnable);
                }

                public void onClearEmojiRecent() {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) ChatActivityEnterView.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("ClearRecentEmoji", R.string.ClearRecentEmoji));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ChatActivityEnterView.AnonymousClass35.this.lambda$onClearEmojiRecent$1$ChatActivityEnterView$35(dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                        ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                    }
                }

                public /* synthetic */ void lambda$onClearEmojiRecent$1$ChatActivityEnterView$35(DialogInterface dialogInterface, int i) {
                    ChatActivityEnterView.this.emojiView.clearRecentEmoji();
                }

                public void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        if (stickerSet != null) {
                            inputStickerSet = new TLRPC.TL_inputStickerSetID();
                            inputStickerSet.access_hash = stickerSet.access_hash;
                            inputStickerSet.id = stickerSet.id;
                        }
                        ChatActivityEnterView.this.parentFragment.showDialog(new StickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, inputStickerSet, (TLRPC.TL_messages_stickerSet) null, ChatActivityEnterView.this));
                    }
                }

                public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).installStickerSet(ChatActivityEnterView.this.parentActivity, 0, stickerSet);
                }

                public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
                    MediaDataController.getInstance(ChatActivityEnterView.this.currentAccount).removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSet.set, 1, ChatActivityEnterView.this.parentFragment, false);
                }

                public void onStickersGroupClick(int chatId) {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        if (AndroidUtilities.isTablet()) {
                            ChatActivityEnterView.this.hidePopup(false);
                        }
                        GroupStickersActivity fragment = new GroupStickersActivity(chatId);
                        fragment.setInfo(ChatActivityEnterView.this.info);
                        ChatActivityEnterView.this.parentFragment.presentFragment(fragment);
                    }
                }

                public void onSearchOpenClose(int type) {
                    int unused = ChatActivityEnterView.this.searchingType = type;
                    ChatActivityEnterView.this.setStickersExpanded(type != 0, false, false);
                    if (ChatActivityEnterView.this.emojiTabOpen && ChatActivityEnterView.this.searchingType == 2) {
                        ChatActivityEnterView.this.checkStickresExpandHeight();
                    }
                }

                public boolean isSearchOpened() {
                    return ChatActivityEnterView.this.searchingType != 0;
                }

                public boolean isExpanded() {
                    return ChatActivityEnterView.this.stickersExpanded;
                }

                public boolean canSchedule() {
                    return ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.canScheduleMessage();
                }

                public boolean isInScheduleMode() {
                    return ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.isInScheduleMode();
                }
            });
            this.emojiView.setDragListener(new EmojiView.DragListener() {
                int initialOffset;
                boolean wasExpanded;

                public void onDragStart() {
                    if (allowDragging()) {
                        if (ChatActivityEnterView.this.stickersExpansionAnim != null) {
                            ChatActivityEnterView.this.stickersExpansionAnim.cancel();
                        }
                        boolean unused = ChatActivityEnterView.this.stickersDragging = true;
                        this.wasExpanded = ChatActivityEnterView.this.stickersExpanded;
                        boolean unused2 = ChatActivityEnterView.this.stickersExpanded = true;
                        int i = 0;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 1);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        int height = chatActivityEnterView.sizeNotifierLayout.getHeight();
                        if (Build.VERSION.SDK_INT >= 21) {
                            i = AndroidUtilities.statusBarHeight;
                        }
                        int unused3 = chatActivityEnterView.stickersExpandedHeight = (((height - i) - ActionBar.getCurrentActionBarHeight()) - ChatActivityEnterView.this.getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        if (ChatActivityEnterView.this.searchingType == 2) {
                            ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                            int unused4 = chatActivityEnterView2.stickersExpandedHeight = Math.min(chatActivityEnterView2.stickersExpandedHeight, AndroidUtilities.dp(120.0f) + (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight));
                        }
                        ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                        ChatActivityEnterView.this.emojiView.setLayerType(2, (Paint) null);
                        ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                        ChatActivityEnterView.this.sizeNotifierLayout.setForeground(new ScrimDrawable());
                        this.initialOffset = (int) ChatActivityEnterView.this.getTranslationY();
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onStickersExpandedChange();
                        }
                    }
                }

                public void onDragEnd(float velocity) {
                    if (allowDragging()) {
                        boolean unused = ChatActivityEnterView.this.stickersDragging = false;
                        if ((!this.wasExpanded || velocity < ((float) AndroidUtilities.dp(200.0f))) && ((this.wasExpanded || velocity > ((float) AndroidUtilities.dp(-200.0f))) && ((!this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress > 0.6f) && (this.wasExpanded || ChatActivityEnterView.this.stickersExpansionProgress < 0.4f)))) {
                            ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, true);
                        } else {
                            ChatActivityEnterView.this.setStickersExpanded(!this.wasExpanded, true, true);
                        }
                    }
                }

                public void onDragCancel() {
                    if (ChatActivityEnterView.this.stickersTabOpen) {
                        boolean unused = ChatActivityEnterView.this.stickersDragging = false;
                        ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, false);
                    }
                }

                public void onDrag(int offset) {
                    if (allowDragging()) {
                        int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? ChatActivityEnterView.this.keyboardHeightLand : ChatActivityEnterView.this.keyboardHeight;
                        int offset2 = Math.max(Math.min(offset + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - origHeight));
                        ChatActivityEnterView.this.emojiView.setTranslationY((float) offset2);
                        ChatActivityEnterView.this.setTranslationY((float) offset2);
                        ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                        float unused = chatActivityEnterView.stickersExpansionProgress = ((float) offset2) / ((float) (-(chatActivityEnterView.stickersExpandedHeight - origHeight)));
                        ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                    }
                }

                private boolean allowDragging() {
                    return ChatActivityEnterView.this.stickersTabOpen && (ChatActivityEnterView.this.stickersExpanded || ChatActivityEnterView.this.messageEditText.length() <= 0) && ChatActivityEnterView.this.emojiView.areThereAnyStickers();
                }
            });
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
            sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 1);
            checkChannelRights();
        }
    }

    private void createMenuView() {
        String str;
        EnterMenuView enterMenuView = this.menuView;
        if (enterMenuView != null) {
            ChatActivity chatActivity = this.parentFragment;
            enterMenuView.setCurrentChat(chatActivity != null ? chatActivity.getCurrentChat() : null);
            return;
        }
        long j = this.dialog_id;
        int lower_id = (int) j;
        int high_id = (int) (j >> 32);
        ArrayList<ChatEnterMenuType> chatEnterMenuTypes = new ArrayList<>();
        ArrayList<Integer> chatEnterMenuIcons = new ArrayList<>();
        ArrayList<String> chatEnterMenuTexts = new ArrayList<>();
        ChatActivity chatActivity2 = this.parentFragment;
        TLRPC.EncryptedChat encryptedChat = chatActivity2 != null ? chatActivity2.getCurrentEncryptedChat() : null;
        if ((lower_id != 0 || high_id == 0) && lower_id <= 0) {
            boolean isChannel = false;
            if (((int) this.dialog_id) < 0) {
                str = "RedPacket";
                TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-((int) this.dialog_id)));
                isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
                boolean isAdmin = ChatObject.hasAdminRights(chat);
            } else {
                str = "RedPacket";
            }
            boolean showRedpacket = BuildVars.WALLET_ENABLE && !isChannel;
            chatEnterMenuTexts.add(LocaleController.getString("chat_choose_photos", R.string.chat_choose_photos));
            chatEnterMenuTexts.add(LocaleController.getString("chat_take_photo", R.string.chat_take_photo));
            if (showRedpacket) {
                chatEnterMenuTexts.add(LocaleController.getString(str, R.string.RedPacket));
            }
            if (0 != 0) {
                chatEnterMenuTexts.add(LocaleController.getString(R.string.live_group_title));
            }
            chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_album));
            chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_camera));
            if (showRedpacket) {
                chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_hongbao));
            }
            if (0 != 0) {
                chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_live));
            }
            chatEnterMenuTypes.add(ChatEnterMenuType.ALBUM);
            chatEnterMenuTypes.add(ChatEnterMenuType.CAMERA);
            if (showRedpacket) {
                chatEnterMenuTypes.add(ChatEnterMenuType.REDPACKET);
            }
            if (0 != 0) {
                chatEnterMenuTypes.add(ChatEnterMenuType.GROUP_LIVE);
            }
        } else if (lower_id == 333000 || lower_id == 777000 || lower_id == 42777 || lower_id == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
            chatEnterMenuTexts.add(LocaleController.getString("chat_choose_photos", R.string.chat_choose_photos));
            chatEnterMenuTexts.add(LocaleController.getString("chat_take_photo", R.string.chat_take_photo));
            chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_album));
            chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_camera));
            chatEnterMenuTypes.add(ChatEnterMenuType.ALBUM);
            chatEnterMenuTypes.add(ChatEnterMenuType.CAMERA);
        } else {
            boolean showVJ = encryptedChat == null;
            boolean showRdp = BuildVars.WALLET_RED_PACKET_ENABLE;
            chatEnterMenuTexts.add(LocaleController.getString("chat_choose_photos", R.string.chat_choose_photos));
            chatEnterMenuTexts.add(LocaleController.getString("chat_take_photo", R.string.chat_take_photo));
            if (showVJ) {
                chatEnterMenuTexts.add(LocaleController.getString("visual_call_voice", R.string.visual_call_voice));
            }
            if (0 != 0) {
                chatEnterMenuTexts.add(LocaleController.getString("ChatVideo", R.string.ChatVideo));
            }
            if (showRdp) {
                chatEnterMenuTexts.add(LocaleController.getString("Transfer", R.string.Transfer));
                chatEnterMenuTexts.add(LocaleController.getString("RedPacket", R.string.RedPacket));
            }
            chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_album));
            chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_camera));
            if (showVJ) {
                chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_voice));
            }
            if (0 != 0) {
                chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_video));
            }
            if (showRdp) {
                chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_transfer));
                chatEnterMenuIcons.add(Integer.valueOf(R.drawable.selector_chat_attach_menu_hongbao));
            }
            chatEnterMenuTypes.add(ChatEnterMenuType.ALBUM);
            chatEnterMenuTypes.add(ChatEnterMenuType.CAMERA);
            if (showVJ) {
                chatEnterMenuTypes.add(ChatEnterMenuType.VOICECALL);
            }
            if (0 != 0) {
                chatEnterMenuTypes.add(ChatEnterMenuType.VIDEOCALL);
            }
            if (showRdp) {
                chatEnterMenuTypes.add(ChatEnterMenuType.TRANSFER);
                chatEnterMenuTypes.add(ChatEnterMenuType.REDPACKET);
            }
        }
        this.attachTexts.addAll(chatEnterMenuTexts);
        this.attachIcons.addAll(chatEnterMenuIcons);
        this.attachTypes.addAll(chatEnterMenuTypes);
        EnterMenuView enterMenuView2 = new EnterMenuView(this.parentActivity);
        this.menuView = enterMenuView2;
        enterMenuView2.setVisibility(8);
        this.menuView.setDelegate(new EnterMenuView.EnterMenuViewDelegate() {
            public final void onItemClie(int i, ChatEnterMenuType chatEnterMenuType) {
                ChatActivityEnterView.this.lambda$createMenuView$27$ChatActivityEnterView(i, chatEnterMenuType);
            }
        });
        this.menuView.setDataAndNotify(this.attachTexts, this.attachIcons, this.attachTypes);
        EnterMenuView enterMenuView3 = this.menuView;
        ChatActivity chatActivity3 = this.parentFragment;
        enterMenuView3.setCurrentChat(chatActivity3 != null ? chatActivity3.getCurrentChat() : null);
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        sizeNotifierFrameLayout.addView(this.menuView, sizeNotifierFrameLayout.getChildCount() - 1, LayoutHelper.createFrame(-1, -1, 17));
    }

    public /* synthetic */ void lambda$createMenuView$27$ChatActivityEnterView(int position, ChatEnterMenuType menuType) {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.didPressedAttachButton(position, menuType);
        }
    }

    /* renamed from: onStickerSelected */
    public void lambda$onStickerSelected$28$ChatActivityEnterView(TLRPC.Document sticker, Object parent, boolean clearsInputField, boolean notify, int scheduleDate) {
        if (isInScheduleMode() && scheduleDate == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(sticker, parent, clearsInputField) {
                private final /* synthetic */ TLRPC.Document f$1;
                private final /* synthetic */ Object f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void didSelectDate(boolean z, int i) {
                    ChatActivityEnterView.this.lambda$onStickerSelected$28$ChatActivityEnterView(this.f$1, this.f$2, this.f$3, z, i);
                }
            });
        } else if (this.slowModeTimer <= 0 || isInScheduleMode()) {
            if (this.searchingType != 0) {
                this.searchingType = 0;
                this.emojiView.closeSearch(true);
                this.emojiView.hideSearchKeyboard();
            }
            setStickersExpanded(false, true, false);
            SendMessagesHelper.getInstance(this.currentAccount).sendSticker(sticker, this.dialog_id, this.replyingMessageObject, parent, notify, scheduleDate);
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, scheduleDate);
            }
            if (clearsInputField) {
                setFieldText("");
            }
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, parent, sticker, (int) (System.currentTimeMillis() / 1000), false);
        } else {
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate2 = this.delegate;
            if (chatActivityEnterViewDelegate2 != null) {
                SimpleTextView simpleTextView = this.slowModeButton;
                chatActivityEnterViewDelegate2.onUpdateSlowModeButton(simpleTextView, true, simpleTextView.getText());
            }
        }
    }

    public boolean canSchedule() {
        ChatActivity chatActivity = this.parentFragment;
        return chatActivity != null && chatActivity.canScheduleMessage();
    }

    public boolean isInScheduleMode() {
        ChatActivity chatActivity = this.parentFragment;
        return chatActivity != null && chatActivity.isInScheduleMode();
    }

    public void addStickerToRecent(TLRPC.Document sticker) {
        createEmojiView();
        this.emojiView.addRecentSticker(sticker);
    }

    public void hideEmojiView() {
        EmojiView emojiView2;
        if (!this.emojiViewVisible && (emojiView2 = this.emojiView) != null && emojiView2.getVisibility() != 8) {
            this.sizeNotifierLayout.removeView(this.emojiView);
            this.emojiView.setVisibility(8);
        }
    }

    private void showAttachMenu() {
    }

    public void showEmojiView() {
        showPopup(1, 0);
    }

    /* access modifiers changed from: private */
    public void showPopup(int show, int contentType) {
        if (show == 1) {
            if (contentType == 0 && this.emojiView == null) {
                if (this.parentActivity != null) {
                    createEmojiView();
                } else {
                    return;
                }
            }
            View currentView = null;
            if (contentType == 0) {
                if (this.emojiView.getParent() == null) {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                    sizeNotifierFrameLayout.addView(this.emojiView, sizeNotifierFrameLayout.getChildCount() - 1);
                }
                this.emojiView.setVisibility(0);
                this.emojiViewVisible = true;
                BotKeyboardView botKeyboardView2 = this.botKeyboardView;
                if (!(botKeyboardView2 == null || botKeyboardView2.getVisibility() == 8)) {
                    this.botKeyboardView.setVisibility(8);
                }
                EnterMenuView enterMenuView = this.menuView;
                if (!(enterMenuView == null || enterMenuView.getVisibility() == 8)) {
                    this.sizeNotifierLayout.removeView(this.menuView);
                    this.menuView.setVisibility(8);
                    this.menuViewVisible = false;
                }
                currentView = this.emojiView;
            } else if (contentType == 1) {
                EmojiView emojiView2 = this.emojiView;
                if (!(emojiView2 == null || emojiView2.getVisibility() == 8)) {
                    this.sizeNotifierLayout.removeView(this.emojiView);
                    this.emojiView.setVisibility(8);
                    this.emojiViewVisible = false;
                }
                EnterMenuView enterMenuView2 = this.menuView;
                if (!(enterMenuView2 == null || enterMenuView2.getVisibility() == 8)) {
                    this.sizeNotifierLayout.removeView(this.menuView);
                    this.menuView.setVisibility(8);
                    this.menuViewVisible = false;
                }
                this.botKeyboardView.setVisibility(0);
                currentView = this.botKeyboardView;
            }
            this.currentPopupContentType = contentType;
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(236.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(236.0f));
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            if (contentType == 1) {
                currentHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), currentHeight);
            }
            BotKeyboardView botKeyboardView3 = this.botKeyboardView;
            if (botKeyboardView3 != null) {
                botKeyboardView3.setPanelHeight(currentHeight);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayout2 != null) {
                this.emojiPadding = currentHeight;
                sizeNotifierFrameLayout2.requestLayout();
                setEmojiButtonImage(true, true);
                updateBotButton();
                onWindowSizeChanged();
            }
        } else if (show == 3) {
            if (contentType == 3 && this.menuView == null) {
                if (this.parentActivity != null) {
                    createMenuView();
                } else {
                    return;
                }
            }
            EnterMenuView enterMenuView3 = this.menuView;
            if (enterMenuView3 != null) {
                ChatActivity chatActivity = this.parentFragment;
                enterMenuView3.setCurrentChat(chatActivity != null ? chatActivity.getCurrentChat() : null);
            }
            View currentView2 = null;
            if (contentType == 3) {
                if (this.menuView.getParent() == null) {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout3 = this.sizeNotifierLayout;
                    sizeNotifierFrameLayout3.addView(this.menuView, sizeNotifierFrameLayout3.getChildCount() - 1, LayoutHelper.createFrame(-2, -2, 1));
                }
                this.menuView.setVisibility(0);
                this.menuViewVisible = true;
                BotKeyboardView botKeyboardView4 = this.botKeyboardView;
                if (!(botKeyboardView4 == null || botKeyboardView4.getVisibility() == 8)) {
                    this.botKeyboardView.setVisibility(8);
                }
                EmojiView emojiView3 = this.emojiView;
                if (!(emojiView3 == null || emojiView3.getVisibility() == 8)) {
                    this.sizeNotifierLayout.removeView(this.emojiView);
                    this.emojiView.setVisibility(8);
                    this.emojiViewVisible = false;
                }
                currentView2 = this.menuView;
            }
            this.currentPopupContentType = contentType;
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(236.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(236.0f));
            }
            int currentHeight2 = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            if (contentType == 1) {
                currentHeight2 = Math.min(this.botKeyboardView.getKeyboardHeight(), currentHeight2);
            }
            BotKeyboardView botKeyboardView5 = this.botKeyboardView;
            if (botKeyboardView5 != null) {
                botKeyboardView5.setPanelHeight(currentHeight2);
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) currentView2.getLayoutParams();
            layoutParams2.height = currentHeight2;
            currentView2.setLayoutParams(layoutParams2);
            if (!AndroidUtilities.isInMultiwindow) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout4 = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayout4 != null) {
                this.emojiPadding = currentHeight2;
                sizeNotifierFrameLayout4.requestLayout();
                updateBotButton();
                onWindowSizeChanged();
            }
        } else {
            if (this.emojiButton != null) {
                setEmojiButtonImage(false, true);
            }
            this.currentPopupContentType = -1;
            if (this.emojiView != null) {
                this.emojiViewVisible = false;
                if (show != 2 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    this.sizeNotifierLayout.removeView(this.emojiView);
                    this.emojiView.setVisibility(8);
                }
            }
            if (this.menuView != null) {
                this.menuViewVisible = false;
                if (show != 3 || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    this.sizeNotifierLayout.removeView(this.menuView);
                    this.menuView.setVisibility(8);
                }
            }
            BotKeyboardView botKeyboardView6 = this.botKeyboardView;
            if (botKeyboardView6 != null) {
                botKeyboardView6.setVisibility(8);
            }
            if (this.sizeNotifierLayout != null) {
                if (show == 0) {
                    this.emojiPadding = 0;
                }
                this.sizeNotifierLayout.requestLayout();
                onWindowSizeChanged();
            }
            updateBotButton();
        }
        if (this.stickersTabOpen || this.emojiTabOpen) {
            checkSendButton(true);
        }
        if (this.stickersExpanded && show != 1) {
            setStickersExpanded(false, false, false);
        }
    }

    /* JADX WARNING: type inference failed for: r13v1 */
    /* JADX WARNING: type inference failed for: r13v2 */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setEmojiButtonImage(boolean r12, boolean r13) {
        /*
            r11 = this;
            if (r13 == 0) goto L_0x0008
            int r0 = r11.currentEmojiIcon
            r1 = -1
            if (r0 != r1) goto L_0x0008
            r13 = 0
        L_0x0008:
            r0 = 0
            r1 = 1
            if (r12 == 0) goto L_0x0012
            int r2 = r11.currentPopupContentType
            if (r2 != 0) goto L_0x0012
            r2 = 0
            goto L_0x003a
        L_0x0012:
            im.bclpbkiauv.ui.components.EmojiView r2 = r11.emojiView
            if (r2 != 0) goto L_0x0021
            android.content.SharedPreferences r2 = im.bclpbkiauv.messenger.MessagesController.getGlobalEmojiSettings()
            java.lang.String r3 = "selected_page"
            int r2 = r2.getInt(r3, r0)
            goto L_0x0025
        L_0x0021:
            int r2 = r2.getCurrentPage()
        L_0x0025:
            if (r2 == 0) goto L_0x0038
            boolean r3 = r11.allowStickers
            if (r3 != 0) goto L_0x0030
            boolean r3 = r11.allowGifs
            if (r3 != 0) goto L_0x0030
            goto L_0x0038
        L_0x0030:
            if (r2 != r1) goto L_0x0035
            r3 = 2
            r2 = r3
            goto L_0x003a
        L_0x0035:
            r3 = 3
            r2 = r3
            goto L_0x003a
        L_0x0038:
            r3 = 1
            r2 = r3
        L_0x003a:
            int r3 = r11.currentEmojiIcon
            if (r3 != r2) goto L_0x003f
            return
        L_0x003f:
            android.animation.AnimatorSet r3 = r11.emojiButtonAnimation
            r4 = 0
            if (r3 == 0) goto L_0x0049
            r3.cancel()
            r11.emojiButtonAnimation = r4
        L_0x0049:
            r3 = 3
            r5 = 2
            if (r2 != 0) goto L_0x0058
            android.widget.ImageView[] r6 = r11.emojiButton
            r6 = r6[r13]
            r7 = 2131231180(0x7f0801cc, float:1.8078434E38)
            r6.setImageResource(r7)
            goto L_0x007e
        L_0x0058:
            if (r2 != r1) goto L_0x0065
            android.widget.ImageView[] r6 = r11.emojiButton
            r6 = r6[r13]
            r7 = 2131231187(0x7f0801d3, float:1.8078448E38)
            r6.setImageResource(r7)
            goto L_0x007e
        L_0x0065:
            if (r2 != r5) goto L_0x0072
            android.widget.ImageView[] r6 = r11.emojiButton
            r6 = r6[r13]
            r7 = 2131231188(0x7f0801d4, float:1.807845E38)
            r6.setImageResource(r7)
            goto L_0x007e
        L_0x0072:
            if (r2 != r3) goto L_0x007e
            android.widget.ImageView[] r6 = r11.emojiButton
            r6 = r6[r13]
            r7 = 2131231179(0x7f0801cb, float:1.8078432E38)
            r6.setImageResource(r7)
        L_0x007e:
            android.widget.ImageView[] r6 = r11.emojiButton
            r6 = r6[r13]
            if (r2 != r5) goto L_0x0088
            java.lang.Integer r4 = java.lang.Integer.valueOf(r1)
        L_0x0088:
            r6.setTag(r4)
            r11.currentEmojiIcon = r2
            if (r13 == 0) goto L_0x0121
            android.widget.ImageView[] r4 = r11.emojiButton
            r4 = r4[r1]
            r4.setVisibility(r0)
            android.animation.AnimatorSet r4 = new android.animation.AnimatorSet
            r4.<init>()
            r11.emojiButtonAnimation = r4
            r6 = 6
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            android.widget.ImageView[] r7 = r11.emojiButton
            r7 = r7[r0]
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r9 = new float[r1]
            r10 = 1036831949(0x3dcccccd, float:0.1)
            r9[r0] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6[r0] = r7
            android.widget.ImageView[] r7 = r11.emojiButton
            r7 = r7[r0]
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r9 = new float[r1]
            r9[r0] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6[r1] = r7
            android.widget.ImageView[] r7 = r11.emojiButton
            r7 = r7[r0]
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r1]
            r10 = 0
            r9[r0] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6[r5] = r7
            android.widget.ImageView[] r5 = r11.emojiButton
            r5 = r5[r1]
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r8 = new float[r1]
            r9 = 1065353216(0x3f800000, float:1.0)
            r8[r0] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r8)
            r6[r3] = r5
            r3 = 4
            android.widget.ImageView[] r5 = r11.emojiButton
            r5 = r5[r1]
            android.util.Property r7 = android.view.View.SCALE_Y
            float[] r8 = new float[r1]
            r8[r0] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r8)
            r6[r3] = r5
            r3 = 5
            android.widget.ImageView[] r5 = r11.emojiButton
            r5 = r5[r1]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r1 = new float[r1]
            r1[r0] = r9
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r5, r7, r1)
            r6[r3] = r0
            r4.playTogether(r6)
            android.animation.AnimatorSet r0 = r11.emojiButtonAnimation
            im.bclpbkiauv.ui.components.ChatActivityEnterView$37 r1 = new im.bclpbkiauv.ui.components.ChatActivityEnterView$37
            r1.<init>()
            r0.addListener(r1)
            android.animation.AnimatorSet r0 = r11.emojiButtonAnimation
            r3 = 150(0x96, double:7.4E-322)
            r0.setDuration(r3)
            android.animation.AnimatorSet r0 = r11.emojiButtonAnimation
            r0.start()
        L_0x0121:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ChatActivityEnterView.setEmojiButtonImage(boolean, boolean):void");
    }

    public void hidePopup(boolean byBackButton) {
        if (isPopupShowing()) {
            if (this.currentPopupContentType == 1 && byBackButton && this.botButtonsMessageObject != null) {
                SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
                edit.putInt("hidekeyboard_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
            }
            if (!byBackButton || this.searchingType == 0) {
                if (this.searchingType != 0) {
                    this.searchingType = 0;
                    this.emojiView.closeSearch(false);
                    this.messageEditText.requestFocus();
                }
                showPopup(0, 0);
                return;
            }
            this.searchingType = 0;
            this.emojiView.closeSearch(true);
            this.messageEditText.requestFocus();
            setStickersExpanded(false, true, false);
            if (this.emojiTabOpen) {
                checkSendButton(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void openKeyboardInternal() {
        showPopup((AndroidUtilities.usingHardwareInput || this.isPaused) ? 0 : 2, 0);
        this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.messageEditText);
        if (this.isPaused) {
            this.showKeyboardOnResume = true;
        } else if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
            this.waitingForKeyboardOpen = true;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
        }
    }

    public boolean isEditingMessage() {
        return this.editingMessageObject != null;
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    public boolean isEditingCaption() {
        return this.editingCaption;
    }

    public boolean hasAudioToSend() {
        return (this.audioToSendMessageObject == null && this.videoToSendMessageObject == null) ? false : true;
    }

    public void openKeyboard() {
        AndroidUtilities.showKeyboard(this.messageEditText);
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
        r0 = r1.botKeyboardView;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isPopupShowing() {
        /*
            r1 = this;
            boolean r0 = r1.emojiViewVisible
            if (r0 != 0) goto L_0x0015
            boolean r0 = r1.menuViewVisible
            if (r0 != 0) goto L_0x0015
            im.bclpbkiauv.ui.components.BotKeyboardView r0 = r1.botKeyboardView
            if (r0 == 0) goto L_0x0013
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0013
            goto L_0x0015
        L_0x0013:
            r0 = 0
            goto L_0x0016
        L_0x0015:
            r0 = 1
        L_0x0016:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ChatActivityEnterView.isPopupShowing():boolean");
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public void addRecentGif(TLRPC.Document searchImage) {
        MediaDataController.getInstance(this.currentAccount).addRecentGif(searchImage, (int) (System.currentTimeMillis() / 1000));
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.addRecentGif(searchImage);
        }
    }

    public void removeRecentGif(TLRPC.Document searchImage) {
        MediaDataController.getInstance(this.currentAccount).removeRecentGifById(searchImage);
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.removeRecentGif(searchImage);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw && this.stickersExpanded) {
            this.searchingType = 0;
            this.emojiView.closeSearch(false);
            setStickersExpanded(false, false, false);
        }
        this.videoTimelineView.clearFrames();
    }

    public boolean isStickersExpanded() {
        return this.stickersExpanded;
    }

    public void onSizeChanged(int height, boolean isWidthGreater) {
        boolean z;
        boolean z2 = true;
        if (this.searchingType != 0) {
            this.lastSizeChangeValue1 = height;
            this.lastSizeChangeValue2 = isWidthGreater;
            if (height <= 0) {
                z2 = false;
            }
            this.keyboardVisible = z2;
            return;
        }
        if (height > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
            if (isWidthGreater) {
                this.keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            int newHeight = isWidthGreater ? this.keyboardHeightLand : this.keyboardHeight;
            if (this.currentPopupContentType == 1 && !this.botKeyboardView.isFullSize()) {
                newHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), newHeight);
            }
            View currentView = null;
            int i = this.currentPopupContentType;
            if (i == 0) {
                currentView = this.emojiView;
            } else if (i == 1) {
                currentView = this.botKeyboardView;
            } else if (i == 3) {
                currentView = this.menuView;
            }
            BotKeyboardView botKeyboardView2 = this.botKeyboardView;
            if (botKeyboardView2 != null) {
                botKeyboardView2.setPanelHeight(newHeight);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
            if (!this.closeAnimationInProgress && (!(layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == newHeight) && !this.stickersExpanded)) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = newHeight;
                currentView.setLayoutParams(layoutParams);
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }
        if (this.lastSizeChangeValue1 == height && this.lastSizeChangeValue2 == isWidthGreater) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = height;
        this.lastSizeChangeValue2 = isWidthGreater;
        boolean oldValue = this.keyboardVisible;
        if (height <= 0) {
            z2 = false;
        }
        this.keyboardVisible = z2;
        if (z2 && isPopupShowing()) {
            showPopup(0, this.currentPopupContentType);
        }
        if (this.emojiPadding != 0 && !(z = this.keyboardVisible) && z != oldValue && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPC.ChatFull chatFull;
        TLRPC.Chat chat;
        int i = id;
        if (i == NotificationCenter.emojiDidLoad) {
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.invalidateViews();
            }
            BotKeyboardView botKeyboardView2 = this.botKeyboardView;
            if (botKeyboardView2 != null) {
                botKeyboardView2.invalidateViews();
                return;
            }
            return;
        }
        int i2 = 0;
        if (i == NotificationCenter.recordProgressChanged) {
            if (args[0].intValue() == this.recordingGuid) {
                long t = args[1].longValue();
                long time = t / 1000;
                String str = String.format("%02d:%02d.%02d", new Object[]{Long.valueOf(time / 60), Long.valueOf(time % 60), Integer.valueOf(((int) (t % 1000)) / 10)});
                String str2 = this.lastTimeString;
                if (str2 == null || !str2.equals(str)) {
                    if (this.lastTypingSendTime != time && time % 5 == 0 && !isInScheduleMode()) {
                        this.lastTypingSendTime = time;
                        MessagesController messagesController = this.accountInstance.getMessagesController();
                        long j = this.dialog_id;
                        ImageView imageView = this.videoSendButton;
                        messagesController.sendTyping(j, (imageView == null || imageView.getTag() == null) ? 1 : 7, 0);
                    }
                    TextView textView = this.recordTimeText;
                    if (textView != null) {
                        textView.setText(str);
                    }
                }
                RecordCircle recordCircle2 = this.recordCircle;
                if (recordCircle2 != null) {
                    recordCircle2.setAmplitude(args[2].doubleValue());
                }
                ImageView imageView2 = this.videoSendButton;
                if (imageView2 != null && imageView2.getTag() != null && t >= 59500) {
                    this.startedDraggingX = -1.0f;
                    this.delegate.needStartRecordVideo(3, true, 0);
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null && editTextCaption.isFocused()) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
        } else if (i == NotificationCenter.recordStartError || i == NotificationCenter.recordStopped) {
            if (args[0].intValue() == this.recordingGuid) {
                if (this.recordingAudioVideo) {
                    this.accountInstance.getMessagesController().sendTyping(this.dialog_id, 2, 0);
                    this.recordingAudioVideo = false;
                    updateRecordIntefrace();
                }
                if (i == NotificationCenter.recordStopped) {
                    Integer reason = args[1];
                    if (reason.intValue() == 2) {
                        this.videoTimelineView.setVisibility(0);
                        this.recordedAudioBackground.setVisibility(8);
                        this.recordedAudioTimeTextView.setVisibility(8);
                        this.recordedAudioPlayButton.setVisibility(8);
                        this.recordedAudioSeekBar.setVisibility(8);
                        this.recordedAudioPanel.setAlpha(1.0f);
                        this.recordedAudioPanel.setVisibility(0);
                        return;
                    }
                    reason.intValue();
                }
            }
        } else if (i == NotificationCenter.recordStarted) {
            if (args[0].intValue() == this.recordingGuid && !this.recordingAudioVideo) {
                this.recordingAudioVideo = true;
                updateRecordIntefrace();
            }
        } else if (i == NotificationCenter.audioDidSent) {
            if (args[0].intValue() == this.recordingGuid) {
                VideoEditedInfo videoEditedInfo = args[1];
                if (videoEditedInfo instanceof VideoEditedInfo) {
                    this.videoToSendMessageObject = videoEditedInfo;
                    String str3 = args[2];
                    this.audioToSendPath = str3;
                    this.videoTimelineView.setVideoPath(str3);
                    this.videoTimelineView.setVisibility(0);
                    this.videoTimelineView.setMinProgressDiff(1000.0f / ((float) this.videoToSendMessageObject.estimatedDuration));
                    this.recordedAudioBackground.setVisibility(8);
                    this.recordedAudioTimeTextView.setVisibility(8);
                    this.recordedAudioPlayButton.setVisibility(8);
                    this.recordedAudioSeekBar.setVisibility(8);
                    this.recordedAudioPanel.setAlpha(1.0f);
                    this.recordedAudioPanel.setVisibility(0);
                    closeKeyboard();
                    hidePopup(false);
                    checkSendButton(false);
                    return;
                }
                TLRPC.TL_document tL_document = args[1];
                this.audioToSend = tL_document;
                this.audioToSendPath = args[2];
                if (tL_document == null) {
                    ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
                    if (chatActivityEnterViewDelegate != null) {
                        chatActivityEnterViewDelegate.onMessageSend((CharSequence) null, true, 0);
                    }
                } else if (this.recordedAudioPanel != null) {
                    this.videoTimelineView.setVisibility(8);
                    this.recordedAudioBackground.setVisibility(0);
                    this.recordedAudioTimeTextView.setVisibility(0);
                    this.recordedAudioPlayButton.setVisibility(0);
                    this.recordedAudioSeekBar.setVisibility(0);
                    TLRPC.TL_message message = new TLRPC.TL_message();
                    message.out = true;
                    message.id = 0;
                    message.to_id = new TLRPC.TL_peerUser();
                    TLRPC.Peer peer = message.to_id;
                    int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    message.from_id = clientUserId;
                    peer.user_id = clientUserId;
                    message.date = (int) (System.currentTimeMillis() / 1000);
                    message.message = "";
                    message.attachPath = this.audioToSendPath;
                    message.media = new TLRPC.TL_messageMediaDocument();
                    message.media.flags |= 3;
                    message.media.document = this.audioToSend;
                    message.flags |= 768;
                    this.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, message, false);
                    this.recordedAudioPanel.setAlpha(1.0f);
                    this.recordedAudioPanel.setVisibility(0);
                    int duration = 0;
                    int a = 0;
                    while (true) {
                        if (a >= this.audioToSend.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = (TLRPC.DocumentAttribute) this.audioToSend.attributes.get(a);
                        if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                            duration = attribute.duration;
                            break;
                        }
                        a++;
                    }
                    int a2 = 0;
                    while (true) {
                        if (a2 >= this.audioToSend.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute2 = (TLRPC.DocumentAttribute) this.audioToSend.attributes.get(a2);
                        if (attribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                            if (attribute2.waveform == null || attribute2.waveform.length == 0) {
                                attribute2.waveform = MediaController.getInstance().getWaveform(this.audioToSendPath);
                            }
                            this.recordedAudioSeekBar.setWaveform(attribute2.waveform);
                        } else {
                            a2++;
                        }
                    }
                    this.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)}));
                    closeKeyboard();
                    hidePopup(false);
                    checkSendButton(false);
                }
            }
        } else if (i == NotificationCenter.audioRouteChanged) {
            if (this.parentActivity != null) {
                boolean frontSpeaker = args[0].booleanValue();
                Activity activity = this.parentActivity;
                if (!frontSpeaker) {
                    i2 = Integer.MIN_VALUE;
                }
                activity.setVolumeControlStream(i2);
            }
        } else if (i == NotificationCenter.messagePlayingDidReset) {
            if (this.audioToSendMessageObject != null && !MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
                this.recordedAudioPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
                this.recordedAudioSeekBar.setProgress(0.0f);
            }
        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer num = args[0];
            if (this.audioToSendMessageObject != null && MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                this.audioToSendMessageObject.audioProgress = player.audioProgress;
                this.audioToSendMessageObject.audioProgressSec = player.audioProgressSec;
                if (!this.recordedAudioSeekBar.isDragging()) {
                    this.recordedAudioSeekBar.setProgress(this.audioToSendMessageObject.audioProgress);
                }
            }
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            if (this.emojiButton != null) {
                int a3 = 0;
                while (true) {
                    ImageView[] imageViewArr = this.emojiButton;
                    if (a3 < imageViewArr.length) {
                        imageViewArr[a3].invalidate();
                        a3++;
                    } else {
                        return;
                    }
                }
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            if (!args[6].booleanValue() && args[3].longValue() == this.dialog_id && (chatFull = this.info) != null && chatFull.slowmode_seconds != 0 && (chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(this.info.id))) != null && !ChatObject.hasAdminRights(chat)) {
                this.info.slowmode_next_send_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + this.info.slowmode_seconds;
                this.info.flags |= 262144;
                setSlowModeTimer(this.info.slowmode_next_send_date);
            }
        } else if (i == NotificationCenter.sendingMessagesChanged && this.info != null) {
            updateSlowModeText();
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2 && this.pendingLocationButton != null) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
            }
            this.pendingLocationButton = null;
            this.pendingMessageObject = null;
        }
    }

    /* access modifiers changed from: private */
    public void checkStickresExpandHeight() {
        int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
        int newHeight = (((this.originalViewHeight - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
        if (this.searchingType == 2) {
            newHeight = Math.min(newHeight, AndroidUtilities.dp(120.0f) + origHeight);
        }
        int currentHeight = this.emojiView.getLayoutParams().height;
        if (currentHeight != newHeight) {
            Animator animator = this.stickersExpansionAnim;
            if (animator != null) {
                animator.cancel();
                this.stickersExpansionAnim = null;
            }
            this.stickersExpandedHeight = newHeight;
            if (currentHeight > newHeight) {
                AnimatorSet anims = new AnimatorSet();
                anims.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)})});
                ((ObjectAnimator) anims.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ChatActivityEnterView.this.lambda$checkStickresExpandHeight$29$ChatActivityEnterView(valueAnimator);
                    }
                });
                anims.setDuration(400);
                anims.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                anims.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                        if (ChatActivityEnterView.this.emojiView != null) {
                            ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                        }
                    }
                });
                this.stickersExpansionAnim = anims;
                this.emojiView.setLayerType(2, (Paint) null);
                anims.start();
                return;
            }
            this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
            this.sizeNotifierLayout.requestLayout();
            int start = this.messageEditText.getSelectionStart();
            int end = this.messageEditText.getSelectionEnd();
            EditTextCaption editTextCaption = this.messageEditText;
            editTextCaption.setText(editTextCaption.getText());
            this.messageEditText.setSelection(start, end);
            AnimatorSet anims2 = new AnimatorSet();
            anims2.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)})});
            ((ObjectAnimator) anims2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatActivityEnterView.this.lambda$checkStickresExpandHeight$30$ChatActivityEnterView(valueAnimator);
                }
            });
            anims2.setDuration(400);
            anims2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            anims2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                    ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                }
            });
            this.stickersExpansionAnim = anims2;
            this.emojiView.setLayerType(2, (Paint) null);
            anims2.start();
        }
    }

    public /* synthetic */ void lambda$checkStickresExpandHeight$29$ChatActivityEnterView(ValueAnimator animation) {
        this.sizeNotifierLayout.invalidate();
    }

    public /* synthetic */ void lambda$checkStickresExpandHeight$30$ChatActivityEnterView(ValueAnimator animation) {
        this.sizeNotifierLayout.invalidate();
    }

    /* access modifiers changed from: private */
    public void setStickersExpanded(boolean expanded, boolean animated, boolean byDrag) {
        boolean z = expanded;
        if (this.emojiView == null) {
            return;
        }
        if (byDrag || this.stickersExpanded != z) {
            this.stickersExpanded = z;
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onStickersExpandedChange();
            }
            final int origHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            Animator animator = this.stickersExpansionAnim;
            if (animator != null) {
                animator.cancel();
                this.stickersExpansionAnim = null;
            }
            if (this.stickersExpanded) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 1);
                int height = this.sizeNotifierLayout.getHeight();
                this.originalViewHeight = height;
                int currentActionBarHeight = (((height - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - ActionBar.getCurrentActionBarHeight()) - getHeight()) + Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                this.stickersExpandedHeight = currentActionBarHeight;
                if (this.searchingType == 2) {
                    this.stickersExpandedHeight = Math.min(currentActionBarHeight, AndroidUtilities.dp(120.0f) + origHeight);
                }
                this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
                this.sizeNotifierLayout.requestLayout();
                this.sizeNotifierLayout.setForeground(new ScrimDrawable());
                int start = this.messageEditText.getSelectionStart();
                int end = this.messageEditText.getSelectionEnd();
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setText(editTextCaption.getText());
                this.messageEditText.setSelection(start, end);
                if (animated) {
                    AnimatorSet anims = new AnimatorSet();
                    anims.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{-(this.stickersExpandedHeight - origHeight)}), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", new float[]{1.0f})});
                    anims.setDuration(400);
                    anims.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    ((ObjectAnimator) anims.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener(origHeight) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.lambda$setStickersExpanded$31$ChatActivityEnterView(this.f$1, valueAnimator);
                        }
                    });
                    anims.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            Animator unused = ChatActivityEnterView.this.stickersExpansionAnim = null;
                            ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                        }
                    });
                    this.stickersExpansionAnim = anims;
                    this.emojiView.setLayerType(2, (Paint) null);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
                    anims.start();
                } else {
                    this.stickersExpansionProgress = 1.0f;
                    setTranslationY((float) (-(this.stickersExpandedHeight - origHeight)));
                    this.emojiView.setTranslationY((float) (-(this.stickersExpandedHeight - origHeight)));
                    this.stickersArrow.setAnimationProgress(1.0f);
                }
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 1);
                if (animated) {
                    this.closeAnimationInProgress = true;
                    AnimatorSet anims2 = new AnimatorSet();
                    anims2.playTogether(new Animator[]{ObjectAnimator.ofInt(this, this.roundedTranslationYProperty, new int[]{0}), ObjectAnimator.ofInt(this.emojiView, this.roundedTranslationYProperty, new int[]{0}), ObjectAnimator.ofFloat(this.stickersArrow, "animationProgress", new float[]{0.0f})});
                    anims2.setDuration(400);
                    anims2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    ((ObjectAnimator) anims2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener(origHeight) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ChatActivityEnterView.this.lambda$setStickersExpanded$32$ChatActivityEnterView(this.f$1, valueAnimator);
                        }
                    });
                    anims2.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            boolean unused = ChatActivityEnterView.this.closeAnimationInProgress = false;
                            Animator unused2 = ChatActivityEnterView.this.stickersExpansionAnim = null;
                            if (ChatActivityEnterView.this.emojiView != null) {
                                ChatActivityEnterView.this.emojiView.getLayoutParams().height = origHeight;
                                ChatActivityEnterView.this.emojiView.setLayerType(0, (Paint) null);
                            }
                            if (ChatActivityEnterView.this.sizeNotifierLayout != null) {
                                ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                                ChatActivityEnterView.this.sizeNotifierLayout.setForeground((Drawable) null);
                                ChatActivityEnterView.this.sizeNotifierLayout.setWillNotDraw(false);
                            }
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                        }
                    });
                    this.stickersExpansionAnim = anims2;
                    this.emojiView.setLayerType(2, (Paint) null);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
                    anims2.start();
                } else {
                    this.stickersExpansionProgress = 0.0f;
                    setTranslationY(0.0f);
                    this.emojiView.setTranslationY(0.0f);
                    this.emojiView.getLayoutParams().height = origHeight;
                    this.sizeNotifierLayout.requestLayout();
                    this.sizeNotifierLayout.setForeground((Drawable) null);
                    this.sizeNotifierLayout.setWillNotDraw(false);
                    this.stickersArrow.setAnimationProgress(0.0f);
                }
            }
            if (z) {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrCollapsePanel", R.string.AccDescrCollapsePanel));
            } else {
                this.expandStickersButton.setContentDescription(LocaleController.getString("AccDescrExpandPanel", R.string.AccDescrExpandPanel));
            }
        }
    }

    public /* synthetic */ void lambda$setStickersExpanded$31$ChatActivityEnterView(int origHeight, ValueAnimator animation) {
        this.stickersExpansionProgress = getTranslationY() / ((float) (-(this.stickersExpandedHeight - origHeight)));
        this.sizeNotifierLayout.invalidate();
    }

    public /* synthetic */ void lambda$setStickersExpanded$32$ChatActivityEnterView(int origHeight, ValueAnimator animation) {
        this.stickersExpansionProgress = getTranslationY() / ((float) (-(this.stickersExpandedHeight - origHeight)));
        this.sizeNotifierLayout.invalidate();
    }

    public void updateMenuViewStatus() {
        if (this.menuView != null && this.menuViewVisible && isPopupShowing()) {
            EnterMenuView enterMenuView = this.menuView;
            ChatActivity chatActivity = this.parentFragment;
            enterMenuView.setCurrentChat(chatActivity != null ? chatActivity.getCurrentChat() : null);
        }
    }

    private class ScrimDrawable extends Drawable {
        private Paint paint;

        public ScrimDrawable() {
            Paint paint2 = new Paint();
            this.paint = paint2;
            paint2.setColor(0);
        }

        public void draw(Canvas canvas) {
            if (ChatActivityEnterView.this.emojiView != null) {
                this.paint.setAlpha(Math.round(ChatActivityEnterView.this.stickersExpansionProgress * 102.0f));
                canvas.drawRect(0.0f, 0.0f, (float) ChatActivityEnterView.this.getWidth(), (ChatActivityEnterView.this.emojiView.getY() - ((float) ChatActivityEnterView.this.getHeight())) + ((float) Theme.chat_composeShadowDrawable.getIntrinsicHeight()), this.paint);
            }
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
        }
    }
}
