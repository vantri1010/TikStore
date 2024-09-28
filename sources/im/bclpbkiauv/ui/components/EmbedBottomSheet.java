package im.bclpbkiauv.ui.components;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BringAppForegroundService;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.components.EmbedBottomSheet;
import im.bclpbkiauv.ui.components.toast.ToastUtils;

public class EmbedBottomSheet extends BottomSheet {
    /* access modifiers changed from: private */
    public static EmbedBottomSheet instance;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    private FrameLayout containerLayout;
    /* access modifiers changed from: private */
    public TextView copyTextButton;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public WebChromeClient.CustomViewCallback customViewCallback;
    /* access modifiers changed from: private */
    public String embedUrl;
    /* access modifiers changed from: private */
    public FrameLayout fullscreenVideoContainer;
    /* access modifiers changed from: private */
    public boolean fullscreenedByButton;
    /* access modifiers changed from: private */
    public boolean hasDescription;
    /* access modifiers changed from: private */
    public int height;
    /* access modifiers changed from: private */
    public LinearLayout imageButtonsContainer;
    /* access modifiers changed from: private */
    public boolean isYouTube;
    private int lastOrientation = -1;
    /* access modifiers changed from: private */
    public DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener() {
        public void onShow(DialogInterface dialog) {
            if (EmbedBottomSheet.this.pipVideoView != null && EmbedBottomSheet.this.videoView.isInline()) {
                EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }
        }
    };
    /* access modifiers changed from: private */
    public String openUrl;
    /* access modifiers changed from: private */
    public OrientationEventListener orientationEventListener;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public ImageView pipButton;
    /* access modifiers changed from: private */
    public PipVideoView pipVideoView;
    /* access modifiers changed from: private */
    public int[] position = new int[2];
    /* access modifiers changed from: private */
    public int prevOrientation = -2;
    /* access modifiers changed from: private */
    public RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public View progressBarBlackBackground;
    /* access modifiers changed from: private */
    public int seekTimeOverride;
    /* access modifiers changed from: private */
    public WebPlayerView videoView;
    /* access modifiers changed from: private */
    public int waitingForDraw;
    /* access modifiers changed from: private */
    public boolean wasInLandscape;
    /* access modifiers changed from: private */
    public WebView webView;
    /* access modifiers changed from: private */
    public int width;
    private final String youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>";

    private class YoutubeProxy {
        private YoutubeProxy() {
        }

