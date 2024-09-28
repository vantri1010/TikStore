package im.bclpbkiauv.ui.hui.discoveryweb;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.animation.RotateAnimation;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.internal.ImagesContract;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.discoveryweb.DiscoveryJumpToPage;
import im.bclpbkiauv.ui.hviews.dragView.DragCallBack;
import im.bclpbkiauv.ui.hviews.dragView.DragHelperFrameLayout;
import java.util.List;

public class DiscoveryJumpToPage extends BaseFragment implements View.OnClickListener {
    private static volatile DiscoveryJumpToPage Instance = null;
    private static final int REQUEST_CODE_FILE = 67;
    private static final int REQUEST_CODE_LOCATION = 99;
    private ChromeClient chromeClient;
    private View containerGuideRoot;
    private View containerMenu;
    private DragHelperFrameLayout dragHelperFrameLayout;
    private ImageView imgRefresh;
    private ImageView ivClose;
    /* access modifiers changed from: private */
    public ImageView ivShowMenuDialog;
    private AnimatorSet mAnimatorSet;
    private RotateAnimation mRefreshAnimation;
    /* access modifiers changed from: private */
    public List<String> mUrlList;
    private volatile boolean needToDestroyWebView;
    /* access modifiers changed from: private */
    public RadialProgressView progressView;
    private String title;
    private String url;
    private volatile boolean viewIsInit;
    private WebClient webClient;
    /* access modifiers changed from: private */
    public volatile WebView webView;
    private FrameLayout webViewContainer;
    private volatile boolean webViewIsInit;

    public static boolean checkCanToPausedPlayGamePage() {
        return Instance != null;
    }

    public static DiscoveryJumpToPage toPage(String title2, String url2) {
        Instance = new DiscoveryJumpToPage();
        Bundle args = new Bundle();
        args.putString("title", title2);
        args.putString(ImagesContract.URL, url2);
        Instance.setArguments(args);
        return Instance;
    }

    public static DiscoveryJumpToPage toPausedPlayGamePage() {
        if (Instance != null) {
            return Instance;
        }
        throw new IllegalArgumentException("The static PlayGameActivity is null, please check it.");
    }

