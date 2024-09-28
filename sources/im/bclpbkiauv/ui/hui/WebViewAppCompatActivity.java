package im.bclpbkiauv.ui.hui;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.internal.ImagesContract;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class WebViewAppCompatActivity extends AppCompatActivity {
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    private ActionBarMenu actionBarMenu;
    private ActionBarMenuItem menuItem;
    /* access modifiers changed from: private */
    public ActionBarMenuItem refreshMenuItem;
    private LinearLayout root;
    /* access modifiers changed from: private */
    public String url;
    /* access modifiers changed from: private */
    public WebView webView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        setTheme(R.style.Theme_TMessages);
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_webview_compat);
        this.root = (LinearLayout) findViewById(R.id.root);
        this.webView = (WebView) findViewById(R.id.webView);
        this.root.setBackgroundColor(-1);
        ActionBar actionBar2 = new ActionBar(this);
        this.actionBar = actionBar2;
        actionBar2.setBackgroundColor(-1);
        this.actionBar.setItemsBackgroundColor(-12554860, false);
        this.actionBar.setItemsBackgroundColor(-986896, true);
        this.actionBar.setItemsColor(-16777216, false);
        this.actionBar.setItemsColor(-16777216, true);
        this.actionBar.setOccupyStatusBar(true);
        this.actionBar.setTitle(LocaleController.getString("Chats", R.string.Chats));
        this.actionBar.setCastShadows(true);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.actionBarMenu = createMenu;
        this.refreshMenuItem = createMenu.addItem(1, (int) R.drawable.ic_againinline);
        ActionBarMenuItem addItem = this.actionBarMenu.addItem(2, (int) R.drawable.bar_right_menu);
        this.menuItem = addItem;
        addItem.addSubItem(3, (int) R.drawable.msg_openin, (CharSequence) LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
        this.menuItem.addSubItem(4, (int) R.drawable.msg_copy, (CharSequence) LocaleController.getString("CopyLink", R.string.CopyLink));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                super.onItemClick(id);
                if (id == -1) {
                    WebViewAppCompatActivity.this.finish();
                } else if (id == 1 && !TextUtils.isEmpty(WebViewAppCompatActivity.this.url) && WebViewAppCompatActivity.this.webView != null) {
                    WebViewAppCompatActivity.this.webView.loadUrl(WebViewAppCompatActivity.this.url);
                }
            }
        });
        this.root.addView(this.actionBar, 0, LayoutHelper.createLinear(-1, -2));
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setTextZoom(100);
        this.webView.setFocusable(true);
        this.webView.setWebChromeClient(new WebChromeClient() {
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (WebViewAppCompatActivity.this.actionBar != null && !TextUtils.isEmpty(title)) {
                    WebViewAppCompatActivity.this.actionBar.setTitle(title);
                }
            }
        });
        this.webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String unused = WebViewAppCompatActivity.this.url = url;
                view.loadUrl(url);
                return false;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                FileLog.e("WebViewAppCompatActivity ===> WebViewClient onReceivedError , errorCode = " + errorCode + " , description = " + description);
            }

            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (WebViewAppCompatActivity.this.refreshMenuItem != null && WebViewAppCompatActivity.this.refreshMenuItem.getAnimation() == null) {
                    Animation rotate = AnimationUtils.loadAnimation(WebViewAppCompatActivity.this, R.anim.rotate_360_forever);
                    rotate.setInterpolator(new LinearInterpolator());
                    WebViewAppCompatActivity.this.refreshMenuItem.startAnimation(rotate);
                }
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (WebViewAppCompatActivity.this.refreshMenuItem != null) {
                    WebViewAppCompatActivity.this.refreshMenuItem.clearAnimation();
                }
            }

            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        String stringExtra = getIntent().getStringExtra(ImagesContract.URL);
        this.url = stringExtra;
        if (TextUtils.isEmpty(stringExtra)) {
            finish();
            return;
        }
        if (this.url.contains("file")) {
            ActionBarMenuItem actionBarMenuItem = this.refreshMenuItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
            ActionBarMenuItem actionBarMenuItem2 = this.menuItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setVisibility(8);
            }
        }
        this.webView.loadUrl(this.url);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.onResume();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.onPause();
        }
    }

    public void onBackPressed() {
        WebView webView2 = this.webView;
        if (webView2 == null || !webView2.canGoBack()) {
            super.onBackPressed();
        } else {
            this.webView.goBack();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.url = null;
        LinearLayout linearLayout = this.root;
        if (linearLayout != null) {
            linearLayout.removeAllViews();
            this.root.removeAllViewsInLayout();
            this.root = null;
        }
        ActionBarMenuItem actionBarMenuItem = this.refreshMenuItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.removeAllViews();
            this.refreshMenuItem.removeAllViewsInLayout();
            this.refreshMenuItem = null;
        }
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            actionBar2.removeAllViews();
            this.actionBar.removeAllViewsInLayout();
            this.actionBar = null;
        }
        try {
            this.webView.setLayerType(0, (Paint) null);
            ViewParent parent = this.webView.getParent();
            if (parent != null) {
                ((FrameLayout) parent).removeView(this.webView);
            }
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
            this.webView = null;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }
}