        @JavascriptInterface
        public void postEvent(String eventName, String eventData) {
            if ("loaded".equals(eventName)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        EmbedBottomSheet.YoutubeProxy.this.lambda$postEvent$0$EmbedBottomSheet$YoutubeProxy();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$postEvent$0$EmbedBottomSheet$YoutubeProxy() {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
        }
    }

    public static void show(Context context, String title, String description, String originalUrl, String url, int w, int h) {
        show(context, title, description, originalUrl, url, w, h, -1);
    }

    public static void show(Context context, String title, String description, String originalUrl, String url, int w, int h, int seekTime) {
        EmbedBottomSheet embedBottomSheet = instance;
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        new EmbedBottomSheet(context, title, description, originalUrl, url, w, h, seekTime).show();
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private EmbedBottomSheet(android.content.Context r33, java.lang.String r34, java.lang.String r35, java.lang.String r36, java.lang.String r37, int r38, int r39, int r40) {
        /*
            r32 = this;
            r0 = r32
            r1 = r33
            r2 = r35
            r3 = r38
            r4 = r39
            r5 = 0
            r0.<init>(r1, r5, r5)
            r6 = 2
            int[] r7 = new int[r6]
            r0.position = r7
            r7 = -1
            r0.lastOrientation = r7
            r8 = -2
            r0.prevOrientation = r8
            java.lang.String r9 = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>"
            r0.youtubeFrame = r9
            im.bclpbkiauv.ui.components.EmbedBottomSheet$1 r9 = new im.bclpbkiauv.ui.components.EmbedBottomSheet$1
            r9.<init>()
            r0.onShowListener = r9
            r9 = 1
            r0.fullWidth = r9
            r0.setApplyTopPadding(r5)
            r0.setApplyBottomPadding(r5)
            r10 = r40
            r0.seekTimeOverride = r10
            boolean r11 = r1 instanceof android.app.Activity
            if (r11 == 0) goto L_0x003a
            r11 = r1
            android.app.Activity r11 = (android.app.Activity) r11
            r0.parentActivity = r11
        L_0x003a:
            r11 = r37
            r0.embedUrl = r11
            if (r2 == 0) goto L_0x0048
            int r12 = r35.length()
            if (r12 <= 0) goto L_0x0048
            r12 = 1
            goto L_0x0049
        L_0x0048:
            r12 = 0
        L_0x0049:
            r0.hasDescription = r12
            r12 = r36
            r0.openUrl = r12
            r0.width = r3
            r0.height = r4
            if (r3 == 0) goto L_0x0057
            if (r4 != 0) goto L_0x0064
        L_0x0057:
            android.graphics.Point r13 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r13 = r13.x
            r0.width = r13
            android.graphics.Point r13 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r13 = r13.y
            int r13 = r13 / r6
            r0.height = r13
        L_0x0064:
            android.widget.FrameLayout r13 = new android.widget.FrameLayout
            r13.<init>(r1)
            r0.fullscreenVideoContainer = r13
            r13.setKeepScreenOn(r9)
            android.widget.FrameLayout r13 = r0.fullscreenVideoContainer
            r14 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            r13.setBackgroundColor(r14)
            int r13 = android.os.Build.VERSION.SDK_INT
            r15 = 21
            if (r13 < r15) goto L_0x0080
            android.widget.FrameLayout r13 = r0.fullscreenVideoContainer
            r13.setFitsSystemWindows(r9)
        L_0x0080:
            android.widget.FrameLayout r13 = r0.fullscreenVideoContainer
            im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$AcpYSdxgkb5xlg5mNT-c7MDI0yg r8 = im.bclpbkiauv.ui.components.$$Lambda$EmbedBottomSheet$AcpYSdxgkb5xlg5mNTc7MDI0yg.INSTANCE
            r13.setOnTouchListener(r8)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r8 = r0.container
            android.widget.FrameLayout r13 = r0.fullscreenVideoContainer
            r6 = -1082130432(0xffffffffbf800000, float:-1.0)
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r7, r6)
            r8.addView(r13, r6)
            android.widget.FrameLayout r6 = r0.fullscreenVideoContainer
            r8 = 4
            r6.setVisibility(r8)
            android.widget.FrameLayout r6 = r0.fullscreenVideoContainer
            im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$Ja3YkunJeZv6qBe_fCW-4BNEDxY r13 = im.bclpbkiauv.ui.components.$$Lambda$EmbedBottomSheet$Ja3YkunJeZv6qBe_fCW4BNEDxY.INSTANCE
            r6.setOnTouchListener(r13)
            im.bclpbkiauv.ui.components.EmbedBottomSheet$2 r6 = new im.bclpbkiauv.ui.components.EmbedBottomSheet$2
            r6.<init>(r1)
            r0.containerLayout = r6
            im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$g-VZgqtgqxPzx4mad4gsPFw8EDM r13 = im.bclpbkiauv.ui.components.$$Lambda$EmbedBottomSheet$gVZgqtgqxPzx4mad4gsPFw8EDM.INSTANCE
            r6.setOnTouchListener(r13)
            android.widget.FrameLayout r6 = r0.containerLayout
            r0.setCustomView(r6)
            android.webkit.WebView r6 = new android.webkit.WebView
            r6.<init>(r1)
            r0.webView = r6
            android.webkit.WebSettings r6 = r6.getSettings()
            r6.setJavaScriptEnabled(r9)
            android.webkit.WebView r6 = r0.webView
            android.webkit.WebSettings r6 = r6.getSettings()
            r6.setDomStorageEnabled(r9)
            int r6 = android.os.Build.VERSION.SDK_INT
            r13 = 17
            if (r6 < r13) goto L_0x00d8
            android.webkit.WebView r6 = r0.webView
            android.webkit.WebSettings r6 = r6.getSettings()
            r6.setMediaPlaybackRequiresUserGesture(r5)
        L_0x00d8:
            int r6 = android.os.Build.VERSION.SDK_INT
            if (r6 < r15) goto L_0x00ee
            android.webkit.WebView r6 = r0.webView
            android.webkit.WebSettings r6 = r6.getSettings()
            r6.setMixedContentMode(r5)
            android.webkit.CookieManager r6 = android.webkit.CookieManager.getInstance()
            android.webkit.WebView r15 = r0.webView
            r6.setAcceptThirdPartyCookies(r15, r9)
        L_0x00ee:
            android.webkit.WebView r6 = r0.webView
            im.bclpbkiauv.ui.components.EmbedBottomSheet$3 r15 = new im.bclpbkiauv.ui.components.EmbedBottomSheet$3
            r15.<init>()
            r6.setWebChromeClient(r15)
            android.webkit.WebView r6 = r0.webView
            im.bclpbkiauv.ui.components.EmbedBottomSheet$4 r15 = new im.bclpbkiauv.ui.components.EmbedBottomSheet$4
            r15.<init>()
            r6.setWebViewClient(r15)
            android.widget.FrameLayout r6 = r0.containerLayout
            android.webkit.WebView r15 = r0.webView
            r16 = -1082130432(0xffffffffbf800000, float:-1.0)
            r17 = -1082130432(0xffffffffbf800000, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            boolean r13 = r0.hasDescription
            r23 = 22
            if (r13 == 0) goto L_0x011b
            r13 = 22
            goto L_0x011c
        L_0x011b:
            r13 = 0
        L_0x011c:
            int r13 = r13 + 84
            float r13 = (float) r13
            r22 = r13
            android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r6.addView(r15, r13)
            im.bclpbkiauv.ui.components.WebPlayerView r6 = new im.bclpbkiauv.ui.components.WebPlayerView
            im.bclpbkiauv.ui.components.EmbedBottomSheet$5 r13 = new im.bclpbkiauv.ui.components.EmbedBottomSheet$5
            r13.<init>()
            r6.<init>(r1, r9, r5, r13)
            r0.videoView = r6
            r6.setVisibility(r8)
            android.widget.FrameLayout r6 = r0.containerLayout
            im.bclpbkiauv.ui.components.WebPlayerView r13 = r0.videoView
            r16 = -1082130432(0xffffffffbf800000, float:-1.0)
            r17 = -1082130432(0xffffffffbf800000, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            boolean r15 = r0.hasDescription
            if (r15 == 0) goto L_0x014e
            r15 = 22
            goto L_0x014f
        L_0x014e:
            r15 = 0
        L_0x014f:
            int r15 = r15 + 84
            int r15 = r15 + -10
            float r15 = (float) r15
            r22 = r15
            android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r6.addView(r13, r15)
            android.view.View r6 = new android.view.View
            r6.<init>(r1)
            r0.progressBarBlackBackground = r6
            r6.setBackgroundColor(r14)
            android.view.View r6 = r0.progressBarBlackBackground
            r6.setVisibility(r8)
            android.widget.FrameLayout r6 = r0.containerLayout
            android.view.View r13 = r0.progressBarBlackBackground
            r16 = -1082130432(0xffffffffbf800000, float:-1.0)
            r17 = -1082130432(0xffffffffbf800000, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            boolean r14 = r0.hasDescription
            if (r14 == 0) goto L_0x0183
            r14 = 22
            goto L_0x0184
        L_0x0183:
            r14 = 0
        L_0x0184:
            int r14 = r14 + 84
            float r14 = (float) r14
            r22 = r14
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r6.addView(r13, r14)
            im.bclpbkiauv.ui.components.RadialProgressView r6 = new im.bclpbkiauv.ui.components.RadialProgressView
            r6.<init>(r1)
            r0.progressBar = r6
            r6.setVisibility(r8)
            android.widget.FrameLayout r6 = r0.containerLayout
            im.bclpbkiauv.ui.components.RadialProgressView r13 = r0.progressBar
            r16 = -1073741824(0xffffffffc0000000, float:-2.0)
            r17 = -1073741824(0xffffffffc0000000, float:-2.0)
            r18 = 17
            r19 = 0
            r20 = 0
            r21 = 0
            boolean r14 = r0.hasDescription
            if (r14 == 0) goto L_0x01af
            goto L_0x01b1
        L_0x01af:
            r23 = 0
        L_0x01b1:
            int r23 = r23 + 84
            r14 = 2
            int r14 = r23 / 2
            float r14 = (float) r14
            r22 = r14
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r6.addView(r13, r14)
            boolean r6 = r0.hasDescription
            java.lang.String r13 = "fonts/rmedium.ttf"
            r14 = 1099956224(0x41900000, float:18.0)
            if (r6 == 0) goto L_0x020f
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r15 = 1098907648(0x41800000, float:16.0)
            r6.setTextSize(r9, r15)
            java.lang.String r15 = "dialogTextBlack"
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
            r6.setTextColor(r15)
            r6.setText(r2)
            r6.setSingleLine(r9)
            android.graphics.Typeface r15 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r6.setTypeface(r15)
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r15)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r6.setPadding(r15, r5, r8, r5)
            android.widget.FrameLayout r8 = r0.containerLayout
            r16 = -1082130432(0xffffffffbf800000, float:-1.0)
            r17 = -1073741824(0xffffffffc0000000, float:-2.0)
            r18 = 83
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 1117388800(0x429a0000, float:77.0)
            android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r8.addView(r6, r15)
        L_0x020f:
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r8 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r9, r8)
            java.lang.String r15 = "dialogTextGray"
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
            r6.setTextColor(r15)
            r15 = r34
            r6.setText(r15)
            r6.setSingleLine(r9)
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r8)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r6.setPadding(r8, r5, r7, r5)
            android.widget.FrameLayout r7 = r0.containerLayout
            r23 = -1082130432(0xffffffffbf800000, float:-1.0)
            r24 = -1073741824(0xffffffffc0000000, float:-2.0)
            r25 = 83
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 1113849856(0x42640000, float:57.0)
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r7.addView(r6, r8)
            android.view.View r7 = new android.view.View
            r7.<init>(r1)
            java.lang.String r8 = "dialogGrayLine"
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r7.setBackgroundColor(r8)
            android.widget.FrameLayout r8 = r0.containerLayout
            android.widget.FrameLayout$LayoutParams r14 = new android.widget.FrameLayout$LayoutParams
            r5 = 83
            r2 = -1
            r14.<init>(r2, r9, r5)
            r8.addView(r7, r14)
            android.view.ViewGroup$LayoutParams r2 = r7.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r8 = 1111490560(0x42400000, float:48.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r2.bottomMargin = r8
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            java.lang.String r8 = "dialogBackground"
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r2.setBackgroundColor(r8)
            android.widget.FrameLayout r8 = r0.containerLayout
            r14 = 48
            r9 = -1
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r9, (int) r14, (int) r5)
            r8.addView(r2, r5)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r1)
            r8 = 0
            r5.setOrientation(r8)
            r8 = 1065353216(0x3f800000, float:1.0)
            r5.setWeightSum(r8)
            r8 = 53
            r9 = -2
            r14 = -1
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r9, (int) r14, (int) r8)
            r2.addView(r5, r8)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            r6 = r8
            r8 = 1096810496(0x41600000, float:14.0)
            r9 = 1
            r6.setTextSize(r9, r8)
            java.lang.String r8 = "dialogTextBlue4"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r6.setTextColor(r14)
            r14 = 17
            r6.setGravity(r14)
            r6.setSingleLine(r9)
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r9)
            java.lang.String r9 = "dialogButtonSelector"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            r3 = 0
            android.graphics.drawable.Drawable r14 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r14, r3)
            r6.setBackgroundDrawable(r14)
            r14 = 1099956224(0x41900000, float:18.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r22 = r7
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r6.setPadding(r4, r3, r7, r3)
            r3 = 2131690631(0x7f0f0487, float:1.9010311E38)
            java.lang.String r4 = "Close"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            java.lang.String r3 = r3.toUpperCase()
            r6.setText(r3)
            android.graphics.Typeface r3 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r6.setTypeface(r3)
            r3 = 51
            r4 = -2
            r7 = -1
            android.widget.LinearLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r4, (int) r7, (int) r3)
            r2.addView(r6, r14)
            im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$dXQzFqRzqVMDFpu9OMUKh1pGQTc r4 = new im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$dXQzFqRzqVMDFpu9OMUKh1pGQTc
            r4.<init>()
            r6.setOnClickListener(r4)
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            r4.<init>(r1)
            r0.imageButtonsContainer = r4
            r7 = 4
            r4.setVisibility(r7)
            android.widget.LinearLayout r4 = r0.imageButtonsContainer
            r24 = r6
            r3 = -1
            r7 = 17
            r14 = -2
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r3, (int) r7)
            r2.addView(r4, r6)
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r1)
            r0.pipButton = r3
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r4)
            android.widget.ImageView r3 = r0.pipButton
            r4 = 2131231666(0x7f0803b2, float:1.807942E38)
            r3.setImageResource(r4)
            android.widget.ImageView r3 = r0.pipButton
            r4 = 0
            r3.setEnabled(r4)
            android.widget.ImageView r3 = r0.pipButton
            r4 = 1056964608(0x3f000000, float:0.5)
            r3.setAlpha(r4)
            android.widget.ImageView r3 = r0.pipButton
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r6, r7)
            r3.setColorFilter(r4)
            android.widget.ImageView r3 = r0.pipButton
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            r6 = 0
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r4, r6)
            r3.setBackgroundDrawable(r4)
            android.widget.LinearLayout r3 = r0.imageButtonsContainer
            android.widget.ImageView r4 = r0.pipButton
            r25 = 1111490560(0x42400000, float:48.0)
            r26 = 1111490560(0x42400000, float:48.0)
            r27 = 51
            r29 = 0
            r30 = 1082130432(0x40800000, float:4.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r4, r6)
            android.widget.ImageView r3 = r0.pipButton
            im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$JYJ6YgB9REaWENAetzwMcgnXMLI r4 = new im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$JYJ6YgB9REaWENAetzwMcgnXMLI
            r4.<init>()
            r3.setOnClickListener(r4)
            im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$MZFUABtG5gdPfcKXbuZPbESX9gQ r3 = new im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$MZFUABtG5gdPfcKXbuZPbESX9gQ
            r3.<init>()
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r6)
            r6 = 2131231659(0x7f0803ab, float:1.8079405E38)
            r4.setImageResource(r6)
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r7, r14)
            r4.setColorFilter(r6)
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            r7 = 0
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r6, r7)
            r4.setBackgroundDrawable(r6)
            android.widget.LinearLayout r6 = r0.imageButtonsContainer
            r7 = 48
            r14 = 51
            android.widget.FrameLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r7, (int) r7, (int) r14)
            r6.addView(r4, r7)
            r4.setOnClickListener(r3)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.copyTextButton = r6
            r7 = 1096810496(0x41600000, float:14.0)
            r14 = 1
            r6.setTextSize(r14, r7)
            android.widget.TextView r6 = r0.copyTextButton
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r6.setTextColor(r7)
            android.widget.TextView r6 = r0.copyTextButton
            r7 = 17
            r6.setGravity(r7)
            android.widget.TextView r6 = r0.copyTextButton
            r6.setSingleLine(r14)
            android.widget.TextView r6 = r0.copyTextButton
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r7)
            android.widget.TextView r6 = r0.copyTextButton
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            r14 = 0
            android.graphics.drawable.Drawable r7 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r7, r14)
            r6.setBackgroundDrawable(r7)
            android.widget.TextView r6 = r0.copyTextButton
            r21 = r2
            r7 = 1099956224(0x41900000, float:18.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            r25 = r4
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            r6.setPadding(r2, r14, r4, r14)
            android.widget.TextView r2 = r0.copyTextButton
            r4 = 2131690734(0x7f0f04ee, float:1.901052E38)
            java.lang.String r6 = "Copy"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r4)
            java.lang.String r4 = r4.toUpperCase()
            r2.setText(r4)
            android.widget.TextView r2 = r0.copyTextButton
            android.graphics.Typeface r4 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r2.setTypeface(r4)
            android.widget.TextView r2 = r0.copyTextButton
            r4 = 51
            r6 = -2
            r7 = -1
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r7, (int) r4)
            r5.addView(r2, r14)
            android.widget.TextView r2 = r0.copyTextButton
            r2.setOnClickListener(r3)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r4 = 1096810496(0x41600000, float:14.0)
            r6 = 1
            r2.setTextSize(r6, r4)
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r2.setTextColor(r4)
            r4 = 17
            r2.setGravity(r4)
            r2.setSingleLine(r6)
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r4)
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            r6 = 0
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r4, r6)
            r2.setBackgroundDrawable(r4)
            r4 = 1099956224(0x41900000, float:18.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r2.setPadding(r7, r6, r4, r6)
            r4 = 2131692495(0x7f0f0bcf, float:1.9014092E38)
            java.lang.String r6 = "OpenInBrowser"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r4)
            java.lang.String r4 = r4.toUpperCase()
            r2.setText(r4)
            android.graphics.Typeface r4 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r2.setTypeface(r4)
            r4 = 51
            r6 = -2
            r7 = -1
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r7, (int) r4)
            r5.addView(r2, r4)
            im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$IlHbHz2zoFDwrFT8sCXMIUbqkos r4 = new im.bclpbkiauv.ui.components.-$$Lambda$EmbedBottomSheet$IlHbHz2zoFDwrFT8sCXMIUbqkos
            r4.<init>()
            r2.setOnClickListener(r4)
            im.bclpbkiauv.ui.components.EmbedBottomSheet$7 r4 = new im.bclpbkiauv.ui.components.EmbedBottomSheet$7
            r4.<init>()
            r0.setDelegate(r4)
            im.bclpbkiauv.ui.components.EmbedBottomSheet$8 r4 = new im.bclpbkiauv.ui.components.EmbedBottomSheet$8
            android.content.Context r6 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r4.<init>(r6)
            r0.orientationEventListener = r4
            im.bclpbkiauv.ui.components.WebPlayerView r4 = r0.videoView
            java.lang.String r6 = r0.embedUrl
            java.lang.String r4 = r4.getYouTubeVideoId(r6)
            if (r4 == 0) goto L_0x050c
            im.bclpbkiauv.ui.components.RadialProgressView r6 = r0.progressBar
            r7 = 0
            r6.setVisibility(r7)
            android.webkit.WebView r6 = r0.webView
            r6.setVisibility(r7)
            android.widget.LinearLayout r6 = r0.imageButtonsContainer
            r6.setVisibility(r7)
            android.view.View r6 = r0.progressBarBlackBackground
            r6.setVisibility(r7)
            android.widget.TextView r6 = r0.copyTextButton
            r7 = 4
            r6.setVisibility(r7)
            android.webkit.WebView r6 = r0.webView
            r8 = 1
            r6.setKeepScreenOn(r8)
            im.bclpbkiauv.ui.components.WebPlayerView r6 = r0.videoView
            r6.setVisibility(r7)
            im.bclpbkiauv.ui.components.WebPlayerView r6 = r0.videoView
            android.view.View r6 = r6.getControlsView()
            r6.setVisibility(r7)
            im.bclpbkiauv.ui.components.WebPlayerView r6 = r0.videoView
            android.view.TextureView r6 = r6.getTextureView()
            r6.setVisibility(r7)
            im.bclpbkiauv.ui.components.WebPlayerView r6 = r0.videoView
            android.widget.ImageView r6 = r6.getTextureImageView()
            if (r6 == 0) goto L_0x050c
            im.bclpbkiauv.ui.components.WebPlayerView r6 = r0.videoView
            android.widget.ImageView r6 = r6.getTextureImageView()
            r6.setVisibility(r7)
        L_0x050c:
            android.view.OrientationEventListener r6 = r0.orientationEventListener
            boolean r6 = r6.canDetectOrientation()
            if (r6 == 0) goto L_0x051a
            android.view.OrientationEventListener r6 = r0.orientationEventListener
            r6.enable()
            goto L_0x0522
        L_0x051a:
            android.view.OrientationEventListener r6 = r0.orientationEventListener
            r6.disable()
            r6 = 0
            r0.orientationEventListener = r6
        L_0x0522:
            instance = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmbedBottomSheet.<init>(android.content.Context, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, int):void");
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ boolean lambda$new$1(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ boolean lambda$new$2(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$new$3$EmbedBottomSheet(View v) {
        dismiss();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0021, code lost:
        r5 = r14.height;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$4$EmbedBottomSheet(android.view.View r15) {
        /*
            r14 = this;
            boolean r0 = r14.checkInlinePermissions()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            im.bclpbkiauv.ui.components.RadialProgressView r0 = r14.progressBar
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0010
            return
        L_0x0010:
            r0 = 0
            im.bclpbkiauv.ui.components.PipVideoView r1 = new im.bclpbkiauv.ui.components.PipVideoView
            r1.<init>()
            r14.pipVideoView = r1
            android.app.Activity r2 = r14.parentActivity
            r4 = 0
            int r3 = r14.width
            r8 = 1065353216(0x3f800000, float:1.0)
            if (r3 == 0) goto L_0x002a
            int r5 = r14.height
            if (r5 == 0) goto L_0x002a
            float r3 = (float) r3
            float r5 = (float) r5
            float r3 = r3 / r5
            r5 = r3
            goto L_0x002c
        L_0x002a:
            r5 = 1065353216(0x3f800000, float:1.0)
        L_0x002c:
            r6 = 0
            android.webkit.WebView r7 = r14.webView
            r3 = r14
            r1.show(r2, r3, r4, r5, r6, r7)
            boolean r1 = r14.isYouTube
            if (r1 == 0) goto L_0x003c
            java.lang.String r1 = "hideControls();"
            r14.runJsCode(r1)
        L_0x003c:
            r1 = 0
            if (r0 == 0) goto L_0x0135
            r2 = 1
            r14.animationInProgress = r2
            im.bclpbkiauv.ui.components.WebPlayerView r3 = r14.videoView
            android.view.View r3 = r3.getAspectRatioView()
            int[] r4 = r14.position
            r3.getLocationInWindow(r4)
            int[] r4 = r14.position
            r5 = 0
            r6 = r4[r5]
            int r7 = r14.getLeftInset()
            int r6 = r6 - r7
            r4[r5] = r6
            int[] r4 = r14.position
            r6 = r4[r2]
            float r6 = (float) r6
            android.view.ViewGroup r7 = r14.containerView
            float r7 = r7.getTranslationY()
            float r6 = r6 - r7
            int r6 = (int) r6
            r4[r2] = r6
            im.bclpbkiauv.ui.components.WebPlayerView r4 = r14.videoView
            android.view.TextureView r4 = r4.getTextureView()
            im.bclpbkiauv.ui.components.WebPlayerView r6 = r14.videoView
            android.widget.ImageView r6 = r6.getTextureImageView()
            android.animation.AnimatorSet r7 = new android.animation.AnimatorSet
            r7.<init>()
            r9 = 10
            android.animation.Animator[] r9 = new android.animation.Animator[r9]
            android.util.Property r10 = android.view.View.SCALE_X
            float[] r11 = new float[r2]
            r11[r5] = r8
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r6, r10, r11)
            r9[r5] = r10
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r11 = new float[r2]
            r11[r5] = r8
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r6, r10, r11)
            r9[r2] = r10
            r10 = 2
            android.util.Property r11 = android.view.View.TRANSLATION_X
            float[] r12 = new float[r2]
            int[] r13 = r14.position
            r13 = r13[r5]
            float r13 = (float) r13
            r12[r5] = r13
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r6, r11, r12)
            r9[r10] = r11
            r10 = 3
            android.util.Property r11 = android.view.View.TRANSLATION_Y
            float[] r12 = new float[r2]
            int[] r13 = r14.position
            r13 = r13[r2]
            float r13 = (float) r13
            r12[r5] = r13
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r6, r11, r12)
            r9[r10] = r11
            r10 = 4
            android.util.Property r11 = android.view.View.SCALE_X
            float[] r12 = new float[r2]
            r12[r5] = r8
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r4, r11, r12)
            r9[r10] = r11
            r10 = 5
            android.util.Property r11 = android.view.View.SCALE_Y
            float[] r12 = new float[r2]
            r12[r5] = r8
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r4, r11, r12)
            r9[r10] = r8
            r8 = 6
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r2]
            int[] r12 = r14.position
            r12 = r12[r5]
            float r12 = (float) r12
            r11[r5] = r12
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r4, r10, r11)
            r9[r8] = r10
            r8 = 7
            android.util.Property r10 = android.view.View.TRANSLATION_Y
            float[] r11 = new float[r2]
            int[] r12 = r14.position
            r12 = r12[r2]
            float r12 = (float) r12
            r11[r5] = r12
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r4, r10, r11)
            r9[r8] = r10
            r8 = 8
            android.view.ViewGroup r10 = r14.containerView
            android.util.Property r11 = android.view.View.TRANSLATION_Y
            float[] r12 = new float[r2]
            r12[r5] = r1
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r10, r11, r12)
            r9[r8] = r1
            r1 = 9
            android.graphics.drawable.ColorDrawable r8 = r14.backDrawable
            android.util.Property<android.graphics.drawable.ColorDrawable, java.lang.Integer> r10 = im.bclpbkiauv.ui.components.AnimationProperties.COLOR_DRAWABLE_ALPHA
            int[] r2 = new int[r2]
            r11 = 51
            r2[r5] = r11
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofInt(r8, r10, r2)
            r9[r1] = r2
            r7.playTogether(r9)
            android.view.animation.DecelerateInterpolator r1 = new android.view.animation.DecelerateInterpolator
            r1.<init>()
            r7.setInterpolator(r1)
            r1 = 250(0xfa, double:1.235E-321)
            r7.setDuration(r1)
            im.bclpbkiauv.ui.components.EmbedBottomSheet$6 r1 = new im.bclpbkiauv.ui.components.EmbedBottomSheet$6
            r1.<init>()
            r7.addListener(r1)
            r7.start()
            goto L_0x013a
        L_0x0135:
            android.view.ViewGroup r2 = r14.containerView
            r2.setTranslationY(r1)
        L_0x013a:
            r14.dismissInternal()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmbedBottomSheet.lambda$new$4$EmbedBottomSheet(android.view.View):void");
    }

    public /* synthetic */ void lambda$new$5$EmbedBottomSheet(View v) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.openUrl));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ToastUtils.show((int) R.string.LinkCopied);
        dismiss();
    }

