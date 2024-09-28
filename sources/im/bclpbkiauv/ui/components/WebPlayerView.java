package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.internal.view.SupportMenu;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.android.gms.wearable.WearableStatusCodes;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.Bitmaps;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.components.VideoPlayer;
import im.bclpbkiauv.ui.components.WebPlayerView;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Marker;

public class WebPlayerView extends ViewGroup implements VideoPlayer.VideoPlayerDelegate, AudioManager.OnAudioFocusChangeListener {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    /* access modifiers changed from: private */
    public static final Pattern aparatFileListPattern = Pattern.compile("fileList\\s*=\\s*JSON\\.parse\\('([^']+)'\\)");
    private static final Pattern aparatIdRegex = Pattern.compile("^https?://(?:www\\.)?aparat\\.com/(?:v/|video/video/embed/videohash/)([a-zA-Z0-9]+)");
    private static final Pattern coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    private static final String exprName = "[a-zA-Z_$][a-zA-Z_$0-9]*";
    /* access modifiers changed from: private */
    public static final Pattern exprParensPattern = Pattern.compile("[()]");
    /* access modifiers changed from: private */
    public static final Pattern jsPattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
    private static int lastContainerId = WearableStatusCodes.DUPLICATE_LISTENER;
    /* access modifiers changed from: private */
    public static final Pattern playerIdPattern = Pattern.compile(".*?-([a-zA-Z0-9_-]+)(?:/watch_as3|/html5player(?:-new)?|(?:/[a-z]{2}_[A-Z]{2})?/base)?\\.([a-z]+)$");
    /* access modifiers changed from: private */
    public static final Pattern sigPattern = Pattern.compile("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(");
    /* access modifiers changed from: private */
    public static final Pattern sigPattern2 = Pattern.compile("[\"']signature[\"']\\s*,\\s*([a-zA-Z0-9$]+)\\(");
    /* access modifiers changed from: private */
    public static final Pattern stmtReturnPattern = Pattern.compile("return(?:\\s+|$)");
    /* access modifiers changed from: private */
    public static final Pattern stmtVarPattern = Pattern.compile("var\\s");
    /* access modifiers changed from: private */
    public static final Pattern stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
    /* access modifiers changed from: private */
    public static final Pattern twitchClipFilePattern = Pattern.compile("clipInfo\\s*=\\s*(\\{[^']+\\});");
    private static final Pattern twitchClipIdRegex = Pattern.compile("https?://clips\\.twitch\\.tv/(?:[^/]+/)*([^/?#&]+)");
    private static final Pattern twitchStreamIdRegex = Pattern.compile("https?://(?:(?:www\\.)?twitch\\.tv/|player\\.twitch\\.tv/\\?.*?\\bchannel=)([^/#?]+)");
    private static final Pattern vimeoIdRegex = Pattern.compile("https?://(?:(?:www|(player))\\.)?vimeo(pro)?\\.com/(?!(?:channels|album)/[^/?#]+/?(?:$|[?#])|[^/]+/review/|ondemand/)(?:.*?/)?(?:(?:play_redirect_hls|moogaloop\\.swf)\\?clip_id=)?(?:videos?/)?([0-9]+)(?:/[\\da-f]+)?/?(?:[?&].*)?(?:[#].*)?$");
    private static final Pattern youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})");
    /* access modifiers changed from: private */
    public boolean allowInlineAnimation;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout aspectRatioFrameLayout;
    private int audioFocus;
    private Paint backgroundPaint;
    /* access modifiers changed from: private */
    public TextureView changedTextureView;
    /* access modifiers changed from: private */
    public boolean changingTextureView;
    /* access modifiers changed from: private */
    public ControlsView controlsView;
    /* access modifiers changed from: private */
    public float currentAlpha;
    /* access modifiers changed from: private */
    public Bitmap currentBitmap;
    private AsyncTask currentTask;
    private String currentYoutubeId;
    /* access modifiers changed from: private */
    public WebPlayerViewDelegate delegate;
    /* access modifiers changed from: private */
    public boolean drawImage;
    /* access modifiers changed from: private */
    public boolean firstFrameRendered;
    private int fragment_container_id;
    private ImageView fullscreenButton;
    private boolean hasAudioFocus;
    /* access modifiers changed from: private */
    public boolean inFullscreen;
    private boolean initFailed;
    /* access modifiers changed from: private */
    public boolean initied;
    private ImageView inlineButton;
    /* access modifiers changed from: private */
    public String interfaceName;
    /* access modifiers changed from: private */
    public boolean isAutoplay;
    private boolean isCompleted;
    /* access modifiers changed from: private */
    public boolean isInline;
    private boolean isLoading;
    /* access modifiers changed from: private */
    public boolean isStream;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    /* access modifiers changed from: private */
    public String playAudioType;
    /* access modifiers changed from: private */
    public String playAudioUrl;
    private ImageView playButton;
    /* access modifiers changed from: private */
    public String playVideoType;
    /* access modifiers changed from: private */
    public String playVideoUrl;
    /* access modifiers changed from: private */
    public AnimatorSet progressAnimation;
    /* access modifiers changed from: private */
    public Runnable progressRunnable;
    private RadialProgressView progressView;
    private boolean resumeAudioOnFocusGain;
    private int seekToTime;
    private ImageView shareButton;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private Runnable switchToInlineRunnable;
    /* access modifiers changed from: private */
    public boolean switchingInlineMode;
    /* access modifiers changed from: private */
    public ImageView textureImageView;
    /* access modifiers changed from: private */
    public TextureView textureView;
    /* access modifiers changed from: private */
    public ViewGroup textureViewContainer;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;
    /* access modifiers changed from: private */
    public int waitingForFirstTextureUpload;
    /* access modifiers changed from: private */
    public WebView webView;

    public interface CallJavaResultInterface {
        void jsCallFinished(String str);
    }

    public interface WebPlayerViewDelegate {
        boolean checkInlinePermissions();

        ViewGroup getTextureViewContainer();

        void onInitFailed();

        void onInlineSurfaceTextureReady();

        void onPlayStateChanged(WebPlayerView webPlayerView, boolean z);

        void onSharePressed();

        TextureView onSwitchInlineMode(View view, boolean z, float f, int i, boolean z2);

        TextureView onSwitchToFullscreen(View view, boolean z, float f, int i, boolean z2);

        void onVideoSizeChanged(float f, int i);

        void prepareToSwitchInlineMode(boolean z, Runnable runnable, float f, boolean z2);
    }

    private abstract class function {
        public abstract Object run(Object[] objArr);

        private function() {
        }
    }

    private class JSExtractor {
        private String[] assign_operators = {"|=", "^=", "&=", ">>=", "<<=", "-=", "+=", "%=", "/=", "*=", "="};
        ArrayList<String> codeLines = new ArrayList<>();
        private String jsCode;
        private String[] operators = {LogUtils.VERTICAL, "^", "&", ">>", "<<", "-", Marker.ANY_NON_NULL_MARKER, "%", "/", "*"};

        public JSExtractor(String js) {
            this.jsCode = js;
        }

        private void interpretExpression(String expr, HashMap<String, String> localVars, int allowRecursion) throws Exception {
            String expr2 = expr.trim();
            if (!TextUtils.isEmpty(expr2)) {
                if (expr2.charAt(0) == '(') {
                    int parens_count = 0;
                    Matcher matcher = WebPlayerView.exprParensPattern.matcher(expr2);
                    while (true) {
                        if (!matcher.find()) {
                            break;
                        } else if (matcher.group(0).indexOf(48) == 40) {
                            parens_count++;
                        } else {
                            parens_count--;
                            if (parens_count == 0) {
                                interpretExpression(expr2.substring(1, matcher.start()), localVars, allowRecursion);
                                String remaining_expr = expr2.substring(matcher.end()).trim();
                                if (!TextUtils.isEmpty(remaining_expr)) {
                                    expr2 = remaining_expr;
                                } else {
                                    return;
                                }
                            }
                        }
                    }
                    if (parens_count != 0) {
                        throw new Exception(String.format("Premature end of parens in %s", new Object[]{expr2}));
                    }
                }
                int a = 0;
                while (true) {
                    String[] strArr = this.assign_operators;
                    if (a < strArr.length) {
                        Matcher matcher2 = Pattern.compile(String.format(Locale.US, "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$", new Object[]{WebPlayerView.exprName, Pattern.quote(strArr[a])})).matcher(expr2);
                        if (!matcher2.find()) {
                            a++;
                        } else {
                            interpretExpression(matcher2.group(3), localVars, allowRecursion - 1);
                            String index = matcher2.group(2);
                            if (!TextUtils.isEmpty(index)) {
                                interpretExpression(index, localVars, allowRecursion);
                                return;
                            } else {
                                localVars.put(matcher2.group(1), "");
                                return;
                            }
                        }
                    } else {
                        try {
                            Integer.parseInt(expr2);
                            return;
                        } catch (Exception e) {
                            if (!Pattern.compile(String.format(Locale.US, "(?!if|return|true|false)(%s)$", new Object[]{WebPlayerView.exprName})).matcher(expr2).find()) {
                                if (expr2.charAt(0) != '\"' || expr2.charAt(expr2.length() - 1) != '\"') {
                                    try {
                                        new JSONObject(expr2).toString();
                                        return;
                                    } catch (Exception e2) {
                                        Matcher matcher3 = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", new Object[]{WebPlayerView.exprName})).matcher(expr2);
                                        if (matcher3.find()) {
                                            String group = matcher3.group(1);
                                            interpretExpression(matcher3.group(2), localVars, allowRecursion - 1);
                                            return;
                                        }
                                        Matcher matcher4 = Pattern.compile(String.format(Locale.US, "(%s)(?:\\.([^(]+)|\\[([^]]+)\\])\\s*(?:\\(+([^()]*)\\))?$", new Object[]{WebPlayerView.exprName})).matcher(expr2);
                                        if (matcher4.find()) {
                                            String variable = matcher4.group(1);
                                            String m1 = matcher4.group(2);
                                            String replace = (TextUtils.isEmpty(m1) ? matcher4.group(3) : m1).replace("\"", "");
                                            String arg_str = matcher4.group(4);
                                            if (localVars.get(variable) == null) {
                                                extractObject(variable);
                                            }
                                            if (arg_str != null) {
                                                if (expr2.charAt(expr2.length() - 1) != ')') {
                                                    throw new Exception("last char not ')'");
                                                } else if (arg_str.length() != 0) {
                                                    String[] args = arg_str.split(",");
                                                    for (String interpretExpression : args) {
                                                        interpretExpression(interpretExpression, localVars, allowRecursion);
                                                    }
                                                    return;
                                                } else {
                                                    return;
                                                }
                                            } else {
                                                return;
                                            }
                                        } else {
                                            Matcher matcher5 = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", new Object[]{WebPlayerView.exprName})).matcher(expr2);
                                            if (matcher5.find()) {
                                                String str = localVars.get(matcher5.group(1));
                                                interpretExpression(matcher5.group(2), localVars, allowRecursion - 1);
                                                return;
                                            }
                                            int a2 = 0;
                                            while (true) {
                                                String[] strArr2 = this.operators;
                                                if (a2 < strArr2.length) {
                                                    String func = strArr2[a2];
                                                    Matcher matcher6 = Pattern.compile(String.format(Locale.US, "(.+?)%s(.+)", new Object[]{Pattern.quote(func)})).matcher(expr2);
                                                    if (matcher6.find()) {
                                                        boolean[] abort = new boolean[1];
                                                        interpretStatement(matcher6.group(1), localVars, abort, allowRecursion - 1);
                                                        if (!abort[0]) {
                                                            interpretStatement(matcher6.group(2), localVars, abort, allowRecursion - 1);
                                                            if (abort[0]) {
                                                                throw new Exception(String.format("Premature right-side return of %s in %s", new Object[]{func, expr2}));
                                                            }
                                                        } else {
                                                            throw new Exception(String.format("Premature left-side return of %s in %s", new Object[]{func, expr2}));
                                                        }
                                                    }
                                                    a2++;
                                                } else {
                                                    Matcher matcher7 = Pattern.compile(String.format(Locale.US, "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$", new Object[]{WebPlayerView.exprName})).matcher(expr2);
                                                    if (matcher7.find()) {
                                                        extractFunction(matcher7.group(1));
                                                    }
                                                    throw new Exception(String.format("Unsupported JS expression %s", new Object[]{expr2}));
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
        }

        private void interpretStatement(String stmt, HashMap<String, String> localVars, boolean[] abort, int allowRecursion) throws Exception {
            String expr;
            if (allowRecursion >= 0) {
                abort[0] = false;
                String stmt2 = stmt.trim();
                Matcher matcher = WebPlayerView.stmtVarPattern.matcher(stmt2);
                if (matcher.find()) {
                    expr = stmt2.substring(matcher.group(0).length());
                } else {
                    Matcher matcher2 = WebPlayerView.stmtReturnPattern.matcher(stmt2);
                    if (matcher2.find()) {
                        String expr2 = stmt2.substring(matcher2.group(0).length());
                        abort[0] = true;
                        expr = expr2;
                    } else {
                        expr = stmt2;
                    }
                }
                interpretExpression(expr, localVars, allowRecursion);
                return;
            }
            throw new Exception("recursion limit reached");
        }

        private HashMap<String, Object> extractObject(String objname) throws Exception {
            HashMap<String, Object> obj = new HashMap<>();
            Matcher matcher = Pattern.compile(String.format(Locale.US, "(?:var\\s+)?%s\\s*=\\s*\\{\\s*((%s\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;", new Object[]{Pattern.quote(objname), "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')"})).matcher(this.jsCode);
            String fields = null;
            while (true) {
                if (!matcher.find()) {
                    break;
                }
                String code = matcher.group();
                fields = matcher.group(2);
                if (!TextUtils.isEmpty(fields)) {
                    if (!this.codeLines.contains(code)) {
                        this.codeLines.add(matcher.group());
                    }
                }
            }
            Matcher matcher2 = Pattern.compile(String.format("(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}", new Object[]{"(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')"})).matcher(fields);
            while (matcher2.find()) {
                buildFunction(matcher2.group(2).split(","), matcher2.group(3));
            }
            return obj;
        }

        private void buildFunction(String[] argNames, String funcCode) throws Exception {
            HashMap<String, String> localVars = new HashMap<>();
            for (String put : argNames) {
                localVars.put(put, "");
            }
            String[] stmts = funcCode.split(";");
            boolean[] abort = new boolean[1];
            int a = 0;
            while (a < stmts.length) {
                interpretStatement(stmts[a], localVars, abort, 100);
                if (!abort[0]) {
                    a++;
                } else {
                    return;
                }
            }
        }

        /* access modifiers changed from: private */
        public String extractFunction(String funcName) {
            try {
                String quote = Pattern.quote(funcName);
                Matcher matcher = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", new Object[]{quote, quote, quote})).matcher(this.jsCode);
                if (matcher.find()) {
                    String group = matcher.group();
                    if (!this.codeLines.contains(group)) {
                        ArrayList<String> arrayList = this.codeLines;
                        arrayList.add(group + ";");
                    }
                    buildFunction(matcher.group(1).split(","), matcher.group(2));
                }
            } catch (Exception e) {
                this.codeLines.clear();
                FileLog.e((Throwable) e);
            }
            return TextUtils.join("", this.codeLines);
        }
    }

    public class JavaScriptInterface {
        private final CallJavaResultInterface callJavaResultInterface;

        public JavaScriptInterface(CallJavaResultInterface callJavaResult) {
            this.callJavaResultInterface = callJavaResult;
        }

        @JavascriptInterface
        public void returnResultToJava(String value) {
            this.callJavaResultInterface.jsCallFinished(value);
        }
    }

    /* access modifiers changed from: protected */
    public String downloadUrlContent(AsyncTask parentTask, String url) {
        return downloadUrlContent(parentTask, url, (HashMap<String, String>) null, true);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x01e9, code lost:
        r10 = r20;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0174  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01a2 A[SYNTHETIC, Splitter:B:117:0x01a2] */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0204 A[SYNTHETIC, Splitter:B:154:0x0204] */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0210  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x0216  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x021b A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0118 A[SYNTHETIC, Splitter:B:71:0x0118] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x013b A[Catch:{ Exception -> 0x0123, all -> 0x0144 }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x016b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String downloadUrlContent(android.os.AsyncTask r24, java.lang.String r25, java.util.HashMap<java.lang.String, java.lang.String> r26, boolean r27) {
        /*
            r23 = this;
            java.lang.String r0 = "ISO-8859-1,utf-8;q=0.7,*;q=0.7"
            java.lang.String r1 = "Accept-Charset"
            java.lang.String r2 = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
            java.lang.String r3 = "Accept"
            java.lang.String r4 = "en-us,en;q=0.5"
            java.lang.String r5 = "Accept-Language"
            java.lang.String r6 = "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)"
            java.lang.String r7 = "User-Agent"
            r8 = 1
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            java.net.URL r13 = new java.net.URL     // Catch:{ all -> 0x015e }
            r14 = r25
            r13.<init>(r14)     // Catch:{ all -> 0x015c }
            java.net.URLConnection r15 = r13.openConnection()     // Catch:{ all -> 0x015c }
            r12 = r15
            r12.addRequestProperty(r7, r6)     // Catch:{ all -> 0x0152 }
            java.lang.String r15 = "gzip, deflate"
            r16 = r8
            java.lang.String r8 = "Accept-Encoding"
            if (r27 == 0) goto L_0x0037
            r12.addRequestProperty(r8, r15)     // Catch:{ all -> 0x0030 }
            goto L_0x0037
        L_0x0030:
            r0 = move-exception
            r20 = r10
            r21 = r11
            goto L_0x0167
        L_0x0037:
            r12.addRequestProperty(r5, r4)     // Catch:{ all -> 0x014a }
            r12.addRequestProperty(r3, r2)     // Catch:{ all -> 0x014a }
            r12.addRequestProperty(r1, r0)     // Catch:{ all -> 0x014a }
            if (r26 == 0) goto L_0x0083
            java.util.Set r17 = r26.entrySet()     // Catch:{ all -> 0x007c }
            java.util.Iterator r17 = r17.iterator()     // Catch:{ all -> 0x007c }
        L_0x004a:
            boolean r18 = r17.hasNext()     // Catch:{ all -> 0x007c }
            if (r18 == 0) goto L_0x0077
            java.lang.Object r18 = r17.next()     // Catch:{ all -> 0x007c }
            java.util.Map$Entry r18 = (java.util.Map.Entry) r18     // Catch:{ all -> 0x007c }
            java.lang.Object r19 = r18.getKey()     // Catch:{ all -> 0x007c }
            r20 = r10
            r10 = r19
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ all -> 0x0072 }
            java.lang.Object r19 = r18.getValue()     // Catch:{ all -> 0x0072 }
            r21 = r11
            r11 = r19
            java.lang.String r11 = (java.lang.String) r11     // Catch:{ all -> 0x0144 }
            r12.addRequestProperty(r10, r11)     // Catch:{ all -> 0x0144 }
            r10 = r20
            r11 = r21
            goto L_0x004a
        L_0x0072:
            r0 = move-exception
            r21 = r11
            goto L_0x0167
        L_0x0077:
            r20 = r10
            r21 = r11
            goto L_0x0087
        L_0x007c:
            r0 = move-exception
            r20 = r10
            r21 = r11
            goto L_0x0167
        L_0x0083:
            r20 = r10
            r21 = r11
        L_0x0087:
            r10 = 5000(0x1388, float:7.006E-42)
            r12.setConnectTimeout(r10)     // Catch:{ all -> 0x0146 }
            r12.setReadTimeout(r10)     // Catch:{ all -> 0x0146 }
            boolean r10 = r12 instanceof java.net.HttpURLConnection     // Catch:{ all -> 0x0146 }
            if (r10 == 0) goto L_0x010f
            r10 = r12
            java.net.HttpURLConnection r10 = (java.net.HttpURLConnection) r10     // Catch:{ all -> 0x0146 }
            r11 = 1
            r10.setInstanceFollowRedirects(r11)     // Catch:{ all -> 0x0146 }
            int r11 = r10.getResponseCode()     // Catch:{ all -> 0x0146 }
            r17 = r12
            r12 = 302(0x12e, float:4.23E-43)
            if (r11 == r12) goto L_0x00ac
            r12 = 301(0x12d, float:4.22E-43)
            if (r11 == r12) goto L_0x00ac
            r12 = 303(0x12f, float:4.25E-43)
            if (r11 != r12) goto L_0x0111
        L_0x00ac:
            java.lang.String r12 = "Location"
            java.lang.String r12 = r10.getHeaderField(r12)     // Catch:{ all -> 0x010a }
            r18 = r11
            java.lang.String r11 = "Set-Cookie"
            java.lang.String r11 = r10.getHeaderField(r11)     // Catch:{ all -> 0x010a }
            r19 = r10
            java.net.URL r10 = new java.net.URL     // Catch:{ all -> 0x010a }
            r10.<init>(r12)     // Catch:{ all -> 0x010a }
            r13 = r10
            java.net.URLConnection r10 = r13.openConnection()     // Catch:{ all -> 0x010a }
            r22 = r12
            java.lang.String r12 = "Cookie"
            r10.setRequestProperty(r12, r11)     // Catch:{ all -> 0x0106 }
            r10.addRequestProperty(r7, r6)     // Catch:{ all -> 0x0106 }
            if (r27 == 0) goto L_0x00d5
            r10.addRequestProperty(r8, r15)     // Catch:{ all -> 0x0106 }
        L_0x00d5:
            r10.addRequestProperty(r5, r4)     // Catch:{ all -> 0x0106 }
            r10.addRequestProperty(r3, r2)     // Catch:{ all -> 0x0106 }
            r10.addRequestProperty(r1, r0)     // Catch:{ all -> 0x0106 }
            if (r26 == 0) goto L_0x0104
            java.util.Set r0 = r26.entrySet()     // Catch:{ all -> 0x0106 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0106 }
        L_0x00e8:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x0106 }
            if (r1 == 0) goto L_0x0104
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x0106 }
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch:{ all -> 0x0106 }
            java.lang.Object r2 = r1.getKey()     // Catch:{ all -> 0x0106 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x0106 }
            java.lang.Object r3 = r1.getValue()     // Catch:{ all -> 0x0106 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0106 }
            r10.addRequestProperty(r2, r3)     // Catch:{ all -> 0x0106 }
            goto L_0x00e8
        L_0x0104:
            r12 = r10
            goto L_0x0113
        L_0x0106:
            r0 = move-exception
            r12 = r10
            goto L_0x0167
        L_0x010a:
            r0 = move-exception
            r12 = r17
            goto L_0x0167
        L_0x010f:
            r17 = r12
        L_0x0111:
            r12 = r17
        L_0x0113:
            r12.connect()     // Catch:{ all -> 0x0144 }
            if (r27 == 0) goto L_0x013b
            java.util.zip.GZIPInputStream r0 = new java.util.zip.GZIPInputStream     // Catch:{ Exception -> 0x0123 }
            java.io.InputStream r1 = r12.getInputStream()     // Catch:{ Exception -> 0x0123 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0123 }
        L_0x0121:
            r9 = r0
            goto L_0x0140
        L_0x0123:
            r0 = move-exception
            r1 = r0
            if (r9 == 0) goto L_0x012d
            r9.close()     // Catch:{ Exception -> 0x012b }
            goto L_0x012d
        L_0x012b:
            r0 = move-exception
            goto L_0x012e
        L_0x012d:
        L_0x012e:
            java.net.URLConnection r0 = r13.openConnection()     // Catch:{ all -> 0x0144 }
            r12 = r0
            r12.connect()     // Catch:{ all -> 0x0144 }
            java.io.InputStream r0 = r12.getInputStream()     // Catch:{ all -> 0x0144 }
            goto L_0x0121
        L_0x013b:
            java.io.InputStream r0 = r12.getInputStream()     // Catch:{ all -> 0x0144 }
            r9 = r0
        L_0x0140:
            r8 = r16
            goto L_0x01a0
        L_0x0144:
            r0 = move-exception
            goto L_0x0167
        L_0x0146:
            r0 = move-exception
            r17 = r12
            goto L_0x0167
        L_0x014a:
            r0 = move-exception
            r20 = r10
            r21 = r11
            r17 = r12
            goto L_0x0167
        L_0x0152:
            r0 = move-exception
            r16 = r8
            r20 = r10
            r21 = r11
            r17 = r12
            goto L_0x0167
        L_0x015c:
            r0 = move-exception
            goto L_0x0161
        L_0x015e:
            r0 = move-exception
            r14 = r25
        L_0x0161:
            r16 = r8
            r20 = r10
            r21 = r11
        L_0x0167:
            boolean r1 = r0 instanceof java.net.SocketTimeoutException
            if (r1 == 0) goto L_0x0174
            boolean r1 = im.bclpbkiauv.messenger.ApplicationLoader.isNetworkOnline()
            if (r1 == 0) goto L_0x019b
            r1 = 0
            r8 = r1
            goto L_0x019d
        L_0x0174:
            boolean r1 = r0 instanceof java.net.UnknownHostException
            if (r1 == 0) goto L_0x017b
            r1 = 0
            r8 = r1
            goto L_0x019d
        L_0x017b:
            boolean r1 = r0 instanceof java.net.SocketException
            if (r1 == 0) goto L_0x0194
            java.lang.String r1 = r0.getMessage()
            if (r1 == 0) goto L_0x019b
            java.lang.String r1 = r0.getMessage()
            java.lang.String r2 = "ECONNRESET"
            boolean r1 = r1.contains(r2)
            if (r1 == 0) goto L_0x019b
            r1 = 0
            r8 = r1
            goto L_0x019d
        L_0x0194:
            boolean r1 = r0 instanceof java.io.FileNotFoundException
            if (r1 == 0) goto L_0x019b
            r1 = 0
            r8 = r1
            goto L_0x019d
        L_0x019b:
            r8 = r16
        L_0x019d:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01a0:
            if (r8 == 0) goto L_0x0210
            boolean r0 = r12 instanceof java.net.HttpURLConnection     // Catch:{ Exception -> 0x01b4 }
            if (r0 == 0) goto L_0x01b3
            r0 = r12
            java.net.HttpURLConnection r0 = (java.net.HttpURLConnection) r0     // Catch:{ Exception -> 0x01b4 }
            int r0 = r0.getResponseCode()     // Catch:{ Exception -> 0x01b4 }
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 == r1) goto L_0x01b3
            r1 = 202(0xca, float:2.83E-43)
        L_0x01b3:
            goto L_0x01b8
        L_0x01b4:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01b8:
            if (r9 == 0) goto L_0x01fe
            r0 = 32768(0x8000, float:4.5918E-41)
            byte[] r0 = new byte[r0]     // Catch:{ all -> 0x01f5 }
            r1 = r0
            r11 = r21
        L_0x01c2:
            boolean r0 = r24.isCancelled()     // Catch:{ all -> 0x01f3 }
            if (r0 == 0) goto L_0x01c9
            goto L_0x01e9
        L_0x01c9:
            int r0 = r9.read(r1)     // Catch:{ Exception -> 0x01ec }
            if (r0 <= 0) goto L_0x01e3
            if (r11 != 0) goto L_0x01d7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ec }
            r2.<init>()     // Catch:{ Exception -> 0x01ec }
            r11 = r2
        L_0x01d7:
            java.lang.String r2 = new java.lang.String     // Catch:{ Exception -> 0x01ec }
            r3 = 0
            java.nio.charset.Charset r4 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ Exception -> 0x01ec }
            r2.<init>(r1, r3, r0, r4)     // Catch:{ Exception -> 0x01ec }
            r11.append(r2)     // Catch:{ Exception -> 0x01ec }
            goto L_0x01c2
        L_0x01e3:
            r2 = -1
            if (r0 != r2) goto L_0x01e8
            r10 = 1
            goto L_0x01f2
        L_0x01e8:
        L_0x01e9:
            r10 = r20
            goto L_0x01f2
        L_0x01ec:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x01f3 }
            r10 = r20
        L_0x01f2:
            goto L_0x0202
        L_0x01f3:
            r0 = move-exception
            goto L_0x01f8
        L_0x01f5:
            r0 = move-exception
            r11 = r21
        L_0x01f8:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r10 = r20
            goto L_0x0202
        L_0x01fe:
            r10 = r20
            r11 = r21
        L_0x0202:
            if (r9 == 0) goto L_0x020f
            r9.close()     // Catch:{ all -> 0x0208 }
            goto L_0x020f
        L_0x0208:
            r0 = move-exception
            r1 = r0
            r0 = r1
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0214
        L_0x020f:
            goto L_0x0214
        L_0x0210:
            r10 = r20
            r11 = r21
        L_0x0214:
            if (r10 == 0) goto L_0x021b
            java.lang.String r0 = r11.toString()
            goto L_0x021c
        L_0x021b:
            r0 = 0
        L_0x021c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.WebPlayerView.downloadUrlContent(android.os.AsyncTask, java.lang.String, java.util.HashMap, boolean):java.lang.String");
    }

    private class YoutubeVideoTask extends AsyncTask<Void, Void, String[]> {
        private boolean canRetry = true;
        private CountDownLatch countDownLatch = new CountDownLatch(1);
        private String[] result = new String[2];
        private String sig;
        private String videoId;

        public YoutubeVideoTask(String vid) {
            this.videoId = vid;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: java.lang.String} */
        /* access modifiers changed from: protected */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String[] doInBackground(java.lang.Void... r28) {
            /*
                r27 = this;
                r1 = r27
                java.lang.String r2 = "UTF-8"
                im.bclpbkiauv.ui.components.WebPlayerView r0 = im.bclpbkiauv.ui.components.WebPlayerView.this
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "https://www.youtube.com/embed/"
                r3.append(r4)
                java.lang.String r4 = r1.videoId
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                java.lang.String r3 = r0.downloadUrlContent(r1, r3)
                boolean r0 = r27.isCancelled()
                r4 = 0
                if (r0 == 0) goto L_0x0025
                return r4
            L_0x0025:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r5 = "video_id="
                r0.append(r5)
                java.lang.String r5 = r1.videoId
                r0.append(r5)
                java.lang.String r5 = "&ps=default&gl=US&hl=en"
                r0.append(r5)
                java.lang.String r5 = r0.toString()
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x006a }
                r0.<init>()     // Catch:{ Exception -> 0x006a }
                r0.append(r5)     // Catch:{ Exception -> 0x006a }
                java.lang.String r6 = "&eurl="
                r0.append(r6)     // Catch:{ Exception -> 0x006a }
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x006a }
                r6.<init>()     // Catch:{ Exception -> 0x006a }
                java.lang.String r7 = "https://youtube.googleapis.com/v/"
                r6.append(r7)     // Catch:{ Exception -> 0x006a }
                java.lang.String r7 = r1.videoId     // Catch:{ Exception -> 0x006a }
                r6.append(r7)     // Catch:{ Exception -> 0x006a }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x006a }
                java.lang.String r6 = java.net.URLEncoder.encode(r6, r2)     // Catch:{ Exception -> 0x006a }
                r0.append(r6)     // Catch:{ Exception -> 0x006a }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x006a }
                r5 = r0
                goto L_0x006e
            L_0x006a:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x006e:
                if (r3 == 0) goto L_0x00b0
                java.util.regex.Pattern r0 = im.bclpbkiauv.ui.components.WebPlayerView.stsPattern
                java.util.regex.Matcher r0 = r0.matcher(r3)
                boolean r6 = r0.find()
                java.lang.String r7 = "&sts="
                if (r6 == 0) goto L_0x00a1
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r5)
                r6.append(r7)
                int r7 = r0.start()
                int r7 = r7 + 6
                int r8 = r0.end()
                java.lang.String r7 = r3.substring(r7, r8)
                r6.append(r7)
                java.lang.String r5 = r6.toString()
                goto L_0x00b0
            L_0x00a1:
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r5)
                r6.append(r7)
                java.lang.String r5 = r6.toString()
            L_0x00b0:
                java.lang.String[] r0 = r1.result
                java.lang.String r6 = "dash"
                r7 = 1
                r0[r7] = r6
                r0 = 0
                r6 = 0
                java.lang.String r8 = ""
                java.lang.String r9 = "&el=leanback"
                java.lang.String r10 = "&el=embedded"
                java.lang.String r11 = "&el=detailpage"
                java.lang.String r12 = "&el=vevo"
                java.lang.String[] r8 = new java.lang.String[]{r8, r9, r10, r11, r12}
                r9 = 0
            L_0x00c8:
                int r10 = r8.length
                java.lang.String r11 = "/s/"
                r12 = 2
                if (r9 >= r10) goto L_0x02a9
                im.bclpbkiauv.ui.components.WebPlayerView r10 = im.bclpbkiauv.ui.components.WebPlayerView.this
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                java.lang.String r15 = "https://www.youtube.com/get_video_info?"
                r14.append(r15)
                r14.append(r5)
                r15 = r8[r9]
                r14.append(r15)
                java.lang.String r14 = r14.toString()
                java.lang.String r10 = r10.downloadUrlContent(r1, r14)
                boolean r14 = r27.isCancelled()
                if (r14 == 0) goto L_0x00f1
                return r4
            L_0x00f1:
                r14 = 0
                r15 = 0
                r16 = 0
                if (r10 == 0) goto L_0x027e
                java.lang.String r4 = "&"
                java.lang.String[] r4 = r10.split(r4)
                r17 = 0
                r13 = r17
                r17 = r16
                r16 = r15
                r15 = r14
                r14 = r6
                r6 = r0
            L_0x0108:
                int r0 = r4.length
                if (r13 >= r0) goto L_0x026f
                r0 = r4[r13]
                java.lang.String r7 = "dashmpd"
                boolean r0 = r0.startsWith(r7)
                java.lang.String r7 = "="
                if (r0 == 0) goto L_0x013a
                r15 = 1
                r0 = r4[r13]
                java.lang.String[] r7 = r0.split(r7)
                int r0 = r7.length
                if (r0 != r12) goto L_0x0134
                java.lang.String[] r0 = r1.result     // Catch:{ Exception -> 0x0130 }
                r19 = 1
                r12 = r7[r19]     // Catch:{ Exception -> 0x0130 }
                java.lang.String r12 = java.net.URLDecoder.decode(r12, r2)     // Catch:{ Exception -> 0x0130 }
                r18 = 0
                r0[r18] = r12     // Catch:{ Exception -> 0x0130 }
                goto L_0x0134
            L_0x0130:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0134:
                r20 = r5
                r25 = r8
                goto L_0x0265
            L_0x013a:
                r0 = r4[r13]
                java.lang.String r12 = "url_encoded_fmt_stream_map"
                boolean r0 = r0.startsWith(r12)
                if (r0 == 0) goto L_0x01e8
                r0 = r4[r13]
                java.lang.String[] r12 = r0.split(r7)
                int r0 = r12.length
                r20 = r5
                r5 = 2
                if (r0 != r5) goto L_0x01e0
                r5 = 1
                r0 = r12[r5]     // Catch:{ Exception -> 0x01d7 }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x01d7 }
                java.lang.String r5 = "[&,]"
                java.lang.String[] r0 = r0.split(r5)     // Catch:{ Exception -> 0x01d7 }
                r5 = 0
                r21 = 0
                r22 = 0
                r26 = r22
                r22 = r5
                r5 = r26
            L_0x0168:
                r23 = r6
                int r6 = r0.length     // Catch:{ Exception -> 0x01d3 }
                if (r5 >= r6) goto L_0x01ce
                r6 = r0[r5]     // Catch:{ Exception -> 0x01d3 }
                java.lang.String[] r6 = r6.split(r7)     // Catch:{ Exception -> 0x01d3 }
                r24 = r0
                r18 = 0
                r0 = r6[r18]     // Catch:{ Exception -> 0x01d3 }
                r25 = r8
                java.lang.String r8 = "type"
                boolean r0 = r0.startsWith(r8)     // Catch:{ Exception -> 0x01cc }
                if (r0 == 0) goto L_0x0195
                r8 = 1
                r0 = r6[r8]     // Catch:{ Exception -> 0x01cc }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x01cc }
                java.lang.String r8 = "video/mp4"
                boolean r8 = r0.contains(r8)     // Catch:{ Exception -> 0x01cc }
                if (r8 == 0) goto L_0x0194
                r21 = 1
            L_0x0194:
                goto L_0x01bb
            L_0x0195:
                r8 = 0
                r0 = r6[r8]     // Catch:{ Exception -> 0x01cc }
                java.lang.String r8 = "url"
                boolean r0 = r0.startsWith(r8)     // Catch:{ Exception -> 0x01cc }
                if (r0 == 0) goto L_0x01aa
                r8 = 1
                r0 = r6[r8]     // Catch:{ Exception -> 0x01cc }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x01cc }
                r22 = r0
                goto L_0x01bb
            L_0x01aa:
                r8 = 0
                r0 = r6[r8]     // Catch:{ Exception -> 0x01cc }
                java.lang.String r8 = "itag"
                boolean r0 = r0.startsWith(r8)     // Catch:{ Exception -> 0x01cc }
                if (r0 == 0) goto L_0x01bb
                r0 = 0
                r8 = 0
                r22 = r0
                r21 = r8
            L_0x01bb:
                if (r21 == 0) goto L_0x01c3
                if (r22 == 0) goto L_0x01c3
                r0 = r22
                r14 = r0
                goto L_0x01d2
            L_0x01c3:
                int r5 = r5 + 1
                r6 = r23
                r0 = r24
                r8 = r25
                goto L_0x0168
            L_0x01cc:
                r0 = move-exception
                goto L_0x01dc
            L_0x01ce:
                r24 = r0
                r25 = r8
            L_0x01d2:
                goto L_0x01e4
            L_0x01d3:
                r0 = move-exception
                r25 = r8
                goto L_0x01dc
            L_0x01d7:
                r0 = move-exception
                r23 = r6
                r25 = r8
            L_0x01dc:
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x01e4
            L_0x01e0:
                r23 = r6
                r25 = r8
            L_0x01e4:
                r6 = r23
                goto L_0x0265
            L_0x01e8:
                r20 = r5
                r23 = r6
                r25 = r8
                r0 = r4[r13]
                java.lang.String r5 = "use_cipher_signature"
                boolean r0 = r0.startsWith(r5)
                if (r0 == 0) goto L_0x0216
                r0 = r4[r13]
                java.lang.String[] r0 = r0.split(r7)
                int r5 = r0.length
                r6 = 2
                if (r5 != r6) goto L_0x0213
                r5 = 1
                r6 = r0[r5]
                java.lang.String r5 = r6.toLowerCase()
                java.lang.String r6 = "true"
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x0213
                r6 = 1
                goto L_0x0215
            L_0x0213:
                r6 = r23
            L_0x0215:
                goto L_0x0265
            L_0x0216:
                r0 = r4[r13]
                java.lang.String r5 = "hlsvp"
                boolean r0 = r0.startsWith(r5)
                if (r0 == 0) goto L_0x023b
                r0 = r4[r13]
                java.lang.String[] r5 = r0.split(r7)
                int r0 = r5.length
                r6 = 2
                if (r0 != r6) goto L_0x0238
                r6 = 1
                r0 = r5[r6]     // Catch:{ Exception -> 0x0234 }
                java.lang.String r0 = java.net.URLDecoder.decode(r0, r2)     // Catch:{ Exception -> 0x0234 }
                r16 = r0
                goto L_0x0238
            L_0x0234:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0238:
                r6 = r23
                goto L_0x0265
            L_0x023b:
                r0 = r4[r13]
                java.lang.String r5 = "livestream"
                boolean r0 = r0.startsWith(r5)
                if (r0 == 0) goto L_0x0263
                r0 = r4[r13]
                java.lang.String[] r0 = r0.split(r7)
                int r5 = r0.length
                r6 = 2
                if (r5 != r6) goto L_0x0263
                r5 = 1
                r6 = r0[r5]
                java.lang.String r5 = r6.toLowerCase()
                java.lang.String r6 = "1"
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x0263
                r17 = 1
                r6 = r23
                goto L_0x0265
            L_0x0263:
                r6 = r23
            L_0x0265:
                int r13 = r13 + 1
                r5 = r20
                r8 = r25
                r7 = 1
                r12 = 2
                goto L_0x0108
            L_0x026f:
                r20 = r5
                r23 = r6
                r25 = r8
                r6 = r14
                r14 = r15
                r15 = r16
                r16 = r17
                r0 = r23
                goto L_0x0282
            L_0x027e:
                r20 = r5
                r25 = r8
            L_0x0282:
                if (r16 == 0) goto L_0x029c
                if (r15 == 0) goto L_0x029a
                if (r0 != 0) goto L_0x029a
                boolean r4 = r15.contains(r11)
                if (r4 == 0) goto L_0x028f
                goto L_0x029a
            L_0x028f:
                java.lang.String[] r4 = r1.result
                r5 = 0
                r4[r5] = r15
                java.lang.String r5 = "hls"
                r7 = 1
                r4[r7] = r5
                goto L_0x029c
            L_0x029a:
                r2 = 0
                return r2
            L_0x029c:
                if (r14 == 0) goto L_0x029f
                goto L_0x02ad
            L_0x029f:
                int r9 = r9 + 1
                r5 = r20
                r8 = r25
                r4 = 0
                r7 = 1
                goto L_0x00c8
            L_0x02a9:
                r20 = r5
                r25 = r8
            L_0x02ad:
                java.lang.String[] r2 = r1.result
                r4 = 0
                r5 = r2[r4]
                if (r5 != 0) goto L_0x02bd
                if (r6 == 0) goto L_0x02bd
                r2[r4] = r6
                java.lang.String r5 = "other"
                r7 = 1
                r2[r7] = r5
            L_0x02bd:
                java.lang.String[] r2 = r1.result
                r5 = r2[r4]
                if (r5 == 0) goto L_0x04af
                if (r0 != 0) goto L_0x02cd
                r2 = r2[r4]
                boolean r2 = r2.contains(r11)
                if (r2 == 0) goto L_0x04af
            L_0x02cd:
                if (r3 == 0) goto L_0x04af
                r2 = 1
                java.lang.String[] r0 = r1.result
                r4 = 0
                r0 = r0[r4]
                int r5 = r0.indexOf(r11)
                java.lang.String[] r0 = r1.result
                r0 = r0[r4]
                r4 = 47
                int r7 = r5 + 10
                int r0 = r0.indexOf(r4, r7)
                r4 = -1
                if (r5 == r4) goto L_0x04ae
                if (r0 != r4) goto L_0x02f5
                java.lang.String[] r4 = r1.result
                r7 = 0
                r4 = r4[r7]
                int r0 = r4.length()
                r4 = r0
                goto L_0x02f7
            L_0x02f5:
                r7 = 0
                r4 = r0
            L_0x02f7:
                java.lang.String[] r0 = r1.result
                r0 = r0[r7]
                java.lang.String r0 = r0.substring(r5, r4)
                r1.sig = r0
                r7 = 0
                java.util.regex.Pattern r0 = im.bclpbkiauv.ui.components.WebPlayerView.jsPattern
                java.util.regex.Matcher r8 = r0.matcher(r3)
                boolean r0 = r8.find()
                if (r0 == 0) goto L_0x032b
                org.json.JSONTokener r0 = new org.json.JSONTokener     // Catch:{ Exception -> 0x0327 }
                r9 = 1
                java.lang.String r10 = r8.group(r9)     // Catch:{ Exception -> 0x0327 }
                r0.<init>(r10)     // Catch:{ Exception -> 0x0327 }
                java.lang.Object r9 = r0.nextValue()     // Catch:{ Exception -> 0x0327 }
                boolean r10 = r9 instanceof java.lang.String     // Catch:{ Exception -> 0x0327 }
                if (r10 == 0) goto L_0x0326
                r10 = r9
                java.lang.String r10 = (java.lang.String) r10     // Catch:{ Exception -> 0x0327 }
                r7 = r10
            L_0x0326:
                goto L_0x032b
            L_0x0327:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x032b:
                if (r7 == 0) goto L_0x04ae
                java.util.regex.Pattern r0 = im.bclpbkiauv.ui.components.WebPlayerView.playerIdPattern
                java.util.regex.Matcher r0 = r0.matcher(r7)
                boolean r8 = r0.find()
                if (r8 == 0) goto L_0x0355
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r9 = 1
                java.lang.String r10 = r0.group(r9)
                r8.append(r10)
                r9 = 2
                java.lang.String r9 = r0.group(r9)
                r8.append(r9)
                java.lang.String r8 = r8.toString()
                goto L_0x0356
            L_0x0355:
                r8 = 0
            L_0x0356:
                r9 = 0
                r10 = 0
                android.content.Context r11 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
                java.lang.String r12 = "youtubecode"
                r13 = 0
                android.content.SharedPreferences r11 = r11.getSharedPreferences(r12, r13)
                java.lang.String r12 = "n"
                if (r8 == 0) goto L_0x037d
                r13 = 0
                java.lang.String r9 = r11.getString(r8, r13)
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                r14.append(r8)
                r14.append(r12)
                java.lang.String r14 = r14.toString()
                java.lang.String r10 = r11.getString(r14, r13)
            L_0x037d:
                if (r9 != 0) goto L_0x0433
                java.lang.String r13 = "//"
                boolean r13 = r7.startsWith(r13)
                if (r13 == 0) goto L_0x0399
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "https:"
                r13.append(r14)
                r13.append(r7)
                java.lang.String r7 = r13.toString()
                goto L_0x03b2
            L_0x0399:
                java.lang.String r13 = "/"
                boolean r13 = r7.startsWith(r13)
                if (r13 == 0) goto L_0x03b2
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "https://www.youtube.com"
                r13.append(r14)
                r13.append(r7)
                java.lang.String r7 = r13.toString()
            L_0x03b2:
                im.bclpbkiauv.ui.components.WebPlayerView r13 = im.bclpbkiauv.ui.components.WebPlayerView.this
                java.lang.String r13 = r13.downloadUrlContent(r1, r7)
                boolean r14 = r27.isCancelled()
                if (r14 == 0) goto L_0x03c0
                r14 = 0
                return r14
            L_0x03c0:
                r14 = 0
                if (r13 == 0) goto L_0x0430
                java.util.regex.Pattern r15 = im.bclpbkiauv.ui.components.WebPlayerView.sigPattern
                java.util.regex.Matcher r0 = r15.matcher(r13)
                boolean r15 = r0.find()
                if (r15 == 0) goto L_0x03d9
                r15 = 1
                java.lang.String r10 = r0.group(r15)
                r14 = r10
                r10 = r0
                goto L_0x03f1
            L_0x03d9:
                r15 = 1
                java.util.regex.Pattern r14 = im.bclpbkiauv.ui.components.WebPlayerView.sigPattern2
                java.util.regex.Matcher r0 = r14.matcher(r13)
                boolean r14 = r0.find()
                if (r14 == 0) goto L_0x03ef
                java.lang.String r10 = r0.group(r15)
                r14 = r10
                r10 = r0
                goto L_0x03f1
            L_0x03ef:
                r14 = r10
                r10 = r0
            L_0x03f1:
                if (r14 == 0) goto L_0x0435
                im.bclpbkiauv.ui.components.WebPlayerView$JSExtractor r0 = new im.bclpbkiauv.ui.components.WebPlayerView$JSExtractor     // Catch:{ Exception -> 0x042b }
                im.bclpbkiauv.ui.components.WebPlayerView r15 = im.bclpbkiauv.ui.components.WebPlayerView.this     // Catch:{ Exception -> 0x042b }
                r0.<init>(r13)     // Catch:{ Exception -> 0x042b }
                java.lang.String r15 = r0.extractFunction(r14)     // Catch:{ Exception -> 0x042b }
                r9 = r15
                boolean r15 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x042b }
                if (r15 != 0) goto L_0x0428
                if (r8 == 0) goto L_0x0428
                android.content.SharedPreferences$Editor r15 = r11.edit()     // Catch:{ Exception -> 0x042b }
                android.content.SharedPreferences$Editor r15 = r15.putString(r8, r9)     // Catch:{ Exception -> 0x042b }
                r16 = r0
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x042b }
                r0.<init>()     // Catch:{ Exception -> 0x042b }
                r0.append(r8)     // Catch:{ Exception -> 0x042b }
                r0.append(r12)     // Catch:{ Exception -> 0x042b }
                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x042b }
                android.content.SharedPreferences$Editor r0 = r15.putString(r0, r14)     // Catch:{ Exception -> 0x042b }
                r0.commit()     // Catch:{ Exception -> 0x042b }
                goto L_0x042a
            L_0x0428:
                r16 = r0
            L_0x042a:
                goto L_0x0435
            L_0x042b:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0435
            L_0x0430:
                r14 = r10
                r10 = r0
                goto L_0x0435
            L_0x0433:
                r14 = r10
                r10 = r0
            L_0x0435:
                boolean r0 = android.text.TextUtils.isEmpty(r9)
                if (r0 != 0) goto L_0x04ae
                int r0 = android.os.Build.VERSION.SDK_INT
                r12 = 21
                r13 = 3
                java.lang.String r15 = "('"
                if (r0 < r12) goto L_0x0466
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r9)
                r0.append(r14)
                r0.append(r15)
                java.lang.String r12 = r1.sig
                java.lang.String r12 = r12.substring(r13)
                r0.append(r12)
                java.lang.String r12 = "');"
                r0.append(r12)
                java.lang.String r0 = r0.toString()
                r9 = r0
                goto L_0x049a
            L_0x0466:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r9)
                java.lang.String r12 = "window."
                r0.append(r12)
                im.bclpbkiauv.ui.components.WebPlayerView r12 = im.bclpbkiauv.ui.components.WebPlayerView.this
                java.lang.String r12 = r12.interfaceName
                r0.append(r12)
                java.lang.String r12 = ".returnResultToJava("
                r0.append(r12)
                r0.append(r14)
                r0.append(r15)
                java.lang.String r12 = r1.sig
                java.lang.String r12 = r12.substring(r13)
                r0.append(r12)
                java.lang.String r12 = "'));"
                r0.append(r12)
                java.lang.String r0 = r0.toString()
                r9 = r0
            L_0x049a:
                r12 = r9
                im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$YoutubeVideoTask$9WJAclqQaVWotPT0YWfJcPRlt8s r0 = new im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$YoutubeVideoTask$9WJAclqQaVWotPT0YWfJcPRlt8s     // Catch:{ Exception -> 0x04aa }
                r0.<init>(r12)     // Catch:{ Exception -> 0x04aa }
                im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x04aa }
                java.util.concurrent.CountDownLatch r0 = r1.countDownLatch     // Catch:{ Exception -> 0x04aa }
                r0.await()     // Catch:{ Exception -> 0x04aa }
                r0 = 0
                goto L_0x04af
            L_0x04aa:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x04ae:
                r0 = r2
            L_0x04af:
                boolean r2 = r27.isCancelled()
                if (r2 != 0) goto L_0x04bb
                if (r0 == 0) goto L_0x04b8
                goto L_0x04bb
            L_0x04b8:
                java.lang.String[] r4 = r1.result
                goto L_0x04bc
            L_0x04bb:
                r4 = 0
            L_0x04bc:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.WebPlayerView.YoutubeVideoTask.doInBackground(java.lang.Void[]):java.lang.String[]");
        }

        public /* synthetic */ void lambda$doInBackground$1$WebPlayerView$YoutubeVideoTask(String functionCodeFinal) {
            if (Build.VERSION.SDK_INT >= 21) {
                WebPlayerView.this.webView.evaluateJavascript(functionCodeFinal, new ValueCallback() {
                    public final void onReceiveValue(Object obj) {
                        WebPlayerView.YoutubeVideoTask.this.lambda$null$0$WebPlayerView$YoutubeVideoTask((String) obj);
                    }
                });
                return;
            }
            try {
                String base64 = Base64.encodeToString(("<script>" + functionCodeFinal + "</script>").getBytes(StandardCharsets.UTF_8), 0);
                WebView access$2100 = WebPlayerView.this.webView;
                access$2100.loadUrl("data:text/html;charset=utf-8;base64," + base64);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public /* synthetic */ void lambda$null$0$WebPlayerView$YoutubeVideoTask(String value) {
            String[] strArr = this.result;
            String str = strArr[0];
            String str2 = this.sig;
            strArr[0] = str.replace(str2, "/signature/" + value.substring(1, value.length() - 1));
            this.countDownLatch.countDown();
        }

        /* access modifiers changed from: private */
        public void onInterfaceResult(String value) {
            String[] strArr = this.result;
            String str = strArr[0];
            String str2 = this.sig;
            strArr[0] = str.replace(str2, "/signature/" + value);
            this.countDownLatch.countDown();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String[] result2) {
            if (result2[0] != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start play youtube video " + result2[1] + " " + result2[0]);
                }
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = result2[0];
                String unused3 = WebPlayerView.this.playVideoType = result2[1];
                if (WebPlayerView.this.playVideoType.equals(DownloadAction.TYPE_HLS)) {
                    boolean unused4 = WebPlayerView.this.isStream = true;
                }
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class VimeoVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public VimeoVideoTask(String vid) {
            this.videoId = vid;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://player.vimeo.com/video/%s/config", new Object[]{this.videoId}));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject files = new JSONObject(playerCode).getJSONObject("request").getJSONObject("files");
                if (files.has(DownloadAction.TYPE_HLS)) {
                    JSONObject hls = files.getJSONObject(DownloadAction.TYPE_HLS);
                    try {
                        this.results[0] = hls.getString(ImagesContract.URL);
                    } catch (Exception e) {
                        this.results[0] = hls.getJSONObject("cdns").getJSONObject(hls.getString("default_cdn")).getString(ImagesContract.URL);
                    }
                    this.results[1] = DownloadAction.TYPE_HLS;
                } else if (files.has(DownloadAction.TYPE_PROGRESSIVE)) {
                    this.results[1] = "other";
                    this.results[0] = files.getJSONArray(DownloadAction.TYPE_PROGRESSIVE).getJSONObject(0).getString(ImagesContract.URL);
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            if (result != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = result;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class AparatVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[2];
        private String videoId;

        public AparatVideoTask(String vid) {
            this.videoId = vid;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", new Object[]{this.videoId}));
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher filelist = WebPlayerView.aparatFileListPattern.matcher(playerCode);
                if (filelist.find()) {
                    JSONArray json = new JSONArray(filelist.group(1));
                    for (int a = 0; a < json.length(); a++) {
                        JSONArray array = json.getJSONArray(a);
                        if (array.length() != 0) {
                            JSONObject object = array.getJSONObject(0);
                            if (object.has("file")) {
                                this.results[0] = object.getString("file");
                                this.results[1] = "other";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            if (result != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = result;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class TwitchClipVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String currentUrl;
        private String[] results = new String[2];
        private String videoId;

        public TwitchClipVideoTask(String url, String vid) {
            this.videoId = vid;
            this.currentUrl = url;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, this.currentUrl, (HashMap<String, String>) null, false);
            if (isCancelled()) {
                return null;
            }
            try {
                Matcher filelist = WebPlayerView.twitchClipFilePattern.matcher(playerCode);
                if (filelist.find()) {
                    this.results[0] = new JSONObject(filelist.group(1)).getJSONArray("quality_options").getJSONObject(0).getString("source");
                    this.results[1] = "other";
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            if (result != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = result;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class TwitchStreamVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String currentUrl;
        private String[] results = new String[2];
        private String videoId;

        public TwitchStreamVideoTask(String url, String vid) {
            this.videoId = vid;
            this.currentUrl = url;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voids) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Client-ID", "jzkbprff40iqj646a697cyrvl0zt2m6");
            int indexOf = this.videoId.indexOf(38);
            int idx = indexOf;
            if (indexOf > 0) {
                this.videoId = this.videoId.substring(0, idx);
            }
            String streamCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/kraken/streams/%s?stream_type=all", new Object[]{this.videoId}), headers, false);
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject jSONObject = new JSONObject(streamCode).getJSONObject("stream");
                JSONObject accessToken = new JSONObject(WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/api/channels/%s/access_token", new Object[]{this.videoId}), headers, false));
                String sig = URLEncoder.encode(accessToken.getString("sig"), "UTF-8");
                String token = URLEncoder.encode(accessToken.getString("token"), "UTF-8");
                URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
                this.results[0] = String.format(Locale.US, "https://usher.ttvnw.net/api/channel/hls/%s.m3u8?%s", new Object[]{this.videoId, "allow_source=true&allow_audio_only=true&allow_spectre=true&player=twitchweb&segment_preference=4&p=" + ((int) (Math.random() * 1.0E7d)) + "&sig=" + sig + "&token=" + token});
                this.results[1] = DownloadAction.TYPE_HLS;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            if (result != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = result;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class CoubVideoTask extends AsyncTask<Void, Void, String> {
        private boolean canRetry = true;
        private String[] results = new String[4];
        private String videoId;

        public CoubVideoTask(String vid) {
            this.videoId = vid;
        }

        private String decodeUrl(String input) {
            StringBuilder source = new StringBuilder(input);
            for (int a = 0; a < source.length(); a++) {
                char c = source.charAt(a);
                char lower = Character.toLowerCase(c);
                source.setCharAt(a, c == lower ? Character.toUpperCase(c) : lower);
            }
            try {
                return new String(Base64.decode(source.toString(), 0), StandardCharsets.UTF_8);
            } catch (Exception e) {
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voids) {
            String playerCode = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", new Object[]{this.videoId}));
            if (isCancelled()) {
                return null;
            }
            try {
                JSONObject json = new JSONObject(playerCode).getJSONObject("file_versions").getJSONObject("mobile");
                String video = decodeUrl(json.getString("gifv"));
                String audio = json.getJSONArray(MimeTypes.BASE_TYPE_AUDIO).getString(0);
                if (!(video == null || audio == null)) {
                    this.results[0] = video;
                    this.results[1] = "other";
                    this.results[2] = audio;
                    this.results[3] = "other";
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (isCancelled()) {
                return null;
            }
            return this.results[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            if (result != null) {
                boolean unused = WebPlayerView.this.initied = true;
                String unused2 = WebPlayerView.this.playVideoUrl = result;
                String unused3 = WebPlayerView.this.playVideoType = this.results[1];
                String unused4 = WebPlayerView.this.playAudioUrl = this.results[2];
                String unused5 = WebPlayerView.this.playAudioType = this.results[3];
                if (WebPlayerView.this.isAutoplay) {
                    WebPlayerView.this.preparePlayer();
                }
                WebPlayerView.this.showProgress(false, true);
                WebPlayerView.this.controlsView.show(true, true);
            } else if (!isCancelled()) {
                WebPlayerView.this.onInitFailed();
            }
        }
    }

    private class ControlsView extends FrameLayout {
        private int bufferedPosition;
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation;
        private int currentProgressX;
        private int duration;
        private StaticLayout durationLayout;
        private int durationWidth;
        private Runnable hideRunnable = new Runnable() {
            public final void run() {
                WebPlayerView.ControlsView.this.lambda$new$0$WebPlayerView$ControlsView();
            }
        };
        /* access modifiers changed from: private */
        public ImageReceiver imageReceiver;
        private boolean isVisible = true;
        private int lastProgressX;
        private int progress;
        private Paint progressBufferedPaint;
        private Paint progressInnerPaint;
        private StaticLayout progressLayout;
        private Paint progressPaint;
        private boolean progressPressed;
        private TextPaint textPaint;

        public /* synthetic */ void lambda$new$0$WebPlayerView$ControlsView() {
            show(false, true);
        }

        public ControlsView(Context context) {
            super(context);
            setWillNotDraw(false);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setColor(-1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            Paint paint = new Paint(1);
            this.progressPaint = paint;
            paint.setColor(-15095832);
            Paint paint2 = new Paint();
            this.progressInnerPaint = paint2;
            paint2.setColor(-6975081);
            Paint paint3 = new Paint(1);
            this.progressBufferedPaint = paint3;
            paint3.setColor(-1);
            this.imageReceiver = new ImageReceiver(this);
        }

        public void setDuration(int value) {
            if (this.duration != value && value >= 0 && !WebPlayerView.this.isStream) {
                this.duration = value;
                StaticLayout staticLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.durationLayout = staticLayout;
                if (staticLayout.getLineCount() > 0) {
                    this.durationWidth = (int) Math.ceil((double) this.durationLayout.getLineWidth(0));
                }
                invalidate();
            }
        }

        public void setBufferedProgress(int position) {
            this.bufferedPosition = position;
            invalidate();
        }

        public void setProgress(int value) {
            if (!this.progressPressed && value >= 0 && !WebPlayerView.this.isStream) {
                this.progress = value;
                this.progressLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[]{Integer.valueOf(this.progress / 60), Integer.valueOf(this.progress % 60)}), this.textPaint, AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                invalidate();
            }
        }

        public void show(boolean value, boolean animated) {
            if (this.isVisible != value) {
                this.isVisible = value;
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                if (this.isVisible) {
                    if (animated) {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        this.currentAnimation = animatorSet2;
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0f})});
                        this.currentAnimation.setDuration(150);
                        this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = ControlsView.this.currentAnimation = null;
                            }
                        });
                        this.currentAnimation.start();
                    } else {
                        setAlpha(1.0f);
                    }
                } else if (animated) {
                    AnimatorSet animatorSet3 = new AnimatorSet();
                    this.currentAnimation = animatorSet3;
                    animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f})});
                    this.currentAnimation.setDuration(150);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet unused = ControlsView.this.currentAnimation = null;
                        }
                    });
                    this.currentAnimation.start();
                } else {
                    setAlpha(0.0f);
                }
                checkNeedHide();
            }
        }

        /* access modifiers changed from: private */
        public void checkNeedHide() {
            AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            if (this.isVisible && WebPlayerView.this.videoPlayer.isPlaying()) {
                AndroidUtilities.runOnUIThread(this.hideRunnable, 3000);
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() != 0) {
                return super.onInterceptTouchEvent(ev);
            }
            if (!this.isVisible) {
                show(true, true);
                return true;
            }
            onTouchEvent(ev);
            return this.progressPressed;
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
            checkNeedHide();
        }

        public boolean onTouchEvent(MotionEvent event) {
            int progressY;
            int progressLineEndX;
            int progressLineX;
            if (WebPlayerView.this.inFullscreen) {
                progressLineX = AndroidUtilities.dp(36.0f) + this.durationWidth;
                progressLineEndX = (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - this.durationWidth;
                progressY = getMeasuredHeight() - AndroidUtilities.dp(28.0f);
            } else {
                progressLineX = 0;
                progressLineEndX = getMeasuredWidth();
                progressY = getMeasuredHeight() - AndroidUtilities.dp(12.0f);
            }
            int i = this.duration;
            int progressX = (i != 0 ? (int) (((float) (progressLineEndX - progressLineX)) * (((float) this.progress) / ((float) i))) : 0) + progressLineX;
            if (event.getAction() == 0) {
                if (!this.isVisible || WebPlayerView.this.isInline || WebPlayerView.this.isStream) {
                    show(true, true);
                } else if (this.duration != 0) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    if (x >= progressX - AndroidUtilities.dp(10.0f) && x <= AndroidUtilities.dp(10.0f) + progressX && y >= progressY - AndroidUtilities.dp(10.0f) && y <= AndroidUtilities.dp(10.0f) + progressY) {
                        this.progressPressed = true;
                        this.lastProgressX = x;
                        this.currentProgressX = progressX;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        invalidate();
                    }
                }
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (WebPlayerView.this.initied && WebPlayerView.this.videoPlayer.isPlaying()) {
                    AndroidUtilities.runOnUIThread(this.hideRunnable, 3000);
                }
                if (this.progressPressed) {
                    this.progressPressed = false;
                    if (WebPlayerView.this.initied) {
                        this.progress = (int) (((float) this.duration) * (((float) (this.currentProgressX - progressLineX)) / ((float) (progressLineEndX - progressLineX))));
                        WebPlayerView.this.videoPlayer.seekTo(((long) this.progress) * 1000);
                    }
                }
            } else if (event.getAction() == 2 && this.progressPressed) {
                int x2 = (int) event.getX();
                int i2 = this.currentProgressX - (this.lastProgressX - x2);
                this.currentProgressX = i2;
                this.lastProgressX = x2;
                if (i2 < progressLineX) {
                    this.currentProgressX = progressLineX;
                } else if (i2 > progressLineEndX) {
                    this.currentProgressX = progressLineEndX;
                }
                setProgress((int) (((float) (this.duration * 1000)) * (((float) (this.currentProgressX - progressLineX)) / ((float) (progressLineEndX - progressLineX)))));
                invalidate();
            }
            super.onTouchEvent(event);
            return true;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int cy;
            int progressLineEndX;
            int progressLineX;
            int progressLineY;
            int progressX;
            int progressX2;
            int i;
            Canvas canvas2 = canvas;
            if (WebPlayerView.this.drawImage) {
                if (WebPlayerView.this.firstFrameRendered && WebPlayerView.this.currentAlpha != 0.0f) {
                    long newTime = System.currentTimeMillis();
                    long unused = WebPlayerView.this.lastUpdateTime = newTime;
                    WebPlayerView webPlayerView = WebPlayerView.this;
                    float unused2 = webPlayerView.currentAlpha = webPlayerView.currentAlpha - (((float) (newTime - WebPlayerView.this.lastUpdateTime)) / 150.0f);
                    if (WebPlayerView.this.currentAlpha < 0.0f) {
                        float unused3 = WebPlayerView.this.currentAlpha = 0.0f;
                    }
                    invalidate();
                }
                this.imageReceiver.setAlpha(WebPlayerView.this.currentAlpha);
                this.imageReceiver.draw(canvas2);
            }
            if (WebPlayerView.this.videoPlayer.isPlayerPrepared() && !WebPlayerView.this.isStream) {
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                if (!WebPlayerView.this.isInline) {
                    int i2 = 6;
                    if (this.durationLayout != null) {
                        canvas.save();
                        canvas2.translate((float) ((width - AndroidUtilities.dp(58.0f)) - this.durationWidth), (float) (height - AndroidUtilities.dp((float) ((WebPlayerView.this.inFullscreen ? 6 : 10) + 29))));
                        this.durationLayout.draw(canvas2);
                        canvas.restore();
                    }
                    if (this.progressLayout != null) {
                        canvas.save();
                        float dp = (float) AndroidUtilities.dp(18.0f);
                        if (!WebPlayerView.this.inFullscreen) {
                            i2 = 10;
                        }
                        canvas2.translate(dp, (float) (height - AndroidUtilities.dp((float) (i2 + 29))));
                        this.progressLayout.draw(canvas2);
                        canvas.restore();
                    }
                }
                if (this.duration != 0) {
                    if (WebPlayerView.this.isInline) {
                        progressLineY = height - AndroidUtilities.dp(3.0f);
                        progressLineX = 0;
                        progressLineEndX = width;
                        cy = height - AndroidUtilities.dp(7.0f);
                    } else if (WebPlayerView.this.inFullscreen) {
                        int progressLineX2 = AndroidUtilities.dp(36.0f) + this.durationWidth;
                        int progressLineEndX2 = (width - AndroidUtilities.dp(76.0f)) - this.durationWidth;
                        progressLineY = height - AndroidUtilities.dp(29.0f);
                        progressLineX = progressLineX2;
                        progressLineEndX = progressLineEndX2;
                        cy = height - AndroidUtilities.dp(28.0f);
                    } else {
                        progressLineY = height - AndroidUtilities.dp(13.0f);
                        progressLineX = 0;
                        progressLineEndX = width;
                        cy = height - AndroidUtilities.dp(12.0f);
                    }
                    if (WebPlayerView.this.inFullscreen) {
                        canvas.drawRect((float) progressLineX, (float) progressLineY, (float) progressLineEndX, (float) (AndroidUtilities.dp(3.0f) + progressLineY), this.progressInnerPaint);
                    }
                    if (this.progressPressed) {
                        progressX = this.currentProgressX;
                    } else {
                        progressX = ((int) (((float) (progressLineEndX - progressLineX)) * (((float) this.progress) / ((float) this.duration)))) + progressLineX;
                    }
                    int i3 = this.bufferedPosition;
                    if (i3 == 0 || (i = this.duration) == 0) {
                        progressX2 = progressX;
                    } else {
                        progressX2 = progressX;
                        canvas.drawRect((float) progressLineX, (float) progressLineY, ((float) progressLineX) + (((float) (progressLineEndX - progressLineX)) * (((float) i3) / ((float) i))), (float) (AndroidUtilities.dp(3.0f) + progressLineY), WebPlayerView.this.inFullscreen ? this.progressBufferedPaint : this.progressInnerPaint);
                    }
                    canvas.drawRect((float) progressLineX, (float) progressLineY, (float) progressX2, (float) (AndroidUtilities.dp(3.0f) + progressLineY), this.progressPaint);
                    if (!WebPlayerView.this.isInline) {
                        canvas2.drawCircle((float) progressX2, (float) cy, (float) AndroidUtilities.dp(this.progressPressed ? 7.0f : 5.0f), this.progressPaint);
                    }
                }
            }
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public WebPlayerView(Context context, boolean allowInline, boolean allowShare, WebPlayerViewDelegate webPlayerViewDelegate) {
        super(context);
        Context context2 = context;
        int i = lastContainerId;
        lastContainerId = i + 1;
        this.fragment_container_id = i;
        this.allowInlineAnimation = Build.VERSION.SDK_INT >= 21;
        this.backgroundPaint = new Paint();
        this.progressRunnable = new Runnable() {
            public void run() {
                if (WebPlayerView.this.videoPlayer != null && WebPlayerView.this.videoPlayer.isPlaying()) {
                    WebPlayerView.this.controlsView.setProgress((int) (WebPlayerView.this.videoPlayer.getCurrentPosition() / 1000));
                    WebPlayerView.this.controlsView.setBufferedProgress((int) (WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000));
                    AndroidUtilities.runOnUIThread(WebPlayerView.this.progressRunnable, 1000);
                }
            }
        };
        this.surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (!WebPlayerView.this.changingTextureView) {
                    return true;
                }
                if (WebPlayerView.this.switchingInlineMode) {
                    int unused = WebPlayerView.this.waitingForFirstTextureUpload = 2;
                }
                WebPlayerView.this.textureView.setSurfaceTexture(surface);
                WebPlayerView.this.textureView.setVisibility(0);
                boolean unused2 = WebPlayerView.this.changingTextureView = false;
                return false;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                if (WebPlayerView.this.waitingForFirstTextureUpload == 1) {
                    WebPlayerView.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            WebPlayerView.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                            if (WebPlayerView.this.textureImageView != null) {
                                WebPlayerView.this.textureImageView.setVisibility(4);
                                WebPlayerView.this.textureImageView.setImageDrawable((Drawable) null);
                                if (WebPlayerView.this.currentBitmap != null) {
                                    WebPlayerView.this.currentBitmap.recycle();
                                    Bitmap unused = WebPlayerView.this.currentBitmap = null;
                                }
                            }
                            AndroidUtilities.runOnUIThread(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0052: INVOKE  
                                  (wrap: im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$2$1$zo5QdaetGeuhhJcf1ZuSFdJ_awM : 0x004f: CONSTRUCTOR  (r0v7 im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$2$1$zo5QdaetGeuhhJcf1ZuSFdJ_awM) = 
                                  (r2v0 'this' im.bclpbkiauv.ui.components.WebPlayerView$2$1 A[THIS])
                                 call: im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$2$1$zo5QdaetGeuhhJcf1ZuSFdJ_awM.<init>(im.bclpbkiauv.ui.components.WebPlayerView$2$1):void type: CONSTRUCTOR)
                                 im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.components.WebPlayerView.2.1.onPreDraw():boolean, dex: classes6.dex
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
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
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
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x004f: CONSTRUCTOR  (r0v7 im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$2$1$zo5QdaetGeuhhJcf1ZuSFdJ_awM) = 
                                  (r2v0 'this' im.bclpbkiauv.ui.components.WebPlayerView$2$1 A[THIS])
                                 call: im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$2$1$zo5QdaetGeuhhJcf1ZuSFdJ_awM.<init>(im.bclpbkiauv.ui.components.WebPlayerView$2$1):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.WebPlayerView.2.1.onPreDraw():boolean, dex: classes6.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 100 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$2$1$zo5QdaetGeuhhJcf1ZuSFdJ_awM, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 106 more
                                */
                            /*
                                this = this;
                                im.bclpbkiauv.ui.components.WebPlayerView$2 r0 = im.bclpbkiauv.ui.components.WebPlayerView.AnonymousClass2.this
                                im.bclpbkiauv.ui.components.WebPlayerView r0 = im.bclpbkiauv.ui.components.WebPlayerView.this
                                android.view.TextureView r0 = r0.changedTextureView
                                android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
                                r0.removeOnPreDrawListener(r2)
                                im.bclpbkiauv.ui.components.WebPlayerView$2 r0 = im.bclpbkiauv.ui.components.WebPlayerView.AnonymousClass2.this
                                im.bclpbkiauv.ui.components.WebPlayerView r0 = im.bclpbkiauv.ui.components.WebPlayerView.this
                                android.widget.ImageView r0 = r0.textureImageView
                                if (r0 == 0) goto L_0x004d
                                im.bclpbkiauv.ui.components.WebPlayerView$2 r0 = im.bclpbkiauv.ui.components.WebPlayerView.AnonymousClass2.this
                                im.bclpbkiauv.ui.components.WebPlayerView r0 = im.bclpbkiauv.ui.components.WebPlayerView.this
                                android.widget.ImageView r0 = r0.textureImageView
                                r1 = 4
                                r0.setVisibility(r1)
                                im.bclpbkiauv.ui.components.WebPlayerView$2 r0 = im.bclpbkiauv.ui.components.WebPlayerView.AnonymousClass2.this
                                im.bclpbkiauv.ui.components.WebPlayerView r0 = im.bclpbkiauv.ui.components.WebPlayerView.this
                                android.widget.ImageView r0 = r0.textureImageView
                                r1 = 0
                                r0.setImageDrawable(r1)
                                im.bclpbkiauv.ui.components.WebPlayerView$2 r0 = im.bclpbkiauv.ui.components.WebPlayerView.AnonymousClass2.this
                                im.bclpbkiauv.ui.components.WebPlayerView r0 = im.bclpbkiauv.ui.components.WebPlayerView.this
                                android.graphics.Bitmap r0 = r0.currentBitmap
                                if (r0 == 0) goto L_0x004d
                                im.bclpbkiauv.ui.components.WebPlayerView$2 r0 = im.bclpbkiauv.ui.components.WebPlayerView.AnonymousClass2.this
                                im.bclpbkiauv.ui.components.WebPlayerView r0 = im.bclpbkiauv.ui.components.WebPlayerView.this
                                android.graphics.Bitmap r0 = r0.currentBitmap
                                r0.recycle()
                                im.bclpbkiauv.ui.components.WebPlayerView$2 r0 = im.bclpbkiauv.ui.components.WebPlayerView.AnonymousClass2.this
                                im.bclpbkiauv.ui.components.WebPlayerView r0 = im.bclpbkiauv.ui.components.WebPlayerView.this
                                android.graphics.Bitmap unused = r0.currentBitmap = r1
                            L_0x004d:
                                im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$2$1$zo5QdaetGeuhhJcf1ZuSFdJ_awM r0 = new im.bclpbkiauv.ui.components.-$$Lambda$WebPlayerView$2$1$zo5QdaetGeuhhJcf1ZuSFdJ_awM
                                r0.<init>(r2)
                                im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                                im.bclpbkiauv.ui.components.WebPlayerView$2 r0 = im.bclpbkiauv.ui.components.WebPlayerView.AnonymousClass2.this
                                im.bclpbkiauv.ui.components.WebPlayerView r0 = im.bclpbkiauv.ui.components.WebPlayerView.this
                                r1 = 0
                                int unused = r0.waitingForFirstTextureUpload = r1
                                r0 = 1
                                return r0
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.WebPlayerView.AnonymousClass2.AnonymousClass1.onPreDraw():boolean");
                        }

                        public /* synthetic */ void lambda$onPreDraw$0$WebPlayerView$2$1() {
                            WebPlayerView.this.delegate.onInlineSurfaceTextureReady();
                        }
                    });
                    WebPlayerView.this.changedTextureView.invalidate();
                }
            }
        };
        this.switchToInlineRunnable = new Runnable() {
            public void run() {
                boolean unused = WebPlayerView.this.switchingInlineMode = false;
                if (WebPlayerView.this.currentBitmap != null) {
                    WebPlayerView.this.currentBitmap.recycle();
                    Bitmap unused2 = WebPlayerView.this.currentBitmap = null;
                }
                boolean unused3 = WebPlayerView.this.changingTextureView = true;
                if (WebPlayerView.this.textureImageView != null) {
                    try {
                        Bitmap unused4 = WebPlayerView.this.currentBitmap = Bitmaps.createBitmap(WebPlayerView.this.textureView.getWidth(), WebPlayerView.this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
                        WebPlayerView.this.textureView.getBitmap(WebPlayerView.this.currentBitmap);
                    } catch (Throwable e) {
                        if (WebPlayerView.this.currentBitmap != null) {
                            WebPlayerView.this.currentBitmap.recycle();
                            Bitmap unused5 = WebPlayerView.this.currentBitmap = null;
                        }
                        FileLog.e(e);
                    }
                    if (WebPlayerView.this.currentBitmap != null) {
                        WebPlayerView.this.textureImageView.setVisibility(0);
                        WebPlayerView.this.textureImageView.setImageBitmap(WebPlayerView.this.currentBitmap);
                    } else {
                        WebPlayerView.this.textureImageView.setImageDrawable((Drawable) null);
                    }
                }
                boolean unused6 = WebPlayerView.this.isInline = true;
                WebPlayerView.this.updatePlayButton();
                WebPlayerView.this.updateShareButton();
                WebPlayerView.this.updateFullscreenButton();
                WebPlayerView.this.updateInlineButton();
                ViewGroup viewGroup = (ViewGroup) WebPlayerView.this.controlsView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(WebPlayerView.this.controlsView);
                }
                WebPlayerView webPlayerView = WebPlayerView.this;
                TextureView unused7 = webPlayerView.changedTextureView = webPlayerView.delegate.onSwitchInlineMode(WebPlayerView.this.controlsView, WebPlayerView.this.isInline, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.aspectRatioFrameLayout.getVideoRotation(), WebPlayerView.this.allowInlineAnimation);
                WebPlayerView.this.changedTextureView.setVisibility(4);
                ViewGroup parent = (ViewGroup) WebPlayerView.this.textureView.getParent();
                if (parent != null) {
                    parent.removeView(WebPlayerView.this.textureView);
                }
                WebPlayerView.this.controlsView.show(false, false);
            }
        };
        setWillNotDraw(false);
        this.delegate = webPlayerViewDelegate;
        this.backgroundPaint.setColor(-16777216);
        AnonymousClass4 r3 = new AspectRatioFrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (WebPlayerView.this.textureViewContainer != null) {
                    ViewGroup.LayoutParams layoutParams = WebPlayerView.this.textureView.getLayoutParams();
                    layoutParams.width = getMeasuredWidth();
                    layoutParams.height = getMeasuredHeight();
                    if (WebPlayerView.this.textureImageView != null) {
                        ViewGroup.LayoutParams layoutParams2 = WebPlayerView.this.textureImageView.getLayoutParams();
                        layoutParams2.width = getMeasuredWidth();
                        layoutParams2.height = getMeasuredHeight();
                    }
                }
            }
        };
        this.aspectRatioFrameLayout = r3;
        addView(r3, LayoutHelper.createFrame(-1, -1, 17));
        this.interfaceName = "JavaScriptInterface";
        WebView webView2 = new WebView(context2);
        this.webView = webView2;
        webView2.addJavascriptInterface(new JavaScriptInterface(new CallJavaResultInterface() {
            public final void jsCallFinished(String str) {
                WebPlayerView.this.lambda$new$0$WebPlayerView(str);
            }
        }), this.interfaceName);
        WebSettings webSettings = this.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        this.textureViewContainer = this.delegate.getTextureViewContainer();
        TextureView textureView2 = new TextureView(context2);
        this.textureView = textureView2;
        textureView2.setPivotX(0.0f);
        this.textureView.setPivotY(0.0f);
        ViewGroup viewGroup = this.textureViewContainer;
        if (viewGroup != null) {
            viewGroup.addView(this.textureView);
        } else {
            this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        }
        if (this.allowInlineAnimation && this.textureViewContainer != null) {
            ImageView imageView = new ImageView(context2);
            this.textureImageView = imageView;
            imageView.setBackgroundColor(SupportMenu.CATEGORY_MASK);
            this.textureImageView.setPivotX(0.0f);
            this.textureImageView.setPivotY(0.0f);
            this.textureImageView.setVisibility(4);
            this.textureViewContainer.addView(this.textureImageView);
        }
        VideoPlayer videoPlayer2 = new VideoPlayer();
        this.videoPlayer = videoPlayer2;
        videoPlayer2.setDelegate(this);
        this.videoPlayer.setTextureView(this.textureView);
        ControlsView controlsView2 = new ControlsView(context2);
        this.controlsView = controlsView2;
        ViewGroup viewGroup2 = this.textureViewContainer;
        if (viewGroup2 != null) {
            viewGroup2.addView(controlsView2);
        } else {
            addView(controlsView2, LayoutHelper.createFrame(-1, -1.0f));
        }
        RadialProgressView radialProgressView = new RadialProgressView(context2);
        this.progressView = radialProgressView;
        radialProgressView.setProgressColor(-1);
        addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
        ImageView imageView2 = new ImageView(context2);
        this.fullscreenButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56.0f, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
        this.fullscreenButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WebPlayerView.this.lambda$new$1$WebPlayerView(view);
            }
        });
        ImageView imageView3 = new ImageView(context2);
        this.playButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WebPlayerView.this.lambda$new$2$WebPlayerView(view);
            }
        });
        if (allowInline) {
            ImageView imageView4 = new ImageView(context2);
            this.inlineButton = imageView4;
            imageView4.setScaleType(ImageView.ScaleType.CENTER);
            this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
            this.inlineButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WebPlayerView.this.lambda$new$3$WebPlayerView(view);
                }
            });
        }
        if (allowShare) {
            ImageView imageView5 = new ImageView(context2);
            this.shareButton = imageView5;
            imageView5.setScaleType(ImageView.ScaleType.CENTER);
            this.shareButton.setImageResource(R.drawable.ic_share_video);
            this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
            this.shareButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WebPlayerView.this.lambda$new$4$WebPlayerView(view);
                }
            });
        }
        updatePlayButton();
        updateFullscreenButton();
        updateInlineButton();
        updateShareButton();
    }

    public /* synthetic */ void lambda$new$0$WebPlayerView(String value) {
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null && !asyncTask.isCancelled()) {
            AsyncTask asyncTask2 = this.currentTask;
            if (asyncTask2 instanceof YoutubeVideoTask) {
                ((YoutubeVideoTask) asyncTask2).onInterfaceResult(value);
            }
        }
    }

    public /* synthetic */ void lambda$new$1$WebPlayerView(View v) {
        if (this.initied && !this.changingTextureView && !this.switchingInlineMode && this.firstFrameRendered) {
            this.inFullscreen = !this.inFullscreen;
            updateFullscreenState(true);
        }
    }

    public /* synthetic */ void lambda$new$2$WebPlayerView(View v) {
        if (this.initied && this.playVideoUrl != null) {
            if (!this.videoPlayer.isPlayerPrepared()) {
                preparePlayer();
            }
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
            } else {
                this.isCompleted = false;
                this.videoPlayer.play();
            }
            updatePlayButton();
        }
    }

    public /* synthetic */ void lambda$new$3$WebPlayerView(View v) {
        if (this.textureView != null && this.delegate.checkInlinePermissions() && !this.changingTextureView && !this.switchingInlineMode && this.firstFrameRendered) {
            this.switchingInlineMode = true;
            if (!this.isInline) {
                this.inFullscreen = false;
                this.delegate.prepareToSwitchInlineMode(true, this.switchToInlineRunnable, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
                return;
            }
            ViewGroup parent = (ViewGroup) this.aspectRatioFrameLayout.getParent();
            if (parent != this) {
                if (parent != null) {
                    parent.removeView(this.aspectRatioFrameLayout);
                }
                addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - AndroidUtilities.dp(10.0f), 1073741824));
            }
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.currentBitmap = null;
            }
            this.changingTextureView = true;
            this.isInline = false;
            updatePlayButton();
            updateShareButton();
            updateFullscreenButton();
            updateInlineButton();
            this.textureView.setVisibility(4);
            ViewGroup viewGroup = this.textureViewContainer;
            if (viewGroup != null) {
                viewGroup.addView(this.textureView);
            } else {
                this.aspectRatioFrameLayout.addView(this.textureView);
            }
            ViewGroup parent2 = (ViewGroup) this.controlsView.getParent();
            if (parent2 != this) {
                if (parent2 != null) {
                    parent2.removeView(this.controlsView);
                }
                ViewGroup viewGroup2 = this.textureViewContainer;
                if (viewGroup2 != null) {
                    viewGroup2.addView(this.controlsView);
                } else {
                    addView(this.controlsView, 1);
                }
            }
            this.controlsView.show(false, false);
            this.delegate.prepareToSwitchInlineMode(false, (Runnable) null, this.aspectRatioFrameLayout.getAspectRatio(), this.allowInlineAnimation);
        }
    }

    public /* synthetic */ void lambda$new$4$WebPlayerView(View v) {
        WebPlayerViewDelegate webPlayerViewDelegate = this.delegate;
        if (webPlayerViewDelegate != null) {
            webPlayerViewDelegate.onSharePressed();
        }
    }

    /* access modifiers changed from: private */
    public void onInitFailed() {
        if (this.controlsView.getParent() != this) {
            this.controlsView.setVisibility(8);
        }
        this.delegate.onInitFailed();
    }

    public void updateTextureImageView() {
        if (this.textureImageView != null) {
            try {
                Bitmap createBitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
                this.currentBitmap = createBitmap;
                this.changedTextureView.getBitmap(createBitmap);
            } catch (Throwable e) {
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.currentBitmap = null;
                }
                FileLog.e(e);
            }
            if (this.currentBitmap != null) {
                this.textureImageView.setVisibility(0);
                this.textureImageView.setImageBitmap(this.currentBitmap);
                return;
            }
            this.textureImageView.setImageDrawable((Drawable) null);
        }
    }

    public String getYoutubeId() {
        return this.currentYoutubeId;
    }

    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState != 2) {
            if (this.videoPlayer.getDuration() != C.TIME_UNSET) {
                this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
            } else {
                this.controlsView.setDuration(0);
            }
        }
        if (playbackState == 4 || playbackState == 1 || !this.videoPlayer.isPlaying()) {
            this.delegate.onPlayStateChanged(this, false);
        } else {
            this.delegate.onPlayStateChanged(this, true);
        }
        if (this.videoPlayer.isPlaying() && playbackState != 4) {
            updatePlayButton();
        } else if (playbackState == 4) {
            this.isCompleted = true;
            this.videoPlayer.pause();
            this.videoPlayer.seekTo(0);
            updatePlayButton();
            this.controlsView.show(true, true);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(10.0f)), this.backgroundPaint);
    }

    public void onError(Exception e) {
        FileLog.e((Throwable) e);
        onInitFailed();
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (this.aspectRatioFrameLayout != null) {
            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                int temp = width;
                width = height;
                height = temp;
            }
            float ratio = height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height);
            this.aspectRatioFrameLayout.setAspectRatio(ratio, unappliedRotationDegrees);
            if (this.inFullscreen) {
                this.delegate.onVideoSizeChanged(ratio, unappliedRotationDegrees);
            }
        }
    }

    public void onRenderedFirstFrame() {
        this.firstFrameRendered = true;
        this.lastUpdateTime = System.currentTimeMillis();
        this.controlsView.invalidate();
    }

    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        if (this.changingTextureView) {
            this.changingTextureView = false;
            if (this.inFullscreen || this.isInline) {
                if (this.isInline) {
                    this.waitingForFirstTextureUpload = 1;
                }
                this.changedTextureView.setSurfaceTexture(surfaceTexture);
                this.changedTextureView.setSurfaceTextureListener(this.surfaceTextureListener);
                this.changedTextureView.setVisibility(0);
                return true;
            }
        }
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        if (this.waitingForFirstTextureUpload == 2) {
            ImageView imageView = this.textureImageView;
            if (imageView != null) {
                imageView.setVisibility(4);
                this.textureImageView.setImageDrawable((Drawable) null);
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.currentBitmap = null;
                }
            }
            this.switchingInlineMode = false;
            this.delegate.onSwitchInlineMode(this.controlsView, false, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), this.allowInlineAnimation);
            this.waitingForFirstTextureUpload = 0;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = ((r - l) - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
        int y = (((b - t) - AndroidUtilities.dp(10.0f)) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
        AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
        aspectRatioFrameLayout2.layout(x, y, aspectRatioFrameLayout2.getMeasuredWidth() + x, this.aspectRatioFrameLayout.getMeasuredHeight() + y);
        if (this.controlsView.getParent() == this) {
            ControlsView controlsView2 = this.controlsView;
            controlsView2.layout(0, 0, controlsView2.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
        }
        int x2 = ((r - l) - this.progressView.getMeasuredWidth()) / 2;
        int y2 = ((b - t) - this.progressView.getMeasuredHeight()) / 2;
        RadialProgressView radialProgressView = this.progressView;
        radialProgressView.layout(x2, y2, radialProgressView.getMeasuredWidth() + x2, this.progressView.getMeasuredHeight() + y2);
        this.controlsView.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height - AndroidUtilities.dp(10.0f), 1073741824));
        if (this.controlsView.getParent() == this) {
            this.controlsView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
        }
        this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), 1073741824));
        setMeasuredDimension(width, height);
    }

    /* access modifiers changed from: private */
    public void updatePlayButton() {
        this.controlsView.checkNeedHide();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (this.videoPlayer.isPlaying()) {
            this.playButton.setImageResource(this.isInline ? R.drawable.ic_pauseinline : R.drawable.ic_pause);
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500);
            checkAudioFocus();
        } else if (this.isCompleted) {
            this.playButton.setImageResource(this.isInline ? R.drawable.ic_againinline : R.drawable.ic_again);
        } else {
            this.playButton.setImageResource(this.isInline ? R.drawable.ic_playinline : R.drawable.ic_play);
        }
    }

    private void checkAudioFocus() {
        if (!this.hasAudioFocus) {
            this.hasAudioFocus = true;
            if (((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).requestAudioFocus(this, 3, 1) == 1) {
                this.audioFocus = 2;
            }
        }
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == -1) {
            if (this.videoPlayer.isPlaying()) {
                this.videoPlayer.pause();
                updatePlayButton();
            }
            this.hasAudioFocus = false;
            this.audioFocus = 0;
        } else if (focusChange == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                this.videoPlayer.play();
            }
        } else if (focusChange == -3) {
            this.audioFocus = 1;
        } else if (focusChange == -2) {
            this.audioFocus = 0;
            if (this.videoPlayer.isPlaying()) {
                this.resumeAudioOnFocusGain = true;
                this.videoPlayer.pause();
                updatePlayButton();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateFullscreenButton() {
        if (!this.videoPlayer.isPlayerPrepared() || this.isInline) {
            this.fullscreenButton.setVisibility(8);
            return;
        }
        this.fullscreenButton.setVisibility(0);
        if (!this.inFullscreen) {
            this.fullscreenButton.setImageResource(R.drawable.ic_gofullscreen);
            this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56.0f, 56.0f, 85, 0.0f, 0.0f, 0.0f, 5.0f));
            return;
        }
        this.fullscreenButton.setImageResource(R.drawable.ic_outfullscreen);
        this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56.0f, 56.0f, 85, 0.0f, 0.0f, 0.0f, 1.0f));
    }

    /* access modifiers changed from: private */
    public void updateShareButton() {
        ImageView imageView = this.shareButton;
        if (imageView != null) {
            imageView.setVisibility((this.isInline || !this.videoPlayer.isPlayerPrepared()) ? 8 : 0);
        }
    }

    private View getControlView() {
        return this.controlsView;
    }

    private View getProgressView() {
        return this.progressView;
    }

    /* access modifiers changed from: private */
    public void updateInlineButton() {
        ImageView imageView = this.inlineButton;
        if (imageView != null) {
            imageView.setImageResource(this.isInline ? R.drawable.ic_goinline : R.drawable.ic_outinline);
            this.inlineButton.setVisibility(this.videoPlayer.isPlayerPrepared() ? 0 : 8);
            if (this.isInline) {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(40, 40, 53));
            } else {
                this.inlineButton.setLayoutParams(LayoutHelper.createFrame(56, 50, 53));
            }
        }
    }

    /* access modifiers changed from: private */
    public void preparePlayer() {
        String str = this.playVideoUrl;
        if (str != null) {
            if (str == null || this.playAudioUrl == null) {
                this.videoPlayer.preparePlayer(Uri.parse(this.playVideoUrl), this.playVideoType);
            } else {
                this.videoPlayer.preparePlayerLoop(Uri.parse(str), this.playVideoType, Uri.parse(this.playAudioUrl), this.playAudioType);
            }
            this.videoPlayer.setPlayWhenReady(this.isAutoplay);
            this.isLoading = false;
            if (this.videoPlayer.getDuration() != C.TIME_UNSET) {
                this.controlsView.setDuration((int) (this.videoPlayer.getDuration() / 1000));
            } else {
                this.controlsView.setDuration(0);
            }
            updateFullscreenButton();
            updateShareButton();
            updateInlineButton();
            this.controlsView.invalidate();
            int i = this.seekToTime;
            if (i != -1) {
                this.videoPlayer.seekTo((long) (i * 1000));
            }
        }
    }

    public void pause() {
        this.videoPlayer.pause();
        updatePlayButton();
        this.controlsView.show(true, true);
    }

    private void updateFullscreenState(boolean byButton) {
        ViewGroup parent;
        if (this.textureView != null) {
            updateFullscreenButton();
            ViewGroup viewGroup = this.textureViewContainer;
            if (viewGroup == null) {
                this.changingTextureView = true;
                if (!this.inFullscreen) {
                    if (viewGroup != null) {
                        viewGroup.addView(this.textureView);
                    } else {
                        this.aspectRatioFrameLayout.addView(this.textureView);
                    }
                }
                if (this.inFullscreen) {
                    ViewGroup viewGroup2 = (ViewGroup) this.controlsView.getParent();
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(this.controlsView);
                    }
                } else {
                    ViewGroup parent2 = (ViewGroup) this.controlsView.getParent();
                    if (parent2 != this) {
                        if (parent2 != null) {
                            parent2.removeView(this.controlsView);
                        }
                        ViewGroup viewGroup3 = this.textureViewContainer;
                        if (viewGroup3 != null) {
                            viewGroup3.addView(this.controlsView);
                        } else {
                            addView(this.controlsView, 1);
                        }
                    }
                }
                TextureView onSwitchToFullscreen = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), byButton);
                this.changedTextureView = onSwitchToFullscreen;
                onSwitchToFullscreen.setVisibility(4);
                if (!(!this.inFullscreen || this.changedTextureView == null || (parent = (ViewGroup) this.textureView.getParent()) == null)) {
                    parent.removeView(this.textureView);
                }
                this.controlsView.checkNeedHide();
                return;
            }
            if (this.inFullscreen) {
                ViewGroup viewGroup4 = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                if (viewGroup4 != null) {
                    viewGroup4.removeView(this.aspectRatioFrameLayout);
                }
            } else {
                ViewGroup parent3 = (ViewGroup) this.aspectRatioFrameLayout.getParent();
                if (parent3 != this) {
                    if (parent3 != null) {
                        parent3.removeView(this.aspectRatioFrameLayout);
                    }
                    addView(this.aspectRatioFrameLayout, 0);
                }
            }
            this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), byButton);
        }
    }

    public void exitFullscreen() {
        if (this.inFullscreen) {
            this.inFullscreen = false;
            updateInlineButton();
            updateFullscreenState(false);
        }
    }

    public boolean isInitied() {
        return this.initied;
    }

    public boolean isInline() {
        return this.isInline || this.switchingInlineMode;
    }

    public void enterFullscreen() {
        if (!this.inFullscreen) {
            this.inFullscreen = true;
            updateInlineButton();
            updateFullscreenState(false);
        }
    }

    public boolean isInFullscreen() {
        return this.inFullscreen;
    }

    public String getYouTubeVideoId(String url) {
        Matcher matcher = youtubeIdRegex.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public boolean loadVideo(String url, TLRPC.Photo thumb, Object parentObject, String originalUrl, boolean autoplay) {
        boolean z;
        String str = url;
        TLRPC.Photo photo = thumb;
        String youtubeId = null;
        String vimeoId = null;
        String coubId = null;
        String twitchClipId = null;
        String twitchStreamId = null;
        String mp4File = null;
        String aparatId = null;
        this.seekToTime = -1;
        if (str != null) {
            if (str.endsWith(".mp4")) {
                mp4File = url;
            } else {
                if (originalUrl != null) {
                    try {
                        Uri uri = Uri.parse(originalUrl);
                        String t = uri.getQueryParameter("t");
                        if (t == null) {
                            t = uri.getQueryParameter("time_continue");
                        }
                        if (t != null) {
                            if (t.contains("m")) {
                                String[] args = t.split("m");
                                this.seekToTime = (Utilities.parseInt(args[0]).intValue() * 60) + Utilities.parseInt(args[1]).intValue();
                            } else {
                                this.seekToTime = Utilities.parseInt(t).intValue();
                            }
                        }
                    } catch (Exception e) {
                        try {
                            FileLog.e((Throwable) e);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    }
                }
                Matcher matcher = youtubeIdRegex.matcher(str);
                String id = null;
                if (matcher.find()) {
                    id = matcher.group(1);
                }
                if (id != null) {
                    youtubeId = id;
                }
                if (youtubeId == null) {
                    try {
                        Matcher matcher2 = vimeoIdRegex.matcher(str);
                        String id2 = null;
                        if (matcher2.find()) {
                            id2 = matcher2.group(3);
                        }
                        if (id2 != null) {
                            vimeoId = id2;
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                if (vimeoId == null) {
                    try {
                        Matcher matcher3 = aparatIdRegex.matcher(str);
                        String id3 = null;
                        if (matcher3.find()) {
                            id3 = matcher3.group(1);
                        }
                        if (id3 != null) {
                            aparatId = id3;
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    }
                }
                if (aparatId == null) {
                    try {
                        Matcher matcher4 = twitchClipIdRegex.matcher(str);
                        String id4 = null;
                        if (matcher4.find()) {
                            id4 = matcher4.group(1);
                        }
                        if (id4 != null) {
                            twitchClipId = id4;
                        }
                    } catch (Exception e5) {
                        FileLog.e((Throwable) e5);
                    }
                }
                if (twitchClipId == null) {
                    try {
                        Matcher matcher5 = twitchStreamIdRegex.matcher(str);
                        String id5 = null;
                        if (matcher5.find()) {
                            id5 = matcher5.group(1);
                        }
                        if (id5 != null) {
                            twitchStreamId = id5;
                        }
                    } catch (Exception e6) {
                        FileLog.e((Throwable) e6);
                    }
                }
                if (twitchStreamId == null) {
                    try {
                        Matcher matcher6 = coubIdRegex.matcher(str);
                        String id6 = null;
                        if (matcher6.find()) {
                            id6 = matcher6.group(1);
                        }
                        if (id6 != null) {
                            coubId = id6;
                        }
                    } catch (Exception e7) {
                        FileLog.e((Throwable) e7);
                    }
                }
            }
        }
        this.initied = false;
        this.isCompleted = false;
        this.isAutoplay = autoplay;
        this.playVideoUrl = null;
        this.playAudioUrl = null;
        destroy();
        this.firstFrameRendered = false;
        this.currentAlpha = 1.0f;
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        updateFullscreenButton();
        updateShareButton();
        updateInlineButton();
        updatePlayButton();
        if (photo != null) {
            TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
            if (photoSize != null) {
                this.controlsView.imageReceiver.setImage((ImageLocation) null, (String) null, ImageLocation.getForPhoto(photoSize, photo), "80_80_b", 0, (String) null, parentObject, 1);
                this.drawImage = true;
            }
        } else {
            this.drawImage = false;
        }
        AnimatorSet animatorSet = this.progressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.progressAnimation = null;
        }
        this.isLoading = true;
        this.controlsView.setProgress(0);
        if (youtubeId != null) {
            this.currentYoutubeId = youtubeId;
            youtubeId = null;
        }
        if (mp4File != null) {
            this.initied = true;
            this.playVideoUrl = mp4File;
            this.playVideoType = "other";
            if (this.isAutoplay) {
                preparePlayer();
            }
            showProgress(false, false);
            this.controlsView.show(true, true);
        } else {
            if (youtubeId != null) {
                YoutubeVideoTask task = new YoutubeVideoTask(youtubeId);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = task;
                z = true;
            } else if (vimeoId != null) {
                VimeoVideoTask task2 = new VimeoVideoTask(vimeoId);
                task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = task2;
                z = true;
            } else if (coubId != null) {
                CoubVideoTask task3 = new CoubVideoTask(coubId);
                task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = task3;
                this.isStream = true;
                z = true;
            } else if (aparatId != null) {
                AparatVideoTask task4 = new AparatVideoTask(aparatId);
                task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = task4;
                z = true;
            } else if (twitchClipId != null) {
                TwitchClipVideoTask task5 = new TwitchClipVideoTask(str, twitchClipId);
                task5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = task5;
                z = true;
            } else if (twitchStreamId != null) {
                TwitchStreamVideoTask task6 = new TwitchStreamVideoTask(str, twitchStreamId);
                z = true;
                task6.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentTask = task6;
                this.isStream = true;
            } else {
                z = true;
            }
            this.controlsView.show(false, false);
            showProgress(z, false);
        }
        if (youtubeId == null && vimeoId == null && coubId == null && aparatId == null && mp4File == null && twitchClipId == null && twitchStreamId == null) {
            this.controlsView.setVisibility(8);
            return false;
        }
        this.controlsView.setVisibility(0);
        return true;
    }

    public View getAspectRatioView() {
        return this.aspectRatioFrameLayout;
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public ImageView getTextureImageView() {
        return this.textureImageView;
    }

    public View getControlsView() {
        return this.controlsView;
    }

    public void destroy() {
        this.videoPlayer.releasePlayer(false);
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        this.webView.stopLoading();
    }

    /* access modifiers changed from: private */
    public void showProgress(boolean show, boolean animated) {
        float f = 1.0f;
        if (animated) {
            AnimatorSet animatorSet = this.progressAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.progressAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            RadialProgressView radialProgressView = this.progressView;
            float[] fArr = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, "alpha", fArr);
            animatorSet2.playTogether(animatorArr);
            this.progressAnimation.setDuration(150);
            this.progressAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = WebPlayerView.this.progressAnimation = null;
                }
            });
            this.progressAnimation.start();
            return;
        }
        RadialProgressView radialProgressView2 = this.progressView;
        if (!show) {
            f = 0.0f;
        }
        radialProgressView2.setAlpha(f);
    }
}
