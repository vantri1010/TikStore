package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.MetricAffectingSpan;
import android.text.style.URLSpan;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.view.DisplayCutout;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ArticleViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.ActionBarPopupWindow;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BackDrawable;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AnchorSpan;
import im.bclpbkiauv.ui.components.AnimatedArrowDrawable;
import im.bclpbkiauv.ui.components.AnimatedFileDrawable;
import im.bclpbkiauv.ui.components.AnimationProperties;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.ClippingImageView;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.GroupedPhotosListView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.LineProgressView;
import im.bclpbkiauv.ui.components.LinkPath;
import im.bclpbkiauv.ui.components.RadialProgress2;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.Scroller;
import im.bclpbkiauv.ui.components.SeekBar;
import im.bclpbkiauv.ui.components.ShareAlert;
import im.bclpbkiauv.ui.components.StaticLayoutEx;
import im.bclpbkiauv.ui.components.TableLayout;
import im.bclpbkiauv.ui.components.TextPaintImageReceiverSpan;
import im.bclpbkiauv.ui.components.TextPaintMarkSpan;
import im.bclpbkiauv.ui.components.TextPaintSpan;
import im.bclpbkiauv.ui.components.TextPaintUrlSpan;
import im.bclpbkiauv.ui.components.TextPaintWebpageUrlSpan;
import im.bclpbkiauv.ui.components.TypefaceSpan;
import im.bclpbkiauv.ui.components.VideoPlayer;
import im.bclpbkiauv.ui.components.WebPlayerView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class ArticleViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    public static final Property<WindowView, Float> ARTICLE_VIEWER_INNER_TRANSLATION_X = new AnimationProperties.FloatProperty<WindowView>("innerTranslationX") {
        public void setValue(WindowView object, float value) {
            object.setInnerTranslationX(value);
        }

        public Float get(WindowView object) {
            return Float.valueOf(object.getInnerTranslationX());
        }
    };
    private static volatile ArticleViewer Instance = null;
    private static final int TEXT_FLAG_ITALIC = 2;
    private static final int TEXT_FLAG_MARKED = 64;
    private static final int TEXT_FLAG_MEDIUM = 1;
    private static final int TEXT_FLAG_MONO = 4;
    private static final int TEXT_FLAG_REGULAR = 0;
    private static final int TEXT_FLAG_STRIKE = 32;
    private static final int TEXT_FLAG_SUB = 128;
    private static final int TEXT_FLAG_SUP = 256;
    private static final int TEXT_FLAG_UNDERLINE = 16;
    private static final int TEXT_FLAG_URL = 8;
    private static final int TEXT_FLAG_WEBPAGE_URL = 512;
    /* access modifiers changed from: private */
    public static TextPaint audioTimePaint = new TextPaint(1);
    private static SparseArray<TextPaint> authorTextPaints = new SparseArray<>();
    private static TextPaint channelNamePaint = null;
    /* access modifiers changed from: private */
    public static Paint colorPaint = null;
    /* access modifiers changed from: private */
    public static DecelerateInterpolator decelerateInterpolator = null;
    private static SparseArray<TextPaint> detailsTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint dividerPaint = null;
    /* access modifiers changed from: private */
    public static Paint dotsPaint = null;
    private static TextPaint embedPostAuthorPaint = null;
    private static SparseArray<TextPaint> embedPostCaptionTextPaints = new SparseArray<>();
    private static TextPaint embedPostDatePaint = null;
    private static SparseArray<TextPaint> embedPostTextPaints = new SparseArray<>();
    private static TextPaint errorTextPaint = null;
    private static SparseArray<TextPaint> footerTextPaints = new SparseArray<>();
    private static final int gallery_menu_openin = 3;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_share = 2;
    private static SparseArray<TextPaint> headerTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> kickerTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static TextPaint listTextNumPaint;
    private static SparseArray<TextPaint> listTextPaints = new SparseArray<>();
    private static TextPaint listTextPointerPaint;
    private static SparseArray<TextPaint> mediaCaptionTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> mediaCreditTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> paragraphTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint photoBackgroundPaint;
    private static SparseArray<TextPaint> photoCaptionTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> photoCreditTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint preformattedBackgroundPaint;
    private static SparseArray<TextPaint> preformattedTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Drawable[] progressDrawables;
    /* access modifiers changed from: private */
    public static Paint progressPaint;
    /* access modifiers changed from: private */
    public static Paint quoteLinePaint;
    private static SparseArray<TextPaint> quoteTextPaints = new SparseArray<>();
    private static TextPaint relatedArticleHeaderPaint;
    private static TextPaint relatedArticleTextPaint;
    private static SparseArray<TextPaint> relatedArticleTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint selectorPaint;
    private static SparseArray<TextPaint> subheaderTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> subtitleTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint tableHalfLinePaint;
    /* access modifiers changed from: private */
    public static Paint tableHeaderPaint;
    /* access modifiers changed from: private */
    public static Paint tableLinePaint;
    /* access modifiers changed from: private */
    public static Paint tableStripPaint;
    private static SparseArray<TextPaint> tableTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> titleTextPaints = new SparseArray<>();
    private static Paint urlPaint;
    /* access modifiers changed from: private */
    public static Paint webpageMarkPaint;
    /* access modifiers changed from: private */
    public static Paint webpageUrlPaint;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public WebpageAdapter[] adapter;
    /* access modifiers changed from: private */
    public int anchorsOffsetMeasuredWidth;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    /* access modifiers changed from: private */
    public ClippingImageView animatingImageView;
    /* access modifiers changed from: private */
    public Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues = ((float[][]) Array.newInstance(float.class, new int[]{2, 10}));
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout aspectRatioFrameLayout;
    /* access modifiers changed from: private */
    public boolean attachedToWindow;
    private ImageView backButton;
    private BackDrawable backDrawable;
    /* access modifiers changed from: private */
    public Paint backgroundPaint;
    /* access modifiers changed from: private */
    public Paint blackPaint = new Paint();
    /* access modifiers changed from: private */
    public FrameLayout bottomLayout;
    private boolean canDragDown = true;
    private boolean canZoom = true;
    /* access modifiers changed from: private */
    public TextView captionTextView;
    /* access modifiers changed from: private */
    public TextView captionTextViewNext;
    private ImageReceiver centerImage = new ImageReceiver();
    private boolean changingPage;
    /* access modifiers changed from: private */
    public TLRPC.TL_pageBlockChannel channelBlock;
    /* access modifiers changed from: private */
    public boolean checkingForLongPress = false;
    /* access modifiers changed from: private */
    public boolean collapsed;
    private ColorCell[] colorCells = new ColorCell[3];
    /* access modifiers changed from: private */
    public FrameLayout containerView;
    private int[] coords = new int[2];
    private Drawable copyBackgroundDrawable;
    /* access modifiers changed from: private */
    public ArrayList<BlockEmbedCell> createdWebViews = new ArrayList<>();
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public AnimatorSet currentActionBarAnimation;
    private AnimatedFileDrawable currentAnimation;
    private String[] currentFileNames = new String[3];
    /* access modifiers changed from: private */
    public int currentHeaderHeight;
    /* access modifiers changed from: private */
    public int currentIndex;
    private TLRPC.PageBlock currentMedia;
    /* access modifiers changed from: private */
    public TLRPC.WebPage currentPage;
    private PlaceProviderObject currentPlaceObject;
    /* access modifiers changed from: private */
    public WebPlayerView currentPlayingVideo;
    private int currentRotation;
    /* access modifiers changed from: private */
    public ImageReceiver.BitmapHolder currentThumb;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public WebChromeClient.CustomViewCallback customViewCallback;
    private TextView deleteView;
    private boolean disableShowCheck;
    private boolean discardTap;
    private boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private float dragY;
    private boolean draggingDown;
    /* access modifiers changed from: private */
    public boolean drawBlockSelection;
    private FontCell[] fontCells = new FontCell[2];
    private final int fontSizeCount = 5;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout fullscreenAspectRatioView;
    /* access modifiers changed from: private */
    public TextureView fullscreenTextureView;
    /* access modifiers changed from: private */
    public FrameLayout fullscreenVideoContainer;
    /* access modifiers changed from: private */
    public WebPlayerView fullscreenedVideo;
    private GestureDetector gestureDetector;
    /* access modifiers changed from: private */
    public GroupedPhotosListView groupedPhotosListView;
    boolean hasCutout;
    /* access modifiers changed from: private */
    public Paint headerPaint = new Paint();
    /* access modifiers changed from: private */
    public Paint headerProgressPaint = new Paint();
    /* access modifiers changed from: private */
    public FrameLayout headerView;
    private PlaceProviderObject hideAfterAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet imageMoveAnimation;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.PageBlock> imagesArr = new ArrayList<>();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    /* access modifiers changed from: private */
    public boolean isPhotoVisible;
    /* access modifiers changed from: private */
    public boolean isPlaying;
    /* access modifiers changed from: private */
    public boolean isRtl;
    /* access modifiers changed from: private */
    public boolean isVisible;
    /* access modifiers changed from: private */
    public int lastBlockNum = 1;
    /* access modifiers changed from: private */
    public Object lastInsets;
    private int lastReqId;
    /* access modifiers changed from: private */
    public Drawable layerShadowDrawable;
    /* access modifiers changed from: private */
    public LinearLayoutManager[] layoutManager;
    private ImageReceiver leftImage = new ImageReceiver();
    private Runnable lineProgressTickRunnable;
    private LineProgressView lineProgressView;
    private BottomSheet linkSheet;
    /* access modifiers changed from: private */
    public RecyclerListView[] listView;
    /* access modifiers changed from: private */
    public TLRPC.Chat loadedChannel;
    private boolean loadingChannel;
    private float maxX;
    private float maxY;
    private ActionBarMenuItem menuItem;
    private float minX;
    private float minY;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private boolean nightModeEnabled;
    /* access modifiers changed from: private */
    public FrameLayout nightModeHintView;
    private ImageView nightModeImageView;
    private int openUrlReqId;
    /* access modifiers changed from: private */
    public AnimatorSet pageSwitchAnimation;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.WebPage> pagesStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    /* access modifiers changed from: private */
    public Runnable photoAnimationEndRunnable;
    private int photoAnimationInProgress;
    private PhotoBackgroundDrawable photoBackgroundDrawable = new PhotoBackgroundDrawable(-16777216);
    /* access modifiers changed from: private */
    public View photoContainerBackground;
    /* access modifiers changed from: private */
    public FrameLayoutDrawer photoContainerView;
    private long photoTransitionAnimationStartTime;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = 1.0f;
    private float pinchStartX;
    private float pinchStartY;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow popupWindow;
    private int pressCount = 0;
    /* access modifiers changed from: private */
    public int pressedLayoutY;
    /* access modifiers changed from: private */
    public TextPaintUrlSpan pressedLink;
    /* access modifiers changed from: private */
    public DrawingText pressedLinkOwnerLayout;
    /* access modifiers changed from: private */
    public View pressedLinkOwnerView;
    private int previewsReqId;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public AnimatorSet progressViewAnimation;
    private RadialProgressView[] radialProgressViews = new RadialProgressView[3];
    private ImageReceiver rightImage = new ImageReceiver();
    private float scale = 1.0f;
    /* access modifiers changed from: private */
    public Paint scrimPaint;
    private Scroller scroller;
    private int selectedColor = 0;
    private int selectedFont = 0;
    /* access modifiers changed from: private */
    public int selectedFontSize = 2;
    private ActionBarMenuItem settingsButton;
    /* access modifiers changed from: private */
    public ImageView shareButton;
    private FrameLayout shareContainer;
    private PlaceProviderObject showAfterAnimation;
    /* access modifiers changed from: private */
    public Drawable slideDotBigDrawable;
    /* access modifiers changed from: private */
    public Drawable slideDotDrawable;
    /* access modifiers changed from: private */
    public Paint statusBarPaint = new Paint();
    private int switchImageAfterAnimation;
    /* access modifiers changed from: private */
    public boolean textureUploaded;
    private SimpleTextView titleTextView;
    private long transitionAnimationStartTime;
    private float translationX;
    private float translationY;
    /* access modifiers changed from: private */
    public Runnable updateProgressRunnable = new Runnable() {
        public void run() {
            if (!(ArticleViewer.this.videoPlayer == null || ArticleViewer.this.videoPlayerSeekbar == null || ArticleViewer.this.videoPlayerSeekbar.isDragging())) {
                ArticleViewer.this.videoPlayerSeekbar.setProgress(((float) ArticleViewer.this.videoPlayer.getCurrentPosition()) / ((float) ArticleViewer.this.videoPlayer.getDuration()));
                ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
                ArticleViewer.this.updateVideoPlayerTime();
            }
            if (ArticleViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable, 100);
            }
        }
    };
    private LinkPath urlPath = new LinkPath();
    private VelocityTracker velocityTracker;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    /* access modifiers changed from: private */
    public ImageView videoPlayButton;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;
    /* access modifiers changed from: private */
    public FrameLayout videoPlayerControlFrameLayout;
    /* access modifiers changed from: private */
    public SeekBar videoPlayerSeekbar;
    /* access modifiers changed from: private */
    public TextView videoPlayerTime;
    private TextureView videoTextureView;
    private Dialog visibleDialog;
    private boolean wasLayout;
    private WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowView windowView;
    private boolean zoomAnimation;
    private boolean zooming;

    public static class PlaceProviderObject {
        public int clipBottomAddition;
        public int clipTopAddition;
        public ImageReceiver imageReceiver;
        public int index;
        public View parentView;
        public int radius;
        public float scale = 1.0f;
        public int size;
        public ImageReceiver.BitmapHolder thumb;
        public int viewX;
        public int viewY;
    }

    static /* synthetic */ int access$1104(ArticleViewer x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    static /* synthetic */ int access$13108(ArticleViewer x0) {
        int i = x0.lastBlockNum;
        x0.lastBlockNum = i + 1;
        return i;
    }

    public static ArticleViewer getInstance() {
        ArticleViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (ArticleViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    ArticleViewer articleViewer = new ArticleViewer();
                    localInstance = articleViewer;
                    Instance = articleViewer;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    private class TL_pageBlockRelatedArticlesChild extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public int num;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesChild() {
        }
    }

    private class TL_pageBlockRelatedArticlesShadow extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesShadow() {
        }
    }

    private class TL_pageBlockDetailsChild extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public TLRPC.PageBlock block;
        /* access modifiers changed from: private */
        public TLRPC.PageBlock parent;

        private TL_pageBlockDetailsChild() {
        }
    }

    private class TL_pageBlockDetailsBottom extends TLRPC.PageBlock {
        private TLRPC.TL_pageBlockDetails parent;

        private TL_pageBlockDetailsBottom() {
        }
    }

    private class TL_pageBlockListParent extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public ArrayList<TL_pageBlockListItem> items;
        /* access modifiers changed from: private */
        public int lastFontSize;
        /* access modifiers changed from: private */
        public int lastMaxNumCalcWidth;
        /* access modifiers changed from: private */
        public int level;
        /* access modifiers changed from: private */
        public int maxNumWidth;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockList pageBlockList;

        private TL_pageBlockListParent() {
            this.items = new ArrayList<>();
        }
    }

    private class TL_pageBlockListItem extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public TLRPC.PageBlock blockItem;
        /* access modifiers changed from: private */
        public int index;
        /* access modifiers changed from: private */
        public String num;
        /* access modifiers changed from: private */
        public DrawingText numLayout;
        /* access modifiers changed from: private */
        public TL_pageBlockListParent parent;
        /* access modifiers changed from: private */
        public TLRPC.RichText textItem;

        private TL_pageBlockListItem() {
            this.index = Integer.MAX_VALUE;
        }
    }

    private class TL_pageBlockOrderedListParent extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public ArrayList<TL_pageBlockOrderedListItem> items;
        /* access modifiers changed from: private */
        public int lastFontSize;
        /* access modifiers changed from: private */
        public int lastMaxNumCalcWidth;
        /* access modifiers changed from: private */
        public int level;
        /* access modifiers changed from: private */
        public int maxNumWidth;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockOrderedList pageBlockOrderedList;

        private TL_pageBlockOrderedListParent() {
            this.items = new ArrayList<>();
        }
    }

    private class TL_pageBlockOrderedListItem extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public TLRPC.PageBlock blockItem;
        /* access modifiers changed from: private */
        public int index;
        /* access modifiers changed from: private */
        public String num;
        /* access modifiers changed from: private */
        public DrawingText numLayout;
        /* access modifiers changed from: private */
        public TL_pageBlockOrderedListParent parent;
        /* access modifiers changed from: private */
        public TLRPC.RichText textItem;

        private TL_pageBlockOrderedListItem() {
            this.index = Integer.MAX_VALUE;
        }
    }

    private class TL_pageBlockEmbedPostCaption extends TLRPC.TL_pageBlockEmbedPost {
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockEmbedPost parent;

        private TL_pageBlockEmbedPostCaption() {
        }
    }

    public class DrawingText {
        public LinkPath markPath;
        public StaticLayout textLayout;
        public LinkPath textPath;

        public DrawingText() {
        }

        public void draw(Canvas canvas) {
            LinkPath linkPath = this.textPath;
            if (linkPath != null) {
                canvas.drawPath(linkPath, ArticleViewer.webpageUrlPaint);
            }
            LinkPath linkPath2 = this.markPath;
            if (linkPath2 != null) {
                canvas.drawPath(linkPath2, ArticleViewer.webpageMarkPaint);
            }
            ArticleViewer.this.drawLayoutLink(canvas, this);
            this.textLayout.draw(canvas);
        }

        public CharSequence getText() {
            return this.textLayout.getText();
        }

        public int getLineCount() {
            return this.textLayout.getLineCount();
        }

        public int getLineAscent(int line) {
            return this.textLayout.getLineAscent(line);
        }

        public float getLineLeft(int line) {
            return this.textLayout.getLineLeft(line);
        }

        public float getLineWidth(int line) {
            return this.textLayout.getLineWidth(line);
        }

        public int getHeight() {
            return this.textLayout.getHeight();
        }

        public int getWidth() {
            return this.textLayout.getWidth();
        }
    }

    private class SizeChooseView extends View {
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private Paint paint = new Paint(1);
        private int sideSide;
        private boolean startMoving;
        private int startMovingQuality;
        private float startX;

        public SizeChooseView(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            boolean z = false;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int a = 0;
                while (true) {
                    if (a >= 5) {
                        break;
                    }
                    int i = this.sideSide;
                    int i2 = this.lineSize + (this.gapSize * 2);
                    int i3 = this.circleSize;
                    int cx = i + ((i2 + i3) * a) + (i3 / 2);
                    if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                        a++;
                    } else {
                        if (a == ArticleViewer.this.selectedFontSize) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingQuality = ArticleViewer.this.selectedFontSize;
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
                        if (a2 >= 5) {
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
                        } else if (ArticleViewer.this.selectedFontSize != a2) {
                            int unused = ArticleViewer.this.selectedFontSize = a2;
                            ArticleViewer.this.updatePaintSize();
                            invalidate();
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (!this.moving) {
                    int a3 = 0;
                    while (true) {
                        if (a3 >= 5) {
                            break;
                        }
                        int i8 = this.sideSide;
                        int i9 = this.lineSize + (this.gapSize * 2);
                        int i10 = this.circleSize;
                        int cx3 = i8 + ((i9 + i10) * a3) + (i10 / 2);
                        if (x <= ((float) (cx3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx3))) {
                            a3++;
                        } else if (ArticleViewer.this.selectedFontSize != a3) {
                            int unused2 = ArticleViewer.this.selectedFontSize = a3;
                            ArticleViewer.this.updatePaintSize();
                            invalidate();
                        }
                    }
                } else if (ArticleViewer.this.selectedFontSize != this.startMovingQuality) {
                    ArticleViewer.this.updatePaintSize();
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int size = View.MeasureSpec.getSize(widthMeasureSpec);
            this.circleSize = AndroidUtilities.dp(5.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(17.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * 5)) - ((this.gapSize * 2) * 4)) - (this.sideSide * 2)) / 4;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int cy = getMeasuredHeight() / 2;
            int a = 0;
            while (a < 5) {
                int i = this.sideSide;
                int i2 = this.lineSize + (this.gapSize * 2);
                int i3 = this.circleSize;
                int cx = i + ((i2 + i3) * a) + (i3 / 2);
                if (a <= ArticleViewer.this.selectedFontSize) {
                    this.paint.setColor(-15428119);
                } else {
                    this.paint.setColor(-3355444);
                }
                canvas.drawCircle((float) cx, (float) cy, (float) (a == ArticleViewer.this.selectedFontSize ? AndroidUtilities.dp(4.0f) : this.circleSize / 2), this.paint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (this.lineSize + x), (float) (AndroidUtilities.dp(1.0f) + cy), this.paint);
                }
                a++;
            }
        }
    }

    public class ColorCell extends FrameLayout {
        private int currentColor;
        private boolean selected;
        private TextView textView;

        public ColorCell(Context context) {
            super(context);
            if (ArticleViewer.colorPaint == null) {
                Paint unused = ArticleViewer.colorPaint = new Paint(1);
                Paint unused2 = ArticleViewer.selectorPaint = new Paint(1);
                ArticleViewer.selectorPaint.setColor(-15428119);
                ArticleViewer.selectorPaint.setStyle(Paint.Style.STROKE);
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            }
            setBackgroundDrawable(Theme.createSelectorDrawable(251658240, 2));
            setWillNotDraw(false);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(-14606047);
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
            addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? 17 : 53), 0.0f, (float) (LocaleController.isRTL ? 53 : 17), 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void setTextAndColor(String text, int color) {
            this.textView.setText(text);
            this.currentColor = color;
            invalidate();
        }

        public void select(boolean value) {
            if (this.selected != value) {
                this.selected = value;
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            ArticleViewer.colorPaint.setColor(this.currentColor);
            canvas.drawCircle((float) (!LocaleController.isRTL ? AndroidUtilities.dp(28.0f) : getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), ArticleViewer.colorPaint);
            if (this.selected) {
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
                ArticleViewer.selectorPaint.setColor(-15428119);
                canvas.drawCircle((float) (!LocaleController.isRTL ? AndroidUtilities.dp(28.0f) : getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), ArticleViewer.selectorPaint);
            } else if (this.currentColor == -1) {
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
                ArticleViewer.selectorPaint.setColor(-4539718);
                canvas.drawCircle((float) (!LocaleController.isRTL ? AndroidUtilities.dp(28.0f) : getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(9.0f), ArticleViewer.selectorPaint);
            }
        }
    }

    public class FontCell extends FrameLayout {
        private TextView textView;
        private TextView textView2;
        final /* synthetic */ ArticleViewer this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public FontCell(im.bclpbkiauv.ui.ArticleViewer r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r19
                r2 = r18
                r0.this$0 = r2
                r0.<init>(r1)
                r3 = 251658240(0xf000000, float:6.3108872E-30)
                r4 = 2
                android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r3, r4)
                r0.setBackgroundDrawable(r3)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r1)
                r0.textView = r3
                r4 = -14606047(0xffffffffff212121, float:-2.1417772E38)
                r3.setTextColor(r4)
                android.widget.TextView r3 = r0.textView
                r5 = 1098907648(0x41800000, float:16.0)
                r6 = 1
                r3.setTextSize(r6, r5)
                android.widget.TextView r3 = r0.textView
                r3.setLines(r6)
                android.widget.TextView r3 = r0.textView
                r3.setMaxLines(r6)
                android.widget.TextView r3 = r0.textView
                r3.setSingleLine(r6)
                android.widget.TextView r3 = r0.textView
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r8 = 5
                r9 = 3
                if (r7 == 0) goto L_0x0043
                r7 = 5
                goto L_0x0044
            L_0x0043:
                r7 = 3
            L_0x0044:
                r7 = r7 | 16
                r3.setGravity(r7)
                android.widget.TextView r3 = r0.textView
                r10 = -1082130432(0xffffffffbf800000, float:-1.0)
                r11 = -1082130432(0xffffffffbf800000, float:-1.0)
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x0055
                r7 = 5
                goto L_0x0056
            L_0x0055:
                r7 = 3
            L_0x0056:
                r12 = r7 | 48
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r13 = 17
                r14 = 53
                if (r7 == 0) goto L_0x0063
                r7 = 17
                goto L_0x0065
            L_0x0063:
                r7 = 53
            L_0x0065:
                float r7 = (float) r7
                r15 = 0
                boolean r16 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r16 == 0) goto L_0x006d
                r13 = 53
            L_0x006d:
                float r14 = (float) r13
                r16 = 0
                r13 = r7
                r7 = r14
                r14 = r15
                r15 = r7
                android.widget.FrameLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r3, r7)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r1)
                r0.textView2 = r3
                r3.setTextColor(r4)
                android.widget.TextView r3 = r0.textView2
                r3.setTextSize(r6, r5)
                android.widget.TextView r3 = r0.textView2
                r3.setLines(r6)
                android.widget.TextView r3 = r0.textView2
                r3.setMaxLines(r6)
                android.widget.TextView r3 = r0.textView2
                r3.setSingleLine(r6)
                android.widget.TextView r3 = r0.textView2
                java.lang.String r4 = "Aa"
                r3.setText(r4)
                android.widget.TextView r3 = r0.textView2
                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00a8
                r4 = 5
                goto L_0x00a9
            L_0x00a8:
                r4 = 3
            L_0x00a9:
                r4 = r4 | 16
                r3.setGravity(r4)
                android.widget.TextView r3 = r0.textView2
                r10 = -1082130432(0xffffffffbf800000, float:-1.0)
                r11 = -1082130432(0xffffffffbf800000, float:-1.0)
                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00b9
                goto L_0x00ba
            L_0x00b9:
                r8 = 3
            L_0x00ba:
                r12 = r8 | 48
                r13 = 1099431936(0x41880000, float:17.0)
                r14 = 0
                r15 = 1099431936(0x41880000, float:17.0)
                r16 = 0
                android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r3, r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.FontCell.<init>(im.bclpbkiauv.ui.ArticleViewer, android.content.Context):void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void select(boolean value) {
            this.textView2.setTextColor(value ? -15428119 : -14606047);
        }

        public void setTextAndTypeface(String text, Typeface typeface) {
            this.textView.setText(text);
            this.textView.setTypeface(typeface);
            this.textView2.setTypeface(typeface);
            invalidate();
        }
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean unused = ArticleViewer.this.processTouchEvent(event);
            return true;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            ArticleViewer.this.drawContent(canvas);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return child != ArticleViewer.this.aspectRatioFrameLayout && super.drawChild(canvas, child, drawingTime);
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (ArticleViewer.this.pendingCheckForLongPress == null) {
                ArticleViewer articleViewer = ArticleViewer.this;
                CheckForLongPress unused = articleViewer.pendingCheckForLongPress = new CheckForLongPress();
            }
            ArticleViewer.this.pendingCheckForLongPress.currentPressCount = ArticleViewer.access$1104(ArticleViewer.this);
            if (ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.postDelayed(ArticleViewer.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }
    }

    private class WindowView extends FrameLayout {
        private float alpha;
        private Runnable attachRunnable;
        private int bHeight;
        private int bWidth;
        private int bX;
        private int bY;
        /* access modifiers changed from: private */
        public boolean closeAnimationInProgress;
        private float innerTranslationX;
        private boolean maybeStartTracking;
        /* access modifiers changed from: private */
        public boolean movingPage;
        private boolean selfLayout;
        /* access modifiers changed from: private */
        public int startMovingHeaderHeight;
        /* access modifiers changed from: private */
        public boolean startedTracking;
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker tracker;

        public WindowView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            if (Build.VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
                setMeasuredDimension(widthSize, heightSize);
            } else {
                setMeasuredDimension(widthSize, heightSize);
                WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                if (AndroidUtilities.incorrectDisplaySizeFix) {
                    if (heightSize > AndroidUtilities.displaySize.y) {
                        heightSize = AndroidUtilities.displaySize.y;
                    }
                    heightSize += AndroidUtilities.statusBarHeight;
                }
                int heightSize2 = heightSize - insets.getSystemWindowInsetBottom();
                widthSize -= insets.getSystemWindowInsetRight() + insets.getSystemWindowInsetLeft();
                if (insets.getSystemWindowInsetRight() != 0) {
                    this.bWidth = insets.getSystemWindowInsetRight();
                    this.bHeight = heightSize2;
                } else if (insets.getSystemWindowInsetLeft() != 0) {
                    this.bWidth = insets.getSystemWindowInsetLeft();
                    this.bHeight = heightSize2;
                } else {
                    this.bWidth = widthSize;
                    this.bHeight = insets.getSystemWindowInsetBottom();
                }
                heightSize = heightSize2 - insets.getSystemWindowInsetTop();
            }
            ArticleViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(heightSize, 1073741824));
            ArticleViewer.this.photoContainerView.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(heightSize, 1073741824));
            ArticleViewer.this.photoContainerBackground.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(heightSize, 1073741824));
            ArticleViewer.this.fullscreenVideoContainer.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(heightSize, 1073741824));
            ViewGroup.LayoutParams layoutParams = ArticleViewer.this.animatingImageView.getLayoutParams();
            ArticleViewer.this.animatingImageView.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int x;
            if (!this.selfLayout) {
                int width = right - left;
                if (ArticleViewer.this.anchorsOffsetMeasuredWidth != width) {
                    for (int i = 0; i < ArticleViewer.this.listView.length; i++) {
                        for (Map.Entry<String, Integer> entry : ArticleViewer.this.adapter[i].anchorsOffset.entrySet()) {
                            entry.setValue(-1);
                        }
                    }
                    int unused = ArticleViewer.this.anchorsOffsetMeasuredWidth = width;
                }
                int y = 0;
                if (Build.VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
                    x = 0;
                } else {
                    WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                    x = insets.getSystemWindowInsetLeft();
                    if (insets.getSystemWindowInsetRight() != 0) {
                        this.bX = width - this.bWidth;
                        this.bY = 0;
                    } else if (insets.getSystemWindowInsetLeft() != 0) {
                        this.bX = 0;
                        this.bY = 0;
                    } else {
                        this.bX = 0;
                        this.bY = (bottom - top) - this.bHeight;
                    }
                    if (Build.VERSION.SDK_INT >= 28) {
                        y = 0 + insets.getSystemWindowInsetTop();
                    }
                }
                ArticleViewer.this.containerView.layout(x, y, ArticleViewer.this.containerView.getMeasuredWidth() + x, ArticleViewer.this.containerView.getMeasuredHeight() + y);
                ArticleViewer.this.photoContainerView.layout(x, y, ArticleViewer.this.photoContainerView.getMeasuredWidth() + x, ArticleViewer.this.photoContainerView.getMeasuredHeight() + y);
                ArticleViewer.this.photoContainerBackground.layout(x, y, ArticleViewer.this.photoContainerBackground.getMeasuredWidth() + x, ArticleViewer.this.photoContainerBackground.getMeasuredHeight() + y);
                ArticleViewer.this.fullscreenVideoContainer.layout(x, y, ArticleViewer.this.fullscreenVideoContainer.getMeasuredWidth() + x, ArticleViewer.this.fullscreenVideoContainer.getMeasuredHeight() + y);
                ArticleViewer.this.animatingImageView.layout(0, 0, ArticleViewer.this.animatingImageView.getMeasuredWidth(), ArticleViewer.this.animatingImageView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            boolean unused = ArticleViewer.this.attachedToWindow = true;
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            boolean unused = ArticleViewer.this.attachedToWindow = false;
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            handleTouchEvent((MotionEvent) null);
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(ev) || super.onInterceptTouchEvent(ev));
        }

        public boolean onTouchEvent(MotionEvent event) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(event) || super.onTouchEvent(event));
        }

        public void setInnerTranslationX(float value) {
            this.innerTranslationX = value;
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent((ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true);
            }
            invalidate();
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            float opacity;
            Canvas canvas2 = canvas;
            int width = getMeasuredWidth();
            int translationX = (int) this.innerTranslationX;
            int restoreCount = canvas.save();
            canvas2.clipRect(translationX, 0, width, getHeight());
            boolean result = super.drawChild(canvas, child, drawingTime);
            canvas2.restoreToCount(restoreCount);
            if (translationX == 0) {
                View view = child;
            } else if (child == ArticleViewer.this.containerView) {
                float opacity2 = Math.min(0.8f, ((float) (width - translationX)) / ((float) width));
                if (opacity2 < 0.0f) {
                    opacity = 0.0f;
                } else {
                    opacity = opacity2;
                }
                ArticleViewer.this.scrimPaint.setColor(((int) (153.0f * opacity)) << 24);
                canvas.drawRect(0.0f, 0.0f, (float) translationX, (float) getHeight(), ArticleViewer.this.scrimPaint);
                float alpha2 = Math.max(0.0f, Math.min(((float) (width - translationX)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                ArticleViewer.this.layerShadowDrawable.setBounds(translationX - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                ArticleViewer.this.layerShadowDrawable.setAlpha((int) (255.0f * alpha2));
                ArticleViewer.this.layerShadowDrawable.draw(canvas2);
            }
            return result;
        }

        public float getInnerTranslationX() {
            return this.innerTranslationX;
        }

        private void prepareForMoving(MotionEvent ev) {
            this.maybeStartTracking = false;
            this.startedTracking = true;
            this.startedTrackingX = (int) ev.getX();
            if (ArticleViewer.this.pagesStack.size() > 1) {
                this.movingPage = true;
                this.startMovingHeaderHeight = ArticleViewer.this.currentHeaderHeight;
                ArticleViewer.this.listView[1].setVisibility(0);
                ArticleViewer.this.listView[1].setAlpha(1.0f);
                ArticleViewer.this.listView[1].setTranslationX(0.0f);
                ArticleViewer.this.listView[0].setBackgroundColor(ArticleViewer.this.backgroundPaint.getColor());
            } else {
                this.movingPage = false;
            }
            ArticleViewer.this.cancelCheckLongPress();
        }

        public boolean handleTouchEvent(MotionEvent event) {
            float distToMove;
            MotionEvent motionEvent = event;
            if (ArticleViewer.this.isPhotoVisible || this.closeAnimationInProgress || ArticleViewer.this.fullscreenVideoContainer.getVisibility() == 0) {
                return false;
            }
            if (motionEvent != null && event.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) event.getX();
                this.startedTrackingY = (int) event.getY();
                VelocityTracker velocityTracker = this.tracker;
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            } else if (motionEvent != null && event.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                int dx = Math.max(0, (int) (event.getX() - ((float) this.startedTrackingX)));
                int dy = Math.abs(((int) event.getY()) - this.startedTrackingY);
                this.tracker.addMovement(motionEvent);
                if (this.maybeStartTracking && !this.startedTracking && ((float) dx) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                    prepareForMoving(event);
                } else if (this.startedTracking) {
                    DrawingText unused = ArticleViewer.this.pressedLinkOwnerLayout = null;
                    View unused2 = ArticleViewer.this.pressedLinkOwnerView = null;
                    if (this.movingPage) {
                        ArticleViewer.this.listView[0].setTranslationX((float) dx);
                    } else {
                        ArticleViewer.this.containerView.setTranslationX((float) dx);
                        setInnerTranslationX((float) dx);
                    }
                }
            } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (event.getAction() == 3 || event.getAction() == 1 || event.getAction() == 6)) {
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                this.tracker.computeCurrentVelocity(1000);
                float velX = this.tracker.getXVelocity();
                float velY = this.tracker.getYVelocity();
                if (!this.startedTracking && velX >= 3500.0f && velX > Math.abs(velY)) {
                    prepareForMoving(event);
                }
                if (this.startedTracking) {
                    View movingView = this.movingPage ? ArticleViewer.this.listView[0] : ArticleViewer.this.containerView;
                    float x = movingView.getX();
                    final boolean backAnimation = x < ((float) movingView.getMeasuredWidth()) / 3.0f && (velX < 3500.0f || velX < velY);
                    AnimatorSet animatorSet = new AnimatorSet();
                    if (!backAnimation) {
                        distToMove = ((float) movingView.getMeasuredWidth()) - x;
                        if (this.movingPage) {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{(float) movingView.getMeasuredWidth()})});
                        } else {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{(float) movingView.getMeasuredWidth()}), ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{(float) movingView.getMeasuredWidth()})});
                        }
                    } else {
                        distToMove = x;
                        if (this.movingPage) {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{0.0f})});
                        } else {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{0.0f})});
                        }
                    }
                    animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) movingView.getMeasuredWidth())) * distToMove), 50));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (WindowView.this.movingPage) {
                                ArticleViewer.this.listView[0].setBackgroundDrawable((Drawable) null);
                                if (!backAnimation) {
                                    WebpageAdapter adapterToUpdate = ArticleViewer.this.adapter[1];
                                    ArticleViewer.this.adapter[1] = ArticleViewer.this.adapter[0];
                                    ArticleViewer.this.adapter[0] = adapterToUpdate;
                                    RecyclerListView listToUpdate = ArticleViewer.this.listView[1];
                                    ArticleViewer.this.listView[1] = ArticleViewer.this.listView[0];
                                    ArticleViewer.this.listView[0] = listToUpdate;
                                    LinearLayoutManager layoutManagerToUpdate = ArticleViewer.this.layoutManager[1];
                                    ArticleViewer.this.layoutManager[1] = ArticleViewer.this.layoutManager[0];
                                    ArticleViewer.this.layoutManager[0] = layoutManagerToUpdate;
                                    ArticleViewer.this.pagesStack.remove(ArticleViewer.this.pagesStack.size() - 1);
                                    TLRPC.WebPage unused = ArticleViewer.this.currentPage = (TLRPC.WebPage) ArticleViewer.this.pagesStack.get(ArticleViewer.this.pagesStack.size() - 1);
                                }
                                ArticleViewer.this.listView[1].setVisibility(8);
                                ArticleViewer.this.headerView.invalidate();
                            } else if (!backAnimation) {
                                ArticleViewer.this.saveCurrentPagePosition();
                                ArticleViewer.this.onClosed();
                            }
                            boolean unused2 = WindowView.this.movingPage = false;
                            boolean unused3 = WindowView.this.startedTracking = false;
                            boolean unused4 = WindowView.this.closeAnimationInProgress = false;
                        }
                    });
                    animatorSet.start();
                    this.closeAnimationInProgress = true;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    this.movingPage = false;
                }
                VelocityTracker velocityTracker2 = this.tracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.tracker = null;
                }
            } else if (motionEvent == null) {
                this.maybeStartTracking = false;
                this.startedTracking = false;
                this.movingPage = false;
                VelocityTracker velocityTracker3 = this.tracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.recycle();
                    this.tracker = null;
                }
            }
            return this.startedTracking;
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            int i;
            int i2;
            super.dispatchDraw(canvas);
            int i3 = this.bWidth;
            if (i3 != 0 && (i = this.bHeight) != 0) {
                int i4 = this.bX;
                if (i4 == 0 && (i2 = this.bY) == 0) {
                    canvas.drawRect((float) i4, (float) i2, (float) (i4 + i3), (float) (i2 + i), ArticleViewer.this.blackPaint);
                    return;
                }
                canvas.drawRect(((float) this.bX) - getTranslationX(), (float) this.bY, ((float) (this.bX + this.bWidth)) - getTranslationX(), (float) (this.bY + this.bHeight), ArticleViewer.this.blackPaint);
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawRect(this.innerTranslationX, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), ArticleViewer.this.backgroundPaint);
            if (Build.VERSION.SDK_INT >= 21 && ArticleViewer.this.hasCutout && ArticleViewer.this.lastInsets != null) {
                canvas.drawRect(this.innerTranslationX, 0.0f, (float) getMeasuredWidth(), (float) ((WindowInsets) ArticleViewer.this.lastInsets).getSystemWindowInsetBottom(), ArticleViewer.this.statusBarPaint);
            }
        }

        public void setAlpha(float value) {
            ArticleViewer.this.backgroundPaint.setAlpha((int) (value * 255.0f));
            ArticleViewer.this.statusBarPaint.setAlpha((int) (255.0f * value));
            this.alpha = value;
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent((ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true);
            }
            invalidate();
        }

        public float getAlpha() {
            return this.alpha;
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (ArticleViewer.this.checkingForLongPress && ArticleViewer.this.windowView != null) {
                boolean unused = ArticleViewer.this.checkingForLongPress = false;
                if (ArticleViewer.this.pressedLink != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0);
                    ArticleViewer articleViewer = ArticleViewer.this;
                    articleViewer.showCopyPopup(articleViewer.pressedLink.getUrl());
                    TextPaintUrlSpan unused2 = ArticleViewer.this.pressedLink = null;
                    DrawingText unused3 = ArticleViewer.this.pressedLinkOwnerLayout = null;
                    if (ArticleViewer.this.pressedLinkOwnerView != null) {
                        ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    }
                } else if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLinkOwnerView != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0);
                    int[] location = new int[2];
                    ArticleViewer.this.pressedLinkOwnerView.getLocationInWindow(location);
                    int y = (location[1] + ArticleViewer.this.pressedLayoutY) - AndroidUtilities.dp(54.0f);
                    if (y < 0) {
                        y = 0;
                    }
                    ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    boolean unused4 = ArticleViewer.this.drawBlockSelection = true;
                    ArticleViewer articleViewer2 = ArticleViewer.this;
                    articleViewer2.showPopup(articleViewer2.pressedLinkOwnerView, 48, 0, y);
                    ArticleViewer.this.listView[0].setLayoutFrozen(true);
                    ArticleViewer.this.listView[0].setLayoutFrozen(false);
                }
            }
        }
    }

    private void createPaint(boolean update) {
        if (quoteLinePaint == null) {
            quoteLinePaint = new Paint();
            preformattedBackgroundPaint = new Paint();
            Paint paint = new Paint(1);
            tableLinePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            tableLinePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
            Paint paint2 = new Paint();
            tableHalfLinePaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            tableHalfLinePaint.setStrokeWidth(((float) AndroidUtilities.dp(1.0f)) / 2.0f);
            tableHeaderPaint = new Paint();
            tableStripPaint = new Paint();
            urlPaint = new Paint();
            webpageUrlPaint = new Paint(1);
            photoBackgroundPaint = new Paint();
            dividerPaint = new Paint();
            webpageMarkPaint = new Paint(1);
        } else if (!update) {
            return;
        }
        int color = getSelectedColor();
        if (color == 0) {
            preformattedBackgroundPaint.setColor(-657156);
            webpageUrlPaint.setColor(-1313798);
            urlPaint.setColor(-2299145);
            tableHalfLinePaint.setColor(-2039584);
            tableLinePaint.setColor(-2039584);
            tableHeaderPaint.setColor(-723724);
            tableStripPaint.setColor(-526345);
            photoBackgroundPaint.setColor(-723724);
            dividerPaint.setColor(-3288619);
            webpageMarkPaint.setColor(-68676);
        } else if (color == 1) {
            preformattedBackgroundPaint.setColor(-1712440);
            webpageUrlPaint.setColor(-2365721);
            urlPaint.setColor(-3481882);
            tableHalfLinePaint.setColor(-3620432);
            tableLinePaint.setColor(-3620432);
            tableHeaderPaint.setColor(-1120560);
            tableStripPaint.setColor(-1120560);
            photoBackgroundPaint.setColor(-1120560);
            dividerPaint.setColor(-4080987);
            webpageMarkPaint.setColor(-1712691);
        } else if (color == 2) {
            preformattedBackgroundPaint.setColor(-15000805);
            webpageUrlPaint.setColor(-14536904);
            urlPaint.setColor(-14469050);
            tableHalfLinePaint.setColor(-13750738);
            tableLinePaint.setColor(-13750738);
            tableHeaderPaint.setColor(-15066598);
            tableStripPaint.setColor(-15066598);
            photoBackgroundPaint.setColor(-14935012);
            dividerPaint.setColor(-12303292);
            webpageMarkPaint.setColor(-14408668);
        }
        quoteLinePaint.setColor(getTextColor());
    }

    /* access modifiers changed from: private */
    public void showCopyPopup(String urlFinal) {
        if (this.parentActivity != null) {
            BottomSheet bottomSheet = this.linkSheet;
            if (bottomSheet != null) {
                bottomSheet.dismiss();
                this.linkSheet = null;
            }
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
            builder.setUseFullscreen(true);
            builder.setTitle(urlFinal);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener(urlFinal) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ArticleViewer.this.lambda$showCopyPopup$0$ArticleViewer(this.f$1, dialogInterface, i);
                }
            });
            BottomSheet sheet = builder.create();
            showDialog(sheet);
            for (int a = 0; a < 2; a++) {
                sheet.setItemColor(a, getTextColor(), getTextColor());
            }
            sheet.setTitleColor(getGrayTextColor());
            int i = this.selectedColor;
            if (i == 0) {
                sheet.setBackgroundColor(-1);
            } else if (i == 1) {
                sheet.setBackgroundColor(-659492);
            } else if (i == 2) {
                sheet.setBackgroundColor(-15461356);
            }
        }
    }

    public /* synthetic */ void lambda$showCopyPopup$0$ArticleViewer(String urlFinal, DialogInterface dialog, int which) {
        String webPageUrl;
        String anchor;
        if (this.parentActivity != null) {
            if (which == 0) {
                int lastIndexOf = urlFinal.lastIndexOf(35);
                int index = lastIndexOf;
                if (lastIndexOf != -1) {
                    if (!TextUtils.isEmpty(this.currentPage.cached_page.url)) {
                        webPageUrl = this.currentPage.cached_page.url.toLowerCase();
                    } else {
                        webPageUrl = this.currentPage.url.toLowerCase();
                    }
                    try {
                        anchor = URLDecoder.decode(urlFinal.substring(index + 1), "UTF-8");
                    } catch (Exception e) {
                        anchor = "";
                    }
                    if (urlFinal.toLowerCase().contains(webPageUrl)) {
                        if (TextUtils.isEmpty(anchor)) {
                            this.layoutManager[0].scrollToPositionWithOffset(0, 0);
                            checkScrollAnimated();
                            return;
                        }
                        scrollToAnchor(anchor);
                        return;
                    }
                }
                Browser.openUrl((Context) this.parentActivity, urlFinal);
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
    }

    /* access modifiers changed from: private */
    public void showPopup(View parent, int gravity, int x, int y) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
                this.popupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = this.popupLayout;
                Drawable drawable = this.parentActivity.getResources().getDrawable(R.drawable.menu_copy);
                this.copyBackgroundDrawable = drawable;
                actionBarPopupWindowLayout2.setBackgroundDrawable(drawable);
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new View.OnTouchListener() {
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return ArticleViewer.this.lambda$showPopup$1$ArticleViewer(view, motionEvent);
                    }
                });
                this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        ArticleViewer.this.lambda$showPopup$2$ArticleViewer(keyEvent);
                    }
                });
                this.popupLayout.setShowedFromBotton(false);
                TextView textView = new TextView(this.parentActivity);
                this.deleteView = textView;
                textView.setBackgroundDrawable(Theme.createSelectorDrawable(251658240, 2));
                this.deleteView.setGravity(16);
                this.deleteView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                this.deleteView.setTextSize(1, 15.0f);
                this.deleteView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.deleteView.setText(LocaleController.getString("Copy", R.string.Copy).toUpperCase());
                this.deleteView.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ArticleViewer.this.lambda$showPopup$3$ArticleViewer(view);
                    }
                });
                this.popupLayout.addView(this.deleteView, LayoutHelper.createFrame(-2, 48.0f));
                ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow = actionBarPopupWindow2;
                actionBarPopupWindow2.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(R.style.PopupContextAnimation);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    public final void onDismiss() {
                        ArticleViewer.this.lambda$showPopup$4$ArticleViewer();
                    }
                });
            }
            if (this.selectedColor == 2) {
                this.deleteView.setTextColor(-5723992);
                Drawable drawable2 = this.copyBackgroundDrawable;
                if (drawable2 != null) {
                    drawable2.setColorFilter(new PorterDuffColorFilter(-14408668, PorterDuff.Mode.MULTIPLY));
                }
            } else {
                this.deleteView.setTextColor(-14606047);
                Drawable drawable3 = this.copyBackgroundDrawable;
                if (drawable3 != null) {
                    drawable3.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
                }
            }
            this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(parent, gravity, x, y);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    public /* synthetic */ boolean lambda$showPopup$1$ArticleViewer(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        v.getHitRect(this.popupRect);
        if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    public /* synthetic */ void lambda$showPopup$2$ArticleViewer(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$showPopup$3$ArticleViewer(View v) {
        DrawingText drawingText = this.pressedLinkOwnerLayout;
        if (drawingText != null) {
            AndroidUtilities.addToClipboard(drawingText.getText());
            ToastUtils.show((int) R.string.TextCopied);
        }
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    public /* synthetic */ void lambda$showPopup$4$ArticleViewer() {
        View view = this.pressedLinkOwnerView;
        if (view != null) {
            this.pressedLinkOwnerLayout = null;
            view.invalidate();
            this.pressedLinkOwnerView = null;
        }
    }

    private TLRPC.RichText getBlockCaption(TLRPC.PageBlock block, int type) {
        if (type == 2) {
            TLRPC.RichText text1 = getBlockCaption(block, 0);
            if (text1 instanceof TLRPC.TL_textEmpty) {
                text1 = null;
            }
            TLRPC.RichText text2 = getBlockCaption(block, 1);
            if (text2 instanceof TLRPC.TL_textEmpty) {
                text2 = null;
            }
            if (text1 != null && text2 == null) {
                return text1;
            }
            if (text1 == null && text2 != null) {
                return text2;
            }
            if (text1 == null || text2 == null) {
                return null;
            }
            TLRPC.TL_textPlain text3 = new TLRPC.TL_textPlain();
            text3.text = " ";
            TLRPC.TL_textConcat textConcat = new TLRPC.TL_textConcat();
            textConcat.texts.add(text1);
            textConcat.texts.add(text3);
            textConcat.texts.add(text2);
            return textConcat;
        }
        if (block instanceof TLRPC.TL_pageBlockEmbedPost) {
            TLRPC.TL_pageBlockEmbedPost blockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) block;
            if (type == 0) {
                return blockEmbedPost.caption.text;
            }
            if (type == 1) {
                return blockEmbedPost.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockSlideshow) {
            TLRPC.TL_pageBlockSlideshow pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) block;
            if (type == 0) {
                return pageBlockSlideshow.caption.text;
            }
            if (type == 1) {
                return pageBlockSlideshow.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
            if (type == 0) {
                return pageBlockPhoto.caption.text;
            }
            if (type == 1) {
                return pageBlockPhoto.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockCollage) {
            TLRPC.TL_pageBlockCollage pageBlockCollage = (TLRPC.TL_pageBlockCollage) block;
            if (type == 0) {
                return pageBlockCollage.caption.text;
            }
            if (type == 1) {
                return pageBlockCollage.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockEmbed) {
            TLRPC.TL_pageBlockEmbed pageBlockEmbed = (TLRPC.TL_pageBlockEmbed) block;
            if (type == 0) {
                return pageBlockEmbed.caption.text;
            }
            if (type == 1) {
                return pageBlockEmbed.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockBlockquote) {
            return ((TLRPC.TL_pageBlockBlockquote) block).caption;
        } else {
            if (block instanceof TLRPC.TL_pageBlockVideo) {
                TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                if (type == 0) {
                    return pageBlockVideo.caption.text;
                }
                if (type == 1) {
                    return pageBlockVideo.caption.credit;
                }
            } else if (block instanceof TLRPC.TL_pageBlockPullquote) {
                return ((TLRPC.TL_pageBlockPullquote) block).caption;
            } else {
                if (block instanceof TLRPC.TL_pageBlockAudio) {
                    TLRPC.TL_pageBlockAudio pageBlockAudio = (TLRPC.TL_pageBlockAudio) block;
                    if (type == 0) {
                        return pageBlockAudio.caption.text;
                    }
                    if (type == 1) {
                        return pageBlockAudio.caption.credit;
                    }
                } else if (block instanceof TLRPC.TL_pageBlockCover) {
                    return getBlockCaption(((TLRPC.TL_pageBlockCover) block).cover, type);
                } else {
                    if (block instanceof TLRPC.TL_pageBlockMap) {
                        TLRPC.TL_pageBlockMap pageBlockMap = (TLRPC.TL_pageBlockMap) block;
                        if (type == 0) {
                            return pageBlockMap.caption.text;
                        }
                        if (type == 1) {
                            return pageBlockMap.caption.credit;
                        }
                    }
                }
            }
        }
        return null;
    }

    private View getLastNonListCell(View view) {
        if (view instanceof BlockListItemCell) {
            BlockListItemCell cell = (BlockListItemCell) view;
            if (cell.blockLayout != null) {
                return getLastNonListCell(cell.blockLayout.itemView);
            }
        } else if (view instanceof BlockOrderedListItemCell) {
            BlockOrderedListItemCell cell2 = (BlockOrderedListItemCell) view;
            if (cell2.blockLayout != null) {
                return getLastNonListCell(cell2.blockLayout.itemView);
            }
        }
        return view;
    }

    /* access modifiers changed from: private */
    public boolean isListItemBlock(TLRPC.PageBlock block) {
        return (block instanceof TL_pageBlockListItem) || (block instanceof TL_pageBlockOrderedListItem);
    }

    /* access modifiers changed from: private */
    public TLRPC.PageBlock getLastNonListPageBlock(TLRPC.PageBlock block) {
        if (block instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem blockListItem = (TL_pageBlockListItem) block;
            if (blockListItem.blockItem != null) {
                return getLastNonListPageBlock(blockListItem.blockItem);
            }
            return blockListItem.blockItem;
        } else if (!(block instanceof TL_pageBlockOrderedListItem)) {
            return block;
        } else {
            TL_pageBlockOrderedListItem blockListItem2 = (TL_pageBlockOrderedListItem) block;
            if (blockListItem2.blockItem != null) {
                return getLastNonListPageBlock(blockListItem2.blockItem);
            }
            return blockListItem2.blockItem;
        }
    }

    private boolean openAllParentBlocks(TL_pageBlockDetailsChild child) {
        TLRPC.PageBlock parentBlock = getLastNonListPageBlock(child.parent);
        if (parentBlock instanceof TLRPC.TL_pageBlockDetails) {
            TLRPC.TL_pageBlockDetails blockDetails = (TLRPC.TL_pageBlockDetails) parentBlock;
            if (blockDetails.open) {
                return false;
            }
            blockDetails.open = true;
            return true;
        } else if (!(parentBlock instanceof TL_pageBlockDetailsChild)) {
            return false;
        } else {
            TL_pageBlockDetailsChild parent = (TL_pageBlockDetailsChild) parentBlock;
            TLRPC.PageBlock parentBlock2 = getLastNonListPageBlock(parent.block);
            boolean opened = false;
            if (parentBlock2 instanceof TLRPC.TL_pageBlockDetails) {
                TLRPC.TL_pageBlockDetails blockDetails2 = (TLRPC.TL_pageBlockDetails) parentBlock2;
                if (!blockDetails2.open) {
                    blockDetails2.open = true;
                    opened = true;
                }
            }
            if (openAllParentBlocks(parent) || opened) {
                return true;
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public TLRPC.PageBlock fixListBlock(TLRPC.PageBlock parentBlock, TLRPC.PageBlock childBlock) {
        if (parentBlock instanceof TL_pageBlockListItem) {
            TLRPC.PageBlock unused = ((TL_pageBlockListItem) parentBlock).blockItem = childBlock;
            return parentBlock;
        } else if (!(parentBlock instanceof TL_pageBlockOrderedListItem)) {
            return childBlock;
        } else {
            TLRPC.PageBlock unused2 = ((TL_pageBlockOrderedListItem) parentBlock).blockItem = childBlock;
            return parentBlock;
        }
    }

    /* access modifiers changed from: private */
    public TLRPC.PageBlock wrapInTableBlock(TLRPC.PageBlock parentBlock, TLRPC.PageBlock childBlock) {
        if (parentBlock instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem parent = (TL_pageBlockListItem) parentBlock;
            TL_pageBlockListItem item = new TL_pageBlockListItem();
            TL_pageBlockListParent unused = item.parent = parent.parent;
            TLRPC.PageBlock unused2 = item.blockItem = wrapInTableBlock(parent.blockItem, childBlock);
            return item;
        } else if (!(parentBlock instanceof TL_pageBlockOrderedListItem)) {
            return childBlock;
        } else {
            TL_pageBlockOrderedListItem parent2 = (TL_pageBlockOrderedListItem) parentBlock;
            TL_pageBlockOrderedListItem item2 = new TL_pageBlockOrderedListItem();
            TL_pageBlockOrderedListParent unused3 = item2.parent = parent2.parent;
            TLRPC.PageBlock unused4 = item2.blockItem = wrapInTableBlock(parent2.blockItem, childBlock);
            return item2;
        }
    }

    private void updateInterfaceForCurrentPage(int order) {
        int offset;
        int i = order;
        TLRPC.WebPage webPage = this.currentPage;
        if (webPage != null && webPage.cached_page != null) {
            this.isRtl = this.currentPage.cached_page.rtl;
            this.channelBlock = null;
            this.titleTextView.setText(this.currentPage.site_name == null ? "" : this.currentPage.site_name);
            if (i != 0) {
                WebpageAdapter[] webpageAdapterArr = this.adapter;
                WebpageAdapter adapterToUpdate = webpageAdapterArr[1];
                webpageAdapterArr[1] = webpageAdapterArr[0];
                webpageAdapterArr[0] = adapterToUpdate;
                RecyclerListView[] recyclerListViewArr = this.listView;
                RecyclerListView listToUpdate = recyclerListViewArr[1];
                recyclerListViewArr[1] = recyclerListViewArr[0];
                recyclerListViewArr[0] = listToUpdate;
                LinearLayoutManager[] linearLayoutManagerArr = this.layoutManager;
                LinearLayoutManager layoutManagerToUpdate = linearLayoutManagerArr[1];
                linearLayoutManagerArr[1] = linearLayoutManagerArr[0];
                linearLayoutManagerArr[0] = layoutManagerToUpdate;
                int index1 = this.containerView.indexOfChild(recyclerListViewArr[0]);
                int index2 = this.containerView.indexOfChild(this.listView[1]);
                if (i == 1) {
                    if (index1 < index2) {
                        this.containerView.removeView(this.listView[0]);
                        this.containerView.addView(this.listView[0], index2);
                    }
                } else if (index2 < index1) {
                    this.containerView.removeView(this.listView[0]);
                    this.containerView.addView(this.listView[0], index1);
                }
                this.pageSwitchAnimation = new AnimatorSet();
                this.listView[0].setVisibility(0);
                final int index = i == 1 ? 0 : 1;
                this.listView[index].setBackgroundColor(this.backgroundPaint.getColor());
                if (Build.VERSION.SDK_INT >= 18) {
                    this.listView[index].setLayerType(2, (Paint) null);
                }
                if (i == 1) {
                    this.pageSwitchAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(56.0f), 0.0f}), ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, new float[]{0.0f, 1.0f})});
                    int i2 = index1;
                } else if (i == -1) {
                    this.listView[0].setAlpha(1.0f);
                    this.listView[0].setTranslationX(0.0f);
                    int i3 = index1;
                    this.pageSwitchAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.listView[1], View.TRANSLATION_X, new float[]{0.0f, (float) AndroidUtilities.dp(56.0f)}), ObjectAnimator.ofFloat(this.listView[1], View.ALPHA, new float[]{1.0f, 0.0f})});
                }
                this.pageSwitchAnimation.setDuration(150);
                this.pageSwitchAnimation.setInterpolator(this.interpolator);
                this.pageSwitchAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ArticleViewer.this.listView[1].setVisibility(8);
                        ArticleViewer.this.listView[index].setBackgroundDrawable((Drawable) null);
                        if (Build.VERSION.SDK_INT >= 18) {
                            ArticleViewer.this.listView[index].setLayerType(0, (Paint) null);
                        }
                        AnimatorSet unused = ArticleViewer.this.pageSwitchAnimation = null;
                    }
                });
                this.pageSwitchAnimation.start();
            }
            this.headerView.invalidate();
            this.adapter[0].cleanup();
            int count = this.currentPage.cached_page.blocks.size();
            int a = 0;
            while (a < count) {
                TLRPC.PageBlock block = this.currentPage.cached_page.blocks.get(a);
                if (a == 0) {
                    block.first = true;
                    if (block instanceof TLRPC.TL_pageBlockCover) {
                        TLRPC.TL_pageBlockCover pageBlockCover = (TLRPC.TL_pageBlockCover) block;
                        TLRPC.RichText caption = getBlockCaption(pageBlockCover, 0);
                        TLRPC.RichText credit = getBlockCaption(pageBlockCover, 1);
                        if (((caption != null && !(caption instanceof TLRPC.TL_textEmpty)) || (credit != null && !(credit instanceof TLRPC.TL_textEmpty))) && count > 1) {
                            TLRPC.PageBlock next = this.currentPage.cached_page.blocks.get(1);
                            if (next instanceof TLRPC.TL_pageBlockChannel) {
                                this.channelBlock = (TLRPC.TL_pageBlockChannel) next;
                            }
                        }
                    }
                } else if (a == 1 && this.channelBlock != null) {
                    a++;
                }
                this.adapter[0].addBlock(block, 0, 0, a == count + -1 ? a : 0);
                a++;
            }
            this.adapter[0].notifyDataSetChanged();
            if (this.pagesStack.size() == 1 || i == -1) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
                String key = "article" + this.currentPage.id;
                int position = preferences.getInt(key, -1);
                boolean z = true;
                boolean z2 = preferences.getBoolean(key + "r", true);
                if (AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
                    z = false;
                }
                if (z2 == z) {
                    offset = preferences.getInt(key + "o", 0) - this.listView[0].getPaddingTop();
                } else {
                    offset = AndroidUtilities.dp(10.0f);
                }
                if (position != -1) {
                    this.layoutManager[0].scrollToPositionWithOffset(position, offset);
                }
            } else {
                this.layoutManager[0].scrollToPositionWithOffset(0, 0);
            }
            checkScrollAnimated();
        }
    }

    private boolean addPageToStack(TLRPC.WebPage webPage, String anchor, int order) {
        saveCurrentPagePosition();
        this.currentPage = webPage;
        this.pagesStack.add(webPage);
        updateInterfaceForCurrentPage(order);
        return scrollToAnchor(anchor);
    }

    private boolean scrollToAnchor(String anchor) {
        if (TextUtils.isEmpty(anchor)) {
            return false;
        }
        String anchor2 = anchor.toLowerCase();
        Integer row = (Integer) this.adapter[0].anchors.get(anchor2);
        if (row == null) {
            return false;
        }
        TLRPC.TL_textAnchor textAnchor = (TLRPC.TL_textAnchor) this.adapter[0].anchorsParent.get(anchor2);
        if (textAnchor != null) {
            TLRPC.TL_pageBlockParagraph paragraph = new TLRPC.TL_pageBlockParagraph();
            paragraph.text = textAnchor.text;
            int type = this.adapter[0].getTypeForBlock(paragraph);
            RecyclerView.ViewHolder holder = this.adapter[0].onCreateViewHolder((ViewGroup) null, type);
            this.adapter[0].bindBlockToHolder(type, holder, paragraph, 0, 0);
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
            builder.setUseFullscreen(true);
            builder.setApplyTopPadding(false);
            LinearLayout linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(1);
            TextView textView = new TextView(this.parentActivity) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                    super.onDraw(canvas);
                }
            };
            textView.setTextSize(1, 16.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("InstantViewReference", R.string.InstantViewReference));
            textView.setGravity((this.isRtl ? 5 : 3) | 16);
            textView.setTextColor(getTextColor());
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            linearLayout.addView(textView, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f) + 1));
            linearLayout.addView(holder.itemView, LayoutHelper.createLinear(-1, -2, 0.0f, 7.0f, 0.0f, 0.0f));
            builder.setCustomView(linearLayout);
            BottomSheet create = builder.create();
            this.linkSheet = create;
            int i = this.selectedColor;
            if (i == 0) {
                create.setBackgroundColor(-1);
            } else if (i == 1) {
                create.setBackgroundColor(-659492);
            } else if (i == 2) {
                create.setBackgroundColor(-15461356);
            }
            showDialog(this.linkSheet);
            return true;
        } else if (row.intValue() < 0 || row.intValue() >= this.adapter[0].blocks.size()) {
            return false;
        } else {
            TLRPC.PageBlock originalBlock = (TLRPC.PageBlock) this.adapter[0].blocks.get(row.intValue());
            TLRPC.PageBlock block = getLastNonListPageBlock(originalBlock);
            if ((block instanceof TL_pageBlockDetailsChild) && openAllParentBlocks((TL_pageBlockDetailsChild) block)) {
                this.adapter[0].updateRows();
                this.adapter[0].notifyDataSetChanged();
            }
            int position = this.adapter[0].localBlocks.indexOf(originalBlock);
            if (position != -1) {
                row = Integer.valueOf(position);
            }
            Integer offset = (Integer) this.adapter[0].anchorsOffset.get(anchor2);
            if (offset == null) {
                offset = 0;
            } else if (offset.intValue() == -1) {
                int type2 = this.adapter[0].getTypeForBlock(originalBlock);
                RecyclerView.ViewHolder holder2 = this.adapter[0].onCreateViewHolder((ViewGroup) null, type2);
                int i2 = type2;
                int i3 = position;
                this.adapter[0].bindBlockToHolder(type2, holder2, originalBlock, 0, 0);
                holder2.itemView.measure(View.MeasureSpec.makeMeasureSpec(this.listView[0].getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
                Integer offset2 = (Integer) this.adapter[0].anchorsOffset.get(anchor2);
                if (offset2.intValue() == -1) {
                    offset = 0;
                } else {
                    offset = offset2;
                }
            }
            this.layoutManager[0].scrollToPositionWithOffset(row.intValue(), (this.currentHeaderHeight - AndroidUtilities.dp(56.0f)) - offset.intValue());
            return true;
        }
    }

    private boolean removeLastPageFromStack() {
        if (this.pagesStack.size() < 2) {
            return false;
        }
        ArrayList<TLRPC.WebPage> arrayList = this.pagesStack;
        arrayList.remove(arrayList.size() - 1);
        ArrayList<TLRPC.WebPage> arrayList2 = this.pagesStack;
        this.currentPage = arrayList2.get(arrayList2.size() - 1);
        updateInterfaceForCurrentPage(-1);
        return true;
    }

    /* access modifiers changed from: protected */
    public void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            this.windowView.postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            this.windowView.removeCallbacks(checkForLongPress);
            this.pendingCheckForLongPress = null;
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            this.windowView.removeCallbacks(checkForTap);
            this.pendingCheckForTap = null;
        }
    }

    private int getTextFlags(TLRPC.RichText richText) {
        if (richText instanceof TLRPC.TL_textFixed) {
            return getTextFlags(richText.parentRichText) | 4;
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getTextFlags(richText.parentRichText) | 2;
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getTextFlags(richText.parentRichText) | 1;
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getTextFlags(richText.parentRichText) | 16;
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getTextFlags(richText.parentRichText) | 32;
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return getTextFlags(richText.parentRichText) | 8;
        }
        if (richText instanceof TLRPC.TL_textPhone) {
            return getTextFlags(richText.parentRichText) | 8;
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            if (((TLRPC.TL_textUrl) richText).webpage_id != 0) {
                return getTextFlags(richText.parentRichText) | 512;
            }
            return getTextFlags(richText.parentRichText) | 8;
        } else if (richText instanceof TLRPC.TL_textSubscript) {
            return getTextFlags(richText.parentRichText) | 128;
        } else {
            if (richText instanceof TLRPC.TL_textSuperscript) {
                return getTextFlags(richText.parentRichText) | 256;
            }
            if (richText instanceof TLRPC.TL_textMarked) {
                return getTextFlags(richText.parentRichText) | 64;
            }
            if (richText != null) {
                return getTextFlags(richText.parentRichText);
            }
            return 0;
        }
    }

    private TLRPC.RichText getLastRichText(TLRPC.RichText richText) {
        if (richText == null) {
            return null;
        }
        if (richText instanceof TLRPC.TL_textFixed) {
            return getLastRichText(((TLRPC.TL_textFixed) richText).text);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getLastRichText(((TLRPC.TL_textItalic) richText).text);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getLastRichText(((TLRPC.TL_textBold) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getLastRichText(((TLRPC.TL_textUnderline) richText).text);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getLastRichText(((TLRPC.TL_textStrike) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return getLastRichText(((TLRPC.TL_textEmail) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            return getLastRichText(((TLRPC.TL_textUrl) richText).text);
        }
        if (richText instanceof TLRPC.TL_textAnchor) {
            getLastRichText(((TLRPC.TL_textAnchor) richText).text);
        } else if (richText instanceof TLRPC.TL_textSubscript) {
            return getLastRichText(((TLRPC.TL_textSubscript) richText).text);
        } else {
            if (richText instanceof TLRPC.TL_textSuperscript) {
                return getLastRichText(((TLRPC.TL_textSuperscript) richText).text);
            }
            if (richText instanceof TLRPC.TL_textMarked) {
                return getLastRichText(((TLRPC.TL_textMarked) richText).text);
            }
            if (richText instanceof TLRPC.TL_textPhone) {
                return getLastRichText(((TLRPC.TL_textPhone) richText).text);
            }
        }
        return richText;
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    public CharSequence getText(View parentView, TLRPC.RichText parentRichText, TLRPC.RichText richText, TLRPC.PageBlock parentBlock, int maxWidth) {
        MetricAffectingSpan span;
        MetricAffectingSpan span2;
        TLRPC.RichText richText2 = parentRichText;
        TLRPC.RichText richText3 = richText;
        TLRPC.PageBlock pageBlock = parentBlock;
        TextPaint textPaint = null;
        if (richText3 == null) {
            return null;
        }
        if (richText3 instanceof TLRPC.TL_textFixed) {
            return getText(parentView, parentRichText, ((TLRPC.TL_textFixed) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textItalic) {
            return getText(parentView, parentRichText, ((TLRPC.TL_textItalic) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textBold) {
            return getText(parentView, parentRichText, ((TLRPC.TL_textBold) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textUnderline) {
            return getText(parentView, parentRichText, ((TLRPC.TL_textUnderline) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textStrike) {
            return getText(parentView, parentRichText, ((TLRPC.TL_textStrike) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textEmail) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getText(parentView, parentRichText, ((TLRPC.TL_textEmail) richText3).text, parentBlock, maxWidth));
            MetricAffectingSpan[] innerSpans = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
            if (spannableStringBuilder.length() != 0) {
                if (innerSpans == null || innerSpans.length == 0) {
                    textPaint = getTextPaint(richText2, richText3, pageBlock);
                }
                spannableStringBuilder.setSpan(new TextPaintUrlSpan(textPaint, "mailto:" + getUrl(richText)), 0, spannableStringBuilder.length(), 33);
            }
            return spannableStringBuilder;
        } else {
            long j = 0;
            if (richText3 instanceof TLRPC.TL_textUrl) {
                TLRPC.TL_textUrl textUrl = (TLRPC.TL_textUrl) richText3;
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(getText(parentView, parentRichText, ((TLRPC.TL_textUrl) richText3).text, parentBlock, maxWidth));
                MetricAffectingSpan[] innerSpans2 = (MetricAffectingSpan[]) spannableStringBuilder2.getSpans(0, spannableStringBuilder2.length(), MetricAffectingSpan.class);
                TextPaint paint = (innerSpans2 == null || innerSpans2.length == 0) ? getTextPaint(richText2, richText3, pageBlock) : null;
                if (textUrl.webpage_id != 0) {
                    span2 = new TextPaintWebpageUrlSpan(paint, getUrl(richText));
                } else {
                    span2 = new TextPaintUrlSpan(paint, getUrl(richText));
                }
                if (spannableStringBuilder2.length() != 0) {
                    spannableStringBuilder2.setSpan(span2, 0, spannableStringBuilder2.length(), 33);
                }
                return spannableStringBuilder2;
            } else if (richText3 instanceof TLRPC.TL_textPlain) {
                return ((TLRPC.TL_textPlain) richText3).text;
            } else {
                if (richText3 instanceof TLRPC.TL_textAnchor) {
                    TLRPC.TL_textAnchor textAnchor = (TLRPC.TL_textAnchor) richText3;
                    SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(getText(parentView, parentRichText, textAnchor.text, parentBlock, maxWidth));
                    spannableStringBuilder3.setSpan(new AnchorSpan(textAnchor.name), 0, spannableStringBuilder3.length(), 17);
                    return spannableStringBuilder3;
                } else if (richText3 instanceof TLRPC.TL_textEmpty) {
                    return "";
                } else {
                    int i = 1;
                    if (richText3 instanceof TLRPC.TL_textConcat) {
                        SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder();
                        int count = richText3.texts.size();
                        int a = 0;
                        while (a < count) {
                            TLRPC.RichText innerRichText = richText3.texts.get(a);
                            TLRPC.RichText lastRichText = getLastRichText(innerRichText);
                            boolean extraSpace = maxWidth >= 0 && (innerRichText instanceof TLRPC.TL_textUrl) && ((TLRPC.TL_textUrl) innerRichText).webpage_id != j;
                            if (!(!extraSpace || spannableStringBuilder4.length() == 0 || spannableStringBuilder4.charAt(spannableStringBuilder4.length() - i) == 10)) {
                                spannableStringBuilder4.append(" ");
                            }
                            CharSequence charSequence = " ";
                            TLRPC.RichText lastRichText2 = lastRichText;
                            TLRPC.RichText innerRichText2 = innerRichText;
                            int a2 = a;
                            int count2 = count;
                            CharSequence innerText = getText(parentView, parentRichText, innerRichText, parentBlock, maxWidth);
                            int flags = getTextFlags(lastRichText2);
                            int startLength = spannableStringBuilder4.length();
                            spannableStringBuilder4.append(innerText);
                            if (flags != 0 && !(innerText instanceof SpannableStringBuilder)) {
                                if ((flags & 8) != 0 || (flags & 512) != 0) {
                                    String url = getUrl(innerRichText2);
                                    if (url == null) {
                                        url = getUrl(parentRichText);
                                    }
                                    if ((flags & 512) != 0) {
                                        span = new TextPaintWebpageUrlSpan(getTextPaint(richText2, lastRichText2, pageBlock), url);
                                    } else {
                                        span = new TextPaintUrlSpan(getTextPaint(richText2, lastRichText2, pageBlock), url);
                                    }
                                    if (startLength != spannableStringBuilder4.length()) {
                                        spannableStringBuilder4.setSpan(span, startLength, spannableStringBuilder4.length(), 33);
                                    }
                                } else if (startLength != spannableStringBuilder4.length()) {
                                    spannableStringBuilder4.setSpan(new TextPaintSpan(getTextPaint(richText2, lastRichText2, pageBlock)), startLength, spannableStringBuilder4.length(), 33);
                                }
                            }
                            if (extraSpace && a2 != count2 - 1) {
                                spannableStringBuilder4.append(charSequence);
                            }
                            a = a2 + 1;
                            count = count2;
                            i = 1;
                            j = 0;
                        }
                        return spannableStringBuilder4;
                    } else if (richText3 instanceof TLRPC.TL_textSubscript) {
                        return getText(parentView, parentRichText, ((TLRPC.TL_textSubscript) richText3).text, parentBlock, maxWidth);
                    } else if (richText3 instanceof TLRPC.TL_textSuperscript) {
                        return getText(parentView, parentRichText, ((TLRPC.TL_textSuperscript) richText3).text, parentBlock, maxWidth);
                    } else if (richText3 instanceof TLRPC.TL_textMarked) {
                        SpannableStringBuilder spannableStringBuilder5 = new SpannableStringBuilder(getText(parentView, parentRichText, ((TLRPC.TL_textMarked) richText3).text, parentBlock, maxWidth));
                        MetricAffectingSpan[] innerSpans3 = (MetricAffectingSpan[]) spannableStringBuilder5.getSpans(0, spannableStringBuilder5.length(), MetricAffectingSpan.class);
                        if (spannableStringBuilder5.length() != 0) {
                            spannableStringBuilder5.setSpan(new TextPaintMarkSpan((innerSpans3 == null || innerSpans3.length == 0) ? getTextPaint(richText2, richText3, pageBlock) : null), 0, spannableStringBuilder5.length(), 33);
                        }
                        return spannableStringBuilder5;
                    } else if (richText3 instanceof TLRPC.TL_textPhone) {
                        try {
                            SpannableStringBuilder spannableStringBuilder6 = new SpannableStringBuilder(getText(parentView, parentRichText, ((TLRPC.TL_textPhone) richText3).text, parentBlock, maxWidth));
                            MetricAffectingSpan[] innerSpans4 = (MetricAffectingSpan[]) spannableStringBuilder6.getSpans(0, spannableStringBuilder6.length(), MetricAffectingSpan.class);
                            if (spannableStringBuilder6.length() != 0) {
                                spannableStringBuilder6.setSpan(new TextPaintUrlSpan((innerSpans4 == null || innerSpans4.length == 0) ? getTextPaint(richText2, richText3, pageBlock) : null, "tel:" + getUrl(richText)), 0, spannableStringBuilder6.length(), 33);
                            }
                            return spannableStringBuilder6;
                        } catch (Throwable th) {
                            throw th;
                        }
                    } else if (richText3 instanceof TLRPC.TL_textImage) {
                        TLRPC.TL_textImage textImage = (TLRPC.TL_textImage) richText3;
                        TLRPC.Document document = getDocumentWithId(textImage.document_id);
                        if (document == null) {
                            return "";
                        }
                        SpannableStringBuilder spannableStringBuilder7 = new SpannableStringBuilder("*");
                        int w = AndroidUtilities.dp((float) textImage.w);
                        int h = AndroidUtilities.dp((float) textImage.h);
                        int maxWidth2 = Math.abs(maxWidth);
                        if (w > maxWidth2) {
                            float scale2 = ((float) maxWidth2) / ((float) w);
                            w = maxWidth2;
                            h = (int) (((float) h) * scale2);
                        }
                        spannableStringBuilder7.setSpan(new TextPaintImageReceiverSpan(parentView, document, this.currentPage, w, h, false, this.selectedColor == 2), 0, spannableStringBuilder7.length(), 33);
                        return spannableStringBuilder7;
                    } else {
                        return "not supported " + richText3;
                    }
                }
            }
        }
    }

    public static CharSequence getPlainText(TLRPC.RichText richText) {
        if (richText == null) {
            return "";
        }
        if (richText instanceof TLRPC.TL_textFixed) {
            return getPlainText(((TLRPC.TL_textFixed) richText).text);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getPlainText(((TLRPC.TL_textItalic) richText).text);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getPlainText(((TLRPC.TL_textBold) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getPlainText(((TLRPC.TL_textUnderline) richText).text);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getPlainText(((TLRPC.TL_textStrike) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return getPlainText(((TLRPC.TL_textEmail) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            return getPlainText(((TLRPC.TL_textUrl) richText).text);
        }
        if (richText instanceof TLRPC.TL_textPlain) {
            return ((TLRPC.TL_textPlain) richText).text;
        }
        if (richText instanceof TLRPC.TL_textAnchor) {
            return getPlainText(((TLRPC.TL_textAnchor) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmpty) {
            return "";
        }
        if (richText instanceof TLRPC.TL_textConcat) {
            StringBuilder stringBuilder = new StringBuilder();
            int count = richText.texts.size();
            for (int a = 0; a < count; a++) {
                stringBuilder.append(getPlainText(richText.texts.get(a)));
            }
            return stringBuilder;
        } else if ((richText instanceof TLRPC.TL_textSubscript) != 0) {
            return getPlainText(((TLRPC.TL_textSubscript) richText).text);
        } else {
            if (richText instanceof TLRPC.TL_textSuperscript) {
                return getPlainText(((TLRPC.TL_textSuperscript) richText).text);
            }
            if (richText instanceof TLRPC.TL_textMarked) {
                return getPlainText(((TLRPC.TL_textMarked) richText).text);
            }
            if (richText instanceof TLRPC.TL_textPhone) {
                return getPlainText(((TLRPC.TL_textPhone) richText).text);
            }
            return richText instanceof TLRPC.TL_textImage ? "" : "";
        }
    }

    public static String getUrl(TLRPC.RichText richText) {
        if (richText instanceof TLRPC.TL_textFixed) {
            return getUrl(((TLRPC.TL_textFixed) richText).text);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getUrl(((TLRPC.TL_textItalic) richText).text);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getUrl(((TLRPC.TL_textBold) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getUrl(((TLRPC.TL_textUnderline) richText).text);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getUrl(((TLRPC.TL_textStrike) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return ((TLRPC.TL_textEmail) richText).email;
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            return ((TLRPC.TL_textUrl) richText).url;
        }
        if (richText instanceof TLRPC.TL_textPhone) {
            return ((TLRPC.TL_textPhone) richText).phone;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public int getTextColor() {
        int selectedColor2 = getSelectedColor();
        if (selectedColor2 == 0 || selectedColor2 == 1) {
            return -14606047;
        }
        return -6710887;
    }

    private int getInstantLinkBackgroundColor() {
        int selectedColor2 = getSelectedColor();
        if (selectedColor2 == 0) {
            return -1707782;
        }
        if (selectedColor2 != 1) {
            return -14536904;
        }
        return -2498337;
    }

    private int getLinkTextColor() {
        int selectedColor2 = getSelectedColor();
        if (selectedColor2 == 0) {
            return -15435321;
        }
        if (selectedColor2 != 1) {
            return -10838585;
        }
        return -13471296;
    }

    /* access modifiers changed from: private */
    public int getGrayTextColor() {
        int selectedColor2 = getSelectedColor();
        if (selectedColor2 == 0) {
            return -8156010;
        }
        if (selectedColor2 != 1) {
            return -10066330;
        }
        return -11711675;
    }

    private TextPaint getTextPaint(TLRPC.RichText parentRichText, TLRPC.RichText richText, TLRPC.PageBlock parentBlock) {
        int additionalSize;
        SparseArray<TextPaint> currentMap;
        int textSize;
        SparseArray<TextPaint> currentMap2;
        int textSize2;
        SparseArray<TextPaint> currentMap3;
        int textSize3;
        SparseArray<TextPaint> currentMap4;
        int textSize4;
        TLRPC.RichText richText2 = parentRichText;
        TLRPC.RichText richText3 = richText;
        TLRPC.PageBlock pageBlock = parentBlock;
        int flags = getTextFlags(richText3);
        SparseArray<TextPaint> currentMap5 = null;
        int textSize5 = AndroidUtilities.dp(14.0f);
        int textColor = SupportMenu.CATEGORY_MASK;
        int additionalSize2 = this.selectedFontSize;
        if (additionalSize2 == 0) {
            additionalSize = -AndroidUtilities.dp(4.0f);
        } else if (additionalSize2 == 1) {
            additionalSize = -AndroidUtilities.dp(2.0f);
        } else if (additionalSize2 == 3) {
            additionalSize = AndroidUtilities.dp(2.0f);
        } else if (additionalSize2 == 4) {
            additionalSize = AndroidUtilities.dp(4.0f);
        } else {
            additionalSize = 0;
        }
        if (pageBlock instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) pageBlock;
            if (pageBlockPhoto.caption.text == richText3 || pageBlockPhoto.caption.text == richText2) {
                currentMap5 = photoCaptionTextPaints;
                textSize5 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap5 = photoCreditTextPaints;
                textSize5 = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockMap) {
            TLRPC.TL_pageBlockMap pageBlockMap = (TLRPC.TL_pageBlockMap) pageBlock;
            if (pageBlockMap.caption.text == richText3 || pageBlockMap.caption.text == richText2) {
                currentMap4 = photoCaptionTextPaints;
                textSize4 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap4 = photoCreditTextPaints;
                textSize4 = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockTitle) {
            currentMap5 = titleTextPaints;
            textSize5 = AndroidUtilities.dp(24.0f);
            textColor = getTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockKicker) {
            currentMap5 = kickerTextPaints;
            textSize5 = AndroidUtilities.dp(14.0f);
            textColor = getTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockAuthorDate) {
            currentMap5 = authorTextPaints;
            textSize5 = AndroidUtilities.dp(14.0f);
            textColor = getGrayTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockFooter) {
            currentMap5 = footerTextPaints;
            textSize5 = AndroidUtilities.dp(14.0f);
            textColor = getGrayTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockSubtitle) {
            currentMap5 = subtitleTextPaints;
            textSize5 = AndroidUtilities.dp(21.0f);
            textColor = getTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockHeader) {
            currentMap5 = headerTextPaints;
            textSize5 = AndroidUtilities.dp(21.0f);
            textColor = getTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockSubheader) {
            currentMap5 = subheaderTextPaints;
            textSize5 = AndroidUtilities.dp(18.0f);
            textColor = getTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockBlockquote) {
            TLRPC.TL_pageBlockBlockquote pageBlockBlockquote = (TLRPC.TL_pageBlockBlockquote) pageBlock;
            if (pageBlockBlockquote.text == richText2) {
                currentMap5 = quoteTextPaints;
                textSize5 = AndroidUtilities.dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote.caption == richText2) {
                currentMap5 = photoCaptionTextPaints;
                textSize5 = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (pageBlock instanceof TLRPC.TL_pageBlockPullquote) {
            TLRPC.TL_pageBlockPullquote pageBlockBlockquote2 = (TLRPC.TL_pageBlockPullquote) pageBlock;
            if (pageBlockBlockquote2.text == richText2) {
                currentMap5 = quoteTextPaints;
                textSize5 = AndroidUtilities.dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote2.caption == richText2) {
                currentMap5 = photoCaptionTextPaints;
                textSize5 = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (pageBlock instanceof TLRPC.TL_pageBlockPreformatted) {
            currentMap5 = preformattedTextPaints;
            textSize5 = AndroidUtilities.dp(14.0f);
            textColor = getTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockParagraph) {
            currentMap5 = paragraphTextPaints;
            textSize5 = AndroidUtilities.dp(16.0f);
            textColor = getTextColor();
        } else if (isListItemBlock(pageBlock)) {
            currentMap5 = listTextPaints;
            textSize5 = AndroidUtilities.dp(16.0f);
            textColor = getTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockEmbed) {
            TLRPC.TL_pageBlockEmbed pageBlockEmbed = (TLRPC.TL_pageBlockEmbed) pageBlock;
            if (pageBlockEmbed.caption.text == richText3 || pageBlockEmbed.caption.text == richText2) {
                currentMap3 = photoCaptionTextPaints;
                textSize3 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap3 = photoCreditTextPaints;
                textSize3 = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockSlideshow) {
            TLRPC.TL_pageBlockSlideshow pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) pageBlock;
            if (pageBlockSlideshow.caption.text == richText3 || pageBlockSlideshow.caption.text == richText2) {
                currentMap2 = photoCaptionTextPaints;
                textSize2 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap2 = photoCreditTextPaints;
                textSize2 = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockCollage) {
            TLRPC.TL_pageBlockCollage pageBlockCollage = (TLRPC.TL_pageBlockCollage) pageBlock;
            if (pageBlockCollage.caption.text == richText3 || pageBlockCollage.caption.text == richText2) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockEmbedPost) {
            TLRPC.TL_pageBlockEmbedPost pageBlockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) pageBlock;
            if (richText3 == pageBlockEmbedPost.caption.text) {
                currentMap5 = photoCaptionTextPaints;
                textSize5 = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            } else if (richText3 == pageBlockEmbedPost.caption.credit) {
                currentMap5 = photoCreditTextPaints;
                textSize5 = AndroidUtilities.dp(12.0f);
                textColor = getGrayTextColor();
            } else if (richText3 != null) {
                currentMap5 = embedPostTextPaints;
                textSize5 = AndroidUtilities.dp(14.0f);
                textColor = getTextColor();
            }
        } else if (pageBlock instanceof TLRPC.TL_pageBlockVideo) {
            if (richText3 == ((TLRPC.TL_pageBlockVideo) pageBlock).caption.text) {
                currentMap5 = mediaCaptionTextPaints;
                textSize5 = AndroidUtilities.dp(14.0f);
                textColor = getTextColor();
            } else {
                currentMap5 = mediaCreditTextPaints;
                textSize5 = AndroidUtilities.dp(12.0f);
                textColor = getTextColor();
            }
        } else if (pageBlock instanceof TLRPC.TL_pageBlockAudio) {
            if (richText3 == ((TLRPC.TL_pageBlockAudio) pageBlock).caption.text) {
                currentMap5 = mediaCaptionTextPaints;
                textSize5 = AndroidUtilities.dp(14.0f);
                textColor = getTextColor();
            } else {
                currentMap5 = mediaCreditTextPaints;
                textSize5 = AndroidUtilities.dp(12.0f);
                textColor = getTextColor();
            }
        } else if (pageBlock instanceof TLRPC.TL_pageBlockRelatedArticles) {
            currentMap5 = relatedArticleTextPaints;
            textSize5 = AndroidUtilities.dp(15.0f);
            textColor = getGrayTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockDetails) {
            currentMap5 = detailsTextPaints;
            textSize5 = AndroidUtilities.dp(15.0f);
            textColor = getTextColor();
        } else if (pageBlock instanceof TLRPC.TL_pageBlockTable) {
            currentMap5 = tableTextPaints;
            textSize5 = AndroidUtilities.dp(15.0f);
            textColor = getTextColor();
        }
        if (!((flags & 256) == 0 && (flags & 128) == 0)) {
            textSize5 -= AndroidUtilities.dp(4.0f);
        }
        if (currentMap5 == null) {
            if (errorTextPaint == null) {
                TextPaint textPaint = new TextPaint(1);
                errorTextPaint = textPaint;
                textPaint.setColor(SupportMenu.CATEGORY_MASK);
            }
            errorTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            return errorTextPaint;
        }
        TextPaint paint = currentMap5.get(flags);
        if (paint == null) {
            paint = new TextPaint(1);
            if ((flags & 4) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            } else if (pageBlock instanceof TLRPC.TL_pageBlockRelatedArticles) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if (this.selectedFont == 1 || (pageBlock instanceof TLRPC.TL_pageBlockTitle) || (pageBlock instanceof TLRPC.TL_pageBlockKicker) || (pageBlock instanceof TLRPC.TL_pageBlockHeader) || (pageBlock instanceof TLRPC.TL_pageBlockSubtitle) || (pageBlock instanceof TLRPC.TL_pageBlockSubheader)) {
                if ((flags & 1) != 0 && (flags & 2) != 0) {
                    paint.setTypeface(Typeface.create(C.SERIF_NAME, 3));
                } else if ((flags & 1) != 0) {
                    paint.setTypeface(Typeface.create(C.SERIF_NAME, 1));
                } else if ((flags & 2) != 0) {
                    paint.setTypeface(Typeface.create(C.SERIF_NAME, 2));
                } else {
                    paint.setTypeface(Typeface.create(C.SERIF_NAME, 0));
                }
            } else if ((flags & 1) != 0 && (flags & 2) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf"));
            } else if ((flags & 1) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if ((flags & 2) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
            }
            if ((flags & 32) != 0) {
                paint.setFlags(paint.getFlags() | 16);
            }
            if ((flags & 16) != 0) {
                paint.setFlags(paint.getFlags() | 8);
            }
            if (!((flags & 8) == 0 && (flags & 512) == 0)) {
                paint.setFlags(paint.getFlags());
                textColor = getLinkTextColor();
            }
            if ((flags & 256) != 0) {
                paint.baselineShift -= AndroidUtilities.dp(6.0f);
            } else if ((flags & 128) != 0) {
                paint.baselineShift += AndroidUtilities.dp(2.0f);
            }
            paint.setColor(textColor);
            currentMap5.put(flags, paint);
        }
        paint.setTextSize((float) (textSize5 + additionalSize));
        return paint;
    }

    /* access modifiers changed from: private */
    public DrawingText createLayoutForText(View parentView, CharSequence plainText, TLRPC.RichText richText, int width, TLRPC.PageBlock parentBlock, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, 0, parentBlock, Layout.Alignment.ALIGN_NORMAL, 0, parentAdapter);
    }

    /* access modifiers changed from: private */
    public DrawingText createLayoutForText(View parentView, CharSequence plainText, TLRPC.RichText richText, int width, TLRPC.PageBlock parentBlock, Layout.Alignment align, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, 0, parentBlock, align, 0, parentAdapter);
    }

    private DrawingText createLayoutForText(View parentView, CharSequence plainText, TLRPC.RichText richText, int width, int textY, TLRPC.PageBlock parentBlock, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, textY, parentBlock, Layout.Alignment.ALIGN_NORMAL, 0, parentAdapter);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x02ef A[Catch:{ Exception -> 0x0332 }] */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x0353 A[Catch:{ Exception -> 0x0396 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public im.bclpbkiauv.ui.ArticleViewer.DrawingText createLayoutForText(android.view.View r24, java.lang.CharSequence r25, im.bclpbkiauv.tgnet.TLRPC.RichText r26, int r27, int r28, im.bclpbkiauv.tgnet.TLRPC.PageBlock r29, android.text.Layout.Alignment r30, int r31, im.bclpbkiauv.ui.ArticleViewer.WebpageAdapter r32) {
        /*
            r23 = this;
            r7 = r23
            r8 = r25
            r9 = r26
            r10 = r29
            r0 = 0
            if (r8 != 0) goto L_0x0012
            if (r9 == 0) goto L_0x0011
            boolean r1 = r9 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_textEmpty
            if (r1 == 0) goto L_0x0012
        L_0x0011:
            return r0
        L_0x0012:
            if (r27 >= 0) goto L_0x001d
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            r21 = r1
            goto L_0x001f
        L_0x001d:
            r21 = r27
        L_0x001f:
            int r22 = r23.getSelectedColor()
            if (r8 == 0) goto L_0x0028
            r1 = r25
            goto L_0x0038
        L_0x0028:
            r1 = r23
            r2 = r24
            r3 = r26
            r4 = r26
            r5 = r29
            r6 = r21
            java.lang.CharSequence r1 = r1.getText(r2, r3, r4, r5, r6)
        L_0x0038:
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 == 0) goto L_0x003f
            return r0
        L_0x003f:
            int r2 = r7.selectedFontSize
            r3 = 1082130432(0x40800000, float:4.0)
            r4 = 1
            if (r2 != 0) goto L_0x004c
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r2 = -r2
            goto L_0x0067
        L_0x004c:
            r5 = 1073741824(0x40000000, float:2.0)
            if (r2 != r4) goto L_0x0056
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r2 = -r2
            goto L_0x0067
        L_0x0056:
            r6 = 3
            if (r2 != r6) goto L_0x005e
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            goto L_0x0067
        L_0x005e:
            r5 = 4
            if (r2 != r5) goto L_0x0066
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            goto L_0x0067
        L_0x0066:
            r2 = 0
        L_0x0067:
            boolean r5 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockEmbedPost
            r6 = 1096810496(0x41600000, float:14.0)
            r11 = 1097859072(0x41700000, float:15.0)
            if (r5 == 0) goto L_0x00c2
            if (r9 != 0) goto L_0x00c2
            r5 = r10
            im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockEmbedPost r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockEmbedPost) r5
            java.lang.String r12 = r5.author
            if (r12 != r8) goto L_0x0098
            android.text.TextPaint r6 = embedPostAuthorPaint
            if (r6 != 0) goto L_0x008a
            android.text.TextPaint r6 = new android.text.TextPaint
            r6.<init>(r4)
            embedPostAuthorPaint = r6
            int r12 = r23.getTextColor()
            r6.setColor(r12)
        L_0x008a:
            android.text.TextPaint r6 = embedPostAuthorPaint
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r2
            float r11 = (float) r11
            r6.setTextSize(r11)
            android.text.TextPaint r6 = embedPostAuthorPaint
            goto L_0x00c0
        L_0x0098:
            android.text.TextPaint r11 = embedPostDatePaint
            if (r11 != 0) goto L_0x00b3
            android.text.TextPaint r11 = new android.text.TextPaint
            r11.<init>(r4)
            embedPostDatePaint = r11
            if (r22 != 0) goto L_0x00ac
            r12 = -7366752(0xffffffffff8f97a0, float:NaN)
            r11.setColor(r12)
            goto L_0x00b3
        L_0x00ac:
            int r12 = r23.getGrayTextColor()
            r11.setColor(r12)
        L_0x00b3:
            android.text.TextPaint r11 = embedPostDatePaint
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r2
            float r6 = (float) r6
            r11.setTextSize(r6)
            android.text.TextPaint r6 = embedPostDatePaint
        L_0x00c0:
            goto L_0x01c5
        L_0x00c2:
            boolean r5 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockChannel
            java.lang.String r12 = "fonts/rmedium.ttf"
            if (r5 == 0) goto L_0x00fc
            android.text.TextPaint r5 = channelNamePaint
            if (r5 != 0) goto L_0x00da
            android.text.TextPaint r5 = new android.text.TextPaint
            r5.<init>(r4)
            channelNamePaint = r5
            android.graphics.Typeface r6 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r12)
            r5.setTypeface(r6)
        L_0x00da:
            im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockChannel r5 = r7.channelBlock
            if (r5 != 0) goto L_0x00e8
            android.text.TextPaint r5 = channelNamePaint
            int r6 = r23.getTextColor()
            r5.setColor(r6)
            goto L_0x00ee
        L_0x00e8:
            android.text.TextPaint r5 = channelNamePaint
            r6 = -1
            r5.setColor(r6)
        L_0x00ee:
            android.text.TextPaint r5 = channelNamePaint
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
            float r6 = (float) r6
            r5.setTextSize(r6)
            android.text.TextPaint r6 = channelNamePaint
            goto L_0x01c5
        L_0x00fc:
            boolean r5 = r10 instanceof im.bclpbkiauv.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild
            if (r5 == 0) goto L_0x0162
            r5 = r10
            im.bclpbkiauv.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild r5 = (im.bclpbkiauv.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild) r5
            im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockRelatedArticles r13 = r5.parent
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_pageRelatedArticle> r13 = r13.articles
            int r14 = r5.num
            java.lang.Object r13 = r13.get(r14)
            im.bclpbkiauv.tgnet.TLRPC$TL_pageRelatedArticle r13 = (im.bclpbkiauv.tgnet.TLRPC.TL_pageRelatedArticle) r13
            java.lang.String r13 = r13.title
            if (r8 != r13) goto L_0x0140
            android.text.TextPaint r6 = relatedArticleHeaderPaint
            if (r6 != 0) goto L_0x0129
            android.text.TextPaint r6 = new android.text.TextPaint
            r6.<init>(r4)
            relatedArticleHeaderPaint = r6
            android.graphics.Typeface r12 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r12)
            r6.setTypeface(r12)
        L_0x0129:
            android.text.TextPaint r6 = relatedArticleHeaderPaint
            int r12 = r23.getTextColor()
            r6.setColor(r12)
            android.text.TextPaint r6 = relatedArticleHeaderPaint
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
            int r11 = r11 + r2
            float r11 = (float) r11
            r6.setTextSize(r11)
            android.text.TextPaint r6 = relatedArticleHeaderPaint
            goto L_0x0161
        L_0x0140:
            android.text.TextPaint r11 = relatedArticleTextPaint
            if (r11 != 0) goto L_0x014b
            android.text.TextPaint r11 = new android.text.TextPaint
            r11.<init>(r4)
            relatedArticleTextPaint = r11
        L_0x014b:
            android.text.TextPaint r11 = relatedArticleTextPaint
            int r12 = r23.getGrayTextColor()
            r11.setColor(r12)
            android.text.TextPaint r11 = relatedArticleTextPaint
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r2
            float r6 = (float) r6
            r11.setTextSize(r6)
            android.text.TextPaint r6 = relatedArticleTextPaint
        L_0x0161:
            goto L_0x01c5
        L_0x0162:
            boolean r5 = r7.isListItemBlock(r10)
            if (r5 == 0) goto L_0x01c1
            if (r8 == 0) goto L_0x01c1
            android.text.TextPaint r5 = listTextPointerPaint
            if (r5 != 0) goto L_0x017c
            android.text.TextPaint r5 = new android.text.TextPaint
            r5.<init>(r4)
            listTextPointerPaint = r5
            int r6 = r23.getTextColor()
            r5.setColor(r6)
        L_0x017c:
            android.text.TextPaint r5 = listTextNumPaint
            if (r5 != 0) goto L_0x018e
            android.text.TextPaint r5 = new android.text.TextPaint
            r5.<init>(r4)
            listTextNumPaint = r5
            int r6 = r23.getTextColor()
            r5.setColor(r6)
        L_0x018e:
            android.text.TextPaint r5 = listTextPointerPaint
            r6 = 1100480512(0x41980000, float:19.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r2
            float r6 = (float) r6
            r5.setTextSize(r6)
            android.text.TextPaint r5 = listTextNumPaint
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 + r2
            float r6 = (float) r6
            r5.setTextSize(r6)
            boolean r5 = r10 instanceof im.bclpbkiauv.ui.ArticleViewer.TL_pageBlockListItem
            if (r5 == 0) goto L_0x01be
            r5 = r10
            im.bclpbkiauv.ui.ArticleViewer$TL_pageBlockListItem r5 = (im.bclpbkiauv.ui.ArticleViewer.TL_pageBlockListItem) r5
            im.bclpbkiauv.ui.ArticleViewer$TL_pageBlockListParent r5 = r5.parent
            im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockList r5 = r5.pageBlockList
            boolean r5 = r5.ordered
            if (r5 != 0) goto L_0x01be
            android.text.TextPaint r6 = listTextPointerPaint
            goto L_0x01c5
        L_0x01be:
            android.text.TextPaint r6 = listTextNumPaint
            goto L_0x01c5
        L_0x01c1:
            android.text.TextPaint r6 = r7.getTextPaint(r9, r9, r10)
        L_0x01c5:
            r5 = 0
            if (r31 == 0) goto L_0x0200
            boolean r11 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockPullquote
            if (r11 == 0) goto L_0x01e4
            android.text.Layout$Alignment r14 = android.text.Layout.Alignment.ALIGN_CENTER
            r15 = 1065353216(0x3f800000, float:1.0)
            r16 = 0
            r17 = 0
            android.text.TextUtils$TruncateAt r18 = android.text.TextUtils.TruncateAt.END
            r11 = r1
            r12 = r6
            r13 = r21
            r19 = r21
            r20 = r31
            android.text.StaticLayout r3 = im.bclpbkiauv.ui.components.StaticLayoutEx.createStaticLayout(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            goto L_0x0247
        L_0x01e4:
            r15 = 1065353216(0x3f800000, float:1.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r17 = 0
            android.text.TextUtils$TruncateAt r18 = android.text.TextUtils.TruncateAt.END
            r11 = r1
            r12 = r6
            r13 = r21
            r14 = r30
            r16 = r3
            r19 = r21
            r20 = r31
            android.text.StaticLayout r3 = im.bclpbkiauv.ui.components.StaticLayoutEx.createStaticLayout(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            goto L_0x0247
        L_0x0200:
            int r11 = r1.length()
            int r11 = r11 - r4
            char r11 = r1.charAt(r11)
            r12 = 10
            if (r11 != r12) goto L_0x0216
            int r11 = r1.length()
            int r11 = r11 - r4
            java.lang.CharSequence r1 = r1.subSequence(r5, r11)
        L_0x0216:
            boolean r11 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockPullquote
            if (r11 == 0) goto L_0x022d
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.Layout$Alignment r15 = android.text.Layout.Alignment.ALIGN_CENTER
            r16 = 1065353216(0x3f800000, float:1.0)
            r17 = 0
            r18 = 0
            r11 = r3
            r12 = r1
            r13 = r6
            r14 = r21
            r11.<init>(r12, r13, r14, r15, r16, r17, r18)
            goto L_0x0247
        L_0x022d:
            android.text.StaticLayout r19 = new android.text.StaticLayout
            r16 = 1065353216(0x3f800000, float:1.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r18 = 0
            r11 = r19
            r12 = r1
            r13 = r6
            r14 = r21
            r15 = r30
            r17 = r3
            r11.<init>(r12, r13, r14, r15, r16, r17, r18)
            r3 = r19
        L_0x0247:
            if (r3 != 0) goto L_0x024a
            return r0
        L_0x024a:
            java.lang.CharSequence r11 = r3.getText()
            r12 = 0
            r13 = 0
            if (r3 == 0) goto L_0x039c
            boolean r0 = r11 instanceof android.text.Spanned
            if (r0 == 0) goto L_0x039c
            r14 = r11
            android.text.Spanned r14 = (android.text.Spanned) r14
            int r0 = r14.length()     // Catch:{ Exception -> 0x02c8 }
            java.lang.Class<im.bclpbkiauv.ui.components.AnchorSpan> r15 = im.bclpbkiauv.ui.components.AnchorSpan.class
            java.lang.Object[] r0 = r14.getSpans(r5, r0, r15)     // Catch:{ Exception -> 0x02c8 }
            im.bclpbkiauv.ui.components.AnchorSpan[] r0 = (im.bclpbkiauv.ui.components.AnchorSpan[]) r0     // Catch:{ Exception -> 0x02c8 }
            int r15 = r3.getLineCount()     // Catch:{ Exception -> 0x02c8 }
            if (r0 == 0) goto L_0x02c3
            int r5 = r0.length     // Catch:{ Exception -> 0x02c8 }
            if (r5 <= 0) goto L_0x02c3
            r5 = 0
        L_0x026f:
            int r4 = r0.length     // Catch:{ Exception -> 0x02c8 }
            if (r5 >= r4) goto L_0x02be
            r4 = 1
            if (r15 > r4) goto L_0x028f
            java.util.HashMap r4 = r32.anchorsOffset     // Catch:{ Exception -> 0x02c8 }
            r17 = r0[r5]     // Catch:{ Exception -> 0x02c8 }
            r18 = r1
            java.lang.String r1 = r17.getName()     // Catch:{ Exception -> 0x028b }
            r17 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r28)     // Catch:{ Exception -> 0x02bc }
            r4.put(r1, r2)     // Catch:{ Exception -> 0x02bc }
            goto L_0x02b4
        L_0x028b:
            r0 = move-exception
            r17 = r2
            goto L_0x02cd
        L_0x028f:
            r18 = r1
            r17 = r2
            java.util.HashMap r1 = r32.anchorsOffset     // Catch:{ Exception -> 0x02bc }
            r2 = r0[r5]     // Catch:{ Exception -> 0x02bc }
            java.lang.String r2 = r2.getName()     // Catch:{ Exception -> 0x02bc }
            r4 = r0[r5]     // Catch:{ Exception -> 0x02bc }
            int r4 = r14.getSpanStart(r4)     // Catch:{ Exception -> 0x02bc }
            int r4 = r3.getLineForOffset(r4)     // Catch:{ Exception -> 0x02bc }
            int r4 = r3.getLineTop(r4)     // Catch:{ Exception -> 0x02bc }
            int r4 = r28 + r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x02bc }
            r1.put(r2, r4)     // Catch:{ Exception -> 0x02bc }
        L_0x02b4:
            int r5 = r5 + 1
            r2 = r17
            r1 = r18
            r4 = 1
            goto L_0x026f
        L_0x02bc:
            r0 = move-exception
            goto L_0x02cd
        L_0x02be:
            r18 = r1
            r17 = r2
            goto L_0x02c7
        L_0x02c3:
            r18 = r1
            r17 = r2
        L_0x02c7:
            goto L_0x02cd
        L_0x02c8:
            r0 = move-exception
            r18 = r1
            r17 = r2
        L_0x02cd:
            r4 = 0
            int r0 = r14.length()     // Catch:{ Exception -> 0x0332 }
            java.lang.Class<im.bclpbkiauv.ui.components.TextPaintWebpageUrlSpan> r5 = im.bclpbkiauv.ui.components.TextPaintWebpageUrlSpan.class
            r15 = 0
            java.lang.Object[] r0 = r14.getSpans(r15, r0, r5)     // Catch:{ Exception -> 0x0332 }
            im.bclpbkiauv.ui.components.TextPaintWebpageUrlSpan[] r0 = (im.bclpbkiauv.ui.components.TextPaintWebpageUrlSpan[]) r0     // Catch:{ Exception -> 0x0332 }
            if (r0 == 0) goto L_0x0331
            int r5 = r0.length     // Catch:{ Exception -> 0x0332 }
            if (r5 <= 0) goto L_0x0331
            im.bclpbkiauv.ui.components.LinkPath r5 = new im.bclpbkiauv.ui.components.LinkPath     // Catch:{ Exception -> 0x0332 }
            r15 = 1
            r5.<init>(r15)     // Catch:{ Exception -> 0x0332 }
            r12 = r5
            r5 = 0
            r12.setAllowReset(r5)     // Catch:{ Exception -> 0x0332 }
            r5 = 0
        L_0x02ec:
            int r15 = r0.length     // Catch:{ Exception -> 0x0332 }
            if (r5 >= r15) goto L_0x032d
            r15 = r0[r5]     // Catch:{ Exception -> 0x0332 }
            int r15 = r14.getSpanStart(r15)     // Catch:{ Exception -> 0x0332 }
            r1 = r0[r5]     // Catch:{ Exception -> 0x0332 }
            int r1 = r14.getSpanEnd(r1)     // Catch:{ Exception -> 0x0332 }
            r12.setCurrentLayout(r3, r15, r4)     // Catch:{ Exception -> 0x0332 }
            r20 = r0[r5]     // Catch:{ Exception -> 0x0332 }
            android.text.TextPaint r20 = r20.getTextPaint()     // Catch:{ Exception -> 0x0332 }
            if (r20 == 0) goto L_0x030f
            r20 = r0[r5]     // Catch:{ Exception -> 0x0332 }
            android.text.TextPaint r2 = r20.getTextPaint()     // Catch:{ Exception -> 0x0332 }
            int r2 = r2.baselineShift     // Catch:{ Exception -> 0x0332 }
            goto L_0x0310
        L_0x030f:
            r2 = 0
        L_0x0310:
            if (r2 == 0) goto L_0x0322
            if (r2 <= 0) goto L_0x0317
            r20 = 1084227584(0x40a00000, float:5.0)
            goto L_0x0319
        L_0x0317:
            r20 = -1073741824(0xffffffffc0000000, float:-2.0)
        L_0x0319:
            int r20 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x0332 }
            int r20 = r2 + r20
            r4 = r20
            goto L_0x0323
        L_0x0322:
            r4 = 0
        L_0x0323:
            r12.setBaselineShift(r4)     // Catch:{ Exception -> 0x0332 }
            r3.getSelectionPath(r15, r1, r12)     // Catch:{ Exception -> 0x0332 }
            int r5 = r5 + 1
            r4 = 0
            goto L_0x02ec
        L_0x032d:
            r1 = 1
            r12.setAllowReset(r1)     // Catch:{ Exception -> 0x0332 }
        L_0x0331:
            goto L_0x0333
        L_0x0332:
            r0 = move-exception
        L_0x0333:
            int r0 = r14.length()     // Catch:{ Exception -> 0x039a }
            java.lang.Class<im.bclpbkiauv.ui.components.TextPaintMarkSpan> r1 = im.bclpbkiauv.ui.components.TextPaintMarkSpan.class
            r2 = 0
            java.lang.Object[] r0 = r14.getSpans(r2, r0, r1)     // Catch:{ Exception -> 0x039a }
            im.bclpbkiauv.ui.components.TextPaintMarkSpan[] r0 = (im.bclpbkiauv.ui.components.TextPaintMarkSpan[]) r0     // Catch:{ Exception -> 0x039a }
            if (r0 == 0) goto L_0x0399
            int r1 = r0.length     // Catch:{ Exception -> 0x039a }
            if (r1 <= 0) goto L_0x0399
            im.bclpbkiauv.ui.components.LinkPath r1 = new im.bclpbkiauv.ui.components.LinkPath     // Catch:{ Exception -> 0x039a }
            r2 = 1
            r1.<init>(r2)     // Catch:{ Exception -> 0x039a }
            r15 = 0
            r1.setAllowReset(r15)     // Catch:{ Exception -> 0x0396 }
            r2 = 0
        L_0x0350:
            int r4 = r0.length     // Catch:{ Exception -> 0x0396 }
            if (r2 >= r4) goto L_0x0390
            r4 = r0[r2]     // Catch:{ Exception -> 0x0396 }
            int r4 = r14.getSpanStart(r4)     // Catch:{ Exception -> 0x0396 }
            r5 = r0[r2]     // Catch:{ Exception -> 0x0396 }
            int r5 = r14.getSpanEnd(r5)     // Catch:{ Exception -> 0x0396 }
            r13 = 0
            r1.setCurrentLayout(r3, r4, r13)     // Catch:{ Exception -> 0x0396 }
            r20 = r0[r2]     // Catch:{ Exception -> 0x0396 }
            android.text.TextPaint r20 = r20.getTextPaint()     // Catch:{ Exception -> 0x0396 }
            if (r20 == 0) goto L_0x0374
            r20 = r0[r2]     // Catch:{ Exception -> 0x0396 }
            android.text.TextPaint r13 = r20.getTextPaint()     // Catch:{ Exception -> 0x0396 }
            int r13 = r13.baselineShift     // Catch:{ Exception -> 0x0396 }
            goto L_0x0375
        L_0x0374:
            r13 = 0
        L_0x0375:
            if (r13 == 0) goto L_0x0386
            if (r13 <= 0) goto L_0x037c
            r20 = 1084227584(0x40a00000, float:5.0)
            goto L_0x037e
        L_0x037c:
            r20 = -1073741824(0xffffffffc0000000, float:-2.0)
        L_0x037e:
            int r20 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x0396 }
            int r20 = r13 + r20
            r15 = r20
        L_0x0386:
            r1.setBaselineShift(r15)     // Catch:{ Exception -> 0x0396 }
            r3.getSelectionPath(r4, r5, r1)     // Catch:{ Exception -> 0x0396 }
            int r2 = r2 + 1
            r15 = 0
            goto L_0x0350
        L_0x0390:
            r2 = 1
            r1.setAllowReset(r2)     // Catch:{ Exception -> 0x0396 }
            r13 = r1
            goto L_0x0399
        L_0x0396:
            r0 = move-exception
            r13 = r1
            goto L_0x03a0
        L_0x0399:
            goto L_0x03a0
        L_0x039a:
            r0 = move-exception
            goto L_0x03a0
        L_0x039c:
            r18 = r1
            r17 = r2
        L_0x03a0:
            im.bclpbkiauv.ui.ArticleViewer$DrawingText r0 = new im.bclpbkiauv.ui.ArticleViewer$DrawingText
            r0.<init>()
            r0.textLayout = r3
            r0.textPath = r12
            r0.markPath = r13
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.createLayoutForText(android.view.View, java.lang.CharSequence, im.bclpbkiauv.tgnet.TLRPC$RichText, int, int, im.bclpbkiauv.tgnet.TLRPC$PageBlock, android.text.Layout$Alignment, int, im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter):im.bclpbkiauv.ui.ArticleViewer$DrawingText");
    }

    /* access modifiers changed from: private */
    public void drawLayoutLink(Canvas canvas, DrawingText layout) {
        float width;
        float x;
        if (canvas != null && layout != null && this.pressedLinkOwnerLayout == layout) {
            if (this.pressedLink != null) {
                canvas.drawPath(this.urlPath, urlPaint);
            } else if (this.drawBlockSelection && layout != null) {
                if (layout.getLineCount() == 1) {
                    width = layout.getLineWidth(0);
                    x = layout.getLineLeft(0);
                } else {
                    width = (float) layout.getWidth();
                    x = 0.0f;
                }
                canvas.drawRect(((float) (-AndroidUtilities.dp(2.0f))) + x, 0.0f, x + width + ((float) AndroidUtilities.dp(2.0f)), (float) layout.getHeight(), urlPaint);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0202  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0222  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x022a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkLayoutForLinks(android.view.MotionEvent r24, android.view.View r25, im.bclpbkiauv.ui.ArticleViewer.DrawingText r26, int r27, int r28) {
        /*
            r23 = this;
            r1 = r23
            r2 = r25
            r3 = r26
            r4 = r27
            r5 = r28
            android.animation.AnimatorSet r0 = r1.pageSwitchAnimation
            if (r0 != 0) goto L_0x0232
            if (r2 == 0) goto L_0x0232
            if (r3 != 0) goto L_0x0014
            goto L_0x0232
        L_0x0014:
            android.text.StaticLayout r7 = r3.textLayout
            float r0 = r24.getX()
            int r8 = (int) r0
            float r0 = r24.getY()
            int r9 = (int) r0
            r10 = 0
            int r0 = r24.getAction()
            if (r0 != 0) goto L_0x0165
            r0 = 0
            r12 = 1325400064(0x4f000000, float:2.14748365E9)
            r13 = 0
            int r14 = r7.getLineCount()
            r22 = r12
            r12 = r0
            r0 = r13
            r13 = r22
        L_0x0035:
            if (r0 >= r14) goto L_0x004a
            float r15 = r7.getLineWidth(r0)
            float r12 = java.lang.Math.max(r15, r12)
            float r15 = r7.getLineLeft(r0)
            float r13 = java.lang.Math.min(r15, r13)
            int r0 = r0 + 1
            goto L_0x0035
        L_0x004a:
            float r0 = (float) r8
            float r14 = (float) r4
            float r14 = r14 + r13
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 < 0) goto L_0x0160
            float r0 = (float) r8
            float r14 = (float) r4
            float r14 = r14 + r13
            float r14 = r14 + r12
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 > 0) goto L_0x0160
            if (r9 < r5) goto L_0x0160
            int r0 = r7.getHeight()
            int r0 = r0 + r5
            if (r9 > r0) goto L_0x0160
            r1.pressedLinkOwnerLayout = r3
            r1.pressedLinkOwnerView = r2
            r1.pressedLayoutY = r5
            java.lang.CharSequence r14 = r7.getText()
            boolean r0 = r14 instanceof android.text.Spannable
            if (r0 == 0) goto L_0x015d
            int r15 = r8 - r4
            int r11 = r9 - r5
            int r0 = r7.getLineForVertical(r11)     // Catch:{ Exception -> 0x0156 }
            r16 = r0
            float r0 = (float) r15     // Catch:{ Exception -> 0x0156 }
            r6 = r16
            int r0 = r7.getOffsetForHorizontal(r6, r0)     // Catch:{ Exception -> 0x0156 }
            r16 = r0
            float r0 = r7.getLineLeft(r6)     // Catch:{ Exception -> 0x0156 }
            r13 = r0
            float r0 = (float) r15     // Catch:{ Exception -> 0x0156 }
            int r0 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x014f
            float r0 = r7.getLineWidth(r6)     // Catch:{ Exception -> 0x0156 }
            float r0 = r0 + r13
            float r3 = (float) r15     // Catch:{ Exception -> 0x0156 }
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 < 0) goto L_0x014f
            java.lang.CharSequence r0 = r7.getText()     // Catch:{ Exception -> 0x0156 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x0156 }
            r3 = r0
            java.lang.Class<im.bclpbkiauv.ui.components.TextPaintUrlSpan> r0 = im.bclpbkiauv.ui.components.TextPaintUrlSpan.class
            r4 = r16
            java.lang.Object[] r0 = r3.getSpans(r4, r4, r0)     // Catch:{ Exception -> 0x0156 }
            im.bclpbkiauv.ui.components.TextPaintUrlSpan[] r0 = (im.bclpbkiauv.ui.components.TextPaintUrlSpan[]) r0     // Catch:{ Exception -> 0x0156 }
            r16 = r0
            r17 = r4
            r4 = r16
            if (r4 == 0) goto L_0x0146
            int r0 = r4.length     // Catch:{ Exception -> 0x0156 }
            if (r0 <= 0) goto L_0x0146
            r16 = 0
            r0 = r4[r16]     // Catch:{ Exception -> 0x0156 }
            r1.pressedLink = r0     // Catch:{ Exception -> 0x0156 }
            int r0 = r3.getSpanStart(r0)     // Catch:{ Exception -> 0x0156 }
            r16 = r0
            im.bclpbkiauv.ui.components.TextPaintUrlSpan r0 = r1.pressedLink     // Catch:{ Exception -> 0x0156 }
            int r0 = r3.getSpanEnd(r0)     // Catch:{ Exception -> 0x0156 }
            r18 = 1
            r5 = r16
            r16 = r6
            r6 = r0
            r0 = r18
        L_0x00ce:
            r18 = r8
            int r8 = r4.length     // Catch:{ Exception -> 0x0144 }
            if (r0 >= r8) goto L_0x00ff
            r8 = r4[r0]     // Catch:{ Exception -> 0x0144 }
            int r19 = r3.getSpanStart(r8)     // Catch:{ Exception -> 0x0144 }
            r20 = r19
            int r19 = r3.getSpanEnd(r8)     // Catch:{ Exception -> 0x0144 }
            r21 = r19
            r19 = r3
            r3 = r20
            if (r5 > r3) goto L_0x00ee
            r20 = r4
            r4 = r21
            if (r4 <= r6) goto L_0x00f6
            goto L_0x00f2
        L_0x00ee:
            r20 = r4
            r4 = r21
        L_0x00f2:
            r1.pressedLink = r8     // Catch:{ Exception -> 0x0144 }
            r5 = r3
            r6 = r4
        L_0x00f6:
            int r0 = r0 + 1
            r8 = r18
            r3 = r19
            r4 = r20
            goto L_0x00ce
        L_0x00ff:
            r19 = r3
            r20 = r4
            im.bclpbkiauv.ui.components.LinkPath r0 = r1.urlPath     // Catch:{ Exception -> 0x013f }
            r3 = 1
            r0.setUseRoundRect(r3)     // Catch:{ Exception -> 0x013f }
            im.bclpbkiauv.ui.components.LinkPath r0 = r1.urlPath     // Catch:{ Exception -> 0x013f }
            r3 = 0
            r0.setCurrentLayout(r7, r5, r3)     // Catch:{ Exception -> 0x013f }
            im.bclpbkiauv.ui.components.TextPaintUrlSpan r0 = r1.pressedLink     // Catch:{ Exception -> 0x013f }
            android.text.TextPaint r0 = r0.getTextPaint()     // Catch:{ Exception -> 0x013f }
            if (r0 == 0) goto L_0x0120
            im.bclpbkiauv.ui.components.TextPaintUrlSpan r0 = r1.pressedLink     // Catch:{ Exception -> 0x013f }
            android.text.TextPaint r0 = r0.getTextPaint()     // Catch:{ Exception -> 0x013f }
            int r0 = r0.baselineShift     // Catch:{ Exception -> 0x013f }
            goto L_0x0121
        L_0x0120:
            r0 = 0
        L_0x0121:
            im.bclpbkiauv.ui.components.LinkPath r3 = r1.urlPath     // Catch:{ Exception -> 0x013f }
            if (r0 == 0) goto L_0x0132
            if (r0 <= 0) goto L_0x012a
            r4 = 1084227584(0x40a00000, float:5.0)
            goto L_0x012c
        L_0x012a:
            r4 = -1073741824(0xffffffffc0000000, float:-2.0)
        L_0x012c:
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x013f }
            int r4 = r4 + r0
            goto L_0x0133
        L_0x0132:
            r4 = 0
        L_0x0133:
            r3.setBaselineShift(r4)     // Catch:{ Exception -> 0x013f }
            im.bclpbkiauv.ui.components.LinkPath r3 = r1.urlPath     // Catch:{ Exception -> 0x013f }
            r7.getSelectionPath(r5, r6, r3)     // Catch:{ Exception -> 0x013f }
            r25.invalidate()     // Catch:{ Exception -> 0x013f }
            goto L_0x0155
        L_0x013f:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0144 }
            goto L_0x0155
        L_0x0144:
            r0 = move-exception
            goto L_0x0159
        L_0x0146:
            r19 = r3
            r20 = r4
            r16 = r6
            r18 = r8
            goto L_0x0155
        L_0x014f:
            r18 = r8
            r17 = r16
            r16 = r6
        L_0x0155:
            goto L_0x0162
        L_0x0156:
            r0 = move-exception
            r18 = r8
        L_0x0159:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0162
        L_0x015d:
            r18 = r8
            goto L_0x0162
        L_0x0160:
            r18 = r8
        L_0x0162:
            r3 = 1
            goto L_0x0200
        L_0x0165:
            r18 = r8
            int r0 = r24.getAction()
            r3 = 1
            if (r0 != r3) goto L_0x01ee
            im.bclpbkiauv.ui.components.TextPaintUrlSpan r0 = r1.pressedLink
            if (r0 == 0) goto L_0x0200
            r10 = 1
            java.lang.String r4 = r0.getUrl()
            if (r4 == 0) goto L_0x01ed
            im.bclpbkiauv.ui.actionbar.BottomSheet r0 = r1.linkSheet
            if (r0 == 0) goto L_0x0183
            r0.dismiss()
            r0 = 0
            r1.linkSheet = r0
        L_0x0183:
            r5 = 0
            r0 = 35
            int r0 = r4.lastIndexOf(r0)
            r6 = r0
            r8 = -1
            if (r0 == r8) goto L_0x01e1
            im.bclpbkiauv.tgnet.TLRPC$WebPage r0 = r1.currentPage
            im.bclpbkiauv.tgnet.TLRPC$Page r0 = r0.cached_page
            java.lang.String r0 = r0.url
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x01a6
            im.bclpbkiauv.tgnet.TLRPC$WebPage r0 = r1.currentPage
            im.bclpbkiauv.tgnet.TLRPC$Page r0 = r0.cached_page
            java.lang.String r0 = r0.url
            java.lang.String r0 = r0.toLowerCase()
            r8 = r0
            goto L_0x01af
        L_0x01a6:
            im.bclpbkiauv.tgnet.TLRPC$WebPage r0 = r1.currentPage
            java.lang.String r0 = r0.url
            java.lang.String r0 = r0.toLowerCase()
            r8 = r0
        L_0x01af:
            int r0 = r6 + 1
            java.lang.String r0 = r4.substring(r0)     // Catch:{ Exception -> 0x01bc }
            java.lang.String r11 = "UTF-8"
            java.lang.String r0 = java.net.URLDecoder.decode(r0, r11)     // Catch:{ Exception -> 0x01bc }
            goto L_0x01c0
        L_0x01bc:
            r0 = move-exception
            java.lang.String r11 = ""
            r0 = r11
        L_0x01c0:
            java.lang.String r11 = r4.toLowerCase()
            boolean r11 = r11.contains(r8)
            if (r11 == 0) goto L_0x01e0
            boolean r11 = android.text.TextUtils.isEmpty(r0)
            if (r11 == 0) goto L_0x01dc
            androidx.recyclerview.widget.LinearLayoutManager[] r11 = r1.layoutManager
            r12 = 0
            r11 = r11[r12]
            r11.scrollToPositionWithOffset(r12, r12)
            r23.checkScrollAnimated()
            goto L_0x01df
        L_0x01dc:
            r1.scrollToAnchor(r0)
        L_0x01df:
            r5 = 1
        L_0x01e0:
            goto L_0x01e2
        L_0x01e1:
            r0 = 0
        L_0x01e2:
            if (r5 != 0) goto L_0x01ed
            im.bclpbkiauv.ui.components.TextPaintUrlSpan r8 = r1.pressedLink
            java.lang.String r8 = r8.getUrl()
            r1.openWebpageUrl(r8, r0)
        L_0x01ed:
            goto L_0x0200
        L_0x01ee:
            int r0 = r24.getAction()
            r4 = 3
            if (r0 != r4) goto L_0x0200
            im.bclpbkiauv.ui.actionbar.ActionBarPopupWindow r0 = r1.popupWindow
            if (r0 == 0) goto L_0x01ff
            boolean r0 = r0.isShowing()
            if (r0 != 0) goto L_0x0200
        L_0x01ff:
            r10 = 1
        L_0x0200:
            if (r10 == 0) goto L_0x0205
            r23.removePressedLink()
        L_0x0205:
            int r0 = r24.getAction()
            if (r0 != 0) goto L_0x020e
            r23.startCheckLongPress()
        L_0x020e:
            int r0 = r24.getAction()
            if (r0 == 0) goto L_0x021e
            int r0 = r24.getAction()
            r4 = 2
            if (r0 == r4) goto L_0x021e
            r23.cancelCheckLongPress()
        L_0x021e:
            boolean r0 = r2 instanceof im.bclpbkiauv.ui.ArticleViewer.BlockDetailsCell
            if (r0 == 0) goto L_0x022a
            im.bclpbkiauv.ui.components.TextPaintUrlSpan r0 = r1.pressedLink
            if (r0 == 0) goto L_0x0228
            r6 = 1
            goto L_0x0229
        L_0x0228:
            r6 = 0
        L_0x0229:
            return r6
        L_0x022a:
            im.bclpbkiauv.ui.ArticleViewer$DrawingText r0 = r1.pressedLinkOwnerLayout
            if (r0 == 0) goto L_0x0230
            r6 = 1
            goto L_0x0231
        L_0x0230:
            r6 = 0
        L_0x0231:
            return r6
        L_0x0232:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.checkLayoutForLinks(android.view.MotionEvent, android.view.View, im.bclpbkiauv.ui.ArticleViewer$DrawingText, int, int):boolean");
    }

    /* access modifiers changed from: private */
    public void removePressedLink() {
        if (this.pressedLink != null || this.pressedLinkOwnerView != null) {
            View parentView = this.pressedLinkOwnerView;
            this.pressedLink = null;
            this.pressedLinkOwnerLayout = null;
            this.pressedLinkOwnerView = null;
            if (parentView != null) {
                parentView.invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    public void openWebpageUrl(String url, String anchor) {
        if (this.openUrlReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.openUrlReqId, false);
            this.openUrlReqId = 0;
        }
        int reqId = this.lastReqId + 1;
        this.lastReqId = reqId;
        closePhoto(false);
        showProgressView(true, true);
        TLRPC.TL_messages_getWebPage req = new TLRPC.TL_messages_getWebPage();
        req.url = url;
        req.hash = 0;
        this.openUrlReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(reqId, anchor, req) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ TLRPC.TL_messages_getWebPage f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ArticleViewer.this.lambda$openWebpageUrl$6$ArticleViewer(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$openWebpageUrl$6$ArticleViewer(int reqId, String anchor, TLRPC.TL_messages_getWebPage req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(reqId, response, anchor, req) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ TLRPC.TL_messages_getWebPage f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ArticleViewer.this.lambda$null$5$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$ArticleViewer(int reqId, TLObject response, String anchor, TLRPC.TL_messages_getWebPage req) {
        if (this.openUrlReqId != 0 && reqId == this.lastReqId) {
            this.openUrlReqId = 0;
            showProgressView(true, false);
            if (!this.isVisible) {
                return;
            }
            if (!(response instanceof TLRPC.TL_webPage) || !(((TLRPC.TL_webPage) response).cached_page instanceof TLRPC.TL_page)) {
                Browser.openUrl((Context) this.parentActivity, req.url);
            } else {
                addPageToStack((TLRPC.TL_webPage) response, anchor, 1);
            }
        }
    }

    /* access modifiers changed from: private */
    public TLRPC.Photo getPhotoWithId(long id) {
        TLRPC.WebPage webPage = this.currentPage;
        if (webPage == null || webPage.cached_page == null) {
            return null;
        }
        if (this.currentPage.photo != null && this.currentPage.photo.id == id) {
            return this.currentPage.photo;
        }
        for (int a = 0; a < this.currentPage.cached_page.photos.size(); a++) {
            TLRPC.Photo photo = this.currentPage.cached_page.photos.get(a);
            if (photo.id == id) {
                return photo;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public TLRPC.Document getDocumentWithId(long id) {
        TLRPC.WebPage webPage = this.currentPage;
        if (webPage == null || webPage.cached_page == null) {
            return null;
        }
        if (this.currentPage.document != null && this.currentPage.document.id == id) {
            return this.currentPage.document;
        }
        for (int a = 0; a < this.currentPage.cached_page.documents.size(); a++) {
            TLRPC.Document document = this.currentPage.cached_page.documents.get(a);
            if (document.id == id) {
                return document;
            }
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0129, code lost:
        r5 = (im.bclpbkiauv.ui.ArticleViewer.BlockAudioCell) r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r10, int r11, java.lang.Object... r12) {
        /*
            r9 = this;
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fileDidFailToLoad
            r1 = 1065353216(0x3f800000, float:1.0)
            r2 = 3
            r3 = 0
            r4 = 1
            if (r10 != r0) goto L_0x002e
            r0 = r12[r3]
            java.lang.String r0 = (java.lang.String) r0
            r3 = 0
        L_0x000e:
            if (r3 >= r2) goto L_0x002c
            java.lang.String[] r5 = r9.currentFileNames
            r6 = r5[r3]
            if (r6 == 0) goto L_0x0029
            r5 = r5[r3]
            boolean r5 = r5.equals(r0)
            if (r5 == 0) goto L_0x0029
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r2 = r9.radialProgressViews
            r2 = r2[r3]
            r2.setProgress(r1, r4)
            r9.checkProgress(r3, r4)
            goto L_0x002c
        L_0x0029:
            int r3 = r3 + 1
            goto L_0x000e
        L_0x002c:
            goto L_0x018d
        L_0x002e:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fileDidLoad
            if (r10 != r0) goto L_0x0064
            r0 = r12[r3]
            java.lang.String r0 = (java.lang.String) r0
            r5 = 0
        L_0x0037:
            if (r5 >= r2) goto L_0x0062
            java.lang.String[] r6 = r9.currentFileNames
            r7 = r6[r5]
            if (r7 == 0) goto L_0x005f
            r6 = r6[r5]
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x005f
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r2 = r9.radialProgressViews
            r2 = r2[r5]
            r2.setProgress(r1, r4)
            r9.checkProgress(r5, r4)
            if (r5 != 0) goto L_0x0062
            int r1 = r9.currentIndex
            boolean r1 = r9.isMediaVideo(r1)
            if (r1 == 0) goto L_0x0062
            r9.onActionClick(r3)
            goto L_0x0062
        L_0x005f:
            int r5 = r5 + 1
            goto L_0x0037
        L_0x0062:
            goto L_0x018d
        L_0x0064:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.FileLoadProgressChanged
            if (r10 != r0) goto L_0x0091
            r0 = r12[r3]
            java.lang.String r0 = (java.lang.String) r0
            r1 = 0
        L_0x006d:
            if (r1 >= r2) goto L_0x008f
            java.lang.String[] r3 = r9.currentFileNames
            r5 = r3[r1]
            if (r5 == 0) goto L_0x008c
            r3 = r3[r1]
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x008c
            r3 = r12[r4]
            java.lang.Float r3 = (java.lang.Float) r3
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r5 = r9.radialProgressViews
            r5 = r5[r1]
            float r6 = r3.floatValue()
            r5.setProgress(r6, r4)
        L_0x008c:
            int r1 = r1 + 1
            goto L_0x006d
        L_0x008f:
            goto L_0x018d
        L_0x0091:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.emojiDidLoad
            if (r10 != r0) goto L_0x009e
            android.widget.TextView r0 = r9.captionTextView
            if (r0 == 0) goto L_0x018d
            r0.invalidate()
            goto L_0x018d
        L_0x009e:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.needSetDayNightTheme
            if (r10 != r0) goto L_0x00c4
            boolean r0 = r9.nightModeEnabled
            if (r0 == 0) goto L_0x018d
            int r0 = r9.selectedColor
            r1 = 2
            if (r0 == r1) goto L_0x018d
            im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter[] r0 = r9.adapter
            if (r0 == 0) goto L_0x018d
            r9.updatePaintColors()
            r0 = 0
        L_0x00b3:
            im.bclpbkiauv.ui.components.RecyclerListView[] r1 = r9.listView
            int r1 = r1.length
            if (r0 >= r1) goto L_0x00c2
            im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter[] r1 = r9.adapter
            r1 = r1[r0]
            r1.notifyDataSetChanged()
            int r0 = r0 + 1
            goto L_0x00b3
        L_0x00c2:
            goto L_0x018d
        L_0x00c4:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingDidStart
            if (r10 != r0) goto L_0x00f9
            r0 = r12[r3]
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            im.bclpbkiauv.ui.components.RecyclerListView[] r1 = r9.listView
            if (r1 == 0) goto L_0x00f7
            r1 = 0
        L_0x00d1:
            im.bclpbkiauv.ui.components.RecyclerListView[] r2 = r9.listView
            int r3 = r2.length
            if (r1 >= r3) goto L_0x00f7
            r2 = r2[r1]
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x00dd:
            if (r3 >= r2) goto L_0x00f4
            im.bclpbkiauv.ui.components.RecyclerListView[] r5 = r9.listView
            r5 = r5[r1]
            android.view.View r5 = r5.getChildAt(r3)
            boolean r6 = r5 instanceof im.bclpbkiauv.ui.ArticleViewer.BlockAudioCell
            if (r6 == 0) goto L_0x00f1
            r6 = r5
            im.bclpbkiauv.ui.ArticleViewer$BlockAudioCell r6 = (im.bclpbkiauv.ui.ArticleViewer.BlockAudioCell) r6
            r6.updateButtonState(r4)
        L_0x00f1:
            int r3 = r3 + 1
            goto L_0x00dd
        L_0x00f4:
            int r1 = r1 + 1
            goto L_0x00d1
        L_0x00f7:
            goto L_0x018d
        L_0x00f9:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingDidReset
            if (r10 == r0) goto L_0x015c
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r10 != r0) goto L_0x0102
            goto L_0x015c
        L_0x0102:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingProgressDidChanged
            if (r10 != r0) goto L_0x018d
            r0 = r12[r3]
            java.lang.Integer r0 = (java.lang.Integer) r0
            im.bclpbkiauv.ui.components.RecyclerListView[] r1 = r9.listView
            if (r1 == 0) goto L_0x018d
            r1 = 0
        L_0x010f:
            im.bclpbkiauv.ui.components.RecyclerListView[] r2 = r9.listView
            int r3 = r2.length
            if (r1 >= r3) goto L_0x018d
            r2 = r2[r1]
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x011b:
            if (r3 >= r2) goto L_0x0159
            im.bclpbkiauv.ui.components.RecyclerListView[] r4 = r9.listView
            r4 = r4[r1]
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof im.bclpbkiauv.ui.ArticleViewer.BlockAudioCell
            if (r5 == 0) goto L_0x0156
            r5 = r4
            im.bclpbkiauv.ui.ArticleViewer$BlockAudioCell r5 = (im.bclpbkiauv.ui.ArticleViewer.BlockAudioCell) r5
            im.bclpbkiauv.messenger.MessageObject r6 = r5.getMessageObject()
            if (r6 == 0) goto L_0x0156
            int r7 = r6.getId()
            int r8 = r0.intValue()
            if (r7 != r8) goto L_0x0156
            im.bclpbkiauv.messenger.MediaController r7 = im.bclpbkiauv.messenger.MediaController.getInstance()
            im.bclpbkiauv.messenger.MessageObject r7 = r7.getPlayingMessageObject()
            if (r7 == 0) goto L_0x0159
            float r8 = r7.audioProgress
            r6.audioProgress = r8
            int r8 = r7.audioProgressSec
            r6.audioProgressSec = r8
            int r8 = r7.audioPlayerDuration
            r6.audioPlayerDuration = r8
            r5.updatePlayingMessageProgress()
            goto L_0x0159
        L_0x0156:
            int r3 = r3 + 1
            goto L_0x011b
        L_0x0159:
            int r1 = r1 + 1
            goto L_0x010f
        L_0x015c:
            im.bclpbkiauv.ui.components.RecyclerListView[] r0 = r9.listView
            if (r0 == 0) goto L_0x018d
            r0 = 0
        L_0x0161:
            im.bclpbkiauv.ui.components.RecyclerListView[] r1 = r9.listView
            int r2 = r1.length
            if (r0 >= r2) goto L_0x018d
            r1 = r1[r0]
            int r1 = r1.getChildCount()
            r2 = 0
        L_0x016d:
            if (r2 >= r1) goto L_0x018a
            im.bclpbkiauv.ui.components.RecyclerListView[] r3 = r9.listView
            r3 = r3[r0]
            android.view.View r3 = r3.getChildAt(r2)
            boolean r5 = r3 instanceof im.bclpbkiauv.ui.ArticleViewer.BlockAudioCell
            if (r5 == 0) goto L_0x0187
            r5 = r3
            im.bclpbkiauv.ui.ArticleViewer$BlockAudioCell r5 = (im.bclpbkiauv.ui.ArticleViewer.BlockAudioCell) r5
            im.bclpbkiauv.messenger.MessageObject r6 = r5.getMessageObject()
            if (r6 == 0) goto L_0x0187
            r5.updateButtonState(r4)
        L_0x0187:
            int r2 = r2 + 1
            goto L_0x016d
        L_0x018a:
            int r0 = r0 + 1
            goto L_0x0161
        L_0x018d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    public void updatePaintSize() {
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_size", this.selectedFontSize).commit();
        for (int i = 0; i < 2; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    private void updatePaintFonts() {
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_type", this.selectedFont).commit();
        Typeface typefaceNormal = this.selectedFont == 0 ? Typeface.DEFAULT : Typeface.SERIF;
        Typeface typefaceItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/ritalic.ttf") : Typeface.create(C.SERIF_NAME, 2);
        Typeface typefaceBold = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmedium.ttf") : Typeface.create(C.SERIF_NAME, 1);
        Typeface typefaceBoldItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf") : Typeface.create(C.SERIF_NAME, 3);
        for (int a = 0; a < quoteTextPaints.size(); a++) {
            updateFontEntry(quoteTextPaints.keyAt(a), quoteTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a2 = 0; a2 < preformattedTextPaints.size(); a2++) {
            updateFontEntry(preformattedTextPaints.keyAt(a2), preformattedTextPaints.valueAt(a2), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a3 = 0; a3 < paragraphTextPaints.size(); a3++) {
            updateFontEntry(paragraphTextPaints.keyAt(a3), paragraphTextPaints.valueAt(a3), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a4 = 0; a4 < listTextPaints.size(); a4++) {
            updateFontEntry(listTextPaints.keyAt(a4), listTextPaints.valueAt(a4), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a5 = 0; a5 < embedPostTextPaints.size(); a5++) {
            updateFontEntry(embedPostTextPaints.keyAt(a5), embedPostTextPaints.valueAt(a5), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a6 = 0; a6 < mediaCaptionTextPaints.size(); a6++) {
            updateFontEntry(mediaCaptionTextPaints.keyAt(a6), mediaCaptionTextPaints.valueAt(a6), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a7 = 0; a7 < mediaCreditTextPaints.size(); a7++) {
            updateFontEntry(mediaCreditTextPaints.keyAt(a7), mediaCreditTextPaints.valueAt(a7), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a8 = 0; a8 < photoCaptionTextPaints.size(); a8++) {
            updateFontEntry(photoCaptionTextPaints.keyAt(a8), photoCaptionTextPaints.valueAt(a8), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a9 = 0; a9 < photoCreditTextPaints.size(); a9++) {
            updateFontEntry(photoCreditTextPaints.keyAt(a9), photoCreditTextPaints.valueAt(a9), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a10 = 0; a10 < authorTextPaints.size(); a10++) {
            updateFontEntry(authorTextPaints.keyAt(a10), authorTextPaints.valueAt(a10), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a11 = 0; a11 < footerTextPaints.size(); a11++) {
            updateFontEntry(footerTextPaints.keyAt(a11), footerTextPaints.valueAt(a11), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a12 = 0; a12 < embedPostCaptionTextPaints.size(); a12++) {
            updateFontEntry(embedPostCaptionTextPaints.keyAt(a12), embedPostCaptionTextPaints.valueAt(a12), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a13 = 0; a13 < relatedArticleTextPaints.size(); a13++) {
            updateFontEntry(relatedArticleTextPaints.keyAt(a13), relatedArticleTextPaints.valueAt(a13), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a14 = 0; a14 < detailsTextPaints.size(); a14++) {
            updateFontEntry(detailsTextPaints.keyAt(a14), detailsTextPaints.valueAt(a14), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a15 = 0; a15 < tableTextPaints.size(); a15++) {
            updateFontEntry(tableTextPaints.keyAt(a15), tableTextPaints.valueAt(a15), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
    }

    private void updateFontEntry(int flags, TextPaint paint, Typeface typefaceNormal, Typeface typefaceBoldItalic, Typeface typefaceBold, Typeface typefaceItalic) {
        if ((flags & 1) != 0 && (flags & 2) != 0) {
            paint.setTypeface(typefaceBoldItalic);
        } else if ((flags & 1) != 0) {
            paint.setTypeface(typefaceBold);
        } else if ((flags & 2) != 0) {
            paint.setTypeface(typefaceItalic);
        } else if ((flags & 4) == 0) {
            paint.setTypeface(typefaceNormal);
        }
    }

    /* access modifiers changed from: private */
    public int getSelectedColor() {
        int currentColor = this.selectedColor;
        if (!this.nightModeEnabled || currentColor == 2) {
            return currentColor;
        }
        if (Theme.selectedAutoNightType == 0) {
            int hour = Calendar.getInstance().get(11);
            if ((hour < 22 || hour > 24) && (hour < 0 || hour > 6)) {
                return currentColor;
            }
            return 2;
        } else if (Theme.isCurrentThemeNight()) {
            return 2;
        } else {
            return currentColor;
        }
    }

    private void updatePaintColors() {
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_color", this.selectedColor).commit();
        int currentColor = getSelectedColor();
        if (currentColor == 0) {
            this.backgroundPaint.setColor(-1);
            int i = 0;
            while (true) {
                RecyclerListView[] recyclerListViewArr = this.listView;
                if (i >= recyclerListViewArr.length) {
                    break;
                }
                recyclerListViewArr[i].setGlowColor(-657673);
                i++;
            }
        } else if (currentColor == 1) {
            this.backgroundPaint.setColor(-659492);
            int i2 = 0;
            while (true) {
                RecyclerListView[] recyclerListViewArr2 = this.listView;
                if (i2 >= recyclerListViewArr2.length) {
                    break;
                }
                recyclerListViewArr2[i2].setGlowColor(-659492);
                i2++;
            }
        } else if (currentColor == 2) {
            this.backgroundPaint.setColor(-15461356);
            int i3 = 0;
            while (true) {
                RecyclerListView[] recyclerListViewArr3 = this.listView;
                if (i3 >= recyclerListViewArr3.length) {
                    break;
                }
                recyclerListViewArr3[i3].setGlowColor(-15461356);
                i3++;
            }
        }
        TextPaint textPaint = listTextPointerPaint;
        if (textPaint != null) {
            textPaint.setColor(getTextColor());
        }
        TextPaint textPaint2 = listTextNumPaint;
        if (textPaint2 != null) {
            textPaint2.setColor(getTextColor());
        }
        TextPaint textPaint3 = embedPostAuthorPaint;
        if (textPaint3 != null) {
            textPaint3.setColor(getTextColor());
        }
        TextPaint textPaint4 = channelNamePaint;
        if (textPaint4 != null) {
            if (this.channelBlock == null) {
                textPaint4.setColor(getTextColor());
            } else {
                textPaint4.setColor(-1);
            }
        }
        TextPaint textPaint5 = relatedArticleHeaderPaint;
        if (textPaint5 != null) {
            textPaint5.setColor(getTextColor());
        }
        TextPaint textPaint6 = relatedArticleTextPaint;
        if (textPaint6 != null) {
            textPaint6.setColor(getGrayTextColor());
        }
        TextPaint textPaint7 = embedPostDatePaint;
        if (textPaint7 != null) {
            if (currentColor == 0) {
                textPaint7.setColor(-7366752);
            } else {
                textPaint7.setColor(getGrayTextColor());
            }
        }
        createPaint(true);
        setMapColors(titleTextPaints);
        setMapColors(kickerTextPaints);
        setMapColors(subtitleTextPaints);
        setMapColors(headerTextPaints);
        setMapColors(subheaderTextPaints);
        setMapColors(quoteTextPaints);
        setMapColors(preformattedTextPaints);
        setMapColors(paragraphTextPaints);
        setMapColors(listTextPaints);
        setMapColors(embedPostTextPaints);
        setMapColors(mediaCaptionTextPaints);
        setMapColors(mediaCreditTextPaints);
        setMapColors(photoCaptionTextPaints);
        setMapColors(photoCreditTextPaints);
        setMapColors(authorTextPaints);
        setMapColors(footerTextPaints);
        setMapColors(embedPostCaptionTextPaints);
        setMapColors(relatedArticleTextPaints);
        setMapColors(detailsTextPaints);
        setMapColors(tableTextPaints);
    }

    private void setMapColors(SparseArray<TextPaint> map) {
        for (int a = 0; a < map.size(); a++) {
            int flags = map.keyAt(a);
            TextPaint paint = map.valueAt(a);
            if ((flags & 8) == 0 && (flags & 512) == 0) {
                paint.setColor(getTextColor());
            } else {
                paint.setColor(getLinkTextColor());
            }
        }
    }

    public void setParentActivity(Activity activity, BaseFragment fragment) {
        Activity activity2 = activity;
        this.parentFragment = fragment;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.leftImage.setCurrentAccount(i);
        this.rightImage.setCurrentAccount(this.currentAccount);
        this.centerImage.setCurrentAccount(this.currentAccount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        if (this.parentActivity == activity2) {
            updatePaintColors();
            return;
        }
        this.parentActivity = activity2;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
        this.selectedFontSize = sharedPreferences.getInt("font_size", 2);
        this.selectedFont = sharedPreferences.getInt("font_type", 0);
        this.selectedColor = sharedPreferences.getInt("font_color", 0);
        this.nightModeEnabled = sharedPreferences.getBoolean("nightModeEnabled", false);
        createPaint(false);
        this.backgroundPaint = new Paint();
        this.layerShadowDrawable = activity.getResources().getDrawable(R.drawable.layer_shadow);
        this.slideDotDrawable = activity.getResources().getDrawable(R.drawable.slide_dot_small);
        this.slideDotBigDrawable = activity.getResources().getDrawable(R.drawable.slide_dot_big);
        this.scrimPaint = new Paint();
        WindowView windowView2 = new WindowView(activity2);
        this.windowView = windowView2;
        windowView2.setWillNotDraw(false);
        this.windowView.setClipChildren(true);
        this.windowView.setFocusable(false);
        AnonymousClass4 r6 = new FrameLayout(activity2) {
            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                int clipRight;
                int clipLeft;
                float opacity;
                Canvas canvas2 = canvas;
                View view = child;
                if (!ArticleViewer.this.windowView.movingPage) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                int width = getMeasuredWidth();
                int translationX = (int) ArticleViewer.this.listView[0].getTranslationX();
                int clipRight2 = width;
                if (view == ArticleViewer.this.listView[1]) {
                    clipLeft = 0;
                    clipRight = translationX;
                } else if (view == ArticleViewer.this.listView[0]) {
                    clipLeft = translationX;
                    clipRight = clipRight2;
                } else {
                    clipLeft = 0;
                    clipRight = clipRight2;
                }
                int restoreCount = canvas.save();
                canvas2.clipRect(clipLeft, 0, clipRight, getHeight());
                boolean result = super.drawChild(canvas, child, drawingTime);
                canvas2.restoreToCount(restoreCount);
                if (translationX != 0) {
                    if (view == ArticleViewer.this.listView[0]) {
                        float alpha = Math.max(0.0f, Math.min(((float) (width - translationX)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                        ArticleViewer.this.layerShadowDrawable.setBounds(translationX - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                        ArticleViewer.this.layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                        ArticleViewer.this.layerShadowDrawable.draw(canvas2);
                    } else if (view == ArticleViewer.this.listView[1]) {
                        float opacity2 = Math.min(0.8f, ((float) (width - translationX)) / ((float) width));
                        if (opacity2 < 0.0f) {
                            opacity = 0.0f;
                        } else {
                            opacity = opacity2;
                        }
                        ArticleViewer.this.scrimPaint.setColor(((int) (153.0f * opacity)) << 24);
                        canvas.drawRect((float) clipLeft, 0.0f, (float) clipRight, (float) getHeight(), ArticleViewer.this.scrimPaint);
                    }
                }
                return result;
            }
        };
        this.containerView = r6;
        this.windowView.addView(r6, LayoutHelper.createFrame(-1, -1, 51));
        this.containerView.setFitsSystemWindows(true);
        if (Build.VERSION.SDK_INT >= 21) {
            this.containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return ArticleViewer.this.lambda$setParentActivity$7$ArticleViewer(view, windowInsets);
                }
            });
        }
        this.containerView.setSystemUiVisibility(1028);
        View view = new View(activity2);
        this.photoContainerBackground = view;
        view.setVisibility(4);
        this.photoContainerBackground.setBackgroundDrawable(this.photoBackgroundDrawable);
        this.windowView.addView(this.photoContainerBackground, LayoutHelper.createFrame(-1, -1, 51));
        ClippingImageView clippingImageView = new ClippingImageView(activity2);
        this.animatingImageView = clippingImageView;
        clippingImageView.setAnimationValues(this.animationValues);
        this.animatingImageView.setVisibility(8);
        this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
        AnonymousClass5 r62 = new FrameLayoutDrawer(activity2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int y = (bottom - top) - ArticleViewer.this.captionTextView.getMeasuredHeight();
                int y2 = (bottom - top) - ArticleViewer.this.groupedPhotosListView.getMeasuredHeight();
                if (ArticleViewer.this.bottomLayout.getVisibility() == 0) {
                    y -= ArticleViewer.this.bottomLayout.getMeasuredHeight();
                    y2 -= ArticleViewer.this.bottomLayout.getMeasuredHeight();
                }
                if (!ArticleViewer.this.groupedPhotosListView.currentPhotos.isEmpty()) {
                    y -= ArticleViewer.this.groupedPhotosListView.getMeasuredHeight();
                }
                ArticleViewer.this.captionTextView.layout(0, y, ArticleViewer.this.captionTextView.getMeasuredWidth(), ArticleViewer.this.captionTextView.getMeasuredHeight() + y);
                ArticleViewer.this.captionTextViewNext.layout(0, y, ArticleViewer.this.captionTextViewNext.getMeasuredWidth(), ArticleViewer.this.captionTextViewNext.getMeasuredHeight() + y);
                ArticleViewer.this.groupedPhotosListView.layout(0, y2, ArticleViewer.this.groupedPhotosListView.getMeasuredWidth(), ArticleViewer.this.groupedPhotosListView.getMeasuredHeight() + y2);
            }
        };
        this.photoContainerView = r62;
        r62.setVisibility(4);
        this.photoContainerView.setWillNotDraw(false);
        this.windowView.addView(this.photoContainerView, LayoutHelper.createFrame(-1, -1, 51));
        FrameLayout frameLayout = new FrameLayout(activity2);
        this.fullscreenVideoContainer = frameLayout;
        frameLayout.setBackgroundColor(-16777216);
        this.fullscreenVideoContainer.setVisibility(4);
        this.windowView.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        AspectRatioFrameLayout aspectRatioFrameLayout2 = new AspectRatioFrameLayout(activity2);
        this.fullscreenAspectRatioView = aspectRatioFrameLayout2;
        aspectRatioFrameLayout2.setVisibility(8);
        this.fullscreenVideoContainer.addView(this.fullscreenAspectRatioView, LayoutHelper.createFrame(-1, -1, 17));
        this.fullscreenTextureView = new TextureView(activity2);
        this.listView = new RecyclerListView[2];
        this.adapter = new WebpageAdapter[2];
        this.layoutManager = new LinearLayoutManager[2];
        int i2 = 0;
        while (true) {
            RecyclerListView[] recyclerListViewArr = this.listView;
            if (i2 >= recyclerListViewArr.length) {
                break;
            }
            recyclerListViewArr[i2] = new RecyclerListView(activity2) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    int count = getChildCount();
                    int a = 0;
                    while (a < count) {
                        View child = getChildAt(a);
                        if (!(child.getTag() instanceof Integer) || ((Integer) child.getTag()).intValue() != 90 || child.getBottom() >= getMeasuredHeight()) {
                            a++;
                        } else {
                            int height = getMeasuredHeight();
                            child.layout(0, height - child.getMeasuredHeight(), child.getMeasuredWidth(), height);
                            return;
                        }
                    }
                }

                public boolean onInterceptTouchEvent(MotionEvent e) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (e.getAction() == 1 || e.getAction() == 3))) {
                        TextPaintUrlSpan unused = ArticleViewer.this.pressedLink = null;
                        DrawingText unused2 = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused3 = ArticleViewer.this.pressedLinkOwnerView = null;
                    } else if (!(ArticleViewer.this.pressedLinkOwnerLayout == null || ArticleViewer.this.pressedLink == null || e.getAction() != 1)) {
                        ArticleViewer articleViewer = ArticleViewer.this;
                        boolean unused4 = articleViewer.checkLayoutForLinks(e, articleViewer.pressedLinkOwnerView, ArticleViewer.this.pressedLinkOwnerLayout, 0, 0);
                    }
                    return super.onInterceptTouchEvent(e);
                }

                public boolean onTouchEvent(MotionEvent e) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (e.getAction() == 1 || e.getAction() == 3))) {
                        TextPaintUrlSpan unused = ArticleViewer.this.pressedLink = null;
                        DrawingText unused2 = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused3 = ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    return super.onTouchEvent(e);
                }

                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (ArticleViewer.this.windowView.movingPage) {
                        ArticleViewer.this.containerView.invalidate();
                        ArticleViewer articleViewer = ArticleViewer.this;
                        articleViewer.setCurrentHeaderHeight((int) (((float) articleViewer.windowView.startMovingHeaderHeight) + (((float) (AndroidUtilities.dp(56.0f) - ArticleViewer.this.windowView.startMovingHeaderHeight)) * (translationX / ((float) getMeasuredWidth())))));
                    }
                }
            };
            ((DefaultItemAnimator) this.listView[i2].getItemAnimator()).setDelayAnimations(false);
            RecyclerListView recyclerListView = this.listView[i2];
            LinearLayoutManager[] linearLayoutManagerArr = this.layoutManager;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.parentActivity, 1, false);
            linearLayoutManagerArr[i2] = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            WebpageAdapter[] webpageAdapterArr = this.adapter;
            WebpageAdapter webpageAdapter = new WebpageAdapter(this.parentActivity);
            webpageAdapterArr[i2] = webpageAdapter;
            WebpageAdapter webpageAdapter2 = webpageAdapter;
            this.listView[i2].setAdapter(webpageAdapter2);
            this.listView[i2].setClipToPadding(false);
            this.listView[i2].setVisibility(i2 == 0 ? 0 : 8);
            this.listView[i2].setPadding(0, AndroidUtilities.dp(56.0f), 0, 0);
            this.listView[i2].setTopGlowOffset(AndroidUtilities.dp(56.0f));
            this.containerView.addView(this.listView[i2], LayoutHelper.createFrame(-1, -1.0f));
            this.listView[i2].setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                public final boolean onItemClick(View view, int i) {
                    return ArticleViewer.this.lambda$setParentActivity$8$ArticleViewer(view, i);
                }
            });
            this.listView[i2].setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(webpageAdapter2) {
                private final /* synthetic */ ArticleViewer.WebpageAdapter f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(View view, int i) {
                    ArticleViewer.this.lambda$setParentActivity$11$ArticleViewer(this.f$1, view, i);
                }
            });
            this.listView[i2].setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView.getChildCount() != 0) {
                        ArticleViewer.this.headerView.invalidate();
                        ArticleViewer.this.checkScroll(dy);
                    }
                }
            });
            i2++;
        }
        this.headerPaint.setColor(-16777216);
        this.statusBarPaint.setColor(-16777216);
        this.headerProgressPaint.setColor(-14408666);
        AnonymousClass8 r63 = new FrameLayout(activity2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                View view;
                float viewProgress;
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                Canvas canvas2 = canvas;
                canvas2.drawRect(0.0f, 0.0f, (float) width, (float) height, ArticleViewer.this.headerPaint);
                if (ArticleViewer.this.layoutManager != null) {
                    int first = ArticleViewer.this.layoutManager[0].findFirstVisibleItemPosition();
                    int last = ArticleViewer.this.layoutManager[0].findLastVisibleItemPosition();
                    int count = ArticleViewer.this.layoutManager[0].getItemCount();
                    if (last >= count - 2) {
                        view = ArticleViewer.this.layoutManager[0].findViewByPosition(count - 2);
                    } else {
                        view = ArticleViewer.this.layoutManager[0].findViewByPosition(first);
                    }
                    if (view != null) {
                        float itemProgress = ((float) width) / ((float) (count - 1));
                        int childCount = ArticleViewer.this.layoutManager[0].getChildCount();
                        float viewHeight = (float) view.getMeasuredHeight();
                        if (last >= count - 2) {
                            viewProgress = ((((float) ((count - 2) - first)) * itemProgress) * ((float) (ArticleViewer.this.listView[0].getMeasuredHeight() - view.getTop()))) / viewHeight;
                        } else {
                            viewProgress = (1.0f - ((((float) Math.min(0, view.getTop() - ArticleViewer.this.listView[0].getPaddingTop())) + viewHeight) / viewHeight)) * itemProgress;
                        }
                        float f = (float) height;
                        canvas.drawRect(0.0f, 0.0f, (((float) first) * itemProgress) + viewProgress, f, ArticleViewer.this.headerProgressPaint);
                    }
                }
            }
        };
        this.headerView = r63;
        r63.setOnTouchListener($$Lambda$ArticleViewer$wifO21KbW_Pr7RzbHR95LT7Gg60.INSTANCE);
        this.headerView.setWillNotDraw(false);
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, 56.0f));
        ImageView imageView = new ImageView(activity2);
        this.backButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        BackDrawable backDrawable2 = new BackDrawable(false);
        this.backDrawable = backDrawable2;
        backDrawable2.setAnimationTime(200.0f);
        this.backDrawable.setColor(-5000269);
        this.backDrawable.setRotated(false);
        this.backButton.setImageDrawable(this.backDrawable);
        this.backButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.headerView.addView(this.backButton, LayoutHelper.createFrame(54, 56.0f));
        this.backButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArticleViewer.this.lambda$setParentActivity$13$ArticleViewer(view);
            }
        });
        this.backButton.setContentDescription(LocaleController.getString("AccDescrGoBack", R.string.AccDescrGoBack));
        SimpleTextView simpleTextView = new SimpleTextView(activity2);
        this.titleTextView = simpleTextView;
        simpleTextView.setGravity(19);
        this.titleTextView.setTextSize(20);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setTextColor(-5000269);
        this.titleTextView.setPivotX(0.0f);
        this.titleTextView.setPivotY((float) AndroidUtilities.dp(28.0f));
        this.headerView.addView(this.titleTextView, LayoutHelper.createFrame(-1.0f, 56.0f, 51, 72.0f, 0.0f, 96.0f, 0.0f));
        LineProgressView lineProgressView2 = new LineProgressView(activity2);
        this.lineProgressView = lineProgressView2;
        lineProgressView2.setProgressColor(-1);
        this.lineProgressView.setPivotX(0.0f);
        this.lineProgressView.setPivotY((float) AndroidUtilities.dp(2.0f));
        this.headerView.addView(this.lineProgressView, LayoutHelper.createFrame(-1.0f, 2.0f, 83, 0.0f, 0.0f, 0.0f, 1.0f));
        this.lineProgressTickRunnable = new Runnable() {
            public final void run() {
                ArticleViewer.this.lambda$setParentActivity$14$ArticleViewer();
            }
        };
        LinearLayout settingsContainer = new LinearLayout(this.parentActivity);
        settingsContainer.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        settingsContainer.setOrientation(1);
        int a = 0;
        while (a < 3) {
            this.colorCells[a] = new ColorCell(this.parentActivity);
            if (a == 0) {
                ImageView imageView2 = new ImageView(this.parentActivity);
                this.nightModeImageView = imageView2;
                imageView2.setScaleType(ImageView.ScaleType.CENTER);
                this.nightModeImageView.setImageResource(R.drawable.moon);
                this.nightModeImageView.setColorFilter(new PorterDuffColorFilter((!this.nightModeEnabled || this.selectedColor == 2) ? -3355444 : -15428119, PorterDuff.Mode.MULTIPLY));
                this.nightModeImageView.setBackgroundDrawable(Theme.createSelectorDrawable(251658240));
                this.colorCells[a].addView(this.nightModeImageView, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 48));
                this.nightModeImageView.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ArticleViewer.this.lambda$setParentActivity$15$ArticleViewer(view);
                    }
                });
                this.colorCells[a].setTextAndColor(LocaleController.getString("ColorWhite", R.string.ColorWhite), -1);
            } else if (a == 1) {
                this.colorCells[a].setTextAndColor(LocaleController.getString("ColorSepia", R.string.ColorSepia), -1382967);
            } else if (a == 2) {
                this.colorCells[a].setTextAndColor(LocaleController.getString("ColorDark", R.string.ColorDark), -14474461);
            }
            this.colorCells[a].select(a == this.selectedColor);
            this.colorCells[a].setTag(Integer.valueOf(a));
            this.colorCells[a].setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ArticleViewer.this.lambda$setParentActivity$16$ArticleViewer(view);
                }
            });
            settingsContainer.addView(this.colorCells[a], LayoutHelper.createLinear(-1, 50));
            a++;
        }
        updateNightModeButton();
        View divider = new View(this.parentActivity);
        divider.setBackgroundColor(-2039584);
        settingsContainer.addView(divider, LayoutHelper.createLinear(-1, 1, 15.0f, 4.0f, 15.0f, 4.0f));
        divider.getLayoutParams().height = 1;
        int a2 = 0;
        while (a2 < 2) {
            this.fontCells[a2] = new FontCell(this, this.parentActivity);
            if (a2 == 0) {
                this.fontCells[a2].setTextAndTypeface("Roboto", Typeface.DEFAULT);
            } else if (a2 == 1) {
                this.fontCells[a2].setTextAndTypeface("Serif", Typeface.SERIF);
            }
            this.fontCells[a2].select(a2 == this.selectedFont);
            this.fontCells[a2].setTag(Integer.valueOf(a2));
            this.fontCells[a2].setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ArticleViewer.this.lambda$setParentActivity$17$ArticleViewer(view);
                }
            });
            settingsContainer.addView(this.fontCells[a2], LayoutHelper.createLinear(-1, 50));
            a2++;
        }
        View divider2 = new View(this.parentActivity);
        divider2.setBackgroundColor(-2039584);
        settingsContainer.addView(divider2, LayoutHelper.createLinear(-1, 1, 15.0f, 4.0f, 15.0f, 4.0f));
        divider2.getLayoutParams().height = 1;
        TextView textView = new TextView(this.parentActivity);
        textView.setTextColor(-14606047);
        textView.setTextSize(1, 16.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView.setText(LocaleController.getString("FontSize", R.string.FontSize));
        settingsContainer.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 12, 17, 0));
        settingsContainer.addView(new SizeChooseView(this.parentActivity), LayoutHelper.createLinear(-1, 38, 0.0f, 0.0f, 0.0f, 1.0f));
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(this.parentActivity, (ActionBarMenu) null, Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, -1);
        this.settingsButton = actionBarMenuItem;
        actionBarMenuItem.setPopupAnimationEnabled(false);
        this.settingsButton.setLayoutInScreen(true);
        TextView textView2 = new TextView(this.parentActivity);
        textView2.setTextSize(1, 18.0f);
        textView2.setText("Aa");
        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView2.setTextColor(-5000269);
        textView2.setGravity(17);
        textView2.setImportantForAccessibility(2);
        this.settingsButton.addView(textView2, LayoutHelper.createFrame(-1, -1.0f));
        this.settingsButton.addSubItem((View) settingsContainer, AndroidUtilities.dp(220.0f), -2);
        this.settingsButton.redrawPopup(-1);
        this.settingsButton.setContentDescription(LocaleController.getString("Settings", R.string.Settings));
        this.headerView.addView(this.settingsButton, LayoutHelper.createFrame(48.0f, 56.0f, 53, 0.0f, 0.0f, 56.0f, 0.0f));
        FrameLayout frameLayout2 = new FrameLayout(activity2);
        this.shareContainer = frameLayout2;
        this.headerView.addView(frameLayout2, LayoutHelper.createFrame(48, 56, 53));
        this.shareContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArticleViewer.this.lambda$setParentActivity$18$ArticleViewer(view);
            }
        });
        ImageView imageView3 = new ImageView(activity2);
        this.shareButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.shareButton.setImageResource(R.drawable.ic_share_article);
        this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.shareButton.setContentDescription(LocaleController.getString("ShareFile", R.string.ShareFile));
        this.shareContainer.addView(this.shareButton, LayoutHelper.createFrame(48, 56.0f));
        ContextProgressView contextProgressView = new ContextProgressView(activity2, 2);
        this.progressView = contextProgressView;
        contextProgressView.setVisibility(8);
        this.shareContainer.addView(this.progressView, LayoutHelper.createFrame(48, 56.0f));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.windowLayoutParams = layoutParams;
        layoutParams.height = -1;
        this.windowLayoutParams.format = -3;
        this.windowLayoutParams.width = -1;
        this.windowLayoutParams.gravity = 51;
        this.windowLayoutParams.type = 99;
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowLayoutParams.flags = -2147417848;
            if (Build.VERSION.SDK_INT >= 28) {
                this.windowLayoutParams.layoutInDisplayCutoutMode = 1;
            }
        } else {
            this.windowLayoutParams.flags = 8;
        }
        if (progressDrawables == null) {
            Drawable[] drawableArr = new Drawable[4];
            progressDrawables = drawableArr;
            drawableArr[0] = this.parentActivity.getResources().getDrawable(R.drawable.circle_big);
            progressDrawables[1] = this.parentActivity.getResources().getDrawable(R.drawable.cancel_big);
            progressDrawables[2] = this.parentActivity.getResources().getDrawable(R.drawable.load_big);
            progressDrawables[3] = this.parentActivity.getResources().getDrawable(R.drawable.play_big);
        }
        this.scroller = new Scroller(activity2);
        this.blackPaint.setColor(-16777216);
        ActionBar actionBar2 = new ActionBar(activity2);
        this.actionBar = actionBar2;
        actionBar2.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, 1, 1));
        this.photoContainerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ArticleViewer.this.closePhoto(true);
                } else if (id == 1) {
                    if (Build.VERSION.SDK_INT < 23 || ArticleViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        ArticleViewer articleViewer = ArticleViewer.this;
                        File f = articleViewer.getMediaFile(articleViewer.currentIndex);
                        if (f == null || !f.exists()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) ArticleViewer.this.parentActivity);
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                            builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                            ArticleViewer.this.showDialog(builder.create());
                            return;
                        }
                        String file = f.toString();
                        Activity access$2500 = ArticleViewer.this.parentActivity;
                        ArticleViewer articleViewer2 = ArticleViewer.this;
                        MediaController.saveFile(file, access$2500, articleViewer2.isMediaVideo(articleViewer2.currentIndex) ? 1 : 0, (String) null, (String) null);
                        return;
                    }
                    ArticleViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                } else if (id == 2) {
                    ArticleViewer.this.onSharePressed();
                } else if (id == 3) {
                    try {
                        AndroidUtilities.openForView(ArticleViewer.this.getMedia(ArticleViewer.this.currentIndex), ArticleViewer.this.parentActivity);
                        ArticleViewer.this.closePhoto(false);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }

            public boolean canOpenMenu() {
                ArticleViewer articleViewer = ArticleViewer.this;
                File f = articleViewer.getMediaFile(articleViewer.currentIndex);
                return f != null && f.exists();
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.addItem(2, (int) R.drawable.share);
        ActionBarMenuItem addItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        this.menuItem = addItem;
        addItem.setLayoutInScreen(true);
        this.menuItem.addSubItem(3, (int) R.drawable.msg_openin, (CharSequence) LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp)).setColors(-328966, -328966);
        this.menuItem.addSubItem(1, (int) R.drawable.msg_gallery, (CharSequence) LocaleController.getString("SaveToGallery", R.string.SaveToGallery)).setColors(-328966, -328966);
        this.menuItem.redrawPopup(-115203550);
        FrameLayout frameLayout3 = new FrameLayout(this.parentActivity);
        this.bottomLayout = frameLayout3;
        frameLayout3.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.photoContainerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        GroupedPhotosListView groupedPhotosListView2 = new GroupedPhotosListView(this.parentActivity);
        this.groupedPhotosListView = groupedPhotosListView2;
        this.photoContainerView.addView(groupedPhotosListView2, LayoutHelper.createFrame(-1.0f, 62.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
        this.groupedPhotosListView.setDelegate(new GroupedPhotosListView.GroupedPhotosListViewDelegate() {
            public int getCurrentIndex() {
                return ArticleViewer.this.currentIndex;
            }

            public int getCurrentAccount() {
                return ArticleViewer.this.currentAccount;
            }

            public int getAvatarsDialogId() {
                return 0;
            }

            public int getSlideshowMessageId() {
                return 0;
            }

            public ArrayList<ImageLocation> getImagesArrLocations() {
                return null;
            }

            public ArrayList<MessageObject> getImagesArr() {
                return null;
            }

            public ArrayList<TLRPC.PageBlock> getPageBlockArr() {
                return ArticleViewer.this.imagesArr;
            }

            public Object getParentObject() {
                return ArticleViewer.this.currentPage;
            }

            public void setCurrentIndex(int index) {
                int unused = ArticleViewer.this.currentIndex = -1;
                if (ArticleViewer.this.currentThumb != null) {
                    ArticleViewer.this.currentThumb.release();
                    ImageReceiver.BitmapHolder unused2 = ArticleViewer.this.currentThumb = null;
                }
                ArticleViewer.this.setImageIndex(index, true);
            }
        });
        TextView textView3 = new TextView(activity2);
        this.captionTextViewNext = textView3;
        textView3.setMaxLines(10);
        this.captionTextViewNext.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.captionTextViewNext.setMovementMethod(new LinkMovementMethodMy());
        this.captionTextViewNext.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        this.captionTextViewNext.setLinkTextColor(-1);
        this.captionTextViewNext.setTextColor(-1);
        this.captionTextViewNext.setHighlightColor(872415231);
        this.captionTextViewNext.setGravity(19);
        this.captionTextViewNext.setTextSize(1, 16.0f);
        this.captionTextViewNext.setVisibility(8);
        this.photoContainerView.addView(this.captionTextViewNext, LayoutHelper.createFrame(-1, -2, 83));
        TextView textView4 = new TextView(activity2);
        this.captionTextView = textView4;
        textView4.setMaxLines(10);
        this.captionTextView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.captionTextView.setMovementMethod(new LinkMovementMethodMy());
        this.captionTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        this.captionTextView.setLinkTextColor(-1);
        this.captionTextView.setTextColor(-1);
        this.captionTextView.setHighlightColor(872415231);
        this.captionTextView.setGravity(19);
        this.captionTextView.setTextSize(1, 16.0f);
        this.captionTextView.setVisibility(8);
        this.photoContainerView.addView(this.captionTextView, LayoutHelper.createFrame(-1, -2, 83));
        this.radialProgressViews[0] = new RadialProgressView(activity2, this.photoContainerView);
        this.radialProgressViews[0].setBackgroundState(0, false);
        this.radialProgressViews[1] = new RadialProgressView(activity2, this.photoContainerView);
        this.radialProgressViews[1].setBackgroundState(0, false);
        this.radialProgressViews[2] = new RadialProgressView(activity2, this.photoContainerView);
        this.radialProgressViews[2].setBackgroundState(0, false);
        SeekBar seekBar = new SeekBar(activity2);
        this.videoPlayerSeekbar = seekBar;
        seekBar.setColors(1728053247, 1728053247, -2764585, -1, -1);
        this.videoPlayerSeekbar.setDelegate(new SeekBar.SeekBarDelegate() {
            public /* synthetic */ void onSeekBarContinuousDrag(float f) {
                SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
            }

            public final void onSeekBarDrag(float f) {
                ArticleViewer.this.lambda$setParentActivity$19$ArticleViewer(f);
            }
        });
        AnonymousClass11 r5 = new FrameLayout(activity2) {
            public boolean onTouchEvent(MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (!ArticleViewer.this.videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - ((float) AndroidUtilities.dp(48.0f)), event.getY())) {
                    return super.onTouchEvent(event);
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                long duration;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (ArticleViewer.this.videoPlayer != null) {
                    duration = ArticleViewer.this.videoPlayer.getDuration();
                    if (duration == C.TIME_UNSET) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                long duration2 = duration / 1000;
                TextPaint paint = ArticleViewer.this.videoPlayerTime.getPaint();
                Object[] objArr = {Long.valueOf(duration2 / 60), Long.valueOf(duration2 % 60), Long.valueOf(duration2 / 60), Long.valueOf(duration2 % 60)};
                ArticleViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) paint.measureText(String.format("%02d:%02d / %02d:%02d", objArr)))), getMeasuredHeight());
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                float progress = 0.0f;
                if (ArticleViewer.this.videoPlayer != null) {
                    progress = ((float) ArticleViewer.this.videoPlayer.getCurrentPosition()) / ((float) ArticleViewer.this.videoPlayer.getDuration());
                }
                ArticleViewer.this.videoPlayerSeekbar.setProgress(progress);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
                ArticleViewer.this.videoPlayerSeekbar.draw(canvas);
                canvas.restore();
            }
        };
        this.videoPlayerControlFrameLayout = r5;
        r5.setWillNotDraw(false);
        this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
        ImageView imageView4 = new ImageView(activity2);
        this.videoPlayButton = imageView4;
        imageView4.setScaleType(ImageView.ScaleType.CENTER);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
        this.videoPlayButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArticleViewer.this.lambda$setParentActivity$20$ArticleViewer(view);
            }
        });
        TextView textView5 = new TextView(activity2);
        this.videoPlayerTime = textView5;
        textView5.setTextColor(-1);
        this.videoPlayerTime.setGravity(16);
        this.videoPlayerTime.setTextSize(1, 13.0f);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2.0f, -1.0f, 53, 0.0f, 0.0f, 8.0f, 0.0f));
        GestureDetector gestureDetector2 = new GestureDetector(activity2, this);
        this.gestureDetector = gestureDetector2;
        gestureDetector2.setOnDoubleTapListener(this);
        this.centerImage.setParentView(this.photoContainerView);
        this.centerImage.setCrossfadeAlpha((byte) 2);
        this.centerImage.setInvalidateAll(true);
        this.leftImage.setParentView(this.photoContainerView);
        this.leftImage.setCrossfadeAlpha((byte) 2);
        this.leftImage.setInvalidateAll(true);
        this.rightImage.setParentView(this.photoContainerView);
        this.rightImage.setCrossfadeAlpha((byte) 2);
        this.rightImage.setInvalidateAll(true);
        updatePaintColors();
    }

    public /* synthetic */ WindowInsets lambda$setParentActivity$7$ArticleViewer(View v, WindowInsets insets) {
        DisplayCutout cutout;
        List<Rect> rects;
        WindowInsets oldInsets = (WindowInsets) this.lastInsets;
        this.lastInsets = insets;
        if (oldInsets == null || !oldInsets.toString().equals(insets.toString())) {
            this.windowView.requestLayout();
        }
        if (Build.VERSION.SDK_INT >= 28 && (cutout = this.parentActivity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout()) != null && (rects = cutout.getBoundingRects()) != null && !rects.isEmpty()) {
            boolean z = false;
            if (rects.get(0).height() != 0) {
                z = true;
            }
            this.hasCutout = z;
        }
        return insets.consumeSystemWindowInsets();
    }

    public /* synthetic */ boolean lambda$setParentActivity$8$ArticleViewer(View view, int position) {
        if (!(view instanceof BlockRelatedArticlesCell)) {
            return false;
        }
        BlockRelatedArticlesCell cell = (BlockRelatedArticlesCell) view;
        showCopyPopup(cell.currentBlock.parent.articles.get(cell.currentBlock.num).url);
        return true;
    }

    public /* synthetic */ void lambda$setParentActivity$11$ArticleViewer(WebpageAdapter webpageAdapter, View view, int position) {
        if (position != webpageAdapter.localBlocks.size() || this.currentPage == null) {
            if (position >= 0 && position < webpageAdapter.localBlocks.size()) {
                TLRPC.PageBlock pageBlock = (TLRPC.PageBlock) webpageAdapter.localBlocks.get(position);
                TLRPC.PageBlock originalBlock = pageBlock;
                TLRPC.PageBlock pageBlock2 = getLastNonListPageBlock(pageBlock);
                if (pageBlock2 instanceof TL_pageBlockDetailsChild) {
                    pageBlock2 = ((TL_pageBlockDetailsChild) pageBlock2).block;
                }
                if (pageBlock2 instanceof TLRPC.TL_pageBlockChannel) {
                    MessagesController.getInstance(this.currentAccount).openByUserName(((TLRPC.TL_pageBlockChannel) pageBlock2).channel.username, this.parentFragment, 2);
                    close(false, true);
                } else if (pageBlock2 instanceof TL_pageBlockRelatedArticlesChild) {
                    TL_pageBlockRelatedArticlesChild pageBlockRelatedArticlesChild = (TL_pageBlockRelatedArticlesChild) pageBlock2;
                    openWebpageUrl(pageBlockRelatedArticlesChild.parent.articles.get(pageBlockRelatedArticlesChild.num).url, (String) null);
                } else if (pageBlock2 instanceof TLRPC.TL_pageBlockDetails) {
                    View view2 = getLastNonListCell(view);
                    if (view2 instanceof BlockDetailsCell) {
                        this.pressedLinkOwnerLayout = null;
                        this.pressedLinkOwnerView = null;
                        if (webpageAdapter.blocks.indexOf(originalBlock) >= 0) {
                            TLRPC.TL_pageBlockDetails pageBlockDetails = (TLRPC.TL_pageBlockDetails) pageBlock2;
                            pageBlockDetails.open = true ^ pageBlockDetails.open;
                            int oldCount = webpageAdapter.getItemCount();
                            webpageAdapter.updateRows();
                            int changeCount = Math.abs(webpageAdapter.getItemCount() - oldCount);
                            BlockDetailsCell cell = (BlockDetailsCell) view2;
                            cell.arrow.setAnimationProgressAnimated(pageBlockDetails.open ? 0.0f : 1.0f);
                            cell.invalidate();
                            if (changeCount == 0) {
                                return;
                            }
                            if (pageBlockDetails.open) {
                                webpageAdapter.notifyItemRangeInserted(position + 1, changeCount);
                            } else {
                                webpageAdapter.notifyItemRangeRemoved(position + 1, changeCount);
                            }
                        }
                    }
                }
            }
        } else if (this.previewsReqId == 0) {
            TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat("previews");
            if (object instanceof TLRPC.TL_user) {
                openPreviewsChat((TLRPC.User) object, this.currentPage.id);
                return;
            }
            int currentAccount2 = UserConfig.selectedAccount;
            long pageId = this.currentPage.id;
            showProgressView(true, true);
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            req.username = "previews";
            this.previewsReqId = ConnectionsManager.getInstance(currentAccount2).sendRequest(req, new RequestDelegate(currentAccount2, pageId) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ArticleViewer.this.lambda$null$10$ArticleViewer(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$10$ArticleViewer(int currentAccount2, long pageId, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response, currentAccount2, pageId) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ long f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ArticleViewer.this.lambda$null$9$ArticleViewer(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$ArticleViewer(TLObject response, int currentAccount2, long pageId) {
        if (this.previewsReqId != 0) {
            this.previewsReqId = 0;
            showProgressView(true, false);
            if (response != null) {
                TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                MessagesController.getInstance(currentAccount2).putUsers(res.users, false);
                MessagesStorage.getInstance(currentAccount2).putUsersAndChats(res.users, res.chats, false, true);
                if (!res.users.isEmpty()) {
                    openPreviewsChat(res.users.get(0), pageId);
                }
            }
        }
    }

    static /* synthetic */ boolean lambda$setParentActivity$12(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$setParentActivity$13$ArticleViewer(View v) {
        close(true, true);
    }

    public /* synthetic */ void lambda$setParentActivity$14$ArticleViewer() {
        float tick;
        float progressLeft = 0.7f - this.lineProgressView.getCurrentProgress();
        if (progressLeft > 0.0f) {
            if (progressLeft < 0.25f) {
                tick = 0.01f;
            } else {
                tick = 0.02f;
            }
            LineProgressView lineProgressView2 = this.lineProgressView;
            lineProgressView2.setProgress(lineProgressView2.getCurrentProgress() + tick, true);
            AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$15$ArticleViewer(View v) {
        this.nightModeEnabled = !this.nightModeEnabled;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putBoolean("nightModeEnabled", this.nightModeEnabled).commit();
        updateNightModeButton();
        updatePaintColors();
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
        if (this.nightModeEnabled != 0) {
            showNightModeHint();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$16$ArticleViewer(View v) {
        int num = ((Integer) v.getTag()).intValue();
        this.selectedColor = num;
        int a12 = 0;
        while (a12 < 3) {
            this.colorCells[a12].select(a12 == num);
            a12++;
        }
        updateNightModeButton();
        updatePaintColors();
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$17$ArticleViewer(View v) {
        int num = ((Integer) v.getTag()).intValue();
        this.selectedFont = num;
        int a1 = 0;
        while (a1 < 2) {
            this.fontCells[a1].select(a1 == num);
            a1++;
        }
        updatePaintFonts();
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$18$ArticleViewer(View v) {
        if (this.currentPage != null && this.parentActivity != null) {
            showDialog(new ShareAlert(this.parentActivity, (ArrayList<MessageObject>) null, this.currentPage.url, false, this.currentPage.url, true));
        }
    }

    public /* synthetic */ void lambda$setParentActivity$19$ArticleViewer(float progress) {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.seekTo((long) ((int) (((float) videoPlayer2.getDuration()) * progress)));
        }
    }

    public /* synthetic */ void lambda$setParentActivity$20$ArticleViewer(View v) {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 == null) {
            return;
        }
        if (this.isPlaying) {
            videoPlayer2.pause();
        } else {
            videoPlayer2.play();
        }
    }

    private void showNightModeHint() {
        if (this.parentActivity != null && this.nightModeHintView == null && this.nightModeEnabled) {
            FrameLayout frameLayout = new FrameLayout(this.parentActivity);
            this.nightModeHintView = frameLayout;
            frameLayout.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
            this.containerView.addView(this.nightModeHintView, LayoutHelper.createFrame(-1, -2, 83));
            ImageView nightModeImageView2 = new ImageView(this.parentActivity);
            nightModeImageView2.setScaleType(ImageView.ScaleType.CENTER);
            nightModeImageView2.setImageResource(R.drawable.moon);
            int i = 5;
            int i2 = 56;
            this.nightModeHintView.addView(nightModeImageView2, LayoutHelper.createFrame(56, 56, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView = new TextView(this.parentActivity);
            textView.setText(LocaleController.getString("InstantViewNightMode", R.string.InstantViewNightMode));
            textView.setTextColor(-1);
            textView.setTextSize(1, 15.0f);
            FrameLayout frameLayout2 = this.nightModeHintView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            int i3 = i | 48;
            float f = (float) (LocaleController.isRTL ? 10 : 56);
            if (!LocaleController.isRTL) {
                i2 = 10;
            }
            frameLayout2.addView(textView, LayoutHelper.createFrame(-1.0f, -1.0f, i3, f, 11.0f, (float) i2, 12.0f));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.nightModeHintView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(100.0f), 0.0f})});
            animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            ArticleViewer.AnonymousClass12.this.lambda$onAnimationEnd$0$ArticleViewer$12();
                        }
                    }, 3000);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$12() {
                    AnimatorSet animatorSet1 = new AnimatorSet();
                    animatorSet1.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.nightModeHintView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(100.0f)})});
                    animatorSet1.setInterpolator(new DecelerateInterpolator(1.5f));
                    animatorSet1.setDuration(250);
                    animatorSet1.start();
                }
            });
            animatorSet.setDuration(250);
            animatorSet.start();
        }
    }

    private void updateNightModeButton() {
        this.nightModeImageView.setEnabled(this.selectedColor != 2);
        this.nightModeImageView.setAlpha(this.selectedColor == 2 ? 0.5f : 1.0f);
        this.nightModeImageView.setColorFilter(new PorterDuffColorFilter((!this.nightModeEnabled || this.selectedColor == 2) ? -3355444 : -15428119, PorterDuff.Mode.MULTIPLY));
    }

    public class ScrollEvaluator extends IntEvaluator {
        public ScrollEvaluator() {
        }

        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            return super.evaluate(fraction, startValue, endValue);
        }
    }

    private void checkScrollAnimated() {
        if (this.currentHeaderHeight != AndroidUtilities.dp(56.0f)) {
            ValueAnimator va = ValueAnimator.ofObject(new IntEvaluator(), new Object[]{Integer.valueOf(this.currentHeaderHeight), Integer.valueOf(AndroidUtilities.dp(56.0f))}).setDuration(180);
            va.setInterpolator(new DecelerateInterpolator());
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ArticleViewer.this.lambda$checkScrollAnimated$21$ArticleViewer(valueAnimator);
                }
            });
            va.start();
        }
    }

    public /* synthetic */ void lambda$checkScrollAnimated$21$ArticleViewer(ValueAnimator animation) {
        setCurrentHeaderHeight(((Integer) animation.getAnimatedValue()).intValue());
    }

    /* access modifiers changed from: private */
    public void setCurrentHeaderHeight(int newHeight) {
        int maxHeight = AndroidUtilities.dp(56.0f);
        int minHeight = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.dp(24.0f));
        if (newHeight < minHeight) {
            newHeight = minHeight;
        } else if (newHeight > maxHeight) {
            newHeight = maxHeight;
        }
        float heightDiff = (float) (maxHeight - minHeight);
        this.currentHeaderHeight = newHeight;
        float scale2 = ((((float) (newHeight - minHeight)) / heightDiff) * 0.2f) + 0.8f;
        this.backButton.setScaleX(scale2);
        this.backButton.setScaleY(scale2);
        this.backButton.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
        this.shareContainer.setScaleX(scale2);
        this.shareContainer.setScaleY(scale2);
        this.settingsButton.setScaleX(scale2);
        this.settingsButton.setScaleY(scale2);
        this.titleTextView.setScaleX(scale2);
        this.titleTextView.setScaleY(scale2);
        this.lineProgressView.setScaleY(((((float) (newHeight - minHeight)) / heightDiff) * 0.5f) + 0.5f);
        this.shareContainer.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
        this.settingsButton.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
        this.titleTextView.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
        this.headerView.setTranslationY((float) (this.currentHeaderHeight - maxHeight));
        int i = 0;
        while (true) {
            RecyclerListView[] recyclerListViewArr = this.listView;
            if (i < recyclerListViewArr.length) {
                recyclerListViewArr[i].setTopGlowOffset(this.currentHeaderHeight);
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkScroll(int dy) {
        setCurrentHeaderHeight(this.currentHeaderHeight - dy);
    }

    private void openPreviewsChat(TLRPC.User user, long wid) {
        if (user != null && this.parentActivity != null) {
            Bundle args = new Bundle();
            args.putInt("user_id", user.id);
            args.putString("botUser", "webpage" + wid);
            ((LaunchActivity) this.parentActivity).presentFragment(new ChatActivity(args), false, true);
            close(false, true);
        }
    }

    public boolean open(MessageObject messageObject) {
        return open(messageObject, (TLRPC.WebPage) null, (String) null, true);
    }

    public boolean open(TLRPC.TL_webPage webpage, String url) {
        return open((MessageObject) null, webpage, url, true);
    }

    private boolean open(MessageObject messageObject, TLRPC.WebPage webpage, String url, boolean first) {
        TLRPC.WebPage webpage2;
        String anchor;
        TLRPC.WebPage webpage3;
        Paint paint;
        String webPageUrl;
        MessageObject messageObject2 = messageObject;
        String str = url;
        if (this.parentActivity == null) {
            return false;
        }
        if (this.isVisible && !this.collapsed) {
            return false;
        }
        if (messageObject2 == null && webpage == null) {
            return false;
        }
        if (messageObject2 != null) {
            webpage2 = messageObject2.messageOwner.media.webpage;
        } else {
            webpage2 = webpage;
        }
        String anchor2 = null;
        if (messageObject2 != null) {
            TLRPC.WebPage webpage4 = messageObject2.messageOwner.media.webpage;
            int a = 0;
            String url2 = str;
            while (true) {
                if (a >= messageObject2.messageOwner.entities.size()) {
                    break;
                }
                TLRPC.MessageEntity entity = messageObject2.messageOwner.entities.get(a);
                if (entity instanceof TLRPC.TL_messageEntityUrl) {
                    try {
                        url2 = messageObject2.messageOwner.message.substring(entity.offset, entity.offset + entity.length).toLowerCase();
                        if (!TextUtils.isEmpty(webpage4.cached_page.url)) {
                            webPageUrl = webpage4.cached_page.url.toLowerCase();
                        } else {
                            webPageUrl = webpage4.url.toLowerCase();
                        }
                        if (url2.contains(webPageUrl)) {
                            break;
                        } else if (webPageUrl.contains(url2)) {
                            break;
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                a++;
            }
            int lastIndexOf = url2.lastIndexOf(35);
            int index = lastIndexOf;
            if (lastIndexOf != -1) {
                anchor2 = url2.substring(index + 1);
            }
            webpage3 = webpage4;
            anchor = anchor2;
            String str2 = url2;
        } else {
            if (str != null) {
                int lastIndexOf2 = str.lastIndexOf(35);
                int index2 = lastIndexOf2;
                if (lastIndexOf2 != -1) {
                    String str3 = str;
                    webpage3 = webpage2;
                    anchor = str.substring(index2 + 1);
                }
            }
            String str4 = str;
            webpage3 = webpage2;
            anchor = null;
        }
        this.pagesStack.clear();
        this.collapsed = false;
        this.backDrawable.setRotation(0.0f, false);
        this.containerView.setTranslationX(0.0f);
        this.containerView.setTranslationY(0.0f);
        this.listView[0].setTranslationY(0.0f);
        this.listView[0].setTranslationX(0.0f);
        this.listView[1].setTranslationX(0.0f);
        this.listView[0].setAlpha(1.0f);
        this.windowView.setInnerTranslationX(0.0f);
        this.actionBar.setVisibility(8);
        this.bottomLayout.setVisibility(8);
        this.captionTextView.setVisibility(8);
        this.captionTextViewNext.setVisibility(8);
        this.layoutManager[0].scrollToPositionWithOffset(0, 0);
        if (first) {
            setCurrentHeaderHeight(AndroidUtilities.dp(56.0f));
        } else {
            checkScrollAnimated();
        }
        boolean scrolledToAnchor = addPageToStack(webpage3, anchor, 0);
        if (first) {
            String anchorFinal = (scrolledToAnchor || anchor == null) ? null : anchor;
            TLRPC.TL_messages_getWebPage req = new TLRPC.TL_messages_getWebPage();
            req.url = webpage3.url;
            if ((webpage3.cached_page instanceof TLRPC.TL_pagePart_layer82) || webpage3.cached_page.part) {
                req.hash = 0;
            } else {
                req.hash = webpage3.hash;
            }
            int currentAccount2 = UserConfig.selectedAccount;
            $$Lambda$ArticleViewer$FYzRJoHfdb_nQaGKuXBguRRFbnw r15 = r1;
            ConnectionsManager instance = ConnectionsManager.getInstance(currentAccount2);
            paint = null;
            $$Lambda$ArticleViewer$FYzRJoHfdb_nQaGKuXBguRRFbnw r1 = new RequestDelegate(webpage3, messageObject, anchorFinal, currentAccount2) {
                private final /* synthetic */ TLRPC.WebPage f$1;
                private final /* synthetic */ MessageObject f$2;
                private final /* synthetic */ String f$3;
                private final /* synthetic */ int f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ArticleViewer.this.lambda$open$23$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                }
            };
            instance.sendRequest(req, r15);
        } else {
            paint = null;
        }
        this.lastInsets = paint;
        if (!this.isVisible) {
            WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
            if (this.attachedToWindow) {
                try {
                    wm.removeView(this.windowView);
                } catch (Exception e2) {
                }
            }
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.windowLayoutParams.flags = -2147417856;
                    if (Build.VERSION.SDK_INT >= 28) {
                        this.windowLayoutParams.layoutInDisplayCutoutMode = 1;
                    }
                }
                this.windowLayoutParams.flags |= 1032;
                this.windowView.setFocusable(false);
                this.containerView.setFocusable(false);
                wm.addView(this.windowView, this.windowLayoutParams);
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
                return false;
            }
        } else {
            this.windowLayoutParams.flags &= -17;
            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
        }
        this.isVisible = true;
        this.animationInProgress = 1;
        this.windowView.setAlpha(0.0f);
        this.containerView.setAlpha(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(56.0f), 0.0f})});
        this.animationEndRunnable = new Runnable() {
            public final void run() {
                ArticleViewer.this.lambda$open$24$ArticleViewer();
            }
        };
        animatorSet.setDuration(150);
        animatorSet.setInterpolator(this.interpolator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        ArticleViewer.AnonymousClass13.this.lambda$onAnimationEnd$0$ArticleViewer$13();
                    }
                });
            }

            public /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$13() {
                NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(false);
                if (ArticleViewer.this.animationEndRunnable != null) {
                    ArticleViewer.this.animationEndRunnable.run();
                    Runnable unused = ArticleViewer.this.animationEndRunnable = null;
                }
            }
        });
        this.transitionAnimationStartTime = System.currentTimeMillis();
        AndroidUtilities.runOnUIThread(new Runnable(animatorSet) {
            private final /* synthetic */ AnimatorSet f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ArticleViewer.this.lambda$open$25$ArticleViewer(this.f$1);
            }
        });
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(2, paint);
        }
        return true;
    }

    public /* synthetic */ void lambda$open$23$ArticleViewer(TLRPC.WebPage webPageFinal, MessageObject messageObject, String anchorFinal, int currentAccount2, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_webPage) {
            TLRPC.TL_webPage webPage = (TLRPC.TL_webPage) response;
            if (webPage.cached_page != null) {
                AndroidUtilities.runOnUIThread(new Runnable(webPageFinal, webPage, messageObject, anchorFinal) {
                    private final /* synthetic */ TLRPC.WebPage f$1;
                    private final /* synthetic */ TLRPC.TL_webPage f$2;
                    private final /* synthetic */ MessageObject f$3;
                    private final /* synthetic */ String f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        ArticleViewer.this.lambda$null$22$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
                LongSparseArray<TLRPC.WebPage> webpages = new LongSparseArray<>(1);
                webpages.put(webPage.id, webPage);
                MessagesStorage.getInstance(currentAccount2).putWebPages(webpages);
            }
        }
    }

    public /* synthetic */ void lambda$null$22$ArticleViewer(TLRPC.WebPage webPageFinal, TLRPC.TL_webPage webPage, MessageObject messageObject, String anchorFinal) {
        if (!this.pagesStack.isEmpty() && this.pagesStack.get(0) == webPageFinal && webPage.cached_page != null) {
            if (messageObject != null) {
                messageObject.messageOwner.media.webpage = webPage;
            }
            this.pagesStack.set(0, webPage);
            if (this.pagesStack.size() == 1) {
                this.currentPage = webPage;
                SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit();
                edit.remove("article" + this.currentPage.id).commit();
                updateInterfaceForCurrentPage(0);
                if (anchorFinal != null) {
                    scrollToAnchor(anchorFinal);
                }
            }
        }
    }

    public /* synthetic */ void lambda$open$24$ArticleViewer() {
        if (this.containerView != null && this.windowView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
    }

    public /* synthetic */ void lambda$open$25$ArticleViewer(AnimatorSet animatorSet) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        animatorSet.start();
    }

    private void showProgressView(boolean useLine, boolean show) {
        final boolean z = show;
        if (useLine) {
            AndroidUtilities.cancelRunOnUIThread(this.lineProgressTickRunnable);
            if (z) {
                this.lineProgressView.setProgress(0.0f, false);
                this.lineProgressView.setProgress(0.3f, true);
                AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100);
                return;
            }
            this.lineProgressView.setProgress(1.0f, true);
            return;
        }
        AnimatorSet animatorSet = this.progressViewAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressViewAnimation = new AnimatorSet();
        if (z) {
            this.progressView.setVisibility(0);
            this.shareContainer.setEnabled(false);
            this.progressViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.shareButton, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.shareButton, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.shareButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f})});
        } else {
            this.shareButton.setVisibility(0);
            this.shareContainer.setEnabled(true);
            this.progressViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.shareButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.shareButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.shareButton, View.ALPHA, new float[]{1.0f})});
        }
        this.progressViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animation)) {
                    if (!z) {
                        ArticleViewer.this.progressView.setVisibility(4);
                    } else {
                        ArticleViewer.this.shareButton.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animation)) {
                    AnimatorSet unused = ArticleViewer.this.progressViewAnimation = null;
                }
            }
        });
        this.progressViewAnimation.setDuration(150);
        this.progressViewAnimation.start();
    }

    public void collapse() {
        if (this.parentActivity != null && this.isVisible && !checkAnimation()) {
            if (this.fullscreenVideoContainer.getVisibility() == 0) {
                if (this.customView != null) {
                    this.fullscreenVideoContainer.setVisibility(4);
                    this.customViewCallback.onCustomViewHidden();
                    this.fullscreenVideoContainer.removeView(this.customView);
                    this.customView = null;
                } else {
                    WebPlayerView webPlayerView = this.fullscreenedVideo;
                    if (webPlayerView != null) {
                        webPlayerView.exitFullscreen();
                    }
                }
            }
            if (this.isPhotoVisible) {
                closePhoto(false);
            }
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[12];
            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{(float) (this.containerView.getMeasuredWidth() - AndroidUtilities.dp(56.0f))});
            FrameLayout frameLayout = this.containerView;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = (float) (ActionBar.getCurrentActionBarHeight() + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
            animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, new float[]{0.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(56.0f))});
            animatorArr[5] = ObjectAnimator.ofFloat(this.headerView, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[6] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_X, new float[]{1.0f});
            animatorArr[7] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_Y, new float[]{1.0f});
            animatorArr[8] = ObjectAnimator.ofFloat(this.backButton, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[9] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_X, new float[]{1.0f});
            animatorArr[10] = ObjectAnimator.ofFloat(this.shareContainer, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[11] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_Y, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.collapsed = true;
            this.animationInProgress = 2;
            this.animationEndRunnable = new Runnable() {
                public final void run() {
                    ArticleViewer.this.lambda$collapse$26$ArticleViewer();
                }
            };
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(250);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ArticleViewer.this.animationEndRunnable != null) {
                        ArticleViewer.this.animationEndRunnable.run();
                        Runnable unused = ArticleViewer.this.animationEndRunnable = null;
                    }
                }
            });
            this.transitionAnimationStartTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, (Paint) null);
            }
            this.backDrawable.setRotation(1.0f, true);
            animatorSet.start();
        }
    }

    public /* synthetic */ void lambda$collapse$26$ArticleViewer() {
        if (this.containerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public void uncollapse() {
        if (this.parentActivity != null && this.isVisible && !checkAnimation()) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.headerView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.backButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backButton, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.shareContainer, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_Y, new float[]{1.0f})});
            this.collapsed = false;
            this.animationInProgress = 2;
            this.animationEndRunnable = new Runnable() {
                public final void run() {
                    ArticleViewer.this.lambda$uncollapse$27$ArticleViewer();
                }
            };
            animatorSet.setDuration(250);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ArticleViewer.this.animationEndRunnable != null) {
                        ArticleViewer.this.animationEndRunnable.run();
                        Runnable unused = ArticleViewer.this.animationEndRunnable = null;
                    }
                }
            });
            this.transitionAnimationStartTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, (Paint) null);
            }
            this.backDrawable.setRotation(0.0f, true);
            animatorSet.start();
        }
    }

    public /* synthetic */ void lambda$uncollapse$27$ArticleViewer() {
        if (this.containerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
        }
    }

    /* access modifiers changed from: private */
    public void saveCurrentPagePosition() {
        int offset;
        if (this.currentPage != null) {
            boolean z = false;
            int position = this.layoutManager[0].findFirstVisibleItemPosition();
            if (position != -1) {
                View view = this.layoutManager[0].findViewByPosition(position);
                if (view != null) {
                    offset = view.getTop();
                } else {
                    offset = 0;
                }
                String key = "article" + this.currentPage.id;
                SharedPreferences.Editor putInt = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt(key, position).putInt(key + "o", offset);
                String str = key + "r";
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    z = true;
                }
                putInt.putBoolean(str, z).commit();
            }
        }
    }

    public void close(boolean byBackPress, boolean force) {
        if (this.parentActivity != null && this.isVisible && !checkAnimation()) {
            if (this.fullscreenVideoContainer.getVisibility() == 0) {
                if (this.customView != null) {
                    this.fullscreenVideoContainer.setVisibility(4);
                    this.customViewCallback.onCustomViewHidden();
                    this.fullscreenVideoContainer.removeView(this.customView);
                    this.customView = null;
                } else {
                    WebPlayerView webPlayerView = this.fullscreenedVideo;
                    if (webPlayerView != null) {
                        webPlayerView.exitFullscreen();
                    }
                }
                if (!force) {
                    return;
                }
            }
            if (this.isPhotoVisible) {
                closePhoto(!force);
                if (!force) {
                    return;
                }
            }
            if (this.openUrlReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.openUrlReqId, true);
                this.openUrlReqId = 0;
                showProgressView(true, false);
            }
            if (this.previewsReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.previewsReqId, true);
                this.previewsReqId = 0;
                showProgressView(true, false);
            }
            saveCurrentPagePosition();
            if (!byBackPress || force || !removeLastPageFromStack()) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
                this.parentFragment = null;
                try {
                    if (this.visibleDialog != null) {
                        this.visibleDialog.dismiss();
                        this.visibleDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.windowView, View.TRANSLATION_X, new float[]{0.0f, (float) AndroidUtilities.dp(56.0f)})});
                this.animationInProgress = 2;
                this.animationEndRunnable = new Runnable() {
                    public final void run() {
                        ArticleViewer.this.lambda$close$28$ArticleViewer();
                    }
                };
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.interpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ArticleViewer.this.animationEndRunnable != null) {
                            ArticleViewer.this.animationEndRunnable.run();
                            Runnable unused = ArticleViewer.this.animationEndRunnable = null;
                        }
                    }
                });
                this.transitionAnimationStartTime = System.currentTimeMillis();
                if (Build.VERSION.SDK_INT >= 18) {
                    this.containerView.setLayerType(2, (Paint) null);
                }
                animatorSet.start();
            }
        }
    }

    public /* synthetic */ void lambda$close$28$ArticleViewer() {
        if (this.containerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
            onClosed();
        }
    }

    /* access modifiers changed from: private */
    public void onClosed() {
        this.isVisible = false;
        this.currentPage = null;
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].cleanup();
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        for (int a = 0; a < this.createdWebViews.size(); a++) {
            this.createdWebViews.get(a).destroyWebView(false);
        }
        this.containerView.post(new Runnable() {
            public final void run() {
                ArticleViewer.this.lambda$onClosed$29$ArticleViewer();
            }
        });
    }

    public /* synthetic */ void lambda$onClosed$29$ArticleViewer() {
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void loadChannel(BlockChannelCell cell, WebpageAdapter adapter2, TLRPC.Chat channel) {
        if (!this.loadingChannel && !TextUtils.isEmpty(channel.username)) {
            this.loadingChannel = true;
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            req.username = channel.username;
            int currentAccount2 = UserConfig.selectedAccount;
            ConnectionsManager.getInstance(currentAccount2).sendRequest(req, new RequestDelegate(adapter2, currentAccount2, cell) {
                private final /* synthetic */ ArticleViewer.WebpageAdapter f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ ArticleViewer.BlockChannelCell f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ArticleViewer.this.lambda$loadChannel$31$ArticleViewer(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadChannel$31$ArticleViewer(WebpageAdapter adapter2, int currentAccount2, BlockChannelCell cell, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(adapter2, error, response, currentAccount2, cell) {
            private final /* synthetic */ ArticleViewer.WebpageAdapter f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ ArticleViewer.BlockChannelCell f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                ArticleViewer.this.lambda$null$30$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$30$ArticleViewer(WebpageAdapter adapter2, TLRPC.TL_error error, TLObject response, int currentAccount2, BlockChannelCell cell) {
        this.loadingChannel = false;
        if (this.parentFragment != null && !adapter2.blocks.isEmpty()) {
            if (error == null) {
                TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                if (!res.chats.isEmpty()) {
                    MessagesController.getInstance(currentAccount2).putUsers(res.users, false);
                    MessagesController.getInstance(currentAccount2).putChats(res.chats, false);
                    MessagesStorage.getInstance(currentAccount2).putUsersAndChats(res.users, res.chats, false, true);
                    TLRPC.Chat chat = res.chats.get(0);
                    this.loadedChannel = chat;
                    if (!chat.left || this.loadedChannel.kicked) {
                        cell.setState(4, false);
                    } else {
                        cell.setState(0, false);
                    }
                } else {
                    cell.setState(4, false);
                }
            } else {
                cell.setState(4, false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void joinChannel(BlockChannelCell cell, TLRPC.Chat channel) {
        TLRPC.TL_channels_joinChannel req = new TLRPC.TL_channels_joinChannel();
        req.channel = MessagesController.getInputChannel(channel);
        int currentAccount2 = UserConfig.selectedAccount;
        ConnectionsManager.getInstance(currentAccount2).sendRequest(req, new RequestDelegate(cell, currentAccount2, req, channel) {
            private final /* synthetic */ ArticleViewer.BlockChannelCell f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ TLRPC.TL_channels_joinChannel f$3;
            private final /* synthetic */ TLRPC.Chat f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ArticleViewer.this.lambda$joinChannel$35$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$joinChannel$35$ArticleViewer(BlockChannelCell cell, int currentAccount2, TLRPC.TL_channels_joinChannel req, TLRPC.Chat channel, TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable(cell, currentAccount2, error, req) {
                private final /* synthetic */ ArticleViewer.BlockChannelCell f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ TLRPC.TL_error f$3;
                private final /* synthetic */ TLRPC.TL_channels_joinChannel f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    ArticleViewer.this.lambda$null$32$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
            return;
        }
        boolean hasJoinMessage = false;
        TLRPC.Updates updates = (TLRPC.Updates) response;
        int a = 0;
        while (true) {
            if (a >= updates.updates.size()) {
                break;
            }
            TLRPC.Update update = updates.updates.get(a);
            if ((update instanceof TLRPC.TL_updateNewChannelMessage) && (((TLRPC.TL_updateNewChannelMessage) update).message.action instanceof TLRPC.TL_messageActionChatAddUser)) {
                hasJoinMessage = true;
                break;
            }
            a++;
        }
        MessagesController.getInstance(currentAccount2).processUpdates(updates, false);
        if (!hasJoinMessage) {
            MessagesController.getInstance(currentAccount2).generateJoinMessage(channel.id, true);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                ArticleViewer.BlockChannelCell.this.setState(2, false);
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable(currentAccount2, channel) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ TLRPC.Chat f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.getInstance(this.f$0).loadFullChat(this.f$1.id, 0, true);
            }
        }, 1000);
        MessagesStorage.getInstance(currentAccount2).updateDialogsWithDeletedMessages(new ArrayList(), (ArrayList<Long>) null, true, channel.id);
    }

    public /* synthetic */ void lambda$null$32$ArticleViewer(BlockChannelCell cell, int currentAccount2, TLRPC.TL_error error, TLRPC.TL_channels_joinChannel req) {
        cell.setState(0, false);
        AlertsCreator.processError(currentAccount2, error, this.parentFragment, req, true);
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

    public void destroyArticleViewer() {
        if (this.parentActivity != null && this.windowView != null) {
            releasePlayer();
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            for (int a = 0; a < this.createdWebViews.size(); a++) {
                this.createdWebViews.get(a).destroyWebView(true);
            }
            this.createdWebViews.clear();
            try {
                this.parentActivity.getWindow().clearFlags(128);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.currentThumb = null;
            }
            this.animatingImageView.setImageBitmap((ImageReceiver.BitmapHolder) null);
            this.parentActivity = null;
            this.parentFragment = null;
            Instance = null;
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void showDialog(Dialog dialog) {
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
                this.visibleDialog = dialog;
                dialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        ArticleViewer.this.lambda$showDialog$36$ArticleViewer(dialogInterface);
                    }
                });
                dialog.show();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public /* synthetic */ void lambda$showDialog$36$ArticleViewer(DialogInterface dialog1) {
        this.visibleDialog = null;
    }

    private class WebpageAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public HashMap<String, Integer> anchors = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<String, Integer> anchorsOffset = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<String, TLRPC.TL_textAnchor> anchorsParent = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC.TL_pageBlockAudio, MessageObject> audioBlocks = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<MessageObject> audioMessages = new ArrayList<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.PageBlock> blocks = new ArrayList<>();
        private Context context;
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.PageBlock> localBlocks = new ArrayList<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.PageBlock> photoBlocks = new ArrayList<>();

        public WebpageAdapter(Context ctx) {
            this.context = ctx;
        }

        private void setRichTextParents(TLRPC.RichText parentRichText, TLRPC.RichText richText) {
            if (richText != null) {
                richText.parentRichText = parentRichText;
                if (richText instanceof TLRPC.TL_textFixed) {
                    setRichTextParents(richText, ((TLRPC.TL_textFixed) richText).text);
                } else if (richText instanceof TLRPC.TL_textItalic) {
                    setRichTextParents(richText, ((TLRPC.TL_textItalic) richText).text);
                } else if (richText instanceof TLRPC.TL_textBold) {
                    setRichTextParents(richText, ((TLRPC.TL_textBold) richText).text);
                } else if (richText instanceof TLRPC.TL_textUnderline) {
                    setRichTextParents(richText, ((TLRPC.TL_textUnderline) richText).text);
                } else if (richText instanceof TLRPC.TL_textStrike) {
                    setRichTextParents(richText, ((TLRPC.TL_textStrike) richText).text);
                } else if (richText instanceof TLRPC.TL_textEmail) {
                    setRichTextParents(richText, ((TLRPC.TL_textEmail) richText).text);
                } else if (richText instanceof TLRPC.TL_textPhone) {
                    setRichTextParents(richText, ((TLRPC.TL_textPhone) richText).text);
                } else if (richText instanceof TLRPC.TL_textUrl) {
                    setRichTextParents(richText, ((TLRPC.TL_textUrl) richText).text);
                } else if (richText instanceof TLRPC.TL_textConcat) {
                    int count = richText.texts.size();
                    for (int a = 0; a < count; a++) {
                        setRichTextParents(richText, richText.texts.get(a));
                    }
                } else if (richText instanceof TLRPC.TL_textSubscript) {
                    setRichTextParents(richText, ((TLRPC.TL_textSubscript) richText).text);
                } else if (richText instanceof TLRPC.TL_textSuperscript) {
                    setRichTextParents(richText, ((TLRPC.TL_textSuperscript) richText).text);
                } else if (richText instanceof TLRPC.TL_textMarked) {
                    setRichTextParents(richText, ((TLRPC.TL_textMarked) richText).text);
                } else if (richText instanceof TLRPC.TL_textAnchor) {
                    TLRPC.TL_textAnchor textAnchor = (TLRPC.TL_textAnchor) richText;
                    setRichTextParents(richText, textAnchor.text);
                    String name = textAnchor.name.toLowerCase();
                    this.anchors.put(name, Integer.valueOf(this.blocks.size()));
                    if (textAnchor.text instanceof TLRPC.TL_textPlain) {
                        if (!TextUtils.isEmpty(((TLRPC.TL_textPlain) textAnchor.text).text)) {
                            this.anchorsParent.put(name, textAnchor);
                        }
                    } else if (!(textAnchor.text instanceof TLRPC.TL_textEmpty)) {
                        this.anchorsParent.put(name, textAnchor);
                    }
                    this.anchorsOffset.put(name, -1);
                }
            }
        }

        private void setRichTextParents(TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockEmbedPost) {
                TLRPC.TL_pageBlockEmbedPost blockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) block;
                setRichTextParents((TLRPC.RichText) null, blockEmbedPost.caption.text);
                setRichTextParents((TLRPC.RichText) null, blockEmbedPost.caption.credit);
            } else if (block instanceof TLRPC.TL_pageBlockParagraph) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockParagraph) block).text);
            } else if (block instanceof TLRPC.TL_pageBlockKicker) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockKicker) block).text);
            } else if (block instanceof TLRPC.TL_pageBlockFooter) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockFooter) block).text);
            } else if (block instanceof TLRPC.TL_pageBlockHeader) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockHeader) block).text);
            } else if (block instanceof TLRPC.TL_pageBlockPreformatted) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockPreformatted) block).text);
            } else if (block instanceof TLRPC.TL_pageBlockSubheader) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockSubheader) block).text);
            } else if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockSlideshow.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockSlideshow.caption.credit);
                int size = pageBlockSlideshow.items.size();
                for (int a = 0; a < size; a++) {
                    setRichTextParents(pageBlockSlideshow.items.get(a));
                }
            } else if (block instanceof TLRPC.TL_pageBlockPhoto) {
                TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockPhoto.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockPhoto.caption.credit);
            } else if (block instanceof TL_pageBlockListItem) {
                TL_pageBlockListItem pageBlockListItem = (TL_pageBlockListItem) block;
                if (pageBlockListItem.textItem != null) {
                    setRichTextParents((TLRPC.RichText) null, pageBlockListItem.textItem);
                } else if (pageBlockListItem.blockItem != null) {
                    setRichTextParents(pageBlockListItem.blockItem);
                }
            } else if (block instanceof TL_pageBlockOrderedListItem) {
                TL_pageBlockOrderedListItem pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) block;
                if (pageBlockOrderedListItem.textItem != null) {
                    setRichTextParents((TLRPC.RichText) null, pageBlockOrderedListItem.textItem);
                } else if (pageBlockOrderedListItem.blockItem != null) {
                    setRichTextParents(pageBlockOrderedListItem.blockItem);
                }
            } else if (block instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage pageBlockCollage = (TLRPC.TL_pageBlockCollage) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockCollage.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockCollage.caption.credit);
                int size2 = pageBlockCollage.items.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    setRichTextParents(pageBlockCollage.items.get(a2));
                }
            } else if (block instanceof TLRPC.TL_pageBlockEmbed) {
                TLRPC.TL_pageBlockEmbed pageBlockEmbed = (TLRPC.TL_pageBlockEmbed) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockEmbed.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockEmbed.caption.credit);
            } else if (block instanceof TLRPC.TL_pageBlockSubtitle) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockSubtitle) block).text);
            } else if (block instanceof TLRPC.TL_pageBlockBlockquote) {
                TLRPC.TL_pageBlockBlockquote pageBlockBlockquote = (TLRPC.TL_pageBlockBlockquote) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockBlockquote.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockBlockquote.caption);
            } else if (block instanceof TLRPC.TL_pageBlockDetails) {
                TLRPC.TL_pageBlockDetails pageBlockDetails = (TLRPC.TL_pageBlockDetails) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockDetails.title);
                int size3 = pageBlockDetails.blocks.size();
                for (int a3 = 0; a3 < size3; a3++) {
                    setRichTextParents(pageBlockDetails.blocks.get(a3));
                }
            } else if (block instanceof TLRPC.TL_pageBlockVideo) {
                TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockVideo.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockVideo.caption.credit);
            } else if (block instanceof TLRPC.TL_pageBlockPullquote) {
                TLRPC.TL_pageBlockPullquote pageBlockPullquote = (TLRPC.TL_pageBlockPullquote) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockPullquote.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockPullquote.caption);
            } else if (block instanceof TLRPC.TL_pageBlockAudio) {
                TLRPC.TL_pageBlockAudio pageBlockAudio = (TLRPC.TL_pageBlockAudio) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockAudio.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockAudio.caption.credit);
            } else if (block instanceof TLRPC.TL_pageBlockTable) {
                TLRPC.TL_pageBlockTable pageBlockTable = (TLRPC.TL_pageBlockTable) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockTable.title);
                int size4 = pageBlockTable.rows.size();
                for (int a4 = 0; a4 < size4; a4++) {
                    TLRPC.TL_pageTableRow row = pageBlockTable.rows.get(a4);
                    int size22 = row.cells.size();
                    for (int b = 0; b < size22; b++) {
                        setRichTextParents((TLRPC.RichText) null, row.cells.get(b).text);
                    }
                }
            } else if (block instanceof TLRPC.TL_pageBlockTitle) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockTitle) block).text);
            } else if (block instanceof TLRPC.TL_pageBlockCover) {
                setRichTextParents(((TLRPC.TL_pageBlockCover) block).cover);
            } else if (block instanceof TLRPC.TL_pageBlockAuthorDate) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockAuthorDate) block).author);
            } else if (block instanceof TLRPC.TL_pageBlockMap) {
                TLRPC.TL_pageBlockMap pageBlockMap = (TLRPC.TL_pageBlockMap) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockMap.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockMap.caption.credit);
            } else if (block instanceof TLRPC.TL_pageBlockRelatedArticles) {
                setRichTextParents((TLRPC.RichText) null, ((TLRPC.TL_pageBlockRelatedArticles) block).title);
            }
        }

        /* access modifiers changed from: private */
        public void addBlock(TLRPC.PageBlock block, int level, int listLevel, int position) {
            int size;
            TLRPC.TL_pageBlockOrderedList pageBlockOrderedList;
            TLRPC.PageBlock block2;
            String str;
            TLRPC.TL_pageListOrderedItemBlocks pageListOrderedItemBlocks;
            TLRPC.PageBlock finalBlock;
            TLRPC.TL_pageBlockList pageBlockList;
            String str2;
            int size2;
            TLRPC.TL_pageListItemBlocks pageListItemBlocks;
            TLRPC.PageBlock finalBlock2;
            TLRPC.PageBlock block3 = block;
            int i = level;
            int i2 = listLevel;
            int i3 = position;
            TLRPC.PageBlock originalBlock = block;
            if (block3 instanceof TL_pageBlockDetailsChild) {
                block3 = ((TL_pageBlockDetailsChild) block3).block;
            }
            if (!(block3 instanceof TLRPC.TL_pageBlockList) && !(block3 instanceof TLRPC.TL_pageBlockOrderedList)) {
                setRichTextParents(block3);
                addAllMediaFromBlock(block3);
            }
            TLRPC.PageBlock block4 = ArticleViewer.this.getLastNonListPageBlock(block3);
            if (!(block4 instanceof TLRPC.TL_pageBlockUnsupported)) {
                if (block4 instanceof TLRPC.TL_pageBlockAnchor) {
                    this.anchors.put(((TLRPC.TL_pageBlockAnchor) block4).name.toLowerCase(), Integer.valueOf(this.blocks.size()));
                    return;
                }
                if (!(block4 instanceof TLRPC.TL_pageBlockList) && !(block4 instanceof TLRPC.TL_pageBlockOrderedList)) {
                    this.blocks.add(originalBlock);
                }
                if (block4 instanceof TLRPC.TL_pageBlockAudio) {
                    TLRPC.TL_pageBlockAudio blockAudio = (TLRPC.TL_pageBlockAudio) block4;
                    TLRPC.TL_message message = new TLRPC.TL_message();
                    message.out = true;
                    int i4 = -Long.valueOf(blockAudio.audio_id).hashCode();
                    block4.mid = i4;
                    message.id = i4;
                    message.to_id = new TLRPC.TL_peerUser();
                    TLRPC.Peer peer = message.to_id;
                    int clientUserId = UserConfig.getInstance(ArticleViewer.this.currentAccount).getClientUserId();
                    message.from_id = clientUserId;
                    peer.user_id = clientUserId;
                    message.date = (int) (System.currentTimeMillis() / 1000);
                    message.message = "";
                    message.media = new TLRPC.TL_messageMediaDocument();
                    message.media.webpage = ArticleViewer.this.currentPage;
                    message.media.flags |= 3;
                    message.media.document = ArticleViewer.this.getDocumentWithId(blockAudio.audio_id);
                    message.flags |= 768;
                    MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, message, false);
                    this.audioMessages.add(messageObject);
                    this.audioBlocks.put(blockAudio, messageObject);
                    TLRPC.PageBlock pageBlock = block4;
                    return;
                }
                AnonymousClass1 r9 = null;
                if (block4 instanceof TLRPC.TL_pageBlockEmbedPost) {
                    TLRPC.TL_pageBlockEmbedPost pageBlockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) block4;
                    if (!pageBlockEmbedPost.blocks.isEmpty()) {
                        block4.level = -1;
                        for (int b = 0; b < pageBlockEmbedPost.blocks.size(); b++) {
                            TLRPC.PageBlock innerBlock = pageBlockEmbedPost.blocks.get(b);
                            if (!(innerBlock instanceof TLRPC.TL_pageBlockUnsupported)) {
                                if (innerBlock instanceof TLRPC.TL_pageBlockAnchor) {
                                    this.anchors.put(((TLRPC.TL_pageBlockAnchor) innerBlock).name.toLowerCase(), Integer.valueOf(this.blocks.size()));
                                } else {
                                    innerBlock.level = 1;
                                    if (b == pageBlockEmbedPost.blocks.size() - 1) {
                                        innerBlock.bottom = true;
                                    }
                                    this.blocks.add(innerBlock);
                                    addAllMediaFromBlock(innerBlock);
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(ArticleViewer.getPlainText(pageBlockEmbedPost.caption.text)) || !TextUtils.isEmpty(ArticleViewer.getPlainText(pageBlockEmbedPost.caption.credit))) {
                            TL_pageBlockEmbedPostCaption pageBlockEmbedPostCaption = new TL_pageBlockEmbedPostCaption();
                            TLRPC.TL_pageBlockEmbedPost unused = pageBlockEmbedPostCaption.parent = pageBlockEmbedPost;
                            pageBlockEmbedPostCaption.caption = pageBlockEmbedPost.caption;
                            this.blocks.add(pageBlockEmbedPostCaption);
                        }
                    }
                    TLRPC.PageBlock pageBlock2 = block4;
                } else if (block4 instanceof TLRPC.TL_pageBlockRelatedArticles) {
                    TLRPC.TL_pageBlockRelatedArticles pageBlockRelatedArticles = (TLRPC.TL_pageBlockRelatedArticles) block4;
                    TL_pageBlockRelatedArticlesShadow shadow = new TL_pageBlockRelatedArticlesShadow();
                    TLRPC.TL_pageBlockRelatedArticles unused2 = shadow.parent = pageBlockRelatedArticles;
                    ArrayList<TLRPC.PageBlock> arrayList = this.blocks;
                    arrayList.add(arrayList.size() - 1, shadow);
                    int size3 = pageBlockRelatedArticles.articles.size();
                    for (int b2 = 0; b2 < size3; b2++) {
                        TL_pageBlockRelatedArticlesChild child = new TL_pageBlockRelatedArticlesChild();
                        TLRPC.TL_pageBlockRelatedArticles unused3 = child.parent = pageBlockRelatedArticles;
                        int unused4 = child.num = b2;
                        this.blocks.add(child);
                    }
                    if (i3 == 0) {
                        TL_pageBlockRelatedArticlesShadow shadow2 = new TL_pageBlockRelatedArticlesShadow();
                        TLRPC.TL_pageBlockRelatedArticles unused5 = shadow2.parent = pageBlockRelatedArticles;
                        this.blocks.add(shadow2);
                    }
                    TLRPC.PageBlock pageBlock3 = block4;
                } else if (block4 instanceof TLRPC.TL_pageBlockDetails) {
                    TLRPC.TL_pageBlockDetails pageBlockDetails = (TLRPC.TL_pageBlockDetails) block4;
                    int size4 = pageBlockDetails.blocks.size();
                    for (int b3 = 0; b3 < size4; b3++) {
                        TL_pageBlockDetailsChild child2 = new TL_pageBlockDetailsChild();
                        TLRPC.PageBlock unused6 = child2.parent = originalBlock;
                        TLRPC.PageBlock unused7 = child2.block = pageBlockDetails.blocks.get(b3);
                        addBlock(ArticleViewer.this.wrapInTableBlock(originalBlock, child2), i + 1, i2, i3);
                    }
                    TLRPC.PageBlock pageBlock4 = block4;
                } else {
                    String str3 = " ";
                    if (block4 instanceof TLRPC.TL_pageBlockList) {
                        TLRPC.TL_pageBlockList pageBlockList2 = (TLRPC.TL_pageBlockList) block4;
                        TL_pageBlockListParent pageBlockListParent = new TL_pageBlockListParent();
                        TLRPC.TL_pageBlockList unused8 = pageBlockListParent.pageBlockList = pageBlockList2;
                        int unused9 = pageBlockListParent.level = i2;
                        int b4 = 0;
                        int size5 = pageBlockList2.items.size();
                        while (b4 < size5) {
                            TLRPC.PageListItem item = pageBlockList2.items.get(b4);
                            int size6 = size5;
                            TL_pageBlockListItem pageBlockListItem = new TL_pageBlockListItem();
                            int unused10 = pageBlockListItem.index = b4;
                            TL_pageBlockListParent unused11 = pageBlockListItem.parent = pageBlockListParent;
                            if (!pageBlockList2.ordered) {
                                String unused12 = pageBlockListItem.num = "•";
                            } else if (ArticleViewer.this.isRtl) {
                                String unused13 = pageBlockListItem.num = String.format(".%d", new Object[]{Integer.valueOf(b4 + 1)});
                            } else {
                                String unused14 = pageBlockListItem.num = String.format("%d.", new Object[]{Integer.valueOf(b4 + 1)});
                            }
                            pageBlockListParent.items.add(pageBlockListItem);
                            if (item instanceof TLRPC.TL_pageListItemText) {
                                TLRPC.RichText unused15 = pageBlockListItem.textItem = ((TLRPC.TL_pageListItemText) item).text;
                                pageBlockList = pageBlockList2;
                            } else if (item instanceof TLRPC.TL_pageListItemBlocks) {
                                TLRPC.TL_pageListItemBlocks pageListItemBlocks2 = (TLRPC.TL_pageListItemBlocks) item;
                                if (!pageListItemBlocks2.blocks.isEmpty()) {
                                    pageBlockList = pageBlockList2;
                                    TLRPC.PageBlock unused16 = pageBlockListItem.blockItem = pageListItemBlocks2.blocks.get(0);
                                } else {
                                    pageBlockList = pageBlockList2;
                                    TLRPC.TL_pageListItemText text = new TLRPC.TL_pageListItemText();
                                    TLRPC.TL_textPlain textPlain = new TLRPC.TL_textPlain();
                                    textPlain.text = str3;
                                    text.text = textPlain;
                                    item = text;
                                }
                            } else {
                                pageBlockList = pageBlockList2;
                            }
                            if (originalBlock instanceof TL_pageBlockDetailsChild) {
                                str2 = str3;
                                TL_pageBlockDetailsChild child3 = new TL_pageBlockDetailsChild();
                                TLRPC.PageBlock unused17 = child3.parent = ((TL_pageBlockDetailsChild) originalBlock).parent;
                                TLRPC.PageBlock unused18 = child3.block = pageBlockListItem;
                                addBlock(child3, i, i2 + 1, i3);
                            } else {
                                str2 = str3;
                                if (b4 == 0) {
                                    finalBlock2 = ArticleViewer.this.fixListBlock(originalBlock, pageBlockListItem);
                                } else {
                                    finalBlock2 = pageBlockListItem;
                                }
                                addBlock(finalBlock2, i, i2 + 1, i3);
                            }
                            if (item instanceof TLRPC.TL_pageListItemBlocks) {
                                TLRPC.TL_pageListItemBlocks pageListItemBlocks3 = (TLRPC.TL_pageListItemBlocks) item;
                                int c = 1;
                                int size22 = pageListItemBlocks3.blocks.size();
                                while (c < size22) {
                                    TLRPC.PageListItem item2 = item;
                                    TL_pageBlockListItem tL_pageBlockListItem = pageBlockListItem;
                                    pageBlockListItem = new TL_pageBlockListItem();
                                    TLRPC.PageBlock unused19 = pageBlockListItem.blockItem = pageListItemBlocks3.blocks.get(c);
                                    TL_pageBlockListParent unused20 = pageBlockListItem.parent = pageBlockListParent;
                                    if (originalBlock instanceof TL_pageBlockDetailsChild) {
                                        pageListItemBlocks = pageListItemBlocks3;
                                        size2 = size22;
                                        TL_pageBlockDetailsChild child4 = new TL_pageBlockDetailsChild();
                                        TLRPC.PageBlock unused21 = child4.parent = ((TL_pageBlockDetailsChild) originalBlock).parent;
                                        TLRPC.PageBlock unused22 = child4.block = pageBlockListItem;
                                        addBlock(child4, i, i2 + 1, i3);
                                    } else {
                                        pageListItemBlocks = pageListItemBlocks3;
                                        size2 = size22;
                                        addBlock(pageBlockListItem, i, i2 + 1, i3);
                                    }
                                    pageBlockListParent.items.add(pageBlockListItem);
                                    c++;
                                    item = item2;
                                    pageListItemBlocks3 = pageListItemBlocks;
                                    size22 = size2;
                                }
                                TLRPC.PageListItem pageListItem = item;
                                TL_pageBlockListItem tL_pageBlockListItem2 = pageBlockListItem;
                                int i5 = size22;
                            }
                            b4++;
                            size5 = size6;
                            pageBlockList2 = pageBlockList;
                            str3 = str2;
                            r9 = null;
                        }
                        int i6 = size5;
                        TLRPC.PageBlock pageBlock5 = block4;
                        return;
                    }
                    String str4 = str3;
                    if (block4 instanceof TLRPC.TL_pageBlockOrderedList) {
                        TLRPC.TL_pageBlockOrderedList pageBlockOrderedList2 = (TLRPC.TL_pageBlockOrderedList) block4;
                        TL_pageBlockOrderedListParent pageBlockOrderedListParent = new TL_pageBlockOrderedListParent();
                        TLRPC.TL_pageBlockOrderedList unused23 = pageBlockOrderedListParent.pageBlockOrderedList = pageBlockOrderedList2;
                        int unused24 = pageBlockOrderedListParent.level = i2;
                        int b5 = 0;
                        int size7 = pageBlockOrderedList2.items.size();
                        while (b5 < size7) {
                            TLRPC.PageListOrderedItem item3 = pageBlockOrderedList2.items.get(b5);
                            TL_pageBlockOrderedListItem pageBlockOrderedListItem = new TL_pageBlockOrderedListItem();
                            int unused25 = pageBlockOrderedListItem.index = b5;
                            TL_pageBlockOrderedListParent unused26 = pageBlockOrderedListItem.parent = pageBlockOrderedListParent;
                            pageBlockOrderedListParent.items.add(pageBlockOrderedListItem);
                            if (item3 instanceof TLRPC.TL_pageListOrderedItemText) {
                                TLRPC.TL_pageListOrderedItemText pageListOrderedItemText = (TLRPC.TL_pageListOrderedItemText) item3;
                                block2 = block4;
                                TLRPC.RichText unused27 = pageBlockOrderedListItem.textItem = pageListOrderedItemText.text;
                                if (TextUtils.isEmpty(pageListOrderedItemText.num)) {
                                    if (ArticleViewer.this.isRtl) {
                                        String unused28 = pageBlockOrderedListItem.num = String.format(".%d", new Object[]{Integer.valueOf(b5 + 1)});
                                        pageBlockOrderedList = pageBlockOrderedList2;
                                    } else {
                                        String unused29 = pageBlockOrderedListItem.num = String.format("%d.", new Object[]{Integer.valueOf(b5 + 1)});
                                        pageBlockOrderedList = pageBlockOrderedList2;
                                    }
                                } else if (ArticleViewer.this.isRtl) {
                                    String unused30 = pageBlockOrderedListItem.num = "." + pageListOrderedItemText.num;
                                    pageBlockOrderedList = pageBlockOrderedList2;
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    pageBlockOrderedList = pageBlockOrderedList2;
                                    sb.append(pageListOrderedItemText.num);
                                    sb.append(".");
                                    String unused31 = pageBlockOrderedListItem.num = sb.toString();
                                }
                                size = size7;
                                str = str4;
                            } else {
                                block2 = block4;
                                pageBlockOrderedList = pageBlockOrderedList2;
                                if (item3 instanceof TLRPC.TL_pageListOrderedItemBlocks) {
                                    TLRPC.TL_pageListOrderedItemBlocks pageListOrderedItemBlocks2 = (TLRPC.TL_pageListOrderedItemBlocks) item3;
                                    if (!pageListOrderedItemBlocks2.blocks.isEmpty()) {
                                        TLRPC.PageBlock unused32 = pageBlockOrderedListItem.blockItem = pageListOrderedItemBlocks2.blocks.get(0);
                                        size = size7;
                                        str = str4;
                                    } else {
                                        TLRPC.TL_pageListOrderedItemText text2 = new TLRPC.TL_pageListOrderedItemText();
                                        TLRPC.TL_textPlain textPlain2 = new TLRPC.TL_textPlain();
                                        size = size7;
                                        str = str4;
                                        textPlain2.text = str;
                                        text2.text = textPlain2;
                                        item3 = text2;
                                    }
                                    if (TextUtils.isEmpty(pageListOrderedItemBlocks2.num)) {
                                        if (ArticleViewer.this.isRtl) {
                                            String unused33 = pageBlockOrderedListItem.num = String.format(".%d", new Object[]{Integer.valueOf(b5 + 1)});
                                        } else {
                                            String unused34 = pageBlockOrderedListItem.num = String.format("%d.", new Object[]{Integer.valueOf(b5 + 1)});
                                        }
                                    } else if (ArticleViewer.this.isRtl) {
                                        String unused35 = pageBlockOrderedListItem.num = "." + pageListOrderedItemBlocks2.num;
                                    } else {
                                        String unused36 = pageBlockOrderedListItem.num = pageListOrderedItemBlocks2.num + ".";
                                    }
                                } else {
                                    size = size7;
                                    str = str4;
                                }
                            }
                            if (originalBlock instanceof TL_pageBlockDetailsChild) {
                                TL_pageBlockDetailsChild child5 = new TL_pageBlockDetailsChild();
                                TLRPC.PageBlock unused37 = child5.parent = ((TL_pageBlockDetailsChild) originalBlock).parent;
                                TLRPC.PageBlock unused38 = child5.block = pageBlockOrderedListItem;
                                addBlock(child5, i, i2 + 1, i3);
                            } else {
                                if (b5 == 0) {
                                    finalBlock = ArticleViewer.this.fixListBlock(originalBlock, pageBlockOrderedListItem);
                                } else {
                                    finalBlock = pageBlockOrderedListItem;
                                }
                                addBlock(finalBlock, i, i2 + 1, i3);
                            }
                            if (item3 instanceof TLRPC.TL_pageListOrderedItemBlocks) {
                                TLRPC.TL_pageListOrderedItemBlocks pageListOrderedItemBlocks3 = (TLRPC.TL_pageListOrderedItemBlocks) item3;
                                int c2 = 1;
                                int size23 = pageListOrderedItemBlocks3.blocks.size();
                                while (c2 < size23) {
                                    String str5 = str;
                                    TLRPC.PageListOrderedItem item4 = item3;
                                    TL_pageBlockOrderedListItem pageBlockOrderedListItem2 = new TL_pageBlockOrderedListItem();
                                    TLRPC.PageBlock unused39 = pageBlockOrderedListItem2.blockItem = pageListOrderedItemBlocks3.blocks.get(c2);
                                    TL_pageBlockOrderedListParent unused40 = pageBlockOrderedListItem2.parent = pageBlockOrderedListParent;
                                    if (originalBlock instanceof TL_pageBlockDetailsChild) {
                                        pageListOrderedItemBlocks = pageListOrderedItemBlocks3;
                                        TL_pageBlockDetailsChild child6 = new TL_pageBlockDetailsChild();
                                        TLRPC.PageBlock unused41 = child6.parent = ((TL_pageBlockDetailsChild) originalBlock).parent;
                                        TLRPC.PageBlock unused42 = child6.block = pageBlockOrderedListItem2;
                                        addBlock(child6, i, i2 + 1, i3);
                                    } else {
                                        pageListOrderedItemBlocks = pageListOrderedItemBlocks3;
                                        try {
                                            addBlock(pageBlockOrderedListItem2, i, i2 + 1, i3);
                                        } catch (Throwable th) {
                                            throw th;
                                        }
                                    }
                                    pageBlockOrderedListParent.items.add(pageBlockOrderedListItem2);
                                    c2++;
                                    pageListOrderedItemBlocks3 = pageListOrderedItemBlocks;
                                    str = str5;
                                    item3 = item4;
                                }
                            }
                            String str6 = str;
                            TLRPC.PageListOrderedItem pageListOrderedItem = item3;
                            b5++;
                            block4 = block2;
                            pageBlockOrderedList2 = pageBlockOrderedList;
                            size7 = size;
                            str4 = str6;
                        }
                        TLRPC.TL_pageBlockOrderedList tL_pageBlockOrderedList = pageBlockOrderedList2;
                        int i7 = size7;
                        return;
                    }
                }
            }
        }

        private void addAllMediaFromBlock(TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
                TLRPC.Photo photo = ArticleViewer.this.getPhotoWithId(pageBlockPhoto.photo_id);
                if (photo != null) {
                    pageBlockPhoto.thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 56, true);
                    pageBlockPhoto.thumbObject = photo;
                    this.photoBlocks.add(block);
                }
            } else if ((block instanceof TLRPC.TL_pageBlockVideo) && ArticleViewer.this.isVideoBlock(block)) {
                TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                TLRPC.Document document = ArticleViewer.this.getDocumentWithId(pageBlockVideo.video_id);
                if (document != null) {
                    pageBlockVideo.thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 56, true);
                    pageBlockVideo.thumbObject = document;
                    this.photoBlocks.add(block);
                }
            } else if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow slideshow = (TLRPC.TL_pageBlockSlideshow) block;
                int count = slideshow.items.size();
                for (int a = 0; a < count; a++) {
                    TLRPC.PageBlock innerBlock = slideshow.items.get(a);
                    innerBlock.groupId = ArticleViewer.this.lastBlockNum;
                    addAllMediaFromBlock(innerBlock);
                }
                ArticleViewer.access$13108(ArticleViewer.this);
            } else if (block instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage collage = (TLRPC.TL_pageBlockCollage) block;
                int count2 = collage.items.size();
                for (int a2 = 0; a2 < count2; a2++) {
                    TLRPC.PageBlock innerBlock2 = collage.items.get(a2);
                    innerBlock2.groupId = ArticleViewer.this.lastBlockNum;
                    addAllMediaFromBlock(innerBlock2);
                }
                ArticleViewer.access$13108(ArticleViewer.this);
            } else if (block instanceof TLRPC.TL_pageBlockCover) {
                addAllMediaFromBlock(((TLRPC.TL_pageBlockCover) block).cover);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view;
            if (viewType != 90) {
                switch (viewType) {
                    case 0:
                        view = new BlockParagraphCell(this.context, this);
                        break;
                    case 1:
                        view = new BlockHeaderCell(this.context, this);
                        break;
                    case 2:
                        view = new BlockDividerCell(this.context);
                        break;
                    case 3:
                        view = new BlockEmbedCell(this.context, this);
                        break;
                    case 4:
                        view = new BlockSubtitleCell(this.context, this);
                        break;
                    case 5:
                        view = new BlockVideoCell(this.context, this, 0);
                        break;
                    case 6:
                        view = new BlockPullquoteCell(this.context, this);
                        break;
                    case 7:
                        view = new BlockBlockquoteCell(this.context, this);
                        break;
                    case 8:
                        view = new BlockSlideshowCell(this.context, this);
                        break;
                    case 9:
                        view = new BlockPhotoCell(this.context, this, 0);
                        break;
                    case 10:
                        view = new BlockAuthorDateCell(this.context, this);
                        break;
                    case 11:
                        view = new BlockTitleCell(this.context, this);
                        break;
                    case 12:
                        view = new BlockListItemCell(this.context, this);
                        break;
                    case 13:
                        view = new BlockFooterCell(this.context, this);
                        break;
                    case 14:
                        view = new BlockPreformattedCell(this.context, this);
                        break;
                    case 15:
                        view = new BlockSubheaderCell(this.context, this);
                        break;
                    case 16:
                        view = new BlockEmbedPostCell(this.context, this);
                        break;
                    case 17:
                        view = new BlockCollageCell(this.context, this);
                        break;
                    case 18:
                        view = new BlockChannelCell(this.context, this, 0);
                        break;
                    case 19:
                        view = new BlockAudioCell(this.context, this);
                        break;
                    case 20:
                        view = new BlockKickerCell(this.context, this);
                        break;
                    case 21:
                        view = new BlockOrderedListItemCell(this.context, this);
                        break;
                    case 22:
                        view = new BlockMapCell(this.context, this, 0);
                        break;
                    case 23:
                        view = new BlockRelatedArticlesCell(this.context, this);
                        break;
                    case 24:
                        view = new BlockDetailsCell(this.context, this);
                        break;
                    case 25:
                        view = new BlockTableCell(this.context, this);
                        break;
                    case 26:
                        view = new BlockRelatedArticlesHeaderCell(this.context, this);
                        break;
                    case 27:
                        view = new BlockDetailsBottomCell(this.context);
                        break;
                    case 28:
                        view = new BlockRelatedArticlesShadowCell(this.context);
                        break;
                    default:
                        TextView textView = new TextView(this.context);
                        textView.setBackgroundColor(SupportMenu.CATEGORY_MASK);
                        textView.setTextColor(-16777216);
                        textView.setTextSize(1, 20.0f);
                        view = textView;
                        break;
                }
            } else {
                AnonymousClass1 r2 = new FrameLayout(this.context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), 1073741824));
                    }
                };
                r2.setTag(90);
                TextView textView2 = new TextView(this.context);
                r2.addView(textView2, LayoutHelper.createFrame(-1.0f, 34.0f, 51, 0.0f, 10.0f, 0.0f, 0.0f));
                textView2.setText(LocaleController.getString("PreviewFeedback", R.string.PreviewFeedback));
                textView2.setTextSize(1, 12.0f);
                textView2.setGravity(17);
                AnonymousClass1 r3 = r2;
                view = r2;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            view.setFocusable(true);
            return new RecyclerListView.Holder(view);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 23 || type == 24) {
                return true;
            }
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position < this.localBlocks.size()) {
                bindBlockToHolder(holder.getItemViewType(), holder, this.localBlocks.get(position), position, this.localBlocks.size());
            } else if (holder.getItemViewType() == 90) {
                TextView textView = (TextView) ((ViewGroup) holder.itemView).getChildAt(0);
                int color = ArticleViewer.this.getSelectedColor();
                if (color == 0) {
                    textView.setTextColor(-8879475);
                    textView.setBackgroundColor(-1183760);
                } else if (color == 1) {
                    textView.setTextColor(ArticleViewer.this.getGrayTextColor());
                    textView.setBackgroundColor(-1712440);
                } else if (color == 2) {
                    textView.setTextColor(ArticleViewer.this.getGrayTextColor());
                    textView.setBackgroundColor(-15000805);
                }
            }
        }

        /* access modifiers changed from: private */
        public void bindBlockToHolder(int type, RecyclerView.ViewHolder holder, TLRPC.PageBlock block, int position, int total) {
            TLRPC.PageBlock originalBlock = block;
            if (block instanceof TLRPC.TL_pageBlockCover) {
                block = ((TLRPC.TL_pageBlockCover) block).cover;
            } else if (block instanceof TL_pageBlockDetailsChild) {
                block = ((TL_pageBlockDetailsChild) block).block;
            }
            if (type != 100) {
                boolean z = false;
                switch (type) {
                    case 0:
                        ((BlockParagraphCell) holder.itemView).setBlock((TLRPC.TL_pageBlockParagraph) block);
                        return;
                    case 1:
                        ((BlockHeaderCell) holder.itemView).setBlock((TLRPC.TL_pageBlockHeader) block);
                        return;
                    case 2:
                        BlockDividerCell blockDividerCell = (BlockDividerCell) holder.itemView;
                        return;
                    case 3:
                        ((BlockEmbedCell) holder.itemView).setBlock((TLRPC.TL_pageBlockEmbed) block);
                        return;
                    case 4:
                        ((BlockSubtitleCell) holder.itemView).setBlock((TLRPC.TL_pageBlockSubtitle) block);
                        return;
                    case 5:
                        BlockVideoCell cell = (BlockVideoCell) holder.itemView;
                        TLRPC.TL_pageBlockVideo tL_pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                        boolean z2 = position == 0;
                        if (position == total - 1) {
                            z = true;
                        }
                        cell.setBlock(tL_pageBlockVideo, z2, z);
                        cell.setParentBlock(originalBlock);
                        return;
                    case 6:
                        ((BlockPullquoteCell) holder.itemView).setBlock((TLRPC.TL_pageBlockPullquote) block);
                        return;
                    case 7:
                        ((BlockBlockquoteCell) holder.itemView).setBlock((TLRPC.TL_pageBlockBlockquote) block);
                        return;
                    case 8:
                        ((BlockSlideshowCell) holder.itemView).setBlock((TLRPC.TL_pageBlockSlideshow) block);
                        return;
                    case 9:
                        BlockPhotoCell cell2 = (BlockPhotoCell) holder.itemView;
                        TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
                        boolean z3 = position == 0;
                        if (position == total - 1) {
                            z = true;
                        }
                        cell2.setBlock(tL_pageBlockPhoto, z3, z);
                        cell2.setParentBlock(originalBlock);
                        return;
                    case 10:
                        ((BlockAuthorDateCell) holder.itemView).setBlock((TLRPC.TL_pageBlockAuthorDate) block);
                        return;
                    case 11:
                        ((BlockTitleCell) holder.itemView).setBlock((TLRPC.TL_pageBlockTitle) block);
                        return;
                    case 12:
                        ((BlockListItemCell) holder.itemView).setBlock((TL_pageBlockListItem) block);
                        return;
                    case 13:
                        ((BlockFooterCell) holder.itemView).setBlock((TLRPC.TL_pageBlockFooter) block);
                        return;
                    case 14:
                        ((BlockPreformattedCell) holder.itemView).setBlock((TLRPC.TL_pageBlockPreformatted) block);
                        return;
                    case 15:
                        ((BlockSubheaderCell) holder.itemView).setBlock((TLRPC.TL_pageBlockSubheader) block);
                        return;
                    case 16:
                        ((BlockEmbedPostCell) holder.itemView).setBlock((TLRPC.TL_pageBlockEmbedPost) block);
                        return;
                    case 17:
                        ((BlockCollageCell) holder.itemView).setBlock((TLRPC.TL_pageBlockCollage) block);
                        return;
                    case 18:
                        ((BlockChannelCell) holder.itemView).setBlock((TLRPC.TL_pageBlockChannel) block);
                        return;
                    case 19:
                        BlockAudioCell cell3 = (BlockAudioCell) holder.itemView;
                        TLRPC.TL_pageBlockAudio tL_pageBlockAudio = (TLRPC.TL_pageBlockAudio) block;
                        boolean z4 = position == 0;
                        if (position == total - 1) {
                            z = true;
                        }
                        cell3.setBlock(tL_pageBlockAudio, z4, z);
                        return;
                    case 20:
                        ((BlockKickerCell) holder.itemView).setBlock((TLRPC.TL_pageBlockKicker) block);
                        return;
                    case 21:
                        ((BlockOrderedListItemCell) holder.itemView).setBlock((TL_pageBlockOrderedListItem) block);
                        return;
                    case 22:
                        BlockMapCell cell4 = (BlockMapCell) holder.itemView;
                        TLRPC.TL_pageBlockMap tL_pageBlockMap = (TLRPC.TL_pageBlockMap) block;
                        boolean z5 = position == 0;
                        if (position == total - 1) {
                            z = true;
                        }
                        cell4.setBlock(tL_pageBlockMap, z5, z);
                        return;
                    case 23:
                        ((BlockRelatedArticlesCell) holder.itemView).setBlock((TL_pageBlockRelatedArticlesChild) block);
                        return;
                    case 24:
                        ((BlockDetailsCell) holder.itemView).setBlock((TLRPC.TL_pageBlockDetails) block);
                        return;
                    case 25:
                        ((BlockTableCell) holder.itemView).setBlock((TLRPC.TL_pageBlockTable) block);
                        return;
                    case 26:
                        ((BlockRelatedArticlesHeaderCell) holder.itemView).setBlock((TLRPC.TL_pageBlockRelatedArticles) block);
                        return;
                    case 27:
                        BlockDetailsBottomCell blockDetailsBottomCell = (BlockDetailsBottomCell) holder.itemView;
                        return;
                    default:
                        return;
                }
            } else {
                ((TextView) holder.itemView).setText("unsupported block " + block);
            }
        }

        /* access modifiers changed from: private */
        public int getTypeForBlock(TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockParagraph) {
                return 0;
            }
            if (block instanceof TLRPC.TL_pageBlockHeader) {
                return 1;
            }
            if (block instanceof TLRPC.TL_pageBlockDivider) {
                return 2;
            }
            if (block instanceof TLRPC.TL_pageBlockEmbed) {
                return 3;
            }
            if (block instanceof TLRPC.TL_pageBlockSubtitle) {
                return 4;
            }
            if (block instanceof TLRPC.TL_pageBlockVideo) {
                return 5;
            }
            if (block instanceof TLRPC.TL_pageBlockPullquote) {
                return 6;
            }
            if (block instanceof TLRPC.TL_pageBlockBlockquote) {
                return 7;
            }
            if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                return 8;
            }
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                return 9;
            }
            if (block instanceof TLRPC.TL_pageBlockAuthorDate) {
                return 10;
            }
            if (block instanceof TLRPC.TL_pageBlockTitle) {
                return 11;
            }
            if (block instanceof TL_pageBlockListItem) {
                return 12;
            }
            if (block instanceof TLRPC.TL_pageBlockFooter) {
                return 13;
            }
            if (block instanceof TLRPC.TL_pageBlockPreformatted) {
                return 14;
            }
            if (block instanceof TLRPC.TL_pageBlockSubheader) {
                return 15;
            }
            if (block instanceof TLRPC.TL_pageBlockEmbedPost) {
                return 16;
            }
            if (block instanceof TLRPC.TL_pageBlockCollage) {
                return 17;
            }
            if (block instanceof TLRPC.TL_pageBlockChannel) {
                return 18;
            }
            if (block instanceof TLRPC.TL_pageBlockAudio) {
                return 19;
            }
            if (block instanceof TLRPC.TL_pageBlockKicker) {
                return 20;
            }
            if (block instanceof TL_pageBlockOrderedListItem) {
                return 21;
            }
            if (block instanceof TLRPC.TL_pageBlockMap) {
                return 22;
            }
            if (block instanceof TL_pageBlockRelatedArticlesChild) {
                return 23;
            }
            if (block instanceof TLRPC.TL_pageBlockDetails) {
                return 24;
            }
            if (block instanceof TLRPC.TL_pageBlockTable) {
                return 25;
            }
            if (block instanceof TLRPC.TL_pageBlockRelatedArticles) {
                return 26;
            }
            if (block instanceof TL_pageBlockDetailsBottom) {
                return 27;
            }
            if (block instanceof TL_pageBlockRelatedArticlesShadow) {
                return 28;
            }
            if (block instanceof TL_pageBlockDetailsChild) {
                return getTypeForBlock(((TL_pageBlockDetailsChild) block).block);
            }
            if (block instanceof TLRPC.TL_pageBlockCover) {
                return getTypeForBlock(((TLRPC.TL_pageBlockCover) block).cover);
            }
            return 100;
        }

        public int getItemViewType(int position) {
            if (position == this.localBlocks.size()) {
                return 90;
            }
            return getTypeForBlock(this.localBlocks.get(position));
        }

        public TLRPC.PageBlock getItem(int position) {
            return this.localBlocks.get(position);
        }

        public int getItemCount() {
            if (ArticleViewer.this.currentPage == null || ArticleViewer.this.currentPage.cached_page == null) {
                return 0;
            }
            return this.localBlocks.size() + 1;
        }

        private boolean isBlockOpened(TL_pageBlockDetailsChild child) {
            TLRPC.PageBlock parentBlock = ArticleViewer.this.getLastNonListPageBlock(child.parent);
            if (parentBlock instanceof TLRPC.TL_pageBlockDetails) {
                return ((TLRPC.TL_pageBlockDetails) parentBlock).open;
            }
            if (!(parentBlock instanceof TL_pageBlockDetailsChild)) {
                return false;
            }
            TL_pageBlockDetailsChild parent = (TL_pageBlockDetailsChild) parentBlock;
            TLRPC.PageBlock parentBlock2 = ArticleViewer.this.getLastNonListPageBlock(parent.block);
            if (!(parentBlock2 instanceof TLRPC.TL_pageBlockDetails) || ((TLRPC.TL_pageBlockDetails) parentBlock2).open) {
                return isBlockOpened(parent);
            }
            return false;
        }

        /* access modifiers changed from: private */
        public void updateRows() {
            this.localBlocks.clear();
            int size = this.blocks.size();
            for (int a = 0; a < size; a++) {
                TLRPC.PageBlock originalBlock = this.blocks.get(a);
                TLRPC.PageBlock block = ArticleViewer.this.getLastNonListPageBlock(originalBlock);
                if (!(block instanceof TL_pageBlockDetailsChild) || isBlockOpened((TL_pageBlockDetailsChild) block)) {
                    this.localBlocks.add(originalBlock);
                }
            }
        }

        /* access modifiers changed from: private */
        public void cleanup() {
            this.blocks.clear();
            this.photoBlocks.clear();
            this.audioBlocks.clear();
            this.audioMessages.clear();
            this.anchors.clear();
            this.anchorsParent.clear();
            this.anchorsOffset.clear();
            notifyDataSetChanged();
        }

        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        public void notifyItemChanged(int position) {
            updateRows();
            super.notifyItemChanged(position);
        }

        public void notifyItemChanged(int position, Object payload) {
            updateRows();
            super.notifyItemChanged(position, payload);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        public void notifyItemInserted(int position) {
            updateRows();
            super.notifyItemInserted(position);
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            super.notifyItemMoved(fromPosition, toPosition);
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeInserted(positionStart, itemCount);
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            super.notifyItemRemoved(position);
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    private class BlockVideoCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
        private int TAG;
        private boolean autoDownload;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private boolean cancelLoading;
        private DrawingText captionLayout;
        private BlockChannelCell channelCell;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockVideo currentBlock;
        private TLRPC.Document currentDocument;
        private int currentType;
        /* access modifiers changed from: private */
        public MessageObject.GroupedMessagePosition groupPosition;
        /* access modifiers changed from: private */
        public ImageReceiver imageView;
        private boolean isFirst;
        private boolean isGif;
        private boolean isLast;
        private WebpageAdapter parentAdapter;
        private TLRPC.PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress2 radialProgress;
        private int textX;
        private int textY;

        public BlockVideoCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageView = imageReceiver;
            imageReceiver.setNeedsQualityThumb(true);
            this.imageView.setShouldGenerateQualityThumb(true);
            this.currentType = type;
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setProgressColor(-1);
            this.radialProgress.setColors(1711276032, (int) Theme.ACTION_BAR_PHOTO_VIEWER_COLOR, -1, -2500135);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            BlockChannelCell blockChannelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            this.channelCell = blockChannelCell;
            addView(blockChannelCell, LayoutHelper.createFrame(-1, -2.0f));
        }

        public void setBlock(TLRPC.TL_pageBlockVideo block, boolean first, boolean last) {
            this.currentBlock = block;
            this.parentBlock = null;
            this.cancelLoading = false;
            TLRPC.Document access$10800 = ArticleViewer.this.getDocumentWithId(block.video_id);
            this.currentDocument = access$10800;
            this.isGif = MessageObject.isGifDocument(access$10800);
            this.isFirst = first;
            this.isLast = last;
            this.channelCell.setVisibility(4);
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(TLRPC.PageBlock block) {
            this.parentBlock = block;
            if (ArticleViewer.this.channelBlock != null && (this.parentBlock instanceof TLRPC.TL_pageBlockCover)) {
                this.channelCell.setBlock(ArticleViewer.this.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0095, code lost:
            if (r1 <= ((float) (r2 + im.bclpbkiauv.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x009b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r12) {
            /*
                r11 = this;
                float r0 = r12.getX()
                float r1 = r12.getY()
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r2 = r11.channelCell
                int r2 = r2.getVisibility()
                r3 = 0
                r4 = 1
                if (r2 != 0) goto L_0x0060
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r2 = r11.channelCell
                float r2 = r2.getTranslationY()
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x0060
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r2 = r11.channelCell
                float r2 = r2.getTranslationY()
                r5 = 1109131264(0x421c0000, float:39.0)
                int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                float r2 = r2 + r5
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 >= 0) goto L_0x0060
                im.bclpbkiauv.ui.ArticleViewer r2 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockChannel r2 = r2.channelBlock
                if (r2 == 0) goto L_0x005f
                int r2 = r12.getAction()
                if (r2 != r4) goto L_0x005f
                im.bclpbkiauv.ui.ArticleViewer r2 = im.bclpbkiauv.ui.ArticleViewer.this
                int r2 = r2.currentAccount
                im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
                im.bclpbkiauv.ui.ArticleViewer r5 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockChannel r5 = r5.channelBlock
                im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r5.channel
                java.lang.String r5 = r5.username
                im.bclpbkiauv.ui.ArticleViewer r6 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.actionbar.BaseFragment r6 = r6.parentFragment
                r7 = 2
                r2.openByUserName(r5, r6, r7)
                im.bclpbkiauv.ui.ArticleViewer r2 = im.bclpbkiauv.ui.ArticleViewer.this
                r2.close(r3, r4)
            L_0x005f:
                return r4
            L_0x0060:
                int r2 = r12.getAction()
                if (r2 != 0) goto L_0x00a4
                im.bclpbkiauv.messenger.ImageReceiver r2 = r11.imageView
                boolean r2 = r2.isInsideImage(r0, r1)
                if (r2 == 0) goto L_0x00a4
                int r2 = r11.buttonState
                r5 = -1
                if (r2 == r5) goto L_0x0097
                int r2 = r11.buttonX
                float r5 = (float) r2
                int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r5 < 0) goto L_0x0097
                r5 = 1111490560(0x42400000, float:48.0)
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                int r2 = r2 + r6
                float r2 = (float) r2
                int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r2 > 0) goto L_0x0097
                int r2 = r11.buttonY
                float r6 = (float) r2
                int r6 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
                if (r6 < 0) goto L_0x0097
                int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                int r2 = r2 + r5
                float r2 = (float) r2
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x009b
            L_0x0097:
                int r2 = r11.buttonState
                if (r2 != 0) goto L_0x00a1
            L_0x009b:
                r11.buttonPressed = r4
                r11.invalidate()
                goto L_0x00d1
            L_0x00a1:
                r11.photoPressed = r4
                goto L_0x00d1
            L_0x00a4:
                int r2 = r12.getAction()
                if (r2 != r4) goto L_0x00c8
                boolean r2 = r11.photoPressed
                if (r2 == 0) goto L_0x00b8
                r11.photoPressed = r3
                im.bclpbkiauv.ui.ArticleViewer r2 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r5 = r11.currentBlock
                r2.openPhoto(r5)
                goto L_0x00d1
            L_0x00b8:
                int r2 = r11.buttonPressed
                if (r2 != r4) goto L_0x00d1
                r11.buttonPressed = r3
                r11.playSoundEffect(r3)
                r11.didPressedButton(r4)
                r11.invalidate()
                goto L_0x00d1
            L_0x00c8:
                int r2 = r12.getAction()
                r5 = 3
                if (r2 != r5) goto L_0x00d1
                r11.photoPressed = r3
            L_0x00d1:
                boolean r2 = r11.photoPressed
                if (r2 != 0) goto L_0x0103
                int r2 = r11.buttonPressed
                if (r2 != 0) goto L_0x0103
                im.bclpbkiauv.ui.ArticleViewer r5 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r8 = r11.captionLayout
                int r9 = r11.textX
                int r10 = r11.textY
                r6 = r12
                r7 = r11
                boolean r2 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10)
                if (r2 != 0) goto L_0x0103
                im.bclpbkiauv.ui.ArticleViewer r5 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r8 = r11.creditLayout
                int r9 = r11.textX
                int r2 = r11.textY
                int r6 = r11.creditOffset
                int r10 = r2 + r6
                r6 = r12
                r7 = r11
                boolean r2 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10)
                if (r2 != 0) goto L_0x0103
                boolean r2 = super.onTouchEvent(r12)
                if (r2 == 0) goto L_0x0104
            L_0x0103:
                r3 = 1
            L_0x0104:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.BlockVideoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:99:0x0316, code lost:
            if ((im.bclpbkiauv.ui.ArticleViewer.WebpageAdapter.access$6900(r8.parentAdapter).get(1) instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockChannel) != false) goto L_0x031a;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r30, int r31) {
            /*
                r29 = this;
                r8 = r29
                int r0 = android.view.View.MeasureSpec.getSize(r30)
                r1 = 0
                int r2 = r8.currentType
                r9 = 2
                r10 = 1
                if (r2 != r10) goto L_0x0023
                android.view.ViewParent r2 = r29.getParent()
                android.view.View r2 = (android.view.View) r2
                int r0 = r2.getMeasuredWidth()
                android.view.ViewParent r2 = r29.getParent()
                android.view.View r2 = (android.view.View) r2
                int r1 = r2.getMeasuredHeight()
                r11 = r0
                goto L_0x0045
            L_0x0023:
                if (r2 != r9) goto L_0x0044
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = r8.groupPosition
                float r2 = r2.ph
                android.graphics.Point r3 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r3 = r3.x
                android.graphics.Point r4 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r4 = r4.y
                int r3 = java.lang.Math.max(r3, r4)
                float r3 = (float) r3
                float r2 = r2 * r3
                r3 = 1056964608(0x3f000000, float:0.5)
                float r2 = r2 * r3
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r1 = (int) r2
                r11 = r0
                goto L_0x0045
            L_0x0044:
                r11 = r0
            L_0x0045:
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r0 = r8.currentBlock
                if (r0 == 0) goto L_0x032a
                r2 = r11
                r3 = r1
                int r4 = r8.currentType
                r5 = 1099956224(0x41900000, float:18.0)
                if (r4 != 0) goto L_0x0071
                int r0 = r0.level
                if (r0 <= 0) goto L_0x0071
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r0 = r8.currentBlock
                int r0 = r0.level
                int r0 = r0 * 14
                float r0 = (float) r0
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                int r0 = r0 + r4
                r4 = r0
                r8.textX = r0
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                int r0 = r0 + r4
                int r2 = r2 - r0
                r0 = r2
                r12 = r0
                goto L_0x0081
            L_0x0071:
                r4 = 0
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                r8.textX = r0
                r0 = 1108344832(0x42100000, float:36.0)
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                int r0 = r11 - r0
                r12 = r0
            L_0x0081:
                im.bclpbkiauv.tgnet.TLRPC$Document r0 = r8.currentDocument
                r14 = 0
                if (r0 == 0) goto L_0x0273
                r0 = 1111490560(0x42400000, float:48.0)
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                im.bclpbkiauv.tgnet.TLRPC$Document r5 = r8.currentDocument
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
                r6 = 48
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r5 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
                int r6 = r8.currentType
                if (r6 != 0) goto L_0x0130
                r6 = 0
                r15 = 0
                im.bclpbkiauv.tgnet.TLRPC$Document r13 = r8.currentDocument
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r13 = r13.attributes
                int r13 = r13.size()
            L_0x00a4:
                if (r15 >= r13) goto L_0x00c6
                im.bclpbkiauv.tgnet.TLRPC$Document r10 = r8.currentDocument
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r10 = r10.attributes
                java.lang.Object r10 = r10.get(r15)
                im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute r10 = (im.bclpbkiauv.tgnet.TLRPC.DocumentAttribute) r10
                boolean r7 = r10 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeVideo
                if (r7 == 0) goto L_0x00c1
                float r7 = (float) r2
                int r9 = r10.w
                float r9 = (float) r9
                float r7 = r7 / r9
                int r9 = r10.h
                float r9 = (float) r9
                float r9 = r9 * r7
                int r1 = (int) r9
                r6 = 1
                goto L_0x00c6
            L_0x00c1:
                int r15 = r15 + 1
                r9 = 2
                r10 = 1
                goto L_0x00a4
            L_0x00c6:
                r7 = 1120403456(0x42c80000, float:100.0)
                if (r5 == 0) goto L_0x00ce
                int r9 = r5.w
                float r9 = (float) r9
                goto L_0x00d0
            L_0x00ce:
                r9 = 1120403456(0x42c80000, float:100.0)
            L_0x00d0:
                if (r5 == 0) goto L_0x00d6
                int r10 = r5.h
                float r10 = (float) r10
                goto L_0x00d8
            L_0x00d6:
                r10 = 1120403456(0x42c80000, float:100.0)
            L_0x00d8:
                if (r6 != 0) goto L_0x00df
                float r13 = (float) r2
                float r13 = r13 / r9
                float r15 = r13 * r10
                int r1 = (int) r15
            L_0x00df:
                im.bclpbkiauv.tgnet.TLRPC$PageBlock r13 = r8.parentBlock
                boolean r13 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockCover
                if (r13 == 0) goto L_0x00ea
                int r1 = java.lang.Math.min(r1, r2)
                goto L_0x0124
            L_0x00ea:
                im.bclpbkiauv.ui.ArticleViewer r13 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.components.RecyclerListView[] r13 = r13.listView
                r13 = r13[r14]
                int r13 = r13.getMeasuredWidth()
                im.bclpbkiauv.ui.ArticleViewer r15 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.components.RecyclerListView[] r15 = r15.listView
                r15 = r15[r14]
                int r15 = r15.getMeasuredHeight()
                int r13 = java.lang.Math.max(r13, r15)
                r15 = 1113587712(0x42600000, float:56.0)
                int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
                int r13 = r13 - r15
                float r13 = (float) r13
                r15 = 1063675494(0x3f666666, float:0.9)
                float r13 = r13 * r15
                int r13 = (int) r13
                if (r1 <= r13) goto L_0x0124
                r1 = r13
                float r15 = (float) r1
                float r15 = r15 / r10
                float r14 = r15 * r9
                int r2 = (int) r14
                int r14 = r11 - r4
                int r14 = r14 - r2
                r16 = 2
                int r14 = r14 / 2
                int r4 = r4 + r14
            L_0x0124:
                if (r1 != 0) goto L_0x012b
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                goto L_0x012e
            L_0x012b:
                if (r1 >= r0) goto L_0x012e
                r1 = r0
            L_0x012e:
                r3 = r1
                goto L_0x0151
            L_0x0130:
                r7 = 2
                if (r6 != r7) goto L_0x0151
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r6 = r8.groupPosition
                int r6 = r6.flags
                r6 = r6 & r7
                if (r6 != 0) goto L_0x0141
                r6 = 1073741824(0x40000000, float:2.0)
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                int r2 = r2 - r7
            L_0x0141:
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r6 = r8.groupPosition
                int r6 = r6.flags
                r6 = r6 & 8
                if (r6 != 0) goto L_0x0152
                r6 = 1073741824(0x40000000, float:2.0)
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                int r3 = r3 - r7
                goto L_0x0152
            L_0x0151:
            L_0x0152:
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                im.bclpbkiauv.tgnet.TLRPC$Document r7 = r8.currentDocument
                r6.setQualityThumbDocument(r7)
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                boolean r7 = r8.isFirst
                if (r7 != 0) goto L_0x0175
                int r7 = r8.currentType
                r9 = 1
                if (r7 == r9) goto L_0x0175
                r9 = 2
                if (r7 == r9) goto L_0x0175
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r7 = r8.currentBlock
                int r7 = r7.level
                if (r7 <= 0) goto L_0x016e
                goto L_0x0175
            L_0x016e:
                r7 = 1090519040(0x41000000, float:8.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                goto L_0x0176
            L_0x0175:
                r9 = 0
            L_0x0176:
                r6.setImageCoords(r4, r9, r2, r3)
                boolean r6 = r8.isGif
                r7 = 0
                if (r6 == 0) goto L_0x020d
                im.bclpbkiauv.ui.ArticleViewer r6 = im.bclpbkiauv.ui.ArticleViewer.this
                int r6 = r6.currentAccount
                im.bclpbkiauv.messenger.DownloadController r6 = im.bclpbkiauv.messenger.DownloadController.getInstance(r6)
                r9 = 4
                im.bclpbkiauv.tgnet.TLRPC$Document r10 = r8.currentDocument
                int r10 = r10.size
                boolean r6 = r6.canDownloadMedia(r9, r10)
                r8.autoDownload = r6
                im.bclpbkiauv.tgnet.TLRPC$Document r6 = r8.currentDocument
                r9 = 1
                java.io.File r6 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r6, r9)
                boolean r9 = r8.autoDownload
                if (r9 != 0) goto L_0x01da
                boolean r9 = r6.exists()
                if (r9 == 0) goto L_0x01a5
                goto L_0x01da
            L_0x01a5:
                im.bclpbkiauv.messenger.ImageReceiver r7 = r8.imageView
                im.bclpbkiauv.tgnet.TLRPC$Document r9 = r8.currentDocument
                im.bclpbkiauv.messenger.ImageLocation r9 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r9)
                r7.setStrippedLocation(r9)
                im.bclpbkiauv.messenger.ImageReceiver r7 = r8.imageView
                r18 = 0
                r19 = 0
                r20 = 0
                r21 = 0
                im.bclpbkiauv.tgnet.TLRPC$Document r9 = r8.currentDocument
                im.bclpbkiauv.messenger.ImageLocation r22 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r5, r9)
                r24 = 0
                im.bclpbkiauv.tgnet.TLRPC$Document r9 = r8.currentDocument
                int r9 = r9.size
                r26 = 0
                im.bclpbkiauv.ui.ArticleViewer r10 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$WebPage r27 = r10.currentPage
                r28 = 1
                java.lang.String r23 = "80_80_b"
                r17 = r7
                r25 = r9
                r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)
                goto L_0x020c
            L_0x01da:
                im.bclpbkiauv.messenger.ImageReceiver r9 = r8.imageView
                r9.setStrippedLocation(r7)
                im.bclpbkiauv.messenger.ImageReceiver r7 = r8.imageView
                im.bclpbkiauv.tgnet.TLRPC$Document r9 = r8.currentDocument
                im.bclpbkiauv.messenger.ImageLocation r18 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r9)
                r19 = 0
                r20 = 0
                r21 = 0
                im.bclpbkiauv.tgnet.TLRPC$Document r9 = r8.currentDocument
                im.bclpbkiauv.messenger.ImageLocation r22 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r5, r9)
                r24 = 0
                im.bclpbkiauv.tgnet.TLRPC$Document r9 = r8.currentDocument
                int r9 = r9.size
                r26 = 0
                im.bclpbkiauv.ui.ArticleViewer r10 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$WebPage r27 = r10.currentPage
                r28 = 1
                java.lang.String r23 = "80_80_b"
                r17 = r7
                r25 = r9
                r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)
            L_0x020c:
                goto L_0x0231
            L_0x020d:
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                r6.setStrippedLocation(r7)
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                r18 = 0
                r19 = 0
                im.bclpbkiauv.tgnet.TLRPC$Document r7 = r8.currentDocument
                im.bclpbkiauv.messenger.ImageLocation r20 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r5, r7)
                r22 = 0
                r23 = 0
                im.bclpbkiauv.ui.ArticleViewer r7 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$WebPage r24 = r7.currentPage
                r25 = 1
                java.lang.String r21 = "80_80_b"
                r17 = r6
                r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25)
            L_0x0231:
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                r7 = 1
                r6.setAspectFit(r7)
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                int r6 = r6.getImageX()
                float r6 = (float) r6
                im.bclpbkiauv.messenger.ImageReceiver r7 = r8.imageView
                int r7 = r7.getImageWidth()
                int r7 = r7 - r0
                float r7 = (float) r7
                r9 = 1073741824(0x40000000, float:2.0)
                float r7 = r7 / r9
                float r6 = r6 + r7
                int r6 = (int) r6
                r8.buttonX = r6
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                int r6 = r6.getImageY()
                float r6 = (float) r6
                im.bclpbkiauv.messenger.ImageReceiver r7 = r8.imageView
                int r7 = r7.getImageHeight()
                int r7 = r7 - r0
                float r7 = (float) r7
                r9 = 1073741824(0x40000000, float:2.0)
                float r7 = r7 / r9
                float r6 = r6 + r7
                int r6 = (int) r6
                r8.buttonY = r6
                im.bclpbkiauv.ui.components.RadialProgress2 r7 = r8.radialProgress
                int r9 = r8.buttonX
                int r10 = r9 + r0
                int r13 = r6 + r0
                r7.setProgressRect(r9, r6, r10, r13)
                r7 = r1
                r9 = r2
                r10 = r3
                r13 = r4
                goto L_0x0277
            L_0x0273:
                r7 = r1
                r9 = r2
                r10 = r3
                r13 = r4
            L_0x0277:
                int r0 = r8.currentType
                if (r0 != 0) goto L_0x02e2
                im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                r2 = 0
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r1 = r8.currentBlock
                im.bclpbkiauv.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                im.bclpbkiauv.tgnet.TLRPC$RichText r3 = r1.text
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r5 = r8.currentBlock
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r6 = r8.parentAdapter
                r1 = r29
                r4 = r12
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6)
                r8.captionLayout = r0
                r14 = 1082130432(0x40800000, float:4.0)
                if (r0 == 0) goto L_0x02aa
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r1 = r8.captionLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                r8.creditOffset = r0
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                int r0 = r0 + r1
                int r7 = r7 + r0
                r15 = r7
                goto L_0x02ab
            L_0x02aa:
                r15 = r7
            L_0x02ab:
                im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                r2 = 0
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r1 = r8.currentBlock
                im.bclpbkiauv.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                im.bclpbkiauv.tgnet.TLRPC$RichText r3 = r1.credit
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r5 = r8.currentBlock
                im.bclpbkiauv.ui.ArticleViewer r1 = im.bclpbkiauv.ui.ArticleViewer.this
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x02c3
                android.text.Layout$Alignment r1 = im.bclpbkiauv.ui.components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x02c5
            L_0x02c3:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x02c5:
                r6 = r1
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r7 = r8.parentAdapter
                r1 = r29
                r4 = r12
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText((android.view.View) r1, (java.lang.CharSequence) r2, (im.bclpbkiauv.tgnet.TLRPC.RichText) r3, (int) r4, (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r5, (android.text.Layout.Alignment) r6, (im.bclpbkiauv.ui.ArticleViewer.WebpageAdapter) r7)
                r8.creditLayout = r0
                if (r0 == 0) goto L_0x02e1
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r1 = r8.creditLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r7 = r15 + r0
                goto L_0x02e2
            L_0x02e1:
                r7 = r15
            L_0x02e2:
                boolean r0 = r8.isFirst
                if (r0 != 0) goto L_0x02f7
                int r0 = r8.currentType
                if (r0 != 0) goto L_0x02f7
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r0 = r8.currentBlock
                int r0 = r0.level
                if (r0 > 0) goto L_0x02f7
                r0 = 1090519040(0x41000000, float:8.0)
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                int r7 = r7 + r1
            L_0x02f7:
                im.bclpbkiauv.tgnet.TLRPC$PageBlock r0 = r8.parentBlock
                boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockCover
                if (r0 == 0) goto L_0x0319
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r0 = r8.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                int r0 = r0.size()
                r1 = 1
                if (r0 <= r1) goto L_0x0319
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r0 = r8.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                java.lang.Object r0 = r0.get(r1)
                boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockChannel
                if (r0 == 0) goto L_0x0319
                goto L_0x031a
            L_0x0319:
                r1 = 0
            L_0x031a:
                r0 = r1
                int r1 = r8.currentType
                r2 = 2
                if (r1 == r2) goto L_0x0329
                if (r0 != 0) goto L_0x0329
                r1 = 1090519040(0x41000000, float:8.0)
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
                int r7 = r7 + r1
            L_0x0329:
                goto L_0x032b
            L_0x032a:
                r7 = 1
            L_0x032b:
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r0 = r8.channelCell
                r1 = r30
                r2 = r31
                r0.measure(r1, r2)
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r0 = r8.channelCell
                im.bclpbkiauv.messenger.ImageReceiver r3 = r8.imageView
                int r3 = r3.getImageHeight()
                r4 = 1109131264(0x421c0000, float:39.0)
                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                int r3 = r3 - r4
                float r3 = (float) r3
                r0.setTranslationY(r3)
                r8.setMeasuredDimension(r11, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.BlockVideoCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (!this.imageView.hasBitmapImage() || this.imageView.getCurrentAlpha() != 1.0f) {
                    canvas.drawRect(this.imageView.getDrawRegion(), ArticleViewer.photoBackgroundPaint);
                }
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                this.textY = this.imageView.getImageY() + this.imageView.getImageHeight() + AndroidUtilities.dp(8.0f);
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private int getIconForCurrentState() {
            int i = this.buttonState;
            if (i == 0) {
                return 2;
            }
            if (i == 1) {
                return 3;
            }
            if (i == 2) {
                return 8;
            }
            if (i == 3) {
                return 0;
            }
            return 4;
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean fileExists = FileLoader.getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setIcon(4, false, false);
            } else if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                if (!this.isGif) {
                    this.buttonState = 3;
                } else {
                    this.buttonState = -1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, (MessageObject) null, this);
                float setProgress = 0.0f;
                boolean progressVisible = false;
                if (FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    progressVisible = true;
                    this.buttonState = 1;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                } else if (this.cancelLoading || !this.autoDownload || !this.isGif) {
                    this.buttonState = 0;
                } else {
                    progressVisible = true;
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), progressVisible, animated);
                this.radialProgress.setProgress(setProgress, false);
                invalidate();
            }
        }

        private void didPressedButton(boolean animated) {
            int i = this.buttonState;
            if (i == 0) {
                this.cancelLoading = false;
                this.radialProgress.setProgress(0.0f, false);
                if (this.isGif) {
                    this.imageView.setImage(ImageLocation.getForDocument(this.currentDocument), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(this.currentDocument.thumbs, 40), this.currentDocument), "80_80_b", this.currentDocument.size, (String) null, ArticleViewer.this.currentPage, 1);
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, ArticleViewer.this.currentPage, 1, 1);
                }
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                invalidate();
            } else if (i == 1) {
                this.cancelLoading = true;
                if (this.isGif) {
                    this.imageView.cancelLoadImage();
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                }
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            } else if (i == 2) {
                this.imageView.setAllowStartAnimation(true);
                this.imageView.startAnimation();
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else if (i == 3) {
                ArticleViewer.this.openPhoto(this.currentBlock);
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageView.onAttachedToWindow();
            updateButtonState(false);
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            if (this.isGif) {
                this.buttonState = 2;
                didPressedButton(true);
                return;
            }
            updateButtonState(true);
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, true);
            if (this.buttonState != 1) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("AttachVideo", R.string.AttachVideo));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            info.setText(sb.toString());
        }
    }

    private class BlockAudioCell extends View implements DownloadController.FileDownloadProgressListener {
        private int TAG;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockAudio currentBlock;
        private TLRPC.Document currentDocument;
        private MessageObject currentMessageObject;
        private StaticLayout durationLayout;
        private boolean isFirst;
        private boolean isLast;
        private String lastTimeString;
        private WebpageAdapter parentAdapter;
        private RadialProgress2 radialProgress;
        private SeekBar seekBar;
        private int seekBarX;
        private int seekBarY;
        private int textX;
        private int textY = AndroidUtilities.dp(54.0f);
        private StaticLayout titleLayout;

        public BlockAudioCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setBackgroundStroke(AndroidUtilities.dp(3.0f));
            this.radialProgress.setCircleRadius(AndroidUtilities.dp(24.0f));
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            SeekBar seekBar2 = new SeekBar(context);
            this.seekBar = seekBar2;
            seekBar2.setDelegate(new SeekBar.SeekBarDelegate() {
                public /* synthetic */ void onSeekBarContinuousDrag(float f) {
                    SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
                }

                public final void onSeekBarDrag(float f) {
                    ArticleViewer.BlockAudioCell.this.lambda$new$0$ArticleViewer$BlockAudioCell(f);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$ArticleViewer$BlockAudioCell(float progress) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                messageObject.audioProgress = progress;
                MediaController.getInstance().seekToProgress(this.currentMessageObject, progress);
            }
        }

        public void setBlock(TLRPC.TL_pageBlockAudio block, boolean first, boolean last) {
            this.currentBlock = block;
            MessageObject messageObject = (MessageObject) this.parentAdapter.audioBlocks.get(this.currentBlock);
            this.currentMessageObject = messageObject;
            this.currentDocument = messageObject.getDocument();
            this.isFirst = first;
            this.isLast = last;
            this.radialProgress.setProgressColor(ArticleViewer.this.getTextColor());
            this.seekBar.setColors(ArticleViewer.this.getTextColor() & 1073741823, ArticleViewer.this.getTextColor() & 1073741823, ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor());
            updateButtonState(false);
            requestLayout();
        }

        public MessageObject getMessageObject() {
            return this.currentMessageObject;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0064, code lost:
            if (r1 <= ((float) (r4 + im.bclpbkiauv.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x006a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0068, code lost:
            if (r12.buttonState == 0) goto L_0x006a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x006a, code lost:
            r12.buttonPressed = 1;
            invalidate();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r13) {
            /*
                r12 = this;
                float r0 = r13.getX()
                float r1 = r13.getY()
                im.bclpbkiauv.ui.components.SeekBar r2 = r12.seekBar
                int r3 = r13.getAction()
                float r4 = r13.getX()
                int r5 = r12.seekBarX
                float r5 = (float) r5
                float r4 = r4 - r5
                float r5 = r13.getY()
                int r6 = r12.seekBarY
                float r6 = (float) r6
                float r5 = r5 - r6
                boolean r2 = r2.onTouch(r3, r4, r5)
                r3 = 1
                if (r2 == 0) goto L_0x0036
                int r4 = r13.getAction()
                if (r4 != 0) goto L_0x0032
                android.view.ViewParent r4 = r12.getParent()
                r4.requestDisallowInterceptTouchEvent(r3)
            L_0x0032:
                r12.invalidate()
                return r3
            L_0x0036:
                int r4 = r13.getAction()
                r5 = 0
                if (r4 != 0) goto L_0x0070
                int r4 = r12.buttonState
                r6 = -1
                if (r4 == r6) goto L_0x0066
                int r4 = r12.buttonX
                float r6 = (float) r4
                int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r6 < 0) goto L_0x0066
                r6 = 1111490560(0x42400000, float:48.0)
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                int r4 = r4 + r7
                float r4 = (float) r4
                int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r4 > 0) goto L_0x0066
                int r4 = r12.buttonY
                float r7 = (float) r4
                int r7 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
                if (r7 < 0) goto L_0x0066
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                int r4 = r4 + r6
                float r4 = (float) r4
                int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r4 <= 0) goto L_0x006a
            L_0x0066:
                int r4 = r12.buttonState
                if (r4 != 0) goto L_0x008f
            L_0x006a:
                r12.buttonPressed = r3
                r12.invalidate()
                goto L_0x008f
            L_0x0070:
                int r4 = r13.getAction()
                if (r4 != r3) goto L_0x0086
                int r4 = r12.buttonPressed
                if (r4 != r3) goto L_0x008f
                r12.buttonPressed = r5
                r12.playSoundEffect(r5)
                r12.didPressedButton(r3)
                r12.invalidate()
                goto L_0x008f
            L_0x0086:
                int r4 = r13.getAction()
                r6 = 3
                if (r4 != r6) goto L_0x008f
                r12.buttonPressed = r5
            L_0x008f:
                int r4 = r12.buttonPressed
                if (r4 != 0) goto L_0x00bf
                im.bclpbkiauv.ui.ArticleViewer r6 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r9 = r12.captionLayout
                int r10 = r12.textX
                int r11 = r12.textY
                r7 = r13
                r8 = r12
                boolean r4 = r6.checkLayoutForLinks(r7, r8, r9, r10, r11)
                if (r4 != 0) goto L_0x00bf
                im.bclpbkiauv.ui.ArticleViewer r6 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r9 = r12.creditLayout
                int r10 = r12.textX
                int r4 = r12.textY
                int r7 = r12.creditOffset
                int r11 = r4 + r7
                r7 = r13
                r8 = r12
                boolean r4 = r6.checkLayoutForLinks(r7, r8, r9, r10, r11)
                if (r4 != 0) goto L_0x00bf
                boolean r4 = super.onTouchEvent(r13)
                if (r4 == 0) goto L_0x00be
                goto L_0x00bf
            L_0x00be:
                r3 = 0
            L_0x00bf:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.BlockAudioCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            SpannableStringBuilder stringBuilder;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height2 = AndroidUtilities.dp(54.0f);
            TLRPC.TL_pageBlockAudio tL_pageBlockAudio = this.currentBlock;
            if (tL_pageBlockAudio != null) {
                if (tL_pageBlockAudio.level > 0) {
                    this.textX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(18.0f);
                }
                int textWidth = (width - this.textX) - AndroidUtilities.dp(18.0f);
                int size = AndroidUtilities.dp(44.0f);
                this.buttonX = AndroidUtilities.dp(16.0f);
                int dp = AndroidUtilities.dp(5.0f);
                this.buttonY = dp;
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i = this.buttonX;
                radialProgress2.setProgressRect(i, dp, i + size, dp + size);
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                this.captionLayout = access$13600;
                if (access$13600 != null) {
                    int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp2;
                    height = height2 + dp2 + AndroidUtilities.dp(4.0f);
                } else {
                    height = height2;
                }
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13700;
                if (access$13700 != null) {
                    height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                if (!this.isFirst && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                String author = this.currentMessageObject.getMusicAuthor(false);
                String title = this.currentMessageObject.getMusicTitle(false);
                int dp3 = this.buttonX + AndroidUtilities.dp(50.0f) + size;
                this.seekBarX = dp3;
                int w = (width - dp3) - AndroidUtilities.dp(18.0f);
                if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(author)) {
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                        stringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{author, title}));
                    } else if (!TextUtils.isEmpty(title)) {
                        stringBuilder = new SpannableStringBuilder(title);
                    } else {
                        stringBuilder = new SpannableStringBuilder(author);
                    }
                    if (!TextUtils.isEmpty(author)) {
                        stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, author.length(), 18);
                    }
                    this.titleLayout = new StaticLayout(TextUtils.ellipsize(stringBuilder, Theme.chat_audioTitlePaint, (float) w, TextUtils.TruncateAt.END), ArticleViewer.audioTimePaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.seekBarY = this.buttonY + ((size - AndroidUtilities.dp(30.0f)) / 2) + AndroidUtilities.dp(11.0f);
                } else {
                    this.titleLayout = null;
                    this.seekBarY = this.buttonY + ((size - AndroidUtilities.dp(30.0f)) / 2);
                }
                this.seekBar.setSize(w, AndroidUtilities.dp(30.0f));
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            updatePlayingMessageProgress();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.radialProgress.setColors(ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor());
                this.radialProgress.draw(canvas);
                canvas.save();
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
                canvas.restore();
                if (this.durationLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.buttonX + AndroidUtilities.dp(54.0f)), (float) (this.seekBarY + AndroidUtilities.dp(6.0f)));
                    this.durationLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.titleLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.buttonX + AndroidUtilities.dp(54.0f)), (float) (this.seekBarY - AndroidUtilities.dp(16.0f)));
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private int getIconForCurrentState() {
            int i = this.buttonState;
            if (i == 1) {
                return 1;
            }
            if (i == 2) {
                return 2;
            }
            if (i == 3) {
                return 3;
            }
            return 0;
        }

        public void updatePlayingMessageProgress() {
            if (this.currentDocument != null && this.currentMessageObject != null) {
                if (!this.seekBar.isDragging()) {
                    this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                }
                int duration = 0;
                if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    int a = 0;
                    while (true) {
                        if (a >= this.currentDocument.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = this.currentDocument.attributes.get(a);
                        if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                            duration = attribute.duration;
                            break;
                        }
                        a++;
                    }
                } else {
                    duration = this.currentMessageObject.audioProgressSec;
                }
                String timeString = String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)});
                String str = this.lastTimeString;
                if (str == null || (str != null && !str.equals(timeString))) {
                    this.lastTimeString = timeString;
                    ArticleViewer.audioTimePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
                    this.durationLayout = new StaticLayout(timeString, ArticleViewer.audioTimePaint, (int) Math.ceil((double) ArticleViewer.audioTimePaint.measureText(timeString)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
                ArticleViewer.audioTimePaint.setColor(ArticleViewer.this.getTextColor());
                invalidate();
            }
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean fileExists = FileLoader.getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setIcon(4, false, false);
                return;
            }
            if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, (MessageObject) null, this);
                if (!FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                } else {
                    this.buttonState = 3;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                }
            }
            updatePlayingMessageProgress();
        }

        private void didPressedButton(boolean animated) {
            int i = this.buttonState;
            if (i == 0) {
                if (MediaController.getInstance().setPlaylist(this.parentAdapter.audioMessages, this.currentMessageObject, false)) {
                    this.buttonState = 1;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    invalidate();
                }
            } else if (i == 1) {
                if (MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    invalidate();
                }
            } else if (i == 2) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, ArticleViewer.this.currentPage, 1, 1);
                this.buttonState = 3;
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                invalidate();
            } else if (i == 3) {
                FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateButtonState(false);
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(true);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, true);
            if (this.buttonState != 3) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    private class BlockEmbedPostCell extends View {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private ImageReceiver avatarImageView;
        private boolean avatarVisible;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockEmbedPost currentBlock;
        private DrawingText dateLayout;
        private int dateX;
        private int lineHeight;
        private DrawingText nameLayout;
        private int nameX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;

        public BlockEmbedPostCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.avatarImageView = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(20.0f));
            this.avatarImageView.setImageCoords(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
        }

        public void setBlock(TLRPC.TL_pageBlockEmbedPost block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockEmbedPost tL_pageBlockEmbedPost = this.currentBlock;
            if (tL_pageBlockEmbedPost != null) {
                if (tL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption) {
                    int textWidth = width - AndroidUtilities.dp(50.0f);
                    DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                    this.captionLayout = access$13600;
                    if (access$13600 != null) {
                        int dp = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        this.creditOffset = dp;
                        height = 0 + dp + AndroidUtilities.dp(4.0f);
                    } else {
                        height = 0;
                    }
                    DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.creditLayout = access$13700;
                    if (access$13700 != null) {
                        height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    }
                    this.textX = AndroidUtilities.dp(18.0f);
                    this.textY = AndroidUtilities.dp(4.0f);
                } else {
                    int i = 0;
                    boolean z = tL_pageBlockEmbedPost.author_photo_id != 0;
                    this.avatarVisible = z;
                    if (z) {
                        TLRPC.Photo photo = ArticleViewer.this.getPhotoWithId(this.currentBlock.author_photo_id);
                        boolean z2 = photo instanceof TLRPC.TL_photo;
                        this.avatarVisible = z2;
                        if (z2) {
                            this.avatarDrawable.setInfo(0, this.currentBlock.author, (String) null);
                            this.avatarImageView.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(40.0f), true), photo), "40_40", (Drawable) this.avatarDrawable, 0, (String) null, (Object) ArticleViewer.this.currentPage, 1);
                        }
                    }
                    this.nameLayout = ArticleViewer.this.createLayoutForText(this, this.currentBlock.author, (TLRPC.RichText) null, width - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), 0, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                    if (this.currentBlock.date != 0) {
                        ArticleViewer articleViewer = ArticleViewer.this;
                        String format = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.date) * 1000);
                        if (this.avatarVisible) {
                            i = 54;
                        }
                        this.dateLayout = articleViewer.createLayoutForText(this, format, (TLRPC.RichText) null, width - AndroidUtilities.dp((float) (i + 50)), this.currentBlock, this.parentAdapter);
                    } else {
                        this.dateLayout = null;
                    }
                    height = AndroidUtilities.dp(56.0f);
                    if (this.currentBlock.blocks.isEmpty()) {
                        int textWidth2 = width - AndroidUtilities.dp(50.0f);
                        DrawingText access$136002 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth2, this.currentBlock, this.parentAdapter);
                        this.captionLayout = access$136002;
                        if (access$136002 != null) {
                            int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                            this.creditOffset = dp2;
                            height += dp2 + AndroidUtilities.dp(4.0f);
                        }
                        DrawingText access$137002 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, this.currentBlock.caption.credit, textWidth2, (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                        this.creditLayout = access$137002;
                        if (access$137002 != null) {
                            height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                        }
                        this.textX = AndroidUtilities.dp(32.0f);
                        this.textY = AndroidUtilities.dp(56.0f);
                    } else {
                        this.captionLayout = null;
                        this.creditLayout = null;
                    }
                }
                this.lineHeight = height;
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            TLRPC.TL_pageBlockEmbedPost tL_pageBlockEmbedPost = this.currentBlock;
            if (tL_pageBlockEmbedPost != null) {
                if (!(tL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption)) {
                    if (this.avatarVisible) {
                        this.avatarImageView.draw(canvas);
                    }
                    int i = 54;
                    int i2 = 0;
                    if (this.nameLayout != null) {
                        canvas.save();
                        canvas.translate((float) AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 32)), (float) AndroidUtilities.dp(this.dateLayout != null ? 10.0f : 19.0f));
                        this.nameLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.dateLayout != null) {
                        canvas.save();
                        if (!this.avatarVisible) {
                            i = 0;
                        }
                        canvas.translate((float) AndroidUtilities.dp((float) (i + 32)), (float) AndroidUtilities.dp(29.0f));
                        this.dateLayout.draw(canvas);
                        canvas.restore();
                    }
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(6.0f);
                    float dp3 = (float) AndroidUtilities.dp(20.0f);
                    int i3 = this.lineHeight;
                    if (this.currentBlock.level == 0) {
                        i2 = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, dp2, dp3, (float) (i3 - i2), ArticleViewer.quoteLinePaint);
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockParagraphCell extends View {
        private TLRPC.TL_pageBlockParagraph currentBlock;
        private WebpageAdapter parentAdapter;
        /* access modifiers changed from: private */
        public DrawingText textLayout;
        private int textX;
        private int textY;

        public BlockParagraphCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockParagraph block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockParagraph tL_pageBlockParagraph = this.currentBlock;
            if (tL_pageBlockParagraph != null) {
                if (tL_pageBlockParagraph.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18));
                }
                DrawingText access$14400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, (width - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, 0, this.parentAdapter);
                this.textLayout = access$14400;
                if (access$14400 != null) {
                    int height2 = access$14400.getHeight();
                    if (this.currentBlock.level > 0) {
                        height = height2 + AndroidUtilities.dp(8.0f);
                    } else {
                        height = height2 + AndroidUtilities.dp(16.0f);
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                info.setText(drawingText.getText());
            }
        }
    }

    private class BlockEmbedCell extends FrameLayout {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockEmbed currentBlock;
        /* access modifiers changed from: private */
        public int exactWebViewHeight;
        private int listX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;
        /* access modifiers changed from: private */
        public WebPlayerView videoView;
        /* access modifiers changed from: private */
        public boolean wasUserInteraction;
        /* access modifiers changed from: private */
        public TouchyWebView webView;

        private class WebviewProxy {
            private WebviewProxy() {
            }

            @JavascriptInterface
            public void postEvent(String eventName, String eventData) {
                AndroidUtilities.runOnUIThread(
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
                      (wrap: im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$WebviewProxy$9NRUf_sQ_RRRjzjC9H0iIJ62k5s : 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$WebviewProxy$9NRUf_sQ_RRRjzjC9H0iIJ62k5s) = 
                      (r1v0 'this' im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell$WebviewProxy A[THIS])
                      (r2v0 'eventName' java.lang.String)
                      (r3v0 'eventData' java.lang.String)
                     call: im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$WebviewProxy$9NRUf_sQ_RRRjzjC9H0iIJ62k5s.<init>(im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell$WebviewProxy, java.lang.String, java.lang.String):void type: CONSTRUCTOR)
                     im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.ArticleViewer.BlockEmbedCell.WebviewProxy.postEvent(java.lang.String, java.lang.String):void, dex: classes2.dex
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
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$WebviewProxy$9NRUf_sQ_RRRjzjC9H0iIJ62k5s) = 
                      (r1v0 'this' im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell$WebviewProxy A[THIS])
                      (r2v0 'eventName' java.lang.String)
                      (r3v0 'eventData' java.lang.String)
                     call: im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$WebviewProxy$9NRUf_sQ_RRRjzjC9H0iIJ62k5s.<init>(im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell$WebviewProxy, java.lang.String, java.lang.String):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.ArticleViewer.BlockEmbedCell.WebviewProxy.postEvent(java.lang.String, java.lang.String):void, dex: classes2.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$WebviewProxy$9NRUf_sQ_RRRjzjC9H0iIJ62k5s, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$WebviewProxy$9NRUf_sQ_RRRjzjC9H0iIJ62k5s r0 = new im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$WebviewProxy$9NRUf_sQ_RRRjzjC9H0iIJ62k5s
                    r0.<init>(r1, r2, r3)
                    im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.BlockEmbedCell.WebviewProxy.postEvent(java.lang.String, java.lang.String):void");
            }

            public /* synthetic */ void lambda$postEvent$0$ArticleViewer$BlockEmbedCell$WebviewProxy(String eventName, String eventData) {
                if ("resize_frame".equals(eventName)) {
                    try {
                        int unused = BlockEmbedCell.this.exactWebViewHeight = Utilities.parseInt(new JSONObject(eventData).getString("height")).intValue();
                        BlockEmbedCell.this.requestLayout();
                    } catch (Throwable th) {
                    }
                }
            }
        }

        public class TouchyWebView extends WebView {
            public TouchyWebView(Context context) {
                super(context);
                setFocusable(false);
            }

            public boolean onTouchEvent(MotionEvent event) {
                boolean unused = BlockEmbedCell.this.wasUserInteraction = true;
                if (BlockEmbedCell.this.currentBlock != null) {
                    if (BlockEmbedCell.this.currentBlock.allow_scrolling) {
                        requestDisallowInterceptTouchEvent(true);
                    } else {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                }
                return super.onTouchEvent(event);
            }
        }

        public BlockEmbedCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            WebPlayerView webPlayerView = new WebPlayerView(context, false, false, new WebPlayerView.WebPlayerViewDelegate(ArticleViewer.this) {
                public void onInitFailed() {
                    BlockEmbedCell.this.webView.setVisibility(0);
                    BlockEmbedCell.this.videoView.setVisibility(4);
                    BlockEmbedCell.this.videoView.loadVideo((String) null, (TLRPC.Photo) null, (Object) null, (String) null, false);
                    HashMap<String, String> args = new HashMap<>();
                    args.put("Referer", "http://youtube.com");
                    BlockEmbedCell.this.webView.loadUrl(BlockEmbedCell.this.currentBlock.url, args);
                }

                public void onVideoSizeChanged(float aspectRatio, int rotation) {
                    ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(aspectRatio, rotation);
                }

                public void onInlineSurfaceTextureReady() {
                }

                public TextureView onSwitchToFullscreen(View controlsView, boolean fullscreen, float aspectRatio, int rotation, boolean byButton) {
                    if (fullscreen) {
                        ArticleViewer.this.fullscreenAspectRatioView.addView(ArticleViewer.this.fullscreenTextureView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(0);
                        ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(aspectRatio, rotation);
                        WebPlayerView unused = ArticleViewer.this.fullscreenedVideo = BlockEmbedCell.this.videoView;
                        ArticleViewer.this.fullscreenVideoContainer.addView(controlsView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                    } else {
                        ArticleViewer.this.fullscreenAspectRatioView.removeView(ArticleViewer.this.fullscreenTextureView);
                        WebPlayerView unused2 = ArticleViewer.this.fullscreenedVideo = null;
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(8);
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    return ArticleViewer.this.fullscreenTextureView;
                }

                public void prepareToSwitchInlineMode(boolean inline, Runnable switchInlineModeRunnable, float aspectRatio, boolean animated) {
                }

                public TextureView onSwitchInlineMode(View controlsView, boolean inline, float aspectRatio, int rotation, boolean animated) {
                    return null;
                }

                public void onSharePressed() {
                    if (ArticleViewer.this.parentActivity != null) {
                        ArticleViewer.this.showDialog(new ShareAlert(ArticleViewer.this.parentActivity, (ArrayList<MessageObject>) null, BlockEmbedCell.this.currentBlock.url, false, BlockEmbedCell.this.currentBlock.url, true));
                    }
                }

                public void onPlayStateChanged(WebPlayerView playerView, boolean playing) {
                    if (playing) {
                        if (!(ArticleViewer.this.currentPlayingVideo == null || ArticleViewer.this.currentPlayingVideo == playerView)) {
                            ArticleViewer.this.currentPlayingVideo.pause();
                        }
                        WebPlayerView unused = ArticleViewer.this.currentPlayingVideo = playerView;
                        try {
                            ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else {
                        if (ArticleViewer.this.currentPlayingVideo == playerView) {
                            WebPlayerView unused2 = ArticleViewer.this.currentPlayingVideo = null;
                        }
                        try {
                            ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    }
                }

                public boolean checkInlinePermissions() {
                    return false;
                }

                public ViewGroup getTextureViewContainer() {
                    return null;
                }
            });
            this.videoView = webPlayerView;
            addView(webPlayerView);
            ArticleViewer.this.createdWebViews.add(this);
            TouchyWebView touchyWebView = new TouchyWebView(context);
            this.webView = touchyWebView;
            touchyWebView.getSettings().setJavaScriptEnabled(true);
            this.webView.getSettings().setDomStorageEnabled(true);
            this.webView.getSettings().setAllowContentAccess(true);
            if (Build.VERSION.SDK_INT >= 17) {
                this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                this.webView.addJavascriptInterface(new WebviewProxy(), "WebviewProxy");
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.webView.getSettings().setMixedContentMode(0);
                CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            }
            this.webView.setWebChromeClient(new WebChromeClient(ArticleViewer.this) {
                public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
                    onShowCustomView(view, callback);
                }

                public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
                    if (ArticleViewer.this.customView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }
                    View unused = ArticleViewer.this.customView = view;
                    WebChromeClient.CustomViewCallback unused2 = ArticleViewer.this.customViewCallback = callback;
                    AndroidUtilities.runOnUIThread(
                    /*  JADX ERROR: Method code generation error
                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0023: INVOKE  
                          (wrap: im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$EzD18bi2iXS_sC8ARsy5OeDi7f0 : 0x001e: CONSTRUCTOR  (r0v7 im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$EzD18bi2iXS_sC8ARsy5OeDi7f0) = 
                          (r3v0 'this' im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell$2 A[THIS])
                         call: im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$EzD18bi2iXS_sC8ARsy5OeDi7f0.<init>(im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell$2):void type: CONSTRUCTOR)
                          (100 long)
                         im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable, long):void type: STATIC in method: im.bclpbkiauv.ui.ArticleViewer.BlockEmbedCell.2.onShowCustomView(android.view.View, android.webkit.WebChromeClient$CustomViewCallback):void, dex: classes2.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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
                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
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
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
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
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x001e: CONSTRUCTOR  (r0v7 im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$EzD18bi2iXS_sC8ARsy5OeDi7f0) = 
                          (r3v0 'this' im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell$2 A[THIS])
                         call: im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$EzD18bi2iXS_sC8ARsy5OeDi7f0.<init>(im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell$2):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.ArticleViewer.BlockEmbedCell.2.onShowCustomView(android.view.View, android.webkit.WebChromeClient$CustomViewCallback):void, dex: classes2.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	... 80 more
                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$EzD18bi2iXS_sC8ARsy5OeDi7f0, state: NOT_LOADED
                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 86 more
                        */
                    /*
                        this = this;
                        im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell r0 = im.bclpbkiauv.ui.ArticleViewer.BlockEmbedCell.this
                        im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                        android.view.View r0 = r0.customView
                        if (r0 == 0) goto L_0x000e
                        r5.onCustomViewHidden()
                        return
                    L_0x000e:
                        im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell r0 = im.bclpbkiauv.ui.ArticleViewer.BlockEmbedCell.this
                        im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                        android.view.View unused = r0.customView = r4
                        im.bclpbkiauv.ui.ArticleViewer$BlockEmbedCell r0 = im.bclpbkiauv.ui.ArticleViewer.BlockEmbedCell.this
                        im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                        android.webkit.WebChromeClient.CustomViewCallback unused = r0.customViewCallback = r5
                        im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$EzD18bi2iXS_sC8ARsy5OeDi7f0 r0 = new im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$EzD18bi2iXS_sC8ARsy5OeDi7f0
                        r0.<init>(r3)
                        r1 = 100
                        im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0, r1)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.BlockEmbedCell.AnonymousClass2.onShowCustomView(android.view.View, android.webkit.WebChromeClient$CustomViewCallback):void");
                }

                public /* synthetic */ void lambda$onShowCustomView$0$ArticleViewer$BlockEmbedCell$2() {
                    if (ArticleViewer.this.customView != null) {
                        ArticleViewer.this.fullscreenVideoContainer.addView(ArticleViewer.this.customView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                    }
                }

                public void onHideCustomView() {
                    super.onHideCustomView();
                    if (ArticleViewer.this.customView != null) {
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
                        ArticleViewer.this.fullscreenVideoContainer.removeView(ArticleViewer.this.customView);
                        if (ArticleViewer.this.customViewCallback != null && !ArticleViewer.this.customViewCallback.getClass().getName().contains(".chromium.")) {
                            ArticleViewer.this.customViewCallback.onCustomViewHidden();
                        }
                        View unused = ArticleViewer.this.customView = null;
                    }
                }
            });
            this.webView.setWebViewClient(new WebViewClient(ArticleViewer.this) {
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                }

                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (!BlockEmbedCell.this.wasUserInteraction) {
                        return false;
                    }
                    Browser.openUrl((Context) ArticleViewer.this.parentActivity, url);
                    return true;
                }
            });
            addView(this.webView);
        }

        public void destroyWebView(boolean completely) {
            try {
                this.webView.stopLoading();
                this.webView.loadUrl("about:blank");
                if (completely) {
                    this.webView.destroy();
                }
                this.currentBlock = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.videoView.destroy();
        }

        public void setBlock(TLRPC.TL_pageBlockEmbed block) {
            TLRPC.TL_pageBlockEmbed previousBlock = this.currentBlock;
            this.currentBlock = block;
            if (previousBlock != block) {
                this.wasUserInteraction = false;
                if (block.allow_scrolling) {
                    this.webView.setVerticalScrollBarEnabled(true);
                    this.webView.setHorizontalScrollBarEnabled(true);
                } else {
                    this.webView.setVerticalScrollBarEnabled(false);
                    this.webView.setHorizontalScrollBarEnabled(false);
                }
                this.exactWebViewHeight = 0;
                try {
                    this.webView.loadUrl("about:blank");
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    if (this.currentBlock.html != null) {
                        this.webView.loadDataWithBaseURL("https://m12345.com/embed", this.currentBlock.html, "text/html", "UTF-8", (String) null);
                        this.videoView.setVisibility(4);
                        this.videoView.loadVideo((String) null, (TLRPC.Photo) null, (Object) null, (String) null, false);
                        this.webView.setVisibility(0);
                    } else {
                        if (this.videoView.loadVideo(block.url, this.currentBlock.poster_photo_id != 0 ? ArticleViewer.this.getPhotoWithId(this.currentBlock.poster_photo_id) : null, ArticleViewer.this.currentPage, (String) null, false)) {
                            this.webView.setVisibility(4);
                            this.videoView.setVisibility(0);
                            this.webView.stopLoading();
                            this.webView.loadUrl("about:blank");
                        } else {
                            this.webView.setVisibility(0);
                            this.videoView.setVisibility(4);
                            this.videoView.loadVideo((String) null, (TLRPC.Photo) null, (Object) null, (String) null, false);
                            HashMap<String, String> args = new HashMap<>();
                            args.put("Referer", "http://youtube.com");
                            this.webView.loadUrl(this.currentBlock.url, args);
                        }
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            requestLayout();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (!ArticleViewer.this.isVisible) {
                this.currentBlock = null;
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int listWidth;
            int textWidth;
            float scale;
            int height2;
            int height3;
            int height4;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockEmbed tL_pageBlockEmbed = this.currentBlock;
            if (tL_pageBlockEmbed != null) {
                int listWidth2 = width;
                if (tL_pageBlockEmbed.level > 0) {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    int listWidth3 = listWidth2 - (dp + AndroidUtilities.dp(18.0f));
                    textWidth = listWidth3;
                    listWidth = listWidth3;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    int textWidth2 = width - AndroidUtilities.dp(36.0f);
                    if (!this.currentBlock.full_width) {
                        int listWidth4 = listWidth2 - AndroidUtilities.dp(36.0f);
                        this.listX += AndroidUtilities.dp(18.0f);
                        listWidth = listWidth4;
                        textWidth = textWidth2;
                    } else {
                        listWidth = listWidth2;
                        textWidth = textWidth2;
                    }
                }
                if (this.currentBlock.w == 0) {
                    scale = 1.0f;
                } else {
                    scale = ((float) width) / ((float) this.currentBlock.w);
                }
                int i = this.exactWebViewHeight;
                if (i != 0) {
                    height2 = AndroidUtilities.dp((float) i);
                } else {
                    height2 = (int) (((float) (this.currentBlock.w == 0 ? AndroidUtilities.dp((float) this.currentBlock.h) : this.currentBlock.h)) * scale);
                }
                if (height2 == 0) {
                    height3 = AndroidUtilities.dp(10.0f);
                } else {
                    height3 = height2;
                }
                this.webView.measure(View.MeasureSpec.makeMeasureSpec(listWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(height3, 1073741824));
                if (this.videoView.getParent() == this) {
                    this.videoView.measure(View.MeasureSpec.makeMeasureSpec(listWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f) + height3, 1073741824));
                }
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                this.captionLayout = access$13600;
                if (access$13600 != null) {
                    this.textY = AndroidUtilities.dp(8.0f) + height3;
                    int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp2;
                    height4 = height3 + dp2 + AndroidUtilities.dp(4.0f);
                } else {
                    height4 = height3;
                }
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13700;
                if (access$13700 != null) {
                    height4 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                height = height4 + AndroidUtilities.dp(5.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                } else if (this.currentBlock.level == 0 && this.captionLayout != null) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            TouchyWebView touchyWebView = this.webView;
            int i = this.listX;
            touchyWebView.layout(i, 0, touchyWebView.getMeasuredWidth() + i, this.webView.getMeasuredHeight());
            if (this.videoView.getParent() == this) {
                WebPlayerView webPlayerView = this.videoView;
                int i2 = this.listX;
                webPlayerView.layout(i2, 0, webPlayerView.getMeasuredWidth() + i2, this.videoView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockTableCell extends FrameLayout implements TableLayout.TableLayoutDelegate {
        private TLRPC.TL_pageBlockTable currentBlock;
        private boolean firstLayout;
        private boolean inLayout;
        private int listX;
        private int listY;
        private WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        /* access modifiers changed from: private */
        public TableLayout tableLayout;
        private int textX;
        private int textY;
        private DrawingText titleLayout;

        public BlockTableCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            AnonymousClass1 r0 = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() > getMeasuredWidth() - AndroidUtilities.dp(36.0f)) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                /* access modifiers changed from: protected */
                public void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        DrawingText unused = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused2 = ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                }

                /* access modifiers changed from: protected */
                public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
                    ArticleViewer.this.removePressedLink();
                    return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    BlockTableCell.this.tableLayout.measure(View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight(), 0), heightMeasureSpec);
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), BlockTableCell.this.tableLayout.getMeasuredHeight());
                }
            };
            this.scrollView = r0;
            r0.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.scrollView.setClipToPadding(false);
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            TableLayout tableLayout2 = new TableLayout(context, this);
            this.tableLayout = tableLayout2;
            tableLayout2.setOrientation(0);
            this.tableLayout.setRowOrderPreserved(true);
            this.scrollView.addView(this.tableLayout, new FrameLayout.LayoutParams(-2, -2));
            setWillNotDraw(false);
        }

        public DrawingText createTextLayout(TLRPC.TL_pageTableCell cell, int maxWidth) {
            Layout.Alignment alignment;
            if (cell == null) {
                return null;
            }
            if (cell.align_right) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            } else if (cell.align_center) {
                alignment = Layout.Alignment.ALIGN_CENTER;
            } else {
                alignment = Layout.Alignment.ALIGN_NORMAL;
            }
            return ArticleViewer.this.createLayoutForText(this, (CharSequence) null, cell.text, maxWidth, 0, this.currentBlock, alignment, 0, this.parentAdapter);
        }

        public Paint getLinePaint() {
            return ArticleViewer.tableLinePaint;
        }

        public Paint getHalfLinePaint() {
            return ArticleViewer.tableHalfLinePaint;
        }

        public Paint getHeaderPaint() {
            return ArticleViewer.tableHeaderPaint;
        }

        public Paint getStripPaint() {
            return ArticleViewer.tableStripPaint;
        }

        public void setBlock(TLRPC.TL_pageBlockTable block) {
            this.currentBlock = block;
            int color = ArticleViewer.this.getSelectedColor();
            if (color == 0) {
                AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, -657673);
            } else if (color == 1) {
                AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, -659492);
            } else if (color == 2) {
                AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, -15461356);
            }
            this.tableLayout.removeAllChildrens();
            this.tableLayout.setDrawLines(this.currentBlock.bordered);
            this.tableLayout.setStriped(this.currentBlock.striped);
            this.tableLayout.setRtl(ArticleViewer.this.isRtl);
            int maxCols = 0;
            if (!this.currentBlock.rows.isEmpty()) {
                TLRPC.TL_pageTableRow row = this.currentBlock.rows.get(0);
                int size2 = row.cells.size();
                for (int c = 0; c < size2; c++) {
                    TLRPC.TL_pageTableCell cell = row.cells.get(c);
                    maxCols += cell.colspan != 0 ? cell.colspan : 1;
                }
            }
            int size = this.currentBlock.rows.size();
            for (int r = 0; r < size; r++) {
                TLRPC.TL_pageTableRow row2 = this.currentBlock.rows.get(r);
                int cols = 0;
                int size22 = row2.cells.size();
                for (int c2 = 0; c2 < size22; c2++) {
                    TLRPC.TL_pageTableCell cell2 = row2.cells.get(c2);
                    int colspan = cell2.colspan != 0 ? cell2.colspan : 1;
                    int rowspan = cell2.rowspan != 0 ? cell2.rowspan : 1;
                    if (cell2.text != null) {
                        this.tableLayout.addChild(cell2, cols, r, colspan);
                    } else {
                        this.tableLayout.addChild(cols, r, colspan, rowspan);
                    }
                    cols += colspan;
                }
            }
            this.tableLayout.setColumnCount(maxCols);
            this.firstLayout = true;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            int N = this.tableLayout.getChildCount();
            for (int i = 0; i < N; i++) {
                TableLayout.Child c = this.tableLayout.getChildAt(i);
                if (ArticleViewer.this.checkLayoutForLinks(event, this, c.textLayout, (this.scrollView.getPaddingLeft() - this.scrollView.getScrollX()) + this.listX + c.getTextX(), this.listY + c.getTextY())) {
                    return true;
                }
            }
            if (ArticleViewer.this.checkLayoutForLinks(event, this, this.titleLayout, this.textX, this.textY) || super.onTouchEvent(event)) {
                return true;
            }
            return false;
        }

        public void invalidate() {
            super.invalidate();
            this.tableLayout.invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int textWidth;
            this.inLayout = true;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height2 = 0;
            TLRPC.TL_pageBlockTable tL_pageBlockTable = this.currentBlock;
            if (tL_pageBlockTable != null) {
                if (tL_pageBlockTable.level > 0) {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14));
                    this.listX = dp;
                    int dp2 = dp + AndroidUtilities.dp(18.0f);
                    this.textX = dp2;
                    textWidth = width - dp2;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                }
                DrawingText access$14400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.title, textWidth, 0, this.currentBlock, Layout.Alignment.ALIGN_CENTER, 0, this.parentAdapter);
                this.titleLayout = access$14400;
                if (access$14400 != null) {
                    this.textY = 0;
                    height2 = 0 + access$14400.getHeight() + AndroidUtilities.dp(8.0f);
                    this.listY = height2;
                } else {
                    this.listY = AndroidUtilities.dp(8.0f);
                }
                this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width - this.listX, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
                height = height2 + this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            this.inLayout = false;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            HorizontalScrollView horizontalScrollView = this.scrollView;
            int i = this.listX;
            horizontalScrollView.layout(i, this.listY, horizontalScrollView.getMeasuredWidth() + i, this.listY + this.scrollView.getMeasuredHeight());
            if (this.firstLayout) {
                if (ArticleViewer.this.isRtl) {
                    this.scrollView.setScrollX((this.tableLayout.getMeasuredWidth() - this.scrollView.getMeasuredWidth()) + AndroidUtilities.dp(36.0f));
                } else {
                    this.scrollView.setScrollX(0);
                }
                this.firstLayout = false;
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.titleLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockCollageCell extends FrameLayout {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockCollage currentBlock;
        private GridLayoutManager gridLayoutManager;
        /* access modifiers changed from: private */
        public GroupedMessages group = new GroupedMessages();
        /* access modifiers changed from: private */
        public boolean inLayout;
        private RecyclerView.Adapter innerAdapter;
        /* access modifiers changed from: private */
        public RecyclerListView innerListView;
        private int listX;
        /* access modifiers changed from: private */
        public WebpageAdapter parentAdapter;
        private int textX;
        private int textY;

        public class GroupedMessages {
            public long groupId;
            public boolean hasSibling;
            private int maxSizeWidth = 1000;
            public ArrayList<MessageObject.GroupedMessagePosition> posArray = new ArrayList<>();
            public HashMap<TLObject, MessageObject.GroupedMessagePosition> positions = new HashMap<>();

            public GroupedMessages() {
            }

            private class MessageGroupedLayoutAttempt {
                public float[] heights;
                public int[] lineCounts;

                public MessageGroupedLayoutAttempt(int i1, int i2, float f1, float f2) {
                    this.lineCounts = new int[]{i1, i2};
                    this.heights = new float[]{f1, f2};
                }

                public MessageGroupedLayoutAttempt(int i1, int i2, int i3, float f1, float f2, float f3) {
                    this.lineCounts = new int[]{i1, i2, i3};
                    this.heights = new float[]{f1, f2, f3};
                }

                public MessageGroupedLayoutAttempt(int i1, int i2, int i3, int i4, float f1, float f2, float f3, float f4) {
                    this.lineCounts = new int[]{i1, i2, i3, i4};
                    this.heights = new float[]{f1, f2, f3, f4};
                }
            }

            private float multiHeight(float[] array, int start, int end) {
                float sum = 0.0f;
                for (int a = start; a < end; a++) {
                    sum += array[a];
                }
                return ((float) this.maxSizeWidth) / sum;
            }

            /* JADX WARNING: Code restructure failed: missing block: B:159:0x07cd, code lost:
                if (r4.lineCounts[2] > r4.lineCounts[3]) goto L_0x07d1;
             */
            /* JADX WARNING: Removed duplicated region for block: B:195:0x086f  */
            /* JADX WARNING: Removed duplicated region for block: B:233:0x0884 A[SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void calculate() {
                /*
                    r40 = this;
                    r10 = r40
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                    r0.clear()
                    java.util.HashMap<im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.positions
                    r0.clear()
                    im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell r0 = im.bclpbkiauv.ui.ArticleViewer.BlockCollageCell.this
                    im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockCollage r0 = r0.currentBlock
                    java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r0 = r0.items
                    int r11 = r0.size()
                    r12 = 1
                    if (r11 > r12) goto L_0x001c
                    return
                L_0x001c:
                    r13 = 1145798656(0x444b8000, float:814.0)
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    r14 = r0
                    r0 = 1065353216(0x3f800000, float:1.0)
                    r1 = 0
                    r15 = 0
                    r10.hasSibling = r15
                    r2 = 0
                    r16 = r1
                L_0x002e:
                    r17 = 1067030938(0x3f99999a, float:1.2)
                    r1 = 1073741824(0x40000000, float:2.0)
                    if (r2 >= r11) goto L_0x00d6
                    im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell r4 = im.bclpbkiauv.ui.ArticleViewer.BlockCollageCell.this
                    im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockCollage r4 = r4.currentBlock
                    java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r4 = r4.items
                    java.lang.Object r4 = r4.get(r2)
                    im.bclpbkiauv.tgnet.TLObject r4 = (im.bclpbkiauv.tgnet.TLObject) r4
                    boolean r5 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockPhoto
                    if (r5 == 0) goto L_0x0063
                    r5 = r4
                    im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockPhoto) r5
                    im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell r6 = im.bclpbkiauv.ui.ArticleViewer.BlockCollageCell.this
                    im.bclpbkiauv.ui.ArticleViewer r6 = im.bclpbkiauv.ui.ArticleViewer.this
                    long r7 = r5.photo_id
                    im.bclpbkiauv.tgnet.TLRPC$Photo r6 = r6.getPhotoWithId(r7)
                    if (r6 != 0) goto L_0x0058
                    goto L_0x00d2
                L_0x0058:
                    java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r7 = r6.sizes
                    int r8 = im.bclpbkiauv.messenger.AndroidUtilities.getPhotoSize()
                    im.bclpbkiauv.tgnet.TLRPC$PhotoSize r5 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
                    goto L_0x0080
                L_0x0063:
                    boolean r5 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockVideo
                    if (r5 == 0) goto L_0x00d2
                    r5 = r4
                    im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockVideo r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockVideo) r5
                    im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell r6 = im.bclpbkiauv.ui.ArticleViewer.BlockCollageCell.this
                    im.bclpbkiauv.ui.ArticleViewer r6 = im.bclpbkiauv.ui.ArticleViewer.this
                    long r7 = r5.video_id
                    im.bclpbkiauv.tgnet.TLRPC$Document r6 = r6.getDocumentWithId(r7)
                    if (r6 != 0) goto L_0x0077
                    goto L_0x00d2
                L_0x0077:
                    java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r7 = r6.thumbs
                    r8 = 90
                    im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
                    r5 = r7
                L_0x0080:
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r6 = new im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition
                    r6.<init>()
                    int r7 = r11 + -1
                    if (r2 != r7) goto L_0x008b
                    r7 = 1
                    goto L_0x008c
                L_0x008b:
                    r7 = 0
                L_0x008c:
                    r6.last = r7
                    if (r5 != 0) goto L_0x0093
                    r3 = 1065353216(0x3f800000, float:1.0)
                    goto L_0x009a
                L_0x0093:
                    int r3 = r5.w
                    float r3 = (float) r3
                    int r7 = r5.h
                    float r7 = (float) r7
                    float r3 = r3 / r7
                L_0x009a:
                    r6.aspectRatio = r3
                    float r3 = r6.aspectRatio
                    int r3 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
                    if (r3 <= 0) goto L_0x00a9
                    java.lang.String r3 = "w"
                    r14.append(r3)
                    goto L_0x00bd
                L_0x00a9:
                    float r3 = r6.aspectRatio
                    r7 = 1061997773(0x3f4ccccd, float:0.8)
                    int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                    if (r3 >= 0) goto L_0x00b8
                    java.lang.String r3 = "n"
                    r14.append(r3)
                    goto L_0x00bd
                L_0x00b8:
                    java.lang.String r3 = "q"
                    r14.append(r3)
                L_0x00bd:
                    float r3 = r6.aspectRatio
                    float r0 = r0 + r3
                    float r3 = r6.aspectRatio
                    int r1 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
                    if (r1 <= 0) goto L_0x00c8
                    r16 = 1
                L_0x00c8:
                    java.util.HashMap<im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r1 = r10.positions
                    r1.put(r4, r6)
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                    r1.add(r6)
                L_0x00d2:
                    int r2 = r2 + 1
                    goto L_0x002e
                L_0x00d6:
                    r2 = 1123024896(0x42f00000, float:120.0)
                    int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                    int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                    float r2 = (float) r2
                    android.graphics.Point r4 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                    int r4 = r4.x
                    android.graphics.Point r5 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                    int r5 = r5.y
                    int r4 = java.lang.Math.min(r4, r5)
                    float r4 = (float) r4
                    int r5 = r10.maxSizeWidth
                    float r5 = (float) r5
                    float r4 = r4 / r5
                    float r2 = r2 / r4
                    int r8 = (int) r2
                    r2 = 1109393408(0x42200000, float:40.0)
                    int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                    float r2 = (float) r2
                    android.graphics.Point r4 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                    int r4 = r4.x
                    android.graphics.Point r5 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                    int r5 = r5.y
                    int r4 = java.lang.Math.min(r4, r5)
                    float r4 = (float) r4
                    int r5 = r10.maxSizeWidth
                    float r6 = (float) r5
                    float r4 = r4 / r6
                    float r2 = r2 / r4
                    int r7 = (int) r2
                    float r2 = (float) r5
                    float r6 = r2 / r13
                    float r2 = (float) r11
                    float r5 = r0 / r2
                    r4 = 4
                    r2 = 2
                    r0 = 3
                    if (r16 != 0) goto L_0x0572
                    if (r11 == r2) goto L_0x012b
                    if (r11 == r0) goto L_0x012b
                    if (r11 != r4) goto L_0x0120
                    goto L_0x012b
                L_0x0120:
                    r24 = r5
                    r25 = r6
                    r12 = r7
                    r23 = r14
                    r19 = 2
                    goto L_0x057b
                L_0x012b:
                    r17 = 1053609165(0x3ecccccd, float:0.4)
                    if (r11 != r2) goto L_0x0272
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                    java.lang.Object r0 = r0.get(r15)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r0 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r0
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r4 = r10.posArray
                    java.lang.Object r4 = r4.get(r12)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r4 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r4
                    java.lang.String r15 = r14.toString()
                    java.lang.String r12 = "ww"
                    boolean r18 = r15.equals(r12)
                    if (r18 == 0) goto L_0x01b1
                    double r2 = (double) r5
                    r20 = 4608983858650965606(0x3ff6666666666666, double:1.4)
                    r27 = r7
                    r26 = r8
                    double r7 = (double) r6
                    double r7 = r7 * r20
                    int r20 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                    if (r20 <= 0) goto L_0x01b5
                    float r2 = r0.aspectRatio
                    float r3 = r4.aspectRatio
                    float r2 = r2 - r3
                    double r2 = (double) r2
                    r7 = 4596373779694328218(0x3fc999999999999a, double:0.2)
                    int r20 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                    if (r20 >= 0) goto L_0x01b5
                    int r2 = r10.maxSizeWidth
                    float r2 = (float) r2
                    float r3 = r0.aspectRatio
                    float r2 = r2 / r3
                    int r3 = r10.maxSizeWidth
                    float r3 = (float) r3
                    float r7 = r4.aspectRatio
                    float r3 = r3 / r7
                    float r1 = r13 / r1
                    float r1 = java.lang.Math.min(r3, r1)
                    float r1 = java.lang.Math.min(r2, r1)
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r1 = r1 / r13
                    r19 = 0
                    r20 = 0
                    r21 = 0
                    r22 = 0
                    int r2 = r10.maxSizeWidth
                    r25 = 7
                    r18 = r0
                    r23 = r2
                    r24 = r1
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    r21 = 1
                    r22 = 1
                    int r2 = r10.maxSizeWidth
                    r25 = 11
                    r18 = r4
                    r23 = r2
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    r8 = r26
                    goto L_0x0261
                L_0x01b1:
                    r27 = r7
                    r26 = r8
                L_0x01b5:
                    boolean r1 = r15.equals(r12)
                    if (r1 != 0) goto L_0x0226
                    java.lang.String r1 = "qq"
                    boolean r1 = r15.equals(r1)
                    if (r1 == 0) goto L_0x01c6
                    r8 = r26
                    goto L_0x0228
                L_0x01c6:
                    int r1 = r10.maxSizeWidth
                    float r2 = (float) r1
                    float r2 = r2 * r17
                    float r1 = (float) r1
                    float r3 = r0.aspectRatio
                    float r1 = r1 / r3
                    float r3 = r0.aspectRatio
                    r7 = 1065353216(0x3f800000, float:1.0)
                    float r3 = r7 / r3
                    float r8 = r4.aspectRatio
                    float r7 = r7 / r8
                    float r3 = r3 + r7
                    float r1 = r1 / r3
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r1 = java.lang.Math.max(r2, r1)
                    int r1 = (int) r1
                    int r2 = r10.maxSizeWidth
                    int r2 = r2 - r1
                    r8 = r26
                    if (r2 >= r8) goto L_0x01ef
                    int r3 = r8 - r2
                    r2 = r8
                    int r1 = r1 - r3
                L_0x01ef:
                    float r3 = (float) r2
                    float r7 = r0.aspectRatio
                    float r3 = r3 / r7
                    float r7 = (float) r1
                    float r12 = r4.aspectRatio
                    float r7 = r7 / r12
                    float r3 = java.lang.Math.min(r3, r7)
                    int r3 = java.lang.Math.round(r3)
                    float r3 = (float) r3
                    float r3 = java.lang.Math.min(r13, r3)
                    float r3 = r3 / r13
                    r19 = 0
                    r20 = 0
                    r21 = 0
                    r22 = 0
                    r25 = 13
                    r18 = r0
                    r23 = r2
                    r24 = r3
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    r19 = 1
                    r20 = 1
                    r25 = 14
                    r18 = r4
                    r23 = r1
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    goto L_0x0261
                L_0x0226:
                    r8 = r26
                L_0x0228:
                    int r1 = r10.maxSizeWidth
                    r2 = 2
                    int r1 = r1 / r2
                    float r2 = (float) r1
                    float r3 = r0.aspectRatio
                    float r2 = r2 / r3
                    float r3 = (float) r1
                    float r7 = r4.aspectRatio
                    float r3 = r3 / r7
                    float r3 = java.lang.Math.min(r3, r13)
                    float r2 = java.lang.Math.min(r2, r3)
                    int r2 = java.lang.Math.round(r2)
                    float r2 = (float) r2
                    float r2 = r2 / r13
                    r19 = 0
                    r20 = 0
                    r21 = 0
                    r22 = 0
                    r25 = 13
                    r18 = r0
                    r23 = r1
                    r24 = r2
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    r19 = 1
                    r20 = 1
                    r25 = 14
                    r18 = r4
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                L_0x0261:
                    r18 = r5
                    r21 = r6
                    r28 = r9
                    r31 = r11
                    r23 = r14
                    r26 = r27
                    r27 = r13
                    r13 = r8
                    goto L_0x086a
                L_0x0272:
                    r27 = r7
                    r1 = 1059648963(0x3f28f5c3, float:0.66)
                    if (r11 != r0) goto L_0x03be
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                    java.lang.Object r0 = r0.get(r15)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r0 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r0
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                    r3 = 1
                    java.lang.Object r2 = r2.get(r3)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r2
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r3 = r10.posArray
                    r4 = 2
                    java.lang.Object r3 = r3.get(r4)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r3 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r3
                    char r4 = r14.charAt(r15)
                    r7 = 110(0x6e, float:1.54E-43)
                    if (r4 != r7) goto L_0x0340
                    r1 = 1056964608(0x3f000000, float:0.5)
                    float r4 = r13 * r1
                    float r7 = r2.aspectRatio
                    int r12 = r10.maxSizeWidth
                    float r12 = (float) r12
                    float r7 = r7 * r12
                    float r12 = r3.aspectRatio
                    float r15 = r2.aspectRatio
                    float r12 = r12 + r15
                    float r7 = r7 / r12
                    int r7 = java.lang.Math.round(r7)
                    float r7 = (float) r7
                    float r4 = java.lang.Math.min(r4, r7)
                    float r7 = r13 - r4
                    float r12 = (float) r8
                    int r15 = r10.maxSizeWidth
                    float r15 = (float) r15
                    float r15 = r15 * r1
                    float r1 = r3.aspectRatio
                    float r1 = r1 * r4
                    r25 = r6
                    float r6 = r2.aspectRatio
                    float r6 = r6 * r7
                    float r1 = java.lang.Math.min(r1, r6)
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r1 = java.lang.Math.min(r15, r1)
                    float r1 = java.lang.Math.max(r12, r1)
                    int r1 = (int) r1
                    float r6 = r0.aspectRatio
                    float r6 = r6 * r13
                    r12 = r27
                    float r15 = (float) r12
                    float r6 = r6 + r15
                    int r15 = r10.maxSizeWidth
                    int r15 = r15 - r1
                    float r15 = (float) r15
                    float r6 = java.lang.Math.min(r6, r15)
                    int r6 = java.lang.Math.round(r6)
                    r29 = 0
                    r30 = 0
                    r31 = 0
                    r32 = 1
                    r34 = 1065353216(0x3f800000, float:1.0)
                    r35 = 13
                    r28 = r0
                    r33 = r6
                    r28.set(r29, r30, r31, r32, r33, r34, r35)
                    r29 = 1
                    r30 = 1
                    r32 = 0
                    float r34 = r7 / r13
                    r35 = 6
                    r28 = r2
                    r33 = r1
                    r28.set(r29, r30, r31, r32, r33, r34, r35)
                    r29 = 0
                    r31 = 1
                    r32 = 1
                    float r34 = r4 / r13
                    r35 = 10
                    r28 = r3
                    r28.set(r29, r30, r31, r32, r33, r34, r35)
                    int r15 = r10.maxSizeWidth
                    r3.spanSize = r15
                    r15 = 2
                    float[] r15 = new float[r15]
                    float r17 = r4 / r13
                    r18 = 0
                    r15[r18] = r17
                    float r17 = r7 / r13
                    r18 = r1
                    r1 = 1
                    r15[r1] = r17
                    r0.siblingHeights = r15
                    int r15 = r10.maxSizeWidth
                    int r15 = r15 - r6
                    r2.spanSize = r15
                    r3.leftSpanOffset = r6
                    r10.hasSibling = r1
                    goto L_0x03ad
                L_0x0340:
                    r25 = r6
                    r12 = r27
                    int r4 = r10.maxSizeWidth
                    float r4 = (float) r4
                    float r6 = r0.aspectRatio
                    float r4 = r4 / r6
                    float r1 = r1 * r13
                    float r1 = java.lang.Math.min(r4, r1)
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r1 = r1 / r13
                    r29 = 0
                    r30 = 1
                    r31 = 0
                    r32 = 0
                    int r4 = r10.maxSizeWidth
                    r35 = 7
                    r28 = r0
                    r33 = r4
                    r34 = r1
                    r28.set(r29, r30, r31, r32, r33, r34, r35)
                    int r4 = r10.maxSizeWidth
                    r6 = 2
                    int r4 = r4 / r6
                    float r6 = r13 - r1
                    float r7 = (float) r4
                    float r15 = r2.aspectRatio
                    float r7 = r7 / r15
                    float r15 = (float) r4
                    r26 = r0
                    float r0 = r3.aspectRatio
                    float r15 = r15 / r0
                    float r0 = java.lang.Math.min(r7, r15)
                    int r0 = java.lang.Math.round(r0)
                    float r0 = (float) r0
                    float r0 = java.lang.Math.min(r6, r0)
                    float r0 = r0 / r13
                    r30 = 0
                    r31 = 1
                    r32 = 1
                    r35 = 9
                    r28 = r2
                    r33 = r4
                    r34 = r0
                    r28.set(r29, r30, r31, r32, r33, r34, r35)
                    r18 = 1
                    r19 = 1
                    r20 = 1
                    r21 = 1
                    r24 = 10
                    r17 = r3
                    r22 = r4
                    r23 = r0
                    r17.set(r18, r19, r20, r21, r22, r23, r24)
                L_0x03ad:
                    r18 = r5
                    r28 = r9
                    r31 = r11
                    r26 = r12
                    r27 = r13
                    r23 = r14
                    r21 = r25
                    r13 = r8
                    goto L_0x086a
                L_0x03be:
                    r25 = r6
                    r12 = r27
                    if (r11 != r4) goto L_0x055f
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                    r3 = 0
                    java.lang.Object r2 = r2.get(r3)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r2
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r3 = r10.posArray
                    r4 = 1
                    java.lang.Object r3 = r3.get(r4)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r3 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r3
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r4 = r10.posArray
                    r6 = 2
                    java.lang.Object r4 = r4.get(r6)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r4 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r4
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r6 = r10.posArray
                    java.lang.Object r6 = r6.get(r0)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r6 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r6
                    r7 = 0
                    char r15 = r14.charAt(r7)
                    r7 = 119(0x77, float:1.67E-43)
                    if (r15 != r7) goto L_0x048d
                    int r7 = r10.maxSizeWidth
                    float r7 = (float) r7
                    float r15 = r2.aspectRatio
                    float r7 = r7 / r15
                    float r1 = r1 * r13
                    float r1 = java.lang.Math.min(r7, r1)
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r1 = r1 / r13
                    r27 = 0
                    r28 = 2
                    r29 = 0
                    r30 = 0
                    int r7 = r10.maxSizeWidth
                    r33 = 7
                    r26 = r2
                    r31 = r7
                    r32 = r1
                    r26.set(r27, r28, r29, r30, r31, r32, r33)
                    int r7 = r10.maxSizeWidth
                    float r7 = (float) r7
                    float r15 = r3.aspectRatio
                    float r0 = r4.aspectRatio
                    float r15 = r15 + r0
                    float r0 = r6.aspectRatio
                    float r15 = r15 + r0
                    float r7 = r7 / r15
                    int r0 = java.lang.Math.round(r7)
                    float r0 = (float) r0
                    float r7 = (float) r8
                    int r15 = r10.maxSizeWidth
                    float r15 = (float) r15
                    float r15 = r15 * r17
                    r23 = r14
                    float r14 = r3.aspectRatio
                    float r14 = r14 * r0
                    float r14 = java.lang.Math.min(r15, r14)
                    float r7 = java.lang.Math.max(r7, r14)
                    int r7 = (int) r7
                    float r14 = (float) r8
                    int r15 = r10.maxSizeWidth
                    float r15 = (float) r15
                    r17 = 1051260355(0x3ea8f5c3, float:0.33)
                    float r15 = r15 * r17
                    float r14 = java.lang.Math.max(r14, r15)
                    float r15 = r6.aspectRatio
                    float r15 = r15 * r0
                    float r14 = java.lang.Math.max(r14, r15)
                    int r14 = (int) r14
                    int r15 = r10.maxSizeWidth
                    int r15 = r15 - r7
                    int r15 = r15 - r14
                    r24 = r5
                    float r5 = r13 - r1
                    float r0 = java.lang.Math.min(r5, r0)
                    float r0 = r0 / r13
                    r28 = 0
                    r29 = 1
                    r30 = 1
                    r33 = 9
                    r26 = r3
                    r31 = r7
                    r32 = r0
                    r26.set(r27, r28, r29, r30, r31, r32, r33)
                    r27 = 1
                    r28 = 1
                    r33 = 8
                    r26 = r4
                    r31 = r15
                    r26.set(r27, r28, r29, r30, r31, r32, r33)
                    r27 = 2
                    r28 = 2
                    r33 = 10
                    r26 = r6
                    r31 = r14
                    r26.set(r27, r28, r29, r30, r31, r32, r33)
                    goto L_0x0550
                L_0x048d:
                    r24 = r5
                    r23 = r14
                    float r0 = r3.aspectRatio
                    r1 = 1065353216(0x3f800000, float:1.0)
                    float r0 = r1 / r0
                    float r5 = r4.aspectRatio
                    float r5 = r1 / r5
                    float r0 = r0 + r5
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r5 = r10.posArray
                    r7 = 3
                    java.lang.Object r5 = r5.get(r7)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r5 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r5
                    float r5 = r5.aspectRatio
                    float r5 = r1 / r5
                    float r0 = r0 + r5
                    float r0 = r13 / r0
                    int r0 = java.lang.Math.round(r0)
                    int r0 = java.lang.Math.max(r8, r0)
                    float r1 = (float) r9
                    float r5 = (float) r0
                    float r7 = r3.aspectRatio
                    float r5 = r5 / r7
                    float r1 = java.lang.Math.max(r1, r5)
                    float r1 = r1 / r13
                    r5 = 1051260355(0x3ea8f5c3, float:0.33)
                    float r1 = java.lang.Math.min(r5, r1)
                    float r7 = (float) r9
                    float r14 = (float) r0
                    float r15 = r4.aspectRatio
                    float r14 = r14 / r15
                    float r7 = java.lang.Math.max(r7, r14)
                    float r7 = r7 / r13
                    float r5 = java.lang.Math.min(r5, r7)
                    r7 = 1065353216(0x3f800000, float:1.0)
                    float r7 = r7 - r1
                    float r7 = r7 - r5
                    float r14 = r2.aspectRatio
                    float r14 = r14 * r13
                    float r15 = (float) r12
                    float r14 = r14 + r15
                    int r15 = r10.maxSizeWidth
                    int r15 = r15 - r0
                    float r15 = (float) r15
                    float r14 = java.lang.Math.min(r14, r15)
                    int r14 = java.lang.Math.round(r14)
                    r27 = 0
                    r28 = 0
                    r29 = 0
                    r30 = 2
                    float r15 = r1 + r5
                    float r32 = r15 + r7
                    r33 = 13
                    r26 = r2
                    r31 = r14
                    r26.set(r27, r28, r29, r30, r31, r32, r33)
                    r27 = 1
                    r28 = 1
                    r30 = 0
                    r33 = 6
                    r26 = r3
                    r31 = r0
                    r32 = r1
                    r26.set(r27, r28, r29, r30, r31, r32, r33)
                    r27 = 0
                    r29 = 1
                    r30 = 1
                    r33 = 2
                    r26 = r4
                    r32 = r5
                    r26.set(r27, r28, r29, r30, r31, r32, r33)
                    int r15 = r10.maxSizeWidth
                    r4.spanSize = r15
                    r29 = 2
                    r30 = 2
                    r33 = 10
                    r26 = r6
                    r32 = r7
                    r26.set(r27, r28, r29, r30, r31, r32, r33)
                    int r15 = r10.maxSizeWidth
                    r6.spanSize = r15
                    int r15 = r10.maxSizeWidth
                    int r15 = r15 - r14
                    r3.spanSize = r15
                    r4.leftSpanOffset = r14
                    r6.leftSpanOffset = r14
                    r15 = 3
                    float[] r15 = new float[r15]
                    r17 = 0
                    r15[r17] = r1
                    r17 = r0
                    r0 = 1
                    r15[r0] = r5
                    r19 = 2
                    r15[r19] = r7
                    r2.siblingHeights = r15
                    r10.hasSibling = r0
                L_0x0550:
                    r28 = r9
                    r31 = r11
                    r26 = r12
                    r27 = r13
                    r18 = r24
                    r21 = r25
                    r13 = r8
                    goto L_0x086a
                L_0x055f:
                    r24 = r5
                    r23 = r14
                    r28 = r9
                    r31 = r11
                    r26 = r12
                    r27 = r13
                    r18 = r24
                    r21 = r25
                    r13 = r8
                    goto L_0x086a
                L_0x0572:
                    r24 = r5
                    r25 = r6
                    r12 = r7
                    r23 = r14
                    r19 = 2
                L_0x057b:
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                    int r0 = r0.size()
                    float[] r14 = new float[r0]
                    r0 = 0
                L_0x0584:
                    if (r0 >= r11) goto L_0x05c7
                    r1 = 1066192077(0x3f8ccccd, float:1.1)
                    int r1 = (r24 > r1 ? 1 : (r24 == r1 ? 0 : -1))
                    if (r1 <= 0) goto L_0x05a0
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                    java.lang.Object r1 = r1.get(r0)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r1 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r1
                    float r1 = r1.aspectRatio
                    r2 = 1065353216(0x3f800000, float:1.0)
                    float r1 = java.lang.Math.max(r2, r1)
                    r14[r0] = r1
                    goto L_0x05b2
                L_0x05a0:
                    r2 = 1065353216(0x3f800000, float:1.0)
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                    java.lang.Object r1 = r1.get(r0)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r1 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r1
                    float r1 = r1.aspectRatio
                    float r1 = java.lang.Math.min(r2, r1)
                    r14[r0] = r1
                L_0x05b2:
                    r1 = 1059760867(0x3f2aaae3, float:0.66667)
                    r3 = 1071225242(0x3fd9999a, float:1.7)
                    r5 = r14[r0]
                    float r3 = java.lang.Math.min(r3, r5)
                    float r1 = java.lang.Math.max(r1, r3)
                    r14[r0] = r1
                    int r0 = r0 + 1
                    goto L_0x0584
                L_0x05c7:
                    java.util.ArrayList r0 = new java.util.ArrayList
                    r0.<init>()
                    r15 = r0
                    r0 = 1
                    r6 = r0
                L_0x05cf:
                    int r0 = r14.length
                    if (r6 >= r0) goto L_0x0617
                    int r0 = r14.length
                    int r7 = r0 - r6
                    r0 = 3
                    if (r6 > r0) goto L_0x0607
                    if (r7 <= r0) goto L_0x05e1
                    r27 = r12
                    r18 = r24
                    r22 = 4
                    goto L_0x060d
                L_0x05e1:
                    im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt r5 = new im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt
                    r1 = 0
                    float r18 = r10.multiHeight(r14, r1, r6)
                    int r1 = r14.length
                    float r21 = r10.multiHeight(r14, r6, r1)
                    r3 = 3
                    r0 = r5
                    r1 = r40
                    r27 = r12
                    r12 = 2
                    r2 = r6
                    r12 = 3
                    r3 = r7
                    r22 = 4
                    r4 = r18
                    r12 = r5
                    r18 = r24
                    r5 = r21
                    r0.<init>(r2, r3, r4, r5)
                    r15.add(r12)
                    goto L_0x060d
                L_0x0607:
                    r27 = r12
                    r18 = r24
                    r22 = 4
                L_0x060d:
                    int r6 = r6 + 1
                    r24 = r18
                    r12 = r27
                    r4 = 4
                    r19 = 2
                    goto L_0x05cf
                L_0x0617:
                    r27 = r12
                    r18 = r24
                    r22 = 4
                    r0 = 1
                    r12 = r0
                L_0x061f:
                    int r0 = r14.length
                    r1 = 1
                    int r0 = r0 - r1
                    if (r12 >= r0) goto L_0x069d
                    r0 = 1
                    r7 = r0
                L_0x0626:
                    int r0 = r14.length
                    int r0 = r0 - r12
                    if (r7 >= r0) goto L_0x0690
                    int r0 = r14.length
                    int r0 = r0 - r12
                    int r6 = r0 - r7
                    r0 = 3
                    if (r12 > r0) goto L_0x067d
                    r0 = 1062836634(0x3f59999a, float:0.85)
                    int r0 = (r18 > r0 ? 1 : (r18 == r0 ? 0 : -1))
                    if (r0 >= 0) goto L_0x063a
                    r4 = 4
                    goto L_0x063b
                L_0x063a:
                    r4 = 3
                L_0x063b:
                    if (r7 > r4) goto L_0x067d
                    r0 = 3
                    if (r6 <= r0) goto L_0x064b
                    r29 = r8
                    r21 = r25
                    r26 = r27
                    r25 = r6
                    r27 = r7
                    goto L_0x0687
                L_0x064b:
                    im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt r5 = new im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt
                    r0 = 0
                    float r21 = r10.multiHeight(r14, r0, r12)
                    int r0 = r12 + r7
                    float r26 = r10.multiHeight(r14, r12, r0)
                    int r0 = r12 + r7
                    int r1 = r14.length
                    float r28 = r10.multiHeight(r14, r0, r1)
                    r0 = r5
                    r1 = r40
                    r2 = r12
                    r3 = r7
                    r4 = r6
                    r29 = r8
                    r8 = r5
                    r5 = r21
                    r21 = r25
                    r25 = r6
                    r6 = r26
                    r26 = r27
                    r27 = r7
                    r7 = r28
                    r0.<init>(r2, r3, r4, r5, r6, r7)
                    r15.add(r8)
                    goto L_0x0687
                L_0x067d:
                    r29 = r8
                    r21 = r25
                    r26 = r27
                    r25 = r6
                    r27 = r7
                L_0x0687:
                    int r7 = r27 + 1
                    r25 = r21
                    r27 = r26
                    r8 = r29
                    goto L_0x0626
                L_0x0690:
                    r29 = r8
                    r21 = r25
                    r26 = r27
                    r27 = r7
                    int r12 = r12 + 1
                    r27 = r26
                    goto L_0x061f
                L_0x069d:
                    r29 = r8
                    r21 = r25
                    r26 = r27
                    r0 = 1
                    r12 = r0
                L_0x06a5:
                    int r0 = r14.length
                    r1 = 2
                    int r0 = r0 - r1
                    if (r12 >= r0) goto L_0x0758
                    r0 = 1
                    r8 = r0
                L_0x06ac:
                    int r0 = r14.length
                    int r0 = r0 - r12
                    if (r8 >= r0) goto L_0x0746
                    r0 = 1
                    r7 = r0
                L_0x06b2:
                    int r0 = r14.length
                    int r0 = r0 - r12
                    int r0 = r0 - r8
                    if (r7 >= r0) goto L_0x0732
                    int r0 = r14.length
                    int r0 = r0 - r12
                    int r0 = r0 - r8
                    int r6 = r0 - r7
                    r0 = 3
                    if (r12 > r0) goto L_0x0717
                    if (r8 > r0) goto L_0x0717
                    if (r7 > r0) goto L_0x0717
                    if (r6 <= r0) goto L_0x06d4
                    r32 = r6
                    r25 = r7
                    r28 = r9
                    r31 = r11
                    r27 = r13
                    r13 = r29
                    r29 = r8
                    goto L_0x0725
                L_0x06d4:
                    im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt r5 = new im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt
                    r0 = 0
                    float r25 = r10.multiHeight(r14, r0, r12)
                    int r0 = r12 + r8
                    float r27 = r10.multiHeight(r14, r12, r0)
                    int r0 = r12 + r8
                    int r1 = r12 + r8
                    int r1 = r1 + r7
                    float r28 = r10.multiHeight(r14, r0, r1)
                    int r0 = r12 + r8
                    int r0 = r0 + r7
                    int r1 = r14.length
                    float r30 = r10.multiHeight(r14, r0, r1)
                    r0 = r5
                    r1 = r40
                    r2 = r12
                    r3 = r8
                    r4 = r7
                    r31 = r11
                    r11 = r5
                    r5 = r6
                    r32 = r6
                    r6 = r25
                    r25 = r7
                    r7 = r27
                    r27 = r13
                    r13 = r29
                    r29 = r8
                    r8 = r28
                    r28 = r9
                    r9 = r30
                    r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
                    r15.add(r11)
                    goto L_0x0725
                L_0x0717:
                    r32 = r6
                    r25 = r7
                    r28 = r9
                    r31 = r11
                    r27 = r13
                    r13 = r29
                    r29 = r8
                L_0x0725:
                    int r7 = r25 + 1
                    r9 = r28
                    r8 = r29
                    r11 = r31
                    r29 = r13
                    r13 = r27
                    goto L_0x06b2
                L_0x0732:
                    r25 = r7
                    r28 = r9
                    r31 = r11
                    r27 = r13
                    r13 = r29
                    r29 = r8
                    int r8 = r29 + 1
                    r29 = r13
                    r13 = r27
                    goto L_0x06ac
                L_0x0746:
                    r28 = r9
                    r31 = r11
                    r27 = r13
                    r13 = r29
                    r29 = r8
                    int r12 = r12 + 1
                    r29 = r13
                    r13 = r27
                    goto L_0x06a5
                L_0x0758:
                    r28 = r9
                    r31 = r11
                    r27 = r13
                    r13 = r29
                    r0 = 0
                    r1 = 0
                    int r2 = r10.maxSizeWidth
                    r3 = 3
                    int r2 = r2 / r3
                    int r2 = r2 * 4
                    float r2 = (float) r2
                    r3 = 0
                L_0x076a:
                    int r4 = r15.size()
                    if (r3 >= r4) goto L_0x07e9
                    java.lang.Object r4 = r15.get(r3)
                    im.bclpbkiauv.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt r4 = (im.bclpbkiauv.ui.ArticleViewer.BlockCollageCell.GroupedMessages.MessageGroupedLayoutAttempt) r4
                    r5 = 0
                    r6 = 2139095039(0x7f7fffff, float:3.4028235E38)
                    r7 = 0
                L_0x077b:
                    float[] r8 = r4.heights
                    int r8 = r8.length
                    if (r7 >= r8) goto L_0x0794
                    float[] r8 = r4.heights
                    r8 = r8[r7]
                    float r5 = r5 + r8
                    float[] r8 = r4.heights
                    r8 = r8[r7]
                    int r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                    if (r8 >= 0) goto L_0x0791
                    float[] r8 = r4.heights
                    r6 = r8[r7]
                L_0x0791:
                    int r7 = r7 + 1
                    goto L_0x077b
                L_0x0794:
                    float r7 = r5 - r2
                    float r7 = java.lang.Math.abs(r7)
                    int[] r8 = r4.lineCounts
                    int r8 = r8.length
                    r9 = 1
                    if (r8 <= r9) goto L_0x07d4
                    int[] r8 = r4.lineCounts
                    r11 = 0
                    r8 = r8[r11]
                    int[] r11 = r4.lineCounts
                    r11 = r11[r9]
                    if (r8 > r11) goto L_0x07d0
                    int[] r8 = r4.lineCounts
                    int r8 = r8.length
                    r11 = 2
                    if (r8 <= r11) goto L_0x07be
                    int[] r8 = r4.lineCounts
                    r8 = r8[r9]
                    int[] r9 = r4.lineCounts
                    r9 = r9[r11]
                    if (r8 > r9) goto L_0x07bc
                    goto L_0x07be
                L_0x07bc:
                    r9 = 3
                    goto L_0x07d1
                L_0x07be:
                    int[] r8 = r4.lineCounts
                    int r8 = r8.length
                    r9 = 3
                    if (r8 <= r9) goto L_0x07d5
                    int[] r8 = r4.lineCounts
                    r11 = 2
                    r8 = r8[r11]
                    int[] r11 = r4.lineCounts
                    r11 = r11[r9]
                    if (r8 <= r11) goto L_0x07d5
                    goto L_0x07d1
                L_0x07d0:
                    r9 = 3
                L_0x07d1:
                    float r7 = r7 * r17
                    goto L_0x07d5
                L_0x07d4:
                    r9 = 3
                L_0x07d5:
                    float r8 = (float) r13
                    int r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                    if (r8 >= 0) goto L_0x07de
                    r8 = 1069547520(0x3fc00000, float:1.5)
                    float r7 = r7 * r8
                L_0x07de:
                    if (r0 == 0) goto L_0x07e4
                    int r8 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
                    if (r8 >= 0) goto L_0x07e6
                L_0x07e4:
                    r0 = r4
                    r1 = r7
                L_0x07e6:
                    int r3 = r3 + 1
                    goto L_0x076a
                L_0x07e9:
                    if (r0 != 0) goto L_0x07ec
                    return
                L_0x07ec:
                    r3 = 0
                    r4 = 0
                    r5 = 0
                L_0x07ef:
                    int[] r6 = r0.lineCounts
                    int r6 = r6.length
                    if (r5 >= r6) goto L_0x0866
                    int[] r6 = r0.lineCounts
                    r6 = r6[r5]
                    float[] r7 = r0.heights
                    r7 = r7[r5]
                    int r8 = r10.maxSizeWidth
                    r9 = 0
                    r11 = 0
                L_0x0800:
                    if (r11 >= r6) goto L_0x0852
                    r17 = r14[r3]
                    r19 = r1
                    float r1 = r17 * r7
                    int r1 = (int) r1
                    int r8 = r8 - r1
                    r20 = r2
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                    java.lang.Object r2 = r2.get(r3)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r2
                    r22 = 0
                    if (r5 != 0) goto L_0x081a
                    r22 = r22 | 4
                L_0x081a:
                    r24 = r8
                    int[] r8 = r0.lineCounts
                    int r8 = r8.length
                    r25 = 1
                    int r8 = r8 + -1
                    if (r5 != r8) goto L_0x0827
                    r22 = r22 | 8
                L_0x0827:
                    if (r11 != 0) goto L_0x082b
                    r22 = r22 | 1
                L_0x082b:
                    int r8 = r6 + -1
                    if (r11 != r8) goto L_0x0833
                    r22 = r22 | 2
                    r8 = r2
                    r9 = r8
                L_0x0833:
                    float r38 = r7 / r27
                    r32 = r2
                    r33 = r11
                    r34 = r11
                    r35 = r5
                    r36 = r5
                    r37 = r1
                    r39 = r22
                    r32.set(r33, r34, r35, r36, r37, r38, r39)
                    int r3 = r3 + 1
                    int r11 = r11 + 1
                    r1 = r19
                    r2 = r20
                    r8 = r24
                    goto L_0x0800
                L_0x0852:
                    r19 = r1
                    r20 = r2
                    int r1 = r9.pw
                    int r1 = r1 + r8
                    r9.pw = r1
                    int r1 = r9.spanSize
                    int r1 = r1 + r8
                    r9.spanSize = r1
                    float r4 = r4 + r7
                    int r5 = r5 + 1
                    r1 = r19
                    goto L_0x07ef
                L_0x0866:
                    r19 = r1
                    r20 = r2
                L_0x086a:
                    r0 = 0
                L_0x086b:
                    r1 = r31
                    if (r0 >= r1) goto L_0x0884
                    java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                    java.lang.Object r2 = r2.get(r0)
                    im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = (im.bclpbkiauv.messenger.MessageObject.GroupedMessagePosition) r2
                    int r3 = r2.flags
                    r4 = 1
                    r3 = r3 & r4
                    if (r3 == 0) goto L_0x087f
                    r2.edge = r4
                L_0x087f:
                    int r0 = r0 + 1
                    r31 = r1
                    goto L_0x086b
                L_0x0884:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.BlockCollageCell.GroupedMessages.calculate():void");
            }
        }

        public BlockCollageCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            AnonymousClass1 r0 = new RecyclerListView(context, ArticleViewer.this) {
                public void requestLayout() {
                    if (!BlockCollageCell.this.inLayout) {
                        super.requestLayout();
                    }
                }
            };
            this.innerListView = r0;
            r0.addItemDecoration(new RecyclerView.ItemDecoration(ArticleViewer.this) {
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    MessageObject.GroupedMessagePosition position;
                    outRect.bottom = 0;
                    if (view instanceof BlockPhotoCell) {
                        position = BlockCollageCell.this.group.positions.get(((BlockPhotoCell) view).currentBlock);
                    } else if (view instanceof BlockVideoCell) {
                        position = BlockCollageCell.this.group.positions.get(((BlockVideoCell) view).currentBlock);
                    } else {
                        position = null;
                    }
                    if (position != null && position.siblingHeights != null) {
                        float maxHeight = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
                        int h = 0;
                        for (float f : position.siblingHeights) {
                            h += (int) Math.ceil((double) (f * maxHeight));
                        }
                        int h2 = h + ((position.maxY - position.minY) * AndroidUtilities.dp2(11.0f));
                        int count = BlockCollageCell.this.group.posArray.size();
                        int a = 0;
                        while (true) {
                            if (a >= count) {
                                break;
                            }
                            MessageObject.GroupedMessagePosition pos = BlockCollageCell.this.group.posArray.get(a);
                            if (pos.minY == position.minY && ((pos.minX != position.minX || pos.maxX != position.maxX || pos.minY != position.minY || pos.maxY != position.maxY) && pos.minY == position.minY)) {
                                h2 -= ((int) Math.ceil((double) (pos.ph * maxHeight))) - AndroidUtilities.dp(4.0f);
                                break;
                            }
                            a++;
                        }
                        outRect.bottom = -h2;
                    }
                }
            });
            final ArticleViewer articleViewer = ArticleViewer.this;
            AnonymousClass3 r2 = new GridLayoutManagerFixed(context, 1000, 1, true) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                public boolean shouldLayoutChildFromOpositeSide(View child) {
                    return false;
                }

                /* access modifiers changed from: protected */
                public boolean hasSiblingChild(int position) {
                    MessageObject.GroupedMessagePosition pos = BlockCollageCell.this.group.positions.get(BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1));
                    if (pos.minX == pos.maxX || pos.minY != pos.maxY || pos.minY == 0) {
                        return false;
                    }
                    int count = BlockCollageCell.this.group.posArray.size();
                    for (int a = 0; a < count; a++) {
                        MessageObject.GroupedMessagePosition p = BlockCollageCell.this.group.posArray.get(a);
                        if (p != pos && p.minY <= pos.minY && p.maxY >= pos.minY) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            this.gridLayoutManager = r2;
            r2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(ArticleViewer.this) {
                public int getSpanSize(int position) {
                    return BlockCollageCell.this.group.positions.get(BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1)).spanSize;
                }
            });
            this.innerListView.setLayoutManager(this.gridLayoutManager);
            RecyclerListView recyclerListView = this.innerListView;
            AnonymousClass5 r1 = new RecyclerView.Adapter(ArticleViewer.this) {
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    if (viewType != 0) {
                        view = new BlockVideoCell(BlockCollageCell.this.getContext(), BlockCollageCell.this.parentAdapter, 2);
                    } else {
                        view = new BlockPhotoCell(BlockCollageCell.this.getContext(), BlockCollageCell.this.parentAdapter, 2);
                    }
                    return new RecyclerListView.Holder(view);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    TLRPC.PageBlock pageBlock = BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1);
                    if (holder.getItemViewType() != 0) {
                        BlockVideoCell cell = (BlockVideoCell) holder.itemView;
                        MessageObject.GroupedMessagePosition unused = cell.groupPosition = BlockCollageCell.this.group.positions.get(pageBlock);
                        cell.setBlock((TLRPC.TL_pageBlockVideo) pageBlock, true, true);
                        return;
                    }
                    BlockPhotoCell cell2 = (BlockPhotoCell) holder.itemView;
                    MessageObject.GroupedMessagePosition unused2 = cell2.groupPosition = BlockCollageCell.this.group.positions.get(pageBlock);
                    cell2.setBlock((TLRPC.TL_pageBlockPhoto) pageBlock, true, true);
                }

                public int getItemCount() {
                    if (BlockCollageCell.this.currentBlock == null) {
                        return 0;
                    }
                    return BlockCollageCell.this.currentBlock.items.size();
                }

                public int getItemViewType(int position) {
                    if (BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1) instanceof TLRPC.TL_pageBlockPhoto) {
                        return 0;
                    }
                    return 1;
                }
            };
            this.innerAdapter = r1;
            recyclerListView.setAdapter(r1);
            addView(this.innerListView, LayoutHelper.createFrame(-1, -2.0f));
            setWillNotDraw(false);
        }

        public void setBlock(TLRPC.TL_pageBlockCollage block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                this.group.calculate();
            }
            this.innerAdapter.notifyDataSetChanged();
            int color = ArticleViewer.this.getSelectedColor();
            if (color == 0) {
                this.innerListView.setGlowColor(-657673);
            } else if (color == 1) {
                this.innerListView.setGlowColor(-659492);
            } else if (color == 2) {
                this.innerListView.setGlowColor(-15461356);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int listWidth;
            int textWidth;
            int height2;
            this.inLayout = true;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockCollage tL_pageBlockCollage = this.currentBlock;
            if (tL_pageBlockCollage != null) {
                int listWidth2 = width;
                if (tL_pageBlockCollage.level > 0) {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    int listWidth3 = listWidth2 - (dp + AndroidUtilities.dp(18.0f));
                    textWidth = listWidth3;
                    listWidth = listWidth3;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                    listWidth = listWidth2;
                }
                this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(listWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
                int height3 = this.innerListView.getMeasuredHeight();
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                this.captionLayout = access$13600;
                if (access$13600 != null) {
                    this.textY = AndroidUtilities.dp(8.0f) + height3;
                    int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp2;
                    height2 = height3 + dp2 + AndroidUtilities.dp(4.0f);
                } else {
                    height2 = height3;
                }
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13700;
                if (access$13700 != null) {
                    height2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                height = height2 + AndroidUtilities.dp(16.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            this.inLayout = false;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(this.listX, AndroidUtilities.dp(8.0f), this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight() + AndroidUtilities.dp(8.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockSlideshowCell extends FrameLayout {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockSlideshow currentBlock;
        /* access modifiers changed from: private */
        public int currentPage;
        /* access modifiers changed from: private */
        public View dotsContainer;
        /* access modifiers changed from: private */
        public PagerAdapter innerAdapter;
        /* access modifiers changed from: private */
        public ViewPager innerListView;
        /* access modifiers changed from: private */
        public float pageOffset;
        /* access modifiers changed from: private */
        public WebpageAdapter parentAdapter;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockSlideshowCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            if (ArticleViewer.dotsPaint == null) {
                Paint unused = ArticleViewer.dotsPaint = new Paint(1);
                ArticleViewer.dotsPaint.setColor(-1);
            }
            AnonymousClass1 r0 = new ViewPager(context, ArticleViewer.this) {
                public boolean onTouchEvent(MotionEvent ev) {
                    return super.onTouchEvent(ev);
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    return super.onInterceptTouchEvent(ev);
                }
            };
            this.innerListView = r0;
            r0.addOnPageChangeListener(new ViewPager.OnPageChangeListener(ArticleViewer.this) {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    float width = (float) BlockSlideshowCell.this.innerListView.getMeasuredWidth();
                    if (width != 0.0f) {
                        BlockSlideshowCell blockSlideshowCell = BlockSlideshowCell.this;
                        float unused = blockSlideshowCell.pageOffset = (((((float) position) * width) + ((float) positionOffsetPixels)) - (((float) blockSlideshowCell.currentPage) * width)) / width;
                        BlockSlideshowCell.this.dotsContainer.invalidate();
                    }
                }

                public void onPageSelected(int position) {
                    int unused = BlockSlideshowCell.this.currentPage = position;
                    BlockSlideshowCell.this.dotsContainer.invalidate();
                }

                public void onPageScrollStateChanged(int state) {
                }
            });
            ViewPager viewPager = this.innerListView;
            AnonymousClass3 r2 = new PagerAdapter(ArticleViewer.this) {

                /* renamed from: im.bclpbkiauv.ui.ArticleViewer$BlockSlideshowCell$3$ObjectContainer */
                class ObjectContainer {
                    /* access modifiers changed from: private */
                    public TLRPC.PageBlock block;
                    /* access modifiers changed from: private */
                    public View view;

                    ObjectContainer() {
                    }
                }

                public int getCount() {
                    if (BlockSlideshowCell.this.currentBlock == null) {
                        return 0;
                    }
                    return BlockSlideshowCell.this.currentBlock.items.size();
                }

                public boolean isViewFromObject(View view, Object object) {
                    return ((ObjectContainer) object).view == view;
                }

                public int getItemPosition(Object object) {
                    if (BlockSlideshowCell.this.currentBlock.items.contains(((ObjectContainer) object).block)) {
                        return -1;
                    }
                    return -2;
                }

                public Object instantiateItem(ViewGroup container, int position) {
                    View view;
                    TLRPC.PageBlock block = BlockSlideshowCell.this.currentBlock.items.get(position);
                    if (block instanceof TLRPC.TL_pageBlockPhoto) {
                        view = new BlockPhotoCell(BlockSlideshowCell.this.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        ((BlockPhotoCell) view).setBlock((TLRPC.TL_pageBlockPhoto) block, true, true);
                    } else {
                        view = new BlockVideoCell(BlockSlideshowCell.this.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        ((BlockVideoCell) view).setBlock((TLRPC.TL_pageBlockVideo) block, true, true);
                    }
                    container.addView(view);
                    ObjectContainer objectContainer = new ObjectContainer();
                    View unused = objectContainer.view = view;
                    TLRPC.PageBlock unused2 = objectContainer.block = block;
                    return objectContainer;
                }

                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView(((ObjectContainer) object).view);
                }

                public void unregisterDataSetObserver(DataSetObserver observer) {
                    if (observer != null) {
                        super.unregisterDataSetObserver(observer);
                    }
                }
            };
            this.innerAdapter = r2;
            viewPager.setAdapter(r2);
            int color = ArticleViewer.this.getSelectedColor();
            if (color == 0) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -657673);
            } else if (color == 1) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -659492);
            } else if (color == 2) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -15461356);
            }
            addView(this.innerListView);
            AnonymousClass4 r1 = new View(context, ArticleViewer.this) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    int xOffset;
                    if (BlockSlideshowCell.this.currentBlock != null) {
                        int count = BlockSlideshowCell.this.innerAdapter.getCount();
                        int totalWidth = (AndroidUtilities.dp(7.0f) * count) + ((count - 1) * AndroidUtilities.dp(6.0f)) + AndroidUtilities.dp(4.0f);
                        if (totalWidth < getMeasuredWidth()) {
                            xOffset = (getMeasuredWidth() - totalWidth) / 2;
                        } else {
                            xOffset = AndroidUtilities.dp(4.0f);
                            int size = AndroidUtilities.dp(13.0f);
                            int halfCount = ((getMeasuredWidth() - AndroidUtilities.dp(8.0f)) / 2) / size;
                            if (BlockSlideshowCell.this.currentPage == (count - halfCount) - 1 && BlockSlideshowCell.this.pageOffset < 0.0f) {
                                xOffset -= ((int) (BlockSlideshowCell.this.pageOffset * ((float) size))) + (((count - (halfCount * 2)) - 1) * size);
                            } else if (BlockSlideshowCell.this.currentPage >= (count - halfCount) - 1) {
                                xOffset -= ((count - (halfCount * 2)) - 1) * size;
                            } else if (BlockSlideshowCell.this.currentPage > halfCount) {
                                xOffset -= ((int) (BlockSlideshowCell.this.pageOffset * ((float) size))) + ((BlockSlideshowCell.this.currentPage - halfCount) * size);
                            } else if (BlockSlideshowCell.this.currentPage == halfCount && BlockSlideshowCell.this.pageOffset > 0.0f) {
                                xOffset -= (int) (BlockSlideshowCell.this.pageOffset * ((float) size));
                            }
                        }
                        int a = 0;
                        while (a < BlockSlideshowCell.this.currentBlock.items.size()) {
                            int cx = AndroidUtilities.dp(4.0f) + xOffset + (AndroidUtilities.dp(13.0f) * a);
                            Drawable drawable = BlockSlideshowCell.this.currentPage == a ? ArticleViewer.this.slideDotBigDrawable : ArticleViewer.this.slideDotDrawable;
                            drawable.setBounds(cx - AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f) + cx, AndroidUtilities.dp(10.0f));
                            drawable.draw(canvas);
                            a++;
                        }
                    }
                }
            };
            this.dotsContainer = r1;
            addView(r1);
            setWillNotDraw(false);
        }

        public void setBlock(TLRPC.TL_pageBlockSlideshow block) {
            this.currentBlock = block;
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setCurrentItem(0, false);
            this.innerListView.forceLayout();
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int height2;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                int height3 = AndroidUtilities.dp(310.0f);
                this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height3, 1073741824));
                int size = this.currentBlock.items.size();
                this.dotsContainer.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), 1073741824));
                int textWidth = width - AndroidUtilities.dp(36.0f);
                this.textY = AndroidUtilities.dp(16.0f) + height3;
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                this.captionLayout = access$13600;
                if (access$13600 != null) {
                    int dp = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp;
                    height2 = height3 + dp + AndroidUtilities.dp(4.0f);
                } else {
                    height2 = height3;
                }
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13700;
                if (access$13700 != null) {
                    height2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                height = height2 + AndroidUtilities.dp(16.0f);
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(0, AndroidUtilities.dp(8.0f), this.innerListView.getMeasuredWidth(), AndroidUtilities.dp(8.0f) + this.innerListView.getMeasuredHeight());
            int y = this.innerListView.getBottom() - AndroidUtilities.dp(23.0f);
            View view = this.dotsContainer;
            view.layout(0, y, view.getMeasuredWidth(), this.dotsContainer.getMeasuredHeight() + y);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockListItemCell extends ViewGroup {
        /* access modifiers changed from: private */
        public RecyclerView.ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockListItem currentBlock;
        private int currentBlockType;
        private boolean drawDot;
        private int numOffsetY;
        private WebpageAdapter parentAdapter;
        private boolean parentIsList;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        /* access modifiers changed from: private */
        public boolean verticalAlign;

        public BlockListItemCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockListItem block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                RecyclerView.ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    int access$6600 = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.currentBlockType = access$6600;
                    RecyclerView.ViewHolder onCreateViewHolder = this.parentAdapter.onCreateViewHolder(this, access$6600);
                    this.blockLayout = onCreateViewHolder;
                    addView(onCreateViewHolder.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int maxWidth;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TL_pageBlockListItem tL_pageBlockListItem = this.currentBlock;
            if (tL_pageBlockListItem != null) {
                this.textLayout = null;
                this.textY = (tL_pageBlockListItem.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.dp(10.0f) : 0;
                this.numOffsetY = 0;
                if (!(this.currentBlock.parent.lastMaxNumCalcWidth == width && this.currentBlock.parent.lastFontSize == ArticleViewer.this.selectedFontSize)) {
                    int unused = this.currentBlock.parent.lastMaxNumCalcWidth = width;
                    int unused2 = this.currentBlock.parent.lastFontSize = ArticleViewer.this.selectedFontSize;
                    int unused3 = this.currentBlock.parent.maxNumWidth = 0;
                    int size = this.currentBlock.parent.items.size();
                    for (int a = 0; a < size; a++) {
                        TL_pageBlockListItem item = (TL_pageBlockListItem) this.currentBlock.parent.items.get(a);
                        if (item.num != null) {
                            DrawingText unused4 = item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, (TLRPC.RichText) null, width - AndroidUtilities.dp(54.0f), this.currentBlock, this.parentAdapter);
                            int unused5 = this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) item.numLayout.getLineWidth(0)));
                        }
                    }
                    int unused6 = this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                this.drawDot = !this.currentBlock.parent.pageBlockList.ordered;
                this.parentIsList = (getParent() instanceof BlockListItemCell) || (getParent() instanceof BlockOrderedListItemCell);
                if (ArticleViewer.this.isRtl) {
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(24.0f) + this.currentBlock.parent.maxNumWidth + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f));
                }
                int maxWidth2 = (width - AndroidUtilities.dp(18.0f)) - this.textX;
                if (ArticleViewer.this.isRtl) {
                    maxWidth = maxWidth2 - ((AndroidUtilities.dp(6.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
                } else {
                    maxWidth = maxWidth2;
                }
                if (this.currentBlock.textItem != null) {
                    DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, this.currentBlock.textItem, maxWidth, (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.textLayout = access$13700;
                    if (access$13700 != null && access$13700.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.dp(2.5f)) - this.textLayout.getLineAscent(0);
                        }
                        height = 0 + this.textLayout.getHeight() + AndroidUtilities.dp(8.0f);
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    RecyclerView.ViewHolder viewHolder = this.blockLayout;
                    if (viewHolder != null) {
                        if (viewHolder.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.dp(8.0f);
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if ((this.blockLayout.itemView instanceof BlockHeaderCell) || (this.blockLayout.itemView instanceof BlockSubheaderCell) || (this.blockLayout.itemView instanceof BlockTitleCell) || (this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                        } else if (ArticleViewer.this.isListItemBlock(this.currentBlock.blockItem)) {
                            this.blockX = 0;
                            this.blockY = 0;
                            this.textY = 0;
                            if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                                height = 0 - AndroidUtilities.dp(10.0f);
                            }
                            maxWidth = width;
                            height -= AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockTableCell) {
                            this.blockX -= AndroidUtilities.dp(18.0f);
                            maxWidth += AndroidUtilities.dp(36.0f);
                        }
                        this.blockLayout.itemView.measure(View.MeasureSpec.makeMeasureSpec(maxWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
                        if ((this.blockLayout.itemView instanceof BlockParagraphCell) && this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            BlockParagraphCell paragraphCell = (BlockParagraphCell) this.blockLayout.itemView;
                            if (paragraphCell.textLayout != null && paragraphCell.textLayout.getLineCount() > 0) {
                                this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.dp(2.5f)) - paragraphCell.textLayout.getLineAscent(0);
                            }
                        }
                        if (this.currentBlock.blockItem instanceof TLRPC.TL_pageBlockDetails) {
                            this.verticalAlign = true;
                            this.blockY = 0;
                            if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                                height -= AndroidUtilities.dp(10.0f);
                            }
                            height -= AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockOrderedListItemCell) {
                            this.verticalAlign = ((BlockOrderedListItemCell) this.blockLayout.itemView).verticalAlign;
                        } else if (this.blockLayout.itemView instanceof BlockListItemCell) {
                            this.verticalAlign = ((BlockListItemCell) this.blockLayout.itemView).verticalAlign;
                        }
                        if (this.verticalAlign && this.currentBlock.numLayout != null) {
                            this.textY = ((this.blockLayout.itemView.getMeasuredHeight() - this.currentBlock.numLayout.getHeight()) / 2) - AndroidUtilities.dp(4.0f);
                            this.drawDot = false;
                        }
                        height += this.blockLayout.itemView.getMeasuredHeight();
                    }
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.parent.items.get(this.currentBlock.parent.items.size() - 1) == this.currentBlock) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                    height += AndroidUtilities.dp(10.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                int i = this.blockX;
                view.layout(i, this.blockY, this.blockLayout.itemView.getMeasuredWidth() + i, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int width = getMeasuredWidth();
                if (this.currentBlock.numLayout != null) {
                    canvas.save();
                    int i = 0;
                    if (ArticleViewer.this.isRtl) {
                        float dp = (float) (((width - AndroidUtilities.dp(15.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
                        int i2 = this.textY + this.numOffsetY;
                        if (this.drawDot) {
                            i = AndroidUtilities.dp(1.0f);
                        }
                        canvas.translate(dp, (float) (i2 - i));
                    } else {
                        float dp2 = (float) (((AndroidUtilities.dp(15.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
                        int i3 = this.textY + this.numOffsetY;
                        if (this.drawDot) {
                            i = AndroidUtilities.dp(1.0f);
                        }
                        canvas.translate(dp2, (float) (i3 - i));
                    }
                    this.currentBlock.numLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                info.setText(drawingText.getText());
            }
        }
    }

    private class BlockOrderedListItemCell extends ViewGroup {
        /* access modifiers changed from: private */
        public RecyclerView.ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockOrderedListItem currentBlock;
        private int currentBlockType;
        private int numOffsetY;
        private WebpageAdapter parentAdapter;
        private boolean parentIsList;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        /* access modifiers changed from: private */
        public boolean verticalAlign;

        public BlockOrderedListItemCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockOrderedListItem block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                RecyclerView.ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    int access$6600 = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.currentBlockType = access$6600;
                    RecyclerView.ViewHolder onCreateViewHolder = this.parentAdapter.onCreateViewHolder(this, access$6600);
                    this.blockLayout = onCreateViewHolder;
                    addView(onCreateViewHolder.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int maxWidth;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = this.currentBlock;
            if (tL_pageBlockOrderedListItem != null) {
                this.textLayout = null;
                this.textY = (tL_pageBlockOrderedListItem.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.dp(10.0f) : 0;
                this.numOffsetY = 0;
                if (!(this.currentBlock.parent.lastMaxNumCalcWidth == width && this.currentBlock.parent.lastFontSize == ArticleViewer.this.selectedFontSize)) {
                    int unused = this.currentBlock.parent.lastMaxNumCalcWidth = width;
                    int unused2 = this.currentBlock.parent.lastFontSize = ArticleViewer.this.selectedFontSize;
                    int unused3 = this.currentBlock.parent.maxNumWidth = 0;
                    int size = this.currentBlock.parent.items.size();
                    for (int a = 0; a < size; a++) {
                        TL_pageBlockOrderedListItem item = (TL_pageBlockOrderedListItem) this.currentBlock.parent.items.get(a);
                        if (item.num != null) {
                            DrawingText unused4 = item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, (TLRPC.RichText) null, width - AndroidUtilities.dp(54.0f), this.currentBlock, this.parentAdapter);
                            int unused5 = this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) item.numLayout.getLineWidth(0)));
                        }
                    }
                    int unused6 = this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                if (ArticleViewer.this.isRtl) {
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(24.0f) + this.currentBlock.parent.maxNumWidth + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f));
                }
                this.verticalAlign = false;
                int maxWidth2 = (width - AndroidUtilities.dp(18.0f)) - this.textX;
                if (ArticleViewer.this.isRtl) {
                    maxWidth = maxWidth2 - ((AndroidUtilities.dp(6.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f)));
                } else {
                    maxWidth = maxWidth2;
                }
                if (this.currentBlock.textItem != null) {
                    DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, this.currentBlock.textItem, maxWidth, (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.textLayout = access$13700;
                    if (access$13700 != null && access$13700.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            this.numOffsetY = this.currentBlock.numLayout.getLineAscent(0) - this.textLayout.getLineAscent(0);
                        }
                        height = 0 + this.textLayout.getHeight() + AndroidUtilities.dp(8.0f);
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    RecyclerView.ViewHolder viewHolder = this.blockLayout;
                    if (viewHolder != null) {
                        if (viewHolder.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.dp(8.0f);
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if ((this.blockLayout.itemView instanceof BlockHeaderCell) || (this.blockLayout.itemView instanceof BlockSubheaderCell) || (this.blockLayout.itemView instanceof BlockTitleCell) || (this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                        } else if (ArticleViewer.this.isListItemBlock(this.currentBlock.blockItem)) {
                            this.blockX = 0;
                            this.blockY = 0;
                            this.textY = 0;
                            maxWidth = width;
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockTableCell) {
                            this.blockX -= AndroidUtilities.dp(18.0f);
                            maxWidth += AndroidUtilities.dp(36.0f);
                        }
                        this.blockLayout.itemView.measure(View.MeasureSpec.makeMeasureSpec(maxWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
                        if ((this.blockLayout.itemView instanceof BlockParagraphCell) && this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            BlockParagraphCell paragraphCell = (BlockParagraphCell) this.blockLayout.itemView;
                            if (paragraphCell.textLayout != null && paragraphCell.textLayout.getLineCount() > 0) {
                                this.numOffsetY = this.currentBlock.numLayout.getLineAscent(0) - paragraphCell.textLayout.getLineAscent(0);
                            }
                        }
                        if (this.currentBlock.blockItem instanceof TLRPC.TL_pageBlockDetails) {
                            this.verticalAlign = true;
                            this.blockY = 0;
                            height -= AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockOrderedListItemCell) {
                            this.verticalAlign = ((BlockOrderedListItemCell) this.blockLayout.itemView).verticalAlign;
                        } else if (this.blockLayout.itemView instanceof BlockListItemCell) {
                            this.verticalAlign = ((BlockListItemCell) this.blockLayout.itemView).verticalAlign;
                        }
                        if (this.verticalAlign && this.currentBlock.numLayout != null) {
                            this.textY = (this.blockLayout.itemView.getMeasuredHeight() - this.currentBlock.numLayout.getHeight()) / 2;
                        }
                        height += this.blockLayout.itemView.getMeasuredHeight();
                    }
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.parent.items.get(this.currentBlock.parent.items.size() - 1) == this.currentBlock) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                    height += AndroidUtilities.dp(10.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                int i = this.blockX;
                view.layout(i, this.blockY, this.blockLayout.itemView.getMeasuredWidth() + i, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int width = getMeasuredWidth();
                if (this.currentBlock.numLayout != null) {
                    canvas.save();
                    if (ArticleViewer.this.isRtl) {
                        canvas.translate((float) (((width - AndroidUtilities.dp(18.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f))), (float) (this.textY + this.numOffsetY));
                    } else {
                        canvas.translate((float) (((AndroidUtilities.dp(18.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f))), (float) (this.textY + this.numOffsetY));
                    }
                    this.currentBlock.numLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                info.setText(drawingText.getText());
            }
        }
    }

    private class BlockDetailsCell extends View implements Drawable.Callback {
        /* access modifiers changed from: private */
        public AnimatedArrowDrawable arrow;
        private TLRPC.TL_pageBlockDetails currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(50.0f);
        private int textY = (AndroidUtilities.dp(11.0f) + 1);

        public BlockDetailsCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            this.arrow = new AnimatedArrowDrawable(ArticleViewer.this.getGrayTextColor(), true);
        }

        public void invalidateDrawable(Drawable drawable) {
            invalidate();
        }

        public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
        }

        public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        }

        public void setBlock(TLRPC.TL_pageBlockDetails block) {
            this.currentBlock = block;
            this.arrow.setAnimationProgress(block.open ? 0.0f : 1.0f);
            this.arrow.setCallback(this);
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int h = AndroidUtilities.dp(39.0f);
            TLRPC.TL_pageBlockDetails tL_pageBlockDetails = this.currentBlock;
            if (tL_pageBlockDetails != null) {
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, tL_pageBlockDetails.title, width - AndroidUtilities.dp(52.0f), (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13700;
                if (access$13700 != null) {
                    h = Math.max(h, AndroidUtilities.dp(21.0f) + this.textLayout.getHeight());
                    this.textY = ((this.textLayout.getHeight() + AndroidUtilities.dp(21.0f)) - this.textLayout.getHeight()) / 2;
                }
            }
            setMeasuredDimension(width, h + 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(18.0f), (float) (((getMeasuredHeight() - AndroidUtilities.dp(13.0f)) - 1) / 2));
                this.arrow.draw(canvas);
                canvas.restore();
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                int y = getMeasuredHeight() - 1;
                canvas.drawLine(0.0f, (float) y, (float) getMeasuredWidth(), (float) y, ArticleViewer.dividerPaint);
            }
        }
    }

    private class BlockDetailsBottomCell extends View {
        private RectF rect = new RectF();

        public BlockDetailsBottomCell(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(4.0f) + 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawLine(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, ArticleViewer.dividerPaint);
        }
    }

    private class BlockRelatedArticlesShadowCell extends View {
        private CombinedDrawable shadowDrawable;

        public BlockRelatedArticlesShadowCell(Context context) {
            super(context);
            CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(-986896), Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, -16777216));
            this.shadowDrawable = combinedDrawable;
            combinedDrawable.setFullsize(true);
            setBackgroundDrawable(this.shadowDrawable);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(12.0f));
            int color = ArticleViewer.this.getSelectedColor();
            if (color == 0) {
                Theme.setCombinedDrawableColor(this.shadowDrawable, -986896, false);
            } else if (color == 1) {
                Theme.setCombinedDrawableColor(this.shadowDrawable, -1712440, false);
            } else if (color == 2) {
                Theme.setCombinedDrawableColor(this.shadowDrawable, -15000805, false);
            }
        }
    }

    private class BlockRelatedArticlesHeaderCell extends View {
        private TLRPC.TL_pageBlockRelatedArticles currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockRelatedArticlesHeaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockRelatedArticles block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockRelatedArticles tL_pageBlockRelatedArticles = this.currentBlock;
            if (tL_pageBlockRelatedArticles != null) {
                DrawingText access$14400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tL_pageBlockRelatedArticles.title, width - AndroidUtilities.dp(52.0f), 0, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                this.textLayout = access$14400;
                if (access$14400 != null) {
                    this.textY = AndroidUtilities.dp(6.0f) + ((AndroidUtilities.dp(32.0f) - this.textLayout.getHeight()) / 2);
                }
            }
            if (this.textLayout != null) {
                setMeasuredDimension(width, AndroidUtilities.dp(38.0f));
            } else {
                setMeasuredDimension(width, 1);
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockRelatedArticlesCell extends View {
        private int additionalHeight;
        /* access modifiers changed from: private */
        public TL_pageBlockRelatedArticlesChild currentBlock;
        private boolean divider;
        private boolean drawImage;
        private ImageReceiver imageView;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textOffset;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(10.0f);

        public BlockRelatedArticlesCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageView = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(6.0f));
        }

        public void setBlock(TL_pageBlockRelatedArticlesChild block) {
            this.currentBlock = block;
            requestLayout();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int availableWidth;
            int layoutHeight;
            boolean isTitleRtl;
            int height;
            int height2;
            String description;
            int height3;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            this.divider = this.currentBlock.num != this.currentBlock.parent.articles.size() - 1;
            TLRPC.TL_pageRelatedArticle item = this.currentBlock.parent.articles.get(this.currentBlock.num);
            this.additionalHeight = 0;
            if (ArticleViewer.this.selectedFontSize == 0) {
                this.additionalHeight = -AndroidUtilities.dp(4.0f);
            } else if (ArticleViewer.this.selectedFontSize == 1) {
                this.additionalHeight = -AndroidUtilities.dp(2.0f);
            } else if (ArticleViewer.this.selectedFontSize == 3) {
                this.additionalHeight = AndroidUtilities.dp(2.0f);
            } else if (ArticleViewer.this.selectedFontSize == 4) {
                this.additionalHeight = AndroidUtilities.dp(4.0f);
            }
            TLRPC.Photo photo = item.photo_id != 0 ? ArticleViewer.this.getPhotoWithId(item.photo_id) : null;
            if (photo != null) {
                this.drawImage = true;
                TLRPC.PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                if (image == thumb) {
                    thumb = null;
                }
                this.imageView.setImage(ImageLocation.getForPhoto(image, photo), "64_64", ImageLocation.getForPhoto(thumb, photo), "64_64_b", image.size, (String) null, ArticleViewer.this.currentPage, 1);
            } else {
                this.drawImage = false;
            }
            int layoutHeight2 = AndroidUtilities.dp(60.0f);
            int availableWidth2 = width - AndroidUtilities.dp(36.0f);
            if (this.drawImage) {
                int imageWidth = AndroidUtilities.dp(44.0f);
                this.imageView.setImageCoords((width - imageWidth) - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), imageWidth, imageWidth);
                availableWidth = availableWidth2 - (this.imageView.getImageWidth() + AndroidUtilities.dp(6.0f));
            } else {
                availableWidth = availableWidth2;
            }
            int height4 = AndroidUtilities.dp(18.0f);
            boolean isTitleRtl2 = false;
            if (item.title != null) {
                layoutHeight = layoutHeight2;
                this.textLayout = ArticleViewer.this.createLayoutForText(this, item.title, (TLRPC.RichText) null, availableWidth, this.textY, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 3, this.parentAdapter);
            } else {
                layoutHeight = layoutHeight2;
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                int count = drawingText.getLineCount();
                int lineCount = 4 - count;
                this.textOffset = this.textLayout.getHeight() + AndroidUtilities.dp(6.0f) + this.additionalHeight;
                int height5 = height4 + this.textLayout.getHeight();
                int a = 0;
                while (true) {
                    if (a >= count) {
                        break;
                    } else if (this.textLayout.getLineLeft(a) != 0.0f) {
                        isTitleRtl2 = true;
                        break;
                    } else {
                        a++;
                    }
                }
                isTitleRtl = isTitleRtl2;
                height = height5;
                height2 = lineCount;
            } else {
                this.textOffset = 0;
                isTitleRtl = false;
                height = height4;
                height2 = 4;
            }
            if (item.published_date != 0 && !TextUtils.isEmpty(item.author)) {
                description = LocaleController.formatString("ArticleDateByAuthor", R.string.ArticleDateByAuthor, LocaleController.getInstance().chatFullDate.format(((long) item.published_date) * 1000), item.author);
            } else if (!TextUtils.isEmpty(item.author)) {
                description = LocaleController.formatString("ArticleByAuthor", R.string.ArticleByAuthor, item.author);
            } else if (item.published_date != 0) {
                description = LocaleController.getInstance().chatFullDate.format(((long) item.published_date) * 1000);
            } else if (!TextUtils.isEmpty(item.description)) {
                description = item.description;
            } else {
                description = item.url;
            }
            ArticleViewer articleViewer = ArticleViewer.this;
            DrawingText access$14400 = articleViewer.createLayoutForText(this, description, (TLRPC.RichText) null, availableWidth, this.textY + this.textOffset, this.currentBlock, (articleViewer.isRtl || isTitleRtl) ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, height2, this.parentAdapter);
            this.textLayout2 = access$14400;
            if (access$14400 != null) {
                int height6 = height + access$14400.getHeight();
                if (this.textLayout != null) {
                    height3 = height6 + AndroidUtilities.dp(6.0f) + this.additionalHeight;
                } else {
                    height3 = height6;
                }
            } else {
                height3 = height;
            }
            setMeasuredDimension(width, (this.divider ? 1 : 0) + Math.max(layoutHeight, height3));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.drawImage) {
                    this.imageView.draw(canvas);
                }
                canvas.save();
                canvas.translate((float) this.textX, (float) AndroidUtilities.dp(10.0f));
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.draw(canvas);
                }
                if (this.textLayout2 != null) {
                    canvas.translate(0.0f, (float) this.textOffset);
                    this.textLayout2.draw(canvas);
                }
                canvas.restore();
                if (this.divider) {
                    canvas.drawLine(ArticleViewer.this.isRtl ? 0.0f : (float) AndroidUtilities.dp(17.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (ArticleViewer.this.isRtl ? AndroidUtilities.dp(17.0f) : 0)), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                }
            }
        }
    }

    private class BlockHeaderCell extends View {
        private TLRPC.TL_pageBlockHeader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockHeaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockHeader block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockHeader tL_pageBlockHeader = this.currentBlock;
            if (tL_pageBlockHeader != null) {
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, tL_pageBlockHeader.text, width - AndroidUtilities.dp(36.0f), (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13700;
                if (access$13700 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout != null) {
                info.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVHeading", R.string.AccDescrIVHeading));
            }
        }
    }

    private class BlockDividerCell extends View {
        private RectF rect = new RectF();

        public BlockDividerCell(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(18.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int width = getMeasuredWidth() / 3;
            this.rect.set((float) width, (float) AndroidUtilities.dp(8.0f), (float) (width * 2), (float) AndroidUtilities.dp(10.0f));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ArticleViewer.dividerPaint);
        }
    }

    private class BlockSubtitleCell extends View {
        private TLRPC.TL_pageBlockSubtitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubtitleCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockSubtitle block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockSubtitle tL_pageBlockSubtitle = this.currentBlock;
            if (tL_pageBlockSubtitle != null) {
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, tL_pageBlockSubtitle.text, width - AndroidUtilities.dp(36.0f), (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13700;
                if (access$13700 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout != null) {
                info.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVHeading", R.string.AccDescrIVHeading));
            }
        }
    }

    private class BlockPullquoteCell extends View {
        private TLRPC.TL_pageBlockPullquote currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockPullquoteCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockPullquote block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockPullquote tL_pageBlockPullquote = this.currentBlock;
            if (tL_pageBlockPullquote != null) {
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tL_pageBlockPullquote.text, width - AndroidUtilities.dp(36.0f), this.currentBlock, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    height = 0 + AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                }
                DrawingText access$136002 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption, width - AndroidUtilities.dp(36.0f), this.currentBlock, this.parentAdapter);
                this.textLayout2 = access$136002;
                if (access$136002 != null) {
                    this.textY2 = AndroidUtilities.dp(2.0f) + height;
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockBlockquoteCell extends View {
        private TLRPC.TL_pageBlockBlockquote currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockBlockquoteCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockBlockquote block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                int textWidth = width - AndroidUtilities.dp(50.0f);
                if (this.currentBlock.level > 0) {
                    textWidth -= AndroidUtilities.dp((float) (this.currentBlock.level * 14));
                }
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, textWidth, this.currentBlock, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    height = 0 + AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                }
                if (this.currentBlock.level > 0) {
                    if (ArticleViewer.this.isRtl) {
                        this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 14));
                    } else {
                        this.textX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(32.0f);
                    }
                } else if (ArticleViewer.this.isRtl) {
                    this.textX = AndroidUtilities.dp(14.0f);
                } else {
                    this.textX = AndroidUtilities.dp(32.0f);
                }
                DrawingText access$136002 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption, textWidth, this.currentBlock, this.parentAdapter);
                this.textLayout2 = access$136002;
                if (access$136002 != null) {
                    this.textY2 = AndroidUtilities.dp(8.0f) + height;
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
                if (ArticleViewer.this.isRtl) {
                    int x = getMeasuredWidth() - AndroidUtilities.dp(20.0f);
                    canvas.drawRect((float) x, (float) AndroidUtilities.dp(6.0f), (float) (AndroidUtilities.dp(2.0f) + x), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
                } else {
                    canvas.drawRect((float) AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18)), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 20)), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockPhotoCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
        private int TAG;
        boolean autoDownload;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private boolean cancelLoading;
        private DrawingText captionLayout;
        private BlockChannelCell channelCell;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockPhoto currentBlock;
        private String currentFilter;
        private TLRPC.Photo currentPhoto;
        private TLRPC.PhotoSize currentPhotoObject;
        private TLRPC.PhotoSize currentPhotoObjectThumb;
        private String currentThumbFilter;
        private int currentType;
        /* access modifiers changed from: private */
        public MessageObject.GroupedMessagePosition groupPosition;
        /* access modifiers changed from: private */
        public ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private boolean isLast;
        private Drawable linkDrawable;
        private WebpageAdapter parentAdapter;
        private TLRPC.PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress2 radialProgress;
        private int textX;
        private int textY;

        public BlockPhotoCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.channelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setProgressColor(-1);
            this.radialProgress.setColors(1711276032, (int) Theme.ACTION_BAR_PHOTO_VIEWER_COLOR, -1, -2500135);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
            this.currentType = type;
        }

        public void setBlock(TLRPC.TL_pageBlockPhoto block, boolean first, boolean last) {
            this.parentBlock = null;
            this.currentBlock = block;
            this.isFirst = first;
            this.isLast = last;
            this.channelCell.setVisibility(4);
            if (!TextUtils.isEmpty(this.currentBlock.url)) {
                this.linkDrawable = getResources().getDrawable(R.drawable.instant_link);
            }
            TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto = this.currentBlock;
            if (tL_pageBlockPhoto != null) {
                TLRPC.Photo photo = ArticleViewer.this.getPhotoWithId(tL_pageBlockPhoto.photo_id);
                if (photo != null) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                } else {
                    this.currentPhotoObject = null;
                }
            } else {
                this.currentPhotoObject = null;
            }
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(TLRPC.PageBlock block) {
            this.parentBlock = block;
            if (ArticleViewer.this.channelBlock != null && (this.parentBlock instanceof TLRPC.TL_pageBlockCover)) {
                this.channelCell.setBlock(ArticleViewer.this.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0095, code lost:
            if (r1 <= ((float) (r2 + im.bclpbkiauv.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x009b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r12) {
            /*
                r11 = this;
                float r0 = r12.getX()
                float r1 = r12.getY()
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r2 = r11.channelCell
                int r2 = r2.getVisibility()
                r3 = 0
                r4 = 1
                if (r2 != 0) goto L_0x0060
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r2 = r11.channelCell
                float r2 = r2.getTranslationY()
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x0060
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r2 = r11.channelCell
                float r2 = r2.getTranslationY()
                r5 = 1109131264(0x421c0000, float:39.0)
                int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                float r2 = r2 + r5
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 >= 0) goto L_0x0060
                im.bclpbkiauv.ui.ArticleViewer r2 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockChannel r2 = r2.channelBlock
                if (r2 == 0) goto L_0x005f
                int r2 = r12.getAction()
                if (r2 != r4) goto L_0x005f
                im.bclpbkiauv.ui.ArticleViewer r2 = im.bclpbkiauv.ui.ArticleViewer.this
                int r2 = r2.currentAccount
                im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
                im.bclpbkiauv.ui.ArticleViewer r5 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockChannel r5 = r5.channelBlock
                im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r5.channel
                java.lang.String r5 = r5.username
                im.bclpbkiauv.ui.ArticleViewer r6 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.actionbar.BaseFragment r6 = r6.parentFragment
                r7 = 2
                r2.openByUserName(r5, r6, r7)
                im.bclpbkiauv.ui.ArticleViewer r2 = im.bclpbkiauv.ui.ArticleViewer.this
                r2.close(r3, r4)
            L_0x005f:
                return r4
            L_0x0060:
                int r2 = r12.getAction()
                if (r2 != 0) goto L_0x00a4
                im.bclpbkiauv.messenger.ImageReceiver r2 = r11.imageView
                boolean r2 = r2.isInsideImage(r0, r1)
                if (r2 == 0) goto L_0x00a4
                int r2 = r11.buttonState
                r5 = -1
                if (r2 == r5) goto L_0x0097
                int r2 = r11.buttonX
                float r5 = (float) r2
                int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r5 < 0) goto L_0x0097
                r5 = 1111490560(0x42400000, float:48.0)
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                int r2 = r2 + r6
                float r2 = (float) r2
                int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r2 > 0) goto L_0x0097
                int r2 = r11.buttonY
                float r6 = (float) r2
                int r6 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
                if (r6 < 0) goto L_0x0097
                int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                int r2 = r2 + r5
                float r2 = (float) r2
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x009b
            L_0x0097:
                int r2 = r11.buttonState
                if (r2 != 0) goto L_0x00a1
            L_0x009b:
                r11.buttonPressed = r4
                r11.invalidate()
                goto L_0x00d3
            L_0x00a1:
                r11.photoPressed = r4
                goto L_0x00d3
            L_0x00a4:
                int r2 = r12.getAction()
                if (r2 != r4) goto L_0x00c8
                boolean r2 = r11.photoPressed
                if (r2 == 0) goto L_0x00b8
                r11.photoPressed = r3
                im.bclpbkiauv.ui.ArticleViewer r2 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r5 = r11.currentBlock
                r2.openPhoto(r5)
                goto L_0x00d3
            L_0x00b8:
                int r2 = r11.buttonPressed
                if (r2 != r4) goto L_0x00d3
                r11.buttonPressed = r3
                r11.playSoundEffect(r3)
                r11.didPressedButton(r4)
                r11.invalidate()
                goto L_0x00d3
            L_0x00c8:
                int r2 = r12.getAction()
                r5 = 3
                if (r2 != r5) goto L_0x00d3
                r11.photoPressed = r3
                r11.buttonPressed = r3
            L_0x00d3:
                boolean r2 = r11.photoPressed
                if (r2 != 0) goto L_0x0105
                int r2 = r11.buttonPressed
                if (r2 != 0) goto L_0x0105
                im.bclpbkiauv.ui.ArticleViewer r5 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r8 = r11.captionLayout
                int r9 = r11.textX
                int r10 = r11.textY
                r6 = r12
                r7 = r11
                boolean r2 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10)
                if (r2 != 0) goto L_0x0105
                im.bclpbkiauv.ui.ArticleViewer r5 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r8 = r11.creditLayout
                int r9 = r11.textX
                int r2 = r11.textY
                int r6 = r11.creditOffset
                int r10 = r2 + r6
                r6 = r12
                r7 = r11
                boolean r2 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10)
                if (r2 != 0) goto L_0x0105
                boolean r2 = super.onTouchEvent(r12)
                if (r2 == 0) goto L_0x0106
            L_0x0105:
                r3 = 1
            L_0x0106:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.BlockPhotoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x014e, code lost:
            r6 = r8.currentType;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r28, int r29) {
            /*
                r27 = this;
                r8 = r27
                int r0 = android.view.View.MeasureSpec.getSize(r28)
                r1 = 0
                int r2 = r8.currentType
                r9 = 2
                r10 = 1
                if (r2 != r10) goto L_0x0023
                android.view.ViewParent r2 = r27.getParent()
                android.view.View r2 = (android.view.View) r2
                int r0 = r2.getMeasuredWidth()
                android.view.ViewParent r2 = r27.getParent()
                android.view.View r2 = (android.view.View) r2
                int r1 = r2.getMeasuredHeight()
                r11 = r0
                goto L_0x0045
            L_0x0023:
                if (r2 != r9) goto L_0x0044
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r2 = r8.groupPosition
                float r2 = r2.ph
                android.graphics.Point r3 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r3 = r3.x
                android.graphics.Point r4 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r4 = r4.y
                int r3 = java.lang.Math.max(r3, r4)
                float r3 = (float) r3
                float r2 = r2 * r3
                r3 = 1056964608(0x3f000000, float:0.5)
                float r2 = r2 * r3
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r1 = (int) r2
                r11 = r0
                goto L_0x0045
            L_0x0044:
                r11 = r0
            L_0x0045:
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r0 = r8.currentBlock
                if (r0 == 0) goto L_0x0317
                im.bclpbkiauv.ui.ArticleViewer r2 = im.bclpbkiauv.ui.ArticleViewer.this
                long r3 = r0.photo_id
                im.bclpbkiauv.tgnet.TLRPC$Photo r0 = r2.getPhotoWithId(r3)
                r8.currentPhoto = r0
                r0 = 1111490560(0x42400000, float:48.0)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                r0 = r11
                r2 = r1
                int r3 = r8.currentType
                r4 = 1099956224(0x41900000, float:18.0)
                if (r3 != 0) goto L_0x0083
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r3 = r8.currentBlock
                int r3 = r3.level
                if (r3 <= 0) goto L_0x0083
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r3 = r8.currentBlock
                int r3 = r3.level
                int r3 = r3 * 14
                float r3 = (float) r3
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                int r3 = r3 + r5
                r5 = r3
                r8.textX = r3
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                int r3 = r3 + r5
                int r0 = r0 - r3
                r3 = r0
                r13 = r3
                goto L_0x0093
            L_0x0083:
                r5 = 0
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                r8.textX = r3
                r3 = 1108344832(0x42100000, float:36.0)
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                int r3 = r11 - r3
                r13 = r3
            L_0x0093:
                im.bclpbkiauv.tgnet.TLRPC$Photo r3 = r8.currentPhoto
                r14 = 1090519040(0x41000000, float:8.0)
                r15 = 0
                if (r3 == 0) goto L_0x0255
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r4 = r8.currentPhotoObject
                if (r4 == 0) goto L_0x0255
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r3 = r3.sizes
                r4 = 40
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4, r10)
                r8.currentPhotoObjectThumb = r3
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r4 = r8.currentPhotoObject
                r6 = 0
                if (r4 != r3) goto L_0x00af
                r8.currentPhotoObjectThumb = r6
            L_0x00af:
                int r3 = r8.currentType
                r4 = 1073741824(0x40000000, float:2.0)
                if (r3 != 0) goto L_0x0113
                float r3 = (float) r0
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r8.currentPhotoObject
                int r7 = r7.w
                float r7 = (float) r7
                float r3 = r3 / r7
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r8.currentPhotoObject
                int r7 = r7.h
                float r7 = (float) r7
                float r7 = r7 * r3
                int r1 = (int) r7
                im.bclpbkiauv.tgnet.TLRPC$PageBlock r7 = r8.parentBlock
                boolean r7 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockCover
                if (r7 == 0) goto L_0x00cf
                int r1 = java.lang.Math.min(r1, r0)
                goto L_0x0111
            L_0x00cf:
                im.bclpbkiauv.ui.ArticleViewer r7 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.components.RecyclerListView[] r7 = r7.listView
                r7 = r7[r15]
                int r7 = r7.getMeasuredWidth()
                im.bclpbkiauv.ui.ArticleViewer r6 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.components.RecyclerListView[] r6 = r6.listView
                r6 = r6[r15]
                int r6 = r6.getMeasuredHeight()
                int r6 = java.lang.Math.max(r7, r6)
                r7 = 1113587712(0x42600000, float:56.0)
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                int r6 = r6 - r7
                float r6 = (float) r6
                r7 = 1063675494(0x3f666666, float:0.9)
                float r6 = r6 * r7
                int r6 = (int) r6
                if (r1 <= r6) goto L_0x0111
                r1 = r6
                float r7 = (float) r1
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r15 = r8.currentPhotoObject
                int r15 = r15.h
                float r15 = (float) r15
                float r3 = r7 / r15
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r8.currentPhotoObject
                int r7 = r7.w
                float r7 = (float) r7
                float r7 = r7 * r3
                int r0 = (int) r7
                int r7 = r11 - r5
                int r7 = r7 - r0
                int r7 = r7 / r9
                int r5 = r5 + r7
            L_0x0111:
                r2 = r1
                goto L_0x0147
            L_0x0113:
                if (r3 != r9) goto L_0x0147
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r3 = r8.groupPosition
                int r3 = r3.flags
                r3 = r3 & r9
                if (r3 != 0) goto L_0x0121
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                int r0 = r0 - r3
            L_0x0121:
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r3 = r8.groupPosition
                int r3 = r3.flags
                r3 = r3 & 8
                if (r3 != 0) goto L_0x012e
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                int r2 = r2 - r3
            L_0x012e:
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r3 = r8.groupPosition
                int r3 = r3.leftSpanOffset
                if (r3 == 0) goto L_0x0148
                im.bclpbkiauv.messenger.MessageObject$GroupedMessagePosition r3 = r8.groupPosition
                int r3 = r3.leftSpanOffset
                int r3 = r3 * r11
                float r3 = (float) r3
                r6 = 1148846080(0x447a0000, float:1000.0)
                float r3 = r3 / r6
                double r6 = (double) r3
                double r6 = java.lang.Math.ceil(r6)
                int r3 = (int) r6
                int r0 = r0 - r3
                int r5 = r5 + r3
                goto L_0x0148
            L_0x0147:
            L_0x0148:
                im.bclpbkiauv.messenger.ImageReceiver r3 = r8.imageView
                boolean r6 = r8.isFirst
                if (r6 != 0) goto L_0x0160
                int r6 = r8.currentType
                if (r6 == r10) goto L_0x0160
                if (r6 == r9) goto L_0x0160
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r6 = r8.currentBlock
                int r6 = r6.level
                if (r6 <= 0) goto L_0x015b
                goto L_0x0160
            L_0x015b:
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                goto L_0x0161
            L_0x0160:
                r6 = 0
            L_0x0161:
                r3.setImageCoords(r5, r6, r0, r2)
                int r3 = r8.currentType
                if (r3 != 0) goto L_0x016d
                r3 = 0
                r8.currentFilter = r3
                r15 = 0
                goto L_0x0186
            L_0x016d:
                java.util.Locale r3 = java.util.Locale.US
                java.lang.Object[] r6 = new java.lang.Object[r9]
                java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
                r15 = 0
                r6[r15] = r7
                java.lang.Integer r7 = java.lang.Integer.valueOf(r2)
                r6[r10] = r7
                java.lang.String r7 = "%d_%d"
                java.lang.String r3 = java.lang.String.format(r3, r7, r6)
                r8.currentFilter = r3
            L_0x0186:
                java.lang.String r3 = "80_80_b"
                r8.currentThumbFilter = r3
                im.bclpbkiauv.ui.ArticleViewer r3 = im.bclpbkiauv.ui.ArticleViewer.this
                int r3 = r3.currentAccount
                im.bclpbkiauv.messenger.DownloadController r3 = im.bclpbkiauv.messenger.DownloadController.getInstance(r3)
                int r3 = r3.getCurrentDownloadMask()
                r3 = r3 & r10
                if (r3 == 0) goto L_0x019d
                r3 = 1
                goto L_0x019e
            L_0x019d:
                r3 = 0
            L_0x019e:
                r8.autoDownload = r3
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = r8.currentPhotoObject
                java.io.File r3 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r3, r10)
                boolean r6 = r8.autoDownload
                if (r6 != 0) goto L_0x01e8
                boolean r6 = r3.exists()
                if (r6 == 0) goto L_0x01b1
                goto L_0x01e8
            L_0x01b1:
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r8.currentPhotoObject
                im.bclpbkiauv.tgnet.TLRPC$Photo r15 = r8.currentPhoto
                im.bclpbkiauv.messenger.ImageLocation r7 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r7, r15)
                r6.setStrippedLocation(r7)
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                r19 = 0
                java.lang.String r7 = r8.currentFilter
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r15 = r8.currentPhotoObjectThumb
                im.bclpbkiauv.tgnet.TLRPC$Photo r9 = r8.currentPhoto
                im.bclpbkiauv.messenger.ImageLocation r21 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r15, r9)
                java.lang.String r9 = r8.currentThumbFilter
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r15 = r8.currentPhotoObject
                int r15 = r15.size
                r24 = 0
                im.bclpbkiauv.ui.ArticleViewer r10 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$WebPage r25 = r10.currentPage
                r26 = 1
                r18 = r6
                r20 = r7
                r22 = r9
                r23 = r15
                r18.setImage(r19, r20, r21, r22, r23, r24, r25, r26)
                goto L_0x021d
            L_0x01e8:
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                r7 = 0
                r6.setStrippedLocation(r7)
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r8.currentPhotoObject
                im.bclpbkiauv.tgnet.TLRPC$Photo r9 = r8.currentPhoto
                im.bclpbkiauv.messenger.ImageLocation r19 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r7, r9)
                java.lang.String r7 = r8.currentFilter
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = r8.currentPhotoObjectThumb
                im.bclpbkiauv.tgnet.TLRPC$Photo r10 = r8.currentPhoto
                im.bclpbkiauv.messenger.ImageLocation r21 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r9, r10)
                java.lang.String r9 = r8.currentThumbFilter
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r10 = r8.currentPhotoObject
                int r10 = r10.size
                r24 = 0
                im.bclpbkiauv.ui.ArticleViewer r15 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$WebPage r25 = r15.currentPage
                r26 = 1
                r18 = r6
                r20 = r7
                r22 = r9
                r23 = r10
                r18.setImage(r19, r20, r21, r22, r23, r24, r25, r26)
            L_0x021d:
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                int r6 = r6.getImageX()
                float r6 = (float) r6
                im.bclpbkiauv.messenger.ImageReceiver r7 = r8.imageView
                int r7 = r7.getImageWidth()
                int r7 = r7 - r12
                float r7 = (float) r7
                float r7 = r7 / r4
                float r6 = r6 + r7
                int r6 = (int) r6
                r8.buttonX = r6
                im.bclpbkiauv.messenger.ImageReceiver r6 = r8.imageView
                int r6 = r6.getImageY()
                float r6 = (float) r6
                im.bclpbkiauv.messenger.ImageReceiver r7 = r8.imageView
                int r7 = r7.getImageHeight()
                int r7 = r7 - r12
                float r7 = (float) r7
                float r7 = r7 / r4
                float r6 = r6 + r7
                int r4 = (int) r6
                r8.buttonY = r4
                im.bclpbkiauv.ui.components.RadialProgress2 r6 = r8.radialProgress
                int r7 = r8.buttonX
                int r9 = r7 + r12
                int r10 = r4 + r12
                r6.setProgressRect(r7, r4, r9, r10)
                r9 = r0
                r7 = r1
                r10 = r2
                r15 = r5
                goto L_0x0259
            L_0x0255:
                r9 = r0
                r7 = r1
                r10 = r2
                r15 = r5
            L_0x0259:
                int r0 = r8.currentType
                if (r0 != 0) goto L_0x02c7
                im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                r2 = 0
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r1 = r8.currentBlock
                im.bclpbkiauv.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                im.bclpbkiauv.tgnet.TLRPC$RichText r3 = r1.text
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r5 = r8.currentBlock
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r6 = r8.parentAdapter
                r1 = r27
                r4 = r13
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6)
                r8.captionLayout = r0
                r16 = 1082130432(0x40800000, float:4.0)
                if (r0 == 0) goto L_0x028d
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r1 = r8.captionLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                r8.creditOffset = r0
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
                int r0 = r0 + r1
                int r7 = r7 + r0
                r18 = r7
                goto L_0x028f
            L_0x028d:
                r18 = r7
            L_0x028f:
                im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                r2 = 0
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r1 = r8.currentBlock
                im.bclpbkiauv.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                im.bclpbkiauv.tgnet.TLRPC$RichText r3 = r1.credit
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r5 = r8.currentBlock
                im.bclpbkiauv.ui.ArticleViewer r1 = im.bclpbkiauv.ui.ArticleViewer.this
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x02a7
                android.text.Layout$Alignment r1 = im.bclpbkiauv.ui.components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x02a9
            L_0x02a7:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x02a9:
                r6 = r1
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r7 = r8.parentAdapter
                r1 = r27
                r4 = r13
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText((android.view.View) r1, (java.lang.CharSequence) r2, (im.bclpbkiauv.tgnet.TLRPC.RichText) r3, (int) r4, (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r5, (android.text.Layout.Alignment) r6, (im.bclpbkiauv.ui.ArticleViewer.WebpageAdapter) r7)
                r8.creditLayout = r0
                if (r0 == 0) goto L_0x02c5
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r1 = r8.creditLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r7 = r18 + r0
                goto L_0x02c7
            L_0x02c5:
                r7 = r18
            L_0x02c7:
                boolean r0 = r8.isFirst
                if (r0 != 0) goto L_0x02da
                int r0 = r8.currentType
                if (r0 != 0) goto L_0x02da
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r0 = r8.currentBlock
                int r0 = r0.level
                if (r0 > 0) goto L_0x02da
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                int r7 = r7 + r0
            L_0x02da:
                im.bclpbkiauv.tgnet.TLRPC$PageBlock r0 = r8.parentBlock
                boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockCover
                if (r0 == 0) goto L_0x0306
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r0 = r8.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                if (r0 == 0) goto L_0x0306
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r0 = r8.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                int r0 = r0.size()
                r1 = 1
                if (r0 <= r1) goto L_0x0306
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r0 = r8.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                java.lang.Object r0 = r0.get(r1)
                boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockChannel
                if (r0 == 0) goto L_0x0306
                r17 = 1
                goto L_0x0308
            L_0x0306:
                r17 = 0
            L_0x0308:
                r0 = r17
                int r1 = r8.currentType
                r2 = 2
                if (r1 == r2) goto L_0x0316
                if (r0 != 0) goto L_0x0316
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                int r7 = r7 + r1
            L_0x0316:
                goto L_0x0318
            L_0x0317:
                r7 = 1
            L_0x0318:
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r0 = r8.channelCell
                r1 = r28
                r2 = r29
                r0.measure(r1, r2)
                im.bclpbkiauv.ui.ArticleViewer$BlockChannelCell r0 = r8.channelCell
                im.bclpbkiauv.messenger.ImageReceiver r3 = r8.imageView
                int r3 = r3.getImageHeight()
                r4 = 1109131264(0x421c0000, float:39.0)
                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                int r3 = r3 - r4
                float r3 = (float) r3
                r0.setTranslationY(r3)
                r8.setMeasuredDimension(r11, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.BlockPhotoCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (!this.imageView.hasBitmapImage() || this.imageView.getCurrentAlpha() != 1.0f) {
                    canvas.drawRect((float) this.imageView.getImageX(), (float) this.imageView.getImageY(), (float) this.imageView.getImageX2(), (float) this.imageView.getImageY2(), ArticleViewer.photoBackgroundPaint);
                }
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                if (!TextUtils.isEmpty(this.currentBlock.url)) {
                    int x = getMeasuredWidth() - AndroidUtilities.dp(35.0f);
                    int y = this.imageView.getImageY() + AndroidUtilities.dp(11.0f);
                    this.linkDrawable.setBounds(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                    this.linkDrawable.draw(canvas);
                }
                this.textY = this.imageView.getImageY() + this.imageView.getImageHeight() + AndroidUtilities.dp(8.0f);
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private int getIconForCurrentState() {
            int i = this.buttonState;
            if (i == 0) {
                return 2;
            }
            if (i == 1) {
                return 3;
            }
            return 4;
        }

        private void didPressedButton(boolean animated) {
            int i = this.buttonState;
            if (i == 0) {
                this.cancelLoading = false;
                this.radialProgress.setProgress(0.0f, animated);
                this.imageView.setImage(ImageLocation.getForPhoto(this.currentPhotoObject, this.currentPhoto), this.currentFilter, ImageLocation.getForPhoto(this.currentPhotoObjectThumb, this.currentPhoto), this.currentThumbFilter, this.currentPhotoObject.size, (String) null, ArticleViewer.this.currentPage, 1);
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                invalidate();
            } else if (i == 1) {
                this.cancelLoading = true;
                this.imageView.cancelLoadImage();
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            }
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
            boolean fileExists = FileLoader.getPathToAttach(this.currentPhotoObject, true).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setIcon(4, false, false);
            } else if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, (MessageObject) null, this);
                float setProgress = 0.0f;
                if (this.autoDownload || FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 1;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                } else {
                    this.buttonState = 0;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                this.radialProgress.setProgress(setProgress, false);
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageView.onAttachedToWindow();
            updateButtonState(false);
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, true);
            if (this.buttonState != 1) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            info.setText(sb.toString());
        }
    }

    private class BlockMapCell extends FrameLayout {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockMap currentBlock;
        private int currentMapProvider;
        private int currentType;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private boolean isLast;
        private WebpageAdapter parentAdapter;
        private boolean photoPressed;
        private int textX;
        private int textY;

        public BlockMapCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.currentType = type;
        }

        public void setBlock(TLRPC.TL_pageBlockMap block, boolean first, boolean last) {
            this.currentBlock = block;
            this.isFirst = first;
            this.isLast = last;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                this.photoPressed = true;
            } else if (event.getAction() == 1 && this.photoPressed) {
                this.photoPressed = false;
                try {
                    double lat = this.currentBlock.geo.lat;
                    double lon = this.currentBlock.geo._long;
                    Activity access$2500 = ArticleViewer.this.parentActivity;
                    access$2500.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (event.getAction() == 3) {
                this.photoPressed = false;
            }
            if (!this.photoPressed) {
                if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                    if (ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event)) {
                        return true;
                    }
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x00cc, code lost:
            r1 = r8.currentType;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r25, int r26) {
            /*
                r24 = this;
                r8 = r24
                int r0 = android.view.View.MeasureSpec.getSize(r25)
                r1 = 0
                int r2 = r8.currentType
                r3 = 1
                r9 = 2
                if (r2 != r3) goto L_0x0023
                android.view.ViewParent r2 = r24.getParent()
                android.view.View r2 = (android.view.View) r2
                int r0 = r2.getMeasuredWidth()
                android.view.ViewParent r2 = r24.getParent()
                android.view.View r2 = (android.view.View) r2
                int r1 = r2.getMeasuredHeight()
                r10 = r0
                goto L_0x0029
            L_0x0023:
                if (r2 != r9) goto L_0x0028
                r1 = r0
                r10 = r0
                goto L_0x0029
            L_0x0028:
                r10 = r0
            L_0x0029:
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r0 = r8.currentBlock
                if (r0 == 0) goto L_0x0201
                r2 = r10
                int r4 = r8.currentType
                r5 = 1099956224(0x41900000, float:18.0)
                if (r4 != 0) goto L_0x0054
                int r0 = r0.level
                if (r0 <= 0) goto L_0x0054
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r0 = r8.currentBlock
                int r0 = r0.level
                int r0 = r0 * 14
                float r0 = (float) r0
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                int r0 = r0 + r4
                r4 = r0
                r8.textX = r0
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                int r0 = r0 + r4
                int r2 = r2 - r0
                r0 = r2
                r11 = r0
                goto L_0x0064
            L_0x0054:
                r4 = 0
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                r8.textX = r0
                r0 = 1108344832(0x42100000, float:36.0)
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                int r0 = r10 - r0
                r11 = r0
            L_0x0064:
                int r0 = r8.currentType
                r5 = 0
                if (r0 != 0) goto L_0x00c1
                float r0 = (float) r2
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r6 = r8.currentBlock
                int r6 = r6.w
                float r6 = (float) r6
                float r0 = r0 / r6
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r6 = r8.currentBlock
                int r6 = r6.h
                float r6 = (float) r6
                float r6 = r6 * r0
                int r1 = (int) r6
                im.bclpbkiauv.ui.ArticleViewer r6 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.components.RecyclerListView[] r6 = r6.listView
                r6 = r6[r5]
                int r6 = r6.getMeasuredWidth()
                im.bclpbkiauv.ui.ArticleViewer r7 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.ui.components.RecyclerListView[] r7 = r7.listView
                r7 = r7[r5]
                int r7 = r7.getMeasuredHeight()
                int r6 = java.lang.Math.max(r6, r7)
                r7 = 1113587712(0x42600000, float:56.0)
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                int r6 = r6 - r7
                float r6 = (float) r6
                r7 = 1063675494(0x3f666666, float:0.9)
                float r6 = r6 * r7
                int r6 = (int) r6
                if (r1 <= r6) goto L_0x00bd
                r1 = r6
                float r7 = (float) r1
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r12 = r8.currentBlock
                int r12 = r12.h
                float r12 = (float) r12
                float r7 = r7 / r12
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r0 = r8.currentBlock
                int r0 = r0.w
                float r0 = (float) r0
                float r0 = r0 * r7
                int r2 = (int) r0
                int r0 = r10 - r4
                int r0 = r0 - r2
                int r0 = r0 / r9
                int r4 = r4 + r0
                r7 = r1
                r12 = r2
                r13 = r4
                goto L_0x00c4
            L_0x00bd:
                r7 = r1
                r12 = r2
                r13 = r4
                goto L_0x00c4
            L_0x00c1:
                r7 = r1
                r12 = r2
                r13 = r4
            L_0x00c4:
                im.bclpbkiauv.messenger.ImageReceiver r0 = r8.imageView
                boolean r1 = r8.isFirst
                r14 = 1090519040(0x41000000, float:8.0)
                if (r1 != 0) goto L_0x00de
                int r1 = r8.currentType
                if (r1 == r3) goto L_0x00de
                if (r1 == r9) goto L_0x00de
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r1 = r8.currentBlock
                int r1 = r1.level
                if (r1 <= 0) goto L_0x00d9
                goto L_0x00de
            L_0x00d9:
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                goto L_0x00df
            L_0x00de:
                r1 = 0
            L_0x00df:
                r0.setImageCoords(r13, r1, r12, r7)
                im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                int r15 = r0.currentAccount
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r0 = r8.currentBlock
                im.bclpbkiauv.tgnet.TLRPC$GeoPoint r0 = r0.geo
                double r0 = r0.lat
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r2 = r8.currentBlock
                im.bclpbkiauv.tgnet.TLRPC$GeoPoint r2 = r2.geo
                double r2 = r2._long
                float r4 = (float) r12
                float r6 = im.bclpbkiauv.messenger.AndroidUtilities.density
                float r4 = r4 / r6
                int r4 = (int) r4
                float r6 = (float) r7
                float r16 = im.bclpbkiauv.messenger.AndroidUtilities.density
                float r6 = r6 / r16
                int r6 = (int) r6
                r22 = 1
                r23 = 15
                r16 = r0
                r18 = r2
                r20 = r4
                r21 = r6
                java.lang.String r15 = im.bclpbkiauv.messenger.AndroidUtilities.formapMapUrl(r15, r16, r18, r20, r21, r22, r23)
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r0 = r8.currentBlock
                im.bclpbkiauv.tgnet.TLRPC$GeoPoint r0 = r0.geo
                float r1 = (float) r12
                float r2 = im.bclpbkiauv.messenger.AndroidUtilities.density
                float r1 = r1 / r2
                int r1 = (int) r1
                float r2 = (float) r7
                float r3 = im.bclpbkiauv.messenger.AndroidUtilities.density
                float r2 = r2 / r3
                int r2 = (int) r2
                r3 = 15
                float r4 = im.bclpbkiauv.messenger.AndroidUtilities.density
                r23 = r15
                double r14 = (double) r4
                double r14 = java.lang.Math.ceil(r14)
                int r4 = (int) r14
                int r4 = java.lang.Math.min(r9, r4)
                im.bclpbkiauv.messenger.WebFile r14 = im.bclpbkiauv.messenger.WebFile.createWithGeoPoint(r0, r1, r2, r3, r4)
                im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                int r0 = r0.currentAccount
                im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
                int r0 = r0.mapProvider
                r8.currentMapProvider = r0
                if (r0 != r9) goto L_0x015d
                if (r14 == 0) goto L_0x0172
                im.bclpbkiauv.messenger.ImageReceiver r15 = r8.imageView
                im.bclpbkiauv.messenger.ImageLocation r16 = im.bclpbkiauv.messenger.ImageLocation.getForWebFile(r14)
                r17 = 0
                android.graphics.drawable.Drawable[] r0 = im.bclpbkiauv.ui.actionbar.Theme.chat_locationDrawable
                r18 = r0[r5]
                r19 = 0
                im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                im.bclpbkiauv.tgnet.TLRPC$WebPage r20 = r0.currentPage
                r21 = 0
                r15.setImage(r16, r17, r18, r19, r20, r21)
                goto L_0x0172
            L_0x015d:
                if (r23 == 0) goto L_0x0172
                im.bclpbkiauv.messenger.ImageReceiver r0 = r8.imageView
                r18 = 0
                android.graphics.drawable.Drawable[] r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_locationDrawable
                r19 = r1[r5]
                r20 = 0
                r21 = 0
                r16 = r0
                r17 = r23
                r16.setImage(r17, r18, r19, r20, r21)
            L_0x0172:
                int r0 = r8.currentType
                if (r0 != 0) goto L_0x01e0
                im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                r2 = 0
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r1 = r8.currentBlock
                im.bclpbkiauv.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                im.bclpbkiauv.tgnet.TLRPC$RichText r3 = r1.text
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r5 = r8.currentBlock
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r6 = r8.parentAdapter
                r1 = r24
                r4 = r11
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6)
                r8.captionLayout = r0
                r15 = 1082130432(0x40800000, float:4.0)
                if (r0 == 0) goto L_0x01a6
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r1 = r8.captionLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                r8.creditOffset = r0
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
                int r0 = r0 + r1
                int r7 = r7 + r0
                r16 = r7
                goto L_0x01a8
            L_0x01a6:
                r16 = r7
            L_0x01a8:
                im.bclpbkiauv.ui.ArticleViewer r0 = im.bclpbkiauv.ui.ArticleViewer.this
                r2 = 0
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r1 = r8.currentBlock
                im.bclpbkiauv.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                im.bclpbkiauv.tgnet.TLRPC$RichText r3 = r1.credit
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r5 = r8.currentBlock
                im.bclpbkiauv.ui.ArticleViewer r1 = im.bclpbkiauv.ui.ArticleViewer.this
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x01c0
                android.text.Layout$Alignment r1 = im.bclpbkiauv.ui.components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x01c2
            L_0x01c0:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x01c2:
                r6 = r1
                im.bclpbkiauv.ui.ArticleViewer$WebpageAdapter r7 = r8.parentAdapter
                r1 = r24
                r4 = r11
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText((android.view.View) r1, (java.lang.CharSequence) r2, (im.bclpbkiauv.tgnet.TLRPC.RichText) r3, (int) r4, (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r5, (android.text.Layout.Alignment) r6, (im.bclpbkiauv.ui.ArticleViewer.WebpageAdapter) r7)
                r8.creditLayout = r0
                if (r0 == 0) goto L_0x01de
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
                im.bclpbkiauv.ui.ArticleViewer$DrawingText r1 = r8.creditLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r7 = r16 + r0
                goto L_0x01e0
            L_0x01de:
                r7 = r16
            L_0x01e0:
                boolean r0 = r8.isFirst
                if (r0 != 0) goto L_0x01f5
                int r0 = r8.currentType
                if (r0 != 0) goto L_0x01f5
                im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockMap r0 = r8.currentBlock
                int r0 = r0.level
                if (r0 > 0) goto L_0x01f5
                r0 = 1090519040(0x41000000, float:8.0)
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                int r7 = r7 + r1
            L_0x01f5:
                int r0 = r8.currentType
                if (r0 == r9) goto L_0x0200
                r0 = 1090519040(0x41000000, float:8.0)
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                int r7 = r7 + r0
            L_0x0200:
                goto L_0x0202
            L_0x0201:
                r7 = 1
            L_0x0202:
                r8.setMeasuredDimension(r10, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.BlockMapCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.imageView.draw(canvas);
                if (this.currentMapProvider == 2 && this.imageView.hasNotThumb()) {
                    int w = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicWidth()) * 0.8f);
                    int h = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicHeight()) * 0.8f);
                    int x = this.imageView.getImageX() + ((this.imageView.getImageWidth() - w) / 2);
                    int y = this.imageView.getImageY() + ((this.imageView.getImageHeight() / 2) - h);
                    Theme.chat_redLocationIcon.setAlpha((int) (this.imageView.getCurrentAlpha() * 255.0f));
                    Theme.chat_redLocationIcon.setBounds(x, y, x + w, y + h);
                    Theme.chat_redLocationIcon.draw(canvas);
                }
                this.textY = this.imageView.getImageY() + this.imageView.getImageHeight() + AndroidUtilities.dp(8.0f);
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("Map", R.string.Map));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            info.setText(sb.toString());
        }
    }

    private class BlockChannelCell extends FrameLayout {
        private Paint backgroundPaint;
        private int buttonWidth;
        private AnimatorSet currentAnimation;
        private TLRPC.TL_pageBlockChannel currentBlock;
        private int currentState;
        private int currentType;
        private ImageView imageView;
        private WebpageAdapter parentAdapter;
        private ContextProgressView progressView;
        private DrawingText textLayout;
        private TextView textView;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textX2;
        private int textY = AndroidUtilities.dp(11.0f);

        public BlockChannelCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.backgroundPaint = new Paint();
            this.currentType = type;
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setText(LocaleController.getString("ChannelJoin", R.string.ChannelJoin));
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-2, 39, 53));
            this.textView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ArticleViewer.BlockChannelCell.this.lambda$new$0$ArticleViewer$BlockChannelCell(view);
                }
            });
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageResource(R.drawable.list_check);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(39, 39, 53));
            ContextProgressView contextProgressView = new ContextProgressView(context, 0);
            this.progressView = contextProgressView;
            addView(contextProgressView, LayoutHelper.createFrame(39, 39, 53));
        }

        public /* synthetic */ void lambda$new$0$ArticleViewer$BlockChannelCell(View v) {
            if (this.currentState == 0) {
                setState(1, true);
                ArticleViewer articleViewer = ArticleViewer.this;
                articleViewer.joinChannel(this, articleViewer.loadedChannel);
            }
        }

        public void setBlock(TLRPC.TL_pageBlockChannel block) {
            this.currentBlock = block;
            int color = ArticleViewer.this.getSelectedColor();
            if (this.currentType == 0) {
                this.textView.setTextColor(-14840360);
                if (color == 0) {
                    this.backgroundPaint.setColor(-526345);
                } else if (color == 1) {
                    this.backgroundPaint.setColor(-1712440);
                } else if (color == 2) {
                    this.backgroundPaint.setColor(-15000805);
                }
                this.imageView.setColorFilter(new PorterDuffColorFilter(-6710887, PorterDuff.Mode.MULTIPLY));
            } else {
                this.textView.setTextColor(-1);
                this.backgroundPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            }
            TLRPC.Chat channel = MessagesController.getInstance(ArticleViewer.this.currentAccount).getChat(Integer.valueOf(block.channel.id));
            if (channel == null || channel.min) {
                ArticleViewer.this.loadChannel(this, this.parentAdapter, block.channel);
                setState(1, false);
            } else {
                TLRPC.Chat unused = ArticleViewer.this.loadedChannel = channel;
                if (!channel.left || channel.kicked) {
                    setState(4, false);
                } else {
                    setState(0, false);
                }
            }
            requestLayout();
        }

        public void setState(int state, boolean animated) {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.currentState = state;
            float f = 0.0f;
            float f2 = 0.1f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[9];
                TextView textView2 = this.textView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = state == 0 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView2, property, fArr);
                TextView textView3 = this.textView;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = state == 0 ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(textView3, property2, fArr2);
                TextView textView4 = this.textView;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                fArr3[0] = state == 0 ? 1.0f : 0.1f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView4, property3, fArr3);
                ContextProgressView contextProgressView = this.progressView;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = state == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(contextProgressView, property4, fArr4);
                ContextProgressView contextProgressView2 = this.progressView;
                Property property5 = View.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = state == 1 ? 1.0f : 0.1f;
                animatorArr[4] = ObjectAnimator.ofFloat(contextProgressView2, property5, fArr5);
                ContextProgressView contextProgressView3 = this.progressView;
                Property property6 = View.SCALE_Y;
                float[] fArr6 = new float[1];
                fArr6[0] = state == 1 ? 1.0f : 0.1f;
                animatorArr[5] = ObjectAnimator.ofFloat(contextProgressView3, property6, fArr6);
                ImageView imageView2 = this.imageView;
                Property property7 = View.ALPHA;
                float[] fArr7 = new float[1];
                if (state == 2) {
                    f = 1.0f;
                }
                fArr7[0] = f;
                animatorArr[6] = ObjectAnimator.ofFloat(imageView2, property7, fArr7);
                ImageView imageView3 = this.imageView;
                Property property8 = View.SCALE_X;
                float[] fArr8 = new float[1];
                fArr8[0] = state == 2 ? 1.0f : 0.1f;
                animatorArr[7] = ObjectAnimator.ofFloat(imageView3, property8, fArr8);
                ImageView imageView4 = this.imageView;
                Property property9 = View.SCALE_Y;
                float[] fArr9 = new float[1];
                if (state == 2) {
                    f2 = 1.0f;
                }
                fArr9[0] = f2;
                animatorArr[8] = ObjectAnimator.ofFloat(imageView4, property9, fArr9);
                animatorSet2.playTogether(animatorArr);
                this.currentAnimation.setDuration(150);
                this.currentAnimation.start();
                return;
            }
            this.textView.setAlpha(state == 0 ? 1.0f : 0.0f);
            this.textView.setScaleX(state == 0 ? 1.0f : 0.1f);
            this.textView.setScaleY(state == 0 ? 1.0f : 0.1f);
            this.progressView.setAlpha(state == 1 ? 1.0f : 0.0f);
            this.progressView.setScaleX(state == 1 ? 1.0f : 0.1f);
            this.progressView.setScaleY(state == 1 ? 1.0f : 0.1f);
            ImageView imageView5 = this.imageView;
            if (state == 2) {
                f = 1.0f;
            }
            imageView5.setAlpha(f);
            this.imageView.setScaleX(state == 2 ? 1.0f : 0.1f);
            ImageView imageView6 = this.imageView;
            if (state == 2) {
                f2 = 1.0f;
            }
            imageView6.setScaleY(f2);
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (this.currentType != 0) {
                return super.onTouchEvent(event);
            }
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, AndroidUtilities.dp(48.0f));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), 1073741824));
            this.buttonWidth = this.textView.getMeasuredWidth();
            this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), 1073741824));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), 1073741824));
            TLRPC.TL_pageBlockChannel tL_pageBlockChannel = this.currentBlock;
            if (tL_pageBlockChannel != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) tL_pageBlockChannel.channel.title, (TLRPC.RichText) null, (width - AndroidUtilities.dp(52.0f)) - this.buttonWidth, (TLRPC.PageBlock) this.currentBlock, StaticLayoutEx.ALIGN_LEFT(), this.parentAdapter);
                if (ArticleViewer.this.isRtl) {
                    this.textX2 = this.textX;
                } else {
                    this.textX2 = (getMeasuredWidth() - this.textX) - this.buttonWidth;
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.imageView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, this.textX2 + (this.buttonWidth / 2) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            this.progressView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, this.textX2 + (this.buttonWidth / 2) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            TextView textView2 = this.textView;
            int i = this.textX2;
            textView2.layout(i, 0, textView2.getMeasuredWidth() + i, this.textView.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(39.0f), this.backgroundPaint);
                DrawingText drawingText = this.textLayout;
                if (drawingText != null && drawingText.getLineCount() > 0) {
                    canvas.save();
                    if (ArticleViewer.this.isRtl) {
                        canvas.translate((((float) getMeasuredWidth()) - this.textLayout.getLineWidth(0)) - ((float) this.textX), (float) this.textY);
                    } else {
                        canvas.translate((float) this.textX, (float) this.textY);
                    }
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockAuthorDateCell extends View {
        private TLRPC.TL_pageBlockAuthorDate currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockAuthorDateCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockAuthorDate block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            MetricAffectingSpan[] spans;
            Spannable spannableAuthor;
            CharSequence text;
            CharSequence text2;
            int idx;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockAuthorDate tL_pageBlockAuthorDate = this.currentBlock;
            if (tL_pageBlockAuthorDate != null) {
                CharSequence author = ArticleViewer.this.getText(this, tL_pageBlockAuthorDate.author, this.currentBlock.author, this.currentBlock, width);
                if (author instanceof Spannable) {
                    Spannable spannableAuthor2 = (Spannable) author;
                    spannableAuthor = spannableAuthor2;
                    spans = (MetricAffectingSpan[]) spannableAuthor2.getSpans(0, author.length(), MetricAffectingSpan.class);
                } else {
                    spannableAuthor = null;
                    spans = null;
                }
                if (this.currentBlock.published_date != 0 && !TextUtils.isEmpty(author)) {
                    text = LocaleController.formatString("ArticleDateByAuthor", R.string.ArticleDateByAuthor, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000), author);
                } else if (!TextUtils.isEmpty(author)) {
                    text = LocaleController.formatString("ArticleByAuthor", R.string.ArticleByAuthor, author);
                } else {
                    text = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000);
                }
                if (spans != null) {
                    try {
                        if (spans.length > 0 && (idx = TextUtils.indexOf(text, author)) != -1) {
                            Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
                            text = spannable;
                            for (int a = 0; a < spans.length; a++) {
                                spannable.setSpan(spans[a], spannableAuthor.getSpanStart(spans[a]) + idx, spannableAuthor.getSpanEnd(spans[a]) + idx, 33);
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        text2 = text;
                    }
                }
                text2 = text;
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, text2, (TLRPC.RichText) null, width - AndroidUtilities.dp(36.0f), this.currentBlock, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    if (ArticleViewer.this.isRtl) {
                        this.textX = (int) Math.floor((double) ((((float) width) - this.textLayout.getLineWidth(0)) - ((float) AndroidUtilities.dp(16.0f))));
                    } else {
                        this.textX = AndroidUtilities.dp(18.0f);
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                info.setText(drawingText.getText());
            }
        }
    }

    private class BlockTitleCell extends View {
        private TLRPC.TL_pageBlockTitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockTitleCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockTitle block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockTitle tL_pageBlockTitle = this.currentBlock;
            if (tL_pageBlockTitle != null) {
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, tL_pageBlockTitle.text, width - AndroidUtilities.dp(36.0f), (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13700;
                if (access$13700 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                }
                if (this.currentBlock.first) {
                    height += AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout != null) {
                info.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVTitle", R.string.AccDescrIVTitle));
            }
        }
    }

    private class BlockKickerCell extends View {
        private TLRPC.TL_pageBlockKicker currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockKickerCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockKicker block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockKicker tL_pageBlockKicker = this.currentBlock;
            if (tL_pageBlockKicker != null) {
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, tL_pageBlockKicker.text, width - AndroidUtilities.dp(36.0f), (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13700;
                if (access$13700 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                }
                if (this.currentBlock.first) {
                    height += AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockFooterCell extends View {
        private TLRPC.TL_pageBlockFooter currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockFooterCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockFooter block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockFooter tL_pageBlockFooter = this.currentBlock;
            if (tL_pageBlockFooter != null) {
                if (tL_pageBlockFooter.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18));
                }
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, this.currentBlock.text, (width - AndroidUtilities.dp(18.0f)) - this.textX, (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13700;
                if (access$13700 != null) {
                    int height2 = access$13700.getHeight();
                    if (this.currentBlock.level > 0) {
                        height = height2 + AndroidUtilities.dp(8.0f);
                    } else {
                        height = height2 + AndroidUtilities.dp(16.0f);
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockPreformattedCell extends FrameLayout {
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockPreformatted currentBlock;
        /* access modifiers changed from: private */
        public WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        /* access modifiers changed from: private */
        public View textContainer;
        /* access modifiers changed from: private */
        public DrawingText textLayout;

        public BlockPreformattedCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            AnonymousClass1 r0 = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (BlockPreformattedCell.this.textContainer.getMeasuredWidth() > getMeasuredWidth()) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                /* access modifiers changed from: protected */
                public void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        DrawingText unused = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused2 = ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                }
            };
            this.scrollView = r0;
            r0.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            this.textContainer = new View(context, ArticleViewer.this) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int height = 0;
                    int width = 1;
                    if (BlockPreformattedCell.this.currentBlock != null) {
                        BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                        DrawingText unused = blockPreformattedCell.textLayout = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, BlockPreformattedCell.this.currentBlock.text, AndroidUtilities.dp(5000.0f), BlockPreformattedCell.this.currentBlock, BlockPreformattedCell.this.parentAdapter);
                        if (BlockPreformattedCell.this.textLayout != null) {
                            height = 0 + BlockPreformattedCell.this.textLayout.getHeight();
                            int count = BlockPreformattedCell.this.textLayout.getLineCount();
                            for (int a = 0; a < count; a++) {
                                width = Math.max((int) Math.ceil((double) BlockPreformattedCell.this.textLayout.getLineWidth(a)), width);
                            }
                        }
                    } else {
                        height = 1;
                    }
                    setMeasuredDimension(AndroidUtilities.dp(32.0f) + width, height);
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (BlockPreformattedCell.this.textLayout != null) {
                        canvas.save();
                        BlockPreformattedCell.this.textLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -1);
            int dp = AndroidUtilities.dp(16.0f);
            layoutParams.rightMargin = dp;
            layoutParams.leftMargin = dp;
            int dp2 = AndroidUtilities.dp(12.0f);
            layoutParams.bottomMargin = dp2;
            layoutParams.topMargin = dp2;
            this.scrollView.addView(this.textContainer, layoutParams);
            setWillNotDraw(false);
        }

        public void setBlock(TLRPC.TL_pageBlockPreformatted block) {
            this.currentBlock = block;
            this.scrollView.setScrollX(0);
            this.textContainer.requestLayout();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
            setMeasuredDimension(width, this.scrollView.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, (float) AndroidUtilities.dp(8.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(8.0f)), ArticleViewer.preformattedBackgroundPaint);
            }
        }
    }

    private class BlockSubheaderCell extends View {
        private TLRPC.TL_pageBlockSubheader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubheaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockSubheader block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockSubheader tL_pageBlockSubheader = this.currentBlock;
            if (tL_pageBlockSubheader != null) {
                DrawingText access$13700 = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) null, tL_pageBlockSubheader.text, width - AndroidUtilities.dp(36.0f), (TLRPC.PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13700;
                if (access$13700 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout != null) {
                info.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVHeading", R.string.AccDescrIVHeading));
            }
        }
    }

    private class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    private class PhotoBackgroundDrawable extends ColorDrawable {
        /* access modifiers changed from: private */
        public Runnable drawRunnable;

        public PhotoBackgroundDrawable(int color) {
            super(color);
        }

        public void setAlpha(int alpha) {
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(!ArticleViewer.this.isPhotoVisible || alpha != 255);
            }
            super.setAlpha(alpha);
        }

        public void draw(Canvas canvas) {
            Runnable runnable;
            super.draw(canvas);
            if (getAlpha() != 0 && (runnable = this.drawRunnable) != null) {
                runnable.run();
                this.drawRunnable = null;
            }
        }
    }

    private class RadialProgressView {
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
        private int previousBackgroundState = -2;
        private RectF progressRect = new RectF();
        private float radOffset = 0.0f;
        private float scale = 1.0f;
        private int size = AndroidUtilities.dp(64.0f);

        public RadialProgressView(Context context, View parentView) {
            if (ArticleViewer.decelerateInterpolator == null) {
                DecelerateInterpolator unused = ArticleViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                Paint unused2 = ArticleViewer.progressPaint = new Paint(1);
                ArticleViewer.progressPaint.setStyle(Paint.Style.STROKE);
                ArticleViewer.progressPaint.setStrokeCap(Paint.Cap.ROUND);
                ArticleViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                ArticleViewer.progressPaint.setColor(-1);
            }
            this.parent = parentView;
        }

        private void updateAnimation() {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.animatedProgressValue != 1.0f) {
                this.radOffset += ((float) (360 * dt)) / 3000.0f;
                float f = this.currentProgress;
                float f2 = this.animationProgressStart;
                float progressDiff = f - f2;
                if (progressDiff > 0.0f) {
                    long j = this.currentProgressTime + dt;
                    this.currentProgressTime = j;
                    if (j >= 300) {
                        this.animatedProgressValue = f;
                        this.animationProgressStart = f;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = f2 + (ArticleViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f) * progressDiff);
                    }
                }
                this.parent.invalidate();
            }
            if (this.animatedProgressValue >= 1.0f && this.previousBackgroundState != -2) {
                float f3 = this.animatedAlphaValue - (((float) dt) / 200.0f);
                this.animatedAlphaValue = f3;
                if (f3 <= 0.0f) {
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
        }

        public void setBackgroundState(int state, boolean animated) {
            int i;
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
            int x = (ArticleViewer.this.getContainerViewWidth() - sizeScaled) / 2;
            int y = (ArticleViewer.this.getContainerViewHeight() - sizeScaled) / 2;
            int i2 = this.previousBackgroundState;
            if (i2 >= 0 && i2 < 4 && (drawable2 = ArticleViewer.progressDrawables[this.previousBackgroundState]) != null) {
                drawable2.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.alpha));
                drawable2.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                drawable2.draw(canvas);
            }
            int i3 = this.backgroundState;
            if (i3 >= 0 && i3 < 4 && (drawable = ArticleViewer.progressDrawables[this.backgroundState]) != null) {
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
                    ArticleViewer.progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.alpha));
                } else {
                    ArticleViewer.progressPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                this.progressRect.set((float) (x + diff), (float) (y + diff), (float) ((x + sizeScaled) - diff), (float) ((y + sizeScaled) - diff));
                canvas.drawArc(this.progressRect, -90.0f + this.radOffset, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, ArticleViewer.progressPaint);
                updateAnimation();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onSharePressed() {
        if (this.parentActivity != null && this.currentMedia != null) {
            try {
                File f = getMediaFile(this.currentIndex);
                if (f == null || !f.exists()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                    builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                    showDialog(builder.create());
                    return;
                }
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(getMediaMime(this.currentIndex));
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
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    private void setScaleToFill() {
        float bitmapWidth = (float) this.centerImage.getBitmapWidth();
        float containerWidth = (float) getContainerViewWidth();
        float bitmapHeight = (float) this.centerImage.getBitmapHeight();
        float containerHeight = (float) getContainerViewHeight();
        float scaleFit = Math.min(containerHeight / bitmapHeight, containerWidth / bitmapWidth);
        float max = Math.max(containerWidth / ((float) ((int) (bitmapWidth * scaleFit))), containerHeight / ((float) ((int) (bitmapHeight * scaleFit))));
        this.scale = max;
        updateMinMax(max);
    }

    /* access modifiers changed from: private */
    public void updateVideoPlayerTime() {
        String newText;
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 == null) {
            newText = String.format("%02d:%02d / %02d:%02d", new Object[]{0, 0, 0, 0});
        } else {
            long current = videoPlayer2.getCurrentPosition() / 1000;
            long total = this.videoPlayer.getDuration() / 1000;
            if (total == C.TIME_UNSET || current == C.TIME_UNSET) {
                newText = String.format("%02d:%02d / %02d:%02d", new Object[]{0, 0, 0, 0});
            } else {
                newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(current / 60), Long.valueOf(current % 60), Long.valueOf(total / 60), Long.valueOf(total % 60)});
            }
        }
        if (!TextUtils.equals(this.videoPlayerTime.getText(), newText)) {
            this.videoPlayerTime.setText(newText);
        }
    }

    private void preparePlayer(File file, boolean playWhenReady) {
        long duration;
        if (this.parentActivity != null) {
            releasePlayer();
            if (this.videoTextureView == null) {
                AspectRatioFrameLayout aspectRatioFrameLayout2 = new AspectRatioFrameLayout(this.parentActivity);
                this.aspectRatioFrameLayout = aspectRatioFrameLayout2;
                aspectRatioFrameLayout2.setVisibility(4);
                this.photoContainerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                TextureView textureView = new TextureView(this.parentActivity);
                this.videoTextureView = textureView;
                textureView.setOpaque(false);
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView2 = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView2.setAlpha(0.0f);
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            if (this.videoPlayer == null) {
                VideoPlayer videoPlayer2 = new VideoPlayer();
                this.videoPlayer = videoPlayer2;
                videoPlayer2.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (ArticleViewer.this.videoPlayer != null) {
                            if (playbackState == 4 || playbackState == 1) {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            } else {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                                } catch (Exception e2) {
                                    FileLog.e((Throwable) e2);
                                }
                            }
                            if (playbackState == 3 && ArticleViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                ArticleViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!ArticleViewer.this.videoPlayer.isPlaying() || playbackState == 4) {
                                if (ArticleViewer.this.isPlaying) {
                                    boolean unused = ArticleViewer.this.isPlaying = false;
                                    ArticleViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
                                    AndroidUtilities.cancelRunOnUIThread(ArticleViewer.this.updateProgressRunnable);
                                    if (playbackState == 4 && !ArticleViewer.this.videoPlayerSeekbar.isDragging()) {
                                        ArticleViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                                        ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
                                        ArticleViewer.this.videoPlayer.seekTo(0);
                                        ArticleViewer.this.videoPlayer.pause();
                                    }
                                }
                            } else if (!ArticleViewer.this.isPlaying) {
                                boolean unused2 = ArticleViewer.this.isPlaying = true;
                                ArticleViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_pause);
                                AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable);
                            }
                            ArticleViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onError(Exception e) {
                        FileLog.e((Throwable) e);
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        if (ArticleViewer.this.aspectRatioFrameLayout != null) {
                            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                                int temp = width;
                                width = height;
                                height = temp;
                            }
                            ArticleViewer.this.aspectRatioFrameLayout.setAspectRatio(height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height), unappliedRotationDegrees);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (!ArticleViewer.this.textureUploaded) {
                            boolean unused = ArticleViewer.this.textureUploaded = true;
                            ArticleViewer.this.containerView.invalidate();
                        }
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }
                });
                VideoPlayer videoPlayer3 = this.videoPlayer;
                if (videoPlayer3 != null) {
                    duration = videoPlayer3.getDuration();
                    if (duration == C.TIME_UNSET) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                long duration2 = duration / 1000;
                Math.ceil((double) this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(duration2 / 60), Long.valueOf(duration2 % 60), Long.valueOf(duration2 / 60), Long.valueOf(duration2 % 60)})));
            }
            this.videoPlayer.preparePlayer(Uri.fromFile(file), "other");
            this.bottomLayout.setVisibility(0);
            this.videoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    private void releasePlayer() {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.releasePlayer(true);
            this.videoPlayer = null;
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout2 != null) {
            this.photoContainerView.removeView(aspectRatioFrameLayout2);
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        this.bottomLayout.setVisibility(8);
    }

    private void toggleActionBar(boolean show, boolean animated) {
        if (show) {
            this.actionBar.setVisibility(0);
            if (this.videoPlayer != null) {
                this.bottomLayout.setVisibility(0);
            }
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(0);
            }
        }
        this.isActionBarVisible = show;
        this.actionBar.setEnabled(show);
        this.bottomLayout.setEnabled(show);
        float f = 1.0f;
        if (animated) {
            ArrayList<Animator> arrayList = new ArrayList<>();
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar2, property, fArr));
            GroupedPhotosListView groupedPhotosListView2 = this.groupedPhotosListView;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView2, property2, fArr2));
            FrameLayout frameLayout = this.bottomLayout;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property3, fArr3));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr4[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, property4, fArr4));
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentActionBarAnimation = animatorSet;
            animatorSet.playTogether(arrayList);
            if (!show) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ArticleViewer.this.currentActionBarAnimation != null && ArticleViewer.this.currentActionBarAnimation.equals(animation)) {
                            ArticleViewer.this.actionBar.setVisibility(8);
                            if (ArticleViewer.this.videoPlayer != null) {
                                ArticleViewer.this.bottomLayout.setVisibility(8);
                            }
                            if (ArticleViewer.this.captionTextView.getTag() != null) {
                                ArticleViewer.this.captionTextView.setVisibility(8);
                            }
                            AnimatorSet unused = ArticleViewer.this.currentActionBarAnimation = null;
                        }
                    }
                });
            }
            this.currentActionBarAnimation.setDuration(200);
            this.currentActionBarAnimation.start();
            return;
        }
        this.actionBar.setAlpha(show ? 1.0f : 0.0f);
        this.bottomLayout.setAlpha(show ? 1.0f : 0.0f);
        if (this.captionTextView.getTag() != null) {
            TextView textView2 = this.captionTextView;
            if (!show) {
                f = 0.0f;
            }
            textView2.setAlpha(f);
        }
        if (!show) {
            this.actionBar.setVisibility(8);
            if (this.videoPlayer != null) {
                this.bottomLayout.setVisibility(8);
            }
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(8);
            }
        }
    }

    private String getFileName(int index) {
        TLObject media = getMedia(index);
        if (media instanceof TLRPC.Photo) {
            media = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) media).sizes, AndroidUtilities.getPhotoSize());
        }
        return FileLoader.getAttachFileName(media);
    }

    /* access modifiers changed from: private */
    public TLObject getMedia(int index) {
        if (this.imagesArr.isEmpty() || index >= this.imagesArr.size() || index < 0) {
            return null;
        }
        TLRPC.PageBlock block = this.imagesArr.get(index);
        if (block instanceof TLRPC.TL_pageBlockPhoto) {
            return getPhotoWithId(((TLRPC.TL_pageBlockPhoto) block).photo_id);
        }
        if (block instanceof TLRPC.TL_pageBlockVideo) {
            return getDocumentWithId(((TLRPC.TL_pageBlockVideo) block).video_id);
        }
        return null;
    }

    /* access modifiers changed from: private */
    public File getMediaFile(int index) {
        TLRPC.Document document;
        TLRPC.PhotoSize sizeFull;
        if (this.imagesArr.isEmpty() || index >= this.imagesArr.size() || index < 0) {
            return null;
        }
        TLRPC.PageBlock block = this.imagesArr.get(index);
        if (block instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.Photo photo = getPhotoWithId(((TLRPC.TL_pageBlockPhoto) block).photo_id);
            if (!(photo == null || (sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize())) == null)) {
                return FileLoader.getPathToAttach(sizeFull, true);
            }
        } else if ((block instanceof TLRPC.TL_pageBlockVideo) && (document = getDocumentWithId(((TLRPC.TL_pageBlockVideo) block).video_id)) != null) {
            return FileLoader.getPathToAttach(document, true);
        }
        return null;
    }

    /* access modifiers changed from: private */
    public boolean isVideoBlock(TLRPC.PageBlock block) {
        TLRPC.Document document;
        if (!(block instanceof TLRPC.TL_pageBlockVideo) || (document = getDocumentWithId(((TLRPC.TL_pageBlockVideo) block).video_id)) == null) {
            return false;
        }
        return MessageObject.isVideoDocument(document);
    }

    /* access modifiers changed from: private */
    public boolean isMediaVideo(int index) {
        return !this.imagesArr.isEmpty() && index < this.imagesArr.size() && index >= 0 && isVideoBlock(this.imagesArr.get(index));
    }

    private String getMediaMime(int index) {
        TLRPC.Document document;
        if (index >= this.imagesArr.size() || index < 0) {
            return "image/jpeg";
        }
        TLRPC.PageBlock block = this.imagesArr.get(index);
        if (!(block instanceof TLRPC.TL_pageBlockVideo) || (document = getDocumentWithId(((TLRPC.TL_pageBlockVideo) block).video_id)) == null) {
            return "image/jpeg";
        }
        return document.mime_type;
    }

    private TLRPC.PhotoSize getFileLocation(TLObject media, int[] size) {
        TLRPC.PhotoSize thumb;
        if (media instanceof TLRPC.Photo) {
            TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) media).sizes, AndroidUtilities.getPhotoSize());
            if (sizeFull != null) {
                size[0] = sizeFull.size;
                if (size[0] == 0) {
                    size[0] = -1;
                }
                return sizeFull;
            }
            size[0] = -1;
            return null;
        } else if (!(media instanceof TLRPC.Document) || (thumb = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Document) media).thumbs, 90)) == null) {
            return null;
        } else {
            size[0] = thumb.size;
            if (size[0] == 0) {
                size[0] = -1;
            }
            return thumb;
        }
    }

    private void onPhotoShow(int index, PlaceProviderObject object) {
        this.currentIndex = -1;
        String[] strArr = this.currentFileNames;
        strArr[0] = null;
        strArr[1] = null;
        strArr[2] = null;
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
        }
        this.currentThumb = object != null ? object.thumb : null;
        this.menuItem.setVisibility(0);
        this.menuItem.hideSubItem(3);
        this.actionBar.setTranslationY(0.0f);
        this.captionTextView.setTag((Object) null);
        this.captionTextView.setVisibility(8);
        for (int a = 0; a < 3; a++) {
            RadialProgressView[] radialProgressViewArr = this.radialProgressViews;
            if (radialProgressViewArr[a] != null) {
                radialProgressViewArr[a].setBackgroundState(-1, false);
            }
        }
        setImageIndex(index, true);
        if (this.currentMedia != null && isMediaVideo(this.currentIndex)) {
            onActionClick(false);
        }
    }

    private void setImages() {
        if (this.photoAnimationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex);
            setIndexToImage(this.rightImage, this.currentIndex + 1);
            setIndexToImage(this.leftImage, this.currentIndex - 1);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00e5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setImageIndex(int r20, boolean r21) {
        /*
            r19 = this;
            r6 = r19
            r7 = r20
            int r0 = r6.currentIndex
            if (r0 != r7) goto L_0x0009
            return
        L_0x0009:
            r8 = 0
            if (r21 != 0) goto L_0x0015
            im.bclpbkiauv.messenger.ImageReceiver$BitmapHolder r0 = r6.currentThumb
            if (r0 == 0) goto L_0x0015
            r0.release()
            r6.currentThumb = r8
        L_0x0015:
            java.lang.String[] r0 = r6.currentFileNames
            java.lang.String r1 = r19.getFileName(r20)
            r9 = 0
            r0[r9] = r1
            java.lang.String[] r0 = r6.currentFileNames
            int r1 = r7 + 1
            java.lang.String r1 = r6.getFileName(r1)
            r10 = 1
            r0[r10] = r1
            java.lang.String[] r0 = r6.currentFileNames
            int r1 = r7 + -1
            java.lang.String r1 = r6.getFileName(r1)
            r11 = 2
            r0[r11] = r1
            int r12 = r6.currentIndex
            r6.currentIndex = r7
            r0 = 0
            r1 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r2 = r6.imagesArr
            boolean r2 = r2.isEmpty()
            r13 = 3
            if (r2 != 0) goto L_0x0149
            int r2 = r6.currentIndex
            if (r2 < 0) goto L_0x0145
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r3 = r6.imagesArr
            int r3 = r3.size()
            if (r2 < r3) goto L_0x0051
            goto L_0x0145
        L_0x0051:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r2 = r6.imagesArr
            int r3 = r6.currentIndex
            java.lang.Object r2 = r2.get(r3)
            r14 = r2
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r14 = (im.bclpbkiauv.tgnet.TLRPC.PageBlock) r14
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r2 = r6.currentMedia
            if (r2 == 0) goto L_0x0064
            if (r2 != r14) goto L_0x0064
            r2 = 1
            goto L_0x0065
        L_0x0064:
            r2 = 0
        L_0x0065:
            r15 = r2
            r6.currentMedia = r14
            int r1 = r6.currentIndex
            boolean r16 = r6.isMediaVideo(r1)
            if (r16 == 0) goto L_0x0075
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.menuItem
            r0.showSubItem(r13)
        L_0x0075:
            r0 = 0
            r1 = 0
            boolean r2 = r14 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockPhoto
            if (r2 == 0) goto L_0x009e
            r2 = r14
            im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockPhoto r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_pageBlockPhoto) r2
            java.lang.String r2 = r2.url
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x009e
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>(r2)
            im.bclpbkiauv.ui.ArticleViewer$21 r4 = new im.bclpbkiauv.ui.ArticleViewer$21
            r4.<init>(r2)
            int r5 = r2.length()
            r13 = 34
            r3.setSpan(r4, r9, r5, r13)
            r0 = r3
            r1 = 1
            r13 = r0
            r5 = r1
            goto L_0x00a0
        L_0x009e:
            r13 = r0
            r5 = r1
        L_0x00a0:
            if (r13 != 0) goto L_0x00c2
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r0 = r6.currentMedia
            im.bclpbkiauv.tgnet.TLRPC$RichText r17 = r6.getBlockCaption(r0, r11)
            r1 = 0
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r4 = r6.currentMedia
            r0 = 1120403456(0x42c80000, float:100.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            int r3 = -r0
            r0 = r19
            r2 = r17
            r18 = r3
            r3 = r17
            r8 = r5
            r5 = r18
            java.lang.CharSequence r13 = r0.getText(r1, r2, r3, r4, r5)
            goto L_0x00c3
        L_0x00c2:
            r8 = r5
        L_0x00c3:
            r6.setCurrentCaption(r13, r8)
            im.bclpbkiauv.ui.components.AnimatedFileDrawable r0 = r6.currentAnimation
            if (r0 == 0) goto L_0x00e5
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.menuItem
            r1 = 8
            r0.setVisibility(r1)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.menuItem
            r0.hideSubItem(r10)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            r1 = 2131689946(0x7f0f01da, float:1.9008922E38)
            java.lang.String r2 = "AttachGif"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x013c
        L_0x00e5:
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.menuItem
            r0.setVisibility(r9)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r0 = r6.imagesArr
            int r0 = r0.size()
            if (r0 != r10) goto L_0x0112
            if (r16 == 0) goto L_0x0103
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            r1 = 2131689963(0x7f0f01eb, float:1.9008956E38)
            java.lang.String r2 = "AttachVideo"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0137
        L_0x0103:
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            r1 = 2131689957(0x7f0f01e5, float:1.9008944E38)
            java.lang.String r2 = "AttachPhoto"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            goto L_0x0137
        L_0x0112:
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            r1 = 2131692467(0x7f0f0bb3, float:1.9014035E38)
            java.lang.Object[] r2 = new java.lang.Object[r11]
            int r3 = r6.currentIndex
            int r3 = r3 + r10
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r9] = r3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r3 = r6.imagesArr
            int r3 = r3.size()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r10] = r3
            java.lang.String r3 = "Of"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r1, r2)
            r0.setTitle(r1)
        L_0x0137:
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.menuItem
            r0.showSubItem(r10)
        L_0x013c:
            im.bclpbkiauv.ui.components.GroupedPhotosListView r0 = r6.groupedPhotosListView
            r0.fillList()
            r1 = r15
            r0 = r16
            goto L_0x0149
        L_0x0145:
            r6.closePhoto(r9)
            return
        L_0x0149:
            im.bclpbkiauv.ui.components.RecyclerListView[] r2 = r6.listView
            r2 = r2[r9]
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x0152:
            r4 = -1
            if (r3 >= r2) goto L_0x017d
            im.bclpbkiauv.ui.components.RecyclerListView[] r5 = r6.listView
            r5 = r5[r9]
            android.view.View r5 = r5.getChildAt(r3)
            boolean r8 = r5 instanceof im.bclpbkiauv.ui.ArticleViewer.BlockSlideshowCell
            if (r8 == 0) goto L_0x017a
            r8 = r5
            im.bclpbkiauv.ui.ArticleViewer$BlockSlideshowCell r8 = (im.bclpbkiauv.ui.ArticleViewer.BlockSlideshowCell) r8
            im.bclpbkiauv.tgnet.TLRPC$TL_pageBlockSlideshow r13 = r8.currentBlock
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PageBlock> r13 = r13.items
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r14 = r6.currentMedia
            int r13 = r13.indexOf(r14)
            if (r13 == r4) goto L_0x017a
            androidx.viewpager.widget.ViewPager r14 = r8.innerListView
            r14.setCurrentItem(r13, r9)
            goto L_0x017d
        L_0x017a:
            int r3 = r3 + 1
            goto L_0x0152
        L_0x017d:
            im.bclpbkiauv.ui.ArticleViewer$PlaceProviderObject r3 = r6.currentPlaceObject
            if (r3 == 0) goto L_0x018d
            int r5 = r6.photoAnimationInProgress
            if (r5 != 0) goto L_0x018b
            im.bclpbkiauv.messenger.ImageReceiver r3 = r3.imageReceiver
            r3.setVisible(r10, r10)
            goto L_0x018d
        L_0x018b:
            r6.showAfterAnimation = r3
        L_0x018d:
            im.bclpbkiauv.tgnet.TLRPC$PageBlock r3 = r6.currentMedia
            im.bclpbkiauv.ui.ArticleViewer$PlaceProviderObject r3 = r6.getPlaceForPhoto(r3)
            r6.currentPlaceObject = r3
            if (r3 == 0) goto L_0x01a3
            int r5 = r6.photoAnimationInProgress
            if (r5 != 0) goto L_0x01a1
            im.bclpbkiauv.messenger.ImageReceiver r3 = r3.imageReceiver
            r3.setVisible(r9, r10)
            goto L_0x01a3
        L_0x01a1:
            r6.hideAfterAnimation = r3
        L_0x01a3:
            if (r1 != 0) goto L_0x0202
            r6.draggingDown = r9
            r3 = 0
            r6.translationX = r3
            r6.translationY = r3
            r5 = 1065353216(0x3f800000, float:1.0)
            r6.scale = r5
            r6.animateToX = r3
            r6.animateToY = r3
            r6.animateToScale = r5
            r13 = 0
            r6.animationStartTime = r13
            r8 = 0
            r6.imageMoveAnimation = r8
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r8 = r6.aspectRatioFrameLayout
            if (r8 == 0) goto L_0x01c5
            r13 = 4
            r8.setVisibility(r13)
        L_0x01c5:
            r19.releasePlayer()
            r6.pinchStartDistance = r3
            r6.pinchStartScale = r5
            r6.pinchCenterX = r3
            r6.pinchCenterY = r3
            r6.pinchStartX = r3
            r6.pinchStartY = r3
            r6.moveStartX = r3
            r6.moveStartY = r3
            r6.zooming = r9
            r6.moving = r9
            r6.doubleTap = r9
            r6.invalidCoords = r9
            r6.canDragDown = r10
            r6.changingPage = r9
            r6.switchImageAfterAnimation = r9
            java.lang.String[] r3 = r6.currentFileNames
            r3 = r3[r9]
            if (r3 == 0) goto L_0x01fa
            if (r0 != 0) goto L_0x01fa
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r3 = r6.radialProgressViews
            r3 = r3[r9]
            int r3 = r3.backgroundState
            if (r3 == 0) goto L_0x01fa
            r3 = 1
            goto L_0x01fb
        L_0x01fa:
            r3 = 0
        L_0x01fb:
            r6.canZoom = r3
            float r3 = r6.scale
            r6.updateMinMax(r3)
        L_0x0202:
            if (r12 != r4) goto L_0x0212
            r19.setImages()
            r3 = 0
        L_0x0208:
            r4 = 3
            if (r3 >= r4) goto L_0x0211
            r6.checkProgress(r3, r9)
            int r3 = r3 + 1
            goto L_0x0208
        L_0x0211:
            goto L_0x025e
        L_0x0212:
            r6.checkProgress(r9, r9)
            int r3 = r6.currentIndex
            if (r12 <= r3) goto L_0x023a
            im.bclpbkiauv.messenger.ImageReceiver r4 = r6.rightImage
            im.bclpbkiauv.messenger.ImageReceiver r5 = r6.centerImage
            r6.rightImage = r5
            im.bclpbkiauv.messenger.ImageReceiver r5 = r6.leftImage
            r6.centerImage = r5
            r6.leftImage = r4
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r5 = r6.radialProgressViews
            r8 = r5[r9]
            r13 = r5[r11]
            r5[r9] = r13
            r5[r11] = r8
            int r3 = r3 - r10
            r6.setIndexToImage(r4, r3)
            r6.checkProgress(r10, r9)
            r6.checkProgress(r11, r9)
            goto L_0x025d
        L_0x023a:
            if (r12 >= r3) goto L_0x025d
            im.bclpbkiauv.messenger.ImageReceiver r4 = r6.leftImage
            im.bclpbkiauv.messenger.ImageReceiver r5 = r6.centerImage
            r6.leftImage = r5
            im.bclpbkiauv.messenger.ImageReceiver r5 = r6.rightImage
            r6.centerImage = r5
            r6.rightImage = r4
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r5 = r6.radialProgressViews
            r8 = r5[r9]
            r13 = r5[r10]
            r5[r9] = r13
            r5[r10] = r8
            int r3 = r3 + r10
            r6.setIndexToImage(r4, r3)
            r6.checkProgress(r10, r9)
            r6.checkProgress(r11, r9)
            goto L_0x025e
        L_0x025d:
        L_0x025e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.setImageIndex(int, boolean):void");
    }

    private void setCurrentCaption(CharSequence caption, boolean setAsIs) {
        CharSequence result;
        if (!TextUtils.isEmpty(caption)) {
            Theme.createChatResources((Context) null, true);
            if (setAsIs) {
                result = caption;
            } else if (caption instanceof Spannable) {
                Spannable spannable = (Spannable) caption;
                TextPaintUrlSpan[] spans = (TextPaintUrlSpan[]) spannable.getSpans(0, caption.length(), TextPaintUrlSpan.class);
                SpannableStringBuilder builder = new SpannableStringBuilder(caption.toString());
                CharSequence result2 = builder;
                if (spans != null && spans.length > 0) {
                    for (int a = 0; a < spans.length; a++) {
                        builder.setSpan(new URLSpan(spans[a].getUrl()) {
                            public void onClick(View widget) {
                                ArticleViewer.this.openWebpageUrl(getURL(), (String) null);
                            }
                        }, spannable.getSpanStart(spans[a]), spannable.getSpanEnd(spans[a]), 33);
                    }
                }
                result = result2;
            } else {
                result = new SpannableStringBuilder(caption.toString());
            }
            CharSequence str = Emoji.replaceEmoji(result, this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            this.captionTextView.setTag(str);
            this.captionTextView.setText(str);
            this.captionTextView.setVisibility(0);
            return;
        }
        this.captionTextView.setTag((Object) null);
        this.captionTextView.setVisibility(8);
    }

    private void checkProgress(int a, boolean animated) {
        if (this.currentFileNames[a] != null) {
            int index = this.currentIndex;
            boolean z = true;
            if (a == 1) {
                index++;
            } else if (a == 2) {
                index--;
            }
            File f = getMediaFile(index);
            boolean isVideo = isMediaVideo(index);
            if (f == null || !f.exists()) {
                if (!isVideo) {
                    this.radialProgressViews[a].setBackgroundState(0, animated);
                } else if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[a])) {
                    this.radialProgressViews[a].setBackgroundState(2, false);
                } else {
                    this.radialProgressViews[a].setBackgroundState(1, false);
                }
                Float progress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[a]);
                if (progress == null) {
                    progress = Float.valueOf(0.0f);
                }
                this.radialProgressViews[a].setProgress(progress.floatValue(), false);
            } else if (isVideo) {
                this.radialProgressViews[a].setBackgroundState(3, animated);
            } else {
                this.radialProgressViews[a].setBackgroundState(-1, animated);
            }
            if (a == 0) {
                if (this.currentFileNames[0] == null || isVideo || this.radialProgressViews[0].backgroundState == 0) {
                    z = false;
                }
                this.canZoom = z;
                return;
            }
            return;
        }
        this.radialProgressViews[a].setBackgroundState(-1, animated);
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int index) {
        ImageReceiver.BitmapHolder placeHolder;
        ImageReceiver.BitmapHolder placeHolder2;
        ImageReceiver imageReceiver2 = imageReceiver;
        int i = index;
        imageReceiver2.setOrientation(0, false);
        int[] size = new int[1];
        TLObject media = getMedia(i);
        TLRPC.PhotoSize fileLocation = getFileLocation(media, size);
        BitmapDrawable bitmapDrawable = null;
        if (fileLocation != null) {
            if (media instanceof TLRPC.Photo) {
                TLRPC.Photo photo = (TLRPC.Photo) media;
                if (this.currentThumb == null || imageReceiver2 != this.centerImage) {
                    placeHolder2 = null;
                } else {
                    placeHolder2 = this.currentThumb;
                }
                if (size[0] == 0) {
                    size[0] = -1;
                }
                TLRPC.PhotoSize thumbLocation = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80);
                ImageLocation forPhoto = ImageLocation.getForPhoto(fileLocation, photo);
                ImageLocation forPhoto2 = ImageLocation.getForPhoto(thumbLocation, photo);
                if (placeHolder2 != null) {
                    bitmapDrawable = new BitmapDrawable(placeHolder2.bitmap);
                }
                TLRPC.PhotoSize photoSize = thumbLocation;
                ImageReceiver.BitmapHolder bitmapHolder = placeHolder2;
                TLRPC.Photo photo2 = photo;
                imageReceiver.setImage(forPhoto, (String) null, forPhoto2, "b", bitmapDrawable, size[0], (String) null, this.currentPage, 1);
            } else if (!isMediaVideo(i)) {
                AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
                if (animatedFileDrawable != null) {
                    imageReceiver2.setImageBitmap((Drawable) animatedFileDrawable);
                    this.currentAnimation.setSecondParentView(this.photoContainerView);
                }
            } else if (!(fileLocation.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                if (this.currentThumb == null || imageReceiver2 != this.centerImage) {
                    placeHolder = null;
                } else {
                    placeHolder = this.currentThumb;
                }
                ImageReceiver.BitmapHolder bitmapHolder2 = placeHolder;
                imageReceiver.setImage((ImageLocation) null, (String) null, ImageLocation.getForDocument(fileLocation, (TLRPC.Document) media), "b", placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, 0, (String) null, this.currentPage, 1);
            } else {
                imageReceiver2.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
            }
        } else if (size[0] == 0) {
            imageReceiver2.setImageBitmap((Bitmap) null);
        } else {
            imageReceiver2.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
        }
    }

    public boolean isShowingImage(TLRPC.PageBlock object) {
        return this.isPhotoVisible && !this.disableShowCheck && object != null && this.currentMedia == object;
    }

    private boolean checkPhotoAnimation() {
        if (this.photoAnimationInProgress != 0 && Math.abs(this.photoTransitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            Runnable runnable = this.photoAnimationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.photoAnimationEndRunnable = null;
            }
            this.photoAnimationInProgress = 0;
        }
        if (this.photoAnimationInProgress != 0) {
            return true;
        }
        return false;
    }

    public boolean openPhoto(TLRPC.PageBlock block) {
        PlaceProviderObject object;
        int clipHorizontal;
        Object obj;
        TLRPC.PageBlock pageBlock = block;
        if (this.pageSwitchAnimation != null || this.parentActivity == null || this.isPhotoVisible || checkPhotoAnimation() || pageBlock == null || (object = getPlaceForPhoto(block)) == null) {
            return false;
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.isPhotoVisible = true;
        toggleActionBar(true, false);
        this.actionBar.setAlpha(0.0f);
        this.bottomLayout.setAlpha(0.0f);
        this.captionTextView.setAlpha(0.0f);
        this.photoBackgroundDrawable.setAlpha(0);
        this.groupedPhotosListView.setAlpha(0.0f);
        this.photoContainerView.setAlpha(1.0f);
        this.disableShowCheck = true;
        this.photoAnimationInProgress = 1;
        if (pageBlock != null) {
            this.currentAnimation = object.imageReceiver.getAnimation();
        }
        int index = this.adapter[0].photoBlocks.indexOf(pageBlock);
        this.imagesArr.clear();
        if (!(pageBlock instanceof TLRPC.TL_pageBlockVideo) || isVideoBlock(block)) {
            this.imagesArr.addAll(this.adapter[0].photoBlocks);
        } else {
            this.imagesArr.add(pageBlock);
            index = 0;
        }
        onPhotoShow(index, object);
        RectF drawRegion = object.imageReceiver.getDrawRegion();
        int orientation = object.imageReceiver.getOrientation();
        int animatedOrientation = object.imageReceiver.getAnimatedOrientation();
        if (animatedOrientation != 0) {
            orientation = animatedOrientation;
        }
        this.animatingImageView.setVisibility(0);
        this.animatingImageView.setRadius(object.radius);
        this.animatingImageView.setOrientation(orientation);
        this.animatingImageView.setNeedRadius(object.radius != 0);
        this.animatingImageView.setImageBitmap(object.thumb);
        this.animatingImageView.setAlpha(1.0f);
        this.animatingImageView.setPivotX(0.0f);
        this.animatingImageView.setPivotY(0.0f);
        this.animatingImageView.setScaleX(object.scale);
        this.animatingImageView.setScaleY(object.scale);
        this.animatingImageView.setTranslationX(((float) object.viewX) + (drawRegion.left * object.scale));
        this.animatingImageView.setTranslationY(((float) object.viewY) + (drawRegion.top * object.scale));
        ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
        layoutParams.width = (int) drawRegion.width();
        layoutParams.height = (int) drawRegion.height();
        this.animatingImageView.setLayoutParams(layoutParams);
        float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
        float scaleY = ((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) / ((float) layoutParams.height);
        float scale2 = scaleX > scaleY ? scaleY : scaleX;
        float height = ((float) layoutParams.height) * scale2;
        float xPos = (((float) AndroidUtilities.displaySize.x) - (((float) layoutParams.width) * scale2)) / 2.0f;
        if (Build.VERSION.SDK_INT >= 21 && (obj = this.lastInsets) != null) {
            xPos += (float) ((WindowInsets) obj).getSystemWindowInsetLeft();
        }
        float yPos = (((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) - height) / 2.0f;
        if (object.imageReceiver.isAspectFit()) {
            clipHorizontal = 0;
        } else {
            clipHorizontal = (int) Math.abs(drawRegion.left - ((float) object.imageReceiver.getImageX()));
        }
        int i = index;
        int clipVertical = (int) Math.abs(drawRegion.top - ((float) object.imageReceiver.getImageY()));
        int i2 = orientation;
        int[] coords2 = new int[2];
        object.parentView.getLocationInWindow(coords2);
        int i3 = animatedOrientation;
        float f = scaleX;
        int clipTop = (int) ((((float) coords2[1]) - (((float) object.viewY) + drawRegion.top)) + ((float) object.clipTopAddition));
        if (clipTop < 0) {
            clipTop = 0;
        }
        int clipBottom = (int) ((((((float) object.viewY) + drawRegion.top) + ((float) layoutParams.height)) - ((float) (coords2[1] + object.parentView.getHeight()))) + ((float) object.clipBottomAddition));
        if (clipBottom < 0) {
            clipBottom = 0;
        }
        int clipTop2 = Math.max(clipTop, clipVertical);
        int clipBottom2 = Math.max(clipBottom, clipVertical);
        ViewGroup.LayoutParams layoutParams2 = layoutParams;
        this.animationValues[0][0] = this.animatingImageView.getScaleX();
        this.animationValues[0][1] = this.animatingImageView.getScaleY();
        this.animationValues[0][2] = this.animatingImageView.getTranslationX();
        this.animationValues[0][3] = this.animatingImageView.getTranslationY();
        RectF rectF = drawRegion;
        this.animationValues[0][4] = ((float) clipHorizontal) * object.scale;
        this.animationValues[0][5] = ((float) clipTop2) * object.scale;
        this.animationValues[0][6] = ((float) clipBottom2) * object.scale;
        this.animationValues[0][7] = (float) this.animatingImageView.getRadius();
        this.animationValues[0][8] = ((float) clipVertical) * object.scale;
        this.animationValues[0][9] = ((float) clipHorizontal) * object.scale;
        float[][] fArr = this.animationValues;
        fArr[1][0] = scale2;
        fArr[1][1] = scale2;
        fArr[1][2] = xPos;
        fArr[1][3] = yPos;
        fArr[1][4] = 0.0f;
        fArr[1][5] = 0.0f;
        fArr[1][6] = 0.0f;
        fArr[1][7] = 0.0f;
        fArr[1][8] = 0.0f;
        fArr[1][9] = 0.0f;
        this.photoContainerView.setVisibility(0);
        this.photoContainerBackground.setVisibility(0);
        this.animatingImageView.setAnimationProgress(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        int i4 = clipVertical;
        float f2 = yPos;
        int i5 = clipHorizontal;
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0, 255}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f, 1.0f})});
        this.photoAnimationEndRunnable = new Runnable() {
            public final void run() {
                ArticleViewer.this.lambda$openPhoto$37$ArticleViewer();
            }
        };
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        ArticleViewer.AnonymousClass23.this.lambda$onAnimationEnd$0$ArticleViewer$23();
                    }
                });
            }

            public /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$23() {
                NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(false);
                if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                    ArticleViewer.this.photoAnimationEndRunnable.run();
                    Runnable unused = ArticleViewer.this.photoAnimationEndRunnable = null;
                }
            }
        });
        this.photoTransitionAnimationStartTime = System.currentTimeMillis();
        AndroidUtilities.runOnUIThread(new Runnable(animatorSet) {
            private final /* synthetic */ AnimatorSet f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ArticleViewer.this.lambda$openPhoto$38$ArticleViewer(this.f$1);
            }
        });
        if (Build.VERSION.SDK_INT >= 18) {
            this.photoContainerView.setLayerType(2, (Paint) null);
        }
        Runnable unused = this.photoBackgroundDrawable.drawRunnable = new Runnable(object) {
            private final /* synthetic */ ArticleViewer.PlaceProviderObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ArticleViewer.this.lambda$openPhoto$39$ArticleViewer(this.f$1);
            }
        };
        return true;
    }

    public /* synthetic */ void lambda$openPhoto$37$ArticleViewer() {
        if (this.photoContainerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.photoContainerView.setLayerType(0, (Paint) null);
            }
            this.photoAnimationInProgress = 0;
            this.photoTransitionAnimationStartTime = 0;
            setImages();
            this.photoContainerView.invalidate();
            this.animatingImageView.setVisibility(8);
            PlaceProviderObject placeProviderObject = this.showAfterAnimation;
            if (placeProviderObject != null) {
                placeProviderObject.imageReceiver.setVisible(true, true);
            }
            PlaceProviderObject placeProviderObject2 = this.hideAfterAnimation;
            if (placeProviderObject2 != null) {
                placeProviderObject2.imageReceiver.setVisible(false, true);
            }
        }
    }

    public /* synthetic */ void lambda$openPhoto$38$ArticleViewer(AnimatorSet animatorSet) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        animatorSet.start();
    }

    public /* synthetic */ void lambda$openPhoto$39$ArticleViewer(PlaceProviderObject object) {
        this.disableShowCheck = false;
        object.imageReceiver.setVisible(false, true);
    }

    public void closePhoto(boolean animated) {
        int clipHorizontal;
        Object obj;
        if (this.parentActivity != null && this.isPhotoVisible && !checkPhotoAnimation()) {
            releasePlayer();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
            this.isActionBarVisible = false;
            VelocityTracker velocityTracker2 = this.velocityTracker;
            if (velocityTracker2 != null) {
                velocityTracker2.recycle();
                this.velocityTracker = null;
            }
            PlaceProviderObject object = getPlaceForPhoto(this.currentMedia);
            if (animated) {
                this.photoAnimationInProgress = 1;
                this.animatingImageView.setVisibility(0);
                this.photoContainerView.invalidate();
                AnimatorSet animatorSet = new AnimatorSet();
                ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                RectF drawRegion = null;
                int orientation = this.centerImage.getOrientation();
                int animatedOrientation = 0;
                if (!(object == null || object.imageReceiver == null)) {
                    animatedOrientation = object.imageReceiver.getAnimatedOrientation();
                }
                if (animatedOrientation != 0) {
                    orientation = animatedOrientation;
                }
                this.animatingImageView.setOrientation(orientation);
                if (object != null) {
                    this.animatingImageView.setNeedRadius(object.radius != 0);
                    drawRegion = object.imageReceiver.getDrawRegion();
                    layoutParams.width = (int) drawRegion.width();
                    layoutParams.height = (int) drawRegion.height();
                    this.animatingImageView.setImageBitmap(object.thumb);
                } else {
                    this.animatingImageView.setNeedRadius(false);
                    layoutParams.width = this.centerImage.getImageWidth();
                    layoutParams.height = this.centerImage.getImageHeight();
                    this.animatingImageView.setImageBitmap(this.centerImage.getBitmapSafe());
                }
                this.animatingImageView.setLayoutParams(layoutParams);
                float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                float scaleY = ((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) / ((float) layoutParams.height);
                float scale2 = scaleX > scaleY ? scaleY : scaleX;
                float height = ((float) layoutParams.height) * this.scale * scale2;
                float xPos = (((float) AndroidUtilities.displaySize.x) - ((((float) layoutParams.width) * this.scale) * scale2)) / 2.0f;
                if (Build.VERSION.SDK_INT >= 21 && (obj = this.lastInsets) != null) {
                    xPos += (float) ((WindowInsets) obj).getSystemWindowInsetLeft();
                }
                float yPos = (((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) - height) / 2.0f;
                this.animatingImageView.setTranslationX(this.translationX + xPos);
                this.animatingImageView.setTranslationY(this.translationY + yPos);
                this.animatingImageView.setScaleX(this.scale * scale2);
                this.animatingImageView.setScaleY(this.scale * scale2);
                if (object != null) {
                    float f = yPos;
                    object.imageReceiver.setVisible(false, true);
                    if (object.imageReceiver.isAspectFit()) {
                        clipHorizontal = 0;
                    } else {
                        clipHorizontal = (int) Math.abs(drawRegion.left - ((float) object.imageReceiver.getImageX()));
                    }
                    int clipVertical = (int) Math.abs(drawRegion.top - ((float) object.imageReceiver.getImageY()));
                    float f2 = xPos;
                    int[] coords2 = new int[2];
                    object.parentView.getLocationInWindow(coords2);
                    ViewGroup.LayoutParams layoutParams2 = layoutParams;
                    int i = orientation;
                    int clipTop = (int) ((((float) coords2[1]) - (((float) object.viewY) + drawRegion.top)) + ((float) object.clipTopAddition));
                    if (clipTop < 0) {
                        clipTop = 0;
                    }
                    int i2 = animatedOrientation;
                    int clipBottom = (int) ((((((float) object.viewY) + drawRegion.top) + (drawRegion.bottom - drawRegion.top)) - ((float) (coords2[1] + object.parentView.getHeight()))) + ((float) object.clipBottomAddition));
                    if (clipBottom < 0) {
                        clipBottom = 0;
                    }
                    int clipTop2 = Math.max(clipTop, clipVertical);
                    int clipBottom2 = Math.max(clipBottom, clipVertical);
                    int[] iArr = coords2;
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
                    float f3 = scaleX;
                    this.animationValues[1][2] = ((float) object.viewX) + (drawRegion.left * object.scale);
                    this.animationValues[1][3] = ((float) object.viewY) + (drawRegion.top * object.scale);
                    this.animationValues[1][4] = ((float) clipHorizontal) * object.scale;
                    this.animationValues[1][5] = ((float) clipTop2) * object.scale;
                    this.animationValues[1][6] = ((float) clipBottom2) * object.scale;
                    this.animationValues[1][7] = (float) object.radius;
                    this.animationValues[1][8] = ((float) clipVertical) * object.scale;
                    this.animationValues[1][9] = ((float) clipHorizontal) * object.scale;
                    int i3 = clipHorizontal;
                    int i4 = clipVertical;
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f})});
                } else {
                    float f4 = xPos;
                    ViewGroup.LayoutParams layoutParams3 = layoutParams;
                    int i5 = orientation;
                    int i6 = animatedOrientation;
                    float f5 = scaleX;
                    int h = AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight;
                    Animator[] animatorArr = new Animator[7];
                    animatorArr[0] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, new float[]{0.0f});
                    ClippingImageView clippingImageView = this.animatingImageView;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    fArr2[0] = this.translationY >= 0.0f ? (float) h : (float) (-h);
                    animatorArr[2] = ObjectAnimator.ofFloat(clippingImageView, property, fArr2);
                    animatorArr[3] = ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f});
                    animatorArr[6] = ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.photoAnimationEndRunnable = new Runnable(object) {
                    private final /* synthetic */ ArticleViewer.PlaceProviderObject f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ArticleViewer.this.lambda$closePhoto$40$ArticleViewer(this.f$1);
                    }
                };
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                ArticleViewer.AnonymousClass24.this.lambda$onAnimationEnd$0$ArticleViewer$24();
                            }
                        });
                    }

                    public /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$24() {
                        if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                            ArticleViewer.this.photoAnimationEndRunnable.run();
                            Runnable unused = ArticleViewer.this.photoAnimationEndRunnable = null;
                        }
                    }
                });
                this.photoTransitionAnimationStartTime = System.currentTimeMillis();
                if (Build.VERSION.SDK_INT >= 18) {
                    this.photoContainerView.setLayerType(2, (Paint) null);
                }
                animatorSet.start();
            } else {
                this.photoContainerView.setVisibility(4);
                this.photoContainerBackground.setVisibility(4);
                this.photoAnimationInProgress = 0;
                onPhotoClosed(object);
                this.photoContainerView.setScaleX(1.0f);
                this.photoContainerView.setScaleY(1.0f);
            }
            AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
            if (animatedFileDrawable != null) {
                animatedFileDrawable.setSecondParentView((View) null);
                this.currentAnimation = null;
                this.centerImage.setImageBitmap((Drawable) null);
            }
        }
    }

    public /* synthetic */ void lambda$closePhoto$40$ArticleViewer(PlaceProviderObject object) {
        if (Build.VERSION.SDK_INT >= 18) {
            this.photoContainerView.setLayerType(0, (Paint) null);
        }
        this.photoContainerView.setVisibility(4);
        this.photoContainerBackground.setVisibility(4);
        this.photoAnimationInProgress = 0;
        onPhotoClosed(object);
    }

    private void onPhotoClosed(PlaceProviderObject object) {
        this.isPhotoVisible = false;
        this.disableShowCheck = true;
        this.currentMedia = null;
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.setSecondParentView((View) null);
            this.currentAnimation = null;
        }
        for (int a = 0; a < 3; a++) {
            RadialProgressView[] radialProgressViewArr = this.radialProgressViews;
            if (radialProgressViewArr[a] != null) {
                radialProgressViewArr[a].setBackgroundState(-1, false);
            }
        }
        Bitmap bitmap = null;
        this.centerImage.setImageBitmap(bitmap);
        this.leftImage.setImageBitmap(bitmap);
        this.rightImage.setImageBitmap(bitmap);
        this.photoContainerView.post(new Runnable() {
            public final void run() {
                ArticleViewer.this.lambda$onPhotoClosed$41$ArticleViewer();
            }
        });
        this.disableShowCheck = false;
        if (object != null) {
            object.imageReceiver.setVisible(true, true);
        }
        this.groupedPhotosListView.clear();
    }

    public /* synthetic */ void lambda$onPhotoClosed$41$ArticleViewer() {
        this.animatingImageView.setImageBitmap((ImageReceiver.BitmapHolder) null);
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false);
        }
    }

    private void updateMinMax(float scale2) {
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

    /* access modifiers changed from: private */
    public int getContainerViewWidth() {
        return this.photoContainerView.getWidth();
    }

    /* access modifiers changed from: private */
    public int getContainerViewHeight() {
        return this.photoContainerView.getHeight();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01fb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processTouchEvent(android.view.MotionEvent r13) {
        /*
            r12 = this;
            int r0 = r12.photoAnimationInProgress
            r1 = 0
            if (r0 != 0) goto L_0x03fb
            long r2 = r12.animationStartTime
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x000f
            goto L_0x03fb
        L_0x000f:
            int r0 = r13.getPointerCount()
            r2 = 1
            if (r0 != r2) goto L_0x002c
            android.view.GestureDetector r0 = r12.gestureDetector
            boolean r0 = r0.onTouchEvent(r13)
            if (r0 == 0) goto L_0x002c
            boolean r0 = r12.doubleTap
            if (r0 == 0) goto L_0x002c
            r12.doubleTap = r1
            r12.moving = r1
            r12.zooming = r1
            r12.checkMinMax(r1)
            return r2
        L_0x002c:
            int r0 = r13.getActionMasked()
            r3 = 1073741824(0x40000000, float:2.0)
            r6 = 2
            if (r0 == 0) goto L_0x036f
            int r0 = r13.getActionMasked()
            r7 = 5
            if (r0 != r7) goto L_0x003e
            goto L_0x036f
        L_0x003e:
            int r0 = r13.getActionMasked()
            r7 = 0
            r8 = 1077936128(0x40400000, float:3.0)
            r9 = 1065353216(0x3f800000, float:1.0)
            if (r0 != r6) goto L_0x0216
            boolean r0 = r12.canZoom
            if (r0 == 0) goto L_0x00cc
            int r0 = r13.getPointerCount()
            if (r0 != r6) goto L_0x00cc
            boolean r0 = r12.draggingDown
            if (r0 != 0) goto L_0x00cc
            boolean r0 = r12.zooming
            if (r0 == 0) goto L_0x00cc
            boolean r0 = r12.changingPage
            if (r0 != 0) goto L_0x00cc
            r12.discardTap = r2
            float r0 = r13.getX(r2)
            float r3 = r13.getX(r1)
            float r0 = r0 - r3
            double r3 = (double) r0
            float r0 = r13.getY(r2)
            float r2 = r13.getY(r1)
            float r0 = r0 - r2
            double r7 = (double) r0
            double r2 = java.lang.Math.hypot(r3, r7)
            float r0 = (float) r2
            float r2 = r12.pinchStartDistance
            float r0 = r0 / r2
            float r2 = r12.pinchStartScale
            float r0 = r0 * r2
            r12.scale = r0
            float r0 = r12.pinchCenterX
            int r2 = r12.getContainerViewWidth()
            int r2 = r2 / r6
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r12.pinchCenterX
            int r3 = r12.getContainerViewWidth()
            int r3 = r3 / r6
            float r3 = (float) r3
            float r2 = r2 - r3
            float r3 = r12.pinchStartX
            float r2 = r2 - r3
            float r3 = r12.scale
            float r4 = r12.pinchStartScale
            float r3 = r3 / r4
            float r2 = r2 * r3
            float r0 = r0 - r2
            r12.translationX = r0
            float r0 = r12.pinchCenterY
            int r2 = r12.getContainerViewHeight()
            int r2 = r2 / r6
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r12.pinchCenterY
            int r3 = r12.getContainerViewHeight()
            int r3 = r3 / r6
            float r3 = (float) r3
            float r2 = r2 - r3
            float r3 = r12.pinchStartY
            float r2 = r2 - r3
            float r3 = r12.scale
            float r4 = r12.pinchStartScale
            float r4 = r3 / r4
            float r2 = r2 * r4
            float r0 = r0 - r2
            r12.translationY = r0
            r12.updateMinMax(r3)
            im.bclpbkiauv.ui.ArticleViewer$FrameLayoutDrawer r0 = r12.photoContainerView
            r0.invalidate()
            goto L_0x03fa
        L_0x00cc:
            int r0 = r13.getPointerCount()
            if (r0 != r2) goto L_0x03fa
            android.view.VelocityTracker r0 = r12.velocityTracker
            if (r0 == 0) goto L_0x00d9
            r0.addMovement(r13)
        L_0x00d9:
            float r0 = r13.getX()
            float r6 = r12.moveStartX
            float r0 = r0 - r6
            float r0 = java.lang.Math.abs(r0)
            float r6 = r13.getY()
            float r10 = r12.dragY
            float r6 = r6 - r10
            float r6 = java.lang.Math.abs(r6)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r10 = (float) r10
            int r10 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r10 > 0) goto L_0x0101
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r10 = (float) r10
            int r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r10 <= 0) goto L_0x0103
        L_0x0101:
            r12.discardTap = r2
        L_0x0103:
            boolean r10 = r12.canDragDown
            if (r10 == 0) goto L_0x0134
            boolean r10 = r12.draggingDown
            if (r10 != 0) goto L_0x0134
            float r10 = r12.scale
            int r10 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
            if (r10 != 0) goto L_0x0134
            r10 = 1106247680(0x41f00000, float:30.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            int r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r10 < 0) goto L_0x0134
            float r3 = r6 / r3
            int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x0134
            r12.draggingDown = r2
            r12.moving = r1
            float r3 = r13.getY()
            r12.dragY = r3
            boolean r3 = r12.isActionBarVisible
            if (r3 == 0) goto L_0x0133
            r12.toggleActionBar(r1, r2)
        L_0x0133:
            return r2
        L_0x0134:
            boolean r3 = r12.draggingDown
            if (r3 == 0) goto L_0x0148
            float r2 = r13.getY()
            float r3 = r12.dragY
            float r2 = r2 - r3
            r12.translationY = r2
            im.bclpbkiauv.ui.ArticleViewer$FrameLayoutDrawer r2 = r12.photoContainerView
            r2.invalidate()
            goto L_0x0214
        L_0x0148:
            boolean r3 = r12.invalidCoords
            if (r3 != 0) goto L_0x0206
            long r10 = r12.animationStartTime
            int r3 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r3 != 0) goto L_0x0206
            float r3 = r12.moveStartX
            float r4 = r13.getX()
            float r3 = r3 - r4
            float r4 = r12.moveStartY
            float r5 = r13.getY()
            float r4 = r4 - r5
            boolean r5 = r12.moving
            if (r5 != 0) goto L_0x0184
            float r5 = r12.scale
            int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r5 != 0) goto L_0x017e
            float r5 = java.lang.Math.abs(r4)
            r10 = 1094713344(0x41400000, float:12.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r5 = r5 + r10
            float r10 = java.lang.Math.abs(r3)
            int r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r5 < 0) goto L_0x0184
        L_0x017e:
            float r5 = r12.scale
            int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r5 == 0) goto L_0x0205
        L_0x0184:
            boolean r5 = r12.moving
            if (r5 != 0) goto L_0x018e
            r3 = 0
            r4 = 0
            r12.moving = r2
            r12.canDragDown = r1
        L_0x018e:
            float r2 = r13.getX()
            r12.moveStartX = r2
            float r2 = r13.getY()
            r12.moveStartY = r2
            float r2 = r12.scale
            r12.updateMinMax(r2)
            float r2 = r12.translationX
            float r5 = r12.minX
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 >= 0) goto L_0x01af
            im.bclpbkiauv.messenger.ImageReceiver r2 = r12.rightImage
            boolean r2 = r2.hasImageSet()
            if (r2 == 0) goto L_0x01bf
        L_0x01af:
            float r2 = r12.translationX
            float r5 = r12.maxX
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x01c0
            im.bclpbkiauv.messenger.ImageReceiver r2 = r12.leftImage
            boolean r2 = r2.hasImageSet()
            if (r2 != 0) goto L_0x01c0
        L_0x01bf:
            float r3 = r3 / r8
        L_0x01c0:
            float r2 = r12.maxY
            int r5 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x01e1
            float r5 = r12.minY
            int r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r7 != 0) goto L_0x01e1
            float r7 = r12.translationY
            float r8 = r7 - r4
            int r8 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
            if (r8 >= 0) goto L_0x01d8
            r12.translationY = r5
            r4 = 0
            goto L_0x01f0
        L_0x01d8:
            float r7 = r7 - r4
            int r5 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r5 <= 0) goto L_0x01f0
            r12.translationY = r2
            r4 = 0
            goto L_0x01f0
        L_0x01e1:
            float r2 = r12.translationY
            float r5 = r12.minY
            int r5 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x01ef
            float r5 = r12.maxY
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x01f0
        L_0x01ef:
            float r4 = r4 / r8
        L_0x01f0:
            float r2 = r12.translationX
            float r2 = r2 - r3
            r12.translationX = r2
            float r2 = r12.scale
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0200
            float r2 = r12.translationY
            float r2 = r2 - r4
            r12.translationY = r2
        L_0x0200:
            im.bclpbkiauv.ui.ArticleViewer$FrameLayoutDrawer r2 = r12.photoContainerView
            r2.invalidate()
        L_0x0205:
            goto L_0x0214
        L_0x0206:
            r12.invalidCoords = r1
            float r2 = r13.getX()
            r12.moveStartX = r2
            float r2 = r13.getY()
            r12.moveStartY = r2
        L_0x0214:
            goto L_0x03fa
        L_0x0216:
            int r0 = r13.getActionMasked()
            r3 = 3
            if (r0 == r3) goto L_0x022a
            int r0 = r13.getActionMasked()
            if (r0 == r2) goto L_0x022a
            int r0 = r13.getActionMasked()
            r4 = 6
            if (r0 != r4) goto L_0x03fa
        L_0x022a:
            boolean r0 = r12.zooming
            if (r0 == 0) goto L_0x02a9
            r12.invalidCoords = r2
            float r0 = r12.scale
            int r3 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r3 >= 0) goto L_0x023d
            r12.updateMinMax(r9)
            r12.animateTo(r9, r7, r7, r2)
            goto L_0x02a5
        L_0x023d:
            int r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r0 <= 0) goto L_0x02a2
            float r0 = r12.pinchCenterX
            int r3 = r12.getContainerViewWidth()
            int r3 = r3 / r6
            float r3 = (float) r3
            float r0 = r0 - r3
            float r3 = r12.pinchCenterX
            int r4 = r12.getContainerViewWidth()
            int r4 = r4 / r6
            float r4 = (float) r4
            float r3 = r3 - r4
            float r4 = r12.pinchStartX
            float r3 = r3 - r4
            float r4 = r12.pinchStartScale
            float r4 = r8 / r4
            float r3 = r3 * r4
            float r0 = r0 - r3
            float r3 = r12.pinchCenterY
            int r4 = r12.getContainerViewHeight()
            int r4 = r4 / r6
            float r4 = (float) r4
            float r3 = r3 - r4
            float r4 = r12.pinchCenterY
            int r5 = r12.getContainerViewHeight()
            int r5 = r5 / r6
            float r5 = (float) r5
            float r4 = r4 - r5
            float r5 = r12.pinchStartY
            float r4 = r4 - r5
            float r5 = r12.pinchStartScale
            float r5 = r8 / r5
            float r4 = r4 * r5
            float r3 = r3 - r4
            r12.updateMinMax(r8)
            float r4 = r12.minX
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x0285
            float r0 = r12.minX
            goto L_0x028d
        L_0x0285:
            float r4 = r12.maxX
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x028d
            float r0 = r12.maxX
        L_0x028d:
            float r4 = r12.minY
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x0296
            float r3 = r12.minY
            goto L_0x029e
        L_0x0296:
            float r4 = r12.maxY
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x029e
            float r3 = r12.maxY
        L_0x029e:
            r12.animateTo(r8, r0, r3, r2)
            goto L_0x02a5
        L_0x02a2:
            r12.checkMinMax(r2)
        L_0x02a5:
            r12.zooming = r1
            goto L_0x03fa
        L_0x02a9:
            boolean r0 = r12.draggingDown
            if (r0 == 0) goto L_0x02cf
            float r0 = r12.dragY
            float r3 = r13.getY()
            float r0 = r0 - r3
            float r0 = java.lang.Math.abs(r0)
            int r3 = r12.getContainerViewHeight()
            float r3 = (float) r3
            r4 = 1086324736(0x40c00000, float:6.0)
            float r3 = r3 / r4
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x02c8
            r12.closePhoto(r2)
            goto L_0x02cb
        L_0x02c8:
            r12.animateTo(r9, r7, r7, r1)
        L_0x02cb:
            r12.draggingDown = r1
            goto L_0x03fa
        L_0x02cf:
            boolean r0 = r12.moving
            if (r0 == 0) goto L_0x03fa
            float r0 = r12.translationX
            float r4 = r12.translationY
            float r5 = r12.scale
            r12.updateMinMax(r5)
            r12.moving = r1
            r12.canDragDown = r2
            r5 = 0
            android.view.VelocityTracker r6 = r12.velocityTracker
            if (r6 == 0) goto L_0x02f6
            float r7 = r12.scale
            int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r7 != 0) goto L_0x02f6
            r7 = 1000(0x3e8, float:1.401E-42)
            r6.computeCurrentVelocity(r7)
            android.view.VelocityTracker r6 = r12.velocityTracker
            float r5 = r6.getXVelocity()
        L_0x02f6:
            float r6 = r12.translationX
            float r7 = r12.minX
            int r8 = r12.getContainerViewWidth()
            int r8 = r8 / r3
            float r8 = (float) r8
            float r7 = r7 - r8
            r8 = 1143111680(0x44228000, float:650.0)
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 < 0) goto L_0x0312
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r6 = -r6
            float r6 = (float) r6
            int r6 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r6 >= 0) goto L_0x031e
        L_0x0312:
            im.bclpbkiauv.messenger.ImageReceiver r6 = r12.rightImage
            boolean r6 = r6.hasImageSet()
            if (r6 == 0) goto L_0x031e
            r12.goToNext()
            return r2
        L_0x031e:
            float r6 = r12.translationX
            float r7 = r12.maxX
            int r9 = r12.getContainerViewWidth()
            int r9 = r9 / r3
            float r3 = (float) r9
            float r7 = r7 + r3
            int r3 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r3 > 0) goto L_0x0336
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r3 = (float) r3
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x0342
        L_0x0336:
            im.bclpbkiauv.messenger.ImageReceiver r3 = r12.leftImage
            boolean r3 = r3.hasImageSet()
            if (r3 == 0) goto L_0x0342
            r12.goToPrev()
            return r2
        L_0x0342:
            float r2 = r12.translationX
            float r3 = r12.minX
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x034d
            float r0 = r12.minX
            goto L_0x0355
        L_0x034d:
            float r3 = r12.maxX
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0355
            float r0 = r12.maxX
        L_0x0355:
            float r2 = r12.translationY
            float r3 = r12.minY
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0360
            float r4 = r12.minY
            goto L_0x0368
        L_0x0360:
            float r3 = r12.maxY
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0368
            float r4 = r12.maxY
        L_0x0368:
            float r2 = r12.scale
            r12.animateTo(r2, r0, r4, r1)
            goto L_0x03fa
        L_0x036f:
            r12.discardTap = r1
            im.bclpbkiauv.ui.components.Scroller r0 = r12.scroller
            boolean r0 = r0.isFinished()
            if (r0 != 0) goto L_0x037e
            im.bclpbkiauv.ui.components.Scroller r0 = r12.scroller
            r0.abortAnimation()
        L_0x037e:
            boolean r0 = r12.draggingDown
            if (r0 != 0) goto L_0x03fa
            boolean r0 = r12.changingPage
            if (r0 != 0) goto L_0x03fa
            boolean r0 = r12.canZoom
            if (r0 == 0) goto L_0x03db
            int r0 = r13.getPointerCount()
            if (r0 != r6) goto L_0x03db
            float r0 = r13.getX(r2)
            float r4 = r13.getX(r1)
            float r0 = r0 - r4
            double r4 = (double) r0
            float r0 = r13.getY(r2)
            float r6 = r13.getY(r1)
            float r0 = r0 - r6
            double r6 = (double) r0
            double r4 = java.lang.Math.hypot(r4, r6)
            float r0 = (float) r4
            r12.pinchStartDistance = r0
            float r0 = r12.scale
            r12.pinchStartScale = r0
            float r0 = r13.getX(r1)
            float r4 = r13.getX(r2)
            float r0 = r0 + r4
            float r0 = r0 / r3
            r12.pinchCenterX = r0
            float r0 = r13.getY(r1)
            float r4 = r13.getY(r2)
            float r0 = r0 + r4
            float r0 = r0 / r3
            r12.pinchCenterY = r0
            float r0 = r12.translationX
            r12.pinchStartX = r0
            float r0 = r12.translationY
            r12.pinchStartY = r0
            r12.zooming = r2
            r12.moving = r1
            android.view.VelocityTracker r0 = r12.velocityTracker
            if (r0 == 0) goto L_0x03fa
            r0.clear()
            goto L_0x03fa
        L_0x03db:
            int r0 = r13.getPointerCount()
            if (r0 != r2) goto L_0x03fa
            float r0 = r13.getX()
            r12.moveStartX = r0
            float r0 = r13.getY()
            r12.moveStartY = r0
            r12.dragY = r0
            r12.draggingDown = r1
            r12.canDragDown = r2
            android.view.VelocityTracker r0 = r12.velocityTracker
            if (r0 == 0) goto L_0x03fa
            r0.clear()
        L_0x03fa:
            return r1
        L_0x03fb:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.processTouchEvent(android.view.MotionEvent):boolean");
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
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f})});
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) duration);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = ArticleViewer.this.imageMoveAnimation = null;
                    ArticleViewer.this.photoContainerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    public void setAnimationValue(float value) {
        this.animationValue = value;
        this.photoContainerView.invalidate();
    }

    public float getAnimationValue() {
        return this.animationValue;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x034f  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x03b2  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01b0  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0217  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawContent(android.graphics.Canvas r30) {
        /*
            r29 = this;
            r0 = r29
            r1 = r30
            int r2 = r0.photoAnimationInProgress
            r3 = 1
            if (r2 == r3) goto L_0x04b7
            boolean r4 = r0.isPhotoVisible
            r5 = 2
            if (r4 != 0) goto L_0x0012
            if (r2 == r5) goto L_0x0012
            goto L_0x04b7
        L_0x0012:
            r2 = -1082130432(0xffffffffbf800000, float:-1.0)
            android.animation.AnimatorSet r4 = r0.imageMoveAnimation
            r6 = 0
            r7 = 0
            r8 = 1065353216(0x3f800000, float:1.0)
            if (r4 == 0) goto L_0x005b
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            boolean r4 = r4.isFinished()
            if (r4 != 0) goto L_0x0029
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            r4.abortAnimation()
        L_0x0029:
            float r4 = r0.scale
            float r9 = r0.animateToScale
            float r10 = r9 - r4
            float r11 = r0.animationValue
            float r10 = r10 * r11
            float r10 = r10 + r4
            float r12 = r0.translationX
            float r13 = r0.animateToX
            float r13 = r13 - r12
            float r13 = r13 * r11
            float r13 = r13 + r12
            float r14 = r0.translationY
            float r15 = r0.animateToY
            float r15 = r15 - r14
            float r15 = r15 * r11
            float r14 = r14 + r15
            int r9 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r9 != 0) goto L_0x0051
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 != 0) goto L_0x0051
            int r4 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r4 != 0) goto L_0x0051
            r2 = r14
        L_0x0051:
            r4 = r10
            r9 = r14
            r11 = r13
            im.bclpbkiauv.ui.ArticleViewer$FrameLayoutDrawer r12 = r0.photoContainerView
            r12.invalidate()
            goto L_0x00f8
        L_0x005b:
            long r9 = r0.animationStartTime
            r11 = 0
            int r4 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r4 == 0) goto L_0x0076
            float r4 = r0.animateToX
            r0.translationX = r4
            float r4 = r0.animateToY
            r0.translationY = r4
            float r4 = r0.animateToScale
            r0.scale = r4
            r0.animationStartTime = r11
            r0.updateMinMax(r4)
            r0.zoomAnimation = r7
        L_0x0076:
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            boolean r4 = r4.isFinished()
            if (r4 != 0) goto L_0x00d1
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            boolean r4 = r4.computeScrollOffset()
            if (r4 == 0) goto L_0x00d1
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            int r4 = r4.getStartX()
            float r4 = (float) r4
            float r9 = r0.maxX
            int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r4 >= 0) goto L_0x00a9
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            int r4 = r4.getStartX()
            float r4 = (float) r4
            float r9 = r0.minX
            int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r4 <= 0) goto L_0x00a9
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            int r4 = r4.getCurrX()
            float r4 = (float) r4
            r0.translationX = r4
        L_0x00a9:
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            int r4 = r4.getStartY()
            float r4 = (float) r4
            float r9 = r0.maxY
            int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r4 >= 0) goto L_0x00cc
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            int r4 = r4.getStartY()
            float r4 = (float) r4
            float r9 = r0.minY
            int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r4 <= 0) goto L_0x00cc
            im.bclpbkiauv.ui.components.Scroller r4 = r0.scroller
            int r4 = r4.getCurrY()
            float r4 = (float) r4
            r0.translationY = r4
        L_0x00cc:
            im.bclpbkiauv.ui.ArticleViewer$FrameLayoutDrawer r4 = r0.photoContainerView
            r4.invalidate()
        L_0x00d1:
            int r4 = r0.switchImageAfterAnimation
            if (r4 == 0) goto L_0x00ec
            if (r4 != r3) goto L_0x00e0
            im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$vehFkTpnzezPjtpaX0cf4uOM9no r4 = new im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$vehFkTpnzezPjtpaX0cf4uOM9no
            r4.<init>()
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r4)
            goto L_0x00ea
        L_0x00e0:
            if (r4 != r5) goto L_0x00ea
            im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$4OpWbb0ks8gegxAAwjHmHc34B5U r4 = new im.bclpbkiauv.ui.-$$Lambda$ArticleViewer$4OpWbb0ks8gegxAAwjHmHc34B5U
            r4.<init>()
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r4)
        L_0x00ea:
            r0.switchImageAfterAnimation = r7
        L_0x00ec:
            float r4 = r0.scale
            float r9 = r0.translationY
            float r11 = r0.translationX
            boolean r10 = r0.moving
            if (r10 != 0) goto L_0x00f8
            float r2 = r0.translationY
        L_0x00f8:
            int r10 = r0.photoAnimationInProgress
            if (r10 == r5) goto L_0x0137
            float r10 = r0.scale
            int r10 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x0130
            r10 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r10 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r10 == 0) goto L_0x0130
            boolean r10 = r0.zoomAnimation
            if (r10 != 0) goto L_0x0130
            int r10 = r29.getContainerViewHeight()
            float r10 = (float) r10
            r12 = 1082130432(0x40800000, float:4.0)
            float r10 = r10 / r12
            im.bclpbkiauv.ui.ArticleViewer$PhotoBackgroundDrawable r12 = r0.photoBackgroundDrawable
            r13 = 1123942400(0x42fe0000, float:127.0)
            r14 = 1132396544(0x437f0000, float:255.0)
            float r15 = java.lang.Math.abs(r2)
            float r15 = java.lang.Math.min(r15, r10)
            float r15 = r15 / r10
            float r15 = r8 - r15
            float r15 = r15 * r14
            float r13 = java.lang.Math.max(r13, r15)
            int r13 = (int) r13
            r12.setAlpha(r13)
            goto L_0x0137
        L_0x0130:
            im.bclpbkiauv.ui.ArticleViewer$PhotoBackgroundDrawable r10 = r0.photoBackgroundDrawable
            r12 = 255(0xff, float:3.57E-43)
            r10.setAlpha(r12)
        L_0x0137:
            r10 = 0
            float r12 = r0.scale
            int r12 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r12 < 0) goto L_0x016b
            boolean r12 = r0.zoomAnimation
            if (r12 != 0) goto L_0x016b
            boolean r12 = r0.zooming
            if (r12 != 0) goto L_0x016b
            float r12 = r0.maxX
            r13 = 1084227584(0x40a00000, float:5.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r14 = (float) r14
            float r12 = r12 + r14
            int r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r12 <= 0) goto L_0x0157
            im.bclpbkiauv.messenger.ImageReceiver r10 = r0.leftImage
            goto L_0x016b
        L_0x0157:
            float r12 = r0.minX
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r12 = r12 - r13
            int r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r12 >= 0) goto L_0x0166
            im.bclpbkiauv.messenger.ImageReceiver r10 = r0.rightImage
            goto L_0x016b
        L_0x0166:
            im.bclpbkiauv.ui.components.GroupedPhotosListView r12 = r0.groupedPhotosListView
            r12.setMoveProgress(r6)
        L_0x016b:
            if (r10 == 0) goto L_0x016f
            r12 = 1
            goto L_0x0170
        L_0x016f:
            r12 = 0
        L_0x0170:
            r0.changingPage = r12
            im.bclpbkiauv.messenger.ImageReceiver r12 = r0.rightImage
            r13 = 1050253722(0x3e99999a, float:0.3)
            r15 = 1106247680(0x41f00000, float:30.0)
            if (r10 != r12) goto L_0x0261
            r12 = r11
            r16 = 0
            r17 = 1065353216(0x3f800000, float:1.0)
            boolean r7 = r0.zoomAnimation
            if (r7 != 0) goto L_0x01a8
            float r7 = r0.minX
            int r19 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r19 >= 0) goto L_0x01a8
            float r7 = r7 - r12
            int r3 = r30.getWidth()
            float r3 = (float) r3
            float r7 = r7 / r3
            float r17 = java.lang.Math.min(r8, r7)
            float r3 = r8 - r17
            float r16 = r3 * r13
            int r3 = r30.getWidth()
            int r3 = -r3
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            int r7 = r7 / r5
            int r3 = r3 - r7
            float r12 = (float) r3
            r3 = r17
            goto L_0x01aa
        L_0x01a8:
            r3 = r17
        L_0x01aa:
            boolean r7 = r10.hasBitmapImage()
            if (r7 == 0) goto L_0x0217
            r30.save()
            int r7 = r29.getContainerViewWidth()
            int r7 = r7 / r5
            float r7 = (float) r7
            int r17 = r29.getContainerViewHeight()
            int r13 = r17 / 2
            float r13 = (float) r13
            r1.translate(r7, r13)
            int r7 = r30.getWidth()
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            int r13 = r13 / r5
            int r7 = r7 + r13
            float r7 = (float) r7
            float r7 = r7 + r12
            r1.translate(r7, r6)
            float r7 = r8 - r16
            float r13 = r8 - r16
            r1.scale(r7, r13)
            int r7 = r10.getBitmapWidth()
            int r13 = r10.getBitmapHeight()
            int r6 = r29.getContainerViewWidth()
            float r6 = (float) r6
            float r14 = (float) r7
            float r6 = r6 / r14
            int r14 = r29.getContainerViewHeight()
            float r14 = (float) r14
            float r15 = (float) r13
            float r14 = r14 / r15
            int r15 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r15 <= 0) goto L_0x01f5
            r15 = r14
            goto L_0x01f6
        L_0x01f5:
            r15 = r6
        L_0x01f6:
            float r8 = (float) r7
            float r8 = r8 * r15
            int r8 = (int) r8
            float r5 = (float) r13
            float r5 = r5 * r15
            int r5 = (int) r5
            r10.setAlpha(r3)
            r23 = r2
            int r2 = -r8
            r22 = 2
            int r2 = r2 / 2
            r24 = r6
            int r6 = -r5
            int r6 = r6 / 2
            r10.setImageCoords(r2, r6, r8, r5)
            r10.draw(r1)
            r30.restore()
            goto L_0x0219
        L_0x0217:
            r23 = r2
        L_0x0219:
            im.bclpbkiauv.ui.components.GroupedPhotosListView r2 = r0.groupedPhotosListView
            float r5 = -r3
            r2.setMoveProgress(r5)
            r30.save()
            float r2 = r9 / r4
            r1.translate(r12, r2)
            int r2 = r30.getWidth()
            float r2 = (float) r2
            float r5 = r0.scale
            r6 = 1065353216(0x3f800000, float:1.0)
            float r5 = r5 + r6
            float r2 = r2 * r5
            r5 = 1106247680(0x41f00000, float:30.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r6
            float r2 = r2 + r5
            r5 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r5
            float r5 = -r9
            float r5 = r5 / r4
            r1.translate(r2, r5)
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r2 = r0.radialProgressViews
            r5 = 1
            r2 = r2[r5]
            r6 = 1065353216(0x3f800000, float:1.0)
            float r8 = r6 - r16
            r2.setScale(r8)
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r2 = r0.radialProgressViews
            r2 = r2[r5]
            r2.setAlpha(r3)
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r2 = r0.radialProgressViews
            r2 = r2[r5]
            r2.onDraw(r1)
            r30.restore()
            goto L_0x0263
        L_0x0261:
            r23 = r2
        L_0x0263:
            r2 = r11
            r3 = 0
            r5 = 1065353216(0x3f800000, float:1.0)
            boolean r6 = r0.zoomAnimation
            if (r6 != 0) goto L_0x0288
            float r6 = r0.maxX
            int r7 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r7 <= 0) goto L_0x0288
            float r6 = r2 - r6
            int r7 = r30.getWidth()
            float r7 = (float) r7
            float r6 = r6 / r7
            r7 = 1065353216(0x3f800000, float:1.0)
            float r5 = java.lang.Math.min(r7, r6)
            r6 = 1050253722(0x3e99999a, float:0.3)
            float r3 = r5 * r6
            float r5 = r7 - r5
            float r2 = r0.maxX
        L_0x0288:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r6 = r0.aspectRatioFrameLayout
            if (r6 == 0) goto L_0x0294
            int r6 = r6.getVisibility()
            if (r6 != 0) goto L_0x0294
            r6 = 1
            goto L_0x0295
        L_0x0294:
            r6 = 0
        L_0x0295:
            im.bclpbkiauv.messenger.ImageReceiver r7 = r0.centerImage
            boolean r7 = r7.hasBitmapImage()
            if (r7 == 0) goto L_0x03b7
            r30.save()
            int r7 = r29.getContainerViewWidth()
            r8 = 2
            int r7 = r7 / r8
            float r7 = (float) r7
            int r12 = r29.getContainerViewHeight()
            int r12 = r12 / r8
            float r8 = (float) r12
            r1.translate(r7, r8)
            r1.translate(r2, r9)
            float r7 = r4 - r3
            float r8 = r4 - r3
            r1.scale(r7, r8)
            im.bclpbkiauv.messenger.ImageReceiver r7 = r0.centerImage
            int r7 = r7.getBitmapWidth()
            im.bclpbkiauv.messenger.ImageReceiver r8 = r0.centerImage
            int r8 = r8.getBitmapHeight()
            if (r6 == 0) goto L_0x02f7
            boolean r12 = r0.textureUploaded
            if (r12 == 0) goto L_0x02f7
            float r12 = (float) r7
            float r13 = (float) r8
            float r12 = r12 / r13
            android.view.TextureView r13 = r0.videoTextureView
            int r13 = r13.getMeasuredWidth()
            float r13 = (float) r13
            android.view.TextureView r14 = r0.videoTextureView
            int r14 = r14.getMeasuredHeight()
            float r14 = (float) r14
            float r13 = r13 / r14
            float r14 = r12 - r13
            float r14 = java.lang.Math.abs(r14)
            r15 = 1008981770(0x3c23d70a, float:0.01)
            int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
            if (r14 <= 0) goto L_0x02f7
            android.view.TextureView r14 = r0.videoTextureView
            int r7 = r14.getMeasuredWidth()
            android.view.TextureView r14 = r0.videoTextureView
            int r8 = r14.getMeasuredHeight()
        L_0x02f7:
            int r12 = r29.getContainerViewWidth()
            float r12 = (float) r12
            float r13 = (float) r7
            float r12 = r12 / r13
            int r13 = r29.getContainerViewHeight()
            float r13 = (float) r13
            float r14 = (float) r8
            float r13 = r13 / r14
            int r14 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1))
            if (r14 <= 0) goto L_0x030b
            r14 = r13
            goto L_0x030c
        L_0x030b:
            r14 = r12
        L_0x030c:
            float r15 = (float) r7
            float r15 = r15 * r14
            int r15 = (int) r15
            r16 = r7
            float r7 = (float) r8
            float r7 = r7 * r14
            int r7 = (int) r7
            if (r6 == 0) goto L_0x0330
            r20 = r8
            boolean r8 = r0.textureUploaded
            if (r8 == 0) goto L_0x0332
            boolean r8 = r0.videoCrossfadeStarted
            if (r8 == 0) goto L_0x0332
            float r8 = r0.videoCrossfadeAlpha
            r21 = 1065353216(0x3f800000, float:1.0)
            int r8 = (r8 > r21 ? 1 : (r8 == r21 ? 0 : -1))
            if (r8 == 0) goto L_0x032b
            goto L_0x0332
        L_0x032b:
            r24 = r12
            r25 = r13
            goto L_0x034d
        L_0x0330:
            r20 = r8
        L_0x0332:
            im.bclpbkiauv.messenger.ImageReceiver r8 = r0.centerImage
            r8.setAlpha(r5)
            im.bclpbkiauv.messenger.ImageReceiver r8 = r0.centerImage
            r24 = r12
            int r12 = -r15
            r22 = 2
            int r12 = r12 / 2
            r25 = r13
            int r13 = -r7
            int r13 = r13 / 2
            r8.setImageCoords(r12, r13, r15, r7)
            im.bclpbkiauv.messenger.ImageReceiver r8 = r0.centerImage
            r8.draw(r1)
        L_0x034d:
            if (r6 == 0) goto L_0x03b2
            boolean r8 = r0.videoCrossfadeStarted
            if (r8 != 0) goto L_0x0363
            boolean r8 = r0.textureUploaded
            if (r8 == 0) goto L_0x0363
            r8 = 1
            r0.videoCrossfadeStarted = r8
            r8 = 0
            r0.videoCrossfadeAlpha = r8
            long r12 = java.lang.System.currentTimeMillis()
            r0.videoCrossfadeAlphaLastTime = r12
        L_0x0363:
            int r8 = -r15
            r12 = 2
            int r8 = r8 / r12
            float r8 = (float) r8
            int r13 = -r7
            int r13 = r13 / r12
            float r12 = (float) r13
            r1.translate(r8, r12)
            android.view.TextureView r8 = r0.videoTextureView
            float r12 = r0.videoCrossfadeAlpha
            float r12 = r12 * r5
            r8.setAlpha(r12)
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r8 = r0.aspectRatioFrameLayout
            r8.draw(r1)
            boolean r8 = r0.videoCrossfadeStarted
            if (r8 == 0) goto L_0x03af
            float r8 = r0.videoCrossfadeAlpha
            r12 = 1065353216(0x3f800000, float:1.0)
            int r8 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r8 >= 0) goto L_0x03af
            long r12 = java.lang.System.currentTimeMillis()
            r19 = r7
            long r7 = r0.videoCrossfadeAlphaLastTime
            long r7 = r12 - r7
            r0.videoCrossfadeAlphaLastTime = r12
            r26 = r12
            float r12 = r0.videoCrossfadeAlpha
            float r13 = (float) r7
            r28 = 1133903872(0x43960000, float:300.0)
            float r13 = r13 / r28
            float r12 = r12 + r13
            r0.videoCrossfadeAlpha = r12
            im.bclpbkiauv.ui.ArticleViewer$FrameLayoutDrawer r12 = r0.photoContainerView
            r12.invalidate()
            float r12 = r0.videoCrossfadeAlpha
            r13 = 1065353216(0x3f800000, float:1.0)
            int r12 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1))
            if (r12 <= 0) goto L_0x03b4
            r0.videoCrossfadeAlpha = r13
            goto L_0x03b4
        L_0x03af:
            r19 = r7
            goto L_0x03b4
        L_0x03b2:
            r19 = r7
        L_0x03b4:
            r30.restore()
        L_0x03b7:
            if (r6 != 0) goto L_0x03e6
            android.widget.FrameLayout r7 = r0.bottomLayout
            int r7 = r7.getVisibility()
            if (r7 == 0) goto L_0x03e6
            r30.save()
            float r7 = r9 / r4
            r1.translate(r2, r7)
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r7 = r0.radialProgressViews
            r8 = 0
            r7 = r7[r8]
            r12 = 1065353216(0x3f800000, float:1.0)
            float r13 = r12 - r3
            r7.setScale(r13)
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r7 = r0.radialProgressViews
            r7 = r7[r8]
            r7.setAlpha(r5)
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r7 = r0.radialProgressViews
            r7 = r7[r8]
            r7.onDraw(r1)
            r30.restore()
        L_0x03e6:
            im.bclpbkiauv.messenger.ImageReceiver r7 = r0.leftImage
            if (r10 != r7) goto L_0x04b0
            boolean r7 = r10.hasBitmapImage()
            if (r7 == 0) goto L_0x0462
            r30.save()
            int r7 = r29.getContainerViewWidth()
            r8 = 2
            int r7 = r7 / r8
            float r7 = (float) r7
            int r12 = r29.getContainerViewHeight()
            int r12 = r12 / r8
            float r8 = (float) r12
            r1.translate(r7, r8)
            int r7 = r30.getWidth()
            float r7 = (float) r7
            float r8 = r0.scale
            r12 = 1065353216(0x3f800000, float:1.0)
            float r8 = r8 + r12
            float r7 = r7 * r8
            r8 = 1106247680(0x41f00000, float:30.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r12
            float r7 = r7 + r8
            float r7 = -r7
            r8 = 1073741824(0x40000000, float:2.0)
            float r7 = r7 / r8
            float r7 = r7 + r11
            r8 = 0
            r1.translate(r7, r8)
            int r7 = r10.getBitmapWidth()
            int r8 = r10.getBitmapHeight()
            int r12 = r29.getContainerViewWidth()
            float r12 = (float) r12
            float r13 = (float) r7
            float r12 = r12 / r13
            int r13 = r29.getContainerViewHeight()
            float r13 = (float) r13
            float r14 = (float) r8
            float r13 = r13 / r14
            int r14 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1))
            if (r14 <= 0) goto L_0x043c
            r14 = r13
            goto L_0x043d
        L_0x043c:
            r14 = r12
        L_0x043d:
            float r15 = (float) r7
            float r15 = r15 * r14
            int r15 = (int) r15
            r16 = r2
            float r2 = (float) r8
            float r2 = r2 * r14
            int r2 = (int) r2
            r17 = r3
            r3 = 1065353216(0x3f800000, float:1.0)
            r10.setAlpha(r3)
            int r3 = -r15
            r18 = 2
            int r3 = r3 / 2
            r19 = r6
            int r6 = -r2
            int r6 = r6 / 2
            r10.setImageCoords(r3, r6, r15, r2)
            r10.draw(r1)
            r30.restore()
            goto L_0x0468
        L_0x0462:
            r16 = r2
            r17 = r3
            r19 = r6
        L_0x0468:
            im.bclpbkiauv.ui.components.GroupedPhotosListView r2 = r0.groupedPhotosListView
            r3 = 1065353216(0x3f800000, float:1.0)
            float r8 = r3 - r5
            r2.setMoveProgress(r8)
            r30.save()
            float r2 = r9 / r4
            r1.translate(r11, r2)
            int r2 = r30.getWidth()
            float r2 = (float) r2
            float r6 = r0.scale
            float r6 = r6 + r3
            float r2 = r2 * r6
            r3 = 1106247680(0x41f00000, float:30.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 + r3
            float r2 = -r2
            r3 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r3
            float r3 = -r9
            float r3 = r3 / r4
            r1.translate(r2, r3)
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r2 = r0.radialProgressViews
            r3 = 2
            r2 = r2[r3]
            r6 = 1065353216(0x3f800000, float:1.0)
            r2.setScale(r6)
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r2 = r0.radialProgressViews
            r2 = r2[r3]
            r2.setAlpha(r6)
            im.bclpbkiauv.ui.ArticleViewer$RadialProgressView[] r2 = r0.radialProgressViews
            r2 = r2[r3]
            r2.onDraw(r1)
            r30.restore()
            goto L_0x04b6
        L_0x04b0:
            r16 = r2
            r17 = r3
            r19 = r6
        L_0x04b6:
            return
        L_0x04b7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ArticleViewer.drawContent(android.graphics.Canvas):void");
    }

    public /* synthetic */ void lambda$drawContent$42$ArticleViewer() {
        setImageIndex(this.currentIndex + 1, false);
    }

    public /* synthetic */ void lambda$drawContent$43$ArticleViewer() {
        setImageIndex(this.currentIndex - 1, false);
    }

    private void onActionClick(boolean download) {
        TLObject media = getMedia(this.currentIndex);
        if ((media instanceof TLRPC.Document) && this.currentFileNames[0] != null) {
            TLRPC.Document document = (TLRPC.Document) media;
            File file = null;
            if (!(this.currentMedia == null || (file = getMediaFile(this.currentIndex)) == null || file.exists())) {
                file = null;
            }
            if (file != null) {
                preparePlayer(file, true);
            } else if (!download) {
            } else {
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                    FileLoader.getInstance(this.currentAccount).loadFile(document, this.currentPage, 1, 1);
                } else {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
                }
            }
        }
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
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
        this.photoContainerView.postInvalidate();
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        int state;
        if (this.discardTap) {
            return false;
        }
        AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
        boolean drawTextureView = aspectRatioFrameLayout2 != null && aspectRatioFrameLayout2.getVisibility() == 0;
        RadialProgressView[] radialProgressViewArr = this.radialProgressViews;
        if (radialProgressViewArr[0] != null && this.photoContainerView != null && !drawTextureView && (state = radialProgressViewArr[0].backgroundState) > 0 && state <= 3) {
            float x = e.getX();
            float y = e.getY();
            if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                onActionClick(true);
                checkProgress(0, true);
                return true;
            }
        }
        toggleActionBar(!this.isActionBarVisible, true);
        return true;
    }

    public boolean onDoubleTap(MotionEvent e) {
        if (!this.canZoom || ((this.scale == 1.0f && (this.translationY != 0.0f || this.translationX != 0.0f)) || this.animationStartTime != 0 || this.photoAnimationInProgress != 0)) {
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

    private ImageReceiver getImageReceiverView(View view, TLRPC.PageBlock pageBlock, int[] coords2) {
        ImageReceiver imageReceiver;
        ImageReceiver imageReceiver2;
        if (view instanceof BlockPhotoCell) {
            BlockPhotoCell cell = (BlockPhotoCell) view;
            if (cell.currentBlock != pageBlock) {
                return null;
            }
            view.getLocationInWindow(coords2);
            return cell.imageView;
        } else if (view instanceof BlockVideoCell) {
            BlockVideoCell cell2 = (BlockVideoCell) view;
            if (cell2.currentBlock != pageBlock) {
                return null;
            }
            view.getLocationInWindow(coords2);
            return cell2.imageView;
        } else if (view instanceof BlockCollageCell) {
            ImageReceiver imageReceiver3 = getImageReceiverFromListView(((BlockCollageCell) view).innerListView, pageBlock, coords2);
            if (imageReceiver3 != null) {
                return imageReceiver3;
            }
            return null;
        } else if (view instanceof BlockSlideshowCell) {
            ImageReceiver imageReceiver4 = getImageReceiverFromListView(((BlockSlideshowCell) view).innerListView, pageBlock, coords2);
            if (imageReceiver4 != null) {
                return imageReceiver4;
            }
            return null;
        } else if (view instanceof BlockListItemCell) {
            BlockListItemCell blockListItemCell = (BlockListItemCell) view;
            if (blockListItemCell.blockLayout == null || (imageReceiver2 = getImageReceiverView(blockListItemCell.blockLayout.itemView, pageBlock, coords2)) == null) {
                return null;
            }
            return imageReceiver2;
        } else if (!(view instanceof BlockOrderedListItemCell)) {
            return null;
        } else {
            BlockOrderedListItemCell blockOrderedListItemCell = (BlockOrderedListItemCell) view;
            if (blockOrderedListItemCell.blockLayout == null || (imageReceiver = getImageReceiverView(blockOrderedListItemCell.blockLayout.itemView, pageBlock, coords2)) == null) {
                return null;
            }
            return imageReceiver;
        }
    }

    private ImageReceiver getImageReceiverFromListView(ViewGroup listView2, TLRPC.PageBlock pageBlock, int[] coords2) {
        int count = listView2.getChildCount();
        for (int a = 0; a < count; a++) {
            ImageReceiver imageReceiver = getImageReceiverView(listView2.getChildAt(a), pageBlock, coords2);
            if (imageReceiver != null) {
                return imageReceiver;
            }
        }
        return null;
    }

    private PlaceProviderObject getPlaceForPhoto(TLRPC.PageBlock pageBlock) {
        ImageReceiver imageReceiver = getImageReceiverFromListView(this.listView[0], pageBlock, this.coords);
        if (imageReceiver == null) {
            return null;
        }
        PlaceProviderObject object = new PlaceProviderObject();
        object.viewX = this.coords[0];
        object.viewY = this.coords[1];
        object.parentView = this.listView[0];
        object.imageReceiver = imageReceiver;
        object.thumb = imageReceiver.getBitmapSafe();
        object.radius = imageReceiver.getRoundRadius();
        object.clipTopAddition = this.currentHeaderHeight;
        return object;
    }
}