    public /* synthetic */ void lambda$new$6$EmbedBottomSheet(View v) {
        Browser.openUrl((Context) this.parentActivity, this.openUrl);
        dismiss();
    }

    private void runJsCode(String code) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.evaluateJavascript(code, (ValueCallback) null);
            return;
        }
        try {
            WebView webView2 = this.webView;
            webView2.loadUrl("javascript:" + code);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean checkInlinePermissions() {
        if (this.parentActivity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.parentActivity)) {
            return true;
        }
        new AlertDialog.Builder((Context) this.parentActivity).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", R.string.PermissionDrawAboveOtherApps)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                EmbedBottomSheet.this.lambda$checkInlinePermissions$7$EmbedBottomSheet(dialogInterface, i);
            }
        }).show();
        return false;
    }

    public /* synthetic */ void lambda$checkInlinePermissions$7$EmbedBottomSheet(DialogInterface dialog, int which) {
        Activity activity = this.parentActivity;
        if (activity != null) {
            activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + this.parentActivity.getPackageName())));
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return this.videoView.getVisibility() != 0 || !this.videoView.isInFullscreen();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (this.videoView.getVisibility() == 0 && this.videoView.isInitied() && !this.videoView.isInline()) {
            if (newConfig.orientation == 2) {
                if (!this.videoView.isInFullscreen()) {
                    this.videoView.enterFullscreen();
                }
            } else if (this.videoView.isInFullscreen()) {
                this.videoView.exitFullscreen();
            }
        }
        PipVideoView pipVideoView2 = this.pipVideoView;
        if (pipVideoView2 != null) {
            pipVideoView2.onConfigurationChanged();
        }
    }

    public void destroy() {
        WebView webView2 = this.webView;
        if (webView2 != null && webView2.getVisibility() == 0) {
            this.containerLayout.removeView(this.webView);
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
        }
        PipVideoView pipVideoView2 = this.pipVideoView;
        if (pipVideoView2 != null) {
            pipVideoView2.close();
            this.pipVideoView = null;
        }
        WebPlayerView webPlayerView = this.videoView;
        if (webPlayerView != null) {
            webPlayerView.destroy();
        }
        instance = null;
        dismissInternal();
    }

    public void exitFromPip() {
        if (this.webView != null && this.pipVideoView != null) {
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (this.isYouTube) {
                runJsCode("showControls();");
            }
            ViewGroup parent = (ViewGroup) this.webView.getParent();
            if (parent != null) {
                parent.removeView(this.webView);
            }
            this.containerLayout.addView(this.webView, 0, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, (float) ((this.hasDescription ? 22 : 0) + 84)));
            setShowWithoutAnimation(true);
            show();
            this.pipVideoView.close();
            this.pipVideoView = null;
        }
    }

    public static EmbedBottomSheet getInstance() {
        return instance;
    }

    public void updateTextureViewPosition() {
        this.videoView.getAspectRatioView().getLocationInWindow(this.position);
        int[] iArr = this.position;
        iArr[0] = iArr[0] - getLeftInset();
        if (!this.videoView.isInline() && !this.animationInProgress) {
            TextureView textureView = this.videoView.getTextureView();
            textureView.setTranslationX((float) this.position[0]);
            textureView.setTranslationY((float) this.position[1]);
            View textureImageView = this.videoView.getTextureImageView();
            if (textureImageView != null) {
                textureImageView.setTranslationX((float) this.position[0]);
                textureImageView.setTranslationY((float) this.position[1]);
            }
        }
        View controlsView = this.videoView.getControlsView();
        if (controlsView.getParent() == this.container) {
            controlsView.setTranslationY((float) this.position[1]);
        } else {
            controlsView.setTranslationY(0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return this.fullscreenVideoContainer.getVisibility() != 0;
    }

    /* access modifiers changed from: protected */
    public void onContainerTranslationYChanged(float translationY) {
        updateTextureViewPosition();
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int width2, int height2) {
        if (view == this.videoView.getControlsView()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = this.videoView.getMeasuredWidth();
            layoutParams.height = this.videoView.getAspectRatioView().getMeasuredHeight() + (this.videoView.isInFullscreen() ? 0 : AndroidUtilities.dp(10.0f));
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        if (view != this.videoView.getControlsView()) {
            return false;
        }
        updateTextureViewPosition();
        return false;
    }

    public void pause() {
        WebPlayerView webPlayerView = this.videoView;
        if (webPlayerView != null && webPlayerView.isInitied()) {
            this.videoView.pause();
        }
    }

    public void onContainerDraw(Canvas canvas) {
        int i = this.waitingForDraw;
        if (i != 0) {
            int i2 = i - 1;
            this.waitingForDraw = i2;
            if (i2 == 0) {
                this.videoView.updateTextureImageView();
                this.pipVideoView.close();
                this.pipVideoView = null;
                return;
            }
            this.container.invalidate();
        }
    }
}
