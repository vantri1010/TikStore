package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerEnd;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.MimeTypes;
import com.king.zxing.util.LogUtils;
import com.serenegiant.usb.UVCCamera;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.Bitmaps;
import im.bclpbkiauv.messenger.BringAppForegroundService;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SecureDocument;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.VideoEditedInfo;
import im.bclpbkiauv.messenger.WebFile;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.MediaActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem;
import im.bclpbkiauv.ui.actionbar.ActionBarPopupWindow;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.MentionsAdapter;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.cells.PhotoPickerPhotoCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AnimatedFileDrawable;
import im.bclpbkiauv.ui.components.AnimationProperties;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ChatAttachAlert;
import im.bclpbkiauv.ui.components.CheckBox;
import im.bclpbkiauv.ui.components.ClippingImageView;
import im.bclpbkiauv.ui.components.GroupedPhotosListView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.NumberPicker;
import im.bclpbkiauv.ui.components.OtherDocumentPlaceholderDrawable;
import im.bclpbkiauv.ui.components.PhotoCropView;
import im.bclpbkiauv.ui.components.PhotoFilterView;
import im.bclpbkiauv.ui.components.PhotoPaintView;
import im.bclpbkiauv.ui.components.PhotoViewerCaptionEnterView;
import im.bclpbkiauv.ui.components.PickerBottomLayoutViewer;
import im.bclpbkiauv.ui.components.PipVideoView;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SeekBar;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayoutPhoto;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.components.URLSpanNoUnderline;
import im.bclpbkiauv.ui.components.URLSpanUserMention;
import im.bclpbkiauv.ui.components.VideoForwardDrawable;
import im.bclpbkiauv.ui.components.VideoPlayer;
import im.bclpbkiauv.ui.components.VideoSeekPreviewImage;
import im.bclpbkiauv.ui.components.VideoTimelinePlayView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class PhotoViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static volatile PhotoViewer Instance = null;
    private static volatile PhotoViewer PipInstance = null;
    public static final int SELECT_TYPE_AVATAR = 1;
    public static final int SELECT_TYPE_WALLPAPER = 3;
    /* access modifiers changed from: private */
    public static DecelerateInterpolator decelerateInterpolator = null;
    private static final int gallery_menu_cancel_loading = 7;
    private static final int gallery_menu_delete = 6;
    private static final int gallery_menu_masks = 13;
    private static final int gallery_menu_openin = 11;
    private static final int gallery_menu_pip = 5;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_send = 3;
    private static final int gallery_menu_share = 10;
    private static final int gallery_menu_showall = 2;
    private static final int gallery_menu_showinchat = 4;
    /* access modifiers changed from: private */
    public static Drawable[] progressDrawables;
    /* access modifiers changed from: private */
    public static Paint progressPaint;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimator;
    private Context actvityContext;
    private ActionBarMenuSubItem allMediaItem;
    /* access modifiers changed from: private */
    public boolean allowMentions;
    private boolean allowOrder = true;
    private boolean allowShare;
    /* access modifiers changed from: private */
    public float animateToScale;
    /* access modifiers changed from: private */
    public float animateToX;
    /* access modifiers changed from: private */
    public float animateToY;
    /* access modifiers changed from: private */
    public ClippingImageView animatingImageView;
    /* access modifiers changed from: private */
    public Runnable animationEndRunnable;
    /* access modifiers changed from: private */
    public int animationInProgress;
    /* access modifiers changed from: private */
    public long animationStartTime;
    private float animationValue;
    /* access modifiers changed from: private */
    public float[][] animationValues = ((float[][]) Array.newInstance(float.class, new int[]{2, 10}));
    /* access modifiers changed from: private */
    public boolean applying;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout aspectRatioFrameLayout;
    /* access modifiers changed from: private */
    public boolean attachedToWindow;
    /* access modifiers changed from: private */
    public long audioFramesSize;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Photo> avatarsArr = new ArrayList<>();
    /* access modifiers changed from: private */
    public int avatarsDialogId;
    /* access modifiers changed from: private */
    public BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-16777216);
    /* access modifiers changed from: private */
    public int bitrate;
    /* access modifiers changed from: private */
    public Paint blackPaint = new Paint();
    /* access modifiers changed from: private */
    public FrameLayout bottomLayout;
    /* access modifiers changed from: private */
    public boolean bottomTouchEnabled = true;
    /* access modifiers changed from: private */
    public ImageView cameraItem;
    private boolean canDragDown = true;
    private boolean canZoom = true;
    /* access modifiers changed from: private */
    public PhotoViewerCaptionEnterView captionEditText;
    /* access modifiers changed from: private */
    public TextView captionTextView;
    /* access modifiers changed from: private */
    public ImageReceiver centerImage = new ImageReceiver();
    /* access modifiers changed from: private */
    public AnimatorSet changeModeAnimation;
    /* access modifiers changed from: private */
    public TextureView changedTextureView;
    private boolean changingPage;
    /* access modifiers changed from: private */
    public boolean changingTextureView;
    /* access modifiers changed from: private */
    public CheckBox checkImageView;
    private int classGuid;
    /* access modifiers changed from: private */
    public ImageView compressItem;
    private AnimatorSet compressItemAnimation;
    /* access modifiers changed from: private */
    public int compressionsCount = -1;
    /* access modifiers changed from: private */
    public FrameLayoutDrawer containerView;
    private ImageView cropItem;
    /* access modifiers changed from: private */
    public int currentAccount;
    private AnimatedFileDrawable currentAnimation;
    /* access modifiers changed from: private */
    public Bitmap currentBitmap;
    private TLRPC.BotInlineResult currentBotInlineResult;
    /* access modifiers changed from: private */
    public AnimatorSet currentCaptionAnimation;
    /* access modifiers changed from: private */
    public long currentDialogId;
    /* access modifiers changed from: private */
    public int currentEditMode;
    /* access modifiers changed from: private */
    public ImageLocation currentFileLocation;
    /* access modifiers changed from: private */
    public String[] currentFileNames = new String[3];
    /* access modifiers changed from: private */
    public int currentIndex;
    /* access modifiers changed from: private */
    public AnimatorSet currentListViewAnimation;
    /* access modifiers changed from: private */
    public Runnable currentLoadingVideoRunnable;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject;
    private String currentPathObject;
    /* access modifiers changed from: private */
    public PlaceProviderObject currentPlaceObject;
    private Uri currentPlayingVideoFile;
    private SecureDocument currentSecureDocument;
    private String currentSubtitle;
    /* access modifiers changed from: private */
    public ImageReceiver.BitmapHolder currentThumb;
    /* access modifiers changed from: private */
    public ImageLocation currentUserAvatarLocation = null;
    /* access modifiers changed from: private */
    public boolean currentVideoFinishedLoading;
    private int dateOverride;
    private TextView dateTextView;
    /* access modifiers changed from: private */
    public boolean disableShowCheck;
    private boolean discardTap;
    private boolean doneButtonPressed;
    /* access modifiers changed from: private */
    public boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private boolean doubleTapEnabled;
    private float dragY;
    private boolean draggingDown;
    /* access modifiers changed from: private */
    public PickerBottomLayoutViewer editorDoneLayout;
    private boolean[] endReached = {false, true};
    private long endTime;
    private long estimatedDuration;
    private int estimatedSize;
    private boolean firstAnimationDelay;
    boolean fromCamera;
    private GestureDetector gestureDetector;
    /* access modifiers changed from: private */
    public GroupedPhotosListView groupedPhotosListView;
    /* access modifiers changed from: private */
    public PlaceProviderObject hideAfterAnimation;
    private boolean ignoreDidSetImage;
    /* access modifiers changed from: private */
    public AnimatorSet imageMoveAnimation;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> imagesArr = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Object> imagesArrLocals = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ImageLocation> imagesArrLocations = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Integer> imagesArrLocationsSizes = new ArrayList<>();
    private ArrayList<MessageObject> imagesArrTemp = new ArrayList<>();
    private SparseArray<MessageObject>[] imagesByIds = {new SparseArray<>(), new SparseArray<>()};
    private SparseArray<MessageObject>[] imagesByIdsTemp = {new SparseArray<>(), new SparseArray<>()};
    /* access modifiers changed from: private */
    public boolean inPreview;
    private VideoPlayer injectingVideoPlayer;
    private SurfaceTexture injectingVideoPlayerSurface;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    /* access modifiers changed from: private */
    public boolean isCurrentVideo;
    /* access modifiers changed from: private */
    public boolean isEvent;
    boolean isFcCrop;
    private boolean isFirstLoading;
    /* access modifiers changed from: private */
    public boolean isInline;
    /* access modifiers changed from: private */
    public boolean isPhotosListViewVisible;
    /* access modifiers changed from: private */
    public boolean isPlaying;
    private boolean isSingleLine;
    /* access modifiers changed from: private */
    public boolean isStreaming;
    /* access modifiers changed from: private */
    public boolean isVisible;
    private boolean keepScreenOnFlagSet;
    /* access modifiers changed from: private */
    public long lastBufferedPositionCheck;
    /* access modifiers changed from: private */
    public Object lastInsets;
    /* access modifiers changed from: private */
    public long lastSaveTime;
    private String lastTitle;
    private ImageReceiver leftImage = new ImageReceiver();
    private boolean loadInitialVideo;
    private boolean loadingMoreImages;
    /* access modifiers changed from: private */
    public boolean mShowNeedAddMorePicButton = true;
    private ActionBarMenuItem masksItem;
    private int maxSelectedPhotos = -1;
    private float maxX;
    private float maxY;
    /* access modifiers changed from: private */
    public LinearLayoutManager mentionLayoutManager;
    /* access modifiers changed from: private */
    public AnimatorSet mentionListAnimation;
    /* access modifiers changed from: private */
    public RecyclerListView mentionListView;
    /* access modifiers changed from: private */
    public MentionsAdapter mentionsAdapter;
    /* access modifiers changed from: private */
    public ActionBarMenuItem menuItem;
    private long mergeDialogId;
    private float minX;
    private float minY;
    /* access modifiers changed from: private */
    public AnimatorSet miniProgressAnimator;
    private Runnable miniProgressShowRunnable = new Runnable() {
        public final void run() {
            PhotoViewer.this.lambda$new$0$PhotoViewer();
        }
    };
    /* access modifiers changed from: private */
    public RadialProgressView miniProgressView;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    /* access modifiers changed from: private */
    public ImageView muteItem;
    /* access modifiers changed from: private */
    public boolean muteVideo;
    private String nameOverride;
    private TextView nameTextView;
    /* access modifiers changed from: private */
    public boolean needCaptionLayout;
    private boolean needSearchImageInArr;
    /* access modifiers changed from: private */
    public boolean needShowOnReady;
    /* access modifiers changed from: private */
    public boolean openedFullScreenVideo;
    private boolean opennedFromMedia;
    /* access modifiers changed from: private */
    public int originalBitrate;
    /* access modifiers changed from: private */
    public int originalHeight;
    private long originalSize;
    /* access modifiers changed from: private */
    public int originalWidth;
    /* access modifiers changed from: private */
    public boolean padImageForHorizontalInsets;
    private ImageView paintItem;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public ChatAttachAlert parentAlert;
    /* access modifiers changed from: private */
    public ChatActivity parentChatActivity;
    /* access modifiers changed from: private */
    public PhotoCropView photoCropView;
    /* access modifiers changed from: private */
    public PhotoFilterView photoFilterView;
    /* access modifiers changed from: private */
    public PhotoPaintView photoPaintView;
    private PhotoProgressView[] photoProgressViews = new PhotoProgressView[3];
    /* access modifiers changed from: private */
    public CounterView photosCounterView;
    /* access modifiers changed from: private */
    public FrameLayout pickerView;
    /* access modifiers changed from: private */
    public ImageView pickerViewSendButton;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = 1.0f;
    private float pinchStartX;
    private float pinchStartY;
    /* access modifiers changed from: private */
    public boolean pipAnimationInProgress;
    private boolean pipAvailable;
    /* access modifiers changed from: private */
    public ActionBarMenuItem pipItem;
    /* access modifiers changed from: private */
    public int[] pipPosition = new int[2];
    /* access modifiers changed from: private */
    public PipVideoView pipVideoView;
    /* access modifiers changed from: private */
    public PhotoViewerProvider placeProvider;
    private View playButtonAccessibilityOverlay;
    private boolean playerInjected;
    private boolean playerWasReady;
    private int previewViewEnd;
    private int previousCompression;
    private RadialProgressView progressView;
    /* access modifiers changed from: private */
    public QualityChooseView qualityChooseView;
    /* access modifiers changed from: private */
    public AnimatorSet qualityChooseViewAnimation;
    /* access modifiers changed from: private */
    public PickerBottomLayoutViewer qualityPicker;
    private boolean requestingPreview;
    private TextView resetButton;
    /* access modifiers changed from: private */
    public int resultHeight;
    /* access modifiers changed from: private */
    public int resultWidth;
    private ImageReceiver rightImage = new ImageReceiver();
    private ImageView rotateItem;
    /* access modifiers changed from: private */
    public int rotationValue;
    /* access modifiers changed from: private */
    public float scale = 1.0f;
    private Scroller scroller;
    /* access modifiers changed from: private */
    public ArrayList<SecureDocument> secureDocuments = new ArrayList<>();
    /* access modifiers changed from: private */
    public float seekToProgressPending;
    private float seekToProgressPending2;
    /* access modifiers changed from: private */
    public int selectedCompression;
    /* access modifiers changed from: private */
    public ListAdapter selectedPhotosAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView selectedPhotosListView;
    private ActionBarMenuItem sendItem;
    /* access modifiers changed from: private */
    public int sendPhotoType;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private Runnable setLoadingRunnable = new Runnable() {
        public void run() {
            if (PhotoViewer.this.currentMessageObject != null) {
                FileLoader.getInstance(PhotoViewer.this.currentMessageObject.currentAccount).setLoadingVideo(PhotoViewer.this.currentMessageObject.getDocument(), true, false);
            }
        }
    };
    private ImageView shareButton;
    /* access modifiers changed from: private */
    public int sharedMediaType;
    /* access modifiers changed from: private */
    public String shouldSavePositionForCurrentVideo;
    /* access modifiers changed from: private */
    public PlaceProviderObject showAfterAnimation;
    private ImageReceiver sideImage;
    private boolean skipFirstBufferingProgress;
    /* access modifiers changed from: private */
    public int slideshowMessageId;
    private long startTime;
    private long startedPlayTime;
    private boolean streamingAlertShown;
    /* access modifiers changed from: private */
    public TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (PhotoViewer.this.videoTextureView == null || !PhotoViewer.this.changingTextureView) {
                return true;
            }
            if (PhotoViewer.this.switchingInlineMode) {
                int unused = PhotoViewer.this.waitingForFirstTextureUpload = 2;
            }
            PhotoViewer.this.videoTextureView.setSurfaceTexture(surface);
            PhotoViewer.this.videoTextureView.setVisibility(0);
            boolean unused2 = PhotoViewer.this.changingTextureView = false;
            PhotoViewer.this.containerView.invalidate();
            return false;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (PhotoViewer.this.waitingForFirstTextureUpload == 1) {
                PhotoViewer.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        PhotoViewer.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (PhotoViewer.this.textureImageView != null) {
                            PhotoViewer.this.textureImageView.setVisibility(4);
                            PhotoViewer.this.textureImageView.setImageDrawable((Drawable) null);
                            if (PhotoViewer.this.currentBitmap != null) {
                                PhotoViewer.this.currentBitmap.recycle();
                                Bitmap unused = PhotoViewer.this.currentBitmap = null;
                            }
                        }
                        AndroidUtilities.runOnUIThread(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0052: INVOKE  
                              (wrap: im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$4$1$AEE-UDiRcFO0_y5dGJlO2PAie2c : 0x004f: CONSTRUCTOR  (r0v7 im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$4$1$AEE-UDiRcFO0_y5dGJlO2PAie2c) = (r2v0 'this' im.bclpbkiauv.ui.PhotoViewer$4$1 A[THIS]) call: im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$4$1$AEE-UDiRcFO0_y5dGJlO2PAie2c.<init>(im.bclpbkiauv.ui.PhotoViewer$4$1):void type: CONSTRUCTOR)
                             im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.PhotoViewer.4.1.onPreDraw():boolean, dex: classes2.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                            	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x004f: CONSTRUCTOR  (r0v7 im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$4$1$AEE-UDiRcFO0_y5dGJlO2PAie2c) = (r2v0 'this' im.bclpbkiauv.ui.PhotoViewer$4$1 A[THIS]) call: im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$4$1$AEE-UDiRcFO0_y5dGJlO2PAie2c.<init>(im.bclpbkiauv.ui.PhotoViewer$4$1):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.PhotoViewer.4.1.onPreDraw():boolean, dex: classes2.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 81 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$4$1$AEE-UDiRcFO0_y5dGJlO2PAie2c, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 87 more
                            */
                        /*
                            this = this;
                            im.bclpbkiauv.ui.PhotoViewer$4 r0 = im.bclpbkiauv.ui.PhotoViewer.AnonymousClass4.this
                            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.this
                            android.view.TextureView r0 = r0.changedTextureView
                            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
                            r0.removeOnPreDrawListener(r2)
                            im.bclpbkiauv.ui.PhotoViewer$4 r0 = im.bclpbkiauv.ui.PhotoViewer.AnonymousClass4.this
                            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.this
                            android.widget.ImageView r0 = r0.textureImageView
                            if (r0 == 0) goto L_0x004d
                            im.bclpbkiauv.ui.PhotoViewer$4 r0 = im.bclpbkiauv.ui.PhotoViewer.AnonymousClass4.this
                            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.this
                            android.widget.ImageView r0 = r0.textureImageView
                            r1 = 4
                            r0.setVisibility(r1)
                            im.bclpbkiauv.ui.PhotoViewer$4 r0 = im.bclpbkiauv.ui.PhotoViewer.AnonymousClass4.this
                            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.this
                            android.widget.ImageView r0 = r0.textureImageView
                            r1 = 0
                            r0.setImageDrawable(r1)
                            im.bclpbkiauv.ui.PhotoViewer$4 r0 = im.bclpbkiauv.ui.PhotoViewer.AnonymousClass4.this
                            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.this
                            android.graphics.Bitmap r0 = r0.currentBitmap
                            if (r0 == 0) goto L_0x004d
                            im.bclpbkiauv.ui.PhotoViewer$4 r0 = im.bclpbkiauv.ui.PhotoViewer.AnonymousClass4.this
                            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.this
                            android.graphics.Bitmap r0 = r0.currentBitmap
                            r0.recycle()
                            im.bclpbkiauv.ui.PhotoViewer$4 r0 = im.bclpbkiauv.ui.PhotoViewer.AnonymousClass4.this
                            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.this
                            android.graphics.Bitmap unused = r0.currentBitmap = r1
                        L_0x004d:
                            im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$4$1$AEE-UDiRcFO0_y5dGJlO2PAie2c r0 = new im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$4$1$AEE-UDiRcFO0_y5dGJlO2PAie2c
                            r0.<init>(r2)
                            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                            im.bclpbkiauv.ui.PhotoViewer$4 r0 = im.bclpbkiauv.ui.PhotoViewer.AnonymousClass4.this
                            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.this
                            r1 = 0
                            int unused = r0.waitingForFirstTextureUpload = r1
                            r0 = 1
                            return r0
                        */
                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PhotoViewer.AnonymousClass4.AnonymousClass1.onPreDraw():boolean");
                    }

                    public /* synthetic */ void lambda$onPreDraw$0$PhotoViewer$4$1() {
                        if (PhotoViewer.this.isInline) {
                            PhotoViewer.this.dismissInternal();
                        }
                    }
                });
                PhotoViewer.this.changedTextureView.invalidate();
            }
        }
    };
    /* access modifiers changed from: private */
    public TextView switchCaptionTextView;
    private int switchImageAfterAnimation;
    /* access modifiers changed from: private */
    public Runnable switchToInlineRunnable = new Runnable() {
        public void run() {
            boolean unused = PhotoViewer.this.switchingInlineMode = false;
            if (PhotoViewer.this.currentBitmap != null) {
                PhotoViewer.this.currentBitmap.recycle();
                Bitmap unused2 = PhotoViewer.this.currentBitmap = null;
            }
            boolean unused3 = PhotoViewer.this.changingTextureView = true;
            if (PhotoViewer.this.textureImageView != null) {
                try {
                    Bitmap unused4 = PhotoViewer.this.currentBitmap = Bitmaps.createBitmap(PhotoViewer.this.videoTextureView.getWidth(), PhotoViewer.this.videoTextureView.getHeight(), Bitmap.Config.ARGB_8888);
                    PhotoViewer.this.videoTextureView.getBitmap(PhotoViewer.this.currentBitmap);
                } catch (Throwable e) {
                    if (PhotoViewer.this.currentBitmap != null) {
                        PhotoViewer.this.currentBitmap.recycle();
                        Bitmap unused5 = PhotoViewer.this.currentBitmap = null;
                    }
                    FileLog.e(e);
                }
                if (PhotoViewer.this.currentBitmap != null) {
                    PhotoViewer.this.textureImageView.setVisibility(0);
                    PhotoViewer.this.textureImageView.setImageBitmap(PhotoViewer.this.currentBitmap);
                } else {
                    PhotoViewer.this.textureImageView.setImageDrawable((Drawable) null);
                }
            }
            boolean unused6 = PhotoViewer.this.isInline = true;
            PipVideoView unused7 = PhotoViewer.this.pipVideoView = new PipVideoView();
            PhotoViewer photoViewer = PhotoViewer.this;
            PipVideoView access$1500 = photoViewer.pipVideoView;
            Activity access$2800 = PhotoViewer.this.parentActivity;
            PhotoViewer photoViewer2 = PhotoViewer.this;
            TextureView unused8 = photoViewer.changedTextureView = access$1500.show(access$2800, photoViewer2, photoViewer2.aspectRatioFrameLayout.getAspectRatio(), PhotoViewer.this.aspectRatioFrameLayout.getVideoRotation());
            PhotoViewer.this.changedTextureView.setVisibility(4);
            PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoTextureView);
        }
    };
    /* access modifiers changed from: private */
    public boolean switchingInlineMode;
    private int switchingToIndex;
    /* access modifiers changed from: private */
    public ImageView textureImageView;
    /* access modifiers changed from: private */
    public boolean textureUploaded;
    private ImageView timeItem;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    /* access modifiers changed from: private */
    public long transitionAnimationStartTime;
    /* access modifiers changed from: private */
    public float translationX;
    /* access modifiers changed from: private */
    public float translationY;
    private boolean tryStartRequestPreviewOnFinish;
    private ImageView tuneItem;
    /* access modifiers changed from: private */
    public Runnable updateProgressRunnable = new Runnable() {
        public void run() {
            float bufferedProgress;
            float bufferedProgress2;
            if (PhotoViewer.this.videoPlayer != null) {
                if (!PhotoViewer.this.isCurrentVideo) {
                    float progress = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (PhotoViewer.this.currentVideoFinishedLoading) {
                        bufferedProgress = 1.0f;
                    } else {
                        long newTime = SystemClock.elapsedRealtime();
                        if (Math.abs(newTime - PhotoViewer.this.lastBufferedPositionCheck) >= 500) {
                            if (PhotoViewer.this.isStreaming) {
                                bufferedProgress2 = FileLoader.getInstance(PhotoViewer.this.currentAccount).getBufferedProgressFromPosition(PhotoViewer.this.seekToProgressPending != 0.0f ? PhotoViewer.this.seekToProgressPending : progress, PhotoViewer.this.currentFileNames[0]);
                            } else {
                                bufferedProgress2 = 1.0f;
                            }
                            long unused = PhotoViewer.this.lastBufferedPositionCheck = newTime;
                            bufferedProgress = bufferedProgress2;
                        } else {
                            bufferedProgress = -1.0f;
                        }
                    }
                    if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                        if (PhotoViewer.this.seekToProgressPending == 0.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
                        }
                        if (bufferedProgress != -1.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setBufferedProgress(bufferedProgress);
                            if (PhotoViewer.this.pipVideoView != null) {
                                PhotoViewer.this.pipVideoView.setBufferedProgress(bufferedProgress);
                            }
                        }
                    } else if (progress >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                        PhotoViewer.this.videoPlayer.pause();
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                        PhotoViewer.this.containerView.invalidate();
                    } else {
                        float progress2 = progress - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (progress2 < 0.0f) {
                            progress2 = 0.0f;
                        }
                        progress = progress2 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (progress > 1.0f) {
                            progress = 1.0f;
                        }
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
                    }
                    PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                    if (PhotoViewer.this.shouldSavePositionForCurrentVideo != null) {
                        float value = progress;
                        if (value >= 0.0f && PhotoViewer.this.shouldSavePositionForCurrentVideo != null && SystemClock.uptimeMillis() - PhotoViewer.this.lastSaveTime >= 1000) {
                            String access$1700 = PhotoViewer.this.shouldSavePositionForCurrentVideo;
                            long unused2 = PhotoViewer.this.lastSaveTime = SystemClock.uptimeMillis();
                            Utilities.globalQueue.postRunnable(new Runnable(value) {
                                private final /* synthetic */ float f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    PhotoViewer.AnonymousClass2.this.lambda$run$0$PhotoViewer$2(this.f$1);
                                }
                            });
                        }
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                } else if (!PhotoViewer.this.videoTimelineView.isDragging()) {
                    float progress3 = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                        PhotoViewer.this.videoTimelineView.setProgress(progress3);
                    } else if (progress3 >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                        PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                        if (PhotoViewer.this.muteVideo) {
                            PhotoViewer.this.videoPlayer.play();
                        } else {
                            PhotoViewer.this.videoPlayer.pause();
                        }
                        PhotoViewer.this.containerView.invalidate();
                    } else {
                        float progress4 = progress3 - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (progress4 < 0.0f) {
                            progress4 = 0.0f;
                        }
                        float progress5 = progress4 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (progress5 > 1.0f) {
                            progress5 = 1.0f;
                        }
                        PhotoViewer.this.videoTimelineView.setProgress(progress5);
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                }
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 17);
            }
        }

        public /* synthetic */ void lambda$run$0$PhotoViewer$2(float value) {
            ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit().putFloat(PhotoViewer.this.shouldSavePositionForCurrentVideo, value).commit();
        }
    };
    private VelocityTracker velocityTracker;
    private ImageView videoBackwardButton;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private float videoCutEnd;
    private float videoCutStart;
    /* access modifiers changed from: private */
    public float videoDuration;
    private ImageView videoForwardButton;
    private VideoForwardDrawable videoForwardDrawable;
    /* access modifiers changed from: private */
    public int videoFramerate;
    /* access modifiers changed from: private */
    public long videoFramesSize;
    /* access modifiers changed from: private */
    public boolean videoHasAudio;
    private ImageView videoPlayButton;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;
    /* access modifiers changed from: private */
    public FrameLayout videoPlayerControlFrameLayout;
    /* access modifiers changed from: private */
    public SeekBar videoPlayerSeekbar;
    /* access modifiers changed from: private */
    public SimpleTextView videoPlayerTime;
    /* access modifiers changed from: private */
    public VideoSeekPreviewImage videoPreviewFrame;
    /* access modifiers changed from: private */
    public AnimatorSet videoPreviewFrameAnimation;
    private MessageObject videoPreviewMessageObject;
    /* access modifiers changed from: private */
    public TextureView videoTextureView;
    /* access modifiers changed from: private */
    public VideoTimelinePlayView videoTimelineView;
    private AlertDialog visibleDialog;
    private int waitingForDraw;
    /* access modifiers changed from: private */
    public int waitingForFirstTextureUpload;
    /* access modifiers changed from: private */
    public boolean wasLayout;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public FrameLayout windowView;
    /* access modifiers changed from: private */
    public boolean zoomAnimation;
    private boolean zooming;

    public interface PhotoViewerProvider {
        boolean allowCaption();

        boolean canCaptureMorePhotos();

        boolean canScrollAway();

        boolean cancelButtonPressed();

        void deleteImageAtIndex(int i);

        String getDeleteMessageString();

        int getPhotoIndex(int i);

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z);

        int getSelectedCount();

        HashMap<Object, Object> getSelectedPhotos();

        ArrayList<Object> getSelectedPhotosOrder();

        ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i);

        boolean isPhotoChecked(int i);

        void needAddMorePhotos();

        boolean scaleToFill();

        void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2);

        int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo);

        int setPhotoUnchecked(Object obj);

        void updatePhotoAtIndex(int i);

        void willHidePhotoViewer();

        void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i);
    }

    public static class PlaceProviderObject {
        public ClippingImageView animatingImageView;
        public int clipBottomAddition;
        public int clipTopAddition;
        public int dialogId;
        public ImageReceiver imageReceiver;
        public int index;
        public boolean isEvent;
        public View parentView;
        public int radius;
        public float scale = 1.0f;
        public int size;
        public ImageReceiver.BitmapHolder thumb;
        public int viewX;
        public int viewY;
    }

    public /* synthetic */ void lambda$new$0$PhotoViewer() {
        toggleMiniProgressInternal(true);
    }

    private class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    URLSpanNoUnderline[] links = (URLSpanNoUnderline[]) buffer.getSpans(widget.getSelectionStart(), widget.getSelectionEnd(), URLSpanNoUnderline.class);
                    if (links != null && links.length > 0) {
                        String url = links[0].getURL();
                        if (!(!url.startsWith(MimeTypes.BASE_TYPE_VIDEO) || PhotoViewer.this.videoPlayer == null || PhotoViewer.this.currentMessageObject == null)) {
                            int seconds = Utilities.parseInt(url).intValue();
                            if (PhotoViewer.this.videoPlayer.getDuration() == C.TIME_UNSET) {
                                float unused = PhotoViewer.this.seekToProgressPending = ((float) seconds) / ((float) PhotoViewer.this.currentMessageObject.getDuration());
                            } else {
                                PhotoViewer.this.videoPlayer.seekTo(((long) seconds) * 1000);
                            }
                        }
                    }
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    private class BackgroundDrawable extends ColorDrawable {
        private boolean allowDrawContent;
        /* access modifiers changed from: private */
        public Runnable drawRunnable;
        private final Paint paint;
        private final RectF rect = new RectF();
        private final RectF visibleRect = new RectF();

        public BackgroundDrawable(int color) {
            super(color);
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            paint2.setColor(color);
        }

        public void setAlpha(int alpha) {
            if (PhotoViewer.this.parentActivity instanceof LaunchActivity) {
                this.allowDrawContent = !PhotoViewer.this.isVisible || alpha != 255;
                ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
                if (PhotoViewer.this.parentAlert != null) {
                    if (!this.allowDrawContent) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                PhotoViewer.BackgroundDrawable.this.lambda$setAlpha$0$PhotoViewer$BackgroundDrawable();
                            }
                        }, 50);
                    } else if (PhotoViewer.this.parentAlert != null) {
                        PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
                    }
                }
            }
            super.setAlpha(alpha);
            this.paint.setAlpha(alpha);
        }

        public /* synthetic */ void lambda$setAlpha$0$PhotoViewer$BackgroundDrawable() {
            if (PhotoViewer.this.parentAlert != null) {
                PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
            }
        }

        public void draw(Canvas canvas) {
            Runnable runnable;
            if (PhotoViewer.this.animationInProgress == 0 || AndroidUtilities.isTablet() || PhotoViewer.this.currentPlaceObject == null || PhotoViewer.this.currentPlaceObject.animatingImageView == null) {
                super.draw(canvas);
            } else {
                PhotoViewer.this.animatingImageView.getClippedVisibleRect(this.visibleRect);
                if (!this.visibleRect.isEmpty()) {
                    this.visibleRect.inset((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f));
                    Rect boundsRect = getBounds();
                    float width = (float) boundsRect.right;
                    float height = (float) boundsRect.bottom;
                    for (int i = 0; i < 4; i++) {
                        if (i == 0) {
                            this.rect.set(0.0f, this.visibleRect.top, this.visibleRect.left, this.visibleRect.bottom);
                        } else if (i == 1) {
                            this.rect.set(0.0f, 0.0f, width, this.visibleRect.top);
                        } else if (i == 2) {
                            this.rect.set(this.visibleRect.right, this.visibleRect.top, width, this.visibleRect.bottom);
                        } else if (i == 3) {
                            this.rect.set(0.0f, this.visibleRect.bottom, width, height);
                        }
                        canvas.drawRect(this.rect, this.paint);
                    }
                }
            }
            if (getAlpha() != 0 && (runnable = this.drawRunnable) != null) {
                AndroidUtilities.runOnUIThread(runnable);
                this.drawRunnable = null;
            }
        }
    }

    private class CounterView extends View {
        private int currentCount = 0;
        private int height;
        private Paint paint;
        private RectF rect;
        private float rotation;
        private StaticLayout staticLayout;
        private TextPaint textPaint;
        private int width;

        public CounterView(Context context) {
            super(context);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(18.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textPaint.setColor(-1);
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            paint2.setColor(-1);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            this.rect = new RectF();
            setCount(0);
        }

        public void setScaleX(float scaleX) {
            super.setScaleX(scaleX);
            invalidate();
        }

        public void setRotationX(float rotationX) {
            this.rotation = rotationX;
            invalidate();
        }

        public float getRotationX() {
            return this.rotation;
        }

        public void setCount(int value) {
            StaticLayout staticLayout2 = new StaticLayout("" + Math.max(1, value), this.textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.staticLayout = staticLayout2;
            this.width = (int) Math.ceil((double) staticLayout2.getLineWidth(0));
            this.height = this.staticLayout.getLineBottom(0);
            AnimatorSet animatorSet = new AnimatorSet();
            if (value == 0) {
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofInt(this.paint, AnimationProperties.PAINT_ALPHA, new int[]{0}), ObjectAnimator.ofInt(this.textPaint, AnimationProperties.PAINT_ALPHA, new int[]{0})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
            } else {
                int i = this.currentCount;
                if (i == 0) {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(this.paint, AnimationProperties.PAINT_ALPHA, new int[]{0, 255}), ObjectAnimator.ofInt(this.textPaint, AnimationProperties.PAINT_ALPHA, new int[]{0, 255})});
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                } else if (value < i) {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{1.1f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{1.1f, 1.0f})});
                    animatorSet.setInterpolator(new OvershootInterpolator());
                } else {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.9f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.9f, 1.0f})});
                    animatorSet.setInterpolator(new OvershootInterpolator());
                }
            }
            animatorSet.setDuration(180);
            animatorSet.start();
            requestLayout();
            this.currentCount = value;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.max(this.width + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(30.0f)), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), 1073741824));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int cy = getMeasuredHeight() / 2;
            this.paint.setAlpha(255);
            this.rect.set((float) AndroidUtilities.dp(1.0f), (float) (cy - AndroidUtilities.dp(14.0f)), (float) (getMeasuredWidth() - AndroidUtilities.dp(1.0f)), (float) (AndroidUtilities.dp(14.0f) + cy));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), this.paint);
            if (this.staticLayout != null) {
                this.textPaint.setAlpha((int) ((1.0f - this.rotation) * 255.0f));
                canvas.save();
                canvas.translate((float) ((getMeasuredWidth() - this.width) / 2), ((float) ((getMeasuredHeight() - this.height) / 2)) + AndroidUtilities.dpf2(0.2f) + (this.rotation * ((float) AndroidUtilities.dp(5.0f))));
                this.staticLayout.draw(canvas);
                canvas.restore();
                this.paint.setAlpha((int) (this.rotation * 255.0f));
                int cx = (int) this.rect.centerX();
                int cy2 = (int) (((float) ((int) this.rect.centerY())) - ((((float) AndroidUtilities.dp(5.0f)) * (1.0f - this.rotation)) + ((float) AndroidUtilities.dp(3.0f))));
                canvas.drawLine((float) (AndroidUtilities.dp(0.5f) + cx), (float) (cy2 - AndroidUtilities.dp(0.5f)), (float) (cx - AndroidUtilities.dp(6.0f)), (float) (AndroidUtilities.dp(6.0f) + cy2), this.paint);
                canvas.drawLine((float) (cx - AndroidUtilities.dp(0.5f)), (float) (cy2 - AndroidUtilities.dp(0.5f)), (float) (AndroidUtilities.dp(6.0f) + cx), (float) (AndroidUtilities.dp(6.0f) + cy2), this.paint);
            }
        }
    }

    private class PhotoProgressView {
        private float alpha = 1.0f;
        private float animatedAlphaValue = 1.0f;
        private float animatedProgressValue = 0.0f;
        private float animationProgressStart = 0.0f;
        /* access modifiers changed from: private */
        public int backgroundState = -1;
        private float currentProgress = 0.0f;
        private long currentProgressTime = 0;
        private long lastUpdateTime = 0;
        private View parent;
        /* access modifiers changed from: private */
        public int previousBackgroundState = -2;
        private RectF progressRect = new RectF();
        private float radOffset = 0.0f;
        private float scale = 1.0f;
        private int size = AndroidUtilities.dp(64.0f);

        public PhotoProgressView(Context context, View parentView) {
            if (PhotoViewer.decelerateInterpolator == null) {
                DecelerateInterpolator unused = PhotoViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                Paint unused2 = PhotoViewer.progressPaint = new Paint(1);
                PhotoViewer.progressPaint.setStyle(Paint.Style.STROKE);
                PhotoViewer.progressPaint.setStrokeCap(Paint.Cap.ROUND);
                PhotoViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                PhotoViewer.progressPaint.setColor(-1);
            }
            this.parent = parentView;
        }

        private void updateAnimation() {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 18) {
                dt = 18;
            }
            this.lastUpdateTime = newTime;
            if (!(this.animatedProgressValue == 1.0f && this.currentProgress == 1.0f)) {
                this.radOffset += ((float) (360 * dt)) / 3000.0f;
                float progressDiff = this.currentProgress - this.animationProgressStart;
                if (Math.abs(progressDiff) > 0.0f) {
                    long j = this.currentProgressTime + dt;
                    this.currentProgressTime = j;
                    if (j >= 300) {
                        float f = this.currentProgress;
                        this.animatedProgressValue = f;
                        this.animationProgressStart = f;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f) * progressDiff);
                    }
                }
                this.parent.invalidate();
            }
            if (this.animatedProgressValue >= 1.0f && this.previousBackgroundState != -2) {
                float f2 = this.animatedAlphaValue - (((float) dt) / 200.0f);
                this.animatedAlphaValue = f2;
                if (f2 <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousBackgroundState = -2;
                }
                this.parent.invalidate();
            }
        }

        public void setProgress(float value, boolean animated) {
            if (!animated) {
                this.animatedProgressValue = value;
                this.animationProgressStart = value;
            } else {
                this.animationProgressStart = this.animatedProgressValue;
            }
            this.currentProgress = value;
            this.currentProgressTime = 0;
            this.parent.invalidate();
        }

        public void setBackgroundState(int state, boolean animated) {
            int i;
            if (this.backgroundState != state) {
                this.lastUpdateTime = System.currentTimeMillis();
                if (!animated || (i = this.backgroundState) == state) {
                    this.previousBackgroundState = -2;
                } else {
                    this.previousBackgroundState = i;
                    this.animatedAlphaValue = 1.0f;
                }
                this.backgroundState = state;
                this.parent.invalidate();
            }
        }

        public void setAlpha(float value) {
            this.alpha = value;
        }

        public void setScale(float value) {
            this.scale = value;
        }

        public void onDraw(Canvas canvas) {
            int i;
            Drawable drawable;
            Drawable drawable2;
            int sizeScaled = (int) (((float) this.size) * this.scale);
            int x = (PhotoViewer.this.getContainerViewWidth() - sizeScaled) / 2;
            int y = (PhotoViewer.this.getContainerViewHeight() - sizeScaled) / 2;
            int i2 = this.previousBackgroundState;
            if (i2 >= 0 && i2 < 4 && (drawable2 = PhotoViewer.progressDrawables[this.previousBackgroundState]) != null) {
                drawable2.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.alpha));
                drawable2.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                drawable2.draw(canvas);
            }
            int i3 = this.backgroundState;
            if (i3 >= 0 && i3 < 4 && (drawable = PhotoViewer.progressDrawables[this.backgroundState]) != null) {
                if (this.previousBackgroundState != -2) {
                    drawable.setAlpha((int) ((1.0f - this.animatedAlphaValue) * 255.0f * this.alpha));
                } else {
                    drawable.setAlpha((int) (this.alpha * 255.0f));
                }
                drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                drawable.draw(canvas);
            }
            int i4 = this.backgroundState;
            if (i4 == 0 || i4 == 1 || (i = this.previousBackgroundState) == 0 || i == 1) {
                int diff = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    PhotoViewer.progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.alpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                this.progressRect.set((float) (x + diff), (float) (y + diff), (float) ((x + sizeScaled) - diff), (float) ((y + sizeScaled) - diff));
                canvas.drawArc(this.progressRect, -90.0f + this.radOffset, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, PhotoViewer.progressPaint);
                updateAnimation();
            }
        }
    }

    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            return null;
        }

        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            return null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        }

        public void willHidePhotoViewer() {
        }

        public int setPhotoUnchecked(Object photoEntry) {
            return -1;
        }

        public boolean isPhotoChecked(int index) {
            return false;
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            return -1;
        }

        public boolean cancelButtonPressed() {
            return true;
        }

        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate) {
        }

        public int getSelectedCount() {
            return 0;
        }

        public void updatePhotoAtIndex(int index) {
        }

        public boolean allowCaption() {
            return true;
        }

        public boolean scaleToFill() {
            return false;
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return null;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return null;
        }

        public boolean canScrollAway() {
            return true;
        }

        public void needAddMorePhotos() {
        }

        public int getPhotoIndex(int index) {
            return -1;
        }

        public void deleteImageAtIndex(int index) {
        }

        public String getDeleteMessageString() {
            return null;
        }

        public boolean canCaptureMorePhotos() {
            return true;
        }
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        private boolean ignoreLayout;
        private Paint paint = new Paint();

        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
            this.paint.setColor(855638016);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            this.ignoreLayout = true;
            PhotoViewer.this.captionTextView.setMaxLines(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 5 : 10);
            this.ignoreLayout = false;
            measureChildWithMargins(PhotoViewer.this.captionEditText, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int inputFieldHeight = PhotoViewer.this.captionEditText.getMeasuredHeight();
            int widthSize2 = widthSize - (getPaddingRight() + getPaddingLeft());
            int heightSize2 = heightSize - getPaddingBottom();
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (!(child.getVisibility() == 8 || child == PhotoViewer.this.captionEditText)) {
                    if (child == PhotoViewer.this.aspectRatioFrameLayout) {
                        child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0), 1073741824));
                    } else if (!PhotoViewer.this.captionEditText.isPopupView(child)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    } else if (!AndroidUtilities.isInMultiwindow) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, 1073741824), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, 1073741824));
                    } else if (AndroidUtilities.isTablet()) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (heightSize2 - inputFieldHeight) - AndroidUtilities.statusBarHeight), 1073741824));
                    } else {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, 1073741824), View.MeasureSpec.makeMeasureSpec((heightSize2 - inputFieldHeight) - AndroidUtilities.statusBarHeight, 1073741824));
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int _l, int t, int _r, int _b) {
            int paddingBottom;
            int count;
            int b;
            int r;
            int l;
            int childLeft;
            int childTop;
            int count2 = getChildCount();
            int paddingBottom2 = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
            int i = 0;
            while (i < count2) {
                View child = getChildAt(i);
                if (child.getVisibility() == 8) {
                    count = count2;
                    paddingBottom = paddingBottom2;
                } else {
                    if (child == PhotoViewer.this.aspectRatioFrameLayout) {
                        l = _l;
                        r = _r;
                        b = _b;
                    } else {
                        l = _l + getPaddingLeft();
                        r = _r - getPaddingRight();
                        b = _b - getPaddingBottom();
                    }
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    int i2 = gravity & 7 & 7;
                    count = count2;
                    if (i2 == 1) {
                        childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                    } else if (i2 != 5) {
                        childLeft = lp.leftMargin;
                    } else {
                        childLeft = ((r - l) - width) - lp.rightMargin;
                    }
                    if (verticalGravity == 16) {
                        childTop = (((((b - paddingBottom2) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                    } else if (verticalGravity != 80) {
                        childTop = lp.topMargin;
                    } else {
                        childTop = (((b - paddingBottom2) - t) - height) - lp.bottomMargin;
                    }
                    if (child == PhotoViewer.this.mentionListView) {
                        childTop -= PhotoViewer.this.captionEditText.getMeasuredHeight();
                        paddingBottom = paddingBottom2;
                        int i3 = r;
                    } else if (PhotoViewer.this.captionEditText.isPopupView(child)) {
                        if (AndroidUtilities.isInMultiwindow) {
                            childTop = (PhotoViewer.this.captionEditText.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                            paddingBottom = paddingBottom2;
                            int i4 = r;
                        } else {
                            childTop = PhotoViewer.this.captionEditText.getBottom();
                            paddingBottom = paddingBottom2;
                            int i5 = r;
                        }
                    } else if (child == PhotoViewer.this.selectedPhotosListView) {
                        childTop = PhotoViewer.this.actionBar.getMeasuredHeight();
                        paddingBottom = paddingBottom2;
                        int i6 = r;
                    } else {
                        if (child == PhotoViewer.this.captionTextView) {
                            paddingBottom = paddingBottom2;
                            int i7 = r;
                        } else if (child == PhotoViewer.this.switchCaptionTextView) {
                            paddingBottom = paddingBottom2;
                            int i8 = r;
                        } else if (child == PhotoViewer.this.cameraItem) {
                            paddingBottom = paddingBottom2;
                            int i9 = r;
                            childTop = (PhotoViewer.this.pickerView.getTop() - AndroidUtilities.dp((PhotoViewer.this.sendPhotoType == 4 || PhotoViewer.this.sendPhotoType == 5) ? 40.0f : 15.0f)) - PhotoViewer.this.cameraItem.getMeasuredHeight();
                        } else {
                            paddingBottom = paddingBottom2;
                            int i10 = r;
                            if (child == PhotoViewer.this.videoPreviewFrame) {
                                if (!PhotoViewer.this.groupedPhotosListView.currentPhotos.isEmpty()) {
                                    childTop -= PhotoViewer.this.groupedPhotosListView.getMeasuredHeight();
                                }
                                if (PhotoViewer.this.captionTextView.getVisibility() == 0) {
                                    childTop -= PhotoViewer.this.captionTextView.getMeasuredHeight();
                                }
                            }
                        }
                        if (!PhotoViewer.this.groupedPhotosListView.currentPhotos.isEmpty()) {
                            childTop -= PhotoViewer.this.groupedPhotosListView.getMeasuredHeight();
                        }
                    }
                    child.layout(childLeft + l, childTop, childLeft + width + l, childTop + height);
                }
                i++;
                count2 = count;
                paddingBottom2 = paddingBottom;
            }
            notifyHeightChanged();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            PhotoViewer.this.onDraw(canvas);
            if (Build.VERSION.SDK_INT >= 21 && AndroidUtilities.statusBarHeight != 0 && PhotoViewer.this.actionBar != null) {
                this.paint.setAlpha((int) (PhotoViewer.this.actionBar.getAlpha() * 255.0f * 0.2f));
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.statusBarHeight, this.paint);
                this.paint.setAlpha((int) (PhotoViewer.this.actionBar.getAlpha() * 255.0f * 0.498f));
                if (getPaddingRight() > 0) {
                    canvas.drawRect((float) (getMeasuredWidth() - getPaddingRight()), 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
                }
                if (getPaddingLeft() > 0) {
                    canvas.drawRect(0.0f, 0.0f, (float) getPaddingLeft(), (float) getMeasuredHeight(), this.paint);
                }
            }
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == PhotoViewer.this.mentionListView || child == PhotoViewer.this.captionEditText) {
                if (!PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.captionEditText.getEmojiPadding() == 0 && ((AndroidUtilities.usingHardwareInput && PhotoViewer.this.captionEditText.getTag() == null) || getKeyboardHeight() == 0)) {
                    return false;
                }
            } else if (child == PhotoViewer.this.cameraItem || child == PhotoViewer.this.pickerView || child == PhotoViewer.this.pickerViewSendButton || child == PhotoViewer.this.captionTextView || (PhotoViewer.this.muteItem.getVisibility() == 0 && child == PhotoViewer.this.bottomLayout)) {
                int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
                if (PhotoViewer.this.captionEditText.isPopupShowing() || ((AndroidUtilities.usingHardwareInput && PhotoViewer.this.captionEditText.getTag() != null) || getKeyboardHeight() > AndroidUtilities.dp(80.0f) || paddingBottom != 0)) {
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.d("keyboard height = " + getKeyboardHeight() + " padding = " + paddingBottom);
                    }
                    boolean unused = PhotoViewer.this.bottomTouchEnabled = false;
                    return false;
                }
                boolean unused2 = PhotoViewer.this.bottomTouchEnabled = true;
            } else if (child == PhotoViewer.this.checkImageView || child == PhotoViewer.this.photosCounterView) {
                if (PhotoViewer.this.captionEditText.getTag() != null) {
                    boolean unused3 = PhotoViewer.this.bottomTouchEnabled = false;
                    return false;
                }
                boolean unused4 = PhotoViewer.this.bottomTouchEnabled = true;
            } else if (child == PhotoViewer.this.miniProgressView) {
                return false;
            }
            try {
                if (child == PhotoViewer.this.aspectRatioFrameLayout || !super.drawChild(canvas, child, drawingTime)) {
                    return false;
                }
                return true;
            } catch (Throwable th) {
                return true;
            }
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }
    }

    public static PhotoViewer getPipInstance() {
        return PipInstance;
    }

    public static PhotoViewer getInstance() {
        PhotoViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    PhotoViewer photoViewer = new PhotoViewer();
                    localInstance = photoViewer;
                    Instance = photoViewer;
                }
            }
        }
        return localInstance;
    }

    public boolean isOpenedFullScreenVideo() {
        return this.openedFullScreenVideo;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public PhotoViewer() {
        this.blackPaint.setColor(-16777216);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int loadFromMaxId;
        int i;
        boolean z;
        ImageLocation location;
        int setToImage;
        int setToImage2;
        int setToImage3;
        float bufferedProgress;
        float progress;
        MessageObject messageObject;
        TLRPC.BotInlineResult botInlineResult;
        int i2 = id;
        int i3 = 3;
        int i4 = 2;
        int i5 = 1;
        boolean animated = false;
        if (i2 == NotificationCenter.fileDidFailToLoad) {
            String location2 = args[0];
            int a = 0;
            while (a < 3) {
                String[] strArr = this.currentFileNames;
                if (strArr[a] == null || !strArr[a].equals(location2)) {
                    a++;
                } else {
                    if (a == 0 || ((a == 1 && this.sideImage == this.rightImage) || (a == 2 && this.sideImage == this.leftImage))) {
                        animated = true;
                    }
                    this.photoProgressViews[a].setProgress(1.0f, animated);
                    checkProgress(a, true);
                    return;
                }
            }
        } else if (i2 == NotificationCenter.fileDidLoad) {
            String location3 = args[0];
            int a2 = 0;
            while (a2 < 3) {
                String[] strArr2 = this.currentFileNames;
                if (strArr2[a2] == null || !strArr2[a2].equals(location3)) {
                    a2++;
                } else {
                    boolean animated2 = a2 == 0 || (a2 == 1 && this.sideImage == this.rightImage) || (a2 == 2 && this.sideImage == this.leftImage);
                    this.photoProgressViews[a2].setProgress(1.0f, animated2);
                    checkProgress(a2, animated2);
                    if (this.videoPlayer == null && a2 == 0 && (((messageObject = this.currentMessageObject) != null && messageObject.isVideo()) || ((botInlineResult = this.currentBotInlineResult) != null && (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document))))) {
                        onActionClick(false);
                    }
                    if (a2 == 0 && this.videoPlayer != null) {
                        this.currentVideoFinishedLoading = true;
                        return;
                    }
                    return;
                }
            }
        } else {
            long j = 0;
            if (i2 == NotificationCenter.FileLoadProgressChanged) {
                String location4 = args[0];
                int a3 = 0;
                while (a3 < i3) {
                    String[] strArr3 = this.currentFileNames;
                    if (strArr3[a3] != null && strArr3[a3].equals(location4)) {
                        Float loadProgress = args[i5];
                        this.photoProgressViews[a3].setProgress(loadProgress.floatValue(), a3 == 0 || (a3 == i5 && this.sideImage == this.rightImage) || (a3 == i4 && this.sideImage == this.leftImage));
                        if (!(a3 != 0 || this.videoPlayer == null || this.videoPlayerSeekbar == null)) {
                            if (this.currentVideoFinishedLoading) {
                                bufferedProgress = 1.0f;
                            } else {
                                long newTime = SystemClock.elapsedRealtime();
                                if (Math.abs(newTime - this.lastBufferedPositionCheck) >= 500) {
                                    if (this.seekToProgressPending == 0.0f) {
                                        long duration = this.videoPlayer.getDuration();
                                        long position = this.videoPlayer.getCurrentPosition();
                                        if (duration < j || duration == C.TIME_UNSET || position < j) {
                                            progress = 0.0f;
                                        } else {
                                            progress = ((float) position) / ((float) duration);
                                        }
                                    } else {
                                        progress = this.seekToProgressPending;
                                    }
                                    float bufferedProgress2 = this.isStreaming ? FileLoader.getInstance(this.currentAccount).getBufferedProgressFromPosition(progress, this.currentFileNames[0]) : 1.0f;
                                    this.lastBufferedPositionCheck = newTime;
                                    bufferedProgress = bufferedProgress2;
                                } else {
                                    bufferedProgress = -1.0f;
                                }
                            }
                            if (bufferedProgress != -1.0f) {
                                this.videoPlayerSeekbar.setBufferedProgress(bufferedProgress);
                                PipVideoView pipVideoView2 = this.pipVideoView;
                                if (pipVideoView2 != null) {
                                    pipVideoView2.setBufferedProgress(bufferedProgress);
                                }
                                this.videoPlayerControlFrameLayout.invalidate();
                            }
                            checkBufferedProgress(loadProgress.floatValue());
                        }
                    }
                    a3++;
                    i3 = 3;
                    i4 = 2;
                    i5 = 1;
                    j = 0;
                }
                return;
            }
            int i6 = -1;
            if (i2 == NotificationCenter.dialogPhotosLoaded) {
                int guid = args[3].intValue();
                if (this.avatarsDialogId == args[0].intValue() && this.classGuid == guid) {
                    boolean fromCache = args[2].booleanValue();
                    int setToImage4 = -1;
                    ArrayList<TLRPC.Photo> photos = args[4];
                    if (!photos.isEmpty()) {
                        this.imagesArrLocations.clear();
                        this.imagesArrLocationsSizes.clear();
                        this.avatarsArr.clear();
                        int a4 = 0;
                        while (a4 < photos.size()) {
                            TLRPC.Photo photo = photos.get(a4);
                            if (photo == null || (photo instanceof TLRPC.TL_photoEmpty)) {
                                setToImage = setToImage4;
                            } else if (photo.sizes == null) {
                                setToImage = setToImage4;
                            } else {
                                TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, UVCCamera.DEFAULT_PREVIEW_WIDTH);
                                if (sizeFull != null) {
                                    if (setToImage4 != i6 || this.currentFileLocation == null) {
                                        setToImage2 = setToImage4;
                                    } else {
                                        int b = 0;
                                        while (b < photo.sizes.size()) {
                                            TLRPC.PhotoSize size = photo.sizes.get(b);
                                            if (size.location.local_id == this.currentFileLocation.location.local_id) {
                                                setToImage3 = setToImage4;
                                                if (size.location.volume_id == this.currentFileLocation.location.volume_id) {
                                                    setToImage4 = this.imagesArrLocations.size();
                                                    break;
                                                }
                                            } else {
                                                setToImage3 = setToImage4;
                                            }
                                            b++;
                                            setToImage4 = setToImage3;
                                        }
                                        setToImage2 = setToImage4;
                                    }
                                    setToImage4 = setToImage2;
                                    if (photo.dc_id != 0) {
                                        sizeFull.location.dc_id = photo.dc_id;
                                        sizeFull.location.file_reference = photo.file_reference;
                                    }
                                    ImageLocation location5 = ImageLocation.getForPhoto(sizeFull, photo);
                                    if (location5 != null) {
                                        this.imagesArrLocations.add(location5);
                                        this.imagesArrLocationsSizes.add(Integer.valueOf(sizeFull.size));
                                        this.avatarsArr.add(photo);
                                    }
                                    a4++;
                                    i6 = -1;
                                } else {
                                    setToImage = setToImage4;
                                }
                            }
                            setToImage4 = setToImage;
                            a4++;
                            i6 = -1;
                        }
                        int setToImage5 = setToImage4;
                        if (!this.avatarsArr.isEmpty()) {
                            this.menuItem.showSubItem(6);
                        } else {
                            this.menuItem.hideSubItem(6);
                        }
                        this.needSearchImageInArr = false;
                        this.currentIndex = -1;
                        int setToImage6 = setToImage5;
                        if (setToImage6 != -1) {
                            setImageIndex(setToImage6, true);
                        } else {
                            TLRPC.User user = null;
                            TLRPC.Chat chat = null;
                            if (this.avatarsDialogId > 0) {
                                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.avatarsDialogId));
                            } else {
                                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-this.avatarsDialogId));
                            }
                            if (!(user == null && chat == null)) {
                                if (user != null) {
                                    location = ImageLocation.getForUser(user, true);
                                } else {
                                    location = ImageLocation.getForChat(chat, true);
                                }
                                if (location != null) {
                                    this.imagesArrLocations.add(0, location);
                                    this.avatarsArr.add(0, new TLRPC.TL_photoEmpty());
                                    this.imagesArrLocationsSizes.add(0, 0);
                                    setImageIndex(0, true);
                                }
                            }
                        }
                        if (fromCache) {
                            MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.avatarsDialogId, 80, 0, false, this.classGuid);
                        }
                    }
                }
            } else if (i2 == NotificationCenter.mediaCountDidLoad) {
                long uid = args[0].longValue();
                if (uid == this.currentDialogId || uid == this.mergeDialogId) {
                    if (uid == this.currentDialogId) {
                        this.totalImagesCount = args[1].intValue();
                    } else if (uid == this.mergeDialogId) {
                        this.totalImagesCountMerge = args[1].intValue();
                    }
                    if (this.needSearchImageInArr && this.isFirstLoading) {
                        this.isFirstLoading = false;
                        this.loadingMoreImages = true;
                        MediaDataController.getInstance(this.currentAccount).loadMedia(this.currentDialogId, 80, 0, this.sharedMediaType, 1, this.classGuid);
                    } else if (this.imagesArr.isEmpty()) {
                    } else {
                        if (this.opennedFromMedia) {
                            this.totalImagesCount = this.imagesArr.size();
                            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                            return;
                        }
                        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(((this.totalImagesCount + this.totalImagesCountMerge) - this.imagesArr.size()) + this.currentIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                    }
                }
            } else if (i2 == NotificationCenter.mediaDidLoad) {
                long uid2 = args[0].longValue();
                int guid2 = args[3].intValue();
                if ((uid2 == this.currentDialogId || uid2 == this.mergeDialogId) && guid2 == this.classGuid) {
                    this.loadingMoreImages = false;
                    int loadIndex = uid2 == this.currentDialogId ? 0 : 1;
                    ArrayList<MessageObject> arr = args[2];
                    this.endReached[loadIndex] = args[5].booleanValue();
                    if (!this.needSearchImageInArr) {
                        int added = 0;
                        Iterator<MessageObject> it = arr.iterator();
                        while (it.hasNext()) {
                            MessageObject message = it.next();
                            if (this.imagesByIds[loadIndex].indexOfKey(message.getId()) < 0) {
                                added++;
                                if (this.opennedFromMedia) {
                                    this.imagesArr.add(message);
                                } else {
                                    this.imagesArr.add(0, message);
                                }
                                this.imagesByIds[loadIndex].put(message.getId(), message);
                            }
                        }
                        if (this.opennedFromMedia) {
                            if (added == 0) {
                                this.totalImagesCount = this.imagesArr.size();
                                this.totalImagesCountMerge = 0;
                            }
                        } else if (added != 0) {
                            int index = this.currentIndex;
                            this.currentIndex = -1;
                            setImageIndex(index + added, true);
                        } else {
                            this.totalImagesCount = this.imagesArr.size();
                            this.totalImagesCountMerge = 0;
                        }
                    } else if (!arr.isEmpty() || (loadIndex == 0 && this.mergeDialogId != 0)) {
                        int foundIndex = -1;
                        MessageObject currentMessage = this.imagesArr.get(this.currentIndex);
                        int added2 = 0;
                        for (int a5 = 0; a5 < arr.size(); a5++) {
                            MessageObject message2 = arr.get(a5);
                            if (this.imagesByIdsTemp[loadIndex].indexOfKey(message2.getId()) < 0) {
                                this.imagesByIdsTemp[loadIndex].put(message2.getId(), message2);
                                if (this.opennedFromMedia) {
                                    this.imagesArrTemp.add(message2);
                                    if (message2.getId() == currentMessage.getId()) {
                                        foundIndex = added2;
                                    }
                                    added2++;
                                } else {
                                    added2++;
                                    this.imagesArrTemp.add(0, message2);
                                    if (message2.getId() == currentMessage.getId()) {
                                        foundIndex = arr.size() - added2;
                                    }
                                }
                            }
                        }
                        if (added2 != 0 && (loadIndex != 0 || this.mergeDialogId == 0)) {
                            this.totalImagesCount = this.imagesArr.size();
                            this.totalImagesCountMerge = 0;
                        }
                        if (foundIndex != -1) {
                            this.imagesArr.clear();
                            this.imagesArr.addAll(this.imagesArrTemp);
                            for (int a6 = 0; a6 < 2; a6++) {
                                this.imagesByIds[a6] = this.imagesByIdsTemp[a6].clone();
                                this.imagesByIdsTemp[a6].clear();
                            }
                            this.imagesArrTemp.clear();
                            this.needSearchImageInArr = false;
                            this.currentIndex = -1;
                            if (foundIndex >= this.imagesArr.size()) {
                                z = true;
                                foundIndex = this.imagesArr.size() - 1;
                            } else {
                                z = true;
                            }
                            setImageIndex(foundIndex, z);
                            return;
                        }
                        if (this.opennedFromMedia) {
                            if (this.imagesArrTemp.isEmpty()) {
                                i = 0;
                            } else {
                                ArrayList<MessageObject> arrayList = this.imagesArrTemp;
                                i = arrayList.get(arrayList.size() - 1).getId();
                            }
                            loadFromMaxId = i;
                            if (loadIndex == 0 && this.endReached[loadIndex] && this.mergeDialogId != 0) {
                                loadIndex = 1;
                                if (!this.imagesArrTemp.isEmpty()) {
                                    ArrayList<MessageObject> arrayList2 = this.imagesArrTemp;
                                    if (arrayList2.get(arrayList2.size() - 1).getDialogId() != this.mergeDialogId) {
                                        loadFromMaxId = 0;
                                    }
                                }
                            }
                        } else {
                            loadFromMaxId = this.imagesArrTemp.isEmpty() ? 0 : this.imagesArrTemp.get(0).getId();
                            if (loadIndex == 0 && this.endReached[loadIndex] && this.mergeDialogId != 0) {
                                loadIndex = 1;
                                if (!this.imagesArrTemp.isEmpty() && this.imagesArrTemp.get(0).getDialogId() != this.mergeDialogId) {
                                    loadFromMaxId = 0;
                                }
                            }
                        }
                        if (!this.endReached[loadIndex]) {
                            this.loadingMoreImages = true;
                            if (this.opennedFromMedia) {
                                MediaDataController.getInstance(this.currentAccount).loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, 80, loadFromMaxId, this.sharedMediaType, 1, this.classGuid);
                            } else {
                                MediaDataController.getInstance(this.currentAccount).loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, 80, loadFromMaxId, this.sharedMediaType, 1, this.classGuid);
                            }
                        }
                    } else {
                        this.needSearchImageInArr = false;
                    }
                }
            } else if (i2 == NotificationCenter.emojiDidLoad) {
                TextView textView = this.captionTextView;
                if (textView != null) {
                    textView.invalidate();
                }
            } else if (i2 == NotificationCenter.filePreparingFailed) {
                MessageObject messageObject2 = args[0];
                if (this.loadInitialVideo) {
                    this.loadInitialVideo = false;
                    this.progressView.setVisibility(4);
                    preparePlayer(this.currentPlayingVideoFile, false, false);
                } else if (this.tryStartRequestPreviewOnFinish) {
                    releasePlayer(false);
                    this.tryStartRequestPreviewOnFinish = !MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true);
                } else if (messageObject2 == this.videoPreviewMessageObject) {
                    this.requestingPreview = false;
                    this.progressView.setVisibility(4);
                }
            } else if (i2 == NotificationCenter.fileNewChunkAvailable) {
                if (args[0] == this.videoPreviewMessageObject) {
                    String finalPath = args[1];
                    if (args[3].longValue() != 0) {
                        this.requestingPreview = false;
                        this.photoProgressViews[0].setProgress(1.0f, true);
                        this.photoProgressViews[0].setBackgroundState(3, true);
                        preparePlayer(Uri.fromFile(new File(finalPath)), false, true);
                    }
                }
            } else if (i2 != NotificationCenter.saveGallerySetChanged) {
            } else {
                if (args[0].booleanValue()) {
                    this.menuItem.showSubItem(1);
                    return;
                }
                this.menuItem.hideSubItem(1);
                this.menuItem.invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    public void showDownloadAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        MessageObject messageObject = this.currentMessageObject;
        boolean alreadyDownloading = false;
        if (messageObject != null && messageObject.isVideo() && FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingFile(this.currentFileNames[0])) {
            alreadyDownloading = true;
        }
        if (alreadyDownloading) {
            builder.setMessage(LocaleController.getString("PleaseStreamDownload", R.string.PleaseStreamDownload));
        } else {
            builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
        }
        showAlertDialog(builder);
    }

    /* access modifiers changed from: private */
    public void onSharePressed() {
        boolean z;
        if (this.parentActivity != null && this.allowShare) {
            File f = null;
            boolean isVideo = false;
            try {
                if (this.currentMessageObject != null) {
                    isVideo = this.currentMessageObject.isVideo();
                    if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                        f = new File(this.currentMessageObject.messageOwner.attachPath);
                        if (!f.exists()) {
                            f = null;
                        }
                    }
                    if (f == null) {
                        f = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
                    }
                } else if (this.currentFileLocation != null) {
                    TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = this.currentFileLocation.location;
                    if (this.avatarsDialogId == 0) {
                        if (!this.isEvent) {
                            z = false;
                            f = FileLoader.getPathToAttach(tL_fileLocationToBeDeprecated, z);
                        }
                    }
                    z = true;
                    f = FileLoader.getPathToAttach(tL_fileLocationToBeDeprecated, z);
                }
                if (f.exists()) {
                    Intent intent = new Intent("android.intent.action.SEND");
                    if (isVideo) {
                        intent.setType(MimeTypes.VIDEO_MP4);
                    } else if (this.currentMessageObject != null) {
                        intent.setType(this.currentMessageObject.getMimeType());
                    } else {
                        intent.setType("image/jpeg");
                    }
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this.parentActivity, "im.bclpbkiauv.messenger.provider", f));
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
                showDownloadAlert();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setScaleToFill() {
        float bitmapWidth = (float) this.centerImage.getBitmapWidth();
        float containerWidth = (float) getContainerViewWidth();
        float bitmapHeight = (float) this.centerImage.getBitmapHeight();
        float containerHeight = (float) getContainerViewHeight();
        float scaleFit = Math.min(containerHeight / bitmapHeight, containerWidth / bitmapWidth);
        float max = Math.max(containerWidth / ((float) ((int) (bitmapWidth * scaleFit))), containerHeight / ((float) ((int) (bitmapHeight * scaleFit))));
        this.scale = max;
        updateMinMax(max);
    }

    public void setParentAlert(ChatAttachAlert alert) {
        this.parentAlert = alert;
    }

    public void setParentActivity(Activity activity) {
        Activity activity2 = activity;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.centerImage.setCurrentAccount(i);
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        if (this.parentActivity != activity2 && activity2 != null) {
            this.parentActivity = activity2;
            this.actvityContext = new ContextThemeWrapper(this.parentActivity, R.style.Theme_TMessages);
            if (progressDrawables == null) {
                Drawable[] drawableArr = new Drawable[4];
                progressDrawables = drawableArr;
                drawableArr[0] = this.parentActivity.getResources().getDrawable(R.drawable.circle_big);
                progressDrawables[1] = this.parentActivity.getResources().getDrawable(R.drawable.cancel_big);
                progressDrawables[2] = this.parentActivity.getResources().getDrawable(R.drawable.load_big);
                progressDrawables[3] = this.parentActivity.getResources().getDrawable(R.drawable.play_big);
            }
            this.scroller = new Scroller(activity2);
            AnonymousClass5 r2 = new FrameLayout(activity2) {
                private Runnable attachRunnable;

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.isVisible && super.onInterceptTouchEvent(ev);
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.isVisible && PhotoViewer.this.onTouchEvent(event);
                }

                /* access modifiers changed from: protected */
                public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    boolean result;
                    try {
                        result = super.drawChild(canvas, child, drawingTime);
                    } catch (Throwable th) {
                        result = false;
                    }
                    if (Build.VERSION.SDK_INT >= 21 && child == PhotoViewer.this.animatingImageView && PhotoViewer.this.lastInsets != null) {
                        canvas.drawRect(0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth(), (float) (getMeasuredHeight() + ((WindowInsets) PhotoViewer.this.lastInsets).getSystemWindowInsetBottom()), PhotoViewer.this.blackPaint);
                    }
                    return result;
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                    int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                    if (Build.VERSION.SDK_INT >= 21 && PhotoViewer.this.lastInsets != null) {
                        WindowInsets insets = (WindowInsets) PhotoViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            if (heightSize > AndroidUtilities.displaySize.y) {
                                heightSize = AndroidUtilities.displaySize.y;
                            }
                            heightSize += AndroidUtilities.statusBarHeight;
                        }
                        heightSize -= insets.getSystemWindowInsetBottom();
                    } else if (heightSize > AndroidUtilities.displaySize.y) {
                        heightSize = AndroidUtilities.displaySize.y;
                    }
                    setMeasuredDimension(widthSize, heightSize);
                    ViewGroup.LayoutParams layoutParams = PhotoViewer.this.animatingImageView.getLayoutParams();
                    PhotoViewer.this.animatingImageView.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
                    PhotoViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(heightSize, 1073741824));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    PhotoViewer.this.animatingImageView.layout(0, 0, PhotoViewer.this.animatingImageView.getMeasuredWidth() + 0, PhotoViewer.this.animatingImageView.getMeasuredHeight());
                    PhotoViewer.this.containerView.layout(0, 0, PhotoViewer.this.containerView.getMeasuredWidth() + 0, PhotoViewer.this.containerView.getMeasuredHeight());
                    boolean unused = PhotoViewer.this.wasLayout = true;
                    if (changed) {
                        if (!PhotoViewer.this.dontResetZoomOnFirstLayout) {
                            float unused2 = PhotoViewer.this.scale = 1.0f;
                            float unused3 = PhotoViewer.this.translationX = 0.0f;
                            float unused4 = PhotoViewer.this.translationY = 0.0f;
                            PhotoViewer photoViewer = PhotoViewer.this;
                            photoViewer.updateMinMax(photoViewer.scale);
                        }
                        if (PhotoViewer.this.checkImageView != null) {
                            PhotoViewer.this.checkImageView.post(new Runnable() {
                                public final void run() {
                                    PhotoViewer.AnonymousClass5.this.lambda$onLayout$0$PhotoViewer$5();
                                }
                            });
                        }
                    }
                    if (PhotoViewer.this.dontResetZoomOnFirstLayout) {
                        PhotoViewer.this.setScaleToFill();
                        boolean unused5 = PhotoViewer.this.dontResetZoomOnFirstLayout = false;
                    }
                }

                public /* synthetic */ void lambda$onLayout$0$PhotoViewer$5() {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
                    int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                    int i = 0;
                    layoutParams.topMargin = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0f)) / 2) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                    PhotoViewer.this.checkImageView.setLayoutParams(layoutParams);
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) PhotoViewer.this.photosCounterView.getLayoutParams();
                    int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0f)) / 2;
                    if (Build.VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    layoutParams2.topMargin = currentActionBarHeight + i;
                    PhotoViewer.this.photosCounterView.setLayoutParams(layoutParams2);
                }

                /* access modifiers changed from: protected */
                public void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    boolean unused = PhotoViewer.this.attachedToWindow = true;
                }

                /* access modifiers changed from: protected */
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    boolean unused = PhotoViewer.this.attachedToWindow = false;
                    boolean unused2 = PhotoViewer.this.wasLayout = false;
                }

                public boolean dispatchKeyEventPreIme(KeyEvent event) {
                    if (event == null || event.getKeyCode() != 4 || event.getAction() != 1) {
                        return super.dispatchKeyEventPreIme(event);
                    }
                    if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                        PhotoViewer.this.closeCaptionEnter(false);
                        return false;
                    }
                    PhotoViewer.getInstance().closePhoto(true, false);
                    return true;
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (Build.VERSION.SDK_INT >= 21 && PhotoViewer.this.isVisible && PhotoViewer.this.lastInsets != null) {
                        WindowInsets insets = (WindowInsets) PhotoViewer.this.lastInsets;
                        if (PhotoViewer.this.animationInProgress == 1) {
                            PhotoViewer.this.blackPaint.setAlpha((int) (PhotoViewer.this.animatingImageView.getAnimationProgress() * 255.0f));
                        } else if (PhotoViewer.this.animationInProgress == 3) {
                            PhotoViewer.this.blackPaint.setAlpha((int) ((1.0f - PhotoViewer.this.animatingImageView.getAnimationProgress()) * 255.0f));
                        } else {
                            PhotoViewer.this.blackPaint.setAlpha(255);
                        }
                        canvas.drawRect(0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth(), (float) (getMeasuredHeight() + insets.getSystemWindowInsetBottom()), PhotoViewer.this.blackPaint);
                    }
                }

                public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback, int type) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        View view = PhotoViewer.this.parentActivity.findViewById(16908290);
                        if (view instanceof ViewGroup) {
                            try {
                                return ((ViewGroup) view).startActionModeForChild(originalView, callback, type);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    }
                    return super.startActionModeForChild(originalView, callback, type);
                }
            };
            this.windowView = r2;
            r2.setBackgroundDrawable(this.backgroundDrawable);
            this.windowView.setClipChildren(true);
            this.windowView.setFocusable(false);
            ClippingImageView clippingImageView = new ClippingImageView(activity2);
            this.animatingImageView = clippingImageView;
            clippingImageView.setAnimationValues(this.animationValues);
            this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
            FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(activity2);
            this.containerView = frameLayoutDrawer;
            frameLayoutDrawer.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            if (Build.VERSION.SDK_INT >= 21) {
                this.containerView.setFitsSystemWindows(true);
                this.containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        return PhotoViewer.this.lambda$setParentActivity$1$PhotoViewer(view, windowInsets);
                    }
                });
                this.containerView.setSystemUiVisibility(1792);
            }
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            if (Build.VERSION.SDK_INT >= 28) {
                this.windowLayoutParams.layoutInDisplayCutoutMode = 1;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -2147286784;
            } else {
                this.windowLayoutParams.flags = 131072;
            }
            AnonymousClass6 r22 = new ActionBar(activity2) {
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    PhotoViewer.this.containerView.invalidate();
                }
            };
            this.actionBar = r22;
            r22.setTitleColor(-1);
            this.actionBar.setSubtitleColor(-1);
            this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21);
            this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
            this.actionBar.setBackButtonImage(R.mipmap.ic_back);
            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, 1, 1));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    TLRPC.Chat currentChat;
                    TLRPC.User currentUser;
                    int revokeTimeLimit;
                    int i = id;
                    int i2 = 1;
                    if (i == -1) {
                        if (!PhotoViewer.this.needCaptionLayout || (!PhotoViewer.this.captionEditText.isPopupShowing() && !PhotoViewer.this.captionEditText.isKeyboardVisible())) {
                            PhotoViewer.this.closePhoto(true, false);
                        } else {
                            PhotoViewer.this.closeCaptionEnter(false);
                        }
                    } else if (i == 1) {
                        if (Build.VERSION.SDK_INT < 23 || PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                            File f = null;
                            if (PhotoViewer.this.currentMessageObject != null) {
                                if (!(PhotoViewer.this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || PhotoViewer.this.currentMessageObject.messageOwner.media.webpage == null || PhotoViewer.this.currentMessageObject.messageOwner.media.webpage.document != null) {
                                    f = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                                } else {
                                    PhotoViewer photoViewer = PhotoViewer.this;
                                    f = FileLoader.getPathToAttach(photoViewer.getFileLocation(photoViewer.currentIndex, (int[]) null), true);
                                }
                            } else if (PhotoViewer.this.currentFileLocation != null) {
                                f = FileLoader.getPathToAttach(PhotoViewer.this.currentFileLocation.location, PhotoViewer.this.avatarsDialogId != 0 || PhotoViewer.this.isEvent);
                            }
                            if (f == null || !f.exists()) {
                                PhotoViewer.this.showDownloadAlert();
                                return;
                            }
                            String file = f.toString();
                            Activity access$2800 = PhotoViewer.this.parentActivity;
                            if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isVideo()) {
                                i2 = 0;
                            }
                            MediaController.saveFile(file, access$2800, i2, (String) null, (String) null);
                            return;
                        }
                        PhotoViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    } else if (i == 2) {
                        if (PhotoViewer.this.currentDialogId != 0) {
                            boolean unused = PhotoViewer.this.disableShowCheck = true;
                            Bundle args2 = new Bundle();
                            args2.putLong("dialog_id", PhotoViewer.this.currentDialogId);
                            MediaActivity mediaActivity = new MediaActivity(args2, new int[]{-1, -1, -1, -1, -1}, (MediaActivity.SharedMediaData[]) null, PhotoViewer.this.sharedMediaType);
                            if (PhotoViewer.this.parentChatActivity != null) {
                                mediaActivity.setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
                            }
                            PhotoViewer.this.closePhoto(false, false);
                            ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(mediaActivity, false, true);
                        }
                    } else if (i == 4) {
                        if (PhotoViewer.this.currentMessageObject != null) {
                            Bundle args = new Bundle();
                            int lower_part = (int) PhotoViewer.this.currentDialogId;
                            int high_id = (int) (PhotoViewer.this.currentDialogId >> 32);
                            if (lower_part == 0) {
                                args.putInt("enc_id", high_id);
                            } else if (lower_part > 0) {
                                args.putInt("user_id", lower_part);
                            } else if (lower_part < 0) {
                                TLRPC.Chat chat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-lower_part));
                                if (!(chat == null || chat.migrated_to == null)) {
                                    args.putInt("migrated_to", lower_part);
                                    lower_part = -chat.migrated_to.channel_id;
                                }
                                args.putInt("chat_id", -lower_part);
                            }
                            args.putInt("message_id", PhotoViewer.this.currentMessageObject.getId());
                            NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            LaunchActivity launchActivity = (LaunchActivity) PhotoViewer.this.parentActivity;
                            launchActivity.presentFragment(new ChatActivity(args), launchActivity.getMainFragmentsCount() > 1 || AndroidUtilities.isTablet(), true);
                            MessageObject unused2 = PhotoViewer.this.currentMessageObject = null;
                            PhotoViewer.this.closePhoto(false, false);
                        }
                    } else if (i == 3) {
                        if (PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.parentActivity != null) {
                            ((LaunchActivity) PhotoViewer.this.parentActivity).switchToAccount(PhotoViewer.this.currentMessageObject.currentAccount, true);
                            Bundle args3 = new Bundle();
                            args3.putBoolean("onlySelect", true);
                            args3.putInt("dialogsType", 3);
                            DialogsActivity fragment = new DialogsActivity(args3);
                            ArrayList<MessageObject> fmessages = new ArrayList<>();
                            fmessages.add(PhotoViewer.this.currentMessageObject);
                            fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate(fmessages) {
                                private final /* synthetic */ ArrayList f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                                    PhotoViewer.AnonymousClass7.this.lambda$onItemClick$0$PhotoViewer$7(this.f$1, dialogsActivity, arrayList, charSequence, z);
                                }
                            });
                            ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(fragment, false, true);
                            PhotoViewer.this.closePhoto(false, false);
                        }
                    } else if (i == 6) {
                        if (PhotoViewer.this.parentActivity != null && PhotoViewer.this.placeProvider != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) PhotoViewer.this.parentActivity);
                            String text = PhotoViewer.this.placeProvider.getDeleteMessageString();
                            if (text != null) {
                                builder.setMessage(text);
                            } else if (PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.currentMessageObject.isVideo()) {
                                builder.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", R.string.AreYouSureDeleteVideo, new Object[0]));
                            } else if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isGif()) {
                                builder.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", R.string.AreYouSureDeletePhoto, new Object[0]));
                            } else {
                                builder.setMessage(LocaleController.formatString("AreYouSureDeleteGIF", R.string.AreYouSureDeleteGIF, new Object[0]));
                            }
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            boolean[] deleteForAll = new boolean[1];
                            if (PhotoViewer.this.currentMessageObject == null || PhotoViewer.this.currentMessageObject.scheduled) {
                            } else {
                                int lower_id = (int) PhotoViewer.this.currentMessageObject.getDialogId();
                                if (lower_id != 0) {
                                    if (lower_id > 0) {
                                        currentUser = MessagesController.getInstance(PhotoViewer.this.currentAccount).getUser(Integer.valueOf(lower_id));
                                        currentChat = null;
                                    } else {
                                        currentUser = null;
                                        currentChat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                                    }
                                    if (currentUser != null || !ChatObject.isChannel(currentChat)) {
                                        int currentDate = ConnectionsManager.getInstance(PhotoViewer.this.currentAccount).getCurrentTime();
                                        if (currentUser != null) {
                                            revokeTimeLimit = MessagesController.getInstance(PhotoViewer.this.currentAccount).revokeTimePmLimit;
                                        } else {
                                            revokeTimeLimit = MessagesController.getInstance(PhotoViewer.this.currentAccount).revokeTimeLimit;
                                        }
                                        if ((currentUser == null || currentUser.id == UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId()) && currentChat == null) {
                                            TLRPC.User user = currentUser;
                                        } else if (PhotoViewer.this.currentMessageObject.messageOwner.action != null && !(PhotoViewer.this.currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)) {
                                            String str = text;
                                        } else if (!PhotoViewer.this.currentMessageObject.isOut() || currentDate - PhotoViewer.this.currentMessageObject.messageOwner.date > revokeTimeLimit) {
                                            TLRPC.User user2 = currentUser;
                                        } else {
                                            FrameLayout frameLayout = new FrameLayout(PhotoViewer.this.parentActivity);
                                            CheckBoxCell cell = new CheckBoxCell(PhotoViewer.this.parentActivity, 1);
                                            cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                            if (currentChat != null) {
                                                String str2 = text;
                                                cell.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), "", false, false);
                                                TLRPC.User user3 = currentUser;
                                            } else {
                                                TLRPC.User user4 = currentUser;
                                                cell.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(currentUser)), "", false, false);
                                            }
                                            cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                                            frameLayout.addView(cell, LayoutHelper.createFrame(-1.0f, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                            cell.setOnClickListener(new View.OnClickListener(deleteForAll) {
                                                private final /* synthetic */ boolean[] f$0;

                                                {
                                                    this.f$0 = r1;
                                                }

                                                public final void onClick(View view) {
                                                    PhotoViewer.AnonymousClass7.lambda$onItemClick$1(this.f$0, view);
                                                }
                                            });
                                            builder.setView(frameLayout);
                                        }
                                    } else {
                                        String str3 = text;
                                    }
                                }
                            }
                            builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener(deleteForAll) {
                                private final /* synthetic */ boolean[] f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    PhotoViewer.AnonymousClass7.this.lambda$onItemClick$2$PhotoViewer$7(this.f$1, dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                            AlertDialog alertDialog = builder.create();
                            PhotoViewer.this.showAlertDialog(builder);
                            View button = alertDialog.getButton(-1);
                            if (button instanceof TextView) {
                                ((TextView) button).setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                            }
                        }
                    } else if (i == 10) {
                        PhotoViewer.this.onSharePressed();
                    } else if (i == 11) {
                        try {
                            AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                            PhotoViewer.this.closePhoto(false, false);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else if (i == 13) {
                        if (PhotoViewer.this.parentActivity != null && PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.currentMessageObject.messageOwner.media != null && PhotoViewer.this.currentMessageObject.messageOwner.media.photo != null) {
                            new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject, PhotoViewer.this.currentMessageObject.messageOwner.media.photo).show();
                        }
                    } else if (i == 5) {
                        if (PhotoViewer.this.pipItem.getAlpha() == 1.0f) {
                            PhotoViewer.this.switchToPip();
                        }
                    } else if (i == 7 && PhotoViewer.this.currentMessageObject != null) {
                        FileLoader.getInstance(PhotoViewer.this.currentAccount).cancelLoadFile(PhotoViewer.this.currentMessageObject.getDocument());
                        PhotoViewer.this.releasePlayer(false);
                        PhotoViewer.this.bottomLayout.setTag(1);
                        PhotoViewer.this.bottomLayout.setVisibility(0);
                    }
                }

                public /* synthetic */ void lambda$onItemClick$0$PhotoViewer$7(ArrayList fmessages, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
                    ArrayList arrayList = dids;
                    if (dids.size() > 1 || ((Long) arrayList.get(0)).longValue() == ((long) UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId())) {
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
                        NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        ChatActivity chatActivity = new ChatActivity(args1);
                        if (((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(chatActivity, true, false)) {
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
                            SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(message.toString(), did2, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                        }
                        SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(fmessages, did2, true, 0);
                    }
                    fragment1.finishFragment();
                }

                static /* synthetic */ void lambda$onItemClick$1(boolean[] deleteForAll, View v) {
                    deleteForAll[0] = !deleteForAll[0];
                    ((CheckBoxCell) v).setChecked(deleteForAll[0], true);
                }

                public /* synthetic */ void lambda$onItemClick$2$PhotoViewer$7(boolean[] deleteForAll, DialogInterface dialogInterface, int i) {
                    TLRPC.EncryptedChat encryptedChat;
                    ArrayList<Long> random_ids;
                    if (!PhotoViewer.this.imagesArr.isEmpty()) {
                        if (PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                            MessageObject obj = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                            if (obj.isSent()) {
                                PhotoViewer.this.closePhoto(false, false);
                                ArrayList<Integer> arr = new ArrayList<>();
                                if (PhotoViewer.this.slideshowMessageId != 0) {
                                    arr.add(Integer.valueOf(PhotoViewer.this.slideshowMessageId));
                                } else {
                                    arr.add(Integer.valueOf(obj.getId()));
                                }
                                if (((int) obj.getDialogId()) != 0 || obj.messageOwner.random_id == 0) {
                                    random_ids = null;
                                    encryptedChat = null;
                                } else {
                                    ArrayList<Long> random_ids2 = new ArrayList<>();
                                    random_ids2.add(Long.valueOf(obj.messageOwner.random_id));
                                    random_ids = random_ids2;
                                    encryptedChat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (obj.getDialogId() >> 32)));
                                }
                                MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteMessages(arr, random_ids, encryptedChat, obj.getDialogId(), obj.messageOwner.to_id.channel_id, deleteForAll[0], obj.scheduled);
                            }
                        }
                    } else if (!PhotoViewer.this.avatarsArr.isEmpty()) {
                        if (PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.avatarsArr.size()) {
                            TLRPC.Photo photo = (TLRPC.Photo) PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                            ImageLocation currentLocation = (ImageLocation) PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
                            if (photo instanceof TLRPC.TL_photoEmpty) {
                                photo = null;
                            }
                            boolean current = false;
                            if (PhotoViewer.this.currentUserAvatarLocation != null) {
                                if (photo != null) {
                                    Iterator<TLRPC.PhotoSize> it = photo.sizes.iterator();
                                    while (true) {
                                        if (!it.hasNext()) {
                                            break;
                                        }
                                        TLRPC.PhotoSize size = it.next();
                                        if (size.location.local_id == PhotoViewer.this.currentUserAvatarLocation.location.local_id && size.location.volume_id == PhotoViewer.this.currentUserAvatarLocation.location.volume_id) {
                                            current = true;
                                            break;
                                        }
                                    }
                                } else if (currentLocation.location.local_id == PhotoViewer.this.currentUserAvatarLocation.location.local_id && currentLocation.location.volume_id == PhotoViewer.this.currentUserAvatarLocation.location.volume_id) {
                                    current = true;
                                }
                            }
                            if (current) {
                                MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto((TLRPC.InputPhoto) null);
                                PhotoViewer.this.closePhoto(false, false);
                            } else if (photo != null) {
                                TLRPC.TL_inputPhoto inputPhoto = new TLRPC.TL_inputPhoto();
                                inputPhoto.id = photo.id;
                                inputPhoto.access_hash = photo.access_hash;
                                inputPhoto.file_reference = photo.file_reference;
                                if (inputPhoto.file_reference == null) {
                                    inputPhoto.file_reference = new byte[0];
                                }
                                MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(inputPhoto);
                                MessagesStorage.getInstance(PhotoViewer.this.currentAccount).clearUserPhoto(PhotoViewer.this.avatarsDialogId, photo.id);
                                PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                                PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                                PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                                if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                                    PhotoViewer.this.closePhoto(false, false);
                                    return;
                                }
                                int index = PhotoViewer.this.currentIndex;
                                if (index >= PhotoViewer.this.avatarsArr.size()) {
                                    index = PhotoViewer.this.avatarsArr.size() - 1;
                                }
                                int unused = PhotoViewer.this.currentIndex = -1;
                                PhotoViewer.this.setImageIndex(index, true);
                            }
                        }
                    } else if (!PhotoViewer.this.secureDocuments.isEmpty() && PhotoViewer.this.placeProvider != null) {
                        PhotoViewer.this.secureDocuments.remove(PhotoViewer.this.currentIndex);
                        PhotoViewer.this.placeProvider.deleteImageAtIndex(PhotoViewer.this.currentIndex);
                        if (PhotoViewer.this.secureDocuments.isEmpty()) {
                            PhotoViewer.this.closePhoto(false, false);
                            return;
                        }
                        int index2 = PhotoViewer.this.currentIndex;
                        if (index2 >= PhotoViewer.this.secureDocuments.size()) {
                            index2 = PhotoViewer.this.secureDocuments.size() - 1;
                        }
                        int unused2 = PhotoViewer.this.currentIndex = -1;
                        PhotoViewer.this.setImageIndex(index2, true);
                    }
                }

                public boolean canOpenMenu() {
                    if (PhotoViewer.this.currentMessageObject != null) {
                        return FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner).exists();
                    }
                    boolean z = false;
                    if (PhotoViewer.this.currentFileLocation == null) {
                        return false;
                    }
                    PhotoViewer photoViewer = PhotoViewer.this;
                    TLRPC.FileLocation access$8900 = photoViewer.getFileLocation(photoViewer.currentFileLocation);
                    if (PhotoViewer.this.avatarsDialogId != 0 || PhotoViewer.this.isEvent) {
                        z = true;
                    }
                    return FileLoader.getPathToAttach(access$8900, z).exists();
                }
            });
            ActionBarMenu menu = this.actionBar.createMenu();
            this.masksItem = menu.addItem(13, (int) R.drawable.msg_mask);
            this.pipItem = menu.addItem(5, (int) R.drawable.ic_goinline);
            this.sendItem = menu.addItem(3, (int) R.drawable.msg_forward);
            ActionBarMenuItem addItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
            this.menuItem = addItem;
            addItem.addSubItem(11, (int) R.drawable.msg_openin, (CharSequence) LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp)).setColors(-328966, -328966);
            this.menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            ActionBarMenuSubItem addSubItem = this.menuItem.addSubItem(2, (int) R.drawable.msg_media, (CharSequence) LocaleController.getString("ShowAllMedia", R.string.ShowAllMedia));
            this.allMediaItem = addSubItem;
            addSubItem.setColors(-328966, -328966);
            this.menuItem.addSubItem(4, (int) R.drawable.msg_message, (CharSequence) LocaleController.getString("ShowInChat", R.string.ShowInChat)).setColors(-328966, -328966);
            this.menuItem.addSubItem(10, (int) R.drawable.msg_shareout, (CharSequence) LocaleController.getString("ShareFile", R.string.ShareFile)).setColors(-328966, -328966);
            this.menuItem.addSubItem(1, (int) R.drawable.msg_gallery, (CharSequence) LocaleController.getString("SaveToGallery", R.string.SaveToGallery)).setColors(-328966, -328966);
            this.menuItem.addSubItem(6, (int) R.drawable.msg_delete, (CharSequence) LocaleController.getString("Delete", R.string.Delete)).setColors(-328966, -328966);
            this.menuItem.addSubItem(7, (int) R.drawable.msg_cancel, (CharSequence) LocaleController.getString("StopDownload", R.string.StopDownload)).setColors(-328966, -328966);
            this.menuItem.redrawPopup(-115203550);
            this.sendItem.setContentDescription(LocaleController.getString("Forward", R.string.Forward));
            FrameLayout frameLayout = new FrameLayout(this.actvityContext);
            this.bottomLayout = frameLayout;
            frameLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            GroupedPhotosListView groupedPhotosListView2 = new GroupedPhotosListView(this.actvityContext);
            this.groupedPhotosListView = groupedPhotosListView2;
            this.containerView.addView(groupedPhotosListView2, LayoutHelper.createFrame(-1.0f, 62.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.groupedPhotosListView.setDelegate(new GroupedPhotosListView.GroupedPhotosListViewDelegate() {
                public int getCurrentIndex() {
                    return PhotoViewer.this.currentIndex;
                }

                public int getCurrentAccount() {
                    return PhotoViewer.this.currentAccount;
                }

                public int getAvatarsDialogId() {
                    return PhotoViewer.this.avatarsDialogId;
                }

                public int getSlideshowMessageId() {
                    return PhotoViewer.this.slideshowMessageId;
                }

                public ArrayList<ImageLocation> getImagesArrLocations() {
                    return PhotoViewer.this.imagesArrLocations;
                }

                public ArrayList<MessageObject> getImagesArr() {
                    return PhotoViewer.this.imagesArr;
                }

                public ArrayList<TLRPC.PageBlock> getPageBlockArr() {
                    return null;
                }

                public Object getParentObject() {
                    return null;
                }

                public void setCurrentIndex(int index) {
                    int unused = PhotoViewer.this.currentIndex = -1;
                    if (PhotoViewer.this.currentThumb != null) {
                        PhotoViewer.this.currentThumb.release();
                        ImageReceiver.BitmapHolder unused2 = PhotoViewer.this.currentThumb = null;
                    }
                    PhotoViewer.this.setImageIndex(index, true);
                }
            });
            this.captionTextView = createCaptionTextView();
            this.switchCaptionTextView = createCaptionTextView();
            for (int a = 0; a < 3; a++) {
                this.photoProgressViews[a] = new PhotoProgressView(this.containerView.getContext(), this.containerView);
                this.photoProgressViews[a].setBackgroundState(0, false);
            }
            AnonymousClass9 r6 = new RadialProgressView(this.actvityContext) {
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    if (PhotoViewer.this.containerView != null) {
                        PhotoViewer.this.containerView.invalidate();
                    }
                }

                public void invalidate() {
                    super.invalidate();
                    if (PhotoViewer.this.containerView != null) {
                        PhotoViewer.this.containerView.invalidate();
                    }
                }
            };
            this.miniProgressView = r6;
            r6.setUseSelfAlpha(true);
            this.miniProgressView.setProgressColor(-1);
            this.miniProgressView.setSize(AndroidUtilities.dp(54.0f));
            this.miniProgressView.setBackgroundResource(R.drawable.circle_big);
            this.miniProgressView.setVisibility(4);
            this.miniProgressView.setAlpha(0.0f);
            this.containerView.addView(this.miniProgressView, LayoutHelper.createFrame(64, 64, 17));
            ImageView imageView = new ImageView(this.containerView.getContext());
            this.shareButton = imageView;
            imageView.setImageResource(R.drawable.share);
            this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
            this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.bottomLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1, 53));
            this.shareButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$2$PhotoViewer(view);
                }
            });
            this.shareButton.setContentDescription(LocaleController.getString("ShareFile", R.string.ShareFile));
            TextView textView = new TextView(this.containerView.getContext());
            this.nameTextView = textView;
            textView.setTextSize(1, 14.0f);
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.nameTextView.setTextColor(-1);
            this.nameTextView.setGravity(3);
            this.bottomLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 16.0f, 5.0f, 60.0f, 0.0f));
            TextView textView2 = new TextView(this.containerView.getContext());
            this.dateTextView = textView2;
            textView2.setTextSize(1, 13.0f);
            this.dateTextView.setSingleLine(true);
            this.dateTextView.setMaxLines(1);
            this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.dateTextView.setTextColor(-1);
            this.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.dateTextView.setGravity(3);
            this.bottomLayout.addView(this.dateTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 16.0f, 25.0f, 50.0f, 0.0f));
            createVideoControlsInterface();
            RadialProgressView radialProgressView = new RadialProgressView(this.parentActivity);
            this.progressView = radialProgressView;
            radialProgressView.setProgressColor(-1);
            this.progressView.setBackgroundResource(R.drawable.circle_big);
            this.progressView.setVisibility(4);
            this.containerView.addView(this.progressView, LayoutHelper.createFrame(54, 54, 17));
            PickerBottomLayoutViewer pickerBottomLayoutViewer = new PickerBottomLayoutViewer(this.parentActivity);
            this.qualityPicker = pickerBottomLayoutViewer;
            pickerBottomLayoutViewer.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.qualityPicker.updateSelectedCount(0, false);
            this.qualityPicker.setTranslationY((float) AndroidUtilities.dp(120.0f));
            this.qualityPicker.doneButton.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
            this.containerView.addView(this.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
            this.qualityPicker.cancelButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$3$PhotoViewer(view);
                }
            });
            this.qualityPicker.doneButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$4$PhotoViewer(view);
                }
            });
            VideoForwardDrawable videoForwardDrawable2 = new VideoForwardDrawable();
            this.videoForwardDrawable = videoForwardDrawable2;
            videoForwardDrawable2.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() {
                public void onAnimationEnd() {
                }

                public void invalidate() {
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            QualityChooseView qualityChooseView2 = new QualityChooseView(this.parentActivity);
            this.qualityChooseView = qualityChooseView2;
            qualityChooseView2.setTranslationY((float) AndroidUtilities.dp(120.0f));
            this.qualityChooseView.setVisibility(4);
            this.qualityChooseView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.qualityChooseView, LayoutHelper.createFrame(-1.0f, 70.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            AnonymousClass11 r3 = new FrameLayout(this.actvityContext) {
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(ev);
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.pickerView = r3;
            r3.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, -2, 83));
            VideoTimelinePlayView videoTimelinePlayView = new VideoTimelinePlayView(this.parentActivity);
            this.videoTimelineView = videoTimelinePlayView;
            videoTimelinePlayView.setDelegate(new VideoTimelinePlayView.VideoTimelineViewDelegate() {
                public void onLeftProgressChanged(float progress) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        if (PhotoViewer.this.videoPlayer.isPlaying()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.containerView.invalidate();
                        }
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * progress)));
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                        PhotoViewer.this.updateVideoInfo();
                    }
                }

                public void onRightProgressChanged(float progress) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        if (PhotoViewer.this.videoPlayer.isPlaying()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.containerView.invalidate();
                        }
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * progress)));
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(1.0f);
                        PhotoViewer.this.videoTimelineView.setProgress(1.0f);
                        PhotoViewer.this.updateVideoInfo();
                    }
                }

                public void onPlayProgressChanged(float progress) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * progress)));
                    }
                }

                public void didStartDragging() {
                }

                public void didStopDragging() {
                }
            });
            this.pickerView.addView(this.videoTimelineView, LayoutHelper.createFrame(-1.0f, 58.0f, 51, 0.0f, 8.0f, 0.0f, 88.0f));
            AnonymousClass13 r32 = new ImageView(this.parentActivity) {
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.pickerViewSendButton = r32;
            r32.setScaleType(ImageView.ScaleType.CENTER);
            this.pickerViewSendButton.setBackgroundDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), -10043398, -10043398));
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            this.pickerViewSendButton.setImageResource(R.drawable.attach_send);
            this.containerView.addView(this.pickerViewSendButton, LayoutHelper.createFrame(56.0f, 56.0f, 85, 0.0f, 0.0f, 14.0f, 14.0f));
            this.pickerViewSendButton.setContentDescription(LocaleController.getString("Send", R.string.Send));
            this.pickerViewSendButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$5$PhotoViewer(view);
                }
            });
            this.pickerViewSendButton.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return PhotoViewer.this.lambda$setParentActivity$8$PhotoViewer(view);
                }
            });
            LinearLayout itemsLayout = new LinearLayout(this.parentActivity);
            itemsLayout.setOrientation(0);
            this.pickerView.addView(itemsLayout, LayoutHelper.createFrame(-2.0f, 48.0f, 81, 0.0f, 0.0f, 34.0f, 0.0f));
            ImageView imageView2 = new ImageView(this.parentActivity);
            this.cropItem = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.cropItem.setImageResource(R.drawable.photo_crop);
            this.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            itemsLayout.addView(this.cropItem, LayoutHelper.createLinear(70, 48));
            this.cropItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$9$PhotoViewer(view);
                }
            });
            this.cropItem.setContentDescription(LocaleController.getString("CropImage", R.string.CropImage));
            ImageView imageView3 = new ImageView(this.parentActivity);
            this.rotateItem = imageView3;
            imageView3.setScaleType(ImageView.ScaleType.CENTER);
            this.rotateItem.setImageResource(R.drawable.tool_rotate);
            this.rotateItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            itemsLayout.addView(this.rotateItem, LayoutHelper.createLinear(70, 48));
            this.rotateItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$10$PhotoViewer(view);
                }
            });
            this.rotateItem.setContentDescription(LocaleController.getString("AccDescrRotate", R.string.AccDescrRotate));
            ImageView imageView4 = new ImageView(this.parentActivity);
            this.paintItem = imageView4;
            imageView4.setScaleType(ImageView.ScaleType.CENTER);
            this.paintItem.setImageResource(R.drawable.photo_paint);
            this.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            itemsLayout.addView(this.paintItem, LayoutHelper.createLinear(70, 48));
            this.paintItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$11$PhotoViewer(view);
                }
            });
            this.paintItem.setContentDescription(LocaleController.getString("AccDescrPhotoEditor", R.string.AccDescrPhotoEditor));
            ImageView imageView5 = new ImageView(this.parentActivity);
            this.compressItem = imageView5;
            imageView5.setTag(1);
            this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
            this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            int i2 = MessagesController.getGlobalMainSettings().getInt("compress_video2", 1);
            this.selectedCompression = i2;
            if (i2 <= 0) {
                this.compressItem.setImageResource(R.drawable.video_240);
            } else if (i2 == 1) {
                this.compressItem.setImageResource(R.drawable.video_360);
            } else if (i2 == 2) {
                this.compressItem.setImageResource(R.drawable.video_480);
            } else if (i2 == 3) {
                this.compressItem.setImageResource(R.drawable.video_720);
            } else if (i2 == 4) {
                this.compressItem.setImageResource(R.drawable.video_1080);
            }
            itemsLayout.addView(this.compressItem, LayoutHelper.createLinear(70, 48));
            this.compressItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$12$PhotoViewer(view);
                }
            });
            this.compressItem.setContentDescription(LocaleController.getString("AccDescrVideoQuality", R.string.AccDescrVideoQuality) + ", " + new String[]{"240", "360", "480", "720", "1080"}[Math.max(0, this.selectedCompression)]);
            ImageView imageView6 = new ImageView(this.parentActivity);
            this.muteItem = imageView6;
            imageView6.setScaleType(ImageView.ScaleType.CENTER);
            this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            itemsLayout.addView(this.muteItem, LayoutHelper.createLinear(70, 48));
            this.muteItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$13$PhotoViewer(view);
                }
            });
            ImageView imageView7 = new ImageView(this.parentActivity);
            this.cameraItem = imageView7;
            imageView7.setScaleType(ImageView.ScaleType.CENTER);
            this.cameraItem.setImageResource(R.drawable.photo_add);
            this.cameraItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.cameraItem.setContentDescription(LocaleController.getString("AccDescrTakeMorePics", R.string.AccDescrTakeMorePics));
            this.containerView.addView(this.cameraItem, LayoutHelper.createFrame(48.0f, 48.0f, 85, 0.0f, 0.0f, 16.0f, 0.0f));
            this.cameraItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$14$PhotoViewer(view);
                }
            });
            ImageView imageView8 = new ImageView(this.parentActivity);
            this.tuneItem = imageView8;
            imageView8.setScaleType(ImageView.ScaleType.CENTER);
            this.tuneItem.setImageResource(R.drawable.photo_tools);
            this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            itemsLayout.addView(this.tuneItem, LayoutHelper.createLinear(70, 48));
            this.tuneItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$15$PhotoViewer(view);
                }
            });
            this.tuneItem.setContentDescription(LocaleController.getString("AccDescrPhotoAdjust", R.string.AccDescrPhotoAdjust));
            ImageView imageView9 = new ImageView(this.parentActivity);
            this.timeItem = imageView9;
            imageView9.setScaleType(ImageView.ScaleType.CENTER);
            this.timeItem.setImageResource(R.drawable.photo_timer);
            this.timeItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.timeItem.setContentDescription(LocaleController.getString("SetTimer", R.string.SetTimer));
            itemsLayout.addView(this.timeItem, LayoutHelper.createLinear(70, 48));
            this.timeItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$21$PhotoViewer(view);
                }
            });
            PickerBottomLayoutViewer pickerBottomLayoutViewer2 = new PickerBottomLayoutViewer(this.actvityContext);
            this.editorDoneLayout = pickerBottomLayoutViewer2;
            pickerBottomLayoutViewer2.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.editorDoneLayout.updateSelectedCount(0, false);
            this.editorDoneLayout.setVisibility(8);
            this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.editorDoneLayout.cancelButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$22$PhotoViewer(view);
                }
            });
            this.editorDoneLayout.doneButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$23$PhotoViewer(view);
                }
            });
            TextView textView3 = new TextView(this.actvityContext);
            this.resetButton = textView3;
            textView3.setVisibility(8);
            this.resetButton.setTextSize(1, 14.0f);
            this.resetButton.setTextColor(-1);
            this.resetButton.setGravity(17);
            this.resetButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
            this.resetButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.resetButton.setText(LocaleController.getString("Reset", R.string.CropReset).toUpperCase());
            this.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.editorDoneLayout.addView(this.resetButton, LayoutHelper.createFrame(-2, -1, 49));
            this.resetButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$24$PhotoViewer(view);
                }
            });
            this.gestureDetector = new GestureDetector(this.containerView.getContext(), this);
            setDoubleTapEnabled(true);
            ImageReceiver.ImageReceiverDelegate imageReceiverDelegate = new ImageReceiver.ImageReceiverDelegate() {
                public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                    PhotoViewer.this.lambda$setParentActivity$25$PhotoViewer(imageReceiver, z, z2);
                }

                public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
                }
            };
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setCrossfadeAlpha((byte) 2);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setDelegate(imageReceiverDelegate);
            this.leftImage.setParentView(this.containerView);
            this.leftImage.setCrossfadeAlpha((byte) 2);
            this.leftImage.setInvalidateAll(true);
            this.leftImage.setDelegate(imageReceiverDelegate);
            this.rightImage.setParentView(this.containerView);
            this.rightImage.setCrossfadeAlpha((byte) 2);
            this.rightImage.setInvalidateAll(true);
            this.rightImage.setDelegate(imageReceiverDelegate);
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            AnonymousClass16 r12 = new CheckBox(this.containerView.getContext(), R.drawable.selectphoto_large) {
                public boolean onTouchEvent(MotionEvent event) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.checkImageView = r12;
            r12.setDrawBackground(true);
            this.checkImageView.setHasBorder(true);
            this.checkImageView.setSize(40);
            this.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0f));
            this.checkImageView.setColor(-10043398, -1);
            this.checkImageView.setVisibility(8);
            this.containerView.addView(this.checkImageView, LayoutHelper.createFrame(40.0f, 40.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 58.0f : 68.0f, 10.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                ((FrameLayout.LayoutParams) this.checkImageView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
            }
            this.checkImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$26$PhotoViewer(view);
                }
            });
            CounterView counterView = new CounterView(this.parentActivity);
            this.photosCounterView = counterView;
            this.containerView.addView(counterView, LayoutHelper.createFrame(40.0f, 40.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 58.0f : 68.0f, 66.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                ((FrameLayout.LayoutParams) this.photosCounterView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
            }
            this.photosCounterView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$27$PhotoViewer(view);
                }
            });
            RecyclerListView recyclerListView = new RecyclerListView(this.parentActivity);
            this.selectedPhotosListView = recyclerListView;
            recyclerListView.setVisibility(8);
            this.selectedPhotosListView.setAlpha(0.0f);
            this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
            this.selectedPhotosListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int position = parent.getChildAdapterPosition(view);
                    if (!(view instanceof PhotoPickerPhotoCell) || position != 0) {
                        outRect.left = 0;
                    } else {
                        outRect.left = AndroidUtilities.dp(3.0f);
                    }
                    outRect.right = AndroidUtilities.dp(3.0f);
                }
            });
            ((DefaultItemAnimator) this.selectedPhotosListView.getItemAnimator()).setDelayAnimations(false);
            this.selectedPhotosListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.selectedPhotosListView.setPadding(0, AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f));
            this.selectedPhotosListView.setLayoutManager(new LinearLayoutManager(this.parentActivity, 0, false) {
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                    LinearSmoothScrollerEnd linearSmoothScroller = new LinearSmoothScrollerEnd(recyclerView.getContext());
                    linearSmoothScroller.setTargetPosition(position);
                    startSmoothScroll(linearSmoothScroller);
                }
            });
            RecyclerListView recyclerListView2 = this.selectedPhotosListView;
            ListAdapter listAdapter = new ListAdapter(this.parentActivity);
            this.selectedPhotosAdapter = listAdapter;
            recyclerListView2.setAdapter(listAdapter);
            this.containerView.addView(this.selectedPhotosListView, LayoutHelper.createFrame(-1, 88, 51));
            this.selectedPhotosListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    PhotoViewer.this.lambda$setParentActivity$28$PhotoViewer(view, i);
                }
            });
            AnonymousClass19 r7 = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView) {
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    try {
                        return !PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return false;
                    }
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    try {
                        return !PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(ev);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return false;
                    }
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }

                /* access modifiers changed from: protected */
                public void extendActionMode(ActionMode actionMode, Menu menu) {
                    if (PhotoViewer.this.parentChatActivity != null) {
                        PhotoViewer.this.parentChatActivity.extendActionMode(menu);
                    }
                }
            };
            this.captionEditText = r7;
            r7.setDelegate(new PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate() {
                public void onCaptionEnter() {
                    PhotoViewer.this.closeCaptionEnter(true);
                }

                public void onTextChanged(CharSequence text) {
                    if (PhotoViewer.this.mentionsAdapter != null && PhotoViewer.this.captionEditText != null && PhotoViewer.this.parentChatActivity != null && text != null) {
                        PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(text.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages, false);
                    }
                }

                public void onWindowSizeChanged(int size) {
                    if (size - (ActionBar.getCurrentActionBarHeight() * 2) < AndroidUtilities.dp((float) ((Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0)))) {
                        boolean unused = PhotoViewer.this.allowMentions = false;
                        if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setVisibility(4);
                            return;
                        }
                        return;
                    }
                    boolean unused2 = PhotoViewer.this.allowMentions = true;
                    if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 4) {
                        PhotoViewer.this.mentionListView.setVisibility(0);
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= 19) {
                this.captionEditText.setImportantForAccessibility(4);
            }
            this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
            AnonymousClass21 r72 = new RecyclerListView(this.actvityContext) {
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(ev);
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(ev);
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
                }
            };
            this.mentionListView = r72;
            r72.setTag(5);
            AnonymousClass22 r73 = new LinearLayoutManager(this.actvityContext) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.mentionLayoutManager = r73;
            r73.setOrientation(1);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.mentionListView.setVisibility(8);
            this.mentionListView.setClipToPadding(true);
            this.mentionListView.setOverScrollMode(2);
            this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
            RecyclerListView recyclerListView3 = this.mentionListView;
            MentionsAdapter mentionsAdapter2 = new MentionsAdapter(this.actvityContext, true, 0, new MentionsAdapter.MentionsAdapterDelegate() {
                public void needChangePanelVisibility(boolean show) {
                    if (show) {
                        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                        int height = (Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0);
                        layoutParams3.height = AndroidUtilities.dp((float) height);
                        layoutParams3.topMargin = -AndroidUtilities.dp((float) height);
                        PhotoViewer.this.mentionListView.setLayoutParams(layoutParams3);
                        if (PhotoViewer.this.mentionListAnimation != null) {
                            PhotoViewer.this.mentionListAnimation.cancel();
                            AnimatorSet unused = PhotoViewer.this.mentionListAnimation = null;
                        }
                        if (PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setAlpha(1.0f);
                            return;
                        }
                        PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
                        if (PhotoViewer.this.allowMentions) {
                            PhotoViewer.this.mentionListView.setVisibility(0);
                            AnimatorSet unused2 = PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                            PhotoViewer.this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, View.ALPHA, new float[]{0.0f, 1.0f})});
                            PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                                        AnimatorSet unused = PhotoViewer.this.mentionListAnimation = null;
                                    }
                                }
                            });
                            PhotoViewer.this.mentionListAnimation.setDuration(200);
                            PhotoViewer.this.mentionListAnimation.start();
                            return;
                        }
                        PhotoViewer.this.mentionListView.setAlpha(1.0f);
                        PhotoViewer.this.mentionListView.setVisibility(4);
                        return;
                    }
                    if (PhotoViewer.this.mentionListAnimation != null) {
                        PhotoViewer.this.mentionListAnimation.cancel();
                        AnimatorSet unused3 = PhotoViewer.this.mentionListAnimation = null;
                    }
                    if (PhotoViewer.this.mentionListView.getVisibility() != 8) {
                        if (PhotoViewer.this.allowMentions) {
                            AnimatorSet unused4 = PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                            PhotoViewer.this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, View.ALPHA, new float[]{0.0f})});
                            PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animation)) {
                                        PhotoViewer.this.mentionListView.setVisibility(8);
                                        AnimatorSet unused = PhotoViewer.this.mentionListAnimation = null;
                                    }
                                }
                            });
                            PhotoViewer.this.mentionListAnimation.setDuration(200);
                            PhotoViewer.this.mentionListAnimation.start();
                            return;
                        }
                        PhotoViewer.this.mentionListView.setVisibility(8);
                    }
                }

                public void onContextSearch(boolean searching) {
                }

                public void onContextClick(TLRPC.BotInlineResult result) {
                }
            });
            this.mentionsAdapter = mentionsAdapter2;
            recyclerListView3.setAdapter(mentionsAdapter2);
            this.mentionListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    PhotoViewer.this.lambda$setParentActivity$29$PhotoViewer(view, i);
                }
            });
            this.mentionListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                public final boolean onItemClick(View view, int i) {
                    return PhotoViewer.this.lambda$setParentActivity$31$PhotoViewer(view, i);
                }
            });
            if (((AccessibilityManager) this.actvityContext.getSystemService("accessibility")).isEnabled()) {
                View view = new View(this.actvityContext);
                this.playButtonAccessibilityOverlay = view;
                view.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
                this.playButtonAccessibilityOverlay.setFocusable(true);
                this.containerView.addView(this.playButtonAccessibilityOverlay, LayoutHelper.createFrame(64, 64, 17));
            }
        }
    }

    public /* synthetic */ WindowInsets lambda$setParentActivity$1$PhotoViewer(View v, WindowInsets insets) {
        WindowInsets oldInsets = (WindowInsets) this.lastInsets;
        this.lastInsets = insets;
        if (oldInsets == null || !oldInsets.toString().equals(insets.toString())) {
            int i = this.animationInProgress;
            if (i == 1 || i == 3) {
                ClippingImageView clippingImageView = this.animatingImageView;
                clippingImageView.setTranslationX(clippingImageView.getTranslationX() - ((float) getLeftInset()));
                this.animationValues[0][2] = this.animatingImageView.getTranslationX();
            }
            this.windowView.requestLayout();
        }
        this.containerView.setPadding(insets.getSystemWindowInsetLeft(), 0, insets.getSystemWindowInsetRight(), 0);
        return insets.consumeSystemWindowInsets();
    }

    public /* synthetic */ void lambda$setParentActivity$2$PhotoViewer(View v) {
        onSharePressed();
    }

    public /* synthetic */ void lambda$setParentActivity$3$PhotoViewer(View view) {
        this.selectedCompression = this.previousCompression;
        didChangedCompressionLevel(false);
        showQualityView(false);
        requestVideoPreview(2);
    }

    public /* synthetic */ void lambda$setParentActivity$4$PhotoViewer(View view) {
        showQualityView(false);
        requestVideoPreview(2);
    }

    public /* synthetic */ void lambda$setParentActivity$5$PhotoViewer(View v) {
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity == null || !chatActivity.isInScheduleMode() || this.parentChatActivity.isEditingMessageMedia()) {
            sendPressed(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, UserObject.isUserSelf(this.parentChatActivity.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    PhotoViewer.this.sendPressed(z, i);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$setParentActivity$8$PhotoViewer(View view) {
        PhotoViewerProvider photoViewerProvider;
        View view2 = view;
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity == null || chatActivity.isInScheduleMode()) {
            return false;
        }
        TLRPC.Chat currentChat = this.parentChatActivity.getCurrentChat();
        TLRPC.User user = this.parentChatActivity.getCurrentUser();
        if (this.parentChatActivity.getCurrentEncryptedChat() != null) {
            return false;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
        this.sendPopupLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setAnimationEnabled(false);
        this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
            private Rect popupRect = new Rect();

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() != 0 || PhotoViewer.this.sendPopupWindow == null || !PhotoViewer.this.sendPopupWindow.isShowing()) {
                    return false;
                }
                v.getHitRect(this.popupRect);
                if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
                PhotoViewer.this.sendPopupWindow.dismiss();
                return false;
            }
        });
        this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                PhotoViewer.this.lambda$null$6$PhotoViewer(keyEvent);
            }
        });
        this.sendPopupLayout.setShowedFromBotton(false);
        this.sendPopupLayout.setBackgroundColor(-115203550);
        int i = 0;
        for (int a = 0; a < 2; a++) {
            if (a == 0 && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.getSelectedPhotos() != null) {
                boolean hasTtl = false;
                Iterator<Map.Entry<Object, Object>> it = this.placeProvider.getSelectedPhotos().entrySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Object object = it.next().getValue();
                    if (!(object instanceof MediaController.PhotoEntry)) {
                        if ((object instanceof MediaController.SearchImage) && ((MediaController.SearchImage) object).ttl != 0) {
                            hasTtl = true;
                            break;
                        }
                    } else if (((MediaController.PhotoEntry) object).ttl != 0) {
                        hasTtl = true;
                        break;
                    }
                }
                if (hasTtl) {
                }
            } else if (a == 1 && UserObject.isUserSelf(user)) {
            }
            int num = a;
            ActionBarMenuSubItem cell = new ActionBarMenuSubItem(this.parentActivity);
            cell.setBackgroundDrawable(Theme.createSelectorDrawable(620756991, 7));
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
            cell.setColors(-1, -1);
            this.sendPopupLayout.addView(cell, LayoutHelper.createFrame(-1.0f, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
            cell.setOnClickListener(new View.OnClickListener(num, user) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ TLRPC.User f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    PhotoViewer.this.lambda$null$7$PhotoViewer(this.f$1, this.f$2, view);
                }
            });
            i++;
        }
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view2.getLocationInWindow(location);
        this.sendPopupWindow.showAtLocation(view2, 51, ((location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(14.0f), (location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(18.0f));
        view2.performHapticFeedback(3, 2);
        return false;
    }

    public /* synthetic */ void lambda$null$6$PhotoViewer(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$7$PhotoViewer(int num, TLRPC.User user, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (num == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, UserObject.isUserSelf(user), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    PhotoViewer.this.sendPressed(z, i);
                }
            });
        } else if (num == 1) {
            sendPressed(false, 0);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$9$PhotoViewer(View v) {
        if (this.captionEditText.getTag() == null) {
            switchToEditMode(1);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$10$PhotoViewer(View v) {
        PhotoCropView photoCropView2 = this.photoCropView;
        if (photoCropView2 != null) {
            photoCropView2.rotate();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$11$PhotoViewer(View v) {
        if (this.captionEditText.getTag() == null) {
            switchToEditMode(3);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$12$PhotoViewer(View v) {
        if (this.captionEditText.getTag() == null) {
            showQualityView(true);
            requestVideoPreview(1);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$13$PhotoViewer(View v) {
        if (this.captionEditText.getTag() == null) {
            this.muteVideo = !this.muteVideo;
            updateMuteButton();
            updateVideoInfo();
            if (!this.muteVideo || this.checkImageView.isChecked()) {
                Object object = this.imagesArrLocals.get(this.currentIndex);
                if (object instanceof MediaController.PhotoEntry) {
                    ((MediaController.PhotoEntry) object).editedInfo = getCurrentVideoEditedInfo();
                    return;
                }
                return;
            }
            this.checkImageView.callOnClick();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$14$PhotoViewer(View v) {
        if (this.placeProvider != null && this.captionEditText.getTag() == null) {
            this.placeProvider.needAddMorePhotos();
            closePhoto(true, false);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$15$PhotoViewer(View v) {
        if (this.captionEditText.getTag() == null) {
            switchToEditMode(2);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$21$PhotoViewer(View v) {
        String str;
        int i;
        int currentTTL;
        if (this.parentActivity != null && this.captionEditText.getTag() == null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
            builder.setUseHardwareLayer(false);
            LinearLayout linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(1);
            builder.setCustomView(linearLayout);
            TextView titleView = new TextView(this.parentActivity);
            titleView.setLines(1);
            titleView.setSingleLine(true);
            titleView.setText(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
            titleView.setTextColor(-1);
            titleView.setTextSize(1, 16.0f);
            titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            titleView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(4.0f));
            titleView.setGravity(16);
            linearLayout.addView(titleView, LayoutHelper.createFrame(-1, -2.0f));
            titleView.setOnTouchListener($$Lambda$PhotoViewer$nmFZqeoqmiMMY3Kcq6vOiHJ8yX4.INSTANCE);
            TextView titleView2 = new TextView(this.parentActivity);
            if (this.isCurrentVideo) {
                i = R.string.MessageLifetimeVideo;
                str = "MessageLifetimeVideo";
            } else {
                i = R.string.MessageLifetimePhoto;
                str = "MessageLifetimePhoto";
            }
            titleView2.setText(LocaleController.getString(str, i));
            titleView2.setTextColor(-8355712);
            titleView2.setTextSize(1, 14.0f);
            titleView2.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            titleView2.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            titleView2.setGravity(16);
            linearLayout.addView(titleView2, LayoutHelper.createFrame(-1, -2.0f));
            titleView2.setOnTouchListener($$Lambda$PhotoViewer$vE6Z16eYaAvn1etpuvQkrN40Ric.INSTANCE);
            BottomSheet bottomSheet = builder.create();
            NumberPicker numberPicker = new NumberPicker(this.parentActivity);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(28);
            Object object = this.imagesArrLocals.get(this.currentIndex);
            if (object instanceof MediaController.PhotoEntry) {
                currentTTL = ((MediaController.PhotoEntry) object).ttl;
            } else if ((object instanceof MediaController.SearchImage) != 0) {
                currentTTL = ((MediaController.SearchImage) object).ttl;
            } else {
                currentTTL = 0;
            }
            if (currentTTL == 0) {
                numberPicker.setValue(MessagesController.getGlobalMainSettings().getInt("self_destruct", 7));
            } else if (currentTTL < 0 || currentTTL >= 21) {
                numberPicker.setValue(((currentTTL / 5) + 21) - 5);
            } else {
                numberPicker.setValue(currentTTL);
            }
            numberPicker.setTextColor(-1);
            numberPicker.setSelectorColor(-11711155);
            numberPicker.setFormatter($$Lambda$PhotoViewer$TzUF57GDVtcpi2oWup_73HL2Ruo.INSTANCE);
            linearLayout.addView(numberPicker, LayoutHelper.createLinear(-1, -2));
            FrameLayout buttonsLayout = new FrameLayout(this.parentActivity) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int count = getChildCount();
                    View positiveButton = null;
                    int width = right - left;
                    for (int a = 0; a < count; a++) {
                        View child = getChildAt(a);
                        if (((Integer) child.getTag()).intValue() == -1) {
                            positiveButton = child;
                            child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), (width - getPaddingRight()) + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                        } else if (((Integer) child.getTag()).intValue() == -2) {
                            int x = (width - getPaddingRight()) - child.getMeasuredWidth();
                            if (positiveButton != null) {
                                x -= positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                            }
                            child.layout(x, getPaddingTop(), child.getMeasuredWidth() + x, getPaddingTop() + child.getMeasuredHeight());
                        } else {
                            child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                        }
                    }
                }
            };
            buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            linearLayout.addView(buttonsLayout, LayoutHelper.createLinear(-1, 52));
            TextView textView = new TextView(this.parentActivity);
            textView.setMinWidth(AndroidUtilities.dp(64.0f));
            textView.setTag(-1);
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(-11944718);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
            textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(-11944718));
            textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
            textView.setOnClickListener(new View.OnClickListener(numberPicker, bottomSheet) {
                private final /* synthetic */ NumberPicker f$1;
                private final /* synthetic */ BottomSheet f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    PhotoViewer.this.lambda$null$19$PhotoViewer(this.f$1, this.f$2, view);
                }
            });
            TextView textView2 = new TextView(this.parentActivity);
            textView2.setMinWidth(AndroidUtilities.dp(64.0f));
            textView2.setTag(-2);
            textView2.setTextSize(1, 14.0f);
            textView2.setTextColor(-11944718);
            textView2.setGravity(17);
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView2.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
            textView2.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(-11944718));
            textView2.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            buttonsLayout.addView(textView2, LayoutHelper.createFrame(-2, 36, 53));
            textView2.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    BottomSheet.this.dismiss();
                }
            });
            bottomSheet.show();
            bottomSheet.setBackgroundColor(-16777216);
        }
    }

    static /* synthetic */ boolean lambda$null$16(View v13, MotionEvent event) {
        return true;
    }

    static /* synthetic */ boolean lambda$null$17(View v12, MotionEvent event) {
        return true;
    }

    static /* synthetic */ String lambda$null$18(int value) {
        if (value == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
        }
        if (value < 1 || value >= 21) {
            return LocaleController.formatTTLString((value - 16) * 5);
        }
        return LocaleController.formatTTLString(value);
    }

    public /* synthetic */ void lambda$null$19$PhotoViewer(NumberPicker numberPicker, BottomSheet bottomSheet, View v1) {
        int seconds;
        int value = numberPicker.getValue();
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("self_destruct", value);
        editor.commit();
        bottomSheet.dismiss();
        if (value < 0 || value >= 21) {
            seconds = (value - 16) * 5;
        } else {
            seconds = value;
        }
        Object object1 = this.imagesArrLocals.get(this.currentIndex);
        if (object1 instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) object1).ttl = seconds;
        } else if (object1 instanceof MediaController.SearchImage) {
            ((MediaController.SearchImage) object1).ttl = seconds;
        }
        this.timeItem.setColorFilter(seconds != 0 ? new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY) : null);
        if (!this.checkImageView.isChecked()) {
            this.checkImageView.callOnClick();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$22$PhotoViewer(View view) {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$setParentActivity$23$PhotoViewer(View view) {
        if (this.currentEditMode != 1 || this.photoCropView.isReady()) {
            applyCurrentEditMode();
            switchToEditMode(0);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$24$PhotoViewer(View v) {
        this.photoCropView.reset();
    }

    public /* synthetic */ void lambda$setParentActivity$25$PhotoViewer(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        PhotoViewerProvider photoViewerProvider;
        Bitmap bitmap;
        if (imageReceiver == this.centerImage && set && !thumb && !((this.currentEditMode != 1 && this.sendPhotoType != 1) || this.photoCropView == null || (bitmap = imageReceiver.getBitmap()) == null)) {
            this.photoCropView.setBitmap(bitmap, imageReceiver.getOrientation(), this.sendPhotoType != 1, true);
        }
        if (imageReceiver == this.centerImage && set && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.scaleToFill() && !this.ignoreDidSetImage) {
            if (!this.wasLayout) {
                this.dontResetZoomOnFirstLayout = true;
            } else {
                setScaleToFill();
            }
        }
    }

    public /* synthetic */ void lambda$setParentActivity$26$PhotoViewer(View v) {
        if (this.captionEditText.getTag() == null) {
            setPhotoChecked();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$27$PhotoViewer(View v) {
        PhotoViewerProvider photoViewerProvider;
        if (this.captionEditText.getTag() == null && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.getSelectedPhotosOrder() != null && !this.placeProvider.getSelectedPhotosOrder().isEmpty()) {
            togglePhotosListView(!this.isPhotosListViewVisible, true);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$28$PhotoViewer(View view, int position) {
        this.ignoreDidSetImage = true;
        int idx = this.imagesArrLocals.indexOf(view.getTag());
        if (idx >= 0) {
            this.currentIndex = -1;
            setImageIndex(idx, true);
        }
        this.ignoreDidSetImage = false;
    }

    public /* synthetic */ void lambda$setParentActivity$29$PhotoViewer(View view, int position) {
        Object object = this.mentionsAdapter.getItem(position);
        int start = this.mentionsAdapter.getResultStartPosition();
        int len = this.mentionsAdapter.getResultLength();
        if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            String name = UserObject.getName(user) + " ";
            if (!"all".equals(name.trim()) || user.id != -1) {
                Spannable spannable = new SpannableString("@" + name);
                spannable.setSpan(new URLSpanUserMention("" + user.id, 1), 0, spannable.length(), 33);
                spannable.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chat_messagePanelMetionText)), 0, spannable.length(), 33);
                this.captionEditText.addMentionText1(start, len, spannable, false);
                return;
            }
            Spannable spannable2 = new SpannableString("@" + name);
            spannable2.setSpan(new URLSpanUserMention("-1", 1), 0, spannable2.length(), 33);
            spannable2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chat_messagePanelMetionText)), 0, spannable2.length(), 33);
            this.captionEditText.addMentionText1(start, len, spannable2, false);
        } else if (object instanceof String) {
            this.captionEditText.replaceWithText(start, len, object + " ", false);
        } else if (object instanceof MediaDataController.KeywordResult) {
            String code = ((MediaDataController.KeywordResult) object).emoji;
            this.captionEditText.addEmojiToRecent(code);
            this.captionEditText.replaceWithText(start, len, code, true);
        }
    }

    public /* synthetic */ boolean lambda$setParentActivity$31$PhotoViewer(View view, int position) {
        if (!(this.mentionsAdapter.getItem(position) instanceof String)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhotoViewer.this.lambda$null$30$PhotoViewer(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showAlertDialog(builder);
        return true;
    }

    public /* synthetic */ void lambda$null$30$PhotoViewer(DialogInterface dialogInterface, int i) {
        this.mentionsAdapter.clearRecentHashtags();
    }

    /* access modifiers changed from: private */
    public void sendPressed(boolean notify, int scheduleDate) {
        if (this.captionEditText.getTag() == null) {
            if (this.sendPhotoType == 1) {
                applyCurrentEditMode();
            }
            if (this.placeProvider != null && !this.doneButtonPressed) {
                ChatActivity chatActivity = this.parentChatActivity;
                if (chatActivity != null) {
                    TLRPC.Chat chat = chatActivity.getCurrentChat();
                    if (this.parentChatActivity.getCurrentUser() != null || ((ChatObject.isChannel(chat) && chat.megagroup) || !ChatObject.isChannel(chat))) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                        edit.putBoolean("silent_" + this.parentChatActivity.getDialogId(), !notify).commit();
                    }
                }
                this.placeProvider.sendButtonPressed(this.currentIndex, getCurrentVideoEditedInfo(), notify, scheduleDate);
                this.doneButtonPressed = true;
                closePhoto(false, false);
            }
        }
    }

    private boolean checkInlinePermissions() {
        if (this.parentActivity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.parentActivity)) {
            return true;
        }
        new AlertDialog.Builder((Context) this.parentActivity).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", R.string.PermissionDrawAboveOtherApps)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhotoViewer.this.lambda$checkInlinePermissions$32$PhotoViewer(dialogInterface, i);
            }
        }).show();
        return false;
    }

    public /* synthetic */ void lambda$checkInlinePermissions$32$PhotoViewer(DialogInterface dialog, int which) {
        Activity activity = this.parentActivity;
        if (activity != null) {
            try {
                activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + this.parentActivity.getPackageName())));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private TextView createCaptionTextView() {
        TextView textView = new TextView(this.actvityContext) {
            public boolean onTouchEvent(MotionEvent event) {
                return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(event);
            }
        };
        textView.setMovementMethod(new LinkMovementMethodMy());
        textView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        textView.setLinkTextColor(-8994063);
        textView.setTextColor(-1);
        textView.setHighlightColor(872415231);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView.setTextSize(1, 16.0f);
        textView.setVisibility(4);
        textView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoViewer.this.lambda$createCaptionTextView$33$PhotoViewer(view);
            }
        });
        return textView;
    }

    public /* synthetic */ void lambda$createCaptionTextView$33$PhotoViewer(View v) {
        if (this.needCaptionLayout) {
            openCaptionEnter();
        }
    }

    /* access modifiers changed from: private */
    public int getLeftInset() {
        if (this.lastInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) this.lastInsets).getSystemWindowInsetLeft();
    }

    /* access modifiers changed from: private */
    public int getRightInset() {
        if (this.lastInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) this.lastInsets).getSystemWindowInsetRight();
    }

    /* access modifiers changed from: private */
    public void dismissInternal() {
        try {
            if (this.windowView.getParent() != null) {
                ((LaunchActivity) this.parentActivity).drawerLayoutContainer.setAllowDrawContent(true);
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void switchToPip() {
        if (this.videoPlayer != null && this.textureUploaded && checkInlinePermissions() && !this.changingTextureView && !this.switchingInlineMode && !this.isInline) {
            if (PipInstance != null) {
                PipInstance.destroyPhotoViewer();
            }
            this.openedFullScreenVideo = false;
            PipInstance = Instance;
            Instance = null;
            this.switchingInlineMode = true;
            this.isVisible = false;
            PlaceProviderObject placeProviderObject = this.currentPlaceObject;
            if (placeProviderObject != null) {
                placeProviderObject.imageReceiver.setVisible(true, true);
                AnimatedFileDrawable animation = this.currentPlaceObject.imageReceiver.getAnimation();
                if (animation != null) {
                    Bitmap bitmap = animation.getAnimatedBitmap();
                    if (bitmap != null) {
                        try {
                            Bitmap src = this.videoTextureView.getBitmap(bitmap.getWidth(), bitmap.getHeight());
                            new Canvas(bitmap).drawBitmap(src, 0.0f, 0.0f, (Paint) null);
                            src.recycle();
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    animation.seekTo(this.videoPlayer.getCurrentPosition(), true);
                    this.currentPlaceObject.imageReceiver.setAllowStartAnimation(true);
                    this.currentPlaceObject.imageReceiver.startAnimation();
                }
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.pipAnimationInProgress = true;
                im.bclpbkiauv.ui.components.Rect rect = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
                float scale2 = rect.width / ((float) this.videoTextureView.getWidth());
                rect.y += (float) AndroidUtilities.statusBarHeight;
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_X, new float[]{scale2}), ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_Y, new float[]{scale2}), ObjectAnimator.ofFloat(this.textureImageView, View.TRANSLATION_X, new float[]{rect.x}), ObjectAnimator.ofFloat(this.textureImageView, View.TRANSLATION_Y, new float[]{rect.y}), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_X, new float[]{scale2}), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_Y, new float[]{scale2}), ObjectAnimator.ofFloat(this.videoTextureView, View.TRANSLATION_X, new float[]{(rect.x - this.aspectRatioFrameLayout.getX()) + ((float) getLeftInset())}), ObjectAnimator.ofFloat(this.videoTextureView, View.TRANSLATION_Y, new float[]{rect.y - this.aspectRatioFrameLayout.getY()}), ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(250);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        boolean unused = PhotoViewer.this.pipAnimationInProgress = false;
                        PhotoViewer.this.switchToInlineRunnable.run();
                    }
                });
                animatorSet.start();
                return;
            }
            this.switchToInlineRunnable.run();
            dismissInternal();
        }
    }

    public VideoPlayer getVideoPlayer() {
        return this.videoPlayer;
    }

    public void exitFromPip() {
        if (this.isInline) {
            if (Instance != null) {
                Instance.closePhoto(false, true);
            }
            Instance = PipInstance;
            PipInstance = null;
            this.switchingInlineMode = true;
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.currentBitmap = null;
            }
            this.changingTextureView = true;
            this.isInline = false;
            this.videoTextureView.setVisibility(4);
            this.aspectRatioFrameLayout.addView(this.videoTextureView);
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.pipAnimationInProgress = true;
                im.bclpbkiauv.ui.components.Rect rect = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
                float scale2 = rect.width / ((float) this.textureImageView.getLayoutParams().width);
                rect.y += (float) AndroidUtilities.statusBarHeight;
                this.textureImageView.setScaleX(scale2);
                this.textureImageView.setScaleY(scale2);
                this.textureImageView.setTranslationX(rect.x);
                this.textureImageView.setTranslationY(rect.y);
                this.videoTextureView.setScaleX(scale2);
                this.videoTextureView.setScaleY(scale2);
                this.videoTextureView.setTranslationX(rect.x - this.aspectRatioFrameLayout.getX());
                this.videoTextureView.setTranslationY(rect.y - this.aspectRatioFrameLayout.getY());
            } else {
                this.pipVideoView.close();
                this.pipVideoView = null;
            }
            try {
                this.isVisible = true;
                ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
                if (this.currentPlaceObject != null) {
                    this.currentPlaceObject.imageReceiver.setVisible(false, false);
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.waitingForDraw = 4;
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVideoSeekPreviewPosition() {
        int x = (this.videoPlayerSeekbar.getThumbX() + AndroidUtilities.dp(48.0f)) - (this.videoPreviewFrame.getMeasuredWidth() / 2);
        int min = AndroidUtilities.dp(10.0f);
        int max = (this.videoPlayerControlFrameLayout.getMeasuredWidth() - AndroidUtilities.dp(10.0f)) - (this.videoPreviewFrame.getMeasuredWidth() / 2);
        if (x < min) {
            x = min;
        } else if (x >= max) {
            x = max;
        }
        this.videoPreviewFrame.setTranslationX((float) x);
    }

    /* access modifiers changed from: private */
    public void showVideoSeekPreviewPosition(boolean show) {
        if (show && this.videoPreviewFrame.getTag() != null) {
            return;
        }
        if (!show && this.videoPreviewFrame.getTag() == null) {
            return;
        }
        if (!show || this.videoPreviewFrame.isReady()) {
            AnimatorSet animatorSet = this.videoPreviewFrameAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.videoPreviewFrame.setTag(show ? 1 : null);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.videoPreviewFrameAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            VideoSeekPreviewImage videoSeekPreviewImage = this.videoPreviewFrame;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(videoSeekPreviewImage, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.videoPreviewFrameAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = PhotoViewer.this.videoPreviewFrameAnimation = null;
                }
            });
            this.videoPreviewFrameAnimation.setDuration(180);
            this.videoPreviewFrameAnimation.start();
            return;
        }
        this.needShowOnReady = show;
    }

    private void createVideoControlsInterface() {
        SeekBar seekBar = new SeekBar(this.containerView.getContext());
        this.videoPlayerSeekbar = seekBar;
        seekBar.setLineHeight(AndroidUtilities.dp(4.0f));
        this.videoPlayerSeekbar.setColors(1728053247, 1728053247, -2764585, -1, -1);
        this.videoPlayerSeekbar.setDelegate(new SeekBar.SeekBarDelegate() {
            public void onSeekBarDrag(float progress) {
                if (PhotoViewer.this.videoPlayer != null) {
                    if (!PhotoViewer.this.inPreview && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        progress = PhotoViewer.this.videoTimelineView.getLeftProgress() + ((PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress()) * progress);
                    }
                    long duration = PhotoViewer.this.videoPlayer.getDuration();
                    if (duration == C.TIME_UNSET) {
                        float unused = PhotoViewer.this.seekToProgressPending = progress;
                    } else {
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (((float) duration) * progress)));
                    }
                    PhotoViewer.this.showVideoSeekPreviewPosition(false);
                    boolean unused2 = PhotoViewer.this.needShowOnReady = false;
                }
            }

            public void onSeekBarContinuousDrag(float progress) {
                if (!(PhotoViewer.this.videoPlayer == null || PhotoViewer.this.videoPreviewFrame == null)) {
                    PhotoViewer.this.videoPreviewFrame.setProgress(progress, PhotoViewer.this.videoPlayerSeekbar.getWidth());
                }
                PhotoViewer.this.showVideoSeekPreviewPosition(true);
                PhotoViewer.this.updateVideoSeekPreviewPosition();
            }
        });
        AnonymousClass28 r0 = new FrameLayout(this.containerView.getContext()) {
            public boolean onTouchEvent(MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (!PhotoViewer.this.videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - ((float) AndroidUtilities.dp(48.0f)), event.getY())) {
                    return true;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                long duration;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (PhotoViewer.this.videoPlayer != null) {
                    duration = PhotoViewer.this.videoPlayer.getDuration();
                    if (duration == C.TIME_UNSET) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                long duration2 = duration / 1000;
                Paint paint = PhotoViewer.this.videoPlayerTime.getPaint();
                Object[] objArr = {Long.valueOf(duration2 / 60), Long.valueOf(duration2 % 60), Long.valueOf(duration2 / 60), Long.valueOf(duration2 % 60)};
                PhotoViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) paint.measureText(String.format("%02d:%02d / %02d:%02d", objArr)))), getMeasuredHeight());
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                float progress = 0.0f;
                if (PhotoViewer.this.videoPlayer != null) {
                    progress = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (!PhotoViewer.this.inPreview && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        float progress2 = progress - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (progress2 < 0.0f) {
                            progress2 = 0.0f;
                        }
                        progress = progress2 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (progress > 1.0f) {
                            progress = 1.0f;
                        }
                    }
                }
                PhotoViewer.this.videoPlayerSeekbar.setProgress(progress);
                PhotoViewer.this.videoTimelineView.setProgress(progress);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
                PhotoViewer.this.videoPlayerSeekbar.draw(canvas);
                canvas.restore();
            }
        };
        this.videoPlayerControlFrameLayout = r0;
        r0.setWillNotDraw(false);
        this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
        AnonymousClass29 r02 = new VideoSeekPreviewImage(this.containerView.getContext(), new VideoSeekPreviewImage.VideoSeekPreviewImageDelegate() {
            public final void onReady() {
                PhotoViewer.this.lambda$createVideoControlsInterface$34$PhotoViewer();
            }
        }) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                PhotoViewer.this.updateVideoSeekPreviewPosition();
            }

            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                if (visibility == 0) {
                    PhotoViewer.this.updateVideoSeekPreviewPosition();
                }
            }
        };
        this.videoPreviewFrame = r02;
        r02.setAlpha(0.0f);
        this.containerView.addView(this.videoPreviewFrame, LayoutHelper.createFrame(-2.0f, -2.0f, 83, 0.0f, 0.0f, 0.0f, 58.0f));
        ImageView imageView = new ImageView(this.containerView.getContext());
        this.videoPlayButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48.0f, 48.0f, 51, 4.0f, 0.0f, 0.0f, 0.0f));
        this.videoPlayButton.setFocusable(true);
        this.videoPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
        this.videoPlayButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoViewer.this.lambda$createVideoControlsInterface$35$PhotoViewer(view);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(this.containerView.getContext());
        this.videoPlayerTime = simpleTextView;
        simpleTextView.setTextColor(-1);
        this.videoPlayerTime.setGravity(53);
        this.videoPlayerTime.setTextSize(13);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2.0f, -1.0f, 53, 0.0f, 17.0f, 7.0f, 0.0f));
    }

    public /* synthetic */ void lambda$createVideoControlsInterface$34$PhotoViewer() {
        if (this.needShowOnReady) {
            showVideoSeekPreviewPosition(true);
        }
    }

    public /* synthetic */ void lambda$createVideoControlsInterface$35$PhotoViewer(View v) {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            if (this.isPlaying) {
                videoPlayer2.pause();
            } else {
                if (this.isCurrentVideo) {
                    if (Math.abs(this.videoTimelineView.getProgress() - 1.0f) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                        this.videoPlayer.seekTo((long) ((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration()))));
                    }
                } else if (Math.abs(this.videoPlayerSeekbar.getProgress() - 1.0f) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                    this.videoPlayer.seekTo(0);
                }
                this.videoPlayer.play();
            }
            this.containerView.invalidate();
        }
    }

    private void openCaptionEnter() {
        int i;
        String str;
        int i2;
        if (this.imageMoveAnimation == null && this.changeModeAnimation == null && this.currentEditMode == 0 && (i = this.sendPhotoType) != 1 && i != 3) {
            this.selectedPhotosListView.setVisibility(8);
            this.selectedPhotosListView.setEnabled(false);
            this.selectedPhotosListView.setAlpha(0.0f);
            this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
            this.photosCounterView.setRotationX(0.0f);
            this.isPhotosListViewVisible = false;
            this.captionEditText.setTag(1);
            this.captionEditText.openKeyboard();
            this.captionEditText.setImportantForAccessibility(0);
            this.lastTitle = this.actionBar.getTitle();
            if (this.isCurrentVideo) {
                ActionBar actionBar2 = this.actionBar;
                if (this.muteVideo) {
                    i2 = R.string.GifCaption;
                    str = "GifCaption";
                } else {
                    i2 = R.string.VideoCaption;
                    str = "VideoCaption";
                }
                actionBar2.setTitle(LocaleController.getString(str, i2));
                this.actionBar.setSubtitle((CharSequence) null);
                return;
            }
            this.actionBar.setTitle(LocaleController.getString("PhotoCaption", R.string.PhotoCaption));
        }
    }

    /* access modifiers changed from: private */
    public VideoEditedInfo getCurrentVideoEditedInfo() {
        if (!this.isCurrentVideo || this.currentPlayingVideoFile == null || this.compressionsCount == 0) {
            return null;
        }
        VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
        videoEditedInfo.startTime = this.startTime;
        videoEditedInfo.endTime = this.endTime;
        videoEditedInfo.start = this.videoCutStart;
        videoEditedInfo.end = this.videoCutEnd;
        videoEditedInfo.rotationValue = this.rotationValue;
        videoEditedInfo.originalWidth = this.originalWidth;
        videoEditedInfo.originalHeight = this.originalHeight;
        videoEditedInfo.bitrate = this.bitrate;
        videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
        int i = this.estimatedSize;
        videoEditedInfo.estimatedSize = i != 0 ? (long) i : 1;
        videoEditedInfo.estimatedDuration = this.estimatedDuration;
        videoEditedInfo.framerate = this.videoFramerate;
        int i2 = -1;
        if (this.muteVideo || !(this.compressItem.getTag() == null || this.selectedCompression == this.compressionsCount - 1)) {
            if (this.muteVideo) {
                this.selectedCompression = 1;
                updateWidthHeightBitrateForCompression();
            }
            videoEditedInfo.resultWidth = this.resultWidth;
            videoEditedInfo.resultHeight = this.resultHeight;
            if (!this.muteVideo) {
                i2 = this.bitrate;
            }
            videoEditedInfo.bitrate = i2;
            videoEditedInfo.muted = this.muteVideo;
        } else {
            videoEditedInfo.resultWidth = this.originalWidth;
            videoEditedInfo.resultHeight = this.originalHeight;
            if (!this.muteVideo) {
                i2 = this.originalBitrate;
            }
            videoEditedInfo.bitrate = i2;
            videoEditedInfo.muted = this.muteVideo;
        }
        return videoEditedInfo;
    }

    /* access modifiers changed from: private */
    public void closeCaptionEnter(boolean apply) {
        int i = this.currentIndex;
        if (i >= 0 && i < this.imagesArrLocals.size()) {
            Object object = this.imagesArrLocals.get(this.currentIndex);
            String str = null;
            if (apply) {
                CharSequence[] result = {this.captionEditText.getFieldCharSequence()};
                ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(result);
                if (object instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                    photoEntry.caption = result[0];
                    photoEntry.entities = entities;
                } else if (object instanceof MediaController.SearchImage) {
                    MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) object;
                    photoEntry2.caption = result[0];
                    photoEntry2.entities = entities;
                }
                if (this.captionEditText.getFieldCharSequence().length() != 0 && !this.placeProvider.isPhotoChecked(this.currentIndex)) {
                    setPhotoChecked();
                }
                setCurrentCaption((MessageObject) null, result[0], false);
            }
            this.captionEditText.setTag((Object) null);
            String str2 = this.lastTitle;
            if (str2 != null) {
                this.actionBar.setTitle(str2);
                this.lastTitle = null;
            }
            if (this.isCurrentVideo) {
                ActionBar actionBar2 = this.actionBar;
                if (!this.muteVideo) {
                    str = this.currentSubtitle;
                }
                actionBar2.setSubtitle(str);
            }
            updateCaptionTextForCurrentPhoto(object);
            if (this.captionEditText.isPopupShowing()) {
                this.captionEditText.hidePopup();
            }
            this.captionEditText.closeKeyboard();
            if (Build.VERSION.SDK_INT >= 19) {
                this.captionEditText.setImportantForAccessibility(4);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVideoPlayerTime() {
        String newText;
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 == null) {
            newText = String.format("%02d:%02d / %02d:%02d", new Object[]{0, 0, 0, 0});
        } else {
            long current = videoPlayer2.getCurrentPosition();
            if (current < 0) {
                current = 0;
            }
            long total = this.videoPlayer.getDuration();
            if (total < 0) {
                total = 0;
            }
            if (total == C.TIME_UNSET || current == C.TIME_UNSET) {
                newText = String.format("%02d:%02d / %02d:%02d", new Object[]{0, 0, 0, 0});
            } else {
                if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                    total = (long) (((float) total) * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()));
                    current = (long) (((float) current) - (this.videoTimelineView.getLeftProgress() * ((float) total)));
                    if (current > total) {
                        current = total;
                    }
                }
                long current2 = current / 1000;
                long total2 = total / 1000;
                if (total2 == 0) {
                    total2 = 1;
                }
                newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(current2 / 60), Integer.valueOf((int) Math.ceil(((double) current2) % 60.0d)), Long.valueOf(total2 / 60), Integer.valueOf((int) Math.ceil(((double) total2) % 60.0d))});
            }
        }
        this.videoPlayerTime.setText(newText);
    }

    private void checkBufferedProgress(float progress) {
        MessageObject messageObject;
        TLRPC.Document document;
        if (this.isStreaming && this.parentActivity != null && !this.streamingAlertShown && this.videoPlayer != null && (messageObject = this.currentMessageObject) != null && (document = messageObject.getDocument()) != null && this.currentMessageObject.getDuration() >= 20 && progress < 0.9f) {
            if ((((float) document.size) * progress >= 5242880.0f || (progress >= 0.5f && document.size >= 2097152)) && Math.abs(SystemClock.elapsedRealtime() - this.startedPlayTime) >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                if (this.videoPlayer.getDuration() == C.TIME_UNSET) {
                    ToastUtils.show((int) R.string.VideoDoesNotSupportStreaming);
                }
                this.streamingAlertShown = true;
            }
        }
    }

    public void injectVideoPlayer(VideoPlayer player) {
        this.injectingVideoPlayer = player;
    }

    public void injectVideoPlayerSurface(SurfaceTexture surface) {
        this.injectingVideoPlayerSurface = surface;
    }

    public boolean isInjectingVideoPlayer() {
        return this.injectingVideoPlayer != null;
    }

    /* access modifiers changed from: private */
    public void updatePlayerState(boolean playWhenReady, int playbackState) {
        MessageObject messageObject;
        if (this.videoPlayer != null) {
            if (this.isStreaming) {
                if (playbackState != 2 || !this.skipFirstBufferingProgress) {
                    toggleMiniProgress(this.seekToProgressPending != 0.0f || playbackState == 2, true);
                } else if (playWhenReady) {
                    this.skipFirstBufferingProgress = false;
                }
            }
            if (!playWhenReady || playbackState == 4 || playbackState == 1) {
                try {
                    this.parentActivity.getWindow().clearFlags(128);
                    this.keepScreenOnFlagSet = false;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                try {
                    this.parentActivity.getWindow().addFlags(128);
                    this.keepScreenOnFlagSet = true;
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            if (playbackState == 3 || playbackState == 1) {
                if (this.currentMessageObject != null) {
                    this.videoPreviewFrame.open(this.videoPlayer.getCurrentUri());
                }
                if (this.seekToProgressPending != 0.0f) {
                    this.videoPlayer.seekTo((long) ((int) (((float) this.videoPlayer.getDuration()) * this.seekToProgressPending)));
                    this.seekToProgressPending = 0.0f;
                    MessageObject messageObject2 = this.currentMessageObject;
                    if (messageObject2 != null && !FileLoader.getInstance(messageObject2.currentAccount).isLoadingVideoAny(this.currentMessageObject.getDocument())) {
                        this.skipFirstBufferingProgress = true;
                    }
                }
            }
            if (playbackState == 3) {
                if (this.aspectRatioFrameLayout.getVisibility() != 0) {
                    this.aspectRatioFrameLayout.setVisibility(0);
                }
                if (!this.pipItem.isEnabled()) {
                    this.pipAvailable = true;
                    this.pipItem.setEnabled(true);
                    this.pipItem.setAlpha(1.0f);
                }
                this.playerWasReady = true;
                MessageObject messageObject3 = this.currentMessageObject;
                if (messageObject3 != null && messageObject3.isVideo()) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.currentMessageObject.currentAccount).removeLoadingVideo(this.currentMessageObject.getDocument(), true, false);
                }
            } else if (playbackState == 2 && playWhenReady && (messageObject = this.currentMessageObject) != null && messageObject.isVideo()) {
                if (this.playerWasReady) {
                    this.setLoadingRunnable.run();
                } else {
                    AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000);
                }
            }
            if (!this.videoPlayer.isPlaying() || playbackState == 4) {
                if (this.isPlaying) {
                    this.isPlaying = false;
                    this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
                    AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
                    if (playbackState == 4) {
                        if (!this.isCurrentVideo) {
                            if (!this.isActionBarVisible) {
                                toggleActionBar(true, true);
                            }
                            this.videoPlayerSeekbar.setProgress(0.0f);
                            this.videoPlayerControlFrameLayout.invalidate();
                            if (this.inPreview || this.videoTimelineView.getVisibility() != 0) {
                                this.videoPlayer.seekTo(0);
                            } else {
                                this.videoPlayer.seekTo((long) ((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration()))));
                            }
                            this.videoPlayer.pause();
                        } else if (!this.videoTimelineView.isDragging()) {
                            this.videoTimelineView.setProgress(0.0f);
                            if (this.inPreview || this.videoTimelineView.getVisibility() != 0) {
                                this.videoPlayer.seekTo(0);
                            } else {
                                this.videoPlayer.seekTo((long) ((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration()))));
                            }
                            this.videoPlayer.pause();
                            this.containerView.invalidate();
                        }
                        PipVideoView pipVideoView2 = this.pipVideoView;
                        if (pipVideoView2 != null) {
                            pipVideoView2.onVideoCompleted();
                        }
                    }
                }
            } else if (!this.isPlaying) {
                this.isPlaying = true;
                this.videoPlayButton.setImageResource(R.drawable.inline_video_pause);
                AndroidUtilities.runOnUIThread(this.updateProgressRunnable);
            }
            PipVideoView pipVideoView3 = this.pipVideoView;
            if (pipVideoView3 != null) {
                pipVideoView3.updatePlayButton();
            }
            updateVideoPlayerTime();
        }
    }

    private void preparePlayer(Uri uri, boolean playWhenReady, boolean preview) {
        if (!preview) {
            this.currentPlayingVideoFile = uri;
        }
        if (this.parentActivity != null) {
            int i = 0;
            this.streamingAlertShown = false;
            this.startedPlayTime = SystemClock.elapsedRealtime();
            this.currentVideoFinishedLoading = false;
            this.lastBufferedPositionCheck = 0;
            this.firstAnimationDelay = true;
            this.inPreview = preview;
            releasePlayer(false);
            if (this.videoTextureView == null) {
                AnonymousClass30 r4 = new AspectRatioFrameLayout(this.parentActivity) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        if (PhotoViewer.this.textureImageView != null) {
                            ViewGroup.LayoutParams layoutParams = PhotoViewer.this.textureImageView.getLayoutParams();
                            layoutParams.width = getMeasuredWidth();
                            layoutParams.height = getMeasuredHeight();
                        }
                    }
                };
                this.aspectRatioFrameLayout = r4;
                r4.setVisibility(4);
                this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                TextureView textureView = new TextureView(this.parentActivity);
                this.videoTextureView = textureView;
                SurfaceTexture surfaceTexture = this.injectingVideoPlayerSurface;
                if (surfaceTexture != null) {
                    textureView.setSurfaceTexture(surfaceTexture);
                    this.textureUploaded = true;
                    this.injectingVideoPlayerSurface = null;
                }
                this.videoTextureView.setPivotX(0.0f);
                this.videoTextureView.setPivotY(0.0f);
                this.videoTextureView.setOpaque(false);
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            if (Build.VERSION.SDK_INT >= 21 && this.textureImageView == null) {
                ImageView imageView = new ImageView(this.parentActivity);
                this.textureImageView = imageView;
                imageView.setBackgroundColor(SupportMenu.CATEGORY_MASK);
                this.textureImageView.setPivotX(0.0f);
                this.textureImageView.setPivotY(0.0f);
                this.textureImageView.setVisibility(4);
                this.containerView.addView(this.textureImageView);
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView2 = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView2.setAlpha(0.0f);
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            boolean newPlayerCreated = false;
            this.playerWasReady = false;
            if (this.videoPlayer == null) {
                VideoPlayer videoPlayer2 = this.injectingVideoPlayer;
                if (videoPlayer2 != null) {
                    this.videoPlayer = videoPlayer2;
                    this.injectingVideoPlayer = null;
                    this.playerInjected = true;
                    updatePlayerState(videoPlayer2.getPlayWhenReady(), this.videoPlayer.getPlaybackState());
                } else {
                    this.videoPlayer = new VideoPlayer();
                    newPlayerCreated = true;
                }
                this.videoPlayer.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        PhotoViewer.this.updatePlayerState(playWhenReady, playbackState);
                    }

                    public void onError(Exception e) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            FileLog.e((Throwable) e);
                            if (PhotoViewer.this.menuItem.isSubItemVisible(11)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) PhotoViewer.this.parentActivity);
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("CantPlayVideo", R.string.CantPlayVideo));
                                builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        PhotoViewer.AnonymousClass31.this.lambda$onError$0$PhotoViewer$31(dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                                PhotoViewer.this.showAlertDialog(builder);
                            }
                        }
                    }

                    public /* synthetic */ void lambda$onError$0$PhotoViewer$31(DialogInterface dialog, int which) {
                        try {
                            AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                            PhotoViewer.this.closePhoto(false, false);
                        } catch (Exception e1) {
                            FileLog.e((Throwable) e1);
                        }
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                                int temp = width;
                                width = height;
                                height = temp;
                            }
                            PhotoViewer.this.aspectRatioFrameLayout.setAspectRatio(height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height), unappliedRotationDegrees);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (!PhotoViewer.this.textureUploaded) {
                            boolean unused = PhotoViewer.this.textureUploaded = true;
                            PhotoViewer.this.containerView.invalidate();
                        }
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        if (PhotoViewer.this.changingTextureView) {
                            boolean unused = PhotoViewer.this.changingTextureView = false;
                            if (PhotoViewer.this.isInline) {
                                if (PhotoViewer.this.isInline) {
                                    int unused2 = PhotoViewer.this.waitingForFirstTextureUpload = 1;
                                }
                                PhotoViewer.this.changedTextureView.setSurfaceTexture(surfaceTexture);
                                PhotoViewer.this.changedTextureView.setSurfaceTextureListener(PhotoViewer.this.surfaceTextureListener);
                                PhotoViewer.this.changedTextureView.setVisibility(0);
                                return true;
                            }
                        }
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                        if (PhotoViewer.this.waitingForFirstTextureUpload == 2) {
                            if (PhotoViewer.this.textureImageView != null) {
                                PhotoViewer.this.textureImageView.setVisibility(4);
                                PhotoViewer.this.textureImageView.setImageDrawable((Drawable) null);
                                if (PhotoViewer.this.currentBitmap != null) {
                                    PhotoViewer.this.currentBitmap.recycle();
                                    Bitmap unused = PhotoViewer.this.currentBitmap = null;
                                }
                            }
                            boolean unused2 = PhotoViewer.this.switchingInlineMode = false;
                            if (Build.VERSION.SDK_INT >= 21) {
                                PhotoViewer.this.aspectRatioFrameLayout.getLocationInWindow(PhotoViewer.this.pipPosition);
                                int[] access$11700 = PhotoViewer.this.pipPosition;
                                access$11700[1] = (int) (((float) access$11700[1]) - PhotoViewer.this.containerView.getTranslationY());
                                PhotoViewer.this.textureImageView.setTranslationX(PhotoViewer.this.textureImageView.getTranslationX() + ((float) PhotoViewer.this.getLeftInset()));
                                PhotoViewer.this.videoTextureView.setTranslationX((PhotoViewer.this.videoTextureView.getTranslationX() + ((float) PhotoViewer.this.getLeftInset())) - PhotoViewer.this.aspectRatioFrameLayout.getX());
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_X, new float[]{(float) PhotoViewer.this.pipPosition[0]}), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_Y, new float[]{(float) PhotoViewer.this.pipPosition[1]}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_X, new float[]{((float) PhotoViewer.this.pipPosition[0]) - PhotoViewer.this.aspectRatioFrameLayout.getX()}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_Y, new float[]{((float) PhotoViewer.this.pipPosition[1]) - PhotoViewer.this.aspectRatioFrameLayout.getY()}), ObjectAnimator.ofInt(PhotoViewer.this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{255}), ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.groupedPhotosListView, View.ALPHA, new float[]{1.0f})});
                                animatorSet.setInterpolator(new DecelerateInterpolator());
                                animatorSet.setDuration(250);
                                animatorSet.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        boolean unused = PhotoViewer.this.pipAnimationInProgress = false;
                                    }
                                });
                                animatorSet.start();
                            }
                            int unused3 = PhotoViewer.this.waitingForFirstTextureUpload = 0;
                        }
                    }
                });
            }
            this.shouldSavePositionForCurrentVideo = null;
            this.lastSaveTime = 0;
            if (newPlayerCreated) {
                this.seekToProgressPending = this.seekToProgressPending2;
                this.videoPlayer.preparePlayer(uri, "other");
                this.videoPlayerSeekbar.setProgress(0.0f);
                this.videoTimelineView.setProgress(0.0f);
                this.videoPlayerSeekbar.setBufferedProgress(0.0f);
                this.videoPlayer.setPlayWhenReady(playWhenReady);
                MessageObject messageObject = this.currentMessageObject;
                if (messageObject != null) {
                    String name = messageObject.getFileName();
                    if (!TextUtils.isEmpty(name) && this.currentMessageObject.getDuration() >= 1200) {
                        if (this.currentMessageObject.forceSeekTo < 0.0f) {
                            float pos = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getFloat(name, -1.0f);
                            if (pos > 0.0f && pos < 0.999f) {
                                this.currentMessageObject.forceSeekTo = pos;
                                this.videoPlayerSeekbar.setProgress(pos);
                            }
                        }
                        this.shouldSavePositionForCurrentVideo = name;
                    }
                }
            }
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 != null && messageObject2.forceSeekTo >= 0.0f) {
                this.seekToProgressPending = this.currentMessageObject.forceSeekTo;
                this.currentMessageObject.forceSeekTo = -1.0f;
            }
            TLRPC.BotInlineResult botInlineResult = this.currentBotInlineResult;
            if (botInlineResult == null || (!botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) && !MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                this.bottomLayout.setPadding(0, 0, 0, 0);
            } else {
                this.bottomLayout.setVisibility(0);
                this.bottomLayout.setPadding(0, 0, AndroidUtilities.dp(84.0f), 0);
                this.pickerView.setVisibility(8);
            }
            FrameLayout frameLayout = this.videoPlayerControlFrameLayout;
            if (this.isCurrentVideo) {
                i = 8;
            }
            frameLayout.setVisibility(i);
            this.dateTextView.setVisibility(8);
            this.nameTextView.setVisibility(8);
            if (this.allowShare) {
                this.shareButton.setVisibility(8);
                this.menuItem.showSubItem(10);
            }
            this.inPreview = preview;
            updateAccessibilityOverlayVisibility();
        }
    }

    /* access modifiers changed from: private */
    public void releasePlayer(boolean onClose) {
        if (this.videoPlayer != null) {
            AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
            this.videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
            updateAccessibilityOverlayVisibility();
        }
        this.videoPreviewFrame.close();
        toggleMiniProgress(false, false);
        this.pipAvailable = false;
        this.playerInjected = false;
        if (this.pipItem.isEnabled()) {
            this.pipItem.setEnabled(false);
            this.pipItem.setAlpha(0.5f);
        }
        if (this.keepScreenOnFlagSet) {
            try {
                this.parentActivity.getWindow().clearFlags(128);
                this.keepScreenOnFlagSet = false;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout2 != null) {
            try {
                this.containerView.removeView(aspectRatioFrameLayout2);
            } catch (Throwable th) {
            }
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            if (!onClose) {
                this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            }
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        if (!onClose && !this.inPreview && !this.requestingPreview) {
            this.videoPlayerControlFrameLayout.setVisibility(8);
            this.dateTextView.setVisibility(0);
            this.nameTextView.setVisibility(0);
            if (this.allowShare) {
                this.shareButton.setVisibility(0);
                this.menuItem.hideSubItem(10);
            }
        }
    }

    private void updateCaptionTextForCurrentPhoto(Object object) {
        CharSequence caption = null;
        if (object instanceof MediaController.PhotoEntry) {
            caption = ((MediaController.PhotoEntry) object).caption;
        } else if (!(object instanceof TLRPC.BotInlineResult) && (object instanceof MediaController.SearchImage)) {
            caption = ((MediaController.SearchImage) object).caption;
        }
        if (TextUtils.isEmpty(caption)) {
            this.captionEditText.setFieldText("");
        } else {
            this.captionEditText.setFieldText(caption);
        }
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        ChatActivity chatActivity = this.parentChatActivity;
        photoViewerCaptionEnterView.setAllowTextEntitiesIntersection(chatActivity != null && (chatActivity.currentEncryptedChat == null || (this.parentChatActivity.currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(this.parentChatActivity.currentEncryptedChat.layer) >= 101)));
    }

    public void showAlertDialog(AlertDialog.Builder builder) {
        if (this.parentActivity != null) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                AlertDialog show = builder.show();
                this.visibleDialog = show;
                show.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        PhotoViewer.this.lambda$showAlertDialog$36$PhotoViewer(dialogInterface);
                    }
                });
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public /* synthetic */ void lambda$showAlertDialog$36$PhotoViewer(DialogInterface dialog) {
        this.visibleDialog = null;
    }

    private void applyCurrentEditMode() {
        TLRPC.PhotoSize size;
        View view;
        PhotoViewerProvider photoViewerProvider;
        View view2;
        Bitmap bitmap = null;
        ArrayList<TLRPC.InputDocument> stickers = null;
        MediaController.SavedFilterState savedFilterState = null;
        boolean removeSavedState = false;
        int i = this.currentEditMode;
        if (i == 1 || (i == 0 && this.sendPhotoType == 1)) {
            bitmap = this.photoCropView.getBitmap();
            removeSavedState = true;
        } else {
            int i2 = this.currentEditMode;
            if (i2 == 2) {
                bitmap = this.photoFilterView.getBitmap();
                savedFilterState = this.photoFilterView.getSavedFilterState();
            } else if (i2 == 3) {
                bitmap = this.photoPaintView.getBitmap();
                stickers = this.photoPaintView.getMasks();
                removeSavedState = true;
            }
        }
        if (bitmap != null && (size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101)) != null) {
            Object object = this.imagesArrLocals.get(this.currentIndex);
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry entry = (MediaController.PhotoEntry) object;
                entry.imagePath = FileLoader.getPathToAttach(size, true).toString();
                MediaController.PhotoEntry entry2 = entry;
                Object object2 = object;
                TLRPC.PhotoSize size2 = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(120.0f), (float) AndroidUtilities.dp(120.0f), 70, false, 101, 101);
                if (size2 != null) {
                    entry2.thumbPath = FileLoader.getPathToAttach(size2, true).toString();
                }
                if (stickers != null) {
                    entry2.stickers.addAll(stickers);
                }
                int i3 = this.currentEditMode;
                if (i3 == 1) {
                    this.cropItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
                    entry2.isCropped = true;
                } else if (i3 == 2) {
                    this.tuneItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
                    entry2.isFiltered = true;
                } else if (i3 == 3) {
                    this.paintItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
                    entry2.isPainted = true;
                }
                if (savedFilterState != null) {
                    entry2.savedFilterState = savedFilterState;
                    view2 = null;
                } else if (removeSavedState) {
                    view2 = null;
                    entry2.savedFilterState = null;
                } else {
                    view2 = null;
                }
                view = view2;
                Object obj = object2;
            } else {
                Object object3 = object;
                if (object3 instanceof MediaController.SearchImage) {
                    MediaController.SearchImage entry3 = (MediaController.SearchImage) object3;
                    entry3.imagePath = FileLoader.getPathToAttach(size, true).toString();
                    MediaController.SearchImage entry4 = entry3;
                    TLRPC.PhotoSize size3 = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(120.0f), (float) AndroidUtilities.dp(120.0f), 70, false, 101, 101);
                    if (size3 != null) {
                        entry4.thumbPath = FileLoader.getPathToAttach(size3, true).toString();
                    }
                    if (stickers != null) {
                        entry4.stickers.addAll(stickers);
                    }
                    int i4 = this.currentEditMode;
                    if (i4 == 1) {
                        this.cropItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
                        entry4.isCropped = true;
                    } else if (i4 == 2) {
                        this.tuneItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
                        entry4.isFiltered = true;
                    } else if (i4 == 3) {
                        this.paintItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
                        entry4.isPainted = true;
                    }
                    if (savedFilterState != null) {
                        entry4.savedFilterState = savedFilterState;
                        view = null;
                    } else if (removeSavedState) {
                        view = null;
                        entry4.savedFilterState = null;
                    } else {
                        view = null;
                    }
                } else {
                    view = null;
                }
            }
            int i5 = this.sendPhotoType;
            if ((i5 == 0 || i5 == 4) && (photoViewerProvider = this.placeProvider) != null) {
                photoViewerProvider.updatePhotoAtIndex(this.currentIndex);
                if (!this.placeProvider.isPhotoChecked(this.currentIndex)) {
                    setPhotoChecked();
                }
            }
            if (this.currentEditMode == 1) {
                float scaleX = this.photoCropView.getRectSizeX() / ((float) getContainerViewWidth());
                float scaleY = this.photoCropView.getRectSizeY() / ((float) getContainerViewHeight());
                this.scale = scaleX > scaleY ? scaleX : scaleY;
                this.translationX = (this.photoCropView.getRectX() + (this.photoCropView.getRectSizeX() / 2.0f)) - ((float) (getContainerViewWidth() / 2));
                this.translationY = (this.photoCropView.getRectY() + (this.photoCropView.getRectSizeY() / 2.0f)) - ((float) (getContainerViewHeight() / 2));
                this.zoomAnimation = true;
                this.applying = true;
                this.photoCropView.onDisappear();
            }
            this.centerImage.setParentView(view);
            this.centerImage.setOrientation(0, true);
            this.ignoreDidSetImage = true;
            this.centerImage.setImageBitmap(bitmap);
            this.ignoreDidSetImage = false;
            this.centerImage.setParentView(this.containerView);
            if (this.sendPhotoType == 1) {
                setCropBitmap();
            }
        }
    }

    private void setPhotoChecked() {
        ChatActivity chatActivity;
        TLRPC.Chat chat;
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider == null) {
            return;
        }
        if (photoViewerProvider.getSelectedPhotos() == null || this.maxSelectedPhotos <= 0 || this.placeProvider.getSelectedPhotos().size() < this.maxSelectedPhotos || this.placeProvider.isPhotoChecked(this.currentIndex)) {
            int num = this.placeProvider.setPhotoChecked(this.currentIndex, getCurrentVideoEditedInfo());
            boolean checked = this.placeProvider.isPhotoChecked(this.currentIndex);
            this.checkImageView.setChecked(checked, true);
            if (num >= 0) {
                if (checked) {
                    this.selectedPhotosAdapter.notifyItemInserted(num);
                    this.selectedPhotosListView.smoothScrollToPosition(num);
                } else {
                    this.selectedPhotosAdapter.notifyItemRemoved(num);
                }
            }
            updateSelectedCount();
        } else if (this.allowOrder && (chatActivity = this.parentChatActivity) != null && (chat = chatActivity.getCurrentChat()) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled) {
            AlertsCreator.createSimpleAlert(this.parentActivity, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSelectSendError", R.string.SlowmodeSelectSendError)).show();
        }
    }

    private void createCropView() {
        PhotoCropView photoCropView2 = this.photoCropView;
        if (photoCropView2 == null || photoCropView2.isFcCrop() != this.isFcCrop) {
            PhotoCropView photoCropView3 = new PhotoCropView(this.actvityContext, this.isFcCrop);
            this.photoCropView = photoCropView3;
            photoCropView3.setVisibility(8);
            this.containerView.addView(this.photoCropView, this.containerView.indexOfChild(this.pickerViewSendButton) - 1, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
            this.photoCropView.setDelegate(new PhotoCropView.PhotoCropViewDelegate() {
                public final void onChange(boolean z) {
                    PhotoViewer.this.lambda$createCropView$37$PhotoViewer(z);
                }
            });
        } else if (BuildVars.DEBUG_VERSION) {
            KLog.d(getClass().getSimpleName(), "不会重新构造裁剪样式");
        }
    }

    public /* synthetic */ void lambda$createCropView$37$PhotoViewer(boolean reset) {
        this.resetButton.setVisibility(reset ? 8 : 0);
    }

    private void switchToEditMode(int mode) {
        Bitmap bitmap;
        final int i = mode;
        if (this.currentEditMode == i || this.centerImage.getBitmap() == null || this.changeModeAnimation != null || this.imageMoveAnimation != null || this.photoProgressViews[0].backgroundState != -1 || this.captionEditText.getTag() != null) {
            return;
        }
        if (i == 0) {
            if (this.centerImage.getBitmap() != null) {
                int bitmapWidth = this.centerImage.getBitmapWidth();
                int bitmapHeight = this.centerImage.getBitmapHeight();
                float scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                float scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                float newScaleX = ((float) getContainerViewWidth(0)) / ((float) bitmapWidth);
                float newScaleY = ((float) getContainerViewHeight(0)) / ((float) bitmapHeight);
                float scale2 = scaleX > scaleY ? scaleY : scaleX;
                float newScale = newScaleX > newScaleY ? newScaleY : newScaleX;
                if (this.sendPhotoType == 1) {
                    setCropTranslations(true);
                } else {
                    this.animateToScale = newScale / scale2;
                    this.animateToX = 0.0f;
                    this.translationX = (float) ((getLeftInset() / 2) - (getRightInset() / 2));
                    int i2 = this.currentEditMode;
                    if (i2 == 1) {
                        this.animateToY = (float) AndroidUtilities.dp(58.0f);
                    } else if (i2 == 2) {
                        this.animateToY = (float) AndroidUtilities.dp(92.0f);
                    } else if (i2 == 3) {
                        this.animateToY = (float) AndroidUtilities.dp(44.0f);
                    }
                    if (Build.VERSION.SDK_INT >= 21) {
                        this.animateToY -= (float) (AndroidUtilities.statusBarHeight / 2);
                    }
                    int i3 = bitmapWidth;
                    this.animationStartTime = System.currentTimeMillis();
                    this.zoomAnimation = true;
                }
            }
            this.padImageForHorizontalInsets = false;
            this.imageMoveAnimation = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>(4);
            int i4 = this.currentEditMode;
            if (i4 == 1) {
                animators.add(ObjectAnimator.ofFloat(this.editorDoneLayout, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(48.0f)}));
                animators.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, new float[]{0.0f}));
            } else if (i4 == 2) {
                this.photoFilterView.shutdown();
                animators.add(ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(186.0f)}));
                animators.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}));
            } else if (i4 == 3) {
                this.photoPaintView.shutdown();
                animators.add(ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(126.0f)}));
                animators.add(ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(126.0f)}));
                animators.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}));
            }
            this.imageMoveAnimation.playTogether(animators);
            this.imageMoveAnimation.setDuration(200);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PhotoViewer.this.currentEditMode == 1) {
                        PhotoViewer.this.photoCropView.onDisappear();
                        PhotoViewer.this.editorDoneLayout.setVisibility(8);
                        PhotoViewer.this.photoCropView.setVisibility(8);
                    } else if (PhotoViewer.this.currentEditMode == 2) {
                        try {
                            PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        PhotoFilterView unused = PhotoViewer.this.photoFilterView = null;
                    } else if (PhotoViewer.this.currentEditMode == 3) {
                        try {
                            PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoPaintView);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        PhotoPaintView unused2 = PhotoViewer.this.photoPaintView = null;
                    }
                    AnimatorSet unused3 = PhotoViewer.this.imageMoveAnimation = null;
                    int unused4 = PhotoViewer.this.currentEditMode = i;
                    boolean unused5 = PhotoViewer.this.applying = false;
                    if (PhotoViewer.this.sendPhotoType != 1) {
                        float unused6 = PhotoViewer.this.animateToScale = 1.0f;
                        float unused7 = PhotoViewer.this.animateToX = 0.0f;
                        float unused8 = PhotoViewer.this.animateToY = 0.0f;
                        float unused9 = PhotoViewer.this.scale = 1.0f;
                    }
                    PhotoViewer photoViewer = PhotoViewer.this;
                    photoViewer.updateMinMax(photoViewer.scale);
                    PhotoViewer.this.containerView.invalidate();
                    AnimatorSet animatorSet = new AnimatorSet();
                    ArrayList<Animator> arrayList = new ArrayList<>();
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, View.TRANSLATION_Y, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, View.TRANSLATION_Y, new float[]{0.0f}));
                    if (PhotoViewer.this.sendPhotoType != 1) {
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, View.TRANSLATION_Y, new float[]{0.0f}));
                    }
                    if (PhotoViewer.this.needCaptionLayout) {
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, View.TRANSLATION_Y, new float[]{0.0f}));
                    }
                    if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4) {
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, View.ALPHA, new float[]{1.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.photosCounterView, View.ALPHA, new float[]{1.0f}));
                    } else if (PhotoViewer.this.sendPhotoType == 1) {
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, View.ALPHA, new float[]{1.0f}));
                    }
                    if (PhotoViewer.this.mShowNeedAddMorePicButton && PhotoViewer.this.cameraItem.getTag() != null) {
                        PhotoViewer.this.cameraItem.setVisibility(0);
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.cameraItem, View.ALPHA, new float[]{1.0f}));
                    }
                    animatorSet.playTogether(arrayList);
                    animatorSet.setDuration(200);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                            PhotoViewer.this.pickerView.setVisibility(0);
                            PhotoViewer.this.pickerViewSendButton.setVisibility(0);
                            PhotoViewer.this.actionBar.setVisibility(0);
                            if (PhotoViewer.this.needCaptionLayout) {
                                PhotoViewer.this.captionTextView.setVisibility(PhotoViewer.this.captionTextView.getTag() != null ? 0 : 4);
                            }
                            if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                PhotoViewer.this.checkImageView.setVisibility(0);
                                PhotoViewer.this.photosCounterView.setVisibility(0);
                            } else if (PhotoViewer.this.sendPhotoType == 1) {
                                PhotoViewer.this.setCropTranslations(false);
                            }
                        }
                    });
                    animatorSet.start();
                }
            });
            this.imageMoveAnimation.start();
        } else if (i == 1) {
            createCropView();
            this.photoCropView.onAppear();
            this.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", R.string.Crop));
            this.editorDoneLayout.doneButton.setTextColor(-11420173);
            this.changeModeAnimation = new AnimatorSet();
            ArrayList<Animator> arrayList = new ArrayList<>();
            arrayList.add(ObjectAnimator.ofFloat(this.pickerView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            arrayList.add(ObjectAnimator.ofFloat(this.pickerViewSendButton, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            arrayList.add(ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, new float[]{0.0f, (float) (-this.actionBar.getHeight())}));
            if (this.needCaptionLayout) {
                arrayList.add(ObjectAnimator.ofFloat(this.captionTextView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            }
            int i5 = this.sendPhotoType;
            if (i5 == 0 || i5 == 4) {
                arrayList.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, new float[]{1.0f, 0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, new float[]{1.0f, 0.0f}));
            }
            if (this.selectedPhotosListView.getVisibility() == 0) {
                arrayList.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, new float[]{1.0f, 0.0f}));
            }
            if (this.mShowNeedAddMorePicButton && this.cameraItem.getTag() != null) {
                arrayList.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, new float[]{1.0f, 0.0f}));
            }
            this.changeModeAnimation.playTogether(arrayList);
            this.changeModeAnimation.setDuration(200);
            this.changeModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = PhotoViewer.this.changeModeAnimation = null;
                    PhotoViewer.this.pickerView.setVisibility(8);
                    PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                    PhotoViewer.this.cameraItem.setVisibility(8);
                    PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                    PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                    PhotoViewer.this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                    PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                    PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                    boolean unused2 = PhotoViewer.this.isPhotosListViewVisible = false;
                    if (PhotoViewer.this.needCaptionLayout) {
                        PhotoViewer.this.captionTextView.setVisibility(4);
                    }
                    if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                        PhotoViewer.this.checkImageView.setVisibility(8);
                        PhotoViewer.this.photosCounterView.setVisibility(8);
                    }
                    Bitmap bitmap = PhotoViewer.this.centerImage.getBitmap();
                    if (bitmap != null) {
                        PhotoViewer.this.photoCropView.setBitmap(bitmap, PhotoViewer.this.centerImage.getOrientation(), PhotoViewer.this.sendPhotoType != 1, false);
                        PhotoViewer.this.photoCropView.onDisappear();
                        int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                        int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                        float scaleX = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                        float scaleY = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                        float newScaleX = ((float) PhotoViewer.this.getContainerViewWidth(1)) / ((float) bitmapWidth);
                        float newScaleY = ((float) PhotoViewer.this.getContainerViewHeight(1)) / ((float) bitmapHeight);
                        float scale = scaleX > scaleY ? scaleY : scaleX;
                        float newScale = newScaleX > newScaleY ? newScaleY : newScaleX;
                        if (PhotoViewer.this.sendPhotoType == 1) {
                            float minSide = (float) Math.min(PhotoViewer.this.getContainerViewWidth(1), PhotoViewer.this.getContainerViewHeight(1));
                            float newScaleX2 = minSide / ((float) bitmapWidth);
                            float newScaleY2 = minSide / ((float) bitmapHeight);
                            newScale = newScaleX2 > newScaleY2 ? newScaleX2 : newScaleY2;
                        }
                        float unused3 = PhotoViewer.this.animateToScale = newScale / scale;
                        PhotoViewer photoViewer = PhotoViewer.this;
                        float unused4 = photoViewer.animateToX = (float) ((photoViewer.getLeftInset() / 2) - (PhotoViewer.this.getRightInset() / 2));
                        float unused5 = PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(56.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                        long unused6 = PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                        boolean unused7 = PhotoViewer.this.zoomAnimation = true;
                    }
                    AnimatorSet unused8 = PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                    PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.editorDoneLayout, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(48.0f), 0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, View.ALPHA, new float[]{0.0f, 1.0f})});
                    PhotoViewer.this.imageMoveAnimation.setDuration(200);
                    PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                            PhotoViewer.this.editorDoneLayout.setVisibility(0);
                            PhotoViewer.this.photoCropView.setVisibility(0);
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.photoCropView.onAppeared();
                            AnimatorSet unused = PhotoViewer.this.imageMoveAnimation = null;
                            int unused2 = PhotoViewer.this.currentEditMode = i;
                            float unused3 = PhotoViewer.this.animateToScale = 1.0f;
                            float unused4 = PhotoViewer.this.animateToX = 0.0f;
                            float unused5 = PhotoViewer.this.animateToY = 0.0f;
                            float unused6 = PhotoViewer.this.scale = 1.0f;
                            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                            boolean unused7 = PhotoViewer.this.padImageForHorizontalInsets = true;
                            PhotoViewer.this.containerView.invalidate();
                        }
                    });
                    PhotoViewer.this.imageMoveAnimation.start();
                }
            });
            this.changeModeAnimation.start();
        } else if (i == 2) {
            if (this.photoFilterView == null) {
                MediaController.SavedFilterState state = null;
                String originalPath = null;
                int orientation = 0;
                if (!this.imagesArrLocals.isEmpty()) {
                    Object object = this.imagesArrLocals.get(this.currentIndex);
                    if (object instanceof MediaController.PhotoEntry) {
                        MediaController.PhotoEntry entry = (MediaController.PhotoEntry) object;
                        if (entry.imagePath == null) {
                            originalPath = entry.path;
                            state = entry.savedFilterState;
                        }
                        orientation = entry.orientation;
                    } else if (object instanceof MediaController.SearchImage) {
                        MediaController.SearchImage entry2 = (MediaController.SearchImage) object;
                        state = entry2.savedFilterState;
                        originalPath = entry2.imageUrl;
                    }
                }
                if (state == null) {
                    bitmap = this.centerImage.getBitmap();
                    orientation = this.centerImage.getOrientation();
                } else {
                    bitmap = BitmapFactory.decodeFile(originalPath);
                }
                PhotoFilterView photoFilterView2 = new PhotoFilterView(this.parentActivity, bitmap, orientation, state);
                this.photoFilterView = photoFilterView2;
                this.containerView.addView(photoFilterView2, LayoutHelper.createFrame(-1, -1.0f));
                this.photoFilterView.getDoneTextView().setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        PhotoViewer.this.lambda$switchToEditMode$38$PhotoViewer(view);
                    }
                });
                this.photoFilterView.getCancelTextView().setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        PhotoViewer.this.lambda$switchToEditMode$40$PhotoViewer(view);
                    }
                });
                this.photoFilterView.getToolsView().setTranslationY((float) AndroidUtilities.dp(186.0f));
            }
            this.changeModeAnimation = new AnimatorSet();
            ArrayList<Animator> arrayList2 = new ArrayList<>();
            arrayList2.add(ObjectAnimator.ofFloat(this.pickerView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            arrayList2.add(ObjectAnimator.ofFloat(this.pickerViewSendButton, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            arrayList2.add(ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, new float[]{0.0f, (float) (-this.actionBar.getHeight())}));
            int i6 = this.sendPhotoType;
            if (i6 == 0 || i6 == 4) {
                arrayList2.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, new float[]{1.0f, 0.0f}));
                arrayList2.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, new float[]{1.0f, 0.0f}));
            } else if (i6 == 1) {
                arrayList2.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, new float[]{1.0f, 0.0f}));
            }
            if (this.selectedPhotosListView.getVisibility() == 0) {
                arrayList2.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, new float[]{1.0f, 0.0f}));
            }
            if (this.mShowNeedAddMorePicButton && this.cameraItem.getTag() != null) {
                arrayList2.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, new float[]{1.0f, 0.0f}));
            }
            this.changeModeAnimation.playTogether(arrayList2);
            this.changeModeAnimation.setDuration(200);
            this.changeModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = PhotoViewer.this.changeModeAnimation = null;
                    PhotoViewer.this.pickerView.setVisibility(8);
                    PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                    PhotoViewer.this.actionBar.setVisibility(8);
                    PhotoViewer.this.cameraItem.setVisibility(8);
                    PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                    PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                    PhotoViewer.this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                    PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                    PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                    boolean unused2 = PhotoViewer.this.isPhotosListViewVisible = false;
                    if (PhotoViewer.this.needCaptionLayout) {
                        PhotoViewer.this.captionTextView.setVisibility(4);
                    }
                    if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                        PhotoViewer.this.checkImageView.setVisibility(8);
                        PhotoViewer.this.photosCounterView.setVisibility(8);
                    }
                    if (PhotoViewer.this.centerImage.getBitmap() != null) {
                        int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                        int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                        float scaleX = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                        float scaleY = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                        float newScaleX = ((float) PhotoViewer.this.getContainerViewWidth(2)) / ((float) bitmapWidth);
                        float newScaleY = ((float) PhotoViewer.this.getContainerViewHeight(2)) / ((float) bitmapHeight);
                        float unused3 = PhotoViewer.this.animateToScale = (newScaleX > newScaleY ? newScaleY : newScaleX) / (scaleX > scaleY ? scaleY : scaleX);
                        PhotoViewer photoViewer = PhotoViewer.this;
                        float unused4 = photoViewer.animateToX = (float) ((photoViewer.getLeftInset() / 2) - (PhotoViewer.this.getRightInset() / 2));
                        float unused5 = PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(92.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                        long unused6 = PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                        boolean unused7 = PhotoViewer.this.zoomAnimation = true;
                    }
                    AnimatorSet unused8 = PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                    PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(186.0f), 0.0f})});
                    PhotoViewer.this.imageMoveAnimation.setDuration(200);
                    PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.photoFilterView.init();
                            AnimatorSet unused = PhotoViewer.this.imageMoveAnimation = null;
                            int unused2 = PhotoViewer.this.currentEditMode = i;
                            float unused3 = PhotoViewer.this.animateToScale = 1.0f;
                            float unused4 = PhotoViewer.this.animateToX = 0.0f;
                            float unused5 = PhotoViewer.this.animateToY = 0.0f;
                            float unused6 = PhotoViewer.this.scale = 1.0f;
                            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                            boolean unused7 = PhotoViewer.this.padImageForHorizontalInsets = true;
                            PhotoViewer.this.containerView.invalidate();
                            if (PhotoViewer.this.sendPhotoType == 1) {
                                PhotoViewer.this.photoCropView.reset();
                            }
                        }
                    });
                    PhotoViewer.this.imageMoveAnimation.start();
                }
            });
            this.changeModeAnimation.start();
        } else if (i == 3) {
            if (this.photoPaintView == null) {
                PhotoPaintView photoPaintView2 = new PhotoPaintView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
                this.photoPaintView = photoPaintView2;
                this.containerView.addView(photoPaintView2, LayoutHelper.createFrame(-1, -1.0f));
                this.photoPaintView.getDoneTextView().setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        PhotoViewer.this.lambda$switchToEditMode$41$PhotoViewer(view);
                    }
                });
                this.photoPaintView.getCancelTextView().setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        PhotoViewer.this.lambda$switchToEditMode$43$PhotoViewer(view);
                    }
                });
                this.photoPaintView.getColorPicker().setTranslationY((float) AndroidUtilities.dp(126.0f));
                this.photoPaintView.getToolsView().setTranslationY((float) AndroidUtilities.dp(126.0f));
            }
            this.changeModeAnimation = new AnimatorSet();
            ArrayList<Animator> arrayList3 = new ArrayList<>();
            arrayList3.add(ObjectAnimator.ofFloat(this.pickerView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            arrayList3.add(ObjectAnimator.ofFloat(this.pickerViewSendButton, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            arrayList3.add(ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, new float[]{0.0f, (float) (-this.actionBar.getHeight())}));
            if (this.needCaptionLayout) {
                arrayList3.add(ObjectAnimator.ofFloat(this.captionTextView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(96.0f)}));
            }
            int i7 = this.sendPhotoType;
            if (i7 == 0 || i7 == 4) {
                arrayList3.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, new float[]{1.0f, 0.0f}));
                arrayList3.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, new float[]{1.0f, 0.0f}));
            } else if (i7 == 1) {
                arrayList3.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, new float[]{1.0f, 0.0f}));
            }
            if (this.selectedPhotosListView.getVisibility() == 0) {
                arrayList3.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, new float[]{1.0f, 0.0f}));
            }
            if (this.mShowNeedAddMorePicButton && this.cameraItem.getTag() != null) {
                arrayList3.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, new float[]{1.0f, 0.0f}));
            }
            this.changeModeAnimation.playTogether(arrayList3);
            this.changeModeAnimation.setDuration(200);
            this.changeModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = PhotoViewer.this.changeModeAnimation = null;
                    PhotoViewer.this.pickerView.setVisibility(8);
                    PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                    PhotoViewer.this.cameraItem.setVisibility(8);
                    PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                    PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                    PhotoViewer.this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                    PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                    PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                    boolean unused2 = PhotoViewer.this.isPhotosListViewVisible = false;
                    if (PhotoViewer.this.needCaptionLayout) {
                        PhotoViewer.this.captionTextView.setVisibility(4);
                    }
                    if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                        PhotoViewer.this.checkImageView.setVisibility(8);
                        PhotoViewer.this.photosCounterView.setVisibility(8);
                    }
                    if (PhotoViewer.this.centerImage.getBitmap() != null) {
                        int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                        int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                        float scaleX = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                        float scaleY = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                        float newScaleX = ((float) PhotoViewer.this.getContainerViewWidth(3)) / ((float) bitmapWidth);
                        float newScaleY = ((float) PhotoViewer.this.getContainerViewHeight(3)) / ((float) bitmapHeight);
                        float unused3 = PhotoViewer.this.animateToScale = (newScaleX > newScaleY ? newScaleY : newScaleX) / (scaleX > scaleY ? scaleY : scaleX);
                        PhotoViewer photoViewer = PhotoViewer.this;
                        float unused4 = photoViewer.animateToX = (float) ((photoViewer.getLeftInset() / 2) - (PhotoViewer.this.getRightInset() / 2));
                        float unused5 = PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(44.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                        long unused6 = PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                        boolean unused7 = PhotoViewer.this.zoomAnimation = true;
                    }
                    AnimatorSet unused8 = PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                    PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getColorPicker(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getToolsView(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f})});
                    PhotoViewer.this.imageMoveAnimation.setDuration(200);
                    PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                        }

                        public void onAnimationEnd(Animator animation) {
                            PhotoViewer.this.photoPaintView.init();
                            AnimatorSet unused = PhotoViewer.this.imageMoveAnimation = null;
                            int unused2 = PhotoViewer.this.currentEditMode = i;
                            float unused3 = PhotoViewer.this.animateToScale = 1.0f;
                            float unused4 = PhotoViewer.this.animateToX = 0.0f;
                            float unused5 = PhotoViewer.this.animateToY = 0.0f;
                            float unused6 = PhotoViewer.this.scale = 1.0f;
                            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                            boolean unused7 = PhotoViewer.this.padImageForHorizontalInsets = true;
                            PhotoViewer.this.containerView.invalidate();
                            if (PhotoViewer.this.sendPhotoType == 1) {
                                PhotoViewer.this.photoCropView.reset();
                            }
                        }
                    });
                    PhotoViewer.this.imageMoveAnimation.start();
                }
            });
            this.changeModeAnimation.start();
        }
    }

    public /* synthetic */ void lambda$switchToEditMode$38$PhotoViewer(View v) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$40$PhotoViewer(View v) {
        if (this.photoFilterView.hasChanges()) {
            Activity activity = this.parentActivity;
            if (activity != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                builder.setMessage(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PhotoViewer.this.lambda$null$39$PhotoViewer(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showAlertDialog(builder);
                return;
            }
            return;
        }
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$null$39$PhotoViewer(DialogInterface dialogInterface, int i) {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$41$PhotoViewer(View v) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$null$42$PhotoViewer() {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$43$PhotoViewer(View v) {
        this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() {
            public final void run() {
                PhotoViewer.this.lambda$null$42$PhotoViewer();
            }
        });
    }

    private void toggleCheckImageView(boolean show) {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> arrayList = new ArrayList<>();
        FrameLayout frameLayout = this.pickerView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
        ImageView imageView = this.pickerViewSendButton;
        Property property2 = View.ALPHA;
        float[] fArr2 = new float[1];
        fArr2[0] = show ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(imageView, property2, fArr2));
        if (this.needCaptionLayout) {
            TextView textView = this.captionTextView;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(textView, property3, fArr3));
        }
        int i = this.sendPhotoType;
        if (i == 0 || i == 4) {
            CheckBox checkBox = this.checkImageView;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(checkBox, property4, fArr4));
            CounterView counterView = this.photosCounterView;
            Property property5 = View.ALPHA;
            float[] fArr5 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr5[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(counterView, property5, fArr5));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void toggleMiniProgressInternal(final boolean show) {
        if (show) {
            this.miniProgressView.setVisibility(0);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.miniProgressAnimator = animatorSet;
        Animator[] animatorArr = new Animator[1];
        RadialProgressView radialProgressView = this.miniProgressView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        fArr[0] = show ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, property, fArr);
        animatorSet.playTogether(animatorArr);
        this.miniProgressAnimator.setDuration(200);
        this.miniProgressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PhotoViewer.this.miniProgressAnimator)) {
                    if (!show) {
                        PhotoViewer.this.miniProgressView.setVisibility(4);
                    }
                    AnimatorSet unused = PhotoViewer.this.miniProgressAnimator = null;
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (animation.equals(PhotoViewer.this.miniProgressAnimator)) {
                    AnimatorSet unused = PhotoViewer.this.miniProgressAnimator = null;
                }
            }
        });
        this.miniProgressAnimator.start();
    }

    private void toggleMiniProgress(boolean show, boolean animated) {
        int i = 0;
        if (animated) {
            toggleMiniProgressInternal(show);
            if (show) {
                AnimatorSet animatorSet = this.miniProgressAnimator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.miniProgressAnimator = null;
                }
                AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
                if (this.firstAnimationDelay) {
                    this.firstAnimationDelay = false;
                    toggleMiniProgressInternal(true);
                    return;
                }
                AndroidUtilities.runOnUIThread(this.miniProgressShowRunnable, 500);
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
            AnimatorSet animatorSet2 = this.miniProgressAnimator;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                toggleMiniProgressInternal(false);
                return;
            }
            return;
        }
        AnimatorSet animatorSet3 = this.miniProgressAnimator;
        if (animatorSet3 != null) {
            animatorSet3.cancel();
            this.miniProgressAnimator = null;
        }
        this.miniProgressView.setAlpha(show ? 1.0f : 0.0f);
        RadialProgressView radialProgressView = this.miniProgressView;
        if (!show) {
            i = 4;
        }
        radialProgressView.setVisibility(i);
    }

    private void toggleActionBar(final boolean show, boolean animated) {
        AnimatorSet animatorSet = this.actionBarAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (show) {
            this.actionBar.setVisibility(0);
            if (this.bottomLayout.getTag() != null) {
                this.bottomLayout.setVisibility(0);
            }
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(0);
                VideoSeekPreviewImage videoSeekPreviewImage = this.videoPreviewFrame;
                if (videoSeekPreviewImage != null) {
                    videoSeekPreviewImage.requestLayout();
                }
            }
        }
        this.isActionBarVisible = show;
        if (Build.VERSION.SDK_INT >= 21 && this.sendPhotoType != 1) {
            int flags = 4 | ((this.containerView.getPaddingLeft() > 0 || this.containerView.getPaddingRight() > 0) ? 4098 : 0);
            if (show) {
                FrameLayoutDrawer frameLayoutDrawer = this.containerView;
                frameLayoutDrawer.setSystemUiVisibility(frameLayoutDrawer.getSystemUiVisibility() & (~flags));
            } else {
                FrameLayoutDrawer frameLayoutDrawer2 = this.containerView;
                frameLayoutDrawer2.setSystemUiVisibility(frameLayoutDrawer2.getSystemUiVisibility() | flags);
            }
        }
        int flags2 = 1065353216;
        if (animated) {
            ArrayList<Animator> arrayList = new ArrayList<>();
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar2, property, fArr));
            FrameLayout frameLayout = this.bottomLayout;
            if (frameLayout != null) {
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = show ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(frameLayout, property2, fArr2));
            }
            GroupedPhotosListView groupedPhotosListView2 = this.groupedPhotosListView;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView2, property3, fArr3));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                if (!show) {
                    flags2 = 0;
                }
                fArr4[0] = flags2;
                arrayList.add(ObjectAnimator.ofFloat(textView, property4, fArr4));
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimator = animatorSet2;
            animatorSet2.playTogether(arrayList);
            this.actionBarAnimator.setDuration(200);
            this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(PhotoViewer.this.actionBarAnimator)) {
                        if (!show) {
                            PhotoViewer.this.actionBar.setVisibility(4);
                            if (PhotoViewer.this.bottomLayout.getTag() != null) {
                                PhotoViewer.this.bottomLayout.setVisibility(4);
                            }
                            if (PhotoViewer.this.captionTextView.getTag() != null) {
                                PhotoViewer.this.captionTextView.setVisibility(4);
                            }
                        }
                        AnimatorSet unused = PhotoViewer.this.actionBarAnimator = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(PhotoViewer.this.actionBarAnimator)) {
                        AnimatorSet unused = PhotoViewer.this.actionBarAnimator = null;
                    }
                }
            });
            this.actionBarAnimator.start();
            return;
        }
        this.actionBar.setAlpha(show ? 1.0f : 0.0f);
        this.bottomLayout.setAlpha(show ? 1.0f : 0.0f);
        this.groupedPhotosListView.setAlpha(show ? 1.0f : 0.0f);
        TextView textView2 = this.captionTextView;
        if (!show) {
            flags2 = 0;
        }
        textView2.setAlpha(flags2);
    }

    private void togglePhotosListView(boolean show, boolean animated) {
        if (show != this.isPhotosListViewVisible) {
            if (show) {
                this.selectedPhotosListView.setVisibility(0);
            }
            this.isPhotosListViewVisible = show;
            this.selectedPhotosListView.setEnabled(show);
            float f = 1.0f;
            if (animated) {
                ArrayList<Animator> arrayList = new ArrayList<>();
                RecyclerListView recyclerListView = this.selectedPhotosListView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView, property, fArr));
                RecyclerListView recyclerListView2 = this.selectedPhotosListView;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[1];
                fArr2[0] = show ? 0.0f : (float) (-AndroidUtilities.dp(10.0f));
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView2, property2, fArr2));
                CounterView counterView = this.photosCounterView;
                Property property3 = View.ROTATION_X;
                float[] fArr3 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(counterView, property3, fArr3));
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentListViewAnimation = animatorSet;
                animatorSet.playTogether(arrayList);
                if (!show) {
                    this.currentListViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (PhotoViewer.this.currentListViewAnimation != null && PhotoViewer.this.currentListViewAnimation.equals(animation)) {
                                PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                                AnimatorSet unused = PhotoViewer.this.currentListViewAnimation = null;
                            }
                        }
                    });
                }
                this.currentListViewAnimation.setDuration(200);
                this.currentListViewAnimation.start();
                return;
            }
            this.selectedPhotosListView.setAlpha(show ? 1.0f : 0.0f);
            this.selectedPhotosListView.setTranslationY(show ? 0.0f : (float) (-AndroidUtilities.dp(10.0f)));
            CounterView counterView2 = this.photosCounterView;
            if (!show) {
                f = 0.0f;
            }
            counterView2.setRotationX(f);
            if (!show) {
                this.selectedPhotosListView.setVisibility(8);
            }
        }
    }

    private String getFileName(int index) {
        ImageLocation location;
        if (index < 0) {
            return null;
        }
        if (this.secureDocuments.isEmpty()) {
            if (!this.imagesArrLocations.isEmpty() || !this.imagesArr.isEmpty()) {
                if (!this.imagesArrLocations.isEmpty()) {
                    if (index >= this.imagesArrLocations.size() || (location = this.imagesArrLocations.get(index)) == null) {
                        return null;
                    }
                    return location.location.volume_id + "_" + location.location.local_id + ".jpg";
                } else if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
                    return null;
                } else {
                    return FileLoader.getMessageFileName(this.imagesArr.get(index).messageOwner);
                }
            } else if (this.imagesArrLocals.isEmpty() || index >= this.imagesArrLocals.size()) {
                return null;
            } else {
                Object object = this.imagesArrLocals.get(index);
                if (object instanceof MediaController.SearchImage) {
                    return ((MediaController.SearchImage) object).getAttachName();
                }
                if (object instanceof TLRPC.BotInlineResult) {
                    TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) object;
                    if (botInlineResult.document != null) {
                        return FileLoader.getAttachFileName(botInlineResult.document);
                    }
                    if (botInlineResult.photo != null) {
                        return FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize()));
                    }
                    if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                        return Utilities.MD5(botInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content.url, FileLoader.getMimeTypePart(botInlineResult.content.mime_type));
                    }
                }
            }
            return null;
        } else if (index >= this.secureDocuments.size()) {
            return null;
        } else {
            SecureDocument location2 = this.secureDocuments.get(index);
            return location2.secureFile.dc_id + "_" + location2.secureFile.id + ".jpg";
        }
    }

    private ImageLocation getImageLocation(int index, int[] size) {
        if (index < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (index >= this.secureDocuments.size()) {
                return null;
            }
            if (size != null) {
                size[0] = this.secureDocuments.get(index).secureFile.size;
            }
            return ImageLocation.getForSecureDocument(this.secureDocuments.get(index));
        } else if (!this.imagesArrLocations.isEmpty()) {
            if (index >= this.imagesArrLocations.size()) {
                return null;
            }
            if (size != null) {
                size[0] = this.imagesArrLocationsSizes.get(index).intValue();
            }
            return this.imagesArrLocations.get(index);
        } else if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
            return null;
        } else {
            MessageObject message = this.imagesArr.get(index);
            if (message.messageOwner instanceof TLRPC.TL_messageService) {
                if (message.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    return null;
                }
                TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    if (size != null) {
                        size[0] = sizeFull.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return ImageLocation.getForObject(sizeFull, message.photoThumbsObject);
                } else if (size != null) {
                    size[0] = -1;
                }
            } else if ((!(message.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || message.messageOwner.media.photo == null) && (!(message.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || message.messageOwner.media.webpage == null)) {
                if (message.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
                    return ImageLocation.getForWebFile(WebFile.createWithWebDocument(((TLRPC.TL_messageMediaInvoice) message.messageOwner.media).photo));
                }
                if (message.getDocument() != null && MessageObject.isDocumentHasThumb(message.getDocument())) {
                    TLRPC.Document document = message.getDocument();
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                    if (size != null) {
                        size[0] = thumb.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return ImageLocation.getForDocument(thumb, document);
                }
            } else if (message.isGif()) {
                return ImageLocation.getForDocument(message.getDocument());
            } else {
                TLRPC.PhotoSize sizeFull2 = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull2 != null) {
                    if (size != null) {
                        size[0] = sizeFull2.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return ImageLocation.getForObject(sizeFull2, message.photoThumbsObject);
                } else if (size != null) {
                    size[0] = -1;
                }
            }
            return null;
        }
    }

    /* access modifiers changed from: private */
    public TLObject getFileLocation(int index, int[] size) {
        if (index < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (index >= this.secureDocuments.size()) {
                return null;
            }
            if (size != null) {
                size[0] = this.secureDocuments.get(index).secureFile.size;
            }
            return this.secureDocuments.get(index);
        } else if (!this.imagesArrLocations.isEmpty()) {
            if (index >= this.imagesArrLocations.size()) {
                return null;
            }
            if (size != null) {
                size[0] = this.imagesArrLocationsSizes.get(index).intValue();
            }
            return this.imagesArrLocations.get(index).location;
        } else if (this.imagesArr.isEmpty() || index >= this.imagesArr.size()) {
            return null;
        } else {
            MessageObject message = this.imagesArr.get(index);
            if (message.messageOwner instanceof TLRPC.TL_messageService) {
                if (message.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    return message.messageOwner.action.newUserPhoto.photo_big;
                }
                TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    if (size != null) {
                        size[0] = sizeFull.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return sizeFull;
                } else if (size != null) {
                    size[0] = -1;
                }
            } else if (((message.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && message.messageOwner.media.photo != null) || ((message.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && message.messageOwner.media.webpage != null)) {
                TLRPC.PhotoSize sizeFull2 = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
                if (sizeFull2 != null) {
                    if (size != null) {
                        size[0] = sizeFull2.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return sizeFull2;
                } else if (size != null) {
                    size[0] = -1;
                }
            } else if (message.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
                return ((TLRPC.TL_messageMediaInvoice) message.messageOwner.media).photo;
            } else {
                if (message.getDocument() != null && MessageObject.isDocumentHasThumb(message.getDocument())) {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(message.getDocument().thumbs, 90);
                    if (size != null) {
                        size[0] = thumb.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return thumb;
                }
            }
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectedCount() {
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider != null) {
            int count = photoViewerProvider.getSelectedCount();
            this.photosCounterView.setCount(count);
            if (count == 0) {
                togglePhotosListView(false, true);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:224:0x05bd  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x05f0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onPhotoShow(im.bclpbkiauv.messenger.MessageObject r25, im.bclpbkiauv.tgnet.TLRPC.FileLocation r26, im.bclpbkiauv.messenger.ImageLocation r27, java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r28, java.util.ArrayList<im.bclpbkiauv.messenger.SecureDocument> r29, java.util.ArrayList<java.lang.Object> r30, int r31, im.bclpbkiauv.ui.PhotoViewer.PlaceProviderObject r32) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            r2 = r28
            r3 = r29
            r4 = r30
            r5 = r31
            r6 = r32
            int r7 = im.bclpbkiauv.tgnet.ConnectionsManager.generateClassGuid()
            r0.classGuid = r7
            r7 = 0
            r0.currentMessageObject = r7
            r0.currentFileLocation = r7
            r0.currentSecureDocument = r7
            r0.currentPathObject = r7
            r8 = 0
            r0.fromCamera = r8
            r0.currentBotInlineResult = r7
            r9 = -1
            r0.currentIndex = r9
            java.lang.String[] r10 = r0.currentFileNames
            r10[r8] = r7
            r11 = 1
            java.lang.Integer r12 = java.lang.Integer.valueOf(r11)
            r10[r11] = r7
            r13 = 2
            r10[r13] = r7
            r0.avatarsDialogId = r8
            r0.totalImagesCount = r8
            r0.totalImagesCountMerge = r8
            r0.currentEditMode = r8
            r0.isFirstLoading = r11
            r0.needSearchImageInArr = r8
            r0.loadingMoreImages = r8
            boolean[] r10 = r0.endReached
            r10[r8] = r8
            long r14 = r0.mergeDialogId
            r16 = 0
            int r18 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x004f
            r14 = 1
            goto L_0x0050
        L_0x004f:
            r14 = 0
        L_0x0050:
            r10[r11] = r14
            r0.opennedFromMedia = r8
            r0.needCaptionLayout = r8
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r10 = r0.containerView
            r10.setTag(r12)
            r0.isCurrentVideo = r8
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r0.imagesArr
            r10.clear()
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r10 = r0.imagesArrLocations
            r10.clear()
            java.util.ArrayList<java.lang.Integer> r10 = r0.imagesArrLocationsSizes
            r10.clear()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Photo> r10 = r0.avatarsArr
            r10.clear()
            java.util.ArrayList<im.bclpbkiauv.messenger.SecureDocument> r10 = r0.secureDocuments
            r10.clear()
            java.util.ArrayList<java.lang.Object> r10 = r0.imagesArrLocals
            r10.clear()
            r10 = 0
        L_0x007c:
            if (r10 >= r13) goto L_0x008f
            android.util.SparseArray<im.bclpbkiauv.messenger.MessageObject>[] r14 = r0.imagesByIds
            r14 = r14[r10]
            r14.clear()
            android.util.SparseArray<im.bclpbkiauv.messenger.MessageObject>[] r14 = r0.imagesByIdsTemp
            r14 = r14[r10]
            r14.clear()
            int r10 = r10 + 1
            goto L_0x007c
        L_0x008f:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r0.imagesArrTemp
            r10.clear()
            r0.currentUserAvatarLocation = r7
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r10 = r0.containerView
            r10.setPadding(r8, r8, r8, r8)
            im.bclpbkiauv.messenger.ImageReceiver$BitmapHolder r10 = r0.currentThumb
            if (r10 == 0) goto L_0x00a2
            r10.release()
        L_0x00a2:
            if (r6 == 0) goto L_0x00a7
            im.bclpbkiauv.messenger.ImageReceiver$BitmapHolder r10 = r6.thumb
            goto L_0x00a8
        L_0x00a7:
            r10 = r7
        L_0x00a8:
            r0.currentThumb = r10
            if (r6 == 0) goto L_0x00b2
            boolean r10 = r6.isEvent
            if (r10 == 0) goto L_0x00b2
            r10 = 1
            goto L_0x00b3
        L_0x00b2:
            r10 = 0
        L_0x00b3:
            r0.isEvent = r10
            r0.sharedMediaType = r8
            im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem r10 = r0.allMediaItem
            r14 = 2131693964(0x7f0f118c, float:1.9017071E38)
            java.lang.String r15 = "ShowAllMedia"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r10.setText(r14)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.menuItem
            r10.setVisibility(r8)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.sendItem
            r14 = 8
            r10.setVisibility(r14)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.pipItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.cameraItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.cameraItem
            r10.setTag(r7)
            android.widget.FrameLayout r10 = r0.bottomLayout
            r10.setVisibility(r8)
            android.widget.FrameLayout r10 = r0.bottomLayout
            r10.setTag(r12)
            android.widget.FrameLayout r10 = r0.bottomLayout
            r15 = 0
            r10.setTranslationY(r15)
            android.widget.TextView r10 = r0.captionTextView
            r10.setTranslationY(r15)
            android.widget.ImageView r10 = r0.shareButton
            r10.setVisibility(r14)
            im.bclpbkiauv.ui.PhotoViewer$QualityChooseView r10 = r0.qualityChooseView
            r9 = 4
            if (r10 == 0) goto L_0x010c
            r10.setVisibility(r9)
            im.bclpbkiauv.ui.components.PickerBottomLayoutViewer r10 = r0.qualityPicker
            r10.setVisibility(r9)
            im.bclpbkiauv.ui.PhotoViewer$QualityChooseView r10 = r0.qualityChooseView
            r10.setTag(r7)
        L_0x010c:
            android.animation.AnimatorSet r10 = r0.qualityChooseViewAnimation
            if (r10 == 0) goto L_0x0115
            r10.cancel()
            r0.qualityChooseViewAnimation = r7
        L_0x0115:
            r0.setDoubleTapEnabled(r11)
            r0.allowShare = r8
            r0.slideshowMessageId = r8
            r0.nameOverride = r7
            r0.dateOverride = r8
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.menuItem
            r10.hideSubItem(r13)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.menuItem
            r10.hideSubItem(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.menuItem
            r13 = 10
            r10.hideSubItem(r13)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.menuItem
            r13 = 11
            r10.hideSubItem(r13)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.menuItem
            r10.hideSubItem(r11)
            im.bclpbkiauv.ui.actionbar.ActionBar r10 = r0.actionBar
            r10.setTranslationY(r15)
            im.bclpbkiauv.ui.components.CheckBox r10 = r0.checkImageView
            r13 = 1065353216(0x3f800000, float:1.0)
            r10.setAlpha(r13)
            im.bclpbkiauv.ui.components.CheckBox r10 = r0.checkImageView
            r10.setVisibility(r14)
            im.bclpbkiauv.ui.actionbar.ActionBar r10 = r0.actionBar
            r10.setTitleRightMargin(r8)
            im.bclpbkiauv.ui.PhotoViewer$CounterView r10 = r0.photosCounterView
            r10.setAlpha(r13)
            im.bclpbkiauv.ui.PhotoViewer$CounterView r10 = r0.photosCounterView
            r10.setVisibility(r14)
            android.widget.FrameLayout r10 = r0.pickerView
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.pickerViewSendButton
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.pickerViewSendButton
            r10.setTranslationY(r15)
            android.widget.FrameLayout r10 = r0.pickerView
            r10.setAlpha(r13)
            android.widget.ImageView r10 = r0.pickerViewSendButton
            r10.setAlpha(r13)
            android.widget.FrameLayout r10 = r0.pickerView
            r10.setTranslationY(r15)
            android.widget.ImageView r10 = r0.paintItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.cropItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.tuneItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.timeItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.rotateItem
            r10.setVisibility(r14)
            im.bclpbkiauv.ui.components.VideoTimelinePlayView r10 = r0.videoTimelineView
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.compressItem
            r10.setVisibility(r14)
            im.bclpbkiauv.ui.components.PhotoViewerCaptionEnterView r10 = r0.captionEditText
            r10.setVisibility(r14)
            im.bclpbkiauv.ui.components.RecyclerListView r10 = r0.mentionListView
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.muteItem
            r10.setVisibility(r14)
            im.bclpbkiauv.ui.actionbar.ActionBar r10 = r0.actionBar
            r10.setSubtitle(r7)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.masksItem
            r10.setVisibility(r14)
            r0.muteVideo = r8
            android.widget.ImageView r10 = r0.muteItem
            r13 = 2131231689(0x7f0803c9, float:1.8079466E38)
            r10.setImageResource(r13)
            im.bclpbkiauv.ui.components.PickerBottomLayoutViewer r10 = r0.editorDoneLayout
            r10.setVisibility(r14)
            android.widget.TextView r10 = r0.captionTextView
            r10.setTag(r7)
            android.widget.TextView r10 = r0.captionTextView
            r10.setVisibility(r9)
            im.bclpbkiauv.ui.components.PhotoCropView r10 = r0.photoCropView
            if (r10 == 0) goto L_0x01d7
            r10.setVisibility(r14)
        L_0x01d7:
            im.bclpbkiauv.ui.components.PhotoFilterView r10 = r0.photoFilterView
            if (r10 == 0) goto L_0x01de
            r10.setVisibility(r14)
        L_0x01de:
            r10 = 0
        L_0x01df:
            r13 = 3
            if (r10 >= r13) goto L_0x01f4
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r13 = r0.photoProgressViews
            r22 = r13[r10]
            if (r22 == 0) goto L_0x01ef
            r13 = r13[r10]
            r15 = -1
            r13.setBackgroundState(r15, r8)
            goto L_0x01f0
        L_0x01ef:
            r15 = -1
        L_0x01f0:
            int r10 = r10 + 1
            r15 = 0
            goto L_0x01df
        L_0x01f4:
            java.lang.String r13 = "ShowAllFiles"
            if (r1 == 0) goto L_0x02fe
            if (r2 != 0) goto L_0x02fe
            im.bclpbkiauv.tgnet.TLRPC$Message r12 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r12 = r12.media
            boolean r12 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaWebPage
            if (r12 == 0) goto L_0x0283
            im.bclpbkiauv.tgnet.TLRPC$Message r12 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r12 = r12.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r12 = r12.webpage
            if (r12 == 0) goto L_0x0283
            im.bclpbkiauv.tgnet.TLRPC$Message r12 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r12 = r12.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r12 = r12.webpage
            java.lang.String r14 = r12.site_name
            if (r14 == 0) goto L_0x0283
            java.lang.String r14 = r14.toLowerCase()
            java.lang.String r15 = r12.type
            java.lang.String r9 = "app_album"
            boolean r9 = r9.equals(r15)
            if (r9 == 0) goto L_0x0283
            java.lang.String r9 = r12.author
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 != 0) goto L_0x022e
            java.lang.String r9 = r12.author
            r0.nameOverride = r9
        L_0x022e:
            im.bclpbkiauv.tgnet.TLRPC$Page r9 = r12.cached_page
            boolean r9 = r9 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_page
            if (r9 == 0) goto L_0x0258
            r9 = 0
        L_0x0235:
            im.bclpbkiauv.tgnet.TLRPC$Page r15 = r12.cached_page
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r15 = r15.blocks
            int r15 = r15.size()
            if (r9 >= r15) goto L_0x0258
            im.bclpbkiauv.tgnet.TLRPC$Page r15 = r12.cached_page
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r15 = r15.blocks
            java.lang.Object r15 = r15.get(r9)
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r15 = (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r15
            boolean r10 = r15 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockAuthorDate
            if (r10 == 0) goto L_0x0255
            r10 = r15
            im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockAuthorDate r10 = (im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockAuthorDate) r10
            int r10 = r10.published_date
            r0.dateOverride = r10
            goto L_0x0258
        L_0x0255:
            int r9 = r9 + 1
            goto L_0x0235
        L_0x0258:
            java.util.ArrayList r9 = r1.getWebPagePhotos(r7, r7)
            boolean r10 = r9.isEmpty()
            if (r10 != 0) goto L_0x0283
            int r10 = r25.getId()
            r0.slideshowMessageId = r10
            r0.needSearchImageInArr = r8
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r0.imagesArr
            r10.addAll(r9)
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r0.imagesArr
            int r10 = r10.size()
            r0.totalImagesCount = r10
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r0.imagesArr
            int r10 = r10.indexOf(r1)
            if (r10 >= 0) goto L_0x0280
            r10 = 0
        L_0x0280:
            r0.setImageIndex(r10, r11)
        L_0x0283:
            boolean r9 = r25.canPreviewDocument()
            if (r9 == 0) goto L_0x0297
            r0.sharedMediaType = r11
            im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem r9 = r0.allMediaItem
            r10 = 2131693963(0x7f0f118b, float:1.901707E38)
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r10)
            r9.setText(r10)
        L_0x0297:
            int r9 = r0.slideshowMessageId
            if (r9 != 0) goto L_0x02fb
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r9 = r0.imagesArr
            r9.add(r1)
            long r9 = r1.eventId
            int r12 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r12 == 0) goto L_0x02a9
            r0.needSearchImageInArr = r8
            goto L_0x02f5
        L_0x02a9:
            im.bclpbkiauv.ui.components.AnimatedFileDrawable r9 = r0.currentAnimation
            if (r9 == 0) goto L_0x02b5
            r0.needSearchImageInArr = r8
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r9 = r0.sendItem
            r9.setVisibility(r8)
            goto L_0x02f5
        L_0x02b5:
            boolean r9 = r1.scheduled
            if (r9 != 0) goto L_0x02f5
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r9 = r9.media
            boolean r9 = r9 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaInvoice
            if (r9 != 0) goto L_0x02f5
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r9 = r9.media
            boolean r9 = r9 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaWebPage
            if (r9 != 0) goto L_0x02f5
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r9 = r9.action
            if (r9 == 0) goto L_0x02d7
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r9 = r9.action
            boolean r9 = r9 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionEmpty
            if (r9 == 0) goto L_0x02f5
        L_0x02d7:
            r0.needSearchImageInArr = r11
            android.util.SparseArray<im.bclpbkiauv.messenger.MessageObject>[] r9 = r0.imagesByIds
            r9 = r9[r8]
            int r10 = r25.getId()
            r9.put(r10, r1)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r9 = r0.menuItem
            r10 = 4
            r9.showSubItem(r10)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r9 = r0.menuItem
            r10 = 2
            r9.showSubItem(r10)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r9 = r0.sendItem
            r9.setVisibility(r8)
        L_0x02f5:
            r0.setImageIndex(r8, r11)
            r12 = r7
            goto L_0x05a1
        L_0x02fb:
            r12 = r7
            goto L_0x05a1
        L_0x02fe:
            if (r3 == 0) goto L_0x030b
            java.util.ArrayList<im.bclpbkiauv.messenger.SecureDocument> r9 = r0.secureDocuments
            r9.addAll(r3)
            r0.setImageIndex(r5, r11)
            r12 = r7
            goto L_0x05a1
        L_0x030b:
            if (r26 == 0) goto L_0x0398
            int r9 = r6.dialogId
            r0.avatarsDialogId = r9
            if (r27 != 0) goto L_0x0341
            if (r9 <= 0) goto L_0x032a
            int r9 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r9 = im.bclpbkiauv.messenger.MessagesController.getInstance(r9)
            int r10 = r0.avatarsDialogId
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            im.bclpbkiauv.tgnet.TLRPC$User r9 = r9.getUser(r10)
            im.bclpbkiauv.messenger.ImageLocation r9 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r9, r11)
            goto L_0x0343
        L_0x032a:
            int r9 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r9 = im.bclpbkiauv.messenger.MessagesController.getInstance(r9)
            int r10 = r0.avatarsDialogId
            int r10 = -r10
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
            im.bclpbkiauv.messenger.ImageLocation r10 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r9, r11)
            r9 = r10
            goto L_0x0343
        L_0x0341:
            r9 = r27
        L_0x0343:
            if (r9 != 0) goto L_0x0349
            r0.closePhoto(r8, r8)
            return
        L_0x0349:
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r10 = r0.imagesArrLocations
            r10.add(r9)
            r0.currentUserAvatarLocation = r9
            java.util.ArrayList<java.lang.Integer> r10 = r0.imagesArrLocationsSizes
            int r12 = r6.size
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r10.add(r12)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Photo> r10 = r0.avatarsArr
            im.bclpbkiauv.tgnet.TLRPC$TL_photoEmpty r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_photoEmpty
            r12.<init>()
            r10.add(r12)
            android.widget.ImageView r10 = r0.shareButton
            android.widget.FrameLayout r12 = r0.videoPlayerControlFrameLayout
            int r12 = r12.getVisibility()
            if (r12 == 0) goto L_0x0370
            r14 = 0
        L_0x0370:
            r10.setVisibility(r14)
            r0.allowShare = r11
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.menuItem
            r12 = 2
            r10.hideSubItem(r12)
            android.widget.ImageView r10 = r0.shareButton
            int r10 = r10.getVisibility()
            if (r10 != 0) goto L_0x038b
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.menuItem
            r12 = 10
            r10.hideSubItem(r12)
            goto L_0x0392
        L_0x038b:
            r12 = 10
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r10 = r0.menuItem
            r10.showSubItem(r12)
        L_0x0392:
            r0.setImageIndex(r8, r11)
            r12 = r7
            goto L_0x05a3
        L_0x0398:
            if (r2 == 0) goto L_0x040a
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r9 = r0.imagesArr
            r9.addAll(r2)
            r9 = 0
        L_0x03a0:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r0.imagesArr
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x03cd
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r0.imagesArr
            java.lang.Object r10 = r10.get(r9)
            im.bclpbkiauv.messenger.MessageObject r10 = (im.bclpbkiauv.messenger.MessageObject) r10
            android.util.SparseArray<im.bclpbkiauv.messenger.MessageObject>[] r12 = r0.imagesByIds
            long r14 = r10.getDialogId()
            long r7 = r0.currentDialogId
            int r19 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1))
            if (r19 != 0) goto L_0x03be
            r7 = 0
            goto L_0x03bf
        L_0x03be:
            r7 = 1
        L_0x03bf:
            r7 = r12[r7]
            int r8 = r10.getId()
            r7.put(r8, r10)
            int r9 = r9 + 1
            r7 = 0
            r8 = 0
            goto L_0x03a0
        L_0x03cd:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r7 = r0.imagesArr
            java.lang.Object r7 = r7.get(r5)
            im.bclpbkiauv.messenger.MessageObject r7 = (im.bclpbkiauv.messenger.MessageObject) r7
            boolean r8 = r7.scheduled
            if (r8 != 0) goto L_0x03fc
            r0.opennedFromMedia = r11
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r8 = r0.menuItem
            r9 = 4
            r8.showSubItem(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r8 = r0.sendItem
            r9 = 0
            r8.setVisibility(r9)
            boolean r8 = r7.canPreviewDocument()
            if (r8 == 0) goto L_0x0404
            r0.sharedMediaType = r11
            im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem r8 = r0.allMediaItem
            r9 = 2131693963(0x7f0f118b, float:1.901707E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r9)
            r8.setText(r9)
            goto L_0x0404
        L_0x03fc:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r8 = r0.imagesArr
            int r8 = r8.size()
            r0.totalImagesCount = r8
        L_0x0404:
            r0.setImageIndex(r5, r11)
            r12 = 0
            goto L_0x05a1
        L_0x040a:
            if (r4 == 0) goto L_0x05a0
            int r7 = r0.sendPhotoType
            r8 = 5
            if (r7 == 0) goto L_0x041f
            r9 = 4
            if (r7 == r9) goto L_0x041f
            r9 = 2
            if (r7 == r9) goto L_0x0419
            if (r7 != r8) goto L_0x0435
        L_0x0419:
            int r7 = r30.size()
            if (r7 <= r11) goto L_0x0435
        L_0x041f:
            im.bclpbkiauv.ui.components.CheckBox r7 = r0.checkImageView
            r9 = 0
            r7.setVisibility(r9)
            im.bclpbkiauv.ui.PhotoViewer$CounterView r7 = r0.photosCounterView
            r7.setVisibility(r9)
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            r9 = 1120403456(0x42c80000, float:100.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r7.setTitleRightMargin(r9)
        L_0x0435:
            boolean r7 = r0.mShowNeedAddMorePicButton
            if (r7 == 0) goto L_0x0453
            int r7 = r0.sendPhotoType
            r9 = 2
            if (r7 == r9) goto L_0x0440
            if (r7 != r8) goto L_0x0453
        L_0x0440:
            im.bclpbkiauv.ui.PhotoViewer$PhotoViewerProvider r7 = r0.placeProvider
            boolean r7 = r7.canCaptureMorePhotos()
            if (r7 == 0) goto L_0x0453
            android.widget.ImageView r7 = r0.cameraItem
            r9 = 0
            r7.setVisibility(r9)
            android.widget.ImageView r7 = r0.cameraItem
            r7.setTag(r12)
        L_0x0453:
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r7 = r0.menuItem
            r7.setVisibility(r14)
            java.util.ArrayList<java.lang.Object> r7 = r0.imagesArrLocals
            r7.addAll(r4)
            java.util.ArrayList<java.lang.Object> r7 = r0.imagesArrLocals
            java.lang.Object r7 = r7.get(r5)
            boolean r9 = r7 instanceof im.bclpbkiauv.messenger.MediaController.PhotoEntry
            if (r9 == 0) goto L_0x04ad
            r9 = r7
            im.bclpbkiauv.messenger.MediaController$PhotoEntry r9 = (im.bclpbkiauv.messenger.MediaController.PhotoEntry) r9
            boolean r9 = r9.isVideo
            if (r9 == 0) goto L_0x0491
            android.widget.ImageView r9 = r0.cropItem
            r9.setVisibility(r14)
            android.widget.ImageView r9 = r0.rotateItem
            r9.setVisibility(r14)
            android.widget.FrameLayout r9 = r0.bottomLayout
            r10 = 0
            r9.setVisibility(r10)
            android.widget.FrameLayout r9 = r0.bottomLayout
            r9.setTag(r12)
            android.widget.FrameLayout r9 = r0.bottomLayout
            r10 = 1111490560(0x42400000, float:48.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            int r10 = -r10
            float r10 = (float) r10
            r9.setTranslationY(r10)
            goto L_0x04ab
        L_0x0491:
            android.widget.ImageView r9 = r0.cropItem
            int r10 = r0.sendPhotoType
            if (r10 == r11) goto L_0x0499
            r10 = 0
            goto L_0x049b
        L_0x0499:
            r10 = 8
        L_0x049b:
            r9.setVisibility(r10)
            android.widget.ImageView r9 = r0.rotateItem
            int r10 = r0.sendPhotoType
            if (r10 == r11) goto L_0x04a7
            r10 = 8
            goto L_0x04a8
        L_0x04a7:
            r10 = 0
        L_0x04a8:
            r9.setVisibility(r10)
        L_0x04ab:
            r9 = 1
            goto L_0x04e1
        L_0x04ad:
            boolean r9 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.BotInlineResult
            if (r9 == 0) goto L_0x04bd
            android.widget.ImageView r9 = r0.cropItem
            r9.setVisibility(r14)
            android.widget.ImageView r9 = r0.rotateItem
            r9.setVisibility(r14)
            r9 = 0
            goto L_0x04e1
        L_0x04bd:
            android.widget.ImageView r9 = r0.cropItem
            boolean r10 = r7 instanceof im.bclpbkiauv.messenger.MediaController.SearchImage
            if (r10 == 0) goto L_0x04cc
            r10 = r7
            im.bclpbkiauv.messenger.MediaController$SearchImage r10 = (im.bclpbkiauv.messenger.MediaController.SearchImage) r10
            int r10 = r10.type
            if (r10 != 0) goto L_0x04cc
            r10 = 0
            goto L_0x04ce
        L_0x04cc:
            r10 = 8
        L_0x04ce:
            r9.setVisibility(r10)
            android.widget.ImageView r9 = r0.rotateItem
            r9.setVisibility(r14)
            android.widget.ImageView r9 = r0.cropItem
            int r9 = r9.getVisibility()
            if (r9 != 0) goto L_0x04e0
            r9 = 1
            goto L_0x04e1
        L_0x04e0:
            r9 = 0
        L_0x04e1:
            im.bclpbkiauv.ui.ChatActivity r10 = r0.parentChatActivity
            if (r10 == 0) goto L_0x053b
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r10 = r10.currentEncryptedChat
            if (r10 == 0) goto L_0x04f7
            im.bclpbkiauv.ui.ChatActivity r10 = r0.parentChatActivity
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r10 = r10.currentEncryptedChat
            int r10 = r10.layer
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r10)
            r12 = 46
            if (r10 < r12) goto L_0x053b
        L_0x04f7:
            im.bclpbkiauv.ui.adapters.MentionsAdapter r10 = r0.mentionsAdapter
            im.bclpbkiauv.ui.ChatActivity r12 = r0.parentChatActivity
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r12 = r12.chatInfo
            r10.setChatInfo(r12)
            im.bclpbkiauv.ui.adapters.MentionsAdapter r10 = r0.mentionsAdapter
            im.bclpbkiauv.ui.ChatActivity r12 = r0.parentChatActivity
            im.bclpbkiauv.tgnet.TLRPC$Chat r12 = r12.currentChat
            if (r12 == 0) goto L_0x050a
            r12 = 1
            goto L_0x050b
        L_0x050a:
            r12 = 0
        L_0x050b:
            r10.setNeedUsernames(r12)
            im.bclpbkiauv.ui.adapters.MentionsAdapter r10 = r0.mentionsAdapter
            r12 = 0
            r10.setNeedBotContext(r12)
            if (r9 == 0) goto L_0x0524
            im.bclpbkiauv.ui.PhotoViewer$PhotoViewerProvider r10 = r0.placeProvider
            if (r10 == 0) goto L_0x0522
            if (r10 == 0) goto L_0x0524
            boolean r10 = r10.allowCaption()
            if (r10 == 0) goto L_0x0524
        L_0x0522:
            r10 = 1
            goto L_0x0525
        L_0x0524:
            r10 = 0
        L_0x0525:
            r0.needCaptionLayout = r10
            im.bclpbkiauv.ui.components.PhotoViewerCaptionEnterView r12 = r0.captionEditText
            if (r10 == 0) goto L_0x052d
            r10 = 0
            goto L_0x052f
        L_0x052d:
            r10 = 8
        L_0x052f:
            r12.setVisibility(r10)
            boolean r10 = r0.needCaptionLayout
            if (r10 == 0) goto L_0x053b
            im.bclpbkiauv.ui.components.PhotoViewerCaptionEnterView r10 = r0.captionEditText
            r10.onCreate()
        L_0x053b:
            android.widget.FrameLayout r10 = r0.pickerView
            r12 = 0
            r10.setVisibility(r12)
            android.widget.ImageView r10 = r0.pickerViewSendButton
            r10.setVisibility(r12)
            android.widget.ImageView r10 = r0.pickerViewSendButton
            r12 = 0
            r10.setTranslationY(r12)
            android.widget.ImageView r10 = r0.pickerViewSendButton
            r12 = 1065353216(0x3f800000, float:1.0)
            r10.setAlpha(r12)
            android.widget.FrameLayout r10 = r0.bottomLayout
            r10.setVisibility(r14)
            android.widget.FrameLayout r10 = r0.bottomLayout
            r12 = 0
            r10.setTag(r12)
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r10 = r0.containerView
            r10.setTag(r12)
            r0.setImageIndex(r5, r11)
            int r10 = r0.sendPhotoType
            if (r10 != r11) goto L_0x0576
            android.widget.ImageView r8 = r0.paintItem
            r10 = 0
            r8.setVisibility(r10)
            android.widget.ImageView r8 = r0.tuneItem
            r8.setVisibility(r10)
            goto L_0x059c
        L_0x0576:
            r13 = 4
            if (r10 == r13) goto L_0x0592
            if (r10 == r8) goto L_0x0592
            android.widget.ImageView r8 = r0.paintItem
            android.widget.ImageView r10 = r0.cropItem
            int r10 = r10.getVisibility()
            r8.setVisibility(r10)
            android.widget.ImageView r8 = r0.tuneItem
            android.widget.ImageView r10 = r0.cropItem
            int r10 = r10.getVisibility()
            r8.setVisibility(r10)
            goto L_0x059c
        L_0x0592:
            android.widget.ImageView r8 = r0.paintItem
            r8.setVisibility(r14)
            android.widget.ImageView r8 = r0.tuneItem
            r8.setVisibility(r14)
        L_0x059c:
            r24.updateSelectedCount()
            goto L_0x05a1
        L_0x05a0:
            r12 = 0
        L_0x05a1:
            r9 = r27
        L_0x05a3:
            im.bclpbkiauv.ui.components.AnimatedFileDrawable r7 = r0.currentAnimation
            if (r7 != 0) goto L_0x0609
            boolean r7 = r0.isEvent
            if (r7 != 0) goto L_0x0609
            long r7 = r0.currentDialogId
            int r10 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r10 == 0) goto L_0x05f0
            int r7 = r0.totalImagesCount
            if (r7 != 0) goto L_0x05f0
            im.bclpbkiauv.messenger.MessageObject r7 = r0.currentMessageObject
            if (r7 == 0) goto L_0x05f0
            boolean r7 = r7.scheduled
            if (r7 != 0) goto L_0x05f0
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r18 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r7)
            long r7 = r0.currentDialogId
            int r10 = r0.sharedMediaType
            int r13 = r0.classGuid
            r23 = 1
            r19 = r7
            r21 = r10
            r22 = r13
            r18.getMediaCount(r19, r21, r22, r23)
            long r7 = r0.mergeDialogId
            int r10 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r10 == 0) goto L_0x0609
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r13 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r7)
            long r14 = r0.mergeDialogId
            int r7 = r0.sharedMediaType
            int r8 = r0.classGuid
            r18 = 1
            r16 = r7
            r17 = r8
            r13.getMediaCount(r14, r16, r17, r18)
            goto L_0x0609
        L_0x05f0:
            int r7 = r0.avatarsDialogId
            if (r7 == 0) goto L_0x0609
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r13 = im.bclpbkiauv.messenger.MessagesController.getInstance(r7)
            int r14 = r0.avatarsDialogId
            r15 = 80
            r16 = 0
            r18 = 1
            int r7 = r0.classGuid
            r19 = r7
            r13.loadDialogPhotos(r14, r15, r16, r18, r19)
        L_0x0609:
            im.bclpbkiauv.messenger.MessageObject r7 = r0.currentMessageObject
            if (r7 == 0) goto L_0x0613
            boolean r7 = r7.isVideo()
            if (r7 != 0) goto L_0x062c
        L_0x0613:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r7 = r0.currentBotInlineResult
            if (r7 == 0) goto L_0x0632
            java.lang.String r7 = r7.type
            java.lang.String r8 = "video"
            boolean r7 = r7.equals(r8)
            if (r7 != 0) goto L_0x062c
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r7 = r0.currentBotInlineResult
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r7.document
            boolean r7 = im.bclpbkiauv.messenger.MessageObject.isVideoDocument(r7)
            if (r7 == 0) goto L_0x0632
        L_0x062c:
            r7 = 0
            r0.onActionClick(r7)
            goto L_0x06af
        L_0x0632:
            java.util.ArrayList<java.lang.Object> r7 = r0.imagesArrLocals
            boolean r7 = r7.isEmpty()
            if (r7 != 0) goto L_0x06af
            java.util.ArrayList<java.lang.Object> r7 = r0.imagesArrLocals
            java.lang.Object r7 = r7.get(r5)
            r8 = 0
            im.bclpbkiauv.ui.ChatActivity r10 = r0.parentChatActivity
            if (r10 == 0) goto L_0x064a
            im.bclpbkiauv.tgnet.TLRPC$User r10 = r10.getCurrentUser()
            goto L_0x064b
        L_0x064a:
            r10 = r12
        L_0x064b:
            im.bclpbkiauv.ui.ChatActivity r12 = r0.parentChatActivity
            if (r12 == 0) goto L_0x0673
            boolean r12 = r12.isSecretChat()
            if (r12 == 0) goto L_0x0673
            im.bclpbkiauv.ui.ChatActivity r12 = r0.parentChatActivity
            boolean r12 = r12.isInScheduleMode()
            if (r12 != 0) goto L_0x0673
            if (r10 == 0) goto L_0x0673
            boolean r12 = r10.bot
            if (r12 != 0) goto L_0x0673
            boolean r12 = im.bclpbkiauv.messenger.UserObject.isUserSelf(r10)
            if (r12 != 0) goto L_0x0673
            im.bclpbkiauv.ui.ChatActivity r12 = r0.parentChatActivity
            boolean r12 = r12.isEditingMessageMedia()
            if (r12 != 0) goto L_0x0673
            r12 = 1
            goto L_0x0674
        L_0x0673:
            r12 = 0
        L_0x0674:
            boolean r13 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.BotInlineResult
            if (r13 == 0) goto L_0x067a
            r12 = 0
            goto L_0x06a7
        L_0x067a:
            boolean r13 = r7 instanceof im.bclpbkiauv.messenger.MediaController.PhotoEntry
            if (r13 == 0) goto L_0x0695
            r11 = r7
            im.bclpbkiauv.messenger.MediaController$PhotoEntry r11 = (im.bclpbkiauv.messenger.MediaController.PhotoEntry) r11
            boolean r13 = r11.isVideo
            if (r13 == 0) goto L_0x06a6
            java.io.File r13 = new java.io.File
            java.lang.String r14 = r11.path
            r13.<init>(r14)
            android.net.Uri r13 = android.net.Uri.fromFile(r13)
            r14 = 0
            r0.preparePlayer(r13, r14, r14)
            goto L_0x06a6
        L_0x0695:
            if (r12 == 0) goto L_0x06a6
            boolean r13 = r7 instanceof im.bclpbkiauv.messenger.MediaController.SearchImage
            if (r13 == 0) goto L_0x06a6
            r13 = r7
            im.bclpbkiauv.messenger.MediaController$SearchImage r13 = (im.bclpbkiauv.messenger.MediaController.SearchImage) r13
            int r13 = r13.type
            if (r13 != 0) goto L_0x06a3
            goto L_0x06a4
        L_0x06a3:
            r11 = 0
        L_0x06a4:
            r12 = r11
            goto L_0x06a7
        L_0x06a6:
        L_0x06a7:
            if (r12 == 0) goto L_0x06af
            android.widget.ImageView r11 = r0.timeItem
            r13 = 0
            r11.setVisibility(r13)
        L_0x06af:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PhotoViewer.onPhotoShow(im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.tgnet.TLRPC$FileLocation, im.bclpbkiauv.messenger.ImageLocation, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int, im.bclpbkiauv.ui.PhotoViewer$PlaceProviderObject):void");
    }

    private void setDoubleTapEnabled(boolean value) {
        this.doubleTapEnabled = value;
        this.gestureDetector.setOnDoubleTapListener(value ? this : null);
    }

    public boolean isMuteVideo() {
        return this.muteVideo;
    }

    /* access modifiers changed from: private */
    public void setImages() {
        if (this.animationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex);
            setIndexToImage(this.rightImage, this.currentIndex + 1);
            setIndexToImage(this.leftImage, this.currentIndex - 1);
        }
    }

    public void setNeedMore(boolean need) {
        this.mShowNeedAddMorePicButton = need;
        this.cameraItem.setVisibility(need ? 0 : 4);
    }

    private void setIsAboutToSwitchToIndex(int index, boolean init) {
        CharSequence caption;
        boolean isVideo;
        boolean isVideo2;
        boolean z;
        float end;
        boolean isMuted;
        boolean z2;
        int loadFromMaxId;
        long date;
        boolean z3;
        int i = index;
        if (init || this.switchingToIndex != i) {
            this.switchingToIndex = i;
            boolean isVideo3 = false;
            String newFileName = getFileName(index);
            MessageObject newMessageObject = null;
            if (!this.imagesArr.isEmpty()) {
                int i2 = this.switchingToIndex;
                if (i2 < 0) {
                    return;
                }
                if (i2 < this.imagesArr.size()) {
                    newMessageObject = this.imagesArr.get(this.switchingToIndex);
                    boolean isVideo4 = newMessageObject.isVideo();
                    boolean isInvoice = newMessageObject.isInvoice();
                    if (isInvoice) {
                        this.masksItem.setVisibility(8);
                        this.menuItem.hideSubItem(6);
                        this.menuItem.hideSubItem(11);
                        caption = newMessageObject.messageOwner.media.description;
                        this.allowShare = false;
                        this.bottomLayout.setTranslationY((float) AndroidUtilities.dp(48.0f));
                        this.captionTextView.setTranslationY((float) AndroidUtilities.dp(48.0f));
                        boolean z4 = isVideo4;
                    } else {
                        this.masksItem.setVisibility((!newMessageObject.hasPhotoStickers() || ((int) newMessageObject.getDialogId()) == 0) ? 8 : 0);
                        ChatActivity chatActivity = this.parentChatActivity;
                        if (!newMessageObject.canDeleteMessage(chatActivity != null && chatActivity.isInScheduleMode(), (TLRPC.Chat) null) || this.slideshowMessageId != 0) {
                            this.menuItem.hideSubItem(6);
                        } else {
                            this.menuItem.showSubItem(6);
                        }
                        if (isVideo4) {
                            this.menuItem.showSubItem(11);
                            if (this.pipItem.getVisibility() != 0) {
                                z3 = false;
                                this.pipItem.setVisibility(0);
                            } else {
                                z3 = false;
                            }
                            if (!this.pipAvailable) {
                                this.pipItem.setEnabled(z3);
                                this.pipItem.setAlpha(0.5f);
                            }
                        } else {
                            this.menuItem.hideSubItem(11);
                            if (this.pipItem.getVisibility() != 8) {
                                this.pipItem.setVisibility(8);
                            }
                        }
                        String str = this.nameOverride;
                        if (str != null) {
                            this.nameTextView.setText(str);
                        } else if (newMessageObject.isFromUser()) {
                            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(newMessageObject.messageOwner.from_id));
                            if (user != null) {
                                this.nameTextView.setText(UserObject.getName(user));
                            } else {
                                this.nameTextView.setText("");
                            }
                        } else {
                            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(newMessageObject.messageOwner.to_id.channel_id));
                            if (ChatObject.isChannel(chat) && chat.megagroup && newMessageObject.isForwardedChannelPost()) {
                                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(newMessageObject.messageOwner.fwd_from.channel_id));
                            }
                            if (chat != null) {
                                this.nameTextView.setText(chat.title);
                            } else {
                                this.nameTextView.setText("");
                            }
                        }
                        int i3 = this.dateOverride;
                        if (i3 != 0) {
                            date = ((long) i3) * 1000;
                        } else {
                            date = ((long) newMessageObject.messageOwner.date) * 1000;
                        }
                        String dateString = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(date)), LocaleController.getInstance().formatterDay.format(new Date(date)));
                        if (newFileName == null || !isVideo4) {
                            this.dateTextView.setText(dateString);
                        } else {
                            boolean z5 = isVideo4;
                            this.dateTextView.setText(String.format("%s (%s)", new Object[]{dateString, AndroidUtilities.formatFileSize((long) newMessageObject.getDocument().size)}));
                        }
                        caption = newMessageObject.caption;
                    }
                    if (this.currentAnimation != null) {
                        this.menuItem.hideSubItem(1);
                        this.menuItem.hideSubItem(10);
                        ChatActivity chatActivity2 = this.parentChatActivity;
                        if (!newMessageObject.canDeleteMessage(chatActivity2 != null && chatActivity2.isInScheduleMode(), (TLRPC.Chat) null)) {
                            this.menuItem.hideSubItem(6);
                        }
                        this.allowShare = true;
                        this.shareButton.setVisibility(0);
                        this.actionBar.setTitle(LocaleController.getString("AttachGif", R.string.AttachGif));
                    } else {
                        if (this.totalImagesCount + this.totalImagesCountMerge == 0 || this.needSearchImageInArr) {
                            if (this.slideshowMessageId != 0 || !(newMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
                                if (isInvoice) {
                                    this.actionBar.setTitle(newMessageObject.messageOwner.media.title);
                                } else if (newMessageObject.isVideo()) {
                                    this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                                } else if (newMessageObject.getDocument() != null) {
                                    this.actionBar.setTitle(LocaleController.getString("AttachDocument", R.string.AttachDocument));
                                }
                            } else if (newMessageObject.canPreviewDocument()) {
                                this.actionBar.setTitle(LocaleController.getString("AttachDocument", R.string.AttachDocument));
                            } else if (newMessageObject.isVideo()) {
                                this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                            } else {
                                this.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                            }
                        } else if (this.opennedFromMedia) {
                            if (this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge && !this.loadingMoreImages && this.switchingToIndex > this.imagesArr.size() - 5) {
                                if (this.imagesArr.isEmpty()) {
                                    loadFromMaxId = 0;
                                } else {
                                    ArrayList<MessageObject> arrayList = this.imagesArr;
                                    loadFromMaxId = arrayList.get(arrayList.size() - 1).getId();
                                }
                                int loadIndex = 0;
                                if (this.endReached[0] && this.mergeDialogId != 0) {
                                    loadIndex = 1;
                                    if (!this.imagesArr.isEmpty()) {
                                        ArrayList<MessageObject> arrayList2 = this.imagesArr;
                                        if (arrayList2.get(arrayList2.size() - 1).getDialogId() != this.mergeDialogId) {
                                            loadFromMaxId = 0;
                                        }
                                    }
                                }
                                MediaDataController.getInstance(this.currentAccount).loadMedia(loadIndex == 0 ? this.currentDialogId : this.mergeDialogId, 80, loadFromMaxId, this.sharedMediaType, 1, this.classGuid);
                                this.loadingMoreImages = true;
                            }
                            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.switchingToIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                        } else {
                            if (this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge && !this.loadingMoreImages && this.switchingToIndex < 5) {
                                int loadFromMaxId2 = this.imagesArr.isEmpty() ? 0 : this.imagesArr.get(0).getId();
                                int loadIndex2 = 0;
                                if (this.endReached[0] && this.mergeDialogId != 0) {
                                    loadIndex2 = 1;
                                    if (!this.imagesArr.isEmpty() && this.imagesArr.get(0).getDialogId() != this.mergeDialogId) {
                                        loadFromMaxId2 = 0;
                                    }
                                }
                                MediaDataController.getInstance(this.currentAccount).loadMedia(loadIndex2 == 0 ? this.currentDialogId : this.mergeDialogId, 80, loadFromMaxId2, this.sharedMediaType, 1, this.classGuid);
                                this.loadingMoreImages = true;
                            } else if (this.imagesArr.size() > 0) {
                                this.totalImagesCount = this.imagesArr.size();
                            }
                            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(((this.totalImagesCount + this.totalImagesCountMerge) - this.imagesArr.size()) + this.switchingToIndex + 1), Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge)));
                        }
                        if (((int) this.currentDialogId) == 0) {
                            this.sendItem.setVisibility(8);
                        }
                        if (newMessageObject.messageOwner.ttl == 0 || newMessageObject.messageOwner.ttl >= 3600) {
                            this.allowShare = true;
                            this.menuItem.showSubItem(1);
                            this.shareButton.setVisibility(this.videoPlayerControlFrameLayout.getVisibility() != 0 ? 0 : 8);
                            if (this.shareButton.getVisibility() == 0) {
                                this.menuItem.hideSubItem(10);
                            } else {
                                this.menuItem.showSubItem(10);
                            }
                        } else {
                            this.allowShare = false;
                            this.menuItem.hideSubItem(1);
                            this.shareButton.setVisibility(8);
                            this.menuItem.hideSubItem(10);
                        }
                    }
                    this.groupedPhotosListView.fillList();
                    String str2 = newFileName;
                } else {
                    return;
                }
            } else {
                CharSequence caption2 = null;
                if (!this.secureDocuments.isEmpty()) {
                    this.allowShare = false;
                    this.menuItem.hideSubItem(1);
                    this.nameTextView.setText("");
                    this.dateTextView.setText("");
                    this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.switchingToIndex + 1), Integer.valueOf(this.secureDocuments.size())));
                    isVideo = false;
                    String str3 = newFileName;
                } else if (!this.imagesArrLocations.isEmpty()) {
                    if (i >= 0 && i < this.imagesArrLocations.size()) {
                        this.nameTextView.setText("");
                        this.dateTextView.setText("");
                        if (this.avatarsDialogId != UserConfig.getInstance(this.currentAccount).getClientUserId() || this.avatarsArr.isEmpty()) {
                            this.menuItem.hideSubItem(6);
                        } else {
                            this.menuItem.showSubItem(6);
                        }
                        if (this.isEvent) {
                            this.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                            z2 = true;
                        } else {
                            z2 = true;
                            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.switchingToIndex + 1), Integer.valueOf(this.imagesArrLocations.size())));
                        }
                        this.menuItem.showSubItem(z2 ? 1 : 0);
                        this.allowShare = z2;
                        this.shareButton.setVisibility(this.videoPlayerControlFrameLayout.getVisibility() != 0 ? 0 : 8);
                        if (this.shareButton.getVisibility() == 0) {
                            this.menuItem.hideSubItem(10);
                        } else {
                            this.menuItem.showSubItem(10);
                        }
                        this.groupedPhotosListView.fillList();
                        isVideo = false;
                        String str4 = newFileName;
                    } else {
                        return;
                    }
                } else if (this.imagesArrLocals.isEmpty()) {
                    isVideo = false;
                    String str5 = newFileName;
                } else if (i < 0) {
                    String str6 = newFileName;
                    return;
                } else if (i >= this.imagesArrLocals.size()) {
                    String str7 = newFileName;
                    return;
                } else {
                    Object object = this.imagesArrLocals.get(i);
                    int ttl = 0;
                    boolean isFiltered = false;
                    boolean isPainted = false;
                    boolean isCropped = false;
                    if (object instanceof TLRPC.BotInlineResult) {
                        TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) object;
                        this.currentBotInlineResult = botInlineResult;
                        if (botInlineResult.document != null) {
                            isVideo3 = MessageObject.isVideoDocument(botInlineResult.document);
                        } else if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                            isVideo3 = botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO);
                        }
                        isVideo2 = isVideo3;
                        String str8 = newFileName;
                    } else {
                        String pathObject = null;
                        boolean isAnimation = false;
                        if (object instanceof MediaController.PhotoEntry) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                            pathObject = photoEntry.path;
                            isVideo3 = photoEntry.isVideo;
                        } else if (object instanceof MediaController.SearchImage) {
                            MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                            pathObject = searchImage.getPathToAttach();
                            if (searchImage.type == 1) {
                                isAnimation = true;
                                isVideo3 = false;
                            } else {
                                isVideo3 = false;
                            }
                        }
                        if (isVideo3) {
                            this.muteItem.setVisibility(0);
                            this.compressItem.setVisibility(0);
                            this.isCurrentVideo = true;
                            updateAccessibilityOverlayVisibility();
                            float start = 0.0f;
                            if (object instanceof MediaController.PhotoEntry) {
                                MediaController.PhotoEntry photoEntry2 = (MediaController.PhotoEntry) object;
                                isVideo2 = isVideo3;
                                if (photoEntry2.editedInfo != null) {
                                    boolean isMuted2 = photoEntry2.editedInfo.muted;
                                    start = photoEntry2.editedInfo.start;
                                    end = photoEntry2.editedInfo.end;
                                    isMuted = isMuted2;
                                    processOpenVideo(pathObject, isMuted, start, end);
                                    boolean z6 = isMuted;
                                    float f = end;
                                    this.videoTimelineView.setVisibility(0);
                                    this.muteItem.setVisibility(0);
                                    this.compressItem.setVisibility(0);
                                    this.paintItem.setVisibility(8);
                                    this.cropItem.setVisibility(8);
                                    this.tuneItem.setVisibility(8);
                                    this.rotateItem.setVisibility(8);
                                }
                            } else {
                                isVideo2 = isVideo3;
                            }
                            end = 1.0f;
                            isMuted = false;
                            processOpenVideo(pathObject, isMuted, start, end);
                            boolean z62 = isMuted;
                            float f2 = end;
                            this.videoTimelineView.setVisibility(0);
                            this.muteItem.setVisibility(0);
                            this.compressItem.setVisibility(0);
                            this.paintItem.setVisibility(8);
                            this.cropItem.setVisibility(8);
                            this.tuneItem.setVisibility(8);
                            this.rotateItem.setVisibility(8);
                        } else {
                            isVideo2 = isVideo3;
                            this.videoTimelineView.setVisibility(8);
                            this.muteItem.setVisibility(8);
                            this.isCurrentVideo = false;
                            updateAccessibilityOverlayVisibility();
                            this.compressItem.setVisibility(8);
                            if (isAnimation) {
                                this.paintItem.setVisibility(8);
                                this.cropItem.setVisibility(8);
                                this.rotateItem.setVisibility(8);
                                this.tuneItem.setVisibility(8);
                            } else {
                                int i4 = this.sendPhotoType;
                                if (i4 == 4 || i4 == 5) {
                                    this.paintItem.setVisibility(8);
                                    this.tuneItem.setVisibility(8);
                                } else {
                                    this.paintItem.setVisibility(0);
                                    this.tuneItem.setVisibility(0);
                                }
                                this.cropItem.setVisibility(this.sendPhotoType != 1 ? 0 : 8);
                                this.rotateItem.setVisibility(this.sendPhotoType != 1 ? 8 : 0);
                            }
                            this.actionBar.setSubtitle((CharSequence) null);
                        }
                        if (object instanceof MediaController.PhotoEntry) {
                            MediaController.PhotoEntry photoEntry3 = (MediaController.PhotoEntry) object;
                            if (photoEntry3.bucketId == 0) {
                                String str9 = newFileName;
                                if (photoEntry3.dateTaken == 0 && this.imagesArrLocals.size() == 1) {
                                    z = true;
                                    this.fromCamera = z;
                                    CharSequence caption3 = photoEntry3.caption;
                                    int ttl2 = photoEntry3.ttl;
                                    isFiltered = photoEntry3.isFiltered;
                                    isPainted = photoEntry3.isPainted;
                                    isCropped = photoEntry3.isCropped;
                                    caption2 = caption3;
                                    ttl = ttl2;
                                }
                            } else {
                                String str10 = newFileName;
                            }
                            z = false;
                            this.fromCamera = z;
                            CharSequence caption32 = photoEntry3.caption;
                            int ttl22 = photoEntry3.ttl;
                            isFiltered = photoEntry3.isFiltered;
                            isPainted = photoEntry3.isPainted;
                            isCropped = photoEntry3.isCropped;
                            caption2 = caption32;
                            ttl = ttl22;
                        } else {
                            String str11 = newFileName;
                            if (object instanceof MediaController.SearchImage) {
                                MediaController.SearchImage searchImage2 = (MediaController.SearchImage) object;
                                CharSequence caption4 = searchImage2.caption;
                                int ttl3 = searchImage2.ttl;
                                isFiltered = searchImage2.isFiltered;
                                isPainted = searchImage2.isPainted;
                                isCropped = searchImage2.isCropped;
                                caption2 = caption4;
                                ttl = ttl3;
                            } else {
                                ttl = 0;
                            }
                        }
                    }
                    if (this.bottomLayout.getVisibility() != 8) {
                        this.bottomLayout.setVisibility(8);
                    }
                    PorterDuffColorFilter porterDuffColorFilter = null;
                    this.bottomLayout.setTag((Object) null);
                    if (!this.fromCamera) {
                        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.switchingToIndex + 1), Integer.valueOf(this.imagesArrLocals.size())));
                    } else if (isVideo2) {
                        this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                    } else {
                        this.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                    }
                    ChatActivity chatActivity3 = this.parentChatActivity;
                    if (chatActivity3 != null) {
                        TLRPC.Chat chat2 = chatActivity3.getCurrentChat();
                        if (chat2 != null) {
                            this.actionBar.setTitle(chat2.title);
                        } else {
                            TLRPC.User user2 = this.parentChatActivity.getCurrentUser();
                            if (user2 != null) {
                                if (user2.self) {
                                    this.actionBar.setTitle(LocaleController.getString("SavedMessages", R.string.SavedMessages));
                                } else {
                                    this.actionBar.setTitle(ContactsController.formatName(user2.first_name, user2.last_name));
                                }
                            }
                        }
                    }
                    int i5 = this.sendPhotoType;
                    if (i5 == 0 || i5 == 4 || ((i5 == 2 || i5 == 5) && this.imagesArrLocals.size() > 1)) {
                        this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.switchingToIndex), false);
                    }
                    updateCaptionTextForCurrentPhoto(object);
                    PorterDuffColorFilter filter = new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY);
                    this.timeItem.setColorFilter(ttl != 0 ? filter : null);
                    this.paintItem.setColorFilter(isPainted ? filter : null);
                    this.cropItem.setColorFilter(isCropped ? filter : null);
                    ImageView imageView = this.tuneItem;
                    if (isFiltered) {
                        porterDuffColorFilter = filter;
                    }
                    imageView.setColorFilter(porterDuffColorFilter);
                    caption = caption2;
                    boolean z7 = isVideo2;
                }
                boolean z8 = isVideo;
                caption = null;
            }
            setCurrentCaption(newMessageObject, caption, !init);
        }
    }

    /* access modifiers changed from: private */
    public TLRPC.FileLocation getFileLocation(ImageLocation location) {
        if (location == null) {
            return null;
        }
        return location.location;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0073, code lost:
        r11 = r0.currentMessageObject;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setImageIndex(int r18, boolean r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            int r2 = r0.currentIndex
            if (r2 == r1) goto L_0x02ba
            im.bclpbkiauv.ui.PhotoViewer$PhotoViewerProvider r2 = r0.placeProvider
            if (r2 != 0) goto L_0x000e
            goto L_0x02ba
        L_0x000e:
            r2 = 0
            if (r19 != 0) goto L_0x001a
            im.bclpbkiauv.messenger.ImageReceiver$BitmapHolder r3 = r0.currentThumb
            if (r3 == 0) goto L_0x001a
            r3.release()
            r0.currentThumb = r2
        L_0x001a:
            java.lang.String[] r3 = r0.currentFileNames
            java.lang.String r4 = r17.getFileName(r18)
            r5 = 0
            r3[r5] = r4
            java.lang.String[] r3 = r0.currentFileNames
            int r4 = r1 + 1
            java.lang.String r4 = r0.getFileName(r4)
            r6 = 1
            r3[r6] = r4
            java.lang.String[] r3 = r0.currentFileNames
            int r4 = r1 + -1
            java.lang.String r4 = r0.getFileName(r4)
            r7 = 2
            r3[r7] = r4
            im.bclpbkiauv.ui.PhotoViewer$PhotoViewerProvider r3 = r0.placeProvider
            im.bclpbkiauv.messenger.MessageObject r4 = r0.currentMessageObject
            im.bclpbkiauv.messenger.ImageLocation r8 = r0.currentFileLocation
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r8 = r0.getFileLocation(r8)
            int r9 = r0.currentIndex
            r3.willSwitchFromPhoto(r4, r8, r9)
            int r3 = r0.currentIndex
            r0.currentIndex = r1
            r17.setIsAboutToSwitchToIndex(r18, r19)
            r4 = 0
            r8 = 0
            r9 = 0
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r0.imagesArr
            boolean r10 = r10.isEmpty()
            if (r10 != 0) goto L_0x00ae
            int r10 = r0.currentIndex
            if (r10 < 0) goto L_0x00aa
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r11 = r0.imagesArr
            int r11 = r11.size()
            if (r10 < r11) goto L_0x0067
            goto L_0x00aa
        L_0x0067:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r0.imagesArr
            int r11 = r0.currentIndex
            java.lang.Object r10 = r10.get(r11)
            im.bclpbkiauv.messenger.MessageObject r10 = (im.bclpbkiauv.messenger.MessageObject) r10
            if (r19 == 0) goto L_0x0083
            im.bclpbkiauv.messenger.MessageObject r11 = r0.currentMessageObject
            if (r11 == 0) goto L_0x0083
            int r11 = r11.getId()
            int r12 = r10.getId()
            if (r11 != r12) goto L_0x0083
            r11 = 1
            goto L_0x0084
        L_0x0083:
            r11 = 0
        L_0x0084:
            r8 = r11
            r0.currentMessageObject = r10
            boolean r4 = r10.isVideo()
            int r11 = r0.sharedMediaType
            if (r11 != r6) goto L_0x00a8
            boolean r11 = r10.canPreviewDocument()
            r0.canZoom = r11
            if (r11 == 0) goto L_0x00a0
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r11 = r0.menuItem
            r11.showSubItem(r6)
            r0.setDoubleTapEnabled(r6)
            goto L_0x00a8
        L_0x00a0:
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r11 = r0.menuItem
            r11.hideSubItem(r6)
            r0.setDoubleTapEnabled(r5)
        L_0x00a8:
            goto L_0x01b8
        L_0x00aa:
            r0.closePhoto(r5, r5)
            return
        L_0x00ae:
            java.util.ArrayList<im.bclpbkiauv.messenger.SecureDocument> r10 = r0.secureDocuments
            boolean r10 = r10.isEmpty()
            if (r10 != 0) goto L_0x00d1
            if (r1 < 0) goto L_0x00cd
            java.util.ArrayList<im.bclpbkiauv.messenger.SecureDocument> r10 = r0.secureDocuments
            int r10 = r10.size()
            if (r1 < r10) goto L_0x00c1
            goto L_0x00cd
        L_0x00c1:
            java.util.ArrayList<im.bclpbkiauv.messenger.SecureDocument> r10 = r0.secureDocuments
            java.lang.Object r10 = r10.get(r1)
            im.bclpbkiauv.messenger.SecureDocument r10 = (im.bclpbkiauv.messenger.SecureDocument) r10
            r0.currentSecureDocument = r10
            goto L_0x01b8
        L_0x00cd:
            r0.closePhoto(r5, r5)
            return
        L_0x00d1:
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r10 = r0.imagesArrLocations
            boolean r10 = r10.isEmpty()
            if (r10 != 0) goto L_0x011b
            if (r1 < 0) goto L_0x0117
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r10 = r0.imagesArrLocations
            int r10 = r10.size()
            if (r1 < r10) goto L_0x00e4
            goto L_0x0117
        L_0x00e4:
            im.bclpbkiauv.messenger.ImageLocation r10 = r0.currentFileLocation
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r11 = r0.imagesArrLocations
            java.lang.Object r11 = r11.get(r1)
            im.bclpbkiauv.messenger.ImageLocation r11 = (im.bclpbkiauv.messenger.ImageLocation) r11
            if (r19 == 0) goto L_0x010b
            if (r10 == 0) goto L_0x010b
            if (r11 == 0) goto L_0x010b
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r12 = r10.location
            int r12 = r12.local_id
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r13 = r11.location
            int r13 = r13.local_id
            if (r12 != r13) goto L_0x010b
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r12 = r10.location
            long r12 = r12.volume_id
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r14 = r11.location
            long r14 = r14.volume_id
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 != 0) goto L_0x010b
            r8 = 1
        L_0x010b:
            java.util.ArrayList<im.bclpbkiauv.messenger.ImageLocation> r12 = r0.imagesArrLocations
            java.lang.Object r12 = r12.get(r1)
            im.bclpbkiauv.messenger.ImageLocation r12 = (im.bclpbkiauv.messenger.ImageLocation) r12
            r0.currentFileLocation = r12
        L_0x0115:
            goto L_0x01b8
        L_0x0117:
            r0.closePhoto(r5, r5)
            return
        L_0x011b:
            java.util.ArrayList<java.lang.Object> r10 = r0.imagesArrLocals
            boolean r10 = r10.isEmpty()
            if (r10 != 0) goto L_0x0115
            if (r1 < 0) goto L_0x01b4
            java.util.ArrayList<java.lang.Object> r10 = r0.imagesArrLocals
            int r10 = r10.size()
            if (r1 < r10) goto L_0x012f
            goto L_0x01b4
        L_0x012f:
            java.util.ArrayList<java.lang.Object> r10 = r0.imagesArrLocals
            java.lang.Object r10 = r10.get(r1)
            boolean r11 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.BotInlineResult
            if (r11 == 0) goto L_0x0186
            r11 = r10
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r11 = (im.bclpbkiauv.tgnet.TLRPC.BotInlineResult) r11
            r0.currentBotInlineResult = r11
            im.bclpbkiauv.tgnet.TLRPC$Document r12 = r11.document
            if (r12 == 0) goto L_0x0155
            im.bclpbkiauv.tgnet.TLRPC$Document r12 = r11.document
            java.io.File r12 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r12)
            java.lang.String r12 = r12.getAbsolutePath()
            r0.currentPathObject = r12
            im.bclpbkiauv.tgnet.TLRPC$Document r12 = r11.document
            boolean r4 = im.bclpbkiauv.messenger.MessageObject.isVideoDocument(r12)
            goto L_0x0185
        L_0x0155:
            im.bclpbkiauv.tgnet.TLRPC$Photo r12 = r11.photo
            if (r12 == 0) goto L_0x0170
            im.bclpbkiauv.tgnet.TLRPC$Photo r12 = r11.photo
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r12 = r12.sizes
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.getPhotoSize()
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r12 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r12, r13)
            java.io.File r12 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r12)
            java.lang.String r12 = r12.getAbsolutePath()
            r0.currentPathObject = r12
            goto L_0x0185
        L_0x0170:
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r12 = r11.content
            boolean r12 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_webDocument
            if (r12 == 0) goto L_0x0185
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r12 = r11.content
            java.lang.String r12 = r12.url
            r0.currentPathObject = r12
            java.lang.String r12 = r11.type
            java.lang.String r13 = "video"
            boolean r4 = r12.equals(r13)
        L_0x0185:
            goto L_0x01b8
        L_0x0186:
            r11 = 0
            boolean r12 = r10 instanceof im.bclpbkiauv.messenger.MediaController.PhotoEntry
            if (r12 == 0) goto L_0x01a6
            r12 = r10
            im.bclpbkiauv.messenger.MediaController$PhotoEntry r12 = (im.bclpbkiauv.messenger.MediaController.PhotoEntry) r12
            java.lang.String r13 = r12.path
            r0.currentPathObject = r13
            if (r13 != 0) goto L_0x0198
            r0.closePhoto(r5, r5)
            return
        L_0x0198:
            boolean r4 = r12.isVideo
            java.io.File r13 = new java.io.File
            java.lang.String r14 = r12.path
            r13.<init>(r14)
            android.net.Uri r9 = android.net.Uri.fromFile(r13)
        L_0x01a5:
            goto L_0x01b8
        L_0x01a6:
            boolean r12 = r10 instanceof im.bclpbkiauv.messenger.MediaController.SearchImage
            if (r12 == 0) goto L_0x01a5
            r12 = r10
            im.bclpbkiauv.messenger.MediaController$SearchImage r12 = (im.bclpbkiauv.messenger.MediaController.SearchImage) r12
            java.lang.String r13 = r12.getPathToAttach()
            r0.currentPathObject = r13
            goto L_0x01b8
        L_0x01b4:
            r0.closePhoto(r5, r5)
            return
        L_0x01b8:
            im.bclpbkiauv.ui.PhotoViewer$PlaceProviderObject r10 = r0.currentPlaceObject
            if (r10 == 0) goto L_0x01c8
            int r11 = r0.animationInProgress
            if (r11 != 0) goto L_0x01c6
            im.bclpbkiauv.messenger.ImageReceiver r10 = r10.imageReceiver
            r10.setVisible(r6, r6)
            goto L_0x01c8
        L_0x01c6:
            r0.showAfterAnimation = r10
        L_0x01c8:
            im.bclpbkiauv.ui.PhotoViewer$PhotoViewerProvider r10 = r0.placeProvider
            im.bclpbkiauv.messenger.MessageObject r11 = r0.currentMessageObject
            im.bclpbkiauv.messenger.ImageLocation r12 = r0.currentFileLocation
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r12 = r0.getFileLocation(r12)
            int r13 = r0.currentIndex
            im.bclpbkiauv.ui.PhotoViewer$PlaceProviderObject r10 = r10.getPlaceForPhoto(r11, r12, r13, r5)
            r0.currentPlaceObject = r10
            if (r10 == 0) goto L_0x01e8
            int r11 = r0.animationInProgress
            if (r11 != 0) goto L_0x01e6
            im.bclpbkiauv.messenger.ImageReceiver r10 = r10.imageReceiver
            r10.setVisible(r5, r6)
            goto L_0x01e8
        L_0x01e6:
            r0.hideAfterAnimation = r10
        L_0x01e8:
            if (r8 != 0) goto L_0x0253
            r0.draggingDown = r5
            r10 = 0
            r0.translationX = r10
            r0.translationY = r10
            r11 = 1065353216(0x3f800000, float:1.0)
            r0.scale = r11
            r0.animateToX = r10
            r0.animateToY = r10
            r0.animateToScale = r11
            r12 = 0
            r0.animationStartTime = r12
            r0.imageMoveAnimation = r2
            r0.changeModeAnimation = r2
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r2 = r0.aspectRatioFrameLayout
            if (r2 == 0) goto L_0x020b
            r12 = 4
            r2.setVisibility(r12)
        L_0x020b:
            r0.pinchStartDistance = r10
            r0.pinchStartScale = r11
            r0.pinchCenterX = r10
            r0.pinchCenterY = r10
            r0.pinchStartX = r10
            r0.pinchStartY = r10
            r0.moveStartX = r10
            r0.moveStartY = r10
            r0.zooming = r5
            r0.moving = r5
            r0.doubleTap = r5
            r0.invalidCoords = r5
            r0.canDragDown = r6
            r0.changingPage = r5
            r0.switchImageAfterAnimation = r5
            int r2 = r0.sharedMediaType
            if (r2 == r6) goto L_0x024b
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0248
            java.lang.String[] r2 = r0.currentFileNames
            r2 = r2[r5]
            if (r2 == 0) goto L_0x0246
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r2 = r0.photoProgressViews
            r2 = r2[r5]
            int r2 = r2.backgroundState
            if (r2 == 0) goto L_0x0246
            goto L_0x0248
        L_0x0246:
            r2 = 0
            goto L_0x0249
        L_0x0248:
            r2 = 1
        L_0x0249:
            r0.canZoom = r2
        L_0x024b:
            float r2 = r0.scale
            r0.updateMinMax(r2)
            r0.releasePlayer(r5)
        L_0x0253:
            if (r4 == 0) goto L_0x025c
            if (r9 == 0) goto L_0x025c
            r0.isStreaming = r5
            r0.preparePlayer(r9, r5, r5)
        L_0x025c:
            r2 = -1
            if (r3 != r2) goto L_0x026d
            r17.setImages()
            r2 = 0
        L_0x0263:
            r6 = 3
            if (r2 >= r6) goto L_0x026c
            r0.checkProgress(r2, r5)
            int r2 = r2 + 1
            goto L_0x0263
        L_0x026c:
            goto L_0x02b9
        L_0x026d:
            r0.checkProgress(r5, r5)
            int r2 = r0.currentIndex
            if (r3 <= r2) goto L_0x0295
            im.bclpbkiauv.messenger.ImageReceiver r10 = r0.rightImage
            im.bclpbkiauv.messenger.ImageReceiver r11 = r0.centerImage
            r0.rightImage = r11
            im.bclpbkiauv.messenger.ImageReceiver r11 = r0.leftImage
            r0.centerImage = r11
            r0.leftImage = r10
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r11 = r0.photoProgressViews
            r12 = r11[r5]
            r13 = r11[r7]
            r11[r5] = r13
            r11[r7] = r12
            int r2 = r2 - r6
            r0.setIndexToImage(r10, r2)
            r0.checkProgress(r6, r5)
            r0.checkProgress(r7, r5)
            goto L_0x02b8
        L_0x0295:
            if (r3 >= r2) goto L_0x02b8
            im.bclpbkiauv.messenger.ImageReceiver r10 = r0.leftImage
            im.bclpbkiauv.messenger.ImageReceiver r11 = r0.centerImage
            r0.leftImage = r11
            im.bclpbkiauv.messenger.ImageReceiver r11 = r0.rightImage
            r0.centerImage = r11
            r0.rightImage = r10
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r11 = r0.photoProgressViews
            r12 = r11[r5]
            r13 = r11[r6]
            r11[r5] = r13
            r11[r6] = r12
            int r2 = r2 + r6
            r0.setIndexToImage(r10, r2)
            r0.checkProgress(r6, r5)
            r0.checkProgress(r7, r5)
            goto L_0x02b9
        L_0x02b8:
        L_0x02b9:
            return
        L_0x02ba:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PhotoViewer.setImageIndex(int, boolean):void");
    }

    private void setCurrentCaption(MessageObject messageObject, CharSequence caption, boolean animated) {
        CharSequence str;
        MessageObject messageObject2 = messageObject;
        if (this.needCaptionLayout) {
            if (this.captionTextView.getParent() != this.pickerView) {
                this.captionTextView.setBackgroundDrawable((Drawable) null);
                this.containerView.removeView(this.captionTextView);
                this.pickerView.addView(this.captionTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 83, 0.0f, 0.0f, 76.0f, 48.0f));
            }
        } else if (this.captionTextView.getParent() != this.containerView) {
            this.captionTextView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.pickerView.removeView(this.captionTextView);
            this.containerView.addView(this.captionTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        }
        if (this.isCurrentVideo) {
            if (this.captionTextView.getMaxLines() != 1) {
                this.captionTextView.setMaxLines(1);
            }
            if (!this.isSingleLine) {
                TextView textView = this.captionTextView;
                this.isSingleLine = true;
                textView.setSingleLine(true);
            }
        } else {
            if (this.isSingleLine) {
                TextView textView2 = this.captionTextView;
                this.isSingleLine = false;
                textView2.setSingleLine(false);
            }
            int newCount = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? 5 : 10;
            if (this.captionTextView.getMaxLines() != newCount) {
                this.captionTextView.setMaxLines(newCount);
            }
        }
        boolean wasVisisble = this.captionTextView.getTag() != null;
        if (!TextUtils.isEmpty(caption)) {
            Theme.createChatResources((Context) null, true);
            if (messageObject2 == null || messageObject2.messageOwner.entities.isEmpty()) {
                str = Emoji.replaceEmoji(new SpannableStringBuilder(caption), this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            } else {
                Spannable spannableString = SpannableString.valueOf(caption.toString());
                messageObject2.addEntitiesToText(spannableString, true, false);
                CharSequence charSequence = caption;
                str = Emoji.replaceEmoji(spannableString, this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.captionTextView.setTag(str);
            AnimatorSet animatorSet = this.currentCaptionAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentCaptionAnimation = null;
            }
            try {
                this.captionTextView.setText(str);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.captionTextView.setScrollY(0);
            this.captionTextView.setTextColor(-1);
            if (this.isActionBarVisible && (this.bottomLayout.getVisibility() == 0 || this.pickerView.getVisibility() == 0)) {
                this.captionTextView.setVisibility(0);
                if (!animated || wasVisisble) {
                    this.captionTextView.setAlpha(1.0f);
                    return;
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentCaptionAnimation = animatorSet2;
                animatorSet2.setDuration(200);
                this.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
                this.currentCaptionAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PhotoViewer.this.currentCaptionAnimation)) {
                            AnimatorSet unused = PhotoViewer.this.currentCaptionAnimation = null;
                        }
                    }
                });
                this.currentCaptionAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.captionTextView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(5.0f), 0.0f})});
                this.currentCaptionAnimation.start();
            } else if (this.captionTextView.getVisibility() == 0) {
                this.captionTextView.setVisibility(4);
                this.captionTextView.setAlpha(0.0f);
            }
        } else {
            CharSequence charSequence2 = caption;
            if (this.needCaptionLayout) {
                this.captionTextView.setText(LocaleController.getString("AddCaption", R.string.AddCaption));
                this.captionTextView.setTag("empty");
                this.captionTextView.setVisibility(0);
                this.captionTextView.setTextColor(-1291845633);
                return;
            }
            this.captionTextView.setTextColor(-1);
            this.captionTextView.setTag((Object) null);
            if (!animated || !wasVisisble) {
                this.captionTextView.setVisibility(4);
                return;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.currentCaptionAnimation = animatorSet3;
            animatorSet3.setDuration(200);
            this.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
            this.currentCaptionAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(PhotoViewer.this.currentCaptionAnimation)) {
                        PhotoViewer.this.captionTextView.setVisibility(4);
                        AnimatorSet unused = PhotoViewer.this.currentCaptionAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(PhotoViewer.this.currentCaptionAnimation)) {
                        AnimatorSet unused = PhotoViewer.this.currentCaptionAnimation = null;
                    }
                }
            });
            this.currentCaptionAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.captionTextView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(5.0f)})});
            this.currentCaptionAnimation.start();
        }
    }

    private void checkProgress(int a, boolean animated) {
        int index;
        boolean canStream;
        boolean isVideo;
        File f2;
        File f1;
        File f22;
        int i = a;
        boolean z = animated;
        int index2 = this.currentIndex;
        boolean canStream2 = true;
        if (i == 1) {
            index = index2 + 1;
        } else if (i == 2) {
            index = index2 - 1;
        } else {
            index = index2;
        }
        if (this.currentFileNames[i] != null) {
            File f12 = null;
            boolean isVideo2 = false;
            if (this.currentMessageObject != null) {
                if (index < 0 || index >= this.imagesArr.size()) {
                    this.photoProgressViews[i].setBackgroundState(-1, z);
                    return;
                }
                MessageObject messageObject = this.imagesArr.get(index);
                if (this.sharedMediaType != 1 || messageObject.canPreviewDocument()) {
                    if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                        f12 = new File(messageObject.messageOwner.attachPath);
                    }
                    if (!(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || messageObject.messageOwner.media.webpage == null || messageObject.messageOwner.media.webpage.document != null) {
                        f22 = FileLoader.getPathToMessage(messageObject.messageOwner);
                    } else {
                        f22 = FileLoader.getPathToAttach(getFileLocation(index, (int[]) null), true);
                    }
                    if (!SharedConfig.streamMedia || !messageObject.isVideo() || !messageObject.canStreamVideo() || ((int) messageObject.getDialogId()) == 0) {
                        canStream2 = false;
                    }
                    f1 = f12;
                    f2 = f22;
                    isVideo = messageObject.isVideo();
                    canStream = canStream2;
                } else {
                    this.photoProgressViews[i].setBackgroundState(-1, z);
                    return;
                }
            } else if (this.currentBotInlineResult != null) {
                if (index < 0 || index >= this.imagesArrLocals.size()) {
                    this.photoProgressViews[i].setBackgroundState(-1, z);
                    return;
                }
                TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) this.imagesArrLocals.get(index);
                if (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(botInlineResult.document)) {
                    if (botInlineResult.document != null) {
                        f12 = FileLoader.getPathToAttach(botInlineResult.document);
                    } else if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                        f12 = new File(FileLoader.getDirectory(4), Utilities.MD5(botInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content.url, "mp4"));
                    }
                    isVideo2 = true;
                } else if (botInlineResult.document != null) {
                    f12 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                } else if (botInlineResult.photo != null) {
                    f12 = new File(FileLoader.getDirectory(0), this.currentFileNames[i]);
                }
                f1 = f12;
                f2 = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                isVideo = isVideo2;
                canStream = false;
            } else if (this.currentFileLocation == null) {
                if (this.currentSecureDocument != null) {
                    if (index < 0 || index >= this.secureDocuments.size()) {
                        this.photoProgressViews[i].setBackgroundState(-1, z);
                        return;
                    }
                    f12 = FileLoader.getPathToAttach(this.secureDocuments.get(index), true);
                } else if (this.currentPathObject != null) {
                    f1 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                    f2 = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                    isVideo = false;
                    canStream = false;
                }
                f1 = f12;
                f2 = null;
                isVideo = false;
                canStream = false;
            } else if (index < 0 || index >= this.imagesArrLocations.size()) {
                this.photoProgressViews[i].setBackgroundState(-1, z);
                return;
            } else {
                TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = this.imagesArrLocations.get(index).location;
                if (this.avatarsDialogId == 0 && !this.isEvent) {
                    canStream2 = false;
                }
                f1 = FileLoader.getPathToAttach(tL_fileLocationToBeDeprecated, canStream2);
                f2 = null;
                isVideo = false;
                canStream = false;
            }
            File file = f1;
            $$Lambda$PhotoViewer$sd0eRzFLl4CMwZSYVZ4CNY6qQ r12 = r0;
            File file2 = f2;
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            $$Lambda$PhotoViewer$sd0eRzFLl4CMwZSYVZ4CNY6qQ r0 = new Runnable(f1, f2, canStream, isVideo, a, animated) {
                private final /* synthetic */ File f$1;
                private final /* synthetic */ File f$2;
                private final /* synthetic */ boolean f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ boolean f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    PhotoViewer.this.lambda$checkProgress$45$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            };
            dispatchQueue.postRunnable(r12);
            return;
        }
        boolean isLocalVideo = false;
        if (!this.imagesArrLocals.isEmpty() && index >= 0 && index < this.imagesArrLocals.size()) {
            Object object = this.imagesArrLocals.get(index);
            if (object instanceof MediaController.PhotoEntry) {
                isLocalVideo = ((MediaController.PhotoEntry) object).isVideo;
            }
        }
        if (isLocalVideo) {
            this.photoProgressViews[i].setBackgroundState(3, z);
        } else {
            this.photoProgressViews[i].setBackgroundState(-1, z);
        }
    }

    public /* synthetic */ void lambda$checkProgress$45$PhotoViewer(File f1Final, File f2Final, boolean canStreamFinal, boolean isVideoFianl, int a, boolean animated) {
        boolean exists = false;
        if (f1Final != null) {
            exists = f1Final.exists();
        }
        if (!exists && f2Final != null) {
            exists = f2Final.exists();
        }
        AndroidUtilities.runOnUIThread(new Runnable(f1Final, f2Final, exists, canStreamFinal, isVideoFianl, a, animated) {
            private final /* synthetic */ File f$1;
            private final /* synthetic */ File f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ int f$6;
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
                PhotoViewer.this.lambda$null$44$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$44$PhotoViewer(File f1Final, File f2Final, boolean existsFinal, boolean canStreamFinal, boolean isVideoFianl, int a, boolean animated) {
        boolean z = true;
        if (!(f1Final == null && f2Final == null) && (existsFinal || canStreamFinal)) {
            if (isVideoFianl) {
                this.photoProgressViews[a].setBackgroundState(3, animated);
            } else {
                this.photoProgressViews[a].setBackgroundState(-1, animated);
            }
            if (a == 0) {
                if (existsFinal) {
                    this.menuItem.hideSubItem(7);
                } else if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[a])) {
                    this.menuItem.hideSubItem(7);
                } else {
                    this.menuItem.showSubItem(7);
                }
            }
        } else {
            if (!isVideoFianl) {
                this.photoProgressViews[a].setBackgroundState(0, animated);
            } else if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[a])) {
                this.photoProgressViews[a].setBackgroundState(2, false);
            } else {
                this.photoProgressViews[a].setBackgroundState(1, false);
            }
            Float progress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[a]);
            if (progress == null) {
                progress = Float.valueOf(0.0f);
            }
            this.photoProgressViews[a].setProgress(progress.floatValue(), false);
        }
        if (a == 0) {
            if (this.imagesArrLocals.isEmpty() && (this.currentFileNames[0] == null || this.photoProgressViews[0].backgroundState == 0)) {
                z = false;
            }
            this.canZoom = z;
        }
    }

    public int getSelectiongLength() {
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            return photoViewerCaptionEnterView.getSelectionLength();
        }
        return 0;
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int index) {
        MessageObject messageObject;
        ImageReceiver.BitmapHolder placeHolder;
        TLObject photoObject;
        TLRPC.PhotoSize thumbLocation;
        TLRPC.PhotoSize thumbLocation2;
        Object parentObject;
        ImageReceiver.BitmapHolder placeHolder2;
        ImageReceiver.BitmapHolder placeHolder3;
        ImageReceiver.BitmapHolder placeHolder4;
        int cacheType;
        String path;
        int cacheType2;
        String filter;
        int imageSize;
        WebFile webDocument;
        TLRPC.Document document;
        TLObject tLObject;
        Drawable drawable;
        Activity activity;
        Drawable drawable2;
        Drawable drawable3;
        Activity activity2;
        Drawable drawable4;
        String path2;
        String filter2;
        String path3;
        String path4;
        ImageReceiver.BitmapHolder placeHolder5;
        ImageReceiver imageReceiver2 = imageReceiver;
        int i = index;
        imageReceiver2.setOrientation(0, false);
        if (!this.secureDocuments.isEmpty()) {
            if (i >= 0 && i < this.secureDocuments.size()) {
                SecureDocument secureDocument = this.secureDocuments.get(i);
                int photoSize = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
                ImageReceiver.BitmapHolder placeHolder6 = null;
                if (this.currentThumb != null && imageReceiver2 == this.centerImage) {
                    placeHolder6 = this.currentThumb;
                }
                if (placeHolder6 == null) {
                    placeHolder5 = this.placeProvider.getThumbForPhoto((MessageObject) null, (TLRPC.FileLocation) null, i);
                } else {
                    placeHolder5 = placeHolder6;
                }
                SecureDocument document2 = this.secureDocuments.get(i);
                int imageSize2 = document2.secureFile.size;
                int i2 = imageSize2;
                SecureDocument secureDocument2 = document2;
                imageReceiver.setImage(ImageLocation.getForSecureDocument(document2), "d", (ImageLocation) null, (String) null, placeHolder5 != null ? new BitmapDrawable(placeHolder5.bitmap) : null, imageSize2, (String) null, (Object) null, 0);
            }
        } else if (this.imagesArrLocals.isEmpty()) {
            if (this.imagesArr.isEmpty() || i < 0 || i >= this.imagesArr.size()) {
                messageObject = null;
            } else {
                imageReceiver2.setShouldGenerateQualityThumb(true);
                messageObject = this.imagesArr.get(i);
            }
            if (messageObject != null) {
                if (messageObject.isVideo()) {
                    imageReceiver2.setNeedsQualityThumb(true);
                    if (messageObject.photoThumbs == null || messageObject.photoThumbs.isEmpty()) {
                        imageReceiver2.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                        return;
                    }
                    if (this.currentThumb == null || imageReceiver2 != this.centerImage) {
                        placeHolder3 = null;
                    } else {
                        placeHolder3 = this.currentThumb;
                    }
                    imageReceiver.setImage((ImageLocation) null, (String) null, placeHolder3 == null ? ImageLocation.getForObject(FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100), messageObject.photoThumbsObject) : null, "b", placeHolder3 != null ? new BitmapDrawable(placeHolder3.bitmap) : null, 0, (String) null, messageObject, 1);
                    return;
                }
                AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
                if (animatedFileDrawable != null) {
                    imageReceiver2.setImageBitmap((Drawable) animatedFileDrawable);
                    this.currentAnimation.setSecondParentView(this.containerView);
                    return;
                } else if (this.sharedMediaType == 1) {
                    if (messageObject.canPreviewDocument()) {
                        TLRPC.Document document3 = messageObject.getDocument();
                        imageReceiver2.setNeedsQualityThumb(true);
                        if (this.currentThumb == null || imageReceiver2 != this.centerImage) {
                            placeHolder2 = null;
                        } else {
                            placeHolder2 = this.currentThumb;
                        }
                        TLRPC.PhotoSize thumbLocation3 = messageObject != null ? FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100) : null;
                        int size = (int) (2048.0f / AndroidUtilities.density);
                        int i3 = size;
                        TLRPC.PhotoSize photoSize2 = thumbLocation3;
                        imageReceiver.setImage(ImageLocation.getForDocument(document3), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size), Integer.valueOf(size)}), placeHolder2 == null ? ImageLocation.getForDocument(thumbLocation3, document3) : null, "b", placeHolder2 != null ? new BitmapDrawable(placeHolder2.bitmap) : null, document3.size, (String) null, messageObject, 0);
                        return;
                    }
                    imageReceiver2.setImageBitmap((Drawable) new OtherDocumentPlaceholderDrawable(this.parentActivity, this.containerView, messageObject));
                    return;
                }
            }
            int[] size2 = new int[1];
            ImageLocation imageLocation = getImageLocation(i, size2);
            TLObject fileLocation = getFileLocation(i, size2);
            if (imageLocation != null) {
                imageReceiver2.setNeedsQualityThumb(true);
                if (this.currentThumb == null || imageReceiver2 != this.centerImage) {
                    placeHolder = null;
                } else {
                    placeHolder = this.currentThumb;
                }
                if (size2[0] == 0) {
                    size2[0] = -1;
                }
                if (messageObject != null) {
                    thumbLocation = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
                    photoObject = messageObject.photoThumbsObject;
                } else {
                    thumbLocation = null;
                    photoObject = null;
                }
                if (thumbLocation == null || thumbLocation != fileLocation) {
                    thumbLocation2 = thumbLocation;
                } else {
                    thumbLocation2 = null;
                }
                boolean cacheOnly = (messageObject != null && messageObject.isWebpage()) || this.avatarsDialogId != 0 || this.isEvent;
                if (messageObject != null) {
                    parentObject = messageObject;
                } else {
                    int i4 = this.avatarsDialogId;
                    if (i4 == 0) {
                        parentObject = null;
                    } else if (i4 > 0) {
                        parentObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.avatarsDialogId));
                    } else {
                        parentObject = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-this.avatarsDialogId));
                    }
                }
                ImageLocation forObject = placeHolder == null ? ImageLocation.getForObject(thumbLocation2, photoObject) : null;
                BitmapDrawable bitmapDrawable = placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null;
                TLRPC.PhotoSize photoSize3 = thumbLocation2;
                TLObject tLObject2 = photoObject;
                ImageReceiver.BitmapHolder bitmapHolder = placeHolder;
                TLObject tLObject3 = fileLocation;
                imageReceiver.setImage(imageLocation, (String) null, forObject, "b", bitmapDrawable, size2[0], (String) null, parentObject, cacheOnly ? 1 : 0);
                return;
            }
            imageReceiver2.setNeedsQualityThumb(true);
            if (size2[0] == 0) {
                imageReceiver2.setImageBitmap((Bitmap) null);
            } else {
                imageReceiver2.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
            }
        } else if (i < 0 || i >= this.imagesArrLocals.size()) {
            imageReceiver2.setImageBitmap((Bitmap) null);
        } else {
            Object object = this.imagesArrLocals.get(i);
            int size3 = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
            ImageReceiver.BitmapHolder placeHolder7 = null;
            if (this.currentThumb != null && imageReceiver2 == this.centerImage) {
                placeHolder7 = this.currentThumb;
            }
            if (placeHolder7 == null) {
                placeHolder4 = this.placeProvider.getThumbForPhoto((MessageObject) null, (TLRPC.FileLocation) null, i);
            } else {
                placeHolder4 = placeHolder7;
            }
            TLRPC.Document document4 = null;
            WebFile webDocument2 = null;
            TLRPC.PhotoSize photo = null;
            TLObject photoObject2 = null;
            int imageSize3 = 0;
            String filter3 = null;
            int cacheType3 = 0;
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                int isVideo = photoEntry.isVideo;
                if (!photoEntry.isVideo) {
                    if (photoEntry.imagePath != null) {
                        path4 = photoEntry.imagePath;
                    } else {
                        imageReceiver2.setOrientation(photoEntry.orientation, false);
                        path4 = photoEntry.path;
                    }
                    filter3 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size3), Integer.valueOf(size3)});
                    path3 = path4;
                } else if (photoEntry.thumbPath != null) {
                    path3 = photoEntry.thumbPath;
                } else {
                    path3 = "vthumb://" + photoEntry.imageId + LogUtils.COLON + photoEntry.path;
                }
                path = path3;
                filter = filter3;
                cacheType = 0;
                cacheType2 = isVideo;
                imageSize = 0;
                webDocument = null;
                tLObject = null;
                document = null;
            } else {
                if (object instanceof TLRPC.BotInlineResult) {
                    cacheType3 = 1;
                    TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) object;
                    if (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO)) {
                        path = null;
                    } else if (MessageObject.isVideoDocument(botInlineResult.document)) {
                        path = null;
                    } else if (botInlineResult.type.equals("gif") && botInlineResult.document != null) {
                        path = null;
                        document4 = botInlineResult.document;
                        imageSize3 = botInlineResult.document.size;
                        filter3 = "d";
                    } else if (botInlineResult.photo != null) {
                        TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize());
                        TLObject photoObject3 = botInlineResult.photo;
                        int imageSize4 = sizeFull.size;
                        path = null;
                        TLRPC.PhotoSize photoSize4 = sizeFull;
                        filter3 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size3), Integer.valueOf(size3)});
                        photoObject2 = photoObject3;
                        imageSize3 = imageSize4;
                        photo = sizeFull;
                    } else {
                        path = null;
                        if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                            if (botInlineResult.type.equals("gif")) {
                                filter2 = "d";
                            } else {
                                filter2 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size3), Integer.valueOf(size3)});
                            }
                            filter3 = filter2;
                            webDocument2 = WebFile.createWithWebDocument(botInlineResult.content);
                        }
                    }
                    if (botInlineResult.document != null) {
                        photo = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.document.thumbs, 90);
                        photoObject2 = botInlineResult.document;
                    } else if (botInlineResult.thumb instanceof TLRPC.TL_webDocument) {
                        webDocument2 = WebFile.createWithWebDocument(botInlineResult.thumb);
                    }
                } else {
                    path = null;
                    if (object instanceof MediaController.SearchImage) {
                        MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) object;
                        if (photoEntry2.photoSize != null) {
                            photo = photoEntry2.photoSize;
                            photoObject2 = photoEntry2.photo;
                            imageSize3 = photoEntry2.photoSize.size;
                            path2 = null;
                        } else if (photoEntry2.imagePath != null) {
                            path2 = photoEntry2.imagePath;
                        } else if (photoEntry2.document != null) {
                            document4 = photoEntry2.document;
                            imageSize3 = photoEntry2.document.size;
                            path2 = null;
                        } else {
                            path2 = photoEntry2.imageUrl;
                            imageSize3 = photoEntry2.size;
                        }
                        path = path2;
                        cacheType = 1;
                        cacheType2 = 0;
                        filter = "d";
                        imageSize = imageSize3;
                        webDocument = null;
                        tLObject = photoObject2;
                        document = document4;
                    }
                }
                cacheType = cacheType3;
                cacheType2 = 0;
                filter = filter3;
                imageSize = imageSize3;
                webDocument = webDocument2;
                tLObject = photoObject2;
                document = document4;
            }
            if (document != null) {
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                ImageLocation forDocument = ImageLocation.getForDocument(document);
                ImageLocation forDocument2 = placeHolder4 == null ? ImageLocation.getForDocument(thumb, document) : null;
                String format = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(size3), Integer.valueOf(size3)});
                TLRPC.PhotoSize photoSize5 = thumb;
                int i5 = size3;
                TLObject tLObject4 = tLObject;
                TLRPC.PhotoSize photoSize6 = photo;
                TLRPC.Document document5 = document;
                imageReceiver.setImage(forDocument, "d", forDocument2, format, placeHolder4 != null ? new BitmapDrawable(placeHolder4.bitmap) : null, imageSize, (String) null, object, cacheType);
                return;
            }
            TLRPC.Document document6 = document;
            int i6 = size3;
            TLObject photoObject4 = tLObject;
            if (photo != null) {
                imageReceiver.setImage(ImageLocation.getForObject(photo, photoObject4), filter, (Drawable) placeHolder4 != null ? new BitmapDrawable(placeHolder4.bitmap) : null, imageSize, (String) null, object, cacheType);
            } else if (webDocument != null) {
                ImageLocation forWebFile = ImageLocation.getForWebFile(webDocument);
                if (placeHolder4 != null) {
                    drawable4 = new BitmapDrawable(placeHolder4.bitmap);
                } else if (cacheType2 == 0 || (activity2 = this.parentActivity) == null) {
                    drawable3 = null;
                    imageReceiver.setImage(forWebFile, filter, drawable3, (String) null, object, cacheType);
                } else {
                    drawable4 = activity2.getResources().getDrawable(R.drawable.nophotos);
                }
                drawable3 = drawable4;
                imageReceiver.setImage(forWebFile, filter, drawable3, (String) null, object, cacheType);
            } else {
                if (placeHolder4 != null) {
                    drawable2 = new BitmapDrawable(placeHolder4.bitmap);
                } else if (cacheType2 == 0 || (activity = this.parentActivity) == null) {
                    drawable = null;
                    imageReceiver.setImage(path, filter, drawable, (String) null, imageSize);
                } else {
                    drawable2 = activity.getResources().getDrawable(R.drawable.nophotos);
                }
                drawable = drawable2;
                imageReceiver.setImage(path, filter, drawable, (String) null, imageSize);
            }
        }
    }

    public static boolean isShowingImage(MessageObject object) {
        boolean result = false;
        boolean result2 = true;
        if (Instance != null) {
            result = !Instance.pipAnimationInProgress && Instance.isVisible && !Instance.disableShowCheck && object != null && Instance.currentMessageObject != null && Instance.currentMessageObject.getId() == object.getId() && Instance.currentMessageObject.getDialogId() == object.getDialogId();
        }
        if (result || PipInstance == null) {
            return result;
        }
        if (!PipInstance.isVisible || PipInstance.disableShowCheck || object == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != object.getId() || PipInstance.currentMessageObject.getDialogId() != object.getDialogId()) {
            result2 = false;
        }
        return result2;
    }

    public static boolean isPlayingMessageInPip(MessageObject object) {
        return (PipInstance == null || object == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != object.getId() || PipInstance.currentMessageObject.getDialogId() != object.getDialogId()) ? false : true;
    }

    public static boolean isPlayingMessage(MessageObject object) {
        return Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && object != null && Instance.currentMessageObject != null && Instance.currentMessageObject.getId() == object.getId() && Instance.currentMessageObject.getDialogId() == object.getDialogId();
    }

    public static boolean isShowingImage(TLRPC.FileLocation object) {
        if (Instance == null) {
            return false;
        }
        return Instance.isVisible && !Instance.disableShowCheck && object != null && Instance.currentFileLocation != null && object.local_id == Instance.currentFileLocation.location.local_id && object.volume_id == Instance.currentFileLocation.location.volume_id && object.dc_id == Instance.currentFileLocation.dc_id;
    }

    public static boolean isShowingImage(TLRPC.BotInlineResult object) {
        if (Instance == null) {
            return false;
        }
        return Instance.isVisible && !Instance.disableShowCheck && object != null && Instance.currentBotInlineResult != null && object.id == Instance.currentBotInlineResult.id;
    }

    public static boolean isShowingImage(String object) {
        if (Instance == null) {
            return false;
        }
        return Instance.isVisible && !Instance.disableShowCheck && object != null && object.equals(Instance.currentPathObject);
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        this.maxSelectedPhotos = value;
        this.allowOrder = order;
    }

    public boolean openPhoto(MessageObject messageObject, long dialogId, long mergeDialogId2, PhotoViewerProvider provider) {
        return openPhoto(messageObject, (TLRPC.FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, provider, (ChatActivity) null, dialogId, mergeDialogId2, true);
    }

    public boolean openPhoto(MessageObject messageObject, long dialogId, long mergeDialogId2, PhotoViewerProvider provider, boolean fullScreenVideo) {
        return openPhoto(messageObject, (TLRPC.FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, provider, (ChatActivity) null, dialogId, mergeDialogId2, fullScreenVideo);
    }

    public boolean openPhoto(TLRPC.FileLocation fileLocation, PhotoViewerProvider provider) {
        return openPhoto((MessageObject) null, fileLocation, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, provider, (ChatActivity) null, 0, 0, true);
    }

    public boolean openPhoto(TLRPC.FileLocation fileLocation, ImageLocation imageLocation, PhotoViewerProvider provider) {
        return openPhoto((MessageObject) null, fileLocation, imageLocation, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, provider, (ChatActivity) null, 0, 0, true);
    }

    public boolean openPhoto(ArrayList<MessageObject> messages, int index, long dialogId, long mergeDialogId2, PhotoViewerProvider provider) {
        return openPhoto(messages.get(index), (TLRPC.FileLocation) null, (ImageLocation) null, messages, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, index, provider, (ChatActivity) null, dialogId, mergeDialogId2, true);
    }

    public boolean openPhoto(ArrayList<SecureDocument> documents, int index, PhotoViewerProvider provider) {
        return openPhoto((MessageObject) null, (TLRPC.FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, documents, (ArrayList<Object>) null, index, provider, (ChatActivity) null, 0, 0, true);
    }

    public boolean openPhotoForSelect(ArrayList<Object> photos, int index, int type, PhotoViewerProvider provider, ChatActivity chatActivity, boolean need) {
        return openPhotoForSelect(photos, index, type, provider, chatActivity);
    }

    public void setIsFcCrop(boolean isFcCrop2) {
        this.isFcCrop = isFcCrop2;
    }

    public boolean openPhotoForSelect(ArrayList<Object> photos, int index, int type, PhotoViewerProvider provider, ChatActivity chatActivity) {
        this.sendPhotoType = type;
        ImageView imageView = this.pickerViewSendButton;
        if (imageView != null) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            int i = this.sendPhotoType;
            if (i == 4 || i == 5) {
                this.pickerViewSendButton.setImageResource(R.drawable.attach_send);
                layoutParams2.bottomMargin = AndroidUtilities.dp(19.0f);
            } else if (i == 1 || i == 3) {
                this.pickerViewSendButton.setImageResource(R.drawable.floating_check);
                this.pickerViewSendButton.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
                layoutParams2.bottomMargin = AndroidUtilities.dp(19.0f);
            } else {
                this.pickerViewSendButton.setImageResource(R.drawable.attach_send);
                layoutParams2.bottomMargin = AndroidUtilities.dp(14.0f);
            }
            this.pickerViewSendButton.setLayoutParams(layoutParams2);
        }
        return openPhoto((MessageObject) null, (TLRPC.FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, photos, index, provider, chatActivity, 0, 0, true);
    }

    private boolean checkAnimation() {
        if (this.animationInProgress != 0 && Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            Runnable runnable = this.animationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.animationEndRunnable = null;
            }
            this.animationInProgress = 0;
        }
        if (this.animationInProgress != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void setCropTranslations(boolean animated) {
        if (this.sendPhotoType == 1) {
            int bitmapWidth = this.centerImage.getBitmapWidth();
            int bitmapHeight = this.centerImage.getBitmapHeight();
            if (bitmapWidth != 0 && bitmapHeight != 0) {
                float scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                float scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                float scaleFinal = scaleX > scaleY ? scaleY : scaleX;
                float minSide = (float) Math.min(getContainerViewWidth(1), getContainerViewHeight(1));
                float newScaleX = minSide / ((float) bitmapWidth);
                float newScaleY = minSide / ((float) bitmapHeight);
                float newScale = newScaleX > newScaleY ? newScaleX : newScaleY;
                if (animated) {
                    this.animationStartTime = System.currentTimeMillis();
                    this.animateToX = (float) ((getLeftInset() / 2) - (getRightInset() / 2));
                    int i = this.currentEditMode;
                    if (i == 2) {
                        this.animateToY = (float) (AndroidUtilities.dp(92.0f) - AndroidUtilities.dp(56.0f));
                    } else if (i == 3) {
                        this.animateToY = (float) (AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(56.0f));
                    }
                    this.animateToScale = newScale / scaleFinal;
                    this.zoomAnimation = true;
                    return;
                }
                this.animationStartTime = 0;
                this.translationX = (float) ((getLeftInset() / 2) - (getRightInset() / 2));
                this.translationY = (float) ((-AndroidUtilities.dp(56.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                float f = newScale / scaleFinal;
                this.scale = f;
                updateMinMax(f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setCropBitmap() {
        if (this.sendPhotoType == 1) {
            Bitmap bitmap = this.centerImage.getBitmap();
            int orientation = this.centerImage.getOrientation();
            if (bitmap == null) {
                bitmap = this.animatingImageView.getBitmap();
                orientation = this.animatingImageView.getOrientation();
            }
            if (bitmap != null) {
                this.photoCropView.setBitmap(bitmap, orientation, false, false);
                if (this.currentEditMode == 0) {
                    setCropTranslations(false);
                }
            }
        }
    }

    private void initCropView() {
        if (this.sendPhotoType == 1) {
            this.photoCropView.setBitmap((Bitmap) null, 0, false, false);
            this.photoCropView.onAppear();
            this.photoCropView.setVisibility(0);
            this.photoCropView.setAlpha(1.0f);
            this.photoCropView.onAppeared();
            this.padImageForHorizontalInsets = true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:105:0x0313  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0386  */
    /* JADX WARNING: Removed duplicated region for block: B:131:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x013e  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x016a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean openPhoto(im.bclpbkiauv.messenger.MessageObject r25, im.bclpbkiauv.tgnet.TLRPC.FileLocation r26, im.bclpbkiauv.messenger.ImageLocation r27, java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r28, java.util.ArrayList<im.bclpbkiauv.messenger.SecureDocument> r29, java.util.ArrayList<java.lang.Object> r30, int r31, im.bclpbkiauv.ui.PhotoViewer.PhotoViewerProvider r32, im.bclpbkiauv.ui.ChatActivity r33, long r34, long r36, boolean r38) {
        /*
            r24 = this;
            r10 = r24
            r11 = r25
            r12 = r26
            r13 = r32
            r14 = r33
            android.app.Activity r0 = r10.parentActivity
            r15 = 0
            if (r0 == 0) goto L_0x03ad
            boolean r0 = r10.isVisible
            if (r0 != 0) goto L_0x03ad
            if (r13 != 0) goto L_0x001f
            boolean r0 = r24.checkAnimation()
            if (r0 != 0) goto L_0x001c
            goto L_0x001f
        L_0x001c:
            r1 = 0
            goto L_0x03ae
        L_0x001f:
            if (r11 != 0) goto L_0x002c
            if (r12 != 0) goto L_0x002c
            if (r28 != 0) goto L_0x002c
            if (r30 != 0) goto L_0x002c
            if (r29 != 0) goto L_0x002c
            if (r27 != 0) goto L_0x002c
            goto L_0x001c
        L_0x002c:
            r9 = 1
            r8 = r31
            im.bclpbkiauv.ui.PhotoViewer$PlaceProviderObject r7 = r13.getPlaceForPhoto(r11, r12, r8, r9)
            r1 = 0
            r10.lastInsets = r1
            android.app.Activity r0 = r10.parentActivity
            java.lang.String r2 = "window"
            java.lang.Object r0 = r0.getSystemService(r2)
            r6 = r0
            android.view.WindowManager r6 = (android.view.WindowManager) r6
            boolean r0 = r10.attachedToWindow
            if (r0 == 0) goto L_0x004d
            android.widget.FrameLayout r0 = r10.windowView     // Catch:{ Exception -> 0x004c }
            r6.removeView(r0)     // Catch:{ Exception -> 0x004c }
            goto L_0x004d
        L_0x004c:
            r0 = move-exception
        L_0x004d:
            android.view.WindowManager$LayoutParams r0 = r10.windowLayoutParams     // Catch:{ Exception -> 0x03a4 }
            r2 = 99
            r0.type = r2     // Catch:{ Exception -> 0x03a4 }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x03a4 }
            r2 = 21
            if (r0 < r2) goto L_0x0067
            android.view.WindowManager$LayoutParams r0 = r10.windowLayoutParams     // Catch:{ Exception -> 0x0061 }
            r3 = -2147286784(0xffffffff80030100, float:-2.75865E-40)
            r0.flags = r3     // Catch:{ Exception -> 0x0061 }
            goto L_0x006d
        L_0x0061:
            r0 = move-exception
            r15 = r6
            r16 = r7
            goto L_0x03a8
        L_0x0067:
            android.view.WindowManager$LayoutParams r0 = r10.windowLayoutParams     // Catch:{ Exception -> 0x03a4 }
            r3 = 131072(0x20000, float:1.83671E-40)
            r0.flags = r3     // Catch:{ Exception -> 0x03a4 }
        L_0x006d:
            if (r14 == 0) goto L_0x007e
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r0 = r33.getCurrentEncryptedChat()     // Catch:{ Exception -> 0x0061 }
            if (r0 == 0) goto L_0x007e
            android.view.WindowManager$LayoutParams r0 = r10.windowLayoutParams     // Catch:{ Exception -> 0x0061 }
            int r3 = r0.flags     // Catch:{ Exception -> 0x0061 }
            r3 = r3 | 8192(0x2000, float:1.14794E-41)
            r0.flags = r3     // Catch:{ Exception -> 0x0061 }
            goto L_0x0086
        L_0x007e:
            android.view.WindowManager$LayoutParams r0 = r10.windowLayoutParams     // Catch:{ Exception -> 0x03a4 }
            int r3 = r0.flags     // Catch:{ Exception -> 0x03a4 }
            r3 = r3 & -8193(0xffffffffffffdfff, float:NaN)
            r0.flags = r3     // Catch:{ Exception -> 0x03a4 }
        L_0x0086:
            android.view.WindowManager$LayoutParams r0 = r10.windowLayoutParams     // Catch:{ Exception -> 0x03a4 }
            r3 = 272(0x110, float:3.81E-43)
            r0.softInputMode = r3     // Catch:{ Exception -> 0x03a4 }
            android.widget.FrameLayout r0 = r10.windowView     // Catch:{ Exception -> 0x03a4 }
            r0.setFocusable(r15)     // Catch:{ Exception -> 0x03a4 }
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r0 = r10.containerView     // Catch:{ Exception -> 0x03a4 }
            r0.setFocusable(r15)     // Catch:{ Exception -> 0x03a4 }
            android.widget.FrameLayout r0 = r10.windowView     // Catch:{ Exception -> 0x03a4 }
            android.view.WindowManager$LayoutParams r4 = r10.windowLayoutParams     // Catch:{ Exception -> 0x03a4 }
            r6.addView(r0, r4)     // Catch:{ Exception -> 0x03a4 }
            r10.doneButtonPressed = r15
            r10.parentChatActivity = r14
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r10.actionBar
            r4 = 2131692467(0x7f0f0bb3, float:1.9014035E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.Integer r16 = java.lang.Integer.valueOf(r9)
            r5[r15] = r16
            java.lang.Integer r16 = java.lang.Integer.valueOf(r9)
            r5[r9] = r16
            java.lang.String r3 = "Of"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r4, r5)
            r0.setTitle(r3)
            int r0 = r10.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.fileDidFailToLoad
            r0.addObserver(r10, r3)
            int r0 = r10.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.fileDidLoad
            r0.addObserver(r10, r3)
            int r0 = r10.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.FileLoadProgressChanged
            r0.addObserver(r10, r3)
            int r0 = r10.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.mediaCountDidLoad
            r0.addObserver(r10, r3)
            int r0 = r10.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.mediaDidLoad
            r0.addObserver(r10, r3)
            int r0 = r10.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.dialogPhotosLoaded
            r0.addObserver(r10, r3)
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.emojiDidLoad
            r0.addObserver(r10, r3)
            int r0 = r10.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.filePreparingFailed
            r0.addObserver(r10, r3)
            int r0 = r10.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.fileNewChunkAvailable
            r0.addObserver(r10, r3)
            int r0 = r10.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.saveGallerySetChanged
            r0.addObserver(r10, r3)
            r10.placeProvider = r13
            r4 = r36
            r10.mergeDialogId = r4
            r1 = r34
            r10.currentDialogId = r1
            im.bclpbkiauv.ui.PhotoViewer$ListAdapter r0 = r10.selectedPhotosAdapter
            r0.notifyDataSetChanged()
            android.view.VelocityTracker r0 = r10.velocityTracker
            if (r0 != 0) goto L_0x0144
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r10.velocityTracker = r0
        L_0x0144:
            r10.isVisible = r9
            r10.togglePhotosListView(r15, r15)
            r0 = r38 ^ 1
            r10.openedFullScreenVideo = r0
            if (r0 == 0) goto L_0x0153
            r10.toggleActionBar(r15, r15)
            goto L_0x0161
        L_0x0153:
            int r0 = r10.sendPhotoType
            if (r0 != r9) goto L_0x015e
            r24.createCropView()
            r10.toggleActionBar(r15, r15)
            goto L_0x0161
        L_0x015e:
            r10.toggleActionBar(r9, r15)
        L_0x0161:
            r0 = 0
            r10.seekToProgressPending2 = r0
            r10.skipFirstBufferingProgress = r15
            r10.playerInjected = r15
            if (r7 == 0) goto L_0x0313
            r10.disableShowCheck = r9
            r10.animationInProgress = r9
            if (r11 == 0) goto L_0x01da
            im.bclpbkiauv.messenger.ImageReceiver r3 = r7.imageReceiver
            im.bclpbkiauv.ui.components.AnimatedFileDrawable r3 = r3.getAnimation()
            r10.currentAnimation = r3
            if (r3 == 0) goto L_0x01da
            boolean r3 = r25.isVideo()
            if (r3 == 0) goto L_0x01cd
            im.bclpbkiauv.messenger.ImageReceiver r3 = r7.imageReceiver
            r3.setAllowStartAnimation(r15)
            im.bclpbkiauv.messenger.ImageReceiver r3 = r7.imageReceiver
            r3.stopAnimation()
            im.bclpbkiauv.messenger.MediaController r3 = im.bclpbkiauv.messenger.MediaController.getInstance()
            boolean r3 = r3.isPlayingMessage(r11)
            if (r3 == 0) goto L_0x0198
            float r3 = r11.audioProgress
            r10.seekToProgressPending2 = r3
        L_0x0198:
            im.bclpbkiauv.ui.components.VideoPlayer r3 = r10.injectingVideoPlayer
            if (r3 != 0) goto L_0x01c6
            int r3 = r11.currentAccount
            im.bclpbkiauv.messenger.FileLoader r3 = im.bclpbkiauv.messenger.FileLoader.getInstance(r3)
            im.bclpbkiauv.tgnet.TLRPC$Document r0 = r25.getDocument()
            boolean r0 = r3.isLoadingVideo(r0, r9)
            if (r0 != 0) goto L_0x01c6
            im.bclpbkiauv.ui.components.AnimatedFileDrawable r0 = r10.currentAnimation
            boolean r0 = r0.hasBitmap()
            if (r0 != 0) goto L_0x01c4
            int r0 = r11.currentAccount
            im.bclpbkiauv.messenger.FileLoader r0 = im.bclpbkiauv.messenger.FileLoader.getInstance(r0)
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r25.getDocument()
            boolean r0 = r0.isLoadingVideo(r3, r15)
            if (r0 != 0) goto L_0x01c6
        L_0x01c4:
            r0 = 1
            goto L_0x01c7
        L_0x01c6:
            r0 = 0
        L_0x01c7:
            r10.skipFirstBufferingProgress = r0
            r3 = 0
            r10.currentAnimation = r3
            goto L_0x01da
        L_0x01cd:
            r3 = 0
            java.util.ArrayList r0 = r11.getWebPagePhotos(r3, r3)
            int r0 = r0.size()
            if (r0 <= r9) goto L_0x01da
            r10.currentAnimation = r3
        L_0x01da:
            r1 = r24
            r2 = r25
            r3 = r26
            r4 = r27
            r5 = r28
            r20 = r6
            r6 = r29
            r18 = r7
            r7 = r30
            r8 = r31
            r15 = 1
            r9 = r18
            r1.onPhotoShow(r2, r3, r4, r5, r6, r7, r8, r9)
            int r0 = r10.sendPhotoType
            if (r0 != r15) goto L_0x0209
            im.bclpbkiauv.ui.components.PhotoCropView r0 = r10.photoCropView
            r1 = 0
            r0.setVisibility(r1)
            im.bclpbkiauv.ui.components.PhotoCropView r0 = r10.photoCropView
            r2 = 0
            r0.setAlpha(r2)
            im.bclpbkiauv.ui.components.PhotoCropView r0 = r10.photoCropView
            r0.setFreeform(r1)
        L_0x0209:
            r9 = r18
            im.bclpbkiauv.messenger.ImageReceiver r0 = r9.imageReceiver
            android.graphics.RectF r0 = r0.getDrawRegion()
            float r8 = r0.left
            float r7 = r0.top
            im.bclpbkiauv.messenger.ImageReceiver r1 = r9.imageReceiver
            int r1 = r1.getOrientation()
            im.bclpbkiauv.messenger.ImageReceiver r2 = r9.imageReceiver
            int r16 = r2.getAnimatedOrientation()
            if (r16 == 0) goto L_0x0227
            r1 = r16
            r6 = r1
            goto L_0x0228
        L_0x0227:
            r6 = r1
        L_0x0228:
            im.bclpbkiauv.ui.components.ClippingImageView[] r5 = r10.getAnimatingImageViews(r9)
            r1 = 0
        L_0x022d:
            int r2 = r5.length
            if (r1 >= r2) goto L_0x025f
            r2 = r5[r1]
            float[][] r3 = r10.animationValues
            r2.setAnimationValues(r3)
            r2 = r5[r1]
            r3 = 0
            r2.setVisibility(r3)
            r2 = r5[r1]
            int r3 = r9.radius
            r2.setRadius(r3)
            r2 = r5[r1]
            r2.setOrientation(r6)
            r2 = r5[r1]
            int r3 = r9.radius
            if (r3 == 0) goto L_0x0251
            r3 = 1
            goto L_0x0252
        L_0x0251:
            r3 = 0
        L_0x0252:
            r2.setNeedRadius(r3)
            r2 = r5[r1]
            im.bclpbkiauv.messenger.ImageReceiver$BitmapHolder r3 = r9.thumb
            r2.setImageBitmap(r3)
            int r1 = r1 + 1
            goto L_0x022d
        L_0x025f:
            r24.initCropView()
            int r1 = r10.sendPhotoType
            if (r1 != r15) goto L_0x0272
            im.bclpbkiauv.ui.components.PhotoCropView r1 = r10.photoCropView
            r1.hideBackView()
            im.bclpbkiauv.ui.components.PhotoCropView r1 = r10.photoCropView
            r2 = 1065353216(0x3f800000, float:1.0)
            r1.setAspectRatio(r2)
        L_0x0272:
            im.bclpbkiauv.ui.components.ClippingImageView r1 = r10.animatingImageView
            android.view.ViewGroup$LayoutParams r4 = r1.getLayoutParams()
            float r1 = r0.width()
            int r1 = (int) r1
            r4.width = r1
            float r1 = r0.height()
            int r1 = (int) r1
            r4.height = r1
            int r1 = r4.width
            if (r1 != 0) goto L_0x028c
            r4.width = r15
        L_0x028c:
            int r1 = r4.height
            if (r1 != 0) goto L_0x0292
            r4.height = r15
        L_0x0292:
            r1 = 0
        L_0x0293:
            int r2 = r5.length
            if (r1 >= r2) goto L_0x02eb
            int r2 = r5.length
            if (r2 <= r15) goto L_0x02a0
            r2 = r5[r1]
            r3 = 0
            r2.setAlpha(r3)
            goto L_0x02a8
        L_0x02a0:
            r3 = 0
            r2 = r5[r1]
            r15 = 1065353216(0x3f800000, float:1.0)
            r2.setAlpha(r15)
        L_0x02a8:
            r2 = r5[r1]
            r2.setPivotX(r3)
            r2 = r5[r1]
            r2.setPivotY(r3)
            r2 = r5[r1]
            float r15 = r9.scale
            r2.setScaleX(r15)
            r2 = r5[r1]
            float r15 = r9.scale
            r2.setScaleY(r15)
            r2 = r5[r1]
            int r15 = r9.viewX
            float r15 = (float) r15
            float r3 = r0.left
            r18 = r6
            float r6 = r9.scale
            float r3 = r3 * r6
            float r15 = r15 + r3
            r2.setTranslationX(r15)
            r2 = r5[r1]
            int r3 = r9.viewY
            float r3 = (float) r3
            float r6 = r0.top
            float r15 = r9.scale
            float r6 = r6 * r15
            float r3 = r3 + r6
            r2.setTranslationY(r3)
            r2 = r5[r1]
            r2.setLayoutParams(r4)
            int r1 = r1 + 1
            r6 = r18
            r15 = 1
            goto L_0x0293
        L_0x02eb:
            r18 = r6
            android.widget.FrameLayout r1 = r10.windowView
            android.view.ViewTreeObserver r15 = r1.getViewTreeObserver()
            im.bclpbkiauv.ui.PhotoViewer$41 r6 = new im.bclpbkiauv.ui.PhotoViewer$41
            r1 = r6
            r2 = r24
            r3 = r5
            r17 = r4
            r19 = r5
            r5 = r8
            r22 = r0
            r0 = r6
            r6 = r9
            r21 = r7
            r23 = r8
            r8 = r30
            r1.<init>(r3, r4, r5, r6, r7, r8)
            r15.addOnPreDrawListener(r0)
            r16 = r9
            r15 = r20
            goto L_0x0376
        L_0x0313:
            r20 = r6
            r9 = r7
            if (r30 == 0) goto L_0x034b
            int r0 = r10.sendPhotoType
            r1 = 3
            if (r0 == r1) goto L_0x034b
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x032b
            android.view.WindowManager$LayoutParams r0 = r10.windowLayoutParams
            r1 = -2147417856(0xffffffff80010100, float:-9.2194E-41)
            r0.flags = r1
            goto L_0x0330
        L_0x032b:
            android.view.WindowManager$LayoutParams r0 = r10.windowLayoutParams
            r1 = 0
            r0.flags = r1
        L_0x0330:
            android.view.WindowManager$LayoutParams r0 = r10.windowLayoutParams
            r1 = 272(0x110, float:3.81E-43)
            r0.softInputMode = r1
            android.widget.FrameLayout r0 = r10.windowView
            android.view.WindowManager$LayoutParams r1 = r10.windowLayoutParams
            r15 = r20
            r15.updateViewLayout(r0, r1)
            android.widget.FrameLayout r0 = r10.windowView
            r1 = 1
            r0.setFocusable(r1)
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r0 = r10.containerView
            r0.setFocusable(r1)
            goto L_0x034d
        L_0x034b:
            r15 = r20
        L_0x034d:
            im.bclpbkiauv.ui.PhotoViewer$BackgroundDrawable r0 = r10.backgroundDrawable
            r1 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r1)
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r0 = r10.containerView
            r1 = 1065353216(0x3f800000, float:1.0)
            r0.setAlpha(r1)
            r1 = r24
            r2 = r25
            r3 = r26
            r4 = r27
            r5 = r28
            r6 = r29
            r7 = r30
            r8 = r31
            r16 = r9
            r1.onPhotoShow(r2, r3, r4, r5, r6, r7, r8, r9)
            r24.initCropView()
            r24.setCropBitmap()
        L_0x0376:
            android.app.Activity r0 = r10.parentActivity
            java.lang.String r1 = "accessibility"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.view.accessibility.AccessibilityManager r0 = (android.view.accessibility.AccessibilityManager) r0
            boolean r1 = r0.isTouchExplorationEnabled()
            if (r1 == 0) goto L_0x03a2
            android.view.accessibility.AccessibilityEvent r1 = android.view.accessibility.AccessibilityEvent.obtain()
            r2 = 16384(0x4000, float:2.2959E-41)
            r1.setEventType(r2)
            java.util.List r2 = r1.getText()
            r3 = 2131689532(0x7f0f003c, float:1.9008082E38)
            java.lang.String r4 = "AccDescrPhotoViewer"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r2.add(r3)
            r0.sendAccessibilityEvent(r1)
        L_0x03a2:
            r1 = 1
            return r1
        L_0x03a4:
            r0 = move-exception
            r15 = r6
            r16 = r7
        L_0x03a8:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            return r1
        L_0x03ad:
            r1 = 0
        L_0x03ae:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PhotoViewer.openPhoto(im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.tgnet.TLRPC$FileLocation, im.bclpbkiauv.messenger.ImageLocation, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int, im.bclpbkiauv.ui.PhotoViewer$PhotoViewerProvider, im.bclpbkiauv.ui.ChatActivity, long, long, boolean):boolean");
    }

    public void injectVideoPlayerToMediaController() {
        if (this.videoPlayer.isPlaying()) {
            MediaController.getInstance().injectVideoPlayer(this.videoPlayer, this.currentMessageObject);
            this.videoPlayer = null;
            updateAccessibilityOverlayVisibility();
        }
    }

    /* JADX WARNING: type inference failed for: r4v1, types: [im.bclpbkiauv.ui.components.AnimatedFileDrawable, android.view.View] */
    /* JADX WARNING: type inference failed for: r4v12 */
    /* JADX WARNING: type inference failed for: r4v15 */
    public void closePhoto(boolean animated, boolean fromEditMode) {
        ? r4;
        boolean z;
        RectF drawRegion;
        int i;
        AnimatedFileDrawable animation;
        Bitmap bitmap;
        int flagsToClear;
        int i2;
        PhotoPaintView photoPaintView2;
        if (fromEditMode || (i2 = this.currentEditMode) == 0) {
            QualityChooseView qualityChooseView2 = this.qualityChooseView;
            if (qualityChooseView2 == null || qualityChooseView2.getTag() == null) {
                this.openedFullScreenVideo = false;
                try {
                    if (this.visibleDialog != null) {
                        this.visibleDialog.dismiss();
                        this.visibleDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                if (!(Build.VERSION.SDK_INT < 21 || this.actionBar == null || (flagsToClear = this.containerView.getSystemUiVisibility() & 4102) == 0)) {
                    FrameLayoutDrawer frameLayoutDrawer = this.containerView;
                    frameLayoutDrawer.setSystemUiVisibility(frameLayoutDrawer.getSystemUiVisibility() & (~flagsToClear));
                }
                int flagsToClear2 = this.currentEditMode;
                if (flagsToClear2 != 0) {
                    if (flagsToClear2 == 2) {
                        this.photoFilterView.shutdown();
                        this.containerView.removeView(this.photoFilterView);
                        this.photoFilterView = null;
                    } else if (flagsToClear2 == 1) {
                        this.editorDoneLayout.setVisibility(8);
                        this.photoCropView.setVisibility(8);
                    } else if (flagsToClear2 == 3) {
                        this.photoPaintView.shutdown();
                        this.containerView.removeView(this.photoPaintView);
                        this.photoPaintView = null;
                    }
                    this.currentEditMode = 0;
                } else if (this.sendPhotoType == 1) {
                    this.photoCropView.setVisibility(8);
                }
                if (this.parentActivity == null) {
                    return;
                }
                if ((!this.isInline && !this.isVisible) || checkAnimation() || this.placeProvider == null) {
                    return;
                }
                if (!this.captionEditText.hideActionMode() || fromEditMode) {
                    PlaceProviderObject object = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex, true);
                    if (!(this.videoPlayer == null || object == null || (animation = object.imageReceiver.getAnimation()) == null)) {
                        if (this.textureUploaded && (bitmap = animation.getAnimatedBitmap()) != null) {
                            try {
                                Bitmap src = this.videoTextureView.getBitmap(bitmap.getWidth(), bitmap.getHeight());
                                new Canvas(bitmap).drawBitmap(src, 0.0f, 0.0f, (Paint) null);
                                src.recycle();
                            } catch (Throwable e2) {
                                FileLog.e(e2);
                            }
                        }
                        animation.seekTo(this.videoPlayer.getCurrentPosition(), !FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingVideo(this.currentMessageObject.getDocument(), true));
                        object.imageReceiver.setAllowStartAnimation(true);
                        object.imageReceiver.startAnimation();
                    }
                    releasePlayer(true);
                    this.captionEditText.onDestroy();
                    this.parentChatActivity = null;
                    removeObservers();
                    this.isActionBarVisible = false;
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 != null) {
                        velocityTracker2.recycle();
                        this.velocityTracker = null;
                    }
                    if (this.isInline) {
                        this.isInline = false;
                        this.animationInProgress = 0;
                        onPhotoClosed(object);
                        this.containerView.setScaleX(1.0f);
                        this.containerView.setScaleY(1.0f);
                        return;
                    }
                    if (animated) {
                        ClippingImageView[] animatingImageViews = getAnimatingImageViews(object);
                        for (int i3 = 0; i3 < animatingImageViews.length; i3++) {
                            animatingImageViews[i3].setAnimationValues(this.animationValues);
                            animatingImageViews[i3].setVisibility(0);
                        }
                        this.animationInProgress = 3;
                        this.containerView.invalidate();
                        AnimatorSet animatorSet = new AnimatorSet();
                        ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                        if (object != null) {
                            RectF drawRegion2 = object.imageReceiver.getDrawRegion();
                            layoutParams.width = (int) drawRegion2.width();
                            layoutParams.height = (int) drawRegion2.height();
                            int orientation = object.imageReceiver.getOrientation();
                            int animatedOrientation = object.imageReceiver.getAnimatedOrientation();
                            if (animatedOrientation != 0) {
                                orientation = animatedOrientation;
                            }
                            for (int i4 = 0; i4 < animatingImageViews.length; i4++) {
                                animatingImageViews[i4].setNeedRadius(object.radius != 0);
                                animatingImageViews[i4].setOrientation(orientation);
                                animatingImageViews[i4].setImageBitmap(object.thumb);
                            }
                            drawRegion = drawRegion2;
                        } else {
                            layoutParams.width = this.centerImage.getImageWidth();
                            layoutParams.height = this.centerImage.getImageHeight();
                            for (int i5 = 0; i5 < animatingImageViews.length; i5++) {
                                animatingImageViews[i5].setNeedRadius(false);
                                animatingImageViews[i5].setOrientation(this.centerImage.getOrientation());
                                animatingImageViews[i5].setImageBitmap(this.centerImage.getBitmapSafe());
                            }
                            drawRegion = null;
                        }
                        if (layoutParams.width == 0) {
                            layoutParams.width = 1;
                        }
                        if (layoutParams.height == 0) {
                            layoutParams.height = 1;
                        }
                        float scaleX = ((float) this.windowView.getMeasuredWidth()) / ((float) layoutParams.width);
                        float scaleY = ((float) (AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / ((float) layoutParams.height);
                        float scale2 = scaleX > scaleY ? scaleY : scaleX;
                        float width = ((float) layoutParams.width) * this.scale * scale2;
                        float height = ((float) layoutParams.height) * this.scale * scale2;
                        float xPos = (((float) this.windowView.getMeasuredWidth()) - width) / 2.0f;
                        float yPos = (((float) (AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) - height) / 2.0f;
                        for (int i6 = 0; i6 < animatingImageViews.length; i6++) {
                            try {
                                animatingImageViews[i6].setLayoutParams(layoutParams);
                                animatingImageViews[i6].setTranslationX(Math.max(Float.MIN_VALUE, Math.min(xPos + this.translationX, Float.MAX_VALUE)));
                                animatingImageViews[i6].setTranslationY(Math.max(Float.MIN_VALUE, Math.min(this.translationY + yPos, Float.MAX_VALUE)));
                                animatingImageViews[i6].setScaleX(Math.max(Float.MIN_VALUE, Math.min(this.scale * scale2, Float.MAX_VALUE)));
                                animatingImageViews[i6].setScaleY(Math.max(Float.MIN_VALUE, Math.min(this.scale * scale2, Float.MAX_VALUE)));
                            } catch (Exception e3) {
                                FileLog.e((Throwable) e3);
                            }
                        }
                        if (object != null) {
                            object.imageReceiver.setVisible(false, true);
                            int clipHorizontal = (int) Math.abs(drawRegion.left - ((float) object.imageReceiver.getImageX()));
                            int clipVertical = (int) Math.abs(drawRegion.top - ((float) object.imageReceiver.getImageY()));
                            int[] coords2 = new int[2];
                            object.parentView.getLocationInWindow(coords2);
                            float f = height;
                            float f2 = yPos;
                            int clipTop = (int) ((((float) (coords2[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) - (((float) object.viewY) + drawRegion.top)) + ((float) object.clipTopAddition));
                            if (clipTop < 0) {
                                clipTop = 0;
                            }
                            float f3 = scaleX;
                            int[] iArr = coords2;
                            int clipBottom = (int) ((((((float) object.viewY) + drawRegion.top) + (drawRegion.bottom - drawRegion.top)) - ((float) ((coords2[1] + object.parentView.getHeight()) - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)))) + ((float) object.clipBottomAddition));
                            if (clipBottom < 0) {
                                clipBottom = 0;
                            }
                            int clipTop2 = Math.max(clipTop, clipVertical);
                            int clipBottom2 = Math.max(clipBottom, clipVertical);
                            this.animationValues[0][0] = this.animatingImageView.getScaleX();
                            this.animationValues[0][1] = this.animatingImageView.getScaleY();
                            this.animationValues[0][2] = this.animatingImageView.getTranslationX();
                            this.animationValues[0][3] = this.animatingImageView.getTranslationY();
                            float[][] fArr = this.animationValues;
                            fArr[0][4] = 0.0f;
                            fArr[0][5] = 0.0f;
                            fArr[0][6] = 0.0f;
                            fArr[0][7] = 0.0f;
                            fArr[0][8] = 0.0f;
                            fArr[0][9] = 0.0f;
                            fArr[1][0] = object.scale;
                            this.animationValues[1][1] = object.scale;
                            ViewGroup.LayoutParams layoutParams2 = layoutParams;
                            this.animationValues[1][2] = ((float) object.viewX) + (drawRegion.left * object.scale);
                            this.animationValues[1][3] = ((float) object.viewY) + (drawRegion.top * object.scale);
                            this.animationValues[1][4] = ((float) clipHorizontal) * object.scale;
                            this.animationValues[1][5] = ((float) clipTop2) * object.scale;
                            this.animationValues[1][6] = ((float) clipBottom2) * object.scale;
                            this.animationValues[1][7] = (float) object.radius;
                            this.animationValues[1][8] = ((float) clipVertical) * object.scale;
                            this.animationValues[1][9] = ((float) clipHorizontal) * object.scale;
                            ArrayList<Animator> animators = new ArrayList<>((this.sendPhotoType == 1 ? 3 : 2) + animatingImageViews.length + (animatingImageViews.length > 1 ? 1 : 0));
                            int i7 = 0;
                            while (i7 < animatingImageViews.length) {
                                animators.add(ObjectAnimator.ofFloat(animatingImageViews[i7], AnimationProperties.CLIPPING_IMAGE_VIEW_PROGRESS, new float[]{0.0f, 1.0f}));
                                i7++;
                                clipTop2 = clipTop2;
                                clipHorizontal = clipHorizontal;
                            }
                            int i8 = clipTop2;
                            if (animatingImageViews.length > 1) {
                                i = 0;
                                animators.add(ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, new float[]{0.0f}));
                            } else {
                                i = 0;
                            }
                            BackgroundDrawable backgroundDrawable2 = this.backgroundDrawable;
                            Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                            int[] iArr2 = new int[1];
                            iArr2[i] = i;
                            animators.add(ObjectAnimator.ofInt(backgroundDrawable2, property, iArr2));
                            FrameLayoutDrawer frameLayoutDrawer2 = this.containerView;
                            Property property2 = View.ALPHA;
                            float[] fArr2 = new float[1];
                            fArr2[i] = 0.0f;
                            animators.add(ObjectAnimator.ofFloat(frameLayoutDrawer2, property2, fArr2));
                            if (this.sendPhotoType == 1) {
                                PhotoCropView photoCropView2 = this.photoCropView;
                                Property property3 = View.ALPHA;
                                float[] fArr3 = new float[1];
                                fArr3[i] = 0.0f;
                                animators.add(ObjectAnimator.ofFloat(photoCropView2, property3, fArr3));
                            }
                            animatorSet.playTogether(animators);
                        } else {
                            float f4 = yPos;
                            float f5 = scaleX;
                            ViewGroup.LayoutParams layoutParams3 = layoutParams;
                            int h = AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                            Animator[] animatorArr = new Animator[4];
                            animatorArr[0] = ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, new float[]{0.0f});
                            ClippingImageView clippingImageView = this.animatingImageView;
                            Property property4 = View.TRANSLATION_Y;
                            float[] fArr4 = new float[1];
                            fArr4[0] = this.translationY >= 0.0f ? (float) h : (float) (-h);
                            animatorArr[2] = ObjectAnimator.ofFloat(clippingImageView, property4, fArr4);
                            animatorArr[3] = ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        }
                        this.animationEndRunnable = new Runnable(object) {
                            private final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PhotoViewer.this.lambda$closePhoto$47$PhotoViewer(this.f$1);
                            }
                        };
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public final void run() {
                                        PhotoViewer.AnonymousClass42.this.lambda$onAnimationEnd$0$PhotoViewer$42();
                                    }
                                });
                            }

                            public /* synthetic */ void lambda$onAnimationEnd$0$PhotoViewer$42() {
                                if (PhotoViewer.this.animationEndRunnable != null) {
                                    PhotoViewer.this.animationEndRunnable.run();
                                    Runnable unused = PhotoViewer.this.animationEndRunnable = null;
                                }
                            }
                        });
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        if (Build.VERSION.SDK_INT >= 18) {
                            this.containerView.setLayerType(2, (Paint) null);
                        }
                        animatorSet.start();
                        r4 = 0;
                    } else {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.SCALE_X, new float[]{0.9f}), ObjectAnimator.ofFloat(this.containerView, View.SCALE_Y, new float[]{0.9f}), ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f})});
                        this.animationInProgress = 2;
                        this.animationEndRunnable = new Runnable(object) {
                            private final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PhotoViewer.this.lambda$closePhoto$48$PhotoViewer(this.f$1);
                            }
                        };
                        animatorSet2.setDuration(200);
                        animatorSet2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (PhotoViewer.this.animationEndRunnable != null) {
                                    PhotoViewer.this.animationEndRunnable.run();
                                    Runnable unused = PhotoViewer.this.animationEndRunnable = null;
                                }
                            }
                        });
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        if (Build.VERSION.SDK_INT >= 18) {
                            z = false;
                            this.containerView.setLayerType(2, (Paint) null);
                        } else {
                            z = false;
                        }
                        animatorSet2.start();
                        r4 = z;
                    }
                    AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
                    if (animatedFileDrawable != null) {
                        animatedFileDrawable.setSecondParentView(r4);
                        this.currentAnimation = r4;
                        this.centerImage.setImageBitmap((Drawable) r4);
                    }
                    PhotoViewerProvider photoViewerProvider = this.placeProvider;
                    if (photoViewerProvider != null && !photoViewerProvider.canScrollAway()) {
                        this.placeProvider.cancelButtonPressed();
                        return;
                    }
                    return;
                }
                return;
            }
            this.qualityPicker.cancelButton.callOnClick();
        } else if (i2 != 3 || (photoPaintView2 = this.photoPaintView) == null) {
            switchToEditMode(0);
        } else {
            photoPaintView2.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() {
                public final void run() {
                    PhotoViewer.this.lambda$closePhoto$46$PhotoViewer();
                }
            });
        }
    }

    public /* synthetic */ void lambda$closePhoto$46$PhotoViewer() {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$closePhoto$47$PhotoViewer(PlaceProviderObject object) {
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, (Paint) null);
        }
        this.animationInProgress = 0;
        onPhotoClosed(object);
    }

    public /* synthetic */ void lambda$closePhoto$48$PhotoViewer(PlaceProviderObject object) {
        if (this.containerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
            onPhotoClosed(object);
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
        }
    }

    private ClippingImageView[] getAnimatingImageViews(PlaceProviderObject object) {
        boolean hasSecondAnimatingImageView = (AndroidUtilities.isTablet() || object == null || object.animatingImageView == null) ? false : true;
        ClippingImageView[] animatingImageViews = new ClippingImageView[((hasSecondAnimatingImageView ? 1 : 0) + 1)];
        animatingImageViews[0] = this.animatingImageView;
        if (hasSecondAnimatingImageView) {
            animatingImageViews[1] = object.animatingImageView;
        }
        return animatingImageViews;
    }

    private void removeObservers() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
    }

    public void destroyPhotoViewer() {
        if (this.parentActivity != null && this.windowView != null) {
            PipVideoView pipVideoView2 = this.pipVideoView;
            if (pipVideoView2 != null) {
                pipVideoView2.close();
                this.pipVideoView = null;
            }
            removeObservers();
            releasePlayer(false);
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.currentThumb = null;
            }
            this.animatingImageView.setImageBitmap((ImageReceiver.BitmapHolder) null);
            PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
            if (photoViewerCaptionEnterView != null) {
                photoViewerCaptionEnterView.onDestroy();
            }
            if (this == PipInstance) {
                PipInstance = null;
            } else {
                Instance = null;
            }
        }
    }

    private void onPhotoClosed(PlaceProviderObject object) {
        this.isVisible = false;
        this.disableShowCheck = true;
        this.currentMessageObject = null;
        this.currentBotInlineResult = null;
        this.currentFileLocation = null;
        this.currentSecureDocument = null;
        this.currentPathObject = null;
        FrameLayout frameLayout = this.videoPlayerControlFrameLayout;
        if (frameLayout != null) {
            frameLayout.setVisibility(8);
            this.dateTextView.setVisibility(0);
            this.nameTextView.setVisibility(0);
        }
        this.sendPhotoType = 0;
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        this.parentAlert = null;
        AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.setSecondParentView((View) null);
            this.currentAnimation = null;
        }
        for (int a = 0; a < 3; a++) {
            PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
            if (photoProgressViewArr[a] != null) {
                photoProgressViewArr[a].setBackgroundState(-1, false);
            }
        }
        requestVideoPreview(0);
        VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
        if (videoTimelinePlayView != null) {
            videoTimelinePlayView.destroy();
        }
        Bitmap bitmap = null;
        this.centerImage.setImageBitmap(bitmap);
        this.leftImage.setImageBitmap(bitmap);
        this.rightImage.setImageBitmap(bitmap);
        this.containerView.post(new Runnable(object) {
            private final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PhotoViewer.this.lambda$onPhotoClosed$49$PhotoViewer(this.f$1);
            }
        });
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider != null) {
            photoViewerProvider.willHidePhotoViewer();
        }
        this.groupedPhotosListView.clear();
        this.placeProvider = null;
        this.selectedPhotosAdapter.notifyDataSetChanged();
        this.disableShowCheck = false;
        this.videoCutStart = 0.0f;
        this.videoCutEnd = 1.0f;
        if (object != null) {
            object.imageReceiver.setVisible(true, true);
        }
    }

    public /* synthetic */ void lambda$onPhotoClosed$49$PhotoViewer(PlaceProviderObject object) {
        this.animatingImageView.setImageBitmap((ImageReceiver.BitmapHolder) null);
        if (!(object == null || AndroidUtilities.isTablet() || object.animatingImageView == null)) {
            object.animatingImageView.setImageBitmap((ImageReceiver.BitmapHolder) null);
        }
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void redraw(int count) {
        FrameLayoutDrawer frameLayoutDrawer;
        if (count < 6 && (frameLayoutDrawer = this.containerView) != null) {
            frameLayoutDrawer.invalidate();
            AndroidUtilities.runOnUIThread(new Runnable(count) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PhotoViewer.this.lambda$redraw$50$PhotoViewer(this.f$1);
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$redraw$50$PhotoViewer(int count) {
        redraw(count + 1);
    }

    public void onResume() {
        redraw(0);
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.seekTo(videoPlayer2.getCurrentPosition() + 1);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        PipVideoView pipVideoView2 = this.pipVideoView;
        if (pipVideoView2 != null) {
            pipVideoView2.onConfigurationChanged();
        }
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false, false);
        } else if (this.lastTitle != null) {
            closeCaptionEnter(true);
        }
    }

    public boolean isVisible() {
        return this.isVisible && this.placeProvider != null;
    }

    /* access modifiers changed from: private */
    public void updateMinMax(float scale2) {
        int maxW = ((int) ((((float) this.centerImage.getImageWidth()) * scale2) - ((float) getContainerViewWidth()))) / 2;
        int maxH = ((int) ((((float) this.centerImage.getImageHeight()) * scale2) - ((float) getContainerViewHeight()))) / 2;
        if (maxW > 0) {
            this.minX = (float) (-maxW);
            this.maxX = (float) maxW;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (maxH > 0) {
            this.minY = (float) (-maxH);
            this.maxY = (float) maxH;
            return;
        }
        this.maxY = 0.0f;
        this.minY = 0.0f;
    }

    private int getAdditionX() {
        int i = this.currentEditMode;
        if (i == 0 || i == 3) {
            return 0;
        }
        return AndroidUtilities.dp(14.0f);
    }

    private int getAdditionY() {
        int i = this.currentEditMode;
        int i2 = 0;
        if (i == 3) {
            int dp = AndroidUtilities.dp(8.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp + i2;
        } else if (i == 0) {
            return 0;
        } else {
            int dp2 = AndroidUtilities.dp(14.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp2 + i2;
        }
    }

    /* access modifiers changed from: private */
    public int getContainerViewWidth() {
        return getContainerViewWidth(this.currentEditMode);
    }

    /* access modifiers changed from: private */
    public int getContainerViewWidth(int mode) {
        int width = this.containerView.getWidth();
        if (mode == 0 || mode == 3) {
            return width;
        }
        return width - AndroidUtilities.dp(28.0f);
    }

    /* access modifiers changed from: private */
    public int getContainerViewHeight() {
        return getContainerViewHeight(this.currentEditMode);
    }

    /* access modifiers changed from: private */
    public int getContainerViewHeight(int mode) {
        int height = AndroidUtilities.displaySize.y;
        if (mode == 0 && Build.VERSION.SDK_INT >= 21) {
            height += AndroidUtilities.statusBarHeight;
        }
        if (mode == 1) {
            return height - AndroidUtilities.dp(144.0f);
        }
        if (mode == 2) {
            return height - AndroidUtilities.dp(214.0f);
        }
        if (mode == 3) {
            return height - (AndroidUtilities.dp(48.0f) + ActionBar.getCurrentActionBarHeight());
        }
        return height;
    }

    /* access modifiers changed from: private */
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.animationInProgress != 0 || this.animationStartTime != 0) {
            return false;
        }
        int i = this.currentEditMode;
        if (i == 2) {
            this.photoFilterView.onTouch(ev);
            return true;
        } else if (i == 1 || this.sendPhotoType == 1) {
            return true;
        } else {
            if (this.captionEditText.isPopupShowing() || this.captionEditText.isKeyboardVisible()) {
                if (ev.getAction() == 1) {
                    closeCaptionEnter(true);
                }
                return true;
            } else if (this.currentEditMode != 0 || this.sendPhotoType == 1 || ev.getPointerCount() != 1 || !this.gestureDetector.onTouchEvent(ev) || !this.doubleTap) {
                if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
                    this.discardTap = false;
                    if (!this.scroller.isFinished()) {
                        this.scroller.abortAnimation();
                    }
                    if (!this.draggingDown && !this.changingPage) {
                        if (this.canZoom && ev.getPointerCount() == 2) {
                            this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)));
                            this.pinchStartScale = this.scale;
                            this.pinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                            this.pinchCenterY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                            this.pinchStartX = this.translationX;
                            this.pinchStartY = this.translationY;
                            this.zooming = true;
                            this.moving = false;
                            VelocityTracker velocityTracker2 = this.velocityTracker;
                            if (velocityTracker2 != null) {
                                velocityTracker2.clear();
                            }
                        } else if (ev.getPointerCount() == 1) {
                            this.moveStartX = ev.getX();
                            float y = ev.getY();
                            this.moveStartY = y;
                            this.dragY = y;
                            this.draggingDown = false;
                            this.canDragDown = true;
                            VelocityTracker velocityTracker3 = this.velocityTracker;
                            if (velocityTracker3 != null) {
                                velocityTracker3.clear();
                            }
                        }
                    }
                } else if (ev.getActionMasked() == 2) {
                    if (this.canZoom && ev.getPointerCount() == 2 && !this.draggingDown && this.zooming && !this.changingPage) {
                        this.discardTap = true;
                        this.scale = (((float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)))) / this.pinchStartDistance) * this.pinchStartScale;
                        this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                        float containerViewHeight = this.pinchCenterY - ((float) (getContainerViewHeight() / 2));
                        float containerViewHeight2 = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY;
                        float f = this.scale;
                        this.translationY = containerViewHeight - (containerViewHeight2 * (f / this.pinchStartScale));
                        updateMinMax(f);
                        this.containerView.invalidate();
                    } else if (ev.getPointerCount() == 1) {
                        VelocityTracker velocityTracker4 = this.velocityTracker;
                        if (velocityTracker4 != null) {
                            velocityTracker4.addMovement(ev);
                        }
                        float dx = Math.abs(ev.getX() - this.moveStartX);
                        float dy = Math.abs(ev.getY() - this.dragY);
                        if (dx > ((float) AndroidUtilities.dp(3.0f)) || dy > ((float) AndroidUtilities.dp(3.0f))) {
                            this.discardTap = true;
                            QualityChooseView qualityChooseView2 = this.qualityChooseView;
                            if (qualityChooseView2 != null && qualityChooseView2.getVisibility() == 0) {
                                return true;
                            }
                        }
                        if (this.placeProvider.canScrollAway() && this.currentEditMode == 0 && this.sendPhotoType != 1 && this.canDragDown && !this.draggingDown && this.scale == 1.0f && dy >= ((float) AndroidUtilities.dp(30.0f)) && dy / 2.0f > dx) {
                            this.draggingDown = true;
                            this.moving = false;
                            this.dragY = ev.getY();
                            if (this.isActionBarVisible && this.containerView.getTag() != null) {
                                toggleActionBar(false, true);
                            } else if (this.pickerView.getVisibility() == 0) {
                                toggleActionBar(false, true);
                                togglePhotosListView(false, true);
                                toggleCheckImageView(false);
                            }
                            return true;
                        } else if (this.draggingDown) {
                            this.translationY = ev.getY() - this.dragY;
                            this.containerView.invalidate();
                        } else if (this.invalidCoords || this.animationStartTime != 0) {
                            this.invalidCoords = false;
                            this.moveStartX = ev.getX();
                            this.moveStartY = ev.getY();
                        } else {
                            float moveDx = this.moveStartX - ev.getX();
                            float moveDy = this.moveStartY - ev.getY();
                            if (this.moving || this.currentEditMode != 0 || ((this.scale == 1.0f && Math.abs(moveDy) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(moveDx)) || this.scale != 1.0f)) {
                                if (!this.moving) {
                                    moveDx = 0.0f;
                                    moveDy = 0.0f;
                                    this.moving = true;
                                    this.canDragDown = false;
                                }
                                this.moveStartX = ev.getX();
                                this.moveStartY = ev.getY();
                                updateMinMax(this.scale);
                                if ((this.translationX < this.minX && (this.currentEditMode != 0 || !this.rightImage.hasImageSet())) || (this.translationX > this.maxX && (this.currentEditMode != 0 || !this.leftImage.hasImageSet()))) {
                                    moveDx /= 3.0f;
                                }
                                float f2 = this.maxY;
                                if (f2 == 0.0f) {
                                    float f3 = this.minY;
                                    if (f3 == 0.0f && this.currentEditMode == 0 && this.sendPhotoType != 1) {
                                        float f4 = this.translationY;
                                        if (f4 - moveDy < f3) {
                                            this.translationY = f3;
                                            moveDy = 0.0f;
                                        } else if (f4 - moveDy > f2) {
                                            this.translationY = f2;
                                            moveDy = 0.0f;
                                        }
                                        this.translationX -= moveDx;
                                        if (!(this.scale == 1.0f && this.currentEditMode == 0)) {
                                            this.translationY -= moveDy;
                                        }
                                        this.containerView.invalidate();
                                    }
                                }
                                float f5 = this.translationY;
                                if (f5 < this.minY || f5 > this.maxY) {
                                    moveDy /= 3.0f;
                                }
                                this.translationX -= moveDx;
                                this.translationY -= moveDy;
                                this.containerView.invalidate();
                            }
                        }
                    }
                } else if (ev.getActionMasked() == 3 || ev.getActionMasked() == 1 || ev.getActionMasked() == 6) {
                    if (this.zooming) {
                        this.invalidCoords = true;
                        float f6 = this.scale;
                        if (f6 < 1.0f) {
                            updateMinMax(1.0f);
                            animateTo(1.0f, 0.0f, 0.0f, true);
                        } else if (f6 > 3.0f) {
                            float atx = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                            float aty = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                            updateMinMax(3.0f);
                            if (atx < this.minX) {
                                atx = this.minX;
                            } else if (atx > this.maxX) {
                                atx = this.maxX;
                            }
                            if (aty < this.minY) {
                                aty = this.minY;
                            } else if (aty > this.maxY) {
                                aty = this.maxY;
                            }
                            animateTo(3.0f, atx, aty, true);
                        } else {
                            checkMinMax(true);
                        }
                        this.zooming = false;
                    } else if (this.draggingDown) {
                        if (Math.abs(this.dragY - ev.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                            closePhoto(true, false);
                        } else {
                            if (this.pickerView.getVisibility() == 0) {
                                toggleActionBar(true, true);
                                toggleCheckImageView(true);
                            }
                            animateTo(1.0f, 0.0f, 0.0f, false);
                        }
                        this.draggingDown = false;
                    } else if (this.moving) {
                        float moveToX = this.translationX;
                        float moveToY = this.translationY;
                        updateMinMax(this.scale);
                        this.moving = false;
                        this.canDragDown = true;
                        float velocity = 0.0f;
                        VelocityTracker velocityTracker5 = this.velocityTracker;
                        if (velocityTracker5 != null && this.scale == 1.0f) {
                            velocityTracker5.computeCurrentVelocity(1000);
                            velocity = this.velocityTracker.getXVelocity();
                        }
                        if (this.currentEditMode == 0 && this.sendPhotoType != 1) {
                            if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / 3)) || velocity < ((float) (-AndroidUtilities.dp(650.0f)))) && this.rightImage.hasImageSet()) {
                                goToNext();
                                return true;
                            } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / 3)) || velocity > ((float) AndroidUtilities.dp(650.0f))) && this.leftImage.hasImageSet()) {
                                goToPrev();
                                return true;
                            }
                        }
                        float f7 = this.translationX;
                        if (f7 < this.minX) {
                            moveToX = this.minX;
                        } else if (f7 > this.maxX) {
                            moveToX = this.maxX;
                        }
                        float f8 = this.translationY;
                        if (f8 < this.minY) {
                            moveToY = this.minY;
                        } else if (f8 > this.maxY) {
                            moveToY = this.maxY;
                        }
                        animateTo(this.scale, moveToX, moveToY, false);
                    }
                }
                return false;
            } else {
                this.doubleTap = false;
                this.moving = false;
                this.zooming = false;
                checkMinMax(false);
                return true;
            }
        }
    }

    private void checkMinMax(boolean zoom) {
        float moveToX = this.translationX;
        float moveToY = this.translationY;
        updateMinMax(this.scale);
        float f = this.translationX;
        if (f < this.minX) {
            moveToX = this.minX;
        } else if (f > this.maxX) {
            moveToX = this.maxX;
        }
        float f2 = this.translationY;
        if (f2 < this.minY) {
            moveToY = this.minY;
        } else if (f2 > this.maxY) {
            moveToY = this.maxY;
        }
        animateTo(this.scale, moveToX, moveToY, zoom);
    }

    private void goToNext() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - extra) - ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, this.maxX + ((float) getContainerViewWidth()) + extra + ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom, int duration) {
        if (this.scale != newScale || this.translationX != newTx || this.translationY != newTy) {
            this.zoomAnimation = isZoom;
            this.animateToScale = newScale;
            this.animateToX = newTx;
            this.animateToY = newTy;
            this.animationStartTime = System.currentTimeMillis();
            AnimatorSet animatorSet = new AnimatorSet();
            this.imageMoveAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f})});
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) duration);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = PhotoViewer.this.imageMoveAnimation = null;
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    public void setAnimationValue(float value) {
        this.animationValue = value;
        this.containerView.invalidate();
    }

    public float getAnimationValue() {
        return this.animationValue;
    }

    /* JADX WARNING: type inference failed for: r2v14, types: [android.graphics.drawable.Drawable] */
    /* JADX WARNING: type inference failed for: r2v22 */
    /* JADX WARNING: type inference failed for: r2v186 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x022c  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0321  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0323  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x036c  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x037a  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x039b  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x039e  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x03bd  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x03ca  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x04b6  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x04c0  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x04d7  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04f6  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x051e  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0556  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0622  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x062c  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x069a  */
    /* JADX WARNING: Removed duplicated region for block: B:263:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r32) {
        /*
            r31 = this;
            r1 = r31
            r8 = r32
            int r0 = r1.animationInProgress
            r9 = 1
            if (r0 == r9) goto L_0x06d8
            r2 = 3
            if (r0 == r2) goto L_0x06d8
            boolean r2 = r1.isVisible
            r10 = 2
            if (r2 != 0) goto L_0x0019
            if (r0 == r10) goto L_0x0019
            boolean r0 = r1.pipAnimationInProgress
            if (r0 != 0) goto L_0x0019
            goto L_0x06d8
        L_0x0019:
            boolean r0 = r1.padImageForHorizontalInsets
            r11 = 0
            if (r0 == 0) goto L_0x0030
            r32.save()
            int r0 = r31.getLeftInset()
            int r0 = r0 / r10
            int r2 = r31.getRightInset()
            int r2 = r2 / r10
            int r0 = r0 - r2
            float r0 = (float) r0
            r8.translate(r0, r11)
        L_0x0030:
            r0 = -1082130432(0xffffffffbf800000, float:-1.0)
            android.animation.AnimatorSet r2 = r1.imageMoveAnimation
            r12 = 0
            r13 = 1065353216(0x3f800000, float:1.0)
            if (r2 == 0) goto L_0x007c
            android.widget.Scroller r2 = r1.scroller
            boolean r2 = r2.isFinished()
            if (r2 != 0) goto L_0x0046
            android.widget.Scroller r2 = r1.scroller
            r2.abortAnimation()
        L_0x0046:
            float r2 = r1.scale
            float r3 = r1.animateToScale
            float r4 = r3 - r2
            float r5 = r1.animationValue
            float r4 = r4 * r5
            float r4 = r4 + r2
            float r6 = r1.translationX
            float r7 = r1.animateToX
            float r7 = r7 - r6
            float r7 = r7 * r5
            float r7 = r7 + r6
            float r14 = r1.translationY
            float r15 = r1.animateToY
            float r15 = r15 - r14
            float r15 = r15 * r5
            float r14 = r14 + r15
            int r3 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r3 != 0) goto L_0x006e
            int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r2 != 0) goto L_0x006e
            int r2 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x006e
            r0 = r14
        L_0x006e:
            r2 = r4
            r3 = r14
            r5 = r7
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r6 = r1.containerView
            r6.invalidate()
            r14 = r0
            r15 = r2
            r7 = r3
            r6 = r5
            goto L_0x014f
        L_0x007c:
            long r2 = r1.animationStartTime
            r4 = 0
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x0097
            float r2 = r1.animateToX
            r1.translationX = r2
            float r2 = r1.animateToY
            r1.translationY = r2
            float r2 = r1.animateToScale
            r1.scale = r2
            r1.animationStartTime = r4
            r1.updateMinMax(r2)
            r1.zoomAnimation = r12
        L_0x0097:
            android.widget.Scroller r2 = r1.scroller
            boolean r2 = r2.isFinished()
            if (r2 != 0) goto L_0x00f2
            android.widget.Scroller r2 = r1.scroller
            boolean r2 = r2.computeScrollOffset()
            if (r2 == 0) goto L_0x00f2
            android.widget.Scroller r2 = r1.scroller
            int r2 = r2.getStartX()
            float r2 = (float) r2
            float r3 = r1.maxX
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x00ca
            android.widget.Scroller r2 = r1.scroller
            int r2 = r2.getStartX()
            float r2 = (float) r2
            float r3 = r1.minX
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x00ca
            android.widget.Scroller r2 = r1.scroller
            int r2 = r2.getCurrX()
            float r2 = (float) r2
            r1.translationX = r2
        L_0x00ca:
            android.widget.Scroller r2 = r1.scroller
            int r2 = r2.getStartY()
            float r2 = (float) r2
            float r3 = r1.maxY
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x00ed
            android.widget.Scroller r2 = r1.scroller
            int r2 = r2.getStartY()
            float r2 = (float) r2
            float r3 = r1.minY
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x00ed
            android.widget.Scroller r2 = r1.scroller
            int r2 = r2.getCurrY()
            float r2 = (float) r2
            r1.translationY = r2
        L_0x00ed:
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r2 = r1.containerView
            r2.invalidate()
        L_0x00f2:
            int r2 = r1.switchImageAfterAnimation
            if (r2 == 0) goto L_0x013a
            r1.openedFullScreenVideo = r12
            java.util.ArrayList<java.lang.Object> r2 = r1.imagesArrLocals
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0121
            int r2 = r1.currentIndex
            if (r2 < 0) goto L_0x0121
            java.util.ArrayList<java.lang.Object> r3 = r1.imagesArrLocals
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0121
            java.util.ArrayList<java.lang.Object> r2 = r1.imagesArrLocals
            int r3 = r1.currentIndex
            java.lang.Object r2 = r2.get(r3)
            boolean r3 = r2 instanceof im.bclpbkiauv.messenger.MediaController.PhotoEntry
            if (r3 == 0) goto L_0x0121
            r3 = r2
            im.bclpbkiauv.messenger.MediaController$PhotoEntry r3 = (im.bclpbkiauv.messenger.MediaController.PhotoEntry) r3
            im.bclpbkiauv.messenger.VideoEditedInfo r4 = r31.getCurrentVideoEditedInfo()
            r3.editedInfo = r4
        L_0x0121:
            int r2 = r1.switchImageAfterAnimation
            if (r2 != r9) goto L_0x012e
            im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$WYz348UumbUAZWuwlhci-Tc2y-k r2 = new im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$WYz348UumbUAZWuwlhci-Tc2y-k
            r2.<init>()
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r2)
            goto L_0x0138
        L_0x012e:
            if (r2 != r10) goto L_0x0138
            im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$i16pDx5OT8TwVY4qgkbYFM0CI48 r2 = new im.bclpbkiauv.ui.-$$Lambda$PhotoViewer$i16pDx5OT8TwVY4qgkbYFM0CI48
            r2.<init>()
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x0138:
            r1.switchImageAfterAnimation = r12
        L_0x013a:
            float r2 = r1.scale
            float r3 = r1.translationY
            float r5 = r1.translationX
            boolean r4 = r1.moving
            if (r4 != 0) goto L_0x014b
            float r0 = r1.translationY
            r14 = r0
            r15 = r2
            r7 = r3
            r6 = r5
            goto L_0x014f
        L_0x014b:
            r14 = r0
            r15 = r2
            r7 = r3
            r6 = r5
        L_0x014f:
            int r0 = r1.animationInProgress
            r2 = 1132396544(0x437f0000, float:255.0)
            if (r0 == r10) goto L_0x019e
            boolean r0 = r1.pipAnimationInProgress
            if (r0 != 0) goto L_0x019e
            boolean r0 = r1.isInline
            if (r0 != 0) goto L_0x019e
            int r0 = r1.currentEditMode
            if (r0 != 0) goto L_0x0197
            int r0 = r1.sendPhotoType
            if (r0 == r9) goto L_0x0197
            float r0 = r1.scale
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x0197
            r0 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r0 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            if (r0 == 0) goto L_0x0197
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x0197
            int r0 = r31.getContainerViewHeight()
            float r0 = (float) r0
            r3 = 1082130432(0x40800000, float:4.0)
            float r0 = r0 / r3
            im.bclpbkiauv.ui.PhotoViewer$BackgroundDrawable r3 = r1.backgroundDrawable
            r4 = 1123942400(0x42fe0000, float:127.0)
            float r5 = java.lang.Math.abs(r14)
            float r5 = java.lang.Math.min(r5, r0)
            float r5 = r5 / r0
            float r5 = r13 - r5
            float r5 = r5 * r2
            float r4 = java.lang.Math.max(r4, r5)
            int r4 = (int) r4
            r3.setAlpha(r4)
            goto L_0x019e
        L_0x0197:
            im.bclpbkiauv.ui.PhotoViewer$BackgroundDrawable r0 = r1.backgroundDrawable
            r3 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r3)
        L_0x019e:
            r5 = 0
            r1.sideImage = r5
            int r0 = r1.currentEditMode
            if (r0 != 0) goto L_0x01e9
            int r0 = r1.sendPhotoType
            if (r0 == r9) goto L_0x01e9
            float r0 = r1.scale
            int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r0 < 0) goto L_0x01e0
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x01e0
            boolean r0 = r1.zooming
            if (r0 != 0) goto L_0x01e0
            float r0 = r1.maxX
            r3 = 1084227584(0x40a00000, float:5.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            float r0 = r0 + r4
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x01ca
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.leftImage
            r1.sideImage = r0
            goto L_0x01e0
        L_0x01ca:
            float r0 = r1.minX
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r0 = r0 - r3
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x01db
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.rightImage
            r1.sideImage = r0
            goto L_0x01e0
        L_0x01db:
            im.bclpbkiauv.ui.components.GroupedPhotosListView r0 = r1.groupedPhotosListView
            r0.setMoveProgress(r11)
        L_0x01e0:
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.sideImage
            if (r0 == 0) goto L_0x01e6
            r0 = 1
            goto L_0x01e7
        L_0x01e6:
            r0 = 0
        L_0x01e7:
            r1.changingPage = r0
        L_0x01e9:
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.sideImage
            im.bclpbkiauv.messenger.ImageReceiver r3 = r1.rightImage
            r4 = 1050253722(0x3e99999a, float:0.3)
            r16 = 1073741824(0x40000000, float:2.0)
            r17 = 1106247680(0x41f00000, float:30.0)
            if (r0 != r3) goto L_0x02e2
            r0 = r6
            r3 = 0
            r18 = 1065353216(0x3f800000, float:1.0)
            boolean r5 = r1.zoomAnimation
            if (r5 != 0) goto L_0x0222
            float r5 = r1.minX
            int r20 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r20 >= 0) goto L_0x0222
            float r5 = r5 - r0
            int r12 = r31.getContainerViewWidth()
            float r12 = (float) r12
            float r5 = r5 / r12
            float r18 = java.lang.Math.min(r13, r5)
            float r5 = r13 - r18
            float r3 = r5 * r4
            int r5 = r31.getContainerViewWidth()
            int r5 = -r5
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r12 = r12 / r10
            int r5 = r5 - r12
            float r0 = (float) r5
            r5 = r18
            goto L_0x0224
        L_0x0222:
            r5 = r18
        L_0x0224:
            im.bclpbkiauv.messenger.ImageReceiver r12 = r1.sideImage
            boolean r12 = r12.hasBitmapImage()
            if (r12 == 0) goto L_0x029e
            r32.save()
            int r12 = r31.getContainerViewWidth()
            int r12 = r12 / r10
            float r12 = (float) r12
            int r18 = r31.getContainerViewHeight()
            int r2 = r18 / 2
            float r2 = (float) r2
            r8.translate(r12, r2)
            int r2 = r31.getContainerViewWidth()
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r12 = r12 / r10
            int r2 = r2 + r12
            float r2 = (float) r2
            float r2 = r2 + r0
            r8.translate(r2, r11)
            float r2 = r13 - r3
            float r12 = r13 - r3
            r8.scale(r2, r12)
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.sideImage
            int r2 = r2.getBitmapWidth()
            im.bclpbkiauv.messenger.ImageReceiver r12 = r1.sideImage
            int r12 = r12.getBitmapHeight()
            int r11 = r31.getContainerViewWidth()
            float r11 = (float) r11
            float r4 = (float) r2
            float r11 = r11 / r4
            int r4 = r31.getContainerViewHeight()
            float r4 = (float) r4
            float r9 = (float) r12
            float r4 = r4 / r9
            int r9 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r9 <= 0) goto L_0x0275
            r9 = r4
            goto L_0x0276
        L_0x0275:
            r9 = r11
        L_0x0276:
            float r13 = (float) r2
            float r13 = r13 * r9
            int r13 = (int) r13
            float r10 = (float) r12
            float r10 = r10 * r9
            int r10 = (int) r10
            r24 = r2
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.sideImage
            r2.setAlpha(r5)
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.sideImage
            r25 = r4
            int r4 = -r13
            r23 = 2
            int r4 = r4 / 2
            r26 = r9
            int r9 = -r10
            int r9 = r9 / 2
            r2.setImageCoords(r4, r9, r13, r10)
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.sideImage
            r2.draw(r8)
            r32.restore()
        L_0x029e:
            im.bclpbkiauv.ui.components.GroupedPhotosListView r2 = r1.groupedPhotosListView
            float r4 = -r5
            r2.setMoveProgress(r4)
            r32.save()
            float r2 = r7 / r15
            r8.translate(r0, r2)
            int r2 = r31.getContainerViewWidth()
            float r2 = (float) r2
            float r4 = r1.scale
            r9 = 1065353216(0x3f800000, float:1.0)
            float r4 = r4 + r9
            float r2 = r2 * r4
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r2 = r2 + r4
            float r2 = r2 / r16
            float r4 = -r7
            float r4 = r4 / r15
            r8.translate(r2, r4)
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r2 = r1.photoProgressViews
            r4 = 1
            r2 = r2[r4]
            r9 = 1065353216(0x3f800000, float:1.0)
            float r13 = r9 - r3
            r2.setScale(r13)
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r2 = r1.photoProgressViews
            r2 = r2[r4]
            r2.setAlpha(r5)
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r2 = r1.photoProgressViews
            r2 = r2[r4]
            r2.onDraw(r8)
            r32.restore()
        L_0x02e2:
            r0 = r6
            r2 = 0
            r3 = 1065353216(0x3f800000, float:1.0)
            boolean r4 = r1.zoomAnimation
            if (r4 != 0) goto L_0x0314
            float r4 = r1.maxX
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x0314
            int r5 = r1.currentEditMode
            if (r5 != 0) goto L_0x0314
            int r5 = r1.sendPhotoType
            r9 = 1
            if (r5 == r9) goto L_0x0314
            float r4 = r0 - r4
            int r5 = r31.getContainerViewWidth()
            float r5 = (float) r5
            float r4 = r4 / r5
            r5 = 1065353216(0x3f800000, float:1.0)
            float r3 = java.lang.Math.min(r5, r4)
            r4 = 1050253722(0x3e99999a, float:0.3)
            float r2 = r3 * r4
            float r3 = r5 - r3
            float r0 = r1.maxX
            r9 = r0
            r10 = r2
            r11 = r3
            goto L_0x0317
        L_0x0314:
            r9 = r0
            r10 = r2
            r11 = r3
        L_0x0317:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.aspectRatioFrameLayout
            if (r0 == 0) goto L_0x0323
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0323
            r0 = 1
            goto L_0x0324
        L_0x0323:
            r0 = 0
        L_0x0324:
            r12 = r0
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.centerImage
            boolean r0 = r0.hasBitmapImage()
            if (r0 != 0) goto L_0x033f
            if (r12 == 0) goto L_0x0334
            boolean r0 = r1.textureUploaded
            if (r0 == 0) goto L_0x0334
            goto L_0x033f
        L_0x0334:
            r30 = r6
            r19 = r9
            r26 = r10
            r21 = r14
            r9 = r7
            goto L_0x04bc
        L_0x033f:
            r32.save()
            int r0 = r31.getContainerViewWidth()
            r2 = 2
            int r0 = r0 / r2
            int r3 = r31.getAdditionX()
            int r0 = r0 + r3
            float r0 = (float) r0
            int r3 = r31.getContainerViewHeight()
            int r3 = r3 / r2
            int r2 = r31.getAdditionY()
            int r3 = r3 + r2
            float r2 = (float) r3
            r8.translate(r0, r2)
            r8.translate(r9, r7)
            float r0 = r15 - r10
            float r2 = r15 - r10
            r8.scale(r0, r2)
            if (r12 == 0) goto L_0x037a
            boolean r0 = r1.textureUploaded
            if (r0 == 0) goto L_0x037a
            android.view.TextureView r0 = r1.videoTextureView
            int r0 = r0.getMeasuredWidth()
            android.view.TextureView r2 = r1.videoTextureView
            int r2 = r2.getMeasuredHeight()
            r13 = r2
            goto L_0x0387
        L_0x037a:
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.centerImage
            int r0 = r0.getBitmapWidth()
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.centerImage
            int r2 = r2.getBitmapHeight()
            r13 = r2
        L_0x0387:
            int r2 = r31.getContainerViewWidth()
            float r2 = (float) r2
            float r3 = (float) r0
            float r22 = r2 / r3
            int r2 = r31.getContainerViewHeight()
            float r2 = (float) r2
            float r3 = (float) r13
            float r24 = r2 / r3
            int r2 = (r22 > r24 ? 1 : (r22 == r24 ? 0 : -1))
            if (r2 <= 0) goto L_0x039e
            r2 = r24
            goto L_0x03a0
        L_0x039e:
            r2 = r22
        L_0x03a0:
            r25 = r2
            float r2 = (float) r0
            float r2 = r2 * r25
            int r5 = (int) r2
            float r2 = (float) r13
            float r2 = r2 * r25
            int r4 = (int) r2
            if (r12 == 0) goto L_0x03ca
            boolean r2 = r1.textureUploaded
            if (r2 == 0) goto L_0x03ca
            boolean r2 = r1.videoCrossfadeStarted
            if (r2 == 0) goto L_0x03ca
            float r2 = r1.videoCrossfadeAlpha
            r3 = 1065353216(0x3f800000, float:1.0)
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x03bd
            goto L_0x03ca
        L_0x03bd:
            r30 = r6
            r19 = r9
            r26 = r10
            r21 = r14
            r14 = r4
            r10 = r5
            r9 = r7
            goto L_0x042e
        L_0x03ca:
            android.graphics.Paint r2 = new android.graphics.Paint
            r2.<init>()
            r3 = r2
            r2 = -1
            r3.setColor(r2)
            r2 = 1132396544(0x437f0000, float:255.0)
            float r2 = r2 * r11
            int r2 = (int) r2
            r3.setAlpha(r2)
            int r2 = -r5
            r21 = 2
            int r2 = r2 / 2
            float r2 = (float) r2
            r26 = r2
            int r2 = -r4
            int r2 = r2 / 2
            float r2 = (float) r2
            r27 = r2
            int r2 = -r5
            int r2 = r2 / 2
            int r2 = r2 + r5
            float r2 = (float) r2
            r28 = r2
            int r2 = -r4
            int r2 = r2 / 2
            int r2 = r2 + r4
            float r2 = (float) r2
            r21 = r26
            r26 = r27
            r27 = r28
            r28 = r2
            r2 = r32
            r29 = r3
            r3 = r21
            r21 = r14
            r14 = r4
            r4 = r26
            r26 = r10
            r10 = r5
            r5 = r27
            r30 = r6
            r6 = r28
            r19 = r9
            r9 = r7
            r7 = r29
            r2.drawRect(r3, r4, r5, r6, r7)
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.centerImage
            r2.setAlpha(r11)
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.centerImage
            int r3 = -r10
            r4 = 2
            int r3 = r3 / r4
            int r5 = -r14
            int r5 = r5 / r4
            r2.setImageCoords(r3, r5, r10, r14)
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.centerImage
            r2.draw(r8)
        L_0x042e:
            if (r12 == 0) goto L_0x04b6
            int r2 = r32.getWidth()
            float r2 = (float) r2
            float r3 = (float) r0
            float r22 = r2 / r3
            int r2 = r32.getHeight()
            float r2 = (float) r2
            float r3 = (float) r13
            float r24 = r2 / r3
            int r2 = (r22 > r24 ? 1 : (r22 == r24 ? 0 : -1))
            if (r2 <= 0) goto L_0x0447
            r2 = r24
            goto L_0x0449
        L_0x0447:
            r2 = r22
        L_0x0449:
            r25 = r2
            float r2 = (float) r13
            float r2 = r2 * r25
            int r4 = (int) r2
            boolean r2 = r1.videoCrossfadeStarted
            if (r2 != 0) goto L_0x0463
            boolean r2 = r1.textureUploaded
            if (r2 == 0) goto L_0x0463
            r2 = 1
            r1.videoCrossfadeStarted = r2
            r2 = 0
            r1.videoCrossfadeAlpha = r2
            long r2 = java.lang.System.currentTimeMillis()
            r1.videoCrossfadeAlphaLastTime = r2
        L_0x0463:
            int r2 = -r10
            r3 = 2
            int r2 = r2 / r3
            float r2 = (float) r2
            int r5 = -r4
            int r5 = r5 / r3
            float r3 = (float) r5
            r8.translate(r2, r3)
            android.view.TextureView r2 = r1.videoTextureView
            float r3 = r1.videoCrossfadeAlpha
            float r3 = r3 * r11
            r2.setAlpha(r3)
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r2 = r1.aspectRatioFrameLayout
            r2.draw(r8)
            boolean r2 = r1.videoCrossfadeStarted
            if (r2 == 0) goto L_0x04b3
            float r2 = r1.videoCrossfadeAlpha
            r3 = 1065353216(0x3f800000, float:1.0)
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x04b3
            long r2 = java.lang.System.currentTimeMillis()
            long r5 = r1.videoCrossfadeAlphaLastTime
            long r5 = r2 - r5
            r1.videoCrossfadeAlphaLastTime = r2
            float r7 = r1.videoCrossfadeAlpha
            float r14 = (float) r5
            r27 = r0
            boolean r0 = r1.playerInjected
            if (r0 == 0) goto L_0x049d
            r0 = 1120403456(0x42c80000, float:100.0)
            goto L_0x049f
        L_0x049d:
            r0 = 1128792064(0x43480000, float:200.0)
        L_0x049f:
            float r14 = r14 / r0
            float r7 = r7 + r14
            r1.videoCrossfadeAlpha = r7
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
            float r0 = r1.videoCrossfadeAlpha
            r7 = 1065353216(0x3f800000, float:1.0)
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x04b9
            r1.videoCrossfadeAlpha = r7
            goto L_0x04b9
        L_0x04b3:
            r27 = r0
            goto L_0x04b9
        L_0x04b6:
            r27 = r0
            r4 = r14
        L_0x04b9:
            r32.restore()
        L_0x04bc:
            boolean r0 = r1.isCurrentVideo
            if (r0 == 0) goto L_0x04d7
            im.bclpbkiauv.ui.components.RadialProgressView r0 = r1.progressView
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x04d4
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r1.videoPlayer
            if (r0 == 0) goto L_0x04d2
            boolean r0 = r0.isPlaying()
            if (r0 != 0) goto L_0x04d4
        L_0x04d2:
            r0 = 1
            goto L_0x04d5
        L_0x04d4:
            r0 = 0
        L_0x04d5:
            r2 = r0
            goto L_0x04f4
        L_0x04d7:
            if (r12 != 0) goto L_0x04e3
            android.widget.FrameLayout r0 = r1.videoPlayerControlFrameLayout
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x04e3
            r0 = 1
            goto L_0x04e4
        L_0x04e3:
            r0 = 0
        L_0x04e4:
            if (r0 == 0) goto L_0x04f3
            im.bclpbkiauv.ui.components.AnimatedFileDrawable r2 = r1.currentAnimation
            if (r2 == 0) goto L_0x04f3
            boolean r2 = r2.isLoadingStream()
            if (r2 != 0) goto L_0x04f3
            r0 = 0
            r2 = r0
            goto L_0x04f4
        L_0x04f3:
            r2 = r0
        L_0x04f4:
            if (r2 == 0) goto L_0x051e
            r32.save()
            float r7 = r9 / r15
            r3 = r19
            r8.translate(r3, r7)
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r4 = 0
            r0 = r0[r4]
            r5 = 1065353216(0x3f800000, float:1.0)
            float r13 = r5 - r26
            r0.setScale(r13)
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r4]
            r0.setAlpha(r11)
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r4]
            r0.onDraw(r8)
            r32.restore()
            goto L_0x0520
        L_0x051e:
            r3 = r19
        L_0x0520:
            boolean r0 = r1.pipAnimationInProgress
            if (r0 != 0) goto L_0x0550
            im.bclpbkiauv.ui.components.RadialProgressView r0 = r1.miniProgressView
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x0530
            android.animation.AnimatorSet r0 = r1.miniProgressAnimator
            if (r0 == 0) goto L_0x0550
        L_0x0530:
            r32.save()
            im.bclpbkiauv.ui.components.RadialProgressView r0 = r1.miniProgressView
            int r0 = r0.getLeft()
            float r0 = (float) r0
            float r0 = r0 + r3
            im.bclpbkiauv.ui.components.RadialProgressView r4 = r1.miniProgressView
            int r4 = r4.getTop()
            float r4 = (float) r4
            float r7 = r9 / r15
            float r4 = r4 + r7
            r8.translate(r0, r4)
            im.bclpbkiauv.ui.components.RadialProgressView r0 = r1.miniProgressView
            r0.draw(r8)
            r32.restore()
        L_0x0550:
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.sideImage
            im.bclpbkiauv.messenger.ImageReceiver r4 = r1.leftImage
            if (r0 != r4) goto L_0x0622
            boolean r0 = r0.hasBitmapImage()
            if (r0 == 0) goto L_0x05d7
            r32.save()
            int r0 = r31.getContainerViewWidth()
            r4 = 2
            int r0 = r0 / r4
            float r0 = (float) r0
            int r5 = r31.getContainerViewHeight()
            int r5 = r5 / r4
            float r4 = (float) r5
            r8.translate(r0, r4)
            int r0 = r31.getContainerViewWidth()
            float r0 = (float) r0
            float r4 = r1.scale
            r5 = 1065353216(0x3f800000, float:1.0)
            float r4 = r4 + r5
            float r0 = r0 * r4
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r0 = r0 + r4
            float r0 = -r0
            float r0 = r0 / r16
            r5 = r30
            float r0 = r0 + r5
            r4 = 0
            r8.translate(r0, r4)
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.sideImage
            int r0 = r0.getBitmapWidth()
            im.bclpbkiauv.messenger.ImageReceiver r4 = r1.sideImage
            int r4 = r4.getBitmapHeight()
            int r6 = r31.getContainerViewWidth()
            float r6 = (float) r6
            float r7 = (float) r0
            float r6 = r6 / r7
            int r7 = r31.getContainerViewHeight()
            float r7 = (float) r7
            float r10 = (float) r4
            float r7 = r7 / r10
            int r10 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r10 <= 0) goto L_0x05ab
            r10 = r7
            goto L_0x05ac
        L_0x05ab:
            r10 = r6
        L_0x05ac:
            float r13 = (float) r0
            float r13 = r13 * r10
            int r13 = (int) r13
            float r14 = (float) r4
            float r14 = r14 * r10
            int r14 = (int) r14
            r18 = r0
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.sideImage
            r19 = r2
            r2 = 1065353216(0x3f800000, float:1.0)
            r0.setAlpha(r2)
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.sideImage
            int r2 = -r13
            r22 = 2
            int r2 = r2 / 2
            r24 = r3
            int r3 = -r14
            int r3 = r3 / 2
            r0.setImageCoords(r2, r3, r13, r14)
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.sideImage
            r0.draw(r8)
            r32.restore()
            goto L_0x05dd
        L_0x05d7:
            r19 = r2
            r24 = r3
            r5 = r30
        L_0x05dd:
            im.bclpbkiauv.ui.components.GroupedPhotosListView r0 = r1.groupedPhotosListView
            r2 = 1065353216(0x3f800000, float:1.0)
            float r13 = r2 - r11
            r0.setMoveProgress(r13)
            r32.save()
            float r7 = r9 / r15
            r8.translate(r5, r7)
            int r0 = r31.getContainerViewWidth()
            float r0 = (float) r0
            float r3 = r1.scale
            float r3 = r3 + r2
            float r0 = r0 * r3
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            float r2 = (float) r2
            float r0 = r0 + r2
            float r0 = -r0
            float r0 = r0 / r16
            float r2 = -r9
            float r2 = r2 / r15
            r8.translate(r0, r2)
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r2 = 2
            r0 = r0[r2]
            r3 = 1065353216(0x3f800000, float:1.0)
            r0.setScale(r3)
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r2]
            r0.setAlpha(r3)
            im.bclpbkiauv.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r2]
            r0.onDraw(r8)
            r32.restore()
            goto L_0x0628
        L_0x0622:
            r19 = r2
            r24 = r3
            r5 = r30
        L_0x0628:
            int r0 = r1.waitingForDraw
            if (r0 == 0) goto L_0x0687
            r2 = 1
            int r0 = r0 - r2
            r1.waitingForDraw = r0
            if (r0 != 0) goto L_0x0682
            android.widget.ImageView r0 = r1.textureImageView
            if (r0 == 0) goto L_0x0679
            android.view.TextureView r0 = r1.videoTextureView     // Catch:{ all -> 0x0651 }
            int r0 = r0.getWidth()     // Catch:{ all -> 0x0651 }
            android.view.TextureView r2 = r1.videoTextureView     // Catch:{ all -> 0x0651 }
            int r2 = r2.getHeight()     // Catch:{ all -> 0x0651 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0651 }
            android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.Bitmaps.createBitmap(r0, r2, r3)     // Catch:{ all -> 0x0651 }
            r1.currentBitmap = r0     // Catch:{ all -> 0x0651 }
            android.view.TextureView r2 = r1.changedTextureView     // Catch:{ all -> 0x0651 }
            r2.getBitmap(r0)     // Catch:{ all -> 0x0651 }
            r2 = 0
            goto L_0x0661
        L_0x0651:
            r0 = move-exception
            android.graphics.Bitmap r2 = r1.currentBitmap
            if (r2 == 0) goto L_0x065d
            r2.recycle()
            r2 = 0
            r1.currentBitmap = r2
            goto L_0x065e
        L_0x065d:
            r2 = 0
        L_0x065e:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0661:
            android.graphics.Bitmap r0 = r1.currentBitmap
            if (r0 == 0) goto L_0x0673
            android.widget.ImageView r0 = r1.textureImageView
            r3 = 0
            r0.setVisibility(r3)
            android.widget.ImageView r0 = r1.textureImageView
            android.graphics.Bitmap r3 = r1.currentBitmap
            r0.setImageBitmap(r3)
            goto L_0x067a
        L_0x0673:
            android.widget.ImageView r0 = r1.textureImageView
            r0.setImageDrawable(r2)
            goto L_0x067a
        L_0x0679:
            r2 = 0
        L_0x067a:
            im.bclpbkiauv.ui.components.PipVideoView r0 = r1.pipVideoView
            r0.close()
            r1.pipVideoView = r2
            goto L_0x0687
        L_0x0682:
            im.bclpbkiauv.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
        L_0x0687:
            boolean r0 = r1.padImageForHorizontalInsets
            if (r0 == 0) goto L_0x068e
            r32.restore()
        L_0x068e:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.aspectRatioFrameLayout
            if (r0 == 0) goto L_0x06d7
            im.bclpbkiauv.ui.components.VideoForwardDrawable r0 = r1.videoForwardDrawable
            boolean r0 = r0.isAnimating()
            if (r0 == 0) goto L_0x06d7
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.aspectRatioFrameLayout
            int r0 = r0.getMeasuredHeight()
            float r0 = (float) r0
            float r2 = r1.scale
            r3 = 1065353216(0x3f800000, float:1.0)
            float r2 = r2 - r3
            float r0 = r0 * r2
            int r0 = (int) r0
            r2 = 2
            int r0 = r0 / r2
            im.bclpbkiauv.ui.components.VideoForwardDrawable r2 = r1.videoForwardDrawable
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r3 = r1.aspectRatioFrameLayout
            int r3 = r3.getLeft()
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r4 = r1.aspectRatioFrameLayout
            int r4 = r4.getTop()
            int r4 = r4 - r0
            float r7 = r9 / r15
            int r6 = (int) r7
            int r4 = r4 + r6
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r6 = r1.aspectRatioFrameLayout
            int r6 = r6.getRight()
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r7 = r1.aspectRatioFrameLayout
            int r7 = r7.getBottom()
            int r7 = r7 + r0
            float r10 = r9 / r15
            int r10 = (int) r10
            int r7 = r7 + r10
            r2.setBounds(r3, r4, r6, r7)
            im.bclpbkiauv.ui.components.VideoForwardDrawable r2 = r1.videoForwardDrawable
            r2.draw(r8)
        L_0x06d7:
            return
        L_0x06d8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PhotoViewer.onDraw(android.graphics.Canvas):void");
    }

    public /* synthetic */ void lambda$onDraw$51$PhotoViewer() {
        setImageIndex(this.currentIndex + 1, false);
    }

    public /* synthetic */ void lambda$onDraw$52$PhotoViewer() {
        setImageIndex(this.currentIndex - 1, false);
    }

    private void onActionClick(boolean download) {
        if ((this.currentMessageObject != null || this.currentBotInlineResult != null) && this.currentFileNames[0] != null) {
            Uri uri = null;
            File file = null;
            this.isStreaming = false;
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                if (!(messageObject.messageOwner.attachPath == null || this.currentMessageObject.messageOwner.attachPath.length() == 0)) {
                    file = new File(this.currentMessageObject.messageOwner.attachPath);
                    if (!file.exists()) {
                        file = null;
                    }
                }
                if (file == null) {
                    file = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
                    if (!file.exists()) {
                        file = null;
                        if (SharedConfig.streamMedia && ((int) this.currentMessageObject.getDialogId()) != 0 && this.currentMessageObject.isVideo() && this.currentMessageObject.canStreamVideo()) {
                            try {
                                int reference = FileLoader.getInstance(this.currentMessageObject.currentAccount).getFileReference(this.currentMessageObject);
                                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
                                TLRPC.Document document = this.currentMessageObject.getDocument();
                                StringBuilder sb = new StringBuilder();
                                sb.append("?account=");
                                sb.append(this.currentMessageObject.currentAccount);
                                sb.append("&id=");
                                sb.append(document.id);
                                sb.append("&hash=");
                                sb.append(document.access_hash);
                                sb.append("&dc=");
                                sb.append(document.dc_id);
                                sb.append("&size=");
                                sb.append(document.size);
                                sb.append("&mime=");
                                sb.append(URLEncoder.encode(document.mime_type, "UTF-8"));
                                sb.append("&rid=");
                                sb.append(reference);
                                sb.append("&name=");
                                sb.append(URLEncoder.encode(FileLoader.getDocumentFileName(document), "UTF-8"));
                                sb.append("&reference=");
                                sb.append(Utilities.bytesToHex(document.file_reference != null ? document.file_reference : new byte[0]));
                                String params = sb.toString();
                                uri = Uri.parse("hchat://" + this.currentMessageObject.getFileName() + params);
                                this.isStreaming = true;
                                checkProgress(0, false);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            } else {
                TLRPC.BotInlineResult botInlineResult = this.currentBotInlineResult;
                if (botInlineResult != null) {
                    if (botInlineResult.document != null) {
                        file = FileLoader.getPathToAttach(this.currentBotInlineResult.document);
                        if (!file.exists()) {
                            file = null;
                        }
                    } else if (this.currentBotInlineResult.content instanceof TLRPC.TL_webDocument) {
                        File directory = FileLoader.getDirectory(4);
                        file = new File(directory, Utilities.MD5(this.currentBotInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.currentBotInlineResult.content.url, "mp4"));
                        if (!file.exists()) {
                            file = null;
                        }
                    }
                }
            }
            if (file != null && uri == null) {
                uri = Uri.fromFile(file);
            }
            if (uri == null) {
                if (download) {
                    if (this.currentMessageObject == null) {
                        TLRPC.BotInlineResult botInlineResult2 = this.currentBotInlineResult;
                        if (botInlineResult2 != null) {
                            if (botInlineResult2.document != null) {
                                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                                    FileLoader.getInstance(this.currentAccount).loadFile(this.currentBotInlineResult.document, this.currentMessageObject, 1, 0);
                                } else {
                                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentBotInlineResult.document);
                                }
                            } else if (this.currentBotInlineResult.content instanceof TLRPC.TL_webDocument) {
                                if (!ImageLoader.getInstance().isLoadingHttpFile(this.currentBotInlineResult.content.url)) {
                                    ImageLoader.getInstance().loadHttpFile(this.currentBotInlineResult.content.url, "mp4", this.currentAccount);
                                } else {
                                    ImageLoader.getInstance().cancelLoadHttpFile(this.currentBotInlineResult.content.url);
                                }
                            }
                        }
                    } else if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                        FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
                    } else {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
                    }
                    Drawable drawable = this.centerImage.getStaticThumb();
                    if (drawable instanceof OtherDocumentPlaceholderDrawable) {
                        ((OtherDocumentPlaceholderDrawable) drawable).checkFileExist();
                    }
                }
            } else if (this.sharedMediaType != 1 || this.currentMessageObject.canPreviewDocument()) {
                preparePlayer(uri, true, false);
            } else {
                AndroidUtilities.openDocument(this.currentMessageObject, this.parentActivity, (BaseFragment) null);
            }
        }
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        if (this.canZoom || this.doubleTapEnabled) {
            return false;
        }
        return onSingleTapConfirmed(e);
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.scale == 1.0f) {
            return false;
        }
        this.scroller.abortAnimation();
        this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(velocityX), Math.round(velocityY), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
        this.containerView.postInvalidate();
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        int state;
        MessageObject messageObject;
        if (this.discardTap) {
            return false;
        }
        if (this.containerView.getTag() != null) {
            AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
            boolean drawTextureView = aspectRatioFrameLayout2 != null && aspectRatioFrameLayout2.getVisibility() == 0;
            float x = e.getX();
            float y = e.getY();
            if (this.sharedMediaType != 1 || (messageObject = this.currentMessageObject) == null) {
                PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                if (photoProgressViewArr[0] != null && this.containerView != null && !drawTextureView && (state = photoProgressViewArr[0].backgroundState) > 0 && state <= 3 && x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, true);
                    return true;
                }
            } else if (!messageObject.canPreviewDocument()) {
                float vy = ((float) (getContainerViewHeight() - AndroidUtilities.dp(360.0f))) / 2.0f;
                if (y >= vy && y <= ((float) AndroidUtilities.dp(360.0f)) + vy) {
                    onActionClick(true);
                    return true;
                }
            }
            toggleActionBar(!this.isActionBarVisible, true);
        } else {
            int i = this.sendPhotoType;
            if (i != 0 && i != 4) {
                TLRPC.BotInlineResult botInlineResult = this.currentBotInlineResult;
                if (botInlineResult != null && (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                    int state2 = this.photoProgressViews[0].backgroundState;
                    if (state2 > 0 && state2 <= 3) {
                        float x2 = e.getX();
                        float y2 = e.getY();
                        if (x2 >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x2 <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y2 >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y2 <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                            onActionClick(true);
                            checkProgress(0, true);
                            return true;
                        }
                    }
                } else if (this.sendPhotoType == 2 && this.isCurrentVideo) {
                    this.videoPlayButton.callOnClick();
                }
            } else if (this.isCurrentVideo) {
                this.videoPlayButton.callOnClick();
            } else {
                this.checkImageView.performClick();
            }
        }
        return true;
    }

    public boolean onDoubleTap(MotionEvent e) {
        boolean z = false;
        if (this.videoPlayer != null && this.videoPlayerControlFrameLayout.getVisibility() == 0) {
            long current = this.videoPlayer.getCurrentPosition();
            long total = this.videoPlayer.getDuration();
            if (total >= 0 && current >= 0 && total != C.TIME_UNSET && current != C.TIME_UNSET) {
                int width = getContainerViewWidth();
                float x = e.getX();
                long old = current;
                if (x >= ((float) ((width / 3) * 2))) {
                    current += OkHttpUtils.DEFAULT_MILLISECONDS;
                } else if (x < ((float) (width / 3))) {
                    current -= OkHttpUtils.DEFAULT_MILLISECONDS;
                }
                if (old != current) {
                    if (current > total) {
                        current = total;
                    } else if (current < 0) {
                        current = 0;
                    }
                    VideoForwardDrawable videoForwardDrawable2 = this.videoForwardDrawable;
                    if (x < ((float) (width / 3))) {
                        z = true;
                    }
                    videoForwardDrawable2.setLeftSide(z);
                    this.videoPlayer.seekTo(current);
                    this.containerView.invalidate();
                    this.videoPlayerSeekbar.setProgress(((float) current) / ((float) total));
                    this.videoPlayerControlFrameLayout.invalidate();
                    return true;
                }
            }
        }
        if (this.canZoom == 0 || ((this.scale == 1.0f && (this.translationY != 0.0f || this.translationX != 0.0f)) || this.animationStartTime != 0 || this.animationInProgress != 0)) {
            return false;
        }
        if (this.scale == 1.0f) {
            float atx = (e.getX() - ((float) (getContainerViewWidth() / 2))) - (((e.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
            float aty = (e.getY() - ((float) (getContainerViewHeight() / 2))) - (((e.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
            updateMinMax(3.0f);
            if (atx < this.minX) {
                atx = this.minX;
            } else if (atx > this.maxX) {
                atx = this.maxX;
            }
            if (aty < this.minY) {
                aty = this.minY;
            } else if (aty > this.maxY) {
                aty = this.maxY;
            }
            animateTo(3.0f, atx, aty, true);
        } else {
            animateTo(1.0f, 0.0f, 0.0f, true);
        }
        this.doubleTap = true;
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private class QualityChooseView extends View {
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private Paint paint = new Paint(1);
        private int sideSide;
        private boolean startMoving;
        private int startMovingQuality;
        private float startX;
        private TextPaint textPaint;

        public QualityChooseView(Context context) {
            super(context);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.textPaint.setColor(-3289651);
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            boolean z = false;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int a = 0;
                while (true) {
                    if (a >= PhotoViewer.this.compressionsCount) {
                        break;
                    }
                    int i = this.sideSide;
                    int i2 = this.lineSize + (this.gapSize * 2);
                    int i3 = this.circleSize;
                    int cx = i + ((i2 + i3) * a) + (i3 / 2);
                    if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                        a++;
                    } else {
                        if (a == PhotoViewer.this.selectedCompression) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingQuality = PhotoViewer.this.selectedCompression;
                    }
                }
            } else if (event.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    int a2 = 0;
                    while (true) {
                        if (a2 >= PhotoViewer.this.compressionsCount) {
                            break;
                        }
                        int i4 = this.sideSide;
                        int i5 = this.lineSize;
                        int i6 = this.gapSize;
                        int i7 = this.circleSize;
                        int cx2 = i4 + (((i6 * 2) + i5 + i7) * a2) + (i7 / 2);
                        int diff = (i5 / 2) + (i7 / 2) + i6;
                        if (x <= ((float) (cx2 - diff)) || x >= ((float) (cx2 + diff))) {
                            a2++;
                        } else if (PhotoViewer.this.selectedCompression != a2) {
                            int unused = PhotoViewer.this.selectedCompression = a2;
                            PhotoViewer.this.didChangedCompressionLevel(false);
                            invalidate();
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (!this.moving) {
                    int a3 = 0;
                    while (true) {
                        if (a3 >= PhotoViewer.this.compressionsCount) {
                            break;
                        }
                        int i8 = this.sideSide;
                        int i9 = this.lineSize + (this.gapSize * 2);
                        int i10 = this.circleSize;
                        int cx3 = i8 + ((i9 + i10) * a3) + (i10 / 2);
                        if (x <= ((float) (cx3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx3))) {
                            a3++;
                        } else if (PhotoViewer.this.selectedCompression != a3) {
                            int unused2 = PhotoViewer.this.selectedCompression = a3;
                            PhotoViewer.this.didChangedCompressionLevel(true);
                            invalidate();
                        }
                    }
                } else if (PhotoViewer.this.selectedCompression != this.startMovingQuality) {
                    PhotoViewer.this.requestVideoPreview(1);
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.circleSize = AndroidUtilities.dp(12.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(18.0f);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            String text;
            Canvas canvas2 = canvas;
            if (PhotoViewer.this.compressionsCount != 1) {
                this.lineSize = (((getMeasuredWidth() - (this.circleSize * PhotoViewer.this.compressionsCount)) - (this.gapSize * 8)) - (this.sideSide * 2)) / (PhotoViewer.this.compressionsCount - 1);
            } else {
                this.lineSize = ((getMeasuredWidth() - (this.circleSize * PhotoViewer.this.compressionsCount)) - (this.gapSize * 8)) - (this.sideSide * 2);
            }
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(6.0f);
            int a = 0;
            while (a < PhotoViewer.this.compressionsCount) {
                int i = this.sideSide;
                int i2 = this.lineSize + (this.gapSize * 2);
                int i3 = this.circleSize;
                int cx = i + ((i2 + i3) * a) + (i3 / 2);
                if (a <= PhotoViewer.this.selectedCompression) {
                    this.paint.setColor(-11292945);
                } else {
                    this.paint.setColor(1728053247);
                }
                if (a == PhotoViewer.this.compressionsCount - 1) {
                    text = Math.min(PhotoViewer.this.originalWidth, PhotoViewer.this.originalHeight) + TtmlNode.TAG_P;
                } else if (a == 0) {
                    text = "240p";
                } else if (a == 1) {
                    text = "360p";
                } else if (a == 2) {
                    text = "480p";
                } else {
                    text = "720p";
                }
                float width = this.textPaint.measureText(text);
                canvas2.drawCircle((float) cx, (float) cy, (float) (a == PhotoViewer.this.selectedCompression ? AndroidUtilities.dp(8.0f) : this.circleSize / 2), this.paint);
                canvas2.drawText(text, ((float) cx) - (width / 2.0f), (float) (cy - AndroidUtilities.dp(16.0f)), this.textPaint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (this.lineSize + x), (float) (AndroidUtilities.dp(2.0f) + cy), this.paint);
                }
                a++;
            }
        }
    }

    public void updateMuteButton() {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.setMute(this.muteVideo);
        }
        if (!this.videoHasAudio) {
            this.muteItem.setEnabled(false);
            this.muteItem.setClickable(false);
            this.muteItem.animate().alpha(0.5f).setDuration(180).start();
            return;
        }
        this.muteItem.setEnabled(true);
        this.muteItem.setClickable(true);
        this.muteItem.animate().alpha(1.0f).setDuration(180).start();
        if (this.muteVideo) {
            this.actionBar.setSubtitle((CharSequence) null);
            this.muteItem.setImageResource(R.drawable.volume_off);
            this.muteItem.setColorFilter(new PorterDuffColorFilter(-12734994, PorterDuff.Mode.MULTIPLY));
            if (this.compressItem.getTag() != null) {
                this.compressItem.setClickable(false);
                this.compressItem.setAlpha(0.5f);
                this.compressItem.setEnabled(false);
            }
            this.videoTimelineView.setMaxProgressDiff(30000.0f / this.videoDuration);
            this.muteItem.setContentDescription(LocaleController.getString("NoSound", R.string.NoSound));
            return;
        }
        this.muteItem.setColorFilter((ColorFilter) null);
        this.actionBar.setSubtitle(this.currentSubtitle);
        this.muteItem.setImageResource(R.drawable.volume_on);
        this.muteItem.setContentDescription(LocaleController.getString("Sound", R.string.Sound));
        if (this.compressItem.getTag() != null) {
            this.compressItem.setClickable(true);
            this.compressItem.setAlpha(1.0f);
            this.compressItem.setEnabled(true);
        }
        this.videoTimelineView.setMaxProgressDiff(1.0f);
    }

    /* access modifiers changed from: private */
    public void didChangedCompressionLevel(boolean request) {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("compress_video2", this.selectedCompression);
        editor.commit();
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        if (request) {
            requestVideoPreview(1);
        }
    }

    /* access modifiers changed from: private */
    public void updateVideoInfo() {
        int height;
        int width;
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            if (this.compressionsCount == 0) {
                actionBar2.setSubtitle((CharSequence) null);
                return;
            }
            int i = this.selectedCompression;
            if (i == 0) {
                this.compressItem.setImageResource(R.drawable.video_240);
            } else if (i == 1) {
                this.compressItem.setImageResource(R.drawable.video_360);
            } else if (i == 2) {
                this.compressItem.setImageResource(R.drawable.video_480);
            } else if (i == 3) {
                this.compressItem.setImageResource(R.drawable.video_720);
            } else if (i == 4) {
                this.compressItem.setImageResource(R.drawable.video_1080);
            }
            ImageView imageView = this.compressItem;
            imageView.setContentDescription(LocaleController.getString("AccDescrVideoQuality", R.string.AccDescrVideoQuality) + ", " + new String[]{"240", "360", "480", "720", "1080"}[Math.max(0, this.selectedCompression)]);
            this.estimatedDuration = (long) Math.ceil((double) ((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
            if (this.compressItem.getTag() == null || this.selectedCompression == this.compressionsCount - 1) {
                int width2 = this.rotationValue;
                width = (width2 == 90 || width2 == 270) ? this.originalHeight : this.originalWidth;
                int i2 = this.rotationValue;
                height = (i2 == 90 || i2 == 270) ? this.originalWidth : this.originalHeight;
                this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.estimatedDuration) / this.videoDuration));
            } else {
                int i3 = this.rotationValue;
                width = (i3 == 90 || i3 == 270) ? this.resultHeight : this.resultWidth;
                int i4 = this.rotationValue;
                height = (i4 == 90 || i4 == 270) ? this.resultWidth : this.resultHeight;
                int i5 = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration));
                this.estimatedSize = i5;
                this.estimatedSize = i5 + ((i5 / 32768) * 16);
            }
            this.videoCutStart = this.videoTimelineView.getLeftProgress();
            this.videoCutEnd = this.videoTimelineView.getRightProgress();
            float f = this.videoCutStart;
            if (f == 0.0f) {
                this.startTime = -1;
            } else {
                this.startTime = ((long) (f * this.videoDuration)) * 1000;
            }
            float f2 = this.videoCutEnd;
            if (f2 == 1.0f) {
                this.endTime = -1;
            } else {
                this.endTime = ((long) (f2 * this.videoDuration)) * 1000;
            }
            String videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
            long j = this.estimatedDuration;
            int minutes = (int) ((j / 1000) / 60);
            String format = String.format("%s, %s", new Object[]{videoDimension, String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf(minutes), Integer.valueOf(((int) Math.ceil(((double) j) / 1000.0d)) - (minutes * 60)), AndroidUtilities.formatFileSize((long) this.estimatedSize)})});
            this.currentSubtitle = format;
            ActionBar actionBar3 = this.actionBar;
            if (this.muteVideo) {
                format = null;
            }
            actionBar3.setSubtitle(format);
        }
    }

    /* access modifiers changed from: private */
    public void requestVideoPreview(int request) {
        if (this.videoPreviewMessageObject != null) {
            MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
        }
        boolean z = true;
        boolean wasRequestingPreview = this.requestingPreview && !this.tryStartRequestPreviewOnFinish;
        this.requestingPreview = false;
        this.loadInitialVideo = false;
        this.progressView.setVisibility(4);
        if (request != 1) {
            this.tryStartRequestPreviewOnFinish = false;
            this.photoProgressViews[0].setBackgroundState(3, false);
            if (request == 2) {
                preparePlayer(this.currentPlayingVideoFile, false, false);
                this.videoPlayer.seekTo((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            }
        } else if (this.resultHeight == this.originalHeight && this.resultWidth == this.originalWidth) {
            this.tryStartRequestPreviewOnFinish = false;
            PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
            photoProgressViewArr[0].setProgress(0.0f, photoProgressViewArr[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0);
            this.photoProgressViews[0].setBackgroundState(3, false);
            if (!wasRequestingPreview) {
                preparePlayer(this.currentPlayingVideoFile, false, false);
                this.videoPlayer.seekTo((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            } else {
                this.progressView.setVisibility(0);
                this.loadInitialVideo = true;
            }
        } else {
            this.requestingPreview = true;
            releasePlayer(false);
            if (this.videoPreviewMessageObject == null) {
                TLRPC.TL_message message = new TLRPC.TL_message();
                message.id = 0;
                message.message = "";
                message.media = new TLRPC.TL_messageMediaEmpty();
                message.action = new TLRPC.TL_messageActionEmpty();
                message.dialog_id = this.currentDialogId;
                MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, message, false);
                this.videoPreviewMessageObject = messageObject;
                messageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), "video_preview.mp4").getAbsolutePath();
                this.videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
                this.videoPreviewMessageObject.videoEditedInfo.rotationValue = this.rotationValue;
                this.videoPreviewMessageObject.videoEditedInfo.originalWidth = this.originalWidth;
                this.videoPreviewMessageObject.videoEditedInfo.originalHeight = this.originalHeight;
                this.videoPreviewMessageObject.videoEditedInfo.framerate = this.videoFramerate;
                this.videoPreviewMessageObject.videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
            }
            VideoEditedInfo videoEditedInfo = this.videoPreviewMessageObject.videoEditedInfo;
            long j = this.startTime;
            videoEditedInfo.startTime = j;
            long start = j;
            VideoEditedInfo videoEditedInfo2 = this.videoPreviewMessageObject.videoEditedInfo;
            long j2 = this.endTime;
            videoEditedInfo2.endTime = j2;
            long end = j2;
            if (start == -1) {
                start = 0;
            }
            if (end == -1) {
                end = (long) (this.videoDuration * 1000.0f);
            }
            if (end - start > 5000000) {
                this.videoPreviewMessageObject.videoEditedInfo.endTime = 5000000 + start;
            }
            this.videoPreviewMessageObject.videoEditedInfo.bitrate = this.bitrate;
            this.videoPreviewMessageObject.videoEditedInfo.resultWidth = this.resultWidth;
            this.videoPreviewMessageObject.videoEditedInfo.resultHeight = this.resultHeight;
            if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {
                this.tryStartRequestPreviewOnFinish = true;
            }
            this.requestingPreview = true;
            PhotoProgressView[] photoProgressViewArr2 = this.photoProgressViews;
            PhotoProgressView photoProgressView = photoProgressViewArr2[0];
            if (!(photoProgressViewArr2[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0)) {
                z = false;
            }
            photoProgressView.setProgress(0.0f, z);
            this.photoProgressViews[0].setBackgroundState(0, false);
        }
        this.containerView.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateWidthHeightBitrateForCompression() {
        float maxSize;
        int targetBitrate;
        int i = this.compressionsCount;
        if (i > 0) {
            if (this.selectedCompression >= i) {
                this.selectedCompression = i - 1;
            }
            int i2 = this.selectedCompression;
            if (i2 != this.compressionsCount - 1) {
                if (i2 == 0) {
                    maxSize = 426.0f;
                    targetBitrate = 400000;
                } else if (i2 == 1) {
                    maxSize = 640.0f;
                    targetBitrate = 900000;
                } else if (i2 != 2) {
                    targetBitrate = 2621440;
                    maxSize = 1280.0f;
                } else {
                    maxSize = 854.0f;
                    targetBitrate = 1100000;
                }
                int i3 = this.originalWidth;
                int i4 = this.originalHeight;
                float scale2 = maxSize / (i3 > i4 ? (float) i3 : (float) i4);
                if (this.selectedCompression != this.compressionsCount - 1 || scale2 < 1.0f) {
                    this.resultWidth = Math.round((((float) this.originalWidth) * scale2) / 2.0f) * 2;
                    this.resultHeight = Math.round((((float) this.originalHeight) * scale2) / 2.0f) * 2;
                } else {
                    this.resultWidth = this.originalWidth;
                    this.resultHeight = this.originalHeight;
                }
                if (this.bitrate != 0) {
                    int min = Math.min(targetBitrate, (int) (((float) this.originalBitrate) / scale2));
                    this.bitrate = min;
                    this.videoFramesSize = (long) ((((float) (min / 8)) * this.videoDuration) / 1000.0f);
                }
            }
        }
    }

    private void showQualityView(final boolean show) {
        if (show) {
            this.previousCompression = this.selectedCompression;
        }
        AnimatorSet animatorSet = this.qualityChooseViewAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.qualityChooseViewAnimation = new AnimatorSet();
        float f = 0.0f;
        if (show) {
            this.qualityChooseView.setTag(1);
            this.qualityChooseViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.pickerView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(152.0f)}), ObjectAnimator.ofFloat(this.pickerViewSendButton, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(152.0f)}), ObjectAnimator.ofFloat(this.bottomLayout, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f)), (float) AndroidUtilities.dp(104.0f)})});
        } else {
            this.qualityChooseView.setTag((Object) null);
            this.qualityChooseViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.qualityChooseView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(166.0f)}), ObjectAnimator.ofFloat(this.qualityPicker, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(166.0f)}), ObjectAnimator.ofFloat(this.bottomLayout, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f)), (float) AndroidUtilities.dp(118.0f)})});
        }
        this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                    AnimatorSet unused = PhotoViewer.this.qualityChooseViewAnimation = new AnimatorSet();
                    if (show) {
                        PhotoViewer.this.qualityChooseView.setVisibility(0);
                        PhotoViewer.this.qualityPicker.setVisibility(0);
                        PhotoViewer.this.qualityChooseViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.qualityChooseView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.qualityPicker, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))})});
                    } else {
                        PhotoViewer.this.qualityChooseView.setVisibility(4);
                        PhotoViewer.this.qualityPicker.setVisibility(4);
                        PhotoViewer.this.qualityChooseViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))})});
                    }
                    PhotoViewer.this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                                AnimatorSet unused = PhotoViewer.this.qualityChooseViewAnimation = null;
                            }
                        }
                    });
                    PhotoViewer.this.qualityChooseViewAnimation.setDuration(200);
                    PhotoViewer.this.qualityChooseViewAnimation.setInterpolator(new AccelerateInterpolator());
                    PhotoViewer.this.qualityChooseViewAnimation.start();
                }
            }

            public void onAnimationCancel(Animator animation) {
                AnimatorSet unused = PhotoViewer.this.qualityChooseViewAnimation = null;
            }
        });
        this.qualityChooseViewAnimation.setDuration(200);
        this.qualityChooseViewAnimation.setInterpolator(new DecelerateInterpolator());
        this.qualityChooseViewAnimation.start();
        if (this.cameraItem.getVisibility() == 0) {
            float f2 = 0.25f;
            ViewPropertyAnimator scaleX = this.cameraItem.animate().scaleX(show ? 0.25f : 1.0f);
            if (!show) {
                f2 = 1.0f;
            }
            ViewPropertyAnimator scaleY = scaleX.scaleY(f2);
            if (!show) {
                f = 1.0f;
            }
            scaleY.alpha(f).setDuration(200);
        }
    }

    private ByteArrayInputStream cleanBuffer(byte[] data) {
        byte[] output = new byte[data.length];
        int inPos = 0;
        int outPos = 0;
        while (inPos < data.length) {
            if (data[inPos] == 0 && data[inPos + 1] == 0 && data[inPos + 2] == 3) {
                output[outPos] = 0;
                output[outPos + 1] = 0;
                inPos += 3;
                outPos += 2;
            } else {
                output[outPos] = data[inPos];
                inPos++;
                outPos++;
            }
        }
        return new ByteArrayInputStream(output, 0, outPos);
    }

    private void processOpenVideo(final String videoPath, boolean muted, float start, float end) {
        if (this.currentLoadingVideoRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.currentLoadingVideoRunnable);
            this.currentLoadingVideoRunnable = null;
        }
        this.videoTimelineView.setVideoPath(videoPath, start, end);
        this.videoPreviewMessageObject = null;
        setCompressItemEnabled(false, true);
        this.muteVideo = muted;
        Object object = this.imagesArrLocals.get(this.currentIndex);
        if (object instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) object).editedInfo = getCurrentVideoEditedInfo();
        }
        this.compressionsCount = -1;
        this.rotationValue = 0;
        this.videoFramerate = 25;
        this.originalSize = new File(videoPath).length();
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        AnonymousClass46 r3 = new Runnable() {
            public void run() {
                if (PhotoViewer.this.currentLoadingVideoRunnable == this) {
                    int[] params = new int[9];
                    AnimatedFileDrawable.getVideoInfo(videoPath, params);
                    if (PhotoViewer.this.currentLoadingVideoRunnable == this) {
                        Runnable unused = PhotoViewer.this.currentLoadingVideoRunnable = null;
                        AndroidUtilities.runOnUIThread(new Runnable(params) {
                            private final /* synthetic */ int[] f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PhotoViewer.AnonymousClass46.this.lambda$run$0$PhotoViewer$46(this.f$1);
                            }
                        });
                    }
                }
            }

            public /* synthetic */ void lambda$run$0$PhotoViewer$46(int[] params) {
                if (PhotoViewer.this.parentActivity != null) {
                    boolean unused = PhotoViewer.this.videoHasAudio = params[0] != 0;
                    long unused2 = PhotoViewer.this.audioFramesSize = (long) params[5];
                    long unused3 = PhotoViewer.this.videoFramesSize = (long) params[6];
                    float unused4 = PhotoViewer.this.videoDuration = (float) params[4];
                    PhotoViewer photoViewer = PhotoViewer.this;
                    int unused5 = photoViewer.originalBitrate = photoViewer.bitrate = params[3];
                    int unused6 = PhotoViewer.this.videoFramerate = params[7];
                    if (PhotoViewer.this.bitrate > 900000) {
                        int unused7 = PhotoViewer.this.bitrate = 900000;
                    }
                    if (PhotoViewer.this.videoHasAudio) {
                        int unused8 = PhotoViewer.this.rotationValue = params[8];
                        PhotoViewer photoViewer2 = PhotoViewer.this;
                        int unused9 = photoViewer2.resultWidth = photoViewer2.originalWidth = params[1];
                        PhotoViewer photoViewer3 = PhotoViewer.this;
                        int unused10 = photoViewer3.resultHeight = photoViewer3.originalHeight = params[2];
                        int unused11 = PhotoViewer.this.selectedCompression = MessagesController.getGlobalMainSettings().getInt("compress_video2", 1);
                        if (PhotoViewer.this.originalWidth > 1280 || PhotoViewer.this.originalHeight > 1280) {
                            int unused12 = PhotoViewer.this.compressionsCount = 5;
                        } else if (PhotoViewer.this.originalWidth > 854 || PhotoViewer.this.originalHeight > 854) {
                            int unused13 = PhotoViewer.this.compressionsCount = 4;
                        } else if (PhotoViewer.this.originalWidth > 640 || PhotoViewer.this.originalHeight > 640) {
                            int unused14 = PhotoViewer.this.compressionsCount = 3;
                        } else if (PhotoViewer.this.originalWidth > 480 || PhotoViewer.this.originalHeight > 480) {
                            int unused15 = PhotoViewer.this.compressionsCount = 2;
                        } else {
                            int unused16 = PhotoViewer.this.compressionsCount = 1;
                        }
                        PhotoViewer.this.updateWidthHeightBitrateForCompression();
                        PhotoViewer photoViewer4 = PhotoViewer.this;
                        photoViewer4.setCompressItemEnabled(photoViewer4.compressionsCount > 1, true);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("compressionsCount = " + PhotoViewer.this.compressionsCount + " w = " + PhotoViewer.this.originalWidth + " h = " + PhotoViewer.this.originalHeight);
                        }
                        if (Build.VERSION.SDK_INT < 18 && PhotoViewer.this.compressItem.getTag() != null) {
                            try {
                                MediaCodecInfo codecInfo = MediaController.selectCodec("video/avc");
                                if (codecInfo == null) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("no codec info for video/avc");
                                    }
                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                } else {
                                    String name = codecInfo.getName();
                                    if (!name.equals("OMX.google.h264.encoder") && !name.equals("OMX.ST.VFM.H264Enc") && !name.equals("OMX.Exynos.avc.enc") && !name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") && !name.equals("OMX.MARVELL.VIDEO.H264ENCODER") && !name.equals("OMX.k3.video.encoder.avc")) {
                                        if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                            if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                                                if (BuildVars.LOGS_ENABLED) {
                                                    FileLog.d("no color format for video/avc");
                                                }
                                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                            }
                                        }
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("unsupported encoder = " + name);
                                    }
                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                }
                            } catch (Exception e) {
                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                FileLog.e((Throwable) e);
                            }
                        }
                        PhotoViewer.this.qualityChooseView.invalidate();
                    } else {
                        int unused17 = PhotoViewer.this.compressionsCount = 0;
                    }
                    PhotoViewer.this.updateVideoInfo();
                    PhotoViewer.this.updateMuteButton();
                }
            }
        };
        this.currentLoadingVideoRunnable = r3;
        dispatchQueue.postRunnable(r3);
    }

    /* access modifiers changed from: private */
    public void setCompressItemEnabled(boolean enabled, boolean animated) {
        ImageView imageView = this.compressItem;
        if (imageView != null) {
            if (enabled && imageView.getTag() != null) {
                return;
            }
            if (enabled || this.compressItem.getTag() != null) {
                this.compressItem.setTag(enabled ? 1 : null);
                this.compressItem.setEnabled(enabled);
                this.compressItem.setClickable(enabled);
                AnimatorSet animatorSet = this.compressItemAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.compressItemAnimation = null;
                }
                float f = 1.0f;
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.compressItemAnimation = animatorSet2;
                    Animator[] animatorArr = new Animator[1];
                    ImageView imageView2 = this.compressItem;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (!enabled) {
                        f = 0.5f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                    animatorSet2.playTogether(animatorArr);
                    this.compressItemAnimation.setDuration(180);
                    this.compressItemAnimation.setInterpolator(decelerateInterpolator);
                    this.compressItemAnimation.start();
                    return;
                }
                ImageView imageView3 = this.compressItem;
                if (!enabled) {
                    f = 0.5f;
                }
                imageView3.setAlpha(f);
            }
        }
    }

    private void updateAccessibilityOverlayVisibility() {
        VideoPlayer videoPlayer2;
        if (this.playButtonAccessibilityOverlay != null) {
            if (!this.isCurrentVideo || ((videoPlayer2 = this.videoPlayer) != null && videoPlayer2.isPlaying())) {
                this.playButtonAccessibilityOverlay.setVisibility(4);
            } else {
                this.playButtonAccessibilityOverlay.setVisibility(0);
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            if (PhotoViewer.this.placeProvider == null || PhotoViewer.this.placeProvider.getSelectedPhotosOrder() == null) {
                return 0;
            }
            return PhotoViewer.this.placeProvider.getSelectedPhotosOrder().size();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PhotoPickerPhotoCell cell = new PhotoPickerPhotoCell(this.mContext, false);
            cell.checkFrame.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.ListAdapter.this.lambda$onCreateViewHolder$0$PhotoViewer$ListAdapter(view);
                }
            });
            return new RecyclerListView.Holder(cell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PhotoViewer$ListAdapter(View v) {
            Object photoEntry = ((View) v.getParent()).getTag();
            int idx = PhotoViewer.this.imagesArrLocals.indexOf(photoEntry);
            if (idx >= 0) {
                int num = PhotoViewer.this.placeProvider.setPhotoChecked(idx, PhotoViewer.this.getCurrentVideoEditedInfo());
                boolean isPhotoChecked = PhotoViewer.this.placeProvider.isPhotoChecked(idx);
                if (idx == PhotoViewer.this.currentIndex) {
                    PhotoViewer.this.checkImageView.setChecked(-1, false, true);
                }
                if (num >= 0) {
                    PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(num);
                }
                PhotoViewer.this.updateSelectedCount();
                return;
            }
            int num2 = PhotoViewer.this.placeProvider.setPhotoUnchecked(photoEntry);
            if (num2 >= 0) {
                PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(num2);
                PhotoViewer.this.updateSelectedCount();
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PhotoPickerPhotoCell cell = (PhotoPickerPhotoCell) holder.itemView;
            cell.itemWidth = AndroidUtilities.dp(82.0f);
            BackupImageView imageView = cell.imageView;
            imageView.setOrientation(0, true);
            Object object = PhotoViewer.this.placeProvider.getSelectedPhotos().get(PhotoViewer.this.placeProvider.getSelectedPhotosOrder().get(position));
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                cell.setTag(photoEntry);
                cell.videoInfoContainer.setVisibility(4);
                if (photoEntry.thumbPath != null) {
                    imageView.setImage(photoEntry.thumbPath, (String) null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                } else if (photoEntry.path != null) {
                    imageView.setOrientation(photoEntry.orientation, true);
                    if (photoEntry.isVideo) {
                        cell.videoInfoContainer.setVisibility(0);
                        int minutes = photoEntry.duration / 60;
                        cell.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(photoEntry.duration - (minutes * 60))}));
                        imageView.setImage("vthumb://" + photoEntry.imageId + LogUtils.COLON + photoEntry.path, (String) null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                    } else {
                        imageView.setImage("thumb://" + photoEntry.imageId + LogUtils.COLON + photoEntry.path, (String) null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                    }
                } else {
                    imageView.setImageResource(R.drawable.nophotos);
                }
                cell.setChecked(-1, true, false);
                cell.checkBox.setVisibility(0);
            } else if (object instanceof MediaController.SearchImage) {
                MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) object;
                cell.setTag(photoEntry2);
                cell.setImage(photoEntry2);
                cell.videoInfoContainer.setVisibility(4);
                cell.setChecked(-1, true, false);
                cell.checkBox.setVisibility(0);
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }
}
