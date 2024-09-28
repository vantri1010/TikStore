package im.bclpbkiauv.ui.fragments;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPC2;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.fragments.DiscoveryFragment;
import im.bclpbkiauv.ui.fragments.TabWebFragment;

public class TabWebFragment extends BaseFmts {
    private static final int REQUEST_CODE_FILE = 67;
    private static final int REQUEST_CODE_LOCATION = 99;
    private ChromeClient chromeClient;
    private DiscoveryFragment.Delegate delegate;
    private int extraDataReqToken;
    private ImageView imgReFresh;
    /* access modifiers changed from: private */
    public WebView webView;
    private FrameLayout webViewContainer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        this.webViewContainer = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.fragmentView = this.webViewContainer;
        this.webView = new WebView(getParentActivity());
        this.webViewContainer.setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        this.webViewContainer.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f));
        ImageView imageView = new ImageView(getParentActivity());
        this.imgReFresh = imageView;
        imageView.setImageResource(R.drawable.icon_refresh_web);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(68.0f));
        layoutParams.gravity = 8388693;
        layoutParams.setMargins(0, 0, requireActivity().getResources().getDimensionPixelSize(R.dimen.dp_5), requireActivity().getResources().getDimensionPixelSize(R.dimen.dp_96));
        this.webViewContainer.addView(this.imgReFresh, layoutParams);
        return this.fragmentView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.imgReFresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TabWebFragment.this.webView.reload();
            }
        });
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
        webSettings.setTextZoom(100);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        this.webView.setWebViewClient(new WebClient());
        WebView webView2 = this.webView;
        ChromeClient chromeClient2 = new ChromeClient();
        this.chromeClient = chromeClient2;
        webView2.setWebChromeClient(chromeClient2);
    }

    /* access modifiers changed from: protected */
    public void lazyLoadData() {
        TLRPC2.TL_DiscoveryPageSetting_SM s;
        super.lazyLoadData();
        DiscoveryFragment.Delegate delegate2 = this.delegate;
        if (delegate2 != null && delegate2.getDiscoveryPageData() != null && this.delegate.getDiscoveryPageData().getS().size() > 0 && (s = this.delegate.getDiscoveryPageData().getS().get(0)) != null) {
            if (TextUtils.isEmpty(s.getUrl())) {
                getExtraDataLoginUrl(s);
            } else if (TextUtils.isEmpty(s.getUrl()) || (!s.getUrl().startsWith("http:") && !s.getUrl().startsWith("https:"))) {
                ToastUtils.show((int) R.string.CancelLinkExpired);
            } else {
                WebView webView2 = this.webView;
                if (webView2 != null) {
                    webView2.loadUrl(s.getUrl());
                }
            }
        }
    }

    private void getExtraDataLoginUrl(TLRPC2.TL_DiscoveryPageSetting_SM s) {
        if (getUserConfig().isClientActivated() && s != null && !TextUtils.isEmpty(s.getTitle()) && this.extraDataReqToken == 0) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    TabWebFragment.this.lambda$getExtraDataLoginUrl$0$TabWebFragment(dialogInterface);
                }
            });
            showDialog(progressDialog);
            TLRPC2.TL_GetLoginUrl req = new TLRPC2.TL_GetLoginUrl();
            req.app_code = s.getTitle();
            this.extraDataReqToken = getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TabWebFragment.this.lambda$getExtraDataLoginUrl$2$TabWebFragment(tLObject, tL_error);
                }
            });
            getConnectionsManager().bindRequestToGuid(this.extraDataReqToken, this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getExtraDataLoginUrl$0$TabWebFragment(DialogInterface dialog) {
        if (this.extraDataReqToken != 0) {
            getConnectionsManager().cancelRequest(this.extraDataReqToken, false);
        }
    }

    public /* synthetic */ void lambda$getExtraDataLoginUrl$2$TabWebFragment(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TabWebFragment.this.lambda$null$1$TabWebFragment(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$TabWebFragment(TLRPC.TL_error error, TLObject response) {
        String str;
        if (error != null || !(response instanceof TLRPC2.TL_LoginUrlInfo)) {
            ToastUtils.show((int) R.string.FailedToGetLink);
            StringBuilder sb = new StringBuilder();
            sb.append("TabWebFmts getExtraDataLoginUrl error: ");
            if (error != null) {
                str = "errCode:" + error.code + ", errText:" + error.text;
            } else {
                str = "error is null";
            }
            sb.append(str);
            FileLog.e(sb.toString());
        } else {
            String url = ((TLRPC2.TL_LoginUrlInfo) response).url;
            if (TextUtils.isEmpty(url) || (!url.startsWith("http:") && !url.startsWith("https:"))) {
                ToastUtils.show((int) R.string.CancelLinkExpired);
            } else {
                WebView webView2 = this.webView;
                if (webView2 != null) {
                    webView2.loadUrl(url);
                }
            }
        }
        this.extraDataReqToken = 0;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ChromeClient chromeClient2;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 99 && (chromeClient2 = this.chromeClient) != null) {
            chromeClient2.showRequestLocationPermissionDialog(permissions[0], chromeClient2.mGeolocationPermissionsCallBack);
        }
    }

    /* access modifiers changed from: private */
    public void showProgressDialog() {
        showDialog(new AlertDialog(getParentActivity(), 3));
    }

    public void setDelegate(DiscoveryFragment.Delegate delegate2) {
        this.delegate = delegate2;
    }

    public boolean onBackPressed() {
        WebView webView2 = this.webView;
        if (webView2 == null || !webView2.canGoBack()) {
            return super.onBackPressed();
        }
        this.webView.goBack();
        return true;
    }

    public void onDestroyView() {
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.setLayerType(0, (Paint) null);
            try {
                ViewParent parent = this.webView.getParent();
                if (parent != null) {
                    ((FrameLayout) parent).removeView(this.webView);
                }
                this.webView.stopLoading();
                this.webView.loadUrl("about:blank");
                this.webView.setWebViewClient((WebViewClient) null);
                this.webView.setWebChromeClient((WebChromeClient) null);
                this.webView.destroy();
                this.webView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        super.onDestroyView();
        ChromeClient chromeClient2 = this.chromeClient;
        if (chromeClient2 != null) {
            ValueCallback unused = chromeClient2.fileChooserCallback = null;
            ValueCallback unused2 = this.chromeClient.fileChooserCallback40 = null;
            GeolocationPermissions.Callback unused3 = this.chromeClient.mGeolocationPermissionsCallBack = null;
            this.chromeClient = null;
        }
        this.chromeClient = null;
        this.delegate = null;
        if (getParentActivity() != null) {
            getParentActivity().setRequestedOrientation(1);
        }
    }

    private class WebClient extends WebViewClient {
        private WebClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TabWebFragment.this.isAdded()) {
                return false;
            }
            if (url != null) {
                try {
                    if (!url.contains("https") && !url.contains("http") && TabWebFragment.this.getParentActivity() != null) {
                        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                        intent.putExtra("create_new_tab", true);
                        intent.putExtra("com.android.browser.application_id", TabWebFragment.this.getParentActivity().getPackageName());
                        TabWebFragment.this.getParentActivity().startActivity(intent);
                        return true;
                    }
                } catch (Throwable e) {
                    FileLog.e(TabWebFragment.class.getName(), e);
                    ToastUtils.show((int) R.string.NoAppCanHandleThis);
                    return true;
                }
            }
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            TabWebFragment.this.showProgressDialog();
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            TabWebFragment.this.dismissCurrentDialog();
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            FileLog.e(TabWebFragment.class.getName() + " onReceivedError: code:" + errorCode + ", des:" + description + ", u:" + failingUrl);
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

        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (!TabWebFragment.this.isAdded()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabWebFragment.this.getParentActivity());
            builder.setMessage(message);
            TabWebFragment.this.showDialog(builder.create());
            return true;
        }

        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            if (!TabWebFragment.this.isAdded()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) TabWebFragment.this.getParentActivity());
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
            TabWebFragment.this.showDialog(builder.create());
            return true;
        }

        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            if (TabWebFragment.this.isAdded() && !this.mIsRequestingLocationPermission) {
                this.mIsRequestingLocationPermission = true;
                this.mGeolocationPermissionsCallBack = callback;
                if (ContextCompat.checkSelfPermission(TabWebFragment.this.getParentActivity(), origin) != 0) {
                    ActivityCompat.requestPermissions(TabWebFragment.this.getParentActivity(), new String[]{origin}, 99);
                    return;
                }
                showRequestLocationPermissionDialog(origin, callback);
            }
        }

        public void onPermissionRequest(PermissionRequest request) {
            if (TabWebFragment.this.isAdded() && Build.VERSION.SDK_INT >= 21) {
                try {
                    request.grant(request.getResources());
                } catch (Exception e) {
                }
            }
        }

        /* access modifiers changed from: private */
        public void showRequestLocationPermissionDialog(String origin, GeolocationPermissions.Callback callback) {
            if (TabWebFragment.this.isAdded() && callback != null) {
                TabWebFragment.this.showDialog(LocaleController.getString(R.string.RequestPermissionOfLocationDoYouAgreement), false, false, LocaleController.getString(R.string.Decline), new DialogInterface.OnClickListener(callback, origin) {
                    private final /* synthetic */ GeolocationPermissions.Callback f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TabWebFragment.ChromeClient.this.lambda$showRequestLocationPermissionDialog$2$TabWebFragment$ChromeClient(this.f$1, this.f$2, dialogInterface, i);
                    }
                }, LocaleController.getString(R.string.Agree), new DialogInterface.OnClickListener(callback, origin) {
                    private final /* synthetic */ GeolocationPermissions.Callback f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TabWebFragment.ChromeClient.this.lambda$showRequestLocationPermissionDialog$3$TabWebFragment$ChromeClient(this.f$1, this.f$2, dialogInterface, i);
                    }
                }, new DialogInterface.OnCancelListener(callback, origin) {
                    private final /* synthetic */ GeolocationPermissions.Callback f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        TabWebFragment.ChromeClient.this.lambda$showRequestLocationPermissionDialog$4$TabWebFragment$ChromeClient(this.f$1, this.f$2, dialogInterface);
                    }
                }, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        TabWebFragment.ChromeClient.this.lambda$showRequestLocationPermissionDialog$5$TabWebFragment$ChromeClient(dialogInterface);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$showRequestLocationPermissionDialog$2$TabWebFragment$ChromeClient(GeolocationPermissions.Callback callback, String origin, DialogInterface dialog, int which) {
            if (callback != null) {
                callback.invoke(origin, false, false);
            }
            this.mIsRequestingLocationPermission = false;
        }

        public /* synthetic */ void lambda$showRequestLocationPermissionDialog$3$TabWebFragment$ChromeClient(GeolocationPermissions.Callback callback, String origin, DialogInterface dialog, int which) {
            if (callback != null) {
                callback.invoke(origin, true, false);
            }
            this.mIsRequestingLocationPermission = false;
        }

        public /* synthetic */ void lambda$showRequestLocationPermissionDialog$4$TabWebFragment$ChromeClient(GeolocationPermissions.Callback callback, String origin, DialogInterface dialog) {
            if (callback != null) {
                callback.invoke(origin, false, false);
            }
            this.mIsRequestingLocationPermission = false;
        }

        public /* synthetic */ void lambda$showRequestLocationPermissionDialog$5$TabWebFragment$ChromeClient(DialogInterface dialog) {
            this.mIsRequestingLocationPermission = false;
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            this.fileChooserCallback40 = valueCallback;
            TabWebFragment.this.openFileChoosers();
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType) {
            this.fileChooserCallback40 = valueCallback;
            TabWebFragment.this.openFileChoosers();
        }

        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            this.fileChooserCallback40 = valueCallback;
            TabWebFragment.this.openFileChoosers();
        }

        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            this.fileChooserCallback = filePathCallback;
            TabWebFragment.this.openFileChoosers();
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
