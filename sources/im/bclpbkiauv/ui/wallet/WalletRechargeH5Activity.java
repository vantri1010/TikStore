package im.bclpbkiauv.ui.wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import java.util.Timer;

public class WalletRechargeH5Activity extends BaseFragment {
    private static final int LOADING = 1;
    private String currentUrl;
    private boolean loadStats;
    private int mCount;
    private boolean mIsDestroy;
    private int mPayWay;
    private int mRequestToken;
    private Timer mTimer;
    private boolean mTimerIsRunning;
    /* access modifiers changed from: private */
    public ActionBarMenuItem progressItem;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public WebView webView;

    public WalletRechargeH5Activity(int payWay, String url) {
        this.mPayWay = payWay;
        this.currentUrl = url;
    }

    public View createView(Context context) {
        this.swipeBackEnabled = false;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletRechargeH5Activity.this.finishFragment();
                }
            }
        });
        this.progressItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_ab_other, AndroidUtilities.dp(54.0f));
        this.actionBar.setTitle(LocaleController.getString(R.string.redpacket_go_recharge));
        ContextProgressView contextProgressView = new ContextProgressView(context, 3);
        this.progressView = contextProgressView;
        this.progressItem.addView(contextProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressView.setAlpha(1.0f);
        this.progressView.setScaleX(1.0f);
        this.progressView.setScaleY(1.0f);
        this.progressView.setVisibility(0);
        this.progressItem.getContentView().setVisibility(8);
        this.progressItem.setEnabled(false);
        WebView webView2 = new WebView(context);
        this.webView = webView2;
        webView2.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.getSettings().setTextZoom(100);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        if (Build.VERSION.SDK_INT >= 19) {
            this.webView.setLayerType(2, (Paint) null);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebViewClient(new WebViewClient() {
            private boolean isInternalUrl(String url) {
                if (TextUtils.isEmpty(url)) {
                    return false;
                }
                Uri uri = Uri.parse(url);
                if (!url.startsWith("alipays:") && !url.startsWith("alipay")) {
                    return false;
                }
                try {
                    Intent intent = new Intent("android.intent.action.VIEW", uri);
                    intent.addFlags(C.ENCODING_PCM_MU_LAW);
                    ApplicationLoader.applicationContext.startActivity(intent);
                    return true;
                } catch (Exception e) {
                    WalletDialogUtil.showWalletDialog(WalletRechargeH5Activity.this.getParentActivity(), LocaleController.getString(R.string.NotInstallAliPayAppTips), LocaleController.getString(R.string.InstallImmediate), false, (DialogInterface.OnClickListener) null, $$Lambda$WalletRechargeH5Activity$2$hQ2gJG1GplwQsxE011JcPCLH8Ag.INSTANCE, (DialogInterface.OnDismissListener) null);
                    return true;
                }
            }

            static /* synthetic */ void lambda$isInternalUrl$0(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://d.alipay.com"));
                    intent.addFlags(C.ENCODING_PCM_MU_LAW);
                    ApplicationLoader.applicationContext.startActivity(intent);
                } catch (Exception e1) {
                    FileLog.e((Throwable) e1);
                }
            }

            public void onLoadResource(WebView view, String url) {
                if (!isInternalUrl(url)) {
                    super.onLoadResource(view, url);
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!isInternalUrl(url)) {
                    WalletRechargeH5Activity.this.webView.loadUrl(url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (WalletRechargeH5Activity.this.progressItem != null && WalletRechargeH5Activity.this.progressItem.getVisibility() == 0) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WalletRechargeH5Activity.this.progressView, "scaleX", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WalletRechargeH5Activity.this.progressView, "scaleY", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WalletRechargeH5Activity.this.progressView, "alpha", new float[]{1.0f, 0.0f})});
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            WalletRechargeH5Activity.this.progressItem.setVisibility(4);
                        }
                    });
                    animatorSet.setDuration(150);
                    animatorSet.start();
                }
            }
        });
        frameLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    public boolean onBackPressed() {
        if (!this.webView.canGoBack()) {
            return super.onBackPressed();
        }
        this.webView.goBack();
        return false;
    }

    public void onResume() {
        super.onResume();
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        WebView webView2 = this.webView;
        if (webView2 != null) {
            webView2.onPause();
        }
    }

    public void onFragmentDestroy() {
        this.mIsDestroy = true;
        super.onFragmentDestroy();
        this.webView.setLayerType(0, (Paint) null);
        try {
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

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        WebView webView2;
        if (isOpen && !backward && (webView2 = this.webView) != null) {
            webView2.loadUrl(this.currentUrl);
        }
    }
}