    public static void hideGameWebView(BaseFragment currentPage) {
        if (Instance != null) {
            if (Instance.webView != null) {
                try {
                    ViewParent parent = Instance.webView.getParent();
                    if (parent != null) {
                        ((FrameLayout) parent).removeView(Instance.webView);
                    }
                    Instance.webView.setWebViewClient((WebViewClient) null);
                    Instance.webView.setWebChromeClient((WebChromeClient) null);
                    Instance.webView.stopLoading();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.folderWebView, new Object[0]);
            if (currentPage != null && !currentPage.isFinishing()) {
                currentPage.finishFragment();
            }
        }
    }

    public static void destroyGameWebView() {
        if (Instance != null) {
            if (Instance.webView != null) {
                Instance.webView.setLayerType(0, (Paint) null);
                try {
                    ViewParent parent = Instance.webView.getParent();
                    if (parent != null) {
                        ((FrameLayout) parent).removeView(Instance.webView);
                    }
                    Instance.webView.stopLoading();
                    Instance.webView.loadUrl("about:blank");
                    Instance.webView.setWebViewClient((WebViewClient) null);
                    Instance.webView.setWebChromeClient((WebChromeClient) null);
                    Instance.webView.destroy();
                    Instance.webView = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            Instance = null;
        }
    }

    public static String getTitle() {
        if (Instance != null) {
            return Instance.title;
        }
        return "";
    }

    private DiscoveryJumpToPage() {
    }

    public boolean onFragmentCreate() {
        if (getArguments() != null) {
            this.title = getArguments().getString("title");
            this.url = getArguments().getString(ImagesContract.URL);
        }
        if (TextUtils.isEmpty(this.url)) {
            return false;
        }
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        int i = 0;
        DragHelperFrameLayout dragHelperFrameLayout2 = (DragHelperFrameLayout) LayoutInflater.from(context).inflate(R.layout.activity_discovery_jump_to_page, (ViewGroup) null, false);
        this.dragHelperFrameLayout = dragHelperFrameLayout2;
        this.fragmentView = dragHelperFrameLayout2;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        getParentActivity().setRequestedOrientation(7);
        initView(context);
        initWebView(false);
        if (this.containerGuideRoot != null && checkShowGuide()) {
            View view = this.containerGuideRoot;
            if (!checkShowGuide()) {
                i = 8;
            }
            view.setVisibility(i);
        }
        return this.fragmentView;
    }

    private void initView(Context context) {
        if (!this.viewIsInit && context != null) {
            this.ivClose = (ImageView) this.fragmentView.findViewById(R.id.ivClose);
            this.ivShowMenuDialog = (ImageView) this.fragmentView.findViewById(R.id.ivShowMenuDialog);
            setInPreviewMode(true);
            this.actionBar.setAddToContainer(false);
            this.containerGuideRoot = this.fragmentView.findViewById(R.id.containerGuideRoot);
            this.imgRefresh = (ImageView) this.fragmentView.findViewById(R.id.img_refresh);
            this.containerMenu = this.fragmentView.findViewById(R.id.containerMenu);
            this.webViewContainer = (FrameLayout) this.fragmentView.findViewById(R.id.containerWebView);
            RadialProgressView radialProgressView = (RadialProgressView) this.fragmentView.findViewById(R.id.progressView);
            this.progressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(20.0f));
            this.progressView.setStrokeWidth(2.0f);
            this.progressView.setProgressColor(-1);
            this.ivClose.setOnClickListener(this);
            this.ivShowMenuDialog.setOnClickListener(this);
            this.imgRefresh.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DiscoveryJumpToPage.this.webView.reload();
                }
            });
            this.fragmentView.findViewById(R.id.btnPlayGameGuideKnown).setOnClickListener(this);
            DragHelperFrameLayout dragHelperFrameLayout2 = this.dragHelperFrameLayout;
            dragHelperFrameLayout2.setViewDragCallBack(new DragCallBack(dragHelperFrameLayout2, this.containerMenu) {
                public List<Rect> getNotchRectList() {
                    WindowInsets windowInsets;
                    DisplayCutout displayCutout;
                    if (Build.VERSION.SDK_INT < 28 || (windowInsets = DiscoveryJumpToPage.this.getParentActivity().getWindow().getDecorView().getRootWindowInsets()) == null || (displayCutout = windowInsets.getDisplayCutout()) == null) {
                        return super.getNotchRectList();
                    }
                    return displayCutout.getBoundingRects();
                }
            });
            changeSystemStatusBar(false);
            this.viewIsInit = true;
        }
    }

    private void initWebView(boolean fromResume) {
        if (!this.webViewIsInit) {
            this.webViewContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            if (this.webView == null) {
                this.webView = new WebView(getParentActivity()) {
                    public void goBack() {
                        super.goBack();
                    }

                    public boolean onTouchEvent(MotionEvent event) {
                        DiscoveryJumpToPage.this.changeSystemStatusBar(false);
                        return super.onTouchEvent(event);
                    }
                };
            }
            if (this.webView.getParent() == null) {
                this.webViewContainer.addView(this.webView, 0, LayoutHelper.createFrame(-1, -1.0f));
            }
            WebSettings webSettings = this.webView.getSettings();
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportZoom(false);
            webSettings.setBlockNetworkImage(false);
            if (Build.VERSION.SDK_INT >= 21) {
                webSettings.setMixedContentMode(0);
            }
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setCacheMode(-1);
            webSettings.setAppCacheEnabled(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDefaultTextEncodingName("utf-8");
            webSettings.setDomStorageEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setTextZoom(100);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            this.webView.setWebViewClient(new WebClient());
            this.webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    DiscoveryJumpToPage.this.webView.loadUrl(url);
                    return true;
                }
            });
            this.webView.setWebChromeClient(new WebChromeClient() {
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                    WebView newWebView = new WebView(DiscoveryJumpToPage.this.getParentActivity());
                    newWebView.getSettings().setJavaScriptEnabled(true);
                    newWebView.getSettings().setSupportZoom(true);
                    newWebView.getSettings().setBuiltInZoomControls(true);
                    newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                    newWebView.getSettings().setSupportMultipleWindows(true);
                    view.addView(newWebView);
                    ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
                    resultMsg.sendToTarget();
                    Message href = view.getHandler().obtainMessage();
                    view.requestFocusNodeHref(href);
                    newWebView.loadUrl(href.getData().getString(ImagesContract.URL));
                    return true;
                }
            });
            this.webView.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    DiscoveryJumpToPage.this.getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                }
            });
            this.webViewIsInit = true;
            if (!fromResume) {
                refresh();
            }
        }
    }

    public void onResume() {
        super.onResume();
        initView(getParentActivity());
        initWebView(true);
        if (this.webView != null) {
            this.webView.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.webView != null) {
            this.webView.onPause();
        }
    }

    /* access modifiers changed from: private */
    public void changeSystemStatusBar(boolean check) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        if (!check) {
            this.fragmentView.setSystemUiVisibility(this.fragmentView.getSystemUiVisibility() | 4);
            return;
        }
        this.fragmentView.getSystemUiVisibility();
        this.fragmentView.setSystemUiVisibility(this.fragmentView.getSystemUiVisibility() | 4);
    }

    private void refresh() {
        String _url;
        if (!isFinishing()) {
            List<String> list = this.mUrlList;
            if (list == null || list.size() <= 1) {
                _url = this.url;
            } else {
                List<String> list2 = this.mUrlList;
                _url = list2.get(list2.size() - 1);
            }
            if (_url != null) {
                if (!_url.startsWith("http://") && !_url.startsWith("https://")) {
                    _url = "http://" + _url;
                }
                this.webView.loadUrl(_url);
            }
        }
    }

    private boolean canFinishThisPage() {
        return this.webView != null && !this.webView.canGoBack();
    }

    public void finishThisPage() {
        this.needToDestroyWebView = true;
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void showDialog(String message, boolean canCancel, boolean canCancelTouchOutside, String negativeBtnText, DialogInterface.OnClickListener negativeBtnClickListener, String positiveBtnText, DialogInterface.OnClickListener positiveBtnClickListener, DialogInterface.OnCancelListener cancelListener, DialogInterface.OnDismissListener dismissListener) {
        if (message != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(message);
            if (negativeBtnText != null) {
                builder.setNegativeButton(negativeBtnText, negativeBtnClickListener);
            }
            if (positiveBtnText != null) {
                builder.setPositiveButton(positiveBtnText, positiveBtnClickListener);
            }
            builder.setOnCancelListener(cancelListener);
            builder.setOnDismissListener(dismissListener);
            AlertDialog dialog = builder.create();
            dialog.setCancelable(canCancel);
            dialog.setCanceledOnTouchOutside(canCancelTouchOutside);
            showDialog(dialog);
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnPlayGameGuideKnown) {
            SharedPreferences.Editor editor = getParentActivity().getSharedPreferences("DiscoveryJumpPage", 0).edit();
            editor.putBoolean("guide", false);
            editor.apply();
            View view = this.containerGuideRoot;
            if (view != null) {
                view.setVisibility(8);
            }
        } else if (id == R.id.ivClose) {
            finishThisPage();
        } else if (id == R.id.ivShowMenuDialog) {
            DiscoveryJumpMenuDialog dialog = new DiscoveryJumpMenuDialog(getParentActivity());
            dialog.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    DiscoveryJumpToPage.this.lambda$onClick$0$DiscoveryJumpToPage(view, i);
                }
            });
            showDialog(dialog);
        }
    }

    public /* synthetic */ void lambda$onClick$0$DiscoveryJumpToPage(View view, int position) {
        if (position == 0) {
            hideGameWebView(this);
        } else if (position == 1) {
            refresh();
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ChromeClient chromeClient2;
        if (requestCode == 99 && (chromeClient2 = this.chromeClient) != null) {
            chromeClient2.showRequestLocationPermissionDialog(permissions[0], chromeClient2.mGeolocationPermissionsCallBack);
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        changeSystemStatusBar(false);
    }

    public void saveSelfArgs(Bundle args) {
        super.saveSelfArgs(args);
        args.putString(ImagesContract.URL, this.url);
        args.putString("title", this.title);
    }

    public void restoreSelfArgs(Bundle args) {
        super.restoreSelfArgs(args);
        this.title = args.getString("title");
        this.url = args.getString(ImagesContract.URL);
    }

    public boolean onBackPressed() {
        if (canFinishThisPage()) {
            finishThisPage();
            return false;
        } else if (this.webView == null || !this.webView.canGoBack()) {
            return super.onBackPressed();
        } else {
            this.webView.goBack();
            return false;
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        super.onActivityResultFragment(requestCode, resultCode, data);
        if (requestCode != 67) {
            return;
        }
        if (this.chromeClient.fileChooserCallback40 != null) {
            Uri result = null;
            if (resultCode == -1) {
                result = data == null ? null : data.getData();
            }
            this.chromeClient.fileChooserCallback40.onReceiveValue(result);
            ValueCallback unused = this.chromeClient.fileChooserCallback40 = null;
        } else if (this.chromeClient.fileChooserCallback != null) {
            Uri[] results = null;
            if (resultCode == -1 && data != null) {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        results[i] = clipData.getItemAt(i).getUri();
                    }
                }
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
            this.chromeClient.fileChooserCallback.onReceiveValue(results);
            ValueCallback unused2 = this.chromeClient.fileChooserCallback = null;
        }
    }

    public boolean canBeginSlide() {
        return false;
    }

    private boolean checkShowGuide() {
        return getParentActivity().getSharedPreferences("DiscoveryJumpPage", 0).getBoolean("guide", true);
    }

    public void onFragmentDestroy() {
        if (this.needToDestroyWebView) {
            destroyGameWebView();
        }
        AnimatorSet animatorSet = this.mAnimatorSet;
        if (animatorSet != null) {
            try {
                animatorSet.cancel();
            } catch (Exception e) {
            }
            this.mAnimatorSet = null;
        }
        RotateAnimation rotateAnimation = this.mRefreshAnimation;
        if (rotateAnimation != null) {
            try {
                rotateAnimation.cancel();
            } catch (Exception e2) {
            }
            this.mRefreshAnimation = null;
        }
        super.onFragmentDestroy();
        List<String> list = this.mUrlList;
        if (list != null) {
            list.clear();
            this.mUrlList = null;
        }
        this.webClient = null;
        ChromeClient chromeClient2 = this.chromeClient;
        if (chromeClient2 != null) {
            ValueCallback unused = chromeClient2.fileChooserCallback = null;
            ValueCallback unused2 = this.chromeClient.fileChooserCallback40 = null;
            GeolocationPermissions.Callback unused3 = this.chromeClient.mGeolocationPermissionsCallBack = null;
            this.chromeClient = null;
        }
        this.chromeClient = null;
        this.viewIsInit = false;
        this.webViewIsInit = false;
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(1);
        }
    }

    private class WebClient extends WebViewClient {
        private WebClient() {
        }

        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (DiscoveryJumpToPage.this.isFinishing()) {
                return false;
            }
            if (url != null) {
                try {
                    if (!url.startsWith("https") && !url.startsWith("http") && DiscoveryJumpToPage.this.getParentActivity() != null) {
                        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                        intent.putExtra("create_new_tab", true);
                        intent.putExtra("com.android.browser.application_id", DiscoveryJumpToPage.this.getParentActivity().getPackageName());
                        DiscoveryJumpToPage.this.getParentActivity().startActivity(intent);
                        return true;
                    }
                } catch (Throwable e) {
                    FileLog.e(DiscoveryJumpToPage.class.getName(), e);
                    ToastUtils.show((int) R.string.NoAppCanHandleThis);
                    return true;
                }
            }
            try {
                if (DiscoveryJumpToPage.this.mUrlList != null) {
                    if (DiscoveryJumpToPage.this.mUrlList.contains(url) && !((String) DiscoveryJumpToPage.this.mUrlList.get(DiscoveryJumpToPage.this.mUrlList.size() - 1)).equals(url)) {
                        DiscoveryJumpToPage.this.mUrlList.remove(url);
                    }
                    DiscoveryJumpToPage.this.mUrlList.add(url);
                }
            } catch (Exception e1) {
                FileLog.e(DiscoveryJumpToPage.class.getName() + " shouldOverrideUrlLoading error: " + e1.getMessage());
            }
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!DiscoveryJumpToPage.this.isFinishing()) {
                if (!(DiscoveryJumpToPage.this.ivShowMenuDialog == null || DiscoveryJumpToPage.this.ivShowMenuDialog.getVisibility() == 8)) {
                    DiscoveryJumpToPage.this.ivShowMenuDialog.setVisibility(8);
                }
                if (DiscoveryJumpToPage.this.progressView != null && DiscoveryJumpToPage.this.progressView.getVisibility() != 0) {
                    DiscoveryJumpToPage.this.progressView.setVisibility(0);
                }
            }
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!DiscoveryJumpToPage.this.isFinishing()) {
                if (!(DiscoveryJumpToPage.this.progressView == null || DiscoveryJumpToPage.this.progressView.getVisibility() == 8)) {
                    DiscoveryJumpToPage.this.progressView.setVisibility(8);
                }
                if (DiscoveryJumpToPage.this.ivShowMenuDialog != null && DiscoveryJumpToPage.this.ivShowMenuDialog.getVisibility() != 0) {
                    DiscoveryJumpToPage.this.ivShowMenuDialog.setVisibility(0);
                }
            }
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            FileLog.e(DiscoveryJumpToPage.class.getName() + " onReceivedError: code:" + errorCode + ", des:" + description + ", u:" + failingUrl);
        }
    }

    private class ChromeClient extends WebChromeClient {
        /* access modifiers changed from: private */
        public ValueCallback<Uri[]> fileChooserCallback;
        /* access modifiers changed from: private */
        public ValueCallback<Uri> fileChooserCallback40;
        /* access modifiers changed from: private */
        public GeolocationPermissions.Callback mGeolocationPermissionsCallBack;
        private boolean mIsRequestingLocationPermission;

        private ChromeClient() {
        }

        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView myWeb = new WebView(DiscoveryJumpToPage.this.getParentActivity());
            Message href = view.getHandler().obtainMessage();
            view.requestFocusNodeHref(href);
            myWeb.loadUrl(href.getData().getString(ImagesContract.URL));
            return true;
        }

        public void onReceivedTitle(WebView view, String title) {
            if (!DiscoveryJumpToPage.this.isFinishing()) {
            }
        }

        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (DiscoveryJumpToPage.this.isFinishing()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) DiscoveryJumpToPage.this.getParentActivity());
            builder.setMessage(message);
            DiscoveryJumpToPage.this.showDialog(builder.create());
            return true;
        }

        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            if (DiscoveryJumpToPage.this.isFinishing()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) DiscoveryJumpToPage.this.getParentActivity());
            builder.setMessage(message);
            builder.setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener(result) {
                private final /* synthetic */ JsResult f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.confirm();
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener(result) {
                private final /* synthetic */ JsResult f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.cancel();
                }
            });
            DiscoveryJumpToPage.this.showDialog(builder.create());
            return true;
        }

        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            if (!DiscoveryJumpToPage.this.isFinishing() && !this.mIsRequestingLocationPermission) {
                this.mIsRequestingLocationPermission = true;
                this.mGeolocationPermissionsCallBack = callback;
                if (ContextCompat.checkSelfPermission(DiscoveryJumpToPage.this.getParentActivity(), origin) != 0) {
                    ActivityCompat.requestPermissions(DiscoveryJumpToPage.this.getParentActivity(), new String[]{origin}, 99);
                    return;
                }
                showRequestLocationPermissionDialog(origin, callback);
            }
        }

        public void onPermissionRequest(PermissionRequest request) {
            if (!DiscoveryJumpToPage.this.isFinishing() && Build.VERSION.SDK_INT >= 21) {
                try {
                    request.grant(request.getResources());
                } catch (Exception e) {
                }
            }
        }

        /* access modifiers changed from: private */
        public void showRequestLocationPermissionDialog(String origin, GeolocationPermissions.Callback callback) {
            if (!DiscoveryJumpToPage.this.isFinishing() && callback != null) {
                DiscoveryJumpToPage.this.showDialog(LocaleController.getString(R.string.RequestPermissionOfLocationDoYouAgreement), false, false, LocaleController.getString(R.string.Decline), new DialogInterface.OnClickListener(callback, origin) {
                    private final /* synthetic */ GeolocationPermissions.Callback f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DiscoveryJumpToPage.ChromeClient.this.lambda$showRequestLocationPermissionDialog$2$DiscoveryJumpToPage$ChromeClient(this.f$1, this.f$2, dialogInterface, i);
                    }
                }, LocaleController.getString(R.string.Agree), new DialogInterface.OnClickListener(callback, origin) {
                    private final /* synthetic */ GeolocationPermissions.Callback f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DiscoveryJumpToPage.ChromeClient.this.lambda$showRequestLocationPermissionDialog$3$DiscoveryJumpToPage$ChromeClient(this.f$1, this.f$2, dialogInterface, i);
                    }
                }, new DialogInterface.OnCancelListener(callback, origin) {
                    private final /* synthetic */ GeolocationPermissions.Callback f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        DiscoveryJumpToPage.ChromeClient.this.lambda$showRequestLocationPermissionDialog$4$DiscoveryJumpToPage$ChromeClient(this.f$1, this.f$2, dialogInterface);
                    }
                }, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        DiscoveryJumpToPage.ChromeClient.this.lambda$showRequestLocationPermissionDialog$5$DiscoveryJumpToPage$ChromeClient(dialogInterface);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$showRequestLocationPermissionDialog$2$DiscoveryJumpToPage$ChromeClient(GeolocationPermissions.Callback callback, String origin, DialogInterface dialog, int which) {
            if (callback != null) {
                callback.invoke(origin, false, false);
            }
            this.mIsRequestingLocationPermission = false;
        }

        public /* synthetic */ void lambda$showRequestLocationPermissionDialog$3$DiscoveryJumpToPage$ChromeClient(GeolocationPermissions.Callback callback, String origin, DialogInterface dialog, int which) {
            if (callback != null) {
                callback.invoke(origin, true, false);
            }
            this.mIsRequestingLocationPermission = false;
        }

        public /* synthetic */ void lambda$showRequestLocationPermissionDialog$4$DiscoveryJumpToPage$ChromeClient(GeolocationPermissions.Callback callback, String origin, DialogInterface dialog) {
            if (callback != null) {
                callback.invoke(origin, false, false);
            }
            this.mIsRequestingLocationPermission = false;
        }

        public /* synthetic */ void lambda$showRequestLocationPermissionDialog$5$DiscoveryJumpToPage$ChromeClient(DialogInterface dialog) {
            this.mIsRequestingLocationPermission = false;
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            this.fileChooserCallback40 = valueCallback;
            DiscoveryJumpToPage.this.openFileChoosers();
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType) {
            this.fileChooserCallback40 = valueCallback;
            DiscoveryJumpToPage.this.openFileChoosers();
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            this.fileChooserCallback40 = valueCallback;
            DiscoveryJumpToPage.this.openFileChoosers();
        }

        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            this.fileChooserCallback = filePathCallback;
            DiscoveryJumpToPage.this.openFileChoosers();
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void openFileChoosers() {
        try {
            Intent i = new Intent("android.intent.action.GET_CONTENT");
            i.addCategory("android.intent.category.OPENABLE");
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "Image Chooser"), 67);
        } catch (Throwable e) {
            ToastUtils.show((CharSequence) "Open Failed.");
            FileLog.e(getClass().getName() + " openFileChoosers error: " + e.getMessage());
        }
    }
}
